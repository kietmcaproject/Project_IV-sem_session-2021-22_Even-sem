using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository
{
    public class AirlineRepository : Repository<AirLine>, IAirlineRepository
    {
        private ApplicationDbContext db;
        public AirlineRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
        public void Update(AirLine obj)
        {
            var objFromDb = db.AirLines.FirstOrDefault(u => u.Id == obj.Id);
            if(objFromDb != null)
            {
                objFromDb.Name = obj.Name;
                if (obj.ImageUrl != null)
                {
                    objFromDb.ImageUrl = obj.ImageUrl;
                }
            }
        }
    }
}
