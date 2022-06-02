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
    public class HotelReservationRepository : Repository<HotelReservation>, IHotelReservationRepository
    {
        private readonly ApplicationDbContext db;
        public HotelReservationRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
        public void Update(HotelReservation obj)
        {
            db.HotelReservations.Update(obj);
        }

        public void UpdateStatus(int id,string reservationStatus, string? paymentStatus = null)
        {
            var bookingFromDb = db.HotelReservations.FirstOrDefault(u => u.Id == id);
            if (bookingFromDb != null)
            {
                bookingFromDb.ReservationStatus = reservationStatus;
                if (paymentStatus != null)
                {
                    bookingFromDb.PaymentStatus = paymentStatus;
                    if(paymentStatus == SD.PaymentStatusPaid)
                    {
                        bookingFromDb.PaymentDate = DateTime.Now;
                    }
                }
            }
        }
        public void UpdateStripePaymentId(int id, string sessionId, string paymentIntentId)
        {
            var bookingFromDb = db.HotelReservations.FirstOrDefault(u => u.Id == id);
            if(bookingFromDb != null)
            {
                bookingFromDb.PaymentDate = DateTime.Now;
                bookingFromDb.SessionId = sessionId;
                bookingFromDb.PaymentIntentId = paymentIntentId;
            }
        }
    }
}
