package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public class AuditSMBasicAccountMaintananceBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	@Override
	public AuSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, PmOrgProfile org, PmUser user, String roleCode) {

		RegistrationManagedBean mBean = (RegistrationManagedBean) managedBean;
		List<AuSMDetailDTO> auDetails = new ArrayList<>();
		AuSupplierManagementDTO auObj = new AuSupplierManagementDTO();
		
		Iterator<SmSupplierBank> oldBanks = mBean.getOldBanks().iterator();
		Iterator<SmSupplierBank> newBanks = mBean.getSmSupp().getSmAppl().getSmSupplierBanks().iterator();
		
		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			while (newBanks.hasNext()) {
				SmSupplierBank newBank = newBanks.next();
				if (oldBank.getSupplierBankId().equals(newBank.getSupplierBankId())) {
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
					oldBanks.remove();
					newBanks.remove();
				}
			}
		}
		while (oldBanks.hasNext()) {
			SmSupplierBank oldBank = oldBanks.next();
			AuSMDetailDTO auDetail = new AuSMDetailDTO();
			auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE);
			auDetail.setOldValue(oldBank.getAccountNo());
			auDetails.add(auDetail);
		}
		while (newBanks.hasNext()) {
			SmSupplierBank newBank = newBanks.next();
			if(newBank.isBrandNew()){
				AuSMDetailDTO auDetail = new AuSMDetailDTO();
				auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD);
				auDetail.setNewValue(newBank.getAccountNo());
				auDetails.add(auDetail);
			}
		}

//		for (SmSupplierBank oldBank : mBean.getOldBanks()) {
//			boolean deleted = true;
//			for (SmSupplierBank newBank : mBean.getSmSupp().getSmAppl().getSmSupplierBanks()) {
//				if (oldBank.getSupplierBankId().equals(newBank.getSupplierBankId())) {
//					deleted = false;
//					if (!oldBank.getPmFinancialOrg().getFinancialOrgId()
//							.equals(newBank.getPmFinancialOrg().getFinancialOrgId())) {
//						AuSMDetailDTO auDetail = new AuSMDetailDTO();
//						auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
//						auDetail.setOldValueId(oldBank.getPmFinancialOrg().getFinancialOrgId());
//						auDetail.setNewValueId(newBank.getPmFinancialOrg().getFinancialOrgId());
//						auDetails.add(auDetail);
//					}
//					if (!oldBank.getAccountNo().equals(newBank.getAccountNo())) {
//						AuSMDetailDTO auDetail = new AuSMDetailDTO();
//						auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY);
//						auDetail.setOldValue(oldBank.getAccountNo());
//						auDetail.setNewValue(newBank.getAccountNo());
//						auDetails.add(auDetail);
//					}
//				}
//			}
//			if (deleted) {
//				AuSMDetailDTO auDetail = new AuSMDetailDTO();
//				auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE);
//				auDetail.setOldValue(oldBank.getAccountNo());
//				auDetails.add(auDetail);
//			}
//		}
//		
//		for (SmSupplierBank newBank : mBean.getSmSupp().getSmAppl().getSmSupplierBanks()) {
//			if(newBank.isBrandNew()){
//				AuSMDetailDTO auDetail = new AuSMDetailDTO();
//				auDetail.setActionType(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD);
//				auDetail.setNewValue(newBank.getAccountNo());
//				auDetails.add(auDetail);
//			}
//		}
		
		auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_BASIC);
		auObj.setRoleCode(roleCode);
		auObj.setOrgProfileId(org.getOrgProfileId());
		auObj.setOrgName(org.getOrgName());
		auObj.setDocId(null);
		auObj.setDocNo(mBean.getSmSupp().getEpNo());
		auObj.setAuditDetails(auDetails);
		
		return auObj;
	}

}
