<?php
  
  session_start();
  include"connect.php";  
  session_destroy();
  header("Location:adminlogin.php");


?>