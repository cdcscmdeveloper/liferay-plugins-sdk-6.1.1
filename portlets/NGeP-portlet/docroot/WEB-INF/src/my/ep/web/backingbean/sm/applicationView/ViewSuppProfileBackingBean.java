package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;

@ManagedBean(name = "viewSuppProfileBackingBean")
@RequestScoped
public class ViewSuppProfileBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void navigateToViewSuppProfile() {
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();

		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(supplier.getSupplierId());
		ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
		SupplierSearchResDTO smResDTO = supplierSearchSO
				.retriveLatestApplBySupplierId(supplierSearchReqDTO);

		if (smResDTO.getSmAppl() != null) {

			String applNo = smResDTO.getSmAppl().getApplNo();
			getManagedBean().setApplicationNoParam(applNo + "_APP");
			initApplication(getManagedBean().getApplicationNoParam());
			String applType = getManagedBean().getSmAppl().getApplType();
			String redirectToAppl = SmApplicationUtil
					.getReviewApplicationSupplierProfileBasePage(applType);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectToAppl);
		}
	}

}
