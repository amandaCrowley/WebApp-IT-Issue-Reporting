<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Knowledge Base Search</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
	<body>
		
		<div class="title">			
			<h1>Knowledge Base Search</h1>
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
				<h1>Knowledge base search:</h1>
				<form action="${pageContext.request.contextPath}/controller" name="search" method="POST">
					Search : 
					<br/>

					<input type="text" name="searchPhrase">

					<input type="submit" name="searchArticle" value="Search" />
				</form>
				<br/>
				"${filteredValue}" search results: ${fn:length(kbArticlesList)}
				<br/><br/>
				<c:choose>
					<c:when test="${not empty kbArticlesList}">

					<table class="issueTable">
						<c:forEach var="issue" items="${kbArticlesList}">
							<tr>					
								<td class="issueTitle">
									<form action="${pageContext.request.contextPath}/controller" name="articleForm" method="POST">
									<input type="hidden" name="articleID" value="${issue.issueID}" />
									<input type="submit" value="${issue.title}" />
								</form>						
								</td>
							</tr>
							<tr>					
								<td class="issueDate">
									<b>Resolved: </b>
									<fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${issue.resolvedDateTime}" />						
								</td>
								
							</tr>
							<tr>
								<td class="issueCategory">
									<b>Category: </b>
									<c:out value="${issue.category}"/>
									
								</td>
							</tr>
							<tr>					
								<td class="issueSubCategory">
									<b>Sub Category: </b>
									<c:out value="${issue.subCategory}"/>
										
								</td>
							</tr>
							<tr>					
								<td class="empty"></td>
							</tr>
						</c:forEach>
						</table>
					</c:when>    
					<c:otherwise>
						<p class="redText">No knowledge base articles found.</p>
					</c:otherwise>
				</c:choose>	
			</div>		

			<div class="col right">
				<jsp:include page="/Includes/Maintenance.jsp" />
			</div>
		</div>			
	</body>
</html>