package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profilemanagement.ProfileManagementManagedBean;
import my.ep.web.util.FacesScopes;

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
	AuditPMSPDTO dtlObj = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjPNP = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjGrade = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjEmail = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjTelNo = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjMobile = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjStatusUser = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRole = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjApprLimit = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjStatusRole = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRcvAddrName = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjRcvAddrStatus = new AuditPMSPDTO();
	
	FacesScopes facesScopes = new FacesScopes();
	
	AuditPMReqDTO auditPMReqDTO;
	
	ProfileManagementManagedBean managedBeans;
	
	private List<String> federalGovUsers = Arrays.asList(AuditConst.PM_LVL_CODE_3_OUSR_EP, AuditConst.PM_LVL_CODE_3_OUSR_MINISTRY,
			AuditConst.PM_LVL_CODE_3_OUSR_PTJ_GROUP, AuditConst.PM_LVL_CODE_3_OUSR_GOV_PROC, AuditConst.PM_LVL_CODE_3_OUSR_PTJ);
	
	private List<String> otherUsers = Arrays.asList(AuditConst.PM_LVL_CODE_3_OUSR_FACT_COMP, AuditConst.PM_LVL_CODE_3_OUSR_AUD_NTNL, AuditConst.PM_LVL_CODE_3_OUSR_SUPP,
			AuditConst.PM_LVL_CODE_3_OUSR_GOV_LINK_COMP, AuditConst.PM_LVL_CODE_3_OUSR_FED_STAT_BOD, AuditConst.PM_LVL_CODE_3_OUSR_STATE_STAT_BOD,
			AuditConst.PM_LVL_CODE_3_OUSR_STATE_GOV, AuditConst.PM_LVL_CODE_3_OUSR_LOC_AUTH, AuditConst.PM_LVL_CODE_3_OUSR_SUPPL, AuditConst.PM_LVL_CODE_3_OUSR_OTH);
	
	private List<BigInteger> federalDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID, AuditConst.PM_AUD_OUSR_GRADE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID, AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID, AuditConst.PM_AUD_OUSR_ROLE_FIELDLABELID, AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID);
	
	private List<BigInteger> federalPTJDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID, AuditConst.PM_AUD_OUSR_GRADE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID, AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID, AuditConst.PM_AUD_OUSR_ROLE_FIELDLABELID, AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID, 
			AuditConst.PM_AUD_OUSR_APPR_LIMIT_FIELDLABELID, AuditConst.PM_AUD_OUSR_RCV_ADDR_NAME_FIELDLABELID, AuditConst.PM_AUD_OUSR_RCV_ADDR_STATUS_FIELDLABELID);
	
	private List<BigInteger> otherDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID, AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID, AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_ROLE_FIELDLABELID, AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID);
	
	private List<BigInteger> otherSupplierDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID);
	
	private Map<Integer, String> lvl3Code = createLvl3CodeMap();
	private Map<Integer, BigInteger> pmParameterLevelId = createPmParameterLevelIdMap();
	
	String roleCode;
	BigInteger docId;
	String docNo;
	String code;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleConfUser();
	}

	/**
	 * @param args
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		auditPMReqDTO = new AuditPMReqDTO();
		
		managedBeans = (ProfileManagementManagedBean) manageBean;
		
		this.roleCode = roleCode;
		this.docId = BigInteger.valueOf(managedBeans.getSelectedUser().getUserId());
		this.docNo = managedBeans.getSelectedUser().getIdentificationNo();

		this.code = lvl3Code.get(managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId().intValue());
		
		if(managedBeans.getOrgCategory() == AuditConst.PM_AUD_FEDERAL && federalGovUsers.contains(code)){
			if(code.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ))
				detailFederalPTJObj();
			else{
				detailFederalObj();
			}
			
			detailRoles();
		} else if(otherUsers.contains(code)){
			if(code.equals(AuditConst.PM_LVL_CODE_3_OUSR_SUPPL))
				detailOtherSupplierObj();
			else {
				detailOtherObj();
				detailRoles();
			}
		}
		
		auditPMReqDTO.setObjList(objList);
		
		managedBeans.setOldAuditPmUser(null);
		managedBeans.setOldAuditPmUserOrg(null);

		return auditPMReqDTO;
	}

	private void detailFederalObj() {
		for(BigInteger obj : federalDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_GRADE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getGrade() != null ? managedBeans.getOldAuditPmUserOrg().getGrade() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getGrade() != null ? managedBeans.getNewAuditPmUserOrg().getGrade() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getEmail() != null ? managedBeans.getOldAuditPmUserOrg().getEmail() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null && 
						managedBeans.getNewAuditPmUserOrg().getEmail() != null ? managedBeans.getNewAuditPmUserOrg().getEmail() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Integer.toString(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Integer.toString(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
			}
			
			if(managedBeans.getOldAuditPmUserOrg() != null){
				if(!oldValue.equals(newValue)) {
					actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				}
			} else {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			}
			
			if(!actionType.isEmpty()){
				detailObj(obj, oldValue, newValue, actionType);
			}
		}
	}
	
	private void detailFederalPTJObj() {
		for(BigInteger obj : federalPTJDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_GRADE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getGrade() != null ? managedBeans.getOldAuditPmUserOrg().getGrade() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getGrade() != null ? managedBeans.getNewAuditPmUserOrg().getGrade() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getEmail() != null ? managedBeans.getOldAuditPmUserOrg().getEmail() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null && 
						managedBeans.getNewAuditPmUserOrg().getEmail() != null ? managedBeans.getNewAuditPmUserOrg().getEmail() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Integer.toString(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Integer.toString(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
			}
			
			if(managedBeans.getOldAuditPmUserOrg() != null){
				if(!oldValue.equals(newValue)) {
					actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				}
			} else {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			}
			
			if(!actionType.isEmpty()){
				detailObj(obj, oldValue, newValue, actionType);
			}
		}
	}
	
	private void detailOtherObj() {
		for(BigInteger obj : otherDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Boolean.toString(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getEmail() != null ? managedBeans.getOldAuditPmUserOrg().getEmail() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null && 
						managedBeans.getNewAuditPmUserOrg().getEmail() != null ? managedBeans.getNewAuditPmUserOrg().getEmail() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null ? managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? Integer.toString(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? Integer.toString(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
			}
			
			if(managedBeans.getOldAuditPmUserOrg() != null){
				if(!oldValue.equals(newValue)) {
					actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				}
			} else {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			}
			
			if(!actionType.isEmpty()){
				detailObj(obj, oldValue, newValue, actionType);
			}
		}
	}
	
	private void detailOtherSupplierObj() {
		for(BigInteger obj : otherSupplierDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if (obj.equals(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getEmail() != null ? managedBeans.getOldAuditPmUserOrg().getEmail() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null && 
						managedBeans.getNewAuditPmUserOrg().getEmail() != null ? managedBeans.getNewAuditPmUserOrg().getEmail() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			}
			
			if(managedBeans.getOldAuditPmUserOrg() != null){
				if(!oldValue.equals(newValue)) {
					actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				}
			} else {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			}
			
			if(!actionType.isEmpty()){
				detailObj(obj, oldValue, newValue, actionType);
			}
		}
	}
	
	private void detailRoles() {
		for(PmUserRole newPmUserRole : managedBeans.getNewAuditPmUserRoles()){
			PmUserRole oldPmUserRole = null;
			
			for(PmUserRole old : managedBeans.getOldAuditPmUserRoles()){
				if(old.getUserRoleId().equals(newPmUserRole.getUserRoleId())){
					oldPmUserRole = old;
				}
			}
			
			
			if(oldPmUserRole != null){
				if(!oldPmUserRole.getRecordStatusFlg().equals(newPmUserRole.getRecordStatusFlg())){
					detailObj(AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID, oldPmUserRole.getRecordStatusFlg().toString(), 
							newPmUserRole.getRecordStatusFlg().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
				}
				
				if(code.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ)){
					if((oldPmUserRole.getApprovalLimit() != null && oldPmUserRole.getApprovalLimit() != null)
							&& (!oldPmUserRole.getApprovalLimit().setScale(2).equals(newPmUserRole.getApprovalLimit().setScale(2)))) {
						detailObj(AuditConst.PM_AUD_OUSR_APPR_LIMIT_FIELDLABELID, oldPmUserRole.getApprovalLimit().setScale(2).toString(), 
								newPmUserRole.getApprovalLimit().setScale(2).toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
					}
				}
				
			} else {
				detailObj(AuditConst.PM_AUD_OUSR_ROLE_FIELDLABELID, null, newPmUserRole.getPmRole().getRoleCode(), AuditConst.PM_AUD_ACTION_TYPE_INS);
				detailObj(AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID, null, newPmUserRole.getRecordStatusFlg().toString(), AuditConst.PM_AUD_ACTION_TYPE_INS);
				
				if(code.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ) && newPmUserRole.getApprovalLimit() != null){
					detailObj(AuditConst.PM_AUD_OUSR_APPR_LIMIT_FIELDLABELID, null, newPmUserRole.getApprovalLimit().setScale(2).toString(), AuditConst.PM_AUD_ACTION_TYPE_INS);
				}
			}
		}
		
		if(code.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ)){
			detailPTJAddress();
		}
	}
	
	private void detailPTJAddress() {
		for(PmAddressType newPmAddressType : managedBeans.getNewAuditPmAddressTypes()){
			PmAddressType oldPmAddressType = null;
			
			for(PmAddressType old : managedBeans.getOldAuditPmAddressTypes()){
				if(old.getAddressTypeId().equals(newPmAddressType.getAddressTypeId())){
					oldPmAddressType = old;
				}
			}
			
			
			if(oldPmAddressType != null){
				if(!oldPmAddressType.getRecordStatusFlg().equals(newPmAddressType.getRecordStatusFlg())){
					detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_STATUS_FIELDLABELID, oldPmAddressType.getRecordStatusFlg().toString(), 
							newPmAddressType.getRecordStatusFlg().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
				}
				
			} else {
				detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_NAME_FIELDLABELID, null, getFullAddress(newPmAddressType.getPmAddress()), AuditConst.PM_AUD_ACTION_TYPE_INS);
				detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_STATUS_FIELDLABELID, null, newPmAddressType.getRecordStatusFlg().toString(), AuditConst.PM_AUD_ACTION_TYPE_INS);
			}
		}
	}

	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocNo(docNo);
		dtlObj.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedUserOrg().getPmUser().getUserId()));
		dtlObj.setDocType(facesScopes.getUserSessionBean().getUserName());
		dtlObj.setPmParameterLvlId(pmParameterLevelId.get(managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId().intValue()));
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OUSR);
		dtlObj.setLvlCode3(lvl3Code.get(managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId().intValue()));
		dtlObj.setDocId(docId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		dtlObj.setOrgName(managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgName());
		dtlObj.setOrgProfileId(BigInteger.valueOf(managedBeans.getSelectedUserOrg().getUserOrgId()));
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
	}
	
	private static Map<Integer, String> createLvl3CodeMap(){
		Map<Integer, String> map = new HashMap<>();
		map.put(1, AuditConst.PM_LVL_CODE_3_OUSR_EP); 
		map.put(2, AuditConst.PM_LVL_CODE_3_OUSR_MINISTRY); 
		map.put(4, AuditConst.PM_LVL_CODE_3_OUSR_PTJ_GROUP); 
		map.put(5, AuditConst.PM_LVL_CODE_3_OUSR_PTJ); 
		map.put(6, AuditConst.PM_LVL_CODE_3_OUSR_GOV_PROC); 
		map.put(7, AuditConst.PM_LVL_CODE_3_OUSR_FACT_COMP); 
		map.put(8, AuditConst.PM_LVL_CODE_3_OUSR_AUD_NTNL); 
		map.put(9, AuditConst.PM_LVL_CODE_3_OUSR_SUPP); 
		map.put(10, AuditConst.PM_LVL_CODE_3_OUSR_GOV_LINK_COMP); 
		map.put(11, AuditConst.PM_LVL_CODE_3_OUSR_FED_STAT_BOD);  
		map.put(12, AuditConst.PM_LVL_CODE_3_OUSR_STATE_STAT_BOD); 
		map.put(13, AuditConst.PM_LVL_CODE_3_OUSR_STATE_GOV); 
		map.put(14, AuditConst.PM_LVL_CODE_3_OUSR_LOC_AUTH);  
		map.put(15, AuditConst.PM_LVL_CODE_3_OUSR_SUPPL); 
		map.put(209, AuditConst.PM_LVL_CODE_3_OUSR_OTH);
		
		return map;
	}
	
	private static Map<Integer, BigInteger> createPmParameterLevelIdMap(){
		Map<Integer, BigInteger> map = new HashMap<>();
		map.put(1, AuditConst.PM_AUD_OUSR_EP_PMPARAMETERLVLID); 
		map.put(2, AuditConst.PM_AUD_OUSR_MINISTRY_PMPARAMETERLVLID); 
		map.put(4,AuditConst.PM_AUD_OUSR_PTJ_GROUP_PMPARAMETERLVLID); 
		map.put(5,AuditConst.PM_AUD_OUSR_PTJ_PMPARAMETERLVLID); 
		map.put(6, AuditConst.PM_AUD_OUSR_GOV_PROC_PMPARAMETERLVLID); 
		map.put(7, AuditConst.PM_AUD_OUSR_FACT_COMP_PMPARAMETERLVLID); 
		map.put(8, AuditConst.PM_AUD_OUSR_AUD_NTNL_PMPARAMETERLVLID); 
		map.put(9, AuditConst.PM_AUD_OUSR_SUPP_PMPARAMETERLVLID); 
		map.put(10, AuditConst.PM_AUD_OUSR_GOV_LINK_COMP_PMPARAMETERLVLID); 
		map.put(11, AuditConst.PM_AUD_OUSR_FED_STAT_BOD_PMPARAMETERLVLID); 
		map.put(12, AuditConst.PM_AUD_OUSR_STATE_STAT_BOD_PMPARAMETERLVLID); 
		map.put(13, AuditConst.PM_AUD_OUSR_STATE_GOV_PMPARAMETERLVLID); 
		map.put(14, AuditConst.PM_AUD_OUSR_LOC_AUTH_PMPARAMETERLVLID); 
		map.put(15, AuditConst.PM_AUD_OUSR_SUPPL_PMPARAMETERLVLID); 
		map.put(209, AuditConst.PM_AUD_OUSR_OTH_PMPARAMETERLVLID);
		
		return map;
	}
}
