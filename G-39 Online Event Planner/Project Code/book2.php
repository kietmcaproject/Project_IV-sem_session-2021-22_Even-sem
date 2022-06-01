<html>
<head>
</head>

<?php include'connect.php';?>
<?php include'headeruser2.php'; ?>
<?php if(isset($_GET['id']))
  {        $id=$_GET['id'];
   ?>
<body background="img/ccc.jpg">
<br/><br/><br/><br/>
<div class="panel" style="min-height:200px;background:url(img/ccc.jpg);color:#FFF">
<div class="container-fluid"  style="width:80%; min-height:260px;">
<form  method="post" enctype="multipart/form-data">
<div class="form-horizontal">
					 <div class="form-horizontal">
                     <input type="checkbox" name="catering" required> <span style="font-size:24px;font-style:Normal; text-shadow:#000 1px 0px 0px">Catering</span></input>
                     <label style="font-size:14px; margin-left:25px">(If you have not choosen your catering yet then please move to catering menu and select your reasonable caterings as per your demands.)</label>
                     </div>
						<br />
                     <div class="form-horizontal">
                     <input type="checkbox" name="decoration" required> <span style="font-size:24px;font-style:Normal; text-shadow:#000 1px 0px 0px">Decoration</span></input>
                     <label style="font-size:14px; margin-left:25px">(If you have not choosen your decorations or theme then you may move to Decoartions section available at the bottom and choose your reasonable decorations as per your demands.)</label>
                     </div>
                     <br />
                     <div class="form-horizontal">
                     <input type="checkbox" name="venue" required> <span style="font-size:24px;font-style:Normal; text-shadow:#000 1px 0px 0px">Venue</span></input>
                     <label style="font-size:14px; margin-left:25px">(If you have not choosen your venue then you may move to Venue section available at the bottom and choose your reasonable Venue as per your demands.)</label>
                     </div>
                     <br /><br />
                     <?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];?>
                      </div>
                    </div>
                    <?php }?>
                    <a href="booking.php" style="color:#FFF;text-decoration:none"><button type="submit" class="form-control btn btn-xs btn-success" style="width:49%"><span class="glyphicon glyphicon-backward"></span> Back</button></a>
                    <a href="package.php?b_id=<?php echo $id; ?>" style="color:#FFF;text-decoration:none"><button type="button" class="form-control btn btn-xs btn-success" style="width:50%"><span class="glyphicon glyphicon-forward"></span> Next</button></a>
                
</form>
</div>
</div><br /><br />
<?php }?>
<?php include"userfooter.php";?>
</body>
</html>