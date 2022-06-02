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
    public class FlightBooking
    {
        public int Id { get; set; }
        public int FlightId { get; set; }
        [ForeignKey("FlightId")]
        [ValidateNever]
        public Flight? Flight { get; set; }
        [Required]
        [Range(1, 10, ErrorMessage = "Please enter a value between 1 and 10")]
        public int NumberOfTourists { get; set; }
        [DisplayName("Total Price")]
        public int TotalPrice { get; set; }
        public string? BookingStatus { get; set; }
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
