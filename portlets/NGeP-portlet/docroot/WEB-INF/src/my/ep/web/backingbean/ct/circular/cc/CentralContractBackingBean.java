/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.cc;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularDetailSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularDetailReqDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.ct.circular.cc.CentralContractManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Central Contract Circular Backingbean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@RequestScoped
public class CentralContractBackingBean extends BaseBackingBean<CentralContractManagedBean>{
	
	private static Logger log = Logger.getLogger(CentralContractBackingBean.class);
	
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	
	public CentralContractBackingBean(){
		if(getManagedBean().getOutComes()==null) {
			getManagedBean().setOutComes(new String[]{"Save","Submit"});
			getManagedBean().setCircularVer(new CtCircularVer());
			PmStatus status = new PmStatus();
			status.setStatusId(ContractStatusConst.CQ_PENDING_SUBMISSION);
			getManagedBean().getCircularVer().setStatus(status);
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		}
		if(getManagedBean().getUserGroupMap()==null) {
			initializeApproverGroup();
		}
		if(isCentralContractAdmin()) {
			getManagedBean().setCircularAdmin(getFacesScopes().getUserSessionBean().getLoginId());
		}
	}
	
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(getManagedBean().getApproverModel()==null) {
				List<String> source = new ArrayList<String>(); 
		        List<String> target = new ArrayList<String>();  
		        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}			
			
			getManagedBean().setUserGroupMap(new HashMap<Long, PmUserGroup>());
			int i = 0;
			for(PmUserGroup userGroup : resDTO.getUserGroupList()) {
				getManagedBean().getUserGroupMap().put(userGroup.getUserGroupId(), userGroup);
				if(i==0) {
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						source.add(user.getPmUser().getUserName());
					}
					getManagedBean().getApproverModel().setSource(source);
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
	}
	
	public SelectItem[] getUserGroupDropdown() {
		SelectItem[] items = null;
		try {
			items = new SelectItem[getManagedBean().getUserGroupMap().values().size()];
			int i = 0;
			for(PmUserGroup userGroup : getManagedBean().getUserGroupMap().values()) {
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public SelectItem[] getZonalLevelDropDown() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, "- Select One -");
		int i = 1;
		 for(SelectItem item : getDropdownCodeValues(ParameterTypeConst.LOCATION_LEVEL)) {
			if(item.getValue()==null) {
				continue;
			}
			if(item.getValue().toString().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE) ||
				item.getValue().toString().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)) {
				items[i++] = item;
			}
		}
		return items;
	}
	
	public void userGroupChangeListener() {
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
	}
	
	public SelectItem[] getStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject("pm",IStatusSO.class);
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			
			List<Long> statusIdList = new ArrayList<Long>();
			statusIdList.add(ContractStatusConst.CQ_PENDING_SUBMISSION);
			statusIdList.add(ContractStatusConst.CQ_PENDING_REVISION);
			statusIdList.add(ContractStatusConst.CQ_PENDING_APPROVAL);
			statusIdList.add(ContractStatusConst.CQ_PENDING_REVISION_APPROVAL);
			statusIdList.add(ContractStatusConst.CQ_APPROVED);
			statusIdList.add(ContractStatusConst.CQ_REJECTED);
			//statusIdList.add(ContractStatusConst.CANCELLED);
			
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
			items = new SelectItem[statusIdList.size()+1];
			int i = 0;
			items[i++] = new SelectItem(null, "- Select One -");
			for(PmStatusDesc statusDesc : resDTO.getStatusDescList()) {
				if(statusIdList.contains(statusDesc.getPmStatus().getStatusId())) {
					items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			statusSO = null;
		}
	    return items;
	}
	
	public void searchContract(){
		IContractSO contractSO = null;
		try { 
			contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.CONTRACT_TYPE));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),ParameterConst.CONTRACT_TYPE_CENTRAL));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.contractVerId",SearchOperator.OPERATOR_IS_NOT_NULL.value(),null));
			
			if(getManagedBean().getSearchContractNo()!=null && getManagedBean().getSearchContractNo().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.contractPhysicalNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchContractNo()));	
			}
			if(getManagedBean().getSearchContractName()!=null && getManagedBean().getSearchContractName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractName()));	
			}
			if(getManagedBean().getCircularVer().getCtCircularContracts().size()>0) {
				List<Long> circularContractIdList = new ArrayList<Long>();
				for(CtCircularContract circularContract : getManagedBean().getCircularVer().getCtCircularContracts()) {
					circularContractIdList.add(circularContract.getCtContract().getContractId());
				}
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractId",SearchOperator.OPERATOR_NOT_IN.value(),circularContractIdList));
			}
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(searchBean);
			reqDTO.setFilterByCircularContract(true);
			
			ContractResDTO resDTO = contractSO.searchContract(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setContractList(resDTO.getContractResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		} finally {
			contractSO = null;
		}
	}
	
	public void searchCircularVer() {
		try { 
			circularVerPagingDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		}			
	}
	
	public void addContactPerson(){
		log.info("CentralContractBackingBean.addContactPerson()");
		try {
			CtContactPerson contactPerson = new CtContactPerson();
	        getManagedBean().getCircularVer().getCtContactPersons().add(contactPerson);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteContactPerson(){
		log.info("CentralContractBackingBean.deleteContactPerson()");
		try {
			getManagedBean().getCircularVer().getCtContactPersons().removeAll(getManagedBean().getSelectedContactPersons());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	public void addExemptedOrg(){
		log.info("CentralContractBackingBean.addExemptedOrg()");
		try {
			CtExemptedOrg ctExemptedOrg = new CtExemptedOrg();
			ctExemptedOrg.setOrgProfile(new PmOrgProfile());
	        getManagedBean().getCircularVer().getCtExemptedOrgs().add(ctExemptedOrg);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteExemptedOrg(){
		log.info("CentralContractBackingBean.deleteExemptedOrg()");
		try {
			getManagedBean().getCircularVer().getCtExemptedOrgs().removeAll(getManagedBean().getSelectedExemptedOrgs());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	public void addCircularContractListener() {
		log.info("CentralContractBackingBean.addCircularContractListener()");
		try {
			getManagedBean().setSearchContractNo(null);
			getManagedBean().setSearchContractName(null);
			getManagedBean().setContractList(new ArrayList<CtContract>());
			getManagedBean().setSelectedContractList(new ArrayList<CtContract>());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCircularContract(){
		log.info("CentralContractBackingBean.deleteCircularContract()");
		try {
			getManagedBean().getCircularVer().getCtCircularContracts().remove(getManagedBean().getSelectedCircularContract());
			if(getManagedBean().getCircularVer().getCtCircularContracts().size()==0) {
    			getManagedBean().getCircularVer().setStartDate(null);
    			getManagedBean().getCircularVer().setEndDate(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	public void addCancelCircularListener() {
		log.info("CentralContractBackingBean.addCancelCircularListener()");
		try {
			if(getManagedBean().getCancelledCircularList()==null) {
				CircularReqDTO reqDTO = new CircularReqDTO();
				ISearchBean<CtCancelCircular> searchBean = new SearchBean<CtCancelCircular>();
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"ctCancelCircularVer.ctCircular.circularType", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
				reqDTO.setCancelCircularSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCancelledCircular(reqDTO);
				if(resDTO.isSuccessful()) {
					getManagedBean().setCancelledCircularList(resDTO.getCancelCircularResultBean().getResultList());
				}
			}
			getManagedBean().setSelectedCancelledCircularList(new ArrayList<CtCancelCircular>());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addCancelledCircular() {
		log.info("CentralContractBackingBean.addCancelledCircular()");
        try {	
        	getManagedBean().getCircularVer().getCtCancelCircularList().addAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelCircular(){
		log.info("CentralContractBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelCirculars());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addSelectedContract(){
		log.info("CentralContractBackingBean.addSelectedContract()");
		IContractSO contractSO = null;
        try {
        	contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
        	ContractReqDTO reqDTO = new ContractReqDTO();
        	
        	CtCircularContract circularContract = null;
        	for(CtContract contract : getManagedBean().getSelectedContractList()) {
        		if(getManagedBean().getCircularVer().getStartDate()==null) {
        			getManagedBean().getCircularVer().setStartDate(contract.getLatestContractVer().getCtContractValue().getEffDate());
        			getManagedBean().getCircularVer().setEndDate(contract.getLatestContractVer().getCtContractValue().getExpDate());
        		}
        		circularContract = new CtCircularContract();
        		
        		reqDTO.setContractId(contract.getContractId());
        		circularContract.setCtContract(contractSO.retrieveContract(reqDTO).getContract());
        		populateItemAndPrice(circularContract);
        		getManagedBean().getCircularVer().getCtCircularContracts().add(circularContract);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	private void populateItemAndPrice(CtCircularContract circularContract) {
		/*CtCircularItem ctCircularItem = null;
		CtCircularItemPrice ctCircularItemPrice = null;
		List<CtCircularItemPrice> circularItemPrices = null;
		List<CtCircularItem> circularItems = circularContract.getCtCircularItems();
		for(CtContractItem ctContractItem : circularContract.getCtContract().getLatestContractVer().getCtContractItems()) {
			ctCircularItem = new CtCircularItem();
			ctCircularItem.setCtContractItem(ctContractItem);
			ctCircularItem.setMaxProcureAmt(new BigDecimal(ctContractItem.getMaxProcureAmt()));
			ctCircularItem.setMinimumOrderQty(new BigDecimal(ctContractItem.getMinimumOrderQty()));
			for(CtItemPrice ctItemPrice : ctContractItem.getCtItemPrices()) {
				ctCircularItemPrice = new CtCircularItemPrice();
				ctCircularItemPrice.setCtCircularZone(new CtCircularZone());
				ctCircularItemPrice.setTierFrom(ctItemPrice.getFromQty());
				ctCircularItemPrice.setTierTo(ctItemPrice.getToQty());
				ctCircularItemPrice.setUnitPrice(ctItemPrice.getUnitPrice());
				circularItemPrices.add(ctCircularItemPrice);
			}
			circularItems.add(ctCircularItem);
		}*/
	}
	
	private class CircularVerPagingDataModel extends PagingDataModel<CtCircularVer> {
		private static final long serialVersionUID = 1L;

		public CircularVerPagingDataModel() {
			super("circularVerDt");
		}
		
		@Override
		public ISearchResultBean<CtCircularVer> pageChangeListener(ISearchBean<CtCircularVer> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.circularType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchCircularNo()));	
				}
				if(getManagedBean().getSearchCircularName()!=null && getManagedBean().getSearchCircularName().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("name",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularName()));	
				}
				if(getManagedBean().getSearchFileRefNo()!=null && getManagedBean().getSearchFileRefNo().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("circularFileRefNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchFileRefNo()));	
				}		
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
					SearchBetweenValue<Date> betweenValue = new SearchBetweenValue<Date>(getManagedBean().getSearchStartDate(), getManagedBean().getSearchEndDate());
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_BETWEEN.value(),betweenValue));
				}	
//				if(getManagedBean().getSearchStatusId()!=null) {
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));	
//				}					
				
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getManagedBean().getSearchStatusId());
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
				if(resDTO.isSuccessful()) {
					return resDTO.getCircularVerResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return null;
		}
	}
	
	public boolean isCentralContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_HQ.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SA.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SK.value());
	}
	
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SA.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SK.value());
	}
	
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_HQ.value())) 
			return RoleCode.BPK_CCA_HQ.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SA.value()))
			return RoleCode.BPK_CCA_SA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA_SK.value()))
			return RoleCode.BPK_CCA_SK.value();		
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) 
			return RoleCode.BPK_APPROVER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SA.value()))
			return RoleCode.BPK_APPROVER_SA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER_SK.value()))
			return RoleCode.BPK_APPROVER_SK.value();	
		else 
			return null;
	}
	
	public String saveCircular(String outcome){
		log.debug("CentralContractBackingBean.saveCircular("+outcome+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			validateCircular(outcome);
			
			if(MDC.get(LogKey.USER_ID)==null) {
				new LogKey(getFacesScopes().getUserSessionBean().getUserId());
			}
			
			CtCircular circular = this.getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			if(circularVer.getDocType()==null) {
				circularVer.setDocType(ContractConst.DOC_TYPE_CENTRAL_CONTRACT_CIRCULAR);
			}
			
			/*if(circularVer.getCtCircularZones().size()>0) {
				Map<String, CtCircularZone> circularZoneMap = new HashMap<String, CtCircularZone>();
				for(CtCircularZone ctCircularZone : circularVer.getCtCircularZones()) {
					circularZoneMap.put(ctCircularZone.toString(), ctCircularZone);
				}
				for(CtCircularContract ctCircularContract : circularVer.getCtCircularContracts()) {
					if(ctCircularContract.getCtCircularZone().getObjectId()==null) {
						ctCircularContract.getCtCircularZone().setObjectId(ctCircularContract.getCtCircularZone().toString());
					}
					if(ctCircularContract.getCtCircularZone()!=null && circularZoneMap.get(ctCircularContract.getCtCircularZone().getObjectId())!=null) {
						ctCircularContract.setCtCircularZone(circularZoneMap.get(ctCircularContract.getCtCircularZone().getObjectId()));
					}					
					for(CtCircularItem ctCircularItem : ctCircularContract.getCtCircularItems()) {
						for(CtCircularItemPrice ctCircularItemPrice : ctCircularItem.getCtCircularItemPrices()) {
							if(ctCircularItemPrice.getCtCircularZone().getObjectId()==null) {
								ctCircularItemPrice.getCtCircularZone().setObjectId(ctCircularItemPrice.getCtCircularZone().toString());
							}
							if(ctCircularItemPrice.getCtCircularZone()!=null && circularZoneMap.get(ctCircularItemPrice.getCtCircularZone().getObjectId())!=null) {
								ctCircularItemPrice.setCtCircularZone(circularZoneMap.get(ctCircularItemPrice.getCtCircularZone().getObjectId()));
							}		
						}
					}
				}
			}*/
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			reqDTO.setOutcome(outcome);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setCircularAdmin(getManagedBean().getCircularAdmin());
			reqDTO.setUserRole(getUserRole());
			if(isBPKApprover()) {
				reqDTO.getCircularVer().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCircularVer().setApprovedDate(new Date());
			}
			
			/*if(circularVer.getPmUserGroup().getUserGroupId()!=null) {
				PmUserGroup pmUserGroup = getManagedBean().getUserGroupMap().get(circularVer.getPmUserGroup().getUserGroupId());
				Map<String, PmUser> userMap = new HashMap<String, PmUser>();
				if(pmUserGroup!=null) {
					for(PmUserGroupUser userGroupUser : pmUserGroup.getPmUserGroupUsers()) {
						userMap.put(userGroupUser.getPmUser().getUserName(), userGroupUser.getPmUser());
					}
				}
				reqDTO.setUserMap(userMap);
			}*/
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String message = "Circular No. : "+circularVer.getCircularNo();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Record saved successfully.",message));
				// save action with task ID not null will remain in the same page
				if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
					(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
				
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setRemarkList(resDTO.getRemarkList());
				getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
				
				ContractBackingBean ctBB = new ContractBackingBean();
				ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());				
			} else {
				throw new EpException("000001");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof EpException) {
				EpException ep = (EpException) e;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, ep.getMessage(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	private void validateCircular(String outcome) {
		ICircularDetailSO circularDetailSO = null;
		circularDetailSO = (ICircularDetailSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularDetailSO.class);
		
		if(getManagedBean().getCircularVer().getCircularFileName()==null) {
			throw new EpException("100022", new String[]{"Circular Letter"});
		}

		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
			if(getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
				throw new EpException("100022", new String[]{"Remark"});
			}
		}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
			if(getManagedBean().getApproverModel().getTarget().size()==0) {
				throw new EpException("100022", new String[]{"Selected approver(s)"});
			}
		}
		
		CircularDetailReqDTO cdReqDTO = new CircularDetailReqDTO();
		cdReqDTO.setCtCircularContractList(getManagedBean().getCircularVer().getCtCircularContracts());
		circularDetailSO.validateSourcingLOA(cdReqDTO);
		
		circularDetailSO = null;
	}

	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
		getManagedBean().getCircularVer().setCircularFileName(item.getFileName());
		getManagedBean().getCircularVer().setCircularFilePath(filePath);
	}
	
	public void downloadFile(int type) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		String filePath = null;
		if(type==1) {
			fileName = getManagedBean().getCircularVer().getCircularFileName();
			filePath = getManagedBean().getCircularVer().getCircularFilePath();
		} 
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if(filePath!=null) {
			InputStream input = null;
			try {
				URLConnection uc = new URL(filePath).openConnection();
				input = new BufferedInputStream(uc.getInputStream()); 
			} catch (Exception e) {
				// Cannot open connection, maybe local file
				input = new FileInputStream(filePath);
			}
			byte[] buffer = new byte[4096];
			int bytesRead;
			while ((bytesRead = input.read( buffer )) != -1){ 
				out.write( buffer, 0, bytesRead ); buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}	
	
	public String gotoPage() {
		String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
		Long circularVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("circularVerId"));
		log.info("Action :"+action+" Id :"+circularVerId);
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			
			CircularResDTO resDTO = null;
			if(action.equalsIgnoreCase("view")) {
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(false);
				getManagedBean().setOutComes(new String[]{});
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				getManagedBean().setCircular(resDTO.getCircular());
				getManagedBean().setCircularVer(resDTO.getCircularVer());
				getManagedBean().setUpdateAction(true);
				getManagedBean().setOutComes(new String[]{"Save","Submit"});
			} else if(action.equalsIgnoreCase("cancel")) {
				
			}
			
			
			/*getManagedBean().getApproverModel().getSource().clear();
			if(resDTO.getCircularVer().getPmUserGroup()!=null) {
				for(PmUserGroupUser user : resDTO.getCircularVer().getPmUserGroup().getPmUserGroupUsers()) {
					getManagedBean().getApproverModel().getSource().add(user.getPmUser().getUserName());
				}
			} else {
				resDTO.getCircularVer().setPmUserGroup(new PmUserGroup());
			}*/
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			return "circularBase";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
		return null;
	}

	public void preRenderView() {
		ICircularSO circularSO = null;
		try {
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(getFacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			System.out.println("taskId:" + taskId);

			if(taskId==null) {
				return;
			}
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin());
			getManagedBean().setTaskId(taskId);
			String[] outcome = new String[res.getAction().length+1];
			outcome[0] = "Save";
			int i = 1;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}
			getManagedBean().setOutComes(outcome);
			
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getDocId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerById(circularReqDTO);
			
			
			/*getManagedBean().getApproverModel().getSource().clear();
			if(resDTO.getCircularVer().getPmUserGroup()!=null) {
				for(PmUserGroupUser user : resDTO.getCircularVer().getPmUserGroup().getPmUserGroupUsers()) {
					getManagedBean().getApproverModel().getSource().add(user.getPmUser().getUserName());
				}
			} else {
				resDTO.getCircularVer().setPmUserGroup(new PmUserGroup());
			}*/
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
			getManagedBean().setCircularVer(resDTO.getCircularVer());
					
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
	}

	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
	
	/*public List<CtCircularZone> getCircularZoneList(String zoneType) {
		List<CtCircularZone> circularZones = new ArrayList<CtCircularZone>();
		List<CtCircularZone> circularZoneList = getManagedBean().getCircularVer().getCtCircularZones();
		if(getManagedBean().getCircularVer().getZonalLevel().getParameterCode()!=null ||
			getManagedBean().getCircularVer().getItemZonalLevel().getParameterCode()!=null) {
			for(CtCircularZone ctCircularZone : circularZoneList) {
				if(ctCircularZone.getZoneType()!=null && ctCircularZone.getZoneType().equalsIgnoreCase(zoneType)) {
					circularZones.add(ctCircularZone);
				}
			}
		} else {
			circularZones = circularZoneList;
		}
		return circularZones;
	}*/

	/*public void selectZoneEvent() {
		getManagedBean().setSelectedCircularZoneDtl(null);
	}
	
	public void selectItemZoneEvent() {
		getManagedBean().setSelectedItemZoneDtl(null);
	}*/
	
	public void addCircularZone(String zoneType){
		log.info("CentralContractBackingBean.addCircularZone()");
       /* try {	
        	CtCircularZone circularZone = new CtCircularZone();
        	circularZone.setZoneType(zoneType);
			getManagedBean().getCircularVer().getCtCircularZones().add(circularZone);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteCircularZone(String zoneType){
		/*log.info("CentralContractBackingBean.deleteCircularZone() : "+getManagedBean().getSelectedCircularZone());
		try {
			getManagedBean().getCircularVer().getCtCircularZones().remove(getManagedBean().getSelectedCircularZone());
			if(zoneType.equalsIgnoreCase(ContractConst.ZONAL_TYPE_CONTRACT)) {
				getManagedBean().setSelectedCircularZone(null);
				getManagedBean().setSelectedCircularZoneDtl(null);
			} else {
				getManagedBean().setSelectedItemZone(null);
				getManagedBean().setSelectedItemZoneDtl(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void addCircularZoneDtl(){
		/*log.info("CentralContractBackingBean.addCircularZoneDtl()");
        try {	
        	CtCircularZoneDtl circularZoneDtl = new CtCircularZoneDtl();
        	circularZoneDtl.setState(new PmState());
        	circularZoneDtl.setDistrict(new PmDistrict());
			getManagedBean().getSelectedCircularZone().getCtCircularZoneDtls().add(circularZoneDtl);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void addItemZoneDtl(){
	/*	log.info("CentralContractBackingBean.addItemZoneDtl()");
        try {	
        	CtCircularZoneDtl circularZoneDtl = new CtCircularZoneDtl();
        	circularZoneDtl.setState(new PmState());
        	circularZoneDtl.setDistrict(new PmDistrict());
			getManagedBean().getSelectedItemZone().getCtCircularZoneDtls().add(circularZoneDtl);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteCircularZoneDtl(){
		/*log.info("CentralContractBackingBean.deleteCircularZoneDtl()");
		try {
			getManagedBean().getSelectedCircularZone().getCtCircularZoneDtls().remove(getManagedBean().getSelectedCircularZoneDtl());
			getManagedBean().setSelectedCircularZoneDtl(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteItemZoneDtl(){
	/*	log.info("CentralContractBackingBean.deleteItemZoneDtl()");
		try {
			getManagedBean().getSelectedItemZone().getCtCircularZoneDtls().remove(getManagedBean().getSelectedItemZoneDtl());
			getManagedBean().setSelectedItemZoneDtl(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public SelectItem[] getStateDropdownValues() {
		SelectItem[] items = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject("pm",IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1l);
			StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
			
		    items = new SelectItem[resDTO.getStateList().size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    int i = 1;
		    for(PmState state: resDTO.getStateList()) {
		    	items[i++] = new SelectItem(state.getStateId(), state.getStateName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public List<SelectItem> getCircularZoneDropdown(String zoneType) {
		List<SelectItem> items = new ArrayList<SelectItem>();
		/*try {
		    items.add(new SelectItem(null, "- Select One -"));
		    for(CtCircularZone circularZone : getManagedBean().getCircularVer().getCtCircularZones()) {
		    	if(circularZone.getObjectId()==null) {
		    		circularZone.setObjectId(circularZone.toString());
		    	}
		    	if(circularZone.getZoneType().equalsIgnoreCase(zoneType)) {
		    		items.add(new SelectItem(circularZone.getObjectId(), circularZone.getName()));
		    	}
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	    return items;
	}

	public void selectCcrContractEvent() {
		getManagedBean().setSelectedCircularItem(null);
	}

	public void selectCircularItemEvent() {
		//getManagedBean().setSelectedCircularItemPrice(null);
	}
	
	public void addCircularItemPrice(){
		/*log.info("CentralContractBackingBean.addCircularItemPrice()");
		try {
			CtCircularItemPrice ctCircularItemPrice = new CtCircularItemPrice();
			ctCircularItemPrice.setCtCircularZone(new CtCircularZone());
			getManagedBean().getSelectedCircularItem().getCtCircularItemPrices().add(ctCircularItemPrice);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}
	
	public void deleteCircularItemPrice(){
		/*log.info("CentralContractBackingBean.deleteCircularItemPrice()");
		try {
			getManagedBean().getSelectedCircularItem().getCtCircularItemPrices().remove(getManagedBean().getSelectedCircularItemPrice());
			getManagedBean().setSelectedCircularItemPrice(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}*/
	}

	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCircularVer().getZoneRemarks());
		initiateRemark(getManagedBean().getCircularVer().getItemRemarks());
	}
	
	private void initiateRemark(List<CtRemark> remarks) {
		if(remarks.size()==0 || !compareUserName(remarks.get(0).getCreatedUser().getLoginId())) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}
	
	public String convertTime(Date time){ 
        String str ="";
    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
        if(time == null)
        	return str;
        return format.format(time); 
    } 
}