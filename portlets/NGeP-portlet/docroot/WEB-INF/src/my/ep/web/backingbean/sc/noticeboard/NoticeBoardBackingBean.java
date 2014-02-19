package my.ep.web.backingbean.sc.noticeboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

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
import my.ep.constant.sc.QTStatusConst;
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
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
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
		log.info("@@@@@@@@@@@@@@@@@@@@" + this.getManagedBean().getTitle());
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("qtTitle", SearchOperator.OPERATOR_EQUALS
						.value(), this.getManagedBean().getTitle()));

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
	public void onTabChange(TabChangeEvent event) {
		log.info("*** TabChange Event **** ");
		IQTDetailSO detailSO = null;
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

		for (SearchFilterBean searchFilter : this.getManagedBean()
				.getSearchBean().getDataFilterBeans()) {
			searchTabBean.getDataFilterBeans().add(
					new SearchFilterBean(searchFilter.getSearchField(),
							searchFilter.getSearchOperator(), searchFilter
									.getSearchValue()));
		}

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
				qtResDTO.getQtResultBean().getResultList());
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

		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			reqDTO.setQtId(146l);
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(reqDTO);
			if (resDTO.getScQt() != null) {
				ScQt scQt = resDTO.getScQt();

				// scQtCriteria for Multiple Locality, AllStates and
				// PaidUpCaptial
				ScQtCriteria scQtCriteria = scQt.getScQtCriterias();

				if (scQtCriteria.isMultiLocality()) {
					this.getManagedBean().setMultipleLocality("Yes");
				} else {
					this.getManagedBean().setMultipleLocality("No");
				}

				// locVisiable for Location Listing Visible
				if (scQtCriteria.isAllState()) {
					this.getManagedBean().setAllStates("Yes");
					this.getManagedBean().setLocVisible(false);
				} else {
					this.getManagedBean().setAllStates("No");
					this.getManagedBean().setLocVisible(true);

				}

				this.getManagedBean().setScQtCriteria(scQtCriteria);
				this.getManagedBean().setScQt(scQt); // Qtn/ Tender notice
														// Details
			}

			// Category List
			if (resDTO.getCategoryResultBean() != null) {
				List<ScQtCategory> catCodelist = resDTO.getCategoryResultBean()
						.getResultList();
				System.out.println("Category Code list: " + catCodelist.size());
				this.getManagedBean().setCategoryList(catCodelist);
			}

			// LocCriteria List
			if (resDTO.getLocCriteriaResultBean() != null) {
				List<ScQtLocCriteria> loclist = resDTO
						.getLocCriteriaResultBean().getResultList();
				System.out.println("LocCriteria list: " + loclist.size());
				this.getManagedBean().setLocationList(loclist);

			}

			// ZoneCriteria List
			if (resDTO.getZoneCriteriaResultBean() != null) {
				List<ScQtZoneCriteria> zonelist = resDTO
						.getZoneCriteriaResultBean().getResultList();
				System.out.println("ZoneCriteria list: " + zonelist.size());
				this.getManagedBean().setZoneList(zonelist);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtNoticeBoardSO = null;
		}

		this.getManagedBean().setTabIndex(0);
		return "/xhtml/sc/notice/tenderNoticeDetailScreen.xhtml?faces-redirect=true";
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

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
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
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
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
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);

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
		if (getManagedBean().getMinistryOrgProfile() == null) {
			getManagedBean().setScQt(new ScQt());
			getManagedBean().getScQt().setPmOrgProfile(new PmOrgProfile());
			getManagedBean().getScQt()
					.setMinistryOrgProfile(new PmOrgProfile());
			getManagedBean().getScQt().setDepartmentOrgProfile(
					new PmOrgProfile());

		}
		return "/xhtml/ss/noticeboard/noticeboardlandingpage.xhtml?faces-redirect=true";

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
			} else {
				this.getManagedBean().setPubYesterday(true);
			}
			this.getManagedBean().setPubShow(true);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(true);
			} else {
				this.getManagedBean().setBreifYesterday(true);
			}

			this.getManagedBean().setBreifShow(true);
		} else if (NoticeBoardSection.SITEVISITDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setSvToday(true);
			} else {
				this.getManagedBean().setSvYesterday(true);
			}

			this.getManagedBean().setSvShow(true);
		}
	}

	public void showFields(String sectionName, String day) {
		log.info("inside show date field");
		if (NoticeBoardSection.PUBLISHDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setPubToday(false);
			} else {
				this.getManagedBean().setPubYesterday(false);
			}
			this.getManagedBean().setPubShow(false);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(false);
			} else {
				this.getManagedBean().setBreifYesterday(false);
			}
			this.getManagedBean().setBreifShow(false);
		} else if (NoticeBoardSection.SITEVISITDETAILS.name().equalsIgnoreCase(
				sectionName)) {
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

			this.getManagedBean().setTempMinstryRootNode(
					this.getManagedBean().getMinstryRootNode());
			this.getManagedBean().getTempMinstryRootNode().setSelected(false);
			this.getManagedBean().setMinstryRootNode(root);
			this.getManagedBean().setMistrySearchBean(
					new SearchBean<PmOrgProfile>());

		} else if (selectedObject instanceof PmCategoryL1
				|| selectedObject instanceof PmCategoryL2
				|| selectedObject instanceof PmCategoryL3) {

			root = consturctTreeNode(node);
			this.getManagedBean().setTempCategoryRootNode(
					this.getManagedBean().getCategoryRootNode());
			this.getManagedBean().getTempCategoryRootNode()
					.setSelectable(false);
			this.getManagedBean().setCategoryRootNode(root);
			this.getManagedBean().setCategorySearchBean(new SearchBean<>());
		} else if (selectedObject instanceof PmState
				|| selectedObject instanceof PmDistrict
				|| selectedObject instanceof PmCity) {
			root = consturctTreeNode(node);
			this.getManagedBean().setTempLocationRootNode(
					this.getManagedBean().getLocationRootNode());
			this.getManagedBean().getTempLocationRootNode().setSelected(false);
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

					IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
							.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
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

					IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate
							.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
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
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
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
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
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
				IDistrictSO districtSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
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
				ICitySO citySO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
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
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// attendanceBackingBean.retrieveBriefingSiteVisitList(146l);
		}
	}

	private TreeNode consturctTreeNode(TreeNode selectedNode) {
		TreeNode root = new EpDefaultTreeNode("Root", null);
		TreeNode immediateParent = selectedNode.getParent();
		if (immediateParent.equals(root)) {
			new EpDefaultTreeNode(selectedNode.getType(),
					selectedNode.getData(), root);
		} else {
			TreeNode parent = immediateParent.getParent();
			if (parent.getParent() == null) {
				new EpDefaultTreeNode(selectedNode.getType(),
						selectedNode.getData(), new EpDefaultTreeNode(
								immediateParent.getType(),
								immediateParent.getData(), root));

			} else {
				new DefaultTreeNode(selectedNode.getType(),
						selectedNode.getData(), new EpDefaultTreeNode(
								immediateParent.getType(),
								immediateParent.getData(),
								new EpDefaultTreeNode(parent.getType(), parent
										.getData(), root)));

			}
		}
		root.setSelected(true);

		return root;
	}

	public void clearTreeNode(String selectedNode) {
		if (selectedNode.equalsIgnoreCase("ministry")) {
			this.getManagedBean().setMinstryRootNode(
					this.getManagedBean().getTempMinstryRootNode());
			this.getManagedBean().setMistrySearchBean(null);
		} else if (selectedNode.equalsIgnoreCase("category")) {
			this.getManagedBean().setCategoryRootNode(
					this.getManagedBean().getTempCategoryRootNode());
			this.getManagedBean().setCategorySearchBean(null);
		} else if (selectedNode.equalsIgnoreCase("location")) {
			this.getManagedBean().setLocationRootNode(
					this.getManagedBean().getTempLocationRootNode());
			this.getManagedBean().setLocationSearchBean(null);
		}

	}

}
