package my.ep.rest.sm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
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
		boolean enter  = false;
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
			
			//31100 start - find data pending suspend register
			SupplierDataMaintenanceResDTO updateSuspendList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveSuspendDisciplinaryAction();
			//31100 end
			
			if(releaseDAList.getSmDisciplinaryActions() != null
					&& releaseDAList.getSmDisciplinaryActions().size() > 0 && enter){
				log.info("releaseDAList.getSmDisciplinaryActions()=============="+releaseDAList.getSmDisciplinaryActions().size());
				int i = 0;
				for (SmDisciplinaryAction disc : releaseDAList.getSmDisciplinaryActions()) {
					log.info("releaseDAList. supplier id=============="+disc.getSmSupplier().getSupplierId()); 
					 
					if(disc != null
							&& disc.getSmSupplier() != null){
						disc.getSmSupplier();
						log.info("-----------Bilangan------------------"+i);
						
						//30784 : Release Disciplinary Action 
						//31100 start
						SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);
						//31100 end 
						
						log.info("-----------After------------------"+i);
						i++;
					}										
				}
			}
			
			if(updateDAList != null
					&& updateDAList.getSmDisciplinaryActions() != null
					&& updateDAList.getSmDisciplinaryActions().size() > 0){
				
				//31384 - start
				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date today = formatter.parse(formatter.format(new Date()));
				
				for (SmDisciplinaryAction pendingDisc : updateDAList.getSmDisciplinaryActions()) {
					log.info("updateDAList. supplier id=============="+pendingDisc.getSmSupplier().getSupplierId());
					 
					if (pendingDisc != null && pendingDisc.getSmSupplier() != null) {

						if (pendingDisc.getStartDate().compareTo(today) <= 0) {

							pendingDisc.getSmSupplier();
							pendingDisc.getSmSupplier().getSmAppl();
							// 27426 : Disciplinary Action - end
							PmUser user = SmCommonUtil.getUserByUserId(pendingDisc.getChangedBy());
							List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
							SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(pendingDisc,
									user, userRoles);
						}
					}
				}
				//31384 - end
			}
			
			//28798: Disciplinary Action - start - 26238 - reopen
			if(updateBCDAList != null && updateBCDAList.getSmDisciplinaryActions() != null && updateBCDAList.getSmDisciplinaryActions().size() > 0){
				Date today = new Date(); 
				for(SmDisciplinaryAction cancelRegBlacklist: updateBCDAList.getSmDisciplinaryActions()){
					log.info("updateBCDAList. supplier id=============="+cancelRegBlacklist.getSmSupplier().getSupplierId()); 
					if(cancelRegBlacklist != null
							&& cancelRegBlacklist.getSmSupplier() != null){
												
						if(cancelRegBlacklist.getStartDate().compareTo(today) <= 0){
							if(cancelRegBlacklist.isPendingBlacklist() || cancelRegBlacklist.isPendingCancel()){
								cancelRegBlacklist.getSmSupplier();
								cancelRegBlacklist.getSmSupplier().getSmAppl(); 
								PmUser user = SmCommonUtil.getUserByUserId(cancelRegBlacklist.getChangedBy());
								List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
								//30878 to check isPendingCanel / isPendingBlack list is updated
								SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(cancelRegBlacklist, user, userRoles);
								
								//31100,30878 start
								Long disciplinaryActionId = cancelRegBlacklist.getDisciplinaryActionId();
								
								if(disciplinaryActionId != null){
									SmDisciplinaryAction smDA = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);
									if( (smDA.isBlacklistOwner() || smDA.isCancelRegistration()) 
											&& (!smDA.isPendingCancel() && !smDA.isPendingBlacklist())){
										smDA.getSmSupplier();
										SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(smDA);	
									}
								}
								//31100,30878 end
							}
							
							if( (cancelRegBlacklist.isBlacklistOwner() || cancelRegBlacklist.isCancelRegistration()) 
									&& (!cancelRegBlacklist.isPendingCancel() && !cancelRegBlacklist.isPendingBlacklist())){//29597 : Disciplinary Action - 26238
								cancelRegBlacklist.getSmSupplier();
								SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(cancelRegBlacklist);	
							}
							
						}
					}
				}
			}
			//28798: Disciplinary Action - end - 26238 - reopen
			
			//31100 start
			if (updateSuspendList != null && updateSuspendList.getSmDisciplinaryActions() != null
					&& updateSuspendList.getSmDisciplinaryActions().size() > 0) {

				DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				Date todayWithZeroTime = formatter.parse(formatter.format(today));

				for (SmDisciplinaryAction suspendList : updateSuspendList.getSmDisciplinaryActions()) {

					log.info("suspendList. supplier id==============" + suspendList.getSmSupplier().getSupplierId());

					if (suspendList != null && suspendList.getSmSupplier() != null) {

						//26238
						if(suspendList.getStartDate().compareTo(today) <= 0){
							// reactive account if DA suspend register is expired
							// 30784 : Release Disciplinary Action 
							if (suspendList.getEndDate().compareTo(todayWithZeroTime) < 0 && !suspendList.isPendingSuspend()) {
								RegistrationReqDTO reqDTO = new RegistrationReqDTO();

								log.info("-----------suspend registration-----------------"+ suspendList.isSuspendRegistration());
								PersonnelReqDTO reqPersonnelDTO = new PersonnelReqDTO();
								reqPersonnelDTO.setSmSupplierId(suspendList.getSmSupplier().getSupplierId());

								// PersonnelResDTO personnelResDTO =
								// SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierId(reqPersonnelDTO);
								PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierIdForDA(reqPersonnelDTO);

								reqDTO.setSmSupp(suspendList.getSmSupplier());
								reqDTO.setDisciplinaryAction(true); //30784: disciplinary action - 26238
								reqDTO.getSmSupp().getSmAppl().setSmPersonnels(personnelResDTO.getSmPersonnelList());
								try {
									RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO().reactivateBasicAccount(reqDTO);

									SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(suspendList);
									/*
									 * SmCommonUtil.redirectToPage(
									 * SmApplicationUtil.getApplicationErrorPage(),
									 * MessageConst.INFO_721029, new String[]
									 * {resDTO
									 * .getBasicSupplierAdmin().getPmUser().getLoginId
									 * () }, FacesMessage.SEVERITY_INFO);
									 */
								} catch (Exception ex) {
									throw new EpException(ex.getMessage());
								}
							} else { // 26238 start future date for suspend registration								
								suspendList.getSmSupplier();
								suspendList.getSmSupplier().getSmAppl();
								
								PmUser user = SmCommonUtil.getUserByUserId(suspendList.getChangedBy());
								List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
								SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(suspendList,user, userRoles);

								Long disciplinaryActionId = suspendList.getDisciplinaryActionId();

								// release DA if pending is completed
								if (disciplinaryActionId != null) {
									SmDisciplinaryAction completedPendingSmDa = SmServiceLookup.getSupplierDataMaintenanceSO()
											.retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);
									
									if (completedPendingSmDa.isSuspendRegistration() && !completedPendingSmDa.isPendingSuspend()) {
										completedPendingSmDa.getSmSupplier();
										SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(completedPendingSmDa);
									}
								}
							} // 26238 end future date for suspend registration	

							// check pending suspend task, if completed = release DA
							if (suspendList.isPendingSuspend()) {
								
								suspendList.getSmSupplier();
								suspendList.getSmSupplier().getSmAppl();
								
								PmUser user = SmCommonUtil.getUserByUserId(suspendList.getChangedBy());
								List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
								
								//26238 start
								// pending that expired
								if(suspendList.getEndDate().compareTo(todayWithZeroTime) < 0 ){
								
									SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryActionForExpiredSuspend(suspendList,user, userRoles);
									
								}else{
									// pending that not expired
									SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(suspendList,user, userRoles);

									Long disciplinaryActionId = suspendList.getDisciplinaryActionId();

									// release DA if pending is completed
									if (disciplinaryActionId != null) {
										SmDisciplinaryAction completedPendingSmDa = SmServiceLookup.getSupplierDataMaintenanceSO()
												.retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);
										
										if (completedPendingSmDa.isSuspendRegistration() && !completedPendingSmDa.isPendingSuspend()) {
											completedPendingSmDa.getSmSupplier();
											SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(completedPendingSmDa);
										}
									}
								}
								//26238 end
							}
						}
						//26238
					}
				}
			}
			//31100 end
			
			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
