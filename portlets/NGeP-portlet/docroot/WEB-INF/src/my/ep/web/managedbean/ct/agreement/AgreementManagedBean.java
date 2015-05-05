/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct.agreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.tpl.doc.TplDocSection;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;


/**
 * Managed Bean For Contract Agreement
 * 
 * @author Alam
 * @since 17/09/2013	 
 */
@ManagedBean(name ="agreementManagedBean")
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String[] action;	
	private String taskId;
	private String taskName;
	private String agreementType = "";
	private String fileDirectory;
	private String textBeforeSign;
	private String buttonAction;
	private Long departmentProfileId;
	private int selectedTabIndex;
	private double performanceBondRuleAmt;
	private boolean uploadFinalAgreement;
	private boolean uploadStampedAndSignedPage;
	private boolean validateWitness;
	private boolean autoUpdate = true;
	private List<CtAgreementParty> ctAgencySigner;
	private List<CtAgreementParty> ctSupplierSigner;
	private List<CtAgreementParty> ctSupplierWitness;
	private List<CtAgreementParty> ctSignAgencyWitness;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<String> agencyOfficerIcNo;
	private List<String> agencyWitnessIcNo;
	private List<String> supplierOfficerIcNo;
	private List<String> supplierWitnessIcNo;
	private DualListModel<String> approverModel;
	private Map<String, PmUser> userMap;
	private Map<String, SmPersonnel> supplierUserMap;
	private SelectItem[] supplierSignerDropdown;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtContractVer particularContractVer;
	private CtAgreement ctAgreement;
	private CtAgreementContent selectedAgreementContent;
	private PmDigiSign pmDigiSign;	
	private TreeNode docSectionTN;
	private Map<TplDocSection, TreeNode> docSectionMap;
	
	/**
	 * @return the action
	 */
	public String[] getAction() {
		return action;
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(String[] action) {
		this.action = action;
	}
	
	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}
	
	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}
	
	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	/**
	 * @return the agreementType
	 */
	public String getAgreementType() {
		return agreementType;
	}
	
	/**
	 * @param agreementType the agreementType to set
	 */
	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}
	
	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}
	
	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	
	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the departmentProfileId
	 */
	public Long getDepartmentProfileId() {
		return departmentProfileId;
	}
	
	/**
	 * @param departmentProfileId the departmentProfileId to set
	 */
	public void setDepartmentProfileId(Long departmentProfileId) {
		this.departmentProfileId = departmentProfileId;
	}
	
	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}
	
	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	
	/**
	 * @return the performanceBondRuleAmt
	 */
	public double getPerformanceBondRuleAmt() {
		return performanceBondRuleAmt;
	}
	
	/**
	 * @param performanceBondRuleAmt the performanceBondRuleAmt to set
	 */
	public void setPerformanceBondRuleAmt(double performanceBondRuleAmt) {
		this.performanceBondRuleAmt = performanceBondRuleAmt;
	}
	
	/**
	 * @return the uploadFinalAgreement
	 */
	public boolean isUploadFinalAgreement() {
		return uploadFinalAgreement;
	}
	
	/**
	 * @param uploadFinalAgreement the uploadFinalAgreement to set
	 */
	public void setUploadFinalAgreement(boolean uploadFinalAgreement) {
		this.uploadFinalAgreement = uploadFinalAgreement;
	}
	
	/**
	 * @return the uploadStampedAndSignedPage
	 */
	public boolean isUploadStampedAndSignedPage() {
		return uploadStampedAndSignedPage;
	}
	
	/**
	 * @param uploadStampedAndSignedPage the uploadStampedAndSignedPage to set
	 */
	public void setUploadStampedAndSignedPage(boolean uploadStampedAndSignedPage) {
		this.uploadStampedAndSignedPage = uploadStampedAndSignedPage;
	}
	
	/**
	 * @return the validateWitness
	 */
	public boolean isValidateWitness() {
		return validateWitness;
	}
	
	/**
	 * @param validateWitness the validateWitness to set
	 */
	public void setValidateWitness(boolean validateWitness) {
		this.validateWitness = validateWitness;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the ctAgencySigner
	 */
	public List<CtAgreementParty> getCtAgencySigner() {
		if (ctAgencySigner == null) {
			ctAgencySigner = new ArrayList<CtAgreementParty>();
		}
		return ctAgencySigner;
	}
	
	/**
	 * @param ctAgencySigner the ctAgencySigner to set
	 */
	public void setCtAgencySigner(List<CtAgreementParty> ctAgencySigner) {
		this.ctAgencySigner = ctAgencySigner;
	}
	
	/**
	 * @return the ctSupplierSigner
	 */
	public List<CtAgreementParty> getCtSupplierSigner() {
		if (ctSupplierSigner == null) {
			ctSupplierSigner = new ArrayList<CtAgreementParty>();
		}
		return ctSupplierSigner;
	}
	
	/**
	 * @param ctSupplierSigner the ctSupplierSigner to set
	 */
	public void setCtSupplierSigner(List<CtAgreementParty> ctSupplierSigner) {
		this.ctSupplierSigner = ctSupplierSigner;
	}
	
	/**
	 * @return the ctSupplierWitness
	 */
	public List<CtAgreementParty> getCtSupplierWitness() {
		if (ctSupplierWitness == null) {
			ctSupplierWitness = new ArrayList<CtAgreementParty>();
		}
		return ctSupplierWitness;
	}
	
	/**
	 * @param ctSupplierWitness the ctSupplierWitness to set
	 */
	public void setCtSupplierWitness(List<CtAgreementParty> ctSupplierWitness) {
		this.ctSupplierWitness = ctSupplierWitness;
	}
	
	/**
	 * @return the ctSignAgencyWitness
	 */
	public List<CtAgreementParty> getCtSignAgencyWitness() {
		if (ctSignAgencyWitness == null) {
			ctSignAgencyWitness = new ArrayList<CtAgreementParty>();
		}
		return ctSignAgencyWitness;
	}
	
	/**
	 * @param ctSignAgencyWitness the ctSignAgencyWitness to set
	 */
	public void setCtSignAgencyWitness(List<CtAgreementParty> ctSignAgencyWitness) {
		this.ctSignAgencyWitness = ctSignAgencyWitness;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}
	
	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	
	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if (attachmentList == null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}
	
	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	
	/**
	 * @return the agencyOfficerIcNo
	 */
	public List<String> getAgencyOfficerIcNo() {
		return agencyOfficerIcNo;
	}
	
	/**
	 * @param agencyOfficerIcNo the agencyOfficerIcNo to set
	 */
	public void setAgencyOfficerIcNo(List<String> agencyOfficerIcNo) {
		this.agencyOfficerIcNo = agencyOfficerIcNo;
	}
	
	/**
	 * @return the agencyWitnessIcNo
	 */
	public List<String> getAgencyWitnessIcNo() {
		return agencyWitnessIcNo;
	}
	
	/**
	 * @param agencyWitnessIcNo the agencyWitnessIcNo to set
	 */
	public void setAgencyWitnessIcNo(List<String> agencyWitnessIcNo) {
		this.agencyWitnessIcNo = agencyWitnessIcNo;
	}
	
	/**
	 * @return the supplierOfficerIcNo
	 */
	public List<String> getSupplierOfficerIcNo() {
		return supplierOfficerIcNo;
	}
	
	/**
	 * @param supplierOfficerIcNo the supplierOfficerIcNo to set
	 */
	public void setSupplierOfficerIcNo(List<String> supplierOfficerIcNo) {
		this.supplierOfficerIcNo = supplierOfficerIcNo;
	}
	
	/**
	 * @return the supplierWitnessIcNo
	 */
	public List<String> getSupplierWitnessIcNo() {
		return supplierWitnessIcNo;
	}
	
	/**
	 * @param supplierWitnessIcNo the supplierWitnessIcNo to set
	 */
	public void setSupplierWitnessIcNo(List<String> supplierWitnessIcNo) {
		this.supplierWitnessIcNo = supplierWitnessIcNo;
	}
	
	/**
	 * @return the userMap
	 */
	public Map<String, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}
	
	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}
	
	/**
	 * @return the approverModel
	 */
	public DualListModel<String> getApproverModel() {
		return approverModel;
	}
	
	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}
	
	/**
	 * @return the supplierUserMap
	 */
	public Map<String, SmPersonnel> getSupplierUserMap() {
		if (supplierUserMap == null) {
			supplierUserMap = new HashMap<String, SmPersonnel>();
		}
		return supplierUserMap;
	}
	
	/**
	 * @param supplierUserMap the supplierUserMap to set
	 */
	public void setSupplierUserMap(Map<String, SmPersonnel> supplierUserMap) {
		this.supplierUserMap = supplierUserMap;
	}
	
	/**
	 * @return the supplierSignerDropdown
	 */
	public SelectItem[] getSupplierSignerDropdown() {
		return supplierSignerDropdown;
	}
	
	/**
	 * @param supplierSignerDropdown the supplierSignerDropdown to set
	 */
	public void setSupplierSignerDropdown(SelectItem[] supplierSignerDropdown) {
		this.supplierSignerDropdown = supplierSignerDropdown;
	}
	
	/**
	 * @return the ctContract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}
	
	/**
	 * @param ctContract the ctContract to set
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}
	
	/**
	 * @return the contractVer
	 */
	public CtContractVer getContractVer() {
		return contractVer;
	}
	
	/**
	 * @param contractVer the contractVer to set
	 */
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}
	
	/**
	 * @return the particularContractVer
	 */
	public CtContractVer getParticularContractVer() {
		return particularContractVer;
	}

	/**
	 * @param particularContractVer the particularContractVer to set
	 */
	public void setParticularContractVer(CtContractVer particularContractVer) {
		this.particularContractVer = particularContractVer;
	}

	/**
	 * @return the ctAgreement
	 */
	public CtAgreement getCtAgreement() {
		return ctAgreement;
	}
	
	/**
	 * @param ctAgreement the ctAgreement to set
	 */
	public void setCtAgreement(CtAgreement ctAgreement) {
		this.ctAgreement = ctAgreement;
	}
	
	/**
	 * @return the selectedAgreementContent
	 */
	public CtAgreementContent getSelectedAgreementContent() {
		return selectedAgreementContent;
	}
	
	/**
	 * @param selectedAgreementContent the selectedAgreementContent to set
	 */
	public void setSelectedAgreementContent(CtAgreementContent selectedAgreementContent) {
		this.selectedAgreementContent = selectedAgreementContent;
	}

	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public TreeNode getDocSectionTN() {
		return docSectionTN;
	}

	public void setDocSectionTN(TreeNode docSectionTN) {
		this.docSectionTN = docSectionTN;
	}

	public Map<TplDocSection, TreeNode> getDocSectionMap() {
		return docSectionMap;
	}

	public void setDocSectionMap(Map<TplDocSection, TreeNode> docSectionMap) {
		this.docSectionMap = docSectionMap;
	}
	
	
}
