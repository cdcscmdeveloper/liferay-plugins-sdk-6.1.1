package my.ep.rest.pm;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.type.pm.RoleCode;
import my.ep.web.util.sm.helper.SmServiceLookup;

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
			int UsersdeactivationPerioidNotLogging = SmServiceLookup.getUserSO().validityPeriodOfUsersNotLogged();
			int UsersdeactivationPerioid = SmServiceLookup.getUserSO().validityPeriodOfUsersAlreadyLogged();
			UserResDTO resDTO = SmServiceLookup.getUserSO().retrieveUserInDeactivationPeriod(UsersdeactivationPerioidNotLogging, UsersdeactivationPerioid);
			for (BigDecimal userId : resDTO.getUserListId()) {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId.longValue());
				UserResDTO resDTO1 = SmServiceLookup.getUserSO().retrieveUserByUserId(reqDTO);
				resDTO1.getPmUser().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
				List<PmUserOrg> pmUserOrgs = resDTO1.getPmUser().getPmUserOrgs();
				List<PmUserOrg> pmUserOrgsNew = new ArrayList<>();
				for(PmUserOrg pmUserOrg : pmUserOrgs){
					List<PmUserRole> userRoles = pmUserOrg.getPmUserRoles();
					List<PmUserRole> userRolesNew = new ArrayList<>();
					for(PmUserRole userRole :userRoles){
						if (userRole.getPmRole() != null) {
							if(userRole.getPmRole().getRoleCode().equals(RoleCode.COMPANY_PROFILE.value()) || userRole.getPmRole().getRoleCode().equals(RoleCode.MARKET_RESEARCH.value())) {
								userRole.setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);							
							}else{
								resDTO1.getPmUser().setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
							}
						}
						userRolesNew.add(userRole);
					}
					pmUserOrg.setPmUserRoles(userRolesNew);
					pmUserOrgsNew.add(pmUserOrg);
				}
				reqDTOFinal.setPmUser(resDTO1.getPmUser());
				reqDTOFinal.setUpdateOIM(true);
				SmServiceLookup.getUserSO().savePmUser(reqDTOFinal);			
			}

			return Response.status(200)
					.entity("success calling process deactivatee user role !!")
					.build();
		} catch (Exception e) {
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
}
