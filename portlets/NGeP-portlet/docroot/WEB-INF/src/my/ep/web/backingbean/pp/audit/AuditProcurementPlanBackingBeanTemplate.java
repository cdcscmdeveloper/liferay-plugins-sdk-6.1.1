package my.ep.web.backingbean.pp.audit;

import java.io.Serializable;

import my.ep.biz.service.pp.audit.IAuditProcurementPlanSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pp.AuditPPConst;
import my.ep.dto.pp.audit.AuditProcurementPlanReqDTO;
import my.ep.dto.pp.audit.AuditProcurementPlanResDTO;
import my.ep.dto.pp.audit.AuditProcurementPlanSPDTO;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pp.procurementplan.ProcurementPlanBackingBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * New file introduced for Audit Trail Procurement Plan (#28895 - #35502)
 * The Backing Bean Template for PP audit
 * 
 * @author Irfan
 * @since 10.06.2024
 */

@SuppressWarnings("rawtypes")
public class AuditProcurementPlanBackingBeanTemplate extends BaseBackingBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private ProcurementPlanBackingBean ppBB = null;

	/**
	 * This constructor is get ProcurementPlanManagedBean()
	 */
	public AuditProcurementPlanBackingBeanTemplate() {
		if (ppBB == null) {
			ppBB = new ProcurementPlanBackingBean();
		}
	}

	/**
	 * The void saveAuditData.
	 * 
	 * @param action
	 * @param isNeedAudit
	 */
	public final void saveAuditData(String action, Long isNeedAudit) {

		AuditProcurementPlanSPDTO auObj = insert(action, isNeedAudit);

		if (ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppBB.getManagedBean().getSelectedPlan().getPlanType()
				.getParameterCode())) {
			ppeAuditSO(auObj);
		} else {
			pppAuditSO(auObj);
		}

	}

	/**
	 * The void ppeAuditSO.
	 * 
	 * @param auProcurementPlanSPDTO
	 */
	private void ppeAuditSO(AuditProcurementPlanSPDTO auPPSPDTO) {
		try {
			IAuditProcurementPlanSO auditPPSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
			auditPPSO.insertPPEAudit(auPPSPDTO);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "ppeAuditSO", null,
					"AuditProcurementPlanBackingBeanTemplate", null, ex);
		}
	}
	
	/**
	 * The void pppAuditSO.
	 * 
	 * @param auProcurementPlanSPDTO
	 */
	private void pppAuditSO(AuditProcurementPlanSPDTO auPPSPDTO) {
		try {
			IAuditProcurementPlanSO auditPPSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
			auditPPSO.insertPPPAudit(auPPSPDTO);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(AuditPPConst.AUDIT_PROCUREMENT_PLAN, "pppAuditSO", null,
					"AuditProcurementPlanBackingBeanTemplate", null, ex);
		}
	}

	/**
	 * The public insert.
	 * 
	 * @param managebean
	 * @param action
	 * @param isNeedAudit
	 * @return auProcurementPlanSPDTO
	 */
	public AuditProcurementPlanSPDTO insert(String action, Long isNeedAudit) {

		AuditProcurementPlanReqDTO reqDTO = new AuditProcurementPlanReqDTO();
		AuditProcurementPlanSPDTO auSpDTO = new AuditProcurementPlanSPDTO();
		IAuditProcurementPlanSO reqSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROCUREMENT_PLAN, IAuditProcurementPlanSO.class);
		String docType = ParameterConst.PLAN_TYPE_EXPENDITURE.equals(ppBB.getManagedBean().getSelectedPlan()
				.getPlanType().getParameterCode()) ? ParameterConst.DOCTYPE_PLAN_EXPENDITURE
				: ParameterConst.DOCTYPE_PLAN_PROCUREMENT;
		reqDTO.setPlanId(ppBB.getManagedBean().getPlanId());
		reqDTO.setPlanType(docType);

		auSpDTO.setPlanId(ppBB.getManagedBean().getPlanId());
		auSpDTO.setOrgProfileId(ppBB.getManagedBean().getSelectedPlan().getOrgProfile().getOrgProfileId());
		auSpDTO.setCreateBy(getUserIdSessionFromLoginId());
		auSpDTO.setIsNeedAudit(isNeedAudit);

		// Expected Max Size in loop is 1
		AuditProcurementPlanResDTO resDTO = reqSO.retrieveCurrentWorkingFlow(reqDTO);
		if (resDTO.isSuccessful()) {
			switch (action) {
			case BpmOutcomeConst.BPM_SUBMIT:
				handleSubmit(resDTO, auSpDTO, docType);
				break;
			case BpmOutcomeConst.BPM_APPROVE:
				handleApprove(resDTO, auSpDTO, docType);
				break;
			case BpmOutcomeConst.BPM_VERIFY:
				handleVerify(resDTO, auSpDTO);
				break;
			default:
				handleRevise(resDTO, auSpDTO, docType);
			}
		}

		return auSpDTO;
	}

	/**
	 * To set current status id and role code for verify process
	 * 
	 * @param resDTO
	 * @param auSpDTO
	 */
	private void handleVerify(AuditProcurementPlanResDTO resDTO, AuditProcurementPlanSPDTO auSpDTO) {

		if (AuditPPConst.PP_PENDING_VERIFICATION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
			auSpDTO.setStatusId(AuditPPConst.PP_PENDING_APPROVAL);
		} else if (AuditPPConst.PP_PENDING_REVISION_VERIFICATION.equals(resDTO.getWorkflowStatus().getPmStatus()
				.getStatusId())) {
			auSpDTO.setStatusId(AuditPPConst.PP_PENDING_REVISION_APPROVAL);
		} else {
			auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
		}
		auSpDTO.setRoleCode(AuditPPConst.PLAN_VERIFICATION_OFFICER);
	}

	/**
	 * To set current status id and role code for submission process
	 * 
	 * @param resDTO
	 * @param auSpDTO
	 * @param docType
	 */
	private void handleSubmit(AuditProcurementPlanResDTO resDTO, AuditProcurementPlanSPDTO auSpDTO, String docType) {

		if (docType.equals(ParameterConst.DOCTYPE_PLAN_EXPENDITURE)) {
			if (AuditPPConst.PE_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PE_PENDING_SUBMISSION
							.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PE_PENDING_APPROVAL);
			} else if (AuditPPConst.PE_PENDING_REVISION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PE_PENDING_REVISION_APPROVAL);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
			}

		} else {
			if (AuditPPConst.PP_PENDING_SUBMISSION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PP_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PP_PENDING_VERIFICATION);
			} else if (AuditPPConst.PP_PENDING_REVISION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PP_PENDING_REVISION_VERIFICATION);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
			}
		}
		auSpDTO.setRoleCode(AuditPPConst.PLAN_OFFICER);
	}

	/**
	 * To set current status id and role code for approver process
	 * 
	 * @param resDTO
	 * @param auSpDTO
	 * @param docType
	 */
	private void handleApprove(AuditProcurementPlanResDTO resDTO, AuditProcurementPlanSPDTO auSpDTO, String docType) {

		if (docType.equals(ParameterConst.DOCTYPE_PLAN_EXPENDITURE)) {
			if (AuditPPConst.PE_PENDING_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PE_PENDING_REVISION_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus()
							.getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PE_APPROVAL);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
			}

		} else {
			if (AuditPPConst.PP_PENDING_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PP_PENDING_REVISION_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus()
							.getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PP_APPROVAL);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
			}
		}
		auSpDTO.setRoleCode(AuditPPConst.PLAN_APPROVER_OFFICER);
	}

	/**
	 * To set current status id and role code for revise process
	 * 
	 * @param resDTO
	 * @param auSpDTO
	 * @param docType
	 */
	private void handleRevise(AuditProcurementPlanResDTO resDTO, AuditProcurementPlanSPDTO auSpDTO, String docType) {

		if (docType.equals(ParameterConst.DOCTYPE_PLAN_EXPENDITURE)) {
			if (AuditPPConst.PE_PENDING_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PE_PENDING_REVISION_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus()
							.getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PE_PENDING_REVISION);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
			}
			auSpDTO.setRoleCode(AuditPPConst.PLAN_APPROVER_OFFICER);

		} else {

			if (AuditPPConst.PP_PENDING_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PP_PENDING_REVISION_APPROVAL.equals(resDTO.getWorkflowStatus().getPmStatus()
							.getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PP_PENDING_REVISION);
				auSpDTO.setRoleCode(AuditPPConst.PLAN_APPROVER_OFFICER);
			} else if (AuditPPConst.PP_PENDING_VERIFICATION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())
					|| AuditPPConst.PP_PENDING_REVISION_VERIFICATION.equals(resDTO.getWorkflowStatus().getPmStatus().getStatusId())) {
				auSpDTO.setStatusId(AuditPPConst.PP_PENDING_REVISION);
				auSpDTO.setRoleCode(AuditPPConst.PLAN_VERIFICATION_OFFICER);
			} else {
				auSpDTO.setStatusId(resDTO.getWorkflowStatus().getPmStatus().getStatusId());
				if (AuditPPConst.PP_PENDING_REVISION_VERIFICATION
						.equals(ppBB.getManagedBean().getAuditReviseStatusID())
						|| AuditPPConst.PP_PENDING_VERIFICATION.equals(ppBB.getManagedBean().getAuditReviseStatusID())) {
					auSpDTO.setRoleCode(AuditPPConst.PLAN_VERIFICATION_OFFICER);
				} else {
					auSpDTO.setRoleCode(AuditPPConst.PLAN_APPROVER_OFFICER);
				}
			}
		}
	}
	
}