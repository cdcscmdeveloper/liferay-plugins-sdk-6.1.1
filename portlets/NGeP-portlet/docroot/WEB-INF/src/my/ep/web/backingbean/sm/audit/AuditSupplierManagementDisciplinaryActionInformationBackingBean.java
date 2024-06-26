package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;

/**
 * @author Firdaus Omar
 * @since 25.06.2024 #28895_35501
 */

public class AuditSupplierManagementDisciplinaryActionInformationBackingBean extends AuditSupplierManagementBackingBeanTemplate {

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

		List<SmDisciplinaryAction> oldDisciplinaryAction = mBean.getOldSmAppl().getSmSupplier().getSmDisciplinaryActions();
		List<SmDisciplinaryAction> newDisciplinaryAction = mBean.getSmAppl().getSmSupplier().getSmDisciplinaryActions();

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails,
				AuditSupplierManagementConst.AU_GROUP_SECTION_DISCIPLINARY_ACTION);

		return auObj;
	}

}
