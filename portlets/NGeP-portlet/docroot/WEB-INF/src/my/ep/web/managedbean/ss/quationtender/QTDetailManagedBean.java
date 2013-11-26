package my.ep.web.managedbean.ss.quationtender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtBriefing;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtGosodoBudget;
import my.ep.web.bean.ss.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.type.ss.quationtender.CloneAll;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
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
	private ScQtCriteria qtc;
	private ScQtBriefing qb=new ScQtBriefing();
	private ScQtCategory qtcategory= new ScQtCategory();
	private String selectedBoolean1;
	private String multiLoc;
	private List<ScQtCategory> openCategorys;
	private List<ScQtCategory> restrictedCategorys;
	private SmSupplier searchSupplier;
	private List<SmSupplier> smSupplierList;

	
	/**
	 * For Search Quotation/Tender Screen
	 */
	private QtnTenderSearchBean qtnTenderSearchBean;
	private QtnTenderSearchBean cloneQtnTenderSearchBean;

	private List<PmCategoryL1> categoryL1s;

	private List<PmCategoryL2> categoryL2s;

	private List<PmCategoryL3> categoryL3s;

	private List<PmState> states;

	private List<PmDistrict> districts;

	public List<ScQtGosodoBudget> gosodos;
	public List<ScQtCategory> categorys;

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

	private List<PmZone> zoneList = new ArrayList<PmZone>();
	private List<PmState> stateList = new ArrayList<PmState>();
	public static List<PmZone> pmZoneList=new ArrayList<PmZone>();
	public static List<PmState> pmStateList=new ArrayList<PmState>();
	public static Map<Long, List<PmState>> zoneStateMap=new HashMap<Long, List<PmState>>();
	public static Map<Long, List<PmDistrict>> stateDistrictMap=new HashMap<Long, List<PmDistrict>>();
	public static Map<Long,PmState> stateMap=new HashMap<Long,PmState>();
	public static Map<Long,PmDistrict> districtMap=new HashMap<Long,PmDistrict>();
	private PmZone pmZone;
	private Node selectedZone;
	private Node selectedState;
	private String selectedPmZoneId;
	private PmZone selectedPmZone =new PmZone();
	private PmState selectedPmState=new PmState();
	private List<SelectItem> pmZoneOptions=new ArrayList<SelectItem>();
	private List<PmState> pmStates=new ArrayList<PmState>();
	private List<PmDistrict> pmDistricts=new ArrayList<PmDistrict>();
	private List<String> selectedPmStateList=new ArrayList<String>();
	private List<String> selectedPmDistricts=new ArrayList<String>();
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
	static{
			PmZone zone=new PmZone();
			zone.setZoneName("India");
			zone.setZoneId(1);
		    pmZoneList.add(zone);
		    PmZone malysiaZone=new PmZone();
		    malysiaZone.setZoneName("Malaysia");
		    malysiaZone.setZoneId(2);
		    pmZoneList.add(malysiaZone);
		    
		    List<PmState> stateList=new ArrayList<PmState>();
		    PmState state=new PmState();
		    state.setStateName("AP");
		    state.setStateId(1);
		    stateList.add(state);
		    pmStateList.add(state);
		    stateMap.put(state.getStateId(),state);
		    
		    PmState state1=new PmState();
		    state1.setStateName("TN");
		    state1.setStateId(2);
		    stateList.add(state1);
		    pmStateList.add(state1);
		    stateMap.put(state1.getStateId(),state1);
		    zoneStateMap.put(new Long(1), stateList);
		    
		    List<PmState> stateList1=new ArrayList<PmState>();
		    PmState state3=new PmState();
		    state3.setStateName("KL");
		    state3.setStateId(3);
		    stateList1.add(state3);
		    pmStateList.add(state3);
		    stateMap.put(state3.getStateId(),state3);
		    PmState state4=new PmState();
		    state4.setStateName("CJ");
		    state4.setStateId(4);
		    stateList1.add(state4);
		    pmStateList.add(state4);
		    stateMap.put(state4.getStateId(),state4);
		    zoneStateMap.put(new Long(2), stateList1);
		    
		    List<PmDistrict> distrcitList= new ArrayList<PmDistrict>();
		    PmDistrict district=new PmDistrict();
		    district.setDistrictName("Hyd");
		    district.setDistrictId(1L);
		    distrcitList.add(district);
		    districtMap.put(district.getDistrictId(),district);
		    stateDistrictMap.put(new Long(1), distrcitList);
		    
		    List<PmDistrict> districtList1 =new ArrayList<PmDistrict>();
		    PmDistrict district2=new PmDistrict();
		    district2.setDistrictName("Brick");
		    district2.setDistrictId(2L);
		    districtList1.add(district2);
		    districtMap.put(district2.getDistrictId(),district2);
		    stateDistrictMap.put(new Long(3), distrcitList);
		    
	}
	
	public static List<PmZone> getPmZoneList() {
		return pmZoneList;
	}


	public static void setPmZoneList(List<PmZone> pmZoneList) {
		QTDetailManagedBean.pmZoneList = pmZoneList;
	}


	public static List<PmState> getPmStateList() {
		return pmStateList;
	}


	public Node getSelectedZone() {
		return selectedZone;
	}


	public Node getSelectedState() {
		return selectedState;
	}


	public static void setPmStateList(List<PmState> pmStateList) {
		QTDetailManagedBean.pmStateList = pmStateList;
	}


	public PmZone getPmZone() {
		return pmZone;
	}
	
	public void setPmZone(PmZone pmZone) {
		this.pmZone = pmZone;
	}
	
	public static Map<Long, List<PmState>> getZoneStateMap() {
		return zoneStateMap;
	}


	public static void setZoneStateMap(Map<Long, List<PmState>> zoneStateMap) {
		QTDetailManagedBean.zoneStateMap = zoneStateMap;
	}


	public static Map<Long, List<PmDistrict>> getStateDistrictMap() {
		return stateDistrictMap;
	}


	public static void setStateDistrictMap(
			Map<Long, List<PmDistrict>> stateDistrictMap) {
		QTDetailManagedBean.stateDistrictMap = stateDistrictMap;
	}


	public static Map<Long, PmState> getStateMap() {
		return stateMap;
	}


	public static void setStateMap(Map<Long, PmState> stateMap) {
		QTDetailManagedBean.stateMap = stateMap;
	}


	public static Map<Long, PmDistrict> getDistrictMap() {
		return districtMap;
	}


	public static void setDistrictMap(Map<Long, PmDistrict> districtMap) {
		QTDetailManagedBean.districtMap = districtMap;
	}


	public void setSelectedZone(Node selectedZone) {
		this.selectedZone = selectedZone;
	}	

	public void setSelectedState(Node selectedState) {
		this.selectedState = selectedState;
	}


	public String getSelectedPmZoneId() {
		return selectedPmZoneId;
	}


	public void setSelectedPmZoneId(String selectedPmZoneId) {
		this.selectedPmZoneId = selectedPmZoneId;
	}


	public PmZone getSelectedPmZone() {
		return selectedPmZone;
	}


	public void setSelectedPmZone(PmZone selectedPmZone) {
		this.selectedPmZone = selectedPmZone;
	}


	public PmState getSelectedPmState() {
		return selectedPmState;
	}


	public void setSelectedPmState(PmState selectedPmState) {
		this.selectedPmState = selectedPmState;
	}


	public List<SelectItem> getPmZoneOptions() {
		return pmZoneOptions;
	}


	public void setPmZoneOptions(List<SelectItem> pmZoneOptions) {
		this.pmZoneOptions = pmZoneOptions;
	}


	public List<PmState> getPmStates() {
		return pmStates;
	}


	public void setPmStates(List<PmState> pmStates) {
		this.pmStates = pmStates;
	}


	public List<PmDistrict> getPmDistricts() {
		return pmDistricts;
	}


	public void setPmDistricts(List<PmDistrict> pmDistricts) {
		this.pmDistricts = pmDistricts;
	}


	public List<String> getSelectedPmStateList() {
		return selectedPmStateList;
	}


	public void setSelectedPmStateList(List<String> selectedPmStateList) {
		this.selectedPmStateList = selectedPmStateList;
	}


	public List<String> getSelectedPmDistricts() {
		return selectedPmDistricts;
	}


	public void setSelectedPmDistricts(List<String> selectedPmDistricts) {
		this.selectedPmDistricts = selectedPmDistricts;
	}

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

		root3 = new DefaultTreeNode("Root", null);
		TreeNode documents = new DefaultTreeNode(("Zone1"), root3);
		documents.setExpanded(true);

		TreeNode states = new DefaultTreeNode(("State1"), documents);
		states.setExpanded(true);
		TreeNode districts = new DefaultTreeNode(("State2"), documents);
		districts.setExpanded(true);

		// Documents
		TreeNode d1 = new DefaultTreeNode(("District1"), states);
		TreeNode d2 = new DefaultTreeNode(("District2"), states);
		TreeNode d = new DefaultTreeNode(("District1"), districts);

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

	public ScQtCriteria getQtc() {
		return qtc;
	}

	public void setQtc(ScQtCriteria qtc) {
		this.qtc = qtc;
	}
	
	public ScQtBriefing getQb() {
		return qb;
	}


	public void setQb(ScQtBriefing qb) {
		this.qb = qb;
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

	public List<PmZone> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

	public List<PmState> getStateList() {
		return stateList;
	}

	public void setStateList(List<PmState> stateList) {
		this.stateList = stateList;
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

	public ScQtCategory getQtcategory() {
		return qtcategory;
	}

	public void setQtcategory(ScQtCategory qtcategory) {
		this.qtcategory = qtcategory;
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


	
	
	

}
