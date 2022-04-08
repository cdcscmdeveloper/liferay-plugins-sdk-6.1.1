package my.ep.rest.pm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmLoginHistory;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

@Path("/pm")
public class DeactivateUserRoleRest {

	public static Logger log = Logger.getLogger(DeactivateUserRoleRest.class);

	@GET
	@Path("deactivateuserrole")
	public Response userDeactivation() {
		log.info("Before success calling retrieve rest api for user role deactivation....");
		try {
			int UsersdeactivationPerioidNotLogging = SmServiceLookup.getUserSO().validityPeriodOfUsersNotLogged();
			int UsersdeactivationPerioid = SmServiceLookup.getUserSO().validityPeriodOfUsersAlreadyLogged();
			UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserInDeactivationPeriod( UsersdeactivationPerioidNotLogging, UsersdeactivationPerioid);
			for (BigDecimal userId : resDTO.getUserListId()) {
				UserReqDTO reqDTOFinal = new UserReqDTO();
				reqDTOFinal.setEligibleForDeactivation(true);
				reqDTOFinal.setNewRoleList(new ArrayList<PmUserRole>());
				reqDTOFinal.setRemoveRoleList(new ArrayList<PmUserRole>());
				log.info("USER ID >>>>>> " +userId );
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId.longValue());
				UserResDTO resDTO1 = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO);
				List<PmUserOrg> pmUserOrgs = resDTO1.getPmUser().getPmUserOrgs();
				List<PmUserOrg> pmUserOrgsNew = new ArrayList<>();
				PmLoginHistory pmLoginHistory = SmServiceLookup.getUserSO().checkLoginHistory(userId.longValue());
				for (PmUserOrg pmUserOrg : pmUserOrgs) {
					pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
					List<PmUserRole> userRoles = pmUserOrg.getPmUserRoles();
					List<PmUserRole> userRolesNew = new ArrayList<>();
					for (PmUserRole userRole : userRoles) {
						if (userRole.getPmRole() != null) {
							if (userRole.getPmRole().getRoleCode().equals(RoleCode.COMPANY_PROFILE.value())
									|| userRole.getPmRole().getRoleCode().equals(RoleCode.MARKET_RESEARCH.value())) {
								if (pmLoginHistory != null && pmLoginHistory.getLoginDate().compareTo(new Date(userRole.getChangedDate().getTime())) > 0) {
									log.info("User has login history for user id >>> "+resDTO1.getPmUser().getUserId());
									log.info("User role changed date >>> "+userRole.getChangedDate());
									if(new Date(userRole.getChangedDate().getTime()+86400000).compareTo(DateUtils.addMonths(new Date(), -UsersdeactivationPerioid)) <= 0 ){
										log.info("Deactivate user role  Role_Id >>> "+userRole.getUserRoleId());
										userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
										reqDTOFinal.getRemoveRoleList().add(userRole);
									} else {
										if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
											log.info("No need to deactivate  ORG_ID >>> "+pmUserOrg.getUserOrgId());
											pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
											reqDTOFinal.setEligibleForDeactivation(false);
										}
									}
								} else {
									log.info("User never log into the system user id >>> "+resDTO1.getPmUser().getUserId());
									log.info("User role changed date >>> "+userRole.getChangedDate());	
									if(new Date(userRole.getChangedDate().getTime()+86400000).compareTo(DateUtils.addMonths(new Date(), -UsersdeactivationPerioidNotLogging)) <= 0 ){
											log.info("Deactivate user role  Role_Id >>> "+userRole.getUserRoleId());
											userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
											reqDTOFinal.getRemoveRoleList().add(userRole);
									} else {	
										if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
												log.info("No need to deactivate  ORG_ID >>> "+pmUserOrg.getUserOrgId());
												pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
												reqDTOFinal.setEligibleForDeactivation(false);
											}
										}
								}
							} else {
								if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
									pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
									reqDTOFinal.setEligibleForDeactivation(false);
								}
							}
						}
						userRolesNew.add(userRole);
					}
					pmUserOrg.setPmUserRoles(userRolesNew);
					pmUserOrgsNew.add(pmUserOrg);
				}
				reqDTOFinal.setPmUser(resDTO1.getPmUser());
				reqDTOFinal.setUpdateOIM(true);
				try {
					SmServiceLookup.getUserSO().updatePTJUser(reqDTOFinal);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}

			return Response.status(200).entity("success calling process deactivatee user role !!").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
