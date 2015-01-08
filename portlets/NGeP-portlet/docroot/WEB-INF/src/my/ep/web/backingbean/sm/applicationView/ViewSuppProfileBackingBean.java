package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void navigateToViewSuppProfile() {
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();

		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(supplier.getSupplierId());
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofRegistrationSO.class);
		MofRegistrationResDTO resDTO = mofRegistrationSO
				.retrieveLatestSmApplWithChildBySupplierId(reqDTO);

		FacesContext fc = FacesContext.getCurrentInstance();
		String redirectToAppl = SmApplicationUtil.getApplicationErrorPage();

		if (resDTO.getSmAppl() != null) {
			String applNo = resDTO.getSmAppl().getApplNo();
			getManagedBean().setApplicationNoParam(applNo + "_APP");
			getManagedBean().setViewSupplierProfile(true);
			if (applNo != null && StringUtils.isNotBlank(applNo)) {
				initApplication(resDTO.getSmAppl());
				redirectToAppl = "/xhtml/sm/revisionSmApplView/viewSupplierProfile/smViewProfileSideMenu_BasePage.xhtml";

			} else {
				log.error("*****************applNo is null**************");
				SmApplicationUtil.showSuppProfileNotReadyMsg(fc);
			}
		} else {
			log.error("*****************appl is null**************");
			SmApplicationUtil.showSuppProfileNotReadyMsg(fc);
		}

		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, redirectToAppl);
		setTabParam();
	}

}
