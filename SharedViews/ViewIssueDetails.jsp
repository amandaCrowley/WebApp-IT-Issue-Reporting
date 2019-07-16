<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Issue Details</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
	<body>
			<div class="title">			
				<h1>Issue Details</h1>
				<div class="userDetails">
					Welcome ${userLoggedIn.firstName} ${userLoggedIn.surname}
				</div>
			</div>
			<div class="titlenav">
				<a href="javascript:void(0);" style="font-size:15px;" class="icon" onclick="responsiveLeftNav()">&#9776;</a>	
				<a class="logOff" href="${pageContext.request.contextPath}/controller">Log off</a>
			</div>
		
		<div class="row">
			<div class="col left" id="leftNavBar">
				<jsp:include page="/Includes/Navigation.jsp" />
			</div>

			<div class="col middle" id="mainBody">
			<h2>Reported Issue Details:</h2>
				<c:choose>
					<c:when test="${errorMessage == 'Success! Your comment has been added.' || errorMessage == 'Success! Issue has been updated.' || errorMessage == 'Success: Issue added to MyIssues' || errorMessage == 'Success: Issue removed from MyIssues'}">
						<p class="greenText">${errorMessage}</p> 
					</c:when>
					<c:otherwise>
						<p class="redText">${errorMessage}</p>
					</c:otherwise>
				</c:choose>
				<p><label class="boldText">Issue title: </label>${currentIssue.title} </p>
				<p><label class="boldText">Description: </label> ${currentIssue.description}</p>
				<c:if test="${not empty currentIssue.keywords}">
					<p><label class="boldText">Keywords: </label> ${currentIssue.keywords}</p>
				</c:if>

				<table class="tableWithBorder">
					<tr>
						<th>Reported DateTime: </th><td><fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${currentIssue.reportedDateTime}" /></td>
					</tr>
					<tr>
						<th>Reported by userID: </th><td><c:out value="${currentIssue.userID}"/></td>
					</tr>
					<tr>
						<th>Reported by userName: </th><td><c:out value="${reportedUserName}"/></td>	
					</tr>
					<!-- Use ternary operator for optional attributes e.g. ${empty attribute ? true : false} -->
					<tr>
						<th>Category: </th><td><c:out value="${currentIssue.category}"/></td>
					</tr>
					<tr>
						<th>Sub Category: </th><td><c:out value="${currentIssue.subCategory}"/></td>
					</tr>
					<tr>
						<th>Status: </th><td><c:out value="${currentIssue.status}"/></td>
					</tr>
					<tr>						
						<th>Resolved DateTime: </th>
						<td><fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${empty currentIssue.resolvedDateTime ? '' : currentIssue.resolvedDateTime}" /></td>
					</tr>	
					<tr>
						<th>Resolution Details: </th><td><c:out value="${empty currentIssue.resolutionDetails ? '-' : currentIssue.resolutionDetails}"/></td>		
					</tr>
					<c:if test="${userLoggedIn.isStaff()}">
					<tr>
						<th>Added to MyIssues: </th>
						<td>
							<form action="${pageContext.request.contextPath}/controller" name="myIssuesForm" method="POST">
								<input type="hidden" id="issueID" name="checkboxIssueID" value="${currentIssue.issueID}">
								<input type="checkbox" name="addMyIssues" onchange="submit()" <c:if test="${currentIssue.isInMyIssues(currentIssue.issueID, userLoggedIn)}">checked="checked"</c:if>/>
							</form>
						</td>
					</tr>
					</c:if>
				</table>
				<jsp:include page="/Includes/Comments.jsp"/>
			</div>		

			<div class="col right">
				<jsp:include page="/Includes/IssueActions.jsp"/>
			</div>
		</div>			
	</body>
</html>