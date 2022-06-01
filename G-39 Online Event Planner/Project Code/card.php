
<?php include"connect.php";
include"headeruser2.php";
if(isset($_GET['total']))
  {        $amount=$_GET['total'];	 
  }

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Untitled Document</title>

<script language="javascript">
	function pay()
	{
		alert('You have successfully paid and book an event.....');
	}
</script>


</head>

<body background="img/ccc.jpg">

<div class="container container-fluid"><br/><br/><br />
			<h4 style="color:#FFF">
            	<a href="payment.php" style="color:#FFF; text-decoration:none">back </a> <img src="img/back-ar.gif"/>
            </h4>
</div>
<div class="container-fluid"  style="width:40%; min-height:450px;">
	
     	
        <h1 style="color:#0F0; font-family:'MS Serif', 'New York', serif; font-size:36px; text-shadow:#000 2px 1px 1px"><center><u>Enter Card Details</u></center></h1>
        <br/>
          <form action="paymentdb.php" method="post" style="color:#FFF">
          		<div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
                	<label> Select Paymnet Mode: </label>   
					<select name="type" class="control" style="color:#000">
						<option>SELECT Modes</option>
								<option>Credit Card</option>
								<option>Debit Card</option>
								<option>Net Banking</option>
					</select>
                 </div>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Your Card Name:</label>
        		        <input type="text" name="name" placeholder="Enter Your Card Name"  required="required" class="form-control" />
                	</div>
                <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Your Card Number:</label>
        		        <input type="text" name="card" placeholder="Enter 16 Digit Valid Card Number"  required="required" class="form-control" />
                	</div>
               <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Your Card CVV Number:</label>
        		        <input type="text" name="cvv" placeholder="Enter 3 Digit Valid CVV Number" required class="form-control" />
                	</div>
               <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Expiry Date (MM/YYYY):</label>
        		        <input type="date" name="edate" required class="form-control" />
                	</div>
                    <div class="form-horizontal"><span class="glyphicon glyphicon-credit-card"></span>
		                <label>Your Amount:</label>
        		        <input type="text" name="amount" value="<?php echo $amount;?>"  required="required" readonly="readonly" class="form-control" />
                	</div>
                	<br/>
					<?php if(isset($_GET['err'])){?>
                    <div class="form-horizontal">
                      <div class="navbar alert-danger">
                          <?php echo $_GET['err'];?>
                      </div>
                    </div>
                    <?php }?>

                <center><button type="submit" class="form-control btn btn-xs btn-success"  onclick="pay()" style="width:30%"><span class="glyphicon glyphicon-buy"></span> Pay</button></center>
                </form>
</div>
<?php include"userfooter.php";?>
</body>
</html>