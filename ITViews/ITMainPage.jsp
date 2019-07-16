<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>IT Main</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
		<link rel="stylesheet" href="/resources/demos/style.css">
		<script src="https://code.jquery.com/jquery-1.12.4.js"></script>
		<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
	<body>	
		<div class="title">			
			<h1>IT Main</h1>
			<div class="userDetails">
				Welcome ${userLoggedIn.firstName} ${userLoggedIn.surname}
			</div>
		</div>
		<div class="titlenav">
			<a href="javascript:void(0);"  class="icon" onclick="responsiveLeftNav()">&#9776;</a>	
			<a class="logOff" href="${pageContext.request.contextPath}/controller">Log off</a>
		</div>
		
		<div class="row">
			<!-- Left Nav bar -->
			<div class="col left" id="leftNavBar">
				<jsp:include page="/Includes/Navigation.jsp" />
			</div>

			<div class="col middle" id="mainBody">
				<jsp:include page="/Includes/MyIssues.jsp" />
			</div>	

			<div class="col right">
				<jsp:include page="/Includes/Maintenance.jsp" />
			</div>
		</div>	
			
	</body>
</html>