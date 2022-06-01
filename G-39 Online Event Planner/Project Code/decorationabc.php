<link rel="stylesheet" href="bootstrap-3.3.6-dist/css/bootstrap.css" />
<html>
<head>
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
  $res= mysqli_query($con,"select * from decoration")or die(mysqli_error());
?>
<br/><br/><br/>
<table class="table table-bordered table-bordered" style="margin-left:8px;width:99%">
 <tr bgcolor="#FF9966">
   <th><center>Operations</center></th>
   <th><center>Id</center></th>
   <th><center>Name</center></th>
   <th><center>Image</center></th>
   <th><center>Description</center></th>
   <th><center>Decoration Reference</center></th>
   <th><center>User Decoration Reference</center></th>
   <th><center>Price</center></th>
 </tr>  
  <?php while($row=mysqli_fetch_array($res)){?>
     <tr style="color:#FFF">
     <td style="width:18%">
     	<a href="adddecoration.php" style="color:#FFF;"><button type="button" class="btn btn-success">Add</button></a>
        <a href="deletedecoration.php?d_id=<?php echo $row[0];?>" style="color:#FFF;"><button type="button" class="btn btn-danger" onClick="del()">Delete</button></a>
        <a href="updatedecoration.php?d_id=<?php echo $row[0];?>" style="color:#FFF;"><button type="button" class="btn btn-primary">Update</button></a></td>
     <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[0];?></td>
     <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[1];?></td>
    <td style="width:10%"><img src='<?php echo $row[2];?>' class="img img-responsive img-circle " width="100" height="100" /></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[3];?></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[4];?></td>
    <td style="text-shadow:#000 0px 0px 1px"><?php echo $row[5];?></td>
    <td style="text-shadow:#000 0px 0px 1px"><h6><?php echo $row[6];?></h6></td>
     </tr>
  <?php }?>
</table>
<?php include"adminfooter.php";?>
</body>
</html>