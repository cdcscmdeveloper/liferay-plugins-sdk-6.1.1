package my.ep.web.backingbean.sc.loa;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.clause.IClauseSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTDirectAwardSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
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
import my.ep.dto.pm.common.clause.ClauseReqDTO;
import my.ep.dto.pm.common.clause.ClauseResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.DirectAwardReqDTO;
import my.ep.dto.sc.quationtender.DirectAwardResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmClause;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
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
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScPbAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.ApprovalAction;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.amendment.JadualHargaBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.loa.DirectLOAManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.SourcingLocationHelper;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

// TODO: Auto-generated Javadoc
/**
 * The Class directLOABackingBean.
 * 
 * @author Sri
 * 
 * @since 15/06/2015
 */
@ManagedBean(name = "directLOABackingBean")
@RequestScoped
public class DirectLOABackingBean extends
		SourcingBaseBackingBean<DirectLOAManagedBean> {

	/** The log. */
	public static Logger log = EpLogger.getLogger(DirectLOABackingBean.class);

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
	public void saveAction() {
		debugLogger("saveAction");
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		try {

			ScLoa scLoa = this.getManagedBean().getScLoa();
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();
			parseLOADetails(scLoa, BpmOutcomeConst.BPM_SAVE, reqDTO);

			// validateLOADetails(scLoa, BpmOutcomeConst.BPM_DRAFT);
			validateSupplierDetails(scLoa);
			validateDeductionPaymentDtls(scLoa);
			validateLOAReportAttachments(scLoa);
						
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setScLoa(scLoa);
			reqDTO.setScQtDoc(this.getManagedBean().getProdscQtDoc());
			reqDTO.setServiceQtDoc(this.getManagedBean().getServicescQtDoc());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);
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
				if (!QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
						.equals(scLoa.getPmStatus().getStatusId())) {
					retriveLoaDetails(scLoa.getLoaId());

					contractTypeChanged();
					constructStartNEndMonth();
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
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();
		scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
		downloadFile(scLoaReportAttachment);
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
			scLoa.setScLoaWorkFlowStatuses(null);
			scLoa.setIssueDate(DateUtils.getCurrentDateWithoutTime());

			validateSupplierDetails(scLoa);
			validateLOAReportAttachments(scLoa);
			validateLOADetails(scLoa, outcome);

			validateContractAmount(scLoa.getLoaAmt());

			reqDTO.setScLoa(scLoa);
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
		StreamedContent loaStreamContent = getManagedBean()
				.getLoaStreamContent();

		if (loaStreamContent == null) {
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
				ScLoaReportAttachment agreementLetterAttachment = generateDocumentAttachment(agreementLetterReport);
				agreementLetterReport
						.setScLoaReportAttachment(agreementLetterAttachment);

				loaStreamContent = getAttachmentStreamContent(
						agreementLetterAttachment,
						CommonConstants.FILE_CONTENT_TYPE_PDF);

				getManagedBean().setLoaStreamContent(loaStreamContent);
			}
		} else {
			loaStreamContent.getStream().reset();
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

			this.getManagedBean().setSelectedBankId(
					getBankByAccountNo(scLoa.getBankAccNo()));
			
			// overall ct amount or loa amt
			BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
			getManagedBean().setOverallContractAmt(overallCtAmout);
			scLoa.setOverallContractAmt(overallCtAmout);

			// Setting BI Report
			List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();
			if (scLoaReports != null && !scLoaReports.isEmpty()) {
				for (ScLoaReport report : scLoaReports) {
					// Check whether Branch Info attachment is existed or not
					PmDocument document = report.getPmDocument();
					if (null != document
							&& SourcingConstants.DOC_DEF_KEY_BRANCH_INFORMATION
									.equalsIgnoreCase(document
											.getDocDefinitionKey())) {
						getManagedBean().setBranchInfoReport(report);
						break;
					}
				}
			}
			// Supplier Witness
			ScLoaWitness loaSupplierWitness = null;
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
			getManagedBean().setLoaSupplierWitness(loaSupplierWitness);

			getManagedBean().setScQt(scLoa.getScQt());

			scLoa.setAckBy(new FacesScopes().getUserSessionBean());

			scLoa = calcBondAmount(scLoa, true);
			Date issueDate = scLoa.getIssueDate();
			Date bondRespDueDate = DateUtils.addDays(issueDate, 30);
			scLoa.setBondResDueDate(bondRespDueDate);

			if (scLoa.getBondAmt() != null) {
				getManagedBean().setBondAmountWord(
						NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			}

			generateDeclarationFromVelocity(scLoa, scLoa.getScQt());

			getManagedBean().setSelectedSupplierBranchName(
					scLoa.getSupplierBranch());
			if (scLoa.getScQt().isZonal()) {
				List<TplSpecZoneDtl> zoneDetails = resDTO.getSpecZoneDetails();
				constructTreeNodeForzones(zoneDetails);
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

		if (scQtSchedule.getStartYear() == null
				|| scQtSchedule.getStartYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getStartMonth() == null
				|| scQtSchedule.getStartMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndYear() == null
				|| scQtSchedule.getEndYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (scQtSchedule.getEndMonth() == null
				|| scQtSchedule.getEndMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.month",
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
					.before(contractExpDate) || endDate.equals(contractEffDate)))) {
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
		IParameterSO parameterSO = null;
		IClauseSO clauseSO = null;
		try {
			Long parameterId = scQtDeduction.getDeductionType()
					.getParameterId();

			if (null != parameterId) {
				parameterSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
				clauseSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IClauseSO.class);

				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterId(parameterId);
				ParameterResDTO resDTO = parameterSO
						.retrieveParameterById(reqDTO);
				scQtDeduction.setDeductionType(resDTO.getPmParameter());
				validateDeductionPayment(scQtDeduction);
				getClausesTitle(scQtDeduction);
				ScQtDeductionClause deductionClause = null;
				List<ScQtDeductionClause> deductionClauseList = new ArrayList<ScQtDeductionClause>();
				if (!ParameterConst.DEDUCTION_TYPE_OTHERS
						.equalsIgnoreCase(scQtDeduction.getDeductionType()
								.getParameterCode())) {
					ClauseReqDTO clauseReqDTO = new ClauseReqDTO();
					clauseReqDTO.setDeductionType(scQtDeduction
							.getDeductionType().getParameterCode());
					ClauseResDTO clauseResDTO = clauseSO
							.retrieveClausesByDeductionType(clauseReqDTO);
					if (clauseResDTO.getResultBean() != null) {
						List<PmClause> pmClauseList = clauseResDTO
								.getResultBean().getResultList();
						for (PmClause pmClause : pmClauseList) {
							deductionClause = new ScQtDeductionClause();
							deductionClause.setPmClause(pmClause);
							deductionClause.setScQtDeduction(scQtDeduction);
							deductionClause.setClauseDesc(pmClause
									.getClauseDesc());
							deductionClauseList.add(deductionClause);
						}
						scQtDeduction
								.setScQtDeductionClauses(deductionClauseList);
					} else {
						scQtDeduction.setScQtDeductionClauses(null);
					}
				} else {
					scQtDeduction.setScQtDeductionClauses(null);
				}
				getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			} else {
				scQtDeduction.setDeductionType(new PmParameter());
				getManagedBean().setSelectedScQtDeduction(scQtDeduction);
			}
		} finally {
			parameterSO = null;
		}
	}

	/**
	 * Validate deduction payment.
	 * 
	 * @param scQtDeduction
	 *            the sc qt deduction
	 */
	private void validateDeductionPayment(ScQtDeduction scQtDeduction) {
		List<ScQtDeduction> scQtDeductions = getManagedBean().getScLoa()
				.getScQtDeductions();
		if (scQtDeductions != null && scQtDeductions.size() > 1) {
			for (ScQtDeduction qtDeduction : scQtDeductions) {
				if (!qtDeduction.equals(scQtDeduction)) {
					if (qtDeduction.getDeductionType().getParameterId() == scQtDeduction
							.getDeductionType().getParameterId()) {
						throw new EpException(
								MessageConst.ERROR_700027,
								new String[] { ResourceBundleUtils.getMessage(
										getParameterDesc(scQtDeduction
												.getDeductionType()),
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}
			}

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
		String selectedSupplierBranchName = this.getManagedBean()
				.getSelectedSupplierBranchName();
		Long supplierId = scLoa.getScLoiLoa().getSmSupplier().getSupplierId();

		if (selectedSupplierBranchName == null
				|| "HQ".equals(selectedSupplierBranchName)) {
			SmPersonnel supplierHQPersonnel = this.getManagedBean()
					.getSupplierHQPersonnel();
			SmCompanyBasic smCompanyBasic = supplierHQPersonnel.getSmAppl()
					.getSmCompanyBasic();

			for (SmCompanyAddress smCompanyAddress : smCompanyBasic
					.getSmCompanyAddresses()) {
				if (AddressType.CONTACT_ADDRESS.value().equals(
						smCompanyAddress.getAddressType())) {
					log.info("found hq address");

					scLoa.setSupplierBranch("HQ");
					scLoa.setSupplierAddress(smCompanyAddress.getSmAddress());
					scLoa.setSupplierContact(supplierHQPersonnel.getName());
					scLoa.setSupplierPhoneNo(smCompanyBasic.getPhoneNo());
					scLoa.setSupplierFaxNo(smCompanyBasic.getFaxNo());
					scLoa.setSupplierMobNo(supplierHQPersonnel.getMobileNo());
					scLoa.setSupplierEmail(supplierHQPersonnel.getEmail());
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
				if (supplierBranch.getBranchName().equals(
						selectedSupplierBranchName)) {
					log.info("found branch address");

					scLoa.setSupplierBranch(supplierBranch.getBranchName());
					scLoa.setSupplierAddress(supplierBranch.getSmAddress());
					scLoa.setSupplierContact(supplierBranch.getSmPersonnel()
							.getName());
					scLoa.setSupplierPhoneNo(supplierBranch.getPhoneNo());
					scLoa.setSupplierFaxNo(supplierBranch.getFaxNo());
					scLoa.setSupplierMobNo(supplierBranch.getSmPersonnel()
							.getMobileNo());
					scLoa.setSupplierEmail(supplierBranch.getSmPersonnel()
							.getEmail());
					// Get gst info
					SupplierGstResDTO resDTO = getGSTInfo(supplierId,
							supplierBranch.getSupplierBranchId());
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

		Long selectedBankId = this.getManagedBean().getSelectedBankId();
		ScLoa scLoa = this.getManagedBean().getScLoa();

		if (selectedBankId != null) {
			List<SmSupplierBank> bankList = this.getManagedBean().getBankList();

			for (SmSupplierBank supplierBank : bankList) {
				if (supplierBank.getSupplierBankId().equals(selectedBankId)) {
					log.info("found supplier bank");

					scLoa.setBankAccNo(supplierBank.getAccountNo());
					scLoa.setBankName(supplierBank.getPmFinancialOrg()
							.getFinOrgName());
					scLoa.setBankBranch(supplierBank.getBankBranch());
					scLoa.setBankAddress(supplierBank.getSmAddress());

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

			if (!getManagedBean().isAgreeWithTermsConditions()) {
				throw new EpException(MessageConst.ERROR_750002);
			}
		}

		// validation for fulfillment schedule and progressivepayment
		if (BpmOutcomeConst.BPM_SUBMIT.equals(outcome)
				|| BpmOutcomeConst.BPM_SAVE.equals(outcome)) {
			// check whether loa from s1 or s2.
			// For s2

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

			// validate deduction payamment for Others
			validateDeductionPaymentDtls(scLoa);
		}

	}

	/**
	 * Validate deduction payment dtls.
	 * 
	 * @param scLoa
	 *            the sc loa
	 */
	private void validateDeductionPaymentDtls(ScLoa scLoa) {
		List<ScQtDeduction> deductions = scLoa.getScQtDeductions();
		if (CollectionUtils.isNotEmpty(deductions)) {
			for (ScQtDeduction deduction : deductions) {
				if (ParameterConst.DEDUCTION_TYPE_OTHERS.equals(deduction
						.getDeductionType().getParameterCode())) {
					if (CollectionUtils.isEmpty(deduction
							.getScQtDeductionClauses())) {
						throw new EpException(MessageConst.ERROR_760324);
					}
				}
				if (null == deduction.getDeductionType().getParameterId()) {
					throw new EpException(MessageConst.ERROR_740035);
				}
			}
		}
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
				scLoa.getScLoiLoa().getMofNo())) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.mof.no", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}
		if (StringUtils.isBlank(
				scLoa.getScLoiLoa().getSupplierName())) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.supplier.name", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}
		if (scLoa.getSupplierAddress() == null) {
			throw new EpException(MessageConst.ERROR_760400,
					new String[] { ResourceBundleUtils.getMessage("lbl.sc.address", 
				 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale)});
		}		
	}
	
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
					this.getManagedBean().setSelectedTabIndex(6);
					throw new EpException(MessageConst.ERROR_760402);
				}
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
		debugLogger("frequencyScheduleValidation");
		FacesScopes facesScopes = new FacesScopes();
		Calendar startDate = null;
		Calendar endDate = null;

		if (progressivePayment.getStartYear() == null
				|| progressivePayment.getStartYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getStartMonth() == null
				|| progressivePayment.getStartMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.start.month",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getEndYear() == null
				|| progressivePayment.getEndYear().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.year",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							facesScopes.getUserLocale()), "" });
		}
		if (progressivePayment.getEndMonth() == null
				|| progressivePayment.getEndMonth().isEmpty()) {
			throw new EpException(MessageConst.ERROR_721110, new String[] {
					ResourceBundleUtils.getMessage("lbl.sc.end.month",
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

		scLoa.setIssueDate(scLoa.getContractEffDate());

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

			scLoaApproval.setApproverAction(action);
			scLoaApproval.setApproverActionDate(new Date());

			scLoa.setScLoaApproval(scLoaApproval);
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)
				|| QTStatusConst.LOA_PENDING_VERIFICATION.equals(statusId)) {

			this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_FINAL);
			int specQuestions = 0;
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getProdscQtDoc().getTplSpecMaster().getTplSpecQuestions())) {
				specQuestions = this.getManagedBean().getProdscQtDoc()
						.getTplSpecMaster().getTplSpecQuestions().size();
			}
			if (CollectionUtils.isNotEmpty(this.getManagedBean()
					.getServicescQtDoc().getTplSpecMaster()
					.getTplSpecQuestions())) {
				specQuestions += this.getManagedBean().getServicescQtDoc()
						.getTplSpecMaster().getTplSpecQuestions().size();
			}
			if (specQuestions < 1) {
				throw new EpException(MessageConst.ERROR_760354);
			}
		} else {
			this.getManagedBean().getProdscQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
			this.getManagedBean().getServicescQtDoc().getTplSpecMaster()
					.setTemplateStatus(TemplateSpecConst.TEMPLATE_STATUS_DRAFT);
		}

		if (QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT.equals(statusId)) {
			scLoa.setAckDate(new Date());
			scLoa.setAckBy(new FacesScopes().getUserSessionBean());
			scLoa.setBondResDueDate(new Date());

		} else {

			saveProgressivePaymntList();
			constructScheduleDetails();
			if (CollectionUtils.isNotEmpty(scLoa.getScQtSchedules())) {
				for (ScQtSchedule scQtSchedule : scLoa.getScQtSchedules()) {
					freqScheduleValidation(scQtSchedule);
				}
			}
		}

		if (!(BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome))) {
			scLoa.setScLoaRemarks(finalizeRemarks(scLoa.getScLoaRemarks()));
		}

		ScQt scQt = this.getManagedBean().getScQt();
		ScQtSupplierCriteria supplierCriteria = this.getManagedBean().getScQt()
				.getScQtSupplierCriteria();
		if (scQt.isZonal()) {

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

		} else {
			ScQtZoneCriteria zoneCriteria = this.getManagedBean()
					.getToBedeletedSupplierZone();

			if (null != zoneCriteria && !zoneCriteria.isBrandNew()) {
				zoneCriteria.setDeleted(true);
				awardReqDTO.setToBeDeltedZoneCriteria(zoneCriteria);
			}
			supplierCriteria.setQtZoneCriterias(null);
		}
		if(null==scQt.getScheduleType()){
			scQt.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		}
		scLoa.setScQt(this.getManagedBean().getScQt());

	}

	/**
	 * Contract type changed. shows Agency based on contract type changes.
	 */
	public void contractTypeChanged() {
		debugLogger("contractTypeChanged");

		ScLoa scLoa = getManagedBean().getScLoa();

		IParameterSO parameterSO = null;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			if(scLoa.getContractType() !=null){
				reqDTO.setParameterId(scLoa.getContractType().getParameterId());
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				scLoa.setContractType(resDTO.getPmParameter());
			}

		} finally {
			parameterSO = null;
		}

		String contractType = scLoa.getContractType().getParameterCode();

		if (ParameterConst.CONTRACT_TYPE_MINISTRY.equals(contractType)) {
			// ministry condtion field for LOV 
			getMinistryConditionField(scLoa);

			//if(null == scLoa.getContractAgency()){
			scLoa.setContractAgency(scLoa.getScLoiLoa().getPmOrgProfile());
			//}

		} else if (ParameterConst.CONTRACT_TYPE_CENTRAL.equals(contractType)) {

			Long userId = new FacesScopes().getUserSessionBean().getUserId();
			
			if (userId != null) {
				IUserSO userSO = null;
				try {
					UserReqDTO reqDTO = new UserReqDTO();
					reqDTO.setUserId(userId);
					userSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					UserResDTO resDTO = userSO
							.retrieveUserOrgListByUserId(reqDTO);

					if (resDTO.getUserOrgList() != null
							&& resDTO.getUserOrgList().size() > 0) {
						for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
							if (pmUserOrg.getPmOrgProfile() != null) {
								Boolean isBPK = pmUserOrg.getPmOrgProfile().getBpk();
								if (BooleanUtils.isTrue(isBPK)) {
									scLoa.getScLoiLoa().setPmOrgProfile(
											pmUserOrg.getPmOrgProfile());
									scLoa.setContractAgency(scLoa
											.getScLoiLoa().getPmOrgProfile());
									break;
								}
							}
						}
					}
				} finally {
					userSO = null;
				}
			}			
		}

		contractAgencyChanged();
		// checkAgreementMandatory();
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
					// TODO - filter user based on central contract.
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
	 * Contract eff date changed. calculate Completion Expiry Date based on
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
		PmUser contractSigner = getManagedBean().getScLoa().getContractSigner();
		if (contractSigner != null) {
			if (getManagedBean().getContractSignerList() != null
					&& getManagedBean().getContractSignerList().size() > 0) {
				List<PmUser> contractSignerList = getManagedBean()
						.getContractSignerList();
				for (PmUser user : contractSignerList) {

					if (user.getUserId().equals(contractSigner.getUserId())) {
						getManagedBean().getScLoa().setLoaSignerDesig(
								user.getDesignation());
						break;
					} else {
						getManagedBean().getScLoa().setLoaSignerDesig("");
					}
				}
			}
		}
	}

	/**
	 * Adds the schedule details in Fulfillment Schedule.
	 */
	public void addScheduleDetails() {

		IParameterSO parameterSo = null;
		try {

			removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);

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
			for (int j = 0; j < this.getManagedBean().getTotalFrequency(); j++) {

				ScQtSchedule schedule = new ScQtSchedule();
				schedule.setScQt(this.getManagedBean().getScQt());
				schedule.setScLoa(this.getManagedBean().getScLoa());

				schedule.setScheduleType(docDefTypeResDTO.getPmParameter());

				scheduleList.add(schedule);
			}

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
			try {
				selectItems.add(new SelectItem(null, ResourceBundleUtils
						.getMessage("lbl.txt.select",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								face.getUserLocale())));
				selectItems.add(new SelectItem(1, getMonth(1)));
				selectItems.add(new SelectItem(2, getMonth(2)));
				selectItems.add(new SelectItem(3, getMonth(3)));
				selectItems.add(new SelectItem(4, getMonth(4)));
				selectItems.add(new SelectItem(5, getMonth(5)));
				selectItems.add(new SelectItem(6, getMonth(6)));
				selectItems.add(new SelectItem(7, getMonth(7)));
				selectItems.add(new SelectItem(8, getMonth(8)));
				selectItems.add(new SelectItem(9, getMonth(9)));
				selectItems.add(new SelectItem(10, getMonth(10)));
				selectItems.add(new SelectItem(11, getMonth(11)));
				selectItems.add(new SelectItem(12, getMonth(12)));

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
	private String getMonth(int monthId) {
		Calendar fullMonth = Calendar.getInstance();
		fullMonth.set(00, monthId, 0000);

		Format formatter = new SimpleDateFormat("MMMM");
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
			Date currentDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			String localDate = sdf.format(currentDate);
			String day = localDate.substring(0, 2);
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

		removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
		List<ScQtProgressivePayment> payments = this.getManagedBean()
				.getScLoa().getScQtProgPayments();

		for (int j = 0; j < this.getManagedBean().getTotalProgressivePayment(); j++) {

			ScQtProgressivePayment payment = new ScQtProgressivePayment();

			setUserDetails(payment);

			payment.setScQt(this.getManagedBean().getScQt());
			payment.setScLoa(this.getManagedBean().getScLoa());
			payments.add(payment);

		}
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

				Date d1 = new Date();

				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String localDate = format.format(d1);
				String date = localDate.substring(0, 2);
				String fromdate = date + "/"
						+ getMonth(schedule.getStartMonth()) + "/"
						+ schedule.getStartYear();
				String toDate = date + "/" + getMonth(schedule.getEndMonth())
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

		retriveLoaDetails(null);
		ScQt scQt = getManagedBean().getScQt();
		PmOrgProfile pmOrgProfile = getPTJOrgProfileByLoginUser();
		scQt.setPmOrgProfile(pmOrgProfile);
		scQt.setOwnerOrgProfile(pmOrgProfile);
		this.getManagedBean()
				.getScLoa()
				.getScLoiLoa()
				.setPmOrgProfile(
						pmOrgProfile);
		populateMinistryBasedOnLoginUser();
		getApprovalUsers();
		prepareSignerInfo();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
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

		SmSupplier smSupplier = mofAccount.getSmSupplier();
		getManagedBean().getScLoa().getScLoiLoa()
				.setSmSupplier(smSupplier);
		getManagedBean().getScLoa().getScLoiLoa()
				.setMofNo(mofAccount.getMofNo());
		getManagedBean().getScLoa().getScLoiLoa()
				.setSupplierName(smSupplier.getCompanyName());
		getManagedBean().getScLoa().getScLoiLoa()
				.setEpNo(smSupplier.getEpNo());
		this.getManagedBean().getProdTemplateSpecBean()
				.setSupplier(smSupplier);

		this.getManagedBean().getSpecTemplateSpecBean()
				.setSupplier(smSupplier);
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());

		DirectAwardResDTO resDTO = getQtDirectAwardSO()
				.retrieveSmPersonnelDtlsBySupplierId(reqDTO);

		this.getManagedBean().getScLoa().getScLoiLoa()
				.setSmSupplier(resDTO.getSmSupplier());
		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
			getManagedBean().setSelectedSupplierBranchName("HQ");
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			handleBranchDetailsChanged();
		}
		
		ScQt scQt = getManagedBean().getScQt();
		if(scQt != null) {
			ScLoiLoa scLoiLoa = getSupplierLoiLoaByDocId(smSupplier.getSupplierId(), scQt.getQtId());
			if(scLoiLoa != null) {
				getManagedBean().getScLoa().setScLoiLoa(scLoiLoa);
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

		getManagedBean().getScLoa().getScLoiLoa().setSmSupplier(suSmSupplier);
		getManagedBean().getScLoa().getScLoiLoa()
				.setSupplierName(suSmSupplier.getCompanyName());

		/*
		 * getManagedBean().getScLoa().getScLoiLoa()
		 * .setMofNo(suSmSupplier.getSmMofAccount().getMofNo());
		 */

		getManagedBean().getScLoa().getScLoiLoa()
				.setEpNo(suSmSupplier.getEpNo());

		this.getManagedBean().getProdTemplateSpecBean()
				.setSupplier(suSmSupplier);
		this.getManagedBean().getSpecTemplateSpecBean()
				.setSupplier(suSmSupplier);
		DirectAwardReqDTO reqDTO = new DirectAwardReqDTO();
		reqDTO.setSupplierId(suSmSupplier.getSupplierId());

		DirectAwardResDTO resDTO = getQtDirectAwardSO()
				.retrieveSmPersonnelDtlsBySupplierId(reqDTO);

		this.getManagedBean().getScLoa().getScLoiLoa()
				.setSmSupplier(resDTO.getSmSupplier());
		// smPersonnel
		if (resDTO.getSupplierHQPersonnel() != null) {
			getManagedBean().setSupplierHQPersonnel(
					resDTO.getSupplierHQPersonnel());
			getManagedBean().setSelectedSupplierBranchName("HQ");
			getManagedBean().setBranchsList(resDTO.getSmSupplierBranchs());
			handleBranchDetailsChanged();
		}
		// mof nor
		getManagedBean()
				.getScLoa()
				.getScLoiLoa()
				.setMofNo(
						resDTO.getSmSupplier().getSmMofAccounts().get(0)
								.getMofNo());
		
		ScQt scQt = getManagedBean().getScQt();
		if(scQt != null && scQt.getQtNo() != null &&
				!scQt.getQtNo().startsWith(ParameterConst.DOCTYPE_QUOTATION)) {
			ScLoiLoa scLoiLoa = getSupplierLoiLoaByDocId(suSmSupplier.getSupplierId(), scQt.getQtId());
			if(scLoiLoa != null) {
				getManagedBean().getScLoa().setScLoiLoa(scLoiLoa);
			}
		}

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
		// TODO ministry name cleared after selected QT.
		populateMinistryBasedOnLoginUser();
		SmSupplier smSupplier = getManagedBean().getScLoa().getScLoiLoa()
				.getSmSupplier();
		if(smSupplier != null) {
			ScLoiLoa scLoiLoa = getSupplierLoiLoaByDocId(smSupplier.getSupplierId(), scQt.getQtId());
			if(scLoiLoa != null) {
				getManagedBean().getScLoa().setScLoiLoa(scLoiLoa);
			}
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
		sb.append("%");
		String qtNoPrefix = sb.toString();
		
		// Filter By - pmStatus
		StringBuilder conditionFieldSb = new StringBuilder();
		conditionFieldSb.append("qtNo^");
		conditionFieldSb.append(qtNoPrefix);
		conditionFieldSb.append("^");
		conditionFieldSb.append(SearchOperator.OPERATOR_NOT_LIKE.value());
		
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

		selectedTreeNode.setSelected(true);
	}

	/**
	 * Un select zone tree node.Event handle uncheck Nonpanel treeNodes.
	 * 
	 * @param event
	 *            (TreeNode treeNode).
	 */
	public void unSelectZoneTreeNode(NodeUnselectEvent event) {
		TreeNode selectedTreeNode = event.getTreeNode();

		selectedTreeNode.setSelected(false);
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
							locationLevelCode, TplSpecZoneLoc.class));
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
											TplSpecZoneLoc.class));
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

		// LocationSingleton.getInstance().deleteTreeNodes(rootTrees);
		deleteZonesTreeNodes(rootTrees);
		TplSpecZoneDtl zoneDtl = null;
		// zone details
		if (!rootTrees.isEmpty()) {
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

			if (null != this.getManagedBean().getScQt()
					.getScQtSupplierCriteria()
					&& null != this.getManagedBean().getScQt()
							.getScQtSupplierCriteria().getQtZoneCriterias()) {
				this.getManagedBean().getScQt().getScQtSupplierCriteria()
						.getQtZoneCriterias().setModified(true);
			}
			TplSpecZoneDtl zone = new TplSpecZoneDtl();

			if (null == getManagedBean().getZoneName()
					|| StringUtils.isBlank(getManagedBean().getZoneName())) {
				throw new EpException("Zone Name is Mandatory.");
			}

			zone.setZoneName(getManagedBean().getZoneName());

			List<PmState> states = SourcingLocationHelper
					.getStateFromLocationDTO(allNodes);

			List<PmDivision> divisions = SourcingLocationHelper
					.getDivisionFromLocationDTO(allNodes);

			List<PmDistrict> districts = SourcingLocationHelper
					.getDistrictFromLocationDTO(allNodes);

			List<PmCity> cities = SourcingLocationHelper
					.getCityFromLocationDTO(allNodes);
			String editZoneName = getManagedBean().getTempZoneName();
			if (getManagedBean().getIndex() >= 0) {
				updateZoneBean(editZoneName);
			}
			zone.setTplSpecZoneLocs(allNodes);
			zonesBean.setTplSpecZoneDtl(zone);
			// zonesBean.setTplSpecZoneDtl(zone);
			// validate zone name
			checkZoneExistsOrNot(zone);

			if (ParameterConst.LOCATION_LEVEL_STATE
					.equalsIgnoreCase(locationLevel)) {
				// check whether state exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (ParameterConst.LOCATION_LEVEL_DIVISION
					.equalsIgnoreCase(locationLevel)) {
				// check whether division exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			if (ParameterConst.LOCATION_LEVEL_DISTRICT
					.equalsIgnoreCase(locationLevel)) {
				// check whether district exists or not
				if (!getManagedBean().getZoneBeans().isEmpty()) {
					checkDuplicateStateOrDivOrDist(zone);
				}
				// zonesBean.setTplSpecZoneLocs(allNodes);
			}

			beans.add(zonesBean);

			if (getManagedBean().getIndex() == -1) {

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));

			}

			else if (getManagedBean().getIndex() >= 0) {

				getManagedBean().getZoneTrees().remove(
						getManagedBean().getIndex());
				getManagedBean().getZoneTrees().add(
						getManagedBean().getIndex(),
						LocationSingleton.getInstance().buildLocationTree(zone,
								states, districts, divisions, cities,
								TplSpecZoneLoc.class));
			}
			getManagedBean().getZoneBeans().add(zonesBean);

			ScQtZoneCriteria qtZoneCriteria = this.getManagedBean().getScQt()
					.getScQtSupplierCriteria().getQtZoneCriterias();

			removeExistingSpecZone(qtZoneCriteria.getTplSpecZoneDtls(),
					editZoneName);
			zone.setTplSpecZone(qtZoneCriteria);
			qtZoneCriteria.getTplSpecZoneDtls().add(zone);
			TplSpecMaster producSpecMaster = this.getManagedBean()
					.getProdscQtDoc().getTplSpecMaster();
			producSpecMaster.setTplSpecZone(qtZoneCriteria);

			TplSpecMaster serviceSpecMaster = this.getManagedBean()
					.getServicescQtDoc().getTplSpecMaster();
			serviceSpecMaster.setTplSpecZone(qtZoneCriteria);

			updateTplSpecMasterNDetails(true);

		} else {
			throw new EpException("Please select At least one Zone Location");
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

		if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
				.getManagedBean().getScQt().getFulfilmentType()
				.getParameterCode())) {
			this.getManagedBean().getScQt()
					.setScheduleType(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
			this.getManagedBean().setTotalFrequency(0);
			this.getManagedBean().setTotalProgressivePayment(0);

		}

		this.getManagedBean().getScQt().setScQtSchedules(null);
		this.getManagedBean().getScQt().setScQtProgressivePayments(null);
		removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);
		removefullfillmentDetails(ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE);
		getLoaReports();
		checkAgreementMandatory();

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
		if (null != parameterId)
			this.getManagedBean()
					.setLocalLevelCode(
							ParameterHelper.getInstance()
									.getPmParameterById(parameterId)
									.getParameterCode());

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

	public void getApprovalUsers() {
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
		removefullfillmentDetails(toBeRemoveType);

	}

	private void removefullfillmentDetails(String scheduleType) {

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE.equalsIgnoreCase(scheduleType)) {
			List<ScQtSchedule> scheduleList = this.getManagedBean().getScLoa()
					.getScQtSchedules();

			if (CollectionUtils.isNotEmpty(scheduleList)) {
				List<ScQtSchedule> toBeRemoveList = new ArrayList<ScQtSchedule>();

				for (ScQtSchedule qtSchedule : scheduleList) {
					if (null != qtSchedule.getQtScheduleId()) {
						qtSchedule.setDeleted(true);
						toBeRemoveList.add(qtSchedule);
					}
				}
				this.getManagedBean().setToBeRemoveSchedules(toBeRemoveList);
				this.getManagedBean().getScQt().setScQtSchedules(null);
			}
			this.getManagedBean().getScLoa()
					.setScQtSchedules(new ArrayList<ScQtSchedule>());
		} else if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scheduleType)) {
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getScLoa().getScQtProgPayments();

			if (CollectionUtils.isNotEmpty(payments)) {
				List<ScQtProgressivePayment> toBeRemoveList = new ArrayList<ScQtProgressivePayment>();

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
			this.getManagedBean()
					.getScLoa()
					.setScQtProgPayments(
							new ArrayList<ScQtProgressivePayment>());
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
		List<ScLoaReport> scLoaReports = scLoa.getScLoaReports();

		if (scLoaReports == null) {
			scLoaReports = new ArrayList<ScLoaReport>();

			scLoa.setScLoaReports(scLoaReports);
		}

		ScLoaReport scLoaReport = new ScLoaReport();
		scLoaReport.setScLoa(scLoa);
		scLoaReport.setScLoaReportAttachment(new ScLoaReportAttachment());

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

	/*
	 * * Download all reports as pdf or xlsx files in Letter of Acceptance.
	 * 
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void downloadAllReports() throws IOException {
		debugLogger("downloadAllReports");

		List<ScLoaReport> scLoaReports = getManagedBean().getScLoa()
				.getScLoaReports();
		List<ScLoaReportAttachment> scAttachments = new ArrayList<ScLoaReportAttachment>();

		for (ScLoaReport scLoaReport : scLoaReports) {
			ScLoaReportAttachment scLoaReportAttachment = scLoaReport
					.getScLoaReportAttachment();
			scLoaReportAttachment = generateDocumentAttachment(scLoaReport);
			scAttachments.add(scLoaReportAttachment);
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
			ScLoaReport scLoaReport) {
		String definitionType = scLoaReport.getDefinationType()
				.getParameterCode();
		ScLoaReportAttachment scLoaReportAttachment = scLoaReport
				.getScLoaReportAttachment();

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equals(definitionType)) {
			try {
				String docDefinitionKey = scLoaReport.getPmDocument()
						.getDocDefinitionKey();
				ScLoa scLoa = getManagedBean().getScLoa();
				Long statusId = scLoa.getPmStatus().getStatusId().longValue();
				StringBuilder filename = new StringBuilder(); // replace white
																// space due to
																// firefox
				filename.append(scLoaReport.getTitle().replaceAll("\\s", ""));
				if (scLoaReportAttachment == null) {
					scLoaReportAttachment = new ScLoaReportAttachment();
					scLoaReportAttachment.setScLoaReport(scLoaReport);
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());

					if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
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
					}

				} else if (scLoaReportAttachment != null
						&& QTStatusConst.LOA_PENDING_SUPPLIER_ACKNOWLEDGEMENT
								.equals(statusId)
						|| QTStatusConst.LOA_PENDING_VERIFICATION
								.equals(statusId)) { // if
					// pending supplier acknowledgement , always regenerate
					scLoaReportAttachment.setDocNo(scLoaReport.getScLoa()
							.getLoaNo());

					if (SourcingConstants.DOC_DEF_KEY_CONTRACT_SUMMARY
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
						saveScheduleList();
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
					}
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
	private void constructTreeNodeForzones(List<TplSpecZoneDtl> zonedtls) {

		List<ZonesBean> beans = getManagedBean().getZoneBeans();

		if (isNotNullOREmpty(zonedtls)) {

			for (TplSpecZoneDtl zone : zonedtls) {

				if (null != this.getManagedBean().getScQt()
						&& !this.getManagedBean().getScQt().isPanel()) {
					ZonesBean zonesBean = new ZonesBean();
					zonesBean.setTplSpecZoneDtl(zone);
					zonesBean.setTplSpecZoneLocs(zone.getTplSpecZoneLocs());
					beans.add(zonesBean);
				}

				getManagedBean().getZoneTrees().add(
						LocationSingleton.getInstance().constuctTreeNode(zone,
								zone.getTplSpecZoneLocs()));

			}
			getManagedBean().setZoneBeans(beans);
		}

	}

	public void preRenderDirectLoa() {
		FacesContext fc = FacesContext.getCurrentInstance();
		
		try {
			SCAwardSupplierDataType awardSupplierDataType = getBpmTaskDetails(SCAwardSupplierDataType.class, null);
			retriveLoaDetails(awardSupplierDataType.getAwardId());
	
			contractTypeChanged();
			constructStartNEndMonth();
			this.getManagedBean().setPayLoad(awardSupplierDataType);
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.SC_DIRECT_LOA_MAIN_PAGE);
		} catch (Exception e) {
			handleException(e);
			fc.getApplication().getNavigationHandler()
			  .handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
		}
	}

	private void retriveLoaDetails(Long loaId) {
		IQTDirectAwardSO directAwardSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDirectAwardSO.class);

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
		getManagedBean().setLoaAgencyWitness(scLoa.getScLoaWitnesses().get(0));
		this.getManagedBean().setScLoa(scLoa);
		if (scQt.isZonal()) {
			List<TplSpecZoneDtl> zoneDetails = awardResDTO.getSpecZoneDetails();
			constructTreeNodeForzones(zoneDetails);
		}
		this.getManagedBean().setContractSignerList(
				awardResDTO.getContractSignerList());
		this.getManagedBean().setApproverList(awardResDTO.getApproverList());
		// award details

		this.getManagedBean().setProdscQtDoc(awardResDTO.getProductDoc());
		setUserDetails(awardResDTO.getProductDoc());
		
		// overall ct amount or loa amt
		BigDecimal overallCtAmout = overallCtAmount(scLoa.getLoaAmt(), scLoa.getGstAmt());
		getManagedBean().setOverallContractAmt(overallCtAmout);
		scLoa.setOverallContractAmt(overallCtAmout);
		
		// Acknowledge Date
		Integer loaRespPeriod = getDaysByRuleType(RuleTypeConst.LOA_RESPONSE_PERIOD);
		scLoa.setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));

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

		this.getManagedBean().setServicescQtDoc(awardResDTO.getServiceDoc());
		setUserDetails(awardResDTO.getServiceDoc());
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
		if (null != scLoa.getScLoaApproval()) {
			this.getManagedBean().setApproverUser(
					scLoa.getScLoaApproval().getApprover());
		}
		scLoa.setScLoaRemarks(initializeRemarks(scLoa, null,
				scLoa.getScLoaRemarks(), ScLoaRemark.class));
		
		// bond calculation
		scLoa = calcBondAmount(scLoa, true);

		// bond amount in words
		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}

		// procurement type category
		if (scLoa.getProcurementTypeCatId() == null) {
			scLoa.setProcurementTypeCatId(new PmParameter());
		}

		// contract Signer info
		if (scLoa.getContractSigner() == null) {
			scLoa.setContractSigner(new PmUser());
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

	}

	private String getMonth(String month) {

		if (StringUtils.isEmpty(month)) {
			throw new EpException("Month should not be empty");
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
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			if (isDigiSigningRequired(RoleCode.PUBLICATION_APPROVER.value())) {
				RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			}
		} else if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
			RequestContext.getCurrentInstance().execute("spki_dlg.show();");

		} else {
			if (QTStatusConst.LOA_PENDING_SUBMISSION.equals(this
					.getManagedBean().getScLoa().getPmStatus().getStatusId())) {
				submitAction();

			} else {
				updateBPMTask(outcome, null);
			}
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
			Long statusId = scLoa.getPmStatus().getStatusId().longValue();

			parseLOADetails(scLoa, outcome, reqDTO);
			validateLOADetails(scLoa, outcome);
			validateContractAmount(scLoa.getLoaAmt());
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
			}

			// spki
			if (BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)) {
				RequestContext.getCurrentInstance().execute(
						"blockWidget.hide();");
				RequestContext.getCurrentInstance().execute("spki_dlg.hide();");
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
			} else {
				// gpki
				if (null != digiSign) {
					updatePmDigiSignData(digiSign,
							ParameterConst.DOCTYPE_DIRECT_NEGO,
							scLoa.getLoaId(), scLoa.getLoaNo());
				}
			}

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
			resDTO = getQtDirectAwardSO().updateBpmTask(reqDTO);
			reqDTO.setTobeDeleteProPayments(this.getManagedBean()
					.getToBeRemoveProgPayments());
			reqDTO.setToBeDeleteschedules(this.getManagedBean()
					.getToBeRemoveSchedules());
			if (resDTO.isSuccessful()) {
				scLoa = resDTO.getLoa();

				String messageCode = null;

				if (QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId)) {
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

	private PmOrgProfile ptjOrgProfileByLoginUser() {
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			// reqDTO.setParentOrgProfileId(parentOrgProfileId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			return resDTO.getOrgProfileList().get(0);
		} else {
			throw new EpException("Please login");

		}
	}

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
		String procurementMode = null;
		if (ParameterConst.DOCTYPE_PURCHASE_REQUEST.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			procurementMode = SourcingConstants.PROCUREMENT_MODE_SIMPLE_QUOTE;
		} else if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
				.getDocType())) {
			if (scQt != null) {
				if (ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt
						.getProcurementMode().getParameterCode())) {
					procurementMode = SourcingConstants.PROCUREMENT_MODE_QUOTATION;
				} else {
					procurementMode = SourcingConstants.PROCUREMENT_MODE_TENDER;
				}
			}
		}
		String title = procurementMode != null ? procurementMode + " "
				+ scLoiLoa.getTitle() : scLoiLoa.getTitle();
		context.put("title", title);

		String userName = "&#60;supplier user name&#62;";
		String icNo = "&#60;ic no&#62;";
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

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_CONTRACT_SUMMARY, context,
				new FileOutputStream(getActualFilePath(attachment)));
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

		context.put("supplierName", scLoiLoa.getSupplierName());
		String supplierAddress = "";
		if (scLoa.getSupplierAddress() != null) {
			supplierAddress = getFullAddress(scLoa.getSupplierAddress());
			supplierAddress = supplierAddress.replaceAll("\r\n", "<br />");
			supplierAddress = supplierAddress.replaceAll("&", "&#38;");
		}
		context.put("supplierAddress", supplierAddress);
		String supplierPhoneNo = scLoa.getSupplierPhoneNo();
		if (supplierPhoneNo == null) {
			supplierPhoneNo = "";
		}
		context.put("supplierPhoneNo", supplierPhoneNo);
		String supplierFaxNo = scLoa.getSupplierFaxNo();
		if (supplierFaxNo == null) {
			supplierFaxNo = "";
		}
		context.put("supplierFaxNo", supplierFaxNo);
		String supplierMobNo = scLoa.getSupplierMobNo();
		if (supplierMobNo == null) {
			supplierMobNo = "";
		}
		context.put("supplierMobNo", supplierMobNo);
		String supplierEmail = scLoa.getSupplierEmail();
		if (supplierEmail == null) {
			supplierEmail = "";
		}
		context.put("supplierEmail", supplierEmail);

		String procurementMode = "";
		ParameterReqDTO reqDTO = new ParameterReqDTO();
		reqDTO.setParameterType(ParameterTypeConst.SC_PROCUREMENT_MODE);
		reqDTO.setLanguageCode(CommonConstants.LANGUAGE_CODE_MALAY);
		reqDTO.setParameterCode(scQt.getProcurementMode().getParameterCode());
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

		context.put("title", scQt.getQtTitle().replaceAll("&", "&#38;"));

		context.put("hasLoi", false); // TODO
		context.put("bondReq", scLoa.isBondReq());

		String bondAmt = "";
		if (scLoa.getBondAmt() != null) {
			bondAmt = amountDF.format(scLoa.getBondAmt());
		}
		context.put("bondAmt", bondAmt);
		context.put("bondAmtText", bondAmt);

		String bondPercentage = "";
		if (scLoa.getBondPercentage() != null) {
			bondPercentage = percentageDF.format(scLoa.getBondPercentage());
		}
		context.put("bondPercentage", bondPercentage);
		context.put("bondExpDate", ""); // TODO

		/*
		 * String bondResDueDays = ""; if (scLoa.getBondResDueDate() != null) {
		 * bondResDueDays =
		 * DateUtils.getDaysDifference(DateUtils.getCurrentDate(),
		 * scLoa.getBondResDueDate()) + ""; }
		 */

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

		String loaSigner = "";
		String loaSignerDesig = "";
		if (null != scLoa.getContractSigner()) {
			loaSigner = scLoa.getContractSigner().getUserName();
			loaSignerDesig = scLoa.getLoaSignerDesig();

		}
		context.put("loaSigner", loaSigner);

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
		for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
			String partyType = scLoaWitness.getPartyType();

			if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(partyType)) {
				supplierWitness = scLoaWitness;

				break;
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
		context.put("bankName", bankName.replaceAll("&", "&#38;"));
		String bankBranch = scLoa.getBankBranch();
		if (bankBranch == null) {
			bankBranch = "";
		}
		context.put("bankBranch", bankBranch);
		String bankAddress = "";
		if (scLoa.getBankAddress() != null) {
			bankAddress = getFullAddress(scLoa.getBankAddress());
			bankAddress = bankAddress.replaceAll("\r\n", "<br />");
			bankAddress = bankAddress.replaceAll("&", "&#38;");
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
		setGstInfoForAgreementLetter(context, scLoa, amountDF, myLocale);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_AGREEMENT_LETTER, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * @param context
	 * @param scLoa
	 * @param amountDF
	 * @param myLocale
	 */
	private void setGstInfoForAgreementLetter(VelocityContext context,
			ScLoa scLoa, DecimalFormat amountDF, Locale myLocale) {

		String gstRegNo = scLoa.getGstRegNo() != null ? scLoa.getGstRegNo()
				: "";
		context.put("gstRegNo", gstRegNo);

		String gstEffectiveDate = scLoa.getGstEffDate() != null ? DateUtils
				.getLocalizeDate(scLoa.getGstEffDate(), myLocale) : "";
		context.put("gstEffectiveDate", gstEffectiveDate);

		String gstDeclaredDate = scLoa.getGstDeclaredDate() != null ? gstEffectiveDate = DateUtils
				.getLocalizeDate(scLoa.getGstDeclaredDate(), myLocale) : "";
		context.put("gstDeclaredDate", gstDeclaredDate);

		String offerPrice = "";
		String offerPriceWording = "";
		BigDecimal loaAmount = scLoa.getLoaAmt();
		if (loaAmount != null) {
			offerPrice = amountDF.format(loaAmount);
			offerPriceWording = WordUtils.capitalizeFully(NumberWordUtil
					.convert(loaAmount.toString()));
		}
		context.put("offerPrice", offerPrice);
		context.put("offerPriceWording", offerPriceWording);

		String gstAmount = "T/B";
		String gstAmountWording = "T/B";
		String overallctAmt = "";
		String overallctAmtWording = "";
		BigDecimal gstAmt = scLoa.getGstAmt();
		if (gstAmt != null) {
			gstAmount = amountDF.format(gstAmt);
			gstAmountWording = WordUtils.capitalizeFully(NumberWordUtil
					.convert(gstAmt.toString()));
			overallctAmt = amountDF.format(loaAmount.add(gstAmt));
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil
					.convert(loaAmount.add(gstAmt).toString()));
		} else {
			overallctAmt = amountDF.format(loaAmount);
			overallctAmtWording = WordUtils.capitalizeFully(NumberWordUtil
					.convert(loaAmount.toString()));
		}
		context.put("gstAmt", gstAmount);
		context.put("gstAmtWording", gstAmountWording);
		context.put("overallCtAmt", overallctAmt);
		context.put("overallCtAmtWording", overallctAmtWording);

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

		DocumentGenerator.getInstance().generatePdf(
				new FileOutputStream(getActualFilePath(attachment)),
				termsConditions);
	}

	/**
	 * Save schedule list.
	 */
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
	private void generateFrequencySchedulePdfFromVelocity(
			ScAttachment attachment, ScLoa scLoa, ScQt scQt) throws Exception {

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

		List<ScQtSchedule> scheduleList = scLoa.getScQtSchedules();
		List<ScQtSchedule> scheduleReportList = new ArrayList<ScQtSchedule>();
		ScQtSchedule reportSchedule = null;
		int totalFrequency = 0;
		if (scheduleList != null && scheduleList.size() > 0) {
			totalFrequency = scheduleList.size();
			for (ScQtSchedule schedule : scheduleList) {
				try {
					reportSchedule = new ScQtSchedule();
					parameterSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterId(schedule.getScheduleType()
							.getParameterId());
					ParameterResDTO resDTO = parameterSO
							.retrieveParameterById(reqDTO);
					reportSchedule.setScheduleType(resDTO.getPmParameter());
					reportSchedule.setStartMonth(monthFormat.format(schedule
							.getFromDate()));
					reportSchedule.setStartYear(yearFormat.format(schedule
							.getFromDate()));
					reportSchedule.setEndMonth(monthFormat.format(schedule
							.getToDate()));
					reportSchedule.setEndYear(yearFormat.format(schedule
							.getToDate()));
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

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOA_FREQUENCY_SCHEDULE, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Form jadual harga. To download jadual-harga.xlsx file
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	public void formJadualHarga(ScAttachment attachment, ScLoa scLoa)
			throws Exception {
		populateAttachmentDetails(attachment);
		TplSpecMaster productSpecMaster = getManagedBean()
				.getProdTemplateSpecBean().getSpecMaster();
		TplSpecMaster serviceSpecMaster = getManagedBean()
				.getSpecTemplateSpecBean().getSpecMaster();
		List<JadualHargaBean> productJadualHargaList = convertSpecToJadualHargaBean(productSpecMaster);
		List<JadualHargaBean> serviceJadualHargaList = convertSpecToJadualHargaBean(serviceSpecMaster);

		// check whether loa from s1 or s2.
		boolean isS1OrS2 = Boolean.TRUE;
		if (scLoa.getScLoiLoa() != null) {
			if (ParameterConst.DOCTYPE_QUOTATION.equals(scLoa.getScLoiLoa()
					.getDocType())) {
				isS1OrS2 = Boolean.FALSE;
			}
		}

		generateJadualHarga(attachment, productJadualHargaList,
				serviceJadualHargaList, isS1OrS2);

	}

	private List<JadualHargaBean> convertSpecToJadualHargaBean(
			TplSpecMaster specMaster) {
		List<JadualHargaBean> jadualHargaList = new ArrayList<JadualHargaBean>();
		JadualHargaBean jadualHarga = null;

		if (specMaster != null && specMaster.getTplSpecQuestions() != null) {
			List<TplSpecQuestion> specQuestions = specMaster
					.getTplSpecQuestions();

			for (TplSpecQuestion specQuestion : specQuestions) {
				if (specQuestion.getTplSpecAnswers() != null) {
					for (TplSpecAnswer specAnswer : specQuestion
							.getTplSpecAnswers()) {
						if (TemplateSpecConst.QUESTION_TYPE_ITEM
								.equals(specQuestion.getQuestionType())) {
							jadualHarga = mappingJadualHarga(specAnswer);

							jadualHargaList.add(jadualHarga);
						}
					}
				}
			}
		}

		return jadualHargaList;
	}

	/**
	 * Mapping jadual harga.
	 * 
	 * @param item
	 *            the item
	 * @return the jadual harga bean
	 */
	private JadualHargaBean mappingJadualHarga(TplSpecAnswer specAnswer) {
		JadualHargaBean jadualHarga = new JadualHargaBean();
		TplSpecQuestion specQuestion = specAnswer.getTplSpecQuestion();
		TplSpecZoneDtl specZoneDtl = specAnswer.getTplSpecSuppAnswer()
				.getTplSpecZoneDtl();
		BigDecimal qty = null;
		
		if (specAnswer.getTplSpecZoneQty() != null) {
			qty = specAnswer.getTplSpecZoneQty().getQty();
		} else {
			qty = specQuestion.getClQuantity();
		}

		jadualHarga.setZone(specZoneDtl != null ? specZoneDtl.getZoneName()
				: "");
		jadualHarga.setItem(specQuestion.getClItem());

		BigDecimal unitPrice = specAnswer.getRatePerUom().setScale(2,
				BigDecimal.ROUND_HALF_EVEN);
		jadualHarga.setUnitPrice(unitPrice.toString());
		jadualHarga.setUom(specQuestion.getClUom().getUomName());
		jadualHarga.setFreqUom(null == specQuestion.getClUomFrequency() ? ""
				: specQuestion.getClUomFrequency().toString());
		jadualHarga.setUomPerDay(null == specQuestion.getClUomPerDay() ? ""
				: specQuestion.getClUomPerDay().toString());
		jadualHarga.setUomPerMonth(null == specQuestion.getClUomPerMonth() ? ""
				: specQuestion.getClUomPerMonth().toString());
		jadualHarga
				.setUomPerYear(null == specQuestion.getClDurationMonth() ? ""
						: specQuestion.getClDurationMonth().toString());
		jadualHarga.setQuantity(null == qty ? "" : qty.toString());

		BigDecimal totalAmount = BigDecimal.valueOf(0d);
		BigDecimal subTotal = TemplateSpecHelper.getInstance()
				.calculateTotalAmount(specQuestion, unitPrice, qty);
		if (subTotal != null) {
			totalAmount = totalAmount.add(subTotal);
			totalAmount = totalAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		}
		jadualHarga.setTotalAmount(totalAmount.toString());
		return jadualHarga;

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
	private void generateJadualHarga(ScAttachment attachment,
			List<JadualHargaBean> productList,
			List<JadualHargaBean> serviceList, boolean isS1OrS2)
			throws Exception {
		try {
			/*
			 * FileInputStream inputStream = new FileInputStream(new File(
			 * FacesContext.getCurrentInstance().getExternalContext()
			 * .getRealPath("/WEB-INF/templates") +
			 * "/sc/loa/jadual-harga.xlsx"));
			 */

			final String templatePath = "templates"
					+ SourcingConstants.DOCPATH_SC_LOA_JADUAL_HARGA;
			InputStream inputStream = this.getClass().getClassLoader()
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
	}

	/**
	 * Form specification. To download specification.xlsx file
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scLoa
	 *            the sc loa
	 * @throws Exception
	 *             the exception
	 */
	public void formSpecification(ScAttachment attachment, ScLoa scLoa)
			throws Exception {
		populateAttachmentDetails(attachment);

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
		List<TplSpecAnswer> serviceAnswers = convertSpecQuestionToSpecAnswer(productSpecMaster);
		List<TplSpecAnswer> productAnswers = convertSpecQuestionToSpecAnswer(serviceSpecMaster);

		generateSpecification(attachment, productAnswers, serviceAnswers);
	}

	private List<TplSpecAnswer> convertSpecQuestionToSpecAnswer(
			TplSpecMaster specMaster) {
		List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();

		if (specMaster != null && specMaster.getTplSpecQuestions() != null) {
			for (TplSpecQuestion specQuestion : specMaster
					.getTplSpecQuestions()) {
				if (TemplateSpecConst.QUESTION_TYPE_ITEM.equals(specQuestion
						.getQuestionType())) {
					if (specQuestion.getTplSpecAnswers() != null) {
						specAnswers.addAll(specQuestion.getTplSpecAnswers());
					}
				} else {
					TplSpecAnswer specAnswer = new TplSpecAnswer();
					specAnswer.setTplSpecQuestion(specQuestion);

					specAnswers.add(specAnswer);
				}
			}
		}

		return specAnswers;
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
	private void generateSpecification(ScAttachment attachment,
			List<TplSpecAnswer> productList, List<TplSpecAnswer> serviceList)
			throws Exception {
		try {
			/*
			 * FileInputStream inputStream = new FileInputStream(new File(
			 * FacesContext.getCurrentInstance().getExternalContext()
			 * .getRealPath("/WEB-INF/templates") +
			 * "/sc/loa/specification.xlsx"));
			 */

			final String templatePath = "templates"
					+ SourcingConstants.DOCPATH_SC_LOA_SPECIFICATION;
			InputStream inputStream = this.getClass().getClassLoader()
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
					for (TplSpecAnswer answer : productList) {
						rowIndex++;
						cellIndex = 0;
						row = productSheet.createRow(rowIndex);
						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClItem() ? "" : answer
												.getTplSpecQuestion()
												.getClItem());

						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClSpecDetail() ? ""
												: answer.getTplSpecQuestion()
														.getClSpecDetail());

						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClUom() ? "" : answer
												.getTplSpecQuestion()
												.getClUom().getUomName());

						row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClPriceTypeCode() ? ""
										: getParameterDesc(answer
												.getTplSpecQuestion()
												.getClPriceTypeCode()));

						/*row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClQuantity() ? "" : answer
										.getTplSpecQuestion().getClQuantity()
										.toString());*/

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

					for (TplSpecAnswer answer : serviceList) {
						rowIndex++;
						cellIndex = 0;
						row = serviceSheet.createRow(rowIndex);
						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClItem() ? "" : answer
												.getTplSpecQuestion()
												.getClItem());

						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClSpecDetail() ? ""
												: answer.getTplSpecQuestion()
														.getClSpecDetail());

						row.createCell(cellIndex++)
								.setCellValue(
										null == answer.getTplSpecQuestion()
												.getClUom() ? "" : answer
												.getTplSpecQuestion()
												.getClUom().getUomName());

						row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClUomFrequency() ? "" : answer
										.getTplSpecQuestion()
										.getClUomFrequency().toString());

						row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClUomPerDay() ? "" : answer
										.getTplSpecQuestion().getClUomPerDay()
										.toString());

						row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClUomPerMonth() ? "" : answer
										.getTplSpecQuestion()
										.getClUomPerMonth().toString());

						/*row.createCell(cellIndex++).setCellValue(
								null == answer.getTplSpecQuestion()
										.getClQuantity() ? "" : answer
										.getTplSpecQuestion().getClQuantity()
										.toString());*/
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
	}

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
	private void generateNegotiationResultPdf(ScAttachment attachment)
			throws Exception {
		attachment.setFileName("Negotiation_Result.pdf");
		ScLoa scLoa = getManagedBean().getScLoa();
		populateAttachmentDetails(attachment);
		List<ScLoi> lois = new ArrayList<ScLoi>();
		if (getManagedBean().getSuppLoiLoaMap() != null
				&& getManagedBean().getSuppLoiLoaMap().size() > 0) {
			Map<Long, List<ScLoiLoa>> suppLoiLoaMap = getManagedBean()
					.getSuppLoiLoaMap();
			if (scLoa.getScLoiLoa() != null
					&& scLoa.getScLoiLoa().getSmSupplier() != null) {
				List<ScLoiLoa> loiloas = suppLoiLoaMap.get(scLoa.getScLoiLoa()
						.getSmSupplier().getSupplierId());
				if (loiloas != null && loiloas.size() > 0) {
					for (ScLoiLoa scLoiLoa : loiloas) {
						if (scLoiLoa.getScLois() != null
								&& scLoiLoa.getScLois().size() > 0) {
							lois.addAll(scLoiLoa.getScLois());
						}
					}
				}
			}
		} else {
			if (scLoa.getScLoiLoa() != null
					&& scLoa.getScLoiLoa().getScLois() != null
					&& scLoa.getScLoiLoa().getScLois().size() > 0) {
				lois = scLoa.getScLoiLoa().getScLois();
			}
		}

		VelocityContext context = new VelocityContext();

		context.put("isZonal", getManagedBean().getScQt().isZonal());
		context.put("scLois", lois);
		context.put("bean", this);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.LOI_NEGOTIONATION_RESULT, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Sum of gstAmount & loa offer price
	 * 
	 * @param gstAmount
	 */
	public void calculateOverallAmount(BigDecimal gstAmount) {
		ScLoa scLoa = getManagedBean().getScLoa();
		BigDecimal loaAmount = scLoa.getLoaAmt();
		BigDecimal contractOverAllAmt = BigDecimal.ZERO;
		prepareSignerInfo();
		if (loaAmount != null) {
			contractOverAllAmt   = overallCtAmount(loaAmount, gstAmount);
			this.getManagedBean().setOverallContractAmt(contractOverAllAmt);

			calcBondAmount(scLoa, true);
			// bond amount in words
			if (scLoa.getBondAmt() != null) {
				getManagedBean().setBondAmountWord(
						NumberWordUtil.convert(scLoa.getBondAmt().toString()));
			}
			
			if (null != scLoa.getContractSigner()
					&& scLoa.getContractSigner().getUserId() != null) {

				scLoa.setLoaSignerDesig(null);
				scLoa.setContractSigner(null);

				RequestContext.getCurrentInstance().execute(
						"gstSignerConfirmPopupWin.show()");
			}
		}
	}

	public boolean isGSTRegActive() {

		boolean isActive = true;
		ScLoa scLoa = getManagedBean().getScLoa();
		SmSupplier supplier = scLoa.getScLoiLoa().getSmSupplier();
		getManagedBean().setRenderRemainder(false);
		if ((supplier != null && supplier.getGstRegisteredFlag() == DbValueConst.RECORD_STATUS_ACTIVE)) {
			if (null != scLoa.getGstEffDate()) {
				if (scLoa.getGstEffDate().compareTo(new Date()) <= 0) {
					isActive = false;
				} else {
					getManagedBean().setRenderRemainder(isActive);
				}
			}
		}

		return isActive;
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

	public void resetContractDates() {
		this.getManagedBean().getScLoa().setContractEffDate(null);
		this.getManagedBean().getScLoa().setContractExpDate(null);
		this.getManagedBean()
				.getSpecTemplateSpecBean()
				.getSpecMaster()
				.setDurationMonth(
						this.getManagedBean().getScLoa().getContractDuration());

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

	private SupplierGstResDTO getGSTInfo(Long supplierId, Long branchId) {

		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchId);
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
		scLoa.setContractSigner(new PmUser());
	}

	private void updateTplSpecMasterNDetails(boolean isZonal) {
		TplSpecMaster producSpecMaster = this.getManagedBean().getProdscQtDoc()
				.getTplSpecMaster();
		TemplateSpecHelper helper = TemplateSpecHelper.getInstance();
		helper.updateZoneDtl(producSpecMaster,
				producSpecMaster.getTplSpecZone());
		resetTplSpecEstimations(producSpecMaster.getTplSpecQuestions(), helper,
				isZonal, this.getManagedBean().getProdTemplateSpecBean());
		TplSpecMaster serviceSpecMaster = this.getManagedBean()
				.getServicescQtDoc().getTplSpecMaster();
		helper.updateZoneDtl(serviceSpecMaster,
				serviceSpecMaster.getTplSpecZone());
		resetTplSpecEstimations(serviceSpecMaster.getTplSpecQuestions(),
				helper, isZonal, this.getManagedBean()
						.getSpecTemplateSpecBean());
	}

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
				scLoaReport.setPmDocument(getDocumentDtls(docCategory)); // PM
																			// document

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
		scLoa.setContractSigner(new PmUser());
		scLoa.setLoaSignerDesig(null);
		
		if (loaAmount != null) {
			contractOverAllAmt   = overallCtAmount(loaAmount, scLoa.getGstAmt());
		}
		getManagedBean().setOverallContractAmt(contractOverAllAmt);
		
		prepareSignerInfo();
		
		// calc bond amt
		calcBondAmount(scLoa, true);
		// bond amount in words
		if (scLoa.getBondAmt() != null) {
			getManagedBean().setBondAmountWord(
					NumberWordUtil.convert(scLoa.getBondAmt().toString()));
		}
	}

	private void updateToBeDeleteSpecAnswersTOReq(DirectAwardReqDTO awardReqDTO) {
		List<TplSpecAnswer> specAnswers = new ArrayList<TplSpecAnswer>();
		List<TplSpecAnswer> tobeDeletedSpecAnswers = this.getManagedBean()
				.getProdTemplateSpecBean().getSpecAnswersToDelete();
		List<TplSpecAnswer> tobeDeleteSpecAnswers = this.getManagedBean()
				.getSpecTemplateSpecBean().getSpecAnswersToDelete();
		if (CollectionUtils.isNotEmpty(tobeDeletedSpecAnswers)) {
			specAnswers.addAll(tobeDeletedSpecAnswers);
		}
		if (CollectionUtils.isNotEmpty(tobeDeleteSpecAnswers)) {
			specAnswers.addAll(tobeDeleteSpecAnswers);
		}

		awardReqDTO.setTobeDeletedList(specAnswers);
	}

	public void populateMinistryBasedOnLoginUser() {
		PmOrgProfile orgProfile = this.getManagedBean().getScQt()
				.getPmOrgProfile();

		if (!ParameterConst.ORG_TYPE_MINISTRY.equalsIgnoreCase(orgProfile
				.getOrgTypeCode().getParameterCode())) {

			orgProfile = getMinistryOrgProfile(orgProfile);

		}
		this.getManagedBean().getScQt().setMinistryOrgProfile(orgProfile);

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

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean isSuppDocFlag() {
		return suppDocFlag;
	}

	public void setSuppDocFlag(boolean suppDocFlag) {
		this.suppDocFlag = suppDocFlag;
	}

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

	private void validateContractAmount(BigDecimal contractAmt) {

		TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
		BigDecimal prodAmount = templateSpecBackingBean
				.calcGrandTotalAmount(this.getManagedBean()
						.getProdTemplateSpecBean());
		BigDecimal servAmount = templateSpecBackingBean
				.calcGrandTotalAmount(this.getManagedBean()
						.getSpecTemplateSpecBean());
		BigDecimal totalSpecEstimatedAmt = prodAmount.add(servAmount);
		if (contractAmt.compareTo(totalSpecEstimatedAmt) < 0) {
			throw new EpException(MessageConst.ERROR_700115, new String[] {
					totalSpecEstimatedAmt.toString(), contractAmt.toString() });
		}
	}

	/**
	 * To check is agreement required
	 * 
	 * @return boolean isRequirementRequired
	 */
	public boolean checkAgreementMandatory() {
		boolean isAgreementMandatory = Boolean.FALSE;
		ScLoa loa = getManagedBean().getScLoa();
		BigDecimal overallContractAmtOrLoaAmt = getManagedBean().getOverallContractAmt();
		
		if (null != loa.getScLoiLoa().getFulfilmentTypeCode() && null != overallContractAmtOrLoaAmt ) {
			String fulfilmentType = loa.getScLoiLoa().getFulfilmentTypeCode()
					.getParameterCode();

			boolean isOneOffFulfilment = ParameterConst.FULFILMENT_TYPE_ONE_OFF
					.equals(fulfilmentType);
			if (!isOneOffFulfilment) {
				isAgreementMandatory = true;
			} else if (isOneOffFulfilment && !isQuotationThresHold(overallContractAmtOrLoaAmt)) {
				isAgreementMandatory = true;
			}
		}
		loa.setAgreementReq(isAgreementMandatory);

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

			//create destination File object
			//StringBuilder aFilePath = new StringBuilder("c:");
			StringBuilder aFilePath = new StringBuilder();
			aFilePath.append(actualFilePath);
			File newName = new File(aFilePath.toString());
			boolean isFileRenamed = oldName.renameTo(newName);
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
				if (null == scLoaReport.getPmDocument()) {
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
	private void getMinistryConditionField(ScLoa scLoa) {
		ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
		
		OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		orgProfileReqDTO.setOrgProfileId(scLoiLoa.getPmOrgProfile()
				.getOrgProfileId());
		IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
				IOrgProfileSO.class);
		PmOrgProfile ministryOrgProfile = orgProfileSO
				.retrieveMinistryByOrgProfileId(orgProfileReqDTO)
				.getPmOrgProfile();
		StringBuilder criteria = new StringBuilder();
		criteria.append(
				"orgTypeCode.parameterCode")
				.append("^").append(OrganizationType.DIVISION.value())
				.append("|");
		
		criteria.append(
				"parentOrgProfile.orgProfileId")
				.append("^").append(ministryOrgProfile.getOrgProfileId())
				.append("|");
		
		criteria.append("recordStatus^1");
		getManagedBean().setMinistryConditionField(criteria.toString());
	}

}
