package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.financialorg.FinancialOrgManagedBean;

/**
 * AuditPMFinancialOrgBackingBean
 * processing officer role task
 * 
 * @author 28895_35509
 */
public class AuditPMFinancialOrgBackingBean extends AuditPMBackingBeanTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String EMPTY_STRING = "";
	private static final String COMMA_STRING = ", ";

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String docNo;
	String docType;
	String roleCode;
	BigInteger docId;
	BigInteger docGroupId;
	String actionType;
	String levelCode3;
	BigInteger parameterLvlId;
	
	FinancialOrgManagedBean managedBean;
	
	//Mapper for DDL object
	Map<Long, Object> serviceTypeMapper;
	Map<Long, BigInteger> serviceTypeMap;
	
	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleFinancialOrganization();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		managedBean = (FinancialOrgManagedBean) manageBean;
		PmFinancialOrg financialOrg = managedBean.getFinancialOrg();
		this.roleCode = roleCode;
		docNo = financialOrg.getBizRegNumber();
		docType = financialOrg.getFinOrgName();
		levelCode3 = AuditConst.PM_LVL_CODE_3_OFO_OFOFFC;
		parameterLvlId = AuditConst.PM_AUD_OFOFFC_PMPARAMETERLVLID;
		
		PmFinancialOrg savedFinancialOrg = managedBean.getSavedFinancialOrg();
		
		if(financialOrg.isBrandNew()){
			// UI allow null for BizRegNumber
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			docId = BigInteger.valueOf(savedFinancialOrg.getFinancialOrgId());
			docGroupId = BigInteger.valueOf(savedFinancialOrg.getFinancialOrgId());
			
			if(financialOrg.getBizRegNumber() != null){
				detailObj(AuditConst.PM_AUD_OFO_BUSINESS_REG_FIELDLABELID, null, financialOrg.getBizRegNumber());
			}
			
			detailObj(AuditConst.PM_AUD_OFO_FINANCIAL_ORG_FIELDLABELID, null, financialOrg.getFinOrgName());
			
			detailObj(AuditConst.PM_AUD_OFO_BANK_IDENTIFIED_FIELDLABELID, null, financialOrg.isForEp()? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			
			detailObj(AuditConst.PM_AUD_OFO_STATUS_FIELDLABELID, null, financialOrg.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
		}
		else{
			PmFinancialOrg oldFinancialOrg = managedBean.getOldFinancialOrg();
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			docId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			docGroupId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			
			setFinancialOrgDetail(financialOrg, oldFinancialOrg);
		}
		
		mainAddressDetail(managedBean.getPmAddress(), managedBean.getOldPmAddress());
		financialService(managedBean.getBankServicesList(), managedBean.getOldBankServicesList());
		factoringBankDetail();
	
		auditPMReqDTO.setObjList(objList);
		managedBean.setOldFinancialOrg(null);
		managedBean.setSavedFinancialOrg(null);
		managedBean.setOldContactPerson(null);
		return auditPMReqDTO;
	}
	
	private void setFinancialOrgDetail(PmFinancialOrg financialOrg, PmFinancialOrg oldFinancialOrg){
		if(financialOrg.getBizRegNumber() != null && !financialOrg.getBizRegNumber().equals(oldFinancialOrg.getBizRegNumber())){
			detailObj(AuditConst.PM_AUD_OFO_BUSINESS_REG_FIELDLABELID, oldFinancialOrg.getBizRegNumber(), financialOrg.getBizRegNumber());
		}
		
		if(!financialOrg.getFinOrgName().equals(oldFinancialOrg.getFinOrgName())){
			detailObj(AuditConst.PM_AUD_OFO_FINANCIAL_ORG_FIELDLABELID, oldFinancialOrg.getFinOrgName(), financialOrg.getFinOrgName());
		}
		
		if(financialOrg.isForEp() != oldFinancialOrg.isForEp()){
			detailObj(AuditConst.PM_AUD_OFO_BANK_IDENTIFIED_FIELDLABELID, oldFinancialOrg.isForEp()? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO, financialOrg.isForEp()? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
		}
		
		if(!financialOrg.getRecordStatusFlg().equals(oldFinancialOrg.getRecordStatusFlg())){
			detailObj(AuditConst.PM_AUD_OFO_STATUS_FIELDLABELID, oldFinancialOrg.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE, financialOrg.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
		}
	}
	
	/**
	 * sets main address details. caters for new and existing data
	 * @param editedAddress
	 * @param oldPmAddress
	 */
	private void mainAddressDetail(PmAddress editedAddress, PmAddress oldPmAddress){
		String address = EMPTY_STRING;
		String oldAddressAppend = EMPTY_STRING;
		
		if (editedAddress != null) {
			address = getAddress(editedAddress);
		}
		
		if(oldPmAddress != null){
			oldAddressAppend = getAddress(oldPmAddress);
		}
			
		if (managedBean.getFinancialOrg().isBrandNew()) {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			
			if(!address.isEmpty()){
				
				setNewMainAddress(editedAddress, address);
				/**
	 			PM_AUD_OFO_FAX_FIELDLABELID
				 */
			}
			
			if(managedBean.getContactPerson() != null ){
				setNewContactPerson(managedBean.getContactPerson());
			}
		} else {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			
			if (!address.equals(oldAddressAppend)){
				detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, oldAddressAppend, address);
			}

			if (!address.isEmpty())
				setUpdateAddressDetail(editedAddress, oldPmAddress);
			
			setContact(managedBean.getContactPerson(), managedBean.getOldContactPerson());
		}
	}
	
	//sonarlint. to get full address from current and/or edited address
	private String getAddress(PmAddress pmAddress){
		String address = EMPTY_STRING;
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
		
		return address;
	}
	
	private void setNewMainAddress(PmAddress editedAddress, String address){
		
		detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, null, address);
		
		detailObj(AuditConst.PM_AUD_OFO_STATE_FIELDLABELID, null, editedAddress.getPmState().getStateName());
		detailObj(AuditConst.PM_AUD_OFO_DISTRICT_FIELDLABELID, null, editedAddress.getPmDistrict().getDistrictName());
		detailObj(AuditConst.PM_AUD_OFO_CITY_FIELDLABELID, null, editedAddress.getPmCity().getCityName());
		detailObj(AuditConst.PM_AUD_OFO_POSTCODE_FIELDLABELID, null, editedAddress.getPostcode());
		detailObj(AuditConst.PM_AUD_OFO_COUNTRY_FIELDLABELID, null, editedAddress.getPmCountry().getCountryName());
		
		if(!editedAddress.getPhoneNo().isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_PHONE_FIELDLABELID, null, 
					editedAddress.getPhoneCountry() + editedAddress.getPhoneArea() + editedAddress.getPhoneNo());
		}
		
		if(editedAddress.getPmDivision() != null){
			detailObj(AuditConst.PM_AUD_OFO_DIVISION_FIELDLABELID, null, editedAddress.getPmDivision().getDivisionName());
		}
		
		if(!editedAddress.getFaxNo().isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_FAX_FIELDLABELID, null, 
					editedAddress.getFaxCountry() + editedAddress.getFaxArea() + editedAddress.getFaxNo());
		}
		
	}

	/**
	 * adds new contact to audit
	 * @param pmContact
	 */
	private void setNewContactPerson(PmContact pmContact){
		String officePhoneNo = pmContact.getPhoneNo() != null && !pmContact.getPhoneNo().isEmpty() ? 
				pmContact.getPhoneCountry() + pmContact.getPhoneArea() + pmContact.getPhoneNo() : EMPTY_STRING;
				
		String officeMobileNo = pmContact.getMobileNo() != null && !pmContact.getMobileNo().isEmpty() ? 
				pmContact.getMobileCountry() + pmContact.getMobileArea() + pmContact.getMobileNo() : EMPTY_STRING;

		if(!EntityHelper.getStringNestedProperty(pmContact, "contactName").isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, null, EntityHelper.getStringNestedProperty(pmContact, "contactName"));
		}
			
		if(!EntityHelper.getStringNestedProperty(pmContact, "email").isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, null, EntityHelper.getStringNestedProperty(pmContact, "email"));
		}
		
		if(!officePhoneNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, null, officePhoneNo);
		}
		
		if(!officeMobileNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, null, officeMobileNo);
		}
	}
	
	private void setUpdateAddressDetail(PmAddress editedAddress, PmAddress oldPmAddress){
		
		if(!editedAddress.getPmState().getStateName().equals(oldPmAddress.getPmState().getStateName())){
			detailObj(AuditConst.PM_AUD_OFO_STATE_FIELDLABELID, oldPmAddress.getPmState().getStateName(), editedAddress.getPmState().getStateName());
		}
		
		if(!editedAddress.getPmDistrict().getDistrictName().equals(oldPmAddress.getPmDistrict().getDistrictName())){
			detailObj(AuditConst.PM_AUD_OFO_DISTRICT_FIELDLABELID, oldPmAddress.getPmState().getStateName(), editedAddress.getPmDistrict().getDistrictName());
		}
		
		if(!editedAddress.getPmCity().getCityName().equals(oldPmAddress.getPmCity().getCityName())){
			detailObj(AuditConst.PM_AUD_OFO_CITY_FIELDLABELID, oldPmAddress.getPmCity().getCityName(), editedAddress.getPmCity().getCityName());
		}
		
		if(!editedAddress.getPostcode().equals(oldPmAddress.getPostcode())){
			detailObj(AuditConst.PM_AUD_OFO_POSTCODE_FIELDLABELID, oldPmAddress.getPostcode(), editedAddress.getPostcode());
		}
		
		if (editedAddress.getPmDivision() != null
				&& !editedAddress.getPmDivision().equals(oldPmAddress.getPmDivision())) {
			detailObj(AuditConst.PM_AUD_OFO_DIVISION_FIELDLABELID, 
					oldPmAddress.getPmDivision().getDivisionName(), 
					editedAddress.getPmDivision().getDivisionName());
		}
		
		String newPhone = checkEmptyString(editedAddress.getPhoneCountry()) +
				checkEmptyString(editedAddress.getPhoneArea()) +
                checkEmptyString(editedAddress.getPhoneNo());

		String oldPhone = checkEmptyString(oldPmAddress.getPhoneCountry()) +
				checkEmptyString(oldPmAddress.getPhoneArea()) +
                checkEmptyString(oldPmAddress.getPhoneNo());
		
		if (!newPhone.isEmpty() && !newPhone.equals(oldPhone)) {
		    detailObj(AuditConst.PM_AUD_OFO_PHONE_FIELDLABELID,
		              oldPhone.isEmpty() ? EMPTY_STRING : oldPhone,
		              newPhone);
		}
		
		String newFax = checkEmptyString(editedAddress.getFaxCountry()) +
				checkEmptyString(editedAddress.getFaxArea()) +
                checkEmptyString(editedAddress.getFaxNo());

		String oldFax = checkEmptyString(oldPmAddress.getFaxCountry()) +
				checkEmptyString(oldPmAddress.getFaxArea()) +
                checkEmptyString(oldPmAddress.getFaxNo());
		
		if (!newFax.isEmpty() && !newFax.equals(oldFax)) {
		    detailObj(AuditConst.PM_AUD_OFO_FAX_FIELDLABELID,
		    		oldFax.isEmpty() ? EMPTY_STRING : oldFax,
		    				newFax);
		}
		
	}
	

	private String checkEmptyString(String value) {
	    return value == null ? "" : value;
	}
	
	/**
	 * edits existing contacts
	 * @param editedtContactPerson
	 * @param oldContactPerson
	 */
	private void setContact(PmContact editedtContactPerson, PmContact oldContactPerson) {
		String officePhoneNo = EMPTY_STRING;
		String officeMobileNo = EMPTY_STRING;
		String auditPhoneNo = EMPTY_STRING;
		String auditMobileNo = EMPTY_STRING;
		
		String currContactName = EntityHelper.getStringNestedProperty(editedtContactPerson, "contactName");
		String oldContactName = EntityHelper.getStringNestedProperty(oldContactPerson, "contactName");
		String currContactEmail = EntityHelper.getStringNestedProperty(editedtContactPerson, "email");
		String oldContactEmail = EntityHelper.getStringNestedProperty(oldContactPerson, "email");

		if(!currContactName.equals(oldContactName)){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, oldContactName, currContactName);					
		}

		if(!currContactEmail.equals(oldContactEmail)){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, oldContactEmail, currContactEmail);					
		}

		if(oldContactPerson.getPhoneNo() != null && !oldContactPerson.getPhoneNo().isEmpty()){
			auditPhoneNo = oldContactPerson.getPhoneCountry() + oldContactPerson.getPhoneArea() + 
					oldContactPerson.getPhoneNo();
		}
		
		if(oldContactPerson.getMobileNo() != null && !oldContactPerson.getMobileNo().isEmpty()){
			auditMobileNo = oldContactPerson.getMobileCountry() + oldContactPerson.getMobileArea() + 
					oldContactPerson.getMobileNo();
		}
		
		if(editedtContactPerson.getPhoneNo() != null && !editedtContactPerson.getPhoneNo().isEmpty()){
			officePhoneNo = editedtContactPerson.getPhoneCountry() + editedtContactPerson.getPhoneArea() + 
					editedtContactPerson.getPhoneNo();
		}
		
		if(editedtContactPerson.getMobileNo() != null && !editedtContactPerson.getMobileNo().isEmpty()){
			officeMobileNo = editedtContactPerson.getMobileCountry() + editedtContactPerson.getMobileArea() + 
					editedtContactPerson.getMobileNo();
		}
		
		if(!officePhoneNo.equals(auditPhoneNo)){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, 
					auditPhoneNo, officePhoneNo);					
		}
		
		if(!officeMobileNo.equals(auditMobileNo)){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, 
					auditMobileNo, officeMobileNo);					
		}
	}
	
	/**
	 * audit factoring bank detail tab. caters for new and existing data
	 */
	private void factoringBankDetail(){
		for(PmFinancialAcc newFinancialAcc : managedBean.getNewFinancialAccList()){
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			
			detailObj(AuditConst.PM_AUD_OFO_PAYMENT_BANK_NAME_FIELDLABELID, null, newFinancialAcc.getPmPaymentBankOrg().getFinOrgName());
			detailObj(AuditConst.PM_AUD_OFO_BANK_CODE_FIELDLABELID, null, newFinancialAcc.getBankCode());
			detailObj(AuditConst.PM_AUD_OFO_ACCT_NO_FIELDLABELID, null, newFinancialAcc.getAccountNo());
			detailObj(AuditConst.PM_AUD_OFO_BANK_STATUS_FIELDLABELID, null, getActiveOrInactive(newFinancialAcc.getRecordStatusFlg()));
		}
		
		if(managedBean.getOldFinancialOrg() != null){
			editedFactoringDetail(managedBean.getOldFinancialOrg().getPmFinancialAccs(), 
					managedBean.getFinancialOrg().getPmFinancialAccs());
		}
	}
	
	//sonarlint. sets comparison value for edited factoring details
	private void editedFactoringDetail(List<PmFinancialAcc> oldFinancialList, List<PmFinancialAcc> currentFinancialList){
		for(PmFinancialAcc oldFinancialAcc : oldFinancialList){
			for(PmFinancialAcc currentFinancialAcc : currentFinancialList){
				if(oldFinancialAcc.getFinancialAccId().equals(currentFinancialAcc.getFinancialAccId()) &&
						!oldFinancialAcc.getRecordStatusFlg().equals(currentFinancialAcc.getRecordStatusFlg())){
					actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
					
					detailObj(AuditConst.PM_AUD_OFO_BANK_STATUS_FIELDLABELID, 
							getActiveOrInactive(oldFinancialAcc.getRecordStatusFlg()), 
							getActiveOrInactive(currentFinancialAcc.getRecordStatusFlg()));
					break;
				}
			}			
		}
	}
	
	//sonarlint. gets String value of Active/Inactive
	private String getActiveOrInactive(Integer value){
		return value == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE;
	}
	
	/**
	 * audit financial service tab. caters for new and existing data
	 * @param bankServicesList
	 * @param oldBankServicesList
	 */
	public void financialService(List<PmParameterDesc> bankServicesList, List<PmParameterDesc> oldBankServicesList){
		retrieveServiceType();
		
		if(managedBean.getFinancialOrg().isBrandNew()){
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			newFinancialService(bankServicesList);
		} else {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			editedFinancialService(bankServicesList,oldBankServicesList);
		}
		
	}
	
	//sonarlint. get String value of Yes/No
	private String getYesOrNo(boolean value){
		return value ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO;
	}
	
	//sonarlint. sets new values for newly added bank payment details
	private void newFinancialService(List<PmParameterDesc> bankServicesList){
		for(PmParameterDesc edited : bankServicesList){
			detailObj(serviceTypeMap.get(edited.getPmParameter().getParameterId()), null, 
					getYesOrNo(edited.isSelected()));
		}
	}
	
	//sonarlint. sets edited comparison values for bank payment details
	private void editedFinancialService(List<PmParameterDesc> bankServicesList, List<PmParameterDesc> oldBankServicesList){
		for(PmParameterDesc current : oldBankServicesList){
			for(PmParameterDesc edited : bankServicesList){
				if(current.getParameterDescId().equals(edited.getParameterDescId()) && 
						current.isSelected() != edited.isSelected()){
					detailObj(serviceTypeMap.get(edited.getPmParameter().getParameterId()), 
							getYesOrNo(current.isSelected()), getYesOrNo(edited.isSelected()));
					break;
				}
			}
		}
	}
	
	/**
	 * retrieve service type into map
	 */
	private void retrieveServiceType(){
		List<PmParameterDesc> parameterDescs = managedBean.getBankServicesList();
		serviceTypeMap = new HashMap<>();
		for(PmParameterDesc pmDesc: parameterDescs){
			if(pmDesc.getPmParameter().getParameterCode().equals(ParameterConst.BANK_SERVICES_FACTORING_COMPANY)){
				serviceTypeMap.put(pmDesc.getPmParameter().getParameterId(), AuditConst.PM_AUD_OFO_FINANCE_FACTORING_FIELDLABELID);
			} else if(pmDesc.getPmParameter().getParameterCode().equals(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND)){
				serviceTypeMap.put(pmDesc.getPmParameter().getParameterId(), AuditConst.PM_AUD_OFO_FINANCE_PBOND_FIELDLABELID);
			} else if(pmDesc.getPmParameter().getParameterCode().equals(ParameterConst.BANK_SERVICES_PAYMENT_BANK)){
				serviceTypeMap.put(pmDesc.getPmParameter().getParameterId(), AuditConst.PM_AUD_OFO_FINANCE_PAYMENT_FIELDLABELID);
			} else if(pmDesc.getPmParameter().getParameterCode().equals(ParameterConst.BANK_SERVICES_REGISTRATION)){
				serviceTypeMap.put(pmDesc.getPmParameter().getParameterId(), AuditConst.PM_AUD_OFO_FINANCE_REG_FIELDLABELID);
			} 
		}
	}
	

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(parameterLvlId);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_ORGUAD);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_OFO);
		dtlObj.setLvlCode3(levelCode3);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
	
}
