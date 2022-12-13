package my.ep.rest.ct;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

@Path("/ct")
public class ContractPerformanceBondRest {

	public static Logger log = Logger.getLogger(ContractPerformanceBondRest.class);
	//#13847 start
    @GET
    @Path("/callPerformanceBondNotifications/")
    public Response callPerformanceBondNotifications() {
        log.info("Before success calling callPerformanceBondNotifications rest api....");
        ICtSchedulerSO tmpSO = (ICtSchedulerSO) BusinessDelegate.getInstance().getServiceObject(
                ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);
        try {
            tmpSO.performanceBondNotification();
            return Response.status(200).entity("success calling callPerformanceBondNotifications!!").build();
        } catch (Exception ex) {
            log.info("callNotifications -  Exception>>>> " + ex.getMessage() );
            return Response.status(406).entity(ex.getMessage()).build();
        }
    }
    //#13847 end
}
