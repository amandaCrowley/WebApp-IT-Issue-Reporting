<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Login</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/loginStyle.css">
	</head>
	<body>
		<div class="loginContainer">
			<form action="${pageContext.request.contextPath}/controller" name="loginForm" method="POST">
				<h3>Login</h3>
				<label>Username: </label>

				<input type="text" name="userName" placeholder=" Enter Username"> 
			    <label id="optionLabel">Password: </label>
			    <input type="password" name="password" placeholder="Enter Password"> 
			    <input type="submit" name="login" value="Login"/>
			</form>
			
			<c:choose>
				<c:when test="${errorMessage == 'Success. You have been logged out.'}">
					<p class="greenText">${errorMessage}</p> 
				</c:when>
				<c:otherwise>
					<p class="redText">${errorMessage}</p>
				</c:otherwise>
			</c:choose>
		</div>
	</body>
</html>