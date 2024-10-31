package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.forcereplaceadmin.ForceReplaceSuppAdminManagedBean;
import my.ep.web.managedbean.sm.userprofilemaintenance.SmPersonnelManagedBean;

/**
 * @author Firdaus Omar
 * @since 22.07.2024 #28895_35501
 */

public class AuditSupplierManagementAdminInformationBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		String oldAdminIC;
		String newAdminIC;
		SmSupplier supplier;
		String oldRoleCode;
		PmOrgProfile oldOrg;
		if (managedBean.isForceReplace()) {
			ForceReplaceSuppAdminManagedBean mBean = (ForceReplaceSuppAdminManagedBean) managedBean;
			oldAdminIC = mBean.getOldIdentification();
			newAdminIC = mBean.getNewIdentifiction();
			supplier = mBean.getSmSupplier();
			oldOrg = null;
			oldRoleCode = null;
		} else {
			SmPersonnelManagedBean mBean = (SmPersonnelManagedBean) managedBean;
			supplier = mBean.getOldSmSupplier();
			oldOrg = mBean.getOldOrg();
			oldRoleCode = mBean.getOldRole();
			oldAdminIC = mBean.getUpdateSmPersonnel().getIdentificationNo();
			newAdminIC = mBean.getSelectedPersonnelToReassign().getIdentificationNo();
		}
		auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
				AuditSupplierManagementConst.AU_FIELD_LABEL_ID_ADMIN_IDENTIFICATION_NO, oldAdminIC, newAdminIC, null, null,
				AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_ADMIN_IDENTIFICATION_NO));
		auditBaseData(auObj, supplier, oldRoleCode, oldOrg, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_ADMIN_INFO, loginUserId);
		return auObj;
	}
}
