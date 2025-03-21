package my.ep.web.backingbean.sm.compexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * BackingBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "compExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Prerender method for screen Update Company Experience Information
	 */
	public void preRenderCompExp() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage("smCompExpTnc.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information screen
	 */
	public void proceedAfterAcceptTnC() {
		SmCommonUtil.redirectToPage("smCompExpMain.xhtml", "", null, null);
	}

	/**
	 * To redirect to SM Update Company Experience Information Draft screen
	 */
	public void redirectToDraft() {
		SmCommonUtil.redirectToPage("smCompExpDraft.xhtml", "", null, null);
	}

}