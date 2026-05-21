package my.ep.web.backingbean.ct.circular;


import org.apache.log4j.Logger;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
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

/**
 * @author bob
 * 
 */
public class CircularBaseBackingBean<T extends BaseManagedBean > extends BaseBackingBean<T>{

	private static final long serialVersionUID = 1L;
	/**
	 * Logger instance.
	 */
	public static Logger log = EpLogger.getLogger(CircularBaseBackingBean.class);
	
	private PmOrgProfile ownerOrgProfile;

	/**
	 * This method used to initiate FacesScopes
	 * instances of current request
	 */
	protected FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	/**
	 * Retrieves and sets the PTJ (organization profile) owner for the current user.
	 *
	 * <p>This method obtains the logged-in user's ID from the session and invokes
	 * the {@code IUserSO} service to retrieve the list of organization profiles
	 * associated with the user. It then selects the first available organization
	 * profile from the list and performs the following actions:
	 * <ul>
	 *   <li>Extracts the organization profile ID</li>
	 *   <li>Stores the PTJ profile ID in {@code CircularBackingBean}</li>
	 *   <li>Sets the owner organization profile using {@code setOwnerOrgProfile}</li>
	 * </ul>
	 *
	 * <p>If no valid organization profile is found, {@code null} is returned.</p>
	 *
	 * @return the organization profile ID (PTJ owner) of the current user,
	 *         or {@code null} if no profile is available or retrieval fails
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

	/**
	 * Retrieves the PTJ rule type for a given organization profile.
	 *
	 * <p>This method invokes the {@code IRuleSO} service to obtain the rule
	 * constant associated with the specified organization profile ID.
	 * If a rule type is found, it is returned; otherwise, {@code null} is returned.</p>
	 *
	 * @param orgProfileId the unique identifier of the organization profile
	 * @return a {@code String} representing the PTJ rule type, or {@code null}
	 *         if no rule type is found
	 */
	protected String getPTJRuleTypeByOrgProfile(Long orgProfileId) {
		IRuleSO ruleSO = (IRuleSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		String getPTJRuleType = ruleSO.getUserRulesConst(orgProfileId);
		String ruleType = null;
		if (getPTJRuleType != null) {
			return ruleType = getPTJRuleType;
		}
		return ruleType;
	}


	public PmOrgProfile getOwnerOrgProfile() {
		return ownerOrgProfile;
	}
	
	public void setOwnerOrgProfile(PmOrgProfile ownerOrgProfile) {
		this.ownerOrgProfile = ownerOrgProfile;
	}
}
