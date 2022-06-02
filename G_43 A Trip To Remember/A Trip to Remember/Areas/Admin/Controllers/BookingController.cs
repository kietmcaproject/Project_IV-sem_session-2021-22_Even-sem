using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using ATripToRemember.Models.ViewModels;
using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using ATripToRemember.Utility;
using Stripe;

namespace ATripToRemember.Areas.Admin.Controllers
{
    [Area("Admin")]
    [Authorize(Roles = SD.Role_Admin)]
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
                FlightBookings = unitOfWork.FlightBooking.GetAll(includeProperties: "Flight,ApplicationUser"),
                HotelReservations = unitOfWork.HotelReservation.GetAll(includeProperties: "Hotel,ApplicationUser"),
                ApplicationUser = unitOfWork.ApplicationUser.GetFirstOrDefault(u => u.Id == claim.Value)
            };
            return View(bookingVM);
        }
        public IActionResult UpdateFlightStatus(int flightBookingId)
        {
            FlightBooking flightBooking = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBookingId, includeProperties: "Flight,ApplicationUser");
            if(flightBooking == null)
            {
                return NotFound();
            }
            return View(flightBooking);
        }
        [HttpPost]
        public IActionResult UpdateFlightStatus(FlightBooking flightBooking)
        {
            if(flightBooking == null)
            {
                return NotFound();
            }
            FlightBooking booking = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBooking.Id);
            booking.BookingStatus = flightBooking.BookingStatus;
            unitOfWork.Save();
            return RedirectToAction("Index", "Booking");
        }
        public IActionResult UpdateHotelStatus(int hotelReservationId)
        {
            HotelReservation hotelReservation = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservationId, includeProperties: "Flight,ApplicationUser");
            if (hotelReservation == null)
            {
                return NotFound();
            }
            return View(hotelReservation);
        }
        [HttpPost]
        public IActionResult UpdateHotelStatus(HotelReservation hotelReservation)
        {
            if (hotelReservation == null)
            {
                return NotFound();
            }
            HotelReservation objFromDb = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservation.Id);
            if(objFromDb.ReservationStatus != SD.BookingStatusCompleted && hotelReservation.ReservationStatus == SD.BookingStatusCompleted)
            {
                Hotel hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == objFromDb.HotelId);
                hotel.availableRooms += objFromDb.numberOfRooms;
                unitOfWork.Save();
            }
            objFromDb.ReservationStatus = hotelReservation.ReservationStatus;
            unitOfWork.Save();
            return RedirectToAction("Index", "Booking");
        }
        public IActionResult CancelFlightBooking(int flightBookingId)
        {
            FlightBooking objFromDb = unitOfWork.FlightBooking.GetFirstOrDefault(u => u.Id == flightBookingId);
            if (objFromDb == null)
            {
                return NotFound();
            }
            if (objFromDb.PaymentStatus == SD.PaymentStatusPaid)
            {
                var options = new RefundCreateOptions
                {
                    Reason = RefundReasons.RequestedByCustomer,
                    PaymentIntent = objFromDb.PaymentIntentId
                };
                var service = new RefundService();
                Refund refund = service.Create(options);
            }
            Flight flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == objFromDb.FlightId);
            flight.AvailableSeats += objFromDb.NumberOfTourists;
            unitOfWork.Save();
            unitOfWork.FlightBooking.Remove(objFromDb);
            unitOfWork.Save();
            TempData["Success"] = "Booking Cancelled Successfully.";
            return RedirectToAction("Index", "Booking");
        }
        public IActionResult CancelHotelReservation(int hotelReservationId)
        {
            HotelReservation objFromDb = unitOfWork.HotelReservation.GetFirstOrDefault(u => u.Id == hotelReservationId);
            if (objFromDb == null)
            {
                return NotFound();
            }
            if (objFromDb.PaymentStatus == SD.PaymentStatusPaid)
            {
                var options = new RefundCreateOptions
                {
                    Reason = RefundReasons.RequestedByCustomer,
                    PaymentIntent = objFromDb.PaymentIntentId
                };
                var service = new RefundService();
                Refund refund = service.Create(options);
            }
            Hotel hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == objFromDb.HotelId);
            hotel.availableRooms += objFromDb.numberOfRooms;
            unitOfWork.Save();
            unitOfWork.HotelReservation.Remove(objFromDb);
            unitOfWork.Save();
            TempData["Success"] = "Reservation Cancelled Successfully.";
            return RedirectToAction("Index", "Booking");
        }
    }
}
