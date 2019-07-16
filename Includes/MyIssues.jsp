<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<h1>My Issues</h1>
	<c:choose>
		<c:when test="${not empty myIssues}">
			<table class="issueTable">
			<c:forEach var="issue" items="${myIssues}">
				<tr>					
					<td class="issueTitle">
						<form action="${pageContext.request.contextPath}/controller" name="issueForm" method="POST">
							<input type="hidden" name="issueID" value="${issue.issueID}" />
							<input type="submit" value="${issue.title}" />
						</form>						
					</td>
				</tr>
				<tr>					
					<td class="issueDate">
						<b>Submitted: </b>
						<fmt:formatDate type = "both" dateStyle = "medium" timeStyle = "short" value = "${issue.reportedDateTime}" />						
					</td>
					
				</tr>
				<tr>
					<td class="issueStatus">
						<b>Status: </b>
						<c:out value="${issue.status}"/>
						
					</td>
				</tr>
				<tr>					
					<td class="issueDescription">
						
						<c:out value="${issue.description}"/>
							
					</td>
				</tr>
				<tr>					
					<td class="empty"></td>
				</tr>
			</c:forEach>
			</table>
		</c:when>    
		<c:otherwise>
			<c:if test="${userLoggedIn.isStaff()}">
				<p class="redText">You have no allocated issues. To add an issue subscribe to it on the all issues page.</p>
			</c:if>
			<p class="redText">You have no allocated issues.</p>
		</c:otherwise>
	</c:choose>


