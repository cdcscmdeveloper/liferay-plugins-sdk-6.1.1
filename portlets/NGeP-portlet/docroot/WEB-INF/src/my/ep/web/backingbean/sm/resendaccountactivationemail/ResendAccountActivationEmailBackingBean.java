package my.ep.web.backingbean.sm.resendaccountactivationemail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@ManagedBean(name = "resendAccountActivationEmailBackingBean")
@ViewScoped
public class ResendAccountActivationEmailBackingBean extends
		BaseBackingBean<BaseManagedBean> {
	
	public static Logger log = Logger.getLogger(ResendAccountActivationEmailBackingBean.class);

	private String businessRegNo;
	private String companyName;
	private String suppAdminIdentificationNo;
	private String suppAdminEmail;
	
	private SmPersonnel selectedPersonnel;
	
	/**
	 * Request to resend account activation email
	 */
	public void resendAccountActivationEmail() {
		FacesScopes fs = new FacesScopes();
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(businessRegNo);
		reqDTO.setCompanyName(companyName);
		
		try {
			
		log.info("---------- Preparing to resend account activation email for : ----------");
		log.info("Business Reg No : " + reqDTO.getRegNo());
		log.info("Company Name : " + reqDTO.getCompanyName());
		log.info("Supplier Admin Identification No : " + suppAdminIdentificationNo);
		log.info("Supplier Admin Email : " + suppAdminEmail);

		SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO().retrieveSupplierAndPersonnelsByBusRegNoAndName(reqDTO);
		
		log.info("---------- " + resDTO.getSmSupplier().getSmAppl().getSmPersonnels().size() + " Personnel record found" + " ----------");
		
		if (resDTO.getSmSupplier().getSmAppl().getSmPersonnels() != null
				&& !resDTO.getSmSupplier().getSmAppl().getSmPersonnels().isEmpty()
				&& resDTO.getSmSupplier().getSmAppl().getSmPersonnels().size() == 1 ) {

			selectedPersonnel = resDTO.getSmSupplier().getSmAppl().getSmPersonnels().get(0);
			
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			Date activationDate = selectedPersonnel.getChangedDate();
			activationDate = sdf.parse(sdf.format(activationDate));
			
			Calendar expiredActivationDate = new GregorianCalendar();
			expiredActivationDate.setTime(activationDate);
			expiredActivationDate.add(Calendar.DAY_OF_MONTH, 7);
			
			Calendar today = new GregorianCalendar();

			log.info("Supplier & Personnel Details found :");
			log.info("Supplier Business Registration No : " + resDTO.getSmSupplier().getRegNo());
			log.info("Supplier Business Name : " + resDTO.getSmSupplier().getCompanyName());
			log.info("Supplier Admin Identification No : " + selectedPersonnel.getIdentificationNo());
			log.info("Supplier Admin Name : " + selectedPersonnel.getName());
			log.info("Supplier Admin Email : " + selectedPersonnel.getEmail());
			log.info("Supplier Registration Date : " + activationDate);
			log.info("Supplier Activation Expired Date : " + expiredActivationDate.getTime());
			log.info("Today's Date : " + today.getTime());
			
			if (selectedPersonnel.getIdentificationNo().equals(suppAdminIdentificationNo)
				&& selectedPersonnel.getEmail().equals(suppAdminEmail)
				&& selectedPersonnel.getRecordStatusFlg().equals(8)
				&& expiredActivationDate.after(today)) {
				
				PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
//				personnelReqDTO.setServerName(fs.getRequest().getServerName());
//				personnelReqDTO.setServerPort(fs.getRequest().getServerPort());
				personnelReqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
				personnelReqDTO.setSmPersonnelList(new ArrayList<SmPersonnel>());
				personnelReqDTO.getSmPersonnelList().add(selectedPersonnel);

				SmServiceLookup.getPersonnelSO().resendPersonnelActivationEmail(personnelReqDTO);
				log.info("---------- New account activation key generated & email sent successfully ----------");
				
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.INFO_721032,null);}
			else {
				log.info("---------- Not a valid personnel ----------");
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.ERROR_721018,null);}
			}
		else {
			log.info("---------- Personnel not found ----------");
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/userProfileMaintenance/outputMsg",
					MessageConst.ERROR_721018,null);
		}
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.ERROR_721018,null);
		}
	}

	public String getBusinessRegNo() {
		return businessRegNo;
	}

	public void setBusinessRegNo(String businessRegNo) {
		this.businessRegNo = businessRegNo.trim();
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName.trim();
	}
	
	public String getSuppAdminIdentificationNo() {
		return suppAdminIdentificationNo;
	}
	
	public void setSuppAdminIdentificationNo(
			String suppAdminIdentificationNo) {
		this.suppAdminIdentificationNo = suppAdminIdentificationNo.trim();
	}
	
	public String getSuppAdminEmail() {
		return suppAdminEmail;
	}
	
	public void setSuppAdminEmail(
			String suppAdminEmail) {
		this.suppAdminEmail = suppAdminEmail.trim();
	}

	public SmPersonnel getSelectedPersonnel() {
		return selectedPersonnel;
	}
	
	public void setSelectedPersonnel(
			SmPersonnel selectedPersonnel) {
		this.selectedPersonnel = selectedPersonnel;
	}
}
