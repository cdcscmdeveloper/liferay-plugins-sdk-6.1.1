package my.ep.web.backingbean.sc.audit;

import java.io.Serializable;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.audit.AuDPRequestNoteDTO;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import org.apache.log4j.Logger;

/**
 * The Class AuditDPRequestNoteBackingBeanTemplate. #28895_35492
 * 
 * @author Muliana
 * @since 19.02.2024
 */
public abstract class AuditDPRequestNoteBackingBeanTemplate extends BaseBackingBean<BaseManagedBean> implements Serializable {
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(AuditDPRequestNoteBackingBeanTemplate.class);

	public AuditDPRequestNoteBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * The void auditSaveData.
	 * 
	 * @param managebean 
	 * @param userId 
	 */
	public final void auditSaveData(BaseManagedBean managebean, Long userId) {
		String roleCode = getRoleCode();

		AuDPRequestNoteDTO auObj = insert(managebean, userId, roleCode);

		dpAuditSO(auObj);
	}

	private String getRoleCode() {
		return getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value()) ? RoleCode.RN_APPROVER.value() : null;
	}

	/**
	 * The Abstract AuDPRequestNoteDTO.
	 * 
	 * @param managebean
	 * @param userId
	 * @param roleCode
	 * @return AuDPRequestNoteDTO
	 */
	public abstract AuDPRequestNoteDTO insert(BaseManagedBean managebean, Long userId, String roleCode);

	private void dpAuditSO(AuDPRequestNoteDTO dtlObjList) {
		try {
			IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
					
			auditDPRequestNoteSO.insertAuditDPRequestNote(dtlObjList.getAuDPRequestNoteSPDTOList(),
					dtlObjList.getAuDPRequestNoteDetailSPDTOList());
			
		} catch (Exception ex) {
			log.info("Catch Exception from TEMPLATE Send TO SO : " + ex);
		}
	}
}
