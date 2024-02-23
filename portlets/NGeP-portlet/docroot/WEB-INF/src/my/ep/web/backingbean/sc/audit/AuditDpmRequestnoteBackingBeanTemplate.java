package my.ep.web.backingbean.sc.audit;

import java.io.Serializable;

import my.ep.biz.service.sc.audit.IAuditDPRequestnoteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.audit.AuDPRequestnoteDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * The Class AuditDpmRequestnoteBackingBeanTemplate.
 * 
 * @author Muliana
 * @since 19.02.2024
 */
public abstract class AuditDpmRequestnoteBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditDpmRequestnoteBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * Gets the audit dp request note so.
	 * 
	 * @return the audit dp request note so
	 */
	private IAuditDPRequestnoteSO getAuditDpmRequestnoteSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IAuditDPRequestnoteSO.class);
	}

	/**
	 * The void auditSaveData.
	 * 
	 * @param managebean
	 * @param userId
	 */
	public final void auditSaveData(BaseManagedBean managebean, Long userId) {

		String roleCode = getRoleCode();

		AuDPRequestnoteDTO auObj = insert(managebean, userId, roleCode);

		dpauditSO(auObj);
	}

	private String getRoleCode() {
		return getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value()) ? RoleCode.EP_ADMIN.value() : getFacesScopes()
				.hasRoleUser(RoleCode.EP_ADMIN_2.value()) ? RoleCode.EP_ADMIN_2.value() : getFacesScopes().hasRoleUser(
				RoleCode.RN_APPROVER.value()) ? RoleCode.RN_APPROVER.value() : null;
	}

	/**
	 * The Abtract AuDPRequestnoteDTO.
	 * 
	 * @param managebean
	 * @param userId
	 * @param roleCode
	 * @return AuDPRequestnoteDTO
	 */
	public abstract AuDPRequestnoteDTO insert(BaseManagedBean managebean, Long userId, String roleCode);

	private void dpauditSO(AuDPRequestnoteDTO dtlObjlist) {
		getAuditDpmRequestnoteSO().insertAuditDPRequestnote(dtlObjlist.getAuDPRequestnoteSPDTOList(),
				dtlObjlist.getAuDPRequestnoteDetailSPDTOList());
	}
}
