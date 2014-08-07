package my.ep.web.managedbean.sc.quationtender;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.doc.TplDocMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.sc.quatationtender.CloneAllDataModel;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.sc.quatationtender.UserCommitteeSearchBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.sc.quationtender.CloneAll;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * @Author VidyasagarReddy
 */

@ManagedBean(name = "qTDetailMB")
@SessionScoped
public class QTDetailManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(QTDetailManagedBean.class);

	private ScQt qt;

	private ScQtDoc qtDoc;
	private ScQtRemark qtRemark;
	private SmMofAccount smMofAccount;
	private ScRemark scRemark;
	private ScQtCheckList qtCheckList;
	private ScQtContactPerson qtContactPerson = new ScQtContactPerson();
	private ScQtDeduction qtDeduction;
	private ScQtDeductionClause qtDeductionClause;
	private PpPlan ppPlan;
	private PmOrgProfile pmOrgProfile;

	private String selectedBoolean1;
	// private String multiLoc;
	private List<CtPanelSupplier> ctPanelSuppliers;

	/**
	 * For Search Quotation/Tender Screen
	 */
	private QtnTenderSearchBean qtnTenderSearchBean;
	private QtnTenderSearchBean cloneQtnTenderSearchBean;

	private List<PmCategoryL1> categoryL1s;

	private List<PmCategoryL2> categoryL2s;

	private List<PmCategoryL3> categoryL3s;

	private List<PmState> states;
	private List<PmCountry> countries;
	private List<PmDistrict> districts;
	private List<PmCity> citys;

	public List<ScQtBudget> gosodos;
	public List<ScQtDoc> scQtDocs;
	public List<CloneAll> cloneAllList;

	private List<CtCircular> ctCirculars;
	private List<ScQtRemark> scQtRemarks;
	private List<ScQtCheckList> scQtChecklists;
	private List<ScQtDeduction> scQtDeductionList;
	private List<ScQtDeductionClause> scQtDeductionClauses;
	private List<CtPanelSupplier> panelSupplierList;

	private List<ScQtBsvDtl> selectedBriefingDetails;
	private List<ScQtBsvDtl> selectedSiteVisitDetails;
	private List<ScQtCheckList> selectedCheckLists;
	private List<ScQtBudget> selectedGosodoBudgets;
	private List<SmMofAccount> selectedpanelsupplier;
	private List<ScQt> selectedCloneTenderList;
	private List<CloneAll> selectedCloneAllList;
	private List<ScQtDeduction> selectedScQtDeductions;
	// private List<ScQtDeduction> selectedScQtDeduction;
	private ScQtDeduction selectedScQtDeduction;
	private List<ScQtDeductionClause> selectedDeductionClauses;

	private String qtNumber;

	private PmOrgProfile orgProfile;
	private PmOrgProfile selectedUserMinistry;
	private PmOrgProfile selectedUserDepartment;

	private List<PmOrgProfile> userMinistryList;
	private List<PmOrgProfile> userDepartmentList;
	private List<PmOrgProfile> userDivisionList;

	private List<PmGo> goList;
	private List<PmSo> soList;
	private List<PmDo> doList;

	private String minstryCode;
	private String deptCode;
	private String divisionCode;

	private TreeNode root2;
	private TreeNode root3;
	private TreeNode[] selectedNodes;
	private TreeNode[] selectedLocNodes;
	private TreeNode[] selectedAddrNodes;
	private List<TreeNode> ctZoneSuppliers = new ArrayList<TreeNode>();

	private List<CtPanelSupplier> ctZoneSupplierList;

	private List<ScQt> cloneTenderList = new ArrayList<ScQt>();
	private List<ScQt> cloneBusinessTenderList = new ArrayList<ScQt>();
	private List<ScQt> documentCloneTenderList = new ArrayList<ScQt>();
	private ScQt tenderCriteria = new ScQt();

	private List<TabBean> tabList;

	private String checkBoxBPK;
	private List<String> checkBoxBPK1;
	private String standardDocument;
	private List<String> approvalTypelist = new ArrayList<String>();
	private int activeIndex = 0;
	private int subTabIndex = 0;
	private CloneAllDataModel cloneAll;

	private List<PmUser> veryfiers;
	private List<PmUser> approvers;
	private List<PmUser> bbkApprovers;
	private String outCome;
	private String processName;
	private ScQtApproval scQtApproval;
	private String uploadUpdateId;
	// Payload for BPM
	private Object quotationTenderDataType;
	private boolean isEditable = true;

	private List<SmSupplier> panelSuppliers;

	// For Circular Panel Search Criteria
	private String circularNo;
	private String circularName;
	private String itemName;
	private String circularCoverage;
	private Date circularExpDate;

	// For Restricted Tender Search Criteria
	private String supplierName;
	private String mofAccountNo;
	private BigDecimal minPaidUpCapital;

	private TreeNode supplierSelectedTreeNode;

	private int totalSearchSupplier;
	private int totalSupplier;
	private TreeNode searchPanelSuppliersNode;
	private TreeNode suppliersNode;
	private List<SmSupplier> searchSupplierList;
	private List<SmSupplier> selectedSupplierList;
	private List<ScQtSupplier> qtSupplierList;
	private List<ScQtSupplier> selectedQtSupplierList;
	private List<SmPersonnel> smPersonnelsList;
	private SmPersonnel personnel;

	private String supplierBranch;
	private String supplierContact;
	private String supplierEmail;
	private SmAddress supplierAddress;

	// supplier Qualification Criteria List and variables
	private List<ScQtCategory> suppCriteriarestrictedList;
	private List<ScQtCategory> selectedRestrictedCategoryList;
	private List<ScQtCategory> selectedCategoryList;
	private List<ScQtCategory> qtCategoryList;

	private IPagingDataModel<ScQt> searchQTDataModel;
	private IPagingDataModel<ScQt> cloneSearchQTDataModel;

	private List<TreeNode> zonesList = new ArrayList<TreeNode>();
	private List<TreeNode> zoneTrees = new ArrayList<TreeNode>();
	private List<TreeNode> zoneTreeList = new ArrayList<TreeNode>();
	private List<TreeNode> zoneTreeAddrList = new ArrayList<TreeNode>();
	private TreeNode rootLocationTree;
	private String zoneName;
	private String zoneName1;
	private String zoneAddrName;
	private List<PmReportSection> pmReportSectionList;
	private List<PmReportSection> selectedPmReportSectionList;
	private TreeNode rootLocationTreeList;
	private TreeNode rootAddressLocationTreeList;
	private String deductionPaymentArray;
	private boolean percentageField = true;
	private boolean amountField = true;
	private boolean descriptionField = true;
	private boolean durationField = true;

	private int allStateCode;

	// Business Owner
	private List<ScCommitteeMember> scCommitteeMemberList;
	private ScCommitteeMember selectScCommitteMember;
	private ScCommitteeMember committeMember;
	private List<SelectItem> selections;
	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;
	private List<ScQtCommittee> cloneSearchCommittes;
	private ScQtCommittee selectedCloneCommittee;
	private List<ScCommitteeMember> cloneCommitteMemebers;
	private List<ScCommitteeMember> selectedCommitteMemebers;
	private ScQtCommittee selectedScQtCommittee;
	
	private IPagingDataModel<ScQt> cloneBusinessOwnerDataModel;
	private IPagingDataModel<PmUser> userSearchBusinessOwnerDataModel;
	private UserCommitteeSearchBean userBusineeOwnerSearchBean;
	private QtnTenderSearchBean cloneBusinessOwnerSearchBean;
	private List<ScQt> selectedCloneBOList;
	
	// Search properties
	private String title = null;
	private String qtNo = null;
	private String procurementMode;
	private Date createdDtFrom;
	private Date createdDtTo;
	private String status;

	private String name = null;
	private String identificationNo = null;
	private String identificationType;

	private int publishingPeriod;

	private List<SelectItem> comitteeUsers;
	private List<SelectItem> budgetApprover;
	private List<PmUser> budgetApproverName; // Drop down Listing for Approver
												// Names
	private List<PmUser> comitteeUserName; // Drop down Listing for Approver
											// Names
	private DualListModel<PmUser> budgetApprovalUsers;
	private DualListModel<PmUser> committeeApprovalUsers;
	private DualListModel<PmUser> ptjApprovalUsers;
	private DualListModel<ScQtApproverSettings> publicationApprovals;
	private ScQtApproval publicationApproval;

	private List<ScQtCommitteRemarks> qtCommitteeRemarks;

	private List<PmUser> deskOfficerAdmin;

	private List<TreeNode> treeNodesList;
	private ScQtCommittee scQtCommittee;

	private List<ScQtApprovalAttachment> attachments;
	private List<ScQtCheckListAttachment> checklistAttachment;

	private List<BriefingZoneBean> briefingZoneBean = new ArrayList<BriefingZoneBean>();

	private int index = -1;

	private ScQtSupplier scQtSupplier;
	private CtContract ctContract = new CtContract();
	private List<CtPanelItem> selectpanelItem;

	// Committee Appointment
	private String tabTitle;
	private ScCommittee scCommittee;
	private List<ScCommitteeMember> committemembers;

	private String committeNo = null;
	private Date createdDt;
	private String selectedCommitte;
	private TreeNode searchcommitteeNode;
	private TreeNode[] selectSearchcommitteeNode;

	private ScQt selectedCloneTender;
	private ScQt selectedCloneBusinessTender;
	private ScQt selectedDocumentCloneTender;
	private List<ScQtCheckList> qtCheckLists;
	private List<ScQtCheckList> selectedQtCheckLists;

	private List<PmParameterDesc> roleTypes; // Role type drop down

	private List<TabBean> scCommitteeMemberTabList;
	private List<PmParameter> finalizationSecratary;

	// Existing Committee Search
	private IPagingDataModel<PmUser> userSearchCommitteeDataModel;
	private UserCommitteeSearchBean userCommitteeSearchBean;

	// Briefing & Site Visit Details
	private ScQtBsvDtl qtBriefing;
	private ScQtBsvDtl qtSiteBriefing = new ScQtBsvDtl();
	public List<ScQtBsvDtl> briefings;
	public List<ScQtBsvDtl> siteBriefings;
	private String briefingZoneName;
	private String previousPage;
	private List<ScQtBsvRegistration> briefingRegistrationList;
	private ScQtBsvRegistration selectedbriefRegistration;
	private List<ScQtBsvAttendance> scQtBsvAttendanceList;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private List<TplSpecZoneDtl> zones;
	private List<ScRemark> remarks;
	private TplSpecZoneDtl selectedZone;
	private ScQtBsvRegistration scQtBsvRegistration;
	private ScQtBsv scQtBsv;
	private ScQtBsv scQtBriefing;
	private ScQtBsv scQtSitevisit;
	private ScQtBSVRemark scQtBSVRemark;
	private boolean required = false;
	private List<ScQtBsvAttendance> briefingAttendanceList;
	private ScQtBsvAttendance scQtBriefingAttendance;

	// Schedule & Contact Information
	private List<ScQtContactPerson> pmuserContactpersonList;
	private List<ScQtContactPerson> selectedpmuserContactpersonList;
	private List<PmUser> pmUsersList;
	private List<PmUser> selectedPmUsersList;
	private PmUser pmUser;
	private ScQtSampleSubmission scQtSampleSubmission;
	private List<ScQtSampleSubmission> scQtSampleSubmissionList;
	private List<ScQtSampleSubmission> selectedSubmissionList;
	private List<ScQtContactPerson> scQtContactPersons;
	private List<ScQtContactPerson> selectedContactPersons;
	private PmAddress pmAddress;
	private Date proposalValidityUntil;
	private Date closingDate;

	// Document
	private ScQtCheckList scQtChecklist;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private List<PmDocument> pmDocumentList;
	private List<PmDocument> selectedPmDocumentList;
	private List<ScQtDocReport> scQtDocReportList;
	private List<ScQtDocReport> selectedScQtDocReports;
	private ScQtDocReport selectedScQtDocReport;
	private ScQtDocReport scQtDocReport;
	private TplDocMaster tplDocMaster;
	
	private IPagingDataModel<ScQt> cloneDocumentDataModel;
	private QtnTenderSearchBean cloneDocumentSearchBean;
	private List<ScQtDocReport> cloneScQtDocReports;
	private List<ScQtDocReport> selectedCloneScQtDocReports;
	
	private TreeNode[] selectedGeneralTreeNodes;

	public ScQtSupplierCriteria supplierCriteria;
	private List<TreeNode> zoneGeneralTrees = new ArrayList<TreeNode>();

	private List<ScQtApprovalRequest> scQtApprovalRequestList;
	private String approvalType;
	private ISearchResultBean<ScQtCommittee> qtCommittes;

	private List<ScQtCheckList> technicalSpecList;
	private List<ScQtCheckList> financialSpecList;

	// Added code to move to tabmenus..
	private MenuModel menuModel;
	public int selectedTabIndex;
	public TabBean selectedTabBean;
	
	public MenuModel approvalMenuModel;
	public int selectedApprovalTabIndex;
	public TabBean selectedApprovalTabBean;

	public void setPanelSuppliers(List<SmSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}

	public QTDetailManagedBean() {
		log.info("inside ");

		qtnTenderSearchBean = new QtnTenderSearchBean();
		cloneQtnTenderSearchBean = new QtnTenderSearchBean();

		userCommitteeSearchBean = new UserCommitteeSearchBean();

		cloneBusinessOwnerSearchBean = new QtnTenderSearchBean();
		userBusineeOwnerSearchBean = new UserCommitteeSearchBean();

		cloneDocumentSearchBean = new QtnTenderSearchBean();

		root2 = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("Clone All", root2);
		node0.setExpanded(true);

		CloneAll[] enumValues = CloneAll.values();
		for (CloneAll cloneValue : enumValues) {
			TreeNode node1 = new DefaultTreeNode(cloneValue.getDisplayName(),
					node0);
		}

		getPTJApproverNames();
	}

	private void getPTJApproverNames() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> availableApprover = userResDTO.getUserList();
		List<PmUser> selectedApprover = new ArrayList<PmUser>();
		ptjApprovalUsers = new DualListModel<PmUser>(availableApprover,
				selectedApprover);
	}

	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}

	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	public DualListModel<PmUser> getCommitteeApprovalUsers() {
		return committeeApprovalUsers;
	}

	public void setCommitteeApprovalUsers(
			DualListModel<PmUser> committeeApprovalUsers) {
		this.committeeApprovalUsers = committeeApprovalUsers;
	}

	public DualListModel<PmUser> getBudgetApprovalUsers() {
		return budgetApprovalUsers;
	}

	public void setBudgetApprovalUsers(DualListModel<PmUser> budgetApprovalUsers) {
		this.budgetApprovalUsers = budgetApprovalUsers;
	}

	public List<BriefingZoneBean> getBriefingZoneBean() {
		return briefingZoneBean;
	}

	public void setBriefingZoneBean(List<BriefingZoneBean> briefingZoneBean) {
		this.briefingZoneBean = briefingZoneBean;
	}

	public List<PmUser> getComitteeUserName() {
		return comitteeUserName;
	}

	public void setComitteeUserName(List<PmUser> comitteeUserName) {
		this.comitteeUserName = comitteeUserName;
	}

	public List<PmUser> getBudgetApproverName() {
		return budgetApproverName;
	}

	public void setBudgetApproverName(List<PmUser> budgetApproverName) {
		this.budgetApproverName = budgetApproverName;
	}

	public List<SelectItem> getBudgetApprover() {
		return budgetApprover;
	}

	public void setBudgetApprover(List<SelectItem> budgetApprover) {
		this.budgetApprover = budgetApprover;
	}

	public List<ScQtCheckListAttachment> getChecklistAttachment() {
		return checklistAttachment;
	}

	public void setChecklistAttachment(
			List<ScQtCheckListAttachment> checklistAttachment) {
		this.checklistAttachment = checklistAttachment;
	}

	public List<ScQtApprovalAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<ScQtApprovalAttachment> attachments) {
		this.attachments = attachments;
	}

	public List<ScQtSupplier> getSelectedQtSupplierList() {
		return selectedQtSupplierList;
	}

	public void setSelectedQtSupplierList(
			List<ScQtSupplier> selectedQtSupplierList) {
		this.selectedQtSupplierList = selectedQtSupplierList;
	}

	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	public List<TreeNode> getTreeNodesList() {
		return treeNodesList;
	}

	public void setTreeNodesList(List<TreeNode> treeNodesList) {
		this.treeNodesList = treeNodesList;
	}

	public List<SelectItem> getComitteeUsers() {
		return comitteeUsers;
	}

	public void setComitteeUsers(List<SelectItem> comitteeUsers) {
		this.comitteeUsers = comitteeUsers;
	}

	public int getPublishingPeriod() {
		return publishingPeriod;
	}

	public void setPublishingPeriod(int publishingPeriod) {
		this.publishingPeriod = publishingPeriod;
	}

	public List<PmUser> getSelectedPmUser() {
		return selectedPmUser;
	}

	public void setSelectedPmUser(List<PmUser> selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	public ScCommitteeMember getSelectScCommitteMember() {
		return selectScCommitteMember;
	}

	public void setSelectScCommitteMember(
			ScCommitteeMember selectScCommitteMember) {
		this.selectScCommitteMember = selectScCommitteMember;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

	public List<ScCommitteeMember> getCloneCommitteMemebers() {
		return cloneCommitteMemebers;
	}

	public void setCloneCommitteMemebers(
			List<ScCommitteeMember> cloneCommitteMemebers) {
		this.cloneCommitteMemebers = cloneCommitteMemebers;
	}

	public List<ScCommitteeMember> getSelectedCommitteMemebers() {
		return selectedCommitteMemebers;
	}

	public void setSelectedCommitteMemebers(
			List<ScCommitteeMember> selectedCommitteMemebers) {
		this.selectedCommitteMemebers = selectedCommitteMemebers;
	}

	public ScQtCommittee getSelectedScQtCommittee() {
		return selectedScQtCommittee;
	}

	public void setSelectedScQtCommittee(ScQtCommittee selectedScQtCommittee) {
		this.selectedScQtCommittee = selectedScQtCommittee;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQtNo() {
		return qtNo;
	}

	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	public String getProcurementMode() {
		return procurementMode;
	}

	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	public Date getCreatedDtFrom() {
		return createdDtFrom;
	}

	public void setCreatedDtFrom(Date createdDtFrom) {
		this.createdDtFrom = createdDtFrom;
	}

	public Date getCreatedDtTo() {
		return createdDtTo;
	}

	public void setCreatedDtTo(Date createdDtTo) {
		this.createdDtTo = createdDtTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ScQtCommittee> getCloneSearchCommittes() {
		return cloneSearchCommittes;
	}

	public void setCloneSearchCommittes(List<ScQtCommittee> cloneSearchCommittes) {
		this.cloneSearchCommittes = cloneSearchCommittes;
	}

	public List<PmUser> getExternalUsers() {
		return externalUsers;
	}

	public void setExternalUsers(List<PmUser> externalUsers) {
		this.externalUsers = externalUsers;
	}

	public List<SelectItem> getSelections() {
		return selections;
	}

	public void setSelections(List<SelectItem> selections) {
		this.selections = selections;
	}

	public ScCommitteeMember getCommitteMember() {
		return committeMember;
	}

	public void setCommitteMember(ScCommitteeMember committeMember) {
		this.committeMember = committeMember;
	}

	public List<ScCommitteeMember> getScCommitteeMemberList() {
		return scCommitteeMemberList;
	}

	public void setScCommitteeMemberList(
			List<ScCommitteeMember> scCommitteeMemberList) {
		this.scCommitteeMemberList = scCommitteeMemberList;
	}

	public List<TreeNode> getZoneTreeAddrList() {
		return zoneTreeAddrList;
	}

	public void setZoneTreeAddrList(List<TreeNode> zoneTreeAddrList) {
		this.zoneTreeAddrList = zoneTreeAddrList;
	}

	public List<TreeNode> getZoneTrees() {
		return zoneTrees;
	}

	public void setZoneTrees(List<TreeNode> zoneTrees) {
		this.zoneTrees = zoneTrees;
	}

	public List<TreeNode> getZoneTreeList() {
		return zoneTreeList;
	}

	public void setZoneTreeList(List<TreeNode> zoneTreeList) {
		this.zoneTreeList = zoneTreeList;
	}

	public TreeNode getRootLocationTree() {
		return rootLocationTree;
	}

	public void setRootLocationTree(TreeNode rootLocationTree) {
		this.rootLocationTree = rootLocationTree;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public String getZoneName1() {
		return zoneName1;
	}

	public void setZoneName1(String zoneName1) {
		this.zoneName1 = zoneName1;
	}

	public String getZoneAddrName() {
		return zoneAddrName;
	}

	public void setZoneAddrName(String zoneAddrName) {
		this.zoneAddrName = zoneAddrName;
	}

	public List<PmReportSection> getPmReportSectionList() {
		return pmReportSectionList;
	}

	public void setPmReportSectionList(List<PmReportSection> pmReportSectionList) {
		this.pmReportSectionList = pmReportSectionList;
	}

	public List<PmReportSection> getSelectedPmReportSectionList() {
		return selectedPmReportSectionList;
	}

	public void setSelectedPmReportSectionList(
			List<PmReportSection> selectedPmReportSectionList) {
		this.selectedPmReportSectionList = selectedPmReportSectionList;
	}

	public TreeNode getRootLocationTreeList() {
		return rootLocationTreeList;
	}

	public void setRootLocationTreeList(TreeNode rootLocationTreeList) {
		this.rootLocationTreeList = rootLocationTreeList;
	}

	public TreeNode getRootAddressLocationTreeList() {
		return rootAddressLocationTreeList;
	}

	public void setRootAddressLocationTreeList(
			TreeNode rootAddressLocationTreeList) {
		this.rootAddressLocationTreeList = rootAddressLocationTreeList;
	}

	public String getDeductionPaymentArray() {
		return deductionPaymentArray;
	}

	public void setDeductionPaymentArray(String deductionPaymentArray) {
		this.deductionPaymentArray = deductionPaymentArray;
	}

	public boolean isPercentageField() {
		return percentageField;
	}

	public void setPercentageField(boolean percentageField) {
		this.percentageField = percentageField;
	}

	public boolean isDescriptionField() {
		return descriptionField;
	}

	public void setDescriptionField(boolean descriptionField) {
		this.descriptionField = descriptionField;
	}

	public boolean isAmountField() {
		return amountField;
	}

	public void setAmountField(boolean amountField) {
		this.amountField = amountField;
	}

	public QtnTenderSearchBean getQtnTenderSearchBean() {
		return qtnTenderSearchBean;
	}

	public void setQtnTenderSearchBean(QtnTenderSearchBean qtnTenderSearchBean) {
		this.qtnTenderSearchBean = qtnTenderSearchBean;
	}

	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}

	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	public TreeNode getRoot2() {
		return root2;
	}

	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	public TreeNode getRoot3() {
		return root3;
	}

	public void setRoot3(TreeNode root3) {
		this.root3 = root3;
	}

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<PmCategoryL1> getCategoryL1s() {
		return categoryL1s;
	}

	public void setCategoryL1s(List<PmCategoryL1> categoryL1s) {
		this.categoryL1s = categoryL1s;
	}

	public List<PmCategoryL2> getCategoryL2s() {
		return categoryL2s;
	}

	public void setCategoryL2s(List<PmCategoryL2> categoryL2s) {
		this.categoryL2s = categoryL2s;
	}

	public List<PmCategoryL3> getCategoryL3s() {
		return categoryL3s;
	}

	public void setCategoryL3s(List<PmCategoryL3> categoryL3s) {
		this.categoryL3s = categoryL3s;
	}

	public String getQtNumber() {
		return qtNumber;
	}

	public void setQtNumber(String qtNumber) {
		this.qtNumber = qtNumber;
	}

	public ScQt getQt() {
		return qt;
	}

	public void setQt(ScQt qt) {
		this.qt = qt;
	}

	public List<PmState> getStates() {
		return states;
	}

	public void setStates(List<PmState> states) {
		this.states = states;
	}

	public List<PmDistrict> getDistricts() {
		return districts;
	}

	public void setDistricts(List<PmDistrict> districts) {
		this.districts = districts;
	}

	public List<ScQtBudget> getGosodos() {
		return gosodos;
	}

	public void setGosodos(List<ScQtBudget> gosodos) {
		this.gosodos = gosodos;
	}

	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}

	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	public List<PmGo> getGoList() {
		return goList;
	}

	public void setGoList(List<PmGo> goList) {
		this.goList = goList;
	}

	public List<PmSo> getSoList() {
		return soList;
	}

	public void setSoList(List<PmSo> soList) {
		this.soList = soList;
	}

	public List<PmDo> getDoList() {
		return doList;
	}

	public void setDoList(List<PmDo> doList) {
		this.doList = doList;
	}

	public ScQt getTenderCriteria() {
		return tenderCriteria;
	}

	public void setTenderCriteria(ScQt tenderCriteria) {
		this.tenderCriteria = tenderCriteria;
	}

	/*
	 * public String getMultiLoc() { return multiLoc; }
	 * 
	 * public void setMultiLoc(String multiLoc) { this.multiLoc = multiLoc; }
	 */

	public String getSelectedBoolean1() {
		return selectedBoolean1;
	}

	public void setSelectedBoolean1(String selectedBoolean1) {
		this.selectedBoolean1 = selectedBoolean1;
	}

	public PmOrgProfile getSelectedUserMinistry() {
		return selectedUserMinistry;
	}

	public void setSelectedUserMinistry(PmOrgProfile selectedUserMinistry) {
		this.selectedUserMinistry = selectedUserMinistry;
	}

	public PmOrgProfile getSelectedUserDepartment() {
		return selectedUserDepartment;
	}

	public void setSelectedUserDepartment(PmOrgProfile selectedUserDepartment) {
		this.selectedUserDepartment = selectedUserDepartment;
	}

	public String getMinstryCode() {
		return minstryCode;
	}

	public void setMinstryCode(String minstryCode) {
		this.minstryCode = minstryCode;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public List<SmSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}

	public List<TabBean> getTabList() {
		return tabList;
	}

	public void setTabList(List<TabBean> tabList) {
		this.tabList = tabList;
	}

	public String getCheckBoxBPK() {
		return checkBoxBPK;
	}

	public void setCheckBoxBPK(String checkBoxBPK) {
		this.checkBoxBPK = checkBoxBPK;
	}

	public List<String> getCheckBoxBPK1() {
		return checkBoxBPK1;
	}

	public void setCheckBoxBPK1(List<String> checkBoxBPK1) {
		this.checkBoxBPK1 = checkBoxBPK1;
	}

	public String getStandardDocument() {
		return standardDocument;
	}

	public void setStandardDocument(String standardDocument) {
		this.standardDocument = standardDocument;
	}

	public List<String> getApprovalTypelist() {
		return approvalTypelist;
	}

	public void setApprovalTypelist(List<String> approvalTypelist) {
		this.approvalTypelist = approvalTypelist;
	}

	public int getActiveIndex() {
		return activeIndex;
	}

	public void setActiveIndex(int activeIndex) {
		this.activeIndex = activeIndex;
	}

	public int getSubTabIndex() {
		return subTabIndex;
	}

	public void setSubTabIndex(int subTabIndex) {
		this.subTabIndex = subTabIndex;
	}

	public ScQtBsvDtl getQtBriefing() {
		return qtBriefing;
	}

	public void setQtBriefing(ScQtBsvDtl qtBriefing) {
		this.qtBriefing = qtBriefing;
	}

	public ScQtBsvDtl getQtSiteBriefing() {
		return qtSiteBriefing;
	}

	public void setQtSiteBriefing(ScQtBsvDtl qtSiteBriefing) {
		this.qtSiteBriefing = qtSiteBriefing;
	}

	public ScQtDoc getQtDoc() {
		return qtDoc;
	}

	public void setQtDoc(ScQtDoc qtDoc) {
		this.qtDoc = qtDoc;
	}

	public List<ScQtBsvDtl> getBriefings() {
		return briefings;
	}

	public void setBriefings(List<ScQtBsvDtl> briefings) {
		this.briefings = briefings;
	}

	public List<ScQtBsvDtl> getSiteBriefings() {
		return siteBriefings;
	}

	public void setSiteBriefings(List<ScQtBsvDtl> siteBriefings) {
		this.siteBriefings = siteBriefings;
	}

	public List<ScQtDoc> getScQtDocs() {
		return scQtDocs;
	}

	public void setScQtDocs(List<ScQtDoc> scQtDocs) {
		this.scQtDocs = scQtDocs;
	}

	public List<CloneAll> getCloneAllList() {
		return cloneAllList;
	}

	public void setCloneAllList(List<CloneAll> cloneAllList) {
		this.cloneAllList = cloneAllList;
	}

	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	public List<CtCircular> getCtCirculars() {
		return ctCirculars;
	}

	public void setCtCirculars(List<CtCircular> ctCirculars) {
		this.ctCirculars = ctCirculars;
	}

	public List<ScQtContactPerson> getScQtContactPersons() {
		return scQtContactPersons;
	}

	public void setScQtContactPersons(List<ScQtContactPerson> scQtContactPersons) {
		this.scQtContactPersons = scQtContactPersons;
	}

	public List<ScQtRemark> getScQtRemarks() {
		return scQtRemarks;
	}

	public void setScQtRemarks(List<ScQtRemark> scQtRemarks) {
		this.scQtRemarks = scQtRemarks;
	}

	public ScQtRemark getQtRemark() {
		return qtRemark;
	}

	public void setQtRemark(ScQtRemark qtRemark) {
		this.qtRemark = qtRemark;
	}

	public ScRemark getScRemark() {
		return scRemark;
	}

	public void setScRemark(ScRemark scRemark) {
		this.scRemark = scRemark;
	}

	public List<CtPanelSupplier> getCtPanelSuppliers() {
		return ctPanelSuppliers;
	}

	public void setCtPanelSuppliers(List<CtPanelSupplier> ctPanelSuppliers) {
		this.ctPanelSuppliers = ctPanelSuppliers;
	}

	public List<ScQtCheckList> getScQtChecklists() {
		return scQtChecklists;
	}

	public void setScQtChecklists(List<ScQtCheckList> scQtChecklists) {
		this.scQtChecklists = scQtChecklists;
	}

	public ScQtCheckList getQtCheckList() {
		return qtCheckList;
	}

	public void setQtCheckList(ScQtCheckList qtCheckList) {
		this.qtCheckList = qtCheckList;
	}

	public CloneAllDataModel getCloneAll() {
		return cloneAll;
	}

	public void setCloneAll(CloneAllDataModel cloneAll) {
		this.cloneAll = cloneAll;
	}

	public ScQtContactPerson getQtContactPerson() {
		return qtContactPerson;
	}

	public void setQtContactPerson(ScQtContactPerson qtContactPerson) {
		this.qtContactPerson = qtContactPerson;
	}

	public List<PmUser> getVeryfiers() {
		return veryfiers;
	}

	public void setVeryfiers(List<PmUser> veryfiers) {
		this.veryfiers = veryfiers;
	}

	public List<PmUser> getApprovers() {
		return approvers;
	}

	public void setApprovers(List<PmUser> approvers) {
		this.approvers = approvers;
	}

	public List<PmUser> getBbkApprovers() {
		return bbkApprovers;
	}

	public void setBbkApprovers(List<PmUser> bbkApprovers) {
		this.bbkApprovers = bbkApprovers;
	}

	public String getOutCome() {
		return outCome;
	}

	public void setOutCome(String outCome) {
		this.outCome = outCome;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Object getQuotationTenderDataType() {
		return quotationTenderDataType;
	}

	public void setQuotationTenderDataType(Object quotationTenderDataType) {
		this.quotationTenderDataType = quotationTenderDataType;
	}

	public List<PmCountry> getCountries() {
		return countries;
	}

	public void setCountries(List<PmCountry> countries) {
		this.countries = countries;
	}

	public CloneAll[] getCloneAllData() {
		return CloneAll.values();
	}

	public PpPlan getPpPlan() {
		return ppPlan;
	}

	public void setPpPlan(PpPlan ppPlan) {
		this.ppPlan = ppPlan;
	}

	public PmOrgProfile getPmOrgProfile() {
		return pmOrgProfile;
	}

	public void setPmOrgProfile(PmOrgProfile pmOrgProfile) {
		this.pmOrgProfile = pmOrgProfile;
	}

	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	public String getUploadUpdateId() {
		return uploadUpdateId;
	}

	public void setUploadUpdateId(String uploadUpdateId) {
		this.uploadUpdateId = uploadUpdateId;
	}

	public List<ScQtBsvDtl> getSelectedBriefingDetails() {
		return selectedBriefingDetails;
	}

	public void setSelectedBriefingDetails(
			List<ScQtBsvDtl> selectedBriefingDetails) {
		this.selectedBriefingDetails = selectedBriefingDetails;
	}

	public List<ScQtBsvDtl> getSelectedSiteVisitDetails() {
		return selectedSiteVisitDetails;
	}

	public void setSelectedSiteVisitDetails(
			List<ScQtBsvDtl> selectedSiteVisitDetails) {
		this.selectedSiteVisitDetails = selectedSiteVisitDetails;
	}

	public List<ScQtContactPerson> getSelectedContactPersons() {
		return selectedContactPersons;
	}

	public void setSelectedContactPersons(
			List<ScQtContactPerson> selectedContactPersons) {
		this.selectedContactPersons = selectedContactPersons;
	}

	public List<ScQtCheckList> getSelectedCheckLists() {
		return selectedCheckLists;
	}

	public void setSelectedCheckLists(List<ScQtCheckList> selectedCheckLists) {
		this.selectedCheckLists = selectedCheckLists;
	}

	public List<ScQtBudget> getSelectedGosodoBudgets() {
		return selectedGosodoBudgets;
	}

	public void setSelectedGosodoBudgets(List<ScQtBudget> selectedGosodoBudgets) {
		this.selectedGosodoBudgets = selectedGosodoBudgets;
	}

	public List<ScQt> getSelectedCloneTenderList() {
		return selectedCloneTenderList;
	}

	public void setSelectedCloneTenderList(List<ScQt> selectedCloneTenderList) {
		this.selectedCloneTenderList = selectedCloneTenderList;
	}

	public List<SmMofAccount> getSelectedpanelsupplier() {
		return selectedpanelsupplier;
	}

	public void setSelectedpanelsupplier(
			List<SmMofAccount> selectedpanelsupplier) {
		this.selectedpanelsupplier = selectedpanelsupplier;
	}

	public List<CtPanelSupplier> getPanelSupplierList() {
		return panelSupplierList;
	}

	public void setPanelSupplierList(List<CtPanelSupplier> panelSupplierList) {
		this.panelSupplierList = panelSupplierList;
	}

	public List<CloneAll> getSelectedCloneAllList() {
		return selectedCloneAllList;
	}

	public void setSelectedCloneAllList(List<CloneAll> selectedCloneAllList) {
		this.selectedCloneAllList = selectedCloneAllList;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	public void setScQtDeductionList(List<ScQtDeduction> scQtDeductionList) {
		this.scQtDeductionList = scQtDeductionList;
	}

	public List<ScQtDeduction> getSelectedScQtDeductions() {
		return selectedScQtDeductions;
	}

	public void setSelectedScQtDeductions(
			List<ScQtDeduction> selectedScQtDeductions) {
		this.selectedScQtDeductions = selectedScQtDeductions;
	}

	public List<ScQtDeductionClause> getScQtDeductionClauses() {
		return scQtDeductionClauses;
	}

	public void setScQtDeductionClauses(
			List<ScQtDeductionClause> scQtDeductionClauses) {
		this.scQtDeductionClauses = scQtDeductionClauses;
	}

	public List<ScQtDeductionClause> getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	public void setSelectedDeductionClauses(
			List<ScQtDeductionClause> selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	public ScQtDeduction getQtDeduction() {
		return qtDeduction;
	}

	public void setQtDeduction(ScQtDeduction qtDeduction) {
		this.qtDeduction = qtDeduction;
	}

	public ScQtDeductionClause getQtDeductionClause() {
		return qtDeductionClause;
	}

	public void setQtDeductionClause(ScQtDeductionClause qtDeductionClause) {
		this.qtDeductionClause = qtDeductionClause;
	}

	public String getCircularNo() {
		return circularNo;
	}

	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	public String getCircularName() {
		return circularName;
	}

	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getCircularCoverage() {
		return circularCoverage;
	}

	public void setCircularCoverage(String circularCoverage) {
		this.circularCoverage = circularCoverage;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getMofAccountNo() {
		return mofAccountNo;
	}

	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	public BigDecimal getMinPaidUpCapital() {
		return minPaidUpCapital;
	}

	public void setMinPaidUpCapital(BigDecimal minPaidUpCapital) {
		this.minPaidUpCapital = minPaidUpCapital;
	}

	public int getTotalSearchSupplier() {
		return totalSearchSupplier;
	}

	public void setTotalSearchSupplier(int totalSearchSupplier) {
		this.totalSearchSupplier = totalSearchSupplier;
	}

	public int getTotalSupplier() {
		return totalSupplier;
	}

	public void setTotalSupplier(int totalSupplier) {
		this.totalSupplier = totalSupplier;
	}

	public TreeNode getSearchPanelSuppliersNode() {
		return searchPanelSuppliersNode;
	}

	public void setSearchPanelSuppliersNode(TreeNode searchPanelSuppliersNode) {
		this.searchPanelSuppliersNode = searchPanelSuppliersNode;
	}

	public TreeNode getSuppliersNode() {
		return suppliersNode;
	}

	public void setSuppliersNode(TreeNode suppliersNode) {
		this.suppliersNode = suppliersNode;
	}

	public List<SmSupplier> getSearchSupplierList() {
		return searchSupplierList;
	}

	public void setSearchSupplierList(List<SmSupplier> searchSupplierList) {
		this.searchSupplierList = searchSupplierList;
	}

	public List<SmSupplier> getSelectedSupplierList() {
		return selectedSupplierList;
	}

	public void setSelectedSupplierList(List<SmSupplier> selectedSupplierList) {
		this.selectedSupplierList = selectedSupplierList;
	}

	public List<ScQtSupplier> getQtSupplierList() {
		return qtSupplierList;
	}

	public void setQtSupplierList(List<ScQtSupplier> qtSupplierList) {
		this.qtSupplierList = qtSupplierList;
	}

	public List<ScQtCategory> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	public void setSelectedCategoryList(List<ScQtCategory> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	public List<ScQtCategory> getQtCategoryList() {
		return qtCategoryList;
	}

	public void setQtCategoryList(List<ScQtCategory> qtCategoryList) {
		this.qtCategoryList = qtCategoryList;
	}

	public ScQtDeduction getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	public void setSelectedScQtDeduction(ScQtDeduction selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	public void setSearchQTDataModel(IPagingDataModel<ScQt> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
	}

	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}

	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}

	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	public TreeNode[] getSelectedAddrNodes() {
		return selectedAddrNodes;
	}

	public void setSelectedAddrNodes(TreeNode[] selectedAddrNodes) {
		this.selectedAddrNodes = selectedAddrNodes;
	}

	public List<PmUser> getDeskOfficerAdmin() {
		return deskOfficerAdmin;
	}

	public void setDeskOfficerAdmin(List<PmUser> deskOfficerAdmin) {
		this.deskOfficerAdmin = deskOfficerAdmin;
	}

	public DualListModel<PmUser> getPtjApprovalUsers() {
		return ptjApprovalUsers;
	}

	public void setPtjApprovalUsers(DualListModel<PmUser> ptjApprovalUsers) {
		this.ptjApprovalUsers = ptjApprovalUsers;
	}

	public boolean isDurationField() {
		return durationField;
	}

	public void setDurationField(boolean durationField) {
		this.durationField = durationField;
	}

	public List<ScCommitteeMember> getCommittemembers() {
		return committemembers;
	}

	public void setCommittemembers(List<ScCommitteeMember> committemembers) {
		this.committemembers = committemembers;
	}

	public List<PmParameterDesc> getRoleTypes() {
		return roleTypes;
	}

	public void setRoleTypes(List<PmParameterDesc> roleTypes) {
		this.roleTypes = roleTypes;
	}

	public TreeNode getSearchcommitteeNode() {
		return searchcommitteeNode;
	}

	public void setSearchcommitteeNode(TreeNode searchcommitteeNode) {
		this.searchcommitteeNode = searchcommitteeNode;
	}

	public TreeNode[] getSelectSearchcommitteeNode() {
		return selectSearchcommitteeNode;
	}

	public void setSelectSearchcommitteeNode(
			TreeNode[] selectSearchcommitteeNode) {
		this.selectSearchcommitteeNode = selectSearchcommitteeNode;
	}

	public String getCommitteNo() {
		return committeNo;
	}

	public void setCommitteNo(String committeNo) {
		this.committeNo = committeNo;
	}

	public Date getCreatedDt() {
		return createdDt;
	}

	public void setCreatedDt(Date createdDt) {
		this.createdDt = createdDt;
	}

	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}

	public List<TabBean> getScCommitteeMemberTabList() {
		return scCommitteeMemberTabList;
	}

	public void setScCommitteeMemberTabList(
			List<TabBean> scCommitteeMemberTabList) {
		this.scCommitteeMemberTabList = scCommitteeMemberTabList;
	}

	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}

	public String getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	public List<ScQt> getCloneTenderList() {
		return cloneTenderList;
	}

	public void setCloneTenderList(List<ScQt> cloneTenderList) {
		this.cloneTenderList = cloneTenderList;
	}

	public ScQt getSelectedCloneTender() {
		return selectedCloneTender;
	}

	public void setSelectedCloneTender(ScQt selectedCloneTender) {
		this.selectedCloneTender = selectedCloneTender;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<TreeNode> getZonesList() {
		return zonesList;
	}

	public void setZonesList(List<TreeNode> zonesList) {
		this.zonesList = zonesList;
	}

	public List<TreeNode> getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	public void setCtZoneSuppliers(List<TreeNode> ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

	public List<ScQt> getCloneBusinessTenderList() {
		return cloneBusinessTenderList;
	}

	public void setCloneBusinessTenderList(List<ScQt> cloneBusinessTenderList) {
		this.cloneBusinessTenderList = cloneBusinessTenderList;
	}

	public ScQt getSelectedCloneBusinessTender() {
		return selectedCloneBusinessTender;
	}

	public void setSelectedCloneBusinessTender(ScQt selectedCloneBusinessTender) {
		this.selectedCloneBusinessTender = selectedCloneBusinessTender;
	}

	public ScQtSupplier getScQtSupplier() {
		return scQtSupplier;
	}

	public void setScQtSupplier(ScQtSupplier scQtSupplier) {
		this.scQtSupplier = scQtSupplier;
	}

	public String getBriefingZoneName() {
		return briefingZoneName;
	}

	public void setBriefingZoneName(String briefingZoneName) {
		this.briefingZoneName = briefingZoneName;
	}

	public ScQtSupplierCriteria getSupplierCriteria() {
		return supplierCriteria;
	}

	public void setSupplierCriteria(ScQtSupplierCriteria supplierCriteria) {
		this.supplierCriteria = supplierCriteria;
	}

	public List<TreeNode> getZoneGeneralTrees() {
		return zoneGeneralTrees;
	}

	public void setZoneGeneralTrees(List<TreeNode> zoneGeneralTrees) {
		this.zoneGeneralTrees = zoneGeneralTrees;
	}

	public TreeNode[] getSelectedGeneralTreeNodes() {
		return selectedGeneralTreeNodes;
	}

	public void setSelectedGeneralTreeNodes(TreeNode[] selectedGeneralTreeNodes) {
		this.selectedGeneralTreeNodes = selectedGeneralTreeNodes;
	}

	public Date getProposalValidityUntil() {
		return proposalValidityUntil;
	}

	public void setProposalValidityUntil(Date proposalValidityUntil) {
		this.proposalValidityUntil = proposalValidityUntil;
	}

	public Date getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(Date closingDate) {
		this.closingDate = closingDate;
	}

	public List<ScQt> getDocumentCloneTenderList() {
		return documentCloneTenderList;
	}

	public void setDocumentCloneTenderList(List<ScQt> documentCloneTenderList) {
		this.documentCloneTenderList = documentCloneTenderList;
	}

	public ScQt getSelectedDocumentCloneTender() {
		return selectedDocumentCloneTender;
	}

	public void setSelectedDocumentCloneTender(ScQt selectedDocumentCloneTender) {
		this.selectedDocumentCloneTender = selectedDocumentCloneTender;
	}

	public List<ScQtCheckList> getQtCheckLists() {
		return qtCheckLists;
	}

	public void setQtCheckLists(List<ScQtCheckList> qtCheckLists) {
		this.qtCheckLists = qtCheckLists;
	}

	public List<ScQtCheckList> getSelectedQtCheckLists() {
		return selectedQtCheckLists;
	}

	public void setSelectedQtCheckLists(List<ScQtCheckList> selectedQtCheckLists) {
		this.selectedQtCheckLists = selectedQtCheckLists;
	}

	public String getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	public List<PmUser> getPmUsersList() {
		return pmUsersList;
	}

	public void setPmUsersList(List<PmUser> pmUsersList) {
		this.pmUsersList = pmUsersList;
	}

	public List<PmUser> getSelectedPmUsersList() {
		return selectedPmUsersList;
	}

	public void setSelectedPmUsersList(List<PmUser> selectedPmUsersList) {
		this.selectedPmUsersList = selectedPmUsersList;
	}

	public PmUser getPmUser() {
		return pmUser;
	}

	public void setPmUser(PmUser pmUser) {
		this.pmUser = pmUser;
	}

	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	public List<ScRemark> getRemarks() {
		return remarks;
	}

	public void setRemarks(List<ScRemark> remarks) {
		this.remarks = remarks;
	}

	public TplSpecZoneDtl getSelectedZone() {
		return selectedZone;
	}

	public void setSelectedZone(TplSpecZoneDtl selectedZone) {
		this.selectedZone = selectedZone;
	}

	public CtContract getCtContract() {
		return ctContract;
	}

	public void setCtContract(CtContract ctContract) {
		this.ctContract = ctContract;
	}

	public List<CtPanelItem> getSelectpanelItem() {
		return selectpanelItem;
	}

	public void setSelectpanelItem(List<CtPanelItem> selectpanelItem) {
		this.selectpanelItem = selectpanelItem;
	}

	public ScQtSampleSubmission getScQtSampleSubmission() {
		return scQtSampleSubmission;
	}

	public void setScQtSampleSubmission(
			ScQtSampleSubmission scQtSampleSubmission) {
		this.scQtSampleSubmission = scQtSampleSubmission;
	}

	public List<ScQtBsvRegistration> getBriefingRegistrationList() {
		return briefingRegistrationList;
	}

	public void setBriefingRegistrationList(
			List<ScQtBsvRegistration> briefingRegistrationList) {
		this.briefingRegistrationList = briefingRegistrationList;
	}

	public ScQtBsvRegistration getSelectedbriefRegistration() {
		return selectedbriefRegistration;
	}

	public void setSelectedbriefRegistration(
			ScQtBsvRegistration selectedbriefRegistration) {
		this.selectedbriefRegistration = selectedbriefRegistration;
	}

	public ScQtBsvRegistration getScQtBsvRegistration() {
		return scQtBsvRegistration;
	}

	public void setScQtBsvRegistration(ScQtBsvRegistration scQtBsvRegistration) {
		this.scQtBsvRegistration = scQtBsvRegistration;
	}

	public ScQtBsv getScQtBsv() {
		return scQtBsv;
	}

	public void setScQtBsv(ScQtBsv scQtBsv) {
		this.scQtBsv = scQtBsv;
	}

	public ScQtBsv getScQtBriefing() {
		return scQtBriefing;
	}

	public void setScQtBriefing(ScQtBsv scQtBriefing) {
		this.scQtBriefing = scQtBriefing;
	}

	public ScQtBsv getScQtSitevisit() {
		return scQtSitevisit;
	}

	public void setScQtSitevisit(ScQtBsv scQtSitevisit) {
		this.scQtSitevisit = scQtSitevisit;
	}

	public List<ScQtSampleSubmission> getScQtSampleSubmissionList() {
		return scQtSampleSubmissionList;
	}

	public void setScQtSampleSubmissionList(
			List<ScQtSampleSubmission> scQtSampleSubmissionList) {
		this.scQtSampleSubmissionList = scQtSampleSubmissionList;
	}

	public List<ScQtSampleSubmission> getSelectedSubmissionList() {
		return selectedSubmissionList;
	}

	public void setSelectedSubmissionList(
			List<ScQtSampleSubmission> selectedSubmissionList) {
		this.selectedSubmissionList = selectedSubmissionList;
	}

	public List<ScQtContactPerson> getPmuserContactpersonList() {
		return pmuserContactpersonList;
	}

	public void setPmuserContactpersonList(
			List<ScQtContactPerson> pmuserContactpersonList) {
		this.pmuserContactpersonList = pmuserContactpersonList;
	}

	public List<ScQtContactPerson> getSelectedpmuserContactpersonList() {
		return selectedpmuserContactpersonList;
	}

	public void setSelectedpmuserContactpersonList(
			List<ScQtContactPerson> selectedpmuserContactpersonList) {
		this.selectedpmuserContactpersonList = selectedpmuserContactpersonList;
	}

	public List<PmCity> getCitys() {
		return citys;
	}

	public void setCitys(List<PmCity> citys) {
		this.citys = citys;
	}

	public List<ScQtCategory> getSuppCriteriarestrictedList() {
		return suppCriteriarestrictedList;
	}

	public void setSuppCriteriarestrictedList(
			List<ScQtCategory> suppCriteriarestrictedList) {
		this.suppCriteriarestrictedList = suppCriteriarestrictedList;
	}

	public ScQtCheckList getScQtChecklist() {
		return scQtChecklist;
	}

	public void setScQtChecklist(ScQtCheckList scQtChecklist) {
		this.scQtChecklist = scQtChecklist;
	}

	public List<ScQtApprovalRequest> getScQtApprovalRequestList() {
		return scQtApprovalRequestList;
	}

	public void setScQtApprovalRequestList(
			List<ScQtApprovalRequest> scQtApprovalRequestList) {
		this.scQtApprovalRequestList = scQtApprovalRequestList;
	}

	public List<ScQtCategory> getSelectedRestrictedCategoryList() {
		return selectedRestrictedCategoryList;
	}

	public void setSelectedRestrictedCategoryList(
			List<ScQtCategory> selectedRestrictedCategoryList) {
		this.selectedRestrictedCategoryList = selectedRestrictedCategoryList;
	}

	public String getApprovalType() {
		return approvalType;
	}

	public void setApprovalType(String approvalType) {
		this.approvalType = approvalType;
	}

	public ISearchResultBean<ScQtCommittee> getQtCommittes() {
		return qtCommittes;
	}

	public void setQtCommittes(ISearchResultBean<ScQtCommittee> qtCommittes) {
		this.qtCommittes = qtCommittes;
	}

	public ScQtBSVRemark getScQtBSVRemark() {
		return scQtBSVRemark;
	}

	public void setScQtBSVRemark(ScQtBSVRemark scQtBSVRemark) {
		this.scQtBSVRemark = scQtBSVRemark;
	}

	public List<ScQtCheckList> getTechnicalSpecList() {
		return technicalSpecList;
	}

	public void setTechnicalSpecList(List<ScQtCheckList> technicalSpecList) {
		this.technicalSpecList = technicalSpecList;
	}

	public List<ScQtCheckList> getFinancialSpecList() {
		return financialSpecList;
	}

	public void setFinancialSpecList(List<ScQtCheckList> financialSpecList) {
		this.financialSpecList = financialSpecList;
	}

	public List<ScQtBsvAttendance> getScQtBsvAttendanceList() {
		return scQtBsvAttendanceList;
	}

	public void setScQtBsvAttendanceList(
			List<ScQtBsvAttendance> scQtBsvAttendanceList) {
		this.scQtBsvAttendanceList = scQtBsvAttendanceList;
	}

	public PmAddress getPmAddress() {
		return pmAddress;
	}

	public void setPmAddress(PmAddress pmAddress) {
		this.pmAddress = pmAddress;
	}

	public String getSupplierBranch() {
		return supplierBranch;
	}

	public void setSupplierBranch(String supplierBranch) {
		this.supplierBranch = supplierBranch;
	}

	public String getSupplierContact() {
		return supplierContact;
	}

	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	public String getSupplierEmail() {
		return supplierEmail;
	}

	public void setSupplierEmail(String supplierEmail) {
		this.supplierEmail = supplierEmail;
	}

	public SmAddress getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(SmAddress supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	public List<SmPersonnel> getSmPersonnelsList() {
		return smPersonnelsList;
	}

	public void setSmPersonnelsList(List<SmPersonnel> smPersonnelsList) {
		this.smPersonnelsList = smPersonnelsList;
	}

	public SmPersonnel getPersonnel() {
		return personnel;
	}

	public void setPersonnel(SmPersonnel personnel) {
		this.personnel = personnel;
	}

	public Date getCircularExpDate() {
		return circularExpDate;
	}

	public void setCircularExpDate(Date circularExpDate) {
		this.circularExpDate = circularExpDate;
	}

	public TreeNode getSupplierSelectedTreeNode() {
		return supplierSelectedTreeNode;
	}

	public void setSupplierSelectedTreeNode(TreeNode supplierSelectedTreeNode) {
		this.supplierSelectedTreeNode = supplierSelectedTreeNode;
	}

	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public List<ScQtBsvAttendance> getBriefingAttendanceList() {
		return briefingAttendanceList;
	}

	public void setBriefingAttendanceList(
			List<ScQtBsvAttendance> briefingAttendanceList) {
		this.briefingAttendanceList = briefingAttendanceList;
	}

	public ScQtBsvAttendance getScQtBriefingAttendance() {
		return scQtBriefingAttendance;
	}

	public void setScQtBriefingAttendance(
			ScQtBsvAttendance scQtBriefingAttendance) {
		this.scQtBriefingAttendance = scQtBriefingAttendance;
	}

	public List<PmParameter> getFinalizationSecratary() {
		return finalizationSecratary;
	}

	public void setFinalizationSecratary(List<PmParameter> finalizationSecratary) {
		this.finalizationSecratary = finalizationSecratary;
	}

	public IPagingDataModel<PmUser> getUserSearchCommitteeDataModel() {
		return userSearchCommitteeDataModel;
	}

	public void setUserSearchCommitteeDataModel(
			IPagingDataModel<PmUser> userSearchCommitteeDataModel) {
		this.userSearchCommitteeDataModel = userSearchCommitteeDataModel;
	}

	public UserCommitteeSearchBean getUserCommitteeSearchBean() {
		return userCommitteeSearchBean;
	}

	public void setUserCommitteeSearchBean(
			UserCommitteeSearchBean userCommitteeSearchBean) {
		this.userCommitteeSearchBean = userCommitteeSearchBean;
	}

	public int getAllStateCode() {
		return allStateCode;
	}

	public void setAllStateCode(int allStateCode) {
		this.allStateCode = allStateCode;
	}

	public List<PmDocument> getPmDocumentList() {
		return pmDocumentList;
	}

	public void setPmDocumentList(List<PmDocument> pmDocumentList) {
		this.pmDocumentList = pmDocumentList;
	}

	public List<PmDocument> getSelectedPmDocumentList() {
		return selectedPmDocumentList;
	}

	public void setSelectedPmDocumentList(
			List<PmDocument> selectedPmDocumentList) {
		this.selectedPmDocumentList = selectedPmDocumentList;
	}

	public IPagingDataModel<ScQt> getCloneBusinessOwnerDataModel() {
		return cloneBusinessOwnerDataModel;
	}

	public void setCloneBusinessOwnerDataModel(
			IPagingDataModel<ScQt> cloneBusinessOwnerDataModel) {
		this.cloneBusinessOwnerDataModel = cloneBusinessOwnerDataModel;
	}

	public IPagingDataModel<PmUser> getUserSearchBusinessOwnerDataModel() {
		return userSearchBusinessOwnerDataModel;
	}

	public void setUserSearchBusinessOwnerDataModel(
			IPagingDataModel<PmUser> userSearchBusinessOwnerDataModel) {
		this.userSearchBusinessOwnerDataModel = userSearchBusinessOwnerDataModel;
	}

	public UserCommitteeSearchBean getUserBusineeOwnerSearchBean() {
		return userBusineeOwnerSearchBean;
	}

	public void setUserBusineeOwnerSearchBean(
			UserCommitteeSearchBean userBusineeOwnerSearchBean) {
		this.userBusineeOwnerSearchBean = userBusineeOwnerSearchBean;
	}

	public QtnTenderSearchBean getCloneBusinessOwnerSearchBean() {
		return cloneBusinessOwnerSearchBean;
	}

	public void setCloneBusinessOwnerSearchBean(
			QtnTenderSearchBean cloneBusinessOwnerSearchBean) {
		this.cloneBusinessOwnerSearchBean = cloneBusinessOwnerSearchBean;
	}

	public List<ScQt> getSelectedCloneBOList() {
		return selectedCloneBOList;
	}

	public void setSelectedCloneBOList(List<ScQt> selectedCloneBOList) {
		this.selectedCloneBOList = selectedCloneBOList;
	}

	public MenuModel getMenuModel() {
		return menuModel;
	}

	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public TabBean getSelectedTabBean() {
		return selectedTabBean;
	}

	public void setSelectedTabBean(TabBean selectedTabBean) {
		this.selectedTabBean = selectedTabBean;
	}

	public MenuModel getApprovalMenuModel() {
		return approvalMenuModel;
	}

	public void setApprovalMenuModel(MenuModel approvalMenuModel) {
		this.approvalMenuModel = approvalMenuModel;
	}

	public int getSelectedApprovalTabIndex() {
		return selectedApprovalTabIndex;
	}

	public void setSelectedApprovalTabIndex(int setSelectedApprovalTabIndex) {
		this.selectedApprovalTabIndex = setSelectedApprovalTabIndex;
	}

	public TabBean getSelectedApprovalTabBean() {
		return selectedApprovalTabBean;
	}

	public void setSelectedApprovalTabBean(TabBean selectedApprovalTabBean) {
		this.selectedApprovalTabBean = selectedApprovalTabBean;
	}

	public List<ScQtDocReport> getScQtDocReportList() {
		return scQtDocReportList;
	}

	public void setScQtDocReportList(List<ScQtDocReport> scQtDocReportList) {
		this.scQtDocReportList = scQtDocReportList;
	}

	public List<ScQtDocReport> getSelectedScQtDocReports() {
		return selectedScQtDocReports;
	}

	public void setSelectedScQtDocReports(
			List<ScQtDocReport> selectedScQtDocReports) {
		this.selectedScQtDocReports = selectedScQtDocReports;
	}

	public ScQtDocReport getSelectedScQtDocReport() {
		return selectedScQtDocReport;
	}

	public void setSelectedScQtDocReport(ScQtDocReport selectedScQtDocReport) {
		this.selectedScQtDocReport = selectedScQtDocReport;
	}

	public ScQtDocReport getScQtDocReport() {
		return scQtDocReport;
	}

	public void setScQtDocReport(ScQtDocReport scQtDocReport) {
		this.scQtDocReport = scQtDocReport;
	}

	public TplDocMaster getTplDocMaster() {
		return tplDocMaster;
	}

	public void setTplDocMaster(TplDocMaster tplDocMaster) {
		this.tplDocMaster = tplDocMaster;
	}

	public IPagingDataModel<ScQt> getCloneDocumentDataModel() {
		return cloneDocumentDataModel;
	}

	public void setCloneDocumentDataModel(
			IPagingDataModel<ScQt> cloneDocumentDataModel) {
		this.cloneDocumentDataModel = cloneDocumentDataModel;
	}

	public QtnTenderSearchBean getCloneDocumentSearchBean() {
		return cloneDocumentSearchBean;
	}

	public void setCloneDocumentSearchBean(
			QtnTenderSearchBean cloneDocumentSearchBean) {
		this.cloneDocumentSearchBean = cloneDocumentSearchBean;
	}

	public List<ScQtDocReport> getCloneScQtDocReports() {
		return cloneScQtDocReports;
	}

	public void setCloneScQtDocReports(List<ScQtDocReport> cloneScQtDocReports) {
		this.cloneScQtDocReports = cloneScQtDocReports;
	}

	public List<ScQtDocReport> getSelectedCloneScQtDocReports() {
		return selectedCloneScQtDocReports;
	}

	public void setSelectedCloneScQtDocReports(
			List<ScQtDocReport> selectedCloneScQtDocReports) {
		this.selectedCloneScQtDocReports = selectedCloneScQtDocReports;
	}

	public DualListModel<ScQtApproverSettings> getPublicationApprovals() {
		return publicationApprovals;
	}

	public void setPublicationApprovals(
			DualListModel<ScQtApproverSettings> publicationApprovals) {
		this.publicationApprovals = publicationApprovals;
	}

	public ScQtApproval getPublicationApproval() {
		return publicationApproval;
	}

	public void setPublicationApproval(ScQtApproval publicationApproval) {
		this.publicationApproval = publicationApproval;
	}

	public List<ScQtCommitteRemarks> getQtCommitteeRemarks() {
		return qtCommitteeRemarks;
	}

	public void setQtCommitteeRemarks(
			List<ScQtCommitteRemarks> qtCommitteeRemarks) {
		this.qtCommitteeRemarks = qtCommitteeRemarks;
	}

}
