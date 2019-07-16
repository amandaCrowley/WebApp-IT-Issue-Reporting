<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">		
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Edit Issue</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
		<body>
		
		<div class="title">			
			<h1>Add Issue to Knowledge Base</h1>
		</div>
		<div class="titlenav">
			<a href="javascript:void(0);" style="font-size:15px;" class="icon" onclick="responsiveLeftNav()">&#9776;</a>	
				<a class="logOff" href="${pageContext.request.contextPath}/controller">Log off</a>
			<div class="userDetails">Welcome ${userLoggedIn.firstName} ${userLoggedIn.surname}</div>
		</div>

		<div class="rows">
			<!-- Left Nav bar -->
			<div class="col left" id="leftNavBar">
				<jsp:include page="/Includes/Navigation.jsp" />
			</div>

			<div class="col middle" id="mainBody">
			<h1>Edit issue:</h1>
			<p>You may edit the issue before submitting to knowledge base</p>
				<form action="${pageContext.request.contextPath}/controller" method="POST" name="issueForm" id="issueForm">
					<table class="tableWithBorder">
						<tr>
							<th>Title</th><td><input type="text" name="Title" value="${currentIssue.getTitle()}"></td>
						</tr>
						<tr>
							<th>Description</th><td><textarea name="Description" rows="5" cols="50">${currentIssue.getDescription()}</textarea></td>
						</tr>
						<tr>
							<th>Keywords*</th>
							<td><input type="text" name="Keywords" value="<c:forEach items="${currentIssue.getKeywords()}" var="word"><c:out value="${word} "/></c:forEach>"/></td>
						</tr>
						<tr>
							<th>Add</th>
							<td>
								<input type="hidden" id="updateIssueID" name="updateIssueID" value="${currentIssue.issueID}">
								<input type="submit" name="updateIssue" value="Add to knowledge base">
							</td>
						</tr>
					</table>
				</form>
				<p>*Please format key words with commas and ensure that each is 25 characters or less e.g. "key, words"</p>
				<p class="redText">${errorMessage}</p>
			</div>	

			<div class="col right">
				<jsp:include page="/Includes/Maintenance.jsp" />	 
			</div>
			

		</div>		
	</body>
</html>