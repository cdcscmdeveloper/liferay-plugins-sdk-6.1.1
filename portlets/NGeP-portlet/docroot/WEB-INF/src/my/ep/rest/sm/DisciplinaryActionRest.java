package my.ep.rest.sm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.util.sm.helper.SmCommonUtil;
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
	@GET
	@Path("smdisciplinaryaction") 
	public Response retrieveDisciplinaryAction() {

		log.info("Before success calling retrieve rest api....");
		try {
			// 27426 : Disciplinary Action - start

			// 26238 : Disciplinary Action -32710- start - replace recalling or
			// retrieve data that cause other data include in next retrieve data
			// that occur error data has been update from last read.
			releaseDA();
			updatePendingDA();
			
			// 28798: Disciplinary Action - start - 26238 - reopen
			SupplierDataMaintenanceResDTO updateBCDAList = SmServiceLookup.getSupplierDataMaintenanceSO()
					.retrieveActiveBlacklistCancelDisciplinaryAction();
			if (!EntityHelper.isCollectionEmpty(updateBCDAList.getSmDisciplinaryActions())) {
				updateBlacklistCancelDA(updateBCDAList.getSmDisciplinaryActions());
			}
			
			// 31100 start - find data pending suspend register
			// 26238 : Disciplinary Action -32710- end -
			SupplierDataMaintenanceResDTO updateSuspendList = SmServiceLookup.getSupplierDataMaintenanceSO()
					.retrieveDisciplinaryAction(true);
			if (!EntityHelper.isCollectionEmpty(updateSuspendList.getSmDisciplinaryActions())){
				updateSuspendDA(updateSuspendList.getSmDisciplinaryActions());
			}
			// 31100 end

			return Response.status(200).entity("success calling process disciplinary action !!").build();
		} catch (Exception e) {
			log.error(e);
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
	
	/**
	 * NO 1 : find data with end date less than current date to release DA
	 */
	private void releaseDA(){
		SupplierDataMaintenanceResDTO resDTO = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveDisciplinaryAction(false);
		if(EntityHelper.isCollectionEmpty(resDTO.getSmDisciplinaryActions())){
			return;
		}
		int i = 0;
		for (SmDisciplinaryAction disc : resDTO.getSmDisciplinaryActions()) {
			log.info("releaseDAList. supplier id==============" + disc.getSmSupplier().getSupplierId());
			if (disc.getSmSupplier() != null) {
				disc.getSmSupplier();

				// 30784 : Release Disciplinary Action
				// 31100 start
				SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(disc);
				// 31100 end

				log.info("-----------After------------------" + i);
				i++;
			}
		}
	}
	
	/**
	 * NO 2: find data pending DA, (data with active txn will update DA after complete all txn)
	 */
	private void updatePendingDA() throws java.text.ParseException{
		// 28798: Disciplinary Action - remove blacklist and cancel registration
		SupplierDataMaintenanceResDTO updateDAList = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryAction(); 

		if (updateDAList != null && !EntityHelper.isCollectionEmpty(updateDAList.getSmDisciplinaryActions())) {

			// 31384 - start
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date today = formatter.parse(formatter.format(new Date()));

			for (SmDisciplinaryAction pendingDisc : updateDAList.getSmDisciplinaryActions()) {
				log.info("updateDAList. supplier id==============" + pendingDisc.getSmSupplier().getSupplierId());

				if (pendingDisc.getSmSupplier() != null && pendingDisc.getStartDate().compareTo(today) <= 0) {

					pendingDisc.getSmSupplier();
					pendingDisc.getSmSupplier().getSmAppl();
					// 27426 : Disciplinary Action - end
					PmUser user = SmCommonUtil.getUserByUserId(pendingDisc.getChangedBy());
					List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
					SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(pendingDisc, user, userRoles);
				}
			}
			// 31384 - end
		}
	}
	
	/**
	 * NO 3 : find data pending Cancel Registration or Blacklist Owner/Director
	 */
	private void updateBlacklistCancelDA(List<SmDisciplinaryAction> daList){
		// 28798: Disciplinary Action - get all active for cancel registration and blacklist
		Date today = new Date();
		for (SmDisciplinaryAction cancelRegBlacklist : daList) {
			log.info("updateBCDAList. supplier id==============" + cancelRegBlacklist.getSmSupplier().getSupplierId());
			if (cancelRegBlacklist.getStartDate().compareTo(today) <= 0) {
				if (cancelRegBlacklist.isPendingBlacklist() || cancelRegBlacklist.isPendingCancel()) {
					cancelRegBlacklist.getSmSupplier();
					cancelRegBlacklist.getSmSupplier().getSmAppl();
					PmUser user = SmCommonUtil.getUserByUserId(cancelRegBlacklist.getChangedBy());
					List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
					// 30878 to check isPendingCanel /
					// isPendingBlack list is updated
					SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(cancelRegBlacklist, user, userRoles);

					// 31100,30878
					releaseBlacklistCancelDA(cancelRegBlacklist.getDisciplinaryActionId());
				}
					
				// 29597 : Disciplinary Action - 26238
				if ((cancelRegBlacklist.isBlacklistOwner() || cancelRegBlacklist.isCancelRegistration())
							&& (!cancelRegBlacklist.isPendingCancel() && !cancelRegBlacklist.isPendingBlacklist())) {
					cancelRegBlacklist.getSmSupplier();
					SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(cancelRegBlacklist);
				}
			}
		}
	}
	
	private void releaseBlacklistCancelDA(Long disciplinaryActionId){
		if (disciplinaryActionId != null) {
			SmDisciplinaryAction smDA = SmServiceLookup.getSupplierDataMaintenanceSO()
					.retrieveActiveDisciplinaryActionByDisciplinaryId(disciplinaryActionId);
			if ((smDA.isBlacklistOwner() || smDA.isCancelRegistration()) && (!smDA.isPendingCancel() && !smDA.isPendingBlacklist())) {
				smDA.getSmSupplier();
				SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(smDA);
			}
		}
	}
	
	/**
	 * NO 4 : find data pending suspend register , update pending txn for DA suspend and release if DA suspend expired
	 */
	private void updateSuspendDA(List<SmDisciplinaryAction> daList) throws java.text.ParseException{
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		Date todayWithZeroTime = formatter.parse(formatter.format(today));

		for (SmDisciplinaryAction suspendList : daList) {

			log.info("suspendList. supplier id==============" + suspendList.getSmSupplier().getSupplierId());
			// 33248 - reopen 31/05: Disciplinary Action - 26238 -
			if (suspendList.isBlacklistOwner() || suspendList.isCancelRegistration() || suspendList.getStartDate().compareTo(today) > 0){
				continue;
			}
			// reactive account if DA suspend register is
			// expired
			// 30784 : Release Disciplinary Action
			if (suspendList.getEndDate().compareTo(todayWithZeroTime) < 0 && !suspendList.isPendingSuspend()) {
				RegistrationReqDTO reqDTO = new RegistrationReqDTO();

				log.info("-----------suspend registration-----------------" + suspendList.isSuspendRegistration());
				PersonnelReqDTO reqPersonnelDTO = new PersonnelReqDTO();
				reqPersonnelDTO.setSmSupplierId(suspendList.getSmSupplier().getSupplierId());

				PersonnelResDTO personnelResDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelBySupplierIdForDA(reqPersonnelDTO);

				reqDTO.setSmSupp(suspendList.getSmSupplier());
				reqDTO.setDisciplinaryAction(true); // 30784: disciplinary action - 26238
				reqDTO.getSmSupp().getSmAppl().setSmPersonnels(personnelResDTO.getSmPersonnelList());
				try {
					SmServiceLookup.getRegistrationSO().reactivateBasicAccount(reqDTO);
					SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(suspendList);

				} catch (Exception ex) {
					log.error(ex);
					throw new EpException(ex.getMessage());
				}
			} else {
				suspendEndDateAfterToday(suspendList, todayWithZeroTime);
			// 32558: disciplinary action - 26238 end
			}
		}
	}
	
	private void suspendEndDateAfterToday(SmDisciplinaryAction suspendList, Date todayWithZeroTime){
		// 32558: disciplinary action - 26238 Start
		PmUser user = SmCommonUtil.getUserByUserId(suspendList.getChangedBy());
		List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());

		// 26238 start
		// pending that expired
		if (suspendList.getEndDate().compareTo(todayWithZeroTime) < 0) {

			SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryActionForExpiredSuspend(suspendList, user, userRoles);
		} else {
			// pending that not expired
			SmServiceLookup.getSupplierDataMaintenanceSO().updatePendingDisciplinaryAction(suspendList, user, userRoles);

			Long disciplinaryActionId = suspendList.getDisciplinaryActionId();

			// release DA if pending is completed
			if (disciplinaryActionId != null) {
				SmDisciplinaryAction completedPendingSmDa = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionByDisciplinaryId(
							disciplinaryActionId);

				// 26238 - 32558 - 32710 - rollback code
				// is for imposed suspen when supplier
				// finish pending txn
				if (completedPendingSmDa.isSuspendRegistration() && !completedPendingSmDa.isPendingSuspend()) {
					completedPendingSmDa.getSmSupplier();
					SmServiceLookup.getSupplierDataMaintenanceSO().releaseDisciplinaryAction(completedPendingSmDa);
				}
			}
		}
		// 26238 end
	} // 26238 end future date for suspend registration
}
