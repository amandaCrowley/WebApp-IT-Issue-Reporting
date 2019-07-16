	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
	
	<script>
	  	  /*JQuery Date pickers */
		  $(function() {
			    $( "#startDatePicker" ).datepicker({
			    	dateFormat: 'dd-mm-yy',
			    	minDate: new Date(),
			    	maxDate: '+2y',
			    	changeMonth: true,
			    	onClose: function( selectedDate ) {
			        	$( "#endDatePicker" ).datepicker( "option", "minDate", selectedDate );
			    	}
			    });
			    $( "#endDatePicker" ).datepicker({
			    	minDate: new Date(),
			    	dateFormat: 'dd-mm-yy',
			      	changeMonth: true,
			      	onClose: function( selectedDate ) {
			        	$( "#startDatePicker" ).datepicker( "option", "maxDate", selectedDate );
			        	$( "#startDatePicker" ).datepicker( "option", "minDate", new Date());
			      	}
			    });
		  });
	  </script>
	
	<h2>Currently scheduled maintenance:</h2>
	
	<c:choose>
		<c:when test="${not empty maintenanceDates}">
			<table>
				<tr><th>Start Date</th><th>End Date</th></tr>
				<c:forEach var="datePair" items="${maintenanceDates}">
					<tr><td><c:out value="${datePair.key}"/></td><td><c:out value="${datePair.value}"/></td></tr>
				</c:forEach>
			</table>
		</c:when>
		<c:otherwise>
		<p class="redText">No maintenance scheduled.</p>
		</c:otherwise>
	</c:choose>
	
	<!-- Only staff may choose to schedule maintenance for the website-->
	<c:choose>
		<c:when test="${userLoggedIn.isStaff()}">
			<h2>Schedule maintenance: </h2>
			
			<form action="${pageContext.request.contextPath}/controller" name="maintenanceForm" method="POST">
				<label for="startDatePicker">Start Date: </label>
				<input type="text" name="startDate" id="startDatePicker" /><br/><br/>
				<label for="endDatePicker">End Date: </label>
				<input type="text" name="endDate" id="endDatePicker" /><br/><br/>
				<input type="submit" value="Add maintenance" />
			</form>
			
			<c:choose>
				<c:when test="${errorMessage == 'Maintenance successfully added.'}">
					<p class="greenText">${errorMessage}</p> 
				</c:when>
				<c:otherwise>
					<p class="redText">${errorMessage}</p>
				</c:otherwise>
			</c:choose>
		</c:when>
		
		<c:otherwise>
			<h2>Pending Issues</h2>
			<c:choose>
				<c:when test="${not empty myPendingIssues}">
						<p>Please select a status for the following issue/s:</p>					
						
						<form action="${pageContext.request.contextPath}/controller" method="POST">
							<c:forEach var="issue" items="${myPendingIssues}">
								<table>
									<tr><th>Issue</th><th>Status</th></tr>
									<tr>
										<td><c:out value="${issue.title}"/></td>
										<td>
											<select name="issueStatus">
												<option value="noneSelected" selected="selected">--- Select a status ---</option>
												<c:forEach items="${statusList}" var="status">
													<option value="${status}">${status}</option>
												</c:forEach>
											</select>
											<c:if test="${not empty issue.completedWaitingDate}">
											<tr>
												<td colspan="2">You have until: <fmt:formatDate type = "date" dateStyle = "medium" value = "${issue.completedWaitingDate}" /> to update this status.</td>
											</tr>
										</c:if>
										</td>
									</tr>
								</table>
								<input type="hidden" id="issueID" name="issueID" value="${issue.issueID}">
							</c:forEach>
							<br />
							<input type="submit" value="Update Issue" />
						</form>
				</c:when>
				<c:otherwise>
				<p class="redText">No pending issues.</p>
				</c:otherwise>
			</c:choose>
		</c:otherwise>
	</c:choose>