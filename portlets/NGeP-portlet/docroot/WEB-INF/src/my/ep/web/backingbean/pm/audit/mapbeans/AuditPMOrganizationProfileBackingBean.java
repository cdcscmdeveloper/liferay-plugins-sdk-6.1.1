package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;

/**
 * AuditPMOrganizationProfileBackingBean
 * 
 * @author 28895_35509
 */
public class AuditPMOrganizationProfileBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO statusL1 = new AuditPMSPDTO();

	String roleCode;
	String docType;
	Long docId;
	String actionType;
	String docNo;

	OrganisationManagedBean managedBean;
	PmOrgProfile orgProfile;
	PmOrgProfile oldOrgProfile;
	String orgType;
	
	private Map<String, String> lvl3Code = createLvl3CodeMap();
	private Map<String, BigInteger> pmParameterLevelId = createPmParameterLevelIdMap();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleOrganizationProfile();
	}

	/**
	 * @param manageBean
	 * @param roleCode
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		managedBean = (OrganisationManagedBean) manageBean;
		orgProfile = managedBean.getOrgProfile();
		docType = orgProfile.getOrgTypeCode().getPmParameterDescsSpecCallOnly().get(0).getCodeName();
		docNo = orgProfile.getOrgCode();
		orgType = managedBean.getOrgType();
		docId = managedBean.getNewProfileId();
		this.roleCode = roleCode;

		if (!orgProfile.isBrandNew())
			oldOrgProfile = managedBean.getAuditOldPMOrgProfile();

		setMethod(managedBean.getOrgType());

		setAddUpdateGeneralDetails();
		auditPMReqDTO.setObjList(objList);
		managedBean.setAuditBillingAddress(null);
		managedBean.setAuditContactPerson(null);
		managedBean.setOldReceivingAddressList(null);
		managedBean.setAuditOldPMOrgProfile(null);
		return auditPMReqDTO;
	}

	private void setMethod(String orgType) {
		
		if(orgType.equals(ParameterConst.ORG_TYPE_SYS)){
			setSys();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_MINISTRY)) {
			setMinistryCase();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL)) {
			setMinistry();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_PTJ)) {
			setPTJGroup();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_BPK) || orgType.equals(ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA) ||
				orgType.equals(ParameterConst.ORG_TYPE_SUPPORT)) {
			setOrgName();
		} else if (!orgType.equals(ParameterConst.ORG_TYPE_PTJ_GROUP)) {
			setOtherCategory();
		}
	}

	private void setSys() {
		setOrgName();
		setStatus();
	}

	private void setMinistryCase() {
		setOrganisationDetail();
		setOrgName();
		setStatus();
	}

	private void setOtherCategory() {
		setOrgName();
		setStatus();
	}

	private String setStatus() {
		if (orgProfile.isBrandNew() && !orgType.equalsIgnoreCase(ParameterConst.ORG_TYPE_SYS)) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_STATUS_FIELDLABELCODE, null,
					orgProfile.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		} else {
			if (oldOrgProfile.getRecordStatusFlg() != orgProfile.getRecordStatusFlg()) {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				detailObj(
						AuditConst.PM_AUD_OPM_STATUS_FIELDLABELCODE,
						oldOrgProfile.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						orgProfile.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			}
		}
		return null;
	}

	private void setAddUpdateGeneralDetails() {
		PmAddress pmAddress = managedBean.getOfficeAddress();
		if (pmAddress != null) {
			String address = pmAddress.getAddress1() + pmAddress.getAddress2() + pmAddress.getAddress3();

			if (orgProfile.isBrandNew()) {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
				detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, null, address);
				
				PmContact pmContact = managedBean.getOfficeContactPerson();
				if(pmContact != null ){
					setNewContactPerson(pmContact);
				}
			} else {
				PmAddress oldPmAddress = managedBean.getAuditBillingAddress();
				actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				String oldAddressAppend = oldPmAddress.getAddress1() + oldPmAddress.getAddress2() + oldPmAddress.getAddress3();
				
				if (!address.equals(oldAddressAppend)){
					docId = pmAddress.getAddressId();
					detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, oldAddressAppend, address);
				}
				setContact();
			}
		}
	}

	private void setNewContactPerson(PmContact pmContact){
		detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, null, pmContact.getContactName());
		detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, null, pmContact.getEmail());
		if(!pmContact.getMobileNo().isEmpty()){
		detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, null, pmContact.getMobileNo());}
		if(!pmContact.getPhoneNo().isEmpty()){
		detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, null, pmContact.getPhoneNo());}
	}
	
	private void setContact() {
		PmContact pmContact = managedBean.getOfficeContactPerson();
		PmContact oldPmContact = managedBean.getAuditContactPerson();
		
		if(pmContact != null){
			docId = pmContact.getContactId();

			if (!pmContact.getContactName().equals(oldPmContact.getContactName())) {
				detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, oldPmContact.getContactName(),
						pmContact.getContactName());
			}

			if (!pmContact.getEmail().equals(oldPmContact.getEmail())) {
				detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, oldPmContact.getEmail(), pmContact.getEmail());

			}
			if (pmContact.getMobileNo() !=null && !pmContact.getMobileNo().equals(oldPmContact.getMobileNo())) {
				detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, oldPmContact.getMobileNo(),
						pmContact.getMobileNo());

			}
			if (pmContact.getPhoneNo() != null && !pmContact.getPhoneNo().equals(oldPmContact.getPhoneNo())) {
				detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, oldPmContact.getPhoneNo(),
						pmContact.getPhoneNo());
			}
		}
	}

	private void setMinistry() {
		if (!orgProfile.isBrandNew() && !orgProfile.getMinistryName().equals(oldOrgProfile.getMinistryName())) {
			detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, oldOrgProfile.getMinistryName(),
					orgProfile.getMinistryName());
		}
	}

	private void setOrgName() {
		if (orgProfile.isBrandNew()) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_ORGNAME_FIELDLABELCODE, null, orgProfile.getPmOrgValidity().getOrgName());

		} else if (!orgProfile.getPmOrgValidity().getOrgName().equals(oldOrgProfile.getPmOrgValidity().getOrgName())) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			detailObj(AuditConst.PM_AUD_OPM_ORGNAME_FIELDLABELCODE, oldOrgProfile.getPmOrgValidity().getOrgName(),
					orgProfile.getPmOrgValidity().getOrgName());
		}
	}

	private void setOrganisationDetail() {
		if (orgProfile.isBrandNew()) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_ORGCATEGORY_FIELDLABELCODE, null, AuditConst.PM_AUD_FEDERAL_GOVT); // 167
			detailObj(AuditConst.PM_AUD_OPM_ORGTYPE_FIELDLABELCODE, null, managedBean.getOrgType()); // 168 
			detailObj(AuditConst.PM_AUD_OPM_ORGCODE_FIELDLABELCODE, null, managedBean.getOrgProfile().getOrgCode()); // 169
		}
	}

	private void setPTJGroup() {
		if (orgProfile.isGovtSeller() != oldOrgProfile.isGovtSeller()) {
			detailObj(AuditConst.PM_AUD_OPM_G2G_FIELDLABELCODE, null, orgProfile.isGovtSeller() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
		}
		if (orgProfile.getEpPtj() != oldOrgProfile.getEpPtj()) {
			detailObj(AuditConst.PM_AUD_OPM_PTJ_IDENTIFIED_FIELDLABELCODE, null,
					orgProfile.getEpPtj() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
		}
		if (!orgProfile.getGroupOrgType().equals(oldOrgProfile.getGroupOrgType())) {
			detailObj(AuditConst.PM_AUD_OPM_AUTHORIZEDTYPE_FIELDLABELCODE, null, orgProfile.getGroupOrgType());
		}

		setSupplierBank();
		setLoginInfo();
		setPTJList();
		receivingAddress();
	}

	private void receivingAddress() {
		PmAddressType addressTypeCompare = null;
		for (PmAddressType addressType : managedBean.getAddressTypesList()) {
			docId = addressType.getPmAddress().getAddressId();
			for (PmAddressType oldAddressType : managedBean.getOldReceivingAddressList()) {
				if (addressType.getAddressTypeId() == oldAddressType.getAddressTypeId()) {
					addressTypeCompare = oldAddressType;
				}
				break;
			}

			if (addressTypeCompare != null) {
				setExistingReceivingAddress(addressType, addressTypeCompare);
			} else {
				setNewReceivingAddress(addressType);
			}

		}
	}

	private void setNewReceivingAddress(PmAddressType addressType) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		detailObj(AuditConst.PM_AUD_OPM_RECEIVING_ADDRESS_FIELDLABELCODE, null, addressType.getPmAddress()
				.getAddressName());
	}

	private void setExistingReceivingAddress(PmAddressType addressType, PmAddressType oldAddressType) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		if (!addressType.getPmAddress().getAddressName()
				.equalsIgnoreCase(oldAddressType.getPmAddress().getAddressName())) {

			detailObj(AuditConst.PM_AUD_OPM_RECEIVING_ADDRESS_FIELDLABELCODE, oldAddressType.getPmAddress()
					.getAddressName(), addressType.getPmAddress().getAddressName());
		}
	}

	private void setSupplierBank() {
		SmSupplierBank oldSmSupplierBankCompare = null;

		for (SmSupplierBank smSupplierBank : orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
			for (SmSupplierBank oldSmSupplierBank : oldOrgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
				if (oldSmSupplierBank.getSupplierBankId() == smSupplierBank.getSupplierBankId()) {
					oldSmSupplierBankCompare = oldSmSupplierBank;
				}
				break;
			}

			if (oldSmSupplierBankCompare != null) {
				setExistingSupplierBank(smSupplierBank, oldSmSupplierBankCompare);
			} else {
				setNewSupplierBank(smSupplierBank);
			}
		}
	}

	private void setNewSupplierBank(SmSupplierBank smSupplierBank) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		docId = smSupplierBank.getSupplierBankId();

		detailObj(AuditConst.PM_AUD_OPM_PAYMENTBANK_FIELDLABELCODE, null, smSupplierBank.getPmFinancialOrg()
				.getFinOrgName());

		detailObj(AuditConst.PM_AUD_OPM_ACCTNO_FIELDLABELCODE, null, smSupplierBank.getAccountNo());

		detailObj(AuditConst.PM_AUD_OPM_STATUS_PAYMENT_FIELDLABELCODE, null,
				smSupplierBank.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
	}

	private void setExistingSupplierBank(SmSupplierBank smSupplierBank, SmSupplierBank oldSmSupplierBank) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		docId = smSupplierBank.getSupplierBankId();
		if (smSupplierBank.getPmFinancialOrg().getFinOrgName() != oldSmSupplierBank.getPmFinancialOrg().getFinOrgName()) {
			detailObj(AuditConst.PM_AUD_OPM_PAYMENTBANK_FIELDLABELCODE, oldSmSupplierBank.getPmFinancialOrg()
					.getFinOrgName(), smSupplierBank.getPmFinancialOrg().getFinOrgName());
		}

		if (smSupplierBank.getAccountNo() != oldSmSupplierBank.getAccountNo()) {
			detailObj(AuditConst.PM_AUD_OPM_ACCTNO_FIELDLABELCODE, oldSmSupplierBank.getAccountNo(),
					smSupplierBank.getAccountNo());
		}

		if (oldSmSupplierBank.getRecordStatusFlg() != smSupplierBank.getRecordStatusFlg()) {
			detailObj(
					AuditConst.PM_AUD_OPM_STATUS_PAYMENT_FIELDLABELCODE,
					oldSmSupplierBank.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					smSupplierBank.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}
	}

	private void setLoginInfo() {
		SmPersonnel oldSmPersonnelCompare = null;

		for (SmPersonnel smPersonnel : orgProfile.getSmSupplier().getSmAppl().getSmPersonnels()) {
			for (SmPersonnel oldSmPersonnel : oldOrgProfile.getSmSupplier().getSmAppl().getSmPersonnels()) {
				if (oldSmPersonnel.getPersonnelId() == smPersonnel.getPersonnelId()) {
					oldSmPersonnelCompare = oldSmPersonnel;
				}
				break;
			}

			if (oldSmPersonnelCompare != null) {
				setExistingSmPersonal(smPersonnel, oldSmPersonnelCompare);

			} else {
				setNewSmPersonal(smPersonnel);
			}
		}
	}

	private void setNewSmPersonal(SmPersonnel smPersonnel) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

		PmUser user = smPersonnel.getPmUser();
		detailObj(AuditConst.PM_AUD_OPM_LOGINID_FIELDLABELCODE, null, user.getLoginId());
		detailObj(AuditConst.PM_AUD_OPM_USERNAME_FIELDLABELCODE, null, user.getUserName());
		detailObj(AuditConst.PM_AUD_OPM_STATUS_SUPPLIER_FIELDLABELCODE, null,
				smPersonnel.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
	}

	private void setExistingSmPersonal(SmPersonnel smPersonnel, SmPersonnel oldSmPersonnel) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;

		PmUser user = smPersonnel.getPmUser();
		PmUser oldUser = oldSmPersonnel.getPmUser();
		if (user.getLoginId() != oldUser.getLoginId()) {
			detailObj(AuditConst.PM_AUD_OPM_LOGINID_FIELDLABELCODE, oldUser.getLoginId(), user.getLoginId());
		}
		if (user.getUserName() != oldUser.getUserName()) {
			detailObj(AuditConst.PM_AUD_OPM_USERNAME_FIELDLABELCODE, oldUser.getUserName(), user.getUserName());
		}
		if (oldSmPersonnel.getRecordStatusFlg() != smPersonnel.getRecordStatusFlg()) {
			detailObj(AuditConst.PM_AUD_OPM_STATUS_SUPPLIER_FIELDLABELCODE,
					oldSmPersonnel.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					smPersonnel.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}

	}

	private void setPTJList() {
		PmProfileGroup oldProfileGrouptoCompare = null;

		for (PmProfileGroup profileGroup : orgProfile.getMainProfileGroups()) {
			for (PmProfileGroup oldProfileGroup : oldOrgProfile.getMainProfileGroups()) {
				if (oldProfileGroup.getProfileGroupId() == profileGroup.getProfileGroupId()) {
					oldProfileGrouptoCompare = oldProfileGroup;

				}
				break;
			}

			if (oldProfileGrouptoCompare != null) {
				setExistingPTJList(profileGroup, oldProfileGrouptoCompare);
			} else {
				setNewPTJList(profileGroup);
			}

		}
	}

	private void setNewPTJList(PmProfileGroup profileGroup) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		detailObj(AuditConst.PM_AUD_OPM_PTJCODE_FIELDLABELCODE, null, profileGroup.getAuthorizedOrgProfile()
				.getOrgValidity().getOrgCode());
		detailObj(AuditConst.PM_AUD_OPM_PTJNAME_FIELDLABELCODE, null, profileGroup.getAuthorizedOrgProfile()
				.getOrgValidity().getOrgCode());
		detailObj(AuditConst.PM_AUD_OPM_PTJSTATUS_FIELDLABELCODE, null,
				profileGroup.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);

	}

	private void setExistingPTJList(PmProfileGroup profileGroup, PmProfileGroup oldProfileGroup) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		if (profileGroup.getAuthorizedOrgProfile().getOrgValidity().getOrgCode() != oldProfileGroup
				.getAuthorizedOrgProfile().getOrgValidity().getOrgCode()) {
			detailObj(AuditConst.PM_AUD_OPM_PTJCODE_FIELDLABELCODE, oldProfileGroup.getAuthorizedOrgProfile()
					.getOrgValidity().getOrgCode(), profileGroup.getAuthorizedOrgProfile().getOrgValidity()
					.getOrgCode());
		}

		if (profileGroup.getAuthorizedOrgProfile().getOrgName() != oldProfileGroup.getAuthorizedOrgProfile()
				.getOrgName()) {
			detailObj(AuditConst.PM_AUD_OPM_PTJNAME_FIELDLABELCODE, oldProfileGroup.getAuthorizedOrgProfile()
					.getOrgValidity().getOrgCode(), profileGroup.getAuthorizedOrgProfile().getOrgValidity()
					.getOrgCode());
		}

		if (oldProfileGroup.getRecordStatusFlg() != profileGroup.getRecordStatusFlg()) {
			detailObj(AuditConst.PM_AUD_OPM_PTJSTATUS_FIELDLABELCODE,
					oldProfileGroup.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					profileGroup.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}

	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(pmParameterLevelId.get(orgType));
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OPM);
		dtlObj.setLvlCode3(lvl3Code.get(orgType));
		dtlObj.setDocId(BigInteger.valueOf(docId));
		dtlObj.setDocGroupId(BigInteger.valueOf(docId));
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
	
	/**
	 * @return
	 */
	private static Map<String, String> createLvl3CodeMap(){
		Map<String, String> map = new HashMap<>();
		map.put(ParameterConst.ORG_TYPE_SYS, ParameterConst.ORG_TYPE_SYS); 
		map.put(ParameterConst.ORG_TYPE_MINISTRY, ParameterConst.ORG_TYPE_MINISTRY);
		map.put(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL, ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL);
		map.put(ParameterConst.ORG_TYPE_PTJ_GROUP, ParameterConst.ORG_TYPE_PTJ_GROUP);
		map.put(ParameterConst.ORG_TYPE_PTJ, ParameterConst.ORG_TYPE_PTJ);
		map.put(ParameterConst.ORG_TYPE_BPK, ParameterConst.ORG_TYPE_BPK);
		map.put(ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA, ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA);
		map.put(ParameterConst.ORG_TYPE_SUPPORT, ParameterConst.ORG_TYPE_SUPPORT);
		map.put(ParameterConst.ORG_TYPE_GLC, ParameterConst.ORG_TYPE_GLC);
		map.put(ParameterConst.ORG_TYPE_STATUTORY_BODY, ParameterConst.ORG_TYPE_STATUTORY_BODY);
		map.put(ParameterConst.ORG_TYPE_STATE_STATUTORY_BODY, ParameterConst.ORG_TYPE_STATE_STATUTORY_BODY);
		map.put(ParameterConst.ORG_TYPE_STATE_GOVT, ParameterConst.ORG_TYPE_STATE_GOVT);
		map.put(ParameterConst.ORG_TYPE_LOCAL_AUTHORITY, ParameterConst.ORG_TYPE_LOCAL_AUTHORITY);
		map.put(ParameterConst.ORG_TYPE_OTHERS, ParameterConst.ORG_TYPE_LOCAL_AUTHORITY);
		
		return map;
	}
	
	/**
	 * @return
	 */
	
	private static Map<String, BigInteger> createPmParameterLevelIdMap(){
		Map<String, BigInteger> map = new HashMap<>();
		map.put(ParameterConst.ORG_TYPE_SYS, AuditConst.PM_AUD_OPM_SYS_PMPARAMETERLVLID); 
		map.put(ParameterConst.ORG_TYPE_MINISTRY, AuditConst.PM_AUD_OPM_FMI_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_PEGAWAI_PENGAWAL, AuditConst.PM_AUD_OPM_FPP_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_PTJ, AuditConst.PM_AUD_OPM_FPJ_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_PTJ_GROUP, AuditConst.PM_AUD_OPM_FJA_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_BPK, AuditConst.PM_AUD_OPM_BPK_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA, AuditConst.PM_AUD_OPM_AUN_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_SUPPORT, AuditConst.PM_AUD_OPM_SPT_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_GLC, AuditConst.PM_AUD_OPM_GLC_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_STATUTORY_BODY, AuditConst.PM_AUD_OPM_STB_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_STATE_STATUTORY_BODY, AuditConst.PM_AUD_OPM_STS_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_STATE_GOVT, AuditConst.PM_AUD_OPM_STG_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_LOCAL_AUTHORITY, AuditConst.PM_AUD_OPM_LAT_PMPARAMETERLVLID);
		map.put(ParameterConst.ORG_TYPE_OTHERS, AuditConst.PM_AUD_OPM_OTH_PMPARAMETERLVLID);
		
		return map;
	}
}
