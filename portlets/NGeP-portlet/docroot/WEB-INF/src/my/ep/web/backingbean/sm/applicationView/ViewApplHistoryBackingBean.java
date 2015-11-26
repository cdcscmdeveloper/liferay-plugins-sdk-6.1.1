package my.ep.web.backingbean.sm.applicationView;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "viewApplHistoryBackingBean")
@RequestScoped
public class ViewApplHistoryBackingBean extends
		BaseReviewApplicationBackingBean<ViewApplHistoryManagedBean> {

	/**
	 * Pre-render for View Application History
	 */
	public void preRenderViewApplicationHistory() {
		renewSesionObject();
		initLogin();
		getManagedBean().setViewSupplierProfile(true);
		getManagedBean().setReviewApplicationExist(false);
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setRenderedBackButton(true);

		if (!getManagedBean().isSupplierUser()) {
			String supplierIdParam = getSupplierIdParam();
			getManagedBean().setRegStatusDescList(
					retrieveRegistrationStatusList());
			getManagedBean().setZoneList(
					SmServiceLookup.getZoneSO().retrieveActiveZoneList()
							.getZoneList());
			if (StringUtils.isNotBlank(supplierIdParam)) {
				SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
				reqDTO.setSupplierId(Long.parseLong(supplierIdParam));
				SmSupplier paramSupplier = SmServiceLookup
						.getSupplierSearchSO().retriveSupplierById(reqDTO)
						.getSmSupplier();
				getManagedBean().setSearchSupplier(new SmSupplier());
				getManagedBean().getSearchSupplier().setRegNo(
						paramSupplier.getRegNo());
				getManagedBean().searchSupplier();
			}
		}

		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewApplicationHistory/searchSupplierApplicationHistoryPage.xhtml",
						"",null);
	}

	/**
	 * Get Application Status
	 * 
	 * @param String
	 *            statusId
	 * @return String
	 */
	public String getApplicationStatus() {
		String statusDesc = "";
		SmWorkflowStatus sts = SmCommonUtil
				.getCurrentActiveWokFlowStatus(getManagedBean().getSmAppl()
						.getApplId());

		if (sts != null && sts.getPmStatus() != null
				&& sts.getPmStatus().getStatusId() != null) {
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(sts.getPmStatus().getStatusId());
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale()
					.getLanguage());
			StatusResDTO resDTO = SmServiceLookup.getStatusSO()
					.retrieveStatusDescById(reqDTO);
			if (resDTO != null && resDTO.getPmStatusDesc() != null) {
				statusDesc = resDTO.getPmStatusDesc().getStatusName();
			}
		}
		return statusDesc;
	}

	public void search() {
		getManagedBean().searchSupplier();
	}

	public void initSmApplByApplNo(String applNo) {
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(applNo);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
		viewApplication(smAppl);
	}

}
