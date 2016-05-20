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
import java.math.BigDecimal;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
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
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
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
import my.ep.persistence.entity.ct.circular.dtl.CtCircularPidReason;
import my.ep.persistence.entity.ct.circular.dtl.CtContactPerson;
import my.ep.persistence.entity.ct.circular.dtl.CtExemptedOrg;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.ReasonCategory;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.circular.cc.CentralContractManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Central Contract Circular Backingbean
 * 
 * @author alam
 * @since 9/10/2013
 */
@ManagedBean (name = "centralContractBackingBean") 
@ViewScoped
public class CentralContractBackingBean extends BaseBackingBean<CentralContractManagedBean>{
	
	private static Logger log = Logger.getLogger(CentralContractBackingBean.class);
	private ContractBackingBean ctBB = null;
	//private PmOrgProfile[] selectedPmOrgProfile;
	private CtExemptedOrg[] selectedExemptedOrgs;
	private static String baseDir = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_BASE_DIR);
	private IPagingDataModel<PmOrgProfile> exemptedOrgDataModel = new ExemptedOrgDataModel();	

	/**
		 * This constructor method used to initialize the variable on start up
		 * Setting button if user not call from BPM
		 * Method used to initialize approver list
		 */
	public CentralContractBackingBean(){
		
			if(getManagedBean().getOutComes()==null) {
				getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			    /*Initialization of circular object*/
				getManagedBean().setCircularVer(new CtCircularVer());
				PmStatus status = new PmStatus();
				status.setStatusId(ContractStatusConst.CQ_PENDING_SUBMISSION);
				getManagedBean().getCircularVer().setStatus(status);
				 /*Initialization of contract details backing bean*/
				 initializeRemarks();
				if(ctBB == null) {
				      ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
				      ctBB.initializeRemarkAndAttachement(null, getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
				}
			    
			}			
		
			if(getManagedBean().getUserMap().size()==0) {
				 initializeApproverList();
				 getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    }
	}
	
	/**
     * This method used to retrieve user login  information
	 */
	private FacesScopes getFacesScopes() {
	    	 return new FacesScopes();
	}
	
	/**
	 * This  method used to initialize approver list
	 */
	public void preRenderCircular() {			
			renewSesionObject();
			 getManagedBean().setFileDirectory(baseDir+"/CT/Circulars/"+ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "circularBase.xhtml?faces-redirect=true");
	}

	/**
	 * This  method call once user navigate central circular  page from task list
	 * Method used to retrieve all information from BPM
	 * Method used to retrieve circular information from BPM
	 * Method return null if task id null
	 */
	public void preRenderView() {
		
				HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(getFacesScopes().getRequest());
				HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
				String taskId = originalServletRequest.getParameter("taskId");
				if(taskId==null) {
					return;
				}
				
				
				CTCircularContractDataType dataType = super.getBpmTaskDetails(CTCircularContractDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularVerId(dataType.getDocId().longValue());
				setContractCircularVar(reqDTO);
				getManagedBean().setSelectedTabIndex(0);						
	   }
	
	   /**
	    * This  method used to set retrieve value from BPM or contract maintenance
       */
    	public void setContractCircularVar(CircularReqDTO reqDTO){
		
	    	ICircularSO circularSO = null;
	        CircularResDTO resDTO = null;
		     try {
		    	        circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, ICircularSO.class);
			            resDTO = circularSO.retrieveCircularVerById(reqDTO);
		
		               getManagedBean().setCircular(resDTO.getCircularVer().getCtCircular());
		               getManagedBean().setCircularVer(resDTO.getCircularVer());
		    
		               if(getManagedBean().getCircularVer().getExtendStartDate() != null && getManagedBean().getCircularVer().getExtendEndDate() != null){
		    	                 getManagedBean().setCircularExtension(true);
		               }
		   
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
		              
		              if(getManagedBean().getUserMap().size()==0) {
		 				 initializeApproverList();
		 		    }	
		   
		                getManagedBean().setRemarkList(resDTO.getRemarkList());
		                getManagedBean().setAttachmentList(resDTO.getAttachmentList());
			            getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		                if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
		                	getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
		                	List<PmUser> invalidApproverList = new ArrayList<PmUser>();
		                	for (PmUser approverUser : resDTO.getApproverList())  {
		                		Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();	
		                		boolean isValidApprover = false;
		                		while (sourceIt.hasNext()) {
		                			PmUser user = sourceIt.next();
		                			if (user.getUserId() == approverUser.getUserId()) {
		                				sourceIt.remove();
		                				isValidApprover = true;
		                				break;
		                			}
		                		}
		                		if (!isValidApprover) {
		                			invalidApproverList.add(approverUser);
		                		}
		                	}
		                	if (invalidApproverList.size() > 0) {
		                		getManagedBean().getApproverModel().getTarget().removeAll(invalidApproverList);
		                	}
			              } else {
			  				getManagedBean().getApproverModel().setTarget(new ArrayList<PmUser>());
			  			}
			
			            ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			            ctBB.getManagedBean().setViewContract(getManagedBean().isViewContract());
		           	    ctBB.initializeRemarkAndAttachement(resDTO.getCircularVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());	
			            initializeRemarks();
		        } catch (Exception e) {
					    e.printStackTrace();
					    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			   	} finally {
					circularSO = null;
					resDTO = null;
				}
	   }
	
	/**
	 * This  method used to initialize approver list
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}
	
	/**
	 * The method used for searching the contract to associate from circular.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see Exception
	 */
	public void searchContract(){
		IContractSO contractSO = null;
		try { 
			contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			
			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.CONTRACT_TYPE));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractType.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),ParameterConst.CONTRACT_TYPE_CENTRAL));
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.contractVerId",SearchOperator.OPERATOR_IS_NOT_NULL.value(),null));
			
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			if (userResDTO.getUserOrgList().size() > 0)
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ownerOrgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(), userResDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId()));
			
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
	
	/**
	 * The method used for to retrieve  associate circular with current circular for cancel
	 */
	public void addCancelCircularListener() {
		   ICircularSO circularSO = null;
		   CircularResDTO resDTO = null;
		
		   try {
			          if(getManagedBean().getCancelledCircularList()==null) {
				             CircularReqDTO reqDTO = new CircularReqDTO();
				             ISearchBean<CtCancelCircular> searchBean = new SearchBean<CtCancelCircular>();
				             searchBean.getDataFilterBeans().add(new SearchFilterBean("circularVer.ctCircular.circularType", 
						      SearchOperator.OPERATOR_EQUALS.value(), ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT));
				              reqDTO.setCancelCircularSearchBean(searchBean);
				
				              circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
				              resDTO = circularSO.searchCancelledCircular(reqDTO);
				              if(resDTO.isSuccessful()) {
					                getManagedBean().setCancelledCircularList(resDTO.getCancelCircularResultBean().getResultList());
			                  }
		           	 }
			
			          getManagedBean().setSelectedCancelledCircularList(new ArrayList<CtCancelCircular>());
			         RequestContext.getCurrentInstance().execute("cancelCircularDlg.show()");
		      } catch (Exception e) {
			         e.printStackTrace();
			         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		      }finally{
		    	  resDTO = null;
		    	  circularSO = null;
		      }
	   }
	
		/**
		 * The method used  to add  associate circular with current circular for cancel
		 */
		 public void addCancelledCircular() {
			  try {	
	        	        
	        	        List<CtCancelCircular> cancelCirculars = new  ArrayList<CtCancelCircular>();
	        	        Iterator<CtCancelCircular> ctCancelCirculars = getManagedBean().getCancelledCircularList().iterator();
					     while(ctCancelCirculars.hasNext()){
					    	CtCancelCircular  ctCancelCircular = ctCancelCirculars.next();
						    if(ctCancelCircular.isSelectedCheckBox()){
						    	ctCancelCircular.setCancelCircularId(null);
						    	ctCancelCircular.setCtCancelCircularReq(null);
						    	ctCancelCircular.setSelectedCheckBox(false);
						    	cancelCirculars.add(ctCancelCircular);
						    }
					     }
	        	
	        	        getManagedBean().getCircularVer().getCtCancelCircularList().addAll(cancelCirculars);
	        	        getManagedBean().getCancelledCircularList().removeAll(cancelCirculars);
		     	} catch (Exception e) {
				           e.printStackTrace();
				          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     	}
		}
	
	/**
	 * The method used to delete  associate circular with current circular 
	 */
	public void addSelectedContract() {
		
         /* if(getManagedBean().getSelectedContractList() == null || getManagedBean().getSelectedContractList().isEmpty()){
        		throw new EpException(MessageConst.ERROR_755015);
        	}*/
          
           IContractSO contractSO = null;
           ContractReqDTO reqDTO  = null;
           CtCircularContract circularContract = null;
        	try {
        	          contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
        	          reqDTO = new ContractReqDTO();
        	          Iterator<CtContract> ctContracts = getManagedBean().getContractList().iterator();
     			     while(ctContracts.hasNext()){
     			    	CtContract  contract = ctContracts.next();
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
		    } finally{
		    	contractSO = null;
		    }
	 }
	
	/**
	 * The method used to set role of central contract admin
	 */
	public boolean isCentralContractAdmin() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value());
	}
	
	/**
	 * The method used to retrieve of login user role
	 */
	private String getUserRole() {
		String userRole = null;
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_CIRCULAR_CONTRACT)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_CIRCULAR_CONTRACT)) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_CIRCULAR_CONTRACT))) {
			if (getFacesScopes().hasRoleUser(RoleCode.BPK_CQ_APPROVER.value())) {
				userRole = RoleCode.BPK_CQ_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
	
	 /**
	 * This method used to save central circular 
	 * This method call validate function to validate circular details
	 *  This method will navigate to acknowledgment page with
	 *   a confirmation message once submit successfully
	 */
	public String saveCircular(String action){
	
		ICircularSO circularSO = null;
		try {
			       circularSO = (ICircularSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,ICircularSO.class);
	               if(!getManagedBean().getCircularVer().isPid()){
				        getManagedBean().getCircularVer().setCtCircularPidReason(null);
			        }
			       
	               if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL))
			             validateCircular(action);
			
			       CtCircular circular = getManagedBean().getCircular();
			       if(circular==null) {
				       circular = new CtCircular();
				       circular.setCircularType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			       }
			  
			       CtCircularVer circularVer = getManagedBean().getCircularVer();
		           if(circularVer.getDocType()==null) {
				      circularVer.setDocType(ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR);
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
			     reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			     reqDTO.setUserRole(getUserRole());
			     reqDTO.setTaskName(getManagedBean().getTaskName());
			     reqDTO.setLanguageCode(getFacesScopes().getUserLocale().getLanguage());
			    reqDTO.setCircular(circular);
			    reqDTO.setCircularVer(circularVer);
			    reqDTO.setUpdateAction(getManagedBean().isUpdateAction());
			    if(getManagedBean().getPmDigiSign() !=null){
				    reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			    }
			
			   CircularResDTO resDTO = circularSO.saveCircular(reqDTO);
			   if(resDTO.isSuccessful()) {
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
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, getManagedBean().getCircularVer().getDocType()), 
								resDTO.getCircular().getCircularNo()}, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));

			     if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}else{
					reqDTO = new CircularReqDTO();
					reqDTO.setCircularVerId(resDTO.getCircularVer().getCircularVerId());
					getManagedBean().setUpdateAction(false);
					getManagedBean().setViewContract(false);
					setContractCircularVar(reqDTO);
				}
				
			  } else {
					throw new EpException(MessageConst.ERROR_700020);
			}
		  } finally {
			  circularSO = null;
		  }
		return null;
	}
	
	 /**
	 * This method used to validate central circular details
	 */
	public void validateCircular(String outcome) {
		  IQTAwardSO qtAwardSO = null;
		  IContractSO contractSO = null;
		  try {
			        qtAwardSO = (IQTAwardSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,IQTAwardSO.class);
			        contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			        getManagedBean().setAutoUpdate(true);
		
			        //Validation for PID
			        if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getCircularVer().isPid() && 
					     (getManagedBean().getCircularVer().getCtCircularPidReason() == null || getManagedBean().getCircularVer().getCtCircularPidReason().size() == 0)) {
				        throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        }
			
			       if(getManagedBean().getCircularVer().getCtCircularPidReason() != null 
					    && getManagedBean().getCircularVer().getCtCircularPidReason().size() > 0 ){
				             for(CtCircularPidReason reason : getManagedBean().getCircularVer().getCtCircularPidReason()){
					              if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){
						                throw new EpException(MessageConst.ERROR_100022, 
									         new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					               }
					  
					              if(PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
						                 if(reason.getReasonDesc().isEmpty()){
							                  throw new EpException(MessageConst.ERROR_100022, 
										            new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason.desc", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						                 }
					              }
				           }
			          } 
			
		          	if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
							
					        if(getManagedBean().getCircularVer().getCtCircularContracts().size()==0) {
						            throw new EpException(MessageConst.ERROR_100023, 
								        new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.list", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					        }
					
					       if(getManagedBean().getApproverModel().getTarget().size()==0) {
						         throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.			
					        }
			      }
			
			      if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				         String remark = getManagedBean().getRemarkList().get(0).getRemark();
	 			         if((remark == null || remark.length() == 0)) {
	 				            throw new EpException(MessageConst.ERROR_100022, 
							       new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	 			          }
			       }
			
			      if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				        AwardSupplierReqDTO awardSupplierReqDTO = new AwardSupplierReqDTO();
				        ContractReqDTO contractReqDTO = new ContractReqDTO();
				        for(CtCircularContract circularContrart : getManagedBean().getCircularVer().getCtCircularContracts()) {				
					           awardSupplierReqDTO.setQtId(circularContrart.getCtContract().getQuotationTender().getQtId());
					           AwardSupplierResDTO awardSupplierResDTO = qtAwardSO.retrieveLOILOAListBySupplier(awardSupplierReqDTO);
					
					           for(ScLoa scLoa : awardSupplierResDTO.getLoaResultBean().getResultList()) {
						           contractReqDTO.setLoaId(scLoa.getLoaId());
						           CtContract ctContract = contractSO.retrieveContractByLoaId(contractReqDTO).getContract();
						           if(ctContract==null || ctContract.getLatestContractVer()==null) { 
							            throw new EpException(MessageConst.ERROR_750005, new String[]{circularContrart.getCtContract().getContractName()});
						           }
					         }
				      }
		   	     }
			
			    if (getManagedBean().getCircularVer().getCtContactPersons() != null && getManagedBean().getCircularVer().getCtContactPersons().size() >= 1) {
				       for(CtContactPerson contrartPerson : getManagedBean().getCircularVer().getCtContactPersons()) {	
					       if(contrartPerson.getName().isEmpty() ||contrartPerson.getAddress().isEmpty() 
							    || contrartPerson.getEmail().isEmpty() || contrartPerson.getPhoneNo().isEmpty()) {
						        throw new EpException(MessageConst.ERROR_100022, new String[]{"Contact Person Details"});
					      }
				     }
			    }
			
			  /* if((getManagedBean().isUpdateAction() || (getManagedBean().getCircular() != null && getManagedBean().getCircular().getLatestCircularVer() != null)) && 
					  outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getAttachmentList().isEmpty()){
					  throw new EpException(MessageConst.ERROR_750018, new String[]{"Central circular update"});
			     }*/
		  }
		 finally {
			 qtAwardSO = null;
			 contractSO = null;
		  }
	}

	 /**
	 * This method used to upload circular letter into database
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
		RequestContext.getCurrentInstance().execute("uploadCircularDlg.hide()");
	}
	
	/**
	 * This method used to download circular letter once approver login
	 */
	public void downloadFile(int type) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		String filePath = null;
		if(type==1) {
			fileName = getManagedBean().getCircularVer().getCircularFileName();
			filePath = DocumentRepositoryUtils.getDBFilePath(getManagedBean().getCircularVer().getCircularFilePath());
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
	
	public void gotoCommonPage(Long contractVerId) {
		
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setCircularVerId(contractVerId);
			getManagedBean().setSelectedTabIndex(0);
			setContractCircularVar(reqDTO);					
	}

	/**
	 * This  method used to retrieve list of active approver start
	 * Setting selected and non selected approver value 
	 * Retrieve approver for central circular
     */
	private List<PmUser> getAvailableApproverList() {
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
			reqDTO.setRoleCode(RoleCode.BPK_CQ_APPROVER.value());

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
		 * This  method used to set drop down status 
		 *  value for delete of central circular details
	     */
	public SelectItem[] getStateDropdownValues() {
		SelectItem[] items = null;
		try {
			IStateSO stateSO = (IStateSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStateSO.class);
			StateReqDTO reqDTO = new StateReqDTO();
			reqDTO.setCountryId(1l);
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

	public void selectCcrContractEvent() {
			this.getManagedBean().getSelectedCircularContract();
	}

	/**
	 * The method to initialize remark
	 * */
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getCircularVer().getContractRemarks());
	}
	
	/**
	 * The method to initialize remark
	 * */
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
	 * The method for date conversion into string
	 * */
	public String convertTime(Date time){ 
        String str ="";
    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
        if(time == null)
        	return str;
        return format.format(time); 
    }

	/**
	 * The method to retrieve list of item
	 * return list object itemlist 
	 * */
	public List<CtItemPrice> getItemPrice() {
	 	 if(this.getManagedBean().getZonePriceMap().size() > 0 && this.getManagedBean().getSelectedZoneItems() != null) {
	 		       return  getManagedBean().getZonePriceMap().get(this.getManagedBean().getSelectedZoneItems());
		    }else if(this.getManagedBean().getSelectedCircularItem() != null && this.getManagedBean().getSelectedCircularItem().getCtContractItem() != null
		    		&& this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices() != null){
		    	return this.getManagedBean().getSelectedCircularItem().getCtContractItem().getCtItemPrices();
	    	}
		return new ArrayList<CtItemPrice>();
	}
	
	/**
	 * The method to retrieve list of  circular item 
	 * */
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
	
	/**
	 * The method to retrieve list of active circular item 
	 * */
	public List<CtCircularItem> getActiveCircularItem() {
		log.debug("CentralContractBackingBean.getActiveCircularItem()");
		List<CtCircularItem> activeItemList = new ArrayList<CtCircularItem>();
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		
		if(getManagedBean().getSelectedCircularContract() != null && getManagedBean().getSelectedCircularContract().getCtCircularItems() != null) {
		for(CtCircularItem circularItem : getManagedBean().getSelectedCircularContract().getCtCircularItems()) {
			for(CtContractItem contractItem : getManagedBean().getSelectedCircularContract().getCtContract().getLatestContractVer().getCtContractItems()){
				if (circularItem.getCtContractItem().getScRequestItem().getRequestNoteItemId().equals(contractItem.getScRequestItem().getRequestNoteItemId())) {
					if(contractItem.getRecordStatusFlg() == 1 || (contractItem.getRecordStatusFlg() == 0 && (currentDate.before(contractItem.getEffDate()) || currentDate.after(contractItem.getExpDate())))) {
						activeItemList.add(circularItem);						
					}
					break;
				}
			}
		  }
		}
		return activeItemList;
	}
	
	/**
	 * The method used to set the zone for item and supplier
	 * */
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
	
	/**
	 * The method used to reset the list of zone
	 * */
	public void setPriceListener(){
		this.getManagedBean().setChkItemPrice(true);
		retriveItemZonesList();
	}
	
	/**
	 * The method used to retrieve list of zone
	 * */
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
	
	/**
	 * The method used to construct the zone into tree map
	 * */
   public void retriveItemZonesList() {
		
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;
		if(getManagedBean().getSelectedZoneItems() != null) {
			   zoneNode = new DefaultTreeNode("zone", new CentralItemZones(getManagedBean().getSelectedZoneItems().getZoneName()), rootLocationTree);
			   TreeNode stateNode = null;
			   List<PmState> state = new ArrayList<PmState>();
			   if(getManagedBean().getSelectedZoneItems().getCtContractZoneDtls() != null 
					   && getManagedBean().getSelectedZoneItems().getCtContractZoneDtls().size() > 0) {
				       for(CtContractZoneDtl zoneDtl : getManagedBean().getSelectedZoneItems().getCtContractZoneDtls()) {
						       if(zoneDtl.getState() != null){
							           if(!state.contains(zoneDtl.getState())){
								              stateNode = new DefaultTreeNode("state",new CentralItemZones(zoneDtl.getState()) , zoneNode);
							           }
							         state.add(zoneDtl.getState());
						        } 
				         if(zoneDtl.getDistrict() != null){
					            new DefaultTreeNode("district", new CentralItemZones(zoneDtl.getDistrict()), stateNode);
				          }
				    }
			    }
			}
		 getManagedBean().setCentralZoneTrees(rootLocationTree);
	  }
   
   /**
	 * The method used to navigate back maintenance page
	 * */
   public String backAction() {
	   if(getManagedBean().isChkCancelPage()) {
		   getManagedBean().setChkCancelPage(false);
		   return "/xhtml/ct/circular/panel/cancelCircularBase.xhtml?faces-redirect=true";
	   } else if(getManagedBean().isFromCircularNotice()) {
		   return "../portal/circularListing.xhtml?faces-redirect=true";
	   } else if(getManagedBean().isChkCentralCircular()) {
		   return getManagedBean().getGotoPreviewPage();
	   } else if(getManagedBean().getGotoPreviewPage()!=null) {
		   return getManagedBean().getGotoPreviewPage();
	   } else {
		   return "search.xhtml?faces-redirect=true";
	   }
	}
	
   /**
	 * Method used to add contract person into contract person list
   */
	public void addContactPerson(){
		try {
			CtContactPerson contactPerson = new CtContactPerson();
	        getManagedBean().getCircularVer().getCtContactPersons().add(contactPerson);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	
	/**
	 * Method used to add exempted 
    */
	public void addExemptedOrg(List<PmOrgProfile> orgProfiles){
		
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
	
	/**
	 * Method used to delete exempted 
    */
	public void deleteExemptedOrg(){
		try {
			getManagedBean().getCircularVer().getCtExemptedOrgs().removeAll(getManagedBean().getSelectedExemptedOrgs());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}	
	
	/**
	 * Method used to add circular contract into contract list
    */
	public void addCircularContractListener() {
		try {
			getManagedBean().setAutoUpdate(false);
			getManagedBean().setSearchContractNo(null);
			getManagedBean().setSearchContractName(null);
			getManagedBean().setContractList(new ArrayList<CtContract>());
			//getManagedBean().setSelectedContractList(new ArrayList<CtContract>());
			getManagedBean().setContractList(new ArrayList<CtContract>());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * Method used to delete circular contract from contract list
    */
	public void deleteCircularContract(){
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
	
	/**
	 * Method used to set extension start date 
	 * and extension end date for central circular
    */
	public void extendStartEndDate(){
		/*if(this.getManagedBean().getCircularVer().getExtPeriod() > 0){
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
		}*/
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
	 * Method used to set value for security check
    */
	public String preSubmitCircular(PmDigiSign pmDigiSign) {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveCircular(getManagedBean().getAction());
	}
	
	 /**
		 * Method to popup window to upload circular letter
	    */
	public void setCentralDetailsValue() {
		if(getManagedBean().getCircularVer().getCircularNo() == null || getManagedBean().getCircularVer().getName() == null
		 ||getManagedBean().getCircularVer().getStartDate() == null || getManagedBean().getCircularVer().getEndDate() == null) {
		         RequestContext.getCurrentInstance().update(":form:centralCircularId");
		} else {
	    	RequestContext.getCurrentInstance().execute("uploadCircularDlg.show()");
	   }
	}
	
	 /**
		 * Method to retrieve portal name from property file
	    */
	public String getPortletTitle() {
		return ResourceBundleUtils.getMessage("lbl.ct.cc.circular.creation", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	/**
	 * Method to authenticate user during submit of central circular
	 * Navigate confirmation dialog for authenticate of login user
	 * Call save central circular method once authentication done
    */
	public String gpkiSignAction(String outcome) throws Exception{
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
			validateCircular(outcome);
			SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
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
		     } else {
			      sb.append(df.format(getManagedBean().getCircularVer().getStartDate()));
			      sb.append(ContractConst.SEPRATE_LINE);
			      sb.append(df.format(getManagedBean().getCircularVer().getEndDate()));
			      sb.append(ContractConst.SEPRATE_LINE);
		     }
			sb.append(outcome);
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(getFacesScopes().getUserSessionBean().getIdentificationNo());
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
			return saveCircular(outcome);
		}
	}
	
	/**
	 * The method used for generating a list of available ministry
	 * for searching exempted as selection.
	 * 
	 * @return the selection list of ministry
	 */
	public SelectItem[] getMinistryDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		     IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
		
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    reqDTO.setOrgType(OrganizationType.MINISTRY.value());	
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		   
		    items = new SelectItem[resDTO.getOrgProfileList().size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		    
		  	for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
			return items;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return null;
	}
	
	/**
	 * The method used for generating a list of 
	 * available jabatan for searching exempted organization 
	 * 
	 * @return the selection list of jabatan
	 */
	public SelectItem[] getJabatanDropdown() {		
		SelectItem[] items = null;
		
		try {
		    int i = 1;
		    if(getManagedBean().getMinistryProfileId() != null) {
		         IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
				reqDTO.setOrgCode(OrganizationType.DEPARTMENT.value());
				OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
				
			    items = new SelectItem[resDTO.getOrgProfileDDList().size() + 1];
			    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
			    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				
			     for(Object[] objects : resDTO.getOrgProfileDDList()){
			         items[i++] = new SelectItem(Long.parseLong(objects[0].toString()), objects[1].toString());
				}
			  return items;
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return null;
	}
	
	/**
	 * Call by ajax listener to set value of
	 * @param officerProfileId  and jabatanProfileId
	 * to set value null
	 */
	public void settingMinistryListener(){
		 getManagedBean().setJabatanProfileId(null);
	}
	
	/**
	 * The method used to reset the search criteria for agency.
	 */
	public void resetMinistryDetails() {
		getManagedBean().setMinistryProfileId(null);
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		exemptedOrgDataModel.resetData();
	}
	
	public void flushExempMisntry(){
		getManagedBean().setAutoUpdate(false);
		getManagedBean().setMinistryProfileId(null);
		getManagedBean().setJabatanProfileId(null);
		getManagedBean().setOrgTypeCode(null);
		exemptedOrgDataModel.resetData();
		getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
		getManagedBean().setPtjCodeNameSearchValues(null);
	}
	
	/**
	 * This method used to retrieve 
	 * deduction type from database 
	 * @return menu item, List of deduction types
	 */
	public SelectItem[] retrieveParameterDescListByType() {
		
		    ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>(); ;
			List<String> parameterTypeList = new ArrayList<String> ();
			parameterTypeList.add(OrganizationType.MINISTRY.value());
			parameterTypeList.add(OrganizationType.DEPARTMENT.value());
			parameterTypeList.add(OrganizationType.DIVISION.value());
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							parameterTypeList));
	    return getDropdownCodeValuesBySearchBean(ParameterTypeConst.ORGANISATION_TYPES, searchBean);
	}
	
	/**
	 * The method used to set auto update true
	 * to show the message summary
	 * */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	public String gotoBackPage() {
		return getManagedBean().getGotoPreviewPage();
	}
	
	/**
	 * The method used for
	 * searching organization details
	 */
	public void searchPtjDetails() {
		try {
			exemptedOrgDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}
	public IPagingDataModel<PmOrgProfile> getExemptedOrgDataModel() {
		return exemptedOrgDataModel;
	}

	public void setExemptedOrgDataModel(
			IPagingDataModel<PmOrgProfile> exemptedOrgDataModel) {
		this.exemptedOrgDataModel = exemptedOrgDataModel;
	}

	/**
	 * Pagination for available 
	 * exempted ministry list
	 */
	private class ExemptedOrgDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public ExemptedOrgDataModel() {
			super("tablePtjCode", false);
		}
		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
	 * .biz.bean.search.ISearchBean)
	 */
	@Override
	public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {			
		
		    try {
		
			        OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			        IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			        // Filter By - parameterType
			       searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
					
			      // Filter Kumpulan Ptj By - Ministry Profile Id
			      if(getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.DEPARTMENT.value())   && getManagedBean().getMinistryProfileId() != null){
				         reqDTO.setParentOrgProfileId(getManagedBean().getMinistryProfileId());
				         reqDTO.setOrgCode(OrganizationType.PENGAWAI_PENGAWAL.value());
				        OrgProfileResDTO resDTO = orgProfileSO.retrieveChildDDListByParentOrgProfileId(reqDTO);
				        List<Object[]> orgName =  resDTO.getOrgProfileDDList();
				        List<Long> longValue = new ArrayList<Long>();
				        for(Object[] org : orgName){
					        BigDecimal decimal =(BigDecimal)org[0];
					        longValue.add(decimal.longValue());
				        }
				       searchBean.getDataFilterBeans().add(
							new SearchFilterBean(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(), OrganizationType.PENGAWAI_PENGAWAL.value()), SearchOperator.OPERATOR_IN.value(),longValue));
				  } 

                  if(getManagedBean().getOrgTypeCode().equalsIgnoreCase(OrganizationType.DIVISION.value())) {
							if (getManagedBean().getMinistryProfileId() != null) {
							          searchBean.getDataFilterBeans().add(
									  new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value()),
											SearchOperator.OPERATOR_EQUALS.value(),
											getManagedBean().getMinistryProfileId()));
							
							}  if (getManagedBean().getJabatanProfileId() != null) {
							     searchBean.getDataFilterBeans().add(
									new SearchFilterBean(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()),
											SearchOperator.OPERATOR_EQUALS.value(),
											getManagedBean().getJabatanProfileId()));
						     }
			        }
			
			       searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getOrgTypeCode()));
				   searchBean.getDataFilterBeans()
			          .add(new SearchFilterBean("recordStatus",SearchOperator.OPERATOR_LESS_EQUAL.value(), DbValueConst.RECORD_STATUS_ACTIVE));

		    	  Date currentDate = DateUtils.getCurrentDateWithoutTime();
			      // Filter By - Org effDate
			      searchBean.getDataFilterBeans()
					.add(new SearchFilterBean("pmOrgValidity.effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

			     // Filter By - Org expDate
			     searchBean.getDataFilterBeans().add(
					  new SearchMultiFilterBean(new SearchFilterBean("pmOrgValidity.expDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(),
							currentDate), new SearchFilterBean("pmOrgValidity.expDate",	SearchOperator.OPERATOR_IS_NULL.value(), null,
							SearchCondition.CONDITION_OR.value())));
			
			     if (getManagedBean().getPtjCodeName() != null
					   && (getManagedBean().getPtjCodeNameSearchValues() != null && !getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))) {
						 if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
								SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
								filterBean.setCaseInsensitive(true);
							    searchBean.getDataFilterBeans().add(filterBean);
						
							} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
								SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValues());
								filterBean.setCaseInsensitive(true);
							    searchBean.getDataFilterBeans().add(filterBean);
							}
			       }
			
					reqDTO.setOrgProfileSearchBean(searchBean);
					OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
					if (resDTO.isSuccessful()) {
						 return resDTO.getOrgProfileResultBean();
					}
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		    return null;
		}
  }
	
		/**
		 * The method used to add a new exempted organization
		 * This method will 
		 * generate a new record in central circular
		 */
		public void addExemtedOrgDetails() {
			
			   getManagedBean().setAutoUpdate(true);
			  List<CtExemptedOrg>  orgList = new ArrayList<CtExemptedOrg>();
			  Iterator<PmOrgProfile> itrPmOrgProfile = getExemptedOrgDataModel().getList().iterator();
			  while(itrPmOrgProfile.hasNext()){
					PmOrgProfile pmObj = itrPmOrgProfile.next();
					if(pmObj.isSelectedCheckBox()){
						 	//Remove if already exist, avoid duplicate
						  	for (CtExemptedOrg exmptOrg : this.getManagedBean().getCircularVer().getCtExemptedOrgs()) {
						  		if (exmptOrg.getOrgProfile().getOrgProfileId().equals(pmObj.getOrgProfileId())) {
						  			throw new EpException(MessageConst.INFO_758000, new String[]{exmptOrg.getOrgProfile().getOrgName()}); 
						  		}
						  	}
			  	
						  	CtExemptedOrg org = new CtExemptedOrg();
					    	org.setOrgProfile(pmObj);
							org.setOrgTypeCode(pmObj.getOrgTypeCode().getParameterCode());
							getManagedBean().setExmptedMinistryDD(false);
							orgList.add(org);
		            }
			  }
		      if (orgList.size()>0) {
				this.getManagedBean().getCircularVer().getCtExemptedOrgs().addAll(orgList);
			 }
		
			  getManagedBean().setPtjCodeName(ContractConst.PTJ_CODE);		
			  getManagedBean().setPtjCodeNameSearchValues(null);
			  RequestContext.getCurrentInstance().execute("dlgExemptedMinistry.hide()");
		}
	
		public void clickExemptedOrgBtn() {
			  getExemptedOrgDataModel().resetData();
		}

		public CtExemptedOrg[] getSelectedExemptedOrgs() {
			return selectedExemptedOrgs;
		}

		public void setSelectedExemptedOrgs(CtExemptedOrg[] selectedExemptedOrgs) {
			this.selectedExemptedOrgs = selectedExemptedOrgs;
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
			    	   
			            reqDTO = new ReasonReqDTO();
				        reqDTO.setLanguage(getFacesScopes().getUserLocale().getLanguage());
				        reqDTO.setSortBy("pmReason.reasonId");
				        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.reasonCategoryCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(), ReasonCategory.PID_JUSTIFICATION.value()));
				        searchBean.getDataFilterBeans().add(new SearchFilterBean("pmReason.pmPIDReasonList.docType", SearchOperator.OPERATOR_EQUALS.value(), ParameterConst.DOCTYPE_CENTRAL_CONTRACT_CIRCULAR));
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
		 * This method used to
		 *  add new record in datatable
		 */
		public void addPurchaseInDefault(){
				CtCircularPidReason ctCircularPidReasons = new CtCircularPidReason();
				ctCircularPidReasons.setPmReason(new PmReason());
				getManagedBean().getCircularVer().getCtCircularPidReason().add(ctCircularPidReasons);
			}
		
		public void selectAllPidReason(AjaxBehaviorEvent e) {
			
			for (CtCircularPidReason ctCircularPidReasons : getManagedBean().getCircularVer().getCtCircularPidReason()) {
				if (getManagedBean().isSelectAll()) {
					ctCircularPidReasons.setSelectedCheckBox(true);
			       }else {
			    	   ctCircularPidReasons.setSelectedCheckBox(false);
			       }
		}
		
		 if((boolean) e.getComponent().getAttributes().get("value")){
			 getManagedBean().setButtonHideActiveDel(false);
		 }else{
			 getManagedBean().setButtonHideActiveDel(true);
		 }	
	   }
		
		/**
	     * Method to set boolean value for checkbox 
	     * @param pass ajax event type
	     * Set boolean value for user action 
	     * */
	    public void selectOnePidCheckboxSelect(AjaxBehaviorEvent e){
			
	    	getManagedBean().setSelectAll(false);
	    	for (CtCircularPidReason ctCircularPidReasons : getManagedBean().getCircularVer().getCtCircularPidReason()){
	    		if(ctCircularPidReasons.isSelectedCheckBox()){
	    			getManagedBean().setButtonHideActiveDel(false);
	    			return;
	    		}
	    	}
	    	
			if((boolean) e.getComponent().getAttributes().get("value")){
				getManagedBean().setButtonHideActiveDel(false);
			}else{
				getManagedBean().setButtonHideActiveDel(true);
			}	
		}
		
		/**
    	 * The method used to delete the selected deduction clause.
    	 */
    	public void deleteTableRow(String tableType) {
    		try {
    			 if(tableType.equalsIgnoreCase("PID")){	
    			      Iterator<CtCircularPidReason> pidReasonIterator = getManagedBean().getCircularVer().getCtCircularPidReason().iterator();
    				   while(pidReasonIterator.hasNext()){
    					 CtCircularPidReason  pidReason = pidReasonIterator.next();
    					 if(pidReason.isSelectedCheckBox()){
    						pidReasonIterator.remove();
    					 }
    				 }
    				 getManagedBean().setCtCircularPidReasons(null);
    				 getManagedBean().setSelectAll(false);
    			
    			 } else if(tableType.equalsIgnoreCase("PERSON")){
    				 Iterator<CtContactPerson> contactPersonIterator = getManagedBean().getCircularVer().getCtContactPersons().iterator();
  				     while(contactPersonIterator.hasNext()){
  				    	CtContactPerson  ctContactPersons = contactPersonIterator.next();
  					    if(ctContactPersons.isSelectedCheckBox()){
  					    	contactPersonIterator.remove();
  					    }
  				    } 
  				     getManagedBean().setSelectedContactPersons(null);
  				   getManagedBean().setSelectAll(false);
    			
    			 }else if(tableType.equalsIgnoreCase("ORG")){
    				 Iterator<CtExemptedOrg> ctExemptedOrgs = getManagedBean().getCircularVer().getCtExemptedOrgs().iterator();
  				     while(ctExemptedOrgs.hasNext()){
  				    	CtExemptedOrg  ctExemptedOrg = ctExemptedOrgs.next();
  					    if(ctExemptedOrg.isSelectedCheckBox()){
  					    	ctExemptedOrgs.remove();
  					    }
  				    } 
    		      getManagedBean().setSelectedExemptedOrgs(null);
    		      getManagedBean().setSelectAll(false);
    			 } else if(tableType.equalsIgnoreCase("CANCEL")){
    				 Iterator<CtCancelCircular> ctCancelCirculars = getManagedBean().getCircularVer().getCtCancelCircularList().iterator();
  				     while(ctCancelCirculars.hasNext()){
  				    	CtCancelCircular  ctCancelCircular = ctCancelCirculars.next();
  					    if(ctCancelCircular.isSelectedCheckBox()){
  					    	ctCancelCirculars.remove();
  					    }
  				    } 
  				     getManagedBean().setSelectedCancelledCircularList(null);
  				   getManagedBean().setSelectAll(false);
  				  if(getManagedBean().getCancelledCircularList() != null && getManagedBean().getCancelledCircularList().size() == 0){
  					    getManagedBean().setCancelledCircularList(null);
  				    }
    			 } 
    			 getManagedBean().setButtonHideActiveDel(true);
    		} catch (Exception e) {
    			e.printStackTrace();
    			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
    		}
        }
    	
    	public void selectAllPerson(AjaxBehaviorEvent e) {
    		
    		for (CtContactPerson ctContactPersons : getManagedBean().getCircularVer().getCtContactPersons()) {
    			if (getManagedBean().isSelectAll()) {
    				ctContactPersons.setSelectedCheckBox(true);
    		       }else {
    		    	   ctContactPersons.setSelectedCheckBox(false);
    		       }
    	}
    	
    	 if((boolean) e.getComponent().getAttributes().get("value")){
    		 getManagedBean().setButtonHideActiveDel(false);
    	 }else{
    		 getManagedBean().setButtonHideActiveDel(true);
    	 }		
        }
      
        public void selectOnePersonCheckboxSelect(AjaxBehaviorEvent e){
    		
        	getManagedBean().setSelectAll(false);
        	  for (CtContactPerson ctContactPersons : getManagedBean().getCircularVer().getCtContactPersons()){
        		if(ctContactPersons.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
    	}
		
        public void selectAllCancelCircular(AjaxBehaviorEvent e) {
    		
    		for (CtCancelCircular ctCancelCirculars : getManagedBean().getCircularVer().getCtCancelCircularList()) {
    			if (getManagedBean().isSelectAll()) {
    				ctCancelCirculars.setSelectedCheckBox(true);
    		       }else {
    		    	   ctCancelCirculars.setSelectedCheckBox(false);
    		       }
    	}
    	
    	 if((boolean) e.getComponent().getAttributes().get("value")){
    		 getManagedBean().setButtonHideActiveDel(false);
    	 }else{
    		 getManagedBean().setButtonHideActiveDel(true);
    	 }		
        }

        public void selectCancelCheckboxSelect(AjaxBehaviorEvent e){
        	getManagedBean().setSelectAll(false);
        	for (CtCancelCircular ctCancelCirculars : getManagedBean().getCircularVer().getCtCancelCircularList()){
        		if(ctCancelCirculars.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
        }
        
 public void selectAllCancelCircularList(AjaxBehaviorEvent e) {
    		
    		for (CtCancelCircular ctCancelCirculars : getManagedBean().getCancelledCircularList()) {
    			if (getManagedBean().isSelectAll()) {
    				ctCancelCirculars.setSelectedCheckBox(true);
    		       }else {
    		    	   ctCancelCirculars.setSelectedCheckBox(false);
    		       }
    	}
    	
    	 if((boolean) e.getComponent().getAttributes().get("value")){
    		 getManagedBean().setButtonHideActiveDel(false);
    	 }else{
    		 getManagedBean().setButtonHideActiveDel(true);
    	 }		
        }

        public void selectCancelCheckboxSelectList(AjaxBehaviorEvent e){
        	getManagedBean().setSelectAll(false);
        	for (CtCancelCircular ctCancelCirculars : getManagedBean().getCancelledCircularList()){
        		if(ctCancelCirculars.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
        }
        
 public void selectAllContractList(AjaxBehaviorEvent e) {
    		
    		for (CtContract ctContracts : getManagedBean().getContractList()) {
    			if (getManagedBean().isSelectAll()) {
    				ctContracts.setSelectedCheckBox(true);
    		       }else {
    		    	   ctContracts.setSelectedCheckBox(false);
    		       }
    	}
    	
    	 if((boolean) e.getComponent().getAttributes().get("value")){
    		 getManagedBean().setButtonHideActiveDel(false);
    	 }else{
    		 getManagedBean().setButtonHideActiveDel(true);
    	 }		
        }

        public void selectContractCheckboxSelectList(AjaxBehaviorEvent e){
        	getManagedBean().setSelectAll(false);
        	for (CtContract ctContracts : getManagedBean().getContractList()){
        		if(ctContracts.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
        }
        
        public void selectAllexemptedOrg(AjaxBehaviorEvent e){
        
        	for (CtExemptedOrg ctExemptedOrgs : getManagedBean().getCircularVer().getCtExemptedOrgs()){
        		if (getManagedBean().isSelectAll()) {
        			ctExemptedOrgs.setSelectedCheckBox(true);
    		       }else {
    		    	   ctExemptedOrgs.setSelectedCheckBox(false);
    		       }
    	      }
        	
        	 if((boolean) e.getComponent().getAttributes().get("value")){
        		 getManagedBean().setButtonHideActiveDel(false);
        	 }else{
        		 getManagedBean().setButtonHideActiveDel(true);
        	 }		
            }
        
        
        public void selectExemptedOrgCheckboxSelect(AjaxBehaviorEvent e){
        	getManagedBean().setSelectAll(false);
        	for (CtExemptedOrg ctExemptedOrgs : getManagedBean().getCircularVer().getCtExemptedOrgs()){
        		if(ctExemptedOrgs.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
        }
        
        public void selectExmptedSearchOrg(AjaxBehaviorEvent e){
            
        	for (PmOrgProfile pmOrgProfiles : getExemptedOrgDataModel().getList()){
        		if (getManagedBean().isSelectAll()) {
        			pmOrgProfiles.setSelectedCheckBox(true);
    		       }else {
    		    	   pmOrgProfiles.setSelectedCheckBox(false);
    		       }
    	      }
        	
        	 if((boolean) e.getComponent().getAttributes().get("value")){
        		 getManagedBean().setButtonHideActiveDel(false);
        	 }else{
        		 getManagedBean().setButtonHideActiveDel(true);
        	 }		
            }
        
        
        public void selectSearchOrgCheckboxSelect(AjaxBehaviorEvent e){
        	getManagedBean().setSelectAll(false);
        	for (PmOrgProfile pmOrgProfiles : getExemptedOrgDataModel().getList()){
        		if(pmOrgProfiles.isSelectedCheckBox()){
        			getManagedBean().setButtonHideActiveDel(false);
        			return;
        		}
        	}
    		if((boolean) e.getComponent().getAttributes().get("value")){
    			getManagedBean().setButtonHideActiveDel(false);
    		}else{
    			getManagedBean().setButtonHideActiveDel(true);
    		}	
        }
        
		/**
	     * Method to set boolean value for checkbox 
	     * @param pass ajax event type
	     * Set boolean value for user action 
	     * */
        public void delCircularVerFile(){
        	getManagedBean().getCircularVer().setCircularFilePath(null);
        	getManagedBean().getCircularVer().setCircularFileName(null);
        }
        
		public void onToggleSelect(ToggleSelectEvent event) {
			getManagedBean().setButtonHideActive(false);
		}
		
		public void onRowSelect(SelectEvent event) {
			getManagedBean().setButtonHideActive(false);
		}
	 
	    public void onRowUnselect(UnselectEvent event) {
	    	getManagedBean().setButtonHideActive(true);
	    }
}