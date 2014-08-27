package my.ep.web.backingbean.sc.supplierproposal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
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
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

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
	private static final String NAV_TO_ZONE_FINANCE_STATNDARD_PRODUCT_PAGE = "/xhtml/sc/supplierproposal/financialZone.xhtml";

	private static final String NAV_TO_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialSpecMoreProposal.xhtml";
	private static final String NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE = "/xhtml/sc/supplierproposal/financialMoreZone.xhtml";

	private static final String NAV_TO_SUPPLIER_DETAILS_PAGE = "/xhtml/sc/supplierproposal/financialSupplierDetails.xhtml";
	private static final String NAV_TO_CREDIT_FACILITIES_PAGE = "/xhtml/sc/supplierproposal/financialCreditFacilities.xhtml";
	private static final String NAV_TO_CASH_ON_HAND_PAGES = "/xhtml/sc/supplierproposal/financialCashOnHand.xhtml";
	private static final String NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialFederalGovt.xhtml";
	private static final String NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE = "/xhtml/sc/supplierproposal/financialNonFederalGovt.xhtml";
	private static final String NAV_TO_FINANCE_BANK_STATEMENT_PAGE = "/xhtml/sc/supplierproposal/financialBankStatement.xhtml";
	private static final String NAV_TO_ASSET_LIABILITY_PAGE = "/xhtml/sc/supplierproposal/financialAssetLiability.xhtml";
	private static final String NAV_TO_PROPOSAL_MAIN_PAGE = "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml";

	public static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);

	public SPDetailBackingBean() {
		log.info("inside constructor");

		getManagedBean().getTemplateSpecBean().setSelectedSpecQuestion(
				new TplSpecQuestion());
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
					.retrieveProposalCheckListByQtIdNSupplierId(reqDTO);
			getManagedBean().setQt(resDTO.getScQt());

			// Technical Check List
			if (resDTO.getTechnicalCheckLists() != null) {
				getManagedBean().setTecCheckDetailsList(
						resDTO.getTechnicalCheckLists());
				log.info("Technical Check list: "
						+ resDTO.getTechnicalCheckLists().size());

				for (ScQtProposalChecklist scQtProposalChecklist : resDTO
						.getTechnicalCheckLists()) {
					// this.getManagedBean().setQtProposalChecklist(scQtProposalChecklist);
					System.out
							.println("*****ScQtProposalChecklist---proposal checklist id is"
									+ scQtProposalChecklist
											.getQtProposalChecklistId());
					System.out
							.println("*****proposalChecklist--- qt checklist id is"
									+ scQtProposalChecklist.getScQtCheckList()
											.getQtChecklistId());
					System.out.println("*****proposalChecklist---qt id is"
							+ scQtProposalChecklist.getScQtCheckList()
									.getScQt().getQtId());

					if (scQtProposalChecklist.getScQtCheckList()
							.getScQtCheckListValue() != null) {

						System.out.println("*****is all employee*****"
								+ scQtProposalChecklist.getScQtCheckList()
										.getScQtCheckListValue()
										.isAllEmployee());

					}
				}
			}
			// Financial Check List
			if (resDTO.getFinancialCheckLists() != null) {

				getManagedBean().setFinanceCheckDetailsList(
						resDTO.getFinancialCheckLists());
				log.info("Financial Check list: "
						+ resDTO.getFinancialCheckLists().size());

				for (ScQtProposalChecklist scQtProposalChecklist : resDTO
						.getFinancialCheckLists()) {
					System.out
							.println("*****ScQtProposalChecklist---proposal checklist id is"
									+ scQtProposalChecklist
											.getQtProposalChecklistId());
					System.out
							.println("*****proposalChecklist---checklist id is"
									+ scQtProposalChecklist.getScQtCheckList()
											.getQtChecklistId());
					System.out.println("*****proposalChecklist---qt id is"
							+ scQtProposalChecklist.getScQtCheckList()
									.getScQt().getQtId());
				}
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

	public void retrieveDeclarationList() {
		debugLogger("retrieveDeclarationList");
		this.getManagedBean().setPrepareActiveIndex(2);
	}

	/**
	 * Method to save the Supplier Proposal details
	 */
	public void saveSPDetailsAction() {
		debugLogger("saveSPDetailsAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();
			ScQtProposal scQtProposal = getManagedBean().getQtProposal();
			List<ScQtProposalChecklist> tecQtProposalChecklists = getManagedBean()
					.getTecCheckDetailsList();
			scQtProposalChecklists.addAll(tecQtProposalChecklists);
			tecQtProposalChecklists = getManagedBean()
					.getFinanceCheckDetailsList();
			scQtProposalChecklists.addAll(tecQtProposalChecklists);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setQtSupplierId(1L);
			ProposalResDTO resDTO = new ProposalResDTO();
			// Update Changes
			for (ScQtProposalChecklist proposalChecklist : scQtProposalChecklists) {
				proposalChecklist.setSubmitted(true);
			}
			scQtProposal.setScQtProposalChecklists(scQtProposalChecklists);
			reqDTO.setScQtProposal(scQtProposal);
			resDTO = proposalSO.saveQTDtl(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "QT Proposal No. : "
						+ scQtProposal.getQtProposalId();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", message));
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Will be called for financial non government company experiance..
	 */
	public void saveNonGovCompanyExpDtls() {
		debugLogger("savePRPExperinceInfo");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			List<ScQtPRPExperienceNCheckList> experienceNCheckList = new ArrayList<ScQtPRPExperienceNCheckList>();
			List<ScQtPRPExperience> experiences = this.getManagedBean()
					.getScQtPRPExperiences();
			// TODO refer later for other requirements

			for (ScQtPRPExperience prpExperience : experiences) {
				
				ScQtPRPExperienceNCheckList checkList = new ScQtPRPExperienceNCheckList();
				checkList.setScQtProposalChecklist(scQtProposalChecklist);
				checkList.setScQtPRPExperience(prpExperience);
				experienceNCheckList.add(checkList);
			}
			// TODO check any chance of returning null from SO
			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();

			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(17);
				scQtProposalScore.setCriteriaValue(this.getManagedBean()
						.getTotalContractAmount());
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(17);
					scQtProposalScore.setCriteriaValue(this.getManagedBean()
							.getTotalContractAmount());
					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}
			}
			scQtProposalChecklist
					.setExperienceNCheckLists(experienceNCheckList);
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

		} finally {
			proposalSO = null;
		}
	}

	public void saveTechnicalSpecificationAction() {
		debugLogger("saveTechnicalSpecificationAction");
		// ScQtDoc scQtDoc = this.getManagedBean().getScQtDoc();
		ProposalResDTO proposalResDTO = saveTempleateSpecDtls();

		if (proposalResDTO.isSuccessful()) {
			int index = getManagedBean().getTecCheckDetailsList().indexOf(
					proposalResDTO.getProposalChecklist());
			if (index >= 0) {
				getManagedBean().getTecCheckDetailsList().set(index,
						proposalResDTO.getProposalChecklist());
			}

		}

	}

	public void saveFinancialSpecificationAction() {
		debugLogger("saveFinancialSpecificationAction");
		// ScQtDoc scQtDoc = this.getManagedBean().getScQtDoc();
		ProposalResDTO proposalResDTO = saveTempleateSpecDtls();
		if (proposalResDTO.isSuccessful()) {
			// return
			// "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
		}
		// return
		// "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
	}

	public ProposalResDTO saveTempleateSpecDtls() {
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
			for (TplSpecAnswer tplSpecAnswer : answers) {
				tplSpecAnswer.setTplSpecSuppAnswer(tplSpecAnswer
						.getTplSpecSuppAnswer());
				if (getManagedBean().getQt().isZonal()) {
					tplSpecAnswer.getTplSpecSuppAnswer().setTplSpecZoneDtl(
							getManagedBean().getSelectedZone());
				}
			}
			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

	public void saveFinancialCreditFacilityAction() {
		debugLogger("saveFinancialCreditFacilityAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ProposalResDTO resDTO = new ProposalResDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();

			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(51);
				scQtProposalScore.setCriteriaValue(this.getManagedBean()
						.getTotalAmountCredit());
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(51);
					scQtProposalScore.setCriteriaValue(this.getManagedBean()
							.getTotalAmountCredit());
					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}
			}
			scQtProposalChecklist.setScQtPRPCreditFacilities(this
					.getManagedBean().getScQtPRPCreditFacilities());
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			resDTO = proposalSO.saveProposalDetails(reqDTO);
		} finally {
			proposalSO = null;
		}

	}

	public void saveFinancialCashOnHandAction() {
		debugLogger("saveFinancialCashOnHandAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ProposalResDTO resDTO = new ProposalResDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();
			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(52);
				scQtProposalScore.setCriteriaValue(this.getManagedBean()
						.getTotalcashHand());
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(52);
					scQtProposalScore.setCriteriaValue(this.getManagedBean()
							.getTotalcashHand());
					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}
			}
			scQtProposalChecklist.setScQtPRPCashOnHands(this.getManagedBean()
					.getScQtPRPCashOnHand());
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			resDTO = proposalSO.saveProposalDetails(reqDTO);
		} finally {
			proposalSO = null;
		}
	}

	public void saveBankStatementAction() {
		debugLogger("saveBankStatementAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ProposalResDTO resDTO = new ProposalResDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();

			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();
			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(50);
				scQtProposalScore.setCriteriaValue(this.getManagedBean()
						.getAvgAmount());
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(50);
					scQtProposalScore.setCriteriaValue(this.getManagedBean()
							.getAvgAmount());
					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}
			}
			scQtProposalChecklist.setPrpBankStmts(this.getManagedBean()
					.getScQtPRPBankStmts());
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			resDTO = proposalSO.saveProposalDetails(reqDTO);
		} finally {
			proposalSO = null;
		}
	}

	// TODO to save no of employees involved in the current QT
	public void saveTechEmployeesDtlAction() {
		debugLogger("saveTechEmployessDtlAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<ScQtProposalScore> qtProposalScoreList = this.getManagedBean()
					.getScQtProposalScoreList();
			if (qtProposalScoreList == null) {
				debugLogger("*****qtProposalScoreList is null *****");
				qtProposalScoreList = new ArrayList<ScQtProposalScore>();
			}
			// TODO check for possible issue
			if (qtProposalScoreList.isEmpty()) {

				if (!scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().isAllEmployee()) {

					ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(21);
					ScQtPRPEmployee employee = getManagedBean()
							.getScQtPRPEmployee();
					employee.setScQtProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaValue(new BigDecimal(employee
							.getTotalEmployees()));
					scQtProposalChecklist.setScQtPRPEmployee(null);
					setUserDetails(scQtProposalScore);
					qtProposalScoreList.add(scQtProposalScore);

				} else {
					scQtProposalChecklist.setScQtPRPEmployee(null);
					qtProposalScoreList = null;
				}
			} else {
				if (!scQtProposalChecklist.getScQtCheckList()
						.getScQtCheckListValue().isAllEmployee()) {
					ScQtPRPEmployee employee = getManagedBean()
							.getScQtPRPEmployee();
					employee.setScQtProposalChecklist(scQtProposalChecklist);
					scQtProposalChecklist.setScQtPRPEmployee(employee);

					for (ScQtProposalScore scQtProposalScore : qtProposalScoreList) {
						scQtProposalScore
								.setProposalChecklist(scQtProposalChecklist);
						scQtProposalScore.setCriteriaType(21);
						scQtProposalScore.setCriteriaValue(new BigDecimal(
								employee.getTotalEmployees()));
					}

				} else {
					scQtProposalChecklist.setScQtPRPEmployee(null);
					for (ScQtProposalScore scQtProposalScore : qtProposalScoreList) {
						scQtProposalScore
								.setProposalChecklist(scQtProposalChecklist);
						scQtProposalScore.setCriteriaType(21);
						scQtProposalScore.setCriteriaValue(null);
					}
				}
			}
			scQtProposalChecklist.setProposalScore(qtProposalScoreList);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				debugLogger("saveTechEmployessDtlAction successful");
				ScQtProposalChecklist ProposalChecklist = resDTO
						.getProposalChecklist();
				if (ProposalChecklist.isSubmitted() == true) {
					ProposalChecklist.setSubmitted(false);
				}
				getManagedBean().setQtProposalChecklist(ProposalChecklist);
				int index = getManagedBean().getTecCheckDetailsList().indexOf(
						getManagedBean().getQtProposalChecklist());
				getManagedBean().getTecCheckDetailsList().set(index,
						getManagedBean().getQtProposalChecklist());

				// return
				// "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
			}
		} finally {
			proposalSO = null;
		}

		// return null;
	}

	public void saveTechNonGovCompanyExpAction() {
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

			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();
			if (proposalScore == null) {
				proposalScore = new ArrayList<ScQtProposalScore>();
			}

			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(15);
				scQtProposalScore.setCriteriaValue(new BigDecimal(this
						.getManagedBean().getTotalContracts()));
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);

				ScQtProposalScore scQtProposalScore1 = new ScQtProposalScore();
				scQtProposalScore1.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore1.setCriteriaType(16);
				scQtProposalScore1.setCriteriaValue(new BigDecimal(this
						.getManagedBean().getTotalYears()));
				setUserDetails(scQtProposalScore1);
				proposalScore.add(scQtProposalScore1);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);

					if (scQtProposalScore.getCriteriaType() == 15) {
						scQtProposalScore.setCriteriaValue(new BigDecimal(this
								.getManagedBean().getTotalContracts()));
					} else if (scQtProposalScore.getCriteriaType() == 16) {
						scQtProposalScore.setCriteriaValue(new BigDecimal(this
								.getManagedBean().getTotalContracts()));
					}
					setUserDetails(scQtProposalScore);

					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}

			}
			scQtProposalChecklist
					.setExperienceNCheckLists(experienceNCheckList);
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {

				ScQtProposalChecklist proposalChecklist = resDTO
						.getProposalChecklist();
				if (proposalChecklist.isSubmitted()) {
					proposalChecklist.setSubmitted(false);
				}
				getManagedBean().setQtProposalChecklist(proposalChecklist);
				int index = getManagedBean().getTecCheckDetailsList().indexOf(
						proposalChecklist);
				getManagedBean().getTecCheckDetailsList().set(index,
						proposalChecklist);
				// return
				// "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
			}

		} finally {
			proposalSO = null;
		}

		// return null;
	}

	public String completeProposalCkeckListAction() {

		debugLogger("completeTechNonGovCompanyExpAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				debugLogger("completeTechEmployessDtlAction successful");
				getManagedBean().setQtProposalChecklist(
						resDTO.getProposalChecklist());
				int index = getManagedBean().getTecCheckDetailsList().indexOf(
						resDTO.getProposalChecklist());
				getManagedBean().getTecCheckDetailsList().set(index,
						getManagedBean().getQtProposalChecklist());
				return "/xhtml/sc/supplierproposal/supplierProposalMain.xhtml?faces-redirect=true";
			}

		} finally {
			proposalSO = null;
		}

		return null;
	}

	public void saveAssetLiabilityAction() {
		debugLogger("saveAssetLiabilityAction");
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ProposalResDTO resDTO = new ProposalResDTO();
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			List<ScQtProposalScore> proposalScore = this.getManagedBean()
					.getScQtProposalScoreList();
			if (null != proposalScore && proposalScore.isEmpty()) {
				ScQtProposalScore scQtProposalScore = new ScQtProposalScore();
				scQtProposalScore.setProposalChecklist(scQtProposalChecklist);
				scQtProposalScore.setCriteriaType(60);
				/*
				 * scQtProposalScore.setCriteriaValue(this.getManagedBean()
				 * .getAvgAmount());
				 */
				setUserDetails(scQtProposalScore);
				proposalScore.add(scQtProposalScore);
			} else {
				for (ScQtProposalScore scQtProposalScore : proposalScore) {
					scQtProposalScore
							.setProposalChecklist(scQtProposalChecklist);
					scQtProposalScore.setCriteriaType(60);
					/*
					 * scQtProposalScore.setCriteriaValue(this.getManagedBean()
					 * .getAvgAmount());
					 */
					int a = proposalScore.indexOf(scQtProposalScore);
					proposalScore.set(a, scQtProposalScore);
				}
			}
			this.getManagedBean().setAcidRatio(
					this.getManagedBean().getAcidRatio());
			this.getManagedBean().getScQtPRPAssetLiability()
					.setScQtProposalChecklist(scQtProposalChecklist);
			scQtProposalChecklist.setPrpAssetLiabilities(this.getManagedBean()
					.getScQtPRPAssetLiability());
			scQtProposalChecklist.setProposalScore(proposalScore);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			resDTO = proposalSO.saveProposalDetails(reqDTO);
		} finally {
			proposalSO = null;
		}
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("handleFileUpload");
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getQtProposalChecklist();
			ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
					.getScQtProposalChecklistAttachment();
			if (null == attachment) {
				attachment = new ScQtProposalChecklistAttachment();
				attachment.setScQtProposalChecklist(scQtProposalChecklist);
			}
			attachment.setDocNo(scQtProposalChecklist.getScQtCheckList()
					.getScQt().getQtNo());
			attachment.setPrefixFileName(scQtProposalChecklist
					.getScQtProposal().getProposalNo());
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			scQtProposalChecklist
					.setScQtProposalChecklistAttachment(attachment);
			scQtProposalChecklist.setSubmitted(true);
		}
	}

	public void downloadPTJFile(ScQtProposalChecklist scQtProposalChecklist)
			throws IOException {
		debugLogger("downloadPTJFile");

		downloadFile(scQtProposalChecklist.getScQtCheckList()
				.getScQtCheckListAttachment());
	}

	public void onlineAction(ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("onlineAction");

		ScQtCheckList scQtCheckList = scQtProposalChecklist.getScQtCheckList();

		ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();

		TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

		templateSpecBean.setSpecMaster(specMaster);
		templateSpecBean.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		/* Long userId = 1L; */
		IRegistrationSO registrationSO = BusinessDelegate.getInstance()
				.getServiceObject("sm", IRegistrationSO.class);
		RegistrationReqDTO registrationReqDTO = new RegistrationReqDTO();
		registrationReqDTO.setSupplierId(userId);
		RegistrationResDTO registrationResDTO = registrationSO
				.retrieveSuppOnlineAcc(registrationReqDTO);
		templateSpecBean.setSupplier(registrationResDTO.getSmSupp());

		templateSpecBackingBean.retrieveTemplateSpecMaster(templateSpecBean);
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
		IQTProposalSO proposalSO = null;
		ProposalResDTO resDTO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			// String definitionKey =
			// scQtProposalChecklist.getScQtCheckList().getPmDocument().getDocDefinitionKey();
			// log.info("Definition Key : " + definitionKey);

			// to check proposalchecklist is technicalspecification - technical
			// standard product
			if ("TS".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getDefinationType().getParameterCode())) {

				getManagedBean().setQtProposalChecklist(scQtProposalChecklist);

				getManagedBean().setScQtCheckList(
						scQtProposalChecklist.getScQtCheckList());

				ScQtDoc scQtDoc = scQtProposalChecklist.getScQtCheckList()
						.getScQtDoc();
				this.getManagedBean().setScQtDoc(
						scQtProposalChecklist.getScQtCheckList().getScQtDoc());
				try {
					TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
					TemplateSpecBean templateSpecBean = getManagedBean()
							.getTemplateSpecBean();
					TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();

					templateSpecBean.setSpecMaster(specMaster);
					templateSpecBean
							.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
					templateSpecBean.setSupplier(scQtProposalChecklist
							.getScQtProposal().getScQtSupplier()
							.getSmSupplier());
					templateSpecBackingBean
							.retrieveTemplateSpecMaster(templateSpecBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE
						+ "?faces-redirect=true";

			}
			// to check proposalchecklist is technicalspecification - technical
			// NON_FEDERAL_GOVT
			else {
				if ("TN".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

					this.getManagedBean().setQtProposalChecklist(
							scQtProposalChecklist);

					this.getManagedBean().setScQtCheckList(
							scQtProposalChecklist.getScQtCheckList());

					reqDTO.setQtProposalCheckListId(scQtProposalChecklist
							.getQtProposalChecklistId());

					resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);

					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());

					List<ScQtPRPExperience> qtPRPExperiences = resDTO
							.getPrpExperience();
					log.info("Size : " + qtPRPExperiences.size());
					int totalYears = 0;

					for (ScQtPRPExperience experience : qtPRPExperiences) {
						totalYears += (experience.getContractPeriod());
					}

					this.getManagedBean().setTotalContracts(
							qtPRPExperiences.size());

					this.getManagedBean().setTotalYears((totalYears));

					this.getManagedBean().setScQtPRPExperiences(
							qtPRPExperiences);
					return NAV_TO_NON_FEDERAL_GOVT_PAGE
							+ "?faces-redirect=true";

				}
				// to check proposalchecklist is technicalspecification -
				// technical NO_OF_EMPLOYEE__PAGE
				else if ("NE".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

					this.getManagedBean().setQtProposalChecklist(
							scQtProposalChecklist);
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);

					if (this.getManagedBean().getScQtCheckList()
							.getScQtCheckListValue() == null) {

						this.getManagedBean()
								.getScQtCheckList()
								.setScQtCheckListValue(new ScQtCheckListValue());
					}

					reqDTO.setQtProposalCheckListId(scQtProposalChecklist
							.getQtProposalChecklistId());
					resDTO = proposalSO.retrieveEmployeeDtls(reqDTO);
					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());

					if (resDTO.getScQtPRPEmployee() != null) {

						this.getManagedBean().setScQtPRPEmployee(
								resDTO.getScQtPRPEmployee());
					} else {
						this.getManagedBean().setScQtPRPEmployee(
								new ScQtPRPEmployee());

					}
					return NAV_TO_NO_OF_EMPLOYEE__PAGE + "?faces-redirect=true";

				}
			}
		} finally {
			proposalSO = null;
		}
		return null;
	}

	public String prepareTechnicalViewAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalViewAction");
		IQTProposalSO proposalSO = null;
		ProposalResDTO resDTO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			System.out.println("*****proposal Check list Id*****"
					+ scQtProposalChecklist.getQtProposalChecklistId());
			System.out
					.println("*****proposal Id*****"
							+ scQtProposalChecklist.getScQtProposal()
									.getQtProposalId());
			if ("SP".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				System.out
						.println("******SP(SAMPLING_ADDRESS_PAGE) defenition type parameter code is ******"
								+ scQtProposalChecklist.getScQtCheckList()
										.getDefinationType().getParameterCode());
				this.getManagedBean().setQtProposalChecklist(
						scQtProposalChecklist);
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);
				reqDTO.setQtSupplierId(scQtProposalChecklist.getScQtProposal()
						.getScQtSupplier().getQtSupplierId());
				resDTO = proposalSO.retrieveSampleDetails(reqDTO);
				List<ScQtSampleSubmission> list = resDTO
						.getSampleSubmissionDtls();
				log.info("Size : " + list.size());
				for (ScQtSampleSubmission scQtSampleSubmission : list) {
					this.getManagedBean().setScQtSampleSubmission(
							scQtSampleSubmission);
					log.info("Address Id : "
							+ getManagedBean().getScQtSampleSubmission()
									.getAddress().getAddress1());
				}
				this.getManagedBean().setScQtSampleSubmissionList(list);
				ScQtProposal scQtProposal = resDTO.getScQtProposal();
				this.getManagedBean().setQtProposal(scQtProposal);
				if (this.getManagedBean().getQtProposal() != null) {
					ScQtSampleLable sampleLable = this.getManagedBean()
							.getQtProposal().getScQtSampleLable();
					List<ScQtSampleItem> sampleItems = sampleLable
							.getSampleItems();
					for (ScQtSampleItem scQtSampleItem : sampleItems) {
						log.info("Item : "
								+ scQtSampleItem.getScRequestItem()
										.getTplSpecQuestion().getClItem());
					}
					this.getManagedBean().setScQtSampleItemList(sampleItems);
				}
				return NAV_TO_SAMPLING_ADDRESS_PAGE + "?faces-redirect=true";

			} else {
				if ("TG".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);
					reqDTO.setQtId(scQtCheckList.getScQt().getQtId());
					reqDTO.setQtSupplierId(scQtProposalChecklist
							.getScQtProposal().getScQtSupplier()
							.getSmSupplier().getSupplierId());
					System.out.println("*****proposal Check list Id*****"
							+ scQtProposalChecklist.getQtProposalChecklistId());
					System.out.println("*****proposal Id*****"
							+ scQtProposalChecklist.getScQtProposal()
									.getQtProposalId());
					System.out.println("*****QtSupplierId*****"
							+ scQtProposalChecklist.getScQtProposal()
									.getScQtSupplier().getQtSupplierId());
					resDTO = proposalSO.retrieveCompanyExpDtls(reqDTO);
					List<ScLoa> list = resDTO.getLoaResultBean()
							.getResultList();
					this.getManagedBean().setScLoaList(list);
					int total = 0;
					for (int i = 0; i < getManagedBean().getScLoaList().size(); i++) {
						ScLoa loa = getManagedBean().getScLoaList().get(i);
						if (loa.getContractDuration() == null) {
							loa.setContractDuration(new BigDecimal(0));
						}
						total = total + loa.getContractDuration().intValue();
						this.getManagedBean().setNoOfYears(total);
						log.info("Total No of Years : " + total);
						// ScQtPRPExperience experience = (ScQtPRPExperience)
						// experiencesList.get(i);
					}
					for (ScLoa loa : getManagedBean().getScLoaList()) {
						// log.info("Agency Name : " +
						// loa.getPmOrgProfile().getOrgName());
						log.info("Title : " + loa.getScLoiLoa().getTitle());
					}
					this.getManagedBean().setContractSize(list.size());
					return NAV_TO_FEDERAL_GOVT_PAGE + "?faces-redirect=true";
				} // to check proposalchecklist is technicalspecification -
					// technical EMPL_LIST_PAGE
				else if ("TL".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					List<SmPersonnel> smPersonnelsList = new ArrayList<SmPersonnel>();
					long supplierId = scQtProposalChecklist.getScQtProposal()
							.getScQtSupplier().getSmSupplier().getSupplierId();
					this.getManagedBean().setQtProposalChecklist(
							scQtProposalChecklist);
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);
					ProposalReqDTO reqDTO2 = new ProposalReqDTO();
					reqDTO2.setSupplierId(supplierId);
					ProposalResDTO resDTO2 = proposalSO
							.retriveSupplierById(reqDTO2);
					getManagedBean().setSmAppl(resDTO2.getSmAppl());
					for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
							.getSmPersonnels()) {
						log.info("Name : " + smPersonnel.getName());
						smPersonnelsList.add(smPersonnel);
					}
					getManagedBean().setSmPersonnelList(smPersonnelsList);
					int totalYearsExp = 0;
					for (int i = 0; i < getManagedBean().getSmPersonnelList()
							.size(); i++) {
						SmPersonnel personnel = getManagedBean()
								.getSmPersonnelList().get(i);
						if (personnel.getWorkExperienceYear() > 0) {

						}
						totalYearsExp = totalYearsExp
								+ personnel.getWorkExperienceYear();
						this.getManagedBean().setTotalNoOfYearsExperience(
								totalYearsExp);
						log.info("Total No of Years Experience : "
								+ totalYearsExp);
					}
					return NAV_TO_TECHNICAL_EMPL_LIST_PAGE
							+ "?faces-redirect=true";
				}
			}
		} finally {
			proposalSO = null;
		}
		return null;
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
		return NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE + "?faces-redirect=true";

	}

	public String prepareFinancialAction(
			ScQtProposalChecklist financialChecklist) {
		debugLogger("prepareFinancialAction");
		IQTProposalSO proposalSO = null;
		ProposalResDTO resDTO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			// to check financial check list is for
			if ("TS".equalsIgnoreCase(financialChecklist.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				System.out
						.println("******TS(ZONE_MORE_THAN_PROPOSAL_PAGE) defenition type parameter code is ******"
								+ financialChecklist.getScQtCheckList()
										.getDefinationType().getParameterCode());

				this.getManagedBean()
						.setQtProposalChecklist(financialChecklist);
				ScQtCheckList scQtCheckList = financialChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);
				ScQtDoc scQtDoc = scQtCheckList.getScQtDoc();
				this.getManagedBean().setScQtDoc(scQtDoc);
				ScQt scQt = this.getManagedBean().getQt();
				reqDTO.setQtId(scQt.getQtId());
				// To retrieve financial spec details
				resDTO = proposalSO.retrieveFinancialSpecDtls(reqDTO);
				if (scQt.isZonal()) {
					List<TplSpecZoneDtl> zones = resDTO.getZones();
					if (null != zones) {
						this.getManagedBean().setZones(zones);
						this.getManagedBean().setSelectedZone(zones.get(0));
					}
					if (scQt.getMaxProposal() > 1) {

						System.out
								.println("TS ***** NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE*****");
						return NAV_TO_ZONE_MORE_THAN_PROPOSAL_PAGE
								+ "?faces-redirect=true";
					}
					try {
						TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
						TemplateSpecBean templateSpecBean = getManagedBean()
								.getTemplateSpecBean();
						TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
						templateSpecBean.setSpecMaster(specMaster);
						templateSpecBean
								.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
						templateSpecBean.setSupplier(financialChecklist
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier());
						templateSpecBackingBean
								.retrieveTemplateSpecMaster(templateSpecBean);
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out
							.println("TS ***** NAV_TO_ZONE_FINANCE_STATNDARD_PRODUCT_PAGE *****");
					return NAV_TO_ZONE_FINANCE_STATNDARD_PRODUCT_PAGE
							+ "?faces-redirect=true";

				} else {

					System.out
							.println("TS ***** NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE*****");
					if (scQt.getMaxProposal() > 1) {
						this.getManagedBean().setScQtCheckList(scQtCheckList);
						System.out
								.println("TS ***** NAV_TO_MORE_THAN_PROPOSAL_PAGE *****");

						return NAV_TO_MORE_THAN_PROPOSAL_PAGE
								+ "?faces-redirect=true";
					}
					try {
						TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();
						TemplateSpecBean templateSpecBean = getManagedBean()
								.getTemplateSpecBean();
						TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
						templateSpecBean.setSpecMaster(specMaster);
						templateSpecBean
								.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);
						templateSpecBean.setSupplier(financialChecklist
								.getScQtProposal().getScQtSupplier()
								.getSmSupplier());
						templateSpecBackingBean
								.retrieveTemplateSpecMaster(templateSpecBean);
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out
							.println("TS ***** NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE *****");

					return NAV_TO_FINANCE_STATNDARD_PRODUCT_PAGE
							+ "?faces-redirect=true";

				}
			}

			// TODO
			else {
				if ("FN".equalsIgnoreCase(financialChecklist.getScQtCheckList()
						.getPmDocument().getDocDefinitionKey())) {
					System.out
							.println("******FN(NON_GOVT_COMPANY_EXPERIENCE_PAGE) defenition type parameter code is ******"
									+ financialChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());

					List<ScQtProposalChecklist> techinicalProposalCheckLists = this
							.getManagedBean().getTecCheckDetailsList();
					ScQtProposalChecklist proposalCheckList = null;
					if (null != techinicalProposalCheckLists) {
						for (ScQtProposalChecklist prChecklist : techinicalProposalCheckLists) {
							if(null != prChecklist.getScQtCheckList().getPmDocument()){
							if (QTConstants.TECHNICAL_PRIVATE_CODE
									.equalsIgnoreCase(
											prChecklist.getScQtCheckList()
													.getPmDocument()
													.getDocDefinitionKey())) {
								proposalCheckList = prChecklist;
								break;
							}
						}
						}
					}
					if(null != proposalCheckList){
					reqDTO.setScQtProposalChecklist(proposalCheckList);
					reqDTO.setDocDefinationKey(financialChecklist
							.getScQtCheckList().getPmDocument()
							.getDocDefinitionKey());
					resDTO = proposalSO.retrieveNonGovCompanyExpDtls(reqDTO);
					
					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());
					List<ScQtPRPExperience> list = resDTO.getPrpExperience();
					int totalContractAmount = 0;
					for (ScQtPRPExperience scQtPRPExperience : list) {
						totalContractAmount = totalContractAmount
								+ scQtPRPExperience.getContractAmt().intValue();
					}
					this.getManagedBean().setTotalContractAmount(
							new BigDecimal(totalContractAmount));
					this.getManagedBean().setScQtPRPExperiences(list);
					}
					System.out
							.println("FN ***** NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE *****");
					return NAV_TO_NON_GOVT_COMPANY_EXPERIENCE_PAGE
							+ "?faces-redirect=true";

				} else if ("CF".equalsIgnoreCase(financialChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					System.out
							.println("******CF(CREDIT_FACILITIES_PAGE) defenition type parameter code is ******"
									+ financialChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());

					reqDTO.setScQtProposalChecklist(financialChecklist);
					// to retrieve credit facility details
					resDTO = proposalSO.retrieveCreditFacilitiesDtls(reqDTO);

					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());

					List<ScQtPRPCreditFacility> facilitiesList = resDTO
							.getCreditFacilityResultBean();

					double totalCredit = 0;
					double totalPaid = 0;

					for (ScQtPRPCreditFacility scQtPRPCreditFacility : facilitiesList) {
						totalCredit += scQtPRPCreditFacility.getCreditAmt()
								.doubleValue();
						totalPaid += scQtPRPCreditFacility.getAmtPaid()
								.doubleValue();
					}

					this.getManagedBean().setTotalAmountCredit(
							new BigDecimal(totalCredit));
					this.getManagedBean().setTotalAmountPaid(
							new BigDecimal(totalPaid));

					BigDecimal credit = new BigDecimal(totalCredit);
					BigDecimal amountPaid = new BigDecimal(totalPaid);

					MathContext mc = new MathContext(4);
					if (credit.doubleValue() > 0) { // &&
													// amountPaid.doubleValue()
													// > 0
						BigDecimal paid = amountPaid.divide(credit, mc);
						BigDecimal amt = new BigDecimal(100);
						BigDecimal percentage = paid.multiply(amt);
						this.getManagedBean()
								.setTotalPercentagePaid(percentage);
					}
					this.getManagedBean().setScQtPRPCreditFacilities(
							facilitiesList);
					System.out
							.println("CF ***** NAV_TO_CREDIT_FACILITIES_PAGE *****");

					return NAV_TO_CREDIT_FACILITIES_PAGE
							+ "?faces-redirect=true";

				} else if ("CH".equalsIgnoreCase(financialChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					System.out
							.println("******CH(CASH_ON_HAND_PAGES) defenition type parameter code is ******"
									+ financialChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());
					reqDTO.setQtProposalCheckListId(financialChecklist
							.getQtProposalChecklistId());
					resDTO = proposalSO.retrieveCashOnHandDtls(reqDTO);
					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());
					List<ScQtPRPCashOnHand> cashOnHand = resDTO
							.getCashOnHandResultBean();
					int totalCashOnHand = 0;
					for (ScQtPRPCashOnHand scQtPRPCashOnHand : cashOnHand) {
						totalCashOnHand = totalCashOnHand
								+ scQtPRPCashOnHand.getCashAmount().intValue();
					}
					this.getManagedBean().setTotalcashHand(
							new BigDecimal(totalCashOnHand));
					log.info("Size : " + cashOnHand.size());
					this.getManagedBean().setScQtPRPCashOnHand(cashOnHand);

					System.out
							.println("CH ***** NAV_TO_CASH_ON_HAND_PAGES *****");

					return NAV_TO_CASH_ON_HAND_PAGES + "?faces-redirect=true";

				} else if ("BS".equalsIgnoreCase(financialChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					System.out
							.println("******BS(FINANCE_BANK_STATEMENT_PAGE) defenition type parameter code is ******"
									+ financialChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());

					reqDTO.setScQtProposalChecklist(financialChecklist);
					resDTO = proposalSO.retrieveBankStmtDtls(reqDTO);
					this.getManagedBean().setScQtProposalScoreList(
							resDTO.getProposalScores());

					List<ScQtPRPBankStmt> bankStmtList = resDTO
							.getBankStmtResultBean();

					int totalAmount = 0;
					List<ScQtPRPBankStmt> bankList = this.getManagedBean()
							.getScQtPRPBankStmts();
					if (bankList == null) {
						bankList = new ArrayList<ScQtPRPBankStmt>();
					}
					for (ScQtPRPBankStmt scQtPRPBankStmt : bankStmtList) {
						totalAmount = totalAmount
								+ scQtPRPBankStmt.getBalanceAmt().intValue();

						String monthvalue = "";
						Date date = scQtPRPBankStmt.getStmtDate();
						SimpleDateFormat dfFormat = new SimpleDateFormat(
								"dd/MM/yyyy");
						String convert = dfFormat.format(date);
						String month = convert.substring(3, 5);

						if ("01".equalsIgnoreCase(month)) {
							monthvalue = "January ".concat(convert.substring(6,
									convert.length()));
						}
						if ("02".equalsIgnoreCase(month)) {
							monthvalue = "February ".concat(convert.substring(
									6, convert.length()));
						}
						if ("03".equalsIgnoreCase(month)) {
							monthvalue = "March ".concat(convert.substring(6,
									convert.length()));
						}
						if ("04".equalsIgnoreCase(month)) {
							monthvalue = "April ".concat(convert.substring(6,
									convert.length()));
						}
						if ("05".equalsIgnoreCase(month)) {
							monthvalue = "May ".concat(convert.substring(6,
									convert.length()));
						}
						if ("06".equalsIgnoreCase(month)) {
							monthvalue = "June ".concat(convert.substring(6,
									convert.length()));
						}
						if ("07".equalsIgnoreCase(month)) {
							monthvalue = "July ".concat(convert.substring(6,
									convert.length()));
						}
						if ("08".equalsIgnoreCase(month)) {
							monthvalue = "August ".concat(convert.substring(6,
									convert.length()));
						}
						if ("09".equalsIgnoreCase(month)) {
							monthvalue = "September ".concat(convert.substring(
									6, convert.length()));
						}
						if ("10".equalsIgnoreCase(month)) {
							monthvalue = "October ".concat(convert.substring(6,
									convert.length()));
						}
						if ("11".equalsIgnoreCase(month)) {
							monthvalue = "November ".concat(convert.substring(
									6, convert.length()));
						}
						if ("12".equalsIgnoreCase(month)) {
							monthvalue = "December ".concat(convert.substring(
									6, convert.length()));
						}

						scQtPRPBankStmt.setMonth(monthvalue);
						bankList.add(scQtPRPBankStmt);
					}
					this.getManagedBean().setTotalAmount(
							new BigDecimal(totalAmount));

					BigDecimal amount = new BigDecimal(totalAmount);
					BigDecimal divide = new BigDecimal(3);
					MathContext mc = new MathContext(4);
					if (amount.intValue() > 0 && divide.intValue() > 0) {
						BigDecimal avg = amount.divide(divide, mc);
						this.getManagedBean().setAvgAmount(avg);
					}
					log.info("Size : " + bankStmtList.size());
					this.getManagedBean().setScQtPRPBankStmts(bankStmtList);

					System.out
							.println("BS ***** NAV_TO_FINANCE_BANK_STATEMENT_PAGE *****");

					return NAV_TO_FINANCE_BANK_STATEMENT_PAGE
							+ "?faces-redirect=true";

				} else if ("AL".equalsIgnoreCase(financialChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					System.out
							.println("******AL(ASSET_LIABILITY_PAGE) defenition type parameter code is ******"
									+ financialChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());

					reqDTO.setScQtProposalChecklist(financialChecklist);
					resDTO = proposalSO.retrieveAssetLiabilityDtls(reqDTO);

					ScQtPRPAssetLiability assetLiability = resDTO
							.getScQtPRPAssetLiability();
					if (null != resDTO.getProposalScores()
							&& !resDTO.getProposalScores().isEmpty()) {
						this.getManagedBean().setScQtProposalScoreList(
								resDTO.getProposalScores());
					}
					if (null != assetLiability) {
						BigDecimal asset = assetLiability.getCurrentAssets();
						BigDecimal liabilities = assetLiability
								.getCurrentLiabilities();
						MathContext mc = new MathContext(4);
						if (asset.intValue() > 0 && liabilities.intValue() > 0) {
							BigDecimal ratio = asset.divide(liabilities, mc);
							this.getManagedBean().setAcidRatio(ratio);
						}
						this.getManagedBean().setScQtPRPAssetLiability(
								assetLiability);
					}
					System.out
							.println("AL ***** NAV_TO_ASSET_LIABILITY_PAGE *****");
					return NAV_TO_ASSET_LIABILITY_PAGE + "?faces-redirect=true";

				}
			}
		}

		finally {
			proposalSO = null;
		}
		return null;
	}

	public String prepareFinancialViewAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFinancialViewAction");
		IQTProposalSO proposalSO = null;
		ProposalResDTO resDTO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			String definitionKey = scQtProposalChecklist.getScQtCheckList()
					.getPmDocument().getDocDefinitionKey();

			// what's happening here
			if ("SP".equalsIgnoreCase(scQtProposalChecklist.getScQtCheckList()
					.getDefinationType().getParameterCode())) {
				System.out
						.println("******SP() defenition type parameter code is ******"
								+ scQtProposalChecklist.getScQtCheckList()
										.getDefinationType().getParameterCode());
				ScQtCheckList scQtCheckList = scQtProposalChecklist
						.getScQtCheckList();
				this.getManagedBean().setScQtCheckList(scQtCheckList);

				System.out.println("SP ***** null_SAME_PAGE *****");
				return null;
			}
			//

			else {
				if ("FG".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {

					System.out
							.println("******FG(GOVT_COMPANY_EXPERIENCE_PAGE) defenition type parameter code is ******"
									+ scQtProposalChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);

					reqDTO.setQtId(scQtCheckList.getScQt().getQtId());
					reqDTO.setQtSupplierId(scQtProposalChecklist
							.getScQtProposal().getScQtSupplier()
							.getQtSupplierId());
					resDTO = proposalSO.retrieveCompanyExpDtls(reqDTO);
					List<ScLoa> list = resDTO.getLoaResultBean()
							.getResultList();
					log.info(" List Size : " + list.size());
					this.getManagedBean().setScLoaList(list);
					int totalAmount = 0;
					for (ScLoa scLoa : list) {
						totalAmount = totalAmount
								+ scLoa.getLoaAmt().intValue();
					}
					this.getManagedBean().setTotalContractAmount(
							new BigDecimal(totalAmount));
					System.out
							.println("FG ***** NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE *****");

					return NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE
							+ "?faces-redirect=true";

				} else if ("SD".equalsIgnoreCase(scQtProposalChecklist
						.getScQtCheckList().getPmDocument()
						.getDocDefinitionKey())) {
					// long qtId =
					// scQtProposalChecklist.getScQtProposal().getScQtSupplier().getScQt().getQtId();

					System.out
							.println("******SD(SUPPLIER_DETAILS_PAGE) defenition type parameter code is ******"
									+ scQtProposalChecklist.getScQtCheckList()
											.getPmDocument()
											.getDocDefinitionKey());
					long supplierId = scQtProposalChecklist.getScQtProposal()
							.getScQtSupplier().getSmSupplier().getSupplierId();
					log.info("Supplier Id : " + supplierId);
					ScQtCheckList scQtCheckList = scQtProposalChecklist
							.getScQtCheckList();
					this.getManagedBean().setScQtCheckList(scQtCheckList);
					ProposalReqDTO reqDTO1 = new ProposalReqDTO();
					reqDTO1.setSupplierId(supplierId);
					ProposalResDTO resDTO1 = proposalSO
							.retriveSupplierById(reqDTO1);
					getManagedBean().setSmSupplier(resDTO1.getSmSupplier());
					getManagedBean().setSmAppl(resDTO1.getSmAppl());
					List<SmSupplierBank> smSupplierBanks = getManagedBean()
							.getSmAppl().getSmSupplierBanks();
					log.info("Paid up capital : "
							+ getManagedBean().getSmAppl().getSmCompanyBasic()
									.getPaidUpCapital());
					log.info("Authorized capital : "
							+ getManagedBean().getSmAppl().getSmCompanyBasic()
									.getAuthorizedCapital());
					log.info("Account Number : "
							+ getManagedBean().getSmAppl().getSmSupplierBanks()
									.get(0).getAccountNo());
					System.out
							.println("SD ***** NAV_TO_SUPPLIER_DETAILS_PAGE *****");

					return NAV_TO_SUPPLIER_DETAILS_PAGE
							+ "?faces-redirect=true";

				}
			}
		} finally {
			proposalSO = null;
		}
		return null;
	}

	public void totalCreditSum() {
		debugLogger("totalCreditSum");
		double total = 0;
		double totalPaid = 0;
		List<ScQtPRPCreditFacility> creditFacilitiesList = this
				.getManagedBean().getScQtPRPCreditFacilities();
		if (creditFacilitiesList == null) {
			creditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
		}

		// for (int i = 0; i < creditFacilitiesList.size(); i++)

		for (ScQtPRPCreditFacility facility : creditFacilitiesList) {
			// ScQtPRPCreditFacility facility = (ScQtPRPCreditFacility)
			// creditFacilitiesList
			// .get(i);
			if (facility.getCreditAmt() == null) {
				facility.setCreditAmt(new BigDecimal(0));
			}
			if (facility.getAmtPaid() == null) {
				facility.setCreditAmt(new BigDecimal(0));
			}
			total += facility.getCreditAmt().doubleValue();
			totalPaid += facility.getAmtPaid().doubleValue();

			this.getManagedBean().setTotalAmountCredit(new BigDecimal(total));

			this.getManagedBean().setTotalAmountPaid(new BigDecimal(totalPaid));

			BigDecimal credit = new BigDecimal(total);
			BigDecimal amountPaid = new BigDecimal(totalPaid);
			MathContext mc = new MathContext(4);
			BigDecimal paid = amountPaid.divide(credit, mc);
			BigDecimal amt = new BigDecimal(100);
			BigDecimal percentage = paid.multiply(amt);
			this.getManagedBean().setTotalPercentagePaid(percentage);
		}
	}

	public void totalAmtPaidSum() {
		debugLogger("totalAmtPaidSum");
		int total = 0;
		int totalCredit = 0;
		List<ScQtPRPCreditFacility> creditFacilitiesList = this
				.getManagedBean().getScQtPRPCreditFacilities();
		if (creditFacilitiesList == null) {
			creditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
		}
		for (int i = 0; i < creditFacilitiesList.size(); i++) {
			ScQtPRPCreditFacility facility = (ScQtPRPCreditFacility) creditFacilitiesList
					.get(i);
			if (facility.getAmtPaid() == null) {
				facility.setAmtPaid(new BigDecimal(0));
			}
			totalCredit = totalCredit + facility.getCreditAmt().intValue();
			total = total + facility.getAmtPaid().intValue();
			this.getManagedBean().setTotalAmountPaid(new BigDecimal(total));
			this.getManagedBean().setTotalAmountCredit(new BigDecimal(total));

			BigDecimal credit = new BigDecimal(totalCredit);
			BigDecimal amountPaid = new BigDecimal(total);
			MathContext mc = new MathContext(4);
			BigDecimal paid = amountPaid.divide(credit, mc);
			System.out.println("paid : " + paid);
			BigDecimal amt = new BigDecimal(100);
			BigDecimal percentage = paid.multiply(amt);
			this.getManagedBean().setTotalPercentagePaid(percentage);
		}
	}

	public void totalCashOnHandSum() {
		debugLogger("totalAmtPaidSum");
		int total = 0;
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		if (cashOnHandList == null) {
			cashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
		}
		for (int i = 0; i < cashOnHandList.size(); i++) {
			ScQtPRPCashOnHand onHand = (ScQtPRPCashOnHand) cashOnHandList
					.get(i);
			if (onHand.getCashAmount() == null) {
				onHand.setCashAmount(new BigDecimal(0));
			}
			total = total + onHand.getCashAmount().intValue();
			this.getManagedBean().setTotalcashHand(new BigDecimal(total));
		}
	}

	public void totalAmountSum() {
		debugLogger("totalAmountSum");
		int total = 0;
		List<ScQtPRPBankStmt> bankStmtList = this.getManagedBean()
				.getScQtPRPBankStmts();
		if (bankStmtList == null) {
			bankStmtList = new ArrayList<ScQtPRPBankStmt>();
		}
		for (int i = 0; i < bankStmtList.size(); i++) {
			ScQtPRPBankStmt stmt = (ScQtPRPBankStmt) bankStmtList.get(i);
			if (stmt.getBalanceAmt() == null) {
				stmt.setBalanceAmt(new BigDecimal(0));
			}
			total = total + stmt.getBalanceAmt().intValue();
			this.getManagedBean().setTotalAmount(new BigDecimal(total));

			BigDecimal amount = new BigDecimal(total);
			BigDecimal divide = new BigDecimal(3);
			MathContext mc = new MathContext(4);
			if (amount.intValue() > 0 && divide.intValue() > 0) {
				BigDecimal avg = amount.divide(divide, mc);
				this.getManagedBean().setAvgAmount(avg);
			}
		}
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
		debugLogger("totalAmountSum");
		int total = 0;
		List<ScQtPRPExperience> experiencesList = this.getManagedBean()
				.getScQtPRPExperiences();
		if (experiencesList == null) {
			experiencesList = new ArrayList<ScQtPRPExperience>();
		}
		for (int i = 0; i < experiencesList.size(); i++) {
			ScQtPRPExperience experience = (ScQtPRPExperience) experiencesList
					.get(i);
			/*
			 * if (experience.getContractPeriod() < 0) {
			 * experience.setContractPeriod(0); }
			 */
			total = total + experience.getContractPeriod();
			this.getManagedBean().setTotalYears(total);
			/*
			 * this.getManagedBean().setTotalAmount(new BigDecimal(total));
			 * 
			 * BigDecimal amount = new BigDecimal(total); BigDecimal divide =
			 * new BigDecimal(3); MathContext mc = new MathContext(4); if
			 * (amount.intValue() > 0 && divide.intValue() > 0) { BigDecimal avg
			 * = amount.divide(divide, mc); System.out.println("paid : " + avg);
			 * this.getManagedBean().setAvgAmount(avg); }
			 */
		}
		if (experiencesList.size() == 0) {
			this.getManagedBean().setTotalYears(0);
		}
	}

	public void totalContractAmount() {
		debugLogger("totalContractAmount");
		int total = 0;
		List<ScQtPRPExperience> experiencesList = this.getManagedBean()
				.getScQtPRPExperiences();
		if (experiencesList == null) {
			experiencesList = new ArrayList<ScQtPRPExperience>();
		}
		for (int i = 0; i < experiencesList.size(); i++) {
			ScQtPRPExperience experience = (ScQtPRPExperience) experiencesList
					.get(i);
			if (experience.getContractAmt() == null) {
				experience.setContractAmt(new BigDecimal(0));
			}
			total = total + experience.getContractAmt().intValue();
			this.getManagedBean().setTotalContractAmount(new BigDecimal(total));
		}
	}

	public void totalAcidRatio() {
		debugLogger("totalAcidRatio");
		ScQtPRPAssetLiability assetLiability = this.getManagedBean()
				.getScQtPRPAssetLiability();
		BigDecimal asset = assetLiability.getCurrentAssets();
		BigDecimal liabilities = assetLiability.getCurrentLiabilities();
		MathContext mc = new MathContext(4);
		if (asset.intValue() > 0 && liabilities.intValue() > 0) {
			BigDecimal ratio = asset.divide(liabilities, mc);
			this.getManagedBean().setAcidRatio(ratio);
		}
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
		if (experienceList.size() == selectedExperienceList.size()) {

			// getManagedBean().get
			experienceList.clear();
		} else {
			for (ScQtPRPExperience scQtPRPExperience : selectedExperienceList) {

				experienceList.remove(scQtPRPExperience);
			}
		}
		getManagedBean().setScQtPRPExperiences(experienceList);
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
			creditFacilitiesList.clear();
		} else {
			for (ScQtPRPCreditFacility facility : selectedcreditFacilitiesList) {
				creditFacilitiesList.remove(facility);
			}
		}
		totalCreditSum();
	}

	public void addCashOnHand() {
		debugLogger("addCashOnHand");
		ScQtPRPCashOnHand cashOnHand = new ScQtPRPCashOnHand();
		try {
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
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	public void deleteCashOnHand() {
		debugLogger("deleteCashOnHand");
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		List<ScQtPRPCashOnHand> selectedCashOnHandList = this.getManagedBean()
				.getSelectedPRPCashOnHand();
		if (cashOnHandList.size() == selectedCashOnHandList.size()) {
			cashOnHandList.clear();
		} else {
			for (ScQtPRPCashOnHand onHand : selectedCashOnHandList) {
				cashOnHandList.remove(onHand);
			}
		}
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

}
