/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import my.ep.dto.pm.common.trackingdiary.TrackingDiaryReqDTO;



/**
 * Task Reassignment VO
 * 
 * @author Ken
 * @since 14.10.2016
 *
 */
public class PmTaskReassignmentVO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long docId;
	private String docType;
	private String docNo;
	private Long docStatus;
	private List<String> roleList;
	private String groupDocType;
	private Long groupDocId;
	private Long userGroupId;
	private Long addressId;
	private Long orgProfileId;
	private BigDecimal approvalLimit;
	private BigDecimal orderAmount;
	private boolean supplier;
	private List<TrackingDiaryReqDTO> trackingDiaryList;
	
	
	public PmTaskReassignmentVO(){
		
	}


	/**
	 * @return the docId
	 */
	public Long getDocId() {
		return docId;
	}


	/**
	 * @param docId the docId to set
	 */
	public void setDocId(Long docId) {
		this.docId = docId;
	}


	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}


	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}


	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}


	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}


	/**
	 * @return the docStatus
	 */
	public Long getDocStatus() {
		return docStatus;
	}


	/**
	 * @param docStatus the docStatus to set
	 */
	public void setDocStatus(Long docStatus) {
		this.docStatus = docStatus;
	}


	/**
	 * @return the roleList
	 */
	public List<String> getRoleList() {
		return roleList;
	}


	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}


	/**
	 * @return the groupDocType
	 */
	public String getGroupDocType() {
		return groupDocType;
	}


	/**
	 * @param groupDocType the groupDocType to set
	 */
	public void setGroupDocType(String groupDocType) {
		this.groupDocType = groupDocType;
	}


	/**
	 * @return the groupDocId
	 */
	public Long getGroupDocId() {
		return groupDocId;
	}


	/**
	 * @param groupDocId the groupDocId to set
	 */
	public void setGroupDocId(Long groupDocId) {
		this.groupDocId = groupDocId;
	}


	/**
	 * @return the userGroupId
	 */
	public Long getUserGroupId() {
		return userGroupId;
	}


	/**
	 * @param userGroupId the userGroupId to set
	 */
	public void setUserGroupId(Long userGroupId) {
		this.userGroupId = userGroupId;
	}


	/**
	 * @return the addressId
	 */
	public Long getAddressId() {
		return addressId;
	}


	/**
	 * @param addressId the addressId to set
	 */
	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}


	/**
	 * @return the orgProfileId
	 */
	public Long getOrgProfileId() {
		return orgProfileId;
	}


	/**
	 * @param orgProfileId the orgProfileId to set
	 */
	public void setOrgProfileId(Long orgProfileId) {
		this.orgProfileId = orgProfileId;
	}


	/**
	 * @return the approvalLimit
	 */
	public BigDecimal getApprovalLimit() {
		return approvalLimit;
	}


	/**
	 * @param approvalLimit the approvalLimit to set
	 */
	public void setApprovalLimit(BigDecimal approvalLimit) {
		this.approvalLimit = approvalLimit;
	}


	/**
	 * @return the supplier
	 */
	public boolean isSupplier() {
		return supplier;
	}


	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(boolean supplier) {
		this.supplier = supplier;
	}


	/**
	 * @return the orderAmount
	 */
	public BigDecimal getOrderAmount() {
		return orderAmount;
	}


	/**
	 * @param orderAmount the orderAmount to set
	 */
	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}


	/**
	 * @return the trackingDiaryList
	 */
	public List<TrackingDiaryReqDTO> getTrackingDiaryList() {
		return trackingDiaryList;
	}


	/**
	 * @param trackingDiaryList the trackingDiaryList to set
	 */
	public void setTrackingDiaryList(List<TrackingDiaryReqDTO> trackingDiaryList) {
		this.trackingDiaryList = trackingDiaryList;
	}
	

}
