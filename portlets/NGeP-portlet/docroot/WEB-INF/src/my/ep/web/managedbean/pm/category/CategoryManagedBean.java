package my.ep.web.managedbean.pm.category;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
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

	

}
