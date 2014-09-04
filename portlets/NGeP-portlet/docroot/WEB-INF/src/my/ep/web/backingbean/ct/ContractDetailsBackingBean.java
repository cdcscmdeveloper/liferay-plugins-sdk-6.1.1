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
import java.text.DateFormatSymbols;
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
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
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
	private BigDecimal paidAdvancePercentage;
	private BigDecimal balanceToPaid;
	private BigDecimal paidPercentage;
	private boolean showZoneDetails;
	private boolean validateScheduleDate;
	
	private ContractBackingBean ctBB = null;
	private List<CtContractItem> ContractItemServiceList;
	private CtSchedule[] selectedSchedule;
	private CtAgency[] selectedListOfAgency;
	private CtDeductionClause[] chkDeductionClause;
	private CtProgressivePayment[] selectedProgressive;
	private PmOrgProfile[] selectedPmOrgProfile;
	private CtPerformanceBond[] selectedBondList;
	private CtDeductionClause[] selectedDeductionClauses;
	private IPagingDataModel<VCtContract> contractDataModel = new ContractPagingDataModel();
	private IPagingDataModel<PmOrgProfile> agencyDataModel = new AgencyPagingDataModel();	
	
	public ContractDetailsBackingBean() {
	    if(this.getManagedBean().getAction() == null) {
		  this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
		  this.getManagedBean().setContractVer(new CtContractVer());
	  
	    }if(getManagedBean().getUserMap().size()==0) {
			 initializeApproverList();
	    }
	 
	    initializeRemarks();
	    if(ctBB == null){
		  ctBB = new ContractBackingBean(this.getManagedBean().getFileDirectory());
		  ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
		}
	}
	
	private FacesScopes getFacesScopes(){
	   FacesScopes facesScopes = new FacesScopes();
	   return facesScopes;
	}
	
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<String> source = new ArrayList<String>();
			List<String> target = new ArrayList<String>();
			getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
	}
	
	public void setSelectedTabValue(int tabValue){
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
			reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			
			if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
				reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
			} else { // Central Contract	
				reqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
			}

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
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CONTRACT_MAINT_PAGE);
	}
	
	public void preRenderContract() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, CONTRACT_PAGE);
	}
	
	public void preRenderView() {
		
		try {
			System.out.println("**********preRenderView**************");
			 Calendar cal = Calendar.getInstance();
			log.info("BackingBean: Performance Testing  preRenderView Function: Time taken to start >>>>"+ cal.getTime());
			
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if(taskId == null)
			return;

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
			if(res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME) && res.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)) {
				CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			} else { 
				CTContractDataType dataType = (CTContractDataType) obj;
				ctReqDTO.setContractId(new Long(dataType.getContractId()));
				ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			}
			
		    IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		    ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		    setValidateContractField(resDTO);
		    
		    log.info("BackingBean: Performance Testing  preRenderView Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
			log.info("BackingBean: Performance Testing  preRenderView Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
		    
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		
         } catch(Exception e){
			e.printStackTrace();
	 	 }finally{
	 		
	 	 }
	}
	
	public void setValidateContractField(ContractResDTO resDTO) {
		
		     this.getManagedBean().setContractVer(resDTO.getCtContractVer());
	         this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	    
	        if(this.getManagedBean().getContractVer().getCtAgencies() != null && this.getManagedBean().getContractVer().getCtAgencies().size() > 0 )
		           Collections.sort(this.getManagedBean().getContractVer().getCtAgencies(), new CtAgency());
	  
	        if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0){
		         if(this.getManagedBean().getCtItemList().size() == 0 && this.getManagedBean().getCtItemServiceList().size() == 0) {
		    	     for(CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
				            if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				        	          Collections.sort(item.getCtItemPrices(), new CtItemPrice());
					                  this.getManagedBean().getCtItemServiceList().add(item);
				           }  else if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)){
				        	         Collections.sort(item.getCtItemPrices(), new CtItemPrice());
				        	         if(item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0)
				        	        	  Collections.sort(item.getCtItemCeilingQties(), new CtItemCeilingQty());
		        	                this.getManagedBean().getCtItemList().add(item);
				           }
			         } if(this.getManagedBean().getCtItemList() != null && this.getManagedBean().getCtItemList().size() > 0) 
			               Collections.sort(this.getManagedBean().getCtItemList(), new CtContractItem());
		    	 
		    	      if(this.getManagedBean().getCtItemServiceList() != null && this.getManagedBean().getCtItemServiceList().size() > 0) 
				           Collections.sort(this.getManagedBean().getCtItemServiceList(), new CtContractItem());
	            } 
		   }
		 
		 if(this.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
		      for(CtDeduction deduct : this.getManagedBean().getContractVer().getCtDeductions()){
		           if(deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)){
		    	          this.getManagedBean().setFlgPaymentTracking(true);
		    	          this.getManagedBean().setAdvancePaymentAmt(deduct.getAmount());
		    	          calAdvancePaymentTracking(this.getManagedBean().getCtContract().getContractId());
		           }
		     }
		
		 } if (this.getManagedBean().getContractVer().getCtSchedules().size() > 0) {
		        this.getManagedBean().setChkSchedule(true);
		        SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
			    Calendar cal = Calendar.getInstance();
			    for(CtSchedule ctschedule : this.getManagedBean().getContractVer().getCtSchedules()) {
		    	      cal.setTime(ctschedule.getFromDate());
		    	      ctschedule.setFromTranDate(cal.get(Calendar.MONTH));
		    	      ctschedule.setFromTranYear(dateFormatYear.format(ctschedule.getFromDate()));
		    	      cal.setTime(ctschedule.getToDate());
		    	      ctschedule.setToTranDate(cal.get(Calendar.MONTH));
		    	      ctschedule.setToTranYears(dateFormatYear.format(ctschedule.getToDate()));
		       }
		
		 } if (this.getManagedBean().getContractVer().getCtProgressivePayments().size() > 0){
			     SimpleDateFormat dateFormatYear = new SimpleDateFormat("yyyy");
		         Calendar cal = Calendar.getInstance();
			     for(CtProgressivePayment ctProgressivePayment : this.getManagedBean().getContractVer().getCtProgressivePayments()){
			            cal.setTime(ctProgressivePayment.getFromDate());
			            ctProgressivePayment.setFromProDate(cal.get(Calendar.MONTH));
			            ctProgressivePayment.setFromProYear(dateFormatYear.format(ctProgressivePayment.getFromDate()));
			    	    cal.setTime(ctProgressivePayment.getToDate());
			            ctProgressivePayment.setToProDate(cal.get(Calendar.MONTH));
			            ctProgressivePayment.setToProYears(dateFormatYear.format(ctProgressivePayment.getToDate()));
			      }
		   }
		    
		    if(this.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null){
		    	this.getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
		    	this.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(574L);
		    }
		  
		    if(this.getManagedBean().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
		    	this.getManagedBean().setDirectPurchase(true);
		    	this.getManagedBean().setFlgPaymentTracking(false);
		    }
		    
		    if(this.getManagedBean().getContractVer().getCtContractZones() != null && this.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
		    	     Collections.sort(getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
		    		 retriveZonesByContractVer();
		    }
		  
	    	getManagedBean().setSmAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
	    	getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(getManagedBean().getSmAddress()));
		    renderUpdateCheckbox(this.getManagedBean().getContractVer());
		    this.getManagedBean().setSupplierCompanyName(this.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		    
	        this.getManagedBean().getApproverModel().setSource(getAvailableApproverList());
		    if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			   this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			   this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			} 
	        this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
			if(this.getManagedBean().getCtContract().isFrequencySchedule()){
			    this.getManagedBean().setFrequencySchedule("S");
			}else{
				 this.getManagedBean().setFrequencySchedule("P");
			}
			
			initializeRemarks();
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		    ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
		    ConstructSideTabMenu();
	}
	
public void ConstructSideTabMenu(){
		
		MenuModel fulfilmentMenuModel = new DefaultMenuModel();
		CtTabSideMenus menu = new CtTabSideMenus();
		
		int zoneTabIndex = 0;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		if(this.getManagedBean().getFinalListOfSideMenu() == null)
			   this.getManagedBean().setFinalListOfSideMenu( new ArrayList<String>());
		else
			   this.getManagedBean().getFinalListOfSideMenu().clear();
		
		if(this.getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)){
		    	  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFulfilement);
			      getManagedBean().getFinalListOfSideMenu().addAll(menu.menuAgency);
			      if(getManagedBean().getCtContract().isZonalPricing()) {
		 		          getManagedBean().getFinalListOfSideMenu().addAll(menu.menuZone);
			      }
		 		  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuItem);
		          if (getManagedBean().isDirectPurchase()) {
		        	    if(getManagedBean().getCtContract(). getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
				    	         getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSchedule);
		        	    }
			      } else{
			    	  	  if (getManagedBean().getContractVer().getCtDeductions().size() > 0) {
			    	  		  getManagedBean().getFinalListOfSideMenu().addAll(menu.menuDeduction);
			    	  	  }
		            	  if (getManagedBean().getCtContract().
				        		getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
		            	        getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSchedule);
		            	  }
			       }
	 		          
		           if(getManagedBean().getCtContract().isFactored()) {
	            	      getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFactoring);
	                     
		           } if(getManagedBean().isFlgPaymentTracking()) {
	            	         getManagedBean().getFinalListOfSideMenu().addAll(menu.menuPaymentTracking);
	                } 
		            getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSetting);
	                
		  } if(this.getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){
		               this.getManagedBean().getFinalListOfSideMenu().addAll(menu.menuFulfilement);
		               this.getManagedBean().getFinalListOfSideMenu().addAll(menu.menuItem);
	                   if(this.getManagedBean().getCtContract().isFactored()){
	            	         this.getManagedBean().getFinalListOfSideMenu().addAll(menu.menuPaymentTracking);
		                }
        	          this.getManagedBean().getFinalListOfSideMenu().addAll(menu.menuSetting);   
		      }
		      
	 	    for( String str : this.getManagedBean().getFinalListOfSideMenu()){
	 	    	MenuItem zoneItem = new MenuItem();
				zoneItem.setValue(str);
				zoneItem.setUpdate("@form");
				zoneItem.setActionExpression(FacesScopes
						.createMethodExpression(facesContext,
								"#{contractDetailsBackingBean.fulfilmentTabChangeAction("
										+ zoneTabIndex + ")}", null,
								new Class[] { Integer.class }));
				fulfilmentMenuModel.addMenuItem(zoneItem);

				zoneTabIndex++;
	 	    } this.getManagedBean().setFulfilmentMenuModel(fulfilmentMenuModel);
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
				getManagedBean().getCtContractStatus() == 0 &&
				getManagedBean().getTerminationTypeId() == null) {
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null, MessageUtils.getMessage(MessageConst.WARN_751000,
							MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			}
			else {
				contractDataModel.reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
		}
	}
	
	public String backAction() {
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
		this.getManagedBean().setCtContractStatus(0);
		this.getManagedBean().setTerminationTypeId(null);
	}
	
	public IPagingDataModel<VCtContract> getContractDataModel() {
		return contractDataModel;
	}

	public void setContractDataModel(IPagingDataModel<VCtContract> contractDataModel) {
		this.contractDataModel = contractDataModel;
	}

	private class ContractPagingDataModel extends PagingDataModel<VCtContract> {
		private static final long serialVersionUID = 1L;

		public ContractPagingDataModel() {
			super("contractTbl");
		}
		
		@Override
		public ISearchResultBean<VCtContract> pageChangeListener(ISearchBean<VCtContract> searchBean) {
			IContractSO contractSO = null;
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			Calendar cal = Calendar.getInstance();
			log.info("BackingBean: Performance Testing  Search Function: Time taken to start >>>>"+ cal.getTime());
			try {
				if(getManagedBean().getCtNumber()!=null && getManagedBean().getCtNumber().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("contractNo",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtNumber());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if(getManagedBean().getCtName()!=null && getManagedBean().getCtName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if(getManagedBean().getCtCircularNo()!=null && getManagedBean().getCtCircularNo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularNo",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtCircularNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if(getManagedBean().getCtCircularName()!=null && getManagedBean().getCtCircularName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("circularname",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtCircularName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if(getManagedBean().getCtSupplierId()!=null && getManagedBean().getCtSupplierId().length()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.supplierId",SearchOperator.OPERATOR_EQUALS.value(),
					getManagedBean().getCtSupplierId()));
				}
				if(getManagedBean().getCtSupplierName()!=null && getManagedBean().getCtSupplierName().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("companyName",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtSupplierName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);				
				}
				if(getManagedBean().getCtDateStart()!=null && getManagedBean().getCtDateEnd()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("effDate",SearchOperator.OPERATOR_GREATER_EQUAL.value(), getManagedBean().getCtDateStart()));	
					searchBean.getDataFilterBeans().add(new SearchFilterBean("expDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), getManagedBean().getCtDateEnd()));		
				
				}
				if(getManagedBean().getCtDateStart()!=null && getManagedBean().getCtDateEnd()==null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("effDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getCtDateStart()));					
				}
				if(getManagedBean().getCtDateStart()==null && getManagedBean().getCtDateEnd()!=null) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("expDate",SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getCtDateEnd()));	
				}
				if(getManagedBean().getCtPhysicalNo()!=null && getManagedBean().getCtPhysicalNo().length()>0) {
				   	SearchFilterBean filterBean = new SearchFilterBean("contractPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),
				   									getManagedBean().getCtPhysicalNo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if(getManagedBean().getCtLOANo()!=null && getManagedBean().getCtLOANo().length()>0) {
					SearchFilterBean filterBean = new SearchFilterBean("loaPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),
													getManagedBean().getCtLOANo());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);					
				}
				if(getManagedBean().getCtContractStatus()>0) {
					searchBean.getDataFilterBeans().add(new SearchFilterBean("status",SearchOperator.OPERATOR_EQUALS.value(),
					getManagedBean().getCtContractStatus()));					
				}
				if(getManagedBean().getTerminationTypeId() != null) {
					String parameterCode = retrieveParameterById(getManagedBean().getTerminationTypeId());
					if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("terminate",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
					else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("mutualTerminate",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
					else if(parameterCode.equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)) {
						searchBean.getDataFilterBeans().add(new SearchFilterBean("remedy",SearchOperator.OPERATOR_EQUALS.value(), 1));	
					}
				}
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setvCtContractSearchBean(searchBean);
				ContractResDTO resDTO = contractSO.searchContractDocument(reqDTO);
				getManagedBean().setSelectedIndex(0);
				
				log.info("BackingBean: Performance Testing  Search Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
				log.info("BackingBean: Performance Testing  Search Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
					
				if(resDTO.isSuccessful()) {
					return resDTO.getvCtContractResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
    public String saveContract(String action) throws Exception{
	    IContractSO contractSO = null;
	    Calendar cal = Calendar.getInstance();
    	log.info("BackingBean: Performance Testing  saveContract Function: Time taken to start >>>>"+ cal.getTime());
		try{
			if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				if(getManagedBean().getRemarkList().get(0).getRemark().length()==0) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
				}
			}
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
			
            reqDTO.setToken(token);
            reqDTO.setOutcome(action);
            reqDTO.setTaskId(this.getManagedBean().getTaskId());
            reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
            
            if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
				this.getManagedBean().getContractVer().getCtContractItems().clear();
            }
            this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtItemList());
            this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtItemServiceList());
            
            getZoneObjDetails();
            if(getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
            	convertScheduleDate();
            	convertProgressiveScheduleDate();
            }
            
            reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
            CtContractVer ctContractVer = this.getManagedBean().getContractVer();
            if (ctContractVer.getCtContractValue().isCompanyNameChange()) {
            	ctContractVer.getCtContractValue().setSupplierName(this.getManagedBean().getSupplierCompanyName());
            }
            
            if (ctContractVer.getCtContractValue().isAddressChange()) {
            	ctContractVer.getCtContractValue().setSupplierAddress(this.getManagedBean().getSmAddress());
            }
            reqDTO.setCtContractVer(ctContractVer);
            
            reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
		    reqDTO.setUserMap(getManagedBean().getUserMap());
		    reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		
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
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					setValidateContractField(resDTO);
				}
			  }
		  } finally {
			contractSO = null;
		  }
		log.info("BackingBean: Performance Testing  saveContract Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
		log.info("BackingBean: Performance Testing  saveContract Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
		
		return null;	
	}
    
    public void convertScheduleDate(){
    	try {
    	 if(this.getManagedBean().getContractVer().getCtSchedules().size() > 0){
    		DateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
    		 for(CtSchedule ctschedule : this.getManagedBean().getContractVer().getCtSchedules()){
    			 String fromDate = String.valueOf(ctschedule.getFromTranDate()+1)+"-"+ ctschedule.getFromTranYear();
    			 String toDate = String.valueOf(ctschedule.getToTranDate()+1) +"-"+ctschedule.getToTranYears();
    			 Date date = dateFormat.parse((fromDate));
    			 ctschedule.setFromDate(date);
    			 Date date1 = dateFormat.parse((toDate));
    			 ctschedule.setToDate(date1); 
    		 }
    	}
      }catch (ParseException e) {
			e.printStackTrace();
	  }
    }
    
    public void convertProgressiveScheduleDate(){
    	try {
    	 if(this.getManagedBean().getContractVer().getCtProgressivePayments().size() > 0){
    		DateFormat dateFormat = new SimpleDateFormat("MM-yyyy");
    		 for(CtProgressivePayment ctProgressivePayment : this.getManagedBean().getContractVer().getCtProgressivePayments()){
    			 String fromDate = String.valueOf(ctProgressivePayment.getFromProDate()+1)+"-"+ ctProgressivePayment.getFromProYear();
    			 String toDate = String.valueOf(ctProgressivePayment.getToProDate()+1)+"-"+ ctProgressivePayment.getToProYears();
    			 Date date = dateFormat.parse((fromDate));
    			 ctProgressivePayment.setFromDate(date);
    			 Date date1 = dateFormat.parse((toDate));
    			 ctProgressivePayment.setToDate(date1); 
    		 }
    	}
      }catch (ParseException e) {
			e.printStackTrace();
	  }
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
			   TreeNode stateNode = null;
			   List<PmState> state = new ArrayList<PmState>();
			   if(zone.getCtContractZoneDtls() != null) {
				 for(CtContractZoneDtl zoneDtl : zone.getCtContractZoneDtls()) {
				   if(zoneDtl.getState() != null){
					  if(!state.contains(zoneDtl.getState())){
						  stateNode = new DefaultTreeNode("state",new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getState()) , zoneNode);
					   }state.add(zoneDtl.getState());
				    }
				   if(zoneDtl.getDistrict() != null && !getParameterDesc(getManagedBean().getContractVer().getCtContract().getLocationLevel()).equalsIgnoreCase(ContractConst.ZONE_LOCATION_NAME)){
					   new DefaultTreeNode("district", new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getDistrict()), stateNode);
				   }
				 }
			  }
			}
		 this.getManagedBean().setCtZoneTrees(rootLocationTree);
	} 
	
    public void getZoneObjDetails() {
    	if(this.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
    		for(CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
    			for(TreeNode zonename : this.getManagedBean().getCtZoneTrees().getChildren()){
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
		    items[0] = new SelectItem(null, "- Select One -");
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
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		    OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		      
			for(PmOrgProfile orgProfile : list) 
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());   
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
	       items[0] = new SelectItem(null, "- Select One -");
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
			items[i++] = new SelectItem(0,"- Select One -");
			items[i++] = new SelectItem(ContractConst.STATUS_COMPLETE, ContractConst.CONTRACT_STATUS_COMPLETE);
			items[i++] = new SelectItem(ContractConst.STATUS_INCOMPLETE, ContractConst.CONTRACT_STATUS_INCOMPLETE);
			
		}catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}
		
	public SelectItem[] getMonthList(){
		 SelectItem[] items = new SelectItem[12];
		 try{
		      
			String[] months = new DateFormatSymbols().getMonths();
		    for(int i = 0; i < months.length; i++) {
		        if(i == 12)
		          break;
		        items[i] = new SelectItem(String.valueOf(i),months[i]);
		    }      
		 }catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		 return items;
	 }

	public List<String> getYearList() {
		List<String> yearList = new ArrayList<String>();
		for(int i = 2014; i<= 2070; i++) {
		   if(String.valueOf(i) == null)
		   break;
		   yearList.add(String.valueOf(i));
		}
      return yearList;
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
			if (this.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				this.getManagedBean().setItemPricing(true);

			} else if (this.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
				this.getManagedBean().setItemCeilingQuantity(true);
				refreshAgencyCeilingQuantityList();
			}
		}catch (Exception e) {
			System.out.println(e);
		}
	}
    
  public void  refreshCeilingQuantity(){
	  if (this.getManagedBean().getShowItem() != null &&
			   this.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)){
		     refreshAgencyCeilingQuantityList();
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
		
       this.getManagedBean().setChkSchedule(false);
  	   this.getManagedBean().setChkProgressive(false);
  	   if(this.getManagedBean().getFrequencySchedule().equalsIgnoreCase("S")){
  		   this.getManagedBean().getCtContract().setFrequencySchedule(true);
  	   }else if(this.getManagedBean().getFrequencySchedule().equalsIgnoreCase("P")){
  		 this.getManagedBean().getCtContract().setFrequencySchedule(false);
  	   }
	   
	}
    
    public void addScheduleDetails(){
		CtSchedule ctSchedules = new CtSchedule();
		ctSchedules.setScheduleNo(getManagedBean().getContractVer().getCtSchedules().size()+1);
		if(ctSchedules.getScheduleType() == null)
			ctSchedules.setScheduleType(new PmParameter());
		this.getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
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
		this.getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	public void deleteFrequencyDetails() {
		for (int i = 0; i < selectedProgressive.length; i++) {
			CtProgressivePayment ctProgressivePayment = selectedProgressive[i];
			this.getManagedBean().getContractVer().getCtProgressivePayments().remove(ctProgressivePayment);
		}
		selectedProgressive = null;
	}/*End schedule*/
    
    /*Implementation of Bond Details*/
    public void addBondDetails() {
		CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
	    ctPerformanceBond.setFinancialOrg(new PmFinancialOrg());
	    ctPerformanceBond.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
	    Calendar cal = Calendar.getInstance();
        cal.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
        cal.add(Calendar.YEAR, 1); 
	    ctPerformanceBond.setExpDate(cal.getTime());
		this.getManagedBean().getContractVer().getCtPerformanceBonds().add(ctPerformanceBond);
	}
    
    public void delBondDetails() {
    	for(int i = 0; i < selectedBondList.length; i++) {
			CtPerformanceBond ctPerformanceBond = selectedBondList[i];
			this.getManagedBean().getContractVer().getCtPerformanceBonds().remove(ctPerformanceBond);
		}selectedBondList = null;	
	}
    
	public void addAgencyDetails(){
		List<CtAgency> ctAgencyList = new ArrayList<CtAgency>();
		
		for(int i=0; i<selectedPmOrgProfile.length; i++){
			PmOrgProfile pmOrgProfile = selectedPmOrgProfile[i];
			CtAgency ctAgency = new CtAgency();
			ctAgency.setEffDate(getManagedBean().getContractVer().getCtContractValue().getEffDate());
			ctAgency.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			ctAgency.setCeilingAmount(new BigDecimal(0));
		  	ctAgency.setOrgProfile(pmOrgProfile);
		  	ctAgency.setCtContractVer(this.getManagedBean().getContractVer());
		  	ctAgencyList.add(ctAgency);
		  
		  	//Remove if already exist, avoid duplicate
		  	for(CtAgency agency : this.getManagedBean().getContractVer().getCtAgencies()) {
		  		if(agency.getOrgProfile().getOrgProfileId().equals(selectedPmOrgProfile[i].getOrgProfileId())) {
		  			ctAgencyList.remove(ctAgency);
		  			ctAgency = null;
		  		}
		  	}
		  
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
		if (selectedPmOrgProfile.length>0)
			this.getManagedBean().getContractVer().getCtAgencies().addAll(ctAgencyList);
		selectedPmOrgProfile = null;
	}
	 
	public void deleteAgencyDetails(){
	  for(int i = 0; i < selectedListOfAgency.length; i++) {
		 CtAgency ctAgency = selectedListOfAgency[i];
		 this.getManagedBean().getContractVer().getCtAgencies().remove(ctAgency);
		 if(this.getManagedBean().getCtItemList() != null &&  this.getManagedBean().getCtItemList().size() > 1){
			     for(CtContractItem contractItem: this.getManagedBean().getCtItemList()){
			    	 Iterator<CtItemCeilingQty> ctItemCeilingQtyIterator =  contractItem.getCtItemCeilingQties().iterator();
			    	 while(ctItemCeilingQtyIterator.hasNext()){
							CtItemCeilingQty ctItemCeilingQty = ctItemCeilingQtyIterator.next();
							if(ctItemCeilingQty.getCtAgency().getOrgProfile().getOrgProfileId().equals(ctAgency.getOrgProfile().getOrgProfileId())){
								ctItemCeilingQtyIterator.remove();
							}
						}
			     }	
		   }
	  }selectedListOfAgency = null;	
	}
	
	public void selectedZoneValue(){
	    this.getManagedBean().setShowZoneDetails(true);
	    this.getManagedBean().setCtContractZoneDtls(this.getManagedBean().getSelectedCtZone().getCtContractZoneDtls());
	 }
    
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.APPROVER.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
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
		
	public String getItemOriginTypes(PmParameter pmParameter) throws NamingException{
			
		if(retrieveParameterById(pmParameter.getParameterId()).equalsIgnoreCase("L"))
			this.getManagedBean().setPopupForImportedItems("I");
		else
			this.getManagedBean().setPopupForImportedItems("L");
		 return getParameterDesc(pmParameter);
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
		 
     public void validateDate(CtSchedule ctSchedule){
    	 if(ctSchedule.getFromTranYear() == null || ctSchedule.getToTranYears() == null) {
    		 for(CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {
 			    if(dataList.toString().equalsIgnoreCase(ctSchedule.toString())){
 			        dataList.setValidateScheduleDate(false);
 			    }
 			 }
    	 }
    	 else {    	 
	    	 int startYearValue = Integer.valueOf((String) ctSchedule.getFromTranYear());  
			 int toyearValue = Integer.valueOf((String) ctSchedule.getToTranYears()); 
			 if((ctSchedule.getFromTranDate() > ctSchedule.getToTranDate() && startYearValue >= toyearValue)||
			     (ctSchedule.getFromTranDate() <= ctSchedule.getToTranDate() && startYearValue > toyearValue)){
			     for (CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {
			    	if(dataList.toString().equalsIgnoreCase(ctSchedule.toString())){
			    		dataList.setValidateScheduleDate(true);
			         }
			     }
			  }
			 else{
			     for(CtSchedule dataList : this.getManagedBean().getContractVer().getCtSchedules()) {
				    if(dataList.toString().equalsIgnoreCase(ctSchedule.toString())){
				        dataList.setValidateScheduleDate(false);
				    }
				 }
			 }
    	 }
	}
		 
    public void validateProgressiveDate(CtProgressivePayment  ctProgressivePayment){
    	if(ctProgressivePayment.getFromProYear() == null || ctProgressivePayment.getToProYears() == null) {
    		 for (CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
		    	  if(dataList.toString().equalsIgnoreCase(ctProgressivePayment.toString())){
			        dataList.setValidateProgressiveDate(false);
			    }
			 }
   	 	}
    	else {
			int startYearValue = Integer.valueOf((String) ctProgressivePayment.getFromProYear()); 
			int toyearValue = Integer.valueOf((String) ctProgressivePayment.getToProYears()); 
			if((ctProgressivePayment.getFromProDate() > ctProgressivePayment.getToProDate()  &&  startYearValue >= toyearValue)||
					(ctProgressivePayment.getFromProDate() <= ctProgressivePayment.getToProDate()  && startYearValue > toyearValue)){
			   for (CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
			    	  if(dataList.equals(ctProgressivePayment)){
			    		 dataList.setValidateProgressiveDate(true);
			    	}
			    }
			 }
			else{
			    for(CtProgressivePayment dataList : this.getManagedBean().getContractVer().getCtProgressivePayments()) {
	                 if(dataList.equals(ctProgressivePayment)){
			    	    dataList.setValidateProgressiveDate(false);
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
			
	public void onRowSelect(SelectEvent event) {}/*End*//*End Item Details*/
    public void onEdit(RowEditEvent event){}
	public void onCancel(RowEditEvent event){}/*End Deduction*/
			    
    /*public void userGroupChangeListener() {
				
	PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getContractVer().getPmUserGroup().getUserGroupId());
	List<String> source = new ArrayList<String>(); 
	List<String> target = new ArrayList<String>();  
	for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
		source.add(userGroupUser.getPmUser().getUserName());
	}
	 getManagedBean().getApproverModel().setSource(source);
	 getManagedBean().getApproverModel().setTarget(target);
	}*/
	 
	public String chkContractStatusType(int status){
		if(status==ContractConst.STATUS_COMPLETE) {
			return ContractConst.CONTRACT_STATUS_COMPLETE;
		} else {
			return ContractConst.CONTRACT_STATUS_INCOMPLETE;
		}
	}
	
	public String gotoTerminatePage() {
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		TerminationBackingBean trBB= null;
		try{
			String latestContractVerId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("latestContractVerId");
			Long contractVerId = (!latestContractVerId.isEmpty()) ? Long.parseLong(latestContractVerId) : null;
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
//			if(contractVerId == null) {
				reqDTO.setContractId(contractId);
				contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
//			}			
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVer(reqDTO);
			trBB = new TerminationBackingBean();
			
			if(resDTO.getCtContractVer().getCtTermination()==null) {
				resDTO.getCtContractVer().setCtTermination(new CtTermination());
			    if(resDTO.getCtContractVer().getCtTermination().getDecisionType() == null)
				    resDTO.getCtContractVer().getCtTermination().setDecisionType(new PmParameter());
			}
	    	
			trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
			trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());	
			
		    trBB.getManagedBean().setRemarkList(resDTO.getCtRemarks());
		    trBB.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			trBB.getManagedBean().setViewContract(false);
			trBB.getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
			trBB.getManagedBean().getContractVer().getDocType(), trBB.getManagedBean().getCtContract().getContractNo()));	
			trBB.flush();
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
			String latestContractVerId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("latestContractVerId");
			Long contractVerId = (!latestContractVerId.isEmpty()) ? Long.parseLong(latestContractVerId) : null;
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		    reqDTO.setContractId(contractId);
		    contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVer(reqDTO);
			
			suspBB = new SuspensionBackingBean();
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
			String versionId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId");
			Long contractVerId = (!versionId.isEmpty()) ? Long.parseLong(versionId) : null;
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			String action = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("action");
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
//			if(contractVerId == null) {
				reqDTO.setContractId(contractId);
				contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
//			}
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			if (action.equalsIgnoreCase("update"))
				reqDTO.setUpdateAction(true);
			ContractResDTO resDTO = contractSO.retrieveContractVer(reqDTO);
			amendBB = new AmendmentBackingBean();
			amendBB.getManagedBean().setSelectedTabIndex(0);
			amendBB.setAmendmentContractVer(resDTO);
			amendBB.ConstructTabSideMenu();
		}finally {
			contractSO = null;
			ctBB = null;
		}
		return PROCESS_AMEND_PAGE;
	}
	
	public String gotoViewPage() {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
		ContractReqDTO ctReqDTO = new ContractReqDTO();
    	ctReqDTO.setContractVerId(contractVerId);
	
	    IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
	    ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
	    setValidateContractField(resDTO);  
	    this.getManagedBean().setViewContract(true);
	    this.getManagedBean().setSelectedTabIndex(0);
		return CONTRACT_PAGE;
	}
	
	public String gotoUpdatePage(){
		IContractSO contractSO = null;
		try {
			Calendar cal = Calendar.getInstance();
			log.info("BackingBean: Performance Testing  gotoUpdatePage Function: Time taken to start >>>>"+ cal.getTime());
			String latestContractVerId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("latestContractVerId");
			Long contractVerId = (!latestContractVerId.isEmpty()) ? Long.parseLong(latestContractVerId) : null;
			Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
//			if(contractVerId == null) {
				reqDTO.setContractId(contractId);
				contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
//			}		
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			reqDTO.setUpdateAction(true);
			getManagedBean().setUpdateContract(true);
			
		    ContractResDTO resDTO = contractSO.retrieveContractVer(reqDTO);
		    setValidateContractField(resDTO);
		    
		    getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SUBMIT});		    
	        getManagedBean().setViewContract(false);
	        this.getManagedBean().setSelectedTabIndex(0);
	        log.info("BackingBean: Performance Testing  gotoUpdatePage Function: Time taken to End >>>>"+ Calendar.getInstance().getTime());
			log.info("BackingBean: Performance Testing  gotoUpdatePage Function: Total Time taken (Second) >>>>"+ (Calendar.getInstance().getTime().getTime() - cal.getTime().getTime()) / 1000);
			
			return CONTRACT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	public String gotoTerminateHistoryPage() {
		Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
		
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(contractId);
    	ctReqDTO.setContractVerId(contractVerId);
	
	    IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
	    ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
    	
		trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
		trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
		resDTO = contractSO.retrieveTerminationHistoryList(ctReqDTO);
		trBB.getManagedBean().setTerminationList(resDTO.getTerminationHistoryList());
		
		return TERMINATE_HISTORY_PAGE;
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
	
	public void resetAgencyDetails(){
		getManagedBean().setMinistryProfileId(null);
		getManagedBean().setPtjCodeName(null);		
		getManagedBean().setPtjSearchtype("E");
		getManagedBean().setPtjCodeNameSearchValues(null);
		agencyDataModel.resetData();
	}
	
	public void assignSelAgency(CtAgency agency) {
		getManagedBean().setSelectedAgency(agency);
		RequestContext.getCurrentInstance().execute("deliveryAddressDlg.show()");
	}
	/*Implementation of search method for agency*/
	public void searchAgencyDetails() {
		try { 
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
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
							"orgTypeCode.pmParameterType.parameterType",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterTypeConst.ORGANISATION_TYPES));
				
				// Filter By - Org Type
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
							"orgTypeCode.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							OrganizationType.DIVISION.value()));
				
				// Filter By - Parent Org Profile Id
				Long ministryProfileId = getManagedBean().getMinistryProfileId();
				if (ministryProfileId == null) {
					ministryProfileId = 0L;
				}
				searchBean.getDataFilterBeans().add(new SearchFilterBean(
						"parentOrgProfile.orgProfileId",
						SearchOperator.OPERATOR_EQUALS.value(),
						ministryProfileId));
				
				if(getManagedBean().getPtjCodeName() != null &&
						(getManagedBean().getPtjCodeNameSearchValues() != null || getManagedBean().getPtjCodeNameSearchValues().equalsIgnoreCase(""))){
					   
					if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Code") && getManagedBean().getPtjSearchtype().equalsIgnoreCase("E")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode",
								  						SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
				
					}else if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Code") && getManagedBean().getPtjSearchtype().equalsIgnoreCase("L")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgCode",
								  						SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					
					}else if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Name") && getManagedBean().getPtjSearchtype().equalsIgnoreCase("E")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName",
														SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					
					}else if(getManagedBean().getPtjCodeName().equalsIgnoreCase("Name") && getManagedBean().getPtjSearchtype().equalsIgnoreCase("L")){
						SearchFilterBean filterBean = new SearchFilterBean("pmOrgValidity.orgName",
														SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getPtjCodeNameSearchValues());
						filterBean.setCaseInsensitive(true);
					    searchBean.getDataFilterBeans().add(filterBean);
					}
				}
				
				 IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
				 OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				 reqDTO.setOrgProfileSearchBean(searchBean);
				 OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileList(reqDTO);
				 if(resDTO.isSuccessful()) {
					 return resDTO.getOrgProfileResultBean();
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public BigDecimal getPaidAdvancePercentage() {
		paidAdvancePercentage = (this.getManagedBean().getPaidAdvancePaymentAmt().multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(this.getManagedBean().getAdvancePaymentAmt()), 2, RoundingMode.HALF_UP));
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

	public CtAgency[] getSelectedListOfAgency() {
		return selectedListOfAgency;
	}

	public void setSelectedListOfAgency(CtAgency[] selectedListOfAgency) {
		this.selectedListOfAgency = selectedListOfAgency;
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
}