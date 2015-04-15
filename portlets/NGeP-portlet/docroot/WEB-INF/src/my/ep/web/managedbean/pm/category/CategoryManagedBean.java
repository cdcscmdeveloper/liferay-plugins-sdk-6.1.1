/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.managedbean.pm.category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCategoryRestr;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.managedbean.BaseManagedBean;


/**
 * The Class CategoryManagedBean.
 */
@ManagedBean(name = "categoryPM")
@SessionScoped
public class CategoryManagedBean extends BaseManagedBean implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The active index. */
	private int activeIndex = 0;
	
	/** The organisation active index. */
	private int organisationActiveIndex;
	
	/** The main cat code. */
	private String mainCatCode;
	
	/** The sub cat code. */
	private String subCatCode;
	
	/** The sub div code. */
	private String subDivCode;
	
	/** The main cat desc. */
	private String mainCatDesc;
	
	/** The sub cat desc. */
	private String subCatDesc;
	
	/** The sub div desc. */
	private String subDivDesc;
	
	/** The pm category level. */
	private Integer pmCategoryLevel;
	
	/** The pm category l1. */
	private PmCategoryL1 pmCategoryL1;
	
	/** The pm category l2. */
	private PmCategoryL2 pmCategoryL2;
	
	/** The pm category l3. */
	private PmCategoryL3 pmCategoryL3;
	
	/** The cat level. */
	private String catLevel;
	
	/** The status. */
	private Long status;
	
	/** The new category. */
	private boolean categoryRestrRequired = true;
	
	/** The new category. */
	private boolean newCategory = false;
	
	/** The category for company type. */
	private boolean categoryForCompanyType = false;
	
	/** The check special category. */
	private boolean checkSpecialCategory = false;
	
	/** The display category grouping. */
	private boolean displayCategoryGrouping = false;
	
	/** The supplier type. */
	private List<PmParameterDesc> supplierType;
	
	/** The special category. */
	private List<PmParameterDesc> specialCategory;
	
	/** The selected basic category. */
	private List<PmBasicCategory> selectedBasicCategory;
	
	/** The basic category list. */
	private List<PmBasicCategory> basicCategoryList;
	
	/** The category restr list. */
	private List<PmCategoryRestr> categoryRestrList;
	
	/** The tobe added category restr list. */
	private List<PmCategoryRestr> tobeAddedCategoryRestrList;
	
	/** The tobe added pm basic category list. */
	private List<PmBasicCategory> tobeAddedPmBasicCategoryList;
	
	/** The tobe added pm machine category list. */
	private List<PmMachineCategory> tobeAddedPmMachineCategoryList;
	
	/** The pm machine category list. */
	private List<PmMachineCategory> pmMachineCategoryList;
	
	/** The selected basic category2. */
	private PmBasicCategory[] selectedBasicCategory2;
	
	/** The search field. */
	private List<SelectItem> searchField;
	
	/** The search operator. */
	private List<SelectItem> searchOperator;
	
	/** The search value. */
	private String[] searchValue = new String[3];
	
	/** The search field value. */
	private String[] searchFieldValue = new String[3];
	
	/** The search operator value. */
	private String[] searchOperatorValue = new String[3];
	
	/** The selected lov. */
	private List<PmCategoryL3> selectedLOV;
	
	/** The pm basic category list. */
	private List<PmBasicCategory> pmBasicCategoryList;
	
	/** The pm machine. */
	private List<PmMachine> pmMachine;
	
	/** The selectedpm machine. */
	private List<PmMachine> selectedpmMachine;
	
	/** The pm machine category. */
	private List<PmMachineCategory> pmMachineCategory;
	
	/** The selectedpm machine category. */
	private List<PmMachineCategory> selectedpmMachineCategory;
	
	/** The deleted pm machine category. */
	private List<PmMachineCategory> deletedpmMachineCategory = new ArrayList<PmMachineCategory>();
	
	/** The deleted pm category restr. */
	private List<PmCategoryRestr> deletedpmRestrCategory=new ArrayList<PmCategoryRestr>();

	/** The deleted pm basic category list. */
	private List<PmBasicCategory> deletedpmBasicCategory=new ArrayList<PmBasicCategory>();
	
	/** The selected pm category restr. */
	private List<PmCategoryRestr> selectedPmCategoryRestr;
	
	/** The company type list. */
	private List<PmParameterDesc> companyTypeList;
	
	/**
	 * Gets the cat level.
	 * 
	 * @return the cat level
	 */
	public String getCatLevel() {
		return catLevel;
	}

	/**
	 * Sets the cat level.
	 * 
	 * @param i
	 *            the new cat level
	 */
	public void setCatLevel(String i) {
		this.catLevel = i;
	}

	/**
	 * Gets the pm category l1.
	 * 
	 * @return the pm category l1
	 */
	public PmCategoryL1 getPmCategoryL1() {
		return pmCategoryL1;
	}

	/**
	 * Sets the pm category l1.
	 * 
	 * @param pmCategoryL1
	 *            the new pm category l1
	 */
	public void setPmCategoryL1(PmCategoryL1 pmCategoryL1) {
		this.pmCategoryL1 = pmCategoryL1;
	}

	/**
	 * Gets the main cat code.
	 * 
	 * @return the main cat code
	 */
	public String getMainCatCode() {
		return mainCatCode;
	}

	/**
	 * Sets the main cat code.
	 * 
	 * @param mainCatCode
	 *            the new main cat code
	 */
	public void setMainCatCode(String mainCatCode) {
		this.mainCatCode = mainCatCode;
	}

	/**
	 * Checks if is category for company type.
	 * 
	 * @return true, if is category for company type
	 */
	public boolean isCategoryForCompanyType() {
		return categoryForCompanyType;
	}

	/**
	 * Sets the category for company type.
	 * 
	 * @param categoryForCompanyType
	 *            the new category for company type
	 */
	public void setCategoryForCompanyType(boolean categoryForCompanyType) {
		this.categoryForCompanyType = categoryForCompanyType;
	}
	
	/**
	 * Gets the sub cat code.
	 * 
	 * @return the sub cat code
	 */
	public String getSubCatCode() {
		return subCatCode;
	}

	/**
	 * Sets the sub cat code.
	 * 
	 * @param subCatCode
	 *            the new sub cat code
	 */
	public void setSubCatCode(String subCatCode) {
		this.subCatCode = subCatCode;
	}

	/**
	 * Gets the sub div code.
	 * 
	 * @return the sub div code
	 */
	public String getSubDivCode() {
		return subDivCode;
	}

	/**
	 * Sets the sub div code.
	 * 
	 * @param subDivCode
	 *            the new sub div code
	 */
	public void setSubDivCode(String subDivCode) {
		this.subDivCode = subDivCode;
	}

	/**
	 * Gets the main cat desc.
	 * 
	 * @return the main cat desc
	 */
	public String getMainCatDesc() {
		return mainCatDesc;
	}

	/**
	 * Sets the main cat desc.
	 * 
	 * @param mainCatDesc
	 *            the new main cat desc
	 */
	public void setMainCatDesc(String mainCatDesc) {
		this.mainCatDesc = mainCatDesc;
	}

	/**
	 * Gets the sub cat desc.
	 * 
	 * @return the sub cat desc
	 */
	public String getSubCatDesc() {
		return subCatDesc;
	}

	/**
	 * Sets the sub cat desc.
	 * 
	 * @param subCatDesc
	 *            the new sub cat desc
	 */
	public void setSubCatDesc(String subCatDesc) {
		this.subCatDesc = subCatDesc;
	}

	/**
	 * Checks if is check special category.
	 * 
	 * @return true, if is check special category
	 */
	public boolean isCheckSpecialCategory() {
		return checkSpecialCategory;
	}

	/**
	 * Sets the check special category.
	 * 
	 * @param checkSpecialCategory
	 *            the new check special category
	 */
	public void setCheckSpecialCategory(boolean checkSpecialCategory) {
		this.checkSpecialCategory = checkSpecialCategory;
	}
	
	/**
	 * Gets the sub div desc.
	 * 
	 * @return the sub div desc
	 */
	public String getSubDivDesc() {
		return subDivDesc;
	}

	/**
	 * Sets the sub div desc.
	 * 
	 * @param subDivDesc
	 *            the new sub div desc
	 */
	public void setSubDivDesc(String subDivDesc) {
		this.subDivDesc = subDivDesc;
	}


	/**
	 * Instantiates a new category managed bean.
	 */
	public CategoryManagedBean() {

	}

	/**
	 * Gets the active index.
	 * 
	 * @return the active index
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * Sets the active index.
	 * 
	 * @param activeIndex
	 *            the new active index
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * Gets the organisation active index.
	 * 
	 * @return the organisation active index
	 */
	public int getOrganisationActiveIndex() {
		return organisationActiveIndex;
	}

	/**
	 * Sets the organisation active index.
	 * 
	 * @param organisationActiveIndex
	 *            the new organisation active index
	 */
	public void setOrganisationActiveIndex(int organisationActiveIndex) {
		this.organisationActiveIndex = organisationActiveIndex;
	}

	/**
	 * Checks if is new category.
	 * 
	 * @return true, if is new category
	 */
	public boolean isNewCategory() {
		return newCategory;
	}

	/**
	 * Sets the new category.
	 * 
	 * @param newCategory
	 *            the new new category
	 */
	public void setNewCategory(boolean newCategory) {
		this.newCategory = newCategory;
	}

	/**
	 * Gets the pm category l2.
	 * 
	 * @return the pm category l2
	 */
	public PmCategoryL2 getPmCategoryL2() {
		return pmCategoryL2;
	}

	/**
	 * Sets the pm category l2.
	 * 
	 * @param pmCategoryL2
	 *            the new pm category l2
	 */
	public void setPmCategoryL2(PmCategoryL2 pmCategoryL2) {
		this.pmCategoryL2 = pmCategoryL2;
	}

	/**
	 * Gets the pm category l3.
	 * 
	 * @return the pm category l3
	 */
	public PmCategoryL3 getPmCategoryL3() {
		return pmCategoryL3;
	}

	/**
	 * Sets the pm category l3.
	 * 
	 * @param pmCategoryL3
	 *            the new pm category l3
	 */
	public void setPmCategoryL3(PmCategoryL3 pmCategoryL3) {
		this.pmCategoryL3 = pmCategoryL3;
	}

	/**
	 * Gets the supplier type.
	 * 
	 * @return the supplier type
	 */
	public List<PmParameterDesc> getSupplierType() {
		return supplierType;
	}

	/**
	 * Sets the supplier type.
	 * 
	 * @param supplierType
	 *            the new supplier type
	 */
	public void setSupplierType(List<PmParameterDesc> supplierType) {
		this.supplierType = supplierType;
	}

	/**
	 * Gets the special category.
	 * 
	 * @return the special category
	 */
	public List<PmParameterDesc> getSpecialCategory() {
		return specialCategory;
	}

	/**
	 * Sets the special category.
	 * 
	 * @param specialCategory
	 *            the new special category
	 */
	public void setSpecialCategory(List<PmParameterDesc> specialCategory) {
		this.specialCategory = specialCategory;
	}

	/**
	 * Gets the selected basic category.
	 * 
	 * @return the selected basic category
	 */
	public List<PmBasicCategory> getSelectedBasicCategory() {
		return selectedBasicCategory;
	}

	/**
	 * Sets the selected basic category.
	 * 
	 * @param selectedBasicCategory
	 *            the new selected basic category
	 */
	public void setSelectedBasicCategory(List<PmBasicCategory> selectedBasicCategory) {
		this.selectedBasicCategory = selectedBasicCategory;
	}

	/**
	 * Gets the search field.
	 * 
	 * @return the search field
	 */
	public List<SelectItem> getSearchField() {
		return searchField;
	}

	/**
	 * Sets the search field.
	 * 
	 * @param searchField
	 *            the new search field
	 */
	public void setSearchField(List<SelectItem> searchField) {
		this.searchField = searchField;
	}

	/**
	 * Gets the search operator.
	 * 
	 * @return the search operator
	 */
	public List<SelectItem> getSearchOperator() {
		return searchOperator;
	}

	/**
	 * Sets the search operator.
	 * 
	 * @param searchOperator
	 *            the new search operator
	 */
	public void setSearchOperator(List<SelectItem> searchOperator) {
		this.searchOperator = searchOperator;
	}

	/**
	 * Gets the search value.
	 * 
	 * @return the search value
	 */
	public String[] getSearchValue() {
		return searchValue;
	}

	/**
	 * Sets the search value.
	 * 
	 * @param searchValue
	 *            the new search value
	 */
	public void setSearchValue(String[] searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * Gets the search field value.
	 * 
	 * @return the search field value
	 */
	public String[] getSearchFieldValue() {
		return searchFieldValue;
	}

	/**
	 * Sets the search field value.
	 * 
	 * @param searchFieldValue
	 *            the new search field value
	 */
	public void setSearchFieldValue(String[] searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}

	/**
	 * Gets the search operator value.
	 * 
	 * @return the search operator value
	 */
	public String[] getSearchOperatorValue() {
		return searchOperatorValue;
	}

	/**
	 * Sets the search operator value.
	 * 
	 * @param searchOperatorValue
	 *            the new search operator value
	 */
	public void setSearchOperatorValue(String[] searchOperatorValue) {
		this.searchOperatorValue = searchOperatorValue;
	}

	/**
	 * Gets the selected lov.
	 * 
	 * @return the selected lov
	 */
	public List<PmCategoryL3> getSelectedLOV() {
		return selectedLOV;
	}

	/**
	 * Sets the selected lov.
	 * 
	 * @param selectedLOV
	 *            the new selected lov
	 */
	public void setSelectedLOV(List<PmCategoryL3> selectedLOV) {
		this.selectedLOV = selectedLOV;
	}

	/**
	 * Gets the pm basic category list.
	 * 
	 * @return the pm basic category list
	 */
	public List<PmBasicCategory> getPmBasicCategoryList() {
		return pmBasicCategoryList;
	}

	/**
	 * Sets the pm basic category list.
	 * 
	 * @param pmBasicCategoryList
	 *            the new pm basic category list
	 */
	public void setPmBasicCategoryList(List<PmBasicCategory> pmBasicCategoryList) {
		this.pmBasicCategoryList = pmBasicCategoryList;
	}

	/**
	 * Gets the selected basic category2.
	 * 
	 * @return the selected basic category2
	 */
	public PmBasicCategory[] getSelectedBasicCategory2() {
		return selectedBasicCategory2;
	}

	/**
	 * Sets the selected basic category2.
	 * 
	 * @param selectedBasicCategory2
	 *            the new selected basic category2
	 */
	public void setSelectedBasicCategory2(PmBasicCategory[] selectedBasicCategory2) {
		this.selectedBasicCategory2 = selectedBasicCategory2;
	}

	/**
	 * Gets the pm category level.
	 * 
	 * @return the pm category level
	 */
	public Integer getPmCategoryLevel() {
		return pmCategoryLevel;
	}

	/**
	 * Sets the pm category level.
	 * 
	 * @param pmCategoryLevel
	 *            the new pm category level
	 */
	public void setPmCategoryLevel(Integer pmCategoryLevel) {
		this.pmCategoryLevel = pmCategoryLevel;
	}

	/**
	 * Checks if is display category grouping.
	 * 
	 * @return true, if is display category grouping
	 */
	public boolean isDisplayCategoryGrouping() {
		return displayCategoryGrouping;
	}

	/**
	 * Sets the display category grouping.
	 * 
	 * @param displayCategoryGrouping
	 *            the new display category grouping
	 */
	public void setDisplayCategoryGrouping(boolean displayCategoryGrouping) {
		this.displayCategoryGrouping = displayCategoryGrouping;
	}

	/**
	 * Gets the pm machine category.
	 * 
	 * @return the pm machine category
	 */
	public List<PmMachineCategory> getPmMachineCategory() {
		return pmMachineCategory;
	}

	/**
	 * Sets the pm machine category.
	 * 
	 * @param pmMachineCategory
	 *            the new pm machine category
	 */
	public void setPmMachineCategory(List<PmMachineCategory> pmMachineCategory) {
		this.pmMachineCategory = pmMachineCategory;
	}

	/**
	 * Gets the selectedpm machine category.
	 * 
	 * @return the selectedpm machine category
	 */
	public List<PmMachineCategory> getSelectedpmMachineCategory() {
		return selectedpmMachineCategory;
	}

	/**
	 * Sets the selectedpm machine category.
	 * 
	 * @param selectedpmMachineCategory
	 *            the new selectedpm machine category
	 */
	public void setSelectedpmMachineCategory(
			List<PmMachineCategory> selectedpmMachineCategory) {
		this.selectedpmMachineCategory = selectedpmMachineCategory;
	}
	/**
	 * Gets the pm machine.
	 * 
	 * @return the pm machine
	 */
	public List<PmMachine> getPmMachine() {
		return pmMachine;
	}

	/**
	 * Sets the pm machine.
	 * 
	 * @param pmMachine
	 *            the new pm machine
	 */
	public void setPmMachine(List<PmMachine> pmMachine) {
		this.pmMachine = pmMachine;
	}

	/**
	 * Gets the selectedpm machine.
	 * 
	 * @return the selectedpm machine
	 */
	public List<PmMachine> getSelectedpmMachine() {
		return selectedpmMachine;
	}

	/**
	 * Sets the selectedpm machine.
	 * 
	 * @param selectedpmMachine
	 *            the new selectedpm machine
	 */
	public void setSelectedpmMachine(List<PmMachine> selectedpmMachine) {
		this.selectedpmMachine = selectedpmMachine;
	}

	/**
	 * Gets the deletedpm machine category.
	 * 
	 * @return the deletedpm machine category
	 */
	public List<PmMachineCategory> getDeletedpmMachineCategory() {
		return deletedpmMachineCategory;
	}

	/**
	 * Sets the deletedpm machine category.
	 * 
	 * @param deletedpmMachineCategory
	 *            the new deletedpm machine category
	 */
	public void setDeletedpmMachineCategory(
			List<PmMachineCategory> deletedpmMachineCategory) {
		this.deletedpmMachineCategory = deletedpmMachineCategory;
	}

	/**
	 * Gets the selected pm category restr.
	 * 
	 * @return the selected pm category restr
	 */
	public List<PmCategoryRestr> getSelectedPmCategoryRestr() {
		return selectedPmCategoryRestr;
	}

	/**
	 * Sets the selected pm category restr.
	 * 
	 * @param selectedPmCategoryRestr
	 *            the new selected pm category restr
	 */
	public void setSelectedPmCategoryRestr(
			List<PmCategoryRestr> selectedPmCategoryRestr) {
		this.selectedPmCategoryRestr = selectedPmCategoryRestr;
	}

	/**
	 * Gets the company type list.
	 * 
	 * @return the company type list
	 */
	public List<PmParameterDesc> getCompanyTypeList() {
		return companyTypeList;
	}

	/**
	 * Sets the company type list.
	 * 
	 * @param companyTypeList
	 *            the new company type list
	 */
	public void setCompanyTypeList(List<PmParameterDesc> companyTypeList) {
		this.companyTypeList = companyTypeList;
	}


	/**
	 * Gets the deletedpm restr category.
	 * 
	 * @return the deletedpm restr category
	 */
	public List<PmCategoryRestr> getDeletedpmRestrCategory() {
		return deletedpmRestrCategory;
	}

	/**
	 * Sets the deletedpm restr category.
	 * 
	 * @param deletedpmRestrCategory
	 *            the new deletedpm restr category
	 */
	public void setDeletedpmRestrCategory(
			List<PmCategoryRestr> deletedpmRestrCategory) {
		this.deletedpmRestrCategory = deletedpmRestrCategory;
	}

	/**
	 * Gets the deletedpm basic category.
	 * 
	 * @return the deletedpm basic category
	 */
	public List<PmBasicCategory> getDeletedpmBasicCategory() {
		return deletedpmBasicCategory;
	}

	/**
	 * Sets the deletedpm basic category.
	 * 
	 * @param deletedpmBasicCategory
	 *            the new deletedpm basic category
	 */
	public void setDeletedpmBasicCategory(
			List<PmBasicCategory> deletedpmBasicCategory) {
		this.deletedpmBasicCategory = deletedpmBasicCategory;
	}

	/**
	 * @return the basicCategoryList
	 */
	public List<PmBasicCategory> getBasicCategoryList() {
		return basicCategoryList;
	}

	/**
	 * @param basicCategoryList the basicCategoryList to set
	 */
	public void setBasicCategoryList(List<PmBasicCategory> basicCategoryList) {
		this.basicCategoryList = basicCategoryList;
	}

	/**
	 * @return the pmMachineCategoryList
	 */
	public List<PmMachineCategory> getPmMachineCategoryList() {
		return pmMachineCategoryList;
	}

	/**
	 * @param pmMachineCategoryList the pmMachineCategoryList to set
	 */
	public void setPmMachineCategoryList(
			List<PmMachineCategory> pmMachineCategoryList) {
		this.pmMachineCategoryList = pmMachineCategoryList;
	}

	/**
	 * @return the categoryRestrRequired
	 */
	public boolean isCategoryRestrRequired() {
		return categoryRestrRequired;
	}

	/**
	 * @param categoryRestrRequired the categoryRestrRequired to set
	 */
	public void setCategoryRestrRequired(boolean categoryRestrRequired) {
		this.categoryRestrRequired = categoryRestrRequired;
	}

	/**
	 * @return the categoryRestrList
	 */
	public List<PmCategoryRestr> getCategoryRestrList() {
		return categoryRestrList;
	}

	/**
	 * @param categoryRestrList the categoryRestrList to set
	 */
	public void setCategoryRestrList(List<PmCategoryRestr> categoryRestrList) {
		this.categoryRestrList = categoryRestrList;
	}

	/**
	 * @return the tobeAddedCategoryRestrList
	 */
	public List<PmCategoryRestr> getTobeAddedCategoryRestrList() {
		return tobeAddedCategoryRestrList;
	}

	/**
	 * @param tobeAddedCategoryRestrList the tobeAddedCategoryRestrList to set
	 */
	public void setTobeAddedCategoryRestrList(
			List<PmCategoryRestr> tobeAddedCategoryRestrList) {
		this.tobeAddedCategoryRestrList = tobeAddedCategoryRestrList;
	}

	/**
	 * @return the tobeAddedPmBasicCategoryList
	 */
	public List<PmBasicCategory> getTobeAddedPmBasicCategoryList() {
		return tobeAddedPmBasicCategoryList;
	}

	/**
	 * @param tobeAddedPmBasicCategoryList the tobeAddedPmBasicCategoryList to set
	 */
	public void setTobeAddedPmBasicCategoryList(
			List<PmBasicCategory> tobeAddedPmBasicCategoryList) {
		this.tobeAddedPmBasicCategoryList = tobeAddedPmBasicCategoryList;
	}

	/**
	 * @return the tobeAddedPmMachineCategoryList
	 */
	public List<PmMachineCategory> getTobeAddedPmMachineCategoryList() {
		return tobeAddedPmMachineCategoryList;
	}

	/**
	 * @param tobeAddedPmMachineCategoryList the tobeAddedPmMachineCategoryList to set
	 */
	public void setTobeAddedPmMachineCategoryList(
			List<PmMachineCategory> tobeAddedPmMachineCategoryList) {
		this.tobeAddedPmMachineCategoryList = tobeAddedPmMachineCategoryList;
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
