package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.pojobweight.PoJobWeightManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;

/**
 * AuditPMJobWeightBackingBean
 * 
 * @author haiza 28895_35509
 */
public class AuditPMJobWeightBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

	String roleCode;
	String docType;
	Long docId;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.rolePoJobWeight();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		PoJobWeightManagedBean managedBean = (PoJobWeightManagedBean) manageBean;
		List<PmPoJobWeight> jobList = managedBean.getJobWeightList();
		this.roleCode = roleCode;
		this.docType = checkDocType(AuditConst.PM_LVL_CODE_3_JWR);

		for (PmPoJobWeight jobWeight : jobList) {
			for (PmPoJobWeight oriJobWeight : managedBean.getOriJobWeightList()) {
				if (jobWeight.getPoJobWeightId().equals(oriJobWeight.getPoJobWeightId())
						&& !jobWeight.getWeightValue().equals(oriJobWeight.getWeightValue())) {

					updateDetails(jobWeight, oriJobWeight);
					break;
				}
			}
		}

		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private void updateDetails(PmPoJobWeight jobWeight, PmPoJobWeight oriJobWeight) {
		this.docId = jobWeight.getPoJobWeightId();

		if (ParameterConst.JOB_WEIGHT_MODERATE.equals(jobWeight.getWeightType().getParameterCode())) {
			detailObj(AuditConst.PM_AUD_JOB_WEIGHT_MODERATE_FIELDLABELCODE,
					String.valueOf(oriJobWeight.getWeightValue()), String.valueOf(jobWeight.getWeightValue()));

		} else if (ParameterConst.JOB_WEIGHT_HIGH.equals(jobWeight.getWeightType().getParameterCode())) {
			detailObj(AuditConst.PM_AUD_JOB_WEIGHT_HIGH_FIELDLABELCODE, String.valueOf(oriJobWeight.getWeightValue()),
					String.valueOf(jobWeight.getWeightValue()));

		} else if (ParameterConst.JOB_WEIGHT_EXTRA_HIGH.equals(jobWeight.getWeightType().getParameterCode())) {
			detailObj(AuditConst.PM_AUD_JOB_WEIGHT_EXTRA_HIGH_FIELDLABELCODE,
					String.valueOf(oriJobWeight.getWeightValue()), String.valueOf(jobWeight.getWeightValue()));
		}
	}

	private String checkDocType(String parameterCode) {

		String description = null;
		Locale locale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		description = ParameterHelper.getInstance().getParameterDesc("DT", parameterCode, locale);
		
		return description;

	}

	/**
	 * 
	 * @param fieldLabelId
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_JOB_WEIGHT_RATIO_PMPARAMETERLVLID);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_JWR);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_JWR);
		dtlObj.setDocId(BigInteger.valueOf(docId));
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
		objList.add(dtlObj);
		return objList;
	}
}
