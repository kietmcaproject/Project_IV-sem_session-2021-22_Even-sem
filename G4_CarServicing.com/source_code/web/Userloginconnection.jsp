<%-- 
    Document   : aa.jsp
    Created on : Apr 7, 2014, 3:18:51 PM
    Author     : prafull
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" import="java.sql.*"%>
<!DOCTYPE html>
<%
try
 
                     {
    
    Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
    Connection con=DriverManager.getConnection("jdbc:odbc:Carservice");
    
    Statement st=con.createStatement();
    String s="select * from Login where Email='"+request.getParameter("t1")+"'and Password='"+request.getParameter("t2")+"'";
    
    ResultSet rs=st.executeQuery(s);
    
    if(rs.next())
               {
       
    out.println("<a href='Mainpage.jsp'>continue</a>");
       }
    else
               {
        out.println("PLEASE ENTER VALID ID AND PASSWORD..........");
    
        out.println("<a href='Login.jsp'>Try Again</a>");   
               }
    
    
            
}
catch(Exception ex)
               {
    
    
    out.println(ex.getMessage());
}
%>
