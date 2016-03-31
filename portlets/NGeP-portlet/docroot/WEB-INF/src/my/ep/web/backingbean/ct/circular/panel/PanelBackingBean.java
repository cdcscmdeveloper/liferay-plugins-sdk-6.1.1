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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.quationtender.IQTSupplierSO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.country.CountryReqDTO;
import my.ep.dto.pm.common.country.CountryResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.QTSupplierReqDTO;
import my.ep.dto.sc.quationtender.QTSupplierResDTO;
import my.ep.dto.sm.mofAccount.MofAccountReqDTO;
import my.ep.dto.sm.mofAccount.MofAccountResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.circular.CTCircularContractDataType;
import my.ep.jaxb.ct.circular.cancellation.CTCircularCancellationDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.circular.CtCancelCircularReq;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.CtCircularVer;
import my.ep.persistence.entity.ct.circular.dtl.CtCancelCircular;
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItem;
import my.ep.persistence.entity.ct.circular.panel.CtPanelItemSpec;
import my.ep.persistence.entity.ct.circular.panel.CtPanelSupplier;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZone;
import my.ep.persistence.entity.ct.circular.panel.CtPanelZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.ct.CtAddressUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.circular.panel.PanelManagedBean;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

/**
 * Circular Panel Backing bean
 * 
 * @author alam
 * @since 9/10/2013
 */
@ManagedBean (name = "panelBackingBean") 
@RequestScoped
public class PanelBackingBean extends BaseBackingBean<PanelManagedBean>{
	
	private static Logger log = Logger.getLogger(PanelBackingBean.class);
	private static String baseDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
	
	/**
	 * The function constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public PanelBackingBean(){
		 
	    /*Execute first time retrieve from maintenance*/
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
				ctBB.initializeRemarkAndAttachement(null, getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
	    	}  
	   
		/*Setting list of approver during initialization*/
		if(getManagedBean().getUserMap().size()==0) {
			   initializeApproverList();
			   this.getManagedBean().getApproverModel().setSource(getAvailableApproverList(RoleCode.BPK_PQ_APPROVER.value()));
			  
	     /*Setting types of document such panel circular */
	     } if(getManagedBean().getCircularDocType()==null) {
			      getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
		
	      /*Setting the admin login id*/
	     } if(isPanelAdmin() || isCentralContractAdmin()) {
			      FacesScopes facesScopes = new FacesScopes();
			      getManagedBean().setCircularAdmin(facesScopes.getUserSessionBean().getLoginId());
		  }
	  }
	
	/**
     *  Method used to retrieve login user information.
	 */
	  private FacesScopes getFacesScopes() {
		     return new FacesScopes();
	  }
	
	  /**
		 * Method used to retrieve the information and render it on screen.
		 */
	  public void preRenderCircular() {
		  	renewSesionObject();
		    getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "circularBase.xhtml?faces-redirect=true");
	  }
	
	/**
	 * Method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		
		    System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId==null) 
			return;
			
			CTCircularContractDataType dataType = super.getBpmTaskDetails(CTCircularContractDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});			
			getManagedBean().setCircularAdmin(dataType.getCircularAdmin().getUserName());
			CircularReqDTO circularReqDTO = new CircularReqDTO();
			circularReqDTO.setCircularVerId(dataType.getDocId().longValue());
			setPanelCircularVar(circularReqDTO);
	}
	
	/**
	 * The method used to set the panel circular  information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for circular
	 */
	public void setPanelCircularVar(CircularReqDTO reqDTO){
		
		   ICircularSO circularSO = null;
		   CircularResDTO resDTO = null;
		   try {
			          circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			          resDTO= circularSO.retrieveCircularVerById(reqDTO);
		              getManagedBean().setCircularVer(resDTO.getCircularVer());
		              getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
		
		             if(getManagedBean().getCircularVer().getExtendStartDate() != null && getManagedBean().getCircularVer().getExtendEndDate() != null){
		    	               getManagedBean().setCircularExtension(true);
		             }
		 
		    
		             /* if(getManagedBean().getCircularVer().getCtPanelItems() != null && getManagedBean().getCircularVer().getCtPanelItems().size() > 0){
			 
		    	       Collections.sort(getManagedBean().getCircularVer().getCtPanelItems(), new CtPanelItem());
			            for(CtPanelItem panelItem : getManagedBean().getCircularVer().getCtPanelItems()) {
				
			        	      if(panelItem.getCtPanelZones() != null && panelItem.getCtPanelZones().size() > 0) {
					           	Collections.sort(panelItem.getCtPanelZones(), new CtPanelZone());
				                    for(CtPanelZone panelZone : panelItem.getCtPanelZones())
				        	               Collections.sort(panelZone.getCtPanelSuppliers(), new CtPanelSupplier()); 
				               }
				
			        	      if(panelItem.getCtPanelSuppliers() != null && panelItem.getCtPanelSuppliers().size() > 0)
			  		                 Collections.sort(panelItem.getCtPanelSuppliers(), new CtPanelSupplier());  
		 	                 }
		                }*/
		
		             	if(getManagedBean().getUserMap().size()==0) {
		     			   initializeApproverList();
		     			   this.getManagedBean().getApproverModel().setSource(getAvailableApproverList(RoleCode.BPK_PQ_APPROVER.value()));
		     			  }
		             	
		               getManagedBean().setRemarkList(resDTO.getRemarkList());
		               getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		               getManagedBean().getApproverModel().setSource(getAvailableApproverList(RoleCode.BPK_PQ_APPROVER.value()));
		   
		               if(resDTO.getApproverList()!=null && resDTO.getApproverList().size()>0) {
			                    getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			                    for (PmUser approverUser : resDTO.getApproverList())  {
						              Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
					          	      while (sourceIt.hasNext()) {
							                PmUser user = sourceIt.next();
							                if (user.getUserId() == approverUser.getUserId()) {
								                 sourceIt.remove();
							                }
						             }
					           }
		              }

		            getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_PANEL_CIRCULAR);
		            initializeRemarks();
		           ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		           ctBB.getManagedBean().setViewContract(this.getManagedBean().isViewContract());
		           ctBB.initializeRemarkAndAttachement(resDTO.getCircularVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
	        
		    } catch (Exception e) {
		            e.printStackTrace();
		            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	        } finally {
		        circularSO = null;
		        resDTO  = null;
	        }
	    }
	/**
	 * The method used to set the panel circular  panel circular details for cancel
	 * Navigate the cancel page for panel circular
	 * 
	 */
	    public void preRenderCancelCircular() {
		     renewSesionObject();
		     getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
		     FacesContext fc = FacesContext.getCurrentInstance();
		     fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "cancelCircularBase.xhtml?faces-redirect=true");
	    }
	
	    /**
		 * The method used to set the  panel circular details for cancel
		 * Navigate the cancel page for panel circular
		 * 
		 */
	    public void preRenderViewCancelReq() {
		     ICircularSO circularSO = null;
		     try {
		
			         System.out.println("**********preRenderView**************");
			         HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			         HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			         String taskId = originalServletRequest.getParameter("taskId");
			         if(taskId==null) {
				           if((isPanelAdmin() || isCentralContractAdmin()) && !getManagedBean().isChkCancelPage()) {
					            getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SUBMIT});	
				           }
				        return;
			         }
			         
			        circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
			        CTCircularCancellationDataType dataType = super.getBpmTaskDetails(CTCircularCancellationDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});			
					getManagedBean().setCircularAdmin(dataType.getCircularAdmin().getUserName());
			        CircularReqDTO cancelReqDTO = new CircularReqDTO();
			        cancelReqDTO.setCancelCircularReqId(dataType.getDocId().longValue());
			
			        CircularResDTO resDTO = circularSO.retrieveCancelCircularReqById(cancelReqDTO);
			        getManagedBean().setCancelCircularReq(resDTO.getCancelCircularReq());
			        List <CtCancelCircular> ctCancelCircularList = resDTO.getCancelCircularReq().getCtCancelCircularList();
			
			        if(ctCancelCircularList.get(0).getCircularVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CENTRAL_CONTRACT_CIRCULAR))
				             getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
		            else 
			        	     getManagedBean().setCircularDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			
	             	if(getManagedBean().getUserMap().size()==0) {
	     			   initializeApproverList();
	             	}
			        getManagedBean().getApproverModel().setSource(getAvailableApproverList(getApproverRoleCodeByDocType()));
			        if(resDTO.getApproverList()!=null && resDTO.getApproverList().size()>0) {
				          getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
				          getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			        }
			
			        getManagedBean().setRemarkList(resDTO.getRemarkList());
			        getManagedBean().setAttachmentList(resDTO.getAttachmentList());
		
			        getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST);
			        ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			        ctBB.initializeRemarkAndAttachement(resDTO.getCancelCircularReq().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
		     } catch (Exception e) {
			     e.printStackTrace();
			     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     } finally {
			     circularSO = null;
		     }		
	   }
	
	    /**
	     * The method used to navigate to contract maintenance screen.
	     * 
	     * @return the navigate URL 
	     */
	    public String backAction() {
	    	if(getManagedBean().isChkCancelPage()) {
	    		getManagedBean().setChkCancelPage(false);
	    		return "cancelCircularBase.xhtml?faces-redirect=true";
	    	} else if(getManagedBean().isFromCircularNotice()) {
	    		return "../portal/circularListing.xhtml?faces-redirect=true";
	    	} else if(getManagedBean().isDifferentModule()){
	    		return getManagedBean().getGotoPreviewPage();
	    	} else if(getManagedBean().getGotoPreviewPage()!=null) {
	    		return getManagedBean().getGotoPreviewPage();
	    	} else {
	    		return "search.xhtml?faces-redirect=true";
	    	}
	    }
	
    /**
	    *Method used to searchCircularVer to retrieve circular information
     */
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
	
	  /**
	    *Method used to save/update of newly created circular
	    *Method call to validate panel circular details as per rule
	    *Navigate to confirmation message page
  */
	public String savePanelCircular(String action) {
		log.debug("PanelBackingBean.savePanelCircular("+action+")");

		ICircularSO circularSO = null;
		try {
			
			
			if(!getManagedBean().getCircularVer().isPid()){
				  getManagedBean().getCircularVer().setCtCircularPidReason(null);
			  }
			
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
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
			
			
            
			CircularReqDTO reqDTO = new CircularReqDTO();
		    reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			reqDTO.setRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
		
			reqDTO.setOutcome(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setTaskName(getManagedBean().getTaskName());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			PmUser pmUSer = new PmUser();
			pmUSer.setLoginId(getManagedBean().getCircularAdmin());
			reqDTO.setCircularAdmin(pmUSer);
			reqDTO.setUserRole(getUserRole());
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			if(getFacesScopes().hasRoleUser(RoleCode.BPK_PQ_APPROVER.value())) {
				circularVer.setApprover(getFacesScopes().getUserSessionBean());
				circularVer.setApprovedDate(new Date());
			}
			
			
			reqDTO.setCircular(circular);
			reqDTO.setCircularVer(circularVer);
			reqDTO.setUpdateAction(getManagedBean().isUpdateAction());
			//gpki
			if (this.getManagedBean().getPmDigiSign() != null) {
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
					} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
						messageId = MessageConst.INFO_700010;
					}
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, getManagedBean().getCircularVer().getDocType()), 
								resDTO.getCircular().getCircularNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  	
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else{
					reqDTO = new CircularReqDTO();
					reqDTO.setCircularVerId(resDTO.getCircularVer().getCircularVerId());
					setPanelCircularVar(reqDTO);
					getManagedBean().setSelectionOfZone(false);
					getManagedBean().setSelectedPanelItems(null);
					getManagedBean().setSelectedPanelZone(null);
					getManagedBean().setUpdateAction(false);
					getManagedBean().setViewContract(false);
					initializeRemarks();
					ContractBackingBean ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
					ctBB.initializeRemarkAndAttachement(getManagedBean().getCircularVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
				}
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		  }finally {
			  circularSO = null;
		  }
		return "circularBase.xhtml";
	}
	
	  /**
	    *Method used to save cancel  circular details into database
	    *Method call to validate panel circular details as per rule
	    *Navigate to confirmation message page
*/
	public String saveCancelCircularReq(String action) {
		log.debug("PanelBackingBean.saveCircularPanel("+action+")");

		ICircularSO circularSO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,ICircularSO.class);
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				if(getManagedBean().getCancelCircularReq().getCtCancelCircularList().size()==0) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{"Circular(s) to be cancel"});
				}
				
				if(getManagedBean().getApproverModel().getTarget().size()==0 && (isPanelAdmin() || isCentralContractAdmin())) {
					    throw new EpException(MessageConst.ERROR_100022, new String[]{"Selected approver(s)"});
				    }
			    }
			
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				String remark = getManagedBean().getRemarkList().get(0).getRemark();
	 			if((remark == null || remark.length() == 0)) {
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
			reqDTO.setTaskName(getManagedBean().getTaskName());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
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
				} else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} 
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_CANCEL_CIRCULAR_REQUEST), 
								resDTO.getCancelCircularReq().getCancelCircularReqNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				renewSesionObject();
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			} else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		} finally {
			circularSO = null;
		}
	}
	
	/**
	 * Method used to validate panel circular details
	 * A warning or error message will display on screen during validation as per rule
	 * Function validate as per user action 
	 */
	public void validateCircular(String outcome) {
			getManagedBean().setAutoUpdate(true);
			if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)){
				 if(!getManagedBean().getCircularVer().isDirectPurchase() 
					  && getManagedBean().getCircularVer().isDirectNego() && getManagedBean().getCircularVer().isQt()){
					 throw new EpException(MessageConst.ERROR_100022,  
							 new String[]{"Direct Nego, Direct Purchase or Quotation Tender checkbox"});
				 }
			}
			
			if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
					&& getManagedBean().getCircularVer().isPid() && 
					(getManagedBean().getCircularVer().getCtCircularPidReason() == null 
					|| getManagedBean().getCircularVer().getCtCircularPidReason().size() == 0)) {
				 throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			
			if(getManagedBean().getCircularVer().getCtCircularPidReason() != null 
					 && getManagedBean().getCircularVer().getCtCircularPidReason().size() > 0 ){
				for(CtCircularPidReason reason : getManagedBean().getCircularVer().getCtCircularPidReason()){
					   if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){
						   throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					   }
					   if(PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
						   if(reason.getReasonDesc().isEmpty()){
							   throw new EpException(MessageConst.ERROR_100022, 
										new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason.desc", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						   }
					   }
				}
			} 
			
			if (getManagedBean().getCircularVer().getCtContactPersons() != null 
					&& getManagedBean().getCircularVer().getCtContactPersons().size() >= 1) {				
			        for(CtContactPerson CtPerson : getManagedBean().getCircularVer().getCtContactPersons()){
				        if(CtPerson.getName() == null  || CtPerson.getName().isEmpty()){
					       throw new EpException(MessageConst.ERROR_100022, 
							 new String[]{ResourceBundleUtils.getMessage("lbl.ct.contact.person.name", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				        }
			       }
			}
			
			/*if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))
					&& getManagedBean().getCircularVer().getCircularFileName()==null) {
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.circular.letter", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}*/
			
			if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				String circularRemark = getManagedBean().getCircularVer().getCircularRemarks().get(0).getRemark();
				String itemRemark = getManagedBean().getCircularVer().getItemRemarks().get(0).getRemark();
				
				if((circularRemark == null || circularRemark.length() == 0)
						&& (itemRemark == null || itemRemark.length() == 0)) {
	 				throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	 			}
			}
			
			if(getManagedBean().getCircularVer().getCtPanelItems().size()==0) {
				if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					throw new EpException(MessageConst.ERROR_100023, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.panel.item", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			
			} else {
			
				for (CtPanelItem panelItem: getManagedBean().getCircularVer().getCtPanelItems()) {
					
					if(panelItem.getZoneAllParamterId()==null) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.panel.coverage", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					 }
					
					if(panelItem.getCategoryParameterId() == null) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.item.category", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					} else {
						if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))
								&& getItemGeneralCode(panelItem.getCategoryParameterId()).equalsIgnoreCase(ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC)) {
							     if(panelItem.getCtPanelItemSpecs() == null || panelItem.getCtPanelItemSpecs().isEmpty()) {
								      throw new EpException(MessageConst.ERROR_750029, new String[]{panelItem.getItemName()});
							     } else {
								     for (CtPanelItemSpec ctPanelItemSpec: panelItem.getCtPanelItemSpecs()) {
									        if (ctPanelItemSpec.getSpecification() == null || ctPanelItemSpec.getSpecification().length() == 0) {
										         throw new EpException(MessageConst.ERROR_750030, new String[]{panelItem.getItemName()});
									        }
								    }
							    }
						  }
					 }
					
					if(panelItem.getCtPanelItemGroup() == null) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.item.group", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					
					if(panelItem.getItemType() == null || panelItem.getItemType().getParameterId()==null) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.item.type", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					
					if(panelItem.getItemName().length()==0) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.item.name", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					
					
					if(panelItem.getPmUom().getUomId()==null) {
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.uom", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					
					if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						  if(getItemGeneralCode(panelItem.getZoneAllParamterId()).equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)) {
							     if(panelItem.getLocationLevelId().getParameterId()==null) {
								       throw new EpException(MessageConst.ERROR_100022, 
										    new String[]{ResourceBundleUtils.getMessage("lbl.ct.location.level", 
												 ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							      }
							
							     if(panelItem.getCtPanelZones().size()==0) {
								       throw new EpException(MessageConst.ERROR_100023, 
										   new String[]{ResourceBundleUtils.getMessage("lbl.ct.Zone", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							    } else {
								        for (CtPanelZone panelZone: panelItem.getCtPanelZones()) {
									        
								        	  if(panelZone.getName().length()==0) {
										          throw new EpException(MessageConst.ERROR_100022, 
												      new String[]{ResourceBundleUtils.getMessage("lbl.ct.zone.name", 
														ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
										          
								        	  }  if(panelZone.getPanelZoneDtls().size() == 0){
										          throw new EpException(MessageConst.ERROR_100022, 
												      new String[]{ResourceBundleUtils.getMessage("lbl.ct.zone.location", 
														 ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
									       
								        	  }  if(panelZone.getCtPanelSuppliers().size()==0) {
								                      throw new EpException(MessageConst.ERROR_100023, 
										                new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier", 
												       ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							      	         } 
								        	  for(CtPanelSupplier panelSupplier : panelZone.getCtPanelSuppliers()){
								        		     if(panelSupplier.getBankAccountNo().isEmpty()){
								        		    	 throw new EpException(MessageConst.ERROR_100022, 
													                new String[]{ResourceBundleUtils.getMessage("lbl.ct.bank.account.name", 
															       ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
								        		     }
								        	  }
								      }
							   }
					
						  } else {					
							   if(panelItem.getCtPanelSuppliers().size()==0) {
								    throw new EpException(MessageConst.ERROR_100023, 
										new String[]{ResourceBundleUtils.getMessage("lbl.ct.supplier", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							    }
							   
							   for(CtPanelSupplier panelSupplier : panelItem.getCtPanelSuppliers()){
				        		     if(panelSupplier.getBankAccountNo() == null || panelSupplier.getBankAccountNo().isEmpty()){
				        		    	 throw new EpException(MessageConst.ERROR_100022, 
									                new String[]{ResourceBundleUtils.getMessage("lbl.ct.bank.account.name", 
											       ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				        		     }
				        	    }
						   }
					  }
				 }
			 }
			
			/*if(getManagedBean().getApproverModel().getTarget().size()==0 && (isPanelAdmin() || isCentralContractAdmin()) ) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Selected approver(s)"});
			}*/
		//}
		if((outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) 
				&& getManagedBean().getApproverModel().getTarget().size()==0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.			
		}
		
		/*if((getManagedBean().isUpdateAction() || (getManagedBean().getCircular() != null && getManagedBean().getCircular().getLatestCircularVer() != null)) && 
				outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getAttachmentList().isEmpty()){				
				throw new EpException(MessageConst.ERROR_750018, new String[]{"panel circular update"});
		}*/
	}
	
	/**
	 * This  method used to initialize approver list
	 */
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}
	
	/**
	 * Method used retrieve list of approver from system
	 * Approver info fetch from system as per rule define rule
	 */
	private List<PmUser> getAvailableApproverList(String roleCode) {
		IUserSO userSO = null;
		List<PmUser> sourceList = new ArrayList<PmUser>();
		try {
			userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			UserResDTO resDTO = new UserResDTO();
			if((getFacesScopes().getUserSessionBean() == null 
					|| getFacesScopes().getUserSessionBean().getUserId() == null) && getManagedBean().isViewContract()){
				   return sourceList;
			}
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());			
			resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			if (resDTO.getUserOrgList().size() > 0) {
				reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			}
			reqDTO.setRoleCode(roleCode);
			
			resDTO = userSO.retrieveUserListByRole(reqDTO);
			List<PmUser> list = resDTO.getUserList();
			
			for (PmUser user : list) {
				sourceList.add(user);
				getManagedBean().getUserMap().put(user.getUserId(), user);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
		return sourceList;
	}	
	
	/**
	 * The method used for initializing the remarks of panel circular.
	 */
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCircularVer().getCircularRemarks());
		initiateRemark(getManagedBean().getCircularVer().getItemRemarks());
	}
	
	/**
	 * Method used to initializing the remarks of panel circular
	 * This method used to check the workflow of current user for
	 *  initialization or view remark
	 * 
	 * return @param remarkList 
	 */
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

	/**
	 * Method used to initializing the remarks of panel circular
	 * This method used to check the workflow of current user for
	 *  initialization or view remark
	 * 
	 * return @param remarkList 
	 */
	public boolean isPanelAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value());
	}
	
	/**
	 * Method used to set role of central contract admin
	 */
	public boolean isCentralContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}
	
	/**
	 * Method used to set role of BPK 1PPCC/1PPPC approver
	 */
	public boolean isBPKApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CQ_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_PQ_APPROVER.value()));
	}
	
	/**
	 * Method used to retrieve the user role on the basis of user login
	 */
	private String getUserRole() {
		String userRole = null;
		// Panel Circular Creation
		if (getManagedBean().getCancelCircularReq() == null 
				|| getManagedBean().getCancelCircularReq().getCtCancelCircularList() == null
				|| getManagedBean().getCancelCircularReq().getCtCancelCircularList().size() == 0) {
			if (getManagedBean().getTaskName() == null
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_CIRCULAR_CONTRACT)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_CIRCULAR_CONTRACT)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_CIRCULAR_CONTRACT)) {
				if (getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) {
					userRole = RoleCode.BPK_PCA.value();
				}
			} else if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_CIRCULAR_CONTRACT))) {
				if (getFacesScopes().hasRoleUser(RoleCode.BPK_PQ_APPROVER.value())) {
					userRole = RoleCode.BPK_PQ_APPROVER.value();
				}
			}
		} else { // Cancellation Request
			if (getManagedBean().getTaskName() == null
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_CIRCULAR_CANCELLATION_RQUEST_HT_NAME)) {
				if (getManagedBean().getCircularDocType() != null 
						&& getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_PANEL)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_PCA.value())) {
					return RoleCode.BPK_PCA.value();
				} else if (getManagedBean().getCircularDocType() != null 
						&& getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
					return RoleCode.BPK_CCA.value();
				}
			} else if (getManagedBean().getTaskName() != null
					&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_CIRCULAR_CANCELLATION_RQUEST_HT_NAME))) {
				if (getManagedBean().getCircularDocType() != null 
						&& getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_PANEL)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_PQ_APPROVER.value())) {
					return RoleCode.BPK_PQ_APPROVER.value();
				} else if (getManagedBean().getCircularDocType() != null 
						&& getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_CQ_APPROVER.value())) {
						return RoleCode.BPK_CQ_APPROVER.value();
				}
			}
		}

		if (userRole == null) {
			 throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	/**
	 * Method used to add list of contract person details
	 * during panel circular creation
	 */
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
	
	/**
	 * Method used to delete list of contract person details
	 * from panel circular creation
	 */
	public void deleteContactPerson(){
		log.info("PanelBackingBean.deleteContactPerson()");
		try {
			getManagedBean().getCircularVer().getCtContactPersons().removeAll(getManagedBean().getSelectedContactPersons());
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method to clear the item specification list when general item category is selected
	 * @param need to pass ctPanelItem Object
	 * return void statement
	  **/
	public void changeItemCategoryListner(CtPanelItem panelItem) {
		if(getItemGeneralCode(panelItem.getCategoryParameterId()).equalsIgnoreCase(ParameterConst.ITEM_CATEGORY_TYPE_GENERAL)) {
			panelItem.setCtPanelItemSpecs(null);
		}			
	}	
	
	/**
	 * The method used to invoke and initiate the panel item specification dialog.
	 */
	public void panelItemSpecDlg(CtPanelItem panelItem) {
		getManagedBean().setSelectedItemForSpec(panelItem);
		RequestContext.getCurrentInstance().execute("itemSpecDlg.show()");
	}
	
	/**
	 * Method used to add list of panel item specification
	 * during panel circular creation
	 */
	public void addPanelItemSpec(){
		log.info("PanelBackingBean.addPanelItemSpec()");
		try {
			CtPanelItemSpec itemSpec = new CtPanelItemSpec();
			itemSpec.setCtPanelItem(getManagedBean().getSelectedItemForSpec());
	        getManagedBean().getSelectedItemForSpec().getCtPanelItemSpecs().add(itemSpec);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to delete list of panel item specification
	 * from panel circular creation
	 */
	public void deletePanelItemSpec(){
		log.info("PanelBackingBean.deletePanelitemSpec()");
		try {
			getManagedBean().getSelectedItemForSpec().getCtPanelItemSpecs().removeAll(getManagedBean().getSelectedPanelItemSpecs());
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to add list of supplier details
	 * during panel circular item creation
	 */
	public void addPanelSupplier(List<SmSupplier> supplierList) { //101
		log.info("PanelBackingBean.addPanelSupplier()");
		try {
			if (supplierList != null) {
				List<CtPanelSupplier> ctPanelSuppliers = new ArrayList<CtPanelSupplier>();
				List<CtPanelSupplier> panelSupplierList = new ArrayList<CtPanelSupplier>();
				List<SmSupplierBank> bankAccountNoList = null;
				SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
				SupplierSearchResDTO supplierResDTO = new SupplierSearchResDTO();
				ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
				
				if (this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelZone() != null) {
					ctPanelSuppliers = this.getManagedBean().getSelectedPanelZone().getCtPanelSuppliers();
				} else if (!this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelItems() != null) {
					ctPanelSuppliers =  this.getManagedBean().getSelectedPanelItems().getCtPanelSuppliers();
				}
				
				for (SmSupplier smSupplier : supplierList) {
					boolean isSupplierExist = false;
					// if already exist, skip to avoid duplicate
					for (CtPanelSupplier ctPanelSupplier : ctPanelSuppliers) {
						if (ctPanelSupplier.getSupplier().getSupplierId().equals(smSupplier.getSupplierId())) {
							isSupplierExist = true;
							break;
						}
					}					
					if (isSupplierExist) {
						continue;
					}
					// Panel Supplier
					CtPanelSupplier panelSupplier = new CtPanelSupplier();
					panelSupplier.setSupplier(smSupplier);
					panelSupplier.setCtPanelItem( this.getManagedBean().getSelectedPanelItems());
					panelSupplier.setCtPanelZone(this.getManagedBean().getSelectedPanelZone());
					panelSupplier.setSupplierCompanyName(smSupplier.getCompanyName());
					panelSupplier.setSupplierMofNo(retrieveMofNo(smSupplier));
					panelSupplier.setSupplierRegNo(smSupplier.getRegNo());
					panelSupplier.setSupplierAddress(CtAddressUtil.retrieveSupplierAddress(
							retrieveSupplierInfo(smSupplier.getSupplierId())));
					
					supplierReqDTO.setSupplierId(smSupplier.getSupplierId());					
					supplierResDTO = supplierSearchSO.retrieveSmSupplierBankBySupplier(supplierReqDTO);
					bankAccountNoList = new ArrayList<SmSupplierBank>();
					if (supplierResDTO.getSmSupplierBankList() != null) {
						for (SmSupplierBank smSupplierBank : supplierResDTO.getSmSupplierBankList()) {
							if (smSupplierBank.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
								bankAccountNoList.add(smSupplierBank);
						    }
						}
					}
					if (bankAccountNoList.size() == 1) {
						panelSupplier.setBankAccountNo(bankAccountNoList.get(0).getAccountNo());
						panelSupplier.setBankName(bankAccountNoList.get(0).getPmFinancialOrg().getFinOrgName());
					}
					panelSupplierList.add(panelSupplier);
				}
				ctPanelSuppliers.addAll(panelSupplierList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to add bulk of of supplier details
	 * into panel item supplier details and 
	 * zone supplier details
	 */
	public void addBulkPanelSupplier(List<SmSupplier> supplierList){ 
		log.info("PanelBackingBean.addPanelSupplier()");
		String panelCoverageICode = "";
		try {
			List<SmSupplierBank> bankAccountNoList = null;
			SupplierSearchReqDTO supplierReqDTO = new SupplierSearchReqDTO();
			SupplierSearchResDTO supplierResDTO = new SupplierSearchResDTO();
			ISupplierSearchSO supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
			
			if (supplierList != null) {				
				for (CtPanelItem panelItem : getManagedBean().getCircularVer().getCtPanelItems()) {
					if (panelItem.isSelectedCheckBox()) {
						List<CtPanelSupplier> panelSupplierList = new ArrayList<CtPanelSupplier>();
						List<CtPanelSupplier> ctPanelSuppliers = new ArrayList<CtPanelSupplier>();
						
						if (panelItem.getPanelCoverageId() == null || panelItem.getPanelCoverageId().getParameterCode() == null) {
							panelCoverageICode = getItemGeneralCode(panelItem.getZoneAllParamterId()); 
						} else {
							panelCoverageICode = panelItem.getPanelCoverageId().getParameterCode();
						}
						if (panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ALL)) {
							ctPanelSuppliers =  panelItem.getCtPanelSuppliers();
							for (SmSupplier smSupplier : supplierList) {
								boolean isSupplierExist = false;
								// if already exist, skip to avoid duplicate
								for (CtPanelSupplier ctPanelSupplier : ctPanelSuppliers) {
									if (ctPanelSupplier.getSupplier().getSupplierId().equals(smSupplier.getSupplierId())) {
										isSupplierExist = true;
										break;
									}
								}					
								if (isSupplierExist) {
									continue;
								}
								// Panel Supplier
								CtPanelSupplier panelSupplier = new CtPanelSupplier();
								panelSupplier.setSupplier(smSupplier);
								panelSupplier.setCtPanelItem(getManagedBean().getSelectedPanelItems());
								panelSupplier.setCtPanelZone(getManagedBean().getSelectedPanelZone());	
								panelSupplier.setSupplierCompanyName(smSupplier.getCompanyName());
								panelSupplier.setSupplierMofNo(retrieveMofNo(smSupplier));
								panelSupplier.setSupplierRegNo(smSupplier.getRegNo());
								panelSupplier.setSupplierAddress(CtAddressUtil.retrieveSupplierAddress(
										retrieveSupplierInfo(smSupplier.getSupplierId())));
								
							    supplierReqDTO.setSupplierId(smSupplier.getSupplierId());					
								supplierResDTO = supplierSearchSO.retrieveSmSupplierBankBySupplier(supplierReqDTO);
								bankAccountNoList = new ArrayList<SmSupplierBank>();
								if (supplierResDTO.getSmSupplierBankList() != null) {
									for (SmSupplierBank smSupplierBank : supplierResDTO.getSmSupplierBankList()) {
										if (smSupplierBank.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
											bankAccountNoList.add(smSupplierBank);
										}
									}
								}
								if (bankAccountNoList.size( ) == 1) {
									panelSupplier.setBankAccountNo(bankAccountNoList.get(0).getAccountNo());
									panelSupplier.setBankName(bankAccountNoList.get(0).getPmFinancialOrg().getFinOrgName());
								}
								panelSupplierList.add(panelSupplier);
							}
							panelItem.getCtPanelSuppliers().addAll(panelSupplierList);
						}
						
						if (panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)) {							   
                        	  for (CtPanelZone zone : panelItem.getCtPanelZones()) { 
                        		  List<CtPanelSupplier> panelSupplierList1 = new ArrayList<CtPanelSupplier>();
                        		  ctPanelSuppliers = zone.getCtPanelSuppliers();
                        		  for (SmSupplier smSupplier : supplierList) {
                        			  boolean isSupplierExist = false;
                        			  // if already exist, skip to avoid duplicate
                        			  for (CtPanelSupplier ctPanelSupplier : ctPanelSuppliers) {
                        				  if (ctPanelSupplier.getSupplier().getSupplierId().equals(smSupplier.getSupplierId())) {
                        					  isSupplierExist = true;
                        					  break;
                        				  }
                        			  }					
                        			  if (isSupplierExist) {
                        				  continue;
                        			  }
                        			  // Panel Supplier
                        			  CtPanelSupplier panelSupplier = new CtPanelSupplier();
                        			  panelSupplier.setSupplier(smSupplier);
                        			  panelSupplier.setCtPanelItem(getManagedBean().getSelectedPanelItems());
                        			  panelSupplier.setCtPanelZone(getManagedBean().getSelectedPanelZone());
                        			  panelSupplier.setSupplierCompanyName(smSupplier.getCompanyName());
                        			  panelSupplier.setSupplierMofNo(retrieveMofNo(smSupplier));
                        			  panelSupplier.setSupplierRegNo(smSupplier.getRegNo());
                        			  panelSupplier.setSupplierAddress(CtAddressUtil.retrieveSupplierAddress(
                        					  retrieveSupplierInfo(smSupplier.getSupplierId())));

                        			  supplierReqDTO.setSupplierId(smSupplier.getSupplierId());					
                        			  supplierResDTO = supplierSearchSO.retrieveSmSupplierBankBySupplier(supplierReqDTO);
                        			  bankAccountNoList = new ArrayList<SmSupplierBank>();
                        			  if (supplierResDTO.getSmSupplierBankList() != null) {
                        				  for (SmSupplierBank smSupplierBank : supplierResDTO.getSmSupplierBankList()) {
                        					  if (smSupplierBank.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
                        						  bankAccountNoList.add(smSupplierBank);
                        					  }
                        				  }
                        			  }
                        			  if (bankAccountNoList.size() == 1) {
                        				  panelSupplier.setBankAccountNo(bankAccountNoList.get(0).getAccountNo());
      									  panelSupplier.setBankName(bankAccountNoList.get(0).getPmFinancialOrg().getFinOrgName());
                        			  }
                        			  panelSupplierList1.add(panelSupplier);
                        		  }
                        		  zone.getCtPanelSuppliers().addAll(panelSupplierList1);
                        	  }
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * The method used to retrieve and display the bank and account information
	 * with the selected bank for change bank request.
	 */
	public void changeAccountActionListener(CtPanelSupplier  panelSupplier, int index) {
			
	    SmSupplierBank supplierBank = getManagedBean().getSupplierBankAccountMap().get(panelSupplier.getBankAccountNo());
		if (supplierBank != null) {
			if(getManagedBean().isSelectionOfZone()) {
				     CtPanelSupplier supplier = getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().get(index);
				     supplier.setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
				     getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().remove(index);
				     getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().add(index, supplier); 
				} else {
					  CtPanelSupplier supplier = getManagedBean().getSelectedPanelItems().getCtPanelSuppliers().get(index);
					  supplier.setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());
					  getManagedBean().getSelectedPanelItems().getCtPanelSuppliers().remove(index);
					  getManagedBean().getSelectedPanelItems().getCtPanelSuppliers().add(index, supplier); 
			    }
		  }
	}	
	
	public List<SelectItem> populateBankAccountList(SmSupplier smSupplier, int index) {		
		 List<SelectItem> populateBankAccountList = new ArrayList<SelectItem>();
		 populateBankAccountList.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
		 ISupplierSearchSO supplierSearchSO = null;
	     SupplierSearchReqDTO supplierReqDTO = null;
	     SupplierSearchResDTO supplierResDTO = null;
	     supplierReqDTO = new SupplierSearchReqDTO();
	     supplierReqDTO.setSupplierId(smSupplier.getSupplierId());
		 supplierSearchSO = (ISupplierSearchSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,ISupplierSearchSO.class);
	     supplierResDTO = supplierSearchSO.retrieveSmSupplierBankBySupplier(supplierReqDTO);
	     if (supplierResDTO.getSmSupplierBankList() != null) {
		    for (SmSupplierBank smSupplierBank : supplierResDTO.getSmSupplierBankList()) {
			      if (smSupplierBank.getRecordStatusFlg().intValue() == DbValueConst.RECORD_STATUS_ACTIVE) {
			    	     String account = smSupplierBank.getAccountNo();
					     if (smSupplierBank.getPmFinancialOrg() != null 
							   && smSupplierBank.getPmFinancialOrg().getFinOrgName() != null) {
						       account = account + ContractConst.SPACE_SLASH + smSupplierBank.getPmFinancialOrg().getFinOrgName();
					     }
					     populateBankAccountList.add(new SelectItem(smSupplierBank.getAccountNo(), account));
					     if (!getManagedBean().getSupplierBankAccountMap().containsKey(smSupplierBank.getAccountNo())) {
						     getManagedBean().getSupplierBankAccountMap().put(smSupplierBank.getAccountNo(), smSupplierBank);
					     }
			       }
		      }
	     }
		return populateBankAccountList;
	}
	
	/**
	 * Method used to delete list of supplier details
	 * from panel circular item list
	 */
	public void deletePanelSupplier(){
		
		try {
			if(this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelZone() != null) {
				this.getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().removeAll(this.getManagedBean().getSelectedPanelSuppliers());
			}
			else if(!this.getManagedBean().isSelectionOfZone() && this.getManagedBean().getSelectedPanelItems() != null) {
				this.getManagedBean().getSelectedPanelItems().getCtPanelSuppliers().removeAll(this.getManagedBean().getSelectedPanelSuppliers());
			}
			
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Set the selected supplier details in managed bean
	 * 
	 * @param ctPanelSupplier  the selected supplier
	 */
	public void showSupplierDetails(CtPanelSupplier ctPanelSupplier) {
		QTSupplierResDTO qtSupplierResDTO = new QTSupplierResDTO();
		MofAccountResDTO mofAccResDTO = new MofAccountResDTO();
		IQTSupplierSO qtSupplierSO = null;
		IMofAccountSO mofAccountSO = null;
		
		try {
			if (ctPanelSupplier != null) {
				getManagedBean().setCtPanelSupplier(ctPanelSupplier);
				qtSupplierSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTSupplierSO.class);
				QTSupplierReqDTO qtSupplierReqDTO = new QTSupplierReqDTO();
				qtSupplierReqDTO.setSupplierId(ctPanelSupplier.getSupplier().getSupplierId());
				qtSupplierResDTO = qtSupplierSO.retrieveSupplierDetails(qtSupplierReqDTO);
				//smSupplier = qtSupplierResDTO.getSmSupplier();
				getManagedBean().setSmSupplier(qtSupplierResDTO.getSmSupplier());
				
				mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
				MofAccountReqDTO mofAccReqDTO = new MofAccountReqDTO();
				mofAccReqDTO.setSupplierId(ctPanelSupplier.getSupplier().getSupplierId());
				mofAccResDTO = mofAccountSO.retrieveMofNoBySupplierId(mofAccReqDTO);
				getManagedBean().setSmMofAccount(mofAccResDTO.getMofAccount());
	
				SmCompanyBasic smCompanyBasic = ctPanelSupplier.getSupplier().getSmAppl().getSmCompanyBasic();
				getManagedBean().setSmCompanyBasic(smCompanyBasic);
	
				List<SmPersonnel> smPersonnelList = ctPanelSupplier.getSupplier().getSmAppl().getSmPersonnels();
				for (SmPersonnel smPersonnel : smPersonnelList) {
					if (smPersonnel.isContactPerson()) {
						getManagedBean().setSmPersonnel(smPersonnel);
						break;
					}
				}
			}
		} finally {
			qtSupplierSO = null;
			mofAccountSO = null;
		}
	}
	
	
	/**
	 * Method used to display type of circular for user selection
	 * to cancel cancel of circular
	 */
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
	
	/**
	 * Method used to add list of circular for cancel
	 */
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
	
	/**
	 * Method used to delete list of circular for circular list
	 */
	public void deleteCancelCircular(){
		log.info("PanelBackingBean.deleteCancelCircular()");
		try {
			getManagedBean().getCircularVer().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelCirculars());
			getManagedBean().getCancelledCircularList().addAll(getManagedBean().getSelectedCancelCirculars());		
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to add item details into list of 
	 * panel circular item details
	 */
	public void addPanelItem(){
		log.info("PanelBackingBean.addPanelItem()");
        try {	
        		 CtPanelItem panelItem = new CtPanelItem();
				 panelItem.setItemCategoryId(new PmParameter());
				 panelItem.setPanelCoverageId(new PmParameter());
				 panelItem.setPmUom(new PmUom());
				 panelItem.setLocationLevelId(new PmParameter());
				 panelItem.setCtPanelZones(null);
				 panelItem.setItemType(new PmParameter());
				 getManagedBean().getCircularVer().getCtPanelItems().add(panelItem);
        	 
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to delete item details from list of 
	 *panel circular item details
	 */
	public void deletePanelItem(){
			getManagedBean().getCircularVer().getCtPanelItems().remove(this.getManagedBean().getSelectedPanelItems());
			this.getManagedBean().setSelectedPanelItems(null);
			this.getManagedBean().setSelectionOfZone(false);
	}
	
	/**
	 * Method used to add zone into panel item details
	 */
	public void addPanelZone(){
		CtPanelZone ctPanelZone = new CtPanelZone();
		this.getManagedBean().getSelectedPanelItems().getCtPanelZones().add(ctPanelZone);
	}
	
	/**
	 * Method used to delete zone from panel item details
	 */
	public void deletePanelZone(){
		this.getManagedBean().getSelectedPanelItems().getCtPanelZones().remove(getManagedBean().getSelectedPanelZone());
		this.getManagedBean().setSelectedPanelZone(null);
		getManagedBean().setButtonHideActiveDel(true);
	}

	/**
	 * Method used to delete zone from panel item details
	 */
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
	
	/**
	 * Method to remove cancel circular from circular details
	 * */
	public void deleteCancelReqCircular() {
		log.info("PanelBackingBean.deleteCancelReqCircular()");
		try {
			getManagedBean().getCancelCircularReq().getCtCancelCircularList().removeAll(getManagedBean().getSelectedCancelledCircularList());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method to retrieve list of state as per country id
	 * Construct drop down for user selection
	 * Return SelectItem object
	 * */
	public SelectItem[] getStateDropdownValues() {
		SelectItem[] items = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(getCountryId());
			StateResDTO resDTO = stateSO.retriveStatesListByCountryID(reqDTO);
			
		    items = new SelectItem[resDTO.getStateList().size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
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
	
	/**
	 * Method to retrieve list of circular types as per Doc
	 * Construct drop down for user selection
	 * Return SelectItem object
	 * */
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
	
	/**
	 * Method to retrieve zonal level list as per district and state
	 * Construct drop down for user selection
	 * Return SelectItem object
	 * */
	public SelectItem[] getZonalLevelDropDown() {
		SelectItem[] items = new SelectItem[3];
		items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
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
	
	/**
	 * User need to make decision on panel circular maintenance page
	 * Navigate to cancel panel circular page as per user selection 
	 * */
	public String gotoCancelPage() {
		
		    String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
			Long circularVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("circularVerId"));
			log.info("Action :"+action+" Id :"+circularVerId);
			String docType = this.getManagedBean().getCircularDocType();
		    CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
				
			if(docType.equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)) {
				CentralContractBackingBean ccBB = new CentralContractBackingBean();
				
				ccBB.getManagedBean().setSelectedTabIndex(0);
				ccBB.getManagedBean().setAction(action);
				ccBB.getManagedBean().setUpdateAction(false);
				ccBB.getManagedBean().setViewContract(true);
				ccBB.getManagedBean().setOutComes(new String[]{});
				ccBB.getManagedBean().setChkCancelPage(true);			
				ccBB.setContractCircularVar(reqDTO);
				return "/xhtml/ct/circular/centralcontract/circularBase.xhtmll?faces-redirect=true";
			}
			else {
				PanelBackingBean panelBB = new PanelBackingBean();
				
				panelBB.getManagedBean().setSelectionOfZone(false);
				panelBB.getManagedBean().setSelectedPanelItems(null);
				panelBB.getManagedBean().setSelectedPanelZone(null);
				panelBB.getManagedBean().setSelectedTabIndex(0);
				panelBB.getManagedBean().setAction(action);				
				panelBB.getManagedBean().setUpdateAction(false);
				panelBB.getManagedBean().setViewContract(true);
				panelBB. getManagedBean().setOutComes(new String[]{});	
				panelBB.getManagedBean().setChkCancelPage(true);
				panelBB.setPanelCircularVar(reqDTO);
				return "/xhtml/ct/circular/panel/circularBase.xhtml?faces-redirect=true";
			}
	}
	
	/**
	 * The method used to upload  file for circular
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */	
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
	
	/**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile() throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = getManagedBean().getCircularVer().getCircularFileName();
		String filePath = DocumentRepositoryUtils.getDBFilePath(getManagedBean().getCircularVer().getCircularFilePath());
		
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
	
	/**
	 * Method to retrieve types of item
	 * @param pass paramterId for item details
	 * Return string type parameter code
	 * */
	public String getItemGeneralCode(Long parameterId){
		if(parameterId == null || parameterId.intValue() == 0)
			return ParameterConst.ITEM_CATEGORY_TYPE_SPECIFIC;
		
		  IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
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
	
	/**
	 * Method to set selected types item as per zone or 
	 * without zone
	 * Set boolean type variable
	 * **/
	public void setPanelListner(){
		
		 String panelCoverageICode = getItemGeneralCode(this.getManagedBean().getSelectedPanelItems().getZoneAllParamterId());
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
	
	/**
	 * Method to set selected item object for zone and without zone
	 * @param need to pass ctPanelItem Object
	 * return void statement
	  **/
	public void setCtPanelZoneListner(CtPanelItem panelItem){
		
		   String panelCoverageICode = getItemGeneralCode(panelItem.getZoneAllParamterId());
		   getManagedBean().setSelectedPanelItems(panelItem);
		   if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ZONE)) {
	 		      getManagedBean().setSelectedPanelZone(null);
	 		      getManagedBean().getSelectedPanelItems().setCtPanelSuppliers(null);
	 		      getManagedBean().getSelectedPanelItems().setCtPanelZones(null);
			      getManagedBean().setSelectionOfZone(true);
			    
			      if(getManagedBean().getSelectedPanelItems().getLocationLevelId() == null)
			    	  getManagedBean().getSelectedPanelItems().setLocationLevelId(new PmParameter());
			      
			      getManagedBean().getSelectedPanelItems().getLocationLevelId().setParameterId(retrieveParameterByTypeAndCode());
			   
	 	   } else if(panelCoverageICode.equalsIgnoreCase(ParameterConst.CIRCULAR_PANEL_COVERAGE_ALL)) {
	 		  getManagedBean().getSelectedPanelItems().setCtPanelZones(null);
	 		  getManagedBean().getSelectedPanelItems().setCtPanelSuppliers(null);
	 		   getManagedBean().setSelectionOfZone(false);   
		   } else {
			    this.getManagedBean().setSelectionOfZone(false);
			    this.getManagedBean().setSelectedPanelItems(null);
			    this.getManagedBean().setSelectedPanelZone(null);
		}
	}
	
	/**
	 * Method to retrieve parameter id as per code
	 * @param need to pass location level and state level
	 * return object long
	  **/
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
	
	/**
	 * Method to retrieve country id  as per code
	 * @param need to pass country code
	 * return object long
	  **/
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
	
	/**
	 * Method to construct zone tree map as location
	 * @param need to pass parameter id 
	 * Construct tree pop up for location
	  **/
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
	
	/**
	 * Method to construct drop down for location 
	 * @param need to pass state level and district level code
	 * Return SelectItem object
	  **/
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
	
	/**
	 * Method to retrieve list of zone details for tree node
	 * @param need to pass TreeNode head
	 * Return  list of CtPanelZoneDtl object
	  **/
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
	
	/**
	 * Method to update tree node for zones
	 * Construct a tree node structure for zone details
	 * @throws warning message if zone name is null
	  **/
	public void updateZoneGeneralLocation() {
		
		if(getManagedBean().getZoneName() == null || getManagedBean().getZoneName() == ""){
			throw new EpException(MessageConst.ERROR_755002);
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
							   throw new EpException(MessageConst.ERROR_755001);
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
						   throw new EpException(MessageConst.ERROR_755001);
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
	
	/**
	 * Method to edit tree map zone details
	 * @param pass CtPanelZone object and int index
	 * Construct the tree on basis of location zone district and state 
	 * **/
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
	
	/**
	   *   Method to set zone status active
	   *   Return string vaule 1
	  **/
	public String getZoneStatus(){
		
			return "1";
	}
	
	/**
	   *   Method to delete selected items from list of items
	  **/
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
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	  /**
	   *   Method to bulk update of item category and zone of 
	   *    panel circular item details
	   *    Set boolean value zones false
	  **/
	  public void bulkUpdateRow(){
		   try {
			       if(getManagedBean().getCircularVer().getCtPanelItems() != null) {
				           Iterator<CtPanelItem> panelItemIterator = getManagedBean().getCircularVer().getCtPanelItems().iterator();
				           while (panelItemIterator.hasNext()) {
					                  
				        	            CtPanelItem  item = panelItemIterator.next();
					                    if(item.isSelectedCheckBox()) {
					                    	
					                    	    if(getManagedBean().getPanelCoverageId() != null) 
						                             item.setZoneAllParamterId(getManagedBean().getPanelCoverageId());
					                    	 
					                    	    if(getManagedBean().getItemCategoryId() != null)
					                    	         item.setCategoryParameterId(getManagedBean().getItemCategoryId());
					                    }
				              }
			          }
			   getManagedBean().setSelectedPanelItems(null);
			   getManagedBean().setSelectionOfZone(false);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	  /**
		 * Method used to set new effective  and end date 
		 *  for panel circular during update 
		 */
	public void extendStartEndDate(){
		
		if(getManagedBean().isAutoUpdate()){
			Calendar c = Calendar.getInstance();
			c.setTime(getManagedBean().getCircularVer().getEndDate());
			c.add(Calendar.DATE, 1);
			getManagedBean().getCircularVer().setExtendStartDate(c.getTime());
		}
	}
	
	 /**
     * Method to set boolean value for checkbox 
     * @param pass ajax event type
     * Set boolean value for user action 
     * */
    public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActiveDel(false);
		}else{
			getManagedBean().setButtonHideActiveDel(true);
		}	
	}
	
    /**
     * Method to fetch supplier address from supplier management
     * @param supplierId Long
     * @return smSupplier SmSupplier Object
     * */ 
	private SmSupplier retrieveSupplierInfo(Long supplierId) {
		ICircularSO circularSO = null;
		CircularReqDTO reqDTO = null;
		try {
			circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
           	reqDTO = new CircularReqDTO();
           	reqDTO.setSupplierId(supplierId);
            CircularResDTO resDTO = circularSO.retrieveSupplierById(reqDTO);
		    return resDTO.getSmSupplier();
		   } catch(Exception ex){			
		}
		return null;
	}

	/**
	 * Method to retrieve description of parameter
	 * @param String type parameter types
	 * Return String type of description
	 * */
	public List<PmParameterDesc> retrieveParameterDescByType(String type) {
		return ParameterHelper.getInstance().getParameterDescList(type);

	}

	/**
	 * Method to set location for supplier item
	 * */
	public void selectLocation(AjaxBehaviorEvent event) {
		Long location = (Long) event.getComponent().getAttributes().get("value");
		getManagedBean().getSelectedPanelItems().getLocationLevelId().setParameterId(location);
		getManagedBean().getSelectedPanelItems().getCtPanelZones().clear();
		if(getManagedBean().getSelectedPanelZone() != null && 
		    getManagedBean().getSelectedPanelZone().getCtPanelSuppliers() != null 
			&& getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().size() > 0)
	      	getManagedBean().getSelectedPanelZone().getCtPanelSuppliers().clear();
	}
	
	/**
	 * Method to update bulk of item details of panel circular
	 * @throws exception if item not selected
	 * Show pop up dialog box
	 * */
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
	
	/**
	 * The method used to supplier account mof details
	 * @param  smSupplier object
	 * @return String type mof no
     */
	private String retrieveMofNo(SmSupplier smSupplier) {
		IMofAccountSO mofAccountSO = (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
		MofAccountReqDTO reqDTO = new MofAccountReqDTO();
		reqDTO.setSupplierId(smSupplier.getSupplierId());
		MofAccountResDTO resDTO = mofAccountSO.retrieveMofNoBySupplierId(reqDTO);
		if(resDTO.isSuccessful() && resDTO.getMofAccount()!=null) {
			return resDTO.getMofAccount().getMofNo();
		}
		return null;
	}
	
	/**
	 * The method used to set value for pre check panel 
	 * circular security and authentication
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the savePanelCircular
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String preSubmitPanelCircular(
			PmDigiSign pmDigiSign) {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		return savePanelCircular(getManagedBean().getAction());
	}
	
	/**
	 * Method to set @param and show dialog box
	 */
	public void setPanelDetailsValue(){
		if(getManagedBean().getCircularVer().getCircularNo() == null || getManagedBean().getCircularVer().getName() == null
				 ||getManagedBean().getCircularVer().getStartDate() == null || getManagedBean().getCircularVer().getEndDate() == null) {
				         RequestContext.getCurrentInstance().update(":form:panelCircularId");
				} else {
			    	RequestContext.getCurrentInstance().execute("uploadCircularDlg.show()");
			   }
	}
	
	/**
	 * Method to retrieve portal name
	 */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.panel.circular.creation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
	
	/**
	 * Method to retrieve portal details title for cancel
	 */
	public String getCancelPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.cancel.circular.request", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
	
	/**
	 * Method used to authenticate user during panel circular creation
	 * Method will invoke a confirmation dialog for user authentication
	 * Method check pre validation and call save method
	 * 
	 * @param  need to pass user action such as outcome 
	 * @return result of the saveCircuar process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String gpkiSignAction(String outcome) throws Exception{
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			    validateCircular(outcome);
			    SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATETIME_WITH_SECONDS_FORMAT);
			    // Temp setTextBeforeSign : CircularNo+StartDate+EndDate+Type
			    StringBuilder sb = new StringBuilder();
			    sb.append(getManagedBean().getCircularVer().getCircularNo());
			    sb.append(ContractConst.SEPRATE_LINE);
			    if(getManagedBean().getCircularVer().getCtCircular().getLatestCircularVer() != null){
				      sb.append(ContractConst.CIRCULAR_UPDATE);
				      sb.append(ContractConst.SEPRATE_LINE);
				      if(getManagedBean().getCircularVer().getExtendStartDate() != null)
				            sb.append(df.format(getManagedBean().getCircularVer().getExtendStartDate()));
				      else
				    	  sb.append(df.format(getManagedBean().getCircularVer().getStartDate())); 
					  sb.append(ContractConst.SEPRATE_LINE);
					  if(getManagedBean().getCircularVer().getExtendEndDate() != null)
					       sb.append(df.format(getManagedBean().getCircularVer().getExtendEndDate()));
					  else
						  sb.append(df.format(getManagedBean().getCircularVer().getEndDate()));
					  sb.append(ContractConst.SEPRATE_LINE);
		        }else{
			         sb.append(df.format(getManagedBean().getCircularVer().getStartDate()));
			         sb.append(ContractConst.SEPRATE_LINE);
			         sb.append(df.format(getManagedBean().getCircularVer().getEndDate()));
			         sb.append(ContractConst.SEPRATE_LINE);
		        }
			    sb.append(outcome);
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());
			    getManagedBean().setTextBeforeSign(sb.toString());
			    // set action
			    getManagedBean().setAction(outcome);
			    getManagedBean().setAutoUpdate(Boolean.FALSE);
			    RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			 return null;
		   } else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			     RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			     return null;
		   } else {
			   return savePanelCircular(outcome);
		  }
	}
	
	/**
   	 * Method used to auto update of request
   	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
	 * Common method to all module that need to access panel circular
	 * Navigate on panel circular view page
	 * Retrieve panel circular details
	 * @param need to pass circularVer Id
	 */
	public String gotoCommonViewPage(Long circularVerId) {
	
		    CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(circularVerId);
			getManagedBean().setSelectionOfZone(false);
			getManagedBean().setSelectedPanelItems(null);
			getManagedBean().setSelectedPanelZone(null);
			getManagedBean().setSelectedTabIndex(0);
	        getManagedBean().setUpdateAction(false);
			getManagedBean().setViewContract(true);
			setPanelCircularVar(reqDTO);
		//	extendStartEndDate();
			return "/xhtml/ct/circular/portal/panelCircularTab.xhtml?faces-redirect=true";
	}
	
	/**
	 * Method used to set new effective date  for panel circular
	 * during update 
	 */
	public void chkCircularExtension(){
		if(getManagedBean().isCircularExtension()) {
				Calendar c = Calendar.getInstance();
				c.setTime(getManagedBean().getCircularVer().getEndDate());
				c.add(Calendar.DATE, 1);
				getManagedBean().getCircularVer().setExtendStartDate(c.getTime());
		} else {
			getManagedBean().getCircularVer().setExtendStartDate(null);
			getManagedBean().getCircularVer().setExtendEndDate(null);
		}
	}
	
	/**
     * The method used to retrieve the approver role code base on doc type.
     * 
     * @return the roleCode
     */
	private String getApproverRoleCodeByDocType() {
		String roleCode = null;
		if(getManagedBean().getCircularDocType() != null && getManagedBean().getCircularDocType().equalsIgnoreCase(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT)) {
			roleCode = RoleCode.BPK_CQ_APPROVER.value();
		} else {
			roleCode = RoleCode.BPK_PQ_APPROVER.value();
		}
		return roleCode;
	}
	
	/**
	 * Method used to retrieve the available approverList base on doc type 
	 * during Cancel Circular Request 
	 */
	public void retrieveApproverList() {		
		getManagedBean().getApproverModel().setSource(getAvailableApproverList(getApproverRoleCodeByDocType()));
		getManagedBean().getApproverModel().setTarget(new ArrayList<PmUser>());
	}
	
	public void deleteAttachFile(){
		getManagedBean().getCircularVer().setCircularFileName(null);
		getManagedBean().getCircularVer().setCircularFilePath(null);
	}
	
	/**
	 * This method used to retrieve 
	 * deduction type from database 
	 * @return menu item, List of deduction types
	 */
	public SelectItem[] getDropdownPidCodeValues() {
		
		    SelectItem[] items = null;
		    ReasonReqDTO reqDTO = null;
		    IReasonSO reasonSO = null;
		    List<PmReasonDesc> list = null;
		    ReasonResDTO resDTO  = null;
		    ISearchBean<PmReasonDesc> searchBean = new SearchBean<PmReasonDesc>();
		    int i = 1;
		    try {
		    	   
		    	    List<String> pmReasonType = new ArrayList<String>();
		    	    pmReasonType.add(ProfileManagementConst.PID_REASONTYPE_CHEAPER_PRICE);
			        pmReasonType.add(ProfileManagementConst.PID_REASONTYPE_LESS_QUANITY);
			        pmReasonType.add(ProfileManagementConst.PID_REASONTYPE_UNABLE_SUPPLIER);
			        pmReasonType.add(ProfileManagementConst. PID_REASONTYPE_SPECIFICATION);
			        pmReasonType.add(ProfileManagementConst.PID_REASONTYPE_OTHER);
			        
			        reqDTO = new ReasonReqDTO();
			        reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
			        reqDTO.setSortBy("pmReason.reasonId");
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.reasonCategoryCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), ReasonCategory.PID_JUSTIFICATION.value()));
			        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.reasonCode", SearchOperator.OPERATOR_IN.value(), pmReasonType));
			        reqDTO.setReasonDescSearchBean(searchBean);
			        
			        reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
			        resDTO = reasonSO.retrieveReasonDescBySearchBean(reqDTO);
			        list = resDTO.getReasonDescResultBean().getResultList();
			        items = new SelectItem[list.size() + 1];
			        items[0] = new SelectItem(null, ResourceBundleUtils.getMessage(
				     	"lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, getFacesScopes().getUserLocale()));
		         	for (PmReasonDesc reasonDesc : list) {
				          items[i++] = new SelectItem(reasonDesc.getPmReason().getReasonId(), reasonDesc.getReasonDesc());
			        }
		     } catch (Exception e) {
			        e.printStackTrace();
			        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		     } finally {
		    	    reqDTO = null;
				    reasonSO = null;
				    list = null;
				    searchBean = null;
				    resDTO = null;
		     }
	    return items;
	}
	
	 /**
	 * This method used to
	 *  add new record in datatable
	 */
	public void addPurchaseInDefault(){
		CtCircularPidReason ctCircularPidReasons = new CtCircularPidReason();
		ctCircularPidReasons.setPmReason(new PmReason());
		getManagedBean().getCircularVer().getCtCircularPidReason().add(ctCircularPidReasons);
	}
	
	 /**
	 * This method used to
	 *  delete old record from datatable
	 */
	public void deletePidPurchase(){
		try {
			getManagedBean().getCircularVer().getCtCircularPidReason().removeAll(getManagedBean().getCtCircularPidReasons());
			getManagedBean().setButtonHideActiveDel(true);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * This method used to 
	 * compare PID Reason other with other
	 * @Reason Id reasonId
	 * @return boolean value
	 */
	public boolean PidPurchaseTypeOther(Long reasonId){
		
		if(reasonId == null || reasonId.intValue() == 0)
		return false;
		
		ReasonReqDTO reqDTO = null;
		IReasonSO reasonSO = null;
		PmReason pmReason = null;
		try {
				 reqDTO = new ReasonReqDTO();
				 reqDTO.setReasonId(reasonId);
				 reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
		         reasonSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IReasonSO.class);
		         pmReason = reasonSO.retrieveReasonById(reqDTO).getPmReason();
		         if(pmReason.getReasonCode().equalsIgnoreCase(ProfileManagementConst.PID_REASONTYPE_OTHER)){
			          return true;
		         }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally{
			 reqDTO = null;
			reasonSO = null;
			pmReason = null;
		}
		return false;
	}
	
	/**
     * Method to set boolean value for checkbox 
     * @param pass ajax event type
     * Set boolean value for user action 
     * */
	
	public void onToggleSelect(ToggleSelectEvent event) {
		getManagedBean().setButtonHideActiveDel(false);
	}
	
	public void onRowSelect(SelectEvent event) {
		getManagedBean().setButtonHideActiveDel(false);
	}
 
    public void onRowUnselect(UnselectEvent event) {
    	getManagedBean().setButtonHideActiveDel(true);
    }
}
