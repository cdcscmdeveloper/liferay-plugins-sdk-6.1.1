package my.ep.web.backingbean.sm.applicationView;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

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

		if (resDTO.getSmAppl() != null) {
			String applNo = resDTO.getSmAppl().getApplNo();
			getManagedBean().setApplicationNoParam(applNo + "_APP");
			getManagedBean().setViewSupplierProfile(true);
			if(applNo!=null && StringUtils.isNotBlank(applNo)){
				initApplication(resDTO.getSmAppl());
				String applType = getManagedBean().getSmAppl().getApplType();
				String redirectToAppl = SmApplicationUtil
						.getReviewApplicationSupplierProfileBasePage(applType);
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, redirectToAppl);
			}else{
				getManagedBean().setReviewApplicationExist(false);
				constructMessage();
			}
		}
		setTabParam();
	}

}
