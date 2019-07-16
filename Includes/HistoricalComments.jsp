<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<h2>
	Historical comments:
</h2>
<display:table name="sessionScope.currentCommentList" id="histCommentTable" keepStatus="true" class="displayTagTable" pagesize="10"> <!-- Name must match the name of the session param that you set the list you'd like to display e.g. session.setAttribute( "currentCommentList", currentCommentList); -->
<display:column property="commentValue" title="Comment"/>
	
<!-- Table properties also located in WEB-INF/displaytag.properties file-->
<display:setProperty name="paging.banner.placement" value="bottom" />
<display:setProperty name="basic.msg.empty_list" value="No comments found." />
</display:table>

