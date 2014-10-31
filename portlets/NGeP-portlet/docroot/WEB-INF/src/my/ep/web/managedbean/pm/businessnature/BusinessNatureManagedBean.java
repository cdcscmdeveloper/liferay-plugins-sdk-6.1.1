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
	 * business nature
	 * search variable
	 */	
	private Long categoryLvl;
	
	private String secLevelCode;
	
	private String secLevelName;

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
	
}
