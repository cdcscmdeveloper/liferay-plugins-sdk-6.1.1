package my.ep.web.backingbean.sm.applicationView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.sm.SmTrackingDiaryConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.backingbean.sm.virtualcert.VirtualCertBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "viewApplHistoryBackingBean")
@RequestScoped
public class ViewApplHistoryBackingBean extends
		BaseReviewApplicationBackingBean<ViewApplHistoryManagedBean> {

	public static VirtualCertBackingBean virtualCertInstance;
	public static CommonApplBackingBean commonApplInstance;
	public static PmTrackingDiaryBackingBean trackinDiaryInstance;
	public static RegistrationBackingBean registrationBackingBean;

	/**
	 * Set tab value
	 * 
	 * @param int outputTab
	 * @param String
	 *            outputPageType
	 */
	public void setTabValue(int outputTab, String outputPageType) {
		getManagedBean().setTabActiveIndex(outputTab);
		getManagedBean().setRedirectToPersonPage(false);
		getManagedBean().setCurrentPageType(outputPageType);
		getManagedBean().getSmAppl().setCurrentActiveTab(outputTab);
		if (!StringUtils.isBlank(outputPageType)
				&& outputPageType
						.equals(my.ep.web.constant.sm.SupplierManagementConst.SM_APPL_PAGE_TYPE_TRACKING_DIARY)) {
			getManagedBean().getTrackingDiary().setTrackingDiaryParam(
					SmTrackingDiaryConst.SM_DEFAULT_DOC_TYPE,
					getManagedBean().getSmAppl().getApplId(),
					false);
		}
	}

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
						"", null);
	}

	/**
	 * View revision application by approver for task that is not claimed
	 * 
	 * @param String
	 *            applNo
	 */
	public void viewUnClaimTask(String applNo) {
		getManagedBean().setViewApplicationHistory(true);
		getManagedBean().setViewSupplierProfile(true);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(applNo);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
		setApplication(smAppl);
	}

	/**
	 * On click claim task button
	 */
	public void onClickClaimButton() {
		claimBpmTask();
		getCommonApplInstance().preRenderView();
	}

	public void redirectToResubmit() {
		SmCommonUtil.redirectToExternalUrl(getManagedBean().getResubmitUrl());
	}

	/**
	 * Get Application Status
	 * 
	 * @param PmStatus
	 *            status
	 * @return String
	 */
	private String getApplicationStatus(PmStatus status) {
		String statusDesc = StringUtils.EMPTY;
		StatusReqDTO reqDTO = new StatusReqDTO();
		reqDTO.setStatusId(status.getStatusId());
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		StatusResDTO resDTO = SmServiceLookup.getStatusSO()
				.retrieveStatusDescById(reqDTO);
		if (resDTO != null && resDTO.getPmStatusDesc() != null) {
			statusDesc = resDTO.getPmStatusDesc().getStatusName();
		}
		return statusDesc;
	}

	private void initWorkflowStatus(SmAppl smAppl, int index) {
		List<String> rejectedStatus = Arrays
				.asList(SupplierManagementConst.PM_STATUS_REJECTED_BY_PROCESSING_OFFICER,
						SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER,
						SupplierManagementConst.PM_STATUS_REJECTED_BY_SYSTEM);
		SmWorkflowStatus sts = SmCommonUtil
				.getCurrentActiveWokFlowStatus(smAppl.getApplId());
		if (sts != null && sts.getPmStatus() != null) {
			smAppl.setWorkflowStatus(sts.getPmStatus());
			smAppl.setWorkflowStatusDesc(getApplicationStatus(sts.getPmStatus()));
			if (rejectedStatus.contains(sts.getPmStatus().getStatusId()
					.toString())) {
				getManagedBean().setResubmitUrl(
						SmCommonUtil.getEpBaseUrl()
								+ "/resubmit-application?apId="
								+ smAppl.getApplId());
			}
		}
	}

	/**
	 * Perform search supplier application history
	 */
	public void search() {
		getManagedBean().searchSupplier();
	}

	/**
	 * Initialized application by application no
	 * 
	 * @param String
	 *            applNo
	 */
	public void initSmApplByApplNo(Long applId, int index) {
		getManagedBean().setResubmitUrl(StringUtils.EMPTY);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplId(applId);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplicationDetailsById(reqDTO).getSmAppl();
		if (smAppl.getApplType()
				.equals(SupplierManagementConst.APPL_TYPE_BASIC)) {
			getRegistrationBackingBean().getManagedBean().setViewMode(true);
			getRegistrationBackingBean().viewOnlineAccount();
		} else {
			viewApplication(smAppl);
			initBumiRecommendNote();
			initVirtualCert();
			initWorkflowStatus(getManagedBean().getSmAppl(), index);
		}
	}

	/**
	 * Download virtual certificate
	 * 
	 * @param SmMofCert
	 *            cert
	 */
	public void printVirtualCert(SmMofCert cert) {
		try {
			getVirtualCertInstance().printVirtualCert(cert);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Initialized virtual certificate by application Id
	 */
	private void initVirtualCert() {
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
		try {
			SmVirtualCertHelper.getInstance().autoGenerateVirtualCert(
					getManagedBean().getSmAppl().getApplId(), false);
			getManagedBean().setMofCertLs(
					SmServiceLookup.getVirtualCertSO()
							.retrieveMofCertsByApplId(reqDTO).getSmMofCerts());
		} catch (Exception ex) {
			getManagedBean().setMofCertLs(
					SmServiceLookup.getVirtualCertSO()
							.retrieveMofCertsByApplId(reqDTO).getSmMofCerts());
		}
	}

	/**
	 * Get VirtualCertBackingBean instance
	 * 
	 * @return VirtualCertBackingBean
	 */
	public static VirtualCertBackingBean getVirtualCertInstance() {
		if (virtualCertInstance == null) {
			virtualCertInstance = new VirtualCertBackingBean();
		}
		return virtualCertInstance;

	}

	/**
	 * Get CommonApplBackingBean instance
	 * 
	 * @return CommonApplBackingBean
	 */
	public static CommonApplBackingBean getCommonApplInstance() {
		if (commonApplInstance == null) {
			commonApplInstance = new CommonApplBackingBean();
		}
		return commonApplInstance;

	}

	/**
	 * Get PmTrackingDiaryBackingBean instance
	 * 
	 * @return PmTrackingDiaryBackingBean
	 */
	public static PmTrackingDiaryBackingBean getTrackinDiaryInstance() {
		if (trackinDiaryInstance == null) {
			trackinDiaryInstance = new PmTrackingDiaryBackingBean();
		}
		return trackinDiaryInstance;

	}

	/**
	 * Get RegistrationBackingBean instance
	 * 
	 * @return RegistrationBackingBean
	 */
	public static RegistrationBackingBean getRegistrationBackingBean() {
		if (registrationBackingBean == null) {
			registrationBackingBean = new RegistrationBackingBean();
		}
		return registrationBackingBean;

	}

}
