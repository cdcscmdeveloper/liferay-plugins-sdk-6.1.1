package my.ep.web.backingbean.sc.audit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.constant.sc.AuditDPRequestNoteConst;
import my.ep.dto.sc.audit.AuDPRequestNoteDTO;
import my.ep.dto.sc.audit.AuDPRequestNoteDetailSPDTO;
import my.ep.dto.sc.audit.AuDPRequestNoteSPDTO;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.web.backingbean.sc.requestnote.RequestNoteBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sc.requestnote.RequestNoteManagedBean;

/**
 * The Class AuditDPRequestNoteBackingBean. #28895_35492
 * 
 * @author Muliana
 * @since 19.02.2024
 */
public class AuditDPRequestNoteBackingBean extends AuditDPRequestNoteBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	/** The log. */
	private static Logger log = Logger.getLogger(AuditDPRequestNoteBackingBean.class);
	@Override
	public AuDPRequestNoteDTO insert(BaseManagedBean managedBean, Long userId, String roleCode) {
		log.info("AUDIT MAP BEAN START :::::: " + roleCode + " <<<userId>>> " + userId);
		RequestNoteManagedBean managedBeans = (RequestNoteManagedBean) managedBean;

		List<AuDPRequestNoteSPDTO> auDPRequestnoteSPDTOList = new ArrayList<>();
		AuDPRequestNoteSPDTO auDPRequestNoteSPDTO = new AuDPRequestNoteSPDTO();
		AuDPRequestNoteDTO auObj = new AuDPRequestNoteDTO();
		List<AuDPRequestNoteDetailSPDTO> auDPRequestnoteDetailSPDTOList = new ArrayList<AuDPRequestNoteDetailSPDTO>();
		AuDPRequestNoteDetailSPDTO data;
		log.info("START ADD DETAIL LIST :::::: ");
		for (ScRequestNoteDtl ri : managedBeans.getPurchaseRequestResDTO().getPurchaseRequest().getRequestNoteDtls()) {
			data = new AuDPRequestNoteDetailSPDTO();
			data.setSubDocId(BigInteger.valueOf(ri.getRequestItem().getRequestNoteItemId()));
			data.setSubDocNo(ri.getRequestItem().getItemName());
			auDPRequestnoteDetailSPDTOList.add(data);
		}
		log.info("auDPRequestnoteDetailSPDTOList.size DETAIL MAP DATA LIST:::::: " + auDPRequestnoteDetailSPDTOList.size());
		auObj.setAuDPRequestNoteDetailSPDTOList(auDPRequestnoteDetailSPDTOList);
		log.info("auObj.getAuDPRequestNoteDetailSPDTOList().size() :::::: " + auObj.getAuDPRequestNoteDetailSPDTOList().size());
		auDPRequestNoteSPDTO.setDocTypeId(AuditDPRequestNoteConst.SC_DP_AU_RN_DOCTYPEID);
		auDPRequestNoteSPDTO.setRoleCode(roleCode);
		auDPRequestNoteSPDTO.setOrgProfileId(BigInteger.valueOf(managedBeans.getRequestNote().getPmOrgProfile()
				.getOrgProfileId()));
		auDPRequestNoteSPDTO.setDocId((managedBeans.getRequestNote().getRequestNoteId()).intValue());
		auDPRequestNoteSPDTO.setDocNo(managedBeans.getRequestNote().getRequestNoteNo());
		auDPRequestNoteSPDTO.setActionType(AuditDPRequestNoteConst.AUD_ACTION_TYPE_DEL);
		auDPRequestNoteSPDTO.setDpFieldLabelId(AuditDPRequestNoteConst.SC_DP_AU_RN_FIELDLABELID);
		auDPRequestNoteSPDTO.setNewValue(null);
		auDPRequestNoteSPDTO.setOldValue(null);
		auDPRequestNoteSPDTO.setPurchaseRequestId(BigInteger.valueOf(managedBeans.getPurchaseRequestResDTO()
				.getPurchaseRequest().getPurchaseRequestId()));

		auDPRequestnoteSPDTOList.add(auDPRequestNoteSPDTO);
		auObj.setAuDPRequestNoteSPDTOList(auDPRequestnoteSPDTOList);
		log.info("auObj.getAuDPRequestNoteSPDTOList().size() MASTER MAP DATA LIST :::::: " + auObj.getAuDPRequestNoteSPDTOList().size());
		return auObj;
	}
}
