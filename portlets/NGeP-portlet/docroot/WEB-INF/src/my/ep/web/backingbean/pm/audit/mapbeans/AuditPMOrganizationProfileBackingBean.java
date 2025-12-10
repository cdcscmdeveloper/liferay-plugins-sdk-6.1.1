package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.organisation.OrganisationManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;

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
	private static final String EMPTY_STRING = "";
	private static final String COMMA_STRING = ", ";
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO statusL1 = new AuditPMSPDTO();

	String roleCode;
	String docType;
	Long docId;
	Long docGroupId;
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
		docType = orgProfile.getPmOrgValidity().getOrgName();
		docNo = orgProfile.getOrgCode();
		orgType = managedBean.getOrgType();
		docId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
		docGroupId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
		this.roleCode = roleCode;

		if (!orgProfile.isBrandNew())
			oldOrgProfile = managedBean.getAuditOldPMOrgProfile();

		setMethod(managedBean.getOrgType());
		
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
		} else if (orgType.equals(ParameterConst.ORG_TYPE_PTJ_GROUP)) {
			setAddUpdateGeneralDetails();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_PTJ)) {
			setPTJGroup();
		} else if (orgType.equals(ParameterConst.ORG_TYPE_BPK) || orgType.equals(ParameterConst.ORG_TYPE_JABATAN_AUDIT_NEGARA) ||
				orgType.equals(ParameterConst.ORG_TYPE_SUPPORT)) {
			setOrgName();
			setAddUpdateGeneralDetails();
		} else if (!orgType.equals(ParameterConst.ORG_TYPE_PTJ_GROUP)) {
			setOtherCategory();
		}
	}

	private void setSys() {
		setOrgName();
		setStatus();
		setAddUpdateGeneralDetails();
	}

	private void setMinistryCase() {
		setOrganisationDetail();
		setOrgName();
		setStatus();
		setAddUpdateGeneralDetails();
	}

	private void setOtherCategory() {
		setOrgName();
		setStatus();
		setAddUpdateGeneralDetails();
	}

	private String setStatus() {
		docId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
		
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
		String address = EMPTY_STRING;
		String oldAddressAppend = EMPTY_STRING;

		docId = pmAddress.getAddressId();
		
		if (pmAddress != null) {
			if(pmAddress.getAddress1() != null && !pmAddress.getAddress1().isEmpty()){
				address += pmAddress.getAddress1();
			}
			
			if(pmAddress.getAddress2() != null && !pmAddress.getAddress2().isEmpty()){
			    if (!address.isEmpty()) address += COMMA_STRING;
				address += pmAddress.getAddress2();
			}
			
			if(pmAddress.getAddress3() != null && !pmAddress.getAddress3().isEmpty()){
			    if (!address.isEmpty()) address += COMMA_STRING;
				address += pmAddress.getAddress3();
			}
			
			if (orgProfile.isBrandNew() || oldPmAddress.getPmCountry() == null) {//cater for data migration
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

				detailObj(AuditConst.PM_AUD_OPM_COUNTRY_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmCountry().getCountryName());
				detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, EMPTY_STRING, address);
				detailObj(AuditConst.PM_AUD_OPM_STATE_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmState().getStateName());

				if(pmAddress.getPmDivision() != null){
					detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmDivision().getDivisionName());
				}
				
				detailObj(AuditConst.PM_AUD_OPM_DISTRICT_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmDistrict().getDistrictName());
				detailObj(AuditConst.PM_AUD_OPM_CITY_TOWN_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmCity().getCityName());
				detailObj(AuditConst.PM_AUD_OPM_POSTCODE_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPostcode());
				detailObj(AuditConst.PM_AUD_OPM_TELNO_FIELDLABELCODE, EMPTY_STRING, 
						pmAddress.getPhoneCountry() + pmAddress.getPhoneArea() + pmAddress.getPhoneNo());
				
				if(pmAddress.getFaxNo() != null && !pmAddress.getFaxNo().isEmpty()){
					detailObj(AuditConst.PM_AUD_OPM_FAXNO_FIELDLABELCODE, EMPTY_STRING, 
							pmAddress.getFaxCountry() + pmAddress.getFaxArea() + pmAddress.getFaxNo());
				}
				
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
				
				if(!pmAddress.getPmCountry().getCountryName().equals(oldPmAddress.getPmCountry().getCountryName())){
					detailObj(AuditConst.PM_AUD_OPM_COUNTRY_FIELDLABELCODE, oldPmAddress.getPmCountry().getCountryName(), pmAddress.getPmCountry().getCountryName());
				}
				
				if (!address.equals(oldAddressAppend)){
					detailObj(AuditConst.PM_AUD_OPM_OFFICEADDRESS_FIELDLABELCODE, oldAddressAppend, address);
				}
				
				if(!pmAddress.getPmState().getStateName().equals(oldPmAddress.getPmState().getStateName())){
					detailObj(AuditConst.PM_AUD_OPM_STATE_FIELDLABELCODE, oldPmAddress.getPmState().getStateName(), pmAddress.getPmState().getStateName());
				}
				
				if(pmAddress.getPmDivision() != null){
					if(oldPmAddress.getPmDivision() != null && 
							!pmAddress.getPmDivision().getDivisionName().equals(oldPmAddress.getPmDivision().getDivisionName())){
						detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, 
								oldPmAddress.getPmDivision().getDivisionName(), pmAddress.getPmDivision().getDivisionName());
					} else if(oldPmAddress.getPmDivision() == null){
						detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, EMPTY_STRING, pmAddress.getPmDivision().getDivisionName());						
					}
				} else if (oldPmAddress.getPmDivision() != null){
					detailObj(AuditConst.PM_AUD_OPM_DIVISION_FIELDLABELCODE, oldPmAddress.getPmDivision().getDivisionName(), EMPTY_STRING);
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
				
				if(!pmAddress.getPhoneFull().equals(oldPmAddress.getPhoneFull())){
					detailObj(AuditConst.PM_AUD_OPM_TELNO_FIELDLABELCODE, 
							oldPmAddress.getPhoneCountry() + oldPmAddress.getPhoneArea() + oldPmAddress.getPhoneNo(), 
							pmAddress.getPhoneCountry() + pmAddress.getPhoneArea() + pmAddress.getPhoneNo());
				}
				if(!pmAddress.getFaxFull().equals(oldPmAddress.getFaxFull())){
					String auditFax = oldPmAddress.getFaxNo() != null && !oldPmAddress.getFaxNo().isEmpty()  ? 
							oldPmAddress.getFaxCountry() + oldPmAddress.getFaxArea() + oldPmAddress.getFaxNo() : EMPTY_STRING;

					String newFax = pmAddress.getFaxNo() != null && !pmAddress.getFaxNo().isEmpty() ? 
							pmAddress.getFaxCountry() + pmAddress.getFaxArea() + pmAddress.getFaxNo() : EMPTY_STRING;		
							
					detailObj(AuditConst.PM_AUD_OPM_FAXNO_FIELDLABELCODE, auditFax, newFax);
				}
				
				setContact();
			}
		}
	}

	private void setNewContactPerson(PmContact pmContact){
		String officePhoneNo = !pmContact.getPhoneNo().isEmpty() ? 
				pmContact.getPhoneCountry() + pmContact.getPhoneArea() + pmContact.getPhoneNo() : EMPTY_STRING;
		String officeMobileNo = !pmContact.getMobileNo().isEmpty() ? 
				pmContact.getMobileCountry() + pmContact.getMobileArea() + pmContact.getMobileNo() : EMPTY_STRING;

		docId = pmContact.getContactId();

		detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, EMPTY_STRING, pmContact.getContactName());
		detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, EMPTY_STRING, pmContact.getEmail());
		
		if(!officePhoneNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, EMPTY_STRING, officePhoneNo);
		}
		if(!officeMobileNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, EMPTY_STRING, officeMobileNo);
		}
	}
	
	private void setContact() {
		PmContact officeContactPerson = managedBean.getOfficeContactPerson();
		PmContact auditContactPerson = managedBean.getAuditContactPerson();
		String officePhoneNo = EMPTY_STRING;
		String officeMobileNo = EMPTY_STRING;
		String auditPhoneNo = EMPTY_STRING;
		String auditMobileNo = EMPTY_STRING;

		docId = officeContactPerson.getContactId();

		if(auditContactPerson.getPhoneNo() != null && !auditContactPerson.getPhoneNo().isEmpty()){
			auditPhoneNo = auditContactPerson.getPhoneCountry() + auditContactPerson.getPhoneArea() + 
					auditContactPerson.getPhoneNo();
		}
		if(auditContactPerson.getMobileNo() != null && !auditContactPerson.getMobileNo().isEmpty()){
			auditMobileNo = auditContactPerson.getMobileCountry() + auditContactPerson.getMobileArea() + 
					auditContactPerson.getMobileNo();
		}
		
		if(!officeContactPerson.getPhoneNo().isEmpty()){
			officePhoneNo = officeContactPerson.getPhoneCountry() + officeContactPerson.getPhoneArea() + 
					officeContactPerson.getPhoneNo();
		}
		
		if(!officeContactPerson.getMobileNo().isEmpty()){
			officeMobileNo = officeContactPerson.getMobileCountry() + officeContactPerson.getMobileArea() + 
					officeContactPerson.getMobileNo();
		}

		if(!officeContactPerson.getContactName().equals(auditContactPerson.getContactName())){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTNAME_FIELDLABELCODE, 
					auditContactPerson.getContactName(), officeContactPerson.getContactName());					
		}
		
		if(!officeContactPerson.getEmail().equals(auditContactPerson.getEmail())){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTEMAIL_FIELDLABELCODE, 
					auditContactPerson.getEmail(), officeContactPerson.getEmail());					
		}
		
		if(!officePhoneNo.equals(auditPhoneNo)){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTPHONE_FIELDLABELCODE, 
					auditPhoneNo, officePhoneNo);					
		}
		
		if(!officeMobileNo.equals(auditMobileNo)){
			detailObj(AuditConst.PM_AUD_OPM_CONTACTMOBILE_FIELDLABELCODE, 
					auditMobileNo, officeMobileNo);					
		}
	}

	private void setMinistry() {
		String currentMinistryName = orgProfile.getParentOrgProfile() != null ?
				orgProfile.getParentOrgProfile().getPmOrgValidity().getOrgName() : 
					orgProfile.getPmOrgValidity().getOrgName();
				
		String oldMinistryName = oldOrgProfile.getParentOrgProfile() != null ?
				oldOrgProfile.getParentOrgProfile().getPmOrgValidity().getOrgName() : 
					oldOrgProfile.getPmOrgValidity().getOrgName();
				
		docId = managedBean.getNewPmOrgProfile() != null ? 
				managedBean.getNewPmOrgProfile().getPmOrgValidity().getOrgValidityId() : 
					orgProfile.getPmOrgValidity().getOrgValidityId();
		
		if (!orgProfile.isBrandNew()) {
			if(!currentMinistryName.equalsIgnoreCase(oldMinistryName)){
				actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
				detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, oldMinistryName, currentMinistryName);	
			} else if (oldMinistryName == null) { //to cater for migration data
				detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, EMPTY_STRING, currentMinistryName);
			}
		} else {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_MINISTRYNAME_FIELDLABELCODE, EMPTY_STRING, currentMinistryName);	
		}
		
		setAddUpdateGeneralDetails();
	}

	private void setOrgName() {
		docId = managedBean.getNewPmOrgProfile() != null ? 
				managedBean.getNewPmOrgProfile().getPmOrgValidity().getOrgValidityId() : 
					orgProfile.getPmOrgValidity().getOrgValidityId();
				
		if (orgProfile.isBrandNew()) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_ORGNAME_FIELDLABELCODE, null, managedBean.getNewPmOrgProfile().getPmOrgValidity().getOrgName());

		} else if (!orgProfile.getPmOrgValidity().getOrgName().equals(oldOrgProfile.getPmOrgValidity().getOrgName())) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			detailObj(AuditConst.PM_AUD_OPM_ORGNAME_FIELDLABELCODE, oldOrgProfile.getPmOrgValidity().getOrgName(),
					orgProfile.getPmOrgValidity().getOrgName());
		}
	}

	private void setOrganisationDetail() {
		if (orgProfile.isBrandNew()) {
			docId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
			
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_ORGCATEGORY_FIELDLABELCODE, null, getOrgType()); // 167
			detailObj(AuditConst.PM_AUD_OPM_ORGTYPE_FIELDLABELCODE, null, checkOrgType()); // 168 
			detailObj(AuditConst.PM_AUD_OPM_ORGCODE_FIELDLABELCODE, null, managedBean.getOrgProfile().getOrgCode()); // 169
		}
	}

	private void setPTJGroup() {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		docId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
		
		if (orgProfile.getEpPtj() != oldOrgProfile.getEpPtj()) {
			detailObj(AuditConst.PM_AUD_OPM_PTJ_IDENTIFIED_FIELDLABELCODE, null,
					orgProfile.getEpPtj() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
		}
		
		if (orgProfile.isGovtSeller() != oldOrgProfile.isGovtSeller()) {
			detailObj(AuditConst.PM_AUD_OPM_G2G_FIELDLABELCODE, null, orgProfile.isGovtSeller() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
		}

		setGroupOrgType();
		setAddUpdateGeneralDetails();
		receivingAddress();

		if((managedBean.getSmSupplierBanks() != null && managedBean.getSmSupplierBanks().size() > 0)
				 && orgProfile.isGovtSeller()){
			setSupplierBank();
		}
		
		if((managedBean.getSmPersonnels() != null && managedBean.getSmPersonnels().size() > 0) 
				&& orgProfile.isGovtSeller()){
			setLoginInfo();
		}
		
		if(managedBean.getProfileGroup() != null && managedBean.getProfileGroup().size() > 0){
			setPTJList();	
		}
		
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
			docId = addressType.getAddressTypeId();
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
						docId = edited.getAddressTypeId();
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
		if(managedBean.getNewAuditSmSupplierBanks().size() > 0){
			setNewSupplierBank(managedBean.getNewAuditSmSupplierBanks());
		}
		
		if(managedBean.getSmSupplierBanks().size() > 0){
			setExistingSupplierBank(managedBean.getSmSupplierBanks(), 
					managedBean.getAuditSmSupplierBanks());
		}
	}

	private void setNewSupplierBank(List<SmSupplierBank> smSupplierBank) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		
		for(SmSupplierBank banks : smSupplierBank){
			docId = banks.getSupplierBankId();
			detailObj(AuditConst.PM_AUD_OPM_PAYMENTBANK_FIELDLABELCODE, null, banks.getPmFinancialOrg()
					.getFinOrgName());

			detailObj(AuditConst.PM_AUD_OPM_ACCTNO_FIELDLABELCODE, null, banks.getAccountNo());

			detailObj(AuditConst.PM_AUD_OPM_STATUS_PAYMENT_FIELDLABELCODE, null,
					banks.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}
	}

	private void setExistingSupplierBank(List<SmSupplierBank> smSupplierBank, List<SmSupplierBank> oldSmSupplierBank) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		for(SmSupplierBank current : oldSmSupplierBank){
			for(SmSupplierBank edited : smSupplierBank){
				if(current.getSupplierBankId().equals(edited.getSupplierBankId())){
					docId = edited.getSupplierBankId();
					if (!edited.getPmFinancialOrg().getFinOrgName().equals(current.getPmFinancialOrg().getFinOrgName())) {
						detailObj(AuditConst.PM_AUD_OPM_PAYMENTBANK_FIELDLABELCODE, current.getPmFinancialOrg()
								.getFinOrgName(), edited.getPmFinancialOrg().getFinOrgName());
					}

					if (!edited.getAccountNo().equals(current.getAccountNo())) {
						detailObj(AuditConst.PM_AUD_OPM_ACCTNO_FIELDLABELCODE, current.getAccountNo(),
								edited.getAccountNo());
					}

					if (!current.getRecordStatusFlg().equals(edited.getRecordStatusFlg())) {
						detailObj(AuditConst.PM_AUD_OPM_STATUS_PAYMENT_FIELDLABELCODE,
								current.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
										edited.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
					}
					break;
				}
			}
		}
	}

	private void setLoginInfo() {
		if(managedBean.getNewAuditSmPersonnels().size() > 0){
			setNewSmPersonal(managedBean.getNewAuditSmPersonnels());
		}
		
		if(managedBean.getSmPersonnels().size() > 0){
			setExistingSmPersonal(managedBean.getSmPersonnels(), 
					managedBean.getAuditSmPersonnels());
		}
	}

	private void setNewSmPersonal(List<SmPersonnel> smPersonnel) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		
		for(SmPersonnel personnel : smPersonnel){
			docId = personnel.getPersonnelId();
			detailObj(AuditConst.PM_AUD_OPM_LOGINID_FIELDLABELCODE, null, personnel.getPmUser().getLoginId());
			detailObj(AuditConst.PM_AUD_OPM_USERNAME_FIELDLABELCODE, null, personnel.getPmUser().getUserName());
			detailObj(AuditConst.PM_AUD_OPM_STATUS_SUPPLIER_FIELDLABELCODE, null,
					personnel.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}
	}

	private void setExistingSmPersonal(List<SmPersonnel> smPersonnel, List<SmPersonnel> oldSmPersonnel) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		for(SmPersonnel edited : smPersonnel){
			for(SmPersonnel current : oldSmPersonnel){
				if(current.getPersonnelId().equals(edited.getPersonnelId())){
					docId = edited.getPersonnelId();
					if (!current.getRecordStatusFlg().equals(edited.getRecordStatusFlg())) {
						detailObj(AuditConst.PM_AUD_OPM_STATUS_SUPPLIER_FIELDLABELCODE,
								current.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
										edited.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
					}
					break;
				}
			}
		}

	}

	private void setPTJList() {
		if(managedBean.getNewAuditProfileGroup().size() > 0){
			setNewPTJList(managedBean.getNewAuditProfileGroup());
		}
		
		if(orgProfile.getMainProfileGroups().size() > 0){
			setExistingPTJList(orgProfile.getMainProfileGroups(), 
					managedBean.getAuditProfileGroup());
		}
	}
	
	//extracted method. Used to set group org type.
	private void setGroupOrgType() {
		docId = managedBean.getNewProfileId() != null ? managedBean.getNewProfileId() : orgProfile.getOrgProfileId();
		
		String oldAuthType = ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.ORG_AUTH_TYPE, oldOrgProfile.getGroupOrgType(), 
				SourcingConstants.MY_LOCALE);
		String authType = ParameterHelper.getInstance().getParameterDesc(ParameterTypeConst.ORG_AUTH_TYPE, orgProfile.getGroupOrgType(), 
				SourcingConstants.MY_LOCALE);
		
		if (orgProfile.getGroupOrgType() != null && oldOrgProfile.getGroupOrgType() == null) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			detailObj(AuditConst.PM_AUD_OPM_AUTHORIZEDTYPE_FIELDLABELCODE, null, authType);
		} else if (orgProfile.getGroupOrgType() == null && oldOrgProfile.getGroupOrgType() != null) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			detailObj(AuditConst.PM_AUD_OPM_AUTHORIZEDTYPE_FIELDLABELCODE, oldAuthType, null);
		} else if ((orgProfile.getGroupOrgType() != null && oldOrgProfile.getGroupOrgType() != null) &&
				!orgProfile.getGroupOrgType().equals(oldOrgProfile.getGroupOrgType())) {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			detailObj(AuditConst.PM_AUD_OPM_AUTHORIZEDTYPE_FIELDLABELCODE, oldAuthType, authType);
		}
	}

	private void setNewPTJList(List<PmProfileGroup> profileGroups) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

		for(PmProfileGroup profileGroup : profileGroups){
			docId = profileGroup.getProfileGroupId();
			detailObj(AuditConst.PM_AUD_OPM_PTJCODE_FIELDLABELCODE, null, profileGroup.getAuthorizedOrgProfile()
					.getOrgValidity().getOrgCode());
			detailObj(AuditConst.PM_AUD_OPM_PTJNAME_FIELDLABELCODE, null, profileGroup.getAuthorizedOrgProfile()
					.getOrgValidity().getOrgName());
			detailObj(AuditConst.PM_AUD_OPM_PTJSTATUS_FIELDLABELCODE, null,
					profileGroup.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
		}

	}

	private void setExistingPTJList(List<PmProfileGroup> profileGroups, List<PmProfileGroup> oldProfileGroups) {
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;

		for(PmProfileGroup edited : profileGroups){
			for(PmProfileGroup current : oldProfileGroups){
				if(current.getProfileGroupId().equals(edited.getProfileGroupId())){
					if (!current.getRecordStatusFlg().equals(edited.getRecordStatusFlg())) {
						docId = edited.getProfileGroupId();
						detailObj(AuditConst.PM_AUD_OPM_PTJSTATUS_FIELDLABELCODE,
								current.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
										edited.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
					}
					break;
				}
			}
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
		dtlObj.setDocGroupId(BigInteger.valueOf(docGroupId));
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
	
	/**
	 * 
	 * @param orgType
	 * @return
	 */
	private String getOrgType(){
		String label = "lbl.pm.org.federal.government";
		return SmCommonUtil.getMessageMalay(label,null);
	}
	
	private String checkOrgType() {
		String description = null;

		for (PmParameterDesc desc : orgProfile.getOrgTypeCode().getPmParameterDescsSpecCallOnly()) {
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
				description = desc.getCodeName();
			}
		}
		return description;

	}
	
}
