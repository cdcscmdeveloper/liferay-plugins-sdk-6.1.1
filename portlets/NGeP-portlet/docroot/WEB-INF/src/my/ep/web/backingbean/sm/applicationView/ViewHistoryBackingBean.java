package my.ep.web.backingbean.sm.applicationView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.smRemarks.SmRemaksResDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.web.backingbean.sm.SmApplicationBackingBean;
import my.ep.web.backingbean.sm.commonappl.CommonApplBackingBean;
import my.ep.web.backingbean.sm.registration.RegistrationBackingBean;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.backingbean.sm.virtualcert.VirtualCertBackingBean;
import my.ep.web.backingbean.trackingdiary.PmTrackingDiaryBackingBean;
import my.ep.web.managedbean.sm.applicationView.ViewHistoryManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.sm.helper.SmVirtualCertHelper;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "viewHistoryBackingBean")
@RequestScoped
public class ViewHistoryBackingBean extends
		BaseViewBackingBean<ViewHistoryManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -760253500851978510L;
	public static VirtualCertBackingBean virtualCertInstance;
	public static CommonApplBackingBean commonApplInstance;
	public static PmTrackingDiaryBackingBean trackinDiaryInstance;
	public static RegistrationBackingBean registrationBackingBean;
	public static SvoSiteVisitBackingBean svoSiteVisitBackingBean;

	/**
	 * Pre-render for View Application History
	 */
	public void preRenderView() {
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
				if (paramSupplier.getSmAppl() != null) {
					getManagedBean().setSearchSuppType(
							paramSupplier.getSmAppl().getSupplierType());
					if (paramSupplier.getSmAppl().getSmCompanyBasic() != null
							&& !EntityHelper.isCollectionEmpty(paramSupplier
									.getSmAppl().getSmCompanyBasic()
									.getSmCompanyAddresses())) {
						SmAddress address = paramSupplier.getSmAppl()
								.getSmCompanyBasic().getSmCompanyAddresses()
								.get(0).getSmAddress();
						if (address != null && address.getPmState() != null) {
							PmZone zone = SmApplicationUtil
									.retrieveZoneByStateId(address.getPmState()
											.getStateId());
							if (zone != null) {
								getManagedBean().setSearchPmZoneId(
										zone.getZoneId());
							}
						}
					}
				}

				getManagedBean().searchSupplier();
			}
		}

		SmCommonUtil
				.redirectToPage(
						"/xhtml/sm/revisionSmApplView/viewHistory/searchHistoryPage.xhtml",
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

	/**
	 * Redirect to resubmit page
	 */
	public void redirectToResubmit() {

		// #14832 - To cater if resubmit link already expired and supplier still
		// in progress (Supplier Temp), so will call alert popup that will
		// redirect to clone new application and start fresh
		if (getManagedBean().getSmAppl().getSmSupplier().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_IN_PROGRESS) {
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				SmCommonUtil.redirectToExternalUrl(getManagedBean()
						.getResubmitUrl());
			} else {
				RequestContext.getCurrentInstance().execute(
						"confirmDialog.show()");
			}
		} else {
			if (SmApplicationUtil
					.isRejectedApplValidForResubmission(getManagedBean()
							.getSmAppl())) {
				SmCommonUtil.redirectToExternalUrl(getManagedBean()
						.getResubmitUrl());
			} else {
				throw new EpException(SmCommonUtil.getMessage(
						"lbl.sm.resubmitExpired", null));
			}
		}
	}

	/**
	 * Cloning new application and redirect to New MOF application page
	 */
	public void redirectToReapplyNewAppl() {
		SmApplicationBackingBean smApplBB = new SmApplicationBackingBean();
		// #24923
		smApplBB.getManagedBean().setNewSupplierType(
				getManagedBean().getSmAppl().getSupplierType());
		smApplBB.proceedToApplication(ParameterConst.NEW_SM_APPLN_FOR_MOF);

		// Enhancement #20467 - point to new appl page with bullet
		String page = my.ep.web.constant.sm.SupplierManagementConst.APPLICATION_BASE_PAGE_BY_TYPE
				.get("RE");
		SmCommonUtil.redirectToPage(page, null, null, null);
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

	private void initWorkflowStatus(SmAppl smAppl, String status) {
		List<String> rejectedStatus = Arrays
				.asList(SupplierManagementConst.REJECTED_STATUSES);
		SmWorkflowStatus sts = SmCommonUtil
				.getCurrentActiveWokFlowStatus(smAppl.getApplId());
		if (sts != null && sts.getPmStatus() != null) {
			smAppl.setWorkflowStatus(sts.getPmStatus());
			smAppl.setWorkflowStatusDesc(StringUtils.isBlank(status) ? getApplicationStatus(sts
					.getPmStatus()) : status);

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
		validateCompanySearch();
		getManagedBean().searchSupplier();
	}

	/**
	 * Initialized application by application no
	 * 
	 * @param String
	 *            applNo
	 */
	public void initSmApplByApplNo(Long applId, String status) {
		getManagedBean().setApplId(applId);
		initCompanyInfo(applId);
		getManagedBean().setResubmitUrl(StringUtils.EMPTY);
		SmAppl smAppl = getManagedBean().getSmAppl();
		if (smAppl != null
				&& smAppl.getApplType().equals(
						SupplierManagementConst.APPL_TYPE_BASIC)) {
			getRegistrationBackingBean().getManagedBean().setViewMode(true);
			getRegistrationBackingBean().viewOnlineAccount();
		} else {
			viewApplication(getManagedBean().getSmAppl());
			getManagedBean().setTabActiveIndex(0);
			initVirtualCert();
			initWorkflowStatus(getManagedBean().getSmAppl(), status);
			if (!getManagedBean().isSupplierUser()) {
				initPOApproverDecisionInfo();
			}
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
	 * Initialized PO or approver decision info
	 */
	private void initPOApproverDecisionInfo() {

		log.info("initPOApproverDecisionInfo");
		SmRemaksResDTO resDTO = SmServiceLookup.getSmRemarksSO()
				.retrievePOApproverFAandOLASmRemarks(
						getManagedBean().getSmAppl().getApplNo(),
						getManagedBean().getSmAppl().getApplId());
		resetPOApproverDecisionInfo();
		if (resDTO.getPoApproverRemarksList() != null) {
			log.info("PoApproverRemarksList() size:"
					+ resDTO.getPoApproverRemarksList().size());
			for (Object[] obj : resDTO.getPoApproverRemarksList()) {
				boolean isPoUser = false;

				String userName = (String) obj[0];
				Date remarkCreatedDate = (Date) obj[1];
				String roleType = (String) obj[2];
				String docType = (String) obj[3];
				String remark = (String) obj[4];

				log.info("userName:" + userName + ",remarkCreatedDate:"
						+ remarkCreatedDate + ",roleType:" + roleType
						+ ",docType:" + docType + ",remark:" + remark);

				if (roleType != null
						&& roleType
								.equals(my.ep.web.constant.sm.SupplierManagementConst.SECTION_REV_ROLE_PO)) {
					isPoUser = true;
				}

				if (isPoUser) {
					getManagedBean().setPoUserName(userName);
					setPORemarksInfo(docType, remark, remarkCreatedDate);
				} else {
					getManagedBean().setApproverUserName(userName);
					setApproverRemarksInfo(docType, remark, remarkCreatedDate);
				}
			}
		}
	}

	/**
	 * Reset PO or approver decision info to null
	 */
	private void resetPOApproverDecisionInfo() {
		getManagedBean().setPoApprOverlappedRemark(null);
		getManagedBean().setPoUserName(null);
		getManagedBean().setPoRemarkCreatedDate(null);
		getManagedBean().setPoRemark(null);
		getManagedBean().setApproverUserName(null);
		getManagedBean().setApproverRemarkCreatedDate(null);
		getManagedBean().setApproverRemark(null);
	}

	/**
	 * Set PO remarks info
	 * 
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setPORemarksInfo(String docType, String remark,
			Date remarkCreatedDate) {
		setPOApproverRemarksInfo(true, docType, remark, remarkCreatedDate);
	}

	/**
	 * Set approver remarks info
	 * 
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setApproverRemarksInfo(String docType, String remark,
			Date remarkCreatedDate) {
		setPOApproverRemarksInfo(false, docType, remark, remarkCreatedDate);
	}

	/**
	 * Set PO/approver remarks info
	 * 
	 * @param isPoUser
	 *            po user indicator
	 * @param docType
	 *            document type
	 * @param remark
	 *            remark
	 * @param remarkCreatedDate
	 *            remark created date
	 */
	private void setPOApproverRemarksInfo(boolean isPoUser, String docType,
			String remark, Date remarkCreatedDate) {
		if (docType != null) {
			if (docType.equals(ParameterConst.SM_APPLN_RMRK_DECISION_APPROVE)
					|| docType
							.equals(ParameterConst.SM_APPLN_RMRK_DECISION_REJECT)) {
				if (isPoUser) {
					getManagedBean().setPoRemark(remark);
					getManagedBean().setPoRemarkCreatedDate(remarkCreatedDate);
				} else {
					getManagedBean().setApproverRemark(remark);
					getManagedBean().setApproverRemarkCreatedDate(
							remarkCreatedDate);
				}
			} else {
				getManagedBean().setPoApprOverlappedRemark(remark);
			}
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

	/**
	 * Get SvoSiteVisitBackingBean instance
	 * 
	 * @return SvoSiteVisitBackingBean
	 */
	public static SvoSiteVisitBackingBean getSvoSiteVisitBackingBean() {
		if (svoSiteVisitBackingBean == null) {
			svoSiteVisitBackingBean = new SvoSiteVisitBackingBean();
		}
		return svoSiteVisitBackingBean;
	}
}
