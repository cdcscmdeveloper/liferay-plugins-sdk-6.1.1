package my.ep.web.backingbean.sm.reactivatebasic;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

@ManagedBean(name = "reactivateBasicBackingBean")
@SessionScoped
public class ReactivateBasicBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	private ReactivateSupplierVO reactivateBasic;

	/**
	 * Reactivate basic account
	 */
	public void reactivateBasic() {
		reactivateBasic = new ReactivateSupplierVO();
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setIdentificationNo(reactivateBasic.getIdentificationNo());
		reqDTO.setEmail(reactivateBasic.getEmail());
		reqDTO.setEstablishDate(reactivateBasic.getEstablishDate());
		reqDTO.setRegNo(reactivateBasic.getRegNo());

		try {
			RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO()
					.requestToReactivateBasicAccount(reqDTO);

			if (resDTO.isSuccessful()) {
				renewSesionObject();
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.INFO_721028);
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	public void basicAccountReactivation() {
		renewSesionObject();
		try {
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			String personnelId = originalServletRequest.getParameter("pr");

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setPersonnelId(Long.parseLong(personnelId));
			SmSupplier basicSupplierToReactivate = SmServiceLookup
					.getRegistrationSO()
					.retrieveValidReactivateBasicSupplier(reqDTO).getSmSupp();
			if (basicSupplierToReactivate == null) {
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721212);
			}
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721212);
		}
	}

	public ReactivateSupplierVO getReactivateBasic() {
		return reactivateBasic;
	}

	public void setReactivateBasic(ReactivateSupplierVO reactivateBasic) {
		this.reactivateBasic = reactivateBasic;
	}
}
