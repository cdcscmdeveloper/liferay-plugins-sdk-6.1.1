/* 
 ***************************************************
 *    
 * Copyright © 2014 Archisoft. All rights reserved.
 * 
 ***************************************************
*/
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 6/8/2014
 *
 */
public class AddressList implements Serializable{

	private static final long serialVersionUID = 1L;
	private String addressName;
	private String addressStatus;
	
	public AddressList(){}

	/**
	 * @return the addressName
	 */
	public String getAddressName() {
		return addressName;
	}

	/**
	 * @param addressName the addressName to set
	 */
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	/**
	 * @return the addressStatus
	 */
	public String getAddressStatus() {
		return addressStatus;
	}

	/**
	 * @param addressStatus the addressStatus to set
	 */
	public void setAddressStatus(String addressStatus) {
		this.addressStatus = addressStatus;
	}
}
