package my.ep.web.managedbean.pm.category;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "categoryPM")
@SessionScoped
public class CategoryManagedBean extends BaseManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private int activeIndex = 0;
	private int organisationActiveIndex;
	
	private String mainCatCode;
	private String subCatCode;
	private String subDivCode;
	private String mainCatDesc;
	private String subCatDesc;
	private String subDivDesc;
	private PmCategoryL1 pmCategoryL1;
	private PmCategoryL2 pmCategoryL2;
	private PmCategoryL3 pmCategoryL3;
	private String catLevel;
	private boolean newCategory = false;
	private List<PmParameterDesc> supplierType;
	private List<PmParameterDesc> specialCategory;
	private List<PmBasicCategory> selectedBasicCategory;
	private PmBasicCategory[] selectedBasicCategory2;
	private List<SelectItem> searchField;
	private List<SelectItem> searchOperator;
	private String[] searchValue = new String[3];
	private String[] searchFieldValue = new String[3];
	private String[] searchOperatorValue = new String[3];
	private List<PmCategoryL3> selectedLOV;
	private List<PmBasicCategory> pmBasicCategoryList;
	
	
	public String getCatLevel() {
		return catLevel;
	}

	public void setCatLevel(String i) {
		this.catLevel = i;
	}

	public PmCategoryL1 getPmCategoryL1() {
		return pmCategoryL1;
	}

	public void setPmCategoryL1(PmCategoryL1 pmCategoryL1) {
		this.pmCategoryL1 = pmCategoryL1;
	}

	public String getMainCatCode() {
		return mainCatCode;
	}

	public void setMainCatCode(String mainCatCode) {
		this.mainCatCode = mainCatCode;
	}

	public String getSubCatCode() {
		return subCatCode;
	}

	public void setSubCatCode(String subCatCode) {
		this.subCatCode = subCatCode;
	}

	public String getSubDivCode() {
		return subDivCode;
	}

	public void setSubDivCode(String subDivCode) {
		this.subDivCode = subDivCode;
	}

	public String getMainCatDesc() {
		return mainCatDesc;
	}

	public void setMainCatDesc(String mainCatDesc) {
		this.mainCatDesc = mainCatDesc;
	}

	public String getSubCatDesc() {
		return subCatDesc;
	}

	public void setSubCatDesc(String subCatDesc) {
		this.subCatDesc = subCatDesc;
	}

	public String getSubDivDesc() {
		return subDivDesc;
	}

	public void setSubDivDesc(String subDivDesc) {
		this.subDivDesc = subDivDesc;
	}


	public CategoryManagedBean() {

	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public int getOrganisationActiveIndex() {
		return organisationActiveIndex;
	}

	public void setOrganisationActiveIndex(int organisationActiveIndex) {
		this.organisationActiveIndex = organisationActiveIndex;
	}

	public boolean isNewCategory() {
		return newCategory;
	}

	public void setNewCategory(boolean newCategory) {
		this.newCategory = newCategory;
	}

	public PmCategoryL2 getPmCategoryL2() {
		return pmCategoryL2;
	}

	public void setPmCategoryL2(PmCategoryL2 pmCategoryL2) {
		this.pmCategoryL2 = pmCategoryL2;
	}

	public PmCategoryL3 getPmCategoryL3() {
		return pmCategoryL3;
	}

	public void setPmCategoryL3(PmCategoryL3 pmCategoryL3) {
		this.pmCategoryL3 = pmCategoryL3;
	}

	public List<PmParameterDesc> getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(List<PmParameterDesc> supplierType) {
		this.supplierType = supplierType;
	}

	public List<PmParameterDesc> getSpecialCategory() {
		return specialCategory;
	}

	public void setSpecialCategory(List<PmParameterDesc> specialCategory) {
		this.specialCategory = specialCategory;
	}

	public List<PmBasicCategory> getSelectedBasicCategory() {
		return selectedBasicCategory;
	}

	public void setSelectedBasicCategory(List<PmBasicCategory> selectedBasicCategory) {
		this.selectedBasicCategory = selectedBasicCategory;
	}

	public List<SelectItem> getSearchField() {
		return searchField;
	}

	public void setSearchField(List<SelectItem> searchField) {
		this.searchField = searchField;
	}

	public List<SelectItem> getSearchOperator() {
		return searchOperator;
	}

	public void setSearchOperator(List<SelectItem> searchOperator) {
		this.searchOperator = searchOperator;
	}

	public String[] getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String[] searchValue) {
		this.searchValue = searchValue;
	}

	public String[] getSearchFieldValue() {
		return searchFieldValue;
	}

	public void setSearchFieldValue(String[] searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}

	public String[] getSearchOperatorValue() {
		return searchOperatorValue;
	}

	public void setSearchOperatorValue(String[] searchOperatorValue) {
		this.searchOperatorValue = searchOperatorValue;
	}

	public List<PmCategoryL3> getSelectedLOV() {
		return selectedLOV;
	}

	public void setSelectedLOV(List<PmCategoryL3> selectedLOV) {
		this.selectedLOV = selectedLOV;
	}

	public List<PmBasicCategory> getPmBasicCategoryList() {
		return pmBasicCategoryList;
	}

	public void setPmBasicCategoryList(List<PmBasicCategory> pmBasicCategoryList) {
		this.pmBasicCategoryList = pmBasicCategoryList;
	}

	public PmBasicCategory[] getSelectedBasicCategory2() {
		return selectedBasicCategory2;
	}

	public void setSelectedBasicCategory2(PmBasicCategory[] selectedBasicCategory2) {
		this.selectedBasicCategory2 = selectedBasicCategory2;
	}



	

}
