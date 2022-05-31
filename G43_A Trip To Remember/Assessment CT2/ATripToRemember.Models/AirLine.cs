using System.ComponentModel.DataAnnotations;
using System.ComponentModel;
using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;

namespace ATripToRemember.Models
{
    public class AirLine
    {
        [Key]
        public int Id { get; set; }
        [Required]
        public string? Name { get; set; }
        [Required]
        [DisplayName("Image")]
        [ValidateNever]
        public string? ImageUrl { get; set; }
    }
}
