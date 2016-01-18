/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.fl.cr;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.VCtActiveContract;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.fl.request.FlDeliveryAddress;
import my.ep.persistence.entity.fl.request.FlFulfilmentItem;
import my.ep.persistence.entity.fl.request.FlFulfilmentItemAddr;
import my.ep.persistence.entity.fl.request.FlFulfilmentRequest;
import my.ep.persistence.entity.fl.request.FlShippingInstr;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmGlAccount;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmPrgActivity;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmSubSetia;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.backingbean.fl.pr.FLCtSupplierItemVO;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.TreeNode;

/**
 * Managed Bean for Create Contract Order
 * 
 * @author Giribabu
 * @since 20/9/2013
 */
@ManagedBean(name = "crMB")
@SessionScoped
public class ContractRequestManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private FlFulfilmentRequest flRequest;
	private FlDeliveryAddress selectedAddrRow;
	private FlDeliveryAddress selectedDelAddr;
	private boolean checkPtjFlg = true;
	private List<PmAddressType> addressTypesList; 
	private List<PmAddressType> selectedPmAddrTypeList;
	private List<PmAddressType> selectedAddrTypeList;
	private List<CtAgencyAddress> selectedAgencyAddressList;
	private List<CtContractItem> contractedItemList;
	private List<CtContractItem> selectedContractedItemList;
	private List<VCtActiveContract> activeContractList; 
	private FlFulfilmentItem selectedItemRow;
	private HashMap<Long, PmUserGroupUser> approverMap;
	private BigDecimal subTotal;
	private FlFulfilmentItemAddr selectedItemAddr;
	private FlDeliveryAddress flManualAddress;
	private List<SelectItem> supplierBranchesList;
	private List<PmUserGroup> userGroupList;
	private List<Long> agentOrgProfileList;
	private Long selectedApprover;
	private List<Long> approverList;

	private List <FlFulfilmentItemAddr>  selectedItemsRows;
	private PmOrgProfile pmOrgProfile;
	private PmUserOrg pmUserOrg = new PmUserOrg();
	private Map<Long, PmUserGroup> userGroupMap;
	private List<Long> agenciesList; 
	private HashMap<Long, List<FlFulfilmentItemAddr>> tierMap;
	private HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap;
	
	private String requisitioner;
	private String approver;
	private Long docStatusId;
	private Long contractId;
	private Long testOrg;
	private String orgProfileIds;
	private String fulfilmentRequisitioner;
	private Date maxDate;
	private Long currentPtjGroupId;
	private Long currentPreparedOrgId;
	private String searchItemType;
	private String searchItemCode;
	private String searchItemName;
	private boolean isFlRequestLink = false;
	private boolean agencyAddress = false;
	private PmOrgProfile currentChargePtjGroup;
	private boolean declarationCheck;
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
	private boolean isImportedItems = false;

	//get GOSODO
	private Long goId;
	private Long soId;
	private Long doId;
	//supp address
	private String supplierAddress;

	//remove multiple selection from data table
	private List<FlDeliveryAddress> selectedAddressRows;
	//private List<FlFulfilmentItem> selectedItemsRows;
	
	//receving officer List
	private List<PmAddressType> receivingOffList;
	
	//chargeline
	private TreeNode chargeLineItemRoot ;
	private FlFulfilmentItemAddr chargeLineSelectedItemAddress;
	//private  List<TreeNode> selectedNodes;
	private TreeNode[] selectedNodes;
	private TreeNode selectedNode;
	private FlFulfilmentItemAddr selectedItemChargeSection;
	//private Long pmPrjSetia;
	private BigDecimal orderedAmtGrandTotal ;
	private String chargeCheck ="false";
		
	// charge Line dropdown lists
	private List<PmVotFund> votFundList;
	private List<PmSubSetia> subSetiaList;
	private List<PmPrgActivity> prgActivityList;
	private List<SelectItem> manualAddrList;
	private List<PmGlAccount> glAccountList;
		
	// set chargeline for selected items
	private String votFundCode;
	private String assetIndCode;
	private String glAccountCode;
	private String projectCode;
	private String setiaCode;
	private String subSetiaCode;
	private String prgActivityCode;
	private String votFundType;
		
	private Long votFundId;
	private Long assetIndId;
	private Long glAccountId;
	private Long subSetiaId;
	private Long prgActivityId;
	private String financingTypeCode;
	
	private boolean deleteButtonHide;
	private boolean zonal;
	
	private String termDays;
	private int deliveryTerm;
	
	// for Manual Address
	private List<PmCountry> countryList;
	private List<PmState> stateList;
	private List<PmDivision> divisionList;
	private List<PmCity> cityList;
	private List<PmDistrict> districtList;
	private FlDeliveryAddress flManulaAddress;
	//tab menu
	private int selectedTabIndex;
	// manual address
	private boolean required;
	private Map<Long, List<PmAddressType>> manualAddressMap;
	private List<PmAddressType> manualAddressList;
	//private List<PmAddressType> addressManualTypesList;
	private PmUser pmUser;
	private List<FlFulfilmentItemAddr> itemAddrList;
	private List<FlFulfilmentItemAddr> showItemAddrList;
	
	//gpki signing
	private PmDigiSign pmDigiSign;
	private boolean autoUpdate = true;
	private String textBeforeSign;
	private String buttonAction;
	private boolean validated = false;
	private boolean itemAutoUpdate = false;
	
	//uom
	private String measurementCodes;	
	private String measurementNames;
	
	public ContractRequestManagedBean(){

	}
	
	//charge line new screen 
	private boolean chargeLinePanel=false;
	private boolean programLov=false;
	private boolean projectLov=false;
	private boolean votFundDisplay=false;
	
	private PmVotFund pmVoteFund;
	private PmGlAccount gosodo;
	private PmPrgActivity pmPrgActivity;
	private PmSubSetia pmSubSetia;
	private FlFulfilmentItemAddr itemAddrVoteFundDisp;
	
	//item price
	//RequestItemInfo List
	private List<CtItemPrice> reqItemInfoList;
	private String itemName;
	private Double quantity;
	private Double standardUnitPrice;
	private PmParameter priceType;
	private boolean standardPrice = false;
	private boolean zonalPrice = false;
	private boolean zonalTierPrice = false;
	private boolean tariffPrice = false;
	private boolean serviceItem = false;
	private PmParameter pmPriceType;
	private List<FLCtSupplierItemVO>	supplierItemPriceList = new ArrayList<FLCtSupplierItemVO>();
	private BigDecimal UomFrequency ; 
	private BigDecimal UomPerYear ; 
	private BigDecimal UomPerMonth ; 
	private BigDecimal UomPerDay ; 
	private BigDecimal pricePerUom;
	private BigDecimal ctQuantity;
	private String uomName;
	
	private boolean emailValidate = false;
	private boolean queryDisplay=false;

	private PmVotFund feeVoteFund;
	private boolean gfmasError=false;
	private String gfmasErrorContent;
	
	//getFactoring address details
	private String facSupplierAddress;
	private String phoneNo;
	private String faxNo;
	private String contactEmail;
	private String contactPerson;
	private String contactPhoneNo;
	public String backToDocument;
	
	//display gst info
	private String gstRegistrationNo;
	private Date gstRegistrationDate;
	
	private boolean selectChargeLineRow=false;
	
	private boolean itemDeleteButtonShow=false;
	private boolean itemAddButtonShow;
	private boolean gstRateChange;
	
	private boolean hyperLinkPage = false;
	private boolean pidValidityExpired = false;
	
	/** The active contract list document data model. */
	private IPagingDataModel<VCtActiveContract> activeContractDataModel = new ActiveContractPagingDataModel();
	
	private class ActiveContractPagingDataModel extends PagingDataModel<VCtActiveContract> {
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new action contract list paging data model.
		 */
		public ActiveContractPagingDataModel() {
			super("ctListDt", true);
		}
		
		/* (non-Javadoc)
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VCtActiveContract> pageChangeListener(ISearchBean<VCtActiveContract> searchBean) {
			try {
				searchBean.setUseEntityClass(true);
				
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				FacesScopes facesScopes = new FacesScopes();
				if (facesScopes.getUserSessionBean().getUserId() == null) {
					throw new IllegalArgumentException("userId cannot be null");
				}	
				userReqDTO.setUserId(facesScopes.getUserSessionBean().getUserId());
				UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
				Long orgProfileId = userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId();
				
				IOrgProfileSO profileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileId(orgProfileId);
				Long ministryId = profileSO.retrieveMinistryByOrgProfileId(reqDTO).getPmOrgProfile().getOrgProfileId();
				
				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO contractReqDTO = new ContractReqDTO();
				contractReqDTO.setActiveContractSearchBean(searchBean);
				contractReqDTO.setMinistryId(ministryId);
				contractReqDTO.setOrgProfileId(orgProfileId);
				ContractResDTO resDTO = contractSO.retrieveActiveContractList(contractReqDTO);
				
				if(resDTO.isSuccessful()) {
					for(VCtActiveContract vCtActiveContract : resDTO.getActiveContractResultBean().getResultList()) {
						if(vCtActiveContract.getFulfilmentType().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
							contractReqDTO.setContractId(vCtActiveContract.getContractId());
							vCtActiveContract.setActiveContractSubList(contractSO.retrieveScheduleInfoForContract(contractReqDTO).getActiveContractList());
						}
					}
					return resDTO.getActiveContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	/**
	 * @param chargeLineItemRoot the chargeLineItemRoot to set
	 */
	public void setChargeLineItemRoot(TreeNode chargeLineItemRoot) {
		this.chargeLineItemRoot = chargeLineItemRoot;
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
	public HashMap<Long, PmUserGroupUser> getApproverMap() {
		if(approverMap == null){
			setApproverMap(new HashMap<Long,PmUserGroupUser>());
		}
		return approverMap;
	}


	/**
	 * @param approverMap the approverMap to set
	 */
	public void setApproverMap(HashMap<Long, PmUserGroupUser> approverMap) {
		this.approverMap = approverMap;
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


	/**
	 * @return the selectedAddressRows
	 */
	public List<FlDeliveryAddress> getSelectedAddressRows() {
		return selectedAddressRows;
	}


	/**
	 * @param selectedAddressRows the selectedAddressRows to set
	 */
	public void setSelectedAddressRows(List<FlDeliveryAddress> selectedAddressRows) {
		this.selectedAddressRows = selectedAddressRows;
	}


	/**
	 * @return the selectedItemsRows
	 */
	public List<FlFulfilmentItemAddr> getSelectedItemsRows() {
		return selectedItemsRows;
	}


	/**
	 * @param selectedItemsRows the selectedItemsRows to set
	 */
	public void setSelectedItemsRows(List<FlFulfilmentItemAddr> selectedItemsRows) {
		this.selectedItemsRows = selectedItemsRows;
	}


	/**
	 * @return the isImportedItems
	 */
	public boolean isImportedItems() {
		return isImportedItems;
	}


	/**
	 * @param isImportedItems the isImportedItems to set
	 */
	public void setImportedItems(boolean isImportedItems) {
		this.isImportedItems = isImportedItems;
	}


	/**
	 * @return the pmOrgProfile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}


	/**
	 * @param pmOrgProfile the pmOrgProfile to set
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	/**
	 * @return the pmUserOrg
	 */
	public PmUserOrg getPmUserOrg() {
		return pmUserOrg;
	}


	/**
	 * @param pmUserOrg the pmUserOrg to set
	 */
	public void setPmUserOrg(PmUserOrg pmUserOrg) {
		this.pmUserOrg = pmUserOrg;
	}


	/**
	 * @return the requisitioner
	 */
	public String getRequisitioner() {
		return requisitioner;
	}


	/**
	 * @param requisitioner the requisitioner to set
	 */
	public void setRequisitioner(String requisitioner) {
		this.requisitioner = requisitioner;
	}


	/**
	 * @return the approver
	 */
	public String getApprover() {
		return approver;
	}


	/**
	 * @param approver the approver to set
	 */
	public void setApprover(String approver) {
		this.approver = approver;
	}


	/**
	 * @return the docStatusId
	 */
	public Long getDocStatusId() {
		return docStatusId;
	}


	/**
	 * @param docStatusId the docStatusId to set
	 */
	public void setDocStatusId(Long docStatusId) {
		this.docStatusId = docStatusId;
	}


	/**
	 * @return the contractId
	 */
	public Long getContractId() {
		return contractId;
	}


	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}


	/**
	 * @return the userGroupMap
	 */
	public Map<Long, PmUserGroup> getUserGroupMap() {
		return userGroupMap;
	}


	/**
	 * @param userGroupMap the userGroupMap to set
	 */
	public void setUserGroupMap(Map<Long, PmUserGroup> userGroupMap) {
		this.userGroupMap = userGroupMap;
	}


	/**
	 * @return the agenciesList
	 */
	public List<Long> getAgenciesList() {
		return agenciesList;
	}


	/**
	 * @param agenciesList the agenciesList to set
	 */
	public void setAgenciesList(List<Long> agenciesList) {
		this.agenciesList = agenciesList;
	}


	/**
	 * @return the testOrg
	 */
	public Long getTestOrg() {
		return testOrg;
	}


	/**
	 * @param testOrg the testOrg to set
	 */
	public void setTestOrg(Long testOrg) {
		this.testOrg = testOrg;
	}


	public List<VCtActiveContract> getActiveContractList() {
		return activeContractList;
	}


	public void setActiveContractList(List<VCtActiveContract> activeContractList) {
		this.activeContractList = activeContractList;
	}


	/**
	 * @return the flManualAddress
	 */
	public FlDeliveryAddress getFlManualAddress() {
		return flManualAddress;
	}


	/**
	 * @param flManualAddress the flManualAddress to set
	 */
	public void setFlManualAddress(FlDeliveryAddress flManualAddress) {
		this.flManualAddress = flManualAddress;
	}


	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}


	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}


	/**
	 * @return the receivingOffList
	 */
	public List<PmAddressType> getReceivingOffList() {
		return receivingOffList;
	}


	/**
	 * @param receivingOffList the receivingOffList to set
	 */
	public void setReceivingOffList(List<PmAddressType> receivingOffList) {
		this.receivingOffList = receivingOffList;
	}


	/**
	 * @return the chargeLineSelectedItemAddress
	 */
	public FlFulfilmentItemAddr getChargeLineSelectedItemAddress() {
		return chargeLineSelectedItemAddress;
	}


	/**
	 * @param chargeLineSelectedItemAddress the chargeLineSelectedItemAddress to set
	 */
	public void setChargeLineSelectedItemAddress(
			FlFulfilmentItemAddr chargeLineSelectedItemAddress) {
		this.chargeLineSelectedItemAddress = chargeLineSelectedItemAddress;
	}


	/**
	 * @return the selectedNodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}


	/**
	 * @param selectedNodes the selectedNodes to set
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}


	/**
	 * @return the selectedNode
	 */
	public TreeNode getSelectedNode() {
		return selectedNode;
	}


	/**
	 * @param selectedNode the selectedNode to set
	 */
	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}


	/**
	 * @return the selectedItemChargeSection
	 */
	public FlFulfilmentItemAddr getSelectedItemChargeSection() {
		return selectedItemChargeSection;
	}


	/**
	 * @param selectedItemChargeSection the selectedItemChargeSection to set
	 */
	public void setSelectedItemChargeSection(
			FlFulfilmentItemAddr selectedItemChargeSection) {
		this.selectedItemChargeSection = selectedItemChargeSection;
	}


	/**
	 * @return the orderedAmtGrandTotal
	 */
	public BigDecimal getOrderedAmtGrandTotal() {
		return orderedAmtGrandTotal;
	}


	/**
	 * @param orderedAmtGrandTotal the orderedAmtGrandTotal to set
	 */
	public void setOrderedAmtGrandTotal(BigDecimal orderedAmtGrandTotal) {
		this.orderedAmtGrandTotal = orderedAmtGrandTotal;
	}


	/**
	 * @return the votFundList
	 */
	public List<PmVotFund> getVotFundList() {
		return votFundList;
	}


	/**
	 * @param votFundList the votFundList to set
	 */
	public void setVotFundList(List<PmVotFund> votFundList) {
		this.votFundList = votFundList;
	}

	/**
	 * @return the subSetiaList
	 */
	public List<PmSubSetia> getSubSetiaList() {
		return subSetiaList;
	}


	/**
	 * @param subSetiaList the subSetiaList to set
	 */
	public void setSubSetiaList(List<PmSubSetia> subSetiaList) {
		this.subSetiaList = subSetiaList;
	}


	/**
	 * @return the prgActivityList
	 */
	public List<PmPrgActivity> getPrgActivityList() {
		return prgActivityList;
	}


	/**
	 * @param prgActivityList the prgActivityList to set
	 */
	public void setPrgActivityList(List<PmPrgActivity> prgActivityList) {
		this.prgActivityList = prgActivityList;
	}


	/**
	 * @return the manualAddrList
	 */
	public List<SelectItem> getManualAddrList() {
		return manualAddrList;
	}


	/**
	 * @param manualAddrList the manualAddrList to set
	 */
	public void setManualAddrList(List<SelectItem> manualAddrList) {
		this.manualAddrList = manualAddrList;
	}


	/**
	 * @return the votFundCode
	 */
	public String getVotFundCode() {
		return votFundCode;
	}


	/**
	 * @param votFundCode the votFundCode to set
	 */
	public void setVotFundCode(String votFundCode) {
		this.votFundCode = votFundCode;
	}


	/**
	 * @return the projectCode
	 */
	public String getProjectCode() {
		return projectCode;
	}


	/**
	 * @param projectCode the projectCode to set
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * @return the setiaCode
	 */
	public String getSetiaCode() {
		return setiaCode;
	}


	/**
	 * @param setiaCode the setiaCode to set
	 */
	public void setSetiaCode(String setiaCode) {
		this.setiaCode = setiaCode;
	}


	/**
	 * @return the subSetiaCode
	 */
	public String getSubSetiaCode() {
		return subSetiaCode;
	}


	/**
	 * @param subSetiaCode the subSetiaCode to set
	 */
	public void setSubSetiaCode(String subSetiaCode) {
		this.subSetiaCode = subSetiaCode;
	}


	/**
	 * @return the prgActivityCode
	 */
	public String getPrgActivityCode() {
		return prgActivityCode;
	}


	/**
	 * @param prgActivityCode the prgActivityCode to set
	 */
	public void setPrgActivityCode(String prgActivityCode) {
		this.prgActivityCode = prgActivityCode;
	}


	/**
	 * @return the votFundId
	 */
	public Long getVotFundId() {
		return votFundId;
	}


	/**
	 * @param votFundId the votFundId to set
	 */
	public void setVotFundId(Long votFundId) {
		this.votFundId = votFundId;
	}



	/**
	 * @return the subSetiaId
	 */
	public Long getSubSetiaId() {
		return subSetiaId;
	}


	/**
	 * @param subSetiaId the subSetiaId to set
	 */
	public void setSubSetiaId(Long subSetiaId) {
		this.subSetiaId = subSetiaId;
	}


	/**
	 * @return the prgActivityId
	 */
	public Long getPrgActivityId() {
		return prgActivityId;
	}


	/**
	 * @param prgActivityId the prgActivityId to set
	 */
	public void setPrgActivityId(Long prgActivityId) {
		this.prgActivityId = prgActivityId;
	}


	/**
	 * @return the chargeLineItemRoot
	 */
	public TreeNode getChargeLineItemRoot() {
		return chargeLineItemRoot;
	}


	/**
	 * @return the chargeCheck
	 */
	public String getChargeCheck() {
		return chargeCheck;
	}


	/**
	 * @param chargeCheck the chargeCheck to set
	 */
	public void setChargeCheck(String chargeCheck) {
		this.chargeCheck = chargeCheck;
	}


	/**
	 * @return the deleteButtonHide
	 */
	public boolean isDeleteButtonHide() {
		return deleteButtonHide;
	}


	/**
	 * @param deleteButtonHide the deleteButtonHide to set
	 */
	public void setDeleteButtonHide(boolean deleteButtonHide) {
		this.deleteButtonHide = deleteButtonHide;
	}


	/**
	 * @return the termDays
	 */
	public String getTermDays() {
		return termDays;
	}


	/**
	 * @param termDays the termDays to set
	 */
	public void setTermDays(String termDays) {
		this.termDays = termDays;
	}


	/**
	 * @return the countryList
	 */
	public List<PmCountry> getCountryList() {
		return countryList;
	}


	/**
	 * @param countryList the countryList to set
	 */
	public void setCountryList(List<PmCountry> countryList) {
		this.countryList = countryList;
	}


	/**
	 * @return the stateList
	 */
	public List<PmState> getStateList() {
		return stateList;
	}


	/**
	 * @param stateList the stateList to set
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}


	/**
	 * @return the divisionList
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}


	/**
	 * @param divisionList the divisionList to set
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}


	/**
	 * @return the cityList
	 */
	public List<PmCity> getCityList() {
		return cityList;
	}


	/**
	 * @param cityList the cityList to set
	 */
	public void setCityList(List<PmCity> cityList) {
		this.cityList = cityList;
	}


	/**
	 * @return the districtList
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}


	/**
	 * @return the flManulaAddress
	 */
	public FlDeliveryAddress getFlManulaAddress() {
		return flManulaAddress;
	}


	/**
	 * @param flManulaAddress the flManulaAddress to set
	 */
	public void setFlManulaAddress(FlDeliveryAddress flManulaAddress) {
		this.flManulaAddress = flManulaAddress;
	}


	/**
	 * @param districtList the districtList to set
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}


	/**
	 * @return the selectedTabIndex
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}


	/**
	 * @param selectedTabIndex the selectedTabIndex to set
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}


	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}


	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}


	/**
	 * @return the pmUser
	 */
	public PmUser getPmUser() {
		return pmUser;
	}


	/**
	 * @param pmUser the pmUser to set
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}


	/**
	 * @return the glAccountList
	 */
	public List<PmGlAccount> getGlAccountList() {
		return glAccountList;
	}


	/**
	 * @param glAccountList the glAccountList to set
	 */
	public void setGlAccountList(List<PmGlAccount> glAccountList) {
		this.glAccountList = glAccountList;
	}


	/**
	 * @return the votFundType
	 */
	public String getVotFundType() {
		return votFundType;
	}


	/**
	 * @param votFundType the votFundType to set
	 */
	public void setVotFundType(String votFundType) {
		this.votFundType = votFundType;
	}


	/**
	 * @return the assetIndCode
	 */
	public String getAssetIndCode() {
		return assetIndCode;
	}


	/**
	 * @param assetIndCode the assetIndCode to set
	 */
	public void setAssetIndCode(String assetIndCode) {
		this.assetIndCode = assetIndCode;
	}


	/**
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}


	/**
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}


	/**
	 * @return the assetIndId
	 */
	public Long getAssetIndId() {
		return assetIndId;
	}


	/**
	 * @param assetIndId the assetIndId to set
	 */
	public void setAssetIndId(Long assetIndId) {
		this.assetIndId = assetIndId;
	}


	/**
	 * @return the glAccountId
	 */
	public Long getGlAccountId() {
		return glAccountId;
	}


	/**
	 * @param glAccountId the glAccountId to set
	 */
	public void setGlAccountId(Long glAccountId) {
		this.glAccountId = glAccountId;
	}


	/**
	 * @return the financingTypeCode
	 */
	public String getFinancingTypeCode() {
		return financingTypeCode;
	}


	/**
	 * @param financingTypeCode the financingTypeCode to set
	 */
	public void setFinancingTypeCode(String financingTypeCode) {
		this.financingTypeCode = financingTypeCode;
	}


	/**
	 * @return the supplierBranchesList
	 */
	public List<SelectItem> getSupplierBranchesList() {
		return supplierBranchesList;
	}


	/**
	 * @param supplierBranchesList the supplierBranchesList to set
	 */
	public void setSupplierBranchesList(List<SelectItem> supplierBranchesList) {
		this.supplierBranchesList = supplierBranchesList;
	}

	/**
	 * @return the deliveryTerm
	 */
	public int getDeliveryTerm() {
		return deliveryTerm;
	}


	/**
	 * @param deliveryTerm the deliveryTerm to set
	 */
	public void setDeliveryTerm(int deliveryTerm) {
		this.deliveryTerm = deliveryTerm;
	}


	/**
	 * @return the zonal
	 */
	public boolean isZonal() {
		return zonal;
	}


	/**
	 * @param zonal the zonal to set
	 */
	public void setZonal(boolean zonal) {
		this.zonal = zonal;
	}


	/**
	 * @return the manualAddressList
	 */
	public List<PmAddressType> getManualAddressList() {
		return manualAddressList;
	}


	/**
	 * @param manualAddressList the manualAddressList to set
	 */
	public void setManualAddressList(List<PmAddressType> manualAddressList) {
		this.manualAddressList = manualAddressList;
	}


	/**
	 * @return the orgProfileIds
	 */
	public String getOrgProfileIds() {
		return orgProfileIds;
	}


	/**
	 * @param orgProfileIds the orgProfileIds to set
	 */
	public void setOrgProfileIds(String orgProfileIds) {
		this.orgProfileIds = orgProfileIds;
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
	 * @return the manualAddressMap
	 */
	public Map<Long, List<PmAddressType>> getManualAddressMap() {
		return manualAddressMap;
	}


	/**
	 * @param manualAddressMap the manualAddressMap to set
	 */
	public void setManualAddressMap(Map<Long, List<PmAddressType>> manualAddressMap) {
		this.manualAddressMap = manualAddressMap;
	}


	/**
	 * @return the selectedAddrTypeList
	 */
	public List<PmAddressType> getSelectedAddrTypeList() {
		return selectedAddrTypeList;
	}


	/**
	 * @param selectedAddrTypeList the selectedAddrTypeList to set
	 */
	public void setSelectedAddrTypeList(List<PmAddressType> selectedAddrTypeList) {
		this.selectedAddrTypeList = selectedAddrTypeList;
	}

	/**
	 * @return the fulfilmentRequisitioner
	 */
	public String getFulfilmentRequisitioner() {
		return fulfilmentRequisitioner;
	}


	/**
	 * @param fulfilmentRequisitioner the fulfilmentRequisitioner to set
	 */
	public void setFulfilmentRequisitioner(String fulfilmentRequisitioner) {
		this.fulfilmentRequisitioner = fulfilmentRequisitioner;
	}


	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return maxDate;
	}


	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}


	/**
	 * @param userGroupList the userGroupList to set
	 */
	public void setUserGroupList(List<PmUserGroup> userGroupList) {
		this.userGroupList = userGroupList;
	}


	/**
	 * @return the userGroupList
	 */
	public List<PmUserGroup> getUserGroupList() {
		if(userGroupList==null) {
			userGroupList = new ArrayList<PmUserGroup>();
		}
		return userGroupList;
	}


	/**
	 * @return the currentPtjGroupId
	 */
	public Long getCurrentPtjGroupId() {
		return currentPtjGroupId;
	}


	/**
	 * @param currentPtjGroupId the currentPtjGroupId to set
	 */
	public void setCurrentPtjGroupId(Long currentPtjGroupId) {
		this.currentPtjGroupId = currentPtjGroupId;
	}


	/**
	 * @return the currentPreparedOrgId
	 */
	public Long getCurrentPreparedOrgId() {
		return currentPreparedOrgId;
	}


	/**
	 * @param currentPreparedOrgId the currentPreparedOrgId to set
	 */
	public void setCurrentPreparedOrgId(Long currentPreparedOrgId) {
		this.currentPreparedOrgId = currentPreparedOrgId;
	}

	/**
	 * @return the chargeLinePanel
	 */
	public boolean isChargeLinePanel() {
		return chargeLinePanel;
	}

	/**
	 * @return the programLov
	 */
	public boolean isProgramLov() {
		return programLov;
	}

	/**
	 * @return the projectLov
	 */
	public boolean isProjectLov() {
		return projectLov;
	}

	/**
	 * @return the votFundDisplay
	 */
	public boolean isVotFundDisplay() {
		return votFundDisplay;
	}

	/**
	 * @return the pmVoteFund
	 */
	public PmVotFund getPmVoteFund() {
		return pmVoteFund;
	}

	/**
	 * @return the gosodo
	 */
	public PmGlAccount getGosodo() {
		return gosodo;
	}

	/**
	 * @return the pmPrgActivity
	 */
	public PmPrgActivity getPmPrgActivity() {
		return pmPrgActivity;
	}

	/**
	 * @return the pmSubSetia
	 */
	public PmSubSetia getPmSubSetia() {
		return pmSubSetia;
	}

	/**
	 * @return the itemAddrVoteFundDisp
	 */
	public FlFulfilmentItemAddr getItemAddrVoteFundDisp() {
		return itemAddrVoteFundDisp;
	}

	/**
	 * @param chargeLinePanel the chargeLinePanel to set
	 */
	public void setChargeLinePanel(boolean chargeLinePanel) {
		this.chargeLinePanel = chargeLinePanel;
	}

	/**
	 * @param programLov the programLov to set
	 */
	public void setProgramLov(boolean programLov) {
		this.programLov = programLov;
	}

	/**
	 * @param projectLov the projectLov to set
	 */
	public void setProjectLov(boolean projectLov) {
		this.projectLov = projectLov;
	}

	/**
	 * @param votFundDisplay the votFundDisplay to set
	 */
	public void setVotFundDisplay(boolean votFundDisplay) {
		this.votFundDisplay = votFundDisplay;
	}

	/**
	 * @param pmVoteFund the pmVoteFund to set
	 */
	public void setPmVoteFund(PmVotFund pmVoteFund) {
		this.pmVoteFund = pmVoteFund;
	}

	/**
	 * @param gosodo the gosodo to set
	 */
	public void setGosodo(PmGlAccount gosodo) {
		this.gosodo = gosodo;
	}

	/**
	 * @param pmPrgActivity the pmPrgActivity to set
	 */
	public void setPmPrgActivity(PmPrgActivity pmPrgActivity) {
		this.pmPrgActivity = pmPrgActivity;
	}

	/**
	 * @param pmSubSetia the pmSubSetia to set
	 */
	public void setPmSubSetia(PmSubSetia pmSubSetia) {
		this.pmSubSetia = pmSubSetia;
	}

	/**
	 * @param itemAddrVoteFundDisp the itemAddrVoteFundDisp to set
	 */
	public void setItemAddrVoteFundDisp(FlFulfilmentItemAddr itemAddrVoteFundDisp) {
		this.itemAddrVoteFundDisp = itemAddrVoteFundDisp;
	}


	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @return the quantity
	 */
	public Double getQuantity() {
		return quantity;
	}

	/**
	 * @return the standardUnitPrice
	 */
	public Double getStandardUnitPrice() {
		return standardUnitPrice;
	}

	/**
	 * @return the priceType
	 */
	public PmParameter getPriceType() {
		return priceType;
	}

	/**
	 * @return the standardPrice
	 */
	public boolean isStandardPrice() {
		return standardPrice;
	}

	/**
	 * @return the zonalPrice
	 */
	public boolean isZonalPrice() {
		return zonalPrice;
	}

	/**
	 * @return the zonalTierPrice
	 */
	public boolean isZonalTierPrice() {
		return zonalTierPrice;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	/**
	 * @param standardUnitPrice the standardUnitPrice to set
	 */
	public void setStandardUnitPrice(Double standardUnitPrice) {
		this.standardUnitPrice = standardUnitPrice;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PmParameter priceType) {
		this.priceType = priceType;
	}

	/**
	 * @param standardPrice the standardPrice to set
	 */
	public void setStandardPrice(boolean standardPrice) {
		this.standardPrice = standardPrice;
	}

	/**
	 * @param zonalPrice the zonalPrice to set
	 */
	public void setZonalPrice(boolean zonalPrice) {
		this.zonalPrice = zonalPrice;
	}

	/**
	 * @param zonalTierPrice the zonalTierPrice to set
	 */
	public void setZonalTierPrice(boolean zonalTierPrice) {
		this.zonalTierPrice = zonalTierPrice;
	}

	/**
	 * @return the reqItemInfoList
	 */
	public List<CtItemPrice> getReqItemInfoList() {
		return reqItemInfoList;
	}

	/**
	 * @param reqItemInfoList the reqItemInfoList to set
	 */
	public void setReqItemInfoList(List<CtItemPrice> reqItemInfoList) {
		this.reqItemInfoList = reqItemInfoList;
	}

	/**
	 * @return the pmPriceType
	 */
	public PmParameter getPmPriceType() {
		return pmPriceType;
	}

	/**
	 * @param pmPriceType the pmPriceType to set
	 */
	public void setPmPriceType(PmParameter pmPriceType) {
		this.pmPriceType = pmPriceType;
	}

	/**
	 * @return the supplierItemPriceList
	 */
	public List<FLCtSupplierItemVO> getSupplierItemPriceList() {
		return supplierItemPriceList;
	}

	/**
	 * @param supplierItemPriceList the supplierItemPriceList to set
	 */
	public void setSupplierItemPriceList(
			List<FLCtSupplierItemVO> supplierItemPriceList) {
		this.supplierItemPriceList = supplierItemPriceList;
	}

	/**
	 * @return the emailValidate
	 */
	public boolean isEmailValidate() {
		return emailValidate;
	}

	/**
	 * @param emailValidate the emailValidate to set
	 */
	public void setEmailValidate(boolean emailValidate) {
		this.emailValidate = emailValidate;
	}

	/**
	 * @return the queryDisplay
	 */
	public boolean isQueryDisplay() {
		return queryDisplay;
	}

	/**
	 * @param queryDisplay the queryDisplay to set
	 */
	public void setQueryDisplay(boolean queryDisplay) {
		this.queryDisplay = queryDisplay;
	}

	public PmVotFund getFeeVoteFund() {
		return feeVoteFund;
	}

	public void setFeeVoteFund(PmVotFund feeVoteFund) {
		this.feeVoteFund = feeVoteFund;
	}

	public boolean isTariffPrice() {
		return tariffPrice;
	}

	public void setTariffPrice(boolean tariffPrice) {
		this.tariffPrice = tariffPrice;
	}
	
	/**
	 * @return the tierMap
	 */
	public HashMap<Long, List<FlFulfilmentItemAddr>> getTierMap() {
		if(tierMap == null){
			tierMap = new HashMap<Long, List<FlFulfilmentItemAddr>>();
		}
		return tierMap;
	}

	/**
	 * @param tierMap the tierMap to set
	 */
	public void setTierMap(HashMap<Long, List<FlFulfilmentItemAddr>> tierMap) {
		this.tierMap = tierMap;
	}

	/**
	 * @return the zonalTierMap
	 */
	public HashMap<String, List<FlFulfilmentItemAddr>> getZonalTierMap() {
		if(zonalTierMap == null){
			zonalTierMap = new HashMap<String,List<FlFulfilmentItemAddr>>();
		}
		return zonalTierMap;
	}

	/**
	 * @param zonalTierMap the zonalTierMap to set
	 */
	public void setZonalTierMap(
			HashMap<String, List<FlFulfilmentItemAddr>> zonalTierMap) {
		this.zonalTierMap = zonalTierMap;
	}

	/**
	 * @return the searchItemType
	 */
	public String getSearchItemType() {
		return searchItemType;
	}

	/**
	 * @param searchItemType the searchItemType to set
	 */
	public void setSearchItemType(String searchItemType) {
		this.searchItemType = searchItemType;
	}

	/**
	 * @return the searchItemCode
	 */
	public String getSearchItemCode() {
		return searchItemCode;
	}

	/**
	 * @param searchItemCode the searchItemCode to set
	 */
	public void setSearchItemCode(String searchItemCode) {
		this.searchItemCode = searchItemCode;
	}

	/**
	 * @return the searchItemName
	 */
	public String getSearchItemName() {
		return searchItemName;
	}

	/**
	 * @param searchItemName the searchItemName to set
	 */
	public void setSearchItemName(String searchItemName) {
		this.searchItemName = searchItemName;
	}

	public List<FlFulfilmentItemAddr> getItemAddrList() {
		return itemAddrList;
	}

	public void setItemAddrList(List<FlFulfilmentItemAddr> itemAddrList) {
		this.itemAddrList = itemAddrList;
	}

	public List<FlFulfilmentItemAddr> getShowItemAddrList() {
		return showItemAddrList;
	}

	public void setShowItemAddrList(List<FlFulfilmentItemAddr> showItemAddrList) {
		this.showItemAddrList = showItemAddrList;
	}

	/**
	 * @return the agentOrgProfileList
	 */
	public List<Long> getAgentOrgProfileList() {
		if(agentOrgProfileList == null){
			agentOrgProfileList = new ArrayList<Long>();
		}
		return agentOrgProfileList;
	}

	/**
	 * @param agentOrgProfileList the agentOrgProfileList to set
	 */
	public void setAgentOrgProfileList(List<Long> agentOrgProfileList) {
		this.agentOrgProfileList = agentOrgProfileList;
	}

	/**
	 * @return the facSupplierAddress
	 */
	public String getFacSupplierAddress() {
		return facSupplierAddress;
	}

	/**
	 * @return the phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * @return the faxNo
	 */
	public String getFaxNo() {
		return faxNo;
	}

	/**
	 * @return the contactEmail
	 */
	public String getContactEmail() {
		return contactEmail;
	}

	/**
	 * @return the contactPerson
	 */
	public String getContactPerson() {
		return contactPerson;
	}

	/**
	 * @return the contactPhoneNo
	 */
	public String getContactPhoneNo() {
		return contactPhoneNo;
	}

	/**
	 * @param facSupplierAddress the facSupplierAddress to set
	 */
	public void setFacSupplierAddress(String facSupplierAddress) {
		this.facSupplierAddress = facSupplierAddress;
	}

	/**
	 * @param phoneNo the phoneNo to set
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * @param faxNo the faxNo to set
	 */
	public void setFaxNo(String faxNo) {
		this.faxNo = faxNo;
	}

	/**
	 * @param contactEmail the contactEmail to set
	 */
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	/**
	 * @param contactPerson the contactPerson to set
	 */
	public void setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
	}

	/**
	 * @param contactPhoneNo the contactPhoneNo to set
	 */
	public void setContactPhoneNo(String contactPhoneNo) {
		this.contactPhoneNo = contactPhoneNo;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
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

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * @return the serviceItem
	 */
	public boolean isServiceItem() {
		return serviceItem;
	}

	/**
	 * @param serviceItem the serviceItem to set
	 */
	public void setServiceItem(boolean serviceItem) {
		this.serviceItem = serviceItem;
	}

	/**
	 * @return the uomFrequency
	 */
	public BigDecimal getUomFrequency() {
		return UomFrequency;
	}

	/**
	 * @return the uomPerYear
	 */
	public BigDecimal getUomPerYear() {
		return UomPerYear;
	}

	/**
	 * @return the uomPerMonth
	 */
	public BigDecimal getUomPerMonth() {
		return UomPerMonth;
	}

	/**
	 * @return the uomPerDay
	 */
	public BigDecimal getUomPerDay() {
		return UomPerDay;
	}

	/**
	 * @param uomFrequency the uomFrequency to set
	 */
	public void setUomFrequency(BigDecimal uomFrequency) {
		UomFrequency = uomFrequency;
	}

	/**
	 * @param uomPerYear the uomPerYear to set
	 */
	public void setUomPerYear(BigDecimal uomPerYear) {
		UomPerYear = uomPerYear;
	}

	/**
	 * @param uomPerMonth the uomPerMonth to set
	 */
	public void setUomPerMonth(BigDecimal uomPerMonth) {
		UomPerMonth = uomPerMonth;
	}

	/**
	 * @param uomPerDay the uomPerDay to set
	 */
	public void setUomPerDay(BigDecimal uomPerDay) {
		UomPerDay = uomPerDay;
	}

	public BigDecimal getPricePerUom() {
		return pricePerUom;
	}

	public void setPricePerUom(BigDecimal pricePerUom) {
		this.pricePerUom = pricePerUom;
	}

	public String getMeasurementCodes() {
		return measurementCodes;
	}

	public void setMeasurementCodes(String measurementCodes) {
		this.measurementCodes = measurementCodes;
	}

	public String getMeasurementNames() {
		return measurementNames;
	}

	public void setMeasurementNames(String measurementNames) {
		this.measurementNames = measurementNames;
	}

	public String getUomName() {
		return uomName;
	}

	public void setUomName(String uomName) {
		this.uomName = uomName;
	}

	/**
	 * @return the backToDocument
	 */
	public String getBackToDocument() {
		return backToDocument;
	}

	/**
	 * @param backToDocument the backToDocument to set
	 */
	public void setBackToDocument(String backToDocument) {
		this.backToDocument = backToDocument;
	}

	/**
	 * @return the isFlRequestLink
	 */
	public boolean isFlRequestLink() {
		return isFlRequestLink;
	}

	/**
	 * @param isFlRequestLink the isFlRequestLink to set
	 */
	public void setFlRequestLink(boolean isFlRequestLink) {
		this.isFlRequestLink = isFlRequestLink;
	}

	/**
	 * @return the ctQuantity
	 */
	public BigDecimal getCtQuantity() {
		return ctQuantity;
	}

	/**
	 * @param ctQuantity the ctQuantity to set
	 */
	public void setCtQuantity(BigDecimal ctQuantity) {
		this.ctQuantity = ctQuantity;
	}

	/**
	 * @return the agencyAddress
	 */
	public boolean isAgencyAddress() {
		return agencyAddress;
	}

	/**
	 * @param agencyAddress the agencyAddress to set
	 */
	public void setAgencyAddress(boolean agencyAddress) {
		this.agencyAddress = agencyAddress;
	}

	public List<CtAgencyAddress> getSelectedAgencyAddressList() {
		return selectedAgencyAddressList;
	}

	public void setSelectedAgencyAddressList(
			List<CtAgencyAddress> selectedAgencyAddressList) {
		this.selectedAgencyAddressList = selectedAgencyAddressList;
	}

	/**
	 * @return the gstRegistrationNo
	 */
	public String getGstRegistrationNo() {
		return gstRegistrationNo;
	}

	/**
	 * @return the gstRegistrationDate
	 */
	public Date getGstRegistrationDate() {
		return gstRegistrationDate;
	}

	/**
	 * @param gstRegistrationNo the gstRegistrationNo to set
	 */
	public void setGstRegistrationNo(String gstRegistrationNo) {
		this.gstRegistrationNo = gstRegistrationNo;
	}

	/**
	 * @param gstRegistrationDate the gstRegistrationDate to set
	 */
	public void setGstRegistrationDate(Date gstRegistrationDate) {
		this.gstRegistrationDate = gstRegistrationDate;
	}

	/**
	 * @return the gfmasError
	 */
	public boolean isGfmasError() {
		return gfmasError;
	}

	/**
	 * @param gfmasError the gfmasError to set
	 */
	public void setGfmasError(boolean gfmasError) {
		this.gfmasError = gfmasError;
	}

	/**
	 * @return the gfmasErrorContent
	 */
	public String getGfmasErrorContent() {
		return gfmasErrorContent;
	}

	/**
	 * @param gfmasErrorContent the gfmasErrorContent to set
	 */
	public void setGfmasErrorContent(String gfmasErrorContent) {
		this.gfmasErrorContent = gfmasErrorContent;
	}

	/**
	 * @return the currentChargePtjGroup
	 */
	public PmOrgProfile getCurrentChargePtjGroup() {
		return currentChargePtjGroup;
	}

	/**
	 * @param currentChargePtjGroup the currentChargePtjGroup to set
	 */
	public void setCurrentChargePtjGroup(PmOrgProfile currentChargePtjGroup) {
		this.currentChargePtjGroup = currentChargePtjGroup;
	}

	/**
	 * @return the approverList
	 */
	public List<Long> getApproverList() {
		if(approverList == null){
			setApproverList(new ArrayList<Long>());
		}
		return approverList;
	}

	/**
	 * @param approverList the approverList to set
	 */
	public void setApproverList(List<Long> approverList) {
		this.approverList = approverList;
	}

	public IPagingDataModel<VCtActiveContract> getActiveContractDataModel() {
		return activeContractDataModel;
	}

	public void setActiveContractDataModel(IPagingDataModel<VCtActiveContract> activeContractDataModel) {
		this.activeContractDataModel = activeContractDataModel;
	}

	/**
	 * @return the selectChargeLineRow
	 */
	public boolean isSelectChargeLineRow() {
		return selectChargeLineRow;
	}

	/**
	 * @param selectChargeLineRow the selectChargeLineRow to set
	 */
	public void setSelectChargeLineRow(boolean selectChargeLineRow) {
		this.selectChargeLineRow = selectChargeLineRow;
	}

	/**
	 * @return the declarationCheck
	 */
	public boolean isDeclarationCheck() {
		return declarationCheck;
	}

	/**
	 * @param declarationCheck the declarationCheck to set
	 */
	public void setDeclarationCheck(boolean declarationCheck) {
		this.declarationCheck = declarationCheck;
	}

	/**
	 * @return the itemDeleteButtonShow
	 */
	public boolean isItemDeleteButtonShow() {
		return itemDeleteButtonShow;
	}

	/**
	 * @param itemDeleteButtonShow the itemDeleteButtonShow to set
	 */
	public void setItemDeleteButtonShow(boolean itemDeleteButtonShow) {
		this.itemDeleteButtonShow = itemDeleteButtonShow;
	}

	/**
	 * @return the itemAddButtonShow
	 */
	public boolean isItemAddButtonShow() {
		return itemAddButtonShow;
	}

	/**
	 * @param itemAddButtonShow the itemAddButtonShow to set
	 */
	public void setItemAddButtonShow(boolean itemAddButtonShow) {
		this.itemAddButtonShow = itemAddButtonShow;
	}

	public boolean isGstRateChange() {
		return gstRateChange;
	}

	public void setGstRateChange(boolean gstRateChange) {
		this.gstRateChange = gstRateChange;
	}

	/**
	 * @return the hyperLinkPage
	 */
	public boolean isHyperLinkPage() {
		return hyperLinkPage;
	}

	/**
	 * @param hyperLinkPage the hyperLinkPage to set
	 */
	public void setHyperLinkPage(boolean hyperLinkPage) {
		this.hyperLinkPage = hyperLinkPage;
	}

	/**
	 * @return the itemAutoUpdate
	 */
	public boolean isItemAutoUpdate() {
		return itemAutoUpdate;
	}

	/**
	 * @param itemAutoUpdate the itemAutoUpdate to set
	 */
	public void setItemAutoUpdate(boolean itemAutoUpdate) {
		this.itemAutoUpdate = itemAutoUpdate;
	}

	/**
	 * @return the selectedApprover
	 */
	public Long getSelectedApprover() {
		return selectedApprover;
	}

	/**
	 * @param selectedApprover the selectedApprover to set
	 */
	public void setSelectedApprover(Long selectedApprover) {
		this.selectedApprover = selectedApprover;
	}

	public boolean isPidValidityExpired() {
		return pidValidityExpired;
	}

	public void setPidValidityExpired(boolean pidValidityExpired) {
		this.pidValidityExpired = pidValidityExpired;
	}

}
