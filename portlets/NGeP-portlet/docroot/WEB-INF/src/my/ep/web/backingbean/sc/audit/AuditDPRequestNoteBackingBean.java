package my.ep.web.backingbean.sc.audit;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import my.ep.constant.sc.AuditDPRequestNoteConst;
import my.ep.dto.sc.audit.AuDPRequestNoteDTO;
import my.ep.dto.sc.audit.AuDPRequestNoteDetailSPDTO;
import my.ep.dto.sc.audit.AuDPRequestNoteSPDTO;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
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

	@Override
	public AuDPRequestNoteDTO insert(BaseManagedBean managedBean, Long userId, String roleCode) {
		RequestNoteManagedBean managedBeans = (RequestNoteManagedBean) managedBean;
		List<AuDPRequestNoteSPDTO> auDPRequestnoteSPDTOList = new ArrayList<>();
		AuDPRequestNoteSPDTO auDPRequestNoteSPDTO = new AuDPRequestNoteSPDTO();
		AuDPRequestNoteDTO auObj = new AuDPRequestNoteDTO();
		List<AuDPRequestNoteDetailSPDTO> auDPRequestnoteDetailSPDTOList = new ArrayList<>();
		AuDPRequestNoteDetailSPDTO data;
		for (ScRequestNoteDtl ri : managedBeans.getPurchaseRequestResDTO().getRequestNoteDtl()) {// #35492#58
			data = new AuDPRequestNoteDetailSPDTO();
			data.setSubDocId(BigInteger.valueOf(ri.getRequestItem().getRequestNoteItemId()));
			data.setSubDocNo(ri.getRequestItem().getItemName());
			data.setPurchaseRequestId(BigInteger.valueOf(ri.getPurchaseRequest().getPurchaseRequestId()));// #35492#58
			data.setCreatedBy(userId);// #35492#58
			auDPRequestnoteDetailSPDTOList.add(data);
		}
		auObj.setAuDPRequestNoteDetailSPDTOList(auDPRequestnoteDetailSPDTOList);
		auDPRequestNoteSPDTO.setDocTypeId(AuditDPRequestNoteConst.SC_DP_AU_RN_DOCTYPEID);
		auDPRequestNoteSPDTO.setRoleCode(roleCode);
		auDPRequestNoteSPDTO.setOrgProfileId(BigInteger.valueOf(managedBeans.getRequestNote().getPmOrgProfile()
				.getOrgProfileId()));
		auDPRequestNoteSPDTO.setDocNo(managedBeans.getRequestNote().getRequestNoteNo());
		auDPRequestNoteSPDTO.setDocId((managedBeans.getRequestNote().getRequestNoteId()).intValue());
		auDPRequestNoteSPDTO.setActionType(AuditDPRequestNoteConst.AUD_ACTION_TYPE_DEL);
		auDPRequestNoteSPDTO.setDpFieldLabelId(AuditDPRequestNoteConst.SC_DP_AU_RN_FIELDLABELID);
		auDPRequestNoteSPDTO.setNewValue(null);
		auDPRequestNoteSPDTO.setOldValue(null);
		auDPRequestNoteSPDTO.setCreatedBy(userId);// #35492#58
		
		auDPRequestnoteSPDTOList.add(auDPRequestNoteSPDTO);
		auObj.setAuDPRequestNoteSPDTOList(auDPRequestnoteSPDTOList);
		return auObj;
	}
}
