<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- Issue status drop down list -->
	Issue status: 
	<select name="issueStatus">
		<option value="noneSelected" selected="selected">--- Select a status ---</option>
		<c:forEach items="${statusList}" var="status">
			<option value="${status}">${status}</option>
		</c:forEach>
	</select>
