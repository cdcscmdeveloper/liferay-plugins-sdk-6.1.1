package my.ep.rest.egpa;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;

import my.ep.biz.service.intg.egpa.IEgpaServiceSO;
import my.ep.biz.service.intg.sm.IPaymentReconcileServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

@Path("/egpa")
public class PTIInboundRest {
	
		public static final Logger log = Logger.getLogger(PTIInboundRest.class);
		
		@GET
		@Path("/ptiOutboundFile")
		public String ptiOutboundFile() throws IOException {
			IEgpaServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IEgpaServiceSO.class);
			service.executePTIOutboundPackage();
			service.generatePTIOutboundFile();
			return "success call ptiOutboundFile";
		}
		
		@GET
		@Path("/ptiInboundFile/retry")
		public String ptiInboundFile() throws IOException {
			IEgpaServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IEgpaServiceSO.class);
			service.executePtiInbound();
			return "success call ptiInboundFile/retry";
		}
		
		@GET
		@Path("/trigger-ptiInboundTest")
		public String triggerPtiInbound() {
			log.info("[trigger-ptiInboundTest] THIS REST IS JUST FOR TESTING PURPOSE!");
			
			IEgpaServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IEgpaServiceSO.class);
			String transId = service.executePtiInbound();
			
			return "Done trigger-ptiInboundTest! transId : "+transId;
		}
		
		@GET
		@Path("/ptiInboundTest")
		public String ptiInbound(@QueryParam("transId") String transId, @QueryParam("fileName") String fileName) throws IOException {
			log.info("[PTI Inbound Test] THIS REST IS JUST FOR TESTING PURPOSE!");
			IEgpaServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IEgpaServiceSO.class);
			service.egpaPTIInboundRest(transId, fileName);
			return "Done PTI Inbound Test! transId : " + transId + " fileName : "+fileName;
		}
}
