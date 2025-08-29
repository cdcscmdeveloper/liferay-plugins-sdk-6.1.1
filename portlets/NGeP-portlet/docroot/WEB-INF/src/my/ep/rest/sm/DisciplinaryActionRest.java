package my.ep.rest.sm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

/**
 * DisciplinaryAction - Rest
 */
@Path("/sm") 
public class DisciplinaryActionRest {

	public static final Logger log = Logger.getLogger(DisciplinaryActionRest.class);
	
	/**
	 * Update pending DA and release DA
	 * @return
	 */
	@SuppressWarnings("finally")
	@GET
	@Path("smdisciplinaryaction") 
	public Response retrieveDisciplinaryAction() {
		
		log.info("UpdateDisciplinaryActionJob New Function ");
		try {
			//27426 : Disciplinary Action - start

			//26238 : Disciplinary Action -32710- start - 
			//replace recalling or retrieve data that cause other data include in next retrieve data
			//that occur error data has been update from last read.
			//27426 : Disciplinary Action - start 
			
			//NO 1 : find data with end date less than current date to release DA
			SupplierDataMaintenanceResDTO resDTO =  SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction(false);
			releaseDA(resDTO.getSmDisciplinaryActions());
			
			//NO 2: find data pending DA, (data with active txn will update DA after complete all txn)
			resDTO =  SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction();
			updatePendingDA(resDTO.getSmDisciplinaryActions());
			
			//28798: Disciplinary Action - start - 26238 - reopen
			//NO 3 : find data pending Cancel Registration or Blacklist Owner / Director of Company 
			//and get all active for cancel registration and blacklist
			resDTO =  SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveBlacklistCancelDisciplinaryAction();
			updatePendingBlacklistAndCancel(resDTO.getSmDisciplinaryActions());
			
			//31100 start - find data pending suspend register
			//NO 4 : find data pending suspend register , update pending txn for DA suspend and release if DA suspend expired
			resDTO =  SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction(true);
			//26238 : Disciplinary Action -32710- end -
			updatePendingSuspend(resDTO.getSmDisciplinaryActions());
			
			log.info("UpdateDisciplinaryActionJob New Function After For");
			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception ex) {
			log.error("UpdateDisciplinaryActionJob Error="+ex);
			return Response.status(406).entity(ex.getMessage()).build();
		} finally {
			log.info("after close ejb for UpdateDisciplinaryActionJob");
		}
	}
	
	private void releaseDA(List<SmDisciplinaryAction> daList){
		if (EntityHelper.isCollectionEmpty(daList)){
			return;
		}
		log.info("releaseDAList.getSmDisciplinaryActions()=============="+daList.size());
		int i = 0;
		for (SmDisciplinaryAction disc : daList) {
			log.info("releaseDAList. supplier id=============="+disc.getSmSupplier().getSupplierId()); 
			
			if(disc.getSmSupplier() != null){
				disc.getSmSupplier();
				log.info("-----------Bilangan------------------"+i);
				//30784 : Release Disciplinary Action, 31100
				 SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);
				log.info("-----------After------------------"+i);
				i++;
			}										
		}
	}
	
	private void updatePendingDA(List<SmDisciplinaryAction> daList) throws ParseException{
		if (EntityHelper.isCollectionEmpty(daList)){
			return;
		}
		//31384 - start
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date today = formatter.parse(formatter.format(new Date()));
		for (SmDisciplinaryAction pendingDisc : daList) {
			log.info("updateDAList. supplier id=============="+pendingDisc.getSmSupplier().getSupplierId());
				 
			if (pendingDisc.getSmSupplier() != null && pendingDisc.getStartDate().compareTo(today) <= 0) {

				pendingDisc.getSmSupplier();
				pendingDisc.getSmSupplier().getSmAppl();
				// 27426 : Disciplinary Action - end
				UserReqDTO reqDTO2 = new UserReqDTO();
				reqDTO2.setUserId(pendingDisc.getChangedBy());
				UserResDTO resDTO1 = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO2);
				PmUser user = resDTO1.getPmUser();


				UserReqDTO reqDTO3 = new UserReqDTO();
				reqDTO3.setUserIds(new ArrayList<Long>());
				reqDTO3.getUserIds().add(user.getUserId());
				List<PmUserRole> userRoles = SmServiceLookup.getUserSO().retrieveUserRoleListByUserIds(reqDTO3)
							.getUserRoleList();

				 SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(pendingDisc, user, userRoles);
			}	
			//31384 - end
		}	
	}
	
	private void updatePendingBlacklistAndCancel(List<SmDisciplinaryAction> daList){
		if (EntityHelper.isCollectionEmpty(daList)){
			return;
		}
		Date today = new Date(); 
		for(SmDisciplinaryAction da: daList){
			log.info("updateBCDAList. supplier id=============="+da.getSmSupplier().getSupplierId()); 
			if(da.getSmSupplier() == null){
				continue;
			}

			if(da.getStartDate().compareTo(today) <= 0){
				if(da.isPendingBlacklist() || da.isPendingCancel()){
					da.getSmSupplier();
					da.getSmSupplier().getSmAppl();
							
					UserReqDTO reqDTO2 = new UserReqDTO();
					reqDTO2.setUserId(da.getChangedBy());
					UserResDTO resDTO1 = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO2);
					PmUser user =  resDTO1.getPmUser();
							
					UserReqDTO reqDTO3 = new UserReqDTO();
					reqDTO3.setUserIds(new ArrayList<Long>());
					reqDTO3.getUserIds().add(user.getUserId());
					List<PmUserRole> userRoles = SmServiceLookup.getUserSO().retrieveUserRoleListByUserIds(reqDTO3).getUserRoleList();
							
					 SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(da, user, userRoles);
					
					//31100,30878 start
					Long disciplinaryActionId = da.getDisciplinaryActionId();
				
					if(disciplinaryActionId != null){
						SmDisciplinaryAction smDA =  SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);
						if( (smDA.isBlacklistOwner() || smDA.isCancelRegistration()) 
								&& (!smDA.isPendingCancel() && !smDA.isPendingBlacklist() )){
							smDA.getSmSupplier();
							 SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(smDA);	
						}
					}
					//31100,30878 end
				}
				//29597 : Disciplinary Action - 26238
				if( (da.isBlacklistOwner() || da.isCancelRegistration()) && 
						(!da.isPendingCancel() && !da.isPendingBlacklist() )){
					da.getSmSupplier();
					 SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(da);	
				}	
			}
		}
	}
	
	private void updatePendingSuspend(List<SmDisciplinaryAction> daList) throws ParseException{
		if (EntityHelper.isCollectionEmpty(daList)){
			return;
		}
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		Date todayWithZeroTime = formatter.parse(formatter.format(today));

		for (SmDisciplinaryAction da : daList) {
			log.info("suspendList. supplier id==============" + da.getSmSupplier().getSupplierId());
			//33248 - reopen 31/05: Disciplinary Action - 26238 - 
			if (da.getSmSupplier() == null || da.isBlacklistOwner() || da.isCancelRegistration()) {
				continue;
			}
			if(da.getStartDate().compareTo(today) <= 0){
				// reactive account if DA suspend register is expired
				//30784 : Release Disciplinary Action 
				if (da.getEndDate().compareTo(todayWithZeroTime) < 0 && !da.isPendingSuspend()) {
					RegistrationReqDTO reqDTO = new RegistrationReqDTO();

					log.info("-----------suspend registration-----------------"+ da.isSuspendRegistration());
					PersonnelReqDTO reqPersonnelDTO = new PersonnelReqDTO();
					reqPersonnelDTO.setSmSupplierId(da.getSmSupplier().getSupplierId());

					PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierIdForDA(reqPersonnelDTO);

					reqDTO.setSmSupp(da.getSmSupplier());
					reqDTO.setDisciplinaryAction(true); //30784: disciplinary action - 26238
					reqDTO.getSmSupp().getSmAppl().setSmPersonnels(personnelResDTO.getSmPersonnelList());
					try {
						SmServiceLookup.getRegistrationSO().reactivateBasicAccount(reqDTO);
						 SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(da);							
					} catch (Exception ex) {
						log.error("UpdateDisciplinaryActionJob Error="+ex);
					}
				} else {
					//32558: disciplinary action - 26238 Start 
					da.getSmSupplier();
					da.getSmSupplier().getSmAppl();

					UserReqDTO reqDTO2 = new UserReqDTO();
					reqDTO2.setUserId(da.getChangedBy());
					UserResDTO resDTO1 = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO2);
					PmUser user =  resDTO1.getPmUser();								
							
					UserReqDTO reqDTO3 = new UserReqDTO();
					reqDTO3.setUserIds(new ArrayList<Long>());
					reqDTO3.getUserIds().add(user.getUserId());
					List<PmUserRole> userRoles = SmServiceLookup.getUserSO().retrieveUserRoleListByUserIds(reqDTO3).getUserRoleList();

					//26238 start
					// pending that expired
					if(da.getEndDate().compareTo(todayWithZeroTime) < 0){			
						 SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryActionForExpiredSuspend(da,user, userRoles);		
					}else{
						// pending that not expired
						 SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(da,user, userRoles);

						Long disciplinaryActionId = da.getDisciplinaryActionId();
						// release DA if pending is completed
						if (disciplinaryActionId != null) {
							SmDisciplinaryAction completedPendingSmDa = 
									 SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);

							//26238 - 32558 - 32710 -  rollback code is for imposed suspen when supplier finish pending txn
							if (completedPendingSmDa.isSuspendRegistration() && !completedPendingSmDa.isPendingSuspend()) {
								if(formatter.format(completedPendingSmDa.getStartDate()).compareTo(formatter.format(today)) == 0){
									completedPendingSmDa.setFutureImposed(true);
								} else {
									completedPendingSmDa.setFutureImposed(false);
								}
								completedPendingSmDa.getSmSupplier();
								//38402 - START
								if (da.getEndDate().compareTo(todayWithZeroTime) < 0 || completedPendingSmDa.isFutureImposed()) {
									SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(completedPendingSmDa);
								}
								//38402 - END
							}
						}
					}
					//26238 end
				} // 26238 end future date for suspend registration	
				// 32558: disciplinary action - 26238 end
			}
			//26238
		}
	}
}
