package my.ep.web.backingbean.sm.compexp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.CompExpManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * @author firdaus.omar Redmine #9299
 */
@ManagedBean(name = "compExpBB")
@RequestScoped
public class CompExpBackingBean extends BaseBackingBean<CompExpManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Prerender method for screen Update Company Experience Information
	 */
	public void preRenderCompExp() {
		renewSesionObject();
		SmCommonUtil.redirectToPage("compExpTnc.xhtml", "", null, null);
	}

	/**
	 * To redirect to main SM Update Company Experience Information
	 */
	public void proceedAfterAcceptTnC() {
		List<SmCompExpDraft> smDraftList = new ArrayList<SmCompExpDraft>();
		SmCompExpDraft smDraft = new SmCompExpDraft();
		smDraftList.add(smDraft);
		this.getManagedBean().setSmDraftList(smDraftList);
		this.getManagedBean().setCtDraftList(null);
		SmCommonUtil.redirectToPage("compExpMain.xhtml", "", null, null);
	}

	/**
	 * To redirect to draft screen
	 */
	public void redirectToDraft() {
		SmCommonUtil.redirectToPage("compExpDraft.xhtml", "", null, null);
	}

}