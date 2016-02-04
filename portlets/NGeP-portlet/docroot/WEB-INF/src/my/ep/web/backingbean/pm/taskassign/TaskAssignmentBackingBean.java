/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.taskassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.fl.IFulfilmentDocumentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.taskassginment.ITaskAssignmentSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.fl.FlDocumentReqDTO;
import my.ep.dto.fl.FlDocumentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentReqDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.pm.taskassign.TaskAssignmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
/**
 * Backing Bean for Task Assignment
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name="taskAssignBackingBean")
@ViewScoped
public class TaskAssignmentBackingBean extends BaseBackingBean<TaskAssignmentManagedBean>{
	private static Logger log = Logger.getLogger(TaskAssignmentBackingBean.class);
	public static final boolean SEARCH_DEFAULT = false;
	//Navigation Details
	public static final String NAV_TO_SEARCH = "taskAssignment";

	//Mapper for DDL object
	private static Map<Long, Object> orgTypeMapper;

	//Authorization 
	private ISearchBean<PmParameterDesc> orgTypeSearchBean;
	
	//List for Paging
	private PagingDataModel<WorklistItemVO> taskListDataModel = new taskPagingDataModel();
	
	private String currentDate;
	public static final String APPROVER = "APPROVER";
	
	public void preLoading() throws EpException{
		log.info("preLoading");
		this.setCurrentActiveOrg();
		this.generateAdditionalFilter();
		this.resetTaskAssignSearchBean();
		this.retrieveOrgTypeMap();
		
		this.getManagedBean().setOrgProfileRequired(true);
		this.getManagedBean().setResultUser(null);
		this.getManagedBean().setResultOrgProfile(null);

		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_SEARCH);
	}
	
	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg(){
		log.info("setCurrentActiveOrg");
		if(getFacesScopes().getUserSessionBean().getUserId()==null){
			throw new EpException(MessageConst.ERROR_770149);
		}
		
		List <PmUserOrg> userOrgList = PmCommonUtil.retrieveActiveOrgWithoutRole(getFacesScopes().getUserSessionBean().getUserId(), false);
		
		if(userOrgList==null || userOrgList.isEmpty()){
			throw new EpException(MessageConst.ERROR_770105);
		}
		
		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
	}
	
	public ISearchBean<PmParameterDesc> getOrgTypeSearchBean() {
		log.info("getOrgTypeSearchBean");
		if (this.orgTypeSearchBean == null) {
			this.orgTypeSearchBean = super.createSearchBean(
					PmParameterDesc.class, "somOrgType");
			
//			SearchFilterBean bean = new SearchFilterBean();
//			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
//			bean.setSearchField("pmParameter.parameterCode");
//			bean.setSearchValue(new ArrayList(Arrays.asList(ProfileManagementConst.TASK_ASSIGNMENT_ORG_TYPE_INCLUDE)));
//			orgTypeSearchBean.getDataFilterBeans().add(bean);
//			
//			if(getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.PROCUREMENT_ADMIN.value())){
//				if(this.getManagedBean().getCurrentActiveOrg()!=null){
//					SearchFilterBean orgTypeBean = new SearchFilterBean();
//					orgTypeBean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//					orgTypeBean.setSearchField("pmParameter.parameterCode");
//					orgTypeBean.setSearchValue(this.getManagedBean().getCurrentActiveOrg().getOrgCode());
//					orgTypeSearchBean.getDataFilterBeans().add(orgTypeBean);
//				}
//			}
			
		}

		return this.orgTypeSearchBean;
	}
	
	
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	
	
	/**
	 * reset search bean fields
	 */
	public void resetTaskAssignSearchBean(){
		log.info("resetTaskAssignSearchBean");
		this.getManagedBean().setTaskAssignmentSearchBean(new TaskAssignmentSearchBean());
	}
	
	/**
	 * search task assignment
	 */
	public void searchTaskAssign(){
		log.info("searchDocument - Start");
		if(!(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()!=null 
				&& this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId()!=null)){
			throw new EpException(MessageConst.ERROR_770091);
		}
		if(!(this.getManagedBean().getTaskAssignmentSearchBean().getUser()!=null 
				&& this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId()!=null 
				&& this.getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId().length() > 0)){
			throw new EpException(MessageConst.ERROR_770092);
		}
		this.getManagedBean().setResultUser(this.getManagedBean().getTaskAssignmentSearchBean().getUser());
		this.getManagedBean().setResultOrgProfile(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile());
		this.taskListDataModel.reloadData();
		log.info("searchDocument - End");
		
	}

	public String getCurrentDateStr(){
//		conditionField="orgTypeCode.parameterType^#{ParameterTypeConst.organisationTypes}|orgTypeCode.parameterCode^#{taskAssignBackingBean.managedBean.taskAssignmentSearchBean.orgType.parameterCode}|recordStatus^1|pmOrgValidity.effDate^#{taskAssignBackingBean.getCurrentDateStr()}^6|pmOrgValidity.expDate^#{taskAssignBackingBean.getCurrentDateStr()}^4*pmOrgValidity.expDate^null^11"/>
		if(this.currentDate==null){
			currentDate = PmCommonUtil.getCurrentDateStr(new Date());
		}
		return currentDate;
		
	}
	
	/**
	 * retrieve and set org type mapper
	 */
	private void retrieveOrgTypeMap(){
		log.info("retrieveOrgTypeMap");
		List<PmParameterDesc> parameterDescs = getOrgTypeList();
		orgTypeMapper = new HashMap<Long,Object>();
		for(PmParameterDesc pmDesc: parameterDescs){
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(), pmDesc.getPmParameter());
		}
	}
	
	/**
	 * Retrieve Org. Type from PM
	 * @return List<PmParameterDesc> 
	 */
	public List<PmParameterDesc> getOrgTypeList() {
		log.info("getOrgTypeList");
		return PmCommonUtil.retrieveFilteredParamDesc(ParameterTypeConst.ORGANISATION_TYPES
				, null
				, new ArrayList(Arrays.asList(ProfileManagementConst.TASK_ASSIGNMENT_ORG_TYPE_INCLUDE)));
	}

	
//	public void roleCheck(){
//		log.info("roleCheck");
//		if(getFacesScopes().getUserSessionBean().getLoginId()!=null){
//			if(getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())){
//				this.getManagedBean().setFilterOrg(false);
//			}else if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
//				this.getManagedBean().setFilterOrg(true);
//				StringBuilder sb = new StringBuilder();
//				//parentId = orgProfileId
//				List <PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
//				if(pmUserOrgs!=null && !pmUserOrgs.isEmpty()){
//					int count = 1;
//					for(PmUserOrg pmUserOrg: pmUserOrgs){
//						sb.append(pmUserOrg.getPmOrgProfile().getOrgProfileId());
//						if(count<pmUserOrgs.size()){
//							sb.append(",");
//						}
//						count++;
//					}
//				}
//				this.getManagedBean().setUserActiveOrgs(sb.toString());
//			}else if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
//				this.getManagedBean().setFilterOrg(true);
//				StringBuilder sb = new StringBuilder();
//				//parentId = parentOrg.orgProfileId
//				List <PmUserOrg> pmUserOrgs = PmCommonUtil.retrieveActiveOrg(getFacesScopes().getUserSessionBean().getUserId());
//				if(pmUserOrgs!=null && !pmUserOrgs.isEmpty()){
//					int count = 1;
//					for(PmUserOrg pmUserOrg: pmUserOrgs){
//						if(pmUserOrg.getPmOrgProfile().getParentOrgProfile()!=null){
//							sb.append(pmUserOrg.getPmOrgProfile().getParentOrgProfile().getOrgProfileId());
//							if(count<pmUserOrgs.size()){
//								sb.append(",");
//							}
//						}
//						count++;
//					}
//				}
//				this.getManagedBean().setUserActiveOrgs(sb.toString());
//				
//			}
//		}
//		log.info("UserActiveOrgs: " + this.getManagedBean().getUserActiveOrgs());
//		log.info("FilterOrg: " + this.getManagedBean().isFilterOrg());
//	}
//	
	
	/**
	 * change organization type
	 */
	public void changeOrgType() {

		
		log.info("changeOrgType");
		if(this.getManagedBean().getTaskAssignmentSearchBean()!=null){
			
			if(this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile()==null){
				this.getManagedBean().getTaskAssignmentSearchBean().setOrgProfile(new PmOrgProfile());
			}
			if(this.getManagedBean().getTaskAssignmentSearchBean().getUser()==null){
				this.getManagedBean().getTaskAssignmentSearchBean().setUser(new PmUser());
			}
			this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgName("");
			this.getManagedBean().getTaskAssignmentSearchBean().getUser().setUserName("");
			this.getManagedBean().getTaskAssignmentSearchBean().getUser().setLoginId("");
			this.getManagedBean().getTaskAssignmentSearchBean().setDocType(null);
			
			if(this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId()!=null){
				PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId());
				this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().setParameterCode(pmParameter.getParameterCode());
		
				if(ParameterConst.ORG_TYPE_SM_BPK_HQ.equals(pmParameter.getParameterCode())
					|| ParameterConst.ORG_TYPE_SM_PMS_SABAH.equals(pmParameter.getParameterCode())
					|| ParameterConst.ORG_TYPE_SM_PMS_SARAWAK.equals(pmParameter.getParameterCode())
					||ParameterConst.ORG_TYPE_BPK_HQ.equals(pmParameter.getParameterCode())
					||ParameterConst.ORG_TYPE_PMS_SABAH.equals(pmParameter.getParameterCode())
					||ParameterConst.ORG_TYPE_PMS_SARAWAK.equals(pmParameter.getParameterCode())){
					this.getManagedBean().setOrgProfileRequired(false);
					//default org profile
					PmOrgValidity pmOrgValidity = getOrgProfile(pmParameter.getParameterCode());
					this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgProfileId(pmOrgValidity.getPmOrgProfile().getOrgProfileId());
					this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().setOrgName(pmOrgValidity.getOrgName());
				}else{
					this.getManagedBean().setOrgProfileRequired(true);
				}
			}
		}
	}
	
		
	private void generateAdditionalFilter(){
		log.info("generateAdditionalFilter");
		this.getManagedBean().setFilterOrgCondition("");
		//generate additional filter
		if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
			this.getManagedBean().setFilterOrgCondition("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()
					+"*parentOrgProfile.orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()
					+"*parentOrgProfile.parentOrgProfile.orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		}else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
			this.getManagedBean().setFilterOrgCondition("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId()
					+"*parentOrgProfile.orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		}else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
			this.getManagedBean().setFilterOrgCondition("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
		}
	}
	
	public String constructConditionFields(){
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterId^");
		sb.append(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId());
		sb.append("|recordStatus^1");
		if(OrganizationType.MINISTRY.value()
				.equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
			if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
				sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}
		}else if(OrganizationType.DEPARTMENT.value()
				.equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
			if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
				sb.append("|");
				sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}else if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
				sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}
		}else if(OrganizationType.DIVISION.value()
				.equals(getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterCode())){
			if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
				sb.append("|");
				sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}else if(getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
				sb.append("|");
				sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
				sb.append("^");
				sb.append(this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}else if(getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())){
				sb.append("|orgProfileId^"+this.getManagedBean().getCurrentActiveOrg().getOrgProfileId());
			}
		}
		
		return sb.toString();
	}

	
	/**
	 * retrieve valid org profile based on selected org type
	 * @param paramCode
	 * @return PmOrgValidity
	 */
	private PmOrgValidity getOrgProfile(String paramCode){
		ISearchBean<PmOrgProfile> searchBean = new SearchBean<PmOrgProfile>();
		
		SearchFilterBean bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("orgTypeCode.parameterCode");
		bean.setSearchValue(paramCode);
		searchBean.getDataFilterBeans().add(bean);
		
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);
		
		bean = new SearchFilterBean();
		bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
		bean.setSearchField("pmOrgValidity.recordStatusFlg");
		bean.setSearchValue(1);
		searchBean.getDataFilterBeans().add(bean);
		
		PmOrgValidity pmOrgValidity = new PmOrgValidity();

		OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		OrgProfileResDTO resDTO = null;
		reqDTO.setOrgType(paramCode);
//		reqDTO.setOrgProfileSearchBean(searchBean);
		IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		resDTO = orgProfileSO.retrieveValidOrgProfile(reqDTO);
		if(resDTO.isSuccessful()){
			pmOrgValidity = resDTO.getPmOrgValidity();
//			System.out.println("size>>>>" + resDTO.getOrgProfileResultBean().getResultList().size()); searchOrgProfile
//			for(PmOrgProfile temp:resDTO.getOrgProfileResultBean().getResultList()){
//				System.out.println(temp.getPmOrgValidity().getOrgName());
//			}
		}
		return pmOrgValidity;
			
	}
	/**
	 * Redirect to Task Re-assignment Details
	 * @param item WorklistItemVO
	 * @return xhtml
	 */
	public void redirectToTaskReassign(WorklistItemVO item){
		log.info("redirectToTaskReassign - Start");
		IFulfilmentDocumentSO fulfilmentDocumentSO = null;
		try{
			this.getManagedBean().setAvailableUsers(new ArrayList<PmUser>());
			this.getManagedBean().setSelectedUser(null);
			this.getManagedBean().setSelectedTask(item);
			this.getManagedBean().setAvailableUsers(generateAvailableUsers(item));			
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToTaskReassign - End");
		RequestContext.getCurrentInstance().execute("dlTaskReassignment.show()");
	}
	
	
	/**
	 * Get meaningful application status message
	 * @param docInt
	 * @return String
	 */
	public String applicationStatus(Integer docInt) {
		log.info("applicationStatus");
		log.info("docInt:" + docInt);
		if(docInt!=null){
			return getStatusDescById(docInt.longValue());
		}else{
			return "";
		}
	}

	
	
	/**
	 * retrieve available roles by status id
	 * @param statusId
	 * @return List<String>
	 */
	private List<String> retrieveAvailableRoles(Long statusId){
		log.info("retrieveAvailableRoles");
		List<String> statusRoles = new ArrayList<String>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO.setStatusId(statusId);
			StatusResDTO resDTO = statusSO.retrieveStatusRoleByStatusId(reqDTO);
			if(resDTO.isSuccessful()){
				for(PmStatusRole statusRole:resDTO.getStatusRoleList()){
					statusRoles.add(statusRole.getPmRole().getRoleCode());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770093, new String[]{e.getMessage()});
		}
		return statusRoles;
	}
	
	/**
	 * retrieve status id list for BPM
	 * @return
	 */
	private List<Integer> retrieveAvailableStatusId(){
		log.info("retrieveAvailableStatusId");
		List<Integer> statusIds = new ArrayList<Integer>();
		try {
			StatusReqDTO reqDTO = new StatusReqDTO();
			if(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null && !getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().isEmpty()){		
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles());
			}else if(getManagedBean().getRoleCodes() != null && !getManagedBean().getRoleCodes().isEmpty()){
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getRoleCodes());
			}
			
			if(reqDTO.getRoleCodeList() != null && !reqDTO.getRoleCodeList().isEmpty()){
				IStatusSO statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusResDTO resDTO = statusSO.retrieveStatusRoleListByRoleCode(reqDTO);
				if(resDTO.isSuccessful()){
					for(int i=0; i<resDTO.getStatusRoleList().size(); i++){
						statusIds.add(resDTO.getStatusRoleList().get(i).getPmStatus().getStatusId().intValue());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770094, new String[]{e.getMessage()});
		}
		return statusIds;
	}
	
	/**
	 * retrieve and generate available users
	 * @return List<PmUser>
	 */
	public List<PmUser> generateAvailableUsers(WorklistItemVO task){
		log.info("generateAvailableUsers");

		boolean isApprover = false;
		List <PmUser> pmUsers = new ArrayList<PmUser>();
		try {
			List<String> statusRoles = retrieveAvailableRoles(this.getManagedBean().getSelectedTask().getDocStatus().longValue());
			Long userId = this.getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId();
			Long orgProfileId = this.getManagedBean().getTaskAssignmentSearchBean().getOrgProfile().getOrgProfileId();
			Long orgTypeId = this.getManagedBean().getTaskAssignmentSearchBean().getOrgType().getParameterId();
			ISearchBean<PmUser> searchBean = new SearchBean<PmUser>();
		
			/* Search Bean Filter - Start */
			// only active user
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// only active user role
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// only active org group
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.recordStatusFlg");
			bean.setSearchValue(DbValueConst.RECORD_STATUS_ACTIVE);
			searchBean.getDataFilterBeans().add(bean);
			
			// not current selected user
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_EQUALS.value());
			bean.setSearchField("userId");
			bean.setSearchValue(userId);
			searchBean.getDataFilterBeans().add(bean);
				
			// same org profile id
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmOrgProfile.orgProfileId");
			bean.setSearchValue(orgProfileId);
			searchBean.getDataFilterBeans().add(bean);
			
			// same org type id
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
			bean.setSearchField("pmUserOrgs.pmOrgProfile.orgTypeCode.parameterId");
			bean.setSearchValue(orgTypeId);
			searchBean.getDataFilterBeans().add(bean);

			// in applicable role code
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
			bean.setSearchValue(statusRoles);
			searchBean.getDataFilterBeans().add(bean);
			
			String moduleCode = PmCommonUtil.getPmStatusById(task.getDocStatus().longValue()).getModuleCode();
			if(ModuleCodeConst.ORDER_FULFILLMENT.equalsIgnoreCase(moduleCode)){
				// approver should be returned by BPM not selected from DB
				boolean receivingOfficer = false;
				for(String status : statusRoles){
					if(ProfileManagementConst.RECEIVING_OFFICER.equals(status)){
						receivingOfficer = true;
						break;
					}
				}
			
				if(receivingOfficer){
					List<Long> userIds = getReceivingOfficerIdListByDeliveryOrderId(task.getDocId().longValue());			
					if(userIds != null && !userIds.isEmpty()){											
						bean = new SearchFilterBean();
						bean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
						bean.setSearchField("userId");					
						bean.setSearchValue(userIds);
						searchBean.getDataFilterBeans().add(bean);
					}				
				}else{
					bean = new SearchFilterBean();
					bean.setSearchOperator(SearchOperator.OPERATOR_NOT_LIKE.value());
					bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
					bean.setSearchValue(APPROVER);
					searchBean.getDataFilterBeans().add(bean);
				}
				
			}
			/* 27 Jan 2014
			// approver should be returned by BPM not selected from DB
			bean = new SearchFilterBean();
			bean.setSearchOperator(SearchOperator.OPERATOR_NOT_LIKE.value());
			bean.setSearchField("pmUserOrgs.pmUserRoles.pmRole.roleCode");
			bean.setSearchValue(APPROVER);
			searchBean.getDataFilterBeans().add(bean);*/
			/* Search Bean Filter - End */
			
			//search users
			pmUsers = PmCommonUtil.searchUsersBySearchBean(searchBean);

			//if contains approver role
			for(String roleCode:statusRoles){
				if(roleCode.toLowerCase().contains(APPROVER.toLowerCase())){
					isApprover = true;
					break;
				}
			}
			if(isApprover && ModuleCodeConst.ORDER_FULFILLMENT.equalsIgnoreCase(moduleCode)){
				generateCompleteUsers(pmUsers, task, userId);
			}
			/* 27 Jan 2014
			if(isApprover){
				generateCompleteUsers(pmUsers, task);
			}*/
			
				
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_770095, new String[]{e.getMessage()});
		}		
		return pmUsers;
	}
	
	/**
	 * retrieve selected approvers from modules (SC, FL, CT)
	 * @param pmUsers
	 * @param task
	 */
	private void generateCompleteUsers(List<PmUser> pmUsers, WorklistItemVO task, Long selectedUserId){
		log.info("generateCompleteUsers");
		String moduleCode = PmCommonUtil.getPmStatusById(task.getDocStatus().longValue()).getModuleCode();
		log.info("moduleCode: " + moduleCode);
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		//FL
		if(ModuleCodeConst.ORDER_FULFILLMENT.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveFlApprovers(task.getDocNumber(), task.getDocType());
		//SC
		}else if(ModuleCodeConst.SUPPLIER_SELECTION.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveScApprovers(task.getDocId(), task.getDocStatus());
		//CT
		}else if(ModuleCodeConst.CONTRACT_MANAGEMENT.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrieveCtApprovers(task.getDocNumber(), task.getDocType());
		//PP
		}else if(ModuleCodeConst.PROCUREMENT_PLAN.equalsIgnoreCase(moduleCode)){
			selectedApprovers = retrievePpApprovers(task.getDocId().longValue());
		}
		if(selectedApprovers!=null && !selectedApprovers.isEmpty()){
			log.info("add approvers to list");
			pmUsers.addAll(selectedApprovers);
			removeSelectedUser(pmUsers, selectedUserId);
		}
	}
	
	/**
	 * remove selected user from list
	 * @param pmUsers
	 * @param selectedUserId
	 */
	private void removeSelectedUser(List<PmUser> pmUsers, Long selectedUserId){
		log.info("removeSelectedUser");
		PmUser selectedUser = null;
		for(PmUser pmUser:pmUsers){
			if(pmUser.getUserId().equals(selectedUserId)){
				selectedUser = pmUser;
			}
		}
		
		if(selectedUser!=null){
			pmUsers.remove(selectedUser);
		}
	}

	/**
	 * retrieve selected approvers from PP
	 * @param docNo
	 * @param docType
	 * @return List<PmUser>
	 */
	private List<PmUser> retrievePpApprovers(Long docId){
		log.info("retrievePpApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IProcurementPlanSO reqSO = null;
		ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
		ProcurementPlanResDTO resDTO = new ProcurementPlanResDTO();
		try{
			
			log.info("docId: " + docId);
			reqDTO.setDocId(docId);
			reqSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROCUREMENT_PLAN, IProcurementPlanSO.class);
			resDTO = reqSO.retrieveApproverListByDocId(reqDTO);
			if(resDTO.isSuccessful()){
				selectedApprovers = resDTO.getApproverList();
			}
		}catch(Exception e){
			throw new EpException(MessageConst.ERROR_770103, new String[]{ModuleCodeConst.PROCUREMENT_PLAN, e.getMessage()}); 
		}finally{
			reqSO = null;
		}
		
		return selectedApprovers;
	}
	/**
	 * retrieve selected approvers from FL
	 * @param docNo
	 * @param docType
	 * @return List<PmUser>
	 */
	private List<PmUser> retrieveFlApprovers(String docNo, String docType){
		log.info("retrieveFlApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IFulfilmentDocumentSO reqSO = null;
		FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
		FlDocumentResDTO resDTO = new FlDocumentResDTO();
		try{
			log.info("docType: " + docType);
			log.info("docNo: " + docNo);
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(docNo);
			reqSO = (IFulfilmentDocumentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentDocumentSO.class);
			resDTO = reqSO.retrieveApproverListByDocTypeAndNo(reqDTO);
			if(resDTO.isSuccessful()){
				selectedApprovers = PmCommonUtil.retriveUsersByIds(resDTO.getUserIdList());
			}
		}catch(Exception e){
			throw new EpException(MessageConst.ERROR_770103, new String[]{ModuleCodeConst.ORDER_FULFILLMENT, e.getMessage()}); 
		}finally{
			reqSO = null;
		}
		
		return selectedApprovers;
	}
	/**
	 * retrieve selected approvers from CT
	 * @param docNo
	 * @param docType
	 * @return List<PmUser> 
	 */
	private List<PmUser> retrieveCtApprovers(String docNo, String docType){
		log.info("retrieveCtApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		IContractIntegrationSO reqSO = null;
		ContractIntegrationReqDTO reqDTO = new ContractIntegrationReqDTO();
		ContractIntegrationResDTO resDTO = new ContractIntegrationResDTO();
		try{
			log.info("docType: " + docType);
			log.info("docNo: " + docNo);
			reqDTO.setDocType(docType);
			reqDTO.setDocNo(docNo);
			reqSO = (IContractIntegrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractIntegrationSO.class);
			resDTO = reqSO.retrieveApproverListByDocTypeAndNo(reqDTO);
			if(resDTO.isSuccessful()){
				selectedApprovers = PmCommonUtil.retriveUsersByIds(resDTO.getUserIdList());
			}
		}catch(Exception e){
			throw new EpException(MessageConst.ERROR_770103, new String[]{ModuleCodeConst.CONTRACT_MANAGEMENT, e.getMessage()}); 
		}finally{
			reqSO = null;
		}
		return selectedApprovers;
	}
	
	/**
	 * retrieve selected approvers from SC
	 * @param docId
	 * @param statusCode
	 * @return List<PmUser>
	 */
	private List<PmUser> retrieveScApprovers(Integer docId, Integer statusCode){
		log.info("retrieveScApprovers");
		List <PmUser> selectedApprovers = new ArrayList<PmUser>();
		ISourcingIntegrationSO reqSO = null;
		SourcingIntegrationReqDTO reqDTO = new SourcingIntegrationReqDTO();
		SourcingIntegrationResDTO resDTO = new SourcingIntegrationResDTO();
		try{
			log.info("statusCode: " + statusCode);
			log.info("docId: " + docId);
			
			reqDTO.setStatusId(statusCode.longValue());
			reqDTO.setDocId(docId.longValue());
			reqSO = (ISourcingIntegrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ISourcingIntegrationSO.class);
			resDTO = reqSO.retrieveApprovalsByStatusId(reqDTO);
			
			if(resDTO.isSuccessful()){
				selectedApprovers = resDTO.getApprovers();
			}
		}catch(Exception e){
			throw new EpException(MessageConst.ERROR_770103, new String[]{ModuleCodeConst.SUPPLIER_SELECTION, e.getMessage()});
		}finally{
			reqSO = null;
		}
		return selectedApprovers;
	}
	
	/**
	 * Reassign task to selected user
	 */
	public void confirmReassignTask(){
		log.info("confirmReassignTask");
		PmUser pmUser = this.getManagedBean().getSelectedUser();
		WorklistItemVO task = this.getManagedBean().getSelectedTask();
		ITaskAssignmentSO taskAssignmentSO = null;
		if(pmUser==null){
			throw new EpException(MessageConst.ERROR_770096);
		}
		if(task==null){
			throw new EpException(MessageConst.ERROR_770097);
		}
		List<String> assigneeUsers = task.getAssignees();
		//populate assignee user id list
//		List<String> assigneeUsers = new ArrayList<String>();
//		if(StringUtils.isNotBlank(task.get())){
//			String[] array = task.getAssigneeUsers().split(",");
//			for (int i = 0; i < array.length; i++){
//				if(array[i].endsWith("(U)") ||  array[i].endsWith("(G)")){
//					assigneeUsers.add(array[i].substring(0,array[i].length() - 3));
//				}
//			}
//		}
		
		if(!assigneeUsers.isEmpty()){
			if(assigneeUsers.contains(pmUser.getLoginId())){
				throw new EpException("the task already exist under the user:" + pmUser.getUserName());
			}else{
				//remove the current user, replace with the new assignee.
				if(assigneeUsers.contains(getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId())){
					assigneeUsers.remove(getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId());		
				}
			}			
		}
		
		assigneeUsers.add(pmUser.getLoginId());	
		
		try{
			TaskAssignmentReqDTO reqDTO = new TaskAssignmentReqDTO();
			reqDTO.setTaskId(task.getTaskId());
			reqDTO.setUser(pmUser);
			reqDTO.setUserIdList(assigneeUsers);
			reqDTO.setDocNumber(task.getDocNumber());
			reqDTO.setDocId(task.getDocId());
			reqDTO.setDocType(task.getDocType());
			reqDTO.setDocStatus(task.getDocStatus());
			reqDTO.setRoleCode(PmCommonUtil.getUserRoleValue());
			reqDTO.setActionedByUser(getFacesScopes().getUserSessionBean().getUserId());
			
			taskAssignmentSO = (ITaskAssignmentSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ITaskAssignmentSO.class);
			TaskAssignmentResDTO resDTO  = taskAssignmentSO.reassignTask(reqDTO);
		
			//	isSuccess = reassignTask(pmUser, task);		
			if(resDTO != null && resDTO.isSuccessful()){
				this.taskListDataModel.reloadData();
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
				FacesMessage.SEVERITY_INFO,
				null,
				MessageUtils.getMessage(MessageConst.INFO_770104, new String[]{task.getDocNumber()}, 
				MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
		}finally{
			taskAssignmentSO = null;		
		}
		
	}
	
//	/**
//	 * Invoke BPM & Reassign task to selected user
//	 * @param users
//	 */
//	private boolean reassignTask(PmUser user, WorklistItemVO task)throws EpException{
//		log.info("reassignTask");
//		boolean isSuccess = false;
//		IBpmSO bpmSO = null;
//		BpmRequestDTO reqDTO = null;
//		BpmResponseDTO responseDTO = null;
//		Object token = null;
//		String taskId = task.getTaskId();
//		try {
//
//			if(taskId==null){
//				throw new EpException(MessageConst.ERROR_770098);
//			}
//			log.info("BPM taskId******"+taskId);
//			log.info("BPM userId*****"+user.getLoginId());
//			
//			reqDTO = new BpmRequestDTO();
//			bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
//			token = bpmSO.authenticate("weblogic","welcome1"); //TODO
//			
//			if(token==null){
//				throw new EpException(MessageConst.ERROR_770099); 
//			}
//		
//			log.info("BPM token******"+token);
//			
//			// set useful info
//			reqDTO.setCtxToken(token);
//			reqDTO.setTaskId(taskId);
//			reqDTO.getAssignee().add(new AssigneeVO(user.getLoginId(), "user"));
//
//			//invoke BPM
//			responseDTO = bpmSO.reassignTask(reqDTO);
//			
//			if(responseDTO.isSuccessful()){
//				isSuccess = true;					
//				
//			}else{
//				throw new EpException(MessageConst.ERROR_770100);
//			}
//		}catch(EpBpmException be){
//			throw new EpException(MessageConst.ERROR_770101, new String[]{be.getMessage()});
//		}catch(EpException e){
//			throw new EpException(MessageConst.ERROR_770102, new String[]{e.getMessage()});
//		} finally {
//			bpmSO = null;
//		}
//		return isSuccess;
//	}
	
	/**
	 * refresh the worklist item
	 */
	public void refreshTaskList() {
		this.taskListDataModel.reloadData();
	}
	
	public PagingDataModel<WorklistItemVO> getTaskListDataModel() {
		return taskListDataModel;
	}

	public void setTaskListDataModel(
			PagingDataModel<WorklistItemVO> taskListDataModel) {
		this.taskListDataModel = taskListDataModel;
	}

	private class taskPagingDataModel extends PagingDataModel<WorklistItemVO> {

		private static final long serialVersionUID = 1L;

		public taskPagingDataModel() {
			super("dtTaskAssignResult");
		}
		
		private ISearchResultBean<WorklistItemVO> getWorklistItems(){
			log.info("getWorklistItems");
			IBpmSO bpmSO = null;
			BpmRequestDTO reqDTO = null;
			BpmResponseDTO responseDTO = null;
			String token = null;
			String loginId = null;
			List<Integer> statusIds = null;
			TaskAssignmentSearchBean taskSearchBean = null;
			SearchResultBean<WorklistItemVO> resultBean = null;
			
			try {
				taskSearchBean = getManagedBean().getTaskAssignmentSearchBean();
				loginId = taskSearchBean.getUser()!=null? taskSearchBean.getUser().getLoginId() : null;
				
				if(loginId!=null && loginId.length() > 0){
					statusIds = retrieveAvailableStatusId();
					if(statusIds != null && !statusIds.isEmpty()){
	//					token = getFacesScopes().getBPMAuthenticationCtxToken();
						
						bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
					//	token = bpmSO.authenticate("weblogic","welcome1"); //TODO
						  
						reqDTO = new BpmRequestDTO();
	
						log.info("BPM pageNo******"+getPageNo());
						log.info("BPM pageSize*****"+getPageSize());
						log.info("BPM loginId******"+loginId);
						log.info("BPM statusIds******"+statusIds.size());
									
//						if(token==null){
//							throw new EpException(MessageConst.ERROR_770099);
//						}
//						log.info("BPM auth******" + token);
						
						// set useful info
					//	reqDTO.setCtxToken(token);
						reqDTO.setLimit(PagingConst.ROWS_PER_PAGE);
						reqDTO.setOffset(calculateFinalOffSet(getPageNo()));
				        reqDTO.setStatusList(statusIds);
				        reqDTO.setUserId(loginId);
				        List<String> docNumberList =  new ArrayList<String>();
				        if(StringUtils.isNotBlank(getManagedBean().getTaskAssignmentSearchBean().getDocNo())){
				        	docNumberList.add(getManagedBean().getTaskAssignmentSearchBean().getDocNo());
				        	reqDTO.setDocNumberList(docNumberList);
				        }
				        
				        if(getManagedBean().getTaskAssignmentSearchBean().getDocType() !=null && getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter() !=null ){
				        	reqDTO.setDocType(getManagedBean().getTaskAssignmentSearchBean().getDocType().getPmParameter().getParameterCode());
				        }
				        
	
						// Invoke BPM 
						responseDTO = bpmSO.getReassignedItems(reqDTO);
						
						// Set resultBean for UI
						resultBean = new SearchResultBean<WorklistItemVO>();
						resultBean.setTotalRecord(0);
						resultBean.setResultList(null);
						
						if (responseDTO.getWorklistVO() != null) {
							log.info("BPM size******" + responseDTO.getWorklistVO().getWorklistItem().size());
							log.info("BPM total******"+ responseDTO.getWorklistVO().getTotal());
							/*
							if(responseDTO.getWorklistVO().getWorklistItem()!=null) {
								// markup data
								markupWorklistItem(responseDTO.getWorklistVO().getWorklistItem());
							}*/
							resultBean.setTotalRecord(responseDTO.getWorklistVO().getTotal());
							resultBean.setResultList(responseDTO.getWorklistVO().getWorklistItem());
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				bpmSO = null;
			}
			return resultBean;
		}

		/**
		 * Generate meaningful title
		 * @param worklistItem
		 */
		private void markupWorklistItem(List<WorklistItemVO> worklistItem){
			log.info("markupWorklistItem");
			for(WorklistItemVO worklistItemVO : worklistItem) {
				// fl module Purchase Request Initiation Submit PR
				if(worklistItemVO.getProcess().equalsIgnoreCase(FLProcessNameConst.PURCHASE_REQ_INITIATION_PROCESS_NAME) && 
						worklistItemVO.getTaskName().equalsIgnoreCase(FLProcessNameConst.SUBMIT_PR_HT)) {
					if(!worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
						worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale())+" - "+worklistItemVO.getTitle());									
					}
				}
				// s1 module
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.RN_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.request.note", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PI_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.pr.inquiry", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
				if(worklistItemVO.getTitle().equalsIgnoreCase(SourcingConstants.PD_TITLE)) {
					worklistItemVO.setTitle(ResourceBundleUtils.getMessage("lbl.sc.purchase.in.default", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
				}
			}
		}
		@Override
		protected ISearchResultBean<WorklistItemVO> pageChangeListener(
				ISearchBean<WorklistItemVO> searchBean) {
			log.info("pageChangeListener");
			return getWorklistItems();
		}
		
		private int calculateFinalOffSet(int currentPage) {
			int offSet = (currentPage - 1) * PagingConst.ROWS_PER_PAGE;
			return offSet;
		}
	}
	
	public SelectItem[] getRoleList() {
		IUserSO userSO =null;
		SelectItem[] items =  null;
		try{
			if(getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId() != null 
					&& 	getManagedBean().getTaskAssignmentSearchBean().getUser().getLoginId().length() > 0
					&&	getManagedBean().getTaskAssignmentSearchBean().getUser().getUserName() != null
					&& 	getManagedBean().getTaskAssignmentSearchBean().getUser().getUserName().length() > 0){
				userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				List<Long> users = new ArrayList<Long>();
				users.add(getManagedBean().getTaskAssignmentSearchBean().getUser().getUserId());
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setUserIds(users);
				
				List<PmUserRole> userRoles = ( userSO
						.retrieveUserRoleListByUserIdsAndRoleCodes(userReqDTO))
						.getUserRoleList();	
				
				List<String> tempRoleCodeList = new ArrayList<String>();
				for(PmUserRole userRole : userRoles){							
					tempRoleCodeList.add(userRole.getPmRole().getRoleCode());
				}
				//get unique role code
				Set<String> tempRoleCodeSet = new HashSet<String>(tempRoleCodeList);				
				items =  new SelectItem[tempRoleCodeSet.size()];
				String userRole = null;
				getManagedBean().setRoleCodes( new ArrayList<String>(tempRoleCodeSet));
				
				for(int i=0; i<getManagedBean().getRoleCodes().size(); i++){
					userRole = getManagedBean().getRoleCodes().get(i);
					items[i] = new SelectItem(userRole, 
							PmCommonUtil.getRoleDescByRoleCode(userRole));					
				}
		
			}
		}catch(Exception e){
			
		}finally{
			userSO = null;		
		}
		return items;
	}
	
	
	/**
	 * This method used for construct condition fields for LOV (Lov Id :
	 * PM_STATUS_ROLE)
	 **/
	public String constuctConditionFields() {
		IStatusSO statusSO = null;
		StringBuilder sb = null;
		try{
			sb = new StringBuilder();
			StatusReqDTO reqDTO = null;
			if(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null && !getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().isEmpty()){		
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles());
			}else if(getManagedBean().getRoleCodes() != null && !getManagedBean().getRoleCodes().isEmpty()){
				reqDTO = new StatusReqDTO();
				reqDTO.setRoleCodeList(getManagedBean().getRoleCodes());
			}
			
			if(reqDTO != null){
				statusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
				StatusResDTO resDTO =statusSO.retrieveStatusRoleListByRoleCode(reqDTO);
				
				if(resDTO.isSuccessful()){
					List<String> tempDocTypeList = new ArrayList<String>();
					for(PmStatusRole statusRole : resDTO.getStatusRoleList()){							
						tempDocTypeList.add(statusRole.getPmStatus().getDocType());
					}
					//get unique doc type
					Set<String> tempDocTypeSet = new HashSet<String>(tempDocTypeList);
					
					sb.append("pmParameter.parameterType^");
					sb.append(ParameterTypeConst.DOCUMENT_TYPES);
					sb.append("|pmParameter.parameterCode^");
					
					int count = 0;
					for(String docType : tempDocTypeSet){
						if(count != 0){
							sb.append(",");
						}
						sb.append(docType);
						count ++;
					}					
					
					sb.append("^9");
					sb.append("|languageCode^");
					sb.append(new FacesScopes().getUserLocale().getLanguage());
					sb.append("|pmParameter.recordStatusFlg^");
					sb.append(DbValueConst.RECORD_STATUS_ACTIVE);
				}
			}
		}catch(Exception e){
			
		}finally{
			statusSO = null;		
		}

		return sb.toString();
	}
	
	/**
	 * Reset Fields 
	 * **/
	public void onChangeOrg(BaseEntity entity){
		if(getManagedBean().getTaskAssignmentSearchBean().getUser() != null ){
			getManagedBean().getTaskAssignmentSearchBean().setUser(null);
		}
		
		if(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null && !getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().isEmpty()){
			getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().clear();
		}
	}
	
	/**
	 * Reset Fields 
	 * **/
	public void onChangeUser(BaseEntity entity){
		if(getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles() != null && !getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().isEmpty()){
			getManagedBean().getTaskAssignmentSearchBean().getSelectedRoles().clear();
		}
	}
	
	private List<Long> getReceivingOfficerIdListByDeliveryOrderId(Long docId){
		IFulfilmentDocumentSO documentSO = null;
		List<Long> userIds = null;
		try{			
			FlDocumentReqDTO reqDTO = new FlDocumentReqDTO();
			reqDTO.setDocId(docId);
			documentSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentDocumentSO.class);
			FlDocumentResDTO resDTO = documentSO.retrieveReceivingOfficerListByDeliveryOrderId(reqDTO);
			userIds = resDTO.getUserIdList();
		}catch(Exception e){
			
		}finally{
			documentSO = null;		
		}
		
		
		return userIds;
	}
		
}