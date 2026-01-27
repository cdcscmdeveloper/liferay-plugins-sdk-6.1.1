package my.ep.rest.sm;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.bpm.vo.WorklistVO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationReqDTO;
import my.ep.dto.sm.reviewApplication.ReviewApplicationResDTO;
import my.ep.dto.sm.sdoreview.SdoReviewReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sm.mofappcandata.SMMOFAppCanDataType;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * #39857
 * REST cancel pending appl
*/
@Path("/sm")
public class ApprovePendingCancelApplRest {

public static final Logger log = Logger.getLogger(ApprovePendingCancelApplRest.class);
	


/**
	 * REST system terminate appl, URL: cancelappl?no=xxx
	 *  
	 * @param applNo
	 * @return
	 * @throws IOException
	 * @return
	 */
	@GET
	@Path("/cancelappl")
	public String terminateAppl(@QueryParam("no") String applNo) throws IOException {
		log.info("Terminating... " + applNo);
		log.info("attempt 1");
		
		SmServiceLookup.getReviewApplicationSO().terminateApplRest(applNo);
		return "Done terminate : "+applNo;
	}


	/**
	 * REST cancel pending appl, URL: cancelappl2?no=xxx
	 * 
	 * Based on deletePendingMofAppln/cancelPendingApplicationDescisionPage.xhtml
	 *  in CommonApplBackingBean
	 *  
	 * @param applNo
	 * @return
	 * @throws IOException
	 * @return
	 */
	@GET
	@Path("/cancelappl2")
	public String cancelAppl(@QueryParam("no") String applNo) throws IOException {
		log.info("Approve pending cancel: " + applNo);
		log.info("attempt 6");
		
		MofRegistrationReqDTO req = new MofRegistrationReqDTO();
		req.setApplNo(applNo);
		SmAppl appl = SmServiceLookup.getMofRegistrationSO().retrieveSmApplWithChildByApplNo(req).getSmAppl();
		
		Object token = SmServiceLookup.getBpmSO().authenticate(
						EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.BPM_PRINCIPAL));
		
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setDocNumber(applNo);
		reqDTO.setState("ASSIGNED");
		BpmResponseDTO resDTO = SmServiceLookup.getBpmSO().getTasksByDocNumber(reqDTO);
		WorklistVO vos = resDTO.getWorklistVO();
		for (WorklistItemVO vo : vos.getWorklistItem()){
			log.info(vo.getTaskId());
			log.info(vo.getTaskName());
			
			token = SmServiceLookup.getBpmSO().authenticate(vo.getAssignees().get(0));
			
			SMMOFAppCanDataType payload = getPojo(vo.getTaskId(), token);
			approve(appl, payload, vo.getTaskId(), token);
		}
		return "Done cancelling : "+applNo;
	}
	
	
	private SMMOFAppCanDataType getPojo(String taskId, Object token){
		SdoReviewReqDTO reqDTO = new SdoReviewReqDTO();
		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);
		BpmResponseDTO resDTO = (BpmResponseDTO) SmServiceLookup.getSdoReviewSO().getBPMTaskDetails(reqDTO);
		return (SMMOFAppCanDataType) resDTO.getPojo();
	}
	
	
	/*
	 * Based on approveOrRejectPendingApplicationCancellation('APPROVE')
	 */
	private void approve(SmAppl appl, SMMOFAppCanDataType payload, String taskId, Object token) {
		String res = my.ep.constant.sm.SupplierManagementConst.APPROVE;
		ReviewApplicationReqDTO reqDTO = new ReviewApplicationReqDTO();
		
		PmState state = appl.getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState();
		if (state != null && state.getStateId() != null) {
			PmZone zone = SmApplicationUtil.retrieveZoneByStateId(state.getStateId());
			if (zone != null && zone.getZoneName() != null) {
				reqDTO.setLocation(zone.getZoneName().toUpperCase());
			}
		} else {
			throw new EpException(MessageConst.ERROR_722568);
		}
		
		if (!StringUtils.isEmpty(reqDTO.getLocation())) {
			if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SABAH)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessageMalay(
						"lbl.sm.notifAddr.sabah", null));
			} else if (reqDTO.getLocation().equalsIgnoreCase(
					SupplierManagementConst.SM_STATE_NAME_SARAWAK)) {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessageMalay(
						"lbl.sm.notifAddr.sarawak", null));
			} else {
				reqDTO.setNotifSignAddr(SmCommonUtil.getMessageMalay(
						"lbl.sm.notifAddr.semunanjung", null));
			}
		}
		
		reqDTO.setOutcome(res);
		reqDTO.setTaskId(taskId);
		reqDTO.setSmAppl(appl);
		reqDTO.setToken(token);
		
		SMMOFAppCanDataType smAppCanObj = payload;
		//if old version & approve outcome use payload to set bpmpojo
		//outcome is always APPROVE
		reqDTO.setBpmPojo(payload);
		
		reqDTO.setCurrWorkflowStatus(smAppCanObj.getCurrWfStatus().longValue());
		reqDTO.setChangedBy(1l);
		//#38195 END
		
		ReviewApplicationResDTO resDTO = SmServiceLookup.getReviewApplicationSO()
				.approveOrRejectPendingApplicationCancellation(reqDTO);
		log.info("Cancelled..." + appl.getApplNo() + "\t" + resDTO.isSuccessful());
	}
	
}
