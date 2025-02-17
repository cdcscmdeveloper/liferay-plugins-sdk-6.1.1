package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.SmDeleteCategoryCodeManagedBean;

/**
 * @author Firdaus Omar
 * @since 13.06.2024 #28895_35501
 */

public class AuditSupplierManagementCancelRegisteredCategoryBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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
		SmDeleteCategoryCodeManagedBean mBean = (SmDeleteCategoryCodeManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();
		List<SmSupplierCategory> delCatCodeList = mBean.getSelectedCategories();
		// Expected Max Size in loop (30) based on NGEP_PDS database
		for (SmSupplierCategory catCode : delCatCodeList) {
			String category = catCode.getPmCategoryL1().getCategoryL1Code() + " - " + catCode.getCategoryL1Name() + "\r\n"
					+ catCode.getPmCategoryL2().getCategoryL2Code() + " - " + catCode.getCategoryL2Name() + "\r\n"
					+ catCode.getPmCategoryL3().getCategoryL3Code() + " - " + catCode.getCategoryL3Name();
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_DELETE,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_CATEGORY_CODE, category, null, catCode.getSupplierCategoryId(), null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_CATEGORY_CODE));
		}
		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_CATEGORY_CODE_INFO,
				loginUserId);
		return auObj;
	}
}
