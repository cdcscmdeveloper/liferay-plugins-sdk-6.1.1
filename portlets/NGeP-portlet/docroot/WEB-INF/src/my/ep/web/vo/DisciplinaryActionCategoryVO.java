package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.sm.SmAppl;

public class DisciplinaryActionCategoryVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7811736505109743175L;
	
	private String categoryL1Code; 
	private String categoryL2Code;
	private String categoryL3Code;
	private String categoryCode;
	
	public String getCategoryL1Code() {
		return categoryL1Code;
	}
	public void setCategoryL1Code(String categoryL1Code) {
		this.categoryL1Code = categoryL1Code;
	}
	
	public String getCategoryL2Code() {
		return categoryL2Code;
	}
	public void setCategoryL2Code(String categoryL2Code) {
		this.categoryL2Code = categoryL2Code;
	}
	
	public String getCategoryL3Code() {
		return categoryL3Code;
	}
	public void setCategoryL3Code(String categoryL3Code) {
		this.categoryL3Code = categoryL3Code;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	
}
