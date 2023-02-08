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

	// #12471 Start
	/** The organization category. */
	private String orgCat;

	/** The ministry. */
	private String ministry;

	/** The PTJ Group. */
	private String ptjGroup;

	/** The PTJ. */
	private String ptj;

	/** The organization type. */
	private String orgType;

	/** The organization name. */
	private String orgName;
	// #12471 End
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

	// #12471 Start
	/**
	 * @return the organization category
	 */
	public String getOrgCat() {
		return orgCat;
	}

	/**
	 * @param orgCat
	 *            the organization category to set
	 */
	public void setOrgCat(String orgCat) {
		this.orgCat = orgCat;
	}

	/**
	 * @return the ministry
	 */
	public String getMinistry() {
		return ministry;
	}

	/**
	 * @param ministry
	 *            the ministry to set
	 */
	public void setMinistry(String ministry) {
		this.ministry = ministry;
	}

	/**
	 * @return the PTJ Group
	 */
	public String getPtjGroup() {
		return ptjGroup;
	}

	/**
	 * @param ptjGroup
	 *            the PTJ Group to set
	 */
	public void setPtjGroup(String ptjGroup) {
		this.ptjGroup = ptjGroup;
	}

	/**
	 * @return the PTJ
	 */
	public String getPtj() {
		return ptj;
	}

	/**
	 * @param ptj
	 *            the PTJ to set
	 */
	public void setPtj(String ptj) {
		this.ptj = ptj;
	}

	/**
	 * @return the organization type
	 */
	public String getOrgType() {
		return orgType;
	}

	/**
	 * @param orgType
	 *            the organization type to set
	 */
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	/**
	 * @return the organization name
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName
	 *            the organization name to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	// #12471 End

}
