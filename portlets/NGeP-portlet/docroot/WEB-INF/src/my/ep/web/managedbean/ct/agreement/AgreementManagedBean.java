package my.ep.web.managedbean.ct.agreement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementContent;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

@ManagedBean(name ="agreementManagedBean")
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String taskId;
	private String[] action;
	private String taskName;
	private PmUser userLogin;
	private Long ministryProfileId;
	private Long departmentProfileId;
	private String agreementType ="";
	private String fileDirectory;
	
	private CtContract ctContract;
	private CtContractVer contractVer;
	
	private List<CtAgreementParty> ctAgencySigner;
	private List<CtAgreementParty> ctSupplierSigner;
	private List<CtAgreementParty> ctSupplierWitness;
	private List<CtAgreementParty> ctSignAgencyWitness;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private CtAgreementContent selectedAgreementContent;
	private Map<String, PmUser> userMap;
	private DualListModel<String> approverModel;
	
	private boolean chkConceValue;
	
	//KY
	private boolean uploadFinalAgreement;
	private boolean uploadStampedAndSignedPage;
	
	private Map<String, SmPersonnel> supplierUserMap;
	
	private CtAgreement ctAgreement;
	private int selectedTabIndex;
	
	public AgreementManagedBean(){
		
	}
	
	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}
	
	public String getFileDirectory() {
		return fileDirectory;
	}

	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}
	
	public List<CtAgreementParty> getCtAgencySigner() {
		if(ctAgencySigner == null)
			ctAgencySigner = new ArrayList<CtAgreementParty>();
		return ctAgencySigner;
	}

	public void setCtAgencySigner(List<CtAgreementParty> ctAgencySigner) {
		this.ctAgencySigner = ctAgencySigner;
	}

	public List<CtAgreementParty> getCtSignAgencyWitness() {
		if(ctSignAgencyWitness == null)
			ctSignAgencyWitness = new ArrayList<CtAgreementParty>();
		return ctSignAgencyWitness;
	}

	public void setCtSignAgencyWitness(List<CtAgreementParty> ctSignAgencyWitness) {
		this.ctSignAgencyWitness = ctSignAgencyWitness;
	}

	public List<CtAgreementParty> getCtSupplierSigner() {
		if(ctSupplierSigner == null)
			ctSupplierSigner = new ArrayList<CtAgreementParty>();
		return ctSupplierSigner;
	}

	public void setCtSupplierSigner(List<CtAgreementParty> ctSupplierSigner) {
		this.ctSupplierSigner = ctSupplierSigner;
	}

	public List<CtAgreementParty> getCtSupplierWitness() {
		if(ctSupplierWitness == null)
			ctSupplierWitness = new ArrayList<CtAgreementParty>();
		return ctSupplierWitness;
	}

	public void setCtSupplierWitness(List<CtAgreementParty> ctSupplierWitness) {
		this.ctSupplierWitness = ctSupplierWitness;
	}

	public Long getMinistryProfileId() {
		return ministryProfileId;
	}

	public void setMinistryProfileId(Long ministryProfileId) {
		this.ministryProfileId = ministryProfileId;
	}

	public Long getDepartmentProfileId() {
		return departmentProfileId;
	}

	public void setDepartmentProfileId(Long departmentProfileId) {
		this.departmentProfileId = departmentProfileId;
	}
	
	public CtContractVer getContractVer() {
		return contractVer;
	}

	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}

	public List<CtRemark> getRemarkList() {
		if(remarkList == null)
			remarkList = new ArrayList<CtRemark>();
		return remarkList;
	}
	
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null)
			attachmentList = new ArrayList<CtAttachment>();
		return attachmentList;
	}
	
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public PmUser getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(PmUser userLogin) {
		this.userLogin = userLogin;
	}

	public Map<String, PmUser> getUserMap() {
		if(userMap==null) {
			userMap = new HashMap<String, PmUser>();
		}
		return userMap;
	}

	public void setUserMap(Map<String, PmUser> userMap) {
		this.userMap = userMap;
	}

	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	public boolean isUploadFinalAgreement() {
		return uploadFinalAgreement;
	}

	public void setUploadFinalAgreement(boolean uploadFinalAgreement) {
		this.uploadFinalAgreement = uploadFinalAgreement;
	}

	public boolean isChkConceValue() {
		return chkConceValue;
	}

	public void setChkConceValue(boolean chkConceValue) {
		this.chkConceValue = chkConceValue;
	}

	public CtAgreementContent getSelectedAgreementContent() {
		return selectedAgreementContent;
	}

	public void setSelectedAgreementContent(
			CtAgreementContent selectedAgreementContent) {
		this.selectedAgreementContent = selectedAgreementContent;
	}

	public Map<String, SmPersonnel> getSupplierUserMap() {
		if(supplierUserMap==null) {
			supplierUserMap = new HashMap<String, SmPersonnel>();
		}
		return supplierUserMap;
	}

	public void setSupplierUserMap(Map<String, SmPersonnel> supplierUserMap) {
		this.supplierUserMap = supplierUserMap;
	}

	public CtAgreement getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(CtAgreement ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	public boolean isUploadStampedAndSignedPage() {
		return uploadStampedAndSignedPage;
	}

	public void setUploadStampedAndSignedPage(boolean uploadStampedAndSignedPage) {
		this.uploadStampedAndSignedPage = uploadStampedAndSignedPage;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}
	
}
