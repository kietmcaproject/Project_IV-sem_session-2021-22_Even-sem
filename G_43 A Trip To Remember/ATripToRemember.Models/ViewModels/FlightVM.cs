using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.AspNetCore.Mvc.ModelBinding.Validation;

namespace ATripToRemember.Models.ViewModels
{
    public class FlightVM
    {
        public Flight Flight { get; set; }
        [ValidateNever]
        public IEnumerable<SelectListItem> AirlineList { get; set; }
    }
}
