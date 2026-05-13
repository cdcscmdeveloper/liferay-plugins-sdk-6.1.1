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
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;

/**
 * The Class UserCommitteeSearchBean.
 *
 * @author Viswanath
 */
public class UserCommitteeSearchBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The name. */
	private String name;
	
	/** The identification no. */
	private String identificationNo;
	
	/** The identification type. */
	private String identificationType;

	/**
	 * Instantiates a new user committee search bean.
	 */
	public UserCommitteeSearchBean() {
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the identification no.
	 *
	 * @return the identification no
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * Sets the identification no.
	 *
	 * @param identificationNo the new identification no
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	/**
	 * Gets the identification type.
	 *
	 * @return the identification type
	 */
	public String getIdentificationType() {
		return identificationType;
	}

	/**
	 * Sets the identification type.
	 *
	 * @param identificationType the new identification type
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

}
