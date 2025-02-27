package my.ep.web.backingbean.sm.supplierdatamaintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
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
import my.ep.persistence.entity.pm.common.PmRegStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.DisciplinaryActionVO;

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

	/**
	 * Navigate to Supplier Maintenance Screen
	 * 
	 * @param SmAppl
	 *            selectedSmAppl
	 */
	public void navigateToMaintenance(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(SmServiceLookup.getMofRegistrationSO().retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		initStep0BaseInfo(getManagedBean().getSmAppl());
		initStep1CompanyInfo();
		initStep6RegisterCategoryCode();
		initStep8_9PoApproverDefaultDecision();
		initBumiRecommendNote();
		initSupplierDisciplinaryAction(getManagedBean().getSmSupplier());
		assignApplActiveAndApprovedCategoryList();
		//Redmine #18959 - no need to check overlapping category
		//setOverLappingCategories();
		getManagedBean().setOverLappedCategoryMsg("");
		getManagedBean().setOverLappedCategoryExists(false);
		
		setCategoryTypeConfirm();
		getManagedBean().setDisplaySearchPage(false);
		setLoginUserAllowedToDoMaintenance();
		checkIfSupplierHasActiveTransaction(false);
		initPOApproverOverlapCategoryRemarkInfo();
		approvalHistoryRemarklist();
		initApproverRemarkInfo();
		
		//Redmine #18959
		if(getManagedBean().isHasActiveTransaction()) {
			List<Object[]> applList = SmApplicationUtil.getActiveApplList(getManagedBean().getSmSupplier());		
			getManagedBean().setActiveApplList(applList);
		}
		getManagedBean().setExistingStatusId(getManagedBean().getSmAppl().getPmStatus().getStatusId());
		
		initPhysicalConsultingCatcode();
		getManagedBean().setExistingRegStatus(getManagedBean().getSmAppl().getPmRegStatus());
		getManagedBean().setOldRegStatusId(getManagedBean().getSmAppl().getPmRegStatus().getPmStatus().getStatusId());
		getManagedBean().setSmMofAccount(getSingleSmMofAccount());
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
		if (getManagedBean().getSmAppl().getPmRegStatus() == null) {
			getManagedBean().getSmAppl().setPmRegStatus(new PmRegStatus());
		}
		getManagedBean().setDeleteApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedApprovedCategories(new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedPendingApprovedCategories(new ArrayList<SmSupplierCategory>());
	
		this.getManagedBean().setApproverRemark(null);
		
		//#30766 & #34209 - START
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
		//#30766 & #34209 - END
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
			reqDTO.setDeletedCategories(getManagedBean()
					.getDeleteApprovedCategories());
			reqDTO.setSmMofAccount(getManagedBean().getSmMofAccount());
			reqDTO.setRemark(getManagedBean().getApplnApprovalRemarks());
			reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			reqDTO.setOverlapCategoryRemarkDocType(ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER);
			reqDTO.setRemarkId(getManagedBean().getRemarkId());
			reqDTO.setRemarkOverlappedId(getManagedBean().getRemarkOverlappedId());
			
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
			
			//#30766 & #34209 - START
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
			//#30766 & #34209 - END
			
			reqDTO.setSmTrackingDiaryVOList(listSmTrackingDiary);
			
			resDTO = SmServiceLookup.getSupplierDataMaintenanceSO()
					.updateSupplierInfo(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmMofAccount(resDTO.getSmMofAccount());

		} catch (OptimisticLockException opt) {
			throw new EpException(opt.getMessage());
		} catch (PersistenceException pers) {
			throw new EpException(pers.getMessage());
		} catch (NullPointerException nex) {
			throw new EpException(nex.getMessage());
		} catch (RuntimeException re) {
			throw new EpException(re.getMessage());
				}

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
		trckVO.setDocNo(appl.getApplNo());
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
			//#30766 & #34209 - START
			if(selectedCategories.size() >= getManagedBean().getApprovedCategoryList().size()){
				throw new EpException(MessageConst.ERROR_722708);
			} else {
				List<SmSupplierCategory> categories = selectedCategories;
				deleteCatCodes(categories);
			}
			//#30766 & #34209 - END

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

	public void onTickDiscipline() {
		if (getManagedBean().isEditDisciplinaryAction()) {
			getManagedBean().setDisciplineVO(getInitDisciplinaryActionVO());
		} else {
			initSupplierDisciplinaryAction(getManagedBean().getSmSupplier());
		}
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
						apprCat.setChangedBy(null); //#30766 & #34209 - START_END
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
				cat.setApprvrDecFlg(1);		//#30766 & #34209 - START
				cat.setPoDecFlg(null);		//#30766 & #34209 - END
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
		getManagedBean().setEditDisciplinaryAction(false);
		DisciplinaryActionVO discipline = getInitDisciplinaryActionVO();
		if (!EntityHelper
				.isCollectionEmpty(supplier.getSmDisciplinaryActions())) {
			for (SmDisciplinaryAction disc : supplier
					.getSmDisciplinaryActions()) {
				if (disc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					discipline.getSmDiscplinaryAction().setRemark(
							disc.getRemark());
					discipline.getSmDiscplinaryAction().setStartDate(
							disc.getStartDate());
					discipline.getSmDiscplinaryAction().setEndDate(
							disc.getEndDate());
					discipline.setSuspend(true);
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
					&& getManagedBean().getDisciplineVO()
							.getSmDiscplinaryAction() != null) {
				if (getManagedBean().getDisciplineVO().getSmDiscplinaryAction()
						.getStartDate() == null
						&& (getManagedBean().getDisciplineVO()
								.getSmDiscplinaryAction().getEndDate() != null || !StringUtils
								.isBlank(getManagedBean().getDisciplineVO()
										.getSmDiscplinaryAction().getRemark()))) {
					throw new EpException(
							SmCommonUtil
									.getMessage(
											"lbl.sm.is.required",
											new String[] { SmCommonUtil
													.getMessage(
															"lbl.sm.disciplinary.jktt.start.date",
															null) }));
				} else if (getManagedBean().getDisciplineVO()
						.getSmDiscplinaryAction().getEndDate() == null
						&& (getManagedBean().getDisciplineVO()
								.getSmDiscplinaryAction().getStartDate() != null || !StringUtils
								.isBlank(getManagedBean().getDisciplineVO()
										.getSmDiscplinaryAction().getRemark()))) {
					throw new EpException(
							SmCommonUtil
									.getMessage(
											"lbl.sm.is.required",
											new String[] { SmCommonUtil
													.getMessage(
															"lbl.sm.disciplinary.jktt.end.date",
															null) }));
				} else if (StringUtils
						.isBlank(getManagedBean().getDisciplineVO()
								.getSmDiscplinaryAction().getRemark())
						&& (getManagedBean().getDisciplineVO()
								.getSmDiscplinaryAction().getStartDate() != null || getManagedBean()
								.getDisciplineVO().getSmDiscplinaryAction()
								.getEndDate() != null)) {
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.is.required", new String[] { SmCommonUtil
									.getMessage(
											"lbl.sm.disciplinary.jktt.remarks",
											null) }));
				} else if (getManagedBean().getDisciplineVO()
						.getSmDiscplinaryAction().getStartDate() != null
						&& getManagedBean().getDisciplineVO()
								.getSmDiscplinaryAction().getEndDate() != null
						&& getManagedBean()
								.getDisciplineVO()
								.getSmDiscplinaryAction()
								.getStartDate()
								.compareTo(
										getManagedBean().getDisciplineVO()
												.getSmDiscplinaryAction()
												.getEndDate()) > 0) {
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
		//#30766 & #34209 - START
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
		//#30766 & #34209 - END

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
	
	//32792 START
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
}
