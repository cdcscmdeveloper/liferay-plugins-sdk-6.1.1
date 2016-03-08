package my.ep.web.backingbean.sm.supplieralert;

import java.util.Calendar;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.supplieralert.SupplierAlertManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

@ManagedBean(name = "supplierAlertBackingBean")
@RequestScoped
public class SupplierAlertBackingBean extends
		BaseBackingBean<SupplierAlertManagedBean> {
	
	private static Logger log = EpLogger.getLogger(SupplierAlertBackingBean.class);
	
	/**
	 * Pre - render MOF expiry alert page
	 */
	public void preRenderMofExpiryAlert() {
		renewSesionObject();
		SmSupplier supp = SmApplicationUtil.getCurrentSupplier();
		if (supp != null) {
			SmMofAccount acc = SmCommonUtil.getMofAccountBySuppIdAndSuppType(
					supp.getSupplierId(), supp.getSmAppl().getSupplierType());
			if (acc != null
					&& acc.getExpDate() != null
					&& acc.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
				Date currDate = new Date();
				Calendar currDateAfterThreeMonths = Calendar.getInstance();
				currDateAfterThreeMonths.setTime(currDate);
				currDateAfterThreeMonths.add(Calendar.MONTH, 3);
				if (currDateAfterThreeMonths.getTime().after(acc.getExpDate())) {
					getManagedBean().setMofExpiryLeftDaysMsg(
							SmCommonUtil.getMessage(
									"lbl.sm.suppalert.mofexpiry.msg", null));
					getManagedBean().setExpDate(acc.getExpDate());
					initCountDown();
				}
			}
		}
		SmCommonUtil.redirectToPage("mofExpiryAlertPage.xhtml", "",null);
	}

	public void initCountDown() {
		Date currDate = new Date();
		long togo = (getManagedBean().getExpDate().getTime() - currDate
				.getTime()) / 1000;
		getManagedBean().setSecs((togo % 60) + "");
		togo = togo / 60;
		getManagedBean().setMins((togo % 60) + "");
		togo = togo / 60;
		getManagedBean().setHours((togo % 24) + "");
		togo = togo / 24;
		getManagedBean().setDays((togo % 7) + "");
	}
	
	/**
	 * to check whether the login user eligible to view virtual cert.
	 * @return
	 */
	public boolean isValidToViewVirtualCert() {
		IMofAccountSO mofSO = null;
		boolean valid = false;
		try {
			mofSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject("sm", IMofAccountSO.class);
			long userId = 0;
			if(new FacesScopes().getUserSessionBean() != null) {
				userId = new FacesScopes().getUserSessionBean().getUserId();
			}
			System.out.println("user id: " + userId);
			MofAccountReqDTO reqDTO = new MofAccountReqDTO();
			reqDTO.setUserId(userId);
			MofAccountResDTO resDTO = mofSO.retriveMofAccountByUserId(reqDTO);
			valid = resDTO.isSuccessful();
			System.out.println("valid: " + valid);
		} finally {
			mofSO = null;
		}
		return valid;
	}

	public void navigateToViewVirtualCert() {
		log.info("In navigateToSiteVisitAssignment");

		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);

		log.info("getURLCurrent" + themeDisplay.getURLCurrent());

		try {
			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			log.info("commonUrl" + commonUrl);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			// externalContext.redirect("/web/guest/" +url +"?taskId="+taskId);
			log.info("site visit url: " + commonUrl + "/" + "view-virtual-certification");
			externalContext.redirect(commonUrl + "/" + "view-virtual-certification");
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}
}
