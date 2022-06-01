
<html>
<head>
<br/><br/>
<script language="javascript">
	function update()
	{
		alert('You have successfully updated the venue...');
	}
</script>
</head>

<body>


<?php 
	include"Home.php";
  include("connect.php");
  
  if(isset($_GET['v_id']))
  {
	  $id=$_GET['v_id'];
	  $res=mysqli_query($con,"select * from venue where v_id=$id");
	  if($row=mysqli_fetch_array($res))
	  {
		  $name=$row[1];
		  $image=$row[2];
		  $add=$row[3];
		  $descri=$row[4];
		  $ref=$row[5];
		  $uref=$row[6];
		  $pri=$row[7];
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
        <h2>Update Venue Information</h2>
        </div>
        <div class="panel-body">
        <?php
            if(isset($_GET['success']))
			{
				?>
                <div class="navbar alert alert-success">Venue Successfully Inserted..</div>
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
        	<form action="updatevenuedb.php" method="post" enctype="multipart/form-data">
            	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>			<input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Name:</label>
                <input type="text" name="name"  class="form-control"
                 placeholder="Enter Venue Name" value="<?php echo $name;?>" />
                </div>
                <br/>
               <div class="form-horizontal">
                    <label>Upload Your Pic</label>
                    <input type="file" accept="image/*" name="image" requiredautocomplete="off" class="form-control"/>
                    <img src="<?php echo $image;?>" style="width:100px; height:100px" class="img img-responsive img-thumbnail"/>
                 </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>			<input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Venue Address:</label>
                <input type="text" name="name"  class="form-control"
                 placeholder="Enter Venue Address" value="<?php echo $add;?>" />
                </div>
                <br/>
                 <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>			<input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Description:</label>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <input type="text" name="des"  required class="form-control"
                 placeholder="Enter Venue Description" value="<?php echo $descri;?>" />
                </div>
                <br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Details Reference:</label>
                <input type="text" name="ref" required class="form-control"
                 placeholder="Enter Venue Reference" value="<?php echo $ref;?>"/>
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>User Reference Details:</label>
                <input type="text" name="uref" required class="form-control"
                 placeholder="Enter User Venue Reference" value="<?php echo $uref;?>"/>
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <input type="hidden" name="id" value="<?php echo $id;?>"/>
                <label>Price:</label>
                <input type="text" name="pri" required class="form-control"
                 placeholder="Enter Venue Price" value="<?php echo $pri;?>"/>
                </div>
                <br/>
                <button type="submit" class="form-control btn btn-xs btn-success" onClick="update()"><span class="glyphicon glyphicon-registration-mark"></span>Update Venue</button>
            </form>
        </div>
	</div>
</div>
</div>

</body>
</html>