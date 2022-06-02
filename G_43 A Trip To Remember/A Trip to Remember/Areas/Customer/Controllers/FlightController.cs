using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using ATripToRemember.Utility;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using Stripe.Checkout;
using Microsoft.AspNetCore.Identity.UI.Services;

namespace ATripToRemember.Areas.Customer.Controllers
{
    [Area("Customer")]
    public class FlightController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IEmailSender emailSender;
        public FlightController(IUnitOfWork _unitOfWork, IEmailSender _emailSender)
        {
            unitOfWork = _unitOfWork;
            emailSender = _emailSender;
        }
        public IActionResult Index()
        {
            IEnumerable<Flight> flightList = unitOfWork.Flight.GetAll(includeProperties: "Airline");
            return View(flightList);
        }
        public IActionResult Details(int flightId)
        {
            Flight flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == flightId, includeProperties: "Airline");
            return View(flight);
        }
        [Authorize(Roles = SD.Role_User)]
        public IActionResult Booking(int flightId)
        {
            Flight flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == flightId, includeProperties: "Airline");
            FlightBooking flightBooking = new()
            {
                FlightId = flightId,
                Flight = flight,
                NumberOfTourists = 0,
            };
            return View(flightBooking);
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Roles = SD.Role_User)]
        public IActionResult Booking(FlightBooking flightBooking)
        {
            var claimsIdentity = (ClaimsIdentity)User.Identity;
            var claim = claimsIdentity.FindFirst(ClaimTypes.NameIdentifier);
            flightBooking.ApplicationUserId = claim.Value;
            flightBooking.Flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == flightBooking.FlightId, includeProperties: "Airline");
            if (flightBooking.NumberOfTourists > flightBooking.Flight.AvailableSeats)
            {
                ModelState.AddModelError("NumberOfTourists", "There are only " + flightBooking.Flight.AvailableSeats + " seats available for booking");
            }
            if (ModelState.IsValid)
            {
                if (flightBooking.TotalPrice == 0)
                {
                    flightBooking.TotalPrice = flightBooking.Flight.Price * flightBooking.NumberOfTourists;
                    return View(flightBooking);
                }
                else
                {
                    unitOfWork.FlightBooking.Add(flightBooking);
                    unitOfWork.Flight.DecreaseNumberOfAvailableSeats(flightBooking.Flight, flightBooking.NumberOfTourists);
                    unitOfWork.Save();
                    unitOfWork.FlightBooking.UpdateStatus(flightBooking.Id, SD.BookingStatusRequested, SD.PaymentStatusPending);
                    unitOfWork.Save();
                    //Stripe Settings
                    var domain = "https://localhost:44326/";
                    var options = new SessionCreateOptions
                    {
                        LineItems = new List<SessionLineItemOptions>
                        {
                            new SessionLineItemOptions
                            {
                                PriceData = new SessionLineItemPriceDataOptions
                                {
                                    UnitAmount = (long)(flightBooking.Flight.Price * 100),
                                    Currency = "INR",
                                    ProductData = new SessionLineItemPriceDataProductDataOptions
                                    {
                                        Name = flightBooking.Flight.StartingLocation + " to " + flightBooking.Flight.Destination,
                                    },
                                },
                                Quantity = flightBooking.NumberOfTourists,
                            },
                        },
                        Mode = "payment",
                        SuccessUrl = domain + $"Customer/Flight/BookingConfirmation?id={flightBooking.Id}",
                        CancelUrl = domain + $"Customer/Flight/Index",
                    };
                    var service = new SessionService();
                    Session session = service.Create(options);
                    unitOfWork.FlightBooking.UpdateStripePaymentId(flightBooking.Id, session.Id, session.PaymentIntentId);
                    unitOfWork.Save();
                    Response.Headers.Add("Location", session.Url);
                    return new StatusCodeResult(303);

                }
            }
            return View(flightBooking);
        }
        [Authorize(Roles = SD.Role_User)]
        public IActionResult BookingConfirmation(int id)
        {
            FlightBooking flightBooking = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == id, includeProperties: "ApplicationUser");
            var service = new SessionService();
            Session session = service.Get(flightBooking.SessionId);
            if (session.PaymentStatus.ToLower() == "paid")
            {
                unitOfWork.FlightBooking.UpdateStatus(id, SD.BookingStatusApproved, SD.PaymentStatusPaid);
                unitOfWork.Save();
            }
            emailSender.SendEmailAsync(flightBooking.ApplicationUser.Email, "New Booking - A Trip to Remember", "<p>New Flight Booked.</p>");
            return View(id);
        }
    }
}
