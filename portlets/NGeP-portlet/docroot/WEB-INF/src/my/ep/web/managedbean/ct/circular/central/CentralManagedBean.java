package my.ep.web.managedbean.ct.circular.central;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class CentralManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private String taskId;
	private String[] outComes;
	private int selectedIndex;
	private Object searchValue;
	
	private List<CtContactPerson> contractPerson;
	private CtContactPerson deleteContractPerson;
	
	private List<CtContract> contractList;
	private List<CtContract> centralContractList;
	
	private List<CtCircularVer> circularVarList;
	private List<CtCircularVer> cancelCircularList;
	private CtCircularVer circularVer = new CtCircularVer();

	private CtCircular circular;
	private List<CtCircular> circularList;
	private CtContract contract = new CtContract();
	private ApproverSetting approverSetting;
	private SearchFilterBean searchFilterBean = new SearchFilterBean();
	
	
	public CentralManagedBean (){
		approverSetting = new ApproverSetting();
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public Object getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(Object searchValue) {
		this.searchValue = searchValue;
	}

	public SearchFilterBean getSearchFilterBean() {
		return searchFilterBean;
	}

	public void setSearchFilterBean(SearchFilterBean searchFilterBean) {
		this.searchFilterBean = searchFilterBean;
	}

	public ApproverSetting getApproverSetting() {
		return approverSetting;
	}

	public void setApproverSetting(ApproverSetting approverSetting) {
		this.approverSetting = approverSetting;
	}

	public List<CtContract> getContractList() {
		if(contractList == null)
			contractList = new ArrayList<CtContract>();
		return contractList;
	}

	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	} 

	public List<CtContactPerson> getContractPerson() {
		if(contractPerson == null)
			contractPerson = new ArrayList<CtContactPerson>();
		return contractPerson;
	}

	public void setContractPerson(List<CtContactPerson> contractPerson) {
		this.contractPerson = contractPerson;
	}

	public CtContactPerson getDeleteContractPerson() {
		return deleteContractPerson;
	}

	public void setDeleteContractPerson(CtContactPerson deleteContractPerson) {
		this.deleteContractPerson = deleteContractPerson;
	}

	public List<CtContract> getCentralContractList() {
		if(centralContractList == null)
			centralContractList = new ArrayList<CtContract>();
		return centralContractList;
	}

	public void setCentralContractList(List<CtContract> centralContractList) {
		this.centralContractList = centralContractList;
	}

	public List<CtCircularVer> getCircularVarList() {
		if(circularVarList == null)
			circularVarList = new ArrayList<CtCircularVer>();
		return circularVarList;
	}

	public void setCircularVarList(List<CtCircularVer> circularVarList) {
		this.circularVarList = circularVarList;
	}

	public List<CtCircularVer> getCancelCircularList() {
		return cancelCircularList;
	}

	public void setCancelCircularList(List<CtCircularVer> cancelCircularList) {
		this.cancelCircularList = cancelCircularList;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public CtCircular getCircular() {
		return circular;
	}

	public void setCircular(CtCircular circular) {
		this.circular = circular;
	}

	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	public CtContract getContract() {
		return contract;
	}

	public void setContract(CtContract contract) {
		this.contract = contract;
	}

	public List<CtCircular> getCircularList() {
		if(circularList == null)
			circularList = new ArrayList<CtCircular>();
		return circularList;
	}

	public void setCircularList(List<CtCircular> circularList) {
		this.circularList = circularList;
	}
	
	
	
}