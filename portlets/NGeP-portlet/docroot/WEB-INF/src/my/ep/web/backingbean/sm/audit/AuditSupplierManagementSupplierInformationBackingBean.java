package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

/**
 * @author Firdaus Omar
 * @since 24.06.2024 #28895_35501
 */

public class AuditSupplierManagementSupplierInformationBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * Prepares audit data
	 * 
	 * @param managedBean
	 * @param roleCode
	 * @param org
	 * @return AuditSupplierManagementDTO
	 */
	@Override
	public AuditSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org, Long loginUserId) {
		AuditSupplierManagementDTO auObj = new AuditSupplierManagementDTO();
		ViewSuppProfileManagedBean mBean = (ViewSuppProfileManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		SmAppl oldSmAppl = mBean.getOldSmAppl();
		SmAppl newSmAppl = mBean.getSmAppl();

		List<PmStatusDesc> descList;
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setLanguageCode(AuditSupplierManagementConst.AU_LANGUAGE_CODE_BAHASA);
		descList = SmServiceLookup.getStatusSO().retrieveRegStatusList(reqDTO).getStatusDescList();

		if (!oldSmAppl.getPmRegStatus().getPmStatus().getStatusId().equals(newSmAppl.getPmRegStatus().getPmStatus().getStatusId())) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_REGISTRATION_STATUS,
					getPmStatusDescs(descList, oldSmAppl.getPmRegStatus().getPmStatus().getStatusId()),
					getPmStatusDescs(descList, newSmAppl.getPmRegStatus().getPmStatus().getStatusId()), oldSmAppl.getPmRegStatus().getPmStatus()
							.getStatusId(), newSmAppl.getPmRegStatus().getPmStatus().getStatusId(),
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_REGISTRATION_STATUS));
		}
		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_SUPPLIER_INFO,
				loginUserId);
		return auObj;
	}

	private String getPmStatusDescs(List<PmStatusDesc> statusList, Long pmStatusId) {
		String statusName = "";
		if (!SmCommonUtil.isCollectionEmpty(statusList) && pmStatusId != null) {
			// Expected Max Size in loop (5) based on NGEP_PDS database
			for (PmStatusDesc status : statusList) {
				if (status.getPmStatus().getStatusId().equals(pmStatusId)) {
					statusName = status.getStatusName();
					break;
				}
			}
		}
		return statusName;
	}
}
