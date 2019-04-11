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

package my.ep.web.managedbean.sc.noofsuppliersreview;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.dto.sc.suppliersreview.SuppliersReviewResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.web.bean.SuppliersReviewSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The Class SuppliersReviewManagedBean.
 */
@ManagedBean(name = "suppliersReviewManagedBean")
@SessionScoped
public class SuppliersReviewManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	
	private SuppliersReviewSearchBean suppliersReviewSearchBean;
	
	private Long status;

	private boolean isLazy;
	
	private Long noOfSuppliers;
	
	private SuppliersReviewResDTO suppliersReviewResDTO;
	
	private List<PmCategoryL3> pmCategoryL3List;
	
	private boolean disabled;
	
	private boolean allStates;
	
	private List<ScQtCategory> qtCategoryList;
	private List<ScQtCategory> selectedCategoryList;
	
		/**
	 * @return the suppliersReviewSearchBean
	 */
	public SuppliersReviewSearchBean getSuppliersReviewSearchBean() {
		return suppliersReviewSearchBean;
	}
	/**
	 * @param suppliersReviewSearchBean the suppliersReviewSearchBean to set
	 */
	public void setSuppliersReviewSearchBean(
			SuppliersReviewSearchBean suppliersReviewSearchBean) {
		this.suppliersReviewSearchBean = suppliersReviewSearchBean;
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
	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}
	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	/**
	 * @return the suppliersReviewResDTO
	 */
	public SuppliersReviewResDTO getSuppliersReviewResDTO() {
		return suppliersReviewResDTO;
	}
	/**
	 * @param suppliersReviewResDTO the suppliersReviewResDTO to set
	 */
	public void setSuppliersReviewResDTO(SuppliersReviewResDTO suppliersReviewResDTO) {
		this.suppliersReviewResDTO = suppliersReviewResDTO;
	}
	/**
	 * @return the noOfSuppliers
	 */
	public Long getNoOfSuppliers() {
		return noOfSuppliers;
	}
	/**
	 * @param noOfSuppliers the noOfSuppliers to set
	 */
	public void setNoOfSuppliers(Long noOfSuppliers) {
		this.noOfSuppliers = noOfSuppliers;
	}
	/**
	 * @return the pmCategoryL3List
	 */
	public List<PmCategoryL3> getPmCategoryL3List() {
		return pmCategoryL3List;
	}
	/**
	 * @param pmCategoryL3List the pmCategoryL3List to set
	 */
	public void setPmCategoryL3List(List<PmCategoryL3> pmCategoryL3List) {
		this.pmCategoryL3List = pmCategoryL3List;
	}
	/**
	 * @return the disabled
	 */
	public boolean isDisabled() {
		return disabled;
	}
	/**
	 * @param disabled the disabled to set
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
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
		this.allStates = allStates;
	}
	/**
	 * @return the qtCategoryList
	 */
	public List<ScQtCategory> getQtCategoryList() {
		return qtCategoryList;
	}
	/**
	 * @param qtCategoryList the qtCategoryList to set
	 */
	public void setQtCategoryList(List<ScQtCategory> qtCategoryList) {
		this.qtCategoryList = qtCategoryList;
	}
	/**
	 * @return the selectedCategoryList
	 */
	public List<ScQtCategory> getSelectedCategoryList() {
		return selectedCategoryList;
	}
	/**
	 * @param selectedCategoryList the selectedCategoryList to set
	 */
	public void setSelectedCategoryList(List<ScQtCategory> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}
	
	
}
