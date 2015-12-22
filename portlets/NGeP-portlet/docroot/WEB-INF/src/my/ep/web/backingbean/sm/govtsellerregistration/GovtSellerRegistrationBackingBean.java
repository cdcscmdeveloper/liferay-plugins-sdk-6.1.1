package my.ep.web.backingbean.sm.govtsellerregistration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.govtsellerregistration.GovtSellerRegistrationManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "govtSellerBackingBean")
@RequestScoped
public class GovtSellerRegistrationBackingBean extends
		BaseBackingBean<GovtSellerRegistrationManagedBean> {

	public static RegistrationBackingBean instance;

	public void preRenderGovtSeller() {
		getManagedBean().setTargetPage(
				SupplierManagementConst.GOVT_SELLER_TNC_PAGE);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/govtSellerRegistration/govtSellerBasePage.xhtml",
				"", null);

	}

	public String returnToSignUp() {
		return getRegistrationBackingBean().returnToSignUp();
	}

	public void navigateToAccInfoPage() {
		getManagedBean().setTargetPage(
				SupplierManagementConst.GOVT_SELLER_ACCOUNT_INFO_PAGE);
	}

	public static RegistrationBackingBean getRegistrationBackingBean() {
		if (instance == null) {
			try {
				instance = new RegistrationBackingBean();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return instance;

	}
}
