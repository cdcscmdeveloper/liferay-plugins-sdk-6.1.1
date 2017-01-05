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
 * The Class SourcingCustomerServiceBean.
 * 
 * @author Garikapati Mangaiah.
 */
public class SourcingCustomerServiceBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private String documentType;
	private String officerName;
	private String statusDesc;
	private Integer reassignType;
	private String documentNum;
	private Long status=new Long(0L);
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the officerName
	 */
	public String getOfficerName() {
		return officerName;
	}
	/**
	 * @param officerName the officerName to set
	 */
	public void setOfficerName(String officerName) {
		this.officerName = officerName;
	}
	/**
	 * @return the statusDesc
	 */
	public String getStatusDesc() {
		return statusDesc;
	}
	/**
	 * @param statusDesc the statusDesc to set
	 */
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	/**
	 * @return the reassignType
	 */
	public Integer getReassignType() {
		return reassignType;
	}
	/**
	 * @param reassignType the reassignType to set
	 */
	public void setReassignType(Integer reassignType) {
		this.reassignType = reassignType;
	}
	/**
	 * @return the documentNum
	 */
	public String getDocumentNum() {
		return documentNum;
	}
	/**
	 * @param documentNum the documentNum to set
	 */
	public void setDocumentNum(String documentNum) {
		this.documentNum = documentNum;
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
	
	

}
