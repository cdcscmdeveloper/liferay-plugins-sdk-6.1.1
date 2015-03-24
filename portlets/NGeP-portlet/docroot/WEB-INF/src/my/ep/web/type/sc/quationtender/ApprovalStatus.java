/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.type.sc.quationtender;

import my.ep.type.IEnumType;

// TODO: Auto-generated Javadoc
/**
 * 
 * Enum class for ApprovalStatus.
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public enum ApprovalStatus implements IEnumType {
	
	/** The approve. */
	APPROVE("A"), 
 /** The revise. */
 REVISE("V"), 
 /** The reject. */
 REJECT("R");
	
	/** The approval status. */
	private String approvalStatus;

	/**
	 * Instantiates a new approval status.
	 *
	 * @param status the status
	 */
	ApprovalStatus(String status) {
		approvalStatus = status;
	}

	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public String value() {
		// TODO Auto-generated method stub
		return approvalStatus;
	}

	/**
	 * Gets the approval status.
	 *
	 * @return the approval status
	 */
	public String getApprovalStatus() {
		return approvalStatus;
	}

	/**
	 * Sets the approval status.
	 *
	 * @param approvalStatus the new approval status
	 */
	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

}
