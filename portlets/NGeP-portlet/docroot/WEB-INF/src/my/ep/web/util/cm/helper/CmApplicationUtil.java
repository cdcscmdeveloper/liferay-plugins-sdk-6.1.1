package my.ep.web.util.cm.helper;

import my.ep.constant.cm.CmConst;
import my.ep.dto.cm.catalogue.CmTrackingDairyVO;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.pm.RoleCode;

import my.ep.web.util.FacesScopes;

public class CmApplicationUtil {
	
	public static CmTrackingDairyVO assignTrackingDairyValues(CmSupplierItem cmSupplierItem, String actionDesc,
			PmStatus status) {
		CmTrackingDairyVO trckVO = new CmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());       
        trckVO.setDocId(cmSupplierItem.getSupplierItemId());
		trckVO.setDocType(CmConst.CM_DocType);
		trckVO.setDocNo(cmSupplierItem.getCmItem().getExtensionCode());		
		trckVO.setGroupId(null);		
		trckVO.setStatus(status);				
		trckVO.setGroupDocType(null);
		return trckVO;
	}
	
	public static CmTrackingDairyVO assignTrackingDiaryValuesForUNSPSCExtension(String actionDesc,
			PmStatus status, String docType) {
		CmTrackingDairyVO trckVO = new CmTrackingDairyVO();
		PmUser user = new FacesScopes().getUserSessionBean();
		trckVO.setActionDesc(actionDesc);
		trckVO.setActionedBy(user != null ? user.getUserId() : null);
		trckVO.setActionedByRole(getUserRoleValue());				
		trckVO.setDocType(docType);	
		trckVO.setGroupId(null);
		trckVO.setStatus(status);
		trckVO.setGroupDocType(CmConst.CD_DocType);
		return trckVO;
	}
	public static String getUserRoleValue() {		
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(RoleCode.CM_USER.value())) {
			return RoleCode.CM_USER.value();
		} else if (sc.hasRoleUser(RoleCode.CM_ADMIN.value())) {
			return RoleCode.CM_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.CD_OFFICER.value())) {
			return RoleCode.CD_OFFICER.value();
		} else if (sc.hasRoleUser(RoleCode.CD_COMMITTEE.value())) {
			return RoleCode.CD_COMMITTEE.value();
		} else if (sc.hasRoleUser(RoleCode.EP_COMMITTEE.value())) {
			return RoleCode.EP_COMMITTEE.value();
		} else if (sc.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
			return RoleCode.BASIC_SUPPLIER_USER.value();
		} else if (sc.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		} else if (sc.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
				return RoleCode.MOF_SUPPLIER_USER.value();
			}
	return null;	
	}
}
