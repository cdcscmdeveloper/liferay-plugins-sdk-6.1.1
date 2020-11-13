package my.ep.rest.pm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.intg.gfmas.IGfmasServiceSO;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@Path("/pm")
public class GFmasTest {
	public static Logger log = Logger.getLogger(GFmasTest.class);
	
	@GET
	@Path("igmastest")
	public Response igmasinboundtest() {
		IGfmasServiceSO gfmasServiceSO = (IGfmasServiceSO) BusinessDelegate.getInstance().getServiceObject("intg",IGfmasServiceSO.class);
		//IGfmasServiceSO gfmasServiceSO1 = (IGfmasServiceSO) EpEjbRemoteBusinessDelegate.getInstance().getServiceObject("intg", IGfmasServiceSO.class);
		gfmasServiceSO.testpaymentGfmasInbound();
		
		return Response.status(200).entity("success").build();
		
	}
}
