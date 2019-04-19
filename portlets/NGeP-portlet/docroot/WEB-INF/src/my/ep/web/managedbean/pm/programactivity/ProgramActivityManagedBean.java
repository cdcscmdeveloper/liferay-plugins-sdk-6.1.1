/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.programactivity;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.bean.pm.ProgramSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Program Activity
 * 
 * @author npoluri
 * @since 12/6/2015
 * 
 */
@ManagedBean(name = "programActivityManagedBean")
@SessionScoped
public class ProgramActivityManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private ProgramSearchBean programSearchBean;
	private PmVotFund pmVotFund;
	private String votCode;
	private String votName;
	private String programActivityCode;
	private String programActivityDesc;
	private Long status;
	private boolean isLazy;

	public ProgramActivityManagedBean() {
	}

	/**
	 * @return the programSearchBean
	 */
	public ProgramSearchBean getProgramSearchBean() {
		return programSearchBean;
	}

	/**
	 * @param programSearchBean
	 *            the programSearchBean to set
	 */
	public void setProgramSearchBean(ProgramSearchBean programSearchBean) {
		this.programSearchBean = programSearchBean;
	}

	/**
	 * @return the programActivityCode
	 */
	public String getProgramActivityCode() {
		return programActivityCode;
	}

	/**
	 * @param programActivityCode
	 *            the programActivityCode to set
	 */
	public void setProgramActivityCode(String programActivityCode) {
		this.programActivityCode = programActivityCode;
	}

	/**
	 * @return the programActivityDesc
	 */
	public String getProgramActivityDesc() {
		return programActivityDesc;
	}

	/**
	 * @param programActivityDesc
	 *            the programActivityDesc to set
	 */
	public void setProgramActivityDesc(String programActivityDesc) {
		this.programActivityDesc = programActivityDesc;
	}

	/**
	 * @return the pmVotFund
	 */
	public PmVotFund getPmVotFund() {
		return pmVotFund;
	}

	/**
	 * @param pmVotFund
	 *            the pmVotFund to set
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
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
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
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

}