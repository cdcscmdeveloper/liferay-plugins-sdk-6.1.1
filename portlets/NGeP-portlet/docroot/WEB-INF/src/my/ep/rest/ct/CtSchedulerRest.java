package my.ep.rest.ct;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@Path("/ct")
public class CtSchedulerRest {
	
	public static Logger log = Logger.getLogger(ContractPerformanceBondRest.class);
	
	//#32911 start
    @GET
    @Path("/callCtStoredProcedureService/")
    public Response callCtStoredProcedure() {
        log.info("Before success calling callCtStoredProcedureService rest api....");
        ICtSchedulerSO tmpSO = (ICtSchedulerSO) BusinessDelegate.getInstance().getServiceObject(
                ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);
        try {
            tmpSO.runCtCloneContractVersion();
            return Response.status(200).entity("success calling callCtStoredProcedureService!!").build();
        } catch (Exception ex) {
            log.info("callCtStoredProcedureService -  Exception>>>> " + ex.getMessage() );
            return Response.status(406).entity(ex.getMessage()).build();
        }
    }
    
  //#32911 end
   
  //#32910 start  
    @GET
    @Path("/callBpmSchedulerService/")
    public Response callBpmSchedulerService() {
        log.info("Before success calling callBpmSchedulerService rest api....");
        ICtSchedulerSO tmpSO = (ICtSchedulerSO) BusinessDelegate.getInstance().getServiceObject(
                ModuleCodeConst.CONTRACT_MANAGEMENT, ICtSchedulerSO.class);
        try {
            tmpSO.runCtCreateCloningTaskJob();
            return Response.status(200).entity("success calling callBpmSchedulerService!!").build();
        } catch (Exception ex) {
            log.info("callBpmSchedulerService -  Exception>>>> " + ex.getMessage() );
            return Response.status(406).entity(ex.getMessage()).build();
        }
    }
    
  //#32910 end

}
