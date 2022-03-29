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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.division.IDivisionSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.bidding.IBiddingSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.document.IQTDocumentSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
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
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
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
//#21695 Farid
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
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
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaBasic;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoaReverseAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaReverseRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoaWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;
import my.ep.persistence.entity.sc.loaloi.ScLoiReverseAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoiReverseRemark;
import my.ep.persistence.entity.sc.loaloi.ScLoiWorkFlowStatus;
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
import my.ep.persistence.entity.sc.quationtender.ScQtProgressivePayment;
import my.ep.persistence.entity.sc.quationtender.ScQtRecommendDtl;
import my.ep.persistence.entity.sc.quationtender.ScQtRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtRequestContent;
import my.ep.persistence.entity.sc.quationtender.ScQtReverse;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtSchedule;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplier;
import my.ep.persistence.entity.sc.quationtender.ScQtSupplierCriteria;
import my.ep.persistence.entity.sc.quationtender.ScQtZoneCriteria;
import my.ep.persistence.entity.sc.scretry.ScQtDisruptionLog;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecLoc;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneLoc;
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
import my.ep.web.backingbean.sc.specification.SpecificationBackingBean;
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
//#21695 Farid
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.UserTasks;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
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
//#21695 Farid
//#21695 Farid


// TODO: Auto-generated Javadoc
/**
 * The Class QTDetailBackingBean.
 * 
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTDetailBB")
@RequestScoped
public class QTDetailBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final long serialVersionUID = 1L;

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
	
	private static final String menuItemDetails = "MenuItemDetails";
	private static final String menuItemSuppQualificationCriteria = "MenuItemSuppQualificationCriteria";
	private static final String menuItemCommitteeAppointment = "MenuItemCommitteeAppointment";
	private static final String menuItemApproval = "MenuItemApproval";
	private static final String menuItemBriefSiteVisit = "MenuItemBriefSiteVisit";
	private static final String menuItemPublishDetail = "MenuItemPublishDetail";
	private static final String menuItemPaymentDeduction = "MenuItemPaymentDeduction";
	private static final String menuItemFulfilmentSchhedule = "MenuItemFulfilmentSchhedule";
	private static final String menuItemDocuments = "MenuItemDocuments";
	private static final String menuItemActivities = "MenuItemActivities";
	private static final String menuItemPublicationApproval = "MenuItemPublicationApproval";
	private static final String menuItemBPKApproval = "MenuItemBPKApproval";
	
	// ***25692#31 - start
	private static final String menuItemCommitteeApproval = "MenuItemCommitteeApproval";
	// ***25692#31 - end 
	
	private static final String menuItemCommitteeApprovalEcOcTecFec = "MenuItemCommitteeApprovalEcOcTecFec";
	private static final String menuItemPublicationApproverAssignment = "MenuItemPublicationApproverAssignment";
	
	//29464-start
	private static final String menuItemSpecificationCommitteeAssignment = "MenuItemSpecificationCommitteeAssignment";
	//29464-end
	
	public static final String TEMP_MB_KEY = "___823452381_76";
	
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
	
	public String getMenuItemFulfilmentSchhedule() {
		return menuItemFulfilmentSchhedule;
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
	
	//29464-start
	public String getMenuItemSpecificationCommitteeAssignment() {
		return menuItemSpecificationCommitteeAssignment;
	}
	//29464-end
	
	public Long[] getCmtOrderedStatuses(){
		return QTStatusConst.CMT_ORDERED_STATUSES;
	}
	
	//25201#52
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
		
		/*ScQt scQt = this.getManagedBean().getQt();
		if(scQt!= null){
			message += " @qtNo = "+scQt.getQtNo();
		}*/
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
		
		//initialization of loi and loa data model
		if(isQTMaintenanceScreen() || isQTSearchScreen()){
			getSearchQtLoiDataModelPriv(false);
			getSearchLoaForQtDataModelPriv(false);
		}
		toggleView();
		return searchQTDataModel;
	}
	
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
		//if(getManagedSearchBeanDocTypeIsLOAAndPrefixLAAndIsDO()||getManagedSearchBeanDocTypeIsLOIAndPrefixLIAndIsDO()){
		if(getManagedSearchBeanDocumentTypeIsLOA()||getManagedSearchBeanDocumentTypeIsLOI()){
			if(rc!=null)rc.execute("toggleView('"+ParameterConst.DOCTYPE_LETTER_OF_AWARD+"');");
			return ParameterConst.DOCTYPE_LETTER_OF_AWARD;
		}
		else{
			if(rc!=null)rc.execute("toggleView('"+ParameterConst.DOCTYPE_QUOTATION+"');");
			return ParameterConst.DOCTYPE_QUOTATION;
		}
		
		
	}
	
	private void renewQTSesionObject() {
		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
				.getSearchQTDataModel();
		IPagingDataModel<ScLoaBasic> searchLoaDataModel = getManagedBean()
				.getSearchLoaDataModel();
		IPagingDataModel<ScLoiForQt> searchLoiForQtDataModel = getManagedBean()
				.getSearchLoiForQtDataModel();
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean()
				.getQtnTenderSearchBean();
		
		super.renewSesionObject();
		
		getManagedBean().setSearchQTDataModel(searchQTDataModel);
		getManagedBean().setSearchLoaDataModel(searchLoaDataModel);
		getManagedBean().setSearchLoiForQtDataModel(searchLoiForQtDataModel);
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
				// #28611 - Start.
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
		        // #28611 = End.
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
			
			ScQt scQt = this.getManagedBean().getQt();
			
			/*
			 * Bug 31610 - To Exclude 9019 to allow 31610 to be merge into SIT
			 * NOTE: 9019 WHEN COMMIT MAKE SURE CHANGE SUCCESSMESSAGE FROM THIS INTO THE ONE THAT 9019 
			 * INCLUDE BACK 9019
			 * */
			boolean isFirstSave = scQt.getQtNo() == null && scQt.isBrandNew() ? true : false;
			
			validateQtDetails();
			
			//#28784
			checkDemoPresentationByStatus();
			
			if(isFirstSave){								
				 /* 
				  * BUG 31610
				  * Added WorkflowStatus to cater qt maintenance search result not working
				  */
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
				reqDTO.setQtdetail(this.getManagedBean().getQt());
				IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				
				detailSO.updateDraftWorkflowStatus(reqDTO);				
				 

				successMessage(MessageConst.INFO_7000044, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.TRUE);
			}else{
				//9019 Start - re-endorse change INFO_700004 
				successMessage(MessageConst.INFO_7000108, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
				//9019 Start
			}
			/* Bug 31610 END */
			debugLogger("end of saveQtDetailsAction");
		} finally {

		}
		
	}

	/**
	 * Submit qt details action.
	 * 
	 * @return the string
	 */
	public void submitQTDetailsAction() {
		
		debugLogger("start of submitQTDetailsAction");
		
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
			//bug #30435 - start
			if (!getManagedBean().isApproverTabLoaded()) {
				retrieveApprovalTabDtl();
			}
			//bug #30435 - end
			
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
			QuationTenderResDTO quationTenderResDTO = detailSO
					.submitQT(quationTenderReqDTO);
			if (quationTenderResDTO.isSuccessful()) {
				ScQt scQt = quationTenderResDTO.getQtDetail();
				successMessage(MessageConst.INFO_700026, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, scQt.getQtNo(), Boolean.TRUE);

				debugLogger("end of submitQTDetailsAction");
			}
		} finally {
			detailSO = null;
		}
	}

	/**
	 * Method to search the quationtenders based on serach date.
	 */
	public void searchQTAction() {
		if(this.getManagedSearchBeanDocumentTypeIsLOA()){
			getManagedBean().getSearchLoaForQtDataModel().reloadData();
		}
		else if(this.getManagedSearchBeanDocumentTypeIsLOI()){
			getManagedBean().getSearchLoiForQtDataModel().reloadData();
		}
		else;
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
		this.getManagedBean().getSearchQTDataModel().reloadData();
		return QTConstants.SEARCH_PAGE;
	}
	
	public String editQuationTenderDetails(ScQt scQt, boolean editable){
		return editQuationTenderDetails(scQt, editable, false, null);
	}
	
	
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
	public String editQuationTenderDetails(ScQt scQt, boolean editable, boolean fullPath, String backUrl) {
		
		debugLogger("start editQuationTenderDetails");
		if(!isApprovalScreen() && !fullPath){ //#17376-153
			renewQTSesionObject();
		}
		debugLogger("editQuationTenderDetails, @Managedbean = "+ this.getManagedBean());
		this.getManagedBean().setReadOnly(!editable);
		this.getManagedBean().setMaintanceFlow(true);
		IQTDetailSO detailSO;
		String REDIRECT_PAGE = QTConstants.MAINTANANCE_PAGE;
		if(fullPath){
			REDIRECT_PAGE = "/xhtml/sc/quatationtender/quatationtendercreation.xhtml";
		}
		if(!StringUtils.isBlank(backUrl)){
			this.getManagedBean().setViewOriginalUrl(backUrl);
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			Long qtId = scQt.getQtId();

			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setMaintanceFlow(true);
			reqDTO.setQtId(qtId);
			
			/*
			 * TRACE HI-EXEC TIME
			 * Development #29385
			 */
			long startTime = System.currentTimeMillis();
			QuationTenderResDTO resDTO = detailSO.retriveQTDetails(reqDTO);
			SLAUtils.getInstance().logHiExecInfo("QT","QTDetailBackingBean.editQuationTenderDetails", startTime);
			/*
			 * END
			 */
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
			if(!isApprovalScreen() || this.getManagedBean().getQt() == null){
				this.getManagedBean().setQt(selectedScQt);
			}
			
			//#28784
			boolean demoRequired = this.getManagedBean().getQt().isDemoRequired();
			Long status = this.getManagedBean().getQt().getQtStatus() != null ? this.getManagedBean().getQt().getQtStatus().getStatusId() : null;
			if(null != status && !status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) && 
					!status.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION) && this.getManagedBean().isMaintanceFlow() && 
					!demoRequired && !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL) && 
					!status.equals(QTStatusConst.CMT_PENDING_APPROVAL) && !status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) &&
					!status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)){
					this.getManagedBean().setDemoEvalDisabled(true);
			} 
			
			if (null != status && !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL) && 
					!status.equals(QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING) && 
					!status.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_REVISION) && this.getManagedBean().isMaintanceFlow() && 
					!status.equals(QTStatusConst.CMT_PENDING_APPROVAL) && !status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) &&
					!status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)) {
				this.getManagedBean().setDemoDisabled(false);
				this.getManagedBean().setDemoType(true);
				this.getManagedBean().setDemoAction(true);
			}			
			

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
			
			//#29474 - start
			if (null == this.getManagedBean().getQt().getFulfilmentType()) {

				this.getManagedBean().getQt().setFulfilmentType(new PmParameter());

			}
			if (null != this.getManagedBean().getQt().getFulfilmentType()) {
				this.getManagedBean().setFulfilmentType(
						this.getManagedBean().getQt().getFulfilmentType()
								.getParameterCode());
			}
			this.getManagedBean().setGenInfo(true);
			//#29474 - end
			
			boolean isValidFor25037 = this.validateSupportedTask(scQt.getQtNo(), QTConstants.ALLOW_COMMITTEE_REASSIGNMENT);
			this.getManagedBean().setValidFor25037(isValidFor25037);
			debugLogger("end of editQuationTenderDetails");
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
	
	
	// ***19366#40
	public void viewLoiListDetails(ScQt scQt, String previousPageUrl){
		Long qtId = scQt.getQtId();
		new LOIDetailBackingBean().preRenderLoiListView(qtId, previousPageUrl);
	}
	
	
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
			if(obj instanceof ScLoiForQt ){
				/* Bug #27412 START */
				scLoiForQt = (ScLoiForQt)obj;
				LOIDetailBackingBean bb = new LOIDetailBackingBean();
				scQt = scLoiForQt.getScLoiLoa()!=null?scLoiForQt.getScLoiLoa().getScQt():null;
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {					
					if(isMofSupplier()){
						//SUPPLIER
						return bb.retrieveSupplierLOIDtl(
								scLoiForQt.getScLoiLoa().getScQt().getQtId(), scLoiForQt.getLoiId(), previousPageUrl, true, true);
					}else{
						//DESK OFFICER						
						//return bb.retrieveSupplierLOIDtl(
								//scLoiForQt.getScLoiLoa().getScQt().getQtId(), scLoiForQt.getLoiId(), previousPageUrl, true, true);
						bb.preRenderViewForQtMaintenenceScreen(scLoiForQt.getLoiId(), previousPageUrl);
					}					
					return null;
				}
				/* Bug #27412 END */
			}
			else if(obj instanceof ScLoaForQt){
				scLoaForQt = (ScLoaForQt)obj;
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)){
					//LOADetailBackingBean bb = new LOADetailBackingBean();
					//return bb.retrieveSupplierLOADtl(scLoaForQt.getScLoiLoa().getScQt().getQtId(), scLoaForQt.getLoaId(), previousPageUrl, true, true);
					Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
					otherArgs.put(MapKeyEnum.loiLoaSelected, scLoaForQt);
					scQt = scLoaForQt.getScLoiLoa()!=null?scLoaForQt.getScLoiLoa().getScQt():null;
					QtLOABackingBean bb = new QtLOABackingBean();
					LOADetailBackingBean ldb = new LOADetailBackingBean();
					if(isMofSupplier()){
						/* Bug #27412 START */
						//return viewDirectLOADetails(scQt, previousPageUrl);
						return ldb.retrieveSupplierLOADtl(
								scLoaForQt.getScLoiLoa().getScQt().getQtId(), scLoaForQt.getLoaId(), previousPageUrl, true);
						/* Bug #27412 END */
					}else{
						if(ParameterConst.DOCTYPE_DIRECT_NEGO.equalsIgnoreCase(scLoaForQt.getScLoiLoa().getDocType())){
							return viewDirectLOADetails(scQt, previousPageUrl);
						}
						else{
							otherArgs.put(MapKeyEnum.showTrackingDiary, Boolean.TRUE);
							bb.preRenderViewForQtMaintenance(scQt, previousPageUrl, otherArgs);
							return null;
						}
					}
				}
			}
			else;
		}
		else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType) 
				|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(docType)){
			scQt = (ScQt)obj;
			if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
				viewLoiListDetails(scQt, previousPageUrl);
				return null;
			}
			else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType)){
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
				
			}
			else{
				return null;
			}
		}
		else if(obj instanceof ScQt){
			scQt = (ScQt)obj;
			if(	ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(docType)
				||  ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL.equals(docType)){
				return new SPDetailBackingBean().retriveSupplierProposalDtl(scQt.getQtId(), previousPageUrl, true);
			}
			else if(ParameterConst.DOCTYPE_BIDDING.equals(docType)) {
				//return QTConstants.ONLINE_BIDDING_MAIN_PAGE;//#17376-144
				return viewOnlineBidding(scQt,previousPageUrl); //#17376-144
			}
		}
		else;
		
		return null;
	}
	
	/**
	 * Enhancement #21694
	 * Method to validateReverseRemark
	 */
	public void validateReverseRemark(){
		/*
		 * VALIDATE REMARKS MUST NOT BE EMPTY
		 */
		if(this.getManagedBean().getScQtReverse().getTypeOfRequest().getParameterCode().equalsIgnoreCase(ParameterConst.APPROVAL_REQUEST_REVERSE_LOA_QT)){
			//IF LOA
			if(this.getManagedBean().getScLoaReverseRemarks() != null ){
				for(ScLoaReverseRemark scLoaReverseRemark :this.getManagedBean().getScLoaReverseRemarks()){
					if(scLoaReverseRemark.getRemark()==null || scLoaReverseRemark.getRemark().equalsIgnoreCase("")){
						throw new EpException(MessageConst.ERROR_100022,
								new String[] { 
									ResourceBundleUtils.getMessage(
										"lbl.sc.remark", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS,
										new FacesScopes().getUserLocale()) });
					}
				}					
			}
		}else if(this.getManagedBean().getScQtReverse().getTypeOfRequest().getParameterCode().equalsIgnoreCase(ParameterConst.APPROVAL_REQUEST_REVERSE_LOI_QT)){
			//IF LOI
			for(ScLoiReverseRemark scLoiReverseRemark :this.getManagedBean().getScLoiReverseRemarks()){
				if(scLoiReverseRemark.getRemark()==null || ("").equalsIgnoreCase(scLoiReverseRemark.getRemark())){
					throw new EpException(MessageConst.ERROR_100022,
							new String[] { 
								ResourceBundleUtils.getMessage(
									"lbl.sc.remark", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
				}
			}
		}
	}
	
	/**
	 * Enhancement #25729
	 * CHECKING TO ENABLE REVERSE BUTTON AT QT MAINTENANCE
	 */	
	public Boolean enableReverseButton(Long statusId){
		if(QTStatusConst.LOI_PENDING_SUBMISSION .equals(statusId)||
				QTStatusConst.LOI_PENDING_APPROVAL.equals(statusId) || 
				QTStatusConst.LOI_PENDING_REVISION.equals(statusId) || 
				QTStatusConst.LOI_PENDING_REVISION_APPROVAL.equals(statusId)){
			//LOI CASE
			return true;
		}else if(QTStatusConst.LOA_PENDING_SUBMISSION .equals(statusId)||
				QTStatusConst.LOA_PENDING_APPROVAL.equals(statusId) || 
				QTStatusConst.LOA_PENDING_REVISION.equals(statusId) || 
				QTStatusConst.LOA_PENDING_REVISION_APPROVAL.equals(statusId)){
			//LOA CASE
			return true;
		}else{
			return false;
		}
		
	}
	
	/**
	 * Enhancement #21694
	 * Method to save the Sc_Qt_Reverse
	 */
	public void saveReverseLoiLoa() {
		
		debugLogger("start of saveScQtReverse");
		this.getManagedBean().setSaveFlow(true);
		try {
			String paramCons;
			String loiloNo;
			saveReverseLoiLoaDtl();
			if(this.getManagedBean().getScQtReverse().getDocType().equalsIgnoreCase("LI")){
				paramCons = ParameterConst.DOCTYPE_LETTER_OF_INTENT;
				loiloNo = this.getManagedBean().getScLoiForQt().getLoiNo();
			}else{
				paramCons = ParameterConst.DOCTYPE_LETTER_OF_AWARD;
				loiloNo = this.getManagedBean().getScLoaForQt().getLoaNo();
			}
			
			successMessage(MessageConst.INFO_700004, ParameterTypeConst.DOCUMENT_TYPES,
					paramCons, loiloNo, Boolean.FALSE);

			debugLogger("end of saveQtReverseAction");
		}finally{
			
		}
	}
	
	/**
	 * Enhancement #21694
	 * METHOD TO UPDATE REVERSE WORKFLOW STATUS
	 */
	public void updateReverseScWorkflowStatus(QuationTenderReqDTO quationTenderReqDTO){
		IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
		IScWorkFlowStatusSO workFlowStatusSO;
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		
		
		if(null==this.getManagedBean().getTaskId() || this.getManagedBean().getTaskId().isEmpty()){
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			if(null!=quationTenderReqDTO.getScLoiForQt()){
				ScLoiWorkFlowStatus scLoiWorkFlowStatus = new ScLoiWorkFlowStatus();
				reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
				reqDTO.setDocId(quationTenderReqDTO.getScLoiForQt().getLoiId());	
				statusReqDTO.setStatusId(QTStatusConst.LOI_REVERSED);
				scLoiWorkFlowStatus.setDocType(reqDTO.getDocType());
				scLoiWorkFlowStatus.setDocId(reqDTO.getDocId());
				scLoiWorkFlowStatus.setScLoi(quationTenderReqDTO.getScLoi());
				scLoiWorkFlowStatus.setPmStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				scLoiWorkFlowStatus.setCurrent(Boolean.TRUE);
				reqDTO.setScWorkFlowStatus(scLoiWorkFlowStatus);
			}if(null!=quationTenderReqDTO.getScLoaForQt()){
				ScLoaWorkFlowStatus scLoaWorkFlowStatus = new ScLoaWorkFlowStatus();
				reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
				reqDTO.setDocId(quationTenderReqDTO.getScLoaForQt().getLoaId());
				statusReqDTO.setStatusId(QTStatusConst.LOA_REVERSED);
				scLoaWorkFlowStatus.setDocType(reqDTO.getDocType());
				scLoaWorkFlowStatus.setDocId(reqDTO.getDocId());
				scLoaWorkFlowStatus.setScLoa(quationTenderReqDTO.getScLoa());
				scLoaWorkFlowStatus.setPmStatus(statusSO.retrieveStatusByStatusId(statusReqDTO).getPmStatus());
				scLoaWorkFlowStatus.setCurrent(Boolean.TRUE);
				reqDTO.setScWorkFlowStatus(scLoaWorkFlowStatus);
			}
			
			workFlowStatusSO.updateCurrentWorkflowStatus(reqDTO);				
		}
	}
	
	/**
	 * Enhancement #21694
	 * Method to UPDATE Sc_QT_RecommendDtl (isLoaAwarded/isLoiAwarded)
	 */
	public void updateScQtRecommendDtl(){
		ScLoiForQt scLoiForQt = this.getManagedBean().getScLoiForQt();
		ScLoaForQt scLoaForQt = this.getManagedBean().getScLoaForQt();
		Long loiLoaId = null;
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTDetailSO.class);
		
		/*******************
		 * UPDATE SC_LOI_LOA
		 *******************/
		if(scLoiForQt!=null){
			loiLoaId = scLoiForQt.getScLoiLoa().getLoiLoaId();
			
		}else if(scLoaForQt!=null){
			loiLoaId = scLoaForQt.getScLoiLoa().getLoiLoaId();
		}
		
		if(loiLoaId!=null){
			ScLoiLoa loiLoa = new ScLoiLoa();
			if(null!=scLoiForQt){
				reqDTO.setLoiLoaId(loiLoaId);
				loiLoa = detailSO.findScLoiLoaByLoiLoaId(reqDTO).getScLoiLoa();
				if(loiLoa!=null){
					loiLoa.setLoiRequired(null);
					reqDTO.setScLoiLoa(loiLoa);
					detailSO.saveScLoiLoaByLoiLoa(reqDTO);
				}
			}			
		}
		
		/****************************
		 * UPDATE SC_LOI_LOA_ITEM
		 *//*
		if(null!=scLoiForQt){
			for(ScLoiLoaItem items : scLoiForQt.getScLoiLoaItems()){
				reqDTO = new QuationTenderReqDTO();
				reqDTO.setScLoiLoaItem(items);
				ScLoiLoaItem resLoiLoaItem = detailSO.retrieveScLoiLoaItem(reqDTO).getScLoiLoaItem();
				resLoiLoaItem.setScLoi(null);
				detailSO.saveScLoiLoaItem(resLoiLoaItem);
			}
		}else if (null!=scLoaForQt){
			for(ScLoiLoaItem items : scLoaForQt.getScLoiLoaItems()){
				reqDTO = new QuationTenderReqDTO();
				reqDTO.setScLoiLoaItem(items);
				ScLoiLoaItem resLoiLoaItem = detailSO.retrieveScLoiLoaItem(reqDTO).getScLoiLoaItem();
				resLoiLoaItem.setScLoa(null);
				detailSO.saveScLoiLoaItem(resLoiLoaItem);
			}
		}*/
		
		/****************************
		 * UPDATE SC_QT_RECOMMEND_DTL
		 ****************************/
		
		if(scLoiForQt!=null){
			for(ScLoiLoaItem items : scLoiForQt.getScLoiLoaItems()){
				reqDTO = new QuationTenderReqDTO();
				reqDTO.setScLoiLoaItem(items);
				ScQtRecommendDtl recDtl = detailSO.retrieveScQtRecommendDtl(reqDTO).getScQtRecommendDtl();
				recDtl.setLoiAwarded(null);
				recDtl.setLoaAwarded(null);
				recDtl.setRecordStatusFlg(QTStatusConst.LOI_STATUS_REVERSED);
				detailSO.saveScQtRecommendDtl(recDtl);
			}
		}		
		
		if(scLoaForQt!=null){
			for(ScLoiLoaItem items : scLoaForQt.getScLoiLoaItems()){
				reqDTO = new QuationTenderReqDTO();
				reqDTO.setScLoiLoaItem(items);
				ScQtRecommendDtl recDtl = detailSO.retrieveScQtRecommendDtl(reqDTO).getScQtRecommendDtl();
				recDtl.setLoiAwarded(null);
				recDtl.setLoaAwarded(null);
				recDtl.setRecordStatusFlg(QTStatusConst.LOA_STATUS_REVERSED);
				detailSO.saveScQtRecommendDtl(recDtl);
			}
		}
		
	}
	
	public void saveReverseLoiLoaDtl(){
		ScQtReverse scQtReverse;
		List<ScLoiReverseRemark> loiRemarks;
		List<ScLoaReverseRemark> loaRemarks;
		loiRemarks = this.getManagedBean().getScLoiReverseRemarks();
		loaRemarks = this.getManagedBean().getScLoaReverseRemarks();
		scQtReverse = this.getManagedBean().getScQtReverse();
		if(null!=scQtReverse){
			scQtReverse.setRecordStatusFlg(Integer.parseInt(ParameterConst.RECORD_STATUS_ACTIVE));
		}
		
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTDetailSO.class);
		reqDTO.setScQtReverse(scQtReverse);			
		
		/*
		 * VALIDATE REMARK
		 */
		validateReverseRemark();
		
		/*
		 * RESPONSE BACK TO MANAGED BEAN
		 */
		scQtReverse = detailSO.saveQtReverse(reqDTO).getScQtReverse();
		if(loiRemarks!=null && !loiRemarks.isEmpty()){
			for(ScLoiReverseRemark scLoiRemarks : loiRemarks){
				scLoiRemarks.setScQtReverse(scQtReverse);	
				reqDTO.setScLoiReverseRemark(scLoiRemarks);
				detailSO.saveLoiLoaReverseRemarks(reqDTO).getScLoiReverseRemark();
			}
		}
		
		if(loaRemarks!=null && !loaRemarks.isEmpty()){
			for(ScLoaReverseRemark scLoaRemarks : loaRemarks){				
				scLoaRemarks.setScQtReverse(scQtReverse);
				reqDTO.setScLoaReverseRemark(scLoaRemarks);
				detailSO.saveLoiLoaReverseRemarks(reqDTO).getScLoaReverseRemark();
			}
		}
		
		this.getManagedBean().setScQtReverse(scQtReverse);
		this.getManagedBean().setScLoiReverseRemarks(loiRemarks);
		this.getManagedBean().setScLoaReverseRemarks(loaRemarks);
	}
	
	/**
	 * Enhancement #21694 START 
	 * Page navigation from reverse screen to qtDetailManageBean.backToPreviousPage
	 */
	public void backToPreviousReverse() {
		debugLogger("backToPreviousReverse");
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						getManagedBean().getPreviousPage());
	}
	
	/**
	 * Enhancement #21694 START (SHAFFUL)
	 * to be used in QT Maintenance Only
	 * @param obj
	 * 		instance of ScQt/ScLoiForQt/ScLoaForQt
	 * @param previousPageUrl
	 * @return
	 */
	public String viewReverseStatusDetailsGen(Object obj, String previousPageUrl) {
		
		debugLogger("viewReverseStatusDetails");
		FacesContext fc = FacesContext.getCurrentInstance();
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		getManagedBean().setPreviousPage(previousPageUrl); 
		ScQt scQt;
		ScLoiForQt scLoiForQt;
		ScLoaForQt scLoaForQt;
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		this.getManagedBean().setEditable(Boolean.TRUE);
		if(getManagedSearchBeanDocTypeIsLOAAndPrefixLAAndIsDO() || getManagedSearchBeanDocTypeIsLOIAndPrefixLIAndIsDO()){
			
			if(obj instanceof ScLoiForQt ){
				scLoiForQt = (ScLoiForQt)obj;
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
					scQt = scLoiForQt.getScLoiLoa()!=null?scLoiForQt.getScLoiLoa().getScQt():null;
					
					
					 /* FINDING WORKFLOW STATS*/
					 
					reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
					reqDTO.setDocId(scLoiForQt.getLoiId());					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					this.getManagedBean().setQt(scQt);
					this.getManagedBean().setScLoiForQt(scLoiForQt);
					this.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, reverseQtDetails(Boolean.TRUE,Boolean.FALSE,null));
				}
			}
			else if(obj instanceof ScLoaForQt){
				scLoaForQt = (ScLoaForQt)obj;
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)){
					Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
					otherArgs.put(MapKeyEnum.loiLoaSelected, scLoaForQt);
					scQt = scLoaForQt.getScLoiLoa()!=null?scLoaForQt.getScLoiLoa().getScQt():null;
					
					
					 /* FINDING WORKFLOW STATS*/
					 
					reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
					reqDTO.setDocId(scLoaForQt.getLoaId());					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					this.getManagedBean().setQt(scQt);
					this.getManagedBean().setScLoaForQt(scLoaForQt);
					this.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, reverseQtDetails(Boolean.FALSE,Boolean.TRUE,null));
				}
			}
			else;
		}
		
		return null;
	}
	
	/**
	 * Enhancement #21694
	 * Show prepared by.
	 *
	 * @param createdBy the created by
	 * @return the string
	 */
	public String showPreparedBy(Long createdBy) {
		String userName = "";
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (createdBy != null) {
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);

			if (resDTO.getPmUser() != null) {
				userName = resDTO.getPmUser().getUserName();
			}
		}
		return userName;
	}
	
	/**
	 * Gets the qt award so.
	 * 
	 * @return the qt award so
	 */
	private IQTAwardSO getQtAwardSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAwardSO.class);
	}
	
	/*
	 * Enhancement #21694 END
	 */
	
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
	 * Enhancement #21694
	 * Find Loi Based on ReqDTO
	 * 
	 * @param QuationTenderReqDTO
	 * @return	ScLoi
	 */
	public ScLoi findScLoiForQtByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.findScLoiForQt(reqDTO).getScLoi();			
		} finally {
			qtDetailSO = null;
		}
	}
	
	/**
	 * Enhancement #21694
	 * Find ScQtReverse Based on ReqDTO
	 * 
	 * @param QuationTenderReqDTO
	 * @return	ScQtReverse
	 */
	public ScQtReverse findScQtReverseByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.findScReverseQt(reqDTO).getScQtReverse();			
		} finally {
			qtDetailSO = null;
		}
	}
		
	/**
	 * Enhancement #21694
	 * Find ScQtReverse Based on ReqDTO
	 * 
	 * @param QuationTenderReqDTO
	 * @return	ScQtReverse
	 */
	public List<ScLoiReverseRemark> findScQtReverseLoiRemarksByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.findScReverseLoiLoaRemarks(reqDTO).getScLoiReverseRemarks();			
		} finally {
			qtDetailSO = null;
		}
	}
	
	/**
	 * Enhancement #21694
	 * Find ScQtReverse Based on ReqDTO
	 * 
	 * @param QuationTenderReqDTO
	 * @return	ScQtReverse
	 */
	public List<ScLoaReverseRemark> findScQtReverseLoaRemarksByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.findScReverseLoiLoaRemarks(reqDTO).getScLoaReverseRemarks();			
		} finally {
			qtDetailSO = null;
		}
	}
	
	
	/**
	 * Enhancement #21694
	 * Find Loa Based on ReqDTO
	 * 
	 * @param QuationTenderReqDTO
	 * @return	ScLoa
	 */
	public ScLoa findScLoaForQtByReqDTO(QuationTenderReqDTO reqDTO) {
		IQTDetailSO qtDetailSO;
		try {
			qtDetailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			return qtDetailSO.findScLoaForQt(reqDTO).getScLoa();			
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
				// TODO need to check why updating the twice.. sagar
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
		/*if (CollectionUtils.isNotEmpty(scQt.getScQtApprovals())) {
			for (ScQtApproval qtApproval : scQt.getScQtApprovals()) {
				qtApproval.setScQtApprovalRemarks(
						finalizeRemarks(qtApproval
								.getScQtApprovalRemarks()));
			}
		}
*/
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
						BriefingZoneBean briefingsamplebean=null;//11104-start
						if(CollectionUtils.isNotEmpty(this.getManagedBean().getSampleAddressBean())){
							for(BriefingZoneBean briefBean: this.getManagedBean().getSampleAddressBean()){
								if(briefBean.getTplSpecZoneDtl().getSpecZoneId().equals(scQtSampleSubmission.getTplSpecZoneDtl().getSpecZoneId())|| scQtSampleSubmission.getTplSpecZoneDtl().getSpecZoneId()==scQtSampleSubmission.getTplSpecZoneDtl().getSpecZoneId()){
									briefingsamplebean=briefBean;	
								}							
							}
						}//11104-End

						if (null != scAddress) {
							if (scAddress.getPmDivision() != null
									&& scAddress.getPmDivision()
											.getDivisionId() == null) {
								scAddress.setPmDivision(null);
								if(null!=briefingsamplebean && null!=briefingsamplebean.getDivisionId()){//11104-start
									scAddress.setPmDivision(retrievePmDivsById(briefingsamplebean.getDivisionId()));
								}//11104-End
							}
							if(null!=scAddress.getPmState() && scAddress.getPmState().getStateId()==null){//11104-start
								if(null!=briefingsamplebean && null!=briefingsamplebean.getStateId()){
									scAddress.setPmState(retrievePmStateById(briefingsamplebean.getStateId()));
								}
							}
							if(null!=scAddress.getPmDistrict() && scAddress.getPmDistrict().getDistrictId()==null){
								if(null!=briefingsamplebean && null!=briefingsamplebean.getDistrictid()){
									scAddress.setPmDistrict(retrievePmDistById(briefingsamplebean.getDistrictid()));
								}
							}
							if(null!=scAddress.getPmCity() && scAddress.getPmCity().getCityId()==null){
								if(null!=briefingsamplebean && null!=briefingsamplebean.getCityId()){
									scAddress.setPmCity(retrievePmCityById(briefingsamplebean.getCityId()));
								}
							}//11104-End
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
			List<ScQtContactPerson> deskOfficerAdminList = this.getManagedBean().getPmuserContactpersonList();
			List<ScQtContactPerson> contactList = new ArrayList<ScQtContactPerson>();

			if(this.getManagedBean().isMaintanceFlow() && StringUtils.isEmpty(this.getManagedBean().getTaskName())){//25037 - 497
				if (CollectionUtils.isNotEmpty(scQt.getRequestContents()) && this.getManagedBean().getRequestContent()!=null ) { 
					ScQtRequestContent requestContent = this.getManagedBean().getRequestContent();
					deskOfficerAdminList = deskOfficerAdminList==null? new ArrayList<ScQtContactPerson>(): deskOfficerAdminList;
					requestContent.setModified(isDOAListModified());
					requestContent.setDeleted(requestContent.isBrandNew() && !requestContent.isModified());//31112 - remove request content container when no modification 
					if(requestContent.isModified()){
						requestContent.setRequireApproval(true);
						Map<Long, String> map = new HashMap<Long, String>();
						for(ScQtContactPerson p:deskOfficerAdminList){
							if(p.getUser().getUserId()==null){
								throw new EpException(getMessageCommon("lbl.sc.required.select.doa"));
							}
							map.put(p.getUser().getUserId(), p.getUser().getLoginId());
						}
						try {
							requestContent.setRequestContent(map);
						} catch (IOException e) {
							throw new EpException(e.getMessage());
						}
					}
				}
			}else if(CollectionUtils.isNotEmpty(deskOfficerAdminList)){
				contactList.addAll(deskOfficerAdminList);
			}
			if (contactPersonsList != null && !contactPersonsList.isEmpty()) {
				contactList.addAll(contactPersonsList);
			}
			if (null != contactPersonsList) {
				scQt.setScQtContactPersons(contactList);
			}

			if(!(getUserTaskIsPublicationApproval() && isApprovalScreen())){ // #25077 - Start 
				
				ScQtApproval pubApproval = this.getManagedBean()
						.getPublicationApproval();
				if (null != pubApproval) {
					List<ScQtApproval> approvals = scQt.getScQtApprovals();
					removePTJApproval(approvals);
					if (null != this.getManagedBean().getPublicationApprovals()) {
						List<ScQtApproverSettings> approvalSettings = this
								.getManagedBean().getPublicationApprovals()
								.getTarget();
						if (CollectionUtils.isNotEmpty(approvalSettings)) {
							for (ScQtApproverSettings approverSettings : approvalSettings) {
								approverSettings.setScQtApproval(pubApproval);
							}
							pubApproval.setApproverSettings(approvalSettings);
						}
	
					}
					/**List<ScQtApprovalRemark> remarks = pubApproval
							.getScQtApprovalRemarks();
					finalizeRemarks(remarks);**/
					approvals.add(pubApproval);
					scQt.setScQtApprovals(approvals);
				}
			
			} // #25077 - End

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
		
		//#29474 Setting the fulfilment to qtdetails.
		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_FREQUENCY_SCHEDULE
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtSchedule> freqschedule = this.getManagedBean()
					.getScheduleList();
			List<ScQtSchedule> localScheduleList = new ArrayList<ScQtSchedule>();
			if (freqschedule != null && !freqschedule.isEmpty()) {

				for (ScQtSchedule schedule : freqschedule) {
					String Startmonth = "";
					String endmonth = "";
					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}

					SimpleDateFormat df = new SimpleDateFormat(
							CommonConstants.DEFAULT_DATE_FORMAT);
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();

					schedule.setScheduleNo(schedule.getScheduleNo());

					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
					localScheduleList.add(schedule);

				}
				scQt.setScQtSchedules(localScheduleList);
			} else {
				scQt.setScQtSchedules(null);
			}
		}

		if (ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT
				.equalsIgnoreCase(scQt.getScheduleType())) {
			List<ScQtProgressivePayment> progressivePayemnt = this
					.getManagedBean().getProgressivePaymentList();
			List<ScQtProgressivePayment> paymentList = new ArrayList<ScQtProgressivePayment>();
			if (progressivePayemnt != null && !progressivePayemnt.isEmpty()) {
				String Startmonth = "";
				String endmonth = "";
				for (ScQtProgressivePayment schedule : progressivePayemnt) {

					if (StringUtils.isNotBlank(schedule.getStartMonth())) {
						Startmonth = retriveMonthDtls(new Integer(
								schedule.getStartMonth()));
					}

					if (StringUtils.isNotBlank(schedule.getEndMonth())) {
						endmonth = retriveMonthDtls(new Integer(
								schedule.getEndMonth()));
					}
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					String fromdate = 01 + "/" + Startmonth + "/"
							+ schedule.getStartYear();
					String toDate = 01 + "/" + endmonth + "/"
							+ schedule.getEndYear();
					try {
						schedule.setFromDate(df.parse(fromdate));
						schedule.setToDate(df.parse(toDate));
					} catch (ParseException e) {
						log.debug(e.getMessage());
					}

					schedule.setScQt(this.getManagedBean().getQt());
					paymentList.add(schedule);

				}
				scQt.setScQtProgressivePayments(paymentList);
			} else {
				scQt.setScQtProgressivePayments(null);
			}
		}
		//#29474 - end
		
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
       
       //9019 for prevent null insert for remark
		if (null != scQt.getScQtRemarks() && CollectionUtils.isNotEmpty(scQt.getScQtRemarks())) {
			for (int i = 0; i < scQt.getScQtRemarks().size(); i++) {
				String userRemark = scQt.getScQtRemarks().get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					scQt.getScQtRemarks().remove(i);
				}
			}
		}// 9019 end

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
		//#29474 remove fulfilment. - start
		quationTenderReqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
				.getToBeRemoveSchedules());
		quationTenderReqDTO.setToBeRemoveProgressivePayments(this.getManagedBean()
				.getToBeRemoveProgPayments());
		//#29474 - end
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
		List<ScQtCategory> removeCategoryCodes = this.getManagedBean()
				.getTobeRemoveCategoryCodes();
		if (null != removeCategoryCodes && !removeCategoryCodes.isEmpty()) {
			for (int i = 0; i < removeCategoryCodes.size(); i++) {
				if (null == removeCategoryCodes.get(i).getQtCategoryId()) {
					removeCategoryCodes.remove(removeCategoryCodes.get(i));
				}
			}
		}
		List<ScQtContactPerson> removeContactPerson=	this.getManagedBean().getToBeRemovalContactPersonList();
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
		quationTenderReqDTO.setMaintanceFlow(this.getManagedBean()
				.isMaintanceFlow());
		quationTenderReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
		quationTenderReqDTO.setUserLocale(new FacesScopes().getUserLocale());
		
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
	
	
	private void populateQTLOISearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScLoiForQt> qtSearchBean){
		
		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean
					.getDataFilterBeans())) {
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
			
			//replaced by subquery at DAO
			/*if(!qtnTenderSearchBean.isQtStatusForQt() && StringUtils.isNotBlank(qtnTenderSearchBean.getQtStatus())){
				searchFilterBean.add(
						new SearchFilterBean("scLoiWorkFlowStatuses.pmStatus.statusId", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								qtnTenderSearchBean.getQtStatus()));
				searchFilterBean.add(
						new SearchFilterBean("scLoiWorkFlowStatuses.current", 
								SearchOperator.OPERATOR_EQUALS.value(), 
								Boolean.TRUE));
			}*/
		
		
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			reqDTO.setLoiForQtSearchBean(qtSearchBean);
		}
	}
	
	
	private void populateLoaForQtSearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScLoaForQt> qtSearchBean){
		
		if (qtnTenderSearchBean != null) {
			if (CollectionUtils.isNotEmpty(qtSearchBean
					.getDataFilterBeans())) {
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
						)
							{
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
			
			//30418-START
			//Special Case for LOA Doctype QT Maintenance Search, procurement mode is search against SC_LOA instead of SC_QT
			if (StringUtils.isNotEmpty(qtnTenderSearchBean.getProcurementMode())) {
				searchFilterBean.add(new SearchFilterBean(
						"procurementMode.parameterCode",
						SearchOperator.OPERATOR_EQUALS.value(),
						qtnTenderSearchBean.getProcurementMode()));
			}
			//30418-END
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			reqDTO.setLoaForQtSearchBean(qtSearchBean);
		}
	}

	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param qtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 * @param isBidding 
	 */
	// #30451-Start.
	private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean, boolean isBidding) {
	// #30451-End.
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
				reqDTO.setDocType(docType);
				if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) 
						|| ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)
						|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)
						|| ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(docType)) {
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
			/*BUG 31120 START*/
			if (qtnTenderSearchBean.getPtjProfile() != null && qtnTenderSearchBean.getPtjProfile().getOrgProfileId() != null) {
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField(QTConstants.QT_ORG_PROFILE);
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(qtnTenderSearchBean.getPtjProfile().getOrgProfileId());
				searchFilterBean.add(bean);
			}
			/*BUG 31120 END*/
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode) && !ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)) {
				searchFilterBean.add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			}else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType) 
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
			/*
			 * //9019 removed for to include SAVED QT without all mandatory all values
			 * else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				// ***17376#55 - start
				//if (!(isQTMaintenanceScreen() || isQTSearchScreen())) {
			    procurementModes.add(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO);	
				//}
				// ***17376#55 - end 
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			}*/
			
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
				// #30451-Start.
				if(qtnTenderSearchBean.isQtStatusForQt() || isBidding) {
				// #30451-End.
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
					if(status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION.toString())) {
						statusBean = new SearchMultiFilterBean(
								statusFilterBean, 
								new SearchFilterBean(
										"scQtStatus.pmStatus",
										SearchOperator.OPERATOR_IS_NULL.value(),
										null,
										SearchCondition.CONDITION_OR.value(),
										JoinType.LEFT));
					}
					qtSearchBean.getDataFilterBeans().add(statusBean);
				}
				else{
					try{
						reqDTO.setStatusId(Long.parseLong(status));
					}
					catch(ClassCastException ex){
						
					}
				}
			// #30451-Start.
			} else if (isBidding) {
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(QTStatusConst.SF_PENDING_SCHEDULE_BIDDING);
				statusIds.add(QTStatusConst.SF_PENDING_BIDDING);
				statusIds.add(QTStatusConst.SF_PENDING_BIDDING_ESCALATION);
				SearchMultiFilterBean statusBean = new SearchMultiFilterBean(
						new SearchFilterBean(
								"scQtStatus.pmStatus.statusId",
								SearchOperator.OPERATOR_IN.value(),
								statusIds,
								JoinType.LEFT), 
						new SearchFilterBean(
								"scQtStatus.current",
								SearchOperator.OPERATOR_EQUALS.value(),
								true,
								JoinType.LEFT));
				qtSearchBean.getDataFilterBeans().add(statusBean);
			// #30451-End.
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
					
				 /* Enhancement #27668
				 * TO INCLUDE QT WHICH PPB SECRETARY INVOLVE IN IT, IF HIGHER_AUTHORITY LINK ALSO
				 * AND WITH PTJ HIMSELF */
				SearchFilterBean secretaryFilterBean = new SearchFilterBean(QTConstants.FINALIZATION_SECRETARY_LOGIN_ID,
						SearchOperator.OPERATOR_EQUALS.value(), new FacesScopes().getUserSessionBean().getLoginId(),JoinType.LEFT);
				
				SearchMultiFilterBean secretartStatusBean = null;
				secretartStatusBean = new SearchMultiFilterBean(
						secretaryFilterBean, 
						new SearchFilterBean(
								QTConstants.FINALIZATION_SECRETARY_LOGIN_ID,
								SearchOperator.OPERATOR_IS_NULL.value(),
								null,
								SearchCondition.CONDITION_OR.value()));

				SearchFilterBean highAuthFilterBean = new SearchFilterBean(QTConstants.FINALIZATION_HIGH_AUTH_SECRETARY_LOGIN_ID,
						SearchOperator.OPERATOR_EQUALS.value(), new FacesScopes().getUserSessionBean().getLoginId(),JoinType.LEFT);
				SearchMultiFilterBean highAuthStatusBean = null;
				highAuthStatusBean = new SearchMultiFilterBean(
						highAuthFilterBean, 
						new SearchFilterBean(
								QTConstants.FINALIZATION_HIGH_AUTH_SECRETARY_LOGIN_ID,
								SearchOperator.OPERATOR_IS_NULL.value(),
								null,
								SearchCondition.CONDITION_OR.value()));
				
				PmOrgProfile ptjProfiles = qtnTenderSearchBean.getPtjProfile();
				SearchFilterBean searchFilterBean1 = null;
				if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
					// Filter By - PTJ
					searchFilterBean1 = new SearchFilterBean(
							"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DIVISION.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjProfiles.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
				} else {
					// Filter By - PTJ Group
					PmOrgProfile ptjGroupProfile = qtnTenderSearchBean.getPtjGroupProfile();
					if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
						searchFilterBean1 = new SearchFilterBean(
								"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ptjGroupProfile.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
					} else {
						// Filter By - Ministry
						PmOrgProfile ministryProfile = qtnTenderSearchBean.getMinistryProfile();
						if (ministryProfile != null
								&& ministryProfile.getOrgProfileId() != null) {	
							searchFilterBean1 = new SearchFilterBean(
									"pmOrgProfile." + OrgProfileUtil
										.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
									SearchOperator.OPERATOR_EQUALS.value(),
									ministryProfile.getOrgProfileId(),SearchCondition.CONDITION_OR.value());
						}
					}						
				}
				if(searchFilterBean1!=null){
					SearchMultiFilterBean searchFilterBeanOr1 = new SearchMultiFilterBean(secretartStatusBean,SearchCondition.CONDITION_OR.value(),highAuthStatusBean);
					SearchMultiFilterBean searchFilterBeanAnd2 = new SearchMultiFilterBean(searchFilterBeanOr1,searchFilterBean1);
					qtSearchBean.getDataFilterBeans().add(searchFilterBeanAnd2);					
					
				}					
			}

			// Adding search filterbeans to searchbean.
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			
			reqDTO.setQtSearchBean(qtSearchBean);
		}
	}
	
	public boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	// ***17376#42 31120#29
	public boolean isQTSearchScreen() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	// ***17376#55
	private boolean isQtTechSupportSearchScreen(){
		return QTConstants.PORTLET_NAME_QT_TECHSUPP.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	
	

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
	public void retrieveCommitteeAppointmentTabDtl(int formCommitteeFlag, int reformCommitteeFlag) {
		
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
		List<TabBean> committeTabs = this.getManagedBean()
				.getScCommitteeMemberTabList();
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
		//20210325@asyrafhaziq_30518 - Start
		 if(getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()){
			 log.debug("====mangaNewCodesExecuted========");
			 committeTabs=null;
		 }
		Long qtId = scQt.getQtId();
		
		//#27502 -Start	
		boolean isBpkCentralContract = false;
		isBpkCentralContract = isBpkCentralContract(scQt);
		//#27502 -End
		
		String taskName = this.getManagedBean().getTaskName()==null ? "": this.getManagedBean().getTaskName(); //25037 #372
		
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
					//25037-start #372
					if(!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)
							&& !evalCommitte.getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CMT_PENDING_REVISION))){
						
						committeTabs.add(new TabBean(parameterHelper
								.getParameterDesc(parameterType, tabType), tabType,
								evalCommitte));
					}//25037-end #372
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
					//25037-start #372
					if(!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)
							&& !openingCommitte.getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CMT_PENDING_REVISION))){
					
						committeTabs.add(new TabBean(parameterHelper
								.getParameterDesc(parameterType, tabType), tabType,
								openingCommitte));	
					}//25037-end #372

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
					//25037-start #372
					if(!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)
							&& !tecCommitte.getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CMT_PENDING_REVISION))){
					
						committeTabs.add(new TabBean(parameterHelper
								.getParameterDesc(parameterType, tabType), tabType,
								tecCommitte));	
					}//25037-end #372

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
					//25037-start #372
					if(!(Arrays.asList(QTStatusConst.QT_DOC_SCHEDULE_GROUP).contains(this.getManagedBean().getCurrentStatusId())
							&& taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)
							&& !fecCommitte.getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CMT_PENDING_REVISION))){
					
						committeTabs.add(new TabBean(parameterHelper
								.getParameterDesc(parameterType, tabType), tabType,
								fecCommitte));	
					}//25037-end #372
				}
				
				//#27502 -Start				
				if (isBpkCentralContract) {
					StringBuilder fcTitleLabel = new StringBuilder("lbl.ministry.finance");
					String fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
					tabType = SourcingConstants.COMMITTE_TYPE_MINISTRY_OF_FINANCE;
					ScQtCommittee mofCommitte = getQtCommiteDtlsByQtIdNType(tabType, qtId);
					committeTabs.add(new TabBean(fcTitleName, tabType, mofCommitte));

				} else {
					// FC tab
					PmParameter fcSecretaryCode = scQt.getFinalizationCommitte();
					tabType = SourcingConstants.COMMITTE_TYPE_FINALIZATION;
					StringBuilder fcTitleLabel = new StringBuilder("lbl.sc.finalization.committee.secretary.");
					String fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString()
							+ SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE.toLowerCase(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
					if (fcSecretaryCode == null || fcSecretaryCode.getParameterId() == null) {
						SelectItem[] fcSecretaries = retrieveFinalizationCommitteeType();
						if (fcSecretaries != null && fcSecretaries.length > 0) {
							fcSecretaryCode = retrieveParameterById(Long
									.valueOf(fcSecretaries[0].getValue().toString()));
						} else {
							fcSecretaryCode = null;
						}
					}

					// Displaying the Secretary Details
					if (fcSecretaryCode != null) {

						String fcSecretary = fcSecretaryCode.getParameterCode();

						if (fcSecretary != null) {
							if (fcSecretary.indexOf(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE) >= 0) {
								fcTitleLabel.append(SourcingConstants.FC_COMMITTEE_TYPE_QUOTATION_COMMITTEE);
							} else {
								fcTitleLabel.append(SourcingConstants.FC_COMMITTEE_TYPE_PROCUREMENT_BOARD);
							}
							fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString().toLowerCase(),
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
						}
					}

					committeTabs.add(new TabBean(fcTitleName, tabType, null));
				}
			}
		} else {		
			if (isBpkCentralContract) {
				List<TabBean> newCommitteTabs = new ArrayList<TabBean>();
				if (!scQt.getQtStatus().getStatusId().equals(QTStatusConst.QT_PENDING_QT_SUBMISSION) 
						&& !scQt.getQtStatus().getStatusId().equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL) 
						&& !Arrays.asList(QTStatusConst.CMT_APPROVAL_GROUP).contains(scQt.getQtStatus().getStatusId())) {
					for (TabBean t : committeTabs) {
						if (t.getTabType().equals(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)) {
							newCommitteTabs.add(t);
						} else if (t.getTabType().equals(ParameterConst.COMMITTEE_TYPE_EVALUATION_COMMITTEE)) {
							newCommitteTabs.add(t);
						}
						else if (t.getTabType().equals(ParameterConst.COMMITTEE_TYPE_OPENING_COMMITTEE)) {//32775-start related to 27502
							newCommitteTabs.add(t);
						}
						else if (t.getTabType().equals(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE)) {
							newCommitteTabs.add(t);
						}
						else if (t.getTabType().equals(ParameterConst.COMMITTEE_TYPE_FINANCIAL_EVALUATION_COMMITTEE)) {
							newCommitteTabs.add(t);
						}//32775-End related to 27502
					}

					StringBuilder fcTitleLabel = new StringBuilder("lbl.ministry.finance");
					String fcTitleName = ResourceBundleUtils.getMessage(fcTitleLabel.toString(),
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
					String tabType = SourcingConstants.COMMITTE_TYPE_MINISTRY_OF_FINANCE;
					ScQtCommittee mofCommitte = getQtCommiteDtlsByQtIdNType(tabType, qtId);
					newCommitteTabs.add(new TabBean(fcTitleName, tabType, mofCommitte));

					committeTabs = newCommitteTabs;
				}
			}//#27502 - End
		}

		this.getManagedBean().setScCommitteeMemberTabList(committeTabs);

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
							.getFinalizationSecratery().getUserId() != null) || 
							SourcingConstants.COMMITTE_TYPE_MINISTRY_OF_FINANCE.equals(tabBean.getTabType())) {//27502
				tabBean.setIcon("ui-icon-endorse");
			}
		}
		if (!committeTabs.isEmpty()) {
			// this.getManagedBean().setSelectedTabBean(committeTabs.get(0));
			this.getManagedBean().setSelectedTabBean(
					committeTabs.get(this.getManagedBean()
							.getSelectedCommitteeTabIndex()));
			String committeeType = this.getManagedBean().getSelectedTabBean().getTabType();
			this.getManagedBean().setRoleFilterList(getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), committeeType));
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

	//two
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
				SCCommitteeDataType committeDataType = null;
				this.getManagedBean().setActiveIndex(2);//29464
				if (SCProcessNameConst.ASSIGN_EC_OC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_TEC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_FEC_COMMITTEE.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_FC_SECRETARY.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
								.equalsIgnoreCase(taskName)) {

					SCEvaluationDataType evalDataType = (SCEvaluationDataType) payLoad;
					qtId = evalDataType.getQtId().longValue();
					
				} else {
					
					committeDataType = (SCCommitteeDataType) payLoad;
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
					//29464-start
					this.getManagedBean().setActiveIndex(5);
					this.getManagedBean().setCommitteeAppointmentMenuTab(true); 
					//29464-end
					retrieveCommitteeAppointmentTabDtl(FORM_SC, REFORM_SC);
					
				} else {
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
					if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
						initCommitteeTabSingularReapproval(committeDataType);
					}
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
            //9019 for load existing values
           if(null==this.getManagedBean().getQt().getProcurementTypeCatId()){
				this.getManagedBean().getQt().setProcurementTypeCatId(new PmParameter());	
			}
			if(null==this.getManagedBean().getQt().getFulfilmentType()){
				this.getManagedBean().getQt().setFulfilmentType(new PmParameter());	
			}
			if(null==this.getManagedBean().getQt().getPmProcurementCategory()){
				this.getManagedBean().getQt().setPmProcurementCategory(new PmParameter());	
			}
			if(null==this.getManagedBean().getQt().getContractType()){
				this.getManagedBean().getQt().setContractType(new PmParameter());	
			}
			//#29474 - start
			if (null == this.getManagedBean().getQt().getFulfilmentType()) {
				this.getManagedBean().getQt().setFulfilmentType(new PmParameter());

			}
			if (null != this.getManagedBean().getQt().getFulfilmentType()) {
				this.getManagedBean().setFulfilmentType(
						this.getManagedBean().getQt().getFulfilmentType()
								.getParameterCode());
			}
			//#29474 - end
			//9019 initializing remark for cancel or subsequent save start
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
			//9019 end


			if (SCProcessNameConst.REVISE_PCB.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				this.getManagedBean().setPcbProcess(true);
			}
			//#25692-start
			if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(this
					.getManagedBean().getTaskName())) {
				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(this.getManagedBean().getQt().getQtId(), ParameterConst.DOCTYPE_BPK_APPROVAL);
				this.getManagedBean().setSubStatusId(sts.getStatusId());
			}
			//#25692-End
			assertSpecificationUpdateInProgress(false);			
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
	public void updateBpmTask(String outcome) {
		
		ScQt scQt = this.getManagedBean().getQt();
		debugLogger("updateBpmTask");

		updateApprovalDetails(scQt);
		
		// #14398 - Start
		if (null != getManagedBean().getPublicationApproval() 
				&& CollectionUtils.isNotEmpty(getManagedBean().getPublicationApproval().getScQtApprovalRemarks())
				&& getManagedBean().getPublicationApproval().getScQtApprovalRemarks().size() > 0) {
			boolean remarkNotEmpty = true;
			for (ScQtApprovalRemark approvalRemark : getManagedBean().getPublicationApproval().getScQtApprovalRemarks()) {
				if (StringUtils.isEmpty(approvalRemark.getRemark())) {
					remarkNotEmpty = false;
					break;
				}
			}
			
			if (remarkNotEmpty) {
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setApproval(getManagedBean().getPublicationApproval());
				getQTDetailSO().updateScQtApproval(reqDTO );
			}
		}
		// #14398 - End
		
		if (null!= this.getManagedBean().getTaskName() &&!SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(this.getManagedBean().getTaskName())){	//#28644-start	
			// #30435- Start.
			if (!getManagedBean().isApproverTabLoaded()) {
				retrieveApprovalTabDtl();
			}
			// #30435 - End.
		} //#28644-End

		// Code will be called if user didn't visit the supplier qualification
		// tab.
		if (null == this.getManagedBean().getSupplierCriteria()) {
			retrieveSuppQualificationTabDtl();
		}

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
			//#25077 Start 
			if (currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
					|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION) 
					&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT) ){
				if(checkSettingPopUpBPKApprovalNeeded(qt)){
					if(!this.getManagedBean().isResetBpkCurrentArea()){
						checkingOtherApprovalFileAttachmentNeedDeleted(qt.getScQtApprovals(),ParameterConst.APPROVAL_CATEGORY_OTHERS);
						if(skipPopUpBPKApprovalNeededOnceApproved(qt.getScQtApprovals(),ParameterConst.APPROVAL_CATEGORY_BPK)){
					      this.getManagedBean().setShowPopUpBPKApprovalNeeded(true);
						  RequestContext.getCurrentInstance().execute("bpkApprovalNeededDlgTemp.show()");
						  throw new EpException(" ");
						}
					}
				}
				checkingOtherApprovalFileAttachmentNeedDeleted(qt.getScQtApprovals(),ParameterConst.APPROVAL_CATEGORY_OTHERS);
				checkValidationForStatusAndFileAttachment(qt.getScQtApprovals(),ParameterConst.APPROVAL_CATEGORY_OTHERS);
			}
			//#25077 End 
			//#30477 Start Vinod
			if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
				//#11299 Start Vinod
				for(ScQtApproval approval : this.getManagedBean().getQt().getScQtApprovals()){
					List<ScQtApproverSettings> approverSettings = approval.getApproverSettings();
					if(CollectionUtils.isEmpty(approverSettings)){
						throw new EpException(MessageConst.ERROR_775174);
					}
				}
				//#11299 End Vinod
				for(ScQtCommittee committee : this.getManagedBean().getQt().getScQtCommittees()){
					if((committee.getScCommittee().getCommitteStatus().getStatusId().equals(QTStatusConst.CMT_PENDING_REVISION) 
							|| currentStatus.equals(QTStatusConst.QT_RE_EVALUATION))
							&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT)){
						for(ScQtApproval approval : this.getManagedBean().getQt().getScQtApprovals()){
							for(ScQtApproverSettings approverSetting : approval.getApproverSettings()){
								if(isInactiveCommiteeApproverRole(approverSetting.getPmUser().getUserId())){
									//#11299 Start Vinod
									throw new EpException(MessageConst.ERROR_775174);
									//throw new EpException(MessageConst.ERROR_775171);
									//#11299 End Vinod
								}
							}
						}
					}
				}
			}
			if(SCProcessNameConst.REVISE_QT_SCHEDULE.equalsIgnoreCase(taskName)) {
				//#11299 Start Vinod updated error message
				List<ScQtApproverSettings> approverSettings = this.getManagedBean().getPublicationApproval().getApproverSettings();
				if(CollectionUtils.isEmpty(approverSettings)){
					throw new EpException(MessageConst.ERROR_775174);
				}else{
					if((currentStatus.equals(QTStatusConst.QT_PENDING_SCHEDULE_REVISION)
							|| currentStatus.equals(QTStatusConst.QT_REVISION_PUBLICATION))
							&& outcome.equals(BpmOutcomeConst.BPM_SUBMIT))	{
						
								for(ScQtApproverSettings approverSetting : this.getManagedBean().getPublicationApproval().getApproverSettings()){
									if(isInactiveCommiteeApproverRole(approverSetting.getPmUser().getUserId())){
										throw new EpException(MessageConst.ERROR_775174);
										//throw new EpException(MessageConst.ERROR_775171);
									}
								}
					}
				}
				//#11299 End Vinod
			}
			//#30477 End Vinod
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
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				
			} else if (SCProcessNameConst.ASSIGN_FC_SECRETARY
					.equalsIgnoreCase(taskName)) {
				
				//#27502 - Start
				boolean isBpkCentralContract = false;
				isBpkCentralContract = isBpkCentralContract(scQt);
				if (isBpkCentralContract) {	
					committeSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
					CommitteReqDTO committeReqDTO = new CommitteReqDTO();
					committeReqDTO.setToken(ctxToken);
					committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
					committeReqDTO.setPayLoad(payLoad);
					committeReqDTO.setOutcome(outcome);
					committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
					committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
					committeReqDTO.setScQt(qt);
					committeResDto = committeSO.updateFinalizateSecrataryTask(committeReqDTO);
				}//#27502 - End
				else {
					validateFCSecretaryDtl(qt.getFinalizationCommitte(), qt.getFinalizationSecratery());

					committeSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
					CommitteReqDTO committeReqDTO = new CommitteReqDTO();
					committeReqDTO.setToken(ctxToken);
					committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
					committeReqDTO.setPayLoad(payLoad);
					committeReqDTO.setOutcome(outcome);
					committeReqDTO.setPmUser(facesScopes.getUserSessionBean());
					committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
					committeReqDTO.setScQt(qt);
					committeResDto = committeSO.updateFinalizateSecrataryTask(committeReqDTO);
				}

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
				qtReqDTO.setDoaList(this.getManagedBean().getPmuserContactpersonList());
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

				if(this.getManagedBean().isReapproval()){//#25037 start
					if (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName)) {
						String committeeNames = "";
						if(committeResDto!=null){
							if(CollectionUtils.isNotEmpty(committeResDto.getQtCommittes())){
								List<ScQtCommittee> approveList = new ArrayList<ScQtCommittee>();
								int count = 0;
								for(ScQtCommittee qtc: committeResDto.getQtCommittes()){
									Long statusId = qtc.getScCommittee().getCommitteStatus()!=null ? 
											qtc.getScCommittee().getCommitteStatus().getStatusId(): 0L;
									if(Arrays.asList(new Long[]{QTStatusConst.CMT_PENDING_APPROVAL, QTStatusConst.CMT_PENDING_REVISION_APPROVAL}).contains(statusId)){
										approveList.add(qtc);
									}
								}
								if(CollectionUtils.isNotEmpty(approveList)){
									for(ScQtCommittee qtc: approveList){
										count++;
										String code = qtc.getScCommittee().getCommitteeType().getParameterCode();
										committeeNames = committeeNames.concat(this.getParameterDesc(ParameterConst.COMMITTE_TYPE, code));
										if(count<approveList.size()){
											committeeNames = committeeNames.concat("/");
										}
									}
								}
							}
						}
						String procurementMode = this.getParameterDesc(scQt.getProcurementMode());
						String qtNo = scQt.getQtNo();
						successMessage(MessageConst.INFO_760504, new String[]{committeeNames, procurementMode, qtNo}, true);					}
				}else{//#25037 end
					successMessage(getMsgCodeByTaskName(taskName), ParameterTypeConst.DOCUMENT_TYPES,
							ParameterConst.DOCTYPE_QUOTATION, qt.getQtNo(), Boolean.TRUE);
				}
				renewSesionObject();//#25037

				debugLogger("end of updateBpmTask");
			}
		} finally {
			detailSO = null;
			committeSO=null;
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
			//reqDTO.setAvaliableApprovals(scQt.getScQtApprovals());
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
                        	 if(!isApprovalScreen()){//13344_start
                        	 qtApproval.setScQtApprovalRemarks(initializeRemarks(
     								qtApproval, scQt.getCurrentWorkFlowStatus(),
     								qtApproval.getScQtApprovalRemarks(),
     								ScQtApprovalRemark.class)); 
                        	 }//13344_End
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
						} else if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT
								.equals(approvalCategoryCode)) {
							Long[] orgIds={scQt.getOwnerOrgProfile().getOrgProfileId(),scQt.getPmOrgProfile().getOrgProfileId()};
							DualListModel<ScQtApproverSettings> budgetApprovalUsers=ApproverSettings.getDetails(
									RoleCode.COMMITTEE_APPROVER,orgIds,
							qtApproval);
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval,
									budgetApprovalUsers));
						} else if (ParameterConst.APPROVAL_CATEGORY_OTHERS
								.equals(approvalCategoryCode)) {
							List<ScQtApprovalRequest> approvalRequest = qtApproval
									.getScQtApprovalDtls();
							if (null != approvalRequest
									&& !approvalRequest.isEmpty()) {
								for (int i = 0; i < approvalRequest.size(); i++) {
									PmParameter parameter = approvalRequest
											.get(i).getApproverAction();
									if (null == parameter) {
										approvalRequest.get(i)
												.setApproverAction(
														new PmParameter());
										ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
										approvalRemark
												.setApprovalRequest(approvalRequest
														.get(i));
										approvalRequest.get(i)
												.setAppRequestRemark(
														approvalRemark);
										//bug #30435 - start
										if (null != getManagedBean().getPmParam()) {
											approvalRequest.get(i)
												.setApproverAction(
														getManagedBean().getPmParam());
										} 
										//bug #30435 - start
										else {
											PmParameter approveParam =  getQTDetailSO().getPmParameterByTypeAndCode(
													ParameterTypeConst.APPROVAL_DECISION_TYPE,
													ParameterConst.APPROVAL_DECISION_TYPE_APPROVED);
											getManagedBean().setPmParam(approveParam);
											approvalRequest.get(i).setApproverAction(approveParam);
										}
										//bug #30435 - end
										
										if (null != getManagedBean().getScQtApprovalReqAttachement()) {
											approvalRequest.get(i)
												.setApprovalRequestAttachement(
														getManagedBean().getScQtApprovalReqAttachement());
										}
										
										if (null != getManagedBean().getScQtAppRequestRemark()) {
											approvalRemark
												.setApprovalRequest(approvalRequest
														.get(i));
											approvalRequest.get(i)
												.getAppRequestRemark().setRemark(getManagedBean().getScQtAppRequestRemark());
										}
										//bug #30435 - end
									} else {
										if (null == approvalRequest.get(i)
												.getAppRequestRemark()) {
											ScQtAppRequestRemark approvalRemark = new ScQtAppRequestRemark();
											approvalRemark
													.setApprovalRequest(approvalRequest
															.get(i));
											approvalRequest.get(i)
													.setAppRequestRemark(
															approvalRemark);
										}
									}
								}
							}
							// #25077 - Start
							List<ScQtApprovalRequest> approvalRequestHistory = qtApproval.getHistoryApprovalRequests();
							if (null != approvalRequestHistory && !approvalRequestHistory.isEmpty()){
								for (int i = 0; i < approvalRequestHistory.size(); i++) {
									PmParameter parameter = approvalRequestHistory.get(i).getApproverAction();
									if (null != parameter) {
										if(null != this.getManagedBean().getScQtApprovalReqAttachementHistory()){
											   approvalRequestHistory.get(i)
											         .setApprovalRequestAttachement(
											        		 this.getManagedBean().getScQtApprovalReqAttachementHistory());
										}
									}
								}
							}
							// #25077 - End
							tabBeans.add(new TabBean(titleName,
									approvalCategoryCode, qtApproval, settings));
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
			// #30435- Start.
			this.getManagedBean().setApproverTabLoaded(true);
			// #30435- End.
		} finally {
			detailSO = null;
		}
	}

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
				//#25037 - start
				getDoaaRequestStatus();
				IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IScWorkFlowStatusSO.class);
				ScWorkFlowStatusReqDTO stReqDTO = new ScWorkFlowStatusReqDTO();
				stReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				stReqDTO.setDocId(this.getManagedBean().getQt().getQtId());
				
				ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(stReqDTO).getWorkFlowStatus();
				wfStatus = wfStatus==null ? new ScWorkFlowStatus(): wfStatus;
				Long statusId = wfStatus.getPmStatus()==null ? 0L: wfStatus.getPmStatus().getStatusId();

				if(this.isStatusGreaterThanOrEquals(statusId, QTStatusConst.QT_PROPOSAL_PREPARATION, QTStatusConst.QT_ORDERED_STATUSES)){
					this.getManagedBean().setPmuserContactpersonList(resDTO.getDeskOfficers().getResultList());
				}else if(this.getManagedBean().isMaintanceFlow() && StringUtils.isEmpty(this.getManagedBean().getTaskName())){//25037 - 497
					ScQtRequestContent requestContent = null;
					if(CollectionUtils.isNotEmpty(scQt.getRequestContents())){
						for(ScQtRequestContent r: scQt.getRequestContents()){
							if(r.getRequestType().getParameterCode().equalsIgnoreCase(ParameterConst.DOCTYPE_DOA_ASSIGNMENT)){
								requestContent = r;
								break;
							}
						}
					}else{
						scQt.setRequestContents(new ArrayList<ScQtRequestContent>());
					}
					this.getManagedBean().setRequestContent(requestContent);
					if (requestContent != null) {
						ScQtApprovalRequest request = requestContent.getApprovalRequest();
						if(request!=null){
							String state = request.getApproverAction() == null ? ""
									: request.getApproverAction().getParameterCode();
							String[] actionArr = new String[] {
									ParameterConst.APPROVAL_DECISION_TYPE_REJECTED,
									ParameterConst.APPROVAL_DECISION_TYPE_APPROVED };
							if (!Arrays.asList(actionArr).contains(state)) {
								requestContent.setBrandNew(false);
								requestContent.setRequireApproval(true);
								requestContent.setSelectable(false);
								this.getManagedBean().setPmuserContactpersonList(generateDOAFromRequestContent(requestContent, scQt));
							}else{
								ScQtRequestContent newRequestContent = new ScQtRequestContent();
								newRequestContent.setBrandNew(true);
								newRequestContent.setScQt(scQt);
								newRequestContent.setRequireApproval(false);
								newRequestContent.setSelectable(true);
								newRequestContent.setRequestType(this.getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, ParameterConst.DOCTYPE_DOA_ASSIGNMENT));
								this.getManagedBean().setRequestContent(newRequestContent);
								scQt.getRequestContents().add(0, newRequestContent);
								this.getManagedBean().setPmuserContactpersonList(
										resDTO.getDeskOfficers().getResultList());
							}
						}else{
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
						newRequestContent.setRequestType(this.getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, ParameterConst.DOCTYPE_DOA_ASSIGNMENT));
						this.getManagedBean().setRequestContent(newRequestContent);
						scQt.getRequestContents().add(0, newRequestContent);

						this.getManagedBean().setPmuserContactpersonList(resDTO.getDeskOfficers().getResultList());
					}
					if(this.getManagedBean().getPmuserContactpersonList()==null){
						this.getManagedBean().setPmuserContactpersonList(new ArrayList<ScQtContactPerson>());
					}
					this.getManagedBean().setPrevDOAs(generateDOACompareStr(this.getManagedBean().getPmuserContactpersonList()));	
				}else{
					this.getManagedBean().setPmuserContactpersonList(resDTO.getDeskOfficers().getResultList());
				}
				//#25037 end
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				if(null!=this.getManagedBean().getRemoveDeductions() && CollectionUtils.isNotEmpty(this.getManagedBean().getRemoveDeductions())){//25635&31402-start
					
				}else{
				this.getManagedBean().setScQtDeductionList(deductionList);
				this.getManagedBean().getQt().setScQtDeductions(deductionList);
				getManagedBean().setSelectedScQtDeduction(null);
				}//25635&31402-End
				/*
				 * if (null != deductionList && !deductionList.isEmpty()) {
				 * this.
				 * getManagedBean().setSelectedScQtDeduction(deductionList.get
				 * (0)); this.getManagedBean().getSelectedScQtDeduction().
				 * setScQtDeductionClauses
				 * (deductionList.get(0).getScQtDeductionClauses()); }
				 */
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
				log.info("Remark Size : "
						+ this.getManagedBean().getPublicationApproval()
								.getScQtApprovalRemarks().size());
			} 
			
			// set up approval pick list
//			dualListModel = approverSettingDtls(
//					RoleCode.PUBLICATION_APPROVER, scQt.getPmOrgProfile()
//							.getOrgProfileId(), publicationApproval);
			Long[] orgIds={scQt.getOwnerOrgProfile().getOrgProfileId(),scQt.getPmOrgProfile().getOrgProfileId()};
			dualListModel = ApproverSettings.getDetails(
					RoleCode.PUBLICATION_APPROVER,orgIds, publicationApproval);
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
			IQTSuppFinalizationSO sfDetailSO;
			try {
				// #30451-Start.
				boolean isBidding = false;
				if ((isQTMaintenanceScreen() || isQTSearchScreen())&& ParameterConst.DOCTYPE_BIDDING.equals(qtnTenderSearchBean.getDocType())) {
					isBidding = true;
					qtnTenderSearchBean.setDocType(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
				}
				// #30451-End.
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				sfDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtnTenderSearchBean(qtnTenderSearchBean.toQTSearchBean());
				// #30451-Start.
				populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, searchBean, isBidding);
				// #30451-End.
				QuationTenderResDTO resDTO = detailSO
						.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				
				//workflow status is_current for SF
				if(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(qtnTenderSearchBean.getDocType())){
					if(resDTO.isSuccessful() && resDTO.getQtResultBean() != null && !CollectionUtils.isEmpty(resDTO.getQtResultBean().getResultList())){
						SearchResultBean<ScQt> qtResultBean = (SearchResultBean<ScQt>)resDTO.getQtResultBean();
						SuppFinalizationResDTO sfResDTO = sfDetailSO.setQtFinalizationCurrentStatus(qtResultBean.getResultList());
						if(sfResDTO.isSuccessful()){
							qtResultBean.setResultList(sfResDTO.getQtList());
							for(int i = 0 ; !CollectionUtils.isEmpty(qtResultBean.getResultList()) && i< qtResultBean.getResultList().size(); i++){
								//log.info(qtResultBean.getResultList().get(i).getFinalization().getCurrentStatus().getPmStatus().getStatusId());
								qtResultBean.getResultList().get(i).setStatusDesc(getStatusDescById(sfResDTO.getQtList().get(i).getFinalization().getCurrentStatus().getPmStatus().getStatusId()));
								//log.info(qtResultBean.getResultList().get(i).getStatusDesc());
							}
							//log.info("update status successful");
						}
					}
				}
				
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
			log.info("LOAPagingDataModel pageChangeListener");
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
			log.info("LOIPagingDataModel pageChangeListener");
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
		
	
		/*private ISearchResultBean<Object[]> getLoiDocument(DocSearchBean docSearchBean) {
			if(docSearchBean.getDocNoFilterBean() != null) {
				docSearchBean.getDocNoFilterBean().setSearchField("loiNo");
			}
			if(docSearchBean.getDocStatusFilterBean() != null) {
				docSearchBean.getDocStatusFilterBean().setSearchField("scLoiWorkFlowStatuses.pmStatus.statusId");
			}
			if(docSearchBean.getDocTitleFilterBean() != null) {
				docSearchBean.getDocTitleFilterBean().setSearchField("scLoiLoa.title");
			}
			if(docSearchBean.getDocPublishFilterBean() != null) {
				docSearchBean.getSearchBean().getDataFilterBeans().remove(docSearchBean.getDocPublishFilterBean());
			}
			if(docSearchBean.getDocModuleFilterBean() != null) {
				docSearchBean.getSearchBean().getDataFilterBeans().remove(docSearchBean.getDocModuleFilterBean());
			}
			return getLoiDAO().searchScLoi(docSearchBean.getSearchBean());
		}*/
	}
	
	
	
	

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
		
		debugLogger("retrieveQuationTenderDetails, @qtId"+qtId);
		if (null != qtId) {
			
			
// #21695 - Farid Start 30/7/2020			
			log.info("ReportBackingBean-retriveUserRoleByUserId");
			IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			List<String> authoriztionList=new ArrayList<String>();
			if (null != new FacesScopes().getUserSessionBean().getUserId()) {
				reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
				// reqDTO.setUserId((long) 240500);
				reqDTO.setLanguageCode(ReportConst.ENG_LANG);
				UserResDTO userResDTO = userSO.retrieveUserRoleDescListByUserId(reqDTO);
				List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
				if (CollectionUtils.isNotEmpty(roleDesc)) {
					for (PmRoleDesc pmRoleDesc : roleDesc) {
						pmRoleDesc.getPmRole().getRoleCode();
						if (!authoriztionList.contains(pmRoleDesc.getPmRole()
								.getRoleCode())) {
							authoriztionList.add(pmRoleDesc.getPmRole()
									.getRoleCode());
						}
						
						RoleCode[] r = {RoleCode.SC_SECRETARY};
						if (userHasRole(r)){
							getManagedBean().setSecretaryValidate(true);
						};
//						if(authoriztionList.contains(pmRoleDesc.getPmRole().getRoleCode().equalsIgnoreCase(RoleCode.SC_SECRETARY.value()))){
//							getManagedBean().setSecretaryValidate(true);
//						}
					}
				}
			}
//#21695 - Farid End 30/7/2020
			
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
					/*
					 * TRACE HI-EXEC TIME
					 * Development #29385
					 */
					long startTime = System.currentTimeMillis();
					tenderResDTO = QtDetailsSO.retriveQTDetails(tenderReqDTO);
					SLAUtils.getInstance().logHiExecInfo("QT","QTDetailBackingBean.retrieveQuationTenderDetails", startTime);
					/*
					 * END
					 */
					if (tenderResDTO.getQtDetail() != null) {
						scQt = tenderResDTO.getQtDetail();
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
						
						//#28784
						checkDemoPresentationByStatus();						
				
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
					
					//#28784
					if(this.getManagedBean().getQt().isDemoRequired()){
						this.getManagedBean().setDemoAction(true);
						this.getManagedBean().setDemoType(true);
					}
				}
			} finally {
				QtDetailsSO = null;
			}
			debugLogger("end of retrieveQuationTenderDetails");
		}
	}
	
	private void checkDemoPresentationByStatus() {	
		ScQt scQt = this.getManagedBean().getQt();
		Long status = this.getManagedBean().getQt().getQtStatus() != null ? this.getManagedBean().getQt().getQtStatus()
				.getStatusId() : null;
		boolean demoRequired = this.getManagedBean().getQt().isDemoRequired();
		if (demoRequired) {
			this.getManagedBean().getQt().setDemoRequired(true);
		} else {
			this.getManagedBean().getQt().setDemoRequired(false);
		}
		if (scQt.getEvaluationType() != null
				&& scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)
				&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {

			if (null != status && (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT))
					&& !this.getManagedBean().isQtSpecMaintanance()) {
				this.getManagedBean().setDemoDisabled(false);
			}

			if (null != status && (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT))
					&& this.getManagedBean().isQtSpecMaintanance() && !demoRequired) {
				this.getManagedBean().setDemoEvalDisabled(true);
			}

			if (null != status
					&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
							&& !this.getManagedBean().isQtSpecMaintanance() && !this.getManagedBean().isMaintanceFlow())
					&& demoRequired) {
				this.getManagedBean().setDemoSpecialCase(true);
				this.getManagedBean().setDemoEvalDisabled(false);
			}

			if (null != status
					&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
							&& !this.getManagedBean().isQtSpecMaintanance() && !this.getManagedBean().isMaintanceFlow())
					&& !demoRequired) {
				this.getManagedBean().setDemoSpecialCase(true);
				this.getManagedBean().setDemoEvalDisabled(true);
			}

			if (this.getManagedBean().isMaintanceFlow() && (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT))
					&& !demoRequired) {
				this.getManagedBean().setDemoDisabled(false);
				this.getManagedBean().setDemoEvalDisabled(true);
			}

			if (null != status
					&& ((status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
							|| status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) || status
								.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)))
					&& this.getManagedBean().isMaintanceFlow() && demoRequired) {
				this.getManagedBean().setDemoEvalDisabled(false);
				this.getManagedBean().setDemoSpecialCase(true);
				this.getManagedBean().setDemoDisabled(true);
			}

			if (null != status
					&& ((status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)
							|| status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) || status
								.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)))
					&& this.getManagedBean().isMaintanceFlow() && !demoRequired) {
				this.getManagedBean().setDemoEvalDisabled(true);
				this.getManagedBean().setDemoSpecialCase(false);
				this.getManagedBean().setDemoDisabled(true);
			}

			if (null != status && (!status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION))
					&& !status.equals(QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)) {
				this.getManagedBean().setDemoType(true);
				this.getManagedBean().setDemoAction(true);
			}
		} else if (this.getManagedBean().isMaintanceFlow()
				&& (status.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT)) && !demoRequired
				&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
			this.getManagedBean().setDemoDisabled(true);
			this.getManagedBean().setDemoEvalDisabled(true);
			this.getManagedBean().setDemoType(true);
			this.getManagedBean().setDemoAction(true);
		} else if (this.getManagedBean().isMaintanceFlow()
				&& status.equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) && !demoRequired
				&& !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
			this.getManagedBean().setDemoDisabled(true);
			this.getManagedBean().setDemoEvalDisabled(true);
		} else if (this.getManagedBean().isMaintanceFlow() && status.equals(QTStatusConst.QT_PENDING_BPK_APPROVAL)
				&& !demoRequired && !status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
			this.getManagedBean().setDemoDisabled(true);
			this.getManagedBean().setDemoEvalDisabled(true);
		}
		
	}

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

				} else if (ParameterConst.COMMITTEE_TYPE_MINISTRY_OF_FINANCE.equalsIgnoreCase(commType)) {
					parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO
							.setParameterType(ParameterTypeConst.SC_COMMITTEE_TYPE);
					parameterReqDTO
							.setParameterCode(ParameterConst.COMMITTEE_TYPE_MINISTRY_OF_FINANCE);
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
				// TODO Code needs to be remove
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
		log.info(":::retrievePmUserSecratryDtl() committeeType: "+ committeeType);
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
	public void committeTabChangeAction(int selectedCommitteeTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("Committe Tab change Action");
		}

		// this.getManagedBean().setToBeDelCommitteMemebers(null);
		this.getManagedBean().setSelectedTabIndex(selectedCommitteeTabIndex);
		this.getManagedBean().setSelectedCommitteeTabIndex(
				selectedCommitteeTabIndex);
		TabBean committeTabBean = this.getManagedBean()
				.getScCommitteeMemberTabList().get(selectedCommitteeTabIndex);

		this.getManagedBean().setSelectedTabBean(committeTabBean);
		String committeeType = this.getManagedBean().getSelectedTabBean().getTabType();
		this.getManagedBean().setRoleFilterList(getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), committeeType));
		
		if(this.getManagedBean().isShowHistory() && Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST).contains(committeeType)){//25037 #372
			navigateToViewHistory(committeTabBean.getScQtCommittee(), true);
		}
		if(committeTabBean!=null){
			if(null!=committeTabBean.getScQtCommittee()){//Bug #29903-start
				List<QtCommitteeMemberBean> historyList = getMemberHistoryList(committeTabBean.getScQtCommittee().getScCommittee());
				this.getManagedBean().setMemberHistoryList(historyList);
				}//Bug #29903-End
		}
	}

	/**
	 * Approval tab change action.
	 */
	public void approvalTabChangeAction(int selectedApprovalTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("ApprovalTabChange Action");
		}

		TabBean approvalTabBean = this.getManagedBean().getTabList()
				.get(selectedApprovalTabIndex);
		// #25077 - #80 - Start.
		if (null != approvalTabBean &&  StringUtils.isNotEmpty(approvalTabBean.getTabType())
				&& ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(this.getManagedBean().getPreviousApprovalTabName()) && !ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(approvalTabBean.getTabType())) {
			IQTDetailSO detailSO;
			try {
				this.getManagedBean().setSaveFlow(false);
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				if(this.getManagedBean().isMaintanceFlow()==false){
					QuationTenderReqDTO reqDTO = prepareQTInfo();
					reqDTO.getQtdetail().setOtherApproval(true);
					// #25077 - #91 - Start
					reqDTO.getQtdetail().setMitiApproval(true);
					// #25077 - #91 - End
					reqDTO.setApprovalOTH(this.getManagedBean().getSelectedApprovalTabBean().getTabType());//bug #30434
					detailSO.validateApprovalDetails(reqDTO);
				}
			} finally {
				detailSO = null;
			}
		}
		if (null != approvalTabBean &&  StringUtils.isNotEmpty(approvalTabBean.getTabType())
				&& (ParameterConst.APPROVAL_CATEGORY_BPK.equals(approvalTabBean.getTabType()) || ParameterConst.APPROVAL_CATEGORY_OTHERS.equals(approvalTabBean.getTabType()))) {
			if(getUserTaskIsPublicationApproval() && isApprovalScreen()){
				List<ScQtApprovalRemark> remarks = this.getManagedBean().getPublicationApproval().getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if(null == userRemark || "".equals(userRemark)) {
							this.getManagedBean().getQt().setRemarksFirstValidation(true);
						}
					}
				}			
				
			}
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
		FacesScopes facesScopes = new FacesScopes();
		reqDTO.setToken(facesScopes
				.getBPMAuthenticationCtxToken());
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
		
		//9019 - Start (Harun) set Group Item to NULL when save while left blank and panel is selected
		if (isPanel && null == this.getManagedBean().getQt().getItemGroup().getPanelItemGroupId()) {
			this.getManagedBean().getQt().setItemGroup(null);
		}
		//9019 - End (Harun) 
		
		int changedIndexNumber = manipulateIndexNumber(activeIndex);
		switch (changedIndexNumber) {
		case 0:
			detailSO.validateQTDetails(reqDTO);
			
			//9019 - start using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO); 
			//9019 - end
			renewQTSesionObject();
			this.getManagedBean().setTaskName(taskName);
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			setAmmendMentBean(resDTO.getAmendMentBean());
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 1:
			detailSO.validateSupplierQualificationDtls(reqDTO);
			//9019 - start using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO); 
			//9019 - end
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			retrieveSuppQualificationTabDtl();
			setAmmendMentBean(resDTO.getAmendMentBean());
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
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
			committeSO.validateAppointCommitteDtls(committeReqDTO);
			
			//9019 - start using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO); 
			//9019 - end

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
				initCommitteeTabSingularReapproval(payLoad);
			}
			setAmmendMentBean(resDTO.getAmendMentBean());
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 3:
			reqDTO.getQtdetail().setModified(false);
			reqDTO.getQtdetail().setFCApproval(true);
			detailSO.validateApprovalDetails(reqDTO);
			
			//9019 - start using new method
			resDTO = detailSO.saveQTDetailsForDraft(reqDTO); 
			//9019 - end
			
			int selectedApprovalTabIndex = this.getManagedBean().getSelectedApprovalTabIndex();
			renewQTSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			this.getManagedBean().setSelectedApprovalTabIndex(selectedApprovalTabIndex);
			setAmmendMentBean(resDTO.getAmendMentBean());
			retrieveApprovalTabDtl();
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 4:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateBsvDetails(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveBriefingSitevisitTabDtl();
			} else if(super.isQTStatusLessThanOrEquals(currentStatus, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)){
				// save scQt info onclick of Activites tab
				detailSO.validateQTDetails(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setTaskName(taskName);
				retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
				retrieveActivitiesTabDtl();
				setAmmendMentBean(resDTO.getAmendMentBean());
			}
			this.getManagedBean().setCurrentStatusId(currentStatus);
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 5:
			if (isinvokeValidations(getManagedBean().getQt())) {
				detailSO.validateScheduleDetails(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveScheduleTabDtl();
			}else if(super.isQTStatusLessThanOrEquals(currentStatus, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)){//#13344-start
				// save scQt info onclick of Activites tab
				//detailSO.validateQTDetails(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewSesionObject();
				this.getManagedBean().setTaskName(taskName);
				retrieveQuationTenderDetails(resDTO.getQtDetail().getQtId());
				retrieveApprovalTabDtl();
				setAmmendMentBean(resDTO.getAmendMentBean());
			}
			this.getManagedBean().setCurrentStatusId(currentStatus);//#13344-End
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 6:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				detailSO.validateDeductionDtls(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDeductionPaymentTabDtl();
			}
			getfulfilmentDtls(resDTO.getQtDetail());//#29474
			break;
		case 7://#29474 - start
			detailSO.validateSCFulfillmentSchedules(reqDTO);
			resDTO = detailSO.saveFulfilmentQTDtl(reqDTO);//#29474
			renewSesionObject();
			this.getManagedBean().setQt(resDTO.getQtDetail());
			this.getManagedBean().setCurrentStatusId(currentStatus);
			getfulfilmentDtls(resDTO.getQtDetail());
//			retriveSpecdetails(resDTO.getQtDetail().getQtId());
			retriveFulfilmentDtls();
			break;
		case 8:
			if (isinvokeValidations(getManagedBean().getQt())||isMaintancePendingChklistStatus()) {
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrieveDocumentTabDtl();
				getfulfilmentDtls(resDTO.getQtDetail());
				break;
			}
		case 9:
			getfulfilmentDtls(this.getManagedBean().getQt());
			break;
		case 10:
			if (isQTStatusGreaterThan(getManagedBean().getQt().getQtStatus().getStatusId(), QTStatusConst.QT_PENDING_QT_SUBMISSION)) {
				reqDTO.getQtdetail().setModified(true);
				reqDTO.getQtdetail().setFCApproval(true);
				detailSO.validateApprovalDetails(reqDTO);
				detailSO.removeSchedule(reqDTO);//#29474
				resDTO = detailSO.saveQTDtl(reqDTO);
				renewQTSesionObject();
				this.getManagedBean().setQt(resDTO.getQtDetail());
				this.getManagedBean().setCurrentStatusId(currentStatus);
				setAmmendMentBean(resDTO.getAmendMentBean());
				retrievePublicationApprovalTabDtl();
			}
			getfulfilmentDtls(resDTO.getQtDetail());//#29474 - end
			break;
		}
		
		if (activeIndex != 0) {
			ScQt scQt = getManagedBean().getQt();
			
			if (scQt.isZonal() && 
					scQt.getScQtSupplierCriteria() != null &&
					scQt.getScQtSupplierCriteria().getQtZoneCriterias() != null) {
				// Populate zone detail into tree node
				populateZoneDetailIntoTreeNode(scQt, 
						scQt.getScQtSupplierCriteria().getQtZoneCriterias().getTplSpecZoneDtls());	
			}
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
		//9019 - End (Harun)
	
		//9019 - Start (Harun) initializing remark for cancel or subsequent save start
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
		
		//9019 - Start (Harun) initializing Group Item when panel is selected and left blank while SAVE
		if (isPanel) {
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
		
		//29464-start
		String taskName = this.getManagedBean().getTaskName();
		taskName = taskName==null ? "":taskName;
		//29464-end

		if (priviousIndex != activeIndex) {
			if (!isReadOnly) {
				switch (priviousIndex) {
				case 0:
					detailSO.validateQTDetails(reqDTO);
					// retrieveQuationTenderDetails(this.getManagedBean().getQt().getQtId());
					break;
				case 1:
					detailSO.validateSupplierQualificationDtls(reqDTO);
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
							if(this.getManagedBean().isMaintanceFlow()==false){reqDTO.getQtdetail().setOtherApproval(true);}
							// #25077 - #80 - End.
							// #25077 - #91 - Start
							if(this.getManagedBean().isMaintanceFlow()==false){reqDTO.getQtdetail().setMitiApproval(true);}
							// #25077 - #91 - End
						}
						
					detailSO.validateApprovalDetails(reqDTO);
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
			case 7://#29474 - start
				ScQt fulfilMentDtls = getManagedBean().getQt();

				if (ParameterConst.FULFILMENT_TYPE_SCHEDULED.equalsIgnoreCase(this
						.getManagedBean().getFulfilmentType())
						|| ParameterConst.FULFILMENT_TYPE_AS_AND_WHEN
								.equalsIgnoreCase(this.getManagedBean()
										.getFulfilmentType())
										|| ParameterConst.FULFILMENT_TYPE_NON_PERIODIC_SCHEDULED
										.equalsIgnoreCase(this.getManagedBean()
												.getFulfilmentType())) {
					reqDTO.setQtdetail(fulfilMentDtls);
					detailSO.validateSCFulfillmentSchedules(reqDTO);
				}
				break;

			case 8:
				break;
			case 9:
				break;
			case 10://#29474 - end
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
				//29464-start
				if(taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)){
					this.getManagedBean().setCommitteeAppointmentMenuTab(false);
				}
				//29464-end
				// detailSO.validateSupplierQualificationDtls(reqDTO);
				retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
				break;
			case 3:
				// detailSO.validateApprovalDetails(reqDTO);
				if (!getManagedBean().isApproverTabLoaded()){// #11104 - Start 
				   retrieveApprovalTabDtl();
				}// #11104 - End
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
				//29464-start
				if(taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)){
					this.getManagedBean().setCommitteeAppointmentMenuTab(true);
					retrieveCommitteeAppointmentTabDtl(FORM_NO_ACTION, REFORM_NO_ACTION);
				}else{
					if(isMaintancePendingChklistStatus){
						retriveFulfilmentDtls();//#29474
					} else{
						retrieveScheduleTabDtl();					
					}
				}//29464-end
				
				break;
			case 6:
				if(isMaintancePendingChklistStatus){
					retrieveDocumentTabDtl();//#29474
				} else{
					retrieveDeductionPaymentTabDtl();
				}
				
				break;
			case 7://#29474 - start
				// fulfilment schedule
				if(isMaintancePendingChklistStatus){
					retrieveActivitiesTabDtl();
				} else{
					retriveFulfilmentDtls();
				}
				break;
			case 8:
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrieveDocumentTabDtl();
				}
				break;
			case 9:
				retrieveActivitiesTabDtl();
				break;
			case 10://#29474 - end
				if (!QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(getManagedBean().getQt().getQtStatus().getStatusId())) {
					retrievePublicationApprovalTabDtl();
					// #25077 - Start
					if(getUserTaskIsPublicationApproval() && isApprovalScreen()){
					    this.getManagedBean().setScQtApproval(this.getManagedBean().getPublicationApproval());
					    this.getManagedBean().getQt().setRemarksFirstValidation(false);
					}
					// #25077 - End
				}
				//#25692-start
				 if(getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()){
					 ApprovalBackingBean approvalbean=new ApprovalBackingBean();
					 approvalbean.retriveCommiteeApprovalTabDetails();
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
			if(getUserTaskIsPublicationApproval() && isApprovalScreen()){
					this.getManagedBean().setPublicationApproval(this.getManagedBean().getScQtApproval());
					this.getManagedBean().setFirstPagePA(false);
			}
			// #25077 - End
			this.getManagedBean().setActiveIndex(activeIndex);
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

	/**
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public void submitQtDetailsFormMaintanace() {//#25037
		submitQtDetailsFormMaintanace(false);
	}

	public void submitQtDetailsFormMaintanace(boolean confirmResetReendorse) {//#25037
		log.debug("submitQtDetailsFormMaintanace");
		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			updateApprovalDetails(this.getManagedBean().getQt());
			retrieveApprovalTabDtl();

			//#25037-start
			boolean showPopup = validateIsCommitteeFormed(confirmResetReendorse);
			if(showPopup){
				return;
			}
			//#25037-end
			if (this.getManagedBean().getQt().isSampleReq()) {//11104-start
				this.getManagedBean().setSubmit(true);
				retrieveScheduleTabDtl();
				
			}//11104-End

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setSaveFlow(false);
			ScQt qtDetails = quationTenderReqDTO.getQtdetail();
			QtAmendMentBean amendMentBean = getManagedBean().getAmendMentBean();

			quationTenderReqDTO.setQtdetail(qtDetails);
			quationTenderReqDTO.setAmendMentBean(amendMentBean);
			quationTenderReqDTO.setRoleCode(getUserRoleCode());
			//start #25037
			FacesScopes fs = new FacesScopes();
			quationTenderReqDTO.setPmUser(fs.getUserSessionBean());
			quationTenderReqDTO.setDoaList(this.getManagedBean().getPmuserContactpersonList());
			quationTenderReqDTO.setToken(fs.getBPMAuthenticationCtxToken());
			//end #25037

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
  				//25037 - start - note 510
				boolean approvalExistOnSubmission = false;
				if(CollectionUtils.isNotEmpty(quationTenderResDTO.getCmtModififationRequests())){ 
					List<ScQtCommittee> qtCommittees = quationTenderResDTO.getQtDetail().getScQtCommittees(); 
					for(ScQtCommittee qtCommittee: qtCommittees){
						PmStatus pmStatus = qtCommittee.getScCommittee().getCommitteStatus();
						if(pmStatus.getStatusId().equals(QTStatusConst.CMT_PENDING_APPROVAL)){
							approvalExistOnSubmission = true;
							break;
						}
					}
				}
				if(approvalExistOnSubmission){
					successMessage(MessageConst.INFO_760524, new String[]{this.getParameterDesc(scQt.getProcurementMode()), scQt.getQtNo()}, true);
				}else{
					String msgCode;
					if (quationTenderResDTO.isSuccessful()) {
						msgCode = MessageConst.INFO_700026;
					} else {
						msgCode = MessageConst.INFO_700004;
					}
					successMessage(msgCode, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
							scQt.getQtNo(), Boolean.TRUE);
				}
  				//25037 - end - note 510
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
             //9019 null remark in UI it showing two editable textarea to prevent that only value be pass to below method 
                List<ScQtRemark> remarks = null;
				if (CollectionUtils.isNotEmpty(scQt.getScQtRemarks())
						&& StringUtils.isNotEmpty(scQt.getScQtRemarks().get(0).getRemark())) {
					remarks = scQt.getScQtRemarks();
				}

				scQt.setScQtRemarks(initializeRemarks(scQt, this
						.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						scQt.getScQtRemarks(), ScQtRemark.class));
			}
			return "/xhtml/sc/quatationtender/cancelqtdetails.xhtml";

		} finally {
			detailSO=null;
		}

	}
	
	/**
	 * Enhancement #21694
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public String reverseQtDetails(Boolean isReverseLOI, Boolean isReverseLOA,QTDetailBackingBean qtDtl ) {
		debugLogger("reverseQtDetails");
		IQTDetailSO detailSO;
		IUserSO userSO;
		IParameterSO parameterSo = null;
		QuationTenderReqDTO quaReqDTO = new QuationTenderReqDTO();
		PmUser currentUser = new FacesScopes().getUserSessionBean();
		this.getManagedBean().setPmUser(currentUser);
		ScLoi scLoi = new ScLoi();
		ScLoa scLoa = new ScLoa();
		PmUserOrg pmUserOrgs = new PmUserOrg();
		try {
			/*
			 * TASK
			 * 1- SETUP TABLE SC_QT_REVERSE
			 */
			/*
			 * SETUP FROM TASKLIST
			 */
			
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			userSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IUserSO.class);
			ParameterResDTO paramResDTO = new ParameterResDTO();
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			QuationTenderResDTO resDTO = new QuationTenderResDTO();
			ScQt scQt = this.getManagedBean().getQt();
			ScQtReverse scQtReverse = new ScQtReverse();
			quationTenderReqDTO.setQtId(scQt.getQtId());
			quationTenderReqDTO.setReversedLOI(isReverseLOI!=null && isReverseLOI?isReverseLOI:Boolean.FALSE);
			quationTenderReqDTO.setReversedLOA(isReverseLOA!=null && isReverseLOA?isReverseLOA:Boolean.FALSE);
			quationTenderReqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
			PmUser finalizationSecretary= detailSO.retrieveFinalizationSecretaryByQtId(quationTenderReqDTO).getFinalizationSecretary();
			quationTenderReqDTO.setUserId(finalizationSecretary.getUserId());
			pmUserOrgs = detailSO.retrievePmUserOrgByUserid(quationTenderReqDTO).getPmUserOrg();
			if(isReverseLOI){
				quationTenderReqDTO.setScLoiForQt(this.getManagedBean().getScLoiForQt());
			}
				
			if(isReverseLOA){
				quationTenderReqDTO.setScLoaForQt(this.getManagedBean().getScLoaForQt());
			}
				
			resDTO = detailSO.retrieveReversalByQtId(quationTenderReqDTO);
			this.getManagedBean().setQt(resDTO.getQtDetail());
			scQt = resDTO.getQtDetail();
			if(isReverseLOI){
				/*
				 * SETTING FINALIZATION SECRETARY LOI
				 */
				
				quaReqDTO.setScLoiForQt(this.getManagedBean().getScLoiForQt());
				quaReqDTO.setReversedLOI(isReverseLOI);
				quaReqDTO.setScQtReverse(resDTO.getScQtReverse());
				scLoi = findScLoiForQtByReqDTO(quaReqDTO);
				
				scQtReverse =  resDTO.getScQtReverse();
				List<ScLoiReverseRemark> loiRemarks = new ArrayList<>();
				if(scQtReverse != null){
					loiRemarks = findScQtReverseLoiRemarksByReqDTO(quaReqDTO);
				}				
				if(!this.getManagedBean().isViewFinalization()){
					loiRemarks = initializeRemarks(scQtReverse, scLoi.getScWorkFlowStatus(), loiRemarks.size()>0?loiRemarks: null, ScLoiReverseRemark.class);
				}
				if(scQtReverse==null){	
					scQtReverse = new ScQtReverse();
				}
				for(ScRemark loiRemark:loiRemarks){
					loiRemark.setEditable(false);
				}
				
				this.getManagedBean().setScLoiReverseRemarks(loiRemarks);
				this.getManagedBean().setScLoi(scLoi);
				this.getManagedBean().setScQtReverse(scQtReverse);	
				this.getManagedBean().setScLoaForQt(null);
				/*
				 * SETTING FINALIZATION SECRETARY LOI (END)
				 */
			}
			if(isReverseLOA){
				/*
				 * SETTING FINALIZATION SECRETARY LOA
				 */
				quaReqDTO.setScLoaForQt(this.getManagedBean().getScLoaForQt());
				quaReqDTO.setReversedLOA(isReverseLOA);
				quaReqDTO.setScQtReverse(resDTO.getScQtReverse());
				scLoa = findScLoaForQtByReqDTO(quaReqDTO);
				scQtReverse =  resDTO.getScQtReverse();
				List<ScLoaReverseRemark> loaRemarks = new ArrayList<>();	
				if(scQtReverse != null){
					loaRemarks = findScQtReverseLoaRemarksByReqDTO(quaReqDTO);
				}
				if(!this.getManagedBean().isViewFinalization()){
					loaRemarks = initializeRemarks(scQtReverse, scLoa.getCurrentStatus(), loaRemarks!=null?loaRemarks:null, ScLoaReverseRemark.class);
				}
				if(scQtReverse==null){
					scQtReverse = new ScQtReverse();
				}
				
				for(ScRemark loaRemark:loaRemarks){
					loaRemark.setEditable(false);
				}
				
				this.getManagedBean().setScLoa(scLoa);
				this.getManagedBean().setScLoaReverseRemarks(loaRemarks);
				this.getManagedBean().setScQtReverse(scQtReverse);
				this.getManagedBean().setScLoiForQt(null);
				
				/*
				 * SETTING FINALIZATION SECRETARY LOA (END)
				 */
			}
				TabBean tabBean = new TabBean(null, null, null,retrieveFinalizationCommitteeType());
				this.getManagedBean().setSelectedApprovalTabBean(tabBean);
				
				if(resDTO.getScQtReverse()!=null){
					/*
					 * CHECKING IF PROFILES DESK OFFICER IS ACTIVE, IF ACTIVE, DISABLE DROPDOWN, ELSE OPEN
					 * STEP 1: CHECK USER ORG PROFILE
					 * STEP 2: CROSS CHECK WITH PM USER ROLE WITH PARAMCODE
					 */
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(scQt.getFinalizationSecratery().getUserId());					
					userReqDTO.setRoleCode(scQt.getFinalizationCommitte().getParameterCode().concat("_SECRETARY"));					
					userReqDTO.setUserOrgId(pmUserOrgs.getUserOrgId());
					
					PmUserRole pmUserRole = (userSO.retrieveSpecificUserRoleByReqDTO(userReqDTO)).getUserRole();
					if(scQt!=null){
						if(scQt.getFinalizationCommitte()!=null){
							scQtReverse.setFinalizationCommitte(scQt.getFinalizationCommitte());
						}if(scQt.getFinalizationSecratery()!=null){
							scQtReverse.setFinalizationSecratery(scQt.getFinalizationSecratery());
						}
					}
					
					if(pmUserRole!=null){
						if(pmUserRole.getRecordStatusFlg().equals(1)){
							
							this.getManagedBean().setApprovalEditableSection(Boolean.TRUE);
						}else{
							this.getManagedBean().setApprovalEditableSection(Boolean.FALSE);
						}						
					}else{
						this.getManagedBean().setApprovalEditableSection(Boolean.FALSE);
					}
					
					this.getManagedBean().setScQtReverse(resDTO.getScQtReverse());
				}else{
					/* SETTING NEW 
					 * DOC_TYPE = DT, PARAM_CODE = LI || LA
					 * */					
					parameterSo = BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					
					ParameterReqDTO reqDTO = new ParameterReqDTO();
					reqDTO.setParameterType(ParameterTypeConst.APPROVAL_REQUEST_TYPES);
					scQtReverse.setScQt(scQt);
					
					
					scQtReverse.setMeeting(Boolean.FALSE);
					scQtReverse.setRecordStatusFlg(0);
					scQtReverse.setChangedBy(1L);
					scQtReverse.setCreatedBy(1L);
					if(isReverseLOI){
						scQtReverse.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
						reqDTO.setParameterCode(ParameterConst.APPROVAL_REQUEST_REVERSE_LOI_QT);
						scQtReverse.setDocId(this.getManagedBean().getScLoiForQt().getLoiId());
					}else if(isReverseLOA){
						scQtReverse.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
						reqDTO.setParameterCode(ParameterConst.APPROVAL_REQUEST_REVERSE_LOA_QT);
						scQtReverse.setDocId(this.getManagedBean().getScLoaForQt().getLoaId());
					}
					paramResDTO = parameterSo.retrieveParameterByTypeAndCode(reqDTO);						
					scQtReverse.setTypeOfRequest(paramResDTO.getPmParameter());
					
					/*
					 * CHECKING IF PROFILES DESK OFFICER IS ACTIVE, IF ACTIVE, DISABLE DROPDOWN, ELSE OPEN
					 * STEP 1: CHECK USER ORG PROFILE
					 * STEP 2: CROSS CHECK WITH PM USER ROLE WITH PARAMCODE
					 */
					UserReqDTO userReqDTO = new UserReqDTO();
					userReqDTO.setUserId(scQt.getFinalizationSecratery().getUserId());					
					userReqDTO.setRoleCode(scQt.getFinalizationCommitte().getParameterCode().concat("_SECRETARY"));					
					userReqDTO.setUserOrgId(pmUserOrgs.getUserOrgId());
					
					PmUserRole pmUserRole = (userSO.retrieveSpecificUserRoleByReqDTO(userReqDTO)).getUserRole();
					if(scQt!=null){
						if(scQt.getFinalizationCommitte()!=null){
							scQtReverse.setFinalizationCommitte(scQt.getFinalizationCommitte());
						}if(scQt.getFinalizationSecratery()!=null){
							scQtReverse.setFinalizationSecratery(scQt.getFinalizationSecratery());
						}
					}
					
					if(pmUserRole!=null){
						if(pmUserRole.getRecordStatusFlg().equals(1)){
							
							this.getManagedBean().setApprovalEditableSection(Boolean.TRUE);
						}else{
							this.getManagedBean().setApprovalEditableSection(Boolean.FALSE);
						}						
					}else{
						this.getManagedBean().setApprovalEditableSection(Boolean.FALSE);
					}
									
					this.getManagedBean().setScQtReverse(scQtReverse);
					
				}	
			return QTConstants.LOI_LOA_REVERSE_PAGE;

		} finally {
			detailSO=null;
		}

	}
	
	/**
	 * Enhancement #21694
	 * Change Reverse Scenario pb secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changeReversePbSecretaryCommType(ScQtReverse scQtReverse) {
		debugLogger("changeReversePbSecretaryCommType");
		
		Long commTypeId = scQtReverse.getFinalizationCommitte().getParameterId();
		if (null != commTypeId) {
			PmParameter commTypeCode = ParameterHelper.getInstance().getPmParameterById(commTypeId);
			scQtReverse.setFinalizationCommitte(commTypeCode);
			
			getManagedBean().setScQtReverse(scQtReverse);
		}
	}
	
	/**
	 * Enhancement #21694
	 * Update user id to settings.
	 * 
	 * @param event
	 *            the event
	 */
	public void updateReverseUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getScQtReverse().setFinalizationSecratery(user);
				break;
			}
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
			
			getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
					.getApproverSettings().get(0).setPmUser(new PmUser());
		}
	}
	
	/**
	 * Enhancement #21689
	 * Submit QT Reverse
	 */
	public void submitQtReverse(){
		IQTDetailSO detailSO;
		IScActorSO actorSO;
		ScActorReqDTO actorReqDTO=new ScActorReqDTO();
		QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
		String[] param = new String[2];
		try{
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			actorSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);
			PmUser user = new FacesScopes().getUserSessionBean();
			quationTenderReqDTO.setPmUser(user);
			quationTenderReqDTO.setScLoiForQt(null!=this.getManagedBean().getScLoiForQt()?this.getManagedBean().getScLoiForQt():null);
			quationTenderReqDTO.setScLoaForQt(null!=this.getManagedBean().getScLoaForQt()?this.getManagedBean().getScLoaForQt():null);
			quationTenderReqDTO.setFinalizationMethod(this.getManagedBean().getQt().getFinalization().getFinalizationMethod());
			quationTenderReqDTO.setScLoi(null!=this.getManagedBean().getScLoi()?this.getManagedBean().getScLoi():null);
			quationTenderReqDTO.setScLoa(null!=this.getManagedBean().getScLoa()?this.getManagedBean().getScLoa():null);
			quationTenderReqDTO.setLoiLoaBy(this.getManagedBean().getQt().getFinalization().getLoiLoaBy());
			quationTenderReqDTO.setQtdetail(this.getManagedBean().getQt());
			quationTenderReqDTO.setScQtReverse(this.getManagedBean().getScQtReverse());
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			
			/**
			 * FINDING QT WORKFLOW-> CASE REVERSAL QT
			 */
			IScWorkFlowStatusSO workFlowStatusSO;
			ScWorkFlowStatusReqDTO scWreqDTO = new ScWorkFlowStatusReqDTO();
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);	
			scWreqDTO.setDocId(this.getManagedBean().getQt().getQtId());		
			scWreqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			ScWorkFlowStatus qtcurrentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(scWreqDTO).getWorkFlowStatus();
			if(null!=this.getManagedBean().getScLoiForQt()){
				if (!qtcurrentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_LOI)) {
					throw new EpException(MessageConst.ERROR_760496,new String[]{this.getManagedBean().getQt().getQtNo()}); 
				}
			}if(null!=this.getManagedBean().getScLoaForQt()){
				if (!qtcurrentWorkFlowStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_LOA)) {
					throw new EpException(MessageConst.ERROR_760497,new String[]{this.getManagedBean().getQt().getQtNo()});
				}
			}
			
			
			boolean isQuotation=true;
			PmParameter procurementMode = this.getManagedBean().getQt().getProcurementMode();
			if (procurementMode != null
					&& procurementMode.getParameterCode().equals(
							ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
				isQuotation = true;
			} else if (procurementMode != null
					&& procurementMode.getParameterCode().equals(
							ParameterConst.PROCUREMENT_MODE_TENDER)) {
				isQuotation = false;
			}
			
			actorReqDTO.setDocId(this.getManagedBean().getQt().getQtId());
			actorReqDTO.setDocTypes(Arrays.asList(ParameterConst.DOCTYPE_QUOTATION));
			switch(this.getManagedBean().getQt().getFinalization().getLoiLoaBy()){
				case ParameterConst.LOILOA_PREPARED_BY_DO	:	//DESK OFFICER
																	actorReqDTO.setUserId(this.getManagedBean().getQt().getCreatedBy());
																	break;
				case ParameterConst.LOILOA_PREPARED_BY_FC_SECRETARY	:	//SECRETARY
																	if(null!=this.getManagedBean().getScLoiForQt()){
																		actorReqDTO.setUserId(this.getManagedBean().getScLoiForQt().getCreatedBy());
																	}else{
																		actorReqDTO.setUserId(this.getManagedBean().getScLoaForQt().getCreatedBy());
																	}
																		break;
			}
			
			quationTenderReqDTO.setScActors(actorSO.retreiveActorByDocTypesDocIdUserId(actorReqDTO).getScActorList());
			
			/**************************************
			 * VALIDATE REVERSE REMARK IS MANDATORY
			 **************************************/
			validateReverseRemark();
			
			/********************
			 * SAVING REVERSE QT
			 * SAVING LOI_LOA_REVERSE_REMARK
			 ********************/
			saveReverseLoiLoaDtl();
			
			/****************************
			 * UPDATE LOI_LOA(IS_LOI_REQ)
			 * UPDATE SC_QT_RECOMMEND_DTL
			 ****************************/
			updateScQtRecommendDtl();
			
			/****************************
			 * UPDATE SC_WORKFLOW_STATUS, 
			 * UPDATE DIRECT WITHOUT USING BPM SINCE THE INSTANCE ARE NOT YET CREATED IN BPM (CASE NOT YET SUBMIT, JUST SAVE LOI/LOA AND REVERSE AND UPDATE 
			 * WORKFLOW_STATUS MANUALLY)
			 */
			updateReverseScWorkflowStatus(quationTenderReqDTO);
			
			/********************
			 * INITIATE BPM (REVERSED LOI/REVERSE LOA)
			 * INITIATE BPM (REVERSE QT)
			 * INITIATE TRACKING DIARY
			 * INITIATE NOTIFICATION
			 ********************/
			QuationTenderResDTO quationTenderResDTO = new QuationTenderResDTO();
			quationTenderResDTO = detailSO.reverseQT(quationTenderReqDTO);			
			
			/*************************
			 * ACKNOWLEDGEMENT MESSAGE
			 *************************/
			
			
			if(this.getManagedBean().getScLoiForQt()!=null){
				param[0] = this.getManagedBean().getScLoiForQt().getDocNo();
				param[1] = this.getManagedBean().getScQtReverse().getScQt().getQtNo();
				if(isQuotation){
					successMessage(MessageConst.INFO_700073,param);
				}else{
					successMessage(MessageConst.INFO_700074,param);
				}				
			}else if(this.getManagedBean().getScLoaForQt()!=null){
				param[0] = this.getManagedBean().getScLoaForQt().getDocNo();
				param[1] = this.getManagedBean().getScQtReverse().getScQt().getQtNo();
				if(isQuotation){
					successMessage(MessageConst.INFO_700075,param);
				}else{
					successMessage(MessageConst.INFO_700076,param);
				}
			}
			
			if (quationTenderResDTO.isSuccessful()) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
				
			}
		}finally {
			detailSO = null;
		}
	}

	/**
	 * Submit qt cancel details.
	 * 
	 * @return the string
	 */
	public void submitQtCancelDetails() {

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
				
				ackMsg = MessageConst.INFO_700052;
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
				
				ackMsg = MessageConst.INFO_700031;
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
		if(null!=this.getManagedBean().getQt()){
			log.debug("====enableFieldByStatusCheckManga==========");
			 approvals = this.getManagedBean().getQt()
					.getScQtApprovals();
			}
		

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
		//#11299 Start Vinod
		String taskName = this.getManagedBean().getTaskName()==null ? "": this.getManagedBean().getTaskName();

		if (taskName.equals(SCProcessNameConst.REVISE_COMMITTEE)
			&& !this.getManagedBean().isMaintanceFlow()
			&& isDeskOfficer()
			&& !this.getManagedBean().isReadOnly()) {
			return false;
		}
		//#11299 End Vinod
		// #25426#36#95 ruzaimah - Start
		if ((this.getManagedBean().isMaintanceFlow()
				&& isDeskOfficer()) // #25426#85 ruzaimah - remove this.
				&& !this.getManagedBean().isReadOnly()) {
			return false;
		}
		// end	
		
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
		if (!StringUtils.isBlank(procurementMode) && getManagedSearchBeanDocumentTypeIsLOI()) {//30418
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
	
	/**
	 * BUG 32050, BUG 32209
	 * Checks if QT/DL is UPDATABLE or NOT.
	 * 
	 * @return true, if its not updatable
	 */
	public boolean isUpdatable(Long statusId, String docNo) {
		Boolean returnVal = Boolean.FALSE;		
		switch(statusId.equals(QTStatusConst.QT_ARCHIVED)?StringUtils.substring(docNo, 0, 1):StringUtils.substring(docNo, 0, 2)){
			case ParameterConst.DOCUMENT_CATEGORY_DIRECT_LOA_FOR_QUOTATION_TENDER : returnVal = Boolean.TRUE;
													break;
			case ParameterConst.DOCTYPE_QUOTATION : returnVal = Arrays.asList(QTStatusConst.UNUPDATABLE_BUTTON_STATUSES).contains(statusId);
													break;
			case ParameterConst.DOCTYPE_ARCHIVED :  returnVal = Arrays.asList(QTStatusConst.UNUPDATABLE_BUTTON_STATUSES).contains(statusId);
													break;
		}		
		return returnVal;
	}
	
	// Bug #32425 - Start
		private boolean isQcaSecretary(){
			FacesScopes facesScopes = new FacesScopes();
			return  facesScopes.hasRoleUser(RoleCode.QCA_SECRETARY.value());  
		}
		
	    private boolean isQcbSecretary(){
	    	FacesScopes facesScopes = new FacesScopes();
			return  facesScopes.hasRoleUser(RoleCode.QCB_SECRETARY.value()); 
		}
	    // Bug #32425 - End
	
	/**
	 * BUG 31120
	 * Added MOF Desk Officer
	 * @return
	 */
	private boolean isDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value())
			   || facesScopes.hasRoleUser(RoleCode.MOF_DESK_OFFICER.value());
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
	
	/**
	 * BUG #31120
	 * TO CATER Finalization Committee Desk Officer
	 * USED IN QT MAINTENANCE & AUDIT QT SEARCH
	 * @return Boolean
	 */
	private boolean isFCDeskOfficer(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.QCA_SECRETARY.value())
			   || facesScopes.hasRoleUser(RoleCode.QCB_SECRETARY.value())
			   || facesScopes.hasRoleUser(RoleCode.PBA_SECRETARY.value())
			   || facesScopes.hasRoleUser(RoleCode.PBB_SECRETARY.value())
			   || facesScopes.hasRoleUser(RoleCode.FPB_SECRETARY.value());
	
	}	
	
	/**
	 * BUG #31120
	 * RULE SET FOR MAGNIFIER GLASS USED IN QT MAINTENANCE & AUDIT 
	 * @return Boolean
	 */
	private boolean isGroupPTJRuleSet(){
		return  isDeskOfficer()	|| isFCDeskOfficer() || isAuditor();
	}
	
	
	/**
	 * BUG #31120
	 * RULE SET FOR MAGNIFIER GLASS USED IN QT MAINTENANCE & AUDIT
	 * @return Boolean 
	 */
	private boolean isAuditPTJRuleSet(){
		return  isDeskOfficer()	|| isAuditor();
	}
	
	/**
	 * BUG #31120
	 * RULE SET FOR MAGNIFIER GLASS USED IN QT MAINTENANCE & AUDIT 
	 * @return Boolean
	 */
	private boolean isMaintenancePTJRuleSet(){
		return isDeskOfficer() || isFCDeskOfficer();
	}
	
	
	/* Bug #27412 START - CHANGE PRIVATE TO PUBLIC TO ALLOWED METHOD CALLED FROM FRONT*/
	public boolean isMofSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| facesScopes.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value());
	}

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
		/*
		 * if (zoneLocation.isLeaf()) {
		 * 
		 * } else { for (TreeNode childrendLocationNode :
		 * zoneLocation.getChildren()) {
		 * results.addAll(getLocationsList(childrendLocationNode)); } }
		 */
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
		log.info("closeProposal-->loginId: " + new FacesScopes().getUserSessionBean().getLoginId());
		log.info("##### Proposal Close started @"+DateUtils.getCurrentDate());
		
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
		
		log.info("##### Proposal Close completed @"+DateUtils.getCurrentDate());
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
		
		//filter document type search by role.
		//26259
		boolean pbbSecretaryOnly = false; 
		boolean qcSecretaryOnly = false; //#32425
		if (isPbbSecretary() && !(isDeskOfficer() || isAuditor() || isMofSupplier())){
			pbbSecretaryOnly = true;
		}else if(isFpbSecretary() && !(isDeskOfficer() || isAuditor() || isMofSupplier())){
			/*
			 * Enhancement #27668 
			 * TO CATER FPB SECRETARY DROPDOWN LIST
			 */
			pbbSecretaryOnly = true;
		}// Bug #32425 - Start
		else if(isQcaSecretary() || isQcbSecretary()){
			if(!(isDeskOfficer() || isAuditor() || isMofSupplier())){
				qcSecretaryOnly = true;
			}
		}// Bug #32425 - End
		
		if (pbbSecretaryOnly) {
			log.info(":::getDocTypeQtLOADropdownValues() -in true- pbbSecretaryOnly = "+ pbbSecretaryOnly +" :::");
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
		}
		// Bug #32425 - Start
		else if(qcSecretaryOnly){
			log.info(":::getDocTypeQtLOADropdownValues() -in true- qcSecretaryOnly = "+ qcSecretaryOnly +" :::");
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
	    }// Bug #32425 - End
		else if(isMofSupplier()){
			/*Bug #27412 START*/
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL);
			/*Bug #27412 END */
		}else {
			log.info(":::getDocTypeQtLOADropdownValues() -in false- pbbSecretaryOnly = "+ pbbSecretaryOnly +" :::");
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_BIDDING);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
			qtLoaDoctypes.add(ParameterConst.DOCTYPE_QUOTATION);
			
			if (isDeskOfficer() || isAuditor()) {
				qtLoaDoctypes.add(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION);
			}	
		}
		
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
					log.info("loopremoveDeskOfficer");
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"pmStatus.statusId",
						SearchOperator.OPERATOR_IN.value(),
						statusIds));
				docType = ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION;
				statusList = super.getStatusDropdownValuesBySearchBean(
						moduleCode, 
						docType, 
						searchBean);
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
			}//29425 - start
//			if (null == scQt.getQtValidityPeriod()) {
//				
//				addFacesMessage(fc,
//						MessageConst.ERROR_770222,
//						new String[] { getRuleTypeDesc(RuleTypeConst.QT_PROPOSAL_VALIDITY_DAYS) });
//			}//#29425 - end
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
			//log.info("end deleteCommitteemembersData"+this.getManagedBean().getCommittemembers().size());
		}
		
	}
	
	/**
	 * Add attachment to list ScLoiReverseAttachment.
	 * Enhancement #21694
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUploadLoiReversal(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.debug("qtReversalLoihandleFileUpload - START");
		if (event.getFile() != null) {
			log.debug("qtReversalLoihandleFileUpload - START");
			ScAttachment scAttachment = null;
			ScLoiReverseAttachment attachment = new ScLoiReverseAttachment();
			ScQtReverse scQtReverse= this.getManagedBean().getScQtReverse();
			List<ScLoiReverseAttachment> approvalAttachments = scQtReverse.getScLoiReverseAttachments();			
			attachment.setScQtReverse(scQtReverse);
			if(scQtReverse.getDocType().equalsIgnoreCase("LI")){
				ScLoiForQt scLoiForQt = this.getManagedBean().getScLoiForQt();
				if(scLoiForQt!=null){
					attachment.setDocNo(scLoiForQt.getLoiNo());
				}				
			}else if(scQtReverse.getDocType().equalsIgnoreCase("LA")){
				ScLoaForQt scLoaForQt = this.getManagedBean().getScLoaForQt();
				if(scLoaForQt!=null){
					attachment.setDocNo(scLoaForQt.getLoaNo());
				}
			}
			if(approvalAttachments==null){
				approvalAttachments = new ArrayList<>();
			}
			approvalAttachments.add(attachment);
			scQtReverse.setScLoiReverseAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			this.getManagedBean().setScQtReverse(scQtReverse);
		}
		log.debug("qtReversalLoihandleFileUpload - END");
	}
	
	/**
	 * Add attachment to list.
	 * Enhancement #21694
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUploadLoaReversal(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.debug("qtReversalLoahandleFileUpload - START");
		if (event.getFile() != null) {
			log.debug("qtReversalLoahandleFileUpload - START");
			ScAttachment scAttachment = null;
			ScLoaReverseAttachment attachment = new ScLoaReverseAttachment();
			ScQtReverse scQtReverse= this.getManagedBean().getScQtReverse();
			List<ScLoaReverseAttachment> approvalAttachments = scQtReverse.getScLoaReverseAttachments();			
			attachment.setScQtReverse(scQtReverse);
			ScLoaForQt scLoaForQt = this.getManagedBean().getScLoaForQt();
			if(scLoaForQt!=null){
				attachment.setDocNo(scLoaForQt.getLoaNo());
			}
			if(approvalAttachments==null){
				approvalAttachments = new ArrayList<>();
			}
			approvalAttachments.add(attachment);
			scQtReverse.setScLoaReverseAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			this.getManagedBean().setScQtReverse(scQtReverse);
		}
		log.debug("qtReversalLoahandleFileUpload - END");
	}
	
	/**
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
		log.debug("qtDetailhandleFileUpload - START");
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
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
		attachments=null;
		log.debug("qtDetailhandleFileUpload - END");
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

	public Boolean getManagedSearchBeanDocumentTypeIsSuppFinal(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
				&& ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
				) return true;
			return false;
	}

	public Timestamp getExtensionResponseDueDate(Long qtId) {
		IScWorkFlowStatusSO workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocId(qtId);
		reqDTO.setDocType(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD);

		ScWorkFlowStatus status = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		if(status!=null){
			if(status.isCurrent() 
					&& (status.getPmStatus().getStatusId().equals(QTStatusConst.EPV_APPROVED)
					|| status.getPmStatus().getStatusId().equals(QTStatusConst.EPV_PENDING_SUPPLIER_ACCEPTANCE))){
				Timestamp extensionRespDate = getQTDetailSO().getExtensionResponseDueDate(qtId);
				return extensionRespDate;
			}
		}
		return null;
	}
	
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
	public boolean displayMenuItem(String menuItem){
		/*List<String> t = new ArrayList<String>();
		t.addAll(Arrays.asList(
				getMenuItemDetails(),   //0
				getMenuItemSuppQualificationCriteria(), //1
				getMenuItemCommitteeAppointment(), //2
				getMenuItemApproval(), //3
				getMenuItemBriefSiteVisit(), //4
				getMenuItemPublishDetail(), //5
				getMenuItemPaymentDeduction(), //6
				getMenuItemDocuments(), //7
				getMenuItemActivities() //8
				));
		if(getUserTaskIsBPKApproval()){
			t.add(getMenuItemBPKApproval());
		}
		else if(getUserTaskIsCommitteeAppointmentApproval()){
			t.remove(4); //4 from initialized t
			t.remove(4); //5 from initialized t
			t.remove(4); //6 from initialized t
			t.remove(4); //7 from initialized t
			t.add(getMenuItemCommitteeApproval());
		}
		else if(getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec()){
			t.add(getMenuItemCommitteeApprovalEcOcTecFec());
		}
		else if(getUserTaskIsPublicationApproval()){
			t.add(getMenuItemPublicationApproval());
		}
		else;		
		
		
		for(int i = 0; i< t.size(); i++){
			if(menuItem!=null && menuItem.equalsIgnoreCase(t.get(i))){
				return true;
			}
		}
		return false; */
		String taskName = this.getManagedBean().getTaskName()==null ? "":this.getManagedBean().getTaskName();//29464
		Set<String> menuSet = new HashSet<String>();
		menuSet.addAll(
			Arrays.asList(
				getMenuItemDetails(),   //0
				getMenuItemSuppQualificationCriteria(), //1
				getMenuItemCommitteeAppointment(), //2
				getMenuItemApproval(), //3
				getMenuItemBriefSiteVisit(), //4
				getMenuItemPublishDetail(), //5
				getMenuItemPaymentDeduction(), //6 (#29474 - start)
				getMenuItemFulfilmentSchhedule(), //7
				getMenuItemDocuments(), //8
				getMenuItemActivities(), //9
				getMenuItemPublicationApproverAssignment() //10
			));
		if(getUserTaskIsBPKApproval() && isApprovalScreen()){
			menuSet.remove(getMenuItemPublicationApproverAssignment());
			menuSet.add(getMenuItemBPKApproval());
		}
		else if(getUserTaskIsCommitteeAppointmentApproval() && isApprovalScreen()){
			menuSet.remove(getMenuItemBriefSiteVisit()); //4 from initialized menuSet
			menuSet.remove(getMenuItemPublishDetail()); //5 from initialized menuSet
			menuSet.remove(getMenuItemPaymentDeduction()); //6 from initialized menuSet
			menuSet.remove(getMenuItemFulfilmentSchhedule()); //7 from initialized menuSet
			menuSet.remove(getMenuItemDocuments()); //8 from initialized menuSet (#29474 - end)
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
		//29464-start
		else if(taskName.equals(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)){
			menuSet.add(getMenuItemSpecificationCommitteeAssignment());
		};
		//29464-end
		// ***25692#31
		if(isApprovalScreen() && menuSet != null){
			for(Object s : menuSet.toArray()){
				log.info("******** getmenuItemCommitteeApproval() : "+s.toString());
			}
		}
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
			//9019 in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[0] = true;
			}else{
				resArr[0] = !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode().getParameterCode());
			}
			 
			resArr[1] = ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getResultDocType());
			
			//9019 in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[2] = false;
			}else{
				resArr[2] = ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode().getParameterCode());
			}
			//9019 in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[3] = false;
			}else{
				resArr[3] = !ParameterConst.DOCTYPE_BIDDING.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getResultDocType()) && !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase( scQt.getProcurementMode().getParameterCode());
			}
			
			resArr[4] = ParameterConst.DOCTYPE_BIDDING.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getResultDocType());
			resArr[5] = getManagedSearchBeanDocumentTypeIsLOI();
			resArr[6] = getManagedSearchBeanDocumentTypeIsLOA();
			
			//9019 in QT maintenance we need to show QT without procurement mode and handle NPE when no procurement mode.
			if (scQt.getProcurementMode() == null) {
				resArr[7] = false;
			}else{
				resArr[7] = getManagedSearchBeanDocumentTypeIsSuppFinal() && !ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO.equalsIgnoreCase(scQt.getProcurementMode().getParameterCode()) && isAllowQTViewSupplierFinalization(scQt);
			}
			
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
			if(isQTMaintenanceScreen()){//32806-start
			//29464-start
			Long qtStatus = scQt.getQtStatus()==null ? 0L: scQt.getQtStatus().getStatusId();
			resArr[1] = !qtStatus.equals(QTStatusConst.QT_PENDING_SPECIFICATION_COMMITTEE_APPOINTMENT);
			//29464-end
			}//32806-End
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
			public boolean closeQTIfValidityPeriodExpiredAndStatusRelated(){
				boolean isCloseQT = false;
				ScQt scQt = this.getManagedBean().getQt();
				Long statusId = scQt.getQtStatus().getStatusId();
				boolean isBiddingCloseQT = false; 
						
				if (scQt.getProposalValidityEndDate() != null && this.getManagedBean().getCurrentDate() != null 
						 && this.getManagedBean().getCurrentDate().after(scQt.getProposalValidityEndDate())
						 && this.getManagedBean().isMaintanceFlow() == true ) {
					
						if(statusId.equals(QTStatusConst.QT_CLOSE_PERMANENTLY)){
							  isCloseQT = true; 
						}
						if(statusId.equals(QTStatusConst.QT_SUPPLIER_FINALIZATION) || statusId.equals(QTStatusConst.QT_RE_FINALIZATION)){
							IQTDetailSO detailSO = BusinessDelegate.getInstance()
						    .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
						    QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
							quationTenderReqDTO.setQtdetail(scQt);
							QuationTenderResDTO resDTOBID = detailSO.retriveScheduleDetailStatus(quationTenderReqDTO);
							if(null!=resDTOBID && null!=resDTOBID.getBidSchedule()){
								isBiddingCloseQT = checkIsBiddingActiveForStatusCloseQT(scQt);
								if(isBiddingCloseQT){
									isCloseQT = true; 
								}else{
									isCloseQT = false;
								}
							}
							quationTenderReqDTO.setQtId(scQt.getQtId());
							QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
							if(resDTOSF.isCloseQT() == true){
								isCloseQT = true; 
							}
							QuationTenderResDTO resDTOCMT = detailSO.closeQTstatusForCMT(quationTenderReqDTO);
							if(resDTOCMT.isCloseQT() == true){
								isCloseQT = true; 
							}
						}
						if(statusId.equals(QTStatusConst.QT_PENDING_LOI)){
						    IQTDetailSO detailSO = BusinessDelegate.getInstance()
						    .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
						    QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
							quationTenderReqDTO.setQtId(scQt.getQtId());
							QuationTenderResDTO resDTOLOI = detailSO.closeQTstatusForLOI(quationTenderReqDTO);
							if(resDTOLOI.isCloseQT() == true){
							     isCloseQT = true; 
							}
							QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
							if(resDTOSF.isCloseQT() == true){
								 isCloseQT = true; 
							}
						}
                       if(statusId.equals(QTStatusConst.QT_PENDING_LOA)){
                       	IQTDetailSO detailSO = BusinessDelegate.getInstance()
						    .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
						    QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
							quationTenderReqDTO.setQtId(scQt.getQtId());
							QuationTenderResDTO resDTOLOA = detailSO.closeQTstatusForLOA(quationTenderReqDTO);
							if(resDTOLOA.isCloseQT() == true){
							  isCloseQT = true; 
							}
							QuationTenderResDTO resDTOLOI = detailSO.closeQTstatusForLOI(quationTenderReqDTO);
							if(resDTOLOI.isCloseQT() == true){
							  isCloseQT = true; 
							}
							QuationTenderResDTO resDTOSF = detailSO.closeQTstatusForSF(quationTenderReqDTO);
							if(resDTOSF.isCloseQT() == true){
								 isCloseQT = true; 
							}
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
			public void handleCloseQTFileUpload(FileUploadEvent event)
					throws FileNotFoundException, IOException {
				if(event.getFile() != null){
						ScAttachment scAttachment = null;
						ScCloseQTAttachment attachment = new ScCloseQTAttachment();
						ScQt scQt = this.getManagedBean().getQt();
						
						List<ScCloseQTAttachment> attachments = scQt.getScCloseQTAttachment();
						attachment.setScQt(scQt);
						attachment.setDocNo(scQt.getQtNo());
						
						 if(attachments == null){
						   attachments = new ArrayList<>();
					     }
						 			
						if(attachments.isEmpty()){
							attachments.add(attachment);
 							scQt.setScCloseQTAttachment(attachments);
 							scAttachment = attachment;
 							uploadAttachment(event.getFile(), scAttachment);
					        this.getManagedBean().setQt(scQt); 
						}else{
							if(attachment != null){
								handleException(new EpException(MessageConst.ERROR_100063));
							}
                        }		
				}
			}
			
			/**
			 * Delete attachment for Close QT by index
			 * @param index  file index to delete
			 */
			public void deleteCloseQTAttachmentAction(int index){
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
						}else{
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
			    if(CollectionUtils.isEmpty(attachment)){
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
			
            public String getUsername(){
            	String userName = null;
				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
				List<ScCloseQTRemark> remarks = new ArrayList<>();
				
				ScQt qt = this.getManagedBean().getQt();
				quationTenderReqDTO.setQtId(qt.getQtId());
				quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QT_CLOSE);
				
				remarks = findCloseQTRemarksByReqDTO(quationTenderReqDTO);
				
				for(ScRemark remark:remarks){
					userName = remark.getCreatedUser().getUserName();
				}
				
			    return userName;   
			}
            
            public String getUserLoginId(){
            	String userLoginId = null;
				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
				List<ScCloseQTRemark> remarks = new ArrayList<>();
				
				ScQt qt = this.getManagedBean().getQt();
				quationTenderReqDTO.setQtId(qt.getQtId());
				quationTenderReqDTO.setDocType(ParameterConst.DOCTYPE_QT_CLOSE);
				
				remarks = findCloseQTRemarksByReqDTO(quationTenderReqDTO);
				
				for(ScRemark remark:remarks){
					userLoginId = remark.getCreatedUser().getLoginId();
				}
			    return userLoginId;   
			}
			// 26095@hatta - end
            
            
          //Start 19287      
            public void resetSearchDataResults(){
            	log.info("==========resetSearchDataResults========");
            	if(isQTSearchScreen()){
            		IPagingDataModel<ScQt> searchQTDataModel = getManagedBean()
            				.getSearchQTDataModel();
            		
            			searchQTDataModel = new QTPagingDataModel("searchQTs", getManagedBean()
            					.getQtnTenderSearchBean());
            			getManagedBean().setSearchQTDataModel(searchQTDataModel);
            			getManagedBean().getSearchQTDataModel().resetData();
            			IPagingDataModel<ScLoaForQt> searchLoaDataModel = getManagedBean()
            					.getSearchLoaForQtDataModel();          			
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
            //End 19287 
   //#25692-start         
    public Long retirveStatusId() {
    	if(((null!=this.getManagedBean().getTaskName()&&SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(this
				.getManagedBean().getTaskName()))|| (getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && isApprovalScreen()) || (getUserTaskIsBPKApproval() && isApprovalScreen())  || (getUserTaskIsCommitteeAppointmentApproval() && isApprovalScreen()))){
    		log.debug("=====returnif====");
    	return this.getManagedBean().getSubStatusId();	
    	}else{
    		log.debug("=====returnelse====");
    		return this.getManagedBean().getCurrentStatusId();	
    	}
    	
    }
    //#25692-End  
    //#17376-start onlineBiddingcodes
    public String viewOnlineBidding(ScQt scQt, String previousPageUrl){
    	//ScQt qt=resDTO.getQtResultBean().getResultList().get(i);
    	log.info("============viewOnlineBidding============");
    	IQTDetailSO detailSO;
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO qtReqDTo=new QuationTenderReqDTO();
		qtReqDTo.setQtdetail(scQt);
		QuationTenderResDTO qtResDTO=detailSO.retriveScheduleDetailStatus(qtReqDTo);
		BiddingBackingBean biddingBB=new BiddingBackingBean();
		// #30451-Start.
		if (getManagedBean().isBdSfFlow()) {
			ScheduleBiddingBackingBean sbb = new ScheduleBiddingBackingBean();
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			bpmReqDTO.setDocNumber(scQt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		    if(bpmResDTO != null && CollectionUtils.isNotEmpty(bpmResDTO.getWorklistVO().getWorklistItem())){
				 for(WorklistItemVO currentVO: bpmResDTO.getWorklistVO().getWorklistItem()){
					 if (StringUtils.isNotEmpty(currentVO.getTaskId())) {
						 sbb.renewSesionObject();
						 sbb.navigateToBiddingPage(currentVO.getTaskId());
						 return "/xhtml/sc/bidding/scheduleBidding/scheduleBiddingMain.xhtml";
					 }
					 break;
				 }
		    }
		} else if(null!=qtResDTO && null!=qtResDTO.getBidSchedule()){
			biddingBB.renewSesionObject();
			return biddingBB.bidManagementQuotationTenderAction(qtResDTO.getBidSchedule(),previousPageUrl);
		}
		// #30451-End.
    	return "/xhtml/sc/bidding/bidManagement/bidManagementMain.xhtml?faces-redirect=true";	
    	} //#17376-End onlineBiddingcodes
    
    //#25037-start
    public void navigateToViewHistory(ScQtCommittee scQtCommittee, boolean showHistory){
    	this.getManagedBean().setShowHistory(showHistory);
    	if(this.getManagedBean().isShowHistory()){
    		ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
    		List<ScCommitteeMemberHistory> histories = committeSO.getMergedHistory(scQtCommittee, false);
    		this.getManagedBean().setCommitteeHistories(new Paginator(histories));
    		this.getManagedBean().setCurrentCommitteeDesc(this.getParameterDesc(scQtCommittee.getScCommittee().getCommitteeType(), new FacesScopes().getUserLocale()));
    		this.getManagedBean().setSelectedHistoryMember(new Paginator(new ArrayList<QtCommitteeMemberBean>()));
    	}
    }
    
	@SuppressWarnings("unchecked")
    public void onSelectHistory() throws IOException{
    	ScCommitteeMemberHistory history= this.getManagedBean().getSelectedCommitteeHistory();
		List<QtCommitteeMemberBean> list =(List<QtCommitteeMemberBean>)(Object)history.deserializeList(history.getCommitteeMembers());
		CustomUtils.sortQtCommitteeMemberRole(list);
    	Paginator selectedHistoryMember = new Paginator(list);
    	this.getManagedBean().setSelectedHistoryMember(selectedHistoryMember);
    }
	
	public void assertSpecificationUpdateInProgress(boolean showPopup){
		boolean inProgress = false;
		String taskName = this.getManagedBean().getTaskName();
		taskName = StringUtils.isNotEmpty(taskName) ? taskName: "";
		String[] taskNames = new String[]{SCProcessNameConst.PREPARE_QT_SCHEDULE, SCProcessNameConst.APPROVE_PUBLICATION};
		String[] specTaskName = new String[]{SCProcessNameConst.SPEC_MAINTENANCE_ENDORSE_SCORING, SCProcessNameConst.SPEC_MAINTENANCE_SIGNOFF_SCORING};

		if(Arrays.asList(taskNames).contains(taskName)){
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
			ScQt qt = this.getManagedBean().getQt();
			bpmReqDTO.setDocNumber(qt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
			 for(WorklistItemVO currentVO: resDTO.getWorklistVO().getWorklistItem()){
				 if(StringUtils.isNotBlank(currentVO.getTaskName())){
					 if(Arrays.asList(specTaskName).contains(currentVO.getTaskName())){
						 this.getManagedBean().setSpecUpdateInProgress(true);
						 FacesContext.getCurrentInstance().addMessage(null,  new FacesMessage(FacesMessage.SEVERITY_ERROR, null, getMessage("lbl.sc.popup.checkBeforePublish", null)));
						 inProgress = true;
						 break;
					 }
				 }
			 }
		}

		if(!inProgress && showPopup){
			RequestContext.getCurrentInstance().execute("submitConfirmPopUp2.show()");
		}
		
	}
	//#29425 - start
	public void validateNewValueForValidityDays(int newValue) {
		PmParameter procureMentMode = this.getManagedBean().getQt().getProcurementMode();
		if (newValue == 0) {
			this.getManagedBean().setNewValidityDays(newValue);
			this.getManagedBean().setNewValueValidity(true);
		}
		if (ParameterConst.PROCUREMENT_MODE_QUOTATION
				.equalsIgnoreCase(procureMentMode.getParameterCode())
					&& newValue > 90) {
			this.getManagedBean().setNewValueValidity(true);
		}
		validateValidityDays(newValue);
		if (ParameterConst.PROCUREMENT_MODE_TENDER
				.equalsIgnoreCase(procureMentMode.getParameterCode())
					&& newValue != 0
						&& newValue < 90) {
			this.getManagedBean().setNewValueValidity(true);
		}
		if (ParameterConst.PROCUREMENT_MODE_TENDER
				.equalsIgnoreCase(procureMentMode.getParameterCode())
						&& newValue >= 90) {
			this.getManagedBean().setNewValueValidity(false);
		}
		this.getManagedBean().setNewValidityDays(newValue);
	}
	
	public void validateValidityDays(int validityDays) {
		PmParameter procureMentMode = this.getManagedBean().getQt().getProcurementMode();
		if (validityDays == 0) {
			throw new EpException(MessageConst.ERROR_100022,
					new String[] { 
							ResourceBundleUtils.getMessage(
									"lbl.sc.qt.proposal.validity", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									new FacesScopes().getUserLocale()) });
		}
		if (ParameterConst.PROCUREMENT_MODE_QUOTATION
				.equalsIgnoreCase(procureMentMode.getParameterCode())) {
			int valDay = getProposalValidityDays(this.getManagedBean().getQt().getEstimatedAmount());
			if (null != this.getManagedBean().getQt().getQtValidityPeriod() && valDay < validityDays) {
				throw new EpException(MessageConst.ERROR_760044,
						new String[] { String.valueOf(valDay)});
			}
		}
	}
	
	private int getProposalValidityDays(BigDecimal estimatedAmount) {

		if (estimatedAmount != null) {
			IRuleSO ruleSO = null;
			try {
				RuleReqDTO reqDTO = new RuleReqDTO();
				reqDTO.setRuleType(RuleTypeConst.QT_PROPOSAL_VALIDITY_DAYS);
				reqDTO.setCompareValue(estimatedAmount.toString());
				ruleSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IRuleSO.class);
				List<String> ruleValueStrList = ruleSO
						.getRuleValuesByTypeAndCurrentDateRange(reqDTO)
						.getRuleValueStrList();

				if (ruleValueStrList != null && ruleValueStrList.size() > 0) {
					return Integer.valueOf(ruleValueStrList.get(0));
				} else {
					throw new EpException(
							MessageConst.ERROR_770222,
							new String[] { getRuleTypeDesc(reqDTO.getRuleType()) });
				}
			} finally {
				ruleSO = null;
			}
		}
		return 0;
	}
	
	private void disableNewValueValidity() {
		PmParameter procureMentMode = this.getManagedBean().getQt().getProcurementMode();
		if (ParameterConst.PROCUREMENT_MODE_QUOTATION
				.equalsIgnoreCase(procureMentMode.getParameterCode())) {
			this.getManagedBean().setNewValueValidity(false);
		}
	}
	//#29425 - end
	public String getDoaaRequestStatus() {
		
		IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_DOA_ASSIGNMENT);
		reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
		ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		Long statusId = wfStatus==null ? 0L: wfStatus.getPmStatus().getStatusId();

		this.getManagedBean().setDoaRequestEditable(true);
		if(wfStatus == null){
			FacesScopes fs = new FacesScopes();
			return ResourceBundleUtils.getMessage("lbl.rpt.pendingsubmission", ResourceBundleUtils.BUNDLE_COMMON_LABELS, fs.getUserLocale());
		}

		Long[] statusArr = new Long[]{QTStatusConst.DAR_COMPLETED, QTStatusConst.DAR_REJECTED};
		if(!Arrays.asList(statusArr).contains(statusId)){
			this.getManagedBean().setDoaRequestEditable(false);
		}
		return this.getStatusDescById(statusId);
	}
	
	private List<ScQtContactPerson> generateDOAFromRequestContent(ScQtRequestContent requestContent, ScQt qt) throws IOException{
		List<ScQtContactPerson> doaList = new ArrayList<ScQtContactPerson>();
		Object content = requestContent.getRequestContent() == null ? new HashMap<Long, String>(): requestContent.getRequestContent();
		@SuppressWarnings("unchecked")
		Map<Long, String> doaMap = (Map<Long, String>) content;
		for (Map.Entry<Long, String> map : doaMap.entrySet()) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(map.getKey());
			UserResDTO userResDTO = this.getUserSOGlobal()
					.retrieveUserByUserId(userReqDTO);
			PmUser user = userResDTO.getPmUser();
			if (user != null
					&& user.getLoginId().equalsIgnoreCase(
							map.getValue())) {
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
		return doaList;
	}
	
	private String generateDOACompareStr(List<ScQtContactPerson> list){
		if(list==null){
			return "";
		}
		Collections.sort(list, new Comparator<ScQtContactPerson>() {
			public int compare(ScQtContactPerson o1, ScQtContactPerson o2) {
				Long o1Id = o1.getUser().getUserId()==null ? 0L: o1.getUser().getUserId();
				Long o2Id = o2.getUser().getUserId()==null ? 0L: o2.getUser().getUserId();
				return o2Id.compareTo(o1Id);
			}
		});
		String str = "";
		for(ScQtContactPerson p: list){
			Long userId = p.getUser().getUserId()==null? 0L: p.getUser().getUserId();
			str = str.concat(userId.toString());
		}
		return str;
	}
	
	public boolean isDOAListModified(){
		boolean modified = false;
		try{
			String currentList = this.getManagedBean().getPrevDOAs();
			currentList = currentList==null ? "": currentList;
			String newList = generateDOACompareStr(this.getManagedBean().getPmuserContactpersonList());

			if(!currentList.equalsIgnoreCase(newList)){
				modified = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return modified;
	}
	private void initCommitteeTabSingularReapproval(Object payload){
		if(payload==null){
			throw new EpException("Payload should not be empty.");
		}
		SCCommitteeDataType committeeData = payload == null ? new SCCommitteeDataType(): (SCCommitteeDataType) payload;
		boolean isReapproval = committeeData.getReapproval() == null ? false:  committeeData.getReapproval();
		this.getManagedBean().setReapproval(isReapproval);
		if(isReapproval && this.getManagedBean().getQt()!=null){
			IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
			ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			
			if(wfStatus!=null){
				Long statusId = wfStatus.getPmStatus().getStatusId();
				if(!this.isStatusLessThanOrEquals(statusId, QTStatusConst.QT_PROPOSAL_PREPARATION, QTStatusConst.QT_ORDERED_STATUSES)){
		            //25037-start #372
					String tabType = committeeData.getCommitteeType() == null ? "": committeeData.getCommitteeType();
					getManagedBean().setRoleFilterList(getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), tabType));
		            //25037-end #372
					List<TabBean> tabs = this.getManagedBean().getScCommitteeMemberTabList();
					int i = 0;
					for(TabBean tab: tabs){
						if(tabType.equalsIgnoreCase(tab.getTabType())){
							this.getManagedBean().setSelectedTabBean(tab);
							this.getManagedBean().setSelectedTabIndex(i);
							this.getManagedBean().setSelectedCommitteeTabIndex(i);
						}else{
							tab.setDisabled(true);
						}
						i++;
					}
				}
			}
		}
	}
    //#25037-end
	
	
	/** 27668-start
	 * Get PTJ condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJMaintenaceCondition() {
		debugLogger("getPTJCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getQtnTenderSearchBean();
		Long MinistryOrgProfileId = qtnTenderSearchBean.getMinistryProfile()
				.getOrgProfileId();
		Long PTJGroupOrgProfileId = qtnTenderSearchBean.getPtjGroupProfile()
				.getOrgProfileId();
		
		if (null != MinistryOrgProfileId) {
			condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
			condition.append(OrgProfileUtil
					.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ SourcingConstants.LOV_CONDITION_SEPARATOR + MinistryOrgProfileId);
		} 
		 else if (null != PTJGroupOrgProfileId) {
				condition.append(SourcingConstants.LOV_FIELD_SEPARATOR);
				condition.append(OrgProfileUtil
						.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
						+ SourcingConstants.LOV_CONDITION_SEPARATOR + PTJGroupOrgProfileId);
			}
		return condition.toString();
	} //#27668-End
	
	/**
	 * #13344 Start : saveQtCAApproval
	 * 
	 * @return the string
	 */
	public void saveQtCAApproval() {
		log.debug("saveQtCAApproval");
		IQTDetailSO detailSO;
		try {
			if(((getUserTaskIsCommitteeAppointmentApproval() && this.getManagedBean().getActiveIndex()==5) || (getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec() && this.getManagedBean().getActiveIndex()==9))){//13344-start
				log.info("=========saveQtCAApprovalActiveIndexCodesExecuted======="+this.getManagedBean().getActiveIndex());
			/*13344-start this.getManagedBean().setSaveFlow(true);
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

			updateApprovalDetails(this.getManagedBean().getQt());
			retrieveApprovalTabDtl();

			QuationTenderReqDTO quationTenderReqDTO = prepareQTInfo();
			quationTenderReqDTO.setApproval(this.getManagedBean().getScQtApproval());
			quationTenderReqDTO.setApprovalRequests(this.getManagedBean().getScQtApprovalRequestList()) ;
			quationTenderReqDTO.setSaveFlow(true);
			ScQt qtDetails = quationTenderReqDTO.getQtdetail();
			QtAmendMentBean amendMentBean = getManagedBean().getAmendMentBean();

			quationTenderReqDTO.setQtdetail(qtDetails);
			quationTenderReqDTO.setAmendMentBean(amendMentBean);
			quationTenderReqDTO.setRoleCode(getUserRoleCode());

			QuationTenderResDTO quationTenderResDTO = detailSO
					.updateQtCAAprovalDetails(quationTenderReqDTO);13344-End */
				String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";//13344-start
				String approvalType=this.getManagedBean().getApprovalType();
			    boolean approvaleditable=	this.getManagedBean().isApprovalEditableSection();
				boolean approvalScreen=this.getManagedBean().isApprovalScreen();
				boolean isCommitteeUpdateFlow=this.getManagedBean().isCommitteeUpdateFlow();
				List<ScQtApprovalRequest> approvalRequest=this.getManagedBean().getScQtApprovalRequestList();
				boolean committeeApprovalSpec=getUserTaskIsCommitteeAppointmentApproval();
				boolean committeeApprovalECOC=getUserTaskIsCommitteeAppointmentApprovalEcOcTecFec();
				List<String> paramTypes=this.getManagedBean().getParamTypes();
				updateApprovalDetails(this.getManagedBean().getQt());
				retrieveApprovalTabDtl();
				validateQtDetails();
				//String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
				//super.isQTStatusLessThanOrEquals(this.getManagedBean().getCurrentStatusId(), QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL)
				if(committeeApprovalSpec){
					log.info("======ifconditionCodesExecuted====");
				this.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(), pageAppr);
				this.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApproval);
				}else{
					log.info("======elseconditionCodesExecuted====");
					this.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString(), pageAppr);
					this.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
					this.getManagedBean().setParamTypes(paramTypes);
					ApprovalBackingBean approvalbean=new ApprovalBackingBean();
					approvalbean.retriveCommiteeApprovalTabDetails();
					retrieveApprovalTabDtl();
				}
				this.getManagedBean().setParamTypes(paramTypes);
				this.getManagedBean().setApprovalType(approvalType);
				this.getManagedBean().setApprovalEditableSection(approvaleditable);
				this.getManagedBean().setApprovalScreen(approvalScreen);
				this.getManagedBean().setCommitteeUpdateFlow(isCommitteeUpdateFlow);
				this.getManagedBean().setScQtApprovalRequestList(approvalRequest);
				ScWorkFlowStatus currentStatus=null;
				if (currentStatus == null) {
					currentStatus = getManagedBean().getQt().getCurrentWorkFlowStatus();
					IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
					ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
					reqDTO.setDocType(ParameterConst.DOCTYPE_COMMITTE_APROVAL);
					reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
					ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					currentStatus=wfStatus;
					
				}
				
						initializeRemarks(this.getManagedBean().getScQtApproval(), 
						currentStatus, 
						this.getManagedBean().getScQtApproval().getScQtApprovalRemarks(),
						ScQtApprovalRemark.class);
			
				successMessage(MessageConst.INFO_700090, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);
			
			}//13344-End
			else{//13344-start
				successMessage(MessageConst.INFO_700090, ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtNo(), Boolean.FALSE);	
			}//13344-End
		} finally {
			detailSO = null;
		}
	}
	// 25077 - Start
	public boolean checkSettingPopUpBPKApprovalNeeded(ScQt scQt){
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			    boolean bpkFlag = false;
			    Long statusId = scQt.getQtStatus().getStatusId();
			    if(this.getManagedBean().isMaintanceFlow() == false && 
				    null != statusId && (statusId.equals(QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT) || 
			    		statusId.equals(QTStatusConst.QT_REVISION_PUBLICATION))){
			    	
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
	
	private boolean skipPopUpBPKApprovalNeededOnceApproved(List<ScQtApproval> approval,String approvalCategory){
		boolean skipPopUp = true;
		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {
				if (approvalCategory.equalsIgnoreCase(approval.get(i).getApprovalCategory().getParameterCode())) {
					List<ScQtApprovalRequest> approvalRequest = approval.get(i).getScQtApprovalDtls();
					List<ScQtApprovalRequest> approvalRequestHistory = approval.get(i).getHistoryApprovalRequests();
					if (CollectionUtils.isNotEmpty(approvalRequest)) {
						skipPopUp = true;
					}else{
	       				if (CollectionUtils.isNotEmpty(approvalRequestHistory)) {
	       					for (int j = 0; j < approvalRequestHistory.size(); j++) {
	       						PmParameter approvalAction = approvalRequestHistory.get(j).getApproverAction();
	       						PmParameter approvalType = approvalRequestHistory.get(j).getApprovalType();
	       						if(null!=approvalAction && null!=approvalType){
	       						    if(approvalAction.getParameterCode().equals(ParameterConst.APPROVAL_DECISION_TYPE_APPROVED)){
	       						    	if(approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_OPEN_TO_BUMI) ||
	       						    	   approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_BUMI_TO_OPEN) ||
	       						    	   approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD) ||
	       						    	   approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_REDUCE_PROPOSAL_VALIDITY_PERIOD)){
	       						    		   skipPopUp = false;
	       						    	}
	       						    }  
	       						}
	       					}
	       				}
				    }
				}
			}
		}
		return skipPopUp;
	}
	
	public void resetBpkCurrentArea(boolean actionFlow){
		if(actionFlow){
		   this.getManagedBean().setResetBpkCurrentArea(true);
		   this.getManagedBean().setShowPopUpBPKApprovalNeeded(false);
		}else{
		   this.getManagedBean().setShowPopUpBPKApprovalNeeded(false);
		}
	}
	
	public boolean isOthersApprovalForInstruction(){
		boolean isForInstruction = false;
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		String procurementCategory = scQt.getPmProcurementCategory().getParameterCode();
	    if(null != procurementCategory && ParameterConst.PROCUREMENT_CATEGORY_ICT.equals(procurementCategory)){
        	if(null != statusId && 
      			   (QTStatusConst.QT_PENDING_QT_SUBMISSION.equals(statusId) || 
      			    QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(statusId) || 
      			    QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
      				QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId))){
         		isForInstruction = true;   
         	}    
	    } 
        if(null != procurementCategory && ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA.equals(procurementCategory)){
        	if(null != statusId && 
     			   (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
     				QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId))){
        		isForInstruction = true;   
        	}  	   
		}
        return isForInstruction; 
	}
	
	public boolean isOthersApprovalMandatoryForAttachment(){
		boolean isMandatoryForUpload = false;
		ScQt scQt = this.getManagedBean().getQt();
		Long statusId = scQt.getQtStatus().getStatusId();
		String procurementCategory = scQt.getPmProcurementCategory().getParameterCode();
		if(null != statusId && 
			   (QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
				QTStatusConst.QT_REVISION_PUBLICATION.equals(statusId))){
	       if(null != procurementCategory && 
			       (ParameterConst.PROCUREMENT_CATEGORY_UBATAN_DAN_BAHAN_KIMIA.equals(procurementCategory) ||
			    	ParameterConst.PROCUREMENT_CATEGORY_ICT.equals(procurementCategory))){
	    	   isMandatoryForUpload = true;   
		   } 
		}
        return isMandatoryForUpload; 
	}
	
	private void checkingOtherApprovalFileAttachmentNeedDeleted(List<ScQtApproval> approval,String approvalCategory){
		if (CollectionUtils.isNotEmpty(approval)) {
			for (int i = 0; i < approval.size(); i++) {
				if (approvalCategory.equalsIgnoreCase(approval.get(i).getApprovalCategory().getParameterCode())) {
					List<ScQtApprovalRequest> approvalRequestHistory = approval.get(i).getHistoryApprovalRequests();
       				if (CollectionUtils.isNotEmpty(approvalRequestHistory)) {
       					for (int j = 0; j < approvalRequestHistory.size(); j++) {
       						PmParameter approvalAction = approvalRequestHistory.get(j).getApproverAction();
       						if(null!=approvalAction){
       						   if(this.getManagedBean().isFlagFileAttahmentDeleted()){
       							 if(null == this.getManagedBean().getScQtApprovalReqAttachementHistory()){
      						         approvalRequestHistory.get(j)
				   					         .setApprovalRequestAttachement(
				   					        		 this.getManagedBean().getScQtApprovalReqAttachementHistory());
		      					 }
	  			                 ScQtApprovalReqAttachement approvalReqAttachement = approvalRequestHistory.get(j).getApprovalRequestAttachement();
	      	                     if(null == approvalReqAttachement){
	      	                    	  throw new EpException(MessageConst.ERROR_760509);
	      	                     }
       						   }else{
       							 if(null != approvalRequestHistory.get(j).getApprovalRequestAttachement()){
   								       this.getManagedBean().setScQtApprovalReqAttachementHistory(
   								    	    	approvalRequestHistory.get(j)
   								    		         .getApprovalRequestAttachement());
   							     }
       						   } 
       						}
       					}
       				}
				}
			}
		}
	}
	
	private void checkValidationForStatusAndFileAttachment(List<ScQtApproval> approval,String approvalCategory){
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
      	                    if(null == approvalReqAttachement){
      	                    	this.getManagedBean().setResetBpkCurrentArea(false);
      	                    	throw new EpException(MessageConst.ERROR_760509);
      	                    }
      	                    
       					}
					}
				} 
			} 
		}
	}
	
	/**
	 * Listener method to handle Others Approval file download event
	 * @param scQtApprovalRequest
	 * @throws Exception
	 */
	public void downloadOthersApprovalFile(ScQtApprovalRequest scQtApprovalRequest) throws Exception {
		downloadFile(scQtApprovalRequest.getApprovalRequestAttachement());
	}
	// 25077 - End
	
    //#13344 start
    public boolean isCommitteeApprovalTask(String taskName) {
     	return (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName));
    }
    //#13344 end
    
    //#25037 - start
  	public boolean validateIsCommitteeFormed(boolean showPopup){
  		ScCommittee specCommittee = null;
  		if(CollectionUtils.isNotEmpty(this.getManagedBean().getScCommitteeMemberTabList())){
  			for(TabBean committeeTab: this.getManagedBean().getScCommitteeMemberTabList()){
  				ScQtCommittee qtCommittee = committeeTab.getScQtCommittee();
  				String[] types = ParameterConst.QT_COMMITTEES_ORDERED_LIST; //25037 #372
  				if(qtCommittee!=null){
  					ScCommittee committee = qtCommittee.getScCommittee();
  					if(Arrays.asList(types).contains(committee.getCommitteeType().getParameterCode())
  							|| committee.isApprovalReq()){
  						QTCommitteBackingBean committeeBB = new QTCommitteBackingBean();
  						committeeBB.checkCommitteFormedOrNot();
  						if(committee.getCommitteeType().getParameterCode()
  								.equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE)){
  							specCommittee = committee;
  						}
  					}
  				}
  			}

  			if(specCommittee!=null && showPopup){
  				boolean modified = isCommitteeModified(specCommittee.getScCommitteeMembers(), getMemberHistoryList(specCommittee));
  				if(modified && specCommittee.isApprovalReq()){
  	  				//25037 - start - note 510
  					ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
					CommitteReqDTO reqDTO = new CommitteReqDTO();
					reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
					reqDTO.setCommitteeType(ParameterConst.COMMITTEE_TYPE_SPECIFICATION_COMMITTEE);
					CommitteResDTO resDTO = committeSO.retrieveCommitteActivityDtls(reqDTO);
					List<ScQtCommitteeActivity> activities = resDTO.getCommitteActivities()==null ? new ArrayList<ScQtCommitteeActivity>(): resDTO.getCommitteActivities();
					for(ScQtCommitteeActivity activity: activities){
						if(activity.isSignedOff() && activity.getScCommitteeMember().getMemberRole().getParameterCode().equals(ParameterConst.COMMITTEE_ROLE_MEMBER)){
							RequestContext.getCurrentInstance().execute("confirmReendorseOnReassignmentPopup.show()");
		  	  				return true;
						}
					}
		  			//25037 - end - note 510
  	  			}
  			}	
  		}
		return false;
  	}
  	private boolean isCommitteeModified(List<ScCommitteeMember> currentList, List<QtCommitteeMemberBean> previousList){
		if(CollectionUtils.isEmpty(previousList)){
			previousList = new ArrayList<QtCommitteeMemberBean>();
		}
		if(CollectionUtils.isEmpty(currentList)){
			currentList = new ArrayList<ScCommitteeMember>();
		}
		String[] previousListArr = new String[previousList.size()];
		String[] currentListArr = new String[currentList.size()];
		for(int i=0;i<previousListArr.length;i++){
			QtCommitteeMemberBean m = previousList.get(i);
			previousListArr[i] = m.getIdentificationNo().concat(m.getMemberRole());
		}
		for(int i=0;i<currentListArr.length;i++){
			ScCommitteeMember m = currentList.get(i);
			currentListArr[i] = m.getIcPassport().concat(m.getMemberRole().getParameterCode());
		}
		Arrays.sort(previousListArr);  
		Arrays.sort(currentListArr);  
		return !StringUtils.join(previousListArr, ",").equalsIgnoreCase(StringUtils.join(currentListArr, ","));
	}
  	//#25037 - end
    
    /**
	 * CR-9019 will be used to cancel QT
	 */
	public void cancelQTDraft() {
		IQTDetailSO detailSO;
		try {
			this.getManagedBean().setSaveFlow(false);
			detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class); //

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
			quationTenderReqDTO.setToken(facesScopes
					.getBPMAuthenticationCtxToken());
			quationTenderReqDTO.setTaskId(this.getManagedBean().getTaskId());
			QuationTenderResDTO quationTenderResDTO = detailSO.cancleQTForDraft(quationTenderReqDTO);

			if (quationTenderResDTO.isSuccessful()) {
				successMessage(ackMsg, ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION,
						qt.getQtNo(), Boolean.TRUE);
			}
		} finally {
			detailSO = null;
		}
	}

	private void throwExceptionForInvalidRemark() {
		throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
				"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale()) });
	}
  	
  	/**
  	 * CR-9019 will be used to differentiate between cancel and submit 
  	 * @param outCome
  	 * @return
  	 */
  	public boolean isCancel(String outCome){
  		return BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outCome);
  	}
  	
  	
  	/**
  	 * CR-9019 will be used to enable/disable mandayory checking on UI
  	 * @param scQt
  	 * @return
  	 */
  	public boolean isProcurementTitleMandatory(ScQt scQt){
  		if(scQt.isCloned()){
  			return false;
  		}	
  		return scQt.isBrandNew() || (StringUtils.isEmpty(scQt.getQtTitle()));
  	}
  	
  	/**
  	 * CR-9019 will be used to enable/disable mandayory checking on UI
  	 * @param scQt
  	 * @return
  	 */
  	public boolean isFileNoMandatory(ScQt scQt){
  		if(scQt.isCloned()){
  			return false;
  		}
  		return scQt.isBrandNew() || StringUtils.isEmpty(scQt.getFileNo());
  	}
  //29464-start - added logic at backingbean for future addition
  	public boolean evaluateCommitteeAppointmentPage(){
  		boolean flag = false;
  		String taskName = this.getManagedBean().getTaskName()==null ? "": this.getManagedBean().getTaskName();
  		//#11299 Start Vinod Added REVISE_COMMITTEE condition
  		if(this.getManagedBean().isCommitteeAppointment() && !taskName.equalsIgnoreCase(SCProcessNameConst.ASSIGN_SCORING_COMMITTEE)
  				&& !taskName.equalsIgnoreCase(SCProcessNameConst.REVISE_COMMITTEE)){
  			flag = true; 
  		}
  		//#11299 End Vinod
  		return flag;
  	}
  	//29464-end
  	
  	//#28784 - Start
//  	public void actionSearchPresentationDemo(){
//  		if(CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions())){
//  			SpecificationBackingBean specbb = new SpecificationBackingBean();
//  			SpecificationResDTO resDTO = null;
//  			resDTO = specbb.retrieveDemoSpecChecklist();
//  			List<TplSpecQuestion> list = resDTO.getScQtCheckList().getScQtDoc().getTplSpecMaster().getTplSpecQuestions();
//  			List<TplSpecQuestion> listNew = new ArrayList<>();
//  			if(StringUtils.isNotBlank(this.getManagedBean().getItemName())){
//  	  			String itemName = this.getManagedBean().getItemName().toLowerCase();
//  	  			for(TplSpecQuestion obj : list){
//  	  				if(StringUtils.isNotBlank(obj.getClItem())){
//  	  					if(obj.getClItem().toLowerCase().contains(itemName)){
//  	  						obj.setSelectable(true);
//  	  						listNew.add(obj);
//  	  					}else{
//  	  						obj.setSelectable(false);
//  	  					}
//  	  				}
//  	  			}
//  	  		}else{
//  	  			for(TplSpecQuestion obj : list){
//  	  	  			obj.setSelectable(true);
//  	  	  		}
//  	  		}
//  			this.getManagedBean().getTemplateSpecBean().getSpecMaster().setTplSpecQuestions(listNew);
//  		}
//  	}
//
//  	public void resetSearchPresentationDemo(){
//  		if(CollectionUtils.isNotEmpty(this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions())){
//  			List<TplSpecQuestion> list = this.getManagedBean().getTemplateSpecBean().getSpecMaster().getTplSpecQuestions();
//  			for(TplSpecQuestion obj : list){
//  				obj.setSelectable(true);
//  			}
//  		}
//  		this.getManagedBean().setItemName(null);
//  	}
  	//#28784 - End
  	
    //#30477 - Start
  	public boolean isInactiveCommiteeApproverRole(Long userId) {
		IQTDetailSO detailSO = null;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			PmUserOrg pmUserOrgs = new PmUserOrg();
			
			quationTenderReqDTO.setUserId(userId);
			pmUserOrgs = detailSO.retrievePmUserOrgByUserid(quationTenderReqDTO).getPmUserOrg();
			
			PmUserOrg pmUserOrg = PmCommonUtil.retrieveUserOrgByUserIdAndOrgId(userId, pmUserOrgs.getUserOrgId());
			
			boolean inactive = DbValueConst.FLAG_YES;;
			for (PmUserRole roles : pmUserOrg.getPmUserRoles()) {	
				if(RoleCode.COMMITTEE_APPROVER.value().equals(roles.getPmRole().getRoleCode()) && 
						roles.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
					inactive = DbValueConst.FLAG_NO;	
				}else if(RoleCode.PUBLICATION_APPROVER.value().equals(roles.getPmRole().getRoleCode()) && 
						roles.getRecordStatusFlg().equals(DbValueConst.RECORD_STATUS_ACTIVE)){
				    inactive = DbValueConst.FLAG_NO;	
				}
			}
			return inactive;
			
		} finally {
			detailSO = null;
		}
	}
  	
    public boolean isCommitteeApprovalReviseTask(String taskName) {
     	return (SCProcessNameConst.REVISE_COMMITTEE.equalsIgnoreCase(taskName));
    }
    //#30477 - End
    
    /**
     * BUG 31120
     * TO ENABLE MAGNIFIER GLASS FOR GROUP PTJ & PTJ
     * @return Boolean
     */
  	public Boolean isDisabledPtj(){
  		if(isQTSearchScreen()){
  			return !isAuditPTJRuleSet();
  		}else if(isQTMaintenanceScreen()){
  			return !isMaintenancePTJRuleSet();
  		}else 
  			return true;
  	}
  	
  	/**
  	 * BUG 31120
  	 * TO ENABLE MAGNIFIER GLASS FOR GROUP PTJ & PTJ
  	 * @return Boolean
  	 */
  	public Boolean isDisabledGroupPtj(){
  		return !isGroupPTJRuleSet();
  	}
  	//#29472 - start
  	public void reviseQtDocument() {
  		
		IQTDetailSO detailSO;
		try {
			ScQt scQt = this.getManagedBean().getQt();
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtdetail(this.getManagedBean().getQt());
			reqDTO.setOutcome(BpmOutcomeConst.BPM_REVISE);
			reqDTO.setPmUser(new FacesScopes().getUserSessionBean());
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			detailSO.qtReviseDocument(reqDTO);
			
			if (scQt.getProcurementMode().getParameterCode().equals(ParameterConst.PROCUREMENT_MODE_TENDER)) {
				successMessage(MessageConst.INFO_700107, getMessageCommon("lbl.qt.maintenance.tender"), scQt.getQtNo(), Boolean.TRUE);
			} else {
				successMessage(MessageConst.INFO_700107, getMessageCommon("lbl.qt.maintenance.quotation"), scQt.getQtNo(), Boolean.TRUE);
			}
		} finally {
			detailSO = null;
		}
	
  	}
  	//#29472 - end
  	
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
	
	public static PmDivision retrievePmDivsById(Long divnId) {
		IDivisionSO reqSO = (IDivisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IDivisionSO.class);
		DivisionReqDTO reqDTO = new DivisionReqDTO();
		reqDTO.setDivisionId(divnId);
		DivisionResDTO resDTO = reqSO.retrieveDivisionById(reqDTO);

		return resDTO.getPmDivision();
	}//11104-End
	
	//#28784 - start
public void retrieveDemoQtDetails(Long qtId) {
		if (null != qtId) {
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
						&& null != this.getManagedBean().getZoneGeneralTrees() && this
								.getManagedBean().getZoneGeneralTrees()
								.isEmpty())) {
					long startTime = System.currentTimeMillis();
					tenderResDTO = QtDetailsSO.retriveDemoQTDetails(tenderReqDTO);
					SLAUtils.getInstance().logHiExecInfo("QT","QTDetailBackingBean.retrieveQuationTenderDetails", startTime);
					if (tenderResDTO.getQtDetail() != null) {
						scQt = tenderResDTO.getQtDetail();
						getManagedBean().setQt(scQt);
						this.getManagedBean().setPanel(scQt.isPanel());
						Long currentStatus = this.getManagedBean().getQt()
								.getQtStatus().getStatusId();
						this.getManagedBean().setCurrentStatusId(currentStatus);
						getManagedBean().getCtContract().setContractNo(
								this.getManagedBean().getQt()
										.getExistingContractNo());
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
						
						if (scQt.getEvaluationType() != null && scQt.getEvaluationType().equalsIgnoreCase(ParameterConst.EVALUATION_TYPES_TWO_TIER)) {
							Long status = this.getManagedBean().getQt().getQtStatus() !=null? 
									this.getManagedBean().getQt().getQtStatus().getStatusId(): null;
							if(null != status && (!status.equals(QTStatusConst.QT_PENDING_QT_SUBMISSION))) {
								this.getManagedBean().setDemoType(true);
							}
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
				}
			} finally {
				QtDetailsSO = null;
			}
		}
	}
	//#28784 - end

	//#29474 - start
	/**
	 * Retrieve fulfilment dtls.retrieve all the FulfilMentDetails menu
	 * information and display in Specification committee.
	 */
	private void retriveFulfilmentDtls() {
		IQTSpecSO specSo = null;

		try {

			SpecificationReqDTO reqDTO = new SpecificationReqDTO();
			specSo = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
			reqDTO.setQtId(this.getManagedBean().getQt().getQtId());
			SpecificationResDTO resDTO = specSo
					.retrieveFulFilMentScheduleDtls(reqDTO);

			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();

			List<ScQtProgressivePayment> paymentList = this.getManagedBean()
					.getProgressivePaymentList();

			if (null == scheduleList) {
				scheduleList = new ArrayList<ScQtSchedule>();
				if (null != resDTO.getScheduleResults()
						&& null != resDTO.getScheduleResults().getResultList()
						&& !resDTO.getScheduleResults().getResultList()
								.isEmpty()) {

					this.getManagedBean().setTotalFrequency(
							resDTO.getScheduleResults().getResultList().size());
					try {

						for (ScQtSchedule scQtSchedule : resDTO
								.getScheduleResults().getResultList()) {
							String startMonth = "";
							String endMonth = "";
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									CommonConstants.DEFAULT_DATE_FORMAT);
							int months;
							Date startDate = null;
							Date endDate = null;
							String fromdate = dateFormat.format(scQtSchedule
									.getFromDate());
							String todate = dateFormat.format(scQtSchedule
									.getToDate());
							String startdate = fromdate.substring(3, 5);
							String startYear = fromdate.substring(6,
									fromdate.length());
							String fromDay = fromdate.substring(0, 2);

							String enddate = todate.substring(3, 5);
							String endYear = todate.substring(6,
									todate.length());
							String toDay = todate.substring(0, 2);

							startMonth = fulfilmentMonthlyDtls(startdate);
							endMonth = fulfilmentMonthlyDtls(enddate);

							int sYear = Integer.parseInt(startYear);
							int eYear = Integer.parseInt(endYear);

							String fromDate = fromDay + "/" + startdate + "/"
									+ sYear;
							String toDate = toDay + "/" + enddate + "/" + eYear;

							startDate = dateFormat.parse(fromDate);
							endDate = dateFormat.parse(toDate);

							months = DateUtils.getMonths(startDate, endDate);
							scQtSchedule.setTotalMonths(new BigDecimal(months));
							// scQtSchedule.setTotalMonths(new
							// BigDecimal(months));

							scQtSchedule.setStartMonth(startMonth);
							scQtSchedule.setEndMonth(endMonth);
							scQtSchedule.setStartYear(startYear);
							scQtSchedule.setEndYear(endYear);

							scheduleList.add(scQtSchedule);

						}
					} catch (ParseException e) {

					}
				}
				this.getManagedBean().setScheduleList(scheduleList);
			}

			// this.getManagedBean().setProgressivePaymentList(resDTO.getProgressivePaymentResults().getResultList());
			if (null == paymentList) {
				paymentList = new ArrayList<ScQtProgressivePayment>();
				if (null != resDTO.getProgressivePaymentResults()
						&& null != resDTO.getProgressivePaymentResults()
								.getResultList()
						&& !resDTO.getProgressivePaymentResults()
								.getResultList().isEmpty()) {

					this.getManagedBean().setTotalProgressivePayment(
							resDTO.getProgressivePaymentResults()
									.getResultList().size());
					String startMonth = "";
					String endMonth = "";
					for (ScQtProgressivePayment payment : resDTO
							.getProgressivePaymentResults().getResultList()) {
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"dd/MM/yyyy");

						String fromdate = dateFormat.format(payment
								.getFromDate());
						String todate = dateFormat.format(payment.getToDate());
						String startdate = fromdate.substring(3, 5);
						String startYear = fromdate.substring(6,
								fromdate.length());
						String enddate = todate.substring(3, 5);
						String endYear = todate.substring(6, todate.length());
						startMonth = fulfilmentMonthlyDtls(startdate);
						endMonth = fulfilmentMonthlyDtls(enddate);
						payment.setStartMonth(startMonth);
						payment.setEndMonth(endMonth);
						payment.setStartYear(startYear);
						payment.setEndYear(endYear);
						paymentList.add(payment);

					}

				}
				this.getManagedBean().setProgressivePaymentList(paymentList);
			}

		} finally {
			specSo = null;
		}

	}
	
	private String fulfilmentMonthlyDtls(String startdate) {
		String startMonth = "";
		try {
			if (startdate.equalsIgnoreCase("01")) {
				startMonth = "0";
			} else if (startdate.equalsIgnoreCase("02")) {
				startMonth = "1";
			} else if (startdate.equalsIgnoreCase("03")) {
				startMonth = "2";
			} else if (startdate.equalsIgnoreCase("04")) {
				startMonth = "3";
			} else if (startdate.equalsIgnoreCase("05")) {
				startMonth = "4";
			} else if (startdate.equalsIgnoreCase("06")) {
				startMonth = "5";
			}

			else if (startdate.equalsIgnoreCase("07")) {
				startMonth = "6";
			} else if (startdate.equalsIgnoreCase("08")) {
				startMonth = "7";
			} else if (startdate.equalsIgnoreCase("09")) {
				startMonth = "8";
			} else if (startdate.equalsIgnoreCase("10")) {
				startMonth = "9";
			} else if (startdate.equalsIgnoreCase("11")) {
				startMonth = "10";
			} else if (startdate.equalsIgnoreCase("12")) {
				startMonth = "11";
			}

		} finally {

		}
		return startMonth;
	}
	
	private String retriveMonthDtls(int Start) {
		String month = "";
		if (Start == 0) {
			month = "01";
		} else if (Start == 1) {
			month = "02";
		} else if (Start == 2) {
			month = "03";
		} else if (Start == 3) {
			month = "04";
		} else if (Start == 4) {
			month = "05";
		} else if (Start == 5) {
			month = "06";
		} else if (Start == 6) {
			month = "07";
		} else if (Start == 7) {
			month = "08";
		} else if (Start == 8) {
			month = "09";
		} else if (Start == 9) {
			month = "10";
		} else if (Start == 10) {
			month = "11";
		} else if (Start == 11) {
			month = "12";
		} else if (Start == 12) {
			month = "01";
		} else if (Start == 13) {
			month = "02";
		}
		return month;
	}
	
	private void getfulfilmentDtls(ScQt scQt) {
		if (null != scQt.getFulfilmentType()) {
			this.getManagedBean().setFulfilmentType(
					scQt.getFulfilmentType().getParameterCode());
		} else {
			scQt.setFulfilmentType(new PmParameter());
		}

	}
	
	public void retriveSpecdetails(Long qtId) {

		QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
		qtBackingBean.retrieveQuationTenderDetails(qtId);
	}
	
	/**
	 * Calculate schedule percentage.calculate months difference in Frequency
	 * schedule.
	 * 
	 * @param schedule
	 *            (ScQtSchedule schedule).
	 */
	public void calculateSchedulePercentage(ScQtSchedule schedule,
			Boolean startMonthAndYearFlag) throws ParseException {
		/*
		 * String endMonth = (String) event.getComponent().getAttributes()
		 * .get("value");
		 */
		// this.getManagedBean().setScQt(this.getManagedBean().getQt());
		List<ScQtSchedule> freqSchedule = this.getManagedBean()
				.getScheduleList();
		if (null == freqSchedule) {
			freqSchedule = new ArrayList<ScQtSchedule>();

		}
		Integer startMonth = 0, endMonth = 0, startYear = 0, endYear = 0, months = 0;
		startMonth = schedule.getStartMonth() != null ? Integer
				.parseInt(schedule.getStartMonth()) : null;
		endMonth = schedule.getEndMonth() != null ? Integer.parseInt(schedule
				.getEndMonth()) : null;
		startYear = schedule.getStartYear() != null ? Integer.parseInt(schedule
				.getStartYear()) : null;
		endYear = schedule.getEndYear() != null ? Integer.parseInt(schedule
				.getEndYear()) : null;

		if (null != startMonthAndYearFlag && startMonthAndYearFlag
				&& startMonth != null) {
			endMonth = startMonth;
			endYear = startYear;
			schedule.setEndMonth(endMonth.toString());
			if (endYear != null) {
				schedule.setEndYear(endYear.toString());
			}
		}

		if (startMonth != null && endMonth != null && startYear != null
				&& endYear != null) {
			// Date currentDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			// String localDate = sdf.format(currentDate);
			// String day = localDate.substring(0, 2);
			String localstartMonth = retriveMonthDtls(new Integer(startMonth));
			String localemdMonth = retriveMonthDtls(new Integer(endMonth));
			String fromDate = 01 + "/" + localstartMonth + "/" + startYear;
			String toDate = 01 + "/" + localemdMonth + "/" + endYear;
			Date startDate = sdf.parse(fromDate);
			Date endDate = sdf.parse(toDate);

			months = DateUtils.getMonths(startDate, endDate);
			schedule.setTotalMonths(new BigDecimal(months));
		} else {
			schedule.setTotalMonths(BigDecimal.ZERO);
		}

		int index = freqSchedule.indexOf(schedule);
		freqSchedule.set(index, schedule);

	}
	
	public void deleteFreqScheduleDetails() {
		SpecificationResDTO resDTO = null;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setToBeRemoveSchedulesList(this.getManagedBean()
				.getScheduleList());
		reqDTO.setQtId(this.getManagedBean().getQt().getQtId());

		try {
			if (null != getManagedBean().getScheduleList()) {

				resDTO = getQtSpecSO().deleteQtScheduleIdAndPaymentId(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);

				}
				getManagedBean().setScheduleList(resDTO.getToBeRemoveSchedulesList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}
	
	private IQTSpecSO getQtSpecSO() {
		return BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTSpecSO.class);
	}
	
	public void deleteProgPaymentDetails() {
		SpecificationResDTO resDTO = null;
		SpecificationReqDTO reqDTO = new SpecificationReqDTO();
		reqDTO.setToBeRemoveProgressivePayments(getManagedBean()
				.getProgressivePaymentList());
		reqDTO.setQtId(getManagedBean().getQt().getQtId());
		try {
			if (null != getManagedBean().getProgressivePaymentList()) {
				
				resDTO = getQtSpecSO().deleteQtScheduleIdAndPaymentId(reqDTO);

				if (resDTO.isSuccessful()) {
					getManagedBean().setSelectAll(false);
					getManagedBean().setButtonHideSchActive(true);
					
				}
				getManagedBean().setProgressivePaymentList(resDTO.getToBeRemoveProgressivePayments());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
	}
	
	/**
	 * Adds the schedule details.add empty rows for frequency schedule
	 * datatable.
	 */
	public void addScheduleDetails() {
		IParameterSO parameterSo = null;
		try {
			
			if(this.getManagedBean()
					.getScheduleList()==null){
				this.getManagedBean().setScheduleList(new ArrayList<ScQtSchedule>());
			}
			List<ScQtSchedule> scheduleList = this.getManagedBean()
					.getScheduleList();
					parameterSo = BusinessDelegate.getInstance()
							.getServiceObject(
									ModuleCodeConst.PROFILE_MANAGEMENT,
									IParameterSO.class);
					ParameterReqDTO docDefTypeReqDTO = new ParameterReqDTO();
					docDefTypeReqDTO
							.setParameterType(ParameterTypeConst.SCHEDULE_TYPE);
					docDefTypeReqDTO
							.setParameterCode(ParameterConst.SCHEDULE_TYPE_PERIODIC);
					ParameterResDTO docDefTypeResDTO = parameterSo
							.retrieveParameterByTypeAndCode(docDefTypeReqDTO);
					
					ScQtSchedule schedule = new ScQtSchedule();
					schedule.setScQt(this.getManagedBean().getQt());
					schedule.setScheduleType(docDefTypeResDTO.getPmParameter());
				scheduleList.add(schedule);

			this.getManagedBean().setScheduleList(scheduleList);
		}

		finally {
			parameterSo = null;
		}
	}
	
	/**
	 * Adds the progressive details.add empty rows for progressive payment
	 * datatable.
	 */
	public void addProgressiveDetails() {

		try {
			if (this.getManagedBean().getProgressivePaymentList() == null) {
				this.getManagedBean().setProgressivePaymentList(
						new ArrayList<ScQtProgressivePayment>());
			}
			List<ScQtProgressivePayment> payments = this.getManagedBean()
					.getProgressivePaymentList();

			ScQtProgressivePayment payment = new ScQtProgressivePayment();

			setUserDetails(payment);
			payment.setScQt(this.getManagedBean().getQt());
			payments.add(payment);
			this.getManagedBean()
					.getQt()
					.setScheduleType(
							ParameterConst.PERIODIC_SCHEDULE_TYPE_PROGRESSIVE_PAYMENT);

			this.getManagedBean().setProgressivePaymentList(payments);

		} finally {

		}
	}
	
	/**
	 * @param e
	 */
	public void selectAllSchedule(AjaxBehaviorEvent e) {

		for (ScQtSchedule ScQtSchedule : getManagedBean().getScheduleList()
				) {
			if (getManagedBean().isSelectAll()) {
				ScQtSchedule.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtSchedule.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getProgressivePaymentList()) {
			if (getManagedBean().isSelectAll()) {
				ScQtProgressivePayment.setSelected(true);
				if (getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(false);
				}
			} else {
				ScQtProgressivePayment.setSelected(false);
				if (!getManagedBean().isButtonHideSchActive()) {
					getManagedBean().setButtonHideSchActive(true);
				}
			}
		}
	}
	
	public void selectOneScheCheckboxSelect(AjaxBehaviorEvent e) {
		getManagedBean().setSelectAll(false);
		for (ScQtSchedule ScQtSchedule : getManagedBean().getScheduleList()) {
			if (ScQtSchedule.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		for (ScQtProgressivePayment ScQtProgressivePayment : getManagedBean()
				.getProgressivePaymentList()) {
			if (ScQtProgressivePayment.isSelected()) {
				getManagedBean().setButtonHideSchActive(false);
				return;
			}
		}
		if ((boolean) e.getComponent().getAttributes().get("value")) {
			getManagedBean().setButtonHideSchActive(false);
		} else {
			getManagedBean().setButtonHideSchActive(true);
		}
	}
	
	/**
	 * Calculate progressive start month.change start month in progessive
	 * payment.
	 * 
	 * @param progressivepayment
	 *            (ScQtProgressivePayment progressivepayment).
	 */
	public void calculateProgressiveStartMonth(
			ScQtProgressivePayment progressivepayment) {
		// event.getComponent().getAttributes().get("value");
		List<ScQtProgressivePayment> payment = this.getManagedBean()
				.getProgressivePaymentList();
		if (null == payment) {
			payment = new ArrayList<ScQtProgressivePayment>();

		}
		if (null != progressivepayment
				&& null != progressivepayment.getStartMonth()) {
			int index = payment.indexOf(progressivepayment);
			if (null == progressivepayment.getEndMonth()) {
				progressivepayment.setEndMonth(progressivepayment
						.getStartMonth());
			}
			payment.set(index, progressivepayment);

			this.getManagedBean().setProgressivePaymentList(payment);
		}

	}
	//#29474 - end
}