<?php

include"Home.php";	
?>

<br/><br/><br/>
<div class="container-fluid"  style="width:40%; min-height:430px">
	
     	
        <h1 style="color:#006; font-family:'MS Serif', 'New York', serif; font-size:36px; text-shadow:#000 2px 1px 1px"><center><u>Log In</u></center></h1>
        <br/>
        
	    
    	    	<form action="emaillogindb.php" method="post" enctype="multipart/form-data">
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>User Name:</label>
        		        <input type="text" name="user" required class="form-control"
                		 placeholder="Enter User Name" />
                	</div>
                	<br/>
          		  	
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                    <label>Registered e-mail id:</label>
                    <input type="email" name="email" required class="form-control" placeholder="Enter e-mail id"/>
                 </div>
                <br/>
                <div class="form-horizontal">
                    <input type="checkbox" name="keep"  required="required"/>
                    <label>keep me logged in	</label>
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
                <button type="submit" class="form-control btn btn-xs btn-success"><span class="glyphicon glyphicon-user"></span> Log in</button>
                <br/>
                <h5 style="color:#000"><center>--------------Or--------------</center></h5>
                
                <a href="Signup.php"><center>Create account</center></a>
                <a href="login.php">Forget Email id</a><br/>
                <a href="moblogin.php">Forget Username</a>
                </form>
                </div>
      <?php include"footer.php"; ?>          
</body>
</html>					
</body>
</html>