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
			SupplierDataMaintenanceResDTO releaseDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction();//28798: Disciplinary Action - remove blacklist and cancel registration
			//find data pending DA, (data with active txn will update DA after complete all txn)
			SupplierDataMaintenanceResDTO updateDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction(); //28798: Disciplinary Action - remove blacklist and cancel registration
			
			//26238 - Disciplinary Action - 28798 - end
			//find data pending Cancel Registration or Blacklist Owner / Director of Company
			SupplierDataMaintenanceResDTO updateBCDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveBlacklistCancelDisciplinaryAction();//28798: Disciplinary Action - get all active for cancel registration and blacklist
			
			//26238 - Disciplinary Action - 28798 - end
			
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
			
			//28798: Disciplinary Action - start - 26238 - reopen
			if(updateBCDAList != null && updateBCDAList.getSmDisciplinaryActions() != null && updateBCDAList.getSmDisciplinaryActions().size() > 0){
				Date today = new Date(); 
				for(SmDisciplinaryAction cancelRegBlacklist: updateBCDAList.getSmDisciplinaryActions()){
					if(cancelRegBlacklist.getStartDate().compareTo(today) <= 0){
						if(cancelRegBlacklist.isPendingBlacklist() || cancelRegBlacklist.isPendingCancel()){
							cancelRegBlacklist.getSmSupplier();
							cancelRegBlacklist.getSmSupplier().getSmAppl(); 
							PmUser user = SmCommonUtil.getUserByUserId(cancelRegBlacklist.getChangedBy());
							List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
							SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(cancelRegBlacklist, user, userRoles);
						}
						if( (cancelRegBlacklist.isBlacklistOwner() || cancelRegBlacklist.isCancelRegistration()) && (!cancelRegBlacklist.isPendingCancel() && !cancelRegBlacklist.isPendingBlacklist() )){//29597 : Disciplinary Action - 26238
							cancelRegBlacklist.getSmSupplier();
							SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(cancelRegBlacklist);	
						}
					}
				}
			}
			//28798: Disciplinary Action - end - 26238 - reopen
			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
