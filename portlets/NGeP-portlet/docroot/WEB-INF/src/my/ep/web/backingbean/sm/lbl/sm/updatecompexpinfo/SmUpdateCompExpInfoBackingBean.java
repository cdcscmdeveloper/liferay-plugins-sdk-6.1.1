package my.ep.web.backingbean.sm.lbl.sm.updatecompexpinfo;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.updatecompexpinfo.SmUpdateCompExpInfoManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "updateCompExpInfoBB")
@RequestScoped
public class SmUpdateCompExpInfoBackingBean extends BaseBackingBean<SmUpdateCompExpInfoManagedBean> {

	private static final long serialVersionUID = 1L;

	public void preRenderUpdateCompExpInfo() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("updateCompExpInfoTnc.xhtml", "", null, null);
	}
	
	public void proceedAfterAcceptTnC() {
		SmCommonUtil.redirectToPage("updateCompExpInfoMain.xhtml", "", null, null);
	}

	public void redirectToDraftPage() {
		SmCommonUtil.redirectToPage("updateCompExpInfoDraft.xhtml", "", null, null);
	}
}