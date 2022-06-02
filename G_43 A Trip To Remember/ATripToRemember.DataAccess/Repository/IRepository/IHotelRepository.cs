using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository.IRepository
{
    public interface IHotelRepository : IRepository<Hotel>
    {
        void Update(Hotel obj);
        void DecreaseNumberOfAvailableRooms(Hotel hotel, int numberOfRooms);
    }
}
