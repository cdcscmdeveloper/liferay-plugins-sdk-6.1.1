package my.ep.web.integration.eperunding;

import java.util.Date;

public class ProfBodyDetail {
	private String profBody;
	private String profBodyNo;
	private Date profBodyExpiry;

	public String getProfBody() {
		return profBody;
	}

	public void setProfBody(String profBody) {
		this.profBody = profBody;
	}

	public String getProfBodyNo() {
		return profBodyNo;
	}

	public void setProfBodyNo(String profBodyNo) {
		this.profBodyNo = profBodyNo;
	}

	public Date getProfBodyExpiry() {
		return profBodyExpiry;
	}

	public void setProfBodyExpiry(Date profBodyExpiry) {
		this.profBodyExpiry = profBodyExpiry;
	}

}
