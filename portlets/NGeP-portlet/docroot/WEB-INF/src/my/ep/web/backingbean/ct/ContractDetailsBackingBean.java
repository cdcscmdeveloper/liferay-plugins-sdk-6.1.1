/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.bank.IChangeBankAccSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.suspension.IContractSuspensionSO;
import my.ep.biz.service.ct.termination.IContractTerminationSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
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
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.VCtContract;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.circular.cc.CentralContractBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import com.liferay.portal.util.PortalUtil;
/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean (name = "contractDetailsBackingBean") 
@ViewScoped
public class ContractDetailsBackingBean extends ContractBaseBackingBean<ContractDetailsManagedBean> {
	
	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml?faces-redirect=true";
	private static final String CONTRACT_PAGE = "/xhtml/ct/fulfilmentdetails/creation/fulfilment.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String CONTRACT_COMMON_PAGE = "/xhtml/ct/viewcontract/fulfilmentTab.xhtml";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml?faces-redirect=true";
	private static final String CENTRAL_CIRCULAR_PAGE = "/xhtml/ct/circular/portal/centralCircularTab.xhtml?faces-redirect=true";
	private static final String CHANGE_BANK_ACCOUNT_PAGE = "/xhtml/ct/supplier/bank/changeBankAccount.xhtml?faces-redirect=true";
	private static final String REQUEST_FACTORING_PAGE = "/xhtml/ct/supplier/factoring/requestOfFactoring.xhtml?faces-redirect=true";
	
	private String editorValue;
	private ContractBackingBean ctBB = null;
	SimpleDateFormat dateFormat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public ContractDetailsBackingBean() {
		/* Initialization of button */
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}
		
		if (getManagedBean().getAction() == null) {
			getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBB.getManagedBean().setContractVer(new CtContractVer());
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	 }
	
	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<PmUser> source = new ArrayList<PmUser>();
			List<PmUser> target = new ArrayList<PmUser>();
			getManagedBean().setApproverModel(new DualListModel<PmUser>(source, target));
		}
	}
	
	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}
	
	/**
	 * The method used for searching the contract.
	 * A warning message will display on screen when searching
	 * with empty search criteria.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void searchContract() throws Exception {
		try { 
			if (StringUtils.isBlank(getManagedBean().getSearchContractNo())
					&& StringUtils.isBlank(getManagedBean().getSearchContractName()) 
					&& StringUtils.isBlank(getManagedBean().getSearchCircularNo()) 
					&& StringUtils.isBlank(getManagedBean().getSearchCircularName()) 
					&& StringUtils.isBlank(getManagedBean().getSearchLoaNo()) 
					&& StringUtils.isBlank(getManagedBean().getSearchSupplierName()) 
					&& getManagedBean().getSearchCtStartDate() == null
					&& getManagedBean().getSearchCtEndDate() == null 
					&& StringUtils.isBlank(getManagedBean().getSearchCtPhysicalNo()) 
					&& StringUtils.isBlank(getManagedBean().getSearchLoaPhysicalNo()) 
					&& getManagedBean().getSearchStatus() == 0 
					&& getManagedBean().getSearchTerminationType() == null 
					&& getManagedBean().getSearchFulfilmentStatus() == null
					&& getManagedBean().getSearchContractStatus() == 0
					&& getManagedBean().getSearchMinistryProfileId() == null
					&& getManagedBean().getSearchPtjProfile() == null) {
					FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, null,
								MessageUtils.getMessage(MessageConst.WARN_755000,
										MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			} else {
				getManagedBean().getContractDataModel().reloadData();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));


		}
	}
	
	/**
	 * The method used to reset the search criteria for contract.
	 */
	public void searchReset() {
		this.getManagedBean().setSearchContractNo(null);
		this.getManagedBean().setSearchContractName(null);	
		this.getManagedBean().setSearchCircularNo(null);	
		this.getManagedBean().setSearchCircularName(null);	
		this.getManagedBean().setSearchLoaNo(null);	
		this.getManagedBean().setSearchSupplierName(null);	
		this.getManagedBean().setSearchCtStartDate(null);	
		this.getManagedBean().setSearchCtEndDate(null);	
		this.getManagedBean().setSearchCtPhysicalNo(null);	
		this.getManagedBean().setSearchLoaPhysicalNo(null);
		this.getManagedBean().setSearchStatus(0);
		this.getManagedBean().setSearchTerminationType(null);
		this.getManagedBean().setSearchFulfilmentStatus(null);
		this.getManagedBean().setSearchContractStatus(0);
		defaultMinistryPtjSearchValue();
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract search screen.
	 */
	public void preRenderSearch() {
		renewSesionObject();
		defaultMinistryPtjSearchValue();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_MAINT_PAGE);
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract details screen.
	 */
	public void preRenderContract() {
		renewSesionObject();
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_PAGE);
	}
	
	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		try {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			
			if (taskId == null) {
				if (getManagedBean().getWarningPercentage() != null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, 
									MessageUtils.getMessage(MessageConst.INFO_745008,
											new String[] {ctBB.getManagedBean().getContractVer().getCtContract().getContractNo(),
												getManagedBean().getWarningPercentage()}, 
											MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
					getManagedBean().setWarningPercentage(null);
				}
				return;
			}			
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
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
			ctReqDTO.setTaskName(res.getTaskName());
			if ((res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME) 
					|| res.getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME)) 
					&& res.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)) {
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
			// If the GST Amount has been keyed in by user in sourcing,0
			// the value must generate into Fulfilment Details and non-editable.
			if(resDTO.getCtContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION) 
					&& ctBB.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null){
				ctBB.getManagedBean().setDisableGstAmount(true);
			}
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, 
					ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			
		}
	}
	
	/**
	 * The method used to set the contract details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	public void setValidateContractField(ContractResDTO resDTO) {
		
		ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
	          
	    // Retrieve Agencies
	    if (ctBB.getManagedBean().getContractVer().getCtAgencies() != null 
	    		&& ctBB.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
	    	ctBB.getManagedBean().setSelectedAgency(null);
	    	//Collections.sort(this.getManagedBean().getContractVer().getCtAgencies(), new CtAgency());		           
	    	sortAgencyList(ctBB.getManagedBean().getContractVer().getCtAgencies());
	    	for (CtAgency ctAgency : ctBB.getManagedBean().getContractVer().getCtAgencies()) {
	    		ctBB.checkAgencyStatus(ctAgency);		        	   
	    	}
	    }
	    if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
		    ctBB.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
					ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
	    }
		// Retrieve Items
	    if (ctBB.getManagedBean().getContractVer().getCtContractItems().size() > 0) {	        	   
	    	ctBB.getManagedBean().setCtItemList(new ArrayList<CtContractItem>());
	    	ctBB.getManagedBean().setCtItemServiceList(new ArrayList<CtContractItem>());
	    	ctBB.getManagedBean().setShowItem(null);
	    	ctBB.getManagedBean().setSelectedItem(null);
	    	ctBB.getManagedBean().setSelectedServiceItem(null);	    	  
 		         
	    	for (CtContractItem item : ctBB.getManagedBean().getContractVer().getCtContractItems()) {
	    		// If effective date is after current date, status is still display old status
	    		if (item.getRecordStatusFlg() == 0) {				       		
	    			if (item.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
	    				item.setChkStatus(true);
	    			} else {
	    				item.setChkStatus(false);
	    			}
	    		} else {
	    			if (item.getEffDate().after(DateUtils.getCurrentDateWithoutTime())) {
	    				item.setChkStatus(false);
	    			} else {
	    				item.setChkStatus(true);
	    			}
	    		}
	        		 
	    		if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
	    			Collections.sort(item.getCtItemPrices(), new CtItemPrice());
	    			ctBB.getManagedBean().getCtItemServiceList().add(item);			           
	    		} else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
	    			Collections.sort(item.getCtItemPrices(), new CtItemPrice());
	    			if (item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0) {
	    				//Collections.sort(item.getCtItemCeilingQties(), new CtItemCeilingQty());
	    				sortItemCeilingQtyList(item.getCtItemCeilingQties());
	    				for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {			        	        	    	   
	    					ctBB.checkAgencyStatus(ceilingQty.getCtAgency());		        	   
	    				}
	    			}
	    			ctBB.getManagedBean().getCtItemList().add(item);
	    		}
	    	}
	    	
	    	if (ctBB.getManagedBean().getCtItemList() != null && ctBB.getManagedBean().getCtItemList().size() > 0) {
	    		Collections.sort(ctBB.getManagedBean().getCtItemList(), new CtContractItem());
	    	}
	    	if (ctBB.getManagedBean().getCtItemServiceList() != null && ctBB.getManagedBean().getCtItemServiceList().size() > 0) {
	    		Collections.sort(ctBB.getManagedBean().getCtItemServiceList(), new CtContractItem());
	    	}
	    }
		// Retrieve Deduction Clauses
	    if (ctBB.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
	    	ctBB.getManagedBean().setSelectedDeduction(null);
	    	for (CtDeduction deduct : ctBB.getManagedBean().getContractVer().getCtDeductions()) {
	    		if (deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)) {
	    			ctBB.getManagedBean().setFlgPaymentTracking(true);
	    			ctBB.getManagedBean().setAdvancePaymentAmt(deduct.getAmount());	    			
	    		}
	    	}
	    }
	    //Retrieve Payment Tracking	    
	    calPaymentTracking();
	    
	    // Retrieve Schedules
	    if (ctBB.getManagedBean().getContractVer().getCtSchedules() != null 
	    		&& !ctBB.getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
	    	ctBB.getManagedBean().setChkSchedule(true);
	    	Collections.sort(ctBB.getManagedBean().getContractVer().getCtSchedules(), new CtSchedule());
	    	for (CtSchedule ctSchedule : ctBB.getManagedBean().getContractVer().getCtSchedules()) {
	    		if (ctSchedule.isUseInFulfilment()) {
	    			ctBB.getManagedBean().setDisableScheduleRadio(true);
	    			break;
	    		}
	    	}
	    	scheduleDate();
	    }
	    if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() != null 
	    		&& !ctBB.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
	    	Collections.sort(ctBB.getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment());
	    	for (CtProgressivePayment ctProgressivePayment : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {
	    		if (ctProgressivePayment.isUseInFulfilment()) {
	    			ctBB.getManagedBean().setDisableScheduleRadio(true);
	    			break;
	    		}
			}
	    	progressiveDate();
	    }
	    // Retrieve Delivery Terms
	    if (ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null) {
	    	ctBB.getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
	    	PmParameter pmParameter = getParameterByTypeAndCode(
	    			ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS);
	    	ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(pmParameter.getParameterId());
	    }
	    if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
	    		ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			 ctBB.getManagedBean().setDirectPurchase(true);
			 ctBB.getManagedBean().setFlgPaymentTracking(false);
		} else {
				ctBB.getManagedBean().setDirectPurchase(false);
		}
	    
	    // Retrieve Supplier Information
	   	if (!ctBB.getManagedBean().isViewContract()
				|| (ctBB.getManagedBean().isViewContract() && this.isContractAdmin())) {
			getManagedBean().getApproverModel().setSource(
					getAvailableApproverList(ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode(),
							getManagedBean().getUserMap()));
			if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
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
		}
	 	getManagedBean().setRemarkList(resDTO.getCtRemarks());
		getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		
		// Get performance bond rule amount
				IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
				RuleReqDTO ruleReqDTO = new RuleReqDTO();
				ruleReqDTO.setRuleType(RuleTypeConst.BOND);
				RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
				if (roleResDTO.isSuccessful() && roleResDTO.getRuleValueList() != null && roleResDTO.getRuleValueList().size() > 0) {
					ctBB.getManagedBean().setPerformanceBondRuleAmt(Double.parseDouble(roleResDTO.getRuleValueList().get(0).getRuleValue()));
				}
				initializeRemarks();
				getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
						ModuleCodeConst.CONTRACT_MANAGEMENT, ctBB.getManagedBean().getContractVer().getDocType(), ctBB.getManagedBean().getContractVer().getCtContract().getContractNo()));
			
			
		 ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
		// Retrieve Zones
		    if (ctBB.getManagedBean().getContractVer().getCtContractZones() != null 
		    		&& ctBB.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
		    	Collections.sort(ctBB.getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
		    	ctBB.retriveZonesByContractVer();
		    }
		 //ctBB.getManagedBean().setSmAddress(getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
		 ctBB.getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress()));
		 renderUpdateCheckbox(ctBB.getManagedBean().getContractVer());
		 ctBB.getManagedBean().setSupplierCompanyName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		 ctBB.getManagedBean().setSupplierBranchName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
		 ctBB.retrieveBranchInfo();
		if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
			ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
		} else {
			ctBB.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
		}
		if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
			ctBB.getManagedBean().setDisableScheduleRadio(true);
			ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
		} else if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules()!= null  && !ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules().isEmpty()) {
			ctBB.getManagedBean().setDisableScheduleRadio(true);
			ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
		}  else if (ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments() != null && !ctBB.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments().isEmpty()) {
			ctBB.getManagedBean().setDisableScheduleRadio(true);
			ctBB.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
		}
		retrieveFulfilmentDetails();
		
		if(getManagedBean().isViewContract()){
			ctBB.getManagedBean().setViewContract(true);
		}
		ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
		ctBB.initializeRemarkAndAttachement(resDTO.getCtContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	}
	
	/**
	 * The method used to save the contract.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this contract
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
    public String saveContract(String action) throws Exception {
    	IContractSO contractSO = null;
    	
    	try {
    		getZoneObjDetails();
    		validateCtContractVer(action);
    		ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			   
			if (ctBB.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
				ctBB.getManagedBean().getContractVer().getCtContractItems().clear();
			}
			ctBB.getManagedBean().getContractVer().getCtContractItems().addAll(ctBB.getManagedBean().getCtItemList());
         	ctBB.getManagedBean().getContractVer().getCtContractItems().addAll(ctBB.getManagedBean().getCtItemServiceList());
              
         	if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
         		scheduleProgressiveDate();
         	}
         	reqDTO.setCtContract(ctBB.getManagedBean().getContractVer().getCtContract());
         	CtContractVer ctContractVer = ctBB.getManagedBean().getContractVer();
         	
         	if (ctContractVer.getCtContractValue().isCompanyNameChange()) {
         		ctContractVer.getCtContractValue().setSupplierName(ctBB.getManagedBean().getSupplierCompanyName());
         		ctContractVer.getCtContractValue().setSupplierBranch(ctBB.getManagedBean().getSupplierBranchName());
         	}
         	if (ctContractVer.getCtContractValue().isAddressChange()) {
         		ctContractVer.getCtContractValue().setSupplierAddress(ctBB.getManagedBean().getSmAddress());
         	}         	
            reqDTO.setCtContractVer(ctContractVer);
            reqDTO.setAgreementContractVer(ctBB.getManagedBean().getAgreementContractVer());
            reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
	        reqDTO.setAttachments(getManagedBean().getAttachmentList());
	        reqDTO.setUserMap(getManagedBean().getUserMap());
	        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		    reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
			
		    if (this.getManagedBean().getPmDigiSign() != null) {
		    	reqDTO.setPmDigiSign(this.getManagedBean().getPmDigiSign());
		    }
		    contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
		    ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
	
		    if (resDTO.isSuccessful()) {
		    	log.info("Record saved successfully");
		    	String messageId = MessageConst.INFO_700005;
		    	if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
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
		    	FacesContext.getCurrentInstance().addMessage(null,
		    			new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
		    					new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ctBB.getManagedBean().getContractVer().getDocType()), 
									ctBB.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		    	
		    	if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
		    			|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) { 				  
		    		renewSesionObject();
		    		return CommonUIConst.ACKNOWLEDGE_PAGE;
		    	} else {				    	
				 	ContractReqDTO ctReqDTO = new ContractReqDTO();
				 	ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
				 	ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
				    	
					ContractResDTO ctResDTO = contractSO.retrieveContractVerDtls(ctReqDTO);
					setValidateContractField(ctResDTO);
				}
		    }
    	} finally {
    		contractSO = null;
    	}
    	return null;	
	}
    
    /**
	 * The method used for validating the contract information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this contract. The validation 
	 * 					will invoke base on the action
	 */
    public void validateCtContractVer(String action) throws EpException {
    	CtContractVer contractVer = ctBB.getManagedBean().getContractVer();
    	getManagedBean().setAutoUpdate(true);
		// Validation for remark
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)
				|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
 			String agencyRemark = ctBB.getManagedBean().getContractVer().getAgencyRemarks().get(0).getRemark();
 			String zoneRemark = ctBB.getManagedBean().getContractVer().getZoneRemarks().get(0).getRemark();
 			String itemRemark = ctBB.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
 			String deductionRemark = ctBB.getManagedBean().getContractVer().getDeductionRemarks().get(0).getRemark();
 			String scheduleRemark = ctBB.getManagedBean().getContractVer().getScheduleRemarks().get(0).getRemark();
 			String settingRemark = ctBB.getManagedBean().getContractVer().getSettingRemarks().get(0).getRemark();
 			if ((remark == null || remark.length() == 0) 
 					&& (agencyRemark == null || agencyRemark.length() == 0) 
 					&& (zoneRemark == null || zoneRemark.length() == 0)
 					&& (itemRemark == null || itemRemark.length() == 0) 
 					&& (deductionRemark == null || deductionRemark.length() == 0)
 					&& (scheduleRemark == null || scheduleRemark.length() == 0) 
 					&& (settingRemark == null || settingRemark.length() == 0)) {
 				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
 			}
 		}
		// Validation for zone
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
	    	getZoneObjDetails();
			if (contractVer.getCtContractZones() != null) {
				for (CtContractZone contractZone : contractVer.getCtContractZones()) {
					if (contractZone.getDeliveryTerm() == 0) {
						throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
					}
				}
			}
		}
		log.info(">>>>>>>>> null exception checking contractVer>>>>>>>>" +contractVer);
		if(contractVer!=null) {
			log.info(">>>>>>>>> null exception checking>>>>>>>>" +contractVer.getDocType());
		}
		if (contractVer.getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_CREATION) 
				|| contractVer.getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
			// Validation for agency
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY) 
					&& contractVer.getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
				throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
			}
			// Validation for performance bond
			if (ctBB.getManagedBean().getCtContractAmount().isBondReq() && !ctBB.getManagedBean().isDirectPurchase()) {
				//In CT Update(bond required): if previously the bond already defined, number of performance bond must be > 0 while submission; 
				//In CT Update(bond required): if previously no bond defined, system allow submission of FD Update even the number of performance bond entered = 0.
				if (contractVer.getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
					if (ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {
						for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractAmounts()) {
				    		if (getManagedBean().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
				    				&& getManagedBean().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()) {
				    			if (contractAmt.getCtPerformanceBonds().size() > 0 
				    					&& (ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds() == null 
				    					|| ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().isEmpty())) {
				    				throw new EpException(MessageConst.ERROR_100022, 
											new String[]{ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				    					
				    			}
				    		}
				    	} 
					}
				}				
				if (ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null && ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().size() > 0) {
					double bondAmount = 0.0;
					for (CtPerformanceBond performanceBond : ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
						bondAmount = bondAmount + performanceBond.getBondAmount();
						if (performanceBond.getRefNo() == null || performanceBond.getRefNo().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.no",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}						
						if (performanceBond.getFinancialOrg() == null || performanceBond.getFinancialOrg().getFinancialOrgId() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.financial.org", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}						
						if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
							if (performanceBond.getBondAmount() == 0) {
								throw new EpException(MessageConst.ERROR_100033,
										new String[]{ResourceBundleUtils.getMessage("lbl.ct.pb.amount",
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							}
						}						
						if (performanceBond.getFileName() == null || performanceBond.getFileName().isEmpty()) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.letter",
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} 
					}				 
					if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						double bondAmendAmount = 0.0;
						DecimalFormat df = new DecimalFormat("#.00");
						
						// Retrieve amendment list
						if (ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {
							IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
									ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
							ContractAmendmentResDTO resAmendDto = amendSO.getCtAmendmentByContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());

							if (resAmendDto.getAmendmentList() != null && !resAmendDto.getAmendmentList().isEmpty()) {
								for (CtAmendment ctAmendment : resAmendDto.getAmendmentList()){
									if (getManagedBean().getAsAtDate().getTime() >= ctAmendment.getAmendmentEffDate().getTime() 
						    				&& getManagedBean().getAsAtDate().getTime() <= ctAmendment.getCtContractVer().getCtContractValue().getExpDate().getTime()){
						    			if (ctAmendment.getAmendmentLoa() != null && ctAmendment.getAmendmentLoa().isBondReq()) {
											bondAmendAmount = bondAmendAmount + ctAmendment.getAmendmentLoa().getBondAmt().doubleValue();
										}				
						    			break;
						    		}						    		
						    	}
							}
						}						
						
						if (contractVer.getCtContract().getLoa().getBondAmt() == null 
							&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
									ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							throw new EpException(MessageConst.ERROR_750021);
				
						} else if (contractVer.getCtContract().getLoa().getBondAmt() != null 
							&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
									ParameterConst.CONTRACT_TYPE_MINISTRY)) {						
							bondAmount = Double.valueOf(df.format(bondAmount));
							double oriBondAmount = Double.valueOf(df.format(contractVer.getCtContract().getLoa().getBondAmt().doubleValue()));
							bondAmendAmount = Double.valueOf(df.format(bondAmendAmount));
						
							int retval = Double.compare(bondAmount , oriBondAmount);
							if (bondAmendAmount != 0) {
								retval = Double.compare(bondAmount , bondAmendAmount);
							}
							if (retval >0) {
								throw new EpException(MessageConst.ERROR_750015);
							}
							if (retval < 0) {
								throw new EpException(MessageConst.ERROR_750017);  
							}	
						}
					}
				}	
			}
			// Validation for schedule
			if(ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
				if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					ctBB.getManagedBean().getContractVer().setCtProgressivePayments(null);
				} else {
					ctBB.getManagedBean().getContractVer().setCtSchedules(null);  	
				}
				List<String> schedulePhaseNoList = new ArrayList<String>();
				if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					if (contractVer.getCtSchedules() == null || contractVer.getCtSchedules().size() < 2 
							&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
						throw new EpException(MessageConst.ERROR_750022);
			     	}
					for (CtSchedule schedules : contractVer.getCtSchedules()) {
						if (schedules.getStartMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedules.getEndMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			        	}
						if (schedules.getScheduleNo() == null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {
							throw new EpException(MessageConst.ERROR_100046, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(schedules.getScheduleNo().toString());
						}
						if (schedules.getScheduleType() == null || schedules.getScheduleType().getParameterId() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.type", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
					}     	
				} else if (contractVer.getCtProgressivePayments() == null || contractVer.getCtProgressivePayments().size() < 2 
						&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()
						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
					throw new EpException(MessageConst.ERROR_750022);
				}
				if (contractVer.getCtProgressivePayments() != null && !contractVer.getCtProgressivePayments().isEmpty() 
						&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					float percentage = 0.0f;
					for (CtProgressivePayment progressivePayment : contractVer.getCtProgressivePayments()) {
						percentage = percentage + progressivePayment.getPercentage();
						if (progressivePayment.getPhaseNo()== null) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.phase", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {
		           			throw new EpException(MessageConst.ERROR_100046, 
		           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.phase", 
		           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						} else {
							schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
						}
						if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.description", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getStartMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
						if (progressivePayment.getEndMonth() == null) {
							throw new EpException(MessageConst.ERROR_100022, 
									new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
											+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}     	
					}
					int value = Float.compare(percentage,100);
					if (value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
							|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
						throw new EpException(MessageConst.ERROR_100022, 
	           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.percentage.progressive", 
	           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			// Validation for AP178 agency
			if (contractVer.getCtAgencies().size()>0) {
				double ceilingAmt = 0.0;
				DecimalFormat df = new DecimalFormat("#.00");
				for (CtAgency ctAgency : contractVer.getCtAgencies()) {
					ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
					if (ctAgency.isAp178() == true && (ctAgency.getFileName() == null  || ctAgency.getFileName().equalsIgnoreCase(""))) {
						throw new EpException(MessageConst.ERROR_750008); 
						// Approval document is mandatory for AP178 agency. Please upload the document.
					}
					
					if (ctAgency.isAp178() == true && (ctAgency.getRefNo() == null  || ctAgency.getRefNo().equalsIgnoreCase(""))) {
						throw new EpException(MessageConst.ERROR_100022, 
	           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.file.ref.no", 
	           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
				ceilingAmt = Double.valueOf(df.format(ceilingAmt));
				
				double latestContractAmount = 0.0;
				for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContractAmounts()){
		    		if (ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
		    				&& ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
		    			latestContractAmount = contractAmt.getContractAmount().doubleValue();
		    			latestContractAmount = Double.valueOf(df.format(latestContractAmount));
		    			break;
		    		}
		    	}
				if (ceilingAmt >  latestContractAmount) {
					CommonUtils commonUtils = new CommonUtils();
					DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
							CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
					throw new EpException(MessageConst.ERROR_750007, 
							new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
				}
			}			
			// Validation for ceiling quantity
			if (contractVer.getCtContractItems() != null) {
				for (CtContractItem item : contractVer.getCtContractItems()) {
					double totalCeilingQuantity = 0.0;
					if (item.getCtItemMaxQties().size() != 0) {						
						for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
							totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
						}
						if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
							throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
						}
					}
				}
			}
		}
		
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		
		if (ctBB.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& (ctBB.getManagedBean().getContractVer().getCtContractValue().getAp201FileName() == null 
				||ctBB.getManagedBean().getContractVer().getCtContractValue().getAp201FileName().equalsIgnoreCase("")) 
				&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			throw new EpException(MessageConst.ERROR_100022, 
   					new String[] {ResourceBundleUtils.getMessage("lbl.ct.attachment.ap201", 
   							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
	}
    
    /**
	 * The method used to authenticate user during submit the contract.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save contract method once authentication success.
	 * 
	 * @param  outcome  the user action on this contract
	 * @return result of the saveContract process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
    public String gpkiSignAction(String outcome) throws Exception {
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
			validateCtContractVer(outcome);
			CtContractVer contractVer = ctBB.getManagedBean().getContractVer();
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(contractVer.getCtContract().getContractNo());
			sb.append(contractVer.getDocType());
			sb.append(contractVer.getCtContractValue().getEffDate());
			sb.append(contractVer.getCtContractValue().getExpDate());
			sb.append(ctBB.getManagedBean().getCtContractAmount().getContractAmount());
			sb.append(outcome);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_CANCEL.equalsIgnoreCase(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		} else {
			return this.saveContract(outcome);
		}
	}
	
    /**
	 * The method used to set value for security check
	 * before save the contract.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveContract process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
    public String preSubmitContract(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContract(getManagedBean().getButtonAction());
	}
    
    /**
	 * The method used for controlling the tab menu
	 * of central contract dynamically.
	 * 
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
    public void setTabCentralValue(int currentTab, String outputTab) {		
		getManagedBean().setItemIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setSettingIndex(11);		
		getManagedBean().setPaymentTracking(11);
		
		if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
			getManagedBean().setSelectedTabIndex(0);
	
		} else if (!ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
			getManagedBean().setSelectedTabIndex(1);
			getManagedBean().setItemIndex(1);
	  
		}else if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
			getManagedBean().setSelectedTabIndex(1);
     	
		}else if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
			getManagedBean().setSelectedTabIndex(2);
			getManagedBean().setItemIndex(2);
     
		} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() 
				&& checkFactoringExpiryDate() && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
			 
			   if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				  getManagedBean().setSelectedTabIndex(3);
  	    	
			  } else{
  	    		  getManagedBean().setSelectedTabIndex(2);
  	     	  }
  	  		 getManagedBean().setFactoring( getManagedBean().getSelectedTabIndex());
      
		} else if (ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt() 
				&& outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
			
			    if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				      if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()){
				             getManagedBean().setSelectedTabIndex(4);
				      }else {
					        getManagedBean().setSelectedTabIndex(3);
				     }
  	    	     } else if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()){
  	    		        getManagedBean().setSelectedTabIndex(3);
  	    	     } else {
  	    			 getManagedBean().setSelectedTabIndex(2);
  	    	     }
  	     	   getManagedBean().setPaymentTracking( getManagedBean().getSelectedTabIndex());
    
		}else if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
			  
			   if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {  
				     if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
					       if(ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()){
					             getManagedBean().setSelectedTabIndex(5);
					       }else {
						       getManagedBean().setSelectedTabIndex(4);
					       }
					} else if(ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()){
					     getManagedBean().setSelectedTabIndex(4);
					 } else {
						 getManagedBean().setSelectedTabIndex(3);
					 }
				
			   } else if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
       	  		       if(ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()){
       	  			          getManagedBean().setSelectedTabIndex(4);
       	  		    
       	  		       }else {
      	  			            getManagedBean().setSelectedTabIndex(3);
       	  		       }
       	  	
		       } else if(ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()) {
       	  			         getManagedBean().setSelectedTabIndex(3);
       	  	   
		       } else {
       	  			 getManagedBean().setSelectedTabIndex(2);
       	  	   }
			getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
		  }
		} 
	
    /**
	 * The method used for controlling the tab menu
	 * of ministry contract dynamically.
	 * 
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
	public void setTabValue(int currentTab, String outputTab) {	 
		getManagedBean().setItemIndex(11);
		getManagedBean().setDeductionIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setScheduleIndex(11);
		getManagedBean().setPaymentTracking(11);
		getManagedBean().setSettingIndex(11);
		
	  	if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
			getManagedBean().setSelectedTabIndex(0);
	    
		}
	  	if(outputTab.equalsIgnoreCase(ContractConst.AGENCY_DETAIL)) {
	  		getManagedBean().setSelectedTabIndex(1);
	  	}
	  	if (!ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
	  		getManagedBean().setSelectedTabIndex(2);
	  		getManagedBean().setItemIndex(2);
	  	}
	  	if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
	  		getManagedBean().setSelectedTabIndex(2);
	  	}
	  	if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing() && outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
	  		getManagedBean().setSelectedTabIndex(3);
	  		getManagedBean().setItemIndex(3);
		}
	  	if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL) && !ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  		if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  		} else {
	  			getManagedBean().setSelectedTabIndex(3);
	      	}
	  		getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
	  	} 
	  	if (outputTab.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL)&& ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  		if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {  
	  			if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(4);
	  			}
	  		} else {
	  			if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(4);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(3);
	  			}
	  		}
	  		getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()
	  			&& outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
	  		if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	        	} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	        			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	        		getManagedBean().setSelectedTabIndex(5);	   
	        	}
	  		} else if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);	   
	  			}
	  		} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(4);
	  		} else {
	  			getManagedBean().setSelectedTabIndex(3);
	       	}
	        getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
	  		if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(6);
	  					if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  						getManagedBean().setSelectedTabIndex(7);
	  					}
	  				} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	  			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);
    	    	 	if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
    	    	 		getManagedBean().setSelectedTabIndex(6);
    	    	 	}
	  			} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(5);	   
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(6);
	  				}
	        	} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	        		getManagedBean().setSelectedTabIndex(5);
	        	}
	  		} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(4);
	  			if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	        } else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
   	     		getManagedBean().setSelectedTabIndex(4);
			} else {
				getManagedBean().setSelectedTabIndex(3);
			}
	  		getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
	  	}
	  	if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
	  		if (ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()) { 
	  			getManagedBean().setSelectedTabIndex(5);
	  			if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  				getManagedBean().setSelectedTabIndex(6);
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  						ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  					getManagedBean().setSelectedTabIndex(7);
	  					if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  						getManagedBean().setSelectedTabIndex(8);
	  					} 
	  				} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(8);
	  				} else {
	  					getManagedBean().setSelectedTabIndex(7);
	  				}
	  			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(6);
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(7);
	  				}
	  			} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(6);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (!ctBB.getManagedBean().getContractVer().getCtDeductions().isEmpty()) {
	  			getManagedBean().setSelectedTabIndex(5);
	  			if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  					ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  				getManagedBean().setSelectedTabIndex(6);	   
	  				if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  					getManagedBean().setSelectedTabIndex(7);
	  				} 
	  			} else if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(6);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
	  				ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	  			getManagedBean().setSelectedTabIndex(5);
	  			if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  				getManagedBean().setSelectedTabIndex(6);
	  			} else {
	  				getManagedBean().setSelectedTabIndex(5);
	  			}
	  		} else if (ctBB.getManagedBean().getContractVer().getCtContract().isFactored() && checkFactoringExpiryDate()) {
	  			getManagedBean().setSelectedTabIndex(5);
	
	  		} else {
	  			getManagedBean().setSelectedTabIndex(4);
	  		}
	  		getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
	  	}
	}
	
	/**
     * The method used to check the accessibility of factoring tab menu.
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
     */
	public boolean checkFactoringExpiryDate() {
    	if (ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring() != null 
    			&& ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus() != null
    			&& ctBB.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()) {
    		return true;
    	}
    	return false;
    }
    
	/**
	 * The method used to navigate to contract termination screen
	 * for termination purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoTerminatePage(Long contractId ) {
		IContractSO contractSO = null;
		ContractResDTO resDTO = null;
		ContractReqDTO reqDTO = null;
		IContractTerminationSO contractTerminationSO;
		TerminationBackingBean trBB = null;
		
		try {
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);
			contractTerminationSO = (IContractTerminationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractTerminationSO.class);
			
			resDTO = contractTerminationSO.retrieveTerminationContractVerDtls(reqDTO);
			trBB = new TerminationBackingBean();
			trBB.getManagedBean().setViewContract(false);
			trBB.getManagedBean().setExternalPageUrl(null);
			trBB.setTerminationContractVer(resDTO);  
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			contractSO = null;
			contractTerminationSO = null;
			resDTO = null;
			reqDTO = null;
			trBB= null;
		}
		return PROCESS_TERMINATE_PAGE;
	} 
	
	/**
	 * The method used to navigate to contract suspension screen
	 * for suspension purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoSuspendPage(Long contractId) {
		
		 long startTime1 = System.currentTimeMillis();
		  log.info("Start Time >>>>>>>>>>>>>>>>>>>>" +startTime1);
		  
		IContractSO contractSO = null;
		IContractSuspensionSO contractSuspensionSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		SuspensionBackingBean suspBB = null;
		
		try {
			//Long contractId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractId"));
			
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		    reqDTO.setContractId(contractId);
		    Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			reqDTO.setUpdateAction(true);
			contractSuspensionSO = (IContractSuspensionSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSuspensionSO.class);
			resDTO = contractSuspensionSO.retrieveSuspensionContractVerDtls(reqDTO);
			
			suspBB = new SuspensionBackingBean();
			suspBB.getManagedBean().setViewOnly(false);
			suspBB.setSuspensionContractVer(resDTO);
			long endTime1 = System.currentTimeMillis();
			log.info("End Time1 >>>>>>>>>>>>>>>>>>>>" +endTime1);
		    log.info("Total Time1 >>>>>>>>>>>>>>>>>>>>" +(endTime1 - startTime1));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			contractSO = null;
			contractSuspensionSO = null;
			resDTO = null;
			reqDTO = null;
			suspBB = null;
		}
		return PROCESS_SUSPEND_PAGE;
	}
	
	/**
	 * The method used to navigate to contract amendment screen
	 * for amendment purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoAmendPage(Long contractId, String action) {
		IContractAmendmentSO contractAmendmentSO = null;
		IContractSO contractSO;
		AmendmentBackingBean amendBB = null;
		long startTime = System.currentTimeMillis();
		log.info("Start Time >>>>>>>>>>>>>>>>>>>>" +startTime);
		
		try {
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			if (action.equalsIgnoreCase("update")) {
				reqDTO.setUpdateAction(true);
			}
			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractResDTO resDTO = contractAmendmentSO.retrieveContractAmendVerDtls(reqDTO);
			amendBB = new AmendmentBackingBean();
			amendBB.getManagedBean().setSelectedTabIndex(0);
			amendBB.getManagedBean().setShowItem(null);
			amendBB.getManagedBean().setSelectedItem(null);
			amendBB.getManagedBean().setSelectedServiceItem(null);
			amendBB.getManagedBean().setDisableGstRadio(true);
			amendBB.setAmendmentContractVer(resDTO);
			long endTime1 = System.currentTimeMillis();
			log.info("End Time >>>>>>>>>>>>>>>>>>>>" +endTime1);
			log.info("Total Time >>>>>>>>>>>>>>>>>>>>" +(endTime1 - startTime));

		} finally {
			contractSO = null;
			ctBB = null;
		}
		return PROCESS_AMEND_PAGE;
	}
	
	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoViewPage(Long contractId, Long contractVerId) {
		
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractId(contractId);
		CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		if (latestContractVer != null) {
			reqDTO.setContractVerId(latestContractVer.getContractVerId());
		} else {
			reqDTO.setContractVerId(contractVerId);
		}
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		reqDTO.setViewOnly(true);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
	    ctBB.getManagedBean().setViewContract(true);
	    setValidateContractField(resDTO);
	    getManagedBean().setSelectedTabIndex(0);
	    getManagedBean().setExternalPageUrl(null);
	    getManagedBean().setLoaContractVerId(resDTO.getCtContractVer().getContractVerId());
	    //reqDTO.setCtContractVer(resDTO.getCtContractVer());
	    String percentage = contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage();
	    getManagedBean().setWarningPercentage(percentage);
		return CONTRACT_PAGE;
	}
	
	/**
	 * The method used to navigate to contract details screen
	 * for view purpose.
	 * This method is used by fulfillment module.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoCommonViewPage(Long contractId) {	
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractId(contractId);
		CtContractVer  latestContractVer = contractSO.getLatestContractVer(reqDTO).getCtContractVer();
		reqDTO.setContractVerId(latestContractVer.getContractVerId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
		reqDTO.setViewOnly(true);
		reqDTO.setCommonView(true);
		 ctBB.getManagedBean().setViewContract(true);
	    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
	    setValidateContractField(resDTO);
	    getManagedBean().setSelectedTabIndex(0);
	    ctBB.getManagedBean().setShowItem(null);
	    getManagedBean().setAsAtDate(DateUtils.getCurrentDateWithoutTime());
	    reqDTO.setCtContractVer(resDTO.getCtContractVer());
	    String percentage = contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage();
	    getManagedBean().setWarningPercentage(percentage);
		return CONTRACT_COMMON_PAGE;
	}
	
	public String gotoCicularPage(Long contractVerId){
		
		   CentralContractBackingBean centralBB = new CentralContractBackingBean();
			FacesContext fc = FacesContext.getCurrentInstance();
			centralBB.getManagedBean().setSelectedCircularContract(null);
			centralBB.getManagedBean().setSelectedCircularItem(null);
			centralBB.getManagedBean().setSelectedZoneItems(null);
			centralBB.getManagedBean().setSelectedTabIndex(0);
			centralBB.getManagedBean().setAction("view");
			centralBB.getManagedBean().setViewContract(true);
			centralBB.getManagedBean().setUpdateAction(false);
			centralBB.getManagedBean().setOutComes(new String[]{});
			centralBB.getManagedBean().setGotoPreviewPage(fc.getViewRoot().getViewId());
			centralBB.getManagedBean().setChkCentralCircular(true);
			centralBB.gotoCommonPage(contractVerId);
			
			return CENTRAL_CIRCULAR_PAGE;
	}
	
	/**
	 * The method used to navigate to contract update screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoUpdatePage(Long contractId) {
		IContractSO contractSO = null;
		
		try {
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
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
	        ctBB.getManagedBean().setViewContract(false);
	        getManagedBean().setExternalPageUrl(null);
	        this.getManagedBean().setSelectedTabIndex(0);	     
			return CONTRACT_PAGE;
		} finally {
			contractSO = null;
		}
	}
	
	/**
	 * The method used to navigate to contract termination history screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoTerminateHistoryPage(Long contractId) {
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		ctReqDTO.setContractId(contractId);
		Long contractVerId = contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId();
    	ctReqDTO.setContractVerId(contractVerId);
	
    	ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
		trBB.getManagedBean().setViewContract(true);
		trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
		trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		trBB.getManagedBean().setExternalPageUrl(null);
		trBB.setCtBB(new ContractBackingBean());	
	//	trBB.getCtBB().getManagedBean().setCtContractVer(resDTO.getCtContractVer());
		if (isContractWithGst(trBB.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())
	    		|| (trBB.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null 
	    		&& trBB.getManagedBean().getContractVer().getCtContractAmount().getGstAmount().doubleValue() != 0)) {
			trBB.getCtBB().getManagedBean().setGstCheckingGUI(true);
	    } else {
	    	trBB.getCtBB().getManagedBean().setGstCheckingGUI(false);
	    }
		trBB.retrieveContractVerOfAgreement();
		
		resDTO = contractSO.retrieveTerminationHistoryList(ctReqDTO);
		trBB.getManagedBean().setTerminationList(resDTO.getTerminationHistoryList());		
		return TERMINATE_HISTORY_PAGE;
	}
	
	/**
	 * The method used to navigate to change bank account screen
	 * for change bank purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoChangeBankPage(Long contractId) {
		long starttime = Calendar.getInstance().getTimeInMillis();
		log.info(" Start time>>>>>" +starttime);
		
		IContractSO contractSO = null;
		IChangeBankAccSO changeBankAccSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			
			changeBankAccSO = (IChangeBankAccSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IChangeBankAccSO.class);	
			reqDTO = new ContractReqDTO();
            reqDTO.setContractVerId(contractVerId);
            reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
            reqDTO.setUpdateAction(true);
			
			resDTO = changeBankAccSO.retrieveContractVerForChangeAcc(reqDTO);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.renewSesionObject();
          	if (resDTO.isSuccessful()) {
          		supplierBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
          		supplierBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
				
          		supplierBB.getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
		    			ModuleCodeConst.CONTRACT_MANAGEMENT, ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT, supplierBB.getManagedBean().getCtContract().getContractNo()));
          		supplierBB.setCtBB(new ContractBackingBean(supplierBB.getManagedBean().getFileDirectory()));		    	
          		
          		supplierBB.getManagedBean().setCurentAccNoBankName(null);
				if(supplierBB.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankAccountNo() != null 
						&& supplierBB.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankName() != null )
					supplierBB.getManagedBean().setCurentAccNoBankName(supplierBB.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankAccountNo() 
							+ " / " + supplierBB.getManagedBean().getCtContract().getLatestContractVer().getCtContractValue().getBankName() );
				
				if (supplierBB.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() != null) {
					supplierBB.getManagedBean().setOriginalAccountNo(supplierBB.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo());
					supplierBB.getPopulateBankAccountList();
				}
           }
			long endttime = Calendar.getInstance().getTimeInMillis();
			 
			 log.info(" End time>>>>>" +endttime);
			 log.info(" Diff time>>>>>" +(starttime - endttime));
				
		} finally {
			reqDTO  = null;
			resDTO   = null;
		}
		return CHANGE_BANK_ACCOUNT_PAGE;
	}
	
	/**
	 * The method used to navigate to request factoring screen
	 * for request or remove factoring company purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoFactoringPage(Long contractId) {		
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;

		try {
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			
			resDTO = contractSO.retrieveFactoringDetailsByContractId(reqDTO);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.renewSesionObject();
			if (resDTO.isSuccessful()) {
				supplierBB.getManagedBean().setCtContract(resDTO.getContract());
				
				supplierBB.getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
		        		ModuleCodeConst.CONTRACT_MANAGEMENT, ParameterConst.DOCTYPE_REQUEST_FACTORING, supplierBB.getManagedBean().getCtContract().getContractNo()));
				supplierBB.setCtBB(new ContractBackingBean(supplierBB.getManagedBean().getFileDirectory()));		    	
          		
				if (supplierBB.getManagedBean().getCtContract().getCtFactoring() != null &&  supplierBB.getManagedBean().getCtContract().isFactored()
						&& (supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()
						|| supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_ACKNOWLEDGEMENT_REJECTED.longValue()
						|| supplierBB.getManagedBean().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_REJECTED.longValue())) {
					supplierBB.getManagedBean().setFactoring(resDTO.getContract().getCtFactoring());
					supplierBB.getManagedBean().getFactoring().setRecordStatusFlg(DbValueConst.RECORD_STATUS_SUSPENDED);
					supplierBB.getManagedBean().setRemoveFactoring(true);
					PmStatus pmStatus = new PmStatus();
					pmStatus.setStatusId(ContractStatusConst.RF_PENDING_SUBMISSION);
					supplierBB.getManagedBean().getFactoring().setStatus(pmStatus);
					supplierBB.resetFactoringCompany();
					supplierBB.getCtBB().initializeRemarkAndAttachement(null, supplierBB.getManagedBean().getRemarkList(), supplierBB.getManagedBean().getAttachmentList());
				} else {
					supplierBB.getManagedBean().setRemoveFactoring(false);
					supplierBB.initializeFactoring();
				}
			}			
		} catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			contractSO = null;
		}
		return REQUEST_FACTORING_PAGE;
	}
    
	/**
	 * The method used to navigate to previous screen.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoBackPage() {
		String url = getManagedBean().getGotoPreviewPage();
		renewSesionObject();
		return url;
	}
	
	/**
	 * The method used to navigate to previous screen.
	 * This method will navigate to contract maintenance screen
	 * when the previous screen is not set. 
	 * 
	 * @return the navigate URL 
	 */
	public String backAction() {
		ctBB.getManagedBean().setViewContract(false);
		getManagedBean().setLoaContractVerList(null);
		return (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
	}
	
	/**
	 * The method used to retrieve the payment tracking information.
     */
	private void calPaymentTracking() {
		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		ContractReqDTO reqDTO = new ContractReqDTO();
		ContractResDTO resDTO = new ContractResDTO();
		reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
		reqDTO.setAsAtDate(getManagedBean().getAsAtDate());
		resDTO = contractSO.calAdvancePaymentTracking(reqDTO);
		
		BigDecimal paidAdvancePaymentAmt = (resDTO.getPaidAdvancePaymentAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidAdvancePaymentAmt();
		BigDecimal paidInvoiceAmt = (resDTO.getPaidInvoiceAmt() == null) ? BigDecimal.valueOf(0) : resDTO.getPaidInvoiceAmt();
	
	   	ctBB.getManagedBean().setPaidAdvancePaymentAmt(paidAdvancePaymentAmt);
		ctBB.getManagedBean().setPaidInvoiceAmt(paidInvoiceAmt);
	}
	
	/**
	 * The method used to set the date of frequency sSchedule.
     */
	private void scheduleDate() {	
		for (CtSchedule ctSchedules : ctBB.getManagedBean().getContractVer().getCtSchedules()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
				String fromDate = dateFormat.format(ctSchedules.getFromDate());
				ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));
					   
				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
				ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}
	
	/**
	 * The method used to set the date of progressive payment.
     */
	private void progressiveDate() {	
		for (CtProgressivePayment ctSchedules : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {			   
			if (ctSchedules.getFromDate() != null && ctSchedules.getToDate() != null) {
			    String fromDate = dateFormat.format(ctSchedules.getFromDate());
			    ctSchedules.setStartMonth(fromDate.substring(3, 5));
				ctSchedules.setStartYear(fromDate.substring(6,fromDate.length()));
			   
				String toDate = dateFormat.format(ctSchedules.getToDate());
				ctSchedules.setEndMonth(toDate.substring(3, 5));
			    ctSchedules.setEndYear(toDate.substring(6, toDate.length()));
			}
		}
	}
	
	/**
	 * The method used to render the check box for company name
	 * and supplier address if there are any updates from profile
	 * management module.
     */
	private void renderUpdateCheckbox(CtContractVer ctContractVer) {
		if (ctContractVer.getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
				&& this.getManagedBean().isUpdateContract()) {
			if (!ctContractVer.getCtContractValue().getSupplierName().equalsIgnoreCase(
					ctBB.getManagedBean().getContractVer().getCtContract().getSmSupplier().getCompanyName())) {
				ctBB.getManagedBean().setUpdateCompanyName(true);
			}
			SmAddress latestAddress = retrieveLatestSupplierAddress();
			if (!ctContractVer.getCtContractValue().getSupplierAddress().equals(latestAddress)) {
				ctBB.getManagedBean().setUpdateSupplierAddress(true);
			}
		} else {
			ctBB.getManagedBean().setUpdateCompanyName(ctContractVer.getCtContractValue().isCompanyNameChange());
			ctBB.getManagedBean().setUpdateSupplierAddress(ctContractVer.getCtContractValue().isAddressChange());
		}
	}
	
	/**
	 * The method used to retrieve the latest supplier address.
     */
	private SmAddress retrieveLatestSupplierAddress() {
		SmAddress smAddress = new SmAddress();
		List<SmCompanyAddress> smCompanyAddresses = new ArrayList<SmCompanyAddress>();
		smCompanyAddresses = ctBB.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();
		for (SmCompanyAddress address : smCompanyAddresses) {
			if (address.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) { // C-Company Address, M-Mailing Address
				smAddress = address.getSmAddress();
			}
		}
		return smAddress;
	}
	
	/**
	 * The method used to retrieve the zone object.
     */
	private void getZoneObjDetails() {
    	if (!ctBB.getManagedBean().getContractVer().getCtContractZones().isEmpty()) {
    		for (CtContractZone ctContractZone : ctBB.getManagedBean().getContractVer().getCtContractZones()) {
    			for (TreeNode zonename : ctBB.getManagedBean().getCtZoneTrees().getChildren()) {
    				if (ctContractZone.toString().equalsIgnoreCase(((LocationDTO)zonename.getData()).getZoneId())) {
    					if (ctContractZone.getDeliveryTermUom() == null) {
     					   ctContractZone.setDeliveryTermUom(new PmParameter());
    					}
    					ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
    					ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
    				}
    			}
    		}
    	}
	}
	
	/**
	 * The method used to retrieve schedule or progressive 
	 * from date and to date value.
     */
	private void scheduleProgressiveDate() {    	
		if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
			if (ctBB.getManagedBean().getContractVer().getCtSchedules() != null 
					&& !ctBB.getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
				for (CtSchedule schedule : ctBB.getManagedBean().getContractVer().getCtSchedules()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						Date d1 = new Date();
						String localDate = dateFormat.format(d1);
						String date = localDate.substring(0, 2);
						String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				ctBB.getManagedBean().getContractVer().setCtProgressivePayments(null);
			}
		} else {
			if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() != null 
					&& !ctBB.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
				for (CtProgressivePayment schedule : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {
					if (schedule.getStartMonth() != null && schedule.getEndMonth() != null) {
						Date d1 = new Date();
						String localDate = dateFormat.format(d1);
						String date = localDate.substring(0, 2);
						String fromDate = date + "/" + schedule.getStartMonth() + "/"+ schedule.getStartYear();
						String toDate = date + "/" + schedule.getEndMonth() + "/"+ schedule.getEndYear();
						try {
							schedule.setFromDate(dateFormat.parse(fromDate));
							schedule.setToDate(dateFormat.parse(toDate));
						} catch (ParseException e) {
							log.debug(e.getMessage());
						}
					}
				}
				ctBB.getManagedBean().getContractVer().setCtSchedules(null);
			} 
		}	
	}
	
	/**
	 * The method used for generating a list of status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of status
	 */
	public SelectItem[] getSearchStatusDropdown() {
		SelectItem[] items = null;
		
		try {
			items = new SelectItem[4];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_ALL,
					ResourceBundleUtils.getMessage("lbl.bpm.all", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_COMPLETE,  
					ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.STATUS_INCOMPLETE,  
					ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}
	
	/**
	 * The method used for generating a list of contract status 
	 * for searching contract as selection.
	 * 
	 * @return the selection list of contract status
	 */
	public SelectItem[] getSearchContractStatusDropdown() {
		SelectItem[] items = null;
		
		try {
			items = new SelectItem[5];
			int i = 0;
			items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_EFFECTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_INACTIVE,  
					ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_EXPIRED,  
					ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			items[i++] = new SelectItem(ContractConst.CONTRACT_STATUS_TERMINATED,  
					ResourceBundleUtils.getMessage("lbl.ct.terminated", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}

	/**
	 * The method used to retrieve the contract status of the contract.
	 * 
	 * @return Effective, Terminated, Expired, Inactive
    */
	public String getContractStatusDesc(int contractStatus) {
		if (contractStatus == ContractConst.CONTRACT_STATUS_EFFECTIVE) {
			return ResourceBundleUtils.getMessage("lbl.ct.effective", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_INACTIVE	) {
			return ResourceBundleUtils.getMessage("lbl.ct.inactive", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_EXPIRED	) {
			return ResourceBundleUtils.getMessage("lbl.ct.expired", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (contractStatus == ContractConst.CONTRACT_STATUS_TERMINATED	) {
			return ResourceBundleUtils.getMessage("lbl.ct.terminated", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return "";
		}
	}
	
	/**
	 * The method used to retrieve the status completion of the contract.
	 * 
	 * @return complete or incomplete
     */
	public String chkContractStatusType(int status) {
		if (status == ContractConst.STATUS_COMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.complete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
	}
	
	/**
	 * The method used to check whether the contract is fulfilled the criteria
	 * for amendment.
	 * 
	 * @return true, if the criteria is fulfilled
	 *         false, if the criteria is not fulfilled
     */
	public boolean checkAmendmentAllow(VCtContract vCtContract) {
		if (vCtContract.getCtContract().getLatestContractVer() != null) {			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractVerId(vCtContract.getCtContract().getLatestContractVer().getContractVerId());
			// Amendment always get the latest contract amount
			reqDTO.setAsAtDate(vCtContract.getCtContract().getLatestContractVer().getCtContractValue().getExpDate());
			
			ContractResDTO resDTO = contractSO.retrieveContractAmount(reqDTO);			
			double contractAmount = resDTO.getCtContractAmount().getContractAmount().doubleValue();
			if (contractAmount <= ContractConst.CONTRACT_THRESHOLD_200k) {
				return false;
			} else if (contractAmount <= ContractConst.CONTRACT_THRESHOLD_500k) {
				if (resDTO.getCtContractAmount().isAgreement()) {
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
	
	/**
	 * The method used to add a new performance bond record.
	 * This method will generate a new record in performance bond
	 * list, user allow to enter the bond details and attachment. 
	 */
    public void addBondDetails() {
		CtPerformanceBond newBond = new CtPerformanceBond();
		newBond.setFinancialOrg(new PmFinancialOrg());
		newBond.setEffDate(ctBB.getManagedBean().getContractVer().getCtContractValue().getEffDate());
		
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);		
		ContractAmendmentReqDTO amendReqDTO = new ContractAmendmentReqDTO();
		amendReqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
		amendReqDTO.setAmendmentEffectiveDate(ctBB.getManagedBean().getCtContractAmount().getEffDate());		
		ContractAmendmentResDTO resAmendDto = amendSO.retrieveContractEndDateByAmendmentEffectiveDate(amendReqDTO);
		
        newBond.setExpDate(resAmendDto.getPerformanceBondExpDate());
     	ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().add(newBond);
	}
	
    /**
	 * The method used for initializing the remarks of contract.
	 */
	private void initializeRemarks() {
		initiateRemark(ctBB.getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(ctBB.getManagedBean().getContractVer().getSettingRemarks());
	}
	
	/**
	 * The method used for initializing the remarks of contract.
	 * This method will create new remark object for every single new 
	 * process, else will display all remarks.
	 * 
	 * @param remarkList  the list of remarks to display. If the remarkList is null, 
	 *                    initiate a new remark
	 */
	private void initiateRemark(List<CtRemark> remarks) {
		boolean ctWorkflowStatusMatch = false;
		if (ctBB.getManagedBean().getContractVer().getCtWorkflowStatus() != null) {
			if (remarks.size()>0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (ctBB.getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId() 
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}
		
		if (remarks.size() == 0 || !ctWorkflowStatusMatch) {
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
	 * The method used to set the file index of attachment list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}
	
    /**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
	public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
	    if (filePath != null) {
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
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
	public String getPortletTitle() {
		String portletTitle = null;
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.ministry.contract.maintenance",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.central.contract.maintenance",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else if (isSupplierRole()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.list",
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
		return portletTitle;
	}
	
	/**
   	 * The method used to retrieve the fulfilement details base on contract expire date or current date
   	 */
	private void retrieveFulfilmentDetails() {
		// Retrieve fulfilement details base on contract exp date
		if (getManagedBean().getAsAtDate() == null) {
			getManagedBean().setAsAtDate(ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		}
		
    	for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContractAmounts()){
    		if (getManagedBean().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
    				&& getManagedBean().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()){
    			ctBB.getManagedBean().setCtContractAmount(contractAmt);
    			break;
    		}
    	}
    	//Retrieve agreement contract version for physical no & signed date
		retrieveContractVerOfAgreement();
		
    	// Retrieve overall contract amount
    	if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() != null) {
    		ctBB.getManagedBean().setGstTotalAmount(ctBB.getManagedBean().getCtContractAmount().getGstAmount().add(ctBB.getManagedBean().getCtContractAmount().getContractAmount()));
    		
    	} else {
	    		ctBB.getManagedBean().setGstTotalAmount(ctBB.getManagedBean().getCtContractAmount().getContractAmount());
	  	  
    	}
	}
	
	/**
	 * The method used for retrieving the contract version 
	 * for agreement or non-agreement contract.
	 * If it's original agreement, return the first contract version.
	 * If it's supplementary agreement, return the amended contract version.
     */
	private void retrieveContractVerOfAgreement() {
		IContractSO contractSO = null;
		IContractAmendmentSO amendmentSO = null;
		ContractResDTO resDTO = null;
		ContractAmendmentResDTO amendmentResDTO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();
			
			if (ctBB.getManagedBean().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(ctBB.getManagedBean().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);				
				if (amendmentResDTO.getCtAmendment() != null) {
					ctBB.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);
					
					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					ctBB.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}
				// Display the contract physical no of the agreement version during contract update
				if (getManagedBean().getTaskId() == null) {
					ctBB.getManagedBean().getContractVer().setContractPhysicalNo(ctBB.getManagedBean().getAgreementContractVer().getContractPhysicalNo());
				}
			} else {
				ctBB.getManagedBean().setAgreementContractVer(ctBB.getManagedBean().getContractVer());
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			 contractSO = null;
			 amendmentSO = null;
		}
	}
	
	/**
	 * The method used to check whether current process is contract creation.
	 * 
	 * @return true, if current process is contract creation
	 *         false, if current process is not contract creation
     */
	public boolean contractCreationProcess() {
		if (ctBB.getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_SUBMISSION.longValue()
				|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_CONTRACT_CREATION.longValue()
				|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_APPROVAL.longValue()
				|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_REVISION.longValue()
				|| ctBB.getManagedBean().getContractVer().getStatus().getStatusId().longValue() == ContractStatusConst.FD_PENDING_REVISION_APPROVAL.longValue()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
   	 * The method to used auto update of request.
   	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
	
	/**
   	 * The method used to set update value false for disable purpose.
     */
	public void disableUpdate() {
		getManagedBean().setAutoUpdate(false);
	}	
	
	/**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {		
		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
			return RoleCode.CT_APPROVER.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
			return RoleCode.BPK_CCA.value();
		} else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) {
			return RoleCode.BPK_APPROVER.value();
		} else {
			return null;
		}
	}
	
	/**
	 * The method used to check whether approver setting tab menu is required to display.
	 * 
	 * @return true, if approver setting tab menu is required to display
	 *         false, if approver setting tab menu is not required
     */
	public boolean isApproverSetting() {
		if (isContractAdmin() && (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME))) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * The method used to verify the login user is BPK approver 
     * or contract approver.
	 * 
	 * @return true, if login user is BPK approver or contract approver
	 *         false, if login user is not BPK approver or contract approver
     */
    public boolean isApprover() {
		return (getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()));
	}    

    /**
     * The method used to verify the login user is central contract administrator.
	 * 
	 * @return true, if login user is central contract administrator
	 *         false, if login user is not central contract administrator
     */
	public boolean isBPKCCA() {
		return (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
    
    /**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
     */
    public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
    
    /**
   	 * The method used to disable the ministry selection
   	 * if login user is central contract administrator
   	 * or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is central contract administrator/ministry administrator/internal auditor
   	 *         false, if login user is not central contract administrator/ministry administrator/internal auditor
	 */
    public boolean disableMinistrySelection() {
    	return (isContractAdmin() || (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) 
				&& !getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())));
    }
    
    public boolean chkCircularActiveRecord(){
    	if(ctBB.getManagedBean().getContractVer().getCtContract().getCtCircularContracts().size() > 0) {
    			  for(CtCircularContract circularContract : ctBB.getManagedBean().getContractVer().getCtContract().getCtCircularContracts()) {
    			      if(circularContract.getCtCircularVer().getRecordStatusFlg() == 1) {
    			    	  getManagedBean().setCtCircularVer(circularContract.getCtCircularVer());
    			    	  return true;
    			      }
    		  } 
    	}
      	return false;
    }
    
    
    private void defaultMinistryPtjSearchValue() {
    	if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
    			|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
    		this.getManagedBean().setSearchMinistryProfileId(null);
    		this.getManagedBean().setSearchPtjProfile(null);
			
		} else {
			IUserSO userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO userResDTO = userSO.retrieveUserOrgListByUserId(userReqDTO);
			PmOrgProfile orgProfile = null;
			if (userResDTO.getUserOrgList()!=null && userResDTO.getUserOrgList().size()>0) {
				orgProfile = userResDTO.getUserOrgList().get(0).getPmOrgProfile();
			}
			
			if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())) {			
				this.getManagedBean().setSearchMinistryProfileId(orgProfile.getOrgProfileId());
				this.getManagedBean().setSearchPtjProfile(null);
			} else if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				this.getManagedBean().setSearchMinistryProfileId(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()).getOrgProfileId());
				this.getManagedBean().setSearchPtjProfile(orgProfile);
			} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				this.getManagedBean().setSearchMinistryProfileId(null);
				this.getManagedBean().setSearchPtjProfile(orgProfile);
			}
		}
    }
    
    /**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPTJCondition() {
		String condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getSearchMinistryProfileId();
		return condition;
	}
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setSearchPtjProfile(null);
	}
	
	public SelectItem[] retrieveParameterDescListByType() {
		
		SelectItem[] items = null;
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>(); ;
		
		
		try {
			SearchFilterBean filterBean = new SearchFilterBean("pmParameter.parameterCode",SearchOperator.OPERATOR_NOT_EQUALS.value(), "AP");
	        filterBean.setCaseInsensitive(true);	
	        searchBean.getDataFilterBeans().add(filterBean);
			List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().getParameterDescListBySearchBean(
					ParameterTypeConst.DEDUCTION_TYPE, searchBean);
			
		    items = new SelectItem[parameterDescs.size()];
		    int i = 0;
		    for (PmParameterDesc parameterDesc : parameterDescs) {
		    	items[i++] = new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc.getCodeName());
		    }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	/**
	 * The method used for generating a list of available history of
	 * loa contract version as selection.
	 * 
	 * @return loaContractVerList  the selection list
	 */
	public SelectItem[] getLoaContractVerList() {
		if (getManagedBean().getLoaContractVerList() == null ) {
			SelectItem[] items = null;
			IContractSO contractSO = null;
	        ContractReqDTO reqDTO = null;
	        ContractResDTO resDTO = null;
	        
	        try {
	        	int i = 0;
	        	if (ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {	        		
	        		contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
	    		    		ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
		            reqDTO = new ContractReqDTO();
		            resDTO = new ContractResDTO();
		            
		            reqDTO.setContractId(ctBB.getManagedBean().getContractVer().getCtContract().getContractId());
		           	resDTO = contractSO.retrieveLoaContractVerByContractId(reqDTO);
		           	
		           	if (resDTO.getList() != null && resDTO.getList().size() > 0) {
		           		items = new SelectItem[resDTO.getList().size()];
		           		for (Object[] object : resDTO.getList()) {
		           			items[i++] = new SelectItem(Long.parseLong(object[0].toString()), object[1].toString());
		           		}
		           	}
	        	}  else {
	           		items = new SelectItem[1];
	           		items[i++] = new SelectItem(ctBB.getManagedBean().getContractVer().getContractVerId(), ctBB.getManagedBean().getContractVer().getCtContractValue().getLoaPhysicalNo());
	           	}
		        getManagedBean().setLoaContractVerList(items);	        		
	        } finally {
	        	contractSO = null;
	            reqDTO = null;
	            resDTO = null;
	        }
		}
		return getManagedBean().getLoaContractVerList();
	}
	
	/**
   	 * The method used to reflect the contract details base on selected loa
   	 */
	public void changeLoaContractVer() {
		if (getManagedBean().getLoaContractVerId() != null) {
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			reqDTO.setViewOnly(true);
			reqDTO.setContractVerId(getManagedBean().getLoaContractVerId());
		    ContractResDTO resDTO = contractSO.retrieveContractVerDtls(reqDTO);
		    ctBB.getManagedBean().setViewContract(true);
		    getManagedBean().setAsAtDate(null);
		    setValidateContractField(resDTO);
		}
	}
}