package my.ep.web.backingbean.sc.supplierproposal;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
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
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperienceNCheckList;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.util.BouncyCastleProvider_AES_CBC;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pp.common.CommonMethod;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "supplierProposalDetailBackingBean")
@RequestScoped
public class SPDetailBackingBean extends
		SourcingBaseBackingBean<SPDetailManagedBean> {

	// Technical Proposal
	private static final String NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/technicalSpecification.xhtml";
	private static final String NAV_TO_NON_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalNonFederalGovt.xhtml";
	private static final String NAV_TO_FEDERAL_GOVT_PAGE = "/xhtml/sc/supplierproposal/technicalFederalGovt.xhtml";
	private static final String NAV_TO_TECHNICAL_EMPL_LIST_PAGE = "/xhtml/sc/supplierproposal/technicalEmployeeList.xhtml";
	private static final String NAV_TO_NO_OF_EMPLOYEE__PAGE = "/xhtml/sc/supplierproposal/technicalNoOfEmployee.xhtml";
	private static final String NAV_TO_SAMPLING_ADDRESS_PAGE = "/xhtml/sc/supplierproposal/technicalSamplingAddress.xhtml";

	// Financial Proposal
	private static final String NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/financialSpecification.xhtml";

	private static final String NAV_TO_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialSpecMoreProposal.xhtml";
	private static final String NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialMoreZone.xhtml";

	private static final String NAV_TO_SUPPLIER_DETAILS_PAGE = "/xhtml/sc/supplierproposal/financialSupplierDetails.xhtml";
	private static final String NAV_TO_CREDIT_FACILITIES_PAGE = "/xhtml/sc/supplierproposal/financialCreditFacilities.xhtml";
	private static final String NAV_TO_CASH_ON_HAND_PAGES = "/xhtml/sc/supplierproposal/financialCashOnHand.xhtml";
	private static final String NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialFederalGovt.xhtml";
	private static final String NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialNonFederalGovt.xhtml";
	private static final String NAV_TO_FINANCE_BANK_STATEMENT_PAGE = "/xhtml/sc/supplierproposal/financialBankStatement.xhtml";
	private static final String NAV_TO_ASSET_LIABILITY_PAGE = "/xhtml/sc/supplierproposal/financialAssetLiability.xhtml";
	private static final String NAV_TO_PAID_UP_CAPITAL_PAGE = "/xhtml/sc/supplierproposal/financialPaidUpCapital.xhtml";
	private static final String NAV_TO_PROPOSAL_MAIN_PAGE = "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml";
	private static final String NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE = "/xhtml/sc/supplierproposal/financialchecklistsmain.xhtml";
	private static final String NAV_TO_TECHNICAL_CHECKLIST_MAIN_PAGE = "/xhtml/sc/supplierproposal/techinicalChecklistmain.xhtml";

	private static final SimpleDateFormat BANK_STATEMENT_MONTH_FORMAT = new SimpleDateFormat(
			"MMMM yyyy");

	public static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	public SPDetailBackingBean() {
		log.info("inside constructor");
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	/**
	 * retrieving Technical & Financial Proposal CheckList
	 */
	public void retrieveProposalChecklistDtls(Long qtID, String supplierID,
			Long userId) {
		debugLogger("retrieveProposalChecklistDtls");

		getManagedBean().setPrepareActiveIndex(0);
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtId(qtID);
			reqDTO.setUserId(userId);
			reqDTO.setHashSupplierId(supplierID);

			ProposalResDTO resDTO = proposalSO
					.retrieveProposalDtlBySupplierId(reqDTO);
			getManagedBean().setQt(resDTO.getScQt());
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

			if (null != getManagedBean().getQt().getClosingDate()) {
				Date today = new Date();
				long days = DateUtils.getDaysDifference(today, getManagedBean()
						.getQt().getClosingDate());
				this.getManagedBean().setDays(days);
			}

		} finally {
			proposalSO = null;
		}
	}

	public void retrieveTechnicalProposalList() {
		debugLogger("retrieveTechnicalProposalList");
		this.getManagedBean().setPrepareActiveIndex(0);
	}

	public void retrieveFinancialProposalList() {
		debugLogger("retrieveFinancialProposalList");
		this.getManagedBean().setPrepareActiveIndex(1);
	}

	public void retrieveDeclarationList() throws Exception {
		debugLogger("retrieveDeclarationList");
		this.getManagedBean().setPrepareActiveIndex(2);

		generateDeclarationFromVelocity(getManagedBean().getQt());
	}

	/**
	 * Method to save the Supplier Proposal details
	 */
	public void submitSupplierProposal() {
		debugLogger("submitSupplierProposal");

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();
			List<ScQtProposalChecklist> technicalChecklist = getManagedBean()
					.getTecCheckDetailsList();
			List<ScQtProposalChecklist> financialChecklist = getManagedBean()
					.getFinanceCheckDetailsList();

			ScQtProposal scQtProposal = getManagedBean().getQtProposal();
			setUserDetails(scQtProposal);
			scQtProposal.setSubmitted(true);

			scQtProposalChecklists.addAll(technicalChecklist);
			scQtProposalChecklists.addAll(financialChecklist);
			scQtProposal
					.setScQtProposalChecklists(new ArrayList<ScQtProposalChecklist>(
							scQtProposalChecklists));

			ProposalReqDTO reqDTO = new ProposalReqDTO();
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

			ProposalResDTO resDTO = proposalSO.saveQTDtl(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = CommonMethod
						.getMessageCommon("lbl.sc.sp.pplno");
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
	 * Will be called for financial non government company experiance..
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

			CriteriaValue contractAmountVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV,
					this.getManagedBean().getTotalContractAmount());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), contractAmountVal);

			List<ScQtPRPExperienceNCheckList> experienceNCheckLists = this
					.getManagedBean().getScQtPRPExperienceNCheckLists();

			scQtProposalChecklist.setSubmitted(isCompleted);

			if (experienceNCheckLists == null
					|| experienceNCheckLists.size() == 0) {
				List<ScQtPRPExperience> experiences = this.getManagedBean()
						.getScQtPRPExperiences();

				if (experiences != null) {
					experienceNCheckLists = new ArrayList<ScQtPRPExperienceNCheckList>();

					for (ScQtPRPExperience prpExperience : experiences) {
						ScQtPRPExperienceNCheckList checkList = new ScQtPRPExperienceNCheckList();
						checkList
								.setScQtProposalChecklist(scQtProposalChecklist);
						checkList.setScQtPRPExperience(prpExperience);
						experienceNCheckLists.add(checkList);
					}
				}
			}

			scQtProposalChecklist
					.setExperienceNCheckLists(experienceNCheckLists);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			// to delete the deleted experiences from db
			reqDTO.setTobeRemovedExperience(getManagedBean()
					.getDeletedPRPExperiences());

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setDeletedPRPExperiences(null);
				debugLogger("*****save successfull*****"
						+ resDTO.isSuccessful());

				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	public String saveTechnicalSpecificationAction(boolean isCompleted) {
		debugLogger("saveTechnicalSpecificationAction");

		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getQtProposalChecklist();

		scQtProposalChecklist.setSubmitted(isCompleted);

		ProposalResDTO proposalResDTO = saveTempleateSpecDtls();

		if (proposalResDTO.isSuccessful()) {
			return navigatePrepareTecProposalCheckList(scQtProposalChecklist,
					proposalResDTO.getProposalChecklist(), isCompleted);
		}

		return null;
	}

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
			List<TplSpecAnswer> answers = getManagedBean()
					.getTemplateSpecBean().getSpecAnswerList();

			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

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
					this.getManagedBean().getTotalAmountCredit());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalCreditVal);

			scQtProposalChecklist.setSubmitted(isCompleted);

			scQtProposalChecklist.setScQtPRPCreditFacilities(this
					.getManagedBean().getScQtPRPCreditFacilities());
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobeRemovedCreditFacilities(getManagedBean()
					.getToBeDeletedCreditFacilities());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setToBeDeletedCreditFacilities(null);

				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

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

			scQtProposalChecklist.setScQtPRPCashOnHands(this.getManagedBean()
					.getScQtPRPCashOnHand());
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobecashOnHand(getManagedBean()
					.getToBeDeletedCashOnHandList());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setToBeDeletedCashOnHandList(null);

				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

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

			scQtProposalChecklist.setPrpBankStmts(this.getManagedBean()
					.getScQtPRPBankStmts());
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

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
			scQtProposalChecklist.setScQtPRPEmployee(getManagedBean()
					.getScQtPRPEmployee());

			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} finally {
			proposalSO = null;
		}

		return null;
	}

	public String saveTechNonGovCompanyExpAction(boolean isCompleted) {
		debugLogger("saveTechNonGovCompanyExpAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<ScQtPRPExperience> experiences = this.getManagedBean()
					.getScQtPRPExperiences();

			scQtProposalChecklist.setSubmitted(isCompleted);

			// initial save list is empty
			List<ScQtPRPExperienceNCheckList> experienceNCheckList = new ArrayList<ScQtPRPExperienceNCheckList>();
			// SC_QT_PRP_EXPERIENCE_LINK = ScQtPRPExperienceNCheckList

			for (ScQtPRPExperience prpExperience : experiences) {
				// previously saved records ignored
				if (prpExperience.getQtPrpExperienceId() == null
						|| prpExperience.getQtPrpExperienceId() == 0) {
					ScQtPRPExperienceNCheckList checkList = new ScQtPRPExperienceNCheckList();
					checkList.setScQtProposalChecklist(scQtProposalChecklist);
					checkList.setScQtPRPExperience(prpExperience);
					experienceNCheckList.add(checkList);
				}
			}

			MathContext mc = new MathContext(4);
			CriteriaValue totalContractsVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV,
					BigDecimal.valueOf(getManagedBean().getTotalContracts()));
			CriteriaValue totalYearsVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACT_YEARS_FOR_NON_FED_GOV,
					BigDecimal.valueOf(getManagedBean().getTotalYears())
							.divide(BigDecimal.valueOf(12), mc));

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalContractsVal,
					totalYearsVal);

			scQtProposalChecklist
					.setExperienceNCheckLists(experienceNCheckList);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			// to delete the deleted experiences from db
			reqDTO.setTobeRemovedExperience(getManagedBean()
					.getDeletedPRPExperiences());

			ScQtProposalChecklist finProposalChecklist = findProposalChecklistByDocDefKey(
					getManagedBean().getFinanceCheckDetailsList(),
					SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);

			reqDTO.setTobeUpdateProposalChecklist(finProposalChecklist);

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setDeletedPRPExperiences(null);

				updateQtFinProposalCheckList(finProposalChecklist,
						resDTO.getTobeUpdateProposalChecklist());

				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

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

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), acidRatioVal);

			scQtProposalChecklist.setSubmitted(isCompleted);

			scQtProposalChecklist.setPrpAssetLiabilities(scQtPRPAssetLiability);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

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

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			return navigatePrepareFinProposalCheckList(scQtProposalChecklist,
					resDTO.getProposalChecklist(), isCompleted);
		} finally {
			proposalSO = null;
		}
	}

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

	protected void uploadAttachmentWithEncryption(UploadedFile file,
			ScAttachment attachment) throws FileNotFoundException, IOException {
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
		try {
			bc.InitCiphers();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// encryption
		try {
			bc.CBCEncrypt(fis, fos);
		} catch (ShortBufferException e) {
			log.debug(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);

		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void downloadPTJFile(ScQtProposalChecklist scQtProposalChecklist)
			throws IOException {
		debugLogger("downloadPTJFile");

		downloadFile(scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment());
	}

	public void downloadEncryptedFile(String filePath, String fileName)
			throws IOException {

		if (filePath != null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream());

			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC();
			try {
				bc.InitCiphers();
			} catch (InvalidKeyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchProviderException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchPaddingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvalidAlgorithmParameterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("Test 1 !" + bc.generateKey());
			// encryption
			// bc.CBCEncrypt(fis, fos);

			FileOutputStream fos = new FileOutputStream(new File(
					"C:\\Windows\\Temp\\PV.pdf"));

			// decryption
			try {
				bc.CBCDecrypt(input, fos);
			} catch (ShortBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// byte[] buffer = new byte[4096];
			// int bytesRead;
			// while ((bytesRead = input.read(buffer)) != -1) {
			// out.write(buffer, 0,ytesRead);
			// buffer = new byte[4096];
			// }
			input.close();
			fos.flush();
			fos.close();
			FileInputStream input1 = new FileInputStream(new File(
					"C:\\Windows\\Temp\\PV.pdf"));
			downloadFileByInputStream(input1, fileName);
		}
		//
		// // use to upload back to database
		// File file = new File(filePath);
		// return file;
	}

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

	public String createProposal() {

		return null;
	}

	public String updateBpmTask(String action) {

		ProposalReqDTO prReqDTO = constructReqDTO();
		prReqDTO.setOutcome(action);
		prReqDTO.setTaskId(this.getManagedBean().getTaskId());
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		prReqDTO.setToken(ctx);
		prReqDTO.setPayLoad(this.getManagedBean().getProposalData());
		IQTProposalSO proSo = getProposalSO();
		try {
			ProposalResDTO resDTO = proSo.updateBPMAction(prReqDTO);
			if (resDTO.isSuccessful()) {

				// to nullify personnel tab SmPersonnleCalcVO object
				getManagedBean().setPersonCalc(null);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", " Type. : "

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sorry We are unable to Process", " Type. : "

					));
		}
		return null;

	}

	private IQTProposalSO getProposalSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
	}

	private ProposalReqDTO constructReqDTO() {
		List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();
		ScQtProposal scQtProposal = getManagedBean().getQtProposal();
		List<ScQtProposalChecklist> tecQtProposalChecklists = getManagedBean()
				.getTecCheckDetailsList();
		scQtProposalChecklists.addAll(tecQtProposalChecklists);
		tecQtProposalChecklists = getManagedBean().getFinanceCheckDetailsList();
		scQtProposalChecklists.addAll(tecQtProposalChecklists);
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setQtSupplierId(1L);
		// Update Changes
		for (ScQtProposalChecklist proposalChecklist : scQtProposalChecklists) {
			proposalChecklist.setSubmitted(true);
		}
		scQtProposal.setScQtProposalChecklists(scQtProposalChecklists);
		reqDTO.setScQtProposal(scQtProposal);
		return reqDTO;
	}

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

		templateSpecBackingBean.retrieveTemplateSpecMaster(templateSpecBean);

		return NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE;
	}

	private String retrieveTecNonGovCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setDocDefinationKey(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey());
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

			int totalYears = 0;
			if (contractYearsScore != null
					&& contractYearsScore.getCriteriaValue() != null) {
				totalYears = contractYearsScore.getCriteriaValue()
						.multiply(BigDecimal.valueOf(12)).intValue();
			}
			this.getManagedBean().setTotalYears(totalYears);

			return NAV_TO_NON_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}

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
			ProposalResDTO resDTO = proposalSO.retrieveEmployeeDtls(reqDTO);

			ScQtPRPEmployee scQtPRPEmployee = resDTO.getScQtPRPEmployee();

			if (resDTO.getScQtPRPEmployee() == null) {
				scQtPRPEmployee = new ScQtPRPEmployee();
				scQtPRPEmployee.setScQtProposalChecklist(scQtProposalChecklist);
			}

			if (scQtCheckListValue != null
					&& scQtCheckListValue.isAllEmployee()) {
				retrieveSupplierDtls(scQtProposalChecklist);

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

	/*public String prepareTechnicalViewAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalViewAction");

		String definationType = scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode();

		getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
		getManagedBean().setScQtCheckList(
				scQtProposalChecklist.getScQtCheckList());

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE
				.equalsIgnoreCase(definationType)) {
			return retrieveSampleDetails(scQtProposalChecklist);

		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED
				.equalsIgnoreCase(definationType)) {
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();

			if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV
					.equalsIgnoreCase(docDefinitionKey)) {
				return retrieveTecCompanyExpDtls(scQtProposalChecklist);

			} // to check proposalchecklist is technicalspecification -
				// technical EMPL_LIST_PAGE
			else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_STAFF_LIST
					.equalsIgnoreCase(docDefinitionKey)) {
				return retriveTechnicalStaffDtls(scQtProposalChecklist);

			}
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM
				.equalsIgnoreCase(definationType)) {
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();

			if (SourcingConstants.DOC_DEF_KEY_NO_OF_EMPLOYEES
					.equalsIgnoreCase(docDefinitionKey)) {

				return retrieveEmployeeDtls(scQtProposalChecklist);

			}
		}

		return null;
	}*/

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

	private String retriveTechnicalStaffDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		List<TechnicalStaffBean> smPersonnelsList = new ArrayList<TechnicalStaffBean>();

		retrieveSupplierDtls(scQtProposalChecklist);

		// Contruct qualification map
		Map<Long, PmParameterDesc> qualificationMap = new HashMap<Long, PmParameterDesc>();
		for (PmParameterDesc parameterDesc : ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.QUALIFICATION_LEVEL)) {

			qualificationMap.put(parameterDesc.getPmParameter()
					.getParameterId(), parameterDesc);
		}

		int totalYearsExp = 0;
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

			totalYearsExp += smPersonnel.getWorkExperienceYear();
		}
		getManagedBean().setSmPersonnelList(smPersonnelsList);
		getManagedBean().setTotalNoOfYearsExperience(totalYearsExp);
		log.info("Total No of Years Experience : " + totalYearsExp);

		return NAV_TO_TECHNICAL_EMPL_LIST_PAGE;
	}

	private String retrieveTecCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist
					.getQtProposalChecklistId());

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

				// ScQtPRPExperience experience = (ScQtPRPExperience)
				// experiencesList.get(i);
			}
			this.getManagedBean().setNoOfYears(total);
			this.getManagedBean().setContractSize(list.size());

			return NAV_TO_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	public String prepareMultileProposalAction(int index) {
		debugLogger("prepareMultileProposalAction");
		getManagedBean().setProposalIndex(index);
		System.out.println("index : "
				+ this.getManagedBean().getProposalIndex());
		ScQtCheckList scQtCheckList = this.getManagedBean()
				.getQtProposalChecklist().getScQtCheckList();
		this.getManagedBean().setScQtCheckList(scQtCheckList);
		ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();
		this.getManagedBean().setScQtDoc(scQtDoc);
		try {
			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
			templateSpecBean.setSpecMaster(specMaster);
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
			templateSpecBean.setSupplier(this.getManagedBean()
					.getQtProposalChecklist().getScQtProposal()
					.getScQtSupplier().getSmSupplier());
			templateSpecBackingBean
					.retrieveTemplateSpecMaster(templateSpecBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE;

	}

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

				pageUrl = retrieveSupplierDtls(scQtProposalChecklist);

			} else if (SourcingConstants.DOC_DEF_KEY_PAID_UP_CAPITAL
					.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrievePaidUpCapitalDtls(scQtProposalChecklist);
			}
		}
		this.getManagedBean().setPageUrl(pageUrl);
		return NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";
	}

	private String retrieveFinancialSpecDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		String redirectPage = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ScQt scQt = getManagedBean().getQt();
			ScQtDoc scQtDoc = scQtProposalChecklist.getScQtCheckList()
					.getScQtDoc();
			this.getManagedBean().setScQtDoc(scQtDoc);

			TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
			TemplateSpecBean templateSpecBean = getManagedBean()
					.getTemplateSpecBean();
			TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

			templateSpecBean.setSpecMaster(specMaster);
			templateSpecBean
					.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

			SmSupplier smSupplier = new SmSupplier();
			smSupplier.setSupplierId(getManagedBean().getSupplierId());
			templateSpecBean.setSupplier(smSupplier);

			templateSpecBackingBean
					.retrieveTemplateSpecMaster(templateSpecBean);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtId(scQt.getQtId());

			// To retrieve financial spec details
			ProposalResDTO resDTO = proposalSO
					.retrieveFinancialSpecDtls(reqDTO);

			if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zones = resDTO.getZones();
				if (null != zones) {
					this.getManagedBean().setZones(zones);
					this.getManagedBean().setSelectedZone(zones.get(0));
				}
				if (scQt.getMaxProposal() > 1) {

					redirectPage = NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE;
				} else {
					redirectPage = NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE;

				}
			} else {
				if (scQt.getMaxProposal() == 1) {

					redirectPage = NAV_TO_MORE_THAN_PROPOSAL_PAGE;

				}
			}
			return redirectPage;
		} finally {
			proposalSO = null;
		}
	}

	public String retrieveFinNonGovCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			List<ScQtProposalChecklist> techinicalProposalCheckLists = this
					.getManagedBean().getTecCheckDetailsList();

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setDocDefinationKey(scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey());
			ProposalResDTO resDTO = proposalSO
					.retrieveNonGovCompanyExpDtls(reqDTO);

			List<ScQtPRPExperienceNCheckList> prpExperienceNCheckLists = resDTO
					.getPrpExperienceNCheckLists();
			List<ScQtPRPExperience> scQtPRPExperiences = resDTO
					.getPrpExperience();

			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());
			this.getManagedBean().setScQtPRPExperienceNCheckLists(
					prpExperienceNCheckLists);

			if (scQtPRPExperiences == null || scQtPRPExperiences.size() == 0) {
				ScQtProposalChecklist tecProposalCheckList = findProposalChecklistByDocDefKey(
						techinicalProposalCheckLists,
						SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);

				if (null != tecProposalCheckList
						&& tecProposalCheckList.isSubmitted()) {
					reqDTO.setScQtProposalChecklist(tecProposalCheckList);
					reqDTO.setDocDefinationKey(tecProposalCheckList
							.getScQtCheckList().getPmDocument()
							.getDocDefinitionKey());
					resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);
					scQtPRPExperiences = resDTO.getPrpExperience();
				}
			}
			this.getManagedBean().setScQtPRPExperiences(scQtPRPExperiences);

			ScQtProposalScore contractAmountScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV);

			if (contractAmountScore != null) {
				this.getManagedBean().setTotalContractAmount(
						contractAmountScore.getCriteriaValue());
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

	public String retrieveCreditFacilitiesDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
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
				this.getManagedBean().setTotalAmountCredit(
						creditFacilitiesScore.getCriteriaValue());
			}

			return NAV_TO_CREDIT_FACILITIES_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	public String retrieveCashOnHandDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
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

	public String retrieveBankStmtDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
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

	public String retrieveAssetLiabilityDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
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

			ScQtProposalScore assetLiabilityScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);

			if (assetLiabilityScore != null) {
				getManagedBean().getScQtPRPAssetLiability().setAcidRatio(
						assetLiabilityScore.getCriteriaValue());
			}

			return NAV_TO_ASSET_LIABILITY_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	public String retrievePaidUpCapitalDtls(
			ScQtProposalChecklist scQtProposalChecklist) {

		retrieveSupplierDtls(scQtProposalChecklist);

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
	 * // what's happening here // TODO - check still valid or not if
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
	public String retrieveFinCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist
					.getQtProposalChecklistId());

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

	private String retrieveSupplierDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		long supplierId = getManagedBean().getSupplierId();
		log.info("Supplier Id : " + supplierId);

		SmSupplier smSupplier = getManagedBean().getSmSupplier();

		if (smSupplier == null
				|| (smSupplier.getSupplierId() != null && smSupplier
						.getSupplierId().longValue() != supplierId)) {

			IQTProposalSO proposalSO = null;
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				reqDTO.setSupplierId(supplierId);
				ProposalResDTO resDTO = proposalSO.retriveSupplierById(reqDTO);
				getManagedBean().setSmSupplier(resDTO.getSmSupplier());
				getManagedBean().setSmAppl(resDTO.getSmAppl());
				log.info("*****SmAppl Appl_Id : *****"
						+ resDTO.getSmAppl().getApplId());
				// equityShareHolders for
				List<SmPersonnel> equityShareHolders = new ArrayList<SmPersonnel>();

				for (SmPersonnel personnel : resDTO.getSmAppl()
						.getSmPersonnels()) {
					if (personnel.isEquityOwner()) {
						equityShareHolders.add(personnel);

					}
				}
				getManagedBean().setEquityShareHolders(equityShareHolders);

				// to set data for category codes

				if (resDTO.getSmAppl().getSmSupplierCategories() != null
						&& resDTO.getSmAppl().getSmSupplierCategories().size() != 0) {
					for (SmSupplierCategory category : resDTO.getSmAppl()
							.getSmSupplierCategories()) {

					}

				}

				// To fetch data for personnel tab using the new static method

				if (getManagedBean().getSmAppl().getSmPersonnels() != null) {
					SmPersonnleCalcVO personnelVO = SmCommonUtil
							.calcPersonnelListCount(getManagedBean()
									.getSmAppl().getSmPersonnels(),
									getManagedBean().getSmAppl()
											.getSmStaffTotals(),
									getManagedBean().getSmSupplier()
											.getBusinessType());
					getManagedBean().setPersonCalc(personnelVO);

				}

				// To set data for bankinfo tab
				getManagedBean().setSmChequeSignatories(
						getManagedBean().getSmAppl().getSmSupplierBanks()
								.get(0).getSmChequeSignatories());

				// staffInformation();
				log.info("Paid up capital : "
						+ getManagedBean().getSmAppl().getSmCompanyBasic()
								.getPaidUpCapital());
				log.info("Authorized capital : "
						+ getManagedBean().getSmAppl().getSmCompanyBasic()
								.getAuthorizedCapital());
				log.info("Account Number : "
						+ getManagedBean().getSmAppl().getSmSupplierBanks()
								.get(0).getAccountNo());

			} finally {
				proposalSO = null;
			}
		}

		return NAV_TO_SUPPLIER_DETAILS_PAGE;
	}

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

	public void effectiveDateSelectListener(ScQtPRPExperience prpExperience) {

		getManagedBean().setNonFedMinDate(prpExperience.getEffectiveDate());
		if (prpExperience.getEffectiveDate() != null
				&& prpExperience.getExpiryDate() != null) {
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(prpExperience.getEffectiveDate());
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(prpExperience.getExpiryDate());
			int diffYear = endCalendar.get(Calendar.YEAR)
					- startCalendar.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH)
					- startCalendar.get(Calendar.MONTH);
			prpExperience.setContractPeriod(diffMonth);

		}
		int index = getManagedBean().getScQtPRPExperiences().indexOf(
				prpExperience);
		getManagedBean().getScQtPRPExperiences().set(index, prpExperience);

		System.out.println("*****experience in months is *****"
				+ getManagedBean().getScQtPRPExperiences().get(index)
						.getContractPeriod());
		totalContractYears();
	}

	public void expiryDateSelectListener(int index) {
		ScQtPRPExperience prpExperience = getManagedBean()
				.getScQtPRPExperiences().get(index);
		getManagedBean().setNonFedEffMaxDate(prpExperience.getExpiryDate());

		if (prpExperience.getEffectiveDate() != null
				&& prpExperience.getExpiryDate() != null) {
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(prpExperience.getEffectiveDate());
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(prpExperience.getExpiryDate());
			int diffYear = endCalendar.get(Calendar.YEAR)
					- startCalendar.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH)
					- startCalendar.get(Calendar.MONTH);
			prpExperience.setContractPeriod(diffMonth);

		}
		getManagedBean().getScQtPRPExperiences().set(index, prpExperience);
		System.out.println("*****experience in months is *****"
				+ getManagedBean().getScQtPRPExperiences().get(index)
						.getContractPeriod());
		totalContractYears();
	}

	public void totalContractYears() {
		debugLogger("totalContractYears");

		int totalYears = 0;
		List<ScQtPRPExperience> experiencesList = this.getManagedBean()
				.getScQtPRPExperiences();
		if (experiencesList == null) {
			experiencesList = new ArrayList<ScQtPRPExperience>();
		}
		for (int i = 0; i < experiencesList.size(); i++) {
			ScQtPRPExperience experience = (ScQtPRPExperience) experiencesList
					.get(i);
			totalYears = totalYears + experience.getContractPeriod();
		}

		this.getManagedBean().setTotalYears(totalYears);
	}

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

	public void totalAcidRatio() {
		debugLogger("totalAcidRatio");

		ScQtPRPAssetLiability assetLiability = this.getManagedBean()
				.getScQtPRPAssetLiability();
		BigDecimal asset = assetLiability.getCurrentAssets();
		BigDecimal liabilities = assetLiability.getCurrentLiabilities();
		BigDecimal acidRatio = null;

		if (asset != null && liabilities != null && asset.intValue() > 0
				&& liabilities.intValue() > 0) {

			MathContext mc = new MathContext(14);

			acidRatio = asset.divide(liabilities, mc);
		}
		assetLiability.setAcidRatio(acidRatio);
	}

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
			prpExperience.setContractAmt(new BigDecimal(0));
			experienceList.add(prpExperience);
			this.getManagedBean().setScQtPRPExperiences(experienceList);
			getManagedBean().setTotalContracts(
					getManagedBean().getScQtPRPExperiences().size());
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteNonFederalGovtExperience() {
		debugLogger("deleteNonFederalGovtExperience");
		List<ScQtPRPExperience> experienceList = this.getManagedBean()
				.getScQtPRPExperiences();
		List<ScQtPRPExperience> selectedExperienceList = this.getManagedBean()
				.getSelectedPRPExperiences();
		if (selectedExperienceList != null) {
			List<ScQtPRPExperience> tobeDeletedExperiences = new ArrayList<ScQtPRPExperience>();

			for (ScQtPRPExperience scQtPRPExperience : selectedExperienceList) {
				tobeDeletedExperiences.add(scQtPRPExperience);
				experienceList.remove(scQtPRPExperience);
			}
			getManagedBean().setDeletedPRPExperiences(tobeDeletedExperiences);
		}
		getManagedBean().setTotalContracts(
				getManagedBean().getScQtPRPExperiences().size());

		totalContractYears();
	}

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

	public void deleteCreditFacilities() {
		debugLogger("deleteCreditFacilities");
		List<ScQtPRPCreditFacility> creditFacilitiesList = this
				.getManagedBean().getScQtPRPCreditFacilities();
		List<ScQtPRPCreditFacility> selectedcreditFacilitiesList = this
				.getManagedBean().getSelectedPRPCreditFacilities();
		if (creditFacilitiesList.size() == selectedcreditFacilitiesList.size()) {
			getManagedBean().setToBeDeletedCreditFacilities(
					selectedcreditFacilitiesList);
			creditFacilitiesList.clear();
		} else if (selectedcreditFacilitiesList != null) {
			List<ScQtPRPCreditFacility> toDeleteCreditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
			for (ScQtPRPCreditFacility facility : selectedcreditFacilitiesList) {
				toDeleteCreditFacilitiesList.add(facility);
				creditFacilitiesList.remove(facility);
			}
			getManagedBean().setToBeDeletedCreditFacilities(
					toDeleteCreditFacilitiesList);
		}
		totalCreditFacilitiesSum();
	}

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

	public void deleteCashOnHand() {
		debugLogger("deleteCashOnHand");
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		List<ScQtPRPCashOnHand> selectedCashOnHandList = this.getManagedBean()
				.getSelectedPRPCashOnHand();
		if (cashOnHandList.size() == selectedCashOnHandList.size()) {
			getManagedBean().setToBeDeletedCashOnHandList(
					selectedCashOnHandList);
			cashOnHandList.clear();
		} else if (selectedCashOnHandList != null) {

			List<ScQtPRPCashOnHand> toDeleteCashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
			for (ScQtPRPCashOnHand onHand : selectedCashOnHandList) {
				toDeleteCashOnHandList.add(onHand);
				cashOnHandList.remove(onHand);
			}
			getManagedBean().setToBeDeletedCashOnHandList(
					toDeleteCashOnHandList);
		}

		totalCashOnHandSum();
	}

	public String supplierProposalBackAction() {
		debugLogger("supplierProposalBackAction");
		return "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
	}

	public void zoneTabChange(TabChangeEvent evnt) {
		debugLogger("zoneTabChange");
		// ScQt scQt = getManagedBean().getQt();
		TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) evnt.getData();
		getManagedBean().setSelectedZone(specZoneDtl);
		log.info("Selected Zone : "
				+ this.getManagedBean().getSelectedZone().getZoneName());

		// Long zoneId = specZoneDtl.getSpecZoneId();
	}

	public void displayProposalDtls() {
		debugLogger("displayProposalDtls");
		List<ScQtProposal> list = this.getManagedBean().getQtProposalsList();
		if (list == null) {
			list = new ArrayList<ScQtProposal>();
		}
		for (int i = 0; i < this.getManagedBean().getPrepareProposal(); i++) {
			ScQtProposal proposal = new ScQtProposal();
			list.add(proposal);
		}
		this.getManagedBean().setQtProposalsList(list);
	}

	// TODO supplier proposal pre-render
	/**
	 * To redirect Supplier Proposal Screen..
	 */
	public void preRenderView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String qtId = originalServletRequest.getParameter("qtId");
		String encodedSupplierId = originalServletRequest
				.getParameter("supplierId");

		super.renewSesionObject();

		retrieveProposalChecklistDtls(Long.parseLong(qtId), encodedSupplierId,
				((PmUser) (new FacesScopes().getUserSessionBean())).getUserId());

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_PROPOSAL_MAIN_PAGE);
	}

	public void numOfEmployeesTypeChangeListener(AjaxBehaviorEvent event) {

		String viewBy = (String) event.getComponent().getAttributes()
				.get("value");
		// listener="#{supplierProposalDetailBackingBean.numOfEmployeesTypeChangeListener}"

	}

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

		retrieveSupplierDtls(getManagedBean().getTecCheckDetailsList().get(0));

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();
		SmSupplier smSupplier = getManagedBean().getSmSupplier();
		VelocityContext context = new VelocityContext();

		context.put("title", scQt.getQtTitle());
		context.put("userName", pmUser.getUserName());
		context.put("icNo", pmUser.getIdentificationNo());
		context.put("supplierName", smSupplier.getCompanyName());
		context.put("mofNo", ""); // TODO
		context.put("agency", pmOrgProfile.getOrgName());

		String integrityPact = DocumentGenerator
				.getInstance()
				.getHtmlFromVelocity(SourcingConstants.LOA_DECLARATION, context);

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
		updateQtFinProposalCheckList(currProposalChecklist,
				newProposalChecklist);

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
			updateQtFinProposalCheckList(currProposalChecklist,
					newProposalChecklist);
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

	private void updateQtFinProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist) {
		int index = getManagedBean().getFinanceCheckDetailsList().indexOf(
				currProposalChecklist);
		if (index >= 0) {
			getManagedBean().getFinanceCheckDetailsList().set(index,
					newProposalChecklist);
		}
		getManagedBean().setQtProposalChecklist(newProposalChecklist);
	}

	public void onRowSelectionListener(SelectEvent event) {
		SmSupplierBank bank = (SmSupplierBank) event.getObject();
		getManagedBean().setSmChequeSignatories(bank.getSmChequeSignatories());

	}

}
