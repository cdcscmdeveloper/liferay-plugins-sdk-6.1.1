package my.ep.web.backingbean.sm.reactivatebasic;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

@ManagedBean(name = "reactivateBasicBackingBean")
@ViewScoped
public class ReactivateBasicBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	private ReactivateSupplierVO reactivateBasic;

	/**
	 * Initialize Reactivate Basic Account
	 */
	@PostConstruct
	public void init() {
		reactivateBasic = new ReactivateSupplierVO();
	}

	/**
	 * Reactivate basic account
	 */
	public void reactivateBasic() {
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
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.INFO_721028);
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
