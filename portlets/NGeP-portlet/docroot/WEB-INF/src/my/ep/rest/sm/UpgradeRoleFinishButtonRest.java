package my.ep.rest.sm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import my.ep.biz.service.ntf.scheduler.INtfMessageBulkSchedulerSO;
import my.ep.biz.service.ntf.scheduler.INtfMessageRealTimeSchedulerSO;
import my.ep.biz.service.osb.sm.ISMOsbSO;
import my.ep.biz.service.sm.upgraderole.IUpgradeRoleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.osb.sm.SMOsbReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.sm.postsupplierregistration.PostSupplierRegistrationBackingBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm") 
public class UpgradeRoleFinishButtonRest {
	public static Logger log = Logger.getLogger(UpgradeRoleFinishButtonRest.class);
	
	@GET
	@Path("/upgraderolefinishbutton/{userId}")
	public Response upgradeRoleFinishButton(@PathParam("userId") long userId) {
		log.info("Before success calling upgradeRoleFinishButton rest api....loginId="+userId);
		PostSupplierRegistrationBackingBean bean = new PostSupplierRegistrationBackingBean();

		try {	
			//bean.finishButtonAction(userId);	
			log.info("START IN UpgradeRoleFinishButtonRest");
			IUpgradeRoleSO tmpSO = (IUpgradeRoleSO) BusinessDelegate
					.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IUpgradeRoleSO.class);
			
			SMOsbReqDTO reqqDTO = new SMOsbReqDTO();
			reqqDTO.setUserId(userId);
			String a = tmpSO.finishButtonAction(userId);
			log.info("END IN UpgradeRoleFinishButtonRest==="+a);
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
			IUpgradeRoleSO upgradeRoleSO = SmServiceLookup.getUpgradeRoleSO();
			SmSchedulerResDTO resDTO = upgradeRoleSO.retrievePendingUpgradeRole();
			if(resDTO.getPersonnels() != null){
				for(SmPersonnel personnel:resDTO.getPersonnels()){
					if(personnel.getPmUser()!=null){
						log.info("LIST USER ID = "+personnel.getPmUser().getUserId());
						upgradeRoleSO.finishButtonAction(personnel.getPmUser().getUserId());
					}
					
				}
			}
			
			return Response.status(200).entity("success calling upgradeRoleFinishButtonBulk!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	


}
