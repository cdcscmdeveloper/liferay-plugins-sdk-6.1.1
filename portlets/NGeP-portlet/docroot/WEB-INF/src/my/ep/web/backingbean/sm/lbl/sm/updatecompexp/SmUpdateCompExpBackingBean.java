package my.ep.web.backingbean.sm.lbl.sm.updatecompexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.updatecompexp.SmUpdateCompExpManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.primefaces.context.RequestContext;

/**
 * @author firdaus.omar
 * 
 *         Backing bean for SM Update Company Experience Information
 */
@ManagedBean(name = "updateCompExpBB")
@RequestScoped
public class SmUpdateCompExpBackingBean extends BaseBackingBean<SmUpdateCompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Pre-render for SM Update Company Experience Information
	 */
	public void preRenderUpdateCompExp() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("updateCompExpTnc.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information after user
	 * accept term and condition
	 */
	public void proceedAfterAcceptTnC() {
		SmCommonUtil.redirectToPage("updateCompExpMain.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information after user
	 * click draft hyperlink
	 */
	public void redirectToDraftPage() {
		SmCommonUtil.redirectToPage("updateCompExpDraft.xhtml", "", null, null);
	}

	/**
	 * To show popup when user click edit on company experience
	 */
	public void onClickEditAction() {
		RequestContext.getCurrentInstance().execute("updateCompExpEditWV.show();");
	}
}