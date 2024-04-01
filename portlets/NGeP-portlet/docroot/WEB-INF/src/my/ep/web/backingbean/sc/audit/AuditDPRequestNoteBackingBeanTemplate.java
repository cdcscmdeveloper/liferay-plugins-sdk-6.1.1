package my.ep.web.backingbean.sc.audit;

import java.io.Serializable;
import my.ep.biz.service.sc.audit.IAuditDPRequestNoteSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.audit.AuDPRequestNoteDTO;
import my.ep.type.pm.RoleCode;
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
public abstract class AuditDPRequestNoteBackingBeanTemplate implements Serializable {
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(AuditDPRequestNoteBackingBeanTemplate.class);

	public AuditDPRequestNoteBackingBeanTemplate() {
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	/**
	 * Gets the audit dp request note so.
	 * 
	 * @return the audit dp request note so
	 */
	// private IAuditDPRequestNoteSO getAuditDpmRequestNoteSO() {
	// return
	// BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
	// IAuditDPRequestNoteSO.class);
	// }

	/**
	 * The void auditSaveData.
	 * 
	 * @param managebean
	 * @param userId
	 */
	public final void auditSaveData(BaseManagedBean managebean, Long userId) {
		log.info("AUDIT TEMPLATE START :::::: " + userId);
		String roleCode = getRoleCode();

		AuDPRequestNoteDTO auObj = insert(managebean, userId, roleCode);

		sendListToSO(auObj);

		sendToSOTest(auObj);

		sendListToSO2(auObj);

		dpAuditSO(auObj);
	}

	private String getRoleCode() {
		return getFacesScopes().hasRoleUser(RoleCode.RN_APPROVER.value()) ? RoleCode.RN_APPROVER.value() : null;
	}

	/**
	 * The Abtract AuDPRequestNoteDTO.
	 * 
	 * @param managebean
	 * @param userId
	 * @param roleCode
	 * @return AuDPRequestNoteDTO
	 */
	public abstract AuDPRequestNoteDTO insert(BaseManagedBean managebean, Long userId, String roleCode);

	private void sendListToSO(AuDPRequestNoteDTO dtlObjList) {
		log.info("1. AUDIT TEMPLATE test send a Detail list to SO :::::: "
				+ dtlObjList.getAuDPRequestNoteSPDTOList().size());
		log.info("2. AUDIT TEMPLATE test send a Master list to SO :::::: "
				+ dtlObjList.getAuDPRequestNoteDetailSPDTOList().size());
		try {
			IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
			auditDPRequestNoteSO.sendListToSO(dtlObjList.getAuDPRequestNoteSPDTOList(),
					dtlObjList.getAuDPRequestNoteDetailSPDTOList());
		} catch (Exception ex) {
			log.info("sendListToSO Catch Exception TEMPLATE SEND TO SO : ");
			ex.printStackTrace();
		}
	}

	private void sendToSOTest(AuDPRequestNoteDTO dtlObjList) {
		log.info("AUDIT TEMPLATE test send to SO :::::: ");
		try{
		IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
		auditDPRequestNoteSO.sendToSOTest(dtlObjList.getAuDPRequestNoteSPDTOList(),
				dtlObjList.getAuDPRequestNoteDetailSPDTOList());
		} catch (Exception ex) {
			log.info("sendToSOTest Catch Exception TEMPLATE SEND TO SO : ");
			ex.printStackTrace();
		}
	}

	private void sendListToSO2(AuDPRequestNoteDTO dtlObjList) {
		log.info("1. AUDIT TEMPLATE test send a Detail list to SO2 :::::: "
				+ dtlObjList.getAuDPRequestNoteSPDTOList().size());
		log.info("2. AUDIT TEMPLATE test send a Master list to SO2 :::::: "
				+ dtlObjList.getAuDPRequestNoteDetailSPDTOList().size());
		try{
		IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
		auditDPRequestNoteSO.sendListToSO2(dtlObjList.getAuDPRequestNoteSPDTOList(),
				dtlObjList.getAuDPRequestNoteDetailSPDTOList());
		} catch (Exception ex) {
			log.info("sendListToSO2 Catch Exception TEMPLATE SEND TO SO : ");
			ex.printStackTrace();
		}
	}

	private void dpAuditSO(AuDPRequestNoteDTO dtlObjList) {
		log.info("AUDIT TEMPLATE AuDPRequestNoteSPDTOList DETAIL SEND TO DAO :::::: "
				+ dtlObjList.getAuDPRequestNoteSPDTOList().size());
		log.info("AUDIT TEMPLATE AuDPRequestNoteDetailSPDTOLisT MASTER SEND TO DAO :::::: "
				+ dtlObjList.getAuDPRequestNoteDetailSPDTOList().size());
		try {
			IAuditDPRequestNoteSO auditDPRequestNoteSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IAuditDPRequestNoteSO.class);
			log.info("TEMPLATE!!!");
			auditDPRequestNoteSO.insertAuditDPRequestNote(dtlObjList.getAuDPRequestNoteSPDTOList(),
					dtlObjList.getAuDPRequestNoteDetailSPDTOList());
		} catch (Exception ex) {
			log.info("dpAuditSO Catch Exception TEMPLATE SEND TO SO : ");
			ex.printStackTrace();
		}
	}
}
