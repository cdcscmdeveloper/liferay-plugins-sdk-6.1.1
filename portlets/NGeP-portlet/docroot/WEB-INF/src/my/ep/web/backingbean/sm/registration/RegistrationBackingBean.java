package my.ep.web.backingbean.sm.registration;

import java.io.IOException;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureReqDTO;
import my.ep.dto.pm.common.businessnature.BusinessNatureResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSalutation;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
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
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.govtsellerregistration.GovtSellerRegistrationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends BaseBackingBean<RegistrationManagedBean>{

	public static final String SUPPLIER = "SUP";

	public static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public static String newICMasking = "999999999999";
	private static String accountRegistration = "/xhtml/sm/onlinereg/registrationInfo.xhtml?faces-redirect=true";
	private static String signUp = "/xhtml/sm/onlinereg/signUp.xhtml?faces-redirect=true";
	private static String firstUserRegistration = "/xhtml/sm/onlinereg/firstUserRegistration.xhtml?faces-redirect=true";
	private static String termAndCondition = "/xhtml/sm/onlinereg/signUpTermCondition.xhtml?faces-redirect=true";
	private static String basicAccDeactivation = "/xhtml/sm/basicaccountdeactivation/basicAccountDeactivationDet.xhtml";
	private static String acknowledgement = "/xhtml/sm/onlinereg/outputMsg.xhtml";
	private static String g2gRegistration = "/xhtml/sm/govtSellerRegistration/preRenderGovtSeller.xhtml?faces-redirect=true";
	
	private static String defaultAccPurpose = "P";
	private static Map<Long, Object> bizNatureL5Map;
	private static Map<Long, Object> identityType;
	private static Map<Long, PmCertBody> profCertBodyMap;
	private static List<String> sabahSarawakRemoveWhiteSpaceList = Arrays.asList("D", "E", "G", "H");
	private static List<String> rosRemoveWhiteSpaceList = Arrays.asList("O","M","P");
	private boolean gmapDisabled;
	
	public RegistrationBackingBean() {	
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		getManagedBean().setMyCountryId(SmCommonUtil.getMyCountryId());
		if (pmUser != null && pmUser.getUserId() != null) {
			log.info("***userId: " + pmUser.getUserId());

			new LogKey(pmUser.getUserId()); 
		}else{
			new LogKey(1L); 
		}
	}

	/**
	 * Prepare the required information and redirect user to the Registration form
	 *
	 * @return String
	 */
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
			getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			getManagedBean().getSmSuppOwner().setNationality(new PmCountry());
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
			this.populateStateByAddType("permenant", false);
			this.populateStateByAddType("residential", false);
			this.populateStateByAddType("business", false);
			this.getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
			
			//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());
			//this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
			//TODO: temporary comment this.retrieveBusinessNatureL5Map();
			this.retrieveIdentityTypeMap();
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
			
			log.info("**********permenant"+ this.getManagedBean().getPermenantStateLs().size());
			log.info("**********residential" + this.getManagedBean().getResidentialStateLs().size());
			
		}
		
		//GST 
		getManagedBean().setSmGstSupplier(new SmGstSupplier());
		prepareGstDoc();
			
		this.getManagedBean().getSmAppl().setApplType(this.getManagedBean().getApplType());
		getIndividualServiceType();
		log.info("-------onlineAccountAgreement end-------------");
		
		return accountRegistration;
	}

	/**
	 * Prepare the required information and redirect user to the first user registration form
	 *
	 * @return String
	 */
	public String proceedToFirstUserRegistration() {
		log.info("****proceedToFirstUserRegistration******");
		
		//Validate GST No
		//validateGstRegNo();
		
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);

		}
		
		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);

		}
		
//		if(checkExistingBusinessRegNo()){
//			throw new EpException(MessageConst.ERROR_721003);
//
//		}
		checkExistingBusinessRegNo();
		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());

		//check the professional body if supplier type is consultant
		if(this.getManagedBean().getSmAppl().getSupplierType() != null){
			if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
				if(this.getManagedBean().getSmAppl().getSmAssociationBodies() == null || this.getManagedBean().getSmAppl().getSmAssociationBodies().size() < 1){
					throw new EpException(MessageConst.ERROR_721033);
				}else{
					checkDuplicatedProfessionalBody();
				}
			}
		}

		SmSupplierOwner suppOwner = this.getManagedBean().getSmSuppOwner();
		//log.info("-----------Owner ID Type---------" + suppOwner.getIdentificationType().getParameterCode());
		//log.info("-----------Owner ID---------" + suppOwner.getIdentificationNo());
		
		this.validateIDFormat(suppOwner.getIdentificationNo(),suppOwner.getIdentificationType().getParameterCode());
		
		if(this.getManagedBean().getSmPersonnel().getPmCountry().isBrandNew()){
			PmCountry nationality = SmCommonUtil.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
			this.getManagedBean().getSmPersonnel().setPmCountry(nationality);
		}
		
		//set all business network to false, if do not meet the criterias
		SmCompanyBasic compBasic = this.getManagedBean().getSmAppl().getSmCompanyBasic();
		if(!this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF) 
				|| !this.getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
			compBasic.setWithFederal(false);
			compBasic.setWithGlc(false);
			compBasic.setWithOthers(false);
			compBasic.setWithState(false);
			compBasic.setWithStatutory(false);
		}else{
			if(!compBasic.isWithFederal() && !compBasic.isWithGlc() && !compBasic.isWithOthers() && !compBasic.isWithState() && !compBasic.isWithStatutory()){
				throw new EpException(MessageConst.ERROR_721036);
			}
		}
		
		if(this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)){
			compBasic.setWithFederal(true);
		} 
		
		if(this.getManagedBean().getSmPersonnel().getTitle() == null) {
			this.getManagedBean().getSmPersonnel().setTitle(new PmSalutation());
		}
		
		return firstUserRegistration;
	}

	/**
	 * redirect user to the main page of supplier registration
	 *
	 * @return String
	 */
	public String returnToSignUp() {
		return signUp;
	}

	/**
	 * redirect user to the supplier registration - Company Info
	 *
	 * @return String
	 */
	public String returnToCompanyInfo() {
		return accountRegistration;
	}
	
	/**
	 * add new row to the Professional Body
	 */
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
	
	/**
	 * remove selected Professional Body
	 */
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

	/**
	 * Submit Supplier Registration
	 * - Perform business checking and save.
	 * @return String
	 */
	public String submitRegistration() {
		setAdminSameAsOwner();
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
		SmAddress smResAdd = this.getManagedBean().getSmResAdd();
		
		if(!smPersonnel.getEmail().equals(this.getManagedBean().getConfirmEmail())){
			throw new EpException(MessageConst.ERROR_721020);
		}
		
		SmCommonUtil.checkEmailDomainWithExceptionMsg(smPersonnel.getName(), smPersonnel.getEmail());
		
		if(!smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
			//check business registration no with supplier Type;
			checkExistingBusinessRegNo();
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
		
		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());

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
			
			//set mobileno to company phone no
			smAppl.getSmCompanyBasic().setPhoneNo(smPersonnel.getMobileNo());
			
			getManagedBean().setGstFlag(false);
		}
		
		//Validate IC Length
		this.retrieveIdentityTypeMap();
		PmParameter personnelIDType = (PmParameter) identityType.get(smPersonnel.getIdentificationTypeId());
		this.validateIDFormat(smPersonnel.getIdentificationNo(), personnelIDType.getParameterCode());
		
		//validate IC with MyIdentity
		if(personnelIDType.getParameterCode().equals(SupplierManagementConst.BASIC_IND_IDENT_TYPE)){
			SmCommonUtil.getInstance().verifyWithMyIdentity(smPersonnel.getName(), smPersonnel.getIdentificationNo(), this.getManagedBean().getSmPermenantAdd().getPostcode());
		}
		
		/*String loginID = smSupp.getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG) ? smSupp.getRegNo() : this.getManagedBean().getSmPersonnel().getIdentificationNo();
		if(isExistUserByIdentificationNo(loginID)){
			throw new EpException(MessageConst.ERROR_721005);

		}*/
		
		//check Exist email in smPersonnel
		//this.checkExistEmailInPersonnel(smPersonnel.getEmail());
		
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
		//smPersonnel.setSoftcertFlg(SupplierManagementConst.REG_SOFTCERT);
		//smPersonnel.setBumiFlg(SupplierManagementConst.REG_BUMI);
		smPersonnel.setContractSignerFlg(SupplierManagementConst.FIRST_USER_CONT_SIGNER);
		smPersonnel.setEpRole(RoleCode.SUPPLIER_TEMP.value());
		smPersonnel.setClerical(true);
		
		String trimmedName = smPersonnel.getName().trim();
		smPersonnel.setName(trimmedName);
		
		//set personnel bumi flag or muslim flag 
		smPersonnel.setBumiFlg(getManagedBean().getUserBumiFlg());
		smPersonnel.setMuslimFlg(getManagedBean().getUserMuslimFlg());

		//to set the softcert Flag
		//All supplier type will mark to "SOFT_CERT_PENDING_REQ", except supplier type is "Consultant"
		if(!smAppl.getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			smPersonnel.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_PENDING_REQ);
		}else{
			smPersonnel.setSoftcertFlg(0);
		}

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
		
		
		/*
		 * Professional Cert 
		 */
		if(this.getManagedBean().getSmAppl().getSupplierType() != null && this.getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
			if((smAppl.getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT) && smAppl.getSupplierType() != null)){  
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
		
		//GST
		if(getManagedBean().isGstFlag()){
			getManagedBean().getSmSupp().setSmGstSuppliers(new ArrayList<SmGstSupplier>());
			getManagedBean().getSmSupp().setGstRegisteredFlag(DbValueConst.RECORD_STATUS_ACTIVE);
			setSupplierGst();
		}

		/*
		 * Add all related objects to Parent SmSupplier
		 */
		smSupp.getSmAppls().add(smAppl);
		smSupp.setPmStatus(new PmStatus());
		smSupp.getPmStatus().setStatusId(20101L);
		smSupp.setRecordStatusFlg(DbValueConst.RECORD_STATUS_IN_PROGRESS);

		smSuppOwner.setSmSupplier(this.getManagedBean().getSmSupp());
		smSupp.getSmSupplierOwners().add(smSuppOwner);
		
		/* Create Basic Account */
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setLanguage(fs.getUserLocale().getLanguage());
		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);
		
		if (resDTO.isSuccessful()) {
			log.info("****User created Successfully*******");
			String suppAdminEmail = smPersonnel != null
					&& StringUtils.isNotBlank(smPersonnel.getEmail()) ? smPersonnel
					.getEmail() : "";
			// SmPersonnel resSmPersonnel =
			// resDTO.getSmSupp().getSmAppls().get(0).getSmPersonnels().get(0);

			// this.sendActivationEmail(resSmPersonnel);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_721012,
									new String[] {
											suppAdminEmail,
											SmRuleHelper.getInstance()
													.getEmailActivationDays()
													+ "" },
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
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

	/**
	 * update Basic Supplier Account
	 * @return String
	 */
	public String updateOnlineAccount() {
		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		
		if(this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmPermenantAdd());
		}else{
			smSupp.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).setSmAddress(this.getManagedBean().getSmBusinessAdd());
		}

		reqDTO.setSmSupp(getManagedBean().getSmSupp());
		
		try{
			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
					.getInstance().getServiceObject("sm", IRegistrationSO.class);			
			RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
			if (resDTO.isSuccessful()) {
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null,
						MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				/*if(deactivateAccount){
					RequestContext.getCurrentInstance().execute("successDialog.show()");
				}else{
					renewSesionObject();
				}
				return retPage;*/
				return acknowledgement;
			} else {
				//RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721006);
			}
		} catch(Exception ex){
			throw new EpException(ex.getMessage());
		}finally{
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
		}
	}

	/**
	 * add new row to Supplier Bank
	 */
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

	/**
	 * remove selected Supplier Bank
	 */
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
	
	/**
	 * create new Business Nature Object and set to the selectedBusinessNature
	 * and reset all lvl and business nature selection list
	 */
	public void addBusinessNature() {
		this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());	
		this.getManagedBean().getSelectedBusinessNature().setPmBusinessNature(new PmBusinessNature());
		
		resetBusinessNatureSelection();
		//RequestContext.getCurrentInstance().update(":form:bizDialog");
		
		log.info("---------addBusinessNature Lvl 1 ------" +this.getManagedBean().getSelectedBusinessNature().getLvl1());
	}
	
	/**
	 * add the selectedBusinessNature to SmSupplierBusiness List
	 */
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

	/**
	 * Validate duplicate Business Nature
	 * @param bizNatureId
	 * @return boolean
	 */
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
	
	/**
	 * remove selected business nature from the SmSupplierBusinesses List
	 */
	public void removeBusinessNature() {
		log.info("---------------removeBusinessNature count------------" + this.getManagedBean().getSelectedBusinessNatureLs().length);
		if(!this.getManagedBean().getSmSupp().isBrandNew()){
			if(this.getManagedBean().getSelectedBusinessNatureLs().length ==  this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size()){
				throw new EpException(MessageConst.ERROR_721014);
			}
		}
		for (SmSupplierBusiness suppBusinessNature : this.getManagedBean().getSelectedBusinessNatureLs()) {
			log.info("---------------removeBusinessNature.------------" + suppBusinessNature);
			this.getManagedBean().getSmSupp().getSmSupplierBusinesses().remove(suppBusinessNature);
		}
	}
	
	/**
	 * Edit Business Nature
	 */
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
	
	/**
	 * update the edited business nature
	 */
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
	
	/**
	 * set business Nature List based on business level
	 * @param bizLvl
	 */
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

	/**
	 * listener - Business Type On Change
	 */
	//Unused
	public void businessTypeOnChange() {
		if (this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)) {
			log.info("--------------Business Type is Individual------------");
			// this.populateStateByAddType("business");
		} else {
			log.info("-----------Business Type is not Individual------------"
					+ this.getManagedBean().getSmSupp().getBusinessType());
		}
	}


	/**
	 * Retrieve Country List from PM
	 */
	private void retrievePmCountryList() {
		List<SelectItem> countryLs = SmCommonUtil.getCountryList();
		this.getManagedBean().setCountryLs(countryLs);
	}

	/**
	 * retrieve State List by country Id
	 * @param countryId
	 * @return List<PmState>
	 */
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

	/**
	 * retrieve District List by state Id
	 * @param stateId
	 * @return List<PmDistrict>
	 */
	/*private List<PmDistrict> retrievePmDistrictList(Long stateId) {
		if(stateId != null){
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
					.getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = reqSO.retriveDistrictListByStateID(reqDTO);
			return resDTO.getDistrictList();
		}
		
		return null;
	}*/

	/**
	 * retrieve City List by district Id
	 * @param districtId
	 * @return List<PmCity>
	 */
/*	private List<PmCity> retrievePmCityList(Long districtId) {
		if(districtId != null){
			CityReqDTO reqDTO = new CityReqDTO();
			reqDTO.setDistrictId(districtId);
			ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
					.getServiceObject("pm", ICitySO.class);
			CityResDTO resDTO = reqSO.retriveCityListByDistrictId(reqDTO);
			return resDTO.getCityList();
		}
		return null;
	}*/

	/**
	 * retrieve PM FinancialOrg List 
	 */
	public List<PmFinancialOrg> retrieveFinancialOrgList() {
		FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
		reqDTO.setServiceType("PB");
		IFinancialOrgSO reqSO = (IFinancialOrgSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IFinancialOrgSO.class);
		FinancialOrgResDTO resDTO = reqSO
				.retrieveFinancialOrgListByServiceType(reqDTO);
		return resDTO.getFinancialOrgList();
	}
	
	/**
	 * Retrieve Proffessional Body List from PM
	 */
	private void retrieveProfBody() {
		// PmCertBodyReqDTO = new PmCertBodyReqDTO();
		log.info("*********retrieveCertBody************");
		this.getManagedBean().setProfCertBodyLs(SmApplicationUtil.retrievePmProfBodyList());
		
		if(this.getManagedBean().getProfCertBodyLs()==null){
			this.getManagedBean().setProfCertBodyLs(new ArrayList<PmCertBody>());
		}
		
		if(this.getManagedBean().getProfCertBodyLs().size() > 0){
			profCertBodyMap = new HashMap<Long, PmCertBody>();
			for(PmCertBody cb : this.getManagedBean().getProfCertBodyLs()){
				profCertBodyMap.put(cb.getCertBodyId(), cb);
			}
		}
	}

	/**
	 * retrieve BusinessNature By Parent Level
	 * @param busLvl
	 * @param parentId
	 * @return List<PmBusinessNature>
	 */
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
	
	/**
	 * Check the existance of business Reg No
	 * @return boolean
	 */
	private void checkExistingBusinessRegNo(){
		
		if (!StringUtils
				.isBlank(getManagedBean().getSmSupp().getBusinessType())
				&& SupplierManagementConst.LOCAL_AUTH_BUSINESS_TYPE
						.contains(getManagedBean().getSmSupp()
								.getBusinessType())) {
			validateBusinessRegNoWithBussTypeAndLocalAuth();
		} else{
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			List<Integer> recStatusLs = new ArrayList<Integer>();
			recStatusLs.add(DbValueConst.RECORD_STATUS_ACTIVE);
			recStatusLs.add(DbValueConst.RECORD_STATUS_IN_PROGRESS);
			
			IRegistrationSO reqSO = SmServiceLookup.getRegistrationSO();
			reqDTO.setRegNo(this.getManagedBean().getSmSupp().getRegNo());
			reqDTO.setStatusLs(recStatusLs);
			RegistrationResDTO resDTO = reqSO.retrieveSupplierByRegNoAndStatusLs(reqDTO);
			
			if(resDTO.getSmSuppliers().size() > 0 ){
				//Appl Type is MOF
				if(getManagedBean().getSmAppl().getApplType().equalsIgnoreCase(SupplierManagementConst.ACC_MOF)){
					for(SmSupplier supplier : resDTO.getSmSuppliers()){
						if(getManagedBean().getSmAppl().getSupplierType().equals(supplier.getSmAppl().getSupplierType())){
							log.info("MOF - Contain same supplier type");
							String labelText = null;
							if(getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
								labelText = SmCommonUtil.getMessage("lbl.sm.contractor", null);
							}else{
								labelText = SmCommonUtil.getMessage("lbl.sm.consultant", null);
							}
							
							throw new EpException(MessageConst.ERROR_721041,new String[]{labelText,getManagedBean().getSmSupp().getRegNo()});
						}
						
						if(getManagedBean().getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
							if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)){
								throw new EpException(MessageConst.ERROR_721042,new String[]{getManagedBean().getSmSupp().getRegNo()});
							}
						}
					}
				}
				else{
					//Appl Type is BASIC
					for(SmSupplier supplier : resDTO.getSmSuppliers()){
						if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
							throw new EpException(MessageConst.ERROR_721041,new String[]{SmCommonUtil.getMessage("lbl.sm.contractor",null), getManagedBean().getSmSupp().getRegNo()});
						}
						
						if(supplier.getSmAppl().getSupplierType().equalsIgnoreCase(SupplierManagementConst.STYPE_BASIC)){
							throw new EpException(MessageConst.ERROR_721041,new String[]{SmCommonUtil.getMessage("lbl.sm.contractor",null), getManagedBean().getSmSupp().getRegNo()});
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * populate indiviual Service Type
	 */
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
	
	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateStateByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			//List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			//this.getManagedBean().setBusinessStateLs(stateLs);
			if(reset){
				getManagedBean().getSmBusinessAdd().getPmState().setStateId(null);
			}
			pmStateDropdownValues();
			//registrationBackingBean.managedBean.smBusinessAdd.pmState.
			if(getManagedBean().getSmBusinessAdd().isBrandNew()){
				getManagedBean().getSmBusinessAdd().getPmDistrict().setDistrictId(null);
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
			}
		} else if (typeAdd.equals("residential")) {
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		} else if (typeAdd.equals("permenant")) {
			List<SelectItem> stateLs = SmCommonUtil.getStateList(this.getManagedBean().getSmPermenantAdd().getPmCountry().getCountryId());
			this.getManagedBean().setPermenantStateLs(stateLs);
			//registrationBackingBean.managedBean.smPermenantAdd.pmCountry.countryId
			getManagedBean().getSmPermenantAdd().getPmDistrict().setDistrictId(null);
			getManagedBean().getSmPermenantAdd().getPmCity().setCityId(null);
		}
	}

	/**
	 * set State By Address Type
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateDistrictByAddType(String typeAdd, boolean reset) {
		if (typeAdd.equals("business")) {
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
				getManagedBean().getSmBusinessAdd().getPmCity().setCityId(null);
			}
			this.getManagedBean().getSmBusinessAdd().setPmState(SmCommonUtil.getInstance().getStateById(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId()));
			List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmBusinessAdd().getPmState().getStateId());
			this.getManagedBean().setBusinessDistrictLs(districtLs);
			this.getManagedBean().setBusinessCityLs(new ArrayList<SelectItem>());
		} else if (typeAdd.equals("residential")) {
			log.info("------------residential----------------");
			List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmResAdd().getPmState().getStateId());
			this.getManagedBean().setResidentialDistrictLs(districtLs);
			this.getManagedBean().setResidentialCityLs(new ArrayList<SelectItem>());
		} else if (typeAdd.equals("permenant")) {
			log.info("------------permenant----------------"
					+ this.getManagedBean().getSmPermenantAdd().getPmState()
					.getStateId());
			List<SelectItem> districtLs = SmCommonUtil.getDistrictList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
			this.getManagedBean().setPermenantDistrictLs(districtLs);
			this.getManagedBean().setPermenantCityLs(new ArrayList<SelectItem>());

			log.info("------permenant LS-------------"
					+ this.getManagedBean().getPermenantDistrictLs().size());
		}
	}

	/**
	 * set City By AddType
	 * @param typeAdd - business, residential, permenant
	 */
	public void populateCityByAddType(String typeAdd, boolean reset) {
		log.info("---------------City By Type--------------------");
		if (typeAdd.equals("business")) {
			if(reset){
				getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
			}
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(SmCommonUtil.getInstance().getDistrictById(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId()));
			List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmBusinessAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setBusinessCityLs(cityLs);
			populateLocalAuthority(getManagedBean().getSmSupp(), this.getManagedBean().getSmBusinessAdd());
		} else if (typeAdd.equals("residential")) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmResAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setResidentialCityLs(cityLs);
		} else if (typeAdd.equals("permenant")) {
			List<SelectItem> cityLs = SmCommonUtil.getCityList(this.getManagedBean().getSmPermenantAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setPermenantCityLs(cityLs);
		}
	}
	
	public void onSelectCity(String typeAdd){
		if (typeAdd.equals("business")) {
			this.getManagedBean().getSmBusinessAdd().setPmCity(SmCommonUtil.getInstance().getCityById(this.getManagedBean().getSmBusinessAdd().getPmCity().getCityId()));
		}
	}

	/**
	 * view supplier basic account
	 */
	public void viewOnlineAccount() {
		log.info("--------------------viewOnlineAccount----------------" + MDC.get(LogKey.USER_ID));
		FacesContext fc = FacesContext.getCurrentInstance();
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if(pmUser.getUserId().longValue() != 1 || pmUser == null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			//846846516516
			//PmUser pmUser = new FacesScopes().getUserSessionBean();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			//ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			//SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
			
			//for govt seller basic account maintenance
			if(smSupp.getBusinessType().equals("Q")){
				GovtSellerRegistrationBackingBean govtSellerRegistrationBackingBean = new GovtSellerRegistrationBackingBean();
				govtSellerRegistrationBackingBean.preRenderGovtSellerMaintenance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/govtSellerRegistration/govtSellerMaintenanceBasePage.xhtml");
			}else{
				log.info("-------smResDTO-------" + smSupp.getSupplierId()); 

				RegistrationReqDTO reqDTO = new RegistrationReqDTO();
				reqDTO.setSupplierId(smSupp.getSupplierId());

				IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
				RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

				this.getManagedBean().setSmSupp(resDTO.getSmSupp());
				this.getManagedBean().setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
				this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
				getManagedBean().setApplType(getManagedBean().getSmAppl().getApplType());
				
				if(resDTO.getSmSupp().getSmSupplierOwners()!=null && CollectionUtils.isNotEmpty(resDTO.getSmSupp().getSmSupplierOwners())){
					this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners().get(0));
				}

				//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());	
				//this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(new PmBusinessNature());

				log.info("---------Supplier Type-----------" + this.getManagedBean().getSmAppl().getSupplierType());
				if(resDTO.getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
					SmPersonnel personnel = getManagedBean().getSmAppl().getSmPersonnels().get(0);
					getManagedBean().setSmPersonnel(personnel);
					getManagedBean().setUserBumiFlg(personnel.getBumiFlg());
					getManagedBean().setUserMuslimFlg(personnel.getMuslimFlg());
					getIndividualServiceType();
					this.getManagedBean().setSmPermenantAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					this.populateStateByAddType("permenant", false);
					this.populateDistrictByAddType("permenant", false);
					this.populateCityByAddType("permenant", false);

					this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					this.populateStateByAddType("business", false);
					this.populateDistrictByAddType("business", false);
					this.populateCityByAddType("business", false);
				}else{
					if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK) ||
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
						this.getManagedBean().setSabahSarawakType(true);
					}
					
					this.getManagedBean().setSmBusinessAdd(resDTO.getSmSupp().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress());
					this.populateStateByAddType("business", false);
					this.populateDistrictByAddType("business", false);
					this.populateCityByAddType("business", false);
				}
				this.retrievePmCountryList();
				this.getManagedBean().setFinancialOrgLs(retrieveFinancialOrgList());
				
				if(this.getManagedBean().getSmAppl().getSupplierType().equals(SupplierManagementConst.STYPE_CONSULTANT)){
					this.retrieveProfBody();
				}

				retrieveIdentityTypeMap();
				this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
				this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
				setDisableCompanyNameBusinessTypeInMaintenance();
				
				//retrieve Supplier GST Info
				SmGstSupplier gstSupp = SmCommonUtil.getSmGstSupplierBySupplierIdAndBranchCode(resDTO.getSmSupp().getSupplierId(), null, null,true);
				getManagedBean().setSmGstSupplier(gstSupp);

				fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlineAccount/accountMaintenanceDet.xhtml");
			}
			
		}else{
			fc.addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					null,
					MessageUtils.getMessage(MessageConst.ERROR_721017, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlinereg/outputMsg.xhtml");

		}
	}
	
	/**
	 * populate the business type drop down based
	 * @return SelectItem[]
	 */
	public SelectItem[] pmBusinessTypeDropDown(){
		String applType = this.getManagedBean().getApplType();
		SelectItem[] items = null;
		
		if(applType.equalsIgnoreCase(SupplierManagementConst.ACC_BASIC)){
			items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE);
		}else{
			FacesScopes fs = new FacesScopes();
			
			String supplierType = this.getManagedBean().getSmAppl().getSupplierType();
			if(supplierType != null){
				if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR)){
					items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONTRACTOR);
				}else if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
					items = getDropdownCodeValues(ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT);
				}
			}else{
				items = new SelectItem[1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						fs.getUserLocale()));
			}
		}

		return items;
	}
	
	/**
	 * retrieve Business Nature L5
	 * and set to a Map
	 */
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

	/**
	 * reset Business Nature Selection - clean the selectedBusinessNature
	 */
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
	
	/**
	 * check the email existence in PmUser
	 * @param email
	 * @return boolean
	 */
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
	
	/**
	 * check the email existence in SmPersonnel
	 * @param email
	 */
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

	/**
	 * check user existence by identification No
	 * @param idNo
	 * @return boolean
	 */
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
	
	/**
	 * Supplier Bank Validation
	 */
	public void validateBankInfo(List<SmSupplierBank> banks){
		List<String> bankList = new ArrayList<String>();
		for(SmSupplierBank suppBank : banks){
			log.info("*********suppBank.getPmFinancialOrg().getFinancialOrgId()****" + suppBank.getPmFinancialOrg().getFinancialOrgId());
			if(suppBank.getPmFinancialOrg().getFinancialOrgId() == null){
				throw new EpException(MessageConst.ERROR_721008);
			}

			if(StringUtils.isBlank(suppBank.getAccountNo())){
				throw new EpException(MessageConst.ERROR_721009);
			}
				
			if (StringUtils.isNotBlank(suppBank.getAccountNo()) && !StringUtils.isNumeric(suppBank.getAccountNo())) {
				throw new EpException(MessageConst.ERROR_722616);
			}

			String uniqBank = suppBank.getPmFinancialOrg().getFinancialOrgId().toString() + suppBank.getAccountNo();
			if(bankList.contains(uniqBank)){
				throw new EpException(MessageConst.ERROR_721012);
			}
			bankList.add(uniqBank);
		}
	}
	
	/**
	 * Validate entered business registration no whether already exist
	 */
	private void validateBusinessRegNoWithBussTypeAndLocalAuth() {
		if (StringUtils.isNotBlank(getManagedBean().getSmSupp()
				.getBusinessType())
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getRegNo())) {
			if (SmCommonUtil.isRegNoAlreadyExist(getManagedBean().getSmSupp()
					.getRegNo(),
					getManagedBean().getSmSupp().getBusinessType(),
					getManagedBean().getSmSupp().getLocalAuthorityId())) {
				throw new EpException(MessageConst.ERROR_722634);
			}
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

	/**
	 * reset Session Date
	 */
	public void resetSessionData() {
		log.debug("renewing session object..");
		renewSesionObject();
		validateLoginSession();
	}
	
	/**
	 * get portal company Id
	 * @return String
	 */
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
	
	/**
	 * trim owner Passport Value
	 */
	public void trimOwnerPassportValue(){
		String newPassportVal = "";
		if(StringUtils.isNotBlank(this.getManagedBean().getSmSuppOwner().getIdentificationNo())){
			newPassportVal = this.getManagedBean().getSmSuppOwner().getIdentificationNo().replaceAll("\\s", "");
			this.getManagedBean().getSmSuppOwner().setIdentificationNo(newPassportVal);
		}
	}
	
	/**
	 * trim Personnel Passport Value
	 */
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
			populateDistrictByAddType("residential", false);
			this.getManagedBean().getSmResAdd().getPmDistrict().setDistrictId(smPermAdd.getPmDistrict().getDistrictId());
			populateCityByAddType("residential", false);
			this.getManagedBean().getSmResAdd().getPmCity().setCityId(smPermAdd.getPmCity().getCityId());
		}else{
			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());
		}
	}
	
	/**
	 * Validate ID format
	 * @param identNo
	 * @param identType
	 */
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
	
	/**
	 * check Duplicated Professional Body
	 * @return boolean
	 */
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
	
	/**
	 * set state drop down value
	 */
	public void pmStateDropdownValues() {
		Long countryId = this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId();
		List<PmState> stateLs = retrievePmStateList(countryId);
		List<SelectItem> stateSelectLs = new ArrayList<SelectItem>();
		
		if(this.getManagedBean().getBusinessStateLs() == null){
			this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
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
				this.getManagedBean().setBusinessStateLs(new ArrayList<SelectItem>());
				
				if(this.getManagedBean().getSmBusinessAdd().isBrandNew()){
					this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
					this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());
				}

				for(PmState state : stateLs){
					if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SARAWAK) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SARAWAK)){
						if(state.getStateName().equalsIgnoreCase("SARAWAK")){
							stateSelectLs.add(convertToStateSelectItem(state));
							this.getManagedBean().getSmBusinessAdd().setPmState(state);
							this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
							this.getManagedBean().setBusinessCityLs(null);
							
							log.info("----SAEAWAK BusinessDistric----" + this.getManagedBean().getBusinessDistrictLs());
						}
					}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SABAH) || 
							this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SABAH)){
						if(state.getStateName().equalsIgnoreCase("SABAH")){
							stateSelectLs.add(convertToStateSelectItem(state));
							this.getManagedBean().getSmBusinessAdd().setPmState(state);
							this.getManagedBean().setBusinessDistrictLs(SmCommonUtil.getDistrictList(state.getStateId()));
							this.getManagedBean().setBusinessCityLs(null);
							log.info("----SABAH BusinessDistric----" + this.getManagedBean().getBusinessDistrictLs());
						}
					}
				}
			}else if(this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERSEORANGAN_SEMENANJUNG) ||
					this.getManagedBean().getSmSupp().getBusinessType().equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROB_PERKONGSIAN_SEMENANJUNG)){
				for(PmState state : stateLs){
					if(!state.getStateName().equalsIgnoreCase("SABAH") && !state.getStateName().equalsIgnoreCase("SARAWAK")){
						stateSelectLs.add(convertToStateSelectItem(state));
					}
				}
				this.getManagedBean().setSabahSarawakType(false);
			}else{
				this.getManagedBean().setSabahSarawakType(false);
				//this.getManagedBean().setBusinessStateLs(stateLs);
			}
		}
		
		if(stateSelectLs.size() < 1){
			for(PmState state : stateLs){
				stateSelectLs.add(convertToStateSelectItem(state));
			}
		}
		this.getManagedBean().setBusinessStateLs(stateSelectLs);
		populateLocalAuthority(getManagedBean().getSmSupp(), getManagedBean().getSmBusinessAdd());
		
	}
	
	/**
	 * Professional Body OnChange -Listener
	 * @param certBody
	 * @return boolean
	 */
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
	
	/**
	 * display section levelcode in table
	 * @param sectionId
	 * @return String
	 */
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
	
	/**
	 * update Basic Supplier
	 * @return String
	 */
	public String updateBasicSupplier(){
		if(!this.getManagedBean().getSmSupp().getBusinessType().equals(SupplierManagementConst.INDIVIDUAL_FLAG)){
			if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
				throw new EpException(MessageConst.ERROR_721011);
			}
		}

		if(this.getManagedBean().getSmAppl().getSmSupplierBanks().size() < 1){
			throw new EpException(MessageConst.ERROR_721002);
		}

		validateBankInfo(this.getManagedBean().getSmAppl().getSmSupplierBanks());

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
		
		return updateOnlineAccount();
		
	}
	
	/**
	 * get Business Nature Section
	 * @param suppBusinessNat
	 * @return String
	 */
	public String getBusinessNatureSection(SmSupplierBusiness suppBusinessNat) {
		String sectionLvl1Code = "";
		PmBusinessNature pmBizNat = suppBusinessNat.getPmBusinessNature();
		PmBusinessNature pmBizObjL4 = null;
		if (pmBizNat != null
				&& pmBizNat.getParentBusinessNature() != null
				&& pmBizNat.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL4 = SmCommonUtil.retrieveBusinessNatureById(pmBizNat
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL3 = null;
		if (pmBizObjL4 != null
				&& pmBizObjL4.getParentBusinessNature() != null
				&& pmBizObjL4.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL3 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL4
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL2 = null;
		if (pmBizObjL3 != null
				&& pmBizObjL3.getParentBusinessNature() != null
				&& pmBizObjL3.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL2 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL3
					.getParentBusinessNature().getBusinessNatureId());
		}
		PmBusinessNature pmBizObjL1 = null;
		if (pmBizObjL2 != null
				&& pmBizObjL2.getParentBusinessNature() != null
				&& pmBizObjL2.getParentBusinessNature().getBusinessNatureId() != null) {
			pmBizObjL1 = SmCommonUtil.retrieveBusinessNatureById(pmBizObjL2
					.getParentBusinessNature().getBusinessNatureId());
		}
		sectionLvl1Code = pmBizObjL1 == null ? "" : pmBizObjL1.getLevelCode();
		return sectionLvl1Code;
	}

	/**
	 * remove RegNo WhiteSpace
	 */
	public void removeRegNoWhiteSpace() {
		if (getManagedBean().getSmSupp() != null
				&& getManagedBean().getSmSupp().getBusinessType() != null
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getBusinessType())
				&& getManagedBean().getSmSupp().getRegNo() != null
				&& StringUtils.isNotBlank(getManagedBean().getSmSupp()
						.getRegNo())) {
			String bussType = getManagedBean().getSmSupp().getBusinessType();
			if (sabahSarawakRemoveWhiteSpaceList.contains(bussType)) {
				getManagedBean().getSmSupp().setRegNo(
						StringUtils.deleteWhitespace(getManagedBean()
								.getSmSupp().getRegNo()));
			} else if (rosRemoveWhiteSpaceList.contains(bussType)) {
				getManagedBean().getSmSupp().setRegNo(
						StringUtils.deleteWhitespace(getManagedBean()
								.getSmSupp().getRegNo()));
			}
		}
	}
	
	public void downloadFile(String filePath) throws IOException {
		StringBuilder downloadPath = new StringBuilder();
		downloadPath.append(EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)).append(filePath);
		
		super.downloadFile(downloadPath.toString());
	}
	
	/**
	 * View Business Nature
	 */
	public void viewBusinessNature(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();

		if(pmUser != null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierSearchSO.class);
			SupplierSearchResDTO smResDTO =supplierSearchSO.retriveSupplierProfiles(supplierSearchReqDTO);
			SmSupplier smSupp = (SmSupplier)smResDTO.getResultBean().getResultList().get(0); 

			log.info("-------smResDTO-------" + smSupp.getSupplierId()); 

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSupplierId(smSupp.getSupplierId());

			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.retrieveBusinessNatureBySupplierId(reqDTO);
			
			this.getManagedBean().setSmSupp(resDTO.getSmSupp());
			
//			this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
			this.getManagedBean().setSelectedBusinessNature(new SmSupplierBusiness());
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplierbusinessnature/businessNatureMaint.xhtml");
	}
	
	/**
	 * update Business Nature Maintenance
	 * @return
	 */
	public String updateBusinessNatureMaintenance(){
		if(this.getManagedBean().getSmSupp().getSmSupplierBusinesses().size() < 1){
			throw new EpException(MessageConst.ERROR_721011);
		}else{
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setSmSupp(this.getManagedBean().getSmSupp());

			IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
			RegistrationResDTO resDTO = reqSO.saveSupplierBusinessNature(reqDTO);

			if(resDTO.isSuccessful()){
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_100002,
						MessageUtils.BUNDLE_COMMON_MESSAGES,new FacesScopes().getUserLocale())));	
				return "/xhtml/common/acknowledge.xhtml";
			}
		}
		
		return null;
	}

	/**
	 * post Update BizNature
	 * @param bisNatures
	 */
	public void postUpdateBizNature(List<BaseEntity> bisNatures) {
		log.info("bisNatures: " + bisNatures);

		IBusinessNatureSO bizNatureSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
		Map<Long, PmBusinessNature> bizNatureUniqueMap = new HashMap<Long, PmBusinessNature>();
		
		for(SmSupplierBusiness ssb : this.getManagedBean().getSmSupp().getSmSupplierBusinesses()) {
			bizNatureUniqueMap.put(ssb.getPmBusinessNature().getBusinessNatureId(), ssb.getPmBusinessNature());
		}
		
		for(int i = 0; i < bisNatures.size(); i++) {
			PmBusinessNature bn = (PmBusinessNature)bisNatures.get(i);
			log.info("bn.getLevelName(): " + bn.getLevelName());
			
			if(!bizNatureUniqueMap.containsKey(bn.getBusinessNatureId())) {
				
				BusinessNatureReqDTO bnReqDTO = new BusinessNatureReqDTO();
				bnReqDTO.setBusinessNatureId(bn.getBusinessNatureId());
				BusinessNatureResDTO resDTO = bizNatureSO.fetchParentBusinessNature(bnReqDTO);
				bn = resDTO.getBusinessNature();
				bizNatureUniqueMap.put(bn.getBusinessNatureId(), bn);

				SmSupplierBusiness suppBusiness = new SmSupplierBusiness();
				suppBusiness.setPmBusinessNature(bn);
				suppBusiness.setSmSupplier(this.getManagedBean().getSmSupp());
				this.getManagedBean().getSmSupp().getSmSupplierBusinesses().add(suppBusiness);
			}
		
		}
	}
	
	/**
	 * Set admin related information same as owner after ticked check box
	 */
	private void setAdminSameAsOwner() {
		if (getManagedBean().isSameAsOwner()) {
			getManagedBean().getSmPersonnel().setName(
					getManagedBean().getSmSuppOwner().getOwnerName());
			getManagedBean().getSmPersonnel().setPmCountry(
					getManagedBean().getSmBusinessAdd().getPmCountry());
			getManagedBean().getSmPersonnel()
					.setIdentificationTypeId(
							getManagedBean().getSmSuppOwner()
									.getIdentificationTypeId());
			getManagedBean().getSmPersonnel().setIdentificationNo(
					getManagedBean().getSmSuppOwner().getIdentificationNo());
		}
	}

	/**
	 * Set whether to disable company name and business type field from editing
	 * in maintenance
	 */
	private void setDisableCompanyNameBusinessTypeInMaintenance() {
		getManagedBean().setDisabledCompanyNameInMaintenance(false);
		if (SmApplicationUtil.hasActiveTransactions(this.getManagedBean()
				.getSmSupp())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		if (SupplierManagementConst.LIST_OF_BUSINESS_TYPE_NOT_ALLOWED_TO_EDIT_COMPANY_NAME
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
		}
		if (!SupplierManagementConst.LIST_OF_BUSINESS_TYPE_ALLOWED_TO_BE_EDITED
				.contains(getManagedBean().getSmSupp().getBusinessType())) {
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
		if (SmApplicationUtil
				.isApplHasSoftcertProcessingNotComplete(getManagedBean()
						.getSmSupp().getSmAppl())) {
			getManagedBean().setDisabledCompanyNameInMaintenance(true);
			getManagedBean().setDisabledBusinessTypeInMaintenance(true);
		}
	}
	
	/**
	 * Validate gst no and format
	 */
	public void validateGstRegNo(){
		String gstNo = this.getManagedBean().getSmGstSupplier().getGstRegNo();
		if(gstNo != null && gstNo.length() > 0){
			if(!SmCommonUtil.validateGstRegNo(gstNo)){
				this.getManagedBean().setGstFlag(false);
				throw new EpException(MessageConst.ERROR_721027);
			}else{
				this.getManagedBean().setGstFlag(true);
			}
		}else{
			deleteGst(this.getManagedBean().getSmGstSupplier());
			this.getManagedBean().setGstFlag(false);
		}
	}
	
	
	/**
	 * FIle upload Handler
	 * @param event
	 * @throws Exception
	 */
	public void handleFileUpload(FileUploadEvent event) throws Exception {
		if(event != null){
			log.info("******handleFileUpload******");
			String tempPath = String.valueOf((new Date()).getTime());
			String filePath = SmCommonUtil.getUploadFilePath(SupplierManagementConst.GST_FILE_PATH) + tempPath + "/";
			
			String fileName = String.valueOf((new Date()).getTime()) + "_" + event.getFile().getFileName();
			getManagedBean().getSelectedGstSupplierDoc().setFileName(fileName);
			getManagedBean().getSelectedGstSupplierDoc().setFilePath(filePath);
			
			SmCommonUtil.processUpload(filePath, event, fileName);
		}
	}
	
	private SelectItem convertToStateSelectItem(PmState state){
		SelectItem si = new SelectItem();
		si.setLabel(state.getStateName());
		si.setValue(state.getStateId());
		
		return si;
	}
	
	/**
	 * Redirect to liferay logout url
	 */
	public void proceedToLogout() {
		RequestContext.getCurrentInstance().execute("successDialog.hide()");
//		SmCommonUtil.redirectToLogoutUrl();
	}
	
	/**
	 * proceed to Term & Condition Page
	 * @return String
	 */
	public String proceedToTermAndCondition(){
		if(getManagedBean().getApplType() != null){
			if(getManagedBean().getApplType().equals(SupplierManagementConst.ACC_GOVT_SELLER)){
				return g2gRegistration;
			}else{
				return termAndCondition;
			}
		}else{
			return signUp;
		}
	}
	
	/**
	 * return to Term & Condition Page
	 * @return String
	 */
	public String returnToTermAndCondition(){
		return termAndCondition;

	}

	/**
	 * Retrieve Gst Supporting Doc From PmDocument
	 */
	public List<PmDocument> retrieveGstDocFromPmDocument(){
		IDocumentSO reqSO = SmServiceLookup.getDocumentSO();
		DocumentReqDTO reqDTO = new DocumentReqDTO();
		DocumentResDTO resDTO = reqSO.retrieveGstSuppDocuments(reqDTO);
		
		return resDTO.getDocumentList();
	}
	
	/**
	 * Set GstSupplier Object to supplier
	 */
	private void setSupplierGst(){
		SmGstSupplier gstSupplier = getManagedBean().getSmGstSupplier();
		gstSupplier.setIsCurrent(true);
		
		if(gstSupplier.getSmGstSupplierDocs().size() > 0){
			List<SmGstSupplierDoc> tempLs = new ArrayList<SmGstSupplierDoc>();
			tempLs.addAll(gstSupplier.getSmGstSupplierDocs());
			
			for(SmGstSupplierDoc supplierDoc : gstSupplier.getSmGstSupplierDocs()){
				if(supplierDoc.getFileName() == null || supplierDoc.getFilePath() == null){
					tempLs.remove(supplierDoc);
				}
			}
			gstSupplier.setSmGstSupplierDocs(tempLs);
		}
		
		SmSupplier supplier = getManagedBean().getSmSupp();
		gstSupplier.setSmSupplier(getManagedBean().getSmSupp());
		supplier.getSmGstSuppliers().add(gstSupplier);
	}
	
	/**
	 * Create Gst Document List
	 */
	private void prepareGstDoc(){
		SmGstSupplier gstSupplier = getManagedBean().getSmGstSupplier();
		List<PmDocument> docLs = retrieveGstDocFromPmDocument();
		gstSupplier.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());

		if(docLs.size() > 0){
			for(PmDocument document : docLs){
				SmGstSupplierDoc gstSupplierDoc = new SmGstSupplierDoc();
				gstSupplierDoc.setSmGstSupplier(gstSupplier);
				gstSupplierDoc.setPmDocument(document);
				gstSupplier.getSmGstSupplierDocs().add(gstSupplierDoc);
			}
		}
	}
	
	/**
	 * set Selected Gst Doc
	 * @param gstSupplierDoc
	 */
	public void setSelectedGstDoc(SmGstSupplierDoc gstSupplierDoc){
		this.getManagedBean().setSelectedGstSupplierDoc(gstSupplierDoc);
	}

	/**
	 * Validate user login
	 */
	private void validateLoginSession(){
		String screenName = new FacesScopes().getUserScreenName();
		FacesContext fc = FacesContext.getCurrentInstance();
		if (screenName != null && StringUtils.isNotEmpty(screenName)) {
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					MessageUtils.getMessage(MessageConst.ERROR_721045,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);			

		}else{
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, signUp);
		}
	}

	public boolean isGmapDisabled() {
		gmapDisabled = false;
		
		if(getManagedBean().getSmBusinessAdd() != null){
			SmAddress bizAddress = getManagedBean().getSmBusinessAdd();

			if(bizAddress.getPmCountry() == null || bizAddress.getPmDistrict() == null || 
					bizAddress.getPmState() == null || bizAddress.getPmCity() == null || 
					bizAddress.getAddress1() == null ){
					gmapDisabled = true;
			}else{
				if(StringUtils.isBlank(bizAddress.getAddress1()) || bizAddress.getPmCountry().getCountryId() == null ||
						bizAddress.getPmDistrict().getDistrictId() == null || bizAddress.getPmState().getStateId() == null ||
						bizAddress.getPmCity().getCityId() == null){
					gmapDisabled = true;
				}

			}
		}else{
			gmapDisabled = true;
		}

		return gmapDisabled;
	}
	
	//captcha test method
	public void check(ActionEvent e){
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Your Code Is Correct !",null));
	}

	public boolean isShowNewChanges() {
		String newChanges = EpPropertiesUtils.getInstance().getProperty("sm.show.new.changes");
		boolean showNewChanges = Boolean.valueOf(newChanges);
		return showNewChanges;
	}

	/**
	 * retrieve Basic Account Status
	 * @param
	 */
	public void retrieveBasicAccountStatus(){
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		FacesContext fc = FacesContext.getCurrentInstance();
		if(pmUser.getUserId().longValue() != 1 || pmUser == null){
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setUserId(pmUser.getUserId());

			SmSupplier smSupp = SmApplicationUtil.getCurrentSupplier();
			log.info("-------smResDTO-------" + smSupp.getSupplierId()); 

			getManagedBean().setSmSupp(smSupp);
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, basicAccDeactivation);
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,
					null,
					MessageUtils.getMessage(MessageConst.ERROR_721017, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/onlinereg/outputMsg.xhtml");
		}
	}
	
	/**
	 * Update basic account status
	 */
	public String updateBasicAccountStatus(){
		if(this.getManagedBean().getSmSupp().getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_SUSPENDED){
			if(SmApplicationUtil.hasActiveTransactions(this.getManagedBean().getSmSupp())){
				RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				throw new EpException(MessageConst.ERROR_721035);
			}else{
				RegistrationReqDTO reqDTO = new RegistrationReqDTO();
				reqDTO.setSmSupp(getManagedBean().getSmSupp());
				IRegistrationSO reqSO = SmServiceLookup.getRegistrationSO();
				try {
					reqSO.updateOnlineAcc(reqDTO);			
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					SmCommonUtil.callDeactivationSuccessDialog(MessageConst.INFO_721034);
					return null;
				} catch(Exception ex){
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
					throw new EpException(ex.getMessage());
				}finally{
					RequestContext.getCurrentInstance().execute("submissionConfirmDlg.hide()");
				}
			}
		}else{
			return acknowledgement;
		}
	}
	
	/**
	 * Save Basic Account Status
	 */
	public String saveBasicAccountStatus(){
		boolean deactivateAccount = this.getManagedBean().getSmSupp().getRecordStatusFlg()
				.intValue() == DbValueConst.RECORD_STATUS_SUSPENDED ? true
				: false;
		
		if(deactivateAccount){
			RequestContext.getCurrentInstance().execute("submissionConfirmDlg.show()");
			return null;
		}else{
			//updateBasicAccountStatus();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					null,
					MessageUtils.getMessage(MessageConst.INFO_721013, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			
			return acknowledgement;
		}
		
	}
	
	/**
	 * Delete GST
	 * 
	 * @param SmGstSupplier
	 *            gst
	 */
	public void deleteGst(SmGstSupplier gst) {
		gst.setGstRegNo(null);
		gst.setGstEffDate(null);
		gst.setGstEndDate(null);
		gst.setSmGstSupplierDocs(new ArrayList<SmGstSupplierDoc>());
		SmApplicationUtil.initGstSuppDocs(gst);
	}
	
	public void removeGstUploadedFile(){
		SmGstSupplierDoc gstDoc = getManagedBean().getSelectedGstSupplierDoc();
		gstDoc.setFileName(null);
		gstDoc.setFilePath(null);

	}
	
	public Date getMinGstEndDate() {
		if (getManagedBean().getSmGstSupplier() != null
				&& getManagedBean().getSmGstSupplier().getGstEffDate() != null) {
			return getManagedBean().getSmGstSupplier().getGstEffDate();
		} else {
			CommonConstants commonConst = new CommonConstants();
			return commonConst.getGST_LAUNCH_DATE();
		}
	}
	
	public String getOwnerLabel(){
		String bizType = getManagedBean().getSmSupp().getBusinessType();
		if(bizType != null){
			if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_KOPERASI_LAIN2)){
				return "lbl.sm.director.name";
			}else if(bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERSATUAN) || 
					bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_PERTUBUHAN_LAIN2) || 
							bizType.equalsIgnoreCase(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_PROFESSIONAL_BODY)){
				return "lbl.sm.chairman.name";
			}else{
				return "lbl.sm.ownername";
			}
		}else{
			return "lbl.sm.ownername";
		}
	}
	
	public void ownerNationalityOnChange(){
		SmSupplierOwner owner = getManagedBean().getSmSuppOwner();
		Long countryId = owner.getNationality().getCountryId();
		
		if(countryId != null){
			PmCountry country = SmCommonUtil.retrievePmCountryById(countryId);
			if(country.getCountryCode().equalsIgnoreCase(SupplierManagementConst.COUNTRY_MALAYSIA_CODE)){
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.IDENTIFICATION_TYPES);
				reqDTO.setParameterCode(ParameterConst.IDENTIFICATION_TYPES_IC);
				PmParameter identObj = SmServiceLookup.getParameterSO().retrieveParameterByTypeAndCode(reqDTO).getPmParameter();
				owner.setIdentificationTypeId(identObj.getParameterId());
				owner.setIdentificationType(identObj);
				getManagedBean().setMyOwner(true);
			}else{
				getManagedBean().setMyOwner(false);
			}
		}else{
			getManagedBean().setMyOwner(false);
		}
	}
}


