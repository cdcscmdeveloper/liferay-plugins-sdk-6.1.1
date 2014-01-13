package my.ep.web.backingbean.sm.registration;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends
		BaseBackingBean<RegistrationManagedBean> {

	public static final String SUPPLIER = "SUP";

	public static Logger log = Logger.getLogger(RegistrationBackingBean.class);
	public static String newICMasking = "999999999999";
	private static String accountRegistration = "registrationInfo";
	// private static String accountRegistration = "testSignUp";
	private static String firstUserRegistration = "firstUserRegistration";
	private static int nationality = 1;
	private static long tempId = 1;

	public RegistrationBackingBean() {
		// if (MDC.get(LogKey.USER_ID) == null) {
		// MDC.put(LogKey.USER_ID, 1L);
		// }

	}

	public void proceedToCompanyInfo() {
		/*
		 * if (this.getManagedBean().getPmOrgProfile().getSmSupplier()
		 * .getBusinessType().equals("Ind")) { return "firstUserRegistration"; }
		 * else { return "corporateAccount"; }
		 */

		// return "firstUserRegistration";
	}

	public String proceedToFirstUserRegistration() {
		return "";
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

	// public String getRegistrationType() {
	// if (this.getManagedBean().getRegType() == "Basic") {
	//
	// } else if (this.getManagedBean().getRegType() == "MOF") {
	//
	// }
	//
	// return "";
	// }

	public String onlineAccountAgreement() {
		log.info("-------onlineAccountAgreement start-------------");
		if (this.getManagedBean().getSmAppl() == null) {
			this.getManagedBean().setSmAppl(new SmAppl());
			SmAppl smAppl = this.getManagedBean().getSmAppl();
			smAppl.setSmSupplier(new SmSupplier());
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
			smAppl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
			smAppl.setSmCompanyBasic(new SmCompanyBasic());
			smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
			
			this.retrievePmCountryList();
			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().getSmBusinessAdd().setPmCountry(new PmCountry());
			this.getManagedBean().getSmBusinessAdd().getPmCountry().setCountryId(1);
		}
		
		this.getManagedBean().getSmAppl().setApplType(this.getManagedBean().getApplType());
		log.info("-------onlineAccountAgreement end-------------");
		return accountRegistration;
	}

	public void addProfessionalCert() {
		log.info("-------------------addProfessionalCert-----------"
				+ this.getManagedBean().getSmSuppCerts());

		this.getManagedBean().getSmSuppCerts().add(new SmSupplierCert());
		/*
		 * if (this.getManagedBean().getPmOrgProfile().getSmSupplier()
		 * .getSmSupplierCerts() == null) {
		 * this.getManagedBean().getPmOrgProfile().getSmSupplier()
		 * .setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		 * 
		 * } SmSupplierCert suppCert = new SmSupplierCert();
		 * 
		 * this.getManagedBean().getPmOrgProfile().getSmSupplier()
		 * .getSmSupplierCerts().add(suppCert);
		 */
	}

	public void removeProfessionalCert() {
		log.info("-------------------removeProfessionalCert-----------");
		/*
		 * this.getManagedBean().getPmOrgProfile().getPmUserOrg().getPmUser()
		 * .getNationalityId();
		 */
	}

	public String submitRegisteration() {
		return "";
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

		reqDTO.setSmSupplier(this.getManagedBean().getSmSupp());
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

	public void viewOnlineAccount() {
		log.info("--------------------viewOnlineAccount----------------");
		this.getManagedBean().setSmBusinessAdd(new SmAddress());
		this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		// reqDTO.setSupplierId(30);
		reqDTO.setSupplierId(80);

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.retrieveSuppOnlineAcc(reqDTO);

		this.getManagedBean().setSmSupp(resDTO.getSmSupp());

		this.getManagedBean().setSmSuppOwner(
				resDTO.getSmSupp().getSmSupplierOwners().get(0));
		// this.getManagedBean().setSmSuppBank(
		// resDTO.getSmSupp().getSmAppl().getSmSupplierBanks().get(0));
		// return "onlineAccount/accountMaintenance";
	}

	public void addSupplierBank() {

	}

	public void removeSupplierBank() {
		// this.getManagedBean().getSmAppl().getSmSupplierBanks().remove();
	}

	public void sameAsPermenantAddress() {
		log.info("----------same permenant add----------"
				+ this.getManagedBean().isSameAddress());
	}

	public void businessTypeOnChange() {
		if (this.getManagedBean().getSmAppl().getSmSupplier().getBusinessType().equals("I")) {
			log.info("-----------Business Type is Individual------------"
					+ this.getManagedBean().getSmAppl().getSmSupplier().getBusinessType());
		} else {
			log.info("-----------Business Type is not Individual------------"
					+ this.getManagedBean().getSmAppl().getSmSupplier().getBusinessType());
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
	
	
	public void retrievePmCountryList(){
		ICountrySO reqSO = (ICountrySO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICountrySO.class);
		CountryResDTO resDTO = reqSO.retrieveCountryList();
		
		this.getManagedBean().setCountryLs(resDTO.getCountryList());
	}
	
	public List<PmState> retrievePmStateList(long countryId){
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(countryId);
		IStateSO reqSO = (IStateSO) BusinessDelegate
				.getInstance().getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		
		return resDTO.getStateList();
	}
	
	public void populateAddressByAddType(String typeAdd){
		if(typeAdd.equals("business")){
			List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmBusinessAdd().getPmCountry().getCountryId());
			this.getManagedBean().setBusinessStateLs(stateLs);
		}else if(typeAdd.equals("residential")){
			List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmResAdd().getPmCountry().getCountryId());
			this.getManagedBean().setResidentialStateLs(stateLs);
		}else if(typeAdd.equals("identity")){
			List<PmState> stateLs = this.retrievePmStateList(this.getManagedBean().getSmIdenAdd().getPmCountry().getCountryId());
			this.getManagedBean().setIdentityStateLs(stateLs);
		}
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
