package my.ep.web.backingbean.sm.audit;

import java.io.Serializable;
import java.util.List;

import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public abstract class AuditSupplierManagementBackingBeanTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(AuditSupplierManagementBackingBeanTemplate.class);

	public AuditSupplierManagementBackingBeanTemplate() {
	}

	public final void saveAuditData(BaseManagedBean managebean, Long userId) {
		String roleCode = getRoleCode();
		PmUser user = retriveUser(userId);
		PmOrgProfile org = retriveUserOrg(userId);
		AuSupplierManagementDTO auObj = prepareAuditData(managebean, org, user, roleCode);
		if(auObj.getAuditDetails().size() > 0){
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

	private PmUser retriveUser(Long userId) {
		PmUser user = PmCommonUtil.retrieveUserById(userId, false);
		return user;
	}

	private PmOrgProfile retriveUserOrg(Long userOrgId) {
		new PmOrgProfile();
		List<PmUserOrg> orgList = PmCommonUtil.retrieveActiveOrg(userOrgId);
		PmOrgProfile org = orgList.get(0).getPmOrgProfile();
		return org;
	}

	public abstract AuSupplierManagementDTO prepareAuditData(BaseManagedBean managebean, PmOrgProfile org, PmUser user,
			String roleCode);

	private void auditSMSO(AuSupplierManagementDTO auObj) {
		try {
			IAuditSupplierManagementSO auSMSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IAuditSupplierManagementSO.class);
			auSMSO.insertAudit(auObj);
		} catch (Exception ex) {
			log.info("Catch Exception from TEMPLATE Send TO SO : ", ex);
		}
	}
}
