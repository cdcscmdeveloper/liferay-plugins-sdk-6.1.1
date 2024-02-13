package my.ep.web.backingbean.pm.audit.race;

import java.io.Serializable;

import my.ep.biz.service.pm.common.audittrail.IAuditLocationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.race.AuPmRaceDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

public abstract class AuditPMRaceBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditPMRaceBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	IAuditLocationSO pmauditSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

	public final void auditSaveData(BaseManagedBean managebean, Long userId) {

		String roleCode = getRoleCode();

		AuPmRaceDTO auPMObj = detailsInsert(managebean, userId);

		AuPmRaceDTO masterObjlist = masterInsert(auPMObj, userId, managebean, roleCode);

		pmauditSO(masterObjlist, auPMObj, managebean);
	}

	private String getRoleCode() {
		String roleCode = getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value()
				: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value()
						: getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value()) ? RoleCode.BPK_ADMIN.value() : null;
		return roleCode;
	}

	public abstract AuPmRaceDTO detailsInsert(BaseManagedBean managebean, Long userId);

	public abstract AuPmRaceDTO masterInsert(AuPmRaceDTO auPMObj, Long userId, BaseManagedBean managebean,
			String roleCode);

	private void pmauditSO(AuPmRaceDTO masterObjlist, AuPmRaceDTO dtlObjlist, BaseManagedBean managebean) {
		pmauditSO.insertPMAuditRace(masterObjlist.getRaceMasterObjlist(), dtlObjlist.getRaceDetailObjlist());
	}
}
