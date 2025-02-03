package my.ep.web.backingbean.sm.supplierdatamaintenance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.dto.sm.sme.SupplierSmeReqDTO;
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
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryActionRemarks;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
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
import my.ep.web.vo.DisciplinaryActionVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;
//#28895_35501 Start - AUDIT TRAIL SM
import my.ep.biz.service.IAuditTrailSO;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.util.CustomUtils;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementApprovedCategoryCodesBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementApproverRemarksBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementBackingBeanTemplate;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementOverlappingCategoryBackingBean;
import my.ep.web.backingbean.sm.audit.AuditSupplierManagementSupplierInformationBackingBean;
//#28895_35501 End
//#34031 Start
import java.util.Calendar;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sm.SmRevisit;
import my.ep.persistence.entity.sm.SmRvAssistOfficer;
import my.ep.persistence.entity.sm.SmRvDocFile;
import my.ep.persistence.entity.sm.SmRvSuppDoc;
import org.primefaces.event.FileUploadEvent;
import my.ep.dto.sm.revisit.SmRevisitReqDTO;
import my.ep.biz.service.sm.revisit.ISmRevisitSO;
//#34031 End

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
	private static String defaultAccPurpose = "P";
	private static String addrPermanent = "permenant";
	private static String addrBusiness = "business";
	private static String addrResidential = "residential";
	//26238 -  Disciplonary action - 28146
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
	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderBasicAccountMaintenance() {
		if(getManagedBean().isSupplierUser()){
			RegistrationBackingBean registrationBackingBean = new RegistrationBackingBean();
			registrationBackingBean.viewOnlineAccount();
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
		initSupplierDisciplinaryAction(); //27426 : Disciplinary Action
 		assignApplActiveAndApprovedCategoryList();
		//Redmine #18959 - no need to check overlapping category
		getManagedBean().setOverLappedCategoryMsg("");
		getManagedBean().setOverLappedCategoryExists(false);
		//26238 - start
		retrieveAllDisciplinaryAction();
		getOwnerAndDirectorList(); 
		getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
		getManagedBean().setSmSupp(getManagedBean().getSmSupplier());
		
		RegistrationReqDTO regReqDTO = new RegistrationReqDTO();
		regReqDTO.setSupplierId(getManagedBean().getSmSupp().getSupplierId());
		
		RegistrationResDTO regResDTO = SmServiceLookup.getRegistrationSO().retrieveSuppOnlineAcc(regReqDTO);
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
			populateStateByAddType(addrPermanent, false);
			populateDistrictByAddType(addrPermanent, false);
			populateCityByAddType(addrPermanent, false);

			getManagedBean().setSmBusinessAdd(regResDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
			populateStateByAddType(addrBusiness, false);
			populateDistrictByAddType(addrBusiness, false);
			populateCityByAddType(addrBusiness, false);
		}else{
			if(getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
					getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
				getManagedBean().setSabahSarawakType(true);
			}
			
			getManagedBean().setSmBusinessAdd(regResDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
			populateStateByAddType(addrBusiness, false);
			populateDistrictByAddType(addrBusiness, false);
			populateCityByAddType(addrBusiness, false);
		}
		retrievePmCountryList();
		
		//retrieve Supplier GST Info
		initGstInfo(regResDTO.getSmSupp(),getManagedBean(),false);
		getManagedBean().setGstFlag(false);
		
		//26238 - end
		//27857: Disciplinary Action - start - 26238
		resetDisciplinaryActionSection(); //27426 : Disciplinary Action
		resetDisciplinaryActionSectionExist();
		if(getActiveDisciplinaryAction() != null){
			initSupplierDisciplinaryAction();
		}
		//27857: Disciplinary Action - start - 26238
		//27437 : Disciplinary Action
		if(getManagedBean().getSmSupplier().getSmAppl().getApplCategoryType() != null){
			setCategoryTypeConfirm();
		}
		
		getManagedBean().setDisplaySearchPage(false);
		//28273: Disciplinary Action  - start - <26238>
		if(getManagedBean().getSmSupplier().getTypeDisciplinaryAction() != null){//26238 :Disciplinary Action - bug fix
			getManagedBean().setAllowedToDoMaintenance(false);
		}else {
			setLoginUserAllowedToDoMaintenance();
		}
		//28273: Disciplinary Action  - end - <26238>
		checkIfSupplierHasActiveTransaction(false);
		initPOApproverOverlapCategoryRemarkInfo();
		approvalHistoryRemarklist();
		initApproverRemarkInfo();
		
		//Redmine #18959 //#30730
		if(getManagedBean().isHasActiveTransaction() 
				|| (getManagedBean().getSmAppl().getPmStatus() != null && (getManagedBean().getSmAppl().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_REPORT_AFTER_APPROVED)
				|| getManagedBean().getSmAppl().getPmStatus().getStatusId().equals(SupplierManagementConst.PM_STATUS_PENDING_SITE_VISIT_AFTER_APPROVED)))) {//29943: Disciplinary Action - 26238
			List<Object[]> applList = SmApplicationUtil.getActiveApplList(getManagedBean().getSmSupplier());		
			getManagedBean().setActiveApplList(applList);
		}
		//27437 : Disciplinary Action - start
		SmAppl appl = getManagedBean().getSmAppl();
		if(appl.getPmStatus() != null
				&& appl.getPmRegStatus() != null
				&& appl.getPmRegStatus().getPmStatus() != null
				&& appl.getPmRegStatus().getPmStatus().getStatusId() != null) {
			getManagedBean().setExistingStatusId(getManagedBean().getSmAppl().getPmStatus().getStatusId());
			getManagedBean().setOldRegStatusId(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId());
		}
		//27437 : Disciplinary Action - end
		initPhysicalConsultingCatcode();
		getManagedBean().setExistingRegStatus(getManagedBean().getSmAppl().getPmRegStatus());
		getManagedBean().setSmMofAccount(getSingleSmMofAccount());
		//START 27465 : Disciplinary Action 26238
		//#32792 START
		if (getManagedBean().getSmMofAccount() != null) {
			getManagedBean().setExistingMofExpiryDate(
					getManagedBean().getSmMofAccount().getExpDate());
		}else if(getManagedBean().getSmSupplier()!= null && getManagedBean().getSmSupplier().getSmMofAccounts()!= null && getManagedBean().getSmSupplier().getSmMofAccounts().size()>0){
			getManagedBean().setSmMofAccount(getManagedBean().getSmSupplier().getSmMofAccounts().get(0));
			getManagedBean().setExistingMofExpiryDate(
					getManagedBean().getSmMofAccount().getExpDate());
		}
		//#32792 END
		//END 27465 : Disciplinary Action 
		
		//START 26238
		if (getManagedBean().getSmAppl().getPmRegStatus() == null) {
			getManagedBean().getSmAppl().setPmRegStatus(new PmRegStatus());
			getManagedBean().setCancelMofAccount(true);
		} else {
			getManagedBean().setCancelMofAccount(false);
		}
		//END 26238
		
		getManagedBean().setSelectedOwnerAndDirectorList(new ArrayList<SmPersonnel>());
		getManagedBean().setDeleteApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedPendingApprovedCategories(new ArrayList<SmSupplierCategory>());
	
		this.getManagedBean().setApproverRemark(null);
		//26238 -  Disciplonary action - 28146
		getManagedBean().setFlagBlacklistCancel(false);
		setFlagShowButtonSubmit(selectedSmAppl.getSmSupplier(), mofStatus);
		
		// #28895_35501 Start - AUDIT TRAIL SM
		this.getManagedBean().setOldSmAppl((SmAppl) CustomUtils.getInstance().deepCopy(this.getManagedBean().getSmAppl()));
		this.getManagedBean()
				.setOldApprovedCategoryList(CustomUtils.getInstance().deepCopyArrayList(this.getManagedBean().getApprovedCategoryList()));
		this.getManagedBean().setOldPoApprOverlappedRemark(this.getManagedBean().getPoApprOverlappedRemark());
		this.getManagedBean().setOldApproverRemark(this.getManagedBean().getApproverRemark());
		// #28895_35501 End
		
		//#34209 - START
			getManagedBean().setTempCategoryLs(new HashMap<Long, String>());
			for(SmSupplierCategory supCat : this.getManagedBean().getSmAppl().getSmSupplierCategories()){
				if(supCat.getRecordStatusFlg().equals(SupplierManagementConst.RECORD_STATUS_ACTIVE) && supCat.isSpecialCategory()){
					if(supCat.getOtherDesc()!=null){
						getManagedBean().getTempCategoryLs().put(supCat.getSupplierCategoryId(), supCat.getOtherDesc());
					} else {
						getManagedBean().getTempCategoryLs().put(supCat.getSupplierCategoryId(), "");
					}
					
				}
			}
		//#34209 - END
		// #34031 Start
		SmRevisit smRevisit = new SmRevisit();
		this.getManagedBean().setSmRevisit(smRevisit);
		this.getManagedBean().setSvoRoleCode(RoleCode.UPS_SVO.value());
		List<SmRvAssistOfficer> smRvAssistOfficers = new ArrayList<SmRvAssistOfficer>();
		this.getManagedBean().getSmRevisit().setSmRvAssistOfficers(smRvAssistOfficers);
		addSmRvAssistOfficer();
		initSmRvDoc();
		initSmRvHistory();
		this.getManagedBean().getSmRevisit().setRevisitPrevRemark(getPrevRevisitRemark());
		// #34031 End
	}
	
	private void setFlagShowButtonSubmit(SmSupplier supplier, String mofStatus){
		getManagedBean().setFlagShowButtonSubmit(true);
		//#28278 - Disciplonary action - #28278, #26238, #32818 -Start
		boolean hasPending = supplier.isPendingCancel() 
				|| supplier.isPendingBlacklist() || supplier.isPendingSuspend();
		hasPending |= supplier.isPendingBumiputera() || supplier.isPendingManufacture();
		if((SupplierManagementConst.MOF_STATUS_DELETED.equals(mofStatus) && !hasPending)
			|| SupplierManagementConst.MOF_STATUS_EXPIRED_SYSTEM.equals(mofStatus) 
			|| getManagedBean().isCancelMofAccount()){ // START //29336: Disciplinary Action 26238 - add condition
			getManagedBean().setFlagShowButtonSubmit(false);
		}
		//#28278 - Disciplonary action - #28278, #26238, #32818 -end	
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
		getManagedBean().searchSupplier();
	}
	//26238 : Disciplinary action - start
	/**
	 * Submit Basic Account Maintenance
	 */
	public void submitBasicAccountMaintenance(){
		validateDisciplinaryAction();
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();
		
		try {
			reqDTO.setSupplier(getManagedBean().getSmSupplier());
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			
			if(!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl().getSmSupplierBanks())){
				reqDTO.setAdditionalBankList(getManagedBean().getSmAppl().getSmSupplierBanks());
			} else {
				reqDTO.setAdditionalBankList(null);
			}
			
			if(!EntityHelper.isCollectionEmpty(getManagedBean().getOwnerAndDirectorList())){
				reqDTO.setSelectedOwnerAndDirector(getManagedBean().getOwnerAndDirectorList());
			}
			//#26238
			List<SmTrackingDairyVO> listSmTrackingDiary = new ArrayList<>();
			if (getManagedBean().isEditDisciplinaryAction()) {
				listSmTrackingDiary.add(setDisciplinaryAction(reqDTO, null));
			}
			reqDTO.setSmTrackingDiaryVOList(listSmTrackingDiary);
			//27906: Disciplinary Action - start - 
			// set portalPath
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			//27906: Disciplinary Action - end - 
			
			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateBasicAccountInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			
			setBasicSupplierPostSubmit();
			//26238 - Disciplinary Action - 28798 - end
			
		} catch (NullPointerException e) {
			SLAUtils.getInstance().logSLAError("SM", "basic", getManagedBean().getSmAppl().getApplNo(),
					"SupplierDataMaintenanceBackingBean", (new LogKey()).getUserId(), e);
		}
		getManagedBean().setDisplaySearchPage(true);
		//27437 : Disciplinary Action
		FacesContext fc = FacesContext.getCurrentInstance();
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
				.getMessage(MessageConst.INFO_721215,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale()), null);
	}
	//26238 - end - updateBasicAccountInfo
	//26238 - Disciplinary Action - 28798 - start
	private void setBasicSupplierPostSubmit(){
		if (getManagedBean().getSmSupplier() == null){
			return;
		}
		SupplierSearchReqDTO suppReqDTO = new SupplierSearchReqDTO();
		suppReqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		SmSupplier supplier = SmServiceLookup.getSupplierSearchSO().retriveSupplierById(suppReqDTO).getSmSupplier();
		//32329 - start
		if (supplier.getRecordStatusFlg().equals(9) 
				&& !EntityHelper.isCollectionEmpty(supplier.getSmDisciplinaryActions())){
			String type = "";
			for(SmDisciplinaryAction da : supplier.getSmDisciplinaryActions()){
				if (da.isCancelRegistration()){
					type = "C";
				} else if(da.isBlacklistOwner()){
					type = "B";
				} else if (da.isSuspendRegistration()){
					type = "S";
				}
				if(!type.isEmpty()){
					break;
				}
			}
			supplier.setTypeDisciplinaryAction(type);
		}
		//32329 - end
		getManagedBean().setSmSupplier(supplier);
		getManagedBean().getBasicSupplierDataModel().getList().set(
					getSupplierIndex(true), supplier);
	}
	
	private int getSupplierIndex(boolean isBasic){
		List<SmSupplier> suppliers = isBasic ? getManagedBean().getBasicSupplierDataModel().getList()
				: getManagedBean().getSmSupplierDataModel().getList();
		// get index of current Supplier from list table
		for(SmSupplier supp : suppliers){
			if(supp.getSupplierId().equals(getManagedBean().getSmSupplier().getSupplierId())){
				return suppliers.indexOf(supp);
			}
		}
		return 0;
	}
	
	//27437 : Disciplinary action - start
	/**
	 * Search basic account
	 */
	public void searchBasicAccount() {
		getManagedBean().setBasicAccountMaintenance(true);
		getManagedBean().searchBasicSupplier();
	}
	
	/**
	 * Reset search
	 */
	@Override
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
		List<SmTrackingDairyVO> listSmTrackingDiary = new ArrayList<>();
		//reset value
		getManagedBean().setHasRegStatusChanged(false);
		getManagedBean().setHasMofExpiryDateChanged(false);
		getManagedBean().setEditDisciplinaryActionCheckBox(false);
		
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
			reqDTO.setSupplier(getManagedBean().getSmSupplier());
			reqDTO.setListRegCategories(getManagedBean().getSmAppl().getSmSupplierCategories());
			reqDTO.setActiveAppl(activeAppl);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setNewlyAddedCategories(getManagedBean().getActiveCategoryList());
			reqDTO.setDeletedCategories(getManagedBean()
					.getDeleteApprovedCategories());
			reqDTO.setSmMofAccount(getManagedBean().getSmMofAccount());
			reqDTO.setRemark(getManagedBean().getApplnApprovalRemarks());
			reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			reqDTO.setOverlapCategoryRemarkDocType(ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER);
			reqDTO.setRemarkId(getManagedBean().getRemarkId());
			reqDTO.setRemarkOverlappedId(getManagedBean().getRemarkOverlappedId());	
			
			//#26238
			if (getManagedBean().isEditDisciplinaryAction()) {
				listSmTrackingDiary.add(setDisciplinaryAction(reqDTO, activeAppl)); 
			}
			
			
			if (getManagedBean().getApproverRemark() != null) {
				reqDTO.setApproverRemark(getManagedBean().getApproverRemark());
				log.info("User ID="+user.getUserId());
				reqDTO.setPmUser(user);
			}
			
			//#26340-start
			//to check if Category Description Changed
			boolean isCategoryDescriptionHasChanged =false;
			if (getManagedBean().getSmAppl().getSmSupplierCategories() != null) {
				for (SmSupplierCategory cat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
					if (cat.getOtherDesc() != null) {
						isCategoryDescriptionHasChanged = true;
						//#37605
						if(!SmApplicationUtil.convertToAscii(cat.getOtherDesc())){
							throw new EpException(MessageConst.ERROR_760555);
						}
						cat.setOtherDesc(SmApplicationUtil.replaceTabWSpace(cat.getOtherDesc()));
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
			if(getManagedBean().getExistingRegStatus().getRegStatusId().compareTo(getPmRegStatusByStatusId(
					getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId()).getRegStatusId()) != 0) {
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
				//#32614
				log.info("32614-diary: "+smTrackingDairyVO.getDocNo()+" -- "+smTrackingDairyVO.getActionedBy());
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
			
			//set overlap remark category remark
			if (isOverlapCategoryRemarkChanged) {		
				SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
						getManagedBean().getSmAppl(),
						getTrackingActionDesc(reqDTO,"OLA"));
				listSmTrackingDiary.add(smTrackingDairyVO);
			}
			reqDTO.setSmTrackingDiaryVOList(listSmTrackingDiary);
			
			//#34209 - START
			//set tracking diary for other description if changed
			for(SmSupplierCategory latestCat : getManagedBean().getSmAppl().getSmSupplierCategories()) {
				if(latestCat.getOtherDesc()==null){
					latestCat.setOtherDesc("");
				}
				if(latestCat.getRecordStatusFlg().equals(SupplierManagementConst.RECORD_STATUS_ACTIVE) 
					&& latestCat.isSpecialCategory() 
					&& this.getManagedBean().getTempCategoryLs().containsKey(latestCat.getSupplierCategoryId())
					&& !latestCat.getOtherDesc().equals(this.getManagedBean().getTempCategoryLs().get(latestCat.getSupplierCategoryId()))){
						reqDTO.setCatCodeChanged(latestCat.getPmCategoryL1().getCategoryL1Code() + latestCat.getPmCategoryL2().getCategoryL2Code() + latestCat.getPmCategoryL3().getCategoryL3Code());
						reqDTO.setOldDesc(this.getManagedBean().getTempCategoryLs().get(latestCat.getSupplierCategoryId()));
						reqDTO.setNewDesc(latestCat.getOtherDesc());
						SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(getManagedBean().getSmAppl(), getTrackingActionDesc(reqDTO,"OD"));
						listSmTrackingDiary.add(smTrackingDairyVO);
				}
			}
			//#34209 - END
			// #34031
			saveRevisitInfo(reqDTO);
			
			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateSupplierInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());
			// #28895_35501 Start - AUDIT TRAIL SM
			if (resDTO.isSuccessful()) {
				IAuditTrailSO auditSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT, IAuditTrailSO.class);
				if (auditSo.isAuditConfig(AuditSupplierManagementConst.AU_SM_CONF)) {
					AuditSupplierManagementBackingBeanTemplate auditSmBackingBean = new AuditSupplierManagementSupplierInformationBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
					auditSmBackingBean = new AuditSupplierManagementApprovedCategoryCodesBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
					auditSmBackingBean = new AuditSupplierManagementOverlappingCategoryBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
					auditSmBackingBean = new AuditSupplierManagementApproverRemarksBackingBean();
					auditSmBackingBean.saveAuditData(getManagedBean(), getUserIdSessionFromLoginId());
				}
			}
			// #28895_35501 End
			
			//Untick after save
			//#26238
			getManagedBean().setEditDisciplinaryAction(false);
			setMOFSupplierPostSubmit();

		} catch (RuntimeException e) {
			SLAUtils.getInstance().logSLAError("SM", "supplier", getManagedBean().getSmAppl().getApplNo(),
					"SupplierDataMaintenanceBackingBean", (new LogKey()).getUserId(), e);
		}
		getManagedBean().setWarningExist(false);
		getManagedBean().setDisplaySearchPage(true);
		FacesContext fc = FacesContext.getCurrentInstance();
		SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO, MessageUtils
				.getMessage(MessageConst.INFO_721215,
						MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale()), null);
	}
	
	//#26238
	private void setMOFSupplierPostSubmit(){
		if (getManagedBean().getSmSupplier() == null){
			return;
		}
		// replace list table with latest supplier
		SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
		reqDTO.setSmSupplier(getManagedBean().getSmSupplier());
		reqDTO.setSupplierType(getManagedBean().getSmSupplier().getSmAppl().getSupplierType());
		reqDTO.setDisplaySearchPage(true);
		reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());//26238: Disciplinary Action - add language - 
		SmApplRevisionResDTO resDTO = SmServiceLookup.getSmApplRevisionSO()
				.retrieveSupplierBySearchCriteria(reqDTO);
		
		if (resDTO.getSupplierResultBean() != null 
				&& !EntityHelper.isCollectionEmpty(resDTO.getSupplierResultBean().getResultList())){
			SmSupplier supplier = resDTO.getSupplierResultBean().getResultList().get(0);
			getManagedBean().setSmSupplier(supplier);
			getManagedBean().getSmSupplierDataModel().getList().set(
					getSupplierIndex(false), supplier);
		}
	}
	
	//#26238
	private SmTrackingDairyVO setDisciplinaryAction(SupplierDataMaintenanceReqDTO reqDTO, SmAppl activeAppl){
		reqDTO.setSelectedCategories(checkCatCodeSubmit());
		//28147: Disciplinary Action - start -
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getOwnerAndDirectorList())) {
			reqDTO.setSelectedOwnerAndDirector(getManagedBean().getOwnerAndDirectorList());
			
			List<SmPersonnel> selectedOwners = getManagedBean().getSelectedOwnerAndDirectorList();
			if (getManagedBean().isBlacklistOwnerOrDirector() && !EntityHelper.isCollectionEmpty(selectedOwners)) {
				reqDTO.setSelectedOwnerAndDirector(selectedOwners);
			} 
		}
		//28147: Disciplinary Action - end -
		SmDisciplinaryAction da = getManagedBean().getDisciplineVO().getSmDiscplinaryAction();
		da.setWarning(getManagedBean().isWarning());
		da.setCancelCategory(getManagedBean().isCancelCategoryCode());
		da.setBumiputeraCancel(getManagedBean().isBumiputeraStatusCancel());
		da.setManufacturerCancel(getManagedBean().isManufacturerStatusCancel());
		
		da.setSuspendRegistration(getManagedBean().isSuspendRegistration());
		da.setCancelRegistration(getManagedBean().isCancelRegistration());
		da.setBlacklistOwner(getManagedBean().isBlacklistOwnerOrDirector());
		
		reqDTO.setSmDisciplinaryAction(da);
		
		//set tracking diary for Disciplinary Action
		//Fix 26238 -08/04/2021- start - checking if active appl  != null
		SmAppl appl = activeAppl != null ? activeAppl : getManagedBean().getSmAppl();
		return assignTrackingDairyValues(appl, getTrackingActionDesc(reqDTO,"DA"));
	}
	
	private void validateUpdateDisciplinaryAction(SmDisciplinaryAction da){
		if (da.getDisciplinaryActionId() == null){
			return;
		}
		SmDisciplinaryAction oldDA = SmApplicationUtil.retrieveActiveSmDisciplinaryActionByDisciplinaryId(
				da.getDisciplinaryActionId());
		if (oldDA == null || oldDA.getStartDate() == null || oldDA.getEndDate() == null){
			return;
		}

		List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil
				.retrieveActiveSmDisciplinaryActionRemarksByDA(da);
		boolean isSameRemarks = false;
		if (!EntityHelper.isCollectionEmpty(remarksList)) {
			isSameRemarks = remarksList.get(0).getRemark().equals(da.getRemark());
		} else {
			isSameRemarks = oldDA.getRemark().equals(da.getRemark());
		}

		boolean isSameDates = oldDA.getStartDate().toString().contentEquals(da.getStartDate().toString())
				&& oldDA.getEndDate().toString().contentEquals(da.getEndDate().toString());
		boolean isSameType = getManagedBean().isWarning() == oldDA.isWarning();
		isSameType &= getManagedBean().isSuspendRegistration() == oldDA.isSuspendRegistration();
		isSameType &= getManagedBean().isCancelRegistration() == oldDA.isCancelRegistration();
		isSameType &= getManagedBean().isBlacklistOwnerOrDirector() == oldDA.isBlacklistOwner();
		isSameType &= getManagedBean().isCancelCategoryCode() == oldDA.isCancelCategory();
		isSameType &= getManagedBean().isManufacturerStatusCancel() == oldDA.isManufacturerCancel();
		isSameType &= getManagedBean().isBumiputeraStatusCancel() == oldDA.isBumiputeraCancel();
		if (isSameDates && isSameType && isSameRemarks){
			throw new EpException(MessageConst.ERROR_100045);
		}
	}
	
	//#26238 - start
	private List<SmSupplierCategory> checkCatCodeSubmit(){
		List<SmSupplierCategory> selectedCategory = new ArrayList<>();
		List<SmSupplierCategory> catcodes = getManagedBean().getSelectedApprovedCategories();
		if(EntityHelper.isCollectionEmpty(catcodes)){
			return selectedCategory;
		}
		//27446 : Disciplinary Action - start
		boolean hasMTO = false;
		for(SmSupplierCategory cat : catcodes){
			selectedCategory.add(cat);
			if(isMTOSubCatcode(cat, false)){
				hasMTO = true;
			}
		}
		
		if(hasMTO && !EntityHelper.isCollectionEmpty(getManagedBean().getApprovedCategoryList())){
			for(SmSupplierCategory regCat : getManagedBean().getApprovedCategoryList()){
				if(isMTOSubCatcode(regCat, true)){
					selectedCategory.add(regCat);
					break;
				}
			}
		}
		return selectedCategory;
		//27446 : Disciplinary Action - end
	}
	
	private boolean isMTOSubCatcode(SmSupplierCategory cat, boolean isMTO){
		boolean flag =  cat.getPmCategoryL1() != null 
				&& "22".equals(cat.getPmCategoryL1().getCategoryL1Code())
				&& cat.getPmCategoryL2() != null 
				&& "17".equals(cat.getPmCategoryL2().getCategoryL2Code());
		if(isMTO){
			return flag && "09".equals(cat.getPmCategoryL3().getCategoryL3Code());
		}
		List<String> basic = Arrays.asList("04", "05", "06");
		return flag && basic.contains(cat.getPmCategoryL3().getCategoryL3Code());
	}
	//#26238 - end

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
		//37807 - START
		if (getManagedBean().getSmAppl().getSupplierType() != null
				&& (my.ep.constant.sm.SupplierManagementConst.SUPP_TYPE_CONTRACTOR).equals(getManagedBean().getSmAppl().getSupplierType())) {
			if (getManagedBean().getSmAppl().getApplCategoryType() != null
					&& (my.ep.constant.sm.SupplierManagementConst.SM_APPL_CATEGORY_TYPE_SPECIAL).equals(getManagedBean().getSmAppl().getApplCategoryType())) {
				getManagedBean().getSmAppl().setApplCategoryType(my.ep.constant.sm.SupplierManagementConst.SM_APPL_CATEGORY_TYPE_NORMAL);
			} else {
				getManagedBean().getSmAppl().setApplCategoryType(my.ep.constant.sm.SupplierManagementConst.SM_APPL_CATEGORY_TYPE_SPECIAL);
			}
		} else {
			if (getManagedBean().getPhysicalConsulting() != null
					&& getManagedBean().getPhysicalConsulting().equals(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE)) {
				getManagedBean().setPhysicalConsulting(my.ep.constant.sm.SupplierManagementConst.NON_PHYSICAL_SERVICES_CAT_CODE);
			} else {
				getManagedBean().setPhysicalConsulting(my.ep.constant.sm.SupplierManagementConst.PHYSICAL_SERVICES_CAT_CODE);
			}
		//37807 - END
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
		//37807 - START
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean()
					.getActiveCategoryList()) {
				if (cat.getRecordStatusFlg().compareTo(
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
		catList.clear();
		if (getManagedBean().getSmAppl().getApplCategoryType().equals("N")) {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories().removeAll(catList);
		} else {
			for (SmSupplierCategory cat : getManagedBean().getSmAppl()
					.getSmSupplierCategories()) {
				if (!cat.isSpecialCategory()
						&& cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}

			}
			getManagedBean().getSmAppl().getSmSupplierCategories().removeAll(catList);
			//37807 - END
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
		//27460 : Disciplinary Action, //32827
		validateUpdateDisciplinaryAction(getManagedBean().getDisciplineVO().getSmDiscplinaryAction());
		if(getManagedBean().getDisciplineVO().getSmDiscplinaryAction().getDisciplinaryActionId() == null){
			boolean cancelCategory = getManagedBean().isCancelCategoryCode() || getManagedBean().isManufacturerStatusCancel();
			if(cancelCategory && !getManagedBean().isCancelCategoryCodeExist()
					&& EntityHelper.isCollectionEmpty(getManagedBean().getSelectedApprovedCategories())){
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.category",null));
			}
			validateCancelManufacturer();
			//#28147 - start - #28494
			if (getManagedBean().isBlacklistOwnerOrDirector() 
					&& EntityHelper.isCollectionEmpty(getManagedBean().getSelectedOwnerAndDirectorList())) {
				throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.owner", null));
			}
			//#28147 - end
		}
		getManagedBean().setShowParentMsg(false);
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
	}
	
	/**
	 * Validate DA Cancel Manufacturer if it is not combined with other DA
	 */
	private void validateCancelManufacturer(){
		if(!getManagedBean().isManufacturerStatusCancel() || getManagedBean().isBumiputeraStatusCancel() 
				&& getManagedBean().isCancelCategoryCode()){
			return;
		}
		List<SmSupplierCategory> selectedCats = getManagedBean().getSelectedApprovedCategories();
		
		if(!EntityHelper.isCollectionEmpty(selectedCats)){
			for(SmSupplierCategory cat : selectedCats){
				if(!"99".equals(cat.getPmCategoryL3().getCategoryL3Code())){
					throw new EpException(SmCommonUtil.getMessage("lbl.sm.mandatory.to.select.manufacturer.category",null));
				}
			}
		} 
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
		//32891 - Checking category code for activeappl - 26238
		boolean applValidCat = true;  
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
		boolean overLapCategory = SmApplicationUtil.validL3CategoryCodeForOverLapping(
				getManagedBean().getSelectedCategory().getPmCategoryL3()
						.getCategoryL3Id(), getManagedBean().getSmSupplier()
						.getSupplierId(), getManagedBean().getSmAppl()
						.getApplId());
		getManagedBean().getSelectedCategory().setOverLapCategory(
				overLapCategory);
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			//Redmine #18959
			
			//32891 - Checking category code for activeappl - 26238
			//Check category added from data maintenance
			if((cat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_ACTIVE 
					|| cat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
				&& samePmCategories(cat, getManagedBean().getSelectedCategory())) {
					
				throw new EpException(MessageConst.ERROR_722533);
			}
		}
				
		//32891 - Checking category code for activeappl - 26238 - start
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(getManagedBean().getSmSupplier().getSupplierId());
			
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(reqDTO).getSmAppl(); 
		
		//32891 - check if active appl is null - start - 26238
		if(activeAppl != null){
			List<SmSupplierCategory> activeApplCategory = retrieveCategoryCode(activeAppl.getApplId());
			for (SmSupplierCategory cat : activeApplCategory) {
				//Redmine #18959
				if(cat.getRecordStatusFlg()==DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
						&& samePmCategories(cat, getManagedBean().getSelectedCategory())) {
					
					throw new EpException(ResourceBundleUtils.getMessage(
							"lbl.sm.category.error.add", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				}
			}
		}
		//32891 - end
			
		if (validCat && applValidCat) {
			getManagedBean().getSmAppl().getSmSupplierCategories()
			.add(getManagedBean().getSelectedCategory());

			assignApplActiveAndApprovedCategoryList();
			setOverLappingCategories(); 
		}

		//32891 - Checking category code for activeappl - 26238 - end
	}
	
	//#26238
	private boolean samePmCategories(SmSupplierCategory cat1, SmSupplierCategory cat2){
		boolean l1 = cat1.getPmCategoryL1().getCategoryL1Id().equals(cat2.getPmCategoryL1().getCategoryL1Id());
		boolean l2 = cat1.getPmCategoryL2().getCategoryL2Id().equals(cat2.getPmCategoryL2().getCategoryL2Id());
		boolean l3 = cat1.getPmCategoryL3().getCategoryL3Id().equals(cat2.getPmCategoryL3().getCategoryL3Id());
		return l1 && l2 && l3;
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
		List<SmSupplierCategory> catList = new ArrayList<>();
		List<SmSupplierCategory> apprvList = new ArrayList<>();
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
		
		//14150
		SmApplicationUtil.sortSupplierCategory(apprvList);
		SmApplicationUtil.sortSupplierCategory(catList);
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
			//#34209 - START
			if(selectedCategories.size() >= getManagedBean().getApprovedCategoryList().size()){
				throw new EpException(MessageConst.ERROR_722708);
			} else {
				List<SmSupplierCategory> categories = selectedCategories;
				deleteCatCodes(categories);
			}
			//#34209 - END
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
	
	private SmDisciplinaryAction getActiveDisciplinaryAction(){
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions())) {
			for (SmDisciplinaryAction disc : getManagedBean().getSmSupplier().getSmDisciplinaryActions()) { 
				if (disc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					return disc;
				}
			}
		}
		return null;
	}
	
	//26238 - start
	/**
	 * On Tick Any of the Disciplinary Actions
	 */
	public void onTickPunishmentType(){
		getManagedBean().setEditDisciplinaryActionCheckBox(true);
		getManagedBean().setEditDisciplinaryAction(true);
		getManagedBean().setDisabledApprovedCategories(false);
		getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(false);
		
		if(!hasSelectDisciplinaryAction()){
			//28858: Disciplinary Action - start - 26238 add condition to check existing DA imposed or not
			if (getActiveDisciplinaryAction() == null){
				// 26238: Disciplinary Action - start -32590 - Reopen--> 
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setStartDate(null);
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setEndDate(null); 
				getManagedBean().getDisciplineVO().getSmDiscplinaryAction().setRemark(null); 
				// 26238: Disciplinary Action - end -32590 - Reopen -->
				
				getManagedBean().setEditDisciplinaryActionCheckBox(false);
				getManagedBean().setEditDisciplinaryAction(false);
				//29181: Disciplinary Action - end - 26238
			}
			//28281 - start
			getManagedBean().setSelectedApprovedCategories(new ArrayList<SmSupplierCategory>());
			for(SmSupplierCategory cat: getManagedBean().getApprovedCategoryList()){
				cat.setSelected(false);
				cat.setSelectable(true);
			}
			//28494
			getManagedBean().setSelectedOwnerAndDirectorList(new ArrayList<SmPersonnel>());
			//28281 - end
		} else {
			// 27815: Disciplinary Action - start - checking condition 
			if (getManagedBean().isCancelCategoryCode() || getManagedBean().isManufacturerStatusCancel() 
					|| getManagedBean().isBumiputeraStatusCancel()) {
				getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(true);
			}
			//28146
			if (getManagedBean().isBlacklistOwnerOrDirector()) {
				getManagedBean().setSelectedOwnerAndDirectorList(getManagedBean().getOwnerAndDirectorList());
			}
			//26238: Disciplinary Action - start - 28324 ,28779
			//28281 - start (bumi code)
			unSelectCancelBumiCategories();
			//29181: Disciplinary Action - end - 26238
		}
	}
	
	private boolean hasSelectDisciplinaryAction(){
		boolean tickDA = getManagedBean().isWarning() || getManagedBean().isCancelCategoryCode()
				|| getManagedBean().isSuspendRegistration() || getManagedBean().isCancelRegistration();
		tickDA |= getManagedBean().isBlacklistOwnerOrDirector() || getManagedBean().isBumiputeraStatusCancel()
				|| getManagedBean().isManufacturerStatusCancel();
		return tickDA;
	}
	//26238 - end

	/**
	 * On Tick Disciplinary Action
	 */
	public void onTickDiscipline() {
		//27426 & 27427 : Disciplinary Action
		boolean hasDA = !EntityHelper.isCollectionEmpty(getManagedBean().getSmSupplier().getSmDisciplinaryActions());
	    boolean isEditCheck = getManagedBean().isEditDisciplinaryActionCheckBox();

	    if (hasDA && isEditCheck) {
	        initSupplierDisciplinaryAction();
	        getManagedBean().setEditDisciplinaryAction(true);
	        getManagedBean().setEditDisciplinaryActionCheckBox(true);
	    } else if (!hasDA) {
	    	if (getManagedBean().isCancelCategoryCode()) {
	            getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(isEditCheck);
	        }
	        getManagedBean().setDisciplineVO(getInitDisciplinaryActionVO());
	    } else {
	        initSupplierDisciplinaryAction();
	        getManagedBean().setEditDisciplinaryActionCheckBox(false);
	        getManagedBean().setEditDisciplinaryAction(false);
	      //29181: Disciplinary Action - end - 26238
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
						apprCat.setChangedBy(null); //#34209 - START_END
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
				cat.setApprvrDecFlg(1);		//#34209 START
				cat.setPoDecFlg(null);		//#34209 END
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
		//26238 - start
		if (!alreadyHasMsg
				&& SmApplicationUtil.checkActiveSmApplForSuppDA(getManagedBean()
						.getSmSupplier())) {
			hasActiveAppl = true;
		}
		getManagedBean().setHasActiveTransaction(hasActiveAppl);
		//26238 - end
	}

	/**
	 * Initialized supplier disciplinary action and set to DisciplinaryActionVO
	 * 
	 * @param SmSupplier
	 *            supplier
	 */
	private void initSupplierDisciplinaryAction() {
		//27857: Disciplinary Action - disabled hardcoded value - 26238
		getManagedBean().setHasActiveDA(false); //#34626
		DisciplinaryActionVO discipline = getInitDisciplinaryActionVO();
		getManagedBean().setDisciplineVO(discipline);
		SmDisciplinaryAction disc = getActiveDisciplinaryAction();
		if (disc == null){
			return;
		}
		//27857: Disciplinary Action - start - 26238
		if(!getManagedBean().isEditDisciplinaryActionCheckBox()){ //29181: Disciplinary Action - change edit check box variable - 26238
			
			// 27815 - add disable button delete if edit DA - Start
			getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(false);
			// 27815 - add disable button delete if edit DA - End
			getManagedBean().setBlacklistOwnerOrDirector(disc.isBlacklistOwner());
			getManagedBean().setBlacklistOwnerOrDirectorExist(true);
			
			getManagedBean().setCancelCategoryCode(disc.isCancelCategory());
			getManagedBean().setCancelCategoryCodeExist(true);
			
			getManagedBean().setSuspendRegistration(disc.isSuspendRegistration());
			getManagedBean().setSuspendRegistrationExist(true);
			
			getManagedBean().setWarning(disc.isWarning());
			if(getManagedBean().isEditDisciplinaryActionCheckBox()){ 
				getManagedBean().setWarningExist(false);
			} else {
				getManagedBean().setWarningExist(true);
			}
			
			getManagedBean().setCancelRegistration(disc.isCancelRegistration());
			getManagedBean().setCancelRegistrationExist(true);
			
			getManagedBean().setBumiputeraStatusCancel(disc.isBumiputeraCancel());
			getManagedBean().setBumiputeraStatusCancelExist(true);
			
			getManagedBean().setManufacturerStatusCancel(disc.isManufacturerCancel());
			getManagedBean().setManufacturerStatusCancelExist(true);
		}else{
			// 27815 - add disable button delete if edit DA - Start
			if(disc.isCancelCategory() || disc.isBumiputeraCancel() || disc.isManufacturerCancel()){
					getManagedBean().setDisableDeleteButtonAprrovedCategoryCodePanel(true);
			}
			// 27815 - add disable button delete if edit DA - End
					
			resetDisciplinaryActionSectionExist();
			getManagedBean().setEditDisciplinaryActionCheckBox(true);//29181: Disciplinary Action - 26238
			getManagedBean().setEditDisciplinaryAction(true);
		}
		//27857: Disciplinary Action - end - 26238
				
		//28198: Disciplinary Action - start - 26238
		List<SmDisciplinaryActionRemarks> remarksList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionRemarksByDA(disc);
		String remark = EntityHelper.isCollectionEmpty(remarksList) ? disc.getRemark()
				: remarksList.get(0).getRemark();
		discipline.getSmDiscplinaryAction().setRemark(remark);
		//28198: Disciplinary Action - end - 26238
		discipline.getSmDiscplinaryAction().setStartDate(disc.getStartDate());
		discipline.getSmDiscplinaryAction().setEndDate(disc.getEndDate());
		discipline.setSuspend(true);
		discipline.getSmDiscplinaryAction().setDisciplinaryActionId(disc.getDisciplinaryActionId());
		getManagedBean().setHasActiveDA(true); //#34626
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
		reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
		reqDTO.setDocId(getManagedBean().getSmAppl().getApplId());
		
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO().retrieveSmRemarksByDocument(
				reqDTO);
		
		if(resDTO.getRemarksList() != null && resDTO.getRemarksList().size() > 0) {
			SmRemark remark = resDTO.getRemarksList().get(0);
			getManagedBean().setApproverRemark(remark.getRemark());
			getManagedBean().setRemarkId(remark.getRemarkId());
		}
	}
	
	//#26238 - start
	private DisciplinaryActionVO getInitDisciplinaryActionVO() {
		DisciplinaryActionVO vo = new DisciplinaryActionVO();
		vo.setSmDiscplinaryAction(new SmDisciplinaryAction());
		vo.getSmDiscplinaryAction().setSmSupplier(getManagedBean().getSmSupplier());
		if (getManagedBean().getSmMofAccount() != null) {
			vo.getSmDiscplinaryAction().setMofNo(getManagedBean().getSmMofAccount().getMofNo());
		}
		vo.setSuspend(false);
		return vo;
	}

	/**
	 * Validate entered disciplinary action
	 */
	private void validateDisciplinaryAction() {
		DisciplinaryActionVO vo = getManagedBean().getDisciplineVO();
		if (vo == null || vo.getSmDiscplinaryAction() == null) {
			return;
		}
		SmDisciplinaryAction da = vo.getSmDiscplinaryAction();
		Date startDate = da.getStartDate();
		Date endDate = da.getEndDate();
		String remark = da.getRemark();
		if (startDate != null || endDate != null || !StringUtils.isBlank(remark)){
			validateFieldDA(startDate, "lbl.sm.disciplinary.jktt.start.date");
			validateFieldDA(endDate, "lbl.sm.disciplinary.jktt.end.date");
			validateFieldDA(remark, "lbl.sm.disciplinary.jktt.remarks");
			if (startDate != null && startDate.compareTo(endDate) > 0) {
				throw new EpException(MessageConst.ERROR_722538);
			}
		}
		validateRemarks();
	}
	
	private void validateFieldDA(Object field, String fieldName) {
	    if (field == null || (field instanceof String && StringUtils.isBlank((String) field))) {
	        throw new EpException(SmCommonUtil.getMessage("lbl.sm.is.required",
	                new String[] { SmCommonUtil.getMessage(fieldName, null) }));
	    }
	}
	
	private void validateRemarks(){
		String remark = getManagedBean().getApproverRemark();
		if (StringUtils.isNotEmpty(remark) && remark.length() > 2500) {
			throw new EpException(MessageConst.ERROR_71025, new String[] {String.valueOf(remark.length())});
		}
	}
	//#26238 - end
	
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
		//#34209 - START
		case "OD": //Other Description
			PmUser curUser = new FacesScopes().getUserSessionBean();
			sb.append("Keterangan Lain bagi bidang ");
			sb.append(reqDTO.getCatCodeChanged());
			sb.append(" telah diubah daripada ");
			sb.append(reqDTO.getOldDesc());
			sb.append(" kepada ");
			sb.append(reqDTO.getNewDesc());
			sb.append(" oleh ").append(curUser.getUserName());
		break;
		//#34209 - END
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
		SmRemaksResDTO resDTO = SmApplicationUtil.getRemarksByDoc(
				ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE, getManagedBean().getSmAppl().getApplId());
		resDTO.getRemarksList();
		getManagedBean().setApproverRemarkList(resDTO.getRemarksList());
	}

	public String getUserNameByUserId(Long userId) {
		return PmCommonUtil.retriveUserName(userId);

	}
	
	//26238 - start
	/**
	 * View Disciplinary Action
	 */
	public void onViewDisciplinaryAction() {
		SmSupplier currSupp = getManagedBean().getSmSupplier();
		if(currSupp != null){
			List<DisciplinaryActionVO> voList = getDisciplinaryActionVO(currSupp);
			if(!EntityHelper.isCollectionEmpty(voList)){
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
	
	/**
	 * Retrieve owner and director list
	 */
	private void getOwnerAndDirectorList() {
		List<SmPersonnel> perList = new ArrayList<>();
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			boolean isOwnerDirector = per.isEquityOwner() || per.isDirector();
			if (per.getRecordStatusFlg().equals(SupplierManagementConst.APPROVED_RECORD_STATUS)
					&& isOwnerDirector && per.getIdentificationNo().length() == 12){
				perList.add(per);
			}
		}
		getManagedBean().setOwnerAndDirectorList(perList);
	}
	
	/**
	 * retrieve PM FinancialOrg List
	 * @return
	 */
	public List<PmFinancialOrg> retrieveFinancialOrgList() {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType("PB");
		FinancialOrgResDTO resDTO = SmServiceLookup.getFinancialOrgSO()
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
		List<String> bankList = new ArrayList<>();
		for(SmSupplierBank suppBank : banks){
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
		String lbl;
		if(bizType != null){
			if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
				lbl =  "lbl.sm.director.name";
			}else if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2)){
				lbl = "lbl.sm.chairman.name";
			}else{
				lbl = "lbl.sm.ownername";
			}
		}else{
			lbl = "lbl.sm.ownername";
		}
		return lbl;
	}
	
	/**
	 * retrieve identity type from PM
	 * @return SelectItem[]
	 */
	public SelectItem[] pmIdTypeDropdownValues() {
		List<SelectItem> itemList = new ArrayList<>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		for(SelectItem selectItem : items) {
			
			if(selectItem.getValue() == null){
				itemList.add(selectItem);
			}else{
				PmParameter pmParameter = (PmParameter) identityType.get(selectItem.getValue());
				if(!("A").equals(pmParameter.getParameterCode())){
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
	 * @param reset
	 */
	public void populateStateByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals(addrBusiness)) {
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
		} else if (typeAdd.equals(addrResidential)) {
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		} else if (typeAdd.equals(addrPermanent)) {
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
	 * @param reset
	 */
	public void populateDistrictByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals(addrBusiness)) {
			PmState state = SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
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
			//33071 - start	
				if(state !=null && state.getAdoptDivision()) {
					getManagedBean().setDivisionBox(false);	
				}
				else{	
					getManagedBean().setDivisionBox(true);//13320-End
				}
			}else{
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
				this.getManagedBean().setBusinessDistrictLs(divisionLs);
				this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
			}
			//33071 - end
		} else if (typeAdd.equals(addrResidential)) {
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
		} else if (typeAdd.equals(addrPermanent)) {
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
	 * @param reset
	 */
	public void populateCityByAddType(String typeAdd, boolean reset) {
		log.info("---------------City By Type--------------------");
		if (typeAdd.equals(addrBusiness)) {
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			}
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(SmCommonUtil.getInstance().getDistrictById(getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId()));
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setBusinessCityLs(cityLs);
			populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		} else if (typeAdd.equals(addrResidential)) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmResAdd().getPmDistrict().getDistrictId());
			getManagedBean().setResidentialCityLs(cityLs);
		} else if (typeAdd.equals(addrPermanent)) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId());
			getManagedBean().setPermenantCityLs(cityLs);
		}
	}
	
	/**
	 * @param typeAdd
	 */
	public void onSelectCity(String typeAdd){
		if (typeAdd.equals(addrBusiness)) {
			getManagedBean().getSmBusinessAdd().setPmCity(SmCommonUtil.getInstance().getCityById(getManagedBean().getSmBusinessAdd().getPmCity().getCityId()));
		}
	}
	
	/**
	 * set state drop down value
	 */
	public void pmStateDropdownValues() {
		Long countryId = this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId();
		List<PmState> stateLs = retrievePmStateList(countryId);
		List<SelectItem> stateSelectLs = new ArrayList<>();
		String stateName = "stateName";
		String stateId = "stateId";
		String bussType = getManagedBean().getSmSupp().getBusinessType();
		
		if(this.getManagedBean().getBusinessStateLs() == null){
			this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
		}
		
		if(bussType != null && !("").equals(bussType)){
			
			if(SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE.contains(bussType)){

				PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
				this.getManagedBean().setSabahSarawakType(true);
				this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
				
				if(this.getManagedBean().getSmBusinessAdd().isBrandNew()){
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
					this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				}
				
				if(bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
						bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
					stateSelectLs = setStateDivision(SupplierManagementConst.SM_STATE_NAME_SARAWAK);
					
				}else if(bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
						bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
					stateSelectLs = setStateDivision(SupplierManagementConst.SM_STATE_NAME_SABAH);
				}
				
				if(getManagedBean().getSmBusinessAdd().isBrandNew()){
					getManagedBean().setBusinessCityLs(null);
					getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
					getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
					getManagedBean().getSmSupp().setLocalAuthorityId(null);
				}
			}else if(bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) ||
					bussType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)){
				
				List<PmState> semenanjungLs = new ArrayList<>();
				
				for(PmState smjState : stateLs){
					if(!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SABAH_CODE) && 
							!smjState.getStateCode().equalsIgnoreCase(SupplierManagementConst.SARAWAK_CODE)){
						semenanjungLs.add(smjState);
					}
				}
				
				stateSelectLs  = EntityHelper.convertListToSelectItem(semenanjungLs, stateName, stateId);
				
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
			}
		}
		
		if(stateSelectLs.isEmpty()){
			stateSelectLs = EntityHelper.convertListToSelectItem(stateLs, stateName, stateId);
		}
		this.getManagedBean().setBusinessStateLs(stateSelectLs);
		populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		
	}
	
	private List<SelectItem> setStateDivision(String stateName){
		List<PmState> stateLs = SmCommonUtil.getInstance().retrieveStateByZone(stateName, 
				SupplierManagementConst.COUNTRY_MALAYSIA_CODE);	
		if(!EntityHelper.isCollectionEmpty(stateLs)){
			List<SelectItem> stateSelectLs = EntityHelper.convertListToSelectItem(stateLs, "stateName", "stateId");
			PmState state = stateLs.get(0);
			this.getManagedBean().getSmBusinessAdd().setPmState(state);
			this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
			return stateSelectLs;
		}
		return new ArrayList<>();
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
			StateResDTO resDTO = SmServiceLookup.getStateSO().retriveStatesListByCountryID(reqDTO);
			return resDTO.getStateList();
		}

		return null;
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
	
	/**
	 * Retrieve disciplinary actions
	 */
	public void retrieveAllDisciplinaryAction(){
		SmSupplier supplier = getManagedBean().getSmSupplier();
		getManagedBean().setWarningExist(false); //27426 : Disciplinary Action
		if(supplier == null){
			return;
		}
			//27724 : Disciplinary Action - start
		List<SmDisciplinaryAction> discList = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(supplier);
		if(!EntityHelper.isCollectionEmpty(discList)){
			for(SmDisciplinaryAction disc : discList){
				if(disc.isWarning()){
					getManagedBean().setWarningExist(true);
				}
			}
		}
	}
	//26238 - end
	
	//27426 : Disciplinary Action - start
	/**
	 * Reset disciplinary action section
	 */
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
	/**
	 * Reset disciplinary action section exist
	 */
	public void resetDisciplinaryActionSectionExist(){
		getManagedBean().setWarningExist(false);
		getManagedBean().setSuspendRegistrationExist(false);
		getManagedBean().setCancelRegistrationExist(false);
		getManagedBean().setBlacklistOwnerOrDirectorExist(false);
		getManagedBean().setBumiputeraStatusCancelExist(false);
		getManagedBean().setCancelCategoryCodeExist(false);
		getManagedBean().setManufacturerStatusCancelExist(false);
	}
	//27857: Disciplinary Action - end - 26238
	
	//26238 : Disciplinary Action - start 28111
	/**
	 * Download disciplinary action history
	 * @param supplier
	 * @throws Exception
	 */
	public void downloadDAHistory(SmSupplier supplier) throws IOException {
		SmSupplier supp = supplier != null ? supplier : getManagedBean().getSmSupplier();
		if(supp == null){
			supp = SmApplicationUtil.getCurrentSupplier();
		}
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("PSUPPLIERID", supp.getSupplierId().toString());
		if(supp.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)){
			generateReport(ReportConst.SM_DISCIPLINARY_ACTION_HISTORY_BASIC, paramMap);
		} else {
			generateReport(ReportConst.SM_DISCIPLINARY_ACTION_HISTORY, paramMap);
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
	
	//13320-Start
	public void populateDivisionByAddType(String typeAdd, boolean reset) {
		log.info("---------------Division By Type--------------------");
			if(typeAdd.equals(addrBusiness)){
			List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmBusinessAdd().getPmDivision().getDivisionId());
			this.getManagedBean().setBusinessDistrictLs(divisionLs);
			this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());	
		}
			if(typeAdd.equals(addrResidential)){
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmResAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setResidentialDistrictLs(divisionLs);
				this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());	
			}
	       if(typeAdd.equals(addrPermanent)){
				List<SelectItem> divisionLs = SmCommonUtil.getDistrictListByID(this.getManagedBean().getSmPermenantAdd().getPmDivision().getDivisionId());
				this.getManagedBean().setPermenantDistrictLs(divisionLs);
				this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());	
			}
	}
	//13320-end
	
	
	//32618 - Start
	//#26238
	/**
	 * Removed selected cancel bumi categories from the list.
	 *  Mandatory auto-tick 100% bumi, disable non-bumi, enable 51% bumi for selection
	 */
	public void unSelectCancelBumiCategories() {
		if (!getManagedBean().getSmSupplier().isBumi() || !getManagedBean().isBumiputeraStatusCancel()
				|| getManagedBean().getSelectedPendingApprovedCategories() == null){
			return;
		}
		List<SmSupplierCategory> selectList = new ArrayList<>();
		List<SmSupplierCategory> prevSelect = getManagedBean().getSelectedApprovedCategories();
		Iterator<SmSupplierCategory> it = getManagedBean().getApprovedCategoryList().iterator();
		while (it.hasNext()) {
			SmSupplierCategory cat = it.next();
			int bumiPct = cat.getPmCategoryL3().getBumiPercentage().intValue();
			if((bumiPct > 50 && prevSelect.contains(cat))
						|| bumiPct == 100){
				selectList.add(cat);
			} 
			cat.setSelectable(bumiPct > 50 && bumiPct < 100);
		}
		//32760 - Start
		getManagedBean().setSelectedApprovedCategories(selectList);
	}
	//32618 - End
	
	//32891 - Checking category code for activeappl - 26238 - start
	/**
	 * retrieve CategoryCode
	 * @param applId - application id
	 * @return List<SmSupplierCategory>
	 */
	public List<SmSupplierCategory> retrieveCategoryCode(Long applId){
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setApplId(applId);
		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().
				retrieveCategoryCodeByApplId(reqDTO);
		return resDTO.getSmSupplierCategoryList();
	}
	//32891 - Checking category code for activeappl - 26238 - end
	//32792 - START
	/**
	 * @param List<SmMofAccount> mofList
	 * @return String 
	 */
	public String getLatestMOFNo(List<SmMofAccount> mofList){
		// sort by latest created date 
		Collections.sort(mofList,new Comparator<SmMofAccount>() {
			@Override
			public int compare(SmMofAccount mof1, SmMofAccount mof2) {
				return Integer.valueOf(mof2.getCreatedDate().compareTo(mof1.getCreatedDate()));
				
			}
		});
		return mofList.get(0).getMofNo();
	}
	//32792 - END
	// #34031 Start
	public void addSmRvAssistOfficer() {
		SmRvAssistOfficer smRvAssistOfficer = new SmRvAssistOfficer();
		smRvAssistOfficer.setSmRevisit(this.getManagedBean().getSmRevisit());
		this.getManagedBean().getSmRevisit().getSmRvAssistOfficers().add(smRvAssistOfficer);
		this.getManagedBean().setDisableAddSmRvAssistOfficerBtn(true);
	}

	public void enableAddSmRvAssistOfficerBtn() {
		this.getManagedBean().setDisableAddSmRvAssistOfficerBtn(false);
		for (SmRvAssistOfficer officer : this.getManagedBean().getSmRevisit().getSmRvAssistOfficers()) {
			if (officer.getOfficerName() == null || officer.getOfficerName().isEmpty()) {
				this.getManagedBean().setDisableAddSmRvAssistOfficerBtn(true);
				break;
			}
		}
	}

	public void onDeleteRvAssistOfficer(SmRvAssistOfficer selectedRvAssistOfficer) {
		this.getManagedBean().setSelectedRvAssistOfficer(selectedRvAssistOfficer);
		RequestContext.getCurrentInstance().execute("rvAssistOfficerDeletePopUpWV.show()");
	}

	public void removeSelectedSmRvAssistOfficer() {
		if (this.getManagedBean().getSelectedRvAssistOfficer() != null) {
			this.getManagedBean().getSmRevisit().getSmRvAssistOfficers().remove(this.getManagedBean().getSelectedRvAssistOfficer());
		}
		this.getManagedBean().setSelectedRvAssistOfficer(null);
		if (this.getManagedBean().getSmRevisit().getSmRvAssistOfficers().isEmpty()) {
			addSmRvAssistOfficer();
		}
	}

	private void initSmRvDoc() {
		List<PmDocument> suppDocList = SmServiceLookup.getDocumentSO().retrieveSmeSuppDocuments(new DocumentReqDTO()).getDocumentList();
		if (suppDocList != null && !suppDocList.isEmpty()) {
			this.getManagedBean().getSmRevisit().setSmRvSuppDocs(new ArrayList<SmRvSuppDoc>());
			for (PmDocument doc : suppDocList) {
				SmRvSuppDoc suppDoc = new SmRvSuppDoc();
				suppDoc.setPmDocument(doc);
				suppDoc.setSmRevisit(this.getManagedBean().getSmRevisit());
				this.getManagedBean().getSmRevisit().getSmRvSuppDocs().add(suppDoc);
			}
		}
	}

	public void assignSelectedSupportDocIndex(SmRvSuppDoc doc, int index) {
		this.getManagedBean().setSmRvAttachedFileIndex(index);
		this.getManagedBean().setSmRvSuppDoc(doc);
	}

	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (this.getManagedBean().getSmRevisit().getSmRvSuppDocs().contains(this.getManagedBean().getSmRvSuppDoc())) {
				String filePath = Calendar.getInstance().get(Calendar.YEAR) + "/SM/REVISIT/" + getManagedBean().getSmAppl().getApplNo() + "/";
				SmRvSuppDoc doc = this.getManagedBean().getSmRvSuppDoc();
				SmRvDocFile docFile = new SmRvDocFile();
				docFile.setFileName(StringUtils.replace(event.getFile().getFileName(), " ", "-"));
				docFile.setSmRvSuppDoc(doc);
				docFile.setFilePath(filePath);
				if (doc.getSmRvDocFiles() == null) {
					doc.setSmRvDocFiles(new ArrayList<SmRvDocFile>());
				}
				doc.getSmRvDocFiles().add(docFile);
				for (int i = 0; i < doc.getSmRvDocFiles().size(); i++) {
					if (doc.getSmRvDocFiles().get(i).getFileName() == null || doc.getSmRvDocFiles().get(i).getFileName().equalsIgnoreCase(" ")) {
						doc.getSmRvDocFiles().remove(i);
					}
				}
				doc.getSmRvDocFiles().get(0).setFilePath(filePath);
				this.getManagedBean()
						.getSmRevisit()
						.getSmRvSuppDocs()
						.set(this.getManagedBean().getSmRevisit().getSmRvSuppDocs().indexOf(this.getManagedBean().getSmRvSuppDoc()),
								this.getManagedBean().getSmRvSuppDoc());
				commonUploadFile(event, filePath);
			}
		}
	}

	public void onDeleteSupDocFileAttachment(SmRvSuppDoc doc, int index, SmRvDocFile file) {
		this.getManagedBean().setSmRvSuppDoc(doc);
		this.getManagedBean().setSmRvAttachedFileIndex(index);
		this.getManagedBean().setSmRvDocFile(file);
		RequestContext.getCurrentInstance().execute("confirmDelRvSuppDocDialogWV.show();");
	}

	public void delRvSuppDocFileAttachment() {
		SmRvSuppDoc suppDoc = this.getManagedBean().getSmRvSuppDoc();
		if (this.getManagedBean().getSmRvDocFile() != null) {
			suppDoc.getSmRvDocFiles().remove(this.getManagedBean().getSmRvDocFile());
		}
		this.getManagedBean().setSmRvDocFile(null);
	}

	private void saveRevisitInfo(SupplierDataMaintenanceReqDTO reqDTO) {
		if (this.getManagedBean().getSmRevisit().getRevisitDate() != null) {
			this.getManagedBean().getSmRevisit().setSmSupplier(this.getManagedBean().getSmSupplier());
			this.getManagedBean().getSmRevisit().setSmAppl(this.getManagedBean().getSmAppl());
			this.getManagedBean().getSmRevisit().setCurrent(true);
			removeEmptyAssistOfficer();
			reqDTO.setSmRevisit(this.getManagedBean().getSmRevisit());
		}
	}

	private void removeEmptyAssistOfficer() {
		List<SmRvAssistOfficer> emptySmRvAssistOfficers = new ArrayList<SmRvAssistOfficer>();
		this.getManagedBean().setEmptyRvAssistOfficers(emptySmRvAssistOfficers);
		for (SmRvAssistOfficer officer : this.getManagedBean().getSmRevisit().getSmRvAssistOfficers()) {
			if (officer.getOfficerName().isEmpty()) {
				this.getManagedBean().getEmptyRvAssistOfficers().add(officer);
			}
		}
		for (SmRvAssistOfficer emptyOfficer : this.getManagedBean().getEmptyRvAssistOfficers()) {
			this.getManagedBean().getSmRevisit().getSmRvAssistOfficers().remove(emptyOfficer);
		}
	}
	
	private void initSmRvHistory() {
		SmRevisitReqDTO reqDTO = new SmRevisitReqDTO();
		reqDTO.setSmSupplier(this.getManagedBean().getSmSupplier());
		ISmRevisitSO smRevisitSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmRevisitSO.class);
		List<SmRevisit> revisitList = smRevisitSo.retrieveSmRevisitListBySupplierId(reqDTO).getSmRevisitList();
		this.getManagedBean().setSmRevisitList(revisitList);
	}

	private String getPrevRevisitRemark() {
		String remark = null;
		if(!this.getManagedBean().getSmRevisitList().isEmpty()){
			for(SmRevisit revisit : this.getManagedBean().getSmRevisitList()){
				if(revisit.isCurrent()){
					remark = revisit.getRevisitRemark();
				}
			}
		}
		return remark;
	}
	// #34031 End
}
