/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmUser;

/**
 * Search Bean for Location
 * 
 */
public class AuditLocationSearchBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//private Long countryId;
	//private String locationCode;
	private String locationType;
	private String actionType;
	private Date dateFrom;
	private Date dateTo;
	private Date dateFromOldVal;
	private Date dateToOldVal;
	private Date pdateFromOldVal;
	private Date pdateToOldVal;
	private String systemConfigParamLevel1;
	private String categoryConfigParamLevel2;
	private String itemConfigParamLevel3;
	private String systemConfigParamLevel1OldVal;
	private String categoryConfigParamLevel2OldVal;
	private String itemConfigParamLevel3OldVal;
	private String role;
	private String actionBy;
	private BigDecimal actionById;
	
	private Integer levelId1;
	private Integer levelId2;
	private Integer levelId3;
	private BigDecimal pmParameterLvlId;
	private BigDecimal pmParameterLvlIdOldval;
	private String createdBy;
	private String createdByOldVal;
	private String roleCode;
	private String roleCodeOldVal;
	private Date pDatefrom;
	private Date pDateto;
	private BigDecimal userId;
	private BigDecimal userIdOldVal;
	private List<PmUser> searchPmUserList;
	private PmUser pmUser;
	private boolean isSearching = true;
//	private String locationName;
//	private String recordStatus;
	//Enhancement29642 - Start
//	private String additionalLocationType;
//	private String additionalLocationName;
//	private String recordAdditional;
	//Enhancement29642 - End
	public AuditLocationSearchBean(){}
	
	
//	/**
//	 * @return the locationCode
//	 */
//	public String getLocationCode() {
//		return locationCode;
//	}
//
//	/**
//	 * @param locationCode the locationCode to set
//	 */
//	public void setLocationCode(String locationCode) {
//		this.locationCode = locationCode;
//	}

	

	/**
	 * @return the locationType
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType the locationType to set
	 */
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getItemConfigParamLevel3() {
		return itemConfigParamLevel3;
	}

	public void setItemConfigParamLevel3(String itemConfigParamLevel3) {
		this.itemConfigParamLevel3 = itemConfigParamLevel3;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getActionBy() {
		return actionBy;
	}

	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

	public String getCategoryConfigParamLevel2() {
		return categoryConfigParamLevel2;
	}

	public void setCategoryConfigParamLevel2(String categoryConfigParamLevel2) {
		this.categoryConfigParamLevel2 = categoryConfigParamLevel2;
	}

	public BigDecimal getActionById() {
		return actionById;
	}

	public void setActionById(BigDecimal actionById) {
		this.actionById = actionById;
	}

	public String getSystemConfigParamLevel1() {
		return systemConfigParamLevel1;
	}

	public void setSystemConfigParamLevel1(String systemConfigParamLevel1) {
		this.systemConfigParamLevel1 = systemConfigParamLevel1;
	}

	public Integer getLevelId1() {
		return levelId1;
	}

	public void setLevelId1(Integer levelId1) {
		this.levelId1 = levelId1;
	}

	public Integer getLevelId2() {
		return levelId2;
	}

	public void setLevelId2(Integer levelId2) {
		this.levelId2 = levelId2;
	}

	public Integer getLevelId3() {
		return levelId3;
	}

	public void setLevelId3(Integer levelId3) {
		this.levelId3 = levelId3;
	}


	public BigDecimal getPmParameterLvlId() {
		return pmParameterLvlId;
	}


	public void setPmParameterLvlId(BigDecimal pmParameterLvlId) {
		this.pmParameterLvlId = pmParameterLvlId;
	}


	public String getRoleCode() {
		return roleCode;
	}


	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return the locationName
	 */
//	public String getLocationName() {
//		return locationName;
//	}
//
//	/**
//	 * @param locationName the locationName to set
//	 */
//	public void setLocationName(String locationName) {
//		this.locationName = locationName;
//	}
//
//	/**
//	 * @return the recordStatus
//	 */
//	public String getRecordStatus() {
//		return recordStatus;
//	}
//
//	/**
//	 * @param recordStatus the recordStatus to set
//	 */
//	public void setRecordStatus(String recordStatus) {
//		
//		this.recordStatus = recordStatus;
//	}


	public Date getDateFrom() {
		return dateFrom;
	}


	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}


	public Date getDateTo() {
		return dateTo;
	}


	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}


	public Date getpDatefrom() {
		return pDatefrom;
	}


	public void setpDatefrom(Date pDatefrom) {
		this.pDatefrom = pDatefrom;
	}


	public Date getpDateto() {
		return pDateto;
	}


	public void setpDateto(Date pDateto) {
		this.pDateto = pDateto;
	}


	public List<PmUser> getSearchPmUserList() {
		if (searchPmUserList.size() <= 0)
			searchPmUserList = new ArrayList<PmUser>();
		return searchPmUserList;
	}


	public void setSearchPmUserList(List<PmUser> searchPmUserList) {
		this.searchPmUserList = searchPmUserList;
	}


	public BigDecimal getUserId() {
		return userId;
	}


	public void setUserId(BigDecimal userId) {
		this.userId = userId;
	}


	public PmUser getPmUser() {
		return pmUser;
	}


	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}


	public boolean isSearching() {
		return isSearching;
	}


	public void setSearching(boolean isSearching) {
		this.isSearching = isSearching;
	}


	public Date getPdateFromOldVal() {
		return pdateFromOldVal;
	}


	public void setPdateFromOldVal(Date pdateFromOldVal) {
		this.pdateFromOldVal = pdateFromOldVal;
	}


	public Date getPdateToOldVal() {
		return pdateToOldVal;
	}


	public void setPdateToOldVal(Date pdateToOldVal) {
		this.pdateToOldVal = pdateToOldVal;
	}


	public BigDecimal getPmParameterLvlIdOldval() {
		return pmParameterLvlIdOldval;
	}


	public void setPmParameterLvlIdOldval(BigDecimal pmParameterLvlIdOldval) {
		this.pmParameterLvlIdOldval = pmParameterLvlIdOldval;
	}


	public String getRoleCodeOldVal() {
		return roleCodeOldVal;
	}


	public void setRoleCodeOldVal(String roleCodeOldVal) {
		this.roleCodeOldVal = roleCodeOldVal;
	}


	public String getCreatedByOldVal() {
		return createdByOldVal;
	}


	public void setCreatedByOldVal(String createdByOldVal) {
		this.createdByOldVal = createdByOldVal;
	}


	public String getSystemConfigParamLevel1OldVal() {
		return systemConfigParamLevel1OldVal;
	}


	public void setSystemConfigParamLevel1OldVal(String systemConfigParamLevel1OldVal) {
		this.systemConfigParamLevel1OldVal = systemConfigParamLevel1OldVal;
	}


	public String getCategoryConfigParamLevel2OldVal() {
		return categoryConfigParamLevel2OldVal;
	}


	public void setCategoryConfigParamLevel2OldVal(String categoryConfigParamLevel2OldVal) {
		this.categoryConfigParamLevel2OldVal = categoryConfigParamLevel2OldVal;
	}


	public String getItemConfigParamLevel3OldVal() {
		return itemConfigParamLevel3OldVal;
	}


	public void setItemConfigParamLevel3OldVal(String itemConfigParamLevel3OldVal) {
		this.itemConfigParamLevel3OldVal = itemConfigParamLevel3OldVal;
	}


	public Date getDateFromOldVal() {
		return dateFromOldVal;
	}


	public void setDateFromOldVal(Date dateFromOldVal) {
		this.dateFromOldVal = dateFromOldVal;
	}


	public Date getDateToOldVal() {
		return dateToOldVal;
	}


	public void setDateToOldVal(Date dateToOldVal) {
		this.dateToOldVal = dateToOldVal;
	}


	public BigDecimal getUserIdOldVal() {
		return userIdOldVal;
	}


	public void setUserIdOldVal(BigDecimal userIdOldVal) {
		this.userIdOldVal = userIdOldVal;
	}

	/**
	 * @return the countryId
	 */
//	public Long getCountryId() {
//		return countryId;
//	}
//
//	/**
//	 * @param countryId the countryId to set
//	 */
//	public void setCountryId(Long countryId) {
//		this.countryId = countryId;
//	}
	//Enhancement29642 - Start
	/**
	 * @return the additionalLocationType
	 */
//	public String getAdditionalLocationType() {
//		return additionalLocationType;
//	}
//
//	/**
//	 * @param additionalLocationType the additionalLocationType to set
//	 */
//	public void setAdditionalLocationType(String additionalLocationType) {
//		this.additionalLocationType = additionalLocationType;
//	}

	/**
	 * @return the additionallocationName
	 */
//	public String getAdditionalLocationName() {
//		return additionalLocationName;
//	}
//
//	/**
//	 * @param additionallocationName the additionallocationName to set
//	 */
//	public void setAdditionalLocationName(String additionalLocationName) {
//		this.additionalLocationName = additionalLocationName;
//	}
	/**
	 * @return the recordAdditional
	 */
//	public String getRecordAdditional() {
//		return recordAdditional;
//	}
//	
	/**
	 * @param setRecordAdditional the setRecordAdditional to set
	 */
//	public void setRecordAdditional(String recordAdditional) {
//		this.recordAdditional= recordAdditional;
//	}
	//Enhancement29642 - End
}
