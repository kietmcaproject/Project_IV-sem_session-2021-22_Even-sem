<?php include"connect.php";
 session_start();
 if(isset($_GET['v_id']))
  {        $id=$_GET['v_id'];	 
  			$res= mysqli_query($con,"select * from venue where v_id=$id")or die(mysqli_error($con));
			if($row=mysqli_fetch_array($res))
	  		{
				$v_name=$row[1];
		  		$v_price=$row[7];
	  		}
			$username=$_SESSION['my'];
			$resu=mysqli_query($con,"insert into addtocart values('$username','$v_name','$v_price')")or die(mysqli_error($con));
}
			if(mysqli_affected_rows($con)==1)
			{
				header("Location:uservenue.php?success");
			}
			else
			{
				header("Location:uservenue.php?err=Username..");
			}
?>