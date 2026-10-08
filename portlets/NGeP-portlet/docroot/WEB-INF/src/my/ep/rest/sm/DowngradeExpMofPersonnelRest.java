package my.ep.rest.sm;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.bpmWorkflowCallback.IBpmWorkflowCallbackSO;
import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.biz.service.sm.trackingdiary.ISmTrackingDiarySO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.constants.BpmEndpointConst;
import my.ep.constants.SMProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.sm.bpmWorkflowCallback.BpmWorkflowCallbackReqDTO;
import my.ep.dto.sm.bpmWorkflowCallback.BpmWorkflowCallbackResDTO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.trackingdiary.SmTrackingDiaryReqDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.bpm.BpmWorkflowCallback;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;

@Path("/sm")
public class DowngradeExpMofPersonnelRest {

	public static Logger log = Logger.getLogger(DowngradeExpMofPersonnelRest.class);

	@GET
	@Path("downgradeexpiredpersonnel")
	public Response downgradeExpMofPersonnel() {
		log.info("Before success calling downgradeExpMofPersonnel rest api....");
		try {
			//33628 START
//			downgradeMOFToBasic();
//			changeExpiredMofPersonnelToDeleted();
//			updateMOFExpiredMoreThanTwoYears();
//			updateForMOFExpiredMoreThanThreeYears();
			//33628 END
			updateBpmWorkflowCallback();
			return Response.status(200).entity("success calling downgradeExpMofPersonnel!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	//33628 START
	private void downgradeMOFToBasic(){
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMof();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();

		log.info("**********downgradeMOFToBasic - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_EXPIRED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_DOWNGRADE_BASIC);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setSmAppl(mofAccount.getSmSupplier().getSmAppl());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());

					try {
						reqSO.updateMOFSupplierPersonnelToBasic(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("ChangeExpiredMofPersonnelToBasicJob updateMoFAcc -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
			log.info("**********downgradeMOFToBasic - END************");
		}
	}

	private void changeExpiredMofPersonnelToDeleted() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMofPendingDeletion();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********changeExpiredMofPersonnelToDeleted - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if(mofAccount.getSmSupplier().getSmAppl() != null){
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_ONEYEAR);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setSmAppl(mofAccount.getSmSupplier().getSmAppl());

					try {
						reqSO.updateExpiredMOFSupplierPersonnel(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("ChangeExpiredMofPersonnelToBasicJob updateMoFAcc -  Exception>>>>" + ex.getMessage()
								+ "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
					
					// #28800 START
					try {
						reqSO.sendEmailExpiredMOFToSupplier(schedulerReqDTO);
					} catch (Exception ex){
						log.info("sendEmailExpiredMOFToSupplier ERROR :: " +ex.getMessage());
					}
					// #28800 END
				}
			}
		}
		log.info("**********changeExpiredMofPersonnelToDeleted - END************");
	}
	
	private void updateMOFExpiredMoreThanTwoYears() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFExpiredMoreThanTwoYears();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********updateSupplierForMOFExpired - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_TWOYEARS);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());
					try {
						reqSO.updateMOFExpiredMoreThanTwoYears(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateMOFExpiredMoreThanTwoYears -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
		}
		log.info("**********updateMOFExpiredMoreThanTwoYears - END************");
	}
		
	private void updateForMOFExpiredMoreThanThreeYears() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveMOFExpiredMoreThanThreeYears();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********updateForMOFExpiredMoreThanThreeYears - Start************");
		if (!mofAccountLs.isEmpty()) {
			for (SmMofAccount mofAccount : mofAccountLs) {
				if (mofAccount.getSmSupplier().getSmAppl() != null) {
					mofAccount.setPrevMofStatus(mofAccount.getRecordStatusFlg());
					mofAccount.setRecordStatusFlg(DbValueConst.RECORD_STATUS_DELETED);
					mofAccount.setLastCheckDate(new Date());
					mofAccount.setChangedBy(null);
					
					SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
					mofAccount.setNewMofStatus(mofAccount.getRecordStatusFlg());
					schedulerReqDTO.setMofAccount(mofAccount);
					schedulerReqDTO.setSchedulerType(SupplierManagementConst.SM_SCHEDULER_MOF_EXPIRED_THREEYEARS);
					schedulerReqDTO.setSmSupplier(mofAccount.getSmSupplier());
					schedulerReqDTO.getSmSupplier().setPrevSuppStatus(mofAccount.getSmSupplier().getRecordStatusFlg());
					try {
						reqSO.updateForMOFExpiredMoreThanThreeYears(schedulerReqDTO);
					} catch (Exception ex) {
						log.info("updateForMOFExpiredMoreThanThreeYears -  Exception>>>>"
								+ ex.getMessage() + "- supplier ID -" + mofAccount.getSmSupplier().getSupplierId());
					}
				}
			}
		}
		log.info("**********updateForMOFExpiredMoreThanThreeYears - END************");
	}
	//33628 END
	
	private void updateBpmWorkflowCallback() {
		IBpmWorkflowCallbackSO bpmWorkflowCallbackSO = SmServiceLookup.getBpmWorkflowCallbackSO();
		ISmTrackingDiarySO reqSO = SmServiceLookup.getSmTrackingDiarySO();
		BpmWorkflowCallbackReqDTO reqDTO = new BpmWorkflowCallbackReqDTO();
		// reqDTO.setRoleCode(RoleCode.UPS_PO.value());
		reqDTO.setCompositeName(SMProcessNameConst.MOF_APPLICATION_COMPOSITE_NAME);
		reqDTO.setCallbackStatus(SMProcessNameConst.PENDING_CALLBACK_STATUS);
		BpmWorkflowCallbackResDTO resDTO = bpmWorkflowCallbackSO.retrieveBpmWorkflowCallback(reqDTO);
		List<BpmWorkflowCallback> bpmWorkflowlLs = resDTO.getBpmWorkflowlLs();
		log.info(bpmWorkflowlLs.size());

		if (!bpmWorkflowlLs.isEmpty()) {
			for (BpmWorkflowCallback workflow : bpmWorkflowlLs) {
				IBpmSO bpmSO = null;
				try {
					bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

					BpmRequestDTO bpmRequestDTO = new BpmRequestDTO();
					String[] workflowInfo = { workflow.getCompositeName(), workflow.getProcessName(),
							workflow.getOperationName() };
					bpmRequestDTO.setEndpoint(workflowInfo);
					HashMap<String, String> parm = new LinkedHashMap<String, String>();
					if (workflow.getOperationName().equalsIgnoreCase("assignedPOUsers")) {
						String assignee = "880314086440";
						parm.put(SMProcessNameConst.MOF_APPLICATION_CORRELATION_ID, workflow.getCorrelationId()
								.toString());
						parm.put(SMProcessNameConst.MOF_APPLICATION_ACTIVE_PO_USER, assignee);
					} else {
						String assignee = "770814125082";
						parm.put(SMProcessNameConst.MOF_APPLICATION_ACTIVE_USER, assignee);
						parm.put(SMProcessNameConst.MOF_APPLICATION_CORRELATION_ID, workflow.getCorrelationId()
								.toString());
					}

					bpmRequestDTO.setParameter(parm);
					BpmResponseDTO responseDTO = bpmSO.callProcessWS(bpmRequestDTO);

					if (responseDTO.isSuccessful()) {
						workflow.setCallbackStatus("RESUMED");
						workflow.setRecordStatusFlg(0);
						reqDTO.setSelectedBpmWorkflow(workflow);
						bpmWorkflowCallbackSO.updateBpmWorkflowCallback(reqDTO);

						SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
						supplierSearchReqDTO.setApplId(workflow.getDocId());
						SmAppl appl = SmServiceLookup.getSupplierSearchSO()
								.retrieveSmApplWithChildByApplID(supplierSearchReqDTO).getSmAppl();

						IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
						UserReqDTO userReqDTO = new UserReqDTO();
						userReqDTO.setLoginId(workflow.getPreviousAssignee().toString());
						String userName = userSO.retrieveUserByLoginId(userReqDTO).getPmUser().getUserName();

						SmTrackingDiaryReqDTO trckReqDTO = new SmTrackingDiaryReqDTO();
						trckReqDTO.setAppl(appl);
						trckReqDTO.setActionedBy(null);
						if (StringUtils.isEmpty(userName)) {
							trckReqDTO.setCurrentPoName(userName);
						}
						reqSO.saveSupplierDeclareOrRespondPoInquiryTrackingDiary(trckReqDTO);
						resDTO.setSuccessful(responseDTO.isSuccessful());
					}
				} catch (Exception e) {
					log.info("$$$$$ QT Publish or Close BPM Update failed");
					log.info(e.getMessage(), e);
					resDTO.setSuccessful(false);
					String errorMessage = null;
					Throwable rootCause = ExceptionUtils.getRootCause(e);
					if (rootCause != null) {
						errorMessage = ExceptionUtils.getStackTrace(rootCause);
					} else {
						errorMessage = ExceptionUtils.getStackTrace(e);
					}
					resDTO.setErrorMessage(errorMessage);
				}
			}
			log.info("**********downgradeMOFToBasic - END************");
		}
	}
	
}
