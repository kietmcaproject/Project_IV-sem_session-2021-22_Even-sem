<?php include"connect.php";
 session_start();
 if(isset($_GET['c_id']))
  {        $id=$_GET['c_id'];	 
  			$res= mysqli_query($con,"select * from catering where c_id=$id")or die(mysqli_error($con));
			if($row=mysqli_fetch_array($res))
	  		{
				$c_name=$row[1];
		  		$c_price=$row[7];
	  		}
			$username=$_SESSION['my'];
			$resu=mysqli_query($con,"insert into addtocart values('$username','$c_name','$c_price')")or die(mysqli_error($con));
}
			if(mysqli_affected_rows($con)==1)
			{
				header("Location:ucatering.php?success");
			}
			else
			{
				header("Location:ucatering.php?err=Username..");
			}
?>