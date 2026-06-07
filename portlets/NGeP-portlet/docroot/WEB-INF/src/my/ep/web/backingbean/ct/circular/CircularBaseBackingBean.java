package my.ep.web.backingbean.ct.circular;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.InactiveUserReqDTO;
import my.ep.dto.pm.authorization.user.InactiveUserResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
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
	
	//#30737 - Start
	/**
	 * Handles inactive user validation during actions.
	 *
	 * @param outcome -action outcome such as SUBMIT or REVISE
	 * @param inactiveUserCheckResult -result code returned from inactive user checking process (SP)
	 * @throws EpException if inactive user conditions are met during validation. 
	 * Different error messages will be generated based on the returned error code.
	 */
	public void handleInactiveUserValidation(String outcome, Map<String, Long> inactiveUserCheckResult, List<PmUser> selectedUsers) {

		ICircularSO circularSO
			= BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
   
        try {
        	circularSO.checkInactiveUserOnAction(
        		buildContractReqDTO(outcome, getUserRole()),
        		selectedUsers,
            	inactiveUserCheckResult
        	);
        } catch (EpException ex) {
        	 List<PmUser> inactiveSelectedUsers = new ArrayList<PmUser>();
             for (PmUser user : selectedUsers) {
                 Long status = inactiveUserCheckResult.get(user.getUserName());
                 if (status != null && (status == 2L || status == 3L || status == 4L)) {
                     inactiveSelectedUsers.add(user);
                 }
             }
             throw buildInactiveUserException(ex, inactiveSelectedUsers);
        }
    }
	
	private CircularReqDTO buildContractReqDTO(String outcome, String userRole) {
		CircularReqDTO reqDTO = new CircularReqDTO();
        reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
        reqDTO.setUserRole(userRole);
        reqDTO.setOutcome(outcome);
        
        log.info("xoxoxoxoxooxoxo user role : "+reqDTO.getUserRole());
		log.info("xoxoxoxoxooxoxo user : "+reqDTO.getPmUser());
    	
        return reqDTO;
    }
	
	private EpException buildInactiveUserException(EpException ex, List<PmUser> selectedUsers) {
    	log.info("xoxoxoxoxoxoxo catch EpException");
    	
    	String errorCode = ex.getMessage();
    	String[] messageKeys = null;
    			for(PmUser user : selectedUsers) {
    				messageKeys = getInactiveUserMessageKeys(
    	                    errorCode,
    	                    user.getUserName()
    	                );
    			}
        
        log.info("xoxoxoxoxoxoxo errorCode : "+errorCode);
        
        if (MessageConst.ERROR_100074.equals(errorCode)) {
            messageKeys = new String[]{selectedUsers.get(0).getUserName()};
        }
        
        if (messageKeys != null && messageKeys.length > 0) {

            String[] resolvedMessages = new String[messageKeys.length];
            for (int i = 0; i < messageKeys.length; i++) {
                resolvedMessages[i] =
                    ResourceBundleUtils.getMessage(
                        messageKeys[i],
                        ResourceBundleUtils.BUNDLE_COMMON_LABELS,
                        getFacesScopes().getUserLocale()
                    );
            }
            return new EpException(errorCode, resolvedMessages);
        }
        return new EpException(errorCode);
    }
	
	/**
     * Returns message parameters based on inactive user validation error code.
     *
     * @param errorCode -inactive user validation error code
     * @param selectedUserName -selected user name used in message parameter generation
     * @return message parameters for inactive user validation message
     * 
     * NOTE:
     * Complexity is caused by explicit business error code mapping.
    */
    private String[] getInactiveUserMessageKeys(String errorCode, String selectedUserName) {

        if (MessageConst.ERROR_100068.equals(errorCode)
        		|| MessageConst.ERROR_100070.equals(errorCode)) {
            return new String[]{selectedUserName};
        }

        if (MessageConst.ERROR_100069.equals(errorCode)) {
            return new String[]{
                "MOF/CDC/2000192",
                selectedUserName
            };
        }

        if (MessageConst.ERROR_100071.equals(errorCode)) {
            return new String[]{
                "KADIR",
                "Chairperson",
                "KADUK",
                "Member"
            };
        }

        if (MessageConst.ERROR_100072.equals(errorCode)) {
            return new String[]{
                "MOF/ABC/20002",
                selectedUserName,
                "RAMLEEE"
            };
        }

        if (MessageConst.ERROR_100073.equals(errorCode)) {
            return new String[]{
                "Requisitioner",
                "Pejabat Pengarah Hospital Kuala Lumpur Unit Pentadbiran"
            };
        }
        return new String[0];
    }
	
	private String getUserRole() {
		String userRole = null;
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_CIRCULAR_CONTRACT)) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_CIRCULAR_CONTRACT))) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CQ_APPROVER.value())) {
				userRole = RoleCode.BPK_CQ_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	public Map<String, Long> getLoginStatusFromSP(CtCircular ctCircular, CtCircularVer ctCircularVer, List<PmUser> selectedUsers, RoleCode roleCode) {
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		InactiveUserReqDTO inactiveDto = new InactiveUserReqDTO();
		
		inactiveDto.setUserIds(populateInactiveUserIdIntoString(selectedUsers)); // should populate it into string with delimiter if more than 1
		inactiveDto.setUserRoleCode(roleCode.value()); 
		inactiveDto.setOrgProfileId(ctCircular.getOwnerOrgProfile().getOrgProfileId());  // 992192L;
		log.info("xoxoxoxoxooxoxo PROFILE ID : "+  ctCircular.getOwnerOrgProfile().getOrgProfileId());
		inactiveDto.setDocType(ctCircularVer.getDocType());
		inactiveDto.setDocNo(ctCircular.getCircularNo());
		
		InactiveUserResDTO resDto = userSO.checkInactiveUserBySP(inactiveDto);
		log.info("xoxoxoxoxooxoxo STATUSSS : "+  resDto.getStatus());
		log.info("xoxoxoxoxooxoxo END ");
		
		return getInactiveUserStatus(resDto.getStatus(), selectedUsers);
	}
	
	private String populateInactiveUserIdIntoString(List<PmUser> userIds){
		StringBuilder sb = new StringBuilder();
		
		for(PmUser user : userIds){
			
			if (sb.length() > 0) {
		        sb.append(",");
		    }

		    sb.append(user.getUserId());
		}
		
		return sb.toString();
		
	}
	
	private Map<String, Long> getInactiveUserStatus(String userStatus, List<PmUser> userIds){
		
		List<String> codeList = Arrays.asList(userStatus.split(","));
		
		Map<String, Long> mappedResult = new LinkedHashMap<String, Long>();
		
		for (int i = 0; i < codeList.size(); i++) {
			mappedResult.put(userIds.get(i).getUserName(), Long.parseLong(codeList.get(i)));
		}
		
		return mappedResult;
	}
	
	//#30737 - End
}
