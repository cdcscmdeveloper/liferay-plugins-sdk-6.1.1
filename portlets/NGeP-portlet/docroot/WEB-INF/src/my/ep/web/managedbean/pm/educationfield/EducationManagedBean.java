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
package my.ep.web.managedbean.pm.educationfield;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmEduFieldRestr;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc
/**
 * The Class EducationManagedBean.
 * 
 * @author DK
 */
@ManagedBean(name = "educationManagedBean")
@SessionScoped
public class EducationManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The pm edu field. */
	private PmEduField pmEduField;

	/** The edu field. */
	private List<PmEduField> eduField;

	/** The pm edu field restr. */
	private List<PmEduFieldRestr> pmEduFieldRestr;

	/** The field codes. */
	private String fieldCodes;

	/** The field names. */
	private String fieldNames;

	/** The status. */
	private Long status;

	/** The edu field retr. */
	private PmEduFieldRestr eduFieldRetr;

	/** The edu restr list. */
	private List<PmEduFieldRestr> eduRestrList;

	/** The edu restr select list. */
	private List<PmCategoryL3> eduRestrSelectList;

	/** The edu restr delete list. */
	private List<PmEduFieldRestr> eduRestrDeleteList;

	/** The is lazy. */
	private boolean isLazy = false;

	/**
	 * Gets the pm edu field.
	 * 
	 * @return the pmEduField
	 */
	public PmEduField getPmEduField() {
		return pmEduField;
	}

	/**
	 * Sets the pm edu field.
	 * 
	 * @param pmEduField
	 *            the pmEduField to set
	 */
	public void setPmEduField(PmEduField pmEduField) {
		this.pmEduField = pmEduField;
	}

	/**
	 * Gets the edu field.
	 * 
	 * @return the eduField
	 */
	public List<PmEduField> getEduField() {
		return eduField;
	}

	/**
	 * Sets the edu field.
	 * 
	 * @param eduField
	 *            the eduField to set
	 */
	public void setEduField(List<PmEduField> eduField) {
		this.eduField = eduField;
	}

	/**
	 * Gets the pm edu field restr.
	 * 
	 * @return the pmEduFieldRestr
	 */
	public List<PmEduFieldRestr> getPmEduFieldRestr() {
		return pmEduFieldRestr;
	}

	/**
	 * Sets the pm edu field restr.
	 * 
	 * @param pmEduFieldRestr
	 *            the pmEduFieldRestr to set
	 */
	public void setPmEduFieldRestr(List<PmEduFieldRestr> pmEduFieldRestr) {
		this.pmEduFieldRestr = pmEduFieldRestr;
	}

	/**
	 * Gets the field codes.
	 * 
	 * @return the pmEduField
	 */
	public String getFieldCodes() {
		return fieldCodes;
	}

	/**
	 * Sets the field codes.
	 * 
	 * @param fieldCodes
	 *            the new field codes
	 */
	public void setFieldCodes(String fieldCodes) {
		this.fieldCodes = fieldCodes;
	}

	/**
	 * Gets the field names.
	 * 
	 * @return the fieldName
	 */
	public String getFieldNames() {
		return fieldNames;
	}

	/**
	 * Sets the field names.
	 * 
	 * @param fieldNames
	 *            the new field names
	 */
	public void setFieldNames(String fieldNames) {
		this.fieldNames = fieldNames;
	}

	/**
	 * Gets the edu field retr.
	 * 
	 * @return the edu field retr
	 */
	public PmEduFieldRestr getEduFieldRetr() {
		return eduFieldRetr;
	}

	/**
	 * Sets the edu field retr.
	 * 
	 * @param eduFieldRetr
	 *            the new edu field retr
	 */
	public void setEduFieldRetr(PmEduFieldRestr eduFieldRetr) {
		this.eduFieldRetr = eduFieldRetr;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}

	/**
	 * Gets the edu restr list.
	 * 
	 * @return the edu restr list
	 */
	public List<PmEduFieldRestr> getEduRestrList() {
		return eduRestrList;
	}

	/**
	 * Sets the edu restr list.
	 * 
	 * @param eduRestrList
	 *            the new edu restr list
	 */
	public void setEduRestrList(List<PmEduFieldRestr> eduRestrList) {
		this.eduRestrList = eduRestrList;
	}

	/**
	 * Gets the edu restr select list.
	 * 
	 * @return the edu restr select list
	 */
	public List<PmCategoryL3> getEduRestrSelectList() {
		return eduRestrSelectList;
	}

	/**
	 * Sets the edu restr select list.
	 * 
	 * @param eduRestrSelectList
	 *            the new edu restr select list
	 */
	public void setEduRestrSelectList(List<PmCategoryL3> eduRestrSelectList) {
		this.eduRestrSelectList = eduRestrSelectList;
	}

	/**
	 * Gets the edu restr delete list.
	 * 
	 * @return the edu restr delete list
	 */
	public List<PmEduFieldRestr> getEduRestrDeleteList() {
		return eduRestrDeleteList;
	}

	/**
	 * Sets the edu restr delete list.
	 * 
	 * @param eduRestrDeleteList
	 *            the new edu restr delete list
	 */
	public void setEduRestrDeleteList(List<PmEduFieldRestr> eduRestrDeleteList) {
		this.eduRestrDeleteList = eduRestrDeleteList;
	}

	/**
	 * Checks if is lazy.
	 * 
	 * @return true, if is lazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the new lazy
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

}
