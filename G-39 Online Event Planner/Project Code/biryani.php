<?php include"home.php"; 
include"connect.php";
	$res= mysqli_query($con,"select * from catering where c_ref='biryani.php' ")or die(mysqli_error());
	if($row=mysqli_fetch_array($res))
	  {
		  $image=$row[2];
		  $ingredients=$row[6];
		  $price=$row[7];
	  }
?>
<html>
<head>
</head>
<body background="img/ccc.jpg">
	<div class="container container-fluid"><br/><br/>
			<h4 style="color:#FFF">
            	<a href="slider.php" style="color:#FFF; text-decoration:none">Home </a> <img src="img/back-ar.gif"/>
            	<a href="catering.php" style="color:#FFF; text-decoration:none">Catering </a> <img src="img/back-ar.gif"/><a href="west.php" style="color:#FFF; text-decoration:none">West-Indian </a><img src="img/back-ar.gif"/> <span style="color:#FFF">Veg-Biryani</span>  
            </h4>
		</div><br />
        <div class="container-fluid">
                	<img src="<?php echo $image;?>" class="col-sm-3 img img-circle img-responsive" style="width:40%; height:180px;margin-top:20px"/>
                    <div class="row">
    			<div class="col-sm-5" style="margin-left:14%">
          			<div class="panel panel-default panel1">
             			<div class="panel-heading" style="background-color:#000; box-shadow:#000 2px 3px 3px; height:60px">
                			<h3 style="color:#F96;text-shadow:#F93 1px 1px 1px">
                            Ingredients</h3>
			             </div>
			             <div class="panel-body" style="text-shadow:none;background-color:#F96;box-shadow:#000 2px 3px 3px; height:160px"><br/>
                         <h4 style="color:#000; text-shadow:#FFF 2px 2px 2px"><?php echo $ingredients;?></h4>
             			</div>     
          			</div>
      			</div><!--Column 1 close-->
			</div><!--Row close-->
            <h2 style="color:#F96; background:#000;text-shadow:#FFF 1px 1px 1px;margin-left:3%;width:15%;box-shadow:#333 2px 2px 2px">
            	<?php echo $price; ?>
            </h2>
            <a href="login.php"><button type="submit" name="add" class="form-control btn btn-xs btn-success" style="width:25%; margin-left:63%"><span class="glyphicon glyphicon-shopping-cart"></span> Add to cart</button></a>
        </div>
		<br/><br />
	<?php include"footer.php";?>
</body>
</html>