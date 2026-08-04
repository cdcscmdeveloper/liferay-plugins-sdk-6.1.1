package my.ep.web.backingbean.ct.circular;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import java.util.HashMap;//34442
import java.util.Map;//34442

import org.apache.log4j.Logger;
import javax.faces.application.FacesMessage; //#30737 add import
import javax.faces.context.FacesContext; //#30737 add import

import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.ct.actor.ICtActorSO; //#30737 add import
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.InactiveUserConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.actor.CtActorReqDTO; //#30737 add import
import my.ep.dto.ct.actor.CtActorResDTO; //#30737 add import
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.InactiveUserReqDTO;
import my.ep.dto.pm.authorization.user.InactiveUserResDTO;
import my.ep.dto.pm.authorization.user.SelectedInactiveUserDTO; //#30737 add import
import my.ep.constant.ct.ContractStatusConst;//34442
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtActor; //#30737 add import
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;//34442
import my.ep.util.SLAUtils;//34442
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.RoleHelper;

//#30737

import org.apache.log4j.Logger;
//#30737

//Start Fix - #34367
/**
 * @author bob
 * 
 */
public abstract class CircularBaseBackingBean<T extends BaseManagedBean > extends BaseBackingBean<T>{

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
	
	//#30737 - Start
	
	protected abstract CtCircularVer getCircularVer();
	
	/**
	 * Handles inactive user validation during actions.
	 *
	 * @param outcome -action outcome such as SUBMIT or REVISE
	 * @param inactiveUserCheckResult -result code returned from inactive user checking process (SP)
	 * @throws EpException if inactive user conditions are met during validation. 
	 * Different error messages will be generated based on the returned error code.
	 */
	public void handleInactiveUserValidation(String outcome, List<SelectedInactiveUserDTO> selectedInactiveUsers) {
        try {
        	checkInactiveUserOnAction(
        		buildContractReqDTO(outcome, getUserRole()),
        		selectedInactiveUsers
        	);
        } catch (EpException ex) {
        	throw buildInactiveUserException(ex, selectedInactiveUsers);
        }
    }
	
	public void checkInactiveUserOnAction(CircularReqDTO reqDTO, List<SelectedInactiveUserDTO> selectedInactiveUsers) {

    	log.info("checkInactiveUserOnAction....");
    	
        List<SelectedInactiveUserDTO> invalidUsers = new ArrayList<SelectedInactiveUserDTO>();

        int activeCount = 0;

        for (SelectedInactiveUserDTO user : selectedInactiveUsers) {

            Long status = user.getStatus();

            if (Long.valueOf(1L).equals(status)) {
                activeCount++;
            }

            if (Long.valueOf(2L).equals(status)
                    || Long.valueOf(3L).equals(status)
                    || Long.valueOf(4L).equals(status)) {

                invalidUsers.add(user);
            }
        }

     // Single selected user and inactive
        if (selectedInactiveUsers.size() == 1 && !invalidUsers.isEmpty()) {
        	log.info("Single selected user and inactive");
            throw new EpException(MessageConst.ERROR_100068);
        }

        // Mixed active and inactive
        if (activeCount > 0 && !invalidUsers.isEmpty()) {
        	log.info("Mixed active and inactive");
            String userNames = getSelectedInactiveUserNames(invalidUsers);
            throw new EpException(MessageConst.ERROR_100068, new String[] { userNames });
        }

        // All inactive (2,3,4)
        if (activeCount == 0 && !invalidUsers.isEmpty()) {
        	log.info("All inactive (2,3,4)");
            throw new EpException(MessageConst.ERROR_100068);
        }

        // All status = 5
        if (Long.valueOf(5L).equals(selectedInactiveUsers.get(0).getStatus())) {
        	log.info("All status = 5");
            throw new EpException(MessageConst.ERROR_100073);
        }

        // Single active or all active
        log.info("PASSED!");
        //throw new EpException(MessageConst.ERROR_100068); //TEMP
        return;
    }
	
	private CircularReqDTO buildContractReqDTO(String outcome, String userRole) {
		CircularReqDTO reqDTO = new CircularReqDTO();
        reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
        reqDTO.setUserRole(userRole);
        reqDTO.setOutcome(outcome);
        
        log.info("buildContractReqDTO user role : "+reqDTO.getUserRole());
		log.info("buildContractReqDTO user : "+reqDTO.getPmUser());
    	
        return reqDTO;
    }
	
	/**
     * Builds and returns a localized {@link EpException} based on the
     * inactive user validation error code.
     *
     * @param ex the original exception containing the inactive user
     * validation error code
     * @return localized {@link EpException} with resolved message
     * parameters
    */
    protected EpException buildInactiveUserException(EpException ex, List<SelectedInactiveUserDTO> selectedInactiveUsers) {
    	log.info("Catch buildInactiveUserException");
        
        String errorCode = ex.getMessage();
        log.info("BuildInactiveUserException errorCode : "+errorCode);
        
        String[] messageKeys =
                getInactiveUserMessageKeys(
                        errorCode,
                        getSelectedUserNames(selectedInactiveUsers),
                        ex.getParameters());
        
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
    private String[] getInactiveUserMessageKeys(String errorCode, String selectedUserName, String[] parameters) {

    	CtCircularVer circularVer = getCircularVer();
    	CtCircular ctCircular = circularVer != null ? circularVer.getCtCircular() : null;
    	if (ctCircular == null) {
    		return null;
    	}
    	String circularNo = ctCircular.getCircularNo();
    	
        if (MessageConst.ERROR_100068.equals(errorCode)
        		|| MessageConst.ERROR_100070.equals(errorCode)) {
        	if(parameters != null){
        		return parameters;
        	}
            return new String[]{selectedUserName};
        }

        if (MessageConst.ERROR_100069.equals(errorCode)) {
            if (parameters != null) {
            	List<String> result = new ArrayList<String>();
            	result.add(circularNo);
            	Collections.addAll(result, parameters);
            	return result.toArray(new String[result.size()]);
            }
            return new String[] {circularNo, selectedUserName};
        }

        if (MessageConst.ERROR_100071.equals(errorCode)
        		|| MessageConst.ERROR_100073.equals(errorCode)) {
        	if(parameters != null){
        		return parameters;
        	}
        }
        
        return new String[0];
    }
    
    private String getSelectedUserNames(List<SelectedInactiveUserDTO> selectedInactiveUsers) {
        List<String> userNames = new ArrayList<String>();
        for (SelectedInactiveUserDTO user : selectedInactiveUsers) {
            userNames.add(user.getUserName());
        }
        return buildUserNames(userNames);
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
			} else if(getFacesScopes().hasRoleUser(RoleCode.BPK_PQ_APPROVER.value())) {
				userRole = RoleCode.BPK_PQ_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	public List<SelectedInactiveUserDTO> getLoginStatusFromSP(CtCircular ctCircular, CtCircularVer ctCircularVer, List<PmUser> selectedUsers, String roleCode) {
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		InactiveUserReqDTO inactiveDto = new InactiveUserReqDTO();
		String userlist = populateInactiveUserIdIntoString(selectedUsers);
		inactiveDto.setUserIds(userlist); 
		inactiveDto.setUserRoleCode(roleCode); 
		inactiveDto.setOrgProfileId(getOwnerOrgProfile().getOrgProfileId());
		inactiveDto.setDocType(ctCircularVer.getDocType());
		inactiveDto.setDocNo(ctCircular.getCircularNo());
		inactiveDto.setNotifyAdmin(RoleCode.PTJ_ADMIN.value());
		
		InactiveUserResDTO resDto = userSO.checkInactiveUserBySP(inactiveDto);
		resDto.setRoleCode(roleCode);
		log.info("checkInactiveUserBySP STATUSSS : "+  resDto.getStatus());
		
		return resolveInactiveUserStatus(selectedUsers, resDto);
	}
	
	public List<SelectedInactiveUserDTO> getLoginStatusForCancelCircularFromSP(String docType, String cancelDocNo, List<PmUser> selectedUsers, String roleCode) {
		
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		InactiveUserReqDTO inactiveDto = new InactiveUserReqDTO();
		String userlist = populateInactiveUserIdIntoString(selectedUsers);
		inactiveDto.setUserIds(userlist); 
		inactiveDto.setUserRoleCode(roleCode); 
		inactiveDto.setOrgProfileId(getOwnerOrgProfile().getOrgProfileId());
		inactiveDto.setDocType(docType);
		inactiveDto.setDocNo(cancelDocNo);
		inactiveDto.setNotifyAdmin(RoleCode.PTJ_ADMIN.value());
		
		InactiveUserResDTO resDto = userSO.checkInactiveUserBySP(inactiveDto);
		resDto.setRoleCode(roleCode);
		log.info("getLoginStatusForCancelCircularFromSP STATUSSS : "+  resDto.getStatus());
		
		return resolveInactiveUserStatus(selectedUsers, resDto);
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
	
	private List<SelectedInactiveUserDTO> resolveInactiveUserStatus(List<PmUser> users, InactiveUserResDTO resDto) {

  	    List<SelectedInactiveUserDTO> selectedUsers = new ArrayList<SelectedInactiveUserDTO>();
  	    if (!users.isEmpty()) {
  	    	String[] statuses = resDto.getStatus().split(",");
  	  	    for (int i = 0; i < statuses.length; i++) {
  	  	    	
  	  	    	String userRoleDesc = RoleHelper.getInstance().getRoleDescByCode(resDto.getRoleCode()).getRoleName();
  	  	    	
  	  	        SelectedInactiveUserDTO selectedUser = new SelectedInactiveUserDTO();
  	  	        selectedUser.setUserId(users.get(i).getUserId());
  	  	        selectedUser.setUserName(users.get(i).getUserName());
  	  	        selectedUser.setUserRoleDesc(userRoleDesc);
  	  	        selectedUser.setStatus(Long.parseLong(statuses[i].trim()));
  	  	        selectedUsers.add(selectedUser);
  	  	    }
  	    }
  	    return selectedUsers;
  	}
	
	private String getSelectedInactiveUserNames(List<SelectedInactiveUserDTO> inactiveUsers) {
        List<String> userNames = new ArrayList<String>();
        for (SelectedInactiveUserDTO user : inactiveUsers) {
            userNames.add(user.getUserName());
        }
        return buildUserNames(userNames);
    }
    
    private String buildUserNames(List<String> userNames) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < userNames.size(); i++) {
            if (i > 0) {
                if (i == userNames.size() - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(userNames.get(i));
        }
        return sb.toString();
    }
    
    /**
	 * Method used to retrieve actor by document Id, document type and role code.
	 * @Param docId, docType, roleCode
	 * @return CtActor
	 */
    public CtActor retrieveContractActor(Long docId, String docType, String roleCode) {
		ICtActorSO ctActorSO = null;
		CtActorResDTO ctActorResDTO = new CtActorResDTO();

		try {
			ctActorSO = (ICtActorSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, ICtActorSO.class);

			CtActorReqDTO ctActorReqDTO = new CtActorReqDTO();
			ctActorReqDTO.setDocId(docId);
			ctActorReqDTO.setDocType(docType);
			ctActorReqDTO.setRoleCode(roleCode);
			ctActorResDTO = ctActorSO.retrieveCtActorByDocIdAndTypeAndRole(ctActorReqDTO);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return ctActorResDTO.getCtActor();
	}
	
	//#30737 - End
}
