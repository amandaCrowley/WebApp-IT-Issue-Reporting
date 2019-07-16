import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class Issue {
	private int issueID;
	private String title;
	private String description;
	private String resolutionDetails;
	private Timestamp reportedDateTime;
	private Timestamp resolvedDateTime;
	private Timestamp completedWaitingDate;
	private int userID;
	private String status;
	private List<Comment> comments;
	private List<Comment> kbComments;
	private List<String> keywords;
	private String category;
	private String subCategory;
	private boolean isKBArticle;
	
	public int getIssueID() {
		return issueID;
	}
	public void setIssueID(int id) {
		this.issueID = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String s) {
		this.title = s;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String desc) {
		this.description = desc;
	}
	public String getResolutionDetails() {
		return resolutionDetails;
	}
	public void setResolutionDetails(String details) {
		this.resolutionDetails = details;
	}
	public Timestamp getReportedDateTime() {
		return reportedDateTime;
	}
	public void setReportedDateTime(Timestamp reported) {
		this.reportedDateTime = reported;
	}
	public Timestamp getResolvedDateTime() {
		return resolvedDateTime;
	}
	public void setResolvedDateTime(Timestamp resolved) {
		this.resolvedDateTime = resolved;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int id) {
		userID = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String stat) {
		this.status = stat;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> s) {
		this.comments = s;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> words) {
		this.keywords = words;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String cat) {
		this.category = cat;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String sub) {
		this.subCategory = sub;
	}
	public boolean isKBArticle() {
		return isKBArticle;
	}
	public void setKBArticle(boolean isKBArticle) {
		this.isKBArticle = isKBArticle;
	}
	public List<Comment> getKbComments() {
		return kbComments;
	}
	public void setKbComments(List<Comment> kbComments) {
		this.kbComments = kbComments;
	}
	public Timestamp getCompletedWaitingDate() {
		return completedWaitingDate;
	}
	public void setCompletedWaitingDate(Timestamp completedWaitingDate) {
		this.completedWaitingDate = completedWaitingDate;
	}
	
	/*
	 * If the issue is in the myIssues list this method returns true
	 * Used for checkbox on viewIssueDetails page
	 * */
	public boolean isInMyIssues(int issueID, User userLoggedIn) throws SQLException {
		boolean foundIssue = false;
		DataAccessMethods dataAccess = new DataAccessMethods();
		
		List<Issue> myIssues = dataAccess.getUserMyIssues(userLoggedIn.getUserID());
		for(Issue i : myIssues) {
			if(i.getIssueID() == issueID) {
				foundIssue = true;
			}
		}
		return foundIssue;
	}
}
