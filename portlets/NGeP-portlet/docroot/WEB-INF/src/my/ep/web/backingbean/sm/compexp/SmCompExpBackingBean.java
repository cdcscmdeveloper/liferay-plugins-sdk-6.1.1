package my.ep.web.backingbean.sm.compexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.compexp.ISmCompExpSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.compexp.SmCompExpReqDTO;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * @redmine #9299 
 */
@ManagedBean(name = "smCompExpBB")
@RequestScoped
public class SmCompExpBackingBean extends BaseBackingBean<SmCompExpManagedBean> {

	private static final long serialVersionUID = 1L;
	private static String updateInfoTncXhtml = "updateInfoTnc.xhtml";
	private static String updateInfoMainXhtml = "updateInfoMain.xhtml";

	/**
	 * Prerender for Update Info
	 */
	public void preRenderUpdateInfo() {
		renewSesionObject();
		this.getManagedBean().setSupplier(SmApplicationUtil.getCurrentSupplier());
		SmCommonUtil.redirectToPage(updateInfoTncXhtml, "", null, null);
	}

	/**
	 * Redirect to Update Info main screen
	 */
	public void proceedAfterUpdateTnC() {
		this.getManagedBean().setDraftExist(checkDraftExist());
		SmCommonUtil.redirectToPage(updateInfoMainXhtml, "", null, null);
	}

	private boolean checkDraftExist() {
		SmCompExpReqDTO reqDTO = new SmCompExpReqDTO();
		reqDTO.setSupplierId(this.getManagedBean().getSmSupplier().getSupplierId());
		return smCompExpSo().checkDraftExist(reqDTO);
	}

	private ISmCompExpSO smCompExpSo() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmCompExpSO.class);
	}

	/**
	 * Redirect to Update Draft list screen
	 */
	public void redirectToDraft() {
		this.getManagedBean().getDraftSmDataModel().reloadData();
	}

}