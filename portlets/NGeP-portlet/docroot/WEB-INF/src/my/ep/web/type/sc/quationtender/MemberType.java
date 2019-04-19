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
 * Enum class for MemberType.
 * 
 * @author VidyaSagarReddy
 * @since 22.11.2013
 * 
 */
public enum MemberType implements IEnumType {
	
	/** The Government user. */
	GovernmentUser(true),
	
	/** The Non government user. */
	NonGovernmentUser(false);
	
	
	/** The memeber type. */
	private boolean memeberType;

	/**
	 * Instantiates a new member type.
	 *
	 * @param type the type
	 */
	private MemberType(boolean type) {
		this.memeberType=type;
	}
	/* (non-Javadoc)
	 * @see my.ep.type.IEnumType#value()
	 */
	@Override
	public Object value() {
		// TODO Auto-generated method stub
		return memeberType;
	}
	
	/**
	 * Gets the memeber type.
	 *
	 * @return the memeber type
	 */
	public boolean getMemeberType() {
		return memeberType;
	}
	
	/**
	 * Sets the memeber type.
	 *
	 * @param memeberType the new memeber type
	 */
	public void setMemeberType(boolean memeberType) {
		this.memeberType = memeberType;
	}

}
