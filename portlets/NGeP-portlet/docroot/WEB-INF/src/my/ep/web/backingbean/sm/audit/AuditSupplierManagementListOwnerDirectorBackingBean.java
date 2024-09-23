package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;

/**
 * @author Firdaus Omar
 * @since 27.06.2024 #28895_35501
 */

public class AuditSupplierManagementListOwnerDirectorBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * @param managedBean
	 * @param org
	 * @param user
	 * @param roleCode
	 *            prepare audit trail data
	 */
	@Override
	public AuditSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org) {

		AuditSupplierManagementDTO auObj = new AuditSupplierManagementDTO();
		ViewSuppProfileManagedBean mBean = (ViewSuppProfileManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();

		List<SmPersonnel> selectedOwnerAndDirectorList = mBean.getSelectedOwnerAndDirectorList();

		for (SmPersonnel owner : selectedOwnerAndDirectorList) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_NAME, null, owner.getName(), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_NAME));
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_IDENTIFICATION_NO, null,
					owner.getIdentificationNo(), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_IDENTIFICATION_NO));
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_ROLE_IN_COMPANY, null, getRoleCompany(owner), null,
					null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_ROLE_IN_COMPANY));
		}

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_DISCIPLINARY_ACTION);

		return auObj;
	}

	private String getRoleCompany(SmPersonnel owner) {
		String role = "";
		if (owner.isEquityOwner()) {
			role += getLabel("lbl.sm.person.equiOwner");
		}
		if (owner.isDirector()) {
			role += role.isEmpty() ? getLabel("lbl.sm.person.dir") : ", " + getLabel("lbl.sm.person.dir");
		}
		if (owner.isContractSigner()) {
			role += role.isEmpty() ? getLabel("lbl.sm.contract.signer") : ", " + getLabel("lbl.sm.contract.signer");
		}
		return role;
	}
}