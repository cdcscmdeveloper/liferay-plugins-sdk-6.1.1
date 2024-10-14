package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;

/**
 * @author Firdaus Omar
 * @since 27.06.2024 #28895_35501
 */

public class AuditSupplierManagementOverlappingCategoryBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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

		String oldPoApprOverlappedRemark = mBean.getOldPoApprOverlappedRemark();
		String poApprOverlappedRemark = mBean.getPoApprOverlappedRemark();

		if (oldPoApprOverlappedRemark == null || " ".equals(oldPoApprOverlappedRemark)) {
			if (!poApprOverlappedRemark.isEmpty() && !" ".equals(poApprOverlappedRemark)) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK, null, poApprOverlappedRemark, null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK));
			}
		} else {
			if (poApprOverlappedRemark.isEmpty()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK, oldPoApprOverlappedRemark, null, null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK));
			} else if (!oldPoApprOverlappedRemark.equals(poApprOverlappedRemark)) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK, oldPoApprOverlappedRemark, poApprOverlappedRemark, null,
						null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OVERLAPPING_REMARK));
			}
		}

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_OVERLAPPING_REMARKS,
				loginUserId);

		return auObj;
	}
}
