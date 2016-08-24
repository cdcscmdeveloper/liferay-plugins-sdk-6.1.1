package my.ep.web.managedbean.sc.supplierproposal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

import my.ep.constant.pm.ParameterTypeConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtProposalChecklist;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPAssetLiability;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPBankStmt;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCashOnHand;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPCreditFacility;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPEmployee;
import my.ep.persistence.entity.sc.supplierproposal.ScQtPRPExperience;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalScore;
import my.ep.persistence.entity.sc.supplierproposal.ScQtSampleLable;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.TechnicalStaffBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.log4j.Logger;
import org.primefaces.model.MenuModel;
import org.primefaces.model.StreamedContent;

/**
 * Supplier Proposal Managed Bean
 * 
 * @author Viswanath
 * @author Venkat Chanumolu
 */
@ManagedBean(name = "supplierProposalDetailManagedBean")
@SessionScoped
public class SPDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(SPDetailManagedBean.class);

	private ScQt qt;
	private boolean closed = true;
	private ScQtProposal qtProposal;
	private ScQtProposalChecklist qtProposalChecklist;
	private String message;
	private List<ScQtProposalChecklist> tecCheckDetailsList;
	private List<ScQtProposalChecklist> financeCheckDetailsList;
	private List<ScQtProposalChecklist> selectedCheckDetails;

	private ScQtCheckList scQtCheckList;
	private String closingTime = "";
	private int activeIndex;
	private int proposalIndex;

	// Tab Menu
	private int prepareActiveIndex;

	private List<ScQtDocReport> scQtDocReports;

	private ScQtProposalChecklist selectDetails;
	/**
	 * flag to disable buttons if QT closed
	 */
	public boolean checkStatus = true;

	private Long supplierId;

	// private ScAttachment scAttachment = new ScAttachment();
	private List<ScAttachment> scAttachmentList;
	// MenuModel
	private MenuModel proposalZonalMenuModel;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private TemplateSpecBackingBean templateSpecBackingBean = new TemplateSpecBackingBean();

	private List<ScQtProposalScore> scQtProposalScoreList;
	private ScQtProposalScore scQtProposalScore;

	private ScQtDoc scQtDoc;

	private List<TplSpecZoneDtl> zones;
	private TplSpecZoneDtl selectedZone;
	private int prepareProposal;
	private List<ScQtProposal> qtProposalsList;

	// Federal Government
	private List<ScLoa> scLoaList;
	private int contractSize;
	private int noOfYears;

	// Non-Federal Government
	private List<ScQtPRPExperience> scQtPRPExperiences;
	private List<ScQtPRPExperience> selectedPRPExperiences;
	private int totalContracts;
	private int totalYears;
	private BigDecimal totalContractAmount;
	private BigDecimal year;
	private boolean selectedAll;
	private boolean selected;
	private boolean finanicialOnlyCheckList;

	// Business rule
	private Date nonFedEffMinDate;
	private Date nonFedEffMaxDate;
	private Date nonFedMinDate;
	private List<ScQtPRPExperience> deletedPRPExperiences;

	// Credit Facilities
	private List<ScQtPRPCreditFacility> scQtPRPCreditFacilities;
	private List<ScQtPRPCreditFacility> selectedPRPCreditFacilities;
	private BigDecimal totalAmountCredit;
	private BigDecimal totalAmountPaid;
	private BigDecimal totalPercentagePaid;
	private List<ScQtPRPCreditFacility> toBeDeletedCreditFacilities;

	// Cash On Hand
	private List<ScQtPRPCashOnHand> scQtPRPCashOnHand;
	private List<ScQtPRPCashOnHand> selectedPRPCashOnHand;
	private BigDecimal totalcashHand;
	private List<ScQtPRPCashOnHand> toBeDeletedCashOnHandList;

	// Bank Statement
	private List<ScQtPRPBankStmt> scQtPRPBankStmts;
	private List<ScQtPRPBankStmt> selectedPRPBankStmts;
	private BigDecimal totalAmount;
	private BigDecimal avgAmount;

	// Asset Liability
	private ScQtPRPAssetLiability scQtPRPAssetLiability;
	private List<ScQtPRPAssetLiability> scQtPRPAssetLiabilities;
	private List<ScQtPRPAssetLiability> selectedPRPAssetLiabilities;

	// No of Employees
	private ScQtPRPEmployee scQtPRPEmployee;

	// Technical Staff List
	private int totalNoOfYearsExperience;

	// Sample Address
	private ScQtSampleSubmission scQtSampleSubmission;
	private List<ScQtSampleSubmission> scQtSampleSubmissionList;
	private ScQtSampleLable scQtSampleLable;

	// Supplier Information
	private SmSupplier smSupplier;
	private SmAppl smAppl;
	private List<TechnicalStaffBean> smPersonnelList;
	private SmFacility smCompanyBasic;
	private List<SmFacility> supplierFacilities;
	private SmSupplierBank selectedBank;
	private List<SmChequeSignatory> smChequeSignatories;
	private List<SmPersonnel> equityShareHolders;
	private SmPersonnleCalcVO personCalc;
	private String businessType;
	private SmGstSupplier smGstSupplier;
	private SmGstSupplier smGstSupplierBranch;
	private int technicalTableRow;
	private int financialTableRow;

	private boolean agreeWithIntegrityPact;
	private boolean agreeWithOthersTermsConditions;
	private String integrityPact;
	private String othersTermsConditions;
	// Added by vidyasagarReddy
	private String pageUrl;
	private String docDefinitionKey;
	private String checkListDefintionKey;
	private List<ScQtProposal> selectMultipleProposalList;

	private Map<Long, TemplateSpecBean> zoneTplSpecBeanMap;
	private StreamedContent sampleLabelStreamContent;
	
	/** The auto update. */
	private boolean autoUpdate = Boolean.TRUE;

	/** The spki backing bean. */
	@ManagedProperty(value = "#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;

	private String encryptionKey;

	/**
	 * Gets the spki backing bean.
	 * 
	 * @return the spki backing bean
	 */
	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	/**
	 * Sets the spki backing bean.
	 * 
	 * @param spkiBackingBean
	 *            the new spki backing bean
	 */
	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	/**
	 * 
	 * @return list of ScQtProposals
	 */
	public List<ScQtProposal> getSelectMultipleProposalList() {
		return selectMultipleProposalList;
	}

	/**
	 * @param selectMultipleProposalList
	 */
	public void setSelectMultipleProposalList(
			List<ScQtProposal> selectMultipleProposalList) {
		this.selectMultipleProposalList = selectMultipleProposalList;
	}

	/**
	 * Default Consructor
	 */
	public SPDetailManagedBean() {
		log.info("inside ");
	}

	/**
	 * @return the smCompanyBasic
	 */
	public SmFacility getSmCompanyBasic() {
		List<SmFacility> facilities = null;
		if (smAppl != null) {
			facilities = smAppl.getSmFacilities();
		}

		if (facilities != null) {

			for (SmFacility facility : facilities) {

				if (facility.getFacilityType() == SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE
						|| facility.getFacilityType() == SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) {
					smCompanyBasic = facility;
					break;
				}
			}

		}
		return smCompanyBasic;
	}

	/**
	 * @param smCompanyBasic
	 *            the smCompanyBasic to set
	 */
	public void setSmCompanyBasic(SmFacility smCompanyBasic) {
		this.smCompanyBasic = smCompanyBasic;
	}

	/**
	 * @return the smChequeSignatories
	 */
	public List<SmChequeSignatory> getSmChequeSignatories() {
		return smChequeSignatories;
	}

	/**
	 * @param smChequeSignatories
	 *            the smChequeSignatories to set
	 */
	public void setSmChequeSignatories(
			List<SmChequeSignatory> smChequeSignatories) {
		this.smChequeSignatories = smChequeSignatories;
	}

	/**
	 * @return the equityShareHolders
	 */
	public List<SmPersonnel> getEquityShareHolders() {
		return equityShareHolders;
	}

	/**
	 * @param equityShareHolders
	 *            the equityShareHolders to set
	 */
	public void setEquityShareHolders(List<SmPersonnel> equityShareHolders) {
		this.equityShareHolders = equityShareHolders;
	}

	/**
	 * @return the personCalc
	 */
	public SmPersonnleCalcVO getPersonCalc() {
		return personCalc;
	}

	/**
	 * @param personCalc
	 *            the personCalc to set
	 */
	public void setPersonCalc(SmPersonnleCalcVO personCalc) {
		this.personCalc = personCalc;
	}

	/**
	 * @return the toBeDeletedCashOnHandList
	 */
	public List<ScQtPRPCashOnHand> getToBeDeletedCashOnHandList() {
		return toBeDeletedCashOnHandList;
	}

	/**
	 * @param toBeDeletedCashOnHandList
	 *            the toBeDeletedCashOnHandList to set
	 */
	public void setToBeDeletedCashOnHandList(
			List<ScQtPRPCashOnHand> toBeDeletedCashOnHandList) {
		this.toBeDeletedCashOnHandList = toBeDeletedCashOnHandList;
	}

	/**
	 * @return the toBeDeletedCreditFacilities
	 */
	public List<ScQtPRPCreditFacility> getToBeDeletedCreditFacilities() {
		return toBeDeletedCreditFacilities;
	}

	/**
	 * @param toBeDeletedCreditFacilities
	 *            the toBeDeletedCreditFacilities to set
	 */
	public void setToBeDeletedCreditFacilities(
			List<ScQtPRPCreditFacility> toBeDeletedCreditFacilities) {
		this.toBeDeletedCreditFacilities = toBeDeletedCreditFacilities;
	}

	/**
	 * @return the deletedPRPExperiences
	 */
	public List<ScQtPRPExperience> getDeletedPRPExperiences() {
		return deletedPRPExperiences;
	}

	/**
	 * @param deletedPRPExperiences
	 *            the deletedPRPExperiences to set
	 */
	public void setDeletedPRPExperiences(
			List<ScQtPRPExperience> deletedPRPExperiences) {
		this.deletedPRPExperiences = deletedPRPExperiences;
	}

	/**
	 * @return the nonFedEffMaxDate
	 */
	public Date getNonFedEffMaxDate() {
		return nonFedEffMaxDate;
	}

	/**
	 * @param nonFedEffMaxDate
	 *            the nonFedEffMaxDate to set
	 */
	public void setNonFedEffMaxDate(Date nonFedEffMaxDate) {
		this.nonFedEffMaxDate = nonFedEffMaxDate;
	}

	/**
	 * @return the nonFedMinDate
	 */
	public Date getNonFedMinDate() {

		if (nonFedMinDate == null) {
			nonFedMinDate = new Date(01 / 01 / 2008);
		}
		return nonFedMinDate;
	}

	/**
	 * @param nonFedMinDate
	 *            the nonFedMinDate to set
	 */
	public void setNonFedMinDate(Date nonFedMinDate) {
		this.nonFedMinDate = nonFedMinDate;
	}

	/**
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId
	 *            the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the scQtSampleLable
	 */
	public ScQtSampleLable getScQtSampleLable() {
		return scQtSampleLable;
	}

	/**
	 * @param scQtSampleLable
	 *            the scQtSampleLable to set
	 */
	public void setScQtSampleLable(ScQtSampleLable scQtSampleLable) {
		this.scQtSampleLable = scQtSampleLable;
	}

	/**
	 * @return the technicalTableRow
	 */
	public int getTechnicalTableRow() {
		return technicalTableRow;
	}

	/**
	 * @param technicalTableRow
	 *            the technicalTableRow to set
	 */
	public void setTechnicalTableRow(int technicalTableRow) {
		this.technicalTableRow = technicalTableRow;
	}

	/**
	 * @return the financialTableRow
	 */
	public int getFinancialTableRow() {
		return financialTableRow;
	}

	/**
	 * @param financialTableRow
	 *            the financialTableRow to set
	 */
	public void setFinancialTableRow(int financialTableRow) {
		this.financialTableRow = financialTableRow;
	}

	/**
	 * @return the agreeWithIntegrityPact
	 */
	public boolean isAgreeWithIntegrityPact() {
		return agreeWithIntegrityPact;
	}

	/**
	 * @param agreeWithIntegrityPact
	 *            the agreeWithIntegrityPact to set
	 */
	public void setAgreeWithIntegrityPact(boolean agreeWithIntegrityPact) {
		this.agreeWithIntegrityPact = agreeWithIntegrityPact;
	}

	/**
	 * @return the agreeWithOthersTermsConditions
	 */
	public boolean isAgreeWithOthersTermsConditions() {
		return agreeWithOthersTermsConditions;
	}

	/**
	 * @param agreeWithOthersTermsConditions
	 *            the agreeWithOthersTermsConditions to set
	 */
	public void setAgreeWithOthersTermsConditions(
			boolean agreeWithOthersTermsConditions) {
		this.agreeWithOthersTermsConditions = agreeWithOthersTermsConditions;
	}

	/**
	 * @return the integrityPact
	 */
	public String getIntegrityPact() {
		return integrityPact;
	}

	/**
	 * @param integrityPact
	 *            the integrityPact to set
	 */
	public void setIntegrityPact(String integrityPact) {
		this.integrityPact = integrityPact;
	}

	/**
	 * @return the othersTermsConditions
	 */
	public String getOthersTermsConditions() {
		return othersTermsConditions;
	}

	/**
	 * @param othersTermsConditions
	 *            the othersTermsConditions to set
	 */
	public void setOthersTermsConditions(String othersTermsConditions) {
		this.othersTermsConditions = othersTermsConditions;
	}

	/**
	 * @return the year
	 */
	public BigDecimal getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(BigDecimal year) {
		this.year = year;
	}

	/**
	 * @return the nonFedEffMinDate
	 */
	public Date getNonFedEffMinDate() {
		return nonFedEffMinDate;
	}

	/**
	 * @param nonFedEffMinDate
	 *            the nonFedEffMinDate to set
	 */
	public void setNonFedEffMinDate(Date nonFedEffMinDate) {
		this.nonFedEffMinDate = nonFedEffMinDate;
	}

	/**
	 * @return the selectedAll
	 */
	public boolean isSelectedAll() {
		return selectedAll;
	}

	/**
	 * @param selectedAll
	 *            the selectedAll to set
	 */
	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * @return the supplierFacilities
	 */
	public List<SmFacility> getSupplierFacilities() {
		return supplierFacilities;
	}

	/**
	 * @param supplierFacilities
	 *            the supplierFacilities to set
	 */
	public void setSupplierFacilities(List<SmFacility> supplierFacilities) {
		this.supplierFacilities = supplierFacilities;
	}

	/**
	 * @return the closingTime
	 */
	public String getClosingTime() {
		return closingTime;
	}

	/**
	 * @param closingTime
	 *            the closingTime to set
	 */
	public void setClosingTime(String closingTime) {
		this.closingTime = closingTime;
	}

	/**
	 * @return the closed
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * @param closed
	 *            the closed to set
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the zoneTplSpecBeanMap
	 */
	public Map<Long, TemplateSpecBean> getZoneTplSpecBeanMap() {
		if (this.zoneTplSpecBeanMap == null) {
			this.zoneTplSpecBeanMap = new LinkedHashMap<Long, TemplateSpecBean>();
		}
		return zoneTplSpecBeanMap;
	}

	/**
	 * @param zoneTplSpecBeanMap
	 *            the zoneTplSpecBeanMap to set
	 */
	public void setZoneTplSpecBeanMap(
			Map<Long, TemplateSpecBean> zoneTplSpecBeanMap) {
		this.zoneTplSpecBeanMap = zoneTplSpecBeanMap;
	}

	/**
	 * @return the sampleLabelStreamContent
	 */
	public StreamedContent getSampleLabelStreamContent() {
		return sampleLabelStreamContent;
	}

	/**
	 * @param sampleLabelStreamContent
	 *            the sampleLabelStreamContent to set
	 */
	public void setSampleLabelStreamContent(
			StreamedContent sampleLabelStreamContent) {
		this.sampleLabelStreamContent = sampleLabelStreamContent;
	}

	/**
	 * @return the proposalZonalMenuModel
	 */
	public MenuModel getProposalZonalMenuModel() {
		return proposalZonalMenuModel;
	}

	/**
	 * @param proposalZonalMenuModel
	 *            the proposalZonalMenuModel to set
	 */
	public void setProposalZonalMenuModel(MenuModel proposalZonalMenuModel) {
		this.proposalZonalMenuModel = proposalZonalMenuModel;
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * @param qt
	 *            the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * @return the qtProposal
	 */
	public ScQtProposal getQtProposal() {
		return qtProposal;
	}

	/**
	 * @param qtProposal
	 *            the qtProposal to set
	 */
	public void setQtProposal(ScQtProposal qtProposal) {
		this.qtProposal = qtProposal;
	}

	/**
	 * @return the qtProposalChecklist
	 */
	public ScQtProposalChecklist getQtProposalChecklist() {
		return qtProposalChecklist;
	}

	/**
	 * @param qtProposalChecklist
	 *            the qtProposalChecklist to set
	 */
	public void setQtProposalChecklist(ScQtProposalChecklist qtProposalChecklist) {
		this.qtProposalChecklist = qtProposalChecklist;
	}

	/**
	 * @return the tecCheckDetailsList
	 */
	public List<ScQtProposalChecklist> getTecCheckDetailsList() {
		return tecCheckDetailsList;
	}

	/**
	 * @param tecCheckDetailsList
	 *            the tecCheckDetailsList to set
	 */
	public void setTecCheckDetailsList(
			List<ScQtProposalChecklist> tecCheckDetailsList) {
		this.tecCheckDetailsList = tecCheckDetailsList;
	}

	/**
	 * @return the financeCheckDetailsList
	 */
	public List<ScQtProposalChecklist> getFinanceCheckDetailsList() {
		return financeCheckDetailsList;
	}

	/**
	 * @param financeCheckDetailsList
	 *            the financeCheckDetailsList to set
	 */
	public void setFinanceCheckDetailsList(
			List<ScQtProposalChecklist> financeCheckDetailsList) {
		this.financeCheckDetailsList = financeCheckDetailsList;
	}

	/**
	 * @return the selectedCheckDetails
	 */
	public List<ScQtProposalChecklist> getSelectedCheckDetails() {
		return selectedCheckDetails;
	}

	/**
	 * @param selectedCheckDetails
	 *            the selectedCheckDetails to set
	 */
	public void setSelectedCheckDetails(
			List<ScQtProposalChecklist> selectedCheckDetails) {
		this.selectedCheckDetails = selectedCheckDetails;
	}

	/**
	 * @return the scQtCheckList
	 */
	public ScQtCheckList getScQtCheckList() {
		return scQtCheckList;
	}

	/**
	 * @param scQtCheckList
	 *            the scQtCheckList to set
	 */
	public void setScQtCheckList(ScQtCheckList scQtCheckList) {
		this.scQtCheckList = scQtCheckList;
	}

	/**
	 * @return the activeIndex
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * @param activeIndex
	 *            the activeIndex to set
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * @return the proposalIndex
	 */
	public int getProposalIndex() {
		return proposalIndex;
	}

	/**
	 * @param proposalIndex
	 *            the proposalIndex to set
	 */
	public void setProposalIndex(int proposalIndex) {
		this.proposalIndex = proposalIndex;
	}

	/**
	 * @return the prepareActiveIndex
	 */
	public int getPrepareActiveIndex() {
		return prepareActiveIndex;
	}

	/**
	 * @param prepareActiveIndex
	 *            the prepareActiveIndex to set
	 */
	public void setPrepareActiveIndex(int prepareActiveIndex) {
		this.prepareActiveIndex = prepareActiveIndex;
	}

	/**
	 * @return the selectDetails
	 */
	public ScQtProposalChecklist getSelectDetails() {
		return selectDetails;
	}

	/**
	 * @param selectDetails
	 *            the selectDetails to set
	 */
	public void setSelectDetails(ScQtProposalChecklist selectDetails) {
		this.selectDetails = selectDetails;
	}

	/**
	 * @return the checkStatus
	 */
	public boolean isCheckStatus() {
		return checkStatus;
	}

	/**
	 * @param checkStatus
	 *            the checkStatus to set
	 */
	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}

	/**
	 * @return the scAttachmentList
	 */
	public List<ScAttachment> getScAttachmentList() {
		return scAttachmentList;
	}

	/**
	 * @param scAttachmentList
	 *            the scAttachmentList to set
	 */
	public void setScAttachmentList(List<ScAttachment> scAttachmentList) {
		this.scAttachmentList = scAttachmentList;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean
	 *            the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the scQtProposalScoreList
	 */
	public List<ScQtProposalScore> getScQtProposalScoreList() {
		return scQtProposalScoreList;
	}

	/**
	 * @param scQtProposalScoreList
	 *            the scQtProposalScoreList to set
	 */
	public void setScQtProposalScoreList(
			List<ScQtProposalScore> scQtProposalScoreList) {
		this.scQtProposalScoreList = scQtProposalScoreList;
	}

	/**
	 * @return the scQtProposalScore
	 */
	public ScQtProposalScore getScQtProposalScore() {
		return scQtProposalScore;
	}

	/**
	 * @param scQtProposalScore
	 *            the scQtProposalScore to set
	 */
	public void setScQtProposalScore(ScQtProposalScore scQtProposalScore) {
		this.scQtProposalScore = scQtProposalScore;
	}

	/**
	 * @return the scQtDoc
	 */
	public ScQtDoc getScQtDoc() {
		return scQtDoc;
	}

	/**
	 * @param scQtDoc
	 *            the scQtDoc to set
	 */
	public void setScQtDoc(ScQtDoc scQtDoc) {
		this.scQtDoc = scQtDoc;
	}

	/**
	 * @return the zones
	 */
	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	/**
	 * @param zones
	 *            the zones to set
	 */
	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	/**
	 * @return the selectedZone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * @param selectedZone
	 *            the selectedZone to set
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * @return the prepareProposal
	 */
	public int getPrepareProposal() {
		return prepareProposal;
	}

	/**
	 * @param prepareProposal
	 *            the prepareProposal to set
	 */
	public void setPrepareProposal(int prepareProposal) {
		this.prepareProposal = prepareProposal;
	}

	/**
	 * @return the qtProposalsList
	 */
	public List<ScQtProposal> getQtProposalsList() {
		return qtProposalsList;
	}

	/**
	 * @param qtProposalsList
	 *            the qtProposalsList to set
	 */
	public void setQtProposalsList(List<ScQtProposal> qtProposalsList) {
		this.qtProposalsList = qtProposalsList;
	}

	/**
	 * @return the scLoaList
	 */
	public List<ScLoa> getScLoaList() {
		return scLoaList;
	}

	/**
	 * @param scLoaList
	 *            the scLoaList to set
	 */
	public void setScLoaList(List<ScLoa> scLoaList) {
		this.scLoaList = scLoaList;
	}

	/**
	 * @return the contractSize
	 */
	public int getContractSize() {
		return contractSize;
	}

	/**
	 * @param contractSize
	 *            the contractSize to set
	 */
	public void setContractSize(int contractSize) {
		this.contractSize = contractSize;
	}

	/**
	 * @return the noOfYears
	 */
	public int getNoOfYears() {

		if (this.getScLoaList() != null && !this.getScLoaList().isEmpty()) {
			List<Date> effectiveDates = new ArrayList<Date>();
			List<Date> expiryDates = new ArrayList<Date>();
			for (ScLoa loa : this.getScLoaList()) {
				effectiveDates.add(loa.getContractEffDate());
				expiryDates.add(loa.getContractExpDate());
			}
			Date minEffectiveDate = getMinEffectiveDate(effectiveDates);
			Date maxExpiryDate = getMaxExpiryDate(expiryDates);
			if (minEffectiveDate != null && maxExpiryDate != null) {
				this.noOfYears = numberOfMonthsBetweenTwoDates(
						minEffectiveDate, maxExpiryDate);
			} else {
				this.noOfYears = 0;
			}
			effectiveDates = null;
			expiryDates = null;
		}
		return noOfYears;
	}

	/**
	 * @param noOfYears
	 *            the noOfYears to set
	 */
	public void setNoOfYears(int noOfYears) {
		this.noOfYears = noOfYears;
	}

	/**
	 * @return the scQtPRPExperiences
	 */
	public List<ScQtPRPExperience> getScQtPRPExperiences() {
		return scQtPRPExperiences;
	}

	/**
	 * @param scQtPRPExperiences
	 *            the scQtPRPExperiences to set
	 */
	public void setScQtPRPExperiences(List<ScQtPRPExperience> scQtPRPExperiences) {
		this.scQtPRPExperiences = scQtPRPExperiences;
	}

	/**
	 * @return the selectedPRPExperiences
	 */
	public List<ScQtPRPExperience> getSelectedPRPExperiences() {
		return selectedPRPExperiences;
	}

	/**
	 * @param selectedPRPExperiences
	 *            the selectedPRPExperiences to set
	 */
	public void setSelectedPRPExperiences(
			List<ScQtPRPExperience> selectedPRPExperiences) {
		this.selectedPRPExperiences = selectedPRPExperiences;
	}

	/**
	 * @return the totalContracts
	 */
	public int getTotalContracts() {
		return totalContracts;
	}

	/**
	 * @param totalContracts
	 *            the totalContracts to set
	 */
	public void setTotalContracts(int totalContracts) {
		this.totalContracts = totalContracts;
	}

	/**
	 * @return the totalYears
	 */
	public int getTotalYears() {
		if (this.getScQtPRPExperiences() != null
				&& !this.getScQtPRPExperiences().isEmpty()) {
			List<Date> effectiveDates = new ArrayList<Date>();
			List<Date> expiryDates = new ArrayList<Date>();

			if (this.getScQtPRPExperiences().size() >= 1) {
				for (ScQtPRPExperience experience : this
						.getScQtPRPExperiences()) {
					if (experience.getEffectiveDate() != null
							&& experience.getExpiryDate() != null) {
						effectiveDates.add(experience.getEffectiveDate());
						expiryDates.add(experience.getExpiryDate());
					}
				}
				Date minEffectiveDate = getMinEffectiveDate(effectiveDates);
				Date maxExpiryDate = getMaxExpiryDate(expiryDates);
				if (minEffectiveDate != null && maxExpiryDate != null) {
					this.totalYears = numberOfMonthsBetweenTwoDates(
							minEffectiveDate, maxExpiryDate);
				} else {
					this.totalYears = 0;
				}
			} else {
				this.totalYears = 0;
			}

			effectiveDates = null;
			expiryDates = null;
		} else {
			this.totalYears = 0;
		}
		return totalYears;
	}

	/**
	 * @param totalYears
	 *            the totalYears to set
	 */
	public void setTotalYears(int totalYears) {
		this.totalYears = totalYears;
	}

	/**
	 * @return the totalContractAmount
	 */
	public BigDecimal getTotalContractAmount() {
		return totalContractAmount;
	}

	/**
	 * @param totalContractAmount
	 *            the totalContractAmount to set
	 */
	public void setTotalContractAmount(BigDecimal totalContractAmount) {
		this.totalContractAmount = totalContractAmount;
	}

	/**
	 * @return the scQtPRPCreditFacilities
	 */
	public List<ScQtPRPCreditFacility> getScQtPRPCreditFacilities() {
		return scQtPRPCreditFacilities;
	}

	/**
	 * @param scQtPRPCreditFacilities
	 *            the scQtPRPCreditFacilities to set
	 */
	public void setScQtPRPCreditFacilities(
			List<ScQtPRPCreditFacility> scQtPRPCreditFacilities) {
		this.scQtPRPCreditFacilities = scQtPRPCreditFacilities;
	}

	/**
	 * @return the selectedPRPCreditFacilities
	 */
	public List<ScQtPRPCreditFacility> getSelectedPRPCreditFacilities() {
		return selectedPRPCreditFacilities;
	}

	/**
	 * @param selectedPRPCreditFacilities
	 *            the selectedPRPCreditFacilities to set
	 */
	public void setSelectedPRPCreditFacilities(
			List<ScQtPRPCreditFacility> selectedPRPCreditFacilities) {
		this.selectedPRPCreditFacilities = selectedPRPCreditFacilities;
	}

	/**
	 * @return the totalAmountCredit
	 */
	public BigDecimal getTotalAmountCredit() {
		return totalAmountCredit;
	}

	/**
	 * @param totalAmountCredit
	 *            the totalAmountCredit to set
	 */
	public void setTotalAmountCredit(BigDecimal totalAmountCredit) {
		this.totalAmountCredit = totalAmountCredit;
	}

	/**
	 * @return the totalAmountPaid
	 */
	public BigDecimal getTotalAmountPaid() {
		return totalAmountPaid;
	}

	/**
	 * @param totalAmountPaid
	 *            the totalAmountPaid to set
	 */
	public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}

	/**
	 * @return the totalPercentagePaid
	 */
	public BigDecimal getTotalPercentagePaid() {
		return totalPercentagePaid;
	}

	/**
	 * @param totalPercentagePaid
	 *            the totalPercentagePaid to set
	 */
	public void setTotalPercentagePaid(BigDecimal totalPercentagePaid) {
		this.totalPercentagePaid = totalPercentagePaid;
	}

	/**
	 * @return the scQtPRPCashOnHand
	 */
	public List<ScQtPRPCashOnHand> getScQtPRPCashOnHand() {
		return scQtPRPCashOnHand;
	}

	/**
	 * @param scQtPRPCashOnHand
	 *            the scQtPRPCashOnHand to set
	 */
	public void setScQtPRPCashOnHand(List<ScQtPRPCashOnHand> scQtPRPCashOnHand) {
		this.scQtPRPCashOnHand = scQtPRPCashOnHand;
	}

	/**
	 * @return the selectedPRPCashOnHand
	 */
	public List<ScQtPRPCashOnHand> getSelectedPRPCashOnHand() {
		return selectedPRPCashOnHand;
	}

	/**
	 * @param selectedPRPCashOnHand
	 *            the selectedPRPCashOnHand to set
	 */
	public void setSelectedPRPCashOnHand(
			List<ScQtPRPCashOnHand> selectedPRPCashOnHand) {
		this.selectedPRPCashOnHand = selectedPRPCashOnHand;
	}

	/**
	 * @return the totalcashHand
	 */
	public BigDecimal getTotalcashHand() {
		return totalcashHand;
	}

	/**
	 * @param totalcashHand
	 *            the totalcashHand to set
	 */
	public void setTotalcashHand(BigDecimal totalcashHand) {
		this.totalcashHand = totalcashHand;
	}

	/**
	 * @return the scQtPRPBankStmts
	 */
	public List<ScQtPRPBankStmt> getScQtPRPBankStmts() {
		return scQtPRPBankStmts;
	}

	/**
	 * @param scQtPRPBankStmts
	 *            the scQtPRPBankStmts to set
	 */
	public void setScQtPRPBankStmts(List<ScQtPRPBankStmt> scQtPRPBankStmts) {
		this.scQtPRPBankStmts = scQtPRPBankStmts;
	}

	/**
	 * @return the selectedPRPBankStmts
	 */
	public List<ScQtPRPBankStmt> getSelectedPRPBankStmts() {
		return selectedPRPBankStmts;
	}

	/**
	 * @param selectedPRPBankStmts
	 *            the selectedPRPBankStmts to set
	 */
	public void setSelectedPRPBankStmts(
			List<ScQtPRPBankStmt> selectedPRPBankStmts) {
		this.selectedPRPBankStmts = selectedPRPBankStmts;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount
	 *            the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the avgAmount
	 */
	public BigDecimal getAvgAmount() {
		return avgAmount;
	}

	/**
	 * @param avgAmount
	 *            the avgAmount to set
	 */
	public void setAvgAmount(BigDecimal avgAmount) {
		this.avgAmount = avgAmount;
	}

	/**
	 * @return the scQtPRPAssetLiability
	 */
	public ScQtPRPAssetLiability getScQtPRPAssetLiability() {
		return scQtPRPAssetLiability;
	}

	/**
	 * @param scQtPRPAssetLiability
	 *            the scQtPRPAssetLiability to set
	 */
	public void setScQtPRPAssetLiability(
			ScQtPRPAssetLiability scQtPRPAssetLiability) {
		this.scQtPRPAssetLiability = scQtPRPAssetLiability;
	}

	/**
	 * @return the scQtPRPAssetLiabilities
	 */
	public List<ScQtPRPAssetLiability> getScQtPRPAssetLiabilities() {
		return scQtPRPAssetLiabilities;
	}

	/**
	 * @param scQtPRPAssetLiabilities
	 *            the scQtPRPAssetLiabilities to set
	 */
	public void setScQtPRPAssetLiabilities(
			List<ScQtPRPAssetLiability> scQtPRPAssetLiabilities) {
		this.scQtPRPAssetLiabilities = scQtPRPAssetLiabilities;
	}

	/**
	 * @return the selectedPRPAssetLiabilities
	 */
	public List<ScQtPRPAssetLiability> getSelectedPRPAssetLiabilities() {
		return selectedPRPAssetLiabilities;
	}

	/**
	 * @param selectedPRPAssetLiabilities
	 *            the selectedPRPAssetLiabilities to set
	 */
	public void setSelectedPRPAssetLiabilities(
			List<ScQtPRPAssetLiability> selectedPRPAssetLiabilities) {
		this.selectedPRPAssetLiabilities = selectedPRPAssetLiabilities;
	}

	/**
	 * @return the scQtPRPEmployee
	 */
	public ScQtPRPEmployee getScQtPRPEmployee() {
		return scQtPRPEmployee;
	}

	/**
	 * @param scQtPRPEmployee
	 *            the scQtPRPEmployee to set
	 */
	public void setScQtPRPEmployee(ScQtPRPEmployee scQtPRPEmployee) {
		this.scQtPRPEmployee = scQtPRPEmployee;
	}

	/**
	 * @return the totalNoOfYearsExperience
	 */
	public int getTotalNoOfYearsExperience() {
		return totalNoOfYearsExperience;
	}

	/**
	 * @param totalNoOfYearsExperience
	 *            the totalNoOfYearsExperience to set
	 */
	public void setTotalNoOfYearsExperience(int totalNoOfYearsExperience) {
		this.totalNoOfYearsExperience = totalNoOfYearsExperience;
	}

	/**
	 * @return the scQtSampleSubmission
	 */
	public ScQtSampleSubmission getScQtSampleSubmission() {
		return scQtSampleSubmission;
	}

	/**
	 * @param scQtSampleSubmission
	 *            the scQtSampleSubmission to set
	 */
	public void setScQtSampleSubmission(
			ScQtSampleSubmission scQtSampleSubmission) {
		this.scQtSampleSubmission = scQtSampleSubmission;
	}

	/**
	 * @return the scQtSampleSubmissionList
	 */
	public List<ScQtSampleSubmission> getScQtSampleSubmissionList() {
		return scQtSampleSubmissionList;
	}

	/**
	 * @param scQtSampleSubmissionList
	 *            the scQtSampleSubmissionList to set
	 */
	public void setScQtSampleSubmissionList(
			List<ScQtSampleSubmission> scQtSampleSubmissionList) {
		this.scQtSampleSubmissionList = scQtSampleSubmissionList;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the smAppl
	 */
	public SmAppl getSmAppl() {
		return smAppl;
	}

	/**
	 * @param smAppl
	 *            the smAppl to set
	 */
	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	/**
	 * @return the smPersonnelList
	 */
	public List<TechnicalStaffBean> getSmPersonnelList() {
		return smPersonnelList;
	}

	/**
	 * @param smPersonnelList
	 *            the smPersonnelList to set
	 */
	public void setSmPersonnelList(List<TechnicalStaffBean> smPersonnelList) {
		this.smPersonnelList = smPersonnelList;
	}

	/**
	 * @return the pageUrl
	 */
	public String getPageUrl() {
		return pageUrl;
	}

	/**
	 * @param pageUrl
	 *            the pageUrl to set
	 */
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	/**
	 * @return the docDefinitionKey
	 */
	public String getDocDefinitionKey() {
		return docDefinitionKey;
	}

	/**
	 * @param docDefinitionKey
	 *            the docDefinitionKey to set
	 */
	public void setDocDefinitionKey(String docDefinitionKey) {
		this.docDefinitionKey = docDefinitionKey;
	}

	/**
	 * @return the checkListDefintionKey
	 */
	public String getCheckListDefintionKey() {
		return checkListDefintionKey;
	}

	/**
	 * @param checkListDefintionKey
	 *            the checkListDefintionKey to set
	 */
	public void setCheckListDefintionKey(String checkListDefintionKey) {
		this.checkListDefintionKey = checkListDefintionKey;
	}

	/**
	 * @return the scQtDocReports
	 */
	public List<ScQtDocReport> getScQtDocReports() {
		return scQtDocReports;
	}

	/**
	 * @param scQtDocReports
	 *            the scQtDocReports to set
	 */
	public void setScQtDocReports(List<ScQtDocReport> scQtDocReports) {
		this.scQtDocReports = scQtDocReports;
	}

	/**
	 * @return the finanicialOnlyCheckList
	 */
	public boolean isFinanicialOnlyCheckList() {
		return finanicialOnlyCheckList;
	}

	/**
	 * @param finanicialOnlyCheckList
	 *            the finanicialOnlyCheckList to set
	 */
	public void setFinanicialOnlyCheckList(boolean finanicialOnlyCheckList) {
		this.finanicialOnlyCheckList = finanicialOnlyCheckList;
	}

	/**
	 * @return the templateSpecBackingBean
	 */
	public TemplateSpecBackingBean getTemplateSpecBackingBean() {
		return templateSpecBackingBean;
	}

	/**
	 * @return the businessType
	 */
	public String getBusinessType() {
		this.setBusinessType(SmCommonUtil
				.getDropDownViewOnlyValueByCode(
						this.getSmAppl().getSupplierType().equals("K") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE
								: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT,
						this.getSmSupplier().getBusinessType()));
		return businessType;
	}

	/**
	 * @param businessType
	 *            the businessType to set
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	private Date getMinEffectiveDate(List<Date> effectiveDates) {
		Date minEffectiveDate = null;
		if (effectiveDates != null && !effectiveDates.isEmpty()) {
			minEffectiveDate = effectiveDates.remove(0);
			if (!effectiveDates.isEmpty()) {
				for (Date date : effectiveDates) {
					if (date.before(minEffectiveDate)) {
						minEffectiveDate = date;
					}
				}
			}
		}
		return minEffectiveDate;
	}

	private Date getMaxExpiryDate(List<Date> expiryDates) {
		Date maxExpiryDate = null;
		if (expiryDates != null && !expiryDates.isEmpty()) {
			maxExpiryDate = expiryDates.remove(0);
			if (!expiryDates.isEmpty()) {
				for (Date date : expiryDates) {
					if (date.after(maxExpiryDate)) {
						maxExpiryDate = date;
					}
				}
			}
		}
		return maxExpiryDate;
	}

	private int numberOfMonthsBetweenTwoDates(Date effectiveDate,
			Date expiryDate) {
		Calendar startCalendar = new GregorianCalendar();
		startCalendar.setTime(effectiveDate);
		Calendar endCalendar = new GregorianCalendar();
		// to resolve one day difference
		Calendar c = Calendar.getInstance();
		c.setTime(expiryDate);
		c.add(Calendar.DATE, 1);
		endCalendar.setTime(c.getTime());
		int diffYear = endCalendar.get(Calendar.YEAR)
				- startCalendar.get(Calendar.YEAR);
		int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH)
				- startCalendar.get(Calendar.MONTH);
		return diffMonth;
	}

	/**
	 * @return the encryptionKey
	 */
	public String getEncryptionKey() {
		return encryptionKey;
	}

	/**
	 * @param encryptionKey
	 *            the encryptionKey to set
	 */
	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	/**
	 * @return the smGstSupplier
	 */
	public SmGstSupplier getSmGstSupplier() {
		return smGstSupplier;
	}

	/**
	 * @param smGstSupplier
	 *            the smGstSupplier to set
	 */
	public void setSmGstSupplier(SmGstSupplier smGstSupplier) {
		this.smGstSupplier = smGstSupplier;
	}

	/**
	 * @return the smGstSupplierBranch
	 */
	public SmGstSupplier getSmGstSupplierBranch() {
		return smGstSupplierBranch;
	}

	/**
	 * @param smGstSupplierBranch the smGstSupplierBranch to set
	 */
	public void setSmGstSupplierBranch(SmGstSupplier smGstSupplierBranch) {
		this.smGstSupplierBranch = smGstSupplierBranch;
	}

	/**
	 * @return the selectedBank
	 */
	public SmSupplierBank getSelectedBank() {
		return selectedBank;
	}

	/**
	 * @param selectedBank the selectedBank to set
	 */
	public void setSelectedBank(SmSupplierBank selectedBank) {
		this.selectedBank = selectedBank;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
	
	

}
