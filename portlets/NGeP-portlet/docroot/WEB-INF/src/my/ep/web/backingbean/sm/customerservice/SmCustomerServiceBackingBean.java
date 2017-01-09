package my.ep.web.backingbean.sm.customerservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.sm.applicationView.BaseReviewApplicationBackingBean;
import my.ep.web.managedbean.sm.customerservice.SmCustomerServiceManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "smCustomerServiceBackingBean")
@RequestScoped
public class SmCustomerServiceBackingBean extends
		BaseReviewApplicationBackingBean<SmCustomerServiceManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2440882995327138193L;

	private static int SEARCH_PAGE = 1;
	private static int DETAILS_PAGE = 2;

	public void preRenderCustomerService() {
		renewSesionObject();
		initLogin();
		getManagedBean().setTargetPage(SEARCH_PAGE);
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/customerService/smCustomerServiceBasePage.xhtml",
				"", null);
	}

	/**
	 * Initialized application by application ID
	 * 
	 * @param String
	 *            applNo
	 */
	public void view(Long applId) {
		getManagedBean().setViewSupplierProfile(true);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		setApplication(smAppl);

		// set MOF registration status
		String mofStatus = StringUtils.EMPTY;
		if (getManagedBean().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_SUSPENDED) {
			mofStatus = SmCommonUtil.getMessage("lbl.sm.mof.status.deleted",
					null);
		} else {
			if (getManagedBean().getSmMofAccount() != null
					|| getManagedBean().getSmMofCert() != null) {
				if (getManagedBean().getSmMofAccount() != null) {
					if (getManagedBean().getSmMofAccount().getExpDate()
							.after(new Date())) {
						mofStatus = SmCommonUtil.getMessage(
								"lbl.sm.mof.status.expired", null);
					}
				} else if (getManagedBean().getSmMofCert() != null) {

				}
			}
		}
		getManagedBean().setMofRegStatus(mofStatus);
		getManagedBean().setTargetPage(DETAILS_PAGE);

		List<SmPersonnel> personnelsToRemove = new ArrayList<SmPersonnel>();
		// set supplier admin
		for (SmPersonnel per : getManagedBean().getSmAppl().getSmPersonnels()) {
			if (per.getEpRole() != null
					&& (per.getEpRole().equals(
							RoleCode.BASIC_SUPPLIER_ADMIN.value()) || per
							.getEpRole().equals(
									RoleCode.MOF_SUPPLIER_ADMIN.value()))) {
				getManagedBean().setSuppAdmin(per);
			}

			// filter to display only personnel that had login whether active or
			// deactivated
			if (per.getPmUser() == null) {
				personnelsToRemove.add(per);
			}

			// set personnel softcert expiry date
			if (per.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
				PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
				personnelReqDTO.setSmSupplierId(getManagedBean().getSmAppl()
						.getSmSupplier().getSupplierId());
				personnelReqDTO.setUserId(per.getPmUser().getUserId());
				SmSoftcertRequest softcertRequest = SmServiceLookup
						.getPersonnelSO()
						.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
								personnelReqDTO).getSoftcertRequest();
				if (softcertRequest != null
						&& softcertRequest.getPmDigiCert() != null
						&& softcertRequest.getPmDigiCert().getValidTo() != null) {
					per.setSoftcertValidityDate(DateUtils.dateToString(
							"dd/MM/yyyy", softcertRequest.getPmDigiCert()
									.getValidTo()));
				}
			}
		}
		getManagedBean().getSmAppl().getSmPersonnels()
				.removeAll(personnelsToRemove);

	}

	/**
	 * Perform search supplier application history
	 */
	public void search() {
		getManagedBean().getSmSupplierDataModel().reloadData();
	}

	/**
	 * Back to search page
	 */
	public void back() {
		getManagedBean().setTargetPage(SEARCH_PAGE);
	}

}
