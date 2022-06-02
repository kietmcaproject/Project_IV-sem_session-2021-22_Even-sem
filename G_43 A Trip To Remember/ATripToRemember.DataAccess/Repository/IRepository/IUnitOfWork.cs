using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository.IRepository
{
    public interface IUnitOfWork
    {
        IAirlineRepository Airline { get; }
        IFlightRepository Flight { get; }
        IHotelRepository Hotel { get; }
        IFlightBookingRepository FlightBooking { get; }
        IHotelReservationRepository HotelReservation { get; }
        IApplicationUserRepository ApplicationUser { get; }
        void Save();
    }
}
