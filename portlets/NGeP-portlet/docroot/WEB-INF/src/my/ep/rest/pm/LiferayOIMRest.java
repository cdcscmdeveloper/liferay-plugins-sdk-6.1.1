package my.ep.rest.pm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/pm")
public class LiferayOIMRest {

	public static Logger log = Logger.getLogger(LiferayOIMRest.class);

	@GET
	@Path("createliferay")
	public void createLiferay() {
		UserReqDTO reqDTO = new UserReqDTO();
		SmServiceLookup.getUserSO().createUserLiferayOIM(reqDTO);
		log.info("After success calling createLiferay rest api....");
	}

}
