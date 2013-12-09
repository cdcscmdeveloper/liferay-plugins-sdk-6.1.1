package my.ep.web.backingbean.ss.noticeboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.categorys.ICategorysSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.biz.service.ss.quationtender.IQTNoticeBoardSO;
import my.ep.constant.ss.QTStatusConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.categorys.CategorysReqDTO;
import my.ep.dto.pm.common.categorys.CategorysResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.ss.quationtender.NoticeBoardReqDTO;
import my.ep.dto.ss.quationtender.NoticeBoardResDTO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.ss.quationtender.ScQt;
import my.ep.persistence.entity.ss.quationtender.ScQtCategory;
import my.ep.persistence.entity.ss.quationtender.ScQtCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.ss.quationtender.ScQtZoneCriteria;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.ss.noticeboard.NoticeBoardManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.TabChangeEvent;

/**
 * @author sri 
 * Notice Board Backing Bean
 */
@ManagedBean(name = "noticeBoardBackingBean")
@RequestScoped
public class NoticeBoardBackingBean extends
		BaseBackingBean<NoticeBoardManagedBean> {
	public static Logger log = Logger.getLogger(NoticeBoardBackingBean.class);
	private static final String NAV_TO_QTN_TENDER_DETAILS_PAGE = "tenderNoticeDetailScreen.xhtml?faces-redirect=true";
	private static final String NAV_TO_NOTICE_BOARD_SEARCH_PAGE = "noticeboardmain.xhtml?faces-redirect=true";

	
	public NoticeBoardBackingBean () {
		load();
	}
	
	public void load(){
		populateMinistryList();
		getTabs();
		retriveCategories1();
		getStateDropdownValues();
	}
	
	/**
	 *  Main Filtration For Notice Board
	 */
	public List<ScQt> searchNoticeBoard() {
		log.info("*** searchNoticeBoard **** ");
		IQTDetailSO detailSO = null;
		QuationTenderReqDTO qtReqDTO= new QuationTenderReqDTO();
		QuationTenderResDTO qtResDTO = null;
		ISearchBean<ScQt> searchBean = new SearchBean<ScQt>();

		if (getManagedBean().getScQt().getMinistryOrgProfile().getOrgProfileId() != null
				&& !"".equals(getManagedBean().getScQt().getMinistryOrgProfile().getOrgProfileId())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"ministryOrgProfile.orgProfileId", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					getManagedBean().getScQt().getMinistryOrgProfile().getOrgProfileId()));
		}
		
		if (getManagedBean().getScQt().getDepartmentOrgProfile().getOrgProfileId() != null
				&& !"".equals(getManagedBean().getScQt().getDepartmentOrgProfile().getOrgProfileId())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"departmentOrgProfile.orgProfileId", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					getManagedBean().getScQt().getDepartmentOrgProfile().getOrgProfileId()));
		}
		
		if (getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId() != null
				&& !"".equals(getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmOrgProfile.orgProfileId", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					getManagedBean().getScQt().getPmOrgProfile().getOrgProfileId()));
		}
		
		if (getManagedBean().getScQt().getQtTitle() != null
				&& !"".equalsIgnoreCase(getManagedBean().getScQt().getQtTitle())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"qtTitle", 
					SearchOperator.OPERATOR_STARTS_WITH.value(), 
					getManagedBean().getScQt().getQtTitle()));
		  }
		
		
		if (getManagedBean().getScQt().getPublishDate() != null
				&& !"".equals(getManagedBean().getScQt().getPublishDate())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"publishDate", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					getManagedBean().getScQt().getPublishDate()));

		  }
		
		if (getManagedBean().getScQt().getClosingDate() != null
				&& !"".equals(getManagedBean().getScQt().getClosingDate())) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean(
					"closingDate", 
					SearchOperator.OPERATOR_EQUALS.value(), 
					getManagedBean().getScQt().getClosingDate()));

		  }
		
		
		     qtReqDTO.setQtSearchBean(searchBean);
		     this.getManagedBean().setSearchBean(searchBean);
	    try {
			
	        detailSO=BusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
		    qtResDTO = detailSO.getQuationTenders(qtReqDTO);
			
		   } catch (Exception ex) {
			       ex.printStackTrace();
		   } finally {	
			     detailSO = null;
		   }
		
	    this.getManagedBean().setScQtSearchlist(qtResDTO.getQtResultBean().getResultList());
		return qtResDTO.getQtResultBean().getResultList();
	}
	
	
	/**
	 *  Sub Filtration for Tabs
	 */
	public void onTabChange(TabChangeEvent event) {
			log.info("*** TabChange Event **** ");
			IQTDetailSO detailSO = null;
			QuationTenderReqDTO qtReqDTO= new QuationTenderReqDTO();
			QuationTenderResDTO qtResDTO = null;
		    Long statusFilter = null ;
			System.out.println("tab index :  " + this.getManagedBean().getActiveTabIndex());
			ISearchBean<ScQt> searchTabBean = new SearchBean<ScQt>();
			
		    if (getManagedBean().getActiveTabIndex().equals(QTConstants.NB_PUBLISH_STATUS)) {
		    	statusFilter=QTStatusConst.QT_PUBLISH;
		    	
		    } else if (getManagedBean().getActiveTabIndex().equals(QTConstants.NB_CHANGES_STATUS)) {
		    	
		    } else if (getManagedBean().getActiveTabIndex().equals(QTConstants.NB_CLOSED_STATUS)) {
		    	
		    } else if (getManagedBean().getActiveTabIndex().equals(QTConstants.NB_PRICE_SCHEDULE_STATUS)) {
		    	
		    } else if (getManagedBean().getActiveTabIndex().equals(QTConstants.NB_AWARDED_STATUS)) {
		    	       statusFilter=QTStatusConst.QT_AWARDED;
		    }
		        
		     // status Tab Filtering
			 if (statusFilter!= null && !"".equals(statusFilter)) {
				 searchTabBean.getDataFilterBeans().add(new SearchFilterBean(
					   "pmStatus.statusId", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						statusFilter));
			  }
			 
			 
			 for( SearchFilterBean searchFilter: this.getManagedBean().getSearchBean().getDataFilterBeans()){
				  searchTabBean.getDataFilterBeans().add(new SearchFilterBean(
							 searchFilter.getSearchField(), 
							 searchFilter.getSearchOperator(), 
							 searchFilter.getSearchValue()));
			 }
			 
			 qtReqDTO.setQtSearchBean(searchTabBean);
			 this.getManagedBean().setSearchBean(searchTabBean);
				 
	     try {
						
			  detailSO=BusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
			  qtResDTO = detailSO.getQuationTenders(qtReqDTO);

						
			} catch (Exception ex) {
					ex.printStackTrace();
						
			} finally {	
					   detailSO = null;
			}
	         searchTabBean.getDataFilterBeans().clear(); // to clear all previous Objects
		    this.getManagedBean().setScQtSearchlist(qtResDTO.getQtResultBean().getResultList());
     }
	
	
	/**
	 *  Sub Filtration for Today , YesterDay, Past days
	 */
	public void subFilter() {
			log.info("*** TabChange Event **** ");
			IQTDetailSO detailSO = null;
			QuationTenderReqDTO qtReqDTO= new QuationTenderReqDTO();
			QuationTenderResDTO qtResDTO = null;
			ISearchBean<ScQt> searchBean = this.getManagedBean().getSearchBean();

		    searchBean.getDataFilterBeans().add(new SearchFilterBean(
				 "publishDate", 
				  SearchOperator.OPERATOR_EQUALS.value(), 
				  new Date()));
				 
			 qtReqDTO.setQtSearchBean(searchBean);
				  
			 this.getManagedBean().setSearchBean(searchBean);
				 
	     try {
						
			  detailSO=BusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, IQTDetailSO.class);
			  qtResDTO = detailSO.getQuationTenders(qtReqDTO);
						
			} catch (Exception ex) {
					ex.printStackTrace();
						
			} finally {	
					   detailSO = null;
			}
					
		    this.getManagedBean().setScQtSearchlist(qtResDTO.getQtResultBean().getResultList());
     }
	
	
	/**
	 *  Qtn / Tender Tab qtnTenderDetails
	 */
	public String qtnTenderDetails() {  
		log.info("** Qtn / TenderDetsils **");
		IQTNoticeBoardSO qtNoticeBoardSO = null;
		NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();	
		
		try {
			qtNoticeBoardSO=BusinessDelegate.getInstance().getServiceObject(QTConstants.SS_MODULE, IQTNoticeBoardSO.class);
			if (getManagedBean().getSelectedScQt() != null) {
				ScQt selectedScQt = this.getManagedBean().getSelectedScQt();
				reqDTO.setQtId(selectedScQt.getQtId());
				
				NoticeBoardResDTO resDTO = qtNoticeBoardSO.retrieveQtNoticeDtl(reqDTO);
				if (resDTO.getScQt() != null) {
				    ScQt scQt = resDTO.getScQt();
				    
				 // scQtCriteria for  Multiple Locality, AllStates and PaidUpCaptial 
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
				    this.getManagedBean().setScQt(scQt); // Qtn/ Tender notice Details 
				}
				
				// Category List
				if (resDTO.getCategoryResultBean() != null) {
				    List<ScQtCategory> catCodelist = resDTO.getCategoryResultBean().getResultList();
				    System.out.println("Category Code list: " + catCodelist.size());
				    this.getManagedBean().setCategoryList(catCodelist);
				}
				
				// LocCriteria List
				if (resDTO.getLocCriteriaResultBean() != null) {
					List<ScQtLocCriteria> loclist = resDTO.getLocCriteriaResultBean().getResultList();
					System.out.println("LocCriteria list: " + loclist.size());
					this.getManagedBean().setLocationList(loclist);
						
				}
				
				// ZoneCriteria List
				if (resDTO.getZoneCriteriaResultBean() != null) {
					List<ScQtZoneCriteria> zonelist = resDTO.getZoneCriteriaResultBean().getResultList();
					System.out.println("ZoneCriteria list: " + zonelist.size());
					this.getManagedBean().setZoneList(zonelist);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			qtNoticeBoardSO = null;
		}
		
		this.getManagedBean().setTabIndex(0);
		return NAV_TO_QTN_TENDER_DETAILS_PAGE;
	}
	
	
	
	/**
	 *  Drop down for Ministry
	 */
	public void populateMinistryList() {
		   log.info("populate Ministry List");
		   
		    // Get Ministry
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());

			IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			List<PmOrgProfile> ministryList = resDTO.getOrgProfileList();

			this.getManagedBean().setMinistryList(ministryList);
			
		}
	
	/**
	 * Handle Department dropdown changed
	 * - reset selected ptj profile and populate list of department.
	 * 	 */
	public void handleMinistryProfileChanged() {
		log.info("handleMinistryProfileChanged");
		Long parentOrgProfileId = this.getManagedBean().getScQt()
				.getMinistryOrgProfile().getOrgProfileId();
		
		if (parentOrgProfileId != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				
				List<PmOrgProfile> deptList = resDTO.getOrgProfileList();

				this.getManagedBean().setDepartmentList(deptList);
			
		} else {
			this.getManagedBean().setDepartmentList(new ArrayList<PmOrgProfile>());
		}
	}
	
	
	/**
	 * Handle Department dropdown changed 
	 * - reset selected ptj profile and populate list of division.
	 */
	public void handleDepartmentProfileChanged() {
		log.info("handleDepartmentProfileChanged");
		Long parentOrgProfileId = this.getManagedBean().getScQt()
				.getDepartmentOrgProfile().getOrgProfileId();

		if (parentOrgProfileId != null) {
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				reqDTO.setParentOrgProfileId(parentOrgProfileId);

				IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);

				this.getManagedBean().setDivisionList(resDTO.getOrgProfileList());
			
		} else {
			this.getManagedBean().setDivisionList(new ArrayList<PmOrgProfile>());
		}
	}
	
	
	/**
	 * To retrieve states based on country code.
	 */
	public List<PmState> getStateDropdownValues() {
		List<PmState> statelist = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1l);
			StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
			
			if (resDTO != null && !resDTO.getStateList().isEmpty()) {
				this.getManagedBean().setStates(resDTO.getStateList());
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return statelist;
	}
	
	/**
	 * To retrive districts based on state value changes.  
	 */
	public void handleStateChange() {
		Long stateId = this.getManagedBean().getPmState().getStateId();
		
		if (stateId != null) {
			DistrictReqDTO reqDTO = new DistrictReqDTO();
			reqDTO.setStateId(stateId);
			IDistrictSO districtSO=EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IDistrictSO.class);
			DistrictResDTO resDTO = districtSO
					.retriveDistrictListByStateID(reqDTO);
			
			 this.getManagedBean().setDistricts(resDTO.getDistrictList());
		    
		}
	 }
	
		/*
		 * Method to load the categories list for notice board
		 */
		public void retriveCategories1() {
			ICategorysSO categorysSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICategorysSO.class);
		
			List<PmCategoryL1> l1categoryList = this.getManagedBean()
					.getCategoryL1s();
			if (l1categoryList == null || !l1categoryList.isEmpty()) {
				CategorysResDTO catResDTO = categorysSO.retiveCategoryL1List();
				if (catResDTO.getCategoryL1List() != null
						&& !catResDTO.getCategoryL1List().isEmpty()) {
					this.getManagedBean().setCategoryL1s(
							catResDTO.getCategoryL1List());
				}
			}
		}
	
		/*
		 * Method to call onchange of L1category
		 */
		public void handleL2CategoryChange() {
			ICategorysSO categorysSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICategorysSO.class);
			Long categoryL1Id = this.getManagedBean().getPmCategoryL1().getCategoryL1Id();
			
			if (null != categoryL1Id) {
				CategorysReqDTO reqDTO = new CategorysReqDTO();
				reqDTO.setL1CategoryId(categoryL1Id);
				CategorysResDTO categorysResDTO = categorysSO
						.retiveCategoryL2ListByL1CategoryId(reqDTO);
				List<PmCategoryL2> catL2List = categorysResDTO.getCategoryL2List();
				this.getManagedBean().setCategoryL2s(catL2List);
			}
		}
	
		/*
		 * Method to call onchange of L2category
		 */
		public void handleL3CategoryChange(AjaxBehaviorEvent event) {
			ICategorysSO categorysSO = BusinessDelegate.getInstance()
					.getServiceObject("pm", ICategorysSO.class);
			Long categoryL2Id = this.getManagedBean().getPmCategoryL2().getCategoryL2Id();
			if (null != categoryL2Id) {
				CategorysReqDTO reqDTO = new CategorysReqDTO();
				reqDTO.setL2CategoryId((Long)categoryL2Id);
				CategorysResDTO categorysResDTO = categorysSO
						.retiveCategoryL3ListByL2CategoryId(reqDTO);
				List<PmCategoryL3> catL3List = categorysResDTO.getCategoryL3List();
				this.getManagedBean().getCategoryL3s().clear();
				this.getManagedBean().setCategoryL3s(catL3List);
			}
		}
		
		@PostConstruct
	    public void init() {
	        if (getManagedBean().getScQt() == null) {
	            getManagedBean().setScQt(new ScQt());
	            getManagedBean().getScQt().setPmOrgProfile(new PmOrgProfile());
	            getManagedBean().getScQt().setMinistryOrgProfile(new PmOrgProfile());
	            getManagedBean().getScQt().setDepartmentOrgProfile(new PmOrgProfile());
	            getManagedBean().setPmState(new PmState());
	            getManagedBean().setPmDistrict(new PmDistrict());
	            getManagedBean().setPmCategoryL1(new PmCategoryL1());
	            getManagedBean().setPmCategoryL2(new PmCategoryL2());
	            getManagedBean().setPmCategoryL3(new PmCategoryL3());
	            getManagedBean().setPmStatus(new PmStatus());
	            
	      
	        }
	       
	    }
	
	/*
	 * Method to call dynamic tabs for notice board search
	 */
	public void getTabs(){
			List<Tab> tabs = new ArrayList<Tab>();
			Tab tab1= new Tab();
			tab1.setTitle("Publish");
			tabs.add(tab1);
			
			Tab tab2= new Tab();
			tab2.setTitle("Changes");
			tabs.add(tab2);
			
			Tab tab3= new Tab();
			tab3.setTitle("Closed");
			tabs.add(tab3);
			
			Tab tab4= new Tab();
			tab4.setTitle("Price Schedule");
			tabs.add(tab4);
			
			Tab tab5= new Tab();
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
           getManagedBean().getScQt().setMinistryOrgProfile(new PmOrgProfile());
           getManagedBean().getScQt().setDepartmentOrgProfile(new PmOrgProfile());
         
		 }
		return NAV_TO_NOTICE_BOARD_SEARCH_PAGE;

	}
	
	/**
	 *  qtnTenderDocs
	 */
	public String qtnTenderDocs() {
		log.info("** qtnTenderDocs **");
		this.getManagedBean().setTabIndex(2);
	   return NAV_TO_QTN_TENDER_DETAILS_PAGE;
	}
	
	
	
	
	
}
