package my.ep.web.backingbean.sm.bulkapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.BpmConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.smRemarks.SmRemarksReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.helper.Paginator;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.pm.ReasonCategory;
import my.ep.util.ResourceBundleUtils;
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
import my.ep.web.vo.SmOverlapCategoryVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "bulkApplicationBackingBean")
@RequestScoped
public class SmBulkApplicationBackingBean extends
		BaseBackingBean<SmBulkApplicationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2492540872306203847L;
	public static Logger log = Logger
			.getLogger(SmBulkApplicationBackingBean.class);

	/**
	 * Pre-render bulk application screen
	 */
	public void preRenderBulkApplication() {
		renewSesionObject();
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
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
		refreshTaskList();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/bulkApplication/bulkApplicationBasePage.xhtml", "",
				null, null);
	}

	public void approveBulkApplication() {
		validateTotalSelectedApplication();
		validateApproveCategories();

		// disable validate overlap remarks make it as optional only. (#24160)
		// validateOverlapRemarks();
		RequestContext.getCurrentInstance().execute(
				"approveConfirmPopup.show();");
	}

	public void submitApprove() {
		FacesScopes fs = new FacesScopes();
		ReviewApplicationReqDTO reqDTO = null;
		String basePortalUrl = SmCommonUtil.getBasePortalUrl();
		List<SmAppl> successSubmittedList = new ArrayList<SmAppl>();
		getManagedBean().setFailedSubmittedList(new ArrayList<SmAppl>());

		for (SmBulkApplicationVO bulkAppl : getManagedBean().getSelectedTasks()) {

			try {
				SmApplicationUtil.setApplicationApproveStatus(
						bulkAppl.getAppl(), bulkAppl.getApprovedStatusId());

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
						&& !StringUtils.isBlank(bulkAppl.getOverlapRemarks())) {
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

				//10908
				SmApplicationNotificationUtil
						.setApprovalNotificationParameters(bulkAppl.getAppl(),
								null, false,
								bulkAppl.getAppl().getSmSupplier(), null, null,
								reqDTO);

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

	public void rejectBulkApplication() {
		setSelectedRejectedResonsToVO();
		//#21937 - start
		if(getManagedBean().getApplStatus().equals(getManagedBean().getRECOMMEND_APPROVE())){
			RequestContext.getCurrentInstance().execute("rejectDialog.show();");
		} else {
		// #15629 -- start - 20/1/2020
		//		RequestContext.getCurrentInstance().execute("rejectDialog.show();");
			submitReject();
		}
		// #15629 -- end - 20/1/2020, #21937 - end
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
				//21937 - start
				if(bulkAppl.getSelectedReasonDescs() == null
						|| bulkAppl.getSelectedReasonDescs().size() == 0){
					throw new EpException(
							SmCommonUtil.getMessage("lbl.sm.mandatory.rejection.remark", null));
				}
				//21937 - end
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
				//30974 - start
				if (!StringUtils.isBlank(bulkAppl.getRejectionRemarks())) {
					SmApplicationUtil.manageAndSaveNewSmRemarks(bulkAppl
							.getAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
							bulkAppl.getRejectionRemarks());
				}else{
					bulkAppl.setRejectionRemarks("N/A");
					SmApplicationUtil.manageAndSaveNewSmRemarks(bulkAppl
							.getAppl().getApplId(),
							ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
							bulkAppl.getRejectionRemarks());
				}
				cloneBulkPoApplSectionReview(bulkAppl);
				//30974 - end
				reqDTO = getReviewApplicationReqDTO(fs, basePortalUrl,
						bulkAppl, "REJECT");

				acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(bulkAppl
						.getAppl().getSmSupplier().getSupplierId(), bulkAppl
						.getAppl().getSupplierType());

				//10908
				SmApplicationNotificationUtil
						.setRejectionNotificationParameters(
								bulkAppl.getAppl(), bulkAppl.getAppl().getSmSupplier(),
								null, null, null, reqDTO);

				SmServiceLookup.getReviewApplicationSO()
						.submitPOorApproverReviewToBPM(reqDTO);
				successSubmittedList.add(bulkAppl.getAppl());
				refreshTaskList();
			} catch (Exception ex) {
				getManagedBean().getFailedSubmittedList().add(
						bulkAppl.getAppl());
				throw new EpException(ex.getMessage()); //21937
			}
		}
		// #15629 -- start - 20/1/2020
		//		RequestContext.getCurrentInstance().execute("rejectDialog.hide();");
		// #15629 -- end - 20/1/2020
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
		revReqDTO.setChangedBy(getUserIdSessionFromLoginId()); // #31849 add log key
		if (bulkVO.getAppl().getCreatedBy() != null) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(bulkVO.getAppl().getCreatedBy());
			PmUser user = SmServiceLookup.getUserSO()
					.retrieveUserByUserId(userReqDTO).getPmUser();
			if (user == null || StringUtils.isBlank(user.getLoginId())) {
				throw new EpException(
						"Internal service error: No user to send to BPM!");
			} else {
				revReqDTO.setUserList(new ArrayList<String>());
				revReqDTO.getUserList().add(user.getLoginId());
			}
		} else {
			throw new EpException(
					"Internal service error: No user to send to BPM!");
		}
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

	public void onViewOverlapCategory(SmBulkApplicationVO vo) {
		//#21937 - start
		//if (EntityHelper.isCollectionEmpty(getManagedBean()
		//		.getSelectedOverlapCategoryVOList())) {
			List<String> overlap = Arrays.asList(
					ParameterConst.NEW_SM_APPLN_FOR_RENEW,
					ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES,
					ParameterConst.NEW_SM_APPLN_FOR_MOF);
			if (vo.getAppl() != null
					&& overlap.contains(vo.getAppl().getApplType())
					&& EntityHelper.isCollectionEmpty(vo
							.getOverlapCategoryVOList())) {
				SmOverlapCategoryVO overlapCategoryVO = SmApplicationUtil
						.getOverlapCategoryVO(vo.getAppl());
				if (overlapCategoryVO != null) {
					vo.setOverlappedCategory(overlapCategoryVO
							.isOverLappedCategoryExist());
					vo.setOverlapMsg(overlapCategoryVO
							.getOverLappedCategoryMsg());
					vo.setOverlapCategoryVOList(overlapCategoryVO
							.getOverlapCategoryVOList());
				}
			}
			getManagedBean().setSelectedOverlapCategoryVOList(
					vo.getOverlapCategoryVOList());
		//}
		//#21937 - end
		RequestContext.getCurrentInstance().execute(
				"overlapCategoryDialog.show();");
	}

	public void refreshTaskList() {
		getManagedBean().setClaimTasks(new ArrayList<SmBulkApplicationVO>());
		getManagedBean().setUnClaimTasks(new ArrayList<SmBulkApplicationVO>());
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
		getManagedBean().setSelectedUnclaimTasks(
				new ArrayList<SmBulkApplicationVO>());
		if (!StringUtils.isBlank(getManagedBean().getApplStatus())) {
			initTasks(BpmConstants.ASSIGNMENT_MY, getManagedBean()
					.getClaimTasks());
			initTasks(BpmConstants.ASSIGNMENT_GROUP, getManagedBean()
					.getUnClaimTasks());

			// sort claim task by received date
			Collections.sort(getManagedBean().getClaimTasks(),
					new Comparator<SmBulkApplicationVO>() {
						public int compare(SmBulkApplicationVO o1,
								SmBulkApplicationVO o2) {
							if (o1.getReceivedDate() == null
									|| o2.getReceivedDate() == null)
								return 0;
							return o1.getReceivedDate().compareTo(
									o2.getReceivedDate());
						}
					});

			// sort not claim task by received date
			Collections.sort(getManagedBean().getUnClaimTasks(),
					new Comparator<SmBulkApplicationVO>() {
						public int compare(SmBulkApplicationVO o1,
								SmBulkApplicationVO o2) {
							if (o1.getReceivedDate() == null
									|| o2.getReceivedDate() == null)
								return 0;
							return o1.getReceivedDate().compareTo(
									o2.getReceivedDate());
						}
					});
		}
		this.getManagedBean().setFailedSubmittedList(null);
	}

	public String applicationStatus(Integer docInt) {
		PmStatusDesc pmStatusDesc = StatusHelper.getInstance()
				.getStatusDescById(docInt.longValue());
		return pmStatusDesc != null ? pmStatusDesc.getStatusName() : "Review";
	}

	/**
	 * On tick checkbox during search rejection reason
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void onTickOrUnTickReason(AjaxBehaviorEvent e) {
		for (SmBulkApplicationVO vo : getManagedBean().getSelectedTasks()) {
			verifySelectedRejectReasons(vo);
		}
	}

	/**
	 * Search rejected reason
	 */
	public void searchRejectedReason(SmBulkApplicationVO vo) {

		// reset selected reject reason during search
		//		#15629 -- start - 19/1/2020
		if(EntityHelper.isCollectionEmpty(vo.getSelectedReasonDescs())){
			vo.setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
		} 
		//		#15629 -- end - 19/1/2020

		// reset selected reject reason remarks during search
		verifySelectedRejectReasons(vo);

		vo.setReasonDescList(SmApplicationUtil.getWildCardSearchRejectReason(vo
				.getSearchRejectReason(), getManagedBean().getReasonDescList()));
	}

	/**
	 * Reset search rejected reason
	 */
	public void resetSearchRejectedReason(SmBulkApplicationVO vo) {
		vo.setSearchRejectReason(StringUtils.EMPTY);
		vo.setReasonDescList(SmApplicationUtil
				.retrieveRejectReasonListByParameterCode(ReasonCategory.SUPPLIER_REJECTION_REASON
						.value()));
		vo.setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
	}

	private void validateOverlapRemarks() {
		StringBuilder sb = new StringBuilder();
		for (SmBulkApplicationVO bulkAppl : getManagedBean().getSelectedTasks()) {
			if (bulkAppl.isOverlappedCategory()
					&& StringUtils.isBlank(bulkAppl.getOverlapRemarks())) {
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
		if (getManagedBean().getSelectedTasks().size() > getManagedBean()
				.getMaxBulkApprovalCount()) {
			throw new EpException(getManagedBean().getMaxBulkApprovalMsg());
		}
	}

	private void validateApproveCategories() {
		StringBuilder sbNoAction = new StringBuilder();
		StringBuilder sbNoRemarks = new StringBuilder();
		StringBuilder sbNoApprove = new StringBuilder();
		StringBuilder sbFullMessage = new StringBuilder();
		//Sonarqube #G22 - start
		StringBuilder catNoAction;
		StringBuilder catNoRemarks;
		int countNoAction = 0;
		int countNoRemarks = 0;
		boolean catNoApprove = true; //21937

		sbNoAction.append("<table>");
		sbNoRemarks.append("<table>");
		sbNoApprove.append("<table>");

		for (SmBulkApplicationVO vo : getManagedBean().getSelectedTasks()) {
			catNoAction = new StringBuilder();
			catNoRemarks = new StringBuilder();

			for (SmSupplierCategory cat : vo.getPendingApproveCategories()) {
				if (cat.getApprvrDecFlg() == null) {
					catNoAction.append(cat.getPmCategoryL1().getCategoryL1Code()
							+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
							+ ",");
				}
				if (cat.getApprvrDecFlg() == 0 && StringUtils.isBlank(cat.getApproverRemarks())) {
					catNoRemarks.append(cat.getPmCategoryL1().getCategoryL1Code()
							+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
							+ ",");
				}
				//21937 - start
				if (cat.getApprvrDecFlg() != 0) {
					catNoApprove = false;
				} //21937 - end
			}
			//21937
			if(vo.getPendingApproveCategories() == null || vo.getPendingApproveCategories().isEmpty())
				catNoApprove = false;
			
			//21937 - start
			if(vo.getAppl().getApplType().equalsIgnoreCase(ParameterConst.NEW_SM_APPLN_FOR_RENEW)){
				for (SmSupplierCategory cat : vo.getApprovedCategories()){
					if (cat.getApprvrDecFlg() == 0 && StringUtils.isBlank(cat.getApproverRemarks())) {
						catNoRemarks.append(cat.getPmCategoryL1().getCategoryL1Code()
								+ cat.getPmCategoryL2().getCategoryL2Code() + cat.getPmCategoryL3().getCategoryL3Code()
								+ ",");
					}
				}
			}
			//21937 - end

			if (!("").equals(catNoAction.toString())) {
				sbNoAction.append("<tr>").append("<td>").append(++countNoAction).append(") ").append("</td><td>");
				sbNoAction.append(vo.getAppl().getApplNo()).append(" : ")
						.append(catNoAction.substring(0, catNoAction.length() - 1));
				sbNoAction.append("</td>").append("</tr>");
			}

			if (!("").equals(catNoRemarks.toString())) {
				//Sonarqube #G22 - end
				sbNoRemarks.append("<tr>").append("<td>").append(++countNoRemarks).append(") ").append("</td><td>");
				sbNoRemarks.append(vo.getAppl().getApplNo()).append(" : ")
						.append(catNoRemarks.substring(0, catNoRemarks.length() - 1));
				sbNoRemarks.append("</td>").append("</tr>");
			}

			if (catNoApprove) {
				sbNoApprove.append("<tr>").append("<td>").append(++countNoAction).append(") ").append("</td><td>");
				sbNoApprove.append(vo.getAppl().getApplNo());
				sbNoApprove.append("</td>").append("</tr>");
			}
		}

		sbNoAction.append("</table>");
		sbNoRemarks.append("</table>");
		sbNoApprove.append("</table>");

		if (!sbNoAction.toString().equals("<table></table>")) {
			sbFullMessage.append(SmCommonUtil.getMessage("lbl.sm.bulk.approval.failed.noActionCat.msg",
					new String[] { sbNoAction.toString() }) + "<br />");
		}
		//21937 - start
		if (!sbNoRemarks.toString().equals("<table></table>")) {
			sbFullMessage.append(SmCommonUtil.getMessage("lbl.sm.bulk.rejected.failed.noRemarks.msg",
					new String[] { sbNoRemarks.toString() }) + "<br />");			
		}

		if (!sbNoApprove.toString().equals("<table></table>")) {
			sbFullMessage.append(SmCommonUtil.getMessage("lbl.sm.bulk.approval.failed.noApprovedCat.msg",
					new String[] { sbNoApprove.toString() }) + "<br />");
		} //21937 - end

		if (sbFullMessage.length() != 0) {
			throw new EpException(sbFullMessage.toString());
		}
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
			rej.setReasonByPo(true);

			resList.add(rej);
			log.info("Reason Id === " + desc.getPmReason().getReasonId());
			/*
			 * sb.append(++count).append(".)"); sb.append(desc.getReasonDesc());
			 * sb.append("\n");
			 */
			sb.append(desc.getPmReason().getReasonId());
			sb.append("|");
		}
		
		for (PmReasonDesc desc : bulkVO.getSelectedReasonDescs()) {
			SmApplRejectReason rej = new SmApplRejectReason();
			rej.setPmReason(desc.getPmReason());
			rej.setSmAppl(bulkVO.getAppl());
			rej.setReasonByPo(false);

			resList.add(rej);
			log.info("Reason Id === " + desc.getPmReason().getReasonId());
			/*
			 * sb.append(++count).append(".)"); sb.append(desc.getReasonDesc());
			 * sb.append("\n");
			 */
			sb.append(desc.getPmReason().getReasonId());
			sb.append("|");
		}
		log.info("bulk - setSmApplRejectionReasons>>>>>" + sb.toString());

		if (!EntityHelper.isCollectionEmpty(bulkVO.getAppl()
				.getSmApplRejectReasons())) {
			bulkVO.getAppl().getSmApplRejectReasons().clear();
		}
		
		bulkVO.getAppl().getSmApplRejectReasons().addAll(resList);
		bulkVO.setReasonDesc(sb.toString());
	}

	private void setSelectedRejectedResonsToVO() {
		for (SmBulkApplicationVO vo : getManagedBean().getSelectedTasks()) {
			vo.setSelectedReasonDescs(new ArrayList<PmReasonDesc>());
			//			#15629 -- start - 19/1/2020
			if(!EntityHelper.isCollectionEmpty(vo.getAppl().getSmApplRejectReasons())) {
				
				for(SmApplRejectReason smApplRejectReason : vo.getAppl().getSmApplRejectReasons()){
						for(PmReasonDesc pmReasonDesc : getManagedBean().getReasonDescList()){
							if(smApplRejectReason.getPmReason().getReasonId().equals(pmReasonDesc.getPmReason().getReasonId())){
								vo.getSelectedReasonDescs().add(pmReasonDesc);
							}
						}			
				}
			}
			//			#15629 -- end - 19/1/2020
			vo.setReasonDescList(getManagedBean().getReasonDescList());
		}
	}

	/**
	 * Verify selected rejection reason
	 */
	private void verifySelectedRejectReasons(SmBulkApplicationVO vo) {
		vo.setRejectReasonReqd(isDpaOthersSelected(vo.getReasonDescList(),
				vo.isRejectReasonReqd()));
		if (!vo.isRejectReasonReqd()) {
			vo.setRejectionRemarks(StringUtils.EMPTY);
		}
	}

	/**
	 * Return true or false whether there is Others selected in Rejection reason
	 * 
	 * @return boolean
	 */
	private boolean isDpaOthersSelected(List<PmReasonDesc> reasonDescList,
			boolean isRejectReasonReqd) {
		if (!EntityHelper.isCollectionEmpty(reasonDescList)) {
			for (PmReasonDesc reasonDesc : reasonDescList) {
				if (reasonDesc.getPmReason() != null
						&& reasonDesc.getPmReason().getReasonId() != null) {
					ReasonReqDTO reqDTO = new ReasonReqDTO();
					reqDTO.setReasonId(reasonDesc.getPmReason().getReasonId());
					PmReason reason = SmServiceLookup.getReasonSO()
							.retrieveReasonById(reqDTO).getPmReason();
					if (reason != null
							&& !StringUtils.isBlank(reason.getReasonCode())
							&& reason.getReasonCode().equals(
									SupplierManagementConst.DPA_REASON_OTHERS)) {
						isRejectReasonReqd = true;
						break;
					}
				}
			}
		}
		return isRejectReasonReqd;
	}

	private void initTasks(String assignmentType,
			List<SmBulkApplicationVO> taskList) {
		FacesScopes face = new FacesScopes();
		Object token = face.getBPMAuthenticationCtxToken();
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setAssignment(assignmentType);
		reqDTO.setState("ASSIGNED");
		reqDTO.setCtxToken(token);
		reqDTO.setDocType("SR");
		// #36838 - Bug
		reqDTO.setStatus(Integer.valueOf(SupplierManagementConst.PM_STATUS_PENDING_APPLICATION_APPROVAL));
		if (assignmentType.equals(BpmConstants.ASSIGNMENT_GROUP)) {
			reqDTO.setIsClaimed(false);
		}

		BpmResponseDTO responseDTO = SmServiceLookup.getBpmSO()
				.getWorklistItems(reqDTO);

		if (responseDTO.getWorklistVO() != null) {
			if (responseDTO.getWorklistVO().getWorklistItem() != null) {
				SmBulkApplicationVO bulkAppl = null;
				for (WorklistItemVO worklistItemVO : responseDTO
						.getWorklistVO().getWorklistItem()) {
					bulkAppl = getBulkApplicationVO(worklistItemVO);
					if (bulkAppl != null) {
						taskList.add(bulkAppl);
					}
				}
			}
		}
	}

	private SmBulkApplicationVO getBulkApplicationVO(
			WorklistItemVO worklistItemVO) {
		SmBulkApplicationVO bulkApplicationVO = null;
		if (worklistItemVO.getDocType() != null
				&& worklistItemVO.getDocType().equalsIgnoreCase("SR")) {
			FacesScopes face = new FacesScopes();
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setApplId(worklistItemVO.getDocId().longValue());
			SmAppl appl = SmServiceLookup.getMofRegistrationSO()
					.retrieveSmApplicationDetailsById(mofReqDTO).getSmAppl();
			SmApplSectionReview poRev = (SmApplSectionReview) EntityHelper
					.getEntityBySinglePropertyFromList(
							SupplierManagementConst.SECTION_REV_ROLE_PO,
							appl.getSmApplSectionReviews(), "roleType");

			String poRecommend = StringUtils.EMPTY;

			if (appl != null && poRev != null
					&& !StringUtils.isBlank(poRev.getRecommendation())) {
				String companyName = appl.getSmSupplier().getCompanyName();
				StringBuilder sb = new StringBuilder();
				sb.append(ResourceBundleUtils.getMessage(
						worklistItemVO.getTitle(),
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						face.getUserLocale()));
				if (companyName != null) {
					sb.append(" ( ");
					sb.append(companyName);
					sb.append(" ) ");
				}

				worklistItemVO.setTitle(sb.toString());
				bulkApplicationVO = new SmBulkApplicationVO();
				bulkApplicationVO.setTaskId(worklistItemVO.getTaskId());
				bulkApplicationVO.setTaskName(worklistItemVO.getTaskName());
				bulkApplicationVO.setCreateDate(worklistItemVO.getCreateDate());
				bulkApplicationVO.setExpirationDate(worklistItemVO
						.getExpirationDate());
				bulkApplicationVO.setTitle(worklistItemVO.getTitle());
				bulkApplicationVO.setAppl(appl);
				bulkApplicationVO.setPo(SmCommonUtil.getUserByUserId(poRev
						.getChangedBy()));
				bulkApplicationVO.setReceivedDate(poRev.getChangedDate());
			    //#15629 - Start
				bulkApplicationVO.setRejectReasons(StringUtils.isNotBlank(getApplicationRejectionReason(appl)) ?
						getApplicationRejectionReason(appl) : "N/A");
			    //#15629 - End
				bulkApplicationVO.setPoRemarks(StringUtils.isNotBlank(poRev.getRemarkToApprover()) ? 
						poRev.getRemarkToApprover() : "N/A");
				bulkApplicationVO.setCurrentRegStatus("N/A"); //21937

				if (poRev.getRecommendation().equalsIgnoreCase("Approve")
						|| poRev.getRecommendation().equalsIgnoreCase("Lulus")) {
					poRecommend = "APPROVE";
					bulkApplicationVO.setPoRecommendation(SmCommonUtil
							.getMessage("lbl.sm.bulk.approve", null));
				} else {
					poRecommend = "REJECT";
					bulkApplicationVO.setPoRecommendation(SmCommonUtil
							.getMessage("lbl.sm.bulk.reject", null));
				}

				if (poRev.getPmRegStatus() != null
						&& poRev.getPmRegStatus().getPmStatus() != null) {
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(poRev.getPmRegStatus()
							.getPmStatus().getStatusId());
					statusReqDTO.setLanguageCode(new FacesScopes()
							.getUserLocale().getLanguage());
					PmStatusDesc statusDesc = SmServiceLookup.getStatusSO()
							.retrieveStatusDescById(statusReqDTO)
							.getPmStatusDesc();
					if (statusDesc != null) {
						bulkApplicationVO.setPoRecommendStatus(statusDesc
								.getStatusName());
						bulkApplicationVO.setApprovedStatusId(statusDesc
								.getPmStatus().getStatusId());
					}
				}
				
				if (appl.getPmRegStatus() != null
						&& appl.getPmRegStatus().getPmStatus() != null) {
					StatusReqDTO statusReqDTO = new StatusReqDTO();
					statusReqDTO.setStatusId(appl.getPmRegStatus()
							.getPmStatus().getStatusId());
					statusReqDTO.setLanguageCode(new FacesScopes()
							.getUserLocale().getLanguage());
					PmStatusDesc statusDesc = SmServiceLookup.getStatusSO()
							.retrieveStatusDescById(statusReqDTO)
							.getPmStatusDesc();
					if (statusDesc != null) {
						bulkApplicationVO.setCurrentRegStatus(statusDesc
								.getStatusName());
						bulkApplicationVO.setCurrentRegStatusId(statusDesc
								.getPmStatus().getStatusId());
					}
				//21937 - start
				}else{
					// init current registration status description
					PmStatusDesc currentStatusDesc = SmApplicationUtil
							.getCurrRegStatusDesc(appl);
					if (currentStatusDesc != null
							&& !appl.getApplType()
									.equals(ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
						bulkApplicationVO.setCurrentRegStatus(currentStatusDesc
								.getStatusName());
						bulkApplicationVO.setCurrentRegStatusId(currentStatusDesc
								.getPmStatus().getStatusId());
					}
					
				}
				//21937 - END
				bulkApplicationVO.setRegStatusDescList(SmApplicationUtil
						.getInstance().retrieveRegistrationStatusList(
								appl.getSupplierType(), appl.getApplType()));
				bulkApplicationVO.setHqFacility(SmApplicationUtil
						.getHqFacilityByAppl(appl));
				PmStatusDesc currentStatusDesc = SmApplicationUtil
						.getCurrRegStatusDesc(appl);
				if (currentStatusDesc == null) {
					currentStatusDesc = SmApplicationUtil
							.getDefaultRegStatusDesc(bulkApplicationVO
									.getHqFacility(), appl.getSmSupplier()
									.isBumi());
				}
				if (bulkApplicationVO.getApprovedStatusId() == null) {
					bulkApplicationVO.setApprovedStatusId(currentStatusDesc
							.getPmStatus().getStatusId());
				}
				bulkApplicationVO.setCurrRegStatus(currentStatusDesc
						.getStatusName());

				// set overlap category alert
				List<String> overlap = Arrays.asList(
						ParameterConst.NEW_SM_APPLN_FOR_RENEW,
						ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES,
						ParameterConst.NEW_SM_APPLN_FOR_MOF);

				if (appl != null && overlap.contains(appl.getApplType())) {
					//#21937 - start					
					SmOverlapCategoryVO overlapCategoryVO = SmApplicationUtil.getOverlapCategoryVO
									(bulkApplicationVO.getAppl());
					if (overlapCategoryVO != null) {
						bulkApplicationVO.setOverlappedCategory(overlapCategoryVO
										.isOverLappedCategoryExist());
						bulkApplicationVO.setOverlapMsg(overlapCategoryVO
										.getOverLappedCategoryMsg());
						bulkApplicationVO.setOverlapCategoryVOList(overlapCategoryVO
										.getOverlapCategoryVOList());
					}
					for (SmSupplierCategory cat : appl
							.getSmSupplierCategories()) {
						if (getManagedBean().getApplStatus().equals(
								getManagedBean().getRECOMMEND_REJECT())) {
							cat.setApprvrDecFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
						}
						
						if(bulkApplicationVO.isOverlappedCategory() &&
							(cat.getRecordStatusFlg().intValue() == SupplierManagementConst.APPROVED_RECORD_STATUS
								|| cat.getRecordStatusFlg().intValue() == SupplierManagementConst.PENDING_ACT_STATUS
								|| cat.getRecordStatusFlg().intValue() == SupplierManagementConst.PENDING_RE_APPROVAL_STATUS)){
							String catcode = cat.getPmCategoryL1().getCategoryL1Code()
									+ cat.getPmCategoryL2().getCategoryL2Code()
									+ cat.getPmCategoryL3().getCategoryL3Code();
							for(SmBulkApplOverlapCatVO bulkVO : bulkApplicationVO.getOverlapCategoryVOList()){
								if(bulkVO.getCategoryCode().equals(catcode)){
									cat.setOverLapCategory(true);
									break;
								}
							}
						} 
						//#21937 - end
					}
				}

				getManagedBean().setSelectedOverlapCategoryVOList(
						bulkApplicationVO.getOverlapCategoryVOList());

				PmState state = appl.getSmCompanyBasic()
						.getSmCompanyAddresses().get(0).getSmAddress()
						.getPmState();
				bulkApplicationVO.setPmZone(SmApplicationUtil
						.retrieveZoneByStateId(state.getStateId()));
				if (appl.getSupportingDocMode().equalsIgnoreCase(
						SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
					bulkApplicationVO.setOnlineDoc(true);
				} else {
					bulkApplicationVO.setOnlineDoc(false);
				}
				bulkApplicationVO.setPaymentReqd(getPaymentMode(appl));
				bulkApplicationVO.setApplType(appl.getApplType());
				//21937 - start
				bulkApplicationVO
						.setPendingApproveCategoriesApprovedByPO(new ArrayList<SmSupplierCategory>());
				bulkApplicationVO
						.setPendingApproveCategoriesRejectedByPO(new ArrayList<SmSupplierCategory>());
				bulkApplicationVO
						.setApprovedCategoriesByPO(new ArrayList<SmSupplierCategory>());
				bulkApplicationVO
						.setRejectedCategoriesByPO(new ArrayList<SmSupplierCategory>());
				//21937 - end
				bulkApplicationVO
						.setPendingApproveCategories(new ArrayList<SmSupplierCategory>());
				bulkApplicationVO
						.setApprovedCategories(new ArrayList<SmSupplierCategory>());
				
				List<SmSupplierCategory> pendingRejectedCategories = new ArrayList<>();
				List<SmSupplierCategory> pendingApprovedCategories = new ArrayList<>();				
				for (SmSupplierCategory cat : appl.getSmSupplierCategories()) {
					if (cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_ACT_STATUS) == 0
							|| cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0) {
						cat.setApprvrDecFlg(cat.getPoDecFlg());
						if (cat.getRecordStatusFlg().compareTo(SupplierManagementConst.PENDING_RE_APPROVAL_STATUS) == 0
								&& SupplierManagementConst.ACC_BASIC_REACTIVATE.equals(appl.getApplType())) {
							bulkApplicationVO.getApprovedCategories().add(cat);
							//21937 - start
							if(cat.isApprovedByPo()){
								bulkApplicationVO.getApprovedCategoriesByPO().add(cat);
							} else {
								bulkApplicationVO.getRejectedCategoriesByPO().add(cat);
							}
							//21937 - end
						} else if (cat.isApprovedByPo()) {
							pendingApprovedCategories.add(cat);
						} else {
							pendingRejectedCategories.add(cat);
						}
						//21937 - start
					} else if(cat.getRecordStatusFlg().compareTo(SupplierManagementConst.DELETED_RECORD_STATUS) != 0) {
						bulkApplicationVO.getApprovedCategories().add(cat);
						//21937 - start
						if(cat.isApprovedByPo()){
							bulkApplicationVO.getApprovedCategoriesByPO().add(cat); 
						} else {
							bulkApplicationVO.getRejectedCategoriesByPO().add(cat); 
						}					
						//21937 - end
					}
				}

				sortListByCategoryCodes(pendingApprovedCategories);
				sortListByCategoryCodes(pendingRejectedCategories);
				bulkApplicationVO.getPendingApproveCategories().addAll(pendingApprovedCategories);
				bulkApplicationVO.getPendingApproveCategories().addAll(pendingRejectedCategories);
				//21937 - start
				bulkApplicationVO.getPendingApproveCategoriesApprovedByPO().addAll(pendingApprovedCategories);
				bulkApplicationVO.getPendingApproveCategoriesRejectedByPO().addAll(pendingRejectedCategories);
				
				bulkApplicationVO.setAppliedPaginatorApprovedByPO(new Paginator(bulkApplicationVO.getPendingApproveCategoriesApprovedByPO()));
				bulkApplicationVO.setAppliedPaginatorRejectedByPO(new Paginator(bulkApplicationVO.getPendingApproveCategoriesRejectedByPO()));
				
				bulkApplicationVO.setApprovedPaginatorApprovedByPO(new Paginator(bulkApplicationVO.getApprovedCategoriesByPO()));
				bulkApplicationVO.setApprovedPaginatorRejectedByPO(new Paginator(bulkApplicationVO.getRejectedCategoriesByPO()));
				//21937 - end
				
				bulkApplicationVO.setAppliedPaginator(new Paginator(bulkApplicationVO.getPendingApproveCategories()));

				sortListByCategoryCodes(bulkApplicationVO.getApprovedCategories());
				bulkApplicationVO.setApprovedPaginator(new Paginator(bulkApplicationVO.getApprovedCategories()));

				// return by applStatus
				if (!StringUtils.isBlank(getManagedBean().getApplStatus())
						&& !getManagedBean().getApplStatus()
								.equals(poRecommend)) {
					bulkApplicationVO = null;
				}
			}
		}

		return bulkApplicationVO;
	}

	private void sortListByCategoryCodes(List<SmSupplierCategory> list) {
		Collections.sort(list, new Comparator<SmSupplierCategory>() {
			@Override
			public int compare(SmSupplierCategory o1, SmSupplierCategory o2) {
				if (o1.getPmCategoryL1().getCategoryL1Code().equals(o2.getPmCategoryL1().getCategoryL1Code())) {
					if (o1.getPmCategoryL2().getCategoryL2Code().equals(o2.getPmCategoryL2().getCategoryL2Code())) {
						return o1.getPmCategoryL3().getCategoryL3Code()
								.compareTo(o2.getPmCategoryL3().getCategoryL3Code());
					} else {
						return o1.getPmCategoryL2().getCategoryL2Code()
								.compareTo(o2.getPmCategoryL2().getCategoryL2Code());
					}
				} else {
					return o1.getPmCategoryL1().getCategoryL1Code().compareTo(o2.getPmCategoryL1().getCategoryL1Code());
				}
			}
		});
	}

	private boolean getPaymentMode(SmAppl smAppl) {
		boolean payReq = false;
		if (smAppl.getApplType().equalsIgnoreCase(
				ParameterConst.NEW_SM_APPLN_FOR_RENEW)
				|| smAppl.getApplType().equalsIgnoreCase(
						ParameterConst.NEW_SM_APPLN_FOR_MOF)) {
			payReq = true;
		}
		return payReq;
	}
	
	/**
	 * Get application rejection reasons
	 * 
	 * @return String
	 */
	private String getApplicationRejectionReason(SmAppl smAppl) {
		String rejectReasons = StringUtils.EMPTY;
		if (smAppl.isActiveAppl()
				&& smAppl.isResubmit()
				&& smAppl.getOriginalAppl() != null) {

			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			mofReqDTO.setSmAppl(smAppl);
			List<SmApplRejectReason> rejectReasonList = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestRejectedApplicationRejectReasons(mofReqDTO)
					.getRejectReasons();
			rejectReasons = SmCommonUtil
					.getHtmlApplicationRejectReasons(rejectReasonList);

			if (StringUtils.isBlank(rejectReasons)) {
				rejectReasons = SmApplicationUtil.getSmRemarksByDocTypeAndId(
						ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
						smAppl.getOriginalAppl()
								.getApplId());
			}

		} else {
			if (smAppl.getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				SmRemarksReqDTO remarkReqDTO = new SmRemarksReqDTO();
				remarkReqDTO.setDocId(smAppl.getApplId());
				remarkReqDTO
						.setDocType(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT);
				SmRemaksResDTO remarkResDTO = SmServiceLookup.getSmRemarksSO()
						.retrieveSmRemarksByDocument(remarkReqDTO);
				if (!EntityHelper.isCollectionEmpty(remarkResDTO
						.getRemarksList())) {
					rejectReasons = remarkResDTO.getRemarksList().get(0)
							.getRemark();
				}
			} else {
				rejectReasons = SmCommonUtil
						.getHtmlApplicationRejectReasons(smAppl.getSmApplRejectReasons());

				if (StringUtils.isBlank(rejectReasons)) {
					rejectReasons = SmApplicationUtil
							.getSmRemarksByDocTypeAndId(
									ParameterConst.SM_APPLN_RMRK_DECISION_REJECT,
									smAppl.getApplId());
				}
			}
		}
		log.info("HTML REJECT REASON:" + rejectReasons);
		return rejectReasons;
	}
	
	//30974 - start
	private void cloneBulkPoApplSectionReview(SmBulkApplicationVO bulkAppl) {
		SmApplSectionReview apprApplSecRev = new SmApplSectionReview();
		for (SmApplSectionReview applSecRev : bulkAppl.getAppl().getSmApplSectionReviews()) {
			EntityHelper.copyEntityProperties(applSecRev, apprApplSecRev,
					new String[] { "applSectionReviewId" });
			apprApplSecRev.setSmAppl(bulkAppl.getAppl());
			apprApplSecRev
					.setRoleType(SupplierManagementConst.SECTION_REV_ROLE_APP);
			apprApplSecRev.setCheckCount(0);
			apprApplSecRev.setRecommendation(null);
			apprApplSecRev.setRemarkToApprover(null);
			apprApplSecRev.setPmRegStatus(null);
			apprApplSecRev.setSitevisitVerified(false);
			apprApplSecRev.setCreatedBy(SmCommonUtil.retrieveCreatedByChangeBy());
			bulkAppl.setApprvrSecReview(apprApplSecRev);
		}
		bulkAppl.getAppl().getSmApplSectionReviews().add(bulkAppl.getApprvrSecReview());
	}
	//30974 - end
}
