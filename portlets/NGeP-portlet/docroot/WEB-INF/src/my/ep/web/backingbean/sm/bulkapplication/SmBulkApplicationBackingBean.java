package my.ep.web.backingbean.sm.bulkapplication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.pm.ReasonCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.bulkapplication.SmBulkApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmApplicationNotificationUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "bulkApplicationBackingBean")
@RequestScoped
public class SmBulkApplicationBackingBean extends
		BaseBackingBean<SmBulkApplicationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2492540872306203847L;

	/**
	 * Pre-render bulk application screen
	 */
	public void preRenderBulkApplication() {
		renewSesionObject();
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
		getManagedBean().setInitSelectedTasks(
				new ArrayList<SmBulkApplicationVO>());
		SmCommonUtil.setMDCUserId();
		getManagedBean().setMaxBulkApprovalCount(
				SmRuleHelper.getInstance().getMaxBulkApproval());
		getManagedBean().setMaxBulkApprovalMsg(
				SmCommonUtil.getMessage("lbl.sm.bulk.max.application",
						new String[] { getManagedBean()
								.getMaxBulkApprovalCount() + "" }));
		getManagedBean()
				.setReasonDescList(
						SmApplicationUtil
								.retrieveRejectReasonListByParameterCode(ReasonCategory.SUPPLIER_REJECTION_REASON
										.value()));
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/bulkApplication/bulkApplicationBasePage.xhtml", "",
				null, null);
	}

	public void approveBulkApplication() {
		if (!EntityHelper
				.isCollectionEmpty(getManagedBean().getSelectedTasks())) {
			validateOverlapRemarks();
			FacesScopes fs = new FacesScopes();
			ReviewApplicationReqDTO reqDTO = null;
			String basePortalUrl = SmCommonUtil.getBasePortalUrl();
			List<SmAppl> successSubmittedList = new ArrayList<SmAppl>();
			getManagedBean().setFailedSubmittedList(new ArrayList<SmAppl>());

			for (SmBulkApplicationVO bulkAppl : getManagedBean()
					.getSelectedTasks()) {

				try {
					// approve SME Status
					SmApplicationUtil.setSmeStatus(bulkAppl.getAppl());

					if (bulkAppl.getApprovedStatusId() != null) {
						// approve status
						PmStatus regStatus = SmCommonUtil.getInstance()
								.retrievePmStatusById(
										bulkAppl.getApprovedStatusId());
						if (regStatus != null
								&& regStatus.getPmRegStatus() != null) {
							bulkAppl.getAppl().setPmStatus(regStatus);
							bulkAppl.getAppl().setPmRegStatus(
									regStatus.getPmRegStatus());
							bulkAppl.getAppl()
									.getSmSupplier()
									.setBumi(
											regStatus.getPmRegStatus()
													.isBumiAppl());
							bulkAppl.getAppl()
									.setBumi(
											bulkAppl.getAppl().getSmSupplier()
													.isBumi());
						}

					}

					// approve categories
					SmApplicationUtil.approveApplCategories(true, false,
							bulkAppl.getAppl());

					// approval remarks
					if (!StringUtils.isBlank(bulkAppl.getApprovalRemarks())) {
						SmApplicationUtil.manageAndSaveNewSmRemarks(bulkAppl
								.getAppl().getApplId(),
								ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE,
								bulkAppl.getApprovalRemarks());
					}

					if (bulkAppl.isOverlappedCategory()
							&& StringUtils.isNotBlank(bulkAppl
									.getOverlapRemarks())) {
						SmApplicationUtil.manageAndSaveNewSmRemarks(bulkAppl
								.getAppl().getApplId(),
								ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY,
								bulkAppl.getOverlapRemarks());
						SmApplicationUtil
								.manageAndSaveNewSmRemarks(
										bulkAppl.getAppl().getApplId(),
										ParameterConst.SM_APPLN_RMRK_OVERLAOP_CATEGORY_APPROVER,
										bulkAppl.getOverlapRemarks());
					}

					reqDTO = getReviewApplicationReqDTO(fs, basePortalUrl,
							bulkAppl, "APPROVE");

					SmApplicationNotificationUtil
							.setApprovalNotificationParameters(bulkAppl
									.getAppl(), null, false, bulkAppl.getAppl()
									.getSmSupplier(), null, null, reqDTO);

					SmServiceLookup.getReviewApplicationSO()
							.submitPOorApproverReviewToBPM(reqDTO);

					successSubmittedList.add(bulkAppl.getAppl());

					refreshTaskList();

				} catch (Exception ex) {
					getManagedBean().getFailedSubmittedList().add(
							bulkAppl.getAppl());
				}

			}
			showSuccessApplicationMsg(successSubmittedList, "APPROVE");
		}
	}

	public void rejectBulkApplication() {
		RequestContext.getCurrentInstance().execute("rejectDialog.show();");
	}

	public void submitReject() {
		FacesScopes fs = new FacesScopes();
		String basePortalUrl = SmCommonUtil.getBasePortalUrl();
		ReviewApplicationReqDTO reqDTO = null;
		SmMofAccount acc = null;
		List<SmAppl> successSubmittedList = new ArrayList<SmAppl>();
		getManagedBean().setFailedSubmittedList(new ArrayList<SmAppl>());
		for (SmBulkApplicationVO bulkAppl : getManagedBean().getSelectedTasks()) {

			try {
				setApplPmStatus(
						SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER,
						bulkAppl.getAppl());

				if (!bulkAppl
						.getAppl()
						.getApplType()
						.equalsIgnoreCase(
								ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
					setSmApplRejectionReasons(bulkAppl);
				}
				if (!StringUtils.isBlank(bulkAppl.getRejectionRemarks())) {
					SmApplicationUtil.manageAndSaveNewSmRemarks(bulkAppl
							.getAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
							bulkAppl.getRejectionRemarks());
				}
				reqDTO = getReviewApplicationReqDTO(fs, basePortalUrl,
						bulkAppl, "REJECT");

				acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(bulkAppl
						.getAppl().getSmSupplier().getSupplierId(), bulkAppl
						.getAppl().getSupplierType());

				SmApplicationNotificationUtil
						.setRejectionNotificationParameters(
								bulkAppl.getAppl(),
								acc == null ? StringUtils.EMPTY : acc
										.getMofNo(), bulkAppl
										.getRejectionRemarks(), bulkAppl
										.getAppl().getSmSupplier(), reqDTO);

				SmServiceLookup.getReviewApplicationSO()
						.submitPOorApproverReviewToBPM(reqDTO);
				successSubmittedList.add(bulkAppl.getAppl());
				refreshTaskList();
			} catch (Exception ex) {
				getManagedBean().getFailedSubmittedList().add(
						bulkAppl.getAppl());
			}
		}
		RequestContext.getCurrentInstance().execute("rejectDialog.hide();");
		showSuccessApplicationMsg(successSubmittedList, "REJECT");
	}

	public void cancelReject() {
		RequestContext.getCurrentInstance().execute("rejectDialog.hide();");
	}

	public boolean failSubmitted(Long smApplId) {
		boolean retVal = false;
		SmAppl smAppl = (SmAppl) EntityHelper
				.getEntityBySinglePropertyFromList(smApplId, getManagedBean()
						.getFailedSubmittedList(), "applId");
		if (smAppl != null) {
			retVal = true;
		}
		return retVal;
	}

	/**
	 * Remove un selected admin from the selected personnel list.
	 * 
	 * @param e
	 *            - AjaxBehaviorEvent
	 * 
	 */
	public void onCheckSelectedAppl(AjaxBehaviorEvent e) {
		boolean isReachMaxBulkApproval = getManagedBean().getSelectedTasks()
				.size() > getManagedBean().getMaxBulkApprovalCount() ? true
				: false;
		if (isReachMaxBulkApproval) {
			SmBulkApplicationVO removeBulkVO = null;
			for (SmBulkApplicationVO bulkVO : getManagedBean()
					.getSelectedTasks()) {
				SmBulkApplicationVO tempBulkVO = (SmBulkApplicationVO) EntityHelper
						.getEntityBySinglePropertyFromList(bulkVO.getAppl()
								.getApplId(), getManagedBean()
								.getInitSelectedTasks(), "appl");
				if (tempBulkVO == null) {
					removeBulkVO = bulkVO;
					break;
				}
			}
			if (removeBulkVO != null) {
				getManagedBean().getSelectedTasks().remove(removeBulkVO);
			}
		}

		for (SmBulkApplicationVO bulkVO : getManagedBean()
				.getClaimTaskListDataModel()) {
			SmBulkApplicationVO tempBulkVO = (SmBulkApplicationVO) EntityHelper
					.getEntityBySinglePropertyFromList(bulkVO.getAppl()
							.getApplId(), getManagedBean().getSelectedTasks(),
							"appl");
			if (isReachMaxBulkApproval && tempBulkVO == null) {
				bulkVO.setNotAllowedToTick(true);
			} else {
				bulkVO.setNotAllowedToTick(false);
			}
		}

		getManagedBean().setInitSelectedTasks(
				getManagedBean().getSelectedTasks());
	}

	/**
	 * Ajax call to set approve or reject date for the selected category.
	 * 
	 * @param category
	 *            - SmSupplierCategory
	 */
	public void assignCategoryApproveOrRejectDate(SmSupplierCategory category) {
		category.setApprovedDate(new Date());
	}

	private void showSuccessApplicationMsg(List<SmAppl> applList,
			String decision) {
		StringBuilder sb = new StringBuilder();
		String messageKey = decision
				.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE) ? "lbl.sm.bulk.approval.success.msg"
				: "lbl.sm.bulk.rejected.success.msg";

		if (!EntityHelper.isCollectionEmpty(applList)) {
			int count = 1;
			sb.append("<table>");
			for (SmAppl smAppl : applList) {
				sb.append("<tr>").append("<td>").append(count).append(") ")
						.append("</td><td>");
				sb.append(smAppl.getApplNo()).append(" (")
						.append(smAppl.getSmSupplier().getCompanyName());
				sb.append(") </td>").append("</tr>");
			}
			sb.append("</table>");

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							SmCommonUtil.getMessage(messageKey,
									new String[] { sb.toString() })));
		}
	}

	private ReviewApplicationReqDTO getReviewApplicationReqDTO(FacesScopes fs,
			String basePortalUrl, SmBulkApplicationVO bulkVO, String decision) {
		ReviewApplicationReqDTO revReqDTO = new ReviewApplicationReqDTO();
		revReqDTO.setSmAppl(bulkVO.getAppl());
		revReqDTO.setPoOrApproverDecision(decision);
		revReqDTO.setPoLoginId(fs.getUserSessionBean().getLoginId());
		revReqDTO.setPoRole(false);
		revReqDTO.setApproverRole(true);
		revReqDTO.setApplId(bulkVO.getAppl().getApplId());
		revReqDTO.setApplNo(bulkVO.getAppl().getApplNo());
		revReqDTO.setOutcome(decision);
		revReqDTO.setSuppId(bulkVO.getAppl().getSmSupplier().getSupplierId());
		revReqDTO.setBpmTaskName(bulkVO.getTaskName());
		revReqDTO.setAppType(bulkVO.getAppl().getApplType());
		revReqDTO.setTaskId(bulkVO.getTaskId());
		revReqDTO.setLocation(bulkVO.getPmZone().getZoneName().toUpperCase());
		revReqDTO.setSupplierType(bulkVO.getAppl().getSupplierType()
				.toUpperCase());
		revReqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
		revReqDTO.setLangCode(fs.getUserLocale().getLanguage());
		revReqDTO.setUserList(new ArrayList<String>());
		revReqDTO.setServerName(fs.getRequest().getServerName());
		revReqDTO.setServerPort(fs.getRequest().getServerPort());
		revReqDTO.setPortalBaseUrl(basePortalUrl);
		revReqDTO.setOnlineDoc(bulkVO.isOnlineDoc());
		revReqDTO.setRejectionReasons(bulkVO.getReasonDesc());
		if (decision
				.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.APPROVE)) {
			revReqDTO.setPaymentReqd(SmApplicationUtil.getPaymentMode(bulkVO
					.getAppl()));
		}
		return revReqDTO;
	}

	public void onClaimBpmTask() {
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSelectedUnclaimTasks())) {

			for (SmBulkApplicationVO bulkAppl : getManagedBean()
					.getSelectedUnclaimTasks()) {
				if (!StringUtils.isBlank(bulkAppl.getTaskId())) {
					claimTaskByTaskId(bulkAppl.getTaskId());
				}
			}
			refreshTaskList();
			getManagedBean().getSelectedUnclaimTasks().clear();
		}
	}

	public void onReleaseBpmTask() {
		if (!EntityHelper
				.isCollectionEmpty(getManagedBean().getSelectedTasks())) {

			for (SmBulkApplicationVO bulkAppl : getManagedBean()
					.getSelectedTasks()) {
				if (!StringUtils.isBlank(bulkAppl.getTaskId())) {
					releaseTaskByTaskId(bulkAppl.getTaskId());
				}
			}
			refreshTaskList();
			getManagedBean().getSelectedTasks().clear();
		}
	}

	public void onViewOverlapCategory(List<SmBulkApplOverlapCatVO> overlapList) {
		getManagedBean().setSelectedOverlapCategoryVOList(overlapList);
		RequestContext.getCurrentInstance().execute(
				"overlapCategoryDialog.show();");
	}

	public void refreshTaskList() {
		this.getManagedBean().getClaimTaskListDataModel().reloadData();
		this.getManagedBean().getUnClaimTaskListDataModel().reloadData();
		this.getManagedBean().setFailedSubmittedList(null);
	}

	public String applicationStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance()
				.getStatusDescById(docInt.longValue());
		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}

	private void validateOverlapRemarks() {
		StringBuilder sb = new StringBuilder();
		for (SmBulkApplicationVO bulkAppl : getManagedBean().getSelectedTasks()) {
			if (StringUtils.isBlank(bulkAppl.getOverlapRemarks())) {
				sb.append(SmCommonUtil.getMessage(
						"lbl.sm.overlapped.category.remarks.appl.required",
						new String[] { bulkAppl.getAppl().getApplNo() }));
				sb.append("<br />");
			}
		}
		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	private void validateTotalSelectedApplication() {

	}

	/**
	 * Sets the application current pm status.
	 * 
	 * @param statusIdpm
	 *            - pm status id passed as String
	 */
	private void setApplPmStatus(String statusId, SmAppl smAppl) {
		StatusResDTO resDTO = SmApplicationUtil.setApplPmStatus(statusId);
		if (resDTO != null && resDTO.getPmStatus() != null) {
			smAppl.setPmStatus(resDTO.getPmStatus());
		}
	}

	/**
	 * Sets the SM application rejection reasons selected.
	 */
	private void setSmApplRejectionReasons(SmBulkApplicationVO bulkVO) {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		List<SmApplRejectReason> resList = new ArrayList<SmApplRejectReason>();

		for (PmReasonDesc desc : bulkVO.getSelectedReasonDescs()) {
			SmApplRejectReason rej = new SmApplRejectReason();
			rej.setPmReason(desc.getPmReason());
			rej.setSmAppl(bulkVO.getAppl());
			resList.add(rej);
			sb.append(++count).append(".)");
			sb.append(desc.getReasonDesc());
			sb.append("\n");
		}

		if (!EntityHelper.isCollectionEmpty(bulkVO.getAppl()
				.getSmApplRejectReasons())) {
			bulkVO.getAppl().getSmApplRejectReasons().clear();
		}
		bulkVO.getAppl().getSmApplRejectReasons().addAll(resList);
		bulkVO.setReasonDesc(sb.toString());
	}
}
