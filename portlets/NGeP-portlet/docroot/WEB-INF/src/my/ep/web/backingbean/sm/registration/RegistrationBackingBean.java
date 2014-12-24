package my.ep.web.backingbean.sm.registration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
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
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
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
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBusiness;
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
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringUtil;
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
	
	private static String defaultAccPurpose = "P";
	private static Map<Long, Object> bizNatureL5Map;
	private static Map<Long, Object> identityType;
	private static Map<Long, PmCertBody> profCertBodyMap;

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
			log.info("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	public String proceedToCompanyInfo() {
		log.info("---------proceedToCompanyInfo-------" + new Date());
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
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
			
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
		
		validateBankInfo();

		//check the professional body if supplier type is consultant
		if(this.getManagedBean().getSmAppl().getSupplierType() != null){
			if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
				if(this.getManagedBean().getSmAppl().getSmAssociationBodies().size() < 1){
					throw new EpException(MessageConst.ERROR_721033);
				}else{
					checkDuplicatedProfessionalBody();
				}
			}
		}

		SmSupplierOwner suppOwner = this.getManagedBean().getSmSuppOwner();
		log.info("-----------Owner ID Type---------" + suppOwner.getIdentificationType().getParameterCode());
		log.info("-----------Owner ID---------" + suppOwner.getIdentificationNo());
		
		this.validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
		
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
	
	public void addProfessionalBody(){
		log.info("-------------------addProfessionalBody-----------");
		if(this.getManagedBean().getSmAppl().getSmAssociationBodies() == null){
			this.getManagedBean().getSmAppl().setSmAssociationBodies(new ArrayList<SmAssociationBody>());
			this.retrieveProfBody();
		}
		SmAssociationBody assocBody = new SmAssociationBody();
		assocBody.setPmCertBody(new PmCertBody());
		this.getManagedBean().getSmAppl().getSmAssociationBodies().add(assocBody);
	}
	
	public void removeProfessionalBody(){
		log.info("------------------removeProfessionalBodyProfessionalBody-----------");
		if(this.getManagedBean().getSelectedAssociationBodies() != null && this.getManagedBean().getSelectedAssociationBodies().length > 0){
			for (SmAssociationBody suppAssocBody : this.getManagedBean().getSelectedAssociationBodies()) {
				//this.getManagedBean().getSmSuppCerts().remove(suppCert);
				this.getManagedBean().getSmAppl().getSmAssociationBodies().remove(suppAssocBody);
				log.info("---removeSupplierCert---" + suppAssocBody);
				// suppBank.getPmFinancialOrg().getFinancialOrgId()
			}
		}
	}

	public String submitRegistration() {
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
		SmAddress smResAdd = this.getManagedBean().getSmResAdd();
		
		if(!smPersonnel.getEmail().equals(this.getManagedBean().getConfirmEmail())){
			throw new EpException(MessageConst.ERROR_721020);
		}
		
		if(!smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
		}else{
			IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
			PersonnelReqDTO personneleqDTO = new PersonnelReqDTO();
			
			personneleqDTO.setIdentificationNo(this.getManagedBean().getSmSupp().getRegNo());
			PersonnelResDTO personnelResDTO =  personnelSO.retrieveIsEquityOrIsDirectorByIdentNo(personneleqDTO);
			
			if(personnelResDTO.getSmPersonnelList().size() > 0){
				throw new EpException(MessageConst.ERROR_721026);
			}
		}
		
		if(smAppl.getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}
		
		validateBankInfo();

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
		this.validateIDFormat(smPersonnel.getIdentificationNo(), personnelIDType.getParameterCode());
		
		//validate IC with MyIdentity
		if(personnelIDType.getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
			this.verifyWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(), this.getManagedBean().getSmPermenantAdd().getPostcode());
		}
		
		if(isExistEmailInPmUser(this.getManagedBean().getSmPersonnel().getEmail())){
			log.info("In Pm User....");
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
		
		//check Exist email in smPersonnel
		this.checkExistEmailInPersonnel(smPersonnel.getEmail());
		
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
		//smPersonnel.setBumiFlg(SupplierManagementConst.REG_BUMI);
		smPersonnel.setContractSignerFlg(SupplierManagementConst.FIRST_USER_CONT_SIGNER);
		smPersonnel.setEpRole(RoleCode.BASIC_SUPPLIER_ADMIN.value());
		smPersonnel.setClerical(true);

		SmPersonnelAddress personnelAddRes = new SmPersonnelAddress();
		SmPersonnelAddress personnelAddPerm = new SmPersonnelAddress();
		
		personnelAddRes.setAddressType(SupplierManagementConst.MAIL_ADD);
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
		if(this.getManagedBean().getSmAppl().getSupplierType() != null && this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
			if((smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT) && smAppl.getSupplierType() != null)){  
				//				|| 
				//				(smAppl.getSupplierType().equals(SupplierManagementConst.INDIVIDUAL_FLAG) && 
				//				smSupp.getServiceCategory().equals(SupplierManagementConst.PROF_SERVICE) && 
				//				smSupp.getServiceCategory() != null)){
				log.info("*****IS PROF********");
				for(SmAssociationBody suppAssocBody : smAppl.getSmAssociationBodies()){
					suppAssocBody.setSmAppl(smAppl);
					suppAssocBody.setRevNo(SupplierManagementConst.DEFAULT_REV_NO);
				}
			}else{
				log.info("*****NOT PROF********");
				smAppl.setSmAssociationBodies(null);
			}
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
			log.info("****User created Successfully*******");
			//SmPersonnel resSmPersonnel = resDTO.getSmSupp().getSmAppls().get(0).getSmPersonnels().get(0);
			
			//this.sendActivationEmail(resSmPersonnel);
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_721012, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			renewSesionObject();
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

		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmPermenantAdd());
		}else{
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmBusinessAdd());
		}

		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		
		try{
			System.out.println(111111111);
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IRegistrationSO.class);
			System.out.println(222222222);
			RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
			System.out.println(333333333);
			if (resDTO.isSuccessful()) {
				System.out.println(444444444);
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				System.out.println(555555555);
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				System.out.println(6666666);
				return "/xhtml/sm/onlinereg/outputMsg";
			} else {
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721006);
			}
		} catch(Exception ex){
			throw new EpException(ex.getMessage());
		}finally{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
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
			PmBusinessNature bizNatObj = SmCommonUtil.retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
			if(bizNatObj != null){
				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bizNatObj);
				suppBusiness.setSmSupplier(this.getManagedBean().getSmSupp());
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
				log.info("----SuppBiz----" + suppBiz.getPmBusinessNature().getBusinessNatureId().toString());
				bizNatureLs.add(suppBiz.getPmBusinessNature().getBusinessNatureId().toString());
			}
			
			if(bizNatureLs.contains(bizNatId)){
				log.info("----Contain Duplicate Business Nature----");
				valid = false;
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
		PmBusinessNature pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3.getParentBusinessNature().getBusinessNatureId());
		PmBusinessNature pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2.getParentBusinessNature().getBusinessNatureId());
		
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
				if(validateNatureBusiness(this.getManagedBean().getSelectedBusinessNature().getLvl5().longValue())){
					PmBusinessNature bizNatObj = SmCommonUtil.retrieveBusinessNatureById(this.getManagedBean().getSelectedBusinessNature().getLvl5());
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

	private List<PmState> retrievePmStateList(Long countryId) {
		if(countryId != null){
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(countryId);
			IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IStateSO.class);
			StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
			return resDTO.getStateList();
		}

		return null;
	}

	private List<PmDistrict> retrievePmDistrictList(Long stateId) {
		if(stateId != null){
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			return resDTO.getDistrictList();
		}
		
		return null;
	}

	private List<PmCity> retrievePmCityList(Long districtId) {
		if(districtId != null){
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			return resDTO.getCityList();
		}
		return null;
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
	

	private void retrieveProfBody() {
		// PmCertBodyReqDTO = new PmCertBodyReqDTO();
		log.info("*********retrieveCertBody************");

		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();

		log.info("*****certbody count" + resDTO.getCertBodyList().size());
		this.getManagedBean().setProfCertBodyLs(new ArrayList<PmCertBody>());
		this.getManagedBean().setProfCertBodyLs(resDTO.getCertBodyList());
		
		if(this.getManagedBean().getProfCertBodyLs().size() > 0){
			profCertBodyMap = new HashMap<Long, PmCertBody>();
			for(PmCertBody cb : this.getManagedBean().getProfCertBodyLs()){
				profCertBodyMap.put(cb.getCertBodyId(), cb);
			}
		}
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
		
		if(resDTO.getBusinessNatureList()!=null && CollectionUtils.isNotEmpty(resDTO.getBusinessNatureList())){
			// sort
			Collections.sort(resDTO.getBusinessNatureList(), new Comparator<PmBusinessNature>() {
				@Override
				public int compare(final PmBusinessNature p1, final PmBusinessNature p2) {
					String levelCode1 = p1.getLevelCode()==null?"":p1.getLevelCode();
					String levelCode2 = p2.getLevelCode()==null?"":p2.getLevelCode();
					return StringUtils.upperCase(levelCode1).compareTo(
							StringUtils.upperCase(levelCode2));
				}
			});
		}

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
//		log.info("******getIndividualServiceType******");
//		if(this.getManagedBean().getSmSupp().getServiceCategory() != null){
//			String serviceTypeParam = null;
//			log.info("******getIndividualServiceType******" + this.getManagedBean().getSmSupp().getServiceCategory());
//			if(this.getManagedBean().getSmSupp().getServiceCategory().equals(SupplierManagementConst.PROF_SERVICE)){
//				serviceTypeParam = SupplierManagementConst.PROF_SERVICE_CODE;
//			}else if(this.getManagedBean().getSmSupp().getServiceCategory().equals(SupplierManagementConst.SPECIAL_SERVICE)){
//				serviceTypeParam = SupplierManagementConst.SPECIAL_SERVICE_CODE;
//			}
//
//			if(serviceTypeParam != null){
//				List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(serviceTypeParam, new FacesScopes().getUserLocale().getLanguage());
//				log.info("******ServiceType******" + paramDesc.size() );
//				this.getManagedBean().setServiceLs(paramDesc);
//			}
//		}
		
		List<PmParameterDesc> paramDesc = SmCommonUtil.retrieveParameterDescList(SupplierManagementConst.SPECIAL_SERVICE_CODE, new FacesScopes().getUserLocale().getLanguage());
		this.getManagedBean().setServiceLs(paramDesc);
	}
	
	public void populateStateByAddType(String typeAdd) {
		if (typeAdd.equals("business")) {
			//List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			//this.getManagedBean().setBusinessStateLs(stateLs);
			pmStateDropdownValues();
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
			this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			if(resDTO.getSmSupp().getSmSupplierOwners()!=null && CollectionUtils.isNotEmpty(resDTO.getSmSupp().getSmSupplierOwners())){
				this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners().get(0));
			}

			//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());	
			//this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(new PmBusinessNature());

			log.info("---------Supplier Type-----------" + this.getManagedBean().getSmAppl().getSupplierType());
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
				if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
						this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
					this.getManagedBean().setSabahSarawakType(true);
				}
				
				this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
				this.populateStateByAddType("business");
				this.populateDistrictByAddType("business");
				this.populateCityByAddType("business");
			}
			this.retrievePmCountryList();
			this.retrieveFinancialOrgList();
			
			if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
				this.retrieveProfBody();
			}
			//this.retrieveBusinessNatureL5Map();
			retrieveIdentityTypeMap();
			this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());

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
		log.info("*********getPmBusinessTypeDropDown**********");
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
							//log.info("****IS CONTRACTOR*****");

							if(selectItem.getValue() == null || !selectItem.getValue().equals("A")){
								//log.info("****IS not NULL or A*****" + selectItem.getValue());
								itemList.add(selectItem);
							}
						}else if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
							//log.info("****IS CONSULTANT*************"); 
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
			
			// sort
			if(CollectionUtils.isNotEmpty(itemList)){
				Collections.sort(itemList, new Comparator<SelectItem>() {
					@Override
					public int compare(final SelectItem s1, final SelectItem s2) {
						return StringUtil.upperCase(s1.getLabel()).compareTo(
								StringUtil.upperCase(s2.getLabel()));
					}
				});
			}
			
			
			log.info("------------->" + itemList.size());
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
			log.info("*****MAP Result******" + bizNat.getBusinessNatureId());
		}
		
		log.info("************MAP Result***************" + bizNatureL5Map.size());
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
		
		log.info("*********resDTO.getUserList().size()*****" + resDTO.getUserList().size());
		if(resDTO.getUserList().size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public void checkExistEmailInPersonnel(String email){
		IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm", IPersonnelSO.class);
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setEmail(email);
		log.info("******checkExistEmailInPersonnel STart********" + new Date());
		PersonnelResDTO resDTO = personnelSO.retrievePersonnelByEmail(reqDTO);
		log.info("******checkExistEmailInPersonnel end********" + new Date());
		if(resDTO.getSmPersonnelList().size() > 0){
			SmPersonnel personnel = resDTO.getSmPersonnelList().get(0);
			log.info("---personnel email---" + personnel.getEmail());
			log.info("---personnel created---" + personnel.getCreatedDate());
			if(personnel.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS){
				long applDateInMilis = personnel.getCreatedDate().getTime();
				long dayInMilis = 1000 * 60 * 60 * 24;
				Date expiryDate = new Date(applDateInMilis + (7 * dayInMilis));

				if(new Date().before(expiryDate)){
					throw new EpException(MessageConst.ERROR_721019);
				}
			}else{
				throw new EpException(MessageConst.ERROR_721004);
			}
		}
	}

	public boolean isExistUserByIdentificationNo(String idNo) {
		log.info("*********retrieveUserByIdentificationNo**********" + idNo);
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
	
	public void validateBankInfo(){
		List<String> bankList = new ArrayList<String>();
		for(SmSupplierBank suppBank : this.getManagedBean().getSmAppl().getSmSupplierBanks()){
			log.info("*********suppBank.getPmFinancialOrg().getFinancialOrgId()****" + suppBank.getPmFinancialOrg().getFinancialOrgId());
			if(suppBank.getPmFinancialOrg().getFinancialOrgId() == null){
				throw new EpException(MessageConst.ERROR_721008);
			}

			if(suppBank.getAccountNo() == null || suppBank.getAccountNo() == ""){
				throw new EpException(MessageConst.ERROR_721009);
			}

			String uniqBank = suppBank.getPmFinancialOrg().getFinancialOrgId().toString() + suppBank.getAccountNo();
			if(bankList.contains(uniqBank)){
				throw new EpException(MessageConst.ERROR_721012);
			}
			bankList.add(uniqBank);
		}
	}

/*	public void resetSessionData() {
		this.getManagedBean().setApplType(SupplierManagementConst.ACC_MOF);
//		this.getManagedBean().setAgreeTC(false);
		this.getManagedBean().setSmSupp(null);
		this.getManagedBean().setSmAppl(null);
		this.getManagedBean().setConfirmEmail(null);
		resetBusinessNatureSelection();
		log.info("renewSesionObject");
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
		List<SelectItem> itemList = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownValues(ParameterTypeConst.IDENTIFICATION_TYPES);
		
		for(SelectItem selectItem : items) {
			
			if(selectItem.getValue() == null){
				itemList.add(selectItem);
			}else{
				PmParameter pmParameter = (PmParameter) identityType.get(selectItem.getValue());
				if(!pmParameter.getParameterCode().equals("A")){
					itemList.add(selectItem);
				}
			}
			
		}
		
		return itemList.toArray(new SelectItem[itemList.size()]);
		
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
	
	public void trimOwnerPassportValue(){
		String newPassportVal = "";
		if(StringUtils.isNotBlank(this.getManagedBean().getSmSuppOwner().getIdentificationNo())){
			newPassportVal = this.getManagedBean().getSmSuppOwner().getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSmSuppOwner().setIdentificationNo(newPassportVal);
		}
	}
	
	public void trimPersonnelPassportValue(){
		String newPassportVal = "";
		if(StringUtils.isNotBlank(this.getManagedBean().getSmPersonnel().getIdentificationNo())){
			newPassportVal = this.getManagedBean().getSmPersonnel().getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSmPersonnel().setIdentificationNo(newPassportVal);
		}
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

	public void verifyWithMyIdentity(String name, String identificationNo, String postCode){
		MyIdentityReqDTO reqDTO = new MyIdentityReqDTO();
		IMyIdentitySO reqSO = BusinessDelegate.getInstance().getServiceObject("sm", IMyIdentitySO.class);
		
		reqDTO.setType("Online");
		reqDTO.setNewIcNo(identificationNo);
		reqDTO.setPostCode(postCode);
		reqDTO.setName(name);
		
		MyIdentityResDTO resDTO = reqSO.myIdentityVerification(reqDTO);
		log.info("verifyWithMyIdentity>>>>" + resDTO.getSeverity() + ">>code>>" + resDTO.getStatusCode());
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
			default : 
				log.info("MyIDENTITY ERROR: " + identificationNo + " - " + resDTO.getStatusCode() + " - " + resDTO.getStatusDesc());
				throw new EpException(MessageConst.ERROR_MYIDENTITY_70099);
			}
		}
	}
	
	public boolean checkDuplicatedProfessionalBody(){
		List<String> assocBodyList = new ArrayList<String>();
		for(SmAssociationBody assocBody : this.getManagedBean().getSmAppl().getSmAssociationBodies()){
			log.info("*********assocBody.getPmCertBody.getCertBodyId****" + assocBody.getPmCertBody().getCertBodyId());

			String uniqAssocBody = assocBody.getPmCertBody().getCertBodyId().toString() + assocBody.getRegNo();
			if(assocBodyList.contains(uniqAssocBody)){
				throw new EpException(MessageConst.ERROR_721034);
			}
			assocBodyList.add(uniqAssocBody);
		}
		return true;
	}
	
	public void pmStateDropdownValues() {
		Long countryId = this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId();
		List<PmState> stateLs = retrievePmStateList(countryId);
		
		if(this.getManagedBean().getBusinessStateLs() == null){
			this.getManagedBean().setBusinessStateLs(new ArrayList<PmState>());
		}
		
		if(this.getManagedBean().getSmSupp().getBusinessType() != null && !this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase("")){
			log.info("------pmStateDropdownValues------" + this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());

			if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){

				PmCountry defaultCountry = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
				this.getManagedBean().getSmBusinessAdd().setPmCountry(defaultCountry);
				this.getManagedBean().setSabahSarawakType(true);
				this.getManagedBean().setBusinessStateLs(new ArrayList<PmState>());
				
				if(this.getManagedBean().getSmBusinessAdd().isBrandNew()){
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
					this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				}

				for(PmState state : stateLs){
					if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
						if(state.getStateName().equalsIgnoreCase("SARAWAK")){
							this.getManagedBean().getBusinessStateLs().add(state);
							this.getManagedBean().getSmBusinessAdd().setPmState(state);
							this.getManagedBean().setBusinessDistrictLs(retrievePmDistrictList(state.getStateId()));
							this.getManagedBean().setBusinessCityLs(null);
							
							log.info("----SAEAWAK BusinessDistric----" + this.getManagedBean().getBusinessDistrictLs());
						}
					}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
						if(state.getStateName().equalsIgnoreCase("SABAH")){
							this.getManagedBean().getBusinessStateLs().add(state);
							this.getManagedBean().getSmBusinessAdd().setPmState(state);
							this.getManagedBean().setBusinessDistrictLs(retrievePmDistrictList(state.getStateId()));
							this.getManagedBean().setBusinessCityLs(null);
							log.info("----SABAH BusinessDistric----" + this.getManagedBean().getBusinessDistrictLs());
						}
					}
				}
			}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) ||
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)){
				
				this.getManagedBean().setBusinessStateLs(new ArrayList<PmState>());
				for(PmState state : stateLs){
					if(!state.getStateName().equalsIgnoreCase("SABAH") && !state.getStateName().equalsIgnoreCase("SARAWAK")){
						this.getManagedBean().getBusinessStateLs().add(state);
					}
				}
				this.getManagedBean().setSabahSarawakType(false);
			}else{
				this.getManagedBean().setSabahSarawakType(false);
				this.getManagedBean().setBusinessStateLs(stateLs);
			}
		}else{
			this.getManagedBean().setBusinessStateLs(stateLs);
		}

	}
	
	public boolean profBodyOnChange(PmCertBody certBody){
		boolean stat = false;

		if(certBody!= null){
			if(certBody.getCertBodyId() != null){
				Long profId = certBody.getCertBodyId();
				PmCertBody selectedCertBody = profCertBodyMap.get(profId);
				if(selectedCertBody != null){
					if(selectedCertBody.getBodyCode().equalsIgnoreCase(SupplierManagementConst.PM_CERT_BODY_OTHERS)){
						log.info("--------IS LAIN LAIN PROF--------" +selectedCertBody.getBodyCode());
						stat = true;
					}
				}
			}
		}
		return stat;
	}
	
	//display section levelcode in table
	public String getSectionBusinessNatureLevelCode(Long sectionId) {
		String retVal = "";
		
		if(sectionId!=null && sectionId.compareTo(new Long(0))==0){
			if (CollectionUtils.isNotEmpty(getManagedBean().getBusinessNatureL1())) {
				for (PmBusinessNature section : getManagedBean()
						.getBusinessNatureL1()) {
					if(sectionId.compareTo(section.getBusinessNatureId())==0){
						retVal = section.getLevelCode()+" - ";
						break;
					}
				}
			}
		}
		

		return retVal;
	}
	
	public String updateBasicSupplier(){
		if(!this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
		}

		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}

		validateBankInfo();

		if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
			if(this.getManagedBean().getSmAppl().getSmAssociationBodies().size() < 1){
				throw new EpException(MessageConst.ERROR_721033);
			}else{
				checkDuplicatedProfessionalBody();
				for(SmAssociationBody assocBody : this.getManagedBean().getSmAppl().getSmAssociationBodies()){
					if(assocBody.getSmAppl() == null){
						assocBody.setSmAppl(this.getManagedBean().getSmAppl());
					}
				}		
			}
		}
		
		if(this.getManagedBean().getSmSupp().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED){
			if(SmApplicationUtil.hasActiveTransactions(this.getManagedBean().getSmSupp())){
				throw new EpException(MessageConst.ERROR_721035);
			}else{
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
			}
			return null;
		}else{
			return updateOnlineAccount();
		}
	}
	
	public String getBusinessType(String businessType) {
		String retVal = "";
		SelectItem[] pmBusinessTypeItems = pmBusinessTypeDropDown();
		List<SelectItem> businessTypeItems = new ArrayList<SelectItem>();
		if (StringUtils.isNotBlank(businessType) && pmBusinessTypeItems!=null && pmBusinessTypeItems.length>0) {
				businessTypeItems = Arrays.asList(pmBusinessTypeItems);
				for (SelectItem item : businessTypeItems) {
					if (businessType.equals(item.getValue())) {
						retVal = item.getLabel();
						break;
					}
				}
		}
		return retVal;
	}
	
	public String getBusinessNatureSection(SmSupplierBusiness suppBusinessNat){
		String sectionLvl1Code = "";
		PmBusinessNature pmBizNat = suppBusinessNat.getPmBusinessNature();
		PmBusinessNature pmBizObjL4 = null;
		if(pmBizNat!=null && pmBizNat.getParentBusinessNature()!=null && pmBizNat.getParentBusinessNature().getBusinessNatureId()!=null){
			pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL3 = null;
		if(pmBizObjL4!=null && pmBizObjL4.getParentBusinessNature()!=null && pmBizObjL4.getParentBusinessNature().getBusinessNatureId()!=null){
			pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL2 = null;
		if(pmBizObjL3!=null && pmBizObjL3.getParentBusinessNature()!=null && pmBizObjL3.getParentBusinessNature().getBusinessNatureId()!=null){
			pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL1 = null;
		if(pmBizObjL2!=null && pmBizObjL2.getParentBusinessNature()!=null && pmBizObjL2.getParentBusinessNature().getBusinessNatureId()!=null){
			pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2.getParentBusinessNature().getBusinessNatureId());
		}
		sectionLvl1Code =  pmBizObjL1==null?"":pmBizObjL1.getLevelCode();
		return sectionLvl1Code;
	}
}


