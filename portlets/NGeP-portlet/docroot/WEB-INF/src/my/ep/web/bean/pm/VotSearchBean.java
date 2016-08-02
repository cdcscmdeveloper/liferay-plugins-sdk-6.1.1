/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;
/**
 * Search Bean for VOT
 * 
 * @author venugopal
 * @since 17/4/2015
 *
 */
public class VotSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private String votCode;
	private String votName;
	private String kumpulanPtjCode;
	private String kumpulanPtjDesc;
	private String status;
	
	public VotSearchBean(){}
	
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
	 * @return the kumpulanPtjCode
	 */
	public String getKumpulanPtjCode() {
		return kumpulanPtjCode;
	}
	/**
	 * @param kumpulanPtjCode the kumpulanPtjCode to set
	 */
	public void setKumpulanPtjCode(String kumpulanPtjCode) {
		this.kumpulanPtjCode = kumpulanPtjCode;
	}
	/**
	 * @return the kumpulanPtjDesc
	 */
	public String getKumpulanPtjDesc() {
		return kumpulanPtjDesc;
	}
	/**
	 * @param kumpulanPtjDesc the kumpulanPtjDesc to set
	 */
	public void setKumpulanPtjDesc(String kumpulanPtjDesc) {
		this.kumpulanPtjDesc = kumpulanPtjDesc;
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
