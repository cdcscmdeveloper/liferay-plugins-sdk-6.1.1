package my.ep.web.backingbean.sm.supplierdatamaintenance;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.SmTrackingDairyVO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "supplierDataMaintenanceBackingBean")
@RequestScoped
public class SupplierDataMaintenanceBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void preRenderSupplierDataMaintenance() {
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
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"");
	}

	public void navigateToMaintenance(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
		setApplication(getManagedBean().getSmAppl());
		assignApplActiveAndApprovedCategoryList();
		setOverLappingCategories();
		setCategoryTypeConfirm();
		getManagedBean().setDisplaySearchPage(false);
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	/**
	 * Submit supplier maintenance
	 */
	public void submit() {
		SupplierDataMaintenanceReqDTO reqDTO = new SupplierDataMaintenanceReqDTO();
		SupplierDataMaintenanceResDTO resDTO = new SupplierDataMaintenanceResDTO();

		// Check if got active application for this supplier
		MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
		mofRegistrationReqDTO.setSupplierId(getManagedBean().getSmSupplier()
				.getSupplierId());
		SmAppl activeAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveActiveSmApplBySupp(mofRegistrationReqDTO).getSmAppl();

		try {

			// set registration status
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

			// set newly added category code
			if (getManagedBean().getActiveCategoryList() != null
					&& !getManagedBean().getActiveCategoryList().isEmpty()) {
				for (SmSupplierCategory apprCat : getManagedBean()
						.getActiveCategoryList()) {
					apprCat.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					apprCat.setApprovedDate(new Date());
					if (getManagedBean().isApproverUser()) {
						apprCat.setApprvrDecFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					} else {
						apprCat.setPoDecFlg(DbValueConst.RECORD_STATUS_ACTIVE);
					}
				}
			}

			reqDTO.setActiveAppl(activeAppl);
			reqDTO.setSmAppl(getManagedBean().getSmAppl());
			reqDTO.setNewlyAddedCategories(getManagedBean()
					.getActiveCategoryList());
			reqDTO.setSmMofAccount(getManagedBean().getSmMofAccount());
			SmTrackingDairyVO smTrackingDairyVO = assignTrackingDairyValues(
					getManagedBean().getSmAppl(),
					SupplierManagementConst.SUPPLIER_MAINTENANCE_REGISTRATION_STATUS);
			reqDTO.setSmTrackingDairyVO(smTrackingDairyVO);

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
		} finally {
			reqDTO = null;
		}

		SmCommonUtil.redirectToPage(
				SmApplicationUtil.getApplicationErrorPage(),
				MessageConst.INFO_721215);
	}

	/**
	 * Back to search screen
	 */
	public void backToSearchSupplierMaintenance() {
		getManagedBean().setDisplaySearchPage(true);
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
		StringBuffer sb = new StringBuffer();
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
	 * Removed un select approved categories from the list.
	 * 
	 * @param e
	 *            -AjaxBehaviorEvent
	 */
	public void unSelectApporvedCategories(AjaxBehaviorEvent e) {
		if (getManagedBean().getDeleteNewApplCategoryList() != null) {
			Iterator<SmSupplierCategory> iterator = getManagedBean()
					.getDeleteNewApplCategoryList().iterator();
			while (iterator.hasNext()) {
				SmSupplierCategory cat = iterator.next();
				if ((cat.getRecordStatusFlg() != null && cat
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
	 * Call confirmation dialog
	 */
	public void showConfirmDialog() {
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
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
									.getPmCategoryL3().getCategoryL3Id())) {
				validCat = false;
				if (cat.getRecordStatusFlg().compareTo(
						SupplierManagementConst.DELETED_RECORD_STATUS) == 0) {
					throw new EpException(MessageConst.ERROR_722563);
				}
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
	 * Set overlapped categories values.Checks if Same owner exist in different
	 * supplier and has same one or more similar categories
	 */
	private void setOverLappingCategories() {
		getManagedBean().setOverLappedCategoryMsg("");
		getManagedBean().setOverLappedCategoryExists(false);
		int i = 0;
		StringBuffer sb = new StringBuffer();
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
	public void deleteSelectedPendingApprovalCategoryCodes() {
		if (getManagedBean().getDeleteNewApplCategoryList() != null
				&& getManagedBean().getDeleteNewApplCategoryList().size() > 0) {
			List<SmSupplierCategory> categories = getManagedBean()
					.getDeleteNewApplCategoryList();
			deleteCatCodes(categories);

			// API FOR LIST OF OVERLAPPING CATEGORIES
			setOverLappingCategories();

		} else {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { "Category Code" });
		}
	}

	/**
	 * Delete category codes.
	 * 
	 * @param categories
	 *            - List of categories
	 */
	private void deleteCatCodes(List<SmSupplierCategory> categories) {
		for (SmSupplierCategory category : categories) {
			if (category.getRecordStatusFlg().compareTo(
					SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
				getManagedBean().getSmAppl().getSmSupplierCategories()
						.remove(category);
			} else if (category.getRecordStatusFlg().compareTo(
					SupplierManagementConst.APPROVED_RECORD_STATUS) == 0) {
				category.setRecordStatusFlg(SupplierManagementConst.RECORD_STATUS_DELETED);
			}
		}
		assignApplActiveAndApprovedCategoryList();
	}

}
