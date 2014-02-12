package my.ep.web.managedbean.pm.userprofile;

public class PreferenceList {

	// user preferences datatables
	private String noticeMode;
	
	private String prefered;	

	private String mobileNo;	
	
	private String emailAddress;		
	
	public String getNoticeMode() {
		return noticeMode;
	}

	public void setNoticeMode(String noticeMode) {
		this.noticeMode = noticeMode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}	
	
	public String getPrefered() {
		return prefered;
	}

	public void setPrefered(String prefered) {
		this.prefered = prefered;
	}	
	
}
