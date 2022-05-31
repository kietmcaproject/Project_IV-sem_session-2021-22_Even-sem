using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository
{
    public class HotelRepository : Repository<Hotel>, IHotelRepository
    {
        private ApplicationDbContext db;
        public HotelRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
        public void Update(Hotel obj)
        {
            var objFromDb = db.Hotels.FirstOrDefault(u => u.Id == obj.Id);
            if(objFromDb != null)
            {
                objFromDb.Name = obj.Name;
                objFromDb.Description = obj.Description;
                objFromDb.Location = obj.Location;
                objFromDb.City = obj.City;
                objFromDb.State = obj.State;
                objFromDb.RentPerDay = obj.RentPerDay;
                objFromDb.availableRooms = obj.availableRooms;
                if (obj.ImageUrl != null)
                {
                    objFromDb.ImageUrl = obj.ImageUrl;
                }
            }
        }
        public void DecreaseNumberOfAvailableRooms(Hotel hotel, int numberOfRooms)
        {
            var hotelFromDb = db.Hotels.FirstOrDefault(u => u.Id == hotel.Id);
            if(hotelFromDb != null)
            {
                hotelFromDb.availableRooms -= numberOfRooms;
            }

        }
    }
}
