package my.ep.web.backingbean.sm.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.certbody.CertBodyResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;


@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends BaseBackingBean<RegistrationManagedBean>{

	public static final String SUPPLIER = "SUP";

	public static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public static String newICMasking = "999999999999";
	private static String accountRegistration = "registrationInfo";
	private static String signUp = "signUp";
	private static String firstUserRegistration = "firstUserRegistration";
	
	private static String defaultAccPurpose = "R";
	private static Map<Long, Object> bizNatureL5Map;
	private static Map<Long, Object> identityType;

	public RegistrationBackingBean() {
//		if (MDC.get(LogKey.USER_ID) == null) {
//			log.info("-------MDC is NULL-----------");
//			MDC.put(LogKey.USER_ID, 1L);
//		}
//
/*		if (this.getManagedBean().getApplType() == null) {
			this.getManagedBean().setApplType(SupplierManagementConst.ACC_MOF);
		}*/
		
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser != null && pmUser.getUserId() != null) {
			System.out.println("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	public String proceedToCompanyInfo() {
		if (this.getManagedBean().getSmSupp() == null) {
			log.info("-------RegistrationBackingBean is NUll-------------");
			//default to malaysia
			PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);

			this.getManagedBean().setSmSupp(new SmSupplier());

			SmSupplier smSupp = this.getManagedBean().getSmSupp();
			smSupp.setSmAppls(new ArrayList<SmAppl>());
			smSupp.setSmSupplierOwners(new ArrayList<SmSupplierOwner>());

			this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			this.getManagedBean().setSmAppl(new SmAppl());
			//this.getManagedBean().setSmSuppBanks(new ArrayList<SmSupplierBank>());

			SmAppl smAppl = this.getManagedBean().getSmAppl();
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
			smAppl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
			this.getManagedBean().setSmPersonnel(new SmPersonnel());
//			this.getManagedBean().getSmPersonnel().setPmCountry(new PmCountry());
			this.getManagedBean().getSmPersonnel().setPmCountry(defaultCountry);
			
			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().getSmBusinessAdd().setPmCountry(new PmCountry());
//			this.getManagedBean().getSmBusinessAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
			this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
//			this.getManagedBean().getSmResAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmResAdd().setPmCountry(defaultCountry);
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmPermenantAdd(new SmAddress());
			this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
//			this.getManagedBean().getSmPermenantAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmPermenantAdd().setPmCountry(defaultCountry);
			this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
			this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());

			this.getManagedBean().setSameAddress(false);
			
			//this.getManagedBean().setSuppBusinessNatureLs(new ArrayList<SmSupplierBusiness>());
			smSupp.setSmSupplierBusinesses(new ArrayList<SmSupplierBusiness>());
			
			this.retrievePmCountryList();
			this.populateStateByAddType("permenant");
			this.populateStateByAddType("residential");
			this.populateStateByAddType("business");
			this.retrieveFinancialOrgList();
			
			//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());
			this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
			//TODO: temporary comment this.retrieveBusinessNatureL5Map();
			this.retrieveIdentityTypeMap();
		
			
			log.info("**********permenant"+ this.getManagedBean().getPermenantStateLs().size());
			log.info("**********residential" + this.getManagedBean().getResidentialStateLs().size());
		}

		this.getManagedBean().getSmAppl().setApplType(this.getManagedBean().getApplType());
		getIndividualServiceType();
		log.info("-------onlineAccountAgreement end-------------");
		
		return accountRegistration;
	}

	public String proceedToFirstUserRegistration() {
		log.info("****proceedToFirstUserRegistration******");
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);

		}
		
		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);

		}
		
		if(checkExistingBusinessRegNo()){
			throw new EpException(MessageConst.ERROR_721003);

		}
		
		if(!validateBankInfo()){
			return null;
		}

		SmSupplierOwner suppOwner = this.getManagedBean().getSmSuppOwner();
		//verifyWithMyIdentity(this.getManagedBean().getSmSuppOwner().getOwnerName(), this.getManagedBean().getSmBusinessAdd().getPostcode());
		System.out.println("-----------Owner ID Type---------" + suppOwner.getIdentificationType().getParameterCode());
		System.out.println("-----------Owner ID---------" + suppOwner.getIdentificationNo());
		
		validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
		
		if(this.getManagedBean().getSmPersonnel().getPmCountry().isBrandNew()){
			PmCountry nationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			this.getManagedBean().getSmPersonnel().setPmCountry(nationality);
		}
		
		return firstUserRegistration;
	}

	public String returnToSignUp() {
		return signUp;
	}

	public String returnToCompanyInfo() {
		return accountRegistration;
	}

	public void addProfessionalCert() {
		log.info("-------------------addProfessionalCert-----------");
		if(this.getManagedBean().getSmAppl().getSmSupplierCerts() == null){
			this.getManagedBean().getSmAppl().setSmSupplierCerts(new ArrayList<SmSupplierCert>());
			this.retrieveCertBody();
		}
		SmSupplierCert newSuppCert = new SmSupplierCert();
		newSuppCert.setPmCertBody(new PmCertBody());
		this.getManagedBean().getSmAppl().getSmSupplierCerts().add(newSuppCert);
	}

	public void removeProfessionalCert() {
		log.info("-------------------removeProfessionalCert-----------");
		for (SmSupplierCert suppCert : this.getManagedBean().getSelectedSuppCerts()) {
			//this.getManagedBean().getSmSuppCerts().remove(suppCert);
			this.getManagedBean().getSmAppl().getSmSupplierCerts().remove(suppCert);
			log.info("---removeSupplierCert---" + suppCert);
			// suppBank.getPmFinancialOrg().getFinancialOrgId()
		}
	}

	public String submitRegistration() {
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
		SmAddress smResAdd = this.getManagedBean().getSmResAdd();
		
		if(!smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
		}
		
		if(smAppl.getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}
		
		if(!validateBankInfo()){
			return null;
		}
		
		if (smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)) {
			PmParameter identificationType = new PmParameter();
			
			for (Map.Entry<Long, Object> entry : identityType.entrySet()) {
					PmParameter identParam = (PmParameter) entry.getValue();
					if(identParam.getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
						identificationType = identParam;
						break;
					}
			}
			
			PmCountry indNationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			smSupp.setServiceCategory(SupplierManagementConst.SPECIAL_SERVICE); //DEFAULT for Individual
//			smSuppOwner.setIdentificationType(SupplierManagementConst.BASIC_IND_IDENT_TYPE);
			smSuppOwner.setIdentificationTypeId(identificationType.getParameterId());
			smSuppOwner.setOwnerName(smAppl.getSmCompanyBasic().getCompanyName());
			smSuppOwner.setIdentificationNo(smSupp.getRegNo());
			smPersonnel.setName(smAppl.getSmCompanyBasic().getCompanyName());
//			smPersonnel.setIdentificationTypeId(SupplierManagementConst.BASIC_IND_IDENT_TYPE);
			smPersonnel.setIdentificationTypeId(identificationType.getParameterId());
			smPersonnel.setIdentificationNo(smSupp.getRegNo());
			smPersonnel.setPmCountry(indNationality);
			this.getManagedBean().setSmBusinessAdd(this.getManagedBean().getSmPermenantAdd());
		}
		
		//Validate IC Length
		this.retrieveIdentityTypeMap();
		PmParameter personnelIDType = (PmParameter) identityType.get(smPersonnel.getIdentificationTypeId());
		validateIDFormat(smPersonnel.getIdentificationNo(), personnelIDType.getParameterCode());
		
		if(isExistEmailInPmUser(this.getManagedBean().getSmPersonnel().getEmail())){
			throw new EpException(MessageConst.ERROR_721004);

		}
		
		String loginID = smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG) ? smSupp.getRegNo() : this.getManagedBean().getSmPersonnel().getIdentificationNo();
		if(isExistUserByIdentificationNo(loginID)){
			throw new EpException(MessageConst.ERROR_721005);

		}
		
		// validate user's email existence at Liferay. 
		try {
			if(UserLocalServiceUtil.getUserByEmailAddress(Long.valueOf(getCompanyId()), this.getManagedBean().getSmPersonnel().getEmail()) != null) {
//				FacesContext.getCurrentInstance().addMessage(null,
//						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email Address already exist.", "Email Address already exist."));
//				return null;
				throw new EpException(MessageConst.ERROR_721015);
			}
			
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		} catch (PortalException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		} catch (SystemException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		}
		
		try {
			
			if(UserLocalServiceUtil.getUserByScreenName(Long.valueOf(getCompanyId()), this.getManagedBean().getSmPersonnel().getIdentificationNo()) != null) {
//			if(UserLocalServiceUtil.getUserByScreenName(1, "user011") != null) {
//				FacesContext.getCurrentInstance().addMessage(null,
//						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login ID already exist.", "Login ID already exist."));
//				return null;
				throw new EpException(MessageConst.ERROR_721016);
			}
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		} catch (PortalException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		} catch (SystemException e) {
//			e.printStackTrace();
			log.info(e.getMessage());
		}
		
		
		if (smAppl.getApplType().equals(SupplierManagementConst.ACC_BASIC)) {
			smAppl.setSupplierType(SupplierManagementConst.STYPE_BASIC);
		}

		smSupp.setCompanyName(smAppl.getSmCompanyBasic().getCompanyName());
		smAppl.setSmSupplier(smSupp);
		smAppl.getSmCompanyBasic().setSmAppl(smAppl);
		smAppl.getSmCompanyBasic().setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smAppl.getSmCompanyBasic().setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		
		/* add personnel And personnel address */
		this.getManagedBean().getSmPersonnel().setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPersonnel.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
		smPersonnel.setAuthorizedFlg(SupplierManagementConst.FIRST_USER_AUTHO);
		smPersonnel.setContactPerson(SupplierManagementConst.FIRST_USER_CONT_PERSON);
		smPersonnel.setSoftcertFlg(SupplierManagementConst.REG_SOFTCERT);
		smPersonnel.setBumiFlg(SupplierManagementConst.REG_BUMI);
		smPersonnel.setContractSignerFlg(SupplierManagementConst.FIRST_USER_CONT_SIGNER);
		smPersonnel.setEpRole(RoleCode.BASIC_SUPPLIER_ADMIN.value());

		SmPersonnelAddress personnelAddRes = new SmPersonnelAddress();
		SmPersonnelAddress personnelAddPerm = new SmPersonnelAddress();
		
		personnelAddRes.setAddressType(SupplierManagementConst.RES_ADD);
		if(this.getManagedBean().isSameAddress()){
			personnelAddRes.setSmAddress(smPermAdd);
			personnelAddRes.setSmPersonnel(smPersonnel);
		}else{
			personnelAddRes.setSmAddress(smResAdd);
			personnelAddRes.setSmPersonnel(smPersonnel);
			smResAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
			smResAdd.getSmPersonnelAddresses().add(personnelAddRes);
		}
		smPersonnel.getSmPersonnelAddresses().add(personnelAddRes);
		personnelAddPerm.setAddressType(SupplierManagementConst.PERM_ADD);
		personnelAddPerm.setSmAddress(smPermAdd);
		personnelAddPerm.setSmPersonnel(smPersonnel);
		smPermAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPermAdd.getSmPersonnelAddresses().add(personnelAddPerm);
		smPersonnel.getSmPersonnelAddresses().add(personnelAddPerm);
		
		smPersonnel.setSmAppl(smAppl);
		smAppl.getSmPersonnels().add(smPersonnel);	
		
		/* add company address */
		SmCompanyAddress smCompAdd = new SmCompanyAddress();
		smCompAdd.setAddressType(SupplierManagementConst.BUS_ADD);
		smCompAdd.setSmCompanyBasic(smAppl.getSmCompanyBasic());
		smCompAdd.setSmAddress(this.getManagedBean().getSmBusinessAdd());
		smAppl.getSmCompanyBasic().getSmCompanyAddresses().add(smCompAdd);

		for(SmSupplierBank suppBank : smAppl.getSmSupplierBanks()){
			suppBank.setSmAppl(smAppl);
			suppBank.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
			suppBank.setAccountPurpose(defaultAccPurpose);
			//smAppl.getSmSupplierBanks().add(suppBank);
		}
		
		for(SmSupplierBusiness smSuppBiz : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
			smSuppBiz.setSmSupplier(smSupp);
		}
		
		/***
		 * Professional Cert 
		 */
		if((smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT) && smAppl.getSupplierType() != null)){  
//				|| 
//				(smAppl.getSupplierType().equals(SupplierManagementConst.INDIVIDUAL_FLAG) && 
//				smSupp.getServiceCategory().equals(SupplierManagementConst.PROF_SERVICE) && 
//				smSupp.getServiceCategory() != null)){
			log.info("*****IS PROF********");
			for(SmSupplierCert suppCert : smAppl.getSmSupplierCerts()){
				log.info("**Cert Body****" + suppCert.getPmCertBody().getCertBodyId());
				suppCert.setSmAppl(smAppl);
				suppCert.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
			}
		}else{
			log.info("*****NOT PROF********");
			smAppl.setSmSupplierCerts(null);
		}

		/*
		 * Add all related objects to Parent SmSupplier
		 */
		smSupp.getSmAppls().add(smAppl);
		smSupp.setPmStatus(new PmStatus());
		smSupp.getPmStatus().setStatusId(20101L);

		smSuppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
		smSupp.getSmSupplierOwners().add(smSuppOwner);

		/* Create Basic Account */
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		
		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);

		if (resDTO.isSuccessful()) {
			System.out.println("****User created Successfully*******");
			//SmPersonnel resSmPersonnel = resDTO.getSmSupp().getSmAppls().get(0).getSmPersonnels().get(0);
			
			//this.sendActivationEmail(resSmPersonnel);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_721012, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			this.getManagedBean().setSmAppl(null);
			this.getManagedBean().setSmSupp(null);
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			log.info("-------------------Error SubmitRegistration----------------");
			throw new EpException(MessageConst.ERROR_721006);
		}
//		return null;
	}

	public String updateOnlineAccount() {
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);
		}

		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}
		
		if(!validateBankInfo()){
			return null;
		}

		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmPermenantAdd());
		}else{
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmBusinessAdd());
		}

		for(SmSupplierBusiness ssb : smSupp.getSmSupplierBusinesses()){
			ssb.setSmSupplier(smSupp);
		}

		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			throw new EpException(MessageConst.ERROR_721006);
		}
	}

	public void addSupplierBank() {

		if (this.getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			this.getManagedBean().getSmAppl().setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}		
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		smSuppBank.setSmAppl(this.getManagedBean().getSmAppl());
		smSuppBank.setAccountPurpose(defaultAccPurpose);
		this.getManagedBean().getSmAppl().getSmSupplierBanks().add(smSuppBank);
		// this.getManagedBean().getSmSuppBanks().add(new SmSupplierBank());
	}

	public void removeSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(this.getManagedBean().getSelectedSuppBanks());
		log.info("---removeSupplierBank---");
		if(this.getManagedBean().getSelectedSuppBanks() != null && this.getManagedBean().getSelectedSuppBanks().length > 0){
			log.info("---removeSupplierBank 1---");
			for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
				this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(suppBank);
				log.info("---removeSupplierBank---" + suppBank);
				// suppBank.getPmFinancialOrg().getFinancialOrgId()
			}
		}
	}
	
	public void addBusinessNature() {
		this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());	
		this.getManagedBean().getSelectedBusinessNature().setPmBusinessNature(new PmBusinessNature());
		
		resetBusinessNatureSelection();
		//RequestContext.getCurrentInstance().update(":form:bizDialog");
		
		log.info("---------addBusinessNature Lvl 1 ------" +this.getManagedBean().getSelectedBusinessNature().getLvl1());
	}
	
	public void addSupplierBusinessNature(){
		log.info("----------addSupplierBusinessNature--------------" +this.getManagedBean().getSelectedBusinessNature().getLvl5());
			
		Long bizNatId  = this.getManagedBean().getSelectedBusinessNature().getLvl5();
		
		if(validateNatureBusiness(bizNatId)){
			PmBusinessNature bizNatObj = retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
			if(bizNatObj != null){
				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bizNatObj);
				this.getManagedBean().getSmSupp().getSmSupplierBusinesses().add(suppBusiness);
				RequestContext.getCurrentInstance().execute("bizDlg.hide()");
			}else{
				//throw new EpException();
			}
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
					MessageUtils.getMessage(MessageConst.ERROR_721007,MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
		}

	}

	private boolean validateNatureBusiness(Long bizNatureId){
		List<String> bizNatureLs = new ArrayList<String>();
		boolean valid = true;
		
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() > 0){
			String bizNatId = bizNatureId.toString();
			log.info("----validateNatureBusiness - bizNatId----" + bizNatId);
			for(SmSupplierBusiness suppBiz : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()){
				
				bizNatureLs.add(suppBiz.getPmBusinessNature().getBusinessNatureId().toString());
				
				log.info("----SuppBiz----" + suppBiz.getPmBusinessNature().getBusinessNatureId().toString());
				if(bizNatureLs.contains(bizNatId)){
					log.info("----Contain Duplicate Business Nature----");
					valid = false;
					break;
				}
				
			}
			
			log.info("----bizNatureLs----" + bizNatureLs.size());
		}
		
		return valid;
	}
	
	public void removeBusinessNature() {
		log.info("---------------removeBusinessNature count------------" + this.getManagedBean().getSelectedBusinessNatureLs().length);
		for (SmSupplierBusiness suppBusinessNature : this.getManagedBean().getSelectedBusinessNatureLs()) {
			log.info("---------------removeBusinessNature.------------" + suppBusinessNature);
			this.getManagedBean().getSmSupp().getSmSupplierBusinesses().remove(suppBusinessNature);
		}
	}
	
	public void editBusinessNature(SmSupplierBusiness suppBusinessNat){
		//TODO:resetBusinessNatureSelection();
		log.info("-----editBusinessNature-----" + suppBusinessNat.getPmBusinessNature().getBusinessNatureId());
		log.info("-----editBusinessNature-----" + suppBusinessNat.getPmBusinessNature().getLevelName());
		log.info("********edit Parent ID*********"  + suppBusinessNat.getPmBusinessNature().getParentBusinessNature().getBusinessNatureId());
		
		//SmSupplierBusiness selectedBiz = this.getManagedBean().getSelectedBusinessNature();
		//PmBusinessNature pmBizNat = selectedBiz.getPmBusinessNature();
		
		SmSupplierBusiness selectedBiz = suppBusinessNat;
		PmBusinessNature pmBizNat = selectedBiz.getPmBusinessNature();
		
		//Get Parent Obj of L5(lv 4) value and List of Level 5
		PmBusinessNature pmBizObjL4 = pmBizNat.getParentBusinessNature();
		PmBusinessNature pmBizObjL3 = pmBizObjL4.getParentBusinessNature();
		PmBusinessNature pmBizObjL2 = pmBizObjL3.getParentBusinessNature();
		PmBusinessNature pmBizObjL1 = pmBizObjL2.getParentBusinessNature();
		
		this.getManagedBean().setSelectedBusinessNature(selectedBiz);
		this.getManagedBean().getSelectedBusinessNature().setLvl5(selectedBiz.getPmBusinessNature().getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl4(pmBizObjL4.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl3(pmBizObjL3.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl2(pmBizObjL2.getBusinessNatureId());
		this.getManagedBean().getSelectedBusinessNature().setLvl1(pmBizObjL1.getBusinessNatureId());
		
		List<PmBusinessNature> bizNatureLs5 = retrieveBusinessNatureByLevelParent("50",pmBizNat.getParentBusinessNature().getBusinessNatureId());
//		List<PmBusinessNature> bizNatureLs4 = retrieveBusinessNatureByLevelParent("40",selectedBiz.getLvl3());
//		List<PmBusinessNature> bizNatureLs3 = retrieveBusinessNatureByLevelParent("30",selectedBiz.getLvl2());
//		List<PmBusinessNature> bizNatureLs2 = retrieveBusinessNatureByLevelParent("20",selectedBiz.getLvl1());
		
		List<PmBusinessNature> bizNatureLs4 = retrieveBusinessNatureByLevelParent("40",pmBizObjL3.getBusinessNatureId());
		List<PmBusinessNature> bizNatureLs3 = retrieveBusinessNatureByLevelParent("30",pmBizObjL2.getBusinessNatureId());
		List<PmBusinessNature> bizNatureLs2 = retrieveBusinessNatureByLevelParent("20",pmBizObjL1.getBusinessNatureId());
		
		this.getManagedBean().setBusinessNatureL5(bizNatureLs5);
		this.getManagedBean().setBusinessNatureL4(bizNatureLs4);
		this.getManagedBean().setBusinessNatureL3(bizNatureLs3);
		this.getManagedBean().setBusinessNatureL2(bizNatureLs2);
		
		//List<PmBusinessNature> bizNatureLs1 = retrieveBusinessNatureByLevelParent("20",pmBizNat.getParentBusinessNature().getBusinessNatureId());
		log.info("********LV 5 list*********" + bizNatureLs5.size());

	}
	
	
	public void updateBusinessNature(){
		log.info("---------updateBusinessNature-------------" + this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getBusinessNatureId());

		Long bizNatId = this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getBusinessNatureId();
		
		if(bizNatId !=null){
			if(bizNatId.longValue() == this.getManagedBean().getSelectedBusinessNature().getLvl5().longValue()){
				RequestContext.getCurrentInstance().execute("bizDlg.hide()");
			}else{
				if(validateNatureBusiness(bizNatId)){
					PmBusinessNature bizNatObj = retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
					if(bizNatObj != null){
						this.getManagedBean().getSelectedBusinessNature().setPmBusinessNature(bizNatObj);
						RequestContext.getCurrentInstance().execute("bizDlg.hide()");
					}
				}else{
					//show error msg on dialog
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
							MessageUtils.getMessage(MessageConst.ERROR_721007,MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));
				}
			}
		}
	}
	
/*	*//**
	 * business nature change listener 
	 * @param event
	 *//*
	public void bizNatChangeListener(ValueChangeEvent event){
		log.info("---------bizNatChangeListener---------0");
		Object oldValueObj = event.getOldValue();
	    Long oldValue = oldValueObj!=null ? (Long)oldValueObj : new Long(0);
		if(oldValue!=0L){
			//hold the old value for validation purpose
			this.getManagedBean().getSuppBusinessNature().getPmBusinessNature().setTempBusinessNatureId(oldValue);
		}
	}*/
	
	public void businessLvlOnChange(String bizLvl){
		SmSupplierBusiness selectedBiz = this.getManagedBean().getSelectedBusinessNature();

		switch (bizLvl) {
			case "20" : 
				this.getManagedBean().setBusinessNatureL2(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
//				this.getManagedBean().setBizIdL2(null);
//				this.getManagedBean().setBizIdL3(null);
//				this.getManagedBean().setBizIdL4(null);
				selectedBiz.setLvl2(null);
				selectedBiz.setLvl3(null);
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				//this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().setBusinessNatureId(null);
				if(selectedBiz.getLvl1() != null){
					this.getManagedBean().setBusinessNatureL2(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl1()));
				}
			break;
			case "30" :
				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl3(null);
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				//this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().setBusinessNatureId(null);
				if(selectedBiz.getLvl2() !=null){
					this.getManagedBean().setBusinessNatureL3(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl2()));
				}
			break;
			case "40" : 
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl4(null);
				selectedBiz.setLvl5(null);
				//this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().setBusinessNatureId(null);
				if(selectedBiz.getLvl3()!=null){
					this.getManagedBean().setBusinessNatureL4(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl3()));
				}
			break;
			case "50" : 
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
				selectedBiz.setLvl5(null);
				//this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().setBusinessNatureId(null);
				if(selectedBiz.getLvl4()!=null){
					this.getManagedBean().setBusinessNatureL5(retrieveBusinessNatureByLevelParent(bizLvl,selectedBiz.getLvl4()));
				}
			break;
		}
	}

	public void businessTypeOnChange() {
		if (this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)) {
			log.info("--------------Business Type is Individual------------");
			// this.populateStateByAddType("business");
		} else {
			log.info("-----------Business Type is not Individual------------"
					+ this.getManagedBean().getSmSupp().getBusinessType());
		}
	}



	private void retrievePmCountryList() {
		ICountrySO reqSO = (ICountrySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		this.getManagedBean().setCountryLs(resDTO.getCountryList());
	}

	private List<PmState> retrievePmStateList(long countryId) {
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		return resDTO.getStateList();
	}

	private List<PmDistrict> retrievePmDistrictList(long stateId) {
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setStateId(stateId);
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IDistrictSO.class);
		DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
		return resDTO.getDistrictList();
	}

	private List<PmCity> retrievePmCityList(long districtId) {
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setDistrictId(districtId);
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICitySO.class);
		CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
		return resDTO.getCityList();
	}

	private void retrieveFinancialOrgList() {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType("PB");
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO
				.retrieveFinancialOrgListByServiceType(reqDTO);
		this.getManagedBean().setFinancialOrgLs(resDTO.getFinancialOrgList());
	}
	

	private void retrieveCertBody() {
		// PmCertBodyReqDTO = new PmCertBodyReqDTO();
		log.info("*********retrieveCertBody************");

		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();

		log.info("*****certbody count" + resDTO.getCertBodyList().size());
		this.getManagedBean().setProfCertBodyLs(new ArrayList<PmCertBody>());
		this.getManagedBean().setProfCertBodyLs(resDTO.getCertBodyList());
	}

	
	private List<PmBusinessNature> retrieveBusinessNatureByLevelParent(String busLvl, long parentId){
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		if(busLvl != "" || busLvl != null){
			reqDTO.setBusinessLevel(busLvl);
		}
		
		if(busLvl != "10"){
			reqDTO.setParentBusinessNatureId(parentId);
		}
		
		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureListByLevel(reqDTO);

		return resDTO.getBusinessNatureList();
	}
	
	private boolean checkExistingBusinessRegNo(){
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		IRegistrationSO reqSO = BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		reqDTO.setRegNo(this.getManagedBean().getSmSupp().getRegNo());
		RegistrationResDTO resDTO = reqSO.findSupplierByRegNo(reqDTO);
		
		if(resDTO.getSmSupp() == null){
			return false;
		}else{
			return true;
		}
	}
	
	public void getIndividualServiceType(){
//		System.out.println("******getIndividualServiceType******");
//		if(this.getManagedBean().getSmSupp().getServiceCategory() != null){
//			String serviceTypeParam = null;
//			System.out.println("******getIndividualServiceType******" + this.getManagedBean().getSmSupp().getServiceCategory());
//			if(this.getManagedBean().getSmSupp().getServiceCategory().equals(SupplierManagementConst.PROF_SERVICE)){
//				serviceTypeParam = SupplierManagementConst.PROF_SERVICE_CODE;
//			}else if(this.getManagedBean().getSmSupp().getServiceCategory().equals(SupplierManagementConst.SPECIAL_SERVICE)){
//				serviceTypeParam = SupplierManagementConst.SPECIAL_SERVICE_CODE;
//			}
//
//			if(serviceTypeParam != null){
//				List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(serviceTypeParam, new FacesScopes().getUserLocale().getLanguage());
//				System.out.println("******ServiceType******" + paramDesc.size() );
//				this.getManagedBean().setServiceLs(paramDesc);
//			}
//		}
		
		List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SPECIAL_SERVICE_CODE, new FacesScopes().getUserLocale().getLanguage());
		this.getManagedBean().setServiceLs(paramDesc);
	}
	
	public void populateStateByAddType(String typeAdd) {
		if (typeAdd.equals("business")) {
			List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			this.getManagedBean().setBusinessStateLs(stateLs);
		} else if (typeAdd.equals("residential")) {
			List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		} else if (typeAdd.equals("permenant")) {
			List<PmState> stateLs = this.retrievePmStateList(this
					.getManagedBean().getSmPermenantAdd().getPmCountry()
					.getCountryId());
			this.getManagedBean().setPermenantStateLs(stateLs);
		}
	}

	public void populateDistrictByAddType(String typeAdd) {
		if (typeAdd.equals("business")) {
			// this.getManagedBean().getSmBusinessAdd().getPmState().getStateId();
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
			this.getManagedBean().setBusinessDistrictLs(districtLs);
			this.getManagedBean().setBusinessCityLs(new ArrayList<PmCity>());
		} else if (typeAdd.equals("residential")) {
			log.info("------------residential----------------");
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this.getManagedBean().getSmResAdd().getPmState().getStateId());
			this.getManagedBean().setResidentialDistrictLs(districtLs);
			this.getManagedBean().setResidentialCityLs(new ArrayList<PmCity>());
		} else if (typeAdd.equals("permenant")) {
			log.info("------------permenant----------------"
					+ this.getManagedBean().getSmPermenantAdd().getPmState()
					.getStateId());
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
			this.getManagedBean().setPermenantDistrictLs(districtLs);
			this.getManagedBean().setPermenantCityLs(new ArrayList<PmCity>());

			log.info("------permenant LS-------------"
					+ this.getManagedBean().getPermenantDistrictLs().size());
		}
	}

	public void populateCityByAddType(String typeAdd) {
		log.info("---------------City By Type--------------------");
		if (typeAdd.equals("business")) {
			// this.getManagedBean().getSmBusinessAdd().getPmState().getStateId();
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setBusinessCityLs(cityLs);
		} else if (typeAdd.equals("residential")) {
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean().getSmResAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setResidentialCityLs(cityLs);
		} else if (typeAdd.equals("permenant")) {
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setPermenantCityLs(cityLs);
			log.info("------permenant City LS-------------"
					+ this.getManagedBean().getPermenantCityLs().size());
		}
	}

	public void viewOnlineAccount() {
		log.info("--------------------viewOnlineAccount----------------" + MDC.get(LogKey.USER_ID));
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if(pmUser.getUserId().longValue() != 1 || pmUser == null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			//846846516516
			//PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			SmSupplier smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0); 
			log.info("-------smResDTO-------" + smSupp.getSupplierId()); 

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSupplierId(smSupp.getSupplierId());

			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

			this.getManagedBean().setSmSupp(resDTO.getSmSupp());
			this.getManagedBean().setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
			this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners().get(0));

			//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());	
			//this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(new PmBusinessNature());

			log.info("---------Supplier Type-----------"
					+ this.getManagedBean().getSmAppl().getSupplierType());

			//		log.info("**********************BUSINESS_TYPE_LS*****************" + this.getManagedBean().getBusinessTypeLs().size());
			if(resDTO.getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
				getIndividualServiceType();
				this.getManagedBean().setSmPermenantAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
				this.populateStateByAddType("permenant");
				this.populateDistrictByAddType("permenant");
				this.populateCityByAddType("permenant");

				this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
				this.populateStateByAddType("business");
				this.populateDistrictByAddType("business");
				this.populateCityByAddType("business");
			}else{
				this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
				this.populateStateByAddType("business");
				this.populateDistrictByAddType("business");
				this.populateCityByAddType("business");
			}
			this.retrievePmCountryList();
			this.retrieveFinancialOrgList();
			//this.retrieveBusinessNatureL5Map();
			retrieveIdentityTypeMap();
			this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceDet.xhtml");
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					null,
					MessageUtils.getMessage(MessageConst.ERROR_721017, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlinereg/outputMsg.xhtml");

		}
	}
	
	public SelectItem[] pmBusinessTypeDropDown(){
		System.out.println("*********getPmBusinessTypeDropDown**********");
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		try {
			SelectItem[] items = getDropdownCodeValues("SBT");
			for(SelectItem selectItem : items) {
				if(!this.getManagedBean().getSmSupp().isBrandNew()){
					itemList.add(selectItem);
				}else{
					if(this.getManagedBean().getApplType().equals(SupplierManagementConst.ACC_BASIC)){
						itemList.add(selectItem);
					}else if(this.getManagedBean().getApplType().equals(SupplierManagementConst.ACC_MOF) && 
							this.getManagedBean().getSmAppl().getSupplierType() != null){
						if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONTRACTOR)){
							//System.out.println("****IS CONTRACTOR*****");

							if(selectItem.getValue() == null || !selectItem.getValue().equals("A")){
								//System.out.println("****IS not NULL or A*****" + selectItem.getValue());
								itemList.add(selectItem);
							}
						}else if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
							//System.out.println("****IS CONSULTANT*************"); 
							if(selectItem.getValue() == null || (!selectItem.getValue().equals("K") 
									&& !selectItem.getValue().equals("L") && !selectItem.getValue().equals("M"))){ 
								itemList.add(selectItem);
							}
						}
					}else if(this.getManagedBean().getSmAppl().getSupplierType() == null && this.getManagedBean().getApplType().equals(SupplierManagementConst.ACC_MOF)){
						if(selectItem.getValue() == null){	
							itemList.add(selectItem);
						}
					}
				}
			}
			
			System.out.println("------------->" + itemList.size());
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_721010, new String[]{e.toString(), e.getMessage()});


		}
		
		return itemList.toArray(new SelectItem[itemList.size()]);
	}
	
	public void retrieveBusinessNatureL5Map(){
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessLevel("50");
		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureListByLevel(reqDTO);
		
		bizNatureL5Map = new HashMap<Long,Object>();
		for(PmBusinessNature bizNat : resDTO.getBusinessNatureList()){
			bizNatureL5Map.put(bizNat.getBusinessNatureId(), bizNat);
			System.out.println("*****MAP Result******" + bizNat.getBusinessNatureId());
		}
		
		System.out.println("************MAP Result***************" + bizNatureL5Map.size());
	}
	
	public PmBusinessNature retrieveBusinessNatureById(Long bid){
		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
		reqDTO.setBusinessNatureId(bid);
		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureById(reqDTO);

		return resDTO.getBusinessNature();
	}

	public void resetBusinessNatureSelection(){
		this.getManagedBean().getSelectedBusinessNature().setLvl1(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl2(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl3(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl4(null);
		this.getManagedBean().getSelectedBusinessNature().setLvl5(null);
		
		//this.getManagedBean().setBusinessNatureL1(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL2(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
	}
	
	public boolean isExistEmailInPmUser(String email){
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(email);
		
		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);
		
		System.out.println("*********resDTO.getUserList().size()*****" + resDTO.getUserList().size());
		if(resDTO.getUserList().size() > 0){
			return true;
		}else{
			return false;
		}
	}

	public boolean isExistUserByIdentificationNo(String idNo) {
		System.out.println("*********retrieveUserByIdentificationNo**********" + idNo);
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(idNo);
		UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
		
		if(resDTO.getPmUser() != null){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean validateBankInfo(){
		List<String> bankList = new ArrayList<String>();
		for(SmSupplierBank suppBank : this.getManagedBean().getSmAppl().getSmSupplierBanks()){
			String uniqBank = suppBank.getPmFinancialOrg().getFinancialOrgId().toString() + suppBank.getAccountNo();
			System.out.println("*********suppBank.getPmFinancialOrg().getFinancialOrgId()****" + suppBank.getPmFinancialOrg().getFinancialOrgId());
			if(suppBank.getPmFinancialOrg().getFinancialOrgId() == null){
				throw new EpException(MessageConst.ERROR_721008);
			}
			
			if(suppBank.getAccountNo() == null || suppBank.getAccountNo() == ""){
				throw new EpException(MessageConst.ERROR_721009);
			}
			
			if(bankList.contains(uniqBank)){
				throw new EpException(MessageConst.ERROR_721012);
			}
			
			bankList.add(uniqBank);
		}
		
		return true;
	}

/*	public void resetSessionData() {
		this.getManagedBean().setApplType(SupplierManagementConst.ACC_MOF);
//		this.getManagedBean().setAgreeTC(false);
		this.getManagedBean().setSmSupp(null);
		this.getManagedBean().setSmAppl(null);
		this.getManagedBean().setConfirmEmail(null);
		resetBusinessNatureSelection();
		System.out.println("renewSesionObject");
//		renewSesionObject();
	}*/

	public void resetSessionData() {
		log.debug("renewing session object..");

		renewSesionObject();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "signUp");

	}
	
	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID);
	}
	
//	public void verifyIC(){
//		log.info("--------verifyIC-----------");
//		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
//		reqDTO.setNewIcNo("841219148899");
//		IMyIdentitySO reqSO = (IMyIdentitySO) BusinessDelegate.getInstance().getServiceObject("sm", IMyIdentitySO.class);
//		MyIdentityResDTO resDTO = reqSO.myIdentityVerification(reqDTO);
//		
//		log.info("-----Response NewIC---------" + resDTO.getICNumber());
//		log.info("-----Response Name ---------" + resDTO.getName());
//		log.info("-----Response PostCode---------" + resDTO.getPermanentAddrPostCode());
//		
//	}
	
	/**
	 * retrieve identity type from PM
	 * @return SelectItem[]
	 */
	public SelectItem[] pmIdTypeDropdownValues() {
		return getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
	}
	
	/**
	 * set supplier owner mask based on selected id type
	 */
	public void suppOwnerIdTypeChange(){
		PmParameter pmParameter = (PmParameter) identityType.get(this.getManagedBean().getSmSuppOwner().getIdentificationTypeId());
		this.getManagedBean().getSmSuppOwner().setIdentificationType(pmParameter);
//		if (ParameterConst.IDENTIFICATION_TYPES_IC.equals(this.getManagedBean().getSmSuppOwner().getIdentificationType().getParameterCode())) {
//			this.getManagedBean().setSuppOwnerIdMask(newICMasking);
//		} else {
//			this.getManagedBean().setSuppOwnerIdMask("");
//		}
	}

	/**
	 * set personnel id mask based on selected id type
	 */
	public void personnelIdTypeChange(){
		PmParameter pmParameter = (PmParameter) identityType.get(this.getManagedBean().getSmPersonnel().getIdentificationTypeId());
		this.getManagedBean().getSmPersonnel().setIdentificationType(pmParameter);
//		if (ParameterConst.IDENTIFICATION_TYPES_IC.equals(this.getManagedBean().getSmPersonnel().getIdentificationType().getParameterCode())) {
//			this.getManagedBean().setPersonnelIdMask(newICMasking);
//		} else {
//			this.getManagedBean().setPersonnelIdMask("");
//		}
	}
	
	/**
	 * retrieve all identity type into mapper
	 */
	public void retrieveIdentityTypeMap(){
		List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		identityType = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			identityType.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * update correspondence address based on "same address" value
	 */
	public void updateCorrespondenceAddress(){
		log.info("----updateCorrespondenceAddress----");
		if(this.getManagedBean().isSameAddress()){
			SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
			this.getManagedBean().setSmResAdd(smPermAdd);
			this.getManagedBean().getSmResAdd().getPmState().setStateId(smPermAdd.getPmState().getStateId());
			populateDistrictByAddType("residential");
			this.getManagedBean().getSmResAdd().getPmDistrict().setDistrictId(smPermAdd.getPmDistrict().getDistrictId());
			populateCityByAddType("residential");
			this.getManagedBean().getSmResAdd().getPmCity().setCityId(smPermAdd.getPmCity().getCityId());
		}else{
			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
		}
	}
	
	private void validateIDFormat(String identNo, String identType){
		if(identType.equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
			if(identNo.length() != 12){
				throw new EpException(MessageConst.ERROR_721013);
			}

			if(!identNo.matches("[0-9]+")){
				throw new EpException(MessageConst.ERROR_721013);
			}	
		}
	}

	/*public void verifyWithMyIdentity(String identificationNo, String postCode){
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		IMyIdentitySO reqSO = BusinessDelegate.getInstance().getServiceObject("sm", IMyIdentitySO.class);
		
		reqDTO.setType("Online");
		reqDTO.setNewIcNo(identificationNo);
		reqDTO.setPostCode(postCode);
		
		MyIdentityResDTO resDTO = reqSO.myIdentityVerification(reqDTO);
		System.out.println("verifyWithMyIdentity>>>>" + resDTO.getSeverity() + ">>code>>" + resDTO.getStatusCode());
		if(!resDTO.getSeverity().equalsIgnoreCase(SupplierManagementConst.SM_MYIDENTITY_SEVERITY_SUCC)){
			switch (resDTO.getStatusCode()) {
			case "70019" :
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70019);
			case "70020" :
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70020);
			case "70031" :
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70031);
			case "70032" :
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70032);
			}
		}
	}*/

}


