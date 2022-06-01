<?php include"Home.php";?>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title>Untitled Document</title>
		<style>
			.row
			{
				font-family:Verdana, Geneva, sans-serif;
			}	
			.panel1
			{
				box-shadow:0 3px 3px 0 rgba(0,0,0,1),0 3px 2px -3px rgba(0,0,0,0.3),0 2px 5px 0 rgba(0,0,0,1);	
			}
			.container
			{
				width:100%;
			}
		</style>
	</head>
	<body background="img/ccc.jpg">
		<div class="container container-fluid"><br/><br/>
			<h4>
            	<a href="slider.php" style="color:#FFF; text-decoration:none">Home </a> <img src="img/back-ar.gif"/>
            	<a href="Cont.php" style="color:#FFF; text-decoration:none">Events </a> <img src="img/back-ar.gif"/>  <span style="color:#FFF">Engagement</span>  
            </h4>
		</div><br />
		<div class="container-fluid" >
			<div class="row">
    			<div class="col-sm-8">
          			<div class="panel panel-default panel1">
             			<div class="panel-heading text-capitalize text-center" style="background-color:#000; height:85px">
                			<h3 style="color:#F96;text-shadow:#F93 1px 1px 1px">
                            	<span class="glyphicon glyphicon-star-empty"></span>
                            <u>Engagement</u></h3>
			             </div>
			             <div class="panel-body" style="text-shadow:none;background-color:#900; height:200px"><br/>
               				<p class="#" style="line-height: 100%; margin-top: 0.02in; margin-bottom: 0.02in;font-family: 'Times New Roman', Times, serif; color:#FFFFFF; font-size:19px; text-shadow:#000 2px 4px 4px">&nbsp; &nbsp; &nbsp; Wedding anniversary events celebrate important milestones in a couple’s life together, and provide an opportunity for friends and relatives to join in celebrating that milestone. While most anniversary events occur at milestone intervals such as 10 years, 25 years, or 50 years, any anniversary can be a celebration.

For many couples, the anniversary celebration includes a renewal of vows. This may be quite similar to an actual Wedding, with all the same detailed facets to coordinate Flowers, Décor, Music, and Photography, as well as details of the reception.

Almost all wedding anniversary events include a reception. 
							</p>
    						<a href="booklogin.php" style="color:#0F0; margin-left:720px; font-size:18px; font-family:'Times New Roman', Times, serif; text-shadow:#000 2px 1px 1px ">Booking</a>
             			</div>     
          			</div>
      			</div><!--Column 1 close-->
      
      			<div class="col-sm-4">
					<div class="panel panel-default panel1">
             			<div class="panel-heading text-capitalize text-center" style="background-color:#000; height:85px">
                			<h3 style="color:#F96;text-shadow:#F93 1px 1px 1px">
                            	<span class="glyphicon glyphicon-picture"></span> <u>Gallery</u>
                            </h3>
             			</div>
						<div id="slider" class="carousel slide">
    						<div class="carousel-inner">
        						<div class="item active">
            						<img src="img/engage/1.jpg" style="width:100%; height:200px"/>
					            </div>
            					<div class="item">
					            	<img src="img/engage/2.jpg" style="width:100%; height:200px"/>
						        </div>
								<div class="item">
            						<img src="img/engage/3.jpg" style="width:100%; height:200px"/>
					            </div>
            					<div class="item">
            						<img src="img/engage/4.jpg" style="width:100%; height:200px"/>
					            </div>
            					<div class="item">
            						<img src="img/123456.jpg" style="width:100%; height:200px"/>
					            </div>
             					<div class="item">
            						<img src="img/Silver deco/2.jpg" style="width:100%; height:200px"/>
					            </div>
					        </div>
        					<a class="carousel-control left" href="#slider" data-slide="prev" >
            					<span class="icon-prev"></span>
        					</a>
   							<a class="carousel-control right" href="#slider" data-slide="next" >
            					<span class="icon-next"></span>
   							</a>
    					</div>
		          	</div>
	   			</div><!--Column 2 close-->
			</div><!--Row close-->
		</div><!--Main close for row-->
		<br/><br /><br />

	<?php include"footer.php";?>


		<script>
			$("#slider").carousel({interval:4000});
		</script>
	</body>
</html>
