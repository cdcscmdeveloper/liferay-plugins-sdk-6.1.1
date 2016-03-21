/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.sc.quationtender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.sc.QTActivityBean;
import my.ep.biz.bean.sc.QTRuleDataBean;
import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.constant.pm.ParameterConst;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListScore;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreOfferPrice;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreSetting;
import my.ep.persistence.entity.sc.quationtender.ScQtScoreWeightage;
import my.ep.persistence.entity.sc.quationtender.ScQtSpecificationRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.CtCircularSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.SupplierSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.sc.quationtender.CloneAll;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * The Class QTDetailManagedBean.
 * 
 * @Author VidyasagarReddy
 */
@ManagedBean
@SessionScoped
public class QTDetailManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDetailManagedBean.class);
	
	/** The qt. */
	private ScQt qt;

	/** The circular type. */
	private String circularType;
	
	/** The selectpanel item. */
	private List<CtPanelItemGroup> panelGroupItems;
	
	private List<CtPanelItem> panelItems;
	
	/** The circular no. */
	private String circularNo;
	
	/** The circular name. */
	private String circularName;
	
	/** The circular exp date. */
	private Date circularExpDate;
	
	// Drop down Listing for Approver
	private List<PmUser> ministrySpecApprovers; 
	
	/** The is zone location. */
	private boolean isZoneLocation;
	
	/** The zone name. */
	private String zoneName;
	
	/** The root zoanl tree. */
	private TreeNode rootZoanlTree;
	
	private boolean isCheckAllList = Boolean.FALSE;
	
	/** The zone general trees. */
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();

	/** The qt doc. */
	private ScQtDoc qtDoc;

	/** The qt remark. */
	private ScQtRemark qtRemark;

	/** The qt check list. */
	private ScQtCheckList qtCheckList;

	/** The qt contact person. */
	private ScQtContactPerson qtContactPerson = new ScQtContactPerson();

	/** The qt deduction. */
	private ScQtDeduction qtDeduction;

	/** The qt deduction clause. */
	private ScQtDeductionClause qtDeductionClause;

	/** The pp plan. */
	private PpPlan ppPlan;

	/** The pm org profile. */
	private PmOrgProfile pmOrgProfile;

	/** The selected boolean1. */
	private String selectedBoolean1;
	// private String multiLoc;
	/** The ct panel suppliers. */
	private List<CtPanelSupplier> ctPanelSuppliers;

	/** For Search Quotation/Tender Screen. */
	private QtnTenderSearchBean qtnTenderSearchBean;

	/** The clone qtn tender search bean. */
	private QtnTenderSearchBean cloneQtnTenderSearchBean;

	/** The category l1s. */
	private List<PmCategoryL1> categoryL1s;

	/** The category l2s. */
	private List<PmCategoryL2> categoryL2s;

	/** The category l3s. */
	private List<PmCategoryL3> categoryL3s;

	/** The states. */
	private List<PmState> states;

	/** The countries. */
	private List<PmCountry> countries;

	/** The districts. */
	private List<PmDistrict> districts;

	/** The citys. */
	private List<PmCity> citys;

	/** The gosodos. */
	public List<ScQtBudget> gosodos;

	/** The sc qt docs. */
	public List<ScQtDoc> scQtDocs;

	/** The clone all list. */
	public List<CloneAll> cloneAllList;

	/** The ct circulars. */
	private List<CtCircular> ctCirculars;

	/** The sc qt checklists. */
	private List<ScQtCheckList> scQtChecklists;

	/** The sc qt deduction list. */
	private List<ScQtDeduction> scQtDeductionList;

	/** The panel supplier list. */
	private List<CtPanelSupplier> panelSupplierList;

	/** The selected briefing details. */
	private List<ScQtBsvDtl> selectedBriefingDetails;

	/** The selected site visit details. */
	private List<ScQtBsvDtl> selectedSiteVisitDetails;

	/** The selected check lists. */
	private List<ScQtCheckList> selectedCheckLists;

	/** The selected gosodo budgets. */
	private List<ScQtBudget> selectedGosodoBudgets;

	/** The selectedpanelsupplier. */
	private List<SmMofAccount> selectedpanelsupplier;

	/** The selected clone tender list. */
	private List<ScQt> selectedCloneTenderList;

	/** The selected clone all list. */
	private List<CloneAll> selectedCloneAllList;

	/** The selected sc qt deductions. */
	private List<ScQtDeduction> selectedScQtDeductions;
	// private List<ScQtDeduction> selectedScQtDeduction;
	/** The selected sc qt deduction. */
	private ScQtDeduction selectedScQtDeduction;

	/** The selected deduction clauses. */
	private List<ScQtDeductionClause> selectedDeductionClauses;

	/** The qt number. */
	private String qtNumber;

	/** The org profile. */
	private PmOrgProfile orgProfile;

	/** The selected user ministry. */
	private PmOrgProfile selectedUserMinistry;

	/** The selected user department. */
	private PmOrgProfile selectedUserDepartment;

	/** The user ministry list. */
	private List<PmOrgProfile> userMinistryList;

	/** The user department list. */
	private List<PmOrgProfile> userDepartmentList;

	/** The user division list. */
	private List<PmOrgProfile> userDivisionList;

	/** The go list. */
	private List<PmGo> goList;

	/** The so list. */
	private List<PmSo> soList;

	/** The do list. */
	private List<PmDo> doList;

	/** The minstry code. */
	private String minstryCode;

	/** The dept code. */
	private String deptCode;

	/** The root2. */
	private TreeNode root2;

	/** The root3. */
	private TreeNode root3;

	/** The clone tender list. */
	private List<ScQt> cloneTenderList = new ArrayList<ScQt>();

	/** The clone business tender list. */
	private List<ScQt> cloneBusinessTenderList = new ArrayList<ScQt>();

	/** The document clone tender list. */
	private List<ScQt> documentCloneTenderList = new ArrayList<ScQt>();

	/** The tender criteria. */
	private ScQt tenderCriteria = new ScQt();

	/** The tab list. */
	private List<TabBean> tabList;

	/** The check box bpk. */
	private String checkBoxBPK;

	/** The check box bp k1. */
	private List<String> checkBoxBPK1;

	/** The standard document. */
	private String standardDocument;

	/** The approval typelist. */
	private List<String> approvalTypelist = new ArrayList<String>();

	/** The sub tab index. */
	private int subTabIndex = 0;

	/** The veryfiers. */
	private List<PmUser> veryfiers;

	/** The approvers. */
	private List<PmUser> approvers;

	/** The bbk approvers. */
	private List<PmUser> bbkApprovers;

	/** The out come. */
	private String outCome;

	/** The process name. */
	private String processName;

	/** The sc qt approval. */
	private ScQtApproval scQtApproval;

	/** The upload update id. */
	private String uploadUpdateId;

	/** The is editable. */
	private boolean isEditable;

	/** The panel suppliers. */
	private List<SmSupplier> panelSuppliers;

	/** The total search supplier. */
	private int totalSearchSupplier;

	/** The search panel suppliers node. */
	private TreeNode searchPanelSuppliersNode;

	/** The suppliers node. */
	private TreeNode suppliersNode;

	/** The search supplier list. */
	private List<SmSupplier> searchSupplierList;

	/** The selected supplier list. */
	private List<SmSupplier> selectedSupplierList;

	/** The qt supplier list. */
	private List<ScQtSupplier> qtSupplierList;

	/** The selected qt supplier list. */
	private List<ScQtSupplier> selectedQtSupplierList;

	/** The sm personnels list. */
	private List<SmPersonnel> smPersonnelsList;

	/** The search supplier data model. */
	private IPagingDataModel<SmSupplier> searchSupplierDataModel;

	/** The supplier search bean. */
	private SupplierSearchBean supplierSearchBean;

	

	/** The selected category list. */
	private List<ScQtCategory> selectedCategoryList;

	/** The qt category list. */
	private List<ScQtCategory> qtCategoryList;

	/** The search qt data model. */
	private IPagingDataModel<ScQt> searchQTDataModel;

	/** The clone search qt data model. */
	private IPagingDataModel<ScQt> cloneSearchQTDataModel;

	/** The pm report section list. */
	private List<PmReportSection> pmReportSectionList;

	/** The selected pm report section list. */
	private List<PmReportSection> selectedPmReportSectionList;

	/** The deduction payment array. */
	private String deductionPaymentArray;

	/** The percentage field. */
	private boolean percentageField = true;

	/** The amount field. */
	private boolean amountField = true;

	/** The description field. */
	private boolean descriptionField = true;

	/** The duration field. */
	private boolean durationField = true;

	/** The circular coverage. */
	private String circularCoverage;

	// Business Owner
	/** The sc committee member list. */
	private List<ScCommitteeMember> scCommitteeMemberList;

	/** The select sc committe member. */
	private ScCommitteeMember selectScCommitteMember;

	/** The committe member. */
	private ScCommitteeMember committeMember;

	/** The selections. */
	private List<SelectItem> selections;

	/** The external users. */
	private List<PmUser> externalUsers;

	/** The selected pm user. */
	private List<PmUser> selectedPmUser;

	/** The clone search committes. */
	private List<ScQtCommittee> cloneSearchCommittes;

	/** The selected clone committee. */
	private ScQtCommittee selectedCloneCommittee;

	/** The clone committe memebers. */
	private List<ScCommitteeMember> cloneCommitteMemebers;

	/** The selected committe memebers. */
	private List<ScCommitteeMember> selectedCommitteMemebers;

	/** The selected sc qt committee. */
	private ScQtCommittee selectedScQtCommittee;

	/** The clone business owner data model. */
	private IPagingDataModel<ScQt> cloneBusinessOwnerDataModel;

	/** The user search business owner data model. */
	private IPagingDataModel<PmUser> userSearchBusinessOwnerDataModel;

	/** The user businee owner search bean. */
	private UserCommitteeSearchBean userBusineeOwnerSearchBean;

	/** The clone business owner search bean. */
	private QtnTenderSearchBean cloneBusinessOwnerSearchBean;

	/** The selected clone bo list. */
	private List<ScQt> selectedCloneBOList;

	// Search properties
	/** The title. */
	private String title = null;

	/** The qt no. */
	private String qtNo = null;

	/** The procurement mode. */
	private String procurementMode;

	/** The created dt from. */
	private Date createdDtFrom;

	/** The created dt to. */
	private Date createdDtTo;

	/** The status. */
	private String status;

	/** The name. */
	private String name = null;

	/** The identification no. */
	private String identificationNo = null;

	/** The identification type. */
	private String identificationType;

	/** The publishing period. */
	private int publishingPeriod;

	/** The comittee users. */
	private List<SelectItem> comitteeUsers;

	/** The budget approver. */
	private List<SelectItem> budgetApprover;

	/** The budget approver name. */
	private List<PmUser> budgetApproverName; // Drop down Listing for Approver
												// Names

	/** The budget approval users. */
	private DualListModel<PmUser> budgetApprovalUsers;

	/** The committee approval users. */
	private DualListModel<PmUser> committeeApprovalUsers;

	/** The publication approvals. */
	private DualListModel<ScQtApproverSettings> publicationApprovals;

	/** The publication approval. */
	private ScQtApproval publicationApproval;

	/** The qt committee remarks. */
	private List<ScQtCommitteRemarks> qtCommitteeRemarks;

	/** The desk officer admin. */
	private List<PmUser> deskOfficerAdmin;

	/** The tree nodes list. */
	private List<TreeNode> treeNodesList;

	/** The sc qt committee. */
	private ScQtCommittee scQtCommittee;

	/** The attachments. */
	private List<ScQtApprovalAttachment> attachments;

	/** The checklist attachment. */
	private List<ScQtCheckListAttachment> checklistAttachment;

	/** The briefing zone bean. */
	private List<BriefingZoneBean> briefingZoneBean = new ArrayList<BriefingZoneBean>();

	

	/** The sc qt supplier. */
	private ScQtSupplier scQtSupplier;

	/** The ct contract. */
	private CtContract ctContract = new CtContract();

	// Committee Appointment
	/** The tab title. */
	private String tabTitle;

	/** The sc committee. */
	private ScCommittee scCommittee;

	/** The committemembers. */
	private List<ScCommitteeMember> committemembers;

	/** The committe no. */
	private String committeNo = null;

	/** The created dt. */
	private Date createdDt;

	/** The selected committe. */
	private String selectedCommitte;

	/** The searchcommittee node. */
	private TreeNode searchcommitteeNode;

	/** The select searchcommittee node. */
	private TreeNode[] selectSearchcommitteeNode;

	/** The selected clone tender. */
	private ScQt selectedCloneTender;

	/** The selected clone business tender. */
	private ScQt selectedCloneBusinessTender;

	/** The selected document clone tender. */
	private ScQt selectedDocumentCloneTender;

	/** The qt check lists. */
	private List<ScQtCheckList> qtCheckLists;

	/** The selected qt check lists. */
	private List<ScQtCheckList> selectedQtCheckLists;

	/** The sc committee member tab list. */
	private List<TabBean> scCommitteeMemberTabList;

	/** The finalization secratary. */
	private List<PmParameter> finalizationSecratary;

	// Existing Committee Search
	/** The user search committee data model. */
	private IPagingDataModel<PmUser> userSearchCommitteeDataModel;

	/** The user committee search bean. */
	private UserCommitteeSearchBean userCommitteeSearchBean;

	/** The comm type. */
	private String commType;

	/** The pm users secretary list. */
	private List<PmUser> pmUsersSecretaryList;

	// Briefing & Site Visit Details
	/** The qt briefing. */
	private ScQtBsvDtl qtBriefing;

	/** The briefing zone name. */
	private String briefingZoneName;

	/** The previous page. */
	private String previousPage;

	/** The briefing registration list. */
	private List<ScQtBsvRegistration> briefingRegistrationList;

	/** The selectedbrief registration. */
	private ScQtBsvRegistration selectedbriefRegistration;

	/** The sc qt bsv attendance list. */
	private List<ScQtBsvAttendance> scQtBsvAttendanceList;

	/** The tpl spec zone dtl. */
	private TplSpecZoneDtl tplSpecZoneDtl;

	/** The zones. */
	private List<TplSpecZoneDtl> zones;

	/** The remarks. */
	private List<ScRemark> remarks;

	/** The selected zone. */
	private TplSpecZoneDtl selectedZone;

	/** The sc qt bsv registration. */
	private ScQtBsvRegistration scQtBsvRegistration;

	/** The required. */
	private boolean required = false;

	/** The briefing attendance list. */
	private List<ScQtBsvAttendance> briefingAttendanceList;

	/** The sc qt briefing attendance. */
	private ScQtBsvAttendance scQtBriefingAttendance;

	/** The today. */
	private Date today = new Date();

	// Briefing Attendance
	/** The mof accounts. */
	private List<SmMofAccount> mofAccounts;

	// Schedule & Contact Information
	/** The pmuser contactperson list. */
	private List<ScQtContactPerson> pmuserContactpersonList;

	/** The selectedpmuser contactperson list. */
	private List<ScQtContactPerson> selectedpmuserContactpersonList;

	/** The pm users list. */
	private List<PmUser> pmUsersList;

	/** The selected pm users list. */
	private List<PmUser> selectedPmUsersList;

	/** The pm user. */
	private PmUser pmUser;

	/** The sc qt sample submission. */
	private ScQtSampleSubmission scQtSampleSubmission;

	/** The sc qt sample submission list. */
	private List<ScQtSampleSubmission> scQtSampleSubmissionList;

	/** The selected submission list. */
	private List<ScQtSampleSubmission> selectedSubmissionList;

	/** The sc qt contact persons. */
	private List<ScQtContactPerson> scQtContactPersons;

	/** The selected contact persons. */
	private List<ScQtContactPerson> selectedContactPersons;

	/** The proposal validity until. */
	private Date proposalValidityUntil;

	/** The closing date. */
	private Date closingDate;

	/** The bsv date. */
	private Date bsvDate;

	

	/** The pm document list. */
	private List<PmDocument> pmDocumentList;

	/** The selected pm document list. */
	private List<PmDocument> selectedPmDocumentList;

	/** The sc qt doc report list. */
	private List<ScQtDocReport> scQtDocReportList;

	/** The selected sc qt doc reports. */
	private List<ScQtDocReport> selectedScQtDocReports;

	/** The selected sc qt doc report. */
	private ScQtDocReport selectedScQtDocReport;

	/** The sc qt doc report. */
	private ScQtDocReport scQtDocReport;

	/** The tpl doc master. */
	private TplDocMaster tplDocMaster;

	/** The mark deleted sc qt doc report list. */
	private List<ScQtDocReport> markDeletedScQtDocReportList;

	/** The clone document data model. */
	private IPagingDataModel<ScQt> cloneDocumentDataModel;

	/** The clone document search bean. */
	private QtnTenderSearchBean cloneDocumentSearchBean;

	/** The clone sc qt doc reports. */
	private List<ScQtDocReport> cloneScQtDocReports;

	/** The selected clone sc qt doc reports. */
	private List<ScQtDocReport> selectedCloneScQtDocReports;

	/** The remove doc reports. */
	private List<ScQtDocReport> removeDocReports;

	/** The sc qt approval request list. */
	private List<ScQtApprovalRequest> scQtApprovalRequestList;

	/** The approval type. */
	private String approvalType;

	/** The qt committes. */
	private ISearchResultBean<ScQtCommittee> qtCommittes;

	/** The technical spec list. */
	private List<ScQtCheckList> technicalSpecList;

	/** The financial spec list. */
	private List<ScQtCheckList> financialSpecList;

	// Added code to move to tabmenus..
	/** The menu model. */
	private MenuModel menuModel;

	/** The selected tab index. */
	public int selectedTabIndex;

	/** The selected tab bean. */
	public TabBean selectedTabBean;

	/** The approval menu model. */
	public MenuModel approvalMenuModel;

	/** The selected approval tab index. */
	public int selectedApprovalTabIndex;

	/** The selected approval tab bean. */
	public TabBean selectedApprovalTabBean;

	/** The bsv dtl bean. */
	private BriefingZoneBean bsvDtlBean;

	/** The bsv type. */
	private String bsvType;

	/** The show bsv text. */
	private boolean showBsvText;

	/** The show sit vistit text. */
	private boolean showSitVistitText;

	/** The bsv index. */
	private int bsvIndex;

	/** The sample address index. */
	private int sampleAddressIndex;

	/** The action type. */
	private String actionType;

	/** The bsv zoneindx. */
	private String bsvZoneindx = "0";

	/** The remove deductions. */
	private List<ScQtDeduction> removeDeductions;

	/** The to be del committe memebers. */
	private List<ScCommitteeMember> toBeDelCommitteMemebers;

	/** The action. */
	private String action;

	/** The is save flow. */
	private boolean isSaveFlow;

	/** The clone needed. */
	private boolean cloneNeeded;

	/** The qt activity beans. */
	private List<QTActivityBean> qtActivityBeans;

	// newly Added.
	/** The state codes. */
	private String stateCodes;

	/**
	 * Added to know whether it is maintance flow or not
	 */
	private boolean maintanceFlow;

	/** The is submit. */
	private boolean isSubmit = false;

	/**
	 * TO update details for modifed qtfileds
	 */
	private QtAmendMentBean amendMentBean;

	private boolean paidupcapital;

	private QTRuleDataBean ruleDataBean;
	private List<TplSpecZoneDtl> templateSpecZoneDtls;
	private ScQtBsvDtl tempScQtBsvDtl;

	private ScQt cloneCommitteeSelectQt;
	private ScQt cloneBusinessSelectQt;
	private ScQt cloneDocumentSelectQt;

	/** The auto update. */
	private boolean autoUpdate = true;

	private int selectedCommitteeTabIndex;

	/** The clauses title. */
	private String clausesTitle;

	private boolean isPcbProcess;

	private boolean pendingRevision;

	private boolean deskOfficer;

	private boolean auditorRoleView;

	private PmOrgProfile ptjProfile;

	private PmOrgProfile ministryProfile;

	private Object[] scTdObj;
	private boolean viewQtDetails;

	
	private boolean createQt;

	private String statecode;
	

	
	
	
	private List<ScQtApproval> toBeApprovalRemoveList;
	
	/** The sc qt remarks. */
	private ScQtRemark scQtRemarks;

	/** The sc qt checklist. */
	private ScQtCheckList scQtChecklist;

	/** The sc qt check st2tech proposal. */
	private List<ScQtCheckList> scQtCheckSt2techProposal;

	/** The mark deleted tech checklist. */
	private List<ScQtCheckList> markDeletedTechChecklist;

	/** The sc qt check st2finance proposal list. */
	private List<ScQtCheckList> scQtCheckSt2financeProposalList;

	/** The mark deleted fin checklist. */
	private List<ScQtCheckList> markDeletedFinChecklist;

	/** The sc remark. */
	private ScRemark scRemark;

	/** The committee activities. */
	private List<ScQtCommitteeActivity> committeeActivities;

	/** The is sample req. */
	public boolean isSampleReq = true;

	/** The is spec status. */
	private boolean isSpecStatus;

	/** The procurement id. */
	private Long procurementId;

	/** The evaluation type. */
	private String evaluationType;

	

	/** The stage two tech compliance list. */
	private List<PmDocument> stageTwoTechComplianceList;

	/** The stage two tech compliance select. */
	private List<PmDocument> stageTwoTechComplianceSelect;

	/** The stage two fin compliance list. */
	private List<PmDocument> stageTwoFinComplianceList;

	/** The stage two fin compliance select. */
	private List<PmDocument> stageTwoFinComplianceSelect;

	/** The s2 tech select. */
	private List<ScQtCheckList> s2TechSelect;

	/** The s2 fin select. */
	private List<ScQtCheckList> s2FinSelect;

	/** The schedule type. */
	private String scheduleType;

	/** The from month. */
	private String fromMonth;

	/** The to month. */
	private String toMonth;

	/** The start year. */
	private String startYear;

	/** The end year. */
	private String endYear;
	// B,S
	/** The business owner. */
	private String businessOwner = "B";

	/** The check list score. */
	private List<ScQtCheckListScore> checkListScore;

	/** The scoring for years. */
	private List<ScQtCheckListScore> scoringForYears;

	/** The check list select score. */
	private List<ScQtCheckListScore> checkListSelectScore;

	/** The schedule list. */
	private List<ScQtSchedule> scheduleList;

	/** The progressive payment list. */
	private List<ScQtProgressivePayment> progressivePaymentList;

	/** The total frequency. */
	private int totalFrequency;

	/** The total progressive payment. */
	private int totalProgressivePayment;

	

	/** The item name. */
	private String itemName;

	/** The technical weightage score. */
	private BigDecimal technicalWeightageScore;

	/** The financial weightage score. */
	private BigDecimal financialWeightageScore;

	/** The total amount. */
	private int totalAmount;

	/** The total amount avg. */
	private BigDecimal totalAmountAvg;

	/** The panel item list. */
	private List<CtPanelItem> panelItemList;

	/** The is panel. */
	private boolean isPanel;

	/** The ct zone trees. */
	private TreeNode ctZoneTrees;

	/** The ct zone tree selected nodes. */
	private TreeNode[] ctZoneTreeSelectedNodes;

	/** The ct zone suppliers. */
	private List<TreeNode> ctZoneSuppliers = new ArrayList<TreeNode>();

	/** The ct zone supplier list. */
	private List<CtPanelSupplier> ctZoneSupplierList;

	// supplier Qualification Criteria List and variables

	

	/** The supp criteriaopen select list. */
	private List<ScQtCategory> suppCriteriaopenSelectList;

	/** The root location tree. */
	private TreeNode rootLocationTree;

	/** The panel zone select. */
	private TreeNode panelZoneSelect;

	/** The zone trees. */
	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();
	
	/** The zones list. */
	private List<TreeNode> zonesList = new ArrayList<TreeNode>();

	/** The zone tree list. */
	private List<TreeNode> zoneTreeList = new ArrayList<TreeNode>();

	/** The zone tree addr list. */
	private List<TreeNode> zoneTreeAddrList = new ArrayList<TreeNode>();

	/** The zone name1. */
	private String zoneName1;

	/** The zone addr name. */
	private String zoneAddrName;

	/** The root location tree list. */
	private TreeNode rootLocationTreeList;

	/** The root address location tree list. */
	private TreeNode rootAddressLocationTreeList;

	/** The selected nodes. */
	private TreeNode[] selectedNodes;

	/** The selected general tree nodes. */
	private TreeNode[] selectedGeneralTreeNodes;

	/** The selected loc nodes. */
	private TreeNode[] selectedLocNodes;

	// template Specification variables
	/** The standard template. */
	private String standardTemplate = ParameterConst.TPL_DOC_PROCESS_TYPE_STANDARD;

	/** The supporting doc. */
	private String supportingDoc;

	/** The template type. */
	private String templateType;

	/** The specification type. */
	private String specificationType;

	/** The scqt doc. */
	private ScQtDoc scqtDoc;

	/** The ministry name. */
	private String ministryName;

	/** The search item type id. */
	private Long searchItemTypeId;

	/** The search spec type id. */
	private Long searchSpecTypeId;

	/** The search procurement id. */
	private Long searchProcurementId;
	
	/** The search created by. */
	private Long searchCreatedBy;

	/** The doc searchlist. */
	private List<ScQtDoc> docSearchlist;

	/** The check list item. */
	private List<ScQtCheckList> checkListItem;

	/** The zone list. */
	private List<SelectItem> zoneList;

	/** The spec name. */
	private String specName;

	/** The status id. */
	private Long statusId;

	/** The is record status. */
	private boolean isRecordStatus;

	/** The index. */
	private int index = -1;

	// technical Scoring Financial Scoring lists
	/** The technical scoring. */
	private List<ScQtCheckList> technicalScoring;

	/** The financial scoring. */
	private List<ScQtCheckList> financialScoring;

	/** The tech points. */
	private int techPoints;

	/** The fin points. */
	private int finPoints;

	
	/** The year list. */
	private List<SelectItem> yearList;

	/** The years display list. */
	private List<SelectItem> yearsDisplayList;

	/** The total suppliers. */
	private Integer totalSuppliers;

	/** The template spec bean. */
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();

	/** The delivery loc bean. */
	private List<BriefingZoneBean> deliveryLocBean;

	/** The delivery loc. */
	public List<ScQtDeliveryLoc> deliveryLoc;

	/** The delivery non zone list. */
	public List<ScQtDeliveryLoc> deliveryNonZoneList;

	/** The delete delivery list. */
	public List<ScQtDeliveryLoc> deleteDeliveryList;

	/** The select delivery list. */
	public List<ScQtDeliveryLoc> selectDeliveryList;

	/** The supplier criteria. */
	public ScQtSupplierCriteria supplierCriteria;

	/** The deliveyzone name. */
	public String deliveyzoneName;

	/** The technical checklist scoring. */
	private int technicalChecklistScoring;

	/** The checklist score type. */
	private String checklistScoreType;

	/** The company score type. */
	private String companyScoreType;

	/** The sc qt check list score. */
	private ScQtCheckListScore scQtCheckListScore;

	/** The sc qt contract score. */
	private ScQtCheckListScore scQtContractScore;

	/** The technical checklist action. */
	private List<SelectItem> technicalChecklistAction;

	/** The score varience. */
	private BigDecimal scoreVarience;

	/** The offer price package. */
	private ScQtScoreOfferPrice offerPricePackage;

	/** The Offer price item. */
	private ScQtScoreOfferPrice OfferPriceItem;

	/** The sc qt score weightage. */
	private ScQtScoreWeightage scQtScoreWeightage;

	/** The technical score setting. */
	private ScQtScoreSetting technicalScoreSetting;

	/** The financial score setting. */
	private ScQtScoreSetting financialScoreSetting;

	/** The demo score setting. */
	private ScQtScoreSetting demoScoreSetting;

	/** The recommendation package score. */
	private ScQtScoreSetting recommendationPackageScore;

	/** The recommendation score. */
	private ScQtScoreSetting recommendationScore;

	/** The procuremnt category. */
	private String procuremntCategory;

	/** The fulfilment type. */
	private String fulfilmentType;

	/** The technicalpackage. */
	private BigDecimal technicalpackage;

	/** The financialpackage. */
	private BigDecimal financialpackage;

	/** The recommendation package total. */
	private BigDecimal recommendationPackageTotal;

	/** The recommend technical score. */
	private BigDecimal recommendTechnicalScore;

	/** The recommendfinancial score. */
	private BigDecimal recommendfinancialScore;

	/** The tab active index. */
	private int tabActiveIndex;

	/** The sc qt remarks list. */
	private List<ScQtRemark> scQtRemarksList;

	/** The state id. */
	private Long stateId;

	/** The district id. */
	private Long districtId;

	/** The ministry id. */
	private Long ministryId;

	/** The department id. */
	private Long departmentId;

	/** The ptj id. */
	private Long ptjId;

	/** The state code. */
	private String stateCode;
	
 private boolean isState;

	/** The district code. */
	private String districtCode;

	/** The ministry list. */
	private List<PmOrgProfile> ministryList;

	/** The department list. */
	private List<PmOrgProfile> departmentList;

	/** The ptjt list. */
	private List<Object[]> ptjtList;

	/** The ptj profile dd list. */
	private List<Object[]> ptjProfileDDList;

	/** The delivery address select. */
	private List<PmAddressType> deliveryAddressSelect;

	/** The delivery zones. */
	private List<TplSpecZoneDtl> deliveryZones;

	

	/** The address zone bean. */
	private List<BriefingZoneBean> addressZoneBean;

	/** The panel item category. */
	private String panelItemCategory;

	/** The fin spec list. */
	private List<ScQtCheckList> finSpecList;

	/** The panel item id. */
	private Long panelItemId;

	/** The active index. */
	private int activeIndex;

	

	/** The ct panel zones. */
	private List<CtPanelZone> ctPanelZones;

	/** The ctpanel supplier. */
	private CtPanelSupplier ctpanelSupplier;

	/** The contractremark. */
	private ScQtRemark contractremark;

	/** The circular ver. */
	private CtCircularVer circularVer;

	/** The subject. */
	private String subject;

	/** The message. */
	private String message;

	/** The chair person pin num. */
	private String chairPersonPinNum;

	// Added By Sagar.. for Delete funcationality..
	/** The selected tree node. */
	private TreeNode selectedTreeNode;

	/** The ct search bean. */
	private CtCircularSearchBean ctSearchBean = new CtCircularSearchBean();

	/** The evaluation type item. */
	private List<SelectItem> evaluationTypeItem;

	/** The supplier selected tree node. */
	private TreeNode supplierSelectedTreeNode;

	/** The personnel. */
	private SmPersonnel personnel;

	/** The supplier branch. */
	private String supplierBranch;

	/** The supplier address. */
	private SmAddress supplierAddress;

	/** The supplier contact. */
	private String supplierContact;

	/** The supplier email. */
	private String supplierEmail;

	/** The ct circular ver. */
	private CtCircularVer ctCircularVer;

	/** The finspec checklist. */
	private List<ScQtCheckList> finspecChecklist;

	/** The button out come. */
	private String buttonOutCome;

	/** The all state code. */
	private PTJLocalityCode allStateCode;

	/** The is panel change. */
	private boolean isPanelChange;

	/** The new item category. */
	private String newItemCategory;

	/** The bankstmnt tomonth. */
	private String bankstmntTomonth;

	/** The temp index. */
	private int tempIndex;

	/** The ctzones. */
	private boolean ctzones = Boolean.FALSE;

	/** The select panel item. */
	private boolean selectPanelItem = Boolean.FALSE;

	/** The spec status id. */
	private Long specStatusId;

	/** The spec select stats. */
	private boolean specSelectStats = Boolean.FALSE;

	/** The selected check list. */
	private int selectedCheckList;

	/** The spec type. */
	private String specType;

	/** The to be remove list. */
	private List<ScQtCheckListScore> toBeRemoveList;

	/** The other score selected. */
	private List<ScQtCheckListScore> otherScoreSelected;

	/** The criteria type. */
	private String criteriaType;

	/** The other criteria type. */
	private String otherCriteriaType;

	/** The check list title. */
	private String checkListTitle;

	/** The Local level code. */
	private String LocalLevelCode;

	/** The delivery loc remove list. */
	private List<ScQtDeliveryLoc> deliveryLocRemoveList;

	/** The checklist remove list. */
	private List<ScQtCheckList> checklistRemoveList;
	// Added by vidyasagarReddy.. for making readonly based on userrole.
	/** The read only. */
	private boolean readOnly;

	/** The technical table row. */
	private int technicalTableRow;

	/** The financial table row. */
	private int financialTableRow;

	/** The total suppliers spec loc. */
	private List<TplSpecLoc> totalSuppliersSpecLoc;

	/** The state list. */
	private List<PmState> stateList;

	/** The district list. */
	private List<PmDistrict> districtList;

	/** The division list. */
	private List<PmDivision> divisionList;

	/** The Score type list. */
	private List<SelectItem> ScoreTypeList;

	/** The is tech standara checklist. */
	private boolean isTechStandaraChecklist;

	/** The is fin standara checklist. */
	private boolean isFinStandaraChecklist;

	/** The local out come. */
	private String localOutCome;

	

	/** The specification remarks. */
	private List<ScQtSpecificationRemark> specificationRemarks;

	/** The supplier state code. */
	private List<String> supplierStateCode;

	/** The supplier district code. */
	private List<String> supplierDistrictCode;

	/** The supplier division code. */
	private List<String> supplierDivisionCode;

	/** The division id. */
	private Long divisionId;

	/** The division code. */
	private String divisionCode;

	/** The select ct panel item. */
	private List<CtPanelItem> selectCtPanelItem;

	/** The spec complete. */
	private boolean specComplete;

	/** The beans. */
	private List<ZonesBean> beans = new ArrayList<ZonesBean>();

	/** The existing supplier location list. */
	private List<ScQtLocCriteria> existingSupplierLocationList;

	/** The is supplier validation. */
	private boolean isSupplierValidation;

	/** The technical spec count. */
	private int technicalSpecCount;

	/** The financial spec count. */
	private int financialSpecCount;

	/** The bankyears list. */
	private List<SelectItem> bankyearsList;

	/** The restricted supplier list. */
	private List<ScQtSupplier> restrictedSupplierList;

	/** The sm supplier. */
	private SmSupplier smSupplier;

	/** The sm mof account. */
	private SmMofAccount smMofAccount;

	/** The new template spec. */
	private boolean newTemplateSpec;

	/** The delete panel treenode. */
	private TreeNode deletePanelTreenode;

	/** The supplier location list. */
	private List<ScQtLocCriteria> supplierLocationList;

	/** The financial checklist action. */
	private List<SelectItem> financialChecklistAction;

	/** The templatespec Newable. */
	private boolean specNewable = Boolean.TRUE;

	/** For Create Specification Screen. */
	private QtnTenderSearchBean qtnTendertemplateSearchBean= new QtnTenderSearchBean();

	/** The temp zone name. */
	private String tempZoneName;

	/** The financial Item score setting. */
	private ScQtScoreSetting finacialItemScoreSetting;

	private BigDecimal financialItemScore;

	private BigDecimal totalScore = new BigDecimal(0);

	private String locationLevel;

	/** The search supplier data model. */
	private IPagingDataModel<PmAddressType> searchDeliveryAddressDataModel;

	

	private Long ptjGroupId;
	private int checkListYesIndex;
	private boolean technicalOtherChecklistSubmit;
	private boolean financialOtherChecklistSubmit;

	private List<ScQtSchedule> toBeRemoveSchedules;
	private List<ScQtProgressivePayment> toBeRemoveProgPayments;
	private boolean zoneSpecUpdate;
	private boolean zonesWarnMessageUpdate;
	private List<ScQtCategory> tobeRemoveCategoryCodes;
	private boolean isSpecItemWeightageRequired;
	private int ministryRemarksSize;
	private boolean isTechnicalUserScoringSchema;
	private boolean isFinancialUserScoringSchema;
	private String loginRoleType;
	private List<ScQtSupplier> toBeRemoveRestrictedSupplierList;
	/** The search panelItem. */
	private IPagingDataModel<CtPanelItem> searchDataModelPanelItem;
	/** The selected flag. */
	private boolean selectedFlag;
	
	
	/**
	 * Sets the panel suppliers.
	 * 
	 * @param panelSuppliers
	 *            the new panel suppliers
	 */
	public void setPanelSuppliers(List<SmSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}

	private ScQtApprovalRequest scQtApprovalDtls;

	/**
	 * Instantiates a new QT detail managed bean.
	 */
	public QTDetailManagedBean() {
		log.info("inside ");

		qtnTenderSearchBean = new QtnTenderSearchBean();
		cloneQtnTenderSearchBean = new QtnTenderSearchBean();

		userCommitteeSearchBean = new UserCommitteeSearchBean();

		cloneBusinessOwnerSearchBean = new QtnTenderSearchBean();
		userBusineeOwnerSearchBean = new UserCommitteeSearchBean();

		cloneDocumentSearchBean = new QtnTenderSearchBean();

		supplierSearchBean = new SupplierSearchBean();

		/*
		 * root2 = new DefaultTreeNode("Root", null); TreeNode node0 = new
		 * DefaultTreeNode("Clone All", root2); node0.setExpanded(true);
		 */

		/*
		 * CloneAll[] enumValues = CloneAll.values(); for (CloneAll cloneValue :
		 * enumValues) { TreeNode node1 = new
		 * DefaultTreeNode(cloneValue.getDisplayName(), node0); }
		 */
	}

	/**
	 * @return the qt
	 */
	public ScQt getQt() {
		return qt;
	}

	/**
	 * @param qt the qt to set
	 */
	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	/**
	 * @return the panelGroupItems
	 */
	public List<CtPanelItemGroup> getPanelGroupItems() {
		return panelGroupItems;
	}

	/**
	 * @param panelGroupItems the panelGroupItems to set
	 */
	public void setPanelGroupItems(List<CtPanelItemGroup> panelGroupItems) {
		this.panelGroupItems = panelGroupItems;
	}

	/**
	 * @return the panelItems
	 */
	public List<CtPanelItem> getPanelItems() {
		return panelItems;
	}

	/**
	 * @param panelItems the panelItems to set
	 */
	public void setPanelItems(List<CtPanelItem> panelItems) {
		this.panelItems = panelItems;
	}

	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}

	/**
	 * @param circularNo the circularNo to set
	 */
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	/**
	 * @return the circularName
	 */
	public String getCircularName() {
		return circularName;
	}

	/**
	 * @param circularName the circularName to set
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	/**
	 * @return the ministrySpecApprovers
	 */
	public List<PmUser> getMinistrySpecApprovers() {
		return ministrySpecApprovers;
	}

	/**
	 * @param ministrySpecApprovers the ministrySpecApprovers to set
	 */
	public void setMinistrySpecApprovers(List<PmUser> ministrySpecApprovers) {
		this.ministrySpecApprovers = ministrySpecApprovers;
	}

	/**
	 * @return the isZoneLocation
	 */
	public boolean isZoneLocation() {
		return isZoneLocation;
	}

	/**
	 * @param isZoneLocation the isZoneLocation to set
	 */
	public void setZoneLocation(boolean isZoneLocation) {
		this.isZoneLocation = isZoneLocation;
	}

	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}

	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	/**
	 * @return the rootZoanlTree
	 */
	public TreeNode getRootZoanlTree() {
		return rootZoanlTree;
	}

	/**
	 * @param rootZoanlTree the rootZoanlTree to set
	 */
	public void setRootZoanlTree(TreeNode rootZoanlTree) {
		this.rootZoanlTree = rootZoanlTree;
	}

	/**
	 * @return the isCheckAllList
	 */
	public boolean isCheckAllList() {
		return isCheckAllList;
	}

	/**
	 * @param isCheckAllList the isCheckAllList to set
	 */
	public void setCheckAllList(boolean isCheckAllList) {
		this.isCheckAllList = isCheckAllList;
	}

	/**
	 * @return the zoneGeneralTrees
	 */
	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	/**
	 * @param zoneGeneralTrees the zoneGeneralTrees to set
	 */
	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	/**
	 * Gets the org profile.
	 * 
	 * @return the org profile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	/**
	 * Sets the org profile.
	 * 
	 * @param orgProfile
	 *            the new org profile
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	/**
	 * Gets the committee approval users.
	 * 
	 * @return the committee approval users
	 */
	public DualListModel<PmUser> getCommitteeApprovalUsers() {
		return committeeApprovalUsers;
	}

	/**
	 * Sets the committee approval users.
	 * 
	 * @param committeeApprovalUsers
	 *            the new committee approval users
	 */
	public void setCommitteeApprovalUsers(
			DualListModel<PmUser> committeeApprovalUsers) {
		this.committeeApprovalUsers = committeeApprovalUsers;
	}

	/**
	 * Gets the budget approval users.
	 * 
	 * @return the budget approval users
	 */
	public DualListModel<PmUser> getBudgetApprovalUsers() {
		return budgetApprovalUsers;
	}

	/**
	 * Sets the budget approval users.
	 * 
	 * @param budgetApprovalUsers
	 *            the new budget approval users
	 */
	public void setBudgetApprovalUsers(DualListModel<PmUser> budgetApprovalUsers) {
		this.budgetApprovalUsers = budgetApprovalUsers;
	}

	/**
	 * Gets the briefing zone bean.
	 * 
	 * @return the briefing zone bean
	 */
	public List<BriefingZoneBean> getBriefingZoneBean() {
		return briefingZoneBean;
	}

	/**
	 * Sets the briefing zone bean.
	 * 
	 * @param briefingZoneBean
	 *            the new briefing zone bean
	 */
	public void setBriefingZoneBean(List<BriefingZoneBean> briefingZoneBean) {
		this.briefingZoneBean = briefingZoneBean;
	}

	/**
	 * Gets the budget approver name.
	 * 
	 * @return the budget approver name
	 */
	public List<PmUser> getBudgetApproverName() {
		return budgetApproverName;
	}

	/**
	 * Sets the budget approver name.
	 * 
	 * @param budgetApproverName
	 *            the new budget approver name
	 */
	public void setBudgetApproverName(List<PmUser> budgetApproverName) {
		this.budgetApproverName = budgetApproverName;
	}

	/**
	 * Gets the budget approver.
	 * 
	 * @return the budget approver
	 */
	public List<SelectItem> getBudgetApprover() {
		return budgetApprover;
	}

	/**
	 * Sets the budget approver.
	 * 
	 * @param budgetApprover
	 *            the new budget approver
	 */
	public void setBudgetApprover(List<SelectItem> budgetApprover) {
		this.budgetApprover = budgetApprover;
	}

	/**
	 * Gets the checklist attachment.
	 * 
	 * @return the checklist attachment
	 */
	public List<ScQtCheckListAttachment> getChecklistAttachment() {
		return checklistAttachment;
	}

	/**
	 * Sets the checklist attachment.
	 * 
	 * @param checklistAttachment
	 *            the new checklist attachment
	 */
	public void setChecklistAttachment(
			List<ScQtCheckListAttachment> checklistAttachment) {
		this.checklistAttachment = checklistAttachment;
	}

	/**
	 * Gets the attachments.
	 * 
	 * @return the attachments
	 */
	public List<ScQtApprovalAttachment> getAttachments() {
		return attachments;
	}

	/**
	 * Sets the attachments.
	 * 
	 * @param attachments
	 *            the new attachments
	 */
	public void setAttachments(List<ScQtApprovalAttachment> attachments) {
		this.attachments = attachments;
	}

	/**
	 * Gets the selected qt supplier list.
	 * 
	 * @return the selected qt supplier list
	 */
	public List<ScQtSupplier> getSelectedQtSupplierList() {
		return selectedQtSupplierList;
	}

	/**
	 * Sets the selected qt supplier list.
	 * 
	 * @param selectedQtSupplierList
	 *            the new selected qt supplier list
	 */
	public void setSelectedQtSupplierList(
			List<ScQtSupplier> selectedQtSupplierList) {
		this.selectedQtSupplierList = selectedQtSupplierList;
	}

	/**
	 * Gets the sc qt committee.
	 * 
	 * @return the sc qt committee
	 */
	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	/**
	 * Sets the sc qt committee.
	 * 
	 * @param scQtCommittee
	 *            the new sc qt committee
	 */
	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	/**
	 * Gets the tree nodes list.
	 * 
	 * @return the tree nodes list
	 */
	public List<TreeNode> getTreeNodesList() {
		return treeNodesList;
	}

	/**
	 * Sets the tree nodes list.
	 * 
	 * @param treeNodesList
	 *            the new tree nodes list
	 */
	public void setTreeNodesList(List<TreeNode> treeNodesList) {
		this.treeNodesList = treeNodesList;
	}

	/**
	 * Gets the comittee users.
	 * 
	 * @return the comittee users
	 */
	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	/**
	 * Sets the comittee users.
	 * 
	 * @param comitteeUsers
	 *            the new comittee users
	 */
	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	/**
	 * Gets the publishing period.
	 * 
	 * @return the publishing period
	 */
	public int getPublishingPeriod() {
		return publishingPeriod;
	}

	/**
	 * Sets the publishing period.
	 * 
	 * @param publishingPeriod
	 *            the new publishing period
	 */
	public void setPublishingPeriod(int publishingPeriod) {
		this.publishingPeriod = publishingPeriod;
	}

	/**
	 * Gets the selected pm user.
	 * 
	 * @return the selected pm user
	 */
	public List<PmUser> getSelectedPmUser() {
		return selectedPmUser;
	}

	/**
	 * Sets the selected pm user.
	 * 
	 * @param selectedPmUser
	 *            the new selected pm user
	 */
	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	/**
	 * Gets the selected clone committee.
	 * 
	 * @return the selected clone committee
	 */
	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	/**
	 * Sets the selected clone committee.
	 * 
	 * @param selectedCloneCommittee
	 *            the new selected clone committee
	 */
	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	/**
	 * Gets the select sc committe member.
	 * 
	 * @return the select sc committe member
	 */
	public ScCommitteeMember getSelectScCommitteMember() {
		return selectScCommitteMember;
	}

	/**
	 * Sets the select sc committe member.
	 * 
	 * @param selectScCommitteMember
	 *            the new select sc committe member
	 */
	public void setSelectScCommitteMember(
			ScCommitteeMember selectScCommitteMember) {
		this.selectScCommitteMember = selectScCommitteMember;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the identification no.
	 * 
	 * @return the identification no
	 */
	public String getIdentificationNo() {
		return identificationNo;
	}

	/**
	 * Sets the identification no.
	 * 
	 * @param identificationNo
	 *            the new identification no
	 */
	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	/**
	 * Gets the identification type.
	 * 
	 * @return the identification type
	 */
	public String getIdentificationType() {
		return identificationType;
	}

	/**
	 * Sets the identification type.
	 * 
	 * @param identificationType
	 *            the new identification type
	 */
	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	/**
	 * Gets the clone committe memebers.
	 * 
	 * @return the clone committe memebers
	 */
	public List<ScCommitteeMember> getCloneCommitteMemebers() {
		return cloneCommitteMemebers;
	}

	/**
	 * Sets the clone committe memebers.
	 * 
	 * @param cloneCommitteMemebers
	 *            the new clone committe memebers
	 */
	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}

	/**
	 * Gets the selected committe memebers.
	 * 
	 * @return the selected committe memebers
	 */
	public List<ScCommitteeMember> getSelectedCommitteMemebers() {
		return selectedCommitteMemebers;
	}

	/**
	 * Sets the selected committe memebers.
	 * 
	 * @param selectedCommitteMemebers
	 *            the new selected committe memebers
	 */
	public void setSelectedCommitteMemebers(
			List<ScCommitteeMember> selectedCommitteMemebers) {
		this.selectedCommitteMemebers = selectedCommitteMemebers;
	}

	/**
	 * Gets the selected sc qt committee.
	 * 
	 * @return the selected sc qt committee
	 */
	public ScQtCommittee getSelectedScQtCommittee() {
		return selectedScQtCommittee;
	}

	/**
	 * Sets the selected sc qt committee.
	 * 
	 * @param selectedScQtCommittee
	 *            the new selected sc qt committee
	 */
	public void setSelectedScQtCommittee(ScQtCommittee selectedScQtCommittee) {
		this.selectedScQtCommittee = selectedScQtCommittee;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.managedbean.BaseManagedBean#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.managedbean.BaseManagedBean#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the qt no.
	 * 
	 * @return the qt no
	 */
	public String getQtNo() {
		return qtNo;
	}

	/**
	 * Sets the qt no.
	 * 
	 * @param qtNo
	 *            the new qt no
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	/**
	 * Gets the procurement mode.
	 * 
	 * @return the procurement mode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * Sets the procurement mode.
	 * 
	 * @param procurementMode
	 *            the new procurement mode
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * Gets the created dt from.
	 * 
	 * @return the created dt from
	 */
	public Date getCreatedDtFrom() {
		return createdDtFrom;
	}

	/**
	 * Sets the created dt from.
	 * 
	 * @param createdDtFrom
	 *            the new created dt from
	 */
	public void setCreatedDtFrom(Date createdDtFrom) {
		this.createdDtFrom = createdDtFrom;
	}

	/**
	 * Gets the created dt to.
	 * 
	 * @return the created dt to
	 */
	public Date getCreatedDtTo() {
		return createdDtTo;
	}

	/**
	 * Sets the created dt to.
	 * 
	 * @param createdDtTo
	 *            the new created dt to
	 */
	public void setCreatedDtTo(Date createdDtTo) {
		this.createdDtTo = createdDtTo;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Gets the clone search committes.
	 * 
	 * @return the clone search committes
	 */
	public List<ScQtCommittee> getCloneSearchCommittes() {
		return cloneSearchCommittes;
	}

	/**
	 * Sets the clone search committes.
	 * 
	 * @param cloneSearchCommittes
	 *            the new clone search committes
	 */
	public void setCloneSearchCommittes(List<ScQtCommittee> cloneSearchCommittes) {
		this.cloneSearchCommittes = cloneSearchCommittes;
	}

	/**
	 * Gets the external users.
	 * 
	 * @return the external users
	 */
	public List<PmUser> getExternalUsers() {
		return externalUsers;
	}

	/**
	 * Sets the external users.
	 * 
	 * @param externalUsers
	 *            the new external users
	 */
	public void setExternalUsers(List<PmUser> externalUsers) {
		this.externalUsers = externalUsers;
	}

	/**
	 * Gets the selections.
	 * 
	 * @return the selections
	 */
	public List<SelectItem> getSelections() {
		return selections;
	}

	/**
	 * Sets the selections.
	 * 
	 * @param selections
	 *            the new selections
	 */
	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	/**
	 * Gets the committe member.
	 * 
	 * @return the committe member
	 */
	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}

	/**
	 * Sets the committe member.
	 * 
	 * @param committeMember
	 *            the new committe member
	 */
	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	/**
	 * Gets the sc committee member list.
	 * 
	 * @return the sc committee member list
	 */
	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}

	/**
	 * Sets the sc committee member list.
	 * 
	 * @param scCommitteeMemberList
	 *            the new sc committee member list
	 */
	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	/**
	 * Gets the zone tree addr list.
	 * 
	 * @return the zone tree addr list
	 */
	public List<TreeNode> getZoneTreeAddrList() {
		return zoneTreeAddrList;
	}

	/**
	 * Sets the zone tree addr list.
	 * 
	 * @param zoneTreeAddrList
	 *            the new zone tree addr list
	 */
	public void setZoneTreeAddrList(List<TreeNode> zoneTreeAddrList) {
		this.zoneTreeAddrList = zoneTreeAddrList;
	}

	/**
	 * Gets the zone trees.
	 * 
	 * @return the zone trees
	 */
	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	/**
	 * Sets the zone trees.
	 * 
	 * @param zoneTrees
	 *            the new zone trees
	 */
	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	/**
	 * Gets the zone tree list.
	 * 
	 * @return the zone tree list
	 */
	public List<TreeNode> getZoneTreeList() {
		return zoneTreeList;
	}

	/**
	 * Sets the zone tree list.
	 * 
	 * @param zoneTreeList
	 *            the new zone tree list
	 */
	public void setZoneTreeList(List<TreeNode> zoneTreeList) {
		this.zoneTreeList = zoneTreeList;
	}

	/**
	 * Gets the root location tree.
	 * 
	 * @return the root location tree
	 */
	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	/**
	 * Sets the root location tree.
	 * 
	 * @param rootLocationTree
	 *            the new root location tree
	 */
	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	/**
	 * Gets the zone name1.
	 * 
	 * @return the zone name1
	 */
	public String getZoneName1() {
		return zoneName1;
	}

	/**
	 * Sets the zone name1.
	 * 
	 * @param zoneName1
	 *            the new zone name1
	 */
	public void setZoneName1(String zoneName1) {
		this.zoneName1 = zoneName1;
	}

	/**
	 * Gets the zone addr name.
	 * 
	 * @return the zone addr name
	 */
	public String getZoneAddrName() {
		return zoneAddrName;
	}

	/**
	 * Sets the zone addr name.
	 * 
	 * @param zoneAddrName
	 *            the new zone addr name
	 */
	public void setZoneAddrName(String zoneAddrName) {
		this.zoneAddrName = zoneAddrName;
	}

	/**
	 * Gets the pm report section list.
	 * 
	 * @return the pm report section list
	 */
	public List<PmReportSection> getPmReportSectionList() {
		return pmReportSectionList;
	}

	/**
	 * Sets the pm report section list.
	 * 
	 * @param pmReportSectionList
	 *            the new pm report section list
	 */
	public void setPmReportSectionList(List<PmReportSection> pmReportSectionList) {
		this.pmReportSectionList = pmReportSectionList;
	}

	/**
	 * Gets the selected pm report section list.
	 * 
	 * @return the selected pm report section list
	 */
	public List<PmReportSection> getSelectedPmReportSectionList() {
		return selectedPmReportSectionList;
	}

	/**
	 * Sets the selected pm report section list.
	 * 
	 * @param selectedPmReportSectionList
	 *            the new selected pm report section list
	 */
	public void setSelectedPmReportSectionList(
			List<PmReportSection> selectedPmReportSectionList) {
		this.selectedPmReportSectionList = selectedPmReportSectionList;
	}

	/**
	 * Gets the root location tree list.
	 * 
	 * @return the root location tree list
	 */
	public TreeNode getRootLocationTreeList() {
		return rootLocationTreeList;
	}

	/**
	 * Sets the root location tree list.
	 * 
	 * @param rootLocationTreeList
	 *            the new root location tree list
	 */
	public void setRootLocationTreeList(TreeNode rootLocationTreeList) {
		this.rootLocationTreeList = rootLocationTreeList;
	}

	/**
	 * Gets the root address location tree list.
	 * 
	 * @return the root address location tree list
	 */
	public TreeNode getRootAddressLocationTreeList() {
		return rootAddressLocationTreeList;
	}

	/**
	 * Sets the root address location tree list.
	 * 
	 * @param rootAddressLocationTreeList
	 *            the new root address location tree list
	 */
	public void setRootAddressLocationTreeList(
			TreeNode rootAddressLocationTreeList) {
		this.rootAddressLocationTreeList = rootAddressLocationTreeList;
	}

	/**
	 * Gets the deduction payment array.
	 * 
	 * @return the deduction payment array
	 */
	public String getDeductionPaymentArray() {
		return deductionPaymentArray;
	}

	/**
	 * Sets the deduction payment array.
	 * 
	 * @param deductionPaymentArray
	 *            the new deduction payment array
	 */
	public void setDeductionPaymentArray(String deductionPaymentArray) {
		this.deductionPaymentArray = deductionPaymentArray;
	}

	/**
	 * Checks if is percentage field.
	 * 
	 * @return true, if is percentage field
	 */
	public boolean isPercentageField() {
		return percentageField;
	}

	/**
	 * Sets the percentage field.
	 * 
	 * @param percentageField
	 *            the new percentage field
	 */
	public void setPercentageField(boolean percentageField) {
		this.percentageField = percentageField;
	}

	/**
	 * Checks if is description field.
	 * 
	 * @return true, if is description field
	 */
	public boolean isDescriptionField() {
		return descriptionField;
	}

	/**
	 * Sets the description field.
	 * 
	 * @param descriptionField
	 *            the new description field
	 */
	public void setDescriptionField(boolean descriptionField) {
		this.descriptionField = descriptionField;
	}

	/**
	 * Checks if is amount field.
	 * 
	 * @return true, if is amount field
	 */
	public boolean isAmountField() {
		return amountField;
	}

	/**
	 * Sets the amount field.
	 * 
	 * @param amountField
	 *            the new amount field
	 */
	public void setAmountField(boolean amountField) {
		this.amountField = amountField;
	}

	/**
	 * Gets the qtn tender search bean.
	 * 
	 * @return the qtn tender search bean
	 */
	public QtnTenderSearchBean getQtnTenderSearchBean() {
		return qtnTenderSearchBean;
	}

	/**
	 * Sets the qtn tender search bean.
	 * 
	 * @param qtnTenderSearchBean
	 *            the new qtn tender search bean
	 */
	public void setQtnTenderSearchBean(QtnTenderSearchBean qtnTenderSearchBean) {
		this.qtnTenderSearchBean = qtnTenderSearchBean;
	}

	/**
	 * Gets the clone qtn tender search bean.
	 * 
	 * @return the clone qtn tender search bean
	 */
	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}

	/**
	 * Sets the clone qtn tender search bean.
	 * 
	 * @param cloneQtnTenderSearchBean
	 *            the new clone qtn tender search bean
	 */
	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	/**
	 * Gets the root2.
	 * 
	 * @return the root2
	 */
	public TreeNode getRoot2() {
		return root2;
	}

	/**
	 * Sets the root2.
	 * 
	 * @param root2
	 *            the new root2
	 */
	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	/**
	 * Gets the root3.
	 * 
	 * @return the root3
	 */
	public TreeNode getRoot3() {
		return root3;
	}

	/**
	 * Sets the root3.
	 * 
	 * @param root3
	 *            the new root3
	 */
	public void setRoot3(TreeNode root3) {
		this.root3 = root3;
	}

	/**
	 * Gets the selected nodes.
	 * 
	 * @return the selected nodes
	 */
	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Sets the selected nodes.
	 * 
	 * @param selectedNodes
	 *            the new selected nodes
	 */
	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	/**
	 * Gets the category l1s.
	 * 
	 * @return the category l1s
	 */
	public List<PmCategoryL1> getCategoryL1s() {
		return categoryL1s;
	}

	/**
	 * Sets the category l1s.
	 * 
	 * @param categoryL1s
	 *            the new category l1s
	 */
	public void setCategoryL1s(List<PmCategoryL1> categoryL1s) {
		this.categoryL1s = categoryL1s;
	}

	/**
	 * Gets the category l2s.
	 * 
	 * @return the category l2s
	 */
	public List<PmCategoryL2> getCategoryL2s() {
		return categoryL2s;
	}

	/**
	 * Sets the category l2s.
	 * 
	 * @param categoryL2s
	 *            the new category l2s
	 */
	public void setCategoryL2s(List<PmCategoryL2> categoryL2s) {
		this.categoryL2s = categoryL2s;
	}

	/**
	 * Gets the category l3s.
	 * 
	 * @return the category l3s
	 */
	public List<PmCategoryL3> getCategoryL3s() {
		return categoryL3s;
	}

	/**
	 * Sets the category l3s.
	 * 
	 * @param categoryL3s
	 *            the new category l3s
	 */
	public void setCategoryL3s(List<PmCategoryL3> categoryL3s) {
		this.categoryL3s = categoryL3s;
	}

	/**
	 * Gets the qt number.
	 * 
	 * @return the qt number
	 */
	public String getQtNumber() {
		return qtNumber;
	}

	/**
	 * Sets the qt number.
	 * 
	 * @param qtNumber
	 *            the new qt number
	 */
	public void setQtNumber(String qtNumber) {
		this.qtNumber = qtNumber;
	}

	/**
	 * Gets the states.
	 * 
	 * @return the states
	 */
	public List<PmState> getStates() {
		return states;
	}

	/**
	 * Sets the states.
	 * 
	 * @param states
	 *            the new states
	 */
	public void setStates(List<PmState> states) {
		this.states = states;
	}

	/**
	 * Gets the districts.
	 * 
	 * @return the districts
	 */
	public List<PmDistrict> getDistricts() {
		return districts;
	}

	/**
	 * Sets the districts.
	 * 
	 * @param districts
	 *            the new districts
	 */
	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}

	/**
	 * Gets the gosodos.
	 * 
	 * @return the gosodos
	 */
	public List<ScQtBudget> getGosodos() {
		return gosodos;
	}

	/**
	 * Sets the gosodos.
	 * 
	 * @param gosodos
	 *            the new gosodos
	 */
	public void setGosodos(List<ScQtBudget> gosodos) {
		this.gosodos = gosodos;
	}

	/**
	 * Gets the user ministry list.
	 * 
	 * @return the user ministry list
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * Sets the user ministry list.
	 * 
	 * @param userMinistryList
	 *            the new user ministry list
	 */
	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	/**
	 * Gets the user department list.
	 * 
	 * @return the user department list
	 */
	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	/**
	 * Sets the user department list.
	 * 
	 * @param userDepartmentList
	 *            the new user department list
	 */
	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}

	/**
	 * Gets the user division list.
	 * 
	 * @return the user division list
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * Sets the user division list.
	 * 
	 * @param userDivisionList
	 *            the new user division list
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * Gets the go list.
	 * 
	 * @return the go list
	 */
	public List<PmGo> getGoList() {
		return goList;
	}

	/**
	 * Sets the go list.
	 * 
	 * @param goList
	 *            the new go list
	 */
	public void setGoList(List<PmGo> goList) {
		this.goList = goList;
	}

	/**
	 * Gets the so list.
	 * 
	 * @return the so list
	 */
	public List<PmSo> getSoList() {
		return soList;
	}

	/**
	 * Sets the so list.
	 * 
	 * @param soList
	 *            the new so list
	 */
	public void setSoList(List<PmSo> soList) {
		this.soList = soList;
	}

	/**
	 * Gets the do list.
	 * 
	 * @return the do list
	 */
	public List<PmDo> getDoList() {
		return doList;
	}

	/**
	 * Sets the do list.
	 * 
	 * @param doList
	 *            the new do list
	 */
	public void setDoList(List<PmDo> doList) {
		this.doList = doList;
	}

	/**
	 * Gets the tender criteria.
	 * 
	 * @return the tender criteria
	 */
	public ScQt getTenderCriteria() {
		return tenderCriteria;
	}

	/**
	 * Sets the tender criteria.
	 * 
	 * @param tenderCriteria
	 *            the new tender criteria
	 */
	public void setTenderCriteria(ScQt tenderCriteria) {
		this.tenderCriteria = tenderCriteria;
	}

	/*
	 * public String getMultiLoc() { return multiLoc; }
	 * 
	 * public void setMultiLoc(String multiLoc) { this.multiLoc = multiLoc; }
	 */

	/**
	 * Gets the selected boolean1.
	 * 
	 * @return the selected boolean1
	 */
	public String getSelectedBoolean1() {
		return selectedBoolean1;
	}

	/**
	 * Sets the selected boolean1.
	 * 
	 * @param selectedBoolean1
	 *            the new selected boolean1
	 */
	public void setSelectedBoolean1(String selectedBoolean1) {
		this.selectedBoolean1 = selectedBoolean1;
	}

	/**
	 * Gets the selected user ministry.
	 * 
	 * @return the selected user ministry
	 */
	public PmOrgProfile getSelectedUserMinistry() {
		return selectedUserMinistry;
	}

	/**
	 * Sets the selected user ministry.
	 * 
	 * @param selectedUserMinistry
	 *            the new selected user ministry
	 */
	public void setSelectedUserMinistry(PmOrgProfile selectedUserMinistry) {
		this.selectedUserMinistry = selectedUserMinistry;
	}

	/**
	 * Gets the selected user department.
	 * 
	 * @return the selected user department
	 */
	public PmOrgProfile getSelectedUserDepartment() {
		return selectedUserDepartment;
	}

	/**
	 * Sets the selected user department.
	 * 
	 * @param selectedUserDepartment
	 *            the new selected user department
	 */
	public void setSelectedUserDepartment(PmOrgProfile selectedUserDepartment) {
		this.selectedUserDepartment = selectedUserDepartment;
	}

	/**
	 * Gets the minstry code.
	 * 
	 * @return the minstry code
	 */
	public String getMinstryCode() {
		return minstryCode;
	}

	/**
	 * Sets the minstry code.
	 * 
	 * @param minstryCode
	 *            the new minstry code
	 */
	public void setMinstryCode(String minstryCode) {
		this.minstryCode = minstryCode;
	}

	/**
	 * Gets the dept code.
	 * 
	 * @return the dept code
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * Sets the dept code.
	 * 
	 * @param deptCode
	 *            the new dept code
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	/**
	 * Gets the division code.
	 * 
	 * @return the division code
	 */
	public String getDivisionCode() {
		return divisionCode;
	}

	/**
	 * Sets the division code.
	 * 
	 * @param divisionCode
	 *            the new division code
	 */
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	/**
	 * Gets the panel suppliers.
	 * 
	 * @return the panel suppliers
	 */
	public List<SmSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}

	/**
	 * Gets the tab list.
	 * 
	 * @return the tab list
	 */
	public List<TabBean> getTabList() {
		return tabList;
	}

	/**
	 * Sets the tab list.
	 * 
	 * @param tabList
	 *            the new tab list
	 */
	public void setTabList(List<TabBean> tabList) {
		this.tabList = tabList;
	}

	/**
	 * Gets the check box bpk.
	 * 
	 * @return the check box bpk
	 */
	public String getCheckBoxBPK() {
		return checkBoxBPK;
	}

	/**
	 * Sets the check box bpk.
	 * 
	 * @param checkBoxBPK
	 *            the new check box bpk
	 */
	public void setCheckBoxBPK(String checkBoxBPK) {
		this.checkBoxBPK = checkBoxBPK;
	}

	/**
	 * Gets the check box bp k1.
	 * 
	 * @return the check box bp k1
	 */
	public List<String> getCheckBoxBPK1() {
		return checkBoxBPK1;
	}

	/**
	 * Sets the check box bp k1.
	 * 
	 * @param checkBoxBPK1
	 *            the new check box bp k1
	 */
	public void setCheckBoxBPK1(List<String> checkBoxBPK1) {
		this.checkBoxBPK1 = checkBoxBPK1;
	}

	/**
	 * Gets the standard document.
	 * 
	 * @return the standard document
	 */
	public String getStandardDocument() {
		return standardDocument;
	}

	/**
	 * Sets the standard document.
	 * 
	 * @param standardDocument
	 *            the new standard document
	 */
	public void setStandardDocument(String standardDocument) {
		this.standardDocument = standardDocument;
	}

	/**
	 * Gets the approval typelist.
	 * 
	 * @return the approval typelist
	 */
	public List<String> getApprovalTypelist() {
		return approvalTypelist;
	}

	/**
	 * Sets the approval typelist.
	 * 
	 * @param approvalTypelist
	 *            the new approval typelist
	 */
	public void setApprovalTypelist(List<String> approvalTypelist) {
		this.approvalTypelist = approvalTypelist;
	}

	/**
	 * Gets the active index.
	 * 
	 * @return the active index
	 */
	public int getActiveIndex() {
		return activeIndex;
	}

	/**
	 * Sets the active index.
	 * 
	 * @param activeIndex
	 *            the new active index
	 */
	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	/**
	 * Gets the sub tab index.
	 * 
	 * @return the sub tab index
	 */
	public int getSubTabIndex() {
		return subTabIndex;
	}

	/**
	 * Sets the sub tab index.
	 * 
	 * @param subTabIndex
	 *            the new sub tab index
	 */
	public void setSubTabIndex(int subTabIndex) {
		this.subTabIndex = subTabIndex;
	}

	/**
	 * Gets the qt briefing.
	 * 
	 * @return the qt briefing
	 */
	public ScQtBsvDtl getQtBriefing() {
		return qtBriefing;
	}

	/**
	 * Sets the qt briefing.
	 * 
	 * @param qtBriefing
	 *            the new qt briefing
	 */
	public void setQtBriefing(ScQtBsvDtl qtBriefing) {
		this.qtBriefing = qtBriefing;
	}

	/**
	 * Gets the qt doc.
	 * 
	 * @return the qt doc
	 */
	public ScQtDoc getQtDoc() {
		return qtDoc;
	}

	/**
	 * Sets the qt doc.
	 * 
	 * @param qtDoc
	 *            the new qt doc
	 */
	public void setQtDoc(ScQtDoc qtDoc) {
		this.qtDoc = qtDoc;
	}

	/**
	 * Gets the sc qt docs.
	 * 
	 * @return the sc qt docs
	 */
	public List<ScQtDoc> getScQtDocs() {
		return scQtDocs;
	}

	/**
	 * Sets the sc qt docs.
	 * 
	 * @param scQtDocs
	 *            the new sc qt docs
	 */
	public void setScQtDocs(List<ScQtDoc> scQtDocs) {
		this.scQtDocs = scQtDocs;
	}

	/**
	 * Gets the clone all list.
	 * 
	 * @return the clone all list
	 */
	public List<CloneAll> getCloneAllList() {
		return cloneAllList;
	}

	/**
	 * Sets the clone all list.
	 * 
	 * @param cloneAllList
	 *            the new clone all list
	 */
	public void setCloneAllList(List<CloneAll> cloneAllList) {
		this.cloneAllList = cloneAllList;
	}

	/**
	 * Gets the sm mof account.
	 * 
	 * @return the sm mof account
	 */
	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	/**
	 * Sets the sm mof account.
	 * 
	 * @param smMofAccount
	 *            the new sm mof account
	 */
	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	/**
	 * Gets the ct circulars.
	 * 
	 * @return the ct circulars
	 */
	public List<CtCircular> getCtCirculars() {
		return ctCirculars;
	}

	/**
	 * Sets the ct circulars.
	 * 
	 * @param ctCirculars
	 *            the new ct circulars
	 */
	public void setCtCirculars(List<CtCircular> ctCirculars) {
		this.ctCirculars = ctCirculars;
	}

	/**
	 * Gets the sc qt contact persons.
	 * 
	 * @return the sc qt contact persons
	 */
	public List<ScQtContactPerson> getScQtContactPersons() {
		return scQtContactPersons;
	}

	/**
	 * Sets the sc qt contact persons.
	 * 
	 * @param scQtContactPersons
	 *            the new sc qt contact persons
	 */
	public void setScQtContactPersons(List<ScQtContactPerson> scQtContactPersons) {
		this.scQtContactPersons = scQtContactPersons;
	}

	/**
	 * Gets the qt remark.
	 * 
	 * @return the qt remark
	 */
	public ScQtRemark getQtRemark() {
		return qtRemark;
	}

	/**
	 * Sets the qt remark.
	 * 
	 * @param qtRemark
	 *            the new qt remark
	 */
	public void setQtRemark(ScQtRemark qtRemark) {
		this.qtRemark = qtRemark;
	}

	/**
	 * Gets the sc remark.
	 * 
	 * @return the sc remark
	 */
	public ScRemark getScRemark() {
		return scRemark;
	}

	/**
	 * Sets the sc remark.
	 * 
	 * @param scRemark
	 *            the new sc remark
	 */
	public void setScRemark(ScRemark scRemark) {
		this.scRemark = scRemark;
	}

	/**
	 * Gets the ct panel suppliers.
	 * 
	 * @return the ct panel suppliers
	 */
	public List<CtPanelSupplier> getCtPanelSuppliers() {
		return ctPanelSuppliers;
	}

	/**
	 * Sets the ct panel suppliers.
	 * 
	 * @param ctPanelSuppliers
	 *            the new ct panel suppliers
	 */
	public void setCtPanelSuppliers(List<CtPanelSupplier> ctPanelSuppliers) {
		this.ctPanelSuppliers = ctPanelSuppliers;
	}

	/**
	 * Gets the sc qt checklists.
	 * 
	 * @return the sc qt checklists
	 */
	public List<ScQtCheckList> getScQtChecklists() {
		return scQtChecklists;
	}

	/**
	 * Sets the sc qt checklists.
	 * 
	 * @param scQtChecklists
	 *            the new sc qt checklists
	 */
	public void setScQtChecklists(List<ScQtCheckList> scQtChecklists) {
		this.scQtChecklists = scQtChecklists;
	}

	/**
	 * Gets the qt check list.
	 * 
	 * @return the qt check list
	 */
	public ScQtCheckList getQtCheckList() {
		return qtCheckList;
	}

	/**
	 * Sets the qt check list.
	 * 
	 * @param qtCheckList
	 *            the new qt check list
	 */
	public void setQtCheckList(ScQtCheckList qtCheckList) {
		this.qtCheckList = qtCheckList;
	}

	/**
	 * Gets the qt contact person.
	 * 
	 * @return the qt contact person
	 */
	public ScQtContactPerson getQtContactPerson() {
		return qtContactPerson;
	}

	/**
	 * Sets the qt contact person.
	 * 
	 * @param qtContactPerson
	 *            the new qt contact person
	 */
	public void setQtContactPerson(ScQtContactPerson qtContactPerson) {
		this.qtContactPerson = qtContactPerson;
	}

	/**
	 * Gets the veryfiers.
	 * 
	 * @return the veryfiers
	 */
	public List<PmUser> getVeryfiers() {
		return veryfiers;
	}

	/**
	 * Sets the veryfiers.
	 * 
	 * @param veryfiers
	 *            the new veryfiers
	 */
	public void setVeryfiers(List<PmUser> veryfiers) {
		this.veryfiers = veryfiers;
	}

	/**
	 * Gets the approvers.
	 * 
	 * @return the approvers
	 */
	public List<PmUser> getApprovers() {
		return approvers;
	}

	/**
	 * Sets the approvers.
	 * 
	 * @param approvers
	 *            the new approvers
	 */
	public void setApprovers(List<PmUser> approvers) {
		this.approvers = approvers;
	}

	/**
	 * Gets the bbk approvers.
	 * 
	 * @return the bbk approvers
	 */
	public List<PmUser> getBbkApprovers() {
		return bbkApprovers;
	}

	/**
	 * Sets the bbk approvers.
	 * 
	 * @param bbkApprovers
	 *            the new bbk approvers
	 */
	public void setBbkApprovers(List<PmUser> bbkApprovers) {
		this.bbkApprovers = bbkApprovers;
	}

	/**
	 * Gets the out come.
	 * 
	 * @return the out come
	 */
	public String getOutCome() {
		return outCome;
	}

	/**
	 * Sets the out come.
	 * 
	 * @param outCome
	 *            the new out come
	 */
	public void setOutCome(String outCome) {
		this.outCome = outCome;
	}

	/**
	 * Gets the process name.
	 * 
	 * @return the process name
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * Sets the process name.
	 * 
	 * @param processName
	 *            the new process name
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * Gets the countries.
	 * 
	 * @return the countries
	 */
	public List<PmCountry> getCountries() {
		return countries;
	}

	/**
	 * Sets the countries.
	 * 
	 * @param countries
	 *            the new countries
	 */
	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	/**
	 * Gets the clone all data.
	 * 
	 * @return the clone all data
	 */
	public CloneAll[] getCloneAllData() {
		return CloneAll.values();
	}

	/**
	 * Gets the pp plan.
	 * 
	 * @return the pp plan
	 */
	public PpPlan getPpPlan() {
		return ppPlan;
	}

	/**
	 * Sets the pp plan.
	 * 
	 * @param ppPlan
	 *            the new pp plan
	 */
	public void setPpPlan(PpPlan ppPlan) {
		this.ppPlan = ppPlan;
	}

	/**
	 * Gets the pm org profile.
	 * 
	 * @return the pm org profile
	 */
	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	/**
	 * Sets the pm org profile.
	 * 
	 * @param pmOrgProfile
	 *            the new pm org profile
	 */
	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	/**
	 * Gets the sc qt approval.
	 * 
	 * @return the sc qt approval
	 */
	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	/**
	 * Sets the sc qt approval.
	 * 
	 * @param scQtApproval
	 *            the new sc qt approval
	 */
	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	/**
	 * Gets the upload update id.
	 * 
	 * @return the upload update id
	 */
	public String getUploadUpdateId() {
		return uploadUpdateId;
	}

	/**
	 * Sets the upload update id.
	 * 
	 * @param uploadUpdateId
	 *            the new upload update id
	 */
	public void setUploadUpdateId(String uploadUpdateId) {
		this.uploadUpdateId = uploadUpdateId;
	}

	/**
	 * Gets the selected briefing details.
	 * 
	 * @return the selected briefing details
	 */
	public List<ScQtBsvDtl> getSelectedBriefingDetails() {
		return selectedBriefingDetails;
	}

	/**
	 * Sets the selected briefing details.
	 * 
	 * @param selectedBriefingDetails
	 *            the new selected briefing details
	 */
	public void setSelectedBriefingDetails(
			List<ScQtBsvDtl> selectedBriefingDetails) {
		this.selectedBriefingDetails = selectedBriefingDetails;
	}

	/**
	 * Gets the selected site visit details.
	 * 
	 * @return the selected site visit details
	 */
	public List<ScQtBsvDtl> getSelectedSiteVisitDetails() {
		return selectedSiteVisitDetails;
	}

	/**
	 * Sets the selected site visit details.
	 * 
	 * @param selectedSiteVisitDetails
	 *            the new selected site visit details
	 */
	public void setSelectedSiteVisitDetails(
			List<ScQtBsvDtl> selectedSiteVisitDetails) {
		this.selectedSiteVisitDetails = selectedSiteVisitDetails;
	}

	/**
	 * Gets the selected contact persons.
	 * 
	 * @return the selected contact persons
	 */
	public List<ScQtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}

	/**
	 * Sets the selected contact persons.
	 * 
	 * @param selectedContactPersons
	 *            the new selected contact persons
	 */
	public void setSelectedContactPersons(
			List<ScQtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}

	/**
	 * Gets the selected check lists.
	 * 
	 * @return the selected check lists
	 */
	public List<ScQtCheckList> getSelectedCheckLists() {
		return selectedCheckLists;
	}

	/**
	 * Sets the selected check lists.
	 * 
	 * @param selectedCheckLists
	 *            the new selected check lists
	 */
	public void setSelectedCheckLists(List<ScQtCheckList> selectedCheckLists) {
		this.selectedCheckLists = selectedCheckLists;
	}

	/**
	 * Gets the selected gosodo budgets.
	 * 
	 * @return the selected gosodo budgets
	 */
	public List<ScQtBudget> getSelectedGosodoBudgets() {
		return selectedGosodoBudgets;
	}

	/**
	 * Sets the selected gosodo budgets.
	 * 
	 * @param selectedGosodoBudgets
	 *            the new selected gosodo budgets
	 */
	public void setSelectedGosodoBudgets(List<ScQtBudget> selectedGosodoBudgets) {
		this.selectedGosodoBudgets = selectedGosodoBudgets;
	}

	/**
	 * Gets the selected clone tender list.
	 * 
	 * @return the selected clone tender list
	 */
	public List<ScQt> getSelectedCloneTenderList() {
		return selectedCloneTenderList;
	}

	/**
	 * Sets the selected clone tender list.
	 * 
	 * @param selectedCloneTenderList
	 *            the new selected clone tender list
	 */
	public void setSelectedCloneTenderList(List<ScQt> selectedCloneTenderList) {
		this.selectedCloneTenderList = selectedCloneTenderList;
	}

	/**
	 * Gets the selectedpanelsupplier.
	 * 
	 * @return the selectedpanelsupplier
	 */
	public List<SmMofAccount> getSelectedpanelsupplier() {
		return selectedpanelsupplier;
	}

	/**
	 * Sets the selectedpanelsupplier.
	 * 
	 * @param selectedpanelsupplier
	 *            the new selectedpanelsupplier
	 */
	public void setSelectedpanelsupplier(
			List<SmMofAccount> selectedpanelsupplier) {
		this.selectedpanelsupplier = selectedpanelsupplier;
	}

	/**
	 * Gets the panel supplier list.
	 * 
	 * @return the panel supplier list
	 */
	public List<CtPanelSupplier> getPanelSupplierList() {
		return panelSupplierList;
	}

	/**
	 * Sets the panel supplier list.
	 * 
	 * @param panelSupplierList
	 *            the new panel supplier list
	 */
	public void setPanelSupplierList(List<CtPanelSupplier> panelSupplierList) {
		this.panelSupplierList = panelSupplierList;
	}

	/**
	 * Gets the selected clone all list.
	 * 
	 * @return the selected clone all list
	 */
	public List<CloneAll> getSelectedCloneAllList() {
		return selectedCloneAllList;
	}

	/**
	 * Sets the selected clone all list.
	 * 
	 * @param selectedCloneAllList
	 *            the new selected clone all list
	 */
	public void setSelectedCloneAllList(List<CloneAll> selectedCloneAllList) {
		this.selectedCloneAllList = selectedCloneAllList;
	}

	/**
	 * Checks if is editable.
	 * 
	 * @return true, if is editable
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * Sets the editable.
	 * 
	 * @param isEditable
	 *            the new editable
	 */
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	/**
	 * Gets the sc qt deduction list.
	 * 
	 * @return the sc qt deduction list
	 */
	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	/**
	 * Sets the sc qt deduction list.
	 * 
	 * @param scQtDeductionList
	 *            the new sc qt deduction list
	 */
	public void setScQtDeductionList(List<ScQtDeduction> scQtDeductionList) {
		this.scQtDeductionList = scQtDeductionList;
	}

	/**
	 * Gets the selected sc qt deductions.
	 * 
	 * @return the selected sc qt deductions
	 */
	public List<ScQtDeduction> getSelectedScQtDeductions() {
		return selectedScQtDeductions;
	}

	/**
	 * Sets the selected sc qt deductions.
	 * 
	 * @param selectedScQtDeductions
	 *            the new selected sc qt deductions
	 */
	public void setSelectedScQtDeductions(
			List<ScQtDeduction> selectedScQtDeductions) {
		this.selectedScQtDeductions = selectedScQtDeductions;
	}

	/**
	 * Gets the selected deduction clauses.
	 * 
	 * @return the selected deduction clauses
	 */
	public List<ScQtDeductionClause> getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	/**
	 * Sets the selected deduction clauses.
	 * 
	 * @param selectedDeductionClauses
	 *            the new selected deduction clauses
	 */
	public void setSelectedDeductionClauses(
			List<ScQtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	/**
	 * Gets the qt deduction.
	 * 
	 * @return the qt deduction
	 */
	public ScQtDeduction getQtDeduction() {
		return qtDeduction;
	}

	/**
	 * Sets the qt deduction.
	 * 
	 * @param qtDeduction
	 *            the new qt deduction
	 */
	public void setQtDeduction(ScQtDeduction qtDeduction) {
		this.qtDeduction = qtDeduction;
	}

	/**
	 * Gets the qt deduction clause.
	 * 
	 * @return the qt deduction clause
	 */
	public ScQtDeductionClause getQtDeductionClause() {
		return qtDeductionClause;
	}

	/**
	 * Sets the qt deduction clause.
	 * 
	 * @param qtDeductionClause
	 *            the new qt deduction clause
	 */
	public void setQtDeductionClause(ScQtDeductionClause qtDeductionClause) {
		this.qtDeductionClause = qtDeductionClause;
	}

	/**
	 * /** Gets the item name.
	 * 
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets the item name.
	 * 
	 * @param itemName
	 *            the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Gets the circular coverage.
	 * 
	 * @return the circular coverage
	 */
	public String getCircularCoverage() {
		return circularCoverage;
	}

	/**
	 * Sets the circular coverage.
	 * 
	 * @param circularCoverage
	 *            the new circular coverage
	 */
	public void setCircularCoverage(String circularCoverage) {
		this.circularCoverage = circularCoverage;
	}

	/**
	 * Gets the total search supplier.
	 * 
	 * @return the total search supplier
	 */
	public int getTotalSearchSupplier() {
		return totalSearchSupplier;
	}

	/**
	 * Sets the total search supplier.
	 * 
	 * @param totalSearchSupplier
	 *            the new total search supplier
	 */
	public void setTotalSearchSupplier(int totalSearchSupplier) {
		this.totalSearchSupplier = totalSearchSupplier;
	}

	/**
	 * Gets the search panel suppliers node.
	 * 
	 * @return the search panel suppliers node
	 */
	public TreeNode getSearchPanelSuppliersNode() {
		return searchPanelSuppliersNode;
	}

	/**
	 * Sets the search panel suppliers node.
	 * 
	 * @param searchPanelSuppliersNode
	 *            the new search panel suppliers node
	 */
	public void setSearchPanelSuppliersNode(TreeNode searchPanelSuppliersNode) {
		this.searchPanelSuppliersNode = searchPanelSuppliersNode;
	}

	/**
	 * Gets the suppliers node.
	 * 
	 * @return the suppliers node
	 */
	public TreeNode getSuppliersNode() {
		return suppliersNode;
	}

	/**
	 * Sets the suppliers node.
	 * 
	 * @param suppliersNode
	 *            the new suppliers node
	 */
	public void setSuppliersNode(TreeNode suppliersNode) {
		this.suppliersNode = suppliersNode;
	}

	/**
	 * Gets the search supplier list.
	 * 
	 * @return the search supplier list
	 */
	public List<SmSupplier> getSearchSupplierList() {
		return searchSupplierList;
	}

	/**
	 * Sets the search supplier list.
	 * 
	 * @param searchSupplierList
	 *            the new search supplier list
	 */
	public void setSearchSupplierList(List<SmSupplier> searchSupplierList) {
		this.searchSupplierList = searchSupplierList;
	}

	/**
	 * Gets the selected supplier list.
	 * 
	 * @return the selected supplier list
	 */
	public List<SmSupplier> getSelectedSupplierList() {
		return selectedSupplierList;
	}

	/**
	 * Sets the selected supplier list.
	 * 
	 * @param selectedSupplierList
	 *            the new selected supplier list
	 */
	public void setSelectedSupplierList(List<SmSupplier> selectedSupplierList) {
		this.selectedSupplierList = selectedSupplierList;
	}

	/**
	 * Gets the qt supplier list.
	 * 
	 * @return the qt supplier list
	 */
	public List<ScQtSupplier> getQtSupplierList() {
		return qtSupplierList;
	}

	/**
	 * Sets the qt supplier list.
	 * 
	 * @param qtSupplierList
	 *            the new qt supplier list
	 */
	public void setQtSupplierList(List<ScQtSupplier> qtSupplierList) {
		this.qtSupplierList = qtSupplierList;
	}

	/**
	 * Gets the selected category list.
	 * 
	 * @return the selected category list
	 */
	public List<ScQtCategory> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	/**
	 * Sets the selected category list.
	 * 
	 * @param selectedCategoryList
	 *            the new selected category list
	 */
	public void setSelectedCategoryList(List<ScQtCategory> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	/**
	 * Gets the qt category list.
	 * 
	 * @return the qt category list
	 */
	public List<ScQtCategory> getQtCategoryList() {
		return qtCategoryList;
	}

	/**
	 * Sets the qt category list.
	 * 
	 * @param qtCategoryList
	 *            the new qt category list
	 */
	public void setQtCategoryList(List<ScQtCategory> qtCategoryList) {
		this.qtCategoryList = qtCategoryList;
	}

	/**
	 * Gets the selected sc qt deduction.
	 * 
	 * @return the selected sc qt deduction
	 */
	public ScQtDeduction getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	/**
	 * Sets the selected sc qt deduction.
	 * 
	 * @param selectedScQtDeduction
	 *            the new selected sc qt deduction
	 */
	public void setSelectedScQtDeduction(ScQtDeduction selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	/**
	 * Gets the search qt data model.
	 * 
	 * @return the search qt data model
	 */
	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	/**
	 * Sets the search qt data model.
	 * 
	 * @param searchQTDataModel
	 *            the new search qt data model
	 */
	public void setSearchQTDataModel(IPagingDataModel<ScQt> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
	}

	/**
	 * Gets the clone search qt data model.
	 * 
	 * @return the clone search qt data model
	 */
	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}

	/**
	 * Sets the clone search qt data model.
	 * 
	 * @param cloneSearchQTDataModel
	 *            the new clone search qt data model
	 */
	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}

	/**
	 * Gets the selected loc nodes.
	 * 
	 * @return the selected loc nodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * Sets the selected loc nodes.
	 * 
	 * @param selectedLocNodes
	 *            the new selected loc nodes
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * Gets the desk officer admin.
	 * 
	 * @return the desk officer admin
	 */
	public List<PmUser> getDeskOfficerAdmin() {
		return deskOfficerAdmin;
	}

	/**
	 * Sets the desk officer admin.
	 * 
	 * @param deskOfficerAdmin
	 *            the new desk officer admin
	 */
	public void setDeskOfficerAdmin(List<PmUser> deskOfficerAdmin) {
		this.deskOfficerAdmin = deskOfficerAdmin;
	}

	/**
	 * Checks if is duration field.
	 * 
	 * @return true, if is duration field
	 */
	public boolean isDurationField() {
		return durationField;
	}

	/**
	 * Sets the duration field.
	 * 
	 * @param durationField
	 *            the new duration field
	 */
	public void setDurationField(boolean durationField) {
		this.durationField = durationField;
	}

	/**
	 * Gets the committemembers.
	 * 
	 * @return the committemembers
	 */
	public List<ScCommitteeMember> getCommittemembers() {
		return committemembers;
	}

	/**
	 * Sets the committemembers.
	 * 
	 * @param committemembers
	 *            the new committemembers
	 */
	public void setCommittemembers(List<ScCommitteeMember> committemembers) {
		this.committemembers = committemembers;
	}

	/**
	 * Gets the searchcommittee node.
	 * 
	 * @return the searchcommittee node
	 */
	public TreeNode getSearchcommitteeNode() {
		return searchcommitteeNode;
	}

	/**
	 * Sets the searchcommittee node.
	 * 
	 * @param searchcommitteeNode
	 *            the new searchcommittee node
	 */
	public void setSearchcommitteeNode(TreeNode searchcommitteeNode) {
		this.searchcommitteeNode = searchcommitteeNode;
	}

	/**
	 * Gets the select searchcommittee node.
	 * 
	 * @return the select searchcommittee node
	 */
	public TreeNode[] getSelectSearchcommitteeNode() {
		return selectSearchcommitteeNode;
	}

	/**
	 * Sets the select searchcommittee node.
	 * 
	 * @param selectSearchcommitteeNode
	 *            the new select searchcommittee node
	 */
	public void setSelectSearchcommitteeNode(
			TreeNode[] selectSearchcommitteeNode) {
		this.selectSearchcommitteeNode = selectSearchcommitteeNode;
	}

	/**
	 * Gets the committe no.
	 * 
	 * @return the committe no
	 */
	public String getCommitteNo() {
		return committeNo;
	}

	/**
	 * Sets the committe no.
	 * 
	 * @param committeNo
	 *            the new committe no
	 */
	public void setCommitteNo(String committeNo) {
		this.committeNo = committeNo;
	}

	/**
	 * Gets the created dt.
	 * 
	 * @return the created dt
	 */
	public Date getCreatedDt() {
		return createdDt;
	}

	/**
	 * Sets the created dt.
	 * 
	 * @param createdDt
	 *            the new created dt
	 */
	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	/**
	 * Gets the selected committe.
	 * 
	 * @return the selected committe
	 */
	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	/**
	 * Sets the selected committe.
	 * 
	 * @param selectedCommitte
	 *            the new selected committe
	 */
	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}

	/**
	 * Gets the sc committee member tab list.
	 * 
	 * @return the sc committee member tab list
	 */
	public List<TabBean> getScCommitteeMemberTabList() {
		return scCommitteeMemberTabList;
	}

	/**
	 * Sets the sc committee member tab list.
	 * 
	 * @param scCommitteeMemberTabList
	 *            the new sc committee member tab list
	 */
	public void setScCommitteeMemberTabList(
			List<TabBean> scCommitteeMemberTabList) {
		this.scCommitteeMemberTabList = scCommitteeMemberTabList;
	}

	/**
	 * Gets the sc committee.
	 * 
	 * @return the sc committee
	 */
	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	/**
	 * Sets the sc committee.
	 * 
	 * @param scCommittee
	 *            the new sc committee
	 */
	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}

	/**
	 * Gets the tab title.
	 * 
	 * @return the tab title
	 */
	public String getTabTitle() {
		return tabTitle;
	}

	/**
	 * Sets the tab title.
	 * 
	 * @param tabTitle
	 *            the new tab title
	 */
	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	/**
	 * Gets the clone tender list.
	 * 
	 * @return the clone tender list
	 */
	public List<ScQt> getCloneTenderList() {
		return cloneTenderList;
	}

	/**
	 * Sets the clone tender list.
	 * 
	 * @param cloneTenderList
	 *            the new clone tender list
	 */
	public void setCloneTenderList(List<ScQt> cloneTenderList) {
		this.cloneTenderList = cloneTenderList;
	}

	/**
	 * Gets the selected clone tender.
	 * 
	 * @return the selected clone tender
	 */
	public ScQt getSelectedCloneTender() {
		return selectedCloneTender;
	}

	/**
	 * Sets the selected clone tender.
	 * 
	 * @param selectedCloneTender
	 *            the new selected clone tender
	 */
	public void setSelectedCloneTender(ScQt selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}

	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Gets the zones list.
	 * 
	 * @return the zones list
	 */
	public List<TreeNode> getZonesList() {
		return zonesList;
	}

	/**
	 * Sets the zones list.
	 * 
	 * @param zonesList
	 *            the new zones list
	 */
	public void setZonesList(List<TreeNode> zonesList) {
		this.zonesList = zonesList;
	}

	/**
	 * Gets the ct zone suppliers.
	 * 
	 * @return the ct zone suppliers
	 */
	public List<TreeNode> getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	/**
	 * Sets the ct zone suppliers.
	 * 
	 * @param ctZoneSuppliers
	 *            the new ct zone suppliers
	 */
	public void setCtZoneSuppliers(List<TreeNode> ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	/**
	 * Gets the ct zone supplier list.
	 * 
	 * @return the ct zone supplier list
	 */
	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	/**
	 * Sets the ct zone supplier list.
	 * 
	 * @param ctZoneSupplierList
	 *            the new ct zone supplier list
	 */
	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

	/**
	 * Gets the clone business tender list.
	 * 
	 * @return the clone business tender list
	 */
	public List<ScQt> getCloneBusinessTenderList() {
		return cloneBusinessTenderList;
	}

	/**
	 * Sets the clone business tender list.
	 * 
	 * @param cloneBusinessTenderList
	 *            the new clone business tender list
	 */
	public void setCloneBusinessTenderList(List<ScQt> cloneBusinessTenderList) {
		this.cloneBusinessTenderList = cloneBusinessTenderList;
	}

	/**
	 * Gets the selected clone business tender.
	 * 
	 * @return the selected clone business tender
	 */
	public ScQt getSelectedCloneBusinessTender() {
		return selectedCloneBusinessTender;
	}

	/**
	 * Sets the selected clone business tender.
	 * 
	 * @param selectedCloneBusinessTender
	 *            the new selected clone business tender
	 */
	public void setSelectedCloneBusinessTender(ScQt selectedCloneBusinessTender) {
		this.selectedCloneBusinessTender = selectedCloneBusinessTender;
	}

	/**
	 * Gets the sc qt supplier.
	 * 
	 * @return the sc qt supplier
	 */
	public ScQtSupplier getScQtSupplier() {
		return scQtSupplier;
	}

	/**
	 * Sets the sc qt supplier.
	 * 
	 * @param scQtSupplier
	 *            the new sc qt supplier
	 */
	public void setScQtSupplier(ScQtSupplier scQtSupplier) {
		this.scQtSupplier = scQtSupplier;
	}

	/**
	 * Gets the briefing zone name.
	 * 
	 * @return the briefing zone name
	 */
	public String getBriefingZoneName() {
		return briefingZoneName;
	}

	/**
	 * Sets the briefing zone name.
	 * 
	 * @param briefingZoneName
	 *            the new briefing zone name
	 */
	public void setBriefingZoneName(String briefingZoneName) {
		this.briefingZoneName = briefingZoneName;
	}

	/**
	 * Gets the proposal validity until.
	 * 
	 * @return the proposal validity until
	 */
	public Date getProposalValidityUntil() {
		return proposalValidityUntil;
	}

	/**
	 * Sets the proposal validity until.
	 * 
	 * @param proposalValidityUntil
	 *            the new proposal validity until
	 */
	public void setProposalValidityUntil(Date proposalValidityUntil) {
		this.proposalValidityUntil = proposalValidityUntil;
	}

	/**
	 * Gets the closing date.
	 * 
	 * @return the closing date
	 */
	public Date getClosingDate() {
		return closingDate;
	}

	/**
	 * Sets the closing date.
	 * 
	 * @param closingDate
	 *            the new closing date
	 */
	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	/**
	 * Gets the document clone tender list.
	 * 
	 * @return the document clone tender list
	 */
	public List<ScQt> getDocumentCloneTenderList() {
		return documentCloneTenderList;
	}

	/**
	 * Sets the document clone tender list.
	 * 
	 * @param documentCloneTenderList
	 *            the new document clone tender list
	 */
	public void setDocumentCloneTenderList(List<ScQt> documentCloneTenderList) {
		this.documentCloneTenderList = documentCloneTenderList;
	}

	/**
	 * Gets the selected document clone tender.
	 * 
	 * @return the selected document clone tender
	 */
	public ScQt getSelectedDocumentCloneTender() {
		return selectedDocumentCloneTender;
	}

	/**
	 * Sets the selected document clone tender.
	 * 
	 * @param selectedDocumentCloneTender
	 *            the new selected document clone tender
	 */
	public void setSelectedDocumentCloneTender(ScQt selectedDocumentCloneTender) {
		this.selectedDocumentCloneTender = selectedDocumentCloneTender;
	}

	/**
	 * Gets the qt check lists.
	 * 
	 * @return the qt check lists
	 */
	public List<ScQtCheckList> getQtCheckLists() {
		return qtCheckLists;
	}

	/**
	 * Sets the qt check lists.
	 * 
	 * @param qtCheckLists
	 *            the new qt check lists
	 */
	public void setQtCheckLists(List<ScQtCheckList> qtCheckLists) {
		this.qtCheckLists = qtCheckLists;
	}

	/**
	 * Gets the selected qt check lists.
	 * 
	 * @return the selected qt check lists
	 */
	public List<ScQtCheckList> getSelectedQtCheckLists() {
		return selectedQtCheckLists;
	}

	/**
	 * Sets the selected qt check lists.
	 * 
	 * @param selectedQtCheckLists
	 *            the new selected qt check lists
	 */
	public void setSelectedQtCheckLists(List<ScQtCheckList> selectedQtCheckLists) {
		this.selectedQtCheckLists = selectedQtCheckLists;
	}

	/**
	 * Gets the previous page.
	 * 
	 * @return the previous page
	 */
	public String getPreviousPage() {
		return previousPage;
	}

	/**
	 * Sets the previous page.
	 * 
	 * @param previousPage
	 *            the new previous page
	 */
	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	/**
	 * Gets the pm users list.
	 * 
	 * @return the pm users list
	 */
	public List<PmUser> getPmUsersList() {
		return pmUsersList;
	}

	/**
	 * Sets the pm users list.
	 * 
	 * @param pmUsersList
	 *            the new pm users list
	 */
	public void setPmUsersList(List<PmUser> pmUsersList) {
		this.pmUsersList = pmUsersList;
	}

	/**
	 * Gets the selected pm users list.
	 * 
	 * @return the selected pm users list
	 */
	public List<PmUser> getSelectedPmUsersList() {
		return selectedPmUsersList;
	}

	/**
	 * Sets the selected pm users list.
	 * 
	 * @param selectedPmUsersList
	 *            the new selected pm users list
	 */
	public void setSelectedPmUsersList(List<PmUser> selectedPmUsersList) {
		this.selectedPmUsersList = selectedPmUsersList;
	}

	/**
	 * Gets the pm user.
	 * 
	 * @return the pm user
	 */
	public PmUser getPmUser() {
		return pmUser;
	}

	/**
	 * Sets the pm user.
	 * 
	 * @param pmUser
	 *            the new pm user
	 */
	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	/**
	 * Gets the tpl spec zone dtl.
	 * 
	 * @return the tpl spec zone dtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * Sets the tpl spec zone dtl.
	 * 
	 * @param tplSpecZoneDtl
	 *            the new tpl spec zone dtl
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * Gets the zones.
	 * 
	 * @return the zones
	 */
	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	/**
	 * Sets the zones.
	 * 
	 * @param zones
	 *            the new zones
	 */
	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	/**
	 * Gets the remarks.
	 * 
	 * @return the remarks
	 */
	public List<ScRemark> getRemarks() {
		return remarks;
	}

	/**
	 * Sets the remarks.
	 * 
	 * @param remarks
	 *            the new remarks
	 */
	public void setRemarks(List<ScRemark> remarks) {
		this.remarks = remarks;
	}

	/**
	 * Gets the selected zone.
	 * 
	 * @return the selected zone
	 */
	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	/**
	 * Sets the selected zone.
	 * 
	 * @param selectedZone
	 *            the new selected zone
	 */
	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	/**
	 * Gets the ct contract.
	 * 
	 * @return the ct contract
	 */
	public CtContract getCtContract() {
		return ctContract;
	}

	/**
	 * Sets the ct contract.
	 * 
	 * @param ctContract
	 *            the new ct contract
	 */
	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	/**
	 * Gets the sc qt sample submission.
	 * 
	 * @return the sc qt sample submission
	 */
	public ScQtSampleSubmission getScQtSampleSubmission() {
		return scQtSampleSubmission;
	}

	/**
	 * Sets the sc qt sample submission.
	 * 
	 * @param scQtSampleSubmission
	 *            the new sc qt sample submission
	 */
	public void setScQtSampleSubmission(
			ScQtSampleSubmission scQtSampleSubmission) {
		this.scQtSampleSubmission = scQtSampleSubmission;
	}

	/**
	 * Gets the briefing registration list.
	 * 
	 * @return the briefing registration list
	 */
	public List<ScQtBsvRegistration> getBriefingRegistrationList() {
		return briefingRegistrationList;
	}

	/**
	 * Sets the briefing registration list.
	 * 
	 * @param briefingRegistrationList
	 *            the new briefing registration list
	 */
	public void setBriefingRegistrationList(
			List<ScQtBsvRegistration> briefingRegistrationList) {
		this.briefingRegistrationList = briefingRegistrationList;
	}

	/**
	 * Gets the selectedbrief registration.
	 * 
	 * @return the selectedbrief registration
	 */
	public ScQtBsvRegistration getSelectedbriefRegistration() {
		return selectedbriefRegistration;
	}

	/**
	 * Sets the selectedbrief registration.
	 * 
	 * @param selectedbriefRegistration
	 *            the new selectedbrief registration
	 */
	public void setSelectedbriefRegistration(
			ScQtBsvRegistration selectedbriefRegistration) {
		this.selectedbriefRegistration = selectedbriefRegistration;
	}

	/**
	 * Gets the sc qt bsv registration.
	 * 
	 * @return the sc qt bsv registration
	 */
	public ScQtBsvRegistration getScQtBsvRegistration() {
		return scQtBsvRegistration;
	}

	/**
	 * Sets the sc qt bsv registration.
	 * 
	 * @param scQtBsvRegistration
	 *            the new sc qt bsv registration
	 */
	public void setScQtBsvRegistration(ScQtBsvRegistration scQtBsvRegistration) {
		this.scQtBsvRegistration = scQtBsvRegistration;
	}

	/**
	 * Gets the sc qt sample submission list.
	 * 
	 * @return the sc qt sample submission list
	 */
	public List<ScQtSampleSubmission> getScQtSampleSubmissionList() {
		return scQtSampleSubmissionList;
	}

	/**
	 * Sets the sc qt sample submission list.
	 * 
	 * @param scQtSampleSubmissionList
	 *            the new sc qt sample submission list
	 */
	public void setScQtSampleSubmissionList(
			List<ScQtSampleSubmission> scQtSampleSubmissionList) {
		this.scQtSampleSubmissionList = scQtSampleSubmissionList;
	}

	/**
	 * Gets the selected submission list.
	 * 
	 * @return the selected submission list
	 */
	public List<ScQtSampleSubmission> getSelectedSubmissionList() {
		return selectedSubmissionList;
	}

	/**
	 * Sets the selected submission list.
	 * 
	 * @param selectedSubmissionList
	 *            the new selected submission list
	 */
	public void setSelectedSubmissionList(
			List<ScQtSampleSubmission> selectedSubmissionList) {
		this.selectedSubmissionList = selectedSubmissionList;
	}

	/**
	 * Gets the pmuser contactperson list.
	 * 
	 * @return the pmuser contactperson list
	 */
	public List<ScQtContactPerson> getPmuserContactpersonList() {
		return pmuserContactpersonList;
	}

	/**
	 * Sets the pmuser contactperson list.
	 * 
	 * @param pmuserContactpersonList
	 *            the new pmuser contactperson list
	 */
	public void setPmuserContactpersonList(
			List<ScQtContactPerson> pmuserContactpersonList) {
		this.pmuserContactpersonList = pmuserContactpersonList;
	}

	/**
	 * Gets the selectedpmuser contactperson list.
	 * 
	 * @return the selectedpmuser contactperson list
	 */
	public List<ScQtContactPerson> getSelectedpmuserContactpersonList() {
		return selectedpmuserContactpersonList;
	}

	/**
	 * Sets the selectedpmuser contactperson list.
	 * 
	 * @param selectedpmuserContactpersonList
	 *            the new selectedpmuser contactperson list
	 */
	public void setSelectedpmuserContactpersonList(
			List<ScQtContactPerson> selectedpmuserContactpersonList) {
		this.selectedpmuserContactpersonList = selectedpmuserContactpersonList;
	}

	/**
	 * Gets the citys.
	 * 
	 * @return the citys
	 */
	public List<PmCity> getCitys() {
		return citys;
	}

	/**
	 * Sets the citys.
	 * 
	 * @param citys
	 *            the new citys
	 */
	public void setCitys(List<PmCity> citys) {
		this.citys = citys;
	}

	
	/**
	 * Gets the sc qt checklist.
	 * 
	 * @return the sc qt checklist
	 */
	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	/**
	 * Sets the sc qt checklist.
	 * 
	 * @param scQtChecklist
	 *            the new sc qt checklist
	 */
	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
	}

	/**
	 * Gets the sc qt approval request list.
	 * 
	 * @return the sc qt approval request list
	 */
	public List<ScQtApprovalRequest> getScQtApprovalRequestList() {
		return scQtApprovalRequestList;
	}

	/**
	 * Sets the sc qt approval request list.
	 * 
	 * @param scQtApprovalRequestList
	 *            the new sc qt approval request list
	 */
	public void setScQtApprovalRequestList(
			List<ScQtApprovalRequest> scQtApprovalRequestList) {
		this.scQtApprovalRequestList = scQtApprovalRequestList;
	}


	/**
	 * Gets the approval type.
	 * 
	 * @return the approval type
	 */
	public String getApprovalType() {
		return approvalType;
	}

	/**
	 * Sets the approval type.
	 * 
	 * @param approvalType
	 *            the new approval type
	 */
	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}

	/**
	 * Gets the qt committes.
	 * 
	 * @return the qt committes
	 */
	public ISearchResultBean<ScQtCommittee> getQtCommittes() {
		return qtCommittes;
	}

	/**
	 * Sets the qt committes.
	 * 
	 * @param qtCommittes
	 *            the new qt committes
	 */
	public void setQtCommittes(ISearchResultBean<ScQtCommittee> qtCommittes) {
		this.qtCommittes = qtCommittes;
	}

	/**
	 * Gets the technical spec list.
	 * 
	 * @return the technical spec list
	 */
	public List<ScQtCheckList> getTechnicalSpecList() {
		return technicalSpecList;
	}

	/**
	 * Sets the technical spec list.
	 * 
	 * @param technicalSpecList
	 *            the new technical spec list
	 */
	public void setTechnicalSpecList(List<ScQtCheckList> technicalSpecList) {
		this.technicalSpecList = technicalSpecList;
	}

	/**
	 * Gets the financial spec list.
	 * 
	 * @return the financial spec list
	 */
	public List<ScQtCheckList> getFinancialSpecList() {
		return financialSpecList;
	}

	/**
	 * Sets the financial spec list.
	 * 
	 * @param financialSpecList
	 *            the new financial spec list
	 */
	public void setFinancialSpecList(List<ScQtCheckList> financialSpecList) {
		this.financialSpecList = financialSpecList;
	}

	/**
	 * Gets the sc qt bsv attendance list.
	 * 
	 * @return the sc qt bsv attendance list
	 */
	public List<ScQtBsvAttendance> getScQtBsvAttendanceList() {
		return scQtBsvAttendanceList;
	}

	/**
	 * Sets the sc qt bsv attendance list.
	 * 
	 * @param scQtBsvAttendanceList
	 *            the new sc qt bsv attendance list
	 */
	public void setScQtBsvAttendanceList(
			List<ScQtBsvAttendance> scQtBsvAttendanceList) {
		this.scQtBsvAttendanceList = scQtBsvAttendanceList;
	}

	/**
	 * Gets the supplier branch.
	 * 
	 * @return the supplier branch
	 */
	public String getSupplierBranch() {
		return supplierBranch;
	}

	/**
	 * Sets the supplier branch.
	 * 
	 * @param supplierBranch
	 *            the new supplier branch
	 */
	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	/**
	 * Gets the supplier contact.
	 * 
	 * @return the supplier contact
	 */
	public String getSupplierContact() {
		return supplierContact;
	}

	/**
	 * Sets the supplier contact.
	 * 
	 * @param supplierContact
	 *            the new supplier contact
	 */
	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	/**
	 * Gets the supplier email.
	 * 
	 * @return the supplier email
	 */
	public String getSupplierEmail() {
		return supplierEmail;
	}

	/**
	 * Sets the supplier email.
	 * 
	 * @param supplierEmail
	 *            the new supplier email
	 */
	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	/**
	 * Gets the supplier address.
	 * 
	 * @return the supplier address
	 */
	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * Sets the supplier address.
	 * 
	 * @param supplierAddress
	 *            the new supplier address
	 */
	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * Gets the sm personnels list.
	 * 
	 * @return the sm personnels list
	 */
	public List<SmPersonnel> getSmPersonnelsList() {
		return smPersonnelsList;
	}

	/**
	 * Sets the sm personnels list.
	 * 
	 * @param smPersonnelsList
	 *            the new sm personnels list
	 */
	public void setSmPersonnelsList(List<SmPersonnel> smPersonnelsList) {
		this.smPersonnelsList = smPersonnelsList;
	}

	/**
	 * Gets the personnel.
	 * 
	 * @return the personnel
	 */
	public SmPersonnel getPersonnel() {
		return personnel;
	}

	/**
	 * Sets the personnel.
	 * 
	 * @param personnel
	 *            the new personnel
	 */
	public void setPersonnel(SmPersonnel personnel) {
		this.personnel = personnel;
	}

	/**
	 * Gets the supplier selected tree node.
	 * 
	 * @return the supplier selected tree node
	 */
	public TreeNode getSupplierSelectedTreeNode() {
		return supplierSelectedTreeNode;
	}

	/**
	 * Sets the supplier selected tree node.
	 * 
	 * @param supplierSelectedTreeNode
	 *            the new supplier selected tree node
	 */
	public void setSupplierSelectedTreeNode(TreeNode supplierSelectedTreeNode) {
		this.supplierSelectedTreeNode = supplierSelectedTreeNode;
	}

	/**
	 * Gets the template spec bean.
	 * 
	 * @return the template spec bean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * Sets the template spec bean.
	 * 
	 * @param templateSpecBean
	 *            the new template spec bean
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * Checks if is required.
	 * 
	 * @return true, if is required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * Sets the required.
	 * 
	 * @param required
	 *            the new required
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}

	/**
	 * Gets the briefing attendance list.
	 * 
	 * @return the briefing attendance list
	 */
	public List<ScQtBsvAttendance> getBriefingAttendanceList() {
		return briefingAttendanceList;
	}

	/**
	 * Sets the briefing attendance list.
	 * 
	 * @param briefingAttendanceList
	 *            the new briefing attendance list
	 */
	public void setBriefingAttendanceList(
			List<ScQtBsvAttendance> briefingAttendanceList) {
		this.briefingAttendanceList = briefingAttendanceList;
	}

	/**
	 * Gets the sc qt briefing attendance.
	 * 
	 * @return the sc qt briefing attendance
	 */
	public ScQtBsvAttendance getScQtBriefingAttendance() {
		return scQtBriefingAttendance;
	}

	/**
	 * Sets the sc qt briefing attendance.
	 * 
	 * @param scQtBriefingAttendance
	 *            the new sc qt briefing attendance
	 */
	public void setScQtBriefingAttendance(
			ScQtBsvAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}

	/**
	 * Gets the finalization secratary.
	 * 
	 * @return the finalization secratary
	 */
	public List<PmParameter> getFinalizationSecratary() {
		return finalizationSecratary;
	}

	/**
	 * Sets the finalization secratary.
	 * 
	 * @param finalizationSecratary
	 *            the new finalization secratary
	 */
	public void setFinalizationSecratary(List<PmParameter> finalizationSecratary) {
		this.finalizationSecratary = finalizationSecratary;
	}

	/**
	 * Gets the user search committee data model.
	 * 
	 * @return the user search committee data model
	 */
	public IPagingDataModel<PmUser> getUserSearchCommitteeDataModel() {
		return userSearchCommitteeDataModel;
	}

	/**
	 * Sets the user search committee data model.
	 * 
	 * @param userSearchCommitteeDataModel
	 *            the new user search committee data model
	 */
	public void setUserSearchCommitteeDataModel(
			IPagingDataModel<PmUser> userSearchCommitteeDataModel) {
		this.userSearchCommitteeDataModel = userSearchCommitteeDataModel;
	}

	/**
	 * Gets the user committee search bean.
	 * 
	 * @return the user committee search bean
	 */
	public UserCommitteeSearchBean getUserCommitteeSearchBean() {
		return userCommitteeSearchBean;
	}

	/**
	 * Sets the user committee search bean.
	 * 
	 * @param userCommitteeSearchBean
	 *            the new user committee search bean
	 */
	public void setUserCommitteeSearchBean(
			UserCommitteeSearchBean userCommitteeSearchBean) {
		this.userCommitteeSearchBean = userCommitteeSearchBean;
	}

	/**
	 * Gets the all state code.
	 * 
	 * @return the all state code
	 */
	public PTJLocalityCode getAllStateCode() {
		return allStateCode;
	}

	/**
	 * Sets the all state code.
	 * 
	 * @param allStateCode
	 *            the new all state code
	 */
	public void setAllStateCode(PTJLocalityCode allStateCode) {
		this.allStateCode = allStateCode;
	}

	/**
	 * Gets the pm document list.
	 * 
	 * @return the pm document list
	 */
	public List<PmDocument> getPmDocumentList() {
		return pmDocumentList;
	}

	/**
	 * Sets the pm document list.
	 * 
	 * @param pmDocumentList
	 *            the new pm document list
	 */
	public void setPmDocumentList(List<PmDocument> pmDocumentList) {
		this.pmDocumentList = pmDocumentList;
	}

	/**
	 * Gets the selected pm document list.
	 * 
	 * @return the selected pm document list
	 */
	public List<PmDocument> getSelectedPmDocumentList() {
		return selectedPmDocumentList;
	}

	/**
	 * Sets the selected pm document list.
	 * 
	 * @param selectedPmDocumentList
	 *            the new selected pm document list
	 */
	public void setSelectedPmDocumentList(
			List<PmDocument> selectedPmDocumentList) {
		this.selectedPmDocumentList = selectedPmDocumentList;
	}

	/**
	 * Gets the clone business owner data model.
	 * 
	 * @return the clone business owner data model
	 */
	public IPagingDataModel<ScQt> getCloneBusinessOwnerDataModel() {
		return cloneBusinessOwnerDataModel;
	}

	/**
	 * Sets the clone business owner data model.
	 * 
	 * @param cloneBusinessOwnerDataModel
	 *            the new clone business owner data model
	 */
	public void setCloneBusinessOwnerDataModel(
			IPagingDataModel<ScQt> cloneBusinessOwnerDataModel) {
		this.cloneBusinessOwnerDataModel = cloneBusinessOwnerDataModel;
	}

	/**
	 * Gets the user search business owner data model.
	 * 
	 * @return the user search business owner data model
	 */
	public IPagingDataModel<PmUser> getUserSearchBusinessOwnerDataModel() {
		return userSearchBusinessOwnerDataModel;
	}

	/**
	 * Sets the user search business owner data model.
	 * 
	 * @param userSearchBusinessOwnerDataModel
	 *            the new user search business owner data model
	 */
	public void setUserSearchBusinessOwnerDataModel(
			IPagingDataModel<PmUser> userSearchBusinessOwnerDataModel) {
		this.userSearchBusinessOwnerDataModel = userSearchBusinessOwnerDataModel;
	}

	/**
	 * Gets the user businee owner search bean.
	 * 
	 * @return the user businee owner search bean
	 */
	public UserCommitteeSearchBean getUserBusineeOwnerSearchBean() {
		return userBusineeOwnerSearchBean;
	}

	/**
	 * Sets the user businee owner search bean.
	 * 
	 * @param userBusineeOwnerSearchBean
	 *            the new user businee owner search bean
	 */
	public void setUserBusineeOwnerSearchBean(
			UserCommitteeSearchBean userBusineeOwnerSearchBean) {
		this.userBusineeOwnerSearchBean = userBusineeOwnerSearchBean;
	}

	/**
	 * Gets the clone business owner search bean.
	 * 
	 * @return the clone business owner search bean
	 */
	public QtnTenderSearchBean getCloneBusinessOwnerSearchBean() {
		return cloneBusinessOwnerSearchBean;
	}

	/**
	 * Sets the clone business owner search bean.
	 * 
	 * @param cloneBusinessOwnerSearchBean
	 *            the new clone business owner search bean
	 */
	public void setCloneBusinessOwnerSearchBean(
			QtnTenderSearchBean cloneBusinessOwnerSearchBean) {
		this.cloneBusinessOwnerSearchBean = cloneBusinessOwnerSearchBean;
	}

	/**
	 * Gets the selected clone bo list.
	 * 
	 * @return the selected clone bo list
	 */
	public List<ScQt> getSelectedCloneBOList() {
		return selectedCloneBOList;
	}

	/**
	 * Sets the selected clone bo list.
	 * 
	 * @param selectedCloneBOList
	 *            the new selected clone bo list
	 */
	public void setSelectedCloneBOList(List<ScQt> selectedCloneBOList) {
		this.selectedCloneBOList = selectedCloneBOList;
	}

	/**
	 * Gets the menu model.
	 * 
	 * @return the menu model
	 */
	public MenuModel getMenuModel() {
		return menuModel;
	}

	/**
	 * Sets the menu model.
	 * 
	 * @param menuModel
	 *            the new menu model
	 */
	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

	/**
	 * Gets the selected tab index.
	 * 
	 * @return the selected tab index
	 */
	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	/**
	 * Sets the selected tab index.
	 * 
	 * @param selectedTabIndex
	 *            the new selected tab index
	 */
	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	/**
	 * Gets the selected tab bean.
	 * 
	 * @return the selected tab bean
	 */
	public TabBean getSelectedTabBean() {
		return selectedTabBean;
	}

	/**
	 * Sets the selected tab bean.
	 * 
	 * @param selectedTabBean
	 *            the new selected tab bean
	 */
	public void setSelectedTabBean(TabBean selectedTabBean) {
		this.selectedTabBean = selectedTabBean;
	}

	/**
	 * Gets the approval menu model.
	 * 
	 * @return the approval menu model
	 */
	public MenuModel getApprovalMenuModel() {
		return approvalMenuModel;
	}

	/**
	 * Sets the approval menu model.
	 * 
	 * @param approvalMenuModel
	 *            the new approval menu model
	 */
	public void setApprovalMenuModel(MenuModel approvalMenuModel) {
		this.approvalMenuModel = approvalMenuModel;
	}

	/**
	 * Gets the selected approval tab index.
	 * 
	 * @return the selected approval tab index
	 */
	public int getSelectedApprovalTabIndex() {
		return selectedApprovalTabIndex;
	}

	/**
	 * Sets the selected approval tab index.
	 * 
	 * @param setSelectedApprovalTabIndex
	 *            the new selected approval tab index
	 */
	public void setSelectedApprovalTabIndex(int setSelectedApprovalTabIndex) {
		this.selectedApprovalTabIndex = setSelectedApprovalTabIndex;
	}

	/**
	 * Gets the selected approval tab bean.
	 * 
	 * @return the selected approval tab bean
	 */
	public TabBean getSelectedApprovalTabBean() {
		return selectedApprovalTabBean;
	}

	/**
	 * Sets the selected approval tab bean.
	 * 
	 * @param selectedApprovalTabBean
	 *            the new selected approval tab bean
	 */
	public void setSelectedApprovalTabBean(TabBean selectedApprovalTabBean) {
		this.selectedApprovalTabBean = selectedApprovalTabBean;
	}

	/**
	 * Gets the sc qt doc report list.
	 * 
	 * @return the sc qt doc report list
	 */
	public List<ScQtDocReport> getScQtDocReportList() {
		return scQtDocReportList;
	}

	/**
	 * Sets the sc qt doc report list.
	 * 
	 * @param scQtDocReportList
	 *            the new sc qt doc report list
	 */
	public void setScQtDocReportList(List<ScQtDocReport> scQtDocReportList) {
		this.scQtDocReportList = scQtDocReportList;
	}

	/**
	 * Gets the selected sc qt doc reports.
	 * 
	 * @return the selected sc qt doc reports
	 */
	public List<ScQtDocReport> getSelectedScQtDocReports() {
		return selectedScQtDocReports;
	}

	/**
	 * Sets the selected sc qt doc reports.
	 * 
	 * @param selectedScQtDocReports
	 *            the new selected sc qt doc reports
	 */
	public void setSelectedScQtDocReports(
			List<ScQtDocReport> selectedScQtDocReports) {
		this.selectedScQtDocReports = selectedScQtDocReports;
	}

	/**
	 * Gets the selected sc qt doc report.
	 * 
	 * @return the selected sc qt doc report
	 */
	public ScQtDocReport getSelectedScQtDocReport() {
		return selectedScQtDocReport;
	}

	/**
	 * Sets the selected sc qt doc report.
	 * 
	 * @param selectedScQtDocReport
	 *            the new selected sc qt doc report
	 */
	public void setSelectedScQtDocReport(ScQtDocReport selectedScQtDocReport) {
		this.selectedScQtDocReport = selectedScQtDocReport;
	}

	/**
	 * Gets the sc qt doc report.
	 * 
	 * @return the sc qt doc report
	 */
	public ScQtDocReport getScQtDocReport() {
		return scQtDocReport;
	}

	/**
	 * Sets the sc qt doc report.
	 * 
	 * @param scQtDocReport
	 *            the new sc qt doc report
	 */
	public void setScQtDocReport(ScQtDocReport scQtDocReport) {
		this.scQtDocReport = scQtDocReport;
	}

	/**
	 * Gets the tpl doc master.
	 * 
	 * @return the tpl doc master
	 */
	public TplDocMaster getTplDocMaster() {
		return tplDocMaster;
	}

	/**
	 * Sets the tpl doc master.
	 * 
	 * @param tplDocMaster
	 *            the new tpl doc master
	 */
	public void setTplDocMaster(TplDocMaster tplDocMaster) {
		this.tplDocMaster = tplDocMaster;
	}

	/**
	 * Gets the clone document data model.
	 * 
	 * @return the clone document data model
	 */
	public IPagingDataModel<ScQt> getCloneDocumentDataModel() {
		return cloneDocumentDataModel;
	}

	/**
	 * Sets the clone document data model.
	 * 
	 * @param cloneDocumentDataModel
	 *            the new clone document data model
	 */
	public void setCloneDocumentDataModel(
			IPagingDataModel<ScQt> cloneDocumentDataModel) {
		this.cloneDocumentDataModel = cloneDocumentDataModel;
	}

	/**
	 * Gets the clone document search bean.
	 * 
	 * @return the clone document search bean
	 */
	public QtnTenderSearchBean getCloneDocumentSearchBean() {
		return cloneDocumentSearchBean;
	}

	/**
	 * Sets the clone document search bean.
	 * 
	 * @param cloneDocumentSearchBean
	 *            the new clone document search bean
	 */
	public void setCloneDocumentSearchBean(
			QtnTenderSearchBean cloneDocumentSearchBean) {
		this.cloneDocumentSearchBean = cloneDocumentSearchBean;
	}

	/**
	 * Gets the clone sc qt doc reports.
	 * 
	 * @return the clone sc qt doc reports
	 */
	public List<ScQtDocReport> getCloneScQtDocReports() {
		return cloneScQtDocReports;
	}

	/**
	 * Sets the clone sc qt doc reports.
	 * 
	 * @param cloneScQtDocReports
	 *            the new clone sc qt doc reports
	 */
	public void setCloneScQtDocReports(List<ScQtDocReport> cloneScQtDocReports) {
		this.cloneScQtDocReports = cloneScQtDocReports;
	}

	/**
	 * Gets the selected clone sc qt doc reports.
	 * 
	 * @return the selected clone sc qt doc reports
	 */
	public List<ScQtDocReport> getSelectedCloneScQtDocReports() {
		return selectedCloneScQtDocReports;
	}

	/**
	 * Sets the selected clone sc qt doc reports.
	 * 
	 * @param selectedCloneScQtDocReports
	 *            the new selected clone sc qt doc reports
	 */
	public void setSelectedCloneScQtDocReports(
			List<ScQtDocReport> selectedCloneScQtDocReports) {
		this.selectedCloneScQtDocReports = selectedCloneScQtDocReports;
	}

	/**
	 * Gets the publication approvals.
	 * 
	 * @return the publication approvals
	 */
	public DualListModel<ScQtApproverSettings> getPublicationApprovals() {
		return publicationApprovals;
	}

	/**
	 * Sets the publication approvals.
	 * 
	 * @param publicationApprovals
	 *            the new publication approvals
	 */
	public void setPublicationApprovals(
			DualListModel<ScQtApproverSettings> publicationApprovals) {
		this.publicationApprovals = publicationApprovals;
	}

	/**
	 * Gets the publication approval.
	 * 
	 * @return the publication approval
	 */
	public ScQtApproval getPublicationApproval() {
		return publicationApproval;
	}

	/**
	 * Sets the publication approval.
	 * 
	 * @param publicationApproval
	 *            the new publication approval
	 */
	public void setPublicationApproval(ScQtApproval publicationApproval) {
		this.publicationApproval = publicationApproval;
	}

	/**
	 * Gets the qt committee remarks.
	 * 
	 * @return the qt committee remarks
	 */
	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}

	/**
	 * Sets the qt committee remarks.
	 * 
	 * @param qtCommitteeRemarks
	 *            the new qt committee remarks
	 */
	public void setQtCommitteeRemarks(
			List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
	}

	/**
	 * Gets the bsv dtl bean.
	 * 
	 * @return the bsv dtl bean
	 */
	public BriefingZoneBean getBsvDtlBean() {
		return bsvDtlBean;
	}

	/**
	 * Sets the bsv dtl bean.
	 * 
	 * @param bsvDtlBean
	 *            the new bsv dtl bean
	 */
	public void setBsvDtlBean(BriefingZoneBean bsvDtlBean) {
		this.bsvDtlBean = bsvDtlBean;
	}

	/**
	 * Gets the bsv type.
	 * 
	 * @return the bsv type
	 */
	public String getBsvType() {
		return bsvType;
	}

	/**
	 * Sets the bsv type.
	 * 
	 * @param bsvType
	 *            the new bsv type
	 */
	public void setBsvType(String bsvType) {
		this.bsvType = bsvType;
	}

	/**
	 * Checks if is show bsv text.
	 * 
	 * @return true, if is show bsv text
	 */
	public boolean isShowBsvText() {
		return showBsvText;
	}

	/**
	 * Sets the show bsv text.
	 * 
	 * @param showBsvText
	 *            the new show bsv text
	 */
	public void setShowBsvText(boolean showBsvText) {
		this.showBsvText = showBsvText;
	}

	/**
	 * Checks if is show sit vistit text.
	 * 
	 * @return true, if is show sit vistit text
	 */
	public boolean isShowSitVistitText() {
		return showSitVistitText;
	}

	/**
	 * Sets the show sit vistit text.
	 * 
	 * @param showSitVistitText
	 *            the new show sit vistit text
	 */
	public void setShowSitVistitText(boolean showSitVistitText) {
		this.showSitVistitText = showSitVistitText;
	}

	/**
	 * Gets the bsv index.
	 * 
	 * @return the bsv index
	 */
	public int getBsvIndex() {
		return bsvIndex;
	}

	/**
	 * Sets the bsv index.
	 * 
	 * @param bsvIndex
	 *            the new bsv index
	 */
	public void setBsvIndex(int bsvIndex) {
		this.bsvIndex = bsvIndex;
	}

	/**
	 * Gets the sample address index.
	 * 
	 * @return the sample address index
	 */
	public int getSampleAddressIndex() {
		return sampleAddressIndex;
	}

	/**
	 * Sets the sample address index.
	 * 
	 * @param sampleAddressIndex
	 *            the new sample address index
	 */
	public void setSampleAddressIndex(int sampleAddressIndex) {
		this.sampleAddressIndex = sampleAddressIndex;
	}

	/**
	 * Gets the action type.
	 * 
	 * @return the action type
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * Sets the action type.
	 * 
	 * @param actionType
	 *            the new action type
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * Gets the bsv zoneindx.
	 * 
	 * @return the bsv zoneindx
	 */
	public String getBsvZoneindx() {
		return bsvZoneindx;
	}

	/**
	 * Sets the bsv zoneindx.
	 * 
	 * @param bsvZoneindx
	 *            the new bsv zoneindx
	 */
	public void setBsvZoneindx(String bsvZoneindx) {
		this.bsvZoneindx = bsvZoneindx;
	}

	/**
	 * Checks if is ctzones.
	 * 
	 * @return true, if is ctzones
	 */
	public boolean isCtzones() {
		return ctzones;
	}

	/**
	 * Sets the ctzones.
	 * 
	 * @param ctzones
	 *            the new ctzones
	 */
	public void setCtzones(boolean ctzones) {
		this.ctzones = ctzones;
	}

	/**
	 * Gets the comm type.
	 * 
	 * @return the comm type
	 */
	public String getCommType() {
		return commType;
	}

	/**
	 * Sets the comm type.
	 * 
	 * @param commType
	 *            the new comm type
	 */
	public void setCommType(String commType) {
		this.commType = commType;
	}

	/**
	 * Gets the local level code.
	 * 
	 * @return the local level code
	 */
	public String getLocalLevelCode() {
		return LocalLevelCode;
	}

	/**
	 * Sets the local level code.
	 * 
	 * @param localLevelCode
	 *            the new local level code
	 */
	public void setLocalLevelCode(String localLevelCode) {
		LocalLevelCode = localLevelCode;
	}

	/**
	 * Gets the removes the deductions.
	 * 
	 * @return the removes the deductions
	 */
	public List<ScQtDeduction> getRemoveDeductions() {
		return removeDeductions;
	}

	/**
	 * Sets the removes the deductions.
	 * 
	 * @param removeDeductions
	 *            the new removes the deductions
	 */
	public void setRemoveDeductions(List<ScQtDeduction> removeDeductions) {
		this.removeDeductions = removeDeductions;
	}

	/**
	 * Gets the removes the doc reports.
	 * 
	 * @return the removes the doc reports
	 */
	public List<ScQtDocReport> getRemoveDocReports() {
		return removeDocReports;
	}

	/**
	 * Sets the removes the doc reports.
	 * 
	 * @param removeDocReports
	 *            the new removes the doc reports
	 */
	public void setRemoveDocReports(List<ScQtDocReport> removeDocReports) {
		this.removeDocReports = removeDocReports;
	}

	/**
	 * Gets the to be del committe memebers.
	 * 
	 * @return the to be del committe memebers
	 */
	public List<ScCommitteeMember> getToBeDelCommitteMemebers() {
		return toBeDelCommitteMemebers;
	}

	/**
	 * Sets the to be del committe memebers.
	 * 
	 * @param toBeDelCommitteMemebers
	 *            the new to be del committe memebers
	 */
	public void setToBeDelCommitteMemebers(
			List<ScCommitteeMember> toBeDelCommitteMemebers) {
		this.toBeDelCommitteMemebers = toBeDelCommitteMemebers;
	}

	/**
	 * Gets the pm users secretary list.
	 * 
	 * @return the pm users secretary list
	 */
	public List<PmUser> getPmUsersSecretaryList() {
		return pmUsersSecretaryList;
	}

	/**
	 * Sets the pm users secretary list.
	 * 
	 * @param pmUsersSecretaryList
	 *            the new pm users secretary list
	 */
	public void setPmUsersSecretaryList(List<PmUser> pmUsersSecretaryList) {
		this.pmUsersSecretaryList = pmUsersSecretaryList;
	}

	/**
	 * Gets the total suppliers spec loc.
	 * 
	 * @return the total suppliers spec loc
	 */
	public List<TplSpecLoc> getTotalSuppliersSpecLoc() {
		return totalSuppliersSpecLoc;
	}

	/**
	 * Sets the total suppliers spec loc.
	 * 
	 * @param totalSuppliersSpecLoc
	 *            the new total suppliers spec loc
	 */
	public void setTotalSuppliersSpecLoc(List<TplSpecLoc> totalSuppliersSpecLoc) {
		this.totalSuppliersSpecLoc = totalSuppliersSpecLoc;
	}

	/**
	 * Gets the state list.
	 * 
	 * @return the state list
	 */
	public List<PmState> getStateList() {
		return stateList;
	}

	/**
	 * Sets the state list.
	 * 
	 * @param stateList
	 *            the new state list
	 */
	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
	}

	/**
	 * Gets the district list.
	 * 
	 * @return the district list
	 */
	public List<PmDistrict> getDistrictList() {
		return districtList;
	}

	/**
	 * Sets the district list.
	 * 
	 * @param districtList
	 *            the new district list
	 */
	public void setDistrictList(List<PmDistrict> districtList) {
		this.districtList = districtList;
	}

	/**
	 * Gets the division list.
	 * 
	 * @return the division list
	 */
	public List<PmDivision> getDivisionList() {
		return divisionList;
	}

	/**
	 * Sets the division list.
	 * 
	 * @param divisionList
	 *            the new division list
	 */
	public void setDivisionList(List<PmDivision> divisionList) {
		this.divisionList = divisionList;
	}

	/**
	 * Gets the total suppliers.
	 * 
	 * @return the total suppliers
	 */
	public Integer getTotalSuppliers() {
		return totalSuppliers;
	}

	/**
	 * Sets the total suppliers.
	 * 
	 * @param totalSuppliers
	 *            the new total suppliers
	 */
	public void setTotalSuppliers(Integer totalSuppliers) {
		this.totalSuppliers = totalSuppliers;
	}

	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action
	 *            the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Checks if is save flow.
	 * 
	 * @return true, if is save flow
	 */
	public boolean isSaveFlow() {
		return isSaveFlow;
	}

	/**
	 * Sets the save flow.
	 * 
	 * @param isSaveFlow
	 *            the new save flow
	 */
	public void setSaveFlow(boolean isSaveFlow) {
		this.isSaveFlow = isSaveFlow;
	}

	/**
	 * Gets the today.
	 * 
	 * @return the today
	 */
	public Date getToday() {
		return today;
	}

	/**
	 * Sets the today.
	 * 
	 * @param today
	 *            the new today
	 */
	public void setToday(Date today) {
		this.today = today;
	}

	/**
	 * Gets the bsv date.
	 * 
	 * @return the bsv date
	 */
	public Date getBsvDate() {
		return bsvDate;
	}

	/**
	 * Sets the bsv date.
	 * 
	 * @param bsvDate
	 *            the new bsv date
	 */
	public void setBsvDate(Date bsvDate) {
		this.bsvDate = bsvDate;
	}

	/**
	 * Checks if is clone needed.
	 * 
	 * @return true, if is clone needed
	 */
	public boolean isCloneNeeded() {
		return cloneNeeded;
	}

	/**
	 * Sets the clone needed.
	 * 
	 * @param cloneNeeded
	 *            the new clone needed
	 */
	public void setCloneNeeded(boolean cloneNeeded) {
		this.cloneNeeded = cloneNeeded;
	}

	/**
	 * Gets the qt activity beans.
	 * 
	 * @return the qtActivityBeans
	 */
	public List<QTActivityBean> getQtActivityBeans() {
		return qtActivityBeans;
	}

	/**
	 * Sets the qt activity beans.
	 * 
	 * @param qtActivityBeans
	 *            the qtActivityBeans to set
	 */
	public void setQtActivityBeans(List<QTActivityBean> qtActivityBeans) {
		this.qtActivityBeans = qtActivityBeans;
	}

	/**
	 * Gets the panel item category.
	 * 
	 * @return the panel item category
	 */
	public String getPanelItemCategory() {
		return panelItemCategory;
	}

	/**
	 * Sets the panel item category.
	 * 
	 * @param panelItemCategory
	 *            the new panel item category
	 */
	public void setPanelItemCategory(String panelItemCategory) {
		this.panelItemCategory = panelItemCategory;
	}

	/**
	 * Gets the ct panel zones.
	 * 
	 * @return the ct panel zones
	 */
	public List<CtPanelZone> getCtPanelZones() {
		return ctPanelZones;
	}

	/**
	 * Sets the ct panel zones.
	 * 
	 * @param ctPanelZones
	 *            the new ct panel zones
	 */
	public void setCtPanelZones(List<CtPanelZone> ctPanelZones) {
		this.ctPanelZones = ctPanelZones;
	}

	/**
	 * Checks if is read only.
	 * 
	 * @return true, if is read only
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * Sets the read only.
	 * 
	 * @param readOnly
	 *            the new read only
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * Gets the ctpanel supplier.
	 * 
	 * @return the ctpanel supplier
	 */
	public CtPanelSupplier getCtpanelSupplier() {
		return ctpanelSupplier;
	}

	/**
	 * Sets the ctpanel supplier.
	 * 
	 * @param ctpanelSupplier
	 *            the new ctpanel supplier
	 */
	public void setCtpanelSupplier(CtPanelSupplier ctpanelSupplier) {
		this.ctpanelSupplier = ctpanelSupplier;
	}

	/**
	 * Gets the supplier state code.
	 * 
	 * @return the supplier state code
	 */
	public List<String> getSupplierStateCode() {
		return supplierStateCode;
	}

	/**
	 * Sets the supplier state code.
	 * 
	 * @param supplierStateCode
	 *            the new supplier state code
	 */
	public void setSupplierStateCode(List<String> supplierStateCode) {
		this.supplierStateCode = supplierStateCode;
	}

	/**
	 * Gets the supplier district code.
	 * 
	 * @return the supplier district code
	 */
	public List<String> getSupplierDistrictCode() {
		return supplierDistrictCode;
	}

	/**
	 * Sets the supplier district code.
	 * 
	 * @param supplierDistrictCode
	 *            the new supplier district code
	 */
	public void setSupplierDistrictCode(List<String> supplierDistrictCode) {
		this.supplierDistrictCode = supplierDistrictCode;
	}

	/**
	 * Gets the supplier division code.
	 * 
	 * @return the supplier division code
	 */
	public List<String> getSupplierDivisionCode() {
		return supplierDivisionCode;
	}

	/**
	 * Sets the supplier division code.
	 * 
	 * @param supplierDivisionCode
	 *            the new supplier division code
	 */
	public void setSupplierDivisionCode(List<String> supplierDivisionCode) {
		this.supplierDivisionCode = supplierDivisionCode;
	}

	/**
	 * Gets the search supplier data model.
	 * 
	 * @return the search supplier data model
	 */
	public IPagingDataModel<SmSupplier> getSearchSupplierDataModel() {
		return searchSupplierDataModel;
	}

	/**
	 * Sets the search supplier data model.
	 * 
	 * @param searchSupplierDataModel
	 *            the new search supplier data model
	 */
	public void setSearchSupplierDataModel(
			IPagingDataModel<SmSupplier> searchSupplierDataModel) {
		this.searchSupplierDataModel = searchSupplierDataModel;
	}

	/**
	 * Gets the supplier search bean.
	 * 
	 * @return the supplier search bean
	 */
	public SupplierSearchBean getSupplierSearchBean() {
		return supplierSearchBean;
	}

	/**
	 * Sets the supplier search bean.
	 * 
	 * @param supplierSearchBean
	 *            the new supplier search bean
	 */
	public void setSupplierSearchBean(SupplierSearchBean supplierSearchBean) {
		this.supplierSearchBean = supplierSearchBean;
	}

	/**
	 * Gets the sm supplier.
	 * 
	 * @return the sm supplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * Sets the sm supplier.
	 * 
	 * @param smSupplier
	 *            the new sm supplier
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * Gets the mark deleted sc qt doc report list.
	 * 
	 * @return the mark deleted sc qt doc report list
	 */
	public List<ScQtDocReport> getMarkDeletedScQtDocReportList() {
		return markDeletedScQtDocReportList;
	}

	/**
	 * Sets the mark deleted sc qt doc report list.
	 * 
	 * @param markDeletedScQtDocReportList
	 *            the new mark deleted sc qt doc report list
	 */
	public void setMarkDeletedScQtDocReportList(
			List<ScQtDocReport> markDeletedScQtDocReportList) {
		this.markDeletedScQtDocReportList = markDeletedScQtDocReportList;
	}

	/**
	 * Gets the mof accounts.
	 * 
	 * @return the mof accounts
	 */
	public List<SmMofAccount> getMofAccounts() {
		return mofAccounts;
	}

	/**
	 * Sets the mof accounts.
	 * 
	 * @param mofAccounts
	 *            the new mof accounts
	 */
	public void setMofAccounts(List<SmMofAccount> mofAccounts) {
		this.mofAccounts = mofAccounts;
	}

	/**
	 * Gets the existing supplier location list.
	 * 
	 * @return the existing supplier location list
	 */
	public List<ScQtLocCriteria> getExistingSupplierLocationList() {
		return existingSupplierLocationList;
	}

	/**
	 * Sets the existing supplier location list.
	 * 
	 * @param existingSupplierLocationList
	 *            the new existing supplier location list
	 */
	public void setExistingSupplierLocationList(
			List<ScQtLocCriteria> existingSupplierLocationList) {
		this.existingSupplierLocationList = existingSupplierLocationList;
	}

	/**
	 * Checks if is supplier validation.
	 * 
	 * @return true, if is supplier validation
	 */
	public boolean isSupplierValidation() {
		return isSupplierValidation;
	}

	/**
	 * Sets the supplier validation.
	 * 
	 * @param isSupplierValidation
	 *            the new supplier validation
	 */
	public void setSupplierValidation(boolean isSupplierValidation) {
		this.isSupplierValidation = isSupplierValidation;
	}

	/**
	 * Gets the state codes.
	 * 
	 * @return the state codes
	 */
	public String getStateCodes() {
		return stateCodes;
	}

	/**
	 * Sets the state codes.
	 * 
	 * @param stateCodes
	 *            the new state codes
	 */
	public void setStateCodes(String stateCodes) {
		this.stateCodes = stateCodes;
	}

	/**
	 * @return the paidupcapital
	 */
	public boolean isPaidupcapital() {
		return paidupcapital;
	}

	/**
	 * @param paidupcapital
	 *            the paidupcapital to set
	 */
	public void setPaidupcapital(boolean paidupcapital) {
		this.paidupcapital = paidupcapital;
	}

	/**
	 * @return the maintanceFlow
	 */
	public boolean isMaintanceFlow() {
		return maintanceFlow;
	}

	/**
	 * @param maintanceFlow
	 *            the maintanceFlow to set
	 */
	public void setMaintanceFlow(boolean maintanceFlow) {
		this.maintanceFlow = maintanceFlow;
	}

	/**
	 * @return the amendMentBean
	 */
	public QtAmendMentBean getAmendMentBean() {
		return amendMentBean;
	}

	/**
	 * @param amendMentBean
	 *            the amendMentBean to set
	 */
	public void setAmendMentBean(QtAmendMentBean amendMentBean) {
		this.amendMentBean = amendMentBean;
	}

	/**
	 * @return the ruleDataBean
	 */
	public QTRuleDataBean getRuleDataBean() {
		return ruleDataBean;
	}

	/**
	 * @param ruleDataBean
	 *            the ruleDataBean to set
	 */
	public void setRuleDataBean(QTRuleDataBean ruleDataBean) {
		this.ruleDataBean = ruleDataBean;
	}

	/**
	 * @return the templateSpecZoneDtls
	 */
	public List<TplSpecZoneDtl> getTemplateSpecZoneDtls() {
		return templateSpecZoneDtls;
	}

	/**
	 * @param templateSpecZoneDtls
	 *            the templateSpecZoneDtls to set
	 */
	public void setTemplateSpecZoneDtls(
			List<TplSpecZoneDtl> templateSpecZoneDtls) {
		this.templateSpecZoneDtls = templateSpecZoneDtls;
	}

	/**
	 * @return the tempScQtBsvDtl
	 */
	public ScQtBsvDtl getTempScQtBsvDtl() {
		return tempScQtBsvDtl;
	}

	/**
	 * @param tempScQtBsvDtl
	 *            the tempScQtBsvDtl to set
	 */
	public void setTempScQtBsvDtl(ScQtBsvDtl tempScQtBsvDtl) {
		this.tempScQtBsvDtl = tempScQtBsvDtl;
	}

	/**
	 * @return the cloneCommitteeSelectQt
	 */
	public ScQt getCloneCommitteeSelectQt() {
		return cloneCommitteeSelectQt;
	}

	/**
	 * @param cloneCommitteeSelectQt
	 *            the cloneCommitteeSelectQt to set
	 */
	public void setCloneCommitteeSelectQt(ScQt cloneCommitteeSelectQt) {
		this.cloneCommitteeSelectQt = cloneCommitteeSelectQt;
	}

	/**
	 * @return the cloneBusinessSelectQt
	 */
	public ScQt getCloneBusinessSelectQt() {
		return cloneBusinessSelectQt;
	}

	/**
	 * @param cloneBusinessSelectQt
	 *            the cloneBusinessSelectQt to set
	 */
	public void setCloneBusinessSelectQt(ScQt cloneBusinessSelectQt) {
		this.cloneBusinessSelectQt = cloneBusinessSelectQt;
	}

	/**
	 * @return the autoUpdate
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	/**
	 * @param autoUpdate
	 *            the autoUpdate to set
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	/**
	 * @return the clausesTitle
	 */
	public String getClausesTitle() {
		return clausesTitle;
	}

	/**
	 * @param clausesTitle
	 *            the clausesTitle to set
	 */
	public void setClausesTitle(String clausesTitle) {
		this.clausesTitle = clausesTitle;
	}

	/**
	 * @return the isSubmit
	 */
	public boolean isSubmit() {
		return isSubmit;
	}

	/**
	 * @param isSubmit
	 *            the isSubmit to set
	 */
	public void setSubmit(boolean isSubmit) {
		this.isSubmit = isSubmit;
	}

	/**
	 * @return the tobeRemoveCategoryCodes
	 */
	public List<ScQtCategory> getTobeRemoveCategoryCodes() {
		return tobeRemoveCategoryCodes;
	}

	/**
	 * @param tobeRemoveCategoryCodes
	 *            the tobeRemoveCategoryCodes to set
	 */
	public void setTobeRemoveCategoryCodes(
			List<ScQtCategory> tobeRemoveCategoryCodes) {
		this.tobeRemoveCategoryCodes = tobeRemoveCategoryCodes;
	}

	public int getSelectedCommitteeTabIndex() {
		return selectedCommitteeTabIndex;
	}

	public void setSelectedCommitteeTabIndex(int selectedCommitteeTabIndex) {
		this.selectedCommitteeTabIndex = selectedCommitteeTabIndex;
	}

	/**
	 * @return the isPcbProcess
	 */
	public boolean isPcbProcess() {
		return isPcbProcess;
	}

	/**
	 * @param isPcbProcess
	 *            the isPcbProcess to set
	 */
	public void setPcbProcess(boolean isPcbProcess) {
		this.isPcbProcess = isPcbProcess;
	}

	/**
	 * @return the pendingRevision
	 */
	public boolean isPendingRevision() {
		return pendingRevision;
	}

	/**
	 * @param pendingRevision
	 *            the pendingRevision to set
	 */
	public void setPendingRevision(boolean pendingRevision) {
		this.pendingRevision = pendingRevision;
	}

	/**
	 * @return the scQtApprovalDtls
	 */
	public ScQtApprovalRequest getScQtApprovalDtls() {
		return scQtApprovalDtls;
	}

	/**
	 * @param scQtApprovalDtls
	 *            the scQtApprovalDtls to set
	 */
	public void setScQtApprovalDtls(ScQtApprovalRequest scQtApprovalDtls) {
		this.scQtApprovalDtls = scQtApprovalDtls;
	}

	/**
	 * @return the cloneDocumentSelectQt
	 */
	public ScQt getCloneDocumentSelectQt() {
		return cloneDocumentSelectQt;
	}

	/**
	 * @param cloneDocumentSelectQt
	 *            the cloneDocumentSelectQt to set
	 */
	public void setCloneDocumentSelectQt(ScQt cloneDocumentSelectQt) {
		this.cloneDocumentSelectQt = cloneDocumentSelectQt;
	}

	/**
	 * @return the deskOfficer
	 */
	public boolean isDeskOfficer() {
		return deskOfficer;
	}

	/**
	 * @param deskOfficer
	 *            the deskOfficer to set
	 */
	public void setDeskOfficer(boolean deskOfficer) {
		this.deskOfficer = deskOfficer;
	}

	/**
	 * @return the auditorRoleView
	 */
	public boolean isAuditorRoleView() {
		return auditorRoleView;
	}

	/**
	 * @param auditorRoleView
	 *            the auditorRoleView to set
	 */
	public void setAuditorRoleView(boolean auditorRoleView) {
		this.auditorRoleView = auditorRoleView;
	}

	/**
	 * @return the ptjProfile
	 */
	public PmOrgProfile getPtjProfile() {
		return ptjProfile;
	}

	/**
	 * @param ptjProfile
	 *            the ptjProfile to set
	 */
	public void setPtjProfile(PmOrgProfile ptjProfile) {
		this.ptjProfile = ptjProfile;
	}

	/**
	 * @return the ministryProfile
	 */
	public PmOrgProfile getMinistryProfile() {
		return ministryProfile;
	}

	/**
	 * @param ministryProfile
	 *            the ministryProfile to set
	 */
	public void setMinistryProfile(PmOrgProfile ministryProfile) {
		this.ministryProfile = ministryProfile;
	}

	/**
	 * @return the scTdObj
	 */
	public Object[] getScTdObj() {
		return scTdObj;
	}

	/**
	 * @param scTdObj
	 *            the scTdObj to set
	 */
	public void setScTdObj(Object[] scTdObj) {
		this.scTdObj = scTdObj;
	}

	/**
	 * @return the viewQtDetails
	 */
	public boolean isViewQtDetails() {
		return viewQtDetails;
	}

	/**
	 * @param viewQtDetails
	 *            the viewQtDetails to set
	 */
	public void setViewQtDetails(boolean viewQtDetails) {
		this.viewQtDetails = viewQtDetails;
	}

	/**
	 * @return the toBeRemoveSchedules
	 */
	public List<ScQtSchedule> getToBeRemoveSchedules() {
		return toBeRemoveSchedules;
	}

	/**
	 * @param toBeRemoveSchedules
	 *            the toBeRemoveSchedules to set
	 */
	public void setToBeRemoveSchedules(List<ScQtSchedule> toBeRemoveSchedules) {
		this.toBeRemoveSchedules = toBeRemoveSchedules;
	}

	/**
	 * @return the toBeRemoveProgPayments
	 */
	public List<ScQtProgressivePayment> getToBeRemoveProgPayments() {
		return toBeRemoveProgPayments;
	}

	/**
	 * @param toBeRemoveProgPayments
	 *            the toBeRemoveProgPayments to set
	 */
	public void setToBeRemoveProgPayments(
			List<ScQtProgressivePayment> toBeRemoveProgPayments) {
		this.toBeRemoveProgPayments = toBeRemoveProgPayments;
	}

	/**
	 * @return the createQt
	 */
	public boolean isCreateQt() {
		return createQt;
	}

	/**
	 * @param createQt
	 *            the createQt to set
	 */
	public void setCreateQt(boolean createQt) {
		this.createQt = createQt;
	}

	/**
	 * @return the statecode
	 */
	public String getStatecode() {
		return statecode;
	}

	/**
	 * @param statecode
	 *            the statecode to set
	 */
	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}

	/**
	 * @return the selectedFlag
	 */
	public boolean isSelectedFlag() {
		return selectedFlag;
	}

	/**
	 * @param selectedFlag
	 *            the selectedFlag to set
	 */
	public void setSelectedFlag(boolean selectedFlag) {
		this.selectedFlag = selectedFlag;
	}

	public ScQtRemark getScQtRemarks() {
		return scQtRemarks;
	}

	public void setScQtRemarks(ScQtRemark scQtRemarks) {
		this.scQtRemarks = scQtRemarks;
	}

	public List<ScQtCheckList> getScQtCheckSt2techProposal() {
		return scQtCheckSt2techProposal;
	}

	public void setScQtCheckSt2techProposal(
			List<ScQtCheckList> scQtCheckSt2techProposal) {
		this.scQtCheckSt2techProposal = scQtCheckSt2techProposal;
	}

	public List<ScQtCheckList> getMarkDeletedTechChecklist() {
		return markDeletedTechChecklist;
	}

	public void setMarkDeletedTechChecklist(
			List<ScQtCheckList> markDeletedTechChecklist) {
		this.markDeletedTechChecklist = markDeletedTechChecklist;
	}

	public List<ScQtCheckList> getScQtCheckSt2financeProposalList() {
		return scQtCheckSt2financeProposalList;
	}

	public void setScQtCheckSt2financeProposalList(
			List<ScQtCheckList> scQtCheckSt2financeProposalList) {
		this.scQtCheckSt2financeProposalList = scQtCheckSt2financeProposalList;
	}

	public List<ScQtCheckList> getMarkDeletedFinChecklist() {
		return markDeletedFinChecklist;
	}

	public void setMarkDeletedFinChecklist(
			List<ScQtCheckList> markDeletedFinChecklist) {
		this.markDeletedFinChecklist = markDeletedFinChecklist;
	}

	public List<ScQtCommitteeActivity> getCommitteeActivities() {
		return committeeActivities;
	}

	public void setCommitteeActivities(
			List<ScQtCommitteeActivity> committeeActivities) {
		this.committeeActivities = committeeActivities;
	}

	public boolean isSampleReq() {
		return isSampleReq;
	}

	public void setSampleReq(boolean isSampleReq) {
		this.isSampleReq = isSampleReq;
	}

	public boolean isSpecStatus() {
		return isSpecStatus;
	}

	public void setSpecStatus(boolean isSpecStatus) {
		this.isSpecStatus = isSpecStatus;
	}

	public Long getProcurementId() {
		return procurementId;
	}

	public void setProcurementId(Long procurementId) {
		this.procurementId = procurementId;
	}

	public String getEvaluationType() {
		return evaluationType;
	}

	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	public List<PmDocument> getStageTwoTechComplianceList() {
		return stageTwoTechComplianceList;
	}

	public void setStageTwoTechComplianceList(
			List<PmDocument> stageTwoTechComplianceList) {
		this.stageTwoTechComplianceList = stageTwoTechComplianceList;
	}

	public List<PmDocument> getStageTwoTechComplianceSelect() {
		return stageTwoTechComplianceSelect;
	}

	public void setStageTwoTechComplianceSelect(
			List<PmDocument> stageTwoTechComplianceSelect) {
		this.stageTwoTechComplianceSelect = stageTwoTechComplianceSelect;
	}

	public List<PmDocument> getStageTwoFinComplianceList() {
		return stageTwoFinComplianceList;
	}

	public void setStageTwoFinComplianceList(
			List<PmDocument> stageTwoFinComplianceList) {
		this.stageTwoFinComplianceList = stageTwoFinComplianceList;
	}

	public List<PmDocument> getStageTwoFinComplianceSelect() {
		return stageTwoFinComplianceSelect;
	}

	public void setStageTwoFinComplianceSelect(
			List<PmDocument> stageTwoFinComplianceSelect) {
		this.stageTwoFinComplianceSelect = stageTwoFinComplianceSelect;
	}

	public List<ScQtCheckList> getS2TechSelect() {
		return s2TechSelect;
	}

	public void setS2TechSelect(List<ScQtCheckList> s2TechSelect) {
		this.s2TechSelect = s2TechSelect;
	}

	public List<ScQtCheckList> getS2FinSelect() {
		return s2FinSelect;
	}

	public void setS2FinSelect(List<ScQtCheckList> s2FinSelect) {
		this.s2FinSelect = s2FinSelect;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getFromMonth() {
		return fromMonth;
	}

	public void setFromMonth(String fromMonth) {
		this.fromMonth = fromMonth;
	}

	public String getToMonth() {
		return toMonth;
	}

	public void setToMonth(String toMonth) {
		this.toMonth = toMonth;
	}

	public String getStartYear() {
		return startYear;
	}

	public void setStartYear(String startYear) {
		this.startYear = startYear;
	}

	public String getEndYear() {
		return endYear;
	}

	public void setEndYear(String endYear) {
		this.endYear = endYear;
	}

	public String getBusinessOwner() {
		return businessOwner;
	}

	public void setBusinessOwner(String businessOwner) {
		this.businessOwner = businessOwner;
	}

	public List<ScQtCheckListScore> getCheckListScore() {
		return checkListScore;
	}

	public void setCheckListScore(List<ScQtCheckListScore> checkListScore) {
		this.checkListScore = checkListScore;
	}

	public List<ScQtCheckListScore> getScoringForYears() {
		return scoringForYears;
	}

	public void setScoringForYears(List<ScQtCheckListScore> scoringForYears) {
		this.scoringForYears = scoringForYears;
	}

	public List<ScQtCheckListScore> getCheckListSelectScore() {
		return checkListSelectScore;
	}

	public void setCheckListSelectScore(
			List<ScQtCheckListScore> checkListSelectScore) {
		this.checkListSelectScore = checkListSelectScore;
	}

	public BigDecimal getTechnicalWeightageScore() {
		return technicalWeightageScore;
	}

	public void setTechnicalWeightageScore(BigDecimal technicalWeightageScore) {
		this.technicalWeightageScore = technicalWeightageScore;
	}

	public BigDecimal getFinancialWeightageScore() {
		return financialWeightageScore;
	}

	public void setFinancialWeightageScore(BigDecimal financialWeightageScore) {
		this.financialWeightageScore = financialWeightageScore;
	}

	public int getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalAmountAvg() {
		return totalAmountAvg;
	}

	public void setTotalAmountAvg(BigDecimal totalAmountAvg) {
		this.totalAmountAvg = totalAmountAvg;
	}

	public List<CtPanelItem> getPanelItemList() {
		return panelItemList;
	}

	public void setPanelItemList(List<CtPanelItem> panelItemList) {
		this.panelItemList = panelItemList;
	}

	public boolean isPanel() {
		return isPanel;
	}

	public void setPanel(boolean isPanel) {
		this.isPanel = isPanel;
	}

	public TreeNode getCtZoneTrees() {
		return ctZoneTrees;
	}

	public void setCtZoneTrees(TreeNode ctZoneTrees) {
		this.ctZoneTrees = ctZoneTrees;
	}

	public TreeNode[] getCtZoneTreeSelectedNodes() {
		return ctZoneTreeSelectedNodes;
	}

	public void setCtZoneTreeSelectedNodes(TreeNode[] ctZoneTreeSelectedNodes) {
		this.ctZoneTreeSelectedNodes = ctZoneTreeSelectedNodes;
	}

	public List<ScQtCategory> getSuppCriteriaopenSelectList() {
		return suppCriteriaopenSelectList;
	}

	public void setSuppCriteriaopenSelectList(
			List<ScQtCategory> suppCriteriaopenSelectList) {
		this.suppCriteriaopenSelectList = suppCriteriaopenSelectList;
	}

	public TreeNode getPanelZoneSelect() {
		return panelZoneSelect;
	}

	public void setPanelZoneSelect(TreeNode panelZoneSelect) {
		this.panelZoneSelect = panelZoneSelect;
	}

	public Date getCircularExpDate() {
		return circularExpDate;
	}

	public void setCircularExpDate(Date circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	public String getCircularType() {
		return circularType;
	}

	public void setCircularType(String circularType) {
		this.circularType = circularType;
	}

	public String getStandardTemplate() {
		return standardTemplate;
	}

	public void setStandardTemplate(String standardTemplate) {
		this.standardTemplate = standardTemplate;
	}

	public String getSupportingDoc() {
		return supportingDoc;
	}

	public void setSupportingDoc(String supportingDoc) {
		this.supportingDoc = supportingDoc;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getSpecificationType() {
		return specificationType;
	}

	public void setSpecificationType(String specificationType) {
		this.specificationType = specificationType;
	}

	public ScQtDoc getScqtDoc() {
		return scqtDoc;
	}

	public void setScqtDoc(ScQtDoc scqtDoc) {
		this.scqtDoc = scqtDoc;
	}

	public String getMinistryName() {
		return ministryName;
	}

	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	public Long getSearchProcurementId() {
		return searchProcurementId;
	}

	public void setSearchProcurementId(Long searchProcurementId) {
		this.searchProcurementId = searchProcurementId;
	}

	public Long getSearchCreatedBy() {
		return searchCreatedBy;
	}

	public void setSearchCreatedBy(Long searchCreatedBy) {
		this.searchCreatedBy = searchCreatedBy;
	}

	public List<ScQtDoc> getDocSearchlist() {
		return docSearchlist;
	}

	public void setDocSearchlist(List<ScQtDoc> docSearchlist) {
		this.docSearchlist = docSearchlist;
	}

	public List<ScQtCheckList> getCheckListItem() {
		return checkListItem;
	}

	public void setCheckListItem(List<ScQtCheckList> checkListItem) {
		this.checkListItem = checkListItem;
	}

	public List<SelectItem> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

	public String getSpecName() {
		return specName;
	}

	public void setSpecName(String specName) {
		this.specName = specName;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public boolean isRecordStatus() {
		return isRecordStatus;
	}

	public void setRecordStatus(boolean isRecordStatus) {
		this.isRecordStatus = isRecordStatus;
	}

	public List<ScQtCheckList> getTechnicalScoring() {
		return technicalScoring;
	}

	public void setTechnicalScoring(List<ScQtCheckList> technicalScoring) {
		this.technicalScoring = technicalScoring;
	}

	public List<ScQtCheckList> getFinancialScoring() {
		return financialScoring;
	}

	public void setFinancialScoring(List<ScQtCheckList> financialScoring) {
		this.financialScoring = financialScoring;
	}

	public int getTechPoints() {
		return techPoints;
	}

	public void setTechPoints(int techPoints) {
		this.techPoints = techPoints;
	}

	public int getFinPoints() {
		return finPoints;
	}

	public void setFinPoints(int finPoints) {
		this.finPoints = finPoints;
	}

	public List<SelectItem> getYearList() {
		return yearList;
	}

	public void setYearList(List<SelectItem> yearList) {
		this.yearList = yearList;
	}

	public List<SelectItem> getYearsDisplayList() {
		return yearsDisplayList;
	}

	public void setYearsDisplayList(List<SelectItem> yearsDisplayList) {
		this.yearsDisplayList = yearsDisplayList;
	}

	public List<BriefingZoneBean> getDeliveryLocBean() {
		return deliveryLocBean;
	}

	public void setDeliveryLocBean(List<BriefingZoneBean> deliveryLocBean) {
		this.deliveryLocBean = deliveryLocBean;
	}

	public List<ScQtDeliveryLoc> getDeliveryLoc() {
		return deliveryLoc;
	}

	public void setDeliveryLoc(List<ScQtDeliveryLoc> deliveryLoc) {
		this.deliveryLoc = deliveryLoc;
	}

	public List<ScQtDeliveryLoc> getDeliveryNonZoneList() {
		return deliveryNonZoneList;
	}

	public void setDeliveryNonZoneList(List<ScQtDeliveryLoc> deliveryNonZoneList) {
		this.deliveryNonZoneList = deliveryNonZoneList;
	}

	public List<ScQtDeliveryLoc> getDeleteDeliveryList() {
		return deleteDeliveryList;
	}

	public void setDeleteDeliveryList(List<ScQtDeliveryLoc> deleteDeliveryList) {
		this.deleteDeliveryList = deleteDeliveryList;
	}

	public List<ScQtDeliveryLoc> getSelectDeliveryList() {
		return selectDeliveryList;
	}

	public void setSelectDeliveryList(List<ScQtDeliveryLoc> selectDeliveryList) {
		this.selectDeliveryList = selectDeliveryList;
	}

	public String getDeliveyzoneName() {
		return deliveyzoneName;
	}

	public void setDeliveyzoneName(String deliveyzoneName) {
		this.deliveyzoneName = deliveyzoneName;
	}

	public int getTechnicalChecklistScoring() {
		return technicalChecklistScoring;
	}

	public void setTechnicalChecklistScoring(int technicalChecklistScoring) {
		this.technicalChecklistScoring = technicalChecklistScoring;
	}

	public String getChecklistScoreType() {
		return checklistScoreType;
	}

	public void setChecklistScoreType(String checklistScoreType) {
		this.checklistScoreType = checklistScoreType;
	}

	public String getCompanyScoreType() {
		return companyScoreType;
	}

	public void setCompanyScoreType(String companyScoreType) {
		this.companyScoreType = companyScoreType;
	}

	public ScQtCheckListScore getScQtCheckListScore() {
		return scQtCheckListScore;
	}

	public void setScQtCheckListScore(ScQtCheckListScore scQtCheckListScore) {
		this.scQtCheckListScore = scQtCheckListScore;
	}

	public ScQtCheckListScore getScQtContractScore() {
		return scQtContractScore;
	}

	public void setScQtContractScore(ScQtCheckListScore scQtContractScore) {
		this.scQtContractScore = scQtContractScore;
	}

	public List<SelectItem> getTechnicalChecklistAction() {
		return technicalChecklistAction;
	}

	public void setTechnicalChecklistAction(
			List<SelectItem> technicalChecklistAction) {
		this.technicalChecklistAction = technicalChecklistAction;
	}

	public BigDecimal getScoreVarience() {
		return scoreVarience;
	}

	public void setScoreVarience(BigDecimal scoreVarience) {
		this.scoreVarience = scoreVarience;
	}

	public ScQtScoreOfferPrice getOfferPricePackage() {
		return offerPricePackage;
	}

	public void setOfferPricePackage(ScQtScoreOfferPrice offerPricePackage) {
		this.offerPricePackage = offerPricePackage;
	}

	public ScQtScoreOfferPrice getOfferPriceItem() {
		return OfferPriceItem;
	}

	public void setOfferPriceItem(ScQtScoreOfferPrice offerPriceItem) {
		OfferPriceItem = offerPriceItem;
	}

	public ScQtScoreWeightage getScQtScoreWeightage() {
		return scQtScoreWeightage;
	}

	public void setScQtScoreWeightage(ScQtScoreWeightage scQtScoreWeightage) {
		this.scQtScoreWeightage = scQtScoreWeightage;
	}

	public ScQtScoreSetting getTechnicalScoreSetting() {
		return technicalScoreSetting;
	}

	public void setTechnicalScoreSetting(ScQtScoreSetting technicalScoreSetting) {
		this.technicalScoreSetting = technicalScoreSetting;
	}

	public ScQtScoreSetting getFinancialScoreSetting() {
		return financialScoreSetting;
	}

	public void setFinancialScoreSetting(ScQtScoreSetting financialScoreSetting) {
		this.financialScoreSetting = financialScoreSetting;
	}

	public ScQtScoreSetting getDemoScoreSetting() {
		return demoScoreSetting;
	}

	public void setDemoScoreSetting(ScQtScoreSetting demoScoreSetting) {
		this.demoScoreSetting = demoScoreSetting;
	}

	public ScQtScoreSetting getRecommendationPackageScore() {
		return recommendationPackageScore;
	}

	public void setRecommendationPackageScore(
			ScQtScoreSetting recommendationPackageScore) {
		this.recommendationPackageScore = recommendationPackageScore;
	}

	public ScQtScoreSetting getRecommendationScore() {
		return recommendationScore;
	}

	public void setRecommendationScore(ScQtScoreSetting recommendationScore) {
		this.recommendationScore = recommendationScore;
	}

	public BigDecimal getTechnicalpackage() {
		return technicalpackage;
	}

	public void setTechnicalpackage(BigDecimal technicalpackage) {
		this.technicalpackage = technicalpackage;
	}

	public BigDecimal getFinancialpackage() {
		return financialpackage;
	}

	public void setFinancialpackage(BigDecimal financialpackage) {
		this.financialpackage = financialpackage;
	}

	public BigDecimal getRecommendationPackageTotal() {
		return recommendationPackageTotal;
	}

	public void setRecommendationPackageTotal(BigDecimal recommendationPackageTotal) {
		this.recommendationPackageTotal = recommendationPackageTotal;
	}

	public BigDecimal getRecommendTechnicalScore() {
		return recommendTechnicalScore;
	}

	public void setRecommendTechnicalScore(BigDecimal recommendTechnicalScore) {
		this.recommendTechnicalScore = recommendTechnicalScore;
	}

	public BigDecimal getRecommendfinancialScore() {
		return recommendfinancialScore;
	}

	public void setRecommendfinancialScore(BigDecimal recommendfinancialScore) {
		this.recommendfinancialScore = recommendfinancialScore;
	}

	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public Long getStateId() {
		return stateId;
	}

	public void setStateId(Long stateId) {
		this.stateId = stateId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public Long getMinistryId() {
		return ministryId;
	}

	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}

	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	public Long getPtjId() {
		return ptjId;
	}

	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getDistrictCode() {
		return districtCode;
	}

	public void setDistrictCode(String districtCode) {
		this.districtCode = districtCode;
	}

	public List<PmOrgProfile> getMinistryList() {
		return ministryList;
	}

	public void setMinistryList(List<PmOrgProfile> ministryList) {
		this.ministryList = ministryList;
	}

	public List<PmOrgProfile> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<PmOrgProfile> departmentList) {
		this.departmentList = departmentList;
	}

	public List<Object[]> getPtjtList() {
		return ptjtList;
	}

	public void setPtjtList(List<Object[]> ptjtList) {
		this.ptjtList = ptjtList;
	}

	public List<Object[]> getPtjProfileDDList() {
		return ptjProfileDDList;
	}

	public void setPtjProfileDDList(List<Object[]> ptjProfileDDList) {
		this.ptjProfileDDList = ptjProfileDDList;
	}

	public List<PmAddressType> getDeliveryAddressSelect() {
		return deliveryAddressSelect;
	}

	public void setDeliveryAddressSelect(List<PmAddressType> deliveryAddressSelect) {
		this.deliveryAddressSelect = deliveryAddressSelect;
	}

	public List<TplSpecZoneDtl> getDeliveryZones() {
		return deliveryZones;
	}

	public void setDeliveryZones(List<TplSpecZoneDtl> deliveryZones) {
		this.deliveryZones = deliveryZones;
	}

	public List<BriefingZoneBean> getAddressZoneBean() {
		return addressZoneBean;
	}

	public void setAddressZoneBean(List<BriefingZoneBean> addressZoneBean) {
		this.addressZoneBean = addressZoneBean;
	}

	public List<ScQtCheckList> getFinSpecList() {
		return finSpecList;
	}

	public void setFinSpecList(List<ScQtCheckList> finSpecList) {
		this.finSpecList = finSpecList;
	}

	public Long getPanelItemId() {
		return panelItemId;
	}

	public void setPanelItemId(Long panelItemId) {
		this.panelItemId = panelItemId;
	}

	public CtCircularVer getCircularVer() {
		return circularVer;
	}

	public void setCircularVer(CtCircularVer circularVer) {
		this.circularVer = circularVer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChairPersonPinNum() {
		return chairPersonPinNum;
	}

	public void setChairPersonPinNum(String chairPersonPinNum) {
		this.chairPersonPinNum = chairPersonPinNum;
	}

	public TreeNode getSelectedTreeNode() {
		return selectedTreeNode;
	}

	public void setSelectedTreeNode(TreeNode selectedTreeNode) {
		this.selectedTreeNode = selectedTreeNode;
	}

	public CtCircularSearchBean getCtSearchBean() {
		return ctSearchBean;
	}

	public void setCtSearchBean(CtCircularSearchBean ctSearchBean) {
		this.ctSearchBean = ctSearchBean;
	}

	public CtCircularVer getCtCircularVer() {
		return ctCircularVer;
	}

	public void setCtCircularVer(CtCircularVer ctCircularVer) {
		this.ctCircularVer = ctCircularVer;
	}

	public List<ScQtCheckList> getFinspecChecklist() {
		return finspecChecklist;
	}

	public void setFinspecChecklist(List<ScQtCheckList> finspecChecklist) {
		this.finspecChecklist = finspecChecklist;
	}

	public String getButtonOutCome() {
		return buttonOutCome;
	}

	public void setButtonOutCome(String buttonOutCome) {
		this.buttonOutCome = buttonOutCome;
	}

	public String getNewItemCategory() {
		return newItemCategory;
	}

	public void setNewItemCategory(String newItemCategory) {
		this.newItemCategory = newItemCategory;
	}

	public String getBankstmntTomonth() {
		return bankstmntTomonth;
	}

	public void setBankstmntTomonth(String bankstmntTomonth) {
		this.bankstmntTomonth = bankstmntTomonth;
	}

	public int getTempIndex() {
		return tempIndex;
	}

	public void setTempIndex(int tempIndex) {
		this.tempIndex = tempIndex;
	}

	public boolean isSelectPanelItem() {
		return selectPanelItem;
	}

	public void setSelectPanelItem(boolean selectPanelItem) {
		this.selectPanelItem = selectPanelItem;
	}

	public Long getSpecStatusId() {
		return specStatusId;
	}

	public void setSpecStatusId(Long specStatusId) {
		this.specStatusId = specStatusId;
	}

	public boolean isSpecSelectStats() {
		return specSelectStats;
	}

	public void setSpecSelectStats(boolean specSelectStats) {
		this.specSelectStats = specSelectStats;
	}

	public int getSelectedCheckList() {
		return selectedCheckList;
	}

	public void setSelectedCheckList(int selectedCheckList) {
		this.selectedCheckList = selectedCheckList;
	}

	public String getSpecType() {
		return specType;
	}

	public void setSpecType(String specType) {
		this.specType = specType;
	}

	public List<ScQtCheckListScore> getToBeRemoveList() {
		return toBeRemoveList;
	}

	public void setToBeRemoveList(List<ScQtCheckListScore> toBeRemoveList) {
		this.toBeRemoveList = toBeRemoveList;
	}

	public List<ScQtCheckListScore> getOtherScoreSelected() {
		return otherScoreSelected;
	}

	public void setOtherScoreSelected(List<ScQtCheckListScore> otherScoreSelected) {
		this.otherScoreSelected = otherScoreSelected;
	}

	public String getCriteriaType() {
		return criteriaType;
	}

	public void setCriteriaType(String criteriaType) {
		this.criteriaType = criteriaType;
	}

	public String getOtherCriteriaType() {
		return otherCriteriaType;
	}

	public void setOtherCriteriaType(String otherCriteriaType) {
		this.otherCriteriaType = otherCriteriaType;
	}

	public String getCheckListTitle() {
		return checkListTitle;
	}

	public void setCheckListTitle(String checkListTitle) {
		this.checkListTitle = checkListTitle;
	}

	public List<ScQtDeliveryLoc> getDeliveryLocRemoveList() {
		return deliveryLocRemoveList;
	}

	public void setDeliveryLocRemoveList(List<ScQtDeliveryLoc> deliveryLocRemoveList) {
		this.deliveryLocRemoveList = deliveryLocRemoveList;
	}

	public List<ScQtCheckList> getChecklistRemoveList() {
		return checklistRemoveList;
	}

	public void setChecklistRemoveList(List<ScQtCheckList> checklistRemoveList) {
		this.checklistRemoveList = checklistRemoveList;
	}

	public int getTechnicalTableRow() {
		return technicalTableRow;
	}

	public void setTechnicalTableRow(int technicalTableRow) {
		this.technicalTableRow = technicalTableRow;
	}

	public int getFinancialTableRow() {
		return financialTableRow;
	}

	public void setFinancialTableRow(int financialTableRow) {
		this.financialTableRow = financialTableRow;
	}

	public List<SelectItem> getScoreTypeList() {
		return ScoreTypeList;
	}

	public void setScoreTypeList(List<SelectItem> scoreTypeList) {
		ScoreTypeList = scoreTypeList;
	}

	public boolean isTechStandaraChecklist() {
		return isTechStandaraChecklist;
	}

	public void setTechStandaraChecklist(boolean isTechStandaraChecklist) {
		this.isTechStandaraChecklist = isTechStandaraChecklist;
	}

	public boolean isFinStandaraChecklist() {
		return isFinStandaraChecklist;
	}

	public void setFinStandaraChecklist(boolean isFinStandaraChecklist) {
		this.isFinStandaraChecklist = isFinStandaraChecklist;
	}

	public String getLocalOutCome() {
		return localOutCome;
	}

	public void setLocalOutCome(String localOutCome) {
		this.localOutCome = localOutCome;
	}

	public List<ScQtSpecificationRemark> getSpecificationRemarks() {
		return specificationRemarks;
	}

	public void setSpecificationRemarks(
			List<ScQtSpecificationRemark> specificationRemarks) {
		this.specificationRemarks = specificationRemarks;
	}

	public Long getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Long divisionId) {
		this.divisionId = divisionId;
	}

	

	public boolean isSpecComplete() {
		return specComplete;
	}

	public void setSpecComplete(boolean specComplete) {
		this.specComplete = specComplete;
	}

	public int getTechnicalSpecCount() {
		return technicalSpecCount;
	}

	public void setTechnicalSpecCount(int technicalSpecCount) {
		this.technicalSpecCount = technicalSpecCount;
	}

	public int getFinancialSpecCount() {
		return financialSpecCount;
	}

	public void setFinancialSpecCount(int financialSpecCount) {
		this.financialSpecCount = financialSpecCount;
	}

	public List<SelectItem> getBankyearsList() {
		return bankyearsList;
	}

	public void setBankyearsList(List<SelectItem> bankyearsList) {
		this.bankyearsList = bankyearsList;
	}

	public List<ScQtSupplier> getRestrictedSupplierList() {
		return restrictedSupplierList;
	}

	public void setRestrictedSupplierList(List<ScQtSupplier> restrictedSupplierList) {
		this.restrictedSupplierList = restrictedSupplierList;
	}

	public boolean isNewTemplateSpec() {
		return newTemplateSpec;
	}

	public void setNewTemplateSpec(boolean newTemplateSpec) {
		this.newTemplateSpec = newTemplateSpec;
	}

	public TreeNode getDeletePanelTreenode() {
		return deletePanelTreenode;
	}

	public void setDeletePanelTreenode(TreeNode deletePanelTreenode) {
		this.deletePanelTreenode = deletePanelTreenode;
	}

	public List<SelectItem> getFinancialChecklistAction() {
		return financialChecklistAction;
	}

	public void setFinancialChecklistAction(
			List<SelectItem> financialChecklistAction) {
		this.financialChecklistAction = financialChecklistAction;
	}

	public boolean isSpecNewable() {
		return specNewable;
	}

	public void setSpecNewable(boolean specNewable) {
		this.specNewable = specNewable;
	}

	public QtnTenderSearchBean getQtnTendertemplateSearchBean() {
		return qtnTendertemplateSearchBean;
	}

	public void setQtnTendertemplateSearchBean(
			QtnTenderSearchBean qtnTendertemplateSearchBean) {
		this.qtnTendertemplateSearchBean = qtnTendertemplateSearchBean;
	}

	public ScQtScoreSetting getFinacialItemScoreSetting() {
		return finacialItemScoreSetting;
	}

	public void setFinacialItemScoreSetting(
			ScQtScoreSetting finacialItemScoreSetting) {
		this.finacialItemScoreSetting = finacialItemScoreSetting;
	}

	public BigDecimal getFinancialItemScore() {
		return financialItemScore;
	}

	public void setFinancialItemScore(BigDecimal financialItemScore) {
		this.financialItemScore = financialItemScore;
	}

	public BigDecimal getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(BigDecimal totalScore) {
		this.totalScore = totalScore;
	}

	public String getLocationLevel() {
		return locationLevel;
	}

	public void setLocationLevel(String locationLevel) {
		this.locationLevel = locationLevel;
	}

	public IPagingDataModel<PmAddressType> getSearchDeliveryAddressDataModel() {
		return searchDeliveryAddressDataModel;
	}

	public void setSearchDeliveryAddressDataModel(
			IPagingDataModel<PmAddressType> searchDeliveryAddressDataModel) {
		this.searchDeliveryAddressDataModel = searchDeliveryAddressDataModel;
	}

	public Long getPtjGroupId() {
		return ptjGroupId;
	}

	public void setPtjGroupId(Long ptjGroupId) {
		this.ptjGroupId = ptjGroupId;
	}

	public int getCheckListYesIndex() {
		return checkListYesIndex;
	}

	public void setCheckListYesIndex(int checkListYesIndex) {
		this.checkListYesIndex = checkListYesIndex;
	}

	public boolean isTechnicalOtherChecklistSubmit() {
		return technicalOtherChecklistSubmit;
	}

	public void setTechnicalOtherChecklistSubmit(
			boolean technicalOtherChecklistSubmit) {
		this.technicalOtherChecklistSubmit = technicalOtherChecklistSubmit;
	}

	public boolean isFinancialOtherChecklistSubmit() {
		return financialOtherChecklistSubmit;
	}

	public void setFinancialOtherChecklistSubmit(
			boolean financialOtherChecklistSubmit) {
		this.financialOtherChecklistSubmit = financialOtherChecklistSubmit;
	}

	public boolean isZoneSpecUpdate() {
		return zoneSpecUpdate;
	}

	public void setZoneSpecUpdate(boolean zoneSpecUpdate) {
		this.zoneSpecUpdate = zoneSpecUpdate;
	}

	public boolean isZonesWarnMessageUpdate() {
		return zonesWarnMessageUpdate;
	}

	public void setZonesWarnMessageUpdate(boolean zonesWarnMessageUpdate) {
		this.zonesWarnMessageUpdate = zonesWarnMessageUpdate;
	}

	public boolean isSpecItemWeightageRequired() {
		return isSpecItemWeightageRequired;
	}

	public void setSpecItemWeightageRequired(boolean isSpecItemWeightageRequired) {
		this.isSpecItemWeightageRequired = isSpecItemWeightageRequired;
	}

	public int getMinistryRemarksSize() {
		return ministryRemarksSize;
	}

	public void setMinistryRemarksSize(int ministryRemarksSize) {
		this.ministryRemarksSize = ministryRemarksSize;
	}

	public boolean isTechnicalUserScoringSchema() {
		return isTechnicalUserScoringSchema;
	}

	public void setTechnicalUserScoringSchema(boolean isTechnicalUserScoringSchema) {
		this.isTechnicalUserScoringSchema = isTechnicalUserScoringSchema;
	}

	public boolean isFinancialUserScoringSchema() {
		return isFinancialUserScoringSchema;
	}

	public void setFinancialUserScoringSchema(boolean isFinancialUserScoringSchema) {
		this.isFinancialUserScoringSchema = isFinancialUserScoringSchema;
	}

	public List<ScQtLocCriteria> getSupplierLocationList() {
		return supplierLocationList;
	}

	public void setSupplierLocationList(List<ScQtLocCriteria> supplierLocationList) {
		this.supplierLocationList = supplierLocationList;
	}

	public ScQtSupplierCriteria getSupplierCriteria() {
		return supplierCriteria;
	}

	public void setSupplierCriteria(ScQtSupplierCriteria supplierCriteria) {
		this.supplierCriteria = supplierCriteria;
	}

	public List<ScQtApproval> getToBeApprovalRemoveList() {
		return toBeApprovalRemoveList;
	}

	public void setToBeApprovalRemoveList(List<ScQtApproval> toBeApprovalRemoveList) {
		this.toBeApprovalRemoveList = toBeApprovalRemoveList;
	}

	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	public List<ZonesBean> getBeans() {
		return beans;
	}

	public void setBeans(List<ZonesBean> beans) {
		this.beans = beans;
	}

	public String getFulfilmentType() {
		return fulfilmentType;
	}

	public void setFulfilmentType(String fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}
	
	public List<ScQtSchedule> getScheduleList() {
		return scheduleList;
	}

	public void setScheduleList(List<ScQtSchedule> scheduleList) {
		this.scheduleList = scheduleList;
	}

	public List<ScQtProgressivePayment> getProgressivePaymentList() {
		return progressivePaymentList;
	}

	public void setProgressivePaymentList(
			List<ScQtProgressivePayment> progressivePaymentList) {
		this.progressivePaymentList = progressivePaymentList;
	}

	public int getTotalFrequency() {
		return totalFrequency;
	}

	public void setTotalFrequency(int totalFrequency) {
		this.totalFrequency = totalFrequency;
	}

	public int getTotalProgressivePayment() {
		return totalProgressivePayment;
	}

	public void setTotalProgressivePayment(int totalProgressivePayment) {
		this.totalProgressivePayment = totalProgressivePayment;
	}



	public String getProcuremntCategory() {
		return procuremntCategory;
	}

	public void setProcuremntCategory(String procuremntCategory) {
		this.procuremntCategory = procuremntCategory;
	}

	public List<ScQtRemark> getScQtRemarksList() {
		return scQtRemarksList;
	}

	public void setScQtRemarksList(List<ScQtRemark> scQtRemarksList) {
		this.scQtRemarksList = scQtRemarksList;
	}

	public ScQtRemark getContractremark() {
		return contractremark;
	}

	public void setContractremark(ScQtRemark contractremark) {
		this.contractremark = contractremark;
	}

	public List<SelectItem> getEvaluationTypeItem() {
		return evaluationTypeItem;
	}

	public void setEvaluationTypeItem(List<SelectItem> evaluationTypeItem) {
		this.evaluationTypeItem = evaluationTypeItem;
	}

	public boolean isPanelChange() {
		return isPanelChange;
	}

	public void setPanelChange(boolean isPanelChange) {
		this.isPanelChange = isPanelChange;
	}
	
	public String getTempZoneName() {
		return tempZoneName;
	}

	public void setTempZoneName(String tempZoneName) {
		this.tempZoneName = tempZoneName;
	}

	public boolean isState() {
		return isState;
	}

	public void setState(boolean isState) {
		this.isState = isState;
	}

	/**
	 * @return the selectCtPanelItem
	 */
	public List<CtPanelItem> getSelectCtPanelItem() {
		return selectCtPanelItem;
	}

	/**
	 * @param selectCtPanelItem the selectCtPanelItem to set
	 */
	public void setSelectCtPanelItem(List<CtPanelItem> selectCtPanelItem) {
		this.selectCtPanelItem = selectCtPanelItem;
	}

	/**
	 * @return the loginRoleType
	 */
	public String getLoginRoleType() {
		return loginRoleType;
	}

	/**
	 * @param loginRoleType the loginRoleType to set
	 */
	public void setLoginRoleType(String loginRoleType) {
		this.loginRoleType = loginRoleType;
	}

	/**
	 * @return the toBeRemoveRestrictedSupplierList
	 */
	public List<ScQtSupplier> getToBeRemoveRestrictedSupplierList() {
		return toBeRemoveRestrictedSupplierList;
	}

	/**
	 * @param toBeRemoveRestrictedSupplierList the toBeRemoveRestrictedSupplierList to set
	 */
	public void setToBeRemoveRestrictedSupplierList(
			List<ScQtSupplier> toBeRemoveRestrictedSupplierList) {
		this.toBeRemoveRestrictedSupplierList = toBeRemoveRestrictedSupplierList;
	}

	/**
	 * @return the searchDataModelPanelItem
	 */
	public IPagingDataModel<CtPanelItem> getSearchDataModelPanelItem() {
		return searchDataModelPanelItem;
	}

	/**
	 * @param searchDataModelPanelItem the searchDataModelPanelItem to set
	 */
	public void setSearchDataModelPanelItem(
			IPagingDataModel<CtPanelItem> searchDataModelPanelItem) {
		this.searchDataModelPanelItem = searchDataModelPanelItem;
	}

}
