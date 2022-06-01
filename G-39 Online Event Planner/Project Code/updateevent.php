<html>
<head>
<br/><br/>
<script language="javascript">
	function update()
	{
		alert('You have successfully updated the event...');
	}
</script>
</head>

<body>


<?php 
	include"Home.php";
  include("connect.php");
  
  if(isset($_GET['e_id']))
  {
	  $id=$_GET['e_id'];
	  $res=mysqli_query($con,"select * from events where e_id=$id");
	  if($row=mysqli_fetch_array($res))
	  {
		  $name=$row[1];
		  $image=$row[2];
		  $descri=$row[3];
		  $ref=$row[4];
		  $uref=$row[5];
	  }
  }
?>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<link rel="stylesheet" href="bootstrap-3.3.6-dist/css/bootstrap.css" />
</head>

<body>
<br/><br /><br />
<div class="container container-fluid">
	<div class="panel panel-default panel-info">
     	<div class="panel-heading text-center text-nowrap">
        <h2>Update Event Information</h2>
        </div>
        <div class="panel-body">
        <?php
            if(isset($_GET['success']))
			{
				?>
                <div class="navbar alert alert-success">Event Successfully Inserted..</div>
                <?php
			}
		  ?>
          
           <?php
            if(isset($_GET['error']))
			{
				?>
                <div class="navbar alert alert-danger">Some Database Issues...</div>
                <?php
			}
		  ?>
         
        <div class="form-control-static container-fluid">
        	<form action="updateeventdb.php" method="post" enctype="multipart/form-data">
            	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>			<input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Name:</label>
                <input type="text" name="name"  class="form-control"
                 placeholder="Enter Event Name" value="<?php echo $name;?>" />
                </div>
                <br/>
               <div class="form-horizontal">
                    <label>Upload Your Pic</label>
                    <input type="file" accept="image/*" name="image" requiredautocomplete="off" class="form-control"/>
                    <img src="<?php echo $image;?>" style="width:100px; height:100px" class="img img-responsive img-thumbnail"/>
                 </div>
                <br/>
                 <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>			<input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Description:</label>
                <textarea rows="5" cols="50" name="des" required class="form-control" placeholder="Enter Event Description" value="<?php echo $descri;?>"></textarea>
                </div>
                <br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Details Reference:</label>
                <input type="text" name="ref" required class="form-control"
                 placeholder="Enter Event Name" value="<?php echo $ref;?>"/>
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>User Reference Details:</label>
                <input type="text" name="uref" required class="form-control"
                 placeholder="Enter Event Name" value="<?php echo $uref;?>"/>
                </div>
                <br/>
                <button type="submit" class="form-control btn btn-xs btn-success" onClick="update()"><span class="glyphicon glyphicon-registration-mark"></span>Update Event</button>
            </form>
        </div>
	</div>
</div>
</div>

</body>
</html>