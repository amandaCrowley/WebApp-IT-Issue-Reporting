<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<h2>Comments:</h2>
<display:table name="sessionScope.currentKBCommentList" id="commentTable" keepStatus="true" class="displayTagTable" pagesize="10"> <!-- Name must match the name of the session param that you set the list you'd like to display e.g. session.setAttribute( "currentCommentList", currentCommentList); -->
    <display:column title="No."><%=pageContext.getAttribute("commentTable_rowNum")%></display:column>
    <display:column property="userName" title="Comment by"/> 
    <display:column property="commentValue" title="Comment"/>

    <!-- Table properties also located in WEB-INF/displaytag.properties file-->
    <display:setProperty name="paging.banner.placement" value="bottom" />
    <display:setProperty name="basic.msg.empty_list" value="No comments found." />
</display:table>

<br/><br/>
		<form action="${pageContext.request.contextPath}/controller" name="CommentForm" method="POST">
			<textarea name="commentBox" rows="5" cols="50" placeholder="Enter comment here"></textarea>
		    <br/><br/><input type="submit" name="addKBComment" value="Add new comment"/>
		</form> 
<br/><br/>