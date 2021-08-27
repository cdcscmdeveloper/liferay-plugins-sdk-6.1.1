package my.ep.web.backingbean.sm.personnelrequestactivation;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.personnelrequestactivation.SmPersonnelRequestActivationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "smPersonnelRequestActivationBackingBean")
@ViewScoped
public class SmPersonnelRequestActivationBackingBean extends
		BaseBackingBean<SmPersonnelRequestActivationManagedBean> {

	/**
	 * Pre-render request activation email
	 */
	public void preRenderRequestActivation() {
		renewSesionObject();
		getManagedBean().setSmSupplier(SmApplicationUtil.getCurrentSupplier());
		initExpiredPersonnels();
		getManagedBean().setTargetPage("1");
		SmCommonUtil.setMDCUserId();
		SmCommonUtil
				.redirectToPage("requestActivationBasePage.xhtml", "", null);
	}

	/**
	 * Navigate to page
	 * 
	 * @param String
	 *            page
	 */
	public void navigateToPage(String page) {
		getManagedBean().setTargetPage(page);
	}

	/**
	 * Re - send activation email to selected personnel
	 */
	public void resendActivation() {
		FacesScopes fs = new FacesScopes();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setSmPersonnelList(new ArrayList<SmPersonnel>());
		reqDTO.getSmPersonnelList().add(
				getManagedBean().getSelectedExpiredActivationPersonnel());
		SmServiceLookup.getPersonnelSO().resendPersonnelActivationEmail(reqDTO);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/userProfileMaintenance/outputMsg",
				MessageConst.INFO_721032, null);
	}

	/**
	 * Initialize all personnel that activation link already expired by supplier
	 */
	private void initExpiredPersonnels() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getSmSupplier().getSupplierId());
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrieveExpiredPendingActivationApprovedPersonnels(reqDTO);
		if (resDTO.getSmPersonnelList() != null
				&& !resDTO.getSmPersonnelList().isEmpty()) {
			getManagedBean().setExpiredActivationPersonnels(
					resDTO.getSmPersonnelList());
		} else {
			getManagedBean().setExpiredActivationPersonnels(
					new ArrayList<SmPersonnel>());
		}
		SmCommonUtil.setRoleName(getManagedBean().getSmSupplier().getSmAppl()
				.getSupplierType(), getManagedBean()
				.getExpiredActivationPersonnels());

	}
	
	// #28720 Start
	public void onChangeEmail()
	{
		// managedBean.selectedAdmin.email
		String email = this.getManagedBean().getSelectedExpiredActivationPersonnel().getEmail().toLowerCase();
		if(email.contains("@yahoo.com") || email.contains("@ymail.com") || email.contains("@rocketmail.com"))
		{
			this.getManagedBean().getSelectedExpiredActivationPersonnel().setEmail("");
			RequestContext.getCurrentInstance().execute(
					"emailPopUpWidgetVar.show()");
		}
	}
	// #28720 End
}
