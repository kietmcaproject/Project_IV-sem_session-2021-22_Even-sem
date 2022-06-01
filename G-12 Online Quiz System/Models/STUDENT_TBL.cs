//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------
using System.Web;
using System.ComponentModel.DataAnnotations;
namespace QuizApp.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class STUDENT_TBL
    {
        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2214:DoNotCallOverridableMethodsInConstructors")]
        public STUDENT_TBL()
        {
            this.SET_EXAM = new HashSet<SET_EXAM>();
        }
    
        public int STUDENT_ID { get; set; }

        [Required(ErrorMessage = "Student Name is Required")]
        [Display(Name = "Student Name")]
        public string STUDENT_NAME { get; set; }

        [Required(ErrorMessage = "Student Password is Required")]
        [Display(Name = "Student Password")]
        [DataType(DataType.Password)]
        public string STUDENT_PASSWORD { get; set; }

        [Required(ErrorMessage = "Student Image is Required")]
        [Display(Name = "Student Image")]
        public string STUDENT_IMAGE { get; set; }

        [Required(ErrorMessage = "Email is Required")]
        [Display(Name = "Email")]
        [DataType(DataType.EmailAddress)]
        [RegularExpression("^[a-zA-Z0-9_\\.-]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$", ErrorMessage = "E-mail is not valid")]
        public string StudentEmail { get; set; }

        [Required(ErrorMessage = "Confirm Password is Required")]
        [Display(Name = "Confirm Password")]
        [DataType(DataType.Password)]
        [Compare("STUDENT_PASSWORD", ErrorMessage = "Password not equal")]
        public string ConfirmPassword { get; set; }

        public HttpPostedFileBase ImageFile { get; set; }

        [System.Diagnostics.CodeAnalysis.SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly")]
        public virtual ICollection<SET_EXAM> SET_EXAM { get; set; }
    }
}
