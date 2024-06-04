package my.ep.web.backingbean.sm.audit;

import java.io.Serializable;
import java.util.List;

import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuSMDetailDTO;
import my.ep.dto.sm.audit.AuSupplierManagementDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.SLAUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.log4j.Logger;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public abstract class AuditSupplierManagementBackingBeanTemplate implements Serializable {

	private static final long serialVersionUID = 1L;

	private LogKey logKey = new LogKey();

	public AuditSupplierManagementBackingBeanTemplate() {
	}

	/**
	 * @param managebean
	 * @param userId
	 * 
	 *            Save audit data
	 */
	public final void saveAuditData(BaseManagedBean managebean, Long userId) {
		List<AuSMDetailDTO> auDetails = prepareAuditData(managebean);
		if (auDetails.size() > 0) {
			String roleCode = getRoleCode();
			PmOrgProfile org = retriveUserOrg(userId);

			AuSupplierManagementDTO auObj = new AuSupplierManagementDTO();
			RegistrationManagedBean mBean = (RegistrationManagedBean) managebean;

			if (mBean.getSmSupp().getSmAppl().getSupplierType()
					.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)) {
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

			auditSMSO(auObj);
		}
	}

	private String getRoleCode() {

		String roleCode = null;

		if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			roleCode = RoleCode.BASIC_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.G2G_SUPPLIER_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())) {
			roleCode = RoleCode.G2G_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
			roleCode = RoleCode.MOF_SUPPLIER_USER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			roleCode = RoleCode.MOF_SUPPLIER_ADMIN.value();
		}

		return roleCode;
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	private PmOrgProfile retriveUserOrg(Long userOrgId) {
		new PmOrgProfile();
		List<PmUserOrg> orgList = PmCommonUtil.retrieveActiveOrg(userOrgId);
		PmOrgProfile org = orgList.get(0).getPmOrgProfile();
		return org;
	}

	public abstract List<AuSMDetailDTO> prepareAuditData(BaseManagedBean managebean);

	private void auditSMSO(AuSupplierManagementDTO auObj) {
		try {
			IAuditSupplierManagementSO auSMSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IAuditSupplierManagementSO.class);
			auSMSO.insertAudit(auObj);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "insert", auObj.getDocNo(),
					"AuditSupplierManagementSO", logKey.getUserId(), ex);
		}
	}
}
