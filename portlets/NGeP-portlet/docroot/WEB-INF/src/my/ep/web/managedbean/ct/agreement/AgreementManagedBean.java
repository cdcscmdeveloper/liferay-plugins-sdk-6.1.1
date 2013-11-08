package my.ep.web.managedbean.ct.agreement;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.agreement.CtAgreement;
import my.ep.persistence.entity.ct.agreement.CtAgreementAppendix;
import my.ep.persistence.entity.ct.agreement.CtSignParty;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class AgreementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AgreementManagedBean.class);
	
	private String taskId;
	private String[] outComes;
	private List<CtSignParty> signPartyList;
	private List<CtSignParty> signSupplierList;
	private CtSignParty signParty = new CtSignParty();
	private CtSignParty signSupplier = new CtSignParty();
	private CtAgreementAppendix agreementAppendix = new CtAgreementAppendix();
	private List<CtAgreementAppendix> appendixList = new ArrayList<CtAgreementAppendix>();
	
	private CtContract contract  = new CtContract();
	private CtAgreement agreement;
	
	public AgreementManagedBean(){
		
	}
	
	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public List<CtSignParty> getSignPartyList() {
		if(signPartyList == null)
	     signPartyList = new ArrayList<CtSignParty>();
		return signPartyList;
	}
	
	public void setSignPartyList(List<CtSignParty> signPartyList) {
		this.signPartyList = signPartyList;
	}
	
	public CtSignParty getSignParty() {
		return signParty;
	}
	
	public void setSignParty(CtSignParty signParty) {
		this.signParty = signParty;
	}

	public List<CtSignParty> getSignSupplierList() {
		if(signSupplierList == null)
			signSupplierList = new ArrayList<CtSignParty>();
		return signSupplierList;
	}

	public void setSignSupplierList(List<CtSignParty> signSupplierList) {
		this.signSupplierList = signSupplierList;
	}

	public CtSignParty getSignSupplier() {
		return signSupplier;
	}

	public void setSignSupplier(CtSignParty signSupplier) {
		this.signSupplier = signSupplier;
	}

	public CtContract getContract() {
		return contract;
	}

	public void setContract(CtContract contract) {
		this.contract = contract;
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
	
}
