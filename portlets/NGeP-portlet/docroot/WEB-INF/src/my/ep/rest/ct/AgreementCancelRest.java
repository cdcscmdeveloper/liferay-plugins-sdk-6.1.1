package my.ep.rest.ct;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import my.ep.dto.ct.agreement.ContractAgreementReqDTO;
import my.ep.dto.ct.agreement.ContractAgreementResDTO;
import my.ep.persistence.entity.ct.agreement.CtAgreementCancelTemp;

import my.ep.web.util.ct.CtServiceLookup;

import org.apache.log4j.Logger;

@Path("/ct")
public class AgreementCancelRest {

	public static Logger log = Logger.getLogger(AgreementCancelRest.class);

	@GET
	@Path("/initiatetask/{limit}/{batch_no}")
	public Response retrievePendingInitiateTask(@PathParam("limit") Long limit, @PathParam("batch_no") String batchNo) {
		log.info("Starting retrievePendingInitiateTask for batch: " + batchNo);

		long startTime = System.currentTimeMillis();

		try {
			ContractAgreementReqDTO request = new ContractAgreementReqDTO();
			request.setLimit(limit);
			request.setBatchNo(batchNo);

			ContractAgreementResDTO resDTO = CtServiceLookup.getAgreementAutoCancelSO().initiateAgreementCancellationTask(request);

			if (resDTO != null && resDTO.getCtAgreementCancelTemp() != null) {
				for (CtAgreementCancelTemp task : resDTO.getCtAgreementCancelTemp()) {
					try {
						log.info("Processing Task: " + task.getDocNo());

						if (task.getCreatorBy() == null && task.getAcquiredBy() == null) {
							log.warn("Skipping Task due to missing user data: " + task.getDocNo());
							continue;
						}

						CtServiceLookup.getAgreementAutoCancelSO().initiateAgreementTask(task);
					} catch (Exception e) {
						log.error("Error processing task: " + task.getDocNo(), e);
					}
				}
			} else {
				log.info("No pending tasks to process.");
			}

			long endTime = System.currentTimeMillis();
			log.info("Execution time: " + (endTime - startTime) + " ms");

			return Response.ok("Success calling retrievePendingInitiateTask!!").build();
		} catch (Exception e) {
			log.error("Error in retrievePendingInitiateTask", e);
			return Response.status(Response.Status.NOT_ACCEPTABLE).entity(e.getMessage()).build();
		}
	}

	@GET
	@Path("/updateprogress/{batch_no}")
	public Response updateProgressStatusTask(@PathParam("batch_no") String batch_no) {
		log.info("Before success calling updateProgressStatusTask retrieve rest api....");

		long startTime = System.currentTimeMillis();

		try {
			ContractAgreementReqDTO newReqDTO = new ContractAgreementReqDTO();
			newReqDTO.setBatchNo(batch_no);

			CtServiceLookup.getAgreementAutoCancelSO().updateProgressStatusTask(newReqDTO);

			long endTime = System.currentTimeMillis();
			log.info("#This function takes " + (endTime - startTime) + " milliseconds");
			return Response.status(200).entity("Success calling updateProgressStatusTask!!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}

}
