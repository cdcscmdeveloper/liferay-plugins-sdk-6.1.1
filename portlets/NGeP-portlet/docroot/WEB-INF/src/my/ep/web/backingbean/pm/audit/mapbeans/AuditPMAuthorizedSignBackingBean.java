package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmCertSignatory;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.signatory.AuthorizedSignatoryManagedBean;

/**
 * AuditPMAuthorizedSignBackingBean
 * 
 * @author 28895_35509
 */
public class AuditPMAuthorizedSignBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO signatoryName = new AuditPMSPDTO();
	AuditPMSPDTO detailStatus = new AuditPMSPDTO();
	AuditPMSPDTO effectiveDate = new AuditPMSPDTO();
	AuditPMSPDTO expiryDate = new AuditPMSPDTO();
	
	String pattern = "dd-MM-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleAuthorizedSign();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		AuthorizedSignatoryManagedBean managedBean = (AuthorizedSignatoryManagedBean) manageBean;
		List<PmCertSignatory> certSignatories = managedBean.getCertSignatories();
		List<PmCertSignatory> listNew = managedBean.getNewCertSignatories();

		for (PmCertSignatory signatory : certSignatories) {
			if(signatory.isBrandNew()){
				addSignatory( auditPMReqDTO,  roleCode,  signatory, listNew);
				
			}else {
				PmCertSignatory oldsignatory = managedBean.getExistingCertSignatoriesMap().get(signatory.getCertSignatoryId());
				if(!signatory.getExpDate().equals(oldsignatory.getExpDate())) {
					expiryDate.setRoleCode(roleCode);
					expiryDate.setDocType(AuditConst.PM_AUD_AUTHORIZED);
					expiryDate.setDocNo(signatory.getSinnatoryName());
					expiryDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
					expiryDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					expiryDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
					expiryDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
					expiryDate.setDocId(BigInteger.valueOf(signatory.getCertSignatoryId()));
					expiryDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EXP_DATE_FIELDLABELCODE);
					expiryDate.setOldValue(oldsignatory.getExpDate() != null ? simpleDateFormat.format(oldsignatory.getExpDate()) : null);
					expiryDate.setNewValue(signatory.getExpDate() != null ? simpleDateFormat.format(signatory.getExpDate()) : null);
					expiryDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(expiryDate);
					auditPMReqDTO.setObjList(objList);
				}
				
				if(signatory.getRecordStatusFlg() != oldsignatory.getRecordStatusFlg()){
					detailStatus.setRoleCode(roleCode);
					detailStatus.setDocType(AuditConst.PM_AUD_AUTHORIZED);
					detailStatus.setDocNo(signatory.getSinnatoryName());
					detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
					detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
					detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
					detailStatus.setDocId(BigInteger.valueOf(signatory.getCertSignatoryId()));
					detailStatus.setFieldLabelCode(AuditConst.PM_AUD_AS_STATUS_FIELDLABELCODE);
					detailStatus.setOldValue(oldsignatory.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
					detailStatus.setNewValue(signatory.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
					detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailStatus);
					auditPMReqDTO.setObjList(objList);
				}
			}
		}
		managedBean.setExistingCertSignatoriesMap(null);
		managedBean.setNewCertSignatories(null);
		return auditPMReqDTO;
	}
	
	private void addSignatory(AuditPMReqDTO auditPMReqDTO, String roleCode, PmCertSignatory signatory, List<PmCertSignatory> listNew){
		
		for(PmCertSignatory newCertSign :listNew){
			if(newCertSign.getEffDate().equals(signatory.getEffDate())){
				signatoryName.setRoleCode(roleCode);
				signatoryName.setDocType(AuditConst.PM_AUD_AUTHORIZED);
				signatoryName.setDocNo(signatory.getSinnatoryName());
				signatoryName.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
				signatoryName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				signatoryName.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
				signatoryName.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
				signatoryName.setDocId(BigInteger.valueOf(newCertSign.getCertSignatoryId()));
				signatoryName.setFieldLabelCode(AuditConst.PM_AUD_AS_NAME_FIELDLABELCODE);
				signatoryName.setNewValue(signatory.getSinnatoryName());
				signatoryName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(signatoryName);
				
				effectiveDate.setRoleCode(roleCode);
				effectiveDate.setDocType(AuditConst.PM_AUD_AUTHORIZED);
				effectiveDate.setDocNo(signatory.getSinnatoryName());
				effectiveDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
				effectiveDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				effectiveDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
				effectiveDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
				effectiveDate.setDocId(BigInteger.valueOf(newCertSign.getCertSignatoryId()));
				effectiveDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EFF_DATE_FIELDLABELCODE);
				effectiveDate.setNewValue(signatory.getEffDate() != null ? simpleDateFormat.format(signatory.getEffDate()) : null);
				effectiveDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(effectiveDate);
				
				expiryDate.setRoleCode(roleCode);
				expiryDate.setDocType(AuditConst.PM_AUD_AUTHORIZED);
				expiryDate.setDocNo(signatory.getSinnatoryName());
				expiryDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
				expiryDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				expiryDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
				expiryDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
				expiryDate.setDocId(BigInteger.valueOf(newCertSign.getCertSignatoryId()));
				expiryDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EXP_DATE_FIELDLABELCODE);
				expiryDate.setNewValue(signatory.getExpDate() != null ? simpleDateFormat.format(signatory.getExpDate()) : null);
				expiryDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(expiryDate);
				
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(AuditConst.PM_AUD_AUTHORIZED);
				detailStatus.setDocNo(signatory.getSinnatoryName());
				detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
				detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
				detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
				detailStatus.setDocId(BigInteger.valueOf(newCertSign.getCertSignatoryId()));
				detailStatus.setFieldLabelCode(AuditConst.PM_AUD_AS_STATUS_FIELDLABELCODE);
				detailStatus.setNewValue(signatory.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				effectiveDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailStatus);
				auditPMReqDTO.setObjList(objList);
			}
		}
	}

}
