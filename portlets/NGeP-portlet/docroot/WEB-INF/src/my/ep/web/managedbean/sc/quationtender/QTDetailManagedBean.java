package my.ep.web.managedbean.sc.quationtender;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmReportSection;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckListAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDeductionClause;
import my.ep.persistence.entity.sc.quationtender.ScQtDeliveryLoc;
import my.ep.persistence.entity.sc.quationtender.ScQtDoc;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScRemark;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.sc.quatationtender.CloneAllDataModel;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.sc.quationtender.CloneAll;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
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

	private ScQtBriefing qtBriefing;
	private ScQtBriefing qtSiteBriefing;
	private ScQtDoc qtDoc;
	private ScQtRemark qtRemark;
	private SmMofAccount smMofAccount;
	private ScRemark scRemark;
	private ScQtCheckList qtCheckList;
	private ScQtContactPerson qtContactPerson;
	private ScQtDeduction qtDeduction;
	private ScQtDeductionClause qtDeductionClause;
	private PpPlan ppPlan;
	private PmOrgProfile pmOrgProfile;

	private String selectedBoolean1;
	private String multiLoc;
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

	public List<ScQtBudget> gosodos;
	public List<ScQtBriefing> briefings;
	public List<ScQtBriefing> siteBriefings;
	public List<ScQtDoc> scQtDocs;
	public List<CloneAll> cloneAllList;
	private List<ScQtContactPerson> scQtContactPersons;
	private List<CtCircular> ctCirculars;
	private List<ScQtRemark> scQtRemarks;
	private List<ScQtCheckList> scQtChecklists;
	private List<ScQtDeduction> scQtDeductionList;
	private List<ScQtDeductionClause> scQtDeductionClauses;
	private List<CtPanelSupplier> panelSupplierList;

	private List<ScQtBriefing> selectedBriefingDetails;
	private List<ScQtBriefing> selectedSiteVisitDetails;
	private List<ScQtContactPerson> selectedContactPersons;
	private List<ScQtCheckList> selectedCheckLists;
	private List<ScQtBudget> selectedGosodoBudgets;
	private List<SmMofAccount> selectedpanelsupplier;
	private List<ScQt> selectedCloneTenderList;
	private List<CloneAll> selectedCloneAllList;
	private List<ScQtDeduction> selectedScQtDeductions;
	private List<ScQtDeduction> selectedScQtDeduction;
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
	private TreeNode ctZoneSuppliers;

	private List<CtPanelSupplier> ctZoneSupplierList;

	private List<ScQt> cloneTenderList = new ArrayList<ScQt>();
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
	private SCQuotationTenderDataType quotationTenderDataType;
	private boolean isEditable = true;

	private List<SmSupplier> panelSuppliers;

	// For Circular Panel Search Criteria
	private String circularNo;
	private String circularName;
	private String itemName;
	private String circularCoverage;

	// For Restricted Tender Search Criteria
	private String supplierName;
	private String mofAccountNo;

	private int totalSearchSupplier;
	private int totalSupplier;
	private TreeNode searchPanelSuppliersNode;
	private TreeNode suppliersNode;
	private List<SmSupplier> searchSupplierList;
	private List<SmSupplier> selectedSupplierList;
	private List<ScQtSupplier> qtSupplierList;
	private List<ScQtSupplier> selectedQtSupplierList;
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

	// Business Owner
	private List<ScCommitteeMember> scCommitteeMemberList;
	private List<ScCommitteeMember> selectScCommitteMember;
	private ScCommitteeMember committeMember;
	private List<SelectItem> selections;
	private List<PmUser> externalUsers;
	private List<PmUser> selectedPmUser;
	private List<ScQtCommittee> cloneSearchCommittes;
	private ScQtCommittee selectedCloneCommittee;
	private List<ScCommitteeMember> cloneCommitteMemebers;
	private List<ScCommitteeMember> selectedCommitteMemebers;
	private ScQtCommittee selectedScQtCommittee;

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

	private String panelItem = "Yes";
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

	private List<PmUser> deskOfficerAdmin;

	private List<TreeNode> treeNodesList;
	private ScQtCommittee scQtCommittee;

	private List<ScQtApprovalAttachment> attachments;
	private List<ScQtCheckListAttachment> checklistAttachment;

	private List<ScQtBriefing> test;

	private List<BriefingZoneBean> briefingZoneBean = new ArrayList<BriefingZoneBean>();

	private String ALPHA_NUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private String number;

	private int index = -1;

	// Committee Appointment
	private String tabTitle;
	private ScCommittee scCommittee;
	private List<ScCommitteeMember> committemembers;

	private String committeNo = null;
	private Date createdDt;
	private String selectedCommitte;
	private TreeNode searchcommitteeNode;
	private String evaluationType = "2";

	private ScQt selectedCloneTender;
	private boolean ecType;

	private SelectItem[] roleTypes; // Role type drop down

	private String roleName;

	private List<TabBean> scCommitteeMemberTabList;

	// Delivery Location
	private List<ScQtDeliveryLoc> deliveryLocList;
	private List<ScQtDeliveryLoc> selectedDeliveryLocList;

	public void setPanelSuppliers(List<SmSupplier> panelSuppliers) {
		this.panelSuppliers = panelSuppliers;
	}

	public QTDetailManagedBean() {
		log.info("inside ");

		qtnTenderSearchBean = new QtnTenderSearchBean();
		cloneQtnTenderSearchBean = new QtnTenderSearchBean();

		root2 = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("Clone All", root2);
		node0.setExpanded(true);

		CloneAll[] enumValues = CloneAll.values();
		for (CloneAll cloneValue : enumValues) {
			TreeNode node1 = new DefaultTreeNode(cloneValue.getDisplayName(),
					node0);
		}

		getBudgetApproverNames();
		getCommitteeApproverNames();
		getPTJApproverNames();

		/*
		 * briefings= new ArrayList<>(); ScQtBriefing briefing= new
		 * ScQtBriefing(); briefing.setBriefingDesc("test");
		 * briefings.add(briefing);
		 */
		briefingZoneBean.add(new BriefingZoneBean("South", briefings));
		briefingZoneBean.add(new BriefingZoneBean("North", briefings));
		briefingZoneBean.add(new BriefingZoneBean("East", briefings));
		briefingZoneBean.add(new BriefingZoneBean("West", briefings));
	}

	private void getCommitteeApproverNames() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(RoleCode.APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> availableApprover = userResDTO.getUserList();
		List<PmUser> selectedApprover = new ArrayList<PmUser>();
		committeeApprovalUsers = new DualListModel<PmUser>(availableApprover,
				selectedApprover);
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

	private void getBudgetApproverNames() {
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		// userReqDto.setRoleCode(RoleCode.BUDGET_APPROVER.value());
		UserResDTO userResDTO = iUserSO.retrieveUserListByRole(userReqDto);
		List<PmUser> availableApprover = userResDTO.getUserList();
		List<PmUser> selectedApprover = new ArrayList<PmUser>();
		budgetApprovalUsers = new DualListModel<PmUser>(availableApprover,
				selectedApprover);
	}

	public String getALPHA_NUM() {
		return ALPHA_NUM;
	}

	public void setALPHA_NUM(String aLPHA_NUM) {
		ALPHA_NUM = aLPHA_NUM;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
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

	public List<ScQtBriefing> getTest() {
		return test;
	}

	public void setTest(List<ScQtBriefing> test) {
		this.test = test;
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

	public String getPanelItem() {
		return panelItem;
	}

	public void setPanelItem(String panelItem) {
		this.panelItem = panelItem;
	}

	public ScQtCommittee getSelectedCloneCommittee() {
		return selectedCloneCommittee;
	}

	public void setSelectedCloneCommittee(ScQtCommittee selectedCloneCommittee) {
		this.selectedCloneCommittee = selectedCloneCommittee;
	}

	public List<ScCommitteeMember> getSelectScCommitteMember() {
		return selectScCommitteMember;
	}

	public void setSelectScCommitteMember(
			List<ScCommitteeMember> selectScCommitteMember) {
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

	/**
	 * @return the userMinistryList
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * @param userMinistryList
	 *            the userMinistryList to set
	 */
	public void setUserMinistryList(List<PmOrgProfile> userMinistryList) {
		this.userMinistryList = userMinistryList;
	}

	/**
	 * @return the userDepartmentList
	 */
	public List<PmOrgProfile> getUserDepartmentList() {
		return userDepartmentList;
	}

	/**
	 * @param userDepartmentList
	 *            the userDepartmentList to set
	 */
	public void setUserDepartmentList(List<PmOrgProfile> userDepartmentList) {
		this.userDepartmentList = userDepartmentList;
	}

	/**
	 * @return the userDivisionList
	 */
	public List<PmOrgProfile> getUserDivisionList() {
		return userDivisionList;
	}

	/**
	 * @param userDivisionList
	 *            the userDivisionList to set
	 */
	public void setUserDivisionList(List<PmOrgProfile> userDivisionList) {
		this.userDivisionList = userDivisionList;
	}

	/**
	 * @return the goList
	 */
	public List<PmGo> getGoList() {
		return goList;
	}

	/**
	 * @param goList
	 *            the goList to set
	 */
	public void setGoList(List<PmGo> goList) {
		this.goList = goList;
	}

	/**
	 * @return the soList
	 */
	public List<PmSo> getSoList() {
		return soList;
	}

	/**
	 * @param soList
	 *            the soList to set
	 */
	public void setSoList(List<PmSo> soList) {
		this.soList = soList;
	}

	/**
	 * @return the doList
	 */
	public List<PmDo> getDoList() {
		return doList;
	}

	/**
	 * @param doList
	 *            the doList to set
	 */
	public void setDoList(List<PmDo> doList) {
		this.doList = doList;
	}

	public ScQt getTenderCriteria() {
		return tenderCriteria;
	}

	public void setTenderCriteria(ScQt tenderCriteria) {
		this.tenderCriteria = tenderCriteria;
	}

	public String getMultiLoc() {
		return multiLoc;
	}

	public void setMultiLoc(String multiLoc) {
		this.multiLoc = multiLoc;
	}

	public String getSelectedBoolean1() {
		return selectedBoolean1;
	}

	public void setSelectedBoolean1(String selectedBoolean1) {
		this.selectedBoolean1 = selectedBoolean1;
	}

	/**
	 * @return the selectedUserMinistry
	 */
	public PmOrgProfile getSelectedUserMinistry() {
		return selectedUserMinistry;
	}

	/**
	 * @param selectedUserMinistry
	 *            the selectedUserMinistry to set
	 */
	public void setSelectedUserMinistry(PmOrgProfile selectedUserMinistry) {
		this.selectedUserMinistry = selectedUserMinistry;
	}

	/**
	 * @return the selectedUserDepartment
	 */
	public PmOrgProfile getSelectedUserDepartment() {
		return selectedUserDepartment;
	}

	/**
	 * @param selectedUserDepartment
	 *            the selectedUserDepartment to set
	 */
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

	public ScQtBriefing getQtBriefing() {
		return qtBriefing;
	}

	public void setQtBriefing(ScQtBriefing qtBriefing) {
		this.qtBriefing = qtBriefing;
	}

	public ScQtBriefing getQtSiteBriefing() {
		return qtSiteBriefing;
	}

	public void setQtSiteBriefing(ScQtBriefing qtSiteBriefing) {
		this.qtSiteBriefing = qtSiteBriefing;
	}

	public ScQtDoc getQtDoc() {
		return qtDoc;
	}

	public void setQtDoc(ScQtDoc qtDoc) {
		this.qtDoc = qtDoc;
	}

	public List<ScQtBriefing> getBriefings() {
		return briefings;
	}

	public void setBriefings(List<ScQtBriefing> briefings) {
		this.briefings = briefings;
	}

	public List<ScQtBriefing> getSiteBriefings() {
		return siteBriefings;
	}

	public void setSiteBriefings(List<ScQtBriefing> siteBriefings) {
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

	public SCQuotationTenderDataType getQuotationTenderDataType() {
		return quotationTenderDataType;
	}

	public void setQuotationTenderDataType(
			SCQuotationTenderDataType quotationTenderDataType) {
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

	/**
	 * @return the scQtApproval
	 */
	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	/**
	 * @param scQtApproval
	 *            the scQtApproval to set
	 */
	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	/**
	 * @return the uploadUpdateId
	 */
	public String getUploadUpdateId() {
		return uploadUpdateId;
	}

	/**
	 * @param uploadUpdateId
	 *            the uploadUpdateId to set
	 */
	public void setUploadUpdateId(String uploadUpdateId) {
		this.uploadUpdateId = uploadUpdateId;
	}

	public List<ScQtBriefing> getSelectedBriefingDetails() {
		return selectedBriefingDetails;
	}

	public void setSelectedBriefingDetails(
			List<ScQtBriefing> selectedBriefingDetails) {
		this.selectedBriefingDetails = selectedBriefingDetails;
	}

	public List<ScQtBriefing> getSelectedSiteVisitDetails() {
		return selectedSiteVisitDetails;
	}

	public void setSelectedSiteVisitDetails(
			List<ScQtBriefing> selectedSiteVisitDetails) {
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

	/**
	 * @return the scQtDeductionList
	 */
	public List<ScQtDeduction> getScQtDeductionList() {
		return scQtDeductionList;
	}

	/**
	 * @param scQtDeductionList
	 *            the scQtDeductionList to set
	 */
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

	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}

	/**
	 * @param circularNo
	 *            the circularNo to set
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
	 * @param circularName
	 *            the circularName to set
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName
	 *            the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the circularCoverage
	 */
	public String getCircularCoverage() {
		return circularCoverage;
	}

	/**
	 * @param circularCoverage
	 *            the circularCoverage to set
	 */
	public void setCircularCoverage(String circularCoverage) {
		this.circularCoverage = circularCoverage;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName
	 *            the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the mofAccountNo
	 */
	public String getMofAccountNo() {
		return mofAccountNo;
	}

	/**
	 * @param mofAccountNo
	 *            the mofAccountNo to set
	 */
	public void setMofAccountNo(String mofAccountNo) {
		this.mofAccountNo = mofAccountNo;
	}

	/**
	 * @return the totalSearchSupplier
	 */
	public int getTotalSearchSupplier() {
		return totalSearchSupplier;
	}

	/**
	 * @param totalSearchSupplier
	 *            the totalSearchSupplier to set
	 */
	public void setTotalSearchSupplier(int totalSearchSupplier) {
		this.totalSearchSupplier = totalSearchSupplier;
	}

	/**
	 * @return the totalSupplier
	 */
	public int getTotalSupplier() {
		return totalSupplier;
	}

	/**
	 * @param totalSupplier
	 *            the totalSupplier to set
	 */
	public void setTotalSupplier(int totalSupplier) {
		this.totalSupplier = totalSupplier;
	}

	/**
	 * @return the searchPanelSuppliersNode
	 */
	public TreeNode getSearchPanelSuppliersNode() {
		return searchPanelSuppliersNode;
	}

	/**
	 * @param searchPanelSuppliersNode
	 *            the searchPanelSuppliersNode to set
	 */
	public void setSearchPanelSuppliersNode(TreeNode searchPanelSuppliersNode) {
		this.searchPanelSuppliersNode = searchPanelSuppliersNode;
	}

	/**
	 * @return the suppliersNode
	 */
	public TreeNode getSuppliersNode() {
		return suppliersNode;
	}

	/**
	 * @param suppliersNode
	 *            the suppliersNode to set
	 */
	public void setSuppliersNode(TreeNode suppliersNode) {
		this.suppliersNode = suppliersNode;
	}

	/**
	 * @return the searchSupplierList
	 */
	public List<SmSupplier> getSearchSupplierList() {
		return searchSupplierList;
	}

	/**
	 * @param searchSupplierList
	 *            the searchSupplierList to set
	 */
	public void setSearchSupplierList(List<SmSupplier> searchSupplierList) {
		this.searchSupplierList = searchSupplierList;
	}

	/**
	 * @return the selectedSupplierList
	 */
	public List<SmSupplier> getSelectedSupplierList() {
		return selectedSupplierList;
	}

	/**
	 * @param selectedSupplierList
	 *            the selectedSupplierList to set
	 */
	public void setSelectedSupplierList(List<SmSupplier> selectedSupplierList) {
		this.selectedSupplierList = selectedSupplierList;
	}

	/**
	 * @return the qtSupplierList
	 */
	public List<ScQtSupplier> getQtSupplierList() {
		return qtSupplierList;
	}

	/**
	 * @param qtSupplierList
	 *            the qtSupplierList to set
	 */
	public void setQtSupplierList(List<ScQtSupplier> qtSupplierList) {
		this.qtSupplierList = qtSupplierList;
	}

	/**
	 * @return the selectedCategoryList
	 */
	public List<ScQtCategory> getSelectedCategoryList() {
		return selectedCategoryList;
	}

	/**
	 * @param selectedCategoryList
	 *            the selectedCategoryList to set
	 */
	public void setSelectedCategoryList(List<ScQtCategory> selectedCategoryList) {
		this.selectedCategoryList = selectedCategoryList;
	}

	/**
	 * @return the qtCategoryList
	 */
	public List<ScQtCategory> getQtCategoryList() {
		return qtCategoryList;
	}

	/**
	 * @param qtCategoryList
	 *            the qtCategoryList to set
	 */
	public void setQtCategoryList(List<ScQtCategory> qtCategoryList) {
		this.qtCategoryList = qtCategoryList;
	}

	/**
	 * @return the selectedScQtDeduction
	 */
	public List<ScQtDeduction> getSelectedScQtDeduction() {
		return selectedScQtDeduction;
	}

	/**
	 * @param selectedScQtDeduction
	 *            the selectedScQtDeduction to set
	 */
	public void setSelectedScQtDeduction(
			List<ScQtDeduction> selectedScQtDeduction) {
		this.selectedScQtDeduction = selectedScQtDeduction;
	}

	/**
	 * @return the searchQTDataModel
	 */
	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		return searchQTDataModel;
	}

	/**
	 * @param searchQTDataModel
	 *            the searchQTDataModel to set
	 */
	public void setSearchQTDataModel(IPagingDataModel<ScQt> searchQTDataModel) {
		this.searchQTDataModel = searchQTDataModel;
	}

	/**
	 * @return the cloneSearchQTDataModel
	 */
	public IPagingDataModel<ScQt> getCloneSearchQTDataModel() {
		return cloneSearchQTDataModel;
	}

	/**
	 * @param cloneSearchQTDataModel
	 *            the cloneSearchQTDataModel to set
	 */
	public void setCloneSearchQTDataModel(
			IPagingDataModel<ScQt> cloneSearchQTDataModel) {
		this.cloneSearchQTDataModel = cloneSearchQTDataModel;
	}

	/**
	 * @return the selectedLocNodes
	 */
	public TreeNode[] getSelectedLocNodes() {
		return selectedLocNodes;
	}

	/**
	 * @param selectedLocNodes
	 *            the selectedLocNodes to set
	 */
	public void setSelectedLocNodes(TreeNode[] selectedLocNodes) {
		this.selectedLocNodes = selectedLocNodes;
	}

	/**
	 * @return the selectedAddrNodes
	 */
	public TreeNode[] getSelectedAddrNodes() {
		return selectedAddrNodes;
	}

	/**
	 * @param selectedAddrNodes
	 *            the selectedAddrNodes to set
	 */
	public void setSelectedAddrNodes(TreeNode[] selectedAddrNodes) {
		this.selectedAddrNodes = selectedAddrNodes;
	}

	/**
	 * @return the deskOfficerAdmin
	 */
	public List<PmUser> getDeskOfficerAdmin() {
		return deskOfficerAdmin;
	}

	/**
	 * @param deskOfficerAdmin
	 *            the deskOfficerAdmin to set
	 */
	public void setDeskOfficerAdmin(List<PmUser> deskOfficerAdmin) {
		this.deskOfficerAdmin = deskOfficerAdmin;
	}

	/**
	 * @return the ptjApprovalUsers
	 */
	public DualListModel<PmUser> getPtjApprovalUsers() {
		return ptjApprovalUsers;
	}

	/**
	 * @param ptjApprovalUsers
	 *            the ptjApprovalUsers to set
	 */
	public void setPtjApprovalUsers(DualListModel<PmUser> ptjApprovalUsers) {
		this.ptjApprovalUsers = ptjApprovalUsers;
	}

	/**
	 * @return the deliveryLocList
	 */
	public List<ScQtDeliveryLoc> getDeliveryLocList() {
		return deliveryLocList;
	}

	/**
	 * @param deliveryLocList
	 *            the deliveryLocList to set
	 */
	public void setDeliveryLocList(List<ScQtDeliveryLoc> deliveryLocList) {
		this.deliveryLocList = deliveryLocList;
	}

	/**
	 * @return the selectedDeliveryLocList
	 */
	public List<ScQtDeliveryLoc> getSelectedDeliveryLocList() {
		return selectedDeliveryLocList;
	}

	/**
	 * @param selectedDeliveryLocList
	 *            the selectedDeliveryLocList to set
	 */
	public void setSelectedDeliveryLocList(
			List<ScQtDeliveryLoc> selectedDeliveryLocList) {
		this.selectedDeliveryLocList = selectedDeliveryLocList;
	}

	/**
	 * @return the durationField
	 */
	public boolean isDurationField() {
		return durationField;
	}

	/**
	 * @param durationField
	 *            the durationField to set
	 */
	public void setDurationField(boolean durationField) {
		this.durationField = durationField;
	}

	/**
	 * @return the committemembers
	 */
	public List<ScCommitteeMember> getCommittemembers() {
		return committemembers;
	}

	/**
	 * @param committemembers
	 *            the committemembers to set
	 */
	public void setCommittemembers(List<ScCommitteeMember> committemembers) {
		this.committemembers = committemembers;
	}

	/**
	 * @return the roleTypes
	 */
	public SelectItem[] getRoleTypes() {
		return roleTypes;
	}

	/**
	 * @param roleTypes
	 *            the roleTypes to set
	 */
	public void setRoleTypes(SelectItem[] roleTypes) {
		this.roleTypes = roleTypes;
	}

	/**
	 * @return the searchcommitteeNode
	 */
	public TreeNode getSearchcommitteeNode() {
		return searchcommitteeNode;
	}

	/**
	 * @param searchcommitteeNode
	 *            the searchcommitteeNode to set
	 */
	public void setSearchcommitteeNode(TreeNode searchcommitteeNode) {
		this.searchcommitteeNode = searchcommitteeNode;
	}

	/**
	 * @return the committeNo
	 */
	public String getCommitteNo() {
		return committeNo;
	}

	/**
	 * @param committeNo
	 *            the committeNo to set
	 */
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

	public String getEvaluationType() {
		return evaluationType;
	}

	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public boolean isEcType() {
		return ecType;
	}

	public void setEcType(boolean ecType) {
		this.ecType = ecType;
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

	public TreeNode getCtZoneSuppliers() {
		return ctZoneSuppliers;
	}

	public void setCtZoneSuppliers(TreeNode ctZoneSuppliers) {
		this.ctZoneSuppliers = ctZoneSuppliers;
	}

	public List<CtPanelSupplier> getCtZoneSupplierList() {
		return ctZoneSupplierList;
	}

	public void setCtZoneSupplierList(List<CtPanelSupplier> ctZoneSupplierList) {
		this.ctZoneSupplierList = ctZoneSupplierList;
	}

}
