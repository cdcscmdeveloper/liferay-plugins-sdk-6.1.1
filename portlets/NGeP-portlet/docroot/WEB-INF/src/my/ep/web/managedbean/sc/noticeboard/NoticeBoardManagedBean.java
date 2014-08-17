package my.ep.web.managedbean.sc.noticeboard;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalAmount;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.NoticeBoardSearchDataBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.component.tabview.Tab;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * @author sri NoticeBoard Managed Bean
 */
@ManagedBean(name = "noticeBoardManagedBean")
@SessionScoped
public class NoticeBoardManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	// dynamic tabs
	private List<Tab> tabList;
	private Integer activeTabIndex = 0;
	private Integer tabIndex = 0;

	public ISearchBean<ScQt> searchBean;
	
	private ScQt scQt;

	// TODO: need to remove above fields...

	// Properties Added by vidyasagar for new noticeboard...

	private String title;
	private String qtNo;
	private PmOrgProfile ministryProfile= new PmOrgProfile();
	private PmOrgProfile ptjProfile = new PmOrgProfile();
	private PmCategoryL3 categoryL3 =  new PmCategoryL3();

	// Fields for hide and show in noticeboard screen
	private boolean isPubToday = false;

	private boolean isPubYesterday = false;

	private boolean isPubShow = false;

	private boolean isBreifToday = false;

	private boolean isBreifYesterday = false;

	private boolean isBreifShow = false;
	private boolean isSvToday = false;

	private boolean isSvYesterday = false;

	private boolean isSvShow = false;

	private TreeNode locationRootNode;

	private TreeNode locationSelectedNode;

	private ISearchBean<Object> locationSearchBean;

	private TreeNode tempLocationRootNode;

	private List<String> tabs;

	private String resetData;

	private String previousPage;

	private boolean isShowPubDate;

	private boolean isShowBriDate;

	private boolean isShowSiteDate;

	private NoticeBoardSearchDataBean publishSearchDataBean;

	private NoticeBoardSearchDataBean briefSearchDataBean;

	private NoticeBoardSearchDataBean siteVisitDataBean;

	private NoticeBoardSearchDataBean clsDataBean;

	private List<ScQtContactPerson> contactPersons;
	private List<ScQtBsvDtl> briefingDtls;
	private List<ScQtBsvDtl> siteVisitDtls;

	private List<TreeNode> locationNodes;

	private List<ScQtCategory> categoryList;

	private String address;
	private String location;

	private List<SmMofAccount> mofAccounts;

	private SmSupplier smSupplier;
	private String selectedTab = ParameterConst.NOTICE_BOARD_STATUS_PUBLISH;

	private List<ScQtBsvDtl> selectedBriefingDtls;
	private List<ScQtBsvDtl> selectedsiteVisitDtls;
	private List<TplSpecZoneDtl> zones;

	private List<PmOrgProfile>ptjList;
	private ScQtBsv bsv;
	
	private List<ScQtCheckList> docuements;
	private List<SupplierQualificationBean> qualificationBean;
	private boolean qualified;

	private List<BriefingZoneBean> zoneBean;

	private BriefingZoneBean normalBsvBean;
	private List<ScQtProposalAmount> proposalAmts;
	private String mofNumber;
	private List<ScQtBsvAttendance> attendanceList;
	
	private List<ScQtBsvAttendance> selectedBsvAttandance;
	
	private int selectedZoneTabIndex = 0;
	private MenuModel zoneMenuModel;
	public NoticeBoardManagedBean() {
		// scQt = new ScQt();
		// selectedScQt = new ScQt();
		publishSearchDataBean = new NoticeBoardSearchDataBean();
		briefSearchDataBean = new NoticeBoardSearchDataBean();
		siteVisitDataBean = new NoticeBoardSearchDataBean();
		clsDataBean = new NoticeBoardSearchDataBean();
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt
	 *            the scQt to set
	 */
	public void setScQt(ScQt scQt) {

		this.scQt = scQt;
	}

	/**
	 * @return the tabList
	 */
	public List<Tab> getTabList() {
		return tabList;
	}

	/**
	 * @param tabList
	 *            the tabList to set
	 */
	public void setTabList(List<Tab> tabList) {
		this.tabList = tabList;
	}

	/**
	 * @return the activeTabIndex
	 */
	public Integer getActiveTabIndex() {
		return activeTabIndex;
	}

	/**
	 * @param activeTabIndex
	 *            the activeTabIndex to set
	 */
	public void setActiveTabIndex(Integer activeTabIndex) {
		this.activeTabIndex = activeTabIndex;
	}

	/**
	 * @return the tabIndex
	 */
	public Integer getTabIndex() {
		return tabIndex;
	}

	/**
	 * @param tabIndex
	 *            the tabIndex to set
	 */
	public void setTabIndex(Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * @return the searchBean
	 */
	public ISearchBean<ScQt> getSearchBean() {
		return searchBean;
	}

	/**
	 * @param searchBean
	 *            the searchBean to set
	 */
	public void setSearchBean(ISearchBean<ScQt> searchBean) {
		this.searchBean = searchBean;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isPubToday() {
		return isPubToday;
	}

	public void setPubToday(boolean isPubToday) {
		this.isPubToday = isPubToday;
	}

	public boolean isPubYesterday() {
		return isPubYesterday;
	}

	public void setPubYesterday(boolean isPubYesterday) {
		this.isPubYesterday = isPubYesterday;
	}

	public boolean isPubShow() {
		return isPubShow;
	}

	public void setPubShow(boolean isPubShow) {
		this.isPubShow = isPubShow;
	}

	public boolean isBreifToday() {
		return isBreifToday;
	}

	public void setBreifToday(boolean isBreifToday) {
		this.isBreifToday = isBreifToday;
	}

	public boolean isBreifYesterday() {
		return isBreifYesterday;
	}

	public void setBreifYesterday(boolean isBreifYesterday) {
		this.isBreifYesterday = isBreifYesterday;
	}

	public boolean isBreifShow() {
		return isBreifShow;
	}

	public void setBreifShow(boolean isBreifShow) {
		this.isBreifShow = isBreifShow;
	}

	public boolean isSvToday() {
		return isSvToday;
	}

	public void setSvToday(boolean isSvToday) {
		this.isSvToday = isSvToday;
	}

	public boolean isSvYesterday() {
		return isSvYesterday;
	}

	public void setSvYesterday(boolean isSvYesterday) {
		this.isSvYesterday = isSvYesterday;
	}

	public boolean isSvShow() {
		return isSvShow;
	}

	public void setSvShow(boolean isSvShow) {
		this.isSvShow = isSvShow;
	}

	public TreeNode getLocationRootNode() {
		return locationRootNode;
	}

	public void setLocationRootNode(TreeNode locationRootNode) {
		this.locationRootNode = locationRootNode;
	}

	public TreeNode getLocationSelectedNode() {
		return locationSelectedNode;
	}

	public void setLocationSelectedNode(TreeNode locationSelectedNode) {
		this.locationSelectedNode = locationSelectedNode;
	}

	public ISearchBean<Object> getLocationSearchBean() {
		return locationSearchBean;
	}

	public void setLocationSearchBean(ISearchBean<Object> locationSearchBean) {
		this.locationSearchBean = locationSearchBean;
	}

	public List<String> getTabs() {
		return tabs;
	}

	public void setTabs(List<String> tabs) {
		this.tabs = tabs;
	}

	public TreeNode getTempLocationRootNode() {
		return tempLocationRootNode;
	}

	public void setTempLocationRootNode(TreeNode tempLocationRootNode) {
		this.tempLocationRootNode = tempLocationRootNode;
	}

	public String getResetData() {
		return resetData;
	}

	public void setResetData(String resetData) {
		this.resetData = resetData;
	}

	public String getPreviousPage() {
		return previousPage;
	}

	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	public boolean isShowPubDate() {
		return isShowPubDate;
	}

	public void setShowPubDate(boolean isShowPubDate) {
		this.isShowPubDate = isShowPubDate;
	}

	public NoticeBoardSearchDataBean getPublishSearchDataBean() {
		return publishSearchDataBean;
	}

	public void setPublishSearchDataBean(
			NoticeBoardSearchDataBean publishSearchDataBean) {
		this.publishSearchDataBean = publishSearchDataBean;
	}

	public NoticeBoardSearchDataBean getBriefSearchDataBean() {
		return briefSearchDataBean;
	}

	public void setBriefSearchDataBean(
			NoticeBoardSearchDataBean briefSearchDataBean) {
		this.briefSearchDataBean = briefSearchDataBean;
	}

	public NoticeBoardSearchDataBean getSiteVisitDataBean() {
		return siteVisitDataBean;
	}

	public void setSiteVisitDataBean(NoticeBoardSearchDataBean siteVisitDataBean) {
		this.siteVisitDataBean = siteVisitDataBean;
	}

	public NoticeBoardSearchDataBean getClsDataBean() {
		return clsDataBean;
	}

	public void setClsDataBean(NoticeBoardSearchDataBean clsDataBean) {
		this.clsDataBean = clsDataBean;
	}

	public boolean isShowBriDate() {
		return isShowBriDate;
	}

	public void setShowBriDate(boolean isShowBriDate) {
		this.isShowBriDate = isShowBriDate;
	}

	public boolean isShowSiteDate() {
		return isShowSiteDate;
	}

	public void setShowSiteDate(boolean isShowSiteDate) {
		this.isShowSiteDate = isShowSiteDate;
	}

	public List<ScQtContactPerson> getContactPersons() {
		return contactPersons;
	}

	public void setContactPersons(List<ScQtContactPerson> contactPersons) {
		this.contactPersons = contactPersons;
	}

	public List<ScQtBsvDtl> getBriefingDtls() {
		return briefingDtls;
	}

	public void setBriefingDtls(List<ScQtBsvDtl> briefingDtls) {
		this.briefingDtls = briefingDtls;
	}

	public List<ScQtBsvDtl> getSiteVisitDtls() {
		return siteVisitDtls;
	}

	public void setSiteVisitDtls(List<ScQtBsvDtl> siteVisitDtls) {
		this.siteVisitDtls = siteVisitDtls;
	}

	public List<TreeNode> getLocationNodes() {
		return locationNodes;
	}

	public void setLocationNodes(List<TreeNode> locationNodes) {
		this.locationNodes = locationNodes;
	}

	public List<ScQtCategory> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<ScQtCategory> categoryList) {
		this.categoryList = categoryList;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * @return the selectedTab
	 */
	public String getSelectedTab() {
		return selectedTab;
	}

	/**
	 * @param selectedTab the selectedTab to set
	 */
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	

	public String getQtNo() {
		return qtNo;
	}

	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	public PmOrgProfile getMinistryProfile() {
		return ministryProfile;
	}

	public void setMinistryProfile(PmOrgProfile ministryProfile) {
		this.ministryProfile = ministryProfile;
	}

	public PmOrgProfile getPtjProfile() {
		return ptjProfile;
	}

	public void setPtjProfile(PmOrgProfile ptjProfile) {
		this.ptjProfile = ptjProfile;
	}

	public PmCategoryL3 getCategoryL3() {
		return categoryL3;
	}

	public void setCategoryL3(PmCategoryL3 categoryL3) {
		this.categoryL3 = categoryL3;
	}

	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	public List<PmOrgProfile> getPtjList() {
		return ptjList;
	}

	public void setPtjList(List<PmOrgProfile> ptjList) {
		this.ptjList = ptjList;
	}

	public ScQtBsv getBsv() {
		return bsv;
	}

	public void setBsv(ScQtBsv bsv) {
		this.bsv = bsv;
	}

	public List<ScQtCheckList> getDocuements() {
		return docuements;
	}

	public void setDocuements(List<ScQtCheckList> docuements) {
		this.docuements = docuements;
	}

	public List<SupplierQualificationBean> getQualificationBean() {
		return qualificationBean;
	}

	public void setQualificationBean(
			List<SupplierQualificationBean> qualificationBean) {
		this.qualificationBean = qualificationBean;
	}

	
	public List<BriefingZoneBean> getZoneBean() {
		return zoneBean;
	}

	public void setZoneBean(List<BriefingZoneBean> zoneBean) {
		this.zoneBean = zoneBean;
	}

	public BriefingZoneBean getNormalBsvBean() {
		return normalBsvBean;
	}

	public void setNormalBsvBean(BriefingZoneBean normalBsvBean) {
		this.normalBsvBean = normalBsvBean;
	}

	public List<ScQtProposalAmount> getProposalAmts() {
		return proposalAmts;
	}

	public void setProposalAmts(List<ScQtProposalAmount> proposalAmts) {
		this.proposalAmts = proposalAmts;
	}

	public List<SmMofAccount> getMofAccounts() {
		return mofAccounts;
	}

	public void setMofAccounts(List<SmMofAccount> mofAccounts) {
		this.mofAccounts = mofAccounts;
	}

	public String getMofNumber() {
		return mofNumber;
	}

	public void setMofNumber(String mofNumber) {
		this.mofNumber = mofNumber;
	}

	public List<ScQtBsvAttendance> getAttendanceList() {
		return attendanceList;
	}

	public void setAttendanceList(List<ScQtBsvAttendance> attendanceList) {
		this.attendanceList = attendanceList;
	}

	public boolean isQualified() {
		return qualified;
	}

	public void setQualified(boolean qualified) {
		this.qualified = qualified;
	}

	
	public List<ScQtBsvDtl> getSelectedBriefingDtls() {
		return selectedBriefingDtls;
	}

	public void setSelectedBriefingDtls(List<ScQtBsvDtl> selectedBriefingDtls) {
		this.selectedBriefingDtls = selectedBriefingDtls;
	}

	public List<ScQtBsvDtl> getSelectedsiteVisitDtls() {
		return selectedsiteVisitDtls;
	}

	public void setSelectedsiteVisitDtls(List<ScQtBsvDtl> selectedsiteVisitDtls) {
		this.selectedsiteVisitDtls = selectedsiteVisitDtls;
	}

	public List<ScQtBsvAttendance> getSelectedBsvAttandance() {
		return selectedBsvAttandance;
	}

	public void setSelectedBsvAttandance(
			List<ScQtBsvAttendance> selectedBsvAttandance) {
		this.selectedBsvAttandance = selectedBsvAttandance;
	}

	/**
	 * @return the selectedZoneTabIndex
	 */
	public int getSelectedZoneTabIndex() {
		return selectedZoneTabIndex;
	}

	/**
	 * @param selectedZoneTabIndex the selectedZoneTabIndex to set
	 */
	public void setSelectedZoneTabIndex(int selectedZoneTabIndex) {
		this.selectedZoneTabIndex = selectedZoneTabIndex;
	}

	/**
	 * @return the zoneMenuModel
	 */
	public MenuModel getZoneMenuModel() {
		return zoneMenuModel;
	}

	/**
	 * @param zoneMenuModel the zoneMenuModel to set
	 */
	public void setZoneMenuModel(MenuModel zoneMenuModel) {
		this.zoneMenuModel = zoneMenuModel;
	}


	

}
