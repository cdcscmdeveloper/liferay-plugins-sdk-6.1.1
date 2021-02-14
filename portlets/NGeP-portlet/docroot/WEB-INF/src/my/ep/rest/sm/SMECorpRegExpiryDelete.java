package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.persistence.entity.sm.SmSmeSupplier;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm")
public class SMECorpRegExpiryDelete {

	public static final Logger log = Logger.getLogger(SMECorpRegExpiryDelete.class);

	@GET
	@Path("smecorpregexpirydelete")
	public Response smeCorpRegExpiryDelete() {
		log.info("Before success calling smeCorpRegExpiryDelete rest api....");
		try {
			deleteExpiredSme();
			return Response.status(200).entity("success calling smeCorpRegExpiryDelete!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}

	private void deleteExpiredSme() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredSME();
		List<SmSmeSupplier> smeSupplierList = resDTO.getExpiredSmeSupplierList();
		log.info("**********smeCorpRegExpiryDelete - Start************");
		log.info("List of Expired SME Supplier == " + smeSupplierList.size());
		for (SmSmeSupplier smeSupplier : smeSupplierList) {
			SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
			schedulerReqDTO.setSmeSupplier(smeSupplier);
			schedulerReqDTO.setSmSupplier(smeSupplier.getSmSupplier());
			try {
				reqSO.deleteExpiredSme(schedulerReqDTO);
			} catch (Exception ex) {
				log.info("deleteExpiredSme ERROR :: " + ex.getMessage());
			}
		}
	}

}
