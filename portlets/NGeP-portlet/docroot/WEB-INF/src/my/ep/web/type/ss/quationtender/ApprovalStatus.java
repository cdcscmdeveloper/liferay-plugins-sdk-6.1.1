/**
 * 
 */
package my.ep.web.type.ss.quationtender;

import my.ep.type.IEnumType;

/**
 * @author VidyasagarReddy
 * 
 */
public enum ApprovalStatus implements IEnumType {
	APPROVED(1), REVIEW(2);
	private int approvalStatus;

	ApprovalStatus(int status) {
		approvalStatus = status;
	}

	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

}
