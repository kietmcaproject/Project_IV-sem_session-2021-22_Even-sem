using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository.IRepository
{
    public interface IFlightBookingRepository : IRepository<FlightBooking>
    {
        void Update(FlightBooking obj);
        void UpdateStatus(int id, string bookingStatus, string? paymentStatus = null);
        void UpdateStripePaymentId(int id, string sessionId, string paymentIntentId);
    }
}
