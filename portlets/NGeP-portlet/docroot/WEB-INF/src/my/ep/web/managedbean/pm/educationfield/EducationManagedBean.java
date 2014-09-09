package my.ep.web.managedbean.pm.educationfield;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduFieldRestr;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "educationManagedBean")
@SessionScoped
public class EducationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PmEduField pmEduField;
	
	private String fieldCodes;
	
	private String fieldNames;
	
	private Long status;
	
	private List<PmEduFieldRestr> eduRestrList;
	
	private List <PmCategoryL3> eduRestrSelectList;
	
	private List<PmEduFieldRestr> eduRestrDeleteList;
	
	/**
	 * @return the pmEduField
	 */
	public PmEduField getPmEduField() {
		return pmEduField;
	}

	/**
	 * @param pmEduField the pmEduField to set
	 */
	public void setPmEduField(PmEduField pmEduField) {
		this.pmEduField = pmEduField;
	}

	/**
	 * @return the pmEduField
	 */
	public String getFieldCodes() {
		return fieldCodes;
	}

	/**
	 * @param pmEduField the pmEduField to set
	 */
	public void setFieldCodes(String fieldCodes) {
		this.fieldCodes = fieldCodes;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldNames() {
		return fieldNames;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldNames(String fieldNames) {
		this.fieldNames = fieldNames;
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

	public List<PmEduFieldRestr> getEduRestrList() {
		return eduRestrList;
	}

	public void setEduRestrList(List<PmEduFieldRestr> eduRestrList) {
		this.eduRestrList = eduRestrList;
	}

	public List<PmCategoryL3> getEduRestrSelectList() {
		return eduRestrSelectList;
	}

	public void setEduRestrSelectList(List<PmCategoryL3> eduRestrSelectList) {
		this.eduRestrSelectList = eduRestrSelectList;
	}

	public List<PmEduFieldRestr> getEduRestrDeleteList() {
		return eduRestrDeleteList;
	}

	public void setEduRestrDeleteList(List<PmEduFieldRestr> eduRestrDeleteList) {
		this.eduRestrDeleteList = eduRestrDeleteList;
	}
	
	
}
