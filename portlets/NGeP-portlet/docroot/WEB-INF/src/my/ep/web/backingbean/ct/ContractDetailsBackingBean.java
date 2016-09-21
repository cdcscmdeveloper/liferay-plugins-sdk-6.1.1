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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
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
	private static final String CONTRACT_COMMON_PAGE = "/xhtml/ct/viewcontract/fulfilmentTab.xhtml?faces-redirect=true";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml?faces-redirect=true";
	private static final String CHANGE_BANK_ACCOUNT_PAGE = "/xhtml/ct/supplier/bank/changeBankAccount.xhtml?faces-redirect=true";
	private static final String REQUEST_FACTORING_PAGE = "/xhtml/ct/supplier/factoring/requestOfFactoring.xhtml?faces-redirect=true";
	
	private String editorValue;
	private ContractBackingBean ctBB = null;
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public ContractDetailsBackingBean() {
		/* Initialization of button */
		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}
		
		if (getManagedBean().getOutComes() == null) {
			getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
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
			getManagedBean().setApproverModel(new DualListModel<PmUser>(new ArrayList<PmUser>(),  new ArrayList<PmUser>()));
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
				&& (getManagedBean().getSearchContractStatus() == 0 && !isSupplierRole())
				&& getManagedBean().getSearchMinistryProfile() == null
				&& getManagedBean().getSearchPtjGroupProfile() == null
				&& getManagedBean().getSearchPtjProfile() == null) {
			throw new EpException(MessageConst.ERROR_100060);
		} else {
			getManagedBean().getContractDataModel().reloadData();
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
		this.getManagedBean().setSearchContractStatus(0);
		defaultMinistryPtjSearchValue();
	}
	
	/**
	 * The method used to clear the previous viewed information before
	 * rendering the contract search screen.
	 */
	public void preRenderSearch(boolean trackingDiarySearch) {
		renewSesionObject();
		defaultMinistryPtjSearchValue();
		getManagedBean().setTrackingDiarySearch(trackingDiarySearch);
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
				
				if (ctBB.getManagedBean().getContractVer().getDocType() != null
						&& ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE)
						&& ctBB.getManagedBean().isServiceTaxAlertFlg()) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, null, 
									MessageUtils.getMessage(MessageConst.INFO_758004,
											MessageUtils.BUNDLE_COMMON_MESSAGES, 
											new FacesScopes().getUserLocale())));
					ctBB.getManagedBean().setServiceTaxAlertFlg(false);				
				}
				
				return;
			}
			
			Object obj = super.getBpmTaskDetails(Object.class, new String[]{BpmOutcomeConst.BPM_SAVE});
			getManagedBean().setSelectedTabIndex(0);

			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setTaskName(getManagedBean().getTaskName());
			if (getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_INITIATION_PROCESS_NAME) 
					|| getManagedBean().getProcessName().equalsIgnoreCase(CTProcessNameConst.CONTRACT_AMENDMENT_PROCESS_NAME)) {
				CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
				reqDTO.setContractId(new Long(dataType.getContractId()));
				reqDTO.setContractVerId(new Long(dataType.getDocId()));
				reqDTO.setDocType(dataType.getDocType());
			} else {
				CTContractDataType dataType = (CTContractDataType) obj;
				reqDTO.setContractId(new Long(dataType.getContractId()));
				reqDTO.setContractVerId(new Long(dataType.getDocId()));
				reqDTO.setDocType(dataType.getDocType());
			}
		    
			setValidateContractField(reqDTO);
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, 
					ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		
	}
	
	/**
	 * The method used to set the contract details information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	public void setValidateContractField(ContractReqDTO reqDTO) {
		
		IContractSO contractSO = null;
		ContractResDTO resDTO = null;
		ctBB = new ContractBackingBean();
		 if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
	        	ctBB.getManagedBean().getDeleteAttachmentList().clear();
	        }
		try {
		          contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
		           resDTO = contractSO.retrieveContractVerDtls(reqDTO);
		           ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
	      
		          // Retrieve Agencies
	              if (ctBB.getManagedBean().getContractVer().getCtAgencies() != null  && ctBB.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
	    	              ctBB.getManagedBean().setSelectedAgency(null);
	    	              for (CtAgency ctAgency : ctBB.getManagedBean().getContractVer().getCtAgencies()) {
	    		                 ctBB.checkAgencyStatus(ctAgency);		        	   
	    	              }
	              }
	   
	              if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
		                   ctBB.getManagedBean().setMinistryProfileId(retrieveMinistryOrgProfile(
					       ctBB.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId());
	              }
		
	              // Retrieve Items
	              ctBB.getManagedBean().getCtItemList().clear();
	              ctBB.getManagedBean().getCtItemServiceList().clear();	        	   
	              ctBB.getManagedBean().setShowItem(null);
	              ctBB.getManagedBean().setShowServiceItem(null);
	              ctBB.getManagedBean().setSelectedItem(null);
	              ctBB.getManagedBean().setSelectedServiceItem(null);	  
	              if (ctBB.getManagedBean().getContractVer().getCtContractItems().size() > 0) {  	  
 		         
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
	    				                for (CtItemCeilingQty ceilingQty : item.getCtItemCeilingQties()) {			        	        	    	   
	    					                  ctBB.checkAgencyStatus(ceilingQty.getCtAgency());		        	   
	    				                }
	    			             }
	    			      ctBB.getManagedBean().getCtItemList().add(item);
	    		      }
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
				    	ctBB.scheduleDate();
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
	    	              ctBB.progressiveDate();
	                 }
	   
				    // Retrieve Delivery Terms
				    if (ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom() == null) {
				    	ctBB.getManagedBean().getContractVer().getCtContractValue().setDeliveryTermUom(new PmParameter());
				    	PmParameter pmParameter = getParameterByTypeAndCode(
				    			ParameterTypeConst.DELIVERY_TERM, ParameterConst.DELIVERY_TERM_DAYS);
				    	ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTermUom().setParameterId(pmParameter.getParameterId());
				    }
	   
					if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				    		ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
						 ctBB.getManagedBean().setDirectPurchase(true);
						 ctBB.getManagedBean().setFlgPaymentTracking(false);
					} else {
							ctBB.getManagedBean().setDirectPurchase(false);
					}
	    
					if (getManagedBean().getUserMap().size() == 0) {
						initializeApproverList();
					}
				    // Retrieve Approver List Information
					if (isApproverSetting()) {				
						getManagedBean().getApproverModel().setSource(
								getAvailableApproverList(ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode(),
										getManagedBean().getUserMap()));
				   		
						if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
							getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
		                	List<PmUser> invalidApproverList = new ArrayList<PmUser>();
		                	for (PmUser approverUser : resDTO.getApproverList())  {
		                		Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();	
		                		boolean isValidApprover = false;
		                		while (sourceIt.hasNext()) {
		                			PmUser user = sourceIt.next();
		                			log.info("user : "+user+"; approverUser: "+approverUser);
		                			if (user!=null && approverUser!=null && (user.getUserId() == approverUser.getUserId())) {
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
					}
	 	
					getManagedBean().setRemarkList(resDTO.getCtRemarks());
					getManagedBean().setAttachmentList(resDTO.getCtAttachments());
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
				 ctBB.getManagedBean().setSupplierAddress(getFullAddress(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress()));
				 ctBB.renderUpdateCheckbox(ctBB.getManagedBean().getContractVer());
				 ctBB.getManagedBean().setSupplierCompanyName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				 ctBB.getManagedBean().setSupplierBranchName(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
				 ctBB.getManagedBean().setSupplierRegNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierRegNo());
				 ctBB.getManagedBean().setSupplierMofNo(ctBB.getManagedBean().getContractVer().getCtContractValue().getSupplierMofNo());
				 ctBB.retrieveBranchInfo();
				if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
					ctBB.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
				} else {
					ctBB.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
				}
				if (ctBB.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE)) {
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
				
				ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());
				ctBB.initializeRemarkAndAttachement(resDTO.getCtContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 

		    	ctBB.getManagedBean().setDisableContractAmount(true);
				if (ctBB.isContractWithGst(ctBB.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
					if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() == null) {
			    		ctBB.getManagedBean().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
					}
			    	ctBB.getManagedBean().setGstCheckingGUI(true);
			    	ctBB.getManagedBean().setDisableGstAmount(true);
			    	
			    	if (!ctBB.getManagedBean().isViewContract()) {
			    		// If the GST is effective and the Amount has been keyed in by user in sourcing,0
			    		// the value must generate into Fulfilment Details and non-editable.
			    		if (ctBB.getManagedBean().getContractVer().getCtContractValue().getLoaGstAmount() == null
			    				&& (resDTO.getCtContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_CREATION)
					    				|| resDTO.getCtContractVer().getDocType().equalsIgnoreCase(ParameterConst.DOCTYPE_CONTRACT_UPDATE))) {
			    			ctBB.getManagedBean().setDisableGstAmount(false);
			    		}
			    		
			    		// If original contract is fulfilled imposed service tax rule
			    		if (ctBB.isBeforeGstReliefDueDate() && ctBB.imposedServiceTaxCondition()) {
			    			if (ctBB.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
			    				ctBB.getManagedBean().setDisableContractAmount(false);
			    			} else {
			    				if (ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= ctBB.getGstReliefDueDate().getTime()) {
			    					ctBB.getManagedBean().setDisableGstAmount(true);
			    				}
			    			}
			    		}
			    	}
			    } else {
			    	ctBB.getManagedBean().setGstCheckingGUI(false);
			    }	
		  } catch(Exception e){
				e.printStackTrace();
			} finally {
				resDTO = null;
				contractSO = null;
			}
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
    		ctBB.getZoneObjDetails();
    	
    		if(!ctBB.getManagedBean().getContractVer().getCtContractValue().isPid()){
         		ctBB.getManagedBean().getContractVer().setCtPidReasons(null);
         	}
    		
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
         		ctBB.scheduleProgressiveDate();
         	}
         	
         	reqDTO.setCtContract(ctBB.getManagedBean().getContractVer().getCtContract());
         	CtContractVer ctContractVer = ctBB.getManagedBean().getContractVer();
         	
         	if (ctContractVer.getCtContractValue().isCompanyNameChange()) {
         		ctContractVer.getCtContractValue().setSupplierName(ctBB.getManagedBean().getSupplierCompanyName());
         		ctContractVer.getCtContractValue().setSupplierBranch(ctBB.getManagedBean().getSupplierBranchName());
         		ctContractVer.getCtContractValue().setSupplierRegNo(ctBB.getManagedBean().getSupplierRegNo());
         		ctContractVer.getCtContractValue().setSupplierMofNo(ctBB.getManagedBean().getSupplierMofNo());
         	}
         	if (ctBB.getManagedBean().getCtAddress() != null) {
         		ctContractVer.getCtContractValue().setSupplierAddress(ctBB.getManagedBean().getCtAddress());
         	}
         	
         	if(ctBB.getManagedBean().getCtContractAmount().isBondReq() &&
         			!ctBB.getManagedBean().isDirectPurchase() && !ctBB.getManagedBean().getCtContractAmount().isBondExempted()){
                 	for (CtContractAmount contractAmt : ctContractVer.getCtContractAmounts()){
        		            if (contractAmt.getContractAmountId() != null && ctBB.getManagedBean().getCtContractAmount() != null
        				           && contractAmt.getContractAmountId().intValue() == ctBB.getManagedBean().getCtContractAmount().getContractAmountId().intValue()){
        		            	contractAmt.setTotalBondAmt( ctBB.getManagedBean().getCtContractAmount().getTotalBondAmt());
        		            	contractAmt.setCtPerformanceBonds( ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds());
        		            }
        	        }
         	 }
           
         	reqDTO.setCtContractVer(ctContractVer);
            reqDTO.setAgreementContractVer(ctBB.getManagedBean().getAgreementContractVer());
            reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
	        reqDTO.setAttachments(getManagedBean().getAttachmentList());
	        reqDTO.setUserMap(getManagedBean().getUserMap());
	        reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
		    reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		    reqDTO.setDeleteAttachments(ctBB.getManagedBean().getDeleteAttachmentList());
			
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
					setValidateContractField(ctReqDTO);
				}
		    } else {
				throw new EpException(MessageConst.ERROR_700020);
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
    	getManagedBean().setAutoUpdate(true);
    	
    	if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) &&
    			!ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
    			if(ctBB.getManagedBean().getContractVer().getCtContractValue().getDeliveryTerm() <= 0){
    				throw new EpException(MessageConst.ERROR_100062);
    			}
    	}
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
			 				getManagedBean().setSelectedTabIndex(0);
			 				throw new EpException(MessageConst.ERROR_100022, 
									new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
											ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			 			}
	 		    }
		
				//Validation for PID
				if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && 
						ctBB.getManagedBean().getContractVer().getCtContractValue().isPid() && 
						(ctBB.getManagedBean().getContractVer().getCtPidReasons() == null || ctBB.getManagedBean().getContractVer().getCtPidReasons().size() == 0)) {
					    getManagedBean().setSelectedTabIndex(0);
					    		throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
		
				// Validation for PID Reason
				if(ctBB.getManagedBean().getContractVer().getCtPidReasons() != null && ctBB.getManagedBean().getContractVer().getCtPidReasons().size() > 0 ){
					for(CtPidReason reason : ctBB.getManagedBean().getContractVer().getCtPidReasons()){
						   if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){
							   getManagedBean().setSelectedTabIndex(0);
							   throw new EpException(MessageConst.ERROR_100022, 
										new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						   }
						   ctBB.validatePidReason(reason.getPmReason().getReasonId());
						   if(ctBB.PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
							   if(reason.getReasonDesc().isEmpty()){
								   getManagedBean().setSelectedTabIndex(0);
								   throw new EpException(MessageConst.ERROR_100022, 
											new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason.desc", 
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							   }
						   }
					 }
				}//  End validation for PID Reason
		
				// Validation for GST amount before GST Relief Due Date
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
						if (ctBB.isBeforeGstReliefDueDate()
								&& ctBB.imposedServiceTaxCondition()
								&& ctBB.isContractWithGst(ctBB.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())
								&& ctBB.getManagedBean().getContractVer().getCtContractValue().isServiceTax()) {
								if (ctBB.getManagedBean().getCtContractAmount().getGstAmount() == null
										|| ctBB.getManagedBean().getCtContractAmount().getGstAmount().doubleValue() == 0) {
									 getManagedBean().setSelectedTabIndex(0);
									 throw new EpException(MessageConst.ERROR_100022, 
												new String[]{ResourceBundleUtils.getMessage("lbl.ct.gst.amt", 
														ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
								}				
								if (ctBB.getManagedBean().getCtContractAmount().getContractAmount() == null
										|| ctBB.getManagedBean().getCtContractAmount().getContractAmount().doubleValue() == 0) {
									 getManagedBean().setSelectedTabIndex(0);
									 throw new EpException(MessageConst.ERROR_100022, 
												new String[]{ResourceBundleUtils.getMessage("lbl.ct.contract.amount", 
														ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
								}
						}
				}
				
				// Validation for performance bond
				if (ctBB.getManagedBean().getCtContractAmount().isBondReq() && !ctBB.getManagedBean().isDirectPurchase()
						&& !ctBB.getManagedBean().getCtContractAmount().isBondExempted() && !action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					//In CT Update(bond required): if previously the bond already defined, number of performance bond must be > 0 while submission; 
					//In CT Update(bond required): if previously no bond defined, system allow submission of FD Update even the number of performance bond entered = 0.
							if (ctBB.getManagedBean().getContractVer().getDocType().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_UPDATE)) {
								if (ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) {
									for (CtContractAmount contractAmt : ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer().getCtContractAmounts()) {
							    		if (getManagedBean().getAsAtDate().getTime() >= contractAmt.getEffDate().getTime() 
							    				&& getManagedBean().getAsAtDate().getTime() <= contractAmt.getExpDate().getTime()) {
							    			if (contractAmt.getCtPerformanceBonds().size() > 0 
							    					&& (ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds() == null 
							    					|| ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().isEmpty())) {
							    				getManagedBean().setSelectedTabIndex(0);
							    				throw new EpException(MessageConst.ERROR_100022, 
														new String[]{ResourceBundleUtils.getMessage("lbl.ct.menu.performance.bond", 
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
							    					
							    			}
							    		}
							    	} 
								}
							}	
							
							if (ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds() != null 
									&& ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds().size() > 0) {
								double bondAmount = 0.0;
							      	for (CtPerformanceBond performanceBond : ctBB.getManagedBean().getCtContractAmount().getCtPerformanceBonds()) {
											bondAmount = bondAmount + performanceBond.getBondAmount();
											if (performanceBond.getRefNo() == null || performanceBond.getRefNo().isEmpty()) {
												getManagedBean().setSelectedTabIndex(0);
												throw new EpException(MessageConst.ERROR_100022,
														new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.no",
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
											}						
							
											if (performanceBond.getFinancialOrg() == null || performanceBond.getFinancialOrg().getFinancialOrgId() == null) {
												getManagedBean().setSelectedTabIndex(0);
												throw new EpException(MessageConst.ERROR_100022, 
														new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.financial.org", 
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
											}						
						
											if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
												if (performanceBond.getBondAmount() == 0) {
													getManagedBean().setSelectedTabIndex(0);
													throw new EpException(MessageConst.ERROR_100033,
															new String[]{ResourceBundleUtils.getMessage("lbl.ct.pb.amount",
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												 }
										    }						
							
										   if (performanceBond.getFileName() == null || performanceBond.getFileName().isEmpty()) {
												getManagedBean().setSelectedTabIndex(0);
												throw new EpException(MessageConst.ERROR_100022,
														new String[] {ResourceBundleUtils.getMessage("lbl.ct.pb.ref.letter",
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
									  	} 
					        	}//End for loop				 
					
							   if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
							           DecimalFormat df = new DecimalFormat("#.00");			
							          // Validation on ministry contract only
							         if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
											ParameterConst.CONTRACT_TYPE_MINISTRY)) {
											if (ctBB.getManagedBean().getCtContractAmount().getTotalBondAmt() == null) {
												getManagedBean().setSelectedTabIndex(0);
												throw new EpException(MessageConst.ERROR_750021);
									
											} else {						
													bondAmount = Double.valueOf(df.format(bondAmount));
													double oriBondAmount = Double.valueOf(df.format(
															ctBB.getManagedBean().getCtContractAmount().getTotalBondAmt().doubleValue()));
												
													int retval = Double.compare(bondAmount , oriBondAmount);
													if (retval >0) {
														getManagedBean().setSelectedTabIndex(0);
														throw new EpException(MessageConst.ERROR_750015);
													}
													if (retval < 0) {
														getManagedBean().setSelectedTabIndex(0);
														throw new EpException(MessageConst.ERROR_750017);  
													}	
										  }
							     }
						    }
					    }	
				    }
	
						// Validation for zone
						if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
							ctBB.getZoneObjDetails();
							if (ctBB.getManagedBean().getContractVer().getCtContractZones() != null) {
								for (CtContractZone contractZone : ctBB.getManagedBean().getContractVer().getCtContractZones()) {
									if (contractZone.getDeliveryTerm() == 0) {
										getManagedBean().setSelectedTabIndex(1);
										throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
									}
								}
							}
						}		
				
						// Validation for agency
						if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY) 
								&& ctBB.getManagedBean().getContractVer().getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
						     	setTabValue("AG");
							   throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
						}
				
								// Validation for Deduction
						if(ctBB.getManagedBean().getContractVer().getCtDeductions() != null && ctBB.getManagedBean().getContractVer().getCtDeductions().size() > 0){
							  for(CtDeduction deduction : ctBB.getManagedBean().getContractVer().getCtDeductions()){
								   if(deduction.getDeductionType() == null ||deduction.getDeductionType().getParameterId() == null ){
									   setTabValue("DE");
									   throw new EpException(MessageConst.ERROR_100022, 
												new String[]{ResourceBundleUtils.getMessage("lbl.ct.deduction.type", 
														ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
								   }ctBB.validateDeductionType(deduction.getDeductionType().getParameterId());
								   if(deduction.getCtDeductionClauses() != null && deduction.getCtDeductionClauses().size() > 0){
								   for(CtDeductionClause ctDeductionClauses : deduction.getCtDeductionClauses()){
										  if(ctDeductionClauses.getClauseDescription().isEmpty()){
											  throw new EpException(MessageConst.ERROR_100022, 
														new String[]{ResourceBundleUtils.getMessage("lbl.ct.deduction.clause.desc", 
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
										  }
									  }
								   }
							  }  
						}		
	
						// Validation for schedule
						if(ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)
								&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
								if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
									ctBB.getManagedBean().getContractVer().setCtProgressivePayments(null);
								} else {
									ctBB.getManagedBean().getContractVer().setCtSchedules(null);  	
								}
							
								List<String> schedulePhaseNoList = new ArrayList<String>();
							    if (ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
										if (ctBB.getManagedBean().getContractVer().getCtSchedules() == null || ctBB.getManagedBean().getContractVer().getCtSchedules().size() < 2 
												&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
											setTabValue("SC");
											throw new EpException(MessageConst.ERROR_750022);
								     	}
										for (CtSchedule schedules : ctBB.getManagedBean().getContractVer().getCtSchedules()) {
												if (schedules.getScheduleNo() == null) {
													setTabValue("SC");
													throw new EpException(MessageConst.ERROR_100022,
															new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												}
									
												if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {
														setTabValue("SC");
														throw new EpException(MessageConst.ERROR_100046, 
																new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
																		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
													} else {
														schedulePhaseNoList.add(schedules.getScheduleNo().toString());
													}
								
												if (schedules.getStartMonth() == null) {
													setTabValue("SC");
													throw new EpException(MessageConst.ERROR_100022,
															new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
																	+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
																			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												}
											
												if (schedules.getEndMonth() == null) {
													setTabValue("SC");
													throw new EpException(MessageConst.ERROR_100022,
															new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
																	+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
																			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
									        	}
									
												if (schedules.getScheduleType() == null || schedules.getScheduleType().getParameterId() == null) {
													setTabValue("SC");
													throw new EpException(MessageConst.ERROR_100022, 
															new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.type", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												}
									}     	
							
							    } else if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() == null || ctBB.getManagedBean().getContractVer().getCtProgressivePayments().size() < 2 
											&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()
											&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
										setTabValue("SC");
										throw new EpException(MessageConst.ERROR_750022);
							    }
							
							    if (ctBB.getManagedBean().getContractVer().getCtProgressivePayments() != null && !ctBB.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty() 
											&& !ctBB.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
										float percentage = 0.0f;
										for (CtProgressivePayment progressivePayment : ctBB.getManagedBean().getContractVer().getCtProgressivePayments()) {
											percentage = percentage + progressivePayment.getPercentage();
											if (progressivePayment.getPhaseNo()== null) {
												setTabValue("SC");
												throw new EpException(MessageConst.ERROR_100022,
														new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
																+ " " + ResourceBundleUtils.getMessage("lbl.ct.phase", 
																		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
											}
									
											if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {
													setTabValue("SC");
								           			throw new EpException(MessageConst.ERROR_100046, 
								           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.phase", 
								           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												} else {
													schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
												}
									
											if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {
													setTabValue("SC");
													throw new EpException(MessageConst.ERROR_100022,
															new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
																	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
																	+ " " + ResourceBundleUtils.getMessage("lbl.ct.description", 
																			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
												}
									
											if (progressivePayment.getStartMonth() == null) {
												setTabValue("SC");
												throw new EpException(MessageConst.ERROR_100022, 
														new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
																ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
																+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
																		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
											}
									
											if (progressivePayment.getEndMonth() == null) {
													setTabValue("SC");
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
											setTabValue("SC");
											throw new EpException(MessageConst.ERROR_100022, 
						           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.percentage.progressive", 
						           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
										}
							     }
						   }
		
										// Validation for AP178 agency
						if (ctBB.getManagedBean().getContractVer().getCtAgencies().size()>0) {
							double ceilingAmt = 0.0;
							DecimalFormat df = new DecimalFormat("#.00");
							for (CtAgency ctAgency : ctBB.getManagedBean().getContractVer().getCtAgencies()) {
								ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
								if (ctAgency.isAp178() == true && (ctAgency.getFileName() == null  || ctAgency.getFileName().equalsIgnoreCase(""))) {
									setTabValue("AG");
									throw new EpException(MessageConst.ERROR_750008); 
									// Approval document is mandatory for AP178 agency. Please upload the document.
								}
								
								if (ctAgency.isAp178() == true && (ctAgency.getRefNo() == null  || ctAgency.getRefNo().equalsIgnoreCase(""))) {
									setTabValue("AG");
									throw new EpException(MessageConst.ERROR_100061, 
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
								if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
									setTabValue("IT");
								}else{
									setTabCentralValue("IT");
								}
								throw new EpException(MessageConst.ERROR_750007, 
										new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
							}
						}			
						
						if (ctBB.getManagedBean().getContractVer().getCtContractItems() != null) {
							for (CtContractItem item : ctBB.getManagedBean().getContractVer().getCtContractItems()) {
								// Validation for minimum order quantity
								if (item.getMinimumOrderQty() <= 0) {
									item.setMinimumOrderQty(1);
									if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
										setTabValue("IT");
									} else {
										setTabCentralValue("IT");
									}
									throw new EpException(MessageConst.ERROR_100033,
											new String[]{ResourceBundleUtils.getMessage("lbl.ct.min.order.qty",
													ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
								} 
								
								// Validation for ceiling quantity
								double totalCeilingQuantity = 0.0;
								if (item.getCtItemMaxQties().size() != 0) {						
									for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
										totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
									}
									if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
										if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
											setTabValue("IT");
										}else{
											setTabCentralValue("IT");
										}
										throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
									}
								}
							}
						}
		
						// Validation for Approver
						if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting() 
								&& getManagedBean().getApproverModel().getTarget().size() == 0) {
							if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
								setTabValue("SE");
							}else{
								setTabCentralValue("SE");
							}
							throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
						}
	
										// Validation for AP201 exempt
						if (ctBB.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
								&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
								&& (ctBB.getManagedBean().getContractVer().getCtContractValue().getAp201FileName() == null 
								||ctBB.getManagedBean().getContractVer().getCtContractValue().getAp201FileName().equalsIgnoreCase("")) 
								&& ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
							getManagedBean().setSelectedTabIndex(0);
							throw new EpException(MessageConst.ERROR_100022, 
				   					new String[] {ResourceBundleUtils.getMessage("lbl.ct.attachment.ap201", 
				   							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
						}
		
						// Validation for performance bond exempted
						if (ctBB.getManagedBean().getCtContractAmount().isBondReq() 
								&& !ctBB.getManagedBean().isDirectPurchase()
								&& ctBB.getManagedBean().getCtContractAmount().isBondExempted()
								&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
								&& (ctBB.getManagedBean().getCtContractAmount().getBondExemptedFileName() == null 
								|| ctBB.getManagedBean().getCtContractAmount().getBondExemptedFileName().equalsIgnoreCase(""))) {
							getManagedBean().setSelectedTabIndex(0);
							throw new EpException(MessageConst.ERROR_100022, 
				   					new String[] {ResourceBundleUtils.getMessage("lbl.ct.bond.exempted.letter", 
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
		if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome) || BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
			   validateCtContractVer(outcome);
			   //Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			    StringBuilder sb = new StringBuilder();
			    sb.append(ctBB.getManagedBean().getContractVer().getCtContract().getContractNo());
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(ctBB.getManagedBean().getContractVer().getDocType());
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(ctBB.getManagedBean().getContractVer().getCtContractValue().getEffDate());
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(ctBB.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(ctBB.getManagedBean().getCtContractAmount().getContractAmount());
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(outcome);		
			    sb.append(ContractConst.SEPRATE_LINE);
			    sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());		
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
    public void setTabCentralValue(String outputTab) {		
		getManagedBean().setItemIndex(11);
		getManagedBean().setFactoring(11);
		getManagedBean().setSettingIndex(11);		
		getManagedBean().setPaymentTracking(11);
		getManagedBean().setBondIndex(11);
		ctBB.getManagedBean().setSelectAll(false);
		
		boolean typeZonalPrice = false;
		boolean typeFactoring = false;
		boolean typeLoaAmt = false;
		boolean bondRequired = false;
		
		/*Set value true if contract is zonal*/
		if(ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
			typeZonalPrice = true;
		}
		
		if(ctBB.isPerformanceBond()){
		     bondRequired = true;
	    }
		
		/*Set value true if contract is factor*/
		if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored()  && checkFactoringExpiryDate()){
			typeFactoring = true;
		}
		
		/*Set value true if contract follow loa amount*/
		if (ctBB.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()){
			typeLoaAmt = true;
		}
		
		if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
			getManagedBean().setSelectedTabIndex(0);
		} 
		
		/*Set tab value if contract is zonal*/
		if(typeZonalPrice){
		
			 if (outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
				     getManagedBean().setSelectedTabIndex(1);
			 
			 } else if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
					getManagedBean().setSelectedTabIndex(2);
					getManagedBean().setItemIndex(2);
		    
			 } else if(bondRequired && outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL)){
				     getManagedBean().setSelectedTabIndex(3);
					getManagedBean().setBondIndex(3);
			 } else if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
				     if(bondRequired){
				    	  getManagedBean().setSelectedTabIndex(4);
				     } else {
				    	  getManagedBean().setSelectedTabIndex(3);
				     }
		  	  		 getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
		 
			 } else  if (typeLoaAmt && outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
					      if(typeFactoring && bondRequired){
					             getManagedBean().setSelectedTabIndex(5);
					      } else if(typeFactoring || bondRequired){
					    	  getManagedBean().setSelectedTabIndex(4);
					      }else {
					    	  getManagedBean().setSelectedTabIndex(3);
					      }
	  	     	   getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
		
		 	  } else if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
		 		 
	  			    if(bondRequired){
	  			    	 getManagedBean().setSelectedTabIndex(4);
	  			    	  if (typeFactoring && typeLoaAmt) {
	  			    		     getManagedBean().setSelectedTabIndex(6);
	  			    	  }else if(typeLoaAmt || typeFactoring){
			  				    getManagedBean().setSelectedTabIndex(5);
	  			           }
	  			    }else if(typeFactoring){
	  			    	getManagedBean().setSelectedTabIndex(4);
	  			    	if(typeLoaAmt){
	  			    		 getManagedBean().setSelectedTabIndex(5);
	  			    	}
	  			    } else if(typeLoaAmt){
	  			    	 getManagedBean().setSelectedTabIndex(4);
	  			    }else {
	  			    	 getManagedBean().setSelectedTabIndex(3);
	  			    }
	  			 getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
			  }	  
	 
	   /*Set tab value if contract is not zonal*/	 
		} else {
			
			 if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
					getManagedBean().setSelectedTabIndex(1);
					getManagedBean().setItemIndex(1);
			
			 } else if(bondRequired && outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL)){
				    getManagedBean().setSelectedTabIndex(2);
					getManagedBean().setBondIndex(2);

             } else if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
		  	    		if(bondRequired){
		  	    			 getManagedBean().setSelectedTabIndex(3);
		  	    		}else{
            	             getManagedBean().setSelectedTabIndex(2);
		  	    		}
		  	  		getManagedBean().setFactoring( getManagedBean().getSelectedTabIndex());
		      
			 } else  if (typeLoaAmt && outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
                       if(bondRequired && typeFactoring){
	  	    		         getManagedBean().setSelectedTabIndex(4);
	  	    	       } else if(bondRequired || typeFactoring){
	  	    	    	 getManagedBean().setSelectedTabIndex(3);
	  	    	       } else {
	  	    	    	 getManagedBean().setSelectedTabIndex(2);
	  	    	       }
	  	     	   getManagedBean().setPaymentTracking( getManagedBean().getSelectedTabIndex());
		   
			 } else if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
				    if(bondRequired){
	  			    	 getManagedBean().setSelectedTabIndex(3);
	  			    	  if (typeFactoring && typeLoaAmt) {
	  			    		     getManagedBean().setSelectedTabIndex(5);
	  			    	  }else if(typeLoaAmt || typeFactoring){
			  				    getManagedBean().setSelectedTabIndex(4);
	  			           }
	  			    }else if(typeFactoring){
	  			    	getManagedBean().setSelectedTabIndex(3);
	  			    	if(typeLoaAmt){
	  			    		 getManagedBean().setSelectedTabIndex(4);
	  			    	}
	  			    } else if(typeLoaAmt){
	  			    	 getManagedBean().setSelectedTabIndex(3);
	  			    }else {
	  			    	 getManagedBean().setSelectedTabIndex(2);
	  			    }
					getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
				  }
		    }
		} 
	
    /**
	 * The method used for controlling the tab menu
	 * of ministry contract dynamically.
	 * 
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
	public void setTabValue(String outputTab) {	 
			getManagedBean().setItemIndex(11);
			getManagedBean().setDeductionIndex(11);
			getManagedBean().setFactoring(11);
			getManagedBean().setScheduleIndex(11);
			getManagedBean().setPaymentTracking(11);
			getManagedBean().setSettingIndex(11);
			getManagedBean().setAgencyIndex(111);
			getManagedBean().setBondIndex(113);
			ctBB.getManagedBean().setSelectAll(false);
			
			boolean typeZonalPrice = false;
			boolean typeSchedule = false;
			boolean typeFactoring = false;
			boolean bondRequired = false;
			
			/*Set value true if contract is zonal*/
			if(ctBB.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
				typeZonalPrice = true;
			}
			
			if(ctBB.isPerformanceBond()){
				     bondRequired = true;
			}
			
			/*Set value true if contract is schedule*/
			if(ctBB.getManagedBean().getContractVer().getCtContract().getFulfillmentType().
					getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
				typeSchedule = true;
			}
			
			/*Set value true if contract is factor*/
			if(ctBB.getManagedBean().getContractVer().getCtContract().isFactored()  && checkFactoringExpiryDate()){
				typeFactoring = true;
			}
			
		  	if (outputTab.equalsIgnoreCase(ContractConst.FULFILMENT_DETAIL)) {
				getManagedBean().setSelectedTabIndex(0);
			}
			  	
		    /*Setting tab value if zonal is true*/
		  	if(typeZonalPrice) {
				  	
		  		  if (outputTab.equalsIgnoreCase(ContractConst.ZONAL_DETAIL)) {
					  		getManagedBean().setSelectedTabIndex(1);
				  }
			  		
				  if(outputTab.equalsIgnoreCase(ContractConst.AGENCY_DETAIL)) {
					   getManagedBean().setSelectedTabIndex(2);
					   getManagedBean().setAgencyIndex(2);
				  }
			  		
		  		if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
			  		getManagedBean().setSelectedTabIndex(3);
			  		getManagedBean().setItemIndex(3);
				}
			  		
		  		if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
				       getManagedBean().setSelectedTabIndex(4);
				  	   getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
			   	} 
			  		
		  	 	if (outputTab.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) && typeSchedule) {
		  	  			  	getManagedBean().setSelectedTabIndex(5);
		  	  			   getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
		  	   	}
		  	 	
		  	 	if (outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL) && bondRequired) {
  	  			  	   getManagedBean().setSelectedTabIndex(5);
  	  			       if (typeSchedule) {
	  					    getManagedBean().setSelectedTabIndex(6);
	  	                }
  	  			     getManagedBean().setBondIndex(getManagedBean().getSelectedTabIndex());
  	        	}
			  	 	
		  		if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
		  		  	     getManagedBean().setSelectedTabIndex(5);
		  		  	     if (typeSchedule && bondRequired) {
		  		  					getManagedBean().setSelectedTabIndex(7);
		  		  	      } else if(typeSchedule || bondRequired){
		  		  	    	getManagedBean().setSelectedTabIndex(6);
		  		  	      }
		  		  	   getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
		  		}
			  		
		  		if (outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
		  			    getManagedBean().setSelectedTabIndex(5);
		  			    if(typeSchedule){
		  			    	 getManagedBean().setSelectedTabIndex(6);
		  			    	  if (typeFactoring && bondRequired) {
		  			    		     getManagedBean().setSelectedTabIndex(8);
		  			    	  }else if(bondRequired || typeFactoring){
				  				    getManagedBean().setSelectedTabIndex(7);
		  			           }
		  			    }else if(typeFactoring){
		  			    	getManagedBean().setSelectedTabIndex(6);
		  			    	if(bondRequired){
		  			    		 getManagedBean().setSelectedTabIndex(7);
		  			    	}
		  			    } else if(bondRequired){
		  			    	 getManagedBean().setSelectedTabIndex(6);
		  			    }
		  			 getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
		  	   }	
			  		
		  	    if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
	  			         getManagedBean().setSelectedTabIndex(6);
	  			       if(typeSchedule){
		  			    	 getManagedBean().setSelectedTabIndex(7);
		  			    	  if (typeFactoring && bondRequired) {
		  			    		     getManagedBean().setSelectedTabIndex(9);
		  			    	  }else if(bondRequired || typeFactoring){
				  				    getManagedBean().setSelectedTabIndex(8);
		  			           }
		  			    }else if(typeFactoring){
		  			    	getManagedBean().setSelectedTabIndex(7);
		  			    	if(bondRequired){
		  			    		 getManagedBean().setSelectedTabIndex(8);
		  			    	}
		  			    } else if(bondRequired){
		  			    	 getManagedBean().setSelectedTabIndex(7);
		  			    }
	  			       getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
		  	     }
	       
		  /*Setting tab value if zonal is false*/
		  } else {
			  		
					if(outputTab.equalsIgnoreCase(ContractConst.AGENCY_DETAIL)) {
					  		getManagedBean().setSelectedTabIndex(1);
					  		getManagedBean().setAgencyIndex(1);
				  	}
				  	
				  	if (outputTab.equalsIgnoreCase(ContractConst.ITEM_DETAIL)) {
					  		getManagedBean().setSelectedTabIndex(2);
					  		getManagedBean().setItemIndex(2);
				  	}
				
				  	if (outputTab.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
					        getManagedBean().setSelectedTabIndex(3);
					  	    getManagedBean().setDeductionIndex( getManagedBean().getSelectedTabIndex());
				   	} 
				  	
					if (outputTab.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) && typeSchedule) {
					  	     getManagedBean().setSelectedTabIndex(4);
					  		 getManagedBean().setScheduleIndex(getManagedBean().getSelectedTabIndex());
					}
					
					if (outputTab.equalsIgnoreCase(ContractConst.BOND_DETAIL) && bondRequired) {
	  	  			  	   getManagedBean().setSelectedTabIndex(4);
	  	  			       if (typeSchedule) {
		  					    getManagedBean().setSelectedTabIndex(5);
		  	                }
	  	  			     getManagedBean().setBondIndex(getManagedBean().getSelectedTabIndex());
	  	        	}
					
					if (typeFactoring && outputTab.equalsIgnoreCase(ContractConst.FACTORING_DETAIL)) {
						  getManagedBean().setSelectedTabIndex(4);
			  		  	     if (typeSchedule && bondRequired) {
			  		  					getManagedBean().setSelectedTabIndex(6);
			  		  	      } else if(typeSchedule || bondRequired){
			  		  	    	getManagedBean().setSelectedTabIndex(5);
			  		  	      }
			  		  	   getManagedBean().setFactoring(getManagedBean().getSelectedTabIndex());
				   }

                 if (outputTab.equalsIgnoreCase(ContractConst.PAYMENT_DETAIL)) {
                	  getManagedBean().setSelectedTabIndex(4);
		  			    if(typeSchedule){
		  			    	 getManagedBean().setSelectedTabIndex(5);
		  			    	  if (typeFactoring && bondRequired) {
		  			    		     getManagedBean().setSelectedTabIndex(7);
		  			    	  }else if(bondRequired || typeFactoring){
				  				    getManagedBean().setSelectedTabIndex(6);
		  			           }
		  			    }else if(typeFactoring){
		  			    	getManagedBean().setSelectedTabIndex(5);
		  			    	if(bondRequired){
		  			    		 getManagedBean().setSelectedTabIndex(6);
		  			    	}
		  			    } else if(bondRequired){
		  			    	 getManagedBean().setSelectedTabIndex(5);
		  			    }
		  			 getManagedBean().setPaymentTracking(getManagedBean().getSelectedTabIndex());
                   }
	  	
		  	    if (outputTab.equalsIgnoreCase(ContractConst.SETTING_DETAIL)) {
		  	    	 getManagedBean().setSelectedTabIndex(5);
	  			       if(typeSchedule){
		  			    	 getManagedBean().setSelectedTabIndex(6);
		  			    	  if (typeFactoring && bondRequired) {
		  			    		     getManagedBean().setSelectedTabIndex(8);
		  			    	  }else if(bondRequired || typeFactoring){
				  				    getManagedBean().setSelectedTabIndex(7);
		  			           }
		  			    }else if(typeFactoring){
		  			    	getManagedBean().setSelectedTabIndex(6);
		  			    	if(bondRequired){
		  			    		 getManagedBean().setSelectedTabIndex(7);
		  			    	}
		  			    } else if(bondRequired){
		  			    	 getManagedBean().setSelectedTabIndex(6);
		  			    }
	  			       getManagedBean().setSettingIndex(getManagedBean().getSelectedTabIndex());
		  	    }
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
		ContractReqDTO reqDTO = null;
		TerminationBackingBean trBB = null;
		
		try {
			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			
			reqDTO.setContractVerId(contractVerId);
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setUpdateAction(true);	
			trBB = new TerminationBackingBean();
			trBB.getManagedBean().setViewContract(false);
			trBB.getManagedBean().setButtonHideActive(true);
			trBB.getManagedBean().setExternalPageUrl(null);
			trBB.setTerminationContractVer(reqDTO);  
		} finally{
			contractSO = null;
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
		  
			 IContractSO	contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		     ContractReqDTO reqDTO  = new ContractReqDTO();   
		     reqDTO.setContractId(contractId);
			 Long  contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
		     reqDTO.setContractVerId(contractVerId);
			 reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_SUSPEND_ACTIVE);
			 reqDTO.setUpdateAction(true);
			
			 SuspensionBackingBean	suspBB = new SuspensionBackingBean();
		     suspBB.getManagedBean().setViewOnly(false);
			suspBB.setSuspensionContractVer(reqDTO);
			return PROCESS_SUSPEND_PAGE;
	}
	
	/**
	 * The method used to navigate to contract amendment screen
	 * for amendment purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoAmendPage(Long contractId) {
		
		     ContractReqDTO reqDTO = new ContractReqDTO();
			 IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			 reqDTO.setContractId(contractId);
			 Long contractVerId = contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId();
			 reqDTO.setContractVerId(contractVerId);
			 reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			 reqDTO.setUpdateAction(true);
			 
			AmendmentBackingBean amendBB = new AmendmentBackingBean();
			amendBB.getManagedBean().setSelectedTabIndex(0);
			//amendBB.getManagedBean().setDisableGstRadio(true);
			amendBB.setAmendmentContractVer(reqDTO);
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
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
		reqDTO.setViewOnly(true);
        ctBB.getManagedBean().setViewContract(true);
	    getManagedBean().setAsAtDate(null);
	    setValidateContractField(reqDTO);
	    getManagedBean().setSelectedTabIndex(0);
	    getManagedBean().setExternalPageUrl(null);
	    getManagedBean().setLoaContractVerId( ctBB.getManagedBean().getContractVer().getContractVerId());
	    reqDTO.setCtContractVer( ctBB.getManagedBean().getContractVer());
	    getManagedBean().setWarningPercentage(contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage());
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
		reqDTO.setContractVerId( contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
		reqDTO.setViewOnly(true); 
		reqDTO.setCommonView(true);
        ctBB.getManagedBean().setViewContract(true);
        ctBB.getManagedBean().setCommonView(true);
	    setValidateContractField(reqDTO);
	    getManagedBean().setSelectedTabIndex(0); 
	    getManagedBean().setAsAtDate(DateUtils.getCurrentDateWithoutTime());
	    reqDTO.setCtContractVer(ctBB.getManagedBean().getContractVer());
	    getManagedBean().setWarningPercentage(contractSO.checkAdvancedPaymentProgress(reqDTO).getPercentage());
	    
	    FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CONTRACT_COMMON_PAGE);
	    
		return null;
	}
	
	/**
	 * The method used to navigate to contract update screen
	 * for update purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoUpdatePage(Long contractId, int agStatus) {
		IContractSO contractSO = null;
		
		try {
			
			     contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			     ContractReqDTO reqDTO = new ContractReqDTO();
			     reqDTO.setContractId(contractId);	
			     reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
			     if (agStatus == 1) {
				        reqDTO.setDocType(ParameterConst.DOCTYPE_AMENDMENT_WITHOUT_SUPPLEMENT);
			     } else {
				       reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE);
			     }
		
			     reqDTO.setUpdateAction(true);
		         ctBB.getManagedBean().setViewContract(false);
		         ctBB.getManagedBean().setUpdateContract(true);
		         setValidateContractField(reqDTO);
		         getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		         ctBB.chkServiceTaxAlert();
	             getManagedBean().setExternalPageUrl(null);
	             getManagedBean().setSelectedTabIndex(0);	     
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
    	ctReqDTO.setContractVerId(contractSO.getLatestContractVer(ctReqDTO).getCtContractVer().getContractVerId());
	
    	ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		TerminationBackingBean trBB =  new TerminationBackingBean();
		trBB.setCtBB(new ContractBackingBean());	
		trBB.getCtBB().getManagedBean().setViewContract(true);
		trBB.getCtBB().getManagedBean().setContractVer(resDTO.getCtContractVer());
		trBB.getManagedBean().setExternalPageUrl(null);
		
		if (ctBB.isContractWithGst(trBB.getCtBB().getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
			if (trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
				trBB.getCtBB().getManagedBean().getContractVer().getCtContractAmount().setContractAmount(BigDecimal.ZERO);
			}
			trBB.getCtBB().getManagedBean().setGstCheckingGUI(true);
	    } else {
	    	trBB.getCtBB().getManagedBean().setGstCheckingGUI(false);
	    }
		trBB.retrieveContractVerOfAgreement();
		trBB.getManagedBean().setTerminationList(contractSO.retrieveTerminationHistoryList(ctReqDTO).getTerminationHistoryList());		
		return TERMINATE_HISTORY_PAGE;
	}
	
	/**
	 * The method used to navigate to change bank account screen
	 * for change bank purpose.
	 * 
	 * @return the navigate URL 
	 */
	public String gotoChangeBankPage(Long contractId) {
		
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO = new ContractReqDTO();
			reqDTO.setContractId(contractId);
            reqDTO.setContractVerId(contractSO.getLatestContractVer(reqDTO).getCtContractVer().getContractVerId());
            //reqDTO = new ContractReqDTO();
            reqDTO.setDocType(ParameterConst.DOCTYPE_CHANGE_BANK_ACCOUNT);
            reqDTO.setUpdateAction(true);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.setChangeBankDetails(reqDTO);
			//supplierBB.renewSesionObject();
		} finally {
			reqDTO  = null;
			contractSO   = null;
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
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setContractId(contractId);
			
			resDTO = contractSO.retrieveFactoringDetailsByContractId(reqDTO);
			SupplierBackingBean supplierBB =  new SupplierBackingBean();
			supplierBB.renewSesionObject();
			if (resDTO.isSuccessful()) {
			     supplierBB.getManagedBean().setCtContract(resDTO.getContract());
			     if (resDTO.getContract().getLatestContractVer() != null) {
			    	 supplierBB.getManagedBean().setContractVer(resDTO.getContract().getLatestContractVer());
			     } else {
			    	 supplierBB.getManagedBean().setContractVer(resDTO.getContract().getCurrentContractVer());
			     }		     
				
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
					supplierBB.getManagedBean().setAccBankName(null);
					supplierBB.getManagedBean().setAccBankName(
							supplierBB.getManagedBean().getFactoring().getBankAccountNo()
								+ ContractConst.SPACE_SLASH 
								+ supplierBB.getManagedBean().getFactoring().getBankName());
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
	 * @throws IOException 
	 */
	public String gotoBackPage() throws IOException {
		try {
		             String url = getManagedBean().getGotoPreviewPage();
		             Long contractId =  ctBB.getManagedBean().getContractVer().getCtContract().getContractId();
		             boolean isTrue =  getManagedBean().isBackPageAmendment();
		             String taskId = getManagedBean().getTaskId();
	                renewSesionObject();
	                if(isTrue && taskId == null){
	    	               return gotoAmendPage(contractId);
	                 }else if (isTrue && taskId != null){
	    		              FacesScopes facesScope = new FacesScopes();
				              ThemeDisplay themeDisplay = (ThemeDisplay) facesScope.getRequestScope(WebKeys.THEME_DISPLAY);
				              String commonUrl = themeDisplay.getURLCurrent().substring(
						           0,themeDisplay.getURLCurrent().indexOf(themeDisplay.getLayout().getGroup()
								.getFriendlyURL())	+ themeDisplay.getLayout().getGroup().getFriendlyURL().length());
			                 	System.out.println("commonUrl" + commonUrl);

				               ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				               externalContext.redirect(commonUrl + "/" +ContractConst.NAV_TO_CONTRACT_AMENDMENT + "?taskId="+ taskId);
	     }	else{
	
	    		return url;
	     }
		} catch (PortalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
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
			if (isSupplierRole()) {
				items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.bpm.all", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			} else {
				items[i++] = new SelectItem(0, ResourceBundleUtils.getMessage("lbl.txt.select", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			}
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
		} else if (status == ContractConst.STATUS_INCOMPLETE) {
			return ResourceBundleUtils.getMessage("lbl.ct.incomplete", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			return ResourceBundleUtils.getMessage("lbl.ct.not.applicable", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		}
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
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
	public String getViewPortletTitle() {
		String portletTitle = null;
		if (ctBB.getManagedBean().isViewContract()) {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.view.contract",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		} else {
			portletTitle = ResourceBundleUtils.getMessage("lbl.ct.menu.contract.maintenance",
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
		String userRole = null;		
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME)) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();			
			}
		} else if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FD_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME))) {
			if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (ctBB.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
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
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FD_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME))) {
			return true;
		} else {
			return false;
		}
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
   	 * The method used to disable the ministry lov
   	 * if login user is central contract administrator
   	 * or ministry administrator
   	 * or internal auditor.
   	 * 
   	 * @return true, if login user is central contract administrator/ministry administrator/internal auditor
   	 *         false, if login user is not central contract administrator/ministry administrator/internal auditor
	 */
    public boolean disableMinistryLov() {
    	return (isContractAdmin() || (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value()) 
				&& !getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())));
    }
     
    /**
   	 * The method used to set the ministry 
   	 * profile id and PTJ profile id
   	 */
    private void defaultMinistryPtjSearchValue() {
    	if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value())
    			|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
    		this.getManagedBean().setSearchMinistryProfile(null);
    		this.getManagedBean().setSearchPtjGroupProfile(null);
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
				this.getManagedBean().setSearchMinistryProfile(orgProfile);
				this.getManagedBean().setSearchPtjGroupProfile(null);
				this.getManagedBean().setSearchPtjProfile(null);
			} else if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
					|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				this.getManagedBean().setSearchMinistryProfile(retrieveMinistryOrgProfile(orgProfile.getOrgProfileId()));
				this.getManagedBean().setSearchPtjGroupProfile(retrieveJabatanOrgProfile(orgProfile.getOrgProfileId()));
				if (this.getManagedBean().getSearchPtjGroupProfile().getOrgProfileId() == -1) {
					this.getManagedBean().setSearchPtjGroupProfile(null);
				}
				this.getManagedBean().setSearchPtjProfile(orgProfile);
			}
		}
    }
    
    /**
   	 * Get PTJ Group condition for construct the LOV listing
   	 * @return String
   	 */
   	public String getPTJGroupCondition() {
   		String condition = OrgProfileUtil.getFromToSearchBeanParentString(
   				OrganizationType.DEPARTMENT.value(), OrganizationType.MINISTRY.value())+"^"+
   				this.getManagedBean().getSearchMinistryProfile().getOrgProfileId();
   		return condition;
   	}
	
	/**
	 * Get PTJ condition for construct the LOV listing
	 * @return String
	 */
	public String getPtjCondition() {
		String condition = null;
		
		if (this.getManagedBean().getSearchPtjGroupProfile() != null) {
			condition = OrgProfileUtil.getFromToSearchBeanParentString(
	   				OrganizationType.DIVISION.value(), OrganizationType.DEPARTMENT.value())+"^"+
	   				this.getManagedBean().getSearchPtjGroupProfile().getOrgProfileId();
		} else {
			condition = OrgProfileUtil.getSearchBeanParentString(OrganizationType.MINISTRY.value())+"^"+
				this.getManagedBean().getSearchMinistryProfile().getOrgProfileId();
		}
		return condition;
	}
	
	/**
	 * The method used reset PTJ/PTJ Group profile value once select the Ministry
	 */
	public void resetPtjGroupProfile() {
		getManagedBean().setSearchPtjProfile(null);
		getManagedBean().setSearchPtjGroupProfile(null);
	}
	
	/**
	 * The method used reset PTJ profile value once select the Ministry
	 */
	public void resetPtjProfile() {
		getManagedBean().setSearchPtjProfile(null);
	}
	
		/**
		 * The method used for generating a list of available history of
		 * loa contract version as selection.
		 * @return loaContractVerList  the selection list
		 */
	     public SelectItem[] getLoaContractVerList() {
		       if(getManagedBean().getLoaContractVerList() == null ) 
		           {
					  SelectItem[] items = null;
					  IContractSO contractSO = null;
			          ContractReqDTO reqDTO = null;
			          ContractResDTO resDTO = null;
                      try {
	        	              int i = 0;
	        	              if(ctBB.getManagedBean().getContractVer().getCtContract().getLatestContractVer() != null) 
	        	                 {	        		
	        		                contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);	
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
    	 if (getManagedBean().getLoaContractVerId() != null)
    	 {
    		 ContractReqDTO reqDTO = new ContractReqDTO();
    		 reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_UPDATE); // View purpose, FU or FW also fine
    		 reqDTO.setViewOnly(true);
    		 reqDTO.setContractVerId(getManagedBean().getLoaContractVerId());
    		 ctBB.getManagedBean().setViewContract(true);
    		 getManagedBean().setAsAtDate(null);
    		 setValidateContractField(reqDTO);
    	 }
     }

}