package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

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
	AuditPMSPDTO effectiveDate = new AuditPMSPDTO();

	String roleCode;
	BigInteger docGroupId;

	String pattern = "dd/MM/yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleAuthorizedSign();
	}

	/**
	 * @param manageBean
	 * @return auditPMReqDTO
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		this.roleCode = roleCode;
		AuthorizedSignatoryManagedBean managedBean = (AuthorizedSignatoryManagedBean) manageBean;
		List<PmCertSignatory> certSignatories = managedBean.getCertSignatories();
		
		if (CollectionUtils.isNotEmpty(certSignatories)) {
			this.docGroupId = BigInteger.valueOf(certSignatories.get(0).getPmZone().getZoneId());
		}

		if (CollectionUtils.isNotEmpty(managedBean.getNewCertSignatories())) {
			List<PmCertSignatory> newAddedCertSign = managedBean.getNewCertSignatories();
			addSignatory(certSignatories, newAddedCertSign);
		}

		if (CollectionUtils.isNotEmpty(managedBean.getModifiedCertSignatories())) {
			List<PmCertSignatory> modifiedCertSign = managedBean.getModifiedCertSignatories();
			updateSignatory(managedBean, modifiedCertSign);
		}

		auditPMReqDTO.setObjList(objList);
		managedBean.setExistingCertSignatoriesMap(null);
		managedBean.setNewCertSignatories(null);
		managedBean.setModifiedCertSignatories(null);
		return auditPMReqDTO;
	}

	/**
	 * @param List PmCertSignatory
	 * 
	 */
	private void addSignatory(List<PmCertSignatory> signatory, List<PmCertSignatory> listNew) {

		for (PmCertSignatory newCertSign : listNew) {
			for (PmCertSignatory savedCertSign : signatory) {
				if (newCertSign.getEffDate().equals(savedCertSign.getEffDate())) {
					signatoryName.setRoleCode(roleCode);
					signatoryName.setDocType(savedCertSign.getSinnatoryName());
					signatoryName.setDocGroupId(docGroupId);
					signatoryName.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
					signatoryName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					signatoryName.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
					signatoryName.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
					signatoryName.setDocId(BigInteger.valueOf(savedCertSign.getCertSignatoryId()));
					signatoryName.setFieldLabelCode(AuditConst.PM_AUD_AS_NAME_FIELDLABELCODE);
					signatoryName.setNewValue(savedCertSign.getSinnatoryName());
					signatoryName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(signatoryName);

					effectiveDate.setRoleCode(roleCode);
					effectiveDate.setDocType(savedCertSign.getSinnatoryName());
					effectiveDate.setDocGroupId(docGroupId);
					effectiveDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
					effectiveDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					effectiveDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
					effectiveDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
					effectiveDate.setDocId(BigInteger.valueOf(savedCertSign.getCertSignatoryId()));
					effectiveDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EFF_DATE_FIELDLABELCODE);
					effectiveDate.setNewValue(savedCertSign.getEffDate() != null ? simpleDateFormat.format(savedCertSign
							.getEffDate()) : null);
					effectiveDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(effectiveDate);
					
					setNewValueExpDate(savedCertSign);

					AuditPMSPDTO addStatus = new AuditPMSPDTO();
					addStatus.setRoleCode(roleCode);
					addStatus.setDocType(savedCertSign.getSinnatoryName());
					addStatus.setDocGroupId(docGroupId);
					addStatus.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
					addStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					addStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
					addStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
					addStatus.setDocId(BigInteger.valueOf(savedCertSign.getCertSignatoryId()));
					addStatus.setFieldLabelCode(AuditConst.PM_AUD_AS_STATUS_FIELDLABELCODE);
					addStatus.setNewValue(savedCertSign.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
					addStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(addStatus);
					
					break;
				}
			}
		}
	}

	private void setNewValueExpDate(PmCertSignatory savedCertSign){
		if(savedCertSign.getExpDate() != null){
			AuditPMSPDTO expiryDate = new AuditPMSPDTO();
			expiryDate.setRoleCode(roleCode);
			expiryDate.setDocType(savedCertSign.getSinnatoryName());
			expiryDate.setDocGroupId(docGroupId);
			expiryDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
			expiryDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			expiryDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
			expiryDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
			expiryDate.setDocId(BigInteger.valueOf(savedCertSign.getCertSignatoryId()));
			expiryDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EXP_DATE_FIELDLABELCODE);
			expiryDate.setNewValue( simpleDateFormat.format(savedCertSign.getExpDate()));
			expiryDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(expiryDate);
		}
	}
	
	private void setValueExpDate(PmCertSignatory oldsignatory, PmCertSignatory signatory){
		AuditPMSPDTO expiryDate = new AuditPMSPDTO();
		expiryDate.setRoleCode(roleCode);
		expiryDate.setDocType(signatory.getSinnatoryName());
		expiryDate.setDocGroupId(docGroupId);
		expiryDate.setPmParameterLvlId(AuditConst.PM_AUD_AUTHORIZED_PMPARAMETERLVLID);
		expiryDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		expiryDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_AS);
		expiryDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_AS);
		expiryDate.setDocId(BigInteger.valueOf(signatory.getCertSignatoryId()));
		expiryDate.setFieldLabelCode(AuditConst.PM_AUD_AS_EXP_DATE_FIELDLABELCODE);
		expiryDate.setOldValue(oldsignatory.getExpDate() != null ? simpleDateFormat.format(oldsignatory
				.getExpDate()) : null);
		expiryDate.setNewValue(signatory.getExpDate() != null ? simpleDateFormat.format(signatory.getExpDate())
				: null);
		expiryDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
		objList.add(expiryDate);
	}
	
	/**
	 * @param PmCertSignatory
	 * 
	 */
	private void setDetailExpDate(PmCertSignatory oldsignatory, PmCertSignatory signatory){
		if(oldsignatory.getExpDate() == null){
			if(signatory.getExpDate() != null ){
				setValueExpDate(oldsignatory, signatory);
			}
		}else 
			if (!signatory.getExpDate().equals(oldsignatory.getExpDate())) {
				setValueExpDate(oldsignatory, signatory);
			}
	}
	
	/**
	 * @param List PmCertSignatory
	 * @param managedBean
	 * 
	 */
	private void updateSignatory(AuthorizedSignatoryManagedBean managedBean, List<PmCertSignatory> modifiedCertSign) {
		for (PmCertSignatory signatory : modifiedCertSign) {

			PmCertSignatory oldsignatory = managedBean.getExistingCertSignatoriesMap().get(
					signatory.getCertSignatoryId());
			
			setDetailExpDate(oldsignatory, signatory);
			
			if (signatory.getRecordStatusFlg() != oldsignatory.getRecordStatusFlg()) {
				AuditPMSPDTO detailStatus = new AuditPMSPDTO();
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(signatory.getSinnatoryName());
				detailStatus.setDocGroupId(docGroupId);
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
			}
		}
	}

}
