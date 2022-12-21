/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Professional Certified Body
 * 
 * @author prasad
 * @since 10/12/20014
 *
 */
public class ProfCertifiedBodySearchBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String bodyCode;
	private String bodyName;
	private Long status;
	private Integer certType;

	public ProfCertifiedBodySearchBean() {
	}

	/**
	 * @return the bodyCode
	 */
	public String getBodyCode() {
		return bodyCode;
	}

	/**
	 * @param bodyCode the bodyCode to set
	 */
	public void setBodyCode(String bodyCode) {
		this.bodyCode = bodyCode;
	}

	/**
	 * @return the bodyName
	 */
	public String getBodyName() {
		return bodyName;
	}

	/**
	 * @param bodyName the bodyName to set
	 */
	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
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
	 * @return the certType
	 */
	public Integer getCertType() {
		return certType;
	}

	/**
	 * @param certType the certType to set
	 */
	public void setCertType(Integer certType) {
		this.certType = certType;
	}

}