package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.educationfield.EducationManagedBean;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;

/**
 * AuditPMEducationBackingBean
 * 
 * @author haiza 28895_35509
 * 
 */
public class AuditPMConfigUserBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlObjPNP = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjGrade = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjTelNo = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjMobile = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjStatusUser = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRole = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjApprLimit = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjStatusRole = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRcvAddrName = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRcvAddrStatus = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleUser();
	}

	/**
	 * @param args
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ProfileManagementManagedBean managedBeans = (ProfileManagementManagedBean) manageBean;

		dtlObjPNP.setRoleCode(roleCode);
		dtlObjPNP.setDocType(AuditConst.PM_AUD_OUSR);
		dtlObjPNP.setPmParameterLvlId(AuditConst.PM_AUD_EDUCATION_PMPARAMETERLVLID);//based on org type
		dtlObjPNP.setDocNo("");
		dtlObjPNP.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObjPNP.setLvlCode2(AuditConst.PM_LVL_CODE_2_OUSR);
		dtlObjPNP.setLvlCode3(AuditConst.PM_LVL_CODE_3_EDU);//needs to change based on user
		dtlObjPNP.setDocId(BigInteger.valueOf(managedBeans.getPmUserOriginal().getUserId()));
		dtlObjPNP.setFieldLabelCode(AuditConst.PM_AUD_AS_NAME_FIELDLABELCODE);

		if (managedBeans.getOldAuditPmUser() != null
				&& managedBeans.getOldAuditPmUser().getUserId() != null) {
			if (managedBeans.getOldAuditPmUser().getUserName() != null
					&& !managedBeans.getOldAuditPmUser().getUserName()
							.equalsIgnoreCase(managedBeans.getSelectedUser().getUserName())) {
				dtlObjPNP.setOldValue(managedBeans.getOldAuditPmUser().getUserName());
				dtlObjPNP.setNewValue(managedBeans.getSelectedUser().getUserName());
				dtlObjPNP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjPNP);
				auditPMReqDTO.setObjList(objList);
			}

		} else {

			dtlObjPNP.setNewValue(managedBeans.getSelectedUser().getUserName());
			dtlObjPNP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjPNP);
			auditPMReqDTO.setObjList(objList);
		}
		managedBeans.setOldAuditPmUser(null);
		managedBeans.setOldAuditPmUserOrg(null);

		return auditPMReqDTO;
	}
}
