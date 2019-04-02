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
public class DeativateUserRoleRest {

	public static Logger log = Logger.getLogger(DeativateUserRoleRest.class);

	@GET
	@Path("deactivateuserrole")
	public Response userDeactivation() {
		log.info("Before success calling retrieve rest api....");
		try {
			UserReqDTO reqDTOFinal = new UserReqDTO();
			reqDTOFinal.setEligibleForDeactivation(true);
			int UsersdeactivationPerioidNotLogging = SmServiceLookup.getUserSO().validityPeriodOfUsersNotLogged();
			int UsersdeactivationPerioid = SmServiceLookup.getUserSO().validityPeriodOfUsersAlreadyLogged();
			UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserInDeactivationPeriod( UsersdeactivationPerioidNotLogging, UsersdeactivationPerioid);
			for (BigDecimal userId : resDTO.getUserListId()) {
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
								if (pmLoginHistory != null && pmLoginHistory.getLoginDate().compareTo(new Date(userRole.getChangedDate().getTime())) >= 0) {
									if(new Date(userRole.getChangedDate().getTime()).compareTo(DateUtils.addMonths(new Date(), -UsersdeactivationPerioid)) < 0 ){
										userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
									} else {
										if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
											pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
											reqDTOFinal.setEligibleForDeactivation(false);
										}
									}
								} else {
										if(new Date(userRole.getChangedDate().getTime()).compareTo(DateUtils.addMonths(new Date(), -UsersdeactivationPerioidNotLogging)) < 0 ){
											userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
										} else {
											if(userRole.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
												pmUserOrg.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
												reqDTOFinal.setEligibleForDeactivation(false);
											}
										}
								}
							} else {
								if(userRole.getPmRole().getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
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
				SmServiceLookup.getUserSO().updatePTJUser(reqDTOFinal);
			}

			return Response.status(200).entity("success calling process deactivatee user role !!").build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
