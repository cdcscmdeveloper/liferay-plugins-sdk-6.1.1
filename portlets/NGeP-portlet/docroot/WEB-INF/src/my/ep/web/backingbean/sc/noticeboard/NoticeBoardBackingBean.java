package my.ep.web.backingbean.sc.noticeboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBriefing;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
import my.ep.web.bean.sc.quatationtender.NoticeBoardSearchDataBean;
import my.ep.web.bean.tree.EpDefaultTreeNode;
import my.ep.web.managedbean.sc.noticeboard.NoticeBoardManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.NoticeBoardSection;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author sri Notice Board Backing Bean
 */
@ManagedBean(name = "noticeBoardBackingBean")
@RequestScoped
public class NoticeBoardBackingBean extends
		BaseBackingBean<NoticeBoardManagedBean> {
	public static Logger log = Logger.getLogger(NoticeBoardBackingBean.class);
	private static final String NAV_TO_QTN_TENDER_DETAILS_PAGE = "tenderNoticeDetailScreen.xhtml?faces-redirect=true";
	private static final String NAV_TO_NOTICE_BOARD_SEARCH_PAGE = "noticeboardmain.xhtml?faces-redirect=true";

	public NoticeBoardBackingBean() {
		if(this.getManagedBean().getPublishSearchDataBean() == null){
		this.getManagedBean().setPublishSearchDataBean(new NoticeBoardSearchDataBean());
		}
		this.getManagedBean().setSiteVisitDataBean(new NoticeBoardSearchDataBean());
		this.getManagedBean().setBriefSearchDataBean(new NoticeBoardSearchDataBean());
		this.getManagedBean().setClsDataBean(new NoticeBoardSearchDataBean());

	}

	
	/**
	 * Main Filtration For Notice Board
	 */
	public void searchNoticeBoard() {
		log.info("*** searchNoticeBoard **** ");

		IQTDetailSO detailSO = null;
		QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO qtResDTO = null;
		ISearchBean<ScQt> searchBean = new SearchBean<ScQt>();
		log.info("@@@@@@@@@@@@@@@@@@@@" + this.getManagedBean().getPublishSearchDataBean().isToday());
		// TODO Need to write one helper method to check whether string is empty
		// or null...
		if (null != this.getManagedBean().getTitle()
				&&!"".equals(this.getManagedBean().getTitle())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("qtTitle",
							SearchOperator.OPERATOR_EQUALS.value(), this
									.getManagedBean().getTitle()));

		}
		/*
		 * if
		 * (getManagedBean().getScQt().getMinistryOrgProfile().getOrgProfileId()
		 * != null &&
		 * !"".equals(getManagedBean().getScQt().getMinistryOrgProfile
		 * ().getOrgProfileId())) { searchBean.getDataFilterBeans().add(new
		 * SearchFilterBean( "ministryOrgProfile.orgProfileId",
		 * SearchOperator.OPERATOR_EQUALS.value(),
		 * getManagedBean().getScQt().getMinistryOrgProfile
		 * ().getOrgProfileId())); }
		 * 
		 * if
		 * (getManagedBean().getScQt().getDepartmentOrgProfile().getOrgProfileId
		 * () != null &&
		 * !"".equals(getManagedBean().getScQt().getDepartmentOrgProfile
		 * ().getOrgProfileId())) { searchBean.getDataFilterBeans().add(new
		 * SearchFilterBean( "departmentOrgProfile.orgProfileId",
		 * SearchOperator.OPERATOR_EQUALS.value(),
		 * getManagedBean().getScQt().getDepartmentOrgProfile
		 * ().getOrgProfileId())); }
		 * 
		 * if (getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId() !=
		 * null &&
		 * !"".equals(getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId
		 * ())) { searchBean.getDataFilterBeans().add(new SearchFilterBean(
		 * "pmOrgProfile.orgProfileId", SearchOperator.OPERATOR_EQUALS.value(),
		 * getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId())); }
		 * 
		 * if (getManagedBean().getScQt().getQtTitle() != null &&
		 * !"".equalsIgnoreCase(getManagedBean().getScQt().getQtTitle())) {
		 * searchBean.getDataFilterBeans().add(new SearchFilterBean( "qtTitle",
		 * SearchOperator.OPERATOR_STARTS_WITH.value(),
		 * getManagedBean().getScQt().getQtTitle())); }
		 * 
		 * 
		 * if (getManagedBean().getScQt().getPublishDate() != null &&
		 * !"".equals(getManagedBean().getScQt().getPublishDate())) {
		 * searchBean.getDataFilterBeans().add(new SearchFilterBean(
		 * "publishDate", SearchOperator.OPERATOR_EQUALS.value(),
		 * getManagedBean().getScQt().getPublishDate()));
		 * 
		 * }
		 * 
		 * if (getManagedBean().getScQt().getClosingDate() != null &&
		 * !"".equals(getManagedBean().getScQt().getClosingDate())) {
		 * searchBean.getDataFilterBeans().add(new SearchFilterBean(
		 * "closingDate", SearchOperator.OPERATOR_EQUALS.value(),
		 * getManagedBean().getScQt().getClosingDate()));
		 * 
		 * }
		 */

		qtReqDTO.setQtSearchBean(searchBean);
		// this.getManagedBean().setSearchBean(searchBean);
		try {

			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			qtResDTO = detailSO.getQuationTenders(qtReqDTO);
			log.info("result list for noticeboard "
					+ qtResDTO.getQtResultBean().getResultList().size());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			detailSO = null;
		}

		this.getManagedBean().setScQtSearchlist(
				qtResDTO.getQtResultBean().getResultList());
		// return qtResDTO.getQtResultBean().getResultList();
	}

	/**
	 * Sub Filtration for Tabs
	 */
	/**
	 * @param event
	 */
	public void onTabChange(TabChangeEvent event) {
		log.info("*** TabChange Event **** ");
				
		PmParameterDesc desc=(PmParameterDesc) event.getData();		
		this.getManagedBean().setTabTitle(desc.getCodeName());
		/*IQTDetailSO detailSO = null;
		QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO qtResDTO = null;
		Long statusFilter = null;
		System.out.println("tab index :  "
				+ this.getManagedBean().getActiveTabIndex());
		ISearchBean<ScQt> searchTabBean = new SearchBean<ScQt>();

		if (getManagedBean().getActiveTabIndex().equals(
				QTConstants.NB_PUBLISH_STATUS)) {
			statusFilter = QTStatusConst.QT_PUBLISH;

		} else if (getManagedBean().getActiveTabIndex().equals(
				QTConstants.NB_CHANGES_STATUS)) {

		} else if (getManagedBean().getActiveTabIndex().equals(
				QTConstants.NB_CLOSED_STATUS)) {

		} else if (getManagedBean().getActiveTabIndex().equals(
				QTConstants.NB_PRICE_SCHEDULE_STATUS)) {

		} else if (getManagedBean().getActiveTabIndex().equals(
				QTConstants.NB_AWARDED_STATUS)) {
			statusFilter = QTStatusConst.QT_AWARDED;
		}

		// status Tab Filtering
		if (statusFilter != null && !"".equals(statusFilter)) {
			searchTabBean.getDataFilterBeans().add(
					new SearchFilterBean("pmStatus.statusId",
							SearchOperator.OPERATOR_EQUALS.value(),
							statusFilter));
		}

		
		 * for (SearchFilterBean searchFilter : this.getManagedBean()
		 * .getSearchBean().getDataFilterBeans()) {
		 * searchTabBean.getDataFilterBeans().add( new
		 * SearchFilterBean(searchFilter.getSearchField(),
		 * searchFilter.getSearchOperator(), searchFilter .getSearchValue())); }
		 

		qtReqDTO.setQtSearchBean(searchTabBean);
		this.getManagedBean().setSearchBean(searchTabBean);

		try {

			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			qtResDTO = detailSO.getQuationTenders(qtReqDTO);

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			detailSO = null;
		}
		searchTabBean.getDataFilterBeans().clear(); // to clear all previous
													// Objects
		this.getManagedBean().setScQtSearchlist(
				qtResDTO.getQtResultBean().getResultList());*/
	}

	/**
	 * Sub Filtration for Today , YesterDay, Past days
	 */
	public void subFilter() {
		log.info("*** TabChange Event **** ");
		IQTDetailSO detailSO = null;
		QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
		QuationTenderResDTO qtResDTO = null;
		ISearchBean<ScQt> searchBean = this.getManagedBean().getSearchBean();

		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("publishDate",
						SearchOperator.OPERATOR_EQUALS.value(), new Date()));

		qtReqDTO.setQtSearchBean(searchBean);

		this.getManagedBean().setSearchBean(searchBean);

		try {

			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			qtResDTO = detailSO.getQuationTenders(qtReqDTO);

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			detailSO = null;
		}

		this.getManagedBean().setScQtSearchlist(
				qtResDTO.getQtResultBean().getResultList());
	}

	/**
	 * Qtn / Tender Tab qtnTenderDetails
	 */
	public String qtnTenderDetails(ScQt qt) {
		log.info("** Qtn / TenderDetsils **");
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage("noticeboardlandingpage");
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			reqDTO.setQtId(qt.getQtId());
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(reqDTO);
			if (resDTO.getScQt() != null) {
				ScQt scQt = resDTO.getScQt();
				this.getManagedBean().setScQt(scQt);
				if (scQt.getScQtContactPersons() != null) {
					this.getManagedBean().setContactPersons(
							scQt.getScQtContactPersons());
				}
				constructBrifingSiteVisitDtls(resDTO);
			
			}

			// Category List
			if (resDTO.getCategoryResultBean() != null) {
				List<ScQtCategory> catCodelist = resDTO.getCategoryResultBean()
						.getResultList();
				this.getManagedBean().setCategoryList(catCodelist);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtNoticeBoardSO = null;
		}

		this.getManagedBean().setTabIndex(0);
		return "/xhtml/sc/noticeboard/tenderNoticeDetail.xhtml?faces-redirect=true";
	}

	/**
	 * Drop down for Ministry
	 */
	public void retriveOrgProfilesForMinistryLevel() {
		log.info("populate Ministry List");

		if (this.getManagedBean().getMinstryRootNode() == null) {
			// Get Ministry
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> ministryList = resDTO.getOrgProfileList();

			org.primefaces.model.TreeNode node = this.getManagedBean()
					.getMinstryRootNode();
			if (node == null) {
				node = new EpDefaultTreeNode("Root", null);
				for (PmOrgProfile orgProfile : ministryList) {
					new EpDefaultTreeNode(orgProfile, node);
				}
			}
			this.getManagedBean().setMinstryRootNode(node);

		}
	}

	/**
	 * To retrieve states based on country code.
	 */
	public void retriveLocationByStates() {

		if (this.getManagedBean().getLocationRootNode() == null) {
			try {
				IStateSO stateSO = (IStateSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IStateSO.class);
				StateReqDTO reqDTO = new StateReqDTO();
				reqDTO.setCountryId(1l);
				StateResDTO resDTO = stateSO
						.retriveStatesListByCountryID(reqDTO);

				if (resDTO != null && !resDTO.getStateList().isEmpty()) {

					org.primefaces.model.TreeNode node = this.getManagedBean()
							.getLocationRootNode();
					if (node == null) {
						node = new EpDefaultTreeNode("Root", null);
						for (PmState state : resDTO.getStateList()) {
							new EpDefaultTreeNode("state", state, node);
						}
					}

					this.getManagedBean().setLocationRootNode(node);
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, e
								.toString(), e.getMessage()));
			}

		}
	}

	/*
	 * Method to load the categories list for notice board
	 */
	public void retriveCategoriesCode1() {
		if (this.getManagedBean().getLocationRootNode() == null) {
			ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							ICategoryCodeSO.class);

			CategoryCodeResDTO catResDTO = categoryCodeSO
					.retrieveCategoryL1List();

			org.primefaces.model.TreeNode node = this.getManagedBean()
					.getCategoryRootNode();
			if (node == null) {
				node = new EpDefaultTreeNode("Root", null);
				for (PmCategoryL1 categoryL1 : catResDTO.getCategoryL1List()) {
					new EpDefaultTreeNode(QTConstants.CATEGORY_L1_CODE,
							categoryL1, node);
				}
			}
			this.getManagedBean().setCategoryRootNode(node);

		}
	}

	/*
	 * Method to call dynamic tabs for notice board search
	 */
	public void getTabs() {
		List<Tab> tabs = new ArrayList<Tab>();
		Tab tab1 = new Tab();
		tab1.setTitle("Publish");
		tabs.add(tab1);

		Tab tab2 = new Tab();
		tab2.setTitle("Changes");
		tabs.add(tab2);

		Tab tab3 = new Tab();
		tab3.setTitle("Closed");
		tabs.add(tab3);

		Tab tab4 = new Tab();
		tab4.setTitle("Price Schedule");
		tabs.add(tab4);

		Tab tab5 = new Tab();
		tab5.setTitle("Awarded");
		tabs.add(tab5);

		this.getManagedBean().setTabList(tabs);

	}

	/**
	 * Cancel button for Notice Board
	 */
	public String backBtn() {
		log.info("** Back Button **");
		/*
		 * if (getManagedBean().getMinistryOrgProfile() == null) {
		 * getManagedBean().setScQt(new ScQt());
		 * getManagedBean().getScQt().setPmOrgProfile(new PmOrgProfile());
		 * getManagedBean().getScQt() .setMinistryOrgProfile(new
		 * PmOrgProfile()); getManagedBean().getScQt().setDepartmentOrgProfile(
		 * new PmOrgProfile());
		 * 
		 * }
		 */

		return "/xhtml/sc/noticeboard/"
				+ this.getManagedBean().getPreviousPage()
				+ ".xhtml?faces-redirect=true";

	}

	/**
	 * qtnTenderDocs
	 */
	public String qtnTenderDocs() {
		log.info("** qtnTenderDocs **");
		this.getManagedBean().setTabIndex(2);
		return NAV_TO_QTN_TENDER_DETAILS_PAGE;
	}

	public void hideFields(String sectionName, String day) {
		log.info("inside update date field");
		if (NoticeBoardSection.PUBLISHDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setPubToday(true);
				this.getManagedBean().getPublishSearchDataBean().setToday(true);
			} else {
				this.getManagedBean().setPubYesterday(true);
				this.getManagedBean().getPublishSearchDataBean().setYesterday(true);
			}
			this.getManagedBean().setPubShow(true);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(true);
				this.getManagedBean().getBriefSearchDataBean().setToday(true);
			} else {
				this.getManagedBean().setBreifYesterday(true);
				this.getManagedBean().getBriefSearchDataBean().setYesterday(true);
			}

			this.getManagedBean().setBreifShow(true);
		} else if (NoticeBoardSection.SITEVISITDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setSvToday(true);
				this.getManagedBean().getSiteVisitDataBean().setToday(true);
			} else {
				this.getManagedBean().setSvYesterday(true);
				this.getManagedBean().getSiteVisitDataBean().setYesterday(true);
			}

			this.getManagedBean().setSvShow(true);
		}
	}

	public void showFields(String sectionName, String day) {
		log.info("inside show date field");
		if (NoticeBoardSection.PUBLISHDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			this.getManagedBean().setPublishSearchDataBean(new NoticeBoardSearchDataBean());
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setPubToday(false);
			} else {
				this.getManagedBean().setPubYesterday(false);
			}
			this.getManagedBean().setPubShow(false);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			this.getManagedBean().setBriefSearchDataBean(new NoticeBoardSearchDataBean());
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(false);
			} else {
				this.getManagedBean().setBreifYesterday(false);
			}
			this.getManagedBean().setBreifShow(false);
		} else if (NoticeBoardSection.SITEVISITDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			this.getManagedBean().setSiteVisitDataBean(new NoticeBoardSearchDataBean());
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setSvToday(false);
			} else {
				this.getManagedBean().setSvYesterday(false);
			}
			this.getManagedBean().setSvShow(false);
		}
	}

	public void onNodeSelect(NodeSelectEvent event) {

		TreeNode root = new EpDefaultTreeNode("Root", null);
		TreeNode node = event.getTreeNode();

		Object selectedObject = node.getData();

		if (selectedObject instanceof PmOrgProfile) {

			root = consturctTreeNode(node);

			if (this.getManagedBean().getTempMinstryRootNode() == null) {
				this.getManagedBean().setTempMinstryRootNode(
						this.getManagedBean().getMinstryRootNode());
			}
			// this.getManagedBean().getTempMinstryRootNode().setSelected(false);
			this.getManagedBean().setMinstryRootNode(root);
			this.getManagedBean().setMistrySearchBean(
					new SearchBean<PmOrgProfile>());

		} else if (selectedObject instanceof PmCategoryL1
				|| selectedObject instanceof PmCategoryL2
				|| selectedObject instanceof PmCategoryL3) {

			root = consturctTreeNode(node);
			if (this.getManagedBean().getTempCategoryRootNode() == null) {
				this.getManagedBean().setTempCategoryRootNode(
						this.getManagedBean().getCategoryRootNode());
			}
			/*
			 * this.getManagedBean().getTempCategoryRootNode()
			 * .setSelectable(false);
			 */
			this.getManagedBean().setCategoryRootNode(root);
			this.getManagedBean().setCategorySearchBean(new SearchBean<>());
		} else if (selectedObject instanceof PmState
				|| selectedObject instanceof PmDistrict
				|| selectedObject instanceof PmCity) {
			root = consturctTreeNode(node);

			if (this.getManagedBean().getTempLocationRootNode() == null) {
				this.getManagedBean().setTempLocationRootNode(
						this.getManagedBean().getLocationRootNode());
			}
			// this.getManagedBean().getTempLocationRootNode().setSelected(false);
			this.getManagedBean().setLocationRootNode(root);
			this.getManagedBean().setLocationSearchBean(new SearchBean<>());
		}

	}

	public void onOrgProfileNodeExpand(NodeExpandEvent event) {
		PmOrgProfile orgProfile = (PmOrgProfile) event.getTreeNode().getData();
		Long parentOrgProfileId = orgProfile.getOrgProfileId();
		this.getManagedBean().setMistrySearchBean(null);
		int childCount = event.getTreeNode().getChildCount();
		if (childCount == 0) {
			if (orgProfile.getParentOrgProfile() == null) {

				if (parentOrgProfileId != null) {
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
					reqDTO.setParentOrgProfileId(parentOrgProfileId);

					IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IOrgProfileSO.class);
					OrgProfileResDTO resDTO = orgProfileSO
							.retrieveOrgProfileListByOrgType(reqDTO);

					List<PmOrgProfile> deptList = resDTO.getOrgProfileList();
					org.primefaces.model.TreeNode root = event.getTreeNode();
					root.getParent().setSelected(false);
					for (PmOrgProfile pmOrgProfile : deptList) {
						new EpDefaultTreeNode(pmOrgProfile, root);
					}

				}
			} else {
				if (parentOrgProfileId != null) {
					OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
					reqDTO.setOrgType(OrganizationType.DIVISION.value());
					reqDTO.setParentOrgProfileId(parentOrgProfileId);

					IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IOrgProfileSO.class);
					OrgProfileResDTO resDTO = orgProfileSO
							.retrieveOrgProfileListByOrgType(reqDTO);

					List<PmOrgProfile> deptList = resDTO.getOrgProfileList();
					org.primefaces.model.TreeNode root = event.getTreeNode();
					for (PmOrgProfile pmOrgProfile : deptList) {
						new DefaultTreeNode(pmOrgProfile, root);
					}
				}
			}
		}
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		System.out.println("collapse @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		System.out.println("unselect @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	public void onNodeExpand(NodeExpandEvent event) {

		Object obj = event.getTreeNode().getData();

		if (event.getTreeNode().getChildCount() <= 0) {
			if (obj instanceof PmCategoryL1) {
				this.getManagedBean().setCategorySearchBean(null);
				ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL1Id(((PmCategoryL1) obj).getCategoryL1Id());
				CategoryCodeResDTO categorysResDTO = categoryCodeSO
						.retrieveCategoryL2ListByL1Id(reqDTO);
				List<PmCategoryL2> catL2List = categorysResDTO
						.getCategoryL2List();

				org.primefaces.model.TreeNode root = event.getTreeNode();
				root.getParent().setSelected(false);
				for (PmCategoryL2 pmCategoryL2 : catL2List) {
					new EpDefaultTreeNode(QTConstants.CATEGORY_L2_CODE,
							pmCategoryL2, root);
				}
			} else if (obj instanceof PmCategoryL2) {
				this.getManagedBean().setCategorySearchBean(null);
				ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();

				reqDTO.setCategoryL2Id(((PmCategoryL2) obj).getCategoryL2Id());
				CategoryCodeResDTO categorysResDTO = categoryCodeSO
						.retrieveCategoryL3ListByL2Id(reqDTO);
				List<PmCategoryL3> catL3List = categorysResDTO
						.getCategoryL3List();
				org.primefaces.model.TreeNode root = event.getTreeNode();
				for (PmCategoryL3 pmCategoryL3 : catL3List) {
					new DefaultTreeNode(QTConstants.CATEGORY_L3_CODE,
							pmCategoryL3, root);
				}

			}

			if (obj instanceof PmState) {
				this.getManagedBean().setLocationSearchBean(null);
				DistrictReqDTO reqDTO = new DistrictReqDTO();
				reqDTO.setStateId(((PmState) obj).getStateId());
				IDistrictSO districtSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IDistrictSO.class);
				DistrictResDTO resDTO = districtSO
						.retriveDistrictListByStateID(reqDTO);

				List<PmDistrict> districList = resDTO.getDistrictList();

				org.primefaces.model.TreeNode root = event.getTreeNode();
				for (PmDistrict district : districList) {
					new EpDefaultTreeNode("district", district, root);
				}
			} else if (obj instanceof PmDistrict) {
				this.getManagedBean().setLocationSearchBean(null);
				CityReqDTO reqDTO = new CityReqDTO();
				reqDTO.setDistrictId(((PmDistrict) obj).getDistrictId());
				ICitySO citySO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								ICitySO.class);
				CityResDTO resDTO = citySO.retriveCityListByDistrictId(reqDTO);
				List<PmCity> cityList = resDTO.getCityList();
				org.primefaces.model.TreeNode root = event.getTreeNode();
				for (PmCity city : cityList) {
					new DefaultTreeNode("city", city, root);
				}

			}
		}
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	public void onTabChangeAction(TabChangeEvent event) {
		String tabTitle = event.getTab().getTitle();
		if ("Schedule".equalsIgnoreCase(tabTitle)) {
			BSVAttendanceBackingBean attendanceBackingBean = null;
			try {
				attendanceBackingBean = new BSVAttendanceBackingBean();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// attendanceBackingBean.retrieveBriefingSiteVisitList(146l);
		}
	}

	private TreeNode consturctTreeNode(TreeNode selectedNode) {
		TreeNode root = new EpDefaultTreeNode("Root", null);
		TreeNode immediateParent = selectedNode.getParent();
		selectedNode.setExpanded(true);
		if (immediateParent.equals(root)) {
			new EpDefaultTreeNode(selectedNode.getType(),
					selectedNode.getData(), root).setExpanded(true);
		} else {
			TreeNode parent = immediateParent.getParent();
			if (parent.getParent() == null) {
				TreeNode nextParent = new EpDefaultTreeNode(
						immediateParent.getType(), immediateParent.getData(),
						root);
				nextParent.setExpanded(true);
				new EpDefaultTreeNode(selectedNode.getType(),
						selectedNode.getData(), nextParent).setExpanded(true);

			} else {

				TreeNode nextParent = new EpDefaultTreeNode(parent.getType(),
						parent.getData(), root);
				nextParent.setExpanded(true);
				TreeNode subParent = new EpDefaultTreeNode(
						immediateParent.getType(), immediateParent.getData(),
						nextParent);
				subParent.setExpanded(true);
				new DefaultTreeNode(selectedNode.getType(),
						selectedNode.getData(), subParent).setExpanded(true);

			}
		}

		return root;
	}

	public void clearTreeNode(String selectedNode) {
		if (selectedNode.equalsIgnoreCase("ministry")) {
			this.getManagedBean().setMinistrySelectedNode(null);

			TreeNode rootNode = this.getManagedBean().getTempMinstryRootNode();
			List<TreeNode> childNodes = rootNode.getChildren();

			for (TreeNode node : childNodes) {
				if (node.isExpanded()) {
					node.setExpanded(false);
				}
			}
			this.getManagedBean().setMinstryRootNode(rootNode);
			this.getManagedBean().setTempMinstryRootNode(null);
			this.getManagedBean().setMistrySearchBean(null);

		} else if (selectedNode.equalsIgnoreCase("category")) {
			this.getManagedBean().setCategorySelectedNode(null);
			TreeNode rootNode = this.getManagedBean().getTempCategoryRootNode();
			List<TreeNode> childNodes = rootNode.getChildren();

			for (TreeNode node : childNodes) {
				if (node.isExpanded()) {
					node.setExpanded(false);
				}
			}
			this.getManagedBean().setCategoryRootNode(rootNode);
			this.getManagedBean().setTempLocationRootNode(null);
			this.getManagedBean().setCategorySearchBean(null);
		} else if (selectedNode.equalsIgnoreCase("location")) {
			this.getManagedBean().setLocationSelectedNode(null);
			TreeNode rootNode = this.getManagedBean().getTempLocationRootNode();
			List<TreeNode> childNodes = rootNode.getChildren();

			for (TreeNode node : childNodes) {
				if (node.isExpanded()) {
					node.setExpanded(false);
				}
			}

			this.getManagedBean().setLocationRootNode(
					this.getManagedBean().getTempLocationRootNode());
			this.getManagedBean().setTempLocationRootNode(null);
			this.getManagedBean().setLocationSearchBean(null);
		}

	}

	public String gotoSupplierQualificationCriteria() {
		this.getManagedBean().setPreviousPage("noticeboardlandingpage");
		if(this.getManagedBean().getMofAccount()==null){
			this.getManagedBean().setMofAccount(new SmMofAccount());
		}
		return "/xhtml/sc/noticeboard/nbsupplierqulificationcriteria.xhtml?faces-redirect=true";
	}

	public String gotoSupplierRegistration(ScQt qt) {
		this.getManagedBean().setPreviousPage("noticeboardlandingpage");
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
		reqDTO.setQtId(qt.getQtId());
		NoticeBoardResDTO resDTO = qtNoticeBoardSO
				.retrieveQtNoticeDtl(reqDTO);
		if (resDTO.getScQt() != null) {
			ScQt scQt = resDTO.getScQt();
			if (scQt.getScQtContactPersons() != null) {
				this.getManagedBean().setContactPersons(
						scQt.getScQtContactPersons());
			}
			constructBrifingSiteVisitDtls(resDTO);
		
		}
		
		return "/xhtml/sc/noticeboard/nbsupplierreg.xhtml?faces-redirect=true";
	}

	public void hideAndShowDates(String dateType) {
		if (dateType.equalsIgnoreCase("PbDate")) {
			if (this.getManagedBean().getPublishSearchDataBean().getPassDays() != null
					&& this.getManagedBean().getPublishSearchDataBean()
							.getPassDays() != "") {
				this.getManagedBean().setShowPubDate(true);
			} else {
				this.getManagedBean().setShowPubDate(false);
			}
		} else if (dateType.equalsIgnoreCase("SiteVisit")) {
			if (this.getManagedBean().getSiteVisitDataBean().getPassDays() != null
					&& this.getManagedBean().getSiteVisitDataBean()
							.getPassDays() != "") {
				this.getManagedBean().setShowSiteDate(true);
			} else {
				this.getManagedBean().setShowSiteDate(false);
			}
		} else if (dateType.equalsIgnoreCase("Brief")) {
			if (this.getManagedBean().getBriefSearchDataBean().getPassDays() != null
					&& this.getManagedBean().getBriefSearchDataBean()
							.getPassDays() != "") {
				this.getManagedBean().setShowBriDate(true);
			} else {
				this.getManagedBean().setShowBriDate(false);
			}
		}
	}

	public void reset() {
		// RequestContext.getCurrentInstance().reset("form:nbpanel");

		this.getManagedBean().setTitle(null);
		this.getManagedBean().setPublishSearchDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setBriefSearchDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setClsDataBean(new NoticeBoardSearchDataBean());
		this.getManagedBean().setSiteVisitDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setShowPubDate(false);
		this.getManagedBean().setShowSiteDate(false);
		this.getManagedBean().setShowBriDate(false);

	}

	private void constructBriefingDtls(List<ScQtBriefing> qtBriefingDtls,
			boolean sameAsBsv) {
		if (sameAsBsv) {
			this.getManagedBean().setBriefingDtls(qtBriefingDtls);

		} else {
			if (qtBriefingDtls != null) {
				List<ScQtBriefing> briefingDtls = new ArrayList<ScQtBriefing>();
				List<ScQtBriefing> siteVisitDtls = new ArrayList<ScQtBriefing>();
				for (ScQtBriefing briefing : qtBriefingDtls) {
					if ("B".equalsIgnoreCase(briefing.getBriefingType())) {
						briefingDtls.add(briefing);
					} else {
						siteVisitDtls.add(briefing);
					}

				}
				this.getManagedBean().setBriefingDtls(briefingDtls);
				this.getManagedBean().setSiteVisitDtls(siteVisitDtls);
			}
		}
	}

	/*private void constructLocationTree(List<ScQtLocCriteria> locCriterias) {
		List<PmState> states = new ArrayList<PmState>();
		List<TreeNode> locations = new ArrayList<TreeNode>();
		for (ScQtLocCriteria locCriteria : locCriterias) {
			PmState pmState = locCriteria.getPmState();
			TreeNode root = null;
			if (!states.contains(pmState)) {
				root = new DefaultTreeNode("state", pmState, null);
				root.setExpanded(true);
				locations.add(root);
			}
			PmDivision division = locCriteria.getPmDivision();
			TreeNode divisonNode = null;
			if (division != null) {
				divisonNode = new DefaultTreeNode("division", division, root);
				divisonNode.setExpanded(true);
			}
			PmDistrict pmDistrict = locCriteria.getPmDistrict();
			TreeNode districtNode = null;
			if (pmDistrict != null) {
				if (divisonNode == null) {
					districtNode = new DefaultTreeNode("district", pmDistrict,
							root);
					districtNode.setExpanded(true);
				} else {
					districtNode = new DefaultTreeNode("district", pmDistrict,
							divisonNode);
					districtNode.setExpanded(true);
				}
			}
			PmCity pmCity = locCriteria.getPmCity();
			if (pmCity != null) {
				new DefaultTreeNode("city", pmCity, districtNode);
			}
			states.add(pmState);
		}
		this.getManagedBean().setLocationNodes(locations);

	}
*/
	public void zoneTabChange(TabChangeEvent changeEvent) {

		/*ScQtZoneCriteria qtZoneCriteria = (ScQtZoneCriteria) changeEvent
				.getData();
		List<ScQtBriefing> briefingDtls = qtZoneCriteria.getScQtBriefings();
		constructBriefingDtls(briefingDtls, this.getManagedBean().getScQt()
				.isSameBsv());
		List<ScQtLocCriteria> locations = qtZoneCriteria.getScQtLocCriterias();*/
		//constructLocationTree(null);
	}

	public void showLocationDtls(ScQtBriefing briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		this.getManagedBean().setAddress(briefing.getPmAddress().getAddress1());
	}

	
	private void constructBrifingSiteVisitDtls(NoticeBoardResDTO resDTO){
		 ScQt scQt=resDTO.getScQt();
	
	/*	ScQtCriteria scQtCriteria = scQt.getScQtCriterias();
		if (scQtCriteria != null) {
			if (scQtCriteria.isMultiLocality()) {

				ISearchResultBean<ScQtZoneCriteria> zoneCriterias = resDTO
						.getZoneCriteriaResultBean();
				List<ScQtZoneCriteria> zoneCriteria = zoneCriterias
						.getResultList();
				this.getManagedBean().setZoneList(zoneCriteria);
				ScQtZoneCriteria qtZoneCriteria = zoneCriteria.get(0);
				List<ScQtBriefing> briefingDtls = qtZoneCriteria
						.getScQtBriefings();
				constructBriefingDtls(briefingDtls, scQt.isSameBsv());
				List<ScQtLocCriteria> locations = qtZoneCriteria
						.getScQtLocCriterias();
				constructLocationTree(locations);

			} else {
				ISearchResultBean<ScQtLocCriteria> locCriterias = resDTO
						.getLocCriteriaResultBean();
				List<ScQtBriefing> qtBriefingDtls = scQt
						.getScQtBriefings();
				constructBriefingDtls(qtBriefingDtls, scQt.isSameBsv());
				List<ScQtLocCriteria> locations = locCriterias
						.getResultList();
				constructLocationTree(locations);
			}
		}else{
			this.getManagedBean().setZoneList(null);
			this.getManagedBean().setBriefingDtls(null);
			this.getManagedBean().setSiteVisitDtls(null);
			this.getManagedBean().setLocationNodes(null);
		}*/
		// this.getManagedBean().setScQtCriteria(scQtCriteria);
		// Qtn/ Tender notice
												// Details
	
	}
	
	public void showPriceSchedules(ScQt scqt){
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			reqDTO.setQtId(scqt.getQtId());
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(reqDTO);
			this.getManagedBean().setScQt(resDTO.getScQt());
	}catch(Exception ex){
		ex.printStackTrace();
	}
	}
}
