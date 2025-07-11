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
		String serviceType = managedBean.getServiceTypes().get(0);
		checkLevelCode3(serviceType);
		this.roleCode = roleCode;
		docNo = financialOrg.getBizRegNumber();
		docType = financialOrg.getFinOrgName();
		retrieveServiceTypeMap();
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
		String address = "";
		String oldAddressAppend = "";
		
		if (editedAddress != null) {
			if(editedAddress.getAddress1() != null && !editedAddress.getAddress1().isEmpty()){
				address += editedAddress.getAddress1();
			}
			if(editedAddress.getAddress2() != null && !editedAddress.getAddress2().isEmpty()){
			    if (!address.isEmpty()) address += ", ";
				address += editedAddress.getAddress2();
			}
			if(editedAddress.getAddress3() != null && !editedAddress.getAddress3().isEmpty()){
			    if (!address.isEmpty()) address += ", ";
				address += editedAddress.getAddress3();
			}
			

			if (managedBean.getFinancialOrg().isBrandNew()) {
				actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
				
				detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, "", address);
				
				if(managedBean.getContactPerson() != null ){
					setNewContactPerson(managedBean.getContactPerson());
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
					detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, oldAddressAppend, address);
				}

				setContact(managedBean.getContactPerson(), managedBean.getOldContactPerson());
			}
		}
	}

	/**
	 * adds new contact to audit
	 * @param pmContact
	 */
	private void setNewContactPerson(PmContact pmContact){
		String officePhoneNo = !pmContact.getPhoneNo().isEmpty() ? 
				pmContact.getPhoneCountry() + pmContact.getPhoneArea() + pmContact.getPhoneNo() : "";
		String officeMobileNo = !pmContact.getMobileNo().isEmpty() ? 
				pmContact.getMobileCountry() + pmContact.getMobileArea() + pmContact.getMobileNo() : "";

		detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, "", pmContact.getContactName());
		detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, "", pmContact.getEmail());
		
		if(!officePhoneNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, "", officePhoneNo);
		}
		if(!officeMobileNo.isEmpty()){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, "", officeMobileNo);
		}
	}
	
	/**
	 * edits existing contacts
	 * @param editedtContactPerson
	 * @param oldContactPerson
	 */
	private void setContact(PmContact editedtContactPerson, PmContact oldContactPerson) {
		String officePhoneNo = "";
		String officeMobileNo = "";
		String auditPhoneNo = "";
		String auditMobileNo = "";

		if(oldContactPerson.getPhoneNo() != null && !oldContactPerson.getPhoneNo().equals("")){
			auditPhoneNo = oldContactPerson.getPhoneCountry() + oldContactPerson.getPhoneArea() + 
					oldContactPerson.getPhoneNo();
		}
		if(oldContactPerson.getMobileNo() != null && !oldContactPerson.getMobileNo().equals("")){
			auditMobileNo = oldContactPerson.getMobileCountry() + oldContactPerson.getMobileArea() + 
					oldContactPerson.getMobileNo();
		}
		
		if(!editedtContactPerson.getPhoneNo().equals("")){
			officePhoneNo = editedtContactPerson.getPhoneCountry() + editedtContactPerson.getPhoneArea() + 
					editedtContactPerson.getPhoneNo();
		}
		
		if(!editedtContactPerson.getMobileNo().equals("")){
			officeMobileNo = editedtContactPerson.getMobileCountry() + editedtContactPerson.getMobileArea() + 
					editedtContactPerson.getMobileNo();
		}

		if(!editedtContactPerson.getContactName().equals(oldContactPerson.getContactName())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, 
					oldContactPerson.getContactName(), editedtContactPerson.getContactName());					
		}
		
		if(!officePhoneNo.equals(auditPhoneNo)){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, 
					auditPhoneNo, officePhoneNo);					
		}
		
		if(!editedtContactPerson.getEmail().equals(oldContactPerson.getEmail())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, 
					oldContactPerson.getEmail(), editedtContactPerson.getEmail());					
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
			detailObj(AuditConst.PM_AUD_OFO_BANK_STATUS_FIELDLABELID, null, newFinancialAcc.getRecordStatusFlg() == 1? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
		}
		
		if(managedBean.getOldFinancialOrg() != null){
			for(PmFinancialAcc oldFinancialAcc : managedBean.getOldFinancialOrg().getPmFinancialAccs()){
				for(PmFinancialAcc currentFinancialAcc : managedBean.getFinancialOrg().getPmFinancialAccs()){
					if(oldFinancialAcc.getFinancialAccId().equals(currentFinancialAcc.getFinancialAccId()) &&
							!oldFinancialAcc.getRecordStatusFlg().equals(currentFinancialAcc.getRecordStatusFlg())){
						actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
						
						detailObj(AuditConst.PM_AUD_OFO_BANK_STATUS_FIELDLABELID, 
								oldFinancialAcc.getRecordStatusFlg() == 1? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
								currentFinancialAcc.getRecordStatusFlg() == 1? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
						
						break;
					}
				}			
			}
		}
	}
	
	/**
	 * audit financial service tab. caters for new and existing data
	 * @param bankServicesList
	 * @param oldBankServicesList
	 */
	public void financialService(List<PmParameterDesc> bankServicesList, List<PmParameterDesc> oldBankServicesList){
		retrieveServiceType();
		
		if(managedBean.getFinancialOrg().isBrandNew()){
			actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			for(PmParameterDesc edited : bankServicesList){
				detailObj(serviceTypeMap.get(edited.getPmParameter().getParameterId()), null, 
								edited.isSelected() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			}
		} else {
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			
			for(PmParameterDesc current : oldBankServicesList){
				for(PmParameterDesc edited : bankServicesList){
					if(current.getParameterDescId().equals(edited.getParameterDescId()) && 
							current.isSelected() != edited.isSelected()){
						detailObj(serviceTypeMap.get(edited.getPmParameter().getParameterId()), 
								current.isSelected() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, 
										edited.isSelected() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
						break;
					}
				}
			}	
		}
		
	}
	
	/**
	 * retrieve service type into mapper
	 */
	private void retrieveServiceTypeMap(){
		List<PmParameterDesc> parameterDescs = managedBean.getBankServicesList();
		serviceTypeMapper = new HashMap<>();
		for(PmParameterDesc pmDesc: parameterDescs){
			serviceTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * retrieve service type into mapper
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
	
	private void checkLevelCode3(String type){
		
		switch (type){
		
		case ParameterConst.BANK_SERVICES_FACTORING_COMPANY :
			levelCode3 = AuditConst.PM_LVL_CODE_3_OFO_OFOFFC;
			parameterLvlId = AuditConst.PM_AUD_OFOFFC_PMPARAMETERLVLID;
			break;
			
		case ParameterConst.BANK_SERVICES_PAYMENT_BANK :
			levelCode3 = AuditConst.PM_LVL_CODE_3_OFO_OFOPBANK;
			parameterLvlId = AuditConst.PM_AUD_OFOPBANK_PMPARAMETERLVLID;
			break;
			
		case ParameterConst.BANK_SERVICES_PERFORMANCE_BOND :
			levelCode3 = AuditConst.PM_LVL_CODE_3_OFO_OFOPBOND;
			parameterLvlId = AuditConst.PM_AUD_OFOPBOND_PMPARAMETERLVLID;
			break;
			
		case ParameterConst.BANK_SERVICES_REGISTRATION :
			levelCode3 = AuditConst.PM_LVL_CODE_3_OFO_OFOREGBANK;
			parameterLvlId = AuditConst.PM_AUD_OFOREGBANK_PMPARAMETERLVLID;
			break;
			
		default :
			break;
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
