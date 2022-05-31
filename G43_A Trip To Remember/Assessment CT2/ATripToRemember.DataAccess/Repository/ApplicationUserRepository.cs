using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.DataAccess.Repository
{
    public class ApplicationUserRepository : Repository<ApplicationUser>, IApplicationUserRepository
    {
        private ApplicationDbContext db;
        public ApplicationUserRepository(ApplicationDbContext _db) : base(_db)
        {
            db = _db;
        }
    }
}
