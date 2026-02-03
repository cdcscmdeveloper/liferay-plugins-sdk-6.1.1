package my.ep.rest.sm;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import my.ep.web.util.sm.helper.SmServiceLookup;
import org.apache.log4j.Logger;

/**
 * #39857
 * REST cancel pending appl
*/
@Path("/sm")
public class ApprovePendingCancelApplRest {

public static final Logger log = Logger.getLogger(ApprovePendingCancelApplRest.class);
	


/**
	 * REST system auto-approve cancel pending appl, URL: cancelappl?no=xxx
	 *  
	 * @param applNo
	 * @return
	 * @throws IOException
	 * @return
	 */
	@GET
	@Path("/cancelappl")
	public String cancelAppl(@QueryParam("no") String applNo) throws IOException {
		log.info("Terminating... " + applNo);
		SmServiceLookup.getReviewApplicationSO().terminateApplRest(applNo);
		return "Done terminate : "+applNo;
	}
	
}
