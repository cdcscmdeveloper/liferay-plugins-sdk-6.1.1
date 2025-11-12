package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.businessnature.BusinessNatureManagedBean;

/**
 * AuditPMBusinessNature
 * 
 * @author haiza 28895_35509
 * 
 */
public class AuditPMBusinessNatureBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlStatus = new AuditPMSPDTO();
	String docType;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleBusinessNature();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		BusinessNatureManagedBean managedBean = (BusinessNatureManagedBean) manageBean;
		PmBusinessNature newBusinessNature = managedBean.getPmBusinessNature();

		docType = newBusinessNature.getLevelName();
		
		if (managedBean.isBrandNew()) {
			updateDetailsNew(roleCode, newBusinessNature, auditPMReqDTO);

		} else {
			PmBusinessNature oldBusinessNature = managedBean.getOldPmBusinessNature();
			updateDetailsExist(roleCode, oldBusinessNature, newBusinessNature, auditPMReqDTO);
		}

		managedBean.setOldPmBusinessNature(null);
		return auditPMReqDTO;

	}

	private void updateDetailsNew(String roleCode, PmBusinessNature newBusinessNature, AuditPMReqDTO auditPMReqDTO) {

		dtlStatus.setRoleCode(roleCode);
		dtlStatus.setDocNo(newBusinessNature.getLevelCode());
		dtlStatus.setDocType(docType);
		dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_BN);
		dtlStatus.setDocId(BigInteger.valueOf(newBusinessNature.getBusinessNatureId()));
		dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_BUSINESS_NATURE_STATUS_FIELDLABELCODE);
		dtlStatus.setNewValue(newBusinessNature.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
				: AuditConst.PM_AUD_INACTIVE);
		dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

		if (AuditConst.PM_AUD_BN_SECTION.equals(newBusinessNature.getBusinessLevel())) {
			dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_SECTION_PMPARAMETERLVLID);
			dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLSEC);
		} else if (AuditConst.PM_AUD_BN_DIVISION.equals(newBusinessNature.getBusinessLevel())) {
			dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_DIVISION_PMPARAMETERLVLID);
			dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLDIV);
		} else if (AuditConst.PM_AUD_BN_GROUP.equals(newBusinessNature.getBusinessLevel())) {
			dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_GROUP_PMPARAMETERLVLID);
			dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLG);
		} else if (AuditConst.PM_AUD_BN_CLASS.equals(newBusinessNature.getBusinessLevel())) {
			dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_CLASS_PMPARAMETERLVLID);
			dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLC);
		}

		objList.add(dtlStatus);
		auditPMReqDTO.setObjList(objList);

	}

	private void updateDetailsExist(String roleCode, PmBusinessNature oldBusinessNature,
			PmBusinessNature newBusinessNature, AuditPMReqDTO auditPMReqDTO) {

		dtlStatus.setRoleCode(roleCode);
		dtlStatus.setDocNo(newBusinessNature.getLevelCode());
		dtlStatus.setDocType(docType);
		dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_BN);
		dtlStatus.setDocId(BigInteger.valueOf(newBusinessNature.getBusinessNatureId()));
		dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_BUSINESS_NATURE_STATUS_FIELDLABELCODE);
		dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

		if (newBusinessNature.getRecordStatusFlg() != oldBusinessNature.getRecordStatusFlg()) {
			dtlStatus.setOldValue(oldBusinessNature.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setNewValue(newBusinessNature.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);

			if (AuditConst.PM_AUD_BN_SECTION.equals(newBusinessNature.getBusinessLevel())) {
				dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_SECTION_PMPARAMETERLVLID);
				dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLSEC);
			} else if (AuditConst.PM_AUD_BN_DIVISION.equals(newBusinessNature.getBusinessLevel())) {
				dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_DIVISION_PMPARAMETERLVLID);
				dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLDIV);
			} else if (AuditConst.PM_AUD_BN_GROUP.equals(newBusinessNature.getBusinessLevel())) {
				dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_GROUP_PMPARAMETERLVLID);
				dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLG);
			} else if (AuditConst.PM_AUD_BN_CLASS.equals(newBusinessNature.getBusinessLevel())) {
				dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_BN_CLASS_PMPARAMETERLVLID);
				dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_BNLVLC);
			}

			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);

		}
	}
}
