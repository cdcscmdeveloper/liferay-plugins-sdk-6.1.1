package my.ep.web.backingbean.sm.audit;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;

import my.ep.biz.service.sm.audit.IAuditSupplierManagementSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * @author Firdaus Omar
 * @since 16.05.2024 #28895_35501
 */

public abstract class AuditSupplierManagementBackingBeanTemplate extends BaseBackingBean<BaseManagedBean> implements Serializable {

	private static final long serialVersionUID = 1L;

	public AuditSupplierManagementBackingBeanTemplate() {
	}

	/**
	 * Saves audit data
	 * 
	 * @param manageBean
	 * @param userId
	 */
	public final void saveAuditData(BaseManagedBean manageBean, Long loginUserId) {
		try {
			String roleCode = getRoleCode();
			PmOrgProfile org = retriveUserOrg(loginUserId);
			AuditSupplierManagementDTO auObj = prepareAuditData(manageBean, roleCode, org, loginUserId);
			if (!auObj.getAuditDetails().isEmpty()) {
				auditSmSo(auObj, loginUserId);
			}
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "insert", null, "AuditSupplierManagementSO", loginUserId, ex);
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
		} else if (getFacesScopes().hasRoleUser(RoleCode.UPS_APPROVER.value())) {
			roleCode = RoleCode.UPS_APPROVER.value();
		}
		return roleCode;
	}

	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}

	private PmOrgProfile retriveUserOrg(Long loginUserId) {
		PmOrgProfile orgProfile = new PmOrgProfile();
		if (loginUserId != null) {
			List<PmUserOrg> orgList = PmCommonUtil.retrieveActiveOrg(loginUserId);
			if (!orgList.isEmpty()) {
				orgProfile = orgList.get(0).getPmOrgProfile();
			}
		}
		return orgProfile;
	}

	/**
	 * Prepares audit data
	 * 
	 * @param managedBean
	 * @param roleCode
	 * @param org
	 * @return AuditSupplierManagementDTO
	 */
	public abstract AuditSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org, Long loginUserId);

	protected AuditSupplierManagementDetailDTO createAuditData(String actionType, Long fieldLabelId, String oldValue, String newValue,
			Long oldValueId, Long newValueId, Long fieldLabelCode) {
		AuditSupplierManagementDetailDTO auDetail = new AuditSupplierManagementDetailDTO();
		auDetail.setActionType(actionType);
		auDetail.setSmFieldLabelId(BigInteger.valueOf(fieldLabelId));
		auDetail.setOldValue(oldValue != null ? oldValue : null);
		auDetail.setNewValue(newValue != null ? newValue : null);
		auDetail.setOldValueId(oldValueId != null ? BigInteger.valueOf(oldValueId) : null);
		auDetail.setNewValueId(newValueId != null ? BigInteger.valueOf(newValueId) : null);
		auDetail.setFieldLabelCode(BigInteger.valueOf(fieldLabelCode));
		return auDetail;
	}

	protected String getLabel(String label) {
		Locale locale = new Locale(AuditSupplierManagementConst.AU_LANGUAGE_CODE_BAHASA, SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		return ResourceBundleUtils.getMessage(label, ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale);
	}

	protected void auditBaseData(AuditSupplierManagementDTO auObj, SmSupplier supplier, String roleCode, PmOrgProfile org,
			List<AuditSupplierManagementDetailDTO> auDetails, Long groupSectionId, Long loginUserId) {
		auObj.setCreatedBy(loginUserId);
		auObj.setChangedBy(loginUserId);
		if (supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_BASIC)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_BASIC);
		} else if (supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)
				|| supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONSULTANT)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_MOF);
		} else if (supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
			auObj.setDocType(AuditSupplierManagementConst.AU_DOC_TYPE_G2G);
		}
		auObj.setRoleCode(roleCode);
		if (org == null) {
			auObj.setOrgProfileId(null);
			auObj.setOrgName(null);
		} else {
			// Both Supplier and Approver organization ID stored in orgProfile
			// but organization name were store in SmSupplier for Supplier and
			// orgProfile for Approver
			auObj.setOrgProfileId(BigInteger.valueOf(org.getOrgProfileId()));
			if (org.getPmOrgValidityList().isEmpty()) {
				auObj.setOrgName(org.getSmSupplier().getCompanyName());
			} else {
				auObj.setOrgName(org.getOrgName());
			}
		}
		auObj.setDocId(BigInteger.valueOf(supplier.getSupplierId()));
		auObj.setDocNo(supplier.getEpNo());
		auObj.setAuditDetails(auDetails);
		auObj.setApplId(BigInteger.valueOf(supplier.getSmAppl().getApplId()));
		auObj.setApplNo(supplier.getSmAppl().getApplNo());
		auObj.setGroupSectionId(BigInteger.valueOf(groupSectionId));
	}

	private void auditSmSo(AuditSupplierManagementDTO auObj, Long loginUserId) {
		try {
			IAuditSupplierManagementSO auSmSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					IAuditSupplierManagementSO.class);
			auSmSo.insertAudit(auObj);
		} catch (Exception ex) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_MANAGEMENT, "insert", auObj.getDocNo(), "AuditSupplierManagementSO",
					loginUserId, ex);
		}
	}
}
