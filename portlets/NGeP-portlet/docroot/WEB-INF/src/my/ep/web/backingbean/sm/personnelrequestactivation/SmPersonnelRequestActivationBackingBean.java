package my.ep.web.backingbean.sm.personnelrequestactivation;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
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
		//26238 - 27848 - start
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		if (currSupp != null){
			List<SmDisciplinaryAction> activeDA = SmApplicationUtil.retrieveActiveSmDisciplinaryActionBySupplier(currSupp);
			//26238 - 39217
			getManagedBean().setDisciplinaryAction(SmApplicationUtil.hasCannotLoginDA(activeDA));
			if (getManagedBean().isDisciplinaryAction()){
				FacesContext.getCurrentInstance().addMessage("imposedDisciplinary", new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
						SmCommonUtil.getMessage("lbl.sm.not.allowed.to.proceed.due.to.disciplinary.action",
								new String[] { "" })));
			}
		}
		getManagedBean().setSmSupplier(currSupp);
		//26238 - 27848 - end
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

}
