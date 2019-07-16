public class User {
	private int userID;
	private String userName;
	private String password;
	private String firstName;
	private String surname;
	private String email;
	private String phoneNum;
	private boolean isStaff;

	public User() {}

	public User(int id, String name, String pass, String fName, String lName, String emailAddress, String phone, boolean staff) {
		this.userID = id;
		this.userName = name;
		this.password = pass;
		this.firstName = fName;
		this.surname = lName;
		this.email = emailAddress;
		this.phoneNum = phone;
		this.isStaff = staff;
	}

	public int getUserID() {
		return userID;
	}
	public void setUserId(int id) {
		this.userID = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String name) {
		this.userName = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String pass) {
		this.password = pass;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String fName) {
		this.firstName = fName;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String lName) {
		this.surname = lName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String emailAddress) {
		this.email = emailAddress;
	}
	public String getphoneNum() {
		return phoneNum;
	}
	public void setphoneNum(String phone) {
		this.phoneNum = phone;
	}
	public boolean isStaff() {
		return isStaff;
	}
	public void setisStaff(boolean staff) {
		this.isStaff = staff;
	}
}
