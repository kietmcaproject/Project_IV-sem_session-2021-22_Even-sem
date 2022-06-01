<?php include"headeruser2.php"; 
session_start();
?>
<body background="img/ccc.jpg">
<br/><br/><br/>
<div class="container container-fluid" style="width:50%;height:570px" >
		
        <h1 style="color:#FFF;text-shadow:#09F 2px 2px 2px;font-family:'MS Serif', 'New York', serif"><center><u>Feedback</u></center></h1>
        
        
    	    	<form action="feedbackdb.php" method="post" enctype="multipart/form-data" style="color:#FFF">
                	<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>User Name:</label>
        		        <input type="text" name="user" required class="form-control"
                		 placeholder="Enter User Name" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>E-mail Id:</label>
        		        <input type="email" name="email" required class="form-control"
                		 placeholder="Enter email id" />
                	</div>
                	<br/>
                   <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Subject:</label>
        		        <input type="text" name="sub" required class="form-control"
                		 placeholder="Enter Subject" />
                	</div>
                	<br/>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Feedback:</label>
        		        <textarea name="feed" rows="5" cols="50" required class="form-control"
                		 placeholder="Enter feedback message..." ></textarea>
                	</div>
                	<br/>
					<?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];?>
                      </div>
                    </div>
                    <?php }?>
                <button type="submit" class="form-control btn btn-xs btn-success"><span class="glyphicon glyphicon-send"></span> Send</button>
                </form>
               </div>
 <?php include"userfooter.php"; ?>
</body>
</html>