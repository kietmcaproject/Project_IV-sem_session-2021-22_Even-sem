
<html>
<head>
<br/><br/>
<script language="javascript">
	function add()
	{
		alert('You have successfully added the catering item...');
	}
</script>
</head>

<body>


<?php

include"home.php";
include"connect.php";
?>
<br/><br/><br/>
<div class="container container-fluid">
	<div class="panel panel-default panel-info">
     	<div class="panel-heading text-center text-nowrap">
        <h2>Enter catering Information</h2>
        </div>
        <div class="panel-body">
         <?php
            if(isset($_GET['success']))
			{
				?>
                <div class="navbar alert alert-success">Caternig Successfully Inserted..</div>
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
        	<form action="cateringdb.php" method="post" enctype="multipart/form-data">
            	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>Name:</label>
                <input type="text" name="name" required class="form-control"
                 placeholder="Enter Dish Name" />
                </div>
                <br/>
                <div class="form-horizontal">
                    <label>Upload Dish Pic</label>
                    <input type="file" accept="image/*" name="image" requiredautocomplete="off" class="form-control"/>
                 </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>Description:</label>
                <input type="text" name="des"  required class="form-control"
                 placeholder="Enter description..." />
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>Details Reference:</label>
                <input type="text" name="ref"  required class="form-control"
                 placeholder="Enter reference url..." />
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>User Reference Details:</label>
                <input type="text" name="uref"  required class="form-control"
                 placeholder="Enter user reference url..." />
                </div>
                <br/>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>Item Ingredients:</label>
                <input type="text" name="ind"  required class="form-control"
                 placeholder="Enter item ingredients..." />
                </div>
                <br/>
	           <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                <label>Item Price:</label>
                <input type="text" name="pri"  required class="form-control"
                 placeholder="Enter item price..." />
                </div>
                <br/>

                
                <button type="submit" class="form-control btn btn-xs btn-success" onClick="add()"><span class="glyphicon glyphicon-upload"></span>Upload</button>
                </form>
</body>
</html>