package my.ep.web.managedbean.ct.agreement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementAppendix;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.web.bean.ct.AgreementDetails;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name ="agreementManagedBean")
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AgreementManagedBean.class);
	
	private String taskId;
	private String[] action;
	private String taskName;
	
	private boolean userDefined = true;
	private boolean sysDefined;
	private CtContract ctContract;
	private CtAgreement ctAgreement;
	private CtContractVer ctContractVer;
	private CtAmendment ctAmendment;
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtAgreementParty> ctSignParties;
	private List<CtAgreementParty> ctSignWitness;
	private List<CtAgreementParty> ctSignSuppliers;
	
	private PmOrgProfile pmOrgProfile;
	
	private Long contractId;
	private String agreementType ="supplemental";
	
	List<AgreementDetails> agreementList;
	private CtAgreementAppendix ctAgreementAppendix;
	private List<CtAgreementAppendix> ctUserAppendixList;
	private List<CtAgreementAppendix> ctSystemAppendixList;
	
	private Long parameterId;
	private String userLoginId;
	private Long ministryProfileId;
	private Long departmentProfileId;

	private Map<Long, PmUserGroup> userGroupMap;
	private DualListModel<String> approverModel;
	
	private PmParameter pmParameter;
	
	private String uploadFinalAgreement;
	private List<PmOrgProfile> orgList;
	private List<PmOrgProfile> orgListDep;
	
	
	public AgreementManagedBean(){
		
	}
	
	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public CtAgreement getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(CtAgreement ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getAgreementType() {
		return agreementType;
	}

	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
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

	public List<CtAgreementParty> getCtSignParties() {
		return ctSignParties;
	}

	public void setCtSignParties(List<CtAgreementParty> ctSignParties) {
		this.ctSignParties = ctSignParties;
	}

	public List<CtAgreementParty> getCtSignSuppliers() {
		return ctSignSuppliers;
	}

	public void setCtSignSuppliers(List<CtAgreementParty> ctSignSuppliers) {
		this.ctSignSuppliers = ctSignSuppliers;
	}

	public boolean isUserDefined() {
		return userDefined;
	}

	public void setUserDefined(boolean userDefined) {
		this.userDefined = userDefined;
	}

	public boolean isSysDefined() {
		return sysDefined;
	}

	public void setSysDefined(boolean sysDefined) {
		this.sysDefined = sysDefined;
	}

	public CtAgreementAppendix getCtAgreementAppendix() {
		return ctAgreementAppendix;
	}

	public void setCtAgreementAppendix(CtAgreementAppendix ctAgreementAppendix) {
		this.ctAgreementAppendix = ctAgreementAppendix;
	}

	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
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

	public List<CtAgreementParty> getCtSignWitness() {
		return ctSignWitness;
	}

	public void setCtSignWitness(List<CtAgreementParty> ctSignwitness) {
		this.ctSignWitness = ctSignwitness;
	}

	public CtContractVer getCtContractVer() {
		return ctContractVer;
	}

	public void setCtContractVer(CtContractVer ctContractVer) {
		this.ctContractVer = ctContractVer;
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

	public String getUserLoginId() {
		return userLoginId;
	}

	public void setUserLoginId(String userLoginId) {
		this.userLoginId = userLoginId;
	}

	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}

	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}

	public DualListModel<String> getApproverModel() {
		return approverModel;
	}

	public void setApproverModel(DualListModel<String> approverModel) {
		this.approverModel = approverModel;
	}

	
	public List<CtAgreementAppendix> getCtUserAppendixList() {
		if(ctUserAppendixList == null)
			ctUserAppendixList = new ArrayList<CtAgreementAppendix>();
		return ctUserAppendixList;
	}

	public void setCtUserAppendixList(List<CtAgreementAppendix> ctUserAppendixList) {
		this.ctUserAppendixList = ctUserAppendixList;
	}

	public List<AgreementDetails> getAgreementList() {
		return agreementList;
	}

	public void setAgreementList(List<AgreementDetails> agreementList) {
		this.agreementList = agreementList;
	}

	public Long getParameterId() {
		return parameterId;
	}

	public void setParameterId(Long parameterId) {
		this.parameterId = parameterId;
	}

	public List<CtAgreementAppendix> getCtSystemAppendixList() {
		if(ctSystemAppendixList == null)
			ctSystemAppendixList = new ArrayList<CtAgreementAppendix>();
		return ctSystemAppendixList;
	}

	public void setCtSystemAppendixList(
			List<CtAgreementAppendix> ctSystemAppendixList) {
		this.ctSystemAppendixList = ctSystemAppendixList;
	}

	public PmParameter getPmParameter() {
		return pmParameter;
	}

	public void setPmParameter(PmParameter pmParameter) {
		this.pmParameter = pmParameter;
	}

	public String getUploadFinalAgreement() {
		return uploadFinalAgreement;
	}

	public void setUploadFinalAgreement(String uploadFinalAgreement) {
		this.uploadFinalAgreement = uploadFinalAgreement;
	}

	public CtAmendment getCtAmendment() {
		return ctAmendment;
	}

	public void setCtAmendment(CtAmendment ctAmendment) {
		this.ctAmendment = ctAmendment;
	}

	public List<PmOrgProfile> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<PmOrgProfile> orgList) {
		this.orgList = orgList;
	}

	public List<PmOrgProfile> getOrgListDep() {
		return orgListDep;
	}

	public void setOrgListDep(List<PmOrgProfile> orgListDep) {
		this.orgListDep = orgListDep;
	}	
	
	
}
