package my.ep.web.backingbean.sm.resendaccountactivationemail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "resendAccountActivationEmailBackingBean")
@ViewScoped
public class ResendAccountActivationEmailBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8242421500840047056L;

	public static Logger log = Logger
			.getLogger(ResendAccountActivationEmailBackingBean.class);

	private String businessRegNo;
	private String companyName;
	private String suppAdminIdentificationNo;
	private String suppAdminEmail;
	private String suppAdminMobileNo;
	private String suppAdminMobileArea;
	private String suppAdminMobileCountry;
	private String suppAdminMobileTelcoId;
	private String accType;

	private SmPersonnel selectedPersonnel;
	
	public void preRender() {
		//26238 - start 27848
		//check disciplinary action on supplier
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		if (pmUser == null || (pmUser != null && pmUser.getUserId() == null)) {
			renewSesionObject();
			SmCommonUtil
					.redirectToPage(
							"/xhtml/sm/resendaccountactivationemail/resendAccountActivationEmail.xhtml",
							"", null, null);
		}else{
			SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
			
			SupplierDataMaintenanceReqDTO supReqDTO = new SupplierDataMaintenanceReqDTO();
			supReqDTO.setSupplier(currSupp);
			SupplierDataMaintenanceResDTO activeDisc = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionBySupplier(supReqDTO);
			if(activeDisc != null){
				String labelNameVal = "";
				FacesContext.getCurrentInstance().addMessage("imposedDisciplinary", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						SmCommonUtil.getMessage("lbl.sm.not.allowed.to.proceed.due.to.disciplinary.action",
								new String[] { labelNameVal })));
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/resendaccountactivationemail/resendAccountActivationEmail.xhtml", "", null, null);
			} else {		
				renewSesionObject();
				SmCommonUtil
						.redirectToPage(
								"/xhtml/sm/resendaccountactivationemail/resendAccountActivationEmail.xhtml",
								"", null, null);
			}
		}
		// 26238 - END 27848
	}

	public void onClickResend() {
		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setRegNo(businessRegNo);
		reqDTO.setCompanyName(companyName);

		try {

			SupplierSearchResDTO resDTO = SmServiceLookup.getSupplierSearchSO()
					.retrieveSupplierAndPersonnelsByBusRegNoAndName(reqDTO);

			if (resDTO.getSmSupplier() != null
					&& resDTO.getSmSupplier().getSmAppl() != null
					&& !EntityHelper.isCollectionEmpty(resDTO.getSmSupplier()
							.getSmAppl().getSmPersonnels())) {

				selectedPersonnel = getAdminPersonnel(resDTO.getSmSupplier());

				if (selectedPersonnel == null) {
					throw new EpException(MessageConst.ERROR_721018);
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
					Date activationDate = selectedPersonnel.getChangedDate();
					activationDate = sdf.parse(sdf.format(activationDate));

					Calendar expiredActivationDate = new GregorianCalendar();
					expiredActivationDate.setTime(activationDate);
					expiredActivationDate
							.add(Calendar.DAY_OF_MONTH, SmRuleHelper
									.getInstance().getEmailActivationDays());

					Calendar today = new GregorianCalendar();

					if (accType.equals(SupplierManagementConst.ACC_GOVT_SELLER)
							&& resDTO.getSmSupplier().getSmAppl() != null
							&& !resDTO
									.getSmSupplier()
									.getSmAppl()
									.getSupplierType()
									.equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
						throw new EpException(MessageConst.ERROR_721018);
					}

					if (accType.equals(SupplierManagementConst.ACC_GOVT_SELLER)
							&& (!selectedPersonnel.getMobileArea().equals(
									suppAdminMobileArea)
									|| !selectedPersonnel.getMobileCountry()
											.equals(suppAdminMobileCountry)
									|| !selectedPersonnel.getMobileTelcoId()
											.equals(suppAdminMobileTelcoId) || !selectedPersonnel
									.getMobileNo().equals(suppAdminMobileNo))) {
						throw new EpException(MessageConst.ERROR_721018);
					}

					if (selectedPersonnel.getIdentificationNo().equals(
							suppAdminIdentificationNo)
							&& selectedPersonnel
									.getRecordStatusFlg()
									.equals(DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)) {
						RequestContext.getCurrentInstance().execute(
								"confirmDialog.show();");

					} else {
						throw new EpException(MessageConst.ERROR_721018);
					}
				}
			} else {
				throw new EpException(MessageConst.ERROR_721018);
			}
		} catch (Exception ex) {
			throw new EpException(MessageConst.ERROR_721018);
		}
	}

	/**
	 * Clear entered information on change account type
	 * 
	 * @param e
	 */
	public void onChangeAccType(AjaxBehaviorEvent e) {
		companyName = StringUtils.EMPTY;
		businessRegNo = StringUtils.EMPTY;
		suppAdminEmail = StringUtils.EMPTY;
		suppAdminIdentificationNo = StringUtils.EMPTY;
		suppAdminMobileArea = StringUtils.EMPTY;
		suppAdminMobileCountry = StringUtils.EMPTY;
		suppAdminMobileNo = StringUtils.EMPTY;
		suppAdminMobileTelcoId = StringUtils.EMPTY;

	}

	/**
	 * Request to resend account activation email
	 */
	public void resendAccountActivationEmail() {
		PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
		personnelReqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		personnelReqDTO.setSmPersonnelList(new ArrayList<SmPersonnel>());
		// reset back to new email
		selectedPersonnel.setEmail(suppAdminEmail);
		personnelReqDTO.getSmPersonnelList().add(selectedPersonnel);

		SmServiceLookup.getPersonnelSO().resendPersonnelActivationEmail(
				personnelReqDTO);

		SmCommonUtil.redirectToPage(
				"/xhtml/sm/userProfileMaintenance/outputMsg",
				MessageConst.INFO_721032, null, null);
	}

	private SmPersonnel getAdminPersonnel(SmSupplier supplier) {
		SmPersonnel retVal = null;
		if (supplier.getSmAppl() != null
				&& !EntityHelper.isCollectionEmpty(supplier.getSmAppl()
						.getSmPersonnels())) {
			for (SmPersonnel per : supplier.getSmAppl().getSmPersonnels()) {
				if (per.getEpRole() != null
						&& my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
								.contains(per.getEpRole())
						&& per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION) {
					retVal = per;
					break;
				}
			}
		}
		return retVal;
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

	public void setSuppAdminIdentificationNo(String suppAdminIdentificationNo) {
		this.suppAdminIdentificationNo = suppAdminIdentificationNo.trim();
	}

	public String getSuppAdminEmail() {
		return suppAdminEmail;
	}

	public void setSuppAdminEmail(String suppAdminEmail) {
		this.suppAdminEmail = suppAdminEmail.trim();
	}

	public SmPersonnel getSelectedPersonnel() {
		return selectedPersonnel;
	}

	public void setSelectedPersonnel(SmPersonnel selectedPersonnel) {
		this.selectedPersonnel = selectedPersonnel;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}

	public String getSuppAdminMobileNo() {
		return suppAdminMobileNo;
	}

	public void setSuppAdminMobileNo(String suppAdminMobileNo) {
		this.suppAdminMobileNo = suppAdminMobileNo;
	}

	public String getSuppAdminMobileArea() {
		return suppAdminMobileArea;
	}

	public void setSuppAdminMobileArea(String suppAdminMobileArea) {
		this.suppAdminMobileArea = suppAdminMobileArea;
	}

	public String getSuppAdminMobileCountry() {
		return suppAdminMobileCountry;
	}

	public void setSuppAdminMobileCountry(String suppAdminMobileCountry) {
		this.suppAdminMobileCountry = suppAdminMobileCountry;
	}

	public String getSuppAdminMobileTelcoId() {
		return suppAdminMobileTelcoId;
	}

	public void setSuppAdminMobileTelcoId(String suppAdminMobileTelcoId) {
		this.suppAdminMobileTelcoId = suppAdminMobileTelcoId;
	}
}
