package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
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
					auditModifiedDataBankName(auDetails, oldBank, newBank);
					auditModifiedDataBankNo(auDetails, oldBank, newBank);
					oldBanks.remove();
					newBanks.remove();
				}
			}
			newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().listIterator();
		}
		oldBanks = mBean.getOldBanks().listIterator();

		auditDeletedData(auDetails, oldBanks);

		auditAddedData(auDetails, newBanks);

		auditBaseData(auObj, mBean, auDetails, roleCode, org);

		return auObj;
	}

	private void auditModifiedDataBankNo(List<AuSMDetailDTO> auDetails, SmSupplierBank oldBank, SmSupplierBank newBank) {
		if (!oldBank.getPmFinancialOrg().getFinancialOrgId().equals(newBank.getPmFinancialOrg().getFinancialOrgId())) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME, null, null, oldBank.getPmFinancialOrg()
							.getFinancialOrgId(), newBank.getPmFinancialOrg().getFinancialOrgId(),
					AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));
		}
	}

	private void auditModifiedDataBankName(List<AuSMDetailDTO> auDetails, SmSupplierBank oldBank, SmSupplierBank newBank) {
		if (!oldBank.getAccountNo().equals(newBank.getAccountNo())) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO, oldBank.getAccountNo(),
					newBank.getAccountNo(), null, null, AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));
		}
	}

	private void auditDeletedData(List<AuSMDetailDTO> auDetails, ListIterator<SmSupplierBank> oldBanks) {
		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME, null, null, oldBank.getPmFinancialOrg()
							.getFinancialOrgId(), null, AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO, oldBank.getAccountNo(), null, null, null,
					AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));
		}
	}

	private void auditAddedData(List<AuSMDetailDTO> auDetails, ListIterator<SmSupplierBank> newBanks) {
		while (newBanks.hasNext()) {
			SmSupplierBank newBank = newBanks.next();
			if (newBank.isBrandNew()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_NAME, null, null, null, newBank
								.getPmFinancialOrg().getFinancialOrgId(),
						AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_BANK_ACC_NO, null, newBank.getAccountNo(), null,
						null, AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO));

			}
		}
	}

	private void auditBaseData(AuSupplierManagementDTO auObj, RegistrationManagedBean mBean,
			List<AuSMDetailDTO> auDetails, String roleCode, PmOrgProfile org) {
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
	}
}
