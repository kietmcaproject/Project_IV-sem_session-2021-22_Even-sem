using Microsoft.AspNetCore.Mvc;
using ATripToRemember.DataAccess;
using ATripToRemember.Models;
using ATripToRemember.DataAccess.Repository.IRepository;
using Microsoft.AspNetCore.Mvc.Rendering;
using ATripToRemember.Utility;
using Microsoft.AspNetCore.Authorization;

namespace ATripToRemember.Controllers
{
    [Area("Admin")]
    [Authorize(Roles = SD.Role_Admin)]
    public class AirLineController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IWebHostEnvironment hostEnvironment;
        public AirLineController(IUnitOfWork _unitOfWork, IWebHostEnvironment _hostEnvironment)
        {
            unitOfWork = _unitOfWork;
            hostEnvironment = _hostEnvironment;
        }
        public IActionResult Index()
        {
            IEnumerable<AirLine> objAirLineList = unitOfWork.Airline.GetAll();
            return View(objAirLineList);
        }
        public IActionResult Upsert(int? id)
        {
            AirLine airline = new();
            
            if(id==null || id == 0)
            {
                return View(airline);
            }
            else
            {
                airline = unitOfWork.Airline.GetFirstOrDefault(u => u.Id == id);
                return View(airline);
            }
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Upsert(AirLine obj, IFormFile? file)
        {
            if (ModelState.IsValid)
            {
                string wwwRootPath = hostEnvironment.WebRootPath;
                if(file != null)
                {
                    string fileName = Guid.NewGuid().ToString();
                    var uploads = Path.Combine(wwwRootPath, @"images\airlines\");
                    var extension = Path.GetExtension(file.FileName);
                    if(obj.ImageUrl != null)
                    {
                        var oldImagePath = Path.Combine(wwwRootPath, obj.ImageUrl.TrimStart('\\'));
                        if (System.IO.File.Exists(oldImagePath))
                        {
                            System.IO.File.Delete(oldImagePath);
                        }
                    }
                    using (var fileStreams = new FileStream(Path.Combine(uploads, fileName + extension), FileMode.Create))
                    {
                        file.CopyTo(fileStreams);
                    }
                    obj.ImageUrl = @"\images\airlines\" + fileName + extension;
                }
                if(obj.Id == 0)
                {
                    unitOfWork.Airline.Add(obj);
                    TempData["success"] = "Airline created successfully";
                }
                else
                {
                    unitOfWork.Airline.Update(obj);
                    TempData["success"] = "Airline updated successfully";
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
            var airlineList = unitOfWork.Airline.GetAll();
            return Json(new { data = airlineList });
        }
        [HttpDelete]
        public IActionResult Delete(int? id)
        {
            var obj = unitOfWork.Airline.GetFirstOrDefault(a => a.Id == id);
            if (obj == null)
            {
                return Json(new { success = false, message = "Error while deleting" });
            }
            var oldImagePath = Path.Combine(hostEnvironment.WebRootPath, obj.ImageUrl.TrimStart('\\'));
            if (System.IO.File.Exists(oldImagePath))
            {
                System.IO.File.Delete(oldImagePath);
            }
            unitOfWork.Airline.Remove(obj);
            unitOfWork.Save();
            return Json(new { success = true, message = "Airline deleted successfully" });
        }
        //End Region
    }
}
