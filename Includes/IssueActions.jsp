<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<form action="${pageContext.request.contextPath}/controller" method="POST">
	<h2>Issue Actions</h2>
	<c:choose>
		<c:when test="${currentIssue.status == 'Waiting on Reporter' && !sessionScope.userLoggedIn.isStaff()}"> 
			<!-- Issue status drop down list -->
			<jsp:include page="/Includes/IssueStatusDropDown.jsp" />
			<input type="submit" value="Update Issue" />
		</c:when>
		
		<c:when test="${currentIssue.status != 'Resolved' && sessionScope.userLoggedIn.isStaff()}">	
			<!-- Issue status drop down list -->
			<jsp:include page="/Includes/IssueStatusDropDown.jsp" />	
			<br /><br /> 
			<input type="submit" value="Update Issue" />
		</c:when>
		<c:otherwise>
			<p class="redText"> No actions currently available.</p>
		</c:otherwise>
	</c:choose>
</form>