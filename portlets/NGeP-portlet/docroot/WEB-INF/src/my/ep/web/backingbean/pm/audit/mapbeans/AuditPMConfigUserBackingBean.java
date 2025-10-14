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
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * AuditPMEducationBackingBean
 * 
 * @author shaqimi 28895_35509
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
			AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID);
	
	private List<BigInteger> federalPTJDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID, AuditConst.PM_AUD_OUSR_GRADE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID, AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID);
	
	private List<BigInteger> otherDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID, AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID,
			AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID, AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID);
	
	private List<BigInteger> otherSupplierDtlObjList = Arrays.asList(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID, AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID);
	
	private Map<Integer, String> lvl3Code = createLvl3CodeMap();
	private Map<Integer, BigInteger> pmParameterLevelId = createPmParameterLevelIdMap();
	
	String roleCode;
	BigInteger docId;
	BigInteger docGroupId;
	String docType;
	String docNo;
	int lvl3CodeIndex;
	String orgName;
	BigInteger orgProfileId;
	String orgType;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleConfUser();
	}
	
	/**
	 * @param status
	 * @return
	 */
	private String pnpCheck(Boolean status){
		return status.equals(Boolean.TRUE) ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO;
	}
	
	/**
	 * @param status
	 * @return
	 */
	private String statusCheck(Integer status){
		return status.equals(AuditConst.PM_AUD_ACTIVE1) ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE;
	}

	
	/* (non-Javadoc)
	 * @see my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate#insert(my.ep.web.managedbean.BaseManagedBean, java.lang.String)
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		auditPMReqDTO = new AuditPMReqDTO();
		
		managedBeans = (ProfileManagementManagedBean) manageBean;
		
		this.roleCode = roleCode;
		this.docType = managedBeans.getSelectedUser().isBrandNew() ? 
				managedBeans.getNewAuditPmUser().getUserName() : managedBeans.getSelectedUser().getUserName();
		this.docId = managedBeans.getOldAuditPmUserOrg() != null ? BigInteger.valueOf(managedBeans.getOldAuditPmUserOrg().getUserOrgId()) : 
			BigInteger.valueOf(managedBeans.getBrandNewUserOrgId());
		this.docGroupId = managedBeans.getSelectedUser().isBrandNew() ? 
				BigInteger.valueOf(managedBeans.getNewAuditPmUser().getUserId()) : 
					BigInteger.valueOf(managedBeans.getSelectedUser().getUserId());
		this.docNo = managedBeans.getSelectedUser().isBrandNew() ? 
				managedBeans.getNewAuditPmUser().getIdentificationNo() : 
					managedBeans.getSelectedUser().getIdentificationNo();
		this.lvl3CodeIndex = managedBeans.getSelectedUserOrg().isBrandNew() ? 
				managedBeans.getNewAuditPmUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId().intValue() : 
					managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterId().intValue();
		this.orgName = managedBeans.getSelectedUserOrg().isBrandNew() ? 
				managedBeans.getCurrentActiveOrg().getPmOrgValidity().getOrgName() : 
					managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgName();
		this.orgProfileId = managedBeans.getSelectedUserOrg().isBrandNew() ? 
				BigInteger.valueOf(managedBeans.getNewAuditPmUserOrg().getUserOrgId()) : 
					BigInteger.valueOf(managedBeans.getSelectedUserOrg().getUserOrgId());
		this.orgType = managedBeans.getSelectedUserOrg().isBrandNew() ? 
				managedBeans.getNewAuditPmUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode() : 
					managedBeans.getSelectedUserOrg().getPmOrgProfile().getOrgTypeCode().getParameterCode();
				
		this.objList = new ArrayList<>();
		
		if(managedBeans.getOrgCategory() == AuditConst.PM_AUD_FEDERAL && federalGovUsers.contains(orgType)){
			if(orgType.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ))
				detailFederalPTJObj();
			else{
				detailFederalObj();
			}
			
			detailRoles();
		} else if(otherUsers.contains(orgType)){
			if(orgType.equals(AuditConst.PM_LVL_CODE_3_OUSR_SUPPL))
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

	/**
	 * set old and new value for federal type user (eP, Ministry, Kumpulan PTJ, Bahagian Perolehan Kerajaan), and sets actionType
	 */
	private void detailFederalObj() {
		for(BigInteger obj : federalDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? pnpCheck(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? pnpCheck(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
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
						managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null ? 
								managedBeans.getOldAuditPmUserOrg().getPhoneCountry() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneArea() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null ? 
								managedBeans.getNewAuditPmUserOrg().getPhoneCountry() +
								managedBeans.getNewAuditPmUserOrg().getPhoneArea() +
								managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getOldAuditPmUserOrg().getMobileCountry() +
								managedBeans.getOldAuditPmUserOrg().getMobileArea() +
								managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getNewAuditPmUserOrg().getMobileCountry() +
								managedBeans.getNewAuditPmUserOrg().getMobileArea() +
								managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? statusCheck(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? statusCheck(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
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


	/**
	 * set old and new value for federal ptj type user (Pusat Tanggungjawab PTJ), and sets actionType
	 */
	private void detailFederalPTJObj() {
		for(BigInteger obj : federalPTJDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? pnpCheck(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? pnpCheck(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
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
						(managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null && 
								!managedBeans.getOldAuditPmUserOrg().getPhoneNo().isEmpty()) ? 
								managedBeans.getOldAuditPmUserOrg().getPhoneCountry() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneArea() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						(managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null && 
								!managedBeans.getNewAuditPmUserOrg().getPhoneNo().isEmpty()) ? 
								managedBeans.getNewAuditPmUserOrg().getPhoneCountry() +
								managedBeans.getNewAuditPmUserOrg().getPhoneArea() +
								managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						(managedBeans.getOldAuditPmUserOrg().getMobileNo() != null && 
								!managedBeans.getOldAuditPmUserOrg().getMobileNo().isEmpty())? 
								managedBeans.getOldAuditPmUserOrg().getMobileCountry() +
								managedBeans.getOldAuditPmUserOrg().getMobileArea() +
								managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						(managedBeans.getNewAuditPmUserOrg().getMobileNo() != null && 
								!managedBeans.getNewAuditPmUserOrg().getMobileNo().isEmpty()) ? 
								managedBeans.getNewAuditPmUserOrg().getMobileCountry() +
								managedBeans.getNewAuditPmUserOrg().getMobileArea() +
								managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? statusCheck(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? statusCheck(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
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

	/**
	 * set old and new value for Other organisation Category, and sets actionType
	 */
	private void detailOtherObj() {
		for(BigInteger obj : otherDtlObjList){
			String oldValue = "";
			String newValue = "";
			String actionType = "";
			
			if(obj.equals(AuditConst.PM_AUD_OUSR_PNP_FIELDLABELID)){
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? pnpCheck(managedBeans.getOldAuditPmUserOrg().isPMP()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? pnpCheck(managedBeans.getNewAuditPmUserOrg().isPMP()) : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_EMAIL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getEmail() != null ? managedBeans.getOldAuditPmUserOrg().getEmail() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null && 
						managedBeans.getNewAuditPmUserOrg().getEmail() != null ? managedBeans.getNewAuditPmUserOrg().getEmail() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_TEL_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getPhoneNo() != null ? 
								managedBeans.getOldAuditPmUserOrg().getPhoneCountry() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneArea() + 
								managedBeans.getOldAuditPmUserOrg().getPhoneNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getPhoneNo() != null ? 
								managedBeans.getNewAuditPmUserOrg().getPhoneCountry() +
								managedBeans.getNewAuditPmUserOrg().getPhoneArea() +
								managedBeans.getNewAuditPmUserOrg().getPhoneNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_MOBILE_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null &&
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getOldAuditPmUserOrg().getMobileCountry() +
								managedBeans.getOldAuditPmUserOrg().getMobileArea() +
								managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getNewAuditPmUserOrg().getMobileCountry() +
								managedBeans.getNewAuditPmUserOrg().getMobileArea() +
								managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
			} else if (obj.equals(AuditConst.PM_AUD_OUSR_STATUS_USER_FIELDLABELID)) {
				oldValue = managedBeans.getOldAuditPmUserOrg() != null ? statusCheck(managedBeans.getOldAuditPmUserOrg().getRecordStatusFlg()) : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null ? statusCheck(managedBeans.getNewAuditPmUserOrg().getRecordStatusFlg()) : "";
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

	/**
	 * set old and new value for Other(Supplier) organisation Category, and sets actionType
	 */
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
						managedBeans.getOldAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getOldAuditPmUserOrg().getMobileCountry() +
								managedBeans.getOldAuditPmUserOrg().getMobileArea() +
								managedBeans.getOldAuditPmUserOrg().getMobileNo() : "";
				newValue = managedBeans.getNewAuditPmUserOrg() != null &&
						managedBeans.getNewAuditPmUserOrg().getMobileNo() != null ? 
								managedBeans.getNewAuditPmUserOrg().getMobileCountry() +
								managedBeans.getNewAuditPmUserOrg().getMobileArea() +
								managedBeans.getNewAuditPmUserOrg().getMobileNo() : "";
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
	
	/**
	 * set old and/or new value for every organization type except for Supplier. Pusat Tanggungjawab PTJ will have extra steps here.
	 */
	private void detailRoles() {
		if(managedBeans.getNewAuditPmUserRoles() != null){
			for(PmUserRole newPmUserRole : managedBeans.getNewAuditPmUserRoles()){
				PmUserRole oldPmUserRole = null;
				
				docId = BigInteger.valueOf(newPmUserRole.getUserRoleId());
				
				if(managedBeans.getOldAuditPmUserRoles() != null){
					for(PmUserRole old : managedBeans.getOldAuditPmUserRoles()){
						if(old.getUserRoleId().equals(newPmUserRole.getUserRoleId())){
							oldPmUserRole = old;
						}
					}
				}
				
				
				if(oldPmUserRole != null){
					if(!oldPmUserRole.getRecordStatusFlg().equals(newPmUserRole.getRecordStatusFlg())){
						detailObj(AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID, statusCheck(oldPmUserRole.getRecordStatusFlg()), 
								statusCheck(newPmUserRole.getRecordStatusFlg()), AuditConst.PM_AUD_ACTION_TYPE_UPD);
					}
					
					if(orgType.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ)){
						if((oldPmUserRole.getApprovalLimit() != null && oldPmUserRole.getApprovalLimit() != null)
								&& (!oldPmUserRole.getApprovalLimit().setScale(2).equals(newPmUserRole.getApprovalLimit().setScale(2)))) {
							detailObj(AuditConst.PM_AUD_OUSR_APPR_LIMIT_FIELDLABELID, oldPmUserRole.getApprovalLimit().setScale(2).toString(), 
									newPmUserRole.getApprovalLimit().setScale(2).toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
						}
					}
					
				} else {
					detailObj(AuditConst.PM_AUD_OUSR_ROLE_FIELDLABELID, null, 
							PmCommonUtil.getRoleDescByRoleCodeAndLanguage(newPmUserRole.getPmRole().getRoleCode(), AuditConst.PM_LANGUAGECODE_MS), 
							AuditConst.PM_AUD_ACTION_TYPE_INS);
					if(orgType.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ) && newPmUserRole.getApprovalLimit() != null){
						detailObj(AuditConst.PM_AUD_OUSR_APPR_LIMIT_FIELDLABELID, null, newPmUserRole.getApprovalLimit().setScale(2).toString(), AuditConst.PM_AUD_ACTION_TYPE_INS);
					}
					detailObj(AuditConst.PM_AUD_OUSR_STATUS_ROLE_FIELDLABELID, null, statusCheck(newPmUserRole.getRecordStatusFlg()), AuditConst.PM_AUD_ACTION_TYPE_INS);
				}
			}
			
			if(orgType.equals(AuditConst.PM_LVL_CODE_3_OUSR_PTJ)){
				detailPTJAddress();
			}
		}
	}

	
	/**
	 * set old and/or new value for every Pusat Tanggungjawab PTJ only
	 */
	private void detailPTJAddress() {
		if(managedBeans.getNewAuditPmAddressTypes() != null){
			for(PmAddressType newPmAddressType : managedBeans.getNewAuditPmAddressTypes()){
				PmAddressType oldPmAddressType = null;
				
				docId = BigInteger.valueOf(newPmAddressType.getAddressTypeId());
				
				if(managedBeans.getOldAuditPmAddressTypes() != null){
					for(PmAddressType old : managedBeans.getOldAuditPmAddressTypes()){
						if(old.getAddressTypeId().equals(newPmAddressType.getAddressTypeId())){
							oldPmAddressType = old;
						}
					}
				}
				
				if(oldPmAddressType != null){
					if(!oldPmAddressType.getRecordStatusFlg().equals(newPmAddressType.getRecordStatusFlg())){
						detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_STATUS_FIELDLABELID, statusCheck(oldPmAddressType.getRecordStatusFlg()), 
								statusCheck(newPmAddressType.getRecordStatusFlg()), AuditConst.PM_AUD_ACTION_TYPE_UPD);
					}
					
				} else {
					detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_NAME_FIELDLABELID, null, getFullAddress(newPmAddressType.getPmAddress()), AuditConst.PM_AUD_ACTION_TYPE_INS);
					detailObj(AuditConst.PM_AUD_OUSR_RCV_ADDR_STATUS_FIELDLABELID, null, statusCheck(newPmAddressType.getRecordStatusFlg()), AuditConst.PM_AUD_ACTION_TYPE_INS);
				}
			}
		}
	}
	
	/**
	 * @param fieldLabelId
	 * @param oldValue
	 * @param newValue
	 * @param actionType
	 */
	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocNo(docNo);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setDocType(docType);
		dtlObj.setPmParameterLvlId(pmParameterLevelId.get(lvl3CodeIndex));
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OUSR);
		dtlObj.setLvlCode3(lvl3Code.get(lvl3CodeIndex));
		dtlObj.setDocId(docId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		dtlObj.setOrgName(orgName);
		dtlObj.setOrgProfileId(orgProfileId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setGroupOrgProfileId(managedBeans.getSelectedUser().isBrandNew() ? 
				BigInteger.valueOf(managedBeans.getNewAuditPmUserOrg().getPmOrgProfile().getOrgProfileId()) : 
					BigInteger.valueOf(managedBeans.getOldAuditPmUserOrg().getPmOrgProfile().getOrgProfileId()));
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
	}
	
	/**
	 * @return
	 */
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
	
	/**
	 * @return
	 */
	
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
