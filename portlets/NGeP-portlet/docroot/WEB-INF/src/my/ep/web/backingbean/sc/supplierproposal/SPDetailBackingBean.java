package my.ep.web.backingbean.sc.supplierproposal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.sc.EncryptDataBean;
import my.ep.biz.bean.sc.EncryptDataBean.EncryptColumnName;
import my.ep.biz.bean.sc.EncryptDataBean.EncryptTableName;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPAssetLiability;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCashOnHand;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCreditFacility;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPEmployee;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScSlAttachment;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.MessageUtils;
import my.ep.util.encrypt.BouncyCastleProvider_AES_CBC;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDocumentsBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Supplier Proposal Backing Bean
 * 
 * @author Viswanath
 * @author Venkat Chanumolu
 * 
 */
@ManagedBean(name = "supplierProposalDetailBackingBean")
@RequestScoped
public class SPDetailBackingBean extends
		SourcingBaseBackingBean<SPDetailManagedBean> {
	// Proposal 
	private static final String NAV_TO_PROPOSAL_MAIN_PAGE = "/xhtml/sc/supplierproposal/proposalcommon/supplierProposalMain.xhtml";

	// Technical Proposal
	private static final String NAV_TO_TECHNICAL_CHECKLIST_MAIN_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/techinicalChecklistmain.xhtml";
	private static final String NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalSpecification.xhtml";
	private static final String NAV_TO_NON_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalNonFederalGovt.xhtml";
	private static final String NAV_TO_NO_OF_EMPLOYEE__PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalNoOfEmployee.xhtml";
	private static final String NAV_TO_SAMPLING_ADDRESS_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalSamplingAddress.xhtml";
	private static final String NAV_TO_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalFederalGovt.xhtml";
	private static final String NAV_TO_TECHNICAL_EMPL_LIST_PAGE = "/xhtml/sc/supplierproposal/technicalproposal/technicalEmployeeList.xhtml";
	
	// Financial Proposal
	private static final String NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialchecklistsmain.xhtml";
	private static final String NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialSpecification.xhtml";
	private static final String NAV_TO_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialSpecMoreProposal.xhtml";
	private static final String NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialMoreZone.xhtml";
	private static final String NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialNonFederalGovt.xhtml";
	private static final String NAV_TO_CREDIT_FACILITIES_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialCreditFacilities.xhtml";
	private static final String NAV_TO_CASH_ON_HAND_PAGES = "/xhtml/sc/supplierproposal/financialproposal/financialCashOnHand.xhtml";
	private static final String NAV_TO_FINANCE_BANK_STATEMENT_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialBankStatement.xhtml";
	private static final String NAV_TO_ASSET_LIABILITY_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialAssetLiability.xhtml";
	private static final String NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialFederalGovt.xhtml";
	private static final String NAV_TO_SUPPLIER_DETAILS_PAGE = "/xhtml/sc/supplierproposal/financialproposal/supplierDetailsTabList.xhtml";
	private static final String NAV_TO_PAID_UP_CAPITAL_PAGE = "/xhtml/sc/supplierproposal/financialproposal/financialPaidUpCapital.xhtml";
	
	
	

	private static final SimpleDateFormat BANK_STATEMENT_MONTH_FORMAT = new SimpleDateFormat(
			"MMMM yyyy");

	private static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	/**
	 * Default Constructor
	 */
	public SPDetailBackingBean() {
		log.info("inside constructor");

		if (null != getManagedBean().getMessage()) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							getManagedBean().getMessage()));
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * retrieving Technical & Financial Proposal CheckList
	 * 
	 * @param qtID
	 * @param userId
	 */
	public void retrieveProposalChecklistDtls(Long qtID, Long userId) {
		debugLogger("retrieveProposalChecklistDtls");

		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtId(qtID);
			reqDTO.setUserId(userId);

			ProposalResDTO resDTO = getProposalSO()
					.retrieveProposalDtlBySupplierId(reqDTO);

			ScQt scQt = resDTO.getScQt();

			getManagedBean().setQt(resDTO.getScQt());

			getManagedBean().setScQtDocReports(resDTO.getScQtDocReports());

			getManagedBean().setQtProposal(resDTO.getScQtProposal());
			getManagedBean().setSupplierId(resDTO.getSupplierId());

			// Technical Check List
			if (resDTO.getTechnicalCheckLists() != null) {
				getManagedBean().setTecCheckDetailsList(
						resDTO.getTechnicalCheckLists());
				log.info("Technical Check list: "
						+ resDTO.getTechnicalCheckLists().size());
			}

			// Financial Check List
			if (resDTO.getFinancialCheckLists() != null) {
				getManagedBean().setFinanceCheckDetailsList(
						resDTO.getFinancialCheckLists());
				log.info("Financial Check list: "
						+ resDTO.getFinancialCheckLists().size());
			}

			if (null != scQt.getClosingDate()) {
				// hide the save/submit,add and delete buttons when QT is closed or Invalid
				Date closingDate = scQt.getClosingDate();
				PmStatus qtStatus = scQt.getQtStatus();
				if (new Date(System.currentTimeMillis()).after(closingDate) || (qtStatus != null
						&& !QTStatusConst.QT_PROPOSAL_PREPARATION
						.equals(qtStatus.getStatusId()))) {
					getManagedBean().setClosed(true);
					getManagedBean().setClosingTime("0");
				} else {
					Date today = new Date();
					long days = DateUtils.getDaysDifference(today,
							getManagedBean().getQt().getClosingDate());
					DecimalFormat df = new DecimalFormat("#00");
					getManagedBean().setClosingTime(df.format(days) + " Days");
					if (days == 0) {
						Long durationInMills = DateUtils.getDateDiffInMillis(
								DateUtils.getCurrentDate(), getManagedBean()
										.getQt().getClosingDate());
						if (durationInMills > 0) {
							getManagedBean()
									.setClosingTime(
											DateUtils
													.durationFormatter(durationInMills));
						} else {
							getManagedBean().setClosingTime("0");
						}
					}
				}

			}
			// checking for proposal validity
			reqDTO.setScQt(resDTO.getScQt());
			getProposalSO().validateSupplierProposal(reqDTO);

		} finally {
		}
	}

	/**
	 * Listener for tab change to retrieve Quotation/Tender Document Details
	 */
	public void retrieveQTDocumentDetails() {
		debugLogger("retrieveTechnicalProposalList");
		this.getManagedBean().setPrepareActiveIndex(0);
	}

	/**
	 * Listener for tab change to retrieve Technical Proposal List
	 */
	public void retrieveTechnicalProposalList() {
		debugLogger("retrieveTechnicalProposalList");
		this.getManagedBean().setPrepareActiveIndex(1);
	}

	/**
	 * Listener for tab change to retrieve Financial Proposal List
	 */
	public void retrieveFinancialProposalList() {
		debugLogger("retrieveFinancialProposalList");
		this.getManagedBean().setPrepareActiveIndex(2);
	}

	/**
	 * Listener for tab change to retrieve Declaration details
	 * 
	 * @throws Exception
	 */
	public void retrieveDeclarationList() throws Exception {
		debugLogger("retrieveDeclarationList");
		this.getManagedBean().setPrepareActiveIndex(3);

		VelocityContext context = new VelocityContext();
		String ptjName = this.getManagedBean().getQt().getPmOrgProfile().getOrgName();
		context.put("ptjName", ptjName);
		String othersTermsConditions = DocumentGenerator.getInstance()
				.getHtmlFromVelocity(
						SourcingConstants.SUPPLIER_PROPOSAL_DECLARATION,
						context);
		getManagedBean().setOthersTermsConditions(othersTermsConditions);

	}

	/**
	 * method to retrieve integrity details
	 * 
	 * @throws Exception
	 */

	public void retrieveIntegrityDetails() throws Exception {
		debugLogger("retrieveIntegrityDetails");
		this.getManagedBean().setPrepareActiveIndex(4);
		generateDeclarationFromVelocity(getManagedBean().getQt());
	}

	/**
	 * Method to submit the Supplier Proposal details
	 */
	public void submitSupplierProposal() {
		
		PmDigiSign digiSign = null;
		debugLogger("submitSupplierProposal");

		// retrievePaidUpCapitalNdDeclarationDtls();

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			this.getManagedBean().setScQtPRPExperiences(null);

			List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();
			List<ScQtProposalChecklist> technicalChecklist = getManagedBean()
					.getTecCheckDetailsList();
			List<ScQtProposalChecklist> financialChecklist = getManagedBean()
					.getFinanceCheckDetailsList();

			ScQtProposal scQtProposal = getManagedBean().getQtProposal();
			setUserDetails(scQtProposal);
			scQtProposal.setLastSubmitDate(new Date());
			scQtProposal.setSubmitted(true);
			scQtProposal.setProposalSubmitBy(new FacesScopes().getUserSessionBean().getUserId());

			scQtProposalChecklists.addAll(technicalChecklist);
			scQtProposalChecklists.addAll(financialChecklist);
			scQtProposal
					.setScQtProposalChecklists(new ArrayList<ScQtProposalChecklist>(
							scQtProposalChecklists));

			/*
			 * // spki
			 * RequestContext.getCurrentInstance().execute("blockWidget.hide();"
			 * );
			 * RequestContext.getCurrentInstance().execute("spki_dlg.hide();");
			 * String originalText =
			 * prepareOriginalText(getManagedBean().getSmSupplier()); byte[]
			 * signedData =
			 * getManagedBean().getSpkiBackingBean().spkiSignTransaction
			 * (originalText); digiSign.setSpkiSignedData(signedData);
			 * 
			 * if (null != digiSign) { updateSpkiDigiSignData(digiSign,
			 * ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL,
			 * scQtProposal.getQtProposalId(), scQtProposal.getProposalNo(),
			 * originalText); }
			 */

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtSupplierId(getManagedBean().getQtProposal()
					.getScQtSupplier().getQtSupplierId());
			reqDTO.setScQtProposal(scQtProposal);
			reqDTO.setTechnicalChecklist(new ArrayList<ScQtProposalChecklist>(
					technicalChecklist));
			reqDTO.setFinancialChecklist(new ArrayList<ScQtProposalChecklist>(
					financialChecklist));
			reqDTO.setAgreeWithIntegrityPact(getManagedBean()
					.isAgreeWithIntegrityPact());
			reqDTO.setAgreeWithOthersTermsConditions(getManagedBean()
					.isAgreeWithOthersTermsConditions());
			// reqDTO.setDigiSignData(digiSign);

			ProposalResDTO resDTO = proposalSO.saveQTDtl(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getMessageCommon("lbl.sc.proposal.no");
				String variable2 = scQtProposal.getProposalNo();
				commonMessageAcknowledmentPage(MessageConst.INFO_700026,
						variable1, variable2);
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			proposalSO = null;
		}
	}

	private void commonMessageAcknowledmentPage(String messageId,
			String variable1, String variable2) {
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(messageId, new String[] { variable1,
								variable2 },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		// clear data
		super.renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}

	/**
	 * method to save financial non government company experiences..
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialNonGovCompanyExpAction(boolean isCompleted) {
		debugLogger("saveFinancialNonGovCompanyExpAction");

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			int index = getManagedBean().getFinanceCheckDetailsList()
					.indexOf(scQtProposalChecklist);

			CriteriaValue contractAmountVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV,
					this.getManagedBean().getTotalContractAmount());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), contractAmountVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			// Added to set null which is the data will be encrypted and save .
			List<ScQtPRPExperience> experiences = this.getManagedBean().getScQtPRPExperiences();
			List<List<EncryptDataBean>>	encryptExpriencList = new ArrayList<List<EncryptDataBean>>();	
			for(ScQtPRPExperience experience:experiences){
				List<EncryptDataBean> experienceBeanList = new ArrayList<EncryptDataBean>();
				if(experience.getEffectiveDate() != null){
					EncryptDataBean effDateBean = new EncryptDataBean();
					effDateBean.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE.toString());
					effDateBean.setColumnName(EncryptColumnName.EFF_DATE.toString());
					effDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy", experience.getEffectiveDate()));
					experienceBeanList.add(effDateBean);
					experience.setEffectiveDate(null);
				}
				if(experience.getExpiryDate() != null){
					EncryptDataBean expDateBean = new EncryptDataBean();
					expDateBean.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE.toString());
					expDateBean.setColumnName(EncryptColumnName.EXP_DATE.toString());
					expDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy", experience.getExpiryDate()));
					experienceBeanList.add(expDateBean);
					experience.setExpiryDate(null);
				}
				if(experience.getContractAmt() != null){
					EncryptDataBean ctAmtDateBean = new EncryptDataBean();
					ctAmtDateBean.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE.toString());
					ctAmtDateBean.setColumnName(EncryptColumnName.CONTRACT_AMT.toString());
					ctAmtDateBean.setValue(experience.getContractAmt().toString());
					experienceBeanList.add(ctAmtDateBean);
					experience.setExpiryDate(null);
				}
				encryptExpriencList.add(experienceBeanList);
			}
			
			ScQtProposal proposal = this.getManagedBean().getQtProposalChecklist().getScQtProposal();
			proposal.setScQtProposalExperiences(experiences);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ScQtProposalChecklist techncalProposalChecklist = null;
			// to update technical non fed govt exp checklist whenever financial
			// checklist is saved
			if (!this.getManagedBean().isFinanicialOnlyCheckList()) {
				techncalProposalChecklist = findProposalChecklistByDocDefKey(
						getManagedBean().getTecCheckDetailsList(),
						SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				reqDTO.setTobeUpdateTechProposalChecklist(techncalProposalChecklist);
				reqDTO.setChecklistType(SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
			} else {
				reqDTO.setChecklistType(SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				reqDTO.setTobeRemovedExperience(this.getManagedBean()
						.getDeletedPRPExperiences());
			}
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setScQtProposal(proposal);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setDeletedPRPExperiences(null);
				ScQtProposal qtProPosal = resDTO.getScQtProposal();
				this.getManagedBean().setScQtProposal(qtProPosal);
				debugLogger("*****save successfull*****"
						+ resDTO.isSuccessful());
				// Encrypt the data &  Save 
				List<ScQtPRPExperience> proposalExp = qtProPosal
						.getScQtProposalExperiences();
				if (CollectionUtils.isNotEmpty(encryptExpriencList)) {
					List<EncryptDataBean> encryptencryptExpList = new ArrayList<EncryptDataBean>();
					for (int i = 0; i < proposalExp.size(); i++) {
						List<EncryptDataBean> encryptExpList = encryptExpriencList.get(i);
						for (EncryptDataBean encryptExp : encryptExpList) {
							encryptExp.setPkId(proposalExp.get(i).getQtPrpExperienceId());
							encryptencryptExpList.add(encryptExp);
						}
					}
					reqDTO.setScQt(this.getManagedBean().getQt());
					reqDTO.setEncryptDataList(encryptencryptExpList);
					proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				}
					
				getManagedBean().getFinanceCheckDetailsList().set(index,
						resDTO.getProposalChecklist());
				this.getManagedBean().setQtProposalChecklist(resDTO.getProposalChecklist());
				// retrieveTechnicalFedNonGovCheklist
				if (!this.getManagedBean().isFinanicialOnlyCheckList()) {
					updateQtFinNdTechProposalCheckList(
							techncalProposalChecklist,
							resDTO.getTobeUpdateTechProposalChecklist(),
							SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				}
				if(isCompleted == true){
					loadDataFromCache();
				}
				
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save technical specification details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechnicalSpecificationAction(boolean isCompleted) {
		debugLogger("saveTechnicalSpecificationAction");

		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getQtProposalChecklist();

		scQtProposalChecklist.setSubmitted(isCompleted);

		ProposalResDTO proposalResDTO = saveTechTempleateSpecDtls();

		if (proposalResDTO.isSuccessful()) {
			return navigatePrepareTecProposalCheckList(scQtProposalChecklist,
					proposalResDTO.getProposalChecklist(), isCompleted);
		}

		return null;
	}

	/**
	 * method to save financial specification details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialSpecificationAction(boolean isCompleted) {
		debugLogger("saveFinancialSpecificationAction");

		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getQtProposalChecklist();

		scQtProposalChecklist.setSubmitted(isCompleted);

		ProposalResDTO proposalResDTO = saveTempleateSpecDtls();

		if (proposalResDTO.isSuccessful()) {
			return navigatePrepareFinProposalCheckList(scQtProposalChecklist,
					proposalResDTO.getProposalChecklist(), isCompleted);
		}

		return null;
	}

	private ProposalResDTO saveTempleateSpecDtls() {
		debugLogger("saveTempleateSpecDtls");
		IQTProposalSO proposalSO = null;
		ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
		ProposalResDTO proposalResDTO = new ProposalResDTO();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<TplSpecAnswer> answers = null;

			if (getManagedBean().getQt().isZonal()) {

				if (getManagedBean().getSelectedZone() != null) {
					getManagedBean().getZoneTplSpecBeanMap().put(
							getManagedBean().getSelectedZone().getSpecZoneId(),
							getManagedBean().getTemplateSpecBean());
				}
				List<TemplateSpecBean> templateSpecBeans = new ArrayList<TemplateSpecBean>();
				templateSpecBeans.addAll(getManagedBean()
						.getZoneTplSpecBeanMap().values());
				if (templateSpecBeans != null && templateSpecBeans.size() > 0) {
					answers = new ArrayList<TplSpecAnswer>();
					for (TemplateSpecBean templateSpecBean : templateSpecBeans) {
						if (templateSpecBean.getSpecAnswerList() != null
								&& templateSpecBean.getSpecAnswerList().size() > 0) {
							boolean isContainPrice = false;
							for (TplSpecAnswer specAnswer : templateSpecBean
									.getSpecAnswerList()) {
								if (specAnswer.getRatePerUom() != null
										&& specAnswer.getRatePerUom()
												.compareTo(
														BigDecimal.valueOf(0)) > 0) {
									isContainPrice = true;
									break;
								}
							}

							if (isContainPrice) {
								answers.addAll(templateSpecBean
										.getSpecAnswerList());
							}
						}
					}
				}

			} else {
				answers = getManagedBean().getTemplateSpecBean()
						.getSpecAnswerList();
			}

			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalReqDTO.setScQt(getManagedBean().getQt());
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

	private ProposalResDTO saveTechTempleateSpecDtls() {
		debugLogger("saveTempleateSpecDtls");
		IQTProposalSO proposalSO = null;
		ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
		ProposalResDTO proposalResDTO = new ProposalResDTO();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<TplSpecAnswer> answers = getManagedBean()
					.getTemplateSpecBean().getSpecAnswerList();
			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalReqDTO.setScQt(this.getManagedBean().getQt());
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

	/**
	 * method to save financial credit facilities details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialCreditFacilityAction(boolean isCompleted) {
		debugLogger("saveFinancialCreditFacilityAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			CriteriaValue totalCreditVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES,
					this.getManagedBean().getTotalPercentagePaid());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalCreditVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			List<ScQtPRPCreditFacility> creditFacilitiesList = this
					.getManagedBean().getScQtPRPCreditFacilities();
			
			List<List<EncryptDataBean>>	encryptCrditFacilitesList = new ArrayList<List<EncryptDataBean>>();
			for(ScQtPRPCreditFacility prpCreditFacilities : creditFacilitiesList){
				
				if (prpCreditFacilities.isRecordStatus()) {
					List<EncryptDataBean> crditFacilitesList = new ArrayList<EncryptDataBean>();
					if (prpCreditFacilities.getCreditAmt() != null) {
						EncryptDataBean amtCredit = new EncryptDataBean();
						amtCredit.setTableName(EncryptTableName.SC_QT_PRP_CREDIT_FACILITY
										.toString());
						amtCredit.setColumnName(EncryptColumnName.CREDIT_AMT.toString());
						amtCredit.setValue(prpCreditFacilities.getCreditAmt().toString());
						crditFacilitesList.add(amtCredit);
						prpCreditFacilities.setCreditAmt(null);
					}
					if (prpCreditFacilities.getAmtPaid() != null) {
						EncryptDataBean amtPaid = new EncryptDataBean();
						amtPaid.setTableName(EncryptTableName.SC_QT_PRP_CREDIT_FACILITY.toString());
						amtPaid.setColumnName(EncryptColumnName.AMT_PAID.toString());
						amtPaid.setValue(prpCreditFacilities.getAmtPaid().toString());
						crditFacilitesList.add(amtPaid);
						prpCreditFacilities.setAmtPaid(null);
					}
					encryptCrditFacilitesList.add(crditFacilitesList);
				}
			}
			scQtProposalChecklist.setScQtPRPCreditFacilities(creditFacilitiesList);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobeRemovedCreditFacilities(getManagedBean()
					.getToBeDeletedCreditFacilities());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setToBeDeletedCreditFacilities(null);
				List<ScQtPRPCreditFacility> creditFacility = resDTO.getProposalChecklist().getScQtPRPCreditFacilities();
				if (CollectionUtils.isNotEmpty(encryptCrditFacilitesList)) {
					List<EncryptDataBean> encryptCreditFecList = new ArrayList<EncryptDataBean>();
					for (int i = 0; i < creditFacility.size(); i++) {
						List<EncryptDataBean> encryptExpList = encryptCrditFacilitesList.get(i);
						for (EncryptDataBean encryptExp : encryptExpList) {
							encryptExp.setPkId(creditFacility.get(i).getCreditFacilityId());
							encryptCreditFecList.add(encryptExp);
						}
					}
					reqDTO.setScQt(this.getManagedBean().getQt());
					reqDTO.setEncryptDataList(encryptCreditFecList);
					proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				}
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save financial cash on hand details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialCashOnHandAction(boolean isCompleted) {
		debugLogger("saveFinancialCashOnHandAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			CriteriaValue cashOnHandVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND, this
							.getManagedBean().getTotalcashHand());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), cashOnHandVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			List<ScQtPRPCashOnHand>  qtPRPCashOnHandList = this.getManagedBean()
					.getScQtPRPCashOnHand();
			scQtProposalChecklist.setScQtPRPCashOnHands(qtPRPCashOnHandList);
			
			List<EncryptDataBean> cashOnHandList = new ArrayList<EncryptDataBean>();
			for(ScQtPRPCashOnHand prpCashOnHand : qtPRPCashOnHandList){
				if(prpCashOnHand.isRecordStatus() && prpCashOnHand.getCashAmount()!= null){
					EncryptDataBean cashAmount = new EncryptDataBean ();
					cashAmount.setTableName(EncryptTableName.SC_QT_PRP_CASH_ONHAND.toString());
					cashAmount.setColumnName(EncryptColumnName.CASH_AMT.toString());
					cashAmount.setValue(prpCashOnHand.getCashAmount().toString());
					cashOnHandList.add(cashAmount);
				}
			}
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobecashOnHand(getManagedBean()
					.getToBeDeletedCashOnHandList());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setToBeDeletedCashOnHandList(null);
				List<ScQtPRPCashOnHand> prpCashOnHandList = resDTO
						.getProposalChecklist().getScQtPRPCashOnHands();
				// Encrypt the data &  Save 
				for (int i = 0; i < prpCashOnHandList.size(); i++) {
					cashOnHandList.get(i).setPkId(
							prpCashOnHandList.get(i).getPrpCashOnHandId());
				}
				reqDTO.setScQt(getManagedBean().getQt());
				reqDTO.setEncryptDataList(cashOnHandList);
				proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save three months bank Statement details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveBankStatementAction(boolean isCompleted) {
		debugLogger("saveBankStatementAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			CriteriaValue avgBankStatementVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT,
					this.getManagedBean().getAvgAmount());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), avgBankStatementVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			List<ScQtPRPBankStmt> prpBankStmts = this.getManagedBean().getScQtPRPBankStmts();
			scQtProposalChecklist.setPrpBankStmts(prpBankStmts);
			List<EncryptDataBean> encryptBankStmtList = new ArrayList<EncryptDataBean>();
			for (ScQtPRPBankStmt prpBankStmt:prpBankStmts){
				if(prpBankStmt.getBalanceAmt() != null){
					EncryptDataBean balanceAmt = new EncryptDataBean();
					balanceAmt.setTableName(EncryptTableName.SC_QT_PRP_BANK_STATEMENT.toString());
					balanceAmt.setColumnName(EncryptColumnName.BALANCE_AMT.toString());
					balanceAmt.setValue(prpBankStmt.getBalanceAmt().toString());
					encryptBankStmtList.add(balanceAmt);
					prpBankStmt.setBalanceAmt(null);
				}
			}
				
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				// Encrypt the data &  Save 
				List<ScQtPRPBankStmt> prpBankStmtsList = resDTO.getProposalChecklist().getPrpBankStmts();
				for (int i = 0; i < prpBankStmtsList.size(); i++) {
					encryptBankStmtList.get(i).setPkId(
							prpBankStmtsList.get(i).getBankStmtId());
				}
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setEncryptDataList(encryptBankStmtList);
				proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to no of employees involved details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechEmployeesDtlAction(boolean isCompleted) {
		debugLogger("saveTechEmployessDtlAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			CriteriaValue totalEmployeeVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE,
					BigDecimal.valueOf(getManagedBean().getScQtPRPEmployee()
							.getTotalEmployees()));

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalEmployeeVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			ScQtPRPEmployee prpEmployee = getManagedBean().getScQtPRPEmployee();
			List<EncryptDataBean> encEmployeeList = null;
			if(prpEmployee != null && prpEmployee.getTotalEmployees() != 0){
				
				encEmployeeList = new ArrayList<EncryptDataBean>();
				EncryptDataBean encEmployeeBean = new EncryptDataBean();
				encEmployeeBean.setTableName(EncryptTableName.SC_QT_PRP_EMPLOYEE.toString());
				encEmployeeBean.setColumnName(EncryptColumnName.TOTAL_EMPLOYEE.toString());
				encEmployeeBean.setValue(Integer.toString(prpEmployee.getTotalEmployees()));
				encEmployeeList.add(encEmployeeBean);
				prpEmployee.setTotalEmployees(0);
			}
			scQtProposalChecklist.setScQtPRPEmployee(prpEmployee);

			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				if(CollectionUtils.isNotEmpty(encEmployeeList)){
				ScQtPRPEmployee employee = resDTO.getProposalChecklist().getScQtPRPEmployee();
				encEmployeeList.get(0).setPkId(employee.getPrpEmployeeId());
				reqDTO.setEncryptDataList(encEmployeeList);
				proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				}
				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save technical non government company experience details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechNonGovCompanyExpAction(boolean isCompleted) {
		debugLogger("saveTechNonGovCompanyExpAction");
		validateContractNo();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			
			int index = getManagedBean().getTecCheckDetailsList().indexOf(
					scQtProposalChecklist);
			List<ScQtPRPExperience> experiences = this.getManagedBean()
					.getScQtPRPExperiences();
			
			MathContext mc = new MathContext(4);
			CriteriaValue totalContractsVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV,
					BigDecimal.valueOf(getManagedBean().getTotalContracts()));
			CriteriaValue totalYearsVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV,
					BigDecimal.valueOf(getManagedBean().getTotalYears())
							.divide(BigDecimal.valueOf(12), mc));
			
			// Added to set null which is the data will be encrypted and save .
				List<List<EncryptDataBean>>	encryptExpriencList = new ArrayList<List<EncryptDataBean>>();	
				 for(ScQtPRPExperience experience:experiences){
					List<EncryptDataBean> experienceBeanList = new ArrayList<EncryptDataBean>();
					if(experience.getEffectiveDate() != null){
						EncryptDataBean effDateBean = new EncryptDataBean();
						 effDateBean.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE.toString());
						 effDateBean.setColumnName(EncryptColumnName.EFF_DATE.toString());
						 effDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy", experience.getEffectiveDate()));
						 experienceBeanList.add(effDateBean);
						  experience.setEffectiveDate(null);
					}
					if(experience.getExpiryDate() != null){
						EncryptDataBean expDateBean = new EncryptDataBean();
						expDateBean.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE.toString());
						expDateBean.setColumnName(EncryptColumnName.EXP_DATE.toString());
						expDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy", experience.getExpiryDate()));
						experienceBeanList.add(expDateBean);
						experience.setExpiryDate(null);
					 }
					encryptExpriencList.add(experienceBeanList);
				}
			scQtProposalChecklist.setSubmitted(isCompleted);

			// initial save list is empty
			List<ScQtPRPExperience> experiencesTosave = new ArrayList<ScQtPRPExperience>();

			for (ScQtPRPExperience prpExperience : experiences) {
				// previously saved records ignored
				if (prpExperience.getQtPrpExperienceId() == null
						|| prpExperience.getQtPrpExperienceId() == 0) {
					// scQtProposalChecklist.getScQtProposal().setScQtProposalChecklists(scQtProposalChecklists)
					experiencesTosave.add(prpExperience);
				}
			}

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalContractsVal,
					totalYearsVal);

			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			// to delete the deleted experiences from db
			reqDTO.setTobeRemovedExperience(getManagedBean()
					.getDeletedPRPExperiences());
			ScQtProposal qtProposal = this.getManagedBean().getQtProposalChecklist().getScQtProposal();
			qtProposal.setScQtProposalExperiences(experiencesTosave);
			reqDTO.setScQtProposal(qtProposal);
			ScQtProposalChecklist finProposalChecklist = findProposalChecklistByDocDefKey(
					getManagedBean().getFinanceCheckDetailsList(),
					SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
			reqDTO.setChecklistType(SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
			reqDTO.setTobeUpdateProposalChecklist(finProposalChecklist);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setDeletedPRPExperiences(null);
				// Encrypt the data &  Save 
				this.getManagedBean().setScQtProposal(resDTO.getScQtProposal());
				List<ScQtPRPExperience> proposalExp = resDTO.getScQtProposal()
						.getScQtProposalExperiences();
				if (CollectionUtils.isNotEmpty(encryptExpriencList)) {
					List<EncryptDataBean> encryptencryptExpList = new ArrayList<EncryptDataBean>();
					for (int i = 0; i < proposalExp.size(); i++) {
						List<EncryptDataBean> encryptExpList = encryptExpriencList.get(i);
						for (EncryptDataBean encryptExp : encryptExpList) {
							encryptExp.setPkId(proposalExp.get(i).getQtPrpExperienceId());
							encryptencryptExpList.add(encryptExp);
						}
					}
					
					reqDTO.setEncryptDataList(encryptencryptExpList);
					proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				} // End
				
				updateQtFinNdTechProposalCheckList(
						finProposalChecklist,
						resDTO.getTobeUpdateProposalChecklist(),
						SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				getManagedBean().getTecCheckDetailsList().set(index,
						resDTO.getProposalChecklist());
				getManagedBean().setQtProposalChecklist(resDTO.getProposalChecklist());
				if(isCompleted == true){
					loadDataFromCache();
				}
				
				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save assets and liabilities details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveAssetLiabilityAction(boolean isCompleted) {
		debugLogger("saveAssetLiabilityAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			ScQtPRPAssetLiability scQtPRPAssetLiability = this.getManagedBean()
					.getScQtPRPAssetLiability();

			CriteriaValue acidRatioVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO,
					scQtPRPAssetLiability.getAcidRatio());
			
			CriteriaValue currentRatioVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO,
					scQtPRPAssetLiability.getCurrentRatio());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), acidRatioVal,currentRatioVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
             List<EncryptDataBean> prpAssetLiabilityList = new ArrayList<EncryptDataBean>();
             if(scQtPRPAssetLiability.getCurrentAssets() != null){
            	 EncryptDataBean currentAsset = new EncryptDataBean ();
            	 currentAsset.setTableName(EncryptTableName.SC_QT_PRP_ASSET_LIABILITY.toString());
            	 currentAsset.setColumnName(EncryptColumnName.CURRENT_ASSETS.toString());
            	 currentAsset.setValue(scQtPRPAssetLiability.getCurrentAssets().toString());
            	 prpAssetLiabilityList.add(currentAsset);
            	 scQtPRPAssetLiability.setCurrentAssets(null);
             }
             if(scQtPRPAssetLiability.getCurrentLiabilities() != null){
            	 EncryptDataBean currentLiabilities = new EncryptDataBean ();
            	 currentLiabilities.setTableName(EncryptTableName.SC_QT_PRP_ASSET_LIABILITY.toString());
            	 currentLiabilities.setColumnName(EncryptColumnName.CURRENT_LIABILITIES.toString());
            	 currentLiabilities.setValue(scQtPRPAssetLiability.getCurrentLiabilities().toString());
            	 prpAssetLiabilityList.add(currentLiabilities);
            	 scQtPRPAssetLiability.setCurrentLiabilities(null);
             }
			scQtProposalChecklist.setPrpAssetLiabilities(scQtPRPAssetLiability);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				// Encrypt the data &  Save 
				for (EncryptDataBean prpAssetLiability : prpAssetLiabilityList) {
					prpAssetLiability.setPkId(resDTO.getProposalChecklist()
							.getPrpAssetLiabilities().getPrpAssesLiabilityId());
				}
				reqDTO.setScQt(this.getManagedBean().getQt());
				reqDTO.setEncryptDataList(prpAssetLiabilityList);
				proposalSO.saveEncryptedProposalCheckLists(reqDTO);
				
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * method to save Paid Up Capital details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialPaidUpCapitalAction(boolean isCompleted) {
		debugLogger("saveFinancialPaidUpCapitalAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			CriteriaValue cashOnHandVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND, this
							.getManagedBean().getTotalcashHand());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), cashOnHandVal);

			scQtProposalChecklist.setSubmitted(isCompleted);

			scQtProposalChecklist.setScQtPRPCashOnHands(this.getManagedBean()
					.getScQtPRPCashOnHand());
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			return navigatePrepareFinProposalCheckList(scQtProposalChecklist,
					resDTO.getProposalChecklist(), isCompleted);
		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to handle file upload events
	 * 
	 * @param event
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		if (event.getFile() != null) {
			IQTProposalSO proposalSO = null;
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				ScQtProposalChecklist scQtProposalChecklist = this
						.getManagedBean().getQtProposalChecklist();
				ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
						.getScQtProposalChecklistAttachment();
				if (null == attachment) {
					attachment = new ScQtProposalChecklistAttachment();
					attachment.setScQtProposalChecklist(scQtProposalChecklist);
				}
				attachment.setDocNo(getManagedBean().getQt().getQtNo());
				attachment.setPrefixFileName(getManagedBean().getQtProposal()
						.getProposalNo());

				uploadAttachmentWithEncryption(event.getFile(), attachment);

				scQtProposalChecklist
						.setScQtProposalChecklistAttachment(attachment);
				scQtProposalChecklist.setSubmitted(true);

				reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
				reqDTO.setScQt(this.getManagedBean().getQt());
				ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

				if (resDTO.isSuccessful()) {
					updateQtProposalCheckList(getManagedBean()
							.getQtProposalChecklist(),
							resDTO.getProposalChecklist());
				}

			} finally {
				proposalSO = null;
			}
		}
	}

	/**
	 * method to handle file upload events with encryption
	 * 
	 * @param file
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	protected void uploadAttachmentWithEncryption(UploadedFile file,
			ScAttachment attachment) throws FileNotFoundException, IOException {
		try{
			String fileName = file.getFileName();
			// byte[] fileBytes = file.getContents();
			FileInputStream fis = (FileInputStream) file.getInputstream();
			attachment.setFileName(fileName);
			attachment.setFileDesc("File uploded");

			populateAttachmentDetails(attachment);

			FileOutputStream fos;
			fos = new FileOutputStream(getActualFilePath(attachment));
			// fos.write(fileBytes);
			// fos.close();
			BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC();
			bc.InitEncryptCipher();
			bc.CBCEncryptFile(fis, fos);
		}
		catch(Exception e){
			throw new EpException(e.getLocalizedMessage());
		}
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	/**
	 * method to handle download events
	 * 
	 * @param scQtProposalChecklist
	 * @throws IOException
	 */

	public void downloadPTJFile(ScQtProposalChecklist scQtProposalChecklist)
			throws IOException {
		debugLogger("downloadPTJFile");

		downloadFile(scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment());
	}

	/**
	 * method to handle download events related to encrypted file
	 * 
	 * @param scAttachment
	 * @return encrypted file
	 * @throws IOException
	 */
	public <ATTACHMENT extends ScAttachment> File downloadEncryptedFile(
			ATTACHMENT scAttachment) throws IOException {
		if (scAttachment != null
				&& StringUtils.isNotBlank(scAttachment.getFileName())) {

			return downloadEncryptedFile(getActualFilePath(scAttachment),
					scAttachment.getFileName());
		} else {
			throw new EpException(MessageConst.ERROR_760329);
		}
	}

	/**
	 * method to handle download events related to encrypted file
	 * 
	 * @param filePath
	 * @param fileName
	 * @return encrypted file
	 * @throws IOException
	 */
	public File downloadEncryptedFile(String filePath, String fileName)
			throws IOException {

		log.info("Inside downloadFile(...)");
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse response = com.liferay.portal.util.PortalUtil
				.getHttpServletResponse(portletResponse);

		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		// response.setContentType("application/vnd.ms-excel");
		// response.setHeader("Content-Transfer-Encoding", "binary");
		String finalFileName = fileName.replaceAll("\\s+",QTConstants.REPLACE_CHAR);
		response.setHeader("Content-Disposition", "attachment; filename="
				+ finalFileName);
		response.setContentType("application/octet-stream");
		response.flushBuffer();

		OutputStream out = response.getOutputStream();

		if (filePath != null) {
			InputStream input = null;
			try {
				log.info("Inside filePath(...)");
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				log.info("Inside filePaths(...)");
				input = new FileInputStream(filePath);
			}
			// byte[] buffer = new byte[4096];
			// int bytesRead;
			// while ((bytesRead = input.read(buffer)) != -1) {
			// out.write(buffer, 0, bytesRead);
			// buffer = new byte[4096];

			BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC();
			bc.InitDecryptCipher();
			bc.CBCDecryptFile(input, out);

			input.close();
		}

		out.flush();
		out.close();
		FacesContext.getCurrentInstance().responseComplete();

		// use to upload back to database
		File file = new File(filePath);
		return file;
	}

	/**
	 * method to handle download events related to SP file
	 * 
	 * @param scQtProposalChecklist
	 * @throws Exception
	 */
	public void downloadSPFile(ScQtProposalChecklist scQtProposalChecklist)
			throws Exception {
		debugLogger("downloadSPFile");
		/*
		 * ScQtProposalChecklistAttachment checklistAttachment =
		 * scQtProposalChecklist .getScQtProposalChecklistAttachment();
		 * System.out.println("File Name : " +
		 * checklistAttachment.getFilePath());
		 * 
		 * File file = new File(checklistAttachment.getFilePath()); InputStream
		 * input = new FileInputStream(file);
		 * 
		 * PortletResponse portletResponse = (PortletResponse) FacesContext
		 * .getCurrentInstance().getExternalContext().getResponse();
		 * HttpServletResponse res = com.liferay.portal.util.PortalUtil
		 * .getHttpServletResponse(portletResponse);
		 * res.setHeader("Content-Disposition", "attachment; filename=\"" +
		 * checklistAttachment.getFileName() + "\"");
		 * res.setHeader("Content-Transfer-Encoding", "binary");
		 * res.setContentType("application/octet-stream"); res.flushBuffer();
		 * OutputStream out = res.getOutputStream(); if
		 * (checklistAttachment.getFilePath() != null) {
		 * 
		 * try { URLConnection uc = new URL(checklistAttachment.getFilePath())
		 * .openConnection(); input = new
		 * BufferedInputStream(uc.getInputStream()); } catch (Exception e) { //
		 * Cannot open connection, maybe local file input = new
		 * FileInputStream(checklistAttachment.getFilePath()); } byte[] buffer =
		 * new byte[4096]; int bytesRead; while ((bytesRead =
		 * input.read(buffer)) != -1) { out.write(buffer, 0, bytesRead); buffer
		 * = new byte[4096]; } input.close(); } else { //
		 * out.write(attachment.getFileByte()); } out.flush(); out.close();
		 */

	}

	private IQTProposalSO getProposalSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
	}

	/**
	 * action method for preparing proposal details for the technical checklist
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String prepareTechnicalAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalAction");
		String pageUrl = null;
		String definationType = scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode();
		this.getManagedBean().setDocDefinitionKey(definationType);
		this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
		this.getManagedBean().setScQtCheckList(
				scQtProposalChecklist.getScQtCheckList());

		// to check proposalchecklist is technicalspecification - technical
		// standard product
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
				.equalsIgnoreCase(definationType)) {

			pageUrl = retrieveTemplateSpecDtls(scQtProposalChecklist);

		}
		// to check proposalchecklist is technicalspecification - technical
		// NON_FEDERAL_GOVT
		else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM
				.equalsIgnoreCase(definationType)) {
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);
			if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV
					.equalsIgnoreCase(docDefinitionKey)) {
				pageUrl = retrieveTecNonGovCompanyExpDtls(scQtProposalChecklist);
			}

			// to check proposalchecklist is technicalspecification -
			// technical NO_OF_EMPLOYEE__PAGE
			else if (SourcingConstants.DOC_DEF_KEY_NO_OF_EMPLOYEES
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveEmployeeDtls(scQtProposalChecklist);

			}
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
				.equalsIgnoreCase(definationType)) {
			pageUrl = retrieveSampleDetails(scQtProposalChecklist);

		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equalsIgnoreCase(definationType)) {
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);
			if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveTecCompanyExpDtls(scQtProposalChecklist);

			} // to check proposalchecklist is technicalspecification -
				// technical EMPL_LIST_PAGE
			else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_STAFF_LIST
					.equalsIgnoreCase(docDefinitionKey)) {
				pageUrl = retriveTechnicalStaffDtls(scQtProposalChecklist);

			}
		}
		this.getManagedBean().setPageUrl(pageUrl);
		return NAV_TO_TECHNICAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";
	}

	private String retrieveTemplateSpecDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		ScQtDoc scQtDoc = scQtProposalChecklist.getScQtCheckList().getScQtDoc();
		this.getManagedBean().setScQtDoc(
				scQtProposalChecklist.getScQtCheckList().getScQtDoc());

		TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
		
		templateSpecBean.setSpecMaster(specMaster);
		templateSpecBean.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

		SmSupplier smSupplier = new SmSupplier();
		smSupplier.setSupplierId(getManagedBean().getSupplierId());
		templateSpecBean.setSupplier(smSupplier);
		templateSpecBackingBean.getManagedBean().setScQt(this.getManagedBean().getQt());
		templateSpecBackingBean.getManagedBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
		templateSpecBackingBean.retrieveTemplateSpecMaster(templateSpecBean);

		return NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE;
	}

	/**
	 * method to retrieve technical non government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveTecNonGovCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {

		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().getYear());
		Calendar mindate = Calendar.getInstance();
		mindate.set(getManagedBean().getYear().intValue(), 0, 1);
		getManagedBean().setNonFedEffMinDate(mindate.getTime());
		getManagedBean().setNonFedMinDate(mindate.getTime());

		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setDocDefinationKey(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey());
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			ProposalResDTO resDTO = proposalSO
					.retrieveNonGovCompanyExpDtls(reqDTO);
			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());
			this.getManagedBean().setScQtPRPExperiences(
					resDTO.getPrpExperience());

			ScQtProposalScore totalContractScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV);
			ScQtProposalScore contractYearsScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV);

			int totalContracts = 0;
			if (totalContractScore != null
					&& totalContractScore.getCriteriaValue() != null) {
				totalContracts = totalContractScore.getCriteriaValue()
						.intValue();
			}
			this.getManagedBean().setTotalContracts(totalContracts);

			/*int totalYears = 0;
			if (contractYearsScore != null
					&& contractYearsScore.getCriteriaValue() != null) {
				totalYears = contractYearsScore.getCriteriaValue()
						.multiply(BigDecimal.valueOf(12)).intValue();
			}
			this.getManagedBean().setTotalYears(totalYears);*/

			return NAV_TO_NON_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}


	/**
	 * method to retrieve technical no of employees involved details
	 * 
	 * @param scQtProposalChecklist
	 * @return
	 */
	public String retrieveEmployeeDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQtCheckListValue scQtCheckListValue = scQtProposalChecklist
					.getScQtCheckList().getScQtCheckListValue();

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.retrieveEmployeeDtls(reqDTO);

			ScQtPRPEmployee scQtPRPEmployee = resDTO.getScQtPRPEmployee();

			if (resDTO.getScQtPRPEmployee() == null) {
				scQtPRPEmployee = new ScQtPRPEmployee();
				scQtPRPEmployee.setScQtProposalChecklist(scQtProposalChecklist);
			}

			if (scQtCheckListValue != null
					&& scQtCheckListValue.isAllEmployee()) {
				getManagedBean().setSupplierId(
						scQtProposalChecklist.getScQtProposal()
								.getScQtSupplier().getSmSupplier()
								.getSupplierId());
				retrieveSupplierDtls();

				scQtPRPEmployee.setTotalEmployees(getManagedBean().getSmAppl()
						.getSmPersonnels().size());
			}

			this.getManagedBean().setScQtPRPEmployee(scQtPRPEmployee);
			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			return NAV_TO_NO_OF_EMPLOYEE__PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/*
	 * public String prepareTechnicalViewAction( ScQtProposalChecklist
	 * scQtProposalChecklist) { debugLogger("prepareTechnicalViewAction");
	 * 
	 * String definationType = scQtProposalChecklist.getScQtCheckList()
	 * .getDefinationType().getParameterCode();
	 * 
	 * getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
	 * getManagedBean().setScQtCheckList(
	 * scQtProposalChecklist.getScQtCheckList());
	 * 
	 * if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
	 * .equalsIgnoreCase(definationType)) { return
	 * retrieveSampleDetails(scQtProposalChecklist);
	 * 
	 * } else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
	 * .equalsIgnoreCase(definationType)) { String docDefinitionKey =
	 * scQtProposalChecklist.getScQtCheckList()
	 * .getPmDocument().getDocDefinitionKey();
	 * 
	 * if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV
	 * .equalsIgnoreCase(docDefinitionKey)) { return
	 * retrieveTecCompanyExpDtls(scQtProposalChecklist);
	 * 
	 * } // to check proposalchecklist is technicalspecification - // technical
	 * EMPL_LIST_PAGE else if
	 * (SourcingConstants.DOC_DEF_KEY_TECHNICAL_STAFF_LIST
	 * .equalsIgnoreCase(docDefinitionKey)) { return
	 * retriveTechnicalStaffDtls(scQtProposalChecklist);
	 * 
	 * } } else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM
	 * .equalsIgnoreCase(definationType)) { String docDefinitionKey =
	 * scQtProposalChecklist.getScQtCheckList()
	 * .getPmDocument().getDocDefinitionKey();
	 * 
	 * if (SourcingConstants.DOC_DEF_KEY_NO_OF_EMPLOYEES
	 * .equalsIgnoreCase(docDefinitionKey)) {
	 * 
	 * return retrieveEmployeeDtls(scQtProposalChecklist);
	 * 
	 * } }
	 * 
	 * return null; }
	 */

	private String retrieveSampleDetails(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQt scQt = getManagedBean().getQt();

			reqDTO.setQtId(scQt.getQtId());
			reqDTO.setQtProposalId(getManagedBean().getQtProposal()
					.getQtProposalId());
			ProposalResDTO resDTO = proposalSO.retrieveSampleDetails(reqDTO);

			List<ScQtSampleSubmission> sampleSubmissionDtls = resDTO
					.getSampleSubmissionDtls();

			if (scQt.isZonal()) {
				getManagedBean().setScQtSampleSubmissionList(
						sampleSubmissionDtls);
			} else {
				if (sampleSubmissionDtls != null
						&& sampleSubmissionDtls.size() > 0) {
					getManagedBean().setScQtSampleSubmission(
							sampleSubmissionDtls.get(0));
				}
			}

			getManagedBean().setScQtSampleLable(resDTO.getScQtSampleLable());

			return NAV_TO_SAMPLING_ADDRESS_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve technical staff details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retriveTechnicalStaffDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		List<TechnicalStaffBean> smPersonnelsList = new ArrayList<TechnicalStaffBean>();

		retrieveSupplierDtls();

		// Contruct qualification map
		Map<Long, PmParameterDesc> qualificationMap = new HashMap<Long, PmParameterDesc>();
		for (PmParameterDesc parameterDesc : ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.QUALIFICATION_LEVEL)) {

			qualificationMap.put(parameterDesc.getPmParameter()
					.getParameterId(), parameterDesc);
		}

		// int totalYearsExp = 0;
		for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
				.getSmPersonnels()) {
			TechnicalStaffBean technicalStaffBean = new TechnicalStaffBean(
					smPersonnel);

			// Populate parameter desc from SmStaffAcademic.
			for (SmStaffAcademic staffAcademic : smPersonnel
					.getSmStaffAcademics()) {
				PmParameterDesc qualificationDesc = qualificationMap
						.get(staffAcademic.getQualificationLevelId()
								.longValue());

				if (qualificationDesc != null) {
					if (technicalStaffBean.getSmStaffAcademics() == null) {
						technicalStaffBean
								.setSmStaffAcademics(new ArrayList<PmParameterDesc>());
					}

					technicalStaffBean.getSmStaffAcademics().add(
							qualificationDesc);
				}
			}

			smPersonnelsList.add(technicalStaffBean);

			// totalYearsExp += smPersonnel.getWorkExperienceYear().intValue();
		}
		getManagedBean().setSmPersonnelList(smPersonnelsList);
		getManagedBean().setTotalNoOfYearsExperience(smPersonnelsList.size());
		// getManagedBean().setTotalNoOfYearsExperience(totalYearsExp);
		// log.info("Total No of Years Experience : " + totalYearsExp);

		return NAV_TO_TECHNICAL_EMPL_LIST_PAGE;
	}

	/**
	 * method to retrieve technical federal government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveTecCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {

		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().getYear());

		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist
					.getQtProposalChecklistId());

			reqDTO.setYear(scQtProposalChecklist.getScQtCheckList()
					.getScQtCheckListValue().getYear().intValue());
			reqDTO.setScQt(this.getManagedBean().getQt());
			
			ProposalResDTO resDTO = proposalSO.retrieveCompanyExpDtls(reqDTO);
			List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
			this.getManagedBean().setScLoaList(list);
			int total = 0;
			for (int i = 0; i < getManagedBean().getScLoaList().size(); i++) {
				ScLoa loa = getManagedBean().getScLoaList().get(i);
				if (loa.getContractDuration() == null) {
					loa.setContractDuration(new BigDecimal(0));
				}
				total = total + loa.getContractDuration().intValue();
			}
			this.getManagedBean().setNoOfYears(total);
			this.getManagedBean().setContractSize(list.size());

			return NAV_TO_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Listener method to prepare multiple proposal selection event
	 * 
	 * @param index
	 * @return
	 */
	public String prepareMultileProposalAction(int index) {
		debugLogger("prepareMultileProposalAction");
		getManagedBean().setProposalIndex(index);

		retrieveTemplateSpecBean();

		this.getManagedBean().setPageUrl(NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE);
		// this.getManagedBean().setPageUrl(getFinancialSpecUrl(scQt));
		return NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";

	}

	/**
	 * method to retrieve financial proposal details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String prepareFinancialAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFinancialAction");
		String pageUrl = null;
		String definationType = scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode();
		this.getManagedBean().setDocDefinitionKey(definationType);
		this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
		this.getManagedBean().setScQtCheckList(
				scQtProposalChecklist.getScQtCheckList());

		// to check financial check list is for
		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
				.equalsIgnoreCase(definationType)) {
			pageUrl = retrieveFinancialSpecDtls(scQtProposalChecklist);
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM
				.equalsIgnoreCase(definationType)) {

			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);

			if (SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveFinNonGovCompanyExpDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_CREDIT_FACILITIES
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveCreditFacilitiesDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_CASH_ON_HAND
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveCashOnHandDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_FINANCE_BANK_STATEMENT
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveBankStmtDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_ASSET_LIABILITY
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveAssetLiabilityDtls(scQtProposalChecklist);

			}
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equalsIgnoreCase(definationType)) {
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);

			if (SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV
					.equalsIgnoreCase(docDefinitionKey)) {
				pageUrl = retrieveFinCompanyExpDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_DETAILS
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveSupplierDtls();

			} else if (SourcingConstants.DOC_DEF_KEY_PAID_UP_CAPITAL
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrievePaidUpCapitalNdDeclarationDtls();
			}
		}
		this.getManagedBean().setPageUrl(pageUrl);
		return NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";
	}

	private String retrieveFinancialSpecDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		this.getManagedBean().setActiveIndex(0);
		this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
		IQTProposalSO proposalSO = null;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQt scQt = getManagedBean().getQt();
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setScQt(scQt);;
			reqDTO.setQtSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtId(scQt.getQtId());
			// To retrieve financial spec details
			ProposalResDTO resDTO = proposalSO
					.retrieveFinancialSpecDtls(reqDTO);

			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zones = resDTO.getZones();
				if (CollectionUtils.isNotEmpty(zones)) {
					this.getManagedBean().setZones(zones);
					for (TplSpecZoneDtl specZone : zones) {
						this.getManagedBean().setSelectedZone(specZone);
						retrieveTemplateSpecBean();
					}
					this.getManagedBean().setSelectedZone(zones.get(0));
					getManagedBean().setTemplateSpecBean(
							getManagedBean().getZoneTplSpecBeanMap().remove(
									getManagedBean().getSelectedZone()
											.getSpecZoneId()));
				}
				populateFinancialSpecDetailsByZone();

			}
			if (!scQt.isZonal() && scQt.getMaxProposal() == 1) {
				retrieveTemplateSpecBean();
			}

			return getFinancialSpecUrl(scQt);
		} finally {
			proposalSO = null;
		}
	}

	private void retrieveTemplateSpecBean() {

		ScQtProposalChecklist qtProposalChecklist = getManagedBean()
				.getQtProposalChecklist();
		ScQtDoc scQtDoc = qtProposalChecklist.getScQtCheckList().getScQtDoc();
		this.getManagedBean().setScQtDoc(scQtDoc);

		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

		ITemplateSpecSO templateSpecSO = null;
		try {

			templateSpecSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
							ITemplateSpecSO.class);
			TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
			reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setSupplier(smSupplier);

			reqDTO.setSpecMaster(specMaster);
			if (getManagedBean().getSelectedZone() != null) {
				reqDTO.setTplSpecZoneDtl(getManagedBean().getSelectedZone());
			}
			if (getManagedBean().getQt().getMaxProposal() > 1) {
				reqDTO.setSubProposalNo(getManagedBean().getProposalIndex());
			}
			reqDTO.setScQt(getManagedBean().getQt());
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			TemplateSpecResDTO resDTO = templateSpecSO
					.getTemplateMaster(reqDTO);
			if (resDTO.isSuccessful()) {
				specMaster = resDTO.getSpecMaster();
				TemplateSpecBean templateSpecBean = new TemplateSpecBean();
				templateSpecBean.setSpecMaster(specMaster);
				templateSpecBean.setContentType(reqDTO.getContentType());
				templateSpecBean.setSpecAnswerList(resDTO.getSpecAnswerList());
				templateSpecBean.setSelectedSpecQuestion(null);
				this.getManagedBean().getTemplateSpecBackingBean().calcGrandTotalAmount(templateSpecBean);
				if (getManagedBean().getQt().isZonal()) {
					getManagedBean().getZoneTplSpecBeanMap().put(
							getManagedBean().getSelectedZone().getSpecZoneId(),
							templateSpecBean);
				} else {
					getManagedBean().setTemplateSpecBean(templateSpecBean);
				}

			}
		} finally {

			templateSpecSO = null;
		}

	}

	private String getFinancialSpecUrl(ScQt scQt) {
		String specUrl = null;

		if (scQt.getMaxProposal() > 1) {
			if (scQt.isZonal()) {
				specUrl = NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE;
			} else {
				specUrl = NAV_TO_MORE_THAN_PROPOSAL_PAGE;
			}
		} else {
			specUrl = NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE;
		}

		return specUrl;
	}

	/**
	 * method to retrieve financial non federal government company experience
	 * details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveFinNonGovCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {

		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().getYear());

		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			List<ScQtProposalChecklist> techinicalProposalCheckLists = this
					.getManagedBean().getTecCheckDetailsList();
			ScQtProposalChecklist tecProposalCheckList = findProposalChecklistByDocDefKey(
					techinicalProposalCheckLists,
					SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setDocDefinationKey(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey());
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setFinancial(true);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			
			ProposalResDTO resDTO = null;
			if (tecProposalCheckList == null) {
				this.getManagedBean().setFinanicialOnlyCheckList(true);
				Calendar mindate = Calendar.getInstance();
				mindate.set(getManagedBean().getYear().intValue(), 0, 1);
				getManagedBean().setNonFedEffMinDate(mindate.getTime());
				getManagedBean().setNonFedMinDate(mindate.getTime());
				this.getManagedBean().setScQtProposal(
						scQtProposalChecklist.getScQtProposal());
				resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);
			} else {

				if (null != tecProposalCheckList
						&& tecProposalCheckList.isSubmitted()) {
					resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);
				} else {
					// TODO add exception remarks
					throw new EpException(
							"Please Complete Technical Non Gov Company Experiences first");
				}
			}
			if (resDTO != null) {

				List<ScQtPRPExperience> scQtPRPExperiences = resDTO
						.getPrpExperience();
				this.getManagedBean().setScQtPRPExperiences(scQtPRPExperiences);
				this.getManagedBean().setScQtProposalScoreList(
						resDTO.getProposalScores());
				ScQtProposalScore contractAmountScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV);

				if (contractAmountScore != null) {
					this.getManagedBean().setTotalContractAmount(
							contractAmountScore.getCriteriaValue());
				}
				totalContractAmount();
			}

			return NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	private ScQtProposalChecklist findProposalChecklistByDocDefKey(
			List<ScQtProposalChecklist> proposalChecklists, String docDefKey) {
		ScQtProposalChecklist proposalCheckList = null;

		if (null != proposalChecklists && StringUtils.isNotBlank(docDefKey)) {
			for (ScQtProposalChecklist prChecklist : proposalChecklists) {
				if (null != prChecklist.getScQtCheckList().getPmDocument()) {
					if (docDefKey.equalsIgnoreCase(prChecklist
							.getScQtCheckList().getPmDocument()
							.getDocDefinitionKey())) {
						proposalCheckList = prChecklist;
						break;
					}
				}
			}
		}

		return proposalCheckList;
	}

	/**
	 * method to retrieve financial credit facilities details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveCreditFacilitiesDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			// to retrieve credit facility details
			ProposalResDTO resDTO = proposalSO
					.retrieveCreditFacilitiesDtls(reqDTO);

			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			List<ScQtPRPCreditFacility> facilitiesList = resDTO
					.getCreditFacilityResultBean();

			this.getManagedBean().setScQtPRPCreditFacilities(facilitiesList);

			totalCreditFacilitiesSum();

			ScQtProposalScore creditFacilitiesScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES);

			if (creditFacilitiesScore != null) {
				/*
				 * this.getManagedBean().setTotalAmountCredit(
				 * creditFacilitiesScore.getCriteriaValue());
				 */
			}

			return NAV_TO_CREDIT_FACILITIES_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve financial Cash On Hand details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveCashOnHandDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.retrieveCashOnHandDtls(reqDTO);
			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			this.getManagedBean().setScQtPRPCashOnHand(
					resDTO.getCashOnHandResultBean());

			ScQtProposalScore cashOnHandScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND);

			if (cashOnHandScore != null) {
				this.getManagedBean().setTotalcashHand(
						cashOnHandScore.getCriteriaValue());
			}

			return NAV_TO_CASH_ON_HAND_PAGES;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve financial three months bank statement details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveBankStmtDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.retrieveBankStmtDtls(reqDTO);
			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			List<ScQtPRPBankStmt> bankStmtList = resDTO.getBankStmtResultBean();

			for (ScQtPRPBankStmt scQtPRPBankStmt : bankStmtList) {
				Date date = scQtPRPBankStmt.getStmtDate();

				scQtPRPBankStmt.setMonth(BANK_STATEMENT_MONTH_FORMAT
						.format(date));
			}

			log.info("Size : " + bankStmtList.size());
			this.getManagedBean().setScQtPRPBankStmts(bankStmtList);

			totalAmountSum();

			ScQtProposalScore avgBankStatementScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT);

			if (avgBankStatementScore != null) {
				this.getManagedBean().setAvgAmount(
						avgBankStatementScore.getCriteriaValue());
			}
			return NAV_TO_FINANCE_BANK_STATEMENT_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve financial Assets and Liabilities details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */

	public String retrieveAssetLiabilityDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO
					.retrieveAssetLiabilityDtls(reqDTO);

			ScQtPRPAssetLiability assetLiability = resDTO
					.getScQtPRPAssetLiability();

			if (assetLiability == null) {
				assetLiability = new ScQtPRPAssetLiability();
				assetLiability.setScQtProposalChecklist(scQtProposalChecklist);
			}

			this.getManagedBean().setScQtPRPAssetLiability(assetLiability);
			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			ScQtProposalScore assetLiabilityAcidRatioScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);
			ScQtProposalScore assetLiabilityCurrentRatioScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO);

			if (assetLiabilityAcidRatioScore != null) {
				getManagedBean().getScQtPRPAssetLiability().setAcidRatio(
						assetLiabilityAcidRatioScore.getCriteriaValue());
			}
			
			if (assetLiabilityCurrentRatioScore != null) {
				getManagedBean().getScQtPRPAssetLiability().setCurrentRatio(
						assetLiabilityCurrentRatioScore.getCriteriaValue());
			}

			return NAV_TO_ASSET_LIABILITY_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve financial supplier limited details for paid up capital
	 * 
	 * @return navigation path
	 */
	public String retrievePaidUpCapitalNdDeclarationDtls() {
		log.info("*****retrievePaidUpCapitalNdDeclarationDtls*****");
		long supplierId = getManagedBean().getSupplierId();
		log.info("Supplier Id : " + supplierId);

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setSupplierId(supplierId);
			ProposalResDTO resDTO = proposalSO
					.retrivePaidUpCapitalBySupplierById(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmSupplier(resDTO.getSmSupplier());
		} finally {
			proposalSO = null;
		}

		return NAV_TO_PAID_UP_CAPITAL_PAGE;
	}

	/*
	 * public String prepareFinancialViewAction( ScQtProposalChecklist
	 * scQtProposalChecklist) { debugLogger("prepareFinancialViewAction");
	 * 
	 * String definationType = scQtProposalChecklist.getScQtCheckList()
	 * .getDefinationType().getParameterCode();
	 * 
	 * getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
	 * getManagedBean().setScQtCheckList(
	 * scQtProposalChecklist.getScQtCheckList());
	 * 
	 * // what's happening here // check still valid or not if
	 * ("SP".equalsIgnoreCase(definationType)) {
	 * 
	 * ScQtCheckList scQtCheckList = scQtProposalChecklist .getScQtCheckList();
	 * this.getManagedBean().setScQtCheckList(scQtCheckList);
	 * 
	 * return null;
	 * 
	 * } else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
	 * .equalsIgnoreCase(definationType)) { String definitionKey =
	 * scQtProposalChecklist.getScQtCheckList()
	 * .getPmDocument().getDocDefinitionKey();
	 * 
	 * if (SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV
	 * .equalsIgnoreCase(definitionKey)) {
	 * 
	 * return retrieveFinCompanyExpDtls(scQtProposalChecklist);
	 * 
	 * } else if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_DETAILS
	 * .equalsIgnoreCase(definitionKey)) {
	 * 
	 * return retrieveSupplierDtls(scQtProposalChecklist);
	 * 
	 * } else if (SourcingConstants.DOC_DEF_KEY_PAID_UP_CAPITAL
	 * .equalsIgnoreCase(definitionKey)) {
	 * 
	 * return retrievePaidUpCapitalDtls(scQtProposalChecklist); } }
	 * 
	 * return null; }
	 */

	/**
	 * method to retrieve financial company experience with federal government
	 * details
	 * 
	 * @param scQtProposalChecklist
	 * @return
	 */
	public String retrieveFinCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {

		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().getYear());

		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			reqDTO.setYear(scQtProposalChecklist.getScQtCheckList()
					.getScQtCheckListValue().getYear().intValue());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist
					.getQtProposalChecklistId());
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			
			ProposalResDTO resDTO = proposalSO.retrieveCompanyExpDtls(reqDTO);
			List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
			log.info(" List Size : " + list.size());
			this.getManagedBean().setScLoaList(list);
			int totalAmount = 0;
			for (ScLoa scLoa : list) {
				totalAmount = totalAmount + scLoa.getLoaAmt().intValue();
			}
			this.getManagedBean().setTotalContractAmount(
					new BigDecimal(totalAmount));

			return NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * method to retrieve Supplier details
	 * 
	 * @return navigation path
	 */

	public String retrieveSupplierDtls() {
		Long supplierId = getManagedBean().getSupplierId();
		log.info("Supplier Id : " + supplierId);

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setSupplierId(supplierId);
			ProposalResDTO resDTO = proposalSO.retriveSupplierById(reqDTO);
			getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			log.info("*****SmAppl Appl_Id : *****"
					+ resDTO.getSmAppl().getApplId());

			// equityShareHolders for
			List<SmPersonnel> equityShareHolders = new ArrayList<SmPersonnel>();

			for (SmPersonnel personnel : resDTO.getSmAppl().getSmPersonnels()) {
				if (personnel.isEquityOwner()) {
					equityShareHolders.add(personnel);

				}
			}
			getManagedBean().setEquityShareHolders(equityShareHolders);

			// To set supplier Basic Company details
			if (getManagedBean().getSupplierFacilities() == null) {
				getManagedBean().setSupplierFacilities(
						new ArrayList<SmFacility>());
			}

			for (SmFacility facility : resDTO.getSmAppl().getSmFacilities()) {

				if (facility
						.getFacilityType()
						.equals(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)
						|| facility
								.getFacilityType()
								.equals(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)) {
					getManagedBean().setSmCompanyBasic(facility);
				} else {
					getManagedBean().getSupplierFacilities().add(facility);
				}

			}

			// To fetch data for personnel tab using the new static method

			if (getManagedBean().getSmAppl().getSmPersonnels() != null) {
				SmPersonnleCalcVO personnelVO = SmCommonUtil
						.calcPersonnelListCount(getManagedBean().getSmAppl()
								.getSmPersonnels(), getManagedBean()
								.getSmAppl().getSmStaffTotals(),
								getManagedBean().getSmSupplier()
										.getBusinessType());
				getManagedBean().setPersonCalc(personnelVO);

			}

			// To set data for bankinfo tab
			getManagedBean().setSmChequeSignatories(
					getManagedBean().getSmAppl().getSmSupplierBanks().get(0)
							.getSmChequeSignatories());

			// staffInformation();
			log.info("Paid up capital : "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital());
			log.info("Authorized capital : "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getAuthorizedCapital());
			log.info("Account Number : "
					+ getManagedBean().getSmAppl().getSmSupplierBanks().get(0)
							.getAccountNo());

		} finally {
			proposalSO = null;
		}

		return NAV_TO_SUPPLIER_DETAILS_PAGE;
	}

	/**
	 * method to add total Credit Facilities
	 */
	public void totalCreditFacilitiesSum() {
		debugLogger("totalCreditFacilitiesSum");

		BigDecimal totalCredit = null;
		BigDecimal totalPaid = null;
		BigDecimal percentagePaid = null;

		List<ScQtPRPCreditFacility> creditFacilitiesList = this
				.getManagedBean().getScQtPRPCreditFacilities();
		if (creditFacilitiesList == null) {
			creditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
		}
		for (ScQtPRPCreditFacility facility : creditFacilitiesList) {

			if (facility.getCreditAmt() != null) {
				if (totalCredit == null) {
					totalCredit = BigDecimal.valueOf(0);
				}
				totalCredit = totalCredit.add(facility.getCreditAmt());
			}

			if (facility.getAmtPaid() != null) {
				if (totalPaid == null) {
					totalPaid = BigDecimal.valueOf(0);
				}
				totalPaid = totalPaid.add(facility.getAmtPaid());
			}
		}

		if (creditFacilitiesList.size() > 0 && totalPaid != null
				&& totalPaid.intValue() > 0) {
			MathContext mc = new MathContext(4);

			percentagePaid = totalPaid.divide(totalCredit, mc).multiply(
					BigDecimal.valueOf(100));

		}

		this.getManagedBean().setTotalAmountCredit(totalCredit);
		this.getManagedBean().setTotalAmountPaid(totalPaid);
		this.getManagedBean().setTotalPercentagePaid(percentagePaid);
	}

	/**
	 * method to add total cash on hand
	 */
	public void totalCashOnHandSum() {
		debugLogger("totalAmtPaidSum");

		BigDecimal totalCashOnHand = null;
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		if (cashOnHandList == null) {
			cashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
		}

		for (ScQtPRPCashOnHand cashOnHand : cashOnHandList) {
			if (cashOnHand.getCashAmount() != null) {
				if (totalCashOnHand == null) {
					totalCashOnHand = BigDecimal.valueOf(0);
				}
				totalCashOnHand = totalCashOnHand.add(cashOnHand
						.getCashAmount());
			}
		}
		this.getManagedBean().setTotalcashHand(totalCashOnHand);
	}

	/**
	 * method to calculate total amount
	 */
	public void totalAmountSum() {
		debugLogger("totalAmountSum");

		BigDecimal totalAmount = null;
		BigDecimal avgAmount = null;
		List<ScQtPRPBankStmt> bankStmtList = this.getManagedBean()
				.getScQtPRPBankStmts();
		if (bankStmtList == null) {
			bankStmtList = new ArrayList<ScQtPRPBankStmt>();
		}

		for (ScQtPRPBankStmt scQtPRPBankStmt : bankStmtList) {
			if (scQtPRPBankStmt.getBalanceAmt() != null) {
				if (totalAmount == null) {
					totalAmount = BigDecimal.valueOf(0);
				}
				totalAmount = totalAmount.add(scQtPRPBankStmt.getBalanceAmt());
			}
		}

		if (bankStmtList.size() > 0 && totalAmount != null
				&& totalAmount.intValue() > 0) {
			MathContext mc = new MathContext(14);

			avgAmount = totalAmount.divide(
					BigDecimal.valueOf(bankStmtList.size()), mc);

		}
		this.getManagedBean().setTotalAmount(totalAmount);
		this.getManagedBean().setAvgAmount(avgAmount);
	}

	/**
	 * Listener method for effective date selection events
	 * 
	 * @param prpExperience
	 */
	public void effectiveDateSelectListener(ScQtPRPExperience prpExperience) {

		getManagedBean().setNonFedMinDate(prpExperience.getEffectiveDate());
		prpExperience.setExpiryDate(null);
		prpExperience.setContractPeriod(0);

		/*
		 * if (prpExperience.getEffectiveDate() != null &&
		 * prpExperience.getExpiryDate() != null) { Calendar startCalendar = new
		 * GregorianCalendar();
		 * startCalendar.setTime(prpExperience.getEffectiveDate()); Calendar
		 * endCalendar = new GregorianCalendar();
		 * endCalendar.setTime(prpExperience.getExpiryDate()); int diffYear =
		 * endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
		 * int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) -
		 * startCalendar.get(Calendar.MONTH);
		 * prpExperience.setContractPeriod(diffMonth);
		 * 
		 * }
		 */
		int index = getManagedBean().getScQtPRPExperiences().indexOf(
				prpExperience);
		getManagedBean().getScQtPRPExperiences().set(index, prpExperience);

		// totalContractYears();
	}

	/**
	 * Listener method for expire date selection events
	 * 
	 * @param prpExperience
	 */
	public void expiryDateSelectListener(ScQtPRPExperience prpExperience) {
		/*
		 * ScQtPRPExperience prpExperience = getManagedBean()
		 * .getScQtPRPExperiences().get(index);
		 */

		int index = getManagedBean().getScQtPRPExperiences().indexOf(
				prpExperience);
		// getManagedBean().setNonFedEffMaxDate(prpExperience.getExpiryDate());

		if (prpExperience.getEffectiveDate() == null) {
			prpExperience.setExpiryDate(null);
			getManagedBean().getScQtPRPExperiences().set(index, prpExperience);
			throw new EpException(MessageConst.ERROR_760330);
		}

		if (prpExperience.getEffectiveDate() != null
				&& prpExperience.getExpiryDate() != null) {
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(prpExperience.getEffectiveDate());
			Calendar endCalendar = new GregorianCalendar();
			// to resolve one day difference
			Calendar c = Calendar.getInstance();
			c.setTime(prpExperience.getExpiryDate());
			c.add(Calendar.DATE, 1);
			endCalendar.setTime(c.getTime());
			int diffYear = endCalendar.get(Calendar.YEAR)
					- startCalendar.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH)
					- startCalendar.get(Calendar.MONTH);
			prpExperience.setContractPeriod(diffMonth);

		}
		getManagedBean().getScQtPRPExperiences().set(index, prpExperience);
	}

	

	/**
	 * method to calculate total Contract Amount
	 */
	public void totalContractAmount() {
		debugLogger("totalContractAmount");

		BigDecimal totalContractAmount = null;

		List<ScQtPRPExperience> experiencesList = this.getManagedBean()
				.getScQtPRPExperiences();
		if (experiencesList == null) {
			experiencesList = new ArrayList<ScQtPRPExperience>();
		}
		for (ScQtPRPExperience experience : experiencesList) {

			if (experience.getContractAmt() != null) {
				if (totalContractAmount == null) {
					totalContractAmount = BigDecimal.valueOf(0);
				}
				totalContractAmount = totalContractAmount.add(experience
						.getContractAmt());
			}
		}

		this.getManagedBean().setTotalContractAmount(totalContractAmount);
	}

	/**
	 * method to calculate acid ratio
	 */
	public void totalAcidRatio() {
		debugLogger("totalAcidRatio");

		ScQtPRPAssetLiability assetLiability = this.getManagedBean()
				.getScQtPRPAssetLiability();
		BigDecimal asset = assetLiability.getCurrentAssets();
		BigDecimal liabilities = assetLiability.getCurrentLiabilities();
		BigDecimal acidRatio = null;
		BigDecimal currentRatio = null;
	BigDecimal totalCashInHand=	assetLiability.getTotalCashInHand();
	BigDecimal totalAccountReceivable=	assetLiability.getTotalAccountReceivable();
		
		if(null!=totalCashInHand && null!=totalAccountReceivable && null!=liabilities){
			BigDecimal totalAmount=totalCashInHand.add(totalAccountReceivable);
			MathContext mc = new MathContext(14);
			acidRatio = totalAmount.divide(liabilities, mc);
			assetLiability.setAcidRatio(acidRatio);
		}

		if (asset != null && liabilities != null && liabilities.intValue() > 0) {

			MathContext mc = new MathContext(14);

			currentRatio = asset.divide(liabilities, mc);
			assetLiability.setCurrentRatio(currentRatio);
		} else {
			assetLiability.setAcidRatio(new BigDecimal(0));
			assetLiability.setCurrentRatio(new BigDecimal(0));
		}

	}

	/**
	 * method to add Non Federal Government Experience to the list
	 */

	public void addNonFederalGovtExperience() {
		debugLogger("addNonFederalGovtExperience");
		ScQtPRPExperience prpExperience = new ScQtPRPExperience();

		try {
			List<ScQtPRPExperience> experienceList = this.getManagedBean()
					.getScQtPRPExperiences();
			if (null == experienceList) {
				experienceList = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : experienceList) {
				scQtPRPExperience.setBrandNew(false);
			}

			prpExperience.setScQtProposal(this.getManagedBean()
					.getQtProposalChecklist().getScQtProposal());
			prpExperience.setContractAmt(new BigDecimal(0));
			experienceList.add(prpExperience);
			this.getManagedBean().setScQtPRPExperiences(experienceList);
			getManagedBean().setTotalContracts(
					getManagedBean().getScQtPRPExperiences().size());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * method to delete Non Federal Government Experience from the list
	 */
	public void deleteNonFederalGovtExperience() {
		debugLogger("deleteNonFederalGovtExperience");
		List<ScQtPRPExperience> experienceList = this.getManagedBean()
				.getScQtPRPExperiences();
		List<ScQtPRPExperience> selectedExperienceList = this.getManagedBean()
				.getSelectedPRPExperiences();
		List<ScQtPRPExperience> tobeDeletedExperiences = getManagedBean()
				.getDeletedPRPExperiences();
		if (selectedExperienceList == null
				|| selectedExperienceList.size() == 0) {
			throw new EpException(MessageConst.ERROR_760331);
		}

		if (experienceList != null && selectedExperienceList != null) {

			if (tobeDeletedExperiences == null) {
				tobeDeletedExperiences = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : selectedExperienceList) {

				if (scQtPRPExperience.getQtPrpExperienceId() != null
						&& scQtPRPExperience.getQtPrpExperienceId() != 0) {
					tobeDeletedExperiences.add(scQtPRPExperience);
				}
				// experienceList.remove(selectedExperienceList.get(i));
				Iterator<ScQtPRPExperience> itr = experienceList.iterator();
				while (itr.hasNext()) {
					ScQtPRPExperience localQtPRPExperience = itr.next();
					if (localQtPRPExperience.equals(scQtPRPExperience)) {
						itr.remove();
						break;
					}
				}
			}
		}

		getManagedBean().setDeletedPRPExperiences(tobeDeletedExperiences);
		getManagedBean().setScQtPRPExperiences(experienceList);
		getManagedBean().setTotalContracts(
				getManagedBean().getScQtPRPExperiences().size());
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedPRPExperiences(null);

		if (this.getManagedBean().isFinanicialOnlyCheckList()) {
			totalContractAmount();
		}

	}

	/**
	 * method to add Credit Facility to the list
	 */
	public void addCreditFacilities() {
		debugLogger("addCreditFacilities");
		ScQtPRPCreditFacility creditFacility = new ScQtPRPCreditFacility();
		try {
			List<ScQtPRPCreditFacility> creditFacilitiesList = this
					.getManagedBean().getScQtPRPCreditFacilities();
			if (null == creditFacilitiesList) {
				creditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
			}
			creditFacility.setCreditType(new PmParameter());
			creditFacility.setBrandNew(true);
			creditFacility.setScQtProposalChecklist(this.getManagedBean()
					.getQtProposalChecklist());
			for (ScQtPRPCreditFacility scQtPRPCreditFacility : creditFacilitiesList) {
				scQtPRPCreditFacility.setBrandNew(false);
			}

			creditFacilitiesList.add(creditFacility);
			this.getManagedBean().setScQtPRPCreditFacilities(
					creditFacilitiesList);
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * method to delete Credit Facility from the list
	 */
	public void deleteCreditFacilities() {
		debugLogger("deleteCreditFacilities");
		List<ScQtPRPCreditFacility> creditFacilitiesList = getManagedBean()
				.getScQtPRPCreditFacilities();
		List<ScQtPRPCreditFacility> selectedCreditFacilitiesList = this
				.getManagedBean().getSelectedPRPCreditFacilities();

		List<ScQtPRPCreditFacility> toDeleteCreditFacilitiesList = getManagedBean()
				.getToBeDeletedCreditFacilities();

		if (selectedCreditFacilitiesList == null
				|| selectedCreditFacilitiesList.size() == 0) {
			throw new EpException(MessageConst.ERROR_760331);
		}

		if (selectedCreditFacilitiesList != null
				&& creditFacilitiesList != null) {

			if (toDeleteCreditFacilitiesList == null) {

				toDeleteCreditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
			}

			for (ScQtPRPCreditFacility facility : selectedCreditFacilitiesList) {

				if (facility.getCreditFacilityId() != null
						&& facility.getCreditFacilityId() != 0) {
					toDeleteCreditFacilitiesList.add(facility);
				}

				Iterator<ScQtPRPCreditFacility> itr = creditFacilitiesList
						.iterator();
				while (itr.hasNext()) {
					ScQtPRPCreditFacility localQtPRPCreditFacility = itr.next();
					if (localQtPRPCreditFacility.equals(facility)) {
						itr.remove();
						break;
					}
				}

			}
			getManagedBean().setToBeDeletedCreditFacilities(
					toDeleteCreditFacilitiesList);
		}
		getManagedBean().setScQtPRPCreditFacilities(creditFacilitiesList);
		getManagedBean().setSelectedPRPCreditFacilities(null);
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		totalCreditFacilitiesSum();
	}

	/**
	 * method to add cash on hand to the list
	 */
	public void addCashOnHand() {
		debugLogger("addCashOnHand");
		ScQtPRPCashOnHand cashOnHand = new ScQtPRPCashOnHand();
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		if (null == cashOnHandList) {
			cashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
		}
		cashOnHand.setAccountType(new PmParameter());
		cashOnHand.setScQtProposalChecklist(this.getManagedBean()
				.getQtProposalChecklist());
		for (ScQtPRPCashOnHand hand : cashOnHandList) {
			hand.setBrandNew(false);
		}
		cashOnHandList.add(cashOnHand);
		this.getManagedBean().setScQtPRPCashOnHand(cashOnHandList);
	}

	/**
	 * method to delete cash on hand from the list
	 */
	public void deleteCashOnHand() {
		debugLogger("deleteCashOnHand");
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		List<ScQtPRPCashOnHand> selectedCashOnHandList = this.getManagedBean()
				.getSelectedPRPCashOnHand();
		List<ScQtPRPCashOnHand> toDeleteCashOnHandList = getManagedBean()
				.getToBeDeletedCashOnHandList();

		if (selectedCashOnHandList == null
				|| selectedCashOnHandList.size() == 0) {
			throw new EpException(MessageConst.ERROR_760331);
		}

		if (selectedCashOnHandList != null && cashOnHandList != null) {
			if (toDeleteCashOnHandList == null) {
				toDeleteCashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
			}

			for (ScQtPRPCashOnHand onHand : selectedCashOnHandList) {
				if (onHand.getPrpCashOnHandId() != null
						&& onHand.getPrpCashOnHandId() != 0) {
					toDeleteCashOnHandList.add(onHand);
				}

				Iterator<ScQtPRPCashOnHand> itr = cashOnHandList.iterator();
				while (itr.hasNext()) {
					ScQtPRPCashOnHand localQtPRPCashOnHand = itr.next();
					if (localQtPRPCashOnHand.equals(onHand)) {
						itr.remove();
						break;
					}
				}

				cashOnHandList.remove(onHand);
			}
			getManagedBean().setToBeDeletedCashOnHandList(
					toDeleteCashOnHandList);
		}
		getManagedBean().setScQtPRPCashOnHand(cashOnHandList);
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		getManagedBean().setSelectedPRPCashOnHand(null);

		totalCashOnHandSum();
	}

	/**
	 * action method to navigate previous page
	 * 
	 * @return navigation path
	 */
	public String supplierProposalBackAction() {
		debugLogger("supplierProposalBackAction");
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		getManagedBean().setDeletedPRPExperiences(null);
		getManagedBean().setToBeDeletedCashOnHandList(null);
		getManagedBean().setToBeDeletedCreditFacilities(null);
		return NAV_TO_PROPOSAL_MAIN_PAGE + "?faces-redirect=true";
	}

	/**
	 * listener method for zone tab change
	 */
	public void zoneTabChange() {

		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));

		getManagedBean().setActiveIndex(selectedZoneTabIndex);
		debugLogger("zoneTabChange");
		TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) getManagedBean()
				.getZones().get(selectedZoneTabIndex);
		log.info("Selected Zone : " + specZoneDtl.getZoneName());

		if (getManagedBean().getSelectedZone() != null) {
			getManagedBean().getZoneTplSpecBeanMap().put(
					getManagedBean().getSelectedZone().getSpecZoneId(),
					getManagedBean().getTemplateSpecBean());
			/*
			 * getManagedBean().setTemplateSpecBean(null);
			 * getManagedBean().setSelectedZone(null);
			 */
		}
		getManagedBean().setSelectedZone(specZoneDtl);
		getManagedBean().setTemplateSpecBean(
				getManagedBean().getZoneTplSpecBeanMap().remove(
						specZoneDtl.getSpecZoneId()));

		/*
		 * if (getManagedBean().getQt().getMaxProposal() == 1) {
		 * retrieveTemplateSpecBean(); }
		 */

	}

	/**
	 * method to calculate maximum proposals allowed
	 */
	public void displayProposalDtls() {
		debugLogger("displayProposalDtls");
		List<ScQtProposal> list = this.getManagedBean().getQtProposalsList();

		list = new ArrayList<ScQtProposal>();
		if (getManagedBean().getPrepareProposal() > getManagedBean().getQt()
				.getMaxProposal()) {
			throw new EpException(MessageConst.ERROR_760332, new String[]{getManagedBean().getQt().getMaxProposal()+""});
		} else {
			for (int i = 0; i < this.getManagedBean().getPrepareProposal(); i++) {
				ScQtProposal proposal = new ScQtProposal();
				list.add(proposal);
			}
		}

		this.getManagedBean().setQtProposalsList(list);
	}

	/**
	 * method to delete no of proposals from list
	 */
	public void deleteMultipleProposal() {
		try {
			List<ScQtProposal> multipleProposalScore = this.getManagedBean()
					.getQtProposalsList();
			List<ScQtProposal> selectProposalScore = this.getManagedBean()
					.getSelectMultipleProposalList();
			for (ScQtProposal proposalScore : selectProposalScore) {
				proposalScore.setDeleted(true);
				multipleProposalScore.remove(proposalScore);
			}
		} finally {

		}

		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		getManagedBean().setSelectMultipleProposalList(null);
	}

	/**
	 * method to redirect Supplier Proposal Screen..
	 */
	public void preRenderView() {
		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String qtId = originalServletRequest.getParameter(SourcingConstants.NAV_PARAM_QUOTATION_TENDER_ID);
			super.renewSesionObject();

			retrieveProposalChecklistDtls(Long.parseLong(qtId),
					((PmUser) (new FacesScopes().getUserSessionBean()))
							.getUserId());
			getManagedBean().setPrepareActiveIndex(0);

		} catch (EpException e) {
			String message = e.getMessage();

			if (MessageConst.ERROR_760195.equals(message)) {

				if (FacesContext.getCurrentInstance().getMessageList() != null
						&& FacesContext.getCurrentInstance().getMessageList()
								.size() > 0) {
					// no need to add the message again
				} else {
					Locale userLocale = new FacesScopes().getUserLocale();
					message = MessageUtils.getMessage(e.getMessage(),
							e.getParameters(),
							MessageUtils.BUNDLE_COMMON_MESSAGES, userLocale);
					getManagedBean().setMessage(message);

					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									message));
				}

			} else {
				handleException(e);
			}

		} catch (Exception e) {
			handleException(e);

			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication()
					.getNavigationHandler()
					.handleNavigation(fc, null,
							CommonUIConst.ACKNOWLEDGE_ERROR_PAGE);
		} finally {

		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_PROPOSAL_MAIN_PAGE);
	}

	private void loadDataFromCache() {

		retrieveProposalChecklistDtls(this.getManagedBean().getQt().getQtId(),
				((PmUser) (new FacesScopes().getUserSessionBean())).getUserId());
	}

	/**
	 * listener method for credit type change
	 * 
	 * @param creditFacility
	 */
	public void creditTypeChange(ScQtPRPCreditFacility creditFacility) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = creditFacility.getCreditType().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			creditFacility.setCreditType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

	}

	/**
	 * listener method for account type change
	 * 
	 * @param cashOnHand
	 */
	public void accountTypeChange(ScQtPRPCashOnHand cashOnHand) {
		IParameterSO parameterSO = null;
		try {
			Long parameterId = cashOnHand.getAccountType().getParameterId();

			parameterSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			cashOnHand.setAccountType(resDTO.getPmParameter());
		} finally {
			parameterSO = null;
		}

	}

	private String generateDeclarationFromVelocity(ScQt scQt) throws Exception {

		if (getManagedBean().getSmSupplier() == null
				|| getManagedBean().getSmSupplier().getCompanyName() == null) {
			retrievePaidUpCapitalNdDeclarationDtls();
		}

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();
		SmSupplier smSupplier = getManagedBean().getSmSupplier();
		VelocityContext context = new VelocityContext();
		if (scQt != null) {
			context.put("title", scQt.getQtTitle());
		}

		if (pmUser != null) {
			context.put("userName", pmUser.getUserName());
			context.put("icNo", pmUser.getIdentificationNo());
		}

		if (smSupplier != null) {
			context.put("supplierName", smSupplier.getCompanyName());
			if (smSupplier.getSmMofAccounts().get(0).getMofNo() != null
					&& !smSupplier.getSmMofAccounts().get(0).getMofNo()
							.isEmpty()) {
				context.put("mofNo", smSupplier.getSmMofAccounts().get(0)
						.getMofNo());
			} else if (smSupplier.getRegNo() != null) {
				context.put("mofNo", smSupplier.getRegNo());
			} else {
				context.put("mofNo", smSupplier.getEpNo());
			}
		}
		if (pmOrgProfile != null) {
			context.put("agency", pmOrgProfile.getOrgName());
		}

		String integrityPact = DocumentGenerator.getInstance()
				.getHtmlFromVelocity(
						SourcingConstants.SUPPLIER_PROPOSAL_INTEGRITY, context);

		getManagedBean().setIntegrityPact(integrityPact);

		return integrityPact;
	}

	private List<ScQtProposalScore> updateProposalScoresValue(
			ScQtProposalChecklist proposalChecklist,
			List<ScQtProposalScore> proposalScores,
			CriteriaValue... criteriaValues) {

		List<ScQtProposalScore> updateProposalScore = new ArrayList<ScQtProposalScore>();
		Map<Integer, ScQtProposalScore> proposalScoreMap = new HashMap<Integer, ScQtProposalScore>();

		for (ScQtProposalScore proposalScore : proposalScores) {
			proposalScoreMap
					.put(proposalScore.getCriteriaType(), proposalScore);
		}

		for (CriteriaValue criteriaValue : criteriaValues) {

			ScQtProposalScore proposalScore = proposalScoreMap
					.get(criteriaValue.getCriteriaType());

			if (proposalScore == null) {
				proposalScore = new ScQtProposalScore();
				proposalScore.setProposalChecklist(proposalChecklist);
				proposalScore.setCriteriaType(criteriaValue.getCriteriaType());
				setUserDetails(proposalScore);
			}

			proposalScore.setCriteriaValue(criteriaValue.getCriteriaValue());

			updateProposalScore.add(proposalScore);
		}

		return updateProposalScore;
	}

	private class CriteriaValue {
		private int criteriaType;
		private BigDecimal criteriaValue;

		public CriteriaValue(int criteriaType, BigDecimal criteriaValue) {
			this.criteriaType = criteriaType;
			this.criteriaValue = criteriaValue;
		}

		/**
		 * @return the criteriaType
		 */
		public int getCriteriaType() {
			return criteriaType;
		}

		/**
		 * @return the criteriaValue
		 */
		public BigDecimal getCriteriaValue() {
			return criteriaValue;
		}
	}

	private ScQtProposalScore findProposalScoreByCriteriaType(int criteriaType) {
		ScQtProposalScore scQtProposalScore = null;
		List<ScQtProposalScore> proposalScores = getManagedBean()
				.getScQtProposalScoreList();

		if (proposalScores != null) {
			for (ScQtProposalScore proposalScore : proposalScores) {
				if (criteriaType == proposalScore.getCriteriaType()) {
					scQtProposalScore = proposalScore;

					break;
				}
			}
		}

		return scQtProposalScore;
	}

	private String navigatePrepareTecProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist, boolean isCompleted) {
		updateQtTechProposalCheckList(currProposalChecklist,
				newProposalChecklist);

		prepareTechnicalAction(newProposalChecklist);

		if (isCompleted) {
			return NAV_TO_PROPOSAL_MAIN_PAGE;
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_100002,
									null, MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return null;
		}
	}

	private String navigatePrepareFinProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist, boolean isCompleted) {
		updateQtFinNdTechProposalCheckList(
				currProposalChecklist,
				newProposalChecklist,
				SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);

		prepareFinancialAction(newProposalChecklist);

		if (isCompleted) {
			return NAV_TO_PROPOSAL_MAIN_PAGE + "?faces-redirect=true";
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(MessageConst.INFO_100002,
									null, MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));

			return null;
		}
	}

	private void updateQtProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist) {
		if (SourcingConstants.TECHNICAL_DOCTYPE.equals(newProposalChecklist
				.getScQtCheckList().getDocCategory())) {
			updateQtTechProposalCheckList(currProposalChecklist,
					newProposalChecklist);
		} else {
			updateQtFinNdTechProposalCheckList(
					currProposalChecklist,
					newProposalChecklist,
					SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
		}
	}

	private void updateQtTechProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist) {
		int index = getManagedBean().getTecCheckDetailsList().indexOf(
				currProposalChecklist);
		if (index >= 0) {
			getManagedBean().getTecCheckDetailsList().set(index,
					newProposalChecklist);
		}
		getManagedBean().setQtProposalChecklist(newProposalChecklist);
	}

	private void updateQtFinNdTechProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist, String finOrTech) {
		if (finOrTech == SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV) {
			int index = getManagedBean().getFinanceCheckDetailsList().indexOf(
					currProposalChecklist);
			if (index >= 0) {
				getManagedBean().getFinanceCheckDetailsList().set(index,
						newProposalChecklist);
			}
		}
		if (finOrTech == SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV) {
			int index = getManagedBean().getTecCheckDetailsList().indexOf(
					currProposalChecklist);
			if (index >= 0) {
				getManagedBean().getTecCheckDetailsList().set(index,
						newProposalChecklist);
			}
		}
		getManagedBean().setQtProposalChecklist(newProposalChecklist);
	}

	/**
	 * listener method for row selection events
	 * 
	 * @param event
	 */
	public void onRowSelectionListener(SelectEvent event) {
		SmSupplierBank bank = (SmSupplierBank) event.getObject();
		getManagedBean().setSmChequeSignatories(bank.getSmChequeSignatories());

	}

	/**
	 * listener method for multi boolean check box selection events
	 */
	public void selectAll() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			List<ScQtPRPExperience> selectedExperiences = getManagedBean()
					.getSelectedPRPExperiences();
			if (selectedExperiences == null) {
				selectedExperiences = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : getManagedBean()
					.getScQtPRPExperiences()) {
				selectedExperiences.add(scQtPRPExperience);
			}
			getManagedBean().setSelectedPRPExperiences(selectedExperiences);
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectedPRPExperiences(null);

		}

	}

	/**
	 * Listener method for boolean check box selection events
	 * 
	 * @param index
	 */

	public void selected(int index) {

		List<ScQtPRPExperience> selectedExperienceList = getManagedBean()
				.getSelectedPRPExperiences();

		if (getManagedBean().isSelected()) {
			if (selectedExperienceList == null) {
				selectedExperienceList = new ArrayList<ScQtPRPExperience>();
			}
			selectedExperienceList.add(getManagedBean().getScQtPRPExperiences()
					.get(index));

		} else {

			if (selectedExperienceList.contains(getManagedBean()
					.getScQtPRPExperiences().get(index))) {
				selectedExperienceList.remove(getManagedBean()
						.getScQtPRPExperiences().get(index));
			}
		}
		getManagedBean().setSelectedPRPExperiences(selectedExperienceList);
	}

	/**
	 * Listener method for multi select boolean check box selection events
	 */
	public void selectAllCreditFacilities() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);

			List<ScQtPRPCreditFacility> selectedCreditFacilityList = getManagedBean()
					.getSelectedPRPCreditFacilities();
			List<ScQtPRPCreditFacility> creditFacilityList = getManagedBean()
					.getScQtPRPCreditFacilities();
			if (selectedCreditFacilityList == null) {
				selectedCreditFacilityList = new ArrayList<ScQtPRPCreditFacility>();
			}

			for (ScQtPRPCreditFacility creditFacility : creditFacilityList) {
				selectedCreditFacilityList.add(creditFacility);
			}

			getManagedBean().setSelectedPRPCreditFacilities(
					selectedCreditFacilityList);
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectedPRPCreditFacilities(null);

		}
	}

	/**
	 * Listener method for boolean check box selection events
	 * 
	 * @param index
	 */
	public void selectedCreditFacility(int index) {

		List<ScQtPRPCreditFacility> selectedCreditFacilityList = getManagedBean()
				.getSelectedPRPCreditFacilities();

		if (getManagedBean().isSelected()) {
			if (selectedCreditFacilityList == null) {
				selectedCreditFacilityList = new ArrayList<ScQtPRPCreditFacility>();
			}
			selectedCreditFacilityList.add(getManagedBean()
					.getScQtPRPCreditFacilities().get(index));

		} else {

			if (selectedCreditFacilityList.contains(getManagedBean()
					.getScQtPRPCreditFacilities().get(index))) {
				selectedCreditFacilityList.remove(getManagedBean()
						.getScQtPRPCreditFacilities().get(index));
			}
		}
		getManagedBean().setSelectedPRPCreditFacilities(
				selectedCreditFacilityList);
	}

	/**
	 * Listener method for boolean check box selection events
	 * 
	 * @param index
	 */
	public void selectedCashOnHand(int index) {

		List<ScQtPRPCashOnHand> selectedCashOnHandList = getManagedBean()
				.getSelectedPRPCashOnHand();

		if (getManagedBean().isSelected()) {
			if (selectedCashOnHandList == null) {
				selectedCashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
			}
			selectedCashOnHandList.add(getManagedBean().getScQtPRPCashOnHand()
					.get(index));

		} else {

			if (selectedCashOnHandList.contains(getManagedBean()
					.getScQtPRPCashOnHand().get(index))) {
				selectedCashOnHandList.remove(getManagedBean()
						.getScQtPRPCashOnHand().get(index));
			}
		}
		getManagedBean().setSelectedPRPCashOnHand(selectedCashOnHandList);
	}

	/**
	 * Listener method for multi select boolean check box selection events
	 */
	public void selectAllCashOnHand() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			List<ScQtPRPCashOnHand> selectedQtPRPCashOnHandList = getManagedBean()
					.getSelectedPRPCashOnHand();
			if (selectedQtPRPCashOnHandList == null) {
				selectedQtPRPCashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
			}

			for (ScQtPRPCashOnHand qtPRPCashOnHand : getManagedBean()
					.getScQtPRPCashOnHand()) {
				selectedQtPRPCashOnHandList.add(qtPRPCashOnHand);
			}

			getManagedBean().setSelectedPRPCashOnHand(
					selectedQtPRPCashOnHandList);
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectedPRPCashOnHand(null);

		}
	}

	/**
	 * Listener method for boolean check box selection events
	 * 
	 * @param index
	 */
	public void selectedMultipleProposal(int index) {

		List<ScQtProposal> selectedQtProposalList = getManagedBean()
				.getSelectMultipleProposalList();

		if (getManagedBean().isSelected()) {
			if (selectedQtProposalList == null) {
				selectedQtProposalList = new ArrayList<ScQtProposal>();
			}
			selectedQtProposalList.add(getManagedBean().getQtProposalsList()
					.get(index));

		} else {

			if (selectedQtProposalList.contains(getManagedBean()
					.getQtProposalsList().get(index))) {
				selectedQtProposalList.remove(getManagedBean()
						.getQtProposalsList().get(index));
			}
		}
		getManagedBean().setSelectMultipleProposalList(selectedQtProposalList);
	}

	/**
	 * Listener method for boolean check box selection events
	 */
	public void selectAllMultipleProposal() {

		if (getManagedBean().isSelectedAll()) {
			getManagedBean().setSelected(true);
			getManagedBean().setSelectMultipleProposalList(
					getManagedBean().getQtProposalsList());
		} else {
			getManagedBean().setSelected(false);
			getManagedBean().setSelectMultipleProposalList(null);

		}
	}

	/**
	 * method to validae credit facilities
	 * 
	 * @param index
	 */
	public void creditFacilityValidator(int index) {

		ScQtPRPCreditFacility validateFacility = getManagedBean()
				.getScQtPRPCreditFacilities().get(index);

		if (validateFacility.getAmtPaid().doubleValue() > validateFacility
				.getCreditAmt().doubleValue()) {
			validateFacility.setAmtPaid(new BigDecimal(0));
			getManagedBean().getScQtPRPCreditFacilities().set(index,
					validateFacility);
			throw new EpException(MessageConst.ERROR_760333);
		}

		totalCreditFacilitiesSum();

	}

	private void validateContractNo() {

		List<String> loaOrContractNoList = new ArrayList<String>();

		List<ScQtPRPExperience> experiences = this.getManagedBean()
				.getScQtPRPExperiences();
		if (null != experiences) {

			for (ScQtPRPExperience experience : experiences) {
				String loaOrContractNo = experience.getContractNo();
				if (loaOrContractNoList.contains(loaOrContractNo)) {
					throw new EpException(MessageConst.ERROR_760334);
				} else {
					loaOrContractNoList.add(loaOrContractNo);
				}
			}
		}

	}

	/**
	 * action method for label print
	 * 
	 * @throws Exception
	 */
	public void printLabelAction() throws Exception {

		ScSlAttachment slAttachment = new ScSlAttachment();
		slAttachment.setDocNo(getManagedBean().getQt().getQtNo());
		generateSampleLabelsFromVelocityToPdf(slAttachment);
		StreamedContent slStreamContent = getAttachmentStreamContent(
				slAttachment, CommonConstants.FILE_CONTENT_TYPE_PDF);
		getManagedBean().setSampleLabelStreamContent(slStreamContent);
		RequestContext.getCurrentInstance().execute(
				"supplierSampleLabelDlg.show();");
	}

	private void generateSampleLabelsFromVelocityToPdf(ScAttachment attachment)
			throws Exception {
		attachment.setFileName("sample Labels.pdf");
		populateAttachmentDetails(attachment);
		// CommonUtils commonUtils = new CommonUtils();

		VelocityContext context = new VelocityContext();

		context.put("sampleItems", getManagedBean().getScQtSampleLable()
				.getSampleItems());
		context.put("lableNo", getManagedBean().getScQtSampleLable()
				.getLableNo());
		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.SUPPLIER_PROPOSAL_PRINT_LABEL, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	private void populateFinancialSpecDetailsByZone() {

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getZones())) {
			List<TplSpecZoneDtl> zoneDtls = this.getManagedBean().getZones();

			// Dynamically generating the tabs
			MenuModel zoneMenuModel = new DefaultMenuModel();
			if (CollectionUtils.isNotEmpty(zoneDtls)) {
				int zoneTabIndex = 0;
				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
					specZoneDtl.setZoneTabIndex(zoneTabIndex);

					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName().toUpperCase());
					zoneItem.setAjax(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setOncomplete("zoneTabChange([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);
					++zoneTabIndex;
				}
			}
			this.getManagedBean().setProposalZonalMenuModel(zoneMenuModel);
		}
	}

	/**
	 * Method is used to download the document from repository on click of
	 * download button.
	 * 
	 * @param scQtDocReport
	 *            the instance of ScQtDocReport
	 * @throws Exception
	 */
	public void downloadReport(ScQtDocReport scQtDocReport) throws Exception {

		log.info("** Qtn / TenderDetsils **");
		ScQt scQt = this.getManagedBean().getQt();
		QTDocumentsBackingBean qtDocumentsBackingBean = new QTDocumentsBackingBean();
		qtDocumentsBackingBean.getManagedBean().setQt(null);
		qtDocumentsBackingBean.getManagedBean().setQt(scQt);
		ScQtDocReportAttachment scQtDocReportAttachment = scQtDocReport
				.getScQtDocReportAttachment();
		if (scQtDocReportAttachment == null) {
			scQtDocReportAttachment = qtDocumentsBackingBean
					.generateDocumentAttachment(scQtDocReport);
			scQtDocReport.setScQtDocReportAttachment(scQtDocReportAttachment);
		}
		downloadFile(scQtDocReportAttachment);
	}

	/**
	 * Method is used to download all documents from repository on click of
	 * 'Download all' button.
	 * 
	 * @param qtId
	 *            the Long variable.
	 * @throws Exception
	 */
	public void downloadAllQTDocuments() throws Exception {

		log.info("** Qtn / TenderDetsils **");
		ScQt scQt = this.getManagedBean().getQt();
		QTDocumentsBackingBean qtDocumentsBackingBean = new QTDocumentsBackingBean();
		qtDocumentsBackingBean.getManagedBean().setQt(null);
		qtDocumentsBackingBean.getManagedBean().setQt(scQt);
		List<ScQtDocReport> scQtDocReports = getManagedBean()
				.getScQtDocReports();

		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		if (null != scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport scQtDocReport : scQtDocReports) {
				ScQtDocReportAttachment attachment = scQtDocReport
						.getScQtDocReportAttachment();
				if (attachment == null) {
					attachment = qtDocumentsBackingBean
							.generateDocumentAttachment(scQtDocReport);
					scQtDocReport.setScQtDocReportAttachment(attachment);
				}
				scAttachments.add(attachment);
			}
			downloadAllFiles("NGeP-QT-Documents", scAttachments);
		}
	}

	/**
	 * Spki sign action.
	 */
	public void spkiSignAction() {
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");
	}

}
