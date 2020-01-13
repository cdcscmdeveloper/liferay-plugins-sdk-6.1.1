package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;

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
			SupplierDataMaintenanceResDTO activeDisc = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction();
			if(resDTO.getSmDisciplinaryActions() != null 
					&& resDTO.getSmDisciplinaryActions().size() > 0){
				for (SmDisciplinaryAction disc : resDTO.getSmDisciplinaryActions()) {
					SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);	
				}
			}
			
			if(activeDisc.getSmDisciplinaryActions() != null
					&& activeDisc.getSmDisciplinaryActions().size() > 0){
				for (SmDisciplinaryAction pendingDisc : activeDisc.getSmDisciplinaryActions()) {
					PmUser user = SmCommonUtil.getUserByUserId(pendingDisc.getChangedBy());
					List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user
							.getUserId());
					SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(pendingDisc, user, userRoles);
				}
			}
			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
