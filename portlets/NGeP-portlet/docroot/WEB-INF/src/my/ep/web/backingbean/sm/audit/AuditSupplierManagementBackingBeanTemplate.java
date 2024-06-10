package my.ep.web.backingbean.sm.audit;

import java.io.Serializable;
import java.util.List;

import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public abstract class AuditSupplierManagementBackingBeanTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	private LogKey logKey = new LogKey();

	public AuditSupplierManagementBackingBeanTemplate() {
	}

	/**
	 * @param managebean
	 * @param userId
	 * 
	 *            Save audit data
	 */
	public final void saveAuditData(BaseManagedBean managebean, Long userId) {
		String roleCode = getRoleCode();
		PmOrgProfile org = retriveUserOrg(userId);
		AuSupplierManagementDTO auObj = prepareAuditData(managebean, roleCode, org);
		if (!auObj.getAuditDetails().isEmpty()) {
			auditSMSO(auObj);
		}
	}

	private String getRoleCode() {

		String roleCode = null;

		if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			roleCode = RoleCode.BASIC_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.G2G_SUPPLIER_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			roleCode = RoleCode.G2G_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			roleCode = RoleCode.MOF_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.MOF_SUPPLIER_ADMIN.value();
		}

		return roleCode;
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	private PmOrgProfile retriveUserOrg(Long userOrgId) {
		new PmOrgProfile();
		List<PmUserOrg> orgList = PmCommonUtil.retrieveActiveOrg(userOrgId);
		return orgList.get(0).getPmOrgProfile();
	}

	/**
	 * @param managedBean
	 * @param org
	 * @param user
	 * @param roleCode
	 * @return AuSupplierManagementDTO prepare audit trail data
	 */
	public abstract AuSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode,
			PmOrgProfile org);

	protected AuSMDetailDTO createAuditData(String actionType, Long fieldLabel, String newValue, String oldValue,
			Long newValueId, Long oldValueId, Long groupSection) {
		AuSMDetailDTO auDetail = new AuSMDetailDTO();
		auDetail.setActionType(actionType);
		auDetail.setSmFieldLabelId(fieldLabel);
		auDetail.setNewValue(newValue);
		auDetail.setOldValue(oldValue);
		auDetail.setOldValueId(newValueId);
		auDetail.setNewValueId(oldValueId);
		auDetail.setGroupSectionId(groupSection);
		return auDetail;
	}

	private void auditSMSO(AuSupplierManagementDTO auObj) {
		try {
			IAuditSupplierManagementSO auSMSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IAuditSupplierManagementSO.class);
			auSMSO.insertAudit(auObj);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "insert", auObj.getDocNo(),
					"AuditSupplierManagementSO", logKey.getUserId(), ex);
		}
	}
}
