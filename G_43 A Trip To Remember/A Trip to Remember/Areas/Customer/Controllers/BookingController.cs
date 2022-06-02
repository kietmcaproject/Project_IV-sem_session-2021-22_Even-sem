using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models.ViewModels;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using ATripToRemember.Utility;
using ATripToRemember.Models;
using Stripe.Checkout;

namespace ATripToRemember.Areas.Customer.Controllers
{
    [Area("Customer")]
    [Authorize(Roles = SD.Role_User)]
    public class BookingController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        public BookingController(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }
        [Authorize]
        public IActionResult Index()
        {
            var claimsIdentity = (ClaimsIdentity)User.Identity;
            var claim = claimsIdentity.FindFirst(ClaimTypes.NameIdentifier);
            BookingVM bookingVM = new()
            {
                FlightBookings = unitOfWork.FlightBooking.GetAll(u => u.ApplicationUserId == claim.Value && u.BookingStatus != SD.BookingStatusCompleted, includeProperties: "Flight,ApplicationUser"),
                HotelReservations = unitOfWork.HotelReservation.GetAll(u => u.ApplicationUserId == claim.Value && u.ReservationStatus != SD.BookingStatusCompleted, includeProperties: "Hotel,ApplicationUser"),
                ApplicationUser = unitOfWork.ApplicationUser.GetFirstOrDefault(u => u.Id == claim.Value)
            };
            return View(bookingVM);
        }
        public IActionResult FlightDetails(int flightBookingId)
        {
            FlightBooking objFromDb = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBookingId, includeProperties: "Flight,ApplicationUser");
            if(objFromDb == null)
            {
                return NotFound();
            }
            return View(objFromDb);
        }
        public IActionResult HotelDetails(int hotelReservationId)
        {
            HotelReservation objFromDb = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservationId, includeProperties: "Hotel,ApplicationUser");
            if(objFromDb == null)
            {
                return NotFound();
            }
            return View(objFromDb);
        }
        public IActionResult DeleteFlightBooking(int flightBookingId)
        {
            FlightBooking flightBooking = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBookingId, includeProperties: "Flight,ApplicationUser");
            if (flightBooking == null)
            {
                return NotFound();
            }
            return View(flightBooking);
        }
        [HttpPost]
        public IActionResult DeleteFlightBooking(FlightBooking flightBooking)
        {
            FlightBooking objFromDb = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBooking.Id);
            if (objFromDb == null)
            {
                return NotFound();
            }
            Flight flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == objFromDb.FlightId);
            flight.AvailableSeats += objFromDb.NumberOfTourists;
            unitOfWork.Save();
            unitOfWork.FlightBooking.Remove(objFromDb);
            unitOfWork.Save();
            return RedirectToAction("Index", "Booking");
        }
        public IActionResult DeleteHotelReservation(int hotelReservationId)
        {
            HotelReservation hotelReservation = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservationId, includeProperties: "Hotel,ApplicationUser");
            if (hotelReservation == null)
            {
                return NotFound();
            }
            return View(hotelReservation);
        }
        [HttpPost]
        public IActionResult DeleteHotelReservation(HotelReservation hotelReservation)
        {
            HotelReservation objFromDb = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservation.Id);
            if (objFromDb == null)
            {
                return NotFound();
            }
            Hotel hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == objFromDb.HotelId);
            hotel.availableRooms += objFromDb.numberOfRooms;
            unitOfWork.Save();
            unitOfWork.HotelReservation.Remove(objFromDb);
            unitOfWork.Save();
            return RedirectToAction("Index", "Booking");
        }
        public IActionResult FlightPayment(int flightBookingId)
        {
            FlightBooking flightBooking = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBookingId, includeProperties: "Flight,ApplicationUser");
            if(flightBooking == null)
            {
                return NotFound();
            }
            flightBooking.Flight.Airline = unitOfWork.Airline.GetFirstOrDefault(u => u.Id == flightBooking.Flight.AirLineId);
            return View(flightBooking);
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult FlightPayment(FlightBooking flightBooking)
        {
            FlightBooking objFromDb = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBooking.Id, includeProperties: "Flight,ApplicationUser");
            if(objFromDb == null)
            {
                return NotFound();
            }
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
                                UnitAmount = (long)(objFromDb.Flight.Price * 100),
                                Currency = "INR",
                                ProductData = new SessionLineItemPriceDataProductDataOptions
                                {
                                    Name = objFromDb.Flight.StartingLocation + " to " + objFromDb.Flight.Destination,
                                },
                            },
                            Quantity = objFromDb.NumberOfTourists,
                        },
                    },
                Mode = "payment",
                SuccessUrl = domain + $"Customer/Flight/BookingConfirmation?id={flightBooking.Id}",
                CancelUrl = domain + $"Customer/Flight/Index",
            };
            var service = new SessionService();
            Session session = service.Create(options);
            unitOfWork.FlightBooking.UpdateStripePaymentId(objFromDb.Id, session.Id, session.PaymentIntentId);
            unitOfWork.Save();
            Response.Headers.Add("Location", session.Url);
            return new StatusCodeResult(303);
        }
        public IActionResult HotelPayment(int hotelReservationId)
        {
            HotelReservation hotelReservation = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservationId, includeProperties: "Hotel,ApplicationUser");
            if (hotelReservation == null)
            {
                return NotFound();
            }
            return View(hotelReservation);
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult HotelPayment(HotelReservation hotelReservation)
        {
            HotelReservation objFromDb = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservation.Id, includeProperties: "Hotel,ApplicationUser");
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
                                UnitAmount = (long)(objFromDb.Hotel.RentPerDay * 100),
                                Currency = "INR",
                                ProductData = new SessionLineItemPriceDataProductDataOptions
                                {
                                    Name = objFromDb.Hotel.Name,
                                },
                            },
                            Quantity = objFromDb.numberOfDays * objFromDb.numberOfRooms,
                        },
                    },
                Mode = "payment",
                SuccessUrl = domain + $"Customer/Hotel/BookingConfirmation?id={hotelReservation.Id}",
                CancelUrl = domain + $"Customer/Hotel/Index",
            };
            var service = new SessionService();
            Session session = service.Create(options);
            unitOfWork.HotelReservation.UpdateStripePaymentId(objFromDb.Id, session.Id, session.PaymentIntentId);
            unitOfWork.Save();
            Response.Headers.Add("Location", session.Url);
            return new StatusCodeResult(303);
        }
    }
}
