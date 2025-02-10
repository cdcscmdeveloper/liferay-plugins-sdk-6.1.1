/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.JoinType;

import my.ep.biz.bean.sc.QtAmendMentBean;
import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.sc.QtCommitteeMemberBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.bean.search.sc.QTSearchBean;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTBriefingSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTMeetingSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sc.scretry.IScRetrySO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.tpl.spec.ITemplateSpecSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.comparator.sc.WorkflowStatusComparator;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.CompositeValidityConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MapKeyEnum;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.BidStatusConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.ScDisruptionConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularDetailResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.city.CityReqDTO;
import my.ep.dto.pm.common.city.CityResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.division.DivisionReqDTO;
import my.ep.dto.pm.common.division.DivisionResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.bidding.BiddingReqDTO;
import my.ep.dto.sc.bidding.BiddingResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.document.QTDocumentReqDTO;
import my.ep.dto.sc.document.QTDocumentResDTO;
import my.ep.dto.sc.quationtender.BriefingReqDTO;
import my.ep.dto.sc.quationtender.BriefingResDTO;
import my.ep.dto.sc.quationtender.MeetingReqDTO;
import my.ep.dto.sc.quationtender.MeetingResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationReqDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.scretry.ScRetryReqDTO;
import my.ep.dto.sc.scretry.ScRetryResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecReqDTO;
import my.ep.dto.tpl.spec.TemplateSpecResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.helper.Paginator;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemGroup;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmDivision;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmGo;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.bidding.ScBidSchedule;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScRemark;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoaBasic;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoiForQt;
import my.ep.persistence.entity.sc.quationtender.ScCloseQTAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCloseQTRemark;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMemberHistory;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtAppRequestRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalReqAttachement;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBSVRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtBudget;
import my.ep.persistence.entity.sc.quationtender.ScQtCategory;
import my.ep.persistence.entity.sc.quationtender.ScQtCheckList;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteeActivity;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtDeduction;
import my.ep.persistence.entity.sc.quationtender.ScQtDocReport;
import my.ep.persistence.entity.sc.quationtender.ScQtFinalization;
import my.ep.persistence.entity.sc.quationtender.ScQtLocCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtRequestContent;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.scretry.ScQtDisruptionLog;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
import my.ep.web.backingbean.ct.circular.CircularBackingBean; //Fix - #30866
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.PTJLocalityCode;
import my.ep.type.pm.RoleCode;
import my.ep.util.CustomUtils;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.approval.ApprovalBackingBean;
import my.ep.web.backingbean.sc.bidding.BiddingBackingBean;
import my.ep.web.backingbean.sc.bidding.ScheduleBiddingBackingBean;
import my.ep.web.backingbean.sc.loa.DirectLOABackingBean;
import my.ep.web.backingbean.sc.loa.LOADetailBackingBean;
import my.ep.web.backingbean.sc.loa.QtLOABackingBean;
import my.ep.web.backingbean.sc.loiloa.LOIDetailBackingBean;
import my.ep.web.backingbean.sc.supplierfinalization.SupplierFinalizationBackingBean;
import my.ep.web.backingbean.sc.supplierproposal.SPDetailBackingBean;
import my.ep.web.backingbean.tpl.doc.TemplateDocBackingBean;
import my.ep.web.backingbean.tpl.spec.TemplateSpecBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.common.ZonesBean;
import my.ep.web.bean.sc.quatationtender.BriefingZoneBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.UserTasks;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;										   
import my.ep.web.util.pm.helper.PmCommonUtil; // #30477											 
import my.ep.web.util.sc.helper.ApproverSettings;
import my.ep.web.util.sc.helper.LocationSingleton;
import my.ep.web.util.sc.helper.QTFieldMapper;
import my.ep.web.util.sc.helper.SourcingLocationHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.util.Constants;

/**
 * The Class QTDetailBackingBean.
 * 
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final long serialVersionUID = 1L; // #Enhancement 9 CRs

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);
	
	
	private static final int FORM_NO_ACTION = 0;
	private static final int FORM_SC = 1;
	private static final int FORM_OC = 2;
	private static final int FORM_TEC = 4;
	private static final int FORM_FEC = 8;
	
	private static final int REFORM_NO_ACTION = 0;
	private static final int REFORM_SC = 1;
	private static final int REFORM_OC = 2;
	private static final int REFORM_TEC = 4;
	private static final int REFORM_FEC = 8;
	
	// #25692 -start-
	private static final String menuItemDetails = "MenuItemDetails";
	private static final String menuItemSuppQualificationCriteria = "MenuItemSuppQualificationCriteria";
	private static final String menuItemCommitteeAppointment = "MenuItemCommitteeAppointment";
	private static final String menuItemApproval = "MenuItemApproval";
	private static final String menuItemBriefSiteVisit = "MenuItemBriefSiteVisit";
	private static final String menuItemPublishDetail = "MenuItemPublishDetail";
	private static final String menuItemPaymentDeduction = "MenuItemPaymentDeduction";
	private static final String menuItemDocuments = "MenuItemDocuments";
	private static final String menuItemActivities = "MenuItemActivities";
	private static final String menuItemPublicationApproval = "MenuItemPublicationApproval";
	private static final String menuItemBPKApproval = "MenuItemBPKApproval";
	private static final String menuItemCommitteeApproval = "MenuItemCommitteeApproval"; // ***25692#31
	private static final String menuItemCommitteeApprovalEcOcTecFec = "MenuItemCommitteeApprovalEcOcTecFec";
	private static final String menuItemPublicationApproverAssignment = "MenuItemPublicationApproverAssignment";
	// #25692 -end-
	
	//29464-start
	private static final String menuItemSpecificationCommitteeAssignment = "MenuItemSpecificationCommitteeAssignment";
	//29464-end
	
	public static final String TEMP_MB_KEY = "___823452381_76";
	
	// #25692 -start-
	public String getMenuItemDetails() {
		return menuItemDetails;
	}

	public String getMenuItemSuppQualificationCriteria() {
		return menuItemSuppQualificationCriteria;
	}

	public String getMenuItemCommitteeAppointment() {
		return menuItemCommitteeAppointment;
	}

	public String getMenuItemApproval() {
		return menuItemApproval;
	}

	public String getMenuItemBriefSiteVisit() {
		return menuItemBriefSiteVisit;
	}

	public String getMenuItemPublishDetail() {
		return menuItemPublishDetail;
	}

	public String getMenuItemPaymentDeduction() {
		return menuItemPaymentDeduction;
	}

	public String getMenuItemDocuments() {
		return menuItemDocuments;
	}

	public String getMenuItemActivities() {
		return menuItemActivities;
	}

	public String getMenuItemPublicationApproval() {
		return menuItemPublicationApproval;
	}

	public String getMenuItemBPKApproval() {
		return menuItemBPKApproval;
	}

	// ***25692#31 - start
	public String getMenuItemCommitteeApproval() {
		return menuItemCommitteeApproval;
	}
	// ***25692#31 - end
	
	public String getMenuItemCommitteeApprovalEcOcTecFec() {
		return menuItemCommitteeApprovalEcOcTecFec;
	}	
	
	public Long[] getCmtOrderedStatuses(){
		return QTStatusConst.CMT_ORDERED_STATUSES;
	}
	// #25692 -end-
	
	// 29464-start
	public String getMenuItemSpecificationCommitteeAssignment() {
		return menuItemSpecificationCommitteeAssignment;
	}
	// 29464-end

	// 25201#52 -start-
	private RoleCode[] deskOfficerRoleCodes = {
			RoleCode.DESK_OFFICER, 
			RoleCode.PBB_SECRETARY, 
			RoleCode.PBA_SECRETARY,
			RoleCode.QCA_SECRETARY,
			RoleCode.QCB_SECRETARY,
			RoleCode.FPB_SECRETARY,
			// ***17376#55
			RoleCode.PPB_SECRETARY 
			};
	// 25201#52 -end-
	
	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public QTDetailBackingBean() {
		log.debug("inside constructor");
		log.debug("@@@@@@@@@ Managed Bean @@@@@@@@@ = "+this.getManagedBean());

	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		
		ScQt scQt = this.getManagedBean().getQt();
		if(scQt!= null){
			message += " @qtNo = "+scQt.getQtNo();
		}
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		/*else{
			log.info(message);
		}*/
		
	}

	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init() {
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		if (null == qtnTenderSearchBean.getMinistryProfile()
				|| null == qtnTenderSearchBean.getPtjGroupProfile()
				|| null == qtnTenderSearchBean.getPtjProfile()) {
			getDefaultPtjAndMinistryValues();
		}

		// 9019 - Start (Harun) rendering Group Item that left blank but panel is selected 
		//       14072022 Add checking ItemGroup due to null when simulate at UAT  
		if (null != this.getManagedBean().getQt()) {
			if (this.getManagedBean().getQt().isPanel() && null == this.getManagedBean().getQt().getItemGroup()) {
				this.getManagedBean().getQt().setItemGroup(new CtPanelItemGroup());
			}
		}
		// 9019 - End (Harun)
	}
	
	/**
	 * Gets the search qt data model.
	 * 
	 * @return the search qt data model
	 */
	public IPagingDataModel<ScQt> getSearchQTDataModel() {
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		if (searchQTDataModel == null) {
			searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean()
					.getQtnTenderSearchBean());
			getManagedBean().setSearchQTDataModel(searchQTDataModel);
		}
		
		// #19336, #25201, #17376, #19287 - start
		//initialization of loi and loa data model
		if(isQTMaintenanceScreen() || isQTSearchScreen()){
			getSearchQtLoiDataModelPriv(false);
			getSearchLoaForQtDataModelPriv(false);
		}
		
		toggleView();
		// #19336, #25201, #17376, #19287 - end

		return searchQTDataModel;
	}

	// #19336, #25201, #17376, #19287 - start	
	public IPagingDataModel<ScLoiForQt> getSearchQtLoiDataModel() {
		return getSearchQtLoiDataModelPriv(true);
	}
	
	public IPagingDataModel<ScLoaForQt> getSearchLoaForQtDataModel() {
		return getSearchLoaForQtDataModelPriv(true);
	}
	
	private IPagingDataModel<ScLoiForQt> getSearchQtLoiDataModelPriv(boolean toggleView) {
		IPagingDataModel<ScLoiForQt> searchLoiDataModel = getManagedBean()
				.getSearchLoiForQtDataModel();
		
		if (searchLoiDataModel == null) {
			searchLoiDataModel = new LOIPagingDataModel("searchQTLoaLois", getManagedBean()
					.getQtnTenderSearchBean());
			getManagedBean().setSearchLoiForQtDataModel(searchLoiDataModel);
		}
		
		if(toggleView){
			toggleView();
		}
		
		return searchLoiDataModel;
	}
	
	private IPagingDataModel<ScLoaForQt> getSearchLoaForQtDataModelPriv(boolean toggleView) {
		IPagingDataModel<ScLoaForQt> searchLoaDataModel = getManagedBean()
				.getSearchLoaForQtDataModel();
		if (searchLoaDataModel == null) {
			searchLoaDataModel = new LOAPagingDataModel("searchQTLoaLois", getManagedBean()
					.getQtnTenderSearchBean());
			getManagedBean().setSearchLoaForQtDataModel(searchLoaDataModel);
		}
		if(toggleView){
			toggleView();
		}
		
		return searchLoaDataModel;
	}
	
	public String toggleView(){
		RequestContext rc = ((RequestContext) FacesContext.getCurrentInstance().getAttributes().get(Constants.REQUEST_CONTEXT_ATTR));
			
		if (getManagedSearchBeanDocumentTypeIsLOA()||getManagedSearchBeanDocumentTypeIsLOI()){
			if(rc!=null)rc.execute("toggleView('"+ParameterConst.DOCTYPE_LETTER_OF_AWARD+"');");
			return ParameterConst.DOCTYPE_LETTER_OF_AWARD;
		} else {
			if(rc!=null)rc.execute("toggleView('"+ParameterConst.DOCTYPE_QUOTATION+"');");
			return ParameterConst.DOCTYPE_QUOTATION;
		}
	}
	// #19336, #25201, #17376, #19287 - end										   
																		 
	private void renewQTSesionObject() {
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		// #19336, #25201, #17376, #19287 - start
		IPagingDataModel<ScLoaBasic> searchLoaDataModel = getManagedBean()
				.getSearchLoaDataModel();
		IPagingDataModel<ScLoiForQt> searchLoiForQtDataModel = getManagedBean()
				.getSearchLoiForQtDataModel();
		// #19336, #25201, #17376, #19287 - end
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean()
				.getQtnTenderSearchBean();
		
		super.renewSesionObject();
		
		getManagedBean().setSearchQTDataModel(searchQTDataModel);
		// #19336, #25201, #17376, #19287 - start
		getManagedBean().setSearchLoaDataModel(searchLoaDataModel);
		getManagedBean().setSearchLoiForQtDataModel(searchLoiForQtDataModel);
		// #19336, #25201, #17376, #19287 - end
		getManagedBean().setQtnTenderSearchBean(qtnTenderSearchBean);
	}

	/**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		
		PmOrgProfile orgProfile = null;
		try {
			orgProfile = getOrgProfileByLoginUser();
		} catch (EpException e) {
			String msgCode = MessageConst.ERROR_770224;
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(msgCode, null,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())
									+ " (" + msgCode + ")"));
		}
		if (orgProfile != null) {
			String orgType = orgProfile.getOrgTypeCode().getParameterCode();
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				ministryProfile = orgProfile;
			} else if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
				ptjGroupProfile = orgProfile;
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
				ptjProfile = orgProfile;
				ptjGroupProfile = retrieveJabatanOrgProfile(
						orgProfile.getOrgProfileId());
				ministryProfile = retrieveMinistryOrgProfile(
						orgProfile.getOrgProfileId());
			}
		}
		
		qtnTenderSearchBean.setMinistryProfile(ministryProfile);
		qtnTenderSearchBean.setPtjGroupProfile(ptjGroupProfile);
		qtnTenderSearchBean.setPtjProfile(ptjProfile);
	}
	
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);

				// #30531[UAT], #28611[SIT] - Start.
				String parameterCodeType = null;
				
		        if (null != userId) {
		            IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ, IPMReportSO.class);
		            List<Object[]> paramcodeLst = reportSO.retrieveParameterCodeByUserid(userId);
		            
		            if (CollectionUtils.isNotEmpty(paramcodeLst)) {
		                for (Object[] object : paramcodeLst) {
		                    parameterCodeType = object[0].toString();
		                }
		            }
		        }
		        
		        if (StringUtils.isNotEmpty(parameterCodeType)
                    && (parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))) {
		          reqDTO.setSupplierUser(true);
		        }
		        // #30531[UAT], #28611[SIT] = End.

		        userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);

				if (resDTO.getUserOrgList() != null
						&& resDTO.getUserOrgList().size() > 0) {
					for (PmUserOrg pmUserOrg : resDTO.getUserOrgList()) {
						if (pmUserOrg.getPmOrgProfile() != null) {
							pmOrgProfile = pmUserOrg.getPmOrgProfile();
							break;
						}
					}
				}
			} finally {
				userSO = null;
			}	
		}
		
		return pmOrgProfile;
	}

	/**
	 * To add empty row to gosodo table.
	 */
	public void addGoSoDoAction() {
		debugLogger("addGoSoDoAction");
		List<ScQtBudget> gosodos = this.getManagedBean().getQt()
				.getScQtGosodoBudgets();
		if (gosodos == null) {
			gosodos = new ArrayList<ScQtBudget>();
			this.getManagedBean().getQt().setScQtGosodoBudgets(gosodos);
		}
		ScQtBudget scQtGosodoBudget = new ScQtBudget();
		scQtGosodoBudget.setPmGo(new PmGo());
		scQtGosodoBudget.setPmSo(new PmSo());
		scQtGosodoBudget.setScQt(this.getManagedBean().getQt());
		gosodos.add(scQtGosodoBudget);
	}

	/**
	 * Delete go so do action.
	 */
	public void deleteGoSoDoAction() {
		debugLogger("deleteGoSoDoAction");
		List<ScQtBudget> budgetList = getManagedBean().getQt()
				.getScQtGosodoBudgets();
		List<ScQtBudget> selectedBudgetList = this.getManagedBean()
				.getSelectedGosodoBudgets();
		if (budgetList.size() == selectedBudgetList.size()) {
			budgetList.clear();
		} else {
			for (ScQtBudget scQtBudget : selectedBudgetList) {
				budgetList.remove(scQtBudget);
			}
		}
	}

	/*
	 * Method to delete gosodo from table
	 */
	/**
	 * Delete go so do item.
	 * 
	 * @param changeEvent
	 *            the change event
	 */
	public void deleteGoSoDoItem(ValueChangeEvent changeEvent) {
		ScQtBudget gosodoBudget = (ScQtBudget) changeEvent.getNewValue();
		List<ScQtBudget> gosodos = this.getManagedBean().getGosodos();
		if (gosodos.contains(gosodoBudget)) {
			gosodos.remove(gosodoBudget);
		}
	}

	/*
	 * Onchange of go's to fetch the so's list
	 */
	/**
	 * Handle go changed.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 * @return the list
	 */
	public List<PmSo> handleGoChanged(ScQtBudget qtGosodoBudget) {
		debugLogger("handleGoChanged");
		PmGo pmGo = qtGosodoBudget.getPmGo();
		if (pmGo != null && pmGo.getGoId() != null) {
			List<PmGo> pmGoList = this.getManagedBean().getGoList();
			for (PmGo go : pmGoList) {
				if (go.getGoId().longValue() == pmGo.getGoId().longValue()) {
					qtGosodoBudget.setPmGo(go);
					debugLogger("set GO");
					break;
				}
			}
			// get the so list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(pmGo.getGoId());
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveSOListByGOCode(reqDTO);
			this.getManagedBean().setSoList(objResDTO.getPmSoList());
			return objResDTO.getPmSoList();
		} else {
			this.getManagedBean().setSoList(new ArrayList<PmSo>());
		}
		// qtGosodoBudget.setPmSo(new PmSo());
		// handleSoChanged(qtGosodoBudget);
		return null;
	}

	/*
	 * OnChange of So's get the do's list
	 */
	/**
	 * Handle so changed.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 */
	public void handleSoChanged(ScQtBudget qtGosodoBudget) {
		debugLogger("handleSoChanged");
		PmSo pmSo = qtGosodoBudget.getPmSo();
		if (pmSo != null && pmSo.getSoId() != null) {
			List<PmSo> pmSoList = this.getManagedBean().getSoList();
			for (PmSo so : pmSoList) {
				if (so.getSoId().longValue() == pmSo.getSoId().longValue()) {
					qtGosodoBudget.setPmSo(so);
					debugLogger("set SO");
					break;
				}
			}
			// get the do list and set it to managed bean property
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setSoId(pmSo.getSoId());
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveDOListBySOCode(reqDTO);
			this.getManagedBean().setDoList(objResDTO.getPmDoList());
		} else {
			this.getManagedBean().setDoList(new ArrayList<PmDo>());
		}
	}

	/**
	 * Handle do change.
	 * 
	 * @param qtGosodoBudget
	 *            the qt gosodo budget
	 */
	public void handleDoChange(ScQtBudget qtGosodoBudget) {

	}

	/**
	 * Method to save the QT details.
	 */
	public void saveQtDetailsAction() {
		
		debugLogger("start of saveQtDetailsAction");
		this.getManagedBean().setSaveFlow(true);
		try {
			
			Long currentStatus = this.getManagedBean().getCurrentStatusId();
			PmOrgProfile qtOrgProfile = this.getManagedBean().getQt().getPmOrgProfile();
			if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(currentStatus)
					&& qtOrgProfile != null
					&& !isPtjAddressTypePresent(qtOrgProfile.getOrgProfileId())) {
				throw new EpException(MessageConst.ERROR_760450);
			}
			
			// #31610, #9019 -start-
			ScQt scQt = this.getManagedBean().getQt();
			
			/*
			 * Bug 31610 - To Exclude 9019 to allow 31610 to be merge into SIT
			 * NOTE: 9019 WHEN COMMIT MAKE SURE CHANGE SUCCESSMESSAGE FROM THIS INTO THE ONE THAT 9019 
			 * INCLUDE BACK 9019
			 * */
			boolean isFirstSave = scQt.getQtNo() == null && scQt.isBrandNew() ? true : false;
			// #31610, #9019 -end-
			
			validateQtDetails();
			
			checkDemoPresentationByStatus(); // #28784

			// #31610, #9019 -start-
			/*
			 * BUG 31610 Added WorkflowStatus to cater qt maintenance search result not working
			 */			
			if (isFirstSave) {
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
				reqDTO.setQtdetail(this.getManagedBean().getQt());
				
				IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				detailSO.updateDraftWorkflowStatus(reqDTO);

				// #9019 -start-
				successMessage(MessageConst.INFO_7000044, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.TRUE);
				// #9019 -end-
			} else {
				// Enhancement #25729 -start-
				// 9019 Start - re-endorse change INFO_700004
				successMessage(MessageConst.INFO_7000108, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
				// 9019 -end-			
				// Enhancement #25729 -end-
			}
			// #31610, #9019 -end-

		} finally {

		}
		
	}

	/**
	 * Submit qt details action.
	 * 
	 * @return the string
	 */
	//Enhancement #25729 -start-
	public void submitQTDetailsAction() {
	//Enhancement #25729 -end-	

		// #29732 Start
		if (this.getManagedBean().getQt().getQtStatus().getStatusId().equals((QTStatusConst.QT_PENDING_QT_SUBMISSION))) {
			String changedCatCodeList = checkCatCodeChange();

			if (!changedCatCodeList.isEmpty()) {
				this.getManagedBean().setCatCodeList(changedCatCodeList);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
		}
		// #29732 End

		// Start Reopen 9019 #note-289
		boolean emptyRemarksExist = false;

		if (null != this.getManagedBean().getQt().getScQtRemarks()
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			for (int i = 0; i < this.getManagedBean().getQt().getScQtRemarks().size(); i++) {
				String userRemark = this.getManagedBean().getQt().getScQtRemarks().get(i).getRemark();
			
				if (null == userRemark || "".equals(userRemark)) {
					emptyRemarksExist = true;
					
					this.getManagedBean().setScQtRemarksEmpty(true);
				} else {
					emptyRemarksExist = false;
				}
			}

			if (emptyRemarksExist) {
				ScQt scQt = this.getManagedBean().getQt();
				List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

				for (ScQtRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(true);
				}
				
				this.getManagedBean().getQt().setScQtRemarks(qtRemarks);
			} else {
				ScQt scQt = this.getManagedBean().getQt();
				
				this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
			}
		}
		// End Reopen 9019 #note-289
		
		this.getManagedBean().setSaveFlow(false);
		IQTDetailSO detailSO;
		try {
			ScQt scQt_ = this.getManagedBean().getQt();
			PmOrgProfile qtOrgProfile = scQt_.getPmOrgProfile();
			if (qtOrgProfile != null
					&& !isPtjAddressTypePresent(qtOrgProfile.getOrgProfileId())) {
				throw new EpException(MessageConst.ERROR_760450);
			}
			
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			updateApprovalDetails(scQt_);
			//Required to fetch the approvers when are trying to Save/Submit from other than the Approval tab 
			//bug #30435, Enhancement #25077 - start
			if (!getManagedBean().isApproverTabLoaded()) {
				retrieveApprovalTabDtl();
			}
			//bug #30435, Enhancement #25077 - end
			
			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			FacesScopes facesScopes = new FacesScopes();
			quationTenderReqDTO.setToken(facesScopes
					.getBPMAuthenticationCtxToken());

			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			quationTenderReqDTO.setPayLoad(this.getManagedBean().getPayLoad());
			quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_SUBMIT);
			quationTenderReqDTO.setUserLocale(facesScopes.getUserLocale());
			quationTenderReqDTO.getQtdetail().setOtherApproval(true);
			quationTenderReqDTO.setDeskOfficer(facesScopes.getUserSessionBean());
			// 9019 #note-289 -start-
			quationTenderReqDTO.setScQtRemarksEmpty(this.getManagedBean().isScQtRemarksEmpty());
			// 9019 #note-289 -end-

			QuationTenderResDTO quationTenderResDTO = detailSO
					.submitQT(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();

		//Enhancement #25729 -start-
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, scQt.getQtNo(), Boolean.TRUE);

				debugLogger("end of submitQTDetailsAction");
			}
		} finally {
			detailSO = null;
		}
		//Enhancement #25729 -end-
	}

	/**
	 * Method to search the quationtenders based on serach date.
	 */
	public void searchQTAction() {
		// #19336, #25201, #17376, #19287 - start
		if (this.getManagedSearchBeanDocumentTypeIsLOA()) {
			getManagedBean().getSearchLoaForQtDataModel().reloadData();
		} else if (this.getManagedSearchBeanDocumentTypeIsLOI()) {
			getManagedBean().getSearchLoiForQtDataModel().reloadData();
		}
		else;
		// #19336, #25201, #17376, #19287 - end

		getManagedBean().getSearchQTDataModel().reloadData();
	}

	/**
	 * Method for clone search qts.
	 */
	public void cloneSearchQtAction() {
		getManagedBean().getCloneSearchQTDataModel().reloadData();
	}

	/**
	 * Gets the go list.
	 * 
	 * @return the go list
	 */
	public List<PmGo> getGoList() {
		debugLogger("getGoList");
		List<PmGo> goList = this.getManagedBean().getGoList();
		if (goList == null) {
			IObjectSO objectSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IObjectSO.class);
			ObjectResDTO objResDTO = objectSO.retrieveGOList();
			goList = objResDTO.getPmGoList();
			this.getManagedBean().setGoList(goList);
		}
		return goList;
	}

	/**
	 * Handle ptj name changed.
	 * 
	 */
	public void handlePTJNameChanged() {
		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);

			ScQt scQT = getManagedBean().getQt();

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgProfileId(scQT.getPmOrgProfile().getOrgProfileId());

			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileDtlByProfileId(reqDTO);
			
			PmOrgProfile orgProfile = resDTO.getPmOrgProfile();

			scQT.setPmOrgProfile(orgProfile);
			scQT.setOrgProfileId(orgProfile.getOrgProfileId());

		} finally {
			orgProfileSO = null;
		}
	}

	/**
	 * Goto qt search page.
	 * 
	 * @return the string
	 */
	public String gotoQtSearchPage() {
		debugLogger("gotoQtSearchPage");		
		this.getManagedBean().getSearchQTDataModel().reloadData(); // #Enhancement 9 CRs
		return QTConstants.SEARCH_PAGE;
	}
	
	// #Enhancement 9 CRs - start
	public String editQuationTenderDetails(ScQt scQt, boolean editable){
		return editQuationTenderDetails(scQt, editable, false, null);
	}
	// #Enhancement 9 CRs - end
	
	/**
	 * Method to return the maintance page.
	 * 
	 * @param scQt
	 *            the sc qt
	 * @param fullPath
	 * 			  full path of redirect page
	 * @param editable
	 * 			  editable
	 * @param backUrl 
	 * 			  usually view.viewId
	 * @return maintanacepage
	 */
	// 25201#109 -start-
	public String editQuationTenderDetails(ScQt scQt, boolean editable, boolean fullPath, String backUrl) {
	// 25201#109 -end-
		debugLogger("start editQuationTenderDetails");
		
		// #25692 -start-
		// #17376-153 -start-
		if (!isApprovalScreen() && !fullPath) {
		// #17376-153 -end-
			renewQTSesionObject();
		}
		// #25692 -end-
		
		debugLogger("editQuationTenderDetails, @Managedbean = "+ this.getManagedBean());
		this.getManagedBean().setReadOnly(!editable);
		this.getManagedBean().setMaintanceFlow(true);
		IQTDetailSO detailSO;
		String REDIRECT_PAGE = QTConstants.MAINTANANCE_PAGE;

		// 25201#109 -start-
		if (fullPath) {
			REDIRECT_PAGE = "/xhtml/sc/quatationtender/quatationtendercreation.xhtml";
		}
		
		if (!StringUtils.isBlank(backUrl)) {
			this.getManagedBean().setViewOriginalUrl(backUrl);
		}
		// 25201#109 -end-

		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			Long qtId = scQt.getQtId();
			
			// #28784 - Demo WIP
			boolean isQtNewVersion = validateSupportedTask(scQt.getQtNo(), CompositeValidityConst.PM_COMPOSITE_VALIDITY);
			this.getManagedBean().setNewVersion(isQtNewVersion);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setMaintanceFlow(true);
			reqDTO.setQtId(qtId);
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			ScQt selectedScQt = resDTO.getQtDetail();		

			if (null == selectedScQt.getContractType()) {
				selectedScQt.setContractType(new PmParameter());
			}
			if (null == selectedScQt.getProcurementTypeCatId()) {
				selectedScQt.setProcurementTypeCatId(new PmParameter());
			}
			if (selectedScQt.isPanel() && null == selectedScQt.getItemGroup()) {
				selectedScQt.setItemGroup(new CtPanelItemGroup());
			}
			if(selectedScQt.getFulfilmentType()==null){
				selectedScQt.setFulfilmentType(new PmParameter());
			}
			if(selectedScQt.getPmProcurementCategory()==null){
				selectedScQt.setPmProcurementCategory(new PmParameter());
			}	
			this.getManagedBean().getCtContract()
					.setContractNo(selectedScQt.getExistingContractNo());
			this.getManagedBean().setPtjSpecialApproval(
					selectedScQt.getPtjSpecialApproval());
			this.getManagedBean().setPanel(selectedScQt.isPanel());
			
			setAmmendMentBean(resDTO.getAmendMentBean());
			checkForPublishperiodAndValidityDays(selectedScQt,fc);
			Long currentStatus = selectedScQt.getQtStatus().getStatusId();
			this.getManagedBean().setCurrentStatusId(currentStatus);
			
			// #25692 -start-
			if (!isApprovalScreen() || this.getManagedBean().getQt() == null) {
				this.getManagedBean().setQt(selectedScQt);
			}
			// #25692 -end-

			// #28784 -start-
			boolean demoRequired = this.getManagedBean().getQt().isDemoRequired();
			Long status = this.getManagedBean().getQt().getQtStatus() != null ? this.getManagedBean().getQt()
					.getQtStatus().getStatusId() : null;

			if (null != status && !status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING)
					&& !status.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
					&& this.getManagedBean().isMaintanceFlow() && !demoRequired
					&& !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)
					&& !status.equals(QTStatusConst.CMT_PENDING_APPROVAL)
					&& !status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
					&& !status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)) {
				this.getManagedBean().setDemoEvalDisabled(true);
			}

			if (null != status && !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)
					&& !status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING)
					&& !status.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION)
					&& this.getManagedBean().isMaintanceFlow() && !status.equals(QTStatusConst.CMT_PENDING_APPROVAL)
					&& !status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
					&& !status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)) {
				this.getManagedBean().setDemoDisabled(false);
				this.getManagedBean().setDemoType(true);
				this.getManagedBean().setDemoAction(true);
			}

			checkDemoPresentationByStatus();
			// #28784 -end-

			if ( QTStatusConst.QT_CANCELLED.equals(currentStatus) && null == selectedScQt
							.getQtType()) {
				ScQtCommittee committee = getQtCommiteDtlsByQtIdNType("BO",
						selectedScQt.getQtId());
				List<ScQtCommittee> qtCommittee = new ArrayList<ScQtCommittee>();
				qtCommittee.add(committee);
				// selectedScQt.setScQtCommittees(qtCommittee);
				this.getManagedBean().setScQtCommittee(committee);

				List<ScCommitteeMember> committeMembers = committee
						.getScCommittee().getScCommitteeMembers();
				// Code for selected consolidate Person should be highlight..
				if (null != committeMembers) {
					for (ScCommitteeMember committeeMember : committeMembers) {
						if (committeeMember.isConsolidate()) {
							this.getManagedBean().setSelectScCommitteMember(
									committeeMember);
							break;
						}
					}
				}
				this.getManagedBean().setScCommitteeMemberList(committeMembers);
				this.getManagedBean()
						.setCommitteMember(new ScCommitteeMember());

			} else {

				if (this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					if (selectedScQt.isZonal()) {
						// Populate zone detail into tree node
						if (selectedScQt.getScQtSupplierCriteria() != null &&
								selectedScQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
							List<TplSpecZoneDtl> specZoneDtls = selectedScQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
							populateZoneDetailIntoTreeNode(selectedScQt, specZoneDtls);
						}
					}
				}
				this.getManagedBean().setGoList(resDTO.getGoList());

			}
			if (null != getManagedBean().getQt()
					&& null == getManagedBean().getQt()
							.getProcurementTypeCatId()) {
				getManagedBean().getQt().setProcurementTypeCatId(
						new PmParameter());
			}

			setScQtTrackingDairyObject(selectedScQt);
			if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean()
					.getCurrentStatusId())) {
				this.getManagedBean().setCreateQt(true);
				if (selectedScQt.getObtainSpecialApproval() != null
						&& selectedScQt.getObtainSpecialApproval()) {
					this.getManagedBean().setProcMethodDropdownPresent(
							checkForProcumentMethodDropdown(selectedScQt));
				}
				
			}

			// 9019 - Start (Harun) WIP Transaction add information message
			if (!getTaskByDocNoModule()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(
								MessageConst.INFO_700110, MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
			}
			// 9019 - End (Harun)

			// #29732 Start
			// qtmaintenance from audit search ,ADD ISREADONLY
			if ((this.getManagedBean().getQt().getQtStatus().getStatusId()
					.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)) && !this.getManagedBean().isReadOnly()) {
				if (isQTRoleMaintenanceValidation()) {
					this.getManagedBean().setCategoryDescriptionChange(true); // 29732
				}

				String changedCatCodeList = checkCatCodeChange();

				if (!changedCatCodeList.isEmpty()) {
					this.getManagedBean().setActiveIndex(1);
					retrieveSuppQualificationTabDtl();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
									"lbl.sc.category.has.changes.on.prerender.view.1", null,
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
									"lbl.sc.category.has.changes.on.prerender.view.2",
									new String[] { changedCatCodeList }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())));
				}
			}
			// #29732 End

			// 25037-start
			boolean isValidFor25037 = this.validateSupportedTask(scQt.getQtNo(),
					QTConstants.ALLOW_COMMITTEE_REASSIGNMENT);
			this.getManagedBean().setValidFor25037(isValidFor25037);
			// 25037-end
		} finally {
			detailSO = null;
		}
		return REDIRECT_PAGE;
	}
	
	/**
	 * Method to view Direct LOA Details.
	 * 
	 * @param scQt
	 *            the ScQt instance
	 * @param previousPageUrl
	 * 				the previous page url.
	 * @return the navigation page
	 */
	public String viewDirectLOADetails(ScQt scQt, String previousPageUrl) {
		debugLogger("viewDirectLOADetails");
		
		Long qtId = scQt.getQtId();
		Long loaId = findDocIdByDocType(qtId, ParameterConst.DOCTYPE_DIRECT_NEGO);
		
		return new DirectLOABackingBean().retriveLoaDetails(loaId, previousPageUrl, true);
	}	
	
	// ***19366#40 -start-
	public void viewLoiListDetails(ScQt scQt, String previousPageUrl) {
		Long qtId = scQt.getQtId();
		
		new LOIDetailBackingBean().preRenderLoiListView(qtId, previousPageUrl);
	}
	// ***19366#40 -end-

	// #19336, #25201, #17376, #19287 -start-
	/**
	 * Method to view Finalization Supplier Details.
	 * 
	 * @param scQt
	 *            the ScQt instance
	 * @param previousPageUrl
	 * 				the previous page url.
	 * @return the navigation page
	 */
	public String viewFinalizationSupplierDetails(ScQt scQt, String previousPageUrl) {
		debugLogger("viewFinalizationSupplierDetails");
		RoleCode[] justAllowRoleCodes = {RoleCode.BPK_AUDITOR};
		
		SupplierFinalizationBackingBean sfbb =  new SupplierFinalizationBackingBean();
		sfbb.renewSesionObject();
		
		return sfbb.editSupplierFinalizationDetails(scQt, previousPageUrl, false, justAllowRoleCodes);
	}
	// #19336, #25201, #17376, #19287 -end-
	
	/**
	 * Method to view Supplier Proposal Details.
	 * 
	 * @param scQt
	 *            the ScQt instance
	 * @param previousPageUrl
	 * 				the previous page url.
	 * @return the navigation page
	 */
	public String viewSupplierProposalDetails(ScQt scQt, String previousPageUrl) {
		
		debugLogger("viewSupplierProposalDetails");
		Long qtId = scQt.getQtId();
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		
		if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) || 
				ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL.equals(docType)) {
			return new SPDetailBackingBean().retriveSupplierProposalDtl(qtId, previousPageUrl, true);
		} else if (ParameterConst.DOCTYPE_BIDDING.equals(docType)) {
			return viewOnlineBidding(scQt,previousPageUrl); //#17376-144
			//return QTConstants.ONLINE_BIDDING_MAIN_PAGE; //#17376-144
		} else {
			Long docId = findDocIdByDocType(qtId, null);			
			if (docId != null) {
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
					return new LOIDetailBackingBean().retrieveSupplierLOIDtl(
							qtId, docId, previousPageUrl, true);
				} else if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)) {
					return new LOADetailBackingBean().retrieveSupplierLOADtl(
							qtId, docId, previousPageUrl, true);
				}	
			}
		}
		
		return null;
	}
	
	//#25201 - start	
	/**
	 * 
	 * @param obj
	 * 		instance of ScQt/ScLoiForQt/ScLoaForQt
	 * @param previousPageUrl
	 * @return
	 */
	public String viewSupplierProposalDetailsGen(Object obj, String previousPageUrl) {
		
		debugLogger("viewSupplierProposalDetails");
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		ScQt scQt = null;
		ScLoiForQt scLoiForQt = null;
		ScLoaForQt scLoaForQt = null;
		
		if(getManagedSearchBeanDocumentTypeIsLOA() || getManagedSearchBeanDocumentTypeIsLOI()){
			if (obj instanceof ScLoiForQt) {
				/* Bug #27412 START */
				scLoiForQt = (ScLoiForQt)obj;
				
				LOIDetailBackingBean bb = new LOIDetailBackingBean();
				
				scQt = scLoiForQt.getScLoiLoa()!=null?scLoiForQt.getScLoiLoa().getScQt():null;
				
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {					
					if (isMofSupplier()) {
						//SUPPLIER
						return bb.retrieveSupplierLOIDtl(
								scLoiForQt.getScLoiLoa().getScQt().getQtId(), scLoiForQt.getLoiId(), previousPageUrl, true, true);
					} else {
						//DESK OFFICER						
						bb.preRenderViewForQtMaintenenceScreen(scLoiForQt.getLoiId(), previousPageUrl);
					}
					
					return null;
				}
				/* Bug #27412 END */
			} else if (obj instanceof ScLoaForQt) {
				scLoaForQt = (ScLoaForQt)obj;
				
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)){
					Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
					otherArgs.put(MapKeyEnum.loiLoaSelected, scLoaForQt);
					
					scQt = scLoaForQt.getScLoiLoa()!=null?scLoaForQt.getScLoiLoa().getScQt():null;
					
					QtLOABackingBean bb = new QtLOABackingBean();
					LOADetailBackingBean ldb = new LOADetailBackingBean();
					
					if (isMofSupplier()) {
						/* Bug #27412 START */
						return ldb.retrieveSupplierLOADtl(
								scLoaForQt.getScLoiLoa().getScQt().getQtId(), scLoaForQt.getLoaId(), previousPageUrl, true);
						/* Bug #27412 END */
					} else {
						if(ParameterConst.DOCTYPE_DIRECT_NEGO.equalsIgnoreCase(scLoaForQt.getScLoiLoa().getDocType())){
							return viewDirectLOADetails(scQt, previousPageUrl);
						} else {
							otherArgs.put(MapKeyEnum.showTrackingDiary, Boolean.TRUE);
							bb.preRenderViewForQtMaintenance(scQt, previousPageUrl, otherArgs);
							
							return null;
						}
					}
				}
			} else
				;
		} else if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType) 
				|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(docType)){
			scQt = (ScQt)obj;
			
			if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
				viewLoiListDetails(scQt, previousPageUrl);
				
				return null;
			} else if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType)){
				// ***19287#42 , ******17376#55  
				if(scQt.getLoiLoa()!=null && ParameterConst.DOCTYPE_DIRECT_NEGO.equalsIgnoreCase(scQt.getLoiLoa().getDocType())){
					DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
					directLoaBackingBean.renewSesionObject();
					
					return viewDirectLOADetails(scQt, previousPageUrl);
				}else{	
					QtLOABackingBean bb = new QtLOABackingBean();
					bb.preRenderViewForQtMaintenance(scQt, previousPageUrl, null);
					
					return null;
				}
				
			} else {
				return null;
			}
		} else if (obj instanceof ScQt) {
			scQt = (ScQt)obj;
			
			if(	ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(docType)
				||  ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL.equals(docType)){
				return new SPDetailBackingBean().retriveSupplierProposalDtl(scQt.getQtId(), previousPageUrl, true);
			} else if (ParameterConst.DOCTYPE_BIDDING.equals(docType)) {
				return viewOnlineBidding(scQt,previousPageUrl); //#17376-144
			}
		} else
			;
		
		return null;
	}
	// #25201 -end-
	
	/**
	 * Find Document Id by document type
	 * 
	 * @param qtId
	 * 		the quotation / tender id
	 * @return	the document id
	 */
	public Long findDocIdByDocType(Long qtId, String docType) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			
			QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
			
			// #30451-Start.
			populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, new SearchBean<ScQt>(), false);
			// #30451-End.

			if (StringUtils.isNotEmpty(docType)) {
				reqDTO.setDocType(docType);
				reqDTO.setDocNo(null);
			}
			return qtDetailSO.findDocumentNoByDocType(reqDTO);
		} finally {
			qtDetailSO = null;
		}
	}

	/**
	 * Method to prepare scqt object with all dependent object.
	 * 
	 * @return QTDetailReq object
	 */
	public QuationTenderReqDTO prepareQTInfo() {
		debugLogger("prepareQTInfo");
		log.debug("Menu Index : " + this.getManagedBean().getActiveIndex());
		ScQt scQt = this.getManagedBean().getQt();
		// QT number need to be generated by code it should be unique
		if (scQt.isBrandNew()) {
			scQt.setQtNo(getManagedBean().getQt().getQtNo());
		}
		if (!scQt.getSpecApprovalReq()) {
			scQt.setMinistrySpecSecrataryId(null);
		}

		scQt.setExistingContractNo(this.getManagedBean().getCtContract()
				.getContractNo());

		// Setting the Committee Appointment details to Qtdetails
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommitteList = null;
		if (CollectionUtils.isNotEmpty(committeTabs)) {
			qtCommitteList = new ArrayList<ScQtCommittee>();
			for (int i = 0; i < committeTabs.size(); i++) {
				TabBean tabBean = committeTabs.get(i);
				if (!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType())) {
					ScQtCommittee qtCommittee = tabBean.getScQtCommittee();
					
					qtCommittee.setScQt(scQt);	// set latest saved scQt object.
					qtCommitteList.add(qtCommittee);
				}
			}
		}
		scQt.setScQtCommittees(qtCommitteList);
		// Code for to update the supplier qualification criteria
		/* List<ScQtCategory> categoryList = new ArrayList<ScQtCategory>(); */
		if (!this.getManagedBean().getQt().isPanel()) {
			if (this.getManagedBean().getQt().getQtType()
					.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {
				List<ScQtCategory> openList = this.getManagedBean()
						.getQtCategoryList();
				if (CollectionUtils.isNotEmpty(openList)) {
					/*
					 * for (ScQtCategory scQtCategory : openList) { if (null !=
					 * scQtCategory.getPmCategoryL3()) {
					 * scQtCategory.setPmCategoryL1(scQtCategory
					 * .getPmCategoryL3().getPmCategoryL2() .getPmCategoryL1());
					 * scQtCategory.setPmCategoryL2(scQtCategory
					 * .getPmCategoryL3().getPmCategoryL2()); //
					 * scQtCategory.setScQt(this.getManagedBean().getQt());
					 * setUserDetails(scQtCategory);
					 * categoryList.add(scQtCategory); } }
					 */
					scQt.setScQtCategorys(openList);
				}
			} else if (this.getManagedBean().getQt().getQtType()
					.equals(ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER)) {
				List<ScQtCategory> restrictedList = this.getManagedBean()
						.getQtCategoryList();
				if (CollectionUtils.isNotEmpty(restrictedList)) {

					scQt.setScQtCategorys(restrictedList);
				}
				// Setting the Restricted Supplier List to Qtdetails
				List<ScQtSupplier> qtSupplierList = this.getManagedBean()
						.getQtSupplierList();
				if (qtSupplierList != null && !qtSupplierList.isEmpty()) {
					for (ScQtSupplier scQtSupplier : qtSupplierList) {
						scQtSupplier
								.setInvitationType(SourcingConstants.INVITATION_TYPE_INSERT);
						setUserDetails(scQtSupplier);
						// qtSupplierList.add(scQtSupplier);
					}
					scQt.setScQtSuppliers(qtSupplierList);
				}

			}
		}
		
		if (scQt.getScQtSupplierCriteria() != null
				&& !scQt.getScQtSupplierCriteria().isAllState()) {
			List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) convertTreeNodesToLocationCriteria(
					this.getManagedBean().getZoneTrees(),scQt.getScQtSupplierCriteria());
					
			/*List<ScQtLocCriteria> locCriteria = (List<ScQtLocCriteria>) getQuoteZones(
					this.getManagedBean().getZoneTrees(), scQt.getScQtSupplierCriteria());*/
			if (null != locCriteria && !locCriteria.isEmpty()) {
				scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
			} 
			//20191127@asyrafhaziq - Bug #26289 - Start
			else
			{				
			Long currentStatus = scQt.getQtStatus().getStatusId();
			if (isQTStatusGreaterThanOrEqualsScheduleDoc(currentStatus)) {
				if ((null == this.getManagedBean().getZoneTrees() || this.getManagedBean().getZoneTrees().isEmpty()) && scQt.getScQtSupplierCriteria().getScQtLocCriteria() != null) {
					scQt.getScQtSupplierCriteria().setScQtLocCriteria(null);
				}
			}
			}
			//20191127@asyrafhaziq - Bug #26289 - End
		}
		if (scQt.isZonal()) {
			if (null != scQt.getScQtSupplierCriteria() && scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
				ScQtSupplierCriteria supplierCriteria = scQt.getScQtSupplierCriteria();
				ScQtZoneCriteria qtZoneCriteria = supplierCriteria.getQtZoneCriterias();
				
				if (qtZoneCriteria.isBrandNew()) {
					qtZoneCriteria.setScQtSupplierCriteria(supplierCriteria);
					setUserDetails(qtZoneCriteria);	
				}
				
				List<TreeNode> zoneTrees = this.getManagedBean().getZoneSuppQualTrees();
				if (zoneTrees == null) {
					zoneTrees = this.getManagedBean().getZoneGeneralTrees();
				}
				
				List<TplSpecZoneDtl> specZoneDtls = convertTreeNodesToZoneCriteria(
						zoneTrees, null);

				if (specZoneDtls != null && !specZoneDtls.isEmpty()) {					
					for (TplSpecZoneDtl tplSpecZoneDtl : specZoneDtls) {
						tplSpecZoneDtl.setTplSpecZone(qtZoneCriteria);
					}
					qtZoneCriteria.setTplSpecZoneDtls(specZoneDtls);
				} else {
					qtZoneCriteria.setTplSpecZoneDtls(null);
				}
			}
		}
		
		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();

		if (CollectionUtils.isNotEmpty(tabList)) {
			List<ScQtApproval> approvalList = null;
			approvalList = new ArrayList<ScQtApproval>();
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}

				if (this.getManagedBean().getQt().isBrandNew()) {
					tabBean.getScQtApproval().setScQt(
							this.getManagedBean().getQt());
				}
				// setUserDetails(tabBean.getScQtApproval());

				approvalList.add(tabBean.getScQtApproval());

			}
			scQt.setScQtApprovals(approvalList);
		}

		scQt.setScQtBriefings(null);
		if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
			List<ScQtBsv> scQtBsvs = constructBriefingSiteVisitList(scQt);
			scQt.setScQtBriefings(scQtBsvs);

			// Setting Sample Submission to qtDetails.
			List<ScQtSampleSubmission> sampleSubmissionList = null;
			if (scQt.isZonal()) {
				sampleSubmissionList = getManagedBean()
						.getScQtSampleSubmissionList();
				if (sampleSubmissionList != null
						&& !sampleSubmissionList.isEmpty()) {
					for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
						ScAddress scAddress = scQtSampleSubmission.getScAddress();
						
						// 11104-start
						BriefingZoneBean briefingsamplebean = null;

						if (CollectionUtils.isNotEmpty(this.getManagedBean().getSampleAddressBean())) {
							for (BriefingZoneBean briefBean : this.getManagedBean().getSampleAddressBean()) {
								if (briefBean.getTplSpecZoneDtl().getSpecZoneId()
										.equals(scQtSampleSubmission.getTplSpecZoneDtl().getSpecZoneId())
										|| scQtSampleSubmission.getTplSpecZoneDtl().getSpecZoneId() == scQtSampleSubmission
												.getTplSpecZoneDtl().getSpecZoneId()) {
									briefingsamplebean = briefBean;
								}
							}
						}
						// 11104-End

						if (null != scAddress) {
							if (scAddress.getPmDivision() != null
									&& scAddress.getPmDivision()
											.getDivisionId() == null) {
								scAddress.setPmDivision(null);
								
								// 11104-start
								if (null != briefingsamplebean
										&& null != briefingsamplebean
												.getDivisionId()) {
									scAddress
											.setPmDivision(retrievePmDivsById(briefingsamplebean
													.getDivisionId()));
								}
								// 11104-End
							}
							
							// 11104-start
							if (null != scAddress.getPmState()
									&& scAddress.getPmState().getStateId() == null) {
								if (null != briefingsamplebean
										&& null != briefingsamplebean
												.getStateId()) {
									scAddress
											.setPmState(retrievePmStateById(briefingsamplebean
													.getStateId()));
								}
							}

							if (null != scAddress.getPmDistrict()
									&& scAddress.getPmDistrict()
											.getDistrictId() == null) {
								if (null != briefingsamplebean
										&& null != briefingsamplebean
												.getDistrictid()) {
									scAddress
											.setPmDistrict(retrievePmDistById(briefingsamplebean
													.getDistrictid()));
								}
							}

							if (null != scAddress.getPmCity()
									&& scAddress.getPmCity().getCityId() == null) {
								if (null != briefingsamplebean
										&& null != briefingsamplebean
												.getCityId()) {
									scAddress
											.setPmCity(retrievePmCityById(briefingsamplebean
													.getCityId()));
								}
							}
							// 11104-End
							
							scAddress.setPmCountry(retrieveCountry());
						}
						scQtSampleSubmission.setScQt(getManagedBean().getQt());
					}
				}
			} else {
				sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
				if (null != this.getManagedBean().getScQtSampleSubmission()) {
					ScAddress scAddress = this.getManagedBean()
							.getScQtSampleSubmission().getScAddress();

					if (scAddress.getPmDivision() != null
							&& scAddress.getPmDivision().getDivisionId() == null) {
						scAddress.setPmDivision(null);
					}
					scAddress.setPmCountry(retrieveCountry());
					sampleSubmissionList.add(this.getManagedBean()
							.getScQtSampleSubmission());
				}
			}
			scQt.setScQtSampleSubmission(sampleSubmissionList);

			// Setting the contact person to qtdetails.
			List<ScQtContactPerson> contactPersonsList = this.getManagedBean()
					.getScQtContactPersons();
			// 25037 - start
			List<ScQtContactPerson> deskOfficerAdminList = this.getManagedBean().getPmuserContactpersonList();
			List<ScQtContactPerson> contactList = new ArrayList<ScQtContactPerson>();

			// 25037 - 497 -start-
			if (this.getManagedBean().isMaintanceFlow() && StringUtils.isEmpty(this.getManagedBean().getTaskName())) {
				// 25037 - 497 -end-
				if (CollectionUtils.isNotEmpty(scQt.getRequestContents())
						&& this.getManagedBean().getRequestContent() != null) {
					ScQtRequestContent requestContent = this.getManagedBean().getRequestContent();
					deskOfficerAdminList = deskOfficerAdminList == null ? new ArrayList<ScQtContactPerson>()
							: deskOfficerAdminList;

					requestContent.setModified(isDOAListModified());
					// #25037, #31112 -start-
					// remove request content container when no modification
					requestContent.setDeleted(requestContent.isBrandNew() && !requestContent.isModified());
					// #25037, #31112 -end-

					if (requestContent.isModified()) {
						requestContent.setRequireApproval(true);

						Map<Long, String> map = new HashMap<Long, String>();

						for (ScQtContactPerson p : deskOfficerAdminList) {
							if (p.getUser().getUserId() == null) {
								throw new EpException(getMessageCommon("lbl.sc.required.select.doa"));
							}

							map.put(p.getUser().getUserId(), p.getUser().getLoginId());
						}
						requestContent.setRequestContent(map);
					}
				}
			} else if (CollectionUtils.isNotEmpty(deskOfficerAdminList)) {
				contactList.addAll(deskOfficerAdminList);
			}
			// 25037 - end

			if (contactPersonsList != null && !contactPersonsList.isEmpty()) {
				contactList.addAll(contactPersonsList);
			}
			if (null != contactPersonsList) {
				scQt.setScQtContactPersons(contactList);
			}

			// #25077 - Start
			if (!(getUserTaskIsPublicationApproval() && isApprovalScreen())) {
			// #25077 - End
				ScQtApproval pubApproval = this.getManagedBean().getPublicationApproval();
				
				if (null != pubApproval) {
					List<ScQtApproval> approvals = scQt.getScQtApprovals();
					
					removePTJApproval(approvals);
					
					if (null != this.getManagedBean().getPublicationApprovals()) {
						List<ScQtApproverSettings> approvalSettings = this.getManagedBean().getPublicationApprovals().getTarget();
						
						if (CollectionUtils.isNotEmpty(approvalSettings)) {
							for (ScQtApproverSettings approverSettings : approvalSettings) {
								approverSettings.setScQtApproval(pubApproval);
							}
							
							pubApproval.setApproverSettings(approvalSettings);
						}
					}

					approvals.add(pubApproval);
					
					scQt.setScQtApprovals(approvals);
				}
			// #25077 - Start
			}
			// #25077 - End

		}
		scQt.setScQtStatus(null);
		
		// Setting the Deductions to qtdetails.
		List<ScQtDeduction> deductionList = this.getManagedBean().getScQtDeductionList();
		if (null != deductionList && !deductionList.isEmpty()) {
			for (ScQtDeduction deduction : deductionList) {
				deduction.setScQt(this.getManagedBean().getQt());
			}
			scQt.setScQtDeductions(deductionList);
		 }
		
		// Setting the Documents to qtdetails.
		List<ScQtDocReport> docReportsList = getManagedBean()
				.getScQtDocReportList();
		if (docReportsList != null && !docReportsList.isEmpty()) {
			scQt.setScQtDocReports(docReportsList);
		}
		
		// Set back procurement mode if null
		//20190910@asyrafhaziq - 26404 -Start
				if (null == scQt.getProcurementMode()) {	
					BigDecimal amount = scQt.getEstimatedAmount() !=null ? scQt.getEstimatedAmount() : scQt.getTmpExpectedIntendedPrice();
					if(null != amount){
						new CreateQtBackingBean().setProcurementModeOnly(scQt, amount);
					}
				}
		//20190910@asyrafhaziq - 26404 -End
		// save.
		
		//9019 for prevent null insert for remark and merge note-522
		QuationTenderResDTO resDto = null;
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		
		if(null != scQt.getScQtRemarks() && CollectionUtils.isNotEmpty(scQt.getScQtRemarks())) {
			for (int i = 0; i < scQt.getScQtRemarks().size(); i++) {
				String userRemark = scQt.getScQtRemarks().get(i).getRemark();
		
				if (null == userRemark || "".equals(userRemark)) {
					if (null != scQt.getScQtRemarks().get(i).getRemarkId()) {
						this.getManagedBean().setQtRemarksIdRemove(scQt.getScQtRemarks().get(i).getRemarkId());
					} else {
						resDto = getQTDetailSO().retriveQtRemarks(reqDTO);
						if (CollectionUtils.isNotEmpty(resDto.getRemarks())) {
							for (ScRemark remark : resDto.getRemarks()) {
								this.getManagedBean().setQtRemarksIdRemove(remark.getRemarkId());
							}
						}
					}
					scQt.getScQtRemarks().remove(i);
				} else {
					resDto = getQTDetailSO().retriveQtRemarks(reqDTO);
					if (CollectionUtils.isNotEmpty(resDto.getRemarks())) {
						for (ScRemark remark : resDto.getRemarks()) {
							this.getManagedBean().setQtRemarksIdRemove(remark.getRemarkId());
						}
					}
				}
			}
		}	
		// 9019 end

		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		quationTenderReqDTO.setQtdetail(scQt);
		quationTenderReqDTO.setQtCommittes(scQt.getScQtCommittees());
		quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);

		quationTenderReqDTO.setToBeRemoveList(this.getManagedBean()
				.getToBeApprovalRemoveList());

		List<ScQtDeduction> deductionRemoveList = this.getManagedBean()
				.getRemoveDeductions();
		if (null != deductionRemoveList && !deductionRemoveList.isEmpty()) {
			for (int i = 0; i < deductionRemoveList.size(); i++) {
				if (null == deductionRemoveList.get(i).getQtDeductionId()) {
					deductionRemoveList.remove(i);
				}
			}
		}
		quationTenderReqDTO.setRemoveDeductions(deductionRemoveList);
		quationTenderReqDTO.setRemoveDocReports(getToBeRemoveList());

		List<ScQtLocCriteria> qtLocCriteriaRemoveList = this.getManagedBean()
				.getSupplierLocationList();
		if (null != qtLocCriteriaRemoveList
				&& !qtLocCriteriaRemoveList.isEmpty()) {
			for (ScQtLocCriteria criteria : qtLocCriteriaRemoveList) {
				criteria.setDeleted(true);
			}
		}
		quationTenderReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());
		quationTenderReqDTO
				.setTobeRemoveLocCriteriaList(qtLocCriteriaRemoveList);

		quationTenderReqDTO.setAmendMentBean(this.getManagedBean()
				.getAmendMentBean());

		quationTenderReqDTO.setToBeRemovedCommitteMembers(this.getManagedBean()
				.getToBeDelCommitteMemebers());
		//20210325@asyrafhaziq_30518 - Start
	    List <ScQtCommittee> scQtCommittees = getQtCommiteDtlsByQtId(scQt.getQtId());
	    List<ScQtCommittee> toBeRemovedScQtCommittees = new ArrayList<ScQtCommittee>() ;
		if ( scQtCommittees != null){
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			for ( ScQtCommittee scQtCommittee : scQtCommittees){
				
					String CommitteeType = scQtCommittee.getScCommittee().getCommitteeType().getParameterCode();
	
					if ( ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(CommitteeType) &&  ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(evaluationType)  ){
						toBeRemovedScQtCommittees.add(scQtCommittee);
						break;
					}
					else if ( (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(CommitteeType)|| 
							ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE .equals(CommitteeType)||
							ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE .equals(CommitteeType)) && 
							ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType)){
						toBeRemovedScQtCommittees.add(scQtCommittee);
					}	
			 }
		 }
		if (CollectionUtils.isNotEmpty(toBeRemovedScQtCommittees) ){
		quationTenderReqDTO.setToBeRemovedScQtCommittees(toBeRemovedScQtCommittees);
		}
		//20210325@asyrafhaziq_30518 - End
		List<ScQtCategory> removeCategoryCodes = this.getManagedBean().getTobeRemoveCategoryCodes();
		if (null != removeCategoryCodes && !removeCategoryCodes.isEmpty()) {
			for (int i = 0; i < removeCategoryCodes.size(); i++) {
				if (null == removeCategoryCodes.get(i).getQtCategoryId()) {
					removeCategoryCodes.remove(removeCategoryCodes.get(i));
				}
			}
		}
		List<ScQtContactPerson> removeContactPerson = this.getManagedBean().getToBeRemovalContactPersonList();
		if (CollectionUtils.isNotEmpty(removeContactPerson)) {
			for (int i = 0; i < removeContactPerson.size(); i++) {
				ScQtContactPerson contactPerson=removeContactPerson.get(i);
				if (null == contactPerson.getQtContactPersonId()) {
					removeContactPerson.remove(contactPerson);
				}
			}
		}
		quationTenderReqDTO.setTobeRemoveCategoryCodes(removeCategoryCodes);
		quationTenderReqDTO.setToBeRemovalContactPersonList(removeContactPerson);
		quationTenderReqDTO.setMaintanceFlow(this.getManagedBean().isMaintanceFlow());
		quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		quationTenderReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		// #9019 (Harun) -start-
		quationTenderReqDTO.setQtRemarksIdRemove(this.getManagedBean().getQtRemarksIdRemove());
		// #9019 (Harun) -end-

		return quationTenderReqDTO;
	}

	/**
	 * @param approvals
	 */
	private void removePTJApproval(List<ScQtApproval> approvals) {
		if (CollectionUtils.isNotEmpty(approvals)) {
			for (int i = 0; i < approvals.size(); i++) {
				if (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approvals.get(i)
								.getApprovalCategory().getParameterCode())) {
					approvals.remove(i);
					break;
				}
			}
		}
	}
	
	/**
	 * Prepare BSV details and populate into ScQt instance
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 */
	public List<ScQtBsv> constructBriefingSiteVisitList(ScQt scQt) {
		// Setting the qtBreifing details to Qtdetails
		List<ScQtBsv> bsvs = null;
		if (scQt.isBsvRequired()) {
			List<BriefingZoneBean> beansList = this.getManagedBean()
					.getBriefingZoneBean();
			if (beansList != null && !beansList.isEmpty()) {
				bsvs = new ArrayList<ScQtBsv>();
				for (BriefingZoneBean bsvDtlBean : beansList) {
					ScQtBsv scQtBsv = constructBsvByBean(bsvDtlBean);
					scQtBsv.setBsvRemarks(finalizeRemarks(scQtBsv
							.getBsvRemarks()));
					// scQtBsv.setScQt(this.getManagedBean().getQt());
					scQtBsv.setTplSpecZoneDtl(bsvDtlBean.getTplSpecZoneDtl());
					if (null != scQtBsv) {
						bsvs.add(scQtBsv);
					}
				}

				for (int i = 0; i < bsvs.size(); i++) {
					List<ScQtBsvDtl> bsvDtls = bsvs.get(i).getScQtBsvDtls();
					if (bsvDtls == null || !(bsvDtls.size() > 0)) {
						bsvs.remove(i--);
					}
				}

			} else {
				// code for if bsv details if any and non zonal
				BriefingZoneBean bsvDtlBean = this.getManagedBean()
						.getBsvDtlBean();
				ScQtBsv scQtBsv = constructBsvByBean(bsvDtlBean);

				if (null != scQtBsv) {
					scQtBsv.setBsvRemarks(finalizeRemarks(scQtBsv
							.getBsvRemarks()));
					// scQtBsv.setScQt(this.getManagedBean().getQt());
					bsvs = new ArrayList<ScQtBsv>();
					bsvs.add(scQtBsv);
				}

			}	
		}
		
		return bsvs;
	}

	/**
	 * Gets the to be remove list.
	 * 
	 * @return the to be remove list
	 */
	private List<ScQtDocReport> getToBeRemoveList() {
		List<ScQtDocReport> toBeRemoveList = new ArrayList<ScQtDocReport>();

		List<ScQtDocReport> techList = getManagedBean()
				.getMarkDeletedScQtDocReportList();
		if (techList != null && techList.size() > 0) {
			toBeRemoveList.addAll(techList);
		}
		return toBeRemoveList;
	}

	/**
	 * Retrieve country.
	 * 
	 * @return the pm country
	 */
	private PmCountry retrieveCountry() {
		ICountrySO countrySo = null;
		PmCountry country = null;
		try {
			countrySo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
			CountryReqDTO countryReqDTo = new CountryReqDTO();
			countryReqDTo.setCountryCode(CommonConstants.DEFAULT_COUNTRY_CODE);
			CountryResDTO countryResDTO = countrySo
					.retrieveCountryByCode(countryReqDTo);
			country = countryResDTO.getPmCountry();
		} finally {
			countrySo = null;
		}
		return country;
	}
	 
	// #19336, #25201, #17376, #19287 - start
	private void populateQTLOISearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScLoiForQt> qtSearchBean) {

		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean.getDataFilterBeans())) {
				qtSearchBean.getDataFilterBeans().clear();
			}
			
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String docType = qtnTenderSearchBean.getDocType();
			String docNo = qtnTenderSearchBean.getDocNo();
			
			if (StringUtils.isNotEmpty(docType)) {
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)
				// ***19336#99 - start
						&& checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_INTENT)
				// ***19336#99 - end
				) {
					if (!StringUtils.isBlank(docNo)) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField("loiNo");
						bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);
					}
				} else {
					reqDTO.setDocType(docType);
					reqDTO.setDocNo(docNo);
				}
			}

			// replaced by subquery at DAO
			/*
			 * if(!qtnTenderSearchBean.isQtStatusForQt() &&
			 * StringUtils.isNotBlank(qtnTenderSearchBean.getQtStatus())){
			 * searchFilterBean.add( new
			 * SearchFilterBean("scLoiWorkFlowStatuses.pmStatus.statusId",
			 * SearchOperator.OPERATOR_EQUALS.value(),
			 * qtnTenderSearchBean.getQtStatus())); searchFilterBean.add( new
			 * SearchFilterBean("scLoiWorkFlowStatuses.current",
			 * SearchOperator.OPERATOR_EQUALS.value(), Boolean.TRUE)); }
			 */

			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);

			reqDTO.setLoiForQtSearchBean(qtSearchBean);
		}
	}

	private void populateLoaForQtSearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScLoaForQt> qtSearchBean) {

		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean.getDataFilterBeans())) {
				qtSearchBean.getDataFilterBeans().clear();
			}
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();

			String docType = qtnTenderSearchBean.getDocType();
			String docNo = qtnTenderSearchBean.getDocNo();
			if (StringUtils.isNotEmpty(docType)) {
				if (ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)) {
					if (!StringUtils.isBlank(docNo)
					// ***19336#99 - start
							&& checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_AWARD)
					// ***19336#99 - end
					) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField("loaNo");
						bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);
					}
				} else {
					reqDTO.setDocType(docType);
					reqDTO.setDocNo(docNo);
				}
			}

			//replaced by subquery in DAO
			/*if(!qtnTenderSearchBean.isQtStatusForQt() && StringUtils.isNotBlank(qtnTenderSearchBean.getQtStatus())){
				searchFilterBean.add(
						new SearchFilterBean("scLoaWorkFlowStatuses.pmStatus.statusId", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								qtnTenderSearchBean.getQtStatus()));
				searchFilterBean.add(
						new SearchFilterBean("scLoaWorkFlowStatuses.current", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								Boolean.TRUE));
			}*/

			// 30418, #25201-START
			// Special Case for LOA Doctype QT Maintenance Search, procurement mode is search against SC_LOA instead of SC_QT
			if (StringUtils.isNotEmpty(qtnTenderSearchBean.getProcurementMode())) {
				searchFilterBean.add(new SearchFilterBean(
						"procurementMode.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						qtnTenderSearchBean.getProcurementMode()));
			}
			// 30418, #25201-END

			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);

			reqDTO.setLoaForQtSearchBean(qtSearchBean);
		}
	}

	// #19336, #25201, #17376, #19287 - end

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param qtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 * @param isBidding 
	 */
	// #30451, #17376-Start.
	private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO, QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean, boolean isBidding) {
		// #30451, #17376-End.
		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean
					.getDataFilterBeans())) {
				qtSearchBean.getDataFilterBeans().clear();
			}
			List<SearchFilterBean> searchFilterBean = new ArrayList<SearchFilterBean>();
			String qtTitle = qtnTenderSearchBean.getQtTitle();
			if (!StringUtils.isBlank(qtTitle)) {
				searchFilterBean.add(
						new SearchFilterBean(QTConstants.QT_TITILE,
								SearchOperator.OPERATOR_LIKE.value(), qtTitle,
								true));
			}
			
			// docType & docNo
			String docType = qtnTenderSearchBean.getDocType();
			String docNo = qtnTenderSearchBean.getDocNo();
			if (StringUtils.isNotEmpty(docType)) {
				reqDTO.setDocType(docType); // 19366#40
				
				// #19336, #25201, #17376, #19287 -start-
				if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) 
						|| ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)
						|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)
						|| ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(docType)) {
				// #19336, #25201, #17376, #19287 -end-
					if (!StringUtils.isBlank(docNo)) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField(QTConstants.QT_NUMBER);
						bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);	
					}
				} else {
					reqDTO.setDocNo(docNo);
				}
			}
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			
			// #13749, #19336, #25201, #17376, #19287 -start-
			if (!StringUtils.isBlank(procurementMode) && !ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)) {
			// #13749, #19336, #25201, #17376, #19287 -end-
				searchFilterBean.add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}
			// #13749, #19336, #25201, #17376, #19287 -start-
			else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType) 
					&& (ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(procurementMode)
						|| ParameterConst.PROCUREMENT_MODE_TENDER.equals(procurementMode)) 
							&& !(isQTMaintenanceScreen() || isQTSearchScreen())){
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(procurementMode);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			} 
			// #13749, #19336, #25201, #17376, #19287 -end-
			
			Date fromCreatedDate = qtnTenderSearchBean.getFromCreatedDate();
			Date toCreatedDate = qtnTenderSearchBean.getToCreatedDate();
			if (null != fromCreatedDate || toCreatedDate != null) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromCreatedDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromCreatedDate = fromDate.getTime();
				if (toCreatedDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toCreatedDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toCreatedDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CREATED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromCreatedDate, toCreatedDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromPublishDate = qtnTenderSearchBean.getFromPublishDate();
			Date toPublishDate = qtnTenderSearchBean.getToPublishDate();
			if (null != fromPublishDate || null != toPublishDate) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromPublishDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromPublishDate = fromDate.getTime();
				if (toPublishDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toPublishDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toPublishDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.PUBLISH_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromPublishDate, toPublishDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			Date fromClosingDate = qtnTenderSearchBean.getFromClosingDate();
			Date toClosingDate = qtnTenderSearchBean.getToClosingDate();
			if (null != fromClosingDate || null != toClosingDate) {
				Calendar c1 = Calendar.getInstance();
				c1.setTime(fromClosingDate);
				Calendar fromDate = Calendar.getInstance();
				fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
						c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				fromDate.set(Calendar.MILLISECOND, 0);
				fromClosingDate = fromDate.getTime();
				if (toClosingDate != null) {
					Calendar c2 = Calendar.getInstance();
					c2.setTime(toClosingDate);
					Calendar toDate = Calendar.getInstance();
					toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
							c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
					toDate.set(Calendar.MILLISECOND, 0);
					toClosingDate = toDate.getTime();
				}
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.CLOSED_DATE);
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						fromClosingDate, toClosingDate);
				bean.setSearchValue(betweenVal);
				searchFilterBean.add(bean);
			}
			
			String status = qtnTenderSearchBean.getQtStatus();
			if (!StringUtils.isBlank(status)){
				// #30451, #17376-Start.
				if (qtnTenderSearchBean.isQtStatusForQt() || isBidding) {
					// #30451, #17376-End.
					SearchMultiFilterBean statusBean = null;
					SearchMultiFilterBean statusFilterBean = new SearchMultiFilterBean(
							new SearchFilterBean(
									"scQtStatus.pmStatus.statusId",
									SearchOperator.OPERATOR_EQUALS.value(),
									status,
									JoinType.LEFT), 
							new SearchFilterBean(
									"scQtStatus.current",
									SearchOperator.OPERATOR_EQUALS.value(),
									true,
									JoinType.LEFT));
					statusBean = statusFilterBean;

					if (status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION.toString())) {
						// #9019 #34702 - Start
						statusBean = new SearchMultiFilterBean(statusFilterBean, new SearchFilterBean(
								"scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_EQUALS.value(),
								QTStatusConst.QT_PENDING_QT_SUBMISSION_WIP.toString(),
								SearchCondition.CONDITION_OR.value(), JoinType.LEFT));
						// #9019 #34702 - End
					}

					qtSearchBean.getDataFilterBeans().add(statusBean);
				}
				// 19366#40 -start-
				else {
					try {
						reqDTO.setStatusId(Long.parseLong(status));
					} catch (ClassCastException ex) {

					}
				}
				// 19366#40 -end-
				// #30451, #17376-Start.
			} else if (isBidding) {
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(QTStatusConst.SF_PENDING_SCHEDULE_BIDDING);
				statusIds.add(QTStatusConst.SF_PENDING_BIDDING);
				statusIds.add(QTStatusConst.SF_PENDING_BIDDING_ESCALATION);
				
				SearchMultiFilterBean statusBean = new SearchMultiFilterBean(new SearchFilterBean(
						"scQtStatus.pmStatus.statusId", SearchOperator.OPERATOR_IN.value(), statusIds, JoinType.LEFT),
						new SearchFilterBean("scQtStatus.current", SearchOperator.OPERATOR_EQUALS.value(), true,
								JoinType.LEFT));
				
				qtSearchBean.getDataFilterBeans().add(statusBean);
				// #30451, #17376-End.
			}			
			
			if (isMofSupplier()) {
				// Filter By - supplierId
				ISupplierSearchSO supplierSearchSO = null;
				try {
					supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);

					SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
					supplierSearchReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
					List<SmSupplier> smSuppliers = supplierSearchSO
							.retriveSupplierProfiles(supplierSearchReqDTO)
							.getResultBean().getResultList();
					
					if (CollectionUtils.isNotEmpty(smSuppliers)) {
						List<Long> supplierIds = new ArrayList<Long>();
						for (SmSupplier smSupplier : smSuppliers) {
							supplierIds.add(smSupplier.getSupplierId());
						}
						
						searchFilterBean.add(new SearchFilterBean(
								"scQtSuppliers.smSupplier.supplierId",
								SearchOperator.OPERATOR_IN.value(), 
								supplierIds));
						reqDTO.setSupplierIds(supplierIds);
					}

				} finally {
					supplierSearchSO = null;
				}
			} else {
				if (isDeskOfficer()) {
					// Filter By - actor user id
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
					
					// Only DO Actor with edit access right. 
					// Otherwise (Audit Search), user can view QT based on user roles match with actor.
					if ((isQTMaintenanceScreen() || isQTSearchScreen()) && !isPbbSecretary()) { //#27668
						reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
					}
				}
				//Bug_33484 - start
				else if (isQcaSecretary() || isQcbSecretary() || isPbbSecretary() 
						|| isFpbSecretary() || isApprover()){
					// Filter By - actor user id
					reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				}
				//Bug_33484 - end
				
				 /* Enhancement #27668 -start-
				 * TO INCLUDE QT WHICH PPB SECRETARY INVOLVE IN IT, IF HIGHER_AUTHORITY LINK ALSO
				 * AND WITH PTJ HIMSELF */
				SearchFilterBean secretaryFilterBean = new SearchFilterBean(
						QTConstants.FINALIZATION_SECRETARY_LOGIN_ID, SearchOperator.OPERATOR_EQUALS.value(),
						new FacesScopes().getUserSessionBean().getLoginId(), JoinType.LEFT);
				
				SearchMultiFilterBean secretartStatusBean = null;
				secretartStatusBean = new SearchMultiFilterBean(secretaryFilterBean, new SearchFilterBean(
						QTConstants.FINALIZATION_SECRETARY_LOGIN_ID, SearchOperator.OPERATOR_IS_NULL.value(), null,
						SearchCondition.CONDITION_OR.value()));

				SearchFilterBean highAuthFilterBean = new SearchFilterBean(QTConstants.FINALIZATION_HIGH_AUTH_SECRETARY_LOGIN_ID,
						SearchOperator.OPERATOR_EQUALS.value(), new FacesScopes().getUserSessionBean().getLoginId(),JoinType.LEFT);
				
				SearchMultiFilterBean highAuthStatusBean = null;
				highAuthStatusBean = new SearchMultiFilterBean(highAuthFilterBean, new SearchFilterBean(
						QTConstants.FINALIZATION_HIGH_AUTH_SECRETARY_LOGIN_ID, SearchOperator.OPERATOR_IS_NULL.value(),
						null, SearchCondition.CONDITION_OR.value()));			
				// #27668 -end-

				PmOrgProfile ptjProfiles = qtnTenderSearchBean.getPtjProfile();
				SearchFilterBean searchFilterBean1 = null; // #27668

				if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
					// Filter By - PTJ
					// #27668 -start-
					searchFilterBean1 = new SearchFilterBean(
							"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DIVISION.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjProfiles.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
					// #27668 -end-
				} else {
					// Filter By - PTJ Group
					PmOrgProfile ptjGroupProfile = qtnTenderSearchBean.getPtjGroupProfile();
					if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
						// #27668 -start-
						searchFilterBean1 = new SearchFilterBean(
								"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ptjGroupProfile.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
						// #27668 -end-
					} else {
						// Filter By - Ministry
						PmOrgProfile ministryProfile = qtnTenderSearchBean.getMinistryProfile();
						if (ministryProfile != null
								&& ministryProfile.getOrgProfileId() != null) {	
							// #27668 -start-
							searchFilterBean1 = new SearchFilterBean(
									"pmOrgProfile." + OrgProfileUtil
										.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(),
									ministryProfile.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
							// #27668 -end-
						}
					}
				}				

				// #27668 -start-
				if (searchFilterBean1 != null) {
					SearchMultiFilterBean searchFilterBeanOr1 = new SearchMultiFilterBean(secretartStatusBean,
							SearchCondition.CONDITION_OR.value(), highAuthStatusBean);
					SearchMultiFilterBean searchFilterBeanAnd2 = new SearchMultiFilterBean(searchFilterBeanOr1,
							searchFilterBean1);
					
					qtSearchBean.getDataFilterBeans().add(searchFilterBeanAnd2);
					// #27668 -end-
					
				}
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			reqDTO.setQtSearchBean(qtSearchBean);
		}
	}
	
	// #25635 -start-
	public boolean isQTMaintenanceScreen() {
		// #25635 -end-
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	// ***17376#42 -start-
	private boolean isQTSearchScreen() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// ***17376#42 -end-
	
	// ***17376#55 -start-
	private boolean isQtTechSupportSearchScreen(){
		return QTConstants.PORTLET_NAME_QT_TECHSUPP.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	// ***17376#55 -end-

	/**
	 * Retrive financial specification.
	 */
	public void retriveFinancialSpecification() {
		debugLogger("retriveFinancialSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_FINANCIAL);
			reqDTO.setPrepareSpec(true);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getFinSpecResultBean().getResultList() != null
					&& !resDto.getFinSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setFinancialSpecList(
						resDto.getFinSpecResultBean().getResultList());
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * Retrive technical specification.
	 */
	public void retriveTechnicalSpecification() {
		debugLogger("retriveTechnicalSpecification");
		IQTSpecSO specSO;
		SpecificationResDTO resDto;
		try {
			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			reqDTO.setSpecType(ParameterConst.SPECIFICATION_TYPE_TECHNICAL);
			reqDTO.setPrepareSpec(true);

			specSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			resDto = specSO.retrieveSpecDetails(reqDTO);
			if (resDto.getSpecResultBean().getResultList() != null
					&& !resDto.getSpecResultBean().getResultList().isEmpty()) {
				this.getManagedBean().setTechnicalSpecList(
						resDto.getSpecResultBean().getResultList());
			}
		} finally {
			specSO = null;
		}
	}

	/**
	 * Retrieve briefing sitevisit tab dtl.
	 */
	public void retrieveBriefingSitevisitTabDtl() {
		
		ScQt qt = this.getManagedBean().getQt();
		debugLogger("retrieveBriefingSitevisitTabDtl");

		if (this.getManagedBean().getBsvDtlBean() == null
				&& this.getManagedBean().getBriefingZoneBean().isEmpty()) {
			IQTBriefingSO qTBriefingSO = null;
			try {
				qTBriefingSO = (IQTBriefingSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTBriefingSO.class);
				String languageCode = new FacesScopes().getUserLocale()
						.getLanguage();
				QtAmendMentBean amendMentBean = getManagedBean().getAmendMentBean();
				// Only pass amendment bean from db.
				if (amendMentBean != null) {
					amendMentBean = !amendMentBean.isBrandNew() ? amendMentBean : null;	
				}
				BriefingReqDTO reqDTO = new BriefingReqDTO();
				// reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setScQt(qt);
				reqDTO.setLanguageCode(languageCode);
				reqDTO.setMaintanceFlow(true);
				reqDTO.setAmendMentBean(amendMentBean);
				BriefingResDTO resDTO = qTBriefingSO
						.retrieveBriefingSiteVisitDtl(reqDTO);
				// getting Briefing Same As Site Visit value
				ScQt scQt = resDTO.getScQt();
				getManagedBean().setQt(scQt);
				Long currentStatus = this.getManagedBean().getQt()
						.getQtStatus().getStatusId();
				this.getManagedBean().setCurrentStatusId(currentStatus);
				if (scQt.isZonal()) {
					List<TplSpecZoneDtl> zones = resDTO.getZonesList();
					List<BriefingZoneBean> zoneBeans = new ArrayList<BriefingZoneBean>();
					if (null != zones) {
						BriefingReqDTO briefingReqDTO = new BriefingReqDTO();
						briefingReqDTO.setQtId(getManagedBean().getQt()
								.getQtId());
						briefingReqDTO.setScQt(getManagedBean().getQt());
						briefingReqDTO.setMaintanceFlow(true);
						briefingReqDTO.setAmendMentBean(amendMentBean);
						for (TplSpecZoneDtl specZoneDtl : zones) {
							briefingReqDTO.setSpecZoneId(specZoneDtl
									.getSpecZoneId());
							BriefingResDTO briefingResDTO = qTBriefingSO
									.retrieveBriefingSiteVisitDtls(briefingReqDTO);
							ScQtBsv scQtBsv = briefingResDTO.getScQtBsv();
							if (null != scQtBsv.getScQtBsvDtls()) {
								for (ScQtBsvDtl bsvDtl : scQtBsv
										.getScQtBsvDtls()) {
									if (QTConstants.BSV_TYPE_SITEVISIT
											.equalsIgnoreCase(bsvDtl
													.getBsvType())) {
										if (scQtBsv.getQtBsvId() == null) {
											scQtBsv.setAllSiteVisitReq(true);
										}
									}
								}
							}
							scQtBsv.setBsvRemarks(initializeRemarks(scQtBsv,
									this.getManagedBean().getQt()
											.getCurrentWorkFlowStatus(),
									scQtBsv.getBsvRemarks(),
									ScQtBSVRemark.class));
							BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
									specZoneDtl,
									briefingResDTO.getBriefingList(),
									briefingResDTO.getSiteVisitList(), scQtBsv);
							zoneBeans.add(briefingZoneBean);
						}
					}
					this.getManagedBean().setBriefingZoneBean(zoneBeans);

					this.getManagedBean().setBsvDtlBean(zoneBeans.get(0));
				} else {
					ScQtBsv qtBsv = resDTO.getScQtBsv();
					if (null != qtBsv.getScQtBsvDtls()) {
						for (ScQtBsvDtl bsvDtl : qtBsv.getScQtBsvDtls()) {
							if (QTConstants.BSV_TYPE_SITEVISIT
									.equalsIgnoreCase(bsvDtl.getBsvType())) {
								if (qtBsv.getQtBsvId() == null) {
									qtBsv.setAllSiteVisitReq(true);
								}
							}
						}
					}
					qtBsv.setBsvRemarks(initializeRemarks(qtBsv, this
							.getManagedBean().getQt()
							.getCurrentWorkFlowStatus(), qtBsv.getBsvRemarks(),
							ScQtBSVRemark.class));
					BriefingZoneBean briefingZoneBean = new BriefingZoneBean(
							null, resDTO.getBriefingList(),
							resDTO.getSiteVisitList(), qtBsv);
					this.getManagedBean().setBsvDtlBean(briefingZoneBean);
				}
				QTBriefingSiteVisitBackingBean backingBean = new QTBriefingSiteVisitBackingBean();
				backingBean.retrievePmStateList();
			} finally {
				qTBriefingSO = null;
			}
		}
	}

	/**
	 * Process checked.
	 * 
	 * @param e
	 *            the e
	 */
	public void processChecked(AjaxBehaviorEvent e) {
		// Boolean id = (Boolean) e.getComponent().getAttributes().get("value");

	}

	/**
	 * Retrieve committee appointment tab dtl.
	 * 
	 * @param isApprovalReq
	 *            the is approval req
	 */
	// Bug 32277 - start
	public void retrieveCommitteeAppointmentTabDtl(int formCommitteeFlag, int reformCommitteeFlag) {
		retrieveCommitteeAppointmentTabDtl(formCommitteeFlag, reformCommitteeFlag, false);
	}

	public void retrieveCommitteeAppointmentTabDtl(int formCommitteeFlag, int reformCommitteeFlag,
			boolean committeeReadOnly) {
	// Bug 32277 - end
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveCommitteeAppointmentTabDtl");
		
		if (null == this.getManagedBean().getQt().getFinalizationCommitte()) {
			this.getManagedBean().getQt()
					.setFinalizationCommitte(new PmParameter());
		}
		if (null == this.getManagedBean().getQt().getFinalizationSecratery()) {
			this.getManagedBean().getQt()
					.setFinalizationSecratery(new PmUser());
		}
		List<TabBean> committeTabs = committeeReadOnly ? this.getManagedBean().getReadOnlyCommitteeTabBean() : this
				.getManagedBean().getScCommitteeMemberTabList(); // Bug 32277
		//20210325@asyrafhaziq_30518 - Start
		if (committeTabs != null){
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			for ( TabBean committeTab : committeTabs){
				
				if ( committeTab.getScQtCommittee() != null ){
					String CommitteeType = committeTab.getScQtCommittee().getScCommittee().getCommitteeType().getParameterCode();
					if ( ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE.equals(CommitteeType) &&  ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(evaluationType)  ){
						 committeTabs = null;
						 break;
					}
					else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE.equals(CommitteeType) && ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType) ){
						 committeTabs = null;
						 break;
					}	
				}
			}	
		}  
		//20210325@asyrafhaziq_30518 - End

		// #25692 -start-
		 if (getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()) {
			 committeTabs=null;
		 }		
		// #25692 -end-

		Long qtId = scQt.getQtId();
		// 25037 #372 -start-
		String taskName = this.getManagedBean().getTaskName() == null ? "" : this.getManagedBean().getTaskName();
		// 25037 #372 -end-
		
		if (null == committeTabs) {
			String evaluationType = this.getManagedBean().getQt()
					.getEvaluationType();
			String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
			ParameterHelper parameterHelper = ParameterHelper.getInstance();

			committeTabs = new ArrayList<TabBean>();

			String tabType = ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE;
			ScQtCommittee specCommitte = getQtCommiteDtlsByQtIdNType(tabType,
					qtId);
			ScCommittee committee = specCommitte.getScCommittee();
			if ((reformCommitteeFlag & REFORM_SC) == REFORM_SC) {
				committee.setModified(true);
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
			}

			committeTabs.add(new TabBean(parameterHelper.getParameterDesc(
					parameterType, tabType), tabType, specCommitte));

			if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
				boolean reformCommittee;
				// evaluation type is one tier only oc is added..
				if (ParameterConst.EVALUATION_TYPES_ONE_TIER.equals(evaluationType)) {
					tabType = ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE;
					ScQtCommittee evalCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if ((formCommitteeFlag & FORM_OC) == FORM_OC) {
						committee = evalCommitte.getScCommittee();
						committee.setApprovalReq(true);
						reformCommittee = (reformCommitteeFlag & REFORM_OC) == REFORM_OC ||
								(reformCommitteeFlag & REFORM_TEC) == REFORM_TEC ||
								(reformCommitteeFlag & REFORM_FEC) == REFORM_FEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					// 25037-start #372
					if (!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(
							this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE) && !evalCommitte
							.getScCommittee().getCommitteStatus().getStatusId()
							.equals(QTStatusConst.CMT_PENDING_REVISION))) {

						committeTabs.add(new TabBean(parameterHelper.getParameterDesc(parameterType, tabType), tabType,
								evalCommitte));
					}// 25037-end #372
				}
				// evaluation type is two tier below committe are added
				else {
					tabType = ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE;
					ScQtCommittee openingCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if ((formCommitteeFlag & FORM_OC) == FORM_OC) {
						committee = openingCommitte.getScCommittee();
						committee.setApprovalReq(true);
						reformCommittee = (reformCommitteeFlag & REFORM_OC) == REFORM_OC;
								
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					// 25037-start #372
					if (!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(
							this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE) && !openingCommitte
							.getScCommittee().getCommitteStatus().getStatusId()
							.equals(QTStatusConst.CMT_PENDING_REVISION))) {

						committeTabs.add(new TabBean(parameterHelper.getParameterDesc(parameterType, tabType), tabType,
								openingCommitte));
					}// 25037-end #372

					tabType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
					ScQtCommittee tecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if ((formCommitteeFlag & FORM_TEC) == FORM_TEC) {
						committee = tecCommitte.getScCommittee();
						committee.setApprovalReq(true);
						reformCommittee = (reformCommitteeFlag & REFORM_TEC) == REFORM_TEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					// 25037-start #372
					if (!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(
							this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE) && !tecCommitte
							.getScCommittee().getCommitteStatus().getStatusId()
							.equals(QTStatusConst.CMT_PENDING_REVISION))) {

						committeTabs.add(new TabBean(parameterHelper.getParameterDesc(parameterType, tabType), tabType,
								tecCommitte));
					}// 25037-end #372

					tabType = ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE;
					ScQtCommittee fecCommitte = getQtCommiteDtlsByQtIdNType(
							tabType, qtId);
					if ((formCommitteeFlag & FORM_FEC) == FORM_FEC) {
						committee = fecCommitte.getScCommittee();
						committee.setApprovalReq(true);
						reformCommittee = (reformCommitteeFlag & REFORM_FEC) == REFORM_FEC;
						
						if ((committee.getCommitteStatus() != null &&
								committee.getCommitteStatus().getStatusId() != null &&
								!QTStatusConst.CMT_COMPLETED.equals(committee.getCommitteStatus().getStatusId())) ||
								reformCommittee) {
							committee.setModified(true);
							
							if (reformCommittee) {
								committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
							}
						}
					}
					// 25037-start #372
					if (!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(
							this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE) && !fecCommitte
							.getScCommittee().getCommitteStatus().getStatusId()
							.equals(QTStatusConst.CMT_PENDING_REVISION))) {

						committeTabs.add(new TabBean(parameterHelper.getParameterDesc(parameterType, tabType), tabType,
								fecCommitte));
					}// 25037-end #372
				}
				// FC tab
				PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
				tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
				StringBuilder fcTitleLabel = new StringBuilder(
						"lbl.sc.finalization.committee.secretary.");
				String fcTitleName = ResourceBundleUtils
						.getMessage(fcTitleLabel.toString()
									+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
				if (fcSecretaryCode == null
						|| fcSecretaryCode.getParameterId() == null) {
					SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
					if (fcSecretaries != null && fcSecretaries.length > 0) {
						fcSecretaryCode = retrieveParameterById(Long
								.valueOf(fcSecretaries[0].getValue().toString()));
						;
					} else {
						fcSecretaryCode = null;
					}
				}

				// Displaying the Secretary Details
				if (fcSecretaryCode != null) {
					
					String fcSecretary = fcSecretaryCode.getParameterCode();

					if (fcSecretary != null) {
						if (fcSecretary
								.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
						} else {
							fcTitleLabel
									.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
						}
						fcTitleName = ResourceBundleUtils.getMessage(
								fcTitleLabel.toString().toLowerCase(),
								ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale());
					}
				}

				committeTabs.add(new TabBean(fcTitleName, tabType, null));
			}
		} 

		// Bug 32277 - start
		if (committeeReadOnly) {
			this.getManagedBean().setReadOnlyCommitteeTabBean(committeTabs);
			int index = 0;
			this.getManagedBean().setSelectedTabIndex(index);
			this.getManagedBean().setSelectedCommitteeTabIndex(index);
			TabBean committeTabBean = this.getManagedBean().getReadOnlyCommitteeTabBean().get(index);
			this.getManagedBean().setSelectedTabBean(committeTabBean);
			this.getManagedBean().setShowHistory(false);
		} else {
			this.getManagedBean().setScCommitteeMemberTabList(committeTabs);
		}
		// Bug 32277 - end

		for (TabBean tabBean : committeTabs) {
			if ((!SourcingConstants.COMMITTE_TYPE_FINALIZATION.equals(tabBean
					.getTabType()) && tabBean.getScQtCommittee()
					.getScCommittee().getCommitteStatus().getStatusId()
					.equals(QTStatusConst.CMT_COMPLETED))
					|| (SourcingConstants.COMMITTE_TYPE_FINALIZATION
							.equals(tabBean.getTabType())
							&& scQt.getFinalizationCommitte() != null
							&& scQt.getFinalizationCommitte().getParameterId() != null
							&& scQt.getFinalizationSecratery() != null && scQt
							.getFinalizationSecratery().getUserId() != null)) {
				tabBean.setIcon("ui-icon-endorse");
			}
		}
		if (committeTabs.size() > 0) {
			// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
			this.getManagedBean().setSelectedTabBean(
					committeTabs.get(this.getManagedBean()
							.getSelectedCommitteeTabIndex()));
			// 25037-start
			String committeeType = this.getManagedBean().getSelectedTabBean().getTabType();
			this.getManagedBean().setRoleFilterList(
					getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), committeeType));
			// 25037-end
		}

	}

	/**
	 * Retrieve finalization committee types.
	 * 
	 * @return the list
	 */
	public SelectItem[] retrieveFinalizationCommitteeType() {
		
		ScQt scQt = getManagedBean().getQt();
		SelectItem[] finalizationSecratary = getManagedBean().getFinalizationSecratary();
		Long currentStatusId = scQt.getQtStatus().getStatusId();
		
		if (finalizationSecratary == null
				&& isQTStatusLessThanOrEquals(currentStatusId, QTStatusConst.QT_PENDING_LOA)) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

				CommitteReqDTO reqDTO = new CommitteReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				CommitteResDTO resDTO = committeSO
						.retrieveFinalizationCommitteeType(reqDTO);

				List<PmParameter> finalizationCommTypes = resDTO.getFinalizationSecratry();
				if(CollectionUtils.isNotEmpty(finalizationCommTypes)){
					finalizationSecratary = new SelectItem[finalizationCommTypes.size()];
					for(int i=0;i<finalizationCommTypes.size();i++){
						String commTypeDesc = getParameterDesc(
								finalizationCommTypes.get(i),new FacesScopes().getUserLocale());
						finalizationSecratary[i] = new SelectItem(
								finalizationCommTypes.get(i).getParameterId(),commTypeDesc);
					}
					getManagedBean().setFinalizationSecratary(finalizationSecratary);
				}

				
			} finally {
				committeSO = null;
			}
		}

		return finalizationSecratary;
	}

	/**
	 * Retrieve business owner tab dtl.
	 */
	public void retrieveBusinessOwnerTabDtl() {
		debugLogger("retrieveBusinessOwnerTabDtl");
		if (getManagedBean().getQt().getQtId() != null) {
			CommitteReqDTO reqDTO = new CommitteReqDTO();
			reqDTO.setQtId(getManagedBean().getQt().getQtId());
			CommitteResDTO resDTO = null;
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
				resDTO = committeSO
						.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
				List<ScCommitteeMember> committeeMembersList = resDTO
						.getCommMemberResultBean().getResultList();
				getManagedBean().setScCommitteeMemberList(committeeMembersList);
			} finally {
				committeSO = null;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean#
	 * preRenderView()
	 */

	/**
	 * Navigate to quotation tender maintenance page.
	 */
	public void preRenderView() {
		try {
			renewSesionObject();
			debugLogger("preRenderView, @Managedbean = "+ this.getManagedBean());
			
			FacesContext fc = FacesContext.getCurrentInstance();
			Long qtId = null;
			Object payLoad = getBpmTaskDetails(Object.class, null);
			String taskName = this.getManagedBean().getTaskName();
			
			this.getManagedBean().setSubStatusId(null); //#25692
			
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_TEC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_FEC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_FC_SECRETARY
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				
				boolean isCommitteeAppointmentRemark = !SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(taskName);
				Long currentStatusId = this.getManagedBean().getCurrentStatusId();
				SCCommitteeDataType committeDataType = null;// 25037
				
				this.getManagedBean().setActiveIndex(2); //29464

				if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_TEC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_FEC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName)) {

					SCEvaluationDataType evalDataType = (SCEvaluationDataType) payLoad;
					qtId = evalDataType.getQtId().longValue();
					
				} else {
												   
					committeDataType = (SCCommitteeDataType) payLoad;// 25037
					qtId = committeDataType.getQtId().longValue();
					
				}
				
				retrieveQuationTenderDetails(new Long(qtId));
				if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					retrieveCommitteeAppointmentTabDtl(FORM_OC, REFORM_NO_ACTION);
				} else if (SCProcessNameConst.ASSIGN_TEC_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					retrieveCommitteeAppointmentTabDtl(FORM_TEC, REFORM_NO_ACTION);
				} else if (SCProcessNameConst.ASSIGN_FEC_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					retrieveCommitteeAppointmentTabDtl(FORM_FEC, REFORM_NO_ACTION);
				} else if (SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName)) {
					IQTSuppFinalizationSO qtSuppFinalizationSO = null;
					try {
						qtSuppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);

						SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
						reqDTO.setQtId(qtId);
						ScQtFinalization scQtFinalization = qtSuppFinalizationSO.retrieveSupplierFinalizationByQtId(reqDTO)
							.getScQtFinalization();
						ScQt scQt = this.getManagedBean().getQt();
						int formCommitteeFlag = FORM_NO_ACTION;
						int reformCommitteeFlag = REFORM_NO_ACTION;
						if (ParameterConst.EVALUATION_TYPES_TWO_TIER.equals(scQt.getEvaluationType())) {
							if (scQtFinalization.isReEvaluateCompReq()) {
								formCommitteeFlag += FORM_OC;
								reformCommitteeFlag += REFORM_OC;
							}
							if (scQtFinalization.isReEvaluateTechReq()) {
								formCommitteeFlag += FORM_TEC;
								reformCommitteeFlag += REFORM_TEC;
							}
							if (scQtFinalization.isReEvaluateFinReq()) {
								formCommitteeFlag += FORM_FEC;
								reformCommitteeFlag += REFORM_FEC;
							}
						} else {
							formCommitteeFlag += FORM_OC;
							reformCommitteeFlag += REFORM_OC;
						}
						retrieveCommitteeAppointmentTabDtl(formCommitteeFlag, 
								reformCommitteeFlag);
						removeCommitteeActivityDetails(scQt);

					} finally {
						qtSuppFinalizationSO = null;
					}
					
				} else if (SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
						.equalsIgnoreCase(taskName)) {
					// 29464-start
					this.getManagedBean().setActiveIndex(5);
					this.getManagedBean().setCommitteeAppointmentMenuTab(true);
					// 29464-end

					retrieveCommitteeAppointmentTabDtl(FORM_SC, REFORM_SC);
					
				} else {
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
					// 25037-start
					if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
						initCommitteeTabSingularReapproval(committeDataType);
					}
					// 25037-end
				}
																			
				if (isCommitteeAppointmentRemark) {
					initializeRemarksForCommitteeApproval();
				}
				
				this.getManagedBean().setReadOnly(false);
				this.getManagedBean().setCommitteeAppointment(true);
				this.getManagedBean().setCommitteeAppointmentRemark(isCommitteeAppointmentRemark);
				this.getManagedBean().setCurrentStatusId(currentStatusId);
			} else {
				// qtId = quotationTenderDataType.getQtId().longValue();
				SCQuotationTenderDataType quationDataType = (SCQuotationTenderDataType) payLoad;
				qtId = quationDataType.getQtId().longValue();
				// retrieveFinalizationSecratryDtl(qtId);
				retrieveQuationTenderDetails(new Long(qtId));
				ScQt scQt = this.getManagedBean().getQt();
				if (scQt.getObtainSpecialApproval() != null
						&& scQt.getObtainSpecialApproval()) {
					this.getManagedBean().setProcMethodDropdownPresent(
							checkForProcumentMethodDropdown(scQt));
				}
				checkForPublishperiodAndValidityDays(scQt,fc);
			}
			
			if (null == this.getManagedBean().getQt().getProcurementMode()) {
				this.getManagedBean().getQt().setProcurementMode(new PmParameter());
			}

			// #9019 -start-
			// for load existing values
			if (null == this.getManagedBean().getQt().getProcurementTypeCatId()) {
				this.getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
			}

			if (null == this.getManagedBean().getQt().getFulfilmentType()) {
				this.getManagedBean().getQt().setFulfilmentType(new PmParameter());
			}

			if (null == this.getManagedBean().getQt().getPmProcurementCategory()) {
				this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());
			}

			if (null == this.getManagedBean().getQt().getContractType()) {
				this.getManagedBean().getQt().setContractType(new PmParameter());
			}

			// #29732 Start
			if (this.getManagedBean().getQt().getQtStatus().getStatusId()
					.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)
					|| this.getManagedBean().getQt().getQtStatus().getStatusId()
							.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| this.getManagedBean().getQt().getQtStatus().getStatusId()
							.equals(QTStatusConst.QT_REVISION_PUBLICATION)) {
				String changedCatCodeList = checkCatCodeChange();

				if (!changedCatCodeList.isEmpty()) {
					this.getManagedBean().setActiveIndex(1);
					retrieveSuppQualificationTabDtl();
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
									"lbl.sc.category.has.changes.on.prerender.view.1", null,
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, ResourceBundleUtils.getMessage(
									"lbl.sc.category.has.changes.on.prerender.view.2",
									new String[] { changedCatCodeList }, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale())));
				}
			}
			// #29732 End

			// initializing remark for cancel or subsequent save start
			if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
				ScQt scQt = this.getManagedBean().getQt();

				List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

				for (ScQtRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(true);
				}

				this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

			} else {
				ScQt scQt = this.getManagedBean().getQt();

				this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
			}

			// - Start (Harun) rendering Group Item that left blank but panel is selected
			if (this.getManagedBean().getQt().isPanel() && null == this.getManagedBean().getQt().getItemGroup()) {
				this.getManagedBean().getQt().setItemGroup(new CtPanelItemGroup());
			}
			// 9019 - End (Harun)

			if (SCProcessNameConst.REVISE_PCB.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				this.getManagedBean().setPcbProcess(true);
			}
			//#25692-start
			if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(this.getManagedBean().getTaskName())) {
				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(this.getManagedBean().getQt().getQtId(), ParameterConst.DOCTYPE_BPK_APPROVAL);
				this.getManagedBean().setSubStatusId(sts.getStatusId());
			}
			//#25692-End

			//assertSpecificationUpdateInProgress(false);//25037 #568 - to tally with 15391
			
			fc.getApplication().getNavigationHandler()
					.handleNavigation(fc, null, QTConstants.MAINTANANCE_PAGE);
			
		} catch (Exception e) {
			handleException(e);
		}
	}
	
	private void initializeRemarksForCommitteeApproval() {
		ScQt scQt = this.getManagedBean().getQt();
		List<ScQtApproval> approvals = scQt.getScQtApprovals();

		for (ScQtApproval scQtApproval : approvals) {
			if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
					.equalsIgnoreCase(scQtApproval
							.getApprovalCategory().getParameterCode())) {
				IScWorkFlowStatusSO workFlowStatusSO;
				try {
					workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
					ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
					reqDTO.setDocType(ParameterConst.DOCTYPE_COMMITTE_APROVAL);
					reqDTO.setDocId(scQt.getQtId());
					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					initializeRemarks(scQtApproval, currentWorkFlowStatus,
							scQtApproval.getScQtApprovalRemarks(),
							ScQtApprovalRemark.class);
					this.getManagedBean().setScQtApproval(scQtApproval);
				} finally {
					workFlowStatusSO = null;
				}

				break;
			}
		}
	}

	/**
	 * Update bpm task.
	 * 
	 * @param outcome
	 *            the outcome
	 * @return the string
	 */
	// #29732 -start-
	public void updateBpmTask(String outcome) {
		// #29732 -end-

		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("updateBpmTask");

		// #29732 Start
		if (this.getManagedBean().getQt().getQtStatus().getStatusId().equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)
				|| this.getManagedBean().getQt().getQtStatus().getStatusId()
						.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
				|| this.getManagedBean().getQt().getQtStatus().getStatusId()
						.equals(QTStatusConst.QT_REVISION_PUBLICATION)) {
			String changedCatCodeList = checkCatCodeChange();

			if (!changedCatCodeList.isEmpty()) {
				this.getManagedBean().setCatCodeList(changedCatCodeList);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
		}
		// #29732 End

		updateApprovalDetails(scQt);

		// Bug #35395 - Remove code #14398 - cater remark by publication approval to void duplicate

		// #28644-start
		if (null != this.getManagedBean().getTaskName() && !SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getTaskName())) {
			// #30435, #25077- Start.
			if (!getManagedBean().isApproverTabLoaded()) {
				retrieveApprovalTabDtl();
			}
			// #30435, #25077 - End.
		}
		// #28644-End

		// Code will be called if user didn't visit the supplier qualification
		// tab.
		if (null == this.getManagedBean().getSupplierCriteria()) {
			retrieveSuppQualificationTabDtl();
		}
		
		// Start Reopen 9019 #note-317
		boolean emptyRemarksExist = false;

		if (null != this.getManagedBean().getQt().getScQtRemarks()
				&& CollectionUtils.isNotEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			for (int i = 0; i < this.getManagedBean().getQt().getScQtRemarks().size(); i++) {
				String userRemark = this.getManagedBean().getQt().getScQtRemarks().get(i).getRemark();
			
				if (null == userRemark || "".equals(userRemark)) {
					emptyRemarksExist = true;
				} else {
					emptyRemarksExist = false;
				}
			}

			if (emptyRemarksExist) {
				List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

				for (ScQtRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(true);
				}
				
				this.getManagedBean().getQt().setScQtRemarks(qtRemarks);
			} else {
				this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
			}
		} else {
			if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
				List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

				for (ScQtRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(true);
				}
				
				this.getManagedBean().getQt().setScQtRemarks(qtRemarks);
			} else {
				this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
			}
		}
		// End Reopen 9019 #note-317

		QuationTenderReqDTO qtReqDTO = prepareQTInfo();

		if (this.getManagedBean().isPcbProcess()) {
			qtReqDTO.setPcbProcess(true);
		}

		FacesScopes facesScopes = new FacesScopes();
		Object ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		String taskName = this.getManagedBean().getTaskName();

		qtReqDTO.setOutcome(outcome);
		IQTDetailSO detailSO;
		ICommitteSO committeSO;
		try {
			ScQt qt = qtReqDTO.getQtdetail();
			Object payLoad = getManagedBean().getPayLoad();
			// Fix for Other Approval Validations on submit only
			qt.setOtherApproval(true);
			qt.setFCApproval(true);
			if (ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA
					.equalsIgnoreCase(qt.getPmProcurementCategory()
							.getParameterCode())) {
				qt.setMitiApproval(true);
				// qt.setMitiApproval(true);
			}

			Long currentStatus = qt.getQtStatus().getStatusId();

			// #25077 Start
			if (currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION)
					|| currentStatus.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)
					&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT)) {
				if (checkSettingPopUpBPKApprovalNeeded(qt)) {
					if (!this.getManagedBean().isResetBpkCurrentArea()) {
						if (popUpBPKApprovalNeededOnceHaveRequestApproval(qt)) {
							this.getManagedBean().setShowPopUpBPKApprovalNeeded(true);
							
							RequestContext.getCurrentInstance().execute("bpkApprovalNeededDlgTemp.show()");
							
							throw new EpException(" ");
						}
					}
				}
				
				checkingOtherApprovalFileAttachmentNeedDeleted(qt.getScQtApprovals(), ParameterConst.APPROVAL_CATEGORY_OTHERS);
				checkValidationForStatusAndFileAttachment(qt.getScQtApprovals(), ParameterConst.APPROVAL_CATEGORY_OTHERS);
			}
			// #25077 End

			// #30477, #25426 Start
			validateUserInactive(taskName, currentStatus, outcome);
			
			// #30477, #25426 End

			if (currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION)
					|| currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)
					|| currentStatus.equals(QTStatusConst.QT_PENDING_DOCUMENT_REVISION)) {

				// check if its Pending Schedule and Documents , only execute
				// this part
				if (null == this.getManagedBean().getScQtDocReportList()) {
					retrieveDocumentTabDtl();

					qt.setScQtDocReports(this.getManagedBean()
							.getScQtDocReportList());
				}
				if (this.getManagedBean().getQt().isSampleReq()) {
					this.getManagedBean().setSubmit(true);
					retrieveScheduleTabDtl();
					qt.setScQtSampleSubmission(this.getManagedBean()
							.getScQtSampleSubmissionList());
					qt.setSampleClosingDate(qt.getClosingDate());
				} else {
					retrieveScheduleTabDtl();
					
				}
				
				qt.setClosingDate(getManagedBean().getQt().getClosingDate());
				if (null == qt.getProposalStartDate()) {
					qt.setProposalStartDate(getManagedBean().getQt()
							.getProposalStartDate());
				}
				/*
				 * QTScheduleDetailBackingBean scheduleBackingBean = new
				 * QTScheduleDetailBackingBean();
				 * scheduleBackingBean.handlePublishClosingDate();
				 * qt.setClosingDate(getManagedBean().getQt().getClosingDate());
				 */
			}
			CommitteResDTO committeResDto = null;
			QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_SCORING_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_TEC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_FEC_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				
				 committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				updateApprovalRequestDtls(qt.getScQtApprovals(),
						ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setScQt(qt);
				committeReqDTO.setQtCommittee(qt.getScQtCommittees());
				committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				committeReqDTO.setToBeDeletedMembers(this.getManagedBean()
						.getToBeDelCommitteMemebers());
				committeReqDTO.setTaskName(taskName); // Bug #34972@Syazani 09/08/2023
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				
			} else if (SCProcessNameConst.ASSIGN_FC_SECRETARY
					.equalsIgnoreCase(taskName)) {
				validateFCSecretaryDtl(qt.getFinalizationCommitte(), qt.getFinalizationSecratery());
				
				 committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(payLoad);
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
				committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO
						.updateFinalizateSecrataryTask(committeReqDTO);

			} else {
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setPayLoad(payLoad);
				qtReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
				qtReqDTO.setUserLocale(facesScope.getUserLocale());
				qtReqDTO.setDoaList(this.getManagedBean().getPmuserContactpersonList());// 25037
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				String approvalCategory = ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT;
				if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_BPK;
				} else if (SCProcessNameConst.REVISE_PCB
						.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
				} else if (SCProcessNameConst.REVISE_QT_SCHEDULE
						.equalsIgnoreCase(taskName)) {
					approvalCategory = ParameterConst.APPROVAL_CATEGORY_PTJ;
				}
				if (null != approvalCategory) {
					updateApprovalRequestDtls(qt.getScQtApprovals(),
							approvalCategory);
				}

				this.getManagedBean().setResetBpkCurrentArea(false); //#25077

				qtResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
			}

			if ((null != committeResDto && committeResDto.isSuccessful())
					|| (null != qtResDTO && qtResDTO.isSuccessful())) {

				//#25037 start
				if(this.getManagedBean().isReapproval()){
					List<ScQtCommittee> approveList = new ArrayList<>();
					if (committeResDto != null && CollectionUtils.isNotEmpty(committeResDto.getQtCommittes())
							&& SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
						for (ScQtCommittee qtCommittee : committeResDto.getQtCommittes()) {
							Long statusId = qtCommittee.getScCommittee().getCommitteStatus() != null ? qtCommittee
									.getScCommittee().getCommitteStatus().getStatusId() : 0L;
							if (Arrays.asList(
									new Long[] { QTStatusConst.CMT_PENDING_APPROVAL,
											QTStatusConst.CMT_PENDING_REVISION_APPROVAL }).contains(statusId)) {
								approveList.add(qtCommittee);
							}
						}
						String procurementMode = this.getParameterDesc(scQt.getProcurementMode());
						String qtNo = scQt.getQtNo();
						successMessage(MessageConst.INFO_760504, new String[] { getCommitteeNames(approveList),
								procurementMode, qtNo }, true);
					}
				}else{
					successMessage(getMsgCodeByTaskName(taskName), ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_QUOTATION, qt.getQtNo(), Boolean.TRUE);
				}
				renewSesionObject();			
				//#25037 end
			
				debugLogger("end of updateBpmTask");
//				return CommonUIConst.ACKNOWLEDGE_PAGE; // #29732 remove
			}
		} finally {
			detailSO = null;
			committeSO=null;
		}
//		return null; // #29732 remove
	}
	
	/**
	 * #30477
	 * To validate User Inactive
	 * @param taskName
	 * @param currentStatus
	 * @param outcome
	 */
	public void validateUserInactive(String taskName, Long currentStatus, String outcome){
		
		List<ScQtApproval> scQtApprovals = this.getManagedBean().getQt().getScQtApprovals();
		//#30477 Start Vinod
		if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE.equalsIgnoreCase(taskName)) {
			// #11299 Start Vinod
			boolean isCmtPendingRevision = false;
			
			for (ScQtCommittee committee : this.getManagedBean().getQt().getScQtCommittees()) {
				if (committee.getScCommittee().getCommitteStatus().getStatusId()
						.equals(QTStatusConst.CMT_PENDING_REVISION)) {
					isCmtPendingRevision = true;
					break;
				}
			}
						
			for (ScQtApproval approval : scQtApprovals) {

				boolean valCondition = (isCmtPendingRevision || currentStatus.equals(QTStatusConst.QT_RE_EVALUATION))
						&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT)
						&& isInactiveCommiteeApproverRole(null, approval.getApproverSettings(),
								RoleCode.COMMITTEE_APPROVER.value());

				boolean valConditionOthers = !approval.getApprovalCategory().getParameterCode()
						.equals(ParameterConst.DOCTYPE_BPK_APPROVAL)
						&& (CollectionUtils.isEmpty(approval.getApproverSettings()));
				
				if ((valCondition && approval.getApprovalCategory().getParameterCode()
						.equals(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT))
						|| valConditionOthers) {
					// #11299 Start Vinod
					throw new EpException(MessageConst.ERROR_775174);
					// #11299 End Vinod
				}
			}
		}
			
		if (SCProcessNameConst.REVISE_QT_SCHEDULE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.PREPARE_QT_SCHEDULE.equalsIgnoreCase(taskName)) {
			// #11299 Start Vinod updated error message
			List<ScQtApproverSettings> approverSettings = this.getManagedBean().getPublicationApproval()
					.getApproverSettings();

			if (CollectionUtils.isEmpty(approverSettings)) {
				this.getManagedBean().setResetBpkCurrentArea(false); // #25077
				throw new EpException(MessageConst.ERROR_775174);
			}

			if ((currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)
					|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION) || currentStatus
						.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT))
					&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT)
					) {

				validatePublicationApproverInactive(approverSettings);
				validateDOAInactive();
				validateBPKInactive();
			}
			// #11299 End Vinod
		}	
	}
	
	/**
	 * #30477
	 * validate DOA inactive
	 */
	public void validateDOAInactive() {
		
		List<ScQtContactPerson> scQtContactPersons = this.getManagedBean().getQt().getScQtContactPersons();
			
		if (scQtContactPersons == null || scQtContactPersons.isEmpty()){
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			scQtContactPersons = detailSO.retrieveContactPersonByQtIdNType(
					this.getManagedBean().getQt().getQtId(), SourcingConstants.OFFICER_TYPE_DOAA);	
		}
		
		List<Long> userIds = new ArrayList<>();
		for (ScQtContactPerson scQtContactPerson : scQtContactPersons) {
			if (scQtContactPerson.getOfficerType().equals(SourcingConstants.OFFICER_TYPE_DOAA)){
				userIds.add(scQtContactPerson.getUser().getUserId());
			}	
		}

		if (isInactiveCommiteeApproverRole(userIds, null, RoleCode.DESK_ASSISTANT.value())) {
			String roleName = getEpRoleDescByRoleCode(RoleCode.DESK_ASSISTANT.value());
			
			throw new EpException(MessageUtils.getMessage(MessageConst.ERROR_775171, new String[] {
					roleName},
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
		}
	}
	
	/**
	 * #30477
	 * validate BPK inactive
	 */
	public void validateBPKInactive() {
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		
		List<ScQtApproval> scQtApprovals = this.getManagedBean().getQt().getScQtApprovals();
		if (detailSO.isApprovalRequired(scQtApprovals, ParameterConst.APPROVAL_CATEGORY_BPK)) {

			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
			reqDTO.setOrgType(OrganizationType.BAHAGIAN_PERBENDAHARAAN_KERAJAAN.value());
			List<PmUser> pmUsers = userSO.retrieveUserListByRole(reqDTO).getUserList();
			List<Long> userIds = new ArrayList<>();
			for (PmUser mUser : pmUsers) {
				userIds.add(mUser.getUserId());
			}

			if (isInactiveCommiteeApproverRole(userIds, null, RoleCode.BPK_APPROVER.value())) {
				String roleName = getEpRoleDescByRoleCode(RoleCode.BPK_APPROVER.value());
				throw new EpException(MessageUtils.getMessage(MessageConst.ERROR_775171, new String[] { roleName },
						MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));

			}
		}
	}
	
	/**
	 * #30477
	 * validate publication Approver inactive
	 * @param approverSettings
	 */
	public void validatePublicationApproverInactive(List<ScQtApproverSettings> approverSettings) {

		if (isInactiveCommiteeApproverRole(null, approverSettings, RoleCode.PUBLICATION_APPROVER.value())) {
			String roleName = getEpRoleDescByRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			this.getManagedBean().setResetBpkCurrentArea(false); // #25077
			throw new EpException(MessageUtils.getMessage(MessageConst.ERROR_775171, new String[] { roleName },
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));

		}
	}

	/**
	 * Sets the out come action.
	 * 
	 * @param outCome
	 *            the new out come action
	 */
	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	/**
	 * Retrieve approval tab dtl.
	 */
	public void retrieveApprovalTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveApprovalTabDtl");
		// Added by Fara on 03092014 - Fix for Approval tab if user dint visit
		// the Committee Appointment tab
		List<TabBean> tabList = this.getManagedBean()
				.getScCommitteeMemberTabList();
		if (CollectionUtils.isEmpty(tabList)) {
			retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
		}
		updateQtDtls();
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			List<ScQtBsv> scQtBsvs = constructBriefingSiteVisitList(scQt);
			scQt.setScQtBriefings(scQtBsvs);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO resDTO = null;

			reqDTO.setQtdetail(scQt);
			// #25692 Lazy error fix Approval tab. -start-
			// reqDTO.setAvaliableApprovals(scQt.getScQtApprovals());
			// #25692 Lazy error fix Approval tab. -end-
			reqDTO.setAmendMentBean(this.getManagedBean().getAmendMentBean());
			reqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
			// reqDTO.setRuleDataBean(this.getManagedBean().getRuleDataBean());
			resDTO = detailSO.retriveQTApprovalDetails(reqDTO);

			List<ScQtApproval> requiredApproval = resDTO.getApproval();

			List<TabBean> tabBeans = new ArrayList<TabBean>();
			List<TabBean> existingApprovalTabs = this.getManagedBean()
					.getTabList();

			ParameterHelper parameterHelper = ParameterHelper.getInstance();
			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {
					qtApproval = checkIsApprovalExists(qtApproval,
							existingApprovalTabs);

					if (!qtApproval.isDeleted()) {
						String approvalCategoryCode = qtApproval
								.getApprovalCategory().getParameterCode();
						String titleName = parameterHelper.getParameterDesc(
								ParameterTypeConst.APPROVAL_CATEGORY,
								approvalCategoryCode);
                      if(null!=scQt.getCurrentWorkFlowStatus() && scQt.getCurrentWorkFlowStatus().isBrandNew()){
                    	if(null==qtApproval.getScQtApprovalRemarks() || qtApproval.getScQtApprovalRemarks().size()==0) {  
						qtApproval.setScQtApprovalRemarks(initializeRemarks(
								qtApproval, scQt.getCurrentWorkFlowStatus(),
								qtApproval.getScQtApprovalRemarks(),
								ScQtApprovalRemark.class));
                    	}
                         }else{
							// 13344, 25037 -start-
							if (!isApprovalScreen() && existingApprovalTabs == null) {
								// 13344, 25037 -End-
	                        	 qtApproval.setScQtApprovalRemarks(initializeRemarks(
	     								qtApproval, scQt.getCurrentWorkFlowStatus(),
	     								qtApproval.getScQtApprovalRemarks(),
	     								ScQtApprovalRemark.class)); 
								// 13344_start
							}
							// 13344_End
                         }
						// qtApproval.setScQtApprovalAttachment(null);
						// qtApproval.setScQt(this.getManagedBean().getQt());

						/*
						 * // BUDGET_APPROVAL if
						 * (qtApproval.getApprovalCategory().getParameterCode()
						 * .equals(ParameterConst.APPROVAL_CATEGORY_BUDGET)) {
						 * tabBeans.add(new
						 * TabBean(qtApproval.getApprovalType(), qtApproval,
						 * approverSettingDtls( RoleCode.BUDGET_APPROVER,
						 * qtApproval))); } else
						 */
						List<PmParameter> settings = null;
						if (ParameterConst.APPROVAL_CATEGORY_BPK
								.equals(approvalCategoryCode)) {
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval, settings));
						} else if (ParameterConst.APPROVAL_CATEGORY_PTJ
								.equals(approvalCategoryCode)) {
							Long statusId = scQt.getQtStatus().getStatusId();
							if (isQTStatusPublished(statusId)) {
								tabBeans.add(new TabBean(titleName,
										approvalCategoryCode, qtApproval,
										approverSettingDtls(
												RoleCode.PUBLICATION_APPROVER,
												scQt.getPmOrgProfile()
														.getOrgProfileId(),
												qtApproval)));
							} else if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) ||
									QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId) ||
									isStatusOfBpkOrScheduleRevise(statusId)) {
								this.getManagedBean().setPublicationApproval(qtApproval);
							}
						} else if (ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE
								.equals(approvalCategoryCode) ||
								ParameterConst.APPROVAL_CATEGORY_PROCUREMENT_BOARD
								.equals(approvalCategoryCode)) {
							List<ScQtApproverSettings> approvalSettings = qtApproval
									.getApproverSettings();
							if (null == approvalSettings
									|| approvalSettings.isEmpty()) {
								approvalSettings = new ArrayList<ScQtApproverSettings>();
								ScQtApproverSettings approverSettings = new ScQtApproverSettings();
								approverSettings.setPmUser(new PmUser());
								approverSettings.setScQtApproval(qtApproval);
								approverSettings.setCommitteeType(new PmParameter());
								approvalSettings.add(approverSettings);
								qtApproval
										.setApproverSettings(approvalSettings);
							} else {
								for (ScQtApproverSettings approverSettings : approvalSettings) {
									approverSettings.setScQtApproval(qtApproval);
									
									// Added to populate security if Finalization committee is same as Committee Appointment
									PmParameter finalizationCommitee = scQt.getFinalizationCommitte();
									PmParameter committeeSettings = approverSettings.getCommitteeType();
									if (committeeSettings != null
											&& finalizationCommitee != null
											&& finalizationCommitee.getParameterId() != null
											&& finalizationCommitee.getParameterCode()
													.equalsIgnoreCase(committeeSettings.getParameterCode())) {
										approverSettings.setPmUser(scQt.getFinalizationSecratery());
									}
								}
							}
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval,
									retrieveFinalizationCommitteeType()));
						} else if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT.equals(approvalCategoryCode)) {
							//#26146 - start -
							Long[] orgIds = { scQt.getOwnerOrgProfile().getOrgProfileId(),scQt.getPmOrgProfile().getOrgProfileId() };
							
							DualListModel<ScQtApproverSettings> budgetApprovalUsers = ApproverSettings.getDetails(
									RoleCode.COMMITTEE_APPROVER,orgIds, qtApproval);
											   
							tabBeans.add(new TabBean(titleName, approvalCategoryCode, qtApproval, budgetApprovalUsers));
							//#26146 - end -
																											
						} else if (ParameterConst.APPROVAL_CATEGORY_OTHERS
								.equals(approvalCategoryCode)) {
							// #25077 - Start
							List<ScQtApprovalRequest> approvalRequestHistory = qtApproval.getHistoryApprovalRequests();
							
							if (null != approvalRequestHistory && !approvalRequestHistory.isEmpty()) {
								for (int i = 0; i < approvalRequestHistory.size(); i++) {
									PmParameter parameter = approvalRequestHistory.get(i).getApproverAction();
							
									if (null != parameter) {
										if (approvalRequestHistory.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
											if (this.getManagedBean().isFlagFileAttahmentDeletedICT()) {
												if (null == getManagedBean().getScQtApprovalReqAttachementHistoryICT()) {
													approvalRequestHistory.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementHistoryICT());
												} else {
													approvalRequestHistory.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementHistoryICT());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementHistoryICT()) {
													approvalRequestHistory.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementHistoryICT());
												}
											}
										}
										
										if (approvalRequestHistory.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
											if (this.getManagedBean().isFlagFileAttahmentDeletedMITI()) {
												if (null == getManagedBean().getScQtApprovalReqAttachementHistoryMITI()) {
													approvalRequestHistory
															.get(i)
															.setApprovalRequestAttachement(
																	getManagedBean()
																			.getScQtApprovalReqAttachementHistoryMITI());
												} else {
													approvalRequestHistory
															.get(i)
															.setApprovalRequestAttachement(
																	getManagedBean()
																			.getScQtApprovalReqAttachementHistoryMITI());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementHistoryMITI()) {
													approvalRequestHistory
															.get(i)
															.setApprovalRequestAttachement(
																	getManagedBean()
																			.getScQtApprovalReqAttachementHistoryMITI());
												}
											}
										}
									}
								}
							}
							// #25077 - End

							List<TabBean> approvalOthersTab = this.getManagedBean().getTabOtherApproval();
							
							if (CollectionUtils.isNotEmpty(approvalOthersTab)) {
								tabBeans.addAll(approvalOthersTab);
							} else {
								// #25077 - End

								tabBeans.add(new TabBean(titleName, approvalCategoryCode, qtApproval, settings));

								// #25077 - Start
							}
							// #25077 - End
						}
					} else {
						if (this.getManagedBean().getToBeRemoveList() == null) {
							this.getManagedBean().setToBeApprovalRemoveList(
									new ArrayList<ScQtApproval>());

						}
						this.getManagedBean().getToBeApprovalRemoveList()
								.add(qtApproval);
					}
				}
			}
			
			for (TabBean approvalTabBean : tabBeans) {
				if (ParameterConst.APPROVAL_CATEGORY_OTHERS
						.equals(approvalTabBean.getTabType())) {
					approvalTabBean.setTooltip(ResourceBundleUtils.getMessage(
							"lbl.sc.qt.approval."
									+ approvalTabBean.getTabType()
											.toLowerCase() + ".tooltip",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()));
				}
			}

			// Added by Fara on 03092014 - Fix for Approval tab if no data for
			// approval
			if (CollectionUtils.isNotEmpty(tabBeans)) {
				int selectedTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
				if (selectedTabIndex > tabBeans.size()-1) {
					selectedTabIndex = 0;
				}
				this.getManagedBean().setSelectedApprovalTabBean(
						tabBeans.get(selectedTabIndex));
				// #13344 start - bind remark & attachment
				// #29607 - add null pointer checking for remark & attachment
				if (this.getManagedBean().getScQtApproval() != null) {
					if (this.getManagedBean().getScQtApproval().getScQtApprovalAttachments() != null) {
						this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval().setScQtApprovalAttachments(this.getManagedBean().getScQtApproval().getScQtApprovalAttachments());
					}
					if (this.getManagedBean().getScQtApproval().getScQtApprovalRemarks() != null) {
						this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval().setScQtApprovalRemarks(this.getManagedBean().getScQtApproval().getScQtApprovalRemarks());
					}
				}
				// #13344 end
				this.getManagedBean().setScQtApproval(
						this.getManagedBean().getSelectedApprovalTabBean()
								.getScQtApproval());
			}
			
			this.getManagedBean().setTabList(tabBeans);
			// #30435, #25077 - Start.
			this.getManagedBean().setApproverTabLoaded(true);
			// #30435, #25077 - End.
		} finally {
			detailSO = null;
		}
	}
	
	// #34694,#25077 - Start
	/**
	 * Retrieve Others approval tab dtl.
	 */
	public void retrieveOthersApprovalTabDtl() {

		IQTDetailSO detailSO;

		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

			reqDTO.setQtdetail(scQt);

			QuationTenderResDTO resDTO = detailSO.retriveQTApprovalDetails(reqDTO);
			List<ScQtApproval> requiredApproval = resDTO.getApproval();
			List<TabBean> tabBeans = new ArrayList<>();

			ParameterHelper parameterHelper = ParameterHelper.getInstance();

			if (requiredApproval != null) {
				for (ScQtApproval qtApproval : requiredApproval) {

					if (!qtApproval.isDeleted()) {
						String approvalCategoryCode = qtApproval.getApprovalCategory().getParameterCode();
						String titleName = parameterHelper.getParameterDesc(ParameterTypeConst.APPROVAL_CATEGORY,
								approvalCategoryCode);

						List<PmParameter> settings = null;

						if (ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(approvalCategoryCode)) {
							boolean isApprovalOthers = false;
							List<ScQtApprovalRequest> approvalRequest = qtApproval.getScQtApprovalDtls();

							if (null != approvalRequest && !approvalRequest.isEmpty()) {
								for (int i = 0; i < approvalRequest.size(); i++) {

									isApprovalOthers = approvalRequest.get(i).isOthersApprovalOnly();
									PmParameter parameter = approvalRequest.get(i).getApproverAction();

									if (null == parameter) {
	                                	 
	                                	// Retrieve ScQtApprovalRequest without save data & do submit.
										if (null != getManagedBean().getPmParam()) {
											approvalRequest.get(i).setApproverAction(getManagedBean().getPmParam());
										} else {
											approvalRequest.get(i).setApproverAction(new PmParameter());
										}

										ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
										approvalRemark.setApprovalRequest(approvalRequest.get(i));

										approvalRequest.get(i).setAppRequestRemark(approvalRemark);

										if (approvalRequest.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
											if (this.getManagedBean().isFlagFileAttahmentDeletedICT()) {
												if (null != getManagedBean().getScQtApprovalReqAttachementICT()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementICT());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementICT()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementICT());
												}
											}

											if (null != getManagedBean().getScQtAppRequestRemarkICT()) {
												approvalRemark.setApprovalRequest(approvalRequest.get(i));
												approvalRequest.get(i).getAppRequestRemark()
														.setRemark(getManagedBean().getScQtAppRequestRemarkICT());
											}
										}

										if (approvalRequest.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
											if (this.getManagedBean().isFlagFileAttahmentDeletedMITI()) {
												if (null != getManagedBean().getScQtApprovalReqAttachementMITI()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementMITI());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementMITI()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementMITI());
												}
											}

											if (null != getManagedBean().getScQtAppRequestRemarkMITI()) {
												approvalRemark.setApprovalRequest(approvalRequest.get(i));
												approvalRequest.get(i).getAppRequestRemark()
														.setRemark(getManagedBean().getScQtAppRequestRemarkMITI());
											}
										}

									} else {

										// Retrieve ScQtApprovalRequest with save data.
										if (null != getManagedBean().getPmParam()) {
											approvalRequest.get(i).setApproverAction(getManagedBean().getPmParam());
										}

										if (approvalRequest.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
											// 34502&25635-start
											if (this.getManagedBean().getQt().isCloneEnabled()) {
												approvalRequest.get(i).getAppRequestRemark()
														.setRemark(getManagedBean().getScQtAppRequestRemarkICT());
												approvalRequest.get(i).setApproverAction(new PmParameter());
												approvalRequest.get(i).setApprovalRequestAttachement(null);
												
												this.getManagedBean().getQt().setCloneEnabled(Boolean.FALSE);
											}
											// 34502&25635-end
											
											if (this.getManagedBean().isFlagFileAttahmentDeletedICT()) {
												if (null != getManagedBean().getScQtApprovalReqAttachementICT()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementICT());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementICT()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementICT());
												}
											}

											if (approvalRequest.get(i).getAppRequestRemark() != null
													&& StringUtils.isNotEmpty(approvalRequest.get(i)
															.getAppRequestRemark().getRemark())) {
												getManagedBean().setScQtAppRequestRemarkICT(
														approvalRequest.get(i).getAppRequestRemark().getRemark());
											}

											if (null == approvalRequest.get(i).getAppRequestRemark()) {
												ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
												approvalRemark.setApprovalRequest(approvalRequest.get(i));
												
												approvalRequest.get(i).setAppRequestRemark(approvalRemark);
												approvalRequest.get(i).getAppRequestRemark().setRemark(" ");
											}
										}
										if (approvalRequest.get(i).getApprovalType().getParameterCode()
												.equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
											if (this.getManagedBean().isFlagFileAttahmentDeletedMITI()) {
												if (null != getManagedBean().getScQtApprovalReqAttachementMITI()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementMITI());
												}
											} else {
												if (null != getManagedBean().getScQtApprovalReqAttachementMITI()) {
													approvalRequest.get(i).setApprovalRequestAttachement(
															getManagedBean().getScQtApprovalReqAttachementMITI());
												}
											}

											if (approvalRequest.get(i).getAppRequestRemark() != null
													&& StringUtils.isNotEmpty(approvalRequest.get(i)
															.getAppRequestRemark().getRemark())) {
												getManagedBean().setScQtAppRequestRemarkMITI(
														approvalRequest.get(i).getAppRequestRemark().getRemark());
											}

											if (null == approvalRequest.get(i).getAppRequestRemark()) {
												ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
												approvalRemark.setApprovalRequest(approvalRequest.get(i));
												
												approvalRequest.get(i).setAppRequestRemark(approvalRemark);
												approvalRequest.get(i).getAppRequestRemark().setRemark(" ");
											}
										}
									}
								}
							}

							if (CollectionUtils.isNotEmpty(qtApproval.getScQtApprovalDtls())) {
								if (isApprovalOthers) {
									tabBeans.add(new TabBean(titleName, approvalCategoryCode, qtApproval, settings));
								} else {
									if (this.isQTStatusLessThanOrEquals(scQt.getQtStatus().getStatusId(),
											QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) {
										qtApproval.setScQtApprovalDtls(getListOthersApprovalRequest());
										tabBeans.add(new TabBean(titleName, approvalCategoryCode, qtApproval, settings));
									} else {
										qtApproval.setScQtApprovalDtls(new ArrayList<ScQtApprovalRequest>());
										tabBeans.add(new TabBean(titleName, approvalCategoryCode, qtApproval, settings));
									}
								}
							}
						}
					}
				}
			}

			this.getManagedBean().setTabOtherApproval(tabBeans);
			this.getManagedBean().setApproverTabLoaded(true);

		} finally {
			detailSO = null;
		}
	}
	// #34694,#25077 - End

	/**
	 * Retrieve supp qualification tab dtl.
	 */
	public void retrieveSuppQualificationTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveSuppQualificationTabDtl");
		
		if (!scQt.isBrandNew() && scQt.isZonal() && scQt.isPanel()) {
			retrieveQuationTenderDetails(scQt.getQtId());
		}
		IQTDetailSO QtDetailsSO;
		QuationTenderResDTO tenderResDTO;

		try {
			QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
			// tenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			tenderReqDTO.setQtdetail(this.getManagedBean().getQt());

			tenderResDTO = QtDetailsSO.retriveQTSuppQulCriDetails(tenderReqDTO);

			getManagedBean().setSupplierCriteria(tenderResDTO.getQtDetail().getScQtSupplierCriteria());
			// Commented as a fix for SIR#9289
//			scQt = tenderResDTO.getQtDetail();
			this.getManagedBean().setAllStateCode(tenderResDTO.getStateCode());
			if (!this.getManagedBean().getQt().isPanel()) {
				if (CollectionUtils.isEmpty(this.getManagedBean()
						.getQtCategoryList())) {
					List<ScQtCategory> categoryResults = tenderResDTO
							.getCategories();

					// 29732 fix duplicate issues -start-
					List<ScQtCategory> removeCategoryCodes = this.getManagedBean().getTobeRemoveCategoryCodes();

					if (null != categoryResults && !categoryResults.isEmpty() && null != removeCategoryCodes
							&& !removeCategoryCodes.isEmpty()) {
						for (int i2 = 0; i2 < removeCategoryCodes.size(); i2++) {
							for (int i = 0; i < categoryResults.size(); i++) {
								if (categoryResults.get(i).getQtCategoryId() == removeCategoryCodes.get(i2)
										.getQtCategoryId()) {
									removeCategoryCodes.remove(removeCategoryCodes.get(i2));
								}
							}
						}
					}
					// 29732 -end-

					this.getManagedBean().setQtCategoryList(categoryResults);
				}

				if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
						.equalsIgnoreCase(scQt.getQtType())) {
					if (CollectionUtils.isEmpty(this.getManagedBean()
							.getQtSupplierList())) {
						this.getManagedBean().setQtSupplierList(
								tenderResDTO.getRestrictedSuppliers());
						this.getManagedBean()
								.setTotalSuppliers(
										tenderResDTO.getRestrictedSuppliers() != null ? tenderResDTO
												.getRestrictedSuppliers()
												.size() : 0);
						if (ParameterConst.OPEN_FOR_BUMI.equals(scQt.getOpenFor())) {
							CreateQtBackingBean backingBean = new CreateQtBackingBean();
							backingBean.clearNonBumiSuppliers(scQt);
						}
					}
				} else {
					if (tenderResDTO.getTotalSuppliers() != null) {
						this.getManagedBean().setTotalSuppliers(tenderResDTO.getTotalSuppliers());	
					}
				} 
			String procurementMode=	scQt.getProcurementMode() !=null ?scQt.getProcurementMode().getParameterCode() : ParameterConst.PROCUREMENT_MODE_QUOTATION;
				boolean isQuotationThresHold = ParameterConst.PROCUREMENT_MODE_QUOTATION
						.equals(procurementMode);
				if (!isQuotationThresHold
						&& !scQt.getScQtSupplierCriteria().isAllState()) {
					if(this.getManagedBean().getAllStateCode() == PTJLocalityCode.STATE_PENINSULAR){
						scQt.getScQtSupplierCriteria().setAllState(true);
					} else if(null == scQt.getScQtSupplierCriteria()
							.getLocationLevel()){
						scQt.getScQtSupplierCriteria().setLocationLevel(new PmParameter());
					}
					
				}

				if (isQuotationThresHold
						&& ParameterConst.QUOTATION_TENDER_TYPE_OPEN
								.equalsIgnoreCase(scQt.getQtType())) {
					if (null == scQt.getScQtSupplierCriteria()
							.getLocationLevel()) {
						scQt.getScQtSupplierCriteria().setLocationLevel(
								new PmParameter());
					}
					scQt.getScQtSupplierCriteria().setAllState(false);
				}

				if (!scQt.getScQtSupplierCriteria().isAllState()) {
					List<ScQtLocCriteria> locCriteria = new ArrayList<ScQtLocCriteria>();
					
					//20191127@asyrafhaziq - Bug #26289 - Start
					boolean isSetLocCriteria = true ;
					if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
						if (null != this.getManagedBean().getZoneTrees() && !this.getManagedBean().getZoneTrees().isEmpty()&& 
								scQt.getScQtSupplierCriteria().getScQtLocCriteria() != null) {
							isSetLocCriteria = false;
						}
					}
					if (null != tenderResDTO.getLocationDetails() && !tenderResDTO.getLocationDetails().isEmpty() && isSetLocCriteria ) {
					//20191127@asyrafhaziq - Bug #26289 - End
						if (null == this.getManagedBean()
								.getTotalSuppliersSpecLoc()) {
							this.getManagedBean().setTotalSuppliersSpecLoc(
									new ArrayList<TplSpecLoc>());
						}
						getManagedBean().setZoneTrees(null);
						this.getManagedBean().getTotalSuppliersSpecLoc()
								.addAll(tenderResDTO.getLocationDetails());
						for (TplSpecLoc localLoc : tenderResDTO
								.getLocationDetails()) {
							if (null != scQt.getScQtSupplierCriteria()
									&& null != scQt.getScQtSupplierCriteria()
											.getLocationLevel()) {
								this.getManagedBean().setLocalLevelCode(
										scQt.getScQtSupplierCriteria()
												.getLocationLevel()
												.getParameterCode());
							}
							getManagedBean().setZoneTrees(
									new ArrayList<TreeNode>());
							ScQtLocCriteria criteria = (ScQtLocCriteria) localLoc;
							locCriteria.add(criteria);
						}
						if (null != getManagedBean().getZoneTrees()
								&& getManagedBean().getZoneTrees().size() == 0) {
							this.getManagedBean()
									.setExistingSupplierLocationList(
											locCriteria);
							getManagedBean().getZoneTrees().addAll(
									LocationSingleton.getInstance()
											.constuctTreeNode(locCriteria));
							scQt.getScQtSupplierCriteria().setScQtLocCriteria(locCriteria);
						}
					} else {
						if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())
								&& !scQt.getScQtSupplierCriteria().isAllState()
								&& ParameterConst.QUOTATION_TENDER_TYPE_OPEN.equalsIgnoreCase(scQt.getQtType())) {
							if (null == this.getManagedBean().getZoneTrees()
									|| this.getManagedBean().getZoneTrees().isEmpty()) {
								retrieveNonZonalDtls();
							}
						}
					}
				}
			} else {
				this.getManagedBean().setPanelItemList(new ArrayList<CtPanelItem>());
				if(CollectionUtils.isNotEmpty(tenderResDTO.getPanelItems())){
					this.getManagedBean().setPanelItemList(
							tenderResDTO.getPanelItems());
				}
				
				if (null != this.getManagedBean().getQt().getCircularVer()) {
					this.getManagedBean().setCircularExpDate(
							this.getManagedBean().getQt().getCircularVer()
									.getEndDate());
					this.getManagedBean().setCircularNo(
							this.getManagedBean().getQt().getCircularVer()
									.getCircularNo());
					this.getManagedBean().setCircularName(
							this.getManagedBean().getQt().getCircularVer()
									.getName());
					this.getManagedBean().setCircularType(
							this.getManagedBean().getQt().getCircularVer()
									.getDocType());
				}
				if (this.getManagedBean().getZoneGeneralTrees()== null ||
						this.getManagedBean().getZoneGeneralTrees().isEmpty()) {
					retriveZonesByCircularNo();
				}
			}
		} finally {
			QtDetailsSO=null;
		}
	}
	

	/**
	 * Retrive zones by circular no.
	 */
	private void retriveZonesByCircularNo() {
		debugLogger("retriveZonesByCircularNo");
		
		
		//PanelSupplierResDTO resDTO = new PanelSupplierResDTO();
		try {
			//PanelSupplierReqDTO reqDTO = new PanelSupplierReqDTO();

			//panelSupplierSO = (IPanelSupplierSO) BusinessDelegate.getInstance()
					//.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
							//IPanelSupplierSO.class);

			List<CtPanelItem> panelItemList = getManagedBean().getPanelItemList();
			
			/*if (CollectionUtils.isNotEmpty(panelItemList)){

				reqDTO.setPanelItem(panelItemList.get(0));
				resDTO = panelSupplierSO.retrievePanelZonesByPanelItem(reqDTO);
			}

			if (null != resDTO && null != resDTO.getCtPanelZones()
					&& !resDTO.getCtPanelZones().isEmpty()) {
				if (isNotNullOREmpty(resDTO.getCtPanelZones())) {
					this.getManagedBean().setCtzones(true);
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					if (null != getManagedBean().getZoneGeneralTrees()
							&& getManagedBean().getZoneGeneralTrees().size() == 0) {

						for (CtPanelZone panelZones : resDTO.getCtPanelZones()) {

							TplSpecZoneDtl tplSpecZoneDtl = new TplSpecZoneDtl();
							tplSpecZoneDtl.setZoneName(panelZones.getName());
							tplSpecZoneDtl.setPanelZone(panelZones);
							setUserDetails(tplSpecZoneDtl);

							List<PmState> states = SourcingLocationHelper
									.getStateFromCtPanelZone(panelZones
											.getPanelZoneDtls());
							List<PmDistrict> districts = SourcingLocationHelper
									.getDistrictFromCtPanelZone(panelZones
											.getPanelZoneDtls());

							getManagedBean()
									.getZoneGeneralTrees()
									.add(LocationSingleton.getInstance()
											.buildTreeWithDisabledChilds(
													tplSpecZoneDtl, states,
													districts, null, null,
													true, TplSpecZoneLoc.class));
						}
					}
				}
			}*/ //else {
				
				this.getManagedBean().setTotalSuppliers(0);
				
				if (CollectionUtils.isNotEmpty(panelItemList)) {
					getManagedBean().setZoneGeneralTrees(
							new ArrayList<TreeNode>());
					this.getManagedBean().setCtzones(false);
					IQTSupplierSO supplierSo = (IQTSupplierSO) BusinessDelegate
							.getInstance().getServiceObject(
									ModuleCodeConst.SUPPLIER_SELECTION,
									IQTSupplierSO.class);
					QTSupplierReqDTO supplierReqDTO = new QTSupplierReqDTO();
					QTSupplierResDTO supplierResDTO = new QTSupplierResDTO();
					supplierReqDTO.setPanelItemId(panelItemList.get(0).getPanelItemId());
				// #34220-start
				if (null != this.getManagedBean().getQt().getCircularVer().getCtCircular().getCurrentCircularVerId()
						&& null != this.getManagedBean().getQt().getCircularVer().getCtCircular()
								.getCurrentCircularVerId().getCircularVerId()
						&& !this.getManagedBean()
								.getQt()
								.getCircularVer()
								.getCircularVerId()
								.equals(this.getManagedBean().getQt().getCircularVer().getCtCircular()
										.getCurrentCircularVerId().getCircularVerId())) { 
//						log.info("=======qtCircularQTNUMBER========"+this.getManagedBean().getQt().getQtNo());
						CircularDetailReqDTO cirreqDTO = new CircularDetailReqDTO();
						cirreqDTO.setCircularVerId(this.getManagedBean().getQt().getCircularVer().getCtCircular()
							.getCurrentCircularVerId().getCircularVerId());
						cirreqDTO.setPanelItemTypeGroup(true);
						cirreqDTO.setGroupItemId(this.getManagedBean().getQt()
								.getItemGroup().getPanelItemGroupId());
						ICircularDetailSO	circularDetailSO = (ICircularDetailSO) EpEjbBusinessDelegate
								.getInstance().getServiceObject(
										ModuleCodeConst.CONTRACT_MANAGEMENT,
										ICircularDetailSO.class);
						CircularDetailResDTO cirresDTO = circularDetailSO
							.getPanelItemsByCircularVerId(cirreqDTO);
					if (CollectionUtils.isNotEmpty(cirresDTO.getPanelItemList())) {
						//Long panelItemId = cirresDTO.getPanelItemList().get(0).getPanelItemId();
						// log.info("=======qtCircularQTNUMBER========"+this.getManagedBean().getQt().getQtNo()+"=======qtCircularVersionId========"+panelItemId);
						supplierReqDTO.setPanelItemId(cirresDTO.getPanelItemList().get(0).getPanelItemId());
					}
				}
				// #34220-end
					supplierResDTO = supplierSo
							.retrievePanelSuppliersByPanelItem(supplierReqDTO);
					
					this.getManagedBean().setCtZoneSupplierList(
							new ArrayList<CtPanelSupplier>());
					if (null != supplierResDTO.getCtPanelSuppliers()) {
						this.getManagedBean().setTotalSuppliers(
								supplierResDTO.getCtPanelSuppliers().size());
						for(CtPanelSupplier paelSupplier:supplierResDTO.getCtPanelSuppliers()){
							paelSupplier.setSmMofAccount(retriveMofAccountBySupplier(paelSupplier.getSupplier()));
						}
						this.getManagedBean().setCtZoneSupplierList(
								supplierResDTO.getCtPanelSuppliers());
					}
				}
			//}
		} finally {
			//panelSupplierSO=null;
		}
	}

	/**
	 * Retrieve schedule tab dtl.
	 */
	public void retrieveScheduleTabDtl() {
		debugLogger("retrieveScheduleTabDtl");

		BriefingZoneBean briefingList = this.getManagedBean().getBsvDtlBean();
		if (briefingList == null) {
			retrieveBriefingSitevisitTabDtl();
		}

		if (this.getManagedBean().getScQtContactPersons() == null) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				// reqDTO.setQtId(getManagedBean().getQt().getQtId());
				reqDTO.setQtdetail(this.getManagedBean().getQt());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTScheduleDetails(reqDTO);
				ScQt scQt = resDTO.getQtDetail();
				this.getManagedBean().setQt(scQt);
				// Set New publish date for update publish date. To solve issue on publish date reset back to original time during primefaces calender update phase.
				scQt.setNewPublishDate(scQt.getPublishDate());
				// Set New closing date for update closing date.
				scQt.setNewClosingDate(scQt.getClosingDate());
				ISearchResultBean<ScQtContactPerson> contactPersons = resDTO
						.getContactPersons();
				List<ScQtContactPerson> qtContactPerson = contactPersons
						.getResultList();
				if (qtContactPerson == null || qtContactPerson.isEmpty()) {
					ScQtContactPerson contactPerson = new ScQtContactPerson();
					PmUser user = new FacesScopes().getUserSessionBean();
					contactPerson.setPhoneNo(user.getPhoneNo());
					contactPerson.setUser(user);
					contactPerson.setEmail(user.getEmail());
					contactPerson.setFaxNo(user.getFaxNo());
					contactPerson
							.setOfficerType(SourcingConstants.OFFICER_TYPE_QT);
					contactPerson.setName(user.getUserName());
					contactPerson.setScQt(this.getManagedBean().getQt());
					qtContactPerson.add(contactPerson);					
				}

				this.getManagedBean().setScQtContactPersons(qtContactPerson);
				// 25037 - start
				getDoaaRequestStatus();
				if (this.getManagedBean().isMaintanceFlow() && StringUtils.isEmpty(this.getManagedBean().getTaskName())) {//25037 - note 497
					prepareDeskofficerAssistantRequest(scQt, resDTO.getDeskOfficers().getResultList());
				} else {
					this.getManagedBean().setPmuserContactpersonList(resDTO.getDeskOfficers().getResultList());
				}
				// 25037 - end
				QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
				if (this.getManagedBean().getQt().isSampleReq()
						&& (null == this.getManagedBean()
								.getScQtSampleSubmission() && isEmptyOrNull(this
								.getManagedBean().getScQtSampleSubmissionList()))) {
					if (this.getManagedBean().getQt().isZonal()) {
						this.getManagedBean().setScQtSampleSubmissionList(
								resDTO.getSampleSubmissionResultBean());
					} else {
						this.getManagedBean().setScQtSampleSubmission(
								resDTO.getSampleSubmissionResultBean().get(0));
						if (null != this.getManagedBean()
								.getScQtSampleSubmission()) {
							if (null == this.getManagedBean()
									.getScQtSampleSubmissionList()) {
								this.getManagedBean()
										.setScQtSampleSubmissionList(
												new ArrayList<ScQtSampleSubmission>());
							}
							this.getManagedBean()
									.getScQtSampleSubmissionList()
									.add(this.getManagedBean()
											.getScQtSampleSubmission());
						}
						if (!getManagedBean().isSubmit()) {
							backingBean.retrievePmStateList();
						}
					}
				}

				backingBean.getDeskOfficerAdminAssistant();
			} catch (Exception e) {
				ScQt scQt = this.getManagedBean().getQt();
				String qtNo = scQt == null ? "" : scQt.getQtNo();
				SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION,
						ParameterConst.DOCTYPE_QUOTATION, qtNo, "qtDetailBackingBean.retrieveScheduleTabDtl",
						getUserIdSessionFromLoginId(), e);
			} finally {
				detailSO = null;
			}
		}
		if (this.getManagedBean().getQt().isSampleReq()) {
			QTScheduleDetailBackingBean backingBean = new QTScheduleDetailBackingBean();
			if (!getManagedBean().isSubmit()) {
				backingBean.retrievePmStateList();
			}
		}
	}

	/**
	 * Retrieve deduction payment tab dtl.
	 */
	public void retrieveDeductionPaymentTabDtl() {
		
		ScQt scQt = getManagedBean().getQt();
		debugLogger("retrieveDeductionPaymentTabDtl");
		if (this.getManagedBean().getScQtDeductionList() == null || this.getManagedBean().getScQtDeductionList().isEmpty()) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				QuationTenderResDTO resDTO = detailSO
						.retriveQTDeductionPaymentDetails(reqDTO);
				List<ScQtDeduction> deductionList = resDTO
						.getDeductionResultBean().getResultList();

				// 25635&31402-start
				if (null != this.getManagedBean().getRemoveDeductions()
						&& CollectionUtils.isNotEmpty(this.getManagedBean().getRemoveDeductions())) {

				} else {
					// 25635&31402 -end-
					this.getManagedBean().setScQtDeductionList(deductionList);
					this.getManagedBean().getQt().setScQtDeductions(deductionList);

					getManagedBean().setSelectedScQtDeduction(null);
				} // 25635, 31402
			} finally {
				detailSO = null;
			}
		}
	}

	/**
	 * Retrieve document tab dtl.
	 */
	public void retrieveDocumentTabDtl() {
		debugLogger("retrieveDocumentTabDtl");
		IQTDocumentSO qtDocumentSO;
		if (getManagedBean().getScQtDocReportList() == null) {
			QTDocumentResDTO resDTO = new QTDocumentResDTO();
			try {
				qtDocumentSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDocumentSO.class);
				QTDocumentReqDTO reqDTO = new QTDocumentReqDTO();
				reqDTO.setScQt(getManagedBean().getQt());
				reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				resDTO = qtDocumentSO.retriveQTDocDetails(reqDTO);
				if (resDTO.getQtDocReports() != null
						&& !resDTO.getQtDocReports().isEmpty()) {
					this.getManagedBean().setScQtDocReportList(
							resDTO.getQtDocReports());
				}
			} finally {
				qtDocumentSO = null;
				resDTO=null;
			}
		}
	}

	/**
	 * Retrieve activities tab dtl.
	 */
	public void retrieveActivitiesTabDtl() {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("retrieveActivitiesTabDtl");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setUserLocale(new FacesScopes().getUserLocale());
			// reqDTO.setQtId(getManagedBean().getQt().getQtId());
			reqDTO.setQtdetail(scQt);
			QuationTenderResDTO resDTO = detailSO.retrieveQTActivities(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setQtActivityBeans(
						resDTO.getQtActivityBeans());
			}
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Retrieve publication approval tab dtl.
	 */
	public void retrievePublicationApprovalTabDtl() {
		debugLogger("retrievePublicationApprovalTabDtl");
		IQTDetailSO detailSO;
		DualListModel<ScQtApproverSettings> dualListModel = null;
		try {
			ScQt scQt = getManagedBean().getQt();
			ScQtApproval publicationApproval = this.getManagedBean().getPublicationApproval();
			
			if (null == publicationApproval) {
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_PTJ);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalDtlsByQtIdNType(reqDTO);
				publicationApproval = resDTO.getQtApproval();
				if (publicationApproval == null) {
					publicationApproval = new ScQtApproval();
					publicationApproval
							.setScQtApprovalRemarks(initializeRemarks(
									publicationApproval, scQt
											.getCurrentWorkFlowStatus(),
									publicationApproval
											.getScQtApprovalRemarks(),
									ScQtApprovalRemark.class));
					publicationApproval.setScQtApprovalAttachments(null);
					publicationApproval.setScQt(scQt);
					publicationApproval.setApprovalCategory(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_CATEGORY, 
							ParameterConst.APPROVAL_CATEGORY_PTJ));

				} 
				
				if (CollectionUtils.isEmpty(publicationApproval.getScQtApprovalDtls())) {
					List<ScQtApprovalRequest> approvalDtls = new ArrayList<ScQtApprovalRequest>();
					ScQtApprovalRequest scQtApprovalDtl = new ScQtApprovalRequest();
					scQtApprovalDtl.setApprovalType(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, 
							ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL));
					scQtApprovalDtl.setScQtApproval(publicationApproval);
					approvalDtls.add(scQtApprovalDtl);
					publicationApproval.setScQtApprovalDtls(approvalDtls);	
				}
				publicationApproval.setDeleted(false);
				List<ScQtApproval> toBeApprovalRemoveList = this.getManagedBean().getToBeApprovalRemoveList();
				if (CollectionUtils.isNotEmpty(toBeApprovalRemoveList)) {
					toBeApprovalRemoveList.remove(publicationApproval);	
				}
				
				this.getManagedBean().setPublicationApproval(
						publicationApproval);
			} 
			
			// set up approval pick list
			//#26146 - start -
//			dualListModel = approverSettingDtls(
//					RoleCode.PUBLICATION_APPROVER, scQt.getPmOrgProfile()
//							.getOrgProfileId(), publicationApproval);
			Long[] orgIds = { scQt.getOwnerOrgProfile().getOrgProfileId(), scQt.getPmOrgProfile().getOrgProfileId() };
			dualListModel = ApproverSettings.getDetails(RoleCode.PUBLICATION_APPROVER,orgIds, publicationApproval);
			//#26146 - end -
			
			this.getManagedBean().setPublicationApprovals(dualListModel);
			
			// set up remark.
			publicationApproval
			.setScQtApprovalRemarks(initializeRemarks(
					publicationApproval, scQt
							.getCurrentWorkFlowStatus(),
					publicationApproval
							.getScQtApprovalRemarks(),
					ScQtApprovalRemark.class));
		} finally {
			detailSO = null;
		}
	}

	
	/**
	 * The Class QTPagingDataModel.
	 */
	private class QTPagingDataModel extends PagingDataModel<ScQt> {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The qtn tender search bean. */
		private QtnTenderSearchBean qtnTenderSearchBean;

		/**
		 * Instantiates a new QT paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param qtnTenderSearchBean
		 *            the qtn tender search bean
		 */
		public QTPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId, false);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}

		/**
		 * retrieve list of ScQt by params.
		 * 
		 * @param searchBean
		 *            the search bean
		 * @return the i search result bean
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScQt> pageChangeListener(
				ISearchBean<ScQt> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			IQTDetailSO detailSO;
			IQTSuppFinalizationSO sfDetailSO; // #Enhancement 9 CRs
			try {
				// #30451-Start.
				boolean isBidding = false;
				
				if ((isQTMaintenanceScreen() || isQTSearchScreen())
						&& ParameterConst.DOCTYPE_BIDDING.equals(qtnTenderSearchBean.getDocType())) {
					isBidding = true;
					
					qtnTenderSearchBean.setDocType(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
				}
				// #30451-End.

				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				sfDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class); // #Enhancement 9 CRs
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtnTenderSearchBean(qtnTenderSearchBean.toQTSearchBean());

				// #30451-Start.
				populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, searchBean, isBidding);
				// #30451-End.

				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				// #19336 - start		
				//workflow status is_current for SF
				if(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(qtnTenderSearchBean.getDocType())){
					if(resDTO.isSuccessful() && resDTO.getQtResultBean() != null && !CollectionUtils.isEmpty(resDTO.getQtResultBean().getResultList())){
						SearchResultBean<ScQt> qtResultBean = (SearchResultBean<ScQt>)resDTO.getQtResultBean();
						SuppFinalizationResDTO sfResDTO = sfDetailSO.setQtFinalizationCurrentStatus(qtResultBean.getResultList());
						if(sfResDTO.isSuccessful()){
							qtResultBean.setResultList(sfResDTO.getQtList());
							
							for(int i = 0 ; !CollectionUtils.isEmpty(qtResultBean.getResultList()) && i< qtResultBean.getResultList().size(); i++){
								qtResultBean.getResultList().get(i).setStatusDesc(getStatusDescById(sfResDTO.getQtList().get(i).getFinalization().getCurrentStatus().getPmStatus().getStatusId()));								
							}
						}
					}
				}
				// #19336 - end

				// set search result doc type for navigate to diff page based on diff doc type selected.
				// #30451-Start.
				if (isBidding) {
					qtnTenderSearchBean.setDocType(ParameterConst.DOCTYPE_BIDDING);
					qtnTenderSearchBean.setResultDocType(ParameterConst.DOCTYPE_BIDDING);
					
					getManagedBean().setBdSfFlow(true);
				} else {
					getManagedBean().setBdSfFlow(false);					
					qtnTenderSearchBean.setResultDocType(qtnTenderSearchBean.getDocType());
				}
				// #30451-End.
				
				
				// ***17376#66 - start
				if(resDTO.getQtResultBean()!=null && resDTO.getQtResultBean().getResultList()!=null){
					for(int i = 0; i< resDTO.getQtResultBean().getResultList().size(); i++){
						resDTO.getQtResultBean().getResultList().get(i).setActionRender(actionRender(resDTO.getQtResultBean().getResultList().get(i)));
					}
				}
				// ***17376#66 - end
				
				return resDTO.getQtResultBean();
				// #Enhancement 9 CRs - start
			}catch(Exception e){
				handleException(new EpException(MessageConst.ERROR_000001));
			}			
			finally {
				detailSO = null;
				sfDetailSO = null;
			}
			return null;
		}
	}
	
	
	
	private class LOAPagingDataModel extends PagingDataModel<ScLoaForQt>{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The qtn tender search bean. */
		private QtnTenderSearchBean qtnTenderSearchBean;
		
		/**
		 * Instantiates a new Loa paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param qtnTenderSearchBean
		 *            the qtn tender search bean
		 */
		public LOAPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId, false);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}
		
		
		/**
		 * retrieve list of ScLoaForQt by params.
		 * 
		 * @param searchBean
		 *            the search bean
		 * @return the i search result bean
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScLoaForQt> pageChangeListener(
				ISearchBean<ScLoaForQt> searchBean) {
			IQTDetailSO qtDetailSO;
			try{
				qtDetailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTDetailSO.class);
				
				// #25201. start filtering the status of LOI/LOA
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(qtnTenderSearchBean.getDocType())
						|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(qtnTenderSearchBean.getDocType())	){					
						qtnTenderSearchBean.setQtStatusForQt(false);
					}
				
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtnTenderSearchBean(qtnTenderSearchBean.toQTSearchBean());
				reqDTO.putToArgs(MapKeyEnum.isMofSupplier, isMofSupplier());
				reqDTO.putToArgs(MapKeyEnum.isDeskOfficer, isDeskOfficer());
				reqDTO.putToArgs(MapKeyEnum.isQtMaintenanceScreen, isQTMaintenanceScreen() || isQTSearchScreen());
				
				if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isMofSupplier)){
					ISupplierSearchSO supplierSearchSO;
					try {
						supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
	
						SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
						supplierSearchReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
						List<SmSupplier> smSuppliers = supplierSearchSO
								.retriveSupplierProfiles(supplierSearchReqDTO)
								.getResultBean().getResultList();
						
						if (CollectionUtils.isNotEmpty(smSuppliers)) {
							List<Long> supplierIds = new ArrayList<Long>();
							for (SmSupplier smSupplier : smSuppliers) {
								supplierIds.add(smSupplier.getSupplierId());
							}
							reqDTO.setSupplierIds(supplierIds);
							reqDTO.putToArgs(MapKeyEnum.supplierIds, supplierIds);
						}
						
						/*Bug #27412*/
						reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
						reqDTO.putToArgs(MapKeyEnum.userID, new FacesScopes().getUserSessionBean().getUserId());
						/*Bug #27412*/
					}
					finally{
						supplierSearchSO = null;
					}
				}
				else{
					if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isDeskOfficer)){
						// Filter By - actor user id
						reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
						reqDTO.putToArgs(MapKeyEnum.userID, new FacesScopes().getUserSessionBean().getUserId());
						
						// Only DO Actor with edit access right. 
						// Otherwise (Audit Search), user can view QT based on user roles match with actor.
						if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isQtMaintenanceScreen)){
							reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
						}
					}
				}
				
				populateLoaForQtSearchCriteria(reqDTO, qtnTenderSearchBean, searchBean);
				populateScQtFilter(reqDTO, searchBean,"scLoiLoa.scQt");
				if(reqDTO.getLoaForQtSearchBean() != null){
					reqDTO.getLoaForQtSearchBean().getSortColumns().add(new SearchOrderBean("loaId", false));
				}
				QuationTenderResDTO resDTO = qtDetailSO
						.retriveLoaListBySearchCriteria(reqDTO);
				
				// set search result doc type for navigate to diff page based on diff doc type selected.
				qtnTenderSearchBean.setResultDocType(qtnTenderSearchBean.getDocType());
				
				return resDTO.getLoaForQtResultBean();
				
			}
			finally{
				qtDetailSO = null;
			}
			
		}
	}
	
	private class LOIPagingDataModel extends PagingDataModel<ScLoiForQt>{
		
		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The qtn tender search bean. */
		private QtnTenderSearchBean qtnTenderSearchBean;
		
		/**
		 * Instantiates a new Loi paging data model.
		 * 
		 * @param componentId
		 *            the component id
		 * @param qtnTenderSearchBean
		 *            the qtn tender search bean
		 */
		public LOIPagingDataModel(String componentId,
				QtnTenderSearchBean qtnTenderSearchBean) {
			super(componentId, false);
			this.qtnTenderSearchBean = qtnTenderSearchBean;
		}
		
		
		/**
		 * retrieve list of ScLoi by params.
		 * 
		 * @param searchBean
		 *            the search bean
		 * @return the i search result bean
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<ScLoiForQt> pageChangeListener(
				ISearchBean<ScLoiForQt> searchBean) {
			IQTDetailSO detailSO;
			try {
				detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTDetailSO.class);
				// ***25201
				// #25201. start filtering the status of LOI/LOA
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(qtnTenderSearchBean.getDocType())
						|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(qtnTenderSearchBean.getDocType())	){					
						qtnTenderSearchBean.setQtStatusForQt(false);
					}
				
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtnTenderSearchBean(qtnTenderSearchBean.toQTSearchBean());
				reqDTO.putToArgs(MapKeyEnum.isMofSupplier, isMofSupplier());
				reqDTO.putToArgs(MapKeyEnum.isDeskOfficer, isDeskOfficer());
				reqDTO.putToArgs(MapKeyEnum.isQtMaintenanceScreen, isQTMaintenanceScreen() || isQTSearchScreen());
				if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isMofSupplier)){
					ISupplierSearchSO supplierSearchSO;
					try {
						supplierSearchSO = BusinessDelegate.getInstance().getServiceObject(
								ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierSearchSO.class);
	
						SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
						supplierSearchReqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
						List<SmSupplier> smSuppliers = supplierSearchSO
								.retriveSupplierProfiles(supplierSearchReqDTO)
								.getResultBean().getResultList();
						
						if (CollectionUtils.isNotEmpty(smSuppliers)) {
							List<Long> supplierIds = new ArrayList<Long>();
							for (SmSupplier smSupplier : smSuppliers) {
								supplierIds.add(smSupplier.getSupplierId());
							}
							reqDTO.setSupplierIds(supplierIds);
							reqDTO.putToArgs(MapKeyEnum.supplierIds, supplierIds);
						}
						/*Bug #27412*/
						reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
						reqDTO.putToArgs(MapKeyEnum.userID, new FacesScopes().getUserSessionBean().getUserId());
						/*Bug #27412*/
					}
					finally{
						supplierSearchSO = null;
					}
				}
				else{
					if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isDeskOfficer)){
						// Filter By - actor user id
						reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
						reqDTO.putToArgs(MapKeyEnum.userID, new FacesScopes().getUserSessionBean().getUserId());
						
						// Only DO Actor with edit access right. 
						// Otherwise (Audit Search), user can view QT based on user roles match with actor.
						if((Boolean)reqDTO.getFromArgs(MapKeyEnum.isQtMaintenanceScreen)){
							reqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
						}
					}
				}
				
				populateQTLOISearchCriteria(reqDTO, qtnTenderSearchBean, searchBean);
				populateScQtFilter(reqDTO, searchBean, "scLoiLoa.scQt");
				
				QuationTenderResDTO resDTO = detailSO
						.retriveLoiListBySearchCriteria(reqDTO);
				
				// set search result doc type for navigate to diff page based on diff doc type selected.
				qtnTenderSearchBean.setResultDocType(qtnTenderSearchBean.getDocType());
								
				return resDTO.getLoiForQtResultBean();
			} 
			catch(Exception ex){
				ex.printStackTrace();
				return null;
			}
			finally {
				detailSO = null;
			}
		}
	}
	// #Enhancement 9 CRs - end

	/**
	 * Retrieve parameter desc by type.
	 * 
	 * @param type
	 *            the type
	 * @return the list
	 */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		//this.getManagedBean().getQtnTenderSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION); //17376-153
		this.getManagedBean().getQtnTenderSearchBean().setDocNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
		getDefaultPtjAndMinistryValues();
	}

	/**
	 * Update qt dtls.
	 */
	private void updateQtDtls() {
		List<TabBean> commiteTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
		List<ScQtCommittee> qtCommittes = null;
		if (null != commiteTabs) {
			qtCommittes = new ArrayList<ScQtCommittee>();
			for (TabBean tabBean : commiteTabs) {
				if (!SourcingConstants.COMMITTE_TYPE_FINALIZATION
						.equals(tabBean.getTabType())) {
					qtCommittes.add(tabBean.getScQtCommittee());
				}

			}
		}
		/*
		 * List<ScQtCommittee> qtCommittees = new ArrayList<ScQtCommittee>();
		 * qtCommittees.add(this.getManagedBean().getScQtCommittee());
		 */
		this.getManagedBean().getQt().setScQtCommittees(qtCommittes);
	}

	/**
	 * Search qt back action.
	 * 
	 * @return the string
	 */
	public String searchQtBackAction() {
		debugLogger("searchQtBackAction");
		return "/xhtml/sc/quatationtender/quatationtendersearch.xhtml?faces-redirect=true";
	}

	/**
	 * Retrieve quation tender details.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param docType
	 *            the doc type
	 */
	public void retrieveQuationTenderDetails(Long qtId) {
		
		if (null != qtId) {			
			// #21695 - Farid Start 30/7/2020
			IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			List<String> authoriztionList = new ArrayList<String>();
		
			if (null != new FacesScopes().getUserSessionBean().getUserId()) {
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				reqDTO.setLanguageCode(ReportConst.ENG_LANG);
				
				UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
				List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
				
				if (CollectionUtils.isNotEmpty(roleDesc)) {
					for (PmRoleDesc pmRoleDesc : roleDesc) {
						pmRoleDesc.getPmRole().getRoleCode();
					
						if (!authoriztionList.contains(pmRoleDesc.getPmRole().getRoleCode())) {
							authoriztionList.add(pmRoleDesc.getPmRole().getRoleCode());
						}

						RoleCode[] r = { RoleCode.SC_SECRETARY };
						
						if (userHasRole(r)) {
							getManagedBean().setSecretaryValidate(true);
						}
						;
					}
				}
			}
			// #21695 - Farid End 30/7/2020
			
			// this.getManagedBean().setActiveIndex(0);
			IQTDetailSO QtDetailsSO;
			try {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean()
						.isMaintanceFlow());
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setBdgDtlsReq(true);
				QuationTenderResDTO tenderResDTO = null;
				ScQt scQt = this.getManagedBean().getQt();
				if (null == scQt
						|| (scQt.isZonal()
						/* && !scQt.isPanel() */
						&& null != this.getManagedBean().getZoneGeneralTrees() && this
								.getManagedBean().getZoneGeneralTrees()
								.isEmpty())) {
					tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);

					if (tenderResDTO.getQtDetail() != null) {
						// #28784 -start-
						boolean demoRequired = false;
						String evalType = null;

						if (scQt != null) {
							demoRequired = scQt.isDemoRequired();
							evalType = scQt.getEvaluationType();
						}

						scQt = tenderResDTO.getQtDetail();
						
						//#28784 - Demo WIP
						boolean isQtNewVersion = validateSupportedTask(scQt.getQtNo(), CompositeValidityConst.PM_COMPOSITE_VALIDITY);
						this.getManagedBean().setNewVersion(isQtNewVersion);

						getManagedBean().setQt(scQt);

						//#28784 - Include Demo WIP
						if (demoRequired && this.getManagedBean().isNewVersion()){
							getManagedBean().getQt().setDemoRequired(demoRequired);
							getManagedBean().getQt().setEvaluationType(evalType);
						}
						// #28784 -end-

						getManagedBean().setQt(scQt);
						this.getManagedBean().setPanel(scQt.isPanel());
						Long currentStatus = this.getManagedBean().getQt()
								.getQtStatus().getStatusId();
						this.getManagedBean().setCurrentStatusId(currentStatus);
						getManagedBean().getCtContract().setContractNo(
								this.getManagedBean().getQt()
										.getExistingContractNo());

						// this.getManagedBean().setContractType(scQt.getContractType());
						if (this.getManagedBean().getQt().isZonal()) {							
							if (null != this.getManagedBean().getQt()
									.getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt()
												.getZoneLocationLevel()
												.getParameterCode());
							}

							// Populate zone detail into tree node
							if (scQt.getScQtSupplierCriteria() != null &&
									scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
								List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls();
								populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
								// setting into zones bean to validate zones while adding 
								if (CollectionUtils.isNotEmpty(specZoneDtls)) {
									List<ZonesBean> zoneBeanLst = new ArrayList<ZonesBean>();
									for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
										ZonesBean bean = new ZonesBean();
										bean.setTplSpecZoneDtl(specZoneDtl);
										zoneBeanLst.add(bean);
									}
									this.getManagedBean().setBeans(zoneBeanLst);
								}
							}
						}
						//Setting special approval info for login user
						if (scQt.getObtainSpecialApproval() != null && !scQt.getObtainSpecialApproval()) {
							this.getManagedBean().setPtjSpecialApproval(
									getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));
											
						} else {
							this.getManagedBean().setPtjSpecialApproval(scQt.getPtjSpecialApproval());
						}

						//#28784 - Include Demo WIP
						if(this.getManagedBean().isNewVersion()){
							checkDemoPresentationByStatus();	
						}
					}

					this.getManagedBean().setAmendMentBean(
							tenderResDTO.getAmendMentBean());
					if (this.getManagedBean().getGosodos() == null) {
						this.getManagedBean().setGosodos(
								tenderResDTO.getBudgetDtls());
					}
					if (null != getManagedBean().getQt()
							&& null == getManagedBean().getQt()
									.getProcurementTypeCatId()) {
						getManagedBean().getQt().setProcurementTypeCatId(
								new PmParameter());
					}
					

				} else {
					if (scQt.getEstimatedAmount() == null) {
						BigDecimal estimatedAmount = QtDetailsSO
								.getdecryptedEstAmount(qtId);
						scQt.setEstimatedAmount(estimatedAmount);
						scQt.setDecryptedEstAmt(estimatedAmount);
					}
					
					if (scQt.getTmpExpectedIntendedPrice() == null) {
						BigDecimal tmpExpectedIntendedPrice = QtDetailsSO
								.getDecryptedExpectedIntendedPrice(qtId);
						scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
						scQt.setDecExpectedIntendedPrice(tmpExpectedIntendedPrice);
					}
					// this.getManagedBean().setContractType(scQt.getContractType());

					// #28784 -start-
					if (this.getManagedBean().getQt().isDemoRequired()) {
						this.getManagedBean().setDemoAction(true);
						this.getManagedBean().setDemoType(true);
					}

					checkDemoPresentationByStatus();
					// #28784 -end-
				}
			} finally {
				QtDetailsSO = null;
			}
			debugLogger("end of retrieveQuationTenderDetails");
		}
	}
	
	/*
	 * Set evaluation Type and demo required radio button to show or disabled, 
	 * #28784
	 */
	public void checkDemoPresentationByStatus() {
		if (this.getManagedBean().isNewVersion()) {
			ScQt scQt = this.getManagedBean().getQt();
			Long status = this.getManagedBean().getQt().getQtStatus() != null ? this.getManagedBean().getQt()
					.getQtStatus().getStatusId() : null;
			boolean demoRequired = this.getManagedBean().getQt().isDemoRequired();
			String taskName = this.getManagedBean().getTaskName() == null ? "" : this.getManagedBean().getTaskName();
			ScWorkFlowStatus specMainworkflow = this.getManagedBean().getSpecMaintenanceWorkFlowStatus();
			Long workflowStatusId = ParameterConst.PM_PARAMETERID_ZERO;
			this.getManagedBean().setDemoEvalDisabled(false);
			this.getManagedBean().setDemoSpecialCase(false);
			this.getManagedBean().setDemoDisabled(false);
			if (specMainworkflow != null && specMainworkflow.getPmStatus() != null) {
				workflowStatusId = specMainworkflow.getPmStatus().getStatusId();
			}
			String role = getUserRoleCode();
			if (demoRequired) {
				this.getManagedBean().getQt().setDemoRequired(true);
			} else {
				this.getManagedBean().getQt().setDemoRequired(false);
			}
			
			boolean qtStatus = !workflowStatusId.equals(QTStatusConst.SE_PENDING_ENDORSMENT)
					&& !workflowStatusId.equals(QTStatusConst.SE_PENDING_SIGNOFF) && !workflowStatusId
					.equals(QTStatusConst.SE_PENDING_REVISION);
			
			if (scQt.getEvaluationType() != null
					&& scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)
					&& null != status && !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {

				if (null != status && (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT))
						&& !this.getManagedBean().isQtSpecMaintanance()) {
					this.getManagedBean().setDemoDisabled(false);
				}

				if (null != status
						&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
								&& !this.getManagedBean().isQtSpecMaintanance() && !this.getManagedBean()
								.isMaintanceFlow()) && demoRequired) {
					this.getManagedBean().setDemoSpecialCase(true);
					this.getManagedBean().setDemoEvalDisabled(false);
				}

				if (null != status
						&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
								&& !this.getManagedBean().isQtSpecMaintanance() && !this.getManagedBean()
								.isMaintanceFlow()) && !demoRequired) {
					this.getManagedBean().setDemoSpecialCase(true);
					this.getManagedBean().setDemoEvalDisabled(true);
				}

				if (this.getManagedBean().isMaintanceFlow()
						&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) && !demoRequired) {
					this.getManagedBean().setDemoDisabled(false);
					this.getManagedBean().setDemoEvalDisabled(false);
				}

				boolean checkStatus = status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
						|| status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
						|| status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)
						|| status.equals(QTStatusConst.QT_PENDING_DOCUMENT_REVISION)
						|| status.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)
						|| status.equals(QTStatusConst.QT_REVISION_PUBLICATION);
				
				boolean workflowStat = !workflowStatusId.equals(QTStatusConst.SE_PENDING_ENDORSMENT)
						&& !workflowStatusId.equals(QTStatusConst.SE_PENDING_SIGNOFF) && !workflowStatusId
						.equals(QTStatusConst.SE_PENDING_REVISION);

				if (null != status && checkStatus && workflowStat && getManagedBean().isMaintanceFlow() && demoRequired
						&& !taskName.equalsIgnoreCase(SCProcessNameConst.ENDORSE_SCORING)
						&& !taskName.equalsIgnoreCase(SCProcessNameConst.SIGNOFF_SCORING) && role != null
						&& role.equals(RoleCode.SC_SECRETARY.value())) {// #28784
					this.getManagedBean().setDemoEvalDisabled(false);
					this.getManagedBean().setDemoSpecialCase(true);
					this.getManagedBean().setDemoDisabled(true);
				}

				boolean newStatus = status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
						|| status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
						|| status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)
						|| status.equals(QTStatusConst.QT_REVISION_PUBLICATION) || status
							.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION);
				
				boolean newWorkflow = !workflowStatusId.equals(QTStatusConst.SE_PENDING_ENDORSMENT)
						&& !workflowStatusId.equals(QTStatusConst.SE_PENDING_SIGNOFF) && !workflowStatusId
						.equals(QTStatusConst.SE_PENDING_REVISION);
				 
				// #28784 note #507 - start
				if (null != status && newStatus && this.getManagedBean().isMaintanceFlow() && !demoRequired
						&& role != null && role.equals(RoleCode.SC_SECRETARY.value()) && newWorkflow) {
					this.getManagedBean().setDemoEvalDisabled(true);
					this.getManagedBean().setDemoSpecialCase(false);
					this.getManagedBean().setDemoDisabled(true);
				}

				boolean stdStatus = status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
						|| status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
						|| status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)
						|| status.equals(QTStatusConst.QT_REVISION_PUBLICATION) || status
							.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION);
				
				if (null != status && stdStatus && workflowStatusId.equals(QTStatusConst.SE_PENDING_REVISION)
						&& this.getManagedBean().isMaintanceFlow() && !isSpecCommittee() && role != null
						&& role.equals(RoleCode.SC_SECRETARY.value())) {
					this.getManagedBean().setDemoEvalDisabled(true);
					this.getManagedBean().setDemoSpecialCase(false);
					this.getManagedBean().setDemoDisabled(true);
				}
				// #28784 note #507 - end

				if (null != status
						&& (status.equals(QTStatusConst.QT_REVISION_PUBLICATION) || status
								.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)) && role != null
						&& (role.equals(RoleCode.RN_APPROVER.value()) || role.equals(RoleCode.DESK_OFFICER.value()))) {
					this.getManagedBean().setDemoSpecialCase(true);
				}

				if (null != status && (!status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION))
						&& !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)) {
					this.getManagedBean().setDemoType(true);
					this.getManagedBean().setDemoAction(true);
				}

				if (null != status
						&& (status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) && role != null && role
								.equals(RoleCode.SC_SECRETARY.value()))) {
					this.getManagedBean().setDemoDisabled(true);
					this.getManagedBean().setDemoEvalDisabled(false);
				}

				if (null != status && status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)) {
					this.getManagedBean().setDemoType(false);
					this.getManagedBean().setDemoAction(false);
				}

			} else if (this.getManagedBean().isMaintanceFlow() && !demoRequired && null != status
					&& !status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION) && role != null
					&& role.equals(RoleCode.SC_SECRETARY.value())) {
				this.getManagedBean().setDemoDisabled(true);
				this.getManagedBean().setDemoEvalDisabled(true);
				this.getManagedBean().setDemoType(true);
				this.getManagedBean().setDemoAction(true);
			} else if (scQt.getEvaluationType() != null
					&& scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_ONE_TIER)
					&& !demoRequired && null != status && status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					&& role != null && role.equals(RoleCode.SC_SECRETARY.value())) {
				this.getManagedBean().setDemoDisabled(true);
				this.getManagedBean().setDemoEvalDisabled(true);
				this.getManagedBean().setDemoType(false);
				this.getManagedBean().setDemoAction(false);
			} else if (this.getManagedBean().isMaintanceFlow() && null != status
					&& status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) && !demoRequired
					&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION) && role != null
					&& role.equals(RoleCode.SC_SECRETARY.value()) && qtStatus) {
				this.getManagedBean().setDemoDisabled(true);
				this.getManagedBean().setDemoEvalDisabled(true);
			} else {
				this.getManagedBean().setDemoType(false);
				this.getManagedBean().setDemoAction(false);
			}
		}
	}
	// #28784 -end-

	private void populateZoneDetailIntoTreeNode(ScQt scQt, List<TplSpecZoneDtl> specZoneDtls) {
		List<TreeNode> zoneGeneralTrees = getManagedBean().getZoneGeneralTrees();
		
		this.getManagedBean().setCtzones(Boolean.TRUE);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		if (zoneGeneralTrees == null) {
			zoneGeneralTrees = new ArrayList<TreeNode>();
			getManagedBean().setZoneGeneralTrees(zoneGeneralTrees);
		} else {
			zoneGeneralTrees.clear();	
		}
		
		boolean zoneCoverageReadonly = enableFieldByStatusNFiledName(QTFieldConst.FIELD_QT_ZONE_COVERAGE);
		List<TreeNode> zoneSuppQualTrees = null;
		if (zoneCoverageReadonly) {
			zoneSuppQualTrees = getManagedBean().getZoneSuppQualTrees();
			if (zoneSuppQualTrees == null) {
				zoneSuppQualTrees = new ArrayList<TreeNode>();
				getManagedBean().setZoneSuppQualTrees(zoneSuppQualTrees);
			} else {
				zoneSuppQualTrees.clear();	
			}	
		} else {
			getManagedBean().setZoneSuppQualTrees(null);
		}
		
		//20200210@asyrafhaziq_Bug #27381&#26694  - Start
		//set  ZoneGeneral Trees Panel
		if (scQt.isZonal() &&  scQt.isPanel() ){
		  new CreateQtBackingBean().retrieveZoneGeneralTreesPanel(scQt, specZoneDtls);
		}
		//20200210@asyrafhaziq_Bug #27381&#26694  - End
		
		if (CollectionUtils.isNotEmpty(specZoneDtls)) {
			for (TplSpecZoneDtl zone : specZoneDtls) {
				if (CollectionUtils.isNotEmpty(zone.getTplSpecZoneLocs())) {
					List<TplSpecZoneLoc> specZoneLocs = new ArrayList<TplSpecZoneLoc>();
					
					for (TplSpecZoneLoc specLoc : zone.getTplSpecZoneLocs()) {
						if (specLoc.isZonalLocation()) {
							specZoneLocs.add(specLoc);
						}
					}
					//20200210@asyrafhaziq_Bug #27381&#26694  - Start
					if (scQt.isZonal() &&  scQt.isPanel() )
					{
						zoneGeneralTrees.add(
								LocationSingleton.getInstance().constuctTreeNodeWithDisableChild(
										zone, specZoneLocs));
						
						if (zoneSuppQualTrees != null) {
							zoneSuppQualTrees.add(
									LocationSingleton.getInstance().constuctTreeNodeWithDisableChild(
											zone, zone.getTplSpecZoneLocs()));}
				
					}else{
						zoneGeneralTrees.add(
								LocationSingleton.getInstance().constuctTreeNode(
										zone, specZoneLocs));
						
						if (zoneSuppQualTrees != null) {
							zoneSuppQualTrees.add(
									LocationSingleton.getInstance().constuctTreeNode(
											zone, zone.getTplSpecZoneLocs()));	
						}
					}
					//20200210@asyrafhaziq_Bug #27381&#26694  - End
				}
			}
		}
	}
	//20210325@asyrafhaziq_30518 - Start
	/**
	 * Gets the qt commite dtls by qt id.
	 * 
	 * @param qtId
	 *            the qt id
	 * @return the qt commite dtls by qt id
	 */
	private List <ScQtCommittee> getQtCommiteDtlsByQtId(Long qtId) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		reqDTO.setQtId(qtId);
		CommitteResDTO resDTO = null;
		List <ScQtCommittee> qtCommittes = null;
		ICommitteSO committeSO;
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommittes = resDTO.getQtCommittes();
		} finally {
			committeSO = null;
		}
		return qtCommittes;
	}
	//20210325@asyrafhaziq_30518 - End

	/**
	 * Gets the qt commite dtls by qt id n type.
	 * 
	 * @param commType
	 *            the comm type
	 * @param qtId
	 *            the qt id
	 * @return the qt commite dtls by qt id n type
	 */
	private ScQtCommittee getQtCommiteDtlsByQtIdNType(String commType, Long qtId) {
		IParameterSO parameterSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		ParameterResDTO parameterResDTO = null;
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
			if (null == qtCommitte) {
				if (ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER);

				} else if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);

				} else if (ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE);
				}

				parameterResDTO = parameterSO
						.retrieveParameterByTypeAndCode(parameterReqDTO);
				qtCommitte = new ScQtCommittee();
				qtCommitte.setScQt(this.getManagedBean().getQt());
				setUserDetails(qtCommitte);
				qtCommitte.setCommitteeActivities(null);
				qtCommitte.setQtCommitteRemarks(null);
				qtCommitte.setScQtMeetings(null);
				ScCommittee committee = new ScCommittee();
				committee.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_SUBMISSION));
				committee.setCommitteeType(parameterResDTO.getPmParameter());
				if (ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE
						.equalsIgnoreCase(commType)) {
					committee.setApprovalReq(true);
				}
				setUserDetails(committee);

				committee
						.setScCommitteeMembers(new ArrayList<ScCommitteeMember>());
				qtCommitte.setScCommittee(committee);
			}
		} finally {
			committeSO = null;
			parameterSO=null;
		}
		return qtCommitte;
	}

	/**
	 * Approver setting dtls.
	 * 
	 * @param code
	 *            the code
	 * @param approval
	 *            the approval
	 * @return the dual list model
	 */
	private DualListModel<ScQtApproverSettings> approverSettingDtls(
			RoleCode code, Long orgProfileId, ScQtApproval approval) {
		List<ScQtApproverSettings> availableApprovers = new ArrayList<ScQtApproverSettings>();
		List<ScQtApproverSettings> selectedApprovers = new ArrayList<ScQtApproverSettings>();
		IUserSO iUserSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO userReqDto = new UserReqDTO();
		userReqDto.setRoleCode(code.value());
		userReqDto.setOrgProfileId(orgProfileId);
		userReqDto.setWithProfileGroup(true);
		UserResDTO userResDTO = iUserSO
				.retrieveUserListWithProfileGroupUserByRole(userReqDto);
		List<PmUser> approvers = userResDTO.getUserList();
		// Remove Current DO from approver list
		removeDeskOfficer(approvers);
		if (null != approval) {
			if (null != approval.getApproverSettings()
					&& !approval.getApproverSettings().isEmpty()) {
				boolean isApproverExists;
				for (PmUser pmUser : approvers) {
					isApproverExists = false;
					for (ScQtApproverSettings scQtApproverSettings : approval
							.getApproverSettings()) {
						if (scQtApproverSettings.getPmUser().getUserId()
								.equals(pmUser.getUserId())) {
							scQtApproverSettings.setScQtApproval(approval);
							isApproverExists = true;
							break;
						}
					}
					if (!isApproverExists) {
						ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
						qtApproverSettings.setPmUser(pmUser);
						qtApproverSettings.setScQtApproval(approval);
						availableApprovers.add(qtApproverSettings);
					}
				}
				selectedApprovers = approval.getApproverSettings();
			} else {
				for (PmUser pmUser : approvers) {
					ScQtApproverSettings qtApproverSettings = new ScQtApproverSettings();
					qtApproverSettings.setPmUser(pmUser);
					qtApproverSettings.setScQtApproval(approval);
					availableApprovers.add(qtApproverSettings);
				}
				selectedApprovers = new ArrayList<ScQtApproverSettings>();
			}
		}
		return new DualListModel<ScQtApproverSettings>(availableApprovers,
				selectedApprovers);
	}

	/**
	 * Technical spec edit action.
	 * 
	 * @param checklist
	 *            the checklist
	 * @return the string
	 */
	public String technicalSpecEditAction(ScQtCheckList checklist) {
		this.getManagedBean().setQtCheckList(checklist);
		this.getManagedBean().setQtDoc(checklist.getScQtDoc());
		TemplateSpecBackingBean specBB = new TemplateSpecBackingBean();
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(checklist.getScQtDoc()
				.getTplSpecMaster());
		templateSpecBean
				.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		specBB.retrieveTemplateSpecMaster(templateSpecBean);
		return "/xhtml/sc/quatationtender/quotationTenderDetails/quotationTenderTechnicalTemplateMaintenance.xhtml?faces-redirect=true";
	}

	/**
	 * Gets the local goods radio values.
	 * 
	 * @param itemType
	 *            the item type
	 * @return the local goods radio values
	 */
	public SelectItem[] getLocalGoodsRadioValues(String itemType) {
		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCAL_GOODS);
		types.add(ParameterConst.IMPORTED_GOODS);
		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(itemType);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode()
							.equals(location)) {
						items[i++] = new SelectItem(paramDesc.getPmParameter()
								.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} finally {

		}
		return items;
	}

	/**
	 * Technical spec back action.
	 * 
	 * @return the string
	 */
	public String technicalSpecBackAction() {
		debugLogger("technicalSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Financial spec edit action.
	 * 
	 * @param checklist
	 *            the checklist
	 * @return the string
	 */
	public String financialSpecEditAction(ScQtCheckList checklist) {
		this.getManagedBean().setQtCheckList(checklist);
		this.getManagedBean().setQtDoc(checklist.getScQtDoc());

		ITemplateSpecSO templateSpecSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.COMMON_COMPONENT,
						ITemplateSpecSO.class);
		TemplateSpecReqDTO specReqDTO = new TemplateSpecReqDTO();
		specReqDTO.setSpecMaster(checklist.getScQtDoc().getTplSpecMaster());
		specReqDTO.setContentType(TemplateSpecConst.CONTENT_TYPE_QUESTION);
		TemplateSpecResDTO templateresDTO = templateSpecSO
				.getTemplateMaster(specReqDTO);
		checklist.getScQtDoc().setTplSpecMaster(templateresDTO.getSpecMaster());
		TemplateSpecBean templateSpecBean = getManagedBean()
				.getTemplateSpecBean();
		templateSpecBean.setSpecMaster(checklist.getScQtDoc()
				.getTplSpecMaster());
		if (this.getManagedBean().getQt().isZonal()
				&& this.getManagedBean().getQt().getQtType()
						.equals(ParameterConst.QUOTATION_TENDER_TYPE_OPEN)) {

			retrieveQuationTenderDetails(this.getManagedBean().getQt()
					.getQtId());
			ScQtSupplierCriteria supplierCriteria = this.getManagedBean()
					.getQt().getScQtSupplierCriteria();
			List<TplSpecZoneDtl> businessOwnerZone = this.getManagedBean()
					.getTemplateSpecZoneDtls();

			if (businessOwnerZone != null && !businessOwnerZone.isEmpty()) {
				// setUserDetails(qtSupplierCriteria.getQtZoneCriterias());

				for (TplSpecZoneDtl tplSpecZoneDtl : businessOwnerZone) {
					supplierCriteria.getQtZoneCriterias().setModified(true);
					tplSpecZoneDtl.setTplSpecZone(supplierCriteria
							.getQtZoneCriterias());

				}
				templateSpecBean.getSpecMaster().setTplSpecZone(
						supplierCriteria.getQtZoneCriterias());
				templateSpecBean.getSpecMaster().getTplSpecZone()
						.setTplSpecZoneDtls(businessOwnerZone);
			}

		}

		return "/xhtml/sc/quatationtender/quotationTenderDetails/quotationTenderFinancialTemplateMaintenance.xhtml?faces-redirect=true";
	}

	/**
	 * Financial spec back action.
	 * 
	 * @return the string
	 */
	public String financialSpecBackAction() {
		debugLogger("financialSpecBackAction");
		return "/xhtml/sc/quatationtender/quatationtendercreation.xhtml?faces-redirect=true";
	}

	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		if (isQTMaintenanceScreen() || isQTSearchScreen()) {
			recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
		}
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	/**
	 * Change secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changeSecretaryCommType(AjaxBehaviorEvent event) {
		debugLogger("changeSecretaryCommType");
		Long id = (Long) event.getComponent().getAttributes().get("value");
		if (id != null) {
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(id);
			ParameterResDTO parameterResDTO = getParameterSO()
					.retrieveParameterById(parameterReqDTO);

			getManagedBean().getQt().setFinalizationCommitte(
					parameterResDTO.getPmParameter());
			getManagedBean().getQt().setFinalizationSecratery(new PmUser());
		}
	}
	
	/**
	 * Change FC secretary user dropdown.
	 * 
	 * @param event
	 *            the event
	 */
	public void changeFCSecretaryUser(AjaxBehaviorEvent event) {
		debugLogger("changeFCSecretaryUser");
		Long userId = (Long) event.getComponent().getAttributes().get("value");
		this.getManagedBean().setChangesOnQTMaintenance(false);
		if (userId != null) {
			IUserSO userSO = null;
			try {
				userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				
				UserReqDTO resDTO = new UserReqDTO();
				resDTO.setUserId(userId);
				PmUser fcSecretaryUser = userSO.retrieveUserByUserId(resDTO)
						.getPmUser();
				
				getManagedBean().getQt().setFinalizationSecratery(fcSecretaryUser);
				this.getManagedBean().setChangesOnQTMaintenance(
						this.getManagedBean().isMaintanceFlow());
			} finally {
				userSO = null;
			}
		}
	}

	/**
	 * Retrieve pm user secratry dtl.
	 * 
	 * @param committeeType
	 *            the committee type
	 * @return the list
	 */
	public List<PmUser> retrievePmUserSecratryDtl(String committeeType) {
		this.getManagedBean().setPmUsersList(null);
		ICommitteSO committeSO;
		
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			// reqDTO.setPmOrgProfile(retrieveMinistryOrgProfile(getManagedBean()
			// .getQt().getPmOrgProfile().getOrgProfileId()));
			reqDTO.setPmOrgProfile(getManagedBean().getQt().getPmOrgProfile());
			reqDTO.setCommitteeType(committeeType);

			CommitteResDTO resDTO = committeSO.retrieveFcSecretaryUsersByCommType(reqDTO);
			this.getManagedBean().setPmUsersList(resDTO.getUserList());
			
			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}
	
	/**
	 * Committe tab change action.
	 */
	// Bug 32277 - start
	public void committeTabChangeAction(int selectedCommitteeTabIndex) {
		committeTabChangeAction(selectedCommitteeTabIndex, false);
	}

	public void committeTabChangeAction(int selectedCommitteeTabIndex, boolean committeeReadOnly) {
	// Bug 32277 - end
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}

		// this.getManagedBean().setToBeDelCommitteMemebers(null);
		this.getManagedBean().setSelectedTabIndex(selectedCommitteeTabIndex);
		// Bug 32277 - start
		List<TabBean> committeTabBeans = committeeReadOnly ? this.getManagedBean().getReadOnlyCommitteeTabBean() : this
				.getManagedBean().getScCommitteeMemberTabList();
		this.getManagedBean().setSelectedCommitteeTabIndex(selectedCommitteeTabIndex);
		TabBean committeTabBean = committeTabBeans.get(selectedCommitteeTabIndex);
		// Bug 32277 - end

		this.getManagedBean().setSelectedTabBean(committeTabBean);
		// 25037 - start
		String committeeType = this.getManagedBean().getSelectedTabBean().getTabType();
		this.getManagedBean().setRoleFilterList(
				getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), committeeType));

		if (this.getManagedBean().isShowHistory()
				&& Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST).contains(committeeType)) {// 25037 - note 372
			navigateToViewHistory(committeTabBean.getScQtCommittee(), true);
		}
		if (null != committeTabBean && null != committeTabBean.getScQtCommittee()) {
			List<QtCommitteeMemberBean> historyList = getMemberHistoryList(committeTabBean.getScQtCommittee()
					.getScCommittee());
			this.getManagedBean().setMemberHistoryList(historyList);
		}
		// 25037 - end
	}
															

	/*
	 * Approval tab change action.
	 */
	public void approvalTabChangeAction(int selectedApprovalTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}

		TabBean approvalTabBean = this.getManagedBean().getTabList()
				.get(selectedApprovalTabIndex);
		
		// #25077 - #80 - Start.
		if (null != approvalTabBean && StringUtils.isNotEmpty(approvalTabBean.getTabType())
				&& ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(this.getManagedBean().getPreviousApprovalTabName())
				&& !ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(approvalTabBean.getTabType())) {
			IQTDetailSO detailSO;
			
			try {
				this.getManagedBean().setSaveFlow(false);
				
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				if (this.getManagedBean().isMaintanceFlow() == false) {
					QuationTenderReqDTO reqDTO = prepareApprovalsInfo();
					reqDTO.getQtdetail().setOtherApproval(true);
					// #25077 - #91 - Start
					reqDTO.getQtdetail().setMitiApproval(true);
					// #25077 - #91 - End

					// bug #30434 for #25077 -start-
					reqDTO.setApprovalOTH(this.getManagedBean().getSelectedApprovalTabBean().getTabType());
					// bug #30434 for #25077 -end-

					detailSO.validateApprovalDetails(reqDTO);
				}
			} finally {
				detailSO = null;
			}
		}

		if (null != approvalTabBean
				&& StringUtils.isNotEmpty(approvalTabBean.getTabType())
				&& (ParameterConst.APPROVAL_CATEGORY_BPK.equals(approvalTabBean.getTabType()) || ParameterConst.APPROVAL_CATEGORY_OTHERS
						.equals(approvalTabBean.getTabType()))) {
			if (getUserTaskIsPublicationApproval() && isApprovalScreen()) {
				List<ScQtApprovalRemark> remarks = this.getManagedBean().getPublicationApproval().getScQtApprovalRemarks();
				
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();

						if (null == userRemark || "".equals(userRemark)) {
							this.getManagedBean().getQt().setRemarksFirstValidation(true);
						}
					}
				}
			}

			// #34694 - Start.
			if ((getUserTaskIsCommitteeAppointmentApproval() || getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec())
					&& isApprovalScreen()) {
				ScQtApproval qtApproval = approvalTabBean.getScQtApproval();
				List<ScQtApprovalRemark> remarks = initializeRemarks(qtApproval, qtApproval.getScQt()
						.getCurrentWorkFlowStatus(), this.getManagedBean().getScQtApproval().getScQtApprovalRemarks(),
						ScQtApprovalRemark.class);

				for (int i = 0; i < remarks.size(); i++) {
					if (remarks.get(i).isBrandNew() && remarks.get(i).getRemark() == null
							&& remarks.get(i).getRemarkId() == null) {
						remarks.remove(i);
					}
					remarks.get(i).setEditable(false);
				}
				
				qtApproval.setScQtApprovalRemarks(remarks);
				approvalTabBean.setScQtApproval(qtApproval);
			}
			// #34694 - End.
		}
		// #25077 - #80 - End.

		this.getManagedBean().setSelectedApprovalTabIndex(
				selectedApprovalTabIndex);
		this.getManagedBean().setSelectedApprovalTabBean(approvalTabBean);
		this.getManagedBean()
				.setScQtApproval(approvalTabBean.getScQtApproval());
		
		// #25077 - #80 - Start.
		if (null != approvalTabBean && StringUtils.isNotEmpty(approvalTabBean.getTabType())) {
			this.getManagedBean().setPreviousApprovalTabName(approvalTabBean.getTabType());
		}
		// #25077 - #80 - End.
	}

	/**
	 * Validate qt details.
	 */
	public void validateQtDetails() {
		
		debugLogger("validateQtDetails");
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		int activeIndex = this.getManagedBean().getActiveIndex();

		int subTabIndex = this.getManagedBean().getSelectedTabIndex();

		String taskName = this.getManagedBean().getTaskName();
		boolean isCreateQt = this.getManagedBean().isCreateQt();
		boolean isMaintFlow = this.getManagedBean().isMaintanceFlow();
		String zonelocationLevel = this.getManagedBean().getStateCodes();
		String locationLevel = this.getManagedBean().getLocalLevelCode();
        boolean readOnly = this.getManagedBean().isReadOnly();
		boolean isCommitteeAppointment = this.getManagedBean().isCommitteeAppointment();
		boolean isCommitteeAppointmentRemark = this.getManagedBean().isCommitteeAppointmentRemark();
		boolean isPanel = this.getManagedBean().getPanel();
		boolean isdropdownPresent  = this.getManagedBean().isProcMethodDropdownPresent();
		Long subStatus = this.getManagedBean().getSubStatusId(); //#25692
		
		QuationTenderResDTO resDTO = null;
		String taskId = this.getManagedBean().getTaskId();
		String[] outComes = this.getManagedBean().getOutComes();
		String processName = this.getManagedBean().getProcessName();
		Object payLoad = this.getManagedBean().getPayLoad();
		Long currentStatus = this.getManagedBean().getCurrentStatusId();
		List<TplSpecZoneDtl> specZoneDtls = this.getManagedBean()
				.getTemplateSpecZoneDtls();
		QuationTenderReqDTO reqDTO = prepareQTInfo();
		reqDTO.setSaveFlow(true);
		reqDTO.setOutcome(BpmOutcomeConst.BPM_SAVE);

		// #9019 -start-
		FacesScopes facesScopes = new FacesScopes();
		
		reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setPayLoad(this.getManagedBean().getPayLoad());

		// - Start (Harun) set Group Item to NULL when save while left blank and panel is selected
		//9019 Start - #Note-418 and merge with #Note-501 
		if (null == this.getManagedBean().getQt().getItemGroup()) {
			this.getManagedBean().getQt().setCircularVer(null);
		}else if (null == this.getManagedBean().getQt().getItemGroup().getPanelItemGroupId() || this.getManagedBean()
				.getQt().getItemGroup().getPanelItemGroupId() == ParameterConst.PM_PARAMETERID_ZERO) {
			this.getManagedBean().getQt().setItemGroup(null);
			this.getManagedBean().getQt().setCircularVer(null);
		}
		//9019 - End (Harun)

		if (activeIndex == 0 || activeIndex == 1 || activeIndex == 2 || activeIndex == 3) {
			if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
				List<ScQtRemark> qtRemarks = initializeRemarks(getManagedBean().getQt(), this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(), getManagedBean().getQt().getScQtRemarks(), ScQtRemark.class);

				for (ScQtRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(true);
				}
				
				this.getManagedBean().getQt().setScQtRemarks(qtRemarks);
				this.getManagedBean().setScQtRemarksEmpty(true);// checking for empty remarks
			}
		}
		// 9019 - End (Harun)
		
		//9019 - Start (Harun) #Note-398 and merge with #Note-487 and #35067
		if (reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)
				&& this.getManagedBean().getQt().getMinistrySpecSecrataryId() != null) {
			if (this.getManagedBean().getQt().getMinistrySpecSecrataryId().getUserId() == null) {
				this.getManagedBean().getQt().setSpecApprovalReq(false);
				reqDTO.getQtdetail().setSpecApprovalReq(false);
				reqDTO.getQtdetail().setMinistrySpecSecrataryId(null);
			}
		}
		//9019 - End (Harun)
		
		//9019 Start - (Harun) #Note-501 
		if(QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())){
			detailSO.validateQTGeneralInformation(reqDTO);
		}
		//9019 End 

		int changedIndexNumber = manipulateIndexNumber(activeIndex);
		switch (changedIndexNumber) {
		case 0:
			detailSO.validateQTDetails(reqDTO);

			//9019 -start-
			if (this.getManagedBean().isScQtRemarksEmpty()) {
				this.getManagedBean().getQt().setScQtRemarks(null);
			}
		
			// using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO);
			//9019 - end

			renewQTSesionObject();
			this.getManagedBean().setTaskName(taskName);
			// #9019 -start-
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			// #9019 -end-

			setAmmendMentBean(resDTO.getAmendMentBean());

			break;
		case 1:
			//9019 - Start (Harun) #Note-378
			if(!reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				detailSO.validateSupplierQualificationDtls(reqDTO);
			}
			//9019 - End (Harun)
	
			// 9019 -start-
			if (this.getManagedBean().isScQtRemarksEmpty()) {
				this.getManagedBean().getQt().setScQtRemarks(null);
			}
		
			// using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO);
			// 9019 - end

			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveSuppQualificationTabDtl();
			setAmmendMentBean(resDTO.getAmendMentBean());

			break;
		case 2:
			//Added the validation for committee while save - 13342
			ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			committeReqDTO.setScQt(reqDTO.getQtdetail());
			committeReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
			committeReqDTO.setSaveFlow(true);
			
			//9019 - Start (Harun) #Note-378 
			if(!reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				committeSO.validateAppointCommitteDtls(committeReqDTO);
			}
			//9019 - End (Harun)
			
			// 9019 -start-
			if (this.getManagedBean().isScQtRemarksEmpty()) {
				this.getManagedBean().getQt().setScQtRemarks(null);
			}

			// using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO);
			// 9019 - end

			int committeeTabIndex = this.getManagedBean()
					.getSelectedCommitteeTabIndex();
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setSelectedTabIndex(subTabIndex);
			// this.getManagedBean().setActiveIndex(0);
			this.getManagedBean().setSelectedCommitteeTabIndex(
					committeeTabIndex);
			retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
			this.getManagedBean().setCurrentStatusId(currentStatus);
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
				initializeRemarksForCommitteeApproval();
				initCommitteeTabSingularReapproval(payLoad);//25037
			}
			setAmmendMentBean(resDTO.getAmendMentBean());
			break;
		case 3:
			reqDTO.getQtdetail().setModified(false);
			reqDTO.getQtdetail().setFCApproval(true);
			
			//9019 - Start (Harun) #Note-378
			if(!reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				detailSO.validateApprovalDetails(reqDTO);
			}
			//9019 - End (Harun)
	
			// 9019 -start-
			if (this.getManagedBean().isScQtRemarksEmpty()) {
				this.getManagedBean().getQt().setScQtRemarks(null);
			}

			// using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO);
			// 9019 - end

			int selectedApprovalTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setSelectedApprovalTabIndex(selectedApprovalTabIndex);
			setAmmendMentBean(resDTO.getAmendMentBean());

			retrieveOthersApprovalTabDtl(); // #25077
			
			retrieveApprovalTabDtl();

			break;
		case 4:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateBsvDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveBriefingSitevisitTabDtl();
			} else if(super.isQTStatusLessThanOrEquals(currentStatus, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)){
				// save scQt info onclick of Activites tab
				//9019 - Start (Harun) #Note-378 
				if(!reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
					detailSO.validateQTDetails(reqDTO);
				}
				//9019 - End (Harun)
				
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setTaskName(taskName);
				retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
				retrieveActivitiesTabDtl();
				setAmmendMentBean(resDTO.getAmendMentBean());
			}
			this.getManagedBean().setCurrentStatusId(currentStatus);
			break;
		case 5:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateScheduleDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveScheduleTabDtl();

				// #13344-start
			} else if (super.isQTStatusLessThanOrEquals(currentStatus, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)) {
				// save scQt info onclick of Activites tab
				// detailSO.validateQTDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				
				renewSesionObject();
				
				this.getManagedBean().setTaskName(taskName);
				
				retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
				retrieveApprovalTabDtl();
				
				setAmmendMentBean(resDTO.getAmendMentBean());
			}

			this.getManagedBean().setCurrentStatusId(currentStatus); 
			// #13344-End

			break;
		case 6:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				detailSO.validateDeductionDtls(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDeductionPaymentTabDtl();
			}
			break;
		case 7:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDocumentTabDtl();
				break;
			}
		case 8:
			break;
		case 9:
			if (isQTStatusGreaterThan(getManagedBean().getQt().getQtStatus().getStatusId(), QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
				reqDTO.getQtdetail().setModified(true);
				reqDTO.getQtdetail().setFCApproval(true);
				detailSO.validateApprovalDetails(reqDTO);
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrievePublicationApprovalTabDtl();
			}
			break;
		}
		
		ScQt scQt = getManagedBean().getQt();
			
		if (scQt.isZonal() && 
				scQt.getScQtSupplierCriteria() != null &&
				scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
			// Populate zone detail into tree node
			populateZoneDetailIntoTreeNode(scQt, 
					scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls());	
		}

		if(null!=this.getManagedBean().getCtContract()){
			this.getManagedBean().getCtContract().setContractNo(this.getManagedBean().getQt().getExistingContractNo());
		}
		this.getManagedBean().setActiveIndex(activeIndex);
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setOutComes(outComes);
		this.getManagedBean().setProcessName(processName);
		this.getManagedBean().setStateCodes(zonelocationLevel);
		this.getManagedBean().setLocalLevelCode(locationLevel);
		this.getManagedBean().setTemplateSpecZoneDtls(specZoneDtls);
		this.getManagedBean().setTaskName(taskName);
		this.getManagedBean().setCommitteeAppointment(isCommitteeAppointment);
		this.getManagedBean().setCommitteeAppointmentRemark(isCommitteeAppointmentRemark);
		this.getManagedBean().setPayLoad(payLoad);
		this.getManagedBean().setCreateQt(isCreateQt);
		this.getManagedBean().setMaintanceFlow(isMaintFlow);
		this.getManagedBean().setReadOnly(readOnly);
		this.getManagedBean().setPanel(isPanel);
		this.getManagedBean().setProcMethodDropdownPresent(isdropdownPresent);
		this.getManagedBean().setSubStatusId(subStatus); //#25692

		//9019 - Start (Harun) initializing for NULL value when save 
		if (null == this.getManagedBean().getQt().getProcurementTypeCatId()) {
			this.getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
			this.getManagedBean().getQt().getProcurementTypeCatId().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getFulfilmentType()) {
			this.getManagedBean().getQt().setFulfilmentType(new PmParameter());
			this.getManagedBean().getQt().getFulfilmentType().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getPmProcurementCategory()) {
			this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());
			this.getManagedBean().getQt().getPmProcurementCategory().setParameterId((long) 0);
		}
		
		if (null == this.getManagedBean().getQt().getContractType()) {
			this.getManagedBean().getQt().setContractType(new PmParameter());
			this.getManagedBean().getQt().getContractType().setParameterId((long) 0);
		}
		// - End (Harun)
	
		// - Start (Harun) initializing remark for cancel or subsequent save start
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else {
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}
		// - End (Harun)
		
		// - Start (Harun) initializing Group Item when panel is selected and left blank while SAVE
		//30062022 by (Harun) Add checking for PanelItemGroupId due to error that effect for enhancement #29891 
		//07072022 by (Harun) Change checking for ItemGroup due to error when save Qt with Panel Item
		if (isPanel && null == this.getManagedBean().getQt().getItemGroup()) {	
			this.getManagedBean().getQt().setItemGroup(new CtPanelItemGroup());
			this.getManagedBean().getQt().getItemGroup().setPanelItemGroupId((long) 0);
		}
		//9019 - End (Harun)
		
		if (isCreateQt) {
			this.getManagedBean().setOutComes(
					new String[] { ResourceBundleUtils.getMessage(
							"lbl.btn.submit",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale()) });
		}
	}
	
	/**
	 * Qt details tab change.
	 * 
	 * @param activeIndex
	 *            the active index
	 */
	public void qtDetailsTabChange(Integer activeIndex) {
		int priviousIndex = this.getManagedBean().getActiveIndex();
		getManagedBean().getQt().setMitiApproval(false);
		getManagedBean().getQt().setOtherApproval(false);
		getManagedBean().getQt().setFCApproval(false);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = prepareQTInfo();
		boolean isReadOnly = getManagedBean().isReadOnly();
		boolean isMaintancePendingChklistStatus = isMaintancePendingChklistStatus();
		
		// 29464-start
		String taskName = this.getManagedBean().getTaskName();
		taskName = taskName == null ? "" : taskName;
		// 29464-end
		
		//9019 - Start (Harun) initializing remark 
	    //       This code for initialize back whenever qtDetailsTabChange is trigger and change while remarks is 
		//       able to entered when available.Reposition code 09082022 cause by reopen
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {

			ScQt scQt = this.getManagedBean().getQt();
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else {
			ScQt scQt = this.getManagedBean().getQt();
			
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}
		// 9019 - End (Harun)
		
		//9019 - Start (Harun) #Note-360 
		boolean isSubmit = StringUtils.isNotEmpty(reqDTO.getOutcome())
				&& reqDTO.getOutcome().equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) ? true : false;
		//9019 - End (Harun)

		if (priviousIndex != activeIndex) {
			if (!isReadOnly) {
				switch (priviousIndex) {
				case 0:
					//9019 - Start (Harun) #Note-360 
					if (isSubmit) {
						detailSO.validateQTDetails(reqDTO);
					}
					//9019 - End (Harun)
					// retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId());
					break;
				case 1:
					//9019 - Start (Harun) #Note-360
					if (isSubmit) {
						detailSO.validateSupplierQualificationDtls(reqDTO);
					}
					//9019 - End (Harun)
					break;

				case 2:
					// retrieveCommitteeAppointmentTabDtl();
					// retrieveSuppQualificationTabDtl();
					break;
				case 3:
						if (isQTStatusBtwScheduleDocAndBeforePublished(getManagedBean()
								.getQt().getQtStatus().getStatusId())) {
							reqDTO.getQtdetail().setFCApproval(true);
	
							// #25077 - #80 - Start.
							if (this.getManagedBean().isMaintanceFlow() == false) {
								reqDTO.getQtdetail().setOtherApproval(true);
							}
							// #25077 - #80 - End.
							// #25077 - #91 - Start
							if (this.getManagedBean().isMaintanceFlow() == false) {
								reqDTO.getQtdetail().setMitiApproval(true);
							}
							// #25077 - #91 - End
						}
						
					//9019 - Start (Harun) #Note-360 
					//       when click sidemenu
					if (isSubmit) {
						detailSO.validateApprovalDetails(reqDTO);
					}
					//9019 - End (Harun)	
					// retrieveApprovalTabDtl();
					break;

				case 4:
					
					if (!isMaintancePendingChklistStatus && isinvokeValidations(getManagedBean().getQt())) {
						detailSO.validateBsvDetails(reqDTO);
					} else if(isMaintancePendingChklistStatus){
							detailSO.validateDeductionDtls(reqDTO);
					}
					break;

			case 5:
				if (!this.getManagedBean().isMaintanceFlow()) {
					Long currentStatusId = getManagedBean().getQt().getQtStatus().getStatusId();
					if (isQTStatusBtwScheduleDocAndBeforePublished(currentStatusId)) {
							detailSO.validateScheduleDetails(reqDTO);
						}
					}
				
					break;

				case 6:
					if (isinvokeValidations(getManagedBean().getQt())) {
						detailSO.validateDeductionDtls(reqDTO);
					}
					break;

			case 7:
				break;
			case 8:
				break;
			case 9:
				if (!this.getManagedBean().isMaintanceFlow()) {
					if (isQTStatusBtwScheduleDocAndBeforePublished(getManagedBean().getQt().getQtStatus().getStatusId())) {

							reqDTO.getQtdetail().setCallPTJValidation(true);
							reqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
							detailSO.validateApprovalDetails(reqDTO);
						}
					}
					break;
				}
			}
			switch (activeIndex) {
			case 0:
				// detailSO.validateateQTDetails(reqDTO);
				retrieveQuationTenderDetails(this.getManagedBean().getQt()
						.getQtId());
				break;
			case 1:
				retrieveSuppQualificationTabDtl();
				break;

			case 2:
				// 29464-start
				if (taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)) {
					this.getManagedBean().setCommitteeAppointmentMenuTab(false);
				}
				// 29464-end
				// detailSO.validateSupplierQualificationDtls(reqDTO);
				// Bug 32277 - start
				if (StringUtils.equals(this.getManagedBean().getTaskName(), SCProcessNameConst.APPROVE_COMMITTEE)) {
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION, true);
				} else {
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
				}
				// Bug 32277 - end

				break;
			case 3:
				// #35285 & #25635 - Start
				if (this.isQTStatusGreaterThanOrEquals(this.getManagedBean().getQt().getQtStatus().getStatusId(),
						QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) {
					// #35285 & #25635 - End
					// 25635&34502-start
					if (!this.getManagedBean().getQt().isCloneEnabled()) {
						// #34694,#25077 - Start
						if (!getManagedBean().isApproverTabLoaded()) {
							retrieveOthersApprovalTabDtl();
						}
						// #34694,#25077 - End

						retrieveApprovalTabDtl();
					}
					// 25635&34502-End
					// #35285 & #25635 - Start
				} else {
					if (!this.getManagedBean().getQt().isCloneEnabled()) {
						retrieveOthersApprovalTabDtl();
						retrieveApprovalTabDtl();
					}
				}
				// #35285 & #25635 - End
				break;

			case 4:
				Long statusId = getManagedBean().getQt().getQtStatus().getStatusId();
				if (super.isQTStatusLessThanOrEquals(statusId, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)) {
					retrieveActivitiesTabDtl();
				} else if(isMaintancePendingChklistStatus){
					retrieveDeductionPaymentTabDtl();
				} else {
					retrieveBriefingSitevisitTabDtl();
				}
				break;
			case 5:
				// 29464-start
				if (taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)) {
					this.getManagedBean().setCommitteeAppointmentMenuTab(true);
					
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
				}
				// 32277 - start
				else if (taskName.equalsIgnoreCase(SCProcessNameConst.APPROVE_COMMITTEE)) {
					if (CollectionUtils.isNotEmpty(this.getManagedBean().getScCommitteeMemberTabList())) {
						int index = 0;
						this.getManagedBean().setSelectedTabIndex(index);
						this.getManagedBean().setSelectedCommitteeTabIndex(index);
						TabBean committeTabBean = this.getManagedBean().getScCommitteeMemberTabList().get(index);
						this.getManagedBean().setSelectedTabBean(committeTabBean);
						this.getManagedBean().setPublicationApproval(null);
						if (null != committeTabBean.getScQtCommittee()) {
							List<QtCommitteeMemberBean> historyList = getMemberHistoryList(committeTabBean
									.getScQtCommittee().getScCommittee());
							this.getManagedBean().setMemberHistoryList(historyList);
						}
					}
				}
				// 32277 - end
				else {
					if (isMaintancePendingChklistStatus) {
						retrieveDocumentTabDtl();
					} else {
						retrieveScheduleTabDtl();
					}
				}
				// 29464-end
				
				break;
			case 6:
				if(isMaintancePendingChklistStatus){
					retrieveActivitiesTabDtl();
				} else{
					retrieveDeductionPaymentTabDtl();
				}
				
				break;
			case 7:
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrieveDocumentTabDtl();
				}
				break;
			case 8:
				retrieveActivitiesTabDtl();
				break;
			case 9:
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrievePublicationApprovalTabDtl();

					// #25077 - Start
					if (getUserTaskIsPublicationApproval() && isApprovalScreen()) {
						this.getManagedBean().setScQtApproval(this.getManagedBean().getPublicationApproval());
						this.getManagedBean().getQt().setRemarksFirstValidation(false);
					}
					// #25077 - End
				}
				//#25692-start
				 if(getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()){
					// Bug 32277 - start
					if (CollectionUtils.isEmpty(this.getManagedBean().getScCommitteeMemberTabList())) {
						ApprovalBackingBean approvalbean = new ApprovalBackingBean();
						approvalbean.retriveCommiteeApprovalTabDetails();
					} else {
						int index = 0;
						this.getManagedBean().setSelectedTabIndex(index);
						this.getManagedBean().setSelectedCommitteeTabIndex(index);
						TabBean committeTabBean = this.getManagedBean().getScCommitteeMemberTabList().get(index);
						this.getManagedBean().setSelectedTabBean(committeTabBean);
						this.getManagedBean().setPublicationApproval(null);
						if (null != committeTabBean.getScQtCommittee()) {
							List<QtCommitteeMemberBean> historyList = getMemberHistoryList(committeTabBean
									.getScQtCommittee().getScCommittee());
							this.getManagedBean().setMemberHistoryList(historyList);
						}
					}
					// Bug 32277 - end
				 }
				 else if(getUserTaskIsBPKApproval() && isApprovalScreen()){
					 this.getManagedBean().setPublicationApproval(null);	
				 }
				 else if(getUserTaskIsPublicationApproval() && isApprovalScreen()){
					 this.getManagedBean().setPublicationApproval(null);	
				 }
				//#25692-End
					break;
			case 50: //BPK Approval
				
			}

			// #25077 - Start
			if (getUserTaskIsPublicationApproval() && isApprovalScreen()) {
				this.getManagedBean().setPublicationApproval(this.getManagedBean().getScQtApproval());
				this.getManagedBean().setFirstPagePA(false);
			}
			// #25077 - End

			this.getManagedBean().setActiveIndex(activeIndex);
		}

		//9019 - Start (Harun) initializing remark 
	    //       This code for initialize back whenever qtDetailsTabChange is trigger and change while remarks is 
		//       able to entered when available 
		if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
			
			ScQt scQt = this.getManagedBean().getQt();
			List<ScQtRemark> qtRemarks = initializeRemarks(scQt, this.getManagedBean().getQt()
					.getCurrentWorkFlowStatus(), scQt.getScQtRemarks(), ScQtRemark.class);

			for (ScQtRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(true);
			}
			
			this.getManagedBean().getQt().setScQtRemarks(qtRemarks);

		} else{
			ScQt scQt = this.getManagedBean().getQt();
			this.getManagedBean().getQt().setScQtRemarks(scQt.getScQtRemarks());
		}
		//9019 - End (Harun)
	}

	/**
	 * 
	 * @param activeIndex
	 * @param otherwiseActiveIndex
	 * @param conditionForActiveIndex - if true, use activeIndex. else otherwiseActiveIndex will be used.
	 */
	public void qtDetailsTabChange(Integer activeIndex, Integer otherwiseActiveIndex, Boolean conditionForActiveIndex){
		if(conditionForActiveIndex){
			qtDetailsTabChange(activeIndex);
		}
		else{
			qtDetailsTabChange(otherwiseActiveIndex);
		}
	}

	private boolean isQTStatusBtwScheduleDocAndBeforePublished(Long currentStatusId) {
		return isQTStatusInBetween(currentStatusId,
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT,
				QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
	}
	
	private boolean isQTStatusGreaterThanOrEqualsScheduleDoc(Long currentStatusId) {
		return isQTStatusGreaterThanOrEquals(currentStatusId,
				QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
	}

	/**
	 * Check is approval exists.
	 * 
	 * @param qtApproval
	 *            the qt approval
	 * @param existingApprovalTabs
	 *            the existing approval tabs
	 * @return the sc qt approval
	 */
	private ScQtApproval checkIsApprovalExists(ScQtApproval qtApproval,
			List<TabBean> existingApprovalTabs) {
		if (null != existingApprovalTabs) {
			for (TabBean approvalTab : existingApprovalTabs) {
				if (qtApproval
						.getApprovalCategory()
						.getParameterCode()
						.equalsIgnoreCase(
								approvalTab.getScQtApproval()
										.getApprovalCategory()
										.getParameterCode())) {
					qtApproval.setApproverSettings(approvalTab
							.getScQtApproval().getApproverSettings());

					qtApproval.setScQtApprovalRemarks(approvalTab
							.getScQtApproval().getScQtApprovalRemarks());
					List<ScQtApprovalRemark> approvalRemarks = qtApproval
							.getScQtApprovalRemarks();
					if (CollectionUtils.isNotEmpty(approvalRemarks)) {
						for (ScQtApprovalRemark remarks : approvalRemarks) {
							remarks.setScQtApproval(qtApproval);
						}

					}
					List<ScQtApprovalAttachment> approvalAttachements = approvalTab
							.getScQtApproval().getScQtApprovalAttachments();
					if (CollectionUtils.isNotEmpty(approvalTab
							.getScQtApproval().getScQtApprovalAttachments())) {
						for (ScQtApprovalAttachment approvalAttachment : approvalAttachements) {

							approvalAttachment.setScQtApproval(qtApproval);
						}
						qtApproval
								.setScQtApprovalAttachments(approvalTab
										.getScQtApproval()
										.getScQtApprovalAttachments());
					}
					return qtApproval;
				}
			}
		}
		return qtApproval;
	}

	/**
	 * Gets the parameter so.
	 * 
	 * @return the parameter so
	 */
	private IParameterSO getParameterSO() {
		return EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Construct bsv by bean.
	 * 
	 * @param bsvDtlBean
	 *            the bsv dtl bean
	 * @return the sc qt bsv
	 */
	private ScQtBsv constructBsvByBean(BriefingZoneBean bsvDtlBean) {
		ScQtBsv scQtBsv = null;
		if (null != bsvDtlBean) {
			scQtBsv = bsvDtlBean.getScQtBsv();
			List<ScQtBsvDtl> bsvDetils = new ArrayList<ScQtBsvDtl>();
			List<ScQtBsvDtl> briefings = bsvDtlBean.getBriefingList();
			List<ScQtBsvDtl> siteVisit = bsvDtlBean.getSitevisitList();

			if (!scQtBsv.isSameBsv()) {
				if (!CollectionUtils.isEmpty(briefings)) {
					bsvDetils.addAll(briefings);
				}
				if (!CollectionUtils.isEmpty(siteVisit)) {
					bsvDetils.addAll(siteVisit);
					if (bsvDetils.size() >= 2) {
						if (!scQtBsv.isAllSiteVisitReq()) {
							if (scQtBsv.getMinSiteVisitAttend() < 1) {
								throw new EpException(MessageConst.ERROR_760273);
							}
						}
					}
				}
			} else {
				if (!CollectionUtils.isEmpty(briefings)) {
					bsvDetils.addAll(briefings);
					scQtBsv.setAllSiteVisitReq(false);
					scQtBsv.setMinSiteVisitAttend(1);
				}
			}
			/*
			 * if (!scQtBsv.isAllBriefingReq()) { if
			 * (scQtBsv.getMinBriefingAttend() < 1) { throw new EpException(
			 * "Min Briefing Attendance is greater than 0"); } }
			 */

			scQtBsv.setScQtBsvDtls(bsvDetils);
		}
		return scQtBsv;
	}

	/**
	 * Checks if is empty or null.
	 * 
	 * @param list
	 *            the list
	 * @return true, if is empty or null
	 */
	private boolean isEmptyOrNull(List<? extends Object> list) {
		if (null == list || list.isEmpty()) {
			return true;
		}
		return false;
	}
   
	/*		   
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	//25037-start
	public void submitQtDetailsFormMaintanace() {
		submitQtDetailsFormMaintanace(false);
	}
	//#25037-end

	// #25037 -start-
	public void submitQtDetailsFormMaintanace(boolean confirmResetReendorse) {
		// #25037 -end-
		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			updateApprovalDetails(this.getManagedBean().getQt());
			retrieveApprovalTabDtl();

			// #25037-start
			boolean showPopup = validateIsCommitteeFormed(confirmResetReendorse);
			if (showPopup) {
				return;
			}
			// #25037-end
			
			// 11104-start
			if (this.getManagedBean().getQt().isSampleReq()) {
				this.getManagedBean().setSubmit(true);
				retrieveScheduleTabDtl();
			}
			// 11104-End

			// 29732 qt maintenance -Start
			this.getManagedBean().setFromSubmitCatChange(true);

			String changedCatCodeList = checkCatCodeChange();
			ScWorkFlowStatus specMainworkflow = this.getManagedBean().getQt().getCurrentWorkFlowStatus();
			Long workflowStatusId = specMainworkflow.getPmStatus().getStatusId();

			Set<Long> pendingStatuses = new HashSet<>(Arrays.asList(QTStatusConst.SE_PENDING_SUBMISSION,
					QTStatusConst.SE_PENDING_ENDORSMENT, QTStatusConst.SE_PENDING_SIGNOFF,
					QTStatusConst.SE_PENDING_REVISION, QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL,
					QTStatusConst.SE_COMPLETED));
			boolean qtStatusWorkflow = pendingStatuses.contains(workflowStatusId);

			if (!changedCatCodeList.isEmpty() && qtStatusWorkflow) {
				this.getManagedBean().setCatCodeList(changedCatCodeList);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_CAT_CODE_CHANGE_ALERT_SHOW);
				return;
			}
			// 29732 -End

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			ScQt qtDetails = quationTenderReqDTO.getQtdetail();
			QtAmendMentBean amendMentBean = getManagedBean().getAmendMentBean();

			quationTenderReqDTO.setQtdetail(qtDetails);
			quationTenderReqDTO.setAmendMentBean(amendMentBean);
			quationTenderReqDTO.setRoleCode(getUserRoleCode());
			
			// start #25037
			FacesScopes fs = new FacesScopes();
			quationTenderReqDTO.setPmUser(fs.getUserSessionBean());
			quationTenderReqDTO.setDoaList(this.getManagedBean().getPmuserContactpersonList());
			quationTenderReqDTO.setToken(fs.getBPMAuthenticationCtxToken());
			// end #25037
			
			QuationTenderResDTO quationTenderResDTO = detailSO
					.updateQtMaintananceDetails(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();
				Long currentStatusId = qtDetails.getQtStatus().getStatusId();
				
				if (QTStatusConst.QT_PENDING_PUBLICATION.equals(currentStatusId) ||
						QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION.equals(currentStatusId) ||
						QTStatusConst.QT_PENDING_RESCEDULE_PROPOSAL_CLOSING_DATE.equals(currentStatusId)) {
					if (amendMentBean != null) {						
						List<String> enableFields = QTFieldMapper.getEnableFieldsByStatus(currentStatusId,
								true, !getManagedBean().isReadOnly());
						if (CollectionUtils.isNotEmpty(enableFields)) {
							List<String> updatedQTFields = new ArrayList<String>();
							
							for (String enableField : enableFields) {
								if (QTFieldConst.FIELD_QT_DOCUMENTS.equals(enableField)) {
									QtAmendMentValueBean valueBean = amendMentBean.getValueMap().get(enableField);
									if (valueBean != null && valueBean.getOldValue() != null) {
										TemplateDocBackingBean tplDocBB = new TemplateDocBackingBean();
										tplDocBB.previewAction();
										valueBean.setNewValue(tplDocBB.getManagedBean().getPreviewValue());
										
										if (valueBean.getNewValue() != null && !valueBean.getOldValue().equals(valueBean.getNewValue())) {
											updatedQTFields.add(enableField);
										}
									}
									
								} else if (QTFieldConst.FIELD_QT_BSVDETAILS.equals(enableField)) {
									List<ScQtBsv> scQtBsvs = qtDetails.getScQtBriefings();
									if (CollectionUtils.isNotEmpty(scQtBsvs)) {
										String[] bsvFields = QTFieldConst.BSV_FIELDS;
										for (ScQtBsv scQtBsv : scQtBsvs) {
											for (ScQtBsvDtl scQtBsvDtl : scQtBsv.getScQtBsvDtls()) {
												String fieldPrefix = scQtBsvDtl.getQtBsvDtlId() + QTFieldConst.FIELD_QT_BSV_SEPARATOR;
												for (String bsvField : bsvFields) {
													QtAmendMentValueBean valueBean = amendMentBean.getValueMap().get(fieldPrefix + bsvField);
													if (valueBean != null && !updatedQTFields.contains(enableField)) {
														updatedQTFields.add(enableField);
														break;	
													}
												}
											}
										}	
									}
									
								} else {
									QtAmendMentValueBean valueBean = amendMentBean.getValueMap().get(enableField);
									if (valueBean != null) {
										updatedQTFields.add(enableField);
									}
								}
							}
							
							// regenerate documents based on qt field changes.
							if (CollectionUtils.isNotEmpty(updatedQTFields)) {
								new QTDocumentsBackingBean().reGenerateDocuments(qtDetails.getQtId(), 
										updatedQTFields.toArray(new String[updatedQTFields.size()]));	
							}
						}
					}
				}

				// 25037 - start - note 510
				boolean approvalExistOnSubmission = false;

				if (CollectionUtils.isNotEmpty(quationTenderResDTO.getCmtModififationRequests())) {
					List<ScQtCommittee> qtCommittees = quationTenderResDTO.getQtDetail().getScQtCommittees();

					for (ScQtCommittee qtCommittee : qtCommittees) {
						PmStatus pmStatus = qtCommittee.getScCommittee().getCommitteStatus();

						if (pmStatus.getStatusId().equals(QTStatusConst.CMT_PENDING_APPROVAL)) {
							approvalExistOnSubmission = true;
							break;
						}
					}
				}

				if (approvalExistOnSubmission) {
					successMessage(MessageConst.INFO_760524,
							new String[] { this.getParameterDesc(scQt.getProcurementMode()), scQt.getQtNo() }, true);
				} else {
					String msgCode;

					if (quationTenderResDTO.isSuccessful()) {
						msgCode = MessageConst.INFO_700026;
					} else {
						msgCode = MessageConst.INFO_700004;
					}

					successMessage(msgCode, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
							scQt.getQtNo(), Boolean.TRUE);
				}
				// 25037 - end - note 510
			}
		} finally {
			detailSO = null;
		}
	}

	/**
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public String cancelQtDetails() {
		debugLogger("cancelQtDetails");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

			if (isQTStatusGreaterThanOrEquals(scQt.getQtStatus().getStatusId()
							, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION)) {
				quationTenderReqDTO.setQtId(scQt.getQtId());
				quationTenderReqDTO
						.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();
				
				List<ScQtApprovalRemark> qtRemarks = initializeRemarks(qtApproval, 
						this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(), ScQtApprovalRemark.class);
				for(ScQtApprovalRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(false);
				}
				qtApproval.setScQtApprovalRemarks(qtRemarks);
				TabBean tabBean = new TabBean(null, null, qtApproval,
						retrieveFinalizationCommitteeType());
				this.getManagedBean().setSelectedApprovalTabBean(tabBean);
			} else {
				// #9019 -start-
				// null remark in UI it showing two editable textarea to prevent that only value be pass to below method 
				List<ScQtRemark> remarks = null;

				if (CollectionUtils.isNotEmpty(scQt.getScQtRemarks()) && StringUtils.isNotEmpty(scQt.getScQtRemarks().get(0).getRemark())) {
					remarks = scQt.getScQtRemarks();
				}
				// #9019 -end-

				scQt.setScQtRemarks(initializeRemarks(scQt, this.getManagedBean().getQt().getCurrentWorkFlowStatus(), remarks, ScQtRemark.class));
			}
			return "/xhtml/sc/quatationtender/cancelqtdetails.xhtml";

		} finally {
			detailSO=null;
		}

	}
			
	/**
	 * Change pb secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changePbSecretaryCommType(ScQtApproverSettings approverSettings) {
		debugLogger("changePbSecretaryCommType");

		Long commTypeId = approverSettings.getCommitteeType().getParameterId();
		if (null != commTypeId) {
			PmParameter commTypeCode = ParameterHelper.getInstance().getPmParameterById(commTypeId);
			approverSettings.setCommitteeType(commTypeCode);

			getManagedBean().getSelectedApprovalTabBean().getScQtApproval().getApproverSettings().get(0).setPmUser(new PmUser());
		}
	}
											 
	/*						   
	 * Submit qt cancel details.
	 * 
	 * @return the string
	 */
	public void submitQtCancelDetails() { //  #Enhancement 9 CRs

		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class); //

			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt qt = this.getManagedBean().getQt();
			String ackMsg = null;
			if (isQTStatusPublished(qt.getQtStatus().getStatusId())) {
				qt.setScQtCommittees(null);
				qt.setScQtBriefings(null);
				qt.setScQtStatus(null);

				ScQtApproval approval = this.getManagedBean()
						.getSelectedApprovalTabBean().getScQtApproval();
//				finalizeRemarks(approval.getScQtApprovalRemarks());
				List<ScQtApprovalRemark> remarks = approval.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { 
										ResourceBundleUtils.getMessage(
											"lbl.sc.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											new FacesScopes().getUserLocale()) });
						}
					}
				}

				List<ScQtApproverSettings> approverSettings = approval
						.getApproverSettings();
				PmParameter fcCommTypeCode = null;
				PmUser approver = null;
				if (null != approverSettings && !approverSettings.isEmpty()) {
					fcCommTypeCode = approverSettings.get(0).getCommitteeType();
					approver = approverSettings.get(0).getPmUser();
				}
				validateFCSecretaryDtl(fcCommTypeCode, approver);

				List<ScQtApproval> scQtApprovals = new ArrayList<ScQtApproval>();

				scQtApprovals.add(approval);
				qt.setScQtApprovals(scQtApprovals);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());
				quationTenderReqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
				
				ackMsg = MessageUtils
						.getMessage(
								MessageConst.INFO_700052,
								new String[] {
										getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												ParameterConst.DOCTYPE_QUOTATION),
										qt.getQtNo() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes()
										.getUserLocale());
			} else {
				quationTenderReqDTO.setQtId(this.getManagedBean().getQt()
						.getQtId());
				List<ScQtRemark> remarks = qt.getScQtRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { 
										ResourceBundleUtils.getMessage(
											"lbl.sc.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS,
											new FacesScopes().getUserLocale()) });
						}
					}
				}
				finalizeRemarks(qt.getScQtRemarks());
				qt.setScQtStatus(null);
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setPmUser(new FacesScopes()
						.getUserSessionBean());
				
				ackMsg = MessageUtils
						.getMessage(
								MessageConst.INFO_700031,
								new String[] {
										getParameterDesc(
												ParameterTypeConst.DOCUMENT_TYPES,
												ParameterConst.DOCTYPE_QUOTATION),
										qt.getQtNo() },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes()
										.getUserLocale());

			}
			QuationTenderResDTO quationTenderResDTO = detailSO
					.cancleQT(quationTenderReqDTO);

			if (quationTenderResDTO.isSuccessful()) {
				successMessage(ackMsg, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
						qt.getQtNo(), Boolean.TRUE);
			}
		} finally {
			detailSO = null;
		}

	}
	
	private void validateFCSecretaryDtl(PmParameter fcCommTypeCode, PmUser fcSecretary) {
		if (fcCommTypeCode == null || fcCommTypeCode.getParameterId() == null) {
			throw new EpException(
					MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.sc.finalization.committee",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
		}

		if (fcSecretary == null || fcSecretary.getUserId() == null) {
			throw new EpException(
					MessageConst.ERROR_100022,
					new String[] { ResourceBundleUtils
							.getMessage(
									"lbl.sc.secretary.name",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes()
											.getUserLocale()) });
		}
	}

	/**
	 * Gets the msg code by task name.
	 * 
	 * @param taskName
	 *            the task name
	 * @return the msg code by task name
	 */
	private String getMsgCodeByTaskName(String taskName) {
		if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)) {
			return MessageConst.INFO_700052;
		}
		return MessageConst.INFO_700005;
	}

	/**
	 * Update user id to settings.
	 * 
	 * @param event
	 *            the event
	 */
	public void updateUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getSelectedApprovalTabBean()
						.getScQtApproval().getApproverSettings().get(0)
						.setPmUser(user);
				break;
			}
		}
	}

	/**
	 * Enable field by status n filed name.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	public boolean enableFieldByStatusNFiledName(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		// #29732 -start-
		if (statusId.equals(QTStatusConst.SE_PENDING_SUBMISSION)) {
			statusId = getManagedBean().getQt().getQtStatus().getStatusId();
		}
		// 29732 -end-

		return enableFieldByStatusNFiledName(fieldName, statusId, 
				getManagedBean().isMaintanceFlow(), 
				!getManagedBean().isReadOnly());

	}
	
	private boolean enableFieldByStatusNFiledName(String fieldName,
			Long statusId, boolean isMaintance, boolean isEditable) {

		List<String> values = QTFieldMapper.getEnableFieldsByStatus(statusId,
				isMaintance, isEditable);
		if (CollectionUtils.isNotEmpty(values)) {
			if (values.contains(fieldName)) {
				return false;
			}

		}
		return true;

	}

	/**
	 * Gets the qt approval by category.
	 * 
	 * @param approvals
	 *            the approvals
	 * @param approvalCategory
	 *            the approval category
	 * @return the qt approval by category
	 */
	private ScQtApproval getQtApprovalByCategory(List<ScQtApproval> approvals,
			String approvalCategory) {
		if (CollectionUtils.isNotEmpty(approvals)) {
			for (ScQtApproval scQtApproval : approvals) {
				if (approvalCategory.equalsIgnoreCase(scQtApproval
						.getApprovalCategory().getParameterCode())) {
					return scQtApproval;

				}
			}
		}
		return null;
	}

	private void setAmmendMentBean(QtAmendMentBean amendMentBean) {
		if (null != amendMentBean) {
			this.getManagedBean().setAmendMentBean(amendMentBean);
		}
	}

	private boolean isinvokeValidations(ScQt scQt) {

		PmStatus pmStatus = scQt.getQtStatus();
		if (null != pmStatus) {
			Long currentStatusId = pmStatus.getStatusId();
			if (isQTStatusInBetween(currentStatusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION)
					&& !this.getManagedBean().isMaintanceFlow()) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Checks if is approval request exists.
	 * 
	 * @param qtApproval
	 *            the qt approval
	 * @param requestType
	 *            the request type
	 * @return true, if is approval request exists
	 */
	private boolean isApprovalRequestExists(ScQtApproval qtApproval) {

		if (null != qtApproval) {
			List<ScQtApprovalRequest> approvalRequests = qtApproval
					.getScQtApprovalDtls();
			if (CollectionUtils.isNotEmpty(approvalRequests)) {
				for (ScQtApprovalRequest approvalRequest : approvalRequests) {

					if (null != approvalRequest.getSubmissionDate()
							&& null == approvalRequest.getApproverAction()) {

						return true;
					}
				}
			}

		}

		return false;
	}

	/**
	 * Enable field by status n filed name.
	 * 
	 * @param fieldName
	 *            the field name
	 * @return true, if successful
	 */
	public boolean enableFieldByStatus(String fieldName) {
		Long statusId = this.getManagedBean().getCurrentStatusId();

		Boolean isFieldToBeEnabled = enableFieldByStatusNFiledName(fieldName);
		List<ScQtApproval> approvals =null;
		//Enhancement #25039, Bug #26261 -start-
		if (null!=this.getManagedBean().getQt()) {
			approvals = this.getManagedBean().getQt().getScQtApprovals();
		}
		//Enhancement #25039, Bug #26261 -end-

		ScQtApproval approval = null;

		if (!isFieldToBeEnabled) {
			if (isStatusOfBpkOrScheduleRevise(statusId)
					|| isQTStatusGreaterThanOrEquals(statusId,QTStatusConst.QT_PROPOSAL_PREPARATION)) {

				approval = getQtApprovalByCategory(approvals,
						ParameterConst.APPROVAL_CATEGORY_BPK);
				return isApprovalRequestExists(approval);

			} else {
				boolean isEditable = false;
				approval = getQtApprovalByCategory(approvals,
						ParameterConst.APPROVAL_CATEGORY_PTJ);
				isEditable = isApprovalRequestExists(approval);
				if (!isEditable) {
					approval = getQtApprovalByCategory(
							approvals,
							ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
					isEditable = isApprovalRequestExists(approval);
				}
				return isEditable;
			}

		}

		return isFieldToBeEnabled;

	}

	/**
	 * Get the ApprovalDetails Enable or Disable based on Approval and status.
	 * 
	 * @return the enable or disable approvals.
	 */
	public boolean approvalDtlsEditable(ScQtApproval approval) {
		
		Long statusId = this
				.getManagedBean().getQt().getQtStatus().getStatusId();
		
		// #25426#36#95 ruzaimah - Start
		if ((this.getManagedBean().isMaintanceFlow()
				&& isDeskOfficer()) // #25426#85 ruzaimah - remove this.
				&& !this.getManagedBean().isReadOnly()) {
			return false;
		}
		// #25426 - end	
		
		if (this.getManagedBean().isMaintanceFlow()
				&& QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(statusId)) {
			return true;
		}
		if ((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
				isStatusOfBpkOrScheduleRevise(statusId)) 
				&& (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode()) || (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode())))) {
			return (this.getManagedBean().isReadOnly());
		}
		List<ScQtApprovalRequest> approvalRequests = approval
				.getScQtApprovalDtls();
		if (CollectionUtils.isNotEmpty(approvalRequests)) {

			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null == approvalRequest.getSubmissionDate()
						|| (null != approvalRequest.getApproverAction() && ParameterConst.APPROVAL_DECISION_TYPE_REVISED
								.equalsIgnoreCase(approvalRequest
										.getApproverAction().getParameterCode()))) {
					return (this.getManagedBean().isReadOnly());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		Long MinistryOrgProfileId = this.getManagedBean().getQtnTenderSearchBean().getMinistryProfile()
				.getOrgProfileId();
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				for (int i = 0; i < orgLevel; i++) {
					condition.append("parentOrgProfile.");
				}
				condition.append("orgProfileId");
				condition.append(SourcingConstants.LOV_CONDITION_SEPARATOR);
				condition.append(MinistryOrgProfileId);

			}

		} 
		return condition.toString();
	}

	/**
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJCondition() {
		debugLogger("getPTJCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		Long MinistryOrgProfileId = qtnTenderSearchBean.getMinistryProfile()
				.getOrgProfileId();
		Long PTJGroupOrgProfileId = qtnTenderSearchBean.getPtjGroupProfile()
				.getOrgProfileId();
		if (null != PTJGroupOrgProfileId) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + PTJGroupOrgProfileId);
		} else if (null != MinistryOrgProfileId) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + MinistryOrgProfileId);
		} 
		return condition.toString();
	}

	// #Enhancement 9 CRs - start	
	
	/**
	 * populate scQt for right side of the join
	 * 
	 * @param searchBean
	 * @param qtSearchBean
	 * @param qtAccessPrefix - access prefix to scQt from root class. 
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	private void populateScQtFilter(QuationTenderReqDTO reqDTO,ISearchBean<?> searchBean, String qtAccessPrefix) {
		
		QTSearchBean qtSearchBean = reqDTO.getQtnTenderSearchBean();
		Map<MapKeyEnum, Object> args = reqDTO.getArgs();
		
		
		//String qtAccessPrefix = "scLoiLoa.scQt.";
		if(!StringUtils.isEmpty(qtAccessPrefix) && !qtAccessPrefix.endsWith(".")){
			qtAccessPrefix += ".";
		}
		if(StringUtils.isEmpty(qtAccessPrefix)){
			qtAccessPrefix = null;
		}
		
		// title
		String qtTitle = qtSearchBean.getQtTitle();
		if (!StringUtils.isBlank(qtTitle)) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(qtAccessPrefix+"qtTitle",
							SearchOperator.OPERATOR_LIKE.value(), qtTitle,
							true));
		}
		
		
		// procurement mode
		String procurementMode = qtSearchBean.getProcurementMode();

		// #30418, #25201 -start-
		if (!StringUtils.isBlank(procurementMode) && getManagedSearchBeanDocumentTypeIsLOI()) {
			// #30418, #25201 -end-
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(qtAccessPrefix+"procurementMode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							procurementMode));
		} else {
			// Filter By - procurementMode
			List<String> procurementModes = new ArrayList<String>();
			procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
			procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
			//if (!isQTMaintenanceScreen()) {
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
			//}
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							qtAccessPrefix+"procurementMode.parameterCode",
							SearchOperator.OPERATOR_IN.value(), 
							procurementModes));
		}


		// qt Created Date
		Date fromCreatedDate = qtSearchBean.getFromCreatedDate();
		Date toCreatedDate = qtSearchBean.getToCreatedDate();
		if (null != fromCreatedDate || toCreatedDate != null) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(fromCreatedDate);
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
					c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			fromDate.set(Calendar.MILLISECOND, 0);
			fromCreatedDate = fromDate.getTime();
			if (toCreatedDate != null) {
				Calendar c2 = Calendar.getInstance();
				c2.setTime(toCreatedDate);
				Calendar toDate = Calendar.getInstance();
				toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				toDate.set(Calendar.MILLISECOND, 0);
				toCreatedDate = toDate.getTime();
			}
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField(qtAccessPrefix+"createdDate");
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					fromCreatedDate, toCreatedDate);
			bean.setSearchValue(betweenVal);
			searchBean.getDataFilterBeans().add(bean);
		}
		
		// publish date
		Date fromPublishDate = qtSearchBean.getFromPublishDate();
		Date toPublishDate = qtSearchBean.getToPublishDate();
		if (null != fromPublishDate || null != toPublishDate) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(fromPublishDate);
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
					c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			fromDate.set(Calendar.MILLISECOND, 0);
			fromPublishDate = fromDate.getTime();
			if (toPublishDate != null) {
				Calendar c2 = Calendar.getInstance();
				c2.setTime(toPublishDate);
				Calendar toDate = Calendar.getInstance();
				toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				toDate.set(Calendar.MILLISECOND, 0);
				toPublishDate = toDate.getTime();
			}
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField(qtAccessPrefix+"publishDate");
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					fromPublishDate, toPublishDate);
			bean.setSearchValue(betweenVal);
			searchBean.getDataFilterBeans().add(bean);
		}
		
		
		
		// closingDate
		Date fromClosingDate = qtSearchBean.getFromClosingDate();
		Date toClosingDate = qtSearchBean.getToClosingDate();
		if (null != fromClosingDate || null != toClosingDate) {
			Calendar c1 = Calendar.getInstance();
			c1.setTime(fromClosingDate);
			Calendar fromDate = Calendar.getInstance();
			fromDate.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH),
					c1.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			fromDate.set(Calendar.MILLISECOND, 0);
			fromClosingDate = fromDate.getTime();
			if (toClosingDate != null) {
				Calendar c2 = Calendar.getInstance();
				c2.setTime(toClosingDate);
				Calendar toDate = Calendar.getInstance();
				toDate.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH),
						c2.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				toDate.set(Calendar.MILLISECOND, 0);
				toClosingDate = toDate.getTime();
			}
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField(qtAccessPrefix+"closingDate");
			bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
			SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
					fromClosingDate, toClosingDate);
			bean.setSearchValue(betweenVal);
			searchBean.getDataFilterBeans().add(bean);
		}
		
		
		// qtStatus
		// only when docType == qt or default
		String status = qtSearchBean.getQtStatus();
		if (qtSearchBean.isQtStatusForQt() && !StringUtils.isBlank(status)) {
			SearchMultiFilterBean statusBean = null;
			SearchMultiFilterBean statusFilterBean = new SearchMultiFilterBean(
					new SearchFilterBean(
							qtAccessPrefix+"scQtStatus.pmStatus.statusId",
							SearchOperator.OPERATOR_EQUALS.value(),
							status,
							JoinType.LEFT), 
					new SearchFilterBean(
							qtAccessPrefix+"scQtStatus.current",
							SearchOperator.OPERATOR_EQUALS.value(),
							true,
							JoinType.LEFT));
			statusBean = statusFilterBean;
			if(status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION.toString())) {
				statusBean = new SearchMultiFilterBean(
						statusFilterBean, 
						new SearchFilterBean(
								qtAccessPrefix+"scQtStatus.pmStatus",
								SearchOperator.OPERATOR_IS_NULL.value(),
								null,
								SearchCondition.CONDITION_OR.value(),
								JoinType.LEFT));
			}
			searchBean.getDataFilterBeans().add(statusBean);
		}
		//two
		if (args!=null && new MapKeyEnum.ValueRetriever<Boolean>().getValue(args, MapKeyEnum.isMofSupplier, Boolean.FALSE)) {
			// Filter By - supplierId
			if (args.containsKey(MapKeyEnum.supplierIds) 
					&& CollectionUtils.isNotEmpty((List<Long>)args.get(MapKeyEnum.supplierIds))) {
				/*
				 * Bug #27412 26/6/2020 #63
				 * CANNOT SPECIFIC BY NORMAL QT SINCE THIS SHARING WITH DIRECT
				 * DIRECT DONT HAVE SC_QT_SUPPLIER 
				 */
				/*searchBean.getDataFilterBeans().add(new SearchFilterBean(
						qtAccessPrefix+"scQtSuppliers.smSupplier.supplierId",
						SearchOperator.OPERATOR_IN.value(), 
						args.get(MapKeyEnum.supplierIds)));*/
				
			}

		} else {
			PmOrgProfile ptjProfiles = qtSearchBean.getPtjProfile();
			if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
				// Filter By - PTJ
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						qtAccessPrefix+"pmOrgProfile." + OrgProfileUtil
							.getSearchBeanParentString(OrganizationType.DIVISION.value()),
						SearchOperator.OPERATOR_EQUALS.value(),
						ptjProfiles.getOrgProfileId()));
			} else {
				// Filter By - PTJ Group
				PmOrgProfile ptjGroupProfile = qtSearchBean.getPtjGroupProfile();
				if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean(
							qtAccessPrefix+"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjGroupProfile.getOrgProfileId()));
				} else {
					// Filter By - Ministry
					PmOrgProfile ministryProfile = qtSearchBean.getMinistryProfile();
					if (ministryProfile != null
							&& ministryProfile.getOrgProfileId() != null) {	
						searchBean.getDataFilterBeans().add(new SearchFilterBean(
								qtAccessPrefix+"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryProfile.getOrgProfileId()));
					}
				}
			}
		}
	}
	
	// #Enhancement 9 CRs - end

	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void postUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("postUpdateOrgProfileListener");
		
		if (orgProfile instanceof PmOrgProfile) {
			PmOrgProfile pmOrgProfile = (PmOrgProfile)orgProfile;
			String orgType = pmOrgProfile.getOrgTypeCode().getParameterCode();
			QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
			
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);
				qtnTenderSearchBean.setPtjGroupProfile(ministryOrgProfile);
				
				postUpdateOrgProfileListener(ministryOrgProfile);
				
			} else { 
				if (qtnTenderSearchBean.getMinistryProfile() == null || 
						qtnTenderSearchBean.getMinistryProfile().getOrgProfileId() == null) {
					qtnTenderSearchBean.setMinistryProfile(retrieveMinistryOrgProfile(
							pmOrgProfile.getOrgProfileId()));	
				}
				
				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					qtnTenderSearchBean.setPtjProfile(new PmOrgProfile());
					
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					if (qtnTenderSearchBean.getPtjGroupProfile() == null || 
							qtnTenderSearchBean.getPtjGroupProfile().getOrgProfileId() == null) {
						qtnTenderSearchBean.setPtjGroupProfile(retrieveJabatanOrgProfile(
								pmOrgProfile.getOrgProfileId()));	
					}
				}
			}
				
		}
	}

	/**
	 * Checks if is Auditor General/Internal user.
	 * 
	 * @return true, if is Auditor General/Internal user
	 */
	public boolean isAuditor() { //#17376-144
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
				|| facesScopes.hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| facesScopes.hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}

	// BUG 32050, 32209 -start-
	/**
	 * BUG 32050, BUG 32209
	 * Checks if QT/DL is UPDATABLE or NOT.
	 * 
	 * @return true, if its not updatable
	 */
	public boolean isUpdatable(Long statusId, String docNo) {
		Boolean returnVal = Boolean.FALSE;

		switch (statusId.equals(QTStatusConst.QT_ARCHIVED) ? StringUtils.substring(docNo, 0, 1) : StringUtils	.substring(docNo, 0, 2)) {
		case ParameterConst.DOCUMENT_CATEGORY_DIRECT_LOA_FOR_QUOTATION_TENDER:
			returnVal = Boolean.TRUE;
			break;
		case ParameterConst.DOCTYPE_QUOTATION:
			returnVal = Arrays.asList(QTStatusConst.UNUPDATABLE_BUTTON_STATUSES).contains(statusId);
			break;
		case ParameterConst.DOCTYPE_ARCHIVED:
			returnVal = Arrays.asList(QTStatusConst.UNUPDATABLE_BUTTON_STATUSES).contains(statusId);
			break;
		}

		return returnVal;
	}
	// BUG 32050, 32209 -end-
	
	// Bug #32425, #19336 - Start
	private boolean isQcaSecretary() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.QCA_SECRETARY.value());
	}

	private boolean isQcbSecretary() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.QCB_SECRETARY.value());
	}
	// Bug #32425, #19336 - End
	
	public boolean isDeskOfficer() {//35142
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	
	private boolean isPbbSecretary() {
		FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.PPB_SECRETARY.value());  //#27668
	}
	
	/*
	 * Enhancement #27668 START
	 * To Cater FPB SECRETARY DROPDOWN LIST
	 */
	private boolean isFpbSecretary() {
		FacesScopes facesScopes = new FacesScopes();
		return  facesScopes.hasRoleUser(RoleCode.FPB_SECRETARY.value());  
	}
	/*
	 * Enhancement #27668 END
	 */
	
	/* Bug #27412 START - CHANGE PRIVATE TO PUBLIC TO ALLOWED METHOD CALLED FROM FRONT*/
	public boolean isMofSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}
	
	//Bug_33484 - start
	public boolean isApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.COMMITTEE_APPROVER.value())
				|| facesScopes.hasRoleUser(RoleCode.PUBLICATION_APPROVER.value());
	}
	//Bug_33484 - end
	
	private void updateApprovalRequestDtls(List<ScQtApproval> approval,
			String approvalCategory) {

		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {

				if (approvalCategory.equalsIgnoreCase(approval.get(i)
						.getApprovalCategory().getParameterCode())) {
					finalizeRemarks(approval.get(i).getScQtApprovalRemarks());
					List<ScQtApprovalRequest> approvalRequests = approval
							.get(i).getScQtApprovalDtls();
					if (CollectionUtils.isNotEmpty(approvalRequests)) {
						for (int j = 0; j < approvalRequests.size(); j++) {
							PmParameter approvalAction = approvalRequests
									.get(j).getApproverAction();
							if (null != approvalAction
									&& ParameterConst.APPROVAL_DECISION_TYPE_REVISED.equalsIgnoreCase(approvalAction
											.getParameterCode())) {
								approvalRequests.get(j).setSubmissionDate(null);
								approvalRequests.get(j).setApproverAction(null);
							}
						}
					}
				}
			}
		}
	}

	private void updateApprovalDetails(ScQt scQt) {
		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();

		if (CollectionUtils.isNotEmpty(tabList)) {
			List<ScQtApproval> approvalList = null;
			approvalList = new ArrayList<ScQtApproval>();
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(
							tabBean.getBudgetApprovalUsers().getTarget());
				}

				tabBean.getScQtApproval().setScQtApprovalRemarks(
						finalizeRemarks(tabBean.getScQtApproval()
								.getScQtApprovalRemarks()));
				// setUserDetails(tabBean.getScQtApproval());

				approvalList.add(tabBean.getScQtApproval());

			}
			scQt.setScQtApprovals(approvalList);
		}

	}

	/**
	 * Set required data for Tracking Dairy info of QT
	 * 
	 * @param scQt
	 */
	private void setScQtTrackingDairyObject(ScQt scQt) {

		Object[] scTdObj = new Object[7];
		scTdObj[0] = scQt.getQtId();
		scTdObj[1] = scQt.getQtNo();
		scTdObj[2] = scQt.getCreatedDate();
		scTdObj[3] = ParameterConst.DOCTYPE_QUOTATION;
		scTdObj[4] = scQt.getQtStatus().getStatusId();
		scTdObj[5] = scQt.getQtTitle();
		this.getManagedBean().setScTdObj(scTdObj);
	}
	
	/**
	 * Check in the MaintenanceFlow ChecklistStatus. 
	 * 
	 * @return the true or false.
	 */
	public boolean isMaintancePendingChklistStatus(){
		
		boolean isPresent = false;
		if (this.getManagedBean().isMaintanceFlow()) {
			Long statusId = this
					.getManagedBean().getQt().getQtStatus().getStatusId();
			if (QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING.equals(statusId) ||
					QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT.equals(statusId)	||
						QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF.equals(statusId)||
						QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION.equals(statusId)) {
				isPresent = true;
			}
		}
	return isPresent;	
	}
	
	/**
	 *manipulate the activeIndex num.
	 * @param activeIndex
	 * @return the current activeIndex.
	 */
	public int manipulateIndexNumber(int activeIndex){
		
		int changedIndex = activeIndex;
		if (isMaintancePendingChklistStatus()) {
			if (activeIndex == 4) {
				changedIndex = QTConstants.DEDUCTION_TAB_INDEX;
			} else if (activeIndex == 5) {
				changedIndex = QTConstants.DOCUMENTS_TAB_INDEX;
			} else if (activeIndex == 6) {
				changedIndex = QTConstants.ACTIVE_TAB_INDEX;
			} 
		}
		return changedIndex;
		
	}
	
 private boolean isStatusOfBpkOrScheduleRevise(Long statusId) {
		
		boolean isStatusOfBpkOrScheduleRevise = false;
		if (!this.getManagedBean().isMaintanceFlow()) {
			if (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(statusId)
					|| QTStatusConst.QT_PENDING_PB_APPROVAL.equals(statusId)
					|| QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(statusId)
					|| QTStatusConst.QT_PENDING_DOCUMENT_REVISION.equals(statusId)) {
				isStatusOfBpkOrScheduleRevise = true;
			}
		}
		return isStatusOfBpkOrScheduleRevise;
	}
 
 /**
	 * Retrieve non zonal dtls.
	 */
	private void retrieveNonZonalDtls() {
		log.debug("retrieveNonZonalDtls - START");
		ScQt scQt = this.getManagedBean().getQt();
		if (null != scQt.getZoneLocationLevel()
				&& null != scQt.getZoneLocationLevel()
						.getParameterId()) {
			Long locationLevel = scQt
					.getZoneLocationLevel().getParameterId();
			scQt.getScQtSupplierCriteria()
					.getLocationLevel().setParameterId(locationLevel);
			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			String locationLevelCode = null;
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId()
						.equals(locationLevel)) {
					locationLevelCode = paramDesc.getPmParameter()
							.getParameterCode();
					this.getManagedBean().setLocalLevelCode(locationLevelCode);
					scQt.getScQtSupplierCriteria().setLocationLevel( paramDesc.getPmParameter());
					
					break;
				}
			}
			
			if (!ParameterConst.PROCUREMENT_MODE_QUOTATION.equals(scQt.getProcurementMode().getParameterCode())) {
				List<ScQtLocCriteria> scQtLocCriteriaList = new ArrayList<ScQtLocCriteria>();
				if (null != getManagedBean().getZoneGeneralTrees()
						&& !getManagedBean().getZoneGeneralTrees().isEmpty()) {
					for (TreeNode treeNode : getManagedBean().getZoneGeneralTrees()) {
						List<ScQtLocCriteria> qtLocCriteriaList = getLocationsList(treeNode);
						scQtLocCriteriaList.addAll(qtLocCriteriaList);
					}
				}
				retrievLocationDtls(scQtLocCriteriaList);	
			}
		}
		log.debug("retrieveNonZonalDtls - END");
	}
 
 /**
	 * Retriev location dtls.
	 * 
	 * @param scQtLocCriteriaList
	 *            the sc qt loc criteria list
	 */
 
 
	private void retrievLocationDtls(List<ScQtLocCriteria> scQtLocCriteriaList) {
		log.debug("retrievLocationDtls - START");
		List<PmState> states = SourcingLocationHelper
				.getStateFromLocationDTO(scQtLocCriteriaList);
		List<PmDivision> divisions = SourcingLocationHelper
				.getDivisionFromLocationDTO(scQtLocCriteriaList);
		List<PmDistrict> districts = SourcingLocationHelper
				.getDistrictFromLocationDTO(scQtLocCriteriaList);
		List<PmCity> cities = SourcingLocationHelper
				.getCityFromLocationDTO(scQtLocCriteriaList);
		getManagedBean().setZoneTrees(new ArrayList<TreeNode>());
		if (ParameterConst.LOCATION_LEVEL_STATE.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != states && !states.isEmpty()) {
				for (PmState state : states) {
					List<PmState> localStateList = new ArrayList<PmState>();
					localStateList.add(state);
					getManagedBean().getZoneTrees().add(
							LocationSingleton.getInstance().buildLocationTree(
									null, localStateList, districts, divisions,
									cities, ScQtLocCriteria.class));
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DISTRICT.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != districts && !districts.isEmpty()) {
				Map<PmState, Map<PmDivision, Map<PmDistrict, List<PmCity>>>> mapStatesDivisionsDistrictsCities = LocationSingleton
						.getInstance().getUniqueStatesDivisionsDistrictsCities(
								districts, null);
				Set<PmState> statesDivisionsDistrictsCitiesKeySet = mapStatesDivisionsDistrictsCities
						.keySet();
				Map<PmState, Map<PmDistrict, List<PmCity>>> mapStatesDistrictsCities = LocationSingleton
						.getInstance().getUniqueStateDistrictsCities(districts,
								null);
				Set<PmState> statesDistrictsCitiesKeySet = mapStatesDistrictsCities
						.keySet();
				if (statesDivisionsDistrictsCitiesKeySet != null
						&& !statesDivisionsDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDivisionsDistrictsCitiesKeySet) {
						Map<PmDivision, Map<PmDistrict, List<PmCity>>> mapDivisionsDistrictsCities = mapStatesDivisionsDistrictsCities
								.get(state);
						Set<PmDivision> divisionsDistrictsCitiesKeySet = mapDivisionsDistrictsCities
								.keySet();
						if (divisionsDistrictsCitiesKeySet != null
								&& !divisionsDistrictsCitiesKeySet.isEmpty()) {
							divisions.clear();
							districts.clear();
							for (PmDivision division : divisionsDistrictsCitiesKeySet) {
								division.setPmState(state);
								Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapDivisionsDistrictsCities
										.get(division);
								Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
										.keySet();
								if (districtsCitiesKeySet != null
										&& !districtsCitiesKeySet.isEmpty()) {
									for (PmDistrict district : districtsCitiesKeySet) {
										districts.add(district);
									}
								}
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
				if (statesDistrictsCitiesKeySet != null
						&& !statesDistrictsCitiesKeySet.isEmpty()) {
					for (PmState state : statesDistrictsCitiesKeySet) {
						divisions.clear();
						Map<PmDistrict, List<PmCity>> mapDistrictsCities = mapStatesDistrictsCities
								.get(state);
						Set<PmDistrict> districtsCitiesKeySet = mapDistrictsCities
								.keySet();
						if (districtsCitiesKeySet != null
								&& !districtsCitiesKeySet.isEmpty()) {
							districts.clear();
							for (PmDistrict district : districtsCitiesKeySet) {
								district.setPmState(state);
								districts.add(district);
							}
						}
						getManagedBean().getZoneTrees().add(
								LocationSingleton.getInstance()
										.buildLocationTree(null, null,
												districts, divisions, cities,
												ScQtLocCriteria.class));
					}
				}
			}
		} else if (ParameterConst.LOCATION_LEVEL_DIVISION.equalsIgnoreCase(this
				.getManagedBean().getLocalLevelCode())) {
			if (null != divisions && !divisions.isEmpty()) {
				Map<PmState, List<PmDivision>> mapStatesDivision = LocationSingleton
						.getInstance().getUniqueStatesDivision(divisions);
				Set<PmState> statesDivisionKeySet = mapStatesDivision.keySet();
				if (statesDivisionKeySet != null
						&& !statesDivisionKeySet.isEmpty()) {
					for (PmState state : statesDivisionKeySet) {
						divisions.clear();
						List<PmDivision> divisions1 = mapStatesDivision
								.get(state);
						if (divisions1 != null && !divisions1.isEmpty()) {
							for (PmDivision division : divisions1) {
								division.setPmState(state);
								divisions.add(division);
							}
							getManagedBean().getZoneTrees().add(
									LocationSingleton.getInstance()
											.buildLocationTree(null, null,
													districts, divisions,
													cities,
													ScQtLocCriteria.class));
						}
					}
				}
			}
		}
		log.debug("retrievLocationDtls - END");
	}
	/**
	 * Gets the locations list.
	 * 
	 * @param zoneLocation
	 *            the zone location
	 * @return the locations list
	 */
	private List<ScQtLocCriteria> getLocationsList(TreeNode zoneLocation) {
		List<ScQtLocCriteria> results = new ArrayList<ScQtLocCriteria>();
		for (TreeNode zoneNode : zoneLocation.getChildren()) {
			for (TreeNode stateNode : zoneNode.getChildren()) {
				if (stateNode.getType().equals("state")) {

					TplSpecLoc stateLoc = (TplSpecLoc) stateNode.getData();
					ScQtLocCriteria stateLocCriteria = new ScQtLocCriteria();
					stateLocCriteria.setPmState(stateLoc.getPmState());
					// stateLocCriteria.setPmDistrict(stateLoc.getPmDistrict());
					// stateLocCriteria.setPmDivision(stateLoc.getPmDivision());
					// ScQtLocCriteria locationDTO = (ScQtLocCriteria)
					// zoneLocation.getData();
					results.add(stateLocCriteria);

					for (TreeNode districtNode : stateNode.getChildren()) {
						TplSpecLoc districtLoc = (TplSpecLoc) districtNode
								.getData();
						ScQtLocCriteria districtLocCriteria = new ScQtLocCriteria();
						// districtLocCriteria.setPmState(districtLoc.getPmState());
						districtLocCriteria.setPmDistrict(districtLoc
								.getPmDistrict());
						districtLocCriteria.setPmDivision(districtLoc
								.getPmDivision());
						results.add(districtLocCriteria);
						for (TreeNode divisionCode : districtNode.getChildren()) {
							TplSpecLoc divisionLoc = (TplSpecLoc) divisionCode
									.getData();
							ScQtLocCriteria divisionLocCriteria = new ScQtLocCriteria();
							divisionLocCriteria.setPmDistrict(divisionLoc
									.getPmDistrict());
							results.add(divisionLocCriteria);
						}
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * is Allow QT Closed button
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public boolean isAllowQTClosedBtn(ScQt scQt) {
		if (scQt != null && 
				scQt.getQtStatus() != null && 
						scQt.getQtStatus().getStatusId() != null &&
				QTStatusConst.QT_PROPOSAL_PREPARATION.equals(scQt.getQtStatus().getStatusId())) {
			String isAllowQTClosedBtn = EpPropertiesUtils.getInstance()
					.getProperty(EpPropertiesConst.SC_SHOW_BUTTON_CLOSEQT);
			
			if (StringUtils.isNotBlank(isAllowQTClosedBtn)) {
				return Boolean.valueOf(isAllowQTClosedBtn);
			}
		}		
		
		return false;
	}
	
	/**
	 * is Allow QT Publish button
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public boolean isAllowQTPublishBtn(ScQt scQt) {
		if (scQt != null && 
				scQt.getQtStatus() != null && 
						scQt.getQtStatus().getStatusId() != null &&
				QTStatusConst.QT_PENDING_PUBLICATION.equals(scQt.getQtStatus().getStatusId())) {
			String isAllowQTPublishBtn = EpPropertiesUtils.getInstance()
					.getProperty(EpPropertiesConst.SC_SHOW_BUTTON_CLOSEQT);
			
			if (StringUtils.isNotBlank(isAllowQTPublishBtn)) {
				return Boolean.valueOf(isAllowQTPublishBtn);
			}
		}		
		
		return false;
	}
	
	/**
	 * is Allow QT To cancel
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public void checkIsQTAllowCancel() {
		debugLogger("checkIsQTAllowCancel");
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		boolean isQTAwarded = false;
		boolean isBiddingActive = false;
		boolean isQTPendingCancellation = false;
		
		IScWorkFlowStatusSO workFlowStatusSO;
		try {
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QT_CANCELLATION);
			reqDTO.setDocId(scQt.getQtId());

			ScWorkFlowStatus cancellationStatus = workFlowStatusSO.
					retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			
			if(cancellationStatus != null && 
					cancellationStatus.getPmStatus().getStatusId().equals(QTStatusConst.QC_PENDING_CANCELATON)) {
				isQTPendingCancellation = true;
			}
		}
		finally {
			workFlowStatusSO = null;
		}
		
		if(statusId.equals(QTStatusConst.QT_PENDING_LOI) || statusId.equals(QTStatusConst.QT_PENDING_LOA) ||
				statusId.equals(QTStatusConst.QT_SUPPLIER_FINALIZATION) || statusId.equals(QTStatusConst.QT_RE_FINALIZATION)) {
			isQTAwarded = checkIsQTAwarded(scQt);
			if(statusId.equals(QTStatusConst.QT_SUPPLIER_FINALIZATION)) {
				isBiddingActive = checkIsBiddingActive(scQt);
			} 
		}
		
		if(isQTAwarded) {
			throw new EpException(MessageConst.ERROR_760425);
		} else if(isBiddingActive) {
			throw new EpException(MessageConst.ERROR_760424);
		} else if(isQTPendingCancellation) {
			throw new EpException(MessageConst.ERROR_760435);
		} else {
			RequestContext.getCurrentInstance().execute("cancelQTDlg.show()");
		}
	}
	
	/**
	 * Trigger to close the supplier prepare proposal.
	 * 
	 * @param qtId
	 * 		the quotation / tender id
	 */
	public void closeProposal(ScQt scQt) {		
		ScRetryResDTO resDTO = saveAndSubmitQTDisruption(scQt, 
				ScDisruptionConst.QT_CLOSE_WORK_FLOW_DISRUPTION, 
				"Manual Close QT (" + System.getProperty("jboss.node.name") + ").");
		
		if (resDTO.isSuccessful()) {
			IQTProposalSO qtProposalSO;
			try {
				qtProposalSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTProposalSO.class);
				
				ProposalReqDTO reqDTO = new ProposalReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				qtProposalSO.closeProposal(reqDTO);
			
			} finally {
				qtProposalSO = null;
			}
		}
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null, resDTO.getErrorMessage() != null ? resDTO.getErrorMessage() :
						scQt.getQtNo() + " Closing Started. Please wait a while for QT closing."
		));
	}
	
	/**
	 * Trigger to publish Quotation / Tender
	 * 
	 * @param scQt
	 * 		the quotation
	 */
	
	public void publishQT(ScQt scQt) {
		String errorMessage = saveAndSubmitQTDisruption(scQt, 
				ScDisruptionConst.QT_PUBLISH_WORK_FLOW_DISRUPTION, 
				"Manual Publish QT (" + System.getProperty("jboss.node.name") + ").")
				.getErrorMessage();
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(
						FacesMessage.SEVERITY_INFO,
						null, errorMessage != null ? errorMessage:
						scQt.getQtNo() + " Publishing Started. Please wait a while for QT publishing."
						));
	}
	
	private ScRetryResDTO saveAndSubmitQTDisruption(ScQt scQt, Integer disruptionStage, String errorMsg) {
		IScRetrySO qtRetrySO;
		try {
			qtRetrySO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScRetrySO.class);
			
			PmUser user = new FacesScopes().getUserSessionBean();
			if (user.getUserId() == null) {
				user.setUserId(1L);
			}
			
			ScRetryReqDTO reqDTO = new ScRetryReqDTO();
			
			// Save QT Disruption
			ScQtDisruptionLog qtDisruptionLog = new ScQtDisruptionLog();
			qtDisruptionLog.setScQt(scQt);
			qtDisruptionLog.setDisruptionStage(disruptionStage);
			qtDisruptionLog.setErrorMsg(errorMsg);
			qtDisruptionLog.setCreatedBy(user.getUserId());
			qtDisruptionLog.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			qtDisruptionLog.setChangedBy(user.getUserId());
			reqDTO.setScDisruptionLog(qtDisruptionLog);
			qtDisruptionLog = (ScQtDisruptionLog)qtRetrySO.saveScDisruption(reqDTO)
				.getScDisruptionLog();
			
			// Perform QT Retry
			reqDTO.setScDisruptionLog(qtDisruptionLog);
			reqDTO.setDisruptionStage(qtDisruptionLog.getDisruptionStage());
			reqDTO.setPmUser(user);
			return qtRetrySO.retryScService(reqDTO);
		
		} finally {
			qtRetrySO = null;
		}
	}
	
	
	/**
	 * Gets the doc type qt loa dropdown values.
	 *
	 * @return the doc type qt loa dropdown values
	 */
	public SelectItem[] getDocTypeQtLOADropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> qtLoaDoctypes = new ArrayList<String>();	
		
		// #Enhancement 9 CRs - start
		//filter document type search by role.
		//26259
		boolean pbbSecretaryOnly = false; 
		boolean qcSecretaryOnly = false; //#32425, #19336
		
		if (isPbbSecretary() && !(isDeskOfficer() || isAuditor() || isMofSupplier())){
			pbbSecretaryOnly = true;

			/*
			 * Enhancement #27668 TO CATER FPB SECRETARY DROPDOWN LIST -start-
			 */
		} else if (isFpbSecretary() && !(isDeskOfficer() || isAuditor() || isMofSupplier())) {
			pbbSecretaryOnly = true;
			// #27668 -end-
		}// Bug #32425, #19336 - Start
		else if (isQcaSecretary() || isQcbSecretary()) {
			if (!(isDeskOfficer() || isAuditor() || isMofSupplier())) {
				qcSecretaryOnly = true;
			}
		}
		// Bug #32425, #19336 - End
		
		if (pbbSecretaryOnly) {
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
		}
		// Bug #32425, #19336 - Start
		else if (qcSecretaryOnly) {
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
		}// Bug #32425, #19336 - End
		else if(isMofSupplier()){
			/*Bug #27412 START*/
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL);
			/*Bug #27412 END */
		}else {
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_BIDDING);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_QUOTATION);
			
			if (isDeskOfficer() || isAuditor()) {
				qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
			}			
			if (isMofSupplier()) {
				qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL);
			}	
		}
		// #Enhancement 9 CRs - end		
													 
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), qtLoaDoctypes));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
  
		return items;
	}
	
	/**
	 * Remove pmUser if contains loggined in Desk Officer
	 * 
	 * @param approvers
	 */
	private void removeDeskOfficer(List<PmUser> approvers) {
      log.debug("removeDeskOfficer");
		if (CollectionUtils.isNotEmpty(approvers)) {
			PmUser currentDO = new FacesScopes().getUserSessionBean();
			//boolean isDO = false;
			//int index = 0;
			for (PmUser pmUser : approvers) {
				if (pmUser.getUserId().equals(currentDO.getUserId())) {
					//isDO = true;
					//index++;
					approvers.remove(pmUser);
					break;
				}
			}
			/*if (isDO) {
				approvers.remove(index);
			}*/
		}
		log.debug("endremoveDeskOfficer");
	}
	
	private boolean checkIsQTAwarded(ScQt scQt) {
		IQTSuppFinalizationSO suppFinalizationSO;
		try {
			boolean isQTAwarded = false;
			suppFinalizationSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
			
			SuppFinalizationReqDTO reqDTO = new SuppFinalizationReqDTO();
			reqDTO.setQtId(scQt.getQtId());
			
			List<ScQtRecommendDtl> recommendDtls = suppFinalizationSO.getRecommendDtlByQTandSupplier(reqDTO).
					getScQtRecommendDtlList();
			for (ScQtRecommendDtl recommendDtl 
					: recommendDtls) {
				Long recommendDtlId = recommendDtl.getRecommendDtlId();

				// If LOI awarded and no decision made on LOA (or) 
				// if LOI awarded and LOA awarded,
				// then consider the item as awarded
				if (recommendDtlId != null) {
					if ( (BooleanUtils.isTrue(
							recommendDtl.getLoiAwarded()) && 
							recommendDtl.getLoaAwarded() == null && 
							recommendDtl.getRecordStatusFlg().equals(
									DbValueConst.RECORD_STATUS_ACTIVE)) ||
											(BooleanUtils.isTrue(
													recommendDtl.getLoiAwarded()) && 
													BooleanUtils.isTrue(
															recommendDtl.getLoaAwarded())) ) {
						isQTAwarded = true;
					}
				}
			}
			return isQTAwarded;
		} finally {
			suppFinalizationSO = null;
		}
	}
	
	private boolean checkIsBiddingActive(ScQt scQt) {
		IBiddingSO biddingSO;
		try {
			boolean isBiddingActive = false;
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
			
			BiddingReqDTO reqDTO = new BiddingReqDTO();	
			
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(BidStatusConst.BID_COMPLETED);
			statusIds.add(BidStatusConst.BID_CANCELLED);
			
			ISearchBean<ScBidSchedule> bidSchSearchBean = new SearchBean<ScBidSchedule>();
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_QUOTATION));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docId", SearchOperator.OPERATOR_EQUALS.value(), scQt.getQtId()));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmStatus.statusId", SearchOperator.OPERATOR_NOT_IN.value(), statusIds));
			reqDTO.setBidSchSearchBean(bidSchSearchBean);

			BiddingResDTO resDTO = biddingSO.searchBiddingScheduleList(reqDTO);
			List<ScBidSchedule> list = resDTO.getBidSchResultBean().getResultList();
			if(list.size() > 0 ) {
				isBiddingActive = true;
			}
			return isBiddingActive;
		} finally {
			biddingSO = null;
		}
	}
	
	
	private void removeCommitteeActivityDetails(ScQt scQt){
		IQTMeetingSO meetingso=null;
		try{
		 meetingso = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTMeetingSO.class);
		 List<String> committeeType=new ArrayList<String>();
		 if(ParameterConst.EVALUATION_TYPES_ONE_TIER.equalsIgnoreCase(scQt.getEvaluationType())){
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE);
		 }else{
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE); 
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE); 
			 committeeType.add(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE); 
		 }
		 for(String type:committeeType){
		MeetingReqDTO activityReqDTo = new MeetingReqDTO();
		activityReqDTo.setQtId(scQt.getQtId().longValue());
		activityReqDTo.setCommitteType(type);
		MeetingResDTO activityResDTo = meetingso
				.retriveCommitteActivityDtls(activityReqDTo);
		if (null != activityResDTo
				&& !activityResDTo.getCommitteMembers().isEmpty()) {
			MeetingReqDTO deleteActivityDao = new MeetingReqDTO();
			List<ScQtCommitteeActivity> deleteActivityList = new ArrayList<ScQtCommitteeActivity>();
			for (ScQtCommitteeActivity activity : activityResDTo
					.getCommitteMembers()) {
				if (!activity.isBrandNew()) {
					// activity.setScCommitteeMember(null);
					activity.setDeleted(true);
					deleteActivityList.add(activity);
				}
			}
			deleteActivityDao
					.setDeletedCommitteeActivityMembersList(deleteActivityList);
			meetingso.deleteCommitteeActivityDtls(deleteActivityDao);
		}
		}
		}finally{
			meetingso=null;
		}
	}

	// #Enhancement 9 CRs - start	
	/**
	 * find isCurrent only, 
	 * @param wsLst
	 * @return statusId
	 */
	public Long getStatusIdToDisplay(List<ScWorkFlowStatus> wsLst){
		for(int i = 0; wsLst!=null && i< wsLst.size(); i++){
			if(!wsLst.get(i).isCurrent()){
				wsLst.remove(i);
				i--;
			}
		}
		if(wsLst!=null&&!wsLst.isEmpty()){
			if(getManagedBean().getQtnTenderSearchBean().getQtStatus() != null){
				if(wsLst!=null){
					for(ScWorkFlowStatus wss: wsLst){
						if(wss.getPmStatus().getStatusId().longValue() == Long.parseLong(getManagedBean().getQtnTenderSearchBean().getQtStatus())){
							return wss.getPmStatus().getStatusId();
						}
					}
				}
			}
			else{
				Collections.sort(wsLst, new WorkflowStatusComparator.SortByChangedDateDescComparator());
				return wsLst.get(0).getPmStatus().getStatusId();
			}
			
		}		
		return -1L;
	}
																							   
	// #Enhancement 9 CRs - end
	
	public SelectItem[] getQTStatusDropdownValues(String moduleCode,
			String docType, String screenName) {

		SelectItem[] statusList = null;
		if (QTConstants.SPEC_MAINTENANCE.equalsIgnoreCase(screenName)) {
			
			SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmStatus.statusId",
							SearchOperator.OPERATOR_NOT_IN.value(),
							nonRecommendedStatusesForSpecMaint()));

			statusList = getStatusDropdownValuesBySearchBean(moduleCode,
					docType, searchBean);
			// #30451-Start.
		} else if ((isQTMaintenanceScreen() || isQTSearchScreen()) && docType.equals(ParameterConst.DOCTYPE_BIDDING)) {
			SearchBean<PmStatusDesc> searchBean = new SearchBean<PmStatusDesc>();
			// Filter By - statusId
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(QTStatusConst.SF_PENDING_SCHEDULE_BIDDING);
			statusIds.add(QTStatusConst.SF_PENDING_BIDDING);
			statusIds.add(QTStatusConst.SF_PENDING_BIDDING_ESCALATION);
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmStatus.statusId", SearchOperator.OPERATOR_IN.value(), statusIds));
			
			docType = ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION;
			statusList = super.getStatusDropdownValuesBySearchBean(moduleCode, docType, searchBean);
		}
		// #30451-End.
		else {
			statusList = getStatusDropdownValues(moduleCode, docType);
		}
		
		
		// ***17376#32
		/**
		 * 17376#32 temporary fix
		 */		
		if(statusList != null && statusList.length>0){
			List<SelectItem> t = new ArrayList<SelectItem>(Arrays.asList(statusList));
			for(int i = 0; t!=null && i<t.size(); i++){
				try{
					if(t.get(i).getValue()!=null && Long.parseLong(t.get(i).getValue().toString()) == 60039L){
						t.remove(i);
						i--;
					}
				}
				catch(ClassCastException ex){}
			}
			statusList = new SelectItem[t.size()];
			for(int i = 0; i < statusList.length; i++){
				statusList[i] = t.get(i);
			}
		}
		/** end of 17376#32 temporary fix **/
		
		
		
		return statusList;
	}
	
	private void checkForPublishperiodAndValidityDays(ScQt scQt, FacesContext fc){
		
		if (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(scQt.getQtStatus().getStatusId())) {
			if (null == scQt.getPublishPeriod()) {
				
				addFacesMessage(fc,
						MessageConst.ERROR_770222,
						new String[] { getRuleTypeDesc(scQt.isPanel() ? RuleTypeConst.PANEL_PUBLISHING_PERIOD_DAYS
								: RuleTypeConst.QT_PUBLISHING_PERIOD_DAYS) });
			}

			//29425 - start
//			if (null == scQt.getQtValidityPeriod()) {
//				
//				addFacesMessage(fc,
//						MessageConst.ERROR_770222,
//						new String[] { getRuleTypeDesc(RuleTypeConst.QT_PROPOSAL_VALIDITY_DAYS) });
//			}
			//#29425 - end
		}
	}
	
	
	
	
	/**
	 * The method used set parent profile and reset child profile value
	 * 
	 * @param orgProfile
	 * 		the PmOrgProfile instance
	 */
	public void SpecMaintenacepostUpdateOrgProfileListener(BaseEntity orgProfile) {
		debugLogger("SpecMaintenacepostUpdateOrgProfileListener");
		if (orgProfile instanceof PmOrgProfile) {
			PmOrgProfile pmOrgProfile = (PmOrgProfile)orgProfile;
		IOrgProfileSO orgProfileSO = null;
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		orgProfileSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		
		OrgProfileReqDTO ptjreqDTO = new OrgProfileReqDTO();
		ptjreqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
		PmOrgProfile orgProfileptj = orgProfileSO.retrieveOrgProfileByProfileId(
				ptjreqDTO).getPmOrgProfile();
		log.debug("SpecMaintenacepostUpdateOrgProfileListener"+orgProfileptj);
		qtnTenderSearchBean.setPtjProfile(orgProfileptj);
		}
	}
	/**
	 * The method used to deltecommitteemembersData.
	 * 
	 * @param existingmember
	 * 		the existingmember ScCommitteeMember.
	 */
	public void deleteCommitteemembersData(List<ScCommitteeMember> existingmember){
		if(CollectionUtils.isNotEmpty(existingmember)){
			log.debug("Start deleteCommitteemembersData"+existingmember.size());
			List<ScCommitteeMember> deleteMemberList=new ArrayList<ScCommitteeMember>();
			for(ScCommitteeMember member:existingmember){
				if(member.isSelected()){
					deleteMemberList.add(member);
				}
				
			}
			this.getManagedBean().setCommittemembers(deleteMemberList);
			deleteMemberList=null;
			
		}
		
	}
	
 
	/*
	 * Add attachment to list.
	 * 
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			log.debug("qtDetailhandleFileUpload - START");
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval();
			if (scQtApproval.getScQt() == null) {
				scQtApproval.setScQt(this.getManagedBean().getQt());
			}
			List<ScQtApprovalAttachment> approvalAttachments = scQtApproval
					.getScQtApprovalAttachments();
			if (null == approvalAttachments) {
				approvalAttachments = new ArrayList<ScQtApprovalAttachment>();
			}
			attachment.setScQtApproval(scQtApproval);
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			approvalAttachments.add(attachment);
			scQtApproval.setScQtApprovalAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			this.getManagedBean()
			.getSelectedApprovalTabBean().setScQtApproval(scQtApproval);
			//getManagedBean().setScQtApproval(scQtApproval);
			//attachments.add(attachment);
			//getManagedBean().setAttachments(attachments);
		}
	}
	
	// ***25201#35
	/**
	 * is Allow QT View Supplier Finalization
	 * 
	 * @param scQt
	 * 		the ScQt instance
	 * 
	 * @return
	 */
	public boolean isAllowQTViewSupplierFinalization(ScQt scQt) {
		boolean isAllowQTViewSupplierFinalization = false;
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		if (scQt != null && scQt.getQtStatus() != null && scQt.getQtStatus().getStatusId() != null &&
				ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(docType) &&
				(isQTMaintenanceScreen() || isQTSearchScreen())) {
			isAllowQTViewSupplierFinalization = true;
		}		
		
		return isAllowQTViewSupplierFinalization;
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOILOA(){
		if(	getManagedBean().getQtnTenderSearchBean().getDocType() != null && 
			(	ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
				||  ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			)) return true;
		return false;
	}
	// 25201 - end
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOI(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			) return true;
		return false;
	}
	
	/**
	 * document type in search bean is LOI and prefix of doc no is LI
	 * @return
	 */
	public boolean getManagedSearchBeanDocTypeIsLOIAndPrefixLIAndIsDO(){
		return getManagedSearchBeanDocumentTypeIsLOI() && checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_INTENT) && userHasRole(deskOfficerRoleCodes);
	}
	
	
	/**
	 * document type in search bean is LOA and prefix of doc no is LA
	 * @return
	 */
	public boolean getManagedSearchBeanDocTypeIsLOAAndPrefixLAAndIsDO(){
		return getManagedSearchBeanDocumentTypeIsLOA() && checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_AWARD) && userHasRole(deskOfficerRoleCodes);
	}
	
	private boolean checkSearchBeanDocTypePrefix(String prefix){
		if(prefix == null){
			prefix = "";
		}
		return  ( getManagedBean().getQtnTenderSearchBean()!=null && !StringUtils.isBlank(getManagedBean().getQtnTenderSearchBean().getDocNo()) && getManagedBean().getQtnTenderSearchBean().getDocNo().substring(0, prefix.length()).equalsIgnoreCase(prefix) );
	}

	public Boolean getManagedSearchBeanDocumentTypeIsLOA(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			) return true;
		return false;
	}


	// #Enhancement 9 CRs - start	
	
	public Boolean getManagedSearchBeanDocumentTypeIsSuppFinal(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
				&& ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
				) return true;
			return false;
	}
	
	// #31919 & 21692 - Start
//	public Timestamp getExtensionResponseDueDate(Long qtId) {
//		IScWorkFlowStatusSO workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
//				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
//		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
//		reqDTO.setDocId(qtId);
//		reqDTO.setDocType(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD);
//
//		ScWorkFlowStatus status = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
//		if(status!=null){
//			if(status.isCurrent() 
//					&& (status.getPmStatus().getStatusId().equals(QTStatusConst.EPV_APPROVED)
//					|| status.getPmStatus().getStatusId().equals(QTStatusConst.EPV_PENDING_SUPPLIER_ACCEPTANCE))){
//				Timestamp extensionRespDate = getQTDetailSO().getExtensionResponseDueDate(qtId);
//				return extensionRespDate;
//			}
//		}
//		return null;
//	}
	// #31919 & 21692 - End

	public String getProcurementMode(ScQt qt){
		String method = null;
		if(qt.getLoiLoa()!=null){//check if direct nego
			if(qt.getLoiLoa().getScloas() != null && qt.getLoiLoa().getScloas().size()>0){
				method = this.getParameterDesc(qt.getLoiLoa().getScloas().get(0).getProcurementMode());
			}
		}else{
			method = this.getParameterDesc(qt.getProcurementMode());
		}
		
		return method;
	}
	
	public Boolean getUserTaskIsBPKApproval(){
		return userHasUserTask(UserTasks.BPKApproval);
	}
	
	public Boolean getUserTaskIsCommitteeAppointmentApproval(){
		return userHasUserTask(UserTasks.CommitteeAppointmentApproval);
	}
	
	/**
	 * 25692 - Committee Appointment Approval for EC, OC, TEC, FEC Committee type
	 * @return
	 */
	public Boolean getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec(){
		return userHasUserTask(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
	}
	
	public Boolean getUserTaskIsPublicationApproval(){
		return userHasUserTask(UserTasks.PublicationApproval);
	}
	
	//default page at index 0
	public String getBPKApprovalPage(){
		if(getManagedBean().getAdditionalPages()!=null && getManagedBean().getAdditionalPages().containsKey(UserTasks.BPKApproval.toString())){
			if(!CollectionUtils.isEmpty( getManagedBean().getAdditionalPages().get(UserTasks.BPKApproval.toString()))){
				String t = getManagedBean().getAdditionalPages().get(UserTasks.BPKApproval.toString()).get(0);
				return t;
			}
		}
		return null;
	}
	
	public String getCommitteeAppointmentApprovalPage(){
		if(getManagedBean().getAdditionalPages()!=null && 
				(	getManagedBean().getAdditionalPages().containsKey(UserTasks.CommitteeAppointmentApproval.toString())
				||	getManagedBean().getAdditionalPages().containsKey(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString())
				)){
			if(!(	(CollectionUtils.isEmpty( getManagedBean().getAdditionalPages().get(UserTasks.CommitteeAppointmentApproval.toString())))
				&& 	(CollectionUtils.isEmpty( getManagedBean().getAdditionalPages().get(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString())))
				)){
				String t = null;
				try{
					t = getManagedBean().getAdditionalPages().get(UserTasks.CommitteeAppointmentApproval.toString()).get(0);
				}catch(NullPointerException ex){
					try{
						t = getManagedBean().getAdditionalPages().get(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString()).get(0);
					}catch(NullPointerException e){}
				}
				return t;
			}
		}
		return null;
	}
	
	public String getPublicationApprovalPage(){
		if(getManagedBean().getAdditionalPages()!=null && getManagedBean().getAdditionalPages().containsKey(UserTasks.PublicationApproval.toString())){
			if(!CollectionUtils.isEmpty( getManagedBean().getAdditionalPages().get(UserTasks.PublicationApproval.toString()))){
				String t = getManagedBean().getAdditionalPages().get(UserTasks.PublicationApproval.toString()).get(0);
				return t;
			}
		}
		return null;
	}
	
	
	/*  ***25692 - start */
	public boolean isApprovalScreen(){
		return QTConstants.PORTLET_NAME_APPROVAL.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	public String getMenuItemPublicationApproverAssignment() {
		return menuItemPublicationApproverAssignment;
	}
	
	/**
	 * @param menuItem
	 * @return
	 */
	public boolean displayMenuItem(String menuItem) {
		// 29464 -start-
		String taskName = this.getManagedBean().getTaskName() == null ? "" : this.getManagedBean().getTaskName();
		// 29464 -end-

		Set<String> menuSet = new HashSet<String>();
		menuSet.addAll(
			Arrays.asList(
				getMenuItemDetails(),   //0
				getMenuItemSuppQualificationCriteria(), //1
				getMenuItemCommitteeAppointment(), //2
				getMenuItemApproval(), //3
				getMenuItemBriefSiteVisit(), //4
				getMenuItemPublishDetail(), //5
				getMenuItemPaymentDeduction(), //6
				getMenuItemDocuments(), //7
				getMenuItemActivities(), //8
				getMenuItemPublicationApproverAssignment() //9
			));
		if(getUserTaskIsBPKApproval() && isApprovalScreen()){
			menuSet.remove(getMenuItemPublicationApproverAssignment());
			menuSet.add(getMenuItemBPKApproval());
		}
		else if(getUserTaskIsCommitteeAppointmentApproval() && isApprovalScreen()){
			menuSet.remove(getMenuItemBriefSiteVisit()); //4 from initialized menuSet
			menuSet.remove(getMenuItemPublishDetail()); //5 from initialized menuSet
			menuSet.remove(getMenuItemPaymentDeduction()); //6 from initialized menuSet
			menuSet.remove(getMenuItemDocuments()); //7 from initialized menuSet
			menuSet.remove(getMenuItemPublicationApproverAssignment());
			// ***25692#31 
			menuSet.add(getMenuItemCommitteeApproval());
		}
		else if(getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()){
			menuSet.remove(getMenuItemPublicationApproverAssignment());
			menuSet.add(getMenuItemCommitteeApprovalEcOcTecFec());
		}
		else if(getUserTaskIsPublicationApproval() && isApprovalScreen()){
			menuSet.remove(getMenuItemPublicationApproverAssignment());
			menuSet.add(getMenuItemPublicationApproval());
		}
		// 29464-start
		else if (taskName.equals(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)) {
			menuSet.add(getMenuItemSpecificationCommitteeAssignment());
		}
		;
		// 29464-end
		// ***25692#31
		return menuSet.contains(menuItem);
		
	}
	
	/* ***25692 - end */

	
	/* ***17376#55 - start */
	public boolean[] actionRender(ScQt scQt){
		boolean[] resArr = new boolean[13];
		
		if(isMofSupplier()){
			/* Bug #27412 START */
			resArr[10] = getManagedSearchBeanDocumentTypeIsLOI();
			resArr[11] = getManagedSearchBeanDocumentTypeIsLOA();
			resArr[12] = getManagedBean().getQtnTenderSearchBean().getResultDocType() != ParameterConst.DOCTYPE_BIDDING && scQt.getProcurementMode().getParameterCode() != ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO;
			/* Bug #27412 END */
			
			
		}else{
			// #9019 -start-
			// in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[0] = true;
			} else {
				resArr[0] = !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode()
						.getParameterCode());
			}
			// #9019 -end-
			
			resArr[1] = ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getResultDocType());
			
			// #9019 -start-
			// in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[2] = false;
			} else {
				resArr[2] = ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode()
						.getParameterCode());
			}
			// in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[3] = false;
			} else {
				resArr[3] = !ParameterConst.DOCTYPE_BIDDING.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean()
						.getResultDocType())
						&& !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode()
								.getParameterCode());
			}
			// #9019 -end-

			resArr[4] = ParameterConst.DOCTYPE_BIDDING.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getResultDocType());
			resArr[5] = getManagedSearchBeanDocumentTypeIsLOI();
			resArr[6] = getManagedSearchBeanDocumentTypeIsLOA();
			
			// #9019 -start-
			// in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[7] = false;
			} else {
				resArr[7] = getManagedSearchBeanDocumentTypeIsSuppFinal()
						&& !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode()
								.getParameterCode()) && isAllowQTViewSupplierFinalization(scQt);
			}
			// #9019 -end-
			
			//only 1 is true
			if(resArr[5] || resArr[6] || resArr[7]){
				resArr = new boolean[]{false, false, false, false, false, resArr[5], resArr[6], resArr[7], false, false};
			}else if(resArr[4]){ //#17376-144
				resArr = new boolean[]{false, false, false, false, resArr[4], false, false, false, false, false};	
			} //#17376-144
			else{
				boolean fnd = false;
				resArr[1] = false;
				for(int i = 0; i<resArr.length; i++){ 
					if(i==1){continue;} //ignore resArr[1].
					if(fnd){
						resArr[i] = false;
					}
					if(resArr[i]){
						fnd = true;
					}
				}
				
				qtEnableEditCheck(resArr);
			}
			
			if(isQtTechSupportSearchScreen()){
				resArr[8] = isAllowQTPublishBtn(scQt);
				resArr[9] = isAllowQTClosedBtn(scQt);
			}

			// 29464-start
			// 32806 , 29464 -start-
			if (isQTMaintenanceScreen()) {
				// 32806 , 29464 -end-
				Long qtStatus = scQt.getQtStatus() == null ? 0L : scQt.getQtStatus().getStatusId();

				resArr[1] = !qtStatus.equals(QTStatusConst.QT_PENDING_SPECIFICATION_COMMITTEE_APPOINTMENT);
				// 29464-end
				// 32806 , 29464 -start-
			}
			// 32806 , 29464 -end-
		}
		
		return resArr;
		
	}
	/* ***17376#55 -end */
	
	
	/* ***19336#73 - start */
	private void qtEnableEditCheck(boolean [] arr){
		if(isQTMaintenanceScreen()){
			if(arr[0]){
				arr[1] = true;
			}
		}
	}
	/* ***19336#73 - end */

	
	/* ***19287#86 - start */
	public void backToPrevious() {
		debugLogger("backToPrevious");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						getManagedBean().getViewOriginalUrl());
	}
	/* ***19287#86 - end */
	
	
	/* ***25692#95 - start */
	public boolean isAllowedToViewSpec(){
		
		if(this.getManagedBean().getQt()!=null
			&& this.getManagedBean().getCurrentStatusId()!=null){
				Long currStatus = this.getManagedBean().getCurrentStatusId();
			 	if(	QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(currStatus)
					||	QTStatusConst.QT_REVISION_PUBLICATION.equals(currStatus)
					||	QTStatusConst.QT_PENDING_DOCUMENT_REVISION.equals(currStatus)
			 	){
			 		if(userHasRole(new RoleCode[]{RoleCode.DESK_OFFICER})){
			 			return true;
			 		}
			 	}
			 	if( QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(currStatus)&&(getUserTaskIsPublicationApproval() && isApprovalScreen())){ //#25692 -163
			 		if(userHasRole(new RoleCode[]{RoleCode.PUBLICATION_APPROVER})){ //#25692 
			 			return true;
			 		}
			 	}
			 	
			 	//28780 starts
			 	if( QTStatusConst.BPK_PENDING_APPROVAL.equals(currStatus)&&(getUserTaskIsBPKApproval() && isApprovalScreen())){ 
			 		if(userHasRole(new RoleCode[]{RoleCode.BPK_APPROVER})){ 
			 			return true;
			 		}
			 	}
			 	
			 	if( QTStatusConst.CMT_PENDING_ASSIGN_FC_SECRETARY.equals(currStatus)){ 
			 		if(userHasRole(new RoleCode[]{RoleCode.DESK_OFFICER})){ 
			 			return true;
			 		}
			 	}			 	
			 	//28780 ends
			
		}
		return false;
		
	}
	/* ***25692#95 - end */

	// 26095@hatta - start
	/**
	 * will be called in close QT only..
	 * @return the string go to page
	 */
	public String closeQtDetails() { 
		debugLogger("closeQtDetails");
				
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();
					
				quationTenderReqDTO.setQtId(scQt.getQtId());
				QuationTenderResDTO resDTO = detailSO.retrieveCloseQTByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQt QtDetail = resDTO.getQtDetail();
						
			    List<ScCloseQTRemark> qtRemarks =  initializeRemarks(
			    		                           QtDetail, 
						                           this.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						                           QtDetail.getScCloseQTRemark(),
						                           ScCloseQTRemark.class);
			
				for(ScCloseQTRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(false);
				}
						
				this.getManagedBean().getQt().setScCloseQTRemark(qtRemarks);

			return "/xhtml/sc/quatationtender/closeQuotationTender.xhtml";

				
					
		} finally {
			detailSO=null;
		}
	}
				
	/**
	 * Check in the validity period expired and status related. 
	 * @return the true or false.
	 */
	public boolean closeQTIfValidityPeriodExpiredAndStatusRelated() {
		boolean isCloseQT = false;
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		boolean isBiddingCloseQT = false; 
						
		if (scQt.getProposalValidityEndDate() != null && this.getManagedBean().getCurrentDate() != null 
				&& this.getManagedBean().getCurrentDate().after(scQt.getProposalValidityEndDate())
				&& this.getManagedBean().isMaintanceFlow() == true ) {
					
			if(statusId.equals(QTStatusConst.QT_CLOSE_PERMANENTLY)) {
				isCloseQT = true; 
			}
	
			if(statusId.equals(QTStatusConst.QT_SUPPLIER_FINALIZATION) || statusId.equals(QTStatusConst.QT_RE_FINALIZATION)) {
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				
				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
				quationTenderReqDTO.setQtdetail(scQt);
				
				QuationTenderResDTO resDTOBID = detailSO.retriveScheduleDetailStatus(quationTenderReqDTO);
				
				if(null!=resDTOBID && null!=resDTOBID.getBidSchedule()) {
					isBiddingCloseQT = checkIsBiddingActiveForStatusCloseQT(scQt);
					
					if(isBiddingCloseQT) {
						isCloseQT = true; 
					} else {
						isCloseQT = false;
					}
				}
	
				quationTenderReqDTO.setQtId(scQt.getQtId());
				
				QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
				
				if(resDTOSF.isCloseQT() == true) {
					isCloseQT = true; 
				}
							
				QuationTenderResDTO resDTOCMT = detailSO.closeQTstatusForCMT(quationTenderReqDTO);
				
				if(resDTOCMT.isCloseQT() == true) {
					isCloseQT = true; 
				}
			}
						
			if(statusId.equals(QTStatusConst.QT_PENDING_LOI)) {
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
						    
				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
				quationTenderReqDTO.setQtId(scQt.getQtId());
				
				QuationTenderResDTO resDTOLOI = detailSO.closeQTstatusForLOI(quationTenderReqDTO);
				
				if(resDTOLOI.isCloseQT() == true) {
					isCloseQT = true; 
				}
				
				QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
				
				if(resDTOSF.isCloseQT() == true) {
					isCloseQT = true; 
				}
			}
            
			if(statusId.equals(QTStatusConst.QT_PENDING_LOA)) {
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
        						    
				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
        		quationTenderReqDTO.setQtId(scQt.getQtId());
        						
        		QuationTenderResDTO resDTOLOA = detailSO.closeQTstatusForLOA(quationTenderReqDTO);
        							
        		if(resDTOLOA.isCloseQT() == true) {
        			isCloseQT = true; 
        		}
        							
        		// Bug #33601 - Start
	       		 /*
	       		QuationTenderResDTO resDTOLOI = detailSO.closeQTstatusForLOI(quationTenderReqDTO);
	       							
	       		if(resDTOLOI.isCloseQT() == true) {
	       			isCloseQT = true; 
	       		}
	       							
	       		QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
	       						
	       		if(resDTOSF.isCloseQT() == true) {
	       			isCloseQT = true; 
	       		}
	       		*/
	       		//Bug #33601 - End 
			}
		}	
		
		return isCloseQT;	
	}
			
	/**
	 * check for available status in active bidding for close QT 
	 * @return the true or false.
	 */
	private boolean checkIsBiddingActiveForStatusCloseQT(ScQt scQt) {
		IBiddingSO biddingSO;
		
		try {
			boolean isBiddingActive = false;
			biddingSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IBiddingSO.class);
					
			BiddingReqDTO reqDTO = new BiddingReqDTO();	
					
			List<Long> statusIds = new ArrayList<Long>();
			statusIds.add(BidStatusConst.BID_PENDING_BIDDING);     // (C)63350
			statusIds.add(BidStatusConst.BID_RESCHEDULE_BIDDING);  // (C)63355
					
			ISearchBean<ScBidSchedule> bidSchSearchBean = new SearchBean<ScBidSchedule>();
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_QUOTATION));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"scBid.docId", SearchOperator.OPERATOR_EQUALS.value(), scQt.getQtId()));
			bidSchSearchBean.getDataFilterBeans().add(new SearchFilterBean(
					"pmStatus.statusId", SearchOperator.OPERATOR_IN.value(), statusIds));
					
			reqDTO.setBidSchSearchBean(bidSchSearchBean);

			BiddingResDTO resDTO = biddingSO.searchBiddingScheduleList(reqDTO);
			List<ScBidSchedule> list = resDTO.getBidSchResultBean().getResultList();
			
			if(list.size() > 0 ) {
				isBiddingActive = true;
			}
			
			return isBiddingActive;
		} finally {
			biddingSO = null;
		}
	}
			
	/**
	 * check Close QT Dialog Pop Up
	 * @return true 
	 */
	public void closeQTDialogPopUp() {
		RequestContext.getCurrentInstance().execute("closeQTDlg.show()");
	}	

	/**
	 * Upload attachment for Close QT 
	 */
	public void handleCloseQTFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if(event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScCloseQTAttachment attachment = new ScCloseQTAttachment();
			ScQt scQt = this.getManagedBean().getQt();
			
			List<ScCloseQTAttachment> attachments = scQt.getScCloseQTAttachment();
			attachment.setScQt(scQt);
			attachment.setDocNo(scQt.getQtNo());
						
			if(attachments == null) {
				attachments = new ArrayList<>();
			}
						 			
			if(attachments.isEmpty()) {
				attachments.add(attachment);
				
				scQt.setScCloseQTAttachment(attachments);
 				
				scAttachment = attachment;
				
				uploadAttachment(event.getFile(), scAttachment);
			
				this.getManagedBean().setQt(scQt); 
			} else {
				if(attachment != null) {
					handleException(new EpException(MessageConst.ERROR_100063));
				}
			}		
		}
	}
			
	/**
	 * Delete attachment for Close QT by index
	 * @param index  file index to delete
	 */
	public void deleteCloseQTAttachmentAction(int index) {
		this.getManagedBean().getQt().getScCloseQTAttachment().remove(index);
	}
			
	/**
	 * The method used to set the file index of attachment list.
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}

	/**
	 * Submit close QT details is succesful.
	 */
	public void submitCloseQTDetail() { 
		IQTDetailSO detailSO;
		
		try {
			this.getManagedBean().setSaveFlow(false);
			
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class); 
					
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			
			ScQt qt = this.getManagedBean().getQt();
						
			validationCloseQT(qt.getScCloseQTRemark(),qt.getScCloseQTAttachment());
						
			if (isQTStatusPublished(qt.getQtStatus().getStatusId())) {
				quationTenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				
				finalizeRemarks(qt.getScCloseQTRemark());
				
				qt.setScQtStatus(null);
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setQtClose(true);
			} else {
				quationTenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				
				finalizeRemarks(qt.getScCloseQTRemark());
				
				qt.setScQtStatus(null);
				qt.setScQtCommittees(null);
				qt.setScQtApprovals(null);
				qt.setScQtBriefings(null);
				
				quationTenderReqDTO.setQtdetail(qt);
				quationTenderReqDTO.setQtClose(true);
			}
						
			QuationTenderResDTO quationTenderResDTO = detailSO.closeQT(quationTenderReqDTO);
						
			if (quationTenderResDTO.isSuccessful()) {
				successMessage(MessageConst.INFO_700036, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
						qt.getQtNo(), Boolean.TRUE);
			}
				        
		} finally {
			detailSO = null;
		}
	}
			
	/**
	 * Validation for close QT detail.
	 * @return the list remark and list attachment
	 */
	private void validationCloseQT(List<ScCloseQTRemark> remark,List<ScCloseQTAttachment> attachment) {
		if (null != remark) {
			for (int i = 0; i < remark.size(); i++) {
				String userRemark = remark.get(i).getRemark();
				
				if (null == userRemark || "".equals(userRemark)) {
					throw new EpException(MessageConst.ERROR_100022,new String[] { 
							ResourceBundleUtils.getMessage("lbl.sc.sf.justification", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) });
				}
			}
		}
			
		if(CollectionUtils.isEmpty(attachment)) {
		    		throw new EpException(MessageConst.ERROR_100022,new String[] { 
						    ResourceBundleUtils.getMessage("lbl.sc.supporting.document", 
						    ResourceBundleUtils.BUNDLE_COMMON_LABELS,new FacesScopes().getUserLocale()) });
		}
	}
			
	public List<ScCloseQTRemark> findCloseQTRemarksByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);	
					
			return qtDetailSO.findScCloseQTRemarks(reqDTO).getScCloseQTRemark();
		} finally {
			qtDetailSO = null;
		}
	}
			
	public String getUsername() {
		String userName = null;
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		List<ScCloseQTRemark> remarks = new ArrayList<>();
				
		ScQt qt = this.getManagedBean().getQt();
		
		quationTenderReqDTO.setQtId(qt.getQtId());
		quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QT_CLOSE);
			
		remarks = findCloseQTRemarksByReqDTO(quationTenderReqDTO);
				
		for(ScRemark remark:remarks) {
			userName = remark.getCreatedUser().getUserName();
		}
				
		return userName;   
	}
            
	public String getUserLoginId() {
		String userLoginId = null;
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		List<ScCloseQTRemark> remarks = new ArrayList<>();
				
		ScQt qt = this.getManagedBean().getQt();
		
		quationTenderReqDTO.setQtId(qt.getQtId());
		quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QT_CLOSE);
				
		remarks = findCloseQTRemarksByReqDTO(quationTenderReqDTO);
				
		for(ScRemark remark:remarks) {
			userLoginId = remark.getCreatedUser().getLoginId();
		}
		
		return userLoginId;   
	}
	// 26095@hatta - end
            
            
	// Start 19287
	public void resetSearchDataResults() {
		if (isQTSearchScreen()) {
			IPagingDataModel<ScQt> searchQTDataModel = getManagedBean().getSearchQTDataModel();
			searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean().getQtnTenderSearchBean());
    		
    			getManagedBean().setSearchQTDataModel(searchQTDataModel);
    			getManagedBean().getSearchQTDataModel().resetData();
			
    			IPagingDataModel<ScLoaForQt> searchLoaDataModel = getManagedBean().getSearchLoaForQtDataModel();          			
    				searchLoaDataModel = new LOAPagingDataModel("searchQTLoaLois", getManagedBean()
    						.getQtnTenderSearchBean());
    				getManagedBean().setSearchLoaForQtDataModel(searchLoaDataModel);
    				getManagedBean().getSearchLoaForQtDataModel().resetData();
    				IPagingDataModel<ScLoiForQt> searchLoiDataModel = getManagedBean()
    						.getSearchLoiForQtDataModel();
    					searchLoiDataModel = new LOIPagingDataModel("searchQTLoaLois", getManagedBean()
    							.getQtnTenderSearchBean());
    					getManagedBean().setSearchLoiForQtDataModel(searchLoiDataModel);
    					getManagedBean().getSearchLoiForQtDataModel().resetData();
    		
		}
	}
	// End 19287
	
   //#25692-start         
    public Long retirveStatusId() {
    	if(((null!=this.getManagedBean().getTaskName()&&SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(this.getManagedBean().getTaskName()))|| (getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()) || (getUserTaskIsBPKApproval() && isApprovalScreen())  || (getUserTaskIsCommitteeAppointmentApproval() && isApprovalScreen()))){
    		log.debug("=====returnif====");
    		return this.getManagedBean().getSubStatusId();	
    	}else{
    		log.debug("=====returnelse====");
    		return this.getManagedBean().getCurrentStatusId();	
    	}
    }
    //#25692-End  
    
	// #17376-start onlineBiddingcodes
	public String viewOnlineBidding(ScQt scQt, String previousPageUrl) {
		IQTDetailSO detailSO;
		detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		
		QuationTenderReqDTO qtReqDTo = new QuationTenderReqDTO();
		qtReqDTo.setQtdetail(scQt);
		
		QuationTenderResDTO qtResDTO = detailSO.retriveScheduleDetailStatus(qtReqDTo);
		BiddingBackingBean biddingBB = new BiddingBackingBean();
		
		// #30451 -Start.
		if (getManagedBean().isBdSfFlow()) {
			ScheduleBiddingBackingBean sbb = new ScheduleBiddingBackingBean();
			
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setDocNumber(scQt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
			
			if (bpmResDTO != null && CollectionUtils.isNotEmpty(bpmResDTO.getWorklistVO().getWorklistItem())) {
				for (WorklistItemVO currentVO : bpmResDTO.getWorklistVO().getWorklistItem()) {
					
					if (StringUtils.isNotEmpty(currentVO.getTaskId())) {
						sbb.renewSesionObject();
						sbb.navigateToBiddingPage(currentVO.getTaskId());
						
						return "/xhtml/sc/bidding/scheduleBidding/scheduleBiddingMain.xhtml";
					}
					
					break;
				}
			}
		} else if (null != qtResDTO && null != qtResDTO.getBidSchedule()) {
			// #30451 -End.
			biddingBB.renewSesionObject();
			
			return biddingBB.bidManagementQuotationTenderAction(qtResDTO.getBidSchedule(), previousPageUrl);
		}
		
		return "/xhtml/sc/bidding/bidManagement/bidManagementMain.xhtml?faces-redirect=true";
	}
	// #17376-End onlineBiddingcodes

	// #25037-start
	/**
	 * Prepare and navigate to committee history view.
	 * 
	 * @param scQtCommittee
	 * @param showHistory
	 */
	public void navigateToViewHistory(ScQtCommittee scQtCommittee, boolean showHistory) {
		this.getManagedBean().setShowHistory(showHistory);

		if (this.getManagedBean().isShowHistory()) {
			ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			List<ScCommitteeMemberHistory> histories = committeSO.getMergedHistory(scQtCommittee, false);

			this.getManagedBean().setCommitteeHistories(new Paginator(histories));
			this.getManagedBean().setCurrentCommitteeDesc(
					this.getParameterDesc(scQtCommittee.getScCommittee().getCommitteeType(),
							new FacesScopes().getUserLocale()));
			this.getManagedBean().setSelectedHistoryMember(new Paginator(new ArrayList<QtCommitteeMemberBean>()));
		}
	}

	// #25037 -end-
	
	// #25037 -start-
	/**
	 * Prepare history data when selected
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void onSelectHistory() throws IOException {
		ScCommitteeMemberHistory history = this.getManagedBean().getSelectedCommitteeHistory();
		List<QtCommitteeMemberBean> list = (List<QtCommitteeMemberBean>) (Object) history.deserializeList(history
				.getCommitteeMembers());

		CustomUtils.sortQtCommitteeMemberRole(list);

		Paginator selectedHistoryMember = new Paginator(list);

		this.getManagedBean().setSelectedHistoryMember(selectedHistoryMember);
	}
	// #25037 -end-
	
	// #25037 -start-
	/**
	 * Validate if specification was updated through maintenance
	 * 
	 * @param showPopup
	 */
	public void assertSpecificationUpdateInProgress(boolean showPopup) {
		boolean inProgress = false;
		String taskName = this.getManagedBean().getTaskName();
		taskName = StringUtils.isNotEmpty(taskName) ? taskName : "";
		String[] taskNames = new String[] { SCProcessNameConst.PREPARE_QT_SCHEDULE,
				SCProcessNameConst.APPROVE_PUBLICATION };
		String[] specTaskName = new String[] { SCProcessNameConst.SPEC_MAINTENANCE_ENDORSE_SCORING,
				SCProcessNameConst.SPEC_MAINTENANCE_SIGNOFF_SCORING };

		if (Arrays.asList(taskNames).contains(taskName)) {
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			ScQt qt = this.getManagedBean().getQt();
			// 25037 - start - note 568
			IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT);
			reqDTO.setDocId(qt.getQtId());
			ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			Long statusId = (wfStatus != null && wfStatus.getPmStatus() != null) ? wfStatus.getPmStatus().getStatusId()
					: 0L;
			if (statusId.equals(QTStatusConst.SE_PENDING_SUBMISSION)) {
				this.getManagedBean().setSpecUpdateInProgress(true);
				inProgress = true;
			} else {
				bpmReqDTO.setDocNumber(qt.getQtNo());
				bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
				BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
				for (WorklistItemVO currentVO : resDTO.getWorklistVO().getWorklistItem()) {
					if (StringUtils.isNotBlank(currentVO.getTaskName())
							&& Arrays.asList(specTaskName).contains(currentVO.getTaskName())) {
						this.getManagedBean().setSpecUpdateInProgress(true);
						inProgress = true;
						break;

					}
				}
			}
		}
		
		if (inProgress) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, getMessage("lbl.sc.popup.checkBeforePublish2",
							null))); // #15391
		} else if (showPopup) {
			// 25037 - end - note 568
			RequestContext.getCurrentInstance().execute("submitConfirmPopUp2.show()");
		}
	}
	// #25037 -end-
	
	// #25037 -start-
	public String getDoaaRequestStatus() {

		IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);

		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_DOA_ASSIGNMENT);
		reqDTO.setDocId(this.getManagedBean().getQt().getQtId());

		ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		Long statusId = wfStatus == null ? 0L : wfStatus.getPmStatus().getStatusId();

		this.getManagedBean().setDoaRequestEditable(true);

		if (wfStatus == null) {
			FacesScopes fs = new FacesScopes();
			return ResourceBundleUtils.getMessage("lbl.rpt.pendingsubmission",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, fs.getUserLocale());
		}

		Long[] statusArr = new Long[] { QTStatusConst.DAR_COMPLETED, QTStatusConst.DAR_REJECTED };

		if (!Arrays.asList(statusArr).contains(statusId)) {
			this.getManagedBean().setDoaRequestEditable(false);
		}

		return this.getStatusDescById(statusId);
	}
	// #25037 -end-
	
	// #25037 -start-
	@SuppressWarnings("unchecked")
	private List<ScQtContactPerson> generateDOAFromRequestContent(ScQtRequestContent requestContent, ScQt qt) {
		List<ScQtContactPerson> doaList = null;
		try {
			doaList = new ArrayList<ScQtContactPerson>();
			Object content = requestContent.getRequestContent() == null ? new HashMap<Long, String>() : requestContent
					.getRequestContent();

			Map<Long, String> doaMap = (Map<Long, String>) content;
			for (Map.Entry<Long, String> map : doaMap.entrySet()) {
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserId(map.getKey());
				UserResDTO userResDTO = this.getUserSOGlobal().retrieveUserByUserId(userReqDTO);
				PmUser user = userResDTO.getPmUser();
				if (user != null && user.getLoginId().equalsIgnoreCase(map.getValue())) {
					ScQtContactPerson doa = new ScQtContactPerson();
					doa.setUserId(user.getUserId());
					doa.setBrandNew(false);
					doa.setScQt(qt);
					doa.setOfficerType(SourcingConstants.OFFICER_TYPE_DOAA);
					doa.setName(user.getUserName());
					doa.setUser(user);
					doa.setPhoneNo(user.getPhoneNo());
					doa.setFaxNo(user.getFaxNo());
					doa.setEmail(user.getEmail());
					doaList.add(doa);
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return doaList;
	}
	// #25037 -end-
	
	// #25037 -start-
	private String generateDOACompareStr(List<ScQtContactPerson> list) {
		if (list == null) {
			return "";
		}
		Collections.sort(list, new Comparator<ScQtContactPerson>() {
			@Override
			public int compare(ScQtContactPerson o1, ScQtContactPerson o2) {
				Long o1Id = o1.getUser().getUserId() == null ? 0L : o1.getUser().getUserId();
				Long o2Id = o2.getUser().getUserId() == null ? 0L : o2.getUser().getUserId();
				return o2Id.compareTo(o1Id);
			}
		});
		String str = "";
		for (ScQtContactPerson p : list) {
			Long userId = p.getUser().getUserId() == null ? 0L : p.getUser().getUserId();
			str = str.concat(userId.toString());
		}
		return str;
	}
	// #25037 -end-
	
	// #25037 -start-
	public boolean isDOAListModified() {
		boolean modified = false;

		try {
			String currentList = this.getManagedBean().getPrevDOAs();
			currentList = currentList == null ? "" : currentList;

			String newList = generateDOACompareStr(this.getManagedBean().getPmuserContactpersonList());

			if (!currentList.equalsIgnoreCase(newList)) {
				modified = true;
			}
		} catch (Exception e) {
			ScQt scQt = this.getManagedBean().getQt();
			String qtNo = scQt == null ? "" : scQt.getQtNo();
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, ParameterConst.DOCTYPE_QUOTATION,
					qtNo, "qtDetailBackingBean.isDOAListModified", getUserIdSessionFromLoginId(), e);
		}

		return modified;
	}
	// #25037 -end-
	
	// #25037 -start-
	private void initCommitteeTabSingularReapproval(Object payload) {
		SCCommitteeDataType committeeData = payload == null ? new SCCommitteeDataType() : (SCCommitteeDataType) payload;
		boolean isReapproval = Boolean.TRUE.equals(committeeData.getReapproval());
		this.getManagedBean().setReapproval(isReapproval);
		IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
		ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();

		if (!isReapproval || wfStatus == null || null == wfStatus.getPmStatus())
			return;

		Long statusId = wfStatus.getPmStatus().getStatusId();
		if (!this.isStatusLessThanOrEquals(statusId, QTStatusConst.QT_PROPOSAL_PREPARATION,
				QTStatusConst.QT_ORDERED_STATUSES) && committeeData.getCommitteeType() != null) {// 25037
																									// -
																									// note
																									// 533
			// 25037 - start - note 372
			String tabType = committeeData.getCommitteeType();
			getManagedBean().setRoleFilterList(
					getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), tabType));
			// 25037 - end - note 372
			List<TabBean> tabs = this.getManagedBean().getScCommitteeMemberTabList();
			int i = 0;
			for (TabBean tab : tabs) {
				if (tabType.equalsIgnoreCase(tab.getTabType())) {
					this.getManagedBean().setSelectedTabBean(tab);
					this.getManagedBean().setSelectedTabIndex(i);
					this.getManagedBean().setSelectedCommitteeTabIndex(i);
				} else {
					tab.setDisabled(true);
				}
				i++;
			}
		}
	}
    //#25037-end

	/**
	 * #13344 Start : saveQtCAApproval
	 * 
	 * @return the string
	 */
	public void saveQtCAApproval() {
		log.debug("saveQtCAApproval");
		//IQTDetailSO detailSO;
		
		try {
			if (((getUserTaskIsCommitteeAppointmentApproval() && this.getManagedBean().getActiveIndex() == 5) || (getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && this
					.getManagedBean().getActiveIndex() == 9))) {

				String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
				String approvalType=this.getManagedBean().getApprovalType();
			    boolean approvaleditable=	this.getManagedBean().isApprovalEditableSection();
				boolean approvalScreen=this.getManagedBean().isApprovalScreen();
				boolean isCommitteeUpdateFlow = this.getManagedBean().isCommitteeUpdateFlow();// 25037
				List<ScQtApprovalRequest> approvalRequest=this.getManagedBean().getScQtApprovalRequestList();
				boolean committeeApprovalSpec=getUserTaskIsCommitteeAppointmentApproval();
				//boolean committeeApprovalECOC=getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec();
				List<String> paramTypes=this.getManagedBean().getParamTypes();
				
				updateApprovalDetails(this.getManagedBean().getQt());
				retrieveApprovalTabDtl();
				validateQtDetails();
				//String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
				//super.isQTStatusLessThanOrEquals(this.getManagedBean().getCurrentStatusId(), QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)
				
				if (committeeApprovalSpec) {
					log.info("======ifconditionCodesExecuted====");
					this.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(),
							pageAppr);
					this.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApproval);
				} else {
					log.info("======elseconditionCodesExecuted====");
					this.getManagedBean().addAdditionalPages(
							UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString(), pageAppr);
					this.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
					this.getManagedBean().setParamTypes(paramTypes);
					
					ApprovalBackingBean approvalbean = new ApprovalBackingBean();
					approvalbean.retriveCommiteeApprovalTabDetails();
					
					retrieveApprovalTabDtl();
				}
				
				this.getManagedBean().setParamTypes(paramTypes);
				this.getManagedBean().setApprovalType(approvalType);
				this.getManagedBean().setApprovalEditableSection(approvaleditable);
				this.getManagedBean().setApprovalScreen(approvalScreen);
				this.getManagedBean().setCommitteeUpdateFlow(isCommitteeUpdateFlow);// 25037
				this.getManagedBean().setScQtApprovalRequestList(approvalRequest);
				
				ScWorkFlowStatus currentStatus=null;
				
				if (currentStatus == null) {
					currentStatus = getManagedBean().getQt().getCurrentWorkFlowStatus();
					
					IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
					
					ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
					reqDTO.setDocType(ParameterConst.DOCTYPE_COMMITTE_APROVAL);
					reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
					
					ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					currentStatus = wfStatus;
				}
				
				initializeRemarks(this.getManagedBean().getScQtApproval(), currentStatus, this.getManagedBean()
						.getScQtApproval().getScQtApprovalRemarks(), ScQtApprovalRemark.class);
			
				successMessage(MessageConst.INFO_700090, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
			
			} else {
				successMessage(MessageConst.INFO_700090, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
			}
		} finally {
			//detailSO = null;
		}
	}
	//#13344 - End
	
	// #25077 - Start
	public boolean checkSettingPopUpBPKApprovalNeeded(ScQt scQt) {
		IQTDetailSO detailSO;
		
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			    boolean bpkFlag = false;
			    Long statusId = scQt.getQtStatus().getStatusId();
			  
			if (this.getManagedBean().isMaintanceFlow() == false
					&& null != statusId
					&& (statusId.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
							|| statusId.equals(QTStatusConst.QT_REVISION_PUBLICATION) 
							|| statusId.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL))) {
			    	
			    	QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			    	reqDTO.setQtdetail(scQt);

			    	QuationTenderResDTO resDTO = detailSO.retriveQTApprovalDetails(reqDTO);

		    		if(null != resDTO && null != resDTO.getApproval()){
						List<ScQtApproval> listOfApprovals = resDTO.getApproval();
						if (listOfApprovals != null) {
							
							for (ScQtApproval bpkApproval : listOfApprovals) {
								String approvalCategoryCode = bpkApproval.getApprovalCategory().getParameterCode();
								if (!bpkApproval.isDeleted()){
									
									if(ParameterConst.APPROVAL_CATEGORY_BPK.equals(approvalCategoryCode)) {
										bpkFlag = true;
									}
								}
							}
						}
		    		}
			    }
			    
			    return bpkFlag;
		} finally {
			detailSO = null;
		}
	}
	// #25077 - End
	
	// #25077 - Start	
	private boolean popUpBPKApprovalNeededOnceHaveRequestApproval(ScQt scQt) {

		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTDetailSO.class);
		Long statusId = scQt.getQtStatus().getStatusId();
		
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtdetail(scQt);

		QuationTenderResDTO resDTO = detailSO.retriveQTApprovalDetails(reqDTO);

		if (null != resDTO && null != resDTO.getApproval()) {
			for (ScQtApproval bpkApproval : resDTO.getApproval()) {
				String approvalCategoryCode = bpkApproval.getApprovalCategory().getParameterCode();

				if (!bpkApproval.isDeleted() && ParameterConst.APPROVAL_CATEGORY_BPK.equals(approvalCategoryCode)) {

					if (null != statusId
							&& !this.getManagedBean().isMaintanceFlow()
							&& (statusId.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
									|| statusId.equals(QTStatusConst.QT_REVISION_PUBLICATION) || statusId
										.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL))) {

						List<ScQtApprovalRequest> bpkRequests = bpkApproval.getScQtApprovalDtls();
						
						for (ScQtApprovalRequest bpkRequest : bpkRequests) {
							PmParameter approvalType = bpkRequest.getApprovalType();
							
							if (null != approvalType) {
								return Boolean.TRUE;
							}
						}// end looping bpk request approval
					}
				}
			} // end looping bpk approval
		}

		return Boolean.FALSE;
	}
	// #25077 - End	
	
	// #25077 - Start
	public void resetBpkCurrentArea(boolean actionFlow) {
		if (actionFlow) {
			this.getManagedBean().setResetBpkCurrentArea(true);
			this.getManagedBean().setShowPopUpBPKApprovalNeeded(false);
		} else {
			this.getManagedBean().setShowPopUpBPKApprovalNeeded(false);
		}
	}
	// #25077 - End
	
	// #25077 -start-
	private void checkingOtherApprovalFileAttachmentNeedDeleted(List<ScQtApproval> approval, String approvalCategory) {
		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {
				if (approvalCategory.equalsIgnoreCase(approval.get(i).getApprovalCategory().getParameterCode())) {
					List<ScQtApprovalRequest> approvalRequestHistory = approval.get(i).getHistoryApprovalRequests();
					
					if (CollectionUtils.isNotEmpty(approvalRequestHistory)) {
						for (int j = 0; j < approvalRequestHistory.size(); j++) {
							PmParameter approvalAction = approvalRequestHistory.get(j).getApproverAction();
							
							if (null != approvalAction) {

								if (approvalRequestHistory.get(j).getApprovalType().getParameterCode()
										.equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
									if (this.getManagedBean().isFlagFileAttahmentDeletedICT()) {
										ScQtApprovalReqAttachement approvalReqAttachement = approvalRequestHistory.get(
												j).getApprovalRequestAttachement();
										
										if (null == approvalReqAttachement) {
											this.getManagedBean().setResetBpkCurrentArea(false);
											
											throw new EpException(MessageConst.ERROR_760509);
										}
									}
								}

								if (approvalRequestHistory.get(j).getApprovalType().getParameterCode()
										.equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
									if (this.getManagedBean().isFlagFileAttahmentDeletedMITI()) {
										ScQtApprovalReqAttachement approvalReqAttachement = approvalRequestHistory.get(
												j).getApprovalRequestAttachement();
										
										if (null == approvalReqAttachement) {
											this.getManagedBean().setResetBpkCurrentArea(false);
											
											throw new EpException(MessageConst.ERROR_760509);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	// #25077 -end-

	// #25077 -start-
	private void checkValidationForStatusAndFileAttachment(List<ScQtApproval> approval, String approvalCategory) {
		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {
				if (approvalCategory.equalsIgnoreCase(approval.get(i).getApprovalCategory().getParameterCode())) {
					List<ScQtApprovalRequest> approvalRequest = approval.get(i).getScQtApprovalDtls();
					
					if (CollectionUtils.isNotEmpty(approvalRequest)) {
						for (int j = 0; j < approvalRequest.size(); j++) {
							PmParameter approverAction = approvalRequest.get(j).getApproverAction();
							
							if (null == approverAction || null == approverAction.getParameterId()) {
								this.getManagedBean().setResetBpkCurrentArea(false);
								
								throw new EpException(MessageConst.ERROR_760149);
							}

							ScQtApprovalReqAttachement approvalReqAttachement = approvalRequest.get(j).getApprovalRequestAttachement();
							
							if (null == approvalReqAttachement) {
								this.getManagedBean().setResetBpkCurrentArea(false);
								
								throw new EpException(MessageConst.ERROR_760509);
							}
						}
					}
				}
			}
		}
	}
	// #25077 -end-
	
	// #25077 -start-	
	/**
	 * Listener method to handle Others Approval file download event
	 * @param scQtApprovalRequest
	 * @throws Exception
	 */
	public void downloadOthersApprovalFile(ScQtApprovalRequest scQtApprovalRequest) throws Exception {
		downloadFile(scQtApprovalRequest.getApprovalRequestAttachement());
	}
	// 25077 - End
	
	// #25077 -start-
	/**
	 * Method to prepare scqt object for Approvals.
	 * 
	 * @return QTDetailReq object
	 */
	public QuationTenderReqDTO prepareApprovalsInfo() {

		ScQt scQt = this.getManagedBean().getQt();

		// Setting the Approval details to Qtdetails
		List<TabBean> tabList = this.getManagedBean().getTabList();

		if (CollectionUtils.isNotEmpty(tabList)) {
			List<ScQtApproval> approvalList = new ArrayList<>();
		
			for (TabBean tabBean : tabList) {
				if (tabBean.getBudgetApprovalUsers() != null) {
					tabBean.getScQtApproval().setApproverSettings(tabBean.getBudgetApprovalUsers().getTarget());
				}

				if (this.getManagedBean().getQt().isBrandNew()) {
					tabBean.getScQtApproval().setScQt(this.getManagedBean().getQt());
				}
				approvalList.add(tabBean.getScQtApproval());

			}
			
			scQt.setScQtApprovals(approvalList);
		}

		if (isQTStatusGreaterThanOrEqualsScheduleDoc(scQt.getQtStatus().getStatusId())) {
			ScQtApproval pubApproval = this.getManagedBean().getPublicationApproval();
			
			if (null != pubApproval) {
				List<ScQtApproval> approvals = scQt.getScQtApprovals();
				
				removePTJApproval(approvals);
				
				if (null != this.getManagedBean().getPublicationApprovals()) {
					List<ScQtApproverSettings> approvalSettings = this.getManagedBean().getPublicationApprovals()
							.getTarget();
					
					if (CollectionUtils.isNotEmpty(approvalSettings)) {
						for (ScQtApproverSettings approverSettings : approvalSettings) {
							approverSettings.setScQtApproval(pubApproval);
						}
						
						pubApproval.setApproverSettings(approvalSettings);
					}
				}
				
				approvals.add(pubApproval);
				
				scQt.setScQtApprovals(approvals);
			}
		}

		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtdetail(scQt);
		
		return reqDTO;
	}
	// #25077 -end-

	// #25077 -start-
	public List<ScQtApprovalRequest> getListOthersApprovalRequest() {
		List<ScQtApprovalRequest> approvalDtls = new ArrayList<>();
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		ScQt scQt = this.getManagedBean().getQt();
		
		reqDTO.setQtId(scQt.getQtId());
		reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_OTHERS);
		
		ScQtApproval approval = this.getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO).getQtApproval();
		
		if (approval != null && approval.getScQtApprovalDtls() != null) {
			approvalDtls = approval.getScQtApprovalDtls();
			
			for (ScQtApprovalRequest oth : approvalDtls) {

				if (oth.getApproverAction() == null) {
					oth.setApproverAction(new PmParameter());
				}

				if (oth.getAppRequestRemark() == null) {
					ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
					oth.setAppRequestRemark(approvalRemark);
					oth.getAppRequestRemark().setRemark(" ");
				}
			}
		}
		
		return approvalDtls;
	}
	// 25077 - End
	
    // #13344 - Start
    public boolean isCommitteeApprovalTask(String taskName) {
     	return (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName));
    }
    // #13344 - End
    
    //#25037 - start
    /**
	 * Validate if committee formed correctly.
	 * 
	 * @param showPopup
	 * @return
	 */
  	public boolean validateIsCommitteeFormed(boolean showPopup){
		ScCommittee specCommittee = null;
		if (CollectionUtils.isNotEmpty(this.getManagedBean().getScCommitteeMemberTabList())) {
			for (TabBean committeeTab : this.getManagedBean().getScCommitteeMemberTabList()) {
				ScQtCommittee qtCommittee = committeeTab.getScQtCommittee();
  				String[] types = ParameterConst.QT_COMMITTEES_ORDERED_LIST; //25037 - note 372

				if (qtCommittee != null) {
					ScCommittee committee = qtCommittee.getScCommittee();
					if (Arrays.asList(types).contains(committee.getCommitteeType().getParameterCode())
							|| committee.isApprovalReq()) {
						QTCommitteBackingBean committeeBB = new QTCommitteBackingBean();
						committeeBB.checkCommitteFormedOrNot();
						if (committee.getCommitteeType().getParameterCode()
								.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)) {
							specCommittee = committee;
						}
					}
				}
			}

			if (specCommittee != null && showPopup) {
				boolean modified = isCommitteeModified(specCommittee.getScCommitteeMembers(),
						getMemberHistoryList(specCommittee));
				if (modified && specCommittee.isApprovalReq()) {
					// 25037 - start - note 510
					ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
					CommitteReqDTO reqDTO = new CommitteReqDTO();
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
					CommitteResDTO resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
					List<ScQtCommitteeActivity> activities = resDTO.getCommitteActivities() == null ? new ArrayList<ScQtCommitteeActivity>()
							: resDTO.getCommitteActivities();
					for (ScQtCommitteeActivity activity : activities) {
						if (activity.isSignedOff()
								&& activity.getScCommitteeMember().getMemberRole().getParameterCode()
										.equals(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
							RequestContext.getCurrentInstance().execute("confirmReendorseOnReassignmentPopup.show()");
							return true;
						}
					}
					// 25037 - end - note 510
				}
			}
		}
		return false;
	}
  	
  	private boolean isCommitteeModified(List<ScCommitteeMember> currList, List<QtCommitteeMemberBean> prevList) {
		List<QtCommitteeMemberBean> previousList = prevList;
		if (CollectionUtils.isEmpty(previousList)) {
			previousList = new ArrayList<>();
		}
		List<ScCommitteeMember> currentList = currList;
		if (CollectionUtils.isEmpty(currentList)) {
			currentList = new ArrayList<>();
		}
		String[] previousListArr = new String[previousList.size()];
		String[] currentListArr = new String[currentList.size()];
		for (int i = 0; i < previousListArr.length; i++) {
			QtCommitteeMemberBean m = previousList.get(i);
			previousListArr[i] = m.getIdentificationNo().concat(m.getMemberRole());
		}
		for (int i = 0; i < currentListArr.length; i++) {
			ScCommitteeMember m = currentList.get(i);
			currentListArr[i] = m.getIcPassport().concat(m.getMemberRole().getParameterCode());
		}
		Arrays.sort(previousListArr);
		Arrays.sort(currentListArr);
		return !StringUtils.join(previousListArr, ",").equalsIgnoreCase(StringUtils.join(currentListArr, ","));
	}
  	//#25037 - end
  	
  // 25037 - start
  	private void prepareDeskofficerAssistantRequest(ScQt scQt, List<ScQtContactPerson> deskofficerAssistance) {
		ScQtRequestContent requestContent = null;
		if (CollectionUtils.isNotEmpty(scQt.getRequestContents())) {
			for (ScQtRequestContent r : scQt.getRequestContents()) {
				if (r.getRequestType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_DOA_ASSIGNMENT)) {
					requestContent = r;
					break;
				}
			}
		} else {
			scQt.setRequestContents(new ArrayList<ScQtRequestContent>());
		}
		this.getManagedBean().setRequestContent(requestContent);
		if (requestContent != null) {
			ScQtApprovalRequest request = requestContent.getApprovalRequest();
			if (request != null) {
				String state = request.getApproverAction() == null ? "" : request.getApproverAction()
						.getParameterCode();
				String[] actionArr = new String[] { ParameterConst.APPROVAL_DECISION_TYPE_REJECTED,
						ParameterConst.APPROVAL_DECISION_TYPE_APPROVED };
				if (!Arrays.asList(actionArr).contains(state)) {
					requestContent.setBrandNew(false);
					requestContent.setRequireApproval(true);
					requestContent.setSelectable(false);
					this.getManagedBean().setPmuserContactpersonList(
							generateDOAFromRequestContent(requestContent, scQt));
				} else {
					ScQtRequestContent newRequestContent = new ScQtRequestContent();
					newRequestContent.setBrandNew(true);
					newRequestContent.setScQt(scQt);
					newRequestContent.setRequireApproval(false);
					newRequestContent.setSelectable(true);
					newRequestContent.setRequestType(this.getParameterByTypeAndCode(
							ParameterTypeConst.APPROVAL_REQUEST_TYPES, ParameterConst.DOCTYPE_DOA_ASSIGNMENT));
					this.getManagedBean().setRequestContent(newRequestContent);
					scQt.getRequestContents().add(0, newRequestContent);
					this.getManagedBean().setPmuserContactpersonList(deskofficerAssistance);
				}
			} else {
				requestContent.setBrandNew(false);
				requestContent.setSelectable(true);
				requestContent.setRequireApproval(false);
				this.getManagedBean().setPmuserContactpersonList(generateDOAFromRequestContent(requestContent, scQt));
			}
		} else {
			ScQtRequestContent newRequestContent = new ScQtRequestContent();
			newRequestContent.setBrandNew(true);
			newRequestContent.setScQt(scQt);
			newRequestContent.setRequireApproval(false);
			newRequestContent.setSelectable(true);
			newRequestContent.setRequestType(this.getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES,
					ParameterConst.DOCTYPE_DOA_ASSIGNMENT));
			this.getManagedBean().setRequestContent(newRequestContent);
			scQt.getRequestContents().add(0, newRequestContent);

			this.getManagedBean().setPmuserContactpersonList(deskofficerAssistance);
		}
		if (this.getManagedBean().getPmuserContactpersonList() == null) {
			this.getManagedBean().setPmuserContactpersonList(new ArrayList<ScQtContactPerson>());
		}
		this.getManagedBean().setPrevDOAs(generateDOACompareStr(this.getManagedBean().getPmuserContactpersonList()));
	}
	// 25037 - end
    
	// #9019 -start-
    /**
	 * CR-9019 will be used to cancel QT
	 */
	public void cancelQTDraft() {
		IQTDetailSO detailSO;
		
		try {
			this.getManagedBean().setSaveFlow(false);
			
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);

			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt qt = this.getManagedBean().getQt();
			String ackMsg = null;

			quationTenderReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			
			List<ScQtRemark> remarks = qt.getScQtRemarks();

			if (CollectionUtils.isEmpty(remarks)) {
				throwExceptionForInvalidRemark();
			}

			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				
				if (null == userRemark || "".equals(userRemark)) {
					throwExceptionForInvalidRemark();
				}
			}
			
			// set Group Item to NULL when save while left blank and panel is selected
			if (qt.isPanel() && null == qt.getItemGroup().getPanelItemGroupId()) {
				this.getManagedBean().getQt().setItemGroup(null);
				this.getManagedBean().getQt().setCircularVer(null);
			}
			// - End
			
			// - Start (Harun) 03082022 Reopen Duplicate remarks when cancel #note-255
			if (null != this.getManagedBean().getQtRemarksIdRemove()) {
				quationTenderReqDTO.setQtRemarksIdRemove(this.getManagedBean().getQtRemarksIdRemove());
			}

			finalizeRemarks(qt.getScQtRemarks());
			
			qt.setScQtStatus(null);
			qt.setScQtCommittees(null);
			qt.setScQtApprovals(null);
			qt.setScQtBriefings(null);
			
			quationTenderReqDTO.setQtdetail(qt);
			quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			quationTenderReqDTO.setOutcome(BpmOutcomeConst.BPM_CANCEL);
			
			ackMsg = MessageConst.INFO_700031;
			
			FacesScopes facesScopes = new FacesScopes();
			
			quationTenderReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			
			//9019 Start #note-445
			if (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(this.getManagedBean().getQt().getQtStatus().getStatusId())) {
				detailSO.validateQTGeneralInformation(quationTenderReqDTO);
			}
			//9019 End 
			
			QuationTenderResDTO quationTenderResDTO = detailSO.cancleQTForDraft(quationTenderReqDTO);

			if (quationTenderResDTO.isSuccessful()) {
				successMessage(ackMsg, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
						qt.getQtNo(), Boolean.TRUE);
			}
		} finally {
			detailSO = null;
		}
	}
	// #9019 -end-
	
	// #9019 -start-
	private void throwExceptionForInvalidRemark() {
		throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage("lbl.sc.remark",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
	}
	// #9019 -end-
  	
	// #9019 -start-
  	/**
  	 * CR-9019 will be used to differentiate between cancel and submit 
  	 * @param outCome
  	 * @return
  	 */
	public boolean isCancel(String outCome) {
		return BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outCome);
	}
	// #9019 -end-
  	
	// #9019 -start-
  	/**
  	 * CR-9019 will be used to enable/disable mandayory checking on UI
  	 * @param scQt
  	 * @return
  	 */
	public boolean isProcurementTitleMandatory(ScQt scQt) {
		if (scQt.isCloned()) {
			return false;
		}
		
		return scQt.isBrandNew() || (StringUtils.isEmpty(scQt.getQtTitle()));
	}
	// #9019 -end-
	
	// #9019 -start-
  	/**
  	 * CR-9019 will be used to enable/disable mandayory checking on UI
  	 * @param scQt
  	 * @return
  	 */
  	public boolean isFileNoMandatory(ScQt scQt){
		if (scQt.isCloned()) {
			return false;
		}
  	    // WIP transaction Pending QT Submission
  		//       File Reference no is empty while Pending QT Submission and update WIP unable to proceed cause by this mandatory
		if (this.getManagedBean().isMaintanceFlow()) {
			return false;
		}
  	    // - End
		
  		return scQt.isBrandNew() || StringUtils.isEmpty(scQt.getFileNo());
  	}
  	// #9019 -end-

	// 29464-start - added logic at backingbean for future addition
	public boolean evaluateCommitteeAppointmentPage() {
		boolean flag = false;
		String taskName = this.getManagedBean().getTaskName() == null ? "" : this.getManagedBean().getTaskName();
		
		if (this.getManagedBean().isCommitteeAppointment()
				&& !taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)
				&& !taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)) {//#34694 & #25077
			flag = true;
		}
		
		return flag;
	}
	// 29464-end
	  	
	// #30477, #25426 - Start
  	/**
  	 * @param userIds
  	 * @param approverSettings
  	 * @param approverRole
  	 * @return
  	 */
  	public boolean isInactiveCommiteeApproverRole(List<Long> userIds, List<ScQtApproverSettings> approverSettings, String approverRole) {
		IQTDetailSO detailSO = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);

			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();

			List<Long> newUserIds = new ArrayList<>();

			if (userIds != null) {
				newUserIds = userIds;
			} else {
				for (ScQtApproverSettings approverSetting : approverSettings) {
					newUserIds.add(approverSetting.getPmUser().getUserId());
				}
			}

			for (Long userId : newUserIds) {
				quationTenderReqDTO.setUserId(userId);
				quationTenderReqDTO.setRoleCode(approverRole);
				List<PmUserOrg> listPmUserOrgs = detailSO.retrievePmUserOrgByUseridByIdRoleCode(quationTenderReqDTO)
						.getListPmUserOrgs();
				if (listPmUserOrgs.isEmpty()) {
					return true;
				}
			}

			return false;

		} finally {
			// set nothing
		}
	}
  	
    public boolean isCommitteeApprovalReviseTask(String taskName) {
     	return (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName));
    }
	// #30477, #25426 - End

  	/**11104-start
	 * Retrieve pm state by id.
	 * 
	 * @param stateId
	 *            - Long state id
	 * @return PmState object
	 */
	public static PmState retrievePmStateById(Long stateId) {
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStateSO.class);
		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setStateId(stateId);

		StateResDTO resDTO = reqSO.retriveStateDetailsByID(reqDTO);

		return resDTO.getPmState();
	}
	// 11104 -end-
	
	// 11104 -start-
	/**
	 * Retrieve pm city by id.
	 * 
	 * @param cityId
	 *            - Long the city id
	 * @return PmCity object
	 */
	public static PmCity retrievePmCityById(Long cityId) {
		ICitySO reqSO = (ICitySO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						ICitySO.class);
		CityReqDTO reqDTO = new CityReqDTO();
		reqDTO.setCityId(cityId);

		CityResDTO resDTO = reqSO.retriveCityById(reqDTO);

		return resDTO.getPmCity();
	}
	// 11104 -end-
	
	// 11104 -start-
	/**
	 * Retrieve pm dist by id.
	 * 
	 * @param distId
	 *            - Long the dist id
	 * @return PmDistrict object
	 */
	public static PmDistrict retrievePmDistById(Long distId) {
		IDistrictSO reqSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDistrictSO.class);
		DistrictReqDTO reqDTO = new DistrictReqDTO();
		reqDTO.setDistrictId(distId);

		DistrictResDTO resDTO = reqSO.retriveDistrictByID(reqDTO);

		return resDTO.getPmDistrict();
	}
	// 11104 -end-
	
	// 11104 -start-
	/**
	 * Retrieve pm division by id.
	 * @param divnId
	 * @return pmdivision.
	 */
	public static PmDivision retrievePmDivsById(Long divnId) {
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setDivisionId(divnId);

		DivisionResDTO resDTO = reqSO.retrieveDivisionById(reqDTO);

		return resDTO.getPmDivision();
	}
	// 11104-End
	
	// 9019 - Start (Harun) WIP transaction Pending QT Submission
	public void updateWIPTransaction() {
		try {
			validateQtDetails();

			successMessage(MessageConst.INFO_700111, ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.TRUE);
		} finally {
			debugLogger("end updateWIPTransaction");
		}
	}
	// #9019 -end-
	
	// #9019 - Start
	/**
	 * Navigate to page quotation tender search page for QT Maintenance & Audit.
	 */
	public void prerenderQtSearch() {
		try {

			FacesContext fc = FacesContext.getCurrentInstance();

			resetSearchForm();

			resetSearchResult();

			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, QTConstants.QT_SEARCHING_PAGE);

		} catch (Exception e) {
			handleException(e);
		}
	}
	// #9019 -end-
	
	// #9019 -start-
	/**
	 * Reset data for search form
	 */
	public void resetSearchForm() {

		if (isDeskOfficer() || !(isPbbSecretary() && !(isDeskOfficer() || isAuditor() || isMofSupplier()))) {
			this.getManagedBean().getQtnTenderSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
		} else {
			this.getManagedBean().getQtnTenderSearchBean().setDocType(null);
		}

		this.getManagedBean().getQtnTenderSearchBean().setDocNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtTitle(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtNo(null);
		this.getManagedBean().getQtnTenderSearchBean().setProcurementMode(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToCreatedDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToPublishDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setFromClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setToClosingDate(null);
		this.getManagedBean().getQtnTenderSearchBean().setQtStatus(null);
	}
	// #9019 -end-
	
	// #9019 -start-
	/**
	 * Reset data for display Data Model result
	 */
	public void resetSearchResult() {

		// reset for ScQt
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean().getSearchQTDataModel();
		searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean().getQtnTenderSearchBean());
		getManagedBean().setSearchQTDataModel(searchQTDataModel);
		getManagedBean().getSearchQTDataModel().resetData();

		// reset for ScLoiForQt
		IPagingDataModel<ScLoiForQt> searchLoiDataModel = getManagedBean().getSearchLoiForQtDataModel();
		searchLoiDataModel = new LOIPagingDataModel("searchQTLoaLois", getManagedBean().getQtnTenderSearchBean());
		getManagedBean().setSearchLoiForQtDataModel(searchLoiDataModel);
		getManagedBean().getSearchLoiForQtDataModel().resetData();

		// reset for ScLoaForQt
		IPagingDataModel<ScLoaForQt> searchLoaDataModel = getManagedBean().getSearchLoaForQtDataModel();
		searchLoaDataModel = new LOAPagingDataModel("searchQTLoaLois", getManagedBean().getQtnTenderSearchBean());
		getManagedBean().setSearchLoaForQtDataModel(searchLoaDataModel);
		getManagedBean().getSearchLoaForQtDataModel().resetData();

	}
	// #9019 - End
	
	// #28784 note #507 - start
	public boolean isSpecCommittee() {
		return QTConstants.PORTLET_NAME_QT_SPEC_ENDORSMENT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil
				.getDisplayPortletId());
	}
	// #28784 -end-
	
	// #28784 - start
	public void retrieveDemoQtDetails(Long qtId) {
		if (null != qtId) {
			IQTDetailSO QtDetailsSO;
			
			try {
				QtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean().isMaintanceFlow());
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setBdgDtlsReq(true);
				QuationTenderResDTO tenderResDTO = null;
				ScQt scQt = this.getManagedBean().getQt();
				
				if (null == scQt
						|| (scQt.isZonal() && null != this.getManagedBean().getZoneGeneralTrees() && this
								.getManagedBean().getZoneGeneralTrees().isEmpty())) {
					long startTime = System.currentTimeMillis();
					tenderResDTO = QtDetailsSO.retriveDemoQTDetails(tenderReqDTO);
					SLAUtils.getInstance().logHiExecInfo("QT", "QTDetailBackingBean.retrieveQuationTenderDetails",
							startTime);
					if (tenderResDTO.getQtDetail() != null) {
						scQt = tenderResDTO.getQtDetail();
						getManagedBean().setQt(scQt);
						this.getManagedBean().setPanel(scQt.isPanel());
						Long currentStatus = this.getManagedBean().getQt().getQtStatus().getStatusId();
						this.getManagedBean().setCurrentStatusId(currentStatus);
						getManagedBean().getCtContract().setContractNo(
								this.getManagedBean().getQt().getExistingContractNo());

						if (this.getManagedBean().getQt().isZonal()) {
							if (null != this.getManagedBean().getQt().getZoneLocationLevel()) {
								this.getManagedBean().setStateCodes(
										this.getManagedBean().getQt().getZoneLocationLevel().getParameterCode());
							}

							// Populate zone detail into tree node
							if (scQt.getScQtSupplierCriteria() != null
									&& scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
								List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias()
										.getTplSpecZoneDtls();
								populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
								// setting into zones bean to validate zones while adding
								if (CollectionUtils.isNotEmpty(specZoneDtls)) {
									List<ZonesBean> zoneBeanLst = new ArrayList<ZonesBean>();
									for (TplSpecZoneDtl specZoneDtl : specZoneDtls) {
										ZonesBean bean = new ZonesBean();
										bean.setTplSpecZoneDtl(specZoneDtl);
										zoneBeanLst.add(bean);
									}
									this.getManagedBean().setBeans(zoneBeanLst);
								}
							}
						}
						// Setting special approval info for login user
						if (scQt.getObtainSpecialApproval() != null && !scQt.getObtainSpecialApproval()) {
							this.getManagedBean().setPtjSpecialApproval(
									getPtjSpecialApprovalByOrgProfileId(scQt.getPmOrgProfile().getOrgProfileId()));

						} else {
							this.getManagedBean().setPtjSpecialApproval(scQt.getPtjSpecialApproval());
						}

						if (scQt.getEvaluationType() != null
								&& scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
							Long status = this.getManagedBean().getQt().getQtStatus() != null ? this.getManagedBean()
									.getQt().getQtStatus().getStatusId() : null;
							if (null != status && (!status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION))) {
								this.getManagedBean().setDemoType(true);
							}
						}
					}

					this.getManagedBean().setAmendMentBean(tenderResDTO.getAmendMentBean());
					if (this.getManagedBean().getGosodos() == null) {
						this.getManagedBean().setGosodos(tenderResDTO.getBudgetDtls());
					}
					if (null != getManagedBean().getQt() && null == getManagedBean().getQt().getProcurementTypeCatId()) {
						getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());
					}

				} else {
					if (scQt.getEstimatedAmount() == null) {
						BigDecimal estimatedAmount = QtDetailsSO.getdecryptedEstAmount(qtId);
						scQt.setEstimatedAmount(estimatedAmount);
						scQt.setDecryptedEstAmt(estimatedAmount);
					}

					if (scQt.getTmpExpectedIntendedPrice() == null) {
						BigDecimal tmpExpectedIntendedPrice = QtDetailsSO.getDecryptedExpectedIntendedPrice(qtId);
						scQt.setTmpExpectedIntendedPrice(tmpExpectedIntendedPrice);
						scQt.setDecExpectedIntendedPrice(tmpExpectedIntendedPrice);
					}
				}
			} finally {
				QtDetailsSO = null;
			}
		}
	}
	// #28784 - end
	
	// #28784 note-#634 - Start
	/**
	 * To Retrieve zonal Detail by qt id.
	 * 
	 * @param QuationTenderReqDTO
	 * 
	 * @return QuationTenderResDTO
	 * 
	 */
	public void retrieveZonalDetailAfterDemoDeletion(Long qtId) {
		if (null != qtId) {
			IQTDetailSO qtDetailsSO;
			try {
				qtDetailsSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
				
				QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
				tenderReqDTO.setMaintanceFlow(this.getManagedBean().isMaintanceFlow());
				tenderReqDTO.setQtId(qtId);
				tenderReqDTO.setBdgDtlsReq(true);
				
				ScQt scQt = this.getManagedBean().getQt();
				QuationTenderResDTO tenderResDTO = qtDetailsSO.retrieveZonalDetail(tenderReqDTO);

				if (tenderResDTO.getQtDetail() != null) {
					ScQt newScQt = tenderResDTO.getQtDetail();
					
					scQt.setScQtSupplierCriteria(newScQt.getScQtSupplierCriteria());
					
					getManagedBean().setQt(scQt);

					if (null != this.getManagedBean().getQt().getZoneLocationLevel()) {
						this.getManagedBean().setStateCodes(
								this.getManagedBean().getQt().getZoneLocationLevel().getParameterCode());
					}
					// Populate zone detail into tree node
					if (scQt.getScQtSupplierCriteria() != null
							&& scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
						// #28784 zone missing after demo = no (if not save) - Start
						List<TplSpecZoneDtl> specZoneDtls = scQt.getScQtSupplierCriteria().getQtZoneCriterias()
								.getTplSpecZoneDtls();
						populateZoneDetailIntoTreeNode(scQt, specZoneDtls);
						// #28784 zone missing after demo = no (if not save) - End
					}
				}
			} catch(Exception e) {
				//SLAUtils.getInstance().logSLAError("QT", "RetrieveZonalDetailForDemo", qtId.toString(), "retrieveZonalDetailAfterDemoDeletion", getUserIdSessionFromLoginId(), e);
			}
		}
	}
	// #28784 note-#634 - End

	// #29425 - start
	public void validateNewValueForValidityDays(int newValue) {
		validateValidityDays(newValue);
	}
	// #29425 -end-
	
	// #29425 -start-
	public void validateValidityDays(int validityDays) {
		PmParameter procureMentMode = this.getManagedBean().getQt().getProcurementMode();

		if (validityDays == 0) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.qt.proposal.validity", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
					new FacesScopes().getUserLocale()) });
		}

		if (ParameterConst.PROCUREMENT_MODE_QUOTATION.equalsIgnoreCase(procureMentMode.getParameterCode())) {
			int valDay = getProposalValidityDays(this.getManagedBean().getQt().getEstimatedAmount());

			if (null != this.getManagedBean().getQt().getQtValidityPeriod() && valDay < validityDays) {
				throw new EpException(MessageConst.ERROR_760044, new String[] { String.valueOf(valDay) });
			}
		}
	}
	// #29425 -end-
	
	// #29425 -start-
	private int getProposalValidityDays(BigDecimal estimatedAmount) {

		if (estimatedAmount != null) {
			IRuleSO ruleSO = null;

			try {
				RuleReqDTO reqDTO = new RuleReqDTO();
				reqDTO.setRuleType(RuleTypeConst.QT_PROPOSAL_VALIDITY_DAYS);
				reqDTO.setCompareValue(estimatedAmount.toString());

				ruleSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IRuleSO.class);
				List<String> ruleValueStrList = ruleSO.getRuleValuesByTypeAndCurrentDateRange(reqDTO)
						.getRuleValueStrList();

				if (ruleValueStrList != null && ruleValueStrList.size() > 0) {
					return Integer.valueOf(ruleValueStrList.get(0));
				} else {
					throw new EpException(MessageConst.ERROR_770222,
							new String[] { getRuleTypeDesc(reqDTO.getRuleType()) });
				}
			} finally {
				ruleSO = null;
			}
		}

		return 0;
	}
	// #29425 - end
	
	//9019 - Start (Harun) WIP transaction (New method)
	/**
	 * getTask By Sc Workflow Status
	 *
	 * @return boolean
	 */
	public boolean getTaskByDocNoModule() {
		ScQt scQt = this.getManagedBean().getQt();//9019
		boolean bpmExistFlag = false;
		IScWorkFlowStatusSO workFlowStatusSO;
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		
		ScWorkFlowStatusReqDTO scWorkFlowreqDTO = new ScWorkFlowStatusReqDTO();
		scWorkFlowreqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		scWorkFlowreqDTO.setDocId(scQt.getQtId());
		ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWorkFlowreqDTO).getWorkFlowStatus();
		// Start - #30866
		CircularBackingBean circularBB = new CircularBackingBean();
		if (circularBB.getManagedBean().isContinuityCircularAuditSearch()) {
			currentWorkFlowStatus = null;
			bpmExistFlag = true;
		}
		// End - #30866
		if(null != currentWorkFlowStatus){
			if(currentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_QT_SUBMISSION_WIP)){
				bpmExistFlag = false;
			}else{
				bpmExistFlag = true;
			}
		}
		return bpmExistFlag;
	}
	//9019 - End (Harun)

	// #29732 Start - reOpen Fix
	public String checkCatCodeChange() {
		// check submit empty category code
		if (CollectionUtils.isEmpty(this.getManagedBean().getQtCategoryList())
				&& this.getManagedBean().isFromSubmitCatChange()) {
			IQTSpecSO specSo = getQtSpecSO();
			SpecificationReqDTO specReqDTOCatChange = new SpecificationReqDTO();

			specReqDTOCatChange.setScQt(this.getManagedBean().getQt());
			specSo.validateSupplierQulificationInfo(specReqDTOCatChange);
		}

		List<ScQtCategory> qtCatCodeList = this.getManagedBean().getQt().getScQtCategorys();
		String catCodeList = "";
		Integer recordstatusInactive = 0;// #29732(#221)

		if (CollectionUtils.isNotEmpty(qtCatCodeList)) {
			for (ScQtCategory catCode : qtCatCodeList) {
				ICategoryCodeSO categoryCodeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, ICategoryCodeSO.class);
				PmCategoryL1 catCodeL1 = new PmCategoryL1();
				CategoryCodeReqDTO reqDTO1 = new CategoryCodeReqDTO();
				
				reqDTO1.setCategoryL1Id(catCode.getPmCategoryL1().getCategoryL1Id());
				
				CategoryCodeResDTO resDTO1 = categoryCodeSO.retrieveCategoryL1DtlById(reqDTO1);

				catCodeL1 = resDTO1.getCategoryL1();
				PmCategoryL2 catCodeL2 = new PmCategoryL2();
				CategoryCodeReqDTO reqDTO2 = new CategoryCodeReqDTO();
				
				reqDTO2.setCategoryL2Id(catCode.getPmCategoryL2().getCategoryL2Id());
				
				CategoryCodeResDTO resDTO2 = categoryCodeSO.retrieveCategoryL2DtlById(reqDTO2);

				catCodeL2 = resDTO2.getCategoryL2();
				PmCategoryL3 catCodeL3 = new PmCategoryL3();
				CategoryCodeReqDTO reqDTO3 = new CategoryCodeReqDTO();
				
				reqDTO3.setCategoryL3Id(catCode.getPmCategoryL3().getCategoryL3Id());

				CategoryCodeResDTO resDTO3 = categoryCodeSO.retrieveCategoryL3DtlById(reqDTO3);
				catCodeL3 = resDTO3.getCategoryL3();

				if (!catCodeL1.getCategoryName().equals(catCode.getPmCategoryL1Name())
						|| !catCodeL2.getCategoryName().equals(catCode.getPmCategoryL2Name())
						|| !catCodeL3.getCategoryName().equals(catCode.getPmCategoryL3Name())
						|| catCodeL1.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
						|| catCodeL2.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
						|| catCodeL3.getRecordStatusFlg().equals(recordstatusInactive)// #29732(#221)
				) {
					if (catCodeList != "") {
						catCodeList = catCodeList.concat(", ");
					}

					catCodeList = catCodeList.concat(catCode.getPmCategoryL1().getCategoryL1Code().toString()
							+ catCode.getPmCategoryL2().getCategoryL2Code().toString()
							+ catCode.getPmCategoryL3().getCategoryL3Code().toString());
				}
			}
		}

		return catCodeList;
	}
	// #29732 End

	// 29732 start
	private boolean isQTRoleMaintenanceValidation() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.PUBLICATION_APPROVER.value());
	}
	// 29732 end

	// 29732 start
	private IQTSpecSO getQtSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}
	// 29732 end
}
