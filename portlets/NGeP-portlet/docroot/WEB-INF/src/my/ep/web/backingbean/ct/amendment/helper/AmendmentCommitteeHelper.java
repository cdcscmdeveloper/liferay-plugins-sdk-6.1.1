/**
 * 
 */
package my.ep.web.backingbean.ct.amendment.helper;

import java.util.List;

import org.primefaces.context.RequestContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.constant.ct.ContractHashSetConst;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * CommitteeHelper is a domain-specific helper class responsible for
 * determining and resolving committee-related user roles within the
 * amendment workflow.
 *
 * <p>
 * This class encapsulates all business rules related to committee role
 * resolution, such as chairperson and member role determination, based on:
 * </p>
 * <ul>
 *     <li>Current committee type</li>
 *     <li>User role assignments</li>
 *     <li>Workflow-specific conditions (e.g. WIP committees)</li>
 *     <li>etc</li>
 * </ul>
 *
 * <p>
 * CommitteeHelper is <b>not</b> a JSF controller, managed bean, or EJB.
 * It does <b>not</b> extend {@code Base} or any framework class. Instead,
 * it operates using context explicitly provided by the caller
 * (typically {@code AmendmentBackingBean}), following the principle of composition
 * over inheritance.
 * </p>
 *
 * <p>
 * This design ensures:
 * </p>
 * <ul>
 *     <li>Clear separation of concerns</li>
 *     <li>Reduced cyclomatic complexity in controller classes</li>
 *     <li>Improved readability and maintainability</li>
 *     <li>Better compliance with SonarLint and Clean Code principles</li>
 * </ul>
 *
 * <p>
 * This class is intentionally <b>stateful per instance</b> and must be
 * initialized before use via its {@code AmendmentCommitteeHelper(...)} method. Each instance
 * is expected to be used within a single request or controller scope.
 * </p>
 *
 * <p>
 * Note:
 * </p>
 * <ul>
 *     <li>This class does <b>not</b> implement {@code Serializable}</li>
 *     <li>No {@code serialVersionUID} is required or expected</li>
 *     <li>This class should not be declared {@code static}</li>
 * </ul>
 *
 * @author hafizuddin
 * @since 8 Jan 2026
 */
public class AmendmentCommitteeHelper {
	
	private final AmendmentManagedBean managedBean;
	private final ContractBackingBean ctBackingBean;
    private final FacesScopes facesScopes;

	/**
	 * Initializes the helper with required context from AmendmentBackingBean.
     * This method must be called once before using this helper.
	 * 
	 * @param managedBean the managed bean from AmendmentManagedBean
	 * @param ctBackingBean the backing bean from ContractBackingBeana
	 * @param facesScopes faces scope utility
	 */
	public AmendmentCommitteeHelper(AmendmentManagedBean managedBean, ContractBackingBean ctBackingBean,
			FacesScopes facesScopes) {
		this.managedBean = managedBean;
		this.ctBackingBean = ctBackingBean;
		this.facesScopes = facesScopes;
	}

	protected AmendmentManagedBean getManagedBean() {
		return managedBean;
	}

	protected FacesScopes getFacesScopes() {
		return facesScopes;
	}
	
	/**
	 * Resets the selected checkbox of a committee member if set.
	 *
	 * @param member the committee member whose checkbox should be reset
	 */
	public void resetSelectedCheckbox(CtCommitteeMember member) {
	    if (member != null && member.isSelectedCheckBox()) {
	        member.setSelectedCheckBox(false);
	    }
	}
	
	/**
	 * Null checking for updated committe and find matching committee member
	 *
	 * @param updatedMember  the updated committee member
	 * @param originalMembers the list of original members
	 * @return matching member if found, otherwise null
	 */
	public CtCommitteeMember findMatchingValidationCommittee(CtCommitteeMember updatedMember, List<CtCommitteeMember> originalMembers) {
		if (updatedMember == null || updatedMember.getPmUser() == null) {
			return null;
		}
		Long updatedUserId = updatedMember.getPmUser().getUserId();
		if (updatedUserId == null) {
			return null;
		}
		return findMatchingCommitteeByUserId(updatedUserId, originalMembers);
	}
	
	/**
	 * Finds the matching committee member in the original list based on user ID.
	 * 
	 * @param updatedUserId user id for updated committee
	 * @param originalMembers the list of original members
	 * @return matching member if found, otherwise null
	 */
	public CtCommitteeMember findMatchingCommitteeByUserId(Long updatedUserId, List<CtCommitteeMember> originalMembers){
		for (CtCommitteeMember originalMember : originalMembers) {
			if (originalMember != null && originalMember.getPmUser() != null
					&& updatedUserId.equals(originalMember.getPmUser().getUserId())) {
				return originalMember;
			}
		}
		return null;
	}
	
	/**
	 * Displays appropriate dialog based on update and validation status.
	 *
	 * @param isUpdateNeeded whether committee update is required
	 * @param isFound whether at least one matching member was found
	 */
	public void showDialogValidateCommittee(boolean isUpdateNeeded, boolean isFound) {
	    if (isUpdateNeeded || !isFound) {
	        RequestContext.getCurrentInstance().execute("updLatestCommitteeDlg.show()");
	    } else {
	        getManagedBean().setUpdateCommittee(Boolean.FALSE);
	        RequestContext.getCurrentInstance().execute("validateLatestCommitteeDlg.show()");
	    }
	}
	/**
	 * Check if the for representative type and member roles
	 * 
	 * @param updated the updated committee member 
	 * @param original the original committee member 
	 * @return true if condition are met
	 */
	public boolean checkRoleAndRepresentativeCommitte(CtCommitteeMember updated, CtCommitteeMember original){
		if (hasRepresentativeTypeChanged(updated, original)
				|| hasMemberRoleChanged(updated, original)){
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if the representative type of the member has changed.
	 *
	 * @param updated the updated committee member
	 * @param original the original committee member
	 * @return true if representative type differs, false otherwise
	 */
	private boolean hasRepresentativeTypeChanged(CtCommitteeMember updated, CtCommitteeMember original) {
	    if (isQuotationCommitteeType()) {
	        return false;
	    }

	    Long updatedTypeId = getParameterId(updated.getRepresentativeType());
	    Long originalTypeId = getParameterId(original.getRepresentativeType());

	    return updatedTypeId != null && originalTypeId != null && !updatedTypeId.equals(originalTypeId);
	}
	
	/**
	 * Checks if the member role of the member has changed.
	 *
	 * @param updated the updated committee member
	 * @param original the original committee member
	 * @return true if member role differs, false otherwise
	 */
	private boolean hasMemberRoleChanged(CtCommitteeMember updated, CtCommitteeMember original) {
	    Long updatedRole = getParameterId(updated.getMemberRole());
	    Long originalRole = getParameterId(original.getMemberRole());

	    return updatedRole != null && originalRole != null && !updatedRole.equals(originalRole);
	}
	
	/**
	 * Checks if the given committee type is a quotation committee type QCA,QCB or QC.
	 *
	 * @return true if it matches any quotation committee type, false otherwise
	 */
	public boolean isQuotationCommitteeType() {
		return getManagedBean().getCommitteeType() != null
				&& ContractHashSetConst.COMMITTEE_TYPE_QUOTATION.contains(getManagedBean().getCommitteeType());
	}
	
	/**
	 * Checks if the given committee type is a quotation committee type QCA and QC.
	 *
	 * @return true if it matches any quotation committee type, false otherwise
	 */
	public boolean isQuotationCommitteeTypeAC() {
		return getManagedBean().getCommitteeType() != null
				&& ContractHashSetConst.COMMITTEE_TYPE_QUOTATION_A_C.contains(getManagedBean().getCommitteeType());
	}
	
	/**
	 * Retrieves the parameter ID safely to avoid NullPointerException.
	 *
	 * @param pmParameter the ParameterConst object
	 * @return parameter ID or null if unavailable
	 */
	private Long getParameterId(PmParameter pmParameter) {
	    return pmParameter != null ? pmParameter.getParameterId() : null;
	}
	
	/**
	 * Checks if the given committee type is a quotation committee type QCA.
	 * 
	 * @return true if current committee is WIP QCA
	 */
	public boolean isQCACommitteeWIP() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCommitteeType() == null) {
			return false;
		}
		String committeeCode = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCommitteeType()
				.getParameterCode();

		return committeeCode != null
				&& committeeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A);
	}
	
	/**
	 * Checks if the given committee type is a quotation committee type QCB.
	 * 
	 * @return true if current committee is WIP QCB
	 */
	public boolean isQCBCommitteeWIP() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCommitteeType() == null) {
			return false;
		}
		String committeeCode = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCommitteeType()
				.getParameterCode();

		return committeeCode != null
				&& committeeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B);
	}
	
	/**
	 * Method to check for WIP QCB Committee Desk Officer. QCA/QCB has been replaced with
	 * QQC. For data that has been saved previously with QCB secretary will be
	 * treated as WIP QCB Committee. For the new version, QCB secretary will be
	 * excluded from desk officer drop down, thus to cater the WIP issue, this
	 * method will add back the selected QCB secretary into the dropdown.
	 * 
	 * @param userList
	 *            the list of Desk Officer
	 */
	public void checkForQCBCommitteeDeskOfficerWIP(List<PmUser> userList) {
		if (checkStatusTaskForQCBCommitteeWIP() && checkSecretaryExistWIP() && loaAmountQCBCommitteeWIP()) {
			checkQCBSecretaryInList(userList);
		}
	}

	/**
	 * Method to check the current status for QCB Secretary dropdown list.
	 * 
	 * @return true if all condition met, otherwise false.
	 */
	private boolean checkStatusTaskForQCBCommitteeWIP() {
		return getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME) || getManagedBean()
						.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME));
	}

	/**
	 * Method to check whether secretary not null and has been saved previously
	 * 
	 * @return true if all condition met, otherwise false.
	 */
	private boolean checkSecretaryExistWIP() {
		return getManagedBean().getSecretaryId() != null && getManagedBean().isCommitteeQCBSecretaryWIP()
				&& isQCBCommitteeWIP();
	}

	/**
	 * Method to check amount loa for WIP QCB committee
	 * 
	 * @return true if all condition met, otherwise false.
	 */
	private boolean loaAmountQCBCommitteeWIP() {
		long loaAmt = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getLoaAmt().longValue();
		return ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbqcAuthorityReq()
				&& loaAmt >= ContractConst.COMMITTEE_20000 && loaAmt <= ContractConst.COMMITTEE_50000;
	}

	/**
	 * Method to check the current save secretary is in the desk officer list dropdown.
	 * 
	 * @param userList
	 *            the list of Desk Officer
	 */
	private void checkQCBSecretaryInList(List<PmUser> userList) {
		boolean isFound = false;
		for (PmUser user : userList) {
			if (user.getUserId().equals(getManagedBean().getSecretaryId())) {
				isFound = true;
				break;
			}
		}
		if (!isFound) {
			checkAndAddQCBSecretaryWIP(userList);
		}
	}
	
	/**
	 * Checks the current contract status within the allowed status and the
	 * amount of the loa for QCB Committee. Checks whether the configured
	 * secretary user has QCB Secretary role and marks the committee QCB
	 * Secretary as work-in-progress.
	 */
	public void checkQCBSecretaryWIP() {
		if (checkStatusTaskForQCBCommitteeWIP() && loaAmountQCBCommitteeWIP() && isQCBCommitteeWIP()) {
			PmUser pmUser = retrieveSecretaryByUserId();

			if (pmUser == null) {
				return;
			}

			if (hasQCBSecretaryRole(pmUser)) {
				getManagedBean().setCommitteeQCBSecretaryWIP(true);
			}
		}
	}

	/**
	 * Method to check the save secretary is QCB. If yes, will insert into list.
	 * 
	 * @param userList the list of Desk Officer
	 */
	private void checkAndAddQCBSecretaryWIP(List<PmUser> userList) {
		PmUser pmUser = retrieveSecretaryByUserId();

		if (pmUser == null || userList == null) {
			return;
		}

		userList.add(pmUser);
	}
	
	/**
	 * Retrieves the secretary user based on secretary ID from managed bean.
	 * 
	 * @return PmUser if found, otherwise null
	 */
	private PmUser retrieveSecretaryByUserId() {
		if (getManagedBean().getSecretaryId() == null) {
			return null;
		}

		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);

		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(getManagedBean().getSecretaryId());

		UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
		return resDTO != null ? resDTO.getPmUser() : null;
	}
	
	/**
	 * Determines whether the given user has QCB Secretary role.
	 * 
	 * @param pmUser user to be checked
	 * @return true if user has QCB Secretary role, otherwise false
	 */
	private boolean hasQCBSecretaryRole(PmUser pmUser) {
		if (pmUser.getPmUserOrgs() == null) {
			return false;
		}

		for (PmUserOrg userOrg : pmUser.getPmUserOrgs()) {
			if (userOrg.getPmUserRoles() == null) {
				continue;
			}

			for (PmUserRole userRole : userOrg.getPmUserRoles()) {
				if (userRole.getPmRole() != null && userRole.getPmRole().getRoleCode() != null
						&& RoleCode.QCB_SECRETARY.value().equalsIgnoreCase(userRole.getPmRole().getRoleCode())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Method to set attended for secretary only for updated committee.
	 */
	public void setUpdatedCommitteeMemberAttended(){
		for(CtCommitteeMember newMember: getManagedBean().getUpdatedCommitteeMembers()) {
			if (newMember.getMemberRole() != null
					&& newMember.getMemberRole().getParameterCode() != null
					&& newMember.getMemberRole().getParameterCode()
							.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				newMember.setAttended(true);
			}
			if(newMember.getUpdateType() != null) {
				getManagedBean().setHasChanges(Boolean.TRUE);
			}
		}
	}
	
	//#41383 start
	/**
	 * Determines whether the user is allowed to access chairperson
	 * functionality for a quotation committee. When the committee type is QCA
	 * or QCB (WIP), chairperson access is denied by default and only granted if
	 * the user has the appropriate chairperson role and belongs to the same
	 * organization (PTJ) as the contract owner.
	 * 
	 * @param orgProfileId  id for ministry committee
	 * @param ctOwnerOrgProfileId contract ministry id
	 * @param userResDTO the user to be checked
	 * @param allowChairpersonRole previous true false for allow chairperson
	 * @return return true if all condition met, otherwise false.
	 */
	public boolean determineChairpersonAccessForQuotationCommmittee(Long orgProfileId, Long ctOwnerOrgProfileId,
			UserResDTO userResDTO, boolean allowChairpersonRole) {
		boolean allow = allowChairpersonRole;

		if (isQuotationCommitteeTypeAC() || isQCBCommitteeWIP()) {
			for (PmRoleDesc pmRoleDesc : userResDTO.getRoleDescList()) {
				String roleCode = pmRoleDesc.getPmRole().getRoleCode();
				if (isValidChairpersonRole(roleCode)) {
					allow = true;
					break;
				} else {
					allow = false;
				}
			}
		}
		return allow;
	}
	//#41383 end
	
	/**
	 * Validates whether the given role code matches the chairperson role
	 * based on the active quotation committee type.
	 *
	 * @param roleCode the role code to evaluate
	 * @return true if the role matches the required chairperson role
	 */
	private boolean isValidChairpersonRole(String roleCode) {
	    return (isQuotationCommitteeTypeAC() && isQuotationChairpersonAC(roleCode))
	            || (isQCBCommitteeWIP()
	            && RoleCode.QCB_CHAIRPERSON.value().equalsIgnoreCase(roleCode));
	}
	
	/**
	 * Check if the role belongs to a valid Chairperson role for Committee Type
	 * QCA and QC
	 * 
	 * @param roleCode user role code
	 * @return true if all condition met, otherwise false.
	 */
	private boolean isQuotationChairpersonAC(String roleCode) {
		return RoleCode.QCA_CHAIRPERSON.value().equalsIgnoreCase(roleCode)
				|| RoleCode.QQC_CHAIRPERSON.value().equalsIgnoreCase(roleCode);
	}
	
	/**
	 * Determines and returns the appropriate desk officer role for the current user
	 * based on the committee type and the roles assigned to the user.
	 * 
	 * @param currentUserRole the current role assigned to the user
	 * @param committeType 
	 * @return the resolved chairperson role code
	 */
	private String determineRoleDeskOfficer(String currentUserRole, String committeType){
		String userRole = currentUserRole;
		if (getFacesScopes().hasRoleUser(RoleCode.DESK_OFFICER.value())) {
			userRole = RoleCode.DESK_OFFICER.value();
		} else {
			String roleCode = committeType + "_DESK_OFFICER";
			if (getFacesScopes().hasRoleUser(roleCode)) {
				userRole = roleCode;
			}
		}
		return userRole;
	}
	
	/**
	 * Determines and returns the appropriate secretary role for the current user
	 * based on the committee type and the roles assigned to the user.
	 *
	 * <p>
	 * This method evaluates the committee type in the following order:
	 * <ul>
	 *     <li>QCA Committee (WIP)</li>
	 *     <li>QCB Committee (WIP)</li>
	 *     <li>Quotation Committee</li>
	 *     <li>Federal Procurement Board (PBA/PBB/PB/SBH/SWK)</li>
	 *     <li>Generic committee chairperson role</li>
	 * </ul>
	 * </p>
	 *
	 * @param currentUserRole the current role assigned to the user
	 * @return the resolved chairperson role code
	 */
	public String determineRoleSecretary(String currentUserRole) {
		String committeeType = getManagedBean().getCommitteeType();
		String userRole = determineRoleDeskOfficer(currentUserRole, committeeType);
		
		if (isQCACommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value())) {
			userRole = RoleCode.QCA_SECRETARY.value();
		} else if (isQCBCommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value())) {
			userRole = RoleCode.QCB_SECRETARY.value();
		} else if (isQuotationCommitteeType() && getFacesScopes().hasRoleUser(RoleCode.QQC_SECRETARY.value())) {
			userRole = RoleCode.QQC_SECRETARY.value();
		} else if (ContractHashSetConst.COMMITTEE_TYPE_PROCUREMENT.contains(committeeType)){
			userRole = determineRoleSecretaryProcurement(userRole, committeeType);
		} else {
			String roleCode = committeeType + "_SECRETARY";
			userRole = resolveGenericRole(userRole, roleCode);
		}
		return userRole;
	}
	
	/**
	 * Determines and returns the appropriate chairperson role for the current user
	 * based on the committee type and the roles assigned to the user.
	 *
	 * <p>
	 * This method evaluates the committee type in the following order:
	 * <ul>
	 *     <li>Federal Procurement Board (SBH / SWK)</li>
	 *     <li>QCA Committee (WIP)</li>
	 *     <li>QCB Committee (WIP)</li>
	 *     <li>Quotation Committee</li>
	 *     <li>Generic committee chairperson role</li>
	 * </ul>
	 * </p>
	 *
	 * @param currentUserRole the current role assigned to the user
	 * @return the resolved chairperson role code
	 */
	public String determineRoleChairperson(String currentUserRole) {
		String userRole = currentUserRole;
		String committeeType = getManagedBean().getCommitteeType();

		if (ContractHashSetConst.COMMITTEE_TYPE_PROCUREMENT_SBH_SWK.contains(committeeType)
				&& getFacesScopes().hasRoleUser(RoleCode.FPB_CHAIRPERSON.value())) {
			userRole = RoleCode.FPB_CHAIRPERSON.value();
		} else if (isQCACommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCA_CHAIRPERSON.value())) {
			userRole = RoleCode.QCA_CHAIRPERSON.value();
		} else if (isQCBCommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCB_CHAIRPERSON.value())) {
			userRole = RoleCode.QCB_CHAIRPERSON.value();
		} else if (isQuotationCommitteeType() && getFacesScopes().hasRoleUser(RoleCode.QQC_CHAIRPERSON.value())) {
			userRole = RoleCode.QQC_CHAIRPERSON.value();
		} else {
			String roleCode = committeeType + "_CHAIRPERSON";
			userRole = resolveGenericRole(userRole, roleCode);
		}
		return userRole;
	}
	
	/**
	 * Determines and returns the appropriate member role for the current user
	 * based on the committee type and the roles assigned to the user.
	 *
	 * <p>
	 * This method evaluates the committee type in the following order:
	 * <ul>
	 *     <li>Federal Procurement Board (SBH / SWK)</li>
	 *     <li>QCA Committee (WIP)</li>
	 *     <li>QCB Committee (WIP)</li>
	 *     <li>Quotation Committee</li>
	 *     <li>Generic committee member role</li>
	 * </ul>
	 * </p>
	 *
	 * @param currentUserRole the current role assigned to the user
	 * @return the resolved member role code
	 */
	public String determineRoleMember(String currentUserRole) {
		String userRole = currentUserRole;
		String committeeType = getManagedBean().getCommitteeType();

		if (ContractHashSetConst.COMMITTEE_TYPE_PROCUREMENT_SBH_SWK.contains(committeeType)
				&& getFacesScopes().hasRoleUser(RoleCode.FPB_MEMBER.value())) {
			userRole = RoleCode.FPB_MEMBER.value();
		} else if (isQCACommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCA_MEMBER.value())) {
			userRole = RoleCode.QCA_MEMBER.value();
		} else if (isQCBCommitteeWIP() && getFacesScopes().hasRoleUser(RoleCode.QCB_MEMBER.value())) {
			userRole = RoleCode.QCB_MEMBER.value();
		} else if (isQuotationCommitteeType() && getFacesScopes().hasRoleUser(RoleCode.QQC_MEMBER.value())) {
			userRole = RoleCode.QQC_MEMBER.value();
		} else {
			String roleCode = committeeType + "_MEMBER";
			userRole = resolveGenericRole(userRole, roleCode);
		}
		return userRole;
	}
	
	/**
	 * Determines and returns the appropriate secretary role for the current user
	 * based on the committee type and the roles assigned to the user.
	 *
	 * <p>
	 * This method evaluates the committee type in the following order:
	 * <ul>
	 *     <li>Federal Procurement Board (PBA/PBB/PB)</li>
	 *     <li>Federal Procurement Board (SBH / SWK)</li>
	 * </ul>
	 * </p>
	 * 
	 * @param currentUserRole the current role assigned to the user
	 * @param committeeType current committee type
	 * @return the resolved member role code
	 */
	private String determineRoleSecretaryProcurement(String currentUserRole, String committeeType) {
		String userRole = currentUserRole;
		if (ContractHashSetConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_AB.contains(committeeType)) {
			if (getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value())) {
				userRole = RoleCode.PBB_SECRETARY.value();
			} else if (getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value())) {
				userRole = RoleCode.PPB_SECRETARY.value();
			}
		} else if (ContractHashSetConst.COMMITTEE_TYPE_PROCUREMENT_SBH_SWK.contains(committeeType)
				&& (getFacesScopes().hasRoleUser(RoleCode.FPB_SECRETARY.value()) 
						|| getFacesScopes().hasRoleUser(RoleCode.PPB_SECRETARY.value()))) { // #29740
			userRole = RoleCode.FPB_SECRETARY.value();
		}
		return userRole;
	}
	
	/**
	 * Resolves a generic role by checking whether the user has the given role code.
	 *
	 * @param currentUserRole the user's current role
	 * @param currentRoleCode the role code to be validated
	 * @return the expected role code if the user has it, otherwise the original role
	 */
	private String resolveGenericRole(String currentUserRole, String currentRoleCode) {
	    if (getFacesScopes().hasRoleUser(currentRoleCode)) {
	        return currentRoleCode;
	    }
	    return currentUserRole;
	}

}
