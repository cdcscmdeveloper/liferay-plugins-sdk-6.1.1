package my.ep.rest.sm;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
			//27426 : Disciplinary Action - start
			//find data with end date less than current date
			SupplierDataMaintenanceResDTO releaseDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction();
			//find data pending DA, (data with active txn will update DA after complete all txn)
			SupplierDataMaintenanceResDTO updateDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction();
			
			//26238 - Disciplinary Action - 28798 - end
			//find data pending Cancel Registration or Blacklist Owner / Director of Company
			SupplierDataMaintenanceResDTO updateBCDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveBlacklistCancelDisciplinaryAction();
			
			//remove duplicated data
			if(updateBCDAList != null
					&& updateBCDAList.getSmDisciplinaryActions() != null
					&& updateBCDAList.getSmDisciplinaryActions().size() > 0){
				releaseDAList.getSmDisciplinaryActions().removeAll(updateBCDAList.getSmDisciplinaryActions());
				updateDAList.getSmDisciplinaryActions().removeAll(updateBCDAList.getSmDisciplinaryActions());
				updateDAList.getSmDisciplinaryActions().addAll(updateBCDAList.getSmDisciplinaryActions());
				
			}
			//26238 - Disciplinary Action - 28798 - end
			
			if(releaseDAList.getSmDisciplinaryActions() != null
					&& releaseDAList.getSmDisciplinaryActions().size() > 0){
				log.info("releaseDAList.getSmDisciplinaryActions()=============="+releaseDAList.getSmDisciplinaryActions().size());
				int i = 0;
				for (SmDisciplinaryAction disc : releaseDAList.getSmDisciplinaryActions()) {
					if(disc != null
							&& disc.getSmSupplier() != null){
						disc.getSmSupplier();
						log.info("-----------Bilangan------------------"+i);
						SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);	
						log.info("-----------After------------------"+i);
						i++;
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
