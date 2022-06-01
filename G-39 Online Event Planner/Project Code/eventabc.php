<link rel="stylesheet" href="bootstrap-3.3.6-dist/css/bootstrap.css" />

<html>
<head>
<br/><br/>
<script language="javascript">
	function del()
	{
		alert('You have successfully deleted...');
	}
</script>
</head>

<body background="img/ccc.jpg">


<?php
include"adminheader.php";
include"connect.php";
  $res= mysqli_query($con,"select * from events")or die(mysqli_error());
?>
<br/>
<table class="table table-bordered table-bordered" style="margin-left:8px;width:99%">
 <tr bgcolor="#FF9966">
 
   <th><center>Operations</center></th>
   <th><center>Id</center></th>
   <th><center>Name</center></th>
   <th><center>Image</center></th>
   <th><center>Description</center></th>
   <th><center>Events Reference</center></th>
   <th><center>User Events Reference</center></th>
 </tr>
  
  <?php while($row=mysqli_fetch_array($res)){?>
  
     <tr style="color:#FFF">
     <td style="width:18%">
     	<button type="button" class="btn btn-success"><a href="addevent.php" style="color:#FFF;">Add</a></button>
        <button type="button" class="btn btn-danger" onclick="del()"><a href="deleteevent.php?e_id=<?php echo $row[0];?>" style="color:#FFF;">Delete</a></button>
        <button type="button" class="btn btn-primary"><a href="updateevent.php?e_id=<?php echo $row[0];?>" style="color:#FFF;">Update</a></button></td>
     <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[0];?></td>
     <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[1];?></td>
    <td><img src='<?php echo $row[2];?>' class="img img-responsive img-circle " width="100" height="100" /></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[3];?></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[4];?></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[5];?></td>
   </tr>
  
  
  
  <?php }?>
</table>
<?php include"adminfooter.php";?>
</body>
</html>