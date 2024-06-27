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

		ListIterator<SmSupplierCategory> oldApprovedCategories = mBean.getOldApprovedCategoryList().listIterator();
		ListIterator<SmSupplierCategory> newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();
		
		while (newApprovedCategories.hasNext()) {
			SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
			if (!newApprovedCategory.getRecordStatusFlg().equals(1)) {
				newApprovedCategories.remove();
			}
		}
		newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();

		while (oldApprovedCategories.hasNext()) {
			SmSupplierCategory oldApprovedCategory = oldApprovedCategories.next();
			while (newApprovedCategories.hasNext()) {
				SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
				if (oldApprovedCategory.getSupplierCategoryId().equals(newApprovedCategory.getSupplierCategoryId())) {
					oldApprovedCategories.remove();
					newApprovedCategories.remove();
				}
			}
			newApprovedCategories = mBean.getSmAppl().getSmSupplierCategories().listIterator();
		}
		oldApprovedCategories = mBean.getOldApprovedCategoryList().listIterator();

		while (newApprovedCategories.hasNext()) {
			SmSupplierCategory newApprovedCategory = newApprovedCategories.next();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_CATEGORY, null, codeToString(newApprovedCategory), null,
					newApprovedCategory.getSupplierCategoryId(), AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_CATEGORY));
		}
		
		while (oldApprovedCategories.hasNext()) {
			SmSupplierCategory oldApprovedCategory = oldApprovedCategories.next();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_CATEGORY, codeToString(oldApprovedCategory), null,
					oldApprovedCategory.getSupplierCategoryId(), null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_CATEGORY));
		}

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails,
				AuditSupplierManagementConst.AU_GROUP_SECTION_CATEGORY_CODE_INFO);

		return auObj;
	}

	private String codeToString(SmSupplierCategory catCode) {
		String Category = catCode.getPmCategoryL1().getCategoryL1Code() + " - " + catCode.getCategoryL1Name() + "<br/>"
				+ catCode.getPmCategoryL2().getCategoryL2Code() + " - " + catCode.getCategoryL2Name() + "<br/>"
				+ catCode.getPmCategoryL3().getCategoryL3Code() + " - " + catCode.getCategoryL3Name();
		return Category;
	}
}
