package my.ep.rest.fl.yep;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import my.ep.web.util.fl.yep.FlYepServiceLookup;

import org.apache.log4j.Logger;

@Path("/yep")
public class FulfilmentYepRest {

	public static Logger log = Logger.getLogger(FulfilmentYepRest.class);

	@GET
	@Path("/terminatetask/{docNo}/{phaseNo}/{limit}/{isBatch}")
	public Response terminateTaskYEP(@PathParam("docNo") String docNo, @PathParam("phaseNo") int phaseNo,
			@PathParam("limit") int limit, @PathParam("isBatch") boolean isBatch) {
		try {
			log.info("Initiate Process:terminateTaskYEP: docNo: "+docNo+"|phaseNo: "+phaseNo+"|limit: "+limit+"|isBatch: "+isBatch);
			long startTime = System.currentTimeMillis();

			FlYepServiceLookup.getFlYepSO().terminateTaskManual(docNo, phaseNo, limit, isBatch);
			
			long endTime = System.currentTimeMillis();
			log.info("End Process:terminateTaskYEP");

			return Response.status(200).entity("Success calling terminateTaskYEP!! Run for "+ (endTime - startTime) +" milliseconds").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}

}
