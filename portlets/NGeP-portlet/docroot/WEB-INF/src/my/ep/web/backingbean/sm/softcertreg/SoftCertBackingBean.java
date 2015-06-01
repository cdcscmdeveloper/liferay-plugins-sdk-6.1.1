package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertDoc;
import my.ep.persistence.entity.sm.SmSoftcertDocFile;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SoftcertUploadSuppDocVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name = "softCertBackingBean")
@RequestScoped
public class SoftCertBackingBean extends BaseBackingBean<SoftCertManagedBean> {

	public static Logger log = Logger.getLogger(SoftCertBackingBean.class);
	private String SELECT_PERSONNELS_PAGE = "1";
	private String UPLOAD_DOC_PAGE = "2";
	private String PAYMENT_PAGE = "3";

	public SoftCertBackingBean() {
		SmCommonUtil.setMDCUserId();
	}

	/**
	 * Pre - render Request Softcert page
	 */
	public void preRenderSoftCert() {
		renewSesionObject();
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
		initAuthorizedPersonnels();
		getManagedBean().setGstRate(SmCommonUtil.getGstCurrentRate());
		getManagedBean().setTargetPage(SELECT_PERSONNELS_PAGE);
		SmCommonUtil.redirectToPage("softCertBasePage.xhtml", "");
	}

	/**
	 * Pre - render My Softcert Page
	 */
	public void preRenderMySoftCert() {
		renewSesionObject();
		FacesScopes facesScopes = new FacesScopes();
		PmUser usr = facesScopes.getUserSessionBean();
		getPersonnelByPmUser(usr);
		setPersonnelSoftcertInformation(getManagedBean()
				.getLoginUserPersonnelInfo());
		SmCommonUtil.redirectToPage("mySoftcertPage.xhtml", "");
	}

	/**
	 * Pre - render Softcert Payment from task list
	 */
	public void preRenderSoftCertPay() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();

		String token = new FacesScopes().getBPMAuthenticationCtxToken();

		reqDTO.setTaskId(taskId);
		reqDTO.setToken(token);

		BpmResponseDTO resDTO = (BpmResponseDTO) SmServiceLookup
				.getPersonnelSO().getBPMTaskDetails(reqDTO);
		this.getManagedBean().setTaskName(resDTO.getTaskName());
		if (resDTO.getTaskName().equalsIgnoreCase("Make Payment")) {
			Object bpmObj = new Object();
			PaymentBackingBean paymentBB = new PaymentBackingBean();
			bpmObj = (SMUserAddDataType) resDTO.getPojo();
			paymentBB.getManagedBean().setAppId(
					(long) ((SMUserAddDataType) bpmObj).getDocumentId());
			paymentBB.getManagedBean().setTaskId(taskId);
			paymentBB.getManagedBean().setBpmPojo(bpmObj);
			paymentBB.retrieveBillInfo();
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/payment/portletViewMode.xhtml", "");
		}
	}

	/**
	 * Return back to Request Softcert first page
	 * 
	 * @return String
	 */
	public void backToUploadPage() {
		getManagedBean().setTargetPage(UPLOAD_DOC_PAGE);
	}

	/**
	 * Return back to Softcert Main Page
	 */
	public void backToPersonnelListPage() {
		getManagedBean().setTargetPage(SELECT_PERSONNELS_PAGE);
	}

	/**
	 * Get personnel by login user
	 * 
	 * @param PmUser
	 *            usr
	 */
	public void getPersonnelByPmUser(PmUser usr) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(Long.valueOf(usr.getUserId()));
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByUserId(reqDTO);
		SmPersonnel smPersonnel = resDTO.getSmPersonnel();
		this.getManagedBean().setLoginUserPersonnelInfo(smPersonnel);
	}

	/**
	 * Navigate to Soft Certificate Upload Supporting Document page
	 */
	public void navigateToUploadSuppDocPage() {
		initRequestedSoftcertUserSuppDoc();
		getManagedBean().setTargetPage(UPLOAD_DOC_PAGE);
	}

	/**
	 * Navigate to Soft Certificate payment page
	 * 
	 * @return String
	 */
	public void navigateToPaymentPage() {
		validateUploadSuppDoc();
		this.getManagedBean().setSoftCertPayList(
				this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		setSoftCertPaymentBpmTask();
		getManagedBean().setTargetPage(PAYMENT_PAGE);
	}

	/**
	 * Set selected supporting document to upload
	 * 
	 * @param SmSoftcertDoc
	 *            doc
	 */
	public void assignSelectedSupportDoc(SmPersonnel smPersonnel,
			SmSoftcertDoc doc) {
		List<SmSoftcertDoc> softcertDocList = new ArrayList<SmSoftcertDoc>();
		softcertDocList.add(doc);
		SoftcertUploadSuppDocVO selectedSuppDoc = new SoftcertUploadSuppDocVO();
		selectedSuppDoc.setPersonnel(smPersonnel);
		selectedSuppDoc.setSuppDocList(softcertDocList);
		getManagedBean().setSelectedSuppDoc(selectedSuppDoc);
	}

	/**
	 * Handle file upload for softcert supporting document
	 * 
	 * @param FileUploadEvent
	 *            event
	 */
	public void handleSupDocFileAttachment(FileUploadEvent event) {
		if (event.getFile() != null) {
			if (getManagedBean().getSelectedSuppDoc() != null
					&& getManagedBean().getSelectedSuppDoc().getSuppDocList() != null
					&& !getManagedBean().getSelectedSuppDoc().getSuppDocList()
							.isEmpty()) {

				SoftcertUploadSuppDocVO selectedSuppDocVO = getManagedBean()
						.getSelectedSuppDoc();

				// construct file path
				String filePath = Calendar.getInstance().get(Calendar.YEAR)
						+ "/SM/SOFTCERT/DOC/"
						+ selectedSuppDocVO.getPersonnel().getPmUser()
								.getLoginId() + "/";

				// init suppDocFile (currently only default one file)
				SmSoftcertDocFile docFile = new SmSoftcertDocFile();
				docFile.setSmSoftcertDoc(selectedSuppDocVO.getSuppDocList()
						.get(0));
				docFile.setFileName(StringUtils.replace(event.getFile()
						.getFileName(), " ", "-"));
				docFile.setFilePath(filePath);
				selectedSuppDocVO
						.getSuppDocList()
						.get(0)
						.setSmSofcertDocFiles(
								new ArrayList<SmSoftcertDocFile>());
				selectedSuppDocVO.getSuppDocList().get(0)
						.getSmSofcertDocFiles().add(docFile);

				// set selected doc to main list
				setSelectedSuppDocToMainList(filePath);

				// upload file
				commonUploadFile(event, filePath);
			}
		}

	}

	/**
	 * Update total amount
	 */
	public void updateAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		int i = 0;
		for (SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()) {
			if (i == 0 && !getManagedBean().isAlreadyHadFreeSoftcert()) {
				this.getManagedBean().getPaymentMap()
						.put(personnel.getPersonnelId(), BigDecimal.ZERO);
				amount = amount.add(BigDecimal.ZERO);
				personnel.setFreeSoftCert(true);
				getManagedBean().setFreeSoftCertPersonnel(personnel);
			} else {
				this.getManagedBean()
						.getPaymentMap()
						.put(personnel.getPersonnelId(),
								BigDecimal.valueOf(150));
				amount = amount.add(BigDecimal.valueOf(150));
			}
			i++;
		}
		BigDecimal gst = amount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
				: getManagedBean().getGstRate().divide(new BigDecimal(100))
						.multiply(amount);
		getManagedBean()
				.setTotalGstAmount(gst.setScale(2, BigDecimal.ROUND_UP));
		this.getManagedBean().setTotalAmountBeforeGst(
				amount.setScale(2, BigDecimal.ROUND_UP));
		this.getManagedBean().setTotalPaymentAmount(
				gst.add(amount).setScale(2, BigDecimal.ROUND_UP));
		setPaymentBtnLbl();
	}

	/**
	 * Create bill, update personnel and call soft certificate payment BPM
	 */
	public void makePayment() {
		if ((getManagedBean().getSoftCertPayList() != null && getManagedBean()
				.getSoftCertPayList().size() > 0)
				|| getManagedBean().getFreeSoftCertPersonnel() != null) {
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			List<String> userList = new ArrayList<String>();
			userList.add(pmUser.getLoginId());

			PersonnelReqDTO reqDTO = new PersonnelReqDTO();

			// if already got soft cert request, include in new bill.
			for (SmPersonnel smPersonnel : getManagedBean()
					.getLoginUserPersonnelInfo().getSmAppl().getSmPersonnels()) {
				if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ) {
					getManagedBean().getSoftCertPayList().add(smPersonnel);
				}
			}
			reqDTO.setSmPersonnelList(getFinalPaidPersonnels());
			reqDTO.setSmPersonnel(getManagedBean().getFreeSoftCertPersonnel());
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			reqDTO.setToken(getManagedBean().getToken());
			reqDTO.setUserList(userList);
			reqDTO.setUserId(pmUser.getUserId());
			reqDTO.setSoftCertPaymentExistInTaskList(getManagedBean()
					.getSoftCertBpmTask() == null ? false : true);
			reqDTO.setSoftCertPaymentDocId(getManagedBean()
					.getSoftCertBpmTask() == null ? null : getManagedBean()
					.getSoftCertBpmTask().getDocId());
			reqDTO.setSoftCertPaymentTaskId(getManagedBean()
					.getSoftCertBpmTask() == null ? null : getManagedBean()
					.getSoftCertBpmTask().getTaskId());
			reqDTO.setSoftcertDocList(getUploadedSoftcertDocs());

			PersonnelResDTO resDTO = new PersonnelResDTO();
			resDTO = SmServiceLookup.getPersonnelSO()
					.updatePersonnelAndRequestSoftCert(reqDTO);

			if (resDTO.isSuccessful()) {
				String msgKey = MessageConst.INFO_721060;
				if (getManagedBean().getTotalPaymentAmount().compareTo(
						BigDecimal.ZERO) == 0) {
					msgKey = MessageConst.INFO_721061;
				}
				renewSesionObject();
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(), msgKey);
			}
		}
	}

	/**
	 * Remove check box tick from data table
	 * 
	 * @param AjaxBehaviorEvent
	 *            e
	 */
	public void unSelectPersonnel(AjaxBehaviorEvent e) {
		if (getManagedBean().getSoftCertSelectList() != null) {
			Iterator<SmPersonnel> iterator = getManagedBean()
					.getSoftCertSelectList().iterator();
			while (iterator.hasNext()) {
				SmPersonnel personnel = iterator.next();
				if (personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
					iterator.remove();
				} else {
					if (personnel.getPmUser() == null) {
						iterator.remove();
					}
				}
			}
		}
	}

	/**
	 * Get personnel soft certificate price (free for first request)
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return String
	 */
	public String getPersonnelSoftCertPrice(SmPersonnel smPersonnel) {
		if (smPersonnel.isFreeSoftCert()) {
			return "0.00 ("
					+ SmCommonUtil
							.getMessage("lbl.sm.softcert.firstfree", null)
					+ ")";
		} else {
			return "150.00";
		}
	}

	/**
	 * Update soft certificate challenge response
	 */
	public void updateChallengeResponse() {
		try {
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestChallRespUpdate();
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.ERROR_721037);
		}
	}

	/**
	 * Reset soft certificate PIN number
	 */
	public void resetPin() {
		try {
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestResetPIN();
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.ERROR_721037);
		}
	}

	/**
	 * Change soft certificate PIN number
	 */
	public void changePin() {
		try {
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestChangePIN();
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.ERROR_721037);
		}
	}

	/**
	 * Revoke soft certificate
	 */
	public void revoke(SmPersonnel smPersonnel) {
		try {
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestRevokeCert(smPersonnel.getIdentificationNo(),
							smPersonnel.getSmAppl().getSmSupplier().getEpNo());
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.ERROR_721037);
		}
	}

	/**
	 * Call confirmation dialog
	 */
	public void showConfirmDialog() {
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
	}

	/**
	 * Set payment button label
	 */
	private void setPaymentBtnLbl() {
		if (getManagedBean().getTotalPaymentAmount().compareTo(BigDecimal.ZERO) == 0) {
			getManagedBean().setPaymentBtnLbl(
					SmCommonUtil.getMessage("lbl.sm.request.softcert", null));
		} else {
			getManagedBean().setPaymentBtnLbl(
					SmCommonUtil.getMessage("lbl.sm.makePayment", null));
		}
	}

	/**
	 * Get if already got soft certificate payment in task list for the login
	 * user
	 */
	private void setSoftCertPaymentBpmTask() {
		getManagedBean().setSoftCertBpmTask(null);
		if (getManagedBean().getToken() == null
				|| StringUtils.isBlank(getManagedBean().getToken())) {
			getManagedBean().setToken(
					new FacesScopes().getBPMAuthenticationCtxToken());
		}
		List<WorklistItemVO> taskList = SmCommonUtil
				.getBpmTaskListByAuthToken(getManagedBean().getToken());
		if (taskList != null && !taskList.isEmpty()) {
			for (WorklistItemVO worklistItemVO : taskList) {
				if (worklistItemVO.getTitle().equalsIgnoreCase(
						"lbl.sm.addAdditional.makePayment")) {
					getManagedBean().setSoftCertBpmTask(worklistItemVO);
					break;
				}
			}
		}
	}

	/**
	 * Initialized authorized personnel list
	 */
	private void initAuthorizedPersonnels() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setApplId(getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getApplId());
		getManagedBean().setAuthorizedPersonnels(new ArrayList<SmPersonnel>());
		List<SmPersonnel> personnelList = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByApplIdForSoftCert(reqDTO)
				.getSmPersonnelList();
		if (personnelList != null && !personnelList.isEmpty()) {
			setPersonnelRowSelection(personnelList);
			getManagedBean().setAuthorizedPersonnels(personnelList);
		}
	}

	/**
	 * Set list of personnel row whether need to be disabled from check box tick
	 * and also to determine whether supplier already given free soft
	 * certificate
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setPersonnelRowSelection(List<SmPersonnel> personnels) {
		getManagedBean().setAlreadyHadFreeSoftcert(false);
		for (SmPersonnel personnel : personnels) {
			if (personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
				personnel.setDisableRowSelection(true);
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
					getManagedBean().setAlreadyHadFreeSoftcert(true);
				}
			} else {
				if (StringUtils.isBlank(personnel.getSmAppl().getApplNo())) {
					if (personnel.getPmUser() == null) {
						personnel.setDisableRowSelection(true);
					}
				}
			}
			setPersonnelSoftcertInformation(personnel);
		}
	}

	/**
	 * Get list of personnel that selected to request soft certificate
	 * 
	 * @return List<SmPersonnel>
	 */
	private List<SmPersonnel> getFinalPaidPersonnels() {
		List<SmPersonnel> finalPayPersonnels = new ArrayList<SmPersonnel>();
		if (getManagedBean().getFreeSoftCertPersonnel() != null) {
			for (SmPersonnel payPersonnel : getManagedBean()
					.getSoftCertPayList()) {
				if (!payPersonnel.isFreeSoftCert()) {
					finalPayPersonnels.add(payPersonnel);
				}
			}
		} else {
			finalPayPersonnels.addAll(getManagedBean().getSoftCertPayList());
		}
		return finalPayPersonnels;
	}

	/**
	 * Get list of uploaded supporting document
	 * 
	 * @return List<SmSoftcertDoc>
	 */
	private List<SmSoftcertDoc> getUploadedSoftcertDocs() {
		List<SmSoftcertDoc> softcertDocList = new ArrayList<SmSoftcertDoc>();
		if (getManagedBean().getSoftcertUploadSuppDocList() != null
				&& !getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			for (SoftcertUploadSuppDocVO docVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				for (SmSoftcertDoc doc : docVO.getSuppDocList()) {
					softcertDocList.add(doc);
				}
			}
		}
		return softcertDocList;
	}

	/**
	 * Set personnel soft certificate validity date and soft certificate number
	 * inside data table for request soft certificate screen
	 * 
	 * @param smPersonnel
	 */
	private void setPersonnelSoftcertInformation(SmPersonnel smPersonnel) {
		String softcertValidityDate = SmCommonUtil.getMessage(
				"lbl.sm.no.available", null);
		String sofcertNo = SmCommonUtil.getMessage("lbl.sm.no.available", null);

		if (smPersonnel.getPmUser() != null
				&& smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(getManagedBean().getLoginUserPersonnelInfo()
					.getSmAppl().getSmSupplier().getSupplierId());
			reqDTO.setUserId(smPersonnel.getPmUser().getUserId());
			SmSoftcertRequest softcertRequest = SmServiceLookup
					.getPersonnelSO()
					.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
							reqDTO).getSoftcertRequest();

			if (softcertRequest != null
					&& softcertRequest.getPmDigiCert() != null) {
				if (StringUtils.isNotBlank(softcertRequest.getPmDigiCert()
						.getCertSerialNo())) {
					sofcertNo = softcertRequest.getPmDigiCert()
							.getCertSerialNo();
				}
				if (softcertRequest.getPmDigiCert().getValidFrom() != null) {
					softcertValidityDate = DateUtils.dateToString("dd/MM/yyyy",
							softcertRequest.getPmDigiCert().getValidFrom());
				}
				if (softcertRequest.getPmDigiCert().getValidTo() != null) {
					softcertValidityDate = softcertValidityDate
							+ " - "
							+ DateUtils.dateToString("dd/MM/yyyy",
									softcertRequest.getPmDigiCert()
											.getValidTo());
				}
			}
		}
		smPersonnel.setSoftcertValidityDate(softcertValidityDate);
		smPersonnel.setSoftcertNo(sofcertNo);
	}

	/**
	 * Construct VO to display list of selected personnel and document that need
	 * to be uploaded
	 */
	private void initRequestedSoftcertUserSuppDoc() {
		List<PmDocument> softcertSuppDocList = SmServiceLookup.getDocumentSO()
				.retrieveSoftcertSuppDocuments().getDocumentList();
		setSoftcertUserSuppDoc();
		if (getManagedBean().getSoftcertUploadSuppDocList() != null
				&& !getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			SmSoftcertDoc softcertDoc = null;
			for (SoftcertUploadSuppDocVO softcertUploadSuppDocVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				if (softcertSuppDocList != null
						&& !softcertSuppDocList.isEmpty()
						&& (softcertUploadSuppDocVO.getSuppDocList() == null || softcertUploadSuppDocVO
								.getSuppDocList().isEmpty())) {
					softcertUploadSuppDocVO
							.setSuppDocList(new ArrayList<SmSoftcertDoc>());
					for (PmDocument softcertSuppDoc : softcertSuppDocList) {
						softcertDoc = new SmSoftcertDoc();
						softcertDoc.setPmDocument(softcertSuppDoc);
						softcertDoc.setPmUser(softcertUploadSuppDocVO
								.getPersonnel().getPmUser());
						softcertDoc
								.setSmSofcertDocFiles(new ArrayList<SmSoftcertDocFile>());
						softcertDoc
								.setRecordStatusFlg(DbValueConst.RECORD_STATUS_ACTIVE);
						softcertUploadSuppDocVO.getSuppDocList().add(
								softcertDoc);
					}
				}
			}
		}
	}

	/**
	 * Init VO list
	 */
	private void setSoftcertUserSuppDoc() {
		deleteUploadSoftcertDocSession();
		if (getManagedBean().getSoftcertUploadSuppDocList() == null
				|| getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			getManagedBean().setSoftcertUploadSuppDocList(
					new ArrayList<SoftcertUploadSuppDocVO>());
		}

		if (getManagedBean().getSoftCertSelectList() != null
				&& !getManagedBean().getSoftCertSelectList().isEmpty()) {
			SoftcertUploadSuppDocVO suppDocVO = null;
			for (SmPersonnel requestedPersonnel : getManagedBean()
					.getSoftCertSelectList()) {
				suppDocVO = getSoftcertUploadDocVO(requestedPersonnel);
				if (suppDocVO == null) {
					suppDocVO = new SoftcertUploadSuppDocVO();
					suppDocVO.setPersonnel(requestedPersonnel);
					getManagedBean().getSoftcertUploadSuppDocList().add(
							suppDocVO);
				}
			}
		}
	}

	/**
	 * Set selected doc to be uploaded to main list
	 * 
	 * @param String
	 *            filePath
	 */
	private void setSelectedSuppDocToMainList(String filePath) {
		Iterator<SoftcertUploadSuppDocVO> docVOIter = getManagedBean()
				.getSoftcertUploadSuppDocList().iterator();
		while (docVOIter.hasNext()) {
			SoftcertUploadSuppDocVO docVO = docVOIter.next();
			if (docVO
					.getPersonnel()
					.getIdentificationNo()
					.equals(getManagedBean().getSelectedSuppDoc()
							.getPersonnel().getIdentificationNo())) {

				docVO.getSuppDocList().set(
						docVO.getSuppDocList().indexOf(
								getManagedBean().getSelectedSuppDoc()
										.getSuppDocList().get(0)),
						getManagedBean().getSelectedSuppDoc().getSuppDocList()
								.get(0));

			}
		}
	}

	/**
	 * Validate uploaded supporting document
	 */
	private void validateUploadSuppDoc() {
		StringBuffer sb = new StringBuffer();
		if (getManagedBean().getSoftcertUploadSuppDocList() != null
				&& !getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			for (SoftcertUploadSuppDocVO docVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				if (docVO.getSuppDocList() != null
						&& !docVO.getSuppDocList().isEmpty()) {
					for (SmSoftcertDoc doc : docVO.getSuppDocList()) {
						if (doc.getSmSofcertDocFiles() == null
								|| doc.getSmSofcertDocFiles().isEmpty()) {
							sb.append(
									getSuppDocRequiredMsg(docVO.getPersonnel(),
											doc.getPmDocument())).append(
									"<br />");
						} else {
							if (StringUtils.isBlank(doc.getSmSofcertDocFiles()
									.get(0).getFileName())
									|| StringUtils.isBlank(doc
											.getSmSofcertDocFiles().get(0)
											.getFilePath())) {
								sb.append(
										getSuppDocRequiredMsg(
												docVO.getPersonnel(),
												doc.getPmDocument())).append(
										"<br />");
							}
						}
					}
				}
			}
		}

		if (sb.length() > 0) {
			throw new EpException(sb.toString());
		}
	}

	/**
	 * Get supporting documents required msg by personnel and document
	 * 
	 * @param String
	 *            addrTypeKey
	 * @param String
	 *            addrFieldKey
	 * @return String
	 */
	private String getSuppDocRequiredMsg(SmPersonnel smPersonnel,
			PmDocument document) {
		return SmCommonUtil.getMessage(
				"lbl.sm.softcert.suppdoc.upload.missing.errmsg", new String[] {
						document.getDocName(), smPersonnel.getName() });
	}

	/**
	 * Remove non selected personnel in SoftcertUploadSuppDocVO session
	 */
	private void deleteUploadSoftcertDocSession() {
		if (getManagedBean().getSoftcertUploadSuppDocList() != null
				&& !getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			for (Iterator<SoftcertUploadSuppDocVO> it = getManagedBean()
					.getSoftcertUploadSuppDocList().iterator(); it.hasNext();) {
				SoftcertUploadSuppDocVO docVO = it.next();
				if (!isExistInSelectedPersonnel(docVO.getPersonnel())) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Check if personnel exist in selected personnel list
	 * 
	 * @param SmPersonnel
	 *            personnel
	 * @return boolean
	 */
	private boolean isExistInSelectedPersonnel(SmPersonnel personnel) {
		boolean exist = false;
		for (SmPersonnel selectedPersonnel : getManagedBean()
				.getSoftCertSelectList()) {
			if (personnel.getIdentificationNo().equals(
					selectedPersonnel.getIdentificationNo())) {
				exist = true;
				break;
			}
		}
		return exist;
	}

	/**
	 * Get SoftcertUploadSuppDocVO by SmPersonnel
	 * 
	 * @param SmPersonnel
	 *            smPersonnel
	 * @return SoftcertUploadSuppDocVO
	 */
	private SoftcertUploadSuppDocVO getSoftcertUploadDocVO(
			SmPersonnel smPersonnel) {
		SoftcertUploadSuppDocVO retVal = null;
		if (getManagedBean().getSoftcertUploadSuppDocList() != null
				&& !getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
			for (SoftcertUploadSuppDocVO docVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				if (docVO.getPersonnel().getIdentificationNo()
						.equals(smPersonnel.getIdentificationNo())) {
					retVal = docVO;
					break;
				}
			}
		}
		return retVal;
	}

}
