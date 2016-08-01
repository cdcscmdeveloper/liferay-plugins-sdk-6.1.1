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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String addressName;

	private String addressStatus;
	
	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getAddressStatus() {
		return addressStatus;
	}

	public void setAddressStatus(String addressStatus) {
		this.addressStatus = addressStatus;
	}	
	
}
