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
	public Response retrievePendingInitiateTask(@PathParam("limit") Long limit, @PathParam("batch_no") String batch_no) {
	    log.info("Before success calling retrieve rest api....");

	    long startTime = System.currentTimeMillis();

	    try {
	        ContractAgreementReqDTO newReqDTO = new ContractAgreementReqDTO();
	        newReqDTO.setLimit(limit);
	        newReqDTO.setBatchNo(batch_no); 

	        ContractAgreementResDTO resDTO = CtServiceLookup.getAgreementAutoCancelSO()
	            .initiateAgreementCancellationTask(newReqDTO);

	        if (resDTO.getCtAgreementCancelTemp() != null) {
	            for (CtAgreementCancelTemp ctPendingProcess : resDTO.getCtAgreementCancelTemp()) {
	                try {
	                	log.info("PUTRA Go to InitiateNewTask ::: "+ctPendingProcess.getDocNo());
	                	if(ctPendingProcess.getCreatorBy() == null && ctPendingProcess.getAcquiredBy() == null){
	                		log.info("PUTRA Go to InitiateNewTask SKIP USER IS NULL");
	                		continue; // Skip this loop and move to the next loop
	                	}
	                	log.info("PUTRA Go to InitiateNewTask Start BEGIN");
	                    CtServiceLookup.getAgreementAutoCancelSO().initiateAgreementTask(ctPendingProcess);
	                } catch (Exception e) {
	                    log.error(e);
	                }
	            }
	        }

	        long endTime = System.currentTimeMillis();
	        log.info("#This function takes " + (endTime - startTime) + " milliseconds");
	        return Response.status(200).entity("Success calling retrievePendingInitiateTask!!").build();
	    } catch (Exception e) {
	        return Response.status(406).entity(e.getMessage()).build();
	    }
	}

}
