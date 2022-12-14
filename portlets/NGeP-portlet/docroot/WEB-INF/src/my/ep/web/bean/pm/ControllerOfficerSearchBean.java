/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Financial Organisation
 * 
 * @author venugopal
 * @since 17/3/2015
 */
public class ControllerOfficerSearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private String ctrlCode;
	private String ctrlDescription;
	private String status;

	public ControllerOfficerSearchBean() {
	}

	/**
	 * @return the ctrlCode
	 */
	public String getCtrlCode() {
		return ctrlCode;
	}

	/**
	 * @param ctrlCode
	 *            the ctrlCode to set
	 */
	public void setCtrlCode(String ctrlCode) {
		this.ctrlCode = ctrlCode;
	}

	/**
	 * @return the ctrlDescription
	 */
	public String getCtrlDescription() {
		return ctrlDescription;
	}

	/**
	 * @param ctrlDescription
	 *            the ctrlDescription to set
	 */
	public void setCtrlDescription(String ctrlDescription) {
		this.ctrlDescription = ctrlDescription;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}
