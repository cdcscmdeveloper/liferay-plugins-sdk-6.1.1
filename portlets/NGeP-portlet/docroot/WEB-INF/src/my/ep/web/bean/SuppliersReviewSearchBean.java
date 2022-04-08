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
package my.ep.web.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmState;

/**
 * The Class SuppliersReviewSearchBean.
 */
public class SuppliersReviewSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private BigDecimal paidUpCapitalAmtFrom;
	private BigDecimal paidUpCapitalAmtTo;
	private boolean allStates;
	private List<PmState> statesList;
	private List<PmState>  selectedStatesList;
	private List<PmCategoryL3> categoryList;
	private List<Long> categoryIdList;
	private int noOfSuppliers;
	private String stateValue;
	private boolean categoryState;
	private Long[] dropDownStatesIdList;	
	
	/**
	 * @return the selectedStatesList
	 */
	public List<PmState> getSelectedStatesList() {
		return selectedStatesList;
	}
	/**
	 * @param selectedStatesList the selectedStatesList to set
	 */
	public void setSelectedStatesList(List<PmState> selectedStatesList) {
		this.selectedStatesList = selectedStatesList;
	}
	/**
	 * @return the categoryState
	 */
	public boolean isCategoryState() {
		return categoryState;
	}
	/**
	 * @param categoryState the categoryState to set
	 */
	public void setCategoryState(boolean categoryState) {
		this.categoryState = categoryState;
	}
	/**
	 * @return the stateValue
	 */
	public String getStateValue() {
		return stateValue;
	}
	/**
	 * @param stateValue the stateValue to set
	 */
	public void setStateValue(String stateValue) {
		this.stateValue = stateValue;
	}
	/**
	 * @return the noOfSuppliers
	 */
	public int getNoOfSuppliers() {
		return noOfSuppliers;
	}
	/**
	 * @param noOfSuppliers the noOfSuppliers to set
	 */
	public void setNoOfSuppliers(int noOfSuppliers) {
		this.noOfSuppliers = noOfSuppliers;
	}
	/**
	 * @return the paidUpCapitalAmtFrom
	 */
	public BigDecimal getPaidUpCapitalAmtFrom() {
		return paidUpCapitalAmtFrom;
	}
	/**
	 * @param paidUpCapitalAmtFrom the paidUpCapitalAmtFrom to set
	 */
	public void setPaidUpCapitalAmtFrom(BigDecimal paidUpCapitalAmtFrom) {
		this.paidUpCapitalAmtFrom = paidUpCapitalAmtFrom;
	}
	/**
	 * @return the paidUpCapitalAmtTo
	 */
	public BigDecimal getPaidUpCapitalAmtTo() {
		return paidUpCapitalAmtTo;
	}
	/**
	 * @param paidUpCapitalAmtTo the paidUpCapitalAmtTo to set
	 */
	public void setPaidUpCapitalAmtTo(BigDecimal paidUpCapitalAmtTo) {
		this.paidUpCapitalAmtTo = paidUpCapitalAmtTo;
	}
	/**
	 * @return the allStates
	 */
	public boolean isAllStates() {
		return allStates;
	}
	/**
	 * @param allStates the allStates to set
	 */
	public void setAllStates(boolean allStates) {
		this.allStates = true;
	}
	/**
	 * @return the statesList
	 */
	public List<PmState> getStatesList() {
		return statesList;
	}
	/**
	 * @param statesList the statesList to set
	 */
	public void setStatesList(List<PmState> statesList) {
		this.statesList = statesList;
	}
	/**
	 * @return the categoryList
	 */
	public List<PmCategoryL3> getCategoryList() {
		return categoryList;
	}
	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(List<PmCategoryL3> categoryList) {
		this.categoryList = categoryList;
	}
	/**
	 * @return the categoryIdList
	 */
	public List<Long> getCategoryIdList() {
		return categoryIdList;
	}
	/**
	 * @param categoryIdList the categoryIdList to set
	 */
	public void setCategoryIdList(List<Long> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}
	/**
	 * @return the dropDownStatesIdList
	 */
	public Long[] getDropDownStatesIdList() {
		return dropDownStatesIdList;
	}
	/**
	 * @param dropDownStatesIdList the dropDownStatesIdList to set
	 */
	public void setDropDownStatesIdList(Long[] dropDownStatesIdList) {
		this.dropDownStatesIdList = dropDownStatesIdList;
	}
	
	

}
