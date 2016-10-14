package my.ep.web.backingbean.sm.bulkapplication;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.bulkapplication.SmBulkApplicationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmApplicationNotificationUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "bulkApplicationBackingBean")
@RequestScoped
public class SmBulkApplicationBackingBean extends
		BaseBackingBean<SmBulkApplicationManagedBean> {
	/**
	 * Pre-render bulk application screen
	 */
	public void preRenderBulkApplication() {
		renewSesionObject();
		getManagedBean().setSelectedTasks(new ArrayList<SmBulkApplicationVO>());
		SmCommonUtil.setMDCUserId();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/bulkApplication/bulkApplicationTaskList.xhtml", "",
				null);
	}

	public void approveBulkApplication() {
		if (getManagedBean().getSelectedTasks() != null
				&& !getManagedBean().getSelectedTasks().isEmpty()) {
			FacesScopes fs = new FacesScopes();
			ReviewApplicationReqDTO reviewAppReqDTO = null;

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
					reviewAppReqDTO = getReviewApplicationReqDTO(fs,
							basePortalUrl, bulkAppl);

					SmServiceLookup.getReviewApplicationSO()
							.submitPOorApproverReviewToBPM(reviewAppReqDTO);
					successSubmittedList.add(bulkAppl.getAppl());
				} catch (Exception ex) {
					getManagedBean().getFailedSubmittedList().add(
							bulkAppl.getAppl());
				}

			}
			showSuccessApplicationMsg(successSubmittedList);
		}
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

	private void showSuccessApplicationMsg(List<SmAppl> applList) {
		StringBuilder sb = new StringBuilder();
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
							SmCommonUtil.getMessage(
									"lbl.sm.bulk.approval.success.msg",
									new String[] { sb.toString() })));
		}
	}

	private ReviewApplicationReqDTO getReviewApplicationReqDTO(FacesScopes fs,
			String basePortalUrl, SmBulkApplicationVO bulkVO) {
		ReviewApplicationReqDTO revReqDTO = new ReviewApplicationReqDTO();
		SdoReviewReqDTO sdoReqDTO = null;
		sdoReqDTO = new SdoReviewReqDTO();
		sdoReqDTO.setTaskId(bulkVO.getTaskId());
		sdoReqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		BpmResponseDTO bpmResDTO = (BpmResponseDTO) SmServiceLookup
				.getSdoReviewSO().getBPMTaskDetails(sdoReqDTO);
		revReqDTO.setSmAppl(bulkVO.getAppl());
		revReqDTO
				.setPoOrApproverDecision(my.ep.constant.sm.SupplierManagementConst.APPROVE);
		revReqDTO.setPoLoginId(fs.getUserSessionBean().getLoginId());
		revReqDTO.setPoRole(false);
		revReqDTO.setApproverRole(true);
		revReqDTO.setApplId(bulkVO.getAppl().getApplId());
		revReqDTO.setApplNo(bulkVO.getAppl().getApplNo());
		revReqDTO.setOutcome(my.ep.constant.sm.SupplierManagementConst.APPROVE);
		revReqDTO.setSuppId(bulkVO.getAppl().getSmSupplier().getSupplierId());
		revReqDTO.setBpmTaskName(bpmResDTO.getTaskName());
		revReqDTO.setAppType(bulkVO.getAppl().getApplType());
		revReqDTO.setTaskId(bulkVO.getTaskId());
		revReqDTO.setLocation(bulkVO.getPmZone().getZoneName().toUpperCase());
		revReqDTO.setSupplierType(bulkVO.getAppl().getSupplierType()
				.toUpperCase());
		revReqDTO.setToken(fs.getBPMAuthenticationCtxToken());
		revReqDTO.setLangCode(fs.getUserLocale().getLanguage());
		revReqDTO.setUserList(new ArrayList<String>());
		revReqDTO.setServerName(fs.getRequest().getServerName());
		revReqDTO.setServerPort(fs.getRequest().getServerPort());
		revReqDTO.setPortalBaseUrl(basePortalUrl);
		revReqDTO.setOnlineDoc(bulkVO.isOnlineDoc());
		SmApplicationNotificationUtil.setApprovalNotificationParameters(
				bulkVO.getAppl(), null, false,
				bulkVO.getAppl().getSmSupplier(), null, null, revReqDTO);
		return revReqDTO;
	}

	public void onClaimBpmTask() {
		claimBpmTask();
		refreshTaskList();
	}

	public void onReleaseBpmTask() {
		releaseBpmTask();
		refreshTaskList();
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
}
