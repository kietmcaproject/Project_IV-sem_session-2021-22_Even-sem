
<?php
  include("connect.php");
  include"adminheader.php";
 
  

  $res= mysqli_query($con,"select * from feedback")or die(mysqli_error($con));

   

?>
<body background="img/ccc.jpg">
<br/><br/><br/>
<div class="container" style="min-height:472px">
<table class="table table-bordered table-bordered" style="margin-left:8px;width:99%">
 <tr bgcolor="#FF9966">
   <th><center>Customer Name</center></th>
   <th><center>E-mail</center></th>
   <th><center>Subject</center></th>
   <th><center>Feedback</center></th>
 </tr>
  
  <?php while($row=mysqli_fetch_array($res)){?>
  
     <tr style="color:#FFF">
     
   	 <td><?php echo $row[0];?></td>    
     <td><?php echo $row[1];?></td>
     <td><?php echo $row[2];?></td>
     <td><?php echo $row[3];?></td>
	
     </tr>
  
  
  
  <?php }?>
</table>

</div>
<?php include"adminfooter.php"; ?>
</body>