package my.ep.web.backingbean.pm.audit;

import java.io.Serializable;

import org.apache.log4j.Logger;

import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.logging.LogKey;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * @author normuliana - 28895-35509
 * 
 */
public abstract class AuditPMBackingBeanTemplate extends BaseBackingBean<BaseManagedBean> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(AuditPMBackingBeanTemplate.class);

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
		new LogKey(getUserIdSessionFromLoginId());

		IAuditPMSO auditPMSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IAuditPMSO.class);
		try {
			if (!auditPMReqDTO.getObjList().isEmpty() && auditPMReqDTO.getObjList() != null) {
				auditPMSO.insertAuditStoreProc(auditPMReqDTO.getObjList(), modName);
			} else {
				throw new EpException(MessageConst.ERROR_880001);
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.PROFILE_MANAGEMENT, "insert", modName, "auditPMSO",
					new LogKey(getUserIdSessionFromLoginId()).getUserId(), e);
		}
	}
}
