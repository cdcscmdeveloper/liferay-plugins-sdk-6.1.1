package my.ep.rest.sm;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
	public Response retrievePending() {
		log.info("Before success calling retrieve rest api....");
		// #36401 - Start
		long startTime = System.currentTimeMillis();
		// #36401 - End
		try {
			PendingPaymentResDTO resDTO = SmServiceLookup.getPendingPaymentSO().retrievePendingProcess();
			// #36401 - Start
			resDTO = SmServiceLookup.getPendingPaymentSO().updateSmPendingProcessIsPickup(resDTO);
			// #36401 - End
			for (SmPendingProcess smPendingProcess : resDTO.getSmPendingProcessLst()) {
				// #36401 - Start
				log.info("#36401 :: logger :: " + smPendingProcess.getApplId() + "Attempt :: "
						+ smPendingProcess.getAttemp() + "Err Msg :: " + smPendingProcess.getErrorMsg());
				// #36401 - End
				try {
					SmServiceLookup.getPendingPaymentSO().completePendingProcess(smPendingProcess);
				} catch (Exception e) {
					smPendingProcess.setAttemp(smPendingProcess.getAttemp() + 1);
					// #36401 - Start
					smPendingProcess.setPickup(false);
					// #36401 - End
					smPendingProcess.setErrorMsg(e.getMessage());
					try {
						SmServiceLookup.getPendingPaymentSO().updateFailPending(smPendingProcess);
					} catch (Exception e2) {
						log.error(e2);
					}
				}
			}
			// #36401 - Start
			long endTime = System.currentTimeMillis();
			log.info("#36401 This function takes " + (endTime - startTime) + " milliseconds");
			// #36401 - End
			return Response.status(200).entity("success calling process pending payment !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
