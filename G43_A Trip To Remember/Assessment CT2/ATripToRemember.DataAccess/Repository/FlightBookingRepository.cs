using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using ATripToRemember.Utility;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository
{
    public class FlightBookingRepository : Repository<FlightBooking>, IFlightBookingRepository
    {
        private ApplicationDbContext db;
        public FlightBookingRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
        public void Update(FlightBooking obj)
        {
            db.FlightBookings.Update(obj);
        }

        public void UpdateStatus(int id, string bookingStatus, string? paymentStatus = null)
        {
            var bookingFromDb = db.FlightBookings.FirstOrDefault(u => u.Id == id);
            if (bookingFromDb != null)
            {
                bookingFromDb.BookingStatus = bookingStatus;
                if (paymentStatus != null)
                {
                    bookingFromDb.PaymentStatus = paymentStatus;
                    if (paymentStatus == SD.PaymentStatusPaid)
                    {
                        bookingFromDb.PaymentDate = DateTime.Now;
                    }
                }
            }
        }
        public void UpdateStripePaymentId(int id, string sessionId, string paymentIntentId)
        {
            var bookingFromDb = db.FlightBookings.FirstOrDefault(u => u.Id == id);
            if (bookingFromDb != null)
            {
                bookingFromDb.PaymentDate = DateTime.Now;
                bookingFromDb.SessionId = sessionId;
                bookingFromDb.PaymentIntentId = paymentIntentId;
            }
        }
    }
}
