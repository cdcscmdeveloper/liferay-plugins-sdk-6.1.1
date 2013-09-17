package my.ep.web.bean.ct;

import java.io.Serializable;


public class AgreementDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     private String agrementTitle;
     private String agrementType;
     private String appendexType;
     
     public String getAppendexType() {
		return appendexType;
	}
	public void setAppendexType(String appendexType) {
		this.appendexType = appendexType;
	}
	public String getAgrementTitle() {
		return agrementTitle;
	}
	public void setAgrementTitle(String agrementTitle) {
		this.agrementTitle = agrementTitle;
	}
	public String getAgrementType() {
		return agrementType;
	}
	public void setAgrementType(String agrementType) {
		this.agrementType = agrementType;
	}
	
}
