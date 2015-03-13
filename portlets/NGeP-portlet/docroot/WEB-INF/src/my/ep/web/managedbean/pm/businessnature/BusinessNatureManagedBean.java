package my.ep.web.managedbean.pm.businessnature;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
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

	private PmBusinessNature pmBusinessNature;

	private PmBusinessNature parentBusinessNature;

	private List<PmBusinessNature> childNatures;

	public Long getCategoryLvl() {
		return categoryLvl;
	}

	public void setCategoryLvl(Long categoryLvl) {
		this.categoryLvl = categoryLvl;
	}

	public PmBusinessNature getPmBusinessNature() {
		return pmBusinessNature;
	}

	public void setPmBusinessNature(PmBusinessNature pmBusinessNature) {
		this.pmBusinessNature = pmBusinessNature;
	}

	public String getSecLevelCode() {
		return secLevelCode;
	}

	public void setSecLevelCode(String secLevelCode) {
		this.secLevelCode = secLevelCode;
	}

	public String getSecLevelName() {
		return secLevelName;
	}

	public void setSecLevelName(String secLevelName) {
		this.secLevelName = secLevelName;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public PmBusinessNature getParentBusinessNature() {
		return parentBusinessNature;
	}

	public void setParentBusinessNature(PmBusinessNature parentBusinessNature) {
		this.parentBusinessNature = parentBusinessNature;
	}

	public List<PmBusinessNature> getChildNatures() {
		return childNatures;
	}

	public void setChildNatures(List<PmBusinessNature> childNatures) {
		this.childNatures = childNatures;
	}

	public String getDivLevelCode() {
		return divLevelCode;
	}

	public void setDivLevelCode(String divLevelCode) {
		this.divLevelCode = divLevelCode;
	}

	public String getDivLevelName() {
		return divLevelName;
	}

	public void setDivLevelName(String divLevelName) {
		this.divLevelName = divLevelName;
	}

	public String getGroupLevelCode() {
		return groupLevelCode;
	}

	public void setGroupLevelCode(String groupLevelCode) {
		this.groupLevelCode = groupLevelCode;
	}

	public String getGroupLevelName() {
		return groupLevelName;
	}

	public void setGroupLevelName(String groupLevelName) {
		this.groupLevelName = groupLevelName;
	}

	public String getClassLevelCode() {
		return classLevelCode;
	}

	public void setClassLevelCode(String classLevelCode) {
		this.classLevelCode = classLevelCode;
	}

	public String getClassLevelName() {
		return classLevelName;
	}

	public void setClassLevelName(String classLevelName) {
		this.classLevelName = classLevelName;
	}

	public String getItemLevelCode() {
		return itemLevelCode;
	}

	public void setItemLevelCode(String itemLevelCode) {
		this.itemLevelCode = itemLevelCode;
	}

	public String getItemLevelName() {
		return itemLevelName;
	}

	public void setItemLevelName(String itemLevelName) {
		this.itemLevelName = itemLevelName;
	}

	public boolean isBrandNew() {
		return brandNew;
	}

	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
	}

	public boolean isNewcategory() {
		return newcategory;
	}

	public void setNewcategory(boolean newcategory) {
		this.newcategory = newcategory;
	}

}
