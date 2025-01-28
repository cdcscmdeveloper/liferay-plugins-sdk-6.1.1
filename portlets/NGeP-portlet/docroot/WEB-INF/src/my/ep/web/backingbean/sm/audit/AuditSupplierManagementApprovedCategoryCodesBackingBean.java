package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;

/**
 * @author Firdaus Omar
 * @since 27.06.2024 #28895_35501
 */

public class AuditSupplierManagementApprovedCategoryCodesBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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

		ListIterator<SmSupplierCategory> oldApprovedCategories = mBean.getOldApprovedCategoryList().listIterator();
		ListIterator<SmSupplierCategory> newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();

		// Expected Max Size in loop (30) based on NGEP_PDS database
		while (newApprovedCategories.hasNext()) {
			SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
			if (!newApprovedCategory.getRecordStatusFlg().equals(1)) {
				newApprovedCategories.remove();
			}
		}
		newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();

		// Expected Max Size in loop (30) based on NGEP_PDS database
		while (oldApprovedCategories.hasNext()) {
			SmSupplierCategory oldApprovedCategory = oldApprovedCategories.next();
			// Expected Max Size in loop (30) based on NGEP_PDS database
			while (newApprovedCategories.hasNext()) {
				SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
				if (oldApprovedCategory.getSupplierCategoryId().equals(newApprovedCategory.getSupplierCategoryId())) {
					checkOtherDesc(auDetails, oldApprovedCategory, newApprovedCategory);
					oldApprovedCategories.remove();
					newApprovedCategories.remove();
					break;
				}
			}
			newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();
		}
		oldApprovedCategories = mBean.getOldApprovedCategoryList().listIterator();

		auditAddedData(newApprovedCategories, auDetails);

		auditDeletedData(oldApprovedCategories, auDetails);

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_APPROVE_CATEGORY_CODES,
				loginUserId);

		return auObj;
	}

	private void checkOtherDesc(List<AuditSupplierManagementDetailDTO> auDetails, SmSupplierCategory oldApprovedCategory,
			SmSupplierCategory newApprovedCategory) {
		if (oldApprovedCategory.getOtherDesc() == null
				&& (newApprovedCategory.getOtherDesc() != null && !"".equalsIgnoreCase(newApprovedCategory.getOtherDesc()))) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_OTHER_DESCRIPTION, null, newApprovedCategory.getOtherDesc(), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OTHER_DESCRIPTION));
		} else if (oldApprovedCategory.getOtherDesc() != null) {
			if (newApprovedCategory.getOtherDesc() != null && newApprovedCategory.getOtherDesc().isEmpty()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_OTHER_DESCRIPTION, oldApprovedCategory.getOtherDesc(), null, null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OTHER_DESCRIPTION));
			} else if (newApprovedCategory.getOtherDesc() != null
					&& !oldApprovedCategory.getOtherDesc().equalsIgnoreCase(newApprovedCategory.getOtherDesc())) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_OTHER_DESCRIPTION, oldApprovedCategory.getOtherDesc(),
						newApprovedCategory.getOtherDesc(), null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OTHER_DESCRIPTION));
			}
		}
	}

	private String codeToString(SmSupplierCategory catCode) {
		return catCode.getPmCategoryL1().getCategoryL1Code() + " - " + catCode.getCategoryL1Name() + "\r\n"
				+ catCode.getPmCategoryL2().getCategoryL2Code() + " - " + catCode.getCategoryL2Name() + "\r\n"
				+ catCode.getPmCategoryL3().getCategoryL3Code() + " - " + catCode.getCategoryL3Name();
	}

	private void auditAddedData(ListIterator<SmSupplierCategory> newApprovedCategories, List<AuditSupplierManagementDetailDTO> auDetails) {
		// Expected Max Size in loop (30) based on NGEP_PDS database
		while (newApprovedCategories.hasNext()) {
			SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_CATEGORY,
					null, codeToString(newApprovedCategory), null, newApprovedCategory.getSupplierCategoryId(),
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_CATEGORY));
			if (newApprovedCategory.getOtherDesc() != null && !newApprovedCategory.getOtherDesc().isEmpty()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_OTHER_DESCRIPTION, null, newApprovedCategory.getOtherDesc(), null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OTHER_DESCRIPTION));
			}
		}
	}

	private void auditDeletedData(ListIterator<SmSupplierCategory> oldApprovedCategories, List<AuditSupplierManagementDetailDTO> auDetails) {
		// Expected Max Size in loop (30) based on NGEP_PDS database
		while (oldApprovedCategories.hasNext()) {
			SmSupplierCategory oldApprovedCategory = oldApprovedCategories.next();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_CATEGORY, codeToString(oldApprovedCategory), null,
					oldApprovedCategory.getSupplierCategoryId(), null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_CATEGORY));
			if (oldApprovedCategory.getOtherDesc() != null && !oldApprovedCategory.getOtherDesc().isEmpty()) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_OTHER_DESCRIPTION, oldApprovedCategory.getOtherDesc(), null, null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_OTHER_DESCRIPTION));
			}
		}
	}
}
