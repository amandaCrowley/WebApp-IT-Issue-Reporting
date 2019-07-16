<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">		
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Report Issue</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
		<body>
		
		<div class="title">			
			<h1>Report Issue</h1>
			<div class="userDetails">
				Welcome ${userLoggedIn.firstName} ${userLoggedIn.surname}
			</div>
		</div>
		<div class="titlenav">
			<a href="javascript:void(0);" style="font-size:15px;" class="icon" onclick="responsiveLeftNav()">&#9776;</a>	
			<a class="logOff" href="${pageContext.request.contextPath}/controller">Log off</a>
		</div>

		<div class="rows">
			<!-- Left Nav bar -->
			<div class="col left" id="leftNavBar">
				<jsp:include page="/Includes/Navigation.jsp" />
			</div>

			<div class="col middle" id="mainBody">
			<h1>Add a new issue:</h1>
				<form action="${pageContext.request.contextPath}/controller" method="POST" name="issueForm" id="issueForm">
					<table class="tableWithBorder">
					<tr>
							<th>Category</th>
							<td>
								<select name="categoriesReportIssue" onchange="submit()">
									<option value="noneSelected" selected="selected">--- Select a category ---</option>
									<c:forEach items="${categoryList}" var="cat">
										 <option value="${cat.category}" ${cat.category == selectedCategory ? 'selected="selected"' : ''}>${cat.category}</option> <!-- If the request param category is not empty it means this was selected in order to populate the sub cat list, therefore it needs to remain selected -->
									 </c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<th>Sub Category</th>
							<td>
								<select name="subCategories">
									<c:choose>
										<c:when test="${empty subCategoryList}">
											<option value="noneSelected" selected="selected">--- Please select parent category ---</option><!-- A parent cat has not been selected -->
										</c:when>
										<c:otherwise>
											<option value="noneSelected" selected="selected">--- Select a sub category ---</option><!-- A parent cat has been selected and only sub cats of this category are being displayed, select a sub cat is optional-->
										</c:otherwise>
									</c:choose>
									<c:forEach items="${subCategoryList}" var="subCat">
										<option value="${subCat}">${subCat}</option>
									</c:forEach>
								</select>
							</td>
						</tr>
						<tr>
							<th>Title</th><td><input type="text" name="Title"></td>
						</tr>
						<tr>
							<th>Description</th><td><textarea name="Description" rows="5" cols="50" placeholder="Enter description here"></textarea></td>
						</tr>
						<tr>
							<th>Keywords*</th>
							<td><input type="text" name="Keywords"></td
						></tr>
						<tr>
							<th>Submit</th><td><input type="submit" name="submitIssue" value="Submit new issue"></td>
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