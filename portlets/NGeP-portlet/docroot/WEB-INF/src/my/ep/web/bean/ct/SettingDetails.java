package my.ep.web.bean.ct;

import java.io.Serializable;

public class SettingDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String aproverName;
	private String approverPosition;
	private int approverId;
	
	public SettingDetails(){
		
	}
	
	public SettingDetails(String aproverName, int approverId ){
		this.approverId=approverId;
		this.aproverName= aproverName;
	}
	
	public int getApproverId() {
		return approverId;
	}
	public void setApproverId(int approverId) {
		this.approverId = approverId;
	}
	public String getAproverName() {
		return aproverName;
	}
	public void setAproverName(String aproverName) {
		this.aproverName = aproverName;
	}
	public String getApproverPosition() {
		return approverPosition;
	}
	public void setApproverPosition(String approverPosition) {
		this.approverPosition = approverPosition;
	}

}
