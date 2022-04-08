package my.ep.rest.pm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
		log.info("Before success calling createLiferay rest api....");

		UserReqDTO reqDTO = new UserReqDTO();
		try {
			SmServiceLookup.getUserSO().createPendingLiferay(reqDTO);

			return Response.status(200)
					.entity("success calling create liferay!!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}

	}
	
	@GET
	@Path("/createliferaysingle/{loginId}")
	public Response createLiferaySingle(@PathParam("loginId") String loginId) {
		log.info("Before success calling createLiferay rest api....");

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(loginId);
		try {
			//SmServiceLookup.getUserSO().createPendingLiferaySingle(reqDTO);

			return Response.status(200)
					.entity("success calling create liferay!!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}

	}

}
