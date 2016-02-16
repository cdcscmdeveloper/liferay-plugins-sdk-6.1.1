package my.ep.web.backingbean.sm.applicationView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

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
import my.ep.web.backingbean.sm.virtualcert.VirtualCertBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewApplHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "viewApplHistoryBackingBean")
@RequestScoped
public class ViewApplHistoryBackingBean extends
		BaseReviewApplicationBackingBean<ViewApplHistoryManagedBean> {

	public static VirtualCertBackingBean instance;

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
						SupplierManagementConst.PM_STATUS_REJECTED_BY_APPROVER);
		SmWorkflowStatus sts = SmCommonUtil
				.getCurrentActiveWokFlowStatus(smAppl.getApplId());
		if (sts != null && sts.getPmStatus() != null) {
			smAppl.setWorkflowStatus(sts.getPmStatus());
			smAppl.setWorkflowStatusDesc(getApplicationStatus(sts.getPmStatus()));
			if (index == 0
					&& rejectedStatus.contains(sts.getPmStatus().getStatusId()
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
	public void initSmApplByApplNo(String applNo, int index) {
		getManagedBean().setResubmitUrl(StringUtils.EMPTY);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setApplNo(applNo);
		SmAppl smAppl = SmServiceLookup.getMofRegistrationSO()
				.retrieveSmApplWithChildByApplNo(reqDTO).getSmAppl();
		viewApplication(smAppl);
		initBumiRecommendNote();
		initVirtualCert();
		initWorkflowStatus(getManagedBean().getSmAppl(), index);
	}

	/**
	 * Download virtual certificate
	 * 
	 * @param SmMofCert
	 *            cert
	 */
	public void printVirtualCert(SmMofCert cert) {
		try {
			getInstance().printVirtualCert(cert);
		} catch (IOException e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Initialized virtual certificate by application Id
	 */
	private void initVirtualCert() {
		boolean isRegenerate = false;
		VirtualCertReqDTO reqDTO = new VirtualCertReqDTO();
		reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
		List<SmMofCert> mofCerts = SmServiceLookup.getVirtualCertSO()
				.retrieveMofCertsByApplId(reqDTO).getSmMofCerts();
		if (!SmCommonUtil.isCollectionEmpty(mofCerts)) {
			for (SmMofCert mCert : mofCerts) {
				// if both is null, generate the pdf and save the file name and
				// path
				if (mCert != null) {
					if (mCert.getFileName() == null
							|| mCert.getFilePath() == null) {
						getInstance()
								.genReport(
										getInstance().getReportType(
												mCert.isBumiCert()),
										getInstance().getReportParam(mCert),
										mCert);
						isRegenerate = true;
					}
				}
			}
			if (isRegenerate) {
				mofCerts = SmServiceLookup.getVirtualCertSO()
						.retrieveMofCertsByApplId(reqDTO).getSmMofCerts();
			}
		}
		getManagedBean().setMofCertLs(mofCerts);
	}

	/**
	 * Get VirtualCertBackingBean instance
	 * 
	 * @return VirtualCertBackingBean
	 */
	public static VirtualCertBackingBean getInstance() {
		if (instance == null) {
			instance = new VirtualCertBackingBean();
		}
		return instance;

	}

}
