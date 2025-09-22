package my.ep.web.backingbean.sm.compexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "smCompExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static String updateInfoTncXhtml = "updateInfoTnc.xhtml";
	
	/**
	 * Prerender for Update Info
	 */
	public void preRenderUpdateInfo() {
		renewSesionObject();
		this.getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage(updateInfoTncXhtml, "", null, null);
	}
	
	public void proceedAfterUpdateTnC(){
		
	}
	
}