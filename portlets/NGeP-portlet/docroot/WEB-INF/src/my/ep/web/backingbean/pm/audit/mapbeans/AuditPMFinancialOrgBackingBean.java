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
import org.apache.commons.collections.Predicate;

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
	String docno;
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
		docno = financialOrg.getBizRegNumber();
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
			
			// new main address
			// new // contact person
			newMainAddressDetail(financialOrg.getPmAddressTypes());
			
			// financial services
			addFinancialService(savedFinancialOrg.getPmFinancialSvcs());
			
		}
		else{
			PmFinancialOrg oldFinancialOrg = managedBean.getOldFinancialOrg();
			actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			docId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			docGroupId = BigInteger.valueOf(financialOrg.getFinancialOrgId());
			
			setFinancialOrgDetail(financialOrg, oldFinancialOrg);
			
			// address + contact person
			// kalau null salah satu nya, akan problem
			/**
			 * for(PmAddressType address : financialOrg.getPmAddressTypes()){
				for(PmAddressType oldAddress : oldFinancialOrg.getPmAddressTypes()){
					docId = BigInteger.valueOf(address.getAddressTypeId());
					
					if(address.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())){
						modifyContactPerson(address.getPmContacts().get(0), oldAddress.getPmContacts().get(0));
						modifyMainAddressDetail(address.getPmAddress(), oldAddress.getPmAddress());
						break;
					}
				}
				
			}
			 */
			
			// modify financial services
			modifyFinancialService(oldFinancialOrg.getPmFinancialSvcs(), financialOrg.getPmFinancialSvcs());
			
		}
		
		//factoring bank details
		addFactoringBankDetail(financialOrg.getPmFinancialAccs(), savedFinancialOrg.getPmFinancialAccs());
	
		auditPMReqDTO.setObjList(objList);
		managedBean.setOldFinancialOrg(null);
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
			for(PmAddressType address : addressType){
				if(address.getAddressType().equalsIgnoreCase(AddressType.BILLING_ADDRESS.value())){
					docId = BigInteger.valueOf(address.getAddressTypeId());
					PmAddress pmAddress = address.getPmAddress();
					detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, null, pmAddress.getAddress1());
					
					PmContact pmContact = address.getPmContacts().get(0);
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_NAME_FIELDLABELID, null, pmContact.getContactName());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_EMAIL_FIELDLABELID, null, pmContact.getEmail());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_TELEPHONE_FIELDLABELID, null, pmContact.getPhoneNo());
					detailObj(AuditConst.PM_AUD_OFO_CONTACT_MOBILE_FIELDLABELID, null, pmContact.getMobileNo());
					
				}
				
			}
		}
	}
	
	private void modifyMainAddressDetail(PmAddress pmAddress, PmAddress oldAddress){
		if(!pmAddress.getAddress1().equals(oldAddress.getAddress1())){
			detailObj(AuditConst.PM_AUD_OFO_MAIN_ADDRESS_FIELDLABELID, oldAddress.getAddress1(), pmAddress.getAddress1());
		}
		
		
	}
	
	private void modifyContactPerson(PmContact pmContact, PmContact oldContact){
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
	}
	
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
	private void modifyFinancialService(List<PmFinancialSvc> financialService, List<PmFinancialSvc> olfFinancialService){
		setCheckBoxYes(financialService, olfFinancialService);
		
		setCheckBoxNo(olfFinancialService, financialService);
	}

	// tab no 2
	private void addFinancialService(List<PmFinancialSvc> financialService){
		
		if(CollectionUtils.isNotEmpty(financialService)){
			for(PmFinancialSvc currentFinancialServ : financialService){
				currentFinancialServ.getServiceTypeCode().getParameterCode();
				PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(currentFinancialServ.getServiceTypeCode().getParameterId());
				docId = BigInteger.valueOf(currentFinancialServ.getFinancialSvcId());
				
				checkFinancialServiceCode(pmParameter.getParameterCode(),AuditConst.PM_AUD_YES);
				
				/**
				// check for servicetypecode id
				// 106 - performance bond
				// 107 - payment bank
				// 108 - factoring company
				// 109 - registration bank
				*/
			}
			
		}
	}
	
	/**
	 * retrieve service type into mapper
	 */
	private void retrieveServiceTypeMap(){
		List<PmParameterDesc> parameterDescs = managedBean.getBankServicesList();
		serviceTypeMapper = new HashMap<Long,Object>();
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
	
	@SuppressWarnings("unchecked")
	private void setCheckBoxYes(List<PmFinancialSvc> financialService, List<PmFinancialSvc> predicatefinancialService) {

		if (CollectionUtils.isNotEmpty(financialService)) {
			Predicate conditionPredicate;
			for (final PmFinancialSvc pmfinancialService : financialService) {
				List<PmFinancialSvc> pmFinancialSvcList;
				conditionPredicate = new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						PmFinancialSvc financialPredicate = (PmFinancialSvc) object;
						return financialPredicate.getServiceTypeCode().getParameterId() != null
								&& !financialPredicate.getServiceTypeCode().getParameterId()
										.equals(pmfinancialService.getServiceTypeCode().getParameterId());
					}
				};

				pmFinancialSvcList = (List<PmFinancialSvc>) CollectionUtils.select(predicatefinancialService,
						conditionPredicate);
				if (CollectionUtils.isNotEmpty(pmFinancialSvcList)) {
					for (PmFinancialSvc currentFinancialServ : financialService) {
						PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(currentFinancialServ
								.getServiceTypeCode().getParameterId());
						checkFinancialServiceCode(pmParameter.getParameterCode(), AuditConst.PM_AUD_YES);
					}

				}
			}
		}

	}
	
	@SuppressWarnings("unchecked")
	private void setCheckBoxNo(List<PmFinancialSvc> financialService, List<PmFinancialSvc> predicatefinancialService) {

		if (CollectionUtils.isNotEmpty(financialService)) {
			Predicate conditionPredicate;
			for (final PmFinancialSvc pmfinancialService : financialService) {
				List<PmFinancialSvc> pmFinancialSvcList;
				conditionPredicate = new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						PmFinancialSvc financialPredicate = (PmFinancialSvc) object;
						return financialPredicate.getServiceTypeCode().getParameterId() != null
								&& !financialPredicate.getServiceTypeCode().getParameterId()
										.equals(pmfinancialService.getServiceTypeCode().getParameterId());
					}
				};

				pmFinancialSvcList = (List<PmFinancialSvc>) CollectionUtils.select(predicatefinancialService,
						conditionPredicate);
				if (CollectionUtils.isNotEmpty(pmFinancialSvcList)) {
					for (PmFinancialSvc currentFinancialServ : financialService) {
						docId = BigInteger.valueOf(currentFinancialServ.getFinancialSvcId());

						PmParameter pmParameter = (PmParameter) serviceTypeMapper.get(currentFinancialServ
								.getServiceTypeCode().getParameterId());
						checkFinancialServiceCode(pmParameter.getParameterCode(), AuditConst.PM_AUD_NO);
					}

				}
			}
		}
	}
	
	private void checkFinancialServiceCode(String parameterCode, String checked) {
		if (ParameterConst.BANK_SERVICES_FACTORING_COMPANY.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_FACTORING_FIELDLABELID, null, checked);
		} else if (ParameterConst.BANK_SERVICES_PERFORMANCE_BOND.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_PBOND_FIELDLABELID, null, checked);
		} else if (ParameterConst.BANK_SERVICES_PAYMENT_BANK.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_PAYMENT_FIELDLABELID, null, checked);
		} else if (ParameterConst.BANK_SERVICES_REGISTRATION.equalsIgnoreCase(parameterCode)) {
			detailObj(AuditConst.PM_AUD_OFO_FINANCE_REG_FIELDLABELID, null, checked);
		}
	}
}
