/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

import my.ep.biz.bean.search.SearchFilterBean;
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
import my.ep.persistence.entity.ct.terminate.CtTerminationClause;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
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
import my.ep.persistence.entity.ct.assoc.CtAssociation;

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
	
	private String taskId;
	private String[] outComes;
	private int selectedIndex;
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
	
	/*Field For Search Value*/
	private Date effDate;
	private Date expDate;
	private String qtLoaNo;
	private String contractNo;
	private String projectName;
	private String contractName;
	private String projectDescription;
	private CtAssociation association = new CtAssociation();
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	private List<CtContract> contractList = new ArrayList<CtContract>();
	private List<CtContract> associateSearchList = new ArrayList<CtContract>();
	private List<CtAssociation> associationList = new ArrayList<CtAssociation>();
	
	/*Termination*/
	private boolean terminateNotice;
	private String TerminateRecommedType;
	private boolean terminateWarningLetter;
	private boolean terminateRejectLetter;
	private List<CtTerminationClause> terminationClauseList;
	private List<CtTerminationReason> terminateReasonList;
	private CtTerminationReason terminationReason = new  CtTerminationReason();
	private CtTerminationClause terminationClause = new CtTerminationClause();
	
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

	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}

	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}

	public List<CtContract> getContractList() {
		return contractList;
	}

	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public String getQtLoaNo() {
		return qtLoaNo;
	}

	public void setQtLoaNo(String qtLoaNo) {
		this.qtLoaNo = qtLoaNo;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getEffDate() {
		return effDate;
	}

	public void setEffDate(Date effDate) {
		this.effDate = effDate;
	}

	public Date getExpDate() {
		return expDate;
	}

	public void setExpDate(Date expDate) {
		this.expDate = expDate;
	}

	public List<CtContract> getAssociateSearchList() {
		return associateSearchList;
	}

	public void setAssociateSearchList(List<CtContract> associateSearchList) {
		this.associateSearchList = associateSearchList;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectDescription() {
		return projectDescription;
	}

	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}

	public CtAssociation getAssociation() {
		return association;
	}

	public void setAssociation(CtAssociation association) {
		this.association = association;
	}

	public List<CtAssociation> getAssociationList() {
		return associationList;
	}

	public void setAssociationList(List<CtAssociation> associationList) {
		this.associationList = associationList;
	}

	public String getTerminateRecommedType() {
		return TerminateRecommedType;
	}

	public void setTerminateRecommedType(String terminateRecommedType) {
		TerminateRecommedType = terminateRecommedType;
	}

	public boolean isTerminateWarningLetter() {
		return terminateWarningLetter;
	}

	public void setTerminateWarningLetter(boolean terminateWarningLetter) {
		this.terminateWarningLetter = terminateWarningLetter;
	}

	public boolean isTerminateNotice() {
		return terminateNotice;
	}

	public void setTerminateNotice(boolean terminateNotice) {
		this.terminateNotice = terminateNotice;
	}

	public boolean isTerminateRejectLetter() {
		return terminateRejectLetter;
	}

	public void setTerminateRejectLetter(boolean terminateRejectLetter) {
		this.terminateRejectLetter = terminateRejectLetter;
	}

	public CtTerminationReason getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(CtTerminationReason terminationReason) {
		this.terminationReason = terminationReason;
	}

	public List<CtTerminationReason> getTerminateReasonList() {
		if(terminateReasonList == null)
			terminateReasonList = new ArrayList<CtTerminationReason>();
		return terminateReasonList;
	}

	public void setTerminateReasonList(List<CtTerminationReason> terminateReasonList) {
		this.terminateReasonList = terminateReasonList;
	}

	public List<CtTerminationClause> getTerminationClauseList() {
		if(terminationClauseList == null)
			terminationClauseList = new ArrayList<CtTerminationClause>();
		return terminationClauseList;
	}

	public void setTerminationClauseList(
			List<CtTerminationClause> terminationClauseList) {
		this.terminationClauseList = terminationClauseList;
	}

	public CtTerminationClause getTerminationClause() {
		return terminationClause;
	}

	public void setTerminationClause(CtTerminationClause terminationClause) {
		this.terminationClause = terminationClause;
	}
	
	/*Testing Start*/	
}
