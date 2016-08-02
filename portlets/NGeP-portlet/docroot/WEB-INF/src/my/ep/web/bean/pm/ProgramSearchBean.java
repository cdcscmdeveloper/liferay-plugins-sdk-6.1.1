/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Program
 * 
 * @author npoluri
 * @since 12/6/2015
 */
public class ProgramSearchBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String votCode;
	private String votName;
	private String programActivityCode;
	private String programActivityDesc;
	private String status;
	
	public ProgramSearchBean(){}
	
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
	 * @return the programActivityCode
	 */
	public String getProgramActivityCode() {
		return programActivityCode;
	}
	/**
	 * @param programActivityCode the programActivityCode to set
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
	 * @param programActivityDesc the programActivityDesc to set
	 */
	public void setProgramActivityDesc(String programActivityDesc) {
		this.programActivityDesc = programActivityDesc;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
