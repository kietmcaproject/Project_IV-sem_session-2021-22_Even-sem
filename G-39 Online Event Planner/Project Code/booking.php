<html>
<head>
</head>
<body background="img/ccc.jpg">
<?php include'connect.php';?>
<?php include'headeruser2.php'; ?>
<br/><br/><br/>
<h4><a href="homeuser.php" style="color:#F00; text-shadow:#666 0px 1px 0px;text-decoration:none">&nbsp; Home </a> &nbsp; <img src="img/back-ar.gif"/><span style="color:#F00; text-shadow:#666 0px 1px 0px;"> Booking</span></h4>

<div class="panel" style="min-height:200px;background:url(img/ccc.jpg);color:#FFF">
<div class="container-fluid"  style="width:60%; min-height:360px;">
        <h1 style="color:#FFF; font-family:'MS Serif', 'New York', serif; font-size:36px; text-shadow:#030 2px 1px 1px;"><center><u>Enter Booking Informatin</u></center></h1>
    
    	    	<form action="bookingdb.php" method="post" enctype="multipart/form-data">
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card;"></span>
		                <label>Customer Name:</label>
        		        <input type="text" name="customer" required class="form-control"
                		 placeholder="Enter Full Name" required="required"/>
                	</div>
                	<br/>
          		  	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>E mail:</label>
        		        <input type="email" name="email" required class="form-control"
                		 placeholder="Enter Email id" required="required" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Mob. No.:</label>
        		        <input type="number" name="mob" required class="form-control"
                		 placeholder="Enter Mobile Number" required="required"/>
                	</div>
                	<br/>
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Number of Guest:</label>
        		        <input type="number" name="nog" required class="form-control"
                		 placeholder="Enter Mobile Number" required="required"/>
                	</div>
                    <br />
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Event Date:</label>
        		        <input type="date" name="edate" required class="form-control"
                		 placeholder="Enter event date" required="required"/>
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		               <label> Choose Events: </label>   
					<select name="event" class="control" style="color:#006; width:150px">
						<option>Select Event</option>
								<option><?php $res= mysqli_query($con,"select * from events")or die(mysqli_error());
	while($row=mysqli_fetch_array($res)) {?>
    <?php 
	echo $row[1];
	?></option>
    <option><?php }?></option>
                                <br />

                     </select>
                	</div>
                    <br/>
					<?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];?>
                      </div>
                    </div>
                    <?php }?>
                <a href="book2.php" style="color:#FFF;text-decoration:none"><button type="submit" class="form-control btn btn-xs btn-success" style="width:49%"><span class="glyphicon glyphicon-forward"></span> Next</button></a>
                <button type="reset" class="form-control btn btn-xs btn-default" style="width:50%"><span class="glyphicon glyphicon-repeat"></span> Reset</button>
                </form></div></div>
                </div>
                </div>
                <?php include"userfooter.php";?>

</body>
</html>