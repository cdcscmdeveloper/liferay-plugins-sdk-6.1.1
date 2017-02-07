package my.ep.web.backingbean.sm.selfrequestsupplieradmin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.dto.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminReqDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.selfrequestsupplieradmin.SelfRequestSupplierAdminManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "selfRequestSupplierAdminBackingBean")
@RequestScoped
public class SelfRequestSupplierAdminBackingBean extends
		BaseBackingBean<SelfRequestSupplierAdminManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4774616656344235966L;

	private static final String COMPANY_VALIDATION_PAGE = "1";
	private static final String SUPP_ADMIN_PERSONNEL_PAGE = "2";
	private static final String SUPP_ADMIN_DETAILS_PAGE = "3";

	public void preRender() {
		renewSesionObject();
		getManagedBean().setCurrDate(new Date());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/selfRequestSupplierAdmin/selfRequestSuppAdminTnc.xhtml",
						"", null);
	}

	public void navigateToCompanyValidation() {
		getManagedBean().setPage(COMPANY_VALIDATION_PAGE);
		getManagedBean().setSupplier(new SmSupplier());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/selfRequestSupplierAdmin/selfRequestSuppAdminBasePage.xhtml",
						null, null);
	}

	public void navigateToSuppAdmin() {
		if (StringUtils.isBlank(getManagedBean().getMofNo())) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage(
					"validationMsg",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							SmCommonUtil.getMessage("lbl.sm.required.msg",
									new String[] { SmCommonUtil.getMessage(
											"lbl.sm.selfrequest.mof.number",
											null) })));
		} else {
			getManagedBean().setPersonnelItems(new ArrayList<SelectItem>());
			SelectItem item = null;
			for (SmPersonnel per : getManagedBean().getSupplier().getSmAppl()
					.getSmPersonnels()) {
				if (per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& per.getPmUser() == null
						&& StringUtils.isBlank(per.getEpRole())
						&& per.getIdentificationTypeId() != null
						&& per.getIdentificationType() != null
						&& per.getIdentificationType().getParameterCode()
								.equals(SupplierManagementConst.NEW_IC)) {
					item = new SelectItem();
					item.setLabel(per.getName());
					item.setValue(per.getPersonnelId());
					getManagedBean().getPersonnelItems().add(item);
				}
			}
			getManagedBean().setPage(SUPP_ADMIN_PERSONNEL_PAGE);
		}
	}

	public void back(String page) {
		getManagedBean().setPage(page);
	}

	public void navigateToSuppAdminDetails() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String invalidMsg = getInValidIcErrMsg(getManagedBean().getIcNo());
		if (StringUtils.isBlank(invalidMsg)) {
			getManagedBean().setPage(SUPP_ADMIN_DETAILS_PAGE);
			SmPersonnel suppAdmin = (SmPersonnel) EntityHelper
					.getEntityBySinglePropertyFromList(getManagedBean()
							.getIcNo(), getManagedBean().getSupplier()
							.getSmAppl().getSmPersonnels(), "identificationNo");
			getManagedBean().setSelectedAdmin(suppAdmin);
			getManagedBean().setCountryItems(SmCommonUtil.getCountryList());
		} else {
			fc.addMessage("icNoId", new FacesMessage(
					FacesMessage.SEVERITY_INFO, null, invalidMsg));
		}
	}

	public void onBlurMofNo(String mofNo) {
		FacesContext fc = FacesContext.getCurrentInstance();
		StringBuilder fullMsg = new StringBuilder();
		if (StringUtils.isBlank(mofNo)) {
			fullMsg.append(SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.mof.number", null) }));
			fc.addMessage("validationMsg", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, fullMsg.toString()));
		} else {
			String mofStatusMsg = "lbl.sm.selfrequest.msg.mofstatus.notvalid.expired";
			String suppAdminStatusMsg = "lbl.sm.selfrequest.msg.suppadmin.not.appointed";
			String suppAdminName = SmCommonUtil.getMessage(
					"lbl.sm.selfrequest.msg.suppadmin.none", null);
			SelfRequestSupplierAdminReqDTO reqDTO = new SelfRequestSupplierAdminReqDTO();
			reqDTO.setMofNo(getManagedBean().getMofNo());
			getManagedBean().setSupplier(
					SmServiceLookup.getSelfRequestSupplierAdminSO()
							.retrieveSupplierByMofNo(reqDTO).getSupplier());

			// set full alert message
			fullMsg.append(
					SmCommonUtil.getMessage("lbl.sm.selfrequest.msg.alert",
							null)).append("<br />");
			fullMsg.append(
					SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.msg.status.as.below",
							new String[] { getManagedBean().getMofNo() }))
					.append("<br />");

			// mof no. entered is not exist
			if (getManagedBean().getSupplier() == null
					|| EntityHelper.isCollectionEmpty(getManagedBean()
							.getSupplier().getSmMofAccounts())) {
				fullMsg.append(
						SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.mofstatus",
								new String[] { SmCommonUtil.getMessage(
										mofStatusMsg, null) }))
						.append("<br />");
				fullMsg.append(SmCommonUtil.getMessage(
						"lbl.sm.selfrequest.msg.click.newreg", null));
				fc.addMessage("mofNoInvalid",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								SmCommonUtil.getMessage(mofStatusMsg, null)));
			} else {
				// set mof account message status
				SmMofAccount mofAcc = getManagedBean().getSupplier()
						.getSmMofAccounts().get(0);

				if (mofAcc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE
						&& mofAcc.getExpDate() != null) {
					Calendar c = Calendar.getInstance();
					c.setTime(mofAcc.getExpDate());
					c.add(Calendar.YEAR, 1);
					Date expiryDate = c.getTime();

					// check if expire date less than a year
					if (expiryDate.after(new Date())) {
						mofStatusMsg = "lbl.sm.selfrequest.msg.mofstatus.valid";
					}
				}

				// set supplier admin status
				if (getManagedBean().getSupplier().getSmAppl() != null
						&& !EntityHelper.isCollectionEmpty(getManagedBean()
								.getSupplier().getSmAppl().getSmPersonnels())) {
					List<SmPersonnel> personnels = getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels();

					for (SmPersonnel per : personnels) {
						if ((per.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE || per
								.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION)
								&& (!StringUtils.isBlank(per.getEpRole()) && (per
										.getEpRole().equals(
												RoleCode.BASIC_SUPPLIER_ADMIN
														.value()) || per
										.getEpRole().equals(
												RoleCode.MOF_SUPPLIER_ADMIN
														.value())))) {
							suppAdminStatusMsg = "lbl.sm.selfrequest.msg.suppadmin.appointed";
							suppAdminName = per.getName();
						}
					}
				}

				fullMsg.append(
						SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.mofstatus",
								new String[] { SmCommonUtil.getMessage(
										mofStatusMsg, null) }))
						.append("<br />");

				// check if mof status is not valid
				if (mofStatusMsg
						.equals("lbl.sm.selfrequest.msg.mofstatus.notvalid.expired")) {
					fullMsg.append(SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.msg.click.newreg", null));
				}
				// if mof status is valid
				else {

					fullMsg.append(
							SmCommonUtil.getMessage(
									"lbl.sm.selfrequest.msg.suppadmin.status",
									new String[] { SmCommonUtil.getMessage(
											suppAdminStatusMsg, null) }))
							.append("<br />");
					fullMsg.append(
							SmCommonUtil
									.getMessage(
											"lbl.sm.selfrequest.msg.suppadmin.name.appointed",
											new String[] { suppAdminName }))
							.append("<br />");

					// if supp admin not assigned
					if (suppAdminStatusMsg
							.equals("lbl.sm.selfrequest.msg.suppadmin.not.appointed")) {
						fullMsg.append(SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.click.proceed", null));
					}

					// if supp admin already assigned
					else {
						fullMsg.append(SmCommonUtil.getMessage(
								"lbl.sm.selfrequest.msg.click.login", null));
					}
				}
			}

			fc.addMessage("validationMsg", new FacesMessage(
					FacesMessage.SEVERITY_INFO, null, fullMsg.toString()));
		}

	}

	public void redirectToMainPage() {
		String url = SmCommonUtil.getBasePortalUrl();
		url = StringUtils.isBlank(url) ? SmCommonUtil
				.getBasePortalAppUrlFromProperties() : url;
		SmCommonUtil.redirectToExternalUrl(url);
	}

	/**
	 * Custom validation for ic no
	 * 
	 * @param FacesContext
	 *            facesContext
	 * @param UIComponent
	 *            uiComponent
	 * @param Object
	 *            value
	 */
	public void onBlurIcNo(String icNo) {
		FacesContext fc = FacesContext.getCurrentInstance();
		String errMsg = getInValidIcErrMsg(icNo);

		if (!StringUtils.isBlank(errMsg)) {
			fc.addMessage("icNoId", new FacesMessage(
					FacesMessage.SEVERITY_ERROR, null, errMsg));
		}

	}

	public void searchSuppAdmin() {
		RequestContext.getCurrentInstance().execute(
				"searchSuppAdminDialog.show();");
	}

	private String getInValidIcErrMsg(String icNo) {
		String labelNameVal = SmCommonUtil.getMessage("lbl.sm.person.icNum",
				null);
		String errMsg = StringUtils.EMPTY;

		if (!StringUtils.isBlank(icNo)) {
			SmPersonnel per = (SmPersonnel) EntityHelper
					.getEntityBySinglePropertyFromList(icNo, getManagedBean()
							.getSupplier().getSmAppl().getSmPersonnels(),
							"identificationNo");

			if (per == null) {
				errMsg = SmCommonUtil.getMessage(
						"lbl.sm.selfrequest.icno.invalid", null);
			} else {
				if (per.getPersonnelId().compareTo(
						getManagedBean().getSelectedPersonnelId()) != 0) {
					errMsg = SmCommonUtil.getMessage(
							"lbl.sm.selfrequest.icno.invalid", null);
				}
			}

		} else {
			errMsg = SmCommonUtil.getMessage("lbl.sm.required.msg",
					new String[] { labelNameVal });
		}

		return errMsg;
	}

}
