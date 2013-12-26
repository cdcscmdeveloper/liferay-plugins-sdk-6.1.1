/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;


import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.jaxb.ct.suspension.CTContractSuspensionDataType;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.suspend.CtSuspendActive;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmCode;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.agreement.AgreementBackingBean;
import my.ep.web.backingbean.ct.supplier.SupplierBackingBean;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013
 */
@ManagedBean (name = "contractDetailsBackingBean")
@RequestScoped
public class ContractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml";
	private static final String PROCESS_SUSPEND_PAGE = "/xhtml/ct/fulfilmentdetails/suspension/suspendTab.xhtml";
	private static final String PROCESS_AMEND_PAGE = "/xhtml/ct/fulfilmentdetails/amendment/amendmentTab.xhtml";
	private static final String PROCESS_VIEW_PAGE = "/xhtml/ct/agreement/shared/sidemenu/agreementViewTab.xhtml";
	private static final String PROCESS_SUPPLIER_TERMINATE_PAGE = "/xhtml/ct/supplier/termination/tabView.xhtml";
	private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml";
	private static final String PROCESS_BANK_PAGE = "/xhtml/ct/supplier/bank/tabView.xhtml";
	private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml";
	
	private TreeNode rootNode;
	private TreeNode selectedNode;
	private TreeNode rootNodeAgency;
	private CtContractVer[] selectedContractList;
	private CtDeductionClause[] chkDeductionClause;
	private String editorValue;
	
	private String fulfillmentNo = "01234567889";
	
	private final static String[] deductions;
	static {

		deductions = new String[6];
		deductions[0] = "Royalty";
		deductions[1] = "Penalty";
		deductions[2] = "Advanced Payment";
		deductions[3] = "Fixed";
		deductions[4] = "Non-Fixed";
		deductions[5] = "Others";
	}
	
	
	public String[] getDeductions() {
		return deductions;
	}
	
	public ContractDetailsBackingBean(){
		rootNode = new DefaultTreeNode("root", null);
		rootNodeAgency = new DefaultTreeNode("root", null);
		
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT });
		}
		
		if (this.getManagedBean().getApproverModel() == null) {
			List<String> source = getAvailableApproverList();
			List<String> target = new ArrayList<String>();
			this.getManagedBean().setApproverModel(new DualListModel<String>(source, target));
		}
		
		if(this.getManagedBean().getContractVer() == null){
			
			this.getManagedBean().setContractVer(new CtContractVer());
			this.getManagedBean().getContractVer().setCtContract(new CtContract());
			this.getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
		}

		if(getManagedBean().getUserGroupMap()==null) {
			getManagedBean().setUserGroupMap(new HashMap<Long, PmUserGroup>());
		}
		if(getManagedBean().getContractVer().getPmUserGroup()==null) {
			PmUserGroup pmUserGroup = new PmUserGroup();
			getManagedBean().getContractVer().setPmUserGroup(pmUserGroup);
		}
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void initializeContractValue() throws ParseException {
		 
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
			
			if(this.getManagedBean().getTermination() == null){
				 System.out.println("inside constructor termination");
				   this.getManagedBean().setTermination(new CtTermination());
				   this.getManagedBean().getTermination().setCaRecommendation(new PmCode());
				   this.getManagedBean().getTermination().setCtContractVer(new CtContractVer());
				
				
				   this.getManagedBean().getTermination().getCaRecommendation().setCodeId(155L);
			 }
			
		}
	
    public void preRenderView(){
	
	try{	
		System.out.println("**********preRenderView**************");
	//	BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		System.out.println("taskId:"+taskId);
		if(taskId==null) {
			return;
		}
		FacesScopes face = new FacesScopes();
		String token =face.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		BpmRequestDTO reqDTO = new BpmRequestDTO();


		reqDTO.setCtxToken(token);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		Object obj = res.getPojo();
		ContractReqDTO ctReqDTO = new ContractReqDTO();
		
		if(res.getProcessName().equalsIgnoreCase("Contract Creation")){
			CTContractDataType dataType = (CTContractDataType)obj;
			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
		}
		
		else if(res.getProcessName().equalsIgnoreCase("Contract Activation Suspension Request")){
			CTContractSuspensionDataType dataType = (CTContractSuspensionDataType)obj;
			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			
		
		} else if(res.getProcessName().equalsIgnoreCase("Contract Termination")){
			CTContractTerminationDataType dataType = (CTContractTerminationDataType)obj;
			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
		
		} else if(res.getProcessName().equalsIgnoreCase("Contract Amendment Request")){
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
			System.out.println("dataType.getContractId()>>"+dataType.getContractId());
			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
		}

		IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
		ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);


		this.getManagedBean().setTaskName(res.getTaskName());
		this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		this.getManagedBean().setContractVer(resDTO.getCtContractVer());
		this.getManagedBean().setTaskId(taskId);
		this.getManagedBean().setAction(res.getAction());
		
		if(res.getProcessName().equalsIgnoreCase("Contract Creation")){
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
	
		}else if(res.getProcessName().equalsIgnoreCase("Contract Activation Suspension Request")){
			
			FacesContext fc = FacesContext.getCurrentInstance();
			this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_SUSPENSION);
		
		}else if(res.getProcessName().equalsIgnoreCase("Contract Termination")){
			
			FacesContext fc = FacesContext.getCurrentInstance();
			this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_TERMINATION);
		
		}else if(res.getProcessName().equalsIgnoreCase("Contract Amendment Request")){
			  this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
			  
			  if(res.getTaskName().equalsIgnoreCase("Confirm Amendment Meeting") || res.getTaskName().equalsIgnoreCase("Update Amendment Result")){
				   FacesContext fc = FacesContext.getCurrentInstance();
				   this.getManagedBean().setAmendAckw(true);
				   this.getManagedBean().setAmendDisable(true);  
			       fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
		
			  }if(res.getTaskName().equalsIgnoreCase("Prepare LOA")){
				  
				  FacesContext fc = FacesContext.getCurrentInstance();
				   this.getManagedBean().setAmendDisable(false);  
				   this.getManagedBean().setApprovrLetter(true);
				   this.getManagedBean().setAmendAckw(true);
			       fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
				 
			  } if(res.getTaskName().equalsIgnoreCase("View Amendment Details")){
				  
				  FacesContext fc = FacesContext.getCurrentInstance();
			      fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
			
			  }if(res.getTaskName().equalsIgnoreCase("Sign Off")){
				  
				  FacesContext fc = FacesContext.getCurrentInstance();
				  this.getManagedBean().setAmendAckw(true);
				  this.getManagedBean().setAmendDisable(false);  
			      fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
				  
			  }if(res.getTaskName().equalsIgnoreCase("Update Amendment Details")){
				  FacesContext fc = FacesContext.getCurrentInstance();
				  this.getManagedBean().setApprovrLetter(true);
			      fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT);
				  
			  }  if(res.getTaskName().equalsIgnoreCase("Review Breached Amendment Request")){
				  FacesContext fc = FacesContext.getCurrentInstance();
				  this.getManagedBean().setAmendAckw(true);
				  fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
			  } else{
			
			       FacesContext fc = FacesContext.getCurrentInstance();
			       fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_AMENDMENT);
			}
		}else{
		
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "navigation");
		}
      } catch(Exception e){
			e.printStackTrace();
		}

	}
    
    /*Implementation for item details*/
    public List<SelectItem> getItemDetailsMethod() {

		List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("Item Pricing", "Item  Pricing"));
		list.add(new SelectItem("Item Ceiling Quantity", "Item Ceiling Quantity"));
		return list;
	}
    
    public List<SelectItem> getItemTypeMethode(){
    	
    	List<SelectItem> list = new ArrayList<SelectItem>();
		list.add(new SelectItem("Item", "Item"));
		list.add(new SelectItem("Service", "Service"));
		return list;
    }
    
    public void changeSuspensionType(){
    
       try {
			flush();
    	    if(this.getManagedBean().getSuspensionType().equalsIgnoreCase("Contract Activation")){
			    this.getManagedBean().setActivation(true);
			
		  }else if(this.getManagedBean().getSuspensionType().equalsIgnoreCase("Contract Suspension")){
			this.getManagedBean().setSuspension(true);
			
		  }
       }catch (Exception e) {
			System.out.println(e);
		}
    }
    public void changeField() {

		try {
			flush();
			if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Pricing")) {
				this.getManagedBean().setItemPricing(true);

			} else if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Ceiling Quantity")) {
				this.getManagedBean().setItemCeilingQuantity(true);

			}else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("155")) {
				this.getManagedBean().setTerminateWarningLetter(true);

			} else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("154")) {
				this.getManagedBean().setTerminateNotice(true);

			} else if (this.getManagedBean().getTermination().getCaRecommendation().getCode().equalsIgnoreCase("156")) {
				this.getManagedBean().setTerminateRejectLetter(true);

			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
    
    public void changeItemTypes(){
    	
    	try{
    		flushItem();
    		if (this.getManagedBean().getItemType().equalsIgnoreCase("Service")) {
				this.getManagedBean().setServiceItem(true);

			} else if (this.getManagedBean().getItemType().equalsIgnoreCase("Item")) {
				this.getManagedBean().setProductItem(true);

			}
    	} catch (Exception e) {
			System.out.println(e);
		}
    }
    
    public void flushItem(){
    	this.getManagedBean().setServiceItem(false);
    	this.getManagedBean().setProductItem(false);
    	this.getManagedBean().setItemPricing(false);
    	this.getManagedBean().setItemCeilingQuantity(false);
    }
  
   public void ChkAmendmentAction(){
    	
    	try {
			if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend()){
				this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(true);
				/*this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);*/
			
			}else if(this.getManagedBean().getContractVer().getCtAmendment().isPriceChange()){
				this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(true);
				/*this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(false);
				this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);*/
		
			}else if(this.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()){
	
				this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(true);
				/*this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(false);
				this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);*/
			
			}else if(this.getManagedBean().getContractVer().getCtAmendment().isClauseChange()){
				this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(true);
				/*this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(false);
				this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(false);
				this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);*/
			
			}else if(this.getManagedBean().getContractVer().getCtAmendment().isValueChange()){
				this.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
				/*this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(false);
				this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(false);
				this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(false);
		    	this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(false);*/
			}
    	   } catch (Exception e) {
			System.out.println(e);
		}
    }
   public void selectedItemList(){
    	  this.getManagedBean().setServiceItem(false);
    	  this.getManagedBean().setProductItem(true);
    }
    
   public void  selectedServiceList(){
	   this.getManagedBean().setServiceItem(true);
 	  this.getManagedBean().setProductItem(false);
    }
    
    private void flushDiscip() {
    	this.getManagedBean().setDisciplinaryValue(false);
    }
    public void ChkDisciplinaryAction(){
    	
    	try {
    		flushDiscip();
			if(this.getManagedBean().isDisciplinaryAction()){
				this.getManagedBean().setDisciplinaryValue(true);
			}
    	} catch (Exception e) {
			System.out.println(e);
		}
    }
    
    private void flush() {
    	
		this.getManagedBean().setItemPricing(false);
		this.getManagedBean().setItemCeilingQuantity(false);
		this.getManagedBean().setChkSchedule(false);
		this.getManagedBean().setChkProgressive(false);
		this.getManagedBean().setTerminateNotice(false);
		this.getManagedBean().setTerminateRejectLetter(false);
		this.getManagedBean().setTerminateWarningLetter(false);
		this.getManagedBean().setSuspension(false);
		this.getManagedBean().setActivation(false);
	}
    
    public void onRowSelect(SelectEvent event) {
    	
    }/*End*/
    
    /*Deduction component start*/
    public void addDeductionDetails() {
		CtDeduction ctDeductions = new CtDeduction();
		this.getManagedBean().getContractVer().getCtDeductions().add(ctDeductions);
	}

	public void deleteDeductionDetails() {
		this.getManagedBean().getContractVer().getCtDeductions().remove(this.getManagedBean().getCtDeductionDel());
	}
	
	public void addDeductionClause(ActionEvent event) {
		
		for (int i = 0; i < chkDeductionClause.length; i++) {
			CtDeductionClause ad = chkDeductionClause[i];
			this.getManagedBean().getCtDeductionClauseList().add(ad);
		}

		chkDeductionClause = null;
	}

	public void deleteClauseDetails() {
		this.getManagedBean().getCtDeductionClauseList().remove(this.getManagedBean().getCtDeductionClauseDel());
	}
	
	public void addDeductionClause() {

	}

    public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();
		if (newValue != null && !newValue.equals(oldValue)) {}
	} /*Deduction Clause End*/

    /*Schedule component start*/
    public void progressivePayment() {
		flush();
		if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("1"))
			this.getManagedBean().setChkSchedule(true);
		else if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("2"))
			this.getManagedBean().setChkProgressive(true);
	}
    
    public void addFrequencyDetails(){
		CtSchedule ctSchedules = new CtSchedule();
		this.getManagedBean().getContractVer().getCtSchedules().add(ctSchedules);	
	}
	
	public void deleteFrequencyDetails(){
		this.getManagedBean().getContractVer().getCtSchedules().remove(this.getManagedBean().getCtSchedulesDel());
		
	}

	public void addScheduleDetails() {
		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
		this.getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	public void deleteScheduleDetails() {
		this.getManagedBean().getContractVer().getCtProgressivePayments().remove(this.getManagedBean().getCtProgressivePaymentDel());
	}/*End schedule*/
	
	/*Termination start*/
	public void addTerminateReason() {
		CtTerminationReason terminateReason = new CtTerminationReason();
		this.getManagedBean().getTermination().getCtTerminationReasons().add(terminateReason);
	}

	public void deleteTerminateReason() {
		this.getManagedBean().getTermination().getCtTerminationReasons().remove(this.getManagedBean().getTerminationReason());
	}
	
	public void deleteTerminateCaluse() {
		this.getManagedBean().getTerminationClauseList().remove(this.getManagedBean().getTerminationClause());
	}

	/*End*/
	
	/* Suspension Start */
	 public List<SelectItem> getSuspentionTypes() {

			List<SelectItem> list = new ArrayList<SelectItem>();
			list.add(new SelectItem("Contract Activation", "Contract Activation"));
			list.add(new SelectItem("Contract Suspension", "Contract Suspension"));
			return list;
		}
	    
	
	/*Implementation of search methode*/
	public void searchContract() {

		ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
		SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
		if (this.getManagedBean().getCtLOANo() != null && !this.getManagedBean().getCtLOANo().equalsIgnoreCase("")) {
			System.out.println("==============="+this.getManagedBean().getCtLOANo());
			equalFilterBean.setSearchField("qtLoaId");
			equalFilterBean.setSearchValue(this.getManagedBean().getCtLOANo());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}
		if (this.getManagedBean().getCtName() != null && !this.getManagedBean().getCtName().equalsIgnoreCase("")) {
			equalFilterBean.setSearchField("contractName");
			equalFilterBean.setSearchValue(getManagedBean().getCtName());
			searchBean.getDataFilterBeans().add(equalFilterBean);
		}

		log.info("searchAction starts");
		IContractSO contractSO = null;
		try {

			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "dataTable"));
			reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
			
		}
	}
	
	public void searchAssociation() {

	/*	IContractAssociationSO contractAssociationSO = null;
		ISearchBean<CtAssociation> searchBean = new SearchBean<CtAssociation>();

		try {

			SearchFilterBean equalFilterBean = new SearchFilterBean(
					SearchOperator.OPERATOR_EQUALS.value());
			// SearchFilterBean likeFilterBean = new
			// SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());

			if (this.getManagedBean().getProjectName() != null) {
				equalFilterBean.setSearchField("projectName");
				equalFilterBean.setSearchValue(this.getManagedBean().getProjectName());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}

			if (this.getManagedBean().getProjectDescription() != null) {
				equalFilterBean.setSearchField("description");
				equalFilterBean.setSearchValue(getManagedBean().getProjectDescription());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setSearchBean(super.createSearchBean(CtAssociation.class,"associateSearch"));
			reqDTO.setSearchBean(searchBean);
			ContractAssociationResDTO ResDTO = contractAssociationSO.searchAssociation(reqDTO);
			this.getManagedBean().setAssociationList(ResDTO.getResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
			searchBean = null;
		} */
	}
	
	public void createAssociation() {

	/*	List<CtAssocContract> assocContract = new ArrayList<CtAssocContract>();
		CtAssocContract association = new CtAssocContract();
		IContractAssociationSO contractAssociationSO = null;

		try {

			this.getManagedBean().getAssociation().setProjectName(this.getManagedBean().getProjectName());
			this.getManagedBean().getAssociation().setDescription(this.getManagedBean().getProjectDescription());
			for (CtContract ctContract : this.getManagedBean().getAssociateSearchList()) {
				association.setCtContract(ctContract);
				association.setCtAssociation(this.getManagedBean().getAssociation());
				assocContract.add(association);
			}
			this.getManagedBean().getAssociation().setCtAssocContracts(assocContract);

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setAssociation(this.getManagedBean().getAssociation());
			contractAssociationSO.createAssociation(reqDTO);

		} catch (Exception ex) {
			log.error("Exception>>>" + ex);

		} finally {
			contractAssociationSO = null;
			assocContract = null;
			association = null;
		}*/

	}
	
	public void advanceContractSearch() {

	/*	log.info("searchAction starts");
		IContractSO contractSO = null;
		try {

			ISearchBean<CtContract> searchBean = new SearchBean<CtContract>();
			SearchFilterBean equalFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
			if (this.getManagedBean().getQtLoaNo() != null) {
				equalFilterBean.setSearchField("qtLoaId");
				equalFilterBean.setSearchValue(this.getManagedBean().getQtLoaNo());
				searchBean.getDataFilterBeans().add(equalFilterBean);
			}

			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			reqDTO.setContractSearchBean(super.createSearchBean(CtContract.class, "advanceCtSearch"));
			reqDTO.setContractSearchBean(searchBean);
			ContractResDTO ResDTO = contractSO.searchContract(reqDTO);
			this.getManagedBean().setContractList(ResDTO.getContractResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);
		} finally {
			contractSO = null;
		}*/
	}
	
	public void addAdvanceContract(ActionEvent event) {

		for (int i = 0; i < selectedContractList.length; i++) {
			CtContractVer ad = selectedContractList[i];
			this.getManagedBean().getAssociateSearchList().add(ad);
		}
		selectedContractList = null;
	}

	public void deleteAdvanceContract() {
		this.getManagedBean().getAssociateSearchList().remove(this.getManagedBean().getCtContract());
	}
	
	public void deleteAssociation() {

	/*	IContractAssociationSO contractAssociationSO = null;

		try {

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setAssociation(this.getManagedBean().getAssociation());
			log.info("deleteing value>>>>>>>"
					+ this.getManagedBean().getAssociation().getAssociationId());
			contractAssociationSO.deleteAssociation(reqDTO);

		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
		} */
	}

	
	/*Search methode finshed*/
	
	/* Testing purpose End */
	
    public String saveContract(String action){
		
	    IContractSO contractSO = null;
		try{
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
      
            if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE)){      
			    this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			    this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
          
            } else if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_TERMINATION)){
            	 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
            	 this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
          
            } else if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)){
		
            	 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
            	// this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
		 
            }else {
            	 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_CREATION);	
            	 this.getManagedBean().getContractVer().getCtContract().setAgreement(true);
            }
            
            reqDTO.setCtContract(this.getManagedBean().getCtContract());
            reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
			reqDTO.setTaskId(this.getManagedBean().getTaskId());
			reqDTO.setOutcome(action);
			reqDTO.setToken(token);
		    ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		    if (resDTO.isSuccessful()) {
				
				  log.info("Record saved successfully");
				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ 
				   this.getManagedBean().getContractVer().getContractVerId()));
					if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
							(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
						renewSesionObject();
						return "/xhtml/shared/acknowledge.xhtml";
				   }
			  } else {
					throw new EpException("Failed.");
				}
		} catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			contractSO = null;
		}
		return null;
		
	}
	
    private List<String> getAvailableApproverList() {
		List<String> availableApprovers = new ArrayList<String>(); 
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);

			UserReqDTO reqDTO = new UserReqDTO();
			//TODO
			reqDTO.setOrgProfileId(1250l);
			UserResDTO resDTO = userSO.retrieveUserGroupList(reqDTO);
			for(PmUserGroupUser user : resDTO.getUserGroupList().get(0).getPmUserGroupUsers()) {
				getManagedBean().getApproverMap().put(user.getPmUser().getUserName(), user);
				availableApprovers.add(user.getPmUser().getUserName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "Save failed."));
		} finally {
			userSO = null;
		}
		return availableApprovers;
	}

	
	public void userGroupChangeListener() {
		PmUserGroup selectedUserGroup = getManagedBean().getUserGroupMap().get(getManagedBean().getContractVer().getPmUserGroup().getUserGroupId());
		List<String> source = new ArrayList<String>(); 
        List<String> target = new ArrayList<String>();  
		for(PmUserGroupUser userGroupUser : selectedUserGroup.getPmUserGroupUsers()) {
			source.add(userGroupUser.getPmUser().getUserName());
		}
		getManagedBean().getApproverModel().setSource(source);
		getManagedBean().getApproverModel().setTarget(target);
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
	
	
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TreeNode getRootNodeAgency() {
		return rootNodeAgency;
	}

	public void setRootNodeAgency(TreeNode rootNodeAgency) {
		this.rootNodeAgency = rootNodeAgency;
	}

	public TreeNode getRootNode() {
		return rootNode;
	}

	public void setRootNode(TreeNode rootNode) {
		this.rootNode = rootNode;
	}

	public CtDeductionClause[] getChkDeductionClause() {
		return chkDeductionClause;
	}

	public void setChkDeductionClause(CtDeductionClause[] chkDeductionClause) {
		this.chkDeductionClause = chkDeductionClause;
	}

	public String getFulfillmentNo() {
		return fulfillmentNo;
	}

	public void setFulfillmentNo(String fulfillmentNo) {
		this.fulfillmentNo = fulfillmentNo;
	}
	
	
	
	public CtContractVer[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContractVer[] selectedContractList) {
		this.selectedContractList = selectedContractList;
	}

	public String getEditorValue() {
		return editorValue;
	}

	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	public String gotoTerminatePage() {

		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveContractVer(reqDTO);
				this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
				this.getManagedBean().setContractVer(resDTO.getCtContractVer());	
				if(this.getManagedBean().getContractVer().getCtTermination()==null) {
					this.getManagedBean().getContractVer().setCtTermination(new CtTermination());
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
			}
		
		return PROCESS_TERMINATE_PAGE;
	} 
	
	public String gotoSuspendPage() {
		
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		
		try {

			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
			resDTO = contractSO.retrieveContractVer(reqDTO);
			this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());	
			if(this.getManagedBean().getContractVer().getCtSuspendActive() == null){
				this.getManagedBean().getContractVer().setCtSuspendActive(new CtSuspendActive());
				this.getManagedBean().getContractVer().getCtSuspendActive().setSuspensionType(new PmCode());
				this.getManagedBean().getContractVer().getCtSuspendActive().getSuspensionType().setCodeId(1L);
				
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractSO = null;
			resDTO = null;
			reqDTO = null;
		}
		return PROCESS_SUSPEND_PAGE;
	}
	
	public String gotoAmendPage() {
		
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveContractVer(reqDTO);
				this.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
				this.getManagedBean().setContractVer(resDTO.getCtContractVer());	
				if(this.getManagedBean().getContractVer().getCtAmendment() ==null) {
					this.getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
			}
		return PROCESS_AMEND_PAGE;
	}
	
	public String gotoViewPage() {

		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		AgreementBackingBean aBB = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractId(this.getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveContract(reqDTO);
				aBB = new AgreementBackingBean();
				aBB.getManagedBean().setCtContract(resDTO.getContract());	
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
				aBB= null;
			}

		return PROCESS_VIEW_PAGE;
	}
	
	public String gotoBankPage(){
		
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		SupplierBackingBean sBB = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveContractVer(reqDTO);
				if(sBB == null){
				    sBB = new SupplierBackingBean();
				}
				sBB.getManagedBean().setChkRenderPage(ContractConst.DOC_TYPE_CHANGE_BANK_ACCOUNT);
				sBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
				this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
				sBB = null;
			}

		return PROCESS_BANK_PAGE;
	}
	
	public String gotoSupplierTerminatePage(){
		
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		SupplierBackingBean sBB = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveContractVer(reqDTO);
				if(sBB == null){
				    sBB = new SupplierBackingBean();
				}
				sBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
				sBB = null;
			}
		return PROCESS_SUPPLIER_TERMINATE_PAGE;
	}
	
	public boolean isContractAdmin() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value());
	}
	
	public boolean isSupplier() {
		FacesScopes facesScopes = new FacesScopes();
		return (facesScopes.hasRoleUser(RoleCode.SUPPLIER_ADMIN.value()) ||(facesScopes.hasRoleUser(RoleCode.SUPPLIER_USER.value())));
	}
	
	public String gotoAssociatePage() {
		return PROCESS_ASSOCIATE_PAGE;
	}
	
	public String gotoCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
	}
}
