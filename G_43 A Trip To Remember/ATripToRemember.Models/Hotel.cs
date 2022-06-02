using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ATripToRemember.Models
{
    public class Hotel
    {
        [Key]
        public int Id { get; set; }
        [Required]
        public string? Name { get; set; }
        [Required]
        [DisplayName("Image")]
        [ValidateNever]
        public string? ImageUrl { get; set; }
        [Required]
        public string? Description { get; set; }
        [Required]
        public string? Location { get; set; }
        [Required]
        public string? City { get; set; }
        [Required]
        public string? State { get; set; }
        [Required]
        [DisplayName("Rent Per Day")]
        public int RentPerDay { get; set; }
        [Required]
        [DisplayName("Number of Available Rooms")]
        [Range(0, 10, ErrorMessage = "Value must be between 0 and 10")]
        public int availableRooms { get; set; }
    }
}
