package my.ep.web.backingbean.pm.audit.uom;

import java.io.Serializable;
import my.ep.biz.service.pm.common.audittrail.IAuditLocationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.uom.AuPMUomDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

public abstract class AuditPMUomBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditPMUomBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	IAuditLocationSO pmauditSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

	public final void auditSaveData(BaseManagedBean managebean, Long userId) {

		String roleCode = getRoleCode();

		AuPMUomDTO auPMObj = detailsInsert(managebean, userId);

		AuPMUomDTO masterObjlist = masterInsert(auPMObj, userId, managebean, roleCode);

		pmauditSO(masterObjlist, auPMObj, managebean);
	}

	private String getRoleCode() {
		String roleCode = getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value()
				: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value()
						: getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
		return roleCode;
	}

	public abstract AuPMUomDTO detailsInsert(BaseManagedBean managebean, Long userId);

	public abstract AuPMUomDTO masterInsert(AuPMUomDTO auPMObj, Long userId, BaseManagedBean managebean,
			String roleCode);

	private void pmauditSO(AuPMUomDTO masterObjlist, AuPMUomDTO dtlObjlist, BaseManagedBean managebean) {
		masterObjlist.setAuditEnable(false);
		pmauditSO.insertPMAuditUom(masterObjlist.getUomMasterObjlist(), dtlObjlist.getUomDetailObjlist());
	}
}
