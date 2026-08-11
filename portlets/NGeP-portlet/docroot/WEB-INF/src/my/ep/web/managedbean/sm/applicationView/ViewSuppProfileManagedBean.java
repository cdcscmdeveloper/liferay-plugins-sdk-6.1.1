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
	//13320-Start
	private List<SelectItem> businessDivisionLs;
	private List<SelectItem> residentialDivisionLs;
	private List<SelectItem> permenantDivisionLs;
	private Long divisionId;
	private boolean divisionBox = true;
	//13320-End
	
	private Map<Long, String> tempCategoryLs; //#30766 & #34209
	
	private SmPersonnel smPersonnel;
	// #28175 start
	private String catCode;
	private List<PmRemarkCategory> historyList;
	// #28175 end
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
	private String smRvFilePath;
	private SmRvSuppDoc smRvSuppDoc;
	private SmRvDocFile smRvDocFile;
	private List<SmRvAssistOfficer> emptyRvAssistOfficers;
	// #34031 End
	
	// 26238 - START
	private List<PmFinancialOrg> financialOrgLs;
	private List<SmSupplierOwner> smSuppOwners = new ArrayList<SmSupplierOwner>();
	private SmSupplierBusiness selectedBusinessNature;
	private SmGstSupplier smGstSupplier;
	private Integer userBumiFlg;
	private Integer userMuslimFlg;
	private SmAddress smBusinessAdd;
	private SmAddress smPermenantAdd;
	private SmAddress smResAdd;
	private boolean sabahSarawakType;
	private boolean gstFlag;
	private boolean cancelMofAccount;
	private boolean editDisciplinaryActionCheckBox; //29181
	private boolean disableDeleteButtonAprrovedCategoryCodePanel; //27815
	private boolean disabledApprovedCategories; //27815
	
	//27437 - START
	private IPagingDataModel<SmSupplier> basicSupplierDataModel = new SmBasicSupplierPagingDataModel();
	private String searchByCompanyName;
	private String searchByRegNo;
	private String searchByEpNo;
	private List<SmSupplier> basicSupplierList;
	// 27437 - END
	
	//27857: Disciplinary Action - start - 26238
	private boolean cancelCategoryCodeExist;
	private boolean suspendRegistrationExist;
	private boolean cancelRegistrationExist;
	private boolean blacklistOwnerOrDirectorExist;
	private boolean bumiputeraStatusCancelExist;
	private boolean manufacturerStatusCancelExist;
	//27857: Disciplinary Action - end - 26238
	
	private boolean ownerChange;
	private boolean hasActiveDA; //#34626
	private boolean disabledCompanyNameInMaintenance;
	private boolean disabledBusinessTypeInMaintenance;
	private List<SelectItem> countryLs;
	private List<SelectItem> businessStateLs;
	private List<SelectItem> residentialStateLs;
	private List<SelectItem> permenantStateLs;
	private List<PmParameterDesc> serviceLs;
	private List<SelectItem> businessDistrictLs;
	private List<SelectItem> residentialDistrictLs;
	private List<SelectItem> permenantDistrictLs;
	private List<SelectItem> businessCityLs;
	private List<SelectItem> residentialCityLs;
	private List<SelectItem> permenantCityLs;
	private String disciplinaryActionMsg; 
	// 26238 - END
	
	public ViewSuppProfileManagedBean() {

	}

	private class SmSupplierPagingDataModel extends PagingDataModel<SmSupplier> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SmSupplierPagingDataModel() {
			super("supplierApplHistoryDtId");
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
			return SmServiceLookup.getSmApplRevisionSO()
					.retrieveSupplierBySearchCriteria(reqDTO)
					.getSupplierResultBean();
		}
	}

	/**
	 * Search supplier latest application
	 */
	public void searchSupplier() {
		validateAtLeastOneSearchCriteria();
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
	public SmSupplierBank[] getSelectedSuppBanks() {
		return selectedSuppBanks;
	}

	public void setSelectedSuppBanks(SmSupplierBank[] selectedSuppBanks) {
		this.selectedSuppBanks = selectedSuppBanks;
	}
	
	public SmPersonnel getSmPersonnel() {
		if(smPersonnel == null)
			smPersonnel = new SmPersonnel();
		return smPersonnel;
	}

	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
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
	//#30766 & #34209 - START
	public Map<Long, String> getTempCategoryLs() {
		return tempCategoryLs;
	}

	public void setTempCategoryLs(Map<Long, String> tempCategoryLs) {
		this.tempCategoryLs = tempCategoryLs;
	}
	//#30766 & #34209 - END
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

	public String getSmRvFilePath() {
		return smRvFilePath;
	}

	public void setSmRvFilePath(String smRvFilePath) {
		this.smRvFilePath = smRvFilePath;
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

	public List<SmRvAssistOfficer> getEmptyRvAssistOfficers() {
		return emptyRvAssistOfficers;
	}

	public void setEmptyRvAssistOfficers(List<SmRvAssistOfficer> emptyRvAssistOfficers) {
		this.emptyRvAssistOfficers = emptyRvAssistOfficers;
	}
	// #34031 End

	public List<PmFinancialOrg> getFinancialOrgLs() {
		return financialOrgLs;
	}

	public void setFinancialOrgLs(List<PmFinancialOrg> financialOrgLs) {
		this.financialOrgLs = financialOrgLs;
	}
	
	public List<SmSupplierOwner> getSmSuppOwners() {
		return smSuppOwners;
	}

	public void setSmSuppOwners(List<SmSupplierOwner> smSuppOwners) {
		this.smSuppOwners = smSuppOwners;
	}
	
	public SmSupplierBusiness getSelectedBusinessNature() {
		return selectedBusinessNature;
	}

	public void setSelectedBusinessNature(SmSupplierBusiness selectedBusinessNature) {
		this.selectedBusinessNature = selectedBusinessNature;
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

	public boolean isSabahSarawakType() {
		return sabahSarawakType;
	}

	public void setSabahSarawakType(boolean sabahSarawakType) {
		this.sabahSarawakType = sabahSarawakType;
	}

	public boolean isGstFlag() {
		return gstFlag;
	}

	public void setGstFlag(boolean gstFlag) {
		this.gstFlag = gstFlag;
	}

	public boolean isCancelMofAccount() {
		return cancelMofAccount;
	}

	public void setCancelMofAccount(boolean cancelMofAccount) {
		this.cancelMofAccount = cancelMofAccount;
	}

	public boolean isEditDisciplinaryActionCheckBox() {
		return editDisciplinaryActionCheckBox;
	}

	public void setEditDisciplinaryActionCheckBox(
			boolean editDisciplinaryActionCheckBox) {
		this.editDisciplinaryActionCheckBox = editDisciplinaryActionCheckBox;
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
	
	// 40078, 26238 - START
	public boolean isOwnerChange() {
		return ownerChange;
	}

	public void setOwnerChange(boolean ownerChange) {
		this.ownerChange = ownerChange;
	}
	// 40078, 26238 - END

	public boolean isDisableDeleteButtonAprrovedCategoryCodePanel() {
		return disableDeleteButtonAprrovedCategoryCodePanel;
	}

	public void setDisableDeleteButtonAprrovedCategoryCodePanel(
			boolean disableDeleteButtonAprrovedCategoryCodePanel) {
		this.disableDeleteButtonAprrovedCategoryCodePanel = disableDeleteButtonAprrovedCategoryCodePanel;
	}

	public boolean isDisabledApprovedCategories() {
		return disabledApprovedCategories;
	}

	public void setDisabledApprovedCategories(boolean disabledApprovedCategories) {
		this.disabledApprovedCategories = disabledApprovedCategories;
	}

	/**
	 * @return the cancelCategoryCodeExist
	 */
	public boolean isCancelCategoryCodeExist() {
		return cancelCategoryCodeExist;
	}

	/**
	 * @param cancelCategoryCodeExist the cancelCategoryCodeExist to set
	 */
	public void setCancelCategoryCodeExist(boolean cancelCategoryCodeExist) {
		this.cancelCategoryCodeExist = cancelCategoryCodeExist;
	}

	/**
	 * @return the suspendRegistrationExist
	 */
	public boolean isSuspendRegistrationExist() {
		return suspendRegistrationExist;
	}

	/**
	 * @param suspendRegistrationExist the suspendRegistrationExist to set
	 */
	public void setSuspendRegistrationExist(boolean suspendRegistrationExist) {
		this.suspendRegistrationExist = suspendRegistrationExist;
	}

	/**
	 * @return the cancelRegistrationExist
	 */
	public boolean isCancelRegistrationExist() {
		return cancelRegistrationExist;
	}

	/**
	 * @param cancelRegistrationExist the cancelRegistrationExist to set
	 */
	public void setCancelRegistrationExist(boolean cancelRegistrationExist) {
		this.cancelRegistrationExist = cancelRegistrationExist;
	}

	/**
	 * @return the blacklistOwnerOrDirectorExist
	 */
	public boolean isBlacklistOwnerOrDirectorExist() {
		return blacklistOwnerOrDirectorExist;
	}

	/**
	 * @param blacklistOwnerOrDirectorExist the blacklistOwnerOrDirectorExist to set
	 */
	public void setBlacklistOwnerOrDirectorExist(
			boolean blacklistOwnerOrDirectorExist) {
		this.blacklistOwnerOrDirectorExist = blacklistOwnerOrDirectorExist;
	}

	/**
	 * @return the bumiputeraStatusCancelExist
	 */
	public boolean isBumiputeraStatusCancelExist() {
		return bumiputeraStatusCancelExist;
	}

	/**
	 * @param bumiputeraStatusCancelExist the bumiputeraStatusCancelExist to set
	 */
	public void setBumiputeraStatusCancelExist(boolean bumiputeraStatusCancelExist) {
		this.bumiputeraStatusCancelExist = bumiputeraStatusCancelExist;
	}

	/**
	 * @return the manufacturerStatusCancelExist
	 */
	public boolean isManufacturerStatusCancelExist() {
		return manufacturerStatusCancelExist;
	}

	/**
	 * @param manufacturerStatusCancelExist the manufacturerStatusCancelExist to set
	 */
	public void setManufacturerStatusCancelExist(
			boolean manufacturerStatusCancelExist) {
		this.manufacturerStatusCancelExist = manufacturerStatusCancelExist;
	}

	/**
	 * @return the hasActiveDA
	 */
	public boolean isHasActiveDA() {
		return hasActiveDA;
	}

	/**
	 * @param hasActiveDA the hasActiveDA to set
	 */
	public void setHasActiveDA(boolean hasActiveDA) {
		this.hasActiveDA = hasActiveDA;
	}

	/**
	 * @return the disabledCompanyNameInMaintenance
	 */
	public boolean isDisabledCompanyNameInMaintenance() {
		return disabledCompanyNameInMaintenance;
	}

	/**
	 * @param disabledCompanyNameInMaintenance the disabledCompanyNameInMaintenance to set
	 */
	public void setDisabledCompanyNameInMaintenance(
			boolean disabledCompanyNameInMaintenance) {
		this.disabledCompanyNameInMaintenance = disabledCompanyNameInMaintenance;
	}

	/**
	 * @return the disabledBusinessTypeInMaintenance
	 */
	public boolean isDisabledBusinessTypeInMaintenance() {
		return disabledBusinessTypeInMaintenance;
	}

	/**
	 * @param disabledBusinessTypeInMaintenance the disabledBusinessTypeInMaintenance to set
	 */
	public void setDisabledBusinessTypeInMaintenance(
			boolean disabledBusinessTypeInMaintenance) {
		this.disabledBusinessTypeInMaintenance = disabledBusinessTypeInMaintenance;
	}

	/**
	 * @return the countryLs
	 */
	public List<SelectItem> getCountryLs() {
		return countryLs;
	}

	/**
	 * @param countryLs the countryLs to set
	 */
	public void setCountryLs(List<SelectItem> countryLs) {
		this.countryLs = countryLs;
	}

	/**
	 * @return the businessStateLs
	 */
	public List<SelectItem> getBusinessStateLs() {
		return businessStateLs;
	}

	/**
	 * @param businessStateLs the businessStateLs to set
	 */
	public void setBusinessStateLs(List<SelectItem> businessStateLs) {
		this.businessStateLs = businessStateLs;
	}

	/**
	 * @return the residentialStateLs
	 */
	public List<SelectItem> getResidentialStateLs() {
		return residentialStateLs;
	}

	/**
	 * @param residentialStateLs the residentialStateLs to set
	 */
	public void setResidentialStateLs(List<SelectItem> residentialStateLs) {
		this.residentialStateLs = residentialStateLs;
	}

	/**
	 * @return the permenantStateLs
	 */
	public List<SelectItem> getPermenantStateLs() {
		return permenantStateLs;
	}

	/**
	 * @param permenantStateLs the permenantStateLs to set
	 */
	public void setPermenantStateLs(List<SelectItem> permenantStateLs) {
		this.permenantStateLs = permenantStateLs;
	}

	/**
	 * @return the serviceLs
	 */
	public List<PmParameterDesc> getServiceLs() {
		return serviceLs;
	}

	/**
	 * @param serviceLs the serviceLs to set
	 */
	public void setServiceLs(List<PmParameterDesc> serviceLs) {
		this.serviceLs = serviceLs;
	}

	/**
	 * @return the businessDistrictLs
	 */
	public List<SelectItem> getBusinessDistrictLs() {
		return businessDistrictLs;
	}

	/**
	 * @param businessDistrictLs the businessDistrictLs to set
	 */
	public void setBusinessDistrictLs(List<SelectItem> businessDistrictLs) {
		this.businessDistrictLs = businessDistrictLs;
	}

	/**
	 * @return the residentialDistrictLs
	 */
	public List<SelectItem> getResidentialDistrictLs() {
		return residentialDistrictLs;
	}

	/**
	 * @param residentialDistrictLs the residentialDistrictLs to set
	 */
	public void setResidentialDistrictLs(List<SelectItem> residentialDistrictLs) {
		this.residentialDistrictLs = residentialDistrictLs;
	}

	/**
	 * @return the permenantDistrictLs
	 */
	public List<SelectItem> getPermenantDistrictLs() {
		return permenantDistrictLs;
	}

	/**
	 * @param permenantDistrictLs the permenantDistrictLs to set
	 */
	public void setPermenantDistrictLs(List<SelectItem> permenantDistrictLs) {
		this.permenantDistrictLs = permenantDistrictLs;
	}

	/**
	 * @return the businessCityLs
	 */
	public List<SelectItem> getBusinessCityLs() {
		return businessCityLs;
	}

	/**
	 * @param businessCityLs the businessCityLs to set
	 */
	public void setBusinessCityLs(List<SelectItem> businessCityLs) {
		this.businessCityLs = businessCityLs;
	}

	/**
	 * @return the residentialCityLs
	 */
	public List<SelectItem> getResidentialCityLs() {
		return residentialCityLs;
	}

	/**
	 * @param residentialCityLs the residentialCityLs to set
	 */
	public void setResidentialCityLs(List<SelectItem> residentialCityLs) {
		this.residentialCityLs = residentialCityLs;
	}

	/**
	 * @return the permenantCityLs
	 */
	public List<SelectItem> getPermenantCityLs() {
		return permenantCityLs;
	}

	/**
	 * @param permenantCityLs the permenantCityLs to set
	 */
	public void setPermenantCityLs(List<SelectItem> permenantCityLs) {
		this.permenantCityLs = permenantCityLs;
	}

	/**
	 * @return the smResAdd
	 */
	public SmAddress getSmResAdd() {
		return smResAdd;
	}

	/**
	 * @param smResAdd the smResAdd to set
	 */
	public void setSmResAdd(SmAddress smResAdd) {
		this.smResAdd = smResAdd;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

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
}
