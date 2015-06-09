package my.ep.web.backingbean.sm.supplierdatamaintenance;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "supplierDataMaintenanceBackingBean")
@RequestScoped
public class SupplierDataMaintenanceBackingBean extends
		BaseReviewApplicationBackingBean<ViewSuppProfileManagedBean> {

	public void preRenderSupplierDataMaintenance() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(false);
		getManagedBean().setRegStatusDescList(
				retrieveRegistrationStatusList());
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/supplierdatamaintenance/searchSupplierDataMaintenance.xhtml",
						"");
	}

	public void navigateToMaintenance(SmAppl selectedSmAppl) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(selectedSmAppl.getApplNo());
		getManagedBean().setSmAppl(
				SmServiceLookup.getMofRegistrationSO()
						.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl());
	}
	
	public void search() {
		getManagedBean().searchSupplier();
	}

}
