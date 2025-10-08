package my.ep.rest.ct;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import my.ep.web.util.ct.CtServiceLookup;
import org.apache.log4j.Logger;

@Path("/ct")
public class CtCompanyExperienceRest {

	public static Logger log = Logger.getLogger(CtCompanyExperienceRest.class);



	@GET
	@Path("/ctcompexpscheduler/{process_mode}")
	public Response hintToCompExpScheduler(@PathParam("process_mode") String process_mode) {
		log.info("Before success calling hintToCompExpScheduler retrieve rest api....");

		long startTime = System.currentTimeMillis();

		try {

			CtServiceLookup.getCtSchedulerSO().companyExpJob(process_mode);

			long endTime = System.currentTimeMillis();
			log.info("#This function takes " + (endTime - startTime) + " milliseconds");
			return Response.status(200).entity("Success calling hintToCompExpScheduler!!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
