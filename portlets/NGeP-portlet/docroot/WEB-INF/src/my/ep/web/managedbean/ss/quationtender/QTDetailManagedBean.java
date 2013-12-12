package my.ep.web.managedbean.ss.quationtender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
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
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCheckList;
import my.ep.persistence.entity.ss.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtDoc;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.persistence.entity.ss.quationtender.ScQtRemark;
import my.ep.persistence.entity.ss.quationtender.ScRemark;
import my.ep.web.backingbean.ss.quatationtender.CloneAllDataModel;
import my.ep.web.bean.ss.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.ss.quationtender.CloneAll;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.sun.org.apache.bcel.internal.generic.LUSHR;

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
	private ScQtCriteria qtCriteria;
	private ScQtBriefing qtBriefing;
	private ScQtCategory qtCategory;
	private CtCircular searchCircular;
	private CtPanelSupplier ctPanelSupplier;
	private CtPanelItem ctPanelItem;
	private ScQtBriefing qtSiteBriefing;
	private ScQtDoc qtDoc;
	private ScQtRemark qtRemark;
	private SmMofAccount smMofAccount;
	private ScRemark scRemark;
	private ScQtCheckList qtCheckList;
	private ScQtContactPerson qtContactPerson;
	private PpPlan ppPlan;
	private PmOrgProfile pmOrgProfile;

	
	private String selectedBoolean1;
	private String multiLoc;
	private List<ScQtCategory> openCategorys;
	private List<ScQtCategory> restrictedCategorys;
	private SmSupplier searchSupplier;
	private List<SmSupplier> smSupplierList;
	private List<SmMofAccount> smMofAccounts;
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

	public List<ScQtGosodoBudget> gosodos;
	public List<ScQtCategory> categorys;
	public List<ScQtBriefing> briefings;
	public List<ScQtBriefing> siteBriefings;
	public List<ScQtDoc> scQtDocs;
    public List<CloneAll> cloneAllList;
    private List<ScQtContactPerson> scQtContactPersons;
    private List<CtCircular> ctCirculars;
    private List<ScQtRemark> scQtRemarks;
    private List<ScQtCheckList> scQtChecklists;
    private List<CtPanelSupplier> panelSupplierList;
    
    private List<ScQtBriefing> selectedBriefingDetails; 
    private List<ScQtBriefing> selectedSiteVisitDetails; 
    private List<ScQtContactPerson> selectedContactPersons;
	private List<ScQtCheckList> selectedCheckLists;
	private List<ScQtGosodoBudget> selectedGosodoBudgets;
	private List<ScQtCategory> selectedopenCategories;
	private List<ScQtCategory> selectedrestrictedcCategories;
    private List<SmMofAccount> selectedrestircedsupplier;
    private List<SmMofAccount> selectedpanelsupplier;
    private List<ScQt> selectedCloneTenderList;
    private List<CloneAll> selectedCloneAllList;
    
	private String qtNumber;
	
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

	private List<ScQt> cloneTenderList = new ArrayList<ScQt>();
	private ScQt tenderCriteria = new ScQt();

	
	private Boolean isTender=false;
	private Boolean isPanel=false;
	private Boolean isResctricted=false;
	private List<TabBean> tabList ;	
	
	private String checkBoxBPK;
	private List<String> checkBoxBPK1;
	private String standardDocument;
	private List<String> approvalTypelist = new ArrayList<String>();
	private int activeIndex=0;
	private int subTabIndex=0;
	private CloneAllDataModel cloneAll;
	
	private List<PmUser> veryfiers;
	private List<PmUser> approvers;
	private List<PmUser> bbkApprovers;
	private String outCome;
	private String processName;
	private ScQtApproval scQtApproval;	 	
    private String uploadUpdateId;
	//Payload for BPM
		private SCQuotationTenderDataType quotationTenderDataType;
	private boolean isEditable;
	
	
	private List<ScQt> qtList = new ArrayList<ScQt>();

	private List<SmSupplier> panelSuppliers; 
	

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

		CloneAll[] enumValues=CloneAll.values();
		for(CloneAll cloneValue:enumValues){
			TreeNode node1 = new DefaultTreeNode(cloneValue.getDisplayName(), node0);
		}


	}

	/**
	 * @return the qtnTenderSearchBean
	 */
	public QtnTenderSearchBean getQtnTenderSearchBean() {
		return qtnTenderSearchBean;
	}

	/**
	 * @param qtnTenderSearchBean the qtnTenderSearchBean to set
	 */
	public void setQtnTenderSearchBean(QtnTenderSearchBean qtnTenderSearchBean) {
		this.qtnTenderSearchBean = qtnTenderSearchBean;
	}

	/**
	 * @return the cloneQtnTenderSearchBean
	 */
	public QtnTenderSearchBean getCloneQtnTenderSearchBean() {
		return cloneQtnTenderSearchBean;
	}

	/**
	 * @param cloneQtnTenderSearchBean the cloneQtnTenderSearchBean to set
	 */
	public void setCloneQtnTenderSearchBean(
			QtnTenderSearchBean cloneQtnTenderSearchBean) {
		this.cloneQtnTenderSearchBean = cloneQtnTenderSearchBean;
	}

	public List<ScQt> getCloneTenderList() {
		return cloneTenderList;
	}

	public void setCloneTenderList(List<ScQt> cloneTenderList) {
		this.cloneTenderList = cloneTenderList;
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

	public List<ScQtGosodoBudget> getGosodos() {
		return gosodos;
	}

	public void setGosodos(List<ScQtGosodoBudget> gosodos) {
		this.gosodos = gosodos;
	}

	public List<ScQtCategory> getCategorys() {
		return categorys;
	}

	public void setCategorys(List<ScQtCategory> categorys) {
		this.categorys = categorys;
	}

	
	/**
	 * @return the userMinistryList
	 */
	public List<PmOrgProfile> getUserMinistryList() {
		return userMinistryList;
	}

	/**
	 * @param userMinistryList the userMinistryList to set
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
	 * @param userDepartmentList the userDepartmentList to set
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
	 * @param userDivisionList the userDivisionList to set
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
	 * @param goList the goList to set
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
	 * @param soList the soList to set
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
	 * @param doList the doList to set
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

	
	public List<ScQt> getQtList() {
		return qtList;
	}

	public void setQtList(List<ScQt> qtList) {
		this.qtList = qtList;
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
	 * @param selectedUserMinistry the selectedUserMinistry to set
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
	 * @param selectedUserDepartment the selectedUserDepartment to set
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


	public Boolean getIsTender() {
		return isTender;
	}


	public void setIsTender(Boolean isTender) {
		this.isTender = isTender;
	}


	public Boolean getIsPanel() {
		return isPanel;
	}


	public void setIsPanel(Boolean isPanel) {
		this.isPanel = isPanel;
	}


	public Boolean getIsResctricted() {
		return isResctricted;
	}


	public void setIsResctricted(Boolean isResctricted) {
		this.isResctricted = isResctricted;
	}


	public List<ScQtCategory> getOpenCategorys() {
		return openCategorys;
	}


	public void setOpenCategorys(List<ScQtCategory> openCategorys) {
		this.openCategorys = openCategorys;
	}


	public List<ScQtCategory> getRestrictedCategorys() {
		return restrictedCategorys;
	}


	public void setRestrictedCategorys(List<ScQtCategory> restrictedCategorys) {
		this.restrictedCategorys = restrictedCategorys;
	}


	public List<SmSupplier> getPanelSuppliers() {
		return panelSuppliers;
	}

	public SmSupplier getSearchSupplier() {
		return searchSupplier;
	}


	public void setSearchSupplier(SmSupplier searchSupplier) {
		this.searchSupplier = searchSupplier;
	}


	public List<SmSupplier> getSmSupplierList() {
		return smSupplierList;
	}


	public void setSmSupplierList(List<SmSupplier> smSupplierList) {
		this.smSupplierList = smSupplierList;
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


	public ScQtCriteria getQtCriteria() {
		return qtCriteria;
	}


	public void setQtCriteria(ScQtCriteria qtCriteria) {
		this.qtCriteria = qtCriteria;
	}


	public ScQtBriefing getQtBriefing() {
		return qtBriefing;
	}


	public void setQtBriefing(ScQtBriefing qtBriefing) {
		this.qtBriefing = qtBriefing;
	}


	public ScQtCategory getQtCategory() {
		return qtCategory;
	}


	public void setQtCategory(ScQtCategory qtCategory) {
		this.qtCategory = qtCategory;
	}


	public CtCircular getSearchCircular() {
		return searchCircular;
	}


	public void setSearchCircular(CtCircular searchCircular) {
		this.searchCircular = searchCircular;
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


	public List<SmMofAccount> getSmMofAccounts() {
		return smMofAccounts;
	}


	public void setSmMofAccounts(List<SmMofAccount> smMofAccounts) {
		this.smMofAccounts = smMofAccounts;
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


	public CtPanelSupplier getCtPanelSupplier() {
		return ctPanelSupplier;
	}


	public void setCtPanelSupplier(CtPanelSupplier ctPanelSupplier) {
		this.ctPanelSupplier = ctPanelSupplier;
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
       
	public void populateCloneAll(){
		CloneAll [] cloneAllValues=CloneAll.values();
		Arrays.asList(cloneAllValues);
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
	 * @param scQtApproval the scQtApproval to set
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
	 * @param uploadUpdateId the uploadUpdateId to set
	 */
	public void setUploadUpdateId(String uploadUpdateId) {
		this.uploadUpdateId = uploadUpdateId;
	}


	public CtPanelItem getCtPanelItem() {
		return ctPanelItem;
	}


	public void setCtPanelItem(CtPanelItem ctPanelItem) {
		this.ctPanelItem = ctPanelItem;
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


	public List<ScQtGosodoBudget> getSelectedGosodoBudgets() {
		return selectedGosodoBudgets;
	}


	public void setSelectedGosodoBudgets(
			List<ScQtGosodoBudget> selectedGosodoBudgets) {
		this.selectedGosodoBudgets = selectedGosodoBudgets;
	}


	public List<ScQtCategory> getSelectedopenCategories() {
		return selectedopenCategories;
	}


	public void setSelectedopenCategories(List<ScQtCategory> selectedopenCategories) {
		this.selectedopenCategories = selectedopenCategories;
	}


	public List<ScQtCategory> getSelectedrestrictedcCategories() {
		return selectedrestrictedcCategories;
	}


	public void setSelectedrestrictedcCategories(
			List<ScQtCategory> selectedrestrictedcCategories) {
		this.selectedrestrictedcCategories = selectedrestrictedcCategories;
	}


	public List<SmMofAccount> getSelectedrestircedsupplier() {
		return selectedrestircedsupplier;
	}


	public void setSelectedrestircedsupplier(
			List<SmMofAccount> selectedrestircedsupplier) {
		this.selectedrestircedsupplier = selectedrestircedsupplier;
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


	public void setSelectedpanelsupplier(List<SmMofAccount> selectedpanelsupplier) {
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
	
    	 
}
