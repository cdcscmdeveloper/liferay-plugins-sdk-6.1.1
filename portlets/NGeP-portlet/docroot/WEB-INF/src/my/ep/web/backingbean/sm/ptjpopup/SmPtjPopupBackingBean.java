package my.ep.web.backingbean.sm.ptjpopup;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.compexp.SmCompExpManagedBean;
import my.ep.web.managedbean.sm.ptjpopup.SmPtjPopupManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

/**
 * BackingBean for Sm PTJ Popup
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smPtjPopupBB")
@RequestScoped
public class SmPtjPopupBackingBean extends BaseBackingBean<SmPtjPopupManagedBean> {

	private static final long serialVersionUID = 1L;

	public void initPopup() {
	}

	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}

	public SelectItem[] getPtjGroupDropdown() {
		return PmCommonUtil.getJabatanDropdown(getManagedBean().getMinistryProfileId());
	}

	public static SelectItem[] getPtjCodeNameDropDown() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage("lbl.sm.ptj.popup.ptjCode",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage("lbl.sm.ptj.popup.ptjName",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		return items;
	}

}