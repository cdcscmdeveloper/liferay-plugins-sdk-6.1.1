package my.ep.web.backingbean.pm.location.audit;

import java.io.Serializable;
import java.util.ArrayList;

import my.ep.biz.service.pm.common.audittrail.IAudittrailSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audittrail.AUPMMasterSPDTO;
import my.ep.dto.pm.common.audittrail.AuditPMBaseDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

public abstract class AuditPMBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditPMBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	IAudittrailSO pmauditSO = (IAudittrailSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.PROFILE_MANAGEMENT, IAudittrailSO.class);

	public final void auditSaveData(BaseManagedBean managebean, Long userId) {

		String roleCode = getRoleCode();

		AuditPMBaseDTO auPMObj = detailsInsert(managebean, userId);

		ArrayList<AUPMMasterSPDTO> masterObjlist = masterInsert(auPMObj, userId, managebean, roleCode);

		pmauditSO(masterObjlist, auPMObj);
	}

	private String getRoleCode() {
		String roleCode = getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value()
				: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value()) ? RoleCode.EP_ADMIN_1.value()
						: getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value(): null;
		return roleCode;
	}

	public abstract AuditPMBaseDTO detailsInsert(BaseManagedBean managebean, Long userId);

	public abstract ArrayList<AUPMMasterSPDTO> masterInsert(AuditPMBaseDTO auPMObj, Long userId,
			BaseManagedBean managebean, String roleCode);
	
	private void pmauditSO(ArrayList<AUPMMasterSPDTO> masterObjlist, AuditPMBaseDTO auPMObj) {
		pmauditSO.insertAUDITPMSP(masterObjlist, auPMObj.detailObjlist);
	}
}
