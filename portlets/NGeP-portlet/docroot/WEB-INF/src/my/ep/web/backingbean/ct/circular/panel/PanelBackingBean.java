/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.circular.panel;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
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
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
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
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

/**
 * Circular Panel Backingbean
 * 
 * @author alam
 * @Created 9/10/2013
 */
@ManagedBean
@RequestScoped
public class PanelBackingBean extends BaseBackingBean<PanelManagedBean>{
	
	private static Logger log = Logger.getLogger(PanelBackingBean.class);
	private IPagingDataModel<CtCircularVer> circularVerPagingDataModel = new CircularVerPagingDataModel();
	private static String baseDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
	
	public PanelBackingBean(){
		 
		if(getManagedBean().getOutComes()==null) {
				getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
				getManagedBean().setCircularVer(new CtCircularVer());
				getManagedBean().setCancelCircularReq(new CtCancelCircularReq());
				PmStatus status = new PmStatus();
				status.setStatusId(ContractStatusConst.PQ_PENDING_SUBMISSION);
				getManagedBean().getCircularVer().setStatus(status);
				status = new PmStatus();
				status.setStatusId(ContractStatusConst.CR_PENDING_SUBMISSION);
				getManagedBean().getCancelCircularReq().setStatus(status);
				
				initializeRemarks();
				ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
				ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		
	    	} if(getManagedBean().getUserMap().size()==0) {
			   initializeApproverList();
			   this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());	
	       
	    	} if(getManagedBean().getCircularDocType()==null) {
			      getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
		
	    	} if(isPanelAdmin() || isCentralContractAdmin()) {
			      FacesScopes facesScopes = new FacesScopes();
			      getManagedBean().setCircularAdmin(facesScopes.getUserSessionBean().getLoginId());
		    }
	  }
	
	  private FacesScopes getFacesScopes() {
		     return new FacesScopes();
	  }
	
	  public void preRenderCircular() {
		    renewSesionObject();
		    getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "circularBase.xhtml?faces-redirect=true");
	  }
	
	public void preRenderSearch() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "search.xhtml?faces-redirect=true");
	}
	
	public void preRenderView() {
		ICircularSO circularSO = null;
		try {
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId==null) 
			return;
			
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			getManagedBean().setTaskId(taskId);
			getManagedBean().setSelectedTabIndex(0);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularContractDataType dataType = (CTCircularContractDataType) obj;
			getManagedBean().setCircularAdmin(dataType.getCircularAdmin().getUserName());
			
			String[] outcome = new String[res.getAction().length+1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}getManagedBean().setOutComes(outcome);			
			
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getDocId().longValue());
			setPanelCircularVar(circularSO.retrieveCircularVerById(circularReqDTO));
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
	}
	
	public void setPanelCircularVar(CircularResDTO resDTO){
		
		getManagedBean().setCircularVer(resDTO.getCircularVer());
		getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
		
		if(getManagedBean().getCircularVer().getCtPanelItems() != null && getManagedBean().getCircularVer().getCtPanelItems().size() > 0){
			Collections.sort(getManagedBean().getCircularVer().getCtPanelItems(), new CtPanelItem());
			for(CtPanelItem panelItem : getManagedBean().getCircularVer().getCtPanelItems()) {
				  if(panelItem.getCtPanelZones() != null && panelItem.getCtPanelZones().size() > 0){
						Collections.sort(panelItem.getCtPanelZones(), new CtPanelZone());
				         for(CtPanelZone panelZone : panelItem.getCtPanelZones()){
				        	 Collections.sort(panelZone.getCtPanelSuppliers(), new CtPanelSupplier()); 
				         }
				  }
				 if(panelItem.getCtPanelSuppliers() != null && panelItem.getCtPanelSuppliers().size() > 0)
			  		   Collections.sort(panelItem.getCtPanelSuppliers(), new CtPanelSupplier());  
		 	}
		}
		
		getManagedBean().setRemarkList(resDTO.getRemarkList());
		getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		
		getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		if(resDTO.getApproverList()!=null && resDTO.getApproverList().size()>0) {
			getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
		}

		getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		initializeRemarks();
		ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
	}
	
	public void preRenderCancelCircular() {
		renewSesionObject();
		getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "cancelCircularBase.xhtml?faces-redirect=true");
	}
	
	public void preRenderViewCancelReq() {
		ICircularSO circularSO = null;
		try {
		
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId==null) {
				if(isPanelAdmin() || isCentralContractAdmin()) {
					getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SUBMIT});	
				}
				return;
			}
			
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject("ct",ICircularSO.class);
			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();			
			CTCircularCancellationDataType dataType = (CTCircularCancellationDataType) obj;
			
			getManagedBean().setCircularAdmin(dataType.getCircularAdmin().getUserName());
			getManagedBean().setTaskId(taskId);
			String[] outcome = new String[res.getAction().length];
			int i = 0;
			for(String action : res.getAction()) {
				outcome[i++]= action;
			}getManagedBean().setOutComes(outcome);	
			
			CircularReqDTO cancelReqDTO = new CircularReqDTO();
			cancelReqDTO.setCancelCircularReqId(dataType.getDocId().longValue());
			
			CircularResDTO resDTO = circularSO.retrieveCancelCircularReqById(cancelReqDTO);
			getManagedBean().setCancelCircularReq(resDTO.getCancelCircularReq());
			List <CtCancelCircular> ctCancelCircularList = resDTO.getCancelCircularReq().getCtCancelCircularList();
			if(ctCancelCircularList.get(0).getCircularVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CENTRAL_CONTRACT_CIRCULAR))
				getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
		    else 
				getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			
			getManagedBean().getApproverModel().setSource(getAvailableApproverList());
			if(resDTO.getApproverList()!=null && resDTO.getApproverList().size()>0) {
				getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
				getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			}
			
			getManagedBean().setRemarkList(resDTO.getRemarkList());
			getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		
			getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
			initializeRemarks();
			ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}		
	}
	
	public String backAction() {
		return "search.xhtml?faces-redirect=true";
	}
	
	public void searchReset(){
		this.getManagedBean().setSearchCircularNo(null);
		this.getManagedBean().setSearchCircularName(null);
		this.getManagedBean().setSearchSupplierName(null);
		this.getManagedBean().setSearchFileRefNo(null);
		this.getManagedBean().setSearchItemName(null);
		this.getManagedBean().setSearchStartDate(null);
		this.getManagedBean().setSearchEndDate(null);
		this.getManagedBean().setSearchStatusId(null);
	}
	
	public void searchCircularVer() throws Exception {
		try {
			if ((getManagedBean().getSearchCircularNo() == null || getManagedBean().getSearchCircularNo().length() == 0) && 
				(getManagedBean().getSearchCircularName() == null || getManagedBean().getSearchCircularName().length() == 0) &&
				(getManagedBean().getSearchSupplierName() == null || getManagedBean().getSearchSupplierName().length() == 0) &&
				(getManagedBean().getSearchFileRefNo() == null || getManagedBean().getSearchFileRefNo().length() == 0) &&
				(getManagedBean().getSearchItemName() == null || getManagedBean().getSearchItemName().length() == 0) &&
				getManagedBean().getSearchStartDate() == null &&
				getManagedBean().getSearchEndDate() == null &&
				getManagedBean().getSearchStatusId() == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_751000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				circularVerPagingDataModel.reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "circular search failed."));
		}			
	}
	
	public void searchActiveCircularVer(ActionEvent event) {
		ICircularSO circularSO = null;
		try {
			getManagedBean().setSelectedCircularVerList(null);
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);

			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularType(getManagedBean().getCircularDocType());
			CircularResDTO resDTO = circularSO.retrieveActiveCircularVerList(reqDTO);
			if(resDTO.isSuccessful()) {
				getManagedBean().setCircularVerList(resDTO.getCircularVerList());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Circular save failed."));
		} finally {
			circularSO = null;
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctCircular.circularType",SearchOperator.OPERATOR_EQUALS.value(),ContractConst.CIRCULAR_DOC_TYPE_PANEL));
				if(getManagedBean().getSearchCircularNo()!=null && getManagedBean().getSearchCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",
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
				if(getManagedBean().getSearchSupplierName()!=null && getManagedBean().getSearchSupplierName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctPanelItems.ctPanelSuppliers.supplier.companyName",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}			
				if(getManagedBean().getSearchFileRefNo()!=null && getManagedBean().getSearchFileRefNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularFileRefNo",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchFileRefNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}	
				if(getManagedBean().getSearchItemName()!=null && getManagedBean().getSearchItemName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("ctPanelItems.itemName",
													SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getSearchItemName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				
				}if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()!=null) {
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getManagedBean().getSearchStartDate()));	
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getManagedBean().getSearchEndDate()));		
			
			    }if(getManagedBean().getSearchStartDate()!=null && getManagedBean().getSearchEndDate()==null) {
				   searchBean.getDataFilterBeans().add(new SearchFilterBean("startDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getSearchStartDate()));	
			
			    }if(getManagedBean().getSearchStartDate()==null && getManagedBean().getSearchEndDate()!=null) {
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("endDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getSearchEndDate()));	
			    }
				
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
							circularVer.getCtCircular().setLatestCircularVer(circularSO.getLatestCircularVer(circularReqDTO).getCircularVer());
							*/
							if(circularVer.getCtCircular().getRecordStatusFlg() == DbValueConst.RECORD_STATUS_CANCELLED) {
								PmStatus cancelStatus = new PmStatus();
								cancelStatus.setStatusId(0L);
								circularVer.setStatus(cancelStatus);
							
						  }if(circularVer.getStatus() != null && circularVer.getStatus().getStatusId().equals(ContractStatusConst.PQ_REJECTED)) {
								if(circularVer.getCtCircular().getLatestCircularVer() != null)
									circularVer.setCircularVerId(circularVer.getCtCircular().getLatestCircularVer().getCircularVerId());
						  }
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
	
	public String savePanelCircular(String action) {
		log.debug("PanelBackingBean.savePanelCircular("+action+")");

		ICircularSO circularSO = null;
		try {
			
			if(getManagedBean().getCircularVer().getCircularFileName()==null) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Circular Letter"});
			}
			
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
			      validateCircular(action);
			}
			  circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			  if(MDC.get(LogKey.USER_ID)==null) {
				new LogKey(getFacesScopes().getUserSessionBean().getUserId());
			  }
			
			CtCircular circular = getManagedBean().getCircular();
			if(circular==null){
				circular = new CtCircular();
				circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			}
			CtCircularVer circularVer = getManagedBean().getCircularVer();
			if(circularVer.getDocType()==null){
				circularVer.setDocType(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
			}
			
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getApproverModel().getTarget().size()==0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select at least one approver.",""));
				return null;
			}
            
			CircularReqDTO reqDTO = new CircularReqDTO();
		    reqDTO.setUserMap(getManagedBean().getUserMap());
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
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			if(isBPKApprover()) {
				circularVer.setApprover(getFacesScopes().getUserSessionBean());
				circularVer.setApprovedDate(new Date());
			}
			
			
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
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
								this.getManagedBean().getCircularVer().getCircularNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else{
			        getManagedBean().setCircularVer(resDTO.getCircularVer());
					getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
					getManagedBean().setSelectionOfZone(false);
					getManagedBean().setSelectedPanelItems(null);
					getManagedBean().setSelectedPanelZone(null);
					getManagedBean().setUpdateAction(false);
					initializeRemarks();
					ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
					ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
				}
			}
		  }finally {
			  circularSO = null;
		  }
		
		return "circularBase.xhtml";
	}
	
	public String saveCancelCircularReq(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICircularSO.class);
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				if(getManagedBean().getCancelCircularReq().getCtCancelCircularList().size()==0) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{"Circular(s) to be cancel"});
				}
				
				if(getManagedBean().getApproverModel().getTarget().size()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Selected approver(s)"});
				}
			}
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				if(getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
				}
			}
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCancelCircularReq(getManagedBean().getCancelCircularReq());
		    reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());		
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());			
			reqDTO.setDocType(getManagedBean().getCircularDocType());
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			if(isBPKApprover()) {
				reqDTO.getCancelCircularReq().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCancelCircularReq().setApprovedDate(new Date());
			}
			
			CircularResDTO resDTO = circularSO.saveCancelCircularRequest(reqDTO);
			if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} 
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST), 
								reqDTO.getDocType()+"-"+resDTO.getCancelCircularReq().getCancelCircularReqId().toString()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				throw new EpException("000001");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof EpException) {
				EpException ep = (EpException) e;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage(ep.getMessage(), ep.getParameters(), MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), MessageUtils.getMessage("000001", null, MessageUtils.BUNDLE_COMMON_MESSAGES, getFacesScopes().getUserLocale())));
			}
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	private void validateCircular(String outcome) {
	
			
			/*if(getManagedBean().getCircularVer().getCtContactPersons().size()==0) {
				throw new EpException(MessageConst.ERROR_100023, new String[]{"Contact Person Details"});
			}*/
			
			if(getManagedBean().getCircularVer().getCtPanelItems().size()==0) {
				throw new EpException(MessageConst.ERROR_100023, new String[]{"Panel Item"});
			}
			else {
				for (CtPanelItem panelItem: getManagedBean().getCircularVer().getCtPanelItems()) {
					if(panelItem.getPanelCoverageId().getParameterId()==null) {
						throw new EpException(MessageConst.ERROR_100022, new String[]{"Panel Coverage"});
					}
					
					if(panelItem.getItemCategoryId().getParameterId()==null) {
						throw new EpException(MessageConst.ERROR_100022, new String[]{"Item Category"});
					}
					
					if(panelItem.getItemName().length()==0) {
						throw new EpException(MessageConst.ERROR_100022, new String[]{"Item Name"});
					}
					
					if(panelItem.getPmUom().getUomId()==null) {
						throw new EpException(MessageConst.ERROR_100022, new String[]{"UOM"});
					}
					
					if(getItemGeneralCode(panelItem.getPanelCoverageId().getParameterId()).equalsIgnoreCase(
							ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)) {
						if(panelItem.getLocationLevelId().getParameterId()==null) {
							throw new EpException(MessageConst.ERROR_100022, new String[]{"Location Level"});
						}
						
						if(panelItem.getCtPanelZones().size()==0) {
							throw new EpException(MessageConst.ERROR_100023, new String[]{"Zone"});
						}
						else {
							for (CtPanelZone panelZone: panelItem.getCtPanelZones()) {
								if(panelZone.getName().length()==0) {
									throw new EpException(MessageConst.ERROR_100022, new String[]{"Zone Name"});
								}
								
								if(panelZone.getCtPanelSuppliers().size()==0) {
									throw new EpException(MessageConst.ERROR_100023, new String[]{"Supplier"});
								}
							}
						}
					}
					else {					
						if(panelItem.getCtPanelSuppliers().size()==0) {
							throw new EpException(MessageConst.ERROR_100023, new String[]{"Supplier"});
						}
					}
				}
			}
			
			if(getManagedBean().getApproverModel().getTarget().size()==0) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Selected approver(s)"});
			}
		//}
		
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			if(getManagedBean().getCircularVer().getCircularRemarks().get(0).getRemark().length()==0 &&
				(getManagedBean().getCircularVer().getItemRemarks().get(0).getRemark() == null || 
					getManagedBean().getCircularVer().getItemRemarks().get(0).getRemark().length()==0 )) {
				throw new EpException("100022", new String[]{"Circular Details/Item Remark"});
			}
		}
	}
	
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
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
	
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCircularVer().getCircularRemarks());
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

	public boolean isPanelAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value());
	}
	
	public boolean isCentralContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}
	
	public boolean isBPKApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
	
	private String getUserRole() {
		if(getManagedBean().getCircularDocType()!=null) { // Cancellation Request
			if(getManagedBean().getCircularDocType()!=null && getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_PANEL)) {
				if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) 
					return RoleCode.BPK_PCA.value();
			} else if(getManagedBean().getCircularDocType()!=null && getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)) {
				if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) 
					return RoleCode.BPK_CCA.value();
			}
		} else { // Panel Circular Creation
			if(getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) 
				return RoleCode.BPK_PCA.value();
		}
		
		if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) 
			return RoleCode.BPK_APPROVER.value();
		else 
			return null;
	}
	
	public void addContactPerson(){
		log.info("PanelBackingBean.addContactPerson()");
		try {
			CtContactPerson contactPerson = new CtContactPerson();
	        getManagedBean().getCircularVer().getCtContactPersons().add(contactPerson);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteContactPerson(){
		log.info("PanelBackingBean.deleteContactPerson()");
		try {
			getManagedBean().getCircularVer().getCtContactPersons().removeAll(getManagedBean().getSelectedContactPersons());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelSupplier(List<SmSupplier> supplierList){
		log.info("PanelBackingBean.addPanelSupplier()");
		try {
			if (supplierList != null){
				List<CtPanelSupplier> ctPanelSuppliers = new ArrayList<CtPanelSupplier>();
				List<CtPanelSupplier> panelSupplierList = new ArrayList<CtPanelSupplier>();
				
				 if (this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelZone() != null) {
					 ctPanelSuppliers = this.getManagedBean().getSelectedPanelZone().getCtPanelSuppliers();
				 }
				 else if (!this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelItems() != null) {
					 ctPanelSuppliers =  this.getManagedBean().getSelectedPanelItems().getCtPanelSuppliers();
				 }
				
				for (SmSupplier smSupplier : supplierList) {
					CtPanelSupplier panelSupplier = new CtPanelSupplier();
					panelSupplier.setSupplier(smSupplier);
					panelSupplier.setCtPanelItem( this.getManagedBean().getSelectedPanelItems());
					panelSupplier.setCtPanelZone(this.getManagedBean().getSelectedPanelZone());					
					panelSupplierList.add(panelSupplier);
				   
					//Remove if already exist, avoid duplicate
					for(CtPanelSupplier ctPanelSupplier : ctPanelSuppliers) {
						if(ctPanelSupplier.getSupplier().getSupplierId().equals(smSupplier.getSupplierId())) {
							panelSupplierList.remove(panelSupplier);
						}
					}
				}
				ctPanelSuppliers.addAll(panelSupplierList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelSupplier(){
		
		try {
			if(this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelZone() != null) {
				this.getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().removeAll(this.getManagedBean().getSelectedPanelSuppliers());
			}
			else if(!this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelItems() != null) {
				this.getManagedBean().getSelectedPanelItems().getCtPanelSuppliers().removeAll(this.getManagedBean().getSelectedPanelSuppliers());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addCancelCircularListener() {
		log.info("PanelBackingBean.addCancelCircularListener()");
		CircularReqDTO reqDTO = null;
		try {
				if(getManagedBean().getCancelledCircularList().size() == 0){
			        reqDTO = new CircularReqDTO();
				    ISearchBean<CtCancelCircular> searchBean = new SearchBean<CtCancelCircular>();
				    searchBean.getDataFilterBeans().add(new SearchFilterBean("circularVer.ctCircular.circularType", 
						SearchOperator.OPERATOR_EQUALS.value(), ContractConst.CIRCULAR_DOC_TYPE_PANEL));
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
	
	public void addCancelledCircular(){
		log.info("PanelBackingBean.addCancelledCircular()");
        try {
        	for(CtCancelCircular cancelCircular : getManagedBean().getSelectedCancelledCircularList()) {
//        		cancelCircular.setCancelCircularId(null);
//        		cancelCircular.setCtCancelCircularReq(null);
        		CtCancelCircular addedCancelCircular = new CtCancelCircular();
        		addedCancelCircular.setParentCircularVer(getManagedBean().getCircularVer());
        		addedCancelCircular.setCircularVer(cancelCircular.getCircularVer());
        		getManagedBean().getCircularVer().getCtCancelCircularList().add(addedCancelCircular);
        	}
//        	getManagedBean().getCircularVer().getCtCancelCircularList().addAll(getManagedBean().getSelectedCancelledCircularList());
        	getManagedBean().getCancelledCircularList().removeAll(getManagedBean().getSelectedCancelledCircularList());
        	getManagedBean().setSelectedCancelledCircularList(new ArrayList<CtCancelCircular>());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelCircular(){
		log.info("PanelBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelCirculars());
			getManagedBean().getCancelledCircularList().addAll(getManagedBean().getSelectedCancelCirculars());			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void addPanelItem(){
		log.info("PanelBackingBean.addPanelItem()");
        try {	
        		 CtPanelItem panelItem = new CtPanelItem();
				 panelItem.setItemCategoryId(new PmParameter());
				 panelItem.setPanelCoverageId(new PmParameter());
				 panelItem.setPmUom(new PmUom());
				 panelItem.setLocationLevelId(new PmParameter());
				 panelItem.setCtPanelZones(null);
				 this.getManagedBean().getCircularVer().getCtPanelItems().add(panelItem);
        	 
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deletePanelItem(){
			getManagedBean().getCircularVer().getCtPanelItems().remove(this.getManagedBean().getSelectedPanelItems());
			this.getManagedBean().setSelectedPanelItems(null);
			this.getManagedBean().setSelectionOfZone(false);
	}
	
	public void addPanelZone(){
		CtPanelZone ctPanelZone = new CtPanelZone();
		this.getManagedBean().getSelectedPanelItems().getCtPanelZones().add(ctPanelZone);
	}
	
	public void deletePanelZone(){
		this.getManagedBean().getSelectedPanelItems().getCtPanelZones().remove(getManagedBean().getSelectedPanelZone());
		this.getManagedBean().setSelectedPanelZone(null);
	}

	
	public void selectActiveCirculars(){
		log.info("PanelBackingBean.selectActiveCirculars()");
        try {	
        	CtCancelCircular cancelCircular = null;
        	List<CtCancelCircular> cancelCircularList = new ArrayList<CtCancelCircular>();
        	for(CtCircularVer circularVer : getManagedBean().getSelectedCircularVerList()) {
        		cancelCircular = new CtCancelCircular();
        		cancelCircular.setCircularVer(circularVer);
        		cancelCircularList.add(cancelCircular);
        		for(CtCancelCircular ctCancelCircular : getManagedBean().getCancelCircularReq().getCtCancelCircularList()) {
        			if(ctCancelCircular.getCircularVer().getCircularVerId().equals(circularVer.getCircularVerId()))
        				cancelCircularList.remove(cancelCircular);
        		}
        	}
        	if(getManagedBean().getCancelCircularReq()==null) {
        		getManagedBean().setCancelCircularReq(new CtCancelCircularReq());
        	}
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().addAll(cancelCircularList);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteCancelReqCircular() {
		log.info("PanelBackingBean.deleteCancelReqCircular()");
		try {
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public SelectItem[] getStateDropdownValues() {
		SelectItem[] items = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(getCountryId());
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
	
	public SelectItem[] getCircularDocTypeValues() {
		SelectItem[] items = null;
		try {
		    if(isPanelAdmin() && isCentralContractAdmin()) { 
		    	items = new SelectItem[2];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_PANEL, "Panel Circular");
		    	items[1] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT, "Central Contract Circular");
		    } else if(isPanelAdmin()) {
		    	items = new SelectItem[1];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_PANEL, "Panel Circular");
		    } else if(isCentralContractAdmin()) {
		    	items = new SelectItem[1];
		    	items[0] = new SelectItem(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT, "Central Contract Circular");
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public SelectItem[] getStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			
			int i = 1;
		    //IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			reqDTO.setDocType(ParameterConst.DOCTYPE_PANEL_CIRCULAR);
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
	
	public String gotoPage() {
		String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
		Long circularVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("circularVerId"));
		log.info("Action :"+action+" Id :"+circularVerId);
		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			CircularResDTO resDTO = null;
			getManagedBean().setSelectionOfZone(false);
			getManagedBean().setSelectedPanelItems(null);
			getManagedBean().setSelectedPanelZone(null);
			getManagedBean().setSelectedTabIndex(0);
			getManagedBean().setAction(action);
			
			if(action.equalsIgnoreCase("view") || action.equalsIgnoreCase("save")) {
			        getManagedBean().setUpdateAction(false);
				    if (action.equalsIgnoreCase("view"))
					       getManagedBean().setOutComes(new String[]{});
			    	else
					       getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			
			} else if(action.equalsIgnoreCase("update")) {
				    reqDTO.setUpdateAction(true);
				    getManagedBean().setUpdateAction(true);
				    getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			
			} else if(action.equalsIgnoreCase("cancel")) {
				
			}
			
			resDTO = circularSO.retrieveCircularVerById(reqDTO);					
			setPanelCircularVar(resDTO);
			return "circularBase.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			circularSO = null;
		}
		return null;
	}
	
	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
		getManagedBean().getCircularVer().setCircularFileName(item.getFileName());
		getManagedBean().getCircularVer().setCircularFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	}
	
	public void downloadFile() throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = getManagedBean().getCircularVer().getCircularFileName();
		String filePath = getManagedBean().getCircularVer().getCircularFilePath();
		
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
	
	public String getItemGeneralCode(Long parameterId){
		if(parameterId == null || parameterId.intValue() == 0)
			return ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC;
		
		  IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setLanguageCode("EN");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			return resDTO.getPmParameter().getParameterCode();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	}
	
	public void setPanelListner(){
		
		 String panelCoverageICode = getItemGeneralCode(this.getManagedBean().getSelectedPanelItems().getPanelCoverageId().getParameterId());
		 if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)){
			  this.getManagedBean().setSelectionOfZone(true);
			  this.getManagedBean().setSelectedPanelZone(null);
		
		 }else if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ALL)){
			  this.getManagedBean().setSelectionOfZone(false);
		
		 }else {
			   this.getManagedBean().setSelectionOfZone(false);
			   this.getManagedBean().setSelectedPanelItems(null);
		 }
	}
	
	public void setCtPanelZoneListner(CtPanelItem panelItem){
		   this.getManagedBean().setSelectedPanelItems(panelItem);
		   String panelCoverageICode = getItemGeneralCode(this.getManagedBean().getSelectedPanelItems().getPanelCoverageId().getParameterId());
	 	 
		   if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)){
	 		      this.getManagedBean().setSelectedPanelZone(null);
			      this.getManagedBean().setSelectionOfZone(true);
			      if(this.getManagedBean().getSelectedPanelItems().getLocationLevelId() == null)
			    	    this.getManagedBean().getSelectedPanelItems().setLocationLevelId(new PmParameter());
			      this.getManagedBean().getSelectedPanelItems().getLocationLevelId().setParameterId(retrieveParameterByTypeAndCode());
			   
	 	   } else if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ALL)) {
			           this.getManagedBean().setSelectionOfZone(false);
		   } else{
			this.getManagedBean().setSelectionOfZone(false);
			this.getManagedBean().setSelectedPanelItems(null);
			this.getManagedBean().setSelectedPanelZone(null);
		}
	}
	
	public Long retrieveParameterByTypeAndCode()  {
		     IParameterSO parameterSO = null;
		     ParameterReqDTO reqDTO  = null;
		     try{
			           reqDTO = new ParameterReqDTO();
			       	   parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	             	   reqDTO.setParameterType(ParameterTypeConst.LOCATION_LEVEL);
		               reqDTO.setParameterCode(ParameterConst.LOCATION_LEVEL_STATE);
		               ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
		               return resDTO.getPmParameter().getParameterId();
		     } catch(Exception ex){
		    	 
		     }finally{
		    	 parameterSO = null;
		     }
		     return null;   
	}
	
	public Long getCountryId(){
		
		CountryReqDTO reqDTO= new CountryReqDTO();
		ICountrySO countrySO = null;
	
		   try{
	              reqDTO.setCountryCode("MY");
	              countrySO = (ICountrySO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
	              CountryResDTO resDTO  = countrySO.retrieveCountryByCode(reqDTO);
	              return resDTO.getPmCountry().getCountryId();
		   }catch(Exception ex){}
		   finally{
			   countrySO = null;
		   }
	         return null;
	}
	
	public void showGeneralZoneLocationPopup() {

		try{
			
			Long locationLevel = this.getManagedBean().getSelectedPanelItems().getLocationLevelId().getParameterId();

			List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(ParameterTypeConst.LOCATION_LEVEL);
			for (PmParameterDesc paramDesc : localParamDesc) {
				if (paramDesc.getPmParameter().getParameterId().equals(locationLevel)) {
			         getManagedBean().setRootZoanlTree(LocationSingleton.getInstance().buildLocationPopupTree(
		                getCountryId(), null, null,paramDesc.getPmParameter().getParameterCode(),CtPanelZoneDtl.class));
			   }
			}
			    this.getManagedBean().setZoneName(null);
		         this.getManagedBean().setEditZone(false);
		}catch(Exception ex){}

	}
	
	public SelectItem[] getDropdownLocationValues(String locationLevel) {

		List<String> types = new ArrayList<String>();
		types.add(ParameterConst.LOCATION_LEVEL_STATE);
		types.add(ParameterConst.LOCATION_LEVEL_DISTRICT);
		SelectItem[] items = null;
		try {
			items = new SelectItem[types.size()];
			int i = 0;
			for (String location : types) {
				List<PmParameterDesc> localParamDesc = retrieveParameterDescByType(locationLevel);
				for (PmParameterDesc paramDesc : localParamDesc) {
					if (paramDesc.getPmParameter().getParameterCode().equals(location)) {
						items[i++] = new SelectItem(paramDesc.getPmParameter()	.getParameterId(), paramDesc.getCodeName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),
							e.getMessage()));
		}
		return items;
	}
	
	private List<CtPanelZoneDtl> getZoneLocations(TreeNode zoneLocation) {

		List<CtPanelZoneDtl> results = new ArrayList<CtPanelZoneDtl>();
		if (zoneLocation.isLeaf()) {
			CtPanelZoneDtl locationDTO = (CtPanelZoneDtl) zoneLocation.getData();
			results.add(locationDTO);
		} else {
			for (TreeNode childrendLocationNode : zoneLocation.getChildren()) {
				  results.addAll(getZoneLocations(childrendLocationNode));
			}
		}

		return results;
	}
	
	public void updateZoneGeneralLocation() {
		
		if(getManagedBean().getZoneName() == null || getManagedBean().getZoneName() == ""){
			throw new EpException(MessageConst.WARN_753000);
		}
		
		List<CtPanelZoneDtl> allNodes = new ArrayList<CtPanelZoneDtl>();
		if (getManagedBean().getSelectedGeneralTreeNodes() != null) {
			for (TreeNode treeNode : getManagedBean().getSelectedGeneralTreeNodes()) {

				if (!treeNode.getParent().isSelected()) {
					List<CtPanelZoneDtl> locationDTO = getZoneLocations(treeNode);
					allNodes.addAll(locationDTO);
				}
			}
			
			if(this.getManagedBean().isEditZone()){
				
				if(!getManagedBean().getSelectedPanelItems().getCtPanelZones().get(getManagedBean().getIndex()).getName().equalsIgnoreCase(getManagedBean().getZoneName())){
					for(CtPanelZone panelZone : getManagedBean().getSelectedPanelItems().getCtPanelZones()){
						   if(panelZone.getName().equalsIgnoreCase(getManagedBean().getZoneName())){
							   throw new EpException(MessageConst.WARN_752000);
						   }
				      }
				}	
				this.getManagedBean().getSelectedPanelItems().getCtPanelZones().get(this.getManagedBean().getIndex()).setName(getManagedBean().getZoneName());
				this.getManagedBean().getSelectedPanelItems().getCtPanelZones().get(this.getManagedBean().getIndex()).getPanelZoneDtls().clear();
				this.getManagedBean().getSelectedPanelItems().getCtPanelZones().get(this.getManagedBean().getIndex()).getPanelZoneDtls().addAll(allNodes);
				this.getManagedBean().setEditZone(false);
		
			}else{
				
		          for(CtPanelZone panelZone : getManagedBean().getSelectedPanelItems().getCtPanelZones()){
					   if(panelZone.getName().equalsIgnoreCase(getManagedBean().getZoneName())){
						   throw new EpException(MessageConst.WARN_752000);
					   }
				   }
			
			       CtPanelZone zone = new CtPanelZone();
			       zone.setName(getManagedBean().getZoneName());
			       getManagedBean().setZoneName(null);
			       if(zone.getPanelZoneDtls() == null){
				           zone.setPanelZoneDtls( new ArrayList<CtPanelZoneDtl>());	
			       } zone.getPanelZoneDtls().addAll(allNodes);
			
		    	if(this.getManagedBean().getSelectedPanelItems().getCtPanelZones() == null)
				this.getManagedBean().getSelectedPanelItems().setCtPanelZones(new ArrayList<CtPanelZone>());
			   
			    this.getManagedBean().getSelectedPanelItems().getCtPanelZones().add(zone);
		  }
		}
	}
	
	public void editPanelZoneLocation(CtPanelZone zoneLocation,int index) {

		List<PmState> states = PanelLocationHelper.getStateFromLocationDTO(zoneLocation.getPanelZoneDtls());
		List<PmDistrict> districts = PanelLocationHelper.getDistrictFromLocationDTO(zoneLocation.getPanelZoneDtls());
		
		if(districts  != null && districts.size() > 0){
		   getManagedBean().setRootZoanlTree(
				LocationSingleton.getInstance().buildLocationPopupTree(getCountryId(),
						states, districts,ParameterConst.LOCATION_LEVEL_DISTRICT,
						CtPanelZoneDtl.class));
		}else{
			getManagedBean().setRootZoanlTree(
					LocationSingleton.getInstance().buildLocationPopupTree(getCountryId(),
							states, districts,ParameterConst.LOCATION_LEVEL_STATE,
							CtPanelZoneDtl.class));
		}

		int i = index;
		
		this.getManagedBean().setIndex(i);
		this.getManagedBean().setEditZone(true);
		this.getManagedBean().setZoneName(zoneLocation.getName());

		
	}
	
	public String getZoneStatus(){
		
			return "1";
	}
	
	public void removeAddRow(){
		try {
			if(this.getManagedBean().getCircularVer().getCtPanelItems() != null){
				Iterator<CtPanelItem> panelItemIterator = this.getManagedBean().getCircularVer().getCtPanelItems().iterator();
				while(panelItemIterator.hasNext()){
					CtPanelItem  item = panelItemIterator.next();
					if(item.isSelectedCheckBox()){
						panelItemIterator.remove();
					}
				}
			}
			
			this.getManagedBean().setSelectedPanelItems(null);
			this.getManagedBean().setSelectionOfZone(false);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void bulkUpdateRow(){
		try {
			if(this.getManagedBean().getCircularVer().getCtPanelItems() != null){
				Iterator<CtPanelItem> panelItemIterator = this.getManagedBean().getCircularVer().getCtPanelItems().iterator();
				while(panelItemIterator.hasNext()){
					CtPanelItem  item = panelItemIterator.next();
					if(item.isSelectedCheckBox()){
						if(item.getPanelCoverageId() == null){
							item.setPanelCoverageId(new PmParameter());
							item.getPanelCoverageId().setParameterId(this.getManagedBean().getPanelCoverageId());
					     }else{
					    		item.getPanelCoverageId().setParameterId(this.getManagedBean().getPanelCoverageId());
					     }
						
						if(item.getItemCategoryId() == null){
					    	   item.setItemCategoryId(new PmParameter());
					    	   item.getItemCategoryId().setParameterId(this.getManagedBean().getItemCategoryId());
					     }else{
					    	  item.getItemCategoryId().setParameterId(this.getManagedBean().getItemCategoryId());
					     }
				}
			}
		  }
			this.getManagedBean().setSelectedPanelItems(null);
			this.getManagedBean().setSelectionOfZone(false);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void extendStartEndDate(){
		
		if(this.getManagedBean().getCircularVer().getExtPeriod() > 0){
			this.getManagedBean().getCircularVer().setExtendStartDate(this.getManagedBean().getCircularVer().getStartDate());
			Calendar cal = Calendar.getInstance();
	        cal.setTime(new Date());
	        cal.add(Calendar.MONTH, this.getManagedBean().getCircularVer().getExtPeriod()); 
	        this.getManagedBean().getCircularVer().setExtendEndDate( cal.getTime());
		}else{
			this.getManagedBean().getCircularVer().setExtendStartDate(null);
			this.getManagedBean().getCircularVer().setExtendEndDate(null);
		}
	}
	
    public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}
	
	public String getSupAddress(CtPanelSupplier panelSupplier){
		IMofRegistrationSO mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofRegistrationSO.class);
		MofRegistrationReqDTO reqDTO = new MofRegistrationReqDTO();
		reqDTO.setSupplierId(panelSupplier.getSupplier().getSupplierId());
		MofRegistrationResDTO resDTO = mofRegistrationSO.retrieveSupplierProfile(reqDTO);
		
		if(resDTO.getSmSupplier().getSmAppl()!=null && resDTO.getSmSupplier().getSmAppl().getSmCompanyBasic()!=null && 
			resDTO.getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()!=null) {
			for(SmCompanyAddress address : resDTO.getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses()) {
				if(address.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) {
					return getFullAddress(address.getSmAddress());
				}
			}
		}
		return "";
	}

	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes().get("value");
		getManagedBean().getSelectedPanelItems().getLocationLevelId().setParameterId(location);
		getManagedBean().getSelectedPanelItems().getCtPanelZones().clear();
		if(getManagedBean().getSelectedPanelZone() != null && 
		    getManagedBean().getSelectedPanelZone().getCtPanelSuppliers() != null 
			&& getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().size() > 0)
	      	getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().clear();
	}
	
	public void bulkUpdateItem(){
		getManagedBean().setPanelCoverageId(null);
		getManagedBean().setItemCategoryId(null);
		boolean itemSelected = false;
		for(CtPanelItem ctPanelItem : getManagedBean().getCircularVer().getCtPanelItems()) {
			if(ctPanelItem.isSelectedCheckBox()) {
				itemSelected = true;
				break;
			}
		}
		if(!itemSelected) {
			throw new EpException(MessageConst.ERROR_750003);
		}
		RequestContext.getCurrentInstance().execute("bulkUpdateDlg.show()");
	}
	
	public String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
	public IPagingDataModel<CtCircularVer> getCircularVerPagingDataModel() {
		return circularVerPagingDataModel;
	}

	public void setCircularVerPagingDataModel(
			IPagingDataModel<CtCircularVer> circularVerPagingDataModel) {
		this.circularVerPagingDataModel = circularVerPagingDataModel;
	}
}
