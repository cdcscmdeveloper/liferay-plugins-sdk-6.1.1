package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.sm.applicationView.ViewProfileManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "viewProfileBackingBean")
@RequestScoped
public class ViewProfileBackingBean extends
		BaseViewBackingBean<ViewProfileManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5585007010583358604L;

	/**
	 * Pre - render for View Supplier Profile
	 */
	public void preRenderViewProfile() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			initCompanyInfo(supplier.getSmAppl().getApplId());
			String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

			if (getManagedBean().getSmAppl() != null) {
				String applNo = getManagedBean().getSmAppl().getApplNo();
				if (applNo != null && StringUtils.isNotBlank(applNo)) {
					viewApplication(getManagedBean().getSmAppl());
				} else {
					SmCommonUtil.redirectToPage(redirectToAppl,
							MessageConst.INFO_721212, null, null);
				}
			} else {
				SmCommonUtil.redirectToPage(redirectToAppl,
						MessageConst.INFO_721212, null, null);
			}
			setTabParam();
			getManagedBean().setRenderedBackButton(false);
		} else {
			getManagedBean().setRenderedBackButton(true);
			String supplierIdParam = getSupplierIdParam();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			if (StringUtils.isNotBlank(supplierIdParam)) {
				getManagedBean().setRenderedBackButton(false);
				SmAppl appl = SmApplicationUtil.getLatestApplBySupplierId(Long
						.parseLong(supplierIdParam));
				initCompanyInfo(appl.getApplId());
			} else {
				SmCommonUtil
						.redirectToPage(
								"/xhtml/sm/revisionSmApplView/viewProfile/searchSupplierProfilePage.xhtml",
								"", null, null);
			}
		}
	}

	public void search() {
		validateCompanySearch();
		getManagedBean().searchSupplier();
	}
}
