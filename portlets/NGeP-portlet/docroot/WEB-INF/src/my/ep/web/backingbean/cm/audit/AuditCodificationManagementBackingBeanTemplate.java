package my.ep.web.backingbean.cm.audit;

import java.io.Serializable;

import my.ep.biz.service.cm.audit.IAuditCodificationManagementSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.cm.AuditCMConst;
import my.ep.dto.cm.audit.AuditCodificationManagementDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * New file introduced for Audit Trail Codification Management (#28895 - #35503) 
 * The backing bean template for the Codification Management audit trail
 * 
 * @author Irfan
 * @since 10.09.2024
 */
@SuppressWarnings("rawtypes")
public abstract class AuditCodificationManagementBackingBeanTemplate extends BaseBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * To save Item Code Creation data into SP
	 */
	public final void saveAuditItemCodeCreation() {
		String roleCode = getRoleCode();
		Long orgProfileId = getOrgProfileId();

		AuditCodificationManagementDTO auObj = insertItemCodeCreation(roleCode, orgProfileId);

		cmAuditSO(auObj);
	}

	/**
	 * To save Extension Code Creation data into SP
	 */
	public final void saveAuditExtensionCodeCreation() {
		String roleCode = getRoleCode();
		Long orgProfileId = getOrgProfileId();

		AuditCodificationManagementDTO auObj = insertExtensionCodeCreation(roleCode, orgProfileId);

		cmAuditSO(auObj);
	}

	/**
	 * To save Item Code Maintenance data into SP
	 */
	public final void saveAuditItemCodeMaintenance() {
		String roleCode = getRoleCode();
		Long orgProfileId = getOrgProfileId();

		AuditCodificationManagementDTO auObj = insertItemCodeMaintenance(roleCode, orgProfileId);

		cmAuditSO(auObj);
	}

	/**
	 * To save Extension Code Maintenance data into SP
	 */
	public final void saveAuditExtensionCodeMaintenance() {
		String roleCode = getRoleCode();
		Long orgProfileId = getOrgProfileId();

		AuditCodificationManagementDTO auObj = insertExtensionCodeMaintenance(roleCode, orgProfileId);

		cmAuditSO(auObj);
	}

	/**
	 * To insert data into object for Item Code Creation
	 * 
	 * @param roleCode
	 * @param orgProfileId
	 * @return auObj
	 */
	public abstract AuditCodificationManagementDTO insertItemCodeCreation(String roleCode, Long orgProfileId);

	/**
	 * To insert data into object for Extension Code Creation
	 * 
	 * @param roleCode
	 * @param orgProfileId
	 * @return auObj
	 */
	public abstract AuditCodificationManagementDTO insertExtensionCodeCreation(String roleCode, Long orgProfileId);

	/**
	 * To insert data into object for Item Code Maintenance
	 * 
	 * @param roleCode
	 * @param orgProfileId
	 * @return auObj
	 */
	public abstract AuditCodificationManagementDTO insertItemCodeMaintenance(String roleCode, Long orgProfileId);

	/**
	 * To insert data into object for Extension Code Maintenance
	 * 
	 * @param roleCode
	 * @param orgProfileId
	 * @return auObj
	 */
	public abstract AuditCodificationManagementDTO insertExtensionCodeMaintenance(String roleCode, Long orgProfileId);

	/**
	 * To get faces scopes for get role code
	 */
	public FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * To get role code
	 */
	private String getRoleCode() {
		return getFacesScopes().hasRoleUser(RoleCode.CD_OFFICER.value()) ? RoleCode.CD_OFFICER.value() : null;
	}

	/**
	 * To get org profile ID
	 */
	private Long getOrgProfileId() {
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);

		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setUserId(getUserIdSessionFromLoginId());
		UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
		return userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId();
	}

	/**
	 * The void cmAuditSO. To pass object to SP
	 * 
	 * @param auCmDTO
	 */
	private void cmAuditSO(AuditCodificationManagementDTO auCmDTO) {
		try {
			IAuditCodificationManagementSO auditCMSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CATALOGUE_MANAGEMENT, IAuditCodificationManagementSO.class);
			auditCMSO.insertCmAudit(auCmDTO.getListAuditSP());
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(AuditCMConst.AUDIT_CODIFICATION_MANAGEMENT, "cmAuditSO", null,
					"AuditCodificationManagementBackingBeanTemplate", null, ex);
		}
	}
}
