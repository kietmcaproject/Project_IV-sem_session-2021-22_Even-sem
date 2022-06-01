<?php

include"Home.php";	
?>

<br/><br/><br/>
<div class="container-fluid"  style="width:40%; min-height:430px">
	
     	
        <h1 style="color:#006; font-family:'MS Serif', 'New York', serif; font-size:36px; text-shadow:#000 2px 1px 1px"><center><u>Change Password</u></center></h1>
        <br/>
        
	    
    	    	<form action="changepassdb.php" method="post" enctype="multipart/form-data">
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Username:</label>
        		        <input type="text" name="user" required class="form-control"
                		 placeholder="Enter your username" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Old Password:</label>
        		        <input type="password" name="opass" required class="form-control"
                		 placeholder="Enter old password" />
                	</div>
                	<br/>
          		  	
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <label>New Password:</label>
                    <input type="password" name="pass" required class="form-control" placeholder="Enter New Password"/>
                 </div>
                <br/>  
                
					<?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];
						  
						  ?>
                      </div>
                    </div>
                    <?php }?>
                <button type="submit" class="form-control btn btn-xs btn-success"><span class="glyphicon glyphicon-upload"></span> Update Password</button>
                <br/>
                <h5 style="color:#000"><center>--------------Or--------------</center></h5>
                
                <a href="Signup.php"><center>Create account</center></a>
                
                </form>
                </div>
       <?php include"footer.php";?>
</body>
</html>					
