using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ATripToRemember.Models;
using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;

namespace ATripToRemember.Models.ViewModels
{
    public class BookingVM
    {
        public IEnumerable<FlightBooking>? FlightBookings { get; set; }
        public IEnumerable<HotelReservation>? HotelReservations { get; set; }
        public int ApplicationUserId { get; set; }
        [ForeignKey("ApplicationUserId")]
        [ValidateNever]
        public ApplicationUser? ApplicationUser { get; set; }
    }
}
