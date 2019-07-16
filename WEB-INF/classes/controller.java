import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/controller")
public class controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/*
	 * This method is called when a user clicks the logout link
	 * The session is invalidated 
	 * A success message is saved to a request attribute
	 * Then the user is redirected back to the login page
	 * */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.invalidate();

		request.setAttribute("errorMessage", "Success. You have been logged out.");
		redirect(request, response,"/Login.jsp");
	}

	/*
	 * Main method in the controller. Relevant methods are called depending on the parameter sent via the POST method e.g. login, issueID etc...
	 * */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DataAccessMethods dataAccess = new DataAccessMethods();
		HttpSession userSession = request.getSession();

		try {
			if(request.getParameter("login") != null) {		//User has clicked login on login page
				login(dataAccess, userSession, request, response);
			}else if(request.getParameter("addMyIssues") != null || request.getParameter("checkboxIssueID") != null){ 
				addToMyIssues(dataAccess, userSession, request, response);
			}else if(request.getParameter("addComment") != null){ //User has clicked add comment on issueDetails page
				addComment(dataAccess, userSession, request, response);
			}else if(request.getParameter("assignedStaff") != null || request.getParameter("issueStatus") != null) { //User has clicked update issue status on issueDetails page
				updateIssue(dataAccess, userSession, request, response);
			}else if(request.getParameter("issueID") != null) {	//User has clicked view issue Main page or all issues page
				displayIssueDetails(request, response, dataAccess, userSession);
			}else if(request.getParameter("startDate")  != null || request.getParameter("endDate")  != null) { //Staff member has added maintenance dates on IT Main page
				setMaintenance(request, response, userSession, dataAccess);
			}else if(request.getParameter("submitIssue") != null){ //User has clicked add an issue on report issue page
				addIssue(dataAccess, userSession, request, response);
			}else if(request.getParameter("filterArticle") != null || request.getParameter("resetList") != null){ //User has clicked filter KB articles on allKBArticle page
				filterArticle(dataAccess, userSession, request, response);
			}else if(request.getParameter("searchArticle") != null){ //User has clicked search on KBSearch page
				searchArticle(dataAccess, userSession, request, response);
			}else if(request.getParameter("articleID") != null){ //User has clicked view article on allKBArticle page
				displayKBArticle(request, response, dataAccess, userSession);
			}else if(request.getParameter("addKBComment") != null){ //User is trying to add a comment to a Knowledge base article
				addKBComment(dataAccess, userSession, request, response);
			}else if(request.getParameter("updateIssueID") != null) {//edit issue page - clicked add to KB
				updateIssueForKbArticle(dataAccess, userSession, request, response);
			}else if(request.getParameter("categories") != null || request.getParameter("categoriesReportIssue") != null){ //User has selected a category in drop down (AllKBArticles/reportIssue)
				populateSubCategoryList(dataAccess, userSession, request, response);
			}else if(request.getParameter("viewEditIssue") != null) {//User has clicked add to KB on resolved issue page
				viewEditIssuePage(dataAccess, userSession, request, response);
			}else {// otherwise direct the to the mainpage depending on what type of user they are
				User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");
				if(userLoggedIn.isStaff()) {
					redirect(request, response,"/ITViews/ITMainPage.jsp");
				}else {
					redirect(request, response,"/UserViews/UserMainPage.jsp");
				}
			}
		}catch(java.net.ConnectException | NullPointerException e){
			request.setAttribute("errorMessage", "Database connection failed. Please try again later.");
			redirect(request, response,"/Login.jsp");
		}catch(Exception e) {
			request.setAttribute("errorMessage", e.getMessage());
			redirect(request, response,"/Login.jsp");
		}
	}

	/*
	 * User has clicked add to KB on resolved issue page
	 * Setup the request parameters needed to display the edit issue page and redirect
	 * */
	private void viewEditIssuePage(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		Issue currentIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("addToKBIssueID")));
		userSession.setAttribute("currentIssue", currentIssue);

		redirect(request, response,"/ITViews/EditIssue.jsp");
	}
	
	
	// This method edits the Issue before it is turned into a KB article
	// it can alter the Issue's, title, description, keywords, category and subcategory
	//
	private void updateIssueForKbArticle(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {	
			// ensure the Issue is valid
			String isValidIssue = checkReportParameters(request, response);
			//
			if(isValidIssue.equals("")) {
				// acquire the data that needs to be changed
				int issueID = Integer.parseInt(request.getParameter("updateIssueID"));
				String title = request.getParameter("Title");
				String description = request.getParameter("Description");
				String keywords = request.getParameter("Keywords");

				//Update issue in the database
				dataAccess.updateIssue(title, description, issueID);

				//splitting up the key words
				String[] splitWords = keywords.split(", ");
				for (String s : splitWords){
					if (s.length() > 25){
						request.setAttribute("errorMessage", "Sorry your keyword "+s+" is too long. Please ensure it is not greater than 25 characters in length.");
						redirect(request, response,"/UserViews/UserMainPage.jsp");
						break;
					}
					dataAccess.insertKeyword(issueID, s);
				}
				//update the issues KB article boolean
				convertToKBArticle(issueID, dataAccess, userSession, request, response);
				request.setAttribute("errorMessage", "Success! The Issue has been added to the knowledge base.");
				displayKBArticle(request, response, dataAccess, userSession);
			}else {
				request.setAttribute("errorMessage", isValidIssue);
				redirect(request, response,"/ITViews/EditIssue.jsp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is used to change the boolean isKBArticle variable to true.
	 * */
	private void convertToKBArticle(int issueID, DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if(issueID != 0)
			{
				dataAccess.UpdateisKBArticle(issueID);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	/*
	 * This method is used to retrieve the issue details of the selected issue from the database
	 * Then set the relevant session attributes in order to display this information on the ViewKBArticle page
	 * */
	private void displayKBArticle(HttpServletRequest request, HttpServletResponse response, DataAccessMethods dataAccess, HttpSession userSession) throws ServletException, IOException {
		try {
			Issue currentIssue = new Issue();
			if(request.getParameter("updateIssueID") != null) {
				currentIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("updateIssueID")));
			}else {
				currentIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("articleID")));
			}

			if(currentIssue != null) {
				populateDropDown(dataAccess, userSession); //Populates drop down list/s on right hand side of page
				userSession.setAttribute("currentIssue", currentIssue);

				List<Comment> currentCommentList = currentIssue.getComments();
				userSession.setAttribute("currentCommentList", currentCommentList); //To be displayed in a display:table on the page

				List<Comment> currentKBCommentList = currentIssue.getKbComments();
				userSession.setAttribute("currentKBCommentList", currentKBCommentList); //To be displayed in a display:table on the page
				
				List<Issue> allIssues = dataAccess.getAllIssues(); //Populate allIssues list				
				userSession.setAttribute("allIssues", allIssues);

				User reportedUser = dataAccess.getUser(currentIssue.getUserID());
				userSession.setAttribute("reportedUserName", reportedUser.getUserName()); //display userName of the user who reported the issue (as well as the userID)
				redirect(request, response,"/SharedViews/ViewKBArticle.jsp");
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * If an issue is not in myIssues list it calls the method to add it to the list, otherwise it is already in the myIssues list so it is remove
	 * This method is called when a checkbox is ticked/unticked on the viewIssueDetails page
	 * */
	private void addToMyIssues(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, SQLException, ServletException, IOException {
		Issue currentIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("checkboxIssueID")));
		User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");

		if(request.getParameter("addMyIssues") != null) {
			dataAccess.addMyIssuesList(currentIssue.getIssueID(), userLoggedIn.getUserID());
			request.setAttribute("errorMessage", "Success: Issue added to MyIssues");
		}else {//off = remove from list
			dataAccess.removeMyIssuesList(currentIssue.getIssueID(), userLoggedIn.getUserID());
			request.setAttribute("errorMessage", "Success: Issue removed from MyIssues");
		}
		List<Issue> myIssues = dataAccess.getUserMyIssues(userLoggedIn.getUserID()); //Populate myIssues list
		userSession.setAttribute("myIssues", myIssues);
		redirect(request, response,"/SharedViews/ViewIssueDetails.jsp");
	}

	/*
	 * Retrieve a list of KB articles from the database and filter it by the user input
	 * If the user puts in nothing it shows all KBs
	 * Update the session list and redirect back to the KBSearch page
	 * */
	private void searchArticle(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		List<Issue> kbArticles = dataAccess.getKBArticles(); //Populate knowledge base articles list
		List<Issue> searchedKbArticles = new ArrayList<Issue>();
		String search = "";

		request.setAttribute("filteredValue", search); //Set request attribute for heading

		if(!request.getParameter("searchPhrase").equals("")) {
			search = request.getParameter("searchPhrase");
			request.setAttribute("filteredValue", search); //Set request attribute for heading

			for(Issue i : kbArticles) {
				if(i.getTitle().toLowerCase().contains(search.toLowerCase())) {
					searchedKbArticles.add(i);
				}else if(i.getCategory().toLowerCase().contains(search.toLowerCase())) {
					searchedKbArticles.add(i);
				}else if(i.getSubCategory().toLowerCase().contains(search.toLowerCase())) {
					searchedKbArticles.add(i);
				}
			}

			userSession.setAttribute("kbArticlesList", searchedKbArticles);
		}else { //User entered nothing
			userSession.setAttribute("kbArticlesList", kbArticles);
		}
		redirect(request, response,"/SharedViews/KBSearch.jsp");
	}

	/*
	 * This method is used to retrieve the issue details of the selected issue from the database
	 * Then set the relevant session attributes in order to display this information on the ViewIssueDetails page
	 * */
	private void displayIssueDetails(HttpServletRequest request, HttpServletResponse response, DataAccessMethods dataAccess, HttpSession userSession) throws ServletException, IOException {
		try {
			if(request.getParameter("issueID") != null) {

				Issue clickedIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("issueID")));

				if(clickedIssue != null) {
					populateDropDown(dataAccess, userSession); //Populates drop down list/s on right hand side of page
					clickedIssue.setKeywords(dataAccess.getKeywords(clickedIssue.getIssueID())); //set keywords
					userSession.setAttribute("currentIssue", clickedIssue);

					List<Comment> currentCommentList = clickedIssue.getComments();
					userSession.setAttribute( "currentCommentList", currentCommentList); //To be displayed in a display:table on the page

					User reportedUser = dataAccess.getUser(clickedIssue.getUserID());
					userSession.setAttribute("reportedUserName", reportedUser.getUserName()); //display userName of the user who reported the issue (as well as the userID)
					redirect(request, response,"/SharedViews/ViewIssueDetails.jsp");
				}
			}
		} catch (NumberFormatException | SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is used to insert a comment into the database and update the relevant session variables needed to display info on the page.
	 * Check that the comment is not too long (less than or equal to 255 characters) as this is how much space has been reserved for this field in the database.
	 * Create the new comment object and populate it.
	 * Call the method to insert it into the database - insertComment
	 *  Update session lists.
	 * */
	private void addKBComment(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			String commentTextBox = request.getParameter("commentBox");

			if(commentTextBox.length() > 255) { //DB field for commentValue is 255 chars
				request.setAttribute("errorMessage", "Sorry your comment is too long. Please ensure it is not greater than 255 characters in length.");
			}else if(commentTextBox.length() == 0){
				request.setAttribute("errorMessage", "Please enter a value in the comment text box.");
			}else {

				User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");
				Issue currentIssue = (Issue) userSession.getAttribute("currentIssue");

				Comment c = new Comment();
				c.setUserID(userLoggedIn.getUserID());
				c.setIssueID(currentIssue.getIssueID());
				c.setCommentValue(commentTextBox);
				c.setUserName(userLoggedIn.getUserName());

				dataAccess.insertKBComment(c); //Add to comment table in DB
				List<Comment> currentKBCommentList = dataAccess.getKBComments(currentIssue.getIssueID());
				userSession.setAttribute("currentKBCommentList", currentKBCommentList); //Save the current currentKBCommentList with the new comment back to session so it displays when the page is loaded		

				request.setAttribute("errorMessage", "Success! Your comment has been added.");
			}
			redirect(request, response,"/SharedViews/ViewKBArticle.jsp");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This method is used to populate a list of subcategories for the matching category found in the "categories" request parameter
	 * The list is used to populate a drop down of subcategories
	 * Updates request attributes for page display purposes then redirect the user back to the relevant page or filter articles depending on the request parameter
	 * */
	private void populateSubCategoryList(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws SQLException, ServletException, IOException {
		String cat = "";
		if(request.getParameter("categoriesReportIssue")!= null) {
			cat = request.getParameter("categoriesReportIssue");
		}else if(request.getParameter("categories")!= null) { 
			cat = request.getParameter("categories");
		}else {
			cat = request.getParameter("categoriesEditIssue");
		}

		List<Category> categories = dataAccess.getCategories();
		List<String> subcategories = new ArrayList<String>();

		for(Category c : categories) {
			if(c.getCategory().equals(cat)) {
				subcategories = c.getSubCategories();
			}
		}
		request.setAttribute("selectedCategory", cat);
		request.setAttribute("subCategoryList", subcategories);
		userSession.setAttribute("filteredValue", "none"); //Set request attribute for filtered by message

		if(request.getParameter("categoriesReportIssue")!= null) {
			redirect(request, response,"/UserViews/ReportIssue.jsp");
		}else if(request.getParameter("categories")!=null) {
			filterArticle(dataAccess, userSession, request, response);
		}else if(request.getParameter("categoriesEditIssue")!=null){
			redirect(request, response,"/ITViews/EditIssue.jsp");
		}
	}

	/*
	 * Retrieve a list of KB articles from the database and filter it
	 * If the user has selected a category or subcategory add all issues from those cat/subcats to the list
	 * If the user has not selected either then the list will be populated will all KB articles in the database
	 * Update the session list and redirect back to the allKBArticles page
	 * */
	private void filterArticle(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		List<Issue> kbArticles = dataAccess.getKBArticles(); //Populate knowledge base articles list
		List<Issue> filteredKbArticles = new ArrayList<Issue>();
		userSession.setAttribute("kbArticlesList", kbArticles);
		String filteredBy = "none";

		if(request.getParameter("resetList") == null) {
			if(!request.getParameter("subCategories").equals("noneSelected")) {
				String subCategory = request.getParameter("subCategories");
				for(Issue i : kbArticles) {
					if(i.getSubCategory().equals(subCategory)) {
						filteredKbArticles.add(i);
					}
				}
				filteredBy = subCategory;
				userSession.setAttribute("kbArticlesList", filteredKbArticles);
			}else if(!request.getParameter("categories").equals("noneSelected")) {
				String category = request.getParameter("categories");
				for(Issue i : kbArticles) {
					if(i.getCategory().equals(category)) {
						filteredKbArticles.add(i);
					}
				}
				filteredBy = category;
				userSession.setAttribute("kbArticlesList", filteredKbArticles);
			}
		}else {//User wants to reset list back to all items
			userSession.setAttribute("kbArticlesList", kbArticles);
		}
		userSession.setAttribute("filteredValue", filteredBy); //Set request attribute for filtered by message
		redirect(request, response,"/SharedViews/AllKBArticles.jsp");
	}

	/*
	 * This method creates a new issue object, populates it with values entered into the form on the report issue page 
	 * Call method to check that valid values have been entered into the report issue form
	 * Calls the method to add these values to the issue table in the database
	 * Also call method to add user generated keyword/s for this issue in the Keyword table of the database
	 * Updates myIssues session list then redirects the user
	 * */
	private void addIssue(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {					
			String isValidIssue = checkReportParameters(request, response);

			if(isValidIssue.equals("")) {
				User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");
				String title = request.getParameter("Title");
				String description = request.getParameter("Description");
				String keywords = request.getParameter("Keywords");

				Issue i = new Issue();
				i.setUserID(userLoggedIn.getUserID());
				i.setTitle(title);
				i.setDescription(description);
				i.setReportedDateTime(new Timestamp(System.currentTimeMillis()));
				i.setStatus("New");
				i.setCategory((String) request.getParameter("categoriesReportIssue"));
				i.setSubCategory((String) request.getParameter("subCategories"));

				dataAccess.insertIssue(i);
				int issueID = dataAccess.getIssueID(i);

				//splitting up the key words
				String[] splitWords = keywords.split(", ");
				for (String s : splitWords){
					if (s.length() > 25){
						request.setAttribute("errorMessage", "Sorry your keyword "+s+" is too long. Please ensure it is not greater than 25 characters in length.");
						redirect(request, response,"/UserViews/UserMainPage.jsp");
						break;
					}
					dataAccess.insertKeyword(issueID, s);
				}

				List<Issue> myIssues = dataAccess.getUserMyIssues(userLoggedIn.getUserID()); //Update myIssues list
				userSession.setAttribute("myIssues", myIssues);

				request.setAttribute("errorMessage", "Success! Your issue has been processed.");
				redirect(request, response,"/UserViews/UserMainPage.jsp");
			}else {
				request.setAttribute("errorMessage", isValidIssue);
				redirect(request, response,"/UserViews/ReportIssue.jsp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Check that valid values have been entered into the report issue form
	 * Return either an error message as a string or empty string if all valid
	 * */
	private String checkReportParameters(HttpServletRequest request, HttpServletResponse response) {
		String errorMsg = "";
		String category = "";
		String subCategory = "";
		
		if(request.getParameter("categoriesReportIssue") != null) {
			category = request.getParameter("categoriesReportIssue").toString();
			subCategory = request.getParameter("subCategories").toString();
		}

		if(category.equals("noneSelected")) {
			errorMsg = "Please select a category.";		
		}else if(subCategory.equals("noneSelected")) {
			errorMsg = "Please select a sub category.";		
		}else if(request.getParameter("Title").equals("")) {
			errorMsg = "Please enter a title.";	
		}else if(request.getParameter("Description").equals("")) {
			errorMsg = "Please enter a description.";	
		}else if (request.getParameter("Description").length() > 255){
			errorMsg = "Sorry your description is too long. Please ensure it is not greater than 255 characters in length.";	
		}else if (request.getParameter("Title").length() > 50){
			errorMsg = "Sorry your title is too long. Please ensure it is not greater than 50 characters in length.";	
		}else{
			return errorMsg;
		}
		return errorMsg;
	}

	/*
	 * A plain text string representing a password value is passed in and hashed using MD5, it returns the hashed string. 
	 * This hashed value is used for the login process (Compare hashed user input with hashed DB value).
	 * Returns a String
	 * */
	private String hashPassword(String password) {
		String passwordToHash = password;
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5"); 
			md.update(passwordToHash.getBytes());

			byte[] bytes = md.digest();
			//This bytes[] has bytes in decimal format;
			//Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			//Get complete hashed password in hex format
			generatedPassword = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return generatedPassword;
	}

	/*
	 * This method allows IT Staff to add scheduled maintenance dates which are displayed to users of the IT services portal
	 * Check that values have been entered into both datepickers
	 * Call method checkValidDates to ensure that these dates are valid dates. If this is true the method to insert them into the DB is called (insertMaintenance).
	 * Then the session attribute maintenanceDates is also updated 
	 * */
	private void setMaintenance(HttpServletRequest request, HttpServletResponse response, HttpSession userSession, DataAccessMethods dataAccess) throws ServletException, IOException {
		String message = "";
		HashMap<String, String> enteredDates = new HashMap<String, String>();

		if(request.getParameter("startDate") == null || request.getParameter("startDate").equals("")) {
			message = "Please set a start date.";
		}else if(request.getParameter("endDate") == null || request.getParameter("endDate").equals("")) {
			message = "Please set an end date.";
		}else {
			if(checkValidDates(request.getParameter("startDate")) && checkValidDates(request.getParameter("endDate"))) {
				try {
					HashMap<String, String> maintenanceDates = dataAccess.getMaintenance();
					enteredDates.put(request.getParameter("startDate"), (request.getParameter("endDate")));
					if(enteredDates.equals(maintenanceDates)) {
						message = "Sorry. You have already set maintenance for those dates.";
					}else {
						dataAccess.insertMaintenance(request.getParameter("startDate"), request.getParameter("endDate")) ;
						userSession.setAttribute("maintenanceDates", maintenanceDates); //Update session
						message = "Maintenance successfully added.";
					}
				}catch (SQLException e) {
					request.setAttribute("errorMessage", e.getMessage());
				}
			}else {
				message = "Invalid date/s entered.";
			}
		}
		request.setAttribute("errorMessage", message);
		redirect(request, response,"/ITViews/ITMainPage.jsp");
	}

	/*
	 * Ensures that the string parameter passed into the method can be evaluated to a valid date
	 * Returns boolean
	 * */	
	private boolean checkValidDates(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
		dateFormat.setLenient(false);
		try {
			dateFormat.parse(dateString.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	/*
	 * This method sets the session attributes stafflist and statuslist which are used to populate drop down lists on the IssueStatusDropDown and IssueActions pages
	 * */
	private void populateDropDown(DataAccessMethods dataAccess, HttpSession userSession) throws SQLException {
		//Populate the IT technician list from the DB
		List<User> staffUsers = dataAccess.getStaffUsers(); 
		userSession.setAttribute("staffList", staffUsers);

		//Populate status list depending on the type of user
		User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");
		List<String> status = new ArrayList<String>();
		if(userLoggedIn.isStaff()) {
			status.add("New");
			status.add("In Progress");
			status.add("Waiting On 3rd Party");
			status.add("Waiting on Reporter");
			status.add("Completed");
		}else {
			status.add("Not Accepted");
			status.add("Resolved");
		}
		userSession.setAttribute("statusList", status);
	}

	/*
	 * Updates an issue's status or assigned IT staff member in the database.
	 * Calls data access methods to update the issue's status in the database
	 * Set errorMessage request object (Displays a success msg) and currentIssue (so the page displays the updated info once redirected)
	 * */
	private void updateIssue(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String status = request.getParameter("issueStatus").toString();		
			Issue currIssue = (Issue) userSession.getAttribute("currentIssue");
			if(userSession.getAttribute("currentIssue") == null) {
				currIssue = dataAccess.getIssue(Integer.parseInt(request.getParameter("issueID")));				
			}

			if(!status.equals("noneSelected")) {//Update Issue with a new status
				dataAccess.updateIssueStatus(status, currIssue.getIssueID());
			} 


			//Update session variables
			request.setAttribute("errorMessage", "Success! Issue has been updated.");
			currIssue = dataAccess.getIssue(currIssue.getIssueID()); 
			userSession.setAttribute("currentIssue", currIssue); 

			setSessionLists((User) userSession.getAttribute("userLoggedIn"), userSession, dataAccess);

			redirect(request, response,"/SharedViews/ViewIssueDetails.jsp");	
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is used to insert a comment into the database and update the relevant session variables needed to display info on the page.
	 * Check that the comment is not too long (less than or equal to 255 characters) as this is how much space has been reserved for this field in the database.
	 * Create the new comment object and populate it.
	 * Call the method to insert it into the database - insertComment
	 *  Update session lists.
	 * */
	private void addComment(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			String commentTextBox = request.getParameter("commentBox");

			if(commentTextBox.length() > 255) { //DB field for commentValue is 255 chars
				request.setAttribute("errorMessage", "Sorry your comment is too long. Please ensure it is not greater than 255 characters in length.");
			}else if(commentTextBox.length() == 0){
				request.setAttribute("errorMessage", "Please enter a value in the comment text box.");
			}else {
				User userLoggedIn = (User) userSession.getAttribute("userLoggedIn");
				Issue currentIssue = (Issue) userSession.getAttribute("currentIssue");

				Comment c = new Comment();
				c.setUserID(userLoggedIn.getUserID());
				c.setIssueID(currentIssue.getIssueID());
				c.setCommentValue(commentTextBox);
				c.setUserName(userLoggedIn.getUserName());

				dataAccess.insertComment(c); //Add to comment table in DB
				List<Comment> currentCommentList = dataAccess.getComments(currentIssue.getIssueID());
				userSession.setAttribute( "currentCommentList", currentCommentList); //Save the current issueList with the new comment back to session so it displays when the page is loaded		

				request.setAttribute("errorMessage", "Success! Your comment has been added.");
			}
			redirect(request, response,"/SharedViews/ViewIssueDetails.jsp");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This method is used to check that a valid user name and password has been used to login to the website
	 * Calls the method (dataAccess.login()) to check the database for a matching userName and password. 
	 * If this method returns a valid user object then the user is redirected to the relevant starting page depending on their user type.
	 * Otherwise they are redirected to the login page with an error message.
	 * */
	private void login(DataAccessMethods dataAccess, HttpSession userSession, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		try {
			String hashedPW = hashPassword(request.getParameter("password")); //Hash password
			User userLoggedIn = dataAccess.login(request.getParameter("userName"), hashedPW); //Check entered userName and hashed pw value match those stored in the User table

			if(userLoggedIn != null) {
				setSessionLists(userLoggedIn, userSession, dataAccess); //Set session lists to display appropriate page data
				checkStatusCompleteDate(userSession, dataAccess); //Check that issue status does not need to be set back to "In Progress"
				if(userLoggedIn.isStaff()) { //redirect depending on type of user
					redirect(request, response,"/ITViews/ITMainPage.jsp");
				}else {
					redirect(request, response,"/UserViews/UserMainPage.jsp");
				}
			}else {
				request.setAttribute("errorMessage", "Login details incorrect. Please try again.");
				redirect(request, response,"/Login.jsp");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Sets session lists: myIssues, pendingIssues, maintenanaceDates (only if null) 
	 * Calls populateDropDown to populate statusList and staffList
	 * */
	private void setSessionLists(User userLoggedIn, HttpSession userSession, DataAccessMethods dataAccess) {
		userSession.setAttribute("userLoggedIn", userLoggedIn);
		List<Issue> myIssues;
		try {
			myIssues = dataAccess.getUserMyIssues(userLoggedIn.getUserID()); //Populate myIssues list
			userSession.setAttribute("myIssues", myIssues);

			List<Issue> kbArticles = dataAccess.getKBArticles(); //Populate knowledge base articles list
			userSession.setAttribute("kbArticlesList", kbArticles);

			List<Category> categories = dataAccess.getCategories(); //Populate category list
			userSession.setAttribute("categoryList", categories);

			if(!userLoggedIn.isStaff()) { //If just a normal user add pending issues to session list - used in notifications.jsp include
				List<Issue> myPendingIssues = new ArrayList<Issue>();
				for(Issue issue: myIssues) {
					if(issue.getStatus().equals("Completed")) {
						myPendingIssues.add(issue);
					}
				}
				userSession.setAttribute("myPendingIssues", myPendingIssues);
			}else{
				List<Issue> allIssues = dataAccess.getAllIssues(); //Populate allIssues list				
				userSession.setAttribute("allIssues", allIssues);
			}

			if(userSession.getAttribute("maintenanceDates") == null) { //populate maintenanceDates list
				HashMap<String, String> maintenanceDates = dataAccess.getMaintenance(); //used in notifications.jsp include
				userSession.setAttribute("maintenanceDates", maintenanceDates);
			}

			if(userSession.getAttribute("statusList") == null || userSession.getAttribute("staffList") == null) {//populate statusList and staffList
				populateDropDown(dataAccess, userSession);
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Check all issues to ensure that the date set for user's to respond to a issue's status has not exceeded today's date
	 * If an issue has exceeded this date call the method to change the status back to In Progress - Staff will then respond accordingly
	 * */
	private void checkStatusCompleteDate(HttpSession userSession, DataAccessMethods dataAccess) throws SQLException {
		List<Issue> allIssues = dataAccess.getAllIssues();

		for(Issue i : allIssues) {
			if(i.getCompletedWaitingDate() != null) {
				if(i.getCompletedWaitingDate().before(new Timestamp(System.currentTimeMillis()))) {
					dataAccess.updatecompletedWaitingDate("In Progress", i.getIssueID());
				}
			}
		}
	}

	/*
	 * Custom method to redirect the user
	 * Uses RequestDispatcher forward method to forward the request object to the JSP page that the user is re-directed to
	 * */
	private void redirect(HttpServletRequest request, HttpServletResponse response, String pageName) throws ServletException, IOException {
		RequestDispatcher rd = getServletContext().getRequestDispatcher(pageName);
		rd.forward(request, response);
	}
}
