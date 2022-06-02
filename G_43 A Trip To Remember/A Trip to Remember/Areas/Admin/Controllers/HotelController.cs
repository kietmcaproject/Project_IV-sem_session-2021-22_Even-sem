using ATripToRemember.DataAccess.Repository.IRepository;
using ATripToRemember.Models;
using Microsoft.AspNetCore.Mvc;
using ATripToRemember.Utility;
using Microsoft.AspNetCore.Authorization;

namespace ATripToRemember.Areas.Admin.Controllers
{
    [Area("Admin")]
    [Authorize(Roles = SD.Role_Admin)]
    public class HotelController : Controller
    {
        private readonly IUnitOfWork unitOfWork;
        private readonly IWebHostEnvironment hostEnvironment;
        public HotelController(IUnitOfWork _unitOfWork, IWebHostEnvironment _hostEnvironment)
        {
            unitOfWork = _unitOfWork;
            hostEnvironment = _hostEnvironment;
        }
        public IActionResult Index()
        {
            IEnumerable<Hotel> objHotelList = unitOfWork.Hotel.GetAll();
            return View(objHotelList);
        }
        public IActionResult Upsert(int? id)
        {
            Hotel hotel = new();

            if (id == null || id == 0)
            {
                return View(hotel);
            }
            else
            {
                hotel = unitOfWork.Hotel.GetFirstOrDefault(u => u.Id == id);
                return View(hotel);
            }
        }
        [HttpPost]
        [ValidateAntiForgeryToken]
        public IActionResult Upsert(Hotel obj, IFormFile? file)
        {
            if (ModelState.IsValid)
            {
                string wwwRootPath = hostEnvironment.WebRootPath;
                if (file != null)
                {
                    string fileName = Guid.NewGuid().ToString();
                    var uploads = Path.Combine(wwwRootPath, @"images\hotels\");
                    var extension = Path.GetExtension(file.FileName);
                    if (obj.ImageUrl != null)
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
                    obj.ImageUrl = @"\images\hotels\" + fileName + extension;
                }
                if (obj.Id == 0)
                {
                    unitOfWork.Hotel.Add(obj);
                    TempData["success"] = "Hotel added successfully";
                }
                else
                {
                    unitOfWork.Hotel.Update(obj);
                    TempData["success"] = "Hotel updated successfully";
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
            var hotelList = unitOfWork.Hotel.GetAll();
            return Json(new { data = hotelList });
        }
        [HttpDelete]
        public IActionResult Delete(int? id)
        {
            var obj = unitOfWork.Hotel.GetFirstOrDefault(a => a.Id == id);
            if (obj == null)
            {
                return Json(new { success = false, message = "Error while deleting" });
            }
            var oldImagePath = Path.Combine(hostEnvironment.WebRootPath, obj.ImageUrl.TrimStart('\\'));
            if (System.IO.File.Exists(oldImagePath))
            {
                System.IO.File.Delete(oldImagePath);
            }
            unitOfWork.Hotel.Remove(obj);
            unitOfWork.Save();
            return Json(new { success = true, message = "Hotel deleted successfully" });
        }
        //End Region
    }
}
