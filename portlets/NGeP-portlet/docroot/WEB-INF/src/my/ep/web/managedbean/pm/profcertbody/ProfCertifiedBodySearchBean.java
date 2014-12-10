package my.ep.web.managedbean.pm.profcertbody;

public class ProfCertifiedBodySearchBean {
	/**
	 * Search Bean 	
	 */
	private String bodyCode;
	private String bodyName;
	private Long status;
	private Integer certType;

	public ProfCertifiedBodySearchBean() {
	}

	public String getBodyCode() {
		return bodyCode;
	}

	public void setBodyCode(String bodyCode) {
		this.bodyCode = bodyCode;
	}

	public String getBodyName() {
		return bodyName;
	}

	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Integer getCertType() {
		return certType;
	}

	public void setCertType(Integer certType) {
		this.certType = certType;
	}
}