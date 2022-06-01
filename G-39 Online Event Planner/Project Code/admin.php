<link rel="stylesheet" href="bootstrap-3.3.6-dist/css/bootstrap.css" />
<html>
<head>
</head>
<body background="img/ccc.jpg">
<?php
include"adminheader.php";
?>
<br/><br/><br/>
<div class="container" style="min-height:472px">
<table class="table table-bordered table-bordered " style="margin-left:25%;width:50%;margin-top:100px">
 <tr bgcolor="#FF9966">
   <th><center>Lists</center></th>
   <th><center>Reference</center></th>
 </tr>  
  <tr style="color:#FFF; text-shadow:#0F0 1px 1px 1px">
  	<td><center>User List</center></td>
    <td><a href="userlist.php" style="color:#FFF"><center>Click here</center></a></td>
  </tr>
  <tr style="color:#FFF; text-shadow:#0F0 1px 1px 1px">
  	<td><center>Event Manager List</center></td>
    <td><a href="eventmanagerlist.php" style="color:#FFF"><center>Click here</center></a></td>
  </tr>
  <tr style="color:#FFF; text-shadow:#0F0 1px 1px 1px">
  	<td><center>Booking List</center></td>
    <td><a href="bookinglist.php" style="color:#FFF"><center>Click here</center></a></td>
  </tr>
  <tr style="color:#FFF; text-shadow:#0F0 1px 1px 1px">
  	<td><center>Feedback View List</center></td>
    <td><a href="feedbacklist.php" style="color:#FFF"><center>Click here</center></a></td>
  </tr>
  <tr style="color:#FFF; text-shadow:#0F0 1px 1px 1px">
  	<td><center>Payment List</center></td>
    <td><a href="pricelist.php" style="color:#FFF"><center>Click here</center></a></td>
  </tr>
</table>
</div>
<?php include"adminfooter.php";?>
</body>
</html>