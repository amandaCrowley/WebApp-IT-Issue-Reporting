<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1">
		<title>Knowledge Base</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/static/mainStyle.css">
		<script src="${pageContext.request.contextPath}/static/Main.js"></script>
	</head>
	<body>
		
		<div class="title">			
			<h1>Knowledge Base</h1>
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
				<h1>Knowledge base articles:</h1>
				<form action="${pageContext.request.contextPath}/controller" name="sortArticles" method="POST">
					Sort by category: 
					<select name="categories" onchange="submit()">
						<option value="noneSelected" selected="selected">--- Select a category ---</option>
						<c:forEach items="${categoryList}" var="cat">
							 <option value="${cat.category}" ${cat.category == selectedCategory ? 'selected="selected"' : ''}>${cat.category}</option> <!-- If the request param category is not empty it means this was selected in order to populate the sub cat list, therefore it needs to remain selected -->
						 </c:forEach>
					</select>
					<br/><br/>
					Sort by Sub category: 
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
					<br/><br/>
					<input type="submit" name="filterArticle" value="Filter by sub category" />
					<input type="submit" name="resetList" value="Reset" />
				</form>
				<br/>
				<c:if test="${empty filteredValue}">
					<%request.setAttribute("filteredValue", "none"); %>
				</c:if>
					<p class="greenText">Filtering by: ${filteredValue} </p>
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