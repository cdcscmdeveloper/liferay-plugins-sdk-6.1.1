package my.ep.web.managedbean.sm.applicationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;
//#28175
import my.ep.persistence.entity.pm.common.PmRemarkCategory;
//#34031 Start
import my.ep.persistence.entity.sm.SmRevisit;
import my.ep.persistence.entity.sm.SmRvAssistOfficer;
import my.ep.persistence.entity.sm.SmRvDocFile;
import my.ep.persistence.entity.sm.SmRvSuppDoc;
//#34031 End

@ManagedBean(name = "viewSuppProfileMB")
@SessionScoped
public class ViewSuppProfileManagedBean extends
		BaseReviewApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029253736925390560L;
	private String physicalConsulting;
	private PmUser applApprover;
	private String approvedDate;
	private SmSupplier selectedSupplier;
	private SmSupplierCategory selectedCategory;
	private SmSupplierBasicInfoVO basicInfo;
	private DisciplinaryActionVO disciplineVO;
	private String applnApprovalRemarks;
	private boolean showParentMsg;
	private boolean allowedToDoMaintenance;
	private boolean hasActiveTransaction;
	private boolean editDisciplinaryAction;
	private PmRegStatus existingRegStatus;
	private Long oldRegStatusId;
	private Date existingMofExpiryDate;
	private List<SmSupplierCategory> selectedApprovedCategories;
	private List<SmSupplierCategory> selectedPendingApprovedCategories;
	private List<SmSupplierCategory> deleteApprovedCategories;
	private IPagingDataModel<SmSupplier> smSupplierDataModel = new SmSupplierPagingDataModel();
	private List<Object[]> activeApplList;
	private boolean hasRegStatusChanged = false;
	private boolean hasMofExpiryDateChanged = false;
	private Long existingStatusId;
	private SmSupplierBank[] selectedSuppBanks;
	private List<PmFinancialOrg> financialOrgLs;
	
	private SmAddress smBusinessAdd;
	private SmAddress smPermenantAdd;
	private SmAddress smResAdd;
	
	private boolean sameAsOwner;
	private boolean disabledCompanyNameInMaintenance;
	private boolean disabledBusinessTypeInMaintenance;
	
	private List<SelectItem> countryLs;
	private List<SelectItem> businessStateLs;
	private List<SelectItem> residentialStateLs;
	private List<SelectItem> permenantStateLs;

	private List<SelectItem> businessDistrictLs;
	private List<SelectItem> residentialDistrictLs;
	private List<SelectItem> permenantDistrictLs;

	//13320-Start
	private List<SelectItem> businessDivisionLs;
	private List<SelectItem> residentialDivisionLs;
	private List<SelectItem> permenantDivisionLs;
	private Long divisionId;
	private boolean divisionBox = true;
	//13320-End
	private List<SelectItem> businessCityLs;
	private List<SelectItem> residentialCityLs;
	private List<SelectItem> permenantCityLs;
	
	private List<PmParameterDesc> serviceLs;
	
	private boolean sabahSarawakType;
	
	private SmPersonnel smPersonnel;
	private SmSupplierOwner smSuppOwner;
	private List<SmSupplierOwner> smSuppOwners = new ArrayList();
	private SmPersonnelAddress smPersonnelAdd;
	
	private Integer userBumiFlg;
	private Integer userMuslimFlg;
	
	private Long myCountryId;
	
	private SmGstSupplier smGstSupplier;
	
	private boolean gstFlag;
	
	private SmSupplierBusiness selectedBusinessNature;
	private SmSupplierBusiness[] selectedBusinessNatureLs;
	
	//27437 : Disciplinary Action
	private IPagingDataModel<SmSupplier> basicSupplierDataModel = new SmBasicSupplierPagingDataModel();
	private String searchByCompanyName;
	private String searchByRegNo;
	private String searchByEpNo;
	private List<SmSupplier> basicSupplierList;
	//26238 - end
	
	private String disciplinaryActionMsg; 
	
	//27857: Disciplinary Action - start - 26238
	private boolean cancelCategoryCodeExist;
	private boolean suspendRegistrationExist;
	private boolean cancelRegistrationExist;
	private boolean blacklistOwnerOrDirectorExist;
	private boolean bumiputeraStatusCancelExist;
	private boolean manufacturerStatusCancelExist;
	//27857: Disciplinary Action - end - 26238
	
	//Start - 27815
	private boolean disableDeleteButtonAprrovedCategoryCodePanel;
	//End - 27815
	
	// Start 27815
	private boolean disabledApprovedCategories;
	// End 27815
	
	//29181: Disciplinary Action - start - 26238
	private boolean editDisciplinaryActionCheckBox;
	//29181: Disciplinary Action - end - 26238
	
	// #28175 start
	private String catCode;
	private List<PmRemarkCategory> historyList;
	// #28175 end
	
	// #26238 start
	private boolean cancelMofAccount;
	// #26238 eend
	
	private boolean hasActiveDA; //#34626
	
	private Map<Long, String> tempCategoryLs; //#34209
	
	// #28895_35501 Start - AUDIT TRAIL SM
	private SmAppl oldSmAppl;
	private List<SmSupplierCategory> oldApprovedCategoryList;
	private String oldPoApprOverlappedRemark;
	private String oldApproverRemark;
	// #28895_35501 End
	// #34031 Start
	private SmRevisit smRevisit;
	private String svoRoleCode;
	private SmRvAssistOfficer selectedRvAssistOfficer;
	private boolean disableAddSmRvAssistOfficerBtn;
	private int smRvAttachedFileIndex;
	private SmRvSuppDoc smRvSuppDoc;
	private SmRvDocFile smRvDocFile;
	// #34031 End
		
	public ViewSuppProfileManagedBean() {

	}
	/*
	 * CREATE CLASS PAGING DATA MODEL
	 * HERE EXAMPLE USING NATIVE QUERY, IF CRITERIA STILL SAME, JUZ USE AND RETURN ENTITY
	 * 
	 * */
	private class SmSupplierPagingDataModel extends PagingDataModel<SmSupplier> { 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");//REFER TO DATATABLE ID
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);
			reqDTO.setViewApplicationHistory(isViewApplicationHistory());
			reqDTO.setDisplaySearchPage(isDisplaySearchPage());

			try {
				if (!isSupplierUser()) {
					if (!isSearchCriteriaEmpty()) {
						return getApplicationHistoryResultBean(reqDTO);
					} else {
						return new SearchResultBean();
					}
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<SmSupplier> getApplicationHistoryResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			reqDTO.setMofAccountNo(getSearchMofNo());
			reqDTO.setEpNo(getSearchEpNo());
			reqDTO.setPmRegStatusId(getSearchPmRegStatusId());
			reqDTO.setSupplierType(getSearchSuppType());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setPmZoneId(getSearchPmZoneId());
			//28237
			reqDTO.setDisplaySearchPage(true);
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}
	//27437 : Disciplinary action - Start
	//Basic account maintenance by Approver
	private class SmBasicSupplierPagingDataModel extends PagingDataModel<SmSupplier> { 
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmBasicSupplierPagingDataModel() {
			super("supplierBasicAccountDtId",true);//REFER TO DATATABLE ID - XHTML
		}

		@Override
		protected ISearchResultBean<SmSupplier> pageChangeListener(
				ISearchBean<SmSupplier> searchBean) {
			SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
			reqDTO.setSupplierSearchBean(searchBean);
			reqDTO.setDisplaySearchPage(isDisplaySearchPage());
			getSearchSupplier().getCompanyName();
			getSearchSupplier().getRegNo();
			getSearchSupplier().getEpNo();
			reqDTO.setSmSupplier(getSearchSupplier());

			try {
				if(isBasicAccountMaintenance()){
					return getBasicSupplierResultBean(reqDTO);
				} else {
					return new SearchResultBean();
				}
			} catch (Exception ex) {
				return new SearchResultBean();
			}
		}
		
		private ISearchResultBean<SmSupplier> getBasicSupplierResultBean(
				SmApplRevisionReqDTO reqDTO) {
			reqDTO.setSmSupplier(getSearchSupplier());
			if(getSearchSupplier().getCompanyName() != null)
				reqDTO.getSmSupplier().setCompanyName(getSearchSupplier().getCompanyName());
			if(getSearchSupplier().getRegNo() != null)
				reqDTO.getSmSupplier().setRegNo(getSearchSupplier().getRegNo());
			if(getSearchSupplier().getEpNo() != null)
				reqDTO.getSmSupplier().setEpNo(getSearchSupplier().getEpNo());
			
			// 28273: Disciplinary Action - start - 26238
			SmAppl smAppl = new SmAppl();
			smAppl.setApplType("O");
			reqDTO.setSmAppl(smAppl);
			// 28273: Disciplinary Action - end - 26238
			
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveBasicSupplierForMaintenance(reqDTO)
					.getSupplierResultBean();
		}
	} 
	
	/**
	 * Search supplier latest application
	 */
	public void searchBasicSupplier() {
		validateAtLeastOneSearchCriteria();
		//HERE WILL START PROCESS PAGING DATA MODEL
		basicSupplierDataModel.reloadData();
	}

	public IPagingDataModel<SmSupplier> getBasicSupplierDataModel() {
		return basicSupplierDataModel;
	}

	public void setBasicSupplierDataModel(
			IPagingDataModel<SmSupplier> basicSupplierDataModel) {
		this.basicSupplierDataModel = basicSupplierDataModel;
	}
	//27437 : Disciplinary Action - end

	/**
	 * Search supplier latest application
	 */
	public void searchSupplier() {
		validateAtLeastOneSearchCriteria();
		//HERE WILL START PROCESS PAGING DATA MODEL
		smSupplierDataModel.reloadData();
	}

	public IPagingDataModel<SmSupplier> getSmSupplierDataModel() {
		return smSupplierDataModel;
	}

	public void setSmSupplierDataModel(
			IPagingDataModel<SmSupplier> smSupplierDataModel) {
		this.smSupplierDataModel = smSupplierDataModel;
	}

	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}

	public SmSupplierCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SmSupplierCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public SmSupplierBasicInfoVO getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(SmSupplierBasicInfoVO basicInfo) {
		this.basicInfo = basicInfo;
	}

	public String getApplnApprovalRemarks() {
		return applnApprovalRemarks;
	}

	public void setApplnApprovalRemarks(String applnApprovalRemarks) {
		this.applnApprovalRemarks = applnApprovalRemarks;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public PmUser getApplApprover() {
		return applApprover;
	}

	public void setApplApprover(PmUser applApprover) {
		this.applApprover = applApprover;
	}

	public String getApprovedDate() {
		return approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public boolean isAllowedToDoMaintenance() {
		return allowedToDoMaintenance;
	}

	public void setAllowedToDoMaintenance(boolean allowedToDoMaintenance) {
		this.allowedToDoMaintenance = allowedToDoMaintenance;
	}

	public PmRegStatus getExistingRegStatus() {
		return existingRegStatus;
	}

	public void setExistingRegStatus(PmRegStatus existingRegStatus) {
		this.existingRegStatus = existingRegStatus;
	}

	public List<SmSupplierCategory> getDeleteApprovedCategories() {
		return deleteApprovedCategories;
	}

	public void setDeleteApprovedCategories(
			List<SmSupplierCategory> deleteApprovedCategories) {
		this.deleteApprovedCategories = deleteApprovedCategories;
	}

	public DisciplinaryActionVO getDisciplineVO() {
		return disciplineVO;
	}

	public void setDisciplineVO(DisciplinaryActionVO disciplineVO) {
		this.disciplineVO = disciplineVO;
	}

	public boolean isEditDisciplinaryAction() {
		return editDisciplinaryAction;
	}

	public void setEditDisciplinaryAction(boolean editDisciplinaryAction) {
		this.editDisciplinaryAction = editDisciplinaryAction;
	}

	public Date getExistingMofExpiryDate() {
		return existingMofExpiryDate;
	}

	public void setExistingMofExpiryDate(Date existingMofExpiryDate) {
		this.existingMofExpiryDate = existingMofExpiryDate;
	}

	public List<SmSupplierCategory> getSelectedApprovedCategories() {
		return selectedApprovedCategories;
	}

	public void setSelectedApprovedCategories(
			List<SmSupplierCategory> selectedApprovedCategories) {
		this.selectedApprovedCategories = selectedApprovedCategories;
	}

	public List<SmSupplierCategory> getSelectedPendingApprovedCategories() {
		return selectedPendingApprovedCategories;
	}

	public void setSelectedPendingApprovedCategories(
			List<SmSupplierCategory> selectedPendingApprovedCategories) {
		this.selectedPendingApprovedCategories = selectedPendingApprovedCategories;
	}

	public boolean isHasActiveTransaction() {
		return hasActiveTransaction;
	}

	public void setHasActiveTransaction(boolean hasActiveTransaction) {
		this.hasActiveTransaction = hasActiveTransaction;
	}

	public String getPhysicalConsulting() {
		return physicalConsulting;
	}

	public void setPhysicalConsulting(String physicalConsulting) {
		this.physicalConsulting = physicalConsulting;
	}

	public List<Object[]> getActiveApplList() {
		return activeApplList;
	}

	public void setActiveApplList(List<Object[]> activeApplList) {
		this.activeApplList = activeApplList;
	}

	public boolean isHasRegStatusChanged() {
		return hasRegStatusChanged;
	}

	public void setHasRegStatusChanged(boolean hasRegStatusChanged) {
		this.hasRegStatusChanged = hasRegStatusChanged;
	}

	public boolean isHasMofExpiryDateChanged() {
		return hasMofExpiryDateChanged;
	}

	public void setHasMofExpiryDateChanged(boolean hasMofExpiryDateChanged) {
		this.hasMofExpiryDateChanged = hasMofExpiryDateChanged;
	}

	public Long getExistingStatusId() {
		return existingStatusId;
	}

	public void setExistingStatusId(Long existingStatusId) {
		this.existingStatusId = existingStatusId;
	}

	public Long getOldRegStatusId() {
		return oldRegStatusId;
	}

	public void setOldRegStatusId(Long oldRegStatusId) {
		this.oldRegStatusId = oldRegStatusId;
	}

	public SmSupplierBusiness[] getSelectedBusinessNatureLs() {
		return selectedBusinessNatureLs;
	}

	public void setSelectedBusinessNatureLs(SmSupplierBusiness[] selectedBusinessNatureLs) {
		this.selectedBusinessNatureLs = selectedBusinessNatureLs;
	}

	public SmSupplierBank[] getSelectedSuppBanks() {
		return selectedSuppBanks;
	}

	public void setSelectedSuppBanks(SmSupplierBank[] selectedSuppBanks) {
		this.selectedSuppBanks = selectedSuppBanks;
	}

	public List<PmFinancialOrg> getFinancialOrgLs() {
		return financialOrgLs;
	}

	public void setFinancialOrgLs(List<PmFinancialOrg> financialOrgLs) {
		this.financialOrgLs = financialOrgLs;
	}

	public SmAddress getSmBusinessAdd() {
		return smBusinessAdd;
	}

	public void setSmBusinessAdd(SmAddress smBusinessAdd) {
		this.smBusinessAdd = smBusinessAdd;
	}

	public SmAddress getSmPermenantAdd() {
		return smPermenantAdd;
	}

	public void setSmPermenantAdd(SmAddress smPermenantAdd) {
		this.smPermenantAdd = smPermenantAdd;
	}

	public SmAddress getSmResAdd() {
		return smResAdd;
	}

	public void setSmResAdd(SmAddress smResAdd) {
		this.smResAdd = smResAdd;
	}

	public boolean isSameAsOwner() {
		return sameAsOwner;
	}

	public void setSameAsOwner(boolean sameAsOwner) {
		this.sameAsOwner = sameAsOwner;
	}

	public boolean isDisabledCompanyNameInMaintenance() {
		return disabledCompanyNameInMaintenance;
	}

	public void setDisabledCompanyNameInMaintenance(boolean disabledCompanyNameInMaintenance) {
		this.disabledCompanyNameInMaintenance = disabledCompanyNameInMaintenance;
	}

	public boolean isDisabledBusinessTypeInMaintenance() {
		return disabledBusinessTypeInMaintenance;
	}

	public void setDisabledBusinessTypeInMaintenance(boolean disabledBusinessTypeInMaintenance) {
		this.disabledBusinessTypeInMaintenance = disabledBusinessTypeInMaintenance;
	}

	public List<SelectItem> getCountryLs() {
		return countryLs;
	}

	public void setCountryLs(List<SelectItem> countryLs) {
		this.countryLs = countryLs;
	}

	public List<SelectItem> getBusinessStateLs() {
		return businessStateLs;
	}

	public void setBusinessStateLs(List<SelectItem> businessStateLs) {
		this.businessStateLs = businessStateLs;
	}

	public List<SelectItem> getResidentialStateLs() {
		return residentialStateLs;
	}

	public void setResidentialStateLs(List<SelectItem> residentialStateLs) {
		this.residentialStateLs = residentialStateLs;
	}

	public List<SelectItem> getPermenantStateLs() {
		return permenantStateLs;
	}

	public void setPermenantStateLs(List<SelectItem> permenantStateLs) {
		this.permenantStateLs = permenantStateLs;
	}

	public List<SelectItem> getBusinessDistrictLs() {
		return businessDistrictLs;
	}

	public void setBusinessDistrictLs(List<SelectItem> businessDistrictLs) {
		this.businessDistrictLs = businessDistrictLs;
	}

	public List<SelectItem> getResidentialDistrictLs() {
		return residentialDistrictLs;
	}

	public void setResidentialDistrictLs(List<SelectItem> residentialDistrictLs) {
		this.residentialDistrictLs = residentialDistrictLs;
	}

	public List<SelectItem> getPermenantDistrictLs() {
		return permenantDistrictLs;
	}

	public void setPermenantDistrictLs(List<SelectItem> permenantDistrictLs) {
		this.permenantDistrictLs = permenantDistrictLs;
	}

	public List<SelectItem> getBusinessCityLs() {
		return businessCityLs;
	}

	public void setBusinessCityLs(List<SelectItem> businessCityLs) {
		this.businessCityLs = businessCityLs;
	}

	public List<SelectItem> getResidentialCityLs() {
		return residentialCityLs;
	}

	public void setResidentialCityLs(List<SelectItem> residentialCityLs) {
		this.residentialCityLs = residentialCityLs;
	}

	public List<SelectItem> getPermenantCityLs() {
		return permenantCityLs;
	}

	public void setPermenantCityLs(List<SelectItem> permenantCityLs) {
		this.permenantCityLs = permenantCityLs;
	}

	public List<PmParameterDesc> getServiceLs() {
		return serviceLs;
	}

	public void setServiceLs(List<PmParameterDesc> serviceLs) {
		this.serviceLs = serviceLs;
	}

	public boolean isSabahSarawakType() {
		return sabahSarawakType;
	}

	public void setSabahSarawakType(boolean sabahSarawakType) {
		this.sabahSarawakType = sabahSarawakType;
	}
	
	public SmPersonnel getSmPersonnel() {
		if(smPersonnel == null)
			smPersonnel = new SmPersonnel();
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	public SmSupplierOwner getSmSuppOwner() {
		if(smSuppOwner == null) {
			smSuppOwner = new SmSupplierOwner();
		}
		return smSuppOwner;
	}

	public void setSmSuppOwner(SmSupplierOwner smSuppOwner) {
		this.smSuppOwner = smSuppOwner;
	}

	public SmPersonnelAddress getSmPersonnelAdd() {
		return smPersonnelAdd;
	}

	public void setSmPersonnelAdd(SmPersonnelAddress smPersonnelAdd) {
		this.smPersonnelAdd = smPersonnelAdd;
	}

	public Integer getUserBumiFlg() {
		return userBumiFlg;
	}

	public void setUserBumiFlg(Integer userBumiFlg) {
		this.userBumiFlg = userBumiFlg;
	}

	public Integer getUserMuslimFlg() {
		return userMuslimFlg;
	}

	public void setUserMuslimFlg(Integer userMuslimFlg) {
		this.userMuslimFlg = userMuslimFlg;
	}

	public Long getMyCountryId() {
		return myCountryId;
	}

	public void setMyCountryId(Long myCountryId) {
		this.myCountryId = myCountryId;
	}

	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	public boolean isGstFlag() {
		return gstFlag;
	}

	public void setGstFlag(boolean gstFlag) {
		this.gstFlag = gstFlag;
	}

	public SmSupplierBusiness getSelectedBusinessNature() {
		return selectedBusinessNature;
	}

	public void setSelectedBusinessNature(SmSupplierBusiness selectedBusinessNature) {
		this.selectedBusinessNature = selectedBusinessNature;
	} 

	public List<SmSupplierOwner> getSmSuppOwners() {
		return smSuppOwners;
	}

	public void setSmSuppOwners(List<SmSupplierOwner> smSuppOwners) {
		this.smSuppOwners = smSuppOwners;
	}
	
	//27437 : Disciplinary Action 
	/**
	 * Search supplier latest application
	 */
	public void searchSupplierByApprover() {
		validateAtLeastOneSearchCriteria();
		//HERE WILL START PROCESS PAGING DATA MODEL
		basicSupplierDataModel.reloadData();
	}
	//26238 - END
	
	/**
	 * @return the disciplinaryActionMsg
	 */
	public String getDisciplinaryActionMsg() {
		return disciplinaryActionMsg;
	}

	/**
	 * @param disciplinaryActionMsg the disciplinaryActionMsg to set
	 */
	public void setDisciplinaryActionMsg(String disciplinaryActionMsg) {
		this.disciplinaryActionMsg = disciplinaryActionMsg;
	}

	public String getSearchByCompanyName() {
		return searchByCompanyName;
	}

	public void setSearchByCompanyName(String searchByCompanyName) {
		this.searchByCompanyName = searchByCompanyName;
	}

	public String getSearchByRegNo() {
		return searchByRegNo;
	}

	public void setSearchByRegNo(String searchByRegNo) {
		this.searchByRegNo = searchByRegNo;
	}

	public String getSearchByEpNo() {
		return searchByEpNo;
	}

	public void setSearchByEpNo(String searchByEpNo) {
		this.searchByEpNo = searchByEpNo;
	}

	public List<SmSupplier> getBasicSupplierList() {
		return basicSupplierList;
	}

	public void setBasicSupplierList(List<SmSupplier> basicSupplierList) {
		this.basicSupplierList = basicSupplierList;
	}

	//27857: Disciplinary Action - start - 26238
	public boolean isCancelCategoryCodeExist() {
		return cancelCategoryCodeExist;
	}

	public void setCancelCategoryCodeExist(boolean cancelCategoryCodeExist) {
		this.cancelCategoryCodeExist = cancelCategoryCodeExist;
	}

	public boolean isSuspendRegistrationExist() {
		return suspendRegistrationExist;
	}

	public void setSuspendRegistrationExist(boolean suspendRegistrationExist) {
		this.suspendRegistrationExist = suspendRegistrationExist;
	}

	public boolean isCancelRegistrationExist() {
		return cancelRegistrationExist;
	}

	public void setCancelRegistrationExist(boolean cancelRegistrationExist) {
		this.cancelRegistrationExist = cancelRegistrationExist;
	}

	public boolean isBlacklistOwnerOrDirectorExist() {
		return blacklistOwnerOrDirectorExist;
	}

	public void setBlacklistOwnerOrDirectorExist(boolean blacklistOwnerOrDirectorExist) {
		this.blacklistOwnerOrDirectorExist = blacklistOwnerOrDirectorExist;
	}

	public boolean isBumiputeraStatusCancelExist() {
		return bumiputeraStatusCancelExist;
	}

	public void setBumiputeraStatusCancelExist(boolean bumiputeraStatusCancelExist) {
		this.bumiputeraStatusCancelExist = bumiputeraStatusCancelExist;
	}

	public boolean isManufacturerStatusCancelExist() {
		return manufacturerStatusCancelExist;
	}

	public void setManufacturerStatusCancelExist(boolean manufacturerStatusCancelExist) {
		this.manufacturerStatusCancelExist = manufacturerStatusCancelExist;
	}
	//27857: Disciplinary Action - end - 26238

	public boolean isDisableDeleteButtonAprrovedCategoryCodePanel() {
		return disableDeleteButtonAprrovedCategoryCodePanel;
	}

	public void setDisableDeleteButtonAprrovedCategoryCodePanel(boolean disableDeleteButtonAprrovedCategoryCodePanel) {
		this.disableDeleteButtonAprrovedCategoryCodePanel = disableDeleteButtonAprrovedCategoryCodePanel;
	}

	public List<SelectItem> getBusinessDivisionLs() {
		return businessDivisionLs;
	}

	public void setBusinessDivisionLs(List<SelectItem> businessDivisionLs) {
		this.businessDivisionLs = businessDivisionLs;
	}

	public List<SelectItem> getResidentialDivisionLs() {
		return residentialDivisionLs;
	}

	public void setResidentialDivisionLs(List<SelectItem> residentialDivisionLs) {
		this.residentialDivisionLs = residentialDivisionLs;
	}

	public List<SelectItem> getPermenantDivisionLs() {
		return permenantDivisionLs;
	}

	public void setPermenantDivisionLs(List<SelectItem> permenantDivisionLs) {
		this.permenantDivisionLs = permenantDivisionLs;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	public boolean isDivisionBox() {
		return divisionBox;
	}

	public void setDivisionBox(boolean divisionBox) {
		this.divisionBox = divisionBox;
	}

	public boolean isDisabledApprovedCategories() {
		return disabledApprovedCategories;
	}

	public void setDisabledApprovedCategories(boolean disabledApprovedCategories) {
		this.disabledApprovedCategories = disabledApprovedCategories;
	}

	//29181: Disciplinary Action - start - 26238
	public boolean isEditDisciplinaryActionCheckBox() {
		return editDisciplinaryActionCheckBox;
	}

	public void setEditDisciplinaryActionCheckBox(boolean editDisciplinaryActionCheckBox) {
		this.editDisciplinaryActionCheckBox = editDisciplinaryActionCheckBox;
	}
	//29181: Disciplinary Action - end - 26238
	// #28175 start
	public String getCatCode() {
		return catCode;
	}
	
	public void setCatCode(String catCode) {
		this.catCode = catCode;
	}
	
	public List<PmRemarkCategory> getHistoryList() {
		return historyList;
	}
	
	public void setHistoryList(List<PmRemarkCategory> historyList) {
		this.historyList = historyList;
	}
	// #28175 end
	
	// #26238 start
	public boolean isCancelMofAccount() {
		return cancelMofAccount;
	}

	public void setCancelMofAccount(boolean cancelMofAccount) {
		this.cancelMofAccount = cancelMofAccount;
	}
	// #26238 end
	//#34626 - start
	public boolean isHasActiveDA() {
		return hasActiveDA;
	}

	public void setHasActiveDA(boolean hasActiveDA) {
		this.hasActiveDA = hasActiveDA;
	}
	//#34626 - end
	
	public Map<Long, String> getTempCategoryLs() {
		return tempCategoryLs;
	}

	public void setTempCategoryLs(Map<Long, String> tempCategoryLs) {
		this.tempCategoryLs = tempCategoryLs;
	}
	
	// #28895_35501 Start - AUDIT TRAIL SM
	public SmAppl getOldSmAppl() {
		return oldSmAppl;
	}

	public void setOldSmAppl(SmAppl oldSmAppl) {
		this.oldSmAppl = oldSmAppl;
	}

	public List<SmSupplierCategory> getOldApprovedCategoryList() {
		return oldApprovedCategoryList;
	}

	public void setOldApprovedCategoryList(List<SmSupplierCategory> oldApprovedCategoryList) {
		this.oldApprovedCategoryList = oldApprovedCategoryList;
	}

	public String getOldPoApprOverlappedRemark() {
		return oldPoApprOverlappedRemark;
	}

	public void setOldPoApprOverlappedRemark(String oldPoApprOverlappedRemark) {
		this.oldPoApprOverlappedRemark = oldPoApprOverlappedRemark;
	}

	public String getOldApproverRemark() {
		return oldApproverRemark;
	}

	public void setOldApproverRemark(String oldApproverRemark) {
		this.oldApproverRemark = oldApproverRemark;
	}
	// #28895_35501 End
	// #34031 Start
	public SmRevisit getSmRevisit() {
		return smRevisit;
	}

	public void setSmRevisit(SmRevisit smRevisit) {
		this.smRevisit = smRevisit;
	}

	public String getSvoRoleCode() {
		return svoRoleCode;
	}

	public void setSvoRoleCode(String svoRoleCode) {
		this.svoRoleCode = svoRoleCode;
	}

	public SmRvAssistOfficer getSelectedRvAssistOfficer() {
		return selectedRvAssistOfficer;
	}

	public void setSelectedRvAssistOfficer(SmRvAssistOfficer selectedRvAssistOfficer) {
		this.selectedRvAssistOfficer = selectedRvAssistOfficer;
	}

	public boolean isDisableAddSmRvAssistOfficerBtn() {
		return disableAddSmRvAssistOfficerBtn;
	}

	public void setDisableAddSmRvAssistOfficerBtn(boolean disableAddSmRvAssistOfficerBtn) {
		this.disableAddSmRvAssistOfficerBtn = disableAddSmRvAssistOfficerBtn;
	}

	public int getSmRvAttachedFileIndex() {
		return smRvAttachedFileIndex;
	}

	public void setSmRvAttachedFileIndex(int smRvAttachedFileIndex) {
		this.smRvAttachedFileIndex = smRvAttachedFileIndex;
	}

	public SmRvSuppDoc getSmRvSuppDoc() {
		return smRvSuppDoc;
	}

	public void setSmRvSuppDoc(SmRvSuppDoc smRvSuppDoc) {
		this.smRvSuppDoc = smRvSuppDoc;
	}

	public SmRvDocFile getSmRvDocFile() {
		return smRvDocFile;
	}

	public void setSmRvDocFile(SmRvDocFile smRvDocFile) {
		this.smRvDocFile = smRvDocFile;
	}
	// #34031 End
}
