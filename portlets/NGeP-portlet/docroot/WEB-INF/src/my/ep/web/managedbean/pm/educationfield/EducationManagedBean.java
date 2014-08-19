package my.ep.web.managedbean.pm.educationfield;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
	
	private List<PmEduField> eduList;	
	
	private List<PmEduField> eduListSelect;
	
	private List<PmEduFieldRestr> eduRestrList;
	
	private List<PmEduFieldRestr> eduRestrSelectList;
	
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

	public List<PmEduFieldRestr> getEduRestrSelectList() {
		return eduRestrSelectList;
	}

	public void setEduRestrSelectList(List<PmEduFieldRestr> eduRestrSelectList) {
		this.eduRestrSelectList = eduRestrSelectList;
	}

	public List<PmEduField> getEduList() {
		return eduList;
	}

	public void setEduList(List<PmEduField> eduList) {
		this.eduList = eduList;
	}

	public List<PmEduField> getEduListSelect() {
		return eduListSelect;
	}

	public void setEduListSelect(List<PmEduField> eduListSelect) {
		this.eduListSelect = eduListSelect;
	}			
	
}
