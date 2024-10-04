package my.ep.web.backingbean.sm.softcertreg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.constant.MessageConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
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
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmSoftcertDoc;
import my.ep.persistence.entity.sm.SmSoftcertDocFile;
import my.ep.persistence.entity.sm.SmSoftcertRequest;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.backingbean.sm.payment.PaymentBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.softcertreg.SoftCertManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = -621778849657233073L;
	public static Logger log = Logger.getLogger(SoftCertBackingBean.class);
	private String SELECT_PERSONNELS_PAGE = "1";
	private String UPLOAD_DOC_PAGE = "2";
	private String PAYMENT_PAGE = "3";
	private String REVOKE_PAGE = "4";

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
		getManagedBean().setRevokeMessage(retrieveRevokeMessage());
		SmCommonUtil.redirectToPage("softCertBasePage.xhtml", "", null, null);
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
				.getLoginUserPersonnelInfo(), false); //29027
		SmCommonUtil.redirectToPage("mySoftcertPage.xhtml", "", null, null);
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
					"/xhtml/sm/payment/portletViewMode.xhtml", "", null, null);
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
		getManagedBean().setSoftCertSelect(null);
		getManagedBean().setSoftCertSelectList(new ArrayList<SmPersonnel>());
	}

	/**
	 * Get personnel by login user
	 * 
	 * @param PmUser
	 *            usr
	 */
	public void getPersonnelByPmUser(PmUser usr) {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setUserId(usr.getUserId());
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
				.retrievePersonnelByUserId(reqDTO);
		if (resDTO.getSmPersonnel() != null
				&& resDTO.getSmPersonnel().getSmAppl() != null) {
			getManagedBean().setLoginUserPersonnelInfo(resDTO.getSmPersonnel());
			getManagedBean().setSmAppl(resDTO.getSmPersonnel().getSmAppl());
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
		// #29630 - start
		boolean result = false;
		boolean isAdmin = my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES.contains(getManagedBean()
				.getLoginUserPersonnelInfo().getEpRole());
		if (isAdmin) {
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(getManagedBean().getSmAppl().getSmSupplier().getSupplierId());
			reqDTO.setSmSupplier(getManagedBean().getSmAppl().getSmSupplier());
			if (!getManagedBean().getSoftcertUploadSuppDocList().isEmpty()) {
				for (SoftcertUploadSuppDocVO softCertList : getManagedBean().getSoftcertUploadSuppDocList()) {
					if(softCertList.getPersonnel().getPmUser() == null){
						continue;
					}
					reqDTO.setUserId(getManagedBean().getLoginUserPersonnelInfo().getPmUser().getUserId());
					if (!reqDTO.getUserId()
							.equals(getManagedBean().getLoginUserPersonnelInfo().getPmUser().getUserId())) {
						SmSoftcertRequest softcertRequest = SmServiceLookup.getPersonnelSO()
								.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO).getSoftcertRequest();
						if (softcertRequest != null && softcertRequest.getPmDigiCert() != null
								&& softcertRequest.getPmDigiCert().getValidFrom() != null
								&& softcertRequest.getPmDigiCert().getValidTo() != null) {
							result = DateUtils.isDateOvarlapping(softcertRequest.getPmDigiCert().getValidFrom(),
									softcertRequest.getPmDigiCert().getValidTo(), new Date(), new Date(), true);
						}
						if (!result) {
							//#30855 - START
							FacesScopes fs = new FacesScopes();
							if(fs.hasRoleUser(RoleCode.SUPPLIER_TEMP.value()) || 
							   fs.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value()) || 
							   fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) ||
							   fs.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
								throw new EpException(MessageConst.ERROR_DIGICERT_776064);
							}
							if(fs.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value()) || 
							   fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || 
							   fs.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()) || 
							   fs.hasRoleUser(RoleCode.FL_USER.value())){
								throw new EpException(MessageConst.ERROR_DIGICERT_776065);
							}
							//#30855 - END
						}
					}
				}
			}
		}
		// #29630 - end
		if (getManagedBean().getSuppDocMode().equals(
				SupplierManagementConst.SUPPORTING_DOC_ONLINE)) {
			validateUploadSuppDoc();
		}
		this.getManagedBean().setSoftCertPayList(
				this.getManagedBean().getSoftCertSelectList());
		updateAmount();
		setSoftCertPaymentBpmTask();
		setSoftcertApplyFor();
		getManagedBean().setGstPercentageLabel(
				SmCommonUtil
						.getMessage("lbl.sm.gst.percent",
								new String[] { getManagedBean().getGstRate()
										.toString() }));

		getManagedBean().setTargetPage(PAYMENT_PAGE);
	}

	public void navigateToRevokeUser() {
		getManagedBean().setTargetPage(REVOKE_PAGE);
		getManagedBean().setSoftCertSelect(null);
		getManagedBean().setSoftCertSelectList(new ArrayList<SmPersonnel>());
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

	// 17003
	public void assignSelectedSmSoftcertDocFile(SmPersonnel smPersonnel,
			SmSoftcertDoc doc, int index) {
		List<SmSoftcertDoc> softcertDocList = new ArrayList<SmSoftcertDoc>();
		softcertDocList.add(doc);
		SoftcertUploadSuppDocVO selectedSuppDoc = new SoftcertUploadSuppDocVO();
		selectedSuppDoc.setPersonnel(smPersonnel);
		selectedSuppDoc.setSuppDocList(softcertDocList);
		this.getManagedBean().setAttachedFileIndex(index);
		getManagedBean().setSelectedSuppDoc(selectedSuppDoc);
	}

	public void deleteSupDocFileAttachment() {
		if (getManagedBean().getSelectedSuppDoc() != null
				&& getManagedBean().getSelectedSuppDoc().getSuppDocList() != null
				&& !getManagedBean().getSelectedSuppDoc().getSuppDocList()
						.isEmpty()) {

			SoftcertUploadSuppDocVO selectedSuppDocVO = getManagedBean()
					.getSelectedSuppDoc();

			// construct file path
			String filePath = Calendar.getInstance().get(Calendar.YEAR)
					+ "/SM/" + "SOFTCERT/"
					+ selectedSuppDocVO.getPersonnel().getPmUser().getLoginId()
					+ "/";

			// init suppDocFile (currently only default one file)
			SmSoftcertDocFile docFile = new SmSoftcertDocFile();
			docFile.setSmSoftcertDoc(null);
			docFile.setFileName(StringUtils.EMPTY);
			selectedSuppDocVO.getSuppDocList().get(0)
					.setSmSofcertDocFiles(new ArrayList<SmSoftcertDocFile>());
			selectedSuppDocVO.getSuppDocList().get(0).getSmSofcertDocFiles()
					.add(docFile);

			// set selected doc to main list
			setSelectedSuppDocToMainList();

			// set empty value to deleted file
			SmCommonUtil.deleteUpload(filePath, docFile.getFileName());
		}
	}

	/**
	 * Delete attachment
	 * 
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void deleteAttachment() throws FileNotFoundException, IOException {
		try {
			if (getManagedBean().getSelectedSmSoftcertDocFile() != null) {
				for (SmSoftcertDocFile smSoftcertDocFile : getManagedBean()
						.getSmSoftcertDocFileList()) {
					if (smSoftcertDocFile
							.getSmSoftcertDoc()
							.getSoftcertDocId()
							.equals(getManagedBean()
									.getSelectedSmSoftcertDocFile()
									.getSmSoftcertDoc().getSoftcertDocId())) {
						smSoftcertDocFile.setFileName(StringUtils.EMPTY);
						smSoftcertDocFile.setFilePath(StringUtils.EMPTY);
						deleteselectedList(getManagedBean()
								.getSmSoftcertDocFileList());
						// deleteFiles(smSoftcertDocFile.getFilePath(),smSoftcertDocFile.getFileName(),smSoftcertDocFile.getFileName());

						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770054,
					new String[] { e.getMessage() });

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
				if (StringUtils.isNotBlank(personnel.getEpRole())
						&& personnel.getEpRole().equals(
								RoleCode.SUPPLIER_TEMP.value())) {
					getManagedBean().setSupplierTmpPersonnel(personnel);
				}
				// #29695 - start
				if (getManagedBean().isAlreadyHadFreeSoftcert()) {
					personnel.setFreeSoftCert(false);
					amountPerPersonnel = SmRuleHelper.getInstance().getSoftCertFee();
					amount = amount.add(amountPerPersonnel);
				} else {
					personnel.setFreeSoftCert(true);
				}
				/*
					*//**
					 * 20200908@MohdHafizAbdRahim - Bug #28264 - Start	
					 *//*
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL || personnel.getEpRole().equals(
                        RoleCode.MOF_SUPPLIER_USER.value())) {
					personnel.setFreeSoftCert(false);
					amountPerPersonnel = SmRuleHelper.getInstance()
							.getSoftCertFee();
					amount = amount.add(amountPerPersonnel);
					*//**
					 * 20200908@MohdHafizAbdRahim - Bug #28264 - End
					 *//*
				} else {
					*//**
					 * 20200908@MohdHafizAbdRahim - Bug #28264 - Start	
					 *//*
					personnel.setFreeSoftCert(true);
					*//**
					 * 20200908@MohdHafizAbdRahim - Bug #28264 - End
					 *//*
				}
				*/
				// #29695 - end
			} else {
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
					personnel.setFreeSoftCert(true);
				} else if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
					if (personnel.getSoftcertRejectCode() != null) {
						if (personnel
								.getSoftcertRejectCode()
								.equals(my.ep.constant.sm.SupplierManagementConst.REJECTED_WITH_FRAUD)
								|| personnel
										.getSoftcertRejectCode()
										.equals(my.ep.constant.sm.SupplierManagementConst.REJECTED_WITH_BLACKLIST)) {
							personnel.setFreeSoftCert(false);
							amountPerPersonnel = SmRuleHelper.getInstance()
									.getSoftCertFee();
						} else {
							personnel.setFreeSoftCert(true);
						}
					} else {
						personnel.setFreeSoftCert(true);
					}
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
			reqDTO.setSoftCertPaymentDocNo(getManagedBean()
					.getSoftCertBpmTask() == null ? null : getManagedBean()
					.getSoftCertBpmTask().getDocNumber());
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
				StringBuilder sbName = null;
				StringBuilder sbEmail = null;
				String name = StringUtils.EMPTY;
				String email = StringUtils.EMPTY;
				if (getManagedBean().getTotalPaymentAmount().compareTo(
						BigDecimal.ZERO) == 0) {
					//msgKey = MessageConst.INFO_721061;

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
					
					//#29481 - Start
					msgKey = MessageConst.INFO_721067;
					params = new String[] { name, email };
					//#29481 - End
					
				} else {
					sbName = new StringBuilder();
					if (!EntityHelper.isCollectionEmpty(reqDTO
							.getSmPersonnelList())) {
						for (SmPersonnel per : reqDTO.getSmPersonnelList()) {
							sbName.append(per.getName()).append(",");
						}
						name = sbName.toString();
						if (name.endsWith(",")) {
							name = name.substring(0, name.length() - 1);
						}
						params = new String[] {
								name,
								SmCommonUtil
										.getBasePortalAppUrlFromProperties()
										+ "task-list" };
					}
				}
				renewSesionObject();
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(), msgKey,
						params, FacesMessage.SEVERITY_INFO);
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
			if (getManagedBean().getSmAppl().getSupplierType()
					.equals(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT)) {
				return "0.00 ("
						+ SmCommonUtil.getMessage("lbl.sm.softcert.g2gfree",
								null) + ")";
			} else if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
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
			ex.printStackTrace();
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
			ex.printStackTrace();
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
			ex.printStackTrace();
			throwKmcExceptionInNewPage(ex.getMessage());
		}
	}

	/**
	 * Revoke soft certificate
	 */
	public void revoke() {
		try {
			SmPersonnel revokePersonnel = getManagedBean().getSoftCertSelect() != null ? getManagedBean()
					.getSoftCertSelect() : getManagedBean()
					.getLoginUserPersonnelInfo();
			getManagedBean().getSoftCertMaintenanceBackingBean()
					.RequestRevokeCert(
							revokePersonnel.getIdentificationNo(),
							getManagedBean().getLoginUserPersonnelInfo()
									.getSmAppl().getSmSupplier().getEpNo());

		} catch (Exception ex) {
			log.info(ex);
			log.info("soft__revoke");
			ex.printStackTrace();
			log.error(ex);
			throwKmcExceptionInNewPage(ex.getMessage());
		}
	}

	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * If supplier admin apply for others, show SPKI and if apply for himself
	 * just directly show declaration popup
	 */
	public void showConfirmDialog() {
		validateRequestSoftcert();
		RequestContext.getCurrentInstance().execute("confirmDialog.show()");
	}

	/**
	 * Submit softcert request
	 */
	public void onSubmit() {
		if (getManagedBean().isApplySoftcertForOthers()) {
			onDisplaySpkiPopup();
		} else {
			makePayment();
			RequestContext.getCurrentInstance().update("@form");
		}
	}

	/**
	 * Call SPKI Popup dialog
	 */
	public void onDisplaySpkiPopup() {
		getManagedBean().getSpkiBackingBean().getChallengeQuestions(
				getManagedBean().getLoginUserPersonnelInfo()
						.getIdentificationNo());
		RequestContext.getCurrentInstance().execute("spki_dlg.show()");
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
		byte[] signedData = spkiBackingBean.spkiSignTransaction(
				prepareOriginalText(), getManagedBean()
						.getLoginUserPersonnelInfo().getIdentificationNo());
		pmDigiSign.setSpkiSignedData(signedData);
		getManagedBean().setDigiSign(pmDigiSign);
		if (getManagedBean().getTargetPage() == PAYMENT_PAGE) {
			makePayment();
		} else if (getManagedBean().getTargetPage() == REVOKE_PAGE) {
			revoke();
		}
		RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
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
		Timestamp date = new Timestamp(System.currentTimeMillis());
		pmDigiSign.setDocId(new BigDecimal(docId));
		pmDigiSign.setDocNo(docNumber);
		pmDigiSign.setDocType(docType);
		pmDigiSign.setCreatedBy(getUserIdSessionFromLoginId()); // #31849 add log key
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
		getManagedBean().setSoftCertBpmTask(
				SmCommonUtil.getSingleBpmWorkListByTaskTitleAndToken(
						"lbl.sm.addAdditional.makePayment", getManagedBean()
								.getToken()));
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
		getManagedBean().setSoftCertSelectList(new ArrayList<SmPersonnel>());
		getManagedBean().setRevokePersonnelList(new ArrayList<SmPersonnel>());
		List<Integer> statusAllowedToRequest = Arrays.asList(
				SupplierManagementConst.SOFT_CERT_PENDING_REQ,
				SupplierManagementConst.SOFT_CERT_REJECTED,
				SupplierManagementConst.SOFT_CERT_REQ_FAILED,
				SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
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
			else if (!statusAllowedToRequest.contains(personnel
					.getSoftcertFlg())) {
				personnel.setDisableRowSelection(true);

				// issued softcert personnel that can do revoke action
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED) {
					getManagedBean().getRevokePersonnelList().add(personnel);
				}

			}
			// check for softcert flg that pending request or rejected or failed
			else {

				// set rejected or failed softcert request reason
				if (personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED
						|| personnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REQ_FAILED) {
					personnel.setDisableRowSelection(false);
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
			setPersonnelSoftcertInformation(personnel, true);  //29027
		}
		setAlreadyHadFreeSoftcert();
		setDisableFlUserRowSelection(personnels);
		setDisableRowSelectionIfSuppTmpNotRequestSoftCert(personnels);
		setDisableRowSelectionIfLoginUserSuppUser(personnels);
		initDisableRevokeButton();
	}

	/**
	 * Set whether this supplier already given free softcert
	 */
	private void setAlreadyHadFreeSoftcert() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(getManagedBean().getLoginUserPersonnelInfo()
				.getSmAppl().getSmSupplier().getSupplierId());
		getManagedBean().setAlreadyHadFreeSoftcert(
				SmServiceLookup.getPersonnelSO()
						.isSupplierAlreadyHadFreeSoftcert(reqDTO));

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
	 * Supplier User can only request softcert for his/her self only. Other
	 * users will be disabled
	 * 
	 * @param List
	 *            <SmPersonnel> personnels
	 */
	private void setDisableRowSelectionIfLoginUserSuppUser(
			List<SmPersonnel> personnels) {
		if (getManagedBean().getLoginUserPersonnelInfo() != null
				&& !StringUtils.isBlank(getManagedBean()
						.getLoginUserPersonnelInfo().getEpRole())) {
			boolean isAdmin = my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
					.contains(getManagedBean().getLoginUserPersonnelInfo()
							.getEpRole());
			for (SmPersonnel per : personnels) {
				if (!isAdmin
						&& per.getPersonnelId().compareTo(
								getManagedBean().getLoginUserPersonnelInfo()
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
	private void setPersonnelSoftcertInformation(SmPersonnel smPersonnel, Boolean isSoftcertManagement) {
		String softcertValidityDate = SmCommonUtil.getMessage(
				"lbl.sm.no.available", null);
		String sofcertNo = SmCommonUtil.getMessage("lbl.sm.no.available", null);
		boolean disableButton = true;

		if (smPersonnel.getPmUser() != null
				&& SupplierManagementConst.SOFT_CERT_SHOW_INFORMATION.contains(smPersonnel.getSoftcertFlg())) { // 29027
			getManagedBean().setRenderRevokeButton(true);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(getManagedBean().getLoginUserPersonnelInfo()
					.getSmAppl().getSmSupplier().getSupplierId());
			reqDTO.setSmSupplier(getManagedBean().getLoginUserPersonnelInfo().getSmAppl().getSmSupplier()); //29027
			reqDTO.setUserId(smPersonnel.getPmUser().getUserId());
			SmSoftcertRequest softcertRequest = SmServiceLookup
					.getPersonnelSO()
					.retrievePersonnelSoftcertRequestBySupplierIdAndUserId(
							reqDTO).getSoftcertRequest();
			//29027 - start
			if (softcertRequest != null && softcertRequest.getPmDigiCert() == null
					&& (smPersonnel.getSoftcertFlg() == 3 && !isSoftcertManagement)) {
				softcertRequest = SmServiceLookup
						.getPersonnelSO()
						.retrieveLatestPersonnelSoftcertRequestBySupplierIdAndUserId(reqDTO,
								smPersonnel.getSoftcertFlg(), isSoftcertManagement).getSoftcertRequest();
			}
			//29027 - end
			
			if (softcertRequest != null
					&& softcertRequest.getPmDigiCert() != null) {
				if (StringUtils.isNotBlank(softcertRequest.getPmDigiCert()
						.getCertSerialNo())) {
					sofcertNo = softcertRequest.getPmDigiCert()
							.getCertSerialNo();
					disableButton = false;
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
					renewDate.add(Calendar.MONTH,
							retrieveSoftcertRenewalPeriod());

					if (DateUtils.isDateGreaterOrEqual(new Date(),
							renewDate.getTime())) {
						disableButton = false;
						smPersonnel
								.setSoftcertFlg(SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL);
						smPersonnel.setDisableRowSelection(false);
						
						// check if renewal already requested for softcert but still
						// pending payment
						if (smPersonnel.getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_READY_FOR_RENEWAL) {
							reqDTO = new PersonnelReqDTO();
							reqDTO.setPersonnelId(smPersonnel.getPersonnelId());

							int count = SmServiceLookup.getPersonnelSO()
									.countPersonnelSoftcertBill(reqDTO);

							//#34964 - start
							int countBillDtl = SmServiceLookup.getPersonnelSO()
									.countPersonnelSoftcertBillDtl(reqDTO);

							if (count > 0 && countBillDtl == 0) {
								smPersonnel.setDisableRowSelection(true);
							}
							//#34964 - end
						}
					}
				}
			}
		}
		smPersonnel.setSoftcertValidityDate(softcertValidityDate);
		smPersonnel.setSoftcertNo(sofcertNo);
		getManagedBean().setDisableButton(disableButton);
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
		// #31462 - start
		// check role for downgrade MOF
		if (!basic
				&& (getManagedBean().getLoginUserPersonnelInfo().getEpRole()
						.equals(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || getManagedBean().getLoginUserPersonnelInfo()
						.getEpRole().equals(RoleCode.BASIC_SUPPLIER_USER.value()))) {
			basic = true;
		}
		// #31462 - end
		String businessType = (appl != null && appl.getSmSupplier() != null) ? appl
				.getSmSupplier().getBusinessType() : StringUtils.EMPTY;
		List<PmDocument> softcertSuppDocList = new ArrayList<PmDocument>();
		DocumentReqDTO reqDTO = null;
		setSoftcertUserSuppDoc();
		if (!EntityHelper.isCollectionEmpty(getManagedBean()
				.getSoftcertUploadSuppDocList())) {
			SmSoftcertDoc softcertDoc = null;
			Locale userLocale = new FacesScopes().getUserLocale();
			for (SoftcertUploadSuppDocVO softcertUploadSuppDocVO : getManagedBean()
					.getSoftcertUploadSuppDocList()) {
				if (EntityHelper.isCollectionEmpty(softcertUploadSuppDocVO
						.getSuppDocList())) {
					softcertUploadSuppDocVO
							.setSuppDocList(new ArrayList<SmSoftcertDoc>());
					reqDTO = new DocumentReqDTO();
					reqDTO.setUserLocale(userLocale);
					reqDTO.setBasicAccount(basic);
					reqDTO.setCompanyTypeCode(businessType);
					reqDTO.setOwner(softcertUploadSuppDocVO.getPersonnel()
							.isEquityOwner());
					// #31462 - start
					reqDTO.setFirstUser((softcertUploadSuppDocVO.getPersonnel().getEpRole()
							.equals(RoleCode.SUPPLIER_TEMP.value())
							|| softcertUploadSuppDocVO.getPersonnel().getEpRole()
									.equals(RoleCode.BASIC_SUPPLIER_ADMIN.value()) || softcertUploadSuppDocVO
							.getPersonnel().getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value())) ? true : false);
					// #31462 - end

					PmParameter paramID = SmCommonUtil.retrieveParameterById(softcertUploadSuppDocVO.getPersonnel().getIdentificationTypeId());
					reqDTO.setForeigner(paramID
							.getParameterCode()
							.equals(SupplierManagementConst.IDENTIFICATION_TYPE_IC) ? false
							: true);
					softcertSuppDocList = SmServiceLookup.getDocumentSO()
							.retrieveSoftcertSuppDocuments(reqDTO)
							.getDocumentList();
					for (PmDocument softcertSuppDoc : softcertSuppDocList) {
						softcertDoc = new SmSoftcertDoc();

						if (ParameterConst.DOCUMENT_DEFINITION_KEY_AUTHORIZATION_LETTER
								.equals(softcertSuppDoc.getDocDefinitionKey())
								|| ParameterConst.DOCUMENT_DEFINITION_KEY_OFFICIAL_ACCEPTANCE_LETTER
										.equals(softcertSuppDoc
												.getDocDefinitionKey())) {
							String letterUrl = "";
							if (CommonConstants.LANGUAGE_CODE_MALAY
									.equals(userLocale.getLanguage())) {
								letterUrl = "(Klik <a href=\""
										+ DocumentRepositoryUtils.getBaseDir()
										+ "shared/SM/"
										+ "Permohonan_Sijil_Digital.docx\">Sini"
										+ "</a> untuk muat turun contoh surat)";
							} else {
								letterUrl = "(Click <a href=\""
										+ DocumentRepositoryUtils.getBaseDir()
										+ "shared/SM/"
										+ "Application_For_Digital_Certificate.docx\">Here"
										+ "</a> to download sample of letter)";
							}
							String docName = softcertSuppDoc.getDocName();
							softcertSuppDoc.setDocName(docName.replace(
									"<here>", ""));
							softcertSuppDoc.setDocNameWithUrl(docName.replace(
									"<here>", letterUrl));
						} else {
							softcertSuppDoc.setDocNameWithUrl(softcertSuppDoc
									.getDocName());
						}

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
		
		//#27371 - start
		validateSelectedPersonnelInfo();
		//#27371 - end

		if (softcertSuppDocList == null || softcertSuppDocList.isEmpty()) {
			getManagedBean().setSuppDocMode("H");
		}

		if (StringUtils.isNotBlank(getManagedBean().getSoftCertSelectList()
				.get(0).getSoftcertRejectCode())) {
			String scertRejectCode = getManagedBean().getSoftCertSelectList()
					.get(0).getSoftcertRejectCode();
			if (scertRejectCode
					.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECTED_WITH_BLACKLIST)
					|| scertRejectCode
							.equalsIgnoreCase(my.ep.constant.sm.SupplierManagementConst.REJECTED_WITH_FRAUD)) {
				getManagedBean().setSuppDocMode(
						SupplierManagementConst.SUPPORTING_DOC_OFFLINE);
			}
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
	
	// #27371 - start
	public void validateSelectedPersonnelInfo() {
		if (getManagedBean().getSoftCertSelectList() != null) {
			for (SmPersonnel selectedPersonnel : getManagedBean().getSoftCertSelectList()) {
				log.info("selectedPersonnel: " + selectedPersonnel.getSmAppl().getApplId() + " || " + selectedPersonnel.getPersonnelId() + " || " + selectedPersonnel.getName()
						+ " || " + selectedPersonnel.getIdentificationNo());

				if (StringUtils.isBlank(selectedPersonnel.getEmail())) {
					throw new EpException(MessageConst.ERROR_721056, new String[] { selectedPersonnel.getName() });
				}

				if (StringUtils.isBlank(selectedPersonnel.getMobileNo()) && StringUtils.isBlank(selectedPersonnel.getPhoneNo())) {
					throw new EpException(MessageConst.ERROR_721057, new String[] { selectedPersonnel.getName() });
				}
			}
		}
	}
	// #27371 - end

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
				/*#28201 - Start*/		
				int count = 0;
				List<String> address = new ArrayList<>();
				address.add("R");
				address.add("M");
				address.add("I");
				address.add("O");
				if (personnel.getSmPersonnelAddresses() == null || personnel.getSmPersonnelAddresses().isEmpty()){
					throw new EpException(MessageConst.ERROR_722614,
							new String[] { personnel.getName() });
				}
				boolean ctrR = false;
				boolean ctrO = false;
				boolean ctrI = false;
				boolean ctrM = false;
				boolean error = false;
				for (SmPersonnelAddress personnelAddress : personnel.getSmPersonnelAddresses()){
					if (address.contains(personnelAddress.getAddressType())){
						switch (personnelAddress.getAddressType()){
						case("R") : if(ctrR) error = true;
						else ctrR = true; break;
						case("O") : if(ctrO) error = true;
						else ctrO = true; break;
						case("I") : if(ctrI) error = true;
						else ctrI = true; break;
						case("M") : if(ctrM) error = true;
						else ctrM = true; break;
						}
						if(error == true){
							throw new IllegalArgumentException("Error - Address cant be duplicated");
						}
						count++;
					}
					//35033-start
					if(personnelAddress.getSmAddress().getPostcode() == null || personnelAddress.getSmAddress().getPostcode().isEmpty()){
						throw new EpException(MessageConst.ERROR_722682,
								new String[] { personnel.getName() });
					}//35033-end
				}
				if (personnel.getSmAppl().getSupplierType().equals(SupplierManagementConst.ACC_BASIC)){
					if (count != 2){
						throw new EpException(MessageConst.ERROR_722614,
								new String[] { personnel.getName() });
					}
				} else if (personnel.getSmAppl().getSupplierType().equals(SupplierManagementConst.ACC_MOF)){
					if (count != 4){
						throw new EpException(MessageConst.ERROR_722614,
								new String[] { personnel.getName() });
					}
				}
				if(personnel.getEmail() == null || personnel.getEmail().isEmpty()
						|| (personnel.getMobileNo() == null && personnel.getPhoneNo() == null)){
					throw new EpException(MessageConst.ERROR_722682,
							new String[] { personnel.getName() });
				}
				/*#28201 - End*/
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
				log.info("payPersonnel: " + payPersonnel.getName() + " : "
						+ payPersonnel.getIdentificationNo());
				log.info("login Personnel: "
						+ getManagedBean().getLoginUserPersonnelInfo()
								.getName()
						+ " : "
						+ getManagedBean().getLoginUserPersonnelInfo()
								.getIdentificationNo());

				if (getManagedBean().getLoginUserPersonnelInfo() != null
						&& !getManagedBean()
								.getLoginUserPersonnelInfo()
								.getIdentificationNo()
								.trim()
								.equalsIgnoreCase(
										payPersonnel.getIdentificationNo()
												.trim())) {
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
					&& (my.ep.constant.sm.SupplierManagementConst.SUPPLIER_ADMIN_ROLES
							.contains(getManagedBean()
									.getLoginUserPersonnelInfo().getEpRole()))
					&& !getManagedBean().getRevokePersonnelList().isEmpty()) {//#29616
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

	/**
	 * Retrieve Soft Cert Renewal Period
	 * 
	 * @return int
	 */
	private int retrieveSoftcertRenewalPeriod() {
		int softCertPeriod = SmServiceLookup.getSmRuleSO()
				.retrieveSoftCertRenewalPeriod().getSoftCertRenewalMonth();
		softCertPeriod = -softCertPeriod;

		return softCertPeriod;
	}

	/**
	 * Retrieve Soft cert revoke message
	 * 
	 * @return docUrl
	 */
	private String retrieveRevokeMessage() {
		String url = "\""
				+ DocumentRepositoryUtils.getBaseDir()
				+ "shared/SM/"
				+ (CommonConstants.LANGUAGE_CODE_MALAY.equals(new FacesScopes()
						.getUserLocale().getLanguage()) ? "Surat_Kebenaran_Pembatalan.docx"
						: "Revocation_Authorization_Letter.docx") + "\"";
		return SmCommonUtil.getMessage("lbl.sm.softcert.revoke.instruction",
				new String[] { url });
	}
	/** disabled due to 30425
	 * Call SPKI Popup dialog
	 */
	/*public void onDisplaySpkiPopup2() {
		getManagedBean().getSpkiBackingBean().getChallengeQuestions2(
				getManagedBean().getLoginUserPersonnelInfo()
						.getIdentificationNo());
		RequestContext.getCurrentInstance().execute("spki_dlg.show()");
	}*/
	
}
