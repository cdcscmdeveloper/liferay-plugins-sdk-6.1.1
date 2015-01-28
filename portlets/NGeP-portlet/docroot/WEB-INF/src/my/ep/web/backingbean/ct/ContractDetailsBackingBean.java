/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
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
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtAgencyAddress;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmFinancialAcc;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.log4j.Logger;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.selectbooleancheckbox.SelectBooleanCheckbox;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;
/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean (name = "contractDetailsBackingBean")
@ViewScoped
public class ContractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml?faces-redirect=true";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml?faces-redirect=true";
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	
	private String editorValue;
	private BigDecimal paidAdvancePercentage = new BigDecimal(0);
	private BigDecimal balanceToPaid;
	private BigDecimal paidPercentage;
	private boolean showZoneDetails;
	private boolean validateScheduleDate;
	
	private ContractBackingBean ctBB = null;
	private List<CtContractItem> ContractItemServiceList;
	private CtSchedule[] selectedSchedule;
	private CtDeductionClause[] chkDeductionClause;
	private CtProgressivePayment[] selectedProgressive;
	private PmOrgProfile[] selectedPmOrgProfile;
	private CtPerformanceBond[] selectedBondList;
	private CtDeductionClause[] selectedDeductionClauses;
	private IPagingDataModel<PmOrgProfile> agencyDataModel = new AgencyPagingDataModel();	
	
	public ContractDetailsBackingBean() {
	   
		 if (this.getManagedBean().getAction() == null) {
		       this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
		       this.getManagedBean().setContractVer(new CtContractVer());
	     } if(getManagedBean().getUserMap().size()==0) {
			  initializeApproverList();
	     }

	 }
	
	private FacesScopes getFacesScopes(){
	   FacesScopes facesScopes = new FacesScopes();
	   return facesScopes;
	}
	
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
		  	 List<String> source = new ArrayList<String>();
			 List<String> target = new ArrayList<String>();
			 getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	public void setSelectedTabValue(int tabValue) {
		 this.getManagedBean().setSelectedTabIndex(tabValue);
	}
	
	 private List<String> getAvailableApproverList() {
		 IUserSO userSO = null;
		 List<String> sourceList = new ArrayList<String>();
	
		 try {
			    userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			    UserReqDTO reqDTO = new UserReqDTO();
			    reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			
			    UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			    if (resDTO.getUserOrgList().size() > 0)
			    	reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			
			    if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) 
				     reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			    else // Central Contract	
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
	
	 public void preRenderSearch() {
	    	renewSesionObject();
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_MAINT_PAGE);
	  }
	
	  public void preRenderContract() {
		    renewSesionObject();
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_PAGE);
	   }
	
	   public void preRenderView() {
		
		     try {
			          
		    	    HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		         	HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		        	String taskId = originalServletRequest.getParameter("taskId");
		 	        if(taskId == null) {
				         ConstructSideTabMenu();
				 	    if(getManagedBean().getWarningPercentage()!=null) {
					    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, null, 
				    			MessageUtils.getMessage(MessageConst.INFO_745008, new String[]{getManagedBean().getCtContract().getContractNo(), getManagedBean().getWarningPercentage()}, 
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					    	getManagedBean().setWarningPercentage(null);
					    }
				      return;
			       }

		    	  FacesScopes face = new FacesScopes();
			      String token = face.getBPMAuthenticationCtxToken();
			      IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			      BpmRequestDTO reqDTO = new BpmRequestDTO();
			      reqDTO.setCtxToken(token);
			      reqDTO.setTaskId(taskId);

			      BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO); 
			      Object obj = res.getPojo();
		      
			      String[] outcome = new String[res.getAction().length + 1];
			      outcome[0] = BpmOutcomeConst.BPM_SAVE;
			      int i = 1;
			      for (String action : res.getAction()) {
				     outcome[i++] = action;
			      }
				
			     this.getManagedBean().setTaskName(res.getTaskName());
			     this.getManagedBean().setAction(outcome);
			     this.getManagedBean().setTaskId(taskId);
			     this.getManagedBean().setSelectedTabIndex(0);

			     ContractReqDTO ctReqDTO = new ContractReqDTO();
			     if((res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME) || res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME)) && res.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)) {
				        CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
				        ctReqDTO.setContractId(new Long(dataType.getContractId()));
				        ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				        ctReqDTO.setDocType(dataType.getDocType());
			
			     } else { 
				
			    	   CTContractDataType dataType = (CTContractDataType) obj;
				       ctReqDTO.setContractId(new Long(dataType.getContractId()));
				       ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
				       ctReqDTO.setDocType(dataType.getDocType());
			     }
		    
			     IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		         ContractResDTO resDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
		         setValidateContractField(resDTO);
		         ConstructSideTabMenu();		         
		    
		         FacesContext fc = FacesContext.getCurrentInstance();
			     fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		
           } catch(Exception e){
			     e.printStackTrace();
	 	 
           }finally{}
	}
	
	public void setValidateContractField(ContractResDTO resDTO) {
		
		     getManagedBean().setContractVer(resDTO.getCtContractVer());
	         getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	    
	         setBondRecordStatus(this.getManagedBean().getContractVer());
	         if(getManagedBean().getContractVer().getCtAgencies() != null && this.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
	        	  	getManagedBean().setSelectedAgency(null);
		           Collections.sort(this.getManagedBean().getContractVer().getCtAgencies(), new CtAgency());		           
		           for(CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
		        	  checkAgencyStatus(ctAgency);		        	   
		           }
	         }
	  
	          if(getManagedBean().getContractVer().getCtContractItems().size() > 0) {	        	   
	        	    getManagedBean().setCtItemList(new ArrayList<CtContractItem>());
 		            getManagedBean().setCtItemServiceList(new ArrayList<CtContractItem>());
 		            getManagedBean().setSelectedItem(null);
 		            getManagedBean().setSelectedServiceItem(null);	    	  
 		            
 		            for(CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
 		            		// If effective date is after current date, status is still display old status
			        	   if (item.getRecordStatusFlg() == 0) {				       		
					       		if(item.getEffDate().after(getCurrentDate())) 
					       			item.setChkStatus(true);
					       		else 
					       			item.setChkStatus(false);
			        	   }
			        	   else {
					       		if(item.getEffDate().after(getCurrentDate())) 
					       			item.setChkStatus(false);
					       		else 
					       			item.setChkStatus(true);		        		   
			        	   }
 		            	   if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
			        	          Collections.sort(item.getCtItemPrices(), new CtItemPrice());
				                  getManagedBean().getCtItemServiceList().add(item);			           
 		            	   } else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
			        	             Collections.sort(item.getCtItemPrices(), new CtItemPrice());
			        	             if(item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0) {
			        	        	       Collections.sort(item.getCtItemCeilingQties(), new CtItemCeilingQty());
			        	        	       for(CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {			        	        	    	   
			        	 		        	  checkAgencyStatus(ceilingQty.getCtAgency());		        	   
			        	 		           }
			        	             }
	        	                     this.getManagedBean().getCtItemList().add(item);
			               }		             
 		            } if(this.getManagedBean().getCtItemList() != null && this.getManagedBean().getCtItemList().size() > 0) 
		                    Collections.sort(this.getManagedBean().getCtItemList(), new CtContractItem());
	    	 
	    	        if(this.getManagedBean().getCtItemServiceList() != null && this.getManagedBean().getCtItemServiceList().size() > 0) 
			              Collections.sort(this.getManagedBean().getCtItemServiceList(), new CtContractItem());
		     }
		 
		    if(this.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
		         for(CtDeduction deduct : this.getManagedBean().getContractVer().getCtDeductions()){
		             if(deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)){
		    	          getManagedBean().setFlgPaymentTracking(true);
		    	          getManagedBean().setAdvancePaymentAmt(deduct.getAmount());
		    	          calAdvancePaymentTracking(this.getManagedBean().getCtContract().getContractId());
		           }
		     }
		
		 } 
		   
		    if (getManagedBean().getContractVer().getCtSchedules() != null && !getManagedBean().getContractVer().getCtSchedules().isEmpty() ) {
		        getManagedBean().setChkSchedule(true);
		        Collections.sort(getManagedBean().getContractVer().getCtSchedules(), new CtSchedule());
		        for(CtSchedule ctSchedule : getManagedBean().getContractVer().getCtSchedules()) {
		        	if(ctSchedule.isUseInFulfilment()) {
		        		getManagedBean().setDisableScheduleRadio(true);
		        		break;
		        	}
		        }
		        ScheduleDate();
		 } if (getManagedBean().getContractVer().getCtProgressivePayments() != null && !getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
			     Collections.sort(getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment());
			     for(CtProgressivePayment ctProgressivePayment : getManagedBean().getContractVer().getCtProgressivePayments()) {
			    	 if(ctProgressivePayment.isUseInFulfilment()) {
			    		 getManagedBean().setDisableScheduleRadio(true);
			    		 break;
			    	 }
			     }ProgressiveDate();
		   }
		    
		    if(getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null){
		    	getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
		    	getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(getParameterIdByTypeAndCode(ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS));
		    }
		  
		    if(getManagedBean().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
		    	getManagedBean().setDirectPurchase(true);
		    	getManagedBean().setFlgPaymentTracking(false);
		    }
		    else {
		    	this.getManagedBean().setDirectPurchase(false);
		    }
		    
		    if(getManagedBean().getContractVer().getCtContractZones() != null && this.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
		    	     Collections.sort(getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
		    		 retriveZonesByContractVer();
		    }
		  
	    	getManagedBean().setSmAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
	    	getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
		    renderUpdateCheckbox(getManagedBean().getContractVer());
		    getManagedBean().setSupplierCompanyName(this.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		    
	        getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			     getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			     getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			} 
	          getManagedBean().setRemarkList(resDTO.getCtRemarks());
			  getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
			if(getManagedBean().getCtContract().isFrequencySchedule()){
			    getManagedBean().setFrequencySchedule("S");
			}else {
				getManagedBean().setFrequencySchedule("P");
			}
			
			 if(getManagedBean().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)){
			    	getManagedBean().setDisableScheduleRadio(true);
			    	getManagedBean().setFrequencySchedule("S");
	  	  
			 } else if( getManagedBean().getCtContract().getLoa().getScQtSchedules()!= null && !getManagedBean().getCtContract().getLoa().getScQtSchedules().isEmpty()){
		    	getManagedBean().setDisableScheduleRadio(true);
		    	getManagedBean().setFrequencySchedule("S");
  	     
			 }  else if( getManagedBean().getCtContract().getLoa().getScQtProgPayments() != null && !getManagedBean().getCtContract().getLoa().getScQtProgPayments().isEmpty()){
 		    	getManagedBean().setDisableScheduleRadio(true);
 		    	getManagedBean().setFrequencySchedule("P");
   	         }
			
			if(getManagedBean().getContractVer().getCtPerformanceBonds() != null && !getManagedBean().getContractVer().getCtPerformanceBonds().isEmpty()){
				   for(CtPerformanceBond bond : getManagedBean().getContractVer().getCtPerformanceBonds()){
					   bond.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
					   bond.setExpDate(getExactDate());
				   }
			}
			
			if(getManagedBean().getCtContract().getLatestContractVer() != null && getManagedBean().getCtContract().getLatestContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)) {
				    IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
					ContractAmendmentResDTO resAmendDto = amendSO.getAmendmentByLastestContractVer(getManagedBean().getCtContract().getLatestContractVer().getContractVerId());
				    getManagedBean().setCtAmendment(resAmendDto.getAmendment());
			}
			
			// Get performance bond rule amount
			IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(RuleTypeConst.BOND);
			RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
			if(roleResDTO.isSuccessful() && roleResDTO.getRuleValueList()!=null && roleResDTO.getRuleValueList().size()>0) {
				getManagedBean().setPerformanceBondRuleAmt(Double.parseDouble(roleResDTO.getRuleValueList().get(0).getRuleValue()));
			}
			initializeRemarks();
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
			ctBB.getManagedBean().setViewContract(getManagedBean().isViewContract());
		    ctBB.initializeRemarkAndAttachement(resDTO.getCtContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	}
	
	
    public void ConstructSideTabMenu(){
		
		   MenuModel fulfilmentMenuModel = new DefaultMenuModel();
		   CtTabSideMenus menu = new CtTabSideMenus();
		
		   int zoneTabIndex = 0;
		   FacesContext facesContext = FacesContext.getCurrentInstance();
		   getManagedBean().setFinalListOfSideMenu( new ArrayList<String>());
		
		   if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
		    	  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFulfilement);
			      getManagedBean().getFinalListOfSideMenu().addAll(menu.menuAgency);
			      if(getManagedBean().getCtContract().isZonalPricing()) {
		 		          getManagedBean().getFinalListOfSideMenu().addAll(menu.menuZone);
			      }
		 		  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuItem);
		          if (!getManagedBean().isDirectPurchase() && getManagedBean().getContractVer().getCtDeductions() != null
		        		  && !getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
		    	  		  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuDeduction); 
			      } 
		         
		          if (getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
		            	        getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSchedule);  
			       }
	 		          
		           if(getManagedBean().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	            	      getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFactoring);
	                     
		           } if(getManagedBean().isFlgPaymentTracking()) {
	            	         getManagedBean().getFinalListOfSideMenu().addAll(menu.menuPaymentTracking);
	                } 
		            getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSetting);
	                
		  } if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){
		               getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFulfilement);
		               if(getManagedBean().getCtContract().isZonalPricing()) {
			 		          getManagedBean().getFinalListOfSideMenu().addAll(menu.menuZone);
				      }
		               getManagedBean().getFinalListOfSideMenu().addAll(menu.menuItem);
	                   if(getManagedBean().getCtContract().isFactored()){
	            	       getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFactoring);
		                }
        	          getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSetting);   
		      }
		      
	 	    for( String str : getManagedBean().getFinalListOfSideMenu()){
	 	    	MenuItem zoneItem = new MenuItem();
				zoneItem.setValue(str);
				zoneItem.setId("zoneMenuID"+zoneTabIndex);
				zoneItem.setUpdate("@form");
				zoneItem.setActionExpression(FacesScopes
						.createMethodExpression(facesContext,
								"#{contractDetailsBackingBean.fulfilmentTabChangeAction("
										+ zoneTabIndex + ")}", null,
								new Class[] { Integer.class }));
				fulfilmentMenuModel.addMenuItem(zoneItem);

				zoneTabIndex++;
	 	    } getManagedBean().setFulfilmentMenuModel(fulfilmentMenuModel);
    }
	
    public boolean checkFactoringExpiryDate() {
    	if(getManagedBean().getCtContract().getCtFactoring() != null  &&
    		getManagedBean().getCtContract().getCtFactoring().getStatus() != null &&
    		getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()) {
    		   return true;
    	 }
    	return false;
    }
    
	public void fulfilmentTabChangeAction(Integer selectedTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("fulfilmentTabChangeAction");
		}

		getManagedBean().setSelectedTabIndex(selectedTabIndex);
	}

	
	private void renderUpdateCheckbox(CtContractVer ctContractVer) {
		if(ctContractVer.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE) &&
			this.getManagedBean().isUpdateContract()) {
			if(!ctContractVer.getCtContractValue().getSupplierName().equals(
		      this.getManagedBean().getCtContract().getSmSupplier().getCompanyName())) {
		    	this.getManagedBean().setUpdateCompanyName(true);
		    }
			
			SmAddress latestAddress = retrieveLatestSupplierAddress();
			if(!ctContractVer.getCtContractValue().getSupplierAddress().equals(latestAddress)) {
				this.getManagedBean().setUpdateSupplierAddress(true);
			}
		}
		else {
			this.getManagedBean().setUpdateCompanyName(ctContractVer.getCtContractValue().isCompanyNameChange());
			this.getManagedBean().setUpdateSupplierAddress(ctContractVer.getCtContractValue().isAddressChange());
		}
	}
	
	private void calAdvancePaymentTracking(Long contractId){
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractResDTO resDTO = new ContractResDTO();
		resDTO = contractSO.calAdvancePaymentTracking(contractId);
		
		BigDecimal paidAdvancePaymentAmt = (resDTO.getPaidAdvancePaymentAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidAdvancePaymentAmt();
		BigDecimal paidInvoiceAmt = (resDTO.getPaidInvoiceAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidInvoiceAmt();
		
		this.getManagedBean().setPaidAdvancePaymentAmt(paidAdvancePaymentAmt);
		this.getManagedBean().setPaidInvoiceAmt(paidInvoiceAmt);
	}
	
	private SmAddress retrieveLatestSupplierAddress() {
		SmAddress smAddress = new SmAddress();
		List<SmCompanyAddress> smCompanyAddresses = new ArrayList<SmCompanyAddress>();
		smCompanyAddresses = this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();
		for(SmCompanyAddress address : smCompanyAddresses) {
			if(address.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) { // C-Company Address, M-Mailing Address
				smAddress = address.getSmAddress();
			}
		}
		return smAddress;
	}
	
	/*Implementation of search method for contract*/
	public void searchContract() throws Exception {
		try { 
			if ((getManagedBean().getCtNumber() == null || getManagedBean().getCtNumber().length() == 0) && 
				(getManagedBean().getCtName() == null || getManagedBean().getCtName().length() == 0) &&
				(getManagedBean().getCtCircularNo() == null || getManagedBean().getCtCircularNo().length() == 0) &&
				(getManagedBean().getCtCircularName() == null || getManagedBean().getCtCircularName().length() == 0) &&
				(getManagedBean().getCtSupplierId() == null || getManagedBean().getCtSupplierId().length() == 0) &&
				(getManagedBean().getCtSupplierName() == null || getManagedBean().getCtSupplierName().length() == 0) && 
				getManagedBean().getCtDateStart() == null && 
				getManagedBean().getCtDateEnd() == null && 
				(getManagedBean().getCtPhysicalNo() == null || getManagedBean().getCtPhysicalNo().length() == 0) && 
				(getManagedBean().getCtLOANo() == null || getManagedBean().getCtLOANo().length() == 0) && 
				getManagedBean().getCtStatus() == 0 &&
				getManagedBean().getTerminationTypeId() == null &&
				getManagedBean().getCtContractStatus() == null) {
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_755000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				getManagedBean().getContractDataModel().reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		}
	}
	
	public String backAction() {
		getManagedBean().setViewContract(false);
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
	}
	
	public void searchReset(){
		this.getManagedBean().setCtNumber(null);
		this.getManagedBean().setCtName(null);	
		this.getManagedBean().setCtCircularNo(null);	
		this.getManagedBean().setCtCircularName(null);	
		this.getManagedBean().setCtSupplierId(null);	
		this.getManagedBean().setCtSupplierName(null);	
		this.getManagedBean().setCtDateStart(null);	
		this.getManagedBean().setCtDateEnd(null);	
		this.getManagedBean().setCtPhysicalNo(null);	
		this.getManagedBean().setCtLOANo(null);
		this.getManagedBean().setCtStatus(0);
		this.getManagedBean().setTerminationTypeId(null);
		this.getManagedBean().setCtContractStatus(null);
	}
		
    public String saveContract(String action) throws Exception{

    	IContractSO contractSO = null;
    	try{
    		getZoneObjDetails();
    		   validateCtContractVer(action);
			   ContractReqDTO reqDTO = new ContractReqDTO();
			   reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			   reqDTO.setOutcome(action);
			   reqDTO.setTaskId(getManagedBean().getTaskId());
	           reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			   
               if(getManagedBean().getContractVer().getCtContractItems().size() > 0) 
				    getManagedBean().getContractVer().getCtContractItems().clear();
            
                    getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtItemList());
                    getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtItemServiceList());
            
                setBondRecordStatus(getManagedBean().getContractVer());
              
                if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
                	scheduleProgressiveDate();
                }
            
                reqDTO.setCtContract(getManagedBean().getContractVer().getCtContract());
                CtContractVer ctContractVer = getManagedBean().getContractVer();
                if(ctContractVer.getCtContractValue().isCompanyNameChange()) 
            	      ctContractVer.getCtContractValue().setSupplierName(getManagedBean().getSupplierCompanyName());
            
                if(ctContractVer.getCtContractValue().isAddressChange()) 
                	 ctContractVer.getCtContractValue().setSupplierAddress(getManagedBean().getSmAddress());
            
                reqDTO.setCtContractVer(ctContractVer);
                reqDTO.setUserRole(getUserRole());
                reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
		        reqDTO.setAttachments(getManagedBean().getAttachmentList());
		        reqDTO.setUserMap(getManagedBean().getUserMap());
		        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			    reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			    if(this.getManagedBean().getPmDigiSign() !=null){
					reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
				}
			    
			    contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
	            ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		      
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
				   
				    FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			        if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
				     	renewSesionObject();
					    return CommonUIConst.ACKNOWLEDGE_PAGE;
				    }
			        else {				    	
				    	ContractReqDTO ctReqDTO = new ContractReqDTO();
				    	ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				    	ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
				    	
				    	ContractResDTO ctResDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
				    	setValidateContractField(ctResDTO);
				    	ConstructSideTabMenu();
			    	}
			  }
		  } finally {
			contractSO = null;
		  }
		
		return null;	
	}
    
    public void validateCtContractVer(String action) throws EpException {
    	CtContractVer contractVer = getManagedBean().getContractVer();
		Date today = new Date();
		getManagedBean().setAutoUpdate(true);
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
 			String remark = getManagedBean().getRemarkList().get(0).getRemark();
 			String agencyRemark = getManagedBean().getContractVer().getAgencyRemarks().get(0).getRemark();
 			String zoneRemark = getManagedBean().getContractVer().getZoneRemarks().get(0).getRemark();
 			String itemRemark = getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
 			String deductionRemark = getManagedBean().getContractVer().getDeductionRemarks().get(0).getRemark();
 			String scheduleRemark = getManagedBean().getContractVer().getScheduleRemarks().get(0).getRemark();
 			String settingRemark = getManagedBean().getContractVer().getSettingRemarks().get(0).getRemark();
 			if((remark == null || remark.length() == 0) && (agencyRemark == null || agencyRemark.length() == 0) &&  (zoneRemark == null || zoneRemark.length() == 0)
 					&& (itemRemark == null || itemRemark.length() == 0) && (deductionRemark == null || deductionRemark.length() == 0)
 					&& (scheduleRemark == null || scheduleRemark.length() == 0) && (settingRemark == null || settingRemark.length() == 0)) {
 				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
 			}
 		}
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
	    	getZoneObjDetails();
			if(contractVer.getCtContractZones()!=null) {
				for(CtContractZone contractZone : contractVer.getCtContractZones()) {
					if(contractZone.getDeliveryTerm()==0) {
						throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
					}
				}
			}
		}
		
		if(contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_CREATION) 
				|| contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_UPDATE)){
			if(getManagedBean().getCtContract().getContractType().getParameterCode().equals(ParameterConst.CONTRACT_TYPE_MINISTRY) 
					&& contractVer.getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)){
				throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
			}
			
			if(getManagedBean().getCtContract().isBondReq() && !getManagedBean().isDirectPurchase()) {
				if(contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_UPDATE)){
					if(getManagedBean().getCtContract().getLatestContractVer() != null 
							&& getManagedBean().getCtContract().getLatestContractVer().getCtPerformanceBonds() != null
							&& getManagedBean().getCtContract().getLatestContractVer().getCtPerformanceBonds().size() > 0
							&& (contractVer.getCtPerformanceBonds() == null || contractVer.getCtPerformanceBonds().isEmpty())) {
						throw new EpException(MessageConst.ERROR_100022, new String[] { "Performance Bond" });
					}
				}
				
				if(contractVer.getCtPerformanceBonds() != null && contractVer.getCtPerformanceBonds().size() > 0) {
					double bondAmount = 0.0;
					for(CtPerformanceBond performanceBond : contractVer.getCtPerformanceBonds()) {
						bondAmount = bondAmount + performanceBond.getBondAmount();
						if(performanceBond.getRefNo() == null || performanceBond.getRefNo().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage("lbl.ct.pb.ref.no", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), null});
						} 
						
						if(performanceBond.getFinancialOrg() == null || performanceBond.getFinancialOrg().getFinancialOrgId() == null) {
							throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.financial.org", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), null});
						} 
						
						if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
							if(performanceBond.getBondAmount()==0) {
								throw new EpException(MessageConst.ERROR_100033, new String[]{ResourceBundleUtils.getMessage("lbl.ct.pb.amount", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()),"0"});
							}
						} 
						
						if(performanceBond.getFileName() == null || performanceBond.getFileName().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022, new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.letter", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()), null});
						} 
					}
				 if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					 
					 double bondAmendAmount = 0.0;
						if(getManagedBean().getCtAmendment() != null && getManagedBean().getCtAmendment().getAmendmentLoa() != null
								&& getManagedBean().getCtAmendment().getAmendmentLoa().isBondReq()) {
								bondAmendAmount = bondAmendAmount+ getManagedBean().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue();
							}
				
					 if(contractVer.getCtContract().getLoa().getBondAmt() == null 
							&& getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						throw new EpException(MessageConst.ERROR_750021);
				
					} else if(contractVer.getCtContract().getLoa().getBondAmt() != null 
							&& getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
						int retval = Double.compare(bondAmount , contractVer.getCtContract().getLoa().getBondAmt().doubleValue() +bondAmendAmount);
						if( retval >0 ){
							throw new EpException(MessageConst.ERROR_750015);
						}
						if(retval < 0){
							throw new EpException(MessageConst.ERROR_750017);  
						}	
					 }
				  }
				}	
			}
		 
			if(getManagedBean().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {

				if(getManagedBean().getCtContract().isFrequencySchedule()){
					getManagedBean().getContractVer().setCtProgressivePayments(null);
				} else {
					getManagedBean().getContractVer().setCtSchedules(null);  	
				}

				if(getManagedBean().getCtContract().isFrequencySchedule()) {
			           	if(contractVer.getCtSchedules() == null || contractVer.getCtSchedules().size() < 2 
			           		 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
			           		throw new EpException(MessageConst.ERROR_750022);
			           	}
			           	
			           	for(CtSchedule schedules : contractVer.getCtSchedules()){
			           		    if(schedules.getStartMonth() == null) {
			           			     throw new EpException(MessageConst.ERROR_100022, new String[] { "Schedule start date"});
			    				  }
			           	
			           		    if(schedules.getEndMonth() == null) {
			           			   throw new EpException(MessageConst.ERROR_100022, new String[] { "Schedule end date"});
			           		    }
			           		    
			           		 if(schedules.getScheduleNo() == null) {
			           			 throw new EpException(MessageConst.ERROR_100022, new String[] { "Schedule no"});
		           		    }
			           		 
			           		if(schedules.getScheduleType() == null || schedules.getScheduleType().getParameterId() == null) {
		           			  throw new EpException(MessageConst.ERROR_100022, new String[] { "Schedule type"});
		           		    }
			             }     	
				} else if (contractVer.getCtProgressivePayments() == null || contractVer.getCtProgressivePayments().size() < 2 && !getManagedBean().getCtContract().isFrequencySchedule()
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
					throw new EpException(MessageConst.ERROR_750022);
				}

				if(contractVer.getCtProgressivePayments() != null && !contractVer.getCtProgressivePayments().isEmpty() && !getManagedBean().getCtContract().isFrequencySchedule()) {
					float percentage = 0.0f;
					for(CtProgressivePayment progressivePayment : contractVer.getCtProgressivePayments()) {
						percentage = percentage+progressivePayment.getPercentage();
						if(progressivePayment.getPhaseNo()== null) {
							throw new EpException(MessageConst.ERROR_100022,new String[] { "Progressive Phase" });
						}

						if(progressivePayment.getPhaseDesc()== null ||progressivePayment.getPhaseDesc() =="" ) {
							throw new EpException(MessageConst.ERROR_100022,new String[] { "Progressive Phase Description" });
						}
						
						    if(progressivePayment.getStartMonth() == null) {
		           			      throw new EpException(MessageConst.ERROR_100022, new String[] { "Progressive payment start date"});
		    				  }
		           	
		           		    if(progressivePayment.getEndMonth() == null) {
		           			   throw new EpException(MessageConst.ERROR_100022, new String[] { "Progressive payment end date"});
		           		    }     	
					}
					int value = Float.compare(percentage,100);
					if(value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
						throw new EpException(MessageConst.ERROR_100022,new String[] { "Progressive Percentage 100%" });
					}
				}
			}

			if(contractVer.getCtAgencies().size()>0) {
				double ceilingAmt = 0.0;
				for(CtAgency ctAgency : contractVer.getCtAgencies()) {
					ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
					if(ctAgency.isAp178() == true && contractVer.getAgencyAttachements().size() == 0 && 
							!ctAgency.getOrgProfile().getParentOrgProfile().getOrgName().equalsIgnoreCase(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName())){
						throw new EpException(MessageConst.ERROR_750008); 
						// Approval document is mandatory for AP178 agency. Please upload the document.
					}
				}
				if(ceilingAmt > contractVer.getCtContractValue().getContractAmount().doubleValue()){
					throw new EpException(MessageConst.ERROR_750007); // Total ceiling value exceeded contract value. Please check your ceiling amount.
				}

			}			

			if(contractVer.getCtContractItems() != null){
				for(CtContractItem item : contractVer.getCtContractItems()) {
					double totalCeilingQuantity = 0.0;
					if(item.getCtItemMaxQties().size() != 0){
						BigDecimal maxCeilingQty = new BigDecimal(0);
						for(CtItemMaxQty ctItemMaxQty : item.getCtItemMaxQties()){
							if(ctItemMaxQty.getEffDate().before(today) && ctItemMaxQty.getExpDate().after(today)){
								maxCeilingQty = ctItemMaxQty.getMaxQuantity();
								break;
							}
						}
						for(CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()){
							totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
						}
						if (maxCeilingQty.doubleValue() < totalCeilingQuantity) {
							throw new EpException(MessageConst.ERROR_750009, new String[]{item.getItemName()});
						}
					}
				}
			}
		}
		if (contractVer.getDocType().equals(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)){
			if(contractVer.getCtContractValue().getContractAmount().doubleValue() < ContractConst.CONTRACT_THRESHOLD_VALUE) {
				if(contractVer.getCtAmendment().isItemPriceChange() || contractVer.getCtAmendment().isQuantityChange() 
						|| contractVer.getCtAmendment().isValueChange()){
					throw new EpException(MessageConst.ERROR_750011);// If original contract value is less than RM500k (threshold value for tender), 
							//CA only can extend the contract period and change the clause.
				}
			}
			if(contractVer.getCtAmendment().isContractExtend()) {
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, ContractConst.CONTRACT_EXTEND_BEFORE_DAYS);
				
				if(contractVer.getCtContractValue().getExpDate().before(c.getTime())){
					throw new EpException(MessageConst.ERROR_750012);//CA only can extend the contract period 3 months before contract expired.
				}
			}
			for (CtContractItem ctContractItem : contractVer.getCtContractItems()){
				double sumOfCeilingQuantites = 0l;
				BigDecimal maxCeilingQty = new BigDecimal(0);
				if(ctContractItem.getCtItemMaxQties().size() != 0){
					for(CtItemMaxQty ctItemMaxQty : ctContractItem.getCtItemMaxQties()){
						if(ctItemMaxQty.getEffDate()==contractVer.getCtAmendment().getAmendmentEffDate()) {
							maxCeilingQty = ctItemMaxQty.getMaxQuantity();
							break;
						}
					}
					for (CtItemCeilingQty ctItemCeilingQty : ctContractItem.getCtItemCeilingQties()) {
						if(ctItemCeilingQty.getExpDate() == contractVer.getCtContractValue().getExpDate()) {
							sumOfCeilingQuantites = sumOfCeilingQuantites + ctItemCeilingQty.getCeilingQuantity().doubleValue();
						}
					}
					if(sumOfCeilingQuantites < maxCeilingQty.doubleValue()){
						throw new EpException(MessageConst.ERROR_750013);//Total ceiling amount for all PTJ defined in contract must be equals to contract amount.
					}
				}
			}
		}		
		
		if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getApproverModel().getTarget().size()==0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		if((getManagedBean().isUpdateContract() || getManagedBean().getCtContract().getCurrentContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FU_PENDING_SUBMISSION.longValue()) && 
		    action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getAttachmentList().isEmpty()){
			throw new EpException(MessageConst.ERROR_750018, new String[]{"contract update"});
		}
	}
    
    public void setBondRecordStatus(CtContractVer contractVar){
    	  if(contractVar.getCtPerformanceBonds().size() > 0) {
    		  for(CtPerformanceBond bond : contractVar.getCtPerformanceBonds()){
    			     if(bond.getRecordStatusFlg().intValue() == 1 &&  !bond.isRecordStatus() ){
    			    	 bond.setRecordStatus(true);
    			     } else if(bond.isRecordStatus() && bond.getRecordStatusFlg().intValue() != 1){
    			    	 bond.setRecordStatusFlg(1);
    			     }
    		  }
    	  }
    }
    
    public void convertScheduleDate(){
    	try {
    	 if(getManagedBean().getContractVer().getCtSchedules().size() > 0){
    		DateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
    		 for(CtSchedule ctschedule : this.getManagedBean().getContractVer().getCtSchedules()){
    			 Date date = dateFormat.parse(ctschedule.getStartMonth()+1+"-"+ ctschedule.getStartYear());
    			 Date date1 = dateFormat.parse(ctschedule.getEndMonth() +1+"-"+ctschedule.getEndYear());
    			 ctschedule.setFromDate(date);
    			 ctschedule.setToDate(date1); 
    		 }
    	}
      }catch (ParseException e) {
			e.printStackTrace();
	  }
    }
    
    public void convertProgressiveScheduleDate() {
    	try {
    	
    		if(this.getManagedBean().getContractVer().getCtProgressivePayments().size() > 0){
    		     DateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
    		     for(CtProgressivePayment ctProgressivePayment : this.getManagedBean().getContractVer().getCtProgressivePayments()){
    			        Date date = dateFormat.parse(ctProgressivePayment.getStartMonth()+1+"-"+ ctProgressivePayment.getStartYear());
    			        ctProgressivePayment.setFromDate(date);
    			        Date date1 = dateFormat.parse(ctProgressivePayment.getEndMonth()+1+"-"+ ctProgressivePayment.getEndYear());
    			        ctProgressivePayment.setToDate(date1); 
    		     }
    	   }
      } catch (ParseException e) {
			e.printStackTrace();
	  }
    }
    
   public int  calculateTotalMonth(Object Var, String frequencyType){
	   DateFormat dateFormat = null;
	   String fromDate = null;
	   String toDate = null;
	   try{
		     
           dateFormat = new SimpleDateFormat("MM-yyyy");
		  
           if(frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
		             CtSchedule schedules = (CtSchedule)Var;
		              if(schedules.getEndMonth() != null) {
	    	                 fromDate = schedules.getStartMonth()+"-"+ schedules.getStartYear();
		                     toDate = schedules.getEndMonth() +"-"+schedules.getEndYear();
		              }
           } else if(frequencyType.equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
        	      CtProgressivePayment progressivePayment = (CtProgressivePayment)Var;
	              if(progressivePayment.getEndMonth() != null) {
	            	    fromDate = progressivePayment.getStartMonth()+"-"+progressivePayment.getStartYear();
    			        toDate =  progressivePayment.getEndMonth()+"-"+progressivePayment.getEndYear();
    			   }
           }
		   
           Date startDate = dateFormat.parse((fromDate));
		   Date endDate = dateFormat.parse((toDate));
		   Calendar sDate = Calendar.getInstance();
		   Calendar eDate = Calendar.getInstance();
		   sDate.setTime(startDate);
		   eDate.setTime(endDate);
		   int months  = (eDate.get(Calendar.YEAR) - sDate.get(Calendar.YEAR)) * 12 +
			       (eDate.get(Calendar.MONTH)- sDate.get(Calendar.MONTH));
		 
		   if(months < 0){
			   return 0;
		   }
		   return months+1;
         } catch(Exception ex){ }
	   finally {
		 
	   }
	   return 0;	
    }
	
	public void retriveZonesByContractVer() {
		
		TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
		TreeNode zoneNode =  null;
	    for(CtContractZone zone : this.getManagedBean().getContractVer().getCtContractZones()) {
			   if(zone.getDeliveryTermUom() == null){
				   zone.setDeliveryTermUom(new PmParameter()); 
				   zone.getDeliveryTermUom().setParameterId(0L);
			   }
			   zoneNode = new DefaultTreeNode("zone", new LocationDTO(zone.toString(), zone.getZoneName(), zone.getDeliveryTerm(), zone.getDeliveryTermUom().getParameterId()), rootLocationTree);
			   List<PmState> state = new ArrayList<PmState>();
			   if(zone.getCtContractZoneDtls() != null) {
				 for(CtContractZoneDtl zoneDtl : zone.getCtContractZoneDtls()) {
					 
				   if(zoneDtl.getState() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_STATE)){
					  if(!state.contains(zoneDtl.getState())){
						  new DefaultTreeNode("state",new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getState()) , zoneNode);
					   }
					  state.add(zoneDtl.getState());
				    }
				   if(zoneDtl.getDistrict() != null && getManagedBean().getContractVer().getCtContract().getLocationLevel().getParameterCode().equalsIgnoreCase(ParameterConst.LOCATION_LEVEL_DISTRICT)){
					   new DefaultTreeNode("district", new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getDistrict()), zoneNode);
				   }
				 }
			  }
			}
		 this.getManagedBean().setCtZoneTrees(rootLocationTree);
	} 
	
    public void getZoneObjDetails() {
    	if(!getManagedBean().getContractVer().getCtContractZones().isEmpty()) {
    		for(CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
    			for(TreeNode zonename : getManagedBean().getCtZoneTrees().getChildren()){
    				if(ctContractZone.toString().equalsIgnoreCase(((LocationDTO)zonename.getData()).getZoneId())) {
    					if(ctContractZone.getDeliveryTermUom() == null)
     					   ctContractZone.setDeliveryTermUom(new PmParameter());
     				ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
     				ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
     			}
    		}
    	}
     }
	}
    
	public SelectItem[] retrieveFinancialOrgList() throws NamingException {
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;
		try{
		    int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		    for(PmFinancialOrg pmFinancialOrg : list) 
				  items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());   
	    } catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			financialOrgSO = null;
		}
	   return items;
	}
	
	 public void uploadAp201File(FileUploadEvent event) throws Exception {
	    	UploadedFile item = event.getFile();
			AttachmentFormat attachmentFormat = new AttachmentDateFormat();
			
			String filePath = getManagedBean().getFileDirectory();
			File file = new File(filePath);
			file.mkdirs();
			filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
			FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
		    fileOuputStream.write(item.getContents());
		    fileOuputStream.close();
		    
		    getManagedBean().getContractVer().getCtContractValue().setAp201FileName(item.getFileName());
		    getManagedBean().getContractVer().getCtContractValue().setAp201FilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
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
	    
	    for(CtPerformanceBond ctPerformanceBond : getManagedBean().getContractVer().getCtPerformanceBonds()){
	    	if(ctPerformanceBond.equals(getManagedBean().getCtPerformanceBond())){
	    		ctPerformanceBond.setFileName(item.getFileName());
	    		ctPerformanceBond.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	    	 }
	     }
	}
    
    
    
	public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
	    if(filePath!=null) {
	    	filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
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
				out.write( buffer, 0, bytesRead );
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
    public List<SelectItem> getItemTypeDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.SC_ITEM_TYPE);
		for(SelectItem item : items) {
		  if(item.getValue()!=null) {
			 if(item.getValue().toString().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT) || item.getValue().toString().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				list.add(item);
			 }
		  }
		}
		return list;
    }
    
    public SelectItem[] getUserGroupDropdown() {
		
		SelectItem[] items = null;
		try{
			items = new SelectItem[getManagedBean().getUserGroupMap().values().size()];
			int i = 0;
			for(PmUserGroup userGroup : this.getManagedBean().getUserGroupMap().values()) 
			  items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
		}catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}/*End approval details*/
	
	public SelectItem[] getMinistryDropdown() {
		
		SelectItem[] items = null;     
		try{
		    int i = 0;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		    OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size()];
			for(PmOrgProfile orgProfile : list) {
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
			}
		 }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
	    return items;
	}
	
	public SelectItem[] getDepartmentDropdown() {
		
		SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		   reqDTO.setParentOrgProfileId(this.getManagedBean().getMinistryProfileId());
		   OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		   List<PmOrgProfile> list = resDTO.getOrgProfileList();
		   items = new SelectItem[list.size()+1];
	       items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
	       for(PmOrgProfile orgProfile : list) {
		       items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
	       }
		 }catch (Exception e) {
	    	   e.printStackTrace();
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	     }
	   return items;
	}
	 
	public SelectItem[] getSearchStatusDropdown() {
		SelectItem[] items = null;
		try {
			items = new SelectItem[3];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_COMPLETE,  ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_INCOMPLETE,  ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}
	
	public SelectItem[] getSearchContractStatusDropdown() {
		IStatusSO statusSO = null;
		SelectItem[] items = null;
		try {
			statusSO = (IStatusSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IStatusSO.class);
			String languageCode = getFacesScopes().getUserLocale().getLanguage();
			
			int i = 1;
		 	StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("CT");
			List <String> docTypeList = new ArrayList<String>();
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_CREATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			docTypeList.add(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			
			reqDTO.setDocTypeList(docTypeList);
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
			
		    List<PmStatusDesc> list =  resDTO.getStatusDescList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		      
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
	
	public String changeIntToString(int temp){
		String months = "";
		
		if (temp ==1) {
			months = "01";
		} else if (temp ==2) {
			months = "02";
		} else if (temp ==3) {
			months = "03";
		} else if (temp ==4) {
			months = "04";
		} else if (temp ==5) {
			months = "05";
		} else if (temp ==6) {
			months = "06";
		} else if (temp ==7) {
			months = "07";
		} else if (temp ==8) {
			months = "08";
		} else if (temp ==9) {
			months = "09";
		} else if (temp ==10) {
			months = "10";
		} else if (temp ==11) {
			months = "11";
		} else if (temp ==12) {
			months = "12";
		}
		
		return months;
		
	}
	
	public List<SelectItem> getMonthList(){
		return new ArrayList<SelectItem>();
	}
	
	public List<SelectItem> retrieveStartMonthList(String  tableStartYear){
		 
	     List<SelectItem> selectItems = null;
	     Map<String, String> months = null;
	     SimpleDateFormat dateFormat = null;
        try {
	
					months = new HashMap<>();
					months.put("01", "January");
					months.put("02", "February");
					months.put("03", "March");
					months.put("04", "April");
					months.put("05", "May");
					months.put("06", "June");
					months.put("07", "July");
					months.put("08", "August");
					months.put("09", "September");
					months.put("10", "October");
					months.put("11", "November");
					months.put("12", "December");  
					
					int schedStartYear =0;
					dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
					String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
					int startMonth = Integer.valueOf(fromdate.substring(3, 5));
					int startYear = Integer.valueOf(fromdate.substring(6, 10));
				
					if(tableStartYear != null && !tableStartYear.equalsIgnoreCase("")) {
					    schedStartYear = Integer.valueOf(tableStartYear);
				 	} 
					
					 String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
					 int endMonth = Integer.valueOf(enddate.substring(3, 5));
					 int endYear = Integer.valueOf(enddate.substring(6, 10));
		 
					 selectItems = new ArrayList<SelectItem>();
					 selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
					 
					 if((schedStartYear == 0) || (schedStartYear == startYear && schedStartYear != endYear)) { 
					      for(int i=startMonth; i <= 12; i++) 
					    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
			
				    } else if(schedStartYear == startYear  && schedStartYear == endYear) { 
					      for(int i=startMonth; i <= endMonth; i++) 
					    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
			
				    }else if(schedStartYear != startYear  && schedStartYear == endYear) { 
					      for(int i=1; i <= endMonth; i++) 
					    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
			
				    } else if(schedStartYear > startYear && schedStartYear < endYear) {
				             for(int i=1; i <= 12; i++) 
				                selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
					 } 
             } finally { }
	 return selectItems;
}
	
	
	
	public List<SelectItem> retrieveEndMonthList(String tableEndYear){
		 
		     List<SelectItem> selectItems = null;
		     Map<String, String> months = null;
		     SimpleDateFormat dateFormat = null;
	         try {
		
						months = new HashMap<>();
						months.put("01", "January");
						months.put("02", "February");
						months.put("03", "March");
						months.put("04", "April");
						months.put("05", "May");
						months.put("06", "June");
						months.put("07", "July");
						months.put("08", "August");
						months.put("09", "September");
						months.put("10", "October");
						months.put("11", "November");
						months.put("12", "December");  
						
					    int schedEndYear =0;
						dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
						String fromdate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
						int startMonth = Integer.valueOf(fromdate.substring(3, 5));
						int startYear = Integer.valueOf(fromdate.substring(6, 10));
					
						if(tableEndYear != null && !tableEndYear.equalsIgnoreCase("")) {
							    schedEndYear = Integer.valueOf(tableEndYear);
						}
			
						 String enddate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
						 int endMonth = Integer.valueOf(enddate.substring(3, 5));
						 int endYear = Integer.valueOf(enddate.substring(6, 10));
			 
						 selectItems = new ArrayList<SelectItem>();
						 selectItems.add(new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));
						 
						  if(schedEndYear == endYear && schedEndYear == startYear) { 
						      for(int i=startMonth; i <= endMonth; i++) 
						    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				
					    } else if(schedEndYear == endYear && schedEndYear != startYear) { 
						      for(int i=1; i <= endMonth; i++) 
						    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				
					    }  else if(schedEndYear != endYear && schedEndYear == startYear) { 
						      for(int i=startMonth; i <= 12; i++) 
						    	  selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
				
					    } else if( schedEndYear >  startYear && schedEndYear < endYear) {
					             for(int i=1; i <= 12; i++) 
					                selectItems.add(new SelectItem(changeIntToString(i), months.get(changeIntToString(i))));
						 } 
                  } finally { }
		 return selectItems;
	 }

	public List<String> getYearList() {
	 
		if(getManagedBean().getYearList() == null) {
			getManagedBean().setYearList(new ArrayList<String>());
		    SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		    int startYear = Integer.valueOf(df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()).substring(6, 10));
		    int endYear = Integer.valueOf(df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()).substring(6, 10));
		    for(int i = startYear; i <= endYear; i++) {
		    	getManagedBean().getYearList().add(String.valueOf(i));
		    }
		}
      return getManagedBean().getYearList();
	}
	
	public SelectItem[] getScheduleTypeDropDown() {
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = null;
		
		try {
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance()
					.getParameterDescListBySearchBean(ParameterTypeConst.SCHEDULE_TYPE, searchBean);
			
		    items = new SelectItem[parameterDescs.size()];
		    int i = 0;
		    for(PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(
		    			parameterDesc.getPmParameter().getParameterId(), 
		    			parameterDesc.getCodeName());
		    }
		} catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		
	    return items;
	}
	
	private void flush() {
	   this.getManagedBean().setItemPricing(false);
	   this.getManagedBean().setItemCeilingQuantity(false);
	   this.getManagedBean().setProductItem(false);
	}
	
	public void flushItem(){
       this.getManagedBean().setServiceItem(false);
       this.getManagedBean().setProductItem(false);
       this.getManagedBean().setItemPricing(false);
       this.getManagedBean().setItemCeilingQuantity(false);
       this.getManagedBean().setSelectedItem(null);
       this.getManagedBean().setShowItem(null);
    }
	
    public void changeField() {
		try{
			flush();
			if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				getManagedBean().setItemPricing(true);

			} else if (getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
				getManagedBean().setItemCeilingQuantity(true);
				refreshAgencyCeilingQuantityList();
			}
		}catch (Exception e) {
			System.out.println(e);
		}
	}
    
  public void  refreshCeilingQuantity(){
	  
	  if(getManagedBean().getSelectedItem() != null) {
		  if(getManagedBean().getShowItem() != null &&
				   getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)){
		      getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
		  } else if(getManagedBean().getShowItem() != null &&
				   getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)){
			  getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			  refreshAgencyCeilingQuantityList();
		  } else {
			  getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
		  }
	  }
    }
    
    public void changeItemTypes(){
    	try{
    		flushItem();
    		if(this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				this.getManagedBean().setServiceItem(true);
			}else if(this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
				this.getManagedBean().setProductItem(true);
			}
    	} catch (Exception e) {
			System.out.println(e);
		}
    }
    
    private void refreshAgencyCeilingQuantityList() {
    	Map<Long, CtItemCeilingQty> itemCeilingQtyMap = new HashMap<Long, CtItemCeilingQty>();
    	for(CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
    		itemCeilingQtyMap.put(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId(), ctItemCeilingQty);
    	}
    	for(CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()) {
    		if(!itemCeilingQtyMap.containsKey(ctAgency.getOrgProfile().getOrgProfileId())) {
    			CtItemCeilingQty ctItemCeilingQty = new CtItemCeilingQty();
    			ctItemCeilingQty.setCtAgency(ctAgency);
    			ctItemCeilingQty.setCtContractItem(getManagedBean().getSelectedItem());
    			ctItemCeilingQty.setCeilingQuantity(new BigDecimal(0));
    			ctItemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
    			ctItemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
    			getManagedBean().getSelectedItem().getCtItemCeilingQties().add(ctItemCeilingQty);
    		}
    	}
    }
    
    /*Deduction Start*/
    public void addDeductionDetails() {
		IParameterSO parameterSO;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT);
			
			CtDeduction ctDeductions = new CtDeduction();
			ctDeductions.setDeductionType(parameterSO.retrieveParameterByTypeAndCode(reqDTO).getPmParameter());
			this.getManagedBean().getContractVer().getCtDeductions().add(ctDeductions);
		} finally {
			parameterSO = null;	
		}
	}

	public void deleteDeductionDetails() {
		this.getManagedBean().getContractVer().getCtDeductions().remove(getManagedBean().getSelectedDeduction());
		getManagedBean().setSelectedDeduction(null);
	}
	
	public void addDeductionClause() {
		CtDeductionClause clause = new CtDeductionClause();
		this.getManagedBean().getSelectedDeduction().getCtDeductionClauses().add(clause);
	}

	public void deleteClauseDetails() {
		for(int i=0; i < selectedDeductionClauses.length; i++){
			 CtDeductionClause clause = selectedDeductionClauses[i];
			 this.getManagedBean().getSelectedDeduction().getCtDeductionClauses().remove(clause);
		}selectedDeductionClauses= null;
	}
	
    /*Schedule Start*/
    public void progressivePayment() {
		
       getManagedBean().setChkSchedule(false);
  	   getManagedBean().setChkProgressive(false);
  	   getManagedBean().setProgSchedButtonDeactive(false);
  	   if(getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_SCHEDULE)) {
  		   getManagedBean().getContractVer().getCtContract().setFrequencySchedule(true);
  	   } else if(getManagedBean().getFrequencySchedule().equalsIgnoreCase(ContractConst.CONTRACT_FREQUENCY_PROGRESSIVE)) {
  	 	   getManagedBean().getContractVer().getCtContract().setFrequencySchedule(false);
  	   }   
	}
    
    public void addScheduleDetails(){
		CtSchedule ctSchedules = new CtSchedule();
		ctSchedules.setScheduleNo(getManagedBean().getContractVer().getCtSchedules().size()+1);
		ctSchedules.setScheduleType(new PmParameter());
		getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
	}
	
	public void deleteScheduleDetails(){
		for (int i = 0; i < selectedSchedule.length; i++) {
			CtSchedule ctSchedule = selectedSchedule[i];
			this.getManagedBean().getContractVer().getCtSchedules().remove(ctSchedule);
		}
		selectedSchedule = null;	
	}

	public void addFrequencyDetails() {
		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
		ctProgressivePayment.setPhaseNo(getManagedBean().getContractVer().getCtProgressivePayments().size()+1);
		getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	public void deleteFrequencyDetails() {
		getManagedBean().setProgSchedButtonDeactive(false);
		for (int i = 0; i < selectedProgressive.length; i++) {
			CtProgressivePayment ctProgressivePayment = selectedProgressive[i];
			this.getManagedBean().getContractVer().getCtProgressivePayments().remove(ctProgressivePayment);
		}
		selectedProgressive = null;
	}/*End schedule*/
    
    /*Implementation of Bond Details*/
    public void addBondDetails() {
    	
		CtPerformanceBond newBond = new CtPerformanceBond();
		newBond.setFinancialOrg(new PmFinancialOrg());
		newBond.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
	    Calendar cal = Calendar.getInstance();
        cal.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
        cal.add(Calendar.YEAR, 1); 
        newBond.setExpDate(cal.getTime());
     	getManagedBean().getContractVer().getCtPerformanceBonds().add(newBond);
	}
    
    public void delBondDetails() {
    	for(int i = 0; i < selectedBondList.length; i++) {
			CtPerformanceBond ctPerformanceBond = selectedBondList[i];
			this.getManagedBean().getContractVer().getCtPerformanceBonds().remove(ctPerformanceBond);
		}selectedBondList = null;	
	}
    
	public void addAgencyDetails(){
		List<CtAgency> ctAgencyList = new ArrayList<CtAgency>();
		
		for(int i=0; i<selectedPmOrgProfile.length; i++) {
			
			//Remove if already exist, avoid duplicate
		  	for(CtAgency agency : this.getManagedBean().getContractVer().getCtAgencies()) {
		  		if(agency.getOrgProfile().getOrgProfileId().equals(selectedPmOrgProfile[i].getOrgProfileId())) {
		  			 throw new EpException(MessageConst.INFO_758000,new String[]{agency.getOrgProfile().getOrgName()}); 
		  		}
		  	}
			
			CtAgency ctAgency = new CtAgency();
			ctAgency.setEffDate(new Date());
			ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			ctAgency.setCeilingAmount(new BigDecimal(0));
			ctAgency.setChkStatus(true);
			
			 IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			 OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			 reqDTO.setOrgProfileId(selectedPmOrgProfile[i].getOrgProfileId());
			 OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileDtlByProfileId(reqDTO);
			 ctAgency.setOrgProfile(resDTO.getPmOrgProfile());
			 
			 if(!getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgProfileId()
						.equals(ctAgency.getOrgProfile().getParentOrgProfile().getOrgProfileId()))
				 ctAgency.setAp178(true);
		  	
		  	ctAgency.setCtContractVer(this.getManagedBean().getContractVer());
		  	ctAgencyList.add(ctAgency);
		  
		  	if(ctAgency != null && this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 1){
		  		for(CtContractItem contractItem: this.getManagedBean().getCtItemList()){
		  			CtItemCeilingQty itemCeilingQty = new CtItemCeilingQty();
		    		itemCeilingQty.setCtAgency(ctAgency);
		    		itemCeilingQty.setCtContractItem(contractItem);
		    		itemCeilingQty.setCeilingQuantity(new BigDecimal(0));
		    		itemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
		    		itemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		    		contractItem.getCtItemCeilingQties().add(itemCeilingQty);
		  		}
			}
		}
		
		if (selectedPmOrgProfile.length>0){
			this.getManagedBean().getContractVer().getCtAgencies().addAll(ctAgencyList);
		}
		selectedPmOrgProfile = null;
	}
	 
	public void deleteAgencyDetails(){
		try {
			if(this.getManagedBean().getContractVer().getCtAgencies() != null){
				Iterator<CtAgency> agencyIterator = this.getManagedBean().getContractVer().getCtAgencies().iterator();
				while(agencyIterator.hasNext()){
					CtAgency agency = agencyIterator.next();
					if(agency.isSelectedCheckBox()){
						agencyIterator.remove();
						if(this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 1){
						     for(CtContractItem contractItem: this.getManagedBean().getCtItemList()){
						    	 Iterator<CtItemCeilingQty> ctItemCeilingQtyIterator =  contractItem.getCtItemCeilingQties().iterator();
						    	 while(ctItemCeilingQtyIterator.hasNext()){
										CtItemCeilingQty ctItemCeilingQty = ctItemCeilingQtyIterator.next();
										if(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId().equals(agency.getOrgProfile().getOrgProfileId())){
											ctItemCeilingQtyIterator.remove();
										}
									}
						     }	
					   }
					}
				}
			}
			
			this.getManagedBean().setSelectedAgency(null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		
		
		
//	  for(int i = 0; i < selectedListOfAgency.length; i++) {
//		 CtAgency ctAgency = selectedListOfAgency[i];
//		 this.getManagedBean().getContractVer().getCtAgencies().remove(ctAgency);
//		 if(this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 1){
//			     for(CtContractItem contractItem: this.getManagedBean().getCtItemList()){
//			    	 Iterator<CtItemCeilingQty> ctItemCeilingQtyIterator =  contractItem.getCtItemCeilingQties().iterator();
//			    	 while(ctItemCeilingQtyIterator.hasNext()){
//							CtItemCeilingQty ctItemCeilingQty = ctItemCeilingQtyIterator.next();
//							if(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId().equals(ctAgency.getOrgProfile().getOrgProfileId())){
//								ctItemCeilingQtyIterator.remove();
//							}
//						}
//			     }	
//		   }
//	  }selectedListOfAgency = null;
	}
	
	public void selectedZoneValue(){
	    this.getManagedBean().setShowZoneDetails(true);
	    this.getManagedBean().setCtContractZoneDtls(this.getManagedBean().getSelectedCtZone().getCtContractZoneDtls());
	 }
    
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}
    
    public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}

	private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
			return RoleCode.CT_APPROVER.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
			return RoleCode.BPK_CCA.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
			return RoleCode.BPK_APPROVER.value();
		else 
			return null;
	}
	
	 public String retrieveParameterById(Long parameterId) throws NamingException {
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
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
	 
	 public String getDeductionType(Long parameterId){
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
			reqDTO.setLanguageCode("EN");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			return resDTO.getParameterDescList().get(0).getCodeName();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 }
		
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(getManagedBean().getContractVer().getSettingRemarks());
	}
		
	private void initiateRemark(List<CtRemark> remarks) {
		boolean ctWorkflowStatusMatch = false;
		if(getManagedBean().getContractVer().getCtWorkflowStatus()!=null) {
			if(remarks.size()>0 && remarks.get(0).getCtWorkflowStatus()!=null) {
				if(getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId()==remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
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
		
	public String getItemOriginTypes(PmParameter pmParameter) throws NamingException{
			
		if(retrieveParameterById(pmParameter.getParameterId()).equalsIgnoreCase("L"))
			this.getManagedBean().setPopupForImportedItems("I");
		else
			this.getManagedBean().setPopupForImportedItems("L");
		 return getParameterDesc(pmParameter);
	}
	
	public Long getParameterIdByTypeAndCode(String paramterType, String parameterCode) {
		
		IParameterSO parameterSO = null;
		ParameterReqDTO reqDTO = null;
		try{
				
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(paramterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return  resDTO.getPmParameter().getParameterId();
		}catch(Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			parameterSO = null;
		}
		return 0l;
	 } 
		
    public int retrieveParameterByTypeAndCode(String parameterCode) throws NamingException {
				
		IParameterSO parameterSO = null;
		ParameterReqDTO reqDTO = null;
		try{
				
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(ParameterTypeConst.TERMINATION_DECISION_TYPE);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter().getParameterId().intValue();
		}catch(Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			parameterSO = null;
		}
		return 0;
	 } 
		 
	 public String getContractItemType(Long paramterId){
	 	return new String();
	 }
		 
     public void validateDate(CtSchedule ctSchedule,  int index){
    	 
    	 getManagedBean().getContractVer().getCtSchedules().remove(index);
    	 getManagedBean().getContractVer().getCtSchedules().add(index, ctSchedule);
    	 
    	 getManagedBean().setProgSchedButtonDeactive(false);
    	 if(ctSchedule.getEndMonth() != null && ctSchedule.getStartMonth() != null) {
    		  
	    	           int startYear = Integer.valueOf((String) ctSchedule.getStartYear());  
			            int endYear = Integer.valueOf((String) ctSchedule.getEndYear());
			            int startMonth = Integer.valueOf((String) ctSchedule.getStartMonth());  
			            int endMonth = Integer.valueOf((String) ctSchedule.getEndMonth());
			      
					    if((startMonth > endMonth && startYear >= endYear)|| (startMonth <= endMonth && startYear > endYear)) {
			                   for(CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {			            
			                	   if(dataList.toString().equalsIgnoreCase(ctSchedule.toString())) {
			    	             	     getManagedBean().setProgSchedButtonDeactive(true);
			                	         throw new EpException(MessageConst.ERROR_750023);
			                	   }
			                }
					    } 
    	        }
	  }
		 
    public void validateProgressiveDate(CtProgressivePayment  ctProgressivePayment, int index){
    	
    	    getManagedBean().getContractVer().getCtProgressivePayments().remove(index);
    	    getManagedBean().getContractVer().getCtProgressivePayments().add(index, ctProgressivePayment);
    	 
    	    calculatePercentage(ctProgressivePayment);
    	    getManagedBean().setProgSchedButtonDeactive(false);
    	    if(ctProgressivePayment.getEndMonth() != null && ctProgressivePayment.getStartMonth() != null) {
    		
		         int startYear = Integer.valueOf((String) ctProgressivePayment.getStartYear());  
			     int endYear = Integer.valueOf((String) ctProgressivePayment.getEndYear());
			     int startMonth = Integer.valueOf((String) ctProgressivePayment.getStartMonth());  
			     int endMonth = Integer.valueOf((String) ctProgressivePayment.getEndMonth());
			   
			     if((startMonth > endMonth  &&  startYear >= endYear)||(startMonth <= endMonth  && startYear > endYear)) {
			             for(CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
			    	             if(dataList.equals(ctProgressivePayment)) {
			    		             getManagedBean().setProgSchedButtonDeactive(true);
			    		             throw new EpException(MessageConst.ERROR_750024);
			    	             }
			              }
			      }
    	   }
	  }
		 
    public void chkJustificationAction(){
		  try {
			    if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
			    }
			    
			    if(this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
			    	if(!this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()){
			    		this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
			    		this.getManagedBean().setChkJustification(true);
			    	}
			    }
			    
			    if(this.getManagedBean().isChkJustification() && !this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    	this.getManagedBean().setChkJustification(false);
			    }
			    
			    if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && this.getManagedBean().getContractVer().getCtContractValue().isConcession() ){
			    	 this.getManagedBean().setChkConceValue(true);
			    }else{
			    	 this.getManagedBean().setChkConceValue(false);
			    }
			    
		   } catch (Exception e) {
				System.out.println(e);
		   }
	}
    
    public void validateCeilingAmount(CtAgency agencyData) {
    	List<CtAgency> agencyList = this.getManagedBean().getContractVer().getCtAgencies();
    	if(agencyList.size()>0) {
			double ceilingAmt = 0.0;
			for(CtAgency ctAgency : agencyList) {
				ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
			}
			if(ceilingAmt >  this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue()) {
				agencyData.setCeilingAmount(BigDecimal.valueOf(0));
				throw new EpException(MessageConst.ERROR_750007); // Total ceiling value exceeded contract value. Please check your ceiling amount.
			}
    	}
    }
    
    public void validateCeilingQty(CtItemCeilingQty ceilingQtyData) {
    	Date today = new Date();
    	double totalCeilingQuantity = 0.0;
    	CtContractItem item = this.getManagedBean().getSelectedItem();
		if(item.getCtItemMaxQties().size() != 0){
			BigDecimal maxCeilingQty = new BigDecimal(0);
			for(CtItemMaxQty ctItemMaxQty : item.getCtItemMaxQties()){
				if(ctItemMaxQty.getEffDate().before(today) && ctItemMaxQty.getExpDate().after(today)){
					maxCeilingQty = ctItemMaxQty.getMaxQuantity();
					break;
				}
			}
			for(CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()){
				totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
			}
			if (maxCeilingQty.doubleValue() < totalCeilingQuantity) {
				ceilingQtyData.setCeilingQuantity(BigDecimal.valueOf(0));
				throw new EpException(MessageConst.ERROR_750009, new String[]{item.getItemName()});
			}
		}
    }
			
	public void onRowSelect(SelectEvent event) {}/*End*//*End Item Details*/
    public void onEdit(RowEditEvent event){}
	public void onCancel(RowEditEvent event){}/*End Deduction*/
	
	public void calculatePercentage(CtProgressivePayment progPayment){
		float addValue = 0.0f;
  	    getManagedBean().setProgSchedButtonDeactive(false);
  	    for(CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
		    addValue = addValue +progressivePayment.getPercentage();
  	     }
 		for(CtProgressivePayment progressivePayment: getManagedBean().getContractVer().getCtProgressivePayments()) {
			     if(progressivePayment.equals(progPayment)) {
			    	  if(Math.abs(addValue) > 100) {
		    	             getManagedBean().setProgSchedButtonDeactive(true);
		    	             progressivePayment.setPercentageExceeded(true);
			    	  } else if(Math.abs(addValue) == 100) {
		    	           getManagedBean().setProgSchedButtonDeactive(true);
		    	           progressivePayment.setPercentageExceeded(false);
			    	  } else {
			    		  progressivePayment.setPercentageExceeded(false);
		              }
			      } else {
			    	  progressivePayment.setPercentageExceeded(false);
			      }
 		   }  
	}
			    
	public String chkContractStatusType(int status){
		if(status==ContractConst.STATUS_COMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
	}
	
	public String gotoTerminatePage() {
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		TerminationBackingBean trBB= null;
		try{
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			trBB = new TerminationBackingBean();
			trBB.getManagedBean().setViewContract(false);
			trBB.setTerminationContractVer(resDTO);
		}catch(Exception e) {
				e.printStackTrace();
		} finally{
		    contractSO = null;
			resDTO = null;
			reqDTO = null;
			trBB= null;
		}
		
		return PROCESS_TERMINATE_PAGE;
	} 
	
	public String gotoSuspendPage() {
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		SuspensionBackingBean suspBB = null;
		try{
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		    reqDTO.setContractId(contractId);
		   Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			
			suspBB = new SuspensionBackingBean();
			suspBB.getManagedBean().setViewOnly(false);
			suspBB.setSuspensionContractVer(resDTO);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			contractSO = null;
			resDTO = null;
			reqDTO = null;
			suspBB = null;
		}
		return PROCESS_SUSPEND_PAGE;
	}
	
	public String gotoAmendPage() {
		IContractSO contractSO = null;
		AmendmentBackingBean amendBB = null;
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
				
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			if (action.equalsIgnoreCase("update"))
				reqDTO.setUpdateAction(true);
			ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
			amendBB = new AmendmentBackingBean();
			amendBB.getManagedBean().setSelectedTabIndex(0);
			amendBB.getManagedBean().setShowItem(null);
			amendBB.getManagedBean().setSelectedItem(null);
			amendBB.getManagedBean().setSelectedServiceItem(null);
			amendBB.setAmendmentContractVer(resDTO);
		}finally {
			contractSO = null;
			ctBB = null;
		}
		return PROCESS_AMEND_PAGE;
	}
	
	public String gotoViewPage() {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		
		 IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		 ContractReqDTO reqDTO = new ContractReqDTO();
		 reqDTO.setContractId(contractId);
		 CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		 if (latestContractVer != null)
			 reqDTO.setContractVerId(latestContractVer.getContractVerId());
		 else
			 reqDTO.setContractVerId(contractVerId);
		 reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		 reqDTO.setViewOnly(true);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
	    getManagedBean().setViewContract(true);
	    setValidateContractField(resDTO); 
	    getManagedBean().setSelectedTabIndex(0);
	    String percentage = contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage();
	    getManagedBean().setWarningPercentage(percentage);
		return CONTRACT_PAGE;
	}
	
	public String gotoUpdatePage(){
		IContractSO contractSO = null;
		try {
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
				
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			reqDTO.setUpdateAction(true);
			getManagedBean().setUpdateContract(true);
			
		    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
		    setValidateContractField(resDTO);
		    
		    getManagedBean().setAction(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		    
	        getManagedBean().setViewContract(false);
	        this.getManagedBean().setSelectedTabIndex(0);
	     
			return CONTRACT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	public String gotoTerminateHistoryPage() {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(contractId);
		Long contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();
    	ctReqDTO.setContractVerId(contractVerId);
	
    	ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
		trBB.getManagedBean().setViewContract(true);
		trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
		trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
		resDTO = contractSO.retrieveTerminationHistoryList(ctReqDTO);
		trBB.getManagedBean().setTerminationList(resDTO.getTerminationHistoryList());
		
		return TERMINATE_HISTORY_PAGE;
	}
	
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}
	
	public void deleteAttachmentAction(int index) {
		getManagedBean().getContractVer().getAgencyAttachements().remove(index);
	}
	
	public void agencyFileUpload(FileUploadEvent event) throws Exception {
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		this.getManagedBean().getContractVer().getAgencyAttachements().add(attachment);
	}
	
	
	public void setContractItemServiceList(
			List<CtContractItem> contractItemServiceList) {
		ContractItemServiceList = contractItemServiceList;
	}
	
	public void retrieveCompanyName(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			this.getManagedBean().setSupplierCompanyName(
					this.getManagedBean().getCtContract().getSmSupplier().getCompanyName());
		}
		else {
			this.getManagedBean().setSupplierCompanyName(
				this.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		}
	}
	
	public void retrieveSupplierAddress(AjaxBehaviorEvent event) {
		if (((SelectBooleanCheckbox)event.getSource()).isSelected()) {
			this.getManagedBean().setSmAddress(retrieveLatestSupplierAddress());
		}
		else {
			this.getManagedBean().setSmAddress(this.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
		}
		getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
	}
	
	public void addDeliveryAddress(List<PmAddressType> addressTypeList){
		try {
			if (addressTypeList != null) {
				List<CtAgencyAddress> ctAgencyAddress = new ArrayList<CtAgencyAddress>();
				List<CtAgencyAddress> agencyAddressList = new ArrayList<CtAgencyAddress>();
				
				ctAgencyAddress = getManagedBean().getSelectedAgency().getCtAgencyAddresses();
				
				for (PmAddressType addressType : addressTypeList) {
					CtAgencyAddress agencyAddress = new CtAgencyAddress();
					agencyAddress.setAddressType(addressType);
					agencyAddress.setCtAgency(getManagedBean().getSelectedAgency());		
					agencyAddressList.add(agencyAddress);
				   
					//Remove if already exist, avoid duplicate
					for(CtAgencyAddress address : ctAgencyAddress) {
						if(address.getAddressType().getAddressTypeId().equals(addressType.getAddressTypeId())) {
							agencyAddressList.remove(agencyAddress);
						}
					}
				}
				ctAgencyAddress.addAll(agencyAddressList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	public void deleteDeliveryAddress(){
		
		try {
			getManagedBean().getSelectedAgency().getCtAgencyAddresses().removeAll(getManagedBean().getSelectedListOfAddress());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}
	
	
	/*Implementation of search method for agency*/
	public void searchAgencyDetails() {
		try { 
			getManagedBean().setCounter(0);  
			agencyDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		}
	}
	
	public IPagingDataModel<PmOrgProfile> getAgencyDataModel() {
		return agencyDataModel;
	}

	public void setAgencytDataModel(IPagingDataModel<PmOrgProfile> agencyDataModel) {
		this.agencyDataModel = agencyDataModel;
	}
	
	public String fileNames(String filePath){
		String fileName = "";
		if(filePath == null || filePath=="")
		  return fileName;
		
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	    return fileName;
	}

	private class AgencyPagingDataModel extends PagingDataModel<PmOrgProfile> {
		private static final long serialVersionUID = 1L;

		public AgencyPagingDataModel() {
			super("tableAgency");
		}
		
		@Override
		public ISearchResultBean<PmOrgProfile> pageChangeListener(ISearchBean<PmOrgProfile> searchBean) {
			
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			try {
				// Filter By - parameterType
				if(getManagedBean().getCounter() == 0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.pmParameterType.parameterType",SearchOperator.OPERATOR_EQUALS.value(),ParameterTypeConst.ORGANISATION_TYPES));
				
				// Filter By - Org Type
				searchBean.getDataFilterBeans().add(new SearchFilterBean("orgTypeCode.parameterCode",SearchOperator.OPERATOR_EQUALS.value(),OrganizationType.DIVISION.value()));
				
				// Filter By - Parent Org Profile Id
				Long ministryProfileId = getManagedBean().getMinistryProfileId();
				if (ministryProfileId == null) {
					ministryProfileId = 0L;
				}
				searchBean.getDataFilterBeans().add(new SearchFilterBean("parentOrgProfile.orgProfileId",SearchOperator.OPERATOR_EQUALS.value(),ministryProfileId));
				
				if(getManagedBean().getPtjCodeName() != null &&
						(getManagedBean().getPtjCodeNameSearchValues() != null || getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))){
					   
					if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Code")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode",SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					
					}else if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Name")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName", SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				
				 IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				 OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				 reqDTO.setOrgProfileSearchBean(searchBean);
				 OrgProfileResDTO resDTO = orgProfileSO.searchOrgProfile(reqDTO);
				 if(resDTO.isSuccessful()) {
					 return resDTO.getOrgProfileResultBean();
				 }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public List<CtItemCeilingQty> getActiveItemCeilingQty() {
		List<CtItemCeilingQty> activeList = new ArrayList<CtItemCeilingQty>();
		if(getManagedBean().getSelectedItem()!=null) {
			for(CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
				if(ctItemCeilingQty.getCtAgency().isChkStatus()) {
					activeList.add(ctItemCeilingQty);
				}
			}
		}
		return activeList;
	}
	
	public void ScheduleDate(){
	
		for (CtSchedule ctSchedules : getManagedBean().getContractVer().getCtSchedules()) {
			   
			   if(ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
			            SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
             
					    String fromdate = dateFormat.format(ctSchedules.getFromDate());
						ctSchedules.setStartMonth(fromdate.substring(3, 5));
						ctSchedules.setStartYear(fromdate.substring(6,fromdate.length()));
					   
					    String todate = dateFormat.format(ctSchedules.getToDate());
					    ctSchedules.setEndMonth(todate.substring(3, 5));
					    ctSchedules.setEndYear(todate.substring(6, todate.length()));
		  	    }
		  }
	 }
	
	public void ProgressiveDate(){
	
		for (CtProgressivePayment ctSchedules : getManagedBean().getContractVer().getCtProgressivePayments()) {
			   
			if(ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
			    SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
               
			    String fromdate = dateFormat.format(ctSchedules.getFromDate());
			    ctSchedules.setStartMonth(fromdate.substring(3, 5));
				ctSchedules.setStartYear(fromdate.substring(6,fromdate.length()));
			   
				String todate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(todate.substring(3, 5));
			    ctSchedules.setEndYear(todate.substring(6, todate.length()));

			}
		}
	}
	
	 public void scheduleProgressiveDate(){
	    	
	    	if(getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
	    		
	    		   if(getManagedBean().getContractVer().getCtSchedules() != null && !getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
					   for(CtSchedule schedule : this.getManagedBean().getContractVer().getCtSchedules()) {
					        if(schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
			                      Date d1 = new Date();
                                  SimpleDateFormat format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
						          String localDate = format.format(d1);
						          String date = localDate.substring(0, 2);
						          String fromdate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						          String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
                                  try {
							              schedule.setFromDate(format.parse(fromdate));
							              schedule.setToDate(format.parse(toDate));
						          } catch (ParseException e) {
							            e.printStackTrace();
						          }
					         }
				       } getManagedBean().getContractVer().setCtProgressivePayments(null);
				  }
	    	  } else {
				           if (getManagedBean().getContractVer().getCtProgressivePayments() != null && !getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				            	for(CtProgressivePayment schedule : getManagedBean().getContractVer().getCtProgressivePayments()) {
						              if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						
						                       Date d1 = new Date();
                                               SimpleDateFormat format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
						                       String localDate = format.format(d1);
						                       String date = localDate.substring(0, 2);
						                       String fromdate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						                       String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						                       try {
							                          schedule.setFromDate(format.parse(fromdate));
							                          schedule.setToDate(format.parse(toDate));
						                      } catch (ParseException e) {
							                          log.debug(e.getMessage());
						                      }
					                 }
					           } getManagedBean().getContractVer().setCtSchedules(null);
				        } 
			    }	
	      }
	
	public void resetAgencyDetails(){
		getManagedBean().setMinistryProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgProfileId());
		getManagedBean().setPtjCodeName(null);		
		getManagedBean().setPtjCodeNameSearchValues(null);
		getManagedBean().setCounter(1);
		agencyDataModel.resetData();
	}
	
	public BigDecimal getPaidAdvancePercentage() {
		if(getManagedBean().getPaidAdvancePaymentAmt().intValue() != 0)
		    paidAdvancePercentage = (getManagedBean().getPaidAdvancePaymentAmt().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(getManagedBean().getAdvancePaymentAmt()), 2, RoundingMode.HALF_UP));
		return paidAdvancePercentage;
	}

	public CtDeductionClause[] getSelectedDeductionClauses() {
		return selectedDeductionClauses;
	}

	public void setSelectedDeductionClauses(
			CtDeductionClause[] selectedDeductionClauses) {
		this.selectedDeductionClauses = selectedDeductionClauses;
	}

	public void setPaidAdvancePercentage(BigDecimal paidAdvancePercentage) {
		this.paidAdvancePercentage = paidAdvancePercentage;
	}

	public BigDecimal getBalanceToPaid() {
		balanceToPaid = (BigDecimal.valueOf(this.getManagedBean().getAdvancePaymentAmt()).subtract(this.getManagedBean().getPaidAdvancePaymentAmt()));
		return balanceToPaid;
	}

	public void setBalanceToPaid(BigDecimal balanceToPaid) {
		this.balanceToPaid = balanceToPaid;
	}

	public BigDecimal getPaidPercentage() {
		paidPercentage = (this.getManagedBean().getPaidInvoiceAmt().multiply(BigDecimal.valueOf(100)).divide(this.getManagedBean().getContractVer().getCtContractValue().getContractAmount(), 2, RoundingMode.HALF_UP));
		return paidPercentage;
	}

	public void setPaidPercentage(BigDecimal paidPercentage) {
		this.paidPercentage = paidPercentage;
	}
	
	public CtDeductionClause[] getChkDeductionClause() {
		return chkDeductionClause;
	}

	public void setChkDeductionClause(CtDeductionClause[] chkDeductionClause) {
		this.chkDeductionClause = chkDeductionClause;
	}

	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
	public boolean isShowZoneDetails() {
		return showZoneDetails;
	}

	public void setShowZoneDetails(boolean showZoneDetails) {
		this.showZoneDetails = showZoneDetails;
	}
	
	public CtSchedule[] getSelectedSchedule() {
		return selectedSchedule;
	}

	public void setSelectedSchedule(CtSchedule[] selectedSchedule) {
		this.selectedSchedule = selectedSchedule;
	}
	
	public CtProgressivePayment[] getSelectedProgressive() {
		return selectedProgressive;
	}

	public void setSelectedProgressive(CtProgressivePayment[] selectedProgressive) {
		this.selectedProgressive = selectedProgressive;
	}

	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
	}

	public PmOrgProfile[] getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	public void setSelectedPmOrgProfile(PmOrgProfile[] selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}
	
	public boolean isValidateScheduleDate() {
			return validateScheduleDate;
	}

    public void setValidateScheduleDate(boolean validateScheduleDate) {
		this.validateScheduleDate = validateScheduleDate;
	}
    
    public List<CtContractItem> getContractItemServiceList() {
		return ContractItemServiceList;
	}
    
    public Date getCurrentDate() {
        return new Date();
    }
    
    public boolean isAddAgencyButtonFlg() {
    	if(getManagedBean().isViewContract()) {
    		return false;
    	} else if(getManagedBean().getContractVer().getCtAgencies().size()>0) {
    		if(getManagedBean().getContractVer().getCtAgencies().get(0).getCtAgencyAddresses().size()>0) {
    			return false;
    		}
    	}
    	return true;
    }
    
    public Date getExactDate(){
    	
    	 SimpleDateFormat dateFormat = null;
    	
    	try{
    	        dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
    	        String expDate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
                int startDay = Integer.valueOf(expDate.substring(0, 2));
		        int startMonth = Integer.valueOf(expDate.substring(3, 5));
		        int startYear = Integer.valueOf(expDate.substring(6, expDate.length())) +1;
		        String toDate = startDay + "/" + startMonth + "/"+ startYear;
		        return (Date)dateFormat.parse(toDate);
    
    	}catch(ParseException ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
    
    public Date chkMimiumDate(boolean maxDate){
    	
   	     SimpleDateFormat dateFormat = null;
   	     String expDate ="";
   	     try {
   	            dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
   	            if(maxDate) 
   	                  expDate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getExpDate());
   	            else 
   	             expDate = dateFormat.format(getManagedBean().getContractVer().getCtContractValue().getEffDate());
   	       
   	            int startDay = Integer.valueOf(expDate.substring(0, 2));
		        int startMonth = Integer.valueOf(expDate.substring(3, 5));
		        int startYear = Integer.valueOf(expDate.substring(6, expDate.length())) +1;
		        String toDate = startDay + "/" + startMonth + "/"+ startYear;
		        return (Date)dateFormat.parse(toDate);
   
      	} catch(ParseException ex) {
   		     ex.printStackTrace();
   	    }
   	return null;
   }
    
    public boolean getConditionalValue(){
    	if(getManagedBean().isViewContract()) {
    		return true;
    	} else if (!getManagedBean().getCtContract().isAgreement() && getManagedBean().getContractVer().getContractPhysicalNo() == null) {
    		if (getManagedBean().getContractVer().getCtContractValue().isConcession())
    			return false;
    		else
    			return true;
    	}
    	else {
    		return false;
    	}
    }
    
    public String getBankAccNoName() {
    	
    	IFinancialOrgSO financialOrgSO;
    	FinancialOrgReqDTO reqDTO = null;
      
    	try {
    		      financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
    	          reqDTO = new FinancialOrgReqDTO();
    	       
    	          if(getManagedBean().getCtContract().getCtFactoring().getPmFinancialBranch() != null) {
    	        	  
    	        		reqDTO.setFinancialBranchId(getManagedBean().getCtContract().getCtFactoring().getPmFinancialBranch().getFinancialBranchId());
    	        		FinancialOrgResDTO resDTO  = financialOrgSO.retriveFinAccDtlByBranchId(reqDTO);
    	        		PmFinancialAcc pmFinancialAcc = resDTO.getPmFinancialAcc();
    	        		getManagedBean().setFinancialAccNo(pmFinancialAcc.getAccountNo());
 	          		    return pmFinancialAcc.getPmPaymentBankOrg().getFinOrgName();

    	          } else if(getManagedBean().getCtContract().getCtFactoring().getFinancialOrg() != null) {
    	            	    reqDTO.setFinancialOrgId(getManagedBean().getCtContract().getCtFactoring().getFinancialOrg().getFinancialOrgId());
    	                    FinancialOrgResDTO resDTO  = financialOrgSO.retriveFinAccDtlByFinOrgId(reqDTO);
    	                    PmFinancialAcc pmFinancialAcc = resDTO.getPmFinancialAcc();
    	                    getManagedBean().setFinancialAccNo(pmFinancialAcc.getAccountNo());
    	          		    return pmFinancialAcc.getPmPaymentBankOrg().getFinOrgName();
    	          } 
    	          
        }catch(Exception ex){
        	
        }
    	return null;
    }
    
	public String preSubmitContract(my.ep.persistence.entity.pm.common.PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContract(getManagedBean().getButtonAction());
	}
	
	public void disableUpdate(){
		getManagedBean().setAutoUpdate(false);
	}
	
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.ministry.contract.maintenance", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		if(isBPKCCA()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.central.contract.maintenance", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	public boolean isBPKCCA() {
	    	if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
				return true;
	    	else
	    		return false;
	}
	
	public void checkAgencyStatus(CtAgency ctAgency){
		// If effective date is after current date, status is still display old status
 	   if (ctAgency.getRecordStatusFlg() == 0) {
	       		if(ctAgency.getEffDate().after(getCurrentDate())) 
	       			ctAgency.setChkStatus(true);
	       		else 
	       			ctAgency.setChkStatus(false);
 	   }
 	   else {
	       		if(ctAgency.getEffDate().after(getCurrentDate())) 
	       			ctAgency.setChkStatus(false);
	       		else 
	       			ctAgency.setChkStatus(true);		        		   
 	   }
	}
	
public void selectOneCheckboxSelect(AjaxBehaviorEvent e){
		
		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

public void removeAddRow(){
	try {
		if(getManagedBean().getContractVer().getCtPerformanceBonds() != null){
			Iterator<CtPerformanceBond> performanceBond = getManagedBean().getContractVer().getCtPerformanceBonds().iterator();
			while(performanceBond.hasNext()){
				CtPerformanceBond  bond = performanceBond.next();
				if(bond.isSelectedCheckBox()){
					performanceBond.remove();
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	} finally{}
}
	
	
	public boolean isContractSharing() {		
		for(CtAgency ctAgency : getManagedBean().getContractVer().getCtAgencies()){
			if(!getManagedBean().getCtContract().getOwnerOrgProfile().getParentOrgProfile().getOrgName()
					.equals(ctAgency.getOrgProfile().getParentOrgProfile().getOrgName())){
				 return true;
			}
		}
		return false;
	}
	
	public void selectAll(){
		    if(getManagedBean().isSelectAll()) {
		    	  for(CtPerformanceBond bonds : getManagedBean().getContractVer().getCtPerformanceBonds()) {
		    		  bonds.setSelectedCheckBox(true);
		    	  }
		    }
	}
	
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME)))
			return true;
		else 
			return false;
	}
	
	public boolean checkAmendmentAllow(VCtContract vCtContract) {
		if(vCtContract.getCtContract().getLatestContractVer()!=null) {
			double contractAmount = vCtContract.getCtContract().getLatestContractVer().getCtContractValue().getContractAmount().doubleValue();
			if(contractAmount<=200000) {
				return false;
			} else if(contractAmount<=500000) {
				if(vCtContract.getCtContract().isAgreement()) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public String gpkiSignAction(String outcome) throws Exception{
		 if(BpmOutcomeConst.BPM_SUBMIT.equals(outcome)
					|| BpmOutcomeConst.BPM_APPROVE.equals(outcome) 
					|| BpmOutcomeConst.BPM_REJECT.equals(outcome)){
			validateCtContractVer(outcome);
			CtContractVer contractVer = getManagedBean().getContractVer();
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(contractVer.getCtContract().getContractNo());
			sb.append(contractVer.getDocType());
			sb.append(contractVer.getCtContractValue().getEffDate());
			sb.append(contractVer.getCtContractValue().getExpDate());
			sb.append(contractVer.getCtContractValue().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		}else if(BpmOutcomeConst.BPM_CANCEL.equals(outcome)){
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		}
		else{
			return this.saveContract(outcome);
		}
	}
	
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	public String getDeliveryAddressType() {
		return AddressType.DELIVERY_ADDRESS.value();
	}
}