<html>
<head>
<br/><br/>
<script language="javascript">
	function sign()
	{
		alert('You have successfully registered...');
	}
</script>
</head>

<body>
<?php include"Home.php"; ?>
<br/><br/><br/>
<div class="container container-fluid" >
	<div class="panel panel-default panel-info">
     	<div class="panel-heading text-center text-nowrap" style="color:#000;background-color:#060;text-shadow:#CCC 2px 2px 2px;font-family:'MS Serif', 'New York', serif">
        <h1>Enter Admin Information</h1>
        </div>
        <div class="panel-body" >
	        <div class="form-control-static container-fluid">
    	    	<form action="adminsignupdb.php" method="post" enctype="multipart/form-data">
          		  	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Name:</label>
        		        <input type="text" name="name" required class="form-control"
                		 placeholder="Enter your Name" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <label>Password:</label>
                    <input type="password" name="pass" required class="form-control" placeholder="Enter Password"/>
                 </div>
                <br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>E-mail:</label>
        		        <input type="email" name="email" required class="form-control"
                		 placeholder="Enter e-mail id" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Mob. No.:</label>
        		        <input type="number" name="mob" required class="form-control"
                		 placeholder="Enter Mobile No." />
                	</div>
                    <br/>
					<?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];?>
                      </div>
                    </div>
                    <?php }?>
                <button type="submit" class="form-control btn btn-xs btn-success" onClick="sign()" ><span class="glyphicon glyphicon-user"></span> Sign Up</button>
                </form>
                </div></div></div></div>
 <?php include"footer.php"; ?>
</body>
</html>