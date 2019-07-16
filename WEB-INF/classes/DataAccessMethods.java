import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class DataAccessMethods {
	/* Retrieves all users who are staff members
	 * Return a list of user objects
	 */
	public List<User> getStaffUsers() throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM User WHERE isStaff=1");
			rs = ps.executeQuery();

			List<User> userList = new ArrayList<User>();
			while(rs.next())
			{
				User user = extractUserFromResultSet(rs);
				if(user.isStaff()) {
					userList.add(user);
				}
			}
			return userList;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve the User with the matching userID stored in the database
	 * Calls method extractUserFromResultSet to populate the User object from the result set
	 * Returns an User object
	 * */
	public User getUser(int id) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM User WHERE userID=?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if(rs.next())
			{
				return extractUserFromResultSet(rs);
			}


		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve the User with the matching userName and password stored in the database
	 * Calls method extractUserFromResultSet to populate the User object from the result set
	 * This method is used to authenticate a user using values entered in the login form and the stored userName/pw values in the database
	 * Returns an User object
	 * */
	public User login(String userName, String pw) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM User WHERE userName=? AND password=?");
			ps.setString(1, userName);
			ps.setString(2, pw);
			rs = ps.executeQuery();
			if(rs.next())
			{
				return extractUserFromResultSet(rs);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve all issues from the SubscribedUserIssue table where the UserID matches the userID parameter
	 * To populate the list the getIssue method is called to retrieve the issue object for each issueID found in the result set
	 * This list of issues is displayed on the user's home page
	 * Returns a list of issue objects
	 * */
	public List<Issue> getUserMyIssues(int userID) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT issueID FROM SubscribedUserIssue WHERE UserID=?");
			ps.setInt(1, userID);
			rs = ps.executeQuery();

			List<Issue> issueList = new ArrayList<Issue>();
			while(rs.next())
			{
				Issue issue = getIssue(rs.getInt("issueID"));
				issueList.add(issue);
			}
			return issueList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve all issues from the Issue table
	 * Used on AllIssues page*/
	public List<Issue> getAllIssues() throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM Issue");

			rs = ps.executeQuery();

			List<Issue> issueList = new ArrayList<Issue>();
			while(rs.next())
			{
				Issue issue = extractIssueFromResultSet(rs);
				issueList.add(issue);
			}
			return issueList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve all knowledge base articles from the Issue table in the database
	 * Issues that have been added to the knowledge base have the field isKBArticle populated with a 1 otherwise it is a 0
	 * This list of articles is displayed on the knowledge base articles page
	 * Returns a list of issue objects
	 * */
	public List<Issue> getKBArticles() throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM Issue WHERE isKBArticle=1");
			rs = ps.executeQuery();

			List<Issue> kbIssueList = new ArrayList<Issue>();
			while(rs.next())
			{
				Issue issue = extractIssueFromResultSet(rs);
				kbIssueList.add(issue);
			}
			return kbIssueList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve the issue with the matching issueID stored in the database
	 * Calls method extractIssueFromResultSet to populate the issue object from the result set
	 * Returns an Issue object
	 * */
	public Issue getIssue(int issueID) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM Issue WHERE issueID=?");
			ps.setInt(1, issueID);
			rs = ps.executeQuery();

			while(rs.next())
			{
				return extractIssueFromResultSet(rs);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Updates the status field (Issue table) with the value stored in status parameter
	 * If the status is waiting on report insert the date that the reporter must respond to the status by (7 days from now)
	 * The issue that is updated must match the issueID parameter
	 * */
	public void updateIssueStatus(String status, int issueID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			if(status.equals("Waiting on Reporter")) {
				//The reporter has 7 days to respond to the status update
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(timestamp.getTime());

				//Add 7 days
				cal.setTimeInMillis(timestamp.getTime());
				cal.add(Calendar.DAY_OF_MONTH, 7);

				ps = connection.prepareStatement("UPDATE Issue Set status =?, completedWaitingDate=? WHERE issueID=?");
				ps.setString(1, status);
				ps.setTimestamp(2, timestamp = new Timestamp(cal.getTime().getTime()));
				ps.setInt(3, issueID);
			}else {
				ps = connection.prepareStatement("UPDATE Issue Set status =?, completedWaitingDate=? WHERE issueID=?");
				ps.setString(1, status);
				ps.setTimestamp(2, null); //clear this date for other status updates
				ps.setInt(3, issueID);
			}

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}


	/*
	 * Update the Issues title and description before it is added to the knowledge base
	 * The issue that is updated must match the issueID parameter
	 * */
	public void updateIssue(String title, String description, int issueID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;
		//PreparedStatement removeKeywords = null;

		try {
			connection = DBConnection.getConnection();
			deleteKeywords(issueID);

			ps = connection.prepareStatement("UPDATE Issue Set title=?, description=? WHERE issueID=?");
			ps.setString(1, title);
			ps.setString(2, description);
			ps.setInt(3, issueID);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	public void deleteKeywords(int issueID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("DELETE FROM Keyword WHERE issueID=?");
			ps.setInt(1, issueID);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Updates the status field (Issue table) with the value stored in status parameter
	 * If the status is waiting on report insert the date that the reporter must respond to the status by (7 days from now)
	 * The issue that is updated must match the issueID parameter
	 * */
	public void updatecompletedWaitingDate(String status, int issueID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("UPDATE Issue Set status =?, completedWaitingDate=? WHERE issueID=?");
			ps.setString(1, status);
			ps.setTimestamp(2, null); //clear this date
			ps.setInt(3, issueID);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Retrieve comments (matching an issueID) stored in the Comment table in the database
	 * Returns them as a list of type Comment
	 * */
	public List<Comment> getComments(int issueID) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM Comment WHERE issueID=?");
			ps.setInt(1, issueID);
			rs = ps.executeQuery();

			List<Comment> commentsList = new ArrayList<Comment>();
			while(rs.next())
			{
				Comment comm = new Comment();
				comm.setCommentID(rs.getInt("commentID"));
				comm.setIssueID(rs.getInt("issueID"));
				comm.setUserID(rs.getInt("userID"));
				comm.setUserName(rs.getString("userName"));
				comm.setCommentValue(rs.getString("commentValue"));

				commentsList.add(comm);
			}
			return commentsList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve keywords (matching an issueID) stored in the Keyword table in the database
	 * Returns them as a list of strings
	 * */
	public List<String> getKeywords(int issueID) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT keyword FROM Keyword WHERE issueID=?");
			ps.setInt(1, issueID);
			rs = ps.executeQuery();

			List<String> keywordList = new ArrayList<String>();
			while(rs.next())
			{
				keywordList.add(rs.getString("keyword"));
			}
			return keywordList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Insert comments made by staff or users into the database
	 * These comments are pertaining to issues which have been reported by a user
	 * */
	public void insertComment(Comment comment) throws SQLException{
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO Comment (issueID, UserID, commentValue, userName) VALUES (?,?,?,?)");
			ps.setInt(1, comment.getIssueID());
			ps.setInt(2, comment.getUserID());
			ps.setString(3, comment.getCommentValue());
			ps.setString(4, comment.getUserName());

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Retrieve the issue with the matching issue parameters stored in the database
	 * Calls method extractIssueFromResultSet to populate the issue object from the result set
	 * Returns an int (Either issue id or -1 for not found)
	 * */
	public int getIssueID(Issue issue) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT IssueID FROM Issue WHERE title=? AND description=? AND reportedDateTime=? AND UserID=? AND status=? AND category=? AND subcategory=?");
			ps.setString(1, issue.getTitle());
			ps.setString(2, issue.getDescription());
			ps.setTimestamp(3, issue.getReportedDateTime());
			ps.setInt(4, issue.getUserID());
			ps.setString(5, issue.getStatus());
			ps.setString(6, issue.getCategory());
			ps.setString(7, issue.getSubCategory());
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("IssueID");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return -1;
	}

	/*
	 * Insert Keyword
	 * issues add these when they are created
	 * */
	public void insertKeyword(int issueID, String keyword) throws SQLException{
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO Keyword (issueID, keyword) VALUES (?,?)");
			ps.setInt(1, issueID);
			ps.setString(2, keyword);

			// execute insert SQL statement
			ps.executeUpdate();

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Insert comments made by staff or users into the database
	 * These comments are pertaining to issues which have been reported by a user
	 * */
	public void insertKBComment(Comment comment) throws SQLException{
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO KBComment (issueID, UserID, commentValue, userName) VALUES (?,?,?,?)");
			ps.setInt(1, comment.getIssueID());
			ps.setInt(2, comment.getUserID());
			ps.setString(3, comment.getCommentValue());
			ps.setString(4, comment.getUserName());

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/* Insert a new issue into the database
	 * New issues are reported by users to be added to the database
	 * */
	public void insertIssue(Issue issue) throws SQLException{
		Connection connection = null;
		PreparedStatement ps = null;

		try{
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO Issue (title, description, reportedDateTime, UserID, status, category, subcategory) VALUES (?,?,?,?,?,?,?)");
			ps.setString(1, issue.getTitle());
			ps.setString(2, issue.getDescription());
			ps.setTimestamp(3, issue.getReportedDateTime());
			ps.setInt(4, issue.getUserID());
			ps.setString(5, issue.getStatus());
			ps.setString(6, issue.getCategory());
			ps.setString(7, issue.getSubCategory());
			ps.executeUpdate();

			addMyIssuesList(getIssueID(issue), issue.getUserID()); //add to my issues list
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Insert maintenance date pair (Start date and end date) into the database
	 * Staff add these maintenance dates
	 * */
	public void insertMaintenance(String startDate, String endDate) throws SQLException{
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO Maintenance (startDate, endDate) VALUES (?,?)");
			ps.setString(1, startDate);
			ps.setString(2, endDate);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}


	/*
	 * Retrieve all maintenance dates stored in the database
	 * All dates are stored as a pair (Start and end date)
	 * Returns a Hashmap of the pair of dates
	 * */
	public HashMap<String, String> getMaintenance() throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM Maintenance");
			rs = ps.executeQuery();

			HashMap<String, String> maintenanceDates = new HashMap<String, String>();
			while(rs.next())
			{
				String startDate = rs.getString("startDate");
				String endDate = rs.getString("endDate");
				maintenanceDates.put(startDate, endDate);
			}
			return maintenanceDates;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve a list of categories from the Category table in the DB
	 * Call method to retrieve all sub categories for each category
	 * Returns a list of category objects
	 * */
	public List<Category> getCategories() throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT categoryName FROM Category");
			rs = ps.executeQuery();

			List<Category> categoryList = new ArrayList<Category>();
			while(rs.next())
			{
				Category c = new Category();
				c.setCategory(rs.getString("categoryName"));

				List<String> subCats = getSubCategories(c.getCategory());
				c.setSubCategories(subCats);
				categoryList.add(c);
			}
			return categoryList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Retrieve all sub categories with a matching parentCategory value
	 * Return a list of strings
	 * */
	public List<String> getSubCategories(String parentCategory) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT subCatName FROM SubCategory WHERE parentCategory=?");
			ps.setString(1, parentCategory);
			rs = ps.executeQuery();

			List<String> subCategoryList = new ArrayList<String>();
			while(rs.next())
			{
				subCategoryList.add(rs.getString("subCatName"));
			}
			return subCategoryList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}

	/*
	 * Updates an issue's boolean isKBArticle to be true
	 * */
	public void UpdateisKBArticle(int IssueId) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("UPDATE Issue Set isKBArticle=1 WHERE issueID=?");
			ps.setInt(1, IssueId);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	/*
	 * Retrieve comments (matching an issueID) stored in the Comment table in the database
	 * Returns them as a list of type Comment
	 * */
	public List<Comment> getKBComments(int issueID) throws SQLException{
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("SELECT * FROM KBComment WHERE issueID=?");
			ps.setInt(1, issueID);
			rs = ps.executeQuery();

			List<Comment> commentsList = new ArrayList<Comment>();
			while(rs.next())
			{
				Comment comm = new Comment();
				comm.setCommentID(rs.getInt("commentID"));
				comm.setIssueID(rs.getInt("issueID"));
				comm.setUserID(rs.getInt("userID"));
				comm.setUserName(rs.getString("userName"));
				comm.setCommentValue(rs.getString("commentValue"));

				commentsList.add(comm);
			}
			return commentsList;

		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, rs, ps); //Release DB resources
		}
		return null;
	}


	public void addMyIssuesList(int issueID, int userID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("INSERT INTO SubscribedUserIssue (issueID, userID) VALUES (?,?)");
			ps.setInt(1, issueID);
			ps.setInt(2, userID);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}

	public void removeMyIssuesList(int issueID, int userID) throws SQLException {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = DBConnection.getConnection();
			ps = connection.prepareStatement("DELETE FROM SubscribedUserIssue WHERE issueID=? AND userID=?;");
			ps.setInt(1, issueID);
			ps.setInt(2, userID);

			// execute insert SQL statement
			ps.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}finally {
			closeConnections(connection, null, ps); //Release DB resources
		}
	}


	/*Close necessary database connections*/
	private void closeConnections(Connection connection, ResultSet rs, PreparedStatement ps) throws SQLException{
		if (ps != null) {
			ps.close();
		}

		if(rs != null) {
			ps.close();
		}

		if (connection != null) {
			connection.close();
		}
	}

	/*
	 * Create a new User object from the result set passed into the method and return the populated user
	 * */
	private User extractUserFromResultSet(ResultSet rs) throws SQLException {
		User user = new User();
		user.setUserId(rs.getInt("userID"));
		user.setUserName(rs.getString("userName"));
		user.setPassword(rs.getString("password"));
		user.setFirstName(rs.getString("firstName"));
		user.setSurname(rs.getString("surname"));
		user.setEmail(rs.getString("email"));
		user.setphoneNum(rs.getString("phoneNum"));
		user.setisStaff(rs.getBoolean("isStaff"));
		return user;
	}

	/*
	 * Create a new Issue object from the result set passed into the method and return the populated issue
	 * */
	private Issue extractIssueFromResultSet(ResultSet rs) throws SQLException {
		Issue issue = new Issue();
		issue.setIssueID(rs.getInt("issueID"));
		issue.setTitle(rs.getString("title"));
		issue.setDescription(rs.getString("description"));
		issue.setResolutionDetails(rs.getString("resolutionDetails"));
		issue.setReportedDateTime(rs.getTimestamp("reportedDateTime"));
		issue.setResolvedDateTime(rs.getTimestamp("resolvedDateTime"));
		issue.setCompletedWaitingDate(rs.getTimestamp("completedWaitingDate"));	
		issue.setStatus(rs.getString("status"));
		issue.setUserID(rs.getInt("userID"));
		issue.setCategory(rs.getString("category"));
		issue.setSubCategory(rs.getString("subCategory"));
		issue.setComments(getComments(issue.getIssueID()));
		issue.setKeywords(getKeywords(issue.getIssueID()));
		issue.setKBArticle(rs.getBoolean("isKBArticle"));
		issue.setKbComments(getKBComments(issue.getIssueID()));

		return issue;
	}

}
