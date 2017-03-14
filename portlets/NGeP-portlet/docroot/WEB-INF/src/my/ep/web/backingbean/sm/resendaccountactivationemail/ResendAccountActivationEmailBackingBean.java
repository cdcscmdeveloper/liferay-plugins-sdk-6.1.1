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
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

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

	private SmPersonnel selectedPersonnel;

	/**
	 * Request to resend account activation email
	 */
	public void resendAccountActivationEmail() {
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

					if (selectedPersonnel.getIdentificationNo().equals(
							suppAdminIdentificationNo)
							&& selectedPersonnel.getEmail().equals(
									suppAdminEmail)
							&& selectedPersonnel.getRecordStatusFlg().equals(8)
							&& expiredActivationDate.after(today)) {

						PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
						personnelReqDTO.setPortalBaseUrl(SmCommonUtil
								.getBasePortalUrl());
						personnelReqDTO
								.setSmPersonnelList(new ArrayList<SmPersonnel>());
						personnelReqDTO.getSmPersonnelList().add(
								selectedPersonnel);

						SmServiceLookup
								.getPersonnelSO()
								.resendPersonnelActivationEmail(personnelReqDTO);

						SmCommonUtil.redirectToPage(
								"/xhtml/sm/userProfileMaintenance/outputMsg",
								MessageConst.INFO_721032, null);
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
}
