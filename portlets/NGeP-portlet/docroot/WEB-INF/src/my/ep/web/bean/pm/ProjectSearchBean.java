/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * The Class ProjectSearchBean.
 * 
 * @author npoluri
 * @since 12/6/2015
 */
public class ProjectSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String votCode;
	private String votName;
	private String projectCode;
	private String projectDesc;
	private String status;

	public ProjectSearchBean() {
	}

	/**
	 * @return the votCode
	 */
	public String getVotCode() {
		return votCode;
	}

	/**
	 * @param votCode
	 *            the votCode to set
	 */
	public void setVotCode(String votCode) {
		this.votCode = votCode;
	}

	/**
	 * @return the votName
	 */
	public String getVotName() {
		return votName;
	}

	/**
	 * @param votName
	 *            the votName to set
	 */
	public void setVotName(String votName) {
		this.votName = votName;
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

	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @param projectCode
	 *            the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * @return the projectDesc
	 */
	public String getProjectDesc() {
		return projectDesc;
	}

	/**
	 * @param projectDesc
	 *            the projectDesc to set
	 */
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}
}
