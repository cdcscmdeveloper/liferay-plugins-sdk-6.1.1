package my.ep.web.managedbean.ct.agreement;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementAppendix;
import my.ep.persistence.entity.ct.agreement.CtAgreementParty;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name ="agreementManagedBean")
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AgreementManagedBean.class);
	
	private String taskId;
	private String[] action;
	private String taskName;
	private CtAgreementAppendix agreementAppendix = new CtAgreementAppendix();
	private List<CtAgreementAppendix> appendixList = new ArrayList<CtAgreementAppendix>();
	CtAgreementParty ctAgreementPartyList;
	private List<CtAgreementParty> signOfficerList;
	private List<CtAgreementParty> signSupplierList;
	private CtAgreementParty signOfficerParty = new CtAgreementParty();
	private CtAgreementParty signSupplierParty = new CtAgreementParty();
	
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	
	private CtContract ctContract;

	private CtAgreement agreement;
	private Long contractId;
	private String agreementType="view";
	
	public AgreementManagedBean(){
		
	}
	
	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public CtAgreement getAgreement() {
		return agreement;
	}

	public void setAgreement(CtAgreement agreement) {
		this.agreement = agreement;
	}

	public CtAgreementAppendix getAgreementAppendix() {
		return agreementAppendix;
	}

	public void setAgreementAppendix(CtAgreementAppendix agreementAppendix) {
		this.agreementAppendix = agreementAppendix;
	}

	public List<CtAgreementAppendix> getAppendixList() {
		return appendixList;
	}

	public void setAppendixList(List<CtAgreementAppendix> appendixList) {
		this.appendixList = appendixList;
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
	
	public List<CtRemark> getRemarkList() {
		if(remarkList==null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	public List<CtAttachment> getAttachmentList() {
		if(attachmentList==null) {
			attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
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

	public List<CtAgreementParty> getSignOfficerList() {
		if(signOfficerList == null)
			signOfficerList = new ArrayList<CtAgreementParty>();
		return signOfficerList;
	}

	public void setSignOfficerList(List<CtAgreementParty> signOfficerList) {
		this.signOfficerList = signOfficerList;
	}

	public List<CtAgreementParty> getSignSupplierList() {
		if(signSupplierList == null)
			signSupplierList = new ArrayList<CtAgreementParty>();
		return signSupplierList;
	}

	public void setSignSupplierList(List<CtAgreementParty> signSupplierList) {
		this.signSupplierList = signSupplierList;
	}

	public CtAgreementParty getSignOfficerParty() {
		return signOfficerParty;
	}

	public void setSignOfficerParty(CtAgreementParty signOfficerParty) {
		this.signOfficerParty = signOfficerParty;
	}

	public CtAgreementParty getSignSupplierParty() {
		return signSupplierParty;
	}

	public void setSignSupplierParty(CtAgreementParty signSupplierParty) {
		this.signSupplierParty = signSupplierParty;
	}

	
}
