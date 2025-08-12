/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameterDesc;

/**
 * #35509
 * Search Bean for AuditPMUserOrgSearchBean
 * 
 */
public class AuditPMUserOrgSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String masterLabel1;
	private String masterLabel2;
	private String masterLabel3;
	private String masterLabel4;
	private String masterLabel5;
	private String masterLabel6;
	private String masterLabel7;
	private String masterLabel8;

	private String dropdownOrgUserVal;
	private String orgCode;
	private String orgName;
	private String orgType;
	private String orgCategory = "0";
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
	private String orgTypeParamId;
	private Long orgValidityId;

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
	private BigDecimal pDocGroupId;
	private BigDecimal pUserOrgProfileId;
	private BigDecimal userId;
	private BigDecimal userIdOldVal;
	private List<PmUser> searchPmUserList;
	private PmUser pmUser;
	private List<PmOrgValidity> searchPmOrgValidity;
	private PmOrgValidity pmOrgValidity;
	private List<PmOrgProfile> searchPmOrgProfile;
	private PmOrgProfile pmOrgProfile;
	private boolean isSearching = true;
	private String itemCode;
	private String itemName;
	private String itemCodeOldVal;
	private String itemNameOldVal;
	private String pLvl1OldVal;
	private String pLvl2OldVal;
	private String pLvl3OldVal;
	private String pLvl4OldVal;
	private String pLvl5OldVal;
	private String pLvl6OldVal;
	private String pLvl7OldVal;
	private Long selectedOrgType;
	private PmParameterDesc pmParameterDescOrgType;

	public AuditPMUserOrgSearchBean() {
		/* Empty Constructor */
	}

	/**
	 * @return the locationType
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * @param locationType
	 *            the locationType to set
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
			searchPmUserList = new ArrayList<>();
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

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemCodeOldVal() {
		return itemCodeOldVal;
	}

	public void setItemCodeOldVal(String itemCodeOldVal) {
		this.itemCodeOldVal = itemCodeOldVal;
	}

	public String getItemNameOldVal() {
		return itemNameOldVal;
	}

	public void setItemNameOldVal(String itemNameOldVal) {
		this.itemNameOldVal = itemNameOldVal;
	}

	public String getDropdownOrgUserVal() {
		return dropdownOrgUserVal;
	}

	public void setDropdownOrgUserVal(String dropdownOrgUserVal) {
		this.dropdownOrgUserVal = dropdownOrgUserVal;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getMasterLabel1() {
		return masterLabel1;
	}

	public void setMasterLabel1(String masterLabel1) {
		this.masterLabel1 = masterLabel1;
	}

	public String getMasterLabel2() {
		return masterLabel2 != null ? masterLabel2.trim() : null;
	}

	public void setMasterLabel2(String masterLabel2) {
		this.masterLabel2 = masterLabel2;
	}

	public String getMasterLabel3() {
		return masterLabel3;
	}

	public void setMasterLabel3(String masterLabel3) {
		this.masterLabel3 = masterLabel3;
	}

	public String getMasterLabel4() {
		return masterLabel4;
	}

	public void setMasterLabel4(String masterLabel4) {
		this.masterLabel4 = masterLabel4;
	}

	public String getMasterLabel5() {
		return masterLabel5;
	}

	public void setMasterLabel5(String masterLabel5) {
		this.masterLabel5 = masterLabel5;
	}

	public String getMasterLabel6() {
		return masterLabel6;
	}

	public void setMasterLabel6(String masterLabel6) {
		this.masterLabel6 = masterLabel6;
	}

	public String getMasterLabel7() {
		return masterLabel7;
	}

	public void setMasterLabel7(String masterLabel7) {
		this.masterLabel7 = masterLabel7;
	}

	public String getMasterLabel8() {
		return masterLabel8;
	}

	public void setMasterLabel8(String masterLabel8) {
		this.masterLabel8 = masterLabel8;
	}

	public String getOrgCategory() {
		return orgCategory;
	}

	public void setOrgCategory(String orgCategory) {
		this.orgCategory = orgCategory;
	}

	public BigDecimal getpDocGroupId() {
		return pDocGroupId;
	}

	public void setpDocGroupId(BigDecimal pDocGroupId) {
		this.pDocGroupId = pDocGroupId;
	}

	public String getpLvl1OldVal() {
		return pLvl1OldVal;
	}

	public void setpLvl1OldVal(String pLvl1OldVal) {
		this.pLvl1OldVal = pLvl1OldVal;
	}

	public String getpLvl2OldVal() {
		return pLvl2OldVal;
	}

	public void setpLvl2OldVal(String pLvl2OldVal) {
		this.pLvl2OldVal = pLvl2OldVal;
	}

	public String getpLvl3OldVal() {
		return pLvl3OldVal;
	}

	public void setpLvl3OldVal(String pLvl3OldVal) {
		this.pLvl3OldVal = pLvl3OldVal;
	}

	public String getpLvl4OldVal() {
		return pLvl4OldVal;
	}

	public void setpLvl4OldVal(String pLvl4OldVal) {
		this.pLvl4OldVal = pLvl4OldVal;
	}

	public String getpLvl5OldVal() {
		return pLvl5OldVal;
	}

	public void setpLvl5OldVal(String pLvl5OldVal) {
		this.pLvl5OldVal = pLvl5OldVal;
	}

	public String getpLvl6OldVal() {
		return pLvl6OldVal;
	}

	public void setpLvl6OldVal(String pLvl6OldVal) {
		this.pLvl6OldVal = pLvl6OldVal;
	}

	public String getpLvl7OldVal() {
		return pLvl7OldVal;
	}

	public void setpLvl7OldVal(String pLvl7OldVal) {
		this.pLvl7OldVal = pLvl7OldVal;
	}

	public Long getSelectedOrgType() {
		return selectedOrgType;
	}

	public void setSelectedOrgType(Long selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	public PmParameterDesc getPmParameterDescOrgType() {
		return pmParameterDescOrgType;
	}

	public void setPmParameterDescOrgType(PmParameterDesc pmParameterDescOrgType) {
		this.pmParameterDescOrgType = pmParameterDescOrgType;
	}

	public List<PmOrgValidity> getSearchPmOrgValidity() {
		if (searchPmOrgValidity.size() <= 0)
			searchPmOrgValidity = new ArrayList<>();
		return searchPmOrgValidity;
	}

	public void setSearchPmOrgValidity(List<PmOrgValidity> searchPmOrgValidity) {
		this.searchPmOrgValidity = searchPmOrgValidity;
	}

	public PmOrgValidity getPmOrgValidity() {
		return pmOrgValidity;
	}

	public void setPmOrgValidity(PmOrgValidity pmOrgValidity) {
		this.pmOrgValidity = pmOrgValidity;
	}

	public List<PmOrgProfile> getSearchPmOrgProfile() {
		if (searchPmOrgProfile.size() <= 0)
			searchPmOrgProfile = new ArrayList<>();
		return searchPmOrgProfile;
	}

	public void setSearchPmOrgProfile(List<PmOrgProfile> searchPmOrgProfile) {
		this.searchPmOrgProfile = searchPmOrgProfile;
	}

	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	public String getOrgTypeParamId() {
		return orgTypeParamId;
	}

	public void setOrgTypeParamId(String orgTypeParamId) {
		this.orgTypeParamId = orgTypeParamId;
	}

	public Long getOrgValidityId() {
		return orgValidityId;
	}

	public void setOrgValidityId(Long orgValidityId) {
		this.orgValidityId = orgValidityId;
	}

	public BigDecimal getpUserOrgProfileId() {
		return pUserOrgProfileId;
	}

	public void setpUserOrgProfileId(BigDecimal pUserOrgProfileId) {
		this.pUserOrgProfileId = pUserOrgProfileId;
	}

}
