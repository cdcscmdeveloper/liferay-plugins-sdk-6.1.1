package my.ep.rest.fl.yep;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import my.ep.dto.fl.FlYepResDTO;
import my.ep.dto.fl.FlYepRestReqDTO;
import my.ep.web.util.fl.yep.FlYepServiceLookup;

import org.apache.log4j.Logger;

/**
 * Rest file for yep process
 * 
 * 03/12/2025
 * @author razin
 *
 */
@Path("/yep")
public class FulfilmentYepRest {

	/**
	 * logger setup
	 */
	public static final Logger log = Logger.getLogger(FulfilmentYepRest.class);
	String keyPass = "testkey";
	
	
	/**
	 * rest for yep manual terminate to call bpm so terminate
	 * 
	 * @param reqDTO
	 * @return Response
	 */
	@POST
	@Path("/terminatetask")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response terminateTaskYEP(FlYepRestReqDTO reqDTO) {
		try {
			log.info("Initiate Process:terminateTaskYEP: docNo: " + reqDTO.getDocNo() + "|phaseNo: "
					+ reqDTO.getPhaseNo() + "|limit: " + reqDTO.getLimit() + "|isBatch: " + reqDTO.isBatch());
			long startTime = System.currentTimeMillis();

			if (reqDTO.getKeyPass() == null || !reqDTO.getKeyPass().equals(keyPass)) {
				return Response.status(Response.Status.UNAUTHORIZED).entity("keyPass invalid!").build();
			}
			FlYepResDTO resDTO = FlYepServiceLookup.getFlYepSO().terminateTaskManual(reqDTO.getDocNo(),
					reqDTO.getPhaseNo(), reqDTO.getLimit(), reqDTO.isBatch());

			long endTime = System.currentTimeMillis();
			log.info("End Process:terminateTaskYEP");

			if (resDTO.isSuccessful()) {
				return Response
						.status(Response.Status.OK)
						.entity("Success calling terminateTaskYEP!! Run for " + (endTime - startTime) + " milliseconds")
						.build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Fail terminateTaskYEP!").build();
			}

		} catch (Exception e) {
			log.error(e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
