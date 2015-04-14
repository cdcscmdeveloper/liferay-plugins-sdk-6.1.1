package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.commons.lang3.StringUtils;

import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "viewApplHistoryBackingBean")
@RequestScoped
public class ViewApplHistoryBackingBean extends
		BaseReviewApplicationBackingBean<ViewApplHistoryManagedBean> {

	/**
	 * Pre-render for View Application History
	 */
	public void preRenderViewApplicationHistory() {
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setRenderedBackButton(true);
		
		if(!getManagedBean().isSupplierUser()){
			String supplierIdParam = getSupplierIdParam();
			if(StringUtils.isNotBlank(supplierIdParam)){
				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSupplierId(Long.parseLong(supplierIdParam));
				getManagedBean().setSearchSupplier(
						SmServiceLookup.getSupplierSearchSO()
								.retriveSupplierById(reqDTO).getSmSupplier());
			}
		}
		
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewApplicationHistory/searchSupplierApplicationHistoryPage.xhtml",
						"");
	}

}
