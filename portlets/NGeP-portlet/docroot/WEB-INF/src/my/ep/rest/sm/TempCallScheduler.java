package my.ep.rest.sm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm")
public class TempCallScheduler {

	public static Logger log = Logger.getLogger(TempCallScheduler.class);

	@GET
	@Path("callscheduler")
	public Response callScheduler() {
		log.info("Before success calling callScheduler rest api....");
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		try {
			reqSO.resetExpiredPendingActivationPersonnel();
			return Response.status(200).entity("success calling callScheduler!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}
	

}
