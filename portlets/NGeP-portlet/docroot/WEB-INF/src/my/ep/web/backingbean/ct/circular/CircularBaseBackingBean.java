package my.ep.web.backingbean.ct.circular;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.HashMap;//34442
import java.util.Map;//34442

import org.apache.log4j.Logger;
import javax.faces.application.FacesMessage; //#30737 add import
import javax.faces.context.FacesContext; //#30737 add import

import my.ep.biz.service.ct.actor.ICtActorSO; //#30737 add import
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.InactiveUserConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.actor.CtActorReqDTO; //#30737 add import
import my.ep.dto.ct.actor.CtActorResDTO; //#30737 add import
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.pm.authorization.user.InactiveUserReqDTO; //#30737
import my.ep.dto.pm.authorization.user.InactiveUserResDTO; //#30737
import my.ep.dto.pm.authorization.user.UserStatusCheckDTO; //#30737 add import
import my.ep.constant.ct.ContractStatusConst;//34442
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtActor; //#30737 add import
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
	
	private static final Map<Long, String> CONFIRMATION_MSGS = buildConfirmationMsgs(); // #34442
		
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
		return CONFIRMATION_MSGS.get(key);
	}
	
	private static Map<Long, String> buildConfirmationMsgs() {
		Map<Long, String> confirmationMsgs = new HashMap<>();

		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_APPROVAL, "lbl.ct.cq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_REVISION_APPROVAL, "lbl.ct.cq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_UPDATE_APPROVAL, "lbl.ct.cq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.CQ_PENDING_UPDATE_REVISION_APPROVAL, "lbl.ct.cq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_REVISION_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_UPDATE_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.PQ_PENDING_UPDATE_REVISION_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.MQ_PENDING_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.MQ_PENDING_REVISION_APPROVAL, "lbl.ct.pq.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.MQ_PENDING_UPDATE_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");
		confirmationMsgs.put(ContractStatusConst.MQ_PENDING_UPDATE_REVISION_APPROVAL, "lbl.ct.pq.upd.rev.app.confirm");

		return Collections.unmodifiableMap(confirmationMsgs);
	}
	//34442 - end

	//Start Fix - #34367 , #30737
	
	public PmOrgProfile getOwnerOrgProfile() {
		return ownerOrgProfile;
	}
	
	public void setOwnerOrgProfile(PmOrgProfile ownerOrgProfile) {
		this.ownerOrgProfile = ownerOrgProfile;
	}
	//End Fix - #34367 , #30737
	
	//#30737 - Start
	
	protected abstract CtCircularVer getCircularVer();
	
	/**
	 * Handles inactive user validation during actions.
	 * 
	 * @param outcome
	 *            -action outcome such as SUBMIT or REVISE
	 * @param docNo
	 *            -circular/cancel-circular doc number to use when building
	 *            validation error messages, since it cannot be reliably derived
	 *            from {@link #getCircularVer()} (that is null for the
	 *            cancel-circular flow)
	 * @param userStatusResults
	 *            -result code returned from inactive user checking process (SP)
	 * @throws EpException
	 *             if inactive user conditions are met during validation.
	 *             Different error messages will be generated based on the
	 *             returned error code.
	 */
	public void handleInactiveUserValidation(String outcome, String docNo,
			List<UserStatusCheckDTO> userStatusResults) {
		try {
			validateUserStatusOnAction(
					buildContractReqDTO(outcome, getUserRole()),
					userStatusResults);
		} catch (EpException ex) {
			throw buildInactiveUserException(ex, docNo, userStatusResults);
		}
	}
	
	/**
	 * Validates the given per-user status check results and blocks the action
	 * when they fall into a disallowed combination.
	 * 
	 * @param reqDTO
	 *            the built request context for the action (outcome and user
	 *            role)
	 * @param userStatusResults
	 *            the per-user status check results to validate
	 * @throws EpException
	 *             if the users' statuses fail validation; the error
	 *             code/message depends on which invalid combination was found
	 */
	public void validateUserStatusOnAction(CircularReqDTO reqDTO,
			List<UserStatusCheckDTO> userStatusResults) {

		log.info("validateUserStatusOnAction....");

		List<UserStatusCheckDTO> inactiveUsers = new ArrayList<>();

		int activeUserCount = 0;

		for (UserStatusCheckDTO user : userStatusResults) {

			Long status = user.getStatus();

			if (InactiveUserConst.INACTIVE_USER_1.equals(status)) {
				activeUserCount++;
			}

			if (InactiveUserConst.INACTIVE_USER_2.equals(status)
					|| InactiveUserConst.INACTIVE_USER_3.equals(status)
					|| InactiveUserConst.INACTIVE_USER_4.equals(status)) {

				inactiveUsers.add(user);
			}
		}

		// Single selected user and inactive
		if (userStatusResults.size() == 1 && !inactiveUsers.isEmpty()) {
			log.info("Single selected user and inactive");
			throw new EpException(MessageConst.ERROR_750096);
		}

		// Mixed active and inactive
		if (activeUserCount > 0 && !inactiveUsers.isEmpty()) {
			log.info("Mixed active and inactive");
			String userNames = getInactiveUserNames(inactiveUsers);
			throw new EpException(MessageConst.ERROR_750096,
					new String[] { userNames });
		}

		// All inactive (2,3,4)
		if (activeUserCount == 0 && !inactiveUsers.isEmpty()) {
			log.info("All inactive (2,3,4)");
			throw new EpException(MessageConst.ERROR_750096);
		}

		// All status = 5
		if (InactiveUserConst.INACTIVE_USER_5.equals(userStatusResults.get(0).getStatus())) {
			log.info("All status = 5");
			throw new EpException(MessageConst.ERROR_750100);
		}

		// Single active or all active
		log.info("PASSED!");
	}
	
	/**
	 * Builds a {@link CircularReqDTO} from the current session user, the given
	 * outcome and role.
	 * 
	 * @param outcome
	 *            the action outcome to apply (e.g. SUBMIT or REVISE)
	 * @param userRole
	 *            the role code the current user is acting under
	 * @return the populated request DTO
	 */
	private CircularReqDTO buildContractReqDTO(String outcome, String userRole) {
		CircularReqDTO reqDTO = new CircularReqDTO();
		reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
		reqDTO.setUserRole(userRole);
		reqDTO.setOutcome(outcome);

		log.info("buildContractReqDTO user role : " + reqDTO.getUserRole());
		log.info("buildContractReqDTO user : " + reqDTO.getPmUser());

		return reqDTO;
	}
	
	/**
	 * Builds and returns a localized {@link EpException} based on the inactive
	 * user validation error code.
	 * 
	 * @param ex
	 *            the original exception containing the inactive user validation
	 *            error code
	 * @return localized {@link EpException} with resolved message parameters
	 */
	protected EpException buildInactiveUserException(EpException ex,
			String docNo, List<UserStatusCheckDTO> userStatusResults) {
		log.info("Catch buildInactiveUserException");

		String errorCode = ex.getMessage();
		log.info("BuildInactiveUserException errorCode : " + errorCode);

		String[] messageKeys = getInactiveUserMessageKeys(errorCode, docNo,
				getSelectedUserNames(userStatusResults), ex.getParameters());

		if (messageKeys != null && messageKeys.length > 0) {

			String[] resolvedMessages = new String[messageKeys.length];
			for (int i = 0; i < messageKeys.length; i++) {
				resolvedMessages[i] = ResourceBundleUtils.getMessage(
						messageKeys[i],
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						getFacesScopes().getUserLocale());
			}
			return new EpException(errorCode, resolvedMessages);
		}
		return new EpException(errorCode);
	}
    
	/**
	 * Returns message parameters based on inactive user validation error code.
	 * 
	 * @param errorCode
	 *            -inactive user validation error code
	 * @param circularNo
	 *            -circular/cancel-circular doc number, supplied by the caller
	 *            since it cannot be reliably derived from
	 *            {@link #getCircularVer()} (that is null for the
	 *            cancel-circular flow)
	 * @param selectedUserNames
	 *            -selected user name used in message parameter generation
	 * @return message parameters for inactive user validation message
	 * 
	 * NOTE: Complexity is caused by explicit business error code
	 *       mapping.
	 */
	private String[] getInactiveUserMessageKeys(String errorCode,
			String circularNo, String selectedUserNames, String[] parameters) {

		if (MessageConst.ERROR_750096.equals(errorCode)
				|| MessageConst.ERROR_750098.equals(errorCode)) {
			if (parameters != null) {
				return parameters;
			}
			return new String[] { selectedUserNames };
		}

		if (MessageConst.ERROR_750097.equals(errorCode)) {
			if (parameters != null) {
				List<String> result = new ArrayList<>();
				result.add(circularNo);
				Collections.addAll(result, parameters);
				return result.toArray(new String[result.size()]);
			}
			return new String[] { circularNo, selectedUserNames };
		}

		if ((MessageConst.ERROR_750099.equals(errorCode)
				|| MessageConst.ERROR_750100.equals(errorCode))
				&& parameters != null) {
			return parameters;
		}

		return new String[0];
	}
    
	/**
	 * Extracts the user names from the given status check results and joins
	 * them into a single human-readable, comma-separated string (with "and"
	 * before the last name).
	 * 
	 * @param userStatusResults
	 *            the status check results to extract user names from
	 * @return the joined user names, e.g. "Alice, Bob and Ali"
	 */
	private String getSelectedUserNames(
			List<UserStatusCheckDTO> userStatusResults) {
		List<String> userNames = new ArrayList<>();
		for (UserStatusCheckDTO user : userStatusResults) {
			userNames.add(user.getUserName());
		}
		return buildUserNames(userNames);
	}
	
	/**
	 * Determines the current user's role code for the active circular task,
	 * based on the managed bean's task name and the roles held by the session
	 * user.
	 * 
	 * @return the resolved role code
	 * @throws EpException
	 *             if no matching role could be resolved for the current
	 *             task/user
	 */
	private String getUserRole() {
		String userRole = null;
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(
						CTProcessNameConst.UPDATE_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(
						CTProcessNameConst.SUBMIT_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(
						CTProcessNameConst.REVISE_CIRCULAR_CONTRACT)) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName()
						.equalsIgnoreCase(CTProcessNameConst.REVIEW_CIRCULAR_CONTRACT))) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CQ_APPROVER.value())) {
				userRole = RoleCode.BPK_CQ_APPROVER.value();
			} else if (getFacesScopes().hasRoleUser(
					RoleCode.BPK_PQ_APPROVER.value())) {
				userRole = RoleCode.BPK_PQ_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	/**
	 * Checks login/inactive status for the given users against a circular
	 * document, delegating to {@link #buildUserStatusCheckResults} to assemble
	 * the results.
	 * 
	 * @param ctCircular
	 *            the circular whose owner org profile and doc number are
	 *            checked against
	 * @param ctCircularVer
	 *            the circular version supplying the document type to check
	 *            against
	 * @param selectedUsers
	 *            the users to check, in the order results should be returned in
	 * @param roleCode
	 *            the role code the users are being checked under
	 * @return one {@link UserStatusCheckDTO} per selected user with id, name,
	 *         role description and parsed status
	 */
	public List<UserStatusCheckDTO> getLoginStatusFromSP(CtCircular ctCircular,
			CtCircularVer ctCircularVer, List<PmUser> selectedUsers,
			String roleCode) {

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		InactiveUserReqDTO inactiveDto = new InactiveUserReqDTO();
		String userlist = buildUserIdString(selectedUsers);
		inactiveDto.setUserIds(userlist);
		inactiveDto.setUserRoleCode(roleCode);
		inactiveDto.setOrgProfileId(ctCircular.getOwnerOrgProfile()
				.getOrgProfileId());
		inactiveDto.setDocType(ctCircularVer.getDocType());
		inactiveDto.setDocNo(ctCircular.getCircularNo());
		inactiveDto.setNotifyAdmin(RoleCode.PTJ_ADMIN.value());

		InactiveUserResDTO resDto = userSO
				.checkInactiveUserBySP(inactiveDto);
		resDto.setRoleCode(roleCode);
		log.info("checkInactiveUserBySP STATUSSS : " + resDto.getStatus());

		return buildUserStatusCheckResults(selectedUsers, resDto);
	}
	
	/**
	 * Checks login/inactive status for the given users against a
	 * cancel-circular document, delegating to
	 * {@link #buildUserStatusCheckResults} to assemble the results.
	 * 
	 * @param docType
	 *            the document type of the cancel-circular request being checked
	 * @param cancelDocNo
	 *            the cancel-circular document number to check status against
	 * @param selectedUsers
	 *            the users to check, in the order results should be returned in
	 * @param roleCode
	 *            the role code the users are being checked under
	 * @return one {@link UserStatusCheckDTO} per selected user with id, name,
	 *         role description and parsed status
	 */
	public List<UserStatusCheckDTO> getLoginStatusForCancelCircularFromSP(
			String docType, String cancelDocNo, List<PmUser> selectedUsers,
			String roleCode) {

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		InactiveUserReqDTO inactiveDto = new InactiveUserReqDTO();
		String userlist = buildUserIdString(selectedUsers);
		inactiveDto.setUserIds(userlist);
		inactiveDto.setUserRoleCode(roleCode);
		inactiveDto.setOrgProfileId(getOwnerOrgProfile().getOrgProfileId());
		inactiveDto.setDocType(docType);
		inactiveDto.setDocNo(cancelDocNo);
		inactiveDto.setNotifyAdmin(RoleCode.PTJ_ADMIN.value());

		InactiveUserResDTO resDto = userSO
				.checkInactiveUserBySP(inactiveDto);
		resDto.setRoleCode(roleCode);
		log.info("getLoginStatusForCancelCircularFromSP STATUSSS : "
				+ resDto.getStatus());

		return buildUserStatusCheckResults(selectedUsers, resDto);
	}
	
	/**
	 * Joins the ids of the given users into a single comma-separated string.
	 * 
	 * @param userIds
	 *            the users whose ids are to be joined
	 * @return the comma-separated user ids, or an empty string if userIds is
	 *         empty
	 */
	private String buildUserIdString(List<PmUser> userIds) {
		StringBuilder sb = new StringBuilder();

		for (PmUser user : userIds) {

			if (sb.length() > 0) {
				sb.append(",");
			}

			sb.append(user.getUserId());
		}

		return sb.toString();

	}
	
	/**
	 * Combines each selected user with the corresponding status code returned
	 * by the inactive-user check, in the order both lists share.
	 * 
	 * @param users
	 *            the users that were checked, in the same order as the status
	 *            codes in resDto
	 * @param resDto
	 *            the check result holding a comma-separated status per user and
	 *            the role code
	 * @return one {@link UserStatusCheckDTO} per user with id, name, role
	 *         description and parsed status; empty list if users is empty
	 */
	private List<UserStatusCheckDTO> buildUserStatusCheckResults(
			List<PmUser> users, InactiveUserResDTO resDto) {

		List<UserStatusCheckDTO> selectedUsers = new ArrayList<>();
		if (!users.isEmpty()) {
			String[] statuses = resDto.getStatus().split(",");
			for (int i = 0; i < statuses.length; i++) {

				String userRoleDesc = RoleHelper.getInstance()
						.getRoleDescByCode(resDto.getRoleCode()).getRoleName();

				UserStatusCheckDTO selectedUser = new UserStatusCheckDTO();
				selectedUser.setUserId(users.get(i).getUserId());
				selectedUser.setUserName(users.get(i).getUserName());
				selectedUser.setUserRoleDesc(userRoleDesc);
				selectedUser.setStatus(Long.parseLong(statuses[i].trim()));
				selectedUsers.add(selectedUser);
			}
		}
		return selectedUsers;
	}
	
	/**
	 * Extracts the user names from the given inactive users and joins them into
	 * a single human-readable, comma-separated string (with "and" before the
	 * last name).
	 * 
	 * @param inactiveUsers
	 *            the inactive users to extract user names from
	 * @return the joined user names, e.g. "Alice, Bob and Ali"
	 */
	private String getInactiveUserNames(List<UserStatusCheckDTO> inactiveUsers) {
		List<String> userNames = new ArrayList<>();
		for (UserStatusCheckDTO user : inactiveUsers) {
			userNames.add(user.getUserName());
		}
		return buildUserNames(userNames);
	}
    
	/**
	 * Joins the given user names into a single human-readable string, separated
	 * by commas with "and" before the last name.
	 * 
	 * @param userNames
	 *            the user names to join
	 * @return the joined names, e.g. "Alice, Bob and Ali"
	 */
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
	 * Method used to retrieve actor by document Id, document type and role
	 * code.
	 * 
	 * @Param docId, docType, roleCode
	 * @return CtActor
	 */
	public CtActor retrieveContractActor(Long docId, String docType,
			String roleCode) {
		ICtActorSO ctActorSO = null;
		CtActorResDTO ctActorResDTO = new CtActorResDTO();

		try {
			ctActorSO = (ICtActorSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							ICtActorSO.class);

			CtActorReqDTO ctActorReqDTO = new CtActorReqDTO();
			ctActorReqDTO.setDocId(docId);
			ctActorReqDTO.setDocType(docType);
			ctActorReqDTO.setRoleCode(roleCode);
			ctActorResDTO = ctActorSO
					.retrieveCtActorByDocIdAndTypeAndRole(ctActorReqDTO);
		} catch (Exception e) {
			log.error(e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return ctActorResDTO.getCtActor();
	}
	
	//#30737 - End
}
