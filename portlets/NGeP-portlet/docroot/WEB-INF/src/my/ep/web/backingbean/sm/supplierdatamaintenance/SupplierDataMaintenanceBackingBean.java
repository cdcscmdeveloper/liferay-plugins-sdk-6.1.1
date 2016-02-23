package my.ep.web.backingbean.sm.supplierdatamaintenance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
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
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderSupplierDataMaintenance() {
		initDataForSearchSupplier();
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"", null);
	}

	/**
	 * Pre-render and redirect to Search Page
	 */
	public void preRenderCancelMofBySupplier() {
		initDataForSearchSupplier();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/cancelRegWithMof/searchSupplierCancelMofPage.xhtml",
				"", null);
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
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		setApplication(getManagedBean().getSmAppl());
		initSupplierDisciplinaryAction(getManagedBean().getSmSupplier());
		assignApplActiveAndApprovedCategoryList();
		setOverLappingCategories();
		setCategoryTypeConfirm();
		getManagedBean().setDisplaySearchPage(false);
		setLoginUserAllowedToDoMaintenance();
		checkIfSupplierHasActiveTransaction(true);
		getManagedBean().setExistingRegStatus(
				getManagedBean().getSmAppl().getPmRegStatus());
		if (getManagedBean().getSmMofAccount() != null) {
			getManagedBean().setExistingMofExpiryDate(
					getManagedBean().getSmMofAccount().getExpDate());
		}
		getManagedBean().setDeleteApprovedCategories(
				new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedApprovedCategories(
				new ArrayList<SmSupplierCategory>());
		getManagedBean().setSelectedPendingApprovedCategories(
				new ArrayList<SmSupplierCategory>());
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
						"", null);
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	/**
	 * Submit supplier maintenance
	 */
	public void submitSupplierDataMaintenance() {
		validateDisciplinaryAction();
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();

		// Check if got active application for this supplier
		SmAppl activeAppl = SmCommonUtil
				.getActiveApplBySupplier(getManagedBean().getSmSupplier());

		try {

			// set registration status and expiry date
			reqDTO.setOldRegStatus(getManagedBean().getExistingRegStatus());
			reqDTO.setOldMofExpiryDate(getManagedBean()
					.getExistingMofExpiryDate());
			setSupplierRegistrationAndBumiStatusChanged();

			// set active status for newly added category
			setActiveStatusForNewAddedCategories();

			reqDTO.setActiveAppl(activeAppl);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setNewlyAddedCategories(getManagedBean()
					.getActiveCategoryList());
			reqDTO.setDeletedCategories(getManagedBean()
					.getDeleteApprovedCategories());
			reqDTO.setSmMofAccount(getManagedBean().getSmMofAccount());
			reqDTO.setRemark(getManagedBean().getApplnApprovalRemarks());
			reqDTO.setDocType(ParameterConst.SM_DATA_MAINTENANCE_RMRK_DECISION_APPROVE);
			reqDTO.setPortalBaseUrl(SmCommonUtil.getEpBaseUrl());
			SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
					getManagedBean().getSmAppl(),
					SupplierManagementConst.APPROVER_SUBMIT_SUPPLIER_DATA_MAINTENANCE);
			reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);

			if (getManagedBean().isEditDisciplinaryAction())
				reqDTO.setSmDisciplinaryAction(getManagedBean()
						.getDisciplineVO().getSmDiscplinaryAction());

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
		} finally {
			reqDTO = null;
			getManagedBean().setDisplaySearchPage(true);
			FacesContext fc = FacesContext.getCurrentInstance();
			SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_INFO,
					MessageUtils.getMessage(MessageConst.INFO_721215,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);
		}
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
				"", null);
	}

	/**
	 * Ajax call to set the application category selected.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent object
	 */
	public void applicationCategorySelected(AjaxBehaviorEvent e) {
		getManagedBean().getSmAppl().setCurrentActiveTab(
				getManagedBean().getTabActiveIndex());
		if (getManagedBean().isPoUser() || getManagedBean().isApproverUser()) {
			SmApplicationUtil.assignMaxReviewCount(getManagedBean().getSmAppl()
					.getApplType(), getManagedBean().getSmAppl()
					.getApplCategoryType(), getManagedBean().getSmAppl()
					.getSupplierType(), getManagedBean().getBusinessTypeVal(),
					getManagedBean().isReviewSiteVisit());
		}

		// added to custom confirm dialog title and msg
		// based on category type Special or Normal
		setRegCatCodeConfirmDialog();
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
									.getPmCategoryL3().getCategoryL3Id())
					&& cat.getRecordStatusFlg().compareTo(
							SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				validCat = false;
				throw new EpException(MessageConst.ERROR_722533);
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
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getManagedBean().getRoleCode());
		trckVO.setDocId(appl.getApplId());
		trckVO.setDocNo(appl.getApplNo());
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
	 * Sets the registered category code confirm dialog text.
	 * 
	 * @param managedBean
	 *            - SmApplicationManagedBean object
	 */
	private void setRegCatCodeConfirmDialog() {
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
	 * Delete selected category code(s) that are still pending approval from the
	 * SmSupplierCategory list of the application.
	 */
	public void deleteSelectedPendingApprovalCategoryCodes(
			List<SmSupplierCategory> selectedCategories) {
		if (selectedCategories != null && selectedCategories.size() > 0) {
			List<SmSupplierCategory> categories = selectedCategories;
			deleteCatCodes(categories);

			// API FOR LIST OF OVERLAPPING CATEGORIES
			setOverLappingCategories();

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
			SmSupplierCategory delCat = (SmSupplierCategory) EntityHelper
					.getEntityBySinglePropertyFromList(
							apprCat.getSupplierCategoryId(), categories,
							"supplierCategoryId");
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
		assignApplActiveAndApprovedCategoryList();
	}

	/**
	 * Set newly added categories record status to active
	 */
	private void setActiveStatusForNewAddedCategories() {
		for (SmSupplierCategory cat : getManagedBean().getSmAppl()
				.getSmSupplierCategories()) {
			if (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS) {
				cat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
			}
		}
	}

	/**
	 * Set supplier registration status and bumi status
	 */
	private void setSupplierRegistrationAndBumiStatusChanged() {
		if (getManagedBean().getSmAppl().getPmRegStatus() != null
				&& getManagedBean()
						.getExistingRegStatus()
						.getRegStatusId()
						.compareTo(
								getManagedBean().getSmAppl().getPmRegStatus()
										.getRegStatusId()) != 0) {
			Long pmStatId = getManagedBean().getSmAppl().getPmStatus()
					.getStatusId();
			for (PmStatusDesc desc : getManagedBean().getRegStatusDescList()) {
				if (pmStatId.equals(desc.getPmStatus().getStatusId())) {
					getManagedBean().getSmAppl()
							.setPmStatus(desc.getPmStatus());
					if (desc.getPmStatus().getPmRegStatus() != null) {
						getManagedBean().getSmAppl().setPmRegStatus(
								desc.getPmStatus().getPmRegStatus());
						getManagedBean()
								.getSmAppl()
								.getSmSupplier()
								.setBumi(
										desc.getPmStatus().getPmRegStatus()
												.isBumiAppl());
						break;
					}
				}
			}
		}
	}

	/**
	 * Initialized data for search supplier
	 */
	private void initDataForSearchSupplier() {
		renewSesionObject();
		initLogin();
		getManagedBean().setDisplaySearchPage(true);
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setRegStatusDescList(retrieveRegistrationStatusList());
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
	}

	/**
	 * Check if the login user is the approver of the selected application
	 * 
	 * @return
	 */
	private boolean isLoginUserTheApproverForAppl() {
		boolean retVal = false;
		PmUser user = new FacesScopes().getUserSessionBean();
		if (!EntityHelper.isCollectionEmpty(getManagedBean().getSmAppl()
				.getSmApplSectionReviews())) {
			// if review size equals to 1 means direct approve by PO
			if (getManagedBean().getSmAppl().getSmApplSectionReviews().size() == 1) {
				getManagedBean().setApplApprover(
						SmCommonUtil.getUserByUserId(getManagedBean()
								.getSmAppl().getSmApplSectionReviews().get(0)
								.getChangedBy()));
				getManagedBean().setApprovedDate(
						DateUtils.dateToString("dd-MMM-yyyy", getManagedBean()
								.getSmAppl().getSmApplSectionReviews().get(0)
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
						getManagedBean()
								.setApplApprover(
										SmCommonUtil.getUserByUserId(rev
												.getChangedBy()));
						getManagedBean().setApprovedDate(
								DateUtils.dateToString(
										"dd-MMM-yyyy",
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
		return retVal;
	}

	/**
	 * Set whether the login user is allowed to do data maintenance
	 */
	private void setLoginUserAllowedToDoMaintenance() {
		getManagedBean().setAllowedToDoMaintenance(false);
		if (isLoginUserTheApproverForAppl()) {
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
		FacesContext fc = FacesContext.getCurrentInstance();
		if (isCancelMof) {
			if (SmApplicationUtil.hasActiveTransactions(getManagedBean()
					.getSmSupplier())) {
				getManagedBean().setAllowedToDoMaintenance(false);
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
			getManagedBean().setAllowedToDoMaintenance(false);
			SmCommonUtil.showFacesMsg(fc, FacesMessage.SEVERITY_ERROR,
					MessageUtils.getMessage(MessageConst.ERROR_722608,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()), null);
		}
	}

	/**
	 * Initialized supplier disciplinary action and set to DisciplinaryActionVO
	 * 
	 * @param SmSupplier
	 *            supplier
	 */
	private void initSupplierDisciplinaryAction(SmSupplier supplier) {
		getManagedBean().setEditDisciplinaryAction(false);
		DisciplinaryActionVO discipline = new DisciplinaryActionVO();
		discipline.setSmDiscplinaryAction(new SmDisciplinaryAction());
		discipline.getSmDiscplinaryAction().setSmSupplier(
				getManagedBean().getSmSupplier());
		if (getManagedBean().getSmMofAccount() != null) {
			discipline.getSmDiscplinaryAction().setMofNo(
					getManagedBean().getSmMofAccount().getMofNo());
		}
		discipline.setSuspend(false);
		if (!EntityHelper
				.isCollectionEmpty(supplier.getSmDisciplinaryActions())) {
			Collections.sort(supplier.getSmDisciplinaryActions(),
					new Comparator<SmDisciplinaryAction>() {
						public int compare(SmDisciplinaryAction o1,
								SmDisciplinaryAction o2) {
							if (o1.getEndDate() == null
									|| o2.getEndDate() == null)
								return 0;
							return o1.getEndDate().compareTo(o2.getEndDate());
						}
					});
			discipline.setSmDiscplinaryAction(supplier
					.getSmDisciplinaryActions().get(0));

			// already finished suspend, need to insert new record
			if (discipline.getSmDiscplinaryAction().getEndDate()
					.compareTo(new Date()) <= 0) {
				discipline.setSuspend(false);
				discipline.setSmDiscplinaryAction(new SmDisciplinaryAction());
				discipline.getSmDiscplinaryAction().setSmSupplier(
						getManagedBean().getSmSupplier());
				if (getManagedBean().getSmMofAccount() != null) {
					discipline.getSmDiscplinaryAction().setMofNo(
							getManagedBean().getSmMofAccount().getMofNo());
				}
			}
			// haven't finished suspend or not yet started suspend, just update
			// record
			else {
				discipline.setSuspend(true);
			}
		}
		getManagedBean().setDisciplineVO(discipline);

	}

	/**
	 * Validate entered disciplinary action
	 */
	private void validateDisciplinaryAction() {
		if (getManagedBean().isEditDisciplinaryAction()) {
			if (getManagedBean().getDisciplineVO() != null
					&& getManagedBean().getDisciplineVO()
							.getSmDiscplinaryAction() != null) {
				if (getManagedBean()
						.getDisciplineVO()
						.getSmDiscplinaryAction()
						.getStartDate()
						.compareTo(
								getManagedBean().getDisciplineVO()
										.getSmDiscplinaryAction().getEndDate()) > 0) {
					throw new EpException(MessageConst.ERROR_722538);
				}
			}
		}
	}
}
