package my.ep.web.managedbean.ct.circular.panel;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractPerson;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean
@SessionScoped
public class PanelManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private List<ContractPerson> contractPerson;
	private ContractPerson deleteContractPerson;
	private ApproverSetting approverSetting = new ApproverSetting();
	private CheckBoxSelections checkBoxSelections;
	

	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}


	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}


	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}


	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}


	public ContractPerson getDeleteContractPerson() {
		return deleteContractPerson;
	}


	public void setDeleteContractPerson(ContractPerson deleteContractPerson) {
		this.deleteContractPerson = deleteContractPerson;
	}


	public List<ContractPerson> getContractPerson() {
		if(contractPerson==null)
			contractPerson = new ArrayList<ContractPerson>();
		return contractPerson;
	}
		

	public void setContractPerson(List<ContractPerson> contractPerson) {
		this.contractPerson = contractPerson;
	}
}
