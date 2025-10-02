package my.ep.web.backingbean.sc.loa;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletSession;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDirectAwardSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.virtualcert.IVirtualCertSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.DirectAwardReqDTO;
import my.ep.dto.sc.quationtender.DirectAwardResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertReqDTO;
import my.ep.dto.sm.virtualcert.VirtualCertResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaApproval;
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaSupportingDoc;
import my.ep.persistence.entity.sc.loaloi.ScLoaSupportingDocAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecTierQty;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.sc.common.ItemBean;
import my.ep.web.bean.sc.common.JadualHargaItemBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.SSTConstant;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.report.QtReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.format.AttachmentDefaultFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.sc.loa.DirectLOAManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.NumberWordUtilEn;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.VmUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 * The Class directLOABackingBean.
 * 
 * @author Sri
 * 
 * @since 15/06/2015
 */
@ManagedBean(name = "directLOABackingBean")
@RequestScoped
public class DirectLOABackingBean extends LOABaseBackingBean<DirectLOAManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The log. */
	private static Logger log = EpLogger.getLogger(DirectLOABackingBean.class);

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;

	/** The text before sign. */
	private String textBeforeSign;

	/** The flag. */
	private boolean flag;

	private boolean suppDocFlag;

	private static final String PIPE_SYMBOL = "|";
	private static final String CARET_SYMBOL = "^";
	private static final String BRANCH_INFO_TITLE = "Branch Information";
	private static final String MALAY_NA = "T/B";
	
	// 26130 - start
	private static final Locale myLocale = new Locale(SourcingConstants.MALAY_LANGUAGE_CODE,
			SourcingConstants.MALAYSIA_COUNTRY_CODE);

	// 26130 - end
	/* #29007 Start Vinod */
	private String itemNameSearchCriteria;
	private String lampiranA3SubStr = "Lampiran A3";
	// #29007 -end-

	/**
	 * Instantiates a new LOA detail backing bean.
	 */
	public DirectLOABackingBean() {
		log.info("inside DirectLOABackingBean constructor");
	}

	/**
	 * save the LOA details. save - save data into database and initiate task
	 * for LOA details. page navigate to loa details page.
	 */
	public void saveAction(boolean supplier) {
		debugLogger("saveAction");
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		try {
			String outcome = BpmOutcomeConst.BPM_SAVE;
			ScLoa scLoa = this.getManagedBean().getScLoa();
			ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();
			boolean validateWitness = this.getManagedBean().isValidatewitness(); // #17376
			boolean directLoa=this.getManagedBean().isIconForDL(); //#29007
			// #29007 Start Vinod
			String bpmVersionNo = this.getManagedBean().getBpmVersionNo();
			// #29007 End Vinod

			// Bug #38049@Syazani 29/04/2025 -- Start
			PmParameter selectedOrgType = null != this.getManagedBean().getSelectedOrgType() ? (PmParameter) this
					.getManagedBean().getSelectedOrgType().cloneEntity() : null;
			PmOrgProfile selectedPtj = null != this.getManagedBean().getSelectedPtj() ? (PmOrgProfile) this
					.getManagedBean().getSelectedPtj().cloneEntity() : null;
			// Bug #38049@Syazani 29/04/2025 -- End
			
			if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
					.equals(statusId)) {
				validateSupplierWitnessDetails(scLoa, loaSupplierWitness, outcome);
			}
			parseLOADetails(scLoa, outcome, reqDTO);
			//20200325@asyrafhaziq _ Bug #27740 - Start
			validateLoaAmount(scLoa);
			//20200325@asyrafhaziq _ Bug #27740 - End
			validateSupplierDetails(scLoa);
			validateDeductionPaymentDtls(scLoa);
			validateLOAReportAttachments(scLoa);
			validateContractNGovtSignDate(scLoa);

			// #34413 - Start
			if (scLoa.getJustificationType() != null && !isPk7Periodic()) {
				scLoa.setJustificationType(null);
			}
			// #34413 - End

			if (!supplier) {
				updateContractDetails(scLoa);
			}
			validateContractDetails(scLoa);
			validateDuplicateItems(this.getManagedBean().getProdscQtDoc(), this.getManagedBean().getServicescQtDoc());

			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setScLoa(scLoa);
			reqDTO.setScQtDoc(this.getManagedBean().getProdscQtDoc());
			reqDTO.setServiceQtDoc(this.getManagedBean().getServicescQtDoc());
			reqDTO.setOutcome(outcome);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			updateToBeDeleteSpecAnswersTOReq(reqDTO);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTobeDeleteProPayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			reqDTO.setToBeDeleteschedules(this.getManagedBean()
					.getToBeRemoveSchedules());
			reqDTO.setToBeDeltedZoneCriteria(this.getManagedBean()
					.getToBedeletedSupplierZone());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.saveDirectAwardDetails(reqDTO);
			scLoa = resDTO.getLoa();

			if (resDTO.isSuccessful()
					&& null == this.getManagedBean().getTaskId()) {
				if (QTStatusConst.LOA_PENDING_SUBMISSION
						.equals(statusId)) {
					// rename temporary Loa No folder name in docRepo
					RenameTempLoaNoRepoDirByLoaNo(scLoa);
				}
				successMessage(MessageConst.INFO_700025,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD,
						scLoa.getLoaNo(), Boolean.TRUE);
			} else {
				int selectedTabIndx = this.getManagedBean()
						.getSelectedTabIndex();
				String taskId = this.getManagedBean().getTaskId();
				String[] outComes = this.getManagedBean().getOutComes();
				SCAwardSupplierDataType awardSupplierDataType = (SCAwardSupplierDataType) this
						.getManagedBean().getPayLoad();
				renewSesionObject();
				this.getManagedBean().setTaskId(taskId);
				this.getManagedBean().setOutComes(outComes);
				this.getManagedBean().setSelectedTabIndex(selectedTabIndx);
				this.getManagedBean().setPayLoad(awardSupplierDataType);
				this.getManagedBean().setValidatewitness(validateWitness); //#17376
				this.getManagedBean().setIconForDL(directLoa); //#29007

				// Bug #38049@Syazani 29/04/2025 -- Start
				if (null != selectedOrgType) {
					this.getManagedBean().setSelectedOrgType(selectedOrgType);
				}
				if (null != selectedPtj) {
					this.getManagedBean().setSelectedPtj(selectedPtj);
				}
				// Bug #38049@Syazani 29/04/2025 -- End

				if (!QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(scLoa.getPmStatus().getStatusId())) {
					retriveLoaDetails(scLoa.getLoaId(), null, false);

					contractTypeChanged();
				}
				if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(scLoa.getPmStatus().getStatusId())) {
					getSupplierDeatilsByLoaId(scLoa.getLoaId());

				}

				successMessage(MessageConst.INFO_700025,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD,
						scLoa.getLoaNo(), Boolean.FALSE);
			}
			// #29007 Start Vinod
			this.getManagedBean().setBpmVersionNo(bpmVersionNo);
			// #29007 End Vinod

		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Download report as pdf or xlsx files in Letter of Acceptance.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void downloadReport(ScLoaReport scLoaReport) throws IOException {
		debugLogger("downloadReport");
		if(scLoaReport.getTitle().equalsIgnoreCase(QTConstants.REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT) &&
				getManagedBean().getScLoa().getPmStatus().getStatusId().
				equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
			// #17376 -start-
			if (this.getManagedBean().isValidatewitness()) {
				validateLoaWitnessAndAckBy();
			}
			// #17376 -end-
		}
		boolean isGenerate = checkDownloadReportStatus(scLoaReport);
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport,isGenerate);
		if(scLoaReport.getDefinationType().getParameterCode().equals(
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) ||
				scLoaReport.getDefinationType().getParameterCode().equals(
						ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
		}
		if(scLoaReportAttachmentGenerated != null) {
			String prefixName = scLoaReport.getTitle().replaceAll(
					SourcingConstants.REG_EXP_WHITE_SPACE, "");
			// #26770-start
			scLoaReportAttachmentGenerated.setTempFileName(setValueLowercase(prefixName, 18));
			// 26770-end
		}
		downloadFile(scLoaReportAttachmentGenerated);
	}
	
	/**
	 * check status to force generate attachment for upload and download
	 * definition type
	 * 
	 * @param scLoaReport
	 * @return return true or false to force generate attachement
	 */
	public boolean checkDownloadReportStatus(ScLoaReport scLoaReport){
		
		boolean isGenerate = false;
		// Enhancement #26130 -start-
		if (scLoaReport.getDefinationType().getParameterCode()
				.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD)
				&& (getManagedBean().getScLoa().getPmStatus().getStatusId()
						.equals(QTStatusConst.LOA_PENDING_SUBMISSION)
						|| getManagedBean().getScLoa().getPmStatus().getStatusId()
								.equals(QTStatusConst.LOA_PENDING_VERIFICATION) || getManagedBean().getScLoa()
						.getPmStatus().getStatusId().equals(QTStatusConst.LOA_COMPLETED))) {
			// Enhancement #26130 -end-
			isGenerate = true;
		}
		if(scLoaReport.getDefinationType().getParameterCode().equals(
				ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)&&
				getManagedBean().getScLoa().getPmStatus().getStatusId().equals(
						QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)){
			isGenerate = true;
		}
		
		return isGenerate;
	}
	
	/**
	 * submit LOA details. submit - save data into database and BPM process will
	 * route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string page navigate to acknowledge page.
	 */
	public void submitAction() {
		debugLogger("submitAction");
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		try {
			String outcome = BpmOutcomeConst.BPM_SUBMIT;
			ScLoa scLoa = this.getManagedBean().getScLoa();

			parseLOADetails(scLoa, outcome, reqDTO);

			validateSupplierDetails(scLoa);
			//20200325@asyrafhaziq _ Bug #27740 - Start
			validateLoaAmount(scLoa);
			//20200325@asyrafhaziq _ Bug #27740 - End
			validateLOAReportAttachments(scLoa);
			validateLOADetails(scLoa, outcome);

			validateContractAmount(scLoa.getLoaAmt(), outcome);
			validateContractNGovtSignDate(scLoa);
			updateContractDetails(scLoa);
			validateContractDetails(scLoa);
			validateContractAdmin(scLoa); // #26474 validate contract admin

			// Bug #37791@Syazani -- 04/02/2025 -- Start
			if (scLoa.getScLoaApproval() != null && !scLoa.isApprovalReq()) {
				scLoa.setScLoaApproval(null);
			}
			// Bug #37791@Syazani -- 04/02/2025 -- End
			
			validateDuplicateItems(this.getManagedBean().getProdscQtDoc(),this.getManagedBean().getServicescQtDoc());
			scLoa.setScLoaWorkFlowStatuses(null);
			// To avoid insert workflow status when direct submit without save.
			if (scLoa.isBrandNew()) {
				List<ScLoaRemark> remarks = scLoa.getScLoaRemarks();
				if (CollectionUtils.isNotEmpty(remarks)) {
					for (ScLoaRemark remark : remarks) {
						remark.setScWorkFlowStatus(null);
					}
				}	
			}
			
			reqDTO.setScLoa(scLoa);

			// #26474 - start
			if (getManagedBean().getContractSignerUser() != null) {
				reqDTO.getScLoa().setContractSigner(getManagedBean().getContractSignerUser());

				String designation = getManagedBean().getContractSignerUser().getDesignation();

				reqDTO.getScLoa().setLoaSignerDesig(designation);
			}

			reqDTO.setLanguageCode(CommonConstants.LANGUAGE_CODE_MALAY);

			if (this.getManagedBean().getSelectedPtj() != null) {
				reqDTO.getScLoa().setContractAgency(this.getManagedBean().getSelectedPtj());
			}
			// #26474 - end

			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setScQtDoc(this.getManagedBean().getProdscQtDoc());
			reqDTO.setServiceQtDoc(this.getManagedBean().getServicescQtDoc());
			updateToBeDeleteSpecAnswersTOReq(reqDTO);
			reqDTO.setTobeDeleteProPayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			reqDTO.setToBeDeleteschedules(this.getManagedBean()
					.getToBeRemoveSchedules());
			reqDTO.setToBeDeltedZoneCriteria(this.getManagedBean()
					.getToBedeletedSupplierZone());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.submitDirectAwardDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();
				RenameTempLoaNoRepoDirByLoaNo(scLoa);
				successMessage(MessageConst.INFO_700026,
						ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD,
						scLoa.getLoaNo(), Boolean.TRUE);
			}
		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * Gets the agreement letter.
	 * 
	 * @return the loa stream content
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public StreamedContent getLoaStreamContent() throws IOException {
		StreamedContent loaStreamContent = null;
		ScLoaReport agreementLetterReport = null;
		for (ScLoaReport scLoaReport : getManagedBean().getScLoa()
				.getScLoaReports()) {
			if (null != scLoaReport.getPmDocument()) {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();

				if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
						.equals(docDefinitionKey)) {
					agreementLetterReport = scLoaReport;
					break;
				}
			}
		}

		if (agreementLetterReport != null) {
			ScLoaReportAttachment agreementLetterAttachment = generateDocumentAttachment(agreementLetterReport,false);
			agreementLetterReport
			.setScLoaReportAttachment(agreementLetterAttachment);

			loaStreamContent = getAttachmentStreamContent(
					agreementLetterAttachment,
					CommonConstants.FILE_CONTENT_TYPE_PDF);
		}
		return loaStreamContent;
	}

	/**
	 * Edits the supplier loa action. page navigate to loa supplier page.
	 * 
	 * @param loaId
	 *            the loa id
	 */
	public void getSupplierDeatilsByLoaId(Long loaId) {
		debugLogger("editSupplierLOAAction");
		try {
			DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
			reqDTO.setLoaId(loaId);

			DirectAwardResDTO resDTO = getQtDirectAwardSO()
					.retriveSupplierDetails(reqDTO);

			ScLoa scLoa = resDTO.getLoa();
			getManagedBean().setScLoa(scLoa);
			this.getManagedBean().setScQt(scLoa.getScQt());

			// Supplier Bank Account
			getManagedBean().setBankList(resDTO.getSmSupplierBanks());
			
			retrieveValBankAccNoDropdown(); //Bug #33674@Syazani -- 14/09/2022
			
			// overall ct amount or loa amt
//			BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
			BigDecimal overallCtAmout = overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt());
			getManagedBean().setOverallContractAmt(overallCtAmout);
			scLoa.setOverallContractAmt(overallCtAmout);

			// Setting BI Report
			List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
			if (scLoaReports != null && !scLoaReports.isEmpty()) {
				//Bug #32188 - start
				if (ParameterConst.DOCTYPE_DIRECT_NEGO
						.equalsIgnoreCase(scLoa.getScLoiLoa().getDocType())) {
					for (ScLoaReport report : scLoaReports) {
						if (null != report.getPmDocument()) {
							if (SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
									.equalsIgnoreCase(report.getPmDocument().getDocDefinitionKey())) {
								getManagedBean().setBranchInfoReport(report);
								break;
							}
						}
					}
				}else{
					for (ScLoaReport report : scLoaReports) {
						// Check whether Branch Info attachment is existed or not
						if (null == report.getPmDocument()) {
							getManagedBean().setBranchInfoReport(report);
							break;
						}
					}
				}
				//Bug #32188 - end
			}
			// Supplier Witness
			ScLoaWitness loaSupplierWitness = null;
			if(scLoa.getScLoaWitnesses() != null){
				List<ScLoaWitness> scLoaWitnesses = scLoa.getScLoaWitnesses();
				for (ScLoaWitness scLoaWitness : scLoaWitnesses) {
					String partyType = scLoaWitness.getPartyType();

					if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER
							.equals(partyType)) {
						loaSupplierWitness = scLoaWitness;
					}
				}
				if (loaSupplierWitness == null) {
					loaSupplierWitness = new ScLoaWitness();
					loaSupplierWitness
					.setPartyType(ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER);
					loaSupplierWitness.setScLoa(scLoa);

					scLoaWitnesses.add(loaSupplierWitness);
				}
			}
			getManagedBean().setLoaSupplierWitness(loaSupplierWitness);

			getManagedBean().setScQt(scLoa.getScQt());

			prepareSupplierSignerInfo();

//			scLoa = calcBondAmount(scLoa, true);
			scLoa = calcBondAmountWithSST(scLoa, true);
			
			Date issueDate = scLoa.getIssueDate();
			Date bondRespDueDate = DateUtils.addDays(issueDate, 30);
			scLoa.setBondResDueDate(bondRespDueDate);

			// #35977 Updated for English Start Vinod
			if (scLoa.getBondAmt() != null
					&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
				getManagedBean().setBondAmountWord(NumberWordUtilEn.convert(scLoa.getBondAmt().toString()));
			} else if (scLoa.getBondAmt() != null
					&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
				getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			} else {
				getManagedBean().setBondAmountWord(null);
			}
			// #35977 End Vinod

			generateDeclarationFromVelocity(scLoa, scLoa.getScQt());

			if (scLoa.getScQt().isZonal()) {
				List<TplSpecZoneDtl> zoneDetails = resDTO.getSpecZoneDetails();
				constructTreeNodeForZones(zoneDetails);
			}
			if (CollectionUtils.isNotEmpty(scLoa.getScQtDeductions())) {
				this.getManagedBean().setSelectedScQtDeduction(
						scLoa.getScQtDeductions().get(0));
			}

			this.getManagedBean().setProdscQtDoc(resDTO.getProductDoc());

			TemplateSpecBean bean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			bean.setSpecMaster(this.getManagedBean().getProdscQtDoc()
					.getTplSpecMaster());

			this.getManagedBean().setProdTemplateSpecBean(bean);

			bean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
					.getSmSupplier());
			if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.isBrandNew()) {

				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(bean);
				this.getManagedBean()
						.getProdscQtDoc()
						.setTplSpecMaster(
								this.getManagedBean().getProdTemplateSpecBean()
										.getSpecMaster());
			}

			this.getManagedBean().setServicescQtDoc(resDTO.getServiceDoc());

			TemplateSpecBean serviceBean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			serviceBean.setSpecMaster(this.getManagedBean().getServicescQtDoc()
					.getTplSpecMaster());
			serviceBean.setSupplier(this.getManagedBean().getScLoa()
					.getScLoiLoa().getSmSupplier());
			this.getManagedBean().setSpecTemplateSpecBean(serviceBean);

			if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.isBrandNew()) {

				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(serviceBean);
				this.getManagedBean()
						.getServicescQtDoc()
						.setTplSpecMaster(
								this.getManagedBean().getSpecTemplateSpecBean()
										.getSpecMaster());
			}
			constructStartNEndMonth();
			// Display alert pop up if loa amount is > 5 lacs & supplier not
			// register with gst
			checkForGSTAlertPopup(this.getManagedBean()
					.getSpecTemplateSpecBean().getSpecMaster());
			getLoaReports();
			
			// #11134 - start
			// Remarks
			scLoa.setScLoaRemarks(initializeRemarks(scLoa, scLoa.getCurrentStatus(), scLoa.getScLoaRemarks(),
					ScLoaRemark.class));
			// #11134 - end
			
			// Bug #35078@Syazani -- 22/09/2023 -- Start
			if (isQtAudit()) {
				this.getManagedBean().setDownloadLOADisabled(false);
				this.getManagedBean().setReadOnlyLOADownload(false);
			} else {
				this.getManagedBean().setDownloadLOADisabled(!this.getManagedBean().getEnableDownloadAllDocuments());
				this.getManagedBean().setReadOnlyLOADownload(this.getManagedBean().isReadOnly());
			}
			// Bug #35078@Syazani -- 22/09/2023 -- End
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							QTConstants.SC_DIRECT_LOA_SUPPLIER_MAIN_PAGE);

		} catch (Exception e) {
			handleException(e);
		} finally {
			// qtAwardSO = null;
		}
	}

	/**
	 * @param bankAccNo
	 * @return
	 */
	/*
	private Long getBankByAccountNo(String bankAccNo) {
		if (StringUtils.isNotEmpty(bankAccNo)) {
			List<SmSupplierBank> bankAccounts = this.getManagedBean()
					.getBankList();
			if (CollectionUtils.isNotEmpty(bankAccounts)) {
				for (SmSupplierBank bank : bankAccounts) {
					if (bankAccNo.equalsIgnoreCase(bank.getAccountNo())) {
						return bank.getSupplierBankId();
					}
				}
			}
		}
		return null;
	}
	*/

	/**
	 * Pre render page navigation to loa supplier page based on task id
	 * 
	 * @return void
	 */
	public void preRenderSupplierView() {
		debugLogger("preRenderSupplierView");

		try {

			SCAwardSupplierDataType awardDateType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
			Long sourceId = Long.valueOf(awardDateType.getSourceId());
			Long loaId = Long.valueOf(awardDateType.getAwardId());

			getManagedBean().setSourceId(sourceId);
			getManagedBean().setPayLoad(awardDateType);

			getSupplierDeatilsByLoaId(loaId);

			this.getManagedBean().setValidatewitness(Boolean.TRUE); //#17376-144

		} catch (Exception e) {
			handleException(e);
		}
	}

	/**
	 * Freq schedule validation.
	 * 
	 * @param scQtSchedule
	 *            the sc qt schedule
	 */
	private void freqScheduleValidation(ScQtSchedule scQtSchedule) {
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Date startDate = null;
		Date endDate = null;
		//#25022
		if (scQtSchedule.getStartMonth() == null
				|| scQtSchedule.getStartMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getStartYear() == null
				|| scQtSchedule.getStartYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndMonth() == null
				|| scQtSchedule.getEndMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndYear() == null
				|| scQtSchedule.getEndYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}

		ScLoa loa = getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();

		Calendar effDate = Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();

		List<BigDecimal> scheduleNo = new ArrayList<BigDecimal>();
		for (ScQtSchedule schedule : loa.getScQtSchedules()) {
			if (scheduleNo.contains(schedule.getScheduleNo())) {
				throw new EpException(MessageConst.ERROR_760319);
			} else {
				scheduleNo.add(schedule.getScheduleNo());
			}
		}
		
		if (scQtSchedule.getStartYear() != null
				&& scQtSchedule.getStartMonth() != null) {
			Calendar strtDate = Calendar.getInstance();
			strtDate.clear();
			strtDate.set(Integer.parseInt(scQtSchedule.getStartYear()),
					Integer.parseInt(scQtSchedule.getStartMonth()) - 1, 1, 0, 0);

			startDate = strtDate.getTime();

		}

		if (scQtSchedule.getEndYear() != null
				&& scQtSchedule.getEndMonth() != null) {
			Calendar enDate = Calendar.getInstance();
			enDate.clear();
			enDate.set(Integer.valueOf(scQtSchedule.getEndYear()),
					Integer.valueOf(scQtSchedule.getEndMonth()), 0, 0, 0);
			endDate = enDate.getTime();
		}

		if (startDate != null && endDate != null) {
			if (startDate.after(endDate)) {
				throw new EpException(MessageConst.ERROR_760345);
			}

			if (endDate.before(startDate)) {
				throw new EpException(MessageConst.ERROR_760346);
			}

			if (!((startDate.after(contractEffDate) || startDate
					.equals(contractEffDate)) && startDate
					.before(contractExpDate))) {
				throw new EpException(MessageConst.ERROR_760347);
			}

			if (!(endDate.after(contractEffDate) && (endDate
					.before(contractExpDate) || endDate.equals(contractExpDate)))) {
				throw new EpException(MessageConst.ERROR_760348);
			}
		}
	}

	/**
	 * @ param Date date
	 * 
	 * @return days of month
	 */
	private static String getDays(Date date) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date);
		String days = String.valueOf(c1.get(Calendar.DAY_OF_MONTH));
		return days;
	}

	/**
	 * Deduction type change. Deduction Clause will show when deduction type
	 * changes
	 * 
	 * @param scQtDeduction
	 *            the sc qt deduction
	 */
	public void deductionTypeChange(ScQtDeduction scQtDeduction) {
		
		debugLogger("deductionTypeChange");
			Long parameterId = scQtDeduction.getDeductionType().getParameterId();
			if (null != parameterId) {
				scQtDeduction.setDeductionType(retrieveParameterById(parameterId));
				getClausesTitle(scQtDeduction);
				scQtDeduction.setScQtDeductionClauses(null);
				getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			} else {
				scQtDeduction.setDeductionType(new PmParameter());
				getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			}
	}

	/**
	 * Gets the clauses title.
	 * 
	 * @param deduction
	 *            the deduction
	 * @return the clauses title
	 */
	private void getClausesTitle(ScQtDeduction deduction) {
		StringBuilder title = new StringBuilder();
		title.append("Title");
		title.append(":");
		title.append(" ");
		if (deduction != null && deduction.getDeductionType() != null) {
			title.append(getParameterDesc(deduction.getDeductionType()));
			title.append(">");
			title.append(" ");
		}

		Locale locale = new FacesScopes().getUserLocale();
		title.append(ResourceBundleUtils.getMessage("lbl.sc.deduction.clauses",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale));
		getManagedBean().setClausesTitle(title.toString());
	}

	/**
	 * Adds the deduction payment in deduction payment.
	 */
	public void addDeductionPaymentAction() {
		debugLogger("addDeductionPaymentAction");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();

		if (scQtDeductions == null) {
			scQtDeductions = new ArrayList<ScQtDeduction>();

			scLoa.setScQtDeductions(scQtDeductions);
		}

		ScQtDeduction scQtDeduction = new ScQtDeduction();
		scQtDeduction.setScLoa(scLoa);
		scQtDeduction.setScQt(this.getManagedBean().getScQt());
		scQtDeduction.setDeductionType(new PmParameter());
		setUserDetails(scQtDeduction);
		scQtDeductions.add(scQtDeduction);
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Adds the deduction clause in deduction payment.
	 */
	public void addDeductionClauseAction() {
		debugLogger("addDeductionClauseAction");

		ScQtDeduction selectedScQtDeduction = this.getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> scQtDeductionClauses = selectedScQtDeduction
				.getScQtDeductionClauses();

		if (scQtDeductionClauses == null) {
			scQtDeductionClauses = new ArrayList<ScQtDeductionClause>();

			selectedScQtDeduction.setScQtDeductionClauses(scQtDeductionClauses);
		}

		ScQtDeductionClause scQtDeductionClause = new ScQtDeductionClause();
		scQtDeductionClause.setScQtDeduction(selectedScQtDeduction);
		scQtDeductionClauses.add(scQtDeductionClause);
		setUserDetails(scQtDeductionClause);
	}

	/**
	 * Delete deduction payment deduction payment.
	 */
	public void deleteDeductionPaymentAction() {
		debugLogger("deleteDeductionPaymentAction");
		if (getManagedBean().getScLoa().getScQtDeductions() != null
				&& getManagedBean().getSelectedScQtDeduction() != null) {
			getManagedBean().getScLoa().getScQtDeductions()
					.remove(getManagedBean().getSelectedScQtDeduction());
		}
		getManagedBean().setSelectedScQtDeduction(null);
	}

	/**
	 * Delete deduction clause.
	 */
	public void deleteDeductionClause() {
		ScQtDeduction scQtDeduction = getManagedBean()
				.getSelectedScQtDeduction();
		List<ScQtDeductionClause> clauses = getManagedBean()
				.getSelectedDeductionClauses();
		if (clauses != null && clauses.size() > 0) {
			deleteRowsAction(scQtDeduction.getScQtDeductionClauses(), clauses);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
		getManagedBean()
		.setSelectedDeductionClauses(null);
	}
	
	/**
	 * Handle file upload.
	 *
	 * @param event the event
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		ScLoa scLoa = this.getManagedBean().getScLoa();
		if (event.getFile() != null) {
			String attachmentDocType = getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;

			if (ParameterConst.DOCTYPE_LETTER_OF_AWARD
					.equals(attachmentDocType)) {
				
				ScLoaAttachment scLoaAttachment = scLoa.getScLoaAttachment();
				if (null == scLoaAttachment) {
					scLoaAttachment = new ScLoaAttachment();
					scLoaAttachment.setScLoa(scLoa);

					scLoa.setScLoaAttachment(scLoaAttachment);
				}

				if(StringUtils.isBlank(scLoa.getLoaNo())){
					// Generate Loa Temp No.
					scLoaAttachment.setDocNo(getQtAwardSO().generateLoaTempNo());	
				} else{
					scLoaAttachment.setDocNo(scLoa.getLoaNo());	
				}

				scLoaAttachment.setScLoa(scLoa);
				scAttachment = scLoaAttachment;

			} else {
				ScLoaReport selectedScLoaReport = this.getManagedBean()
						.getSelectedScLoaReport();
				ScLoaReportAttachment scLoaReportAttachment = selectedScLoaReport
						.getScLoaReportAttachment();
				if (null == scLoaReportAttachment) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(selectedScLoaReport);

					selectedScLoaReport
							.setScLoaReportAttachment(scLoaReportAttachment);
				}
				
				if (StringUtils.isBlank(scLoa.getLoaNo())) {
					// Generate Loa Temporary No.
					if(null == getManagedBean().getTempLoaNo()){
						getManagedBean().setTempLoaNo(getQtAwardSO().generateLoaTempNo());
						scLoaReportAttachment.setDocNo(getManagedBean().getTempLoaNo());
					} else{
						scLoaReportAttachment.setDocNo(getManagedBean().getTempLoaNo());
					}
					
				} else{
					scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
				}
				scLoaReportAttachment.setScLoaReport(selectedScLoaReport);
				scAttachment = scLoaReportAttachment;
			}

			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setTempFilePath(getActualFilePath(scAttachment));
		}
	}

	/**
	 * Handle branch details changed. shows the supplier address details.
	 */
	public void handleBranchDetailsChanged() {
		debugLogger("handleBranchDetailsChanged");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		Long selectedSupplierBranchCode = scLoa.getSupplierBranchCode();
		Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();
		
		String BranchName = getSupplierBranchNameByBranchCode(selectedSupplierBranchCode);
		if (selectedSupplierBranchCode == null
				|| SourcingConstants.SUPPLIER_BRANCH_HQ.equals(BranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl()
					.getSmCompanyBasic();

			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");

					scLoa.setSupplierBranch(SourcingConstants.SUPPLIER_BRANCH_HQ);
					
					ScAddress supplierAddress = createOrUpdateScAddress(
														scLoa.getScSupplierAddress(),
														smCompanyAddress.getSmAddress(), 
														supplierHQPersonnel, smCompanyBasic);
					scLoa.setScSupplierAddress(supplierAddress);
					// Get gst info
					SupplierGstResDTO resDTO = getGSTInfo(supplierId, null);
					setGSTInfo(resDTO, scLoa);
					break;
				}
			}
		} else {
			List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
					.getBranchsList();

			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchCode().equals(
						selectedSupplierBranchCode)) {
					log.info("found branch address");

					scLoa.setSupplierBranch(supplierBranch.getBranchName());
					
					SmCompanyBasic smCompanyBasic = null;
					SmPersonnel smPersonnel = supplierBranch.getSmPersonnel();
					if (smPersonnel != null) {
						if (smPersonnel.getSmAppl() != null) {
							smCompanyBasic = smPersonnel.getSmAppl()
												.getSmCompanyBasic();
						}
					}
					ScAddress supplierAddress = createOrUpdateScAddress(
													scLoa.getScSupplierAddress(),
													supplierBranch.getSmAddress(), 
													supplierBranch.getSmPersonnel(), 
													smCompanyBasic);
					scLoa.setScSupplierAddress(supplierAddress);
					
					// Get gst info
					SupplierGstResDTO resDTO = getGSTInfo(supplierId,supplierBranch.getBranchCode());
					setGSTInfo(resDTO, scLoa);
					break;
				}
			}
		}
	}

	/**
	 * Handle bank details changed. shows the supplier address details for
	 * selected branch and bank.
	 */
	public void handleBankDetailsChanged() {
		debugLogger("handleBankDetailsChanged");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		//Bug #33674@Syazani -- Start 08/09/2022
		Long supplierBankId = this.getManagedBean().getSupplierBankId();

		// if (scLoa.getBankAccNo() != null) {
		if (supplierBankId != null) {
			List<SmSupplierBank> bankList = this.getManagedBean().getBankList();

			for (SmSupplierBank supplierBank : bankList) {
				// if (supplierBank.getAccountNo().equals(scLoa.getBankAccNo())) {
				if (supplierBank.getSupplierBankId().equals(supplierBankId)) {
					// Bug #33674@Syazani -- End 08/09/2022
					log.info("found supplier bank");

					scLoa.setBankAccNo(supplierBank.getAccountNo());
					scLoa.setBankName(supplierBank.getPmFinancialOrg()
							.getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setScBankAddress(createOrUpdateScAddress(
											scLoa.getScBankAddress(),
											supplierBank.getSmAddress(), null, null));
					
					scLoa.getScLoiLoa().getSmSupplier();

					break;
				}
			}
		}
	}

	/**
	 * Validate loa details.
	 * 
	 * @param scLoa
	 *            the sc loa
	 * @param outcome
	 *            the outcome
	 */
	private void validateLOADetails(ScLoa scLoa, String outcome) {
		Long statusId = scLoa.getPmStatus().getStatusId().longValue();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			// validate questions and item code
			TplSpecMaster producSpecMaster = this.getManagedBean().getProdscQtDoc().getTplSpecMaster();
			TplSpecMaster serviceSpecMaster = this.getManagedBean().getServicescQtDoc().getTplSpecMaster();
			int specQuestions = 0;
			if (CollectionUtils.isNotEmpty(producSpecMaster.getTplSpecQuestions())) {
				specQuestions = producSpecMaster.getTplSpecQuestions().size();
			}
			if (CollectionUtils.isNotEmpty(serviceSpecMaster.getTplSpecQuestions())) {
				specQuestions += serviceSpecMaster.getTplSpecQuestions().size();
			}
			if (specQuestions < 1) {
				throw new EpException(MessageConst.ERROR_760354);
			} else{
				if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
					if (CollectionUtils.isNotEmpty(producSpecMaster.getTplSpecQuestions())) {
						for (TplSpecQuestion productQuestion : producSpecMaster.getTplSpecQuestions()) {
							if(null == productQuestion.getCmItem() && TemplateSpecConst.QUESTION_TYPE_ITEM.equals(productQuestion.getQuestionType())){
								throw new EpException(MessageConst.ERROR_760445, new String[] {productQuestion.getClItem()});
							}
						}
					}

					if (CollectionUtils.isNotEmpty(serviceSpecMaster.getTplSpecQuestions())) {
						for (TplSpecQuestion serviceQuestion : serviceSpecMaster.getTplSpecQuestions()) {
							if(null == serviceQuestion.getCmItem() && TemplateSpecConst.QUESTION_TYPE_ITEM.equals(serviceQuestion.getQuestionType())){
								throw new EpException(MessageConst.ERROR_760445, new String[] {serviceQuestion.getClItem()});
							}
						}
					}
				}
			}
			// validate terms and conditions 
			// Remove declaration side menu LOA CR
			/*if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}*/
			
			// validate repo attachements
			validateRepoAttachment(scLoa);
			validateDuplicateItems(this.getManagedBean().getProdscQtDoc(),this.getManagedBean().getServicescQtDoc());
			
		}

		// #36757 Start Vinod
		if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)
				|| BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			validateTierStandardUnitPrice();
		}
		// #36757 End Vinod

		// #11134 - Start
		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				&& BpmOutcomeConst.BPM_REJECT.equals(outcome)) {

			boolean remarksEmpty = false;

			// check if remark is empty
			List<ScLoaRemark> scLoaRemarks = scLoa.getScLoaRemarks();
			
			if (CollectionUtils.isNotEmpty(scLoaRemarks)) {
				for (ScLoaRemark each : scLoaRemarks) {
					
					if (StringUtils.isEmpty(each.getRemark())) {
						remarksEmpty = true;
						
						break;
					}
				}
			} else {
				remarksEmpty = true;
			}

			// throw message
			if (remarksEmpty) {
				throw new EpException(MessageConst.ERROR_716024);
				// Remark is mandatory for rejection.
			}
		}
		// #11134 -end

		// validation for fulfillment schedule and progressivepayment
		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome)
				|| BpmOutcomeConst.BPM_SAVE.equals(outcome)) {
			// check whether loa from s1 or s2.
			// For s2
			if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome) && scLoa.getScLoiLoa().getPmOrgProfile() == null) {
				throw new EpException(MessageConst.ERROR_100022, new String[] {
						ResourceBundleUtils.getMessage("lbl.sc.ptj",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale()), "" });
			}
			
			//Bug #24061
			if(scLoa.getScLoiLoa() != null){
			if(ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode().getParameterCode())){
				if (scLoa.getScQtProgPayments() != null
						&& scLoa.getScQtProgPayments().size() > 0) {
					for (ScQtProgressivePayment progressivePayment : scLoa
							.getScQtProgPayments()) {
						progressivePayValidation(progressivePayment);
					}
				}
	
				if (scLoa.getScQtSchedules() != null
						&& scLoa.getScQtSchedules().size() > 0) {
					for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
						freqScheduleValidation(scQtSchedule);
					}
				}
			}
			}

			// validate deduction payamment for Others
			validateDeductionPaymentDtls(scLoa);
			
			// validate repo attachements
			validateRepoAttachment(scLoa);
		}

	}
	

	/**
	 * Validate deduction payment dtls.
	 * 
	 * @param scLoa
	 *            the sc loa
	 */
	private void validateDeductionPaymentDtls(ScLoa scLoa) {
		
		validateDeductionPaymentDtls(scLoa.getScQtDeductions());
	}
	
	/**
	 * Validate supplier details
	 * 
	 * @param scLoa
	 *          the scLoa
	 */	
	private void validateSupplierDetails(ScLoa scLoa) {
		Locale locale = new FacesScopes().getUserLocale();
		
		if (StringUtils.isBlank(
				scLoa.getScLoiLoa().getSupplierName())) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.supplier.name", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}
		if (StringUtils.isBlank(
				scLoa.getScLoiLoa().getMofNo())) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.mof.no", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}
		if (scLoa.getScSupplierAddress() == null) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.address", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}		
	}
	
	//20200325@asyrafhaziq _ Bug #27740 - Start
		/**
		 * Validate Loa Amount
		 * 
		 * @param scLoa
		 *          the scLoa
		 */	
		private void validateLoaAmount(ScLoa scLoa) {
			Locale locale = new FacesScopes().getUserLocale();
			if (scLoa.getLoaAmt() != null && scLoa.getLoaAmt().compareTo(BigDecimal.valueOf(0)) <= 0) {
				throw new EpException(MessageConst. ERROR_760501,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.loa.off.price", 
					 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
			}
		}
		//20200325@asyrafhaziq _ Bug #27740 - End
	
	/**
	 * Validate Report attachments
	 * 
	 * @param scLoa
	 *          the scLoa
	 */	
	private void validateLOAReportAttachments(ScLoa scLoa) {
		List<ScLoaReport> loaReports = scLoa.getScLoaReports();
		
		if (CollectionUtils.isNotEmpty(loaReports)) {
			for (ScLoaReport report : loaReports) {
				if (report.getScLoaReportAttachment() != null && 
						report.getScLoaReportAttachment().getFileName() == null) {
					this.getManagedBean().setSelectedTabIndex(1);
					throw new EpException(MessageConst.ERROR_760402);
				}
			}
		}
		
	}
	
	private void validateContractNGovtSignDate(ScLoa scLoa) {
		if(null != scLoa.getGovtSignDate()){
			if(scLoa.getContractEffDate().before(scLoa.getGovtSignDate())) {
				throw new EpException(
						MessageConst.ERROR_760468);
			}
		}
	}
	
	/**
	 * Progressive pay validation.
	 * 
	 * @param progressivePayment
	 *            the progressive payment
	 */
	private void progressivePayValidation(
			ScQtProgressivePayment progressivePayment) {
		debugLogger("progressivePayValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;
		
		//#25022
		if (progressivePayment.getStartMonth() == null
				|| progressivePayment.getStartMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getStartYear() == null
				|| progressivePayment.getStartYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getEndMonth() == null
				|| progressivePayment.getEndMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getEndYear() == null
				|| progressivePayment.getEndYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}

		ScLoa loa = getManagedBean().getScLoa();
		Calendar expDate = Calendar.getInstance();
		expDate.setTime(loa.getContractExpDate());
		expDate.add(Calendar.MONTH, 1);
		expDate.set(Calendar.DAY_OF_MONTH, 1);
		expDate.add(Calendar.DATE, -1);
		Date contractExpDate = expDate.getTime();

		Calendar effDate = Calendar.getInstance();
		effDate.setTime(loa.getContractEffDate());
		effDate.set(Calendar.DAY_OF_MONTH, 1);
		Date contractEffDate = effDate.getTime();

		if (progressivePayment.getStartYear() != null
				&& progressivePayment.getStartMonth() != null) {
			startDate = Calendar.getInstance();
			startDate.clear();
			startDate.set(Integer.valueOf(progressivePayment.getStartYear()),
					Integer.valueOf(progressivePayment.getStartMonth()) - 1, 1,
					0, 0, 0);
		}

		if (progressivePayment.getEndYear() != null
				&& progressivePayment.getEndMonth() != null) {
			endDate = Calendar.getInstance();
			endDate.clear();
			endDate.set(Integer.valueOf(progressivePayment.getEndYear()),
					Integer.valueOf(progressivePayment.getEndMonth()) - 1,
					Integer.valueOf(getDays(contractExpDate)), 0, 0, 0);
		}

		if (startDate != null && endDate != null) {
			if (startDate.after(endDate)) {
				throw new EpException(MessageConst.ERROR_760345);
			}

			if (endDate.before(startDate)) {
				throw new EpException(MessageConst.ERROR_760346);
			}

			if (!(startDate.getTime().after(contractEffDate)
					&& (startDate.getTime().before(contractExpDate)) || contractEffDate
						.equals(startDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760347);
			}

			if (!(endDate.getTime().after(contractEffDate)
					&& (endDate.getTime().before(contractExpDate)) || contractExpDate
						.equals(endDate.getTime()))) {
				throw new EpException(MessageConst.ERROR_760348);
			}
		}
	}

	/**
	 * Parses the loa details.
	 * 
	 * @param scLoa
	 *            the sc loa
	 * @param outcome
	 *            the outcome
	 */
	private void parseLOADetails(ScLoa scLoa, String outcome,
			DirectAwardReqDTO awardReqDTO) {
		
		if (scLoa.isBrandNew()) {
			ScLoaWorkFlowStatus currentStatus = (ScLoaWorkFlowStatus)scLoa.getCurrentStatus();
			List<ScLoaWorkFlowStatus> workFlowStatuses = new ArrayList<ScLoaWorkFlowStatus>();
			workFlowStatuses.add(currentStatus);
			scLoa.setScLoaWorkFlowStatuses(workFlowStatuses);
			
			// To avoid workflow status reset to null when got error during direct submit without save.
			List<ScLoaRemark> remarks = scLoa.getScLoaRemarks();
			if (CollectionUtils.isNotEmpty(remarks)) {
				for (ScLoaRemark remark : remarks) {
					remark.setScWorkFlowStatus(currentStatus);
				}
			}
		}

		scLoa.setIssueDate(scLoa.getContractEffDate());

		// #26474 - start
		scLoa.setContractAdmin(null);
		// #26474 - end

		// Acknowledge Date
		if(BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_SUBMIT.equals(outcome)){
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
			scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
		}

		Long statusId = scLoa.getPmStatus().getStatusId().longValue();

		if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_SUBMISSION.equals(statusId)) {
			PmUser approverUser = getManagedBean().getApproverUser();

			if (approverUser.getUserId() != null) {
				ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();

				if (scLoaApproval == null) {
					scLoaApproval = new ScLoaApproval();
					scLoaApproval.setScLoa(scLoa);
					scLoaApproval.setApprover(approverUser);

					scLoa.setScLoaApproval(scLoaApproval);
				} else {
					scLoaApproval.setApprover(approverUser);
				}
			}
		} else if (scLoa.isApprovalReq()
				&& QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
			ScLoaApproval scLoaApproval = scLoa.getScLoaApproval();
			String action = null;
			if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_APPROVED.value();
			} else if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REJECTED.value();
			} else if (BpmOutcomeConst.BPM_REVISE.equals(outcome)) {
				action = ApprovalAction.APPROVAL_REVISED.value();
			}
			if(null != action){
				scLoaApproval.setApproverAction(action);
				scLoaApproval.setApproverActionDate(new Date());
				scLoa.setScLoaApproval(scLoaApproval);
			}
		}
		
		// #29007-Start
		if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)) {
			TemplateSpecBean bean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			bean.setSpecMaster(this.getManagedBean().getProdscQtDoc()
					.getTplSpecMaster());

			this.getManagedBean().setProdTemplateSpecBean(bean);

			bean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
					.getSmSupplier());

			if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.isBrandNew()) {
				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(bean);

				this.getManagedBean()
						.getProdscQtDoc()
						.setTplSpecMaster(
								this.getManagedBean().getProdTemplateSpecBean()
										.getSpecMaster());
			}

			TemplateSpecBean serviceBean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			serviceBean.setSpecMaster(this.getManagedBean().getServicescQtDoc()
					.getTplSpecMaster());
			serviceBean.setSupplier(this.getManagedBean().getScLoa()
					.getScLoiLoa().getSmSupplier());

			this.getManagedBean().setSpecTemplateSpecBean(serviceBean);

			if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.isBrandNew()) {
				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(serviceBean);

				this.getManagedBean()
						.getServicescQtDoc()
						.setTplSpecMaster(
								this.getManagedBean().getSpecTemplateSpecBean()
										.getSpecMaster());
			}
		}
		// #29007-End
		
		TplSpecMaster producSpecMaster = this.getManagedBean().getProdscQtDoc().getTplSpecMaster();
		TplSpecMaster serviceSpecMaster = this.getManagedBean().getServicescQtDoc().getTplSpecMaster();
		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId) && 
				BpmOutcomeConst.BPM_ACCEPT.equals(outcome)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			scLoa.setBondResDueDate(new Date());
			producSpecMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			serviceSpecMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			
		} else {

			saveProgressivePaymntList();
			constructScheduleDetails();
			//Bug #24061
			// 19287#35 -start-
			if (scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getFulfilmentTypeCode() != null) {
			// 19287#35 -end-
				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scLoa.getScLoiLoa().getFulfilmentTypeCode()
						.getParameterCode())) {
					if (CollectionUtils.isNotEmpty(scLoa.getScQtSchedules())) {
						for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
							freqScheduleValidation(scQtSchedule);
						}
					}
				}
			}
			
			producSpecMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			serviceSpecMaster.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		}

		if (!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))) {
			scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
		}
		
		// copy item origin 
		serviceSpecMaster.setItemOriginCode(producSpecMaster.getItemOriginCode());

		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getScQt()
				.getScQtSupplierCriteria();
		
		if (supplierCriteria == null) {
			supplierCriteria = new ScQtSupplierCriteria();
			supplierCriteria.setScQt(scQt);
			scQt.setScQtSupplierCriteria(supplierCriteria);	
		}
		
		if (scQt.isZonal()) {
			if(supplierCriteria != null ){
				ScQtZoneCriteria qtZoneCriteria = supplierCriteria
						.getQtZoneCriterias();
				List<TplSpecZoneDtl> zoneDetails = convertTreeNodesToZoneCriteria(
						this.getManagedBean().getZoneTrees(), null);

				for (TplSpecZoneDtl tplSpecZoneDtl : zoneDetails) {

					List<TplSpecZoneLoc> specLocs = tplSpecZoneDtl
							.getTplSpecZoneLocs();
					for (TplSpecZoneLoc specZoneLoc : specLocs) {
						specZoneLoc.setTplSpecZoneDtl(tplSpecZoneDtl);
					}

					tplSpecZoneDtl.setTplSpecZone(qtZoneCriteria);

				}
				qtZoneCriteria.setTplSpecZoneDtls(zoneDetails);
				supplierCriteria.setLocationLevel(scQt.getZoneLocationLevel());
			}
		} else {
			ScQtZoneCriteria zoneCriteria = this.getManagedBean()
					.getToBedeletedSupplierZone();

			if (null != zoneCriteria && !zoneCriteria.isBrandNew()) {
				zoneCriteria.setDeleted(true);
				awardReqDTO.setToBeDeltedZoneCriteria(zoneCriteria);
			}
			if(supplierCriteria != null){
				supplierCriteria.setQtZoneCriterias(null);
			}
		}
		if(null==scQt.getScheduleType()){
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		}
		
		//Bug_29025 - Start
		if(scQt.getProcurementMode() == null){
			scQt.setProcurementMode(this.getManagedBean().getScQt().getProcurementMode());
			
			//Bug_34061, 36610 - Start
			if(this.getManagedBean().getScQt().getProcurementMode() == null){
				IParameterSO parameterSO = null;
				try {
					
					parameterSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterCode(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);
					reqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
					scQt.setProcurementMode(resDTO.getPmParameter());
				} finally {
					parameterSO = null;
				}
			}
			//Bug_34061, 36610 - End
		}
		//Bug_29025 - End
		
		scLoa.setScQt(scQt);

	}

	/**
	 * Contract type changed. shows Agency based on contract type changes.
	 */
	public void contractTypeChanged() {
		debugLogger("contractTypeChanged");

		ScLoa scLoa = getManagedBean().getScLoa();

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			if(scLoa.getContractType() !=null){
				reqDTO.setParameterId(scLoa.getContractType().getParameterId());
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				scLoa.setContractType(resDTO.getPmParameter());
			} 
			
			// to reset Contract Admin User
			if(getManagedBean().getContractTypeId() != null) {
				if (!scLoa.getContractType().getParameterId().equals(getManagedBean().getContractTypeId())){
					scLoa.setContractAdmin(new PmUser());
				}
				getManagedBean().setContractTypeId(scLoa.getContractType().getParameterId());
			} else {
				scLoa.setContractAdmin(new PmUser());
			}

		} finally {
			parameterSO = null;
		}

		String contractType = scLoa.getContractType().getParameterCode();

		if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {
			/*
			 * BUG 31723 START
			 */
			if(null!=scLoa.getContractAgency() && null!=scLoa.getContractAgency().getOrgProfileId()){
				scLoa.getScLoiLoa().setPmOrgProfile(scLoa.getContractAgency());
			}
			/*
			 * END
			 */
			scLoa.getScQt().setFulfilmentType(
					getParameterByTypeAndCode(ParameterTypeConst.FULFILMENT_TYPE,
							ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN));
		}
	}

	/**
	 * Contract agency changed. shows Contract Administrator based on agency
	 * changes.
	 */
	public void contractAgencyChanged() {
		debugLogger("contractAgencyChanged");

		IUserSO userSO;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			ScLoa scLoa = getManagedBean().getScLoa();
			String contractType = scLoa.getContractType().getParameterCode();
			List<PmUser> userList = null;

			if (scLoa.getContractAgency() != null) {
				if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());
					reqDTO.setOrgProfileId(scLoa.getContractAgency().getOrgProfileId());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();

				} else if (ParameterConst.CONTRACT_TYPE_CENTRAL
								.equals(contractType)) {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setRoleCode(RoleCode.BPK_CCA.value());
					reqDTO.setOrgProfileId(
							scLoa.getContractAgency().getOrgProfileId());

					UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);

					userList = resDTO.getUserList();
				}
			}

			getManagedBean().setContractAdminList(userList);

		} finally {
			userSO = null;
		}
	}

	/**
	 * Contract admin changed.
	 */
	public void contractAdminChanged() {
		debugLogger("contractAdminChanged");

		IUserSO userSO;
		ScLoa scLoa = getManagedBean().getScLoa();
		PmUser pmUser = scLoa.getContractAdmin();

		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(pmUser.getUserId());

			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			scLoa.setContractAdmin(resDTO.getPmUser());

		} finally {
			userSO = null;
		}

	}

	/**
	 * Contract eff date changed. calculate Completion Expire Date based on
	 * Completion Period.
	 */
	public void contractEffDateChanged() {
		debugLogger("contractEffDateChanged");

		ScLoa scLoa = getManagedBean().getScLoa();
		Date contractEffDate = scLoa.getContractEffDate();
		BigDecimal contractDuration = scLoa.getContractDuration();
		if (null == contractDuration) {
			throw new EpException(MessageConst.ERROR_760344);
		}
		Calendar contractExpDate = Calendar.getInstance();
		contractExpDate.setTime(contractEffDate);
		contractExpDate.add(Calendar.MONTH, contractDuration.intValue());
		contractExpDate.add(Calendar.DATE, -1);

		scLoa.setContractExpDate(contractExpDate.getTime());
		scLoa.setIssueDate(contractEffDate);
	}

	/**
	 * Approver changed.
	 */
	public void approverChanged() {
		debugLogger("approverChanged");

		PmUser approverUser = getManagedBean().getApproverUser();

		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(approverUser.getUserId());

			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			getManagedBean().setApproverUser(resDTO.getPmUser());

		} finally {
			userSO = null;
		}

	}

	/**
	 * Contract signer event.
	 */
	public void contractSignerEvent() {
		debugLogger("contractSignerEvent");
		ScLoa scLoa = getManagedBean().getScLoa();
		if (scLoa.getContractSigner() != null) {
			if (getManagedBean().getContractSignerList() != null
					&& getManagedBean().getContractSignerList().size() > 0) {
				List<PmUser> contractSignerList = getManagedBean()
						.getContractSignerList();
				for (PmUser user : contractSignerList) {

					if (user.getUserId().equals(scLoa.getContractSigner().getUserId())) {
						scLoa.setContractSigner(user);
						scLoa.setLoaSignerDesig(user.getDesignation());
						break;
					} else {
						getManagedBean().getScLoa().setLoaSignerDesig("");
					}
				}
			}
		}
	}
	
	/**
	 * Supplier Contract signer event.
	 */
	public void supplierContractSignerEvent() {
		debugLogger("supplierContractSignerEvent");
		PmUser supplierContractSigner = getManagedBean().getScLoa().getAckBy();
		IUserSO userSO = null;
		try {
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			if (supplierContractSigner != null) {
				if (getManagedBean().getSupplierContractSignerList() != null
						&& getManagedBean().getSupplierContractSignerList().size() > 0) {
					List<PmUser> supplierContractSignerList = getManagedBean()
							.getSupplierContractSignerList();
					for (PmUser user : supplierContractSignerList) {
						if (user.getUserId().equals(supplierContractSigner.getUserId())) {
							UserReqDTO reqDTO = new UserReqDTO();
							reqDTO.setUserId(user.getUserId());
							UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
							getManagedBean().getScLoa().setAckBy(resDTO.getPmUser());
							break;
						}
					} 
				}
			} 
		} finally {
			userSO = null;
		}
	}

	/**
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {

		IParameterSO parameterSo = null;
		try {

			if(this.getManagedBean().getScLoa()
					.getScQtSchedules()==null){
				this.getManagedBean().getScLoa().setScQtSchedules(new ArrayList<ScQtSchedule>());
			}

			List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
					.getScQtSchedules();

			parameterSo = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			// int year = Calendar.getInstance().get(Calendar.YEAR);
			ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
			docDefTypeReqDTO.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
			docDefTypeReqDTO
					.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
			ParameterResDTO docDefTypeResDTO = parameterSo
					.retrieveParameterByTypeAndCode(docDefTypeReqDTO);

			ScQtSchedule schedule = new ScQtSchedule();
			schedule.setScQt(this.getManagedBean().getScQt());
			schedule.setScLoa(this.getManagedBean().getScLoa());

			schedule.setScheduleType(docDefTypeResDTO.getPmParameter());

			scheduleList.add(schedule);

			this.getManagedBean().getScLoa().setScQtSchedules(scheduleList);
		}

		finally {
			parameterSo = null;
		}
	}

	/**
	 * Gets the year details. Shows start month and end month drop downs based
	 * on Total Frequency.
	 * 
	 * @return the year details
	 */
	public List<SelectItem> getYearDetails() {
		if (null == getManagedBean().getYearList()) {
			List<SelectItem> selectItems = new ArrayList<SelectItem>();
			FacesScopes face = new FacesScopes();
			Locale local = face.getUserLocale();
			try {
				selectItems.add(new SelectItem(null, ResourceBundleUtils
						.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								local)));
				
				for (int i = 1; i <= 12; i++) {
					selectItems.add(new SelectItem(i, getMonth(i, local)));
				}
				this.getManagedBean().setYearList(selectItems);
			} finally {
			}
		}
		return getManagedBean().getYearList();
	}

	/**
	 * Gets the month.
	 * 
	 * @param monthId
	 *            the month id
	 * @return the month
	 */
	private String getMonth(int monthId, Locale locale) {
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM", locale);
		String month = formatter.format(fullMonth.getTime());

		return month;
	}

	/**
	 * Gets the years display dtls. Shows start year and end year drop downs
	 * based on Total Frequency.
	 * 
	 * @return the years display dtls
	 */
	public List<SelectItem> getYearsDisplayDtls() {
		List<SelectItem> selectItems = new ArrayList<SelectItem>();
		ScLoa loa = getManagedBean().getScLoa();

		try {
			// int year = Calendar.getInstance().get(Calendar.YEAR);
			Calendar calendar = Calendar.getInstance();
			int effYear = 0;
			int expYear = 0;
			if (loa != null) {

				if (loa.getContractEffDate() != null) {
					calendar.setTime(loa.getContractEffDate());
					effYear = calendar.get(Calendar.YEAR);
				}

				if (loa.getContractExpDate() != null) {
					calendar.setTime(loa.getContractExpDate());
					expYear = calendar.get(Calendar.YEAR);
				}

				int noOfYears = expYear - effYear;
				selectItems.add(new SelectItem(null, ResourceBundleUtils
						.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())));

				for (int i = 0; i <= noOfYears + 1; i++) {

					if (i == 0) {
						selectItems.add(new SelectItem(effYear, String
								.valueOf(effYear)));
					}

					if (noOfYears == 0) {
						break;
					}
					effYear = effYear + 1;

					if (effYear == expYear) {
						selectItems.add(new SelectItem(effYear, String
								.valueOf(effYear)));
						break;
					}

					selectItems.add(new SelectItem(effYear, String
							.valueOf(effYear)));
				}
			}

			/*
			 * selectItems.add(new SelectItem(year, String.valueOf(year))); for
			 * (int i = 0; i < 20; i++) { year = year + 1; selectItems.add(new
			 * SelectItem(year, String.valueOf(year))); }
			 */

			this.getManagedBean().setYearsDisplayList(selectItems);
		} finally {

		}
		return getManagedBean().getYearsDisplayList();
	}

	/**
	 * Calculate schedule months. Calculating start month , start year , end
	 * month and end year to display total months.
	 * 
	 * @param schedule
	 *            the schedule
	 * @param startMonthAction
	 *            the start month action
	 * @throws ParseException
	 */
	public void calculateScheduleMonths(ScQtSchedule schedule,
			Boolean startMonthAction) throws ParseException {
		List<ScQtSchedule> freqSchedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();
		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, months = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
		endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
				.getEndMonth()) : null;
		startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
				.getStartYear()) : null;
		endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
				.getEndYear()) : null;

		if (startMonthAction != null && startMonthAction && startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if (endYear != null) {
				schedule.setEndYear(endYear.toString());
			}
		}

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			String fromDate = 01 + "/" + startMonth + "/" + startYear;
			String toDate = 01 + "/" + endMonth + "/" + endYear;
			Date startDate = sdf.parse(fromDate);
			Date endDate = sdf.parse(toDate);

			months = DateUtils.getMonths(startDate, endDate);
			schedule.setTotalMonths(new BigDecimal(months));
		} else {
			schedule.setTotalMonths(BigDecimal.ZERO);
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);
		this.getManagedBean().getScLoa().setScQtSchedules(freqSchedule);
	}


	/**
	 * Gets the deduction item type dropdown list.
	 * 
	 * @return the deduction item type dropdown list
	 */
	public SelectItem[] getDeductionItemTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT));
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(),
						ParameterConst.DEDUCTION_TYPE_TRANSACTION_FEE));

		return super.getDropdownValuesBySearchBean(
				ParameterTypeConst.DEDUCTION_TYPE, searchBean);
	}

	/**
	 * Adds the progressive details.
	 */
	public void addProgressiveDetails() {

		if (this.getManagedBean().getScLoa().getScQtProgPayments() == null) {
			this.getManagedBean().getScLoa().setScQtProgPayments(
					new ArrayList<ScQtProgressivePayment>());
		}
		List<ScQtProgressivePayment> payments = this.getManagedBean()
				.getScLoa().getScQtProgPayments();

		ScQtProgressivePayment payment = new ScQtProgressivePayment();

		setUserDetails(payment);

		payment.setScQt(this.getManagedBean().getScQt());
		payment.setScLoa(this.getManagedBean().getScLoa());
		payments.add(payment);
			
		this.getManagedBean().getScQt()
				.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
		this.getManagedBean().getScLoa().setScQtProgPayments(payments);

	}

	/**
	 * Save progressive paymnt list.
	 */
	private void saveProgressivePaymntList() {
		List<ScQtProgressivePayment> progressivePayemnt = this.getManagedBean()
				.getScLoa().getScQtProgPayments();

		List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
		if (CollectionUtils.isNotEmpty(progressivePayemnt)) {

			for (ScQtProgressivePayment payment : progressivePayemnt) {

				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/"
						+ getMonth(payment.getStartMonth()) + "/"
						+ payment.getStartYear();
				String toDate = date + "/" + getMonth(payment.getEndMonth())
						+ "/" + payment.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						payment.setFromDate(df.parse(fromdate));
						payment.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					debugLogger("Parce Exception");
				}
				getManagedBean().getScLoa().getScQtProgPayments();
				setUserDetails(payment);
				paymentList.add(payment);
			}
			getManagedBean().getScLoa().setScQtProgPayments(paymentList);

		} else {
			getManagedBean().getScLoa().setScQtProgPayments(null);
		}
	}

	/**
	 * Save progressive paymnt list.
	 */
	private void constructScheduleDetails() {
		List<ScQtSchedule> schdules = this.getManagedBean().getScLoa()
				.getScQtSchedules();

		List<ScQtSchedule> schedules = new ArrayList<ScQtSchedule>();
		if (CollectionUtils.isNotEmpty(schdules)) {

			for (ScQtSchedule schedule : schdules) {

				//SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String fromdate = 01 + "/"
						+ getMonth(schedule.getStartMonth()) + "/"
						+ schedule.getStartYear();
				String toDate = 01 + "/" + getMonth(schedule.getEndMonth())
						+ "/" + schedule.getEndYear();
				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					debugLogger("Parce Exception");
				}

				schedules.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(schedules);

		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
		}
	}

	/**
	 * Pre render page navigation to direct nego or loa
	 */
	public void preRenderDirectNegoView() {
		debugLogger("preRenderDirectNegoView");
		renewSesionObject();

		String redirectUrl = retriveLoaDetails(null, null, false);
		ScQt scQt = getManagedBean().getScQt();
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		scQt.setPmOrgProfile(pmOrgProfile);
		scQt.setOwnerOrgProfile(pmOrgProfile);
		scQt.setOrgProfileId(pmOrgProfile.getOrgProfileId());
		scQt.setOwnerOrgProfileId(pmOrgProfile.getOrgProfileId());
		this.getManagedBean()
				.getScLoa()
				.getScLoiLoa()
				.setPmOrgProfile(
						pmOrgProfile);
		initDefaultSelectedOrgType();
		populateMinistryBasedOnLoginUser();
		getApprovalUsers();
		prepareSignerInfo();
		FacesContext fc = FacesContext.getCurrentInstance();

		/* ENHANCEMENT 26474 START */
		this.getManagedBean().setSelectedPtj(pmOrgProfile);
		/* ENHANCEMENT 26474 END */

		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, redirectUrl);
	}

	/**
	 * Mof no listener. Gets supplier, branch and adress details based on mof
	 * nor.
	 * 
	 * @param mofAccount
	 *            the mof account
	 */
	public void mofNoListener(SmMofAccount mofAccount) {
		debugLogger("mofNoListener");
		ScLoa scLoa = getManagedBean().getScLoa();
		SmSupplier smSupplier = mofAccount.getSmSupplier();
		scLoa.getScLoiLoa()
				.setSmSupplier(smSupplier);
		scLoa.getScLoiLoa()
				.setMofNo(mofAccount.getMofNo());
		scLoa.getScLoiLoa()
				.setSupplierName(smSupplier.getCompanyName());
		scLoa.getScLoiLoa()
				.setEpNo(smSupplier.getEpNo());
		this.getManagedBean().getProdTemplateSpecBean()
				.setSupplier(smSupplier);

		this.getManagedBean().getSpecTemplateSpecBean()
				.setSupplier(smSupplier);
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());

		DirectAwardResDTO resDTO = getQtDirectAwardSO()
				.retrieveSmPersonnelDtlsBySupplierId(reqDTO);

		scLoa.getScLoiLoa()
				.setSmSupplier(resDTO.getSmSupplier());
		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			handleBranchDetailsChanged();
		}
		
		ScQt scQt = getManagedBean().getScQt();
		if(scQt != null) {
			ScLoiLoa scLoiLoa = getSupplierLoiLoaByDocId(smSupplier.getSupplierId(), scQt.getQtId());
			if(scLoiLoa != null) {
				scLoa.setScLoiLoa(scLoiLoa);
			}
		}
	}

	/**
	 * Supplier listener. Gets mof no, branch and adress details based on mof
	 * nor.
	 * 
	 * @param suSmSupplier
	 *            the su sm supplier
	 */
	public void supplierListener(SmSupplier suSmSupplier) {
		debugLogger("supplierListener");

		//Bug #35107 - start
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		reqDTO.setSupplierId(suSmSupplier.getSupplierId());

		DirectAwardResDTO resDTO = getQtDirectAwardSO().retrieveSmPersonnelDtlsBySupplierId(reqDTO);
		
		if(resDTO.getMofNo() == null){
			throw new EpException(MessageConst.ERROR_760552);
		}
		//Bug #35107 - end
		
		ScLoa scLoa = getManagedBean().getScLoa();
		scLoa.getScLoiLoa().setSmSupplier(suSmSupplier);
		scLoa.getScLoiLoa()
				.setSupplierName(suSmSupplier.getCompanyName());
		scLoa.getScLoiLoa()
				.setEpNo(suSmSupplier.getEpNo());

		this.getManagedBean().getProdTemplateSpecBean()
				.setSupplier(suSmSupplier);
		this.getManagedBean().getSpecTemplateSpecBean()
				.setSupplier(suSmSupplier);

		scLoa.getScLoiLoa()
				.setSmSupplier(resDTO.getSmSupplier());
		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			handleBranchDetailsChanged();
		} else{
			scLoa.setSupplierBranch(null);
			scLoa.setScSupplierAddress(null);
		}
		// mof nor
		scLoa.getScLoiLoa().setMofNo(resDTO.getMofNo());
		
		ScQt scQt = getManagedBean().getScQt();
		if(scQt != null && scQt.getQtNo() != null &&
				!scQt.getQtNo().startsWith(ParameterConst.DOCTYPE_QUOTATION)) {
			ScLoiLoa scLoiLoa = getSupplierLoiLoaByDocId(suSmSupplier.getSupplierId(), scQt.getQtId());
			if(scLoiLoa != null) {
				scLoa.setScLoiLoa(scLoiLoa);
			}
		}

	}
			
	public SelectItem[] getSelectRadioButtonCodeValues(String type) {		
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		List<String> qtTypes = new ArrayList<String>();
		qtTypes.add(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		
		searchBean.getDataFilterBeans().add(
			new SearchFilterBean("pmParameter.parameterCode",
			SearchOperator.OPERATOR_IN.value(), qtTypes));
		
		return super.getRadioButtonCodeValuesBySearchBean(type,searchBean);
	}
	
	/**
	 * On QT Selected. If supplier is also selected, retrieve exist ScLoiLoa for 
	 * selected QT and selected Supplier if exist.
	 * 
	 * @param suSmSupplier
	 *            the su sm supplier
	 */
	public void onQTSelected(ScQt scQt) {
		debugLogger("onQTSelected");
		ScLoa loa  = getManagedBean().getScLoa();
		getManagedBean().setZoneTrees(null);
		populateMinistryBasedOnLoginUser();
		
		DirectAwardReqDTO awardReqDTO = new DirectAwardReqDTO();
		awardReqDTO.setDocType(ParameterConst.DOCTYPE_DIRECT_NEGO);
		awardReqDTO.setSourceId(scQt.getQtId());
		ScLoiLoa scLoiLoa = getQtDirectAwardSO().retrieveLoiLoaByDocIdNDocType(awardReqDTO).getScLoiLoa();
		if(scLoiLoa != null) {
			loa.setScLoiLoa(scLoiLoa);
			
			supplierListener(scLoiLoa.getSmSupplier());
			
			if(scQt.getFulfilmentType() != null) {
				scLoiLoa.setFulfilmentTypeCode(scQt.getFulfilmentType());
			}
		}
		loa.setScQt(scQt);
		loa.setEstimatedPrice(scQt.getExpectedIntendedPrice());
		loa.setProcurementMode(scQt.getProcurementMode());
		loa.setProcurementTypeCatId(scQt.getProcurementTypeCatId());
		
		if (loa.getProcurementMode() == null) {
			loa.setProcurementMode(new PmParameter());
		}
		
		if (loa.getProcurementTypeCatId() == null) {
			loa.setProcurementTypeCatId(new PmParameter());
		}
		
		PmParameter qtTypeCode = null;
		if (StringUtils.isNotBlank(scQt.getQtType())) {
			qtTypeCode = getParameterByTypeAndCode(
					ParameterTypeConst.QUOTATION_TENDER_TYPE, scQt.getQtType());	
		}
		if (qtTypeCode == null) {
			qtTypeCode = new PmParameter();
		}
		loa.setQtType(qtTypeCode);
		
		//get loa report due to fulfilment change
		getLoaReports();
		
		// update existing scQts  
		awardReqDTO.setScQt(scQt);
		awardReqDTO.setScLoa(loa);
		DirectAwardResDTO awardResDTO = getQtDirectAwardSO().getScQtDocs(awardReqDTO);
		
		// Product
		if(awardResDTO.getProductDoc() != null){
			setUserDetails(awardResDTO.getProductDoc());
			this.getManagedBean().setProdscQtDoc(awardResDTO.getProductDoc());
			TemplateSpecBean bean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			bean.setSpecMaster(this.getManagedBean().getProdscQtDoc()
					.getTplSpecMaster());
			this.getManagedBean().setProdTemplateSpecBean(bean);
			bean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
					.getSmSupplier());
			if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.isBrandNew()) {

				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(bean);
				this.getManagedBean()
				.getProdscQtDoc()
				.setTplSpecMaster(
						this.getManagedBean().getProdTemplateSpecBean()
						.getSpecMaster());
			}
		}
		// Service
		if(awardResDTO.getServiceDoc() != null){
			setUserDetails(awardResDTO.getServiceDoc());
			this.getManagedBean().setServicescQtDoc(awardResDTO.getServiceDoc());

			TemplateSpecBean serviceBean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			serviceBean.setSpecMaster(this.getManagedBean().getServicescQtDoc()
					.getTplSpecMaster());
			serviceBean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
					.getSmSupplier());
			this.getManagedBean().setSpecTemplateSpecBean(serviceBean);

			if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.isBrandNew()) {

				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				backingBean.retrieveTemplateSpecMaster(serviceBean);
				this.getManagedBean()
				.getServicescQtDoc()
				.setTplSpecMaster(
						this.getManagedBean().getSpecTemplateSpecBean()
						.getSpecMaster());
			}
		}
		setUserDetails(scQt);
		this.getManagedBean().setScQt(scQt);
		this.getManagedBean().setApproverUser(new PmUser());
		if (scQt.isZonal()) {
			List<TplSpecZoneDtl> zoneDetails = awardResDTO.getSpecZoneDetails();
			constructTreeNodeForZones(zoneDetails);
			getManagedBean().setLocalLevelCode(scQt.getZoneLocationLevel().getParameterCode());
		}
	}

	/**
	 * Ptj dropdown list.
	 * 
	 * @return the list
	 */
	public List<PmOrgProfile> ptjDropdownList() {
		debugLogger("ptjDropdownList");
		// PmUser user = new FacesScopes().getUserSessionBean();
		Long parentOrgProfileId = this.getManagedBean().getScQt()
				.getMinistryOrgProfile().getOrgProfileId();
		if (null != parentOrgProfileId) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(parentOrgProfileId);
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			// reqDTO.setParentOrgProfileId(parentOrgProfileId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> pmOrgProfiles = resDTO.getOrgProfileList();
			this.getManagedBean().setPtjDropdownList(pmOrgProfiles);
			return pmOrgProfiles;
		}
		return null;
	}

	/**
	 * Ptj dropdown list.
	 * 
	 * @return the list
	 */
	public PmOrgProfile getMinistryOrgProfile(PmOrgProfile orgProfile) {
		debugLogger("ministry drop downs");

		if (null != orgProfile) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

			reqDTO.setOrgProfileId(this.getManagedBean().getScQt()
					.getPmOrgProfile().getOrgProfileId());
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveMinistryByOrgProfileId(reqDTO);

			return resDTO.getPmOrgProfile();
		}
		return null;
	}

	/**
	 * construct the ConditionFields based on MinistryProfile
	 * 
	 * @return orgProfileId
	 */
	public String constuctConditionFields() {
		StringBuilder conditionField = null;
		PmOrgProfile ministryOrg = this.getManagedBean().getScQt()
									   .getPmOrgProfile();
		if (null != ministryOrg) {
			conditionField = new StringBuilder();
			Long ministryOrgProfileId = ministryOrg.getOrgProfileId();
			if (ParameterConst.ORG_TYPE_MINISTRY.equalsIgnoreCase(ministryOrg
					.getOrgTypeCode().getParameterCode())) {

				conditionField.append(OrgProfileUtil
						.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
						+ CARET_SYMBOL + ministryOrgProfileId);
			} else {
				conditionField.append("orgProfileId" + CARET_SYMBOL + ministryOrgProfileId);
			}
			conditionField.append(PIPE_SYMBOL + "recordStatus" + CARET_SYMBOL + 
								  DbValueConst.RECORD_STATUS_ACTIVE);
		}
		if (conditionField != null) {
			return conditionField.toString();
		}
		
		return null;
	}
	
	/**
	 * construct the ConditionFields based on MinistryProfile
	 * 
	 * @return orgProfileId
	 */
	public String constuctQTConditionFields() {

		StringBuilder sb = new StringBuilder();
		sb.append(ParameterConst.DOCTYPE_QUOTATION);
		String qtNoPrefix = sb.toString();
		
		StringBuilder conditionFieldSb = new StringBuilder();
		conditionFieldSb.append("qtNo^");
		conditionFieldSb.append(qtNoPrefix);
		conditionFieldSb.append("^");
		conditionFieldSb.append(SearchOperator.OPERATOR_NOT_LIKE.value());
		
		// Filter By - pmStatus
		conditionFieldSb.append("|");
		conditionFieldSb.append("scQtStatus.docType^");
		conditionFieldSb.append(ParameterConst.DOCTYPE_QUOTATION);
		conditionFieldSb.append("^");
		conditionFieldSb.append(SearchOperator.OPERATOR_EQUALS.value());
		conditionFieldSb.append("|");
		conditionFieldSb.append("scQtStatus.current^");
		conditionFieldSb.append(true);
		conditionFieldSb.append("^");
		conditionFieldSb.append(SearchOperator.OPERATOR_EQUALS.value());
		conditionFieldSb.append("|");
		conditionFieldSb.append("scQtStatus.pmStatus.statusId^");
		
		List<Long> statusIds = new ArrayList<Long>();
		statusIds.add(QTStatusConst.QT_AWARDED);
		statusIds.add(QTStatusConst.QT_CANCELLED);
		conditionFieldSb.append(StringUtils.join(statusIds, ","));
		conditionFieldSb.append("^");
		conditionFieldSb.append(SearchOperator.OPERATOR_NOT_IN.value());
		
		PmOrgProfile ministryOrg = this.getManagedBean().getScQt()
				.getPmOrgProfile();
		if (null != ministryOrg) {
			Long ministryOrgProfileId = ministryOrg.getOrgProfileId();
			
			// Filter By - PTJ
			conditionFieldSb.append("|");
			conditionFieldSb.append("pmOrgProfile.orgProfileId^");
			conditionFieldSb.append(ministryOrgProfileId);
		}
		
		return conditionFieldSb.toString();
	}

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            (String type)
	 * @returnList<PmParameterDesc>
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Gets the local goods radio values.display Local goods (Local/Import)
	 * radio values in template spec.
	 * 
	 * @param itemType
	 *            (String itemType)
	 * @return SelectItem[].
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());

					}
				}
			}
		} finally {
		}
		return items;
	}

	/**
	 * Get item type values from pmParameter
	 * 
	 * @return list of dropdown item values.
	 */
	public SelectItem[] getItemTypeValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		String recommend = ParameterConst.ITEM_TYPE_PRODUCT_AND_SERVICE;

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_EQUALS.value(), recommend));

		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_ITEM_TYPE, searchBean);
		return items;
	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * Gets the pm digi sign.
	 * 
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 * 
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		// #36466 -start-
		StringBuilder sb = new StringBuilder();

		if (getManagedBean().getScLoa() != null) {
			sb.append(getManagedBean().getScLoa().getLoaNo());
		}

		sb.append(new FacesScopes().getUserSessionBean().getUserId());

		textBeforeSign = sb.toString();
		// #36466 -end-

		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTDirectAwardSO getQtDirectAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDirectAwardSO.class);
	}
	
	/**
	 * Gets the mof account by supplier id.
	 *
	 * @param supplierId the supplier id
	 * @return the mof account by supplier id
	 */
	public SmMofAccount getMofAccountBySupplierId(Long supplierId) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
		MofAccountReqDTO accountReqDTO = new MofAccountReqDTO();
		accountReqDTO.setSupplierId(supplierId);
		MofAccountResDTO mofAccountResDTO = mofAccountSO
				.retrieveMofNoBySupplierId(accountReqDTO);
		return mofAccountResDTO.getMofAccount();
	}
	
	/**
	 * Gets the mof cert by mof account id.
	 *
	 * @param mofAccountId the mofAccountId id
	 * @return the mof cert by mof account id
	 */
	public SmMofCert getMofCertBySupplierId(Long mofAccountId) {
		IVirtualCertSO virtualCertSO = (IVirtualCertSO) BusinessDelegate
				.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IVirtualCertSO.class);
		VirtualCertReqDTO certReqDTO = new VirtualCertReqDTO();
		certReqDTO.setMofAccountId(mofAccountId);
		VirtualCertResDTO certResDTO = virtualCertSO
				.retrieveLatestCertByAccountId(certReqDTO);
		return certResDTO.getSmMofCert();
	}

	/**
	 * Close listener.
	 */
	public void closeListener() {
		getManagedBean().setRequired(Boolean.FALSE);
		this.getManagedBean().setZoneLocation(Boolean.FALSE);
		this.getManagedBean().setAutoUpdate(true);
	}

	/**
	 * Select zone tree node.Event handle check non panel Treenodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode)
	 */
	public void selectZoneTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(true);
			
		}
		}else{
			
		selectedTreeNode.setSelected(true);
		
		}
	}

	/**
	 * Un select zone tree node.Event handle uncheck Nonpanel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();
		List<TreeNode> childNodes = event.getTreeNode().getChildren();
		if(null!=childNodes && childNodes.size()>0){
			
		for (int i = 0; i < childNodes.size(); i++) {
			childNodes.get(i).setSelected(false);
			
		}
		}else{
			
		selectedTreeNode.setSelected(false);
		}
	}

	/**
	 * Edits the zones.click pencil icon in general information menu navigate to
	 * ZoneLocation Popup.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index).
	 */
	public void editZones(TreeNode rootZoneLocation, int index) {
		IParameterSO parameterSo = null;
		ICountrySO countrySo = null;
		try {
			parameterSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			List<TplSpecZoneLoc> locationDTOs = getZoneLocations(rootZoneLocation);

			// TplSpecZoneDtl zone = getZone(rootZoneLocation);
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();
			if (locationLevel == null) {
				ParameterReqDTO districtReqDTO = new ParameterReqDTO();
				districtReqDTO
						.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
				districtReqDTO
						.setParameterCode(ParameterConst.LOCATION_LEVEL_DISTRICT);
				ParameterResDTO districtResDTO = parameterSo
						.retrieveParameterByTypeAndCode(districtReqDTO);

				locationLevel = districtResDTO.getPmParameter()
						.getParameterId();
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();

					break;
				}
			}

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(locationDTOs);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(locationDTOs);
			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(locationDTOs);
			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(locationDTOs);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(
							countryResDTO.getPmCountry().getCountryId(),
							states, districts, divisions, cities,
							locationLevelCode, TplSpecZoneLoc.class,
							false));
			int i = index;
			this.getManagedBean().setAutoUpdate(false);
			this.getManagedBean().setIndex(i);
			getManagedBean().setZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
			getManagedBean().setTempZoneName(
					locationDTOs.get(0).getTplSpecZoneDtl().getZoneName());
		} finally {

		}

	}

	/**
	 * Gets the zone locations. convert Treenode to TplSpecZoneLoc objects in
	 * general information.
	 * 
	 * @param zoneLocation
	 *            (TreeNode zoneLocation)
	 * @return List<TplSpecZoneLoc>
	 */
	private List<TplSpecZoneLoc> getZoneLocations(TreeNode zoneLocation) {

		List<TplSpecZoneLoc> results = new ArrayList<TplSpecZoneLoc>();
		if (zoneLocation.isLeaf()) {
			TplSpecZoneLoc locationDTO = (TplSpecZoneLoc) zoneLocation
					.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				results.addAll(getZoneLocations(childrendLocationNode));
			}
		}

		return results;
	}

	/**
	 * Select location tree node.Event handle check treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void selectLocationTreeNode(NodeSelectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select location tree node.Event handle Uncheck treeNode Locations in
	 * Supplier qualification criteria .
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectLocationTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
	}

	/**
	 * Delete child tree nodes.
	 * 
	 * @param rootZoneLocation
	 *            (TreeNode rootZoneLocation)
	 * @param index
	 *            (int index)
	 */
	private void deleteChildTreeNodes(List<TreeNode> treeNodes) {
		if (treeNodes != null) {
			List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {

					nodesToRemoved.add(treeNode);

					// deleteSupplierLocacriteria(loccriteria);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {

						deleteChildTreeNodes(treeNode.getChildren());
						// deleteSupplierLocacriteria(loccriteria);
						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
	}

	/**
	 * Show general zones popup.Event handle navigate to the ZoneLocations
	 * Popup.
	 */
	public void showGeneralZonesPopup() {

		ICountrySO countrySo = null;

		try {
			Long locationLevel = this.getManagedBean().getScQt()
					.getZoneLocationLevel().getParameterId();

			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			if (locationLevel == null) {

				throw new EpException(MessageConst.ERROR_760063);
			}

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);

			for (PmParameterDesc paramDesc : localParamDesc) {

				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					this.getManagedBean().setZoneLocation(true);

					CountryReqDTO countryReqDTo = new CountryReqDTO();
					countryReqDTo
							.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
					CountryResDTO countryResDTO = countrySo
							.retrieveCountryByCode(countryReqDTo);
					getManagedBean().setRootZoanlTree(
							LocationSingleton.getInstance()
									.buildLocationPopupTree(
											countryResDTO.getPmCountry()
													.getCountryId(),
											null,
											null,
											null,
											null,
											paramDesc.getPmParameter()
													.getParameterCode(),
											TplSpecZoneLoc.class,
											false));
					this.getManagedBean().setIndex(-1);
					this.getManagedBean().setZoneName("");
				}

			}
			this.getManagedBean().setAutoUpdate(false);
			RequestContext.getCurrentInstance().execute("ZoneGeneral.show()");
		} finally {

		}

	}

	/**
	 * Delete tree node.Event handle delete NonPanel Zones from General
	 * information.
	 */
	public void deleteTreeNode() {
		List<TreeNode> rootTrees = this.getManagedBean().getZoneTrees();

		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (rootTrees.isEmpty()) {
			getManagedBean()
					.getScQt()
					.getScQtSupplierCriteria()
					.getQtZoneCriterias()
					.setTplSpecZoneDtls(new ArrayList<TplSpecZoneDtl>());
		} else{
			getManagedBean()
			.getScQt()
			.getScQtSupplierCriteria()
			.getQtZoneCriterias()
			.setTplSpecZoneDtls(
					convertTreeNodesToZoneCriteria(rootTrees, zoneDtl));
		}
		TplSpecMaster producSpecMaster = this.getManagedBean().getProdscQtDoc()
				.getTplSpecMaster();
		producSpecMaster.setTplSpecZone(getManagedBean().getScQt()
				.getScQtSupplierCriteria().getQtZoneCriterias());
		
		TplSpecMaster serviceSpecMaster = this.getManagedBean()
				.getServicescQtDoc().getTplSpecMaster();

		serviceSpecMaster.setTplSpecZone(getManagedBean().getScQt()
				.getScQtSupplierCriteria().getQtZoneCriterias());
		updateTplSpecMasterNDetails(true);
		this.getManagedBean().setIndex(-1);

	}

	/**
	 * Delete zones tree nodes.
	 * 
	 * @param rootTrees
	 *            the root trees
	 * @return the list
	 */
	private List<TreeNode> deleteZonesTreeNodes(List<TreeNode> rootTrees) {
		TplSpecZoneDtl tplSpecZoneDtl = null;
		List<TreeNode> removedList = new ArrayList<TreeNode>();
		if (rootTrees != null) {
			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			for (int row = 0; row < rootTrees.size(); row++) {
				TreeNode rootTree = rootTrees.get(row);

				for (TreeNode zoneTree : rootTree.getChildren()) {
					if (zoneTree.isSelected()) {
						tplSpecZoneDtl = (TplSpecZoneDtl) zoneTree.getData();
						deleteStateOrDivOrDist(tplSpecZoneDtl, null);

						rootTrees.remove(row--);
					} else {

						removedList = deleteZoneChildTreeNodes(zoneTree
								.getChildren());

						if (removedList != null) {
							for (TreeNode treeNode : removedList) {
								List<TplSpecZoneLoc> locationDTOs = getZoneLocations(treeNode);

								deleteStateOrDivOrDist(tplSpecZoneDtl,
										locationDTOs);
							}
						}

						if (zoneTree.getChildren().size() == 0) {
							rootTrees.remove(row--);
						}
					}

				}
			}
		}
		return removedList;
	}

	/**
	 * Delete zone child tree nodes.
	 * 
	 * @param treeNodes
	 *            the tree nodes
	 * @return the list
	 */
	private List<TreeNode> deleteZoneChildTreeNodes(List<TreeNode> treeNodes) {
		List<TreeNode> nodesToRemoved = new ArrayList<TreeNode>();
		if (treeNodes != null) {

			for (TreeNode treeNode : treeNodes) {
				if (treeNode.isSelected()) {
					nodesToRemoved.add(treeNode);
				} else {
					if (treeNode.getChildren() != null
							&& treeNode.getChildren().size() > 0) {
						deleteChildTreeNodes(treeNode.getChildren());

						if (treeNode.getChildren().size() == 0) {
							nodesToRemoved.add(treeNode);
						}
					}
				}
			}

			if (nodesToRemoved.size() > 0) {
				treeNodes.removeAll(nodesToRemoved);
			}
		}
		return nodesToRemoved;
	}

	/**
	 * Delete state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 */
	private void deleteStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl,
			List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			stateIds = getPmStateIds(tplSpecZoneLocs);

			divIds = getPmDivisionIds(tplSpecZoneLocs);

			distIds = getPmDistrictsIds(tplSpecZoneLocs);
		}

		List<ZonesBean> results = new ArrayList<ZonesBean>();
		List<ZonesBean> list = getManagedBean().getZoneBeans();
		if (list != null && list.size() > 0) {
			for (ZonesBean zonesBean : list) {
				ZonesBean bean = new ZonesBean();
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (tplSpecZoneDtl != null) {
						if (!zone.getZoneName().equalsIgnoreCase(
								tplSpecZoneDtl.getZoneName())) {
							bean = zonesBean;
							results.add(bean);
						}

					} else {
						if (zonesBean.getTplSpecZoneLocs() != null
								&& zonesBean.getTplSpecZoneLocs().size() > 0) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							for (TplSpecZoneLoc TplSpecZoneLoc : zonesBean
									.getTplSpecZoneLocs()) {

								if (TplSpecZoneLoc.getPmState() != null) {

									if (stateIds != null && stateIds.size() > 0) {

										if (!stateIds.contains(TplSpecZoneLoc
												.getPmState().getStateId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}

								} else if (TplSpecZoneLoc.getPmDivision() != null) {
									if (divIds != null && divIds.size() > 0) {
										if (!divIds.contains(TplSpecZoneLoc
												.getPmDivision()
												.getDivisionId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								} else if (TplSpecZoneLoc.getPmDistrict() != null) {
									if (distIds != null && distIds.size() > 0) {
										if (!distIds.contains(TplSpecZoneLoc
												.getPmDistrict()
												.getDistrictId())) {
											specZoneLocs.add(TplSpecZoneLoc);
										}
									}
								}
							}
							bean.setTplSpecZoneLocs(specZoneLocs);
						}
						results.add(bean);
					}
				}
			}
			getManagedBean().setZoneBeans(results);
		}
	}

	/**
	 * Gets the pm state ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmStateIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmState() != null) {
					results.add(TplSpecZoneLoc.getPmState().getStateId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm division ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDivisionIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDivision() != null) {
					results.add(TplSpecZoneLoc.getPmDivision().getDivisionId());
				}
			}
		}
		return results;
	}

	/**
	 * Gets the pm districts ids.
	 * 
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 * @return (List<Long>).
	 */
	private List<Long> getPmDistrictsIds(List<TplSpecZoneLoc> tplSpecZoneLocs) {
		List<Long> results = new ArrayList<Long>();

		if (tplSpecZoneLocs != null && tplSpecZoneLocs.size() > 0) {
			for (TplSpecZoneLoc TplSpecZoneLoc : tplSpecZoneLocs) {

				if (TplSpecZoneLoc.getPmDistrict() != null) {
					results.add(TplSpecZoneLoc.getPmDistrict().getDistrictId());
				}
			}
		}
		return results;
	}

	/**
	 * Update zone general location.Event handle add zones to general
	 * information menu.
	 */
	public void updateZoneGeneralLocation() {
		if (null == this.getManagedBean().getTotalSuppliersSpecLoc()) {
			this.getManagedBean().setTotalSuppliersSpecLoc(
					new ArrayList<TplSpecLoc>());
		}
		List<ZonesBean> beans = getManagedBean().getZoneBeans();
		if (null == beans) {
			beans = new ArrayList<ZonesBean>();
		}
		ZonesBean zonesBean = new ZonesBean();
		String locationLevel = this.getManagedBean().getLocalLevelCode();
		List<TplSpecZoneLoc> allNodes = new ArrayList<TplSpecZoneLoc>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean()
					.getSelectedGeneralTreeNodes()) {

				if (ParameterConst.LOCATION_LEVEL_DIVISION
						.equalsIgnoreCase(locationLevel)) {
					if (treeNode.getType().equals("state")) {
						if (treeNode.getChildCount() == 0
								&& treeNode.getChildren().isEmpty()) {
							List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
							allNodes.addAll(locationDTO);

						}
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				} else {

					if (treeNode.getParent().isSelected()) {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					} else {
						List<TplSpecZoneLoc> locationDTO = getZoneLocations(treeNode);
						allNodes.addAll(locationDTO);
					}
				}
			}
		}

		if (null == getManagedBean().getZoneTrees()) {
			getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		}
		if (CollectionUtils.isNotEmpty(allNodes)) {
			TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();

			if (null == getManagedBean().getZoneName()
					|| StringUtils.isBlank(getManagedBean().getZoneName())) {
				throw new EpException(MessageConst.ERROR_100061, 
						new String[]{ResourceBundleUtils.getMessage("lbl.sc.zone.name", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}

			tplSpecZoneDtl.setZoneName(getManagedBean().getZoneName());
			String editZoneName = getManagedBean().getTempZoneName();
			if (getManagedBean().getIndex() >= 0) {
				updateZoneBean(editZoneName);
			}
			tplSpecZoneDtl.setTplSpecZoneLocs(allNodes);
			zonesBean.setTplSpecZoneDtl(tplSpecZoneDtl);
			
			// validate zone name
			checkZoneExistsOrNot(tplSpecZoneDtl);

			if (ParameterConst.LOCATION_LEVEL_STATE
					.equalsIgnoreCase(locationLevel)) {
				// check duplicate states
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}

			if (ParameterConst.LOCATION_LEVEL_DIVISION
					.equalsIgnoreCase(locationLevel)) {
				// check duplicate divisions
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}

			if (ParameterConst.LOCATION_LEVEL_DISTRICT
					.equalsIgnoreCase(locationLevel)) {
				// check duplicate districts
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(tplSpecZoneDtl);
				}
			}
			beans.add(zonesBean);

			if (getManagedBean().getIndex() == -1) {
				getManagedBean().getZoneTrees().add(
						buildZoneTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
				
			} else if (getManagedBean().getIndex() >= 0) {
				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
				getManagedBean().getZoneTrees().add(
						getManagedBean().getIndex(),
						buildZoneTreeNode(tplSpecZoneDtl, tplSpecZoneDtl.getTplSpecZoneLocs()));
			}
			getManagedBean().getZoneBeans().add(zonesBean);

			ScQtZoneCriteria qtZoneCriteria = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getQtZoneCriterias();
			qtZoneCriteria.setModified(true);
			removeExistingSpecZone(qtZoneCriteria.getTplSpecZoneDtls(),
					editZoneName);
			tplSpecZoneDtl.setTplSpecZone(qtZoneCriteria);
			qtZoneCriteria.getTplSpecZoneDtls().add(tplSpecZoneDtl);
			TplSpecMaster producSpecMaster = this.getManagedBean()
					.getProdscQtDoc().getTplSpecMaster();
			producSpecMaster.setTplSpecZone(qtZoneCriteria);

			TplSpecMaster serviceSpecMaster = this.getManagedBean()
					.getServicescQtDoc().getTplSpecMaster();
			serviceSpecMaster.setTplSpecZone(qtZoneCriteria);

			updateTplSpecMasterNDetails(true);

		} else {
			throw new EpException(MessageConst.ERROR_760163);
			
		}

		this.getManagedBean().setZoneLocation(false);
		getManagedBean().setZoneName(null);
		getManagedBean().setTempZoneName(null);
		this.getManagedBean().setIndex(-1);
		this.getManagedBean().setAutoUpdate(true);
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");

	}

	// check zone name existing in the list or not.
	private void updateZoneBean(String zoneName) {
		List<ZonesBean> beans = new ArrayList<ZonesBean>();
		List<ZonesBean> zoneBeans = getManagedBean().getZoneBeans();

		if (zoneBeans != null && zoneBeans.size() > 0) {
			for (ZonesBean zonesBean : zoneBeans) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();
				if (null != prevsZone) {
					if (!prevsZone.getZoneName().equalsIgnoreCase(zoneName)) {
						beans.add(zonesBean);
					}
				}
			}
		}
		getManagedBean().setZoneBeans(beans);

	}

	/**
	 * Check zone exists or not.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * 
	 * @return List<Long>
	 */
	private List<Long> checkZoneExistsOrNot(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> results = new ArrayList<Long>();
		// if (index < 0) {
		List<ZonesBean> list = getManagedBean().getZoneBeans();
		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl zone = zonesBean.getTplSpecZoneDtl();

				if (zone != null) {

					if (zone.getZoneName().equalsIgnoreCase(
							tplSpecZoneDtl.getZoneName())) {
						throw new EpException(tplSpecZoneDtl.getZoneName()
								+ " zone already exists.");
					}
				}
			}
		}
		// }
		return results;
	}

	/**
	 * Check duplicate state or div or dist.
	 * 
	 * @param tplSpecZoneDtl
	 *            (TplSpecZoneDtl tplSpecZoneDtl)
	 * @param tplSpecZoneLocs
	 *            (List<TplSpecZoneLoc> tplSpecZoneLocs)
	 */
	private void checkDuplicateStateOrDivOrDist(TplSpecZoneDtl tplSpecZoneDtl) {
		List<Long> stateIds = null;
		List<Long> divIds = null;
		List<Long> distIds = null;

		if (null != tplSpecZoneDtl.getTplSpecZoneLocs()
				&& tplSpecZoneDtl.getTplSpecZoneLocs().size() > 0) {
			List<TplSpecZoneLoc> currentTplSpecLocs = tplSpecZoneDtl
					.getTplSpecZoneLocs();
			stateIds = getPmStateIds(currentTplSpecLocs);
			divIds = getPmDivisionIds(currentTplSpecLocs);
			distIds = getPmDistrictsIds(currentTplSpecLocs);
		}

		List<ZonesBean> list = getManagedBean().getZoneBeans();

		if (list != null && list.size() > 0) {

			for (ZonesBean zonesBean : list) {
				TplSpecZoneDtl prevsZone = zonesBean.getTplSpecZoneDtl();

				if (prevsZone != null) {

					if (prevsZone.getTplSpecZoneLocs() != null
							&& prevsZone.getTplSpecZoneLocs().size() > 0) {
						List<TplSpecZoneLoc> previousTplSpecLocs = prevsZone
								.getTplSpecZoneLocs();
						for (TplSpecZoneLoc TplSpecZoneLoc : previousTplSpecLocs) {

							if (TplSpecZoneLoc.getPmState() != null) {

								if (stateIds != null && stateIds.size() > 0) {

									if (stateIds.contains(TplSpecZoneLoc
											.getPmState().getStateId())) {
										throw new EpException(
												MessageConst.ERROR_760168,
												new String[] {
														TplSpecZoneLoc
																.getPmState()
																.getStateName(),
														prevsZone.getZoneName() });
									}
								}

							}

							if (TplSpecZoneLoc.getPmDivision() != null) {
								if (divIds != null && divIds.size() > 0) {
									if (divIds.contains(TplSpecZoneLoc
											.getPmDivision().getDivisionId())) {
										throw new EpException(
												MessageConst.ERROR_760167,
												new String[] {
														TplSpecZoneLoc
																.getPmDivision()
																.getDivisionName(),
														prevsZone.getZoneName() });
									}
								}
							}

							if (TplSpecZoneLoc.getPmDistrict() != null) {
								if (distIds != null && distIds.size() > 0) {
									if (distIds.contains(TplSpecZoneLoc
											.getPmDistrict().getDistrictId())) {
										throw new EpException(
												MessageConst.ERROR_760166,
												new String[] {
														TplSpecZoneLoc
																.getPmDistrict()
																.getDistrictName(),
														prevsZone.getZoneName() });
									}
								}
							}
						}
					}
					if (prevsZone.getZoneName().equals(
							tplSpecZoneDtl.getZoneName())) {
						throw new EpException(
								MessageConst.ERROR_700027,
								new String[] { ResourceBundleUtils.getMessage(
										tplSpecZoneDtl.getZoneName(),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}
			}
		}
	}

	/**
	 * Fulfilment change.changed Fulfilemnt Dropdown in specification committee.
	 * 
	 * @param event
	 *            (Long fulfilmentId)
	 */
	public void fulfilmentChange(AjaxBehaviorEvent event) {
		Long type = (Long) event.getComponent().getAttributes().get("value");
		List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.FULFILMENT_TYPE);
		for (PmParameterDesc paramDesc : localParamDesc) {
			if (paramDesc.getPmParameter().getParameterId()
					.equals(Long.valueOf(type))) {

				this.getManagedBean().getScQt()
						.setFulfilmentType(paramDesc.getPmParameter());
				this.getManagedBean().getScLoa().getScLoiLoa()
						.setFulfilmentTypeCode(paramDesc.getPmParameter());
				break;
			}
		}
		
		//#24540 - start -
		if (null == getManagedBean().getPeriodicAsAndWhenParamId()) {
			getManagedBean().setPeriodicAsAndWhenParamId(getParameterIdByTypeAndCode(ParameterTypeConst.FULFILMENT_TYPE, ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.toUpperCase()));
		}
		
		if (null == getManagedBean().getPeriodicScheduleParamId()) {
			getManagedBean().setPeriodicScheduleParamId(getParameterIdByTypeAndCode(ParameterTypeConst.FULFILMENT_TYPE, ParameterConst.FULFILMENT_TYPE_SCHEDULED.toUpperCase()));
		}
		/*
		if (type == getManagedBean().getPeriodicAsAndWhenParamId().longValue() || type == getManagedBean().getPeriodicScheduleParamId().longValue()) {
			getManagedBean().setBondReqSelectedVal(null);
			getManagedBean().setAgreemetSelectedVal(null);
			getManagedBean().getScLoa().setBondReq(false);
		}*/
		//#24540 - end -
		
		if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
				.getManagedBean().getScQt().getFulfilmentType()
				.getParameterCode())) {
			this.getManagedBean().getScQt()
					.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
			this.getManagedBean().setTotalFrequency(0);
			this.getManagedBean().setTotalProgressivePayment(0);

		}
		checkFulFilmentType(this.getManagedBean().getScLoa());
		calculateServiceTemplate();
		this.getManagedBean().getScQt().setScQtSchedules(null);
		this.getManagedBean().getScQt().setScQtProgressivePayments(null);
		removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT, true);
		removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE, true);
		getLoaReports();
		checkAgreementMandatory();

	}
    private void checkFulFilmentType(ScLoa scLoa){
    log.info("DirectLoacheckFulFilmentTypeContractDuration"+scLoa.getContractDuration());
	PmParameter fulfilMentType=scLoa.getScLoiLoa().getFulfilmentTypeCode();
	if(isFulfillmentTypeOneOffOrAsAndWhen(fulfilMentType)){
			this.getManagedBean()
			.getSpecTemplateSpecBean()
			.getSpecMaster().setDurationMonth(
				null);
	}else{
		this.getManagedBean()
		.getSpecTemplateSpecBean()
		.getSpecMaster()
		.setDurationMonth(
				scLoa.getContractDuration());
	}
	  log.info("EndDirectLoacheckFulFilmentTypeContractDuration"+this.getManagedBean()
				.getSpecTemplateSpecBean()
				.getSpecMaster()
				.getDurationMonth());
}
	/**
	 * Gets the dropdown location values.display the Location level dropdown
	 * values.
	 * 
	 * @param locationLevel
	 *            the location level
	 * @return the dropdown location values
	 */
	public SelectItem[] getDropdownLocationValues(String locationLevel) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		if (PTJLocalityCode.STATE_SARAWAK == this.getManagedBean()
				.getAllStateCode()) {
			types.add(ParameterConst.LOCATION_LEVEL_DIVISION);
		}
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);

		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));
			int i = 1;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);

				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {

						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
		// return getDropdownCodeValues(committeeRole);
	}

	/**
	 * Select zone location level.change Location Level Dropdown in General
	 * information menu.
	 * 
	 * @param event
	 *            (Long locationLevelId).
	 */
	public void selectZoneLocationLevel(AjaxBehaviorEvent event) {

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getZoneTrees())) {
			this.getManagedBean().setZoneTrees(null);
			this.getManagedBean().setZoneBeans(new ArrayList<ZonesBean>());
		}
		this.getManagedBean().setSelectedGeneralTreeNodes(null);
		Long parameterId = (Long) ((UIOutput) event.getSource()).getValue();
		if (null != parameterId) {
			this.getManagedBean()
					.setLocalLevelCode(
							ParameterHelper.getInstance()
									.getPmParameterById(parameterId)
									.getParameterCode());
		}
		
		this.getManagedBean().getScQt().getScQtSupplierCriteria()
				.getQtZoneCriterias()
				.setTplSpecZoneDtls(new ArrayList<TplSpecZoneDtl>());
		this.getManagedBean()
				.getServicescQtDoc()
				.getTplSpecMaster()
				.setTplSpecZone(
						this.getManagedBean().getScQt()
								.getScQtSupplierCriteria().getQtZoneCriterias());
		this.getManagedBean()
				.getProdscQtDoc()
				.getTplSpecMaster()
				.setTplSpecZone(
						this.getManagedBean().getScQt()
								.getScQtSupplierCriteria().getQtZoneCriterias());
		updateTplSpecMasterNDetails(true);
	}

	/**
	 * Zone radio change.change zone location level radio in business owner.
	 * 
	 * @param event
	 *            (boolean zoneLocationlevel).
	 */
	public void zoneRadioChange(AjaxBehaviorEvent event) {
		Boolean isZonal = (Boolean) event.getComponent().getAttributes()
				.get("value");
		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
		scQt.setZoneLocationLevel(new PmParameter());

		if (supplierCriteria == null) {
			supplierCriteria = new ScQtSupplierCriteria();
			supplierCriteria.setScQt(scQt);
		}
		
		if (isZonal) {
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();
			if (null == zoneCriteria) {
				zoneCriteria = new ScQtZoneCriteria();
				zoneCriteria.setScQtSupplierCriteria(supplierCriteria);

				supplierCriteria.setQtZoneCriterias(zoneCriteria);

			}
			zoneCriteria.setTplSpecZoneDtls(new ArrayList<TplSpecZoneDtl>());
			supplierCriteria.setScQtLocCriteria(null);
			setUserDetails(zoneCriteria);

			this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.setTplSpecZone(zoneCriteria);

			this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.setTplSpecZone(zoneCriteria);
		} else {
			ScQtZoneCriteria zoneCriteria = supplierCriteria
					.getQtZoneCriterias();

			this.getManagedBean().setToBedeletedSupplierZone(zoneCriteria);

			supplierCriteria.setQtZoneCriterias(null);
			this.getManagedBean().setZoneTrees(null);
			this.getManagedBean().setZoneBeans(new ArrayList<ZonesBean>());

			this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.setTplSpecZone(null);
			this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.setTplSpecZone(null);
		}
		scQt.setScQtSupplierCriteria(supplierCriteria);
		this.getManagedBean().setScQt(scQt);
		// Code to reset tplspecEstimations.
		updateTplSpecMasterNDetails(isZonal);
	}

	/**
	 * Gets the approval users.
	 *
	 * @return the approval users
	 */
	public void getApprovalUsers() {
		if(this.getManagedBean().getScQt()
					.getPmOrgProfile() != null) {
			if (CollectionUtils.isEmpty(this.getManagedBean().getApproverList())) {
				IUserSO userSo = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
				userReqDTO.setOrgProfileId(this.getManagedBean().getScQt()
						.getPmOrgProfile().getOrgProfileId());
				userReqDTO.setWithProfileGroup(true);
				UserResDTO userResDTO = userSo
						.retrieveUserListWithProfileGroupUserByRole(userReqDTO);

				this.getManagedBean().setApproverList(userResDTO.getUserList());
			}
		}
	}

	/**
	 * Schedule type change.Event handle change Schedule
	 * type(Schedule/Progressive payment)in Specification committee.
	 * 
	 * @param event
	 *            (String ScheduleType).
	 */
	public void scheduleTypeChange(AjaxBehaviorEvent event) {

		String scheduleType = (String) event.getComponent().getAttributes()
				.get("value");
		String toBeRemoveType = null;
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			toBeRemoveType = ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT;
			this.getManagedBean().setTotalProgressivePayment(0);
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			toBeRemoveType = ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE;
			this.getManagedBean().setTotalFrequency(0);
		}
		this.getManagedBean().getScQt().setScheduleType(scheduleType);
		removefullfillmentDetails(toBeRemoveType, true);

	}

	private void removefullfillmentDetails(String scheduleType,
			boolean resetList) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
					.getScQtSchedules();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = getManagedBean().getToBeRemoveSchedules();
				if (toBeRemoveList == null) {
					toBeRemoveList = new ArrayList<ScQtSchedule>();	
				}

				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().setToBeRemoveSchedules(toBeRemoveList);
				this.getManagedBean().getScQt().setScQtSchedules(null);
			}
			if (resetList) {
				this.getManagedBean().getScLoa()
						.setScQtSchedules(new ArrayList<ScQtSchedule>());
			}
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getScLoa().getScQtProgPayments();

			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = getManagedBean().getToBeRemoveProgPayments();
				if (toBeRemoveList == null) {
					toBeRemoveList = new ArrayList<ScQtProgressivePayment>();	
				}

				for (ScQtProgressivePayment payment : payments) {
					if (null != payment.getQtPaymentId()) {
						payment.setDeleted(true);
						toBeRemoveList.add(payment);
					}
				}
				this.getManagedBean().setToBeRemoveProgPayments(toBeRemoveList);
				this.getManagedBean().getScQt()
						.setScQtProgressivePayments(null);
			}
			if (resetList) {
				this.getManagedBean()
						.getScLoa()
						.setScQtProgPayments(
								new ArrayList<ScQtProgressivePayment>());
			}
		}

	}

	/**
	 * Calculate progressive start month.change start month in progessive
	 * payment.
	 * 
	 * @param progressivepayment
	 *            (ScQtProgressivePayment progressivepayment).
	 */
	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean().getScLoa()
				.getScQtProgPayments();

		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			if (null == progressivepayment.getEndMonth()) {
				progressivepayment.setEndMonth(progressivepayment
						.getStartMonth());
			}
			payment.set(index, progressivepayment);

			this.getManagedBean().getScLoa().setScQtProgPayments(payment);
		}

	}

	/**
	 * Calculate progressivepayment.change end month dropdown in progressive
	 * payment.
	 * 
	 * @param event
	 *            the event
	 */
	public void calculateProgressivepayment(
			ScQtProgressivePayment progressivepayment) {

		List<ScQtProgressivePayment> payment = this.getManagedBean().getScLoa()
				.getScQtProgPayments();

		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment) {
			int index = payment.indexOf(progressivepayment);

			payment.set(index, progressivepayment);

			this.getManagedBean().getScLoa().setScQtProgPayments(payment);
		}

	}

	private void getLoaReports() {
		if (CollectionUtils.isEmpty(this.getManagedBean().getScLoa()
				.getScLoaReports())) {
			IQTAwardSO awardSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTAwardSO.class);
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			awardSupplierReqDTO.setScLoa(this.getManagedBean().getScLoa());
			this.getManagedBean()
					.getScLoa()
					.setScLoaReports(
							awardSO.getAwardReports(awardSupplierReqDTO)
									.getLoaReports());
		}
	}

	/**
	 * Adds the loa content action.
	 */
	public void addLoaContentAction() {
		debugLogger("addLoaContentAction");
		ScLoa scLoa = this.getManagedBean().getScLoa();
		validateRepoAttachment(scLoa);
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		if (scLoaReports == null) {
			scLoaReports = new ArrayList<ScLoaReport>();
			scLoa.setScLoaReports(scLoaReports);
		}

		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(scLoa);

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoaReport.setDefinationType(resDTO.getPmParameter());
			scLoaReport
					.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); // AQ
		} finally {
			parameterSO = null;
		}

		scLoaReports.add(scLoaReport);
	}

	/**
	 * Delete loa content action.
	 */
	public void deleteLoaContentAction() {
		debugLogger("deleteLoaContentAction");
		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScLoaReport> selectedScLoaReports = getManagedBean()
				.getSelectedScLoaReports();

		if (selectedScLoaReports != null && selectedScLoaReports.size() > 0) {
			deleteRowsAction(scLoa.getScLoaReports(), selectedScLoaReports);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}


	/**
	 * Download all reports.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			if(scLoaReport.getTitle().equalsIgnoreCase(QTConstants.REPORT_ACKNOWLEDGEMENT_LOA_RECEIPT) &&
					scLoa.getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
				// #17376 -start-
				if (this.getManagedBean().isValidatewitness()) {
					validateLoaWitnessAndAckBy();
				}
				// #17376 -end-
			}
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			// #26130 - Start
			boolean isGenerate = false;

			if ("SuratSetujuTerima(TermasukLampiranA)".equals(scLoaReport.getTitle().replaceAll("\\s", ""))
					&& scLoaReport.getDefinationType().getParameterCode()
							.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD)
					&& (getManagedBean().getScLoa().getPmStatus().getStatusId()
							.equals(QTStatusConst.LOA_PENDING_VERIFICATION) || getManagedBean().getScLoa()
							.getPmStatus().getStatusId().equals(QTStatusConst.LOA_COMPLETED))) {
				isGenerate = true;
			}
			ScLoaReportAttachment scLoaReportAttachmentGenerated = generateDocumentAttachment(scLoaReport, isGenerate);
			// #26130 - End
			if(scLoaReport.getDefinationType().getParameterCode().equals(
					ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD) ||
					scLoaReport.getDefinationType().getParameterCode().equals(
							ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD)) {
				scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			} 
			String prefixName = scLoaReport.getTitle().replaceAll(
					SourcingConstants.REG_EXP_WHITE_SPACE, "");
			
			// 29007-start
			if (StringUtils.isNotBlank(scLoaReport.getTitle())
					&& scLoaReport.getTitle().contains(lampiranA3SubStr)) {
				prefixName = prefixName.replaceAll(
						SourcingConstants.REG_EXP_BACK_SLASH, "");
			}
			// 29007-End
			
			// #26770-start
			scLoaReportAttachmentGenerated.setTempFileName(setValueLowercase(prefixName, 18));
			// 26770-start
			scAttachments.add(scLoaReportAttachmentGenerated);
		}

		downloadAllFiles("NGeP-LOA-Report", scAttachments);
	}

	/**
	 * Generate document attachment.
	 * 
	 * @param scLoaReport
	 *            the sc loa report
	 * @return the sc loa report attachment
	 */
	private ScLoaReportAttachment generateDocumentAttachment(
			ScLoaReport scLoaReport, boolean isGenerated) {
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equals(definitionType) || 
				ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD.equals(definitionType) ||
				ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();
				ScLoa scLoa = getManagedBean().getScLoa();
				Long statusId = scLoa.getPmStatus().getStatusId().longValue();
				
				if (scLoaReportAttachment == null || isGenerated) {
					StringBuilder filename = new StringBuilder(); // replace white space due to firefox
                    filename.append(scLoaReport.getTitle().replaceAll("\\s", ""));
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(scLoaReport);
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());
					
					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						// #29007 Start Vinod
						if (StringUtils.isNotBlank(scLoaReport.getTitle())
								&& scLoaReport.getTitle().contains(lampiranA3SubStr)) {
							filename.append(QTConstants.LAMPIRAN_A3_FILE_EXTENSION_PDF);
							
							scLoaReportAttachment.setFileName(filename.toString().replaceAll("/", ""));

							generateLampiranA3PdfFromVelocity(scLoaReportAttachment, scLoa);
						} else {
							// #29007 End Vinod
						// Enhancement #26130 -start-
						if (getManagedBean().getScLoa().getPmStatus().getStatusId()
								.equals(QTStatusConst.LOA_PENDING_VERIFICATION)
								|| getManagedBean().getScLoa().getPmStatus().getStatusId()
										.equals(QTStatusConst.LOA_COMPLETED)) {

							scLoaReportAttachment = scLoaReport.getScLoaReportAttachment();

							scLoaReportAttachment.setDocNo(scLoaReport.getScLoa().getLoaNo());

							generaateUpdatedLampiraA3PDF(scLoaReportAttachment, scLoa);

						} else {

							filename.append(".docx");
							scLoaReportAttachment.setFileName(filename.toString());
							generateAgreementLetterDocFromVelocity((ScAttachment) scLoaReportAttachment, scLoa,
									getManagedBean().getScQt());
						}
						// Enhancement #26130 -end-
						}// #29007 Added Vinod
					}else if (SourcingConstants.DOC_DEF_KEY_ACKNOWLEDGEMENT_LOA_RECEIPT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAckOfLOAReceiptPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, getManagedBean().getScQt());
					}else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					}else if (SourcingConstants.DOC_DEF_KEY_SURAT_AKUAN_SYARIKAT
							.equals(docDefinitionKey)){
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationOfOathFromVelocity(scLoaReportAttachment, scLoa);
					}
					/*if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateAgreementLetterPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa,
								getManagedBean().getScQt());
					} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						saveScheduleList();
						generateFrequencySchedulePdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa,
								getManagedBean().getScQt());
					} else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formJadualHarga((ScAttachment) scLoaReportAttachment,
								scLoa);

					} else if (SourcingConstants.DOC_DEF_KEY_SPECIFICATION
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formSpecification((ScAttachment) scLoaReportAttachment,
								scLoa);

					} else if (SourcingConstants.DOC_DEF_KEY_FORMAT_BOND_EXECUTION
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateBondLetterFromVelocityToPdf(
								(ScAttachment) scLoaReportAttachment, scLoa);
					} else if (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment);
					} else if (SourcingConstants.DOC_DEF_KEY_ACKNOWLEDGEMENT_LOA_RECEIPT
							.equals(docDefinitionKey)) {
						// download from system
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment);
					}*/
				} else if (scLoaReportAttachment != null
						&& QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)
						|| QTStatusConst.LOA_PENDING_VERIFICATION
						.equals(statusId)) { // if
					// pending supplier acknowledgement , always regenerate
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());
					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
					}else if (SourcingConstants.DOC_DEF_KEY_ACKNOWLEDGEMENT_LOA_RECEIPT
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
						/*generateAckOfLOAReceiptPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa, getManagedBean().getScQt());*/
					}else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						//filename.append(".pdf");
						//scLoaReportAttachment.setFileName(filename.toString());
						/*generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);*/
					}else if (SourcingConstants.DOC_DEF_KEY_SURAT_AKUAN_SYARIKAT
							.equals(docDefinitionKey)){
						//generateDeclarationOfOathFromVelocity(scLoaReportAttachment, scLoa);
					}
					/*if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
 					} else if (SourcingConstants.DOC_DEF_KEY_INTEGRITY_PACT
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateDeclarationPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);

					} else if (SourcingConstants.DOC_DEF_KEY_JADUAL_HARGA
							.equals(docDefinitionKey)) {
						filename.append(".xlsx");
						scLoaReportAttachment.setFileName(filename.toString());
						formJadualHarga((ScAttachment) scLoaReportAttachment,
								scLoa);

					} else if (SourcingConstants.DOC_DEF_KEY_LOI_NEGOTIATIONS_OUTCOME
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateNegotiationResultPdf((ScAttachment) scLoaReportAttachment);
					} 
				} else if (scLoaReportAttachment != null
						&& QTStatusConst.LOA_PENDING_SUBMISSION
						.equals(statusId)) {
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());

					// if pending submission , always regenerate
					if (SourcingConstants.DOC_DEF_KEY_FREQUENCY_SCHEDULE
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateFrequencySchedulePdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa,
								getManagedBean().getScQt());
					}
					else if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
							.equals(docDefinitionKey)) {
						filename.append(".pdf");
						scLoaReportAttachment.setFileName(filename.toString());
						generateContractSummaryPdfFromVelocity(
								(ScAttachment) scLoaReportAttachment, scLoa);
					}*/
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new EpException(e.getMessage());
			}
			if (null != scLoaReportAttachment
					&& scLoaReportAttachment.getFileName() != null
					&& scLoaReportAttachment.getFilePath() != null) {
				scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			}
		}

		return scLoaReportAttachment;
	}

	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}

	/**
	 * Retrive nonpanel zone dtls.retrive all the zones informations and
	 * display.
	 * 
	 * @param zonedtls
	 *            the zonedtls
	 */
	private void constructTreeNodeForZones(List<TplSpecZoneDtl> zonedtls) {

		List<ZonesBean> beans = getManagedBean().getZoneBeans();
		
		List<TreeNode> zoneTrees  = new ArrayList<TreeNode>();

		if (isNotNullOREmpty(zonedtls)) {

			for (TplSpecZoneDtl zone : zonedtls) {

				if (null != this.getManagedBean().getScQt()
						&& !this.getManagedBean().getScQt().isPanel()) {
					ZonesBean zonesBean = new ZonesBean();
					zonesBean.setTplSpecZoneDtl(zone);
					zonesBean.setTplSpecZoneLocs(zone.getTplSpecZoneLocs());
					beans.add(zonesBean);
				}
				zoneTrees.add(
						LocationSingleton.getInstance().constuctTreeNode(zone,
								zone.getTplSpecZoneLocs()));
			}
			getManagedBean().setZoneTrees(zoneTrees);
			getManagedBean().setZoneBeans(beans);
		}

	}

	/**
	 * Pre render direct loa.
	 */
	public void preRenderDirectLoa() {
		FacesContext fc = FacesContext.getCurrentInstance();		
		
		try {
			SCAwardSupplierDataType awardSupplierDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
			String redirectUrl = retriveLoaDetails(awardSupplierDataType.getAwardId(), null, false);
	
			contractTypeChanged();
			viewContractDetails(this.getManagedBean().getScLoa());
			this.getManagedBean().setPayLoad(awardSupplierDataType);
			if(getManagedBean().getCurrentStatusId() != null){
				if(QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getCurrentStatusId())){
					getManagedBean().setSelectedTabIndex(1);
					getManagedBean().setIconForDL(true);// #29007
				}
			}
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, redirectUrl);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
		}
	}

	/**
	 * Get the loa details.
	 * 
	 * @param loaId 
	 * 		the loa id
	 * @param previousPageUrl
	 * 		the previous page url.
	 * @param isReadonly
	 * 		the readonly flag
	 * 
	 * @return the loa page
	 */
	public String retriveLoaDetails(Long loaId, String previousPageUrl, boolean isReadonly) {
		IQTDirectAwardSO directAwardSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDirectAwardSO.class);
		
		getManagedBean().setBackToPreviousPage(previousPageUrl);
		getManagedBean().setReadOnly(isReadonly);

		DirectAwardReqDTO awardReqDTO = new DirectAwardReqDTO();
		if (null == loaId) {
			awardReqDTO.setScLoa(new ScLoa());
		} else {
			awardReqDTO.setLoaId(loaId);
		}
		DirectAwardResDTO awardResDTO = directAwardSO
				.retriveDirectAwardDetails(awardReqDTO);
		ScLoa scLoa = awardResDTO.getLoa();
		setUserDetails(scLoa);
		setUserDetails(scLoa.getScLoiLoa());
		this.getManagedBean().setScLoa(scLoa);
		ScQt scQt = scLoa.getScQt();
		setUserDetails(scQt);
		this.getManagedBean().setScQt(scLoa.getScQt());
		this.getManagedBean().setApproverUser(new PmUser());
		this.getManagedBean().setScLoa(scLoa);
		if(scLoa.getContractType().getParameterId() != null) {
			this.getManagedBean().setContractTypeId(scLoa.getContractType().getParameterId());
		}
		if (scQt.isZonal()) {
			List<TplSpecZoneDtl> zoneDetails = awardResDTO.getSpecZoneDetails();
			constructTreeNodeForZones(zoneDetails);
			this.getManagedBean().setLocalLevelCode(scQt.getZoneLocationLevel().getParameterCode());
		}
		this.getManagedBean().setContractSignerList(
				awardResDTO.getContractSignerList());
		this.getManagedBean().setApproverList(awardResDTO.getApproverList());
		// award details

		this.getManagedBean().setProdscQtDoc(awardResDTO.getProductDoc());
		setUserDetails(awardResDTO.getProductDoc());
		
		// overall ct amount or loa amt
		//Enhancement #24259 #24269
//		BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
		BigDecimal overallCtAmout = overallCtAmountWithSST(scLoa.getLoaAmt(), scLoa.getSstAmt());
		getManagedBean().setOverallContractAmt(overallCtAmout);
		scLoa.setOverallContractAmt(overallCtAmout);
		
		// Acknowledge Date
		if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(scLoa
				.getPmStatus().getStatusId().longValue()) ||
				QTStatusConst.LOA_PENDING_REVISION.equals(scLoa
						.getPmStatus().getStatusId().longValue())) {
			Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
			scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
		}

		TemplateSpecBean bean = new TemplateSpecBean(
				TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);

		bean.setSpecMaster(this.getManagedBean().getProdscQtDoc()
				.getTplSpecMaster());

		this.getManagedBean().setProdTemplateSpecBean(bean);
		this.getManagedBean().setProdFirst(0);
		bean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
				.getSmSupplier());
		if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
				.isBrandNew()) {

			TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
			backingBean.retrieveTemplateSpecMaster(bean);
			this.getManagedBean()
					.getProdscQtDoc()
					.setTplSpecMaster(
							this.getManagedBean().getProdTemplateSpecBean()
									.getSpecMaster());
		}

		this.getManagedBean().setServicescQtDoc(awardResDTO.getServiceDoc());
		setUserDetails(awardResDTO.getServiceDoc());
		TemplateSpecBean serviceBean = new TemplateSpecBean(
				TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
		serviceBean.setSpecMaster(this.getManagedBean().getServicescQtDoc()
				.getTplSpecMaster());

		serviceBean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
				.getSmSupplier());
		this.getManagedBean().setSpecTemplateSpecBean(serviceBean);
		this.getManagedBean().setServFirst(0);

		if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
				.isBrandNew()) {

			TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
			backingBean.retrieveTemplateSpecMaster(serviceBean);
			backingBean.calcGrandTotalAmount(serviceBean);
			this.getManagedBean()
					.getServicescQtDoc()
					.setTplSpecMaster(
							this.getManagedBean().getSpecTemplateSpecBean()
									.getSpecMaster());
		}
		if (null != scLoa.getScLoaApproval()) {
			this.getManagedBean().setApproverUser(
					scLoa.getScLoaApproval().getApprover());
		}
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, 
				scLoa.getCurrentStatus(),
				scLoa.getScLoaRemarks(), ScLoaRemark.class));
		
		// #11134 - start
		scLoa.setScLoaRemarks(filterSupplierRemarks(scLoa.getScLoaRemarks(), false));
		// #11134 - end
		
		// bond calculation
		//Enhancement #24259 #24269
		scLoa = calcBondAmountWithSST(scLoa, true);
//		scLoa = calcBondAmount(scLoa, true);

		// bond amount in words
		// #35977 Updated for English Start Vinod
		if (scLoa.getBondAmt() != null
				&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtilEn.convert(scLoa.getBondAmt().toString()));
		} else if (scLoa.getBondAmt() != null
				&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		} else {
			getManagedBean().setBondAmountWord(null);
		}
		// #35977 End Vinod

		// procurement type category
		if (scLoa.getProcurementTypeCatId() == null) {
			scLoa.setProcurementTypeCatId(new PmParameter());
		}

		// contract Signer info
		if (scLoa.getContractSigner() == null) {
			scLoa.setContractSigner(new PmUser());
		} else { //#26474 - start
			getManagedBean().setContractSignerUser(scLoa.getContractSigner());
			getManagedBean().getScLoa().setLoaSignerDesig(scLoa.getContractSigner().getDesignation());
		}
		
		if (this.getManagedBean().getSelectedPtj() == null) {
			PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
			this.getManagedBean().setSelectedPtj(pmOrgProfile);
		}
		// #26474 - end

		// Actual Total Intended Price (RM)
		if (scLoa.getEstimatedPrice() != null) {
			scQt.setEstimatedAmount(scLoa.getEstimatedPrice());
		}

		this.getManagedBean().setContractSignerList(
				awardResDTO.getContractSignerList());
		this.getManagedBean().setSupplierHQPersonnel(
				awardResDTO.getSupplierHQPersonnel());
		this.getManagedBean()
				.setBranchsList(awardResDTO.getSmSupplierBranchs());
		if (CollectionUtils.isNotEmpty(scLoa.getScQtDeductions())) {
			this.getManagedBean().setSelectedScQtDeduction(
					scLoa.getScQtDeductions().get(0));
		}
		// construct frequence schedule
		constructStartNEndMonth();
		
		// 29007&31423-start
		if (isQTMaintenanceScreen()
				&& (QTStatusConst.LOA_PENDING_VERIFICATION.equals(scLoa
						.getPmStatus().getStatusId().longValue()) || QTStatusConst.LOA_COMPLETED
						.equals(scLoa.getPmStatus().getStatusId().longValue()))) {// 29007&31423-start
			getManagedBean().setIconForDL(true);

		}// 29007&31423-End
		// #32119 - Missing icon from Contract Maintenance
		if (isCTMaintenanceScreen()
				&& (QTStatusConst.LOA_PENDING_VERIFICATION.equals(scLoa
						.getPmStatus().getStatusId().longValue()) || QTStatusConst.LOA_COMPLETED
						.equals(scLoa.getPmStatus().getStatusId().longValue()))) {
			getManagedBean().setIconForDL(true);

		}
		// #32199 - end

		// 25791 -start-
		if (isQTMaintenanceScreen() || isCTMaintenanceScreen()) {
			this.getManagedBean().setSelectedTabIndex(QTConstants.TAB_MAIN_INDEX);
		}
		// 25791 -end-

		return QTConstants.SC_DIRECT_LOA_MAIN_PAGE;
	}
	
	public void calculateServiceTemplate(){
		log.info("start calculateServiceTemplate");
		try{
			TplSpecMaster serviceSpecMaster=this.getManagedBean().getServicescQtDoc().getTplSpecMaster();
			/*TemplateSpecBean serviceBean = new TemplateSpecBean(
					TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			serviceBean.setSpecMaster(serviceSpecMaster);

			serviceBean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa()
					.getSmSupplier());
			this.getManagedBean().setSpecTemplateSpecBean(serviceBean);
			this.getManagedBean().setServFirst(0);*/
			List<TplSpecAnswer> specAnswerList = null;

			if (serviceSpecMaster.getTplSpecQuestions() != null) {
				log.info("calculateServiceTemplateQuestionsNotNull");
				specAnswerList = new ArrayList<TplSpecAnswer>();
				for (TplSpecQuestion specQuestion : serviceSpecMaster
						.getTplSpecQuestions()) {
					if (specQuestion.getTplSpecAnswers() != null) {
						specAnswerList.addAll(specQuestion.getTplSpecAnswers());
					}
				}
			}
			if (null!=specAnswerList && CollectionUtils.isNotEmpty(specAnswerList)) {
				log.info("calculateServiceTemplateSpecAnswersNotNull");
				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();
				//backingBean.retrieveTemplateSpecMaster(serviceBean);
				for(TplSpecAnswer specAnswer:specAnswerList){
				backingBean.calcTotalAmount(this.getManagedBean().getSpecTemplateSpecBean(), specAnswer, true);
				}
				this.getManagedBean()
						.getServicescQtDoc()
						.setTplSpecMaster(
								this.getManagedBean().getSpecTemplateSpecBean()
										.getSpecMaster());
			}	
		}finally{
			
		}
	}
	/**
	 * Page navigation from loa details page to qt loa list or dp loa list.
	 */
	public String backToPrevious() {
		log.info("backToPrevious");
		
		return getManagedBean().getBackToPreviousPage();
	}

	private String getMonth(String month) {
		FacesScopes facesScopes = new FacesScopes();
		List <ScQtSchedule> scQtSchedule = getManagedBean().getScLoa().getScQtSchedules();
		for (ScQtSchedule schedule : scQtSchedule) {
			if (StringUtils.isEmpty(month)
					|| schedule.getStartMonth() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.start.month",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								facesScopes.getUserLocale()) });
			}
			else if(StringUtils.isEmpty(month)
					|| schedule.getEndMonth() == null) {
				throw new EpException(MessageConst.ERROR_100022,
						new String[] { ResourceBundleUtils.getMessage(
								"lbl.sc.end.month",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								facesScopes.getUserLocale()) });
			}
		}
		
		int mon = Integer.parseInt(month);
		String finalMonth = null;
		if (mon <= 9) {
			finalMonth = "0" + mon;
		} else {
			return month;
		}
		return finalMonth;

	}

	private void constructStartNEndMonth() {

		List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		if (CollectionUtils.isNotEmpty(scheduleList)) {

			this.getManagedBean().setTotalFrequency(scheduleList.size());

		try{
			for (ScQtSchedule scQtSchedule : scheduleList) {

				
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						CommonConstants.DEFAULT_DATE_FORMAT);
				int months;
				Date startDate = null;
				Date endDate = null;
				String fromdate = dateFormat.format(scQtSchedule
						.getFromDate());
				String todate = dateFormat.format(scQtSchedule
						.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6,
						fromdate.length());
				String fromDay = fromdate.substring(0, 2);

				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6,
						todate.length());
				String toDay = todate.substring(0, 2);

				//startMonth = fulfilmentMonthlyDtls(startdate);
				//endMonth = fulfilmentMonthlyDtls(enddate);

				int sYear = Integer.parseInt(startYear);
				int eYear = Integer.parseInt(endYear);

				String fromDate = fromDay + "/" + startdate + "/"
						+ sYear;
				String toDate = toDay + "/" + enddate + "/" + eYear;

				startDate = dateFormat.parse(fromDate);
				endDate = dateFormat.parse(toDate);

				months = DateUtils.getMonths(startDate, endDate);
				scQtSchedule.setTotalMonths(new BigDecimal(months));

				scQtSchedule.setStartMonth(Integer.valueOf(startdate).toString());
				scQtSchedule.setEndMonth(Integer.valueOf(enddate).toString());
				scQtSchedule.setStartYear(startYear);
				scQtSchedule.setEndYear(endYear);

			}
			this.sortFrequentScheduleList(scheduleList);
		}catch(ParseException e){
			
		}

		}

		// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());

		List<ScQtProgressivePayment> payments = this.getManagedBean()
				.getScLoa().getScQtProgPayments();
		if (CollectionUtils.isNotEmpty(payments)) {
			this.getManagedBean().setTotalProgressivePayment(payments.size());
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

			for (ScQtProgressivePayment payment : payments) {

				String fromdate = dateFormat.format(payment.getFromDate());
				String todate = dateFormat.format(payment.getToDate());
				String startdate = fromdate.substring(3, 5);
				String startYear = fromdate.substring(6, fromdate.length());
				String enddate = todate.substring(3, 5);
				String endYear = todate.substring(6, todate.length());
				int start = Integer.parseInt(startdate);
				int end = Integer.parseInt(enddate);
				payment.setStartMonth(Integer.valueOf(start).toString());
				payment.setEndMonth(Integer.valueOf(end).toString());
				payment.setStartYear(startYear);
				payment.setEndYear(endYear);

			}
			//this.sortProgressivePaymentList(payments);
		}
	}

	/**
	 * Gets the bond letter stream content.
	 * 
	 * @return the bond letter stream content
	 * @throws Exception
	 *             the exception
	 */
	public StreamedContent getBondLetterStreamContent() throws Exception {

		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		generateBondLetterFromVelocityToPdf((ScAttachment) pbAttachment, null);
		StreamedContent pbStreamContent = getAttachmentStreamContent(
				pbAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);

		return pbStreamContent;

	}

	/**
	 * Show verify button.
	 */
	public void showVerifyButton() {

		if (getManagedBean().isVerifyBtnFlag()) {
			getManagedBean().setVerifyBtnFlag(false);
		}
	}
	//20190405@asyrafhaziq - CR25062 - Start 
		/**
		 * Approve before date bpm task action. Approve - Will show notification
		 * If yes it will go to authenticating gpki
		 * 
		 * @param outcome
		 *            the outcome
		 */
		public void approveBeforeupdateBpmTaskAction(String outcome) {
			debugLogger("approveBeforeupdateBpmTaskAction");
			
			/* #29007 Start Vinod */
			if (BpmOutcomeConst.BPM_VERIFY.equals(outcome) && this.getManagedBean().getLoaBpmVerify() == null) {
				this.verifyConfirmationForLampiranA3(outcome);
			}
			/* #29007 End Vinod */
			
			this.getManagedBean().setAction(outcome);
			 		
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			// #26770 - Start.
			validateReportAttachment();
			// #26770 - End.
			RequestContext.getCurrentInstance().execute("loaApprovalPopUp.show()");
		} 
			else {
				// #29007 Start Vinod
				checkAndInsertItemSpecOffer();
				// #29007 End Vinod
				updateBpmTaskAction(outcome);
			}
		}
		//20190405@asyrafhaziq - CR25062 - End
	/**
	 * Update bpm task action. update bpm - save data into database and BPM
	 * process will route to the next respective user. Authenticating gpki
	 * before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateBpmTaskAction(String outcome) {
		debugLogger("updateBpmTaskAction");
		this.getManagedBean().setAction(outcome);
		validateLOADetails(this.getManagedBean().getScLoa(), outcome);
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			if (isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER.value())) {
				this.getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			// validate spec questions
			validateSpecQuestions();
			
			//gpki for only govt seller
			if(isDigiSigningRequired(RoleCode.GOVT_SELLER.value())) {
				this.getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");

			}  else {
				// spki
				getManagedBean().getSpkiBackingBean().getChallengeQuestions();
				RequestContext.getCurrentInstance().execute("spki_dlg.show();");
			}
			
		} else if (BpmOutcomeConst.BPM_SUBMIT.equalsIgnoreCase(outcome)) {
			submitAction();
		}
		else {
			updateBPMTask(outcome, null);
		}
	}

	/**
	 * Spki sign action.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Update bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 * @return AwardSupplierResDTO
	 */
	private DirectAwardResDTO updateBPMTask(String outcome, PmDigiSign digiSign) {
		debugLogger("updateBPMTask");
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		DirectAwardResDTO resDTO = null;
		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();
			ScLoaWitness loaSupplierWitness = this.getManagedBean().getLoaSupplierWitness();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
					.equals(statusId)) {
				validateSupplierWitnessDetails(scLoa, loaSupplierWitness, outcome);
			}
			parseLOADetails(scLoa, outcome, reqDTO);
			validateLOADetails(scLoa, outcome);
			validateContractAmount(scLoa.getLoaAmt(), outcome);
			// if reject, remove the new witness
			if (BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
				ScLoaWitness scLoaWitness = null;
				Iterator<ScLoaWitness> it = scLoa.getScLoaWitnesses()
						.iterator();
				while (it.hasNext()) {
					scLoaWitness = it.next();
					if (scLoaWitness.isBrandNew()
							&& null == scLoaWitness.getWitnessName()) {
						it.remove();
					}
				}
				//Bug #24713
				if(this.getManagedBean().getLoaSupplierWitness() != null 
						&& getManagedBean().getScLoa().getScLoaWitnesses() != null) {

					getManagedBean().getScLoa().getScLoaWitnesses().remove(
							this.getManagedBean().getLoaSupplierWitness());					
					this.getManagedBean().setLoaSupplierWitness(null);
				}
			}

			// spki
			if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
				String originalText = prepareOriginalText(scLoa.getScLoiLoa()
						.getSmSupplier());
				byte[] signedData = getManagedBean().getSpkiBackingBean()
						.spkiSignTransaction(originalText);
				digiSign.setSpkiSignedData(signedData);

				if (null != digiSign) {
					updateSpkiDigiSignData(digiSign,
							ParameterConst.DOCTYPE_DIRECT_NEGO,
							scLoa.getLoaId(), scLoa.getLoaNo(), originalText);
				}
				
				RequestContext.getCurrentInstance().execute(
						"blockWidget.hide();");
			} else {
				// gpki
				if (null != digiSign) {
					updatePmDigiSignData(digiSign,
							ParameterConst.DOCTYPE_DIRECT_NEGO,
							scLoa.getLoaId(), scLoa.getLoaNo());
				}
			}

			// #26474 -start-
			ScQt scQt = this.getManagedBean().getScQt();

			reqDTO.setScQt(scQt);
			reqDTO.setLanguageCode(CommonConstants.LANGUAGE_CODE_MALAY);
			// #26474 -end-

			reqDTO.setScLoa(scLoa);
			reqDTO.setSourceId(getManagedBean().getSourceId());
			reqDTO.setPayLoad(getManagedBean().getPayLoad());
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(new FacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setDigiSignData(digiSign);
			reqDTO.setRoleCode(getUserRoleCode());
			reqDTO.setScQtDoc(this.getManagedBean().getProdscQtDoc());
			reqDTO.setServiceQtDoc(this.getManagedBean().getServicescQtDoc());
			updateToBeDeleteSpecAnswersTOReq(reqDTO);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setTobeDeleteProPayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			reqDTO.setToBeDeleteschedules(this.getManagedBean()
					.getToBeRemoveSchedules());
			//Bug #24769
			reqDTO.setToBeDeltedZoneCriteria(this.getManagedBean()
					.getToBedeletedSupplierZone());
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			resDTO = getQtDirectAwardSO().updateBpmTask(reqDTO);
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();

				String messageCode = null;
				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId) || 
						QTStatusConst.LOA_PENDING_REVISION_APPROVAL.equals(statusId)) {
					if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700007;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					} else if (BpmOutcomeConst.BPM_REVISE
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700009;
					}
					
				} else if (QTStatusConst.LOA_PENDING_REVISION.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
					
				} else if (QTStatusConst.LOA_PENDING_CONTRACT_SIGNING
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700026;
					
				} else if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(statusId)) {
					if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700011;
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700008;
					}
					
				} else if (QTStatusConst.LOA_PENDING_VERIFICATION
						.equals(statusId)) {
					messageCode = MessageConst.INFO_700001;
					
				}else if (QTStatusConst.LOA_PENDING_SUBMISSION
						.equals(statusId)) {
					if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
						messageCode = MessageConst.INFO_700010;
					}
				}
				successMessage(messageCode, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_LETTER_OF_AWARD,
						scLoa.getLoaNo(), Boolean.TRUE);
			}

		} finally {
			// qtAwardSO = null;
		}

		return resDTO;
	}

	/**
	 * Gpki sign action. Authenticating gpki before update bpm.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		updateBPMTask(this.getManagedBean().getAction(), digiSign);
	}

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";

		if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null
				&& smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().size() > 0) {
			List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses();

			for (SmCompanyAddress compaAddress : companyAddress) {
				if (compaAddress.getAddressType().equalsIgnoreCase(
						AddressType.CONTACT_ADDRESS.value())) {
					countryCode = compaAddress.getSmAddress().getPmCountry()
							.getCountryCode();
					break;
				}
			}
		}
		sb.append(getManagedBean().getScLoa().getLoaNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|").append(smSupplier.getRegNo()).append("|")
				.append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}

	/**
	 * Ministry dropdown list.
	 *
	 * @return the list
	 */
	public List<PmOrgProfile> ministryDropdownList() {
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

		reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		// reqDTO.setParentOrgProfileId(parentOrgProfileId);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(reqDTO);
		return resDTO.getOrgProfileList();
	}

	/**
	 * Generate declaration from velocity.
	 * 
	 * @param scLoa
	 *            the sc loa
	 * @param scQt
	 *            the sc qt
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	private String generateDeclarationFromVelocity(ScLoa scLoa, ScQt scQt)
			throws Exception {
		PmUser pmUser = scLoa.getAckBy();
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		VelocityContext context = new VelocityContext();
		//#32738 & #32739 - start
		String procurementMode = "";
		String title = scQt.getQtTitle(); // #32648 remove uppercase
		//#32738 & #32739 - end

		if (title == null) {
			title = "";
		}
		title = title.trim();

		int countTitle = title.length(); //#32738 & #32739 spacing

		title = title.replaceAll("<", "&#60;");
		title = title.replaceAll(">", "&#62;");
		title = title.replaceAll("\r\n", "<br />");

		// #32738, #32739 -start-
		context.put("title", title + "<br />");
		context.put("countTitle", countTitle);

		String userName = "&#60;Nama Wakil Syarikat&#62;";
		// #32738 & #32739 spacing -end-
		String icNo = "&#60;No. Kad Pengenalan&#62;";
		if (pmUser != null) {
			userName = pmUser.getUserName();
			icNo = pmUser.getIdentificationNo();
		}
		context.put("userName", userName);
		context.put("icNo", icNo);

		context.put("supplierName", scLoiLoa.getSupplierName());
		// show (MOF/PKK/CIDB/ROS/ROC/ROB) in declaration.
		if (scLoiLoa.getSmSupplier() != null) {
			SmSupplier smSupplier = scLoiLoa.getSmSupplier();
			if (smSupplier.getSmMofAccounts() != null
					&& smSupplier.getSmMofAccounts().size() > 0) {
				context.put("mofNo", smSupplier.getSmMofAccounts().get(0)
						.getMofNo());
			} else {
				context.put("mofNo", smSupplier.getRegNo());
			}
		}
		context.put("agency", pmOrgProfile.getOrgName());
		
		String qtNo = "&#60;Quotation/Tender No.&#62;";		 
		if(scQt.getQtNo() != null){
			qtNo = scQt.getQtNo();
		}
		context.put("qtNo", qtNo);
		
		// #32648 populate lampiran 7 if LOA at supplier - start
		int isLoa = 0;
		if (null != pmUser) {
			isLoa = 1;
		}

		context.put("isSupplierLoa", isLoa);
		// #32648 populate lampiran 7 if LOA at supplier - end

		String loaNo = "&#60;LOA No.&#62;";
		if(scLoa.getLoaNo() != null){
			loaNo = scLoa.getLoaNo();
		}
		context.put("loaNo", loaNo);
		context.put("procurementMode", procurementMode.toLowerCase());

		String termsConditions = DocumentGenerator
				.getInstance()
				.getHtmlFromVelocity(SourcingConstants.LOA_DECLARATION, context);

		getManagedBean().setTermsConditions(termsConditions);

		return termsConditions;
	}

	/**
	 * Generate contract summary pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	public void generateContractSummaryPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {

		populateAttachmentDetails(attachment);

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		VelocityContext context = new VelocityContext();

		context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
		context.put("docNo", "");
		context.put("title", scLoiLoa.getTitle());
		context.put("supplierName", scLoiLoa.getSupplierName());
		context.put("loaAmt", amountDF.format(scLoa.getLoaAmt()));
		context.put("fulfilmentType",
				getParameterDesc(scLoiLoa.getFulfilmentTypeCode()));

		StringBuilder deductionTypes = new StringBuilder();
		List<ScQtDeduction> scQtDeductions = scLoa.getScQtDeductions();
		if (scQtDeductions != null) {
			for (ScQtDeduction scQtDeduction : scQtDeductions) {
				if (deductionTypes.length() > 0) {
					deductionTypes.append(",");
				}
				deductionTypes.append(getParameterDesc(scQtDeduction
						.getDeductionType()));
			}
		}
		context.put("deductionTypes", deductionTypes.toString());

		context.put("contractDuration", scLoa.getContractDuration());

		if (null == scLoa.getContractEffDate()) {
			context.put("contractEffDate", "");
		} else {
			context.put("contractEffDate",
					dateSDF.format(scLoa.getContractEffDate()));
		}

		if (null == scLoa.getContractExpDate()) {
			context.put("contractExpDate", "");
		} else {
			context.put("contractExpDate",
					dateSDF.format(scLoa.getContractExpDate()));
		}

		String bondAmtStr = "";
		if (scLoa.getBondAmt() != null) {
			bondAmtStr = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmtStr);

		String gstAmount = "";
		String overallctAmt = "";
		BigDecimal loaAmount = scLoa.getLoaAmt();
		if (scLoa.getGstAmt() != null) {
			gstAmount = amountDF.format(scLoa.getGstAmt());
			overallctAmt = amountDF.format(loaAmount.add(scLoa.getGstAmt()));
		} else {
			overallctAmt = amountDF.format(scLoa.getLoaAmt());
		}
		context.put("gstAmt", gstAmount);
		context.put("overallctAmt", overallctAmt);
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_CONTRACT_SUMMARY, context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generate agreement letter pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @param scQt
	 *            the sc qt
	 * @throws Exception
	 *             the exception
	 */
	public void generateAgreementLetterPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa, ScQt scQt) throws Exception {
		populateAttachmentDetails(attachment);
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY,
				CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		PmOrgProfile pmOrgProfile = scLoiLoa.getPmOrgProfile();
		PmUser ackByUser = scLoa.getAckBy();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		VelocityContext context = new VelocityContext();

		FacesScopes fs = new FacesScopes();
		String hostUrl = fs.getRequest().getServerName() + ":"
				+ fs.getRequest().getServerPort();
		context.put("hostUrl", hostUrl);

		IAddressSO addressSO = null;
		PmAddressType pmAddressType = null;
		IOrgProfileSO orgProfileSO = null;
		try {
			addressSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);

			AddressReqDTO reqDTO = new AddressReqDTO();
			reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);

			if (resDTO.getAddressTypeList().size() > 0) {
				pmAddressType = resDTO.getAddressTypeList().get(0);
			}

			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
			orgProfileReqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
			OrgProfileResDTO orgProfileResDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(orgProfileReqDTO);

			if (orgProfileResDTO.getPmOrgProfile() != null) {
				context.put("agency", orgProfileResDTO.getPmOrgProfile()
						.getOrgName());
				context.put("ministry", orgProfileResDTO.getPmOrgProfile()
						.getParentOrgProfile().getOrgName());
			}

		} finally {
			addressSO = null;
			orgProfileSO = null;
		}

		String agencyAddress = "";
		String agencyPhoneNo = "";
		String agencyFaxNo = "";
		if (pmAddressType != null) {
			PmAddress pmAddress = pmAddressType.getPmAddress();

			agencyAddress = getFullAddress(pmAddress);
			agencyAddress = agencyAddress.replaceAll("\r\n", "<br />");
			agencyPhoneNo = pmAddress.getPhoneNo();
			agencyFaxNo = pmAddress.getFaxNo();
		}
		context.put("agencyAddress", agencyAddress);
		context.put("agencyPhoneNo", agencyPhoneNo);
		context.put("agencyFaxNo", agencyFaxNo);
		context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
		context.put("loaNo", scLoa.getLoaNo());

		String issueDate = "";
		if (scLoa.getIssueDate() != null) {
			issueDate = DateUtils.getLocalizeDate(scLoa.getIssueDate(),
					myLocale);
		}
		context.put("issueDate", issueDate);
		
		// 26770 - Start fix sonarlint
		String headerTitle = constructSSTHeaderTitle(scLoa);
		context.put(SSTConstant.HEADER_TITLE, headerTitle);
		context.put(SSTConstant.SUBJECT_TITLE, headerTitle.toLowerCase());
		// #26770 -end-

		context.put("supplierName", scLoiLoa.getSupplierName());
		String supplierAddress = "";
		String supplierEmail = "";
		String supplierPhoneNo = "";
		String supplierFaxNo = "";
		String supplierMobNo = "";
		if (scLoa.getScSupplierAddress() != null) {
			supplierEmail = scLoa.getScSupplierAddress().getContactEmail();
			if (supplierEmail == null) {
				supplierEmail = "";
			}
			
			supplierPhoneNo = scLoa.getScSupplierAddress().getPhoneFull();
			if (supplierPhoneNo == null) {
				supplierPhoneNo = "";
			}
			
			supplierFaxNo = scLoa.getScSupplierAddress().getFaxFull();
			if (supplierFaxNo == null) {
				supplierFaxNo = "";
			}
			
			supplierMobNo = scLoa.getScSupplierAddress().getContactMobileFull();
			if (supplierMobNo == null) {
				supplierMobNo = "";
			}
			
			supplierAddress = getFullAddress(scLoa.getScSupplierAddress());
			supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
		}
		context.put("supplierAddress", supplierAddress);
		context.put("supplierEmail", supplierEmail);
		context.put("supplierPhoneNo", supplierPhoneNo);
		context.put("supplierFaxNo", supplierFaxNo);
		context.put("supplierMobNo", supplierMobNo);

		String procurementMode = "";
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
		reqDTO.setLanguageCode(CommonConstants.LANGUAGE_CODE_MALAY);
		reqDTO.setParameterCode(scLoa.getProcurementMode().getParameterCode());
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterResDTO resDTO = parameterSO
				.retrieveParameterDescListByType(reqDTO);

		List<PmParameterDesc> procurementModes = resDTO.getParameterDescList();
		if (procurementModes != null && procurementModes.size() > 0) {
			procurementMode = procurementModes.get(0).getCodeName();
		}
		context.put("procurementMode", procurementMode);
		context.put("s1ors2", procurementMode);

		String title=	scQt.getQtTitle();
		if (title == null) {
			title = "";
		}
		title = title.trim();
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		//title=title.replaceAll("&", "&#38;");
		title=title.replaceAll("<", "&#60;");
		title=title.replaceAll(">", "&#62;");
		title = title.replaceAll("\r\n", "<br />");
		context.put("title", title);

		context.put("hasLoi", false); 
		context.put("bondReq", scLoa.isBondReq());

		String bondAmt = "";
		String bondAmtText = "";
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
			bondAmtText = NumberWordUtil.convert(scLoa.getBondAmt().toString());
		}
		context.put("bondAmt", bondAmt);
		context.put("bondAmtText", bondAmtText);
	
		String bondPercentage = "";
		String bondPercentageText = "";
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage());
			bondPercentageText  = NumberWordUtil.convert(scLoa.getBondPercentage().toString());
		}
		context.put("bondPercentage", bondPercentage);
		context.put("bondPercentageText", bondPercentageText); 
		
		
		String bondExpDate = "";
		//bond exp date should be one year after the end of the contract period
		if(scLoa.getContractExpDate() != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoa.getContractExpDate());
			cal.add(Calendar.YEAR, 1); //add one year
			bondExpDate = getLocalizeDate(cal.getTime(), myLocale);
		} 
		context.put("bondExpDate", bondExpDate); 

		// bond response due days - 30
		context.put("bondResDueDays", 30);

		String ackDate = "";
		if (scLoa.getAckDate() != null) {
			ackDate = DateUtils.getLocalizeDate(scLoa.getAckDate(), myLocale);
		}
		context.put("ackDate", ackDate);

		context.put("insWarrantyReq", scLoa.isInsWarrantyReq());

		String contractEffDate = "";
		if (scLoa.getContractEffDate() != null) {
			contractEffDate = DateUtils.getLocalizeDate(
					scLoa.getContractEffDate(), myLocale);
		}
		context.put("contractEffDate", contractEffDate);

		String LoaSigner = "";
		String loaSignerDesig = "";
		if(getManagedBean().getContractSignerUser() != null){
			PmUser contractSigner = getManagedBean().getContractSignerUser();
			if(StringUtils.isNotEmpty(contractSigner.getUserName())){
				LoaSigner = contractSigner.getUserName();
			}
		}
		context.put("loaSigner", LoaSigner);
		
		loaSignerDesig = scLoa.getLoaSignerDesig();
		 if(StringUtils.isNotEmpty(loaSignerDesig) && loaSignerDesig.length()>86){
			loaSignerDesig = splitLongLengthString(loaSignerDesig, 86);
		}
		context.put("loaSignerDesignation", loaSignerDesig);

		// Supplier user details
		String supplierUser = "";
		String supplierIcNo = "";
		String supplierDesignation = "";
		if (ackByUser != null) {
			supplierUser = ackByUser.getUserName();
			supplierIcNo = ackByUser.getIdentificationNo();
			supplierDesignation = ackByUser.getDesignation();
		}
		context.put("supplierUser", supplierUser);
		context.put("supplierIcNo", supplierIcNo);
		context.put("supplierDesignation", supplierDesignation);

		context.put("loiContentFlag", false);

		// Supplier Witness
		String supplierWitnessUser = "";
		String supplierWitnessIcNo = "";
		String supplierWitnessDesignation = "";
		String supplierWitnessAddress = "";
		String supplierWitnessPhoneNo = "";
		String supplierWitnessFaxNo = "";
		String supplierWitnessMobNo = "";
		String supplierWitnessEmail = "";
		ScLoaWitness supplierWitness = null;
		if(scLoa.getScLoaWitnesses() !=null){
			for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
				String partyType = scLoaWitness.getPartyType();

				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
					supplierWitness = scLoaWitness;

					break;
				}
			}
		}
		if (supplierWitness != null) {
			supplierWitnessUser = supplierWitness.getWitnessName();
			supplierWitnessIcNo = supplierWitness.getIdentificationNo();
			supplierWitnessDesignation = supplierWitness.getDesignation();
			supplierWitnessAddress = supplierWitness.getAddress();
			supplierWitnessPhoneNo = supplierWitness.getPhoneNo();
			supplierWitnessFaxNo = supplierWitness.getFaxNo();
			supplierWitnessMobNo = supplierWitness.getMobileNo();
			supplierWitnessEmail = supplierWitness.getEmail();
		}
		context.put("supplierWitnessUser", supplierWitnessUser);
		context.put("supplierWitnessIcNo", supplierWitnessIcNo);
		context.put("supplierWitnessDesignation", supplierWitnessDesignation);
		context.put("supplierWitnessAddress", supplierWitnessAddress);
		context.put("supplierWitnessPhoneNo", supplierWitnessPhoneNo);
		context.put("supplierWitnessFaxNo", supplierWitnessFaxNo);
		context.put("supplierWitnessMobNo", supplierWitnessMobNo);
		context.put("supplierWitnessEmail", supplierWitnessEmail);

		// Bank account details.
		String bankAccNo = scLoa.getBankAccNo();
		if (bankAccNo == null) {
			bankAccNo = "";
		}
		context.put("bankAccNo", bankAccNo);
		String bankName = scLoa.getBankName();
		if (bankName == null) {
			bankName = "";
		}
		context.put("bankName", bankName);
		String bankBranch = scLoa.getBankBranch();
		if (bankBranch == null) {
			bankBranch = "";
		}
		context.put("bankBranch", bankBranch);
		String bankAddress = "";
		if (scLoa.getScBankAddress() != null) {
			bankAddress = getFullAddress(scLoa.getScBankAddress());
			bankAddress = bankAddress.replaceAll("\r\n", "<br />");
			//bankAddress = bankAddress.replaceAll("&", "&#38;");
		}
		context.put("bankAddress", bankAddress);

		// Setting GST info
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
		String gstRegFlag = "false";
		if (supplier != null
				&& supplier.getGstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE) {
			gstRegFlag = "true";
		}
		context.put("gstRegFlag", gstRegFlag);

		//Enhancement #24259 #24269
		setSstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
//		setGstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);

		//26130 - start
		if(scQt != null && (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType())
				|| ParameterConst.DOCTYPE_DIRECT_NEGO.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode()) 
					|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scQt.getFulfilmentType().getParameterCode()))){
			TplSpecMaster productSpecMaster = getManagedBean().getProdTemplateSpecBean().getSpecMaster();
			TplSpecMaster serviceSpecMaster = getManagedBean().getSpecTemplateSpecBean().getSpecMaster();
			
			List<TplSpecMaster> specMasters = null;
			
			if (null != productSpecMaster || null != serviceSpecMaster) {
				specMasters = new ArrayList<>();
				
				if (null != productSpecMaster) {
					specMasters.add(productSpecMaster);
				}
				if (null != serviceSpecMaster) {
					specMasters.add(serviceSpecMaster);
				}
			}
			
			constructSupplierSpecificationAndRemarks(context, scLoa, specMasters);
			
			// scLoa
			context.put("scLoa", scLoa);
			
			if(scLoa.getProcurementTypeCatId() != null && 
					scLoa.getProcurementTypeCatId().getParameterCode().equals(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY)){
				DocumentGenerator.getInstance().generatePdfFromVelocity(
						SourcingConstants.LOA_AGREEMENT_LETTER_PRODUCT, context,
						new FileOutputStream(getActualFilePath(attachment)));
			} else {
				DocumentGenerator.getInstance().generatePdfFromVelocity(
						SourcingConstants.LOA_AGREEMENT_LETTER_SERVICE, context,
						new FileOutputStream(getActualFilePath(attachment)));
			}
		}
		//26130 - end
		
	}

//	/**
//	 * @param context
//	 * @param scLoa
//	 * @param amountDF
//	 * @param myLocale
//	 */
//	private void setGstInfoForAgreementLetter(VelocityContext context,
//			ScLoa scLoa, DecimalFormat amountDF, Locale myLocale) {
//
//		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo()
//				: "______________";
//		context.put("gstRegNo", gstRegNo);
//
//		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
//				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : "______________";
//		context.put("gstEffectiveDate", gstEffectiveDate);
//
//		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
//				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "______________";
//		context.put("gstDeclaredDate", gstDeclaredDate);
//
//		String offerPrice = "______________";
//		String offerPriceWording = "______________";
//		BigDecimal loaAmount = scLoa.getLoaAmt();
//		if (loaAmount != null) {
//			offerPrice = amountDF.format(loaAmount);
//			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil
//					.convert(loaAmount.toString()));
//		}
//		context.put("offerPrice", offerPrice);
//		context.put("offerPriceWording", offerPriceWording);
//
//		String gstAmount = MALAY_NA;
//		String gstAmountWording = MALAY_NA;
//		String overallctAmt = "______________";
//		String overallctAmtWording = "______________";
//		BigDecimal gstAmt = scLoa.getGstAmt();
//		if (gstAmt != null) {
//			gstAmount = amountDF.format(gstAmt);
//			gstAmountWording = WordUtils.capitalizeFully(NumberWordUtil
//					.convert(gstAmt.toString()));
//			overallctAmt = amountDF.format(loaAmount.add(gstAmt));
//			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil
//					.convert(loaAmount.add(gstAmt).toString()));
//		} else {
//			overallctAmt = amountDF.format(loaAmount);
//			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil
//					.convert(loaAmount.toString()));
//		}
//		context.put("gstAmt", gstAmount);
//		context.put("gstAmtWording", gstAmountWording);
//		context.put("overallCtAmt", overallctAmt);
//		context.put("overallCtAmtWording", overallctAmtWording);
//
//	}
	
	/**
	 * @param context
	 * @param scLoa
	 * @param amountDF
	 * @param myLocale
	 */
	private void setSstInfoForAgreementLetter(VelocityContext context,
			ScLoa scLoa, DecimalFormat amountDF, Locale myLocale) {

		// 26770 start
		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo() : SSTConstant.UNDERLINE;
		context.put(SSTConstant.GST_REG_NO, gstRegNo);

		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils.getLocalizeDate(scLoa.getGstEffDate(),
				myLocale) : SSTConstant.UNDERLINE;
		// #26770 -end-
		context.put("gstEffectiveDate", gstEffectiveDate);

		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "______________";
		context.put("gstDeclaredDate", gstDeclaredDate);

		String offerPrice = SSTConstant.UNDERLINE; // #26770
		String offerPriceWording = "______________";
		BigDecimal loaAmount = scLoa.getLoaAmt();
		
		if (loaAmount != null) {
			offerPrice = amountDF.format(loaAmount);
			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil
					.convert(loaAmount.toString()));
		}
		context.put("offerPrice", offerPrice);
		context.put("offerPriceWording", offerPriceWording);

		String sstAmount = MALAY_NA;
		String overallctAmt = "______________";
		String overallctAmtWording = "______________";
			
		BigDecimal sstAmt = scLoa.getSstAmt();
				
		if (sstAmt != null && loaAmount != null ) { //26770 
			sstAmount = amountDF.format(sstAmt);
			overallctAmt = amountDF.format(loaAmount.add(sstAmt));
			log.info(":::setSstInfoForAgreementLetter(). loaId: "+ scLoa.getLoaId() +". overallctAmt inc. sstAmt: "+ overallctAmt);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.add(sstAmt).toString()));						
		} else if (loaAmount != null){//26770
			overallctAmt = amountDF.format(loaAmount);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil.convert(loaAmount.toString()));
		}
		
		context.put("sstAmt", sstAmount);
		context.put("overallCtAmt", overallctAmt);
		context.put("overallCtAmtWording", overallctAmtWording);

	}
	//end Enhancement #24259 #24269
	
	/**
	 * @param context
	 * @param scLoa
	 * @param myLocale
	 */
	private void setMofInfoForAgreementLetter(VelocityContext context,
			ScLoa scLoa, Locale myLocale) {

		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		SmSupplier smSupplier = scLoiLoa.getSmSupplier();
		Long supplierId = smSupplier.getSupplierId();
		// #25025 - Start.
		if (StringUtils.isNotEmpty(scLoiLoa.getMofNo())) {
			context.put("mofNo", scLoiLoa.getMofNo());
		} else {
			context.put("mofNo", "");
		}
		// #25025 - End.
		
		SmMofAccount mofAccount = getMofAccountBySupplierId(supplierId);
		
		String mofValidityDuration = MALAY_NA;
		String mofCertValidityDuration = MALAY_NA;
		String isBumi = MALAY_NA;
		String kodBidang = MALAY_NA;
		
		if(mofAccount != null) {
			String mofEffectiveDate = null;
			String mofExpiredDate = null;
			if(mofAccount.getEffDate() != null) {
				mofEffectiveDate = DateUtils
						.getLocalizeDate(mofAccount.getEffDate(), myLocale);
			}
			if(mofAccount.getExpDate() != null) {
				mofExpiredDate = DateUtils
						.getLocalizeDate(mofAccount.getExpDate(), myLocale);
			}
			
			if(mofEffectiveDate != null && mofExpiredDate != null) {
				mofValidityDuration = new StringBuilder().append(mofEffectiveDate).
						append(" - ").append(mofExpiredDate).toString();
			}
			
			SmMofCert mofCert = getMofCertBySupplierId(mofAccount.getMofAccountId());
			
			String mofCertEffectiveDate = null;
			String mofCertExpiredDate = null;
			if(mofCert != null){
				if(mofCert.getEffDate() != null) {
					mofCertEffectiveDate = DateUtils
							.getLocalizeDate(mofCert.getEffDate(), myLocale);
				}
				if(mofCert.getExpDate() != null) {
					mofCertExpiredDate = DateUtils
							.getLocalizeDate(mofCert.getExpDate(), myLocale);
				}
			}
			
			if(mofCertEffectiveDate != null && mofCertExpiredDate != null) {
				mofValidityDuration = new StringBuilder().append(mofCertEffectiveDate).
						append(" - ").append(mofCertExpiredDate).toString();
			}
			
			if(smSupplier.isBumi()) {
				isBumi = SSTConstant.BUMIPUTERA; // #26770
			} else {
				isBumi = SSTConstant.BUKAN_BUMIPUTERA; // #26770
			}
			// #26770 new kod bidang
			if ((smSupplier.getSmAppl() != null && smSupplier.getSmAppl().getSmSupplierCategories() != null)
					&& !smSupplier.getSmAppl().getSmSupplierCategories().isEmpty()) {
				StringBuilder categoryCode = constructCategoryCodeForSST(smSupplier);
				kodBidang = StringUtils.removeEnd(categoryCode.toString(), SSTConstant.SEPARATOR);
			}
			// 26770 - end
		}
		context.put("mofValidityDuration", mofValidityDuration);
		context.put("mofCertValidityDuration", mofCertValidityDuration);
		context.put("isBumi", isBumi);		
		context.put("kodBidang", kodBidang);
	}
	
	public void setMinSL1MParticipant(VelocityContext context,
			ScLoa scLoa, DecimalFormat integerDF){
		
		BigDecimal loaAmount = scLoa.getLoaAmt();
		BigDecimal gstAmt = scLoa.getGstAmt();
		BigDecimal value = new BigDecimal(18000);
		BigDecimal hundred = new BigDecimal(100);
		BigDecimal totalAmount = new BigDecimal(0);
		
		String minSL1M = "........................";
		if(gstAmt != null){
			totalAmount = loaAmount.add(gstAmt);
		}else{
			totalAmount = loaAmount;
		}
		minSL1M = integerDF.format(((totalAmount)
				.divide(hundred))
				.divide(value,0, BigDecimal.ROUND_HALF_UP));
		context.put("minSL1M", minSL1M);
	}

	/**
	 * Generate declaration pdf from velocity in Performance Bond.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	public void generateDeclarationPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception {
		String termsConditions = generateDeclarationFromVelocity(scLoa,
				getManagedBean().getScQt());

		populateAttachmentDetails(attachment);

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdf(fos, termsConditions);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Save schedule list.
	 */
	/*
	private void saveScheduleList() {
		List<ScQtSchedule> freqschedule = this.getManagedBean().getScLoa()
				.getScQtSchedules();
		List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
		if (freqschedule != null && !freqschedule.isEmpty()) {
			for (ScQtSchedule schedule : freqschedule) {
				Date currentDate = new Date();
				SimpleDateFormat df = new SimpleDateFormat(
						CommonConstants.DEFAULT_DATE_FORMAT);
				String localDate = df.format(currentDate);
				String day = localDate.substring(0, 2);
				String fromdate = day + "/" + schedule.getStartMonth() + "/"
						+ schedule.getStartYear();
				String toDate = day + "/" + schedule.getEndMonth() + "/"
						+ schedule.getEndYear();

				schedule.setScheduleNo(schedule.getScheduleNo());

				try {
					if (df.parse(fromdate).after(df.parse(toDate))) {
						throw new EpException(MessageConst.ERROR_715011);
					} else {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					}
				} catch (ParseException e) {
					e.printStackTrace();
					throw new EpException(e.toString());
				}
				if (schedule.getScLoa() == null) {
					schedule.setScLoa(this.getManagedBean().getScLoa());
				}
				localScheduleList.add(schedule);
			}
			getManagedBean().getScLoa().setScQtSchedules(localScheduleList);
		} else {
			getManagedBean().getScLoa().setScQtSchedules(null);
		}
	}
	*/

	/**
	 * Generate frequency schedule pdf from velocity.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	/*
	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {
		debugLogger("generateFrequencySchedulePdfFromVelocity");
		populateAttachmentDetails(attachment);
		VelocityContext context = new VelocityContext();

		String frequencyScheduleType = "";
		if (scQt.getScheduleType() != null) {
			frequencyScheduleType = scQt.getScheduleType();
		}
		context.put("frequencyScheduleType", frequencyScheduleType);

		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		IParameterSO parameterSO = null;
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		Date currentDate = new Date();
		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();
		List<ScQtSchedule> scheduleReportList = new ArrayList<ScQtSchedule>();
		ScQtSchedule reportSchedule = null;
		PmParameter scheduleType =  null;
		int totalFrequency = 0;
		if (scheduleList != null && scheduleList.size() > 0) {
			totalFrequency = scheduleList.size();
			for (ScQtSchedule schedule : scheduleList) {
				try {
					reportSchedule = new ScQtSchedule();
					if(StringUtils.isNotBlank(schedule.getStartMonth()) 
							&& StringUtils.isNotBlank(schedule.getStartYear())){
						String localDate = df.format(currentDate);
						String day = localDate.substring(0, 2);
						String fromdate = day + "/" + schedule.getStartMonth() + "/"
								+ schedule.getStartYear();
						schedule.setFromDate(df.parse(fromdate));
						
						reportSchedule.setStartMonth(monthFormat.format(schedule
								.getFromDate()));
						reportSchedule.setStartYear(yearFormat.format(schedule
								.getFromDate()));
					} 
					if(StringUtils.isNotBlank(schedule.getEndMonth()) && 
							StringUtils.isNotBlank(schedule.getEndYear())){
						String localDate = df.format(currentDate);
						String day = localDate.substring(0, 2);
						String toDate = day + "/" + schedule.getEndMonth() + "/"
								+ schedule.getEndYear();
						schedule.setToDate(df.parse(toDate));
						
						reportSchedule.setEndMonth(monthFormat.format(schedule
								.getToDate()));
						reportSchedule.setEndYear(yearFormat.format(schedule
								.getToDate()));
					} 
					if(scheduleType ==  null){
						parameterSO = BusinessDelegate.getInstance()
								.getServiceObject(
										ModuleCodeConst.PROFILE_MANAGEMENT,
										IParameterSO.class);
						ParameterReqDTO reqDTO = new ParameterReqDTO();
						reqDTO.setParameterId(schedule.getScheduleType()
								.getParameterId());
						ParameterResDTO resDTO = parameterSO
								.retrieveParameterById(reqDTO);
						scheduleType  = resDTO.getPmParameter();
					}
					reportSchedule.setScheduleType(scheduleType);
					reportSchedule.setScheduleNo(schedule.getScheduleNo());
					reportSchedule.setTotalMonths(schedule.getTotalMonths());
					scheduleReportList.add(reportSchedule);

				} finally {
					parameterSO = null;
				}
			}
			context.put("totalFrequency", totalFrequency);
			context.put("scheduleList", scheduleReportList);
			context.put("bean", this);
		}
		int totalPhase = 0;
		List<ScQtProgressivePayment> progressivePayments = scLoa
				.getScQtProgPayments();
		if (null != progressivePayments && progressivePayments.size() > 0) {
			totalPhase = progressivePayments.size();
			context.put("totalPhase", totalPhase);
			context.put("paymentList", progressivePayments);

		}

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_FREQUENCY_SCHEDULE, context,
					fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/

	private List<ItemBean> convertSpecToJadualHargaBean(
			TplSpecMaster specMaster) {
		List<ItemBean> itemBeans = new ArrayList<ItemBean>();
		ItemBean itemBean = null;

		if (specMaster != null && specMaster.getTplSpecQuestions() != null) {
			List<TplSpecQuestion> specQuestions = specMaster
					.getTplSpecQuestions();

			for (TplSpecQuestion specQuestion : specQuestions) {
				if (specQuestion.getTplSpecAnswers() != null) {
					for (TplSpecAnswer specAnswer : specQuestion
							.getTplSpecAnswers()) {
						if (TemplateSpecConst.QUESTION_TYPE_ITEM
								.equals(specQuestion.getQuestionType())) {
							itemBean = mappingJadualHarga(specAnswer);

							itemBeans.add(itemBean);
						}
					}
				}
			}
		}

		return itemBeans;
	}

	/**
	 * Mapping jadual harga.
	 * 
	 * @param item
	 *            the item
	 * @return the jadual harga bean
	 */
	private ItemBean mappingJadualHarga(TplSpecAnswer specAnswer) {
		ItemBean itemBean = new ItemBean();
		TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
		TplSpecZoneDtl specZoneDtl = specAnswer.getTplSpecSuppAnswer()
				.getTplSpecZoneDtl();
		BigDecimal qty = null;
		//BigDecimal unitPrice =  null;
		
		if (specAnswer.getTplSpecZoneQty() != null) {
			qty = specAnswer.getTplSpecZoneQty().getQty();
		} else {
			qty = specQuestion.getClQuantity();
		}
		
		if(specZoneDtl != null) {
			itemBean.setZonal(true);
		}
		
		if(specAnswer.getTplSpecTierQty() != null) {
			itemBean.setTier(true);
			itemBean.setFromQty(specAnswer.getTplSpecTierQty().getFromQty().toString());
			itemBean.setToQty(specAnswer.getTplSpecTierQty().getToQty().toString());
		}

		String zoneName = specZoneDtl != null ? specZoneDtl.getZoneName() : "";
		//zoneName = zoneName.replaceAll("&", "&#38;");
		itemBean.setZoneName(zoneName);
		itemBean.setItem(specQuestion.getClItem());

		/*if(specAnswer.getRatePerUom() != null){
			 unitPrice = specAnswer.getRatePerUom().setScale(2,
					BigDecimal.ROUND_HALF_EVEN);
			 itemBean.setUnitPrice(unitPrice.toString());
		}
		itemBean.setUom(null == specQuestion.getClUom() ? "" : specQuestion.getClUom().getUomName());
		itemBean.setFreqUom(null == specQuestion.getClUomFrequency() ? ""
				: specQuestion.getClUomFrequency().toString());
		itemBean.setUomPerDay(null == specQuestion.getClUomPerDay() ? ""
				: specQuestion.getClUomPerDay().toString());
		itemBean.setUomPerMonth(null == specQuestion.getClUomPerMonth() ? ""
				: specQuestion.getClUomPerMonth().toString());
		itemBean
				.setUomPerYear(null == specQuestion.getClDurationMonth() ? ""
						: specQuestion.getClDurationMonth().toString());*/
		itemBean.setQuantity(null == qty ? "" : qty.toString());

		/*BigDecimal totalAmount = BigDecimal.valueOf(0d);
		BigDecimal subTotal = TemplateSpecHelper.getInstance()
				.calculateTotalAmount(specQuestion, unitPrice, qty);
		if (subTotal != null) {
			totalAmount = totalAmount.add(subTotal);
			totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}*/
		//itemBean.setTotalAmount(totalAmount.toString());
		return itemBean;

	}

	/**
	 * Generate jadual harga.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param productList
	 *            the product list
	 * @param serviceList
	 *            the service list
	 * @throws Exception
	 *             the exception
	 */
	/*
	private void generateJadualHarga(ScAttachment attachment, List<JadualHargaBean> productList, List<JadualHargaBean> serviceList, boolean isS1OrS2) throws Exception {
		InputStream inputStream = null;
		try {
			final String templatePath = "templates"
					+ SourcingConstants.DOCPATH_SC_LOA_JADUAL_HARGA;
			inputStream = this.getClass().getClassLoader()
					.getResourceAsStream(templatePath);
			if (inputStream != null) {
				XSSFWorkbook wb = new XSSFWorkbook(inputStream); // or new
																	// HSSFWorkbook();

				boolean emptyProductList = false;
				Sheet productSheet = null;
				Sheet serviceSheet = null;
				Row row = null;
				int rowIndex = 0;
				int cellIndex = 0;
				if (productList.size() > 0
						|| (productList.size() == 0 && serviceList.size() == 0)) {
					rowIndex = 0;
					productSheet = wb.getSheetAt(0);
					wb.setSheetName(0, "ProductSheet");
					for (JadualHargaBean product : productList) {
						rowIndex++;
						cellIndex = 0;
						row = productSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(
								product.getZone());
						row.createCell(cellIndex++).setCellValue(
								product.getItem());
						row.createCell(cellIndex++).setCellValue(
								product.getUom());
						row.createCell(cellIndex++).setCellValue(
								product.getQuantity());
						row.createCell(cellIndex++).setCellValue(
								product.getSubSequence());
						row.createCell(cellIndex++).setCellValue(
								product.getUnitPrice());
						row.createCell(cellIndex++).setCellValue(
								product.getTotalAmount());
					}
					// Auto size all the columns
					for (int x = 0; x < productSheet.getRow(0)
							.getPhysicalNumberOfCells(); x++) {
						productSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(0);
					emptyProductList = true;
				}

				if (serviceList.size() > 0
						|| (productList.size() == 0 && serviceList.size() == 0)) {
					rowIndex = 0;
					if (emptyProductList) {
						serviceSheet = wb.getSheetAt(0);
						wb.setSheetName(0, "ServiceSheet");
					} else {
						serviceSheet = wb.getSheetAt(1);
						wb.setSheetName(1, "ServiceSheet");
					}

					if (isS1OrS2) {
						Row row0 = serviceSheet.getRow(0);
						Cell cell9 = row0.getCell(9);
						cell9.setCellValue("Total Amount (RM)");
					}

					for (JadualHargaBean service : serviceList) {
						rowIndex++;
						cellIndex = 0;
						row = serviceSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(
								service.getZone());
						row.createCell(cellIndex++).setCellValue(
								service.getItem());
						row.createCell(cellIndex++).setCellValue(
								service.getUom());
						row.createCell(cellIndex++).setCellValue(
								service.getFreqUom());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerDay());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerMonth());
						row.createCell(cellIndex++).setCellValue(
								service.getUomPerYear());
						row.createCell(cellIndex++).setCellValue(
								service.getQuantity());
						row.createCell(cellIndex++).setCellValue(
								service.getUnitPrice());
						row.createCell(cellIndex++).setCellValue(
								service.getTotalAmount());

					}

					for (int x = 0; x < serviceSheet.getRow(0)
							.getPhysicalNumberOfCells(); x++) {
						serviceSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(1);
				}

				wb.setActiveSheet(0);
				FileOutputStream os = new FileOutputStream(
						getActualFilePath(attachment));
				wb.write(os);
				os.flush();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(inputStream != null) {
			inputStream.close();
		}
	}
	*/

	private List<ItemBean> convertSpecQuestionToSpecAnswer(TplSpecMaster specMaster) {
		Map<Integer, ItemBean> questionSpecMap = new HashMap<Integer, ItemBean>();

		if (specMaster != null && specMaster.getTplSpecQuestions() != null) {
			for (TplSpecQuestion specQuestion : specMaster.getTplSpecQuestions()) {
				if (specQuestion.getParentSpecQuestion() == null) {
					int order = specQuestion.getQuestionOrder();
					if (!questionSpecMap.containsKey(order)) {
						ItemBean specBean = new ItemBean();
						specBean.setItem(specQuestion.getClItem());
						specBean.setSpecs(new ArrayList<String>());
						questionSpecMap.put(order, specBean);
					}
				} else {
					int parentOrder = specQuestion.getParentSpecQuestion().getQuestionOrder();
					questionSpecMap.get(parentOrder).getSpecs().add(specQuestion.getClSpecDetail());
				}
			}
		}

		return new ArrayList<ItemBean>(questionSpecMap.values());
	}

	/**
	 * Generate Specification.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param productList
	 *            the product list
	 * @param serviceList
	 *            the service list
	 * @throws Exception
	 *             the exception
	 */
	/*
	private void generateSpecification(ScAttachment attachment, List<TplSpecAnswer> productList, List<TplSpecAnswer> serviceList) throws Exception {
		InputStream inputStream = null;
		try {

			final String templatePath = "templates" + SourcingConstants.DOCPATH_SC_LOA_SPECIFICATION;
			inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
			if (inputStream != null) {
				XSSFWorkbook wb = new XSSFWorkbook(inputStream); // or new HSSFWorkbook();
				boolean emptyProductList = false;
				Sheet productSheet = null;
				Sheet serviceSheet = null;
				Row row = null;
				int rowIndex = 0;
				int cellIndex = 0;
				if (productList.size() > 0 || (productList.size() == 0 && serviceList.size() == 0)) {
					rowIndex = 0;
					productSheet = wb.getSheetAt(0);
					wb.setSheetName(0, "ProductSheet");
					for (TplSpecAnswer answer : productList) {
						rowIndex++;
						cellIndex = 0;
						row = productSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClItem() ? "" : answer.getTplSpecQuestion().getClItem());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClSpecDetail() ? "" : answer.getTplSpecQuestion().getClSpecDetail());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUom() ? "" : answer.getTplSpecQuestion().getClUom().getUomName());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClPriceTypeCode() ? "" : getParameterDesc(answer.getTplSpecQuestion().getClPriceTypeCode()));

					}

					// Auto size all the columns
					for (int x = 0; x < productSheet.getRow(0).getPhysicalNumberOfCells(); x++) {
						productSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(0);
					emptyProductList = true;
				}

				if (serviceList.size() > 0 || (productList.size() == 0 && serviceList.size() == 0)) {
					rowIndex = 0;
					if (emptyProductList) {
						serviceSheet = wb.getSheetAt(0);
						wb.setSheetName(0, "ServiceSheet");
					} else {
						serviceSheet = wb.getSheetAt(1);
						wb.setSheetName(1, "ServiceSheet");
					}

					for (TplSpecAnswer answer : serviceList) {
						rowIndex++;
						cellIndex = 0;
						row = serviceSheet.createRow(rowIndex);
						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClItem() ? "" : answer.getTplSpecQuestion().getClItem());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClSpecDetail() ? "" : answer.getTplSpecQuestion().getClSpecDetail());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUom() ? "" : answer.getTplSpecQuestion().getClUom().getUomName());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomFrequency() ? "" : answer.getTplSpecQuestion().getClUomFrequency().toString());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomPerDay() ? "" : answer.getTplSpecQuestion().getClUomPerDay().toString());

						row.createCell(cellIndex++).setCellValue(null == answer.getTplSpecQuestion().getClUomPerMonth() ? "" : answer.getTplSpecQuestion().getClUomPerMonth().toString());

					}

					for (int x = 0; x < serviceSheet.getRow(0).getPhysicalNumberOfCells(); x++) {
						serviceSheet.autoSizeColumn(x);
					}
				} else {
					wb.removeSheetAt(1);
				}

				wb.setActiveSheet(0);
				FileOutputStream os = new FileOutputStream(getActualFilePath(attachment));
				wb.write(os);
				os.flush();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (inputStream != null) {
			inputStream.close();
		}
	}
	*/

	/**
	 * Generate negotiation result pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	/*
	private void generateNegotiationResultPdf(ScAttachment attachment) throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		ScLoa scLoa = getManagedBean().getScLoa();
		populateAttachmentDetails(attachment);
		List<ScLoi> lois = new ArrayList<ScLoi>();
		if (getManagedBean().getSuppLoiLoaMap() != null && getManagedBean().getSuppLoiLoaMap().size() > 0) {
			Map<Long, List<ScLoiLoa>> suppLoiLoaMap = getManagedBean().getSuppLoiLoaMap();
			if (scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getSmSupplier() != null) {
				List<ScLoiLoa> loiloas = suppLoiLoaMap.get(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
				if (loiloas != null && loiloas.size() > 0) {
					for (ScLoiLoa scLoiLoa : loiloas) {
						if (scLoiLoa.getScLois() != null && scLoiLoa.getScLois().size() > 0) {
							lois.addAll(scLoiLoa.getScLois());
						}
					}
				}
			}
		} else {
			if (scLoa.getScLoiLoa() != null && scLoa.getScLoiLoa().getScLois() != null && scLoa.getScLoiLoa().getScLois().size() > 0) {
				lois = scLoa.getScLoiLoa().getScLois();
			}
		}

		VelocityContext context = new VelocityContext();

		context.put("isZonal", getManagedBean().getScQt().isZonal());
		context.put("scLois", lois);
		context.put("bean", this);

		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOI_NEGOTIONATION_RESULT, context, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	*/
	
	/**
	 * Generate negotiation result pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	/*private void generateAcknowledgmentLOAReceiptPdf(ScAttachment attachment)
			throws Exception {
		attachment.setFileName("Acknowledgement_LOA_receipt.pdf");
		File file = new File("C:\\Users\\USER\\Desktop\\S1 Appendix Review and comment by Unit eP.docx");

		DocumentGenerator.getInstance().generatePdfFromFile(new FileOutputStream(getActualFilePath(attachment)), file);
	}*/

	//Enhancement #24259 #24269
//	/**
//	 * Sum of gstAmount & loa offer price
//	 * 
//	 * @param gstAmount
//	 */
//	public void calculateOverallAmount(BigDecimal gstAmount) {
//		ScLoa scLoa = getManagedBean().getScLoa();
//		BigDecimal loaAmount = scLoa.getLoaAmt();
//		BigDecimal contractOverAllAmt = BigDecimal.ZERO;
//		
//		if (loaAmount != null) {
//			contractOverAllAmt   = overallCtAmount(loaAmount, gstAmount);
//			this.getManagedBean().setOverallContractAmt(contractOverAllAmt);
//
//			calcBondAmount(scLoa, true);
//			// bond amount in words
//			if (scLoa.getBondAmt() != null) {
//				getManagedBean().setBondAmountWord(
//						NumberWordUtil.convert(scLoa.getBondAmt().toString()));
//			}
//			
//			if (null != scLoa.getContractSigner()
//					&& scLoa.getContractSigner().getUserId() != null) {
//				scLoa.setLoaSignerDesig(null);
//				scLoa.setContractSigner(null);
//				prepareSignerInfo();
//				RequestContext.getCurrentInstance().execute(
//						"gstSignerConfirmPopupWin.show()");
//			}
//		}
//	}
	
	/**
	 * Sum of sstAmount & loa offer price
	 * 
	 * @param sstAmount
	 */
	public void calculateOverallAmount(BigDecimal sstAmount) {
		ScLoa scLoa = getManagedBean().getScLoa();
		BigDecimal loaAmount = scLoa.getLoaAmt();
		BigDecimal contractOverAllAmt = BigDecimal.ZERO;
		
		if (loaAmount != null) {
			contractOverAllAmt = overallCtAmountWithSST(loaAmount, sstAmount);
			this.getManagedBean().setOverallContractAmt(contractOverAllAmt);

			calcBondAmountWithSST(scLoa, true);
			
			// bond amount in words
			// #35977 Updated for English Start Vinod
			if (scLoa.getBondAmt() != null
					&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
				getManagedBean().setBondAmountWord(NumberWordUtilEn.convert(scLoa.getBondAmt().toString()));
			} else if (scLoa.getBondAmt() != null
					&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
				getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			} else {
				getManagedBean().setBondAmountWord(null);
			}
			// #35977 End Vinod

			if (null != scLoa.getContractSigner() && scLoa.getContractSigner().getUserId() != null) {
				prepareSignerInfo();
				
				RequestContext.getCurrentInstance().execute("sstSignerConfirmPopupWin.show()");
			}
		}
	}

//	/**
//	 * Checks if is GST reg active.
//	 *
//	 * @return true, if is GST reg active
//	 */
//	public boolean isGSTRegActive() {
//
//		boolean isActive = true;
//		ScLoa scLoa = getManagedBean().getScLoa();
//		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
//		getManagedBean().setRenderRemainder(false);
//		if ((supplier != null && supplier.getGstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)) {
//			if (null != scLoa.getGstEffDate()) {
//				if (scLoa.getGstEffDate().compareTo(new Date()) <= 0) {
//					isActive = false;
//				} else {
//					getManagedBean().setRenderRemainder(isActive);
//				}
//			}
//		}
//
//		return isActive;
//	}
	
//	/**
//	 * Checks if is SST reg active.
//	 *
//	 * @return true, if is SST reg active
//	 */
//	public boolean isSSTRegActive() {
//
//		boolean isActive = true;
//		ScLoa scLoa = getManagedBean().getScLoa();
//		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
//		
//		if ((supplier != null && supplier.getSstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)) {			
//			if (null != scLoa.getSstEffDate()) {	
//				if (scLoa.getSstEffDate().compareTo(new Date()) <= 0) {
//					isActive = false;
//				}
//			}
//		}
//
//		return isActive;
//	}
	//end Enhancement #24259 #24269
	
	//Enhancement #20007: DPQT GST - Disable Field GST Amount
	public BigDecimal getGstRate() {
		if(log.isDebugEnabled()) {
			log.debug("DirectLOABackingBean.getGstRate()");
		}
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		BigDecimal gstRate = BigDecimal.ZERO;
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		RuleResDTO roleResDTO = null;
		ruleReqDTO.setRuleType(RuleTypeConst.GST);
		roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
		if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
			gstRate = new BigDecimal(roleResDTO.getRuleValueList().get(0).getRuleValue());
		} else {
			throw new EpException(MessageConst.ERROR_770222, new String[]{getRuleTypeDesc(ruleReqDTO.getRuleType())});
		}
		return gstRate;
	}

	/**
	 * Adds the loi supp doc action.
	 */
	public void addLoaSuppDocAction() {
		debugLogger("addLoiSuppDocAction");

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaSupportingDoc> scLoaSuppDocs = scLoa.getScLoaSupportingDocs();

		if (scLoaSuppDocs == null) {
			scLoaSuppDocs = new ArrayList<ScLoaSupportingDoc>();
			scLoa.setScLoaSupportingDocs(scLoaSuppDocs);
		}

		ScLoaSupportingDoc scLoaSuppDoc = new ScLoaSupportingDoc();
		scLoaSuppDoc.setScLoa(scLoa);
		// scLoaSuppDoc.setDocId(scLoa.getLoaId().toString());
		scLoaSuppDoc.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		scLoaSuppDoc
				.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOI_SUPPORTING_DOC);
		scLoaSuppDoc.setPmDocument(null);

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
			ParameterResDTO resDTO = parameterSO
					.retrieveParameterByTypeAndCode(reqDTO);
			scLoaSuppDoc.setDefinationType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

		scLoaSuppDocs.add(scLoaSuppDoc);
	}

	/**
	 * Delete supporing docs in negotiation details.
	 */
	public void deleteSupporingDocs() {
		ScLoa scLoa = getManagedBean().getScLoa();
		List<ScLoaSupportingDoc> selectedScLoiSuppDocs = getManagedBean()
				.getSelectedScLoaSuppDocs();

		if (selectedScLoiSuppDocs != null && selectedScLoiSuppDocs.size() > 0) {
			deleteRowsAction(scLoa.getScLoaSupportingDocs(),
					selectedScLoiSuppDocs);
		} else {
			throw new EpException(MessageConst.ERROR_760158);
		}
	}

	/**
	 * Handle supp doc file upload.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleSuppDocFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleSuppDocFileUpload");
		if (event.getFile() != null) {
			// String attachmentDocType =
			// getManagedBean().getAttachmentDocType();
			ScAttachment scAttachment = null;
			ScLoaSupportingDoc selectedScLoaSuppDoc = this.getManagedBean()
					.getSelectedScLoaSuppDoc();
			ScLoaSupportingDocAttachment scLoaSupportingDocAttachment = selectedScLoaSuppDoc
					.getScLoaSupportingDocAttachment();
			if (null == scLoaSupportingDocAttachment) {
				scLoaSupportingDocAttachment = new ScLoaSupportingDocAttachment();
				scLoaSupportingDocAttachment
						.setScLoaSupportingDoc(selectedScLoaSuppDoc);
				selectedScLoaSuppDoc
						.setScLoaSupportingDocAttachment(scLoaSupportingDocAttachment);
			}
			scLoaSupportingDocAttachment.setDocNo(selectedScLoaSuppDoc
					.getScLoa().getLoaNo());
			scAttachment = scLoaSupportingDocAttachment;

			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	/**
	 * Reset contract dates.
	 */
	public void resetContractDates() {
		ScLoa scLoa = this.getManagedBean().getScLoa();
		scLoa.setContractEffDate(null);
		scLoa.setContractExpDate(null);
		checkFulFilmentType(this.getManagedBean().getScLoa());
		calculateServiceTemplate();
		updatePerformanceBond(scLoa);

	}

	private void resetTplSpecEstimations(List<TplSpecQuestion> specQuestions,
			TemplateSpecHelper helper, boolean isZonal,
			TemplateSpecBean templateSpecBean) {

		if (CollectionUtils.isNotEmpty(specQuestions)) {

			for (TplSpecQuestion specQuestion : specQuestions) {
				helper.resetSpecAnswers(templateSpecBean, specQuestion);

				helper.resetEstimations(specQuestion);
			}
		}

	}
	
	private void resetTplSpecZoneQuantities(List<TplSpecQuestion> specQuestions,
											TemplateSpecHelper helper, boolean isZonal, 
											TemplateSpecBean templateSpecBean) {
		if (isZonal && CollectionUtils.isNotEmpty(specQuestions)) {
			for (TplSpecQuestion specQuestion : specQuestions) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM
						.equalsIgnoreCase(specQuestion.getQuestionType())) {
					helper.resetSpecZoneQuanties(specQuestion);
				}
			}
		}
	}	
	
	/**
	 * @param specZoneDtls
	 * @param tempZoneName
	 */
	private void removeExistingSpecZone(List<TplSpecZoneDtl> specZoneDtls,
			String tempZoneName) {

		for (int i = 0; i < specZoneDtls.size(); i++) {
			if (null != tempZoneName
					&& tempZoneName.equalsIgnoreCase(specZoneDtls.get(i)
							.getZoneName())) {
				specZoneDtls.remove(i);
				break;
			}
		}

	}

	private SupplierGstResDTO getGSTInfo(Long supplierId, Long branchCode) {

		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchCode);
			SupplierGstResDTO gstResDTO = supplierGstSO
					.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			return gstResDTO;
		} finally {
			supplierGstSO = null;
		}
	}

	private void setGSTInfo(SupplierGstResDTO resDTO, ScLoa scLoa) {

		if (resDTO.isSuccessful() && resDTO.getSmGstSupplier() != null) {
			SmGstSupplier gstSupplier = resDTO.getSmGstSupplier();
			scLoa.setGstRegNo(gstSupplier.getGstRegNo());
			scLoa.setGstDeclaredDate(gstSupplier.getGstDeclaredDate());
			scLoa.setGstEffDate(gstSupplier.getGstEffDate());
		} else {
			scLoa.setGstRegNo(null);
			scLoa.setGstDeclaredDate(null);
			scLoa.setGstEffDate(null);
		}
		scLoa.setGstAmt(null);
	}

	/**
	 * @param scLoa
	 */
	private void prepareSignerInfo() {
		if(getManagedBean().getScLoa().getScLoiLoa().getPmOrgProfile() != null) {
			ScLoa scLoa = getManagedBean().getScLoa();
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());
			if (scLoa.getLoaAmt() != null) {
				userReqDTO.setApprovalLimit(overallCtAmount(scLoa.getLoaAmt(),
						scLoa.getGstAmt()));
			}
			userReqDTO.setOrgProfileId(scLoa.getScLoiLoa().getPmOrgProfile()
					.getOrgProfileId());
			UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
			getManagedBean().setContractSignerList(userResDTO.getUserList());
			// #31704 - start
//			scLoa.setContractSigner(new PmUser());
			// #31704 - end
		}
	}
	
	/**
	 * Prepare Supplier Contract signer info.
	 */
	private void prepareSupplierSignerInfo() {

		ScLoa scLoa = getManagedBean().getScLoa();

		// Supplier Contract Signer
		PmUser supplierContractSignerUser = scLoa.getAckBy();
		if (supplierContractSignerUser == null) {
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
		}

		ISupplierSearchSO supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
		supplierReqDTO.setSupplierId(scLoa.getScLoiLoa().getSmSupplier().getSupplierId());
		supplierReqDTO.setAuthorized(true);
		List<SmPersonnel> personnels = supplierSearchSO.retrieveContractSigners(supplierReqDTO).getPersonnels();
		List<PmUser> supplierSigners = new ArrayList<PmUser>();
		for(SmPersonnel personnel : personnels) {
			supplierSigners.add(personnel.getPmUser());
		}
		getManagedBean().setSupplierContractSignerList(supplierSigners);
	}

	private void updateTplSpecMasterNDetails(boolean isZonal) {
		TemplateSpecHelper helper = TemplateSpecHelper.getInstance();
		
		TemplateSpecBean prodSpecBean = this.getManagedBean().getProdTemplateSpecBean();
		TplSpecMaster producSpecMaster = this.getManagedBean().getProdscQtDoc()
				.getTplSpecMaster();
		helper.updateZoneDtl(producSpecMaster,producSpecMaster.getTplSpecZone());
		
		resetTplSpecZoneQuantities(producSpecMaster.getTplSpecQuestions(), helper, 
				isZonal, prodSpecBean);
		resetTplSpecEstimations(producSpecMaster.getTplSpecQuestions(), helper, isZonal,
				prodSpecBean);
		
		TemplateSpecBean serviceSpecBean = this.getManagedBean().getSpecTemplateSpecBean();
		TplSpecMaster serviceSpecMaster = this.getManagedBean().getServicescQtDoc().getTplSpecMaster();
		helper.updateZoneDtl(serviceSpecMaster, serviceSpecMaster.getTplSpecZone());
		
		resetTplSpecZoneQuantities(serviceSpecMaster.getTplSpecQuestions(), helper,
				isZonal, serviceSpecBean);		
		resetTplSpecEstimations(serviceSpecMaster.getTplSpecQuestions(), helper, isZonal, 
				serviceSpecBean);	
		
	}

	/**
	 * Upload branch info.
	 *
	 * @param event the event
	 */
	public void uploadBranchInfo(FileUploadEvent event) {

		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		ScAttachment scAttachment = null;
		boolean isBIReport = false;
		if (scLoaReports == null) {
			scLoaReports = new ArrayList<ScLoaReport>();
			scLoa.setScLoaReports(scLoaReports);
		}
		for (ScLoaReport report : scLoaReports) {
			PmDocument document = report.getPmDocument();
			// Check whether Branch Info attachment is existed or not
			if (null != document
					&& SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
							.equalsIgnoreCase(report.getPmDocument()
									.getDocDefinitionKey())) {
				ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
				report.setScLoaReportAttachment(scLoaReportAttachment);
				scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
				scLoaReportAttachment.setScLoaReport(report);
				report.setScLoaReportAttachment(scLoaReportAttachment);
				scAttachment = scLoaReportAttachment;
				isBIReport = true;
				getManagedBean().setBranchInfoReport(report);
				break;
			}
		}
		if (!isBIReport) {
			ScLoaReport scLoaReport = new ScLoaReport();
			scLoaReport.setScLoa(scLoa);
			scLoaReport.setTitle(BRANCH_INFO_TITLE);
			IParameterSO parameterSO = null;
			try {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);

				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
				reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD);
				ParameterResDTO resDTO = parameterSO
						.retrieveParameterByTypeAndCode(reqDTO);
				scLoaReport.setDefinationType(resDTO.getPmParameter());
				String docCategory = ParameterConst.DOCUMENT_CATEGORY_DIRECT_LOA_FOR_QUOTATION_TENDER; // DL
				scLoaReport.setDocCategory(docCategory);
				//Bug #32188 - start
				scLoaReport.setPmDocument(getDocumentDtls(docCategory)); // PM
																			// document
				//Bug #32188 - end

			} finally {
				parameterSO = null;
			}
			ScLoaReportAttachment scLoaReportAttachment = new ScLoaReportAttachment();
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			scLoaReportAttachment.setDocNo(scLoa.getLoaNo());
			scLoaReportAttachment.setScLoaReport(scLoaReport);
			scLoaReport.setScLoaReportAttachment(scLoaReportAttachment);
			scAttachment = scLoaReportAttachment;
			getManagedBean().setBranchInfoReport(scLoaReport);
			scLoaReports.add(scLoaReport);
		}

		try {
			uploadAttachment(event.getFile(), scAttachment);
		} catch (FileNotFoundException e) {
			log.error("Exceptionn occured while uploading the file"
					+ e.getMessage());
		} catch (IOException e) {
			log.error("Exceptionn occured while uploading the file"
					+ e.getMessage());
		}
	}

	private PmDocument getDocumentDtls(String docCategoryCode) {

		IDocumentSO documentSO = null;
		PmDocument document = null;
		try {
			documentSO = (IDocumentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IDocumentSO.class);
			DocumentReqDTO reqDTO = new DocumentReqDTO();
			reqDTO.setDocCategoryCode(docCategoryCode);
			DocumentResDTO resDTO = documentSO
					.retrieveDocumentCheckList(reqDTO);

			for (PmDocument pmDocument : resDTO.getDocumentList()) {
				if (SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
						.equalsIgnoreCase(pmDocument.getDocDefinitionKey())) {
					document = pmDocument;
					break;
				}
			}
			return document;
		} finally {
			documentSO = null;
		}
	}

	/**
	 * Sets the overall ct amount.
	 *
	 * @param loaAmount the new overall ct amount
	 */
	public void setOverallCtAmount(BigDecimal loaAmount) {
		BigDecimal contractOverAllAmt = BigDecimal.ZERO;
		ScLoa scLoa = this.getManagedBean().getScLoa();
		// #31704 - start
//		scLoa.setContractSigner(new PmUser());
		// #26474 - Start.
		//scLoa.setLoaSignerDesig(null);
        // #26474 - End.
		// #31704 - End
		
		if (loaAmount != null) {
			contractOverAllAmt   = overallCtAmountWithSST(loaAmount, scLoa.getSstAmt());
//			contractOverAllAmt   = overallCtAmount(loaAmount, scLoa.getGstAmt());
		}
		getManagedBean().setOverallContractAmt(contractOverAllAmt);
		
		prepareSignerInfo();
		
		// calc bond amt
		calcBondAmountWithSST(scLoa, true);
//		calcBondAmount(scLoa, true);
		
		// bond amount in words
		// #35977 Updated for English Start Vinod
		if (scLoa.getBondAmt() != null
				&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtilEn.convert(scLoa.getBondAmt().toString()));
		} else if (scLoa.getBondAmt() != null
				&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		} else {
			getManagedBean().setBondAmountWord(null);
		}
		// #35977 End Vinod

		checkAgreementMandatory();
	}

	/**
	 * Update performance bond.
	 */
	public void updatePerformanceBond(ScLoa scLoa) { // #35977
		// calc bond amt
		calcBondAmountWithSST(scLoa, Boolean.TRUE);
//		calcBondAmount(scLoa, Boolean.TRUE);
		
		// bond amount in words
		// #35977 Updated for English Start Vinod
		if (scLoa.getBondAmt() != null
				&& ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtilEn.convert(scLoa.getBondAmt().toString()));
		} else if (scLoa.getBondAmt() != null
				&& ReportConst.MALAY_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			getManagedBean().setBondAmountWord(NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		} else {
			getManagedBean().setBondAmountWord(null);
		}
		// #35977 End Vinod
	}

	private void updateToBeDeleteSpecAnswersTOReq(DirectAwardReqDTO awardReqDTO) {
		List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
		List<TplSpecAnswer> tobeDeletedAnsProd = this.getManagedBean()
				.getProdTemplateSpecBean().getSpecAnswersToDelete();
		if (CollectionUtils.isNotEmpty(tobeDeletedAnsProd)) {
			specAnswers.addAll(tobeDeletedAnsProd);
		}
		
		List<TplSpecAnswer> tobeDeletedAnsService = this.getManagedBean()
				.getSpecTemplateSpecBean().getSpecAnswersToDelete();
		if (CollectionUtils.isNotEmpty(tobeDeletedAnsService)) {
			specAnswers.addAll(tobeDeletedAnsService);
		}

		awardReqDTO.setTobeDeletedList(specAnswers);
	}

	/**
	 * Populate ministry based on login user.
	 */
	public void populateMinistryBasedOnLoginUser() {
		PmOrgProfile orgProfile = this.getManagedBean().getScQt()
				.getPmOrgProfile();
		if(orgProfile != null){
			
			if (!ParameterConst.ORG_TYPE_MINISTRY.equalsIgnoreCase(orgProfile
					.getOrgTypeCode().getParameterCode())) {

				orgProfile = getMinistryOrgProfile(orgProfile);
			}
			this.getManagedBean().getScQt().setMinistryOrgProfile(orgProfile);
		}
	}

	/**
	 * Select all documents.
	 * 
	 * @param event
	 *            the event
	 */
	public void selectAllDocuments(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource())
				.isSelected();
		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
		if (scLoaReports != null && scLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : scLoaReports) {
				if (isSelectAll && null == scLoaReport.getPmDocument()) {
					scLoaReport.setSelected(true);
				} else {
					scLoaReport.setSelected(false);
				}
			}
		}
	}

	/**
	 * Select all supp documents.
	 *
	 * @param event the event
	 */
	public void selectAllSuppDocuments(AjaxBehaviorEvent event) {
		boolean isSelectAll = ((SelectBooleanCheckbox) event.getSource())
				.isSelected();
		ScLoa scLoa = this.getManagedBean().getScLoa();
		List<ScLoaSupportingDoc> supportDocs = scLoa.getScLoaSupportingDocs();
		if (CollectionUtils.isNotEmpty(supportDocs)) {
			for (ScLoaSupportingDoc suppDoc : supportDocs) {
				if (isSelectAll) {
					suppDoc.setSelected(true);
				} else {
					suppDoc.setSelected(false);
				}
			}
		}
	}

	/**
	 * Delete loa document.
	 */
	public void deleteLoaSuppDocument() {
		List<ScLoaSupportingDoc> result = new ArrayList<ScLoaSupportingDoc>();
		ScLoa scLoa = getManagedBean().getScLoa();
		boolean flag = true;
		List<ScLoaSupportingDoc> selectedScLoaReports = scLoa
				.getScLoaSupportingDocs();
		if (selectedScLoaReports != null && selectedScLoaReports.size() > 0) {
			for (ScLoaSupportingDoc scLoaReport : selectedScLoaReports) {
				if (!scLoaReport.isSelected()) {
					result.add(scLoaReport);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		scLoa.setScLoaSupportingDocs(result);
	}

	/**
	 * Delete loa document.
	 */
	public void deleteLoaDocument() {
		List<ScLoaReport> result = new ArrayList<ScLoaReport>();
		ScLoa scLoa = getManagedBean().getScLoa();
		boolean flag = true;
		List<ScLoaReport> selectedScLoaReports = scLoa.getScLoaReports();
		if (selectedScLoaReports != null && selectedScLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : selectedScLoaReports) {
				if (!scLoaReport.isSelected()) {
					result.add(scLoaReport);
				} else {
					flag = false;
				}
			}
		}
		if (flag) {
			throw new EpException(MessageConst.ERROR_760158);
		}
		scLoa.setScLoaReports(result);
	}

	/**
	 * Checks if is flag.
	 *
	 * @return true, if is flag
	 */
	public boolean isFlag() {
		return flag;
	}

	/**
	 * Sets the flag.
	 *
	 * @param flag the new flag
	 */
	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	/**
	 * Checks if is supp doc flag.
	 *
	 * @return true, if is supp doc flag
	 */
	public boolean isSuppDocFlag() {
		return suppDocFlag;
	}

	/**
	 * Sets the supp doc flag.
	 *
	 * @param suppDocFlag the new supp doc flag
	 */
	public void setSuppDocFlag(boolean suppDocFlag) {
		this.suppDocFlag = suppDocFlag;
	}

	/**
	 * Tab change action.
	 *
	 * @param selectedTabIndex the selected tab index
	 */
	public void tabChangeAction(int selectedTabIndex) {

		if (selectedTabIndex != 0 && this.getManagedBean().isDisplayAlert()) {
			this.getManagedBean().setDisplayAlert(false);
			RequestContext.getCurrentInstance().execute(
					"gstConfirmPopupWid.show()");
		}

	}

	/**
	 * @param specMaster
	 */
	private void checkForGSTAlertPopup(TplSpecMaster specMaster) {

		if (null != specMaster) {
			ScLoa scLoa = this.getManagedBean().getScLoa();
			SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
			if ((supplier != null && supplier.getGstRegisteredFlag() != DbValueConst.RECORD_STATUS_ACTIVE)) {
				BigDecimal durationMnth = specMaster.getDurationMonth();
				if (durationMnth != null) {
					final BigDecimal noOfMnth = new BigDecimal(12);
					BigDecimal awardedPrice = (scLoa.getLoaAmt().divide(
							durationMnth, 2, BigDecimal.ROUND_HALF_UP))
							.multiply(noOfMnth);
					awardedPrice = awardedPrice.setScale(2,
							BigDecimal.ROUND_HALF_EVEN);

					if (awardedPrice.intValue() > 500000) {
						getManagedBean().setDisplayAlert(true);
					}
				}
			}
		}
	}

	private void validateContractAmount(BigDecimal contractAmt, String outcome) {
		if(!BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			BigDecimal prodAmount = templateSpecBackingBean
					.calcGrandTotalAmount(this.getManagedBean()
							.getProdTemplateSpecBean());
			BigDecimal servAmount = templateSpecBackingBean
					.calcGrandTotalAmount(this.getManagedBean()
							.getSpecTemplateSpecBean());
			BigDecimal totalSpecEstimatedAmt = prodAmount.add(servAmount);
			if (contractAmt.compareTo(totalSpecEstimatedAmt) < 0) {
			DecimalFormat df = new DecimalFormat("###,###.##");
			throw new EpException(MessageConst.ERROR_700125, new String[] {
			df.format(totalSpecEstimatedAmt).toString(),
					df.format(contractAmt).toString() });
			}
		}
	}

	/**
	 * To check is agreement required
	 * 
	 * @return boolean isRequirementRequired
	 */
	public boolean checkAgreementMandatory() {
		/*Ministry/Central contract 
		 * Fulfillment type : One Off more than RM500k
		 * is mandatory less than RM500k is optional
		 * 
		 * Fulfillment type : Periodic more than RM500k is mandatory less than
		 * RM500k is mandatory
		 */
		boolean isAgreementMandatory = Boolean.FALSE;
		boolean isOneOffFulfilment = Boolean.FALSE;
		ScLoa loa = getManagedBean().getScLoa();
		BigDecimal overallContractAmtOrLoaAmt = getManagedBean().getOverallContractAmt();

		//#24540 - start -
		if (null != loa.getScLoiLoa().getFulfilmentTypeCode() && null != overallContractAmtOrLoaAmt 
				&& !isPk7Periodic()) {
		//#24540 - end -
			String fulfilmentType = loa.getScLoiLoa().getFulfilmentTypeCode()
					.getParameterCode();

			isOneOffFulfilment = ParameterConst.FULFILMENT_TYPE_ONE_OFF
					.equals(fulfilmentType);
			if (!isOneOffFulfilment) {
				isAgreementMandatory = true;
				loa.setAgreementReq(isAgreementMandatory);
			} else if (isOneOffFulfilment && !isQuotationThresHold(overallContractAmtOrLoaAmt)) {
				//Bug #24065 
				//isAgreementMandatory = false;
				isAgreementMandatory = true;
				loa.setAgreementReq(isAgreementMandatory);
			}
		}
		
		return isAgreementMandatory;
	}

	private void RenameTempLoaNoRepoDirByLoaNo(ScLoa scLoa){
		String tempFilePath = splitFilePath(getManagedBean().getTempFilePath());
		String actualFilePath = getActualFilePathLoaNo(scLoa);
		if(!StringUtils.isBlank(tempFilePath)){
			// /docuRepo/dev/ngep/2016/SC/LB16000001565237 
			//StringBuilder tFilePath = new StringBuilder("c:");
			StringBuilder tFilePath = new StringBuilder();
			tFilePath.append(tempFilePath);
			//create source File object
			File oldName = new File(tFilePath.toString());
			log.info("Old Path : " + tFilePath.toString());
			//create destination File object
			//StringBuilder aFilePath = new StringBuilder("c:");
			StringBuilder aFilePath = new StringBuilder();
			aFilePath.append(actualFilePath);
			File newName = new File(aFilePath.toString());
			log.info("New Path : " + aFilePath.toString());
			log.info("Before rename to new path");
			boolean isFileRenamed = oldName.renameTo(newName);
			
			log.info("After rename to new path");
			
			if(isFileRenamed)
			log.info("Temporary Loa No folder has been renamed.");
			else 
			log.info("Temporary Loa No folder has not been renamed.");
		}
	}

	private String getActualFilePathLoaNo(ScLoa loa) {
		String  actualFilePath = "";
		List<ScLoaReport> scLoaReports = loa.getScLoaReports();
		if (scLoaReports != null && scLoaReports.size() > 0) {
			for (ScLoaReport scLoaReport : scLoaReports) {
				if (null != scLoaReport.getPmDocument()) {
					String docDefinitionKey = scLoaReport.getPmDocument()
							.getDocDefinitionKey();
					if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER
							.equals(docDefinitionKey)) {
						if (scLoaReport.getScLoaReportAttachment() != null) {
							String docRepoBaseDir = EpPropertiesUtils.getInstance().getProperty(
									EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
							StringBuilder path = new StringBuilder(docRepoBaseDir);
							path.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
							path.append(scLoaReport.getScLoaReportAttachment().getFilePath());
							actualFilePath = splitFilePath(path.toString());
						}
					}
				}
			}
		}
		return actualFilePath;
	}

	private String splitFilePath(String filePath){
		StringBuilder path = new StringBuilder();
		if(!StringUtils.isBlank(filePath)){
			String[] filePathArr = filePath.split("/");
			for (int i = 0; i < filePathArr.length-2 ; i++) {
				path.append(filePathArr[i]).append("/");
			}
			path.deleteCharAt(path.toString().length()-1);
		}
		return path.toString();
	}
	
	private ScLoiLoa getSupplierLoiLoaByDocId(Long supplierId, Long docId) {
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_DIRECT_NEGO);
		reqDTO.setSourceId(docId);
		reqDTO.setSupplierId(supplierId);
		DirectAwardResDTO resDTO = getQtDirectAwardSO().retrieveLoiLoaByDocumentAndSupplier(reqDTO);
		
		return resDTO.getScLoiLoa();
	}

	/**
	 * Gets the ministry condition field.
	 *
	 * @param scLoa the sc loa
	 * @return the ministry condition field
	 */
	/*
	private void getMinistryConditionField(ScLoa scLoa) {
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(scLoiLoa.getPmOrgProfile().getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		PmOrgProfile ministryOrgProfile = orgProfileSO.retrieveMinistryByOrgProfileId(orgProfileReqDTO).getPmOrgProfile();
		StringBuilder criteria = new StringBuilder();
		criteria.append("orgTypeCode.parameterCode").append("^").append(OrganizationType.DIVISION.value()).append("|");
		
		criteria.append("parentOrgProfile.orgProfileId").append("^").append(ministryOrgProfile.getOrgProfileId()).append("|");
		
		criteria.append("recordStatus^1");
		getManagedBean().setMinistryConditionField(criteria.toString());
	}
	*/

	/**
	 * Localize date.
	 *
	 * @param date the date
	 * @param locale the locale
	 * @return the string
	 */
	private static String getLocalizeDate(Date date, Locale locale) { 
		String DEFAULT_DATE_FORMAT_WITH_MONTH_NAME = "dd MMMM yyyy";
	    SimpleDateFormat localDateFormate = new SimpleDateFormat(DEFAULT_DATE_FORMAT_WITH_MONTH_NAME, locale);       
	    
	    return localDateFormate.format(date);
	}
	private static String splitLongLengthString(String text, int split){
		StringBuilder sb = new StringBuilder();
		String[] array = text.split("(?<=\\G.{" + split + "})");
		int counter=1;
		for (String string : array) {
			sb.append(string);
			if(array.length != counter){
				sb.append("<br />");
				++counter;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Cancel zone pop up.
	 */
	public void cancelZonePopUp() {
		log.info("cancelZonePopUp");
		RequestContext.getCurrentInstance().execute("ZoneGeneral.hide();");
		// zone required validation
		getManagedBean().setZoneLocation(Boolean.FALSE);
	}
	
	
	/**
	 * based on treenode selection button enable/disable.
	 * 
	 * @param event
	 *            the event
	 */
	public boolean zoneLocationDeleteBtnDisable(){
		List<TreeNode> list = getManagedBean().getZoneTrees();

		if (CollectionUtils.isNotEmpty(list) ) {
			for (TreeNode node : list) {
				for (TreeNode childNode : node.getChildren()) {
					if (childNode.getChildCount() > 0) {

						for (TreeNode state : childNode.getChildren()) {

							if(state.isSelected()){
								return false;
							}
							for(TreeNode district : state.getChildren()){

								if(district.getChildCount()>0){
									for(TreeNode division : district.getChildren()){
										if(division.isSelected()){
											return false;
										}
										if(division.getChildCount()>0){
											for(TreeNode anotherDistrict : district.getChildren()){
												if(anotherDistrict.isSelected()){
													return false;
												}
											}
										}
									}
								}else{
									if(district.isSelected()){
										return false;
									}
								}
							}
						}
					}else{
						if(childNode.isSelected()){
							return false;
						}
					}
				}
			}
		} else {
			return true;
		}
		return true;
	}

	private void validateRepoAttachment(ScLoa scLoa){
		for (ScLoaReport scLoaReport : scLoa.getScLoaReports()) {
			if(scLoaReport.getDefinationType() != null && 
					scLoaReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD)){
				if(null == scLoaReport.getScLoaReportAttachment()){
					throw new EpException(MessageConst.ERROR_760432,
							new String[] {scLoaReport.getTitle()});
				}
			}
			if(scLoaReport.getDefinationType() != null && 
					scLoaReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD_AND_DOWNLOAD)){
				if(null == scLoaReport.getScLoaReportAttachment()){
					throw new EpException(MessageConst.ERROR_760441,
							new String[] {scLoaReport.getTitle()});
				}
			}
			if(this.getManagedBean().getScLoa().getPmStatus().getStatusId().equals(QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT)) {
				if(scLoaReport.getDefinationType() != null && 
						(scLoaReport.getDefinationType().getParameterCode().equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD))){
					if(null == scLoaReport.getScLoaReportAttachment()){
						throw new EpException(MessageConst.ERROR_760441,
								new String[] {scLoaReport.getTitle()});
					}
				}
			}
		}
	}
	
	/**
	 * To enable auto update flag
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(true);
	}
	
	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}
	
	/**
	 * Gets the quotation/tender type dropdown values.
	 * 
	 * @return the quotation/tender type dropdown values
	 */
	public SelectItem[] getQTTypesDropdownValues() {
		ScLoa scLoa = getManagedBean().getScLoa();
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> qtTypes = new ArrayList<String>();
		
		/*#34162 Start Vinod
		if (null != scLoa &&null != scLoa.getProcurementMode()
				&& ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scLoa
						.getProcurementMode().getParameterCode())) {*/
			qtTypes.add(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER);
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_NOT_IN.value(), qtTypes));
		//}
		if (null != scLoa
			&& scLoa.getQtType()!= null 
			&& scLoa.getQtType().getParameterCode()!=null
			&& scLoa.getQtType().getParameterCode().equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
				searchBean = new SearchBean<PmParameterDesc>();
		}
		//#34162 End Vinod

		return super.getDropdownValuesBySearchBean(ParameterTypeConst.QUOTATION_TENDER_TYPE,
				searchBean);
	}
	
	/**
	 * Procurement Mode dropdown change action
	 */
	public void procurementModeChanged() {
		IParameterSO parameterSO = null;
		try {
			ScLoa scLoa = getManagedBean().getScLoa();
			Long parameterId = scLoa.getProcurementMode().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setProcurementMode(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	/**
	 * Proc type category changed.
	 *
	 * @param scLoa the sc loa
	 */
	public void procTypeCategoryChanged(ScLoa scLoa) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = scLoa.getProcurementTypeCatId().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setProcurementTypeCatId(resDTO.getPmParameter());
			
//			if (scLoa.getProcurementTypeCatId().getParameterCode().equals(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY)) {
//				reqDTO.setParameterType(ParameterTypeConst.SALES_SERVICE_TAX);
//				reqDTO.setParameterCode(ParameterConst.SST_SALES_TAX);
//				
//				scLoa.setSstTypeId(parameterSO.retrieveParameterByTypeAndCode(reqDTO).getPmParameter());
//			} else {
//				reqDTO.setParameterType(ParameterTypeConst.SALES_SERVICE_TAX);
//				reqDTO.setParameterCode(ParameterConst.SST_SERVICE_TAX);
//				
//				scLoa.setSstTypeId(parameterSO.retrieveParameterByTypeAndCode(reqDTO).getPmParameter());
//			}
		} finally {
			parameterSO = null;
		}
	}
	
	/**
	 * QT Type dropdown change action
	 */
	public void qtTypeChanged() {
		IParameterSO parameterSO = null;
		try {
			ScLoa scLoa = getManagedBean().getScLoa();
			Long parameterId = scLoa.getQtType().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			scLoa.setQtType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}
	}
	
	
	/**
	 * Show prepared by.
	 *
	 * @param createdBy the created by
	 * @return the string
	 */
	public String showPreparedBy(Long createdBy) {
		String userName = "";
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (createdBy != null) {
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);

			if (resDTO.getPmUser() != null) {
				userName = resDTO.getPmUser().getUserName();
			}
		}
		return userName;
	}
	

	/**
	 * Generate ack of loa receipt pdf from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @throws Exception the exception
	 */
	public void generateAckOfLOAReceiptPdfFromVelocity(ScAttachment attachment,
			ScLoa scLoa, ScQt scQt) throws Exception {
		populateAttachmentDetails(attachment);
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		VelocityContext context = new VelocityContext();
		
		String loaNo = "N/A";
		if(!StringUtils.isEmpty(scLoa.getLoaNo())) {
			loaNo = scLoa.getLoaNo();
		}
		context.put("loaNo", loaNo);
		
		String fileNo ="";
		if(!StringUtils.isBlank(scQt.getFileNo())){
			fileNo = scQt.getFileNo();
			if(fileNo.length()>30){
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());

		String govtSignDate	 = "";
		if (scLoa.getGovtSignDate() != null) {
			govtSignDate = getLocalizeDate(scLoa.getGovtSignDate(), myLocale);
		}
		context.put("govtSignDate", govtSignDate);
		
		String supplierSignDate = "";
		if (scLoa.getSupplierSignDate() != null) {
			supplierSignDate = getLocalizeDate(scLoa.getSupplierSignDate(), myLocale);
		}
		context.put("supplierSignDate", supplierSignDate);

		// Supplier Witness
		String supplierWitnessUser = "";
		String supplierWitnessIcNo = "";
		String supplierWitnessAddress = "";
		
		ScLoaWitness supplierWitness = null;
		if(scLoa.getScLoaWitnesses() !=null){
			for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
				String partyType = scLoaWitness.getPartyType();

				if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
					supplierWitness = scLoaWitness;
					break;
				}
			}
		}
		if (supplierWitness != null) {
			supplierWitnessUser = supplierWitness.getWitnessName();
			supplierWitnessIcNo = supplierWitness.getIdentificationNo();
			supplierWitnessAddress = supplierWitness.getAddress();
		}
		supplierWitnessAddress = supplierWitnessAddress.replaceAll("\r\n", "<br />");
		context.put("supplierWitnessUser", supplierWitnessUser);
		context.put("supplierWitnessIcNo", supplierWitnessIcNo);
		context.put("supplierWitnessAddress", supplierWitnessAddress);
		
		// Supplier Signer
		String supplierSignerUser = "";
		String supplierSignerIcNo = "";
		String supplierSignerAddress = "";
		
		PmUser ackBy = scLoa.getAckBy();
		if(ackBy !=null){
			supplierSignerUser = ackBy.getUserName();
			supplierSignerIcNo = ackBy.getIdentificationNo();
		}
		
		if (scLoa.getScSupplierAddress() != null) {
			supplierSignerAddress = getFullAddress(scLoa.getScSupplierAddress());
		}
		log.info("supplierAddress" + supplierSignerAddress);
		//context.put("supplierAddress", supplierAddress);
		StringBuilder supplierAddressTable = null;
		if(StringUtils.isNotBlank(supplierSignerAddress)){
			String[] address = supplierSignerAddress.split(System.getProperty("line.separator"));
			supplierAddressTable = new StringBuilder();
			for (String suppAddr : address) {
				supplierAddressTable.append(suppAddr);
				supplierAddressTable.append("<br />");
			}
		}
		log.info("SupplierAddressTable" + supplierAddressTable);
		
		// #26770 - start fix sonarlint
		String headerTitle = SSTConstant.EMPTY_STRING;
		headerTitle = constructSSTHeaderTitle(scLoa);
		context.put(SSTConstant.HEADER_TITLE, headerTitle);
		// #26770 - end fix sonarlint
				
		context.put("supplierSignerUser", supplierSignerUser);
		context.put("supplierSignerIcNo", supplierSignerIcNo);
		context.put("supplierSignerAddress", supplierAddressTable);
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_ACKNOWLEDGEMENT_OF_LOA_RECEIPT, context, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void generateDeclarationOfOathFromVelocity(ScAttachment attachment,
			ScLoa scLoa) throws Exception{
		populateAttachmentDetails(attachment);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		VelocityContext context = new VelocityContext();
		
		context.put("supplierName", scLoiLoa.getSupplierName());
		String loaNo = "&#60;LOA No.&#62;";
		if(scLoa.getLoaNo() != null){
			loaNo = scLoa.getLoaNo();
		}
		context.put("loaNo", loaNo);
		
		if(scLoiLoa.getSmSupplier() != null){
			SmSupplier smSupplier = scLoiLoa.getSmSupplier();
			// 29819 - start
			String mofNo = null;
			String contextMofNo = smSupplier.getRegNo();

			if (Arrays.asList(ParameterConst.DOCTYPE_DIRECT_NEGO, ParameterConst.DOCTYPE_QUOTATION).contains(
					scLoiLoa.getDocType())) {
				if (StringUtils.isNotBlank(scLoiLoa.getMofNo())) {
					mofNo = scLoiLoa.getMofNo();
				} else if (CollectionUtils.isNotEmpty(smSupplier.getSmMofAccounts())) {
					mofNo = smSupplier.getSmMofAccounts().get(0).getMofNo();
				}
			} else if (smSupplier.getSmMofAccount() != null
					&& CollectionUtils.isNotEmpty(smSupplier.getSmMofAccounts())) {
				mofNo = smSupplier.getSmMofAccounts().get(0).getMofNo();
			}

			if (null != mofNo) {
				contextMofNo = mofNo;
			} else if (StringUtils.isNotBlank(smSupplier.getSsmNewRegNo())) {
				contextMofNo = smSupplier.getSsmNewRegNo();
			}

			context.put(QtReportConst.VELOCITY_CTX_KEY_MOFNO, contextMofNo);
			// 29819 - end
		}
		
		// #26770 - start
		String headerTitle = "";
		if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scLoa.getProcurementMode().getParameterCode())) {
			headerTitle = "Tender";
		} else {
			headerTitle = "Sebut Harga";
		}
		context.put("headerTitle", headerTitle);
		// #26770 - end
		
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(
					SourcingConstants.LOA_SURAT_AKUAN_SUMPAH_SYARIKAT, context, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Update unspsc item.
	 *
	 * @param selectedSpecQuestion the selected spec question
	 */
	public void updateUnspscItem(TplSpecQuestion selectedSpecQuestion) {
		log.info("selectedSpecQuestion " + selectedSpecQuestion);
		// product
		TplSpecMaster  prodMaster = getManagedBean().getProdTemplateSpecBean().getSpecMaster();
		if(prodMaster.getTplSpecQuestions() != null){
			int prodQuestionIndex = prodMaster.getTplSpecQuestions().indexOf(selectedSpecQuestion);
			if(prodQuestionIndex != -1) {
				prodMaster.getTplSpecQuestions().set(prodQuestionIndex, selectedSpecQuestion);
			}
		}
		
		// service
		TplSpecMaster  serviceMaster = getManagedBean().getSpecTemplateSpecBean().getSpecMaster();
		if(serviceMaster.getTplSpecQuestions() != null){
			int serviceQuestionIndex = serviceMaster.getTplSpecQuestions().indexOf(selectedSpecQuestion);
			if(serviceQuestionIndex != -1) {
				serviceMaster.getTplSpecQuestions().set(serviceQuestionIndex, selectedSpecQuestion);
			}
		}
	}

	/**
	 * Download guideline pdf.
	 *
	 * @throws Exception the exception
	 */
	public void downloadGuidelinePdf() throws Exception{
		debugLogger("downloadGuidelinePdf");
		ScPbAttachment pbAttachment = new ScPbAttachment();
		pbAttachment.setDocNo(getManagedBean().getScLoa().getLoaNo());
		pbAttachment.setFileName("Guideline.pdf");
		
		String docRepoSharedDir = EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_DIR);

		StringBuilder filePath = new StringBuilder(docRepoSharedDir);
		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(ModuleCodeConst.SUPPLIER_SELECTION.toUpperCase());
		pbAttachment.setFilePath(filePath.toString());

		File theDir = new File(getActualFilePath(pbAttachment));
		theDir.mkdirs();

		AttachmentFormat scFormat = new AttachmentDefaultFormat();
		String formatedFileName = scFormat.format(SourcingConstants.DOCNAME_QT_SST_GUIDELINE,
				pbAttachment.getPrefixFileName());

		filePath.append(CommonConstants.DOC_REPO_PATH_SEPARATOR);
		filePath.append(formatedFileName);
		pbAttachment.setFilePath(filePath.toString());
	}
	
	/**
	 * View loa.
	 *
	 * @param loaId the loa id
	 */
	public void viewLOA(long loaId) {
		debugLogger("viewLOA");
		try {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(loaId);
			retriveLoaDetails(loaId, this.getManagedBean().getBackToPreviousPage(), true);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
			.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	/**
	 * Generate agreement letter doc from velocity.
	 *
	 * @param attachment the attachment
	 * @param scLoa the sc loa
	 * @param scQt the sc qt
	 * @throws Exception the exception
	 */
	private void generateAgreementLetterDocFromVelocity(ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {
		debugLogger("generateAgreementLetterDocFromVelocity");
		populateAttachmentDetails(attachment);
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(
				commonUtils
						.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		Locale myLocale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		
		DecimalFormat integerDF = new DecimalFormat(
				commonUtils
				.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_INTEGER));
		DecimalFormat amountDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat percentageDF = new DecimalFormat(
				commonUtils
						.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		VelocityContext context = new VelocityContext();
		
		// date formatter
		context.put("dateSDF", dateSDF);
		
		context.put("isZonal", scQt.isZonal());
		
		context.put("isSL1M", scLoa.isSl1mReq());
		
		String fileNo = "";
		if(!StringUtils.isBlank(scQt.getFileNo())){
			fileNo = scQt.getFileNo();
			if(fileNo.length()>30){
				fileNo = splitLongLengthString(fileNo, 29);
			}
		}
		
		// #26770 Note-#548 - Start
		fileNo = fileNo.trim().replaceAll("é", SSTConstant.PROTEGE_LOWERCASE_E);
		fileNo = fileNo.trim().replaceAll("É", SSTConstant.PROTEGE_UPPERCASE_E);
		// #26770 Note-#548 - End
				
		context.put("fileNo", fileNo + "-" + scLoa.getFileExtNo());
		context.put("loaNo", scLoa.getLoaNo());

		String issueDate = "______________";
		if (scLoa.getIssueDate() != null) {
			issueDate = getLocalizeDate(scLoa.getIssueDate(), myLocale);
		}
		context.put("issueDate", issueDate);

		String govtSignDate = "______________";
		if (scLoa.getGovtSignDate() != null) {
			govtSignDate = getLocalizeDate(scLoa.getGovtSignDate(), myLocale);
		}
		context.put("govtSignDate", govtSignDate);

		context.put("supplierName", scLoiLoa.getSupplierName());
		String supplierAddress = "";
		if (scLoa.getScSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoa.getScSupplierAddress());
		}
		log.info("supplierAddress" + supplierAddress);
		StringBuilder supplierAddressTable = null;
		if(StringUtils.isNotBlank(supplierAddress)){
			String[] address = supplierAddress.split(System.getProperty("line.separator"));
			supplierAddressTable = new StringBuilder();
			for (String suppAddr : address) {
				supplierAddressTable.append(suppAddr);
				supplierAddressTable.append("<br />");
			}
//			supplierAddressTable.append("</table>");
		}
		log.info("SupplierAddressTable" + supplierAddressTable);
		context.put("supplierAddressTable", supplierAddressTable);

		String headerTitle = SSTConstant.EMPTY_STRING; // #25025,#26770
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			headerTitle = SSTConstant.HEADER_PEMBELIAN_TERUS; // #25025,#26770
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || 
				ParameterConst.DOCTYPE_DIRECT_NEGO.equals(scLoa.getScLoiLoa().getDocType())) {
			if (ParameterConst.PROCUREMENT_MODE_TENDER.equals(scLoa.getProcurementMode().getParameterCode())) {
				headerTitle = "Tender";
			} else {
				headerTitle = "Sebut Harga";
			}
		}
		// #25025,#26770 -start-
		context.put(SSTConstant.HEADER_TITLE, headerTitle);
		context.put(SSTConstant.SUBJECT_TITLE, headerTitle.toLowerCase());
		// #25025,#26770 -end-
		context.put("headerTitle", headerTitle); 
		context.put("subTitle", headerTitle.toLowerCase()); 
		
		// loa title
		String title=	scQt.getQtTitle();
		if (title == null) {
			title = "";
		}
		title = title.trim();
		//replace all special characters
		//title = title.replaceAll("\\W", ""); 
		//title=title.replaceAll("&", "&#38;");
		// 26770 - start
		title = title.replaceAll("&", SSTConstant.AND_SYMBOL);
		title = title.replaceAll("<", SSTConstant.LESS_THAN_SYMBOL);
		title = title.replaceAll(">", SSTConstant.GREATER_THAN_SYMBOL);
		title = title.replaceAll("\r\n", SSTConstant.BREAKLINE);
		title = title.replaceAll("é", SSTConstant.PROTEGE_LOWERCASE_E);
		title = title.replaceAll("É", SSTConstant.PROTEGE_UPPERCASE_E);
		// 26770 - end
		context.put("loaTitle", title);
		
		context.put("hasLoi", false); 
		context.put("bondReq", scLoa.isBondReq());

		// 26770 - start
		String bondAmt = SSTConstant.UNDERLINE_BOND_AMT;
		String bondAmtText = SSTConstant.UNDERLINE;
		// 26770 - end
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
			bondAmtText = NumberWordUtil.convert(scLoa.getBondAmt().toString());
		}
		// 26770 - start
		context.put(SSTConstant.BOND_AMOUNT, bondAmt);
		context.put(SSTConstant.BOND_AMOUNT_TEXT, bondAmtText);

		String bondPercentage = SSTConstant.UNDERLINE;
		String bondPercentageText = SSTConstant.UNDERLINE;
		// 26770 - end
		if (scLoa.getBondPercentage() != null) {
			// 26770 - start
			bondPercentage = percentageDF.format(scLoa.getBondPercentage()) + SSTConstant.SIGN_PERCENTAGE;
			bondPercentageText = NumberWordUtil.convert(scLoa.getBondPercentage().toString());
			// 26770 - end
		}
		// 26770 - end
		context.put(SSTConstant.BOND_PERCENTAGE_TEXT, bondPercentageText);
		context.put(SSTConstant.BOND_PERCENTAGE, bondPercentage);
		// 26770 - end
		
		String bondExpDate = "______________";
		//bond exp date should be one year after the end of the contract period
		if(scLoa.getContractExpDate() != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(scLoa.getContractExpDate());
			cal.add(Calendar.YEAR, 1); //add one year
			bondExpDate = getLocalizeDate(cal.getTime(), myLocale);
		} 
		context.put("bondExpDate", bondExpDate); 

		// bond response due days  - 30
		context.put("bondResDueDays", 30);

		String ackDate = "";
		if (scLoa.getAckDate() != null) {
			ackDate = DateUtils.getLocalizeDate(scLoa.getAckDate(), myLocale);
		}
		context.put("ackDate", ackDate);
		context.put("insWarrantyReq", scLoa.isInsWarrantyReq());

		// #26770 - start fix sonarlint
		if (null == scLoa.getContractEffDate()) {
			context.put(SSTConstant.CONTRACT_EFFECTIVE_DATE, SSTConstant.EMPTY_STRING);
		} else {
			context.put(SSTConstant.CONTRACT_EFFECTIVE_DATE,
					new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(scLoa.getContractEffDate()));
		}

		if (null == scLoa.getContractExpDate()) {
			context.put(SSTConstant.CONTRACT_EXPIRED_DATE, SSTConstant.EMPTY_STRING);
		} else {
			context.put(SSTConstant.CONTRACT_EXPIRED_DATE,
					new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(scLoa.getContractExpDate()));
		}
		// #26770 - end
		
		//ontractDuration
		context.put("contractDuration", scLoa.getContractDuration());

		context.put("isAgreementReq", scLoa.isAgreementReq());
		
		String LoaSigner = "______________";
		String loaSignerDesig = "______________";
		if(scLoa.getContractSigner() != null){
			PmUser contractSigner = scLoa.getContractSigner();
			if(StringUtils.isNotEmpty(contractSigner.getUserName())){
				LoaSigner = contractSigner.getUserName();
			}
		}
		context.put("loaSigner", LoaSigner);	
		
		loaSignerDesig = scLoa.getLoaSignerDesig();
		 if(StringUtils.isNotEmpty(loaSignerDesig) && loaSignerDesig.length()>86){
			loaSignerDesig = splitLongLengthString(loaSignerDesig, 86);
		}
		context.put("loaSignerDesignation", loaSignerDesig);
		
		// Setting GST info
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
	    String gstRegFlag = "false";
		if(supplier != null && supplier.getGstRegisteredFlag()== DbValueConst.RECORD_STATUS_ACTIVE){
			gstRegFlag = "true";
		}
		context.put("gstRegFlag", gstRegFlag);
		
		// 26770 - start fix sonarlint
		if (supplier != null && supplier.getRegNo() != null
				&& !supplier.getRegNo().equalsIgnoreCase(SSTConstant.EMPTY_STRING)) {
			context.put(SSTConstant.REGISTRATION_NO, supplier.getRegNo());
		} else {
			context.put(SSTConstant.REGISTRATION_NO, SSTConstant.UNDERLINE_SHORT);
		}

		if (supplier != null && null != supplier.getSmAppl()) {
			setSSMExpiredDateForAgreementLetter(context, supplier);
		}
		// 26770-end fix sonarlint
		
//		setGstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
		setSstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);
		setMofInfoForAgreementLetter(context, scLoa, myLocale);
		setMinSL1MParticipant(context, scLoa, integerDF);
		
		String fiService = "T/B";
		if(scQt != null){
			if(ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())){
				fiService = "0.8%";
			}
			else if(ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode())
					|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(scQt.getFulfilmentType().getParameterCode())){ //Bug #22393
				fiService = "0.4%";
			}
		}
		context.put("fiService", fiService);
		
		// qtno
		String qtNo = (StringUtils.isEmpty(scQt.getQtNo()))? "______________" : scQt.getQtNo();
		context.put("qtNo", qtNo);
		
		// #26770 - Start fix sonarlint
		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))) {
			// #26770 - Start.
			if ("Q".equals(getManagedBean().getScLoa().getProcurementMode().getParameterCode())) {
				context.put(SSTConstant.DOCUMENT_TYPE, SSTConstant.HEADER_SEBUT_HARGA);
			} else if ("T".equals(getManagedBean().getScLoa().getProcurementMode().getParameterCode())) {
				context.put(SSTConstant.DOCUMENT_TYPE, SSTConstant.HEADER_TENDER);
			}
			// #26770 - End.
		}
		// #26770 - End fix sonarlint
		
		// #26770 - Start fix sonarlint
		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))) {
			// #26770 - Start.
			if ("Q".equals(getManagedBean().getScLoa().getProcurementMode().getParameterCode())) {
				context.put(SSTConstant.DOCUMENT_TYPE, SSTConstant.HEADER_SEBUT_HARGA);
			} else if ("T".equals(getManagedBean().getScLoa().getProcurementMode().getParameterCode())) {
				context.put(SSTConstant.DOCUMENT_TYPE, SSTConstant.HEADER_TENDER);
			}
			// #26770 - End.
		}
		// #26770 - End fix sonarlint
		// JadualHarga
		formJadualHarga(context, scLoa);
		
		// Specification
		formSpecification(context, scLoa);
		
		// scLoa
		context.put("scLoa", scLoa);
		
		String content = null;
		if(scLoa.getProcurementTypeCatId() != null && 
				scLoa.getProcurementTypeCatId().getParameterCode().equals(ParameterConst.PROCUREMENT_TYPE_CODE_SUPPLY)){
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(SourcingConstants.LOA_AGREEMENT_LETTER_PRODUCT, context);
		} else{
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(SourcingConstants.LOA_AGREEMENT_LETTER_SERVICE, context);
		}
		if (content != null) {
			DocumentGenerator.convertHtmlToDocFile(content, getActualFilePath(attachment), scLoa.getLoaNo());
		}
	}
	
	/**
	 * Form jadual harga.
	 * @param context the context
	 * @param scLoa the sc loa
	 */
	private void formJadualHarga(VelocityContext context, ScLoa scLoa){
		List<ItemBean> productItemBeans = null;
		List<ItemBean> serviceItemBeans = null;
		JadualHargaItemBean itemBean = new JadualHargaItemBean();
		TplSpecMaster productSpecMaster = getManagedBean()
				.getProdTemplateSpecBean().getSpecMaster();
		TplSpecMaster serviceSpecMaster = getManagedBean()
				.getSpecTemplateSpecBean().getSpecMaster();
		productItemBeans = convertSpecToJadualHargaBean(productSpecMaster);
		serviceItemBeans = convertSpecToJadualHargaBean(serviceSpecMaster);

		// check whether loa from s1 or s2.
		/*boolean isS1OrS2 = Boolean.TRUE;
		if (scLoa.getScLoiLoa() != null) {
			if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
					.getDocType())) {
				isS1OrS2 = Boolean.FALSE;
			}
		}*/
		
		itemBean.setProductItemBeans(productItemBeans);
		itemBean.setServiceItemBeans(serviceItemBeans);
		context.put("jadualHargaItemBean", itemBean);
	}
	
	private void formSpecification(VelocityContext context, ScLoa scLoa)
			throws Exception {
		List<ItemBean> serviceBeans  = null;
		List<ItemBean> productBeans  = null;

		List<TplSpecQuestion> productQuestions = this.getManagedBean()
				.getProdscQtDoc().getTplSpecMaster().getTplSpecQuestions();
		List<TplSpecQuestion> serviceQuestions = this.getManagedBean()
				.getServicescQtDoc().getTplSpecMaster().getTplSpecQuestions();
		List<String> itemNames = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(productQuestions)) {
			for (TplSpecQuestion specQuestion : productQuestions) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM
						.equalsIgnoreCase(specQuestion.getQuestionType())) {
					itemNames.add(specQuestion.getClItem());
				}
			}
		}
		if (CollectionUtils.isNotEmpty(serviceQuestions)) {
			for (TplSpecQuestion specQuestion : serviceQuestions) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM
						.equalsIgnoreCase(specQuestion.getQuestionType())) {
					itemNames.add(specQuestion.getClItem());
				}
			}
		}
		TplSpecMaster productSpecMaster = getManagedBean()
				.getProdTemplateSpecBean().getSpecMaster();
		TplSpecMaster serviceSpecMaster = getManagedBean()
				.getSpecTemplateSpecBean().getSpecMaster();
		productBeans = convertSpecQuestionToSpecAnswer(productSpecMaster);
		serviceBeans = convertSpecQuestionToSpecAnswer(serviceSpecMaster);

		context.put("productAnswers", productBeans);
		context.put("serviceAnswers", serviceBeans);
	}

	/**
	 * Mapping jadual harga.
	 * @param item the item
	 * @return the item bean
	 */
	/*
	private ItemBean mappingJadualHarga(ScLoiLoaItem item) {
		ItemBean itemBean = new ItemBean();
		itemBean.setZoneName(item.getTplSpecZoneDtl() != null ? item.getTplSpecZoneDtl().getZoneName() : "");
		itemBean.setItem(item.getScRequestItem().getItemName());

		if (item.getTplSpecTierQty() != null) {
			itemBean.setTier(Boolean.TRUE);
			itemBean.setFromQty(null == item.getTplSpecTierQty().getFromQty() ? "" : item.getTplSpecTierQty().getFromQty().toString());
			itemBean.setToQty(null == item.getTplSpecTierQty().getToQty() ? "" : item.getTplSpecTierQty().getToQty().toString());
		}
		itemBean.setQuantity(null == item.getQty() ? "" : item.getQty().toString());

		return itemBean;
	}
	*/
	
	/**
	 * Validate ScLoaWitness and suppler contract signer before download attachment of Acknowledge LOA Receipt
	 */
	private void validateLoaWitnessAndAckBy() {
		ScLoa scLoa = this.getManagedBean().getScLoa();
		ScLoaWitness witness = this.getManagedBean().getLoaSupplierWitness();
		PmUser ackBy = scLoa.getAckBy();
		if(witness == null || 
				StringUtils.isBlank(witness.getWitnessName()) || StringUtils.isBlank(witness.getIdentificationNo()) ||
				StringUtils.isBlank(witness.getDesignation()) || StringUtils.isBlank(witness.getAddress()) || 
				StringUtils.isBlank(witness.getPhoneNo()) || StringUtils.isBlank(witness.getFaxNo())) {
			throw new EpException(MessageConst.ERROR_760442);
		}
		if(ackBy == null || ackBy.getUserName().isEmpty()) {
			throw new EpException(MessageConst.ERROR_760442);
		}
		if(scLoa.getSupplierSignDate() == null) {
			throw new EpException(MessageConst.ERROR_760442);
		}
	}
	
	private TreeNode buildZoneTreeNode(TplSpecZoneDtl tplSpecZoneDtl, List<TplSpecZoneLoc> allNodes){
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(allNodes);

		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(allNodes);

		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(allNodes);

		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(allNodes);
		

		TreeNode treeNode = LocationSingleton.getInstance().buildLocationTree(tplSpecZoneDtl,
				states, districts, divisions, cities,
				TplSpecZoneLoc.class);
		
		return treeNode;
	}

	private String getSupplierBranchNameByBranchCode(Long branchCode){
		List<SmSupplierBranch> supplierBranchs = this.getManagedBean()
				.getBranchsList();
		String branchName = null;
		if(supplierBranchs != null && branchCode != null){
			for (SmSupplierBranch supplierBranch : supplierBranchs) {
				if (supplierBranch.getBranchCode().equals(
						branchCode)) {
					branchName = supplierBranch.getBranchName();
				}
			}
		}
		return branchName;
	}
	
	private ITemplateSpecSO getTemplateSpecSO(){
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
	}
	
	private void validateSpecQuestions(){
		Locale locale = new Locale(CommonConstants.LANGUAGE_CODE_MALAY, CommonConstants.DEFAULT_COUNTRY_CODE);
		// product
		TplSpecMaster  prodMaster = getManagedBean().getProdTemplateSpecBean().getSpecMaster();
		if(CollectionUtils.isNotEmpty(prodMaster.getTplSpecQuestions())){
			getTemplateSpecSO().validateSpecQuestion(prodMaster.getTplSpecQuestions(), Boolean.TRUE, locale);
		}
		// service
		TplSpecMaster  serviceMaster = getManagedBean().getSpecTemplateSpecBean().getSpecMaster();
		if(CollectionUtils.isNotEmpty(serviceMaster.getTplSpecQuestions())){
			getTemplateSpecSO().validateSpecQuestion(serviceMaster.getTplSpecQuestions(), Boolean.TRUE, locale);
		}
	}
	
	public void downloadViewLoa() throws Exception {
		log.info("start downloadViewLoa");
		List<ScLoaReport> scloaReports=	this.getManagedBean().getScLoa().getScLoaReports();
		if(CollectionUtils.isNotEmpty(scloaReports)){
			ScLoaReport report=null;
			for(ScLoaReport loaReport:scloaReports){
				if(null!=loaReport.getPmDocument() && SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equalsIgnoreCase(loaReport.getPmDocument().getDocDefinitionKey())){
					log.info("LoaReport Title"+loaReport.getTitle());
					//log.info("DocDefinitionType"+loaReport.getDefinationType().getParameterCode());
					//log.info("LoaReportAttachment"+loaReport.getScLoaReportAttachment());
					report = loaReport;									
				break;	
				}
			}
			if(null!=report){
				//log.info("report not null"+report.getScLoaReportAttachment().getFileName());
				downloadFile(report.getScLoaReportAttachment());
			}
		}
	}
	
	public String constructUserConditionFields() {
		StringBuilder sb = new StringBuilder();
		ScLoa scLoa = getManagedBean().getScLoa();
		//Bug #30698
		Long MinistryOrgProfileId = retrieveMinistryOrgProfile(scLoa.getScLoiLoa().getPmOrgProfile().getOrgProfileId()).getOrgProfileId();
		sb.append("pmUserOrgs.pmUserRoles.pmRole.roleCode^");
		sb.append(RoleCode.CONTRACT_SIGNER.value());
		sb.append("^9");
		//Bug #30698
		 if (null != MinistryOrgProfileId) {
			 sb.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			 sb.append("pmUserOrgs.pmOrgProfile.");
			 sb.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + MinistryOrgProfileId);
		}
		if (scLoa.getLoaAmt() != null) {
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.isLimitRequired^");
			sb.append(true);
			sb.append("^1");
			sb.append("|pmUserOrgs.pmUserRoles.approvalLimit^");
			sb.append(overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt()));
			sb.append("^4");
		}
		return sb.toString();
	}

	public void signerUserListener(PmUser contractSignerUser) {
		ScLoa scLoa = getManagedBean().getScLoa();
		scLoa.setLoaSignerDesig(contractSignerUser.getDesignation());

		// #26474 - start.
		getManagedBean().setContractSignerUser(contractSignerUser);
		// #26474 - end.
	}

	private boolean isFulfillmentTypeOneOffOrAsAndWhen(PmParameter fulfillmentType) {
		String fullfillmentTypeParamCode = null;
		if(null != fulfillmentType) {
			fullfillmentTypeParamCode = fulfillmentType.getParameterCode();
		}
		log.info("isFulfillmentTypeOneOffOrAsAndWhen:"+ fullfillmentTypeParamCode);
		
		return ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fullfillmentTypeParamCode)
				|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(fullfillmentTypeParamCode);
	}
	
	/**
	 * Validate Contract Admin
	 * 
	 * @param scLoa
	 *          the scLoa
	 */	
	private void validateContractDetails(ScLoa scLoa) {
		//Redmine Bug #21137
		if(scLoa.getContractDuration() != null && (scLoa.getContractDuration().compareTo(new BigDecimal(1)) < 0)) {
			throw new EpException(MessageConst.ERROR_760473);
		} else if (scLoa.getContractDuration() != null && (scLoa.getContractDuration().doubleValue() % 1 != 0)){
			throw new EpException(MessageConst.ERROR_760472, new String[] {scLoa.getContractDuration().toString()});
		}
		
		//Bug #30837
		BigDecimal contractDuration = scLoa.getContractDuration();
		Calendar contractExpDate = Calendar.getInstance();
		contractExpDate.setTime(scLoa.getContractEffDate());
		contractExpDate.add(Calendar.MONTH, contractDuration.intValue());
		contractExpDate.add(Calendar.DATE, -1);

		if (!scLoa.getContractExpDate().equals(contractExpDate.getTime())){
			scLoa.setContractExpDate(contractExpDate.getTime());
		}
	}

	// #26474 - start
	private void validateContractAdmin(ScLoa scLoa) {
		List<PmUser> pmUserList = getUsersByAgency();

		if (CollectionUtils.isEmpty(pmUserList)) {
			throw new EpException(MessageConst.ERROR_760512);
		}
	}
	// #26474 - end

	/**
	 * Get User Organization Profile by user id.
	 * 
	 * @param userId
	 *            the user id.
	 * @return PmOrgValidity 
	 */
	public PmOrgValidity getOrgProfileByUserId(Long userId) {
		log.info("getOrgProfileByUserId");
		PmOrgValidity orgValidity = null;
		String roleCode = "MIN_CONTRACT_ADMIN";
		IUserSO userSo = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setRoleCode(roleCode);
		userReqDTO.setUserId(userId);
		
		UserResDTO userResDTO = userSo.retrieveUserOrgListByUserId(userReqDTO);
		
		if (userResDTO.getUserOrgList() != null
				&& userResDTO.getUserOrgList().size() > 0) {
			for (PmUserOrg pmUserOrg : userResDTO.getUserOrgList()) {
				if (pmUserOrg.getPmOrgProfile() != null) {
					orgValidity = pmUserOrg.getPmOrgProfile().getPmOrgValidity();
				}
			}
		}
		
		return orgValidity;
	}
	
	//Enhancement #21573 To have confirmation box for reject/acknowledge button at document LOI/LOA	
	/**
	 * Update bpm task action for supplier. update bpm - save data into database and BPM
	 * process will route to the next respective user. Authenticating spki
	 * before update bpm.
	 * 
	 * @param outcome
	 *            the outcome
	 */
	public void updateBpmTaskActionSupplier(String outcome) {
		this.getManagedBean().setAction(outcome);
		Long statusId = this.getManagedBean().getScLoa().getPmStatus().getStatusId().longValue();

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
				.equals(statusId)) {
			validateSupplierWitnessDetails(this.getManagedBean().getScLoa(), this.getManagedBean().getLoaSupplierWitness(), outcome);
		}
		validateLOADetails(this.getManagedBean().getScLoa(), outcome);

		// validate spec questions
		// 11134 -note 172 -start
		if (StringUtils.isNotEmpty(outcome) && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			validateSpecQuestions();
		}
		// 11134 -note 172 -end

		String loa = getMessage("lbl.sc.loiloa.loa.header.title", null);
		String procurementMode = null; 
		String mode = this.getManagedBean().getScLoa().getProcurementMode().getParameterCode();
		
		if (mode.equals(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			procurementMode = getMessage("lbl.sc.sq", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			procurementMode = getMessage("lbl.rpt.sc.quotation", null);
		} else if (mode.equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
			procurementMode = getMessage("lbl.rpt.sc.tender", null);
		}
		
		if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {					
			this.getManagedBean().setAutoUpdate(false);		
			
			String acceptMsg = getMessage("lbl.sc.loa.supplier.confirm.button.accept", new String[] { loa.trim() , procurementMode.trim() });
			
			this.getManagedBean().setSuppConfirmMsgAccept(acceptMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmAcceptDlg.show()"); 			
		} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {			
			this.getManagedBean().setAutoUpdate(false);
			
			String rejectMsg = getMessage("lbl.sc.loiloa.supplier.confirm.button.reject", new String[] { loa.trim(), procurementMode.trim() });
 
			this.getManagedBean().setSuppConfirmMsgReject(rejectMsg.toString().trim());
			RequestContext.getCurrentInstance().execute("confmRejectDlg.show()"); 						
		} 
	}
	
	public void confirmSupplierAccept() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_ACCEPT);
		log.info(":::confirmSupplierAccept() to "+ BpmOutcomeConst.BPM_ACCEPT +" DIRECT LOA.");
		// #29007 Start Vinod
		checkAndInsertItemSpecOffer();
		// #29007 End Vinod
		
		// spki
		getManagedBean().getSpkiBackingBean().getChallengeQuestions();
		log.info("::: #25061 before spki_dlg.show ");
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");		
	}
	
	public void confirmSupplierReject() {
		this.getManagedBean().setAction(BpmOutcomeConst.BPM_REJECT);
		log.info(":::confirmSupplierReject() to "+ BpmOutcomeConst.BPM_REJECT +" DIRECT LOA.");
		updateBPMTask(BpmOutcomeConst.BPM_REJECT, null);
	}
	//END Enhancement #21573
	
	//Bug #24713
	/**
	 * Validate supplier and LOA details
	 * 
	 * @param scLoa
	 *          the scLoa,  ScLoaWitness the scLoaWitness, String outcome
	 */	
	private void validateSupplierWitnessDetails(ScLoa scLoa, ScLoaWitness scLoaWitness, String outcome) {
		Locale locale = new FacesScopes().getUserLocale();
		
		if (!BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
			if (scLoa.getSupplierSignDate() == null) {
				throw new EpException(MessageConst.ERROR_760400,
						new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.loa.supplier.sign.date", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoa.getBankAccNo())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.account.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (scLoa.getAckBy().getUserId() == null) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.signer.full.name", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoa.getAckBy().getIdentificationNo())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.identification.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getWitnessName())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.loiloa.witness.full.name", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getIdentificationNo())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.identification.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getDesignation())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.designation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getAddress())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.address", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getPhoneNo())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.tel.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
			if (StringUtils.isBlank(scLoaWitness.getFaxNo())) {
				throw new EpException(MessageConst.ERROR_760400, new String[] { ResourceBundleUtils.getMessage("lbl.sc.fax.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale) });
			}
		}
	}
	
	private void validateDuplicateItems(ScQtDoc productScQtDoc, ScQtDoc serviceScQtDoc) {
		log.info("validateDuplicateItems");
		List<TplSpecMaster> productServiceDocs = new ArrayList<TplSpecMaster>();
		if (null != productScQtDoc) {
			if (null != productScQtDoc.getTplSpecMaster()) {
				productServiceDocs.add(productScQtDoc.getTplSpecMaster());
			}
		}
		if (null != serviceScQtDoc) {
			if (null != serviceScQtDoc.getTplSpecMaster()) {
				productServiceDocs.add(serviceScQtDoc.getTplSpecMaster());
			}
		}
		if (CollectionUtils.isNotEmpty(productServiceDocs)) {
			List<TplSpecQuestion> FinancialUiSpecQuestions = new ArrayList<TplSpecQuestion>();
			for (TplSpecMaster itemProdSerSpecMaster : productServiceDocs) {

				if (CollectionUtils.isNotEmpty(itemProdSerSpecMaster.getTplSpecQuestions())) {
					for (TplSpecQuestion specQuestion : itemProdSerSpecMaster.getTplSpecQuestions()) {
						if (TemplateSpecConst.QUESTION_TYPE_ITEM.equalsIgnoreCase(specQuestion.getQuestionType())) {
							if (FinancialUiSpecQuestions.isEmpty()) {
								FinancialUiSpecQuestions.add(specQuestion);
							} else {
								for (TplSpecQuestion uiQuestions : FinancialUiSpecQuestions) {
									if (uiQuestions.getClItem().trim().replaceAll("\\s", "").equalsIgnoreCase(specQuestion.getClItem().trim().replaceAll("\\s", ""))) {
										throw new EpException(MessageConst.ERROR_760340, new String[] { specQuestion.getClItem() });
									}
								}
								FinancialUiSpecQuestions.add(specQuestion);
							}
						}

					}
				}
			}
		}
	}
		
	/**
	 * @param e
	 */
	public void selectAllSchedule(AjaxBehaviorEvent e) {

		for (ScQtSchedule ScQtSchedule : getManagedBean().getScLoa().getScQtSchedules()) {
			if (getManagedBean().isSelectAll()) {
				ScQtSchedule.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtSchedule.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getScLoa().getScQtProgPayments()) {
			if (getManagedBean().isSelectAll()) {
				ScQtProgressivePayment.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtProgressivePayment.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
	}

	public void selectOneScheCheckboxSelect(AjaxBehaviorEvent e) {
		getManagedBean().setSelectAll(false);
		for (ScQtSchedule ScQtSchedule : getManagedBean().getScLoa()
				.getScQtSchedules()) {
			if (ScQtSchedule.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getScLoa().getScQtProgPayments()) {
			if (ScQtProgressivePayment.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			getManagedBean().setButtonHideSchActive(false);
		} else {
			getManagedBean().setButtonHideSchActive(true);
		}
	}

	public void deleteFreqScheduleDetails() {
		AwardSupplierResDTO resDTO = null;
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoa(this.getManagedBean().getScLoa());
		try {
			if (null != getManagedBean().getScLoa().getScQtSchedules()) {
				resDTO = getQtAwardSO()
						.deletetScheduleIdAndPaymentId(reqDTO);
				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);

				}
				getManagedBean().getScLoa().setScQtSchedules(resDTO.getScLoa().getScQtSchedules());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}
	
	public void deleteProgPaymentDetails() {
		AwardSupplierResDTO resDTO = null;
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
		reqDTO.setScLoa(this.getManagedBean().getScLoa());
		try {
			if (null != getManagedBean().getScLoa().getScQtProgPayments()) {
				resDTO = getQtAwardSO()
						.deletetScheduleIdAndPaymentId(reqDTO);
				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);

				}
				getManagedBean().getScLoa().setScQtProgPayments(resDTO.getScLoa().getScQtProgPayments());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}
	
	//#24540 - start -
	/**
	 * To display Performance Bond dropdown when QT Type is <b>Special Item / Item In PK 7</b> 
	 * and Fulfilment Type is <b>One Off</b>.
	 * 
	 * @return
	 */
	public boolean isPbDropDown() {
		PmParameter qtType = null;
		PmParameter fulfilmentType = null;
		PmParameter contractType = null;
		boolean QtTypepk7 = false;
			
		if (null != getManagedBean().getScLoa().getQtType()) {
			qtType = getManagedBean().getScLoa().getQtType();
		}
			
		if (null != getManagedBean().getScLoa().getScQt() && null != getManagedBean().getScLoa().getScQt().getFulfilmentType()) {
			fulfilmentType = getManagedBean().getScLoa().getScQt().getFulfilmentType();
		}
			
		if (null != getManagedBean().getScLoa().getContractType()) {
			contractType = getManagedBean().getScLoa().getContractType();
		}
			
		if (getManagedBean().getPk7Reasons() == null) {
			setPk7DropDownValues();
		}
			
		// 34420-start
		// MC
		if (null != contractType && ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType())
				&& !ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType.getParameterCode())) {
			return false;
		}
		// 34420-end

		if (StringUtils.isEmpty(getManagedBean().getYesParameterId())) {
			getManagedBean().setYesParameterId(String.valueOf(getParameterIdByTypeAndCode(ParameterTypeConst.YES_NO_FLAG, ParameterConst.COMPLIANCE_STATUS_COMPLIANCE.toUpperCase())));
		}
			
		if (StringUtils.isEmpty(getManagedBean().getNoParameterId())) {
			getManagedBean().setNoParameterId(String.valueOf(getParameterIdByTypeAndCode(ParameterTypeConst.YES_NO_FLAG, ParameterConst.COMPLIANCE_STATUS_NOT_COMPLIANCE.toUpperCase())));
		}
			
		if (StringUtils.isEmpty(getManagedBean().getBondReqSelectedVal())) {
			if (getManagedBean().getScLoa().isBondReq()) {
				getManagedBean().setBondReqSelectedVal(getManagedBean().getYesParameterId());
			} else if (getManagedBean().getScLoa().getLoaNo() != null) {
				getManagedBean().setBondReqSelectedVal(getManagedBean().getNoParameterId());
			}
		}
			
		if (null != qtType && ParameterTypeConst.QUOTATION_TENDER_TYPE.equals(qtType.getParameterType())
				&& ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7.equals(qtType.getParameterCode())) {
			QtTypepk7 = true;
				
			if (null == getManagedBean().getScLoa().getJustificationType()) {
				getManagedBean().getScLoa().setJustificationType(new PmReason());
			}
		}
			
		if (QtTypepk7 && null != fulfilmentType
				&& ParameterTypeConst.FULFILMENT_TYPE.equals(fulfilmentType.getParameterType())
				&& ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fulfilmentType.getParameterCode())) {
				
			if (null == getManagedBean().getBondReqSelectedVal()) {
				getManagedBean().getScLoa().setBondReq(false);
			} else if (getManagedBean().getNoParameterId().equals(getManagedBean().getBondReqSelectedVal())) {
				getManagedBean().getScLoa().setBondReq(false);
			}
				
			// If Contract Type is selected and its not 'Ministry', then return false.
			if (null != contractType && ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType())
					&& !ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType.getParameterCode())) {
				return false;
			}
				
			if (!getManagedBean().isDisableFields()) {
				String loaStatus = getStatusDescById(this.getManagedBean().getScLoa().getPmStatus().getStatusId());
				if (StringEscapeUtils.unescapeJava(SourcingConstants.LOA_PENDING_VERIFICATION_STATUS).equals(loaStatus)) {
					getManagedBean().setDisableFields(true);
				}
			}
				
			return true;
		} else {
			// 17376 -start-
			if (!getManagedBean().getScLoa().isBondReq() && !isPk7Periodic() && !this.getManagedBean().isReadOnly()) {
			// 17376 -end-
				setOverallCtAmount(getManagedBean().getScLoa().getLoaAmt());
			}
		}

		return false;
	}

	private Long getParameterIdByTypeAndCode(String paramType, String paramCode) {
		IParameterSO parameterSO = null;
		
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);
			
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(paramType);
			reqDTO.setParameterCode(paramCode);
			
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			
			if (null != resDTO && resDTO.isSuccessful() && null != resDTO.getPmParameter()) {
				return resDTO.getPmParameter().getParameterId();
			}
		} finally {
			parameterSO = null;
		}
		
		return null;
	}
		
	/**
	 * To display Agreement dropdown when QT Type is <b>Special Item / Item In PK 7</b> 
	 * and Fulfilment Type is <b>One Off</b>.
	 * 
	 * @return
	 */
	public boolean isAgreementDropDown() {
		PmParameter qtType = null;
		PmParameter fulfilmentType = null;
		PmParameter contractType = null;
		
		if (null != getManagedBean().getScLoa().getQtType()) {
			qtType = getManagedBean().getScLoa().getQtType();
		}
		
		if (null != getManagedBean().getScLoa().getScQt() && null != getManagedBean().getScLoa().getScQt().getFulfilmentType()) {
			fulfilmentType = getManagedBean().getScLoa().getScQt().getFulfilmentType();
		}
		
		if (null != getManagedBean().getScLoa().getContractType()) {
			contractType = getManagedBean().getScLoa().getContractType();
		}
		
 		// 34420-start
		if (null != contractType && ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType())
				&& !ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType.getParameterCode())) {
			return false;
		}
		// 34420-end
		
		if (StringUtils.isEmpty(getManagedBean().getYesParameterId())) {
			getManagedBean().setYesParameterId(String.valueOf(getParameterIdByTypeAndCode(ParameterTypeConst.YES_NO_FLAG, ParameterConst.COMPLIANCE_STATUS_COMPLIANCE.toUpperCase())));
		}
		
		if (StringUtils.isEmpty(getManagedBean().getNoParameterId())) {
			getManagedBean().setNoParameterId(String.valueOf(getParameterIdByTypeAndCode(ParameterTypeConst.YES_NO_FLAG, ParameterConst.COMPLIANCE_STATUS_NOT_COMPLIANCE.toUpperCase())));
		}
		
		if (StringUtils.isEmpty(getManagedBean().getAgreemetSelectedVal())) {
			if (getManagedBean().getScLoa().isAgreementReq()) { 
				getManagedBean().setAgreemetSelectedVal(getManagedBean().getYesParameterId());
			} else if (getManagedBean().getScLoa().getLoaNo() != null) {
				getManagedBean().setAgreemetSelectedVal(getManagedBean().getNoParameterId());
			}
		}
		
		if (null != qtType && null != fulfilmentType
				&& ParameterTypeConst.QUOTATION_TENDER_TYPE.equals(qtType.getParameterType())
				&& ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7.equals(qtType.getParameterCode())
				&& ParameterTypeConst.FULFILMENT_TYPE.equals(fulfilmentType.getParameterType())
				&& ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(fulfilmentType.getParameterCode())) {
			
			if (null == getManagedBean().getAgreemetSelectedVal()) {
				getManagedBean().getScLoa().setAgreementReq(false);
			} else if (getManagedBean().getNoParameterId().equals(getManagedBean().getAgreemetSelectedVal())) {
				getManagedBean().getScLoa().setAgreementReq(false);
			}
			
			// If Contract Type is selected and its not 'Ministry', then return false.
			if (null != contractType && ParameterTypeConst.CONTRACT_TYPE.equals(contractType.getParameterType()) 
					&& !ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType.getParameterCode())) {
				return false;
			}
			
			return true;
		} else {
			// 17376 -start-
			if (!getManagedBean().getScLoa().isAgreementReq() && !isPk7Periodic() && !this.getManagedBean().isReadOnly()) {
			// 17376 -end-
				setOverallCtAmount(getManagedBean().getScLoa().getLoaAmt());
			}
		}
		
		return false;
	}
		
	public void setPk7DropDownValues() {
	    getManagedBean().setPk7Reasons(getReasonCodeDropdownValues(ReasonCategory.JUSTIFICATION_PK7.value()));
	}
	
	public void reasonChanged(String reasonCode) {
		getManagedBean().getScLoa().setJustificationType(getReasonByCategoryAndCode(
				ReasonCategory.JUSTIFICATION_PK7.value(), reasonCode));
	}
		
	public boolean isPk7Periodic() {
		PmParameter qtType = null;
		PmParameter fulfilmentType = null;

		if (null != getManagedBean().getScLoa().getQtType()) {
			qtType = getManagedBean().getScLoa().getQtType();
		}

		if (null != getManagedBean().getScLoa().getScQt() && null != getManagedBean().getScLoa().getScQt().getFulfilmentType()) {
			fulfilmentType = getManagedBean().getScLoa().getScQt().getFulfilmentType();
		}

		// #34413 - Start
		if (resetJustificatonPmReasonIfNull(qtType)) {
			return true;
		}
		// #34413 - End

		if (null != qtType
				&& null != fulfilmentType
				&& ParameterTypeConst.QUOTATION_TENDER_TYPE.equals(qtType.getParameterType())
				&& ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7.equals(qtType.getParameterCode())
				&& ((ParameterTypeConst.FULFILMENT_TYPE.equals(fulfilmentType.getParameterType()) 
				&& ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(fulfilmentType.getParameterCode()))
				|| (ParameterTypeConst.FULFILMENT_TYPE.equals(fulfilmentType.getParameterType()) 
				&& ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN.equals(fulfilmentType.getParameterCode())))) {

			if (null == getManagedBean().getScLoa().getJustificationType()) {
				getManagedBean().getScLoa().setJustificationType(new PmReason());
			}

			return true;
		}

		return false;
	}
	// #24540 - end -

	// #34413 - Start
	/**
	 * Method to return true, if dropdown of Special Item / Item In PK 7 is
	 * selected
	 * 
	 * @param qtType
	 * @return true or false
	 **/
	private Boolean resetJustificatonPmReasonIfNull(PmParameter qtType) {
		if (null != qtType && ParameterConst.QUOTATION_TENDER_TYPE_ITEM_IN_PK7.equals(qtType.getParameterCode())
				&& null != getManagedBean().getScLoa().getJustificationType()) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}
	// #34413 - End
		
	/* ***19336#83 - start */
	public String getReasonDesc(String reasonCode) {
		PmReason reason = getReasonByCategoryAndCode(ReasonCategory.JUSTIFICATION_PK7.value(), reasonCode);
		String reasonDesc = getReasonDesc(reason);

		if (!StringUtils.isEmpty(reasonDesc)) {
			return reasonDesc;
		}

		return "";
	}
	/* ***19336#83 - end */
	
	// Bug #33674@Syazani -- Start 14/09/2022
	private void retrieveValBankAccNoDropdown() {
		List<SmSupplierBank> lstSupplierBank = getManagedBean().getBankList();
		ScLoa tmpScLoa = getManagedBean().getScLoa();
		if (tmpScLoa != null && lstSupplierBank != null && lstSupplierBank.size() > 0 && tmpScLoa.getBankAccNo() != null) {
			for (SmSupplierBank ssb : lstSupplierBank) {
				if (ssb.getAccountNo().equalsIgnoreCase(tmpScLoa.getBankAccNo())
						&& ssb.getPmFinancialOrg().getFinOrgName().equalsIgnoreCase(tmpScLoa.getBankName())) {
					getManagedBean().setSupplierBankId(ssb.getSupplierBankId());
					break;
				}
			}
		}
	}
	// Bug #33674@Syazani -- End 14/09/2022
	
	// Bug #35078@Syazani -- Start 14/09/2023
	public boolean isQtAudit() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// Bug #35078@Syazani -- End 14/09/2023	

	// #36757 Start Vinod
	/**
	 * This method will validate unit price for tier standard and zone.
	 * 
	 * @param
	 * 
	 */
	private void validateTierStandardUnitPrice() {
		FacesScopes facesScopes = new FacesScopes();

		boolean showUnitPriceError = checkUnitPriceForProductAndServiceFromQuestions(this.getManagedBean()
				.getProdscQtDoc().getTplSpecMaster()) // product validation.
				|| checkUnitPriceForProductAndServiceFromQuestions(this.getManagedBean().getServicescQtDoc()
						.getTplSpecMaster());// Service validation.

		if (showUnitPriceError) {
			throw new EpException(MessageConst.ERROR_100022, new String[] {
					ResourceBundleUtils.getMessage(QTConstants.LBL_TPL_SPEC_CONTENT_UNIT_PRICE,
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, facesScopes.getUserLocale()), "" });
		}
	}
	// #36757 End Vinod

	// #36757 Start Vinod
	/**
	 * This is the common method to check both product and service if unit price
	 * is entered or not using questions.
	 * 
	 * @param specMaster
	 *            using this check to get questions and check if the unit price
	 *            is entered or not.
	 * @return boolean if user key in all the unit price this method returns
	 *         false if any one is null this method returns true to throw error.
	 */
	private boolean checkUnitPriceForProductAndServiceFromQuestions(TplSpecMaster specMaster) {
		TplSpecMaster specMasterLocal = specMaster;
		List<TplSpecQuestion> questions = specMasterLocal.getTplSpecQuestions();

		if (CollectionUtils.isEmpty(questions)) {
			return false;
		}

		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		String itemType = getParameterDescForReport(specMasterLocal.getItemTypeCode());
		boolean isProduct = itemType.equals(SSTConstant.produk) || itemType.equals(SSTConstant.product);

		for (TplSpecQuestion question : questions) {
			boolean isZoneItem = isZonal && TemplateSpecConst.QUESTION_TYPE_ITEM.equals(question.getQuestionType());
			boolean isProductTier = isProduct
					&& TemplateSpecConst.QUESTION_TYPE_ITEM.equals(question.getQuestionType())
					&& question.getClPriceTypeCode().getParameterCode().equals(ParameterConst.PRICE_TYPE_TIER);

			if ((isZoneItem || isProductTier) && checkUnitPriceisNullForTierAndZoneFromAnswers(question)) {
				return true;
			}
		}

		return false;
	}
	// #36757 End Vinod

	// #36757 Start Vinod
	/**
	 * This method will check whether the unit price is entered for all tiers
	 * and zones.
	 * 
	 * @param question
	 *            using this check all the answers whether they entered unit
	 *            price or not if any one of the unit price is null return false
	 *            to throw exception.
	 * @return boolean if user key in all the unit price this method returns
	 *         false if any one is null this method returns true to throw error.
	 */
	private boolean checkUnitPriceisNullForTierAndZoneFromAnswers(TplSpecQuestion question) {
		TplSpecQuestion questionLocal = question;

		if (CollectionUtils.isEmpty(questionLocal.getTplSpecAnswers())) {
			return true;
		}

		for (TplSpecAnswer specAnswer : questionLocal.getTplSpecAnswers()) {
			if (specAnswer.getRatePerUom() == null) {
				return true;
			}
		}

		return false;
	}
	// #36757 End Vinod
	
	// #32648 populate lampiran 7 if LOA at supplier - start
	public boolean isDpAuditOrQtAuditOrQtMaint() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE
				.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| SourcingConstants.PORTLET_NAME_DP_SEARCH
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
						|| QTConstants.PORTLET_NAME_DL_CREATE
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// #32648 populate lampiran 7 if LOA at supplier - end
	
	// #26130 - Start.
	/**
	 * @param scLoaReportAttachment
	 * @param scLoa
	 * @throws Exception
	 * @throws FileNotFoundException
	 */
	private void generaateUpdatedLampiraA3PDF(ScLoaReportAttachment scLoaReportAttachment, ScLoa scLoa)
			throws Exception, FileNotFoundException {
		VelocityContext context = new VelocityContext();
		ScQt scQt = getManagedBean().getScQt();

		if (scQt != null
				&& (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa().getDocType()) || ParameterConst.DOCTYPE_DIRECT_NEGO
						.equals(scLoa.getScLoiLoa().getDocType()))
				&& (ParameterConst.FULFILMENT_TYPE_ONE_OFF.equals(scQt.getFulfilmentType().getParameterCode())
						|| ParameterConst.FULFILMENT_TYPE_SCHEDULED.equals(scQt.getFulfilmentType().getParameterCode()) || ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
							.equals(scQt.getFulfilmentType().getParameterCode()))) {

			TplSpecMaster productSpecMaster = getManagedBean().getProdTemplateSpecBean().getSpecMaster();
			TplSpecMaster serviceSpecMaster = getManagedBean().getSpecTemplateSpecBean().getSpecMaster();
			List<TplSpecMaster> specMasters = null;

			if (null != productSpecMaster || null != serviceSpecMaster) {
				specMasters = new ArrayList<>();

				if (null != productSpecMaster) {
					specMasters.add(productSpecMaster);
				}

				if (null != serviceSpecMaster) {
					specMasters.add(serviceSpecMaster);
				}
			}

			constructSupplierSpecificationAndRemarks(context, scLoa, specMasters);

			// scLoa
			context.put("scLoa", scLoa);

			FileInputStream inputStream = null;

			try {
				String filePath = getActualFilePath(scLoaReportAttachment);

				if (filePath.endsWith(".gz")) {
					if (!filePath.endsWith("_merged.pdf.gz")) {
						inputStream = new FileInputStream(new File(filePath));
						GZIPInputStream gis = new GZIPInputStream(inputStream);
						filePath = filePath.replace(".pdf.gz", ".pdf");

						File pdfFile = new File(filePath);

						if (pdfFile.exists()) {
							pdfFile.delete();
						}

						Path path = FileSystems.getDefault().getPath(filePath);

						try {
							Files.copy(gis, path);
						} catch (Exception e) {
							log.error("DirectLOABackingBean: generaateUpdatedLampiraA3PDF: " + e.getMessage());
						}

						String tempFilePath = filePath.replace(".pdf", "_temp.pdf");
						File file = new File(filePath);

						mergePdf(file, filePath, tempFilePath, context);

						String destinatonZipFilepath = filePath.replace(".pdf", "_merged.pdf.gz");
						byte[] buffer = new byte[1024];
						FileInputStream fileInput = null;
						FileOutputStream fileOutputStream = null;
						GZIPOutputStream gzipOuputStream = null;

						try {
							fileOutputStream = new FileOutputStream(destinatonZipFilepath);
							gzipOuputStream = new GZIPOutputStream(fileOutputStream);
							fileInput = new FileInputStream(filePath);
							int bytes_read;

							while ((bytes_read = fileInput.read(buffer)) > 0) {
								gzipOuputStream.write(buffer, 0, bytes_read);
							}

							gzipOuputStream.finish();
						} catch (IOException ex) {
							ex.printStackTrace();
						} finally {
							if (null != fileInput)
								fileInput.close();
							if (null != fileOutputStream)
								fileOutputStream.close();
							if (null != gzipOuputStream)
								gzipOuputStream.close();
						}

						if (file.exists()) {
							file.delete();
						}

						if (!scLoaReportAttachment.getFilePath().endsWith("_merged.pdf.gz")) {
							String filepath = scLoaReportAttachment.getFilePath().replace(".pdf.gz", "_merged.pdf.gz");
							scLoaReportAttachment.setFilePath(filepath);
						}
					}
				} else if (filePath.endsWith(".pdf")) {
					if (!filePath.endsWith("_merged.pdf")) {
						String tempFilePath = filePath.replace(".pdf", "_temp.pdf");
						File file = new File(filePath);

						mergePdf(file, filePath, tempFilePath, context);

						file.renameTo(new File(filePath.replace(".pdf", "_merged.pdf")));

						scLoaReportAttachment.setFilePath(scLoaReportAttachment.getFilePath().replace(".pdf",
								"_merged.pdf"));
					}
				}
			} finally {
				if (null != inputStream)
					inputStream.close();
			}
		}
	}
	// #26130 - End.

	// #26130 - Start.
	/**
	 * @param file
	 * @param filePath
	 * @param tempFilePath
	 * @param context
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void mergePdf(File file, String filePath, String tempFilePath, VelocityContext context) throws IOException {
		PDDocument sstDocument = null;
		PDDocument lampiranA3Doc = null;

		try {
			sstDocument = PDDocument.load(file);
			int noOfPages = sstDocument.getNumberOfPages();

			List<TplSpecMaster> specMasters = (List<TplSpecMaster>) context.get("masters");
			boolean serviceItemFound = false;
			boolean productItemFound = false;

			if (CollectionUtils.isNotEmpty(specMasters) && specMasters.size() > 0) {
				for (TplSpecMaster tplSpecMaster : specMasters) {

					if (null != tplSpecMaster) {
						String itemType = getParameterDescForReport(tplSpecMaster.getItemTypeCode());

						if (itemType.equals("Perkhidmatan") || itemType.equals("Service")) {
							serviceItemFound = true;
						} else if (itemType.equals("Produk") || itemType.equals("Product")) {
							productItemFound = true;
						}
					}
				}

				context.put("specCombineProduct", retrieveDLSpecProduct(specMasters));
				context.put("specCombineService", retrieveDLSpecService(specMasters));
			}

			StringBuffer sb = new StringBuffer();
			sb.append(noOfPages);
			sb.append(" ");
			sb.append("daripada");
			sb.append(" ");

			if ((productItemFound && serviceItemFound) || (productItemFound && !serviceItemFound)) {
				noOfPages = noOfPages + 1;
			}

			sb.append(noOfPages);

			context.put("pageNo", sb.toString());

			if ((productItemFound && serviceItemFound) || (productItemFound && !serviceItemFound)) {
				sb = new StringBuffer();
				sb.append(noOfPages);
				sb.append(" ");
				sb.append("daripada");
				sb.append(" ");
				sb.append(noOfPages);

				context.put("serviceItemPageNo", sb.toString());

				noOfPages = noOfPages - 1;
			}

			if (!productItemFound && serviceItemFound) {
				context.put("serviceItemPageNo", sb.toString());
			}

			DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOA_AGREEMENT_LAMPIRAN_A3,
					context, new FileOutputStream(tempFilePath));

			if (noOfPages >= 14) {
				sstDocument.removePage(noOfPages - 1);
			}

			lampiranA3Doc = PDDocument.load(tempFilePath);

			for (int i = 0; i < noOfPages - 1; i++) {
				PDPage page = (PDPage) sstDocument.getDocumentCatalog().getAllPages().get(i);
				page.setMediaBox(PDPage.PAGE_SIZE_A4);
			}

			noOfPages = lampiranA3Doc.getNumberOfPages();

			for (int i = 0; i < noOfPages; i++) {
				PDPage page = (PDPage) lampiranA3Doc.getDocumentCatalog().getAllPages().get(i);
				page.setMediaBox(PDPage.PAGE_SIZE_A4);

				sstDocument.addPage(page);
			}

			sstDocument.save(filePath);

			File tempFile = new File(tempFilePath);

			if (tempFile.exists()) {
				tempFile.delete();
			}
		} catch (Exception e) {
			log.error("DirectLOABackingBean: mergePdf(): error message is: " + e.getMessage());
		} finally {
			if (null != sstDocument)
				sstDocument.close();
			if (null != lampiranA3Doc)
				lampiranA3Doc.close();
		}
	}
	// #26130 - End.

	// #26130 start
	public TplSpecQuestion retrieveDLSpecService(List<TplSpecMaster> specMasters) {
		TplSpecQuestion specQuestion = new TplSpecQuestion();
		List<List<HashMap<String, Object>>> listDL = new ArrayList<>();
		List<HashMap<String, Object>> listSpecDL = new ArrayList<>();

		for (TplSpecMaster master : specMasters) {

			String itemType = getParameterDescForReport(master.getItemTypeCode());
			// retrieve master value

			if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
				for (TplSpecQuestion question : master.getTplSpecQuestions()) {
					if (question.getTplSpecZoneQties().size() > 0) {
						// zonal
						if (question.getQuestionType().equalsIgnoreCase("I")) {
							listDL.add(retrieveZonalSpec(question, master.getSpecTypeCode().getParameterCode(),
									itemType));
						}
					} else {
						// non zonal
						if (question.getQuestionType().equalsIgnoreCase("I")) {
							listSpecDL.add(retrieveNonZonalSpec(question, master.getSpecTypeCode().getParameterCode(),
									getParameterDescForReport(master.getItemTypeCode())));
						}
					}
				}

				specQuestion.setListOfZonalService(listDL);
				specQuestion.setListOfNonZonalService(listSpecDL);
			}
		}

		return specQuestion;
	}
	// #26130 -end-

	// #26130 -start-
	public TplSpecQuestion retrieveDLSpecProduct(List<TplSpecMaster> specMasters) {
		TplSpecQuestion specQuestion = new TplSpecQuestion();
		List<List<HashMap<String, Object>>> listDL = new ArrayList<>();
		List<HashMap<String, Object>> listSpecDL = new ArrayList<>();

		for (TplSpecMaster master : specMasters) {

			String itemType = getParameterDescForReport(master.getItemTypeCode());
			// retrieve master value

			if (itemType.equalsIgnoreCase("Produk") || itemType.equalsIgnoreCase("Product")) {
				for (TplSpecQuestion question : master.getTplSpecQuestions()) {
					if (question.getTplSpecZoneQties().size() > 0) {
						// zonal
						if (question.getQuestionType().equalsIgnoreCase("I")) {
							listDL.add(retrieveZonalSpec(question, master.getSpecTypeCode().getParameterCode(),
									itemType));
						}
					} else {
						// non zonal
						if (question.getQuestionType().equalsIgnoreCase("I")) {
							listSpecDL.add(retrieveNonZonalSpec(question, master.getSpecTypeCode().getParameterCode(),
									getParameterDescForReport(master.getItemTypeCode())));
						}
					}
				}

				specQuestion.setListOfZonalProduct(listDL);
				specQuestion.setListOfNonZonalProduct(listSpecDL);
			}
		}

		return specQuestion;
	}
	// #26130 -end-

	// #26130 -start-
	public HashMap<String, Object> retrieveNonZonalSpec(TplSpecQuestion question, String specType, String itemType) {
		HashMap<String, Object> map = new HashMap<>();
		List<String> listSpec = new ArrayList<>();
		String defaultValueBM = "T/B";
		Boolean getFirstValue = true;
		String whiteSpace = "&nbsp;";

		map = new HashMap<>();
		map.put("clItem", question.getClItem());
		map.put("uomName", question.getClUom().getUomName());
		map.put("spesifikasi", whiteSpace);

		if (itemType.equalsIgnoreCase("Product") || itemType.equalsIgnoreCase("produk")) {
			map.put("clPriceTypeCode", getParameterDesc(question.getClPriceTypeCode(), SourcingConstants.MY_LOCALE));
		}

		if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
			map.put("clUomFrequency", (null != question.getClUomFrequency()) ? question.getClUomFrequency().toString()
					: getDefaultValue());
			map.put("clUomPerDay", (null != question.getClUomPerDay()) ? question.getClUomPerDay().toString()
					: getDefaultValue());
			map.put("clUomPerMonth", (null != question.getClUomPerMonth()) ? question.getClUomPerMonth().toString()
					: getDefaultValue());
		}
		// end
		map.put("qty", (null != question.getClQuantity()) ? question.getClQuantity().toString() : getDefaultValue());

		if (null != question.getTplSpecTierQties() && question.getTplSpecTierQties().size() > 0) {
			// ada tier
			if (getFirstValue) {
				String tierValue = retrieveTierQuantity(question.getTplSpecTierQties());

				map.put("tierQty", tierValue);
			} else {
				map.put("tierQty", whiteSpace);
			}
		} else {
			if (getFirstValue) {
				map.put("tierQty", defaultValueBM);
			} else {
				map.put("tierQty", whiteSpace);
			}
		}

		if (question.getQuestionType().equalsIgnoreCase("I")) {
			for (String specVal : question.getSpecList()) {
				listSpec.add(specVal);
			}

			map.put("specVal", listSpec);
		}

		map.put("specType", specType);
		map.put("itemType", itemType);

		int rowspan = question.getSpecList().size() + 1;

		map.put("rowSpan", rowspan);

		return map;
	}
	// #26130 -end-

	// #26130 -start-
	public List<HashMap<String, Object>> retrieveZonalSpec(TplSpecQuestion question, String specType, String itemType) {
		HashMap<String, Object> map = new HashMap<>();
		int zoneQty = 1;
		String whiteSpace = "&nbsp;";
		List<HashMap<String, Object>> listZonalDLSpec = new ArrayList<HashMap<String, Object>>();
		List<String> listSpec = new ArrayList<>();

		if (question.getQuestionType().equalsIgnoreCase("I")) {
			map.put("questionType", question.getQuestionType());

			if (specType.equalsIgnoreCase("F")) {
				Boolean getFirstValue = true;

				for (TplSpecAnswer answer : question.getTplSpecAnswers()) {
					map = new HashMap<>();

					if (getFirstValue) {
						// start
						map.put("clItem", question.getClItem());
						map.put("uomName", question.getClUom().getUomName());
						map.put("spesifikasi", whiteSpace);

						if (itemType.equalsIgnoreCase("Product") || itemType.equalsIgnoreCase("produk")) {
							map.put("clPriceTypeCode", getParameterDesc(question.getClPriceTypeCode(),SourcingConstants.MY_LOCALE));
						}

						if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
							map.put("clUomFrequency", (null != question.getClUomFrequency()) ? question
									.getClUomFrequency().toString() : getDefaultValue());
							map.put("clUomPerDay", (null != question.getClUomPerDay()) ? question.getClUomPerDay()
									.toString() : getDefaultValue());
							map.put("clUomPerMonth", (null != question.getClUomPerMonth()) ? question
									.getClUomPerMonth().toString() : getDefaultValue());
						}
						// end
						map.put("zoneName",
								(null != answer.getTplSpecZoneQty().getTplSpecZoneDtl().getZoneName()) ? answer
										.getTplSpecZoneQty().getTplSpecZoneDtl().getZoneName() : getDefaultValue());
						map.put("qty", (null != answer.getTplSpecZoneQty().getQty()) ? answer.getTplSpecZoneQty()
								.getQty().toString() : getDefaultValue());
						map.put("answerNumeric", (null != answer.getAnswerNumeric()) ? answer.getAnswerNumeric()
								.toString() : getDefaultValue());

						getFirstValue = false;
						zoneQty = zoneQty + 1;
					} else {
						// second zone onward
						map.put("clItem", whiteSpace);
						map.put("uomName", whiteSpace);
						map.put("spesifikasi", whiteSpace);

						if (itemType.equalsIgnoreCase("Product") || itemType.equalsIgnoreCase("produk")) {
							map.put("clPriceTypeCode", whiteSpace);
						}

						if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
							map.put("clUomFrequency", whiteSpace);
							map.put("clUomPerDay", whiteSpace);
							map.put("clUomPerMonth", whiteSpace);
						}
						// end
						map.put("zoneName",
								(null != answer.getTplSpecZoneQty().getTplSpecZoneDtl().getZoneName()) ? answer
										.getTplSpecZoneQty().getTplSpecZoneDtl().getZoneName() : getDefaultValue());
						map.put("qty", (null != answer.getTplSpecZoneQty().getQty()) ? answer.getTplSpecZoneQty()
								.getQty().toString() : getDefaultValue());
						map.put("answerNumeric", (null != answer.getAnswerNumeric()) ? answer.getAnswerNumeric()
								.toString() : getDefaultValue());

						if (zoneQty == question.getTplSpecAnswers().size()) {
							for (String specVal : question.getSpecList()) {
								listSpec.add(specVal);
							}

							map.put("specVal", listSpec);
						}
						zoneQty = zoneQty + 1;
					}

					map.put("specType", specType);
					map.put("itemType", itemType);

					int rowspan = question.getSpecList().size() + question.getTplSpecZoneQties().size();

					map.put("rowSpan", rowspan);

					listZonalDLSpec.add(map);
				}

			} else if (specType.equalsIgnoreCase("T")) {
				String defaultValueBM = "T/B";
				Boolean getFirstValue = true;

				for (TplSpecZoneQty zone : question.getTplSpecZoneQties()) {
					map = new HashMap<>();

					if (getFirstValue) {
						// start
						map.put("clItem", question.getClItem());
						map.put("uomName", question.getClUom().getUomName());
						map.put("spesifikasi", whiteSpace);

						if (itemType.equalsIgnoreCase("Product") || itemType.equalsIgnoreCase("produk")) {
							map.put("clPriceTypeCode", getParameterDesc(question.getClPriceTypeCode(),SourcingConstants.MY_LOCALE));
						}
						
						if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
							map.put("clUomFrequency", (null != question.getClUomFrequency()) ? question
									.getClUomFrequency().toString() : getDefaultValue());
							map.put("clUomPerDay", (null != question.getClUomPerDay()) ? question.getClUomPerDay()
									.toString() : getDefaultValue());
							map.put("clUomPerMonth", (null != question.getClUomPerMonth()) ? question
									.getClUomPerMonth().toString() : getDefaultValue());

						}
						// end
						map.put("zoneName", (null != zone.getTplSpecZoneDtl().getZoneName()) ? zone.getTplSpecZoneDtl()
								.getZoneName() : getDefaultValue());
						map.put("qty", (null != zone.getQty()) ? zone.getQty().toString() : getDefaultValue());

						if (zone.getTplSpecQuestion().getTplSpecTierQties().size() > 0) {
							// ada tier
							String tierValue = retrieveTierQuantity(zone.getTplSpecQuestion().getTplSpecTierQties());

							if (getFirstValue) {
								map.put("tierQty", tierValue);
							} else {
								map.put("tierQty", whiteSpace);
							}
						} else {
							if (getFirstValue) {
								map.put("tierQty", defaultValueBM);
							} else {
								map.put("tierQty", whiteSpace);
							}
						}

						getFirstValue = false;

						if (zoneQty == question.getTplSpecZoneQties().size()) {
							for (String specVal : question.getSpecList()) {
								listSpec.add(specVal);
							}

							map.put("specVal", listSpec);
						}

						zoneQty = zoneQty + 1;
					} else {
						// second zonal onward start
						map.put("clItem", whiteSpace);
						map.put("uomName", whiteSpace);
						map.put("spesifikasi", whiteSpace);

						if (itemType.equalsIgnoreCase("Product") || itemType.equalsIgnoreCase("produk")) {
							map.put("clPriceTypeCode", whiteSpace);
						}

						if (itemType.equalsIgnoreCase("Service") || itemType.equalsIgnoreCase("Perkhidmatan")) {
							map.put("clUomFrequency", whiteSpace);
							map.put("clUomPerDay", whiteSpace);
							map.put("clUomPerMonth", whiteSpace);
						}
						// end
						map.put("zoneName", (null != zone.getTplSpecZoneDtl().getZoneName()) ? zone.getTplSpecZoneDtl()
								.getZoneName() : getDefaultValue());
						map.put("qty", (null != zone.getQty()) ? zone.getQty().toString() : getDefaultValue());

						if (zone.getTplSpecQuestion().getTplSpecTierQties().size() > 0) {
							// ada tier
							if (getFirstValue) {
								String tierValue = retrieveTierQuantity(zone.getTplSpecQuestion().getTplSpecTierQties());

								map.put("tierQty", tierValue);
							} else {
								map.put("tierQty", whiteSpace);
							}
						} else {
							if (getFirstValue) {
								map.put("tierQty", defaultValueBM);
							} else {
								map.put("tierQty", whiteSpace);
							}
						}

						if (zoneQty == question.getTplSpecZoneQties().size()) {
							for (String specVal : question.getSpecList()) {
								listSpec.add(specVal);
							}

							map.put("specVal", listSpec);
						}

						zoneQty = zoneQty + 1;
					}

					map.put("specType", specType);
					map.put("itemType", itemType);

					int rowspan = question.getSpecList().size() + question.getTplSpecZoneQties().size();

					map.put("rowSpan", rowspan);

					listZonalDLSpec.add(map);
				}
			} else {
				String defaultValueBM = "T/B";

				map.put("zoneName", getDefaultValue());
				map.put("qty", getDefaultValue());
				map.put("tierQty", defaultValueBM);
				map.put("spesifikasi", "&nbsp");

				if (zoneQty == question.getTplSpecZoneQties().size()) {
					for (String specVal : question.getSpecList()) {
						listSpec.add(specVal);
					}

					map.put("specVal", listSpec);
				}

				zoneQty = zoneQty + 1;

				map.put("specType", specType);
				map.put("itemType", itemType);

				int rowspan = question.getSpecList().size() + question.getTplSpecZoneQties().size();

				map.put("rowSpan", rowspan);

				listZonalDLSpec.add(map);
			}
		}

		return listZonalDLSpec;
	}
	// #26130 -end-

	// #26130 -start-
	public String retrieveTierQuantity(List<TplSpecTierQty> listTplSpecTierQties) {
		StringBuffer sb = new StringBuffer();

		for (TplSpecTierQty qty : listTplSpecTierQties) {
			sb.append(qty.getFromQty() + " - " + qty.getToQty() + "<br />");
		}

		return sb.toString();
	}
	// #26130 -end-

	// #26130 - start
	public String getDefaultValue() {
		String defaultValue = "T/B";

		return defaultValue;
	}
	// #26130 - end

	/**
	 * 29007-start Get Parameter description by parameter for report.
	 * 
	 * @param parameter
	 *            the PmParameter instance.
	 * @return parameter description
	 */
	public String getParameterDescForReport(PmParameter parameter) {
		if (parameter != null && StringUtils.isNotBlank(parameter.getParameterType())) {
			return getParameterDesc(parameter, myLocale);
		} else {
			return null;
		}
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	public String getItemNameSearchCriteria() {
		return itemNameSearchCriteria;
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	public void setItemNameSearchCriteria(String itemNameSearchCriteria) {
		this.itemNameSearchCriteria = itemNameSearchCriteria;
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	public String getLampiranA3SubStr() {
		return lampiranA3SubStr;
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	public void setLampiranA3SubStr(String lampiranA3SubStr) {
		this.lampiranA3SubStr = lampiranA3SubStr;
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	/**
	 * This method calls when user click on reset all button in new offered
	 * specification screen.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void onResetSupplierCriteria() {
		setItemNameSearchCriteria(null);
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be triggered when user click on back and complete button
	 * in new offered specification screen.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void onBackToZoneItemDetailsTab() {
		getManagedBean().setSelectedTabIndex(2);
		this.getManagedBean().setSpecKeyword(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be triggered when user click reset on the particular
	 * specification in new offered specification screen.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void resetSpecKeyword() {
		this.getManagedBean().setSpecKeyword(null);
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	/**
	 * This method will be triggered when user click edit icon on the item in
	 * zone/item screen to retrive specifications based on item name.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void searchSupplierItemByName() {
		if (this.getItemNameSearchCriteria() != null && this.getItemNameSearchCriteria() != "") {
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

			if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster().isBrandNew()) {
				reqDTO.setTplSpecMaster(this.getManagedBean().getProdscQtDoc().getTplSpecMaster());
				reqDTO.setItemName(this.getItemNameSearchCriteria());

				AwardSupplierResDTO resDTO = getQtAwardSO().searchSupplierItemByIDforDL(reqDTO);

				TemplateSpecBean bean = this.getManagedBean().getTemplateSpecBean();
				bean.setSpecMaster(resDTO.getTplSpecMaster());
				bean.setSpecAnswerList(resDTO.getTplSpecAnswerList());
				bean.setSelectedSpecQuestion(null);
				bean.setSpecAnswersToDelete(null);

				this.getManagedBean().setSearchItemForDL(true);

				bean.setWordBreakForQT(true);

				this.getManagedBean().setProdTemplateSpecBean(bean);
			}

			if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster().isBrandNew()) {
				reqDTO.setTplSpecMaster(this.getManagedBean().getServicescQtDoc().getTplSpecMaster());
				reqDTO.setItemName(this.getItemNameSearchCriteria());

				AwardSupplierResDTO resDTO = getQtAwardSO().searchSupplierItemByIDforDL(reqDTO);
				TemplateSpecBean serviceBean = new TemplateSpecBean(TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
				serviceBean.setSpecMaster(resDTO.getTplSpecMaster());
				serviceBean.setSpecAnswerList(resDTO.getTplSpecAnswerList());
				serviceBean.setSelectedSpecQuestion(null);
				serviceBean.setSpecAnswersToDelete(null);

				this.getManagedBean().setSearchItemForDL(true);

				serviceBean.setWordBreakForQT(true);

				this.getManagedBean().setSpecTemplateSpecBean(serviceBean);
			}
		} else {
			this.getManagedBean().setSearchItemForDL(false);

			TemplateSpecBean bean = new TemplateSpecBean(TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			bean.setSpecMaster(this.getManagedBean().getProdscQtDoc().getTplSpecMaster());
			bean.setWordBreakForQT(true);

			this.getManagedBean().setProdTemplateSpecBean(bean);
			this.getManagedBean().setProdFirst(0);

			bean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());

			if (!this.getManagedBean().getProdscQtDoc().getTplSpecMaster().isBrandNew()) {

				this.getManagedBean().getProdscQtDoc()
						.setTplSpecMaster(this.getManagedBean().getProdTemplateSpecBean().getSpecMaster());
			}

			TemplateSpecBean serviceBean = new TemplateSpecBean(TemplateSpecConst.CONTENT_TYPE_QUESTION_AND_ASNWER);
			serviceBean.setSpecMaster(this.getManagedBean().getServicescQtDoc().getTplSpecMaster());
			serviceBean.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
			serviceBean.setWordBreakForQT(true);

			this.getManagedBean().setSpecTemplateSpecBean(serviceBean);
			this.getManagedBean().setServFirst(0);

			if (!this.getManagedBean().getServicescQtDoc().getTplSpecMaster().isBrandNew()) {
				TemplateSpecBackingBean backingBean = new TemplateSpecBackingBean();

				backingBean.calcGrandTotalAmount(serviceBean);

				this.getManagedBean().getServicescQtDoc()
						.setTplSpecMaster(this.getManagedBean().getSpecTemplateSpecBean().getSpecMaster());
			}
		}
	}
	// #29007 -end-

	/*#29007 Start Vinod*/
	/**
	 * This method will be triggered when user click on search button in new
	 * offered specification screen to search specific specifications.
	 * 
	 * @param tplSpecQuestion1
	 * @param mainSearch
	 * 
	 * @return
	 */
	public void searchSupplierSpecification(TplSpecQuestion tplSpecQuestion1, boolean mainSearch) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		ScLoiLoaItem loiLoaItem = new ScLoiLoaItem();
		boolean qtMaint = isQTMaintenanceScreen();
		boolean checkLoiLoaItems = false;

		if (QTStatusConst.LOA_PENDING_VERIFICATION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				&& !qtMaint) {
			getManagedBean().setSupPropSpecDisable(false);
			checkLoiLoaItems = true;
		} else if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(getManagedBean().getScLoa().getPmStatus().getStatusId())
				&& !qtMaint) {
			getManagedBean().setSupPropSpecDisable(false);
		} else {
			getManagedBean().setSupPropSpecDisable(true);
			checkLoiLoaItems = true;
		}
		
		if(checkLoiLoaItems){
			if (CollectionUtils.isEmpty(this.getManagedBean().getScLoa().getScLoiLoaItems())) {
				DirectAwardReqDTO awardReqDTOnew = new DirectAwardReqDTO();
				awardReqDTOnew.setScLoa(this.getManagedBean().getScLoa());

				DirectAwardResDTO awardResDTOnew = getQtDirectAwardSO().saveScloiloaitemforDirectLOA(awardReqDTOnew);

				if (awardResDTOnew.isSuccessful()) {
					this.getManagedBean().getScLoa().setScLoiLoaItems(awardResDTOnew.getLoa().getScLoiLoaItems());
				}
			}
		}

		for (ScLoiLoaItem scloiloaitem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {

			Long requestItemQuestionId = scloiloaitem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId();
			Long specQuestionId = tplSpecQuestion1.getSpecQuestionId();
			String requestItemQuestionName = scloiloaitem.getScRequestItem().getTplSpecQuestion().getClItem();
			String specQuestionName = tplSpecQuestion1.getClItem();

			if (scloiloaitem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId()
					.equals(tplSpecQuestion1.getSpecQuestionId())
					|| scloiloaitem.getScRequestItem().getTplSpecQuestion().getSpecQuestionId() == tplSpecQuestion1
							.getSpecQuestionId()
					|| scloiloaitem.getScRequestItem().getTplSpecQuestion().getClItem().trim().replaceAll("\\s", "")
							.equals(tplSpecQuestion1.getClItem().trim().replaceAll("\\s", ""))) { // 29007&31423&31638
				loiLoaItem = scloiloaitem;

				break;
			}
		}

		this.getManagedBean().setItemNamePopup(loiLoaItem.getScRequestItem().getItemName());
		this.getManagedBean().setScLoiLoaItem(loiLoaItem);
		this.getManagedBean().setNewSupplierJustification(loiLoaItem.getLoaSpecJustification());

		specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
		specReqDto.setScQt(this.getManagedBean().getScQt());
		specReqDto.setScLoiLoaItem(loiLoaItem);

		if (this.getManagedBean().getSpecKeyword() != null) {
			TplSpecQuestion tplSpecQuestion2 = new TplSpecQuestion();
			TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();

			tplSpecQuestion2.setClSpecDetail(this.getManagedBean().getSpecKeyword());
			tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion2);

			specReqDto.setTplSpecAnswer(tplSpecAnswer);
		}

		specReqDto.setSpecSearchForApp(true);

		TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);
		List<TplSpecAnswer> afterTierCheckAnswerList = new ArrayList<>();

		for (TplSpecAnswer tplSpecAnswer : templateSpecResDTO.getSpecAnswerList()) {
			int contains = 0;

			if (tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != "") {
				tplSpecAnswer.setRenderNewSpec(true);
			}

			for (TplSpecAnswer answer : afterTierCheckAnswerList) {
				if (answer.getTplSpecQuestion() != null
						&& tplSpecAnswer.getTplSpecQuestion() != null
						&& answer.getTplSpecQuestion().getSpecQuestionId() == tplSpecAnswer.getTplSpecQuestion()
								.getSpecQuestionId()) {
					contains++;
					break;
				}
			}

			boolean qtMaintCheck = qtMaint
					|| (QTStatusConst.LOA_COMPLETED.equals(getManagedBean().getScLoa().getPmStatus().getStatusId()) && !qtMaint);
			if (qtMaintCheck
					&& (afterTierCheckAnswerList.isEmpty() || !afterTierCheckAnswerList.isEmpty() && contains == 0)) {
				afterTierCheckAnswerList.add(tplSpecAnswer);
			} else {
				continue;
			}
		}

		if (qtMaint
				|| (QTStatusConst.LOA_COMPLETED.equals(getManagedBean().getScLoa().getPmStatus().getStatusId()) && !qtMaint)) {
			this.getManagedBean().setTplSpecAnswerList(afterTierCheckAnswerList);
		} else {
			this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
		}

		this.getManagedBean().setScLoiLoaItem(loiLoaItem);

		if (mainSearch) {
			this.getManagedBean().setTplSpecQuestion(tplSpecQuestion1);

			PortletSession portletSession = (PortletSession) FacesContext.getCurrentInstance().getExternalContext()
					.getSession(false);
			portletSession.setAttribute(QTConstants.PORTLET_ATTRIBUTE_NEW_OFFERED_SPECIFICATION, loiLoaItem);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.REDIRECT_URL_NEW_OFFERED_SPECIFICATION);
		}
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be triggered when user click save or complete button in
	 * new offered specification screen to save the changes.
	 * 
	 * @param isComplete
	 * 
	 * @return
	 */
	public void saveSupplierSpecification(boolean isComplete) {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		IQTAwardSO iQTAwardSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTAwardSO.class);
		AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();

		if (this.getManagedBean().getTplSpecAnswerList() != null
				|| this.getManagedBean().getNewSupplierJustification() != null) {
			if (this.getManagedBean().getScQt().isZonal()) {
				for (ScLoiLoaItem zoneLoiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (null != this.getManagedBean().getScLoiLoaItem()
							&& zoneLoiLoaItem.getLoiLoaItemId() == this.getManagedBean().getScLoiLoaItem()
									.getLoiLoaItemId()
							|| zoneLoiLoaItem.getLoiLoaItemId().equals(
									this.getManagedBean().getScLoiLoaItem().getLoiLoaItemId())) {
						zoneLoiLoaItem.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());
					}
				}

				reqDTO.setScLoiLoaItemList(this.getManagedBean().getScLoa().getScLoiLoaItems());

				AwardSupplierResDTO resDTOLatest = getQtAwardSO().saveZoneItemListForResetAll(reqDTO);

				if (resDTOLatest.isSuccessful()) {
					ScLoa scLoa = this.getManagedBean().getScLoa();
					scLoa.setScLoiLoaItems(resDTOLatest.getScLoiLoaItemList());
				}

				for (ScLoiLoaItem updatedLoiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (updatedLoiLoaItem.getLoiLoaItemId() == this.getManagedBean().getScLoiLoaItem()
							.getLoiLoaItemId()
							|| updatedLoiLoaItem.getLoiLoaItemId().equals(
									this.getManagedBean().getScLoiLoaItem().getLoiLoaItemId())) {
						this.getManagedBean().setScLoiLoaItem(updatedLoiLoaItem);
						break;
					}
				}

				reqDTO = new AwardSupplierReqDTO();
			}

			ScLoiLoaItem scLoiLoaItem = this.getManagedBean().getScLoiLoaItem();
			AwardSupplierReqDTO reqDTOLatest = new AwardSupplierReqDTO();
			reqDTOLatest.setScLoiLoaItem(scLoiLoaItem);

			AwardSupplierResDTO resDTOLatest = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest);

			if (resDTOLatest.getScLoiLoaItem() != null) {
				scLoiLoaItem = resDTOLatest.getScLoiLoaItem();
			}

			scLoiLoaItem.setComplete(isComplete);

			if (isComplete) {
				if (null == this.getManagedBean().getNewSupplierJustification()
						|| StringUtils.isBlank(this.getManagedBean().getNewSupplierJustification())
						|| StringUtils.isEmpty(this.getManagedBean().getNewSupplierJustification())) {
					String errorMessage = ResourceBundleUtils.getMessage("lbl.sc.justification",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
					throw new EpException(MessageConst.ERROR_775185, new String[] { errorMessage });
				}
			}

			if (this.getManagedBean().getNewSupplierJustification() != null
					&& this.getManagedBean().getNewSupplierJustification() != "") {
				int newoffered = 0;

				for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
					if (tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != "") {
						newoffered++;
					}
				}

				if (isComplete && newoffered < 1) {
					int index = 0;

					for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (loiLoaItem.getLoiLoaItemId() == scLoiLoaItem.getLoiLoaItemId()
								|| loiLoaItem.getLoiLoaItemId().equals(scLoiLoaItem.getLoiLoaItemId())) {
							this.getManagedBean().getScLoa().getScLoiLoaItems().set(index, scLoiLoaItem);
							break;
						}

						index++;
					}

					throw new EpException(MessageConst.ERROR_775170);
				}
			}

			scLoiLoaItem.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());

			reqDTO.setTplSpecAnswerList(this.getManagedBean().getTplSpecAnswerList());
			reqDTO.setScLoiLoaItem(scLoiLoaItem);

			AwardSupplierResDTO resDTO = iQTAwardSO.saveSupplierSpecification(reqDTO);
			int index = 0;

			for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (loiLoaItem.getLoiLoaItemId() == resDTO.getScLoiLoaItem().getLoiLoaItemId()
						|| loiLoaItem.getLoiLoaItemId().equals(resDTO.getScLoiLoaItem().getLoiLoaItemId())) {
					this.getManagedBean().getScLoa().getScLoiLoaItems().remove(index);
					this.getManagedBean().getScLoa().getScLoiLoaItems().add(index, resDTO.getScLoiLoaItem());
					break;
				}

				index++;
			}

			List<ScLoiLoaItem> items = new ArrayList<>();
			AwardSupplierReqDTO reqDTONew = new AwardSupplierReqDTO();

			for (ScLoiLoaItem item : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (!scLoiLoaItem.getLoiLoaItemId().equals(item.getLoiLoaItemId())
						&& scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
								.equals(item.getScRequestItem().getRequestNoteItemId())) {
					reqDTOLatest.setScLoiLoaItem(item);

					item = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest).getScLoiLoaItem();

					reqDTONew.setTplSpecAnswerList(null);

					item.setLoaSpecJustification(this.getManagedBean().getNewSupplierJustification());
					item.setComplete(resDTO.getScLoiLoaItem().isComplete());

					reqDTONew.setScLoiLoaItem(item);

					getQtAwardSO().saveSupplierSpecification(reqDTONew);

					items.add(item);
				}
			}

			for (ScLoiLoaItem item : items) {
				int indexNew = 0;

				reqDTOLatest.setScLoiLoaItem(item);

				item = getQtAwardSO().retriveLatestLoiLoaItemDetails(reqDTOLatest).getScLoiLoaItem();

				for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (loiLoaItem.getLoiLoaItemId() == item.getLoiLoaItemId()
							|| loiLoaItem.getLoiLoaItemId().equals(item.getLoiLoaItemId())) {
						if (null != this.getManagedBean().getScLoa() && this.getManagedBean().getScLoa().isBrandNew()) {
							resDTO.getScLoiLoaItem().setScLoa(this.getManagedBean().getScLoa());
						}

						this.getManagedBean().getScLoa().getScLoiLoaItems().remove(indexNew);
						this.getManagedBean().getScLoa().getScLoiLoaItems().add(indexNew, item);
						break;
					}

					indexNew++;
				}
			}

			if (this.getManagedBean().getScQt().isZonal() && !isComplete) {
				for (ScLoiLoaItem loacalloiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
					if (resDTO.getScLoiLoaItem().getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId())
							|| resDTO.getScLoiLoaItem().getScRequestItem().getRequestNoteItemId()
									.equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())) {
						loacalloiLoaItem.setComplete(isComplete);
					}
				}
			}

			if (resDTO.isSuccessful()) {
				createLampiranA3forQT(isComplete);

				if (isComplete) {
					onBackToZoneItemDetailsTab();
				} else {
					TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();

					this.getManagedBean().setScLoiLoaItem(resDTO.getScLoiLoaItem());

					if (this.getManagedBean().getSpecKeyword() != null) {
						TplSpecQuestion tplSpecQuestion = new TplSpecQuestion();
						TplSpecAnswer tplSpecAnswer = new TplSpecAnswer();

						specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
						specReqDto.setScQt(this.getManagedBean().getScQt());

						tplSpecQuestion.setClSpecDetail(this.getManagedBean().getSpecKeyword());
						tplSpecAnswer.setTplSpecQuestion(tplSpecQuestion);
						specReqDto.setScLoiLoaItem(resDTO.getScLoiLoaItem());

						specReqDto.setSpecSearchForApp(true);
					} else {
						specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
						specReqDto.setScQt(this.getManagedBean().getScQt());
						specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
						specReqDto.setSpecSearchForApp(true);
					}

					TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);

					this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());

					for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
						if (tplSpecAnswer.getNewSupplierOffer() != null && tplSpecAnswer.getNewSupplierOffer() != "") {
							tplSpecAnswer.setRenderNewSpec(true);
						}
					}

					successMessage(MessageConst.INFO_700004,
							getMessageCommon("lbl.sc.loiloa.supplier.specification.save"), "", Boolean.FALSE);
				}
			}
		}
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	public String populateLabelLampiranA3(String dateOfUpdate, String deskOfficerName) {
		FacesScopes facesScopes = new FacesScopes();
		return ResourceBundleUtils.getMessage(QTConstants.LAMPIRAN_A3_LBL,
				new String[] { dateOfUpdate, deskOfficerName }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				facesScopes.getUserLocale());
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	private void createLampiranA3forQT(boolean isComplete) {
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		List<ScLoaReport> oldLampiranA3List = new ArrayList<>();
		int recordCount = 0;

		if (isComplete) {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setTitle(populateLabelLampiranA3(new SimpleDateFormat(
								CommonConstants.DEFAULT_DATETIME_FORMAT).format(DateUtils.getCurrentDate()),
								SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy()).getUserName()));
						report.setIslampiranA3(true);

						recordCount++;
					}

					oldLampiranA3List.add(report);
				}
			}

			if (recordCount > 0) {
				getManagedBean().getScLoa().setScLoaReports(oldLampiranA3List);

				awardReqDTO.setScLoa(getManagedBean().getScLoa());

				AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();

				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);

				getManagedBean().setScLoa(scLoa);
			} else {
				ScLoaReport lampiranA3 = getQtAwardSO()
						.createLampiranA3(
								getManagedBean().getScLoa(),
								populateLabelLampiranA3(new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT)
										.format(DateUtils.getCurrentDate()),
										SmCommonUtil.getUserByUserId(getManagedBean().getScLoa().getCreatedBy())
												.getUserName()));

				lampiranA3.setIslampiranA3(true);
				lampiranA3.setScLoa(getManagedBean().getScLoa());

				IParameterSO parameterSO = null;

				try {
					parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);

					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.DOCUMENT_DEFINITION_TYPE);
					reqDTO.setParameterCode(ParameterConst.DOCUMENT_DEFINITION_TYPE_SUPPLIER_UPLOAD_AND_DOWNLOAD);

					ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);

					lampiranA3.setDefinationType(resDTO.getPmParameter());
					lampiranA3.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_QUOTATION_TENDER); // AQ
				} catch (Exception ex) {
					SLAUtils.getInstance()
							.logSLAError(
									SLALogConstant.QT_MODULE,
									SLALogConstant.QT_DIRECT_LOA_DETAIL_BACKINGBEAN,
									this.getManagedBean().getScQt().getQtNo(),
									SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY,
									getUserIdSessionFromLoginId(), ex);
				}

				getManagedBean().getScLoa().getScLoaReports().add(lampiranA3);

				awardReqDTO.setScLoa(getManagedBean().getScLoa());

				AwardSupplierResDTO resDTO = getQtAwardSO().saveLampiranA3Report(awardReqDTO);
				List<ScLoaReport> scLoaReports = resDTO.getScLoa().getScLoaReports();

				ScLoa scLoa = getManagedBean().getScLoa();
				scLoa.setScLoaReports(scLoaReports);

				getManagedBean().setScLoa(scLoa);
			}
		} else {
			if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
				for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
					if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
						report.setDeleted(true);

						oldLampiranA3List.add(report);

						recordCount++;
					}
				}
			}

			int loaItemsLampiran = 0;

			for (ScLoiLoaItem loiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
				if (loiLoaItem.isComplete()) {
					loaItemsLampiran++;
					break;
				}
			}

			if (recordCount > 0 && loaItemsLampiran == 0) {
				getManagedBean().getScLoa().getScLoaReports().removeAll(oldLampiranA3List);

				ScLoa scLoa = new ScLoa();
				scLoa.setScLoaReports(oldLampiranA3List);

				awardReqDTO.setScLoa(scLoa);

				getQtAwardSO().deleteLampiranA3Report(awardReqDTO);
			}
		}
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * generate LampiranA3Pdf From Velocity
	 * @param attachment
	 * @param scLoa
	 * @return 
	 */
	private void generateLampiranA3PdfFromVelocity(ScAttachment attachment, ScLoa scLoa) throws Exception {
		ITemplateSpecSO templateSpecSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		List<Long> itemIds = new ArrayList<>();

		populateAttachmentDetails(attachment);

		VelocityContext context = new VelocityContext();
		List<ScLoiLoaItem> scLoiLoaItemsList = null;
		String defaultValue = QTConstants.LAMPIRAN_A3_TEXT_TB;
		FacesScopes facesScopes = new FacesScopes();
		IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();

		userReqDTO.setLoginId(facesScopes.getUserScreenName());

		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();

		if (ReportConst.ENG_LANGUAGE_CODE.equals(pmUser.getPreferredLanguage())) {
			defaultValue = QTConstants.LAMPIRAN_A3_TEXT_TB;
		}

		if (!scLoa.getScLoiLoaItems().isEmpty()) {
			scLoiLoaItemsList = new ArrayList<>();

			for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
				DateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_FORMAT);
				String date = df.format(scLoiLoaItem.getChangedDate());
				StringBuilder finalMsg = new StringBuilder();

				if (this.getManagedBean().getScQt().isZonal()) {
					List<ScLoiLoaItem> zonalItemList = new ArrayList<>();

					for (ScLoiLoaItem loacalloiLoaItem : this.getManagedBean().getScLoa().getScLoiLoaItems()) {
						if (scLoiLoaItem.getLoiLoaItemId() == loacalloiLoaItem.getLoiLoaItemId()
								|| scLoiLoaItem.getLoiLoaItemId().equals(loacalloiLoaItem.getLoiLoaItemId())
								|| scLoiLoaItem.getScRequestItem().getRequestNoteItemId() == loacalloiLoaItem
										.getScRequestItem().getRequestNoteItemId()
								|| scLoiLoaItem.getScRequestItem().getRequestNoteItemId()
										.equals(loacalloiLoaItem.getScRequestItem().getRequestNoteItemId())) {
							zonalItemList.add(loacalloiLoaItem);
						}
					}

					if (zonalItemList.size() > 1) {
						checkItemSorting(zonalItemList);

						ScLoiLoaItem latestChangedItem = zonalItemList.get(zonalItemList.size() - 1);

						if (latestChangedItem.getLoaSpecJustification() != null
								&& StringUtils.isNotBlank(latestChangedItem.getLoaSpecJustification())
								&& StringUtils.isNotEmpty(latestChangedItem.getLoaSpecJustification())) {
							scLoiLoaItem.setLoaSpecJustification(latestChangedItem.getLoaSpecJustification());
							scLoiLoaItem.setLatestChangedDate(latestChangedItem.getChangedDate());
							scLoiLoaItem.setComplete(latestChangedItem.isComplete());
							date = df.format(scLoiLoaItem.getLatestChangedDate());
						}
					} else {
						scLoiLoaItem.setLatestChangedDate(scLoiLoaItem.getChangedDate());

						date = df.format(scLoiLoaItem.getLatestChangedDate());
					}
				}

				if (scLoiLoaItem.getScRequestItem() != null && scLoiLoaItem.getScRequestItem().getItemName() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getScRequestItem().getItemName())
						&& StringUtils.isNotEmpty(scLoiLoaItem.getScRequestItem().getItemName())) {
					String itemName = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
							scLoiLoaItem.getScRequestItem().getItemName());
					scLoiLoaItem.setItemNameForVelocity(VmUtil.getInstance().addLineBreaks(itemName, 60));
				}
				if (scLoiLoaItem.getLoaSpecJustification() != null
						&& StringUtils.isNotBlank(scLoiLoaItem.getLoaSpecJustification())
						&& StringUtils.isNotEmpty(scLoiLoaItem.getLoaSpecJustification())) {
					finalMsg.append(QTConstants.LAMPIRAN_A3_JUSTIFICATION + scLoiLoaItem.getLoaSpecJustification()
							+ " " + date);
					String finalMessage = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(finalMsg.toString());
					scLoiLoaItem.setLoaSpecJustificationForVelocity(VmUtil.getInstance()
							.addLineBreaks(finalMessage, 60));
				}

				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(scLoiLoaItem);
				specReqDto.setSpecSearchForApp(false);

				TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);
				List<Long> replacedQuestionIds = new ArrayList<>();

				if (this.getManagedBean().getScQt().isZonal()) {// 29007&31423-start
					List<TplSpecAnswer> zoneSpecAnswerList = new ArrayList<>();
					boolean newOfferedSpecification = false;

					for (TplSpecAnswer specAnswer : templateSpecResDTO.getSpecAnswerList()) {
						if (TemplateSpecConst.QUESTION_TYPE_ITEM.equalsIgnoreCase(specAnswer.getTplSpecQuestion()
								.getQuestionType())) {
							boolean isReplaceSpChar = replacedQuestionIds.isEmpty()
									|| !replacedQuestionIds.contains(specAnswer.getTplSpecQuestion()
											.getSpecQuestionId());
							boolean isItemNotNull = specAnswer.getTplSpecQuestion().getClItem() != null
									&& StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClItem())
									&& StringUtils.isNotEmpty(specAnswer.getTplSpecQuestion().getClItem());

							if (specAnswer.getTplSpecQuestion() != null && isItemNotNull && isReplaceSpChar) {
								String itemName = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getTplSpecQuestion().getClItem());
								specAnswer.getTplSpecQuestion().setClItem(
										VmUtil.getInstance().addLineBreaks(itemName, 30));
								replacedQuestionIds.add(specAnswer.getTplSpecQuestion().getSpecQuestionId());
							}

							if (scLoiLoaItem.getTplSpecZoneDtl().getSpecZoneId()
									.equals(specAnswer.getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId())
									|| scLoiLoaItem.getTplSpecZoneDtl().getSpecZoneId() == specAnswer
											.getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId()) {

								if (null != scLoiLoaItem.getTplSpecTierQty()) {

									if (scLoiLoaItem.getTplSpecTierQty().getSpecTierQtyId()
											.equals(specAnswer.getTplSpecTierQty().getSpecTierQtyId())
											|| scLoiLoaItem.getTplSpecTierQty().getSpecTierQtyId() == specAnswer
													.getTplSpecTierQty().getSpecTierQtyId()) {

										zoneSpecAnswerList.add(specAnswer);
									}
								} else {
									zoneSpecAnswerList.add(specAnswer);
								}
							}
						} else {
							if (!scLoiLoaItem.isComplete()) {
								specAnswer.setNewSupplierOffer(null);
							}

							if (specAnswer.getNewSupplierOffer() != null
									&& StringUtils.isNotBlank(specAnswer.getNewSupplierOffer())
									&& StringUtils.isNotEmpty(specAnswer.getNewSupplierOffer())) {
								newOfferedSpecification = true;
								String newSupplierOffered = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getNewSupplierOffer());
								specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(newSupplierOffered,
										25));
							} else if (specAnswer.getTplSpecQuestion().getClSpecDetail() != null
									&& StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClSpecDetail())) {
								String spec = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getTplSpecQuestion().getClSpecDetail());
								specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(spec, 25));
							}

							if (specAnswer.getAnswerRemark() != null
									&& StringUtils.isNotBlank(specAnswer.getAnswerRemark())
									&& StringUtils.isNotEmpty(specAnswer.getAnswerRemark())) {
								String ansRemark = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getAnswerRemark());
								specAnswer.setAnswerRemark(VmUtil.getInstance().addLineBreaks(ansRemark, 25));
							}

							zoneSpecAnswerList.add(specAnswer);
						}
					}

					if (!newOfferedSpecification) {
						scLoiLoaItem.setLoaSpecJustificationForVelocity(null);
					}

					scLoiLoaItem.setTplSpecAnswerList(zoneSpecAnswerList);// 29007&31423
				} else {// 29007&31423_End
					List<TplSpecAnswer> zoneSpecAnswerList = new ArrayList<>();// 29007&31423-Start
					boolean newOfferedSpecification = false;

					for (TplSpecAnswer specAnswer : templateSpecResDTO.getSpecAnswerList()) {
						if (TemplateSpecConst.QUESTION_TYPE_ITEM.equalsIgnoreCase(specAnswer.getTplSpecQuestion()
								.getQuestionType())) {

							boolean isReplaceSpChar = replacedQuestionIds.isEmpty()
									|| !replacedQuestionIds.contains(specAnswer.getTplSpecQuestion()
											.getSpecQuestionId());
							boolean isItemNotNull = specAnswer.getTplSpecQuestion().getClItem() != null
									&& StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClItem())
									&& StringUtils.isNotEmpty(specAnswer.getTplSpecQuestion().getClItem());

							if (specAnswer.getTplSpecQuestion() != null && isItemNotNull && isReplaceSpChar) {
								String itemName = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getTplSpecQuestion().getClItem());
								specAnswer.getTplSpecQuestion().setClItem(
										VmUtil.getInstance().addLineBreaks(itemName, 30));
								replacedQuestionIds.add(specAnswer.getTplSpecQuestion().getSpecQuestionId());
							}

							if (null != scLoiLoaItem.getTplSpecTierQty()) {

								if (scLoiLoaItem.getTplSpecTierQty().getSpecTierQtyId()
										.equals(specAnswer.getTplSpecTierQty().getSpecTierQtyId())
										|| scLoiLoaItem.getTplSpecTierQty().getSpecTierQtyId() == specAnswer
												.getTplSpecTierQty().getSpecTierQtyId()) {

									zoneSpecAnswerList.add(specAnswer);
								}
							} else {
								zoneSpecAnswerList.add(specAnswer);
							}
						} else {
							if (!scLoiLoaItem.isComplete()) {
								specAnswer.setNewSupplierOffer(null);
							}

							if (specAnswer.getNewSupplierOffer() != null
									&& StringUtils.isNotBlank(specAnswer.getNewSupplierOffer())
									&& StringUtils.isNotEmpty(specAnswer.getNewSupplierOffer())) {
								newOfferedSpecification = true;

								String newSupplierOffered = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getNewSupplierOffer());
								specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(newSupplierOffered,
										25));
							} else if (specAnswer.getTplSpecQuestion().getClSpecDetail() != null
									&& StringUtils.isNotBlank(specAnswer.getTplSpecQuestion().getClSpecDetail())) {
								String spec = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getTplSpecQuestion().getClSpecDetail());
								specAnswer.setNewSupplierOffer(VmUtil.getInstance().addLineBreaks(spec, 25));
							}

							if (specAnswer.getAnswerRemark() != null
									&& StringUtils.isNotBlank(specAnswer.getAnswerRemark())
									&& StringUtils.isNotEmpty(specAnswer.getAnswerRemark())) {
								String ansRemark = VmUtil.getInstance().replaceAllSpecialCharactersToHTML(
										specAnswer.getAnswerRemark());
								specAnswer.setAnswerRemark(VmUtil.getInstance().addLineBreaks(ansRemark, 25));
							}

							zoneSpecAnswerList.add(specAnswer);
						}
					}// 29007&31423-End

					if (!newOfferedSpecification) {
						scLoiLoaItem.setLoaSpecJustificationForVelocity(null);
					}

					scLoiLoaItem.setTplSpecAnswerList(zoneSpecAnswerList);// 29007&31423
				}// 29007&31423_End

				if (itemIds.isEmpty() || !itemIds.contains(scLoiLoaItem.getScRequestItem().getRequestNoteItemId())) {
					scLoiLoaItemsList.add(scLoiLoaItem);
					itemIds.add(scLoiLoaItem.getScRequestItem().getRequestNoteItemId());
				}
			}
		}

		sortLoiLoaItemsBasedOnQuestionOrder(scLoiLoaItemsList);

		context.put(QTConstants.LAMPIRAN_A3_TEXT_SERVICE_SCOPE, defaultValue);
		context.put(QTConstants.LAMPIRAN_A3_TEXT_ITEMS_LIST, scLoiLoaItemsList);
		context.put(QTConstants.LAMPIRAN_A3_TEXT_IS_ZONAL, scLoa.getScLoiLoa().isZonal());
		context.put(QTConstants.LAMPIRAN_A3_TEXT_BEAN, this);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(getActualFilePath(attachment));
			DocumentGenerator.getInstance().generatePdfFromVelocity(SourcingConstants.LOA_LAMPIRAN_A3, context, fos);
		} catch (FileNotFoundException e) {
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_DIRECT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScQt().getQtNo(),
					SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY, getUserIdSessionFromLoginId(), e);
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END

		} catch (Exception ex) {
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] START
			SLAUtils.getInstance().logSLAError(SLALogConstant.QT_MODULE,
					SLALogConstant.QT_DIRECT_LOA_DETAIL_BACKINGBEAN, this.getManagedBean().getScQt().getQtNo(),
					SLALogConstant.QT_GENERATE_LAMPIRANA3PDF_FROM_VELOCITY, getUserIdSessionFromLoginId(), ex);
			// Sonarqube QT Vulnerability Minor Vinod #53 [15/8/2023] END
		}
	}
	// #29007 -end-
	
	/* #29007 Start Vinod */
	private void sortLoiLoaItemsBasedOnQuestionOrder(List<ScLoiLoaItem> scLoiLoaItemsList) {
		List<ScLoiLoaItem> productItemsList = new ArrayList<>();
		List<ScLoiLoaItem> serviceItemsList = new ArrayList<>();

		for (ScLoiLoaItem item : scLoiLoaItemsList) {
			String itemType = getParameterDesc(item.getScRequestItem().getItemType());

			if (itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUK_STRING)
					|| itemType.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT_STRING)) {
				productItemsList.add(item);
			} else {
				serviceItemsList.add(item);
			}
		}

		Collections.sort(productItemsList, new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
				return Long.compare(s1.getScRequestItem().getTplSpecQuestion().getQuestionOrder(), s2
						.getScRequestItem().getTplSpecQuestion().getQuestionOrder());
			}
		});

		Collections.sort(serviceItemsList, new Comparator<ScLoiLoaItem>() {
			@Override
			public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
				return Long.compare(s1.getScRequestItem().getTplSpecQuestion().getQuestionOrder(), s2
						.getScRequestItem().getTplSpecQuestion().getQuestionOrder());
			}
		});

		scLoiLoaItemsList.clear();
		scLoiLoaItemsList.addAll(productItemsList);// Add all product list after sorting
		scLoiLoaItemsList.addAll(serviceItemsList);// Add all service list after sorting
	}
	/* #29007 End Vinod */

	/* #29007 Start Vinod */
	/**
	 * This method will be called when user click on verify button and do the
	 * validations for LampiranA3.
	 * 
	 * @param bpmAction
	 * 
	 * @return
	 */
	public void verifyConfirmationForLampiranA3(String bpmAction) {
		ITemplateSpecSO templateSpecSO = (ITemplateSpecSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.COMMON_COMPONENT, ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		int recordCount = 0;
		int itemCount = 0;
		StringBuilder itemsList = new StringBuilder();
		int zoneitemCount = 0;
		ScLoiLoaItem zoneloiLoaItem = null;
		boolean isZonal = this.getManagedBean().getScLoa().getScLoiLoa().isZonal();
		List<Long> itemIds = new ArrayList<>();
		HashMap<Long, Long> itemZoneMap = new HashMap<>();

		if (this.getManagedBean().getScLoa().getScLoiLoa().getDocType().equals(ParameterConst.DOCTYPE_DIRECT_NEGO)) {
			for (ScLoiLoaItem loiLoaItem : sortLoiLoaItems(this.getManagedBean().getScLoa().getScLoiLoaItems(), isZonal)) {
				specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
				specReqDto.setScQt(this.getManagedBean().getScQt());
				specReqDto.setScLoiLoaItem(loiLoaItem);
				specReqDto.setSpecSearchForApp(true);
				
				TemplateSpecResDTO templateSpecResDTO = templateSpecSO.searchSupplierSpecification(specReqDto);

				int newSupplierOffer = checkSupplierOffered(templateSpecResDTO);

				boolean addItem = !loiLoaItem.isComplete()
						&& (newSupplierOffer > 0 || StringUtils.isNotBlank(loiLoaItem.getLoaSpecJustification()));
				if (addItem
						&& !isZonal
						&& (CollectionUtils.isEmpty(itemIds) || !itemIds.contains(loiLoaItem.getScRequestItem()
								.getRequestNoteItemId()))) {
					itemIds.add(loiLoaItem.getScRequestItem().getRequestNoteItemId());
					itemCount++;
					itemsList.append(itemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
							.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
				} else if (addItem
						&& isZonal
						&& (itemZoneMap.isEmpty() || checkItemAndZoneAdded(itemZoneMap, loiLoaItem.getScRequestItem()
								.getRequestNoteItemId(), loiLoaItem.getTplSpecZoneDtl().getSpecZoneId()))) {
					itemZoneMap.put(loiLoaItem.getScRequestItem().getRequestNoteItemId(), loiLoaItem
							.getTplSpecZoneDtl().getSpecZoneId());

					Long zoneIdAdded = null != zoneloiLoaItem ? new Long(zoneloiLoaItem.getTplSpecZoneDtl()
							.getSpecZoneId()) : null;
					Long zoneIdNew = null != loiLoaItem ? new Long(loiLoaItem.getTplSpecZoneDtl().getSpecZoneId())
							: null;
					boolean addZoneName = null == zoneloiLoaItem || zoneIdAdded.compareTo(zoneIdNew) != 0;

					if (addZoneName) {
						zoneloiLoaItem = loiLoaItem;
						itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE)
								.append(loiLoaItem.getTplSpecZoneDtl().getZoneName())
								.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
						zoneitemCount = 0;
					}

					zoneitemCount++;
					itemsList.append(zoneitemCount).append(". ").append(loiLoaItem.getScRequestItem().getItemName())
							.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
					itemCount++;
				}
			}
		}

		if (itemCount > 0) {
			itemsList.append(QTConstants.NEW_OFFERED_SPECIFICATION_ERROR_NEXTLINE);
			throw new EpException(MessageConst.ERROR_776000, new String[] { itemsList.toString() });
		}

		if (CollectionUtils.isNotEmpty(getManagedBean().getScLoa().getScLoaReports())) {
			for (ScLoaReport report : getManagedBean().getScLoa().getScLoaReports()) {
				if (StringUtils.isNotBlank(report.getTitle()) && report.getTitle().contains(lampiranA3SubStr)) {
					recordCount++;
				}
			}
		}

		if (recordCount > 0
				&& this.getManagedBean().getScLoa().getScLoiLoa().getDocType()
						.equals(ParameterConst.DOCTYPE_DIRECT_NEGO)) {
			this.getManagedBean().setLoaBpmVerify(bpmAction);
			RequestContext.getCurrentInstance().execute("verifyConfirmPopUp.show()");
		} else {
			return;
		}
	}
	// #29007 -end-
	
	// #29007 -start-
	private int checkSupplierOffered(TemplateSpecResDTO templateSpecResDTO) {
		int newSupplierOffer = 0;
		for (TplSpecAnswer tplSpecAnswer : templateSpecResDTO.getSpecAnswerList()) {
			if (StringUtils.isNotBlank(tplSpecAnswer.getNewSupplierOffer())) {
				newSupplierOffer++;
			}
		}
		return newSupplierOffer;
	}
	// #29007 -end-
	
	// #29007 -start-
	private List<ScLoiLoaItem> sortLoiLoaItems(List<ScLoiLoaItem> loiLoaItems, boolean isZonal) {

		if (isZonal) {
			Collections.sort(loiLoaItems, new Comparator<ScLoiLoaItem>() {
				@Override
				public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
					return Long.compare(s1.getTplSpecZoneDtl().getSpecZoneId(), s2.getTplSpecZoneDtl().getSpecZoneId());
				}
			});
		} else {
			Collections.sort(loiLoaItems, new Comparator<ScLoiLoaItem>() {
				@Override
				public int compare(ScLoiLoaItem s1, ScLoiLoaItem s2) {
					return Long.compare(s1.getLoiLoaItemId(), s2.getLoiLoaItemId());
				}
			});
		}

		return loiLoaItems;
	}
	// #29007 -end-
	
	// #29007 -start-
	private boolean checkItemAndZoneAdded(HashMap<Long, Long> itemZoneMap, Long itemId, Long specZoneId) {
		for (Map.Entry<Long, Long> itemZone : itemZoneMap.entrySet()) {
			Long eItemId = itemZone.getKey();
			Long eZoneId = itemZone.getValue();
			boolean alreadyAdded = (eItemId.equals(itemId) || eItemId == itemId)
					&& (eZoneId == specZoneId || eZoneId.equals(specZoneId));

			if (alreadyAdded) {
				return false;
			}
		}

		return true;
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be clled when user click on no for verification popup
	 * and close the popup.
	 * 
	 * @param 
	 * 
	 * @return
	 */
	public void resetLoaBpmVerify() {
		this.getManagedBean().setLoaBpmVerify(null);
		RequestContext.getCurrentInstance().execute("verifyConfirmPopUp.hide()");
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());// 29007&31423
	}
	// #29007 -end-

	//#32119 - start	
	public boolean isCTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_CT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())
				|| QTConstants.PORTLET_NAME_CT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	//#32119 - end

	/* #29007 Start Vinod */
	/**
	 * This method will be clled when user click on add button in new offered
	 * spec screen to edit new offered specification.
	 * 
	 * @param spec
	 * 
	 * @return
	 */
	public void addNewOfferedSpec(TplSpecAnswer spec) {
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();
		
		for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
			if (tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)) {
				tplSpecAnswer.setRenderNewSpec(true);
			}
		}
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be clled when user click on refresh button in new offered
	 * spec screen to clear and hide new offered specification.
	 * 
	 * @param spec
	 * 
	 * @return
	 */
	public void refreshNewOfferedSpec(TplSpecAnswer spec) {
		String specDet = spec.getTplSpecQuestion().getClSpecDetail();
		
		for (TplSpecAnswer tplSpecAnswer : this.getManagedBean().getTplSpecAnswerList()) {
			if (tplSpecAnswer.getTplSpecQuestion().getClSpecDetail().equals(specDet)) {
				tplSpecAnswer.setNewSupplierOffer(null);
				tplSpecAnswer.setRenderNewSpec(false);
			}
		}
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be clled when user click on resetall button this method
	 * will show the conformation.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void resetAllNewOfferedSpecification() {
		RequestContext.getCurrentInstance().execute("resetAllConfirmPopUp.show()");
	}
	// #29007 -end-

	/* #29007 Start Vinod */
	/**
	 * This method will be clled when user click on yes this method will proceed
	 * to reset.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public void resetAllNewOfferedSpecificationSubmit() {
		TemplateSpecReqDTO specReqDto = new TemplateSpecReqDTO();
		
		if (this.getManagedBean().getSpecKeyword() != null) {
			this.getManagedBean().setSpecKeyword(null);
			specReqDto.setSupplier(this.getManagedBean().getScLoa().getScLoiLoa().getSmSupplier());
			specReqDto.setScQt(this.getManagedBean().getScQt());
			specReqDto.setScLoiLoaItem(this.getManagedBean().getScLoiLoaItem());
			specReqDto.setSpecSearchForApp(true);

			TemplateSpecResDTO templateSpecResDTO = getTemplateSpecSO().searchSupplierSpecification(specReqDto);

			this.getManagedBean().setTplSpecAnswerList(templateSpecResDTO.getSpecAnswerList());
		}

		for (TplSpecAnswer answer : this.getManagedBean().getTplSpecAnswerList()) {
			answer.setNewSupplierOffer(null);
		}
		
		this.getManagedBean().setNewSupplierJustification(null);
		
		saveSupplierSpecification(false);
	}
	/* #29007 End Vinod */
	
	/* #29007 Start Vinod */
	/**
	 * replace all special characters and next line characters to html codes.
	 * @param str
	 * @return updatedStr
	 */
	public String replaceAllSpecialCharactersAndNewline(String str) {
		String updatedStr = str.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL, TemplateSpecConst.LESS_THEN_HTML_ENTITY);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.LESS_THEN_SYMBOL, TemplateSpecConst.LESS_THEN_HTML_ENTITY);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER,
				TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		updatedStr = updatedStr.replaceAll(TemplateSpecConst.NEW_LINE_CHARACTER_R,
				TemplateSpecConst.HTML_NEW_LINE_CHARACTER);
		return updatedStr;
	}
	/*#29007 End Vinod*/

	/*
	 * 29007 Start sorting LoiItems
	 */
	private void checkItemSorting(List<ScLoiLoaItem> loiLoaItem) {
		Collections.sort(loiLoaItem, new Comparator<ScLoiLoaItem>() {
			public int compare(ScLoiLoaItem loiItem1, ScLoiLoaItem loiItem2) {

				return new CompareToBuilder().append(loiItem1.getChangedDate(), loiItem2.getChangedDate())
						.toComparison();
			}
		});
	}
	// 29007 End
	
	/* #29007 Start Vinod */
	/**
	 * this method to display N/A or T/B based on language for Supplier remark.
	 * 
	 * @param supplierRemarks
	 * @return updated string
	 */
	public String supplierRemarks(String supplierRemarks) {
		String remarks = QTConstants.LAMPIRAN_A3_TEXT_TB;
		if (null != supplierRemarks && StringUtils.isNotBlank(supplierRemarks)
				&& StringUtils.isNotEmpty(supplierRemarks)) {
			return this.replaceAllSpecialCharactersAndNewline(supplierRemarks);
		}

		if (ReportConst.ENG_LANGUAGE_CODE.equals(new FacesScopes().getUserLocale().getLanguage())) {
			remarks = QTConstants.LAMPIRAN_A3_TEXT_NA;
		}

		return remarks;
	}
	// 29007-End
	
	// #29007 Start vinod
	private void checkAndInsertItemSpecOffer() {
		ScLoa scLoa = this.getManagedBean().getScLoa();

		if (checkWIPVersionAndInsertItemSpecOffer(scLoa)) {
			Long workFlowStatus = scLoa.getPmStatus().getStatusId();
			boolean isPendingAcknowledgement = QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
					.equals(workFlowStatus);
			if (isPendingAcknowledgement) {
				DirectAwardReqDTO awardReqDTOnew = new DirectAwardReqDTO();
				awardReqDTOnew.setScLoa(scLoa);
				DirectAwardResDTO awardResDTOnew = getQtDirectAwardSO()
						.saveScloiloaitemforDirectLOA(awardReqDTOnew);
				if (awardResDTOnew.isSuccessful()) {
					scLoa.setScLoiLoaItems(awardResDTOnew.getLoa()
							.getScLoiLoaItems());
				}
			}
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setLoaId(scLoa.getLoaId());

			getQtAwardSO().insertItemSpecOffer(reqDTO);
		}
	}

	// #29007 End vinod
	
	// #26770 - Start fix sonarlint
	/**
	 * To Validate Report Attachment
	 */
	private void validateReportAttachment() {
		if (null != this.getManagedBean().getScLoa()
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getScLoa().getScLoaReports())) {
			validateScLoaReportAttachment(this.getManagedBean().getScLoa().getScLoaReports());
		}
	}

	// #26770 - end fix sonarlint

	// #26770 - Start fix sonarlint
	/**
	 * @param scLoaReport
	 */
	private void validateScLoaReportAttachment(List<ScLoaReport> scLoaReport) {
		if (scLoaReport == null || scLoaReport.isEmpty()) {
			return;
		}
		// 64 records max (PDS) as per March 2025
		for (ScLoaReport report : scLoaReport) {
			if (report == null) {
				continue;
			}

			if ("Surat Setuju Terima (Termasuk Lampiran A)".equals(report.getTitle())
					&& report.getScLoaReportAttachment() == null) {
				throw new EpException(MessageConst.ERROR_775159);
			}
		}
	}

	// #26770 - End fix sonarlint
	// #26770 - start fix sonarlint
	/**
	 * 
	 * Change SuratSetujuterima(TermasukLampiranA) capital T to small t.
	 * 
	 * @param text
	 * @param index
	 * @return
	 */
	public String setValueLowercase(String text, int index) {
		if (text == null) {
			return null;
		}

		String trimmedText = text.trim();

		if ("SuratSetujuterima(TermasukLampiranA)".equalsIgnoreCase(trimmedText)) {
			if (index < 0 || index >= trimmedText.length()) {
				return text; // Return unchanged if index is invalid
			}

			char[] arr = trimmedText.toCharArray();
			arr[index] = Character.toUpperCase(arr[index]);
			return new String(arr);

		}
		return text;
	}

	// 26770 - end fix sonarlint
	// 26770 - start fix sonarlint
	/**
	 * @param scLoa
	 * @return
	 */
	public String constructSSTHeaderTitle(ScLoa scLoa) {
		if (scLoa == null || scLoa.getScLoiLoa() == null || scLoa.getProcurementMode() == null) {
			return null; // Ensure method doesn't break
		}

		String docType = scLoa.getScLoiLoa().getDocType();
		String procurementCode = scLoa.getProcurementMode().getParameterCode();

		if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) || ParameterConst.DOCTYPE_DIRECT_NEGO.equals(docType)) {
			return ParameterConst.PROCUREMENT_MODE_TENDER.equals(procurementCode) ? SSTConstant.HEADER_TENDER
					: SSTConstant.HEADER_SEBUT_HARGA;
		}

		return SSTConstant.EMPTY_STRING; // Ensure headerTitle remains unchanged
											// for invalid cases
	}

	// 26770 - end fix sonarlint
	// 26770 start fix sonarlint
	/**
	 * @param context
	 * @param supplier
	 */
	private void setSSMExpiredDateForAgreementLetter(VelocityContext context, SmSupplier supplier) {
		if (null != supplier.getSmAppl().getSmCompanyBasic()) {
			if (null != supplier.getSmAppl().getSmCompanyBasic().getRobExpiryDate()) {
				context.put(
						SSTConstant.SSM_EXPIRED,
						new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT).format(supplier.getSmAppl()
								.getSmCompanyBasic().getRobExpiryDate()));
			} else {
				context.put(SSTConstant.SSM_EXPIRED, SSTConstant.UNDERLINE); // 26770
			}

		}
	}

	// 26770 end fix sonarlint
	// 26770 start fix sonarlint
	/**
	 * Will display 4 Category Code per line. If more than 4, the category code
	 * will break into new line.
	 * 
	 * @param smSupplier
	 * @return
	 */
	private StringBuilder constructCategoryCodeForSST(SmSupplier smSupplier) {
		int counter = 0;
		StringBuilder categoryCode = new StringBuilder();
		// max record 7593 (PDS) as per March 2025
		for (SmSupplierCategory category : smSupplier.getSmAppl().getSmSupplierCategories()) {
			String l1Code = category.getPmCategoryL1().getCategoryL1Code();
			String l2Code = category.getPmCategoryL2().getCategoryL2Code();
			String l3Code = category.getPmCategoryL3().getCategoryL3Code();
			String catcode = l1Code + l2Code + l3Code + SSTConstant.SEPARATOR;

			counter++;

			if (counter % 4 == 0 && counter < smSupplier.getSmAppl().getSmSupplierCategories().size()) {
				catcode = catcode + SSTConstant.LINE_BREAK;
			}

			categoryCode.append(catcode);
		}

		return categoryCode;
	}
	// 26770 - end fix sonarlint
}
