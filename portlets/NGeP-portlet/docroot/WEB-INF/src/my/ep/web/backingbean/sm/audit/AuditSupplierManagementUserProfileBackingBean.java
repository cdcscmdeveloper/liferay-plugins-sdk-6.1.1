package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profilepersonalization.ProfilePersonalizationManagedBean;

/**
 * @author Firdaus Omar
 * @since 12.06.2024 #28895_35501
 */

public class AuditSupplierManagementUserProfileBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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
		ProfilePersonalizationManagedBean mBean = (ProfilePersonalizationManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		PmUser oldUser = mBean.getOldSelectedUser();
		PmUser newUser = mBean.getSelectedUser();

		String oldPhoneNo = oldUser.getMobileCountry() + oldUser.getMobileArea() + oldUser.getMobileNo();
		String newPhoneNo = newUser.getMobileCountry() + newUser.getMobileArea() + newUser.getMobileNo();

		if (!oldPhoneNo.equalsIgnoreCase(newPhoneNo)) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_MOBILE_NO, oldPhoneNo, newPhoneNo, null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_MOBILE_NO));
		}

		if (!oldUser.getEmail().equalsIgnoreCase(newUser.getEmail())) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_EMAIL,
					oldUser.getEmail(), newUser.getEmail(), null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_EMAIL));
		}

		auditBaseData(auObj, mBean.getCurrentProfile().getSmSupplier(), roleCode, org, auDetails,
				AuditSupplierManagementConst.AU_GROUP_SECTION_USER_INFO, loginUserId);
		return auObj;
	}

}
