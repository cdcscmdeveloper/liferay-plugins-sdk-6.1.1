package my.ep.web.managedbean.ct.circular.central;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CentralContract;
import my.ep.web.bean.ct.ContractPerson;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class CentralManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ContractPerson> contractPerson;
	private ContractPerson deleteContractPerson;

	private ApproverSetting approverSetting;
	private CentralContract centralContract;
	private CentralContract[] selectedCentralContractList;

	public CentralManagedBean (){
		approverSetting = new ApproverSetting();
	}
	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}


	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}


	public CentralContract getCentralContract() {
		return centralContract;
	}


	public void setCentralContract(CentralContract centralContract) {
		this.centralContract = centralContract;
	}


	public CentralContract[] getSelectedCentralContractList() {
		return selectedCentralContractList;
	}


	public void setSelectedCentralContractList(
			CentralContract[] selectedCentralContractList) {
		this.selectedCentralContractList = selectedCentralContractList;
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
