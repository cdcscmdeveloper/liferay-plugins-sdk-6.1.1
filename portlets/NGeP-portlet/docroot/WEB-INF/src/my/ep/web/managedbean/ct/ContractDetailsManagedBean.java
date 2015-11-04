/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.ct;

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
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.model.DualListModel;
/**
 * Managed Bean For Contract Details
 * 
 * @author Alam
 * @since 23/9/2013	 
 */
@ManagedBean(name ="contractDetailsManagedBean")
@SessionScoped
public class ContractDetailsManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private String[] action;
	private String taskName;
	private String popupForImportedItems = "L";
	private String ctName;
	private String ctPhysicalLoaNo;
	private String ctNumber;
	private String ctCircularNo;
	private String ctLoaNo;
	private String ctPhysicalNo;
	private String ctCircularName;
	private String ctSupplierName;
	private String externalPageUrl;
	private String warningPercentage;
	private String textBeforeSign;
	private String buttonAction;	
	private String gotoPreviewPage;	
	private String fileDirectory;
	private Long ctContractStatus;
	private Long terminationTypeId;
	private Long loaContractVerId;
    private Date ctDateStart;
	private Date ctDateEnd;
	private Date asAtDate = null;
	private int selectedTabIndex = 0;
	private int ctStatus;	
	private int attachFileIndex;
   private boolean updateContract = false;
	private boolean viewContract = false;
	private boolean autoUpdate = true;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<String> finalListOfSideMenu;
	private SelectItem[] loaContractVerList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;
	private Map<Long, PmUser> userMap;
	private HashMap<String, PmUserGroupUser> approverMap;
	private CtCircularVer ctCircularVer;
		private PmDigiSign pmDigiSign;	
	private IPagingDataModel<VCtContract> contractDataModel = new ContractPagingDataModel();
	
	private Long searchMinistryProfileId;
	private PmOrgProfile searchPtjProfile;
	
	public int deductionIndex =99;
	public int scheduleIndex =99;
	public int factoring = 99;
	public int paymentTracking = 99;
	public int itemIndex = 99;
	public int settingIndex = 101;
	private int index;
	
	
	/**
	 * @return the action
	 */
	public String[] getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String[] action) {
		this.action = action;
	}
	
	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the popupForImportedItems
	 */
	public String getPopupForImportedItems() {
		return popupForImportedItems;
	}

	/**
	 * @param popupForImportedItems the popupForImportedItems to set
	 */
	public void setPopupForImportedItems(String popupForImportedItems) {
		this.popupForImportedItems = popupForImportedItems;
	}

	/**
	 * @return the ctName
	 */
	public String getCtName() {
		return ctName;
	}

	/**
	 * @param ctName the ctName to set
	 */
	public void setCtName(String ctName) {
		this.ctName = ctName;
	}

	/**
	 * @return the ctPhysicalLoaNo
	 */
	public String getCtPhysicalLoaNo() {
		return ctPhysicalLoaNo;
	}
	
	/**
	 * @param ctPhysicalLoaNo the ctPhysicalLoaNo to set
	 */
	public void setCtPhysicalLoaNo(String ctPhysicalLoaNo) {
		this.ctPhysicalLoaNo = ctPhysicalLoaNo;
	}

	/**
	 * @return the ctNumber
	 */
	public String getCtNumber() {
		return ctNumber;
	}

	/**
	 * @param ctNumber the ctNumber to set
	 */
	public void setCtNumber(String ctNumber) {
		this.ctNumber = ctNumber;
	}
	
	/**
	 * @return the ctCircularNo
	 */
	public String getCtCircularNo() {
		return ctCircularNo;
	}

	/**
	 * @param ctCircularNo the ctCircularNo to set
	 */
	public void setCtCircularNo(String ctCircularNo) {
		this.ctCircularNo = ctCircularNo;
	}

	/**
	 * @return the ctLoaNo
	 */
	public String getCtLoaNo() {
		return ctLoaNo;
	}

	/**
	 * @param ctLoaNo the ctLoaNo to set
	 */
	public void setCtLoaNo(String ctLoaNo) {
		this.ctLoaNo = ctLoaNo;
	}

	/**
	 * @return the ctPhysicalNo
	 */
	public String getCtPhysicalNo() {
		return ctPhysicalNo;
	}

	/**
	 * @param ctPhysicalNo the ctPhysicalNo to set
	 */
	public void setCtPhysicalNo(String ctPhysicalNo) {
		this.ctPhysicalNo = ctPhysicalNo;
	}

	/**
	 * @return the ctCircularName
	 */
	public String getCtCircularName() {
		return ctCircularName;
	}

	/**
	 * @param ctCircularName the ctCircularName to set
	 */
	public void setCtCircularName(String ctCircularName) {
		this.ctCircularName = ctCircularName;
	}

	/**
	 * @return the ctSupplierName
	 */
	public String getCtSupplierName() {
		return ctSupplierName;
	}

	/**
	 * @param ctSupplierName the ctSupplierName to set
	 */
	public void setCtSupplierName(String ctSupplierName) {
		this.ctSupplierName = ctSupplierName;
	}

	/**
	 * @return the externalPageUrl
	 */
	public String getExternalPageUrl() {
		return externalPageUrl;
	}

	/**
	 * @param externalPageUrl the externalPageUrl to set
	 */
	public void setExternalPageUrl(String externalPageUrl) {
		this.externalPageUrl = externalPageUrl;
	}
	
	/**
	 * @return the warningPercentage
	 */
	public String getWarningPercentage() {
		return warningPercentage;
	}

	/**
	 * @param warningPercentage the warningPercentage to set
	 */
	public void setWarningPercentage(String warningPercentage) {
		this.warningPercentage = warningPercentage;
	}

	/**
	 * @return the textBeforeSign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * @param textBeforeSign the textBeforeSign to set
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * @return the buttonAction
	 */
	public String getButtonAction() {
		return buttonAction;
	}

	/**
	 * @param buttonAction the buttonAction to set
	 */
	public void setButtonAction(String buttonAction) {
		this.buttonAction = buttonAction;
	}

	/**
	 * @return the gotoPreviewPage
	 */
	public String getGotoPreviewPage() {
		return gotoPreviewPage;
	}

	/**
	 * @param gotoPreviewPage the gotoPreviewPage to set
	 */
	public void setGotoPreviewPage(String gotoPreviewPage) {
		this.gotoPreviewPage = gotoPreviewPage;
	}

	/**
	 * @return the fileDirectory
	 */
	public String getFileDirectory() {
		return fileDirectory;
	}

	/**
	 * @param fileDirectory the fileDirectory to set
	 */
	public void setFileDirectory(String fileDirectory) {
		this.fileDirectory = fileDirectory;
	}

	/**
	 * @return the ctContractStatus
	 */
	public Long getCtContractStatus() {
		return ctContractStatus;
	}

	/**
	 * @param ctContractStatus the ctContractStatus to set
	 */
	public void setCtContractStatus(Long ctContractStatus) {
		this.ctContractStatus = ctContractStatus;
	}

	/**
	 * @return the terminationTypeId
	 */
	public Long getTerminationTypeId() {
		return terminationTypeId;
	}

	/**
	 * @param terminationTypeId the terminationTypeId to set
	 */
	public void setTerminationTypeId(Long terminationTypeId) {
		this.terminationTypeId = terminationTypeId;
	}
	
	/**
	 * @return the loaContractVerId
	 */
	public Long getLoaContractVerId() {
		return loaContractVerId;
	}

	/**
	 * @param loaContractVerId the loaContractVerId to set
	 */
	public void setLoaContractVerId(Long loaContractVerId) {
		this.loaContractVerId = loaContractVerId;
	}
	
	/**
	 * @return the ctDateStart
	 */
	public Date getCtDateStart() {
		return ctDateStart;
	}

	/**
	 * @param ctDateStart the ctDateStart to set
	 */
	public void setCtDateStart(Date ctDateStart) {
		this.ctDateStart = ctDateStart;
	}

	/**
	 * @return the ctDateEnd
	 */
	public Date getCtDateEnd() {
		return ctDateEnd;
	}

	/**
	 * @param ctDateEnd the ctDateEnd to set
	 */
	public void setCtDateEnd(Date ctDateEnd) {
		this.ctDateEnd = ctDateEnd;
	}	

	/**
	 * @return the asAtDate
	 */
	public Date getAsAtDate() {
		return asAtDate;
	}

	/**
	 * @param asAtDate the asAtDate to set
	 */
	public void setAsAtDate(Date asAtDate) {
		this.asAtDate = asAtDate;
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
	 * @return the ctStatus
	 */
	public int getCtStatus() {
		return ctStatus;
	}

	/**
	 * @param ctStatus the ctStatus to set
	 */
	public void setCtStatus(int ctStatus) {
		this.ctStatus = ctStatus;
	}

	/**
	 * @return the attachFileIndex
	 */
	public int getAttachFileIndex() {
		return attachFileIndex;
	}

	/**
	 * @param attachFileIndex the attachFileIndex to set
	 */
	public void setAttachFileIndex(int attachFileIndex) {
		this.attachFileIndex = attachFileIndex;
	}

	/**
	 * @return the updateContract
	 */
	public boolean isUpdateContract() {
		return updateContract;
	}

	/**
	 * @param updateContract the updateContract to set
	 */
	public void setUpdateContract(boolean updateContract) {
		this.updateContract = updateContract;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	/**
	 * @return the remarkList
	 */
	public List<CtRemark> getRemarkList() {
		if (remarkList == null) {
			remarkList = new ArrayList<CtRemark>();
		}
		return remarkList;
	}

	/**
	 * @param remarkList the remarkList to set
	 */
	public void setRemarkList(List<CtRemark> remarkList) {
		this.remarkList = remarkList;
	}

	/**
	 * @return the attachmentList
	 */
	public List<CtAttachment> getAttachmentList() {
		if(attachmentList == null) {
			   attachmentList = new ArrayList<CtAttachment>();
		}
		return attachmentList;
	}

	/**
	 * @param attachmentList the attachmentList to set
	 */
	public void setAttachmentList(List<CtAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	/**
	 * @return the finalListOfSideMenu
	 */
	public List<String> getFinalListOfSideMenu() {
		return finalListOfSideMenu;
	}

	/**
	 * @param finalListOfSideMenu the finalListOfSideMenu to set
	 */
	public void setFinalListOfSideMenu(List<String> finalListOfSideMenu) {
		this.finalListOfSideMenu = finalListOfSideMenu;
	}
	
	/**
	 * @return the loaContractVerList
	 */
	public SelectItem[] getLoaContractVerList() {
		return loaContractVerList;
	}

	/**
	 * @param loaContractVerList the loaContractVerList to set
	 */
	public void setLoaContractVerList(SelectItem[] loaContractVerList) {
		this.loaContractVerList = loaContractVerList;
	}

	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
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
	 * @return the userMap
	 */
	public Map<Long, PmUser> getUserMap() {
		if (userMap == null) {
			userMap = new HashMap<Long, PmUser>();
		}
		return userMap;
	}

	/**
	 * @param userMap the userMap to set
	 */
	public void setUserMap(Map<Long, PmUser> userMap) {
		this.userMap = userMap;
	}

	/**
	 * @return the approverMap
	 */
	public HashMap<String, PmUserGroupUser> getApproverMap() {
		if (approverMap == null) {
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
	 * @return the deductionIndex
	 */
	public int getDeductionIndex() {
		return deductionIndex;
	}

	/**
	 * @param deductionIndex the deductionIndex to set
	 */
	public void setDeductionIndex(int deductionIndex) {
		this.deductionIndex = deductionIndex;
	}

	/**
	 * @return the scheduleIndex
	 */
	public int getScheduleIndex() {
		return scheduleIndex;
	}

	/**
	 * @param scheduleIndex the scheduleIndex to set
	 */
	public void setScheduleIndex(int scheduleIndex) {
		this.scheduleIndex = scheduleIndex;
	}

	/**
	 * @return the factoring
	 */
	public int getFactoring() {
		return factoring;
	}

	/**
	 * @param factoring the factoring to set
	 */
	public void setFactoring(int factoring) {
		this.factoring = factoring;
	}

	/**
	 * @return the paymentTracking
	 */
	public int getPaymentTracking() {
		return paymentTracking;
	}

	/**
	 * @param paymentTracking the paymentTracking to set
	 */
	public void setPaymentTracking(int paymentTracking) {
		this.paymentTracking = paymentTracking;
	}

	/**
	 * @return the itemIndex
	 */
	public int getItemIndex() {
		return itemIndex;
	}

	/**
	 * @param itemIndex the itemIndex to set
	 */
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}

	/**
	 * @return the settingIndex
	 */
	public int getSettingIndex() {
		return settingIndex;
	}

	/**
	 * @param settingIndex the settingIndex to set
	 */
	public void setSettingIndex(int settingIndex) {
		this.settingIndex = settingIndex;
	}

	/**
	 * @return the fulfilmentMenuModel
	 *//*
	public MenuModel getFulfilmentMenuModel() {
		return fulfilmentMenuModel;
	}

	*//**
	 * @param fulfilmentMenuModel the fulfilmentMenuModel to set
	 *//*
	public void setFulfilmentMenuModel(MenuModel fulfilmentMenuModel) {
		this.fulfilmentMenuModel = fulfilmentMenuModel;
	}*/

	/**
	 * @return the ctCircularVer
	 */
	public CtCircularVer getCtCircularVer() {
		return ctCircularVer;
	}

	/**
	 * @param ctCircularVer the ctCircularVer to set
	 */
	public void setCtCircularVer(CtCircularVer ctCircularVer) {
		this.ctCircularVer = ctCircularVer;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
	
	/**
	 * @return the pmDigiSign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * @param pmDigiSign the pmDigiSign to set
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}	

	/**
	 * @return the searchMinistryProfile
	 */
	public Long getSearchMinistryProfileId() {
		return searchMinistryProfileId;
	}

	/**
	 * @param searchMinistryProfile the searchMinistryProfileId to set
	 */
	public void setSearchMinistryProfileId(Long searchMinistryProfileId) {
		this.searchMinistryProfileId = searchMinistryProfileId;
	}

	/**
	 * @return the searchPtjProfile
	 */
	public PmOrgProfile getSearchPtjProfile() {
		return searchPtjProfile;
	}

	/**
	 * @param searchPtjProfile the searchPtjProfile to set
	 */
	public void setSearchPtjProfile(PmOrgProfile searchPtjProfile) {
		this.searchPtjProfile = searchPtjProfile;
	}

	public boolean isViewContract() {
		return viewContract;
	}

	public void setViewContract(boolean viewContract) {
		this.viewContract = viewContract;
	}

	/**
	 * @return the contractDataModel
	 */
	public IPagingDataModel<VCtContract> getContractDataModel() {
		return contractDataModel;
	}

	/**
	 * @param contractDataModel the contractDataModel to set
	 */
	public void setContractDataModel(IPagingDataModel<VCtContract> contractDataModel) {
		this.contractDataModel = contractDataModel;
	}

	/**
	 * Pagination for available contract search list
	 */
	private class ContractPagingDataModel extends PagingDataModel<VCtContract> {
		private static final long serialVersionUID = 1L;

		public ContractPagingDataModel() {
			super("contractTbl", false);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<VCtContract> pageChangeListener(ISearchBean<VCtContract> searchBean) {
			IContractSO contractSO = null;
			boolean isSupplierUser = false;
			try {
				FacesScopes fc = new FacesScopes();
				if (fc.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
						|| fc.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
						|| fc.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
						|| fc.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
					// supplier
					isSupplierUser = true;
					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(fc.getUserSessionBean().getUserId());
					ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);	
					SupplierSearchResDTO supplierSearchResDTO = supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
					
					if (supplierSearchResDTO.getResultBean() != null && supplierSearchResDTO.getResultBean().getResultList() != null 
			        		&& !supplierSearchResDTO.getResultBean().getResultList().isEmpty()) {
						List<Long> supplierIdList = new ArrayList<Long>();
		  		        for (SmSupplier supplier : supplierSearchResDTO.getResultBean().getResultList()) {
		  		        	supplierIdList.add(supplier.getSupplierId());
		  		        }
						searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.supplierId",SearchOperator.OPERATOR_IN.value(), 
								supplierIdList));
					}	
				}
				
				if (getCtNumber() != null && getCtNumber().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),
													getCtNumber());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getCtName() != null && getCtName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),
													getCtName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchPtjProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.ownerOrgProfile.orgProfileId", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjProfile().getOrgProfileId()));
					
				} else if (getSearchMinistryProfileId() != null) {
					String searchBeanStr = "ctContract.ownerOrgProfile." + OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value());
					searchBean.getDataFilterBeans().add(new SearchFilterBean(searchBeanStr, SearchOperator.OPERATOR_EQUALS.value(),
							getSearchMinistryProfileId()));
				}
				if (getCtCircularNo() != null && getCtCircularNo().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircularVer.circularNo",SearchOperator.OPERATOR_LIKE.value(),
													getCtCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getCtCircularName() != null && getCtCircularName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctCircularVer.name",SearchOperator.OPERATOR_LIKE.value(),
													getCtCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getCtLoaNo() != null && getCtLoaNo().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContractValue.loaNo",SearchOperator.OPERATOR_LIKE.value(),
							getCtLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getCtSupplierName() != null && getCtSupplierName().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("companyName",SearchOperator.OPERATOR_LIKE.value(),
													getCtSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getCtDateStart() != null && getCtDateEnd() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("effDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getCtDateStart()));	
					searchBean.getDataFilterBeans().add(new SearchFilterBean("expDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getCtDateEnd()));		
				
				}
				if (getCtDateStart() != null && getCtDateEnd() == null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("effDate",SearchOperator.OPERATOR_EQUALS.value(), getCtDateStart()));					
				}
				if (getCtDateStart() == null && getCtDateEnd() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("expDate",SearchOperator.OPERATOR_EQUALS.value(), getCtDateEnd()));	
				}
				if (getCtPhysicalNo() != null && getCtPhysicalNo().length() > 0) {
				   	SearchFilterBean filterBean = new SearchFilterBean("contractPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),
				   									getCtPhysicalNo().trim());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getCtPhysicalLoaNo() != null && getCtPhysicalLoaNo().length() > 0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctContractVer.ctContractValue.loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),
													getCtPhysicalLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getCtStatus() > 0) {
					if (getCtStatus() == ContractConst.STATUS_ALL) {
						searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
								new SearchFilterBean(
										"status",
										SearchOperator.OPERATOR_EQUALS.value(),
										ContractConst.STATUS_COMPLETE),
								new SearchFilterBean(
										"status",
										SearchOperator.OPERATOR_EQUALS.value(), 
										ContractConst.STATUS_INCOMPLETE,
								SearchCondition.CONDITION_OR.value())));
						
					} else {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("status", 
								SearchOperator.OPERATOR_EQUALS.value(), getCtStatus()));
					}
				}
				if (getTerminationTypeId() != null) {
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(getTerminationTypeId());
					reqDTO.setLanguageCode(fc.getUserLocale().getLanguage());
					
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
					String parameterCode = resDTO.getPmParameter().getParameterCode();
					
					if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("terminate",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("mutualTerminate",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("remedy",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
				}
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				if (getCtContractStatus() != null) {
					reqDTO.setStatusId(getCtContractStatus());
				}
				reqDTO.setFromSupplier(isSupplierUser);
				reqDTO.setvCtContractSearchBean(searchBean);
				ContractResDTO resDTO = contractSO.searchContractDocument(reqDTO);
				setSelectedTabIndex(0);
					
				if (resDTO.isSuccessful()) {
					return resDTO.getvCtContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}