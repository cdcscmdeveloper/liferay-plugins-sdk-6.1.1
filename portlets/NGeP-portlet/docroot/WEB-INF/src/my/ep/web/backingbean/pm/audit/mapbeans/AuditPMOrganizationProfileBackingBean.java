package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
		PmAddress oldPmAddress = managedBean.getAuditBillingAddress();
		String address = "";
		String oldAddressAppend = "";
		
		if (pmAddress != null) {
			if(pmAddress.getAddress1() != null && !pmAddress.getAddress1().isEmpty()){
				address += pmAddress.getAddress1();
			}
			if(pmAddress.getAddress2() != null && !pmAddress.getAddress2().isEmpty()){
			    if (!address.isEmpty()) address += ", ";
				address += pmAddress.getAddress2();
			}
			if(pmAddress.getAddress3() != null && !pmAddress.getAddress3().isEmpty()){
			    if (!address.isEmpty()) address += ", ";
				address += pmAddress.getAddress3();
			}
			

			if (orgProfile.isBrandNew()) {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
				
				if(pmAddress.getPmDivision() != null){
					detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, "", pmAddress.getPmDivision().getDivisionName());
				}
				
				detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, "", address);
				detailObj(AuditConst.PM_AUD_OPM_STATE_FIELDLABELCODE, "", pmAddress.getPmState().getStateName());
				detailObj(AuditConst.PM_AUD_OPM_DISTRICT_FIELDLABELCODE, "", pmAddress.getPmDistrict().getDistrictName());
				detailObj(AuditConst.PM_AUD_OPM_CITY_TOWN_FIELDLABELCODE, "", pmAddress.getPmCity().getCityName());
				detailObj(AuditConst.PM_AUD_OPM_POSTCODE_FIELDLABELCODE, "", pmAddress.getPostcode());
				detailObj(AuditConst.PM_AUD_OPM_COUNTRY_FIELDLABELCODE, "", pmAddress.getPmCountry().getCountryName());
				detailObj(AuditConst.PM_AUD_OPM_TELNO_FIELDLABELCODE, "", 
						pmAddress.getPhoneCountry() + pmAddress.getPhoneArea() + pmAddress.getPhoneNo());
				
				if(managedBean.getOfficeContactPerson() != null ){
					setNewContactPerson(managedBean.getOfficeContactPerson());
				}
				
			} else {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;

				if(oldPmAddress.getAddress1() != null && !oldPmAddress.getAddress1().isEmpty()){
					oldAddressAppend += oldPmAddress.getAddress1();
				}
				if(oldPmAddress.getAddress2() != null && !oldPmAddress.getAddress2().isEmpty()){
				    if (!oldAddressAppend.isEmpty()) oldAddressAppend += ", ";
				    oldAddressAppend += oldPmAddress.getAddress2();
				}
				if(oldPmAddress.getAddress3() != null && !oldPmAddress.getAddress3().isEmpty()){
				    if (!oldAddressAppend.isEmpty()) oldAddressAppend += ", ";
				    oldAddressAppend += oldPmAddress.getAddress3();
				}
				
				if (!address.equals(oldAddressAppend)){
					docId = pmAddress.getAddressId();
					detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, oldAddressAppend, address);
				}
				
				if(pmAddress.getPmDivision() != null){
					if(oldPmAddress.getPmDivision() != null && 
							!pmAddress.getPmDivision().getDivisionName().equals(oldPmAddress.getPmDivision().getDivisionName())){
						detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, 
								oldPmAddress.getPmDivision().getDivisionName(), pmAddress.getPmDivision().getDivisionName());
					} else if(oldPmAddress.getPmDivision() == null){
						detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, "", pmAddress.getPmDivision().getDivisionName());						
					}
				} else if (oldPmAddress.getPmDivision() != null){
					detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, oldPmAddress.getPmDivision().getDivisionName(), "");
				}
				if(!pmAddress.getPmState().getStateName().equals(oldPmAddress.getPmState().getStateName())){
					detailObj(AuditConst.PM_AUD_OPM_STATE_FIELDLABELCODE, oldPmAddress.getPmState().getStateName(), pmAddress.getPmState().getStateName());
				}
				if(!pmAddress.getPmDistrict().getDistrictName().equals(oldPmAddress.getPmDistrict().getDistrictName())){
					detailObj(AuditConst.PM_AUD_OPM_DISTRICT_FIELDLABELCODE, oldPmAddress.getPmDistrict().getDistrictName(), pmAddress.getPmDistrict().getDistrictName());
				}
				if(!pmAddress.getPmCity().getCityName().equals(oldPmAddress.getPmCity().getCityName())){
					detailObj(AuditConst.PM_AUD_OPM_CITY_TOWN_FIELDLABELCODE, oldPmAddress.getPmCity().getCityName(), pmAddress.getPmCity().getCityName());
				}
				if(!pmAddress.getPostcode().equals(oldPmAddress.getPostcode())){
					detailObj(AuditConst.PM_AUD_OPM_POSTCODE_FIELDLABELCODE, oldPmAddress.getPostcode(), pmAddress.getPostcode());
				}
				if(!pmAddress.getPmCountry().getCountryName().equals(oldPmAddress.getPmCountry().getCountryName())){
					detailObj(AuditConst.PM_AUD_OPM_COUNTRY_FIELDLABELCODE, oldPmAddress.getPmCountry().getCountryName(), pmAddress.getPmCountry().getCountryName());
				}
				if(!pmAddress.getPhoneFull().equals(oldPmAddress.getPhoneFull())){
					detailObj(AuditConst.PM_AUD_OPM_TELNO_FIELDLABELCODE, 
							oldPmAddress.getPhoneCountry() + oldPmAddress.getPhoneArea() + oldPmAddress.getPhoneNo(), 
							pmAddress.getPhoneCountry() + pmAddress.getPhoneArea() + pmAddress.getPhoneNo());
				}
				if(!pmAddress.getFaxFull().equals(oldPmAddress.getFaxFull())){
					detailObj(AuditConst.PM_AUD_OPM_FAXNO_FIELDLABELCODE, 
							oldPmAddress.getFaxCountry() + oldPmAddress.getFaxArea() + oldPmAddress.getFaxNo(), 
							pmAddress.getFaxCountry() + pmAddress.getFaxArea() + pmAddress.getFaxNo());
				}
				
				setContact();
			}
		}
	}

	private void setNewContactPerson(PmContact pmContact){
		String officePhoneNo = !pmContact.getPhoneNo().isEmpty() ? 
				pmContact.getPhoneCountry() + pmContact.getPhoneArea() + pmContact.getPhoneNo() : "";
		String officeMobileNo = !pmContact.getMobileNo().isEmpty() ? 
				pmContact.getMobileCountry() + pmContact.getMobileArea() + pmContact.getMobileNo() : "";

		detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, "", pmContact.getContactName());
		detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, "", pmContact.getEmail());
		
		if(!officePhoneNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, "", officePhoneNo);
		}
		if(!officeMobileNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, "", officeMobileNo);
		}
	}
	
	private void setContact() {
		PmContact officeContactPerson = managedBean.getOfficeContactPerson();
		PmContact auditContactPerson = managedBean.getAuditContactPerson();
		String officePhoneNo = "";
		String officeMobileNo = "";
		String auditPhoneNo = "";
		String auditMobileNo = "";

		if(auditContactPerson.getPhoneNo() != null && !auditContactPerson.getPhoneNo().equals("")){
			auditPhoneNo = auditContactPerson.getPhoneCountry() + auditContactPerson.getPhoneArea() + 
					auditContactPerson.getPhoneNo();
		}
		if(auditContactPerson.getMobileNo() != null && !auditContactPerson.getMobileNo().equals("")){
			auditMobileNo = auditContactPerson.getMobileCountry() + auditContactPerson.getMobileArea() + 
					auditContactPerson.getMobileNo();
		}
		
		if(!officeContactPerson.getPhoneNo().equals("")){
			officePhoneNo = officeContactPerson.getPhoneCountry() + officeContactPerson.getPhoneArea() + 
					officeContactPerson.getPhoneNo();
		}
		
		if(!officeContactPerson.getMobileNo().equals("")){
			officeMobileNo = officeContactPerson.getMobileCountry() + officeContactPerson.getMobileArea() + 
					officeContactPerson.getMobileNo();
		}

		if(!officeContactPerson.getContactName().equals(auditContactPerson.getContactName())){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, 
					auditContactPerson.getContactName(), officeContactPerson.getContactName());					
		}
		
		if(!officePhoneNo.equals(auditPhoneNo)){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, 
					auditPhoneNo, officePhoneNo);					
		}
		
		if(!officeContactPerson.getEmail().equals(auditContactPerson.getEmail())){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, 
					auditContactPerson.getEmail(), officeContactPerson.getEmail());					
		}
		
		if(!officeMobileNo.equals(auditMobileNo)){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, 
					auditMobileNo, officeMobileNo);					
		}
	}

	private void setMinistry() {
		String currentMinistryName = "";
		String oldMinistryName = "";

		currentMinistryName = orgProfile.getParentOrgProfile() != null ?
				orgProfile.getParentOrgProfile().getPmOrgValidity().getOrgName() : 
					orgProfile.getPmOrgValidity().getOrgName();
				
		oldMinistryName = oldOrgProfile.getParentOrgProfile() != null ?
				oldOrgProfile.getParentOrgProfile().getPmOrgValidity().getOrgName() : 
					oldOrgProfile.getPmOrgValidity().getOrgName();
		
		if (!orgProfile.isBrandNew()) {
			if(!currentMinistryName.equalsIgnoreCase(oldMinistryName)){
				actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, oldMinistryName, currentMinistryName);	
			} else if (oldMinistryName == null) { //to cater for migration data
				detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, "", currentMinistryName);
			}
		} else {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, "", currentMinistryName);	
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

		if(orgProfile.getSmSupplier() != null){
			setSupplierBank();
			setLoginInfo();
		}
		setPTJList();
		receivingAddress();
	}

	private void receivingAddress() {
		if(managedBean.getNewReceivingAddressList().size() > 0){
			setNewReceivingAddress(managedBean.getNewReceivingAddressList());
		}
		
		if(managedBean.getEditedReceivingAddressList().size() > 0){
			setExistingReceivingAddress(managedBean.getEditedReceivingAddressList(), 
					managedBean.getOldReceivingAddressList());
		}
	}

	private void setNewReceivingAddress(List<PmAddressType> receivingAddress) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		
		for(PmAddressType addressType : receivingAddress){
			detailObj(AuditConst.PM_AUD_OPM_RECEIVING_ADDRESS_FIELDLABELCODE, null, addressType.getPmAddress()
					.getAddressName());
		}
	}

	private void setExistingReceivingAddress(List<PmAddressType> editedAddressType, List<PmAddressType> oldAddressType) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		for(PmAddressType edited : editedAddressType){
			for(PmAddressType current : oldAddressType){
				if(current.getAddressTypeId().equals(edited.getAddressTypeId())){
					if(!current.getPmAddress().getAddressName().equals(edited.getPmAddress().getAddressName())){
						detailObj(AuditConst.PM_AUD_OPM_RECEIVING_ADDRESS_FIELDLABELCODE, 
								current.getPmAddress().getAddressName(), 
								edited.getPmAddress().getAddressName());
					}
					break;
				}
			}
		}
	}

	private void setSupplierBank() {
		SmSupplierBank oldSmSupplierBankCompare = null;

		for (SmSupplierBank smSupplierBank : orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
			for (SmSupplierBank oldSmSupplierBank : orgProfile.getSmSupplier().getSmAppl().getSmSupplierBanks()) {
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
		dtlObj.setGroupOrgProfileId(BigInteger.valueOf(managedBean.getNewProfileId()));
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
		map.put(ParameterConst.ORG_TYPE_OTHERS, ParameterConst.ORG_TYPE_OTHERS);
		
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
