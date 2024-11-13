/**
 * Copyright © 2014 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.pm.businessnature;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Business Nature
 * 
 * @author DK
 * @since 15/8/2014
 */
@ManagedBean(name = "businessNatureManagedBean")
@SessionScoped
public class BusinessNatureManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	/**
	 * business nature search variable
	 */
	private Long categoryLvl;

	private String secLevelCode;
	private String secLevelName;
	private String divLevelCode;
	private String divLevelName;
	private String groupLevelCode;
	private String groupLevelName;
	private String classLevelCode;
	private String classLevelName;
	private String itemLevelCode;
	private String itemLevelName;
	private boolean brandNew = false;
	private boolean newcategory = false;

	private Long status;

	private boolean isLazy = false;

	private PmBusinessNature pmBusinessNature;

	private PmBusinessNature parentBusinessNature;

	private List<PmBusinessNature> childNatures;
	/*************** AUDIT**********/
	private PmBusinessNature oldPmBusinessNature;
	/*************** AUDIT**********/
	public BusinessNatureManagedBean(){}

	/**
	 * @return the categoryLvl
	 */
	public Long getCategoryLvl() {
		return categoryLvl;
	}

	/**
	 * @param categoryLvl the categoryLvl to set
	 */
	public void setCategoryLvl(Long categoryLvl) {
		this.categoryLvl = categoryLvl;
	}

	/**
	 * @return the secLevelCode
	 */
	public String getSecLevelCode() {
		return secLevelCode;
	}

	/**
	 * @param secLevelCode the secLevelCode to set
	 */
	public void setSecLevelCode(String secLevelCode) {
		this.secLevelCode = secLevelCode;
	}

	/**
	 * @return the secLevelName
	 */
	public String getSecLevelName() {
		return secLevelName;
	}

	/**
	 * @param secLevelName the secLevelName to set
	 */
	public void setSecLevelName(String secLevelName) {
		this.secLevelName = secLevelName;
	}

	/**
	 * @return the divLevelCode
	 */
	public String getDivLevelCode() {
		return divLevelCode;
	}

	/**
	 * @param divLevelCode the divLevelCode to set
	 */
	public void setDivLevelCode(String divLevelCode) {
		this.divLevelCode = divLevelCode;
	}

	/**
	 * @return the divLevelName
	 */
	public String getDivLevelName() {
		return divLevelName;
	}

	/**
	 * @param divLevelName the divLevelName to set
	 */
	public void setDivLevelName(String divLevelName) {
		this.divLevelName = divLevelName;
	}

	/**
	 * @return the groupLevelCode
	 */
	public String getGroupLevelCode() {
		return groupLevelCode;
	}

	/**
	 * @param groupLevelCode the groupLevelCode to set
	 */
	public void setGroupLevelCode(String groupLevelCode) {
		this.groupLevelCode = groupLevelCode;
	}

	/**
	 * @return the groupLevelName
	 */
	public String getGroupLevelName() {
		return groupLevelName;
	}

	/**
	 * @param groupLevelName the groupLevelName to set
	 */
	public void setGroupLevelName(String groupLevelName) {
		this.groupLevelName = groupLevelName;
	}

	/**
	 * @return the classLevelCode
	 */
	public String getClassLevelCode() {
		return classLevelCode;
	}

	/**
	 * @param classLevelCode the classLevelCode to set
	 */
	public void setClassLevelCode(String classLevelCode) {
		this.classLevelCode = classLevelCode;
	}

	/**
	 * @return the classLevelName
	 */
	public String getClassLevelName() {
		return classLevelName;
	}

	/**
	 * @param classLevelName the classLevelName to set
	 */
	public void setClassLevelName(String classLevelName) {
		this.classLevelName = classLevelName;
	}

	/**
	 * @return the itemLevelCode
	 */
	public String getItemLevelCode() {
		return itemLevelCode;
	}

	/**
	 * @param itemLevelCode the itemLevelCode to set
	 */
	public void setItemLevelCode(String itemLevelCode) {
		this.itemLevelCode = itemLevelCode;
	}

	/**
	 * @return the itemLevelName
	 */
	public String getItemLevelName() {
		return itemLevelName;
	}

	/**
	 * @param itemLevelName the itemLevelName to set
	 */
	public void setItemLevelName(String itemLevelName) {
		this.itemLevelName = itemLevelName;
	}

	/**
	 * @return the brandNew
	 */
	public boolean isBrandNew() {
		return brandNew;
	}

	/**
	 * @param brandNew the brandNew to set
	 */
	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
	}

	/**
	 * @return the newcategory
	 */
	public boolean isNewcategory() {
		return newcategory;
	}

	/**
	 * @param newcategory the newcategory to set
	 */
	public void setNewcategory(boolean newcategory) {
		this.newcategory = newcategory;
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
	 * @return the pmBusinessNature
	 */
	public PmBusinessNature getPmBusinessNature() {
		return pmBusinessNature;
	}

	/**
	 * @param pmBusinessNature the pmBusinessNature to set
	 */
	public void setPmBusinessNature(PmBusinessNature pmBusinessNature) {
		this.pmBusinessNature = pmBusinessNature;
	}

	/**
	 * @return the parentBusinessNature
	 */
	public PmBusinessNature getParentBusinessNature() {
		return parentBusinessNature;
	}

	/**
	 * @param parentBusinessNature the parentBusinessNature to set
	 */
	public void setParentBusinessNature(PmBusinessNature parentBusinessNature) {
		this.parentBusinessNature = parentBusinessNature;
	}

	/**
	 * @return the childNatures
	 */
	public List<PmBusinessNature> getChildNatures() {
		return childNatures;
	}

	/**
	 * @param childNatures the childNatures to set
	 */
	public void setChildNatures(List<PmBusinessNature> childNatures) {
		this.childNatures = childNatures;
	}

	/*********************AUDIT**********/
	public PmBusinessNature getOldPmBusinessNature() {
		return oldPmBusinessNature;
	}

	public void setOldPmBusinessNature(PmBusinessNature oldPmBusinessNature) {
		this.oldPmBusinessNature = oldPmBusinessNature;
	}
	/*********************AUDIT**********/
	
}
