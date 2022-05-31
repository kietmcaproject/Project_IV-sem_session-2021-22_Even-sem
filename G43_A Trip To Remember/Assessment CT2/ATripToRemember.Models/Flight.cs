using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;

namespace ATripToRemember.Models
{
    public class Flight
    {
        [Key]
        public int Id { get; set; }
        [Required]
        [DisplayName("From")]
        public string StartingLocation { get; set; }
        [Required]
        [DisplayName("To")]
        public string Destination { get; set; }
        [Required]
        [DisplayName("Starting Time")]
        [DataType(DataType.DateTime)]
        public DateTime StartingTime { get; set; }
        [Required]
        [DisplayName("Reaching Time")]
        [DataType(DataType.DateTime)]
        public DateTime ReachingTime { get; set; }
        [Required]
        public int Price { get; set; }
        [Required]
        [DisplayName("Number of Available Seats")]
        [Range(1, 50, ErrorMessage = "Please enter a value between 1 and 50")]
        public int AvailableSeats { get; set; }
        [Required]
        [DisplayName("Airline")]
        public int AirLineId { get; set; }
        [ForeignKey("AirLineId")]
        [ValidateNever]
        public AirLine Airline { get; set; }
    }
}
