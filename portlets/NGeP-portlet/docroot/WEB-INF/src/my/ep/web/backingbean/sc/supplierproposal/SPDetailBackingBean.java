package my.ep.web.backingbean.sc.supplierproposal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.sc.EncryptDataBean;
import my.ep.biz.bean.sc.EncryptDataBean.EncryptColumnName;
import my.ep.biz.bean.sc.EncryptDataBean.EncryptTableName;
import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.tpl.spec.ISpecZoneLocSO;
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
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sm.gst.SupplierGstReqDTO;
import my.ep.dto.sm.gst.SupplierGstResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceReqDTO;
import my.ep.dto.sm.supplierDataMaintenance.SupplierDataMaintenanceResDTO;
import my.ep.dto.tpl.spec.SpecZoneLocReqDTO;
import my.ep.dto.tpl.spec.SpecZoneLocResDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListValue;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklistAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPAssetLiability;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCashOnHand;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCreditFacility;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPEmployee;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalReportAttachment;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleItem;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.sc.supplierproposal.ScSlAttachment;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecSuppAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecZone;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
//#21695 farid start 2/4/2021
import my.ep.persistence.entity.tpl.spec.TplSpecZoneQty;
//#21695 farid end 2/4/2021														
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.encrypt.BouncyCastleProvider_AES_CBC;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sc.supplierproposal.SPDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.util.template.DocumentGenerator;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Supplier Proposal Backing Bean
 * 
 * @author Viswanath
 * @author Venkat Chanumolu
 * @author saha
 * 
 */
@ManagedBean(name = "supplierProposalDetailBackingBean")
@RequestScoped
public class SPDetailBackingBean extends
		SourcingBaseBackingBean<SPDetailManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	private StreamedContent sampleLabelStreamContent;

	private static Logger log = EpLogger.getLogger(SPDetailBackingBean.class);
	

	/**
	 * Default Constructor
	 */
	public SPDetailBackingBean() {
		debugLogger("inside constructor");
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
		}else{
			log.info(message);	
		}
	}

	
	/**
	 * 
	 * @param qtID
	 * @param userId
	 * @param isReadonly
	 */
	public void retrieveProposalChecklistDtls(Long qtID, Long userId,
			boolean isReadonly){
		retrieveProposalChecklistDtls(qtID, userId, isReadonly, false);
	}
	
	/**
	 * @param qtOrProposalId  -> set qtID or proposalId if byProposalId is true
	 * @param userId
	 */
	public void retrieveProposalChecklistDtls(Long qtOrProposalId, Long userId,
			boolean isReadonly, boolean byProposalId) {
		debugLogger("retrieveProposalChecklistDtls");
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		IQTProposalSO proposalSO;
		try {
			reqDTO.setUserId(userId);
			if(!byProposalId)	{reqDTO.setQtId(qtOrProposalId);}
			else				{reqDTO.setQtProposalId(qtOrProposalId);}			
			proposalSO = getProposalSO();
			ProposalResDTO resDTO = null; 
			if(!byProposalId)	{ resDTO = proposalSO.retrieveProposalDtlBySupplierId(reqDTO);}
			else				{ resDTO = proposalSO.retrieveProposalDetail(reqDTO);}

			ScQt scQt = resDTO.getScQt();
			 ScQtProposal scQtProposal=resDTO.getScQtProposal();
			getManagedBean().setQt(resDTO.getScQt());

			getManagedBean().setScQtDocReports(resDTO.getScQtDocReports());
			 
			getManagedBean().setQtProposal(scQtProposal);
			getManagedBean().setLastSubmittedDate(scQtProposal.getLastSubmitDate());
			getManagedBean().setSupplierId(resDTO.getSupplierId());
			// Technical Check List
						if (resDTO.getTechnicalCheckLists() != null) {
							getManagedBean().setTecCheckDetailsList(
									resDTO.getTechnicalCheckLists());
							checklistSorting(getManagedBean().getTecCheckDetailsList());
							debugLogger("Technical Check list: "
									+ resDTO.getTechnicalCheckLists().size());
						}

						// Financial Check List
						if (resDTO.getFinancialCheckLists() != null) {
							getManagedBean().setFinanceCheckDetailsList(
									resDTO.getFinancialCheckLists());
							checklistSorting(getManagedBean().getFinanceCheckDetailsList());
							if(resDTO.getFinancialCheckLists().size()>0){ //#28792-start
							this.getManagedBean().setFinancialAttchment(Boolean.TRUE);	
							} //#28792-End
							debugLogger("Financial Check list: "
									+ resDTO.getFinancialCheckLists().size());
						}
			if(resDTO.getTechnicalCheckLists().size()==0 || resDTO.getFinancialCheckLists().size()==0){
				//log.info("BackingBeanResDTO"+resDTO.getTechnicalCheckLists());
				reqDTO = new ProposalReqDTO();
				reqDTO.setTechnicalCount(resDTO.getTechnicalCheckLists().size());
				reqDTO.setFinancialCount(resDTO.getFinancialCheckLists().size());
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setSupplierId(resDTO.getSupplierId());
				reqDTO.setScQt(scQt);
				reqDTO.setScQtProposal(scQtProposal);
			ProposalResDTO	checklist = proposalSO.retrieveProposalChecklistDcoumentDtls(reqDTO);
			if(resDTO.getTechnicalCheckLists().size()==0 && checklist.getTechnicalCheckLists()!=null ){
				getManagedBean().setTecCheckDetailsList(
						checklist.getTechnicalCheckLists());
				checklistSorting(getManagedBean().getTecCheckDetailsList());
				debugLogger("TechnicalChecklistretrieveProposalChecklistDcoumentDtls: "
						+ checklist.getTechnicalCheckLists().size());
			}
			if(resDTO.getFinancialCheckLists().size()==0 && checklist.getFinancialCheckLists()!=null ){
				getManagedBean().setFinanceCheckDetailsList(
						checklist.getFinancialCheckLists());
				checklistSorting(getManagedBean().getFinanceCheckDetailsList());
				debugLogger("FinancialChecklistretrieveProposalChecklistDcoumentDtls: "
						+ checklist.getFinancialCheckLists().size());
			}
			}

			

			if (null != scQt.getClosingDate()) {
				Date closingDate = scQt.getClosingDate();
				PmStatus qtStatus = scQt.getQtStatus();
				if (DateUtils.getCurrentDate().after(closingDate)
						|| (qtStatus != null && !QTStatusConst.QT_PROPOSAL_PREPARATION
								.equals(qtStatus.getStatusId()))) {
					getManagedBean().setReadOnly(true);
					getManagedBean().setQtClosed(true);
					getManagedBean().setClosingTime("0");
				} else {
					if (scQtProposal.isSubmitted()) {
						getManagedBean().setReadOnly(true);
					} else {
						getManagedBean().setReadOnly(isReadonly);
					}
					
					//20190516@hatta - 2808 - Start
					showQtClosingTime();
					//20190516@hatta - 2808 - End
					
				}

			}

			if (!isReadonly) {
				// checking for proposal validity
				reqDTO.setScQt(scQt);
				getProposalSO().validateSupplierProposal(reqDTO);
			}

		} finally {
			proposalSO = null;
			reqDTO = null;
		}
	}
	
	//20190516@hatta - 2808 - Start	
	public void showQtClosingTime(){
		Date today = new Date();
		long days = DateUtils.getDaysDifference(today,getManagedBean().getQt().getClosingDate());
		
		DecimalFormat dF = new DecimalFormat("#00");
		//#15934 - start
		long durationInMills = DateUtils.getDateDiffInMillis(
				DateUtils.getCurrentDate(), getManagedBean()
						.getQt().getClosingDate());
	
		getManagedBean()
				.setClosingTime(dF.format(days)+" "
								+ ResourceBundleUtils
										.getMessage(
												"lbl.sc.days",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes()
														.getUserLocale()) + " " + formattedDurationBasedOnLocale(
																						durationInMills,
																						getDisplayLanguage()));
		
		if (days == 0) {
			//#15934 - end
			if (durationInMills > 0) {
				getManagedBean().setClosingTime(
						formattedDurationBasedOnLocale(
								durationInMills,
								getDisplayLanguage()));
			} else {
				getManagedBean().setClosingTime("0");
			}
		}
	}
	//20190516@hatta - 2808 - End

	/**
	 * Gets the display language.
	 * 
	 * @return the display language
	 */
	private String getDisplayLanguage() {
		FacesScopes facesScopes = new FacesScopes();
		try{
			String displayLanguage = facesScopes.getUserLocale().getDisplayLanguage();
			return displayLanguage;
		}finally{
			facesScopes = null;
		}
		
	}

	/**
	 * Formatted duration based on locale.
	 * 
	 * @param durationInMills
	 *            the duration in mills
	 * @param displayLanguage
	 *            the display language
	 * @return the string
	 */
	private String formattedDurationBasedOnLocale(long durationInMills,
			String displayLanguage) {
		DecimalFormat durationFormat = new DecimalFormat("#00");
		try{
			long diffMinutes = durationInMills / (60 * 1000) % 60;
			long diffHours = durationInMills / (60 * 60 * 1000) % 24;
			
			String duration = "";
			if (diffHours <= 24 && diffHours >= 2) {

				if (displayLanguage.equalsIgnoreCase("english")) {
					duration = durationFormat.format(diffHours) + " Hours  "
							+ durationFormat.format(diffMinutes) + " Minutes";
				} else {
					duration = durationFormat.format(diffHours) + " Jam  "
							+ durationFormat.format(diffMinutes) + " Minit";
				}
			}

			if (diffHours < 2 && diffHours >= 1) {

				if (displayLanguage.equalsIgnoreCase("english")) {
					duration = durationFormat.format(diffHours) + " Hour  "
							+ durationFormat.format(diffMinutes) + " Minutes";
				} else {
					duration = durationFormat.format(diffHours) + " Jam  "
							+ durationFormat.format(diffMinutes) + " Minit";
				}
			}
			if (diffHours < 1) {
				if (displayLanguage.equalsIgnoreCase("english")) {
					duration = durationFormat.format(diffMinutes) + " Minutes";
				} else {
					duration = durationFormat.format(diffMinutes) + " Minit";
				}
			}

			return duration;
		}finally{
			durationFormat = null;
		}
	}

	/**
	 * Listener method for tab change to retrieve Quotation/Tender Document
	 * Details
	 */
	public void retrieveQTDocumentDetails() {
		debugLogger("retrieveTechnicalProposalList supplierId::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setPrepareActiveIndex(0);
	}

	/**
	 * Listener method for tab change to retrieve Technical Proposal List
	 */
	public void retrieveTechnicalProposalList() {
		debugLogger("retrieveTechnicalProposalList supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setPrepareActiveIndex(1);
	}

	/**
	 * Listener method for tab change to retrieve Financial Proposal List
	 */
	public void retrieveFinancialProposalList() {
		debugLogger("retrieveFinancialProposalList supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setPrepareActiveIndex(2);
	}

	/**
	 * Listener method for tab change to retrieve Declaration details
	 * 
	 * @throws Exception
	 */
	public void retrieveDeclarationList() throws Exception {
		debugLogger("retrieveDeclarationList supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		VelocityContext context = new VelocityContext();
		try{
			this.getManagedBean().setPrepareActiveIndex(3);
			Date date = retriveDatesforOrgProfile(this.getManagedBean().getQt()
					.getPublishDate(), this.getManagedBean().getQt()
					.getCreatedDate());
			String ptjName = retrievePTJByOrgProfileId(this.getManagedBean()
					.getQt().getPmOrgProfile().getOrgProfileId(), date);
			debugLogger("date" + date);
			debugLogger("ptjName" + ptjName);
			context.put("ptjName", ptjName);
			String othersTermsConditions = DocumentGenerator.getInstance()
					.getHtmlFromVelocity(
							SourcingConstants.SUPPLIER_PROPOSAL_DECLARATION,
							context);
			getManagedBean().setOthersTermsConditions(othersTermsConditions);
		}finally{
			context = null;
		}
		
	}

	/**
	 * Method to retrieve integrity details
	 * 
	 * @throws Exception
	 */

	public void retrieveIntegrityDetails() throws Exception {
		debugLogger("retrieveIntegrityDetails supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setPrepareActiveIndex(4);
		generateDeclarationFromVelocity(getManagedBean().getQt());
	}

	/**
	 * Listener method for tab change to retrieve Quotation/Tender general info
	 * Details
	 */
	public void retrieveGeneralInfo() {
		debugLogger("retrieveGeneralInfo supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setPrepareActiveIndex(5);

		if (getManagedBean().getSmAppl() == null
				|| getManagedBean().getSmCompanyBasic() == null) {
			retrievePaidUpCapitalNdDeclarationDtls();
			if (getManagedBean().getSmAppl().getSmPersonnels() != null
					&& !getManagedBean().getSmAppl().getSmPersonnels()
							.isEmpty()) {
				for (SmPersonnel smContactperson : getManagedBean().getSmAppl()
						.getSmPersonnels()) {
					if (smContactperson.isContactPerson()) {
						this.getManagedBean().setContactPerson(smContactperson);
						break;
					}
				}
			}
		}
	}

	/**
	 * Listener method to download supplier proposal report
	 * @throws Exception
	 */
	public void downloadProposalReport() throws Exception {
		debugLogger("downloadProposalReport supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		String reportName = null;
		ScQt scQt = getManagedBean().getQt();
		ScAttachment reportAttachment = new ScQtProposalReportAttachment();
		StringBuilder fileName = new StringBuilder(getManagedBean()
				.getReportDownloadFileName());
		fileName.append(".pdf");
		reportAttachment.setFileName(fileName.toString());
		reportAttachment.setDocNo(scQt.getQtNo());
		reportAttachment.setPrefixFileName(getManagedBean().getQtProposal()
				.getProposalNo());
		ScAttachment attachment = (ScAttachment) reportAttachment;
		populateAttachmentDetails(attachment);
		reportName = ReportConst.SC_SUPPLIER_PROPOSAL_REPORT;

		Map<String, String> paramMap = new HashMap<String, String>();
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setScQt(scQt);
			paramMap.put("PKEY", SourcingConstants.ORACLE_ENCRYPTION_KEY);
			paramMap.put("QT_ID", getManagedBean().getQt().getQtId().toString());
			if (getManagedBean().getSmSupplier() == null) {
				retrievePaidUpCapitalNdDeclarationDtls();
			}
			paramMap.put("P_SUPP", getManagedBean().getSmSupplier()
					.getSupplierId().toString());

			super.generatePdfFromOracle(reportName, paramMap, attachment, true);
			downloadFile(attachment);
		} catch (Exception e) {
			log.error(e.getMessage());
		}finally{
			reportAttachment = null;
			fileName = null;
			paramMap.clear();
			paramMap = null;
		}
	}

	/**
	 * Populate proposal details to save.
	 * 
	 * @return the proposal ProposalReqDTO
	 */
	private ProposalReqDTO populateProposalDetailsTosave() {
		debugLogger("populateProposalDetailsTosave supplierId::::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		ProposalReqDTO reqDTO = null;
		FacesScopes facesScopes = new FacesScopes();
		try{
			List<ScQtProposalChecklist> technicalChecklist = getManagedBean()
					.getTecCheckDetailsList();
			List<ScQtProposalChecklist> financialChecklist = getManagedBean()
					.getFinanceCheckDetailsList();
			ScQtProposal scQtProposal = getManagedBean().getQtProposal();
			setUserDetails(scQtProposal);
			scQtProposal.setProposalSubmitBy(facesScopes.getUserSessionBean()
					.getUserId());
			reqDTO = new ProposalReqDTO();
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
			reqDTO.setPmUser(facesScopes.getUserSessionBean());
			reqDTO.setRoleCode(super.getUserRoleCode());

			return reqDTO;
		}finally{
			reqDTO = null;
			facesScopes = null;
		}

		
	}

	/**
	 * Method to submit the Supplier Proposal details.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void submitSupplierProposal(PmDigiSign digiSign) {
		debugLogger("submitSupplierProposal supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		
		long startTime = System.currentTimeMillis();
		
		validateProposalCloseTime(this.getManagedBean().getQt());
		if (getManagedBean().getSmSupplier() == null
				|| getManagedBean().getSmAppl() == null) {
			retrievePaidUpCapitalNdDeclarationDtls();
		}
		// #29134 - CR 27233: Static Code Analysis
		// start
		// initialized proposalSO outside of try to prevent nullpointerexception in catch clause
		// removed finally clause as it is not needed
		IQTProposalSO proposalSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);;
		ProposalReqDTO reqDTO = null;
		try {
//			proposalSO = BusinessDelegate.getInstance().getServiceObject(
//					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			reqDTO = populateProposalDetailsTosave();
			List<ScQtProposalChecklist> scQtProposalChecklists = new ArrayList<ScQtProposalChecklist>();
			scQtProposalChecklists.addAll(reqDTO.getTechnicalChecklist());
			scQtProposalChecklists.addAll(reqDTO.getFinancialChecklist());
			ScQtProposal scQtProposal = reqDTO.getScQtProposal();
			scQtProposal
					.setScQtProposalChecklists(new ArrayList<ScQtProposalChecklist>(
							scQtProposalChecklists));
			this.getManagedBean().setScQtPRPExperiences(null);
			String originalText = prepareOriginalText(getManagedBean()
					.getSmSupplier());
			byte[] signedData = getManagedBean().getSpkiBackingBean()
					.spkiSignTransaction(originalText);
			digiSign.setSpkiSignedData(signedData);

			if (null != digiSign) {
				updateSpkiDigiSignData(digiSign,
						ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL,
						scQtProposal.getQtProposalId(),
						scQtProposal.getProposalNo(), originalText);
			}
			
			// #27233 : CR add new tracking diary entry for update - START
			reqDTO.setResubmit(scQtProposal.getLastSubmitDate() != null);
			// #27233 : CR add new tracking diary entry for update - END

			RequestContext.getCurrentInstance().execute("blockWidget.hide();");
			scQtProposal.setLastSubmitDate(DateUtils.getCurrentDate());
			scQtProposal.setSubmitted(true);
			reqDTO.setScQtProposal(scQtProposal);
			reqDTO.setDigiSignData(digiSign);
            ///20191023@asyrafhaziq - Prevent Auto Score null - Start
			checkingForAutoScore(scQtProposal);
			//20191023@asyrafhaziq - Prevent Auto Score null - End	
			ProposalResDTO resDTO = proposalSO.submitSupplierProposal(reqDTO);
			if (resDTO.isSuccessful()) {
				String variable1 = getMessageCommon("lbl.sc.proposal.no");
				String variable2 = scQtProposal.getProposalNo();
				
				commonMessageAcknowledgementPage(MessageConst.INFO_700026, variable1, variable2);
								
				SLAUtils.getInstance().logSLAInfo(ModuleCodeConst.SUPPLIER_SELECTION, ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL, 
						"SPDetailBackingBean.submitSupplierProposal", startTime);
			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception e) {
			//Bug #18695 when exception happens while proposal submit update back as not submit
			getManagedBean().getQtProposal().setSubmitted(false);
			getManagedBean().getQtProposal().setLastSubmitDate(getManagedBean().getLastSubmittedDate());
			proposalSO.insertTrackingDiary(reqDTO);
			throw e;
		}
//		} finally {
//			proposalSO = null;
//			reqDTO = null;
//		}
		// end #29134 - CR 27233: Static Code Analysis
	}
	
	
	//20191023@asyrafhaziq - Prevent Auto Score null - Start
	public void checkingForAutoScore(ScQtProposal scQtProposal ) {
		log.info("enter checkingForAutoScore");
		List<ScQtProposalChecklist> scQtProposalChecklist = scQtProposal.getScQtProposalChecklists();
		IQTProposalSO proposalSO = null;	
		try {

			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			
			ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
			for (ScQtProposalChecklist proposalChecklist : scQtProposalChecklist )
			{
				proposalReqDTO.setScQtProposalChecklist(proposalChecklist);
				proposalReqDTO.setScQt(this.getManagedBean().getQt());
				proposalReqDTO.setSupplierId(getManagedBean().getSupplierId());
				proposalSO.saveAndCheckAutoScore(proposalReqDTO);
					
			}
		} finally {

			proposalSO = null;
		}
	}
	//20191023@asyrafhaziq - Prevent Auto Score null - End

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier) {
		debugLogger("prepareOriginalText supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";
		
		try{
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
			sb.append(getManagedBean().getQtProposal().getProposalNo()).append("|")
					.append(facesScopes.getUserSessionBean().getIdentificationNo())
					.append("|").append(smSupplier.getRegNo()).append("|")
					.append(countryCode);
			orginalText = sb.toString();
			return orginalText;
		}finally{
			facesScopes = null;
			sb = null;
			countryCode = null;
			orginalText = null;
		}
	}

	/**
	 * Common message acknowledgment page.
	 * 
	 * @param messageId
	 *            the message id
	 * @param variable1
	 *            the variable 1
	 * @param variable2
	 *            the variable 2
	 */
	private void commonMessageAcknowledgementPage(String messageId,
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
	 * Method to save financial non government company experiences..
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialNonGovCompanyExpAction(boolean isCompleted) {
		debugLogger("saveFinancialNonGovCompanyExpAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		List<ScQtPRPExperience> experiences = this.getManagedBean()
				.getScQtPRPExperiences();
		List<List<EncryptDataBean>> encryptExpriencList = new ArrayList<List<EncryptDataBean>>();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			int index = getManagedBean().getFinanceCheckDetailsList().indexOf(
					scQtProposalChecklist);
			CriteriaValue contractAmountVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CONTRACT_AMOUNT_FOR_NON_FED_GOV,
					this.getManagedBean().getTotalContractAmount());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), contractAmountVal);

			scQtProposalChecklist.setSubmitted(isCompleted);
			for (ScQtPRPExperience experience : experiences) {
				List<EncryptDataBean> experienceBeanList = new ArrayList<EncryptDataBean>();
				if (experience.getEffectiveDate() != null) {
					EncryptDataBean effDateBean = new EncryptDataBean();
					effDateBean
							.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE
									.toString());
					effDateBean.setColumnName(EncryptColumnName.EFF_DATE
							.toString());
					effDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy",
							experience.getEffectiveDate()));
					experienceBeanList.add(effDateBean);
					experience.setEffectiveDate(null);
				}
				if (experience.getExpiryDate() != null) {
					EncryptDataBean expDateBean = new EncryptDataBean();
					expDateBean
							.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE
									.toString());
					expDateBean.setColumnName(EncryptColumnName.EXP_DATE
							.toString());
					expDateBean.setValue(DateUtils.dateToString("dd-MMM-yyyy",
							experience.getExpiryDate()));
					experienceBeanList.add(expDateBean);
					experience.setExpiryDate(null);
				}
				if (experience.getContractAmt() != null) {
					EncryptDataBean ctAmtDateBean = new EncryptDataBean();
					ctAmtDateBean
							.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE
									.toString());
					ctAmtDateBean.setColumnName(EncryptColumnName.CONTRACT_AMT
							.toString());
					ctAmtDateBean.setValue(experience.getContractAmt()
							.toString());
					experienceBeanList.add(ctAmtDateBean);
					experience.setContractAmt(null);
				}
				encryptExpriencList.add(experienceBeanList);
			}

			ScQtProposal proposal = this.getManagedBean()
					.getFinancialQtProposalChecklist().getScQtProposal();
			proposal.setScQtProposalExperiences(experiences);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			ScQtProposalChecklist techncalProposalChecklist = null;
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
			reqDTO.setMultiEncryptBeanList(encryptExpriencList);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setDeletedPRPExperiences(null);
				ScQtProposal qtProPosal = resDTO.getScQtProposal();
				this.getManagedBean().setQtProposal(qtProPosal);
				debugLogger("*****save successfull*****"
						+ resDTO.isSuccessful());

				getManagedBean().getFinanceCheckDetailsList().set(index,
						resDTO.getProposalChecklist());
				this.getManagedBean().setFinancialQtProposalChecklist(
						resDTO.getProposalChecklist());
				if (!this.getManagedBean().isFinanicialOnlyCheckList()) {
					updateQtFinNdTechProposalCheckList(
							techncalProposalChecklist,
							resDTO.getTobeUpdateTechProposalChecklist(),
							SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				}
				if (isCompleted == true) {
					loadDataFromCache();
				}

				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} catch (Exception e) {
			handleSaveResetAfterExceptionForFinNonGovExp(experiences,
					encryptExpriencList);
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * Handle save reset after exception for technical non government
	 * experiences.
	 * 
	 * @param experiences
	 *            the experiences
	 * @param encryptExpriencList
	 *            the encrypt exprienc list
	 */
	private void handleSaveResetAfterExceptionForTechNonGovExp(
			List<ScQtPRPExperience> experiences,
			List<List<EncryptDataBean>> encryptExpriencList) {
		try {
			int size = experiences.size();
			if (size >= 1 && encryptExpriencList.size() == size) {
				for (int i = 0; i < size; i++) {
					List<EncryptDataBean> experienceEncryptBeans = encryptExpriencList
							.get(i);
					if (experienceEncryptBeans.size() == 2) {
						experiences.get(i).setEffectiveDate(
								DateUtils.convertStringToDate("dd-MMM-yyyy",
										experienceEncryptBeans.get(0)
												.getValue()));
						experiences.get(i).setExpiryDate(
								DateUtils.convertStringToDate("dd-MMM-yyyy",
										experienceEncryptBeans.get(1)
												.getValue()));
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Handle save reset after exception for financial non government
	 * experiences.
	 * 
	 * @param experiences
	 *            the experiences
	 * @param encryptExpriencList
	 *            the encrypt exprienc list
	 */
	private void handleSaveResetAfterExceptionForFinNonGovExp(
			List<ScQtPRPExperience> experiences,
			List<List<EncryptDataBean>> encryptExpriencList) {
		try {
			int size = experiences.size();
			if (size >= 1 && encryptExpriencList.size() == size) {
				for (int i = 0; i < size; i++) {
					List<EncryptDataBean> experienceEncryptBeans = encryptExpriencList
							.get(i);
					if (experienceEncryptBeans.size() == 3) {
						experiences.get(i).setEffectiveDate(
								DateUtils.convertStringToDate("dd-MMM-yyyy",
										experienceEncryptBeans.get(0)
												.getValue()));
						experiences.get(i).setExpiryDate(
								DateUtils.convertStringToDate("dd-MMM-yyyy",
										experienceEncryptBeans.get(1)
												.getValue()));
						experiences.get(i).setContractAmt(
								new BigDecimal(experienceEncryptBeans.get(2)
										.getValue()));
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Method to save technical specification details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechnicalSpecificationAction(boolean isCompleted) {
		log.info("saveTechnicalSpecificationAction supplierId::::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getTechnicalQtProposalChecklist();

		scQtProposalChecklist.setSubmitted(isCompleted);

		ProposalResDTO proposalResDTO = saveTechTemplateSpecDtls(isCompleted);
		//Bug #30073 Start
		if (isCompleted){
			checkDuplicateSupplierAnswerItemSpec(scQtProposalChecklist, proposalResDTO.getProposalChecklist());
		}
		//Bug #30073 End
		if (proposalResDTO.isSuccessful()) {
			return navigatePrepareTecProposalCheckList(scQtProposalChecklist,
					proposalResDTO.getProposalChecklist(), isCompleted);
		}

		return null;
	}

	/**
	 * Method to save financial specification details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialSpecificationAction(boolean isCompleted) {
		debugLogger("saveFinancialSpecificationAction supplierId::::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		ScQtProposalChecklist completedFinSpec = null;
		for (ScQtProposalChecklist finProposalChecklist : getManagedBean().getFinanceCheckDetailsList()) {
			if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION
					.equals(finProposalChecklist.getScQtCheckList().getDefinationType().getParameterCode()) &&
					finProposalChecklist.isSubmitted() &&
					!finProposalChecklist.equals(scQtProposalChecklist)) {
				completedFinSpec = finProposalChecklist;
				break;
			}
		}

		scQtProposalChecklist.setSubmitted(isCompleted);

		ProposalResDTO proposalResDTO = saveTemplateSpecDtls(completedFinSpec, isCompleted);
		//Bug #30073 Start 
		if (isCompleted){
			checkDuplicateSupplierAnswerItemSpec(scQtProposalChecklist, proposalResDTO.getProposalChecklist());
		}
		//Bug #30073 End		
		if (proposalResDTO.isSuccessful()) {
			return navigatePrepareFinProposalCheckList(scQtProposalChecklist,
					proposalResDTO.getProposalChecklist(), isCompleted);
		}

		return null;
	}

	/**
	 * Save template specification details.
	 * 
	 * @return the proposal res DTO
	 */
	private ProposalResDTO saveTemplateSpecDtls(ScQtProposalChecklist completedFinSpec, boolean isCompleted) {
		log.info("saveTempleateSpecDtls.completedFinSpec: " + completedFinSpec +":::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		IQTProposalSO proposalSO = null;
		ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
		ProposalResDTO proposalResDTO;
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			List<TplSpecAnswer> answers = null;

			if (getManagedBean().getQt().isZonal()) {

				if (getManagedBean().getSelectedZone() != null) {
					getManagedBean().getZoneTplSpecBeanMap().put(
							getManagedBean().getSelectedZone().getSpecZoneId(),
							getManagedBean().getTemplateSpecBean());
				}
				List<TemplateSpecBean> templateSpecBeans = new ArrayList<TemplateSpecBean>();
				List<TplSpecAnswer> tobeDeletedAnswers = new ArrayList<TplSpecAnswer>();
				List<TplSpecSuppAnswer> tobeDeletedSuppAnswers = new ArrayList<TplSpecSuppAnswer>();
				templateSpecBeans.addAll(getManagedBean()
						.getZoneTplSpecBeanMap().values());
				if (templateSpecBeans != null && templateSpecBeans.size() > 0) {
					answers = new ArrayList<TplSpecAnswer>();
					for (TemplateSpecBean templateSpecBean : templateSpecBeans) {
						TplSpecZoneDtl specZoneDtl=getKeysByValue(getManagedBean().getZoneTplSpecBeanMap(), templateSpecBean);//#28785						
						if (templateSpecBean.getSpecAnswerList() != null
								&& templateSpecBean.getSpecAnswerList().size() > 0) {
							List<TplSpecAnswer> toDeleteZoneAnswers = new ArrayList<TplSpecAnswer>();	
							for (TplSpecAnswer specAnswer : templateSpecBean
									.getSpecAnswerList()) {
//#21695 farid IN MONITORING START	
								
								if(null==	specAnswer.getTplSpecSuppAnswer().getTplSpecZoneDtl() || null==specAnswer.getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId()){ //#28785-Start
									specAnswer.getTplSpecSuppAnswer().setTplSpecZoneDtl(specZoneDtl);
								}	//#28785-End
								
								if (specAnswer.getRatePerUom() != null
										&& specAnswer.getRatePerUom().compareTo(BigDecimal.ZERO) > 0) {
									answers.add(specAnswer);
								
								}else if (specAnswer.getRatePerUom() != null
											&& specAnswer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) == 0) {
									answers.add(specAnswer);
								
								}else if (specAnswer.getRatePerUom() == null
										&& specAnswer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) == 0) {
								answers.add(specAnswer);
							
								}else if (specAnswer.getRatePerUom() == null
										&& specAnswer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) > 0) {
								answers.add(specAnswer);
							
								}else if (specAnswer.getRatePerUom() != null && specAnswer.getRatePerUom().compareTo(BigDecimal.ZERO) == 0
										&& specAnswer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) > 0) {
								answers.add(specAnswer);
							
								}else if (specAnswer.getRatePerUom() != null && specAnswer.getRatePerUom().compareTo(BigDecimal.ZERO) == 0
										&& specAnswer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) == 0) {
								answers.add(specAnswer);
//#21695 farid IN MONITORING END	
							}else{
									specAnswer.setDeleted(true);
									toDeleteZoneAnswers.add(specAnswer);
								}
							}
							if(!toDeleteZoneAnswers.isEmpty() && (toDeleteZoneAnswers.size() != templateSpecBean
									.getSpecAnswerList().size())){
								for(TplSpecAnswer answerNullPrice : toDeleteZoneAnswers){
										throw new EpException(MessageConst.ERROR_760464);
								}
								
							} else if (!toDeleteZoneAnswers.isEmpty()) {
								Set<TplSpecSuppAnswer> zoneSpecSuppAnswersSet = new HashSet<TplSpecSuppAnswer>();
								for(TplSpecAnswer specAnswer :toDeleteZoneAnswers){
									TplSpecSuppAnswer specSupplierAnswer = specAnswer.getTplSpecSuppAnswer();
									specSupplierAnswer.setDeleted(true);
									zoneSpecSuppAnswersSet.add(specSupplierAnswer);
								}
								tobeDeletedSuppAnswers.addAll(zoneSpecSuppAnswersSet);
								tobeDeletedAnswers.addAll(toDeleteZoneAnswers);
							}
							toDeleteZoneAnswers = null;
						}
					}
				}
				proposalReqDTO.setToBeRemovedSpecAnswers(tobeDeletedAnswers);
				proposalReqDTO.setToBeRemovedSpecSuppAnswers(tobeDeletedSuppAnswers);
			} else {
				answers = getManagedBean().getTemplateSpecBean()
						.getSpecAnswerList();
			}
            if(answers == null || answers.size() == 0){
            	for(TplSpecAnswer answer:answers){
// #21695 7/10/2021	START	            		
//farid start #21695 to allow item has 0 qty and 0 price
            		if(answer.getRatePerUom().equals(0) && answer.getTplSpecZoneQty()!= null || answer.getRatePerUom().equals(0) && answer.getTplSpecZoneQty() == null){
            			throw new EpException(MessageConst.ERROR_760463);
            		   }
//farid end #21695 7/10/2021
            		  }
            		} else {
            		   for(TplSpecAnswer answer:answers){
            			if (isCompleted) {
// #21695 7/10/2021	START
//farid start #21695 to allow item has 0 qty and 0 price
            		// bug #31733 add condition 5/11/2021
            			if (answer.getRatePerUom() == null && answer.getTplSpecZoneQty()!= null && answer.getTplSpecZoneQty().getQty().compareTo(BigDecimal.ZERO) > 0
            					|| answer.getRatePerUom() == null && answer.getTplSpecZoneQty() == null) {
            				throw new EpException(MessageConst.ERROR_760464);
            			}
            		// bug #31733 add condition 5/11/2021
//farid end #21695 7/10/2021
            		}
            		//added because of Bug #17772, please do not remove or change the order of if statements
            		if(answer.getAnswerNumeric() == null || answer.getAnswerNumeric().equals(0)){
            			boolean isItemTypeService = false; 
            			if(getManagedBean().getTemplateSpecBean().getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)){
            				isItemTypeService = true;
            			}
            			this.getManagedBean().getTemplateSpecBackingBean()
            			  .calcTotalAmount(getManagedBean().getTemplateSpecBean(),answer,isItemTypeService);
            		}
            		//20210419@asyrafhaziq - Bug #30061 - start
            		if ((answer.getRatePerUom() != null  && !answer.getRatePerUom().equals(0))&& 
            			(answer.getAnswerNumeric() != null && !answer.getAnswerNumeric().equals(0))) { // update for Bug #Bug #31134 
        				boolean isItemTypeService = false; 
            			if(getManagedBean().getTemplateSpecBean().getSpecMaster().getItemTypeCode().getParameterCode().equals(ParameterConst.ITEM_TYPE_SERVICE)){
            				isItemTypeService = true;
            			}	
        			   this.getManagedBean().getTemplateSpecBackingBean()
        			  .CheckAndUpdateCalcTotalAmount(getManagedBean().getTemplateSpecBean(),answer,isItemTypeService);
				     }
            		//20210419@asyrafhaziq - Bug #30061 - End
            		super.setUserDetails(answer);
            	}
            }
			TemplateSpecHelper tplHelper = TemplateSpecHelper.getInstance();
			boolean isPriceTypeTier = tplHelper.isPriceTypeTier(answers.get(0)
					.getTplSpecQuestion().getClPriceTypeCode()
					.getParameterCode());

			BigDecimal compareTotalAmount = new BigDecimal(99999999999.99);

			if (isPriceTypeTier) {
				BigDecimal quantity;
				if (answers.get(0).getTplSpecZoneQty() != null) {
					quantity = answers.get(0).getTplSpecZoneQty().getQty();
				} else {
					quantity = answers.get(0).getTplSpecQuestion()
							.getClQuantity();
				}

				for (TplSpecAnswer answer : answers) {
					//20200701@asyrafhaziq - Bug #28464 - Start
					BigDecimal totalPrice = tplHelper.calculateTotalAmount(
							answer.getTplSpecQuestion(),
							answer.getRatePerUom(), quantity);	
					if (totalPrice != null && totalPrice.compareTo(compareTotalAmount) == 1) {
						throw new EpException(MessageConst.ERROR_760437);
					}
					//20200701@asyrafhaziq - Bug #28464 - End
				}

			} else {
				for (TplSpecAnswer answer : answers) {
					if (answer.getAnswerNumeric() != null
							&& answer.getAnswerNumeric().compareTo(
									compareTotalAmount) == 1) {
						throw new EpException(MessageConst.ERROR_760437);
					} else if (answer.getTplSpecQuestion().getTplSpecMaster()
							.getTotalAmount() != null
							&& answer.getTplSpecQuestion().getTplSpecMaster()
									.getTotalAmount()
									.compareTo(compareTotalAmount) == 1) {
						throw new EpException(MessageConst.ERROR_760437);
					}
				}
			}
			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalReqDTO.setScQt(getManagedBean().getQt());
			proposalReqDTO.setCompletedProposalChecklist(completedFinSpec);
			proposalReqDTO.setSupplierId(getManagedBean().getSupplierId());
			
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} catch (EpException e) {
			scQtProposalChecklist.setSubmitted(false);
			throw e;
		} 
		catch (Exception e) {
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		}
		finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

	/**
	 * Save tech template spec dtls.
	 * 
	 * @return the proposal res DTO
	 */
	private ProposalResDTO saveTechTemplateSpecDtls(boolean isCompleted) {
		debugLogger("saveTempleateSpecDtls supplierId::::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		IQTProposalSO proposalSO = null;
		ProposalReqDTO proposalReqDTO = new ProposalReqDTO();
		ProposalResDTO proposalResDTO = new ProposalResDTO();
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean().getTechnicalQtProposalChecklist();
		ScQt scQt = getManagedBean().getQt();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTProposalSO.class);

			List<TplSpecAnswer> answers = getManagedBean().getTemplateSpecBean().getSpecAnswerList();

			if (isCompleted) {
				processResponseException(answers);
			}
			/** #21695 Farid */				
			if(scQt != null
					&& scQt.isZonal()){
				proposalReqDTO.setTplSpecZoneQties(answers.get(0).getTplSpecQuestion().getTplSpecZoneQties());
				}	
			
			proposalReqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			proposalReqDTO.setAnswersList(answers);
			proposalReqDTO.setScQt(this.getManagedBean().getQt());
			proposalResDTO = proposalSO.saveSpecDetails(proposalReqDTO);
		} catch (Exception e) {
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}
		return proposalResDTO;
	}

	private void processResponseException(List<TplSpecAnswer> answers) {
		if (answers == null) {
			throw new EpException(MessageConst.ERROR_760479);
		} else {
			log.info("%%%%% Template Spec Answer Size is :" + answers.size());
			if (answers.size() == 0) {
				throw new EpException(MessageConst.ERROR_760479);
			}

			for (TplSpecAnswer answer : answers) {
				TplSpecQuestion tplSpecQuestion = answer.getTplSpecQuestion();
				PmParameter answerType = tplSpecQuestion.getAnswerTypeCode();
				String answerTypeCode = answerType != null ? answerType.getParameterCode() : null;
//#21695 Farid allow complete if not offer, respond will be null and disabled		
				//start Bug #31750
				if ((ParameterConst.ANSWER_TYPE_TEXT.equalsIgnoreCase(answerTypeCode) && StringUtils.isEmpty(answer.getAnswerText()) && answer.getOffer() !=null && answer.getOffer() == true 
						||ParameterConst.ANSWER_TYPE_TEXT.equalsIgnoreCase(answerTypeCode) && StringUtils.isEmpty(answer.getAnswerText()) && answer.getOffer() == null)
					|| (ParameterConst.ANSWER_TYPE_DROPDOWN.equalsIgnoreCase(answerTypeCode) && answer.getAnswerCompliance() == null && answer.getOffer() !=null && answer.getOffer() == true
							|| (ParameterConst.ANSWER_TYPE_DROPDOWN.equalsIgnoreCase(answerTypeCode) && answer.getAnswerCompliance() == null && answer.getOffer() == null))
							|| ((ParameterConst.ANSWER_TYPE_NUMERIC.equalsIgnoreCase(answerTypeCode) && answer.getAnswerNumeric() == null && answer.getOffer() !=null && answer.getOffer() == true)
									   ||(ParameterConst.ANSWER_TYPE_NUMERIC.equalsIgnoreCase(answerTypeCode) && answer.getAnswerNumeric() == null && answer.getOffer() == null))) {
						throw new EpException(MessageConst.ERROR_760480);
				}
				//end Bug #31750
//#21695 end				
			}
		}
	}

	/**
	 * Method to save financial credit facilities details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialCreditFacilityAction(boolean isCompleted) {
		debugLogger("saveFinancialCreditFacilityAction supplierId::::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		IQTProposalSO proposalSO = null;
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		List<ScQtPRPCreditFacility> creditFacilitiesList = this
				.getManagedBean().getScQtPRPCreditFacilities();
		List<List<EncryptDataBean>> encryptCrditFacilitesList = new ArrayList<List<EncryptDataBean>>();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			CriteriaValue totalCreditVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CREDIT_FACILITIES,
					this.getManagedBean().getTotalPercentagePaid());

			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalCreditVal);
			scQtProposalChecklist.setSubmitted(isCompleted);
			for (ScQtPRPCreditFacility prpCreditFacilities : creditFacilitiesList) {

				if (prpCreditFacilities.isRecordStatus()) {
					List<EncryptDataBean> crditFacilitesList = new ArrayList<EncryptDataBean>();
					if (prpCreditFacilities.getCreditAmt() != null) {
						EncryptDataBean amtCredit = new EncryptDataBean();
						amtCredit
								.setTableName(EncryptTableName.SC_QT_PRP_CREDIT_FACILITY
										.toString());
						amtCredit.setColumnName(EncryptColumnName.CREDIT_AMT
								.toString());
						amtCredit.setValue(prpCreditFacilities.getCreditAmt()
								.toString());
						crditFacilitesList.add(amtCredit);
						prpCreditFacilities.setCreditAmt(null);
					}
					if (prpCreditFacilities.getAmtPaid() != null) {
						EncryptDataBean amtPaid = new EncryptDataBean();
						amtPaid.setTableName(EncryptTableName.SC_QT_PRP_CREDIT_FACILITY
								.toString());
						amtPaid.setColumnName(EncryptColumnName.AMT_PAID
								.toString());
						amtPaid.setValue(prpCreditFacilities.getAmtPaid()
								.toString());
						crditFacilitesList.add(amtPaid);
						prpCreditFacilities.setAmtPaid(null);
					}
					encryptCrditFacilitesList.add(crditFacilitesList);
				}
			}
			reqDTO.setMultiEncryptBeanList(encryptCrditFacilitesList);

			ScQtProposalChecklist finProposalChecklist = null;
			finProposalChecklist = findProposalChecklistByDocDefKey(
					getManagedBean().getFinanceCheckDetailsList(),
					ParameterConst.DOCUMENT_DEFINITION_KEY_FOR_CREDIT_FACILITY_UPLOAD);
			
			// #26339 - Start
			if (null != finProposalChecklist) {
				finProposalChecklist.setSubmitted(isCompleted);
			}
			// #26339 - End
			
			// #26339 - Start
			/*int index = getManagedBean().getFinanceCheckDetailsList().indexOf(
					finProposalChecklist);*/
			boolean removeAttachment = false;
			// #26339 - End
			
			if (isCompleted && finProposalChecklist!= null) {
				if (creditFacilitiesList == null
						|| creditFacilitiesList.isEmpty()) {
						
						// #26339 - Start
						//finProposalChecklist.setScQtProposalChecklistAttachment(null);
						removeAttachment = true;
						// #26339 - End
						finProposalChecklist.setSubmitted(true);
						finProposalChecklist
								.setCompliance(ParameterConst.COMPLIANCE_STATUS__COMPLIANCE);

				} else{
					if (finProposalChecklist.isSubmitted()){
						if (finProposalChecklist
								.getScQtProposalChecklistAttachment() == null) {
							finProposalChecklist.setSubmitted(false);
							finProposalChecklist
									.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
						} else {
							finProposalChecklist
									.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
						}
					}else{
						finProposalChecklist
						.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
					}
				}
			}

			scQtProposalChecklist
					.setScQtPRPCreditFacilities(creditFacilitiesList);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobeRemovedCreditFacilities(getManagedBean()
					.getToBeDeletedCreditFacilities());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());

			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				// #26339 - Start
				/*if (isCompleted) {*/
				
					// #26339 - Start
					int index = 0;
					for (ScQtProposalChecklist finCheckList : getManagedBean().getFinanceCheckDetailsList()) {
						if (finCheckList.getQtProposalChecklistId().longValue() == scQtProposalChecklist.getQtProposalChecklistId().longValue()) {
							getManagedBean().getFinanceCheckDetailsList().set(index,
									resDTO.getProposalChecklist());
							index = 0;
							break;
						}
						index++;
					}
					// #26339 - End
					
					reqDTO.setMultiEncryptBeanList(null);
					reqDTO.setTobeRemovedCreditFacilities(null);
					if(finProposalChecklist != null){
						reqDTO.setScQtProposalChecklist(finProposalChecklist);
						// #26339 - Start
						// #28398 BUG - 26339 CR - Start
						if (removeAttachment && null != finProposalChecklist.getScQtProposalChecklistAttachment()) {
							List<ScQtProposalChecklistAttachment> removeAttachmentList = new ArrayList<>();
							removeAttachmentList.add(finProposalChecklist.getScQtProposalChecklistAttachment());
							reqDTO.setTobeRemovedProposalChecklistAttachments(removeAttachmentList);
							finProposalChecklist.setScQtProposalChecklistAttachment(null);
						}
						// #28398 BUG - 26339 CR - End
						// #26339 - End
						ProposalResDTO resDto = proposalSO
								.saveProposalDetails(reqDTO);
						
						// #26339 - Start
						/*getManagedBean().getFinanceCheckDetailsList().set(index,
								resDto.getProposalChecklist());*/
						
						for (ScQtProposalChecklist finCheckList : getManagedBean().getFinanceCheckDetailsList()) {
							if (finCheckList.getQtProposalChecklistId().longValue() == finProposalChecklist.getQtProposalChecklistId().longValue()) {
								getManagedBean().getFinanceCheckDetailsList().set(index,
										resDto.getProposalChecklist());
								break;
							}
							index++;
						}
						// #26339 - End
					}
				/*}*/
				// #26339 - End
				getManagedBean().setToBeDeletedCreditFacilities(null);
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} catch (Exception e) {
			handleSaveResetAfterExceptionForFinCreditFacility(
					creditFacilitiesList, encryptCrditFacilitesList);
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * Handle save reset after exception for financial credit facilities.
	 * 
	 * @param creditFacilitiesList
	 *            the credit facilities list
	 * @param encryptExpriencList
	 *            the encrypt exprienc list
	 */
	private void handleSaveResetAfterExceptionForFinCreditFacility(
			List<ScQtPRPCreditFacility> creditFacilitiesList,
			List<List<EncryptDataBean>> encryptExpriencList) {
		try {
			int size = creditFacilitiesList.size();
			if (size >= 1 && encryptExpriencList.size() == size) {
				for (int i = 0; i < size; i++) {
					List<EncryptDataBean> experienceEncryptBeans = encryptExpriencList
							.get(i);
					if (experienceEncryptBeans.size() == 2) {
						creditFacilitiesList.get(i).setCreditAmt(
								new BigDecimal(experienceEncryptBeans.get(0)
										.getValue()));
						creditFacilitiesList.get(i).setAmtPaid(
								new BigDecimal(experienceEncryptBeans.get(1)
										.getValue()));
					}
				}
			}
		} catch (Exception e) {

		}
	}

	/**
	 * Method to save financial cash on hand details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialCashOnHandAction(boolean isCompleted) {
		debugLogger("saveFinancialCashOnHandAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		List<ScQtPRPCashOnHand> qtPRPCashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		List<EncryptDataBean> cashOnHandList = new ArrayList<EncryptDataBean>();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			CriteriaValue cashOnHandVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_CASH_ON_HAND, this
							.getManagedBean().getTotalcashHand());
			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), cashOnHandVal);
			scQtProposalChecklist.setSubmitted(isCompleted);
			scQtProposalChecklist.setScQtPRPCashOnHands(qtPRPCashOnHandList);
			for (ScQtPRPCashOnHand prpCashOnHand : qtPRPCashOnHandList) {
				if (prpCashOnHand.isRecordStatus()
						&& prpCashOnHand.getCashAmount() != null) {
					EncryptDataBean cashAmount = new EncryptDataBean();
					cashAmount
							.setTableName(EncryptTableName.SC_QT_PRP_CASH_ONHAND
									.toString());
					cashAmount.setColumnName(EncryptColumnName.CASH_AMT
							.toString());
					cashAmount.setValue(prpCashOnHand.getCashAmount()
							.toString());
					cashOnHandList.add(cashAmount);
				}
			}
			reqDTO.setEncryptDataList(cashOnHandList);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobecashOnHand(getManagedBean()
					.getToBeDeletedCashOnHandList());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				getManagedBean().setToBeDeletedCashOnHandList(null);
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} catch (Exception e) {
			if (qtPRPCashOnHandList != null && cashOnHandList != null) {
				int size = qtPRPCashOnHandList.size();
				if (cashOnHandList.size() == size) {
					for (int i = 0; i < size; i++) {
						qtPRPCashOnHandList.get(i)
								.setCashAmount(
										new BigDecimal(cashOnHandList.get(i)
												.getValue()));
					}
				}

			}
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}
		return null;
	}

	/**
	 * Method to save three months bank Statement details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveBankStatementAction(boolean isCompleted) {
		debugLogger("saveBankStatementAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		List<ScQtPRPBankStmt> prpBankStmts = this.getManagedBean()
				.getScQtPRPBankStmts();
		List<EncryptDataBean> encryptBankStmtList = new ArrayList<EncryptDataBean>();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			
			totalAmountSum(); //Bug #31360
			
			CriteriaValue avgBankStatementVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_AVERAGE_OF_BANK_STATEMENT,
					this.getManagedBean().getAvgAmount());
			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), avgBankStatementVal);
			scQtProposalChecklist.setSubmitted(isCompleted);
			scQtProposalChecklist.setPrpBankStmts(prpBankStmts);
			for (ScQtPRPBankStmt prpBankStmt : prpBankStmts) {
				if (prpBankStmt.getBalanceAmt() != null) {
					EncryptDataBean balanceAmt = new EncryptDataBean();
					balanceAmt
							.setTableName(EncryptTableName.SC_QT_PRP_BANK_STATEMENT
									.toString());
					balanceAmt.setColumnName(EncryptColumnName.BALANCE_AMT
							.toString());
					balanceAmt.setValue(prpBankStmt.getBalanceAmt().toString());
					encryptBankStmtList.add(balanceAmt);
					prpBankStmt.setBalanceAmt(null);
				}
			}
			reqDTO.setEncryptDataList(encryptBankStmtList);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);
			if (resDTO.isSuccessful()) {
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} catch (Exception e) {
			if (prpBankStmts != null && !prpBankStmts.isEmpty()) {
				int size = prpBankStmts.size();
				if (encryptBankStmtList != null
						&& encryptBankStmtList.size() == size) {
					for (int i = 0; i < size; i++) {
						prpBankStmts.get(i).setBalanceAmt(
								new BigDecimal(encryptBankStmtList.get(i)
										.getValue()));
					}
				}
			}
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}
		return null;
	}

	/**
	 * Method to save no of employees involved details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechEmployeesDtlAction(boolean isCompleted) {
		debugLogger("saveTechEmployessDtlAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		IQTProposalSO proposalSO = null;
		List<EncryptDataBean> encEmployeeList = null;
		ScQtPRPEmployee prpEmployee = getManagedBean().getScQtPRPEmployee();
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getTechnicalQtProposalChecklist();
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			CriteriaValue totalEmployeeVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_EMPLOYEE,
					BigDecimal.valueOf(getManagedBean().getScQtPRPEmployee()
							.getTotalEmployees()));
			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalEmployeeVal);
			scQtProposalChecklist.setSubmitted(isCompleted);
			if (prpEmployee != null && prpEmployee.getTotalEmployees() != 0) {

				encEmployeeList = new ArrayList<EncryptDataBean>();
				EncryptDataBean encEmployeeBean = new EncryptDataBean();
				encEmployeeBean
						.setTableName(EncryptTableName.SC_QT_PRP_EMPLOYEE
								.toString());
				encEmployeeBean.setColumnName(EncryptColumnName.TOTAL_EMPLOYEE
						.toString());
				encEmployeeBean.setValue(Integer.toString(prpEmployee
						.getTotalEmployees()));
				encEmployeeList.add(encEmployeeBean);
				prpEmployee.setTotalEmployees(0);
				reqDTO.setEncryptDataList(encEmployeeList);
			}
			scQtProposalChecklist.setScQtPRPEmployee(prpEmployee);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);
			if (resDTO.isSuccessful()) {
				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} catch (Exception e) {
			if (prpEmployee != null) {
				if (encEmployeeList != null && !encEmployeeList.isEmpty()) {
					prpEmployee.setTotalEmployees(Integer
							.parseInt(encEmployeeList.get(0).getValue()));
				}
			}
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * Method to save technical non government company experience details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveTechNonGovCompanyExpAction(boolean isCompleted) {
		debugLogger("saveTechNonGovCompanyExpAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		validateContractNo();
		List<ScQtPRPExperience> experiencesTosave = new ArrayList<ScQtPRPExperience>();
		List<List<EncryptDataBean>> encryptExpriencList = new ArrayList<List<EncryptDataBean>>();
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getTechnicalQtProposalChecklist();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			List<ScQtPRPExperience> experiences = this.getManagedBean()
					.getScQtPRPExperiences();
			int index = getManagedBean().getTecCheckDetailsList().indexOf(
					scQtProposalChecklist);
			CriteriaValue totalContractsVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_TOTAL_NO_OF_CONTRACTS_FOR_NON_FED_GOV,
					BigDecimal.valueOf(getManagedBean().getTotalContracts()));

			scQtProposalChecklist.setSubmitted(isCompleted);

			for (ScQtPRPExperience prpExperience : experiences) {
				if (prpExperience.getQtPrpExperienceId() == null
						|| prpExperience.getQtPrpExperienceId() == 0 
						      || (prpExperience.isBrandNew() == false && prpExperience.isEditable() == false) ) {
					experiencesTosave.add(prpExperience);
				}
			}
			if (CollectionUtils.isNotEmpty(experiencesTosave)) {
				for (ScQtPRPExperience experience : experiencesTosave) {
					List<EncryptDataBean> experienceBeanList = new ArrayList<EncryptDataBean>();
					if (experience.getEffectiveDate() != null) {
						EncryptDataBean effDateBean = new EncryptDataBean();
						effDateBean
								.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE
										.toString());
						effDateBean.setColumnName(EncryptColumnName.EFF_DATE
								.toString());
						effDateBean.setValue(DateUtils.dateToString(
								"dd-MMM-yyyy", experience.getEffectiveDate()));
						experienceBeanList.add(effDateBean);
						experience.setEffectiveDate(null);
					}
					if (experience.getExpiryDate() != null) {
						EncryptDataBean expDateBean = new EncryptDataBean();
						expDateBean
								.setTableName(EncryptTableName.SC_QT_PRP_EXPERIENCE
										.toString());
						expDateBean.setColumnName(EncryptColumnName.EXP_DATE
								.toString());
						expDateBean.setValue(DateUtils.dateToString(
								"dd-MMM-yyyy", experience.getExpiryDate()));
						experienceBeanList.add(expDateBean);
						experience.setExpiryDate(null);
					}
					encryptExpriencList.add(experienceBeanList);
				}
			}
			reqDTO.setMultiEncryptBeanList(encryptExpriencList);
			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), totalContractsVal);

			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setTobeRemovedExperience(getManagedBean()
					.getDeletedPRPExperiences());
			ScQtProposal qtProposal = this.getManagedBean()
					.getTechnicalQtProposalChecklist().getScQtProposal();
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
				// Encrypt the data & Save
				this.getManagedBean().setQtProposal(resDTO.getScQtProposal());

				updateQtFinNdTechProposalCheckList(
						finProposalChecklist,
						resDTO.getTobeUpdateProposalChecklist(),
						SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
				getManagedBean().getTecCheckDetailsList().set(index,
						resDTO.getProposalChecklist());
				getManagedBean().setTechnicalQtProposalChecklist(
						resDTO.getProposalChecklist());
				if (isCompleted == true) {
					loadDataFromCache();
				}

				return navigatePrepareTecProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}

		} catch (Exception e) {
			handleSaveResetAfterExceptionForTechNonGovExp(experiencesTosave,
					encryptExpriencList);
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}

		return null;
	}

	/**
	 * Method to save assets and liabilities details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	@SuppressWarnings("unused")
	public String saveAssetLiabilityAction(boolean isCompleted) {
		debugLogger("saveAssetLiabilityAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		// #26339 Venkat start
		ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
				.getFinancialQtProposalChecklist();
		List<EncryptDataBean> prpAssetLiabilityList = new ArrayList<EncryptDataBean>();
		ScQtPRPAssetLiability scQtPRPAssetLiability = this.getManagedBean()
				.getScQtPRPAssetLiability();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			CriteriaValue acidRatioVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO,
					scQtPRPAssetLiability.getAcidRatio());
			CriteriaValue currentRatioVal = new CriteriaValue(
					SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO,
					scQtPRPAssetLiability.getCurrentRatio());
			List<ScQtProposalScore> proposalScores = updateProposalScoresValue(
					scQtProposalChecklist, this.getManagedBean()
							.getScQtProposalScoreList(), acidRatioVal,
					currentRatioVal);
			scQtProposalChecklist.setSubmitted(isCompleted);
			
			ScQtProposalChecklist assetProposalChecklist = null;
			assetProposalChecklist = findProposalChecklistByGroupID(
					getManagedBean().getFinanceCheckDetailsList(),
					ParameterConst.DOCUMENT_DEFINITION_KEY_FOR_ASSET_LIABILITIES_UPLOAD,ParameterConst.ASSET_LIABILITIES_GROUP_ID);
			// #26339 - Start 
			if (null != assetProposalChecklist) {
				assetProposalChecklist.setSubmitted(isCompleted);
			}
			// #26339 - Start
			/*int index = getManagedBean().getFinanceCheckDetailsList().indexOf(
					assetProposalChecklist) - 1;*/
			boolean removeAttachment = false;
			// #26339 - End
			if (isCompleted && scQtPRPAssetLiability != null) {
				// #28398 BUG - 26339 CR - Start
				BigDecimal totalVal = new BigDecimal(0.00);
				if (null != scQtPRPAssetLiability.getCurrentAssets()) {
					totalVal = totalVal.add(scQtPRPAssetLiability.getCurrentAssets());
				}
				if (null != scQtPRPAssetLiability.getCurrentLiabilities()) {
					totalVal = totalVal.add(scQtPRPAssetLiability.getCurrentLiabilities());
				}
				if (null != scQtPRPAssetLiability.getTotalCashInHand()) {
					totalVal = totalVal.add(scQtPRPAssetLiability.getTotalCashInHand());
				}
				if (null != scQtPRPAssetLiability.getTotalAccountReceivable()) {
					totalVal = totalVal.add(scQtPRPAssetLiability.getTotalAccountReceivable());
				}
				
				if( ((null != totalVal && totalVal.compareTo(BigDecimal.ZERO) == 0)
								&& (null != scQtPRPAssetLiability.getAcidRatio() && scQtPRPAssetLiability.getAcidRatio().compareTo(BigDecimal.ZERO) == 0 )
								&& (null != scQtPRPAssetLiability.getCurrentRatio() && scQtPRPAssetLiability.getCurrentRatio().compareTo(BigDecimal.ZERO) == 0))) 
					 {
					// #26339 - Start
					//assetProposalChecklist.setScQtProposalChecklistAttachment(null);
					removeAttachment = true;
					// #26339 - End
					//start Bug #28887
					if (null != assetProposalChecklist) {
						assetProposalChecklist.setSubmitted(true);
						//this.getManagedBean().setReadOnly(true);
						assetProposalChecklist.setCompliance(ParameterConst.COMPLIANCE_STATUS__COMPLIANCE);
					}
					//end Bug #28887
					// #28398 BUG - 26339 CR - End
				}else{
					//start Bug #28887
					if (null != assetProposalChecklist) {					
						if (assetProposalChecklist.isSubmitted()){
							if (assetProposalChecklist.getScQtProposalChecklistAttachment() == null 
									|| (scQtPRPAssetLiability.getCurrentAssets() != null)
									|| (scQtPRPAssetLiability.getCurrentLiabilities() != null) 
								    || (scQtPRPAssetLiability.getTotalCashInHand() != null)
									|| (scQtPRPAssetLiability.getTotalAccountReceivable() != null)
									|| (scQtPRPAssetLiability.getAcidRatio() != null )
								    || (scQtPRPAssetLiability.getCurrentRatio() != null)) 
							{
								assetProposalChecklist.setSubmitted(false);
								assetProposalChecklist.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
								
							} else {
								assetProposalChecklist.setSubmitted(false);
								assetProposalChecklist.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
								
							}
						}else{
							assetProposalChecklist.setSubmitted(false);
							assetProposalChecklist.setCompliance(ParameterConst.COMPLIANCE_STATUS__NOTAPPLICABLE);
							
						}
					}//end Bug #28887
				}
			}      	// #26339 Venkat End
			// #26339 - End
			
			// #26339 - Start
			//Current Asset - Start
			EncryptDataBean currentAsset = new EncryptDataBean();
			currentAsset
					.setTableName(EncryptTableName.SC_QT_PRP_ASSET_LIABILITY
							.toString());
			currentAsset.setColumnName(EncryptColumnName.CURRENT_ASSETS
					.toString());
			if (null != scQtPRPAssetLiability.getCurrentAssets()) {
				currentAsset.setValue(scQtPRPAssetLiability.getCurrentAssets()
						.toString());
			} else {
				currentAsset.setValue(null);
			}
			prpAssetLiabilityList.add(currentAsset);
			scQtPRPAssetLiability.setCurrentAssets(null);
			// Current Asset - End
			
			// Current Liabilities - Start
			EncryptDataBean currentLiabilities = new EncryptDataBean();
			currentLiabilities
					.setTableName(EncryptTableName.SC_QT_PRP_ASSET_LIABILITY
							.toString());
			currentLiabilities
					.setColumnName(EncryptColumnName.CURRENT_LIABILITIES
							.toString());
			
			if (null != scQtPRPAssetLiability.getCurrentLiabilities()) {
				currentLiabilities.setValue(scQtPRPAssetLiability
						.getCurrentLiabilities().toString());
				prpAssetLiabilityList.add(currentLiabilities);
				reqDTO.setCurrentLiability(scQtPRPAssetLiability
						.getCurrentLiabilities().intValue());
			} else {
				currentLiabilities.setValue(null);
				prpAssetLiabilityList.add(currentLiabilities);
				reqDTO.setCurrentLiability(0);
			}
			scQtPRPAssetLiability.setCurrentLiabilities(null);
			// Current Liabilities - End
			// #26339 - End
			
			reqDTO.setEncryptDataList(prpAssetLiabilityList);
			scQtProposalChecklist.setPrpAssetLiabilities(scQtPRPAssetLiability);
			scQtProposalChecklist.setProposalScore(proposalScores);
			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);
			// #26339 Venkat start
			 
			if (resDTO.isSuccessful()) {
				// #26339 - Start
				/*if (isCompleted) {*/
					// #26339 - Start
					int index = 0;
					for (ScQtProposalChecklist finCheckList : getManagedBean().getFinanceCheckDetailsList()) {
						if (finCheckList.getQtProposalChecklistId().longValue() == scQtProposalChecklist.getQtProposalChecklistId().longValue()) {
							getManagedBean().getFinanceCheckDetailsList().set(index,
									resDTO.getProposalChecklist());
							index = 0;
							break;
						}
						index++;
					}
					// #26339 - End
					reqDTO.setMultiEncryptBeanList(null);
					reqDTO.setTobeRemovedCreditFacilities(null);
					if(assetProposalChecklist != null){
						reqDTO.setScQtProposalChecklist(assetProposalChecklist);
						// #26339 - Start
						// #28398 BUG - 26339 CR - Start
						if (removeAttachment && null != assetProposalChecklist.getScQtProposalChecklistAttachment()) {
							List<ScQtProposalChecklistAttachment> removeAttachmentList = new ArrayList<>();
							removeAttachmentList.add(assetProposalChecklist.getScQtProposalChecklistAttachment());
							reqDTO.setTobeRemovedProposalChecklistAttachments(removeAttachmentList);
							assetProposalChecklist.setScQtProposalChecklistAttachment(null);
						}
						// #28398 BUG - 26339 CR - End
						// #26339 - End
						ProposalResDTO resDto = proposalSO
								.saveProposalDetails(reqDTO);
						// #26339 - Start
						/*getManagedBean().getFinanceCheckDetailsList().set(index,
								resDto.getProposalChecklist());*/
						
						for (ScQtProposalChecklist finCheckList : getManagedBean().getFinanceCheckDetailsList()) {
							if (finCheckList.getQtProposalChecklistId().longValue() == assetProposalChecklist.getQtProposalChecklistId().longValue()) {
								getManagedBean().getFinanceCheckDetailsList().set(index,
										resDto.getProposalChecklist());
								break;
							}
							index++;
						}
						// #26339 - End
					}
			/*	}*/
				// #26339 - End
				// #26339 Venkat End	
				
		
	
				return navigatePrepareFinProposalCheckList(
						scQtProposalChecklist, resDTO.getProposalChecklist(),
						isCompleted);
			}
		} catch (Exception e) {
			if (scQtPRPAssetLiability != null && prpAssetLiabilityList != null
					&& prpAssetLiabilityList.size() == 2) {
				scQtPRPAssetLiability.setCurrentAssets(new BigDecimal(
						prpAssetLiabilityList.get(0).getValue()));
				scQtPRPAssetLiability.setCurrentLiabilities(new BigDecimal(
						prpAssetLiabilityList.get(1).getValue()));
			}
			scQtProposalChecklist.setSubmitted(false);
			throw new EpException(e.getMessage());
		} finally {
			proposalSO = null;
		}
		return null;
	}
	
	// #26339 - Start
	/**
	 * @param scQtPRPAssetLiability
	 */
	private void setDefaultValToAssetAndLiabilities(
			ScQtPRPAssetLiability scQtPRPAssetLiability) {
		if (null != scQtPRPAssetLiability) {
				
			if (null ==scQtPRPAssetLiability.getCurrentAssets()) {
				scQtPRPAssetLiability.setCurrentAssets(new BigDecimal(0));
			}
			if (null ==scQtPRPAssetLiability.getCurrentLiabilities()) {
				scQtPRPAssetLiability.setCurrentLiabilities(new BigDecimal(0));
			}
			if (null ==scQtPRPAssetLiability.getTotalCashInHand()) {
				scQtPRPAssetLiability.setTotalCashInHand(new BigDecimal(0));
			}
			if (null ==scQtPRPAssetLiability.getTotalAccountReceivable()) {
				scQtPRPAssetLiability.setTotalAccountReceivable(new BigDecimal(0));
			}
			if (null ==scQtPRPAssetLiability.getAcidRatio()) {
				scQtPRPAssetLiability.setAcidRatio(new BigDecimal(0));
			}
			if (null ==scQtPRPAssetLiability.getCurrentRatio()) {
				scQtPRPAssetLiability.setCurrentRatio(new BigDecimal(0));
			}
				
		}
	}
	// #26339 - End

	/**
	 * Method to save Paid Up Capital details
	 * 
	 * @param isCompleted
	 * @return navigation path
	 */
	public String saveFinancialPaidUpCapitalAction(boolean isCompleted) {
		debugLogger("saveFinancialPaidUpCapitalAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ProposalReqDTO reqDTO = new ProposalReqDTO();

			ScQtProposalChecklist scQtProposalChecklist = this.getManagedBean()
					.getFinancialQtProposalChecklist();

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
	 * Method to delete Technical Attachment Action
	 * 
	 * @param entity
	 *            the deleted attachment
	 */
	public void deleteProposalChecklistAttachmentTechnicalAction(BaseEntity deletedEntity) {
		IQTProposalSO proposalSO = null;
		debugLogger("deleteProposalChecklistAttachmentTechnicalAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			
			ScQtProposalChecklist scQtProposalChecklist=this.getManagedBean().getTechnicalQtProposalChecklist();
			
			
			//log.info("deleteProposalChecklistAttachmentTechnicalActiondocCategoryValue"+scQtProposalChecklist.getScQtCheckList().getDocCategory());

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			scQtProposalChecklist.setSubmitted(false);
			scQtProposalChecklist.setScQtProposalChecklistAttachment(null);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());

			List<ScQtProposalChecklistAttachment> attachments = new ArrayList<ScQtProposalChecklistAttachment>();
			attachments.add((ScQtProposalChecklistAttachment) deletedEntity);
			reqDTO.setTobeRemovedProposalChecklistAttachments(attachments);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				updateQtProposalCheckList(scQtProposalChecklist,
						resDTO.getProposalChecklist());
			}

		} finally {
			proposalSO = null;
		}
	}
	
	
	/**
	 * Method to delete Financial Attachment Action
	 * 
	 * @param entity
	 *            the deleted attachment
	 */
	public void deleteProposalChecklistAttachmentFinancialAction(BaseEntity deletedEntity) {
		IQTProposalSO proposalSO = null;
		debugLogger("deleteProposalChecklistAttachmentFinancialAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			
			ScQtProposalChecklist scQtProposalChecklist=this.getManagedBean().getFinancialQtProposalChecklist();
			
			
			//log.info("deleteProposalChecklistAttachmentFinancialActionDocCategoryValue"+scQtProposalChecklist.getScQtCheckList().getDocCategory());

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			scQtProposalChecklist.setSubmitted(false);
			scQtProposalChecklist.setScQtProposalChecklistAttachment(null);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());

			List<ScQtProposalChecklistAttachment> attachments = new ArrayList<ScQtProposalChecklistAttachment>();
			attachments.add((ScQtProposalChecklistAttachment) deletedEntity);
			reqDTO.setTobeRemovedProposalChecklistAttachments(attachments);
			ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

			if (resDTO.isSuccessful()) {
				updateQtProposalCheckList(scQtProposalChecklist,
						resDTO.getProposalChecklist());
			}

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Method to handle file upload events
	 * 
	 * @param event
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public void technicalhandleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("technicalhandleFileUpload supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		if (event.getFile() != null) {
			IQTProposalSO proposalSO = null;
			
		
			ScQtProposalChecklist scQtProposalChecklist=this.getManagedBean().getTechnicalQtProposalChecklist();
			
			
			
			//log.info("technicalhandleFileUploadDoccategoryValue"+scQtProposalChecklist.getScQtCheckList().getDocCategory());
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
						.getScQtProposalChecklistAttachment();
				if (null == attachment) {
					attachment = new ScQtProposalChecklistAttachment();
					attachment.setScQtProposalChecklist(scQtProposalChecklist);
				}
				attachment.setDocNo(getManagedBean().getQt().getQtNo());
				attachment.setPrefixFileName(getManagedBean().getQtProposal()
						.getProposalNo());

				uploadAttachmentWithEncryption(getEncryptionKey(),
						event.getFile(), attachment);
				
				//20191104@asyrafhaziq - Bug #26698 - problem when upload 
				if (getActualFilePath(attachment) != null && !getActualFilePath(attachment).isEmpty())
				{
				scQtProposalChecklist.setScQtProposalChecklistAttachment(attachment);
				scQtProposalChecklist.setSubmitted(true);

				reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
				reqDTO.setScQt(this.getManagedBean().getQt());
				ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

				if (resDTO.isSuccessful()) {
					updateQtProposalCheckList(scQtProposalChecklist,
							resDTO.getProposalChecklist());
					// RequestContext.getCurrentInstance().execute("checklistUploadDlg.hide();");
				}
			 }
			} catch (Exception e) {
				scQtProposalChecklist.setSubmitted(false);
				throw e;
			} finally {
				proposalSO = null;
			} 
		}
	}

	
	/**
	 * Method to handle file upload events
	 * 
	 * @param event
	 * @throws FileNotFoundException
	 * @throws IOException
	 */

	public void financialhandleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		debugLogger("financialhandleFileUpload supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		if (event.getFile() != null) {
			IQTProposalSO proposalSO = null;
		
			ScQtProposalChecklist scQtProposalChecklist=this.getManagedBean().getFinancialQtProposalChecklist();
			
			//log.info("financialhandleFileUploadDoccategoryValue"+scQtProposalChecklist.getScQtCheckList().getDocCategory());
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				ScQtProposalChecklistAttachment attachment = scQtProposalChecklist
						.getScQtProposalChecklistAttachment();
				if (null == attachment) {
					attachment = new ScQtProposalChecklistAttachment();
					attachment.setScQtProposalChecklist(scQtProposalChecklist);
				}
				attachment.setDocNo(getManagedBean().getQt().getQtNo());
				attachment.setPrefixFileName(getManagedBean().getQtProposal()
						.getProposalNo());

				uploadAttachmentWithEncryption(getEncryptionKey(),
						event.getFile(), attachment);

				//20191104@asyrafhaziq - Bug #26698 - problem when upload 
				if (getActualFilePath(attachment) != null && !getActualFilePath(attachment).isEmpty())
				{
					scQtProposalChecklist.setScQtProposalChecklistAttachment(attachment);
				/* As per CR Request #26339*/
					if(scQtProposalChecklist.getScQtCheckList().getTitle().contains("Annual Balance Sheet Kunci Kira-Kira Tahunan") ||
						scQtProposalChecklist.getScQtCheckList().getTitle().contains("Kunci Kira-Kira Tahunan (Nota - Untuk syarikat ROC, ini mestilah salinan yang telah diaudit)") ||
						scQtProposalChecklist.getScQtCheckList().getTitle().contains("Pengesahan dari Institusi Kewangan ke atas jumlah yang telah dibayar"))
					{
	
						scQtProposalChecklist.setCompliance(new Integer(2));
						log.info("SPDeatsilBackingBean Tile : "+scQtProposalChecklist.getScQtCheckList().getTitle());
						log.info("SPDetailsBackingBean 	 Value 2 is set for Compliance");
					}
				
				scQtProposalChecklist.setSubmitted(true);

				reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
				reqDTO.setScQt(this.getManagedBean().getQt());
				ProposalResDTO resDTO = proposalSO.saveProposalDetails(reqDTO);

				if (resDTO.isSuccessful()) {
					updateQtProposalCheckList(scQtProposalChecklist,
							resDTO.getProposalChecklist());
					// RequestContext.getCurrentInstance().execute("checklistUploadDlg.hide();");
				}
			}
			} catch (Exception e) {
				scQtProposalChecklist.setSubmitted(false);
				throw e;
			} finally {
				proposalSO = null;
			} 
		}
	}
	
	
	/**
	 * Method to handle file upload events with encryption
	 * 
	 * @param file
	 * @param attachment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void uploadAttachmentWithEncryption(String encryptionKey,
			UploadedFile file, ScAttachment attachment)
			throws FileNotFoundException, IOException {
		debugLogger("uploadAttachmentWithEncryption supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try{
			try {
				String fileName = file.getFileName();
				if (fileName.contains("\\")) {
					int lastIndex = fileName.lastIndexOf("\\");
					fileName = fileName.substring(lastIndex + 1, fileName.length());
				}
				// byte[] fileBytes = file.getContents();
				fis = (FileInputStream) file.getInputstream();
				attachment.setFileName(fileName);
				attachment.setFileDesc("File uploded");
				populateAttachmentDetails(attachment);
				fos = new FileOutputStream(getActualFilePath(attachment));
				// fos.write(fileBytes);
				BouncyCastleProvider_AES_CBC bc = new BouncyCastleProvider_AES_CBC(
						encryptionKey);
				bc.InitEncryptCipher();
				bc.CBCEncryptFile(fis, fos);
			} catch (Exception e) {
				throw new EpException(e.getLocalizedMessage());
			}

			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
					"The file was uploaded successfully.", null);

			FacesContext.getCurrentInstance().addMessage(null, message);
		}finally{
			if(fis!=null){
				fis.close();
			}
			if(fos!=null){
				fos.close();
			}
		}
		
	}

	/**
	 * To get the encryption key by quotation / tender id.
	 * 
	 * @return the encryption key
	 */
	public String getEncryptionKey() {
		debugLogger("getEncryptionKey supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		String encryptionKey = getManagedBean().getEncryptionKey();

		if (StringUtils.isBlank(encryptionKey)) {
			IQTProposalSO proposalSO = null;
			try {
				proposalSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTProposalSO.class);

				ProposalReqDTO reqDTO = new ProposalReqDTO();
				reqDTO.setScQt(this.getManagedBean().getQt());

				encryptionKey = proposalSO.getGeneratedEncrpyptedKey(reqDTO);
				getManagedBean().setEncryptionKey(encryptionKey);

			} finally {
				proposalSO = null;
			}
		}

		return encryptionKey;
	}

	/**
	 * Gets the proposal SO.
	 * 
	 * @return the proposal SO
	 */
	private IQTProposalSO getProposalSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
	}

	/**
	 * Action method for preparing proposal details for the technical check list
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String prepareTechnicalAction(ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareTechnicalAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		String pageUrl = null;
		String definationType = scQtProposalChecklist.getScQtCheckList().getDefinationType().getParameterCode();
		this.getManagedBean().setDocDefinitionKey(definationType);
		this.getManagedBean().setTechnicalQtProposalChecklist(scQtProposalChecklist);
		this.getManagedBean().setTechnicalQtChecklist(scQtProposalChecklist.getScQtCheckList());
		this.getManagedBean().getTemplateSpecBean().getSpecAnswerList();

		if (ParameterConst.DOCUMENT_DEFINITION_TYPE_TEMPLATE_SPECIFICATION.equalsIgnoreCase(definationType)) {

			pageUrl = retrieveTemplateSpecDtls(scQtProposalChecklist);

		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_WEB_FORM.equalsIgnoreCase(definationType)) {			
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList().getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);
			
			if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV.equalsIgnoreCase(docDefinitionKey)) {
				
				pageUrl = retrieveTecNonGovCompanyExpDtls(scQtProposalChecklist);
				
			} else if (SourcingConstants.DOC_DEF_KEY_NO_OF_EMPLOYEES.equalsIgnoreCase(docDefinitionKey)) {

				pageUrl = retrieveEmployeeDtls(scQtProposalChecklist);
				
			}
			
		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SAMPLE.equalsIgnoreCase(definationType)) {
			
			pageUrl = retrieveSampleDetails(scQtProposalChecklist);

		} else if (ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED.equalsIgnoreCase(definationType)) {			
			String docDefinitionKey = scQtProposalChecklist.getScQtCheckList().getPmDocument().getDocDefinitionKey();
			this.getManagedBean().setCheckListDefintionKey(docDefinitionKey);
			
			if (SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_FEDERAL_GOV.equalsIgnoreCase(docDefinitionKey)) {
				
				//Enhancement #18723 To Generate Company Experience from SM Module 	
				pageUrl = getTecCompanyExpDtls(scQtProposalChecklist); 
//				pageUrl = retrieveTecCompanyExpDtls(scQtProposalChecklist);
				//end of Enhancement #18723
				
			} else if (SourcingConstants.DOC_DEF_KEY_TECHNICAL_STAFF_LIST.equalsIgnoreCase(docDefinitionKey)) {
				
				pageUrl = retriveTechnicalStaffDtls(scQtProposalChecklist);
				
			}			
		}
		
		this.getManagedBean().setPageUrl(pageUrl);
		
		return NAV_TO_TECHNICAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";
	}

	/**
	 * Retrieve template specification details.
	 * 
	 * @param scQtProposalChecklist
	 *            the sc qt proposal checklist
	 * @return the string
	 */
	private String retrieveTemplateSpecDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveTechnicalTemplateSpecDtls:::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
		//#21695 Farid Start
		ScQt scQT = this.getManagedBean().getQt();
		//#21695 Farid End
		smSupplier.setSupplierId(getManagedBean().getSupplierId());
		templateSpecBean.setSupplier(smSupplier);
		//#21695 Farid Start
		templateSpecBackingBean.getManagedBean().setScQt(scQT);
		//#21695 Farid End
		templateSpecBackingBean.getManagedBean().setDocType(
				ParameterConst.DOCTYPE_QUOTATION);
		templateSpecBackingBean.retrieveTemplateSpecMaster(templateSpecBean);
//#21695 Farid Start 2/4/2021
		if (scQT.isZonal()) {
		populateZoneDetailIntoTreeNode(templateSpecBean);
		
		IQTProposalSO proposalSO = null;
		proposalSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
		ScQt scQt = getManagedBean().getQt();
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		reqDTO.setScQt(scQt);
		reqDTO.setQtSupplierId(getManagedBean().getSupplierId());
		reqDTO.setQtId(scQt.getQtId());
		// To retrieve financial specification details
		ProposalResDTO resDTO = proposalSO
				.retrieveTechnicalSpecZoneDtlsSupplier(reqDTO);
		
		List<TplSpecZoneDtl> zones = resDTO.getZones();
		if (CollectionUtils.isNotEmpty(zones)) {
			this.getManagedBean().setZones(zones);
			
			}
		}
//#21695 Farid end 2/4/2021
		return NAV_TO_TECHNICAL_STANDARD_PRODUCT_PAGE;
	}

	/**
	 * Method to retrieve technical non government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveTecNonGovCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
       debugLogger("retrieveTecNonGovCompanyExpDtls::::::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
			int totalContracts = 0;
			if (totalContractScore != null
					&& totalContractScore.getCriteriaValue() != null) {
				totalContracts = totalContractScore.getCriteriaValue()
						.intValue();
			}
			this.getManagedBean().setTotalContracts(totalContracts);
			return NAV_TO_NON_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Method to retrieve technical no of employees involved details
	 * 
	 * @param scQtProposalChecklist
	 * @return
	 */
	public String retrieveEmployeeDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveEmployeeDtls::::::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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

	/**
	 * Retrieve technical sample details.
	 * 
	 * @param scQtProposalChecklist
	 *            the proposal checklist
	 * @return the string
	 */
	private String retrieveSampleDetails(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveSampleDetails::::::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
	 * Method to retrieve technical staff details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retriveTechnicalStaffDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		 debugLogger("retriveTechnicalStaffDtls::::::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		List<TechnicalStaffBean> smPersonnelsList = new ArrayList<TechnicalStaffBean>();
		retrieveSupplierDtls();
		Map<Long, PmParameterDesc> qualificationMap = new HashMap<Long, PmParameterDesc>();
		for (PmParameterDesc parameterDesc : ParameterHelper.getInstance()
				.getParameterDescList(ParameterTypeConst.QUALIFICATION_LEVEL)) {

			qualificationMap.put(parameterDesc.getPmParameter()
					.getParameterId(), parameterDesc);
		}

		for (SmPersonnel smPersonnel : getManagedBean().getSmAppl()
				.getSmPersonnels()) {
			TechnicalStaffBean technicalStaffBean = new TechnicalStaffBean(
					smPersonnel);
			for (SmStaffAcademic staffAcademic : smPersonnel
					.getSmStaffAcademics()) {
				if(null!=staffAcademic.getQualificationLevelId()){
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
			}
			smPersonnelsList.add(technicalStaffBean);
		}
		getManagedBean().setSmPersonnelList(smPersonnelsList);
		getManagedBean().setTotalNoOfYearsExperience(smPersonnelsList.size());
		return NAV_TO_TECHNICAL_EMPL_LIST_PAGE;
	}

	/**
	 * Method to retrieve technical federal government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveTecCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
debugLogger("retrieveTecCompanyExpDtls supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
		return NAV_TO_FINANCIAL_CHECKLIST_MAIN_PAGE + "?faces-redirect=true";
	}

	/**
	 * Method to retrieve financial proposal details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String prepareFinancialAction(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("prepareFinancialAction:::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		String pageUrl = null;
		String definationType = scQtProposalChecklist.getScQtCheckList()
				.getDefinationType().getParameterCode();
		this.getManagedBean().setDocDefinitionKey(definationType);
		this.getManagedBean().setFinancialQtProposalChecklist(scQtProposalChecklist);

		this.getManagedBean().setFinancialQtChecklist(
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

				pageUrl = getFinCompanyExpDtls(scQtProposalChecklist);
				//pageUrl = retrieveFinCompanyExpDtls(scQtProposalChecklist);
				
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

	/**
	 * Retrieve financial specification details.
	 * 
	 * @param scQtProposalChecklist
	 *            the sc qt proposal checklist
	 * @return the string
	 */
	private String retrieveFinancialSpecDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveFinancialSpecDtls::::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		this.getManagedBean().setActiveIndex(0);
		this.getManagedBean().setFinancialQtProposalChecklist(scQtProposalChecklist);
		//REMOVE Bug #31542 
		IQTProposalSO proposalSO = null;

		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			ScQt scQt = getManagedBean().getQt();
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setScQt(scQt);
			reqDTO.setQtSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtId(scQt.getQtId());
			// To retrieve financial specification details
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
					TemplateSpecBean templateSpecbean=	getManagedBean().getTemplateSpecBean(); //#28785-start
					if(null!=templateSpecbean && null!=templateSpecbean.getSpecAnswerList() && templateSpecbean.getSpecAnswerList().size()>0){
					List<TplSpecAnswer> specAnswerList=	templateSpecbean.getSpecAnswerList();
						for(int i=0;i<specAnswerList.size();i++){	
							if(null==specAnswerList.get(i).getTplSpecSuppAnswer().getTplSpecZoneDtl() || null==specAnswerList.get(i).getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId()){ 
							specAnswerList.get(i).getTplSpecSuppAnswer().setTplSpecZoneDtl(getManagedBean().getSelectedZone());
							}
						}
						//REMOVE Bug #31733
						getManagedBean().getTemplateSpecBean().setSpecAnswerList(specAnswerList);
					} //#28785-End
				}
				//REMOVE Bug #31542 
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

	/**
	 * Retrieve template specification bean.
	 */
	private void retrieveTemplateSpecBean() {
        debugLogger("retrieveTemplateSpecBean supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		ScQtProposalChecklist qtProposalChecklist = getManagedBean()
				.getFinancialQtProposalChecklist();
		ScQtDoc scQtDoc = qtProposalChecklist.getScQtCheckList().getScQtDoc();
		this.getManagedBean().setScQtDoc(scQtDoc);

		TplSpecMaster specMaster = scQtDoc.getTplSpecMaster();
		//#29134 - CR 27233: Static Code Analysis
		//shaqimi start - removed try-finally clause as it is not needed
		ITemplateSpecSO templateSpecSO = null;
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
			
			//#11715 danial start
			for(TplSpecAnswer answer: resDTO.getSpecAnswerList()){
				//shaqimi start - merged if statement
				if(null!=answer.getTplSpecQuestion() &&
						null!=answer.getTplSpecQuestion().getClPriceTypeCode()){
					String code=answer.getTplSpecQuestion().getClPriceTypeCode().getParameterCode();
					if(code.equals(ParameterConst.PRICE_TYPE_TIER)||
					   code.equals(ParameterConst.PRICE_TYPE_PROMOTION_TIER)||
					   code.equals(ParameterConst.PRICE_TYPE_ZONAL_TIER)||
					   code.equals(ParameterConst.PRICE_TYPE_ZONAL_PROMOTION_TIER)||
					   code.equals(ParameterConst.PRICE_TYPE_TARIFF)||
					   code.equals(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)){
						this.getManagedBean().setTierType(true);
					}
				}
			}
			//#11715 danial end
			
			TemplateSpecBean templateSpecBean = new TemplateSpecBean();
			templateSpecBean.setSpecMaster(specMaster);
			templateSpecBean.setContentType(reqDTO.getContentType());
			templateSpecBean.setSpecAnswerList(resDTO.getSpecAnswerList());
			templateSpecBean.setSelectedSpecQuestion(null);
//#21695 Farid start(calculation rate per uom reset to 0 if partial offer				
			this.getManagedBean().getTemplateSpecBackingBean()
			.calcAllSpecAnswerTotalAmount(templateSpecBean);
//#21695 Farid end				
			this.getManagedBean().getTemplateSpecBackingBean()
					.calcGrandTotalAmount(templateSpecBean);
			if (getManagedBean().getQt().isZonal()) {
				getManagedBean().getZoneTplSpecBeanMap().put(
						getManagedBean().getSelectedZone().getSpecZoneId(),
						templateSpecBean);
			} else {
				getManagedBean().setTemplateSpecBean(templateSpecBean);
			}
			//shaqimi start - removed useless assignment to templateSpecSO
		}
		// end #29134 - CR 27233: Static Code Analysis
	}

	/**
	 * Gets the financial specification url.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @return the financial spec url
	 */
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
		debugLogger("retrieveFinNonGovCompanyExpDtls:::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());

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
				this.getManagedBean().setQtProposal(
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

	/**
	 * Find proposal check list by doc definition key.
	 * 
	 * @param proposalChecklists
	 *            the list of proposal check lists
	 * @param docDefKey
	 *            the doc definition key
	 * @return the proposal check list
	 */
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
	
	
	
	private ScQtProposalChecklist findProposalChecklistByGroupID( List<ScQtProposalChecklist> proposalChecklists, String docDefKey,String groupId) {
		ScQtProposalChecklist proposalCheckList = null;
        Integer  docGroupId= Integer.valueOf(groupId);
		if (null != proposalChecklists && StringUtils.isNotBlank(docDefKey)) {
			for (ScQtProposalChecklist prChecklist : proposalChecklists) {
				if (null != prChecklist.getScQtCheckList().getPmDocument()) {
					if ((docDefKey.equalsIgnoreCase(prChecklist.getScQtCheckList().getPmDocument().getDocDefinitionKey())) && (docGroupId.equals(prChecklist.getScQtCheckList().getPmDocument().getDocGroupId()) )) {
						proposalCheckList = prChecklist;
						break;
					}
				}
			}
		}
		return proposalCheckList;
	}

	/**
	 * Method to retrieve financial credit facilities details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveCreditFacilitiesDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveCreditFacilitiesDtls;;;supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		IQTProposalSO proposalSO = null;
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO
					.retrieveCreditFacilitiesDtls(reqDTO);

			this.getManagedBean().setScQtProposalScoreList(
					resDTO.getProposalScores());

			List<ScQtPRPCreditFacility> facilitiesList = resDTO
					.getCreditFacilityResultBean();

			this.getManagedBean().setScQtPRPCreditFacilities(facilitiesList);
			totalCreditFacilitiesSum();
			return NAV_TO_CREDIT_FACILITIES_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Method to retrieve financial Cash On Hand details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveCashOnHandDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveCashOnHandDtls:;;;supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
	 * Method to retrieve financial three months bank statement details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String retrieveBankStmtDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveBankStmtDtls::::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
	 * Method to retrieve financial Assets and Liabilities details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */

	public String retrieveAssetLiabilityDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
		debugLogger("retrieveAssetLiabilityDtls::::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
			
			// #26339 - Start
			setDefaultValToAssetAndLiabilities(assetLiability);
			// #26339 - End
			
			return NAV_TO_ASSET_LIABILITY_PAGE;

		} finally {
			proposalSO = null;
		}
	}

	/**
	 * Method to retrieve financial supplier limited details for paid up capital
	 * 
	 * @return navigation path
	 */
	public String retrievePaidUpCapitalNdDeclarationDtls() {
		debugLogger("*****retrievePaidUpCapitalNdDeclarationDtls*****"+getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		long supplierId = getManagedBean().getSupplierId();
		debugLogger("Supplier Id : " + supplierId);

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setSupplierId(supplierId);
			reqDTO.setApplId(getManagedBean().getQtProposal()
					.getSupplierApplId());
			ProposalResDTO resDTO = proposalSO
					.retrivePaidUpCapitalBySupplierById(reqDTO);
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			getManagedBean().setSmSupplier(resDTO.getSmSupplier());
		} finally {
			proposalSO = null;
		}

		return NAV_TO_PAID_UP_CAPITAL_PAGE;
	}

	/**
	 * Method to retrieve financial company experience with federal government
	 * details
	 * 
	 * @param scQtProposalChecklist
	 * @return
	 */
	public String retrieveFinCompanyExpDtls(
			ScQtProposalChecklist scQtProposalChecklist) {
        debugLogger("retrieveFinCompanyExpDtls::::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
			reqDTO.setScQt(this.getManagedBean().getQt());
			ProposalResDTO resDTO = proposalSO.retrieveCompanyExpDtls(reqDTO);
			List<ScLoa> list = resDTO.getLoaResultBean().getResultList();
			debugLogger(" List Size : " + list.size());
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
	 * Method to retrieve Supplier details
	 * 
	 * @return navigation path
	 */

	public String retrieveSupplierDtls() {
		Long supplierId = getManagedBean().getSupplierId();
		debugLogger("retrieveSupplierDtlsSupplier Id ::: " + supplierId+"QTID:::"+this.getManagedBean().getQt().getQtId());

		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setSupplierId(supplierId);
			reqDTO.setApplId(getManagedBean().getQtProposal()
					.getSupplierApplId());
			ProposalResDTO resDTO = proposalSO.retriveSupplierById(reqDTO);
			getManagedBean().setSmSupplier(resDTO.getSmSupplier());
			getManagedBean().setSmAppl(resDTO.getSmAppl());
			debugLogger("*****SmAppl Appl_Id : *****"
					+ resDTO.getSmAppl().getApplId());
			// To fetch and set Supplier GST info
			this.getManagedBean()
					.setSmGstSupplier(getGSTInfo(supplierId, null));
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
			if (getManagedBean().getSmAppl().getSmSupplierBanks() != null
					&& !getManagedBean().getSmAppl().getSmSupplierBanks()
							.isEmpty()) {
				getManagedBean().setSelectedBank(
						getManagedBean().getSmAppl().getSmSupplierBanks()
								.get(0));
				getManagedBean().setSmChequeSignatories(
						getManagedBean().getSmAppl().getSmSupplierBanks()
								.get(0).getSmChequeSignatories());
			}

			// TO set first branch
			if (this.getManagedBean().getSmAppl().getSmSupplierBranches() != null
					&& !this.getManagedBean().getSmAppl()
							.getSmSupplierBranches().isEmpty()) {
				this.getManagedBean()
						.setSmGstSupplierBranch(
								getGSTInfo(this.getManagedBean()
										.getSupplierId(), this.getManagedBean()
										.getSmAppl().getSmSupplierBranches()
										.get(0).getBranchCode()));
			}

			// staffInformation();
			debugLogger("Paid up capital : "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getPaidUpCapital());
			debugLogger("Authorized capital : "
					+ getManagedBean().getSmAppl().getSmCompanyBasic()
							.getAuthorizedCapital());
			debugLogger("Account Number : "
					+ getManagedBean().getSmAppl().getSmSupplierBanks().get(0)
							.getAccountNo());

		} finally {
			proposalSO = null;
		}

		return NAV_TO_SUPPLIER_DETAILS_PAGE;
	}

	/**
	 * Gets the GST info.
	 * 
	 * @param supplierId
	 *            the supplier id
	 * @param branchId
	 *            the branch id
	 * @return the GST info
	 */
	private SmGstSupplier getGSTInfo(Long supplierId, Long branchId) {
		SmGstSupplier smGstSupplier = null;
		ISupplierGstSO supplierGstSO = null;
		try {
			supplierGstSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
			SupplierGstReqDTO gstReqDTO = new SupplierGstReqDTO();
			gstReqDTO.setSupplierId(supplierId);
			gstReqDTO.setBranchCode(branchId);
			SupplierGstResDTO gstResDTO = supplierGstSO
					.retrieveGstInfoBySupplierIdAndBranchCode(gstReqDTO);
			smGstSupplier = gstResDTO.getSmGstSupplier();
		} finally {
			supplierGstSO = null;
		}
		return smGstSupplier;
	}

	/**
	 * Method to calculate total amounts from all Credit Facilities
	 */
	public void totalCreditFacilitiesSum() {
		debugLogger("totalCreditFacilitiesSum supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());

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

			//Start Bug #26498
			if (totalCredit.compareTo(BigDecimal.ZERO) == 1){
				percentagePaid = totalPaid.divide(totalCredit, mc).multiply(
						BigDecimal.valueOf(100));
			}
			//end Bug #26498
		}

		this.getManagedBean().setTotalAmountCredit(totalCredit);
		this.getManagedBean().setTotalAmountPaid(totalPaid);
		this.getManagedBean().setTotalPercentagePaid(percentagePaid);
	}

	/**
	 * Method to calculate total cash on hand.
	 */
	public void totalCashOnHandSum() {
		debugLogger("totalAmtPaidSum supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());

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
	 * Method to calculate total amount for three months bank statement check
	 * list
	 */
	public void totalAmountSum() {
		debugLogger("totalAmountSum supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());

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

		//#31746 - start
		if (bankStmtList.size() > 0 && totalAmount != null
				&& totalAmount.longValue() > 0) {
		//#31746 - end	
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
		int index = getManagedBean().getScQtPRPExperiences().indexOf(
				prpExperience);
		getManagedBean().getScQtPRPExperiences().set(index, prpExperience);
	}

	/**
	 * Listener method for expire date selection events
	 * 
	 * @param prpExperience
	 */
	public void expiryDateSelectListener(ScQtPRPExperience prpExperience) {

		int index = getManagedBean().getScQtPRPExperiences().indexOf(
				prpExperience);
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
	 * Method to calculate total Contract Amount
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
	 * Method to calculate acid ratio
	 */
	public void totalAcidRatio() {
		debugLogger("totalAcidRatio");

		ScQtPRPAssetLiability assetLiability = this.getManagedBean()
				.getScQtPRPAssetLiability();
		BigDecimal asset = assetLiability.getCurrentAssets();
		BigDecimal liabilities = assetLiability.getCurrentLiabilities();
		BigDecimal acidRatio = null;
		BigDecimal currentRatio = null;
		BigDecimal totalCashInHand = assetLiability.getTotalCashInHand();
		BigDecimal totalAccountReceivable = assetLiability
				.getTotalAccountReceivable();

		if (liabilities == null || liabilities.compareTo(BigDecimal.ZERO) <= 0) { //#28473
			// #26339 - Start
			assetLiability.setAcidRatio(new BigDecimal(0));
			assetLiability.setCurrentRatio(new BigDecimal(0));
			// #26339 - End
		} else {
			BigDecimal totalAmount;
			if (null != totalCashInHand && null != totalAccountReceivable) {
				totalAmount = totalCashInHand.add(totalAccountReceivable);
			} else if (null != totalCashInHand) {
				totalAmount = totalCashInHand;
			} else if (null != totalAccountReceivable) {
				totalAmount = totalAccountReceivable;
			} else {
				totalAmount = new BigDecimal(0);
			}
			MathContext mc = new MathContext(14);
			acidRatio = totalAmount.divide(liabilities, mc);
			assetLiability.setAcidRatio(acidRatio);

			if (asset != null) {
				currentRatio = asset.divide(liabilities, mc);
				assetLiability.setCurrentRatio(currentRatio);
			} else {
				assetLiability.setCurrentRatio(new BigDecimal(0));
			}

		}
	}

	/**
	 * Method to add Non Federal Government Experience to the list
	 */

	public void addNonFederalGovtExperience(String type) {
		debugLogger("addNonFederalGovtExperience supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		ScQtPRPExperience prpExperience = new ScQtPRPExperience();

		try {
			ScQtProposalChecklist proposalChecklist=null;
			if("1".equalsIgnoreCase(type)){
				proposalChecklist=getManagedBean().getTechnicalQtProposalChecklist();	
			}
			else if("2".equalsIgnoreCase(type)){
				proposalChecklist=getManagedBean().getFinancialQtProposalChecklist();	
			}
			List<ScQtPRPExperience> experienceList = this.getManagedBean()
					.getScQtPRPExperiences();
			if (null == experienceList) {
				experienceList = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : experienceList) {
				scQtPRPExperience.setBrandNew(false);
			}

			prpExperience.setScQtProposal(proposalChecklist.getScQtProposal());
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
	* Listener method to edit by row for list of Non Federal Government Experience
	*/
	public void editRowNonFederalGovtExperience(ScQtPRPExperience scQtPRPExperience) {
		scQtPRPExperience.setEditable(true);		
	}
		
	/**
	* Method to update Non Federal Government Experience to the list
	*/
	public void updateRowNonFederalGovtExperience(ScQtPRPExperience scQtPRPExperience) {
		List<ScQtPRPExperience> experienceList = this.getManagedBean()
		         .getScQtPRPExperiences();
		
		for(ScQtPRPExperience update :experienceList){
			  if(update.getQtPrpExperienceId().equals(scQtPRPExperience.getQtPrpExperienceId())){
			
					update.setClientName(scQtPRPExperience.getClientName());
					update.setContractTitle(scQtPRPExperience.getContractTitle());
					update.setContractNo(scQtPRPExperience.getContractNo());
					update.setEffectiveDate(scQtPRPExperience.getEffectiveDate());
					update.setExpiryDate(scQtPRPExperience.getExpiryDate());
					update.setRemark(scQtPRPExperience.getRemark());
				
					update.setBrandNew(false);
					update.setEditable(false);	
			  }
		}
		this.getManagedBean().setScQtPRPExperiences(experienceList);
	}

	/**
	 * Listener method to delete Non Federal Government Experience from the list
	 */
	public void deleteNonFederalGovtExperience() {
		debugLogger("deleteNonFederalGovtExperience supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		List<ScQtPRPExperience> experienceList = this.getManagedBean()
				.getScQtPRPExperiences();

		List<ScQtPRPExperience> tobeDeletedExperiences = getManagedBean()
				.getDeletedPRPExperiences();

		List<ScQtPRPExperience> deleteList = new ArrayList<ScQtPRPExperience>();
		if (experienceList != null) {

			if (tobeDeletedExperiences == null) {
				tobeDeletedExperiences = new ArrayList<ScQtPRPExperience>();
			}
			for (ScQtPRPExperience scQtPRPExperience : experienceList) {
				if (scQtPRPExperience.isSelected()) {

					if (scQtPRPExperience.getQtPrpExperienceId() != null
							&& scQtPRPExperience.getQtPrpExperienceId() != 0) {
						tobeDeletedExperiences.add(scQtPRPExperience);
					}
					deleteList.add(scQtPRPExperience);
				}
			}
			if (CollectionUtils.isNotEmpty(deleteList)) {
				experienceList.removeAll(deleteList);
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
	 * Method to add Credit Facility to the list
	 */
	public void addCreditFacilities() {
		debugLogger("addCreditFacilities supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
					.getFinancialQtProposalChecklist());
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
	 * Method to delete Credit Facility from the list
	 */
	public void deleteCreditFacilities() {
		debugLogger("deleteCreditFacilities supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		List<ScQtPRPCreditFacility> creditFacilitiesList = getManagedBean()
				.getScQtPRPCreditFacilities();

		List<ScQtPRPCreditFacility> toDeleteCreditFacilitiesList = getManagedBean()
				.getToBeDeletedCreditFacilities();

		if (creditFacilitiesList != null) {
			List<ScQtPRPCreditFacility> deleteFacilityList = new ArrayList<ScQtPRPCreditFacility>();
			if (toDeleteCreditFacilitiesList == null) {

				toDeleteCreditFacilitiesList = new ArrayList<ScQtPRPCreditFacility>();
			}

			for (ScQtPRPCreditFacility facility : creditFacilitiesList) {
				if (facility.isSelected()) {
					if (facility.getCreditFacilityId() != null
							&& facility.getCreditFacilityId() != 0) {
						toDeleteCreditFacilitiesList.add(facility);
					}
					deleteFacilityList.add(facility);
				}
			}
			creditFacilitiesList.removeAll(deleteFacilityList);
			getManagedBean().setToBeDeletedCreditFacilities(
					toDeleteCreditFacilitiesList);
			deleteFacilityList = null;
		}
		getManagedBean().setScQtPRPCreditFacilities(creditFacilitiesList);
		getManagedBean().setSelectedPRPCreditFacilities(null);
		getManagedBean().setSelectedAll(false);
		getManagedBean().setSelected(false);
		totalCreditFacilitiesSum();
	}

	/**
	 * Method to add cash on hand to the list
	 */
	public void addCashOnHand() {
		debugLogger("addCashOnHand supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		ScQtPRPCashOnHand cashOnHand = new ScQtPRPCashOnHand();
		List<ScQtPRPCashOnHand> cashOnHandList = this.getManagedBean()
				.getScQtPRPCashOnHand();
		if (null == cashOnHandList) {
			cashOnHandList = new ArrayList<ScQtPRPCashOnHand>();
		}
		cashOnHand.setAccountType(new PmParameter());
		cashOnHand.setScQtProposalChecklist(this.getManagedBean()
				.getFinancialQtProposalChecklist());
		for (ScQtPRPCashOnHand hand : cashOnHandList) {
			hand.setBrandNew(false);
		}
		cashOnHandList.add(cashOnHand);
		this.getManagedBean().setScQtPRPCashOnHand(cashOnHandList);
	}

	/**
	 * Method to delete cash on hand from the list
	 */
	public void deleteCashOnHand() {
		debugLogger("deleteCashOnHand supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
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
	 * Action method to navigate previous page
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
	 * Listener method for zone tab change
	 */
	public void zoneTabChange() {

		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		//REMOVE Bug #31542 
		getManagedBean().setActiveIndex(selectedZoneTabIndex);
		debugLogger("zoneTabChange");
		TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) getManagedBean()
				.getZones().get(selectedZoneTabIndex);
		debugLogger("Selected Zone : " + specZoneDtl.getZoneName());

		if (getManagedBean().getSelectedZone() != null) {
			getManagedBean().getZoneTplSpecBeanMap().put(
					getManagedBean().getSelectedZone().getSpecZoneId(),
					getManagedBean().getTemplateSpecBean());
		}
		getManagedBean().setSelectedZone(specZoneDtl);
		getManagedBean().setTemplateSpecBean(
				getManagedBean().getZoneTplSpecBeanMap().remove(
						specZoneDtl.getSpecZoneId()));
		TemplateSpecBean templateSpecbean=	this.getManagedBean().getTemplateSpecBean(); //#28785-start
		if(null!=templateSpecbean && null!=templateSpecbean.getSpecAnswerList() && templateSpecbean.getSpecAnswerList().size()>0){
			List<TplSpecAnswer> specAnswerList=	templateSpecbean.getSpecAnswerList();
			for(int i=0;i<specAnswerList.size();i++){
				if(null==specAnswerList.get(i).getTplSpecSuppAnswer().getTplSpecZoneDtl() || null==specAnswerList.get(i).getTplSpecSuppAnswer().getTplSpecZoneDtl().getSpecZoneId()){ 
				specAnswerList.get(i).getTplSpecSuppAnswer().setTplSpecZoneDtl(getManagedBean().getSelectedZone());
				}
			}
			//REMOVE Bug #31733
				getManagedBean().getTemplateSpecBean().setSpecAnswerList(specAnswerList);
			} //#28785-End
		//REMOVE Bug #31542 
	}

	/**
	 * Method to calculate maximum proposals allowed
	 */
	public void displayProposalDtls() {
		debugLogger("displayProposalDtls supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		List<ScQtProposal> list = this.getManagedBean().getQtProposalsList();

		list = new ArrayList<ScQtProposal>();
		if (getManagedBean().getPrepareProposal() > getManagedBean().getQt()
				.getMaxProposal()) {
			throw new EpException(MessageConst.ERROR_760332,
					new String[] { getManagedBean().getQt().getMaxProposal()
							+ "" });
		} else {
			for (int i = 0; i < this.getManagedBean().getPrepareProposal(); i++) {
				ScQtProposal proposal = new ScQtProposal();
				list.add(proposal);
			}
		}

		this.getManagedBean().setQtProposalsList(list);
	}

	/**
	 * Method to delete no of proposals from list
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
	 * Method to redirect Supplier Proposal Screen..
	 */
	public void preRenderView() {
		debugLogger("SpdetailBackingBeanpreRenderView");
		try {
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String qtId = originalServletRequest
					.getParameter(SourcingConstants.NAV_PARAM_QUOTATION_TENDER_ID);

			retriveSupplierProposalDtl(Long.parseLong(qtId), null, false);
			if(this.getManagedBean().isFinancialAttchment()){//#28792-start
			validateFinacialProposalAttachments(false);
			}//#28792-End
			
			//#29729 - Start
			if(displaySupplierBranchListDeleted()){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, null,
						MessageUtils.getMessage(MessageConst.INFO_760522, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			//#29729 - End

		} catch (EpException e) {
			String message = e.getMessage();

			if (MessageConst.ERROR_760195.equals(message)
					|| MessageConst.ERROR_760465.equals(message)) {

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

	
	public String retriveSupplierProposalDtl(Long qtId, String previousPageUrl,
			boolean isReadonly){
		return retriveSupplierProposalDtl(qtId, previousPageUrl, isReadonly, false);
	}	
	
	/**
	 * Get supplier proposal details.
	 * 
	 * @param qtOrProposalId
	 *            the quotation / tender id
	 *            or proposalId if byProposalId is true
	 *        
	 * @param previousPageUrl
	 *            the previous page url.
	 * @param isReadonly
	 *            the read only flag
	 * @param byProposalId -> accept qtId as proposalId if set to true
	 * @return the supplier proposal page
	 */
	public String retriveSupplierProposalDtl(Long qtOrProposalId, String previousPageUrl,
			boolean isReadonly, boolean byProposalId) {
		super.renewSesionObject();
		// ***25201	
		if(!byProposalId){
			retrieveProposalChecklistDtls(qtOrProposalId, new FacesScopes()
				.getUserSessionBean().getUserId(), isReadonly);
		}
		else{
			retrieveProposalChecklistDtls(qtOrProposalId, new FacesScopes()
			.getUserSessionBean().getUserId(), isReadonly, true);
		}
		
		if(this.isProposalSubmitted()){
			this.getManagedBean().setAgreeWithIntegrityPact(true);
			this.getManagedBean().setAgreeWithOthersTermsConditions(true);
		}
		getManagedBean()
				.setProcCategoryMedicine(
						super.isMultipleProposalCategory(getManagedBean()
								.getQt(), null));
		getManagedBean().setPrepareActiveIndex(0);
		getManagedBean().setPreviousPageUrl(previousPageUrl);

		return NAV_TO_PROPOSAL_MAIN_PAGE;
	}

	/**
	 * Method to navigate back to previous page
	 * 
	 * @return the previous page
	 */
	public String backAction() {
		return getManagedBean().getPreviousPageUrl();
	}

	private void loadDataFromCache() {

		retrieveProposalChecklistDtls(this.getManagedBean().getQt().getQtId(),
				new FacesScopes().getUserSessionBean().getUserId(), false);
	}

	/**
	 * Listener method for credit type change
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
	 * Listener method for account type change
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

	/**
	 * Generate declaration from velocity.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @return the string
	 * @throws Exception
	 *             the exception
	 */
	private String generateDeclarationFromVelocity(ScQt scQt) throws Exception {
		debugLogger("generateDeclarationFromVelocity");
		if (getManagedBean().getSmSupplier() == null
				|| getManagedBean().getSmSupplier().getCompanyName() == null) {
			retrievePaidUpCapitalNdDeclarationDtls();
		}

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		PmOrgProfile pmOrgProfile = scQt.getPmOrgProfile();
		SmSupplier smSupplier = getManagedBean().getSmSupplier();
		VelocityContext context = new VelocityContext();
		Date date = retriveDatesforOrgProfile(scQt.getPublishDate(),
				scQt.getCreatedDate());
		debugLogger("date" + date);
		String ptjName = retrievePTJByOrgProfileId(
				pmOrgProfile.getOrgProfileId(), date);
		debugLogger("ptjName" + ptjName);
		if (scQt != null) {
			String title = scQt.getQtTitle();
			if (title == null) {
				title = "";
			}
			title = title.replaceAll("\r\n", "<br />");
			title = title.replaceAll("&", "&#38;");
			title = title.replaceAll("<", "&#60;");
			title = title.replaceAll(">", "&#62;");
			context.put("title", title);
			context.put("qtNo", scQt.getQtNo());
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
			context.put("agency", ptjName);
		}

		String integrityPact = DocumentGenerator.getInstance()
				.getHtmlFromVelocity(
						SourcingConstants.SUPPLIER_PROPOSAL_INTEGRITY, context);

		getManagedBean().setIntegrityPact(integrityPact);

		return integrityPact;
	}

	/**
	 * Update proposal scores value.
	 * 
	 * @param proposalChecklist
	 *            the proposal check list
	 * @param proposalScores
	 *            the proposal scores
	 * @param criteriaValues
	 *            the criteria values
	 * @return the proposal score list
	 */
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

	/**
	 * The Class CriteriaValue.
	 */
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

	/**
	 * Find proposal score by criteria type.
	 * 
	 * @param criteriaType
	 *            the criteria type
	 * @return the ScQtProposalScore
	 */
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

	/**
	 * Navigate prepare technical proposal check list.
	 * 
	 * @param currProposalChecklist
	 *            the current proposal check list
	 * @param newProposalChecklist
	 *            the new proposal check list
	 * @param isCompleted
	 *            the is completed
	 * @return the string
	 */
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

	/**
	 * Navigate prepare fin proposal check list.
	 * 
	 * @param currProposalChecklist
	 *            the current proposal check list
	 * @param newProposalChecklist
	 *            the new proposal check list
	 * @param isCompleted
	 *            the is completed
	 * @return the string
	 */
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

	/**
	 * Update proposal check list.
	 * 
	 * @param currProposalChecklist
	 *            the current proposal check list
	 * @param newProposalChecklist
	 *            the new proposal check list
	 */
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

	/**
	 * Update technical proposal check list.
	 * 
	 * @param currProposalChecklist
	 *            the curr proposal check list
	 * @param newProposalChecklist
	 *            the new proposal check list
	 */
	private void updateQtTechProposalCheckList(
			ScQtProposalChecklist currProposalChecklist,
			ScQtProposalChecklist newProposalChecklist) {
		int index = getManagedBean().getTecCheckDetailsList().indexOf(
				currProposalChecklist);
		if (index >= 0) {
			getManagedBean().getTecCheckDetailsList().set(index,
					newProposalChecklist);
		}
		getManagedBean().setTechnicalQtProposalChecklist(newProposalChecklist);
	}

	/**
	 * Update fin and technical proposal check list.
	 * 
	 * @param currProposalChecklist
	 *            the current proposal check list
	 * @param newProposalChecklist
	 *            the new proposal check list
	 * @param finOrTech
	 *            the fin or technical
	 */
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
			getManagedBean().setFinancialQtProposalChecklist(newProposalChecklist);
		}
		if (finOrTech == SourcingConstants.DOC_DEF_KEY_TEC_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV) {
			int index = getManagedBean().getTecCheckDetailsList().indexOf(
					currProposalChecklist);
			if (index >= 0) {
				getManagedBean().getTecCheckDetailsList().set(index,
						newProposalChecklist);
			}
			getManagedBean().setTechnicalQtProposalChecklist(newProposalChecklist);
		}
		//getManagedBean().setQtProposalChecklist(newProposalChecklist);
	}

	/**
	 * Listener method for row selection events
	 * 
	 * @param event
	 */
	public void onRowSelectionListener(SelectEvent event) {
		SmSupplierBank bank = (SmSupplierBank) event.getObject();
		this.getManagedBean().setSelectedBank(bank);
		getManagedBean().setSmChequeSignatories(bank.getSmChequeSignatories());

	}

	/**
	 * Listener method for branch row selection events
	 * 
	 * @param event
	 */
	public void onBranchRowSelectionListener(SelectEvent event) {
		SmSupplierBranch smSupplierBranch = (SmSupplierBranch) event
				.getObject();
		this.getManagedBean().setSmGstSupplierBranch(
				getGSTInfo(getManagedBean().getSupplierId(),
						smSupplierBranch.getBranchCode()));
	}

	/**
	 * Listener method for multi boolean check box selection events
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
	 * Method to validate credit facilities
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

	/**
	 * Validate contract no.
	 */
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
	 * Action method to print label for sample submission
	 * 
	 * @throws Exception
	 */
	public void printLabelAction() {
		debugLogger("===printLabelAction");
		RequestContext.getCurrentInstance().execute(
				"supplierSampleLabelDlg.show();");
	}

	/**
	 * @return the sampleLabelStreamContent
	 */
	public StreamedContent getSampleLabelStreamContent() throws Exception {
		if (sampleLabelStreamContent == null) {
			ScSlAttachment slAttachment = new ScSlAttachment();
			slAttachment.setDocNo(getManagedBean().getQt().getQtNo());
			generateSampleLabelsFromVelocityToPdf(slAttachment,
					getManagedBean().getQt());
			sampleLabelStreamContent = getAttachmentStreamContent(slAttachment,
					CommonConstants.FILE_CONTENT_TYPE_PDF);
		}
		return sampleLabelStreamContent;
	}

	/**
	 * Generate sample labels from velocity to pdf.
	 * 
	 * @param attachment
	 *            the attachment
	 * @param scQt
	 *            the sc qt
	 * @throws Exception
	 *             the exception
	 */
	private void generateSampleLabelsFromVelocityToPdf(ScAttachment attachment,
			ScQt scQt) throws Exception {

		attachment.setFileName("sample Labels.pdf");
		populateAttachmentDetails(attachment);
		// CommonUtils commonUtils = new CommonUtils();

		VelocityContext context = new VelocityContext();
		debugLogger("======currentstatusid generateSampleLabelsFromVelocityToPdf===="
				+ this.getManagedBean().getCurrentStatusId());
		Date date = retriveDatesforOrgProfile(scQt.getPublishDate(),
				scQt.getCreatedDate());
		String ptjName = retrievePTJByOrgProfileId(scQt.getPmOrgProfile()
				.getOrgProfileId(), date);
		debugLogger("=======ptjName=====" + ptjName);
		//20190524@ Bug #25860 - Start
		List<ScQtSampleItem> scQtSampleItems=  new ArrayList<ScQtSampleItem>();
		ScQtSampleLable sampleLable = getManagedBean().getScQtSampleLable();
		ScQtSampleItem sampleItem ;
		ScRequestItem scRequestItem ;
	    for (ScQtSampleItem subSampleItem : sampleLable.getSampleItems() )
		  { sampleItem = new ScQtSampleItem();
		    scRequestItem = new ScRequestItem();
			String itemName = subSampleItem.getScRequestItem().getItemName();
			if (itemName == null) {
				itemName = "";
			}
			itemName = itemName.replaceAll("<", "&#60;");
			itemName = itemName.replaceAll(">", "&#62;");
			itemName = itemName.replaceAll("\u2264", "&#60;=");//symbol less then equal
			itemName = itemName.replaceAll("\u2265", "&#62;=");//symbol greater then equal
			itemName = itemName.replaceAll("\r\n", "<br/>");
			scRequestItem.setItemName(itemName);
			sampleItem.setScRequestItem(scRequestItem);
			sampleItem.setSubLableNo(subSampleItem.getSubLableNo());
			scQtSampleItems.add(sampleItem);
		  }
		context.put("sampleItems",scQtSampleItems);
		//20190524@ Bug #25860 - End
		context.put("lableNo", getManagedBean().getScQtSampleLable()
				.getLableNo());
		context.put("PTJCode", scQt.getPmOrgProfile().getPmOrgValidity()
				.getOrgCode());
		context.put("PTJName", ptjName);
		context.put("QTNo", scQt.getQtNo());
		String title = scQt.getQtTitle();
		if (title == null) {
			title = "";
		}
		title = title.replaceAll("&", "&#38;");
		title = title.replaceAll("<", "&#60;");
		title = title.replaceAll(">", "&#62;");
		title = title.replaceAll("\r\n", "<br />");
		context.put("QTTitle", title);

		DocumentGenerator.getInstance().generatePdfFromVelocity(
				SourcingConstants.SUPPLIER_PROPOSAL_PRINT_LABEL, context,
				new FileOutputStream(getActualFilePath(attachment)));
	}

	/**
	 * Populate financial spec details by zone.
	 */
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
					// zoneItem.setAjax(true);
					zoneItem.setTransient(true);
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem.setUpdate("@this");
					zoneItem.setOnstart("blockWidget.show();");
					zoneItem.setOncomplete("zoneTabChange([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]); blockWidget.hide();");
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
		debugLogger("** download Report  in Proposal Preparation**");
		downloadFile(scQtDocReport.getScQtDocReportAttachment());
	}

	/**
	 * Download the documents from repository on click of download all button
	 * 
	 * @throws Exception
	 */
	public void downloadAllQTDocuments() throws Exception {

		List<ScQtDocReport> scQtDocReports = this.getManagedBean()
				.getScQtDocReports();
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		if (null != scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport scQtDocReport : scQtDocReports) {
				ScQtDocReportAttachment attachment = scQtDocReport
						.getScQtDocReportAttachment();
				
				//start Bug #26416
				String docDefinitionKey = scQtDocReport.getPmDocument()!=null? scQtDocReport.getPmDocument().getDocDefinitionKey() : " ";
				//end Bug #26416
				
				if (attachment != null
						&& SourcingConstants.DOC_DEF_KEY_CONTRACT_COPY_SAMPLE
								.equals(docDefinitionKey)) {
					String custFileName = getDocTitleAsFileName(
							(scQtDocReports.indexOf(scQtDocReport) + 1),
							scQtDocReport.getTitle(), attachment.getFilePath());
					if (custFileName != null) {
						attachment.setFileName(custFileName);
					}
				}
				scAttachments.add(attachment);
			}
			if (!scAttachments.isEmpty()) {
				downloadAllFiles("NGeP-QT-Documents", scAttachments);
			} else {
				throw new EpException("Attachment not found.");
			}
		}

	}

	/**
	 * Action method for Spki sign.
	 */
	public void spkiSignAction() {
		debugLogger("spkiSignAction supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		validateProposalCloseTime(this.getManagedBean().getQt());
		retrievePaidUpCapitalNdDeclarationDtls();
		IQTProposalSO proposalSO = null;
		try {
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
			//for Bug #25537	
			checkSupplierQualificationStatus();
			proposalSO
					.validateProposalDetailsBeforeSPkiSign(populateProposalDetailsTosave());
			validateFinacialProposalAttachments(true);//#28792
			//#21695 Farid start 1/4/2021
			validateTechSuppNotOfferAll();
			//#21695 Farid end 1/4/2021			
			this.getManagedBean().getSpkiBackingBean().getChallengeQuestions();
		} finally {
			proposalSO = null;
		}
		this.getManagedBean().setAutoUpdate(false);
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");
	}
	//2010529@#25537 - Start
	/**
	 * Verify whether Mofno is valid or not.
	 */
	public void checkSupplierQualificationStatus() {
		
		String mofNo = this.getManagedBean().getQtProposal().getMofNo();
		IQTNoticeBoardSO boardSO = null;
		if (null == mofNo || "".equalsIgnoreCase(mofNo)) {
			throw new EpException(MessageConst.ERROR_760297);
		}
		ScQt scQt = this.getManagedBean().getQt();
		final Long qtId = scQt.getQtId();
		try {
			boardSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTNoticeBoardSO.class);
			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			reqDTO.setMofNo(mofNo);
			reqDTO.setQtId(qtId);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			NoticeBoardResDTO resDTO = boardSO
					.checkSupplierQualificationStatus(reqDTO);
			if (null != resDTO.getQualificationBean()) {
				for (SupplierQualificationBean qualificationBean : resDTO
						.getQualificationBean()) {
					if ((!SourcingConstants.SUPPLIER_CRITERIA_TYPE_SOFT_CERTIFICATE
							.equalsIgnoreCase(qualificationBean
									.getCriteria()))
							&& !qualificationBean.isSupplierStatus()) {
						String formattedDate = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT).format(DateUtils.getCurrentDate().getTime());	
						throw new EpException(MessageConst.ERROR_760484, 
								 new String[]{formattedDate});
					}
				}
			}
		} finally {
			boardSO = null;
		} 
	}
	//2010529@#25537 - End
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
	}

	/**
	 * Get SmMofAccount By mof account number
	 * 
	 * @param mofNo
	 *            the mof account number
	 * @return the SmMofAccount instance
	 */
	public SmMofAccount getSmMofAccountByMofNo(String mofNo) {
		debugLogger("getSmMofAccountByMofNo"+this.getManagedBean().getSupplierId());
		SmMofAccount mofAccount = null;

		if (StringUtils.isNotBlank(mofNo)) {
			IMofAccountSO mofAccountSO;
			try {
				mofAccountSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofAccountSO.class);
				MofAccountReqDTO reqDTO = new MofAccountReqDTO();
				reqDTO.setMofNo(mofNo);
				mofAccount = mofAccountSO.retriveMofAccountByMofNo(reqDTO)
						.getMofAccount();
			} finally {
				mofAccountSO = null;
			}
		}

		if (mofAccount != null) {
			return mofAccount;
		} else {
			return new SmMofAccount();
		}
	}

	/**
	 * Check whether Date is null or not
	 * 
	 * @param publishDate
	 * @param createdDate
	 * @return Date
	 */
	public Date retriveDatesforOrgProfile(Date publishDate, Date createdDate) {
		if (publishDate == null) {
			debugLogger("===========createdDate=====" + createdDate);
			return createdDate;
		}
		debugLogger("===========publishDate=====" + publishDate);
		return publishDate;

	}

	/**
	 * Method to sort checklist's as per display sequence order
	 * @param checkList
	 */
	private void checklistSorting(List<ScQtProposalChecklist> checkList) {
		Collections.sort(checkList, new Comparator<ScQtProposalChecklist>() {
			public int compare(ScQtProposalChecklist checklist1,
					ScQtProposalChecklist checklist2) {

				return new CompareToBuilder()
						.append(checklist1.getScQtCheckList().getChecklistSeq(),
								checklist2.getScQtCheckList().getChecklistSeq())
						.append(checklist1.getScQtCheckList().getCreatedDate(),
								checklist2.getScQtCheckList().getCreatedDate())
						.toComparison();
			}
		});
	}
	
    //Bug #28326 start
	private void validateProposalCloseTimeForUpdateButton(ScQt qt){
		Calendar closeTime = Calendar.getInstance();
		closeTime.setTime(qt.getClosingDate());
		closeTime.set(Calendar.SECOND,0);
		closeTime.set(Calendar.MILLISECOND,0);
		if (DateUtils.getCurrentDate().after(closeTime.getTime())) {
			
			this.getManagedBean().setQtClosed(true);
			
			StringBuilder qtSB = new StringBuilder();
			qtSB.append(getParameterDesc(qt.getProcurementMode()
					.getParameterType(), qt.getProcurementMode()
					.getParameterCode()));
			qtSB.append(" - ");
			qtSB.append(qt.getQtNo());
			SimpleDateFormat closeDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATETIME_FORMAT);
			throw new EpException(MessageConst.ERROR_760461, new String[] {
					qtSB.toString(),
					closeDateFormat.format(qt.getClosingDate()).toString() });
		}
	}
	//Bug #28326 end	

	/**
	 * validation method for proposal close time
	 * @param qt
	 */
	private void validateProposalCloseTime(ScQt qt) {
		Calendar closeTime = Calendar.getInstance();
		closeTime.setTime(qt.getClosingDate());
		closeTime.set(Calendar.SECOND,0);
		closeTime.set(Calendar.MILLISECOND,0);
		if (DateUtils.getCurrentDate().after(closeTime.getTime())) {
			this.getManagedBean().setReadOnly(true);
			StringBuilder qtSB = new StringBuilder();
			qtSB.append(getParameterDesc(qt.getProcurementMode()
					.getParameterType(), qt.getProcurementMode()
					.getParameterCode()));
			qtSB.append(" - ");
			qtSB.append(qt.getQtNo());
			SimpleDateFormat closeDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATETIME_FORMAT);
			throw new EpException(MessageConst.ERROR_760461, new String[] {
					qtSB.toString(),
					closeDateFormat.format(qt.getClosingDate()).toString() });
		}
	}

	/**
	 * Method to update scQTProposal to allow resubmit.
	 */
	public void updateProposal() {
		debugLogger("updateProposal supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		//Bug #28326 start
		validateProposalCloseTimeForUpdateButton(this.getManagedBean().getQt());
		//Bug #28326 end
		getManagedBean().setReadOnly(false);
		ScQtProposal proposal = this.getManagedBean().getQtProposal();
		proposal.setSubmitted(false);
		IQTProposalSO proposalSO;
		
		// #29134 - CR 27233: Static Code Analysis
		// start
		// removed try and finally clause
//		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			reqDTO.setScQtProposal(proposal);
			
			// #27233 : CR add new tracking diary entry for update - START
			Long scQtSuppId = proposal.getScQtSupplier() != null ? proposal.getScQtSupplier().getQtSupplierId() : null;
			if(scQtSuppId != null){
				reqDTO.setUpdating(true);
				reqDTO.setQtSupplierId(scQtSuppId); // need real scSupplier
				reqDTO.setRoleCode(super.getUserRoleCode());
				reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			}
			// #27233 : CR add new tracking diary entry for update - END
			
			proposalSO = getProposalSO();
			ProposalResDTO resDTO = proposalSO
					.updateQTProposalForReSubmit(reqDTO);
			this.getManagedBean().setQtProposal(resDTO.getScQtProposal());
//		} finally {
//			proposalSO = null;
//		}
			// end #29134 - CR 27233: Static Code Analysis 
	}

	/**
	 * Method to to fetch proposal submission.
	 * @return
	 */
	public boolean isProposalSubmitted() {
		return this.getManagedBean().getQtProposal() != null ? this.getManagedBean().getQtProposal().isSubmitted() : false;
	}	

	//Enhancement #18723 To Generate Company Experience from SM Module
	/**
	 * Method to retrieve technical federal government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String getTecCompanyExpDtls(ScQtProposalChecklist scQtProposalChecklist) { 
       debugLogger("getTecGovCompanyExpDtls::::::SupplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList().getScQtCheckListValue().getYear());

		IQTProposalSO proposalSO = null;
		
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist.getQtProposalChecklistId());

			reqDTO.setYear(scQtProposalChecklist.getScQtCheckList().getScQtCheckListValue().getYear().intValue());
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setApplId(scQtProposalChecklist.getScQtProposal().getSupplierApplId()); //Bug #30980

			ProposalResDTO sdto = proposalSO.getCompanyExperience(reqDTO);
			List<SmCompanyExperience> suppExpList = sdto.getCompExperienceList();
			this.getManagedBean().setSupplierExperienceList(suppExpList);
			this.getManagedBean().setSuppExpSize(suppExpList.size());
			
			log.info(":::getTecCompanyExpDtls() suppExpList.size() "+ suppExpList.size());	

			return NAV_TO_FEDERAL_GOVT_PAGE;

		} finally {
			proposalSO = null;
		}
	}
	//end of Enhancement #18723

	/**
	 * Method to retrieve financial federal government experience details
	 * 
	 * @param scQtProposalChecklist
	 * @return navigation path
	 */
	public String getFinCompanyExpDtls(ScQtProposalChecklist scQtProposalChecklist) { 
        debugLogger("getFinCompanyExpDtls::::supplierId:::"+this.getManagedBean().getSupplierId()+"QTID:::"+this.getManagedBean().getQt().getQtId());
		getManagedBean().setYear(
				scQtProposalChecklist.getScQtCheckList().getScQtCheckListValue().getYear());

		IQTProposalSO proposalSO = null;
		
		try {
			ProposalReqDTO reqDTO = new ProposalReqDTO();
			proposalSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setSupplierId(getManagedBean().getSupplierId());
			reqDTO.setQtProposalCheckListId(scQtProposalChecklist.getQtProposalChecklistId());

			reqDTO.setYear(scQtProposalChecklist.getScQtCheckList().getScQtCheckListValue().getYear().intValue());
			reqDTO.setScQt(this.getManagedBean().getQt());
			reqDTO.setApplId(scQtProposalChecklist.getScQtProposal().getSupplierApplId()); //Bug #30980

			ProposalResDTO sdto = proposalSO.getCompanyExperience(reqDTO);
			List<SmCompanyExperience> suppExpList = sdto.getCompExperienceList();
			this.getManagedBean().setSupplierExperienceList(suppExpList);
			
			BigDecimal totalAmount = new BigDecimal(0);
			for (SmCompanyExperience suppExp : suppExpList) {
				if(suppExp.getContractValue() != null){
					totalAmount = totalAmount.add(suppExp.getContractValue());
				}
			}
			
			this.getManagedBean().setTotalContractAmount(totalAmount);

			return NAV_TO_GOVT_COMPANY_EXPERIENCE_PAGE;

		} finally {
			proposalSO = null;
		}
	}
	
	
	
	/**
	 * 
	 * @return 
	 */
	public Boolean canShowSupplierProposal(){
		
		SupplierFinalizationBackingBean sfbb = new SupplierFinalizationBackingBean();
		sfbb.setScQt(this.getManagedBean().getQt());
		return sfbb.canShowSupplierProposal();
		
	}
	
	/**
	 * @return
	 */
	public boolean checkDisciplinaryActionForQTCat() {
		//#27233#note-36 for #26247 -start-
		log.debug("Entered inside " + this.getClass().getName() + ".checkDisciplinaryActionForQTCat()");
		if (null != this.getManagedBean().getQt()) {
			if (StringUtils.isEmpty(this.getManagedBean().getQt().getDisciplinaryActionPresent())) {
				log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : checking DA for QT No. : " + this.getManagedBean().getQt().getQtNo());
				if (null != getManagedBean().getQtProposal() && null != getManagedBean().getQtProposal().getScQtSupplier()
						&& null != getManagedBean().getQtProposal().getScQtSupplier().getSmSupplier()) {
					log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : checking DA for Supplier ID : " + getManagedBean().getQtProposal().getScQtSupplier().getSmSupplier().getSupplierId());
					// #28338 - Start.
					List<SmDisciplinaryAction> resultList = null;
					SupplierDataMaintenanceReqDTO supReqDTO = new SupplierDataMaintenanceReqDTO();
					supReqDTO.setSupplier(getManagedBean().getQtProposal().getScQtSupplier().getSmSupplier());
					SupplierDataMaintenanceResDTO dataRes = SmServiceLookup.getSupplierDataMaintenanceSO().retrieveActiveDisciplinaryActionBySupplier(supReqDTO);
					
					if(null != dataRes && dataRes.getSmDisciplinaryActions() != null && dataRes.getSmDisciplinaryActions().size() > 0){
						resultList = dataRes.getSmDisciplinaryActions();
						List<String> catCodeList = null;
						
						// #30609 - Start.
						for(SmDisciplinaryAction disciplinaryAction: resultList){
							if(disciplinaryAction.isSuspendRegistration()) {
								this.getManagedBean().getQt().setDisciplinaryActionPresent("true");
								getManagedBean().setDisableUpdateProposal(true);
								return true;
							}
						}
						// #30609 - End.
						
						for(SmDisciplinaryAction disciplinaryAction: resultList){
							if(disciplinaryAction.isSuspendRegistration() || disciplinaryAction.isCancelRegistration() 
									|| disciplinaryAction.isBlacklistOwner() || disciplinaryAction.isManufacturerCancel()
									|| disciplinaryAction.isBumiputeraCancel()){
								this.getManagedBean().getQt().setDisciplinaryActionPresent("true");
								log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : DA found for QT No. : " + this.getManagedBean().getQt().getQtNo());
								return true;
							} else if(disciplinaryAction.isCancelCategory()){
								if (null == catCodeList) {
									catCodeList = new ArrayList<>();
								}
								List<Object[]> catList = SmApplicationUtil.retrieveCancelCategory();
								
								if(!EntityHelper.isCollectionEmpty(catList)
										&& catList.size() > 0){
									for(Object[] arr : catList){
										String catCode = StringUtils.EMPTY;
										catCode = arr[3].toString() + arr[4].toString() + arr[5].toString() ;
										catCodeList.add(catCode);
									}						
								}
							}
						}
						
						if (CollectionUtils.isNotEmpty(catCodeList)) {
							IQTDetailSO QtDetailsSO;
							QuationTenderResDTO tenderResDTO;
							
							try {
								QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
								QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
								// tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
								tenderReqDTO.setQtdetail(this.getManagedBean().getQt());
								
								tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
								
								if (!this.getManagedBean().getQt().isPanel()) {
									List<ScQtCategory> categoryResults = tenderResDTO
											.getCategories();
									if (CollectionUtils.isNotEmpty(categoryResults)) {
										for (ScQtCategory category : categoryResults) {
											String scQtCatCode = category.getPmCategoryL1().getCategoryL1Code().toString() 
													+ category.getPmCategoryL2().getCategoryL2Code().toString() 
													+ category.getPmCategoryL3().getCategoryL3Code().toString() ;
											if (catCodeList.contains(scQtCatCode)) {
												this.getManagedBean().getQt().setDisciplinaryActionPresent("true");
												log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : DA found for QT No. : " + this.getManagedBean().getQt().getQtNo());
												return true;
											}
										}
										this.getManagedBean().getQt().setDisciplinaryActionPresent("false");
										log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : DA not found for QT No. : " + this.getManagedBean().getQt().getQtNo());
									}
								}
							} finally {
								QtDetailsSO=null;
							}
						}
					} else {
						this.getManagedBean().getQt().setDisciplinaryActionPresent("false");
						log.debug(this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : DA not found for QT No. : " + this.getManagedBean().getQt().getQtNo());
					}
					// #28338 - End.
				}
			}
			else if ("true".equals(this.getManagedBean().getQt().getDisciplinaryActionPresent())) {
				log.debug("Exit " + this.getClass().getName() + ".checkDisciplinaryActionForQTCat() : DA already present for QT No. : " + this.getManagedBean().getQt().getQtNo());
				return true;
			}
		}
		log.debug("Exit " + this.getClass().getName() + ".checkDisciplinaryActionForQTCat()");
		//#27233#note-36 for #26247 -end-
		
		return false;
	}
	
	
	/* 27368 - start */
	public boolean enableQtNoHyperlink(){
		return userHasRole(new RoleCode[]{RoleCode.DESK_OFFICER, RoleCode.BPK_AUDITOR}); 
	}
	/* 27368 - end */
	
	//#25089@hatta - start
		public void onTabChange(TabChangeEvent event){
			if(event.getTab().getId().equals("tab1")){
				try {
					Thread.sleep(500);
					RequestContext.getCurrentInstance().execute("popUpInfoDlg.show()");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void dafaultPopUpSupplierInfo(){
		   RequestContext.getCurrentInstance().execute("popUpInfoDlg.show()");
		}
	//#25089@hatta - end
		

		//#21695 farid	start display tree node
			private void populateZoneDetailIntoTreeNode( TemplateSpecBean templateSpecBean) {
				
				TplSpecZone specZone = templateSpecBean.getSpecMaster().getTplSpecZone();
				List<TplSpecZoneDtl> specZoneDtls = null;
				List<TreeNode> zoneGeneralTrees = getManagedBean().getZoneGeneralTrees();
				
				if (specZone != null) {
						ISpecZoneLocSO specZoneLocSO;
						try {
							specZoneLocSO = BusinessDelegate.getInstance()
									.getServiceObject(
											ModuleCodeConst.COMMON_COMPONENT,
											ISpecZoneLocSO.class);

							SpecZoneLocReqDTO reqDTO = new SpecZoneLocReqDTO();
							reqDTO.setDocType(specZone.getDocType());
							reqDTO.setDocId(specZone.getDocId());

							// Get zone details list from db.
							SpecZoneLocResDTO resDTO = specZoneLocSO
									.retrieveZoneDtlAndLocByDocIdNType(reqDTO);

							specZoneDtls = resDTO.getZonesList();
						} finally {
							specZoneLocSO = null;
						}
					}
				
				if (zoneGeneralTrees == null) {
					zoneGeneralTrees = new ArrayList<TreeNode>();
					getManagedBean().setZoneGeneralTrees(zoneGeneralTrees);
				} else {
					zoneGeneralTrees.clear();	
				}
				
				if (CollectionUtils.isNotEmpty(specZoneDtls)) {
					for (TplSpecZoneDtl zone : specZoneDtls) {
					
						if (CollectionUtils.isNotEmpty(zone.getTplSpecZoneLocs())) {
							List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
							
							for (TplSpecZoneLoc specLoc : zone.getTplSpecZoneLocs()) {
								if (specLoc.isZonalLocation()) {
									specZoneLocs.add(specLoc);
								}
							}
								zoneGeneralTrees.add(
										LocationSingleton.getInstance().constuctTreeNode(
												zone, specZoneLocs));
							}
					}
				}
				log.info("habisss");
			}
		//#21695 farid end
//#28792-start			
private void validateFinacialProposalAttachments(boolean validationSuspend){
	
	List<ScQtProposalChecklist> financialChecklist = getManagedBean()
			.getFinanceCheckDetailsList();
	if(null!=financialChecklist && CollectionUtils.isNotEmpty(financialChecklist)){
	for (ScQtProposalChecklist checklist : financialChecklist) {
	String defType = checklist.getScQtCheckList().getDefinationType()
			.getParameterCode();
	String financialsupplierAction = checklist.getScQtCheckList()
			.getAction().getParameterCode();

	if ((checklist.isSubmitted() && defType.equals(ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD) && ParameterConst.CHECKLIST_ACTION_ONLINE_SUBMISSION
					.equalsIgnoreCase(financialsupplierAction))||(checklist.isSubmitted() && ParameterConst.DOCUMENT_DEFINITION_TYPE_NO_ACTION.equalsIgnoreCase(defType))) {
		if(null==checklist.getScQtProposalChecklistAttachment()){
		PmDocument document=	checklist.getScQtCheckList().getPmDocument();
			if(null!=document && null!=document.getDocGroupId() && new Integer(ParameterConst.ASSET_LIABILITIES_GROUP_ID).equals(document.getDocGroupId()) ){
				ScQtProposalChecklist finProposalChecklist = null;
				finProposalChecklist = findProposalChecklistByDocDefKey(
						getManagedBean().getFinanceCheckDetailsList(),
						QTConstants.FINANCIAL_ASSET_LIABILITY_CODE);
				
				if(null!=finProposalChecklist){
					if(finProposalChecklist.isSubmitted()){
					boolean assetLiabilityUpload=assetLiabilitiesUpload(finProposalChecklist);
					if(!assetLiabilityUpload){
					checklist.setSubmitted(Boolean.FALSE);
					if(validationSuspend){
					throw new EpException(MessageConst.ERROR_760514);
					}
					}
					}
					
				}else{
					
					checklist.setSubmitted(Boolean.FALSE);
					if(validationSuspend){
					throw new EpException(MessageConst.ERROR_760514);
					}	
				}
			}
			else if(null!=document && null!=document.getDocGroupId() && new Integer(ParameterConst.CREDIT_FACILITIES_GROUP_ID).equals(document.getDocGroupId()) ){
				ScQtProposalChecklist finProposalChecklist = null;
				finProposalChecklist = findProposalChecklistByDocDefKey(
						getManagedBean().getFinanceCheckDetailsList(),
						QTConstants.FINANCIAL_CREDIT_FACILITY_CODE);
				
				if(null!=finProposalChecklist){
					if(finProposalChecklist.isSubmitted()){
						boolean creditfacilityUpload=creditFacilityUpload(finProposalChecklist);
						if(!creditfacilityUpload){
							checklist.setSubmitted(Boolean.FALSE);
							if(validationSuspend){
							throw new EpException(MessageConst.ERROR_760514);
							}
							}
						
					}
			}
				else{
					
					checklist.setSubmitted(Boolean.FALSE);
					if(validationSuspend){
					throw new EpException(MessageConst.ERROR_760514);
					}	
				}
				
			}
         else{
        	 checklist.setSubmitted(Boolean.FALSE);
        	 if(validationSuspend){
        	 throw new EpException(MessageConst.ERROR_760514); 
        	 }
         }
			
		}
		
	}
	
}
}
}
private boolean assetLiabilitiesUpload(ScQtProposalChecklist scQtProposalChecklist){
	IQTProposalSO proposalSO = null;
	boolean removeAttachment = false;
	try {
		ProposalReqDTO reqDTO = new ProposalReqDTO();
		proposalSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

		reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
		reqDTO.setScQt(this.getManagedBean().getQt());
		ProposalResDTO resDTO = proposalSO
				.retrieveAssetLiabilityDtls(reqDTO);

		ScQtPRPAssetLiability scQtPRPAssetLiability = resDTO
				.getScQtPRPAssetLiability();
		this.getManagedBean().setScQtProposalScoreList(
				resDTO.getProposalScores());

		ScQtProposalScore assetLiabilityAcidRatioScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_ACID_RATIO);
		//ScQtProposalScore assetLiabilityCurrentRatioScore = findProposalScoreByCriteriaType(SourcingConstants.CRITERIA_TYPE_ASSETS_AND_LIABILITIES_CURRENT_RATIO);

		if (null!=scQtPRPAssetLiability && assetLiabilityAcidRatioScore != null) {
			scQtPRPAssetLiability.setAcidRatio(
					assetLiabilityAcidRatioScore.getCriteriaValue());
		}
		
		if ( scQtPRPAssetLiability != null) {
			
			BigDecimal totalVal = new BigDecimal(0.00);
			if (null != scQtPRPAssetLiability.getCurrentAssets()) {
				totalVal = totalVal.add(scQtPRPAssetLiability.getCurrentAssets());
			}
			if (null != scQtPRPAssetLiability.getCurrentLiabilities()) {
				totalVal = totalVal.add(scQtPRPAssetLiability.getCurrentLiabilities());
			}
			if (null != scQtPRPAssetLiability.getTotalCashInHand()) {
				totalVal = totalVal.add(scQtPRPAssetLiability.getTotalCashInHand());
			}
			if (null != scQtPRPAssetLiability.getTotalAccountReceivable()) {
				totalVal = totalVal.add(scQtPRPAssetLiability.getTotalAccountReceivable());
			}
			if( ((null != totalVal && totalVal.compareTo(BigDecimal.ZERO) == 0)
							&& (null != scQtPRPAssetLiability.getAcidRatio() && scQtPRPAssetLiability.getAcidRatio().compareTo(BigDecimal.ZERO) == 0 )
							&& (null != scQtPRPAssetLiability.getCurrentRatio() && scQtPRPAssetLiability.getCurrentRatio().compareTo(BigDecimal.ZERO) == 0))) 
				 {
			
				removeAttachment = true;
				
				//assetProposalChecklist.setSubmitted(true);
				
			}	
	}	
	}finally {
		proposalSO = null;
		this.getManagedBean().setScQtProposalScoreList(null);
	}
	return removeAttachment;
}

private boolean creditFacilityUpload(ScQtProposalChecklist scQtProposalChecklist){
boolean creditFaciltyupload=false;	
IQTProposalSO proposalSO = null;
try {
	ProposalReqDTO reqDTO = new ProposalReqDTO();
	proposalSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);

	reqDTO.setScQtProposalChecklist(scQtProposalChecklist);
	reqDTO.setScQt(this.getManagedBean().getQt());
	ProposalResDTO resDTO = proposalSO
			.retrieveCreditFacilitiesDtls(reqDTO);	
	List<ScQtPRPCreditFacility> creditFacilitiesList = resDTO
			.getCreditFacilityResultBean();
	if (creditFacilitiesList == null
			|| creditFacilitiesList.isEmpty()) {
		creditFaciltyupload=true;	
	}

	
	

} finally {
	proposalSO = null;
}
return creditFaciltyupload;
}
//#28792-End	

//#28785-Start
public TplSpecZoneDtl getKeysByValue(Map<Long, TemplateSpecBean> map, TemplateSpecBean value) {
	  TplSpecZoneDtl specZoneDetail=null;
	  try{
    for (Entry<Long, TemplateSpecBean> entry : map.entrySet()) {
        if (Objects.equals(value, entry.getValue())) {
        if(null!=getManagedBean().getZones() && getManagedBean().getZones().size()>0){
            for(TplSpecZoneDtl specZoneDtl:getManagedBean().getZones()){
            if(entry.getKey().equals(specZoneDtl.getSpecZoneId())){	
            	  return specZoneDtl;
            }
            }
        	}
        }
    }
	  }finally{
		  
	  }
    return specZoneDetail;
}
//#28785-End

		//29729 - start	
		public boolean displaySupplierBranchListDeleted(){
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
			IQTDetailSO QtDetailsSO = (IQTDetailSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			boolean isSupplierBranchListDeleted = false;
			Long supplierId = this.getManagedBean().getSupplierId();
			ScQt scQt = this.getManagedBean().getQt();
		    ScQtSupplierCriteria qtSupplierCriteria = scQt.getScQtSupplierCriteria();
		    
		     if(qtSupplierCriteria.isAllState()){
		    	    SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
		    	    suppSearchReqDTO.setSupplierId(supplierId);
		    		SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchDeletedBySupplier(suppSearchReqDTO);
		    		for (SmSupplierBranch supplierBranch : suppSearchResDTO.getSmSupplierBranchList()) {
						if(supplierBranch.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){
							isSupplierBranchListDeleted = true;
							this.getManagedBean().setSupplierBranchListDeleted(isSupplierBranchListDeleted);
						}
		    		}
		    	
		     }else{
		    	 
		    	    SupplierSearchReqDTO suppSearchReqDTO = new SupplierSearchReqDTO();
		    		suppSearchReqDTO.setSupplierId(supplierId);
		    		SupplierSearchResDTO suppSearchResDTO = supplierSearchSO.retrieveSmSupplierBranchDeletedBySupplier(suppSearchReqDTO);
		    		for (SmSupplierBranch supplierBranch : suppSearchResDTO.getSmSupplierBranchList()) {
		    			
						QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			    		tenderReqDTO.setQtdetail(scQt);
			    		QuationTenderResDTO tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);
			    		if(tenderResDTO != null && tenderResDTO.getLocationDetails()!= null){
			    			for(TplSpecLoc location : tenderResDTO.getLocationDetails()){
								if(supplierBranch.getSmAddress().getPmState().getStateId() != null && 
			    						   location.getPmState().getStateId() != null){
			    					if(supplierBranch.getSmAddress().getPmState().getStateCode().equals(
			    							location.getPmState().getStateCode())){
			    						if(supplierBranch.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_DELETED)){
				    						isSupplierBranchListDeleted = true;
				    						this.getManagedBean().setSupplierBranchListDeleted(isSupplierBranchListDeleted);
			    						}
			    					}	
			    				}
			    			}
			    		}	
		    		}
		     }	
		     
		    return isSupplierBranchListDeleted;
		}
		//29729 - end

		//Bug #30073 Start
		public void checkDuplicateSupplierAnswerItemSpec(ScQtProposalChecklist currProposalChecklist, ScQtProposalChecklist newProposalChecklist){
			IQTProposalSO proposalSO = null;

			try {
				ProposalReqDTO reqDTO = new ProposalReqDTO();
				ProposalResDTO resDTO = new ProposalResDTO();
				
				proposalSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
				
				Long supplierId = getManagedBean().getSupplierId();
				Long qtChecklistId = newProposalChecklist.getScQtCheckList().getQtChecklistId();
				
				reqDTO.setSupplierId(supplierId);
				reqDTO.setQtCheckListId(qtChecklistId);	
				reqDTO.setScQtProposalChecklist(newProposalChecklist);
				
				resDTO = proposalSO.checkDuplicateItemSpec(reqDTO);
				
				//Duplicate exist
				if (resDTO.isSuccessful()){
					newProposalChecklist = resDTO.getProposalChecklist();
					
					if (newProposalChecklist.getScQtCheckList().getDocCategory().
							equals(QTConstants.TECHNICAL_DOC_TYPE)){
						updateQtTechProposalCheckList(currProposalChecklist,	newProposalChecklist);
						prepareTechnicalAction(newProposalChecklist);
					}else{
						updateQtFinNdTechProposalCheckList(
								currProposalChecklist,
								newProposalChecklist,
								SourcingConstants.DOC_DEF_KEY_FIN_COMPANY_EXPERIENCE_FOR_NON_FEDERAL_GOV);
						prepareFinancialAction(newProposalChecklist);
					}
					throw new EpException(MessageConst.ERROR_760524); 
				}
				
			}finally {
				proposalSO = null;
			}
			
		}
		//Bug #30073 End

		//#21695 Farid start 27/8/2021
		public void validateTechSuppNotOfferAll() {

				ScQt scQt = this.getManagedBean().getQt();
				final Long qtId = scQt.getQtId();
				
				if (scQt.isPartial() && scQt.isZonal()) {
					List<TplSpecAnswer> answerList = this.getManagedBean()
							.getTemplateSpecBean().getSpecAnswerList();
					ITemplateSpecSO templateSpecSO = null;
					templateSpecSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
									ITemplateSpecSO.class);

					TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
					reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

					SmSupplier smSupplier = new SmSupplier();
					smSupplier.setSupplierId(getManagedBean().getSupplierId());
					reqDTO.setSupplier(smSupplier);
					reqDTO.setQtId(qtId);
					reqDTO.setAnswerList(answerList);
					reqDTO.setUserLocale(new FacesScopes().getUserLocale());

					TemplateSpecResDTO resDTO = templateSpecSO
							.checkTecNotOfferAllSupProposal(reqDTO);							 
				}else if(scQt.isPartial() && !scQt.isZonal()){
					List<TplSpecAnswer> answerList = this.getManagedBean()
							.getTemplateSpecBean().getSpecAnswerList();
					ITemplateSpecSO templateSpecSO = null;
					templateSpecSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
									ITemplateSpecSO.class);

					TemplateSpecReqDTO reqDTO = new TemplateSpecReqDTO();
					reqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_ANSWER);

					SmSupplier smSupplier = new SmSupplier();
					smSupplier.setSupplierId(getManagedBean().getSupplierId());
					reqDTO.setSupplier(smSupplier);
					reqDTO.setQtId(qtId);
					reqDTO.setAnswerList(answerList);
					reqDTO.setUserLocale(new FacesScopes().getUserLocale());

					TemplateSpecResDTO resDTO = templateSpecSO
							.validateSpecAnwerNotOfferNonZonal(reqDTO);
				}
			}
		//#21695 Farid end 27/8/2021
		


//#21695 Farid start 2/4/2021	
	public boolean evaluateZonelistSupp(TplSpecZoneQty specQties) {
		if(specQties!=null){
			log.info("--->specQties " + specQties.getTplSpecZoneOffer().getSpecZoneOfferId());

			if (CollectionUtils.isNotEmpty(this.getManagedBean().getZones())) {
				for (TplSpecZoneDtl specZones : this.getManagedBean().getZones()) {
					if (specQties.getTplSpecZoneDtl() != null) {
						if (specZones.getSpecZoneId().equals(
								specQties.getTplSpecZoneDtl().getSpecZoneId())) {
								for(TplSpecZoneQty specQtyZoneOffer : specQties.getTplSpecZoneOffer().getTplSpecQuestion().getTplSpecZoneQties()){
									if(specQties.getSpecZoneQtyId().equals(specQtyZoneOffer.getSpecZoneQtyId())){
										specQtyZoneOffer.setOffer(false);
									}
								}
							return true;
						}
					}
				}
			}
			specQties.getTplSpecZoneOffer().setOffer(true);
		}
		return false;
	}
//#21695 Farid end 2/4/2021	
}
