package my.ep.web.backingbean.sc.noticeboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
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
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.sc.bsvattendance.BSVAttendanceBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.NoticeBoardSearchDataBean;
import my.ep.web.bean.tree.EpDefaultTreeNode;
import my.ep.web.managedbean.sc.noticeboard.NoticeBoardManagedBean;
import my.ep.web.type.sc.quationtender.NoticeBoardSection;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
@ViewScoped
public class NoticeBoardBackingBean extends
		BaseBackingBean<NoticeBoardManagedBean> {
	public static Logger log = Logger.getLogger(NoticeBoardBackingBean.class);
	private static final String NAV_TO_QTN_TENDER_DETAILS_PAGE = "tenderNoticeDetailScreen.xhtml?faces-redirect=true";
	private static final String NAV_TO_NOTICE_BOARD_SEARCH_PAGE = "noticeboardmain.xhtml?faces-redirect=true";

	private IPagingDataModel<ScQt> qtDataModel = new QTPagingDataModel();
	private String mofNo;

	/**
	 * Main Filtration For Notice Board
	 */
	public void searchNoticeBoard() {
		log.info("*** searchNoticeBoard **** ");

		qtDataModel.reloadData();
	}

	/**
	 * Sub Filtration for Tabs
	 */
	/**
	 * @param event
	 */
	public void onTabChange(TabChangeEvent event) {
		log.info("*** TabChange Event **** ");

		PmParameterDesc desc = (PmParameterDesc) event.getData();
		this.getManagedBean().setSelectedTab(
				desc.getPmParameter().getParameterCode());
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
			ScQt scQt = resDTO.getScQt();
			this.getManagedBean().setScQt(resDTO.getScQt());
			this.getManagedBean().setContactPersons(
					resDTO.getContactPersonResultBean().getResultList());
			this.getManagedBean().setCategoryList(
					resDTO.getCategoryResultBean().getResultList());

			
			if (!resDTO.getScQt().isZonal()) {				
				constructLocationTree(resDTO.getLocCriteria());
				this.getManagedBean().setBsv(resDTO.getScQtBsv());
				this.getManagedBean().setBriefingDtls(
						resDTO.getBriefingResultBean().getResultList());
				this.getManagedBean().setSiteVisitDtls(
						resDTO.getSiteVisitResultBean().getResultList());
			} else {	
				this.getManagedBean().setZones(resDTO.getZones());
				

				// }
				// constructLocationTree(resDTO.getLocCriteria());
			}
			// constructBrifingSiteVisitDtls(resDTO);

			/*
			 * // Category List if (resDTO.getCategoryResultBean() != null) {
			 * List<ScQtCategory> catCodelist = resDTO.getCategoryResultBean()
			 * .getResultList();
			 * this.getManagedBean().setCategoryList(catCodelist); }
			 */

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
	public List<PmOrgProfile> retriveOrgProfilesForMinistryLevel() {
		log.info("populate Ministry List");

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgType(OrganizationType.MINISTRY.value());

		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IOrgProfileSO.class);
		OrgProfileResDTO resDTO = orgProfileSO
				.retrieveOrgProfileListByOrgType(reqDTO);

		return resDTO.getOrgProfileList();

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

	/**
	 * Cancel button for Notice Board
	 */
	public String backBtn() {
		log.info("** Back Button **");

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
				this.getManagedBean().getPublishSearchDataBean()
						.setYesterday(true);
			}
			this.getManagedBean().setPubShow(true);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(true);
				this.getManagedBean().getBriefSearchDataBean().setToday(true);
			} else {
				this.getManagedBean().setBreifYesterday(true);
				this.getManagedBean().getBriefSearchDataBean()
						.setYesterday(true);
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
			this.getManagedBean().setPublishSearchDataBean(
					new NoticeBoardSearchDataBean());
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setPubToday(false);
			} else {
				this.getManagedBean().setPubYesterday(false);
			}
			this.getManagedBean().setPubShow(false);
		} else if (NoticeBoardSection.BRIEFINGDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			this.getManagedBean().setBriefSearchDataBean(
					new NoticeBoardSearchDataBean());
			if (day.equalsIgnoreCase("today")) {
				this.getManagedBean().setBreifToday(false);
			} else {
				this.getManagedBean().setBreifYesterday(false);
			}
			this.getManagedBean().setBreifShow(false);
		} else if (NoticeBoardSection.SITEVISITDETAILS.name().equalsIgnoreCase(
				sectionName)) {
			this.getManagedBean().setSiteVisitDataBean(
					new NoticeBoardSearchDataBean());
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
		if (selectedObject instanceof PmState
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

	public void onNodeCollapse(NodeCollapseEvent event) {
		System.out.println("collapse @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	public void onNodeUnselect(NodeUnselectEvent event) {
		System.out.println("unselect @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	public void onNodeExpand(NodeExpandEvent event) {

		Object obj = event.getTreeNode().getData();

		if (event.getTreeNode().getChildCount() <= 0) {
			
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
	if (selectedNode.equalsIgnoreCase("location")) {
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
		if (this.getManagedBean().getMofAccount() == null) {
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
		NoticeBoardResDTO resDTO = qtNoticeBoardSO.retrieveQtNoticeDtl(reqDTO);
		
		if (!resDTO.getScQt().isZonal()) {				
			constructLocationTree(resDTO.getLocCriteria());
			this.getManagedBean().setBsv(resDTO.getScQtBsv());
			this.getManagedBean().setBriefingDtls(
					resDTO.getBriefingResultBean().getResultList());
			this.getManagedBean().setSiteVisitDtls(
					resDTO.getSiteVisitResultBean().getResultList());
		} else {	
			this.getManagedBean().setZones(resDTO.getZones());
			
		}

	
	
		return "/xhtml/sc/noticeboard/nbsupplierreg.xhtml?faces-redirect=true";
	}

	public void hideAndShowDates(String dateType) {
		if (dateType.equalsIgnoreCase("PbDate")) {
			if (this.getManagedBean().getPublishSearchDataBean().getPassDays() != null) {
				this.getManagedBean().setShowPubDate(true);
			} else {
				this.getManagedBean().setShowPubDate(false);
			}
		} else if (dateType.equalsIgnoreCase("SiteVisit")) {
			if (this.getManagedBean().getSiteVisitDataBean().getPassDays() != null) {
				this.getManagedBean().setShowSiteDate(true);
			} else {
				this.getManagedBean().setShowSiteDate(false);
			}
		} else if (dateType.equalsIgnoreCase("Brief")) {
			if (this.getManagedBean().getBriefSearchDataBean().getPassDays() != null) {
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

	private void constructBriefingDtls(List<ScQtBsvDtl> qtBriefingDtls,
			boolean sameAsBsv) {
		if (sameAsBsv) {
			this.getManagedBean().setBriefingDtls(qtBriefingDtls);

		} else {
			if (qtBriefingDtls != null) {
				List<ScQtBsvDtl> briefingDtls = new ArrayList<ScQtBsvDtl>();
				List<ScQtBsvDtl> siteVisitDtls = new ArrayList<ScQtBsvDtl>();
				for (ScQtBsvDtl briefing : qtBriefingDtls) {
					if ("B".equalsIgnoreCase(briefing.getBsvType())) {
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

	private void constructLocationTree(List<TplSpecLoc> locCriterias) {
		List<PmState> states = new ArrayList<PmState>();
		List<TreeNode> locations = new ArrayList<TreeNode>();
		for (TplSpecLoc locCriteria : locCriterias) {
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
			states.add(pmState);
		}
		this.getManagedBean().setLocationNodes(locations);

	}

	public void zoneTabChange(TabChangeEvent changeEvent) {

		ScQt scQt = getManagedBean().getScQt();
		
		 TplSpecZoneDtl specZoneDtl = (TplSpecZoneDtl) changeEvent.getData();
		 NoticeBoardReqDTO boardReqDTO= new NoticeBoardReqDTO();
		
		IQTNoticeBoardSO	qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
		     boardReqDTO.setQtId(scQt.getQtId());
		     boardReqDTO.setZoneId(specZoneDtl.getSpecZoneId());
			NoticeBoardResDTO resDTO = qtNoticeBoardSO.retrieveBSVDtlsByZone(boardReqDTO);
		 	 		
			ScQtBsv scQtBsv = resDTO.getScQtBsv();
			this.getManagedBean().setBsv(scQtBsv);
		  if(null != scQtBsv && scQtBsv.isSameBsv()){			  
			  this.getManagedBean().setBriefingDtls(resDTO.getBriefingResultBean().getResultList());
		  }else{
			  this.getManagedBean().setBriefingDtls(resDTO.getBriefingResultBean().getResultList());
			  this.getManagedBean().setSiteVisitDtls(resDTO.getSiteVisitResultBean().getResultList());
		  }
		  List<TplSpecLoc> locs= resDTO.getLocCriteria();		  
		  constructLocationTree(locs);
		 this.getManagedBean().setContactPersons(resDTO.getContactPersonResultBean().getResultList());
		 
	}

	

	public void showLocationDtls(ScQtBsvDtl briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		this.getManagedBean().setAddress(briefing.getAddress().getAddress1());
	}

	private void constructBrifingSiteVisitDtls(NoticeBoardResDTO resDTO) {
		ScQt scQt = resDTO.getScQt();

		/*
		 * ScQtCriteria scQtCriteria = scQt.getScQtCriterias(); if (scQtCriteria
		 * != null) { if (scQtCriteria.isMultiLocality()) {
		 * 
		 * ISearchResultBean<ScQtZoneCriteria> zoneCriterias = resDTO
		 * .getZoneCriteriaResultBean(); List<ScQtZoneCriteria> zoneCriteria =
		 * zoneCriterias .getResultList();
		 * this.getManagedBean().setZoneList(zoneCriteria); ScQtZoneCriteria
		 * qtZoneCriteria = zoneCriteria.get(0); List<ScQtBriefing> briefingDtls
		 * = qtZoneCriteria .getScQtBriefings();
		 * constructBriefingDtls(briefingDtls, scQt.isSameBsv());
		 * List<ScQtLocCriteria> locations = qtZoneCriteria
		 * .getScQtLocCriterias(); constructLocationTree(locations);
		 * 
		 * } else { ISearchResultBean<ScQtLocCriteria> locCriterias = resDTO
		 * .getLocCriteriaResultBean(); List<ScQtBriefing> qtBriefingDtls = scQt
		 * .getScQtBriefings(); constructBriefingDtls(qtBriefingDtls,
		 * scQt.isSameBsv()); List<ScQtLocCriteria> locations = locCriterias
		 * .getResultList(); constructLocationTree(locations); } }else{
		 * this.getManagedBean().setZoneList(null);
		 * this.getManagedBean().setBriefingDtls(null);
		 * this.getManagedBean().setSiteVisitDtls(null);
		 * this.getManagedBean().setLocationNodes(null); }
		 */
		// this.getManagedBean().setScQtCriteria(scQtCriteria);
		// Qtn/ Tender notice
		// Details

	}

	public String showPriceSchedules(ScQt scqt) {
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage("noticeboardlandingpage");
		
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);

			reqDTO.setQtId(scqt.getQtId());
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(reqDTO);
			this.getManagedBean().setScQt(resDTO.getScQt());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "/xhtml/sc/noticeboard/noticeboardPriceSchedule.xhtml?faces-redirect=true";
	}

	

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public void checkSupplierQulificationStatus() {
		/**
		 * Need to call the Sm api to check the supplier status based on mof no
		 */

	}

	/**
	 * Handle Department dropdown changed - reset selected ptj profile and
	 * populate list of department.
	 * */
	public void handleMinistryProfileChanged() {

		Long parentOrgProfileId = this.getManagedBean().getMinistryProfile()
				.getOrgProfileId();

		if (parentOrgProfileId != null) {
			PmUser user = new FacesScopes().getUserSessionBean();

			if (user != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();

				reqDTO.setUserId(user.getUserId());
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				this.getManagedBean().setPtjList(resDTO.getOrgProfileList());
			}
		} else {
			this.getManagedBean().setPtjList(new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getPtjProfile().setOrgProfileId(null);

	}

	public Long getClosingDays(Date closingDate) {
		if (log.isDebugEnabled()) {
			log.debug("getClosingDays");
		}

		Long closingDays = null;
		Date currentDate = new Date();

		if (closingDate != null && closingDate.after(currentDate)) {
			closingDays = DateUtils.getDaysDifference(currentDate, closingDate);
		}

		return closingDays;
	}

	/**
	 * notice board quotation/tender list paging class
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		private static final long serialVersionUID = 1L;

		public QTPagingDataModel() {
			super("nbsearchresults");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTDetailSO detailSO = null;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				// Filter By - Status
				List<Long> statusIds = new ArrayList<Long>();
				String selectedTab = getManagedBean().getSelectedTab();
				if (ParameterConst.NOTICE_BOARD_STATUS_CHANGES
						.equals(selectedTab)) {
					// TODO filter by changes flag.
				} else {
					if (ParameterConst.NOTICE_BOARD_STATUS_CLOSED
							.equals(selectedTab)) {
						statusIds.add(QTStatusConst.QT_EVALUATION_PROPOSAL);
						statusIds.add(QTStatusConst.QT_SUPPLIER_FINALIZATION);
						statusIds.add(QTStatusConst.QT_LOI_LOA);
					} else if (ParameterConst.NOTICE_BOARD_STATUS_AWARDED
							.equals(selectedTab)) {
						statusIds.add(QTStatusConst.QT_AWARDED);
					} else {
						statusIds.add(QTStatusConst.QT_BRIEFING_SITE_VISIT);
						statusIds.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
					}

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("scQtStatus.current",
									SearchOperator.OPERATOR_EQUALS.value(),
									true));

					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"scQtStatus.pmStatus.statusId",
									SearchOperator.OPERATOR_IN.value(),
									statusIds));
				}

				// Filter By - QT No
				if (StringUtils.isNotBlank(getManagedBean().getQtNo())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("qtNo",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getQtNo()));

				}

				// Filter By - Title
				if (StringUtils.isNotBlank(getManagedBean().getTitle())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("qtTitle",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getTitle(), true));

				}

				// Filter By - Category Code
				PmCategoryL3 pmCategoryL3 = getManagedBean().getCategoryL3();
				if (pmCategoryL3 != null
						&& pmCategoryL3.getCategoryL3Id() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"scQtCategorys.pmCategoryL3.categoryL3Id",
									SearchOperator.OPERATOR_EQUALS.value(),
									pmCategoryL3.getCategoryL3Id()));

				}

				// Filter By - Ministry
				PmOrgProfile ministryProfile = getManagedBean()
						.getMinistryProfile();
				if (ministryProfile != null
						&& ministryProfile.getOrgProfileId() != null) {
					searchBean
							.getDataFilterBeans()
							.add(new SearchFilterBean(
									"pmOrgProfile.parentOrgProfile.parentOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									ministryProfile.getOrgProfileId()));

				}

				// Filter By - PTJ
				PmOrgProfile ptjProfile = getManagedBean().getPtjProfile();
				if (ptjProfile != null && ptjProfile.getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmOrgProfile.orgProfileId",
									SearchOperator.OPERATOR_EQUALS.value(),
									ptjProfile.getOrgProfileId()));

				}

				// Filter By - Publish Date
				NoticeBoardSearchDataBean publishSearchDataBean = getManagedBean()
						.getPublishSearchDataBean();
				if (publishSearchDataBean != null) {
					SearchFilterBean searchFilterBean = getSearchFilterBeanByDataBean(
							"publishDate", publishSearchDataBean);

					if (searchFilterBean != null) {
						searchBean.getDataFilterBeans().add(searchFilterBean);
					}
				}

				// Filter By - Briefing & Site Visit Date
				// TODO cannot search for both.
				NoticeBoardSearchDataBean briefSearchDataBean = getManagedBean()
						.getBriefSearchDataBean();
				NoticeBoardSearchDataBean siteVisitDataBean = getManagedBean()
						.getSiteVisitDataBean();
				if (briefSearchDataBean != null || siteVisitDataBean != null) {
					String searchField = "scQtBriefings.scQtBsvDtls.bsvDate";
					SearchFilterBean briefSearchFilterBean = getSearchFilterBeanByDataBean(
							searchField, briefSearchDataBean);
					SearchFilterBean siteVisitSearchFilterBean = getSearchFilterBeanByDataBean(
							searchField, siteVisitDataBean);

					if (briefSearchFilterBean != null
							|| siteVisitSearchFilterBean != null) {
						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("isBsvRequired",
										SearchOperator.OPERATOR_EQUALS.value(),
										true));

						List<String> bsvTypes = new ArrayList<String>();
						bsvTypes.add("A");

						if (briefSearchFilterBean != null) {
							bsvTypes.add("B");

							searchBean.getDataFilterBeans().add(
									briefSearchFilterBean);
						}

						if (siteVisitSearchFilterBean != null) {
							bsvTypes.add("S");

							searchBean.getDataFilterBeans().add(
									siteVisitSearchFilterBean);
						}

						searchBean.getDataFilterBeans().add(
								new SearchFilterBean(
										"scQtBriefings.scQtBsvDtls.bsvType",
										SearchOperator.OPERATOR_IN.value(),
										bsvTypes));
					}
				}

				// Filter By - Closing Date
				NoticeBoardSearchDataBean clsDataBean = getManagedBean()
						.getClsDataBean();
				if (clsDataBean != null) {
					SearchFilterBean searchFilterBean = getSearchFilterBeanByDataBean(
							"closingDate", clsDataBean);

					if (searchFilterBean != null) {
						searchBean.getDataFilterBeans().add(searchFilterBean);
					}
				}

				// Filter By - Location
				TreeNode locationRootNode = getManagedBean()
						.getLocationRootNode();
				if (locationRootNode != null) {
					TreeNode treeNode = locationRootNode;

					// Get the last child node.
					while (treeNode != null
							&& treeNode.getChildren().size() > 0) {
						treeNode = treeNode.getChildren().iterator().next();
					}

					if (treeNode != null) {
						if (treeNode.getType().equals("state")) {
							searchBean
									.getDataFilterBeans()
									.add(new SearchFilterBean(
											"scQtSupplierCriteria.scQtLocCriteria.pmState.stateId",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmState) treeNode.getData())
													.getStateId()));
						} else if (treeNode.getType().equals("division")) {
							searchBean
									.getDataFilterBeans()
									.add(new SearchFilterBean(
											"scQtSupplierCriteria.scQtLocCriteria.pmDivision.divisionId",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmDivision) treeNode.getData())
													.getDivisionId()));
						} else if (treeNode.getType().equals("district")) {
							searchBean
									.getDataFilterBeans()
									.add(new SearchFilterBean(
											"scQtSupplierCriteria.scQtLocCriteria.pmDistrict.districtId",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmDistrict) treeNode.getData())
													.getDistrictId()));
						} else if (treeNode.getType().equals("city")) {
							searchBean
									.getDataFilterBeans()
									.add(new SearchFilterBean(
											"scQtSupplierCriteria.scQtLocCriteria.pmDistrict.pmCities.cityId",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmCity) treeNode.getData())
													.getCityId()));
						}

					}
				}

				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setQtSearchBean(searchBean);

				QuationTenderResDTO qtResDTO = detailSO
						.getQuationTenders(qtReqDTO);

				System.out.println("qtResDTO:"
						+ qtResDTO.getQtResultBean().getTotalRecord());
				return qtResDTO.getQtResultBean();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				detailSO = null;
			}

			return null;
		}
	}

	private SearchFilterBean getSearchFilterBeanByDataBean(String searchField,
			NoticeBoardSearchDataBean searchDataBean) {
		SearchFilterBean searchFilterBean = new SearchFilterBean();

		if (searchDataBean.isToday()) {
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
					.value());
			searchFilterBean.setSearchValue(new Date());
		} else if (searchDataBean.isYesterday()) {
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
					.value());
			searchFilterBean.setSearchValue(DateUtils.minusDays(new Date(), 1));
		} else if (searchDataBean.getPassDays() != null) {
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS
					.value());
			searchFilterBean.setSearchValue(DateUtils.minusDays(new Date(),
					searchDataBean.getPassDays()));
		} else if (searchDataBean.getFromDate() != null
				&& searchDataBean.getToDate() != null) {
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
					.value());
			searchFilterBean.setSearchValue(new SearchBetweenValue<Date>(
					searchDataBean.getFromDate(), searchDataBean.getToDate()));
		}

		if (searchFilterBean.getSearchValue() == null) {
			return null;
		}

		searchFilterBean.setSearchField(searchField);

		return searchFilterBean;
	}

	/**
	 * @return the qtDataModel
	 */
	public IPagingDataModel<ScQt> getQtDataModel() {
		return qtDataModel;
	}

	/**
	 * @param qtDataModel
	 *            the qtDataModel to set
	 */
	public void setQtDataModel(IPagingDataModel<ScQt> qtDataModel) {
		this.qtDataModel = qtDataModel;
	}

}
