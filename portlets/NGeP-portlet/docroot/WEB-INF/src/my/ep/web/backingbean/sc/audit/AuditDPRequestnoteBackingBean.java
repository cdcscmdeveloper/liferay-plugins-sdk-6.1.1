package my.ep.web.backingbean.sc.audit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import my.ep.dto.sc.audit.AuDPRequestnoteDTO;
import my.ep.dto.sc.audit.AuDPRequestnoteDetailSPDTO;
import my.ep.dto.sc.audit.AuDPRequestnoteSPDTO;
import my.ep.persistence.entity.sc.directpurchase.ScRequestNoteDtl;
import my.ep.web.constant.sc.dp.audit.AuditConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sc.requestnote.RequestNoteManagedBean;

/**
 * The Class AuditDPRequestnoteBackingBean.
 * #28895_35492
 * @author Muliana
 * @since 19.02.2024
 */
public class AuditDPRequestnoteBackingBean extends AuditDPRequestnoteBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	@Override
	public AuDPRequestnoteDTO insert(BaseManagedBean managedBean, Long userId, String roleCode) {
		RequestNoteManagedBean managedBeans = (RequestNoteManagedBean) managedBean;

		List<AuDPRequestnoteSPDTO> auDPRequestnoteSPDTOList = new ArrayList<>();
		AuDPRequestnoteSPDTO auDPRequestnoteSPDTO = new AuDPRequestnoteSPDTO();
		AuDPRequestnoteDTO auObj = new AuDPRequestnoteDTO();
		List<AuDPRequestnoteDetailSPDTO> auDPRequestnoteDetailSPDTOList = new ArrayList<>();

		for (ScRequestNoteDtl ri : managedBeans.getPurchaseRequestResDTO().getPurchaseRequest().getRequestNoteDtls()) {
			AuDPRequestnoteDetailSPDTO data = new AuDPRequestnoteDetailSPDTO();
			data.setSubDocId(new BigDecimal(ri.getRequestItem().getRequestNoteItemId()));
			data.setSubDocNo(ri.getRequestItem().getItemName());
			auDPRequestnoteDetailSPDTOList.add(data);
		}
		auObj.setAuDPRequestnoteDetailSPDTOList(auDPRequestnoteDetailSPDTOList);
		auDPRequestnoteSPDTO.setDocTypeId(AuditConst.SC_DP_AU_RN_DOCTYPEID);
		auDPRequestnoteSPDTO.setRoleCode(roleCode);
		auDPRequestnoteSPDTO.setOrgProfileId(BigDecimal.valueOf(managedBeans.getRequestNote().getPmOrgProfile()
				.getOrgProfileId()));
		auDPRequestnoteSPDTO.setDocId(BigDecimal.valueOf(managedBeans.getRequestNote().getRequestNoteId()));
		auDPRequestnoteSPDTO.setDocNo(managedBeans.getRequestNote().getRequestNoteNo());
		auDPRequestnoteSPDTO.setActionType(AuditConst.AUD_ACTION_TYPE_DEL);
		auDPRequestnoteSPDTO.setDpFieldLabelId(AuditConst.SC_DP_AU_RN_FIELDLABELID);
		auDPRequestnoteSPDTO.setNewValue(null);
		auDPRequestnoteSPDTO.setOldValue(null);
		auDPRequestnoteSPDTO.setPurchaseRequestId(new BigDecimal(managedBeans.getPurchaseRequestResDTO()
				.getPurchaseRequest().getPurchaseRequestId()));

		auDPRequestnoteSPDTOList.add(auDPRequestnoteSPDTO);
		auObj.setAuDPRequestnoteSPDTOList(auDPRequestnoteSPDTOList);
		return auObj;
	}
}
