package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	/**
	 * Pre - render for View Supplier Profile
	 */
	public void preRenderViewSupplierProfile() {
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		if (getManagedBean().isSupplierUser()) {
			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
			reqDTO.setSupplierId(supplier.getSupplierId());
			MofRegistrationResDTO resDTO = SmServiceLookup
					.getMofRegistrationSO()
					.retrieveLatestSmApplWithChildBySupplierId(reqDTO);
			String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

			if (resDTO.getSmAppl() != null) {
				String applNo = resDTO.getSmAppl().getApplNo();
				getManagedBean().setApplicationNoParam(applNo + "_APP");
				if (applNo != null && StringUtils.isNotBlank(applNo)) {
					setApplication(resDTO.getSmAppl());
					redirectToAppl = "/xhtml/sm/revisionSmApplView/viewSupplierProfile/smViewProfileSideMenu_BasePage.xhtml";
					SmCommonUtil.redirectToPage(redirectToAppl, "");
				} else {
					SmCommonUtil.redirectToPage(redirectToAppl,
							MessageConst.INFO_721212);
				}
			} else {
				SmCommonUtil.redirectToPage(redirectToAppl,
						MessageConst.INFO_721212);
			}
			setTabParam();
			getManagedBean().setRenderedBackButton(false);
		} else {
			getManagedBean().setRenderedBackButton(true);
			String supplierIdParam = getSupplierIdParam();
			if (StringUtils.isNotBlank(supplierIdParam)) {
				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSupplierId(Long.parseLong(supplierIdParam));
				getManagedBean().setSearchSupplier(
						SmServiceLookup.getSupplierSearchSO()
								.retriveSupplierById(reqDTO).getSmSupplier());
			} else {
				SmCommonUtil
						.redirectToPage(
								"/xhtml/sm/revisionSmApplView/viewSupplierProfile/searchSupplierProfilePage.xhtml",
								"");
			}
		}
	}

}
