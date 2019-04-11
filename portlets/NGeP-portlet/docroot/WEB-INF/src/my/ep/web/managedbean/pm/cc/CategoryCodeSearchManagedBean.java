/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.pm.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Common Category Code Search Managed Bean
 * 
 * @author Kenny
 * @since 11.03.2015
 */

@ManagedBean(name="categoryCodeSearchMB")
@SessionScoped
public class CategoryCodeSearchManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	 
	private String searchBy = ParameterConst.CATEGORY_CODE_SEARCH_BY_CODE;
	private Long categoryCodeIdLvl1;
	private Long categoryCodeIdLvl2;
	private Long categoryCodeIdLvl3;
	private String categoryDesciption;
	private String categoryCode;
	private PmCategoryL3 selectedCategoryL3;
	private IPagingDataModel<PmCategoryL3> categoryDescrDataModel;
	private IPagingDataModel<PmCategoryL3> categoryDataModel;
	
	private String supplierType = null;
	private String facilityType = null;
	private String paidUpCapital = null;
	private String bumiPercentage = null;
	private String categoryType = null;
	private String businessTypeCode = null;
	private String catcodeL1;
	
	public CategoryCodeSearchManagedBean() {
	}

	/**
	 * @return the searchBy
	 */
	public String getSearchBy() {
		return searchBy;
	}

	/**
	 * @param searchBy the searchBy to set
	 */
	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}

	/**
	 * @return the categoryCodeIdLvl1
	 */
	public Long getCategoryCodeIdLvl1() {
		return categoryCodeIdLvl1;
	}

	/**
	 * @param categoryCodeIdLvl1 the categoryCodeIdLvl1 to set
	 */
	public void setCategoryCodeIdLvl1(Long categoryCodeIdLvl1) {
		this.categoryCodeIdLvl1 = categoryCodeIdLvl1;
	}

	/**
	 * @return the categoryCodeIdLvl2
	 */
	public Long getCategoryCodeIdLvl2() {
		return categoryCodeIdLvl2;
	}

	/**
	 * @param categoryCodeIdLvl2 the categoryCodeIdLvl2 to set
	 */
	public void setCategoryCodeIdLvl2(Long categoryCodeIdLvl2) {
		this.categoryCodeIdLvl2 = categoryCodeIdLvl2;
	}

	/**
	 * @return the categoryCodeIdLvl3
	 */
	public Long getCategoryCodeIdLvl3() {
		return categoryCodeIdLvl3;
	}

	/**
	 * @param categoryCodeIdLvl3 the categoryCodeIdLvl3 to set
	 */
	public void setCategoryCodeIdLvl3(Long categoryCodeIdLvl3) {
		this.categoryCodeIdLvl3 = categoryCodeIdLvl3;
	}

	/**
	 * @return the categoryDesciption
	 */
	public String getCategoryDesciption() {
		return categoryDesciption;
	}

	/**
	 * @param categoryDesciption the categoryDesciption to set
	 */
	public void setCategoryDesciption(String categoryDesciption) {
		this.categoryDesciption = categoryDesciption;
	}

	/**
	 * @return the selectedCategoryL3
	 */
	public PmCategoryL3 getSelectedCategoryL3() {
		return selectedCategoryL3;
	}

	/**
	 * @param selectedCategoryL3 the selectedCategoryL3 to set
	 */
	public void setSelectedCategoryL3(PmCategoryL3 selectedCategoryL3) {
		this.selectedCategoryL3 = selectedCategoryL3;
	}

	/**
	 * @return the categoryDescrDataModel
	 */
	public IPagingDataModel<PmCategoryL3> getCategoryDescrDataModel() {
		return categoryDescrDataModel;
	}

	/**
	 * @param categoryDescrDataModel the categoryDescrDataModel to set
	 */
	public void setCategoryDescrDataModel(IPagingDataModel<PmCategoryL3> categoryDescrDataModel) {
		this.categoryDescrDataModel = categoryDescrDataModel;
	}

	/**
	 * @return the categoryDescrDataModel
	 */
	public IPagingDataModel<PmCategoryL3> getCategoryDataModel() {
		return categoryDataModel;
	}

	/**
	 * @param categoryDescrDataModel the categoryDescrDataModel to set
	 */
	public void setCategoryDataModel(IPagingDataModel<PmCategoryL3> categoryDataModel) {
		this.categoryDataModel = categoryDataModel;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public String getPaidUpCapital() {
		return paidUpCapital;
	}

	public void setPaidUpCapital(String paidUpCapital) {
		this.paidUpCapital = paidUpCapital;
	}

	public String getBumiPercentage() {
		return bumiPercentage;
	}

	public void setBumiPercentage(String bumiPercentage) {
		this.bumiPercentage = bumiPercentage;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getBusinessTypeCode() {
		return businessTypeCode;
	}

	public void setBusinessTypeCode(String businessTypeCode) {
		this.businessTypeCode = businessTypeCode;
	}

	public String getCatcodeL1() {
		return catcodeL1;
	}

	public void setCatcodeL1(String catcodeL1) {
		this.catcodeL1 = catcodeL1;
	}
	
}
