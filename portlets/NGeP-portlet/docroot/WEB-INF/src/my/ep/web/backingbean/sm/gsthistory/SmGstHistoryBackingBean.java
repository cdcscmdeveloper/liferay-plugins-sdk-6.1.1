package my.ep.web.backingbean.sm.gsthistory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.gsthistory.SmGstHistoryManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "gstHistoryBackingBean")
@RequestScoped
public class SmGstHistoryBackingBean extends
		BaseBackingBean<SmGstHistoryManagedBean> {
	public void preRenderGstHistory() {
		renewSesionObject();
		getManagedBean().setSearchSupplier(new SmSupplier());
		getManagedBean().setZoneList(
				SmServiceLookup.getZoneSO().retrieveActiveZoneList()
						.getZoneList());
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/gstHistory/searchGstHistory.xhtml", "");
	}

	public void resetSearch() {
		getManagedBean().getSearchSupplier().setCompanyName("");
		getManagedBean().getSearchSupplier().setRegNo("");
		getManagedBean().setSearchMofNo("");
		getManagedBean().setSearchSuppType("");
		getManagedBean().setSearchPmZoneId(null);
	}
}
