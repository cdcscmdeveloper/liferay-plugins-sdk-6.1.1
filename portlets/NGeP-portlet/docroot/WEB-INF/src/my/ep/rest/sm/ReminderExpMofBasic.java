package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.biz.service.sm.scheduler.ISmSchedulerSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.scheduler.SmSchedulerResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm")
public class ReminderExpMofBasic {

public static final Logger log = Logger.getLogger(ReminderExpMofBasic.class);
	
	@GET
	@Path("reminderexpmofbasic")
	public Response reminderExpMofBasic() {
		log.info("Before success calling reminderExpMofBasic rest api....");
		try {
			reminderExpiredMofToDelete();
			reminderInactiveBasicToDelete();

			return Response.status(200).entity("success calling reminderExpMofBasic!!").build();
		} catch (Exception ex) {
			return Response.status(406).entity(ex.getMessage()).build();
		}
	}

	
	private void reminderExpiredMofToDelete() {
		ISmSchedulerSO reqSO = SmServiceLookup.getSmSchedulerSO();
		SmSchedulerResDTO resDTO = reqSO.retrieveExpiredMofReminderDeletion();
		List<SmMofAccount> mofAccountLs = resDTO.getMofAccountLs();
		log.info("**********reminderExpiredMofToDelete - Start************");
		log.info("List of Reminder Mof == " + mofAccountLs.size());
		for (SmMofAccount mofAccount : mofAccountLs) {
			SmSchedulerReqDTO schedulerReqDTO = new SmSchedulerReqDTO();
			schedulerReqDTO.setMofAccount(mofAccount);
			try {
				reqSO.sendEmailReminderMofToDeleteToSupplier(schedulerReqDTO);
			} catch (Exception ex){
				log.info("sendEmailExpiredMOFToSupplier ERROR :: " +ex.getMessage());
			}
		}
	}
	
	private void reminderInactiveBasicToDelete(){
		ISupplierSearchSO reqSO = SmServiceLookup.getSupplierSearchSO();
		SupplierSearchResDTO resDTO = reqSO.retrieveInactiveBasicReminderDeletion();
		List<SmSupplier> basicAccountLs = resDTO.getSmSuppliers();
		log.info("**************reminderBasicToDelete - Start***********");
		log.info("List of Reminder Basic == " + basicAccountLs.size());
		for(SmSupplier smSupplier : basicAccountLs){
			SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
			reqDTO.setSmSupplier(smSupplier);
			try{
				reqSO.sendEmailReminderBasicToDelete(reqDTO);
			}catch(Exception ex){
				log.info("sendEmailReminderBasicToDelete ERROR :: " + ex.getMessage());
			}
		}
	}

}
