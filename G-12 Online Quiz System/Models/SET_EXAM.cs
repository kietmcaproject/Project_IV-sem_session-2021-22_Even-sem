//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated from a template.
//
//     Manual changes to this file may cause unexpected behavior in your application.
//     Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace QuizApp.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class SET_EXAM
    {
        public int EXAM_ID { get; set; }
        public Nullable<System.DateTime> EXAM_DATE { get; set; }
        public Nullable<int> ENROLL_STUDENT { get; set; }
        public string EXAM_NAME { get; set; }
        public Nullable<int> EXAM_SCORE { get; set; }
    
        public virtual STUDENT_TBL STUDENT_TBL { get; set; }
    }
}