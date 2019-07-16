<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>

<h1>All Issues</h1>
	<c:choose>
		<c:when test="${not empty allIssues}">
			<c:forEach var="status" items="${statusList}">
				<table class="issueTable ${issue}">
				<tr><th>${status}</th></tr>
				<tr>					
					<td class="empty"></td>
				</tr>
				<c:forEach var="issue" items="${allIssues}">
					<c:if test="${issue.status == status}">
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
							<td class="issueStatus ${status}">
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
					</c:if>    
				</c:forEach>
				</table>
			</c:forEach>
		</c:when>    
		<c:otherwise>
			There are no issues at this time.
		</c:otherwise>
	</c:choose>


