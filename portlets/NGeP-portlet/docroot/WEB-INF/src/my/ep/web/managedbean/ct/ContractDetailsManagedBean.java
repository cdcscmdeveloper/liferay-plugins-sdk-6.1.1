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
import org.apache.commons.lang3.StringUtils;

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
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
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
	private String taskName;
	private String popupForImportedItems = "L";
	private String searchContractNo;
	private String searchContractName;
	private PmOrgProfile searchMinistryProfile;
	private PmOrgProfile searchPtjGroupProfile;
	private PmOrgProfile searchPtjProfile;
	private String searchCircularNo;
	private String searchCircularName;
    private Date searchCtStartDate;
	private Date searchCtEndDate;
	private String searchSupplierName;
	private String searchLoaNo;
	private String searchLoaPhysicalNo;
	private String searchCtPhysicalNo;
	private int searchContractStatus;
	private Long searchTerminationType;
	private int searchStatus;
	private String externalPageUrl;
	private String warningPercentage;
	private String textBeforeSign;
	private String buttonAction;	
	private String gotoPreviewPage;	
	private String fileDirectory;
	private Long loaContractVerId;
	private Date asAtDate = null;
	private int selectedTabIndex = 0;
	private int attachFileIndex;
	private boolean autoUpdate = true;
	private List<CtRemark> remarkList;
	private List<CtAttachment> attachmentList;
	private List<String> finalListOfSideMenu;
	private SelectItem[] loaContractVerList;
	private DualListModel<PmUser> approverModel;
	private Map<Long, PmUserGroup> userGroupMap;
	private Map<Long, PmUser> userMap;
	private HashMap<String, PmUserGroupUser> approverMap;
		private PmDigiSign pmDigiSign;	
	private IPagingDataModel<VCtContract> contractDataModel = new ContractPagingDataModel();
	
	public int deductionIndex =99;
	public int scheduleIndex =99;
	public int factoring = 99;
	public int paymentTracking = 99;
	public int itemIndex = 99;
	public int settingIndex = 101;
	public int agencyIndex = 102;
	private int index;
	private boolean trackingDiarySearch;
	
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
	 * @return the searchContractNo
	 */
	public String getSearchContractNo() {
		return searchContractNo;
	}

	/**
	 * @param searchContractNo the searchContractNo to set
	 */
	public void setSearchContractNo(String searchContractNo) {
		this.searchContractNo = searchContractNo;
	}
	
	/**
	 * @return the searchContractName
	 */
	public String getSearchContractName() {
		return searchContractName;
	}

	/**
	 * @param searchContractName the searchContractName to set
	 */
	public void setSearchContractName(String searchContractName) {
		this.searchContractName = searchContractName;
	}

	/**
	 * @return the searchCircularNo
	 */
	public String getSearchCircularNo() {
		return searchCircularNo;
	}

	/**
	 * @param searchCircularNo the searchCircularNo to set
	 */
	public void setSearchCircularNo(String searchCircularNo) {
		this.searchCircularNo = searchCircularNo;
	}
	
	/**
	 * @return the searchCircularName
	 */
	public String getSearchCircularName() {
		return searchCircularName;
	}

	/**
	 * @param searchCircularName the searchCircularName to set
	 */
	public void setSearchCircularName(String searchCircularName) {
		this.searchCircularName = searchCircularName;
	}
	
	/**
	 * @return the searchCtPhysicalNo
	 */
	public String getSearchCtPhysicalNo() {
		
		return searchCtPhysicalNo;
	}

	/**
	 * @param searchCtPhysicalNo the searchCtPhysicalNo to set
	 */
	public void setSearchCtPhysicalNo(String searchCtPhysicalNo) {
		this.searchCtPhysicalNo = searchCtPhysicalNo;
	}
	
	/**
	 * @return the searchContractStatus
	 */
	public int getSearchContractStatus() {
		return searchContractStatus;
	}

	/**
	 * @param searchContractStatus the searchContractStatus to set
	 */
	public void setSearchContractStatus(int searchContractStatus) {
		this.searchContractStatus = searchContractStatus;
	}

	/**
	 * @return the searchStatus
	 */
	public int getSearchStatus() {
		return searchStatus;
	}

	/**
	 * @param searchStatus the searchStatus to set
	 */
	public void setSearchStatus(int searchStatus) {
		this.searchStatus = searchStatus;
	}
	/**
	 * @return the searchCtStartDate
	 */
	public Date getSearchCtStartDate() {
		return searchCtStartDate;
	}

	/**
	 * @param searchCtStartDate the searchCtStartDate to set
	 */
	public void setSearchCtStartDate(Date searchCtStartDate) {
		this.searchCtStartDate = searchCtStartDate;
	}
	
	/**
	 * @return the searchLoaPhysicalNo
	 */
	public String getSearchLoaPhysicalNo() {
		
		return searchLoaPhysicalNo;
	}
	
	/**
	 * @param searchLoaPhysicalNo the searchLoaPhysicalNo to set
	 */
	public void setSearchLoaPhysicalNo(String searchLoaPhysicalNo) {
		this.searchLoaPhysicalNo = searchLoaPhysicalNo;
	}

	/**
	 * @return the searchTerminationType
	 */
	public Long getSearchTerminationType() {
		return searchTerminationType;
	}

	/**
	 * @param searchTerminationType the searchTerminationType to set
	 */
	public void setSearchTerminationType(Long searchTerminationType) {
		this.searchTerminationType = searchTerminationType;
	}

	/**
	 * @return the searchCtEndDate
	 */
	public Date getSearchCtEndDate() {
		return searchCtEndDate;
	}

	/**
	 * @param searchCtEndDate the searchCtEndDate to set
	 */
	public void setSearchCtEndDate(Date searchCtEndDate) {
		this.searchCtEndDate = searchCtEndDate;
	}	

	/**
	 * @return the searchSupplierName
	 */
	public String getSearchSupplierName() {
		return searchSupplierName;
	}

	/**
	 * @param searchSupplierName the searchSupplierName to set
	 */
	public void setSearchSupplierName(String searchSupplierName) {
		this.searchSupplierName = searchSupplierName;
	}
	
	/**
	 * @return the searchLoaNo
	 */
	public String getSearchLoaNo() {
		return searchLoaNo;
	}

	/**
	 * @param searchLoaNo the searchLoaNo to set
	 */
	public void setSearchLoaNo(String searchLoaNo) {
		this.searchLoaNo = searchLoaNo;
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
	public PmOrgProfile getSearchMinistryProfile() {
		return searchMinistryProfile;
	}

	/**
	 * @param searchMinistryProfile the searchMinistryProfile to set
	 */
	public void setSearchMinistryProfile(PmOrgProfile searchMinistryProfile) {
		this.searchMinistryProfile = searchMinistryProfile;
	}
	
	/**
	 * @return the searchPtjGroupProfile
	 */
	public PmOrgProfile getSearchPtjGroupProfile() {
		return searchPtjGroupProfile;
	}

	/**
	 * @param searchPtjGroupProfile the searchPtjGroupProfile to set
	 */
	public void setSearchPtjGroupProfile(PmOrgProfile searchPtjGroupProfile) {
		this.searchPtjGroupProfile = searchPtjGroupProfile;
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

	/**
	 * @return the agencyIndex
	 */
	public int getAgencyIndex() {
		return agencyIndex;
	}

	/**
	 * @param agencyIndex the agencyIndex to set
	 */
	public void setAgencyIndex(int agencyIndex) {
		this.agencyIndex = agencyIndex;
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
	 * @return the trackingDiarySearch
	 */
	public boolean isTrackingDiarySearch() {
		return trackingDiarySearch;
	}

	/**
	 * @param trackingDiarySearch the trackingDiarySearch to set
	 */
	public void setTrackingDiarySearch(boolean trackingDiarySearch) {
		this.trackingDiarySearch = trackingDiarySearch;
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
						searchBean.getDataFilterBeans().add(new SearchFilterBean("SUPPLIER_ID",SearchOperator.OPERATOR_IN.value(), 
								supplierIdList));
					}	
				}
				
				if (StringUtils.isNotBlank(getSearchContractNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchContractNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchContractName())) {
					SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchContractName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchPtjProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("PTJ_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjProfile().getOrgProfileId()));
					
				} else if (getSearchPtjGroupProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("PTJ_GROUP_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchPtjGroupProfile().getOrgProfileId()));
				} else if (getSearchMinistryProfile() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("MINISTRY_ORG_ID", SearchOperator.OPERATOR_EQUALS.value(),
							getSearchMinistryProfile().getOrgProfileId()));
				}
				if (StringUtils.isNotBlank(getSearchCircularNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("CIRCULAR_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchCircularName())) {
					SearchFilterBean filterBean = new SearchFilterBean("CIRCULAR_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (StringUtils.isNotBlank(getSearchLoaNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("LOA_NO",SearchOperator.OPERATOR_LIKE.value(),
							getSearchLoaNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (StringUtils.isNotBlank(getSearchSupplierName())) {
					SearchFilterBean filterBean = new SearchFilterBean("COMPANY_NAME",SearchOperator.OPERATOR_LIKE.value(),
													getSearchSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if (getSearchCtStartDate() != null && getSearchCtEndDate() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EFF_DATE",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getSearchCtStartDate()));	
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EXP_DATE",SearchOperator.OPERATOR_LESS_EQUAL.value(), getSearchCtEndDate()));		
				
				}
				if (getSearchCtStartDate() != null && getSearchCtEndDate() == null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EFF_DATE",SearchOperator.OPERATOR_EQUALS.value(), getSearchCtStartDate()));					
				}
				if (getSearchCtStartDate() == null && getSearchCtEndDate() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("EXP_DATE",SearchOperator.OPERATOR_EQUALS.value(), getSearchCtEndDate()));	
				}
				if (StringUtils.isNotBlank(getSearchCtPhysicalNo())) {
				   	SearchFilterBean filterBean = new SearchFilterBean("CONTRACT_PHYSICAL_NO",SearchOperator.OPERATOR_LIKE.value(),
				   									getSearchCtPhysicalNo().trim());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (StringUtils.isNotBlank(getSearchLoaPhysicalNo())) {
					SearchFilterBean filterBean = new SearchFilterBean("LOA_PHYSICAL_NO",SearchOperator.OPERATOR_LIKE.value(),
													getSearchLoaPhysicalNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if (getSearchStatus() > 0) {
					if (getSearchStatus() == ContractConst.STATUS_ALL) {
						searchBean.getDataFilterBeans().add(new SearchMultiFilterBean(
								new SearchFilterBean(
										"STATUS",
										SearchOperator.OPERATOR_EQUALS.value(),
										ContractConst.STATUS_COMPLETE),
								new SearchFilterBean(
										"STATUS",
										SearchOperator.OPERATOR_EQUALS.value(), 
										ContractConst.STATUS_INCOMPLETE,
								SearchCondition.CONDITION_OR.value())));
						
					} else {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("STATUS", 
								SearchOperator.OPERATOR_EQUALS.value(), getSearchStatus()));
					}
				}
				if (getSearchTerminationType() != null) {
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(getSearchTerminationType());
					reqDTO.setLanguageCode(fc.getUserLocale().getLanguage());
					
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
					String parameterCode = resDTO.getPmParameter().getParameterCode();
					
					if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_TERMINATE",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_MUTUAL_TERMINATE",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					} else if (parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("IS_REMEDY",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
				}
				if (getSearchContractStatus() > 0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("CONTRACT_STATUS", 
							SearchOperator.OPERATOR_EQUALS.value(), getSearchContractStatus()));
				}
				searchBean.setUseEntityClass(true);
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				
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