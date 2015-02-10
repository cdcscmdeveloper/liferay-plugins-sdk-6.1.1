package my.ep.web.backingbean.sm.reactivatebasic;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.vo.ReactivateSupplierVO;

@ManagedBean(name = "reactivateBasicBackingBean")
@ViewScoped
public class ReactivateBasicBackingBean extends
BaseBackingBean<BaseManagedBean>{

	private ReactivateSupplierVO reactivateBasic;

	@PostConstruct
	public void init() {
		reactivateBasic = new ReactivateSupplierVO();
	}

	public void reactivateBasic() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setIdentificationNo(reactivateBasic.getIdentificationNo());
		reqDTO.setEmail(reactivateBasic.getEmail());
		reqDTO.setEstablishDate(reactivateBasic.getEstablishDate());
		reqDTO.setRegNo(reactivateBasic.getRegNo());

		IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IRegistrationSO.class);
		try {
			RegistrationResDTO resDTO = reqSO.reactivateBasicAccount(reqDTO);

			if (resDTO.isSuccessful()) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(
										MessageConst.INFO_721028,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/sm/userProfileMaintenance/outputMsg");
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	public ReactivateSupplierVO getReactivateBasic() {
		return reactivateBasic;
	}

	public void setReactivateBasic(ReactivateSupplierVO reactivateBasic) {
		this.reactivateBasic = reactivateBasic;
	}
}
