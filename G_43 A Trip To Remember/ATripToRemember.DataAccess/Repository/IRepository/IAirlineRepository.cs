using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository.IRepository
{
    public interface IAirlineRepository : IRepository<AirLine>
    {
        void Update(AirLine obj);
    }
}
