package my.ep.rest.sm;

import java.sql.Timestamp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import my.ep.biz.service.osb.sm.ISMOsbSO;
import my.ep.biz.service.sm.upgraderole.IUpgradeRoleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.sm.SMOsbReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmUpdateRoleProcess;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm") 
public class UpgradeRoleFinishButtonRest {
	public static Logger log = Logger.getLogger(UpgradeRoleFinishButtonRest.class);
	
	//#30942 LOG START
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}
	
	@GET
	@Path("/upgraderolefinishbutton/{userId}")
	public Response upgradeRoleFinishButton(@PathParam("userId") long userId) {
		log.info("Before success calling upgradeRoleFinishButton rest api....loginId="+userId);
		//PostSupplierRegistrationBackingBean bean = new PostSupplierRegistrationBackingBean();

		try {	
			//bean.finishButtonAction(userId);	
			log.info("START IN UpgradeRoleFinishButtonRest");
			IUpgradeRoleSO tmpSO = (IUpgradeRoleSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IUpgradeRoleSO.class);
			
			SMOsbReqDTO reqqDTO = new SMOsbReqDTO();
			reqqDTO.setUserId(userId);
			SmSchedulerResDTO resDTO = tmpSO.finishButtonAction(userId);
			log.info(resDTO.isSuccessful()+"=END IN UpgradeRoleFinishButtonRest==="+resDTO.getErrorMessage());
			return Response.status(200).entity("success calling upgradeRoleFinishButton!!").build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	
	@GET
	@Path("upgraderolefinishbuttonbulk")
	public Response upgradeRoleFinishButtonBulk() {
		log.info("calling upgradeRoleFinishButtonBulk rest api....");
		try {	
			//START 12491
			IUpgradeRoleSO upgradeRoleSO = SmServiceLookup.getUpgradeRoleSO();
			debugLogger("30942 :: start -> upgradeRoleFinishButtonBulk()");
			SmSchedulerResDTO resDTO = upgradeRoleSO.retrievePendingUpgradeRole();
			if(resDTO.getSmUpdateRoleProcess()!= null){
				for(SmUpdateRoleProcess process:resDTO.getSmUpdateRoleProcess()){
					try{
						if(process.getPmUser()!=null){
							log.info("LIST USER ID = "+process.getPmUser().getUserId());
							SmSchedulerResDTO res = upgradeRoleSO.finishButtonAction(process.getPmUser().getUserId());
							process.setChangedDate(new Timestamp(System.currentTimeMillis()));
							if(res.isSuccessful()){
								process.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
								process.setErrorMsg(null);
							}else{
								process.setAttemp(process.getAttemp() + 1);
								process.setErrorMsg("Error IS :"+res.getErrorMessage());
							}
							
							upgradeRoleSO.updateFailPending(process);
						}
					}catch(Exception e){
						process.setAttemp(process.getAttemp() + 1);
						process.setErrorMsg(e.getMessage());
						try {
							upgradeRoleSO.updateFailPending(process);
						} catch (Exception e2) {
							log.error(e2);
						}
					}
					
					
				}
			}
			//END 12491
			return Response.status(200).entity("success calling upgradeRoleFinishButtonBulk!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	@GET
	@Path("callscheduler")
	public Response callScheduler() {
		log.info("calling callscheduler rest api....");
		try {	
			ISMOsbSO osbSO = (ISMOsbSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISMOsbSO.class);
			SMOsbReqDTO smOsbReqDTO = new SMOsbReqDTO();
		/*	smOsbReqDTO.setSoftcertRequestId();
			smOsbReqDTO.setCertMode();
			smOsbReqDTO.setResponseStatus()
			smOsbReqDTO.setOrn()
			smOsbReqDTO.setPrn();
			smOsbReqDTO.setCertSerialNo();
			smOsbReqDTO.setValidFrom();
			smOsbReqDTO.setValidTo();
			smOsbReqDTO.setReasonCode()
			smOsbReqDTO.setResponseRemark()
			smOsbReqDTO.setResponseStatus()*/
			//SMOsbResDTO smOsbResDTO =  osbSO.updateSupplierSoftcert(smOsbReqDTO);
			osbSO.updateSupplierSoftcert(smOsbReqDTO);
			
			return Response.status(200).entity("success calling callscheduler!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	


}
