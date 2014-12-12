/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.cc;

import java.io.BufferedInputStream;
import java.io.File;
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
import java.util.Calendar;
import java.util.Collections;
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
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.quationtender.AwardSupplierReqDTO;
import my.ep.dto.sc.quationtender.AwardSupplierResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
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
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.circular.cc.CentralContractManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
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
	private static String fileDirectory = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR)+"/CT/Circulars/"+ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR;
	
	public CentralContractBackingBean(){
	
			if(getManagedBean().getOutComes()==null) {
				getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
				getManagedBean().setCircularVer(new CtCircularVer());
				PmStatus status = new PmStatus();
				status.setStatusId(ContractStatusConst.CQ_PENDING_SUBMISSION);
				getManagedBean().getCircularVer().setStatus(status);
				ContractBackingBean ctBB = new ContractBackingBean(fileDirectory);
				ctBB.initializeRemarkAndAttachement(null, getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
				initializeRemarks();
			}
		
			if(isCentralContractAdmin()) 
				getManagedBean().setCircularAdmin(getFacesScopes().getUserSessionBean().getLoginId());
			
			if(getManagedBean().getUserMap().size()==0) {
				 initializeApproverList();
				 getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    }
	}
	
	private FacesScopes getFacesScopes() {
	    	 return new FacesScopes();
	}
	
	public void preRenderCircular() {			
			renewSesionObject();
			getManagedBean().setAutoUpdate(true);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "circularBase.xhtml?faces-redirect=true");
	}
	
	public void preRenderSearch() {
			renewSesionObject();
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "search.xhtml?faces-redirect=true");
	}

	public void preRenderView() {
		ICircularSO circularSO = null;
		try {
			log.info("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(getFacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId==null) {
				return;
			}
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularSO.class);
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
			
			getManagedBean().setCircularAdmin(dataType.getCircularAdmin().getUserName());
			getManagedBean().setTaskId(taskId);
			String[] outcome = new String[res.getAction().length+1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}
			getManagedBean().setOutComes(outcome);
			
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getDocId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCircularVerById(circularReqDTO);
			setContractCircularVar(resDTO);
			getManagedBean().setSelectedTabIndex(0);						
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
	}
	
	public void setContractCircularVar(CircularResDTO resDTO){
		
		    getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
		    getManagedBean().setCircularVer(resDTO.getCircularVer());
		   
		   if (getManagedBean().isUpdateAction()) {
			   IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			   ContractReqDTO contractReqDTO = null;
			   
			   for (CtCircularContract circularContract : getManagedBean().getCircularVer().getCtCircularContracts()) {
				   //Get latest contract version
				   contractReqDTO = new ContractReqDTO();
				   contractReqDTO.setContractId(circularContract.getCtContract().getContractId());
				   CtContractVer ctContractVer = contractSO.getLatestContractVer(contractReqDTO).getCtContractVer();
				   circularContract.setCtContractVer(ctContractVer);
			   }
		   }
		  
		    if(getManagedBean().getCircularVer().getCtCircularContracts() != null && !getManagedBean().getCircularVer().getCtCircularContracts().isEmpty()) {
		    	Collections.sort(getManagedBean().getCircularVer().getCtCircularContracts(), new CtCircularContract());
		     	for(CtCircularContract ctCircularContract : getManagedBean().getCircularVer().getCtCircularContracts()) {
		    		
		    		  if(ctCircularContract.getCtCircularItems() != null && !ctCircularContract.getCtCircularItems().isEmpty())
		    			        Collections.sort(ctCircularContract.getCtCircularItems(), new CtCircularItem());
		    		 
		    		  for(CtCircularItem circularItem : ctCircularContract.getCtCircularItems()){
		    		             if(circularItem.getCtContractItem() != null)
		    		    	            Collections.sort(circularItem.getCtContractItem().getCtItemPrices(), new CtItemPrice());
		    		     }
		         }
		    }
		    
			getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			   getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			   getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			}
		    
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());		
			
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean(fileDirectory);
			ctBB.getManagedBean().setViewContract(this.getManagedBean().isViewContract());
			ctBB.initializeRemarkAndAttachement(resDTO.getCircularVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());	
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
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			
			int i = 1;
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			reqDTO.setDocType(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
		    List<PmStatusDesc> list =  resDTO.getStatusDescList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		      
			for(PmStatusDesc statusDesc : list) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
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
			contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.CONTRACT_TYPE));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),ParameterConst.CONTRACT_TYPE_CENTRAL));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.contractVerId",SearchOperator.OPERATOR_IS_NOT_NULL.value(),null));
			
			if(getManagedBean().getSearchContractNo()!=null && getManagedBean().getSearchContractNo().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchContractNo()));	
			
			}if(getManagedBean().getSearchContractName()!=null && getManagedBean().getSearchContractName().length()>0) {
				SearchFilterBean filterBean = new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),
												getManagedBean().getSearchContractName());
				filterBean.setCaseInsensitive(true);
				searchBean.getDataFilterBeans().add(filterBean);
			}if(getManagedBean().getCircularVer().getCtCircularContracts().size()>0) {
				List<Long> circularContractIdList = new ArrayList<Long>();
				for(CtCircularContract circularContract : getManagedBean().getCircularVer().getCtCircularContracts()) {
					circularContractIdList.add(circularContract.getCtContract().getContractId());
				}searchBean.getDataFilterBeans().add(new SearchFilterBean("contractId",SearchOperator.OPERATOR_NOT_IN.value(),circularContractIdList));
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
	
	public void addCancelCircularListener() {
		log.info("CentralContractBackingBean.addCancelCircularListener()");
		try {
			if(getManagedBean().getCancelledCircularList()==null) {
				CircularReqDTO reqDTO = new CircularReqDTO();
				ISearchBean<CtCancelCircular> searchBean = new SearchBean<CtCancelCircular>();
				searchBean.getDataFilterBeans().add(new SearchFilterBean("circularVer.ctCircular.circularType", 
						SearchOperator.OPERATOR_EQUALS.value(), 
						ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
				reqDTO.setCancelCircularSearchBean(searchBean);
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
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
        	for(CtCancelCircular cancelCircular : getManagedBean().getSelectedCancelledCircularList()) {
        		cancelCircular.setCancelCircularId(null);
        		cancelCircular.setCtCancelCircularReq(null);
        	}
        	getManagedBean().getCircularVer().getCtCancelCircularList().addAll(getManagedBean().getSelectedCancelledCircularList());
        	getManagedBean().getCancelledCircularList().removeAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelCircular(){
		log.info("CentralContractBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelCirculars());
			getManagedBean().getCancelledCircularList().addAll(getManagedBean().getSelectedCancelCirculars());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addSelectedContract() {
		IContractSO contractSO = null;
        
        	
        	if(getManagedBean().getSelectedContractList() == null || getManagedBean().getSelectedContractList().isEmpty()){
        		throw new EpException(MessageConst.WARN_755015);
        	}
        	try {
        	contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
        	ContractReqDTO reqDTO = new ContractReqDTO();
        	
        	CtCircularContract circularContract = null;
        	for(CtContract contract : getManagedBean().getSelectedContractList()) {
        		circularContract = new CtCircularContract();
        		
        		reqDTO.setContractId(contract.getContractId());
        		ContractResDTO resDTO = contractSO.retrieveSelectedContract(reqDTO);
        		circularContract.setCtContract(resDTO.getContract());
        		circularContract.setCtContractVer(resDTO.getContract().getLatestContractVer());
        		getManagedBean().getCircularVer().getCtCircularContracts().add(circularContract);
        		
        		int currentIndex = getManagedBean().getCircularVer().getCtCircularContracts().size() - 1;
        		CtCircularContract ctCircularContract = getManagedBean().getCircularVer().getCtCircularContracts().get(currentIndex);
        		for(CtContractItem ctitems : ctCircularContract.getCtContractVer().getCtContractItems()){
					CtCircularItem item = new CtCircularItem();
					item.setCtContractItem(ctitems);
					ctCircularContract.getCtCircularItems().add(item);
				}
        	}
        	RequestContext.getCurrentInstance().execute("contractDlg.hide()");
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
					SearchFilterBean filterBean = new SearchFilterBean("ctCircular.circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getManagedBean().getSearchCircularName()!=null && getManagedBean().getSearchCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("name",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getManagedBean().getSearchPhysicalCircularNo()!=null && getManagedBean().getSearchPhysicalCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchPhysicalCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getManagedBean().getSearchStartDate()));	
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getManagedBean().getSearchEndDate()));		
				}
				if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()==null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getSearchStartDate()));	
			    }
				if(getManagedBean().getSearchStartDate()==null && getManagedBean().getSearchEndDate()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getSearchEndDate()));	
				}
		    
//				if(getManagedBean().getSearchStatusId()!=null) {
//					searchBean.getDataFilterBeans().add(new SearchFilterBean("status.statusId",SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getSearchStatusId()));	
//				}					
				
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerSearchBean(searchBean);
				reqDTO.setFilterStatusId(getManagedBean().getSearchStatusId());
				
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				CircularResDTO resDTO = circularSO.searchCircularVer(reqDTO);
				if(resDTO.isSuccessful()) {
					if(resDTO.getCircularVerResultBean() != null && resDTO.getCircularVerResultBean().getResultList() != null) {
						for(CtCircularVer circularVer : resDTO.getCircularVerResultBean().getResultList()) {
							/*CircularReqDTO circularReqDTO = new CircularReqDTO();
							circularReqDTO.setCircularId(circularVer.getCtCircular().getCircularId());
							circularVer.getCtCircular().setLatestCircularVer(
									circularSO.getLatestCircularVer(circularReqDTO).getCircularVer());
						*/	
							if(circularVer.getCtCircular().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
								PmStatus cancelStatus = new PmStatus();
								cancelStatus.setStatusId(0L);
								circularVer.setStatus(cancelStatus);
							}
							//if(circularVer.getStatus() != null && circularVer.getStatus().getStatusId().equals(ContractStatusConst.CQ_REJECTED)) {
								if(circularVer.getCtCircular().getLatestCircularVer() != null)
									circularVer.setCircularVerId(circularVer.getCtCircular().getLatestCircularVer().getCircularVerId());
							//}
						}
					}
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
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}
	
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
	
	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) 
			return RoleCode.BPK_CCA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) 
			return RoleCode.BPK_APPROVER.value();
		else 
			return null;
	}
	
	public String saveCircular(String action){
		log.debug("CentralContractBackingBean.saveCircular("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
		
//			if(getManagedBean().getCircularVer().getCircularFileName()==null) {
//				throw new EpException(MessageConst.ERROR_100022, new String[]{"Circular Letter"});
//			}
			
			if( !action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE))
			       validateCircular(action);
			
			CtCircular circular = this.getManagedBean().getCircular();
			if(circular==null) {
				circular = new CtCircular();
				circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			}
			CtCircularVer circularVer = this.getManagedBean().getCircularVer();
			if(circularVer.getDocType()==null) {
				circularVer.setDocType(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
			}
			
			CircularReqDTO reqDTO = new CircularReqDTO();
		    reqDTO.setUserMap(this.getManagedBean().getUserMap());
		    reqDTO.setSelectedApprovers(this.getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(this.getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(this.getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(this.getManagedBean().getAttachmentList());			
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(this.getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			if(isBPKApprover()) {
				circularVer.setApprover(getFacesScopes().getUserSessionBean());
				circularVer.setApprovedDate(new Date());
			}
			
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			
			if(this.getManagedBean().getPmDigiSign() !=null){
				reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
			}
			
			CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
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
								resDTO.getCircular().getCircularNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			     if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else{
					reqDTO = new CircularReqDTO();
					reqDTO.setCircularVerId(resDTO.getCircularVer().getCircularVerId());
					getManagedBean().setUpdateAction(false);
					getManagedBean().setViewContract(false);
					setContractCircularVar(circularSO.retrieveCircularVerById(reqDTO));
				}
				
			  }else {
					throw new EpException("Failed.");
			  }
		  } finally {
			  circularSO = null;
		  }
		return null;
	}
	
	public void validateCircular(String outcome) {
		IQTAwardSO qtAwardSO = null;
		IContractSO contractSO = null;
		try {
			qtAwardSO = (IQTAwardSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTAwardSO.class);
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			getManagedBean().setAutoUpdate(true);
			
			/*	if(getManagedBean().getCircularVer().getCtContactPersons().size()==0) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{"Contact Person Details"});
				}*/
			
				if(getManagedBean().getCircularVer().getCircularFileName()==null) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					    throw new EpException(MessageConst.ERROR_100022, new String[]{"Circular Letter"});
				}
				
				if(getManagedBean().getCircularVer().getCtCircularContracts().size()==0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					throw new EpException(MessageConst.ERROR_100023, new String[]{"Contract List"});
				}
				
				if(getManagedBean().getApproverModel().getTarget().size()==0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Selected approver(s)"});
				}
			//}
			
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				String remark = getManagedBean().getRemarkList().get(0).getRemark();
	 			if((remark == null || remark.length() == 0)) {
	 				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
	 				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
	 			}
			}
			
			AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
			ContractReqDTO contractReqDTO = new ContractReqDTO();
			for(CtCircularContract circularContrart : getManagedBean().getCircularVer().getCtCircularContracts()) {				
				awardSupplierReqDTO.setQtId(circularContrart.getCtContract().getQuotationTender().getQtId());
				AwardSupplierResDTO awardSupplierResDTO = qtAwardSO.retrieveLOILOAListBySupplier(awardSupplierReqDTO);
				
				for(ScLoa scLoa : awardSupplierResDTO.getLoaResultBean().getResultList()) {
					contractReqDTO.setLoaId(scLoa.getLoaId());
					CtContract ctContract = contractSO.retrieveContractByLoaId(contractReqDTO).getContract();
					if(ctContract==null || ctContract.getLatestContractVer()==null) { 
						RequestContext.getCurrentInstance().addCallbackParam("epException", true);
						throw new EpException(MessageConst.ERROR_750005, new String[]{circularContrart.getCtContract().getContractName()});
					}
				}
			}
			
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)
					|| outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)
					|| outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
				// Temp setTextBeforeSign : CircularNo+StartDate+EndDate+Type
				StringBuilder sb = new StringBuilder();
				sb.append(getManagedBean().getCircularVer().getCircularNo());
				sb.append(df.format(getManagedBean().getCircularVer().getStartDate()));
				sb.append(df.format(getManagedBean().getCircularVer().getEndDate()));
				sb.append(outcome);
				getManagedBean().setTextBeforeSign(sb.toString());
				// set action
				getManagedBean().setAction(outcome);
			}
		}
		 finally {
			 qtAwardSO = null;
			 contractSO = null;
		  }
	}

	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = fileDirectory;
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
		getManagedBean().getCircularVer().setCircularFileName(item.getFileName());
		getManagedBean().getCircularVer().setCircularFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
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
			filePath = DocumentRepositoryUtils.getBaseDir() +filePath;
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
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setAction(action);
			CircularResDTO resDTO = null;
			if(action.equalsIgnoreCase("view")) {
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				getManagedBean().setViewContract(true);
				getManagedBean().setUpdateAction(false);
				getManagedBean().setOutComes(new String[]{});
				setContractCircularVar(resDTO);				
			} else if(action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
				resDTO = circularSO.retrieveCircularVerById(reqDTO);
				getManagedBean().setUpdateAction(true);
				getManagedBean().setViewContract(false);
				getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
				setContractCircularVar(resDTO);
			}
			
			getManagedBean().setSelectedCircularContract(null);
			getManagedBean().setSelectedCircularItem(null);
			getManagedBean().setSelectedZoneItems(null);
			
			return "circularBase.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
		return null;
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
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());

			resDTO = userSO.retrieveUserListByRole(reqDTO);
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
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
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
		initiateRemark(getManagedBean().getCircularVer().getContractRemarks());
	}
	
	private void initiateRemark(List<CtRemark> remarks) {
		
		boolean ctWorkflowStatusMatch = false;
		if(getManagedBean().getCircularVer().getCtWorkflowStatus()!=null) {
			if(remarks.size()>0 && remarks.get(0).getCtWorkflowStatus()!=null) {
				if(getManagedBean().getCircularVer().getCtWorkflowStatus().getWorkflowStatusId()==remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}
		
		if(remarks.size()==0 || !ctWorkflowStatusMatch) {
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
		 List<CtItemPrice> itemPriceList = new ArrayList<CtItemPrice>();
	 	 if(this.getManagedBean().getZonePriceMap().size() > 0 && this.getManagedBean().getSelectedZoneItems() != null) {
		           itemPriceList =  getManagedBean().getZonePriceMap().get(this.getManagedBean().getSelectedZoneItems());
		    }else if(this.getManagedBean().getSelectedCircularItem() != null && this.getManagedBean().getSelectedCircularItem().getCtContractItem() != null
		    		&& this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null){
		         itemPriceList = this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices();
	    	}
		return itemPriceList;
	}
	
	public void setContractListner(){
		
		  if(getManagedBean().getSelectedCircularContract() != null && 
			       (getManagedBean().getSelectedCircularContract().getCtCircularItems() == null || getManagedBean().getSelectedCircularContract().getCtCircularItems().isEmpty())) {
					  getManagedBean().getSelectedCircularContract().setCtCircularItems(new ArrayList<CtCircularItem>());
					  for(CtContractItem ctitems : getManagedBean().getSelectedCircularContract().getCtContract().getLatestContractVer().getCtContractItems()){
						CtCircularItem item = new CtCircularItem();
						item.setCtContractItem(ctitems);
						getManagedBean().getSelectedCircularContract().getCtCircularItems().add(item);
					}
				}
			getManagedBean().setSelectedCircularItem(null);
		 	getManagedBean().setZonePriceMap(null);
			getManagedBean().setChkItemZone(false);
			getManagedBean().setChkItemPrice(false);
	}
	
	public List<CtCircularItem> getActiveCircularItem() {
		List<CtCircularItem> activeItemList = new ArrayList<CtCircularItem>();
		Date currentDate = new Date();
		
		for(CtCircularItem circularItem : getManagedBean().getSelectedCircularContract().getCtCircularItems()) {
			for(CtContractItem contractItem : getManagedBean().getSelectedCircularContract().getCtContract().getLatestContractVer().getCtContractItems()){
				if (circularItem.getCtContractItem().getScRequestItem().getRequestNoteItemId().equals(contractItem.getScRequestItem().getRequestNoteItemId())) {
					if(contractItem.isRecordStatus() || (!contractItem.isRecordStatus() && (currentDate.before(contractItem.getEffDate()) || currentDate.after(contractItem.getExpDate())))) {
						activeItemList.add(circularItem);						
					}
					break;
				}
			}
		}
		return activeItemList;
	}
	
	public void setZoneListener(){
		
		getManagedBean().setZonePriceMap(null);
		getManagedBean().setChkItemZone(false);
		getManagedBean().setChkItemPrice(false);
		 if(getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null
				   && !getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices().isEmpty()){
				   for(CtItemPrice itemPrices : this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices()){
					 if(itemPrices.getCtContractZone() != null){
					    this.getManagedBean().setChkItemZone(true);
					    break;
					 }
				  }
			   }if(this.getManagedBean().isChkItemZone()){
					  getContractZoneList();
				  }else{
					    this.getManagedBean().setChkItemPrice(true);
				  } 
	}
	
	public void setPriceListener(){
		this.getManagedBean().setChkItemPrice(true);
		retriveItemZonesList();
	}
	
	public void getContractZoneList(){
		
		this.getManagedBean().setContractItemZones(new ArrayList<CtContractZone>());
		List<CtItemPrice> itemPriceList = new ArrayList<CtItemPrice>();
		   if(this.getManagedBean().getSelectedCircularItem() != null && this.getManagedBean().getSelectedCircularItem() != null
				&& this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null){
			       for(CtItemPrice price :this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices()){
			    	   this.getManagedBean().getContractItemZones().add(price.getCtContractZone()); 
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
			s.addAll(this.getManagedBean().getContractItemZones());         
			this.getManagedBean().getContractItemZones().clear();
			this.getManagedBean().getContractItemZones().addAll(s); 
			
		  } 
	}
	
   public void retriveItemZonesList() {
		
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;
		if(this.getManagedBean().getSelectedZoneItems() != null) {
			   zoneNode = new DefaultTreeNode("zone", new CentralItemZones(this.getManagedBean().getSelectedZoneItems().getZoneName()), rootLocationTree);
			   TreeNode stateNode = null;
			   List<PmState> state = new ArrayList<PmState>();
			   if(this.getManagedBean().getSelectedZoneItems().getCtContractZoneDtls() != null 
					   && this.getManagedBean().getSelectedZoneItems().getCtContractZoneDtls().size() > 0) {
				 for(CtContractZoneDtl zoneDtl : this.getManagedBean().getSelectedZoneItems().getCtContractZoneDtls()) {
						 if(zoneDtl.getState() != null){
							  if(!state.contains(zoneDtl.getState())){
								  stateNode = new DefaultTreeNode("state",new CentralItemZones(zoneDtl.getState()) , zoneNode);
							   }state.add(zoneDtl.getState());
						    } 
				    if(zoneDtl.getDistrict() != null){
					   new DefaultTreeNode("district", new CentralItemZones(zoneDtl.getDistrict()), stateNode);
				   }
				 }
			  }
			}
		 this.getManagedBean().setCentralZoneTrees(rootLocationTree);
	  }
   
   public String backAction() {
	   if(getManagedBean().isChkCancelPage()) {
		   getManagedBean().setChkCancelPage(false);
		   return "/xhtml/ct/circular/panel/cancelCircularBase.xhtml?faces-redirect=true";
	   }
	   else {
		   return "search.xhtml?faces-redirect=true";
	   }
	}
   
   public void searchReset(){
		this.getManagedBean().setSearchCircularNo(null);
		this.getManagedBean().setSearchCircularName(null);
		this.getManagedBean().setSearchPhysicalCircularNo(null);
		this.getManagedBean().setSearchStartDate(null);
		this.getManagedBean().setSearchEndDate(null);
		this.getManagedBean().setSearchStatusId(null);
	}
   
   public void searchCircularVer() throws Exception {
		try {
			if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) &&
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) && 
				(getManagedBean().getSearchPhysicalCircularNo() == null || getManagedBean().getSearchPhysicalCircularNo().length() == 0) && 
				getManagedBean().getSearchStartDate() == null &&
				getManagedBean().getSearchEndDate() == null &&
				getManagedBean().getSearchStatusId() == null) {
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_755000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				circularVerPagingDataModel.reloadData();
			}
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
	
	public void addExemptedOrg(List<PmOrgProfile> orgProfiles){
		log.info("CentralContractBackingBean.addExemptedOrg()");
		try {
			if (orgProfiles != null){
				
				List<CtExemptedOrg> exemptedOrgList = new ArrayList<CtExemptedOrg>();
				for (PmOrgProfile orgProfile : orgProfiles) {
					CtExemptedOrg ctExemptedOrg = new CtExemptedOrg();
					ctExemptedOrg.setOrgProfile(orgProfile);
					ctExemptedOrg.setCtCircularVer(getManagedBean().getCircularVer());
					exemptedOrgList.add(ctExemptedOrg);
					
					//Remove if already exist, avoid duplicate
					for(CtExemptedOrg exemptedOrg : getManagedBean().getCircularVer().getCtExemptedOrgs()) {
						if(exemptedOrg.getOrgProfile().getOrgProfileId().equals(orgProfile.getOrgProfileId())) {
							exemptedOrgList.remove(ctExemptedOrg);
						}
					}
				}
				getManagedBean().getCircularVer().getCtExemptedOrgs().addAll(exemptedOrgList);
			}
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
			getManagedBean().setAutoUpdate(false);
			getManagedBean().setSearchContractNo(null);
			getManagedBean().setSearchContractName(null);
			getManagedBean().setContractList(new ArrayList<CtContract>());
			getManagedBean().setSelectedContractList(new ArrayList<CtContract>());
			getManagedBean().setContractList(new ArrayList<CtContract>());
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
   			this.getManagedBean().setSelectedCircularItem(null);
				this.getManagedBean().setSelectedZoneItems(null);
				this.getManagedBean().setSelectedCircularContract(null);
			}
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void extendStartEndDate(){
		if(this.getManagedBean().getCircularVer().getExtPeriod() > 0){
			Calendar c = Calendar.getInstance();
			c.setTime(getManagedBean().getCircularVer().getEndDate());
			c.add(Calendar.DATE, 1);
			getManagedBean().getCircularVer().setExtendStartDate(c.getTime());
			
			c.setTime(getManagedBean().getCircularVer().getEndDate());
			c.add(Calendar.MONTH, getManagedBean().getCircularVer().getExtPeriod());
			getManagedBean().getCircularVer().setExtendEndDate(c.getTime());
		}else{
			getManagedBean().getCircularVer().setExtendStartDate(null);
			getManagedBean().getCircularVer().setExtendEndDate(null);
		}
	}
	
	public String preSubmitCircular(PmDigiSign pmDigiSign) {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveCircular(getManagedBean().getAction());
	}
	
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}
	
	public String getStatusDesc(CtCircularVer circularVer) {
		if(circularVer.getCtCircular()!= null && circularVer.getCtCircular().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
			return getStatusDescById(ContractStatusConst.CR_APPROVED);
		}
		else {
			return getStatusDescById(circularVer.getStatus().getStatusId());
		}
	}
}