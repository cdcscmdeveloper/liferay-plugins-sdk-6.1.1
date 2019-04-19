package my.ep.web.bean.sm;

public class CompanyBranchesBean {

	private int branchSeq;
	private String branchName;
	private String branchAddress;

	public CompanyBranchesBean() {
	}

	public CompanyBranchesBean(int branchSeq, String branchName,
			String branchAddress) {
		this.branchSeq = branchSeq;
		this.branchName = branchName;
		this.branchAddress = branchAddress;
	}

	public int getBranchSeq() {
		return branchSeq;
	}

	public void setBranchSeq(int branchSeq) {
		this.branchSeq = branchSeq;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBranchAddress() {
		return branchAddress;
	}

	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}

}
