package my.ep.web.backingbean.ct.circular;


import java.util.HashMap;//34442
import java.util.Map;//34442

import org.apache.log4j.Logger;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractStatusConst;//34442
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.util.ResourceBundleUtils;//34442
import my.ep.util.SLAUtils;//34442
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

//Start Fix - #34367
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
	public FacesScopes getFacesScopes() {
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
		Long orgProfileId = null;
		if (getFacesScopes().getUserSessionBean().getUserId() != null) {
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			if (userResDTO != null && userResDTO.isSuccessful() && userResDTO.getUserOrgList() != null) {
				for (PmUserOrg orgProfile : userResDTO.getUserOrgList()) {
					if (orgProfile != null && orgProfile.getPmOrgProfile() != null) {
						orgProfileId = orgProfile.getPmOrgProfile().getOrgProfileId();
						setOwnerOrgProfile(orgProfile.getPmOrgProfile());
						break;
					}
				}
			}
		}
		return orgProfileId;
	}

	/**
	 * Retrieves the PTJ rule type associated with the specified organization profile.
	 *
	 * <p>This method delegates to the {@code IRuleSO} service to obtain the
	 * rule constant for the given organization profile ID and returns the
	 * result directly.</p>
	 *
	 * @param orgProfileId the unique identifier of the organization profile
	 * @return the PTJ rule type for the specified organization profile, or
	 *         {@code null} if no rule type is defined
	 */
	protected String getPTJRuleTypeByOrgProfile(Long orgProfileId) {
		IRuleSO ruleSO = (IRuleSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		return ruleSO.getUserRulesConst(orgProfileId);
	}
	//End Fix - #34367
	//34442 - start
	/**
	 * Retrieve confirmation message
	 * 
	 * @param key the statusId to find label values for use
	 * @return Fulfillment with Bond Confirmation message
	 */
	public String getConfirmationContent(String key) {
		String message = "";

		try {
			message = ResourceBundleUtils.getMessage(getConfirmationMsgs(Long.parseLong(key)),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT,
					"getConfirmationContent(String key)", key, "CircularBaseBackingBean",
					getUserIdSessionFromLoginId(), e);
		}

		return message.replace("<br/>", System.lineSeparator());
	}
	
	/**
	 * Returns the resource bundle message key for the confirmation prompt
	 * associated with the given contract status code.
	 *
	 * @param key the contract status code (see {@link ContractStatusConst})
	 * @return the message key for the confirmation text, or {@code null}
	 *         if no confirmation message is defined for the given status
	 */
	public String getConfirmationMsgs(Long key) {
		final Map<Long, String> confirmationMsgs = new HashMap<>();

		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_APPROVAL, "lbl.ct.cq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_REVISION_APPROVAL, "lbl.ct.cq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_UPDATE_APPROVAL, "lbl.ct.cq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_UPDATE_REVISION_APPROVAL, "lbl.ct.cq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CR_PENDING_APPROVAL, "lbl.ct.cq.cancel.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CR_PENDING_REVISION_APPROVAL, "lbl.ct.cq.cancel.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_REVISION_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_UPDATE_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_UPDATE_REVISION_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CR_PENDING_REVISION, "lbl.ct.pq.cancel.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CR_PENDING_REVISION_APPROVAL, "lbl.ct.pq.cancel.rev.app.confirm");

		return confirmationMsgs.get(key);
	}
	//34442 - end

	//Start Fix - #34367
	public PmOrgProfile getOwnerOrgProfile() {
		return ownerOrgProfile;
	}
	
	public void setOwnerOrgProfile(PmOrgProfile ownerOrgProfile) {
		this.ownerOrgProfile = ownerOrgProfile;
	}
	//End Fix - #34367
}
