<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>User Main</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
	<body>
		
		<div class="title">			
			<h1>User Main</h1>
			<div class="userDetails">
				Welcome ${userLoggedIn.firstName} ${userLoggedIn.surname}
			</div>
		</div>
		<div class="titlenav">
			<a href="javascript:void(0);" style="font-size:15px;" class="icon" onclick="responsiveLeftNav()">&#9776;</a>				
			<a class="logOff" href="${pageContext.request.contextPath}/controller">Log off</a>
		</div>
		<div class="row">
			<!-- Left Nav bar -->
			<div class="col left" id="leftNavBar">
				<jsp:include page="/Includes/Navigation.jsp" />
			</div>

			<div class="col middle" id="mainBody">
				<p class="greenText">${errorMessage}</p>
				<jsp:include page="/Includes/MyIssues.jsp" />
			</div>	

			<div class="col right">
				<jsp:include page="/Includes/Maintenance.jsp" />	 
			</div>
		</div>		
	</body>
</html>