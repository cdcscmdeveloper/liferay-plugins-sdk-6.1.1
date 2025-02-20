package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmContact;

/**
 * @author normuliana #28895_35509
 * @param <OrganisationManagedBean>
 */
public class StoreProcedureRegistryBakingBean<OrganisationManagedBean> {
	static PmOrgProfile orgProfile;
	private static final Map<String, AuditParams> auditParamsMap = new HashMap<>();
	static {
		auditParamsMap.put("ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA", new AuditParams(
				"AuditConst.PM_AUD_OPM_AUN_PMPARAMETERLVLID", "ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA", setStatus(),
				setUpdateGeneralDetails(), setAddUpdateGeneralDetails()));

		auditParamsMap.put("ParameterConst.ORG_TYPE_SUPPORT", new AuditParams("AuditConst.PM_AUD_OPM_SPT_PMPARAMETERLVLID",
				"ParameterConst.ORG_TYPE_SUPPORT", setStatus(), setUpdateGeneralDetails(), setAddUpdateGeneralDetails()));

		auditParamsMap.put("ParameterConst.ORG_TYPE_SYS", new AuditParams("AuditConst.PM_AUD_OPM_SPT_PMPARAMETERLVLID",
				"ParameterConst.ORG_TYPE_SYS", setStatus(), setUpdateGeneralDetails(), setAddUpdateGeneralDetails()));
	}
	static String orgType;

	public static AuditParams getAuditParams(String orgType) {
		AuditParams params = auditParamsMap.get(orgType);
		return params != null ? params : new AuditParams(null, null, null, null, null);

	}

	static AuditParams params = getAuditParams("managedBean.getOrgType()");
	static String auditPmParameterLvlId = params.auditPmParameterLvlId;
	String levelCode3 = params.levelCode3;

	private static String setStatus() {
		if (orgProfile.isBrandNew() && !orgType.equalsIgnoreCase("ParameterConst.ORG_TYPE_SYS")) {
			detailObj("AuditConst.PM_AUD_ORGPROFILE_STATUS_FIELDLABELCODE", null,
					orgProfile.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		} else {
			// checking with old if
			detailObj("AuditConst.PM_AUD_ORGPROFILE_STATUS_FIELDLABELCODE", "OLD",
					orgProfile.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}
		return null;
	}

	private static String setUpdateGeneralDetails() {
		PmContact pmContact = orgProfile.getPmAddressTypes().get(0).getPmContacts().get(0);
		if (!orgProfile.getOrgName().equals("OLD")) {
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", "OLD", orgProfile.getOrgName());
		} // change to office address
		if (pmContact.getContactName().equals("OLD")) {
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", "OLD", pmContact.getContactName());
		}
		// setContact(pmContact);
		return null;
	}

	private static String setAddUpdateGeneralDetails() {
		// PmOrgProfile orgProfile = managedBean.getOrgProfile();
		PmContact pmContact = orgProfile.getPmAddressTypes().get(0).getPmContacts().get(0);
		if (orgProfile.isBrandNew()) {
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", null, orgProfile.getOrgName());
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", null, pmContact.getContactName());
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", null, pmContact.getEmail());
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", null, pmContact.getMobileNo());
			detailObj("AuditConst.PM_AUD_ORGPROFILE_ORGNAME_FIELDLABELCODE", null, pmContact.getPhoneNo());
		} else {
			setUpdateGeneralDetails();
		}
		return null;
	}

	// ///////////////////////////////////////////////////////////////////////////////////////
	static ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

	private static ArrayList<AuditPMSPDTO> detailObj(String fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		// dtlObj.setRoleCode(roleCode);
		// dtlObj.setDocType(docType);
		// dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(new BigInteger(auditPmParameterLvlId));
		// dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		// dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OPM);
		// dtlObj.setLvlCode3(levelCode3);
		// dtlObj.setDocId(docId);
		// dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		// dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
}

class AuditParams {
	public final String auditPmParameterLvlId;
	public final String levelCode3;
	public final String status;
	public final String updateGeneralDetails;
	public final String addUpdateGeneralDetails;

	public AuditParams(String auditPmParameterLvlId, String levelCode3, String status, String updateGeneralDetails,
			String addUpdateGeneralDetails) {
		this.auditPmParameterLvlId = auditPmParameterLvlId;
		this.levelCode3 = levelCode3;
		this.status = status;
		this.updateGeneralDetails = updateGeneralDetails;
		this.addUpdateGeneralDetails = addUpdateGeneralDetails;
	}
}