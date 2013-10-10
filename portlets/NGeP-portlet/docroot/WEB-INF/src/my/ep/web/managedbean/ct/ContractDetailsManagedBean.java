/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSetting;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.FactoringCompany;
import my.ep.web.bean.ct.MtoDetails;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */

@ManagedBean(name="contractDetailsManagedBean")
@SessionScoped
public class ContractDetailsManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value="#{viewAgreementbeans}")
	private String viewAgreement;
	
	private String showItem ="Item  Pricing";
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	
	/*Table from supplier managements*/
	private SmSupplier smSupplier = new SmSupplier();
	private SmSupplierBank smSupplierBank = new SmSupplierBank();
	private FactoringCompany factoringCompany = new FactoringCompany();
	/*End*/
	
	private MtoDetails mtoDetails  = new MtoDetails();
	private ApproverSetting approverSetting = new ApproverSetting();
	private CheckBoxSelections checkBoxSelections = new  CheckBoxSelections();
	private CreateContractDetails createContractDetails = new CreateContractDetails();
	
	private CtSetting ctSetting = new CtSetting();
	private CtContract ctContract = new CtContract();
	private CtDeduction ctDeduction = new CtDeduction();
	private CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
	
	public  ContractDetailsManagedBean(){
		
	}
	
	public String getShowItem() {
		return showItem;
	}

	public void setShowItem(String showItem) {
		this.showItem = showItem;
	}
	
	public boolean isItemPricing() {
		return itemPricing;
	}

	public void setItemPricing(boolean itemPricing) {
		this.itemPricing = itemPricing;
	}

	public boolean isItemCeilingQuantity() {
		return itemCeilingQuantity;
	}

	public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
		this.itemCeilingQuantity = itemCeilingQuantity;
	}

	public String getViewAgreement() {
		return viewAgreement;
	}
	public void setViewAgreement(String viewAgreement) {
		this.viewAgreement = viewAgreement;
	}
	
	public CtContract getCtContract() {
		return ctContract;
	}
	
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}
	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}
	
	public CtDeduction getCtDeduction() {
		return ctDeduction;
	}
	public void setCtDeduction(CtDeduction ctDeduction) {
		this.ctDeduction = ctDeduction;
	}
	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}
	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}
	public CreateContractDetails getCreateContractDetails() {
		return createContractDetails;
	}
	public void setCreateContractDetails(CreateContractDetails createContractDetails) {
		this.createContractDetails = createContractDetails;
	}

	public CtProgressivePayment getCtProgressivePayment() {
		return ctProgressivePayment;
	}

	public void setCtProgressivePayment(CtProgressivePayment ctProgressivePayment) {
		this.ctProgressivePayment = ctProgressivePayment;
	}

	public CtSetting getCtSetting() {
		return ctSetting;
	}

	public void setCtSetting(CtSetting ctSetting) {
		this.ctSetting = ctSetting;
	}

	public MtoDetails getMtoDetails() {
		return mtoDetails;
	}

	public void setMtoDetails(MtoDetails mtoDetails) {
		this.mtoDetails = mtoDetails;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmSupplierBank getSmSupplierBank() {
		return smSupplierBank;
	}

	public void setSmSupplierBank(SmSupplierBank smSupplierBank) {
		this.smSupplierBank = smSupplierBank;
	}

	public FactoringCompany getFactoringCompany() {
		return factoringCompany;
	}

	public void setFactoringCompany(FactoringCompany factoringCompany) {
		this.factoringCompany = factoringCompany;
	}

	
	/*Testing Start*/
	
}
