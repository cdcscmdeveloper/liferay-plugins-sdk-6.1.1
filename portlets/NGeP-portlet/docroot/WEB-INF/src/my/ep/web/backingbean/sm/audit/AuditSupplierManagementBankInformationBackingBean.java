package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public class AuditSupplierManagementBankInformationBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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
		RegistrationManagedBean mBean = (RegistrationManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		ListIterator<SmSupplierBank> oldBanks = mBean.getOldBanks().listIterator();
		ListIterator<SmSupplierBank> newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().listIterator();
		List<PmFinancialOrg> bankList = mBean.getFinancialOrgLs();
		// Expected Max Size in loop (93) based on NGEP_PDS database
		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			// Expected Max Size in loop (93) based on NGEP_PDS database
			while (newBanks.hasNext()) {
				SmSupplierBank newBank = newBanks.next();
				if (oldBank.getSupplierBankId().equals(newBank.getSupplierBankId())) {
					auditModifiedDataBankName(bankList, auDetails, oldBank, newBank);
					auditModifiedDataBankNo(auDetails, oldBank, newBank);
					oldBanks.remove();
					newBanks.remove();
				}
			}
			newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().listIterator();
		}
		oldBanks = mBean.getOldBanks().listIterator();
		auditDeletedData(auDetails, oldBanks);
		auditAddedData(bankList, auDetails, newBanks);
		auditBaseData(auObj, mBean.getSmSupp(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_BANK_INFO, loginUserId);
		return auObj;
	}

	private void auditModifiedDataBankName(List<PmFinancialOrg> bankList, List<AuditSupplierManagementDetailDTO> auDetails, SmSupplierBank oldBank,
			SmSupplierBank newBank) {
		if (!oldBank.getPmFinancialOrg().getFinancialOrgId().equals(newBank.getPmFinancialOrg().getFinancialOrgId())) {
			String oldBankName = oldBank.getPmFinancialOrg().getFinOrgName();
			String newBankName = getFinOrgName(bankList, newBank.getPmFinancialOrg().getFinancialOrgId());
			Long oldBankId = oldBank.getPmFinancialOrg().getFinancialOrgId();
			Long newBankId = newBank.getPmFinancialOrg().getFinancialOrgId();

			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_NAME, oldBankName, newBankName, oldBankId, newBankId,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_NAME));
		}
	}

	private void auditModifiedDataBankNo(List<AuditSupplierManagementDetailDTO> auDetails, SmSupplierBank oldBank, SmSupplierBank newBank) {
		if (!oldBank.getAccountNo().equals(newBank.getAccountNo())) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_ACC_NO, oldBank.getAccountNo(), newBank.getAccountNo(), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_ACC_NO));
		}
	}

	private void auditDeletedData(List<AuditSupplierManagementDetailDTO> auDetails, ListIterator<SmSupplierBank> oldBanks) {
		// Expected Max Size in loop (93) based on NGEP_PDS database
		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();

			String oldBankName = oldBank.getPmFinancialOrg().getFinOrgName();
			Long oldBankId = oldBank.getPmFinancialOrg().getFinancialOrgId();

			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_NAME, oldBankName, null, oldBankId, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_NAME));

			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_ACC_NO, oldBank.getAccountNo(), null, null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_ACC_NO));
		}
	}

	private void auditAddedData(List<PmFinancialOrg> bankList, List<AuditSupplierManagementDetailDTO> auDetails, ListIterator<SmSupplierBank> newBanks) {
		// Expected Max Size in loop (93) based on NGEP_PDS database
		while (newBanks.hasNext()) {
			SmSupplierBank newBank = newBanks.next();
			if (newBank.isBrandNew()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_NAME, null,
						getFinOrgName(bankList, newBank.getPmFinancialOrg().getFinancialOrgId()), null, newBank.getPmFinancialOrg()
								.getFinancialOrgId(), AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_NAME));
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_BANK_ACC_NO, null, newBank.getAccountNo(), null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_BANK_ACC_NO));

			}
		}
	}

	private String getFinOrgName(List<PmFinancialOrg> bankList, Long finOrgId) {
		String finName = "";
		if (!SmCommonUtil.isCollectionEmpty(bankList) && finOrgId != null) {
			// Expected Max Size in loop (85) based on NGEP_PDS database
			for (PmFinancialOrg finOrg : bankList) {
				if (finOrg.getFinancialOrgId().compareTo(finOrgId) == 0) {
					finName = finOrg.getFinOrgName();
					break;
				}
			}
		}
		return finName;
	}

}
