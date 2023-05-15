/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pm.performanceconfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchOrderBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractIntegrationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.ICtConfigSO;
import my.ep.biz.service.fl.IFulfilmentSO;
import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.taskassginment.ITaskAssignmentSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.biz.service.sc.ISourcingIntegrationSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sm.supplierDataMaintenance.ISupplierDataMaintenanceSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constant.pm.ProfileManagementStatusConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.FLProcessNameConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractIntegrationReqDTO;
import my.ep.dto.ct.ContractIntegrationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ctconfig.CtConfigReqDTO;
import my.ep.dto.ctconfig.CtConfigResDTO;
import my.ep.dto.fl.FulfilmentReqDTO;
import my.ep.dto.fl.FulfilmentResDTO;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentReqDTO;
import my.ep.dto.pm.common.taskassignment.TaskAssignmentResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.dto.sc.SourcingIntegrationReqDTO;
import my.ep.dto.sc.SourcingIntegrationResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.ct.CtConfig;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPendingRemoveTask;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmStatusRole;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pm.TaskAssignmentSearchBean;
import my.ep.web.constant.PagingConst;
import my.ep.web.managedbean.pm.performanceConfiguration.PerformanceConfigurationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pm.helper.StatusHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.PmTaskReassignmentVO;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

/**
 * Backing Bean for Task Reasssingment (new)
 * 
 * @author Basri
 * @since 09.02.2023
 * 
 */
@ManagedBean(name = "performanceConfigurationBackingBean")
@ViewScoped
public class PerformanceConfigurationBackingBean extends
 ContractBaseBackingBean<PerformanceConfigurationManagedBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger(PerformanceConfigurationBackingBean.class);

	// Navigation Details
	public static final String NAV_TO_SEARCH = "performanceConfiguration";


	public PerformanceConfigurationBackingBean(){
		//initiateCtConfigList();
	}
	
	
	public void preLoading()  {
		log.info("preLoading");
		
		try{
		initiateCtConfigList();
		
		getManagedBean().getCtConfigModel().setSource(null);
		getManagedBean().getCtConfigModel().setSource(getSourceCtConfigList());
			
		getManagedBean().getCtConfigModel().setTarget(null);
		getManagedBean().getCtConfigModel().setTarget(getTargetCtConfigList());
		
		
		List<CtContract> lstConfig = getTargetCtConfigList();
		if(!lstConfig.isEmpty()){
			for(CtContract targetConfig : getManagedBean().getCtConfigModel().getTarget()){
				Iterator<CtContract> sourceConfig = getManagedBean().getCtConfigModel().getSource().iterator();
				while(sourceConfig.hasNext()){
					CtContract contract = sourceConfig.next();
					if(contract != null && targetConfig != null && (contract.getContractNo().equalsIgnoreCase(targetConfig.getContractNo()))){
						
						sourceConfig.remove();
						contract.setBrandNew(true);
						
					}else{
						contract.setBrandNew(true);
						//contract.setRemove(true);
					}
				}
				//targetConfig.setRemove(true);
			}
		}else{
			Iterator<CtContract> sourceConfig = getManagedBean().getCtConfigModel().getSource().iterator();
			while(sourceConfig.hasNext()){
				CtContract contract = sourceConfig.next();
				contract.setBrandNew(true);
			}
		}
		//[#25018] - end
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_SEARCH);
		
		}catch(Exception e){
				e.printStackTrace();
			} finally {
				
			}
			
	}

	

	private void initiateCtConfigList() {
		// TODO Auto-generated method stub
		getManagedBean().setCtConfigModel(new DualListModel<CtContract>(new ArrayList<CtContract>(),new ArrayList<CtContract>()));
	}
	
	private List<CtContract> getTargetCtConfigList() {
		ICtConfigSO ctconfigSO = null;
		List<CtContract> targetList = new ArrayList<CtContract>();
		CtConfigReqDTO reqDTO = new CtConfigReqDTO();
		CtConfigResDTO resDTO = new CtConfigResDTO();
		try{
			ctconfigSO = (ICtConfigSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, ICtConfigSO.class);

		
		resDTO = ctconfigSO.targetContractForCtConfig(reqDTO);
		List<CtContract> list = resDTO.getCtConfigList();
		for(CtContract ctConfig : list){
			targetList.add(ctConfig);
		}
		
		}catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}finally{
			ctconfigSO = null;
		}
		return targetList;
		
		
	}
	
	
	/**
	 * The method used to save the ct Config.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this contract
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	@SuppressWarnings("unchecked")
	
	public void saveCtConfig() throws Exception{
		ICtConfigSO ctConfigSO = null;
		
		
		CtConfigReqDTO reqDTO = new CtConfigReqDTO();
		
		reqDTO.setSelectedCtConfig(getManagedBean().getCtConfigModel().getTarget());
		reqDTO.setAvailableCtConfig(getManagedBean().getCtConfigModel().getSource());
		
		ctConfigSO = (ICtConfigSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, ICtConfigSO.class);	
		CtConfigResDTO resDTO = ctConfigSO.saveCtConfigurationContract(reqDTO);	
		
		
	}
	
	private List<CtContract> getSourceCtConfigList(){
		ICtConfigSO ctconfigSO = null;
		List<CtContract> sourceList = new ArrayList<CtContract>();
		CtConfigReqDTO reqDTO = new CtConfigReqDTO();
		CtConfigResDTO resDTO = new CtConfigResDTO();
		try{
			ctconfigSO = (ICtConfigSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, ICtConfigSO.class);

		
		resDTO = ctconfigSO.sourceContractForCtConfig(reqDTO);
		List<CtContract> list = resDTO.getCtConfigList();
		for(CtContract ctConfig : list){
			sourceList.add(ctConfig);
		}
		
		}catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}finally{
			ctconfigSO = null;
		}
		return sourceList;
	}
	
	
	/**
	 * This  method used to initialize approver list.
	 */
//	private void initializeApproverList() {
//		if (getManagedBean().getApproverModel() == null) {
//			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(),  new ArrayList<PmUser>()));
//		}
//	}
	
	




}