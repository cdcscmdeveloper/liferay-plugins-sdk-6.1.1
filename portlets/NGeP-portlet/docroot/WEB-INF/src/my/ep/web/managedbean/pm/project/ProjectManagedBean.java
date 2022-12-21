/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.project;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.bean.pm.ProjectSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Project
 * 
 * @author npoluri
 * @since 12/6/2015
 * 
 */
@ManagedBean(name = "projectManagedBean")
@SessionScoped
public class ProjectManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private ProjectSearchBean projectSearchBean;	
	
	private PmVotFund pmVotFund;

	private String votCode;

	private String votName;
	
	private String projectCode;
	
	private String projectDesc;

	private Long status;

	private boolean isLazy;

		

	/**
	 * @return the projectSearchBean
	 */
	public ProjectSearchBean getProjectSearchBean() {
		return projectSearchBean;
	}

	/**
	 * @param projectSearchBean the projectSearchBean to set
	 */
	public void setProjectSearchBean(ProjectSearchBean projectSearchBean) {
		this.projectSearchBean = projectSearchBean;
	}

	/**
	 * @return the pmVotFund
	 */
	public PmVotFund getPmVotFund() {
		return pmVotFund;
	}

	/**
	 * @param pmVotFund the pmVotFund to set
	 */
	public void setPmVotFund(PmVotFund pmVotFund) {
		this.pmVotFund = pmVotFund;
	}

	/**
	 * @return the votCode
	 */
	public String getVotCode() {
		return votCode;
	}

	/**
	 * @param votCode the votCode to set
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
	 * @param votName the votName to set
	 */
	public void setVotName(String votName) {
		this.votName = votName;
	}

	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}

	/**
	 * @param projectCode the projectCode to set
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
	 * @param projectDesc the projectDesc to set
	 */
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}
	
}