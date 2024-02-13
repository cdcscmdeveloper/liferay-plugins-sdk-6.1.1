package my.ep.web.backingbean.pm.audit.location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import my.ep.biz.service.pm.common.audittrail.IAuditLocationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.location.AUPMLocationMasterSPDTO;
import my.ep.dto.pm.common.audit.location.AuditPMBaseDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

public abstract class AuditPMLocationBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditPMLocationBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	IAuditLocationSO pmauditSO = (IAuditLocationSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAuditLocationSO.class);

	public final void auditSaveData(BaseManagedBean managebean, Long userId) {

		String roleCode = getRoleCode();

		AuditPMBaseDTO auPMObj = detailsInsert(managebean, userId);

		ArrayList<AUPMLocationMasterSPDTO> masterObjlist = masterInsert(auPMObj, userId, managebean, roleCode);

		pmauditSO(masterObjlist, auPMObj);
	}

	private String getRoleCode() {
		String roleCode = getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value()
				: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value()) ? RoleCode.EP_ADMIN_1.value()
						: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value()
								: null;
		return roleCode;
	}

	public abstract AuditPMBaseDTO detailsInsert(BaseManagedBean managebean, Long userId);

	public abstract ArrayList<AUPMLocationMasterSPDTO> masterInsert(AuditPMBaseDTO auPMObj, Long userId,
			BaseManagedBean managebean, String roleCode);

	private void pmauditSO(List<AUPMLocationMasterSPDTO> masterObjlist, AuditPMBaseDTO auPMObj) {
		pmauditSO.insertAUDITPMSP(masterObjlist, auPMObj.getDetailObjlist());
	}
}
