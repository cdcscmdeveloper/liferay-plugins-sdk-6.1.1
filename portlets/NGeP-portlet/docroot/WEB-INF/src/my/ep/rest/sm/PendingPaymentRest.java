package my.ep.rest.sm;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.pending.PendingPaymentResDTO;
import my.ep.persistence.entity.sm.SmPendingProcess;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm") 
public class PendingPaymentRest {

	public static Logger log = Logger.getLogger(PendingPaymentRest.class);

	@GET
	@Path("smpendingprocess") 
	public Response retrievePending(@Context HttpServletRequest req) {
		log.info("Before success calling retrieve rest api....PendingPaymentRestTesting");
		//#36401 - Start
		long startTime = System.currentTimeMillis();
		//#36401 - End
		String remoteHost = req.getRemoteHost();
	    String remoteAddr = req.getRemoteAddr();
	    int remotePort = req.getRemotePort();
	    log.info("Remote host:\t" + remoteHost + " (" + remoteAddr + ":" + remotePort + ")");
		try {
			PendingPaymentResDTO resDTO = SmServiceLookup.getPendingPaymentSO().retrievePendingProcess();
			//#33948 - Start
			//resDTO = SmServiceLookup.getPendingPaymentSO().filterAndUpdateSmPendingProcess(resDTO);
			//#33948 - End
			for (SmPendingProcess smPendingProcess : resDTO.getSmPendingProcessLst()) {
				try {
					SmServiceLookup.getPendingPaymentSO().completePendingProcess(smPendingProcess);
				} catch (Exception e) {
					smPendingProcess.setAttemp(smPendingProcess.getAttemp() + 1);
					smPendingProcess.setErrorMsg(e.getMessage());
					try {
						SmServiceLookup.getPendingPaymentSO().updateFailPending(smPendingProcess);
					} catch (Exception e2) {
						log.error(e2);
					}
				}
			}
			//#36401 - Start
			long endTime = System.currentTimeMillis();
			log.info("#33948 This function takes " + (endTime - startTime) + " milliseconds");
			//#36401 - End
			return Response.status(200).entity("success calling process pending payment !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
