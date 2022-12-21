package my.ep.web.bean.ct;

import java.io.Serializable;

public class CustomerServiceEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String officerName;
	private String documentStatusDesc;
	public String getOfficerName() {
		return officerName;
	}
	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}
	public String getDocumentStatusDesc() {
		return documentStatusDesc;
	}
	public void setDocumentStatusDesc(String documentStatusDesc) {
		this.documentStatusDesc = documentStatusDesc;
	}
}
