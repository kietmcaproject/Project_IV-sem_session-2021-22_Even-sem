<?php include"headeruser2.php"; 
include"connect.php";
	$res= mysqli_query($con,"select * from decoration where d_ref='mandap.php' ")or die(mysqli_error());
	if($row=mysqli_fetch_array($res))
	  {
		  $id=$row[0];
		  $name=$row[1];
		  $image=$row[2];
		  $price=$row[6];
	  }
?>
<html>
<head>
</head>
<body background="img/ccc.jpg">
	<div class="container container-fluid"><br/><br/><br />
			<h4 style="color:#FFF">
            	<a href="homeuser.php" style="color:#FFF; text-decoration:none">Home </a> <img src="img/back-ar.gif"/>
            	<a href="userdecoration.php" style="color:#FFF; text-decoration:none">Decoration </a> <img src="img/back-ar.gif"/> <span style="color:#FFF">Mandap</span>  
            </h4>
		</div><br />
        <div class="container-fluid">
                	<img src="<?php echo $image;?>" class="col-sm-3 img img-circle img-responsive" style="width:50%; height:250px;margin-top:20px"/>
                    <div class="row">
    			<div class="col-sm-3" style="margin-left:13%;">
          			<div class="panel panel-default panel1">
             			<div class="panel-heading" style="background-color:#000; box-shadow:#000 2px 3px 3px; height:70px">
                			<center><h3 style="color:#F96;text-shadow:#F93 1px 1px 1px">
                            <?php echo $name; ?></h3></center>
			             </div> 
                         <div class="panel-body" style="text-shadow:none;background-color:#FF0;box-shadow:#000 2px 3px 3px; height:160px"><br/>
                         <h4 style="color:#F00; text-shadow:#FFF 2px 2px 2px">Aayush Gupta</h4>
             			</div>     
          			</div>
      			</div><!--Column 1 close-->
			</div><!--Row close-->
            <h2 style="color:#F96; background:#000;text-shadow:#FFF 1px 1px 1px;margin-left:3%;width:15%;box-shadow:#333 2px 2px 2px">
            	<?php echo $price; ?>
            </h2>
            <a href="addtocartdeco.php?d_id=<?php echo $id; ?>&&d_price=<?php echo $price; ?>"><button type="submit" name="add" class="form-control btn btn-xs btn-success" style="width:25%; margin-left:63%"><span class="glyphicon glyphicon-shopping-cart"></span> Add to cart</button></a>
        </div>
		<br/><br />
	
</body>
</html>