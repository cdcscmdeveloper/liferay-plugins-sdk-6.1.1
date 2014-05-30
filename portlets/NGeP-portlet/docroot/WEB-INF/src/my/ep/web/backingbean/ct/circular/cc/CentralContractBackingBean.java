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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
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
import my.ep.persistence.entity.ct.circular.cc.CtCircularItem;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
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
import my.ep.web.constant.CommonUIConst;
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
	
	private List<CtContractZone> contractItemZones;
	private List<CtCircularItem> ctCircularItemList;
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
		
		if(isCentralContractAdmin()) 
			getManagedBean().setCircularAdmin(getFacesScopes().getUserSessionBean().getLoginId());
		
		if(getManagedBean().getUserMap().size()==0) {
			 initializeApproverList();
			 this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		}
	}
	
	private FacesScopes getFacesScopes() {
		return new FacesScopes();
	}
	
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractPhysicalNo",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchContractNo()));	
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
    			this.getManagedBean().setSelectedCircularItem(null);
				this.getManagedBean().setSelectedZoneItems(null);
				this.getManagedBean().setSelectedCircularContract(null);
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircularVer.ctCircular.circularType", 
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
        		getManagedBean().getCircularVer().getCtCircularContracts().add(circularContract);
        	}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
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
	
	public String saveCircular(String action){
		log.debug("CentralContractBackingBean.saveCircular("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			//validateCircular(outcome);
			
			CtCircular circular = this.getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			if(circularVer.getDocType()==null) {
				circularVer.setDocType(ContractConst.DOC_TYPE_CENTRAL_CONTRACT_CIRCULAR);
			}
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			if(isBPKApprover()) {
				reqDTO.getCircularVer().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCircularVer().setApprovedDate(new Date());
			}
			
			reqDTO.setCircularVer(circularVer);
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			if(resDTO.isSuccessful()) {
				if(resDTO.isSuccessful()) {
					log.info("Record saved successfully");
					String messageId = MessageConst.INFO_700005;
					if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
						messageId = MessageConst.INFO_700025;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
						messageId = MessageConst.INFO_700009;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						messageId = MessageConst.INFO_700007;
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
						messageId = MessageConst.INFO_700008;
					} 
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getCircularVer().getDocType()), 
								this.getManagedBean().getCircularVer().getCircularNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			    initializeRemarks();
			  }else {
					throw new EpException("Failed.");
			  }
			}
		  }catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			if(e instanceof EpException) {
				EpException ep = (EpException) e;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		  }finally {
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
			
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
			return "circularBase.xhtml?faces-redirect=true";
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
			
			getManagedBean().setCircularAdmin(dataType.getContractAdmin().getUserName());
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
			getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
			getManagedBean().setCircularVer(resDTO.getCircularVer());
			
			  this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
			    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
				   this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
				   this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
				} 
		        this.getManagedBean().setRemarkList(resDTO.getRemarkList());
				this.getManagedBean().setAttachmentList(resDTO.getAttachmentList());
				
				initializeRemarks();
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
	
	private List<String> getAvailableApproverList() {
		IUserSO userSO = null;
		List<String> sourceList = new ArrayList<String>();
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();

				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
				if(getFacesScopes().getUserSessionBean().getPmUserOrgs() != null && getFacesScopes().getUserSessionBean().getPmUserOrgs().size() > 0){
					reqDTO.setOrgProfileId(getFacesScopes().getUserSessionBean().getPmUserOrgs().get(0).getPmOrgProfile().getOrgProfileId());
				}// TODO hardcode to get the value, should remove after have real data
				reqDTO.setOrgProfileId(1003L);
			

			UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
			List<PmUser> list = resDTO.getUserList();
			
			for (PmUser user : list) {
				sourceList.add(user.getUserName());
				getManagedBean().getUserMap().put(user.getUserName(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
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

	public void selectCcrContractEvent() {
		//getManagedBean().setSelectedCircularItem(null);
		this.getManagedBean().getSelectedCircularContract();
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

	public List<CtItemPrice> getItemPrice() {
		 List<CtItemPrice> itemPriceList;
		if(this.getManagedBean().getZonePriceMap().size() > 0) {
		  itemPriceList =  getManagedBean().getZonePriceMap().get(this.getManagedBean().getSelectedZoneItems());
		}else{
		  itemPriceList = this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices();
		}
		return itemPriceList;
	}
	
	public void setContractListner(){
		
		if(this.getManagedBean().getSelectedCircularContract().getCtCircularItems() == null)
			this.getManagedBean().getSelectedCircularContract().setCtCircularItems(new ArrayList<CtCircularItem>());
		else
			this.getManagedBean().getSelectedCircularContract().getCtCircularItems().clear();
			
		if(this.getManagedBean().getSelectedCircularContract() != null){
			for(CtContractItem ctitems :this.getManagedBean().getSelectedCircularContract().getCtContract().getLatestContractVer().getCtContractItems()){
				CtCircularItem item = new CtCircularItem();
				item.setCtContractItem(ctitems);
				this.getManagedBean().getSelectedCircularContract().getCtCircularItems().add(item);
			}
		}
		this.getManagedBean().setSelectedCircularItem(null);
		this.getManagedBean().setZonePriceMap(null);
		this.getManagedBean().setChkItemZone(false);
		this.getManagedBean().setChkItemPrice(false);
	}
	
	public void setZoneListener(){
		this.getManagedBean().setZonePriceMap(null);
		this.getManagedBean().setChkItemZone(false);
		 this.getManagedBean().setChkItemPrice(false);
		if(this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null
		   && this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices().size() > 0){
		   for(CtItemPrice itemPrices : this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices()){
			 if(itemPrices.getCtContractZone() != null){
			    this.getManagedBean().setChkItemZone(true);
			    break;
			 }
		  }if(this.getManagedBean().isChkItemZone()){
			  getContractZoneList();
		  }else{
			    this.getManagedBean().setChkItemPrice(true);
		  }
	   }	
	}
	
	public void setPriceListener(){
		this.getManagedBean().setChkItemPrice(true);
		this.getManagedBean().getSelectedZoneItems();
	}
	
	public void getContractZoneList(){
		
		contractItemZones = new ArrayList<CtContractZone>();
		List<CtItemPrice> itemPriceList = new ArrayList<CtItemPrice>();
		   if(this.getManagedBean().getSelectedCircularItem() != null && this.getManagedBean().getSelectedCircularItem() != null
				&& this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null){
		       for(CtItemPrice price :this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices()){
			    contractItemZones.add(price.getCtContractZone());
			    if(getManagedBean().getZonePriceMap().containsKey(price.getCtContractZone())) {
				  itemPriceList = getManagedBean().getZonePriceMap().get(price.getCtContractZone());
				  itemPriceList.add(price);
				  getManagedBean().getZonePriceMap().put(price.getCtContractZone(), itemPriceList);
			  } else {
				  itemPriceList = new ArrayList<CtItemPrice>();
				  itemPriceList.add(price);
				  getManagedBean().getZonePriceMap().put(price.getCtContractZone(), itemPriceList);
			  }
			  
			}
		    Set<CtContractZone> s= new HashSet<CtContractZone>();
			s.addAll(contractItemZones);         
			contractItemZones = new ArrayList<CtContractZone>();
			contractItemZones.addAll(s); 
		  } 
	}
	
	
	public List<CtCircularItem> getCtCircularItemList() {
		return ctCircularItemList;
	}

	public void setCtCircularItemList(List<CtCircularItem> ctCircularItemList) {
		this.ctCircularItemList = ctCircularItemList;
	}

	public List<CtContractZone> getContractItemZones() {
		return contractItemZones;
	}

	public void setContractItemZones(List<CtContractZone> contractItemZones) {
		this.contractItemZones = contractItemZones;
	}
}