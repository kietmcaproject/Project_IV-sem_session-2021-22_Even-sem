using Microsoft.EntityFrameworkCore;
using ATripToRemember.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;

namespace ATripToRemember.DataAccess
{
    public class ApplicationDbContext : IdentityDbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options) : base(options)
        {

        }
        public DbSet<AirLine> AirLines { get; set; }
        public DbSet<Flight> Flights { get; set; }
        public DbSet<Hotel> Hotels { get; set; }
        public DbSet<ApplicationUser> ApplicationUsers { get; set; }
        public DbSet<FlightBooking> FlightBookings { get; set; }
        public DbSet<HotelReservation> HotelReservations { get; set; }
    }
}
