package my.ep.web.bean.sm;

public class MofRegistrationUtilBean {

	private int seqNo;
	private String applicationName;

	public MofRegistrationUtilBean() {
	}

	public MofRegistrationUtilBean(int seqNo, String applicationName) {
		this.seqNo = seqNo;
		this.applicationName = applicationName;

	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

}
