/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.managedbean.sc.noticeboard;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.supplierproposal.ScQtProposalAmount;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.web.bean.sc.quatationtender.AwardedItemsBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.NoticeBoardSearchDataBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.component.tabview.Tab;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

/**
 * NoticeBoard Managed Bean class holds the properties of Notice Board UI screens.
 * 
 * @author sri 
 * 
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
	private PmOrgProfile ptjGroupProfile = new PmOrgProfile();
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
	private ScQtBsvDtl briefingDtls;
	//private List<ScQtBsvDtl> siteVisitDtls;

	private List<TreeNode> locationNodes;

	private List<ScQtCategory> categoryList;

	private ScQtBsvDtl bsvDetails;
	private String location;

	private List<SmMofAccount> mofAccounts;

	private SmSupplier smSupplier;
	private String selectedTab = ParameterConst.NOTICE_BOARD_STATUS_PUBLISH;

	
	private List<TplSpecZoneDtl> zones;

	private List<PmOrgProfile>ptjList;
	private ScQtBsv bsv;
	
	private List<ScQtDocReport> scQtDocReports;
	private List<SupplierQualificationBean> qualificationBean;
	private boolean qualified;

	private List<BriefingZoneBean> zoneBean;

	private BriefingZoneBean normalBsvBean;
	private List<ScQtProposalAmount> proposalAmts;
	private String mofNumber;
	
	
	private int selectedZoneTabIndex = 0;
	private MenuModel zoneMenuModel;
	
	private String companyName;
	private String proposalURL;
	private List<ScCommitteeMember> scCommitteeMembers;
	private String committeeName;
	private List<ScLoiLoaItem> scLoiLoaItems;
	private String supplierName;
	private String awardType;
	private List<AwardedItemsBean> awardedItems;
	// Used to set mof number if logged user is a supplier
	private String mofNo = null;
	private String supplierMofNo;
	private boolean validMofNo;
	private Map<String, Date> amendmentDateMap;
	private Map<BigDecimal, Date> amendmentClosingDateMap;
	private String ministryName;
	private String jabatan;
	private Long qtId;
	private boolean  renderRegisterBtn = true;
	private boolean  disableRegisterBtn;
	private boolean isNavigateFromNoticeBoard = true;
	private List<TreeNode> zoneTree;
	
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

	
	/**
	 * @return the proposalURL
	 */
	public String getProposalURL() {
		return proposalURL;
	}

	/**
	 * @param proposalURL the proposalURL to set
	 */
	public void setProposalURL(String proposalURL) {
		this.proposalURL = proposalURL;
	}

	/**
	 * @return the scCommitteeMembers
	 */
	public List<ScCommitteeMember> getScCommitteeMembers() {
		return scCommitteeMembers;
	}

	/**
	 * @param scCommitteeMembers the scCommitteeMembers to set
	 */
	public void setScCommitteeMembers(List<ScCommitteeMember> scCommitteeMembers) {
		this.scCommitteeMembers = scCommitteeMembers;
	}

	/**
	 * @return the committeeName
	 */
	public String getCommitteeName() {
		return committeeName;
	}

	/**
	 * @param committeeName the committeeName to set
	 */
	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}

	/**
	 * @return the scLoiLoaItem
	 */
	public List<ScLoiLoaItem> getScLoiLoaItems() {
		return scLoiLoaItems;
	}

	/**
	 * @param scLoiLoaItem the scLoiLoaItem to set
	 */
	public void setScLoiLoaItems(List<ScLoiLoaItem> scLoiLoaItems) {
		this.scLoiLoaItems = scLoiLoaItems;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the awardType
	 */
	public String getAwardType() {
		return awardType;
	}

	/**
	 * @param awardType the awardType to set
	 */
	public void setAwardType(String awardType) {
		this.awardType = awardType;
	}

	/**
	 * @return the awardedItems
	 */
	public List<AwardedItemsBean> getAwardedItems() {
		return awardedItems;
	}

	/**
	 * @param awardedItems the awardedItems to set
	 */
	public void setAwardedItems(List<AwardedItemsBean> awardedItems) {
		this.awardedItems = awardedItems;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the qtNo
	 */
	public String getQtNo() {
		return qtNo;
	}

	/**
	 * @param qtNo the qtNo to set
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	/**
	 * @return the ministryProfile
	 */
	public PmOrgProfile getMinistryProfile() {
		return ministryProfile;
	}

	/**
	 * @param ministryProfile the ministryProfile to set
	 */
	public void setMinistryProfile(PmOrgProfile ministryProfile) {
		this.ministryProfile = ministryProfile;
	}

	/**
	 * @return the ptjProfile
	 */
	public PmOrgProfile getPtjProfile() {
		return ptjProfile;
	}

	/**
	 * @param ptjProfile the ptjProfile to set
	 */
	public void setPtjProfile(PmOrgProfile ptjProfile) {
		this.ptjProfile = ptjProfile;
	}

	/**
	 * @return the categoryL3
	 */
	public PmCategoryL3 getCategoryL3() {
		return categoryL3;
	}

	/**
	 * @param categoryL3 the categoryL3 to set
	 */
	public void setCategoryL3(PmCategoryL3 categoryL3) {
		this.categoryL3 = categoryL3;
	}

	/**
	 * @return the isPubToday
	 */
	public boolean isPubToday() {
		return isPubToday;
	}

	/**
	 * @param isPubToday the isPubToday to set
	 */
	public void setPubToday(boolean isPubToday) {
		this.isPubToday = isPubToday;
	}

	/**
	 * @return the isPubYesterday
	 */
	public boolean isPubYesterday() {
		return isPubYesterday;
	}

	/**
	 * @param isPubYesterday the isPubYesterday to set
	 */
	public void setPubYesterday(boolean isPubYesterday) {
		this.isPubYesterday = isPubYesterday;
	}

	/**
	 * @return the isPubShow
	 */
	public boolean isPubShow() {
		return isPubShow;
	}

	/**
	 * @param isPubShow the isPubShow to set
	 */
	public void setPubShow(boolean isPubShow) {
		this.isPubShow = isPubShow;
	}

	/**
	 * @return the isBreifToday
	 */
	public boolean isBreifToday() {
		return isBreifToday;
	}

	/**
	 * @param isBreifToday the isBreifToday to set
	 */
	public void setBreifToday(boolean isBreifToday) {
		this.isBreifToday = isBreifToday;
	}

	/**
	 * @return the isBreifYesterday
	 */
	public boolean isBreifYesterday() {
		return isBreifYesterday;
	}

	/**
	 * @param isBreifYesterday the isBreifYesterday to set
	 */
	public void setBreifYesterday(boolean isBreifYesterday) {
		this.isBreifYesterday = isBreifYesterday;
	}

	/**
	 * @return the isBreifShow
	 */
	public boolean isBreifShow() {
		return isBreifShow;
	}

	/**
	 * @param isBreifShow the isBreifShow to set
	 */
	public void setBreifShow(boolean isBreifShow) {
		this.isBreifShow = isBreifShow;
	}

	/**
	 * @return the isSvToday
	 */
	public boolean isSvToday() {
		return isSvToday;
	}

	/**
	 * @param isSvToday the isSvToday to set
	 */
	public void setSvToday(boolean isSvToday) {
		this.isSvToday = isSvToday;
	}

	/**
	 * @return the isSvYesterday
	 */
	public boolean isSvYesterday() {
		return isSvYesterday;
	}

	/**
	 * @param isSvYesterday the isSvYesterday to set
	 */
	public void setSvYesterday(boolean isSvYesterday) {
		this.isSvYesterday = isSvYesterday;
	}

	/**
	 * @return the isSvShow
	 */
	public boolean isSvShow() {
		return isSvShow;
	}

	/**
	 * @param isSvShow the isSvShow to set
	 */
	public void setSvShow(boolean isSvShow) {
		this.isSvShow = isSvShow;
	}

	/**
	 * @return the locationRootNode
	 */
	public TreeNode getLocationRootNode() {
		return locationRootNode;
	}

	/**
	 * @param locationRootNode the locationRootNode to set
	 */
	public void setLocationRootNode(TreeNode locationRootNode) {
		this.locationRootNode = locationRootNode;
	}

	/**
	 * @return the locationSelectedNode
	 */
	public TreeNode getLocationSelectedNode() {
		return locationSelectedNode;
	}

	/**
	 * @param locationSelectedNode the locationSelectedNode to set
	 */
	public void setLocationSelectedNode(TreeNode locationSelectedNode) {
		this.locationSelectedNode = locationSelectedNode;
	}

	/**
	 * @return the locationSearchBean
	 */
	public ISearchBean<Object> getLocationSearchBean() {
		return locationSearchBean;
	}

	/**
	 * @param locationSearchBean the locationSearchBean to set
	 */
	public void setLocationSearchBean(ISearchBean<Object> locationSearchBean) {
		this.locationSearchBean = locationSearchBean;
	}

	/**
	 * @return the tempLocationRootNode
	 */
	public TreeNode getTempLocationRootNode() {
		return tempLocationRootNode;
	}

	/**
	 * @param tempLocationRootNode the tempLocationRootNode to set
	 */
	public void setTempLocationRootNode(TreeNode tempLocationRootNode) {
		this.tempLocationRootNode = tempLocationRootNode;
	}

	/**
	 * @return the tabs
	 */
	public List<String> getTabs() {
		return tabs;
	}

	/**
	 * @param tabs the tabs to set
	 */
	public void setTabs(List<String> tabs) {
		this.tabs = tabs;
	}

	/**
	 * @return the resetData
	 */
	public String getResetData() {
		return resetData;
	}

	/**
	 * @param resetData the resetData to set
	 */
	public void setResetData(String resetData) {
		this.resetData = resetData;
	}

	/**
	 * @return the previousPage
	 */
	public String getPreviousPage() {
		return previousPage;
	}

	/**
	 * @param previousPage the previousPage to set
	 */
	public void setPreviousPage(String previousPage) {
		this.previousPage = previousPage;
	}

	/**
	 * @return the isShowPubDate
	 */
	public boolean isShowPubDate() {
		return isShowPubDate;
	}

	/**
	 * @param isShowPubDate the isShowPubDate to set
	 */
	public void setShowPubDate(boolean isShowPubDate) {
		this.isShowPubDate = isShowPubDate;
	}

	/**
	 * @return the isShowBriDate
	 */
	public boolean isShowBriDate() {
		return isShowBriDate;
	}

	/**
	 * @param isShowBriDate the isShowBriDate to set
	 */
	public void setShowBriDate(boolean isShowBriDate) {
		this.isShowBriDate = isShowBriDate;
	}

	/**
	 * @return the isShowSiteDate
	 */
	public boolean isShowSiteDate() {
		return isShowSiteDate;
	}

	/**
	 * @param isShowSiteDate the isShowSiteDate to set
	 */
	public void setShowSiteDate(boolean isShowSiteDate) {
		this.isShowSiteDate = isShowSiteDate;
	}

	/**
	 * @return the publishSearchDataBean
	 */
	public NoticeBoardSearchDataBean getPublishSearchDataBean() {
		return publishSearchDataBean;
	}

	/**
	 * @param publishSearchDataBean the publishSearchDataBean to set
	 */
	public void setPublishSearchDataBean(
			NoticeBoardSearchDataBean publishSearchDataBean) {
		this.publishSearchDataBean = publishSearchDataBean;
	}

	/**
	 * @return the briefSearchDataBean
	 */
	public NoticeBoardSearchDataBean getBriefSearchDataBean() {
		return briefSearchDataBean;
	}

	/**
	 * @param briefSearchDataBean the briefSearchDataBean to set
	 */
	public void setBriefSearchDataBean(NoticeBoardSearchDataBean briefSearchDataBean) {
		this.briefSearchDataBean = briefSearchDataBean;
	}

	/**
	 * @return the siteVisitDataBean
	 */
	public NoticeBoardSearchDataBean getSiteVisitDataBean() {
		return siteVisitDataBean;
	}

	/**
	 * @param siteVisitDataBean the siteVisitDataBean to set
	 */
	public void setSiteVisitDataBean(NoticeBoardSearchDataBean siteVisitDataBean) {
		this.siteVisitDataBean = siteVisitDataBean;
	}

	/**
	 * @return the clsDataBean
	 */
	public NoticeBoardSearchDataBean getClsDataBean() {
		return clsDataBean;
	}

	/**
	 * @param clsDataBean the clsDataBean to set
	 */
	public void setClsDataBean(NoticeBoardSearchDataBean clsDataBean) {
		this.clsDataBean = clsDataBean;
	}

	/**
	 * @return the contactPersons
	 */
	public List<ScQtContactPerson> getContactPersons() {
		return contactPersons;
	}

	/**
	 * @param contactPersons the contactPersons to set
	 */
	public void setContactPersons(List<ScQtContactPerson> contactPersons) {
		this.contactPersons = contactPersons;
	}

	/**
	 * @return the briefingDtls
	 */
	public ScQtBsvDtl getBriefingDtls() {
		return briefingDtls;
	}

	/**
	 * @param briefingDtls the briefingDtls to set
	 */
	public void setBriefingDtls(ScQtBsvDtl briefingDtls) {
		this.briefingDtls = briefingDtls;
	}

	/**
	 * @return the locationNodes
	 */
	public List<TreeNode> getLocationNodes() {
		return locationNodes;
	}

	/**
	 * @param locationNodes the locationNodes to set
	 */
	public void setLocationNodes(List<TreeNode> locationNodes) {
		this.locationNodes = locationNodes;
	}

	/**
	 * @return the categoryList
	 */
	public List<ScQtCategory> getCategoryList() {
		return categoryList;
	}

	/**
	 * @param categoryList the categoryList to set
	 */
	public void setCategoryList(List<ScQtCategory> categoryList) {
		this.categoryList = categoryList;
	}

	/**
	 * @return the address
	 */
	public ScQtBsvDtl getBsvDetails() {
		return bsvDetails;
	}

	/**
	 * @param address the address to set
	 */
	public void setBsvDetails(ScQtBsvDtl bsvDetails) {
		this.bsvDetails = bsvDetails;
	}

	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the mofAccounts
	 */
	public List<SmMofAccount> getMofAccounts() {
		return mofAccounts;
	}

	/**
	 * @param mofAccounts the mofAccounts to set
	 */
	public void setMofAccounts(List<SmMofAccount> mofAccounts) {
		this.mofAccounts = mofAccounts;
	}

	/**
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * @param smSupplier the smSupplier to set
	 */
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

	/**
	 * @return the zones
	 */
	public List<TplSpecZoneDtl> getZones() {
		return zones;
	}

	/**
	 * @param zones the zones to set
	 */
	public void setZones(List<TplSpecZoneDtl> zones) {
		this.zones = zones;
	}

	/**
	 * @return the ptjList
	 */
	public List<PmOrgProfile> getPtjList() {
		return ptjList;
	}

	/**
	 * @param ptjList the ptjList to set
	 */
	public void setPtjList(List<PmOrgProfile> ptjList) {
		this.ptjList = ptjList;
	}

	/**
	 * @return the bsv
	 */
	public ScQtBsv getBsv() {
		return bsv;
	}

	/**
	 * @param bsv the bsv to set
	 */
	public void setBsv(ScQtBsv bsv) {
		this.bsv = bsv;
	}

	/**
	 * @return the scQtDocReports
	 */
	public List<ScQtDocReport> getScQtDocReports() {
		return scQtDocReports;
	}

	/**
	 * @param scQtDocReports the scQtDocReports to set
	 */
	public void setScQtDocReports(List<ScQtDocReport> scQtDocReports) {
		this.scQtDocReports = scQtDocReports;
	}

	/**
	 * @return the qualificationBean
	 */
	public List<SupplierQualificationBean> getQualificationBean() {
		return qualificationBean;
	}

	/**
	 * @param qualificationBean the qualificationBean to set
	 */
	public void setQualificationBean(
			List<SupplierQualificationBean> qualificationBean) {
		this.qualificationBean = qualificationBean;
	}

	/**
	 * @return the qualified
	 */
	public boolean isQualified() {
		return qualified;
	}

	/**
	 * @param qualified the qualified to set
	 */
	public void setQualified(boolean qualified) {
		this.qualified = qualified;
	}

	/**
	 * @return the zoneBean
	 */
	public List<BriefingZoneBean> getZoneBean() {
		return zoneBean;
	}

	/**
	 * @param zoneBean the zoneBean to set
	 */
	public void setZoneBean(List<BriefingZoneBean> zoneBean) {
		this.zoneBean = zoneBean;
	}

	/**
	 * @return the normalBsvBean
	 */
	public BriefingZoneBean getNormalBsvBean() {
		return normalBsvBean;
	}

	/**
	 * @param normalBsvBean the normalBsvBean to set
	 */
	public void setNormalBsvBean(BriefingZoneBean normalBsvBean) {
		this.normalBsvBean = normalBsvBean;
	}

	/**
	 * @return the proposalAmts
	 */
	public List<ScQtProposalAmount> getProposalAmts() {
		return proposalAmts;
	}

	/**
	 * @param proposalAmts the proposalAmts to set
	 */
	public void setProposalAmts(List<ScQtProposalAmount> proposalAmts) {
		this.proposalAmts = proposalAmts;
	}

	/**
	 * @return the mofNumber
	 */
	public String getMofNumber() {
		return mofNumber;
	}

	/**
	 * @param mofNumber the mofNumber to set
	 */
	public void setMofNumber(String mofNumber) {
		this.mofNumber = mofNumber;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	/**
	 * @return the supplierMofNo
	 */
	public String getSupplierMofNo() {
		return supplierMofNo;
	}

	/**
	 * @param supplierMofNo the supplierMofNo to set
	 */
	public void setSupplierMofNo(String supplierMofNo) {
		this.supplierMofNo = supplierMofNo;
	}

	/**
	 * @return the validMofNo
	 */
	public boolean isValidMofNo() {
		return validMofNo;
	}

	/**
	 * @param validMofNo the validMofNo to set
	 */
	public void setValidMofNo(boolean validMofNo) {
		this.validMofNo = validMofNo;
	}

	/**
	 * @return the amendmentDateMap
	 */
	public Map<String, Date> getAmendmentDateMap() {
		return amendmentDateMap;
	}

	/**
	 * @param amendmentDateMap the amendmentDateMap to set
	 */
	public void setAmendmentDateMap(Map<String, Date> amendmentDateMap) {
		this.amendmentDateMap = amendmentDateMap;
	}

	/**
	 * @return the amendmentClosingDateMap
	 */
	public Map<BigDecimal, Date> getAmendmentClosingDateMap() {
		return amendmentClosingDateMap;
	}

	/**
	 * @param amendmentClosingDateMap the amendmentClosingDateMap to set
	 */
	public void setAmendmentClosingDateMap(Map<BigDecimal, Date> amendmentClosingDateMap) {
		this.amendmentClosingDateMap = amendmentClosingDateMap;
	}

	/**
	 * @return the ptjGroupProfile
	 */
	public PmOrgProfile getPtjGroupProfile() {
		return ptjGroupProfile;
	}

	/**
	 * @param ptjGroupProfile the ptjGroupProfile to set
	 */
	public void setPtjGroupProfile(PmOrgProfile ptjGroupProfile) {
		this.ptjGroupProfile = ptjGroupProfile;
	}

	/**
	 * @return the ministryName
	 */
	public String getMinistryName() {
		return ministryName;
	}

	/**
	 * @param ministryName the ministryName to set
	 */
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}

	/**
	 * @return the jabatan
	 */
	public String getJabatan() {
		return jabatan;
	}

	/**
	 * @param jabatan the jabatan to set
	 */
	public void setJabatan(String jabatan) {
		this.jabatan = jabatan;
	}

	/**
	 * @return the qtId
	 */
	public Long getQtId() {
		return qtId;
	}

	/**
	 * @param qtId the qtId to set
	 */
	public void setQtId(Long qtId) {
		this.qtId = qtId;
	}

	/**
	 * @return the renderRegisterBtn
	 */
	public boolean isRenderRegisterBtn() {
		return renderRegisterBtn;
	}

	/**
	 * @param renderRegisterBtn the renderRegisterBtn to set
	 */
	public void setRenderRegisterBtn(boolean renderRegisterBtn) {
		this.renderRegisterBtn = renderRegisterBtn;
	}

	/**
	 * @return the disableRegisterBtn
	 */
	public boolean isDisableRegisterBtn() {
		return disableRegisterBtn;
	}

	/**
	 * @param disableRegisterBtn the disableRegisterBtn to set
	 */
	public void setDisableRegisterBtn(boolean disableRegisterBtn) {
		this.disableRegisterBtn = disableRegisterBtn;
	}

	/**
	 * @return the isNavigateFromNoticeBoard
	 */
	public boolean isNavigateFromNoticeBoard() {
		return isNavigateFromNoticeBoard;
	}

	/**
	 * @param isNavigateFromNoticeBoard the isNavigateFromNoticeBoard to set
	 */
	public void setNavigateFromNoticeBoard(boolean isNavigateFromNoticeBoard) {
		this.isNavigateFromNoticeBoard = isNavigateFromNoticeBoard;
	}

	/**
	 * @return the zoneTree
	 */
	public List<TreeNode> getZoneTree() {
		return zoneTree;
	}

	/**
	 * @param zoneTree the zoneTree to set
	 */
	public void setZoneTree(List<TreeNode> zoneTree) {
		this.zoneTree = zoneTree;
	}


}