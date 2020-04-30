package my.ep.web.managedbean.ct.requestCtAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean (name = "requestCtAdminManagedBean")
@SessionScoped
public class RequestCtAdminManagedBean extends BaseManagedBean{
	
	private  boolean readOnly=true;
	private String taskName;
	private String fileDirectory;
	private String textBeforeSign;
	private String buttonAction;
	private String searchItemString;
	private String contractId;
	private String itemtype;
	private String itemcode;
	private String itemstatus;
	private String skuno;
	private String UOM;
	private String typeofgoods;

	private boolean autoUpdate = true;
	private boolean viewOnly = false;
	
	private PmDigiSign pmDigiSign;
	private CtContract ctContract;
	private CtContractVer contractVer;
	private CtContractItem selectedItemForSpec;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<CtContractItem> contractItemList;
	private HashMap<Long, CtAgency> agencyMap;
	private HashMap<Long, CtContractItem> itemMap;	
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUser> userMap;
	
	
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}
	public CtContract getCtContract() {
		return ctContract;
	}
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}
	public CtContractVer getContractVer() {
		return contractVer;
	}
	public void setContractVer(CtContractVer contractVer) {
		this.contractVer = contractVer;
	}
	public CtContractItem getSelectedItemForSpec() {
		return selectedItemForSpec;
	}
	public void setSelectedItemForSpec(CtContractItem selectedItemForSpec) {
		this.selectedItemForSpec = selectedItemForSpec;
	}
	public List<CtRemark> getRemarkList() {
		return remarkList;
	}
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}
	public List<CtAttachment> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public List<CtContractItem> getContractItemList() {
		return contractItemList;
	}
	public void setContractItemList(List<CtContractItem> contractItemList) {
		this.contractItemList = contractItemList;
	}
	public HashMap<Long, CtAgency> getAgencyMap() {
		return agencyMap;
	}
	public void setAgencyMap(HashMap<Long, CtAgency> agencyMap) {
		this.agencyMap = agencyMap;
	}
	public HashMap<Long, CtContractItem> getItemMap() {
		return itemMap;
	}
	public void setItemMap(HashMap<Long, CtContractItem> itemMap) {
		this.itemMap = itemMap;
	}
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}
	public Map<Long, PmUser> getUserMap() {
		return userMap;
	}
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}
	public boolean isReadOnly() {
		return readOnly;
	}
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getFileDirectory() {
		return fileDirectory;
	}
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}
	public String getTextBeforeSign() {
		return textBeforeSign;
	}
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}
	public String getButtonAction() {
		return buttonAction;
	}
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}
	public String getSearchItemString() {
		return searchItemString;
	}
	public void setSearchItemString(String searchItemString) {
		this.searchItemString = searchItemString;
	}
	public String getContractId() {
		return contractId;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getItemtype() {
		return itemtype;
	}
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}
	public String getItemcode() {
		return itemcode;
	}
	public void setItemcode(String itemcode) {
		this.itemcode = itemcode;
	}
	public String getItemstatus() {
		return itemstatus;
	}
	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}
	public String getSkuno() {
		return skuno;
	}
	public void setSkuno(String skuno) {
		this.skuno = skuno;
	}
	public String getUOM() {
		return UOM;
	}
	public void setUOM(String uOM) {
		UOM = uOM;
	}
	public String getTypeofgoods() {
		return typeofgoods;
	}
	public void setTypeofgoods(String typeofgoods) {
		this.typeofgoods = typeofgoods;
	}
	public boolean isAutoUpdate() {
		return autoUpdate;
	}
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	public boolean isViewOnly() {
		return viewOnly;
	}
	public void setViewOnly(boolean viewOnly) {
		this.viewOnly = viewOnly;
	}
	
	
	
	

}
