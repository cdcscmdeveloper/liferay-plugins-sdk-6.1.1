/**
 * 
 */
package my.ep.web.backingbean.sc.quatationtender.deskofficermaintenance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.criteria.JoinType;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.general.settings.bpm.ICompositeValiditySO;
import my.ep.biz.service.sc.actor.IScActorSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTSuppFinalizationSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.general.settings.bpm.CompositeValidityReqDTO;
import my.ep.dto.sc.actor.ScActorReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SuppFinalizationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.general.settings.bpm.PmCompositeValidity;
import my.ep.persistence.entity.sc.common.ScActor;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sc.quatationtender.DeskOfficerMaintenanceSearchBean;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.deskofficermaintenance.DeskOfficerMaintenanceManagedBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 * @author Mallareddy Belageri
 *
 */
@ManagedBean(name = "DOMaintenanceBackingBean")
@ViewScoped
public class DeskOfficerMaintenanceBackingBean extends SourcingBaseBackingBean<DeskOfficerMaintenanceManagedBean> {

	private static final long serialVersionUID = 1L;
	
	private static final Logger log = EpLogger.getLogger(DeskOfficerMaintenanceBackingBean.class);
	
	private PagingDataModel<PmUser> userListDataModel = new UserPagingDataModel();
	
	/**
	 * Initialize default values.
	 */
	@PostConstruct
	private void init() {
		DeskOfficerMaintenanceSearchBean SearchBean = getManagedBean().getDoMaintenanceSearchBean();
		if (null != SearchBean && (null == SearchBean.getMinistryProfile() 
				|| null == SearchBean.getPtjGroupProfile()
				|| null == SearchBean.getPtjProfile())) {
			getDefaultPtjAndMinistryValues();
		}
	}
	private String textBeforeSign;

	public String getTextBeforeSign() {
		return textBeforeSign;
	}
	
	public void setTextBeforeSign(String textBeforeSign) {
		this.textBeforeSign = textBeforeSign;
	}

	
	/**
	 * Gets the default ptj and ministry search values based on user role.
	 */
	private void getDefaultPtjAndMinistryValues() {

		debugLogger("getDefaultPtjAndMinistryValues");
		PmOrgProfile ministryProfile = new PmOrgProfile();
		PmOrgProfile ptjGroupProfile = new PmOrgProfile();
		PmOrgProfile ptjProfile = new PmOrgProfile();
		DeskOfficerMaintenanceSearchBean SearchBean = getManagedBean().getDoMaintenanceSearchBean();
		
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
		
		SearchBean.setMinistryProfile(ministryProfile);
		SearchBean.setPtjGroupProfile(ptjGroupProfile);
		SearchBean.setPtjProfile(ptjProfile);
	
		if(isCommitteeApprover()){
			if(this.getManagedBean().getTaskName()==null){
				this.getManagedBean().setOriginalDeskOfficer(null);
			}
		}else if(isDeskOfficer()){
			this.getManagedBean().setOriginalDeskOfficer(this.getFacesScopes().getUserSessionBean());
		}
		SearchBean.setOriginalDeskOfficer(isCommitteeApprover() ? null : this.getManagedBean().getOriginalDeskOfficer().getUserName());
	}
	
	/**
	 * @return
	 */
	private PmOrgProfile getOrgProfileByLoginUser() {
		PmOrgProfile pmOrgProfile = null;
		Long userId = new FacesScopes().getUserSessionBean().getUserId();
		
		if (userId != null) {
			IUserSO userSO = null;
			try {
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setUserId(userId);
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
	 * Gets the doc type qt loa dropdown values.
	 *
	 * @return the doc type qt loa dropdown values
	 */
	public SelectItem[] getDocTypeQtLOADropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> qtLoaDoctypes = new ArrayList<String>();
		
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_BIDDING);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
		qtLoaDoctypes.add(ParameterConst.DOCTYPE_QUOTATION);
		
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), qtLoaDoctypes));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.DOCUMENT_TYPES, searchBean);
		return items;
	}
	
	/**
	 * @param moduleCode
	 * @param docType
	 * @param screenName
	 * @return
	 */
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
		} else {
			statusList = getStatusDropdownValues(moduleCode, docType);
		}
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
		return statusList;
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
			DeskOfficerMaintenanceSearchBean searchBean = getManagedBean().getDoMaintenanceSearchBean();
			
			if (ParameterConst.ORG_TYPE_MINISTRY.equals(orgType)) {
				PmOrgProfile ministryOrgProfile = new PmOrgProfile();
				PmParameter orgTypeCode = new PmParameter();
				orgTypeCode.setParameterCode(ParameterConst.ORG_TYPE_PTJ_GROUP);
				ministryOrgProfile.setOrgTypeCode(orgTypeCode);
				searchBean.setPtjGroupProfile(ministryOrgProfile);
				
				postUpdateOrgProfileListener(ministryOrgProfile);
				
			} else { 
				if (searchBean.getMinistryProfile() == null || 
						searchBean.getMinistryProfile().getOrgProfileId() == null) {
					searchBean.setMinistryProfile(retrieveMinistryOrgProfile(
							pmOrgProfile.getOrgProfileId()));	
				}
				
				if (ParameterConst.ORG_TYPE_PTJ_GROUP.equals(orgType)) {
					searchBean.setPtjProfile(new PmOrgProfile());
					
				} else if (ParameterConst.ORG_TYPE_PTJ.equals(orgType)) {
					if (searchBean.getPtjGroupProfile() == null || 
							searchBean.getPtjGroupProfile().getOrgProfileId() == null) {
						searchBean.setPtjGroupProfile(retrieveJabatanOrgProfile(
								pmOrgProfile.getOrgProfileId()));	
					}
				}
			}
				
		}
	}
	
	/**
	 * Gets the procurement mode dropdown values.
	 * 
	 * @return the procurement mode dropdown values
	 */
	public SelectItem[] getProcurementModeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		SelectItem[] items = getDropdownCodeValuesBySearchBean(
				ParameterTypeConst.SC_PROCUREMENT_MODE, searchBean);
		return items;
	}

	public Boolean getManagedSearchBeanDocumentTypeIsLOI(){
		if(!StringUtils.isEmpty(getManagedBean().getDoMaintenanceSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(getManagedBean().getDoMaintenanceSearchBean().getDocType())
			) return true;
		return false;
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOA(){
		if(!StringUtils.isEmpty(getManagedBean().getDoMaintenanceSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(getManagedBean().getDoMaintenanceSearchBean().getDocType())
			) return true;
		return false;
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsSuppFinal(){
		if(!StringUtils.isEmpty(getManagedBean().getDoMaintenanceSearchBean().getDocType())
				&& ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equalsIgnoreCase(getManagedBean().getDoMaintenanceSearchBean().getDocType())
				) return true;
			return false;
	}
	/**
	 * Get PTJ Group condition for construct the LOV listing
	 * 
	 * @return String
	 */
	public String getPTJGroupCondition() {
		debugLogger("getPTJGroupCondition");
		StringBuffer condition = new StringBuffer("recordStatus" + SourcingConstants.LOV_CONDITION_SEPARATOR + DbValueConst.RECORD_STATUS_ACTIVE);
		Long MinistryOrgProfileId = this.getManagedBean().getDoMaintenanceSearchBean().getMinistryProfile()
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
		QtnTenderSearchBean qtnTenderSearchBean = getManagedBean().getDoMaintenanceSearchBean();
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
	
	public void searchQTAction() {
		// #25627 - start.
		this.getManagedBean().setDisableAssignTo(true);
		this.getManagedBean().setDisableSubmit(true);
		// #25627 - end.
		this.getManagedBean().setSelectedQtIds(new ArrayList<Long>());
		this.getManagedBean().setQtNoDeskOfficerMap(null);
		getManagedBean().getSearchQTDataModel().reloadData();
	}
	
	/**
	 * Reset.
	 */
	public void reset() {
		// #25627 - start.
		this.getManagedBean().setDisableAssignTo(true);
		getManagedBean().setDisableSubmit(true);
		// #25627 - end.
		this.getManagedBean().getDoMaintenanceSearchBean().setDocType(ParameterConst.DOCTYPE_QUOTATION);
		this.getManagedBean().getDoMaintenanceSearchBean().setDocNo(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setQtTitle(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setQtNo(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setProcurementMode(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setFromCreatedDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setToCreatedDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setFromPublishDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setToPublishDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setFromClosingDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setToClosingDate(null);
		this.getManagedBean().getDoMaintenanceSearchBean().setQtStatus(null);
		this.getManagedBean().setSelectedQtIds(new ArrayList<Long>());
		this.getManagedBean().setQtNoDeskOfficerMap(null);
		if(this.getManagedBean().getSearchQTDataModel()!=null){
			this.getManagedBean().setSearchQTDataModel(null);
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
					.getDoMaintenanceSearchBean());
			getManagedBean().setSearchQTDataModel(searchQTDataModel);
		}
		return searchQTDataModel;
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
			// #25627 - Start.
			//getManagedBean().setSelectedQtIds(new ArrayList<Long>());
			//getManagedBean().setQtNoDeskOfficerMap(null);
			IQTDetailSO detailSO;
			IQTSuppFinalizationSO sfDetailSO;
			try {
				/*if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedQtIds())) {
					getManagedBean().getSelectedQtIds().clear(); 
				}*/
				//getManagedBean().setDisableAssignTo(true);
				//getManagedBean().setDisableSubmit(true);
				// #25627 - End.
				
				detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				sfDetailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSuppFinalizationSO.class);
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtnTenderSearchBean(qtnTenderSearchBean.toQTSearchBean());
				populateQTSearchCriteria(reqDTO, qtnTenderSearchBean, searchBean);
				reqDTO.setDoMaintenanceFlow(Boolean.TRUE);//assumption: only do maintenance portlet used this backingbean
				if(isCommitteeApprover()){
					reqDTO.setCAApproverScreen(Boolean.TRUE);
					reqDTO.setCAApprover(getFacesScopes().getUserSessionBean());
				}
				QuationTenderResDTO resDTO = detailSO.retriveQuationTenderDetailsBySearchCriteria(reqDTO);
				
				if(ParameterConst.DOCTYPE_SUPPLIER_FINALIZATION.equals(qtnTenderSearchBean.getDocType())){
					if(resDTO.isSuccessful() && resDTO.getQtResultBean() != null && !CollectionUtils.isEmpty(resDTO.getQtResultBean().getResultList())){
						SearchResultBean<ScQt> qtResultBean = (SearchResultBean<ScQt>)resDTO.getQtResultBean();
						SuppFinalizationResDTO sfResDTO = sfDetailSO.setQtFinalizationCurrentStatus(qtResultBean.getResultList());
						if(sfResDTO.isSuccessful()){
							qtResultBean.setResultList(sfResDTO.getQtList());
						}
					}
				}
				
				if(!isCommitteeApprover() && isDeskOfficer()){
					IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
							.getServiceObject(IBpmSO.class);
					
					ICompositeValiditySO compositeSO = (ICompositeValiditySO)  BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, ICompositeValiditySO.class);
					CompositeValidityReqDTO compositeReqDTO = new CompositeValidityReqDTO();
					compositeReqDTO.setCompositeValidityId(new Long(25627L));
					PmCompositeValidity composite =  compositeSO.getCompositeByValidityId(compositeReqDTO).getPmCompositeValidity();
					String doReassignCompositeVersion = composite.getMinCompositeVersion();
					
                    // #25627 - Start.
					if(CollectionUtils.isNotEmpty(resDTO.getQtResultBean().getResultList())){
                      List<String> docNoList = null;
                      
						for(ScQt qt :  resDTO.getQtResultBean().getResultList()){
                          if (null == docNoList) {
                            docNoList = new ArrayList<>();
                          }
                          docNoList.add(qt.getQtNo());
                      }
					  BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
                      bpmReqDTO.setDocNumberList(docNoList);
                      bpmReqDTO.setLimit(1000);
                      BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumberList(bpmReqDTO);

                      Map<String, WorklistItemVO> docNoWorkItemMap = null;
					  if(bpmResDTO != null && CollectionUtils.isNotEmpty(bpmResDTO.getWorklistVO().getWorklistItem())){
						 for(WorklistItemVO currentVO: bpmResDTO.getWorklistVO().getWorklistItem()){
                           if (null == docNoWorkItemMap) {
                             docNoWorkItemMap = new HashMap<String, WorklistItemVO>();
						   }
                           
                           if(StringUtils.isNotBlank(currentVO.getInstanceVersion()) && docNoWorkItemMap.get(currentVO.getDocNumber().trim()) == null){
                             docNoWorkItemMap.put(currentVO.getDocNumber().trim(), currentVO);
						   }
                         }
                       }

                       if (null != docNoWorkItemMap && docNoWorkItemMap.size() > 0) {
                         for(ScQt qt :  resDTO.getQtResultBean().getResultList()){
                           qt.setSelectable(false);
                           WorklistItemVO currentVO = docNoWorkItemMap.get(qt.getQtNo());
                           if (null != currentVO) {
                             if(StringUtils.isNotBlank(currentVO.getInstanceVersion()) && compositeVersionComparator(currentVO.getInstanceVersion(), doReassignCompositeVersion)>=0){
									qt.setSelectable(true);
								 }
							 }
						}
                       } else {
                         for(ScQt qt :  resDTO.getQtResultBean().getResultList()){
                           qt.setSelectable(false);
                         }
                       }
					}
                    // #25627 -End.
                }
			qtnTenderSearchBean.setResultDocType(qtnTenderSearchBean.getDocType());
			// #25627 - Start.
			if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedQtIds()) 
					&& getManagedBean().getSelectedQtIds().size() > 0) {
				if (CollectionUtils.isNotEmpty(resDTO.getQtResultBean().getResultList())
						&& resDTO.getQtResultBean().getResultList().size() > 0) {
					for (ScQt scqt : resDTO.getQtResultBean().getResultList()) {
						if (getManagedBean().getSelectedQtIds().toString().contains(String.valueOf(scqt.getQtId()))) {
							scqt.setSelected(true);
						}
					}
				}
			}
			// #25627 - End.
			return resDTO.getQtResultBean();
			}catch(Exception e){
				handleException(e);
			}			
			finally {
				detailSO = null;
				sfDetailSO = null;
			}
			return null;
		}
	}
	
	/**
	 * Method to prepare qt search criteria.
	 * 
	 * @param qtnTenderSearchBean
	 *            the qt search criteria bean
	 * @param qtSearchBean
	 *            the qt search filter bean
	 */
	private void populateQTSearchCriteria(QuationTenderReqDTO reqDTO,
			QtnTenderSearchBean qtnTenderSearchBean,
			ISearchBean<ScQt> qtSearchBean) {
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
			//Default filter start------------------------------------------
			//show only normal flow QT
			searchFilterBean.add(
					new SearchFilterBean(QTConstants.QT_NUMBER,
							SearchOperator.OPERATOR_LIKE.value(), "QT",
							true));
			//Default filter end---------------------------------------------------------------

			// docType & docNo
			String docType = qtnTenderSearchBean.getDocType();
			String docNo = qtnTenderSearchBean.getDocNo();
			if (StringUtils.isNotEmpty(docType)) {
				reqDTO.setDocType(docType);
				if (ParameterConst.DOCTYPE_QUOTATION.equals(docType) 
						|| ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)
						|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {

					if (!StringUtils.isBlank(docNo)) {
						SearchFilterBean bean = new SearchFilterBean();
						bean.setSearchField(QTConstants.QT_NUMBER);
						bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
						bean.setSearchValue(docNo);
						searchFilterBean.add(bean);
					} 
				} else {
					reqDTO.setDocNo(docNo);
				}
			}
			String procurementMode = qtnTenderSearchBean.getProcurementMode();
			if (!StringUtils.isBlank(procurementMode)) {
				searchFilterBean.add(
						new SearchFilterBean("procurementMode.parameterCode",
								SearchOperator.OPERATOR_EQUALS.value(),
								procurementMode));
			} else {
				// Filter By - procurementMode
				List<String> procurementModes = new ArrayList<String>();
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_QUOTATION);
				procurementModes.add(ParameterConst.PROCUREMENT_MODE_TENDER);
				searchFilterBean.add(
						new SearchFilterBean(
								"procurementMode.parameterCode",
								SearchOperator.OPERATOR_IN.value(), 
								procurementModes));
			}
			
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
				if(qtnTenderSearchBean.isQtStatusForQt()) {			
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
			}
			
			if(this.getManagedBean().getOriginalDeskOfficer()!=null){
				reqDTO.setDeskOfficer(this.getManagedBean().getOriginalDeskOfficer());		
			}
			
			PmOrgProfile ptjProfiles = qtnTenderSearchBean.getPtjProfile();
			if (ptjProfiles != null && ptjProfiles.getOrgProfileId() != null) {
				// Otherwise (Audit Search), user can view QT based on user roles match with actor.
				// Filter By - PTJ
				searchFilterBean.add(new SearchFilterBean(
						"pmOrgProfile." + OrgProfileUtil
							.getSearchBeanParentString(OrganizationType.DIVISION.value()),
						SearchOperator.OPERATOR_EQUALS.value(),
						ptjProfiles.getOrgProfileId()));
			} else {
				// Filter By - PTJ Group
				PmOrgProfile ptjGroupProfile = qtnTenderSearchBean.getPtjGroupProfile();
				if (ptjGroupProfile != null && ptjGroupProfile.getOrgProfileId() != null) {
					searchFilterBean.add(new SearchFilterBean(
							"pmOrgProfile." + OrgProfileUtil
								.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
							SearchOperator.OPERATOR_EQUALS.value(),
							ptjGroupProfile.getOrgProfileId()));
				} else {
					// Filter By - Ministry
					PmOrgProfile ministryProfile = qtnTenderSearchBean.getMinistryProfile();
					if (ministryProfile != null
							&& ministryProfile.getOrgProfileId() != null) {	
						searchFilterBean.add(new SearchFilterBean(
								"pmOrgProfile." + OrgProfileUtil
									.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
								SearchOperator.OPERATOR_EQUALS.value(),
								ministryProfile.getOrgProfileId()));
					}
				}
			}
			qtSearchBean.getDataFilterBeans().addAll(searchFilterBean);
			reqDTO.setQtSearchBean(qtSearchBean);
		}
	}

	public boolean isDeskOfficer() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.DESK_OFFICER.value());
	}
	public boolean isCommitteeApprover() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.COMMITTEE_APPROVER.value());
	}
	
	public void selectedQtId(Long qtId) {
		this.getManagedBean().setSelectedUser(null);
		getManagedBean().setDisableAssign(true);
		if (null == getManagedBean().getSelectedQtIds()) {
			getManagedBean().setSelectedQtIds(new ArrayList<Long>());
		}
		if (getManagedBean().getSelectedQtIds().contains(qtId)) {
			getManagedBean().getSelectedQtIds().remove(qtId);
			if(getManagedBean().getQtNoDeskOfficerMap()!=null){
				if(getManagedBean().getQtNoDeskOfficerMap().get(qtId)!=null){
					getManagedBean().getQtNoDeskOfficerMap().remove(qtId);
				}
			}
		} else {
			getManagedBean().getSelectedQtIds().add(qtId);
		}
		
		if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedQtIds())) {
			getManagedBean().setDisableAssignTo(false);
			if (null != getManagedBean().getQtNoDeskOfficerMap()) {
				boolean disableSubmit = false;
				for (Long qtIde : getManagedBean().getSelectedQtIds()) {
					PmUser selectedUser = getManagedBean().getQtNoDeskOfficerMap().get(qtIde);
					if(selectedUser==null){
						disableSubmit = true;
						break;
					}
				}
				getManagedBean().setDisableSubmit(disableSubmit);
			}
		} else {
			getManagedBean().setDisableAssignTo(true);
			getManagedBean().setDisableSubmit(true);
		}

	}
	
	/**
	 * Redirect to Task Re-assignment Details
	 * 
	 * @param item
	 *            WorklistItemVO
	 * @return xhtml
	 */
	public void redirectToTaskReassign() {
		log.info("redirectToTaskReassign - Start");
		// #25627 - start.
		resetUsers();
		// #25627 - end.
		getUserListDataModel();
		RequestContext.getCurrentInstance()
				.execute("dlTaskReassignment.show()");
		log.info("redirectToTaskReassign - End");
	}
	
	/**
	 * get faces scopes
	 * 
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * Available user data model (to confirm which user to be assignee)
	 * 
	 * @author Ken
	 *
	 */
	private class UserPagingDataModel extends PagingDataModel<PmUser> {

		private static final long serialVersionUID = 1L;

		public UserPagingDataModel() {
			super("dtTaskReassign");
		}

		@Override
		protected ISearchResultBean<PmUser> pageChangeListener(
				ISearchBean<PmUser> searchBean) {
			log.info("generateAvailableUsers");
			try {
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.pmRole.roleCode",
								SearchOperator.OPERATOR_EQUALS.value(), RoleCode.DESK_OFFICER));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmUserRoles.recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean("recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.recordStatus",
								SearchOperator.OPERATOR_EQUALS.value(), DbValueConst.RECORD_STATUS_ACTIVE));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"pmUserOrgs.pmOrgProfile.orgProfileId",
								SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getDoMaintenanceSearchBean().getPtjProfile().getOrgProfileId()));
				searchBean.getDataFilterBeans().add(
						new SearchFilterBean(
								"userId",
								SearchOperator.OPERATOR_NOT_EQUALS.value(), getFacesScopes().getUserSessionBean().getUserId()));
	
				if(StringUtils.isNotBlank(getManagedBean().getSearchUserIdNo())){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"identificationNo",
									SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getSearchUserIdNo().trim()));
				}
				if(StringUtils.isNotBlank(getManagedBean().getSearchUserName())){
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean(
									"userName",
									SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getSearchUserName().trim(), true));
				}

				searchBean.getSortColumns().add(new SearchOrderBean("userName"));
				return PmCommonUtil.searchUsersResultBeanBySearchBean(searchBean);			
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
			} finally {
			}
			
			return null;
		}
	}
	
	/**
	 * To search available user to be assignee
	 */
	public void searchUser(){
		userListDataModel.reloadData();
	}
	
	/**
	 * 
	 */
	public void resetUsers() {
		getManagedBean().setSearchUserName(null);
		getManagedBean().setSearchUserIdNo(null);
		userListDataModel.reloadData();
	}

		
	public boolean isValidReassignment(Long qtId, PmUser user){//check if the new DO is currently committe approver or same as original approver
		boolean valid = true;
		IScActorSO scActorSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);
		ScActorReqDTO reqDTO  = new ScActorReqDTO();
		List<String> docTypes = new ArrayList<String>();
		docTypes.add(ParameterConst.DOCTYPE_QUOTATION);
		reqDTO.setDocTypes(docTypes);
		reqDTO.setDocId(qtId);
		reqDTO.setUserId(user.getUserId());
		List<ScActor> actorList = scActorSO.retreiveActorByDocTypesDocIdUserId(reqDTO).getScActorList();
		if(CollectionUtils.isEmpty(actorList)){
			actorList = new ArrayList<ScActor>();
		}
		String[] rolesToCheck = {RoleCode.DESK_OFFICER.value(), RoleCode.COMMITTEE_APPROVER.value()};
		for(ScActor actor : actorList){
			if(Arrays.asList(rolesToCheck).contains(actor.getRoleCode())){
				valid = false;
				break;
			}
		}
		return valid;
	}
	public void assignUser() {
		PmUser selectedUser = getManagedBean().getSelectedUser();
		if (getManagedBean().getQtNoDeskOfficerMap() == null) {
			Map<Long, PmUser> qtNoDeskOfficerMap = new LinkedHashMap<Long, PmUser>();
			getManagedBean().setQtNoDeskOfficerMap(qtNoDeskOfficerMap );
		}

		// #25627 - Start.
		boolean showError = false;
		boolean disableSubmit = false;

		if (null != selectedUser) {
			if (CollectionUtils.isNotEmpty(getManagedBean().getSelectedQtIds())) {

				for (Long qtID : getManagedBean().getSelectedQtIds()) {
					if (isValidReassignment(qtID, selectedUser)) {
						getManagedBean().getQtNoDeskOfficerMap().put(qtID, selectedUser);
					} else {
						String notAvailableStr = ResourceBundleUtils.getMessage("lbl.txt.na",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());

						if (notAvailableStr.trim().equals(getNewDeskOfficer(qtID))) {
							disableSubmit = true;
						}

						showError = true;
					}
				}
			}

			if (showError) {
				RequestContext.getCurrentInstance().execute("dlTaskReassignment.hide()");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null, MessageUtils.getMessage("ERR-760493", null,
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

				getManagedBean().setDisableSubmit(disableSubmit);
			} else {
				getManagedBean().setDisableSubmit(disableSubmit);

				RequestContext.getCurrentInstance().execute("dlTaskReassignment.hide()");
			}
		}
		// #25627 - End.
	}
	
	public String getNewDeskOfficer(Long qtID) {
		PmUser newDeskOfficer = null;
		if (null != getManagedBean().getQtNoDeskOfficerMap()) {
			newDeskOfficer = getManagedBean().getQtNoDeskOfficerMap().get(qtID);
			if (newDeskOfficer != null) {
				return newDeskOfficer.getUserName();
			} 
			newDeskOfficer = null;
		}
		// #25627 - Start.
		Locale locale = new FacesScopes().getUserLocale();
		if (ReportConst.ENG_LANGUAGE_CODE.equals(locale.getLanguage())) {
			return "N/A";			
		} else {
			return "T/B";
		}
		// #25627 - End.
	}
	
	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

	/**
	 * @return the userListDataModel
	 */
	public PagingDataModel<PmUser> getUserListDataModel() {//find me 1
		return userListDataModel;
	}

	/**
	 * @param userListDataModel the userListDataModel to set
	 */
	public void setUserListDataModel(PagingDataModel<PmUser> userListDataModel) {
		this.userListDataModel = userListDataModel;
	}
	
	public void submitReassignmentForApproval(PmDigiSign digisign){
		QuationTenderResDTO resDTO  = null;
		try{
			IQTDetailSO detailSO= BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			int successCount = 0;
			for (Map.Entry<Long, PmUser> entry : this.getManagedBean().getQtNoDeskOfficerMap().entrySet()){
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(entry.getKey());
				
				resDTO = detailSO.retriveQTDetails(reqDTO);
				ScQt qt = resDTO.getQtDetail();
			
				reqDTO = null;
				reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtdetail(qt);
				reqDTO.setDeskOfficer(entry.getValue());
				resDTO = null;
				if(!isCommitteeApprover() && isDeskOfficer()){
					resDTO = detailSO.submitDOReassignment(reqDTO);	
				}else if(isCommitteeApprover()){
				
					IScActorSO scActorSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IScActorSO.class);
					ScActorReqDTO actorReqDTO  = new ScActorReqDTO();
					actorReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
					actorReqDTO.setDocId(qt.getQtId());
					actorReqDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
					ScActor deskOfficer = scActorSO.retrieveActorByTypeIdAndRoleCode(actorReqDTO).getScActor();
					PmUser originalDO = this.retrivePmUser(deskOfficer.getUserId());
					
					if(digisign != null){
						reqDTO.setDigiSignData(digisign);
					}
					reqDTO.setNewDO(entry.getValue());
					reqDTO.setOriginalDO(originalDO);
					reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
					reqDTO.setCAApprover(getFacesScopes().getUserSessionBean()); //25627 - update 233
					
					resDTO = detailSO.reassignDeskOfficer(reqDTO);
				}

				if(resDTO.isSuccessful()){
					successCount ++;
				}
			}  
			String msgCode = this.isCommitteeApprover() ? "INFO-760490" : "INFO-760492";
			if(successCount ==  this.getManagedBean().getQtNoDeskOfficerMap().size()){
			
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null,
								MessageUtils.getMessage(msgCode, null,
										MessageUtils.BUNDLE_COMMON_MESSAGES,
										new FacesScopes().getUserLocale())));
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);

			}	
		}catch(EpException e){
			handleException(new EpException(MessageConst.ERROR_000001));
		}finally{
			resDTO = null;
		}
	}
	
	public void preRenderApprovalView(){
		Object obj = getBpmTaskDetails(Object.class, null);
		String taskName = getManagedBean().getTaskName();
		
		SCQuotationTenderDataType payload = ((SCQuotationTenderDataType) obj);
		String page = null;
		Long requestId = payload.getRequestId().longValue();
		Long qtId = payload.getQtId().longValue();
		
		if(SCProcessNameConst.APPROVE_DO_REASSIGNMENT.equalsIgnoreCase(taskName)){
			QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
			reqDTO.setQtId(qtId);
			reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);//later change
			QuationTenderResDTO resDTO = getQTDetailSO().retrieveApprovalDtlsByQtIdNType(reqDTO);
			ScQt qt =  getQTDetailSO().retriveQtById(reqDTO).getQtDetail();
			
			ScQtApprovalRequest currentApprovalRequest = null;
			
			IScWorkFlowStatusSO workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO statusReqDTO = new ScWorkFlowStatusReqDTO();
			statusReqDTO.setDocType(ParameterConst.DOCTYPE_DO_REASSIGN);
			statusReqDTO.setDocId(qtId);
			ScWorkFlowStatus currentStatus = workFlowStatusSO.retrieveStatusByTypeAndId(statusReqDTO).getWorkFlowStatus();
			
			if(resDTO.getQtApproval() !=null && CollectionUtils.isNotEmpty(resDTO.getQtApproval().getScQtApprovalDtls())){
				this.getManagedBean().setCurrentApproval(resDTO.getQtApproval());
				for(ScQtApprovalRequest req: resDTO.getQtApproval().getScQtApprovalDtls()){
					if(req.getQtApprovalRequestId().equals(requestId)){
						currentApprovalRequest = req;
						break;
					}
				}
			}
			
			if(CollectionUtils.isEmpty(getManagedBean().getCurrentApproval().getScQtApprovalRemarks())){//BYPASS DEFAULT REMARK INIT
				getManagedBean().getCurrentApproval().setScQtApprovalRemarks(new ArrayList<ScQtApprovalRemark>());
			}else{
				for(ScQtApprovalRemark remark: getManagedBean().getCurrentApproval().getScQtApprovalRemarks()){
					remark.setBrandNew(false);
					remark.setEditable(false);
				}
			}		
			ScQtApprovalRemark remark = new ScQtApprovalRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean()
					.getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remark.setParent(getManagedBean().getCurrentApproval());
			getManagedBean().getCurrentApproval().getScQtApprovalRemarks().add(0, remark);
//			initializeRemarks(getManagedBean().getCurrentApproval(), 
//					currentStatus, 
//					getManagedBean().getCurrentApproval().getScQtApprovalRemarks(),
//					ScQtApprovalRemark.class);
			
			this.getManagedBean().setPayLoad(payload);
			this.getManagedBean().setCurrentApprovalRequest(currentApprovalRequest);
			this.getManagedBean().getCurrentApproval().setScQt(qt);
			this.getManagedBean().setOriginalDeskOfficer(this.retrivePmUser(Long.parseLong(currentApprovalRequest.getOriginalValue())));
			this.getManagedBean().setNewDeskOfficer(this.retrivePmUser(Long.parseLong(currentApprovalRequest.getChangedValue())));
			page = "/xhtml/sc/quatationtender/deskOfficerMaintenance/approval/deskOfficerMaintenanceApproval.xhtml";
			
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler().handleNavigation(context, null, page);
	}
	
	public String requestTypeDescription(Long requestId){
		return this.getParameterDesc(this.retrieveParameterById(requestId),  new FacesScopes().getUserLocale());
	}
	
	private void submitDOReassignmentApproval(String outcome, PmDigiSign digisign){
		try{
			if(CollectionUtils.isNotEmpty(getManagedBean().getCurrentApproval().getScQtApprovalRemarks())){
				// 25627 - note 310 start
				if (!BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
					this.getManagedBean().getCurrentApprovalRequest()
							.setChangedValue(this.getManagedBean().getNewDeskOfficer().getUserId().toString());
				}
				// 25627 - note 310 end

				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setApproval(this.getManagedBean().getCurrentApproval());
				reqDTO.setApproverAction(outcome);
				reqDTO.setPayLoad(this.getManagedBean().getPayLoad());
				reqDTO.setTaskId(this.getManagedBean().getTaskId());
				reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
				reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
				reqDTO.setDigiSignData(digisign);
				QuationTenderResDTO resDTO = getQTDetailSO().updateDOReasignmentTask(reqDTO);
				
				if(resDTO.isSuccessful()){
					String msgCode = outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) ? "INFO-760490" : "INFO-760491";
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null,
									MessageUtils.getMessage(msgCode, null,
											MessageUtils.BUNDLE_COMMON_MESSAGES,
											new FacesScopes().getUserLocale())));
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
				}
			}
			this.reset();
		}catch(EpException e){
			handleException(new EpException(MessageConst.ERROR_000001));
		}finally{
		}
	}
	
	public void signGPKI(String outcome){
		this.getManagedBean().setAction(outcome);
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)){
			getManagedBean().setAutoUpdate(false);	
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");	
			//submitDOReassignmentApproval(this.getManagedBean().getAction(), null);// to skip GPKI-INCASE 
		}else{
			submitDOReassignmentApproval(this.getManagedBean().getAction(), null);
		}
	}
	
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
	
	public void postGPKISignAction(PmDigiSign digiSign) {
		if(StringUtils.isNotBlank(this.getManagedBean().getAction())){
			ScQtApproval approval = this.getManagedBean().getCurrentApproval();
			updatePmDigiSignData(digiSign, approval.getApprovalCategory().getParameterCode(),
					approval.getScQt().getQtId(), approval.getScQt().getQtNo());
			submitDOReassignmentApproval(this.getManagedBean().getAction(), digiSign);
		}
	}
	
	public String constructConditionForUserSearch(){
		StringBuilder sb = new StringBuilder();
        sb.append("pmUserOrgs.pmOrgProfile.orgProfileId^");
    	sb.append(this.getManagedBean().getDoMaintenanceSearchBean().getPtjProfile().getOrgProfileId());
		sb.append("|userId^");
        sb.append(this.getFacesScopes().getUserSessionBean().getUserId());
        if(this.getManagedBean().getOriginalDeskOfficer()!=null && this.getManagedBean().getOriginalDeskOfficer().getUserId()!=null){
        	   sb.append(",");
               sb.append(this.getManagedBean().getOriginalDeskOfficer().getUserId());
        }
		sb.append("^");
		sb.append(SearchOperator.OPERATOR_NOT_IN.value());
		if(StringUtils.isNotEmpty(this.getManagedBean().getTaskName())){
			sb.append("|pmUserOrgs.pmUserRoles.pmRole.roleCode^");
			sb.append(RoleCode.DESK_OFFICER.value());
			sb.append("|pmUserOrgs.pmUserRoles.recordStatus^1");
		}
		return sb.toString();
	}
	
	public String retrieveUsername(){
		if(this.getManagedBean().getOriginalDeskOfficer()!=null){
			return this.getManagedBean().getOriginalDeskOfficer().getUserName();
		}
		return null;
	}
	public void handleFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean().getCurrentApproval();

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
		}
	}
	private int compositeVersionComparator(String v1, String v2){//return +1 if v1 is latest, -1 if not, 0 if same
		 int res = 0;
		 if(StringUtils.isBlank(v1) || StringUtils.isBlank(v2)){
	            throw new IllegalArgumentException("Version can not be null or empty.");
		 }
	     if(!v1.matches("[0-9]+(\\.[0-9]+)*") || !v2.matches("[0-9]+(\\.[0-9]+)*")){
	            throw new IllegalArgumentException("Version format is not valid.");
	     }
	     String[] va1 = v1.split("\\.");
	     String[] va2 = v2.split("\\.");
	     List<String> c1 = new ArrayList<String>(Arrays.asList(va1));
	     List<String> c2 = new ArrayList<String>(Arrays.asList(va2));

	     int len = c1.size()-c2.size();
	     if(len < 0){
	    	 for(int i = 0; i<-len;i++){
	    		 c1.add("0");
	    	 }
	     }else if(len>0){
	    	 for(int i = 0; i<len;i++){
	    		 c2.add("0");
	    	 }
	     }
	     for(int j=0; j<c1.size();j++){
	    	int a =  Integer.parseInt(c1.get(j));
	    	int b =  Integer.parseInt(c2.get(j));
	    	 if(a>b){
	    		 res = 1;
	    		 break;
	    	 }else if(a<b){
	    		 res = -1;
	    		 break;
	    	 }
	     }
	     return res;
	}
	
	public void assertMethodIfRequireApproval(){
		if(isCommitteeApprover()){
			getManagedBean().setAutoUpdate(false);	
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");	
			//submitReassignmentForApproval(null);// to skip GPKI-INCASE 
		}else if(isDeskOfficer()){
			submitReassignmentForApproval(null);
		}
	}
	
	public void preRenderDeskReassignmentView(){
		this.reset();
		String page = "/xhtml/sc/quatationtender/deskOfficerMaintenance/deskOfficerMaintenance.xhtml";
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler().handleNavigation(context, null, page);
	}
	
	/**
	 * 
	 */
	public void enableDisableAssign() {
		if (getManagedBean().getSelectedUser() != null) {
			getManagedBean().setDisableAssign(false);
		} else {
			getManagedBean().setDisableAssign(true);
		}
	}
}
