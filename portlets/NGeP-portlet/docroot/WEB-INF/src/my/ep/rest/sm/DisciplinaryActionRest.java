package my.ep.rest.sm;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.scheduler.SmSchedulerReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmMofAccount;
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
			//27426 : Disciplinary Action - start
			//find data with end date less than current date
			SupplierDataMaintenanceResDTO releaseDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction();
			//find data pending DA, (data with active txn will update DA after complete all txn)
			SupplierDataMaintenanceResDTO updateDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction();
			
			if(releaseDAList.getSmDisciplinaryActions() != null
					&& releaseDAList.getSmDisciplinaryActions().size() > 0){
				for (SmDisciplinaryAction disc : releaseDAList.getSmDisciplinaryActions()) {
					if(disc != null
							&& disc.getSmSupplier() != null){
						disc.getSmSupplier();
						SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);	
					}										
				}
			}
			
			if(updateDAList != null
					&& updateDAList.getSmDisciplinaryActions() != null
					&& updateDAList.getSmDisciplinaryActions().size() > 0){
				for (SmDisciplinaryAction pendingDisc : updateDAList.getSmDisciplinaryActions()) {
					pendingDisc.getSmSupplier();
					pendingDisc.getSmSupplier().getSmAppl(); 
					//27426 : Disciplinary Action - end
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
