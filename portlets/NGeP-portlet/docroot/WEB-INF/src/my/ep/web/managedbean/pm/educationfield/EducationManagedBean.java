package my.ep.web.managedbean.pm.educationfield;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmEduField;
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
	

}
