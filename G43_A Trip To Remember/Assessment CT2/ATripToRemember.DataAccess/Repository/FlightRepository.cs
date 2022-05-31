using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository
{
    public class FlightRepository : Repository<Flight>, IFlightRepository
    {
        private ApplicationDbContext db;
        public FlightRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
        public void Update(Flight obj)
        {
            var objFromDb = db.Flights.FirstOrDefault(u => u.Id == obj.Id);
            if(objFromDb != null)
            {
                objFromDb.StartingLocation = obj.StartingLocation;
                objFromDb.Destination = obj.Destination;
                objFromDb.StartingTime = obj.StartingTime;
                objFromDb.ReachingTime = obj.ReachingTime;
                objFromDb.Price = obj.Price;
                objFromDb.AirLineId = obj.AirLineId;
            }
        }
        public void DecreaseNumberOfAvailableSeats(Flight flight, int numberOfSeats)
        {
            var flightFromDb = db.Flights.FirstOrDefault(u => u.Id == flight.Id);
            if (flightFromDb != null)
            {
                flightFromDb.AvailableSeats -= numberOfSeats;
            }

        }
    }
}
