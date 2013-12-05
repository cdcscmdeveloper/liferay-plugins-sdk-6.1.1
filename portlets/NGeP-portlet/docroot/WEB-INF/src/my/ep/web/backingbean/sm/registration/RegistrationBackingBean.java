package my.ep.web.backingbean.sm.registration;

import java.sql.Timestamp;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
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
		log.info("----------------proceedToFirstUserRegistration---------------------");
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		log.info("-------------------------------------"
				+ smSupp.getBusinessType());
		if (smSupp.getBusinessType().equals("I")) {
			this.getManagedBean().getSmPersonnel()
					.setIdentificationNo(smSupp.getRegNo());
			this.getManagedBean().getSmPersonnel()
					.setName(smSupp.getSupplierName());
			this.getManagedBean().getSmPersonnel().setIdentificationType("I");

			this.getManagedBean().getSmSuppOwner()
					.setIdentificationNo(smSupp.getRegNo());
			this.getManagedBean().getSmSuppOwner().setIdentificationType("I");
			this.getManagedBean().getSmSuppOwner()
					.setOwnerName(smSupp.getSupplierName());
			
		}

		return firstUserRegistration;
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
		if (this.getManagedBean().getSmSupp() == null) {
			this.getManagedBean().setSmSupp(new SmSupplier());
			SmSupplier smSupp = this.getManagedBean().getSmSupp();

			this.getManagedBean().setSmAppl(new SmAppl());
			this.getManagedBean().getSmAppl()
					.setSmPersonnels(new ArrayList<SmPersonnel>());
			this.getManagedBean().getSmAppl()
					.setSmSupplierBanks(new ArrayList<SmSupplierBank>());

			smSupp.setSmAppls(new ArrayList<SmAppl>());
			// smSupp.setSmBusinessNatures(new ArrayList<SmBusinessNature>());
			smSupp.setSmSupplierOwners(new ArrayList<SmSupplierOwner>());

			this.getManagedBean().setSmPersonnel(new SmPersonnel());
			this.getManagedBean().setSmSuppBank(new SmSupplierBank());
			// this.getManagedBean().setSmBusNature(new SmBusinessNature());
			this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());
			// this.getManagedBean().getSmAppl().setSmAddress(new
			// SmAddress());//////////////

			/*
			 * SmAddress busAddress = new SmAddress(); busAddress.setPmState(new
			 * PmState()); busAddress.setPmCity(new PmCity());
			 * busAddress.setPmCountry(new PmCountry());
			 * busAddress.setPmDistrict(new PmDistrict());
			 */

			this.getManagedBean().setSmBusinessAdd(new SmAddress());
			this.getManagedBean().setSmRegAdd(new SmAddress());
			this.getManagedBean().setSmResAdd(new SmAddress());
			// this.getManagedBean().setSmPersonnelAdd(new
			// SmPersonnelAddress());
			
			this.getManagedBean().setSmSuppCerts(new ArrayList<SmSupplierCert>());
		}

		this.getManagedBean().getSmAppl()
				.setApplType(this.getManagedBean().getApplType());

		/*
		 * if (this.getManagedBean().getPmOrgProfile().getSmSupplier()
		 * .getRegType().equals("M")) {
		 * 
		 * } else {
		 * 
		 * }
		 */

		return accountRegistration;
	}

	public void addProfessionalCert() {
		log.info("-------------------addProfessionalCert-----------" + this.getManagedBean().getSmSuppCerts());

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
		log.info("-----------------submitRegisteration---------------");
		SmSupplier smSupp = this.getManagedBean().getSmSupp();
		smSupp.setCatalogueUploaded(false);
		
		
		java.util.Date date= new java.util.Date();
		Timestamp ts = new Timestamp(date.getTime());
		 
		smSupp.setCreatedBy(tempId);
		smSupp.setChangedBy(tempId);
		smSupp.setCreatedDate(ts);
		smSupp.setChangedDate(ts);
		
		this.getManagedBean().getSmAppl().setCreatedBy(tempId);
		this.getManagedBean().getSmAppl().setChangedBy(tempId);
		this.getManagedBean().getSmAppl().setCreatedDate(ts);
		this.getManagedBean().getSmAppl().setChangedDate(ts);
		
		this.getManagedBean().getSmPersonnel().setCreatedBy(tempId);
		this.getManagedBean().getSmPersonnel().setChangedBy(tempId);
		this.getManagedBean().getSmPersonnel().setCreatedDate(ts);
		this.getManagedBean().getSmPersonnel().setChangedDate(ts);
		
		this.getManagedBean().getSmSuppOwner().setCreatedBy(tempId);
		this.getManagedBean().getSmSuppOwner().setChangedBy(tempId);
		this.getManagedBean().getSmSuppOwner().setCreatedDate(ts);
		this.getManagedBean().getSmSuppOwner().setChangedDate(ts);
		/*
		 * if (smSupp.getBusinessType() != "I") {
		 * smSupp.getSmBusinessNatures().add
		 * (this.getManagedBean().getSmBusNature()); }
		 */

		this.getManagedBean().getSmSuppOwner().setSmSupplier(smSupp);
		smSupp.getSmSupplierOwners()
				.add(this.getManagedBean().getSmSuppOwner());

		/*
		 * this.getManagedBean().getSmPersonnel().setSmPersonnelAddresses(new
		 * ArrayList<SmPersonnelAddress>());
		 */
		// SmPersonnelAddress regAdd = new SmPersonnelAddress();
		/* SmPersonnelAddress resAdd = new SmPersonnelAddress(); */
		// regAdd.setAddressType("P");
		/* resAdd.setAddressType("R"); */
		// regAdd.setSmAddress(this.getManagedBean().getSmRegAdd());
		/* resAdd.setSmAddress(this.getManagedBean().getSmResAdd()); */
		// regAdd.setSmPersonnel(this.getManagedBean().getSmPersonnel());
		/* resAdd.setSmPersonnel(this.getManagedBean().getSmPersonnel()); */

		this.getManagedBean().getSmSuppBank().setDefaultAccount(true);
		this.getManagedBean().getSmSuppBank()
				.setSmAppl(this.getManagedBean().getSmAppl());

		// this.getManagedBean().getSmPersonnel().getSmPersonnelAddresses().add(regAdd);
		/*
		 * this.getManagedBean().getSmPersonnel().getSmPersonnelAddresses().add(
		 * resAdd);
		 */
		this.getManagedBean().getSmPersonnel().setEpRole("A");
		this.getManagedBean().getSmPersonnel()
				.setSmAppl(this.getManagedBean().getSmAppl());
		this.getManagedBean().getSmPersonnel().setNationalityId(nationality);
		this.getManagedBean().getSmPersonnel().setEquityOwner(true);
		// this.getManagedBean().getSmPersonnel().setAuthorized(true);

		this.getManagedBean().getSmAppl().setSupplierType("B");

		// this.getManagedBean().getSmAppl().getSmSupplierBanks()
		// .add(this.getManagedBean().getSmSuppBank());

		this.getManagedBean().getSmAppl().getSmPersonnels()
				.add(this.getManagedBean().getSmPersonnel());
		// this.getManagedBean().getSmAppl().setSmAddress(this.getManagedBean().getSmBusinessAdd());////////
		this.getManagedBean().getSmAppl().setSmSupplier(smSupp);

		// smSupp.setSmAppl(this.getManagedBean().getSmAppl());
		this.getManagedBean().getSmAppl().setStatusId(20001);
		smSupp.setStatusId(20000);
		smSupp.getSmAppls().add(this.getManagedBean().getSmAppl());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setSmSupplier(smSupp);
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.createSupplier(reqDTO);
		log.info("-----------------End submitRegisteration---------------");

		
		if (resDTO.isSuccessful()){
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Your account has been registered successfully.", ""));
			return "/xhtml/sm/onlinereg/outputMsg";
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", ""));
			return "";
		}

		// this.getManagedBean().getSmBusinessAdd().setSmAppls(new
		// ArrayList<SmAppl>());
		// this.getManagedBean().getSmBusinessAdd().getSmAppls().add(this.getManagedBean().getSmAppl());
	}

	public void updateOnlineAccount() {
		log.info("--------------------updateOnlineAccount----------------");
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();

		this.getManagedBean().getSmSuppBank()
				.setSmAppl(this.getManagedBean().getSmSupp().getSmAppl());
		log.info("---------------getSmSuppBank----------------"
				+ this.getManagedBean().getSmSuppBank().getAccountNo());
		log.info("---------------getSmSuppBank----------------"
				+ this.getManagedBean().getSmSuppBank().getSupplierBankId());
		this.getManagedBean().getSmSupp().getSmAppl().getSmSupplierBanks()
				.set(0, this.getManagedBean().getSmSuppBank());

		reqDTO.setSmSupplier(this.getManagedBean().getSmSupp());
		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject("sm", IRegistrationSO.class);
		RegistrationResDTO resDTO = reqSO.updateOnlineAcc(reqDTO);
	}

	public void viewOnlineAccount() {
		log.info("--------------------viewOnlineAccount----------------");
		this.getManagedBean().setSmBusinessAdd(new SmAddress());
		this.getManagedBean().setSmSuppOwner(new SmSupplierOwner());

		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		reqDTO.setSupplierId(10);

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


}
