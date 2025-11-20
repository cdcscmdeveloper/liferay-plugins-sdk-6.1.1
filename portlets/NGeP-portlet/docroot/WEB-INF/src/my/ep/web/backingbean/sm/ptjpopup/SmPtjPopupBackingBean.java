package my.ep.web.backingbean.sm.ptjpopup;

import javax.el.MethodExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.constant.MessageConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.ptjpopup.SmPtjPopupManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.primefaces.context.RequestContext;

/**
 * BackingBean for Sm PTJ Popup
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "smPtjPopupBB")
@RequestScoped
public class SmPtjPopupBackingBean extends BaseBackingBean<SmPtjPopupManagedBean> {

	private static final long serialVersionUID = 1L;

	/**
	 * Initialize PTJ popup dialog
	 */
	public void initPtjPopup() {
		reset();
	}

	/**
	 * Reset all field to default value
	 */
	public void reset() {
		this.getManagedBean().setMinistryProfileId(null);
		this.getManagedBean().setJabatanProfileId(null);
		this.getManagedBean().setPtjCodeNameKey(ProfileManagementConst.PTJ_CODE);
		this.getManagedBean().setPtjCodeNameValue(null);
		this.getManagedBean().getOrgProfileDataModel().resetData();
		this.getManagedBean().setDisabledSelect(true);
	}

	/**
	 * Retrieve Ministry dropdown list
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getMinistryDropdown() {
		return PmCommonUtil.getMinistryDropdown();
	}

	/**
	 * Retrieve PTJ Group dropdown list
	 * 
	 * @return SelectItem[]
	 */
	public SelectItem[] getPtjGroupDropdown() {
		if (null != this.getManagedBean().getMinistryProfileId()) {
			this.getManagedBean().setDisabledPtjGroup(false);
		} else {
			this.getManagedBean().setDisabledPtjGroup(true);
		}
		return PmCommonUtil.getJabatanDropdown(this.getManagedBean().getMinistryProfileId());
	}

	/**
	 * Retrieve PTJ Code Name dropdown list
	 * 
	 * @return SelectItem[]
	 */
	public static SelectItem[] getPtjCodeNameDropDown() {
		SelectItem[] items = new SelectItem[2];
		items[0] = new SelectItem(ProfileManagementConst.PTJ_CODE, ResourceBundleUtils.getMessage("lbl.sm.ptj.popup.ptjCode",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		items[1] = new SelectItem(ProfileManagementConst.PTJ_NAME, ResourceBundleUtils.getMessage("lbl.sm.ptj.popup.ptjName",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		return items;
	}

	/**
	 * Search PTJ list based on criteria
	 */
	public void search() {
		if (null != this.getManagedBean().getPtjCodeNameValue() && !this.getManagedBean().getPtjCodeNameValue().isEmpty()
				&& this.getManagedBean().getPtjCodeNameValue().length() < 3) {
			throw new EpException(MessageConst.ERROR_775221);
		}
		this.getManagedBean().getOrgProfileDataModel().reloadData();
	}

	/**
	 * Listener to enable select button
	 */
	public void enableSelect() {
		if (null != this.getManagedBean().getSelectedPtj()) {
			this.getManagedBean().setDisabledSelect(false);
		} else {
			this.getManagedBean().setDisabledSelect(true);
		}
	}

	/**
	 * Get method to return value of selected PTJ to main managed bean
	 * 
	 * @param methodActionStr
	 *            String
	 */
	public void okAction(String methodActionStr) {
		if (!methodActionStr.isEmpty()) {
			MethodExpression methodExp = FacesScopes.createMethodExpression(FacesContext.getCurrentInstance(), methodActionStr, null,
					new Class[] { PmOrgProfile.class });
			if (this.getManagedBean().getSelectedPtj() != null) {
				methodExp.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[] { this.getManagedBean().getSelectedPtj() });
			}
			RequestContext.getCurrentInstance().execute("smPtjPopupWV.hide();");
		}
	}

}