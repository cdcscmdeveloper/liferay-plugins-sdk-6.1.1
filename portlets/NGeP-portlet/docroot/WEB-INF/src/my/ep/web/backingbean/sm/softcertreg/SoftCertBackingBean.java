package my.ep.web.backingbean.sm.softcertreg;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.jaxb.sm.useradddata.SMUserAddDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSoftcertDoc;
import my.ep.persistence.entity.sm.SmSoftcertDocFile;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SoftcertUploadSuppDocVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
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
		initPersonnels();
		getManagedBean().setGstRate(SmCommonUtil.getGstCurrentRate());
		getManagedBean().setTargetPage(SELECT_PERSONNELS_PAGE);
		getManagedBean().setSuppDocMode(
				SupplierManagementConst.SUPPORTING_DOC_ONLINE);
		getManagedBean().setSoftcertDocNo(
				SmCommonUtil.getSoftcertDocNo(getManagedBean()
						.getLoginUserPersonnelInfo().getSmAppl()));
		SmCommonUtil.redirectToPage("softCertBasePage.xhtml", "", null);
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
		SmCommonUtil.redirectToPage("mySoftcertPage.xhtml", "", null);
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

		Object token = new FacesScopes().getBPMAuthenticationCtxToken();

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
			paymentBB.getManagedBean().setDocNo(resDTO.getDocNumber());
			paymentBB.retrieveBillInfo();
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/payment/portletViewMode.xhtml", "", null);
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
		SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(supplier.getSupplierId());
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelBySupplierId(reqDTO);
		if (!SmCommonUtil.isCollectionEmpty(resDTO.getSmPersonnelList())) {
			for (SmPersonnel personnel : resDTO.getSmPersonnelList()) {
				if (personnel.getPmUser() != null
						&& personnel.getPmUser().getUserId() != null
						&& usr.getUserId().compareTo(
								personnel.getPmUser().getUserId()) == 0) {
					this.getManagedBean().setLoginUserPersonnelInfo(personnel);
					break;
				}
			}
		}
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
		if (getManagedBean().getSuppDocMode().equals(
				SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			validateUploadSuppDoc();
		}
		this.getManagedBean().setSoftCertPayList(
				this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		setSoftCertPaymentBpmTask();
		setSoftcertApplyFor();
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
						+ "/SM/"
						+ "SOFTCERT/"
						+ selectedSuppDocVO.getPersonnel().getPmUser()
								.getLoginId() + "/";

				// init suppDocFile (currently only default one file)
				SmSoftcertDocFile docFile = new SmSoftcertDocFile();
				docFile.setSmSoftcertDoc(selectedSuppDocVO.getSuppDocList()
						.get(0));
				docFile.setFileName(System.currentTimeMillis() + "_");
				docFile.setFileName(docFile.getFileName()
						+ StringUtils.replace(event.getFile().getFileName(),
								" ", "_"));
				docFile.setFilePath(filePath);
				selectedSuppDocVO
						.getSuppDocList()
						.get(0)
						.setSmSofcertDocFiles(
								new ArrayList<SmSoftcertDocFile>());
				selectedSuppDocVO.getSuppDocList().get(0)
						.getSmSofcertDocFiles().add(docFile);

				// set selected doc to main list
				setSelectedSuppDocToMainList();

				// upload file
				SmCommonUtil.processUpload(filePath, event,
						docFile.getFileName());
			}
		}

	}

	/**
	 * Update total amount
	 */
	public void updateAmount() {
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal amountPerPersonnel = BigDecimal.ZERO;
		int i = 0;
		for (SmPersonnel personnel : this.getManagedBean().getSoftCertPayList()) {
			if (i == 0 && !getManagedBean().isAlreadyHadFreeSoftcert()) {
				personnel.setFreeSoftCert(true);
				if (StringUtils.isNotBlank(personnel.getEpRole())
						&& personnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value())) {
					getManagedBean().setSupplierTmpPersonnel(personnel);
				}
			} else {
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
					personnel.setFreeSoftCert(true);
				} else if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
					personnel.setFreeSoftCert(true);
				} else {
					amountPerPersonnel = SmRuleHelper.getInstance()
							.getSoftCertFee();
				}
				this.getManagedBean().getPaymentMap()
						.put(personnel.getPersonnelId(), amountPerPersonnel);
				amount = amount.add(amountPerPersonnel);
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
				.getSoftCertPayList().size() > 0)) {
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
			reqDTO.setSmPersonnel(getManagedBean().getSupplierTmpPersonnel());
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
			reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());
			reqDTO.setSoftcertDocNo(getManagedBean().getSoftcertDocNo());
			reqDTO.setFreeSofcertPersonnelList(getFinalFreePersonnels());
			reqDTO.setDigiSign(getManagedBean().getDigiSign());

			PersonnelResDTO resDTO = new PersonnelResDTO();
			resDTO = SmServiceLookup.getPersonnelSO()
					.updatePersonnelAndRequestSoftCert(reqDTO);

			if (resDTO.isSuccessful()) {
				String msgKey = MessageConst.INFO_721060;
				String[] params = null;
				if (getManagedBean().getTotalPaymentAmount().compareTo(
						BigDecimal.ZERO) == 0) {
					msgKey = MessageConst.INFO_721061;
					StringBuilder sbName = null;
					StringBuilder sbEmail = null;
					String name = StringUtils.EMPTY;
					String email = StringUtils.EMPTY;

					if (getManagedBean().getSupplierTmpPersonnel() != null) {
						name = getManagedBean().getSupplierTmpPersonnel()
								.getName();
						email = getManagedBean().getSupplierTmpPersonnel()
								.getEmail();
					} else if (!EntityHelper.isCollectionEmpty(reqDTO
							.getFreeSofcertPersonnelList())) {
						sbName = new StringBuilder();
						sbEmail = new StringBuilder();
						for (SmPersonnel per : reqDTO
								.getFreeSofcertPersonnelList()) {
							sbName.append(per.getName()).append(",");
							sbEmail.append(per.getEmail()).append(",");
						}
						name = sbName.toString();
						if (name.endsWith(",")) {
							name = name.substring(0, name.length() - 1);
						}
						email = sbEmail.toString();
						if (email.endsWith(",")) {
							email = email.substring(0, email.length() - 1);
						}

					}
					params = new String[] { name, email };
				}
				renewSesionObject();
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(), msgKey,
						params);
			} else {
				throw new EpException(MessageConst.ERROR_721037);
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
		getManagedBean().setSoftCertSelectList(new ArrayList<SmPersonnel>());
		if (getManagedBean().getSoftCertSelect() != null) {
			getManagedBean().getSoftCertSelectList().add(
					getManagedBean().getSoftCertSelect());
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
			if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
				return "0.00 ("
						+ SmCommonUtil.getMessage("lbl.sm.softcert.rejectfree",
								null) + ")";
			} else if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
				return "0.00 ("
						+ SmCommonUtil.getMessage("lbl.sm.softcert.failedfree",
								null) + ")";
			} else {
				return "0.00 ("
						+ SmCommonUtil.getMessage("lbl.sm.softcert.firstfree",
								null) + ")";
			}
		} else {
			return SmRuleHelper.getInstance().getSoftCertFee()
					.setScale(2, BigDecimal.ROUND_UP).toString();
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
			throwKmcExceptionInNewPage(ex.getMessage());
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
			throwKmcExceptionInNewPage(ex.getMessage());
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
			throwKmcExceptionInNewPage(ex.getMessage());
		}
	}

	/**
	 * Revoke soft certificate
	 */
	public void revoke() {
		try {
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestRevokeCert(
							getManagedBean().getLoginUserPersonnelInfo()
									.getIdentificationNo(),
							getManagedBean().getLoginUserPersonnelInfo()
									.getSmAppl().getSmSupplier().getEpNo());
		} catch (Exception ex) {
			throwKmcExceptionInNewPage(ex.getMessage());
		}
	}

	/**
	 * If supplier admin apply for others, show SPKI and if apply for himself
	 * just directly show declaration popup
	 */
	public void showConfirmDialog() {
		validateRequestSoftcert();
		if (getManagedBean().isApplySoftcertForOthers()) {
			onDisplaySpkiPopup();
		} else {
			RequestContext.getCurrentInstance().execute("confirmDialog.show()");
		}
	}

	/**
	 * Call SPKI Popup dialog
	 */
	public void onDisplaySpkiPopup() {
		getManagedBean().getSpkiBackingBean().getChallengeQuestions();
//		RequestContext.getCurrentInstance().execute("spki_dlg.show()");
	}

	/**
	 * On click SPKI popup dialog
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 */
	public void onClickSignSpki(PmDigiSign pds) {
		PmDigiSign pmDigiSign = new PmDigiSign();
		setSpkiInformation(pmDigiSign, StringUtils.isNotBlank(getManagedBean()
				.getLoginUserPersonnelInfo().getSmAppl().getApplNo()) ? "SR"
				: "SC", getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getApplId(), getManagedBean().getSoftcertDocNo(),
				prepareOriginalText());
		SpkiBackingBean spkiBackingBean = this.getManagedBean()
				.getSpkiBackingBean();
		byte[] signedData = spkiBackingBean
				.spkiSignTransaction(prepareOriginalText());
		pmDigiSign.setSpkiSignedData(signedData);
		getManagedBean().setDigiSign(pmDigiSign);
		RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * Set SPKI information
	 * 
	 * @param PmDigiSign
	 *            pmDigiSign
	 * @param String
	 *            docType
	 * @param Long
	 *            docId
	 * @param String
	 *            docNumber
	 * @param String
	 *            textBeforeSign
	 */
	private void setSpkiInformation(PmDigiSign pmDigiSign, String docType,
			Long docId, String docNumber, String textBeforeSign) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(userId);
		pmDigiSign.setCreatedDate(date);
		pmDigiSign.setOriginalData(textBeforeSign);
	}

	/**
	 * text to be signed for SM: applNo + userId (IC or password) + epNo
	 * 
	 * @return
	 */
	private String prepareOriginalText() {
		String softcertDocNo = this.getManagedBean().getSoftcertDocNo();
		String userIdentificationNo = new FacesScopes().getUserSessionBean()
				.getIdentificationNo();
		String epNo = this.getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getSmSupplier().getEpNo();
		String originalText = softcertDocNo + "|" + userIdentificationNo + "|"
				+ epNo;
		return originalText;
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
		if (getManagedBean().getToken() == null) {
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
	private void initPersonnels() {
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
	 * Set list of personnel row whether need to be disabled from selected and
	 * also to determine whether supplier already given free soft certificate
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setPersonnelRowSelection(List<SmPersonnel> personnels) {
		Long supplierId = getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getSmSupplier().getSupplierId();
		getManagedBean().setAllAuthSuppUserAlreadyRequested(true);
		getManagedBean().setAlreadyHadFreeSoftcert(false);
		getManagedBean().setSoftCertSelectList(new ArrayList<SmPersonnel>());
		for (SmPersonnel personnel : personnels) {
			// check for supplier tmp
			if (StringUtils.isNotBlank(personnel.getEpRole())
					&& personnel.getEpRole().equals(
							RoleCode.SUPPLIER_TEMP.value())
					&& personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ) {
				getManagedBean().setSupplierTmpPersonnel(personnel);
				getManagedBean().setSoftCertSelect(personnel);
				getManagedBean().getSoftCertSelectList().add(
						getManagedBean().getSoftCertSelect());

			}
			// check for softcert flg not entitled to request
			else if (personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_PENDING_REQ
					&& personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_REJECTED
					&& personnel.getSoftcertFlg() != SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
				personnel.setDisableRowSelection(true);
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
					getManagedBean().setAlreadyHadFreeSoftcert(true);
				}
			}
			// check for softcert flg that pending request or rejected or failed
			else {

				// set rejected or failed softcert request reason
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED
						|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
					personnel.setDisableRowSelection(false);
					getManagedBean().setAlreadyHadFreeSoftcert(true);
					SmCommonUtil.setRejectedPersonnelSoftcert(personnel,
							supplierId);

					// set supplier tmp if rejected or failed
					if (!StringUtils.isBlank(personnel.getEpRole())
							&& personnel.getEpRole().equals(
									RoleCode.SUPPLIER_TEMP.value())) {
						getManagedBean().setSupplierTmpPersonnel(personnel);
					}
				}

				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PENDING_REQ) {

					// check if pending activation or deactivated personnel set
					// not entitled to request
					if (personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_ACTIVATION
							|| (StringUtils.isBlank(personnel.getEpRole()) && personnel
									.getPmUser() != null)) {
						personnel.setDisableRowSelection(true);
					}

					if (!personnel.getEpRole().equals(RoleCode.FL_USER.value())
							&& personnel.getAuthorizedFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().setAllAuthSuppUserAlreadyRequested(
								false);
					}
				}
			}
			setPersonnelSoftcertInformation(personnel);
		}
		setDisableFlUserRowSelection(personnels);
		setDisableRowSelectionIfSuppTmpNotRequestSoftCert(personnels);
		initDisableRevokeButton();
	}

	/**
	 * Disable FL_USER to request softcert if still got authorized
	 * MOF_SUPPLIER_USER that haven't request
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setDisableFlUserRowSelection(List<SmPersonnel> personnels) {
		if (!getManagedBean().isAllAuthSuppUserAlreadyRequested()) {
			for (SmPersonnel personnel : personnels) {
				if (!StringUtils.isBlank(personnel.getEpRole())
						&& personnel.getEpRole().equals(
								RoleCode.FL_USER.value())) {
					personnel.setDisableRowSelection(true);
				}
			}
		}
	}

	/**
	 * Disable row select for all personnel except for SUPPLIER_TEMP if
	 * SUPPLIER_TEMP haven't request for softcert
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setDisableRowSelectionIfSuppTmpNotRequestSoftCert(
			List<SmPersonnel> personnels) {
		if (getManagedBean().getSupplierTmpPersonnel() != null
				&& getManagedBean().getSupplierTmpPersonnel().getPersonnelId() != null) {
			for (SmPersonnel per : personnels) {
				if (per.getPersonnelId().compareTo(
						getManagedBean().getSupplierTmpPersonnel()
								.getPersonnelId()) != 0) {
					per.setDisableRowSelection(true);
				}
			}
		}
	}

	/**
	 * Get list of personnel that selected to request for pay soft certificate
	 * 
	 * @return List<SmPersonnel>
	 */
	private List<SmPersonnel> getFinalPaidPersonnels() {
		List<SmPersonnel> finalPayPersonnels = new ArrayList<SmPersonnel>();
		if (getManagedBean().getSoftCertPayList() != null) {
			for (SmPersonnel payPersonnel : getManagedBean()
					.getSoftCertPayList()) {
				if (!payPersonnel.isFreeSoftCert()) {
					finalPayPersonnels.add(payPersonnel);
				}
			}
		}
		return finalPayPersonnels;
	}

	/**
	 * Get list of personnel that selected to request for free certificate
	 * 
	 * @return List<SmPersonnel>
	 */
	private List<SmPersonnel> getFinalFreePersonnels() {
		List<SmPersonnel> finalFreePersonnels = new ArrayList<SmPersonnel>();
		if (getManagedBean().getSoftCertPayList() != null) {
			for (SmPersonnel payPersonnel : getManagedBean()
					.getSoftCertPayList()) {
				// add free softcert personnel (exclude SUPPLIER_TEMP)
				if (payPersonnel.isFreeSoftCert()
						&& StringUtils.isNotBlank(payPersonnel.getEpRole())
						&& !payPersonnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value())) {
					finalFreePersonnels.add(payPersonnel);
				}
			}
		}
		return finalFreePersonnels;
	}

	/**
	 * Get list of uploaded supporting document
	 * 
	 * @return List<SmSoftcertDoc>
	 */
	private List<SmSoftcertDoc> getUploadedSoftcertDocs() {
		List<SmSoftcertDoc> softcertDocList = new ArrayList<SmSoftcertDoc>();
		if (getManagedBean().getSuppDocMode().equals(
				SupplierManagementConst.SUPPORTING_DOC_ONLINE)
				&& getManagedBean().getSoftcertUploadSuppDocList() != null
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
			getManagedBean().setRenderRevokeButton(true);
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
					Calendar renewDate = Calendar.getInstance();
					renewDate.setTime(softcertRequest.getPmDigiCert()
							.getValidTo());
					renewDate.add(Calendar.MONTH, -3);
					if (DateUtils.isDateGreaterOrEqual(new Date(),
							renewDate.getTime())) {
						smPersonnel
								.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
						smPersonnel.setDisableRowSelection(false);
					}
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
		SmAppl appl = getManagedBean().getLoginUserPersonnelInfo() != null ? getManagedBean()
				.getLoginUserPersonnelInfo().getSmAppl() : null;
		boolean basic = (appl != null
				&& !StringUtils.isBlank(appl.getApplType()) && appl
				.getApplType().equals(SupplierManagementConst.ACC_BASIC)) ? true
				: false;
		String businessType = (appl != null && appl.getSmSupplier() != null) ? appl
				.getSmSupplier().getBusinessType() : StringUtils.EMPTY;
		List<PmDocument> softcertSuppDocList = new ArrayList<PmDocument>();
		DocumentReqDTO reqDTO = null;
		setSoftcertUserSuppDoc();
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSoftcertUploadSuppDocList())) {
			SmSoftcertDoc softcertDoc = null;
			for (SoftcertUploadSuppDocVO softcertUploadSuppDocVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				if (EntityHelper.isCollectionEmpty(softcertUploadSuppDocVO
						.getSuppDocList())) {
					softcertUploadSuppDocVO
							.setSuppDocList(new ArrayList<SmSoftcertDoc>());
					reqDTO = new DocumentReqDTO();
					reqDTO.setBasicAccount(basic);
					reqDTO.setCompanyTypeCode(businessType);
					reqDTO.setOwner(softcertUploadSuppDocVO.getPersonnel()
							.isEquityOwner());
					reqDTO.setFirstUser(softcertUploadSuppDocVO.getPersonnel()
							.getEpRole().equals(RoleCode.SUPPLIER_TEMP.value()) ? true
							: false);
					reqDTO.setForeigner(softcertUploadSuppDocVO
							.getPersonnel()
							.getPmCountry()
							.getCountryCode()
							.equals(SupplierManagementConst.COUNTRY_MALAYSIA_CODE) ? false
							: true);
					softcertSuppDocList = SmServiceLookup.getDocumentSO()
							.retrieveSoftcertSuppDocuments(reqDTO)
							.getDocumentList();
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
		if (softcertSuppDocList == null || softcertSuppDocList.isEmpty()) {
			getManagedBean().setSuppDocMode("H");
		}
		getManagedBean().setBasicAppl(basic);
	}

	/**
	 * Init VO list
	 */
	private void setSoftcertUserSuppDoc() {
		deleteUploadSoftcertDocSession();
		if (EntityHelper.isCollectionEmpty(getManagedBean()
				.getSoftcertUploadSuppDocList())) {
			getManagedBean().setSoftcertUploadSuppDocList(
					new ArrayList<SoftcertUploadSuppDocVO>());
		}

		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSoftCertSelectList())) {
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
	private void setSelectedSuppDocToMainList() {
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
		StringBuilder sb = new StringBuilder();
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
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSoftcertUploadSuppDocList())) {
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

	/**
	 * Throw KMC Portal exception msg in new page
	 * 
	 * @param String
	 *            msg
	 */
	private void throwKmcExceptionInNewPage(String msg) {
		if (msg == null || StringUtils.isBlank(msg)) {
			msg = SmCommonUtil.getMessage(
					"lbl.sm.softcert.call.kmcportal.err.msg", null);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						SmApplicationUtil.getApplicationErrorPage());
		fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
				msg));
	}

	/**
	 * Validate personnel to request softcert
	 */
	private void validateRequestSoftcert() {
		if (getManagedBean().getSoftCertPayList() != null) {
			for (SmPersonnel personnel : getManagedBean().getSoftCertPayList()) {
				if (personnel.getSmPersonnelAddresses() == null
						|| personnel.getSmPersonnelAddresses().isEmpty()) {
					throw new EpException(MessageConst.ERROR_722614,
							new String[] { personnel.getName() });
				}
			}
		}
	}

	/**
	 * Check whether softcert apply on behalf others
	 */
	private void setSoftcertApplyFor() {
		boolean applyForOthers = false;
		if (getManagedBean().getSoftCertPayList() != null) {
			for (SmPersonnel payPersonnel : getManagedBean()
					.getSoftCertPayList()) {
				if (getManagedBean().getLoginUserPersonnelInfo() != null
						&& !getManagedBean()
								.getLoginUserPersonnelInfo()
								.getIdentificationNo()
								.equalsIgnoreCase(
										payPersonnel.getIdentificationNo())) {
					applyForOthers = true;
				}
			}
		}
		getManagedBean().setApplySoftcertForOthers(applyForOthers);
	}

	/**
	 * Set whether to disable or enable revoke button
	 */
	private void initDisableRevokeButton() {
		if (getManagedBean().isRenderRevokeButton()) {
			if (getManagedBean().getLoginUserPersonnelInfo() != null
					&& (getManagedBean().getLoginUserPersonnelInfo()
							.getEpRole()
							.equals(RoleCode.G2G_SUPPLIER_ADMIN.value())
							|| getManagedBean()
									.getLoginUserPersonnelInfo()
									.getEpRole()
									.equals(RoleCode.MOF_SUPPLIER_ADMIN.value())
							|| getManagedBean()
									.getLoginUserPersonnelInfo()
									.getEpRole()
									.equals(RoleCode.BASIC_SUPPLIER_ADMIN
											.value()) || getManagedBean()
							.getLoginUserPersonnelInfo().getEpRole()
							.equals(RoleCode.SUPPLIER_TEMP.value()))) {
				getManagedBean().setRenderRevokeButton(true);
			} else {
				getManagedBean().setRenderRevokeButton(false);
			}
		}
	}

	public void navigateToViewSoftCert() {
		log.info("navigateToViewSoftCert");

		FacesScopes facesScope = new FacesScopes();
		ThemeDisplay themeDisplay = (ThemeDisplay) facesScope
				.getRequestScope(WebKeys.THEME_DISPLAY);

		log.info("getURLCurrent" + themeDisplay.getURLCurrent());

		try {
			String commonUrl = themeDisplay.getURLCurrent().substring(
					0,
					themeDisplay.getURLCurrent().indexOf(
							themeDisplay.getLayout().getGroup()
									.getFriendlyURL())
							+ themeDisplay.getLayout().getGroup()
									.getFriendlyURL().length());
			log.info("commonUrl" + commonUrl);

			ExternalContext externalContext = FacesContext.getCurrentInstance()
					.getExternalContext();

			externalContext.redirect(commonUrl + "/" + "my-softcert");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
