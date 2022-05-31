using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.Models
{
    public class HotelReservation
    {
        public int Id { get; set; }
        public int HotelId { get; set; }
        [ForeignKey("HotelId")]
        [ValidateNever]
        public Hotel? Hotel { get; set; }
        [Required]
        [Range(1, 10, ErrorMessage = "Please enter a value between 1 and 10")]
        [DisplayName("Number of Rooms")]
        public int numberOfRooms { get; set; }
        [Required]
        [DisplayName("Check-in Date")]
        public DateTime FromDate { get; set; }
        [Required]
        [DisplayName("Check-out Date")]
        public DateTime ToDate { get; set; }
        [DisplayName("Number of Days")]
        public int numberOfDays { get; set; }
        [DisplayName("Total Rent")]
        public int TotalRent { get; set; }
        public string? ReservationStatus { get; set; }
        public string? PaymentStatus { get; set; }
        [DataType(DataType.Date)]
        public DateTime PaymentDate { get; set; }
        public string? SessionId { get; set; }
        public string? PaymentIntentId { get; set; }
        public string? ApplicationUserId { get; set; }
        [ForeignKey("ApplicationUserId")]
        [ValidateNever]
        public ApplicationUser? ApplicationUser { get; set; }
    }
}
