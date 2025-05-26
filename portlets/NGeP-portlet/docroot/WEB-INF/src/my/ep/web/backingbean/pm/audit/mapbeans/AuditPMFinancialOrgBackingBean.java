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
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmContact;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmFinancialSvc;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.type.pm.AddressType;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.financialorg.FinancialOrgManagedBean;

import org.apache.commons.collections.CollectionUtils;

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
			
			if(financialOrg.getBizRegNumber() != null)
				detailObj(AuditConst.PM_AUD_OFO_BUSINESS_REG_FIELDLABELID, null, financialOrg.getBizRegNumber());
			
			detailObj(AuditConst.PM_AUD_OFO_FINANCIAL_ORG_FIELDLABELID, null, financialOrg.getFinOrgName());
			
			detailObj(AuditConst.PM_AUD_OFO_BANK_IDENTIFIED_FIELDLABELID, null, financialOrg.isForEp()? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			
			detailObj(AuditConst.PM_AUD_OFO_STATUS_FIELDLABELID, null, financialOrg.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);

			// financial services
			addFinancialService(savedFinancialOrg.getPmFinancialSvcs());
			// new main address
			// new contact person
			newMainAddressDetail(savedFinancialOrg.getPmAddressTypes());
		}
		else{
			PmFinancialOrg oldFinancialOrg = managedBean.getOldFinancialOrg();
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			docId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			docGroupId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			
			setFinancialOrgDetail(financialOrg, oldFinancialOrg);
			
			// address + contact person
			// kalau null salah satu nya, akan problem
			/**if(CollectionUtils.isNotEmpty(financialOrg.getPmAddressTypes())){
				modifyMainAddressDetail(financialOrg.getPmAddressTypes(), oldFinancialOrg.getPmAddressTypes());
			}*/
			
			 /** 
			  * // modify financial services
			  * need confirmation from BA 
			  * if current checked, then how about old  data, because there is no previous data
			  * 
			  * modifyFinancialService(financialOrg.getPmFinancialSvcs(), oldFinancialOrg.getPmFinancialSvcs());
			  * 
			  */
			// modify financial services
			modifyFinancialService(financialOrg.getPmFinancialSvcs(), oldFinancialOrg.getPmFinancialSvcs());
		}
		
		//factoring bank details
		addFactoringBankDetail(financialOrg.getPmFinancialAccs(), savedFinancialOrg.getPmFinancialAccs());
	
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
	
	private void newMainAddressDetail(List<PmAddressType> addressType){
		if(CollectionUtils.isNotEmpty(addressType)){
			StringBuilder sb = new StringBuilder();
			for(PmAddressType address : addressType){
				if(address.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())){
					docId = BigInteger.valueOf(address.getAddressTypeId());
					PmAddress pmAddress = address.getPmAddress();
					sb.append(pmAddress.getAddress1());
					sb.append(pmAddress.getAddress2());
					sb.append(pmAddress.getAddress3());
					
					detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, null, sb.toString());
					
					PmContact pmContact = address.getPmContacts().get(0);
					
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, null, pmContact.getContactName());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, null, pmContact.getEmail());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, null, pmContact.getPhoneNo());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, null, pmContact.getMobileNo());
					
				}
			}
		}
	}
	
	private void modifyMainAddressDetail(List<PmAddressType> pmAddressTypeList, List<PmAddressType> oldPmAddressTypeList){
		StringBuilder sb = new StringBuilder();
		StringBuilder oldSb = new StringBuilder();
		
		/**for(PmAddressType addressType : pmAddressTypeList){
			for(PmAddressType oldAddress : oldPmAddressTypeList){
				docId = BigInteger.valueOf(addressType.getAddressTypeId());
				if(addressType.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())){
					PmAddress pmAddress = addressType.getPmAddress();
					sb.append(pmAddress.getAddress1());
					sb.append(pmAddress.getAddress2());
					sb.append(pmAddress.getAddress3());
					
					PmAddress oldPmAddress = oldAddress.getPmAddress();
					oldSb.append(oldPmAddress.getAddress1());
					oldSb.append(oldPmAddress.getAddress2());
					oldSb.append(oldPmAddress.getAddress3());
					
					if(!sb.equals(oldSb)){
						detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, oldSb.toString(), sb.toString());
					}
					
					if(!pmAddress.getPmCity().getCityId().equals(oldPmAddress.getPmCity().getCityId())){
						detailObj(AuditConst.PM_AUD_OFO_CITY_FIELDLABELID, oldPmAddress.getPmCity().getCityName(), pmAddress.getPmCity().getCityName());
					}
					
					if(!pmAddress.getPmDistrict().getDistrictId().equals(oldPmAddress.getPmDistrict().getDistrictId())){
						detailObj(AuditConst.PM_AUD_OFO_DISTRICT_FIELDLABELID, oldSb.toString(), sb.toString());
					}
					
					if(!pmAddress.getPmState().getStateId().equals(oldPmAddress.getPmState().getStateId())){
						detailObj(AuditConst.PM_AUD_OFO_STATE_FIELDLABELID, oldPmAddress.getPmState().getStateName(),pmAddress.getPmState().getStateName());
					}
					
					if(!pmAddress.getPostcode().equals(oldPmAddress.getPostcode())){
						detailObj(AuditConst.PM_AUD_OFO_POSTCODE_FIELDLABELID, oldPmAddress.getPostcode(), pmAddress.getPostcode());
					}
					
					if(!pmAddress.getPhoneFull().equals(oldPmAddress.getPhoneFull())){
						detailObj(AuditConst.PM_AUD_OFO_PHONE_FIELDLABELID, oldPmAddress.getPhoneFull(), pmAddress.getPhoneFull());
					}
					
					if(!pmAddress.getFaxNo().equals(oldPmAddress.getFaxNo())){
						detailObj(AuditConst.PM_AUD_OFO_FAX_FIELDLABELID, oldPmAddress.getFaxNo(), pmAddress.getFaxNo());
					}
					
					modifyContactPerson(addressType.getPmContacts().get(0), oldAddress.getPmContacts().get(0));
				}
			}
		}*/
	}
	
	/**private void modifyContactPerson(PmContact pmContact, PmContact oldContact){
		if(!pmContact.getContactName().equals(oldContact.getContactName())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, oldContact.getContactName(), pmContact.getContactName());
		}
		
		if(!pmContact.getEmail().equals(oldContact.getEmail())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, oldContact.getEmail(), pmContact.getEmail());
		}
		
		if(!pmContact.getPhoneNo().equals(oldContact.getPhoneNo())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, oldContact.getPhoneNo(), pmContact.getPhoneNo());
		}
		
		if(!pmContact.getMobileNo().equals(oldContact.getMobileNo())){
			detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, oldContact.getMobileNo(), pmContact.getMobileNo());
		}
	}*/
	
	// tab no 3
	private void addFactoringBankDetail(List<PmFinancialAcc> financialAccList, List<PmFinancialAcc> savedfinancialAccList){
		if(CollectionUtils.isNotEmpty(financialAccList)){
			for(PmFinancialAcc financialAcc : financialAccList){
				for(PmFinancialAcc savedFinancialAcc : savedfinancialAccList){
					if(financialAcc.isBrandNew() && financialAcc.getPmPaymentBankOrg().getFinancialOrgId() == savedFinancialAcc.getPmPaymentBankOrg().getFinancialOrgId()){
						docId = BigInteger.valueOf(savedFinancialAcc.getFinancialAccId());
						actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

						detailObj(AuditConst.PM_AUD_OFO_PAYMENT_BANK_NAME_FIELDLABELID, null, financialAcc.getPmPaymentBankOrg().getFinOrgName());
						
						detailObj(AuditConst.PM_AUD_OFO_BANK_CODE_FIELDLABELID, null, financialAcc.getBankCode());
						
						detailObj(AuditConst.PM_AUD_OFO_ACCT_NO_FIELDLABELID, null, financialAcc.getAccountNo());
						
						detailObj(AuditConst.PM_AUD_OFO_BANK_STATUS_FIELDLABELID, null, financialAcc.getRecordStatusFlg() == 1? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
						
						break;
						
					}
				}
				
			}
		}
	}
	
	// modify tab no 2
	private void modifyFinancialService(List<PmFinancialSvc> financialService, List<PmFinancialSvc> olfFinancialService) {
		PmFinancialSvc oldFinancialService=null;
		actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		
		if(CollectionUtils.isNotEmpty(financialService)){
			for(PmFinancialSvc svc : financialService){
				PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(svc.getServiceTypeCode().getParameterId());
				for(PmFinancialSvc oldsvc : olfFinancialService){
					if(svc.getServiceTypeCode().getParameterId() == oldsvc.getServiceTypeCode().getParameterId()){
						oldFinancialService = oldsvc;
					}
					break;
					
				}
				if(oldFinancialService != null){
					compareFinancialService(svc.getRecordStatusFlg(), oldFinancialService.getRecordStatusFlg(), pmParameter.getParameterCode());
				}else {
					setNewFinancialService(pmParameter.getParameterCode());
				}
			}
		}
		
	}
	
	/**
	 * @param newStatus
	 * @param oldStatus
	 * @param parameterCode
	 *  comparison status
	 */
	public void compareFinancialService(Integer newStatus, Integer oldStatus, String parameterCode){
		if(!newStatus.equals(oldStatus)){
			
			if (ParameterConst.BANK_SERVICES_FACTORING_COMPANY.equalsIgnoreCase(parameterCode)) {
				detailObj(AuditConst.PM_AUD_OFO_FINANCE_FACTORING_FIELDLABELID, oldStatus==1? AuditConst.PM_AUD_YES
						: AuditConst.PM_AUD_NO, newStatus==1? AuditConst.PM_AUD_YES
								: AuditConst.PM_AUD_NO);
			} else if (ParameterConst.BANK_SERVICES_PERFORMANCE_BOND.equalsIgnoreCase(parameterCode)) {
				detailObj(AuditConst.PM_AUD_OFO_FINANCE_PBOND_FIELDLABELID, oldStatus==1? AuditConst.PM_AUD_YES
						: AuditConst.PM_AUD_NO, newStatus==1? AuditConst.PM_AUD_YES
								: AuditConst.PM_AUD_NO);
			/**} else if (ParameterConst.BANK_SERVICES_PAYMENT_BANK.equalsIgnoreCase(parameterCode)) {
				detailObj(AuditConst.PM_AUD_OFO_FINANCE_PAYMENT_FIELDLABELID, oldStatus==1? AuditConst.PM_AUD_YES
						: AuditConst.PM_AUD_NO, newStatus==1? AuditConst.PM_AUD_YES
								: AuditConst.PM_AUD_NO);*/
			} else if (ParameterConst.BANK_SERVICES_REGISTRATION.equalsIgnoreCase(parameterCode)) {
				detailObj(AuditConst.PM_AUD_OFO_FINANCE_REG_FIELDLABELID, oldStatus==1? AuditConst.PM_AUD_YES
						: AuditConst.PM_AUD_NO, newStatus==1? AuditConst.PM_AUD_YES
								: AuditConst.PM_AUD_NO);
			}
		}
	}
	
	public void setNewFinancialService( String parameterCode){
		if (ParameterConst.BANK_SERVICES_FACTORING_COMPANY.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_FACTORING_FIELDLABELID, null, AuditConst.PM_AUD_YES);
		} else if (ParameterConst.BANK_SERVICES_PERFORMANCE_BOND.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_PBOND_FIELDLABELID, null, AuditConst.PM_AUD_YES);
		} else if (ParameterConst.BANK_SERVICES_PAYMENT_BANK.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_PAYMENT_FIELDLABELID, null, AuditConst.PM_AUD_YES);
		} else if (ParameterConst.BANK_SERVICES_REGISTRATION.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_REG_FIELDLABELID, null, AuditConst.PM_AUD_YES);
		}
	}
	
	// tab no 2
	private void addFinancialService(List<PmFinancialSvc> financialServiceList){

		for (PmFinancialSvc financialService : financialServiceList) {
			PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(financialService.getServiceTypeCode().getParameterId());
			setNewFinancialService(pmParameter.getParameterCode());
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
