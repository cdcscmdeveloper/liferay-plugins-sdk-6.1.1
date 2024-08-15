package my.ep.web.backingbean.pm.audit;

import java.io.Serializable;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author normuliana
 * 
 */
public abstract class AuditPMBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	public AuditPMBackingBeanTemplate() {
	}

	/**
	 * @param manageBean
	 * @param modName
	 */
	public final void auditSaveData(BaseManagedBean manageBean, String modName) {

		String roleCode = getRoleCode();

		AuditPMReqDTO auObjLocation = insert(manageBean, roleCode);

		pmAuditSO(auObjLocation, modName);
	}

	/**
	 * @param modName
	 * @return
	 */
	public abstract String getRoleCode();

	/**
	 * @param manageBean
	 * @param modName
	 * @param roleCode
	 * @return
	 */
	public abstract AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode);

	/**
	 * @param auditPMReqDTO
	 */
	private void pmAuditSO(AuditPMReqDTO auditPMReqDTO, String modName) {
		IAuditPMSO auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);

		auditPMSO.insertAuditStoreProc(auditPMReqDTO.getObjList(), modName);
	}
}
