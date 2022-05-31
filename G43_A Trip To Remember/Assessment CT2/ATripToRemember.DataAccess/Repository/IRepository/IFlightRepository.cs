using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository.IRepository
{
    public interface IFlightRepository : IRepository<Flight>
    {
        void Update(Flight obj);
        void DecreaseNumberOfAvailableSeats(Flight flight, int numberOfSeats);
    }
}
