package my.ep.rest.ct;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author shaqimi
 *
 * REST endpoint for schedular to handling contract performance bond notifications.
 */
@Path("/ct")
public class ContractPerformanceBondRest {

	private static final Logger log = Logger.getLogger(ContractPerformanceBondRest.class);
	//#13847 start

	/**
     * Calls the performance bond notification service.
     *
     * @return HTTP response indicating success or failure.
     */
    @GET
    @Path("/callPerformanceBondNotifications/")
    public Response callPerformanceBondNotifications() {
        ICtSchedulerSO tmpSO = BusinessDelegate.getInstance().getServiceObject(
                ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);
        try {
            tmpSO.performanceBondNotification();
            return Response.status(200).entity("success calling callPerformanceBondNotifications!!").build();
        } catch (Exception ex) {
            log.error("callNotifications -  Exception>>>> " + ex);
            return Response.status(406).entity(ex.getMessage()).build();
        }
    }
    //#13847 end
}
