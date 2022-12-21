package my.ep.web.bean.sm;

public class CompanyHeadOfficeBean {

	private int headOffSeq;
	private String headOffAddress;

	public CompanyHeadOfficeBean() {
	}

	public CompanyHeadOfficeBean(int headOffSeq, String headOffAddress) {
		this.headOffSeq = headOffSeq;
		this.headOffAddress = headOffAddress;
	}

	public int getHeadOffSeq() {
		return headOffSeq;
	}

	public void setHeadOffSeq(int headOffSeq) {
		this.headOffSeq = headOffSeq;
	}

	public String getHeadOffAddress() {
		return headOffAddress;
	}

	public void setHeadOffAddress(String headOffAddress) {
		this.headOffAddress = headOffAddress;
	}

}
