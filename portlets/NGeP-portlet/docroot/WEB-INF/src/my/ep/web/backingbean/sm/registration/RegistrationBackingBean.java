package my.ep.web.backingbean.sm.registration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.certbody.ICertBodySO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
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
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends
		BaseBackingBean<RegistrationManagedBean> {

	public static final String SUPPLIER = "SUP";

	public static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public static String newICMasking = "999999999999";
	private static String accountRegistration = "registrationInfo";
	private static String signUp = "signUp";
	private static String firstUserRegistration = "firstUserRegistration";
	private static long nationality = 1;

	public RegistrationBackingBean() {
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		
		if(this.getManagedBean().getApplType() == null){
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
			this.getManagedBean().setSmSuppBanks(
					new ArrayList<SmSupplierBank>());

			SmAppl smAppl = this.getManagedBean().getSmAppl();
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
			smAppl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());

			this.getManagedBean().setSmPersonnel(new SmPersonnel());

			smAppl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());

			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().getSmBusinessAdd()
					.setPmCountry(new PmCountry());
			this.getManagedBean().getSmBusinessAdd().getPmCountry()
					.setCountryId(1L);
			this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
			this.getManagedBean().getSmBusinessAdd()
					.setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmResAdd(new SmAddress());
			this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmResAdd().getPmCountry().setCountryId(1L);
			this.getManagedBean().getSmResAdd().setPmState(new PmState());
			this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmResAdd().setPmCity(new PmCity());

			this.getManagedBean().setSmPermenantAdd(new SmAddress());
			this.getManagedBean().getSmPermenantAdd()
					.setPmCountry(new PmCountry());
			this.getManagedBean().getSmPermenantAdd().getPmCountry()
					.setCountryId(1L);
			this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
			this.getManagedBean().getSmPermenantAdd()
					.setPmDistrict(new PmDistrict());
			this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());

			this.getManagedBean().setSameAddress(false);
			this.getManagedBean().setSmPersonnel(new SmPersonnel());

			this.retrievePmCountryList();
			this.populateStateByAddType("permenant");
			this.populateStateByAddType("residential");
			this.populateStateByAddType("business");
			this.retrieveFinancialOrgList();
			
			log.info("**********permenant" + this.getManagedBean().getPermenantStateLs().size());
			log.info("**********residential" + this.getManagedBean().getResidentialStateLs().size());
			// this.retrievePmBusinessType("BTB");

			// for(PmParameterDesc test :
			// this.getManagedBean().getBusinessTypeLs()){
			// log.info("-------------PmParameter------------"+test.getPmParameter().getParameterCode());
			// log.info("-------------PmParameterDESC------------" +
			// test.getCodeName());
			// }
		}

		if (this.getManagedBean().getApplType().equals("O")) {
			this.retrievePmBusinessType("BTB");
		} else if (this.getManagedBean().getApplType().equals("M")) {
			if (this.getManagedBean().getSmAppl().getSupplierType() != null) {
				if (this.getManagedBean().getSmAppl().getSupplierType()
						.equals("K")) {
					this.retrievePmBusinessType("BTK");
				} else if (this.getManagedBean().getSmAppl().getSupplierType()
						.equals("P")) {
					this.retrievePmBusinessType("BTJ");
				}
			}
		}
		this.getManagedBean().getSmAppl()
				.setApplType(this.getManagedBean().getApplType());
//		this.getManagedBean().setServiceLs(new HashMap<String,String>());
//		this.getManagedBean().getServiceLs().size();
		log.info("-------onlineAccountAgreement end-------------");
		
//		this.getManagedBean().getSmSupp().setServiceCategory("P");
//		log.info("******Service List******"  + this.getManagedBean().getServiceLs().size());
		
		return accountRegistration;
	}

	public String proceedToFirstUserRegistration() {
		return firstUserRegistration;
	}
	
	public String returnToSignUp(){
		return signUp;
	}
	
	public String returnToCompanyInfo(){
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
			this.getManagedBean().setSmSuppCerts(
					new ArrayList<SmSupplierCert>());
			this.retrieveCertBody();
		}

		this.getManagedBean().getSmSuppCerts().add(new SmSupplierCert());
	}

	public void removeProfessionalCert() {
		log.info("-------------------removeProfessionalCert-----------");
		for (SmSupplierCert suppCert : this.getManagedBean()
				.getSelectedSuppCerts()) {
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
		if (smAppl.getApplType().equals("O")) {
			smAppl.setSupplierType("B");
		}
		log.info("----------SupplierType-------------"
				+ smAppl.getSupplierType());
		if (smSupp.getBusinessType().equals("13")) {
			this.getManagedBean().getSmSuppOwner().setIdentificationType("I");
			this.getManagedBean()
					.getSmSuppOwner()
					.setOwnerName(
							this.getManagedBean().getSmAppl()
									.getSmCompanyBasic().getCompanyName());
			this.getManagedBean()
					.getSmSuppOwner()
					.setIdentificationNo(
							this.getManagedBean().getSmSupp().getRegNo());
		}

		this.getManagedBean().getSmSupp().setCompanyName(this.getManagedBean().getSmAppl().getSmCompanyBasic().getCompanyName());
		// smSupp.setSmAppl(smAppl);
		// smAppl.getSmSupplier().setSmAppl(smAppl); //

		smAppl.setSmSupplier(smSupp);
		smAppl.getSmCompanyBasic().setSmAppl(smAppl);
		smAppl.getSmCompanyBasic().setRevNo(0);
		
		// for(SmSupplierBank suppBank :
		// this.getManagedBean().getSmSuppBanks()){
		// suppBank.setSmAppl(smAppl);
		// suppBank.setRevNo(0);
		// smAppl.getSmSupplierBanks().add(suppBank);
		// }

		smSupp.getSmAppls().add(smAppl);
		// smSupp.getSmAppls().get(0).getSmSupplier().setSmAppl(smAppl);
		smSupp.setPmStatus(new PmStatus());
		smSupp.getPmStatus().setStatusId(20101L);
		smSupp.setCreatedBy(1L);
		this.getManagedBean().getSmSuppOwner()
				.setSmSupplier(this.getManagedBean().getSmSupp());
		smSupp.getSmSupplierOwners()
				.add(this.getManagedBean().getSmSuppOwner());
		// smSupp.setSmAppl(smAppl);

		reqDTO.setSmSupp(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);

		if (resDTO.isSuccessful()) {
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Successful Registered.", ""));
			this.getManagedBean().setSmAppl(null);
			this.getManagedBean().setSmSupp(null);
			return "/xhtml/sm/disciplinaryAction/outputMsg";
		} else {
			log.info("-------------------Error SubmitRegistration----------------");
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", ""));
			return "";
		}
	}

	public String updateOnlineAccount() {
		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		// this.getManagedBean().getSmSuppBank()
		// .setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
		// log.info("---------------getSmSuppBank----------------"
		// + this.getManagedBean().getSmSuppBank().getAccountNo());
		// log.info("---------------getSmSuppBank----------------"
		// + this.getManagedBean().getSmSuppBank().getSupplierBankId());
		// this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()
		// .set(0, this.getManagedBean().getSmSuppBank());

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

	// public void viewOnlineAccount() {
	// log.info("--------------------viewOnlineAccount----------------");
	// this.getManagedBean().setSmBusinessAdd(new SmAddress());
	// this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
	//
	// RegistrationReqDTO reqDTO = new RegistrationReqDTO();
	// // reqDTO.setSupplierId(30);
	// reqDTO.setSupplierId(80);
	//
	// IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
	// .getInstance().getServiceObject("sm", IRegistrationSO.class);
	// RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);
	//
	// this.getManagedBean().setSmSupp(resDTO.getSmSupp());
	//
	// this.getManagedBean().setSmSuppOwner(
	// resDTO.getSmSupp().getSmSupplierOwners().get(0));
	// // this.getManagedBean().setSmSuppBank(
	// // resDTO.getSmSupp().getSmAppl().getSmSupplierBanks().get(0));
	// // return "onlineAccount/accountMaintenance";
	// }

	public void addSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks()
		// .add(new SmSupplierBank());
		if (this.getManagedBean().getSmAppl().getSmSupplierBanks() == null) {
			this.getManagedBean().getSmAppl()
					.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		SmSupplierBank smSuppBank = new SmSupplierBank();
		smSuppBank.setPmFinancialOrg(new PmFinancialOrg());
		this.getManagedBean().getSmSuppBanks().add(smSuppBank);
		// this.getManagedBean().getSmSuppBanks().add(new SmSupplierBank());
	}

	public void removeSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks().remove(this.getManagedBean().getSelectedSuppBanks());

		log.info("---removeSupplierBank---");
		for (SmSupplierBank suppBank : this.getManagedBean()
				.getSelectedSuppBanks()) {
			this.getManagedBean().getSmSuppBanks().remove(suppBank);
			log.info("---removeSupplierBank---" + suppBank);
			// suppBank.getPmFinancialOrg().getFinancialOrgId()
		}
	}

	public void checkSameAddress() {
		if (this.getManagedBean().isSameAddress()) {
			this.getManagedBean().setSmResAdd(
					this.getManagedBean().getSmPermenantAdd());
			this.getManagedBean().setResidentialStateLs(
					this.getManagedBean().getPermenantStateLs());
			this.getManagedBean().setResidentialDistrictLs(
					this.getManagedBean().getPermenantDistrictLs());
			this.getManagedBean().setResidentialCityLs(
					this.getManagedBean().getPermenantCityLs());
			log.info("-----------is same address---------------"
					+ this.getManagedBean().getSmPermenantAdd().getAddress1());
		} else {
			log.info("-----------is diff address---------------");
			this.getManagedBean().getSmResAdd().setAddress1(null);
			this.getManagedBean().getSmResAdd().setAddress2(null);
			this.getManagedBean().getSmResAdd().setAddress3(null);
			this.getManagedBean().getSmResAdd().setAddress4(null);
			this.getManagedBean().getSmResAdd().setPostcode(null);
			this.getManagedBean().setResidentialDistrictLs(null);
			this.getManagedBean().setResidentialCityLs(null);

		}
	}

	public void businessTypeOnChange() {
		if (this.getManagedBean().getSmSupp().getBusinessType().equals("I")) {
			log.info("--------------Business Type is Individual------------");
			this.populateStateByAddType("business");
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
		if (this.getManagedBean().getSmSuppOwner().getIdentificationType()
				.equals("I")) {
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

	public void populateStateByAddType(String typeAdd) {
		if (typeAdd.equals("business")) {
			List<PmState> stateLs = this.retrievePmStateList(this
					.getManagedBean().getSmBusinessAdd().getPmCountry()
					.getCountryId());
			this.getManagedBean().setBusinessStateLs(stateLs);
		} else if (typeAdd.equals("residential")) {
			List<PmState> stateLs = this.retrievePmStateList(this
					.getManagedBean().getSmResAdd().getPmCountry()
					.getCountryId());
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
			List<PmDistrict> districtLs = this.retrievePmDistrictList(this
					.getManagedBean().getSmPermenantAdd().getPmState()
					.getStateId());
			this.getManagedBean().setPermenantDistrictLs(districtLs);

			log.info("------permenant LS-------------"
					+ this.getManagedBean().getPermenantDistrictLs().size());
		}
	}

	public void populateCityByAddType(String typeAdd) {
		log.info("---------------City By Type--------------------");
		if (typeAdd.equals("business")) {
			// this.getManagedBean().getSmBusinessAdd().getPmState().getStateId();
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean()
					.getSmBusinessAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setBusinessCityLs(cityLs);
		} else if (typeAdd.equals("residential")) {
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean()
					.getSmResAdd().getPmDistrict().getDistrictId());
			this.getManagedBean().setResidentialCityLs(cityLs);
		} else if (typeAdd.equals("permenant")) {
			List<PmCity> cityLs = this.retrievePmCityList(this.getManagedBean()
					.getSmPermenantAdd().getPmDistrict().getDistrictId());
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

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

		this.getManagedBean().setSmSupp(resDTO.getSmSupp());
		this.getManagedBean().setSmAppl(
				this.getManagedBean().getSmSupp().getSmAppl());
		this.getManagedBean().setSmSuppOwner(
				resDTO.getSmSupp().getSmSupplierOwners().get(0));
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
		this.getManagedBean().getSmBusinessAdd().getPmCountry()
				.setCountryId(1L);
		this.getManagedBean().getSmBusinessAdd().setPmState(new PmState());
		this.getManagedBean().getSmBusinessAdd()
				.setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmBusinessAdd().setPmCity(new PmCity());

		this.getManagedBean().setSmResAdd(new SmAddress());
		this.getManagedBean().getSmResAdd().setPmCountry(new PmCountry());
		this.getManagedBean().getSmResAdd().getPmCountry().setCountryId(1L);
		this.getManagedBean().getSmResAdd().setPmState(new PmState());
		this.getManagedBean().getSmResAdd().setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmResAdd().setPmCity(new PmCity());

		this.getManagedBean().setSmPermenantAdd(new SmAddress());
		this.getManagedBean().getSmPermenantAdd().setPmCountry(new PmCountry());
		this.getManagedBean().getSmPermenantAdd().getPmCountry()
				.setCountryId(1L);
		this.getManagedBean().getSmPermenantAdd().setPmState(new PmState());
		this.getManagedBean().getSmPermenantAdd()
				.setPmDistrict(new PmDistrict());
		this.getManagedBean().getSmPermenantAdd().setPmCity(new PmCity());

		this.getManagedBean().setSameAddress(false);
		this.getManagedBean().setSmPersonnel(new SmPersonnel());

		this.retrievePmCountryList();
		this.populateStateByAddType("permenant");
		this.populateStateByAddType("residential");
		this.populateStateByAddType("business");
		this.retrieveFinancialOrgList();

		// this.getManagedBean().setSmSuppBank(
		// resDTO.getSmSupp().getSmAppl().getSmSupplierBanks().get(0));
		// return "onlineAccount/accountMaintenance";
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
