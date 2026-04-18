package my.ep.web.backingbean.ct.circular;

import java.util.List;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class CircularBaseBackingBean<T extends BaseManagedBean > extends BaseBackingBean<T>{

	private static final long serialVersionUID = 1L;
	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger.getLogger(CircularBaseBackingBean.class);

	/**
	 * This method used to retrieve login user information.
	 */
	public FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	private PmOrgProfile ownerOrgProfile;
	
	//Start Fix - #34367
//	public void setDocumentTypeBasedOnUserRules() {
//		Long orgProfileId = getPTJOwner();
//		log.info("userOrgProfileCheck orgProfileId ::: " + orgProfileId);
//		if (orgProfileId == null) {
//			return;
//		}
//		
//		String userRule = getPTJByRuleType(orgProfileId);
//		if (circularVer.getDocType() == null) {
//			if (RuleTypeConst.PTJCO.equalsIgnoreCase(userRule)) {
//				circularVer.setDocType(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
//
//			} else if (RuleTypeConst.PTJ_AUTH_MCPC.equalsIgnoreCase(userRule)) {
//				circularVer.setDocType(ParameterConst.NEW_DOCTYPE_PANEL_CIRCULAR);
//
//			} else {
//				circularVer.setDocType(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
//			}
//		}
//	}

	/**
	 * @param userSO
	 * @return
	 */
	protected Long getPTJOwner() {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		CircularBackingBean circularBB = new CircularBackingBean();
		Long orgProfileId = null;
		userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
		UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
		if (userResDTO != null && userResDTO.isSuccessful() && userResDTO.getUserOrgList() != null) {
			for (PmUserOrg orgProfile : userResDTO.getUserOrgList()) {
				if (orgProfile != null && orgProfile.getPmOrgProfile() != null) {
					orgProfileId = orgProfile.getPmOrgProfile().getOrgProfileId();
					circularBB.getManagedBean().setUserPTJProfile(orgProfileId);
					setOwnerOrgProfile(orgProfile.getPmOrgProfile());
					break;
				}
			}
		}
		return orgProfileId;
	}


	protected String getPTJByRuleType(Long orgProfileId) {
		IRuleSO ruleSO = (IRuleSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		List<Object[]> getPTJRuleType = ruleSO.getUserRulesConst(orgProfileId);
		int hasPTJCO = 0;
		int hasPTJCC = 0;
		int bothRuleType = 0;
		String ruleType = null;
		if (getPTJRuleType != null && !getPTJRuleType.isEmpty()) {
			Object[] row = getPTJRuleType.get(0);
			hasPTJCO = ((Number) row[0]).intValue();
			hasPTJCC = ((Number) row[1]).intValue();
			bothRuleType = ((Number) row[2]).intValue();

			if (bothRuleType != 0 || hasPTJCO != 0) {
				return ruleType = RuleTypeConst.PTJCO;
			}
			if (hasPTJCC != 0) {
				return ruleType = RuleTypeConst.PTJ_AUTH_MCPC;
			}
		}
		return ruleType;
	}


	public PmOrgProfile getOwnerOrgProfile() {
		return ownerOrgProfile;
	}


	public void setOwnerOrgProfile(PmOrgProfile ownerOrgProfile) {
		this.ownerOrgProfile = ownerOrgProfile;
	}
	

	//End Fix - #34367

}
