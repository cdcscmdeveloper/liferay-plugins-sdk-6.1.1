/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.ct.CtApprover;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.bank.CtSupplierBank;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.CtSetting;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.FactoringCompany;
import my.ep.web.bean.ct.MtoDetails;
import my.ep.web.bean.ct.SettingDetails;
import my.ep.persistence.entity.ct.agreement.CtAgreement;

/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */
@ManagedBean(name ="contractDetailsManagedBean")
@SessionScoped
public class ContractDetailsManagedBean extends BaseManagedBean{

	private static Logger log = Logger.getLogger(ContractDetailsManagedBean.class);
	private static final long serialVersionUID = 1L;
	
	private String viewAgreement;
	
	private boolean chkSchedule;
	private boolean itemPricing = true;
	private boolean itemCeilingQuantity;
	private String showItem ="Item  Pricing";
	
	/*Table from supplier managements*/
	private SmSupplier smSupplier = new SmSupplier();
	private SmSupplierBank smSupplierBank = new SmSupplierBank();
	private FactoringCompany factoringCompany = new FactoringCompany();
	private CtSupplierBank ctSupplierBank = new CtSupplierBank();
	/*End*/
	
	private DualListModel<SettingDetails> settingDetails;  
	List<SettingDetails> source = new ArrayList<SettingDetails>();  
    List<SettingDetails> target = new ArrayList<SettingDetails>();  
    private List<CtDeduction> ctDeductionList = new ArrayList<CtDeduction>();
    private List<CtSchedule> ctScheduleList = new ArrayList<CtSchedule>();
    private List<CtProgressivePayment> ctProgressivePaymentList = new ArrayList<CtProgressivePayment>();
     
	private MtoDetails mtoDetails  = new MtoDetails();
	private ApproverSetting approverSetting = new ApproverSetting();
	private CheckBoxSelections checkBoxSelections = new  CheckBoxSelections();
	private CreateContractDetails createContractDetails = new CreateContractDetails();
	
	private CtSetting ctSetting = new CtSetting();
	private CtCircular ctCircular = new CtCircular();
	private CtContract ctContract = new CtContract();
	private CtSchedule ctSchedules = new CtSchedule();
	private CtApprover ctApprovers = new CtApprover();
	private CtDeduction ctDeduction = new CtDeduction();
	private CtAgreement ctAgreement = new CtAgreement();
	private CtFactoring ctFactoring = new  CtFactoring();
	private CtContractValue contractValues = new CtContractValue();
	private CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
	
	private List<CtDeductionClause> ctDeductionClause;
	private List<CtDeductionClause> addDeductionClause;
	private CtDeductionClause deductionClause = new CtDeductionClause();
	
	public  ContractDetailsManagedBean(){
		source.add(new SettingDetails("Khan", 10));  
	    source.add(new SettingDetails("Alam", 8));  
	    source.add(new SettingDetails("Kieyun", 7));  
	    source.add(new SettingDetails("Rajesh", 2));  
	    settingDetails = new DualListModel<SettingDetails>(source, target);  
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
	

	public CtContractValue getContractValues() {
		return contractValues;
	}

	public void setContractValues(CtContractValue contractValues) {
		this.contractValues = contractValues;
	}

	public CtCircular getCtCircular() {
		return ctCircular;
	}

	public void setCtCircular(CtCircular ctCircular) {
		this.ctCircular = ctCircular;
	}

	public CtAgreement getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(CtAgreement ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	public DualListModel<SettingDetails> getSettingDetails() {
		return settingDetails;
	}

	public void setSettingDetails(DualListModel<SettingDetails> settingDetails) {
		this.settingDetails = settingDetails;
	}

	public List<CtDeduction> getCtDeductionList() {
			return ctDeductionList;
	}

	public void setCtDeductionList(List<CtDeduction> ctDeductionList) {
		this.ctDeductionList = ctDeductionList;
	}

	public List<CtSchedule> getCtScheduleList() {
		if(ctScheduleList == null)
			ctScheduleList = new ArrayList<CtSchedule>();
		return ctScheduleList;
	}

	public void setCtScheduleList(List<CtSchedule> ctScheduleList) {
		this.ctScheduleList = ctScheduleList;
	}

	public List<CtProgressivePayment> getCtProgressivePaymentList() {
		if(ctProgressivePaymentList == null)
			ctProgressivePaymentList = new ArrayList<CtProgressivePayment>();
		return ctProgressivePaymentList;
	}

	public void setCtProgressivePaymentList(
			List<CtProgressivePayment> ctProgressivePaymentList) {
		this.ctProgressivePaymentList = ctProgressivePaymentList;
	}


	public CtSupplierBank getCtSupplierBank() {
		return ctSupplierBank;
	}

	public void setCtSupplierBank(CtSupplierBank ctSupplierBank) {
		this.ctSupplierBank = ctSupplierBank;
	}

	public CtFactoring getCtFactoring() {
		return ctFactoring;
	}

	public void setCtFactoring(CtFactoring ctFactoring) {
		this.ctFactoring = ctFactoring;
	}

	public CtSchedule getCtSchedules() {
		return ctSchedules;
	}

	public void setCtSchedules(CtSchedule ctSchedules) {
		this.ctSchedules = ctSchedules;
	}

	public CtApprover getCtApprovers() {
		return ctApprovers;
	}

	public void setCtApprovers(CtApprover ctApprovers) {
		this.ctApprovers = ctApprovers;
	}

	public List<CtDeductionClause> getCtDeductionClause() {
		return ctDeductionClause;
	}

	public void setCtDeductionClause(List<CtDeductionClause> ctDeductionClause) {
		this.ctDeductionClause = ctDeductionClause;
	}

	public List<CtDeductionClause> getAddDeductionClause() {
		if(addDeductionClause == null)
			addDeductionClause = new ArrayList<CtDeductionClause>();
		return addDeductionClause;
	}

	public void setAddDeductionClause(List<CtDeductionClause> addDeductionClause) {
		this.addDeductionClause = addDeductionClause;
	}

	public CtDeductionClause getDeductionClause() {
		return deductionClause;
	}

	public void setDeductionClause(CtDeductionClause deductionClause) {
		this.deductionClause = deductionClause;
	}

	public boolean isChkSchedule() {
		return chkSchedule;
	}

	public void setChkSchedule(boolean chkSchedule) {
		this.chkSchedule = chkSchedule;
	}
	/*Testing Start*/	
}
