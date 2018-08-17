package my.ep.rest.pm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/pm")
public class LiferayOIMRest {

	public static Logger log = Logger.getLogger(LiferayOIMRest.class);

	@GET
	@Path("createliferay")
	public Response createLiferay() {
		UserReqDTO reqDTO = new UserReqDTO();
		log.info("Before success calling createLiferay rest api....");
		SmServiceLookup.getUserSO().createUserLiferayOIM(reqDTO);
		log.info("After success calling createLiferay rest api....");
		return Response.status(200).entity("success calling create liferay!!")
				.build();
	}

}
