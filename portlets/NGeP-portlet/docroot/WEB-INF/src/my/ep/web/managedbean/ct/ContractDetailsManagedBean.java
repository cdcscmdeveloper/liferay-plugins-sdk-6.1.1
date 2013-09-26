/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractDeduction;
import my.ep.web.bean.ct.ContractSchedule;
import my.ep.web.bean.ct.CreateContractDetails;

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
	
	private List<ContractSchedule> scheduleList;
	private List<ContractDeduction>  deductionList;
	private ApproverSetting approverSetting = new ApproverSetting();
	private ContractSchedule contractSchedule  = new ContractSchedule();
	private ContractDeduction contractDeduction = new ContractDeduction();
	private CheckBoxSelections checkBoxSelections = new  CheckBoxSelections();
	private CreateContractDetails createContractDetails = new CreateContractDetails();
	
	public String getViewAgreement() {
		return viewAgreement;
	}
	public void setViewAgreement(String viewAgreement) {
		this.viewAgreement = viewAgreement;
	}
	public List<ContractSchedule> getScheduleList() {
		if(scheduleList==null)
			scheduleList = new ArrayList<ContractSchedule>();
		return scheduleList;
	}
	public void setScheduleList(List<ContractSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}
	public List<ContractDeduction> getDeductionList() {
		if(deductionList==null)
			deductionList = new ArrayList<ContractDeduction>();
		return deductionList;
	}
	public void setDeductionList(List<ContractDeduction> deductionList) {
		this.deductionList = deductionList;
	}
	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}
	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}
	public ContractSchedule getContractSchedule() {
		return contractSchedule;
	}
	public void setContractSchedule(ContractSchedule contractSchedule) {
		this.contractSchedule = contractSchedule;
	}
	public ContractDeduction getContractDeduction() {
		return contractDeduction;
	}
	public void setContractDeduction(ContractDeduction contractDeduction) {
		this.contractDeduction = contractDeduction;
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
	
}
