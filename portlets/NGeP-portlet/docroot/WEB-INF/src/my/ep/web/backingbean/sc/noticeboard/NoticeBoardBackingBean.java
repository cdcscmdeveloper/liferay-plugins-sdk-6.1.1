/* Copyright © 2014 Archisoft. All rights reserved. */
package my.ep.web.backingbean.sc.noticeboard;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.sc.SupplierQualificationBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTNoticeBoardSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.NoticeBoardReqDTO;
import my.ep.dto.sc.quationtender.NoticeBoardResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sc.common.ScRequestItem;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvAttendance;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvRegistration;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReportAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.pm.cc.CategoryCodeSearchBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.AwardedItemsBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.NoticeBoardSearchDataBean;
import my.ep.web.bean.tree.EpDefaultTreeNode;
import my.ep.web.managedbean.sc.noticeboard.NoticeBoardManagedBean;
import my.ep.web.type.sc.quationtender.NoticeBoardSection;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.tpl.helper.TemplateSpecHelper;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;

import com.liferay.portal.util.PortalUtil;

/**
 * Notice Board Backing Bean class displays the status of 
 * 'Publish,Changes,Closed,Awarded' Quotation/Tender's in Notice Board screen.
 *  
 * @author sri 
 *
 */
@ManagedBean(name = "noticeBoardBackingBean")
@ViewScoped
public class NoticeBoardBackingBean extends
		SourcingBaseBackingBean<NoticeBoardManagedBean> {
	public static Logger log = Logger.getLogger(NoticeBoardBackingBean.class);
	private static final String NAV_TO_QTN_TENDER_DETAILS_PAGE = "nbQuotationNTendenDetail.xhtml?faces-redirect=true";
	private static final String MOF_INVALID_MSG_KEY = "lbl.sc.mof.invalid.orinactive";
	private static final String CAP_CHAR = "^";
	private static final String MAIN_PAGE = "nbMainPage";
	private static final String QT_DETAILS_PAGE = "/xhtml/sc/noticeboard/qtdetails/nbQuotationNTendenDetail.xhtml";
	private static final String CHECK_SUPPLIER_QUAL_PAGE = "/xhtml/sc/noticeboard/supplierqualification/nbSupplierQulificationCriteria.xhtml";
	private static final String PRICE_SCHEDULE_PAGE ="/xhtml/sc/noticeboard/priceshedule/nbPriceSchedule.xhtml";
	private static final String VIEW_AWARD_SUPP_PAGE = "/xhtml/sc/noticeboard/awardsupplier/nbAwardSupplierView.xhtml";
	private static final String BSV_SUPP_REGISTRATION_PAGE = "/xhtml/sc/noticeboard/bsvregistration/nbSupplierRegistration.xhtml";
	private static final String NB_MAIN_PAGE = "/xhtml/sc/noticeboard/resultview/nbMainPage.xhtml";
	private static final String REGIST_MSG_KEY = "lbl.sc.notice.board.regist.msg";
	private static final String MOBILE_NB_MAIN_PAGE = "/xhtml/sc/noticeboard/mobileview/nbMainPage.xhtml";
	
	private IPagingDataModel<Object[]> qtDataModel = new QTPagingDataModel();
	private IPagingDataModel<ScQtCategory> categoryCodesDataModel = new QTCategoryCodesDataModel();
	private boolean visible;
	private boolean displayImage;
	private String slectedHeaderName;
	
	
	public NoticeBoardBackingBean() {
		
	}
	
	/**
	 * Main Filtration For Notice Board
	 */
	public void searchNoticeBoard() {
		
		log.info("*** searchNoticeBoard **** ");
		qtDataModel.reloadData();
	}

	/**
	 * Will be called on tab change event.
	 * 
	 * @param event
	 * 			the instance of TabChangeEvent
	 */
	public void onTabChange(TabChangeEvent event) {
		
		log.info("*** TabChange Event **** ");
		PmParameterDesc desc = (PmParameterDesc) event.getData();
		this.getManagedBean().setSelectedTab(
				desc.getPmParameter().getParameterCode());
		searchNoticeBoard();
	}

	
	/**
	 * Get Quotation/Tender details on click of QT Title link
	 * 
	 * @param qtId
	 * 			the Long variable
	 */
	public void retrieveQTDetails(Long qtId) {
		
		log.info("** Qtn / TenderDetsils **");
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage(MAIN_PAGE);
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			reqDTO.setQtId(qtId);
			this.getManagedBean().setQtId(qtId);
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveQtNoticeDtl(reqDTO);
			ScQt scQt = resDTO.getScQt();

			this.getManagedBean().setScQt(resDTO.getScQt());
			this.getManagedBean().setContactPersons(
					resDTO.getContactPersonResultBean().getResultList());

			// Setting ministry name
			this.getManagedBean().setMinistryName(
					retrieveMinistryByOrgProfileId(scQt.getPmOrgProfile()
							.getOrgProfileId()));
			
			// Get sample submission details
			List<ScQtSampleSubmission> sampleAddrList = resDTO
					.getSampleSubmissionDtls();
			
			if (!scQt.isZonal()) {
				if (null != resDTO.getLocCriteria()) {

					this.getManagedBean().setLocationNodes(
							LocationSingleton.getInstance().constuctTreeNode(
									resDTO.getLocCriteria()));

				}
				ScQtBsv scQtBsv = resDTO.getScQtBsv();
				
				ScQtSampleSubmission sampleAddr = null;
				if (sampleAddrList != null && !sampleAddrList.isEmpty()) {
						sampleAddr = sampleAddrList.get(0);
				}
				
				BriefingZoneBean briefingZoneBean = new BriefingZoneBean(null,
						resDTO.getBriefingResultBean(),
						resDTO.getSiteVisitResultBean(), scQtBsv, sampleAddr);
				this.getManagedBean().setNormalBsvBean(briefingZoneBean);
			} else if (scQt.isZonal()) {
				List<TplSpecZoneDtl> zoneDtls = resDTO.getZones();
				// Need to construct tab bean for zone
				
				List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();

				if (null != zoneDtls) {
					boolean isBsvRequired = scQt.isBsvRequired();
					List<TreeNode> zoneTree = new ArrayList<TreeNode>();
					
					for (TplSpecZoneDtl specZoneDtl : zoneDtls) {
						if(!isBsvRequired){
						 zoneTree.add(LocationSingleton.getInstance()
										.constuctTreeNode(
												specZoneDtl,specZoneDtl.getTplSpecZoneLocs()));
						}
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(scQt.getQtId());
						briefingReqDTO.setSpecZoneId(specZoneDtl
								.getSpecZoneId());
						briefingReqDTO.setAttandanceReq(true);
						IQTBriefingSO briefingSO = EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.SUPPLIER_SELECTION,
										IQTBriefingSO.class);
						BriefingResDTO briefingResDTO = briefingSO
								.retrieveBriefingSiteVisitDtls(briefingReqDTO);
						ScQtSampleSubmission sampleAddress = null; 
						for (ScQtSampleSubmission sampleaddr : sampleAddrList) {
							TplSpecZoneDtl addrZoneDtl = sampleaddr.getTplSpecZoneDtl();
							if (addrZoneDtl != null && specZoneDtl
									.getSpecZoneId().equals(addrZoneDtl.getSpecZoneId())) {
								sampleAddress = sampleaddr;
							}
						}
						BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
								specZoneDtl, briefingResDTO.getBriefingList(),
								briefingResDTO.getSiteVisitList(),
								briefingResDTO.getScQtBsv(),sampleAddress);
						bsvZoneBeans.add(briefingZoneBean);
					}
					this.getManagedBean().setLocationNodes(
							LocationSingleton.getInstance().constuctTreeNode(
									zoneDtls.get(0).getTplSpecZoneLocs()));
					if(!isBsvRequired){
						this.getManagedBean().setZoneTree(zoneTree);
					}
				}

				this.getManagedBean().setZoneBean(bsvZoneBeans);
				this.getManagedBean().setNormalBsvBean(bsvZoneBeans.get(0));
			}
			
			getManagedBean().setScQtDocReports(resDTO.getScQtDocReports());
			String selectedTab = getManagedBean().getSelectedTab();
			
			if (ParameterConst.NOTICE_BOARD_STATUS_CHANGES.equals(selectedTab)) {
				populateQtAmendIntoAmendmentBeanMap(resDTO.getQtAmendMentBeans());
			} else {
				this.getManagedBean().setAmendmentDateMap(null);
			}
		} finally {
			qtNoticeBoardSO = null;
		}

		// this.getManagedBean().setTabIndex(0);
		String pageUrl = QT_DETAILS_PAGE;
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler()
				.handleNavigation(context, null, pageUrl);
	}
	
	private void populateQtAmendIntoAmendmentBeanMap(List<QtAmendMentBean> qtAmendMentBeans) {
		Map<String, Date> amendmentBeanMap = new HashMap<String, Date>();
		
		if (CollectionUtils.isNotEmpty(qtAmendMentBeans)) {
			for (QtAmendMentBean amendMentBean : qtAmendMentBeans) {
				if (amendMentBean.getValueMap() != null) {
					for (Entry<String, QtAmendMentValueBean> amendMentBeanValue : amendMentBean.getValueMap().entrySet()) {
						QtAmendMentValueBean valueBean = amendMentBeanValue.getValue();
						Date approvalDate = valueBean.getApprovalDate();
						if (approvalDate != null && BooleanUtils.isTrue(valueBean.getIsApproved())) {
							Date prevApprovalDate = amendmentBeanMap.get(amendMentBeanValue.getKey());
							if (prevApprovalDate != null && prevApprovalDate.after(approvalDate)) {
								continue;
							}
							amendmentBeanMap.put(amendMentBeanValue.getKey(), approvalDate);	
						}
					}
					
				}
			}	
		}
		
		this.getManagedBean().setAmendmentDateMap(amendmentBeanMap);
	}

	/**
	 * Drop down for Ministry
	 */
	public List<PmOrgProfile> retriveOrgProfilesForMinistryLevel() {
		
		log.info("populate Ministry List");

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		// reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
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
				reqDTO.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
				StateResDTO resDTO = stateSO
						.retriveStatesListByCountryCode(reqDTO);

				if (resDTO != null && !resDTO.getStateList().isEmpty()) {

					TreeNode node = this.getManagedBean().getLocationRootNode();
					if (node == null) {
						node = new EpDefaultTreeNode("Root", null);
						for (PmState state : resDTO.getStateList()) {
							new EpDefaultTreeNode("state", state, node);
						}
					}

					this.getManagedBean().setLocationRootNode(node);
				}
			} finally {
			}

		}
	}

	/**
	 * Cancel button for Notice Board
	 */
	public void backBtn() {
		log.info("** Back Button **");
		this.getManagedBean().setMofNumber(null);
		this.getManagedBean().setCompanyName(null);
		String pageUrl = "/xhtml/sc/noticeboard/resultview/"
				+ this.getManagedBean().getPreviousPage();
		if(this.getManagedBean().isNavigateFromMobileView()){
			pageUrl=MOBILE_NB_MAIN_PAGE;
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler()
				.handleNavigation(context, null, pageUrl+"?faces-redirect=true");

	}

	/**
	 * qtnTenderDocs
	 * return the quotation tender details page.
	 */
	public String qtnTenderDocs() {
		log.info("** qtnTenderDocs **");
		this.getManagedBean().setTabIndex(2);
		return NAV_TO_QTN_TENDER_DETAILS_PAGE;
	}

	/**
	 * hide fields based on sectionName and day.
	 * @param sectionName
	 * @param day
	 * 
	 */
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

	/**
	 * show fields based on sectionName and day.
	 * @param sectionName
	 * @param day
	 * 
	 */
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

	/**
	 * treenode select checkbox event.
	 * @param event
	 * 
	 * 
	 */
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

	/**
	 * treenode collapse event.
	 * @param event
	 * 
	 * 
	 */
	public void onNodeCollapse(NodeCollapseEvent event) {
		System.out.println("collapse @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	/**
	 * treenode Unselect checkbox event.
	 * @param event
	 * 
	 * 
	 */
	public void onNodeUnselect(NodeUnselectEvent event) {
		System.out.println("unselect @@@@@@@@@@@@@ " + event.getTreeNode());
	}

	/**
	 * treenode expand event.
	 * @param event
	 * 
	 * 
	 */
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
					new DefaultTreeNode("district", district, root);
				}
			}
		}
	}

	/**
	 * method to get parameter Description by parametertype.
	 * @param type
	 * 
	 * @return the list of parameter descriptions.
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * @param selectedNode
	 *  		the instance of TreeNode
	 * @return TreeNode
	 */
	private TreeNode consturctTreeNode(TreeNode selectedNode) {
		TreeNode root = new EpDefaultTreeNode("Root", null);
		TreeNode immediateParent = selectedNode.getParent();
		selectedNode.setExpanded(true);
		// selectedNode.setSelected(true);
		if (immediateParent.equals(root)) {
			TreeNode tempNode = new EpDefaultTreeNode(selectedNode.getType(),
					selectedNode.getData(), root);
			tempNode.setExpanded(true);
			tempNode.setSelected(true);
		} else {
			TreeNode parent = immediateParent.getParent();
			if (parent.getParent() == null) {
				TreeNode nextParent = new EpDefaultTreeNode(
						immediateParent.getType(), immediateParent.getData(),
						root);
				nextParent.setExpanded(true);
				nextParent.setSelected(true);
				TreeNode nexTempNode = new EpDefaultTreeNode(
						selectedNode.getType(), selectedNode.getData(),
						nextParent);
				nexTempNode.setExpanded(true);
				nexTempNode.setSelected(true);

			} else {

				TreeNode nextParent = new EpDefaultTreeNode(parent.getType(),
						parent.getData(), root);
				nextParent.setExpanded(true);
				TreeNode subParent = new EpDefaultTreeNode(
						immediateParent.getType(), immediateParent.getData(),
						nextParent);
				subParent.setExpanded(true);
				subParent.setSelected(true);
				TreeNode subTempNode = new DefaultTreeNode(
						selectedNode.getType(), selectedNode.getData(),
						subParent);
				subTempNode.setExpanded(true);
				subTempNode.setSelected(true);

			}
		}

		return root;
	}

	/**
	 * Clear previous nodes on click of tree nodes.
	 * 
	 * @param selectedNode
	 * 			the String variable	
	 */
	public void clearTreeNode(String selectedNode) {
		
		if (selectedNode.equalsIgnoreCase("location")) {
			this.getManagedBean().setLocationSelectedNode(null);
			TreeNode rootNode = this.getManagedBean().getTempLocationRootNode();

			if (rootNode != null) {
				List<TreeNode> childNodes = rootNode.getChildren();

				for (TreeNode node : childNodes) {
					if (node.isExpanded()) {
						node.setExpanded(false);
					}
				}
			}

			this.getManagedBean().setLocationRootNode(
					this.getManagedBean().getTempLocationRootNode());
			this.getManagedBean().setTempLocationRootNode(null);
			this.getManagedBean().setLocationSearchBean(null);
		}

	}

	/**
	 * Navigate to supplier qualification criteria screen 
	 * on click of check supplier icon.
	 * 
	 * @param qtId
	 *  		the Long variable
	 * @return navigateUrl
	 */
	public String gotoSupplierQualificationCriteria(Long qtId) {

		this.getManagedBean().setPreviousPage(MAIN_PAGE);
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		reqDTO.setQtId(qtId);
		NoticeBoardResDTO resDTO = getNBResDTO(reqDTO);
		/*ScQt scQt = new ScQt();
		scQt.setQtId(qtId);*/
		this.getManagedBean().setScQt(resDTO.getScQt());
		this.getManagedBean().setMofNo(null);
		String mofNo = this.getManagedBean().getSupplierMofNo();
		if(null != mofNo){
			this.getManagedBean().setMofNo(mofNo);
		}
		return CHECK_SUPPLIER_QUAL_PAGE;
	}
	
	/**
	 * Pre-render view to navigate porlet to bsv supplier registration screen
	 */
	public void preRenderBsvRegistration() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String qtId = originalServletRequest.getParameter(SourcingConstants.NAV_PARAM_QUOTATION_TENDER_ID);
		
		super.renewSesionObject();
		
		isUser();
		
		getManagedBean().setNavigateFromNoticeBoard(false);

		String url = gotoSupplierRegistration(Long.parseLong(qtId));
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, url);
	}
	
	/**
	 * Navigate to NoticeBoard view page on click of NoticeBoard tab
	 */
	public void preRenderNBView() {
		
		super.renewSesionObject();
		searchNoticeBoard();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NB_MAIN_PAGE+"?faces-redirect=true");
	}
	
	/**
	 * Navigate to NoticeBoard view page on click of NoticeBoard tab
	 */
	public void preRenderMobileView() {
		
		super.renewSesionObject();
		new CategoryCodeSearchBackingBean().renewSesionObject();
		searchNoticeBoard();
		this.getManagedBean().setNavigateFromMobileView(true);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, MOBILE_NB_MAIN_PAGE+"?faces-redirect=true");
	}
	
	/**
	 * Navigate to supplier qualification criteria page when click on liferay menu
	 */
	public void preRenderSuppQualView() {
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String qtId = originalServletRequest
				.getParameter(SourcingConstants.NAV_PARAM_QUOTATION_TENDER_ID);
		
		if (StringUtils.isNotBlank(qtId)) {
			super.renewSesionObject();
			isUser();
			getManagedBean().setNavigateFromNoticeBoard(false);
			String redirectUrl = gotoSupplierQualificationCriteria(Long.valueOf(qtId));
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, redirectUrl);	
		}
	}
	
	/**
	 * Navigate to supplier registration screen on click of register icon
	 * 
	 * @param qtId
	 * 			the Long Variable
	 * @return navigateUrl
	 */
	public String gotoSupplierRegistration(Long qtId) {
		
		NoticeBoardManagedBean managedBean = this.getManagedBean();
		managedBean.setPreviousPage(MAIN_PAGE);
		this.getManagedBean().setSelectedZoneTabIndex(0);
		retriveBsvDetails(qtId);
		SmSupplier smSupplier = managedBean.getSmSupplier();
		managedBean.setMofNumber(managedBean.getSupplierMofNo());
		if (smSupplier != null && null != managedBean.getMofNumber()) {
			managedBean.setCompanyName(smSupplier.getCompanyName());
			managedBean.setValidMofNo(true);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							null, ResourceBundleUtils.getMessage(
									REGIST_MSG_KEY, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())));
		} else {
			managedBean.setValidMofNo(false);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							null, ResourceBundleUtils.getMessage(
									MOF_INVALID_MSG_KEY, 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())));
							
		}
		//managedBean.setActiveTabIndex(0);
		//managedBean.setSelectedZoneTabIndex(0);
		managedBean.setBriefingDtls(null);
		
		return BSV_SUPP_REGISTRATION_PAGE;
	}

	
	/**
	 * User click on zones in bsvZones tab.
	 * 
	 *
	 */ 
	public void bsvZoneTabChangeAction() {
		if (log.isDebugEnabled()) {
			log.debug("bsvZoneTabChangeAction");
		}
		Integer selectedZoneTabIndex = Integer.valueOf(FacesContext
				.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("selectedZoneTabIndex"));
		
		this.getManagedBean().setBriefingDtls(null);
		BriefingZoneBean selectedBsvBean = this.getManagedBean().getZoneBean()
				.get(selectedZoneTabIndex);

		this.getManagedBean().setNormalBsvBean(selectedBsvBean);
		this.getManagedBean().setSelectedZoneTabIndex(selectedZoneTabIndex);
		this.getManagedBean().setRenderRegisterBtn(this.getManagedBean().isDisableRegisterBtn());
		setSlectedHeaderName(null);
	}

	/**
	 * hide or show date fields based on date types.
	 * 
	 * @param dateType
	 * 			the String variable	
	 */
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

	/**
	 * Reset all required form values on click of 'Reset' button.
	 */
	public void reset() {

		this.getManagedBean().setQtNo(null);
		this.getManagedBean().setTitle(null);
		this.getManagedBean().setClsDataBean(new NoticeBoardSearchDataBean());
		this.getManagedBean().setShowPubDate(false);
		this.getManagedBean().setShowSiteDate(false);
		this.getManagedBean().setShowBriDate(false);
		this.getManagedBean().setPubShow(false);
		this.getManagedBean().setPubToday(false);
		this.getManagedBean().setPubYesterday(false);
		this.getManagedBean().setBreifToday(false);
		this.getManagedBean().setBreifYesterday(false);
		this.getManagedBean().setBreifShow(false);
		this.getManagedBean().setSvToday(false);
		this.getManagedBean().setSvYesterday(false);
		this.getManagedBean().setSvShow(false);
		this.getManagedBean().setCategoryL3(new PmCategoryL3());
		this.getManagedBean().setMinistryProfile(new PmOrgProfile());
		resetPtjGroupOrgProfile();
		this.getManagedBean().getSiteVisitDataBean().setPassDays(0);
		this.getManagedBean().getPublishSearchDataBean().setPassDays(0);
		this.getManagedBean().setPublishSearchDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setBriefSearchDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setSiteVisitDataBean(
				new NoticeBoardSearchDataBean());
		this.getManagedBean().setClsDataBean(new NoticeBoardSearchDataBean());
		clearTreeNode("location");
	}
	
	/**
	 * Reset Ptj Group Org Profile
	 */
	public void resetPtjGroupOrgProfile() {
		this.getManagedBean().setPtjGroupProfile(new PmOrgProfile());
		resetPtjOrgProfile();
	}
	
	/**
	 * Reset Ptj Org Profile
	 */
	public void resetPtjOrgProfile() {
		this.getManagedBean().setPtjProfile(new PmOrgProfile());
	}

	/**
	 * Method will be called on Zone tab change
	 * 
	 * @param changeEvent
	 *        the instance of TabChangeEvent
	 */
	public void zoneTabChange(TabChangeEvent changeEvent) {
		this.getManagedBean().setBriefingDtls(null);
		TplSpecZoneDtl specZoneDtl = ((BriefingZoneBean) changeEvent.getData())
				.getTplSpecZoneDtl();

		this.getManagedBean().setLocationNodes(
				LocationSingleton.getInstance().constuctTreeNode(
						specZoneDtl.getTplSpecZoneLocs()));

	}

	/**
	 * Set location,address from Bsv Details bean
	 * 
	 * @param briefing
	 *        the instance of ScQtBsvDtl 
	 */
	public void showLocationDtls(ScQtBsvDtl briefing) {
		this.getManagedBean().setLocation(briefing.getLocation());
		/*StringBuffer address = new StringBuffer(briefing.getAddress()
				.getAddress1());

		if (!StringUtils.isBlank(briefing.getAddress().getAddress2())) {
			address.append(",");
			address.append(briefing.getAddress().getAddress2());

		}
		if (!StringUtils.isBlank(briefing.getAddress().getAddress3())) {
			address.append(",");
			address.append(briefing.getAddress().getAddress3());

		}
		if (!StringUtils.isBlank(briefing.getAddress().getAddress4())) {
			address.append(",");
			address.append(briefing.getAddress().getAddress4());

		}
		briefing.getAddress().getPmState().getStateName()*/
		this.getManagedBean().setBsvDetails(briefing);
	}

	/**
	 * Method is used to display price schedule details screen
	 * on click of 'Show Price Schedule' link
	 * 
	 * @param qtId
	 *        the Long variable. 
	 * @return naviagteUrl
	 */
	public String showPriceSchedules(Long qtId) {
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		ScQtCommittee evalCommitte = null;
		
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage(MAIN_PAGE);

		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			reqDTO.setQtId(qtId);
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveOfferedPriceDtls(reqDTO);
			ScQt scQt = resDTO.getScQt();
			this.getManagedBean().setScQt(resDTO.getScQt());
			Long orgProfileId = scQt.getPmOrgProfile().getOrgProfileId();
					
			// Setting ministry name
			this.getManagedBean().setMinistryName(
					retrieveMinistryByOrgProfileId(orgProfileId));
			
			// Setting PTJ Group/ Jabatan
			this.getManagedBean().setJabatan(
					retrieveJabatanByOrgProfileId(orgProfileId));
			
			if (resDTO.getScQt().isZonal()) {
				this.getManagedBean().setZones(resDTO.getZones());
				reqDTO.setZoneId(resDTO.getZones().get(0).getSpecZoneId());
				NoticeBoardResDTO resDto = qtNoticeBoardSO.retrieveOfferedPriceDtlsByZone(reqDTO);
				this.getManagedBean().setQualificationBean(resDto.getQualificationBean());
			} else {
				this.getManagedBean().setQualificationBean(
						resDTO.getQualificationBean());
			}
			
			// Get EC members for 1 tier & OC for 2 tier
			if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(this
					.getManagedBean().getScQt().getEvaluationType())) {
				evalCommitte = getQtCommiteDtlsByQtIdNType(
						ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE, qtId);

			} else {
				evalCommitte = getQtCommiteDtlsByQtIdNType(
						ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE, qtId);
			}
			if (null != evalCommitte) {
				
				ScCommittee committee = evalCommitte.getScCommittee();
				this.getManagedBean()
						.setCommitteeName(getParameterDesc(ParameterTypeConst.SC_COMMITTEE_TYPE,
										ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE));
				this.getManagedBean().setScCommitteeMembers(
						committee != null ? committee.getScCommitteeMembers(): null);
			}
			
			
		} finally {
		}
		return PRICE_SCHEDULE_PAGE;
	}

	/**
	 * Verify whether Mofno is valid or not.
	 */
	public void checkSupplierQualificationStatus() {
		
		this.getManagedBean().setQualificationBean(null);
		this.getManagedBean().setProposalURL(null);
		String mofNo = this.getManagedBean().getMofNo();
		setVisible(false);
		IQTNoticeBoardSO boardSO = null;
		if (null == mofNo || "".equalsIgnoreCase(mofNo)) {
			throw new EpException(MessageConst.ERROR_760297);
		}
		ScQt scQt = this.getManagedBean().getScQt();
		final Long qtId = scQt.getQtId();
		Long currentStatus = scQt.getQtStatus().getStatusId();
		try {
			
			boardSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTNoticeBoardSO.class);
			NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
			reqDTO.setMofNo(mofNo);
			reqDTO.setQtId(qtId);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			NoticeBoardResDTO resDTO = boardSO
					.checkSupplierQualificationStatus(reqDTO);
			this.getManagedBean().setQualificationBean(
					resDTO.getQualificationBean());
			if (null != resDTO.getQualificationBean()) {
				this.getManagedBean().setQualified(true);
				for (SupplierQualificationBean qualificationBean : resDTO
						.getQualificationBean()) {
					if ((!SourcingConstants.SUPPLIER_CRITERIA_TYPE_SOFT_CERTIFICATE
							.equalsIgnoreCase(qualificationBean
									.getCriteria()))
							&& !qualificationBean.isSupplierStatus()) {
						this.getManagedBean().setQualified(false);
						break;
					}
				}

				setVisible(true);
			}
			
			SmSupplier supplier = resDTO.getSupplier();
			boolean isValidSupplier = false;
			if (supplier != null && this.getManagedBean().isQualified()) {
				NoticeBoardReqDTO supplierReqDTO = new NoticeBoardReqDTO();
				supplierReqDTO.setSupplierId(supplier.getSupplierId());
				supplierReqDTO.setQtId(qtId);
				NoticeBoardResDTO supplierResDTO = boardSO
						.retrieveQtSupplierByQtIdNSupplierId(supplierReqDTO);
				if(supplierResDTO.isSuccessful()){
					// Value will be present when supplier rejected for prepare proposal
					// while doing bsv registration by DO
					if (null == supplierResDTO.getScQtSupplier()
							.getDisqualifiedStage()) {
						isValidSupplier = true;
					}
				}
			}
			
			// construct the proposal/update url
			if (QTStatusConst.QT_PROPOSAL_PREPARATION
					.equals(currentStatus) && isValidSupplier ) {
					
				StringBuffer proposalURL = new StringBuffer(85);
				proposalURL.append(EpPropertiesUtils.getInstance().getProperty(
						EpPropertiesConst.PORTAL_APPLICATION_URL));
				proposalURL.append(SourcingConstants.NAV_PAGE_SUPPLIER_PROPOSAL);
				proposalURL.append(qtId);
				this.getManagedBean().setProposalURL(proposalURL.toString());
			}
		} finally {

		}

	}

	/**
	 * Handle Department drop down changed - reset selected ptj profile and
	 * populate list of department.
	 * */
	public void handleMinistryProfileChanged() {

		Long parentOrgProfileId = this.getManagedBean().getMinistryProfile()
				.getOrgProfileId();
		if (parentOrgProfileId != null) {
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			reqDTO.setParentOrgProfileId(parentOrgProfileId);

			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			this.getManagedBean().setPtjList(resDTO.getOrgProfileList());
		} else {
			this.getManagedBean().setPtjList(new ArrayList<PmOrgProfile>());
		}

		this.getManagedBean().getPtjProfile().setOrgProfileId(null);

	}

	/**
	 * notice board quotation/tender list paging class
	 */
	private class QTPagingDataModel extends PagingDataModel<Object[]> {

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
		public ISearchResultBean<Object[]> pageChangeListener(
				ISearchBean<Object[]> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			IQTNoticeBoardSO qtNoticeBoardSO = null;
			try {
				qtNoticeBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTNoticeBoardSO.class);

				// Filter By - Status
				List<Long> statusIds = new ArrayList<Long>();
				String selectedTab = getManagedBean().getSelectedTab();
				if (ParameterConst.NOTICE_BOARD_STATUS_CHANGES
						.equals(selectedTab)) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("IS_CHANGED",
									SearchOperator.OPERATOR_EQUALS.value(), 1));
					statusIds.add(QTStatusConst.QT_BRIEFING_SITE_VISIT);
					statusIds.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
					statusIds.add(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE);
					setDisplayImage(isUser());

				} else if (ParameterConst.NOTICE_BOARD_STATUS_CLOSED
						.equals(selectedTab)) {
					List<Long> orderedStatuses = Arrays.asList(QTStatusConst.QT_ORDERED_STATUSES);
					orderedStatuses = orderedStatuses.subList(
							orderedStatuses.indexOf(QTStatusConst.QT_CLOSED), 
							orderedStatuses.indexOf(QTStatusConst.QT_AWARDED));
					
					statusIds.addAll(orderedStatuses);
				} else if (ParameterConst.NOTICE_BOARD_STATUS_AWARDED
						.equals(selectedTab)) {
					statusIds.add(QTStatusConst.QT_AWARDED);
				} else if (ParameterConst.NOTICE_BOARD_STATUS_CANCELLED
						.equals(selectedTab)) {
					statusIds.add(QTStatusConst.QT_CANCELLED);
					
				} else {
					
					statusIds.add(QTStatusConst.QT_BRIEFING_SITE_VISIT);
					statusIds.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
					statusIds.add(QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE);
					setDisplayImage(isUser());

				}
				
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("status.IS_CURRENT",
								SearchOperator.OPERATOR_EQUALS.value(), 1));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("pmStatus.STATUS_ID",
								SearchOperator.OPERATOR_IN.value(), statusIds));
				
				if (ParameterConst.NOTICE_BOARD_STATUS_CANCELLED
						.equals(selectedTab)) {
					
					List<Long> statusIdList = new ArrayList<>(2);
					statusIdList.add(QTStatusConst.QT_BRIEFING_SITE_VISIT);
					statusIdList.add(QTStatusConst.QT_PROPOSAL_PREPARATION);
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("wfStatus.STATUS_ID",
									SearchOperator.OPERATOR_IN.value(),statusIdList));
				}
				
				// Filter By - QT No
				if (StringUtils.isNotBlank(getManagedBean().getQtNo())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("QT_NO",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getQtNo().trim()));

				}

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("QT_TYPE",
								SearchOperator.OPERATOR_EQUALS.value(), "O"));

				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("IS_PANEL",
								SearchOperator.OPERATOR_EQUALS.value(), 0));

				// Filter By - Title
				if (StringUtils.isNotBlank(getManagedBean().getTitle())) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("QT_TITLE",
									SearchOperator.OPERATOR_LIKE.value(),
									getManagedBean().getTitle().trim(), true));

				}

				// Filter By - Category Code
				PmCategoryL3 pmCategoryL3 = getManagedBean().getCategoryL3();
				if (pmCategoryL3 != null
						&& pmCategoryL3.getCategoryL3Id() != null) {
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmcatL3.CATEGORY_L3_ID",
									SearchOperator.OPERATOR_EQUALS.value(),
									pmCategoryL3.getCategoryL3Id()));

				}
				
				
				PmOrgProfile ministryProfile = getManagedBean().getMinistryProfile();
				PmOrgProfile ptjGroupProfile = getManagedBean().getPtjGroupProfile();
				PmOrgProfile ptjProfile = getManagedBean().getPtjProfile();
				int level = 0;
				
				if (ptjProfile != null && ptjProfile.getOrgProfileId() != null) {// Filter By - PTJ
					
				level =  Integer.valueOf(EpPropertiesUtils.getInstance().getProperty(
							EpPropertiesConst.LVL_ORG_PROFILE_PTJ));
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(ParameterConst.ORG_PROFILE+level+".ORG_PROFILE_ID",
									SearchOperator.OPERATOR_EQUALS.value(),
									ptjProfile.getOrgProfileId()));

				} else if (ptjGroupProfile != null
						&& ptjGroupProfile.getOrgProfileId() != null) {// Filter By - PTJ Group
					level =  Integer.valueOf(EpPropertiesUtils.getInstance().getProperty(
							EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP));
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(ParameterConst.ORG_PROFILE+level+".ORG_PROFILE_ID",
									SearchOperator.OPERATOR_EQUALS.value(),
									ptjGroupProfile.getOrgProfileId()));

				} else if (ministryProfile != null
						&& ministryProfile.getOrgProfileId() != null) {// Filter By - Ministry
					level =  Integer.valueOf(EpPropertiesUtils.getInstance().getProperty(
							EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY));
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(ParameterConst.ORG_PROFILE+level+".ORG_PROFILE_ID",
									SearchOperator.OPERATOR_EQUALS.value(),
									ministryProfile.getOrgProfileId()));
				}

				// Filter By - Publish Date
				NoticeBoardSearchDataBean publishSearchDataBean = getManagedBean()
						.getPublishSearchDataBean();
				if (publishSearchDataBean != null) {
					SearchFilterBean searchFilterBean = getSearchFilterBeanByDataBean(
							"PUBLISH_DATE", publishSearchDataBean);

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
				if (briefSearchDataBean != null) {
					String searchField = "bsvDtl.BSV_DATE";
					SearchFilterBean briefSearchFilterBean = getSearchFilterBeanByDataBean(
							searchField, briefSearchDataBean);

					if (briefSearchFilterBean != null) {

						List<String> bsvTypes = new ArrayList<String>();
						bsvTypes.add("A");

						if (briefSearchFilterBean != null) {
							bsvTypes.add("B");

							searchBean.getDataFilterBeans().add(
									briefSearchFilterBean);
						}

						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("bsvDtl.BSV_TYPE",
										SearchOperator.OPERATOR_IN.value(),
										bsvTypes));
					}
				}

				if (siteVisitDataBean != null) {
					String searchField = "svDtl.BSV_DATE";

					SearchFilterBean siteVisitSearchFilterBean = getSearchFilterBeanByDataBean(
							searchField, siteVisitDataBean);

					if (siteVisitSearchFilterBean != null) {

						List<String> bsvTypes = new ArrayList<String>();
						bsvTypes.add("A");

						if (siteVisitSearchFilterBean != null) {
							bsvTypes.add("S");
							searchBean.getDataFilterBeans().add(
									siteVisitSearchFilterBean);
						}

						searchBean.getDataFilterBeans().add(
								new SearchFilterBean("svDtl.BSV_TYPE",
										SearchOperator.OPERATOR_IN.value(),
										bsvTypes));
					}
				}

				// Filter By - Closing Date
				NoticeBoardSearchDataBean clsDataBean = getManagedBean()
						.getClsDataBean();
				if (clsDataBean != null) {
					SearchFilterBean searchFilterBean = getSearchFilterBeanByDataBean(
							"CLOSING_DATE", clsDataBean);

					if (searchFilterBean != null) {
						searchBean.getDataFilterBeans().add(searchFilterBean);
					}
				}

				// Filter By - Location
				TreeNode locationRootNode = getManagedBean()
						.getLocationSelectedNode();

				if (locationRootNode != null) {
					TreeNode treeNode = locationRootNode;

					// Get the last child node.
					while (treeNode != null
							&& treeNode.getChildren().size() > 0) {
						treeNode = treeNode.getChildren().iterator().next();
						if (treeNode.isSelected()) {
							// treeNode= treeNode;
							break;
						}
					}

					if (treeNode != null) {
						if (treeNode.getType().equals("state")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("state.STATE_ID",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmState) treeNode.getData())
													.getStateId()));
						} else if (treeNode.getType().equals("division")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("div.DIVISION_ID",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmDivision) treeNode.getData())
													.getDivisionId()));
						} else if (treeNode.getType().equals("district")) {
							searchBean.getDataFilterBeans().add(
									new SearchFilterBean("distr.DISTRICT_ID",
											SearchOperator.OPERATOR_EQUALS
													.value(),
											((PmDistrict) treeNode.getData())
													.getDistrictId()));
						} /*
						 * else if (treeNode.getType().equals("city")) {
						 * searchBean .getDataFilterBeans() .add(new
						 * SearchFilterBean( "city.CITY_ID",
						 * SearchOperator.OPERATOR_EQUALS .value(), ((PmCity)
						 * treeNode.getData()) .getCityId())); }
						 */

					}
				}
				searchBean
						.getSortColumns()
						.add(new SearchOrderBean(
								"to_char(qt.PUBLISH_DATE,'yyyy-MM-dd')", false));
				NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
				reqDTO.setQtSearchBean(searchBean);
				reqDTO.setCurrentTab(selectedTab);
				reqDTO.setLevels(level);
				NoticeBoardResDTO resDTO = qtNoticeBoardSO
						.searchQTNoticeBoard(reqDTO);
				
				if (ParameterConst.NOTICE_BOARD_STATUS_CHANGES.equals(selectedTab)) {
					populateClosingQtAmendIntoAmendmentBeanMap(resDTO.getQtAmendMentBeans());
				} else {
					getManagedBean().setAmendmentClosingDateMap(null);
				}
				getManagedBean().setCommitteeFormed(resDTO.getCommitteeFormed());
				return resDTO.getQtResultBean();
			} finally {
				qtNoticeBoardSO = null;
			}
		}
	}
	
	private void populateClosingQtAmendIntoAmendmentBeanMap(List<QtAmendMentBean> qtAmendMentBeans) {
		Map<BigDecimal, Date> amendmentClosingDateMap = new HashMap<BigDecimal, Date>();
		
		if (CollectionUtils.isNotEmpty(qtAmendMentBeans)) {
			for (QtAmendMentBean amendMentBean : qtAmendMentBeans) {
				if (amendMentBean.getValueMap() != null) {
					for (Entry<String, QtAmendMentValueBean> amendMentBeanValue : amendMentBean.getValueMap().entrySet()) {
						QtAmendMentValueBean valueBean = amendMentBeanValue.getValue();
						Date approvalDate = valueBean.getApprovalDate();
						if (approvalDate != null && BooleanUtils.isTrue(valueBean.getIsApproved())) {
							Date prevApprovalDate = amendmentClosingDateMap.get(amendMentBeanValue.getKey());
							if (prevApprovalDate != null && prevApprovalDate.after(approvalDate)) {
								continue;
							}
							amendmentClosingDateMap.put(BigDecimal.valueOf(amendMentBean.getQtId()), approvalDate);	
						}
					}
					
				}
			}	
		}
		
		this.getManagedBean().setAmendmentClosingDateMap(amendmentClosingDateMap);
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
			searchFilterBean.setSearchValue(DateUtils.addDays(new Date(),
					searchDataBean.getPassDays()));
		} else if (searchDataBean.getFromDate() != null
				&& searchDataBean.getToDate() != null) {
			
			Date searchToDate = searchDataBean.getToDate();
			Calendar c2 = Calendar.getInstance();
			c2.setTime(searchToDate);
			Calendar toDate = Calendar.getInstance();
			toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
					c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			toDate.set(Calendar.MILLISECOND, 0);
			searchToDate = toDate.getTime();
			searchFilterBean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN
					.value());
			searchFilterBean.setSearchValue(new SearchBetweenValue<Date>(
					searchDataBean.getFromDate(), searchToDate));
		}

		if (searchFilterBean.getSearchValue() == null) {
			return null;
		}

		searchFilterBean.setSearchField(searchField);

		return searchFilterBean;
	}

	/**
	 * Will be called up on tab change in offered price screen.
	 * 
	 * @param changeEvent
	 *        the instance of TabChangeEvent
	 */
	public void offeredPriceTabChange(TabChangeEvent changeEvent) {

		TplSpecZoneDtl zoneDtl = (TplSpecZoneDtl) changeEvent.getData();
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		IQTNoticeBoardSO qtNoticeBoardSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTNoticeBoardSO.class);
		reqDTO.setQtId(this.getManagedBean().getScQt().getQtId());
		reqDTO.setZoneId(zoneDtl.getSpecZoneId());
		NoticeBoardResDTO resDTO = qtNoticeBoardSO
				.retrieveOfferedPriceDtlsByZone(reqDTO);

		// this.getManagedBean().setScQt(resDTO.getScQt());

		this.getManagedBean().setQualificationBean(
				resDTO.getQualificationBean());

	}

	/**
	 * Will call on change of MOF account number..
	 * 
	 * @param event
	 */
	/*
	 * public void mofNoOnchange(AjaxBehaviorEvent event) {
	 * 
	 * String mofNo = (String) event.getComponent().getAttributes()
	 * .get("value"); SmSupplier supplier = null;
	 * this.getManagedBean().setSelectedBsvAttandance(null);
	 * this.getManagedBean().setAttendanceList(null);
	 * this.getManagedBean().setSmSupplier(supplier);
	 * this.getManagedBean().setCompanyName(null); if (null != mofNo) {
	 * List<SmMofAccount> accounts = this.getManagedBean() .getMofAccounts();
	 * for (SmMofAccount mofAccount : accounts) { if
	 * (mofAccount.getMofNo().equalsIgnoreCase(mofNo)) { supplier =
	 * mofAccount.getSmSupplier(); break; } }
	 * this.getManagedBean().setSmSupplier(supplier);
	 * this.getManagedBean().setCompanyName(supplier.getCompanyName());
	 * ISupplierSearchSO supplierSearchSo = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
	 * ISupplierSearchSO.class); SupplierSearchReqDTO searchReqDTO = new
	 * SupplierSearchReqDTO(); searchReqDTO.setMofAccountNo(mofNo);
	 * SupplierSearchResDTO resDTO = supplierSearchSo
	 * .retrieveAuthorizedPersonnelsByMofNo(searchReqDTO);
	 * 
	 * List<SmPersonnel> personal = resDTO.getPersonnels();
	 * 
	 * IQTBriefingSO qtBriefingSO = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
	 * IQTBriefingSO.class); BriefingReqDTO reqDTO = new BriefingReqDTO();
	 * reqDTO.setSupplierId(supplier.getSupplierId());
	 * 
	 * BriefingResDTO briefingResDto = qtBriefingSO
	 * .retrieveBriefingAttendanceList(reqDTO); List<ScQtBsvAttendance>
	 * attandance = briefingResDto .getBriefingAttResultBean().getResultList();
	 * 
	 * // List<ScQtBsvAttendance> selectedBsvAttandance = new //
	 * ArrayList<ScQtBsvAttendance>(); List<ScQtBsvAttendance> bsvAttandances =
	 * new ArrayList<ScQtBsvAttendance>(); // Code for to set selected personal
	 * if alredy done..
	 * 
	 * if (null != personal) { for (SmPersonnel smPersonal : personal) {
	 * 
	 * ScQtBsvAttendance attendance = new ScQtBsvAttendance(); //
	 * attendance.setPreRegisterd(true); attendance.setAuthorized(true);
	 * attendance.setContactNo(smPersonal.getPhoneNo());
	 * attendance.setIcPassPort(smPersonal.getIdentificationNo());
	 * attendance.setEmail(smPersonal.getEmail());
	 * attendance.setName(smPersonal.getName()); bsvAttandances.add(attendance);
	 * }
	 * 
	 * }
	 * 
	 * this.getManagedBean().setAttendanceList(bsvAttandances); } }
	 */

	/**
	 * @param smPersonal
	 * @param attandance
	 */
	/*
	 * private boolean isExistedAttandance(SmPersonnel smPersonal,
	 * 
	 * 
	 * List<ScQtBsvAttendance> bsvttandances) { for (ScQtBsvAttendance
	 * bsvAttandance : attandance) { if
	 * (bsvAttandance.getIcPassPort().equalsIgnoreCase(
	 * smPersonal.getIdentificationNo())) {
	 * 
	 * bsvttandances.add(bsvAttandance); return true;
	 * 
	 * }
	 * 
	 * } return false; }
	 */
	
	/**
	 * TO register the supplier for Bsv...
	 */
	public void registerSupplierForBSV() {

		if (null == this.getManagedBean().getSmSupplier()) {
			throw new EpException(MessageConst.ERROR_760200);
		}
		List<ScQtBsvDtl> bsvDetails = new ArrayList<ScQtBsvDtl>();
		List<BriefingZoneBean> zoneBeans = null;
		boolean isZonal = this.getManagedBean().getScQt().isZonal();
		if (isZonal) {
			zoneBeans = this.getManagedBean().getZoneBean();
		} else {
			zoneBeans = new ArrayList<BriefingZoneBean>();
			zoneBeans.add(this.getManagedBean().getNormalBsvBean());
		}
		StringBuilder zoneNames = new StringBuilder(" ");
		int count = 0;
		for (BriefingZoneBean briefingZoneBean : zoneBeans) {
			boolean isBSVPresent = false;
			if (CollectionUtils.isNotEmpty(briefingZoneBean.getBriefingList())) {
				bsvDetails.addAll(briefingZoneBean.getBriefingList());
				for(ScQtBsvDtl bsvDtl:briefingZoneBean.getBriefingList()){
					if(bsvDtl.getTempRegistration() != null){
						isBSVPresent = true;
					}
				}
			}
			if (CollectionUtils.isNotEmpty(briefingZoneBean.getSitevisitList())) {
				bsvDetails.addAll(briefingZoneBean.getSitevisitList());
				for(ScQtBsvDtl bsvDtl:briefingZoneBean.getSitevisitList()){
					if(bsvDtl.getTempRegistration() != null){
						isBSVPresent = true;
					}
				}
			}
			if(isBSVPresent && isZonal){
				if(count == 0){
					zoneNames.append("for  ").append(briefingZoneBean.getTplSpecZoneDtl().getZoneName());
				} else {
					zoneNames.append(",  ").append(
							briefingZoneBean.getTplSpecZoneDtl().getZoneName());
				}
				count++;
			}
		}

		IQTBriefingSO qtBriefingSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTBriefingSO.class);
		BriefingReqDTO reqDTO = new BriefingReqDTO();
		String languageCode = new FacesScopes().getUserLocale()
				.getLanguage();
		reqDTO.setLanguageCode(languageCode);
		List<ScQtBsvRegistration> registrations = new ArrayList<ScQtBsvRegistration>();
		
		if (!bsvDetails.isEmpty()) {
			boolean isContain = true;
			for (ScQtBsvDtl bsvDetail : bsvDetails) {
				if (null != bsvDetail.getTempRegistration()) {
					isContain = hasSupplierRegisterAlready(bsvDetail, isContain);
					ScQtBsvRegistration scQtBsvRegistration = null;
					try {
						scQtBsvRegistration = (ScQtBsvRegistration) BeanUtils
								.cloneBean(bsvDetail.getTempRegistration());
						scQtBsvRegistration.setBsvAttendance(isAttananceMarkedOrNot(
								scQtBsvRegistration.getBsvAttendance(),
								scQtBsvRegistration));

					} catch (Exception e) {
						log.error("Exception occured while clone  ScQtBsvRegistration object "
								+ e.getMessage());
					}

					if (scQtBsvRegistration != null && scQtBsvRegistration.getBsvAttendance() != null
							 && !scQtBsvRegistration.getBsvAttendance().isEmpty()) {
						registrations.add(scQtBsvRegistration);
					}
				}
			}
			if (CollectionUtils.isEmpty(registrations)) {
				throw new EpException(MessageConst.ERROR_760244);
			}
			if (isContain) {
				throw new EpException(MessageConst.ERROR_760311);
			}
			reqDTO.setBriefingReqDtls(registrations);
			ScQt currentQt = this.getManagedBean().getScQt();
			reqDTO.setScQt(currentQt);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			reqDTO.setRoleCode(super.getUserRoleCode());

			BriefingResDTO resDto = qtBriefingSO.registerSupplierForBriefingSiteVisit(reqDTO);
			if (resDto.isSuccessful()) {
				String prvPage = this.getManagedBean().getPreviousPage();

				SmSupplier supplier = this.getManagedBean().getSmSupplier();
				String mofNumber = this.getManagedBean().getMofNumber();
				String companyName = this.getManagedBean().getCompanyName();
				int tabIndex = this.getManagedBean().getSelectedZoneTabIndex();
				boolean isValidMofNo =  this.getManagedBean().isValidMofNo();
				boolean isNavigateFromNoticeBoard = this.getManagedBean().isNavigateFromNoticeBoard();
				renewSesionObject();
				this.getManagedBean().setScQt(currentQt);
				this.getManagedBean().setSmSupplier(supplier);
				this.getManagedBean().setPreviousPage(prvPage);
				this.getManagedBean().setSelectedZoneTabIndex(tabIndex);
				retriveBsvDetails(currentQt.getQtId());
				this.getManagedBean().setMofNumber(mofNumber);
				this.getManagedBean().setCompanyName(companyName);
				this.getManagedBean().setValidMofNo(isValidMofNo);
				this.getManagedBean().setNavigateFromNoticeBoard(isNavigateFromNoticeBoard);
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(FacesMessage.SEVERITY_INFO,
										null,
										ResourceBundleUtils.getMessage(
												"lbl.sc.notice.board.bsv.success.msg",new String[] {zoneNames.toString()}, 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS,
												new FacesScopes().getUserLocale())));
			}

		} else {
			throw new EpException(MessageConst.ERROR_760185);
		}

	}

	/**
	 * @param attandance
	 *         the instance of ScQtBsvAttendance list
	 */
	private List<ScQtBsvAttendance> isAttananceMarkedOrNot(
			List<ScQtBsvAttendance> preRegisterdAttandance,ScQtBsvRegistration scQtBsvRegistration) {
		
		List<ScQtBsvAttendance> registerdAttandance = null;
		if (CollectionUtils.isNotEmpty(preRegisterdAttandance)) {
			registerdAttandance = new ArrayList<ScQtBsvAttendance>();
			for (ScQtBsvAttendance scQtBsvAttendance : preRegisterdAttandance) {
				if (scQtBsvAttendance.isPreRegisterd()) {
					scQtBsvAttendance.setAttended(true);
					scQtBsvAttendance.setBsvRegistration(scQtBsvRegistration);
					registerdAttandance.add(scQtBsvAttendance);
				}
			}
			/*for (int i = 0; i < preRegisterdAttandance.size(); i++) {
				if (preRegisterdAttandance.get(i).isPreRegisterd()) {
					preRegisterdAttandance.get(i).setAttended(true);
				} else {
					preRegisterdAttandance.remove(i--);

				}
			}*/
		}
		return registerdAttandance;

	}

	/**
	 * Display Award details on click of 'Show Award Details' link
	 * 
	 * @param qtId 
	 *         the Long variable.
	 * @return navigateUrl
	 */
	public String showAwardDetails(Long qtId) {
		
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		this.getManagedBean().setPreviousPage(MAIN_PAGE);
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			reqDTO.setQtId(qtId);
			NoticeBoardResDTO resDTO = qtNoticeBoardSO
					.retrieveAwardDetails(reqDTO);
			ScQt scQt = resDTO.getScQt();
			this.getManagedBean().setScQt(scQt);
			
			Long orgProfileId = scQt.getPmOrgProfile().getOrgProfileId();
			
			// Setting ministry name
			this.getManagedBean().setMinistryName(
					retrieveMinistryByOrgProfileId(orgProfileId));
			
			// Setting PTJ Group/ Jabatan
			this.getManagedBean().setJabatan(
					retrieveJabatanByOrgProfileId(orgProfileId));
			Long specZoneId = null;
			List<AwardedItemsBean> awardedItemList = null;
			if (resDTO.getScQt().isZonal()) {
				this.getManagedBean().setZones(resDTO.getZones());
					specZoneId = resDTO.getZones().get(0).getSpecZoneId();
					List<ScLoa> scLoalist = retrieveLOA(qtId,specZoneId);
					awardedItemList = getAwardedItemDetails(scLoalist,qtId);
				
			} else {
				this.getManagedBean().setProposalAmts(resDTO.getProposalAmts());
				// Setting Awarded list tab details
				List<ScLoa> scLoalist = retrieveLOA(qtId,specZoneId);
				awardedItemList = getAwardedItemDetails(scLoalist,qtId);
			}
			PmParameter pmParameter = this.getManagedBean().getScQt()
					.getFinalization().getRecommendedTypeId();
			this.getManagedBean().setAwardType(
					getParameterDesc(pmParameter.getParameterType(),
											pmParameter.getParameterCode()));
			
			this.getManagedBean().setAwardedItems(awardedItemList);
			
		} finally {
		}

		return VIEW_AWARD_SUPP_PAGE;
	}

	/**
	 * Method is used to download all documents from repository by qt ID 
	 * 
	 * @param qtId
	 *        the Long variable.
	 * @throws Exception
	 */
	public void downloadAllQTDocuments(Long qtId) throws Exception {

		log.info("downloadAllQTDocuments in Notice Board");
		IQTDocumentSO qtDocumentSO;
		QTDocumentResDTO resDTO = null;
		List<ScQtDocReport> scQtDocReports = null;
		boolean isNotReportPresent = true;
		try {
			qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
			QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			reqDTO.setQtId(qtId);
			resDTO = qtDocumentSO.retriveQTDocDetailsByQtId(reqDTO);
			scQtDocReports = resDTO.getQtDocReports();
		} finally {
			qtDocumentSO = null;
		}
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		if (null != scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport scQtDocReport : scQtDocReports) {
				ScQtDocReportAttachment attachment = scQtDocReport
						.getScQtDocReportAttachment();
				scAttachments.add(attachment);
			}
			if (!scAttachments.isEmpty()) {
				isNotReportPresent = false;
				downloadAllFiles("NGeP-QT-Documents", scAttachments);
			}
		}
		if (isNotReportPresent) {
			FacesContext.getCurrentInstance()
			.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,
							null,"Attachment not found."));
			String pageUrl = NB_MAIN_PAGE;
			FacesContext context = FacesContext.getCurrentInstance();
			context.getApplication().getNavigationHandler()
					.handleNavigation(context, null, pageUrl);
		}
	}


	/**
	 * construct the ConditionFields based on MinistryProfile
	 * 
	 * @return orgProfileId
	 */
	public String constuctConditionFields() {

		String parentOrgProfile = null;
		
		PmOrgProfile ministryOrgProfile = this.getManagedBean()
				.getMinistryProfile();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile
				.getOrgProfileId() : null;
		PmOrgProfile groupOrgProfile = this.getManagedBean()
				.getPtjGroupProfile();
		Long PTJGroupOrgProfileId = groupOrgProfile != null ? groupOrgProfile
				.getOrgProfileId() : null;
		if (null != PTJGroupOrgProfileId) {
			parentOrgProfile = OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ CAP_CHAR + PTJGroupOrgProfileId;
		} else if (null != MinistryOrgProfileId) {
			parentOrgProfile = OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ CAP_CHAR + MinistryOrgProfileId;
		} else {
			parentOrgProfile = "";
		}
		return parentOrgProfile;
	}
	
	/**
	 * construct the ConditionFields based on PTJGroup.
	 * 
	 * @return orgProfileId
	 */
	public String constuctConditionFieldsForPTJGroup() {

		StringBuffer parentOrgProfile = new StringBuffer();
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getMinistryProfile();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile
				.getOrgProfileId() : null;
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				for (int i = 0; i < orgLevel; i++) {
					parentOrgProfile.append("parentOrgProfile.");
				}
				parentOrgProfile.append("orgProfileId");
				parentOrgProfile.append(CAP_CHAR);
				parentOrgProfile.append(MinistryOrgProfileId);

			}

		} else {
			parentOrgProfile.append("  ");
		}
		return parentOrgProfile.toString();
	}
	
	
	/**
	 * Method will return 0 days if clsDate is after Today date else
	 * number of days.
	 * 
	 * @param clsDate
	 *         the String variable.
	 * @return closingDays
	 * @throws Exception
	 */
	public String getClosingDays(String clsDate) throws Exception {
		
		if (log.isDebugEnabled()) {
			log.debug("getClosingDays");
		}
		String closingDays = "0";

		Date closingDate = DateUtils.convertStringToDate(
				"yyyy-MM-dd HH:mm:ss.S", clsDate);

		if (new Date(System.currentTimeMillis()).after(closingDate)) {
			// closed
		} else {
			Date today = new Date();
			long days = DateUtils.getDaysDifference(today, closingDate);
			DecimalFormat df = new DecimalFormat("#00");
			closingDays = df.format(days);

			if (days == 0) {
				Long durationInMills = DateUtils.getDateDiffInMillis(
						DateUtils.getCurrentDate(), closingDate);
				if (durationInMills > 0) {
					closingDays = DateUtils.durationFormatter(durationInMills
							.longValue());
				}
			}
		}
		return closingDays;
	}

	/**
	 *Method is used to download the document from repository 
	 *on click of download button.
	 * 
	 * @param scQtDocReport
	 * 			the instance of ScQtDocReport
	 * @throws Exception
	 */
	public void downloadReport(ScQtDocReport scQtDocReport) throws Exception {
		
		log.info("** download Report  in NOtice Board**");
		downloadFile(scQtDocReport.getScQtDocReportAttachment());
	}

	/**
	 * Get Bsvs details.
	 * 
	 * @param qtId
	 *        the Long variable.
	 */
	private void retriveBsvDetails(Long qtId) {

		PmUser pmUser = new FacesScopes().getUserSessionBean();
		IQTNoticeBoardSO qtNoticeBoardSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTNoticeBoardSO.class);
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
		reqDTO.setUserId(pmUser.getUserId());
		reqDTO.setQtId(qtId);
		NoticeBoardResDTO resDTO = qtNoticeBoardSO
				.retrieveSupplierRegistrationDtl(reqDTO);
		ScQt scQt = resDTO.getScQt();
		this.getManagedBean().setScQt(resDTO.getScQt());
		
		Long supplierId = getSupplierId();
		if (supplierId != null) {
			
			SupplierSearchResDTO searchResDTO = getSupplierInfo(supplierId);
			SmSupplier smSupplier = searchResDTO.getSmSupplier();
			this.getManagedBean().setSmSupplier(smSupplier);
		} else {
			this.getManagedBean().setSmSupplier(new SmSupplier());
		}
		boolean isNotRigistered = false;
		if (!scQt.isZonal()) {
			ScQtBsv scQtBsv = resDTO.getScQtBsv();

			BriefingZoneBean briefingZoneBean = new BriefingZoneBean(null,
					resDTO.getBriefingResultBean(),
					resDTO.getSiteVisitResultBean(), scQtBsv,null);
			this.getManagedBean().setNormalBsvBean(briefingZoneBean);
			isNotRigistered = isNotRigistered(briefingZoneBean,isNotRigistered);
		} else if (scQt.isZonal()) {
			List<TplSpecZoneDtl> zoneDtls = resDTO.getZones();
			// Need to construct tab bean for zone
			MenuModel zoneMenuModel = new DefaultMenuModel();
			List<BriefingZoneBean> bsvZoneBeans = new ArrayList<BriefingZoneBean>();

			if (null != zoneDtls) {
				int zoneTabIndex = 0;

				for (TplSpecZoneDtl specZoneDtl : zoneDtls) {

					BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
					briefingReqDTO.setQtId(scQt.getQtId());
					briefingReqDTO.setSpecZoneId(specZoneDtl.getSpecZoneId());
					briefingReqDTO.setAttandanceReq(true);
					briefingReqDTO.setSupplierId(this.getManagedBean()
							.getSmSupplier().getSupplierId());
					IQTBriefingSO briefingSO = EpEjbBusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTBriefingSO.class);
					BriefingResDTO briefingResDTO = briefingSO
							.retrieveBriefingSiteVisitDtls(briefingReqDTO);

					BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
							specZoneDtl, briefingResDTO.getBriefingList(),
							briefingResDTO.getSiteVisitList(),
							briefingResDTO.getScQtBsv(),null);
					bsvZoneBeans.add(briefingZoneBean);
					isNotRigistered = isNotRigistered(briefingZoneBean,isNotRigistered);
					MenuItem zoneItem = new MenuItem();
					zoneItem.setValue(specZoneDtl.getZoneName());
					zoneItem.setAjax(true);
					zoneItem.setUpdate("@this");
					zoneItem.setPartialSubmit(true);
					zoneItem.setProcess("@this");
					zoneItem
					.setOncomplete("bsvZoneTabChangeAction([ {name : 'selectedZoneTabIndex', value : "
							+ zoneTabIndex + "}]);");
					zoneMenuModel.addMenuItem(zoneItem);

					zoneTabIndex++;
				}
			}

			this.getManagedBean().setZoneMenuModel(zoneMenuModel);
			if (bsvZoneBeans.size() > 0) {
				getManagedBean().setNormalBsvBean(bsvZoneBeans.get(this.getManagedBean().getSelectedZoneTabIndex()));
			}
			this.getManagedBean().setZoneBean(bsvZoneBeans);
		}
		this.getManagedBean().setRenderRegisterBtn(isNotRigistered);
		this.getManagedBean().setDisableRegisterBtn(isNotRigistered);
	}

	/**
	 * Returns true if clsDate is less than or equal to Today date
	 * 
	 * @param clsDate
	 * 			the String variable
	 * @return true/false
	 * @throws Exception
	 */
	public boolean isShowHours(String clsDate) throws Exception {
		
		if (!StringUtils.isEmpty(clsDate)) {
			Date closingDate = DateUtils.convertStringToDate(
					CommonConstants.DEFAULT_DATETIME_FORMAT,
					clsDate.replace("-", "/"));
			return (DateUtils.getDaysDifference(
					DateUtils.getCurrentDateWithoutTime(), closingDate) <= 0);
		} else {
			return false;
		}

	}

	/**
	 * Method will be called to update attendance through Ajax.
	 * 
	 * @param event
	 *        the instance of AjaxBehaviorEvent
	 */
	public void updateToBeAttandance(AjaxBehaviorEvent event) {
		
		boolean isChecked = ((SelectBooleanCheckbox) event.getSource())
				.isSelected();
		ScQtBsvDtl bsvDtl = this.getManagedBean().getBriefingDtls();
		if (null != bsvDtl) {
			if (isChecked) {

				bsvDtl.setToBeattand(bsvDtl.getToBeattand() + 1);
			} else {
				if(bsvDtl.getToBeattand()!= 0){
					bsvDtl.setToBeattand(bsvDtl.getToBeattand() - 1);
				}
			}
			if (bsvDtl.getToBeattand() > bsvDtl.getMaxNoOfAttendance()) {

				((SelectBooleanCheckbox) event.getSource()).setSelected(false);
				bsvDtl.setToBeattand(bsvDtl.getToBeattand() - 1);
				throw new EpException(MessageConst.ERROR_760230,
						new String[] { bsvDtl.getBsvDesc() });
			}
		} else {

		}

		List<ScQtBsvDtl> bsvDetails = this.getManagedBean().getNormalBsvBean()
				.getBriefingList();
		if(null != bsvDetails && bsvDetails.contains(bsvDtl)){
			bsvDetails.set(bsvDetails.indexOf(bsvDtl), bsvDtl);
		}
		
		List<ScQtBsvDtl> svDetails = this.getManagedBean().getNormalBsvBean()
				.getSitevisitList();
		if(null != svDetails && svDetails.contains(bsvDtl)){
			svDetails.set(svDetails.indexOf(bsvDtl), bsvDtl);
		}
	}

	/**
	 * Method will be called on row select event from data table.
	 * 
	 * @param event
	 *         the instance of SelectEvent
	 */
	public void onRowSelect(SelectEvent event) {
		
		this.getManagedBean().setBriefingDtls(null);
		ScQtBsvDtl scQtBsvDtl = (ScQtBsvDtl) event.getObject();
		if(scQtBsvDtl != null){
			BriefingZoneBean zoneBean = this.getManagedBean().getNormalBsvBean();
	
			ScQtBsvRegistration bsvRegistration = scQtBsvDtl.getTempRegistration();
	
			if ((null != this.getManagedBean().getSmSupplier() && null != this
					.getManagedBean().getMofNumber())&&
					 (null == bsvRegistration || bsvRegistration
							.getBsvAttendance().isEmpty())) {
					
				
				List<ScQtBsvAttendance> bsvAttandances = new ArrayList<ScQtBsvAttendance>();
				IQTBriefingSO briefingSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTBriefingSO.class);
				BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
				briefingReqDTO.setQtBriefingId(scQtBsvDtl.getQtBsvDtlId());
				briefingReqDTO.setSupplierId(this.getManagedBean().getSmSupplier()
						.getSupplierId());
				BriefingResDTO briefingResDTO = briefingSO
						.retrieveBriefingRegistrationList(briefingReqDTO);
	
				// ScQtBsvRegistration bsvRegistration = null;
				if (briefingResDTO.getBriefingRegResultBean().getTotalRecord() == 0) {
	
					bsvRegistration = new ScQtBsvRegistration();
					bsvRegistration.setBsvDtl(scQtBsvDtl);
					bsvRegistration.setSupplier(this.getManagedBean()
							.getSmSupplier());
					
				} else {
					bsvRegistration = briefingResDTO.getBriefingRegResultBean()
							.getResultList().get(0);
				}
				// Code to get authorize personal..
				ISupplierSearchSO supplierSearchSo = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
								ISupplierSearchSO.class);
				SupplierSearchReqDTO searchReqDTO = new SupplierSearchReqDTO();
				searchReqDTO.setMofAccountNo(this.getManagedBean().getMofNumber());
				SupplierSearchResDTO resDTO = supplierSearchSo
						.retrieveAuthorizedPersonnelsByMofNo(searchReqDTO);
	
				List<SmPersonnel> personal = resDTO.getPersonnels();
	
				// Code for to set selected personal if alredy done..
	
				if (null != personal) {
	
					for (SmPersonnel smPersonal : personal) {
						ScQtBsvAttendance attendance = new ScQtBsvAttendance();
						// attendance.setPreRegisterd(true);
						attendance.setAuthorized(true);
						attendance.setContactNo(smPersonal.getPhoneNo());
						attendance.setIcPassPort(smPersonal.getIdentificationNo());
						attendance.setEmail(smPersonal.getEmail());
						attendance.setName(smPersonal.getName());
						attendance.setBsvRegistration(bsvRegistration);
	
						bsvAttandances.add(attendance);
					}
	
				}
	
				List<ScQtBsvAttendance> existingAttandances = bsvRegistration
						.getBsvAttendance();
				if (CollectionUtils.isNotEmpty(existingAttandances)) {
					for (ScQtBsvAttendance extAttandance : existingAttandances) {
						if (bsvAttandances.contains(extAttandance)) {
							bsvAttandances.remove(extAttandance);
	
						}
						bsvAttandances.add(extAttandance);
					}
				}
				bsvRegistration.setBsvAttendance(bsvAttandances);
				scQtBsvDtl.setTempRegistration(bsvRegistration);
			}
			setSlectedHeaderName(null);
			this.getManagedBean().setBriefingDtls(scQtBsvDtl);
			List<ScQtBsvDtl> bsvDetails = zoneBean.getBriefingList();
			if(null != bsvDetails && bsvDetails.contains(scQtBsvDtl)){
			bsvDetails.set(bsvDetails.indexOf(scQtBsvDtl), scQtBsvDtl);
			setSlectedHeaderName(scQtBsvDtl.getBsvDesc().concat("  ").concat(">"));
					
			}
			List<ScQtBsvDtl> svDetails = zoneBean.getSitevisitList();
			if(null != svDetails && svDetails.contains(scQtBsvDtl)){
			    svDetails.set(svDetails.indexOf(scQtBsvDtl), scQtBsvDtl);
			    setSlectedHeaderName(scQtBsvDtl.getBsvDesc().concat("  ").concat(">"));
			}
			this.getManagedBean().setNormalBsvBean(zoneBean);
			
			if(this.getManagedBean().isDisableRegisterBtn()){
				this.getManagedBean().setRenderRegisterBtn(!isAttandanceEditable(scQtBsvDtl));
			}
		}
	}

	/**
	 * Method will be called on change of tabs.
	 * 
	 * @param event
	 *         the instance of TabChangeEvent
	 */
	public void bsvZoneTabChange(TabChangeEvent event) {
		
		BriefingZoneBean zoneBean = (BriefingZoneBean) event.getData();
		this.getManagedBean().setNormalBsvBean(zoneBean);
		TplSpecZoneDtl tplZoneDetails = this.getManagedBean()
											.getNormalBsvBean().getTplSpecZoneDtl();
		this.getManagedBean().setLocationNodes(
				LocationSingleton.getInstance().constuctTreeNode(
						tplZoneDetails.getTplSpecZoneLocs()));

	}

	/**
	 * Return false when getUserId contains null and assuming the logged user is
	 * a guest
	 */
	private boolean isUser() {

		boolean isUser = false;
		try {
			FacesScopes fs = new FacesScopes();
			PmUser pmUser = fs.getUserSessionBean();
			if (null != pmUser && null != pmUser.getUserId()) {
				isUser = true;
				
				// Get MOF number of sign in supplier & set into mofNo property
				// to pre populate in check supplier qualification criteria  screen.
				if (this.getManagedBean().getSupplierMofNo() == null) {
					final Long supplierId = getSupplierId();
					if (null != supplierId) {
						SmMofAccount mofAccount = retrieveMofAccount(supplierId);
						if (mofAccount != null) {
							this.getManagedBean().setSupplierMofNo(mofAccount.getMofNo());
						} else {
							isUser = false;// no need to display the check qualification icon
						}

					}

				}
			}

		} catch (EpException exception) {
			log.info("Exception :" + exception.getMessage());
		}
		return isUser;
	}

	/**
	 * Get any Committee member details through 
	 * retrieveCommitteByQtIdNCommType method
	 * 
	 * @param commType
	 *        the String variable 
	 * @param qtId
	 *        the Long variable
	 * @return qtCommitte
	 */
	private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long qtId) {
		
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setCommitteeType(commType);
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		ScQtCommittee qtCommitte = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
		} finally {
			committeSO = null;
		}
		return qtCommitte;
	}
	
	/**
	 * Get ScLoa object through retrieveLoaListByQtId method
	 * 
	 * @param qtId
	 *        the Long variable 
	 * @return ScLoa
	 */
	private List<ScLoa> retrieveLOA(Long qtId,Long specZoneId) {
		
		IQTAwardSO qtAwardSO = null;
		try {
			qtAwardSO = getQTAwardSO();
			AwardSupplierReqDTO reqDTO = new AwardSupplierReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setSpecZoneId(specZoneId);
			AwardSupplierResDTO resDTO = qtAwardSO.retrieveLoaListByQtId(reqDTO);
			return resDTO.getLoaResultBean().getResultList();

		} finally {
			qtAwardSO = null;
		}

	}

	private IQTAwardSO getQTAwardSO() {
		
		IQTAwardSO qtAwardSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);

		return qtAwardSO;
	}
	
	
	/**
	 * Category Selection Action
	 * 
	 * @param pmCategoryL3
	 * 		the PmCategoryL3 instance
	 */
	public void selectCategoryAction(PmCategoryL3 pmCategoryL3) {
		this.getManagedBean().setCategoryL3(pmCategoryL3);
	}
	
	/**
	 * Get notice board QT info through retrieveQtNoticeDtl 
	 * method of IQTNoticeBoardSO
	 * 
	 * @param reqDTO 
	 *         the instance of NoticeBoardReqDTO
	 * @return qtNoticeBoardSO
	 */
	private NoticeBoardResDTO getNBResDTO(NoticeBoardReqDTO reqDTO) {
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		try {
			qtNoticeBoardSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTNoticeBoardSO.class);
			return qtNoticeBoardSO.retrieveScQt(reqDTO);
		} finally {
			qtNoticeBoardSO = null;
		}
	}
	
	/**
	 * Get QT PagingDataModel.
	 * @return the qtDataModel
	 */
	public IPagingDataModel<Object[]> getQtDataModel() {
		return qtDataModel;
	}

	/**
	 * Set QT PagingDataModel.
	 * @param qtDataModel
	 *            the qtDataModel to set
	 */
	public void setQtDataModel(IPagingDataModel<Object[]> qtDataModel) {
		this.qtDataModel = qtDataModel;
	}

	/**
	 * Set the Visible.
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Get the Visible.
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Set the dispalyImage.
	 * @return the displayImage
	 */
	public boolean isDisplayImage() {
		return displayImage;
	}

	/**
	 * Get the dispalyImage.
	 * @param displayImage the displayImage to set
	 */
	public void setDisplayImage(boolean displayImage) {
		this.displayImage = displayImage;
	}
	
	/**
	 * check BsvDate is greater than currentdate.
	 * @param bsvDtl
	 * @return the attendance is editable or not.
	 */
	public boolean isAttandanceEditable(ScQtBsvDtl bsvDtl){
		if(bsvDtl.getBsvDate().compareTo(new Date()) > 0 ){
			return false;
		}
		return true;
	}
	
	/**
	 * fetch the  mofAccount object based on sign in supplier id
	 * 
	 * @return mofAccount
	 */
	private SmMofAccount retrieveMofAccount(Long supplierId) {
		
		SmMofAccount mofAccount = null;
			SupplierSearchResDTO supplierResDTO = getSupplierInfo(supplierId);
			SmSupplier supplier = supplierResDTO.getSmSupplier();
			if (supplier != null) {
				List<SmMofAccount> mofAccounts = supplier.getSmMofAccounts();
				if (CollectionUtils.isNotEmpty(mofAccounts)) {
					mofAccount = mofAccounts.get(0);
				}
			}
		
		return mofAccount;
	}
	
	private Long getSupplierId() {

		Long supplierId = null;
		SmSupplier currSupp = SmApplicationUtil.getCurrentSupplier();
		if(currSupp != null){
			supplierId = currSupp.getSupplierId();
		}
		return supplierId;
	}
	
	/**
	 * fetch the smSupper info based on supplier id 
	 * 
	 * @param supplierId
	 * @return
	 */
	private SupplierSearchResDTO getSupplierInfo(Long supplierId){
		
		ISupplierSearchSO supplierSearchSO = null;
		try{
			supplierSearchSO = EpEjbBusinessDelegate
				.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
		SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
		supplierSearchReqDTO.setSupplierId(supplierId);
		return supplierSearchSO
				.retriveSupplierInfoById(supplierSearchReqDTO);
		} finally {
			supplierSearchSO = null;
		}
		
	}

	/**
	 * Get the SlectedHeaderName.
	 * @return the slectedHeaderName
	 */
	public String getSlectedHeaderName() {
		return slectedHeaderName;
	}

	/**
	 * Set the SlectedHeaderName.
	 * @param slectedHeaderName the slectedHeaderName to set
	 */
	public void setSlectedHeaderName(String slectedHeaderName) {
		this.slectedHeaderName = slectedHeaderName;
	}
	
	private class QTCategoryCodesDataModel extends PagingDataModel<ScQtCategory> {

		public QTCategoryCodesDataModel() {
			super("restrcatcode");
		}

		private static final long serialVersionUID = 1L;

		@Override
		public ISearchResultBean<ScQtCategory> pageChangeListener(
				ISearchBean<ScQtCategory> searchBean) {
			IQTNoticeBoardSO qtNoticeBoardSO = null;
			
			// Filter By - qtId
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("scQt.qtId",
							SearchOperator.OPERATOR_EQUALS.value(),
							getManagedBean().getQtId()));

			// Order by categoryCodes..
			if (searchBean.getSortColumns().size() == 0) {

				searchBean.getSortColumns().add(
						new SearchOrderBean("pmCategoryL1.categoryL1Code"));
				searchBean.getSortColumns().add(
						new SearchOrderBean("pmCategoryL2.categoryL2Code"));
				searchBean.getSortColumns().add(
						new SearchOrderBean("pmCategoryL3.categoryL3Code"));
			}

			try {
				qtNoticeBoardSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTNoticeBoardSO.class);
				NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
				reqDTO.setQtCategorySearchBean(searchBean);
				NoticeBoardResDTO resDTO = qtNoticeBoardSO.getQtCategoryCodesList(reqDTO);
				return resDTO.getCategoryResultBean();
			} finally{
				qtNoticeBoardSO = null;
			}
			
		}
	}

	/**
	 * Get CategoryCodes PagingDataModel.
	 * @return the categoryCodesDataModel
	 */
	public IPagingDataModel<ScQtCategory> getCategoryCodesDataModel() {
		return categoryCodesDataModel;
	}

	/**
	 * Set CategoryCodes PagingDataModel.
	 * @param categoryCodesDataModel the categoryCodesDataModel to set
	 */
	public void setCategoryCodesDataModel(
			IPagingDataModel<ScQtCategory> categoryCodesDataModel) {
		this.categoryCodesDataModel = categoryCodesDataModel;
	}
	
	/**
	 * @param bsvDetail
	 */
	private boolean hasSupplierRegisterAlready(ScQtBsvDtl bsvDetail, boolean isContain) {
		
		IQTBriefingSO briefingSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTBriefingSO.class);
		BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
		briefingReqDTO.setQtBriefingId(bsvDetail.getQtBsvDtlId());
		briefingReqDTO.setSupplierId(this.getManagedBean().getSmSupplier()
				.getSupplierId());
		BriefingResDTO briefingResDTO = briefingSO
				.retrieveBriefingRegistrationList(briefingReqDTO);
		if (briefingResDTO.getBriefingRegResultBean().getTotalRecord() > 0) {
			ScQtBsvRegistration bsvRegistration = briefingResDTO
					.getBriefingRegResultBean().getResultList().get(0);
			List<ScQtBsvAttendance> existedAttendences = bsvRegistration
					.getBsvAttendance();
			List<ScQtBsvAttendance> currentAttendences = bsvDetail
					.getTempRegistration().getBsvAttendance();
			if (existedAttendences != null && !existedAttendences.isEmpty()) {
				
				for (ScQtBsvAttendance attendence : currentAttendences) {
					if (existedAttendences.contains(attendence)) {
						if (!attendence.isPreRegisterd()) {
							isContain = false;
							break;
						}
					} else if (attendence.isPreRegisterd()) {
						isContain = false;
						break;
					}
				}
				
			}
		} else {
			isContain = false;
		}
		return isContain;
	}
	
	public boolean isNotRigistered(BriefingZoneBean briefingZoneBean,
			boolean isNotRigistered) {

		List<ScQtBsvDtl> briefinfDtls = briefingZoneBean.getBriefingList();
		if (briefinfDtls != null && !briefinfDtls.isEmpty()) {
			for (ScQtBsvDtl bsvDtl : briefinfDtls) {
				if (bsvDtl.getToBeattand() == 0) {
					isNotRigistered = true;
				}
			}
		}
		List<ScQtBsvDtl> svDtls = briefingZoneBean.getSitevisitList();
		if (svDtls != null && !svDtls.isEmpty()) {
			for (ScQtBsvDtl bsvDtl : svDtls) {
				if (bsvDtl.getToBeattand() == 0) {
					isNotRigistered = true;
				}
			}
		}
		return isNotRigistered;
	}
	
	
	/**
	 * @param scLoalist
	 * @param qtId
	 * @return
	 */
	private List<AwardedItemsBean> getAwardedItemDetails(List<ScLoa> scLoalist, Long qtId){
		
		PmParameter pmParameter = this.getManagedBean().getScQt()
				.getFinalization().getRecommendedTypeId();
		
		PmParameter finalizationMethod = this.getManagedBean().getScQt()
				.getFinalization().getFinalizationMethod();
		
		AwardSupplierReqDTO awardReqDTO = new AwardSupplierReqDTO();
		awardReqDTO.setQtId(qtId);
		boolean isRefinalized = getQTAwardSO().isQtRefinalized(awardReqDTO).isRefinalized();
		
		List<AwardedItemsBean> awardedItemList = new ArrayList<>();
		for (ScLoa scLoa : scLoalist) {
			if (ParameterConst.RECOMMEND_TYPE_PACKAGE
					.equalsIgnoreCase(pmParameter.getParameterCode())) {
				this.getManagedBean().setSupplierName(
						scLoa.getScLoiLoa().getSupplierName());
				
			} else {
				for (ScLoiLoaItem scLoiLoaItem : scLoa.getScLoiLoaItems()) {
					AwardedItemsBean awardedItem = new AwardedItemsBean();
					ScRequestItem reqItem = scLoiLoaItem.getScRequestItem();
					awardedItem.setItemName(reqItem.getItemName());
					awardedItem.setSupplierName(scLoiLoaItem.getScLoa().getScLoiLoa().getSupplierName());
					// By Loa
					awardedItem.setPriceByLoa(TemplateSpecHelper.getInstance()
							.calcTotalAmount(reqItem.getTplSpecQuestion(),
									scLoiLoaItem.getUnitPrice(), scLoiLoaItem.getQty()));
					// By Proposal
					ScQtRecommendDtl scQtRecommendDtl = scLoiLoaItem.getScQtRecommendDtl();
					if(null != scQtRecommendDtl && null != scQtRecommendDtl.getScQtProposalItem()){
						BigDecimal totalPrice = TemplateSpecHelper.getInstance()
						.calcTotalAmount(reqItem.getTplSpecQuestion(),
								scQtRecommendDtl.getScQtProposalItem().getOfferPrice(), 
									scLoiLoaItem.getQty());
						awardedItem.setPriceByProposal(totalPrice);
					}
					// By Bidding
					if (finalizationMethod != null && ParameterConst.RECOMMEND_BY_ONLINE_BIDDING
							.equals(finalizationMethod.getParameterCode()) && !isRefinalized) {
					awardedItem.setPriceByBidding(TemplateSpecHelper.getInstance()
						.calcTotalAmount(reqItem.getTplSpecQuestion(),
								scQtRecommendDtl.getScQtProposalItem().getFinalPrice(), 
									scLoiLoaItem.getQty()));
					}
					awardedItemList.add(awardedItem);
				}
			}
		}
   return awardedItemList;
	}
	
	/**
	 * Will be called up on tab change of show Award Details  screen.
	 * 
	 * @param changeEvent
	 *        the instance of TabChangeEvent
	 */
	public void awardSupplierTabChange(TabChangeEvent changeEvent) {

		TplSpecZoneDtl zoneDtl = (TplSpecZoneDtl) changeEvent.getData();
		Long qtId = this.getManagedBean().getScQt().getQtId();
		List<ScLoa> scLoaList = retrieveLOA(qtId, zoneDtl.getSpecZoneId());
		this.getManagedBean().setAwardedItems(
				getAwardedItemDetails(scLoaList, qtId));

	}
	
	/**
	 * Download the documents from repository on click of download all button
	 * 
	 * @throws Exception
	 */
	public void downloadAllQTDocuments() throws Exception {
		
		List<ScQtDocReport> scQtDocReports = this.getManagedBean()
				.getScQtDocReports();
		List<ScQtDocReportAttachment> scAttachments = new ArrayList<ScQtDocReportAttachment>();
		if (null != scQtDocReports && !scQtDocReports.isEmpty()) {
			for (ScQtDocReport scQtDocReport : scQtDocReports) {
				ScQtDocReportAttachment attachment = scQtDocReport
						.getScQtDocReportAttachment();
				scAttachments.add(attachment);
			}
			if (!scAttachments.isEmpty()) {
				downloadAllFiles("NGeP-QT-Documents", scAttachments);
			} else {
				throw new EpException(MessageConst.ERROR_760329);
			}
		}

	}
	
	/**
	 * Check whether Committee is formed or not
	 * 
	 * @param qtId
	 * @param evalType
	 * @return 
	 */
	public boolean isCommitteeFormed(Long qtId) {
		
		Map<Long, Boolean> committeeFormedMap = this.getManagedBean().getCommitteeFormed();
		Boolean isCommitteeFormed = false;
		if (committeeFormedMap != null && committeeFormedMap.size() > 0
				&& qtId != null) {
			isCommitteeFormed = committeeFormedMap.get(qtId);
		}
		return isCommitteeFormed!= null?isCommitteeFormed:false;
	}
	/**
	 * Check whether Date is null or not
	 * 
	 * @param publishDate
	 * @param createdDate
	 * @return Date
	 */
	public Date retriveDatesforOrgProfile(Date publishDate,Date createdDate){
		if(publishDate==null){
			log.info("===========createdDate====="+createdDate);
			return createdDate;
		}
		log.info("===========publishDate====="+publishDate);
		return publishDate;
		
	}
	
	/**
	 * Get list Of Ministry.
	 * 
	 * @param query
	 * 
	 * @return  List<PmOrgProfile>
	 */
	public List<PmOrgProfile> autoCompleteOrgMinistry(String query) {
		
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setAutoCompeleteString(query);
		reqDTO.setMaxResults(getMaxResultsCount("autoOrgMinistryId"));
		reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
		OrgProfileResDTO resDTO = getOrgProfileSo()
				.retrieveOrgProfilesBasedOnOrgType(reqDTO);
		return resDTO.getOrgProfileList();
	}
	
	/**
	 * Get list Of PTJGroups.
	 * 
	 * @param query
	 * 
	 * @return  List<PmOrgProfile>
	 */
	public List<PmOrgProfile> autoCompleteOrgPTJGroup(String query) {
		
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setAutoCompeleteString(query);
		reqDTO.setMaxResults(getMaxResultsCount("autoPtjGropupId"));
		reqDTO.setOrgType(ParameterConst.ORG_TYPE_PTJ_GROUP);
		reqDTO.setOrgProfileConditionField(constuctConditionFieldsForPTJGroup());
		OrgProfileResDTO resDTO = getOrgProfileSo()
				.retrieveOrgProfilesBasedOnOrgType(reqDTO);
		return resDTO.getOrgProfileList();
	}
	
	/**
	 * Get list Of PTJ.
	 * 
	 * @param query
	 * 
	 * @return  List<PmOrgProfile>
	 */
	public List<PmOrgProfile> autoCompleteOrgPTJ(String query) {
		
		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		reqDTO.setMaxResults(getMaxResultsCount("autoPtjId"));
		reqDTO.setAutoCompeleteString(query);
		reqDTO.setOrgType(ParameterConst.ORG_TYPE_PTJ);
		reqDTO.setOrgProfileConditionField(constuctConditionFields());
		OrgProfileResDTO resDTO = getOrgProfileSo()
				.retrieveOrgProfilesBasedOnOrgType(reqDTO);
		return resDTO.getOrgProfileList();
	}
	
	/**
	 * method to get itemLabels based on PmOrgProfile.
	 * 
	 * @param orgProfile
	 * 
	 * @return  the item label data.
	 */
	public String itemLabel(PmOrgProfile orgProfile) {

		if (orgProfile != null && orgProfile.getOrgCode() != null
				&& orgProfile.getOrgName() != null) {
			return orgProfile.getOrgCode() + "-" + orgProfile.getOrgName();
		}
		return " ";
	}
	
	/**
	 * Method will cut the string based on cutOffIndex value.
	 * 
	 * @param str
	 * @param cutOffIndex
	 * @return formatted string
	 */
	public String chopString(String str, int cutOffIndex) {

		if (str.length() > cutOffIndex) {
			str = StringUtils.substring(str, 0, cutOffIndex) + " ...";
		}
		return str;
	}
	
	private int getMaxResultsCount(String componentId) {

		int maxResults = 5;
		UIComponent component = new FacesScopes().findComponentById(
				FacesContext.getCurrentInstance().getViewRoot(), componentId);
		if (component instanceof AutoComplete) {
			AutoComplete autoComplete = (AutoComplete) component;
			maxResults = autoComplete.getMaxResults();
		}

		return maxResults;
	}
	
	private IOrgProfileSO getOrgProfileSo() {

		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}
	
	/**
	 * method to get list of categoryCodes based on CategoryId.
	 * 
	 * @param event
	 * 
	 *  
	 */
	public void selectedCategory(AjaxBehaviorEvent event){
		
		Long selectedCategoryL3 = (Long) event.getComponent().getAttributes()
				.get("value");
		this.getManagedBean().setCategoryL3(null);
		if(selectedCategoryL3 != null){
			ICategoryCodeSO categoryCodeSO = null;
			try {
				categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
				CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
				reqDTO.setCategoryL3Id(selectedCategoryL3);
				CategoryCodeResDTO resDTO = categoryCodeSO
						.retrieveCategoryL3DtlById(reqDTO);
				this.getManagedBean().setCategoryL3(resDTO.getCategoryL3());
			} finally {
				categoryCodeSO = null;
			}
		}
	}

	/**
	 * The method used for generating a list of available ministry.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			items = new SelectItem[resDTO.getOrgProfileList().size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
					"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()));

			for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());
			}
			return items;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return null;
	}

	/**
	 * The method used for generating a list of available jabatan
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			if (getManagedBean().getMinistryProfile() != null
					&& getManagedBean().getMinistryProfile().getOrgProfileId() != null) {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean()
						.getMinistryProfile().getOrgProfileId());
				reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveChildDDListByParentOrgProfileId(reqDTO);

				items = new SelectItem[(resDTO.getOrgProfileDDList() != null ? resDTO
						.getOrgProfileDDList().size() : 0) + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));

				if (CollectionUtils.isNotEmpty(resDTO.getOrgProfileDDList())) {
					for (Object[] objects : resDTO.getOrgProfileDDList()) {
						items[i++] = new SelectItem(Long.parseLong(objects[0]
								.toString()), objects[1].toString());
					}
				}
				return items;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return null;
	}
	
	/**
	 * The method used for generating a list of available PTJ
	 * 
	 * @return the selection list of ptj
	 */
	public SelectItem[] getPTJDropdown() {		
		SelectItem[] items = null;

		try {
			int i = 1;
			if (getManagedBean().getPtjGroupProfile() != null
					&& getManagedBean().getPtjGroupProfile().getOrgProfileId() != null) {
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate
						.getInstance().getServiceObject(
								ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean()
						.getPtjGroupProfile().getOrgProfileId());
				reqDTO.setOrgCode(OrganizationType.DIVISION.value());
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveChildDDListByParentOrgProfileId(reqDTO);

				items = new SelectItem[(resDTO.getOrgProfileDDList() != null ? resDTO
						.getOrgProfileDDList().size() : 0) + 1];
				items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale()));

				if (CollectionUtils.isNotEmpty(resDTO.getOrgProfileDDList())) {
					for (Object[] objects : resDTO.getOrgProfileDDList()) {
						items[i++] = new SelectItem(Long.parseLong(objects[0]
								.toString()), objects[1].toString());
					}
				}
				return items;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return null;
	}
}
