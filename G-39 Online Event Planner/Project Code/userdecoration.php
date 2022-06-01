<?php include"headeruser2.php" ;

include"connect.php";
  $res= mysqli_query($con,"select * from decoration")or die(mysqli_error());

?>



<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>
<style>
		.row
		{
			font-family:"Comic Sans MS", cursive;
			text-shadow:#666 2px 2px 2px;
			color:#009;
		}
		.panel1
			{
				box-shadow:0 3px 3px 0 rgba(0,0,0,1),0 5px 5px -3px rgba(0,0,0,0.3),0 5px 10px 0 rgba(0,0,0,1);	
			}
</style>

</head>
<body background="img/ccc.jpg">
<br /><br />
<h4><a href="homeuser.php" style="color:#F00; text-shadow:#666 0px 1px 0px;text-decoration:none">&nbsp; Home </a> <img src="img/back-ar.gif"/><span style="color:#F00; text-shadow:#666 0px 1px 0px;"> Decoration</span></h4><br/>
<div class="container">	
	<?php 
     $numofRow=mysqli_num_rows($res);
   ?>
   		<?php for($i=1;$i<=$numofRow;$i++)
	  	{
		  ?>    
              <?php while($row=mysqli_fetch_array($res)){?>
          <div class="row" style="margin-left:0.5px; margin-right:0.5px">
          <div class="col-sm-4">
          <div class="panel panel-default panel1">
          <br/>
             <img src="<?php echo $row[2];?>" class="col-sm-3 img img-circle img-responsive" style="width:100%; height:150px"/>
             <br/>  
             <div class="panel-body" style="text-shadow:none"><br/><br/><br/><br/>
             	<h1 style="color:#900; text-shadow:#000 1px 2px 3px"><center> <a href="<?php echo $row[5];?>?c_id=<?php echo $row[0];?>" style="color:#900; text-decoration:none;"><?php echo $row[1];?></a></center></h1>
             </div>
          </div>
          </div>
             <?php } ?> 
      	  </div>
    
          <?php } ?>
</div><!--Main close for row-->
<?php include"userfooter.php";?>
</body>
</html>