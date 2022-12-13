package my.ep.rest.cm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import my.ep.web.util.sm.helper.SmServiceLookup;

@Path("/cm")
public class CmJobRest {
	//new file #30457
	
	public static Logger log = Logger.getLogger(CmJobRest.class);
	
	@GET
	@Path("/deleteimages/{year}")
	public Response deleteImages(@PathParam("year") int year) {
		log.info("Before success calling delete item rest api....");
		try {
			SmServiceLookup.getCatalogueSO().getAllItemByDeletedSupplier(year);
			
			return Response.status(200).entity("success calling deleteImages!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}

}
