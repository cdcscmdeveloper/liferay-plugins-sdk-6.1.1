package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;
// #35501_Reopen#38
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author Firdaus Omar
 * @since 12.06.2024 #28895_35501
 */

public class AuditSupplierManagementPersonnelBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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
		SmPersonnelManagedBean mBean = (SmPersonnelManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		String oldRole = mBean.getOldRole();
		// #35501_Reopen#38 Start
		if (null != oldRole && !oldRole.isEmpty()) {
			// #35501_Reopen#132
			oldRole = PmCommonUtil.getRoleDescByRoleCodeAndLanguage(mBean.getOldRole(), AuditSupplierManagementConst.AU_LANGUAGE_CODE_BAHASA);
		}
		// #35501_Reopen#38 End
		String newRole = mBean.getNewRole();
		// #35501_Reopen#38 Start
		if (null != newRole && !newRole.isEmpty() && !newRole.equalsIgnoreCase(SupplierManagementConst.EPROLE_DELETED)) {
			// #35501_Reopen#132
			newRole = PmCommonUtil.getRoleDescByRoleCodeAndLanguage(mBean.getNewRole(), AuditSupplierManagementConst.AU_LANGUAGE_CODE_BAHASA);
		}
		// #35501_Reopen#38 End

		if (oldRole.isEmpty() && !newRole.isEmpty()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_EP_ROLE,
					null, newRole, null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_EP_ROLE));
		} else if (newRole.equalsIgnoreCase(SupplierManagementConst.EPROLE_DELETED)) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_EP_ROLE,
					oldRole, null, null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_EP_ROLE));
		} else if (!oldRole.equalsIgnoreCase(newRole)) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_EP_ROLE,
					oldRole, newRole, null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_EP_ROLE));
		}

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_USER_INFO, loginUserId);

		return auObj;
	}
}
