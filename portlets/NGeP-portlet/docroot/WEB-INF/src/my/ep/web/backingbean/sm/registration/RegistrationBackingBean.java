package my.ep.web.backingbean.sm.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
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
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityReqDTO;
import my.ep.dto.sm.common.myidentity.MyIdentityResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmBusinessNature;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
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
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends BaseBackingBean<RegistrationManagedBean>{

	public static final String SUPPLIER = "SUP";

	public static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public static String newICMasking = "999999999999";
	private static String accountRegistration = "registrationInfo";
	private static String signUp = "signUp";
	private static String firstUserRegistration = "firstUserRegistration";
	private static int defaultRevNo = 1;
	private static String defaultAccPurpose = "R";
	private static Map<Long, Object> bizNatureL5Map;

	public RegistrationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}

		if (this.getManagedBean().getApplType() == null) {
			this.getManagedBean().setApplType("M");
		}

	}

	public String proceedToCompanyInfo() {
		log.info("-------onlineAccountAgreement start-------------");
		if (this.getManagedBean().getSmSupp() == null) {
			log.info("-------RegistrationBackingBean is NUll-------------");
			this.getManagedBean().setSmSupp(new SmSupplier());

			SmSupplier smSupp = this.getManagedBean().getSmSupp();
			smSupp.setSmAppls(new ArrayList<SmAppl>());
			smSupp.setSmSupplierOwners(new ArrayList<SmSupplierOwner>());

			this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			this.getManagedBean().setSmAppl(new SmAppl());
			this.getManagedBean().setSmSuppBanks(new ArrayList<SmSupplierBank>());

			SmAppl smAppl = this.getManagedBean().getSmAppl();
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
			smAppl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());

			this.getManagedBean().setSmPersonnel(new SmPersonnel());
			this.getManagedBean().getSmPersonnel().setPmCountry(new PmCountry());

			smAppl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
			
			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().getSmBusinessAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmBusinessAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
			this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmResAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmPermenantAdd(new SmAddress());
			this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmPermenantAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
			this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());

			this.getManagedBean().setSameAddress(false);
			this.getManagedBean().setSmPersonnel(new SmPersonnel());
			this.getManagedBean().getSmPersonnel().setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
			this.getManagedBean().setSuppBusinessNatureLs(new ArrayList<SmSupplierBusiness>());
			
			this.retrievePmCountryList();
			this.populateStateByAddType("permenant");
			this.populateStateByAddType("residential");
			this.populateStateByAddType("business");
			this.retrieveFinancialOrgList();
			
			//this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());
			this.getManagedBean().setBusinessNatureL1(this.retrieveBusinessNatureByLevelParent("10",0));
			this.retrieveBusinessNatureL5Map();
			this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());	
			this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(new PmBusinessNature());

			log.info("**********permenant"+ this.getManagedBean().getPermenantStateLs().size());
			log.info("**********residential" + this.getManagedBean().getResidentialStateLs().size());
		}

		if (this.getManagedBean().getApplType().equals("B")) {
			this.retrievePmBusinessType("BTB");
		} else if (this.getManagedBean().getApplType().equals("M")) {
			if (this.getManagedBean().getSmAppl().getSupplierType() != null) {
				if (this.getManagedBean().getSmAppl().getSupplierType().equals("K")) {
					this.retrievePmBusinessType("BTK");
				} else if (this.getManagedBean().getSmAppl().getSupplierType().equals("P")) {
					this.retrievePmBusinessType("BTJ");
				}
			}
		}
		this.getManagedBean().getSmAppl().setApplType(this.getManagedBean().getApplType());
		// this.getManagedBean().setServiceLs(new HashMap<String,String>());
		// this.getManagedBean().getServiceLs().size();
		log.info("-------onlineAccountAgreement end-------------");

		// this.getManagedBean().getSmSupp().setServiceCategory("P");
		// log.info("******Service List******" +
		// this.getManagedBean().getServiceLs().size());

		return accountRegistration;
	}

	public String proceedToFirstUserRegistration() {
		return firstUserRegistration;
	}

	public String returnToSignUp() {
		return signUp;
	}

	public String returnToCompanyInfo() {
		return accountRegistration;
	}

	public void identificationTypeOnChange() {
		/*
		 * log.info(
		 * "-----------------------identificationTypeOnChange---------------" +
		 * this.getManagedBean().getUserIdentificationType());
		 * 
		 * if
		 * (this.getManagedBean().getPmOrgProfile().getPmUserOrg().getPmUser()
		 * .getIdentificationType().equals("0")) {
		 * this.getManagedBean().setIdTypeMasking(newICMasking); } else {
		 * this.getManagedBean().setIdTypeMasking(""); }
		 */
	}

	public void nationalityOnChange() {
		/*
		 * if
		 * (this.getManagedBean().getPmOrgProfile().getPmUserOrg().getPmUser()
		 * .getNationalityId() == 0) {
		 * this.getManagedBean().getPmOrgProfile().getPmUserOrg().getPmUser()
		 * .setIdentificationType("0");
		 * log.info("-----------------------nationalityOnChange---------------"
		 * ); } else if (this.getManagedBean().getPmOrgProfile().getPmUserOrg()
		 * .getPmUser().getNationalityId() == 1) {
		 * this.getManagedBean().getPmOrgProfile().getPmUserOrg().getPmUser()
		 * .setIdentificationType("1"); }
		 */
	}

	public void addProfessionalCert() {
		log.info("-------------------addProfessionalCert-----------"
				+ this.getManagedBean().getSmSuppCerts());
		if (this.getManagedBean().getSmSuppCerts() == null) {
			this.getManagedBean().setSmSuppCerts(new ArrayList<SmSupplierCert>());
			this.retrieveCertBody();
		}

		this.getManagedBean().getSmSuppCerts().add(new SmSupplierCert());
	}

	public void removeProfessionalCert() {
		log.info("-------------------removeProfessionalCert-----------");
		for (SmSupplierCert suppCert : this.getManagedBean().getSelectedSuppCerts()) {
			this.getManagedBean().getSmSuppCerts().remove(suppCert);
			log.info("---removeSupplierCert---" + suppCert);
			// suppBank.getPmFinancialOrg().getFinancialOrgId()
		}
	}

	public String submitRegistration() {
		log.info("----------Submit Registration-------------");

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		SmAppl smAppl = this.getManagedBean().getSmAppl();
		SmSupplierOwner smSuppOwner = this.getManagedBean().getSmSuppOwner();
		SmPersonnel smPersonnel = this.getManagedBean().getSmPersonnel();
		SmAddress smPermAdd = this.getManagedBean().getSmPermenantAdd();
		SmAddress smResAdd = this.getManagedBean().getSmResAdd();

		if (smAppl.getApplType().equals("B")) {
			smAppl.setSupplierType("B");
		}

		if (smSupp.getBusinessType().equals("13")) {
			smSuppOwner.setIdentificationType("I");
			smSuppOwner.setOwnerName(smAppl.getSmCompanyBasic().getCompanyName());
			smSuppOwner.setIdentificationNo(smSupp.getRegNo());
			this.getManagedBean().setSmBusinessAdd(this.getManagedBean().getSmPermenantAdd());
		}

		smSupp.setCompanyName(smAppl.getSmCompanyBasic().getCompanyName());
		smAppl.setSmSupplier(smSupp);
		smAppl.getSmCompanyBasic().setSmAppl(smAppl);
		smAppl.getSmCompanyBasic().setRevNo(defaultRevNo);
		smAppl.getSmCompanyBasic().setSmCompanyAddresses(new ArrayList<SmCompanyAddress>());
		
		/* add personnel And personnel address */
		smPersonnel.setRevNo(defaultRevNo);
		SmPersonnelAddress personnelAddRes = new SmPersonnelAddress();
		SmPersonnelAddress personnelAddPerm = new SmPersonnelAddress();
		
		personnelAddRes.setAddressType("R");
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
		
		personnelAddPerm.setAddressType("P");
		personnelAddPerm.setSmAddress(smPermAdd);
		personnelAddPerm.setSmPersonnel(smPersonnel);
		smPermAdd.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		smPermAdd.getSmPersonnelAddresses().add(personnelAddPerm);
		smPersonnel.getSmPersonnelAddresses().add(personnelAddPerm);
		
		smPersonnel.setSmAppl(smAppl);
		smAppl.getSmPersonnels().add(smPersonnel);	
		
		for(SmPersonnelAddress spa : smPersonnel.getSmPersonnelAddresses()){
			log.info("****************Object**************" + spa.getSmAddress().getSmPersonnelAddresses().size());
			log.info("***************Personnel Name**************" + spa.getSmAddress().getSmPersonnelAddresses().get(0).getSmPersonnel().getName());
			log.info("**************SPA country code****************" + spa.getSmAddress().getPmCountry().getCountryId());
			log.info("**************SPA district code****************" + spa.getSmAddress().getPmDistrict().getDistrictId());
			log.info("**************SPA City code****************" + spa.getSmAddress().getPmCity().getCityId());
			log.info("**************SPA State****************" + spa.getSmAddress().getPmState().getStateId());
			log.info("**************SPA postcode****************" + spa.getSmAddress().getPostcode());
			log.info("**************SPA Add Type****************" + spa.getSmAddress().getSmPersonnelAddresses().get(0).getAddressType());
			log.info("");
			
		}
		
		/* add company address */
		SmCompanyAddress smCompAdd = new SmCompanyAddress();
		smCompAdd.setAddressType("C");
		smCompAdd.setSmCompanyBasic(smAppl.getSmCompanyBasic());
		smCompAdd.setSmAddress(this.getManagedBean().getSmBusinessAdd());
		smAppl.getSmCompanyBasic().getSmCompanyAddresses().add(smCompAdd);

		for(SmSupplierBank suppBank : this.getManagedBean().getSmSuppBanks()){
			suppBank.setSmAppl(smAppl);
			suppBank.setRevNo(defaultRevNo);
			suppBank.setAccountPurpose(defaultAccPurpose);
			smAppl.getSmSupplierBanks().add(suppBank);
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
		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_INFO,
					"Successful Registered.<br />An activation information has been sent to your email. You need to activate your account within 7 days.",
					""));
			this.getManagedBean().setSmAppl(null);
			this.getManagedBean().setSmSupp(null);
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			log.info("-------------------Error SubmitRegistration----------------");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}

	public String updateOnlineAccount() {
		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Your account has been updated successfully.", ""));
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}
	}

	public void addSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks()
		// .add(new SmSupplierBank());
		if (this.getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			this.getManagedBean().getSmAppl().setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		this.getManagedBean().getSmSuppBanks().add(smSuppBank);
		// this.getManagedBean().getSmSuppBanks().add(new SmSupplierBank());
	}

	public void removeSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(this.getManagedBean().getSelectedSuppBanks());
		log.info("---removeSupplierBank---");
		for (SmSupplierBank suppBank : this.getManagedBean().getSelectedSuppBanks()) {
			this.getManagedBean().getSmSuppBanks().remove(suppBank);
			log.info("---removeSupplierBank---" + suppBank);
			// suppBank.getPmFinancialOrg().getFinancialOrgId()
		}
	}
	
	public void addBusinessNature() {
		log.info("-----------addBusinessNature--------------");
		this.getManagedBean().setBizIdL1(new Long(0L));
		this.getManagedBean().setBizIdL2(new Long(0L));
		this.getManagedBean().setBizIdL3(new Long(0L));
		this.getManagedBean().setBizIdL4(new Long(0L));
		this.getManagedBean().setBusinessNatureL2(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
		this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
		
		this.getManagedBean().setSuppBusinessNature(new SmSupplierBusiness());	
		this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(new PmBusinessNature());
		
	}
	
	public void addSupplierBusinessNature(){
		log.info("----------addSupplierBusiness Nature--------------" + bizNatureL5Map.size());
		log.info("----------addSupplierBusiness Nature--------------" + this.getManagedBean().getSuppBusinessNature().getPmBusinessNature().getBusinessNatureId());
		
		PmBusinessNature bizNatObj = (PmBusinessNature) bizNatureL5Map.get(this.getManagedBean().getSuppBusinessNature().getPmBusinessNature().getBusinessNatureId());
		
		this.getManagedBean().getSuppBusinessNature().setPmBusinessNature(bizNatObj);
		this.getManagedBean().getSuppBusinessNatureLs().add(this.getManagedBean().getSuppBusinessNature());
	}
	
	public void removeBusinessNature() {
		log.info("---------------removeBusinessNature count------------" + this.getManagedBean().getSelectedBusinessNatureLs().length);
		for (SmSupplierBusiness suppBusinessNature : this.getManagedBean().getSelectedBusinessNatureLs()) {
			log.info("---------------removeBusinessNature.------------" + suppBusinessNature);
			this.getManagedBean().getSuppBusinessNatureLs().remove(suppBusinessNature);
		}
	}
	
	public void editBusinessNature(){
		log.info("-----editBusinessNature-----" + this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getBusinessNatureId());
		log.info("********edit Parent ID*********"  + this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getParentBusinessNature().getBusinessNatureId());
		
		List<PmBusinessNature> parentObjList = retrieveBusinessNatureByLevelParent(null,this.getManagedBean().getSelectedBusinessNature().getPmBusinessNature().getParentBusinessNature().getBusinessNatureId());
		//List<PmBusinessNature> parentObj = retrieveBusinessNatureByLevelParent(null,this.get);
	}
	
	public void businessLvlOnChange(String bizLvl){
		switch (bizLvl) {
			case "20" : 
				this.getManagedBean().setBusinessNatureL2(retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL1()));
				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
			break;
			case "30" : 
				this.getManagedBean().setBusinessNatureL3(retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL2()));
				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
			break;
			case "40" : 
				this.getManagedBean().setBusinessNatureL4(retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL3()));
				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
			break;
			
			case "50" : 
				this.getManagedBean().setBusinessNatureL5(retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL4()));
//				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
			break;
		}
	}
	
	
//	public void businessLvlOnChange(String bizLvl){
//		switch (bizLvl) {
//			case "20" : 
//				retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL1());
//				this.getManagedBean().setBusinessNatureL3(new ArrayList<PmBusinessNature>());
//				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
//				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
//			break;
//			case "30" : 
//				retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL2());
//				this.getManagedBean().setBusinessNatureL4(new ArrayList<PmBusinessNature>());
//				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
//			break;
//			case "40" : 
//				retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL3());
//				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
//			break;
//			
//			case "50" : 
//				retrieveBusinessNatureByLevelParent(bizLvl,this.getManagedBean().getBizIdL4());
////				this.getManagedBean().setBusinessNatureL5(new ArrayList<PmBusinessNature>());
//			break;
//		}
//	}

	public void businessTypeOnChange() {
		if (this.getManagedBean().getSmSupp().getBusinessType().equals("13")) {
			log.info("--------------Business Type is Individual------------");
			// this.populateStateByAddType("business");
		} else {
			log.info("-----------Business Type is not Individual------------"
					+ this.getManagedBean().getSmSupp().getBusinessType());
		}
	}

	public void suppTypeOnChange() {
		if (this.getManagedBean().getSmAppl().getSupplierType() != null) {
			if (this.getManagedBean().getSmAppl().getSupplierType().equals("K")) {
				this.retrievePmBusinessType("BTK");
			} else if (this.getManagedBean().getSmAppl().getSupplierType()
					.equals("P")) {
				this.retrievePmBusinessType("BTJ");
			}
		}
	}

	public void suppOwnerIdTypeOnChange() {
		if (this.getManagedBean().getSmSuppOwner().getIdentificationType().equals("I")) {
			this.getManagedBean().setSuppOwnerIdMask(newICMasking);
		} else {
			this.getManagedBean().setSuppOwnerIdMask("");
		}
	}

	public void firstIdTypeOnChange() {
		if (this.getManagedBean().getSmSuppOwner().getIdentificationType()
				.equals("I")) {
			this.getManagedBean().setSuppOwnerIdMask(newICMasking);
		} else {
			this.getManagedBean().setSuppOwnerIdMask("");
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

		ICertBodySO reqSO = (ICertBodySO) BusinessDelegate.getInstance()
				.getServiceObject("pm", ICertBodySO.class);
		CertBodyResDTO resDTO = reqSO.retrieveProfessionalBodyList();

		log.info("*****certbody count" + resDTO.getCertBodyList().size());
		this.getManagedBean().setProfCertBodyLs(resDTO.getCertBodyList());
	}
	
//	private void retrieveBusinessNatureByLevelParent(String busLvl, long parentId){
//		BusinessNatureReqDTO reqDTO = new BusinessNatureReqDTO();
//		if(busLvl != "10"){
//			reqDTO.setParentBusinessNatureId(parentId);
//		}
//		reqDTO.setBusinessLevel(busLvl);
//		IBusinessNatureSO reqSO = (IBusinessNatureSO) BusinessDelegate.getInstance().getServiceObject("pm", IBusinessNatureSO.class);
//		BusinessNatureResDTO resDTO = reqSO.retrieveBusinessNatureListByLevel(reqDTO);
//
//		switch (busLvl) {
//			case "10" : this.getManagedBean().setBusinessNatureL1(resDTO.getBusinessNatureList());
//			break;
//			case "20" : this.getManagedBean().setBusinessNatureL2(resDTO.getBusinessNatureList());
//			break;
//			case "30" : this.getManagedBean().setBusinessNatureL3(resDTO.getBusinessNatureList());
//			break;
//			case "40" : this.getManagedBean().setBusinessNatureL4(resDTO.getBusinessNatureList());
//			break;
//			case "50" : this.getManagedBean().setBusinessNatureL5(resDTO.getBusinessNatureList());
//			break;
//		}
//
//		log.info("*********Business Nature" + busLvl +"***********" + resDTO.getBusinessNatureList().size());
//	}

	
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
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this
					.getManagedBean().getSmBusinessAdd().getPmState()
					.getStateId());
			this.getManagedBean().setBusinessDistrictLs(districtLs);
		} else if (typeAdd.equals("residential")) {
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this
					.getManagedBean().getSmResAdd().getPmState().getStateId());
			this.getManagedBean().setResidentialDistrictLs(districtLs);
		} else if (typeAdd.equals("permenant")) {
			log.info("------------permenant----------------"
					+ this.getManagedBean().getSmPermenantAdd().getPmState()
					.getStateId());
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this.getManagedBean().getSmPermenantAdd().getPmState().getStateId());
			this.getManagedBean().setPermenantDistrictLs(districtLs);

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
		log.info("--------------------viewOnlineAccount----------------");
		// this.getManagedBean().setSmBusinessAdd(new SmAddress());
		// this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		// reqDTO.setSupplierId(30);
		reqDTO.setSupplierId(80);

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

		this.getManagedBean().setSmSupp(resDTO.getSmSupp());
		this.getManagedBean().setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
		this.getManagedBean().setSmSuppOwner(resDTO.getSmSupp().getSmSupplierOwners().get(0));
		
		log.info("---------Supplier Type-----------"
				+ this.getManagedBean().getSmAppl().getSupplierType());

		if (this.getManagedBean().getSmAppl().getSupplierType() != null) {
			if (this.getManagedBean().getSmAppl().getSupplierType().equals("B")) {
				this.retrievePmBusinessType("BTB");
			} else if (this.getManagedBean().getSmAppl().getSupplierType()
					.equals("K")) {
				this.retrievePmBusinessType("BTK");
			} else if (this.getManagedBean().getSmAppl().getSupplierType()
					.equals("P")) {
				this.retrievePmBusinessType("BTJ");
			}
		}

		this.getManagedBean().setSmBusinessAdd(new SmAddress());
		this.getManagedBean().getSmBusinessAdd().setPmCountry(new PmCountry());
		this.getManagedBean().getSmBusinessAdd().getPmCountry().setCountryId(1L);
		this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
		this.getManagedBean().getSmBusinessAdd().setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());

		this.getManagedBean().setSmResAdd(new SmAddress());
		this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
		this.getManagedBean().getSmResAdd().getPmCountry().setCountryId(1L);
		this.getManagedBean().getSmResAdd().setPmState(new PmState());
		this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmResAdd().setPmCity(new PmCity());

		this.getManagedBean().setSmPermenantAdd(new SmAddress());
		this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
		this.getManagedBean().getSmPermenantAdd().getPmCountry().setCountryId(1L);
		this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
		this.getManagedBean().getSmPermenantAdd().setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());

		this.getManagedBean().setSameAddress(false);
		this.getManagedBean().setSmPersonnel(new SmPersonnel());

		this.retrievePmCountryList();
		this.populateStateByAddType("permenant");
		this.populateStateByAddType("residential");
		this.populateStateByAddType("business");
		this.retrieveFinancialOrgList();
	}

	public void retrievePmBusinessType(String pmType) {
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(pmType);
		reqDTO.setLanguageCode("en");
		IParameterSO reqSO = (IParameterSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IParameterSO.class);
		ParameterResDTO resDTO = reqSO.retrieveParameterDescListByType(reqDTO);

		this.getManagedBean().setBusinessTypeLs(resDTO.getParameterDescList());
	}
	

	public void imageUpload(FileUploadEvent event) throws Exception {
		log.info("------CB HK-------");
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
	
	// public void myIdentityVerification() {
	// log.info("-------myIdentityVerification-----------");
	// RetrieveMyIdentityServicePortType_ServiceLocator locator = new
	// RetrieveMyIdentityServicePortType_ServiceLocator();
	// locator.setRetrieveMyIdentityServicePortTypePortEndpointAddress("http://192.168.120.14:8011/RetrieveMyIdentityService/Proxy_Services/RetrieveMyIdentity");
	// // EPMFRq_Type data = new EPMFRq_Type();
	// // RetrieveMyIdentityRq_Type rqType = new RetrieveMyIdentityRq_Type();
	// // rqType.setUserID("123456");
	// // data.setRetrieveMyIdentityRq(rqType);
	//
	// try {
	// String URL =
	// "http://192.168.120.14:8011/RetrieveMyIdentityService/Proxy_Services/RetrieveMyIdentity";
	//
	// RetrieveMyIdentityServicePortType_ServiceLocator service = new
	// RetrieveMyIdentityServicePortType_ServiceLocator();
	// service.setRetrieveMyIdentityServicePortTypePortEndpointAddress(URL);
	//
	// RetrieveMyIdentityServicePortType_PortType port = service
	// .getRetrieveMyIdentityServicePortTypePort();
	//
	// RetrieveMyIdentityServiceBindingStub binding =
	// (RetrieveMyIdentityServiceBindingStub) port;
	// binding.setTimeout(1000);
	//
	// RqUID_Type uid = new RqUID_Type();
	// uid.setAsyncRqUID("String");
	// uid.setRqUID("String");
	//
	// RqHeader_Type rqHdr = new RqHeader_Type();
	// rqHdr.setConsumerID("String");
	// rqHdr.setGovOrgUnit("String");
	// rqHdr.setIpAddress("String");
	// rqHdr.setSegment("String");
	// rqHdr.setUID(uid);
	//
	// RetrieveMyIdentityRq_Type retRq = new RetrieveMyIdentityRq_Type();
	// retRq.setICNumber("12345");
	//
	// EPMFRq_Type rq = new EPMFRq_Type();
	// rq.setRqHeader(rqHdr);
	// rq.setRetrieveMyIdentityRq(retRq);
	//
	// EPMFRs_Type epmfrs = port.inquire(rq);
	//
	// System.out
	// .println(epmfrs.getRsHeader().getStatus().getStatusCode());
	// System.out.println(epmfrs.getRetrieveMyIdentityRs().getName());
	// System.out.println(epmfrs.getRetrieveMyIdentityRs()
	// .getPermanentAddrStateCode());
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private void sendEmail() {
	// System.out.println("initiateTask******");
	// INtfSO ntfSO = (INtfSO) EpNtfBusinessDelegate.getInstance()
	// .getOSBServiceObject(INtfSO.class);
	//
	// NtfRequestDTO reqDTO = new NtfRequestDTO();
	// reqDTO.setTemplateId("399999");
	// reqDTO.setSenderId("4");
	// // reqDTO.setSendEmailOnly(true);
	//
	// List<String> usrLst = new ArrayList<String>();
	// usrLst.add("27");
	// usrLst.add("28");
	// reqDTO.setUserIdArr(usrLst.toArray(new String[usrLst.size()]));
	//
	// List<String> subParaLst = new ArrayList<String>();
	// subParaLst.add("#SM#");
	// reqDTO.setSubParaArr(subParaLst.toArray(new String[subParaLst.size()]));
	// List<String> cntParaLst = new ArrayList<String>();
	// cntParaLst.add("Jenson");
	//
	// cntParaLst.add("http://google.com.my/andrew");
	// cntParaLst.add("U_0001_andrew");
	// cntParaLst.add("23434");
	// reqDTO.setCntParaArr(cntParaLst.toArray(new String[cntParaLst.size()]));
	//
	// ntfSO.processAndPublish(reqDTO);
	// }
}
