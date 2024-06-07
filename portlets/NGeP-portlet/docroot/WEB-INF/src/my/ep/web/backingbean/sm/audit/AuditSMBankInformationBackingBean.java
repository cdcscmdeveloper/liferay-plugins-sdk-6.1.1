package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public class AuditSMBankInformationBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * @param managedBean
	 * @param org
	 * @param user
	 * @param roleCode
	 *            prepare audit trail data
	 */
	@Override
	public AuSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org) {

		AuSupplierManagementDTO auObj = new AuSupplierManagementDTO();

		RegistrationManagedBean mBean = (RegistrationManagedBean) managedBean;
		List<AuSMDetailDTO> auDetails = new ArrayList<>();

		ListIterator<SmSupplierBank> oldBanks = mBean.getOldBanks().listIterator();
		ListIterator<SmSupplierBank> newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().listIterator();

		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			while (newBanks.hasNext()) {
				SmSupplierBank newBank = newBanks.next();
				if (oldBank.getSupplierBankId().equals(newBank.getSupplierBankId())) {
					if (!oldBank.getPmFinancialOrg().getFinancialOrgId()
							.equals(newBank.getPmFinancialOrg().getFinancialOrgId())) {
						AuSMDetailDTO auDetailBankName = new AuSMDetailDTO();
						auDetailBankName.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
						auDetailBankName.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME);
						auDetailBankName.setOldValueId(oldBank.getPmFinancialOrg().getFinancialOrgId());
						auDetailBankName.setNewValueId(newBank.getPmFinancialOrg().getFinancialOrgId());
						auDetailBankName.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
						auDetails.add(auDetailBankName);
					}
					if (!oldBank.getAccountNo().equals(newBank.getAccountNo())) {
						AuSMDetailDTO auDetailBankNo = new AuSMDetailDTO();
						auDetailBankNo.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
						auDetailBankNo.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO);
						auDetailBankNo.setOldValue(oldBank.getAccountNo());
						auDetailBankNo.setNewValue(newBank.getAccountNo());
						auDetailBankNo.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
						auDetails.add(auDetailBankNo);
					}
					oldBanks.remove();
					newBanks.remove();
				}
			}
			newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().listIterator();
		}
		oldBanks = mBean.getOldBanks().listIterator();

		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			AuSMDetailDTO auDetailBankName = new AuSMDetailDTO();
			auDetailBankName.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE);
			auDetailBankName.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME);
			auDetailBankName.setOldValueId(oldBank.getPmFinancialOrg().getFinancialOrgId());
			auDetailBankName.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
			auDetails.add(auDetailBankName);

			AuSMDetailDTO auDetailBankNo = new AuSMDetailDTO();
			auDetailBankNo.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE);
			auDetailBankNo.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO);
			auDetailBankNo.setOldValue(oldBank.getAccountNo());
			auDetailBankNo.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
			auDetails.add(auDetailBankNo);
		}

		while (newBanks.hasNext()) {
			SmSupplierBank newBank = newBanks.next();
			if (newBank.isBrandNew()) {
				AuSMDetailDTO auDetailBankName = new AuSMDetailDTO();
				auDetailBankName.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD);
				auDetailBankName.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME);
				auDetailBankName.setNewValueId(newBank.getPmFinancialOrg().getFinancialOrgId());
				auDetailBankName.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
				auDetails.add(auDetailBankName);

				AuSMDetailDTO auDetailBankNo = new AuSMDetailDTO();
				auDetailBankNo.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD);
				auDetailBankNo.setSmFieldLabelId(AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO);
				auDetailBankNo.setNewValue(newBank.getAccountNo());
				auDetailBankNo.setGroupSectionId(AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO);
				auDetails.add(auDetailBankNo);
			}
		}

		if (mBean.getSmSupp().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_BASIC);
		} else if (mBean.getSmSupp().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| mBean.getSmSupp().getSmAppl().getSupplierType()
						.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_MOF);
		} else if (mBean.getSmSupp().getSmAppl().getSupplierType()
				.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_G2G);
		}
		auObj.setRoleCode(roleCode);
		auObj.setOrgProfileId(org.getOrgProfileId());
		auObj.setOrgName(org.getOrgName());
		auObj.setDocId(mBean.getSmSupp().getSupplierId());
		auObj.setDocNo(mBean.getSmSupp().getEpNo());
		auObj.setAuditDetails(auDetails);
		auObj.setApplId(mBean.getSmSupp().getSmAppl().getApplId());
		auObj.setApplNo(mBean.getSmSupp().getSmAppl().getApplNo());

		return auObj;
	}
}
