package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public class AuditSMBasicAccountMaintananceBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	@Override
	public AuSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, PmOrgProfile org, String roleCode) {

		RegistrationManagedBean mBean = (RegistrationManagedBean) managedBean;
		List<AuSMDetailDTO> auDetails = new ArrayList<>();
		AuSupplierManagementDTO auObj = new AuSupplierManagementDTO();

		for (SmSupplierBank oldBank : mBean.getOldBanks()) {
			Boolean deleted = true;
			for (SmSupplierBank newBank : mBean.getSmSupp().getSmAppl().getSmSupplierBanks()) {
				if (oldBank.getSupplierBankId().equals(newBank.getSupplierBankId())) {
					deleted = false;
					if (!oldBank.getPmFinancialOrg().getFinancialOrgId()
							.equals(newBank.getPmFinancialOrg().getFinancialOrgId())) {
						AuSMDetailDTO auDetail = new AuSMDetailDTO();
						auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
						auDetail.setOldValueId(oldBank.getPmFinancialOrg().getFinancialOrgId());
						auDetail.setNewValueId(newBank.getPmFinancialOrg().getFinancialOrgId());
						auDetails.add(auDetail);
					}
					if (!oldBank.getAccountNo().equals(newBank.getAccountNo())) {
						AuSMDetailDTO auDetail = new AuSMDetailDTO();
						auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
						auDetail.setOldValue(oldBank.getAccountNo());
						auDetail.setNewValue(newBank.getAccountNo());
						auDetails.add(auDetail);
					}
				}
			}
			if (deleted) {
				AuSMDetailDTO auDetail = new AuSMDetailDTO();
				auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE);
				auDetail.setOldValue(oldBank.getAccountNo());
				auDetails.add(auDetail);
			}
		}
		
		for (SmSupplierBank newBank : mBean.getSmSupp().getSmAppl().getSmSupplierBanks()) {
			if(newBank.isBrandNew()){
				AuSMDetailDTO auDetail = new AuSMDetailDTO();
				auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD);
				auDetail.setNewValue(newBank.getAccountNo());
				auDetails.add(auDetail);
			}
		}
		
		auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_BASIC);
		auObj.setRoleCode(roleCode);
		auObj.setAuditDetails(auDetails);
		

		return auObj;
	}

}
