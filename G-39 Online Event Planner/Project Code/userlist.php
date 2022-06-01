
<?php
  include("connect.php");
  include"adminheader.php";
 
  

  $res= mysqli_query($con,"select * from signupinfo")or die(mysqli_error($con));

   

?>
<body background="img/ccc.jpg">
<br/><br/><br/>
<div class="container" style="min-height:472px">
<table class="table table-bordered table-bordered" style="margin-left:8px;width:99%">
 <tr bgcolor="#FF9966">
 
   <th><center>Id</center></th>
   <th><center>Username</center></th>
   <th><center>Firstname</center></th>
   <th><center>Lastname</center></th>
   <th><center>E-mail</center></th>
   <th><center>Password</center></th>
   <th><center>Confirm Password</center></th>
   <th><center>DOB</center></th>
   <th><center>City</center></th>
   <th><center>Mobile No.</center></th>
 </tr>
  
  <?php while($row=mysqli_fetch_assoc($res)){?>
  
     <tr style="color:#FFF">
     
   	 <td><?php echo $row['id'];?></td>    
     <td><?php echo $row['username'];?></td>
     <td><?php echo $row['firstname'];?></td>
     <td><?php echo $row['lastname'];?></td>
     <td><?php echo $row['Email'];?></td>
     <td><?php echo $row['password'];?></td>
     <td><?php echo $row['cpassword'];?></td>
     <td><?php echo $row['dob'];?></td>
     <td><?php echo $row['city'];?></td>
     <td><?php echo $row['mobno'];?></td>
	
     </tr>
  
  
  
  <?php }?>
</table>

</div>
<?php include"adminfooter.php"; ?>
</body>