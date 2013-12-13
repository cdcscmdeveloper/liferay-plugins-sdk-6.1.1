/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.cr;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

/**
 * Managed Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */
@ManagedBean(name = "crMB")
@SessionScoped
public class ContractRequestManagedBean extends BaseManagedBean {
	private static Logger log = Logger.getLogger(ContractRequestManagedBean.class);
	private static final long serialVersionUID = 1L;

	private FlFulfilmentRequest flRequest;
	private FlDeliveryAddress selectedAddrRow;
	private FlDeliveryAddress selectedDelAddr;
	private List<PmMaksud> maksudCodeList;
	private boolean checkPtjFlg = true;
	private List<PmAddressType> addressTypesList; 
	private List<PmAddressType> selectedPmAddrTypeList;
	private List<CtContractItem> contractedItemList;
	private List<CtContractItem> selectedContractedItemList;
	private DualListModel<String> approverModel;
	private FlFulfilmentItem selectedItemRow;
	private HashMap<String, PmUserGroupUser> approverMap;
	private HashMap<Long, FlFulfilmentItem> flItemMap;
	private BigDecimal subTotal;
	private FlFulfilmentItemAddr selectedItemAddr;
	
	
	//getcontract
	private CtContract ctContract = new CtContract();
	private List <CtContract> contractList;
	private CtContract selectedContract;
	
	//getSI
	private FlShippingInstr shippingInstr;
	
	private boolean selectedRowFlg;
	
	private String tempLogin; //TODO: temp and to be removed
	
	//doc search
	private String docProcess;
	private String flDocId;
	private String buttonVal;
	private boolean isMto = false;
		
	//get GOSODO
	private Long goId;
	private Long soId;
	private Long doId;
	
	public ContractRequestManagedBean(){
		
	}
	

	public FlFulfilmentRequest getFlRequest() {
		return flRequest;
	}


	public void setFlRequest(FlFulfilmentRequest flRequest) {
		this.flRequest = flRequest;
	}


	/**
	 * @return the selectedAddrRow
	 */
	public FlDeliveryAddress getSelectedAddrRow() {
		return selectedAddrRow;
	}


	/**
	 * @param selectedAddrRow the selectedAddrRow to set
	 */
	public void setSelectedAddrRow(FlDeliveryAddress selectedAddrRow) {
		this.selectedAddrRow = selectedAddrRow;
	}

	/**
	 * @return the selectedDelAddrList
	 */
	public FlDeliveryAddress getSelectedDelAddr() {
		return selectedDelAddr;
	}


	/**
	 * @param selectedDelAddrList the selectedDelAddrList to set
	 */
	public void setSelectedDelAddr(FlDeliveryAddress selectedDelAddr) {
		this.selectedDelAddr = selectedDelAddr;
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
	 * @return the contractList
	 */
	public List<CtContract> getContractList() {
		return contractList;
	}


	/**
	 * @param contractList the contractList to set
	 */
	public void setContractList(List<CtContract> contractList) {
		this.contractList = contractList;
	}


	/**
	 * @return the selectedContract
	 */
	public CtContract getSelectedContract() {
		return selectedContract;
	}


	/**
	 * @param selectedContract the selectedContract to set
	 */
	public void setSelectedContract(CtContract selectedContract) {
		this.selectedContract = selectedContract;
	}


	/**
	 * @return the selectedRowFlg
	 */
	public boolean isSelectedRowFlg() {
		return selectedRowFlg;
	}


	/**
	 * @param selectedRowFlg the selectedRowFlg to set
	 */
	public void setSelectedRowFlg(boolean selectedRowFlg) {
		this.selectedRowFlg = selectedRowFlg;
	}


	/**
	 * @return the maksudCodeList
	 */
	public List<PmMaksud> getMaksudCodeList() {
		return maksudCodeList;
	}


	/**
	 * @param maksudCodeList the maksudCodeList to set
	 */
	public void setMaksudCodeList(List<PmMaksud> maksudCodeList) {
		this.maksudCodeList = maksudCodeList;
	}


	/**
	 * @return the checkPtjFlg
	 */
	public boolean isCheckPtjFlg() {
		return checkPtjFlg;
	}


	/**
	 * @param checkPtjFlg the checkPtjFlg to set
	 */
	public void setCheckPtjFlg(boolean checkPtjFlg) {
		this.checkPtjFlg = checkPtjFlg;
	}

	/**
	 * @return the shippingInstr
	 */
	public FlShippingInstr getShippingInstr() {
		return shippingInstr;
	}


	/**
	 * @param shippingInstr the shippingInstr to set
	 */
	public void setShippingInstr(FlShippingInstr shippingInstr) {
		this.shippingInstr = shippingInstr;
	}


	/**
	 * @return the tempLogin
	 */
	public String getTempLogin() {
		return tempLogin;
	}


	/**
	 * @param tempLogin the tempLogin to set
	 */
	public void setTempLogin(String tempLogin) {
		this.tempLogin = tempLogin;
	}


	/**
	 * @return the docProcess
	 */
	public String getDocProcess() {
		return docProcess;
	}


	/**
	 * @param docProcess the docProcess to set
	 */
	public void setDocProcess(String docProcess) {
		this.docProcess = docProcess;
	}


	/**
	 * @return the flDocId
	 */
	public String getFlDocId() {
		return flDocId;
	}


	/**
	 * @param flDocId the flDocId to set
	 */
	public void setFlDocId(String flDocId) {
		this.flDocId = flDocId;
	}


	/**
	 * @return the buttonVal
	 */
	public String getButtonVal() {
		return buttonVal;
	}


	/**
	 * @param buttonVal the buttonVal to set
	 */
	public void setButtonVal(String buttonVal) {
		this.buttonVal = buttonVal;
	}


	/**
	 * @return the isMto
	 */
	public boolean isMto() {
		return isMto;
	}


	/**
	 * @param isMto the isMto to set
	 */
	public void setMto(boolean isMto) {
		this.isMto = isMto;
	}


	/**
	 * @return the addressTypesList
	 */
	public List<PmAddressType> getAddressTypesList() {
		return addressTypesList;
	}


	/**
	 * @param addressTypesList the addressTypesList to set
	 */
	public void setAddressTypesList(List<PmAddressType> addressTypesList) {
		this.addressTypesList = addressTypesList;
	}


	/**
	 * @return the selectedPmAddrTypeList
	 */
	public List<PmAddressType> getSelectedPmAddrTypeList() {
		return selectedPmAddrTypeList;
	}


	/**
	 * @param selectedPmAddrTypeList the selectedPmAddrTypeList to set
	 */
	public void setSelectedPmAddrTypeList(List<PmAddressType> selectedPmAddrTypeList) {
		this.selectedPmAddrTypeList = selectedPmAddrTypeList;
	}


	/**
	 * @return the contractedItemList
	 */
	public List<CtContractItem> getContractedItemList() {
		return contractedItemList;
	}


	/**
	 * @param contractedItemList the contractedItemList to set
	 */
	public void setContractedItemList(List<CtContractItem> contractedItemList) {
		this.contractedItemList = contractedItemList;
	}


	/**
	 * @return the selectedContractedItemList
	 */
	public List<CtContractItem> getSelectedContractedItemList() {
		return selectedContractedItemList;
	}


	/**
	 * @param selectedContractedItemList the selectedContractedItemList to set
	 */
	public void setSelectedContractedItemList(
			List<CtContractItem> selectedContractedItemList) {
		this.selectedContractedItemList = selectedContractedItemList;
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
	 * @return the selectedItemRow
	 */
	public FlFulfilmentItem getSelectedItemRow() {
		return selectedItemRow;
	}


	/**
	 * @param selectedItemRow the selectedItemRow to set
	 */
	public void setSelectedItemRow(FlFulfilmentItem selectedItemRow) {
		this.selectedItemRow = selectedItemRow;
	}


	/**
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
			setApproverMap(new HashMap<String,PmUserGroupUser>());
		}
		return approverMap;
	}


	/**
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<String, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
	}


	/**
	 * @return the flItemMap
	 */
	public HashMap<Long, FlFulfilmentItem> getFlItemMap() {
		if(flItemMap == null){
			setFlItemMap(new HashMap<Long, FlFulfilmentItem>());
		}
		return flItemMap;
	}


	/**
	 * @param flItemMap the flItemMap to set
	 */
	public void setFlItemMap(HashMap<Long, FlFulfilmentItem> flItemMap) {
		this.flItemMap = flItemMap;
	}


	/**
	 * @return the goId
	 */
	public Long getGoId() {
		return goId;
	}


	/**
	 * @param goId the goId to set
	 */
	public void setGoId(Long goId) {
		this.goId = goId;
	}


	/**
	 * @return the soId
	 */
	public Long getSoId() {
		return soId;
	}


	/**
	 * @param soId the soId to set
	 */
	public void setSoId(Long soId) {
		this.soId = soId;
	}


	/**
	 * @return the doId
	 */
	public Long getDoId() {
		return doId;
	}


	/**
	 * @param doId the doId to set
	 */
	public void setDoId(Long doId) {
		this.doId = doId;
	}


	/**
	 * @return the subTotal
	 */
	public BigDecimal getSubTotal() {
		return subTotal;
	}


	/**
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}


	/**
	 * @return the selectedItemAddr
	 */
	public FlFulfilmentItemAddr getSelectedItemAddr() {
		return selectedItemAddr;
	}


	/**
	 * @param selectedItemAddr the selectedItemAddr to set
	 */
	public void setSelectedItemAddr(FlFulfilmentItemAddr selectedItemAddr) {
		this.selectedItemAddr = selectedItemAddr;
	}

}
