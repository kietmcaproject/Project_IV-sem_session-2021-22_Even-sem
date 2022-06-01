using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using QuizApp.Models;
using System.IO;

namespace QuizApp.Controllers
{
    public class HomeController : Controller
    {
         QuizAPPEntities1 db = new QuizAPPEntities1();

        [HttpGet]
        public ActionResult Admin_Signup()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Admin_Signup(Admin_tb snup)
        {
            if (ModelState.IsValid == true)
            {
                db.Admin_tb.Add(snup);
                int a = db.SaveChanges();
                if (a > 0)
                {
                    ViewBag.InsertMessage = "<script>swal('Congradulations', 'Registered Successfully','success')</script>";
                    ModelState.Clear();
                }
                else
                {
                    ViewBag.InsertMessage = "<script>alert('Registration Failed!!')</script>";
                }

            }
            return View();
        }


        [HttpGet]
        public ActionResult Admin_login()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Admin_login(Admin_tb ad)
        {
            Admin_tb admin = db.Admin_tb.Where(x => x.ADMIN_NAME == ad.ADMIN_NAME && x.ADMIN_PASSWORD == ad.ADMIN_PASSWORD).SingleOrDefault();
            if (admin != null)
            {
                Session["Admin_id"] = admin.ADMIN_ID.ToString();
                Session["AdminName"] = admin.ADMIN_NAME;
                TempData.Keep();
                return RedirectToAction("Dashboard");
            }
            else
            {
                ViewBag.msg = "Invalid UserName Or Password!";
            }
            return View();
        }


       

        [HttpGet]
        public ActionResult Add_Category()
        {
            if (Session["Admin_id"]==null)
            {
                return RedirectToAction("Admin_login");
            }

            //Session["ad_id"] = 2;//remove it 
            int Admin_id = Convert.ToInt32(Session["Admin_id"]);
            List<CATEGORY> cat = db.CATEGORies.Where(x=>x.CAT_ADMIN_ID == Admin_id).OrderBy(x => x.CAT_ID).ToList();
            ViewData["list"] = cat;
            return View();
        }

        [HttpPost]
        public ActionResult Add_Category(CATEGORY cat)
        {
            
            //----
            List<CATEGORY> cat_Li = db.CATEGORies.OrderBy(x => x.CAT_ID).ToList();
            ViewData["list"] = cat_Li;
            

            CATEGORY c = new CATEGORY();

            Random r = new Random();

            c.CAT_NAME = cat.CAT_NAME;
            c.CAT_ADMIN_ID = Convert.ToInt32(Session["Admin_id"].ToString());

            c.cat_encrypted_string = Crypt.Encrypt(cat.CAT_NAME.Trim() + r.Next().ToString(), true);
            db.CATEGORies.Add(c);
            Session["catid"] = c.CAT_ID.ToString();
            db.SaveChanges();

            return RedirectToAction("Add_Category");
        }

        [HttpGet]
        public ActionResult AddQuestions()
        {

            if (Session["Admin_id"] == null)
            {
                return RedirectToAction("Admin_login");
            }
            int sid = Convert.ToInt32(Session["Admin_id"]);
            


            List<CATEGORY> cat_li = db.CATEGORies.Where(x=>x.CAT_ADMIN_ID== sid).ToList();
            ViewBag.list = new SelectList(cat_li, "CAT_ID", "CAT_NAME");

                List<QUESTION> que = db.QUESTIONS.ToList();
                ViewData["que"] = que;

            return View();
        }

        [HttpPost]
        public ActionResult AddQuestions(QUESTION q)
        {
            
            int aid = Convert.ToInt32(Session["Admin_id"]);

            TempData["QUE_ID"] = q.QUE_ID.ToString();
            TempData.Keep();

            List<CATEGORY> cat_li = db.CATEGORies.Where(x => x.CAT_ADMIN_ID == aid).ToList();

            ViewBag.list = new SelectList(cat_li, "CAT_ID", "CAT_NAME");

            QUESTION qa = new QUESTION();

            qa.QUE_TEXT = q.QUE_TEXT;
            qa.OPT_A = q.OPT_A;
            qa.OPT_B = q.OPT_B;
            qa.OPT_C= q.OPT_C;
            qa.OPT_D = q.OPT_D;

            qa.CORRECT_OPT = q.CORRECT_OPT;

            qa.QUE_CAT_ID = q.QUE_CAT_ID;

            db.QUESTIONS.Add(qa);
            db.SaveChanges();
            ViewBag.msg = "Question Added Successfully";
            ModelState.Clear();
            return View();
        }

        [HttpGet]
        public ActionResult StudentSignup()
        {
            return View();
        }

        [HttpPost]
        public ActionResult StudentSignup(STUDENT_TBL st)
        {
            
                string filename = Path.GetFileNameWithoutExtension(st.ImageFile.FileName);
                string extension = Path.GetExtension(st.ImageFile.FileName);
                HttpPostedFileBase postedFile = st.ImageFile;
                int length = postedFile.ContentLength;

                if (extension.ToLower() == ".jpg" || extension.ToLower() == ".jpeg" || extension.ToLower() == ".png")
                {
                    if (length <= 1000000)
                    {
                        filename = filename + extension;
                        st.STUDENT_IMAGE = "~/Content/Studentimg/" + filename;
                        filename = Path.Combine(Server.MapPath("~/Content/Studentimg/"),filename);
                        st.ImageFile.SaveAs(filename);
                        db.STUDENT_TBL.Add(st);
                        int a= db.SaveChanges();

                        if (a>0)
                        {
                            TempData["CreateMsg"] = "<script>alert('Registered Successfully.');</script>";
                            ModelState.Clear();
                            return RedirectToAction("Student_login","Home");
                        }
                        else
                        {
                            TempData["CreateMsg"] = "<script>alert('Registration Failed...');</script>";
                        }
                    }
                    else
                    {
                        TempData["SizeMsg"] = "<script>alert('Image Size should be less than 1 MB');</script>";
                    }
                }
                else
                {
                    TempData["ExtensionMsg"] = "<script>alert('Format Not Supported');</script>";
                }

            
            return View();
        }


        

        public ActionResult Student_login()
        {
            return View();
        }

        [HttpPost]
        public ActionResult Student_login(STUDENT_TBL st)
        {
            STUDENT_TBL student = db.STUDENT_TBL.Where(x => x.STUDENT_NAME == st.STUDENT_NAME && x.STUDENT_PASSWORD == st.STUDENT_PASSWORD).SingleOrDefault();
            if (student != null)
            {
                Session["studentId"] = student.STUDENT_ID.ToString();
                Session["studentname"] = student.STUDENT_NAME.ToString();
                Session["studentimg"] = student.STUDENT_IMAGE.ToString();
                return RedirectToAction("ExamDashboard");
            }
            else
            {
                ViewBag.msg = "<script>alert('Invalid Name and Password..');</script>";
            }

            return View();
        }


        public ActionResult Dashboard()
        {
            if (Session["Admin_id"] == null)
            {
                return RedirectToAction("Admin_login");
            }



            return View();
        }

        public ActionResult ExamDashboard()
        {
            if (Session["studentId"] == null)
            {
                return RedirectToAction("Student_login");
            }
            return View();
        }

        [HttpPost]
        public ActionResult ExamDashboard(string room)
        {
            List<CATEGORY> list = db.CATEGORies.ToList();

            foreach (var item in list)
            {
                if (item.cat_encrypted_string==room)
                {

                    List<QUESTION> li = db.QUESTIONS.Where(x => x.QUE_CAT_ID == item.CAT_ID).ToList();
                    Queue<QUESTION> queue = new Queue<QUESTION>();

                    foreach (QUESTION a in li)
                    {
                        queue.Enqueue(a);
                    }

                    TempData["questions"]=queue;

                    TempData["Score"] = 0;



                    //TempData["examid"] = item.CAT_ID;
                    TempData.Keep();
                    return RedirectToAction("StartQuiz");
                }

                else
                {
                    ViewBag.error = "<script>alert('No Room Found. Please Enter Correct Room Name....');</script>";
                }
            }

            return View();
        }


        public ActionResult StartQuiz()
        {
            if (Session["studentId"]==null)
            {
                return RedirectToAction("Student_login"); 
            }

            QUESTION q = null;
            TempData["questionno"] = null;
            if (TempData["questions"]!=null)
            {
                
                Queue<QUESTION> qlist = (Queue<QUESTION>)TempData["questions"];
                if (qlist.Count>0)
                {
                    q = qlist.Peek();

                    qlist.Dequeue();
                    TempData["questions"] = qlist;
                    TempData.Keep();
                }
                else
                {
                    return RedirectToAction("EndExam");
                    
                }
            }
            else
            {
                return RedirectToAction("ExamDashboard");
            }

            return View(q);
            
        }


        [HttpPost]
        public ActionResult StartQuiz(QUESTION q)
        {
            
            string correctans = null;
            if (q.OPT_A!=null)
            {
                correctans = "A";
            }
            else if(q.OPT_B!=null)
            {
                correctans = "B";
            }
            else if (q.OPT_C != null)
            {
                correctans = "C";
            }
            else if (q.OPT_D != null)
            {
                correctans = "D";
            }

            if (correctans.Equals(q.CORRECT_OPT))
            {
                TempData["Score"] = Convert.ToInt32(TempData["Score"]) + 1;
                
            }
            TempData["questionno"] = Convert.ToInt32(TempData["questionno"]) + 1;
            TempData.Keep();

            return RedirectToAction("StartQuiz");
        }


        public ActionResult EndExam()
        {
            return View();
        }

        public ActionResult AdminLogout()
        {
            Session.Abandon();
            return RedirectToAction("Index", "Home");
        }

        public ActionResult Index()
        {
            return View();
        }

        

        public ActionResult About()
        {
            ViewBag.Message = "Your application description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

            return View();
        }
    }
}