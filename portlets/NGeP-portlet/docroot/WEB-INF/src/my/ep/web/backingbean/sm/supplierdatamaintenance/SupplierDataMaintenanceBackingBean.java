package my.ep.web.backingbean.sm.supplierdatamaintenance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.PmTrackingDairyVO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.zone.ZoneReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "supplierDataMaintenanceBackingBean")
@RequestScoped
public class SupplierDataMaintenanceBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3781591594455061750L;
	//26238 - start
	private static Map<Long, Object> identityType; 
	private boolean gmapDisabled;
	//26238 - end
	//26238 -  Disciplonary action - 28146
	private boolean searchBlacklisted = true;
	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderSupplierDataMaintenance() {
		initDataForSearchSupplier();
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"", null, null);
	}

	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderCancelMofBySupplier() {
		initDataForSearchSupplier();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/cancelRegWithMof/searchSupplierCancelMofPage.xhtml",
				"", null, null);
	}
	//26238 - start
	private static String defaultAccPurpose = "P";
	
	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderBasicAccountMaintenance() {
		FacesContext fc = FacesContext.getCurrentInstance();
		if(getManagedBean().isSupplierUser()){
			RegistrationBackingBean registrationBackingBean = new RegistrationBackingBean();
			registrationBackingBean.viewOnlineAccount();
			
//			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceDet.xhtml");
		} else {
			initDataForSearchSupplier();
			SmCommonUtil
					.redirectToPage(
							"/xhtml/sm/basicaccountmaintenance/searchBasicAccountMaintenance.xhtml",
							"", null, null);
		}
		
	} //26238 - end

	/**
	 * Navigate to Supplier Maintenance Screen
	 * 
	 * @param SmAppl
	 *            selectedSmAppl
	 */
	public void navigateToMaintenance(SmAppl selectedSmAppl, String mofStatus) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		//27437 : Disciplinary Action - start
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		if(selectedSmAppl.getApplNo() != null){
			reqDTO.setApplNo(selectedSmAppl.getApplNo());
			getManagedBean().setSmAppl(SmServiceLookup.getMofRegistrationSO().retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		} else {
			supplierSearchReqDTO.setApplId(selectedSmAppl.getApplId());
			getManagedBean().setSmAppl(SmServiceLookup.getSupplierSearchSO().retrieveSmApplWithChildByApplID(supplierSearchReqDTO).getSmAppl());
		}
		log.info("Appl ID : " + getManagedBean().getSmAppl().getApplId());
		//28623 - start
		if(getManagedBean().getSmAppl().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS){
			throw new EpException(MessageConst.ERROR_775154);
		}
		//28623 - end
		
		//27437 : Disciplinary Action - end
		initStep0BaseInfo(getManagedBean().getSmAppl());
		initStep1CompanyInfo();
		initStep6RegisterCategoryCode();
		initStep8_9PoApproverDefaultDecision();
		initBumiRecommendNote();
		initSupplierDisciplinaryAction(getManagedBean().getSmSupplier()); //27426 : Disciplinary Action
 		assignApplActiveAndApprovedCategoryList();
		//Redmine #18959 - no need to check overlapping category
		//setOverLappingCategories();
		getManagedBean().setOverLappedCategoryMsg("");
		getManagedBean().setOverLappedCategoryExists(false);
		//26238 - start
		retrieveAllDisciplinaryAction();
		getOwnerAndDirectorList(); 
		getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
		getManagedBean().setSmSupp(getManagedBean().getSmSupplier());
		
		RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
		regReqDTO.setSupplierId(getManagedBean().getSmSupp().getSupplierId());

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO regResDTO = reqSO.retrieveSuppOnlineAcc(regReqDTO);
		getManagedBean().setSmSuppOwner(new SmSupplierOwner());
		getManagedBean().setApplType(getManagedBean().getSmAppl().getApplType());
		//START 28166 - Disciplinary Action - 26238
		if(regResDTO.getSmSupp().getSmAppl() !=null && regResDTO.getSmSupp().getSmAppl().getSmAssociationBodies() != null){
			getManagedBean().getSmAppl().setSmAssociationBodies(regResDTO.getSmSupp().getSmAppl().getSmAssociationBodies());
		}
		//END 28166 - Disciplinary Action - 26238
		if(regResDTO.getSmSupp().getSmSupplierOwners()!=null && CollectionUtils.isNotEmpty(regResDTO.getSmSupp().getSmSupplierOwners())){
			getManagedBean().setSmSuppOwner(regResDTO.getSmSupp().getSmSupplierOwners().get(0));
			getManagedBean().setSmSuppOwners(regResDTO.getSmSupp().getSmSupplierOwners());
		}
		
		getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
		setDisableCompanyNameBusinessTypeInMaintenance();
		
		if(regResDTO.getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			SmPersonnel personnel = getManagedBean().getSmAppl().getSmPersonnels().get(0);
			getManagedBean().setSmPersonnel(personnel);
			getManagedBean().setUserBumiFlg(personnel.getBumiFlg());
			getManagedBean().setUserMuslimFlg(personnel.getMuslimFlg());
			getIndividualServiceType();
			getManagedBean().setSmPermenantAdd(regResDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
			populateStateByAddType("permenant", false);
			populateDistrictByAddType("permenant", false);
			populateCityByAddType("permenant", false);

			getManagedBean().setSmBusinessAdd(regResDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
			populateStateByAddType("business", false);
			populateDistrictByAddType("business", false);
			populateCityByAddType("business", false);
		}else{
			if(getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
				getManagedBean().setSabahSarawakType(true);
			}
			
			getManagedBean().setSmBusinessAdd(regResDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
			populateStateByAddType("business", false);
			populateDistrictByAddType("business", false);
			populateCityByAddType("business", false);
		}
		retrievePmCountryList();
		
		//retrieve Supplier GST Info
		initGstInfo(regResDTO.getSmSupp(),getManagedBean(),false);
		getManagedBean().setGstFlag(false);
		
		//26238 - end
		//27857: Disciplinary Action - start - 26238
		if(EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions())){
			resetDisciplinaryActionSection(); //27426 : Disciplinary Action
			resetDisciplinaryActionSectionExist();
		}else{
			for (SmDisciplinaryAction disc : getManagedBean().getSmSupplier().getSmDisciplinaryActions()) {
				if (disc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					initSupplierDisciplinaryAction(getManagedBean().getSmSupplier());
					break;
				} else if(disc.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_ACTIVE){
					resetDisciplinaryActionSection();
					resetDisciplinaryActionSectionExist();
				}
			}
		}
		//27857: Disciplinary Action - start - 26238
		//27437 : Disciplinary Action
		if(getManagedBean().getSmSupplier().getSmAppl().getApplCategoryType() != null){
			setCategoryTypeConfirm();
		}
		
		getManagedBean().setDisplaySearchPage(false);
		//28273: Disciplinary Action  - start - <26238>
		if(selectedSmAppl.getSmSupplier().getTypeDisciplinaryAction() != null){
			getManagedBean().setAllowedToDoMaintenance(false);
		}else {
			setLoginUserAllowedToDoMaintenance();
		}
		//28273: Disciplinary Action  - end - <26238>
		checkIfSupplierHasActiveTransaction(false);
		initPOApproverOverlapCategoryRemarkInfo();
		approvalHistoryRemarklist();
		initApproverRemarkInfo();
		
		//Redmine #18959
		if(getManagedBean().isHasActiveTransaction()) {
			List<Object[]> applList = SmApplicationUtil.getActiveApplList(getManagedBean().getSmSupplier());		
			getManagedBean().setActiveApplList(applList);
		}
		//27437 : Disciplinary Action - start
		if(getManagedBean().getSmAppl().getPmStatus() != null
				&& getManagedBean().getSmAppl().getPmStatus().getStatusId() != null
				&& getManagedBean().getSmAppl().getPmRegStatus() != null
				&& getManagedBean().getSmAppl().getPmRegStatus().getPmStatus() != null
				&& getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId() != null) {
			getManagedBean().setExistingStatusId(getManagedBean().getSmAppl().getPmStatus().getStatusId());
			getManagedBean().setOldRegStatusId(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId());
		}
		//27437 : Disciplinary Action - end
		initPhysicalConsultingCatcode();
		getManagedBean().setExistingRegStatus(getManagedBean().getSmAppl().getPmRegStatus());
//		getManagedBean().setOldRegStatusId(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId()); //27437 : Disciplinary Action
		getManagedBean().setSmMofAccount(getSingleSmMofAccount());
		//START 27465 : Disciplinary Action 26238
		if (getManagedBean().getSmMofAccount() != null) {
			getManagedBean().setExistingMofExpiryDate(
					getManagedBean().getSmMofAccount().getExpDate());
		}else if(getManagedBean().getSmSupplier()!= null && getManagedBean().getSmSupplier().getSmMofAccounts()!= null && getManagedBean().getSmSupplier().getSmMofAccounts().size()>0){
			getManagedBean().setSmMofAccount(getManagedBean().getSmSupplier().getSmMofAccounts().get(0));
			getManagedBean().setExistingMofExpiryDate(
					getManagedBean().getSmMofAccount().getExpDate());
		}
		//END 27465 : Disciplinary Action 26238
		if (getManagedBean().getSmAppl().getPmRegStatus() == null) {
			getManagedBean().getSmAppl().setPmRegStatus(new PmRegStatus());
		}
		
		//28146
		if(searchBlacklisted){
			//28494
			getManagedBean().setSelectedOwnerAndDirectorList(new ArrayList<SmPersonnel>());
		}
		getManagedBean().setDeleteApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedPendingApprovedCategories(new ArrayList<SmSupplierCategory>());
	
		this.getManagedBean().setApproverRemark(null);
		//26238 -  Disciplonary action - 28146
		getManagedBean().setFlagBlacklistCancel(false);
		
		//#28278 - Disciplonary action - #28278 - Start
		if(StringUtils.equals(mofStatus, "D")){
			getManagedBean().setFlagShowButtonSubmit(false);
		}
		else {
			getManagedBean().setFlagShowButtonSubmit(true);
		}
		//#28278 - Disciplonary action - #28278 - end
		// 28912 start - filter Registration Status List for bumi only
		if (getManagedBean().isBumiputeraDataMaintainance()) {
			getManagedBean().setRegStatusDescList(
					SmApplicationUtil.getInstance().retrieveRegistrationStatusList(null,
							ParameterConst.SM_BUMI_SUPP_DATA_MAINTAINACE));
		}
		// 28912 end
		
	}

	/**
	 * Navigate to Cancel MOF By Supplier
	 * 
	 * @param SmAppl
	 *            selectedSmAppl
	 */
	public void navigateToCancelMof(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		getManagedBean().setSmSupplier(
				getManagedBean().getSmAppl().getSmSupplier());
		getManagedBean().setBasicInfo(
				SmApplicationUtil.getSmSupplierBasicInfoVO(getManagedBean()
						.getBasicInfo(), getManagedBean().getSmSupplier(),
						getManagedBean().getSmAppl()));
		setLoginUserAllowedToDoMaintenance();
		checkIfSupplierHasActiveTransaction(true);

		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/cancelRegWithMof/cancelMofRegistrationBySupplierPage.xhtml",
						"", null, null);
	}

	public void search() {
		// 28912 start - set supplier type to contractor
		if(getManagedBean().isBumiputeraDataMaintainance()){
			getManagedBean().setSearchSuppType(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
		}
		// 28912 end
		getManagedBean().searchSupplier();
	}
	//26238 : Disciplinary action - start
	public void submitBasicAccountMaintenance(){
		validateDisciplinaryAction();
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();
		List<SmTrackingDairyVO> listSmTrackingDiary = new ArrayList<SmTrackingDairyVO>();
		
		try {
			reqDTO.setSupplier(getManagedBean().getSmSupplier());
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			
			if(getManagedBean().getSmAppl().getSmSupplierBanks().size() > 0){
				reqDTO.setAdditionalBankList(getManagedBean().getSmAppl().getSmSupplierBanks());
			} else {
				reqDTO.setAdditionalBankList(null);
			}
			
			if(getManagedBean().getOwnerAndDirectorList().size() > 0){
				reqDTO.setSelectedOwnerAndDirector(getManagedBean().getOwnerAndDirectorList());
			}
			
			if(getManagedBean().isEditDisciplinaryAction()){
				reqDTO.setSmDisciplinaryAction(getManagedBean()
						.getDisciplineVO().getSmDiscplinaryAction());
			}
			
			if(getManagedBean().isWarning()) {
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setWarning(true);			
			}
			
			if(getManagedBean().isSuspendRegistration()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setSuspendRegistration(true);
				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}	
			}
			
			if(getManagedBean().isCancelRegistration()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setCancelRegistration(true);
				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}
			}
			
			if(getManagedBean().isBlacklistOwnerOrDirector()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setBlacklistOwner(true);
				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}
			}
			
			//set tracking diary for Disciplinary Action
			if (getManagedBean().isEditDisciplinaryAction()) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"DA"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}			
			
			reqDTO.setSmTrackingDiaryVOList(listSmTrackingDiary);
			
			//27906: Disciplinary Action - start - 
			// set portalPath
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			//27906: Disciplinary Action - end - 
			
			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateBasicAccountInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		}
		reqDTO = null;
		getManagedBean().setDisplaySearchPage(true);
		//27437 : Disciplinary Action
		FacesContext fc = FacesContext.getCurrentInstance();
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
				.getMessage(MessageConst.INFO_721215,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale()), null);
	}
	//26238 - end - updateBasicAccountInfo
	
	//27437 : Disciplinary action - start
	public void searchBasicAccount() {
		getManagedBean().setBasicAccountMaintenance(true);
		getManagedBean().searchBasicSupplier();
	}
	
	public void resetSearch(){
		getManagedBean().getSearchSupplier().setCompanyName(null);
		getManagedBean().getSearchSupplier().setRegNo(null);
		getManagedBean().getSearchSupplier().setEpNo(null);
	}
	//27437 : Disciplinary action - end
	/**
	 * Submit supplier maintenance
	 */
	public void submitSupplierDataMaintenance() {
		
		PmUser user = new FacesScopes().getUserSessionBean();
		validateDisciplinaryAction();
		validateAddedCategory(getManagedBean().getActiveCategoryList());
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();
		List<SmTrackingDairyVO> listSmTrackingDiary = new ArrayList<SmTrackingDairyVO>();
		//reset value
		getManagedBean().setHasRegStatusChanged(false);
		getManagedBean().setHasMofExpiryDateChanged(false);
		
		// Check if got active application for this supplier
		SmAppl activeAppl = SmCommonUtil
				.getActiveApplBySupplier(getManagedBean().getSmSupplier());
		boolean isOverlapCategoryRemarkChanged = false;

		try {

			// set registration status and expiry date
			reqDTO.setOldRegStatus(getManagedBean().getExistingRegStatus());
			reqDTO.setOldRegStatusId(getManagedBean().getOldRegStatusId());
			reqDTO.setOldMofExpiryDate(getManagedBean().getExistingMofExpiryDate());
			
			// set active status for newly added category
			setActiveStatusForNewAddedCategories();
			reqDTO.setListRegCategories(getManagedBean().getSmAppl().getSmSupplierCategories());
			reqDTO.setActiveAppl(activeAppl);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setNewlyAddedCategories(getManagedBean().getActiveCategoryList());
			// 28912 start - set selected to deleted
			if (getManagedBean().isBumiputeraDataMaintainance()) {
				reqDTO.setDeletedCategories(getManagedBean().getSelectedApprovedCategories());
			} else {
				reqDTO.setDeletedCategories(getManagedBean().getDeleteApprovedCategories());
			}
			// 28912 end
			reqDTO.setSmMofAccount(getManagedBean().getSmMofAccount());
			reqDTO.setRemark(getManagedBean().getApplnApprovalRemarks());
			// 28912 start - set remark doc type to bumiputera
			if (getManagedBean().isBumiputeraDataMaintainance()) {
				reqDTO.setDocType(ParameterConst.SM_BUMI_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			} else {
				reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			}
			// 28912 end
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			reqDTO.setOverlapCategoryRemarkDocType(ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER);
			reqDTO.setRemarkId(getManagedBean().getRemarkId());
			reqDTO.setRemarkOverlappedId(getManagedBean().getRemarkOverlappedId());	
			//26238 - start
			if(getManagedBean().getSelectedApprovedCategories() != null 
					|| getManagedBean().getSelectedApprovedCategories().size() > 0){
				//27446 : Disciplinary Action - start
				List<SmSupplierCategory> selectedCategory = new ArrayList<SmSupplierCategory>();
				boolean basicCategoryExist = false;
				for(SmSupplierCategory category : getManagedBean().getSelectedApprovedCategories()){
					selectedCategory.add(category);
					//check for basic category code 
					//used to auto cancel MTO code
					if(category.getPmCategoryL1().getCategoryL1Code().equalsIgnoreCase("22")
							&& category.getPmCategoryL2().getCategoryL2Code().equalsIgnoreCase("17")
							&& (category.getPmCategoryL3().getCategoryL3Code().equalsIgnoreCase("04")
									|| category.getPmCategoryL3().getCategoryL3Code().equalsIgnoreCase("05")
									|| category.getPmCategoryL3().getCategoryL3Code().equalsIgnoreCase("06"))){
						basicCategoryExist = true;
					}
				}
				
				if(basicCategoryExist){						
					if(getManagedBean().getApprovedCategoryList() != null){
						for(SmSupplierCategory regCat : getManagedBean().getApprovedCategoryList()){
							if(regCat.getPmCategoryL1() != null
									&& regCat.getPmCategoryL1().getCategoryL1Code()
									.equalsIgnoreCase("22")){
								if(regCat.getPmCategoryL2() != null
										&& regCat.getPmCategoryL2().getCategoryL2Code()
										.equalsIgnoreCase("17")){
									if(regCat.getPmCategoryL3() != null){
										if(regCat.getPmCategoryL3().getCategoryL3Code() != null
												&& regCat.getPmCategoryL3().getCategoryL3Code()
												.equalsIgnoreCase("09")){
											log.info("MTO Code exist");
											selectedCategory.add(regCat);
										}
									}
								}
							}
						}
					}
				}
				reqDTO.setSelectedCategories(selectedCategory);
				//27446 : Disciplinary Action - end
			} 
//			28147: Disciplinary Action - start -
			if (getManagedBean().getOwnerAndDirectorList() != null
					&& getManagedBean().getOwnerAndDirectorList().size() > 0) {
				if (getManagedBean().isBlacklistOwnerOrDirector() && !getManagedBean().getSelectedOwnerAndDirectorList().isEmpty()) {
					reqDTO.setSelectedOwnerAndDirector(getManagedBean().getSelectedOwnerAndDirectorList());
				} else {
					reqDTO.setSelectedOwnerAndDirector(getManagedBean().getOwnerAndDirectorList());
				}
			}
//			28147: Disciplinary Action - end -
			
			if(getManagedBean().isWarning()) {
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setWarning(true);			
			}
			
			if(getManagedBean().isCancelCategoryCode()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setCancelCategory(true);
				//27460 : Disciplinary Action
				if(getManagedBean().getSelectedApprovedCategories() == null
						|| getManagedBean().getSelectedApprovedCategories().size() <= 0){
					throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.category",null));
				}
			}
			
			if(getManagedBean().isSuspendRegistration()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setSuspendRegistration(true);
				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}					
			}
			
			if(getManagedBean().isCancelRegistration()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setCancelRegistration(true);

				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}
			}
			
			if(getManagedBean().isBlacklistOwnerOrDirector()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setBlacklistOwner(true);

				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						SupplierManagementConst.DELETED_BY_DISCIPLINARY_ACTION);
				if(smTrackingDairyVO != null){
					reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);
				}
			}
			
			if(getManagedBean().isBumiputeraStatusCancel()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setBumiputeraCancel(true);
			}
			
			if(getManagedBean().isManufacturerStatusCancel()){
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setManufacturerCancel(true);
			}
			//26238 - end
			
			if (getManagedBean().getApproverRemark() != null) {
				reqDTO.setApproverRemark(getManagedBean().getApproverRemark());
				log.info("User ID="+user.getUserId());
				reqDTO.setPmUser(user);
			}

			if (getManagedBean().isEditDisciplinaryAction()) {
				reqDTO.setSmDisciplinaryAction(getManagedBean()
						.getDisciplineVO().getSmDiscplinaryAction());
			}
			
			//#26340-start
			//to check if Category Description Changed
			boolean isCategoryDescriptionHasChanged =false;
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null) {
				for (SmSupplierCategory cat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
					if (cat.getOtherDesc() != null) {
						isCategoryDescriptionHasChanged = true;
					}
				}
			}
			reqDTO.setUpdatedOtherDescChanged(isCategoryDescriptionHasChanged);
			//#26340-end
			
			String oriPoApprOverlappedRemark = getManagedBean().getOriPoApprOverlappedRemark();
			reqDTO.setCurrentOverlapCategoryRemark(oriPoApprOverlappedRemark);
			String poApprOverlappedRemark = getManagedBean().getPoApprOverlappedRemark();
			//Redmine #16927
			//to check if overlap remark changed 
			if(oriPoApprOverlappedRemark != null) {
				if(poApprOverlappedRemark != null && !poApprOverlappedRemark.trim().equals(oriPoApprOverlappedRemark.trim())) {
					isOverlapCategoryRemarkChanged = true;
				reqDTO.setUpdatedOverlapCategoryRemark(poApprOverlappedRemark);
				}
			} else {
				if(poApprOverlappedRemark != null && poApprOverlappedRemark.trim().length() > 0) {
					isOverlapCategoryRemarkChanged = true;
					reqDTO.setNewlyFilledInOverlapCategoryRemark(poApprOverlappedRemark);
				}
			}
			reqDTO.setOverlapCategoryRemarkChanged(isOverlapCategoryRemarkChanged);
			
			//Redmine #18959
			//to check if regStatus changed 
			if(getManagedBean().getExistingRegStatus().getRegStatusId().compareTo(getPmRegStatusByStatusId(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId()).getRegStatusId()) != 0) {
				getManagedBean().setHasRegStatusChanged(true);				
			}
			//to check if MOF expiry date changed
			if(getManagedBean().getExistingMofExpiryDate().compareTo(getManagedBean().getSmMofAccount().getExpDate()) != 0) {
				getManagedBean().setHasMofExpiryDateChanged(true);				
			}
			
			//set tracking diary for Registering Status
			if(getManagedBean().isHasRegStatusChanged()) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"RS"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			//set tracking diary for MOF Expiry Date
			if(getManagedBean().isHasMofExpiryDateChanged()) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"ED"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			//set tracking diary for Added Category
			if(getManagedBean().getActiveCategoryList().size() > 0) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"AC"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			//set tracking diary for Deleted Category
			if(getManagedBean().getDeleteApprovedCategories().size() > 0) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"DC"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			//set tracking diary for Disciplinary Action
			if (getManagedBean().isEditDisciplinaryAction()) {				
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"DA"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			
			//set overlap remark category remark
			if (isOverlapCategoryRemarkChanged) {		
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"OLA"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			reqDTO.setSmTrackingDiaryVOList(listSmTrackingDiary);
			
			// 28912 start - set BumiputeraDataMaintainance for tracking
			if(getManagedBean().isBumiputeraDataMaintainance()){
				reqDTO.setBumiputeraDataMaintainance(getManagedBean().isBumiputeraDataMaintainance());
			}
			// 28912 end
			
			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateSupplierInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());
			
			//26238 - start
			if(resDTO.isSuccessful()){
				if(resDTO.getCategoryList() != null){
					SupplierDataMaintenanceReqDTO supplierDataMaintenanceReqDTO = new SupplierDataMaintenanceReqDTO();
					SmAppl latestAppl = resDTO.getSmAppl();
					SmSupplier supplier = latestAppl.getSmSupplier();
					List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
					
					if(discList != null){
						for(SmDisciplinaryAction disc : discList){
							if(disc.getDisciplinaryActionId() != null 
									&& disc.isRecordStatus()){
								supplierDataMaintenanceReqDTO.setDisciplinaryActionId(disc.getDisciplinaryActionId());
								break;
							}
						}
					}
					supplierDataMaintenanceReqDTO.setSelectedCategories(resDTO.getCategoryList());
					SupplierDataMaintenanceResDTO resDTO2 = new SupplierDataMaintenanceResDTO();
					resDTO2 = SmServiceLookup.getSupplierDataMaintenanceSO()
							.updateSelectedCategory(supplierDataMaintenanceReqDTO);
					
					if(!resDTO2.isSuccessful()){
						throw new EpException("Failed to update Supplier Category imposed Disciplinary Action");
					}
				}
			} 
			//26238 - end
			
			//26238 -  Disciplonary action - 28146 - start
			if (getManagedBean().getOwnerAndDirectorList() != null && getManagedBean().getOwnerAndDirectorList().size() > 0 
					&& getManagedBean().isBlacklistOwnerOrDirector() && reqDTO.getSelectedOwnerAndDirector() != null){
				
				reqDTO.setSelectedOwnerAndDirector(getManagedBean().getSelectedOwnerAndDirectorList());
				
				if(searchBlacklisted){
					
					Long smApplId =  getManagedBean().getSmAppl().getApplId();
					SmDisciplinaryAction smDisciplinaryAction =  cloneSmDisciplinaryActionEditableValue(
							new SmDisciplinaryAction(), getManagedBean().getDisciplineVO().getSmDiscplinaryAction());
					
					for(SmPersonnel personnel : reqDTO.getSelectedOwnerAndDirector()){
						MofRegistrationReqDTO reqDTO2 = new MofRegistrationReqDTO();
						MofRegistrationResDTO resDTO2 = new MofRegistrationResDTO();
						reqDTO2.setPersonnel(personnel);
						resDTO2 =  SmServiceLookup.getMofRegistrationSO().retrieveSmApplWithChildByPersonnel(reqDTO2);
						if(resDTO2.getApplList() != null && resDTO2.getApplList().size() > 1){
							
							for(SmAppl smAppl : resDTO2.getApplList()){
								if(!smAppl.getApplId().equals(smApplId)){
									searchBlacklisted = false;
									navigateToMaintenance(smAppl, StringUtils.EMPTY);							if(getManagedBean().getOwnerAndDirectorList().size() > 1){
										for (Iterator<SmPersonnel> iter = getManagedBean().getOwnerAndDirectorList().listIterator(); iter.hasNext(); ) {
											SmPersonnel person = iter.next();
										    if (!person.getIdentificationNo().equals(personnel.getIdentificationNo())) {
										        iter.remove();
										    }
										}
									}
									getManagedBean().getSelectedOwnerAndDirectorList().clear();
									getManagedBean().getSelectedOwnerAndDirectorList().addAll(getManagedBean().getOwnerAndDirectorList());
									getManagedBean().setBlacklistOwnerOrDirector(true);
									getManagedBean().setEditDisciplinaryAction(true);
									onTickPunishmentType();
									if (EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions())){
										cloneSmDisciplinaryActionEditableValue(getManagedBean().getDisciplineVO().getSmDiscplinaryAction(), smDisciplinaryAction);
									}
									
									submitSupplierDataMaintenance();
								}
							}
						}
					}
				}
				
			}
			//26238 -  Disciplonary action - 28146 - end
			
			//Untick after save
			getManagedBean().setEditDisciplinaryAction(false);

		} catch (OptimisticLockException opt) {
			throw new EpException(opt.getMessage());
		} catch (PersistenceException pers) {
			throw new EpException(pers.getMessage());
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		} catch (RuntimeException re) {
			throw new EpException(re.getMessage());
				}
		getManagedBean().setWarningExist(false);
		reqDTO = null;
		getManagedBean().setDisplaySearchPage(true);
		FacesContext fc = FacesContext.getCurrentInstance();
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
				.getMessage(MessageConst.INFO_721215,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale()), null);
	}

	/**
	 * Submit Cancel MOF Account
	 */
	public void submitToCancelMof() {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
		reqDTO.setSmAppl(getManagedBean().getSmAppl());
		reqDTO.setRemark(getManagedBean().getApplnApprovalRemarks());
		reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
		SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
				getManagedBean().getSmAppl(),
				SupplierManagementConst.APPROVER_SUBMIT_CANCEL_SUPPLIER_MOF_ACCOUNT);
		reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);

		try {
			SmServiceLookup.getSupplierDataMaintenanceSO()
					.cancelMofRegistrationBySupplier(reqDTO);
		} catch (OptimisticLockException opt) {
			throw new EpException(opt.getMessage());
		} catch (PersistenceException pers) {
			throw new EpException(pers.getMessage());
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		} finally {
			reqDTO = null;
		}

		String[] params = { "" };
		params[0] = getManagedBean().getSmAppl().getSmSupplier()
				.getCompanyName();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, SmCommonUtil
						.getMessage("lbl.sm.cancelmof.bysupp.success.msg",
								params)));
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						SmApplicationUtil.getApplicationErrorPage());
	}

	/**
	 * Back to search screen
	 */
	public void backToSearchSupplierMaintenance() {
		if(getManagedBean().isBumiputeraDataMaintainance()){
			getManagedBean().setRegStatusDescList(
					SmApplicationUtil.getInstance().retrieveRegistrationStatusList(null,
							ParameterConst.NEW_SM_APPLN_FOR_BUMI));
		}
		getManagedBean().setDisplaySearchPage(true);
	}

	/**
	 * Back to Cancel MOF Search Page
	 */
	public void backToSearchCancelMof() {
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/cancelRegWithMof/searchSupplierCancelMofPage.xhtml",
				"", null, null);
	}

	/**
	 * Ajax call to set the application category selected.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void applicationCategorySelected(AjaxBehaviorEvent e) {

		if (getManagedBean().getSmAppl().getSupplierType().equals("J")) {
			// added to custom confirm dialog title and msg
			// based on category type Physical or Non Physical
			setRegCatCodeConsConfirmDialog();
		} else {
			// added to custom confirm dialog title and msg
			// based on category type Special or Normal
			setRegCatCodeContConfirmDialog();
		}
	}

	/**
	 * Reset application category type based on the selection to Normal or
	 * Special.
	 */
	public void resetApplnCategoryType() {
		if (getManagedBean().getSmAppl().getApplCategoryType() != null
				&& getManagedBean().getSmAppl().getApplCategoryType()
						.equals("S")) {
			getManagedBean().getSmAppl().setApplCategoryType("N");
		} else {
			getManagedBean().getSmAppl().setApplCategoryType("S");
		}
		if (getManagedBean().getSmAppl().getSupportingDocMode() == null
				|| getManagedBean().getSmAppl().getSupportingDocMode()
						.equals("")) {
			getManagedBean().getSmAppl().setSupportingDocMode(
					SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		}
		RequestContext.getCurrentInstance().execute("typeConfirmation.hide()");
	}

	/**
	 * Assign application category type based on selection on screen.
	 */
	public void assignApplnCategoryType() {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		// Remove from active list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getActiveCategoryList().removeAll(catList);
		}
		// Remove from main list
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
		}

		assignApplnConsultantCategoryType();

		StringBuilder sb = new StringBuilder();
		String desc = null;
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.normal",
					null);
		} else {
			desc = SmCommonUtil.getMessage("lbl.sm.categorycode.note.special",
					null);
		}
		sb.append(desc);
		getManagedBean().setCatTypeConfirm(sb.toString());
	}

	private void assignApplnConsultantCategoryType() {
		List<SmSupplierCategory> toRemoveCategoryList = SmApplicationUtil
				.getToRemoveConsultantCategoriesByChangeCategorType(
						getManagedBean().getSmAppl(), getManagedBean()
								.getActiveCategoryList(), getManagedBean()
								.getPhysicalConsulting());
		getManagedBean().getActiveCategoryList()
				.removeAll(toRemoveCategoryList);
	}

	/**
	 * Removed selected approved categories from the list.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void unSelectApporvedCategories(AjaxBehaviorEvent e) {
		if (getManagedBean().getSelectedPendingApprovedCategories() != null) {
			Iterator<SmSupplierCategory> iterator = getManagedBean()
					.getSelectedPendingApprovedCategories().iterator();
			while (iterator.hasNext()) {
				SmSupplierCategory cat = iterator.next();
				if (!getManagedBean().isAllowedToDoMaintenance()) {
					iterator.remove();
				} else if ((cat.getRecordStatusFlg() != null && cat
						.getRecordStatusFlg().compareTo(
								SupplierManagementConst.APPROVED_RECORD_STATUS) == 0)
						|| (cat.getApprovedDate() != null)) {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Add new category selected to main list
	 * 
	 * @param PmCategoryL3
	 *            pmCategoryL3
	 */
	public void addNewCategoryAction(PmCategoryL3 pmCategoryL3) {
		SmSupplierCategory categ = new SmSupplierCategory();
		categ.setRevNo(1);
		categ.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
		if (getManagedBean().getSmAppl().getApplCategoryType()
				.equalsIgnoreCase("S")) {
			categ.setSpecialCategory(true);
		}
		categ.setSmAppl(getManagedBean().getSmAppl());
		categ.setSupplierType(getManagedBean().getSmAppl().getSupplierType());
		getManagedBean().setSelectedCategory(categ);

		categ.setPmCategoryL3(pmCategoryL3);
		categ.setPmCategoryL2(pmCategoryL3.getPmCategoryL2());
		categ.setPmCategoryL1(pmCategoryL3.getPmCategoryL2().getPmCategoryL1());
		categ.setApplRefId(getManagedBean().getSmAppl().getApplId());
		getManagedBean().setSelectedCategory(categ);
		saveSelectedCategory();
	}

	/**
	 * Assign category re approval on other description change
	 * 
	 * @param SmSupplierCategory
	 *            cat
	 */
	public void assignCategoryReApprovalOnOtherDescChange(SmSupplierCategory cat) {
		if (StringUtils.isNotEmpty(cat.getOtherDesc())
				&& cat.getApprovedDate() != null) {
			if (!cat.getOtherDesc().trim()
					.equalsIgnoreCase(cat.getPrevOtherDesc().trim())) {
				cat.setRecordStatusFlg(SupplierManagementConst.PENDING_ACT_STATUS);
				cat.setPoDecFlg(null);
				cat.setApprvrDecFlg(null);
			} else {
				cat.setRecordStatusFlg(SupplierManagementConst.APPROVED_RECORD_STATUS);
				cat.setPoDecFlg(cat.getPrevPoDecFlg());
				cat.setApprvrDecFlg(cat.getPrevApprDecFlg());
			}

		}
	}

	/**
	 * Show confirmation dialog
	 */
	public void showConfirmDialog() {
		//26238: Disciplinary Action - start - 28342
		if (getManagedBean().isWarning() || getManagedBean().isCancelCategoryCode()
				|| getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration()
				|| getManagedBean().isBlacklistOwnerOrDirector() || getManagedBean().isBumiputeraStatusCancel()
				&& getManagedBean().isManufacturerStatusCancel()){ 
			log.info("---showConfirmDialog semua DA---");
		} else if (!getManagedBean().isWarning() && !getManagedBean().isCancelCategoryCode()
				&& !getManagedBean().isSuspendRegistration() && !getManagedBean().isCancelRegistration()
				&& !getManagedBean().isBlacklistOwnerOrDirector() && !getManagedBean().isBumiputeraStatusCancel()
				&& getManagedBean().isManufacturerStatusCancel()) {
			//27919 - start
				if(getManagedBean().getSelectedApprovedCategories() == null
						|| getManagedBean().getSelectedApprovedCategories().size() <= 0){
					boolean manufactureCodeExist = false;
					for(SmSupplierCategory cat : getManagedBean().getSelectedApprovedCategories()){
						if(cat.getPmCategoryL3().getCategoryL3Code().equalsIgnoreCase("99")){
							manufactureCodeExist = true;
						}
					}
					
					if(!manufactureCodeExist){
						throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.category",null));
					}	
				}else{
					boolean checkOtherCodeExist = false;
					for(SmSupplierCategory cat : getManagedBean().getSelectedApprovedCategories()){
						if(cat.getPmCategoryL3().getCategoryL3Code().equalsIgnoreCase("99")){ 
						}else {
							checkOtherCodeExist = true;
						}
					}
					 if (checkOtherCodeExist){
							throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.manufacturer.category",null));
					}
				} 
				//27919 - end	
		} 
		//26238: Disciplinary Action - end - 28342
		//27460 : Disciplinary Action
		if(getManagedBean().isCancelCategoryCode()){			
			if(getManagedBean().getSelectedApprovedCategories() == null
					|| getManagedBean().getSelectedApprovedCategories().size() <= 0){
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.category",null));
			}
		}
		//#28147 - start - #28494
		if (getManagedBean().isBlacklistOwnerOrDirector()
				&& (getManagedBean().getSelectedOwnerAndDirectorList() == null
				|| getManagedBean().getSelectedOwnerAndDirectorList().isEmpty())) {
			throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.owner", null));
		}
		//#28147 - end
 	
		getManagedBean().setShowParentMsg(false);
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
	}

	/**
	 * Hide confirmation dialog
	 */
	public void hideConfirmDialog() {
		getManagedBean().setShowParentMsg(true);
		RequestContext.getCurrentInstance().execute("confirmDialog.hide()");
	}

	/**
	 * Save selected category information into SmSupplierCategory list of the
	 * application.
	 */
	private void saveSelectedCategory() {
		boolean validCat = true;
		if (getManagedBean().getSelectedCategory().getPmCategoryL1() != null
				&& getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryL1Id() != null) {
			if (getManagedBean().getSelectedCategory().getPmCategoryL1()
					.getCategoryL1Code() != null
					&& getManagedBean()
							.getSelectedCategory()
							.getPmCategoryL1()
							.getCategoryL1Code()
							.equalsIgnoreCase(
									SupplierManagementConst.ICT_CATEGORY_L1_CODE)) {
				validCat = SmApplicationUtil.validateIctCatCode(
						getManagedBean().getBusinessTypeVal(), getManagedBean()
								.getSmAppl(), getManagedBean()
								.getSelectedCategory());
			}
		}
		boolean overLapCategory = false;
		overLapCategory = SmApplicationUtil.validL3CategoryCodeForOverLapping(
				getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id(), getManagedBean().getSmSupplier()
						.getSupplierId(), getManagedBean().getSmAppl()
						.getApplId());
		getManagedBean().getSelectedCategory().setOverLapCategory(
				overLapCategory);
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			//Redmine #18959
			if(cat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE) {
				if (cat.getPmCategoryL1()
						.getCategoryL1Id()
						.equals(getManagedBean().getSelectedCategory()
								.getPmCategoryL1().getCategoryL1Id())
						&& cat.getPmCategoryL2()
								.getCategoryL2Id()
								.equals(getManagedBean().getSelectedCategory()
										.getPmCategoryL2().getCategoryL2Id())
						&& cat.getPmCategoryL3()
								.getCategoryL3Id()
								.equals(getManagedBean().getSelectedCategory()
										.getPmCategoryL3().getCategoryL3Id())) {
					validCat = false;
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}

		if (validCat) {
			getManagedBean().getSmAppl().getSmSupplierCategories()
					.add(getManagedBean().getSelectedCategory());

			assignApplActiveAndApprovedCategoryList();
			setOverLappingCategories();

		}
	}

	/**
	 * Assign tracking diary values to VO
	 * 
	 * @param SmAppl
	 *            appl
	 * @param String
	 *            actionDesc
	 * @return SmTrackingDairyVO
	 */
	private SmTrackingDairyVO assignTrackingDairyValues(SmAppl appl,
			String actionDesc) {
		SmTrackingDairyVO trckVO = new SmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		
		//start-#26418 SM URGENT SHOWSTOPPER : Registered Category Created By Issue
		List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user
				.getUserId());
		String ePRole = StringUtils.EMPTY;
		if (!EntityHelper.isCollectionEmpty(userRoles)) { 
			for (PmUserRole userRole : userRoles) {
				if (userRole.getPmRole().getRoleCode()
						.equals(RoleCode.UPS_APPROVER.value())
						|| userRole.getPmRole().getRoleCode()
								.equals(RoleCode.UPS_PO.value())) {
					ePRole = userRole.getPmRole().getRoleCode();
					break;
				}
			}
		}
		
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null && user.getUserId()!= null ? user.getUserId() : 1L);
		//end
		trckVO.setActionedByRole(ePRole);
		trckVO.setDocId(appl.getApplId());
		//27437 : Disciplinary Action - start
		if(appl.getApplNo() != null){
			trckVO.setDocNo(appl.getApplNo());
		} else {
			trckVO.setDocNo(appl.getSmSupplier().getRegNo());
		}
		
		trckVO.setDocType("SR");
		trckVO.setGroupId(null);
		trckVO.setStatus(appl.getPmStatus());
		trckVO.setGroupDocType(null);
		return trckVO;

	}

	private String getActionDesc(SmAppl appl) {
		PmUser user = new FacesScopes().getUserSessionBean();
		StringBuffer sb = new StringBuffer();
		sb.append("Keterangan Lain untuk bidang:<br />");
		boolean editOtherDesc = false;
		if (!EntityHelper.isCollectionEmpty(appl.getSmSupplierCategories())) {
			for (SmSupplierCategory sc : appl.getSmSupplierCategories()) {
				if (!StringUtils.isBlank(sc.getOtherDesc())
						&& !sc.getOtherDesc().equals(sc.getEntryMode())) {
					editOtherDesc = true;
					sb.append(sc.getPmCategoryL1().getCategoryL1Code()
							+ sc.getPmCategoryL2().getCategoryL2Code()
							+ sc.getPmCategoryL3().getCategoryL3Code());
					sb.append(" telah dikemaskini dari ")
							.append(sc.getEntryMode()).append(" kepada ")
							.append(sc.getOtherDesc()).append(" oleh ")
							.append(user.getUserName()).append("<br />");
				}
			}
		}
		if (!editOtherDesc) {
			sb = new StringBuffer();
			sb.append(SupplierManagementConst.APPROVER_SUBMIT_SUPPLIER_DATA_MAINTENANCE);
		}
		return sb.toString();
	}

	private void setCategoryTypeConfirm() {
		if (getManagedBean().getSmAppl().getApplCategoryType()
				.equalsIgnoreCase("N")) {
			getManagedBean().setCatTypeConfirm(
					SmCommonUtil.getMessage("lbl.sm.categorycode.note.normal",
							null));
		} else {
			getManagedBean().setCatTypeConfirm(
					SmCommonUtil.getMessage("lbl.sm.categorycode.note.special",
							null));
		}
	}

	/**
	 * Assign active category list with status Active or Pending approval
	 */
	private void assignApplActiveAndApprovedCategoryList() {
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		List<SmSupplierCategory> apprvList = new ArrayList<SmSupplierCategory>();
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0
					|| cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
				catList.add(cat);
			} else if (cat.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				cat.setEntryMode(cat.getOtherDesc());
				//26238: Disciplinary Action - start - 28219
				apprvList.add(cat);
				//26238: Disciplinary Action - End- 28219
				//26238 - start
				//if(!cat.isCancelCategory()){
				//	apprvList.add(cat);
				//}
				//26238 - end
			}
		}
		if (getManagedBean().getActiveCategoryList() != null
				&& !getManagedBean().getActiveCategoryList().isEmpty()) {
			getManagedBean().getActiveCategoryList().clear();
		}
		if (getManagedBean().getApprovedCategoryList() != null
				&& !getManagedBean().getApprovedCategoryList().isEmpty()) {
			getManagedBean().getApprovedCategoryList().clear();
		}
		SmApplicationUtil.sortSupplierCategory(apprvList);
		getManagedBean().setApprovedCategoryList(apprvList);
		getManagedBean().setActiveCategoryList(catList);
	}

	/**
	 * Set overlapped categories values.Checks if same owner exist in different
	 * supplier and has same one or more similar categories
	 */
	private void setOverLappingCategories() {
		getManagedBean().setOverLappedCategoryMsg("");
		getManagedBean().setOverLappedCategoryExists(false);
		int i = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(SmCommonUtil.getMessage("lbl.sm.category.overlappingMsg",
				null));
		sb.append("<table>");
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.isOverLapCategory()
					&& (cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.APPROVED_RECORD_STATUS) == 0 || cat
							.getRecordStatusFlg().compareTo(
									SupplierManagementConst.PENDING_ACT_STATUS) == 0)) {
				getManagedBean().setOverLappedCategoryExists(true);
				sb.append("<tr><td>").append(++i).append(".) ")
						.append(cat.getPmCategoryL1().getCategoryL1Code())
						.append(cat.getPmCategoryL2().getCategoryL2Code())
						.append(cat.getPmCategoryL3().getCategoryL3Code())
						.append("</tr></td>");
			}
		}
		sb.append("</table>");
		if (getManagedBean().isOverLappedCategoryExists()) {
			getManagedBean().setOverLappedCategoryMsg(sb.toString());
		}
	}

	/**
	 * Sets the registered category code confirm dialog text for Contractor.
	 * 
	 */
	private void setRegCatCodeContConfirmDialog() {
		getManagedBean().setRegCatCodeConfirmHeader(
				SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.header.normal", null));
		getManagedBean().setRegCatCodeConfirmMsg(
				SmCommonUtil.getMessage("lbl.sm.regCatCode.confirm.msg.normal",
						null));
		if (getManagedBean().getSmAppl() != null) {
			if (StringUtils.isNotBlank(getManagedBean().getSmAppl()
					.getApplCategoryType())
					&& getManagedBean().getSmAppl().getApplCategoryType()
							.equals("S")) {
				getManagedBean().setRegCatCodeConfirmHeader(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.header.special",
								null));
				getManagedBean().setRegCatCodeConfirmMsg(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.msg.special", null));
			}
		}
	}

	/**
	 * Sets the registered category code confirm dialog text for Consultant.
	 * 
	 */
	private void setRegCatCodeConsConfirmDialog() {
		getManagedBean().setRegCatCodeConfirmHeader(
				SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.header.physical", null));
		getManagedBean().setRegCatCodeConfirmMsg(
				SmCommonUtil.getMessage(
						"lbl.sm.regCatCode.confirm.msg.physical", null));
		if (getManagedBean().getSmAppl() != null) {
			if (StringUtils
					.isNotBlank(getManagedBean().getPhysicalConsulting())
					&& getManagedBean()
							.getPhysicalConsulting()
							.equals(my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE)) {
				getManagedBean().setRegCatCodeConfirmHeader(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.header.nonphysical",
								null));
				getManagedBean().setRegCatCodeConfirmMsg(
						SmCommonUtil.getMessage(
								"lbl.sm.regCatCode.confirm.msg.nonphysical",
								null));
			}
		}
	}

	/**
	 * Delete selected category code(s) that are still pending approval from the
	 * SmSupplierCategory list of the application.
	 */
	public void deleteSelectedPendingApprovalCategoryCodes(
			List<SmSupplierCategory> selectedCategories) {
		if (selectedCategories != null && selectedCategories.size() > 0) {
			List<SmSupplierCategory> categories = selectedCategories;
			deleteCatCodes(categories);

			// API FOR LIST OF OVERLAPPING CATEGORIES
			//Redmine #18959 - no need to check overlapping
			//setOverLappingCategories();

		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.catCodes.catcode", null) });
		}
		getManagedBean().setSelectedApprovedCategories(
				new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedPendingApprovedCategories(
				new ArrayList<SmSupplierCategory>());
	}

	/**
	 * On click add category code
	 */
	public void onAddCategoryCode() {
		RequestContext.getCurrentInstance().execute("catCodePopup.show();");
	}
	
	//26238 - start
	public void onTickPunishmentType(){		
		if(!getManagedBean().isWarning() && !getManagedBean().isCancelCategoryCode()
				&& !getManagedBean().isSuspendRegistration() && !getManagedBean().isCancelRegistration()
				&& !getManagedBean().isBlacklistOwnerOrDirector() && !getManagedBean().isBumiputeraStatusCancel()
				&& !getManagedBean().isManufacturerStatusCancel()){
			getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(false);
			getManagedBean().setEditDisciplinaryAction(false);
			//28281 - start
			getManagedBean().setSelectedApprovedCategories(new ArrayList<SmSupplierCategory>());
			List<SmSupplierCategory> catList = getManagedBean().getApprovedCategoryList();
			for(SmSupplierCategory cat: catList){
				cat.setSelected(false);
			}
			getManagedBean().setApprovedCategoryList(catList);
			//28494
			getManagedBean().setSelectedOwnerAndDirectorList(new ArrayList<SmPersonnel>());
			//28281 - end
			onTickDiscipline();
		} else {
			// 27815: Disciplinary Action - start - checking condition 
			if (getManagedBean().isCancelCategoryCode() || getManagedBean().isManufacturerStatusCancel() 
					|| getManagedBean().isBumiputeraStatusCancel()) {
				getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(true);
			} else if (getManagedBean().isBlacklistOwnerOrDirector()) {
				//26238 -  Disciplonary action - 28146 - start
				/*getManagedBean().setCancelRegistration(true);
				getManagedBean().setFlagBlacklistCancel(true);*/
				//26238 -  Disciplonary action - 28146 - end
			} else {
				getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(false);
				
				//26238 -  Disciplonary action - 28146 - start
				/*if(getManagedBean().isFlagBlacklistCancel()){
					getManagedBean().setCancelRegistration(false);
					getManagedBean().setFlagBlacklistCancel(false);
				}*/
				//26238 -  Disciplonary action - 28146 - end
			}

			//26238: Disciplinary Action - start - 28342
			//28281 - start (bumi code)
			if(getManagedBean().isWarning() || getManagedBean().isCancelCategoryCode()
					|| getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration()
					|| getManagedBean().isBlacklistOwnerOrDirector() && getManagedBean().isBumiputeraStatusCancel()
					|| getManagedBean().isManufacturerStatusCancel()){
				autoTickBumiCode(); 				 
			} else if (getManagedBean().isBumiputeraStatusCancel()){
				autoTickBumiCode();  
			}
			//28281 - end
			//26238: Disciplinary Action - end - 28342
			
			//27815: Disciplinary Action - end -checking condition 
			getManagedBean().setEditDisciplinaryAction(true);
			onTickDiscipline();
		}
	}
	//26238 - end

	public void onTickDiscipline() {
		//27426 & 27427 : Disciplinary Action
		//27857: Disciplinary Action - start - change checking condition - 26238
		if (
//				getManagedBean().isEditDisciplinaryAction()
//				&& !getManagedBean().isWarning() && !getManagedBean().isCancelCategoryCode()
//				&& !getManagedBean().isSuspendRegistration() && !getManagedBean().isCancelRegistration()
//				&& !getManagedBean().isBlacklistOwnerOrDirector() && !getManagedBean().isBumiputeraStatusCancel()
//				&& !getManagedBean().isManufacturerStatusCancel() ||
				!EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions()) &&
				getManagedBean().isEditDisciplinaryAction()) {
			initSupplierDisciplinaryAction(getManagedBean().getSmSupplier());
			getManagedBean().setEditDisciplinaryAction(true);
		} else if (
				EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions())
//				getManagedBean().isEditDisciplinaryAction()
//				|| getManagedBean().isWarning() || getManagedBean().isCancelCategoryCode()
//				|| getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration()
//				|| getManagedBean().isBlacklistOwnerOrDirector() || getManagedBean().isBumiputeraStatusCancel()
//				|| getManagedBean().isManufacturerStatusCancel()
				) { 
			getManagedBean().setDisciplineVO(getInitDisciplinaryActionVO());
		} else {
			initSupplierDisciplinaryAction(getManagedBean().getSmSupplier()); 
		}
		//27857: Disciplinary Action - end - 26238
	}

	/**
	 * Delete category codes.
	 * 
	 * @param categories
	 *            - List of categories
	 */
	private void deleteCatCodes(List<SmSupplierCategory> categories) {
		Iterator<SmSupplierCategory> apprCatIt = getManagedBean().getSmAppl()
				.getSmSupplierCategories().iterator();

		while (apprCatIt.hasNext()) {
			SmSupplierCategory apprCat = apprCatIt.next();
			//#25753 : Supplier Data Maintenance: Special Category Other Description
			if(apprCat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE || apprCat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
				SmSupplierCategory delCat = (SmSupplierCategory) EntityHelper
						.getEntityByMultiplePropertiesFromList(apprCat, categories,
								null, new String[] { "pmCategoryL1",
										"pmCategoryL2", "pmCategoryL3" }, null);
				if (delCat != null) {
					if (delCat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
						apprCatIt.remove();
					} else if (delCat.getRecordStatusFlg() == SupplierManagementConst.APPROVED_RECORD_STATUS) {
						apprCat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
						apprCat.setCancelledDate(new Date());
						getManagedBean().getDeleteApprovedCategories().add(apprCat);
					}
				}
			}
		}
		assignApplActiveAndApprovedCategoryList();
	}

	private void setActiveStatusForNewAddedCategories() {
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
				cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
				cat.setCategoryL1Name(cat.getPmCategoryL1().getCategoryName());
				cat.setCategoryL2Name(cat.getPmCategoryL2().getCategoryName());
				cat.setCategoryL3Name(cat.getPmCategoryL3().getCategoryName());
				cat.setApprovedDate(new Date());
				if (getManagedBean().isPoUser()) {
					cat.setPoDecFlg(1);
				} else {
					cat.setApprvrDecFlg(1);
				}
				SmApplicationUtil.setSupplierCategoryEffectiveDate(cat,
						getManagedBean().getSmAppl().getApplType());
			}
		}
	}

	/**
	 * Initialized data for search supplier
	 */
	private void initDataForSearchSupplier() {
		renewSesionObject();
		initLogin();
		initLoginUserZone();
		getManagedBean().setDisplaySearchPage(true);
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setViewSupplierMaintenancePage(true);		
		getManagedBean().setRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(
						null, null));
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());

		// for login user not semenanjung, only can select contractor
		/*
		if (getManagedBean().getLoginUserZone() != null
				&& !getManagedBean()
						.getLoginUserZone()
						.getZoneCode()
						.equals(my.ep.constant.sm.SupplierManagementConst.PM_ZONE_SEMENANJUNG)) {
			getManagedBean().setSearchSuppType(
					SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
		}
		*/
	}

	/**
	 * Check if the login user is the approver of the selected application
	 * 
	 * @return
	 */
	private boolean isLoginUserAllowedToEdit() {
		boolean retVal = isLoginUserTheApplApprover();

		// Contractor always allowed due to already filter zone
		if (getManagedBean().getSmAppl().getSupplierType()
				.equals(SupplierManagementConst.STYPE_CONTRACTOR)) {
			retVal = true;
		}

		return retVal;
	}

	private boolean isLoginUserTheApplApprover() {
		boolean retVal = false;
		FacesScopes sc = new FacesScopes();
		PmUser user = sc.getUserSessionBean();

		// for APPROVER can directly do maintenance for any application. But for
		// PO only can do maintenance for application that approved by
		// themselves only (#23699)
		if (sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ_TEMP)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER)) {
			retVal = true;
		} else {
			if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
					.getSmApplSectionReviews())) {
				// if review size equals to 1 means direct approve by PO
				if (getManagedBean().getSmAppl().getSmApplSectionReviews()
						.size() == 1) {
					getManagedBean().setApplApprover(
							SmCommonUtil.getUserByUserId(getManagedBean()
									.getSmAppl().getSmApplSectionReviews()
									.get(0).getChangedBy()));
					getManagedBean().setApprovedDate(
							DateUtils.dateToString("dd-MMM-yyyy",
									getManagedBean().getSmAppl()
											.getSmApplSectionReviews().get(0)
											.getChangedDate()));
					if (getManagedBean().getSmAppl().getSmApplSectionReviews()
							.get(0).getChangedBy().compareTo(user.getUserId()) == 0) {
						retVal = true;
					}
				} else {
					// if review size more than 1 means approve by Approver
					for (SmApplSectionReview rev : getManagedBean().getSmAppl()
							.getSmApplSectionReviews()) {
						if (rev.getRoleType().equals("A")) {
							getManagedBean().setApplApprover(
									SmCommonUtil.getUserByUserId(rev
											.getChangedBy()));
							getManagedBean().setApprovedDate(
									DateUtils.dateToString("dd-MMM-yyyy",
											getManagedBean().getSmAppl()
													.getSmApplSectionReviews()
													.get(0).getChangedDate()));
							if (rev.getChangedBy().compareTo(user.getUserId()) == 0) {
								retVal = true;
								break;
							}
						}
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Initialized login user zone
	 */
	/*private void initLoginUserZone() {
		FacesScopes fs = new FacesScopes();
		PmUser user = fs.getUserSessionBean();
		List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user
				.getUserId());
		String ePRole = StringUtils.EMPTY;
		if (!EntityHelper.isCollectionEmpty(userRoles)) {
			for (PmUserRole userRole : userRoles) {
				if (userRole.getPmRole().getRoleCode()
						.equals(RoleCode.UPS_APPROVER.value())
						|| userRole.getPmRole().getRoleCode()
								.equals(RoleCode.UPS_PO.value())) {
					ePRole = userRole.getPmRole().getRoleCode();
					break;
				}
			}
		}
		getManagedBean().setSearchPmZoneId(
				SmCommonUtil.getUserZoneIdByLoginIdAndEpRole(user.getLoginId(),
						ePRole));
		ZoneReqDTO reqDTO = new ZoneReqDTO();
		reqDTO.setZoneId(getManagedBean().getSearchPmZoneId());

		getManagedBean()
				.setLoginUserZone(
						SmServiceLookup.getZoneSO().retriveZoneByID(reqDTO)
								.getPmZone());

	}*/

	/**
	 * Set whether the login user is allowed to do data maintenance
	 */
	private void setLoginUserAllowedToDoMaintenance() {
		getManagedBean().setAllowedToDoMaintenance(false);
		if (isLoginUserAllowedToEdit()) {
			getManagedBean().setAllowedToDoMaintenance(true);
		}
	}

	/**
	 * To check whether selected supplier has active transaction
	 * 
	 * @param boolean isCancelMof
	 */
	private void checkIfSupplierHasActiveTransaction(boolean isCancelMof) {
		boolean alreadyHasMsg = false;
		boolean hasActiveAppl = false;
		FacesContext fc = FacesContext.getCurrentInstance();
		if (isCancelMof) {
			if (SmApplicationUtil.hasActiveTransactions(getManagedBean()
					.getSmSupplier())) {
				hasActiveAppl = true;
				alreadyHasMsg = true;
				SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_ERROR,
						MessageUtils.getMessage(MessageConst.ERROR_722609,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale()), null);
			}

		}
		if (!alreadyHasMsg
				&& SmApplicationUtil.checkActiveSmApplForSupp(getManagedBean()
						.getSmSupplier())) {
			hasActiveAppl = true;
			//Redmine #18959
			/*SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_ERROR,
					MessageUtils.getMessage(MessageConst.ERROR_722608,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);*/
		}
		getManagedBean().setHasActiveTransaction(hasActiveAppl);
	}

	/**
	 * Initialized supplier disciplinary action and set to DisciplinaryActionVO
	 * 
	 * @param SmSupplier
	 *            supplier
	 */
	private void initSupplierDisciplinaryAction(SmSupplier supplier) {
		//getManagedBean().setEditDisciplinaryAction(false); //27857: Disciplinary Action - disabled hardcoded value - 26238
		DisciplinaryActionVO discipline = getInitDisciplinaryActionVO();
		if (!EntityHelper
				.isCollectionEmpty(supplier.getSmDisciplinaryActions())) {
			for (SmDisciplinaryAction disc : supplier
					.getSmDisciplinaryActions()) {
				if (disc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					//27857: Disciplinary Action - start - 26238
					if(!getManagedBean().isEditDisciplinaryAction()){
						if(disc.isBlacklistOwner()){
							getManagedBean().setBlacklistOwnerOrDirector(true);
							getManagedBean().setBlacklistOwnerOrDirectorExist(true);
							
							//26238 -  Disciplonary action - 28146 - start
							if(disc.isCancelRegistration()){
								getManagedBean().setCancelRegistration(true);
								getManagedBean().setCancelRegistrationExist(true);
							}
							//26238 -  Disciplonary action - 28146 - end
						}
						if(disc.isCancelCategory()){
							getManagedBean().setCancelCategoryCode(true);
							getManagedBean().setCancelCategoryCodeExist(true);
						}
						if(disc.isSuspendRegistration()){
							getManagedBean().setSuspendRegistration(true);
							getManagedBean().setSuspendRegistrationExist(true);
						}
						if(disc.isWarning()){
							getManagedBean().setWarning(true);
							getManagedBean().setWarningExist(true);
						}
						if(disc.isCancelRegistration()){
							getManagedBean().setCancelRegistration(true);
							getManagedBean().setCancelRegistrationExist(true);
						}
						if(disc.isBumiputeraCancel()){
							getManagedBean().setBumiputeraStatusCancel(true);
							getManagedBean().setBumiputeraStatusCancelExist(true);
						}
						if(disc.isManufacturerCancel()){
							getManagedBean().setManufacturerStatusCancel(true);
							getManagedBean().setManufacturerStatusCancelExist(true);
						}
					}else{
						resetDisciplinaryActionSectionExist();
						getManagedBean().setEditDisciplinaryAction(true);
					}
					//27857: Disciplinary Action - end - 26238
					
					//28198: Disciplinary Action - start - 26238
					List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionRemarksByDA(disc);
					
					if(remarksList != null && remarksList.size() > 0){
						for (SmDisciplinaryActionRemarks remarks : remarksList) {
							discipline.getSmDiscplinaryAction().setRemark(remarks.getRemark());
							break;
						}
					}else{
						discipline.getSmDiscplinaryAction().setRemark(disc.getRemark());
					}
					//28198: Disciplinary Action - end - 26238
					discipline.getSmDiscplinaryAction().setStartDate(
							disc.getStartDate());
					discipline.getSmDiscplinaryAction().setEndDate(
							disc.getEndDate());
					discipline.setSuspend(true);
					// 28912 - get Action By for Disciplinary Action popup
					discipline.getSmDiscplinaryAction().setCreatedBy(
							disc.getCreatedBy());
					
					break;
				}
			}
		}
		getManagedBean().setDisciplineVO(discipline);
	}
	
	/**
	 * Initialized PO or approver overlapping category remarks info
	 */
	private void initPOApproverOverlapCategoryRemarkInfo() {
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		reqDTO.setDocType(ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER);
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
		
		// reset
	    getManagedBean().setOriPoApprOverlappedRemark(null);
		getManagedBean().setPoApprOverlappedRemark(null);
		getManagedBean().setRemarkId(null);
		
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(
				reqDTO);
		if(resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
			SmRemark remark = resDTO.getRemarksList().get(0);
			getManagedBean().setPoApprOverlappedRemark(remark.getRemark());
			getManagedBean().setOriPoApprOverlappedRemark(remark.getRemark());
			getManagedBean().setRemarkOverlappedId(remark.getRemarkId());
		}
		
		
	}
	
	private void initApproverRemarkInfo(){
		SmRemarksReqDTO reqDTO = new SmRemarksReqDTO();
		// 28912 start - bumiputra approver remark
		if(getManagedBean().isBumiputeraDataMaintainance()){
			reqDTO.setDocType(ParameterConst.SM_BUMI_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
		}else {
			reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
		}
		// 28912 end
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
		
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(
				reqDTO);
		
		if(resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
			SmRemark remark = resDTO.getRemarksList().get(0);
			getManagedBean().setApproverRemark(remark.getRemark());
			getManagedBean().setRemarkId(remark.getRemarkId());
		}
	}

	private DisciplinaryActionVO getInitDisciplinaryActionVO() {
		DisciplinaryActionVO discipline = new DisciplinaryActionVO();
		discipline.setSmDiscplinaryAction(new SmDisciplinaryAction());
		discipline.getSmDiscplinaryAction().setSmSupplier(
				getManagedBean().getSmSupplier());
		if (getManagedBean().getSmMofAccount() != null) {
			discipline.getSmDiscplinaryAction().setMofNo(
					getManagedBean().getSmMofAccount().getMofNo());
		}
		discipline.setSuspend(false);
		return discipline;
	}

	/**
	 * Validate entered disciplinary action
	 */
	private void validateDisciplinaryAction() {
		if (getManagedBean().isEditDisciplinaryAction()) {
			if (getManagedBean().getDisciplineVO() != null
					&& getManagedBean().getDisciplineVO().getSmDiscplinaryAction() != null) {
				if (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getStartDate() == null
						&& (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getEndDate() != null || !StringUtils
								.isBlank(getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getRemark()))) {
					throw new EpException(SmCommonUtil.getMessage("lbl.sm.is.required",
							new String[] { SmCommonUtil.getMessage("lbl.sm.disciplinary.jktt.start.date", null) }));
				} else if (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getEndDate() == null
						&& (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getStartDate() != null || !StringUtils
								.isBlank(getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getRemark()))) {
					throw new EpException(SmCommonUtil.getMessage("lbl.sm.is.required",
							new String[] { SmCommonUtil.getMessage("lbl.sm.disciplinary.jktt.end.date", null) }));
				} else if (StringUtils.isBlank(getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getRemark())
						&& (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getStartDate() != null || getManagedBean()
								.getDisciplineVO().getSmDiscplinaryAction().getEndDate() != null)) {
					throw new EpException(SmCommonUtil.getMessage("lbl.sm.is.required",
							new String[] { SmCommonUtil.getMessage("lbl.sm.disciplinary.jktt.remarks", null) }));
				} else if (getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getStartDate() != null
						&& getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getEndDate() != null
						&& getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getStartDate()
								.compareTo(getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getEndDate()) > 0) {
					throw new EpException(MessageConst.ERROR_722538);
				}
			}
		}
		
		if (StringUtils.isNotEmpty(this.getManagedBean().getApproverRemark())
				&& this.getManagedBean().getApproverRemark().length() > 2500) {
			throw new EpException(MessageConst.ERROR_71025, new String[] {String.valueOf(this.getManagedBean().getApproverRemark().length())});
		}
	}
	
	private void validateAddedCategory(List<SmSupplierCategory> activeCategories) {
		SmApplicationUtil
				.validateRenewalApplTypeForSpecialCategory(getManagedBean()
						.getSmAppl());
		String errOtherDesc = SmApplicationUtil
				.getCategoryOtherDescErrMsg(activeCategories);
		if (!StringUtils.isBlank(errOtherDesc)) {
			throw new EpException(MessageConst.ERROR_722514,
					new String[] { errOtherDesc });
		}
	}

	private void initPhysicalConsultingCatcode() {
		if (!StringUtils
				.isBlank(getManagedBean().getSmAppl().getSupplierType())
				&& getManagedBean().getSmAppl().getSupplierType().equals("J")) {
			if (SmCommonUtil.isApplPhysicalConsultant(getManagedBean()
					.getSmAppl())) {
				getManagedBean()
						.setPhysicalConsulting(
								my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE);
			} else {
				getManagedBean()
						.setPhysicalConsulting(
								my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE);
			}
		}
	}
	
	// Redmine #18959
	public void onViewActiveApplList() {
		RequestContext.getCurrentInstance().execute(
				"activeApplDialog.show();");
	}		
	
	private String getTrackingActionDesc(SupplierDataMaintenanceReqDTO reqDTO, String val) {
		StringBuffer sb = new StringBuffer();
		
		switch(val) {
		case "RS": // Registration Status
			sb.append("Status Pendaftaran telah dikemaskini daripada ")
				.append(getStatusDescByStatusIdForTracking(getManagedBean().getExistingStatusId()).getStatusName()).append(" kepada ")
				.append(getStatusDescByStatusIdForTracking(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId()).getStatusName());
		break;
		case "ED": // MOF Expiry Date
			sb.append("Tarikh Luput Akaun MOF Pembekal telah dikemaskini dari ")
				.append(DateUtils.dateToString("dd/MM/yyyy", reqDTO.getOldMofExpiryDate())).append(" kepada ")
				.append(DateUtils.dateToString("dd/MM/yyyy", getManagedBean().getSmMofAccount().getExpDate()));
			break;
		case "DA": // Disclipinary Action
			sb.append("Pembekal telah dikenakan tindakan tatatertib dari ")
				.append(DateUtils.dateToString("dd/MM/yyyy", reqDTO.getSmDisciplinaryAction().getStartDate())).append(" hingga ")
				.append(DateUtils.dateToString("dd/MM/yyyy", reqDTO.getSmDisciplinaryAction().getEndDate()));
		break;
		case "AC": // Add new Category Code			
			if (!EntityHelper.isCollectionEmpty(reqDTO.getSmAppl().getSmSupplierCategories())) {
				sb.append("Bidang berikut telah ditambah : ");
				int i = reqDTO.getNewlyAddedCategories().size();
				for (SmSupplierCategory sc : reqDTO.getNewlyAddedCategories()) {
					i = i-1;
					sb.append(sc.getPmCategoryL1().getCategoryL1Code()
								+ sc.getPmCategoryL2().getCategoryL2Code()
								+ sc.getPmCategoryL3().getCategoryL3Code());
					if(i > 0){
						sb.append(", ");
					}
				}
			}
		break;
		case "DC": // Delete Category Code Approved
			if (!EntityHelper.isCollectionEmpty(reqDTO.getSmAppl().getSmSupplierCategories())) {
				sb.append("Bidang berikut telah dihapuskan : ");
				int i = reqDTO.getDeletedCategories().size();
				for (SmSupplierCategory sc : reqDTO.getDeletedCategories()) {
					i = i-1;
					sb.append(sc.getPmCategoryL1().getCategoryL1Code()
								+ sc.getPmCategoryL2().getCategoryL2Code()
								+ sc.getPmCategoryL3().getCategoryL3Code());
					if(i > 0){
						sb.append(", ");
					}
				}
			}
		break;
		case "OLA": // Overlap remark
			String oriPoApprOverlappedRemark = getManagedBean().getOriPoApprOverlappedRemark();
			if (oriPoApprOverlappedRemark == null) {
				oriPoApprOverlappedRemark = "(tiada)";
			}
			String latestPoApprOverlappedRemark = getManagedBean().getPoApprOverlappedRemark();
			if (latestPoApprOverlappedRemark == null) {
				latestPoApprOverlappedRemark = "(tiada)";
			}
			PmUser user = new FacesScopes().getUserSessionBean();
			sb.append("Catatan pertindihan bidang dari ")
				.append(oriPoApprOverlappedRemark).append(" kepada ")
				.append(latestPoApprOverlappedRemark).append(" oleh ").append(user.getUserName());
		break;
		default:
			sb = new StringBuffer();
			sb.append(SupplierManagementConst.APPROVER_SUBMIT_SUPPLIER_DATA_MAINTENANCE);
		}
		return sb.toString();
	}
	
	/**
	 * Get status description by status ID for Tracking purpose
	 * 
	 * @param Long
	 *            statusId
	 * @return PmStatusDesc
	 */
	public static PmStatusDesc getStatusDescByStatusIdForTracking(Long statusId) {
		PmStatusDesc retVal = null;
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(statusId);
		reqDTO.setLanguageCode("ms");
		StatusResDTO resDTO = SmServiceLookup.getStatusSO()
				.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			retVal = resDTO.getPmStatusDesc();
		}
		return retVal;
	}
	
	/**
	 * Get PmRegStatus by status ID for 
	 * 
	 * @param Long
	 *            statusId
	 * @return PmRegStatus
	 */
	public static PmRegStatus getPmRegStatusByStatusId(Long statusId) {
		PmRegStatus retVal = null;
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(statusId);
		StatusResDTO resDTO = SmServiceLookup.getStatusSO().retrieveStatusByStatusId(reqDTO);
		if (resDTO != null && resDTO.getPmStatus().getPmRegStatus() != null) {
			retVal = resDTO.getPmStatus().getPmRegStatus();
		}
		return retVal;
	}
	
	public void onViewApprRemarkHistoryList() {
		RequestContext.getCurrentInstance().execute("approverRemarkDialog.show();");
	}

	private void approvalHistoryRemarklist() {
		// 28912 start - bumiputera approver remark
		if(getManagedBean().isBumiputeraDataMaintainance()){
			SmRemaksResDTO resDTO = SmApplicationUtil.getRemarksByDoc(
					ParameterConst.SM_BUMI_DATA_MAINTENANCE_RMRK_DECISION_APPROVE, getManagedBean().getSmAppl().getApplId());
			resDTO.getRemarksList();
			getManagedBean().setApproverRemarkList(resDTO.getRemarksList());
		}else {
			SmRemaksResDTO resDTO = SmApplicationUtil.getRemarksByDoc(
					ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE, getManagedBean().getSmAppl().getApplId());
			resDTO.getRemarksList();
			getManagedBean().setApproverRemarkList(resDTO.getRemarksList());
		}
		// 28912 end
	}

	public String getUserNameByUserId(Long userId) {
		return PmCommonUtil.retriveUserName(userId);

	}
	
	//26238 - start
	public void onViewDisciplinaryAction() {
		SmSupplier currSupp = getManagedBean().getSmSupplier();
		if(currSupp != null){
			List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
			if(!EntityHelper.isCollectionEmpty(voList)
					&& voList.size() > 0){
				getManagedBean().setDisciplineVOs(voList);
			} else {
				getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
			}
		} else {
			getManagedBean().setDisciplineVOs(new ArrayList<DisciplinaryActionVO>());
		}
		RequestContext.getCurrentInstance().execute(
				"disciplinaryActionHistoryDlg.show();");
	} 
	
	//28281
	/*
	private static List<DisciplinaryActionVO> getDisciplinaryActionVO(SmSupplier currSupp) {
		List<DisciplinaryActionVO> voList = new ArrayList<DisciplinaryActionVO>();
		PersonnelReqDTO perReqDTO = new PersonnelReqDTO();
		perReqDTO.setSmSupplierId(currSupp.getSupplierId());
		
		//27915: Disciplinary Action - start - 26238
		PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelDABySupplierId(perReqDTO);
		//27915: Disciplinary Action - end - 26238
		
		//27796: Disciplinary Action - start - 26238
		List<SmDisciplinaryAction> resultList = SmApplicationUtil.retrieveDisciplinaryActionHistoryBySupplier(currSupp);
		
		if (resultList != null
				&& resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++){
				SmDisciplinaryAction disciplinaryAction = resultList.get(i);
				DisciplinaryActionVO retVal = new DisciplinaryActionVO();
				List<DisciplinaryActionVO> remList = new ArrayList<DisciplinaryActionVO>();
				if(disciplinaryAction.getEditNo() == 1){
					if(disciplinaryAction.isWarning() || disciplinaryAction.isCancelCategory() 
							|| disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
							|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
							|| disciplinaryAction.isBumiputeraCancel()){
						
						if(disciplinaryAction.getStartDate() != null)
							retVal.setStartDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getStartDate()));
						retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						if(disciplinaryAction.getEndDate() != null)
							retVal.setEndDate(DateUtils.dateToString("dd/MM/yyyy",disciplinaryAction.getEndDate()));
						
						DisciplinaryActionVO rem = new DisciplinaryActionVO();
						rem.setRemarks(disciplinaryAction.getRemark());
						remList.add(rem);
						for (int k = i+1; k < resultList.size(); k++){
							SmDisciplinaryAction da1 = resultList.get(k);
							if(disciplinaryAction.getEndDate().toString().contentEquals(da1.getEndDate().toString()) &&
									disciplinaryAction.getStartDate().toString().contentEquals(da1.getStartDate().toString())
									){
								DisciplinaryActionVO rem1 = new DisciplinaryActionVO();
								rem1.setRemarks(da1.getRemark());
								remList.add(rem1);
								
							}
						}
						retVal.setRemarksList(remList);
						
						if(disciplinaryAction.isWarning())
							retVal.setWarning(true);
						if(disciplinaryAction.isCancelCategory())
							retVal.setCancelCategory(true);
						if(disciplinaryAction.isSuspendRegistration())
							retVal.setSuspendRegistration(true);
						if(disciplinaryAction.isCancelRegistration())
							retVal.setCancelRegistration(true);
						if(disciplinaryAction.isBumiputeraCancel())
							retVal.setBumiputeraCancel(true);
						if(disciplinaryAction.isManufacturerCancel())
							retVal.setManufacturerCancel(true);
						if(disciplinaryAction.isBlacklistOwner())
							retVal.setBlacklistOwnerDirector(true);
						//27576: Disciplinary Action - start
						if(disciplinaryAction.isCancelCategory() || disciplinaryAction.isManufacturerCancel()){
							List<DisciplinaryActionCategoryVO> catVoList = new ArrayList<DisciplinaryActionCategoryVO>();
							//27617 : Disciplinary Action - 26238
							List<Object[]> catList = SmApplicationUtil.retrieveCancelCategoryByDisciplinaryActionId(disciplinaryAction.getDisciplinaryActionId());
							
							if(catList != null
									&& catList.size() > 0){
								for(Object[] arr : catList){
									if(arr[7] != null){
										if(disciplinaryAction.getDisciplinaryActionId().toString().equalsIgnoreCase(arr[7].toString())){
											DisciplinaryActionCategoryVO catVo = new DisciplinaryActionCategoryVO();
											String catCode = StringUtils.EMPTY;
											catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
											catVo.setCategoryCode(catCode);	
											catVoList.add(catVo);
										}
									}								
								}						
								retVal.setCategoryCodeList(catVoList);
							}
						}

						//27576: Disciplinary Action - end
						
						if(disciplinaryAction.isBlacklistOwner()
								|| disciplinaryAction.isSuspendRegistration()
									|| disciplinaryAction.isCancelRegistration()){
							List<BlacklistOwnerDirectorVO> blacklistownerDirectorList = new ArrayList<BlacklistOwnerDirectorVO>();
							if(personnelResDTO != null 
									&& personnelResDTO.getSmPersonnelList().size() > 0){
								for(SmPersonnel personnel : personnelResDTO.getSmPersonnelList()){
									BlacklistOwnerDirectorVO blacklistOwner = new BlacklistOwnerDirectorVO();
									if(personnel.isCancelRegistration()
											|| personnel.isSuspendRegistration()
												|| personnel.isBlacklisted()){
										blacklistOwner.setIcNo(personnel.getIdentificationNo());
										blacklistownerDirectorList.add(blacklistOwner);
									}
								}
								retVal.setBlacklistownerDirectorList(blacklistownerDirectorList);
							}
						}
						voList.add(retVal);	
					}
				}
							
			}
			//27796: Disciplinary Action - end - 26238
		}
		return voList;
	}
	*/
	/**
	 * Retrieve owner and director list
	 */
	private void getOwnerAndDirectorList() {
		List<SmPersonnel> perList = new ArrayList<SmPersonnel>();
		
		for (SmPersonnel per : getManagedBean().getSmAppl()
				.getSmPersonnels()) {
			if (per.getRecordStatusFlg().equals(SupplierManagementConst.APPROVED_RECORD_STATUS)) {
				if((per.isEquityOwner() || per.isDirector()) && per.getIdentificationNo().length() == 12){
					perList.add(per);
				}
			}
		}
		getManagedBean().setOwnerAndDirectorList(perList);
	}
	
	
	/**
	 * retrieve PM FinancialOrg List 
	 */
	public List<PmFinancialOrg> retrieveFinancialOrgList() {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType("PB");
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO
				.retrieveFinancialOrgListByServiceType(reqDTO);
		return resDTO.getFinancialOrgList();
	}
	
	/**
	 * add new row to Supplier Bank
	 */
	public void addSupplierBank() {

		if (getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			getManagedBean().getSmAppl().setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		
		checkMaxBankAccount();
		
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		smSuppBank.setSmAppl(getManagedBean().getSmAppl());
		smSuppBank.setAccountPurpose(defaultAccPurpose);
		smSuppBank.setForHqFlg(1);
		getManagedBean().getSmAppl().getSmSupplierBanks().add(smSuppBank);
		// this.getManagedBean().getSmSuppBanks().add(new SmSupplierBank());
	}
	
	private void checkMaxBankAccount(){
		int maxPaymentBank = SmServiceLookup.getSmRuleSO().retrieveMaxPaymentBank().getMaxPaymentBank();
		
		if(getManagedBean().getSmAppl().getSmSupplierBanks().size() >= maxPaymentBank){
			throw new EpException(MessageConst.ERROR_721049,new String[]{String.valueOf(maxPaymentBank)});
		}
	}
	
	/**
	 * remove selected Supplier Bank
	 */
	public void removeSupplierBank() {
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			log.info("---removeSupplierBank 1---");
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(suppBank);
				log.info("---removeSupplierBank---" + suppBank);
				// suppBank.getPmFinancialOrg().getFinancialOrgId()
			}
		}
		
		this.getManagedBean().setSelectedSuppBanks(null);
	}
	
	/**
	 * Supplier Bank Validation
	 */
	public void validateBankInfo(List<SmSupplierBank> banks){
		List<String> bankList = new ArrayList<String>();
		for(SmSupplierBank suppBank : banks){
			log.info("*********suppBank.getPmFinancialOrg().getFinancialOrgId()****" + suppBank.getPmFinancialOrg().getFinancialOrgId());
			if(suppBank.getPmFinancialOrg().getFinancialOrgId() == null){
				throw new EpException(MessageConst.ERROR_721008);
			}

			if(StringUtils.isBlank(suppBank.getAccountNo())){
				throw new EpException(MessageConst.ERROR_721009);
			}
				
			if (StringUtils.isNotBlank(suppBank.getAccountNo()) && !StringUtils.isNumeric(suppBank.getAccountNo())) {
				throw new EpException(MessageConst.ERROR_722616);
			}

			String uniqBank = suppBank.getPmFinancialOrg().getFinancialOrgId().toString() + suppBank.getAccountNo();
			if(bankList.contains(uniqBank)){
				throw new EpException(MessageConst.ERROR_721012);
			}
			bankList.add(uniqBank);
		}
	}
	
	public String getOwnerLabel(){
		String bizType = getManagedBean().getSmSupp().getBusinessType();
		if(bizType != null){
			if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
				return "lbl.sm.director.name";
			}else if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)){
				return "lbl.sm.chairman.name";
			}else{
				return "lbl.sm.ownername";
			}
		}else{
			return "lbl.sm.ownername";
		}
	}
	
	/**
	 * retrieve identity type from PM
	 * @return SelectItem[]
	 */
	public SelectItem[] pmIdTypeDropdownValues() {
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		for(SelectItem selectItem : items) {
			
			if(selectItem.getValue() == null){
				itemList.add(selectItem);
			}else{
				PmParameter pmParameter = (PmParameter) identityType.get(selectItem.getValue());
				if(!pmParameter.getParameterCode().equals("A")){
					itemList.add(selectItem);
				}
			}
			
		}
		
		return itemList.toArray(new SelectItem[itemList.size()]);
		
	}
	
	/**
	 * Set whether to disable company name and business type field from editing
	 * in maintenance
	 */
	private void setDisableCompanyNameBusinessTypeInMaintenance() {
		getManagedBean().setDisabledCompanyNameInMaintenance(false);
		if (SmApplicationUtil.hasActiveTransactions(getManagedBean()
				.getSmSupp()) || SmApplicationUtil.hasOtherActiveTransactions(getManagedBean()
				.getSmSupp())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
		}
		
		if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
		}
		if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_ALLOWED_TO_BE_EDITED
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		
		SmAppl isActieveAppl = SmCommonUtil.getActiveApplBySupplier(getManagedBean().getSmSupp());
		if(isActieveAppl != null){
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		
		if (SmApplicationUtil
				.isApplHasSoftcertProcessingNotComplete(getManagedBean()
						.getSmSupp().getSmAppl())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
	}
	
	/**
	 * Retrieve Country List from PM
	 */
	private void retrievePmCountryList() {
		List<SelectItem> countryLs = SmCommonUtil.getCountryList();
		this.getManagedBean().setCountryLs(countryLs);
	}
	
	/**
	 * populate indiviual Service Type
	 */
	public void getIndividualServiceType(){
		
		List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SPECIAL_SERVICE_CODE, new FacesScopes().getUserLocale().getLanguage());
		getManagedBean().setServiceLs(paramDesc);
	}
	
	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateStateByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			//List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			//this.getManagedBean().setBusinessStateLs(stateLs);
			if(reset){
				getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
			}
			pmStateDropdownValues();
			//registrationBackingBean.managedBean.smBusinessAdd.pmState.
			if(getManagedBean().getSmBusinessAdd().isBrandNew()){
				getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
				getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
		} else if (typeAdd.equals("residential")) {
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		} else if (typeAdd.equals("permenant")) {
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmPermenantAdd().getPmCountry().getCountryId());
			this.getManagedBean().setPermenantStateLs(stateLs);
			//registrationBackingBean.managedBean.smPermenantAdd.pmCountry.countryId
			if(getManagedBean().getSmPermenantAdd().isBrandNew()){
				getManagedBean().getSmPermenantAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmPermenantAdd().getPmCity().setCityId(null);
				getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
				getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());
			}
		}
	}

	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateDistrictByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId()));
			if(reset){
				//13320-Start
				if(state !=null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				this.getManagedBean().getSmBusinessAdd().getPmDivision().setDivisionId(null);
				this.getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
				this.getManagedBean().setBusinessDivisionLs(divisionLs);
				getManagedBean().setBusinessDistrictLs(new ArrayList<SelectItem>());
				this.getManagedBean().setBusinessCityLs(null);	
			}
				if(state !=null && state.getAdoptDivision()) {
					getManagedBean().setDivisionBox(false);	
				}
				else{	
					getManagedBean().setDivisionBox(true);//13320-End
			List<SelectItem> divisionLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
			this.getManagedBean().setBusinessDistrictLs(divisionLs);
			this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			
				}
			}
		} else if (typeAdd.equals("residential")) {
			log.info("------------residential----------------");
			//13320-Start
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmResAdd().getPmState().getStateId()));
			if(state !=null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				this.getManagedBean().getSmResAdd().getPmDivision().setDivisionId(null);
				this.getManagedBean().getSmResAdd().getPmDistrict().setDistrictId(null);
				this.getManagedBean().getSmResAdd().getPmCity().setCityId(null);
				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmResAdd().getPmState().getStateId());
				this.getManagedBean().setResidentialDivisionLs(divisionLs);
				getManagedBean().setResidentialDistrictLs(new ArrayList<SelectItem>());
				this.getManagedBean().setResidentialCityLs(null);	
				
			}
				if(state !=null && state.getAdoptDivision()) {
					getManagedBean().setDivisionBox(false);	
				}
				else{	
				getManagedBean().setDivisionBox(true);// 13320-End
				List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmResAdd()
						.getPmState().getStateId());
				this.getManagedBean().setResidentialDistrictLs(districtLs);
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
				}
		} else if (typeAdd.equals("permenant")) {
			log.info("------------permenant----------------"
					+ this.getManagedBean().getSmPermenantAdd().getPmState()
					.getStateId());
			//13320-Start
			PmState state = (SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId()));

			if(state !=null && state.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)) {
				this.getManagedBean().getSmPermenantAdd().getPmDivision().setDivisionId(null);
				this.getManagedBean().getSmPermenantAdd().getPmDistrict().setDistrictId(null);
				this.getManagedBean().getSmPermenantAdd().getPmCity().setCityId(null);
				
				List<SelectItem> divisionLs = SmCommonUtil.getDivisionList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
				this.getManagedBean().setPermenantDivisionLs(divisionLs);
				getManagedBean().setPermenantDistrictLs(new ArrayList<SelectItem>());
				this.getManagedBean().setPermenantCityLs(null);	
				
			}
				if(state !=null && state.getAdoptDivision()) {
					getManagedBean().setDivisionBox(false);	
				}
				else{	
					getManagedBean().setDivisionBox(true);//13320-End
			List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
			this.getManagedBean().setPermenantDistrictLs(districtLs);
			this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());

			log.info("------permenant LS-------------"
					+ this.getManagedBean().getPermenantDistrictLs().size());
		}
		}
	}

	/**
	 * set City By AddType
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateCityByAddType(String typeAdd, boolean reset) {
		log.info("---------------City By Type--------------------");
		if (typeAdd.equals("business")) {
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			}
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(SmCommonUtil.getInstance().getDistrictById(getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId()));
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setBusinessCityLs(cityLs);
			populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		} else if (typeAdd.equals("residential")) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmResAdd().getPmDistrict().getDistrictId());
			getManagedBean().setResidentialCityLs(cityLs);
		} else if (typeAdd.equals("permenant")) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId());
			getManagedBean().setPermenantCityLs(cityLs);
		}
	}
	
	public void onSelectCity(String typeAdd){
		if (typeAdd.equals("business")) {
			getManagedBean().getSmBusinessAdd().setPmCity(SmCommonUtil.getInstance().getCityById(getManagedBean().getSmBusinessAdd().getPmCity().getCityId()));
		}
	}
	
	/**
	 * set state drop down value
	 */
	public void pmStateDropdownValues() {
		Long countryId = this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId();
		List<PmState> stateLs = retrievePmStateList(countryId);
		List<SelectItem> stateSelectLs = new ArrayList<SelectItem>();
		PmState state = new PmState();
		
		if(this.getManagedBean().getBusinessStateLs() == null){
			this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
		}
		
		if(this.getManagedBean().getSmSupp().getBusinessType() != null && !this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase("")){
			log.info("------pmStateDropdownValues------" + this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			if(SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE.contains(this.getManagedBean().getSmSupp().getBusinessType())){

				PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
				this.getManagedBean().setSabahSarawakType(true);
				this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
				
				if(this.getManagedBean().getSmBusinessAdd().isBrandNew()){
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
					this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				}

				//for(PmState state : stateLs){
				if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
					List<PmState> sarawakLs = SmCommonUtil.getInstance().retrieveStateByZone(SupplierManagementConst.SM_STATE_NAME_SARAWAK, 
							SupplierManagementConst.COUNTRY_MALAYSIA_CODE);				
					if(sarawakLs.size() > 0){
						stateSelectLs = EntityHelper.convertListToSelectItem(sarawakLs,"stateName", "stateId");
						state = sarawakLs.get(0);
						this.getManagedBean().getSmBusinessAdd().setPmState(state);
						this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
					}
				}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
					List<PmState> sabahLs = SmCommonUtil.getInstance().retrieveStateByZone(SupplierManagementConst.SM_STATE_NAME_SABAH, 
							SupplierManagementConst.COUNTRY_MALAYSIA_CODE);	
					if(sabahLs.size() > 0){
						stateSelectLs = EntityHelper.convertListToSelectItem(sabahLs,"stateName", "stateId");
						state = sabahLs.get(0);
						this.getManagedBean().getSmBusinessAdd().setPmState(state);
						this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
					}
				}
				//}
				
				if(getManagedBean().getSmBusinessAdd().isBrandNew()){
					getManagedBean().setBusinessCityLs(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().getSmSupp().setLocalAuthorityId(null);
				}
			}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) ||
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)){
				/*List<PmState> sabahLs = SmCommonUtil.getInstance().retrieveStateByZone(SupplierManagementConst.SM_STATE_NAME_SEMENANJUNG, 
						SupplierManagementConst.COUNTRY_MALAYSIA_CODE);	
				stateSelectLs  = EntityHelper.convertListToSelectItem(sabahLs,"stateName", "stateId");*/
				List<PmState> semenanjungLs = new ArrayList<PmState>();
				
				for(PmState smjState : stateLs){
					if(!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SABAH_CODE) && 
							!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
						semenanjungLs.add(smjState);
					}
				}
				
				stateSelectLs  = EntityHelper.convertListToSelectItem(semenanjungLs,"stateName", "stateId");
				
				if(getManagedBean().getSmBusinessAdd().isBrandNew()){
					getManagedBean().setSabahSarawakType(false);
					getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().setBusinessDistrictLs(null);
					getManagedBean().setBusinessCityLs(null);
				}
			}else{
				this.getManagedBean().setSabahSarawakType(false);
				//this.getManagedBean().setBusinessStateLs(stateLs);
			}
		}
		
		if(stateSelectLs.size() < 1){
			stateSelectLs = EntityHelper.convertListToSelectItem(stateLs,"stateName", "stateId");
		}
		this.getManagedBean().setBusinessStateLs(stateSelectLs);
		populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		
	}
	
	/**
	 * retrieve State List by country Id
	 * @param countryId
	 * @return List<PmState>
	 */
	private List<PmState> retrievePmStateList(Long countryId) {
		if(countryId != null){
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			return resDTO.getStateList();
		}

		return null;
	}
	
	public boolean isGmapDisabled() {
		gmapDisabled = false;
		
		if(getManagedBean().getSmBusinessAdd() != null){
			SmAddress bizAddress = getManagedBean().getSmBusinessAdd();

			if(bizAddress.getPmCountry() == null || bizAddress.getPmDistrict() == null || 
					bizAddress.getPmState() == null || bizAddress.getPmCity() == null || 
					bizAddress.getAddress1() == null ){
					gmapDisabled = true;
			}else{
				if(StringUtils.isBlank(bizAddress.getAddress1()) || bizAddress.getPmCountry().getCountryId() == null ||
						bizAddress.getPmDistrict().getDistrictId() == null || bizAddress.getPmState().getStateId() == null ||
						bizAddress.getPmCity().getCityId() == null){
					gmapDisabled = true;
				}

			}
		}else{
			gmapDisabled = true;
		}

		return gmapDisabled;
	}
	
	public void initGstInfo(SmSupplier sup,
			ViewSuppProfileManagedBean managedBean, boolean loadBranchGst) {

		managedBean.setSmGstSupplier(SmCommonUtil
				.getSmGstSupplierBySupplierIdAndBranchCode(sup.getSupplierId(),
						null, null, true));
		if (managedBean.getSmGstSupplier() == null) {
			managedBean.setSmGstSupplier(new SmGstSupplier());
			managedBean.getSmGstSupplier().setIsCurrent(true);
		}
		SmApplicationUtil.initGstSuppDocs(managedBean.getSmGstSupplier());
		if (loadBranchGst) {
			SmApplicationUtil.initBranchGstInformation(
					managedBean.getSmGstSupplier(), managedBean.getSmAppl());
		}
	}
	
	public void retrieveAllDisciplinaryAction(){
		SmSupplier supplier = getManagedBean().getSmSupplier();
		getManagedBean().setWarningExist(false); //27426 : Disciplinary Action
		if(supplier != null){
			//27724 : Disciplinary Action - start
//			List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveSmDisciplinaryActionBySupplier(supplier);
			List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
			//27724 : Disciplinary Action - end
			if(discList != null && discList.size() > 0){
				for(SmDisciplinaryAction disc : discList){
					if(disc.isWarning()){
						getManagedBean().setWarningExist(true);
					}
				}
			}
		}
	}
	//26238 - end
	
	//27426 : Disciplinary Action - start
	public void resetDisciplinaryActionSection(){
		getManagedBean().setWarning(false);
		getManagedBean().setCancelCategoryCode(false);
		getManagedBean().setSuspendRegistration(false);
		getManagedBean().setCancelRegistration(false);
		getManagedBean().setBlacklistOwnerOrDirector(false);
		getManagedBean().setManufacturerStatusCancel(false);
		getManagedBean().setBumiputeraStatusCancel(false);
		getManagedBean().setEditDisciplinaryAction(false);
	}
	//27426 : Disciplinary Action - end
	
	//27857: Disciplinary Action - start - 26238
	public void resetDisciplinaryActionSectionExist(){
		getManagedBean().setWarningExist(false);
		getManagedBean().setCancelCategoryCodeExist(false);
		getManagedBean().setSuspendRegistrationExist(false);
		getManagedBean().setCancelRegistrationExist(false);
		getManagedBean().setBlacklistOwnerOrDirectorExist(false);
		getManagedBean().setManufacturerStatusCancelExist(false);
		getManagedBean().setBumiputeraStatusCancelExist(false);
	}
	//27857: Disciplinary Action - end - 26238
	
	//26238 : Disciplinary Action - start 28111
	public void downloadDAHistory(SmSupplier supplier) throws Exception {
		String language = new FacesScopes().getUserLocale().getLanguage();
		if(supplier == null){
			supplier = getManagedBean().getSmSupplier();
		}
		
		if(supplier == null){
			supplier = SmApplicationUtil.getCurrentSupplier();
		}
		
		log.info(supplier.getSupplierId().toString()+"-----------------Supplier ID-------------------------------");
		if (language.equals("en")) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("PSUPPLIERID", supplier.getSupplierId().toString());
			generateReport(ReportConst.SM_DISCIPLINARY_ACTION_HISTORY_ENG, paramMap);
		} else {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("PSUPPLIERID", supplier.getSupplierId().toString());
			generateReport(ReportConst.SM_DISCIPLINARY_ACTION_HISTORY_MALAY, paramMap);
		}
	}
	//26238 : Disciplinary Action - end 28111
	
	//START 17906
	public void checkingBeforeRemoveSupplierBank() {
		StringBuilder sb = new StringBuilder();
		sb.append("<font color='red'>");
		boolean showErrorContract = false;//to avoid duplicate message
		boolean showErrorFl = false;//to avoid duplicate message
		boolean showErrorPopup = false;
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			this.getManagedBean().setDisplayMessageBank("");
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				if(suppBank.isHasActiveTransactionContractManagement() && !showErrorContract){
					showErrorPopup = true;
					showErrorContract = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.active.contract.tied",null));
					sb.append("<br/>");
				}
				if(suppBank.isHasActiveTransactionFulfilment() && !showErrorFl){
					showErrorPopup = true;
					showErrorFl = true;
					sb.append(SmCommonUtil.getMessage("lbl.sm.bankDtls.is.other.active.contract.tied",null));
					sb.append("<br/>");
				}				
			}
		}
		sb.append("</font>");
		this.getManagedBean().setDisplayMessageBank(sb.toString());
		if(showErrorPopup){
			RequestContext.getCurrentInstance().execute("bankAlertPopUp.show()");
			return;
		}
		RequestContext.getCurrentInstance().execute("bankDeletePopUp.show()");
	}
	//End 17906
	
	//26238 -  Disciplonary action - 28146 - start
	private SmDisciplinaryAction cloneSmDisciplinaryActionEditableValue(SmDisciplinaryAction newSmDisciplinaryAction, SmDisciplinaryAction smDisciplinaryAction){
		
		newSmDisciplinaryAction.setStartDate(smDisciplinaryAction.getStartDate());
		newSmDisciplinaryAction.setEndDate(smDisciplinaryAction.getEndDate());
		newSmDisciplinaryAction.setRemark(smDisciplinaryAction.getRemark());
		
		return newSmDisciplinaryAction;
		
	}
	//26238 -  Disciplonary action - 28146 - end
	
	
	//28281: auto-tick bumi code when bumi status DA is ticked
	private void autoTickBumiCode(){
		List<SmSupplierCategory> catList = getManagedBean().getApprovedCategoryList();
		if(catList == null || catList.isEmpty()) return;		
		List<Long> bumiList = getManagedBean().getBumiCodeList();
		if(bumiList == null || bumiList.isEmpty()){
			bumiList = SmApplicationUtil.retrieveBumiCodeList();
			getManagedBean().setBumiCodeList(bumiList);
		}
		
		List<SmSupplierCategory> selectList = getManagedBean().getSelectedApprovedCategories();
		for(SmSupplierCategory cat: catList){
			if(bumiList.contains(cat.getPmCategoryL3().getCategoryL3Id())){
				if(!cat.isSelected()) selectList.add(cat);
				cat.setSelected(true);
			}
		}		
		getManagedBean().setApprovedCategoryList(catList);
		getManagedBean().setSelectedApprovedCategories(selectList);
		// Start 27815,28324
		if (getManagedBean().isWarning() || getManagedBean().isCancelCategoryCode()
				|| getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration()
				|| getManagedBean().isBlacklistOwnerOrDirector() || getManagedBean().isManufacturerStatusCancel() 
				&& getManagedBean().isBumiputeraStatusCancel()) {
			log.info("---do nothing semua DA---");
			getManagedBean().setDisabledApprovedCategories(CollectionUtils.isNotEmpty(selectList) ? false : true);
		} else if(!getManagedBean().isWarning() && !getManagedBean().isCancelCategoryCode()
				&& !getManagedBean().isSuspendRegistration() && !getManagedBean().isCancelRegistration()
				&& !getManagedBean().isBlacklistOwnerOrDirector() && getManagedBean().isBumiputeraStatusCancel()
				&& !getManagedBean().isManufacturerStatusCancel())
			getManagedBean().setDisabledApprovedCategories(CollectionUtils.isNotEmpty(selectList) ? true : false);
		
		// End 27815,28324
	}
	//28281 end
	
	//13320-Start
	public void populateDivisionByAddType(String typeAdd, boolean reset) {
		log.info("---------------Division By Type--------------------");
			if((typeAdd.equals("business"))){
			List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId());
			this.getManagedBean().setBusinessDistrictLs(divisionLs);
			this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());	
		}
			if((typeAdd.equals("residential"))){
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmResAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setResidentialDistrictLs(divisionLs);
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());	
			}
	       if((typeAdd.equals("permenant"))){
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmPermenantAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setPermenantDistrictLs(divisionLs);
				this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());	
			}
	}
	//13320-end
	
	// 28912 start
	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderBumiputeraSupplierDataMaintenance() {
		initDataForSearchBumiputeraSupplier();
		SmCommonUtil.redirectToPage("/xhtml/sm/supplierdatamaintenance/searchBumiputeraSupplierDataMaintenance.xhtml",
				"", null, null);
	}

	/**
	 * Initialized data for search supplier
	 */
	private void initDataForSearchBumiputeraSupplier() {
		renewSesionObject();
		initLogin();
		initLoginUserZone();
		getManagedBean().setDisplaySearchPage(true);
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setViewSupplierMaintenancePage(true);
		getManagedBean().setRegStatusDescList(
				SmApplicationUtil.getInstance().retrieveRegistrationStatusList(null,
						ParameterConst.NEW_SM_APPLN_FOR_BUMI));
		getManagedBean().setZoneList(SmServiceLookup.getZoneSO().retrieveActiveZoneList().getZoneList());
		getManagedBean().setBumiputeraDataMaintainance(true);
	}
	// 28912 end
}
