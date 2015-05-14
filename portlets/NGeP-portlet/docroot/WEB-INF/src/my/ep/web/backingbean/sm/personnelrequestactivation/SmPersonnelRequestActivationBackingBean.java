package my.ep.web.backingbean.sm.personnelrequestactivation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.springframework.util.CollectionUtils;

@ManagedBean(name = "smPersonnelRequestActivationBackingBean")
@ViewScoped
public class SmPersonnelRequestActivationBackingBean {
	private SmSupplier smSupplier;
	private List<SmPersonnel> selectedExpiredActivationPersonnels;
	private List<SmPersonnel> expiredActivationPersonnels;

	/**
	 * Initialized reactivation information
	 */
	@PostConstruct
	public void init() {
		selectedExpiredActivationPersonnels = new ArrayList<SmPersonnel>();
		smSupplier = SmApplicationUtil.getCurrentSupplier();
		initExpiredPersonnels();
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Re - send activation email to selected personnel
	 */
	public void resendActivation() {
		validate();
		FacesScopes fs = new FacesScopes();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
		reqDTO.setSmPersonnelList(selectedExpiredActivationPersonnels);
		SmServiceLookup.getPersonnelSO().resendPersonnelActivationEmail(reqDTO);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/userProfileMaintenance/outputMsg",
				MessageConst.INFO_721028);
	}

	/**
	 * Initialize all personnel that activation link already expired by supplier
	 */
	private void initExpiredPersonnels() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrieveExpiredPendingActivationApprovedPersonnels(reqDTO);
		if (resDTO.getSmPersonnelList() != null
				&& !resDTO.getSmPersonnelList().isEmpty()) {
			expiredActivationPersonnels = resDTO.getSmPersonnelList();
		} else {
			expiredActivationPersonnels = new ArrayList<SmPersonnel>();
		}
		SmCommonUtil.setRoleName(smSupplier.getSmAppl().getSupplierType(),
				expiredActivationPersonnels);

	}

	/**
	 * Validate if dont have any personnel selected for resend activation email
	 */
	private void validate() {
		if (CollectionUtils.isEmpty(selectedExpiredActivationPersonnels)) {
			throw new EpException(MessageConst.ERROR_722587,
					new String[] { SmCommonUtil.getMessage("lbl.sm.personnel",
							null) });
		}
	}

	public List<SmPersonnel> getSelectedExpiredActivationPersonnels() {
		return selectedExpiredActivationPersonnels;
	}

	public void setSelectedExpiredActivationPersonnels(
			List<SmPersonnel> selectedExpiredActivationPersonnels) {
		this.selectedExpiredActivationPersonnels = selectedExpiredActivationPersonnels;
	}

	public List<SmPersonnel> getExpiredActivationPersonnels() {
		return expiredActivationPersonnels;
	}

	public void setExpiredActivationPersonnels(
			List<SmPersonnel> expiredActivationPersonnels) {
		this.expiredActivationPersonnels = expiredActivationPersonnels;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

}
