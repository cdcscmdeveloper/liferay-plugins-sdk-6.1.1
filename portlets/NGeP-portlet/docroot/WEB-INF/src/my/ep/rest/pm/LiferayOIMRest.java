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
import org.jboss.resteasy.annotations.Suspend;
import org.jboss.resteasy.spi.AsynchronousResponse;

@Path("/pm")
public class LiferayOIMRest {

	public static Logger log = Logger.getLogger(LiferayOIMRest.class);

	@GET
	@Path("createliferay")
	public Response createLiferay(
			@Suspend(30000) final AsynchronousResponse response) {
		log.info("Before success calling createLiferay rest api....");

		Thread t = new Thread() {
			@Override
			public void run() {
				UserReqDTO reqDTO = new UserReqDTO();
				List<PmPendingLiferay> liferayList = SmServiceLookup
						.getUserSO().retrievePendingLiferay(reqDTO)
						.getPendingLiferayList();

				if (!EntityHelper.isCollectionEmpty(liferayList)) {
					for (PmPendingLiferay liferay : liferayList) {
						reqDTO.setUserId(liferay.getPmUser().getUserId());
						reqDTO.setLoginId(liferay.getPmUser().getLoginId());
						SmServiceLookup.getUserSO()
								.syncLiferayAndOimInfoFromPm(reqDTO);
					}
				}
				response.setResponse(Response.status(200)
						.entity("success sync Liferay and OIM!!").build());
			}
		};
		t.start();

		return Response.status(200).entity("success calling create liferay!!")
				.build();

	}

}
