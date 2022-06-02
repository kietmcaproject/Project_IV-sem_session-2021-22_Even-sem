using Microsoft.AspNetCore.Mvc;
using ATripToRemember.DataAccess;
using ATripToRemember.Models;
using ATripToRemember.Models.ViewModels;
using ATripToRemember.DataAccess.Repository.IRepository;
using Microsoft.AspNetCore.Mvc.Rendering;
using ATripToRemember.Utility;
using Microsoft.AspNetCore.Authorization;

namespace ATripToRemember.Controllers
{
    [Area("Admin")]
    [Authorize(Roles = SD.Role_Admin)]
    public class FlightController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        public FlightController(IUnitOfWork _unitOfWork)
        {
            unitOfWork = _unitOfWork;
        }
        public IActionResult Index()
        {
            return View();
        }
        public IActionResult Upsert(int? id)
        {
            FlightVM flightVM = new()
            {
                Flight = new(),
                AirlineList = unitOfWork.Airline.GetAll().Select(i => new SelectListItem
                {
                    Text = i.Name,
                    Value = i.Id.ToString()
                }),
            };
            if(id==null || id==0)
            {
                return View(flightVM);
            }
            else
            {
                flightVM.Flight = unitOfWork.Flight.GetFirstOrDefault(u => u.Id == id);
                return View(flightVM);
            }
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Upsert(FlightVM obj)
        {
            if(obj.Flight.StartingLocation == obj.Flight.Destination)
            {
                ModelState.AddModelError("Flight.Destination", "Destination and Starting Location cannot be same");
            }
            if(obj.Flight.StartingTime <= DateTime.Now)
            {
                ModelState.AddModelError("Flight.StartingTime", "Date and Time is not valid");
            }
            if(obj.Flight.ReachingTime <= obj.Flight.StartingTime)
            {
                ModelState.AddModelError("Flight.ReachingTime", "Reaching Time should be greater than Starting Time");
            }
            if (ModelState.IsValid)
            {
                if (obj.Flight.Id == 0)
                {
                    unitOfWork.Flight.Add(obj.Flight);
                    TempData["success"] = "Flight created successfully";
                }
                else
                {
                    unitOfWork.Flight.Update(obj.Flight);
                    TempData["success"] = "Flight updated successfully";
                }
                unitOfWork.Save();
                return RedirectToAction("Index");
            }
            return View(obj);
        }
        //Region API Calls
        [HttpGet]
        public IActionResult GetAll()
        {
            var flightList = unitOfWork.Flight.GetAll(includeProperties: "Airline");
            return Json(new { data = flightList });
        }
        [HttpDelete]
        public IActionResult Delete(int? id)
        {
            var obj = unitOfWork.Flight.GetFirstOrDefault(a => a.Id == id);
            if (obj == null)
            {
                return Json(new {success = false, message = "Error while deleting"});
            }
            unitOfWork.Flight.Remove(obj);
            unitOfWork.Save();
            return Json(new {success = true, message = "Flight deleted successfully"});
        }
        //End Region
    }
}
