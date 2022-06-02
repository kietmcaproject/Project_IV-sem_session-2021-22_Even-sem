using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using ATripToRemember.Utility;
using Stripe.Checkout;
using Microsoft.AspNetCore.Identity.UI.Services;

namespace ATripToRemember.Areas.Customer.Controllers
{
    [Area("Customer")]
    public class HotelController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IEmailSender emailSender;
        public HotelController(IUnitOfWork _unitOfWork, IEmailSender _emailSender)
        {
            unitOfWork = _unitOfWork;
            emailSender = _emailSender;
        }
        public IActionResult Index()
        {
            IEnumerable<Hotel> hotelList = unitOfWork.Hotel.GetAll();
            return View(hotelList);
        }
        [HttpPost]
        public IActionResult Index(string? city, string? state)
        {
            IEnumerable<Hotel> hotelList;
            if (city == null && state == null)
            {
                return RedirectToAction(nameof(Index));
            }
            else if (city == null)
            {
                hotelList = unitOfWork.Hotel.GetAll(u => u.State == state);
            }
            else if (state == null)
            {
                hotelList = unitOfWork.Hotel.GetAll(u => u.City == city);
            }
            else
            {
                hotelList = unitOfWork.Hotel.GetAll(u => u.City == city && u.State == state);
            }
            if(hotelList.Count() == 0)
            {
                return NotFound();
            }
            return View(hotelList);
        }
        public IActionResult Details(int hotelId)
        {
            Hotel hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == hotelId);
            return View(hotel);
        }
        [Authorize(Roles = SD.Role_User)]
        public IActionResult Booking(int hotelId)
        {
            Hotel hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == hotelId);
            HotelReservation hotelReservation = new()
            {
                HotelId = hotelId,
                Hotel = hotel,
                numberOfRooms = 1,
                numberOfDays = 0
            };
            return View(hotelReservation);
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        [Authorize(Roles = SD.Role_User)]
        public IActionResult Booking(HotelReservation hotelReservation)
        {
            var claimsIdentity = (ClaimsIdentity)User.Identity;
            var claim = claimsIdentity.FindFirst(ClaimTypes.NameIdentifier);
            hotelReservation.ApplicationUserId = claim.Value;
            hotelReservation.Hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == hotelReservation.HotelId);
            if (hotelReservation.FromDate <= DateTime.Now)
            {
                ModelState.AddModelError("FromDate", "You can book room atleast one day before Check-in Date");
            }
            if(hotelReservation.ToDate <= hotelReservation.FromDate)
            {
                ModelState.AddModelError("ToDate", "Check-out Date must be greater than Check-in Date");
            }
            if(hotelReservation.numberOfRooms > hotelReservation.Hotel.availableRooms)
            {
                ModelState.AddModelError("numberOfRooms", "There are only " + hotelReservation.Hotel.availableRooms + " rooms available for booking");
            }
            if (ModelState.IsValid)
            {
                if(hotelReservation.numberOfDays == 0)
                {
                    hotelReservation.numberOfDays = (hotelReservation.ToDate - hotelReservation.FromDate).Days;
                    hotelReservation.TotalRent = hotelReservation.Hotel.RentPerDay * hotelReservation.numberOfDays * hotelReservation.numberOfRooms;
                    return View(hotelReservation);
                }
                else
                {
                    unitOfWork.HotelReservation.Add(hotelReservation);
                    unitOfWork.Hotel.DecreaseNumberOfAvailableRooms(hotelReservation.Hotel, hotelReservation.numberOfRooms);
                    unitOfWork.Save();
                    unitOfWork.HotelReservation.UpdateStatus(hotelReservation.Id, SD.BookingStatusRequested, SD.PaymentStatusPending);
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
                                    UnitAmount = (long)(hotelReservation.Hotel.RentPerDay * 100),
                                    Currency = "INR",
                                    ProductData = new SessionLineItemPriceDataProductDataOptions
                                    {
                                        Name = hotelReservation.Hotel.Name,
                                    },
                                },
                                Quantity = hotelReservation.numberOfDays * hotelReservation.numberOfRooms,
                            },
                        },
                        Mode = "payment",
                        SuccessUrl = domain + $"Customer/Hotel/BookingConfirmation?id={hotelReservation.Id}",
                        CancelUrl = domain + $"Customer/Hotel/Index",
                    };
                    var service = new SessionService();
                    Session session = service.Create(options);
                    unitOfWork.HotelReservation.UpdateStripePaymentId(hotelReservation.Id, session.Id, session.PaymentIntentId);
                    unitOfWork.Save();
                    Response.Headers.Add("Location", session.Url);
                    return new StatusCodeResult(303);

                }
            }
            return View(hotelReservation);
        }
        [Authorize(Roles = SD.Role_User)]
        public IActionResult BookingConfirmation(int id)
        {
            HotelReservation hotelReservation = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == id, includeProperties: "ApplicationUser");
            var service = new SessionService();
            Session session = service.Get(hotelReservation.SessionId);
            if(session.PaymentStatus.ToLower() == "paid")
            {
                unitOfWork.HotelReservation.UpdateStatus(id, SD.BookingStatusApproved, SD.PaymentStatusPaid);
                unitOfWork.Save();
            }
            emailSender.SendEmailAsync(hotelReservation.ApplicationUser.Email, "New Booking - A Trip to Remember", "<p>New Hotel Booked.</p>");
            return View(id);
        }
    }
}
