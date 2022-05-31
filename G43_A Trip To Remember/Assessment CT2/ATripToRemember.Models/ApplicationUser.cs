using Microsoft.AspNetCore.Identity;
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
    public class ApplicationUser : IdentityUser
    {
        [Required]
        [DisplayName("First Name")]
        public string? FirstName { get; set; }
        [Required]
        [DisplayName("Last Name")]
        public string? LastName { get; set; }
        [Required]
        [DisplayName("Date of Birth")]
        [DataType(DataType.Date)]
        public DateTime DOB { get; set; }
        [Required]
        [DisplayName("Street Address")]
        public string? StreetAddress { get; set; }
        [Required]
        public string? City { get; set; }
        [Required]
        public string? State { get; set; }
        [Required]
        [DisplayName("Postal Code")]
        [DataType(DataType.PostalCode)]
        public string? PostalCode { get; set; }
    }
}
