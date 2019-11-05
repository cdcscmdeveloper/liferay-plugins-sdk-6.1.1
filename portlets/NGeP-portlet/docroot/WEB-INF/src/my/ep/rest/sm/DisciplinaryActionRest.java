package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.pending.PendingPaymentResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmDisciplinaryAction_;
import my.ep.persistence.entity.sm.SmPendingProcess;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm") 
public class DisciplinaryActionRest {

	public static Logger log = Logger.getLogger(DisciplinaryActionRest.class);

	@GET
	@Path("smdisciplinaryaction") 
	public Response retrieveDisciplinaryAction() {
		log.info("Before success calling retrieve rest api....");
		try {
			SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction();
			for (SmDisciplinaryAction disc : resDTO.getSmDisciplinaryActions()) {
				SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);	
			}
			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
