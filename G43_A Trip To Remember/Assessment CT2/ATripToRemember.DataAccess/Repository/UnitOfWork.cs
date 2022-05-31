using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ATripToRemember.DataAccess.Repository.IRepository;

namespace ATripToRemember.DataAccess.Repository
{
    public class UnitOfWork : IUnitOfWork
    {
        private ApplicationDbContext db;
        public IAirlineRepository Airline { get; private set; }
        public IFlightRepository Flight { get; private set; }
        public IHotelRepository Hotel { get; private set; }
        public IFlightBookingRepository FlightBooking { get; private set; }
        public IHotelReservationRepository HotelReservation { get; private set; }
        public IApplicationUserRepository ApplicationUser { get; private set; }
        public UnitOfWork(ApplicationDbContext _db)
        {
            db = _db;
            Airline = new AirlineRepository(db);
            Flight = new FlightRepository(db);
            Hotel = new HotelRepository(db);
            FlightBooking = new FlightBookingRepository(db);
            HotelReservation = new HotelReservationRepository(db);
            ApplicationUser = new ApplicationUserRepository(db);
        }
        public void Save()
        {
            db.SaveChanges();
        }
    }
}
