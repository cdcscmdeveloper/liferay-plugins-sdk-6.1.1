package my.ep.rest.pm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmPendingLiferay;
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
		List<PmPendingLiferay> liferayList = SmServiceLookup.getUserSO()
				.retrievePendingLiferay(reqDTO).getPendingLiferayList();

		if (!EntityHelper.isCollectionEmpty(liferayList)) {
			for (PmPendingLiferay liferay : liferayList) {
				reqDTO.setLiferay(liferay);
				SmServiceLookup.getUserSO().saveUserLiferayOIM(reqDTO);
			}
		}
		log.info("After success calling createLiferay rest api....");
		return Response.status(200).entity("success calling create liferay!!")
				.build();
	}

}
