/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.bean.report;

import java.io.Serializable;
import java.util.Date;

import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

/**
 * The Class FinancialTableSearchBean.
 * 
 * @author Garikapati Mangaiah.
 */
public class ScReportFieldsSearchBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String[] allCheckboxValue;
	private boolean allCheckboxEditView;
	private String[] quotationCheckboxValue;
	private boolean quotationCheckboxEditView;
	private String[] tenderCheckboxValue;
	private boolean tenderCheckboxEditView;
	private String[] restrictedTndrCheckboxValue;
	private boolean restrictedTndrCheckboxEditView;
	private String quotationNumber;
	private String scStatus;
	private String scCreatedFromMonth;
	private String scCreatedFromYear;
	private String scCreatedToMonth;
	private String scCreatedToYear;
	private Date scPublishFromDate;
	private Date scPublishToDate;
	private String scDeskOfficer;
	private String scPtj;
	//#21692 - Start
	private String scPtjName;
	private PmOrgProfile ptjProfile;
	private SelectItem doSelectItem;
	//#21692 - End
	/**
	 * @return the allCheckboxValue
	 */
	public String[] getAllCheckboxValue() {
		return allCheckboxValue;
	}
	/**
	 * @param allCheckboxValue the allCheckboxValue to set
	 */
	public void setAllCheckboxValue(String[] allCheckboxValue) {
		this.allCheckboxValue = allCheckboxValue;
	}
	/**
	 * @return the allCheckboxEditView
	 */
	public boolean isAllCheckboxEditView() {
		return allCheckboxEditView;
	}
	/**
	 * @param allCheckboxEditView the allCheckboxEditView to set
	 */
	public void setAllCheckboxEditView(boolean allCheckboxEditView) {
		this.allCheckboxEditView = allCheckboxEditView;
	}
	/**
	 * @return the quotationCheckboxValue
	 */
	public String[] getQuotationCheckboxValue() {
		return quotationCheckboxValue;
	}
	/**
	 * @param quotationCheckboxValue the quotationCheckboxValue to set
	 */
	public void setQuotationCheckboxValue(String[] quotationCheckboxValue) {
		this.quotationCheckboxValue = quotationCheckboxValue;
	}
	/**
	 * @return the quotationCheckboxEditView
	 */
	public boolean isQuotationCheckboxEditView() {
		return quotationCheckboxEditView;
	}
	/**
	 * @param quotationCheckboxEditView the quotationCheckboxEditView to set
	 */
	public void setQuotationCheckboxEditView(boolean quotationCheckboxEditView) {
		this.quotationCheckboxEditView = quotationCheckboxEditView;
	}
	/**
	 * @return the tenderCheckboxValue
	 */
	public String[] getTenderCheckboxValue() {
		return tenderCheckboxValue;
	}
	/**
	 * @param tenderCheckboxValue the tenderCheckboxValue to set
	 */
	public void setTenderCheckboxValue(String[] tenderCheckboxValue) {
		this.tenderCheckboxValue = tenderCheckboxValue;
	}
	/**
	 * @return the tenderCheckboxEditView
	 */
	public boolean isTenderCheckboxEditView() {
		return tenderCheckboxEditView;
	}
	/**
	 * @param tenderCheckboxEditView the tenderCheckboxEditView to set
	 */
	public void setTenderCheckboxEditView(boolean tenderCheckboxEditView) {
		this.tenderCheckboxEditView = tenderCheckboxEditView;
	}
	/**
	 * @return the restrictedTndrCheckboxValue
	 */
	public String[] getRestrictedTndrCheckboxValue() {
		return restrictedTndrCheckboxValue;
	}
	/**
	 * @param restrictedTndrCheckboxValue the restrictedTndrCheckboxValue to set
	 */
	public void setRestrictedTndrCheckboxValue(String[] restrictedTndrCheckboxValue) {
		this.restrictedTndrCheckboxValue = restrictedTndrCheckboxValue;
	}
	/**
	 * @return the restrictedTndrCheckboxEditView
	 */
	public boolean isRestrictedTndrCheckboxEditView() {
		return restrictedTndrCheckboxEditView;
	}
	/**
	 * @param restrictedTndrCheckboxEditView the restrictedTndrCheckboxEditView to set
	 */
	public void setRestrictedTndrCheckboxEditView(
			boolean restrictedTndrCheckboxEditView) {
		this.restrictedTndrCheckboxEditView = restrictedTndrCheckboxEditView;
	}
	/**
	 * @return the quotationNumber
	 */
	public String getQuotationNumber() {
		return quotationNumber;
	}
	/**
	 * @param quotationNumber the quotationNumber to set
	 */
	public void setQuotationNumber(String quotationNumber) {
		this.quotationNumber = quotationNumber;
	}
	/**
	 * @return the scStatus
	 */
	public String getScStatus() {
		return scStatus;
	}
	/**
	 * @param scStatus the scStatus to set
	 */
	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}
	/**
	 * @return the scCreatedFromMonth
	 */
	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}
	/**
	 * @param scCreatedFromMonth the scCreatedFromMonth to set
	 */
	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}
	/**
	 * @return the scCreatedFromYear
	 */
	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}
	/**
	 * @param scCreatedFromYear the scCreatedFromYear to set
	 */
	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}
	/**
	 * @return the scCreatedToMonth
	 */
	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}
	/**
	 * @param scCreatedToMonth the scCreatedToMonth to set
	 */
	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}
	/**
	 * @return the scCreatedToYear
	 */
	public String getScCreatedToYear() {
		return scCreatedToYear;
	}
	/**
	 * @param scCreatedToYear the scCreatedToYear to set
	 */
	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}
	/**
	 * @return the scPublishFromDate
	 */
	public Date getScPublishFromDate() {
		return scPublishFromDate;
	}
	/**
	 * @param scPublishFromDate the scPublishFromDate to set
	 */
	public void setScPublishFromDate(Date scPublishFromDate) {
		this.scPublishFromDate = scPublishFromDate;
	}
	/**
	 * @return the scPublishToDate
	 */
	public Date getScPublishToDate() {
		return scPublishToDate;
	}
	/**
	 * @param scPublishToDate the scPublishToDate to set
	 */
	public void setScPublishToDate(Date scPublishToDate) {
		this.scPublishToDate = scPublishToDate;
	}
	/**
	 * @return the scDeskOfficer
	 */
	public String getScDeskOfficer() {
		return scDeskOfficer;
	}
	/**
	 * @param scDeskOfficer the scDeskOfficer to set
	 */
	public void setScDeskOfficer(String scDeskOfficer) {
		this.scDeskOfficer = scDeskOfficer;
	}
	/**
	 * @return the scPtj
	 */
	public String getScPtj() {
		return scPtj;
	}
	/**
	 * @param scPtj the scPtj to set
	 */
	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}
	
	//#21692 - Start
	public String getScPtjName() {
		return scPtjName;
	}
	public void setScPtjName(String scPtjName) {
		this.scPtjName = scPtjName;
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * @return the ptjProfile
	 */
	public PmOrgProfile getPtjProfile() {
		return ptjProfile;
	}
	/**
	 * @param ptjProfile the ptjProfile to set
	 */
	public void setPtjProfile(PmOrgProfile ptjProfile) {
		this.ptjProfile = ptjProfile;
	}
	//#21692 - End
	
	//#21692 - Start
	/**
	 * @return the doSelectItem
	 */
	public SelectItem getDoSelectItem() {
		return doSelectItem;
	}
	/**
	 * @param doSelectItem the doSelectItem to set
	 */
	public void setDoSelectItem(SelectItem doSelectItem) {
		this.doSelectItem = doSelectItem;
	}
	//#21692 - End
	
	
	
	
	
	

}
