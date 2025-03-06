package my.ep.web.backingbean.sm.lbl.sm.updatecompexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.updatecompexp.SmUpdateCompExpManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "updateCompExpBB")
@RequestScoped
public class SmUpdateCompExpBackingBean extends BaseBackingBean<SmUpdateCompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	public void preRenderUpdateCompExp() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("updateCompExpTnc.xhtml", "", null, null);
	}
	
	public void proceedAfterAcceptTnC() {
		SmCommonUtil.redirectToPage("updateCompExpMain.xhtml", "", null, null);
	}

	public void redirectToDraftPage() {
		SmCommonUtil.redirectToPage("updateCompExpDraft.xhtml", "", null, null);
	}
	
	public void onClickEditAction() {
		RequestContext.getCurrentInstance().execute("updateCompExpDialogWV.show();");
	}
}