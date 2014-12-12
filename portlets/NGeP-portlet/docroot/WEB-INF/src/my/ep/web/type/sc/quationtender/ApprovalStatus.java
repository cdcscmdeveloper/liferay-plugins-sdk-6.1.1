/**
 * 
 */
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 * 
 */
public enum ApprovalStatus implements IEnumType {
	APPROVE("A"), REVISE("V"), REJECT("R");
	
	private String approvalStatus;

	ApprovalStatus(String status) {
		approvalStatus = status;
	}

	@Override
	public String value() {
		// TODO Auto-generated method stub
		return approvalStatus;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}


	
}
