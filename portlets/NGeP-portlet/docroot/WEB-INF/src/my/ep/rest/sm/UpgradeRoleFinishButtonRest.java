package my.ep.rest.sm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.backingbean.sm.postsupplierregistration.PostSupplierRegistrationBackingBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;

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
			bean.finishButtonAction(userId);			
			return Response.status(200).entity("success calling upgradeRoleFinishButton!!").build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	
	@GET
	@Path("/upgraderolefinishbuttonbyloginid/{loginId}")
	public Response upgradeRoleFinishButtonByLoginId(@PathParam("loginId") String loginId) {
		log.info("Before success calling upgraderolefinishbuttonbyloginid rest api....loginId="+loginId);
		PostSupplierRegistrationBackingBean bean = new PostSupplierRegistrationBackingBean();
		try {	
			PmUser pmUser = SmApplicationUtil.retreiveUserByLoginId(loginId);
			if(pmUser!= null){
				bean.finishButtonAction(pmUser.getUserId());
				bean = null;
			}
			
			return Response.status(200).entity("success calling upgraderolefinishbuttonbyloginid!!").build();
		} catch (Exception ex) {
			bean=null;
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}


}
