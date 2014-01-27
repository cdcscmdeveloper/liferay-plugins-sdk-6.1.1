/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
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
import my.ep.biz.service.ct.maintain.IContractAssociationSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAssociationReqDTO;
import my.ep.dto.ct.maintain.ContractAssociationResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.assoc.CtAssocContract;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.agreement.AgreementBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.backingbean.ct.suspension.SuspensionBackingBean;
import my.ep.web.backingbean.ct.termination.TerminationBackingBean;
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
	private static final String PROCESS_ASSOCIATE_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectCreation.xhtml";
	private static final String PROCESS_ASSOCIATE_CANCEL_PAGE = "/xhtml/ct/fulfilmentdetails/association/projectAssociation.xhtml";
	
	private TreeNode rootNode;
	private TreeNode selectedNode;
	private TreeNode rootNodeAgency;
	private CtContract[] selectedContractList;
	private CtDeductionClause[] chkDeductionClause;
	private String editorValue;
	private boolean showZoneDetails;
	private String fulfillmentNo = "01234567889";
	private CtSchedule[] selectedSchedule;
	private CtProgressivePayment[] selectedProgressive;
	ContractBackingBean ctBB = null;
	
	private final static String[] deductions;
	static {

		deductions = new String[1];
		deductions[0] = "Advanced Payment";
	}
	
	
	public String[] getDeductions() {
		return deductions;
	}
	
	public ContractDetailsBackingBean(){
		rootNode = new DefaultTreeNode("root", null);
		rootNodeAgency = new DefaultTreeNode("root", null);
		
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT});
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
		 
		 ctBB = new ContractBackingBean();
		 ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
	}
	
    public void preRenderView(){
	
	     try{	
		      System.out.println("**********preRenderView**************");
		      HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		      HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		      String taskId = originalServletRequest.getParameter("taskId");
		      if(taskId==null) 
			    return;
		      
		      FacesScopes face = new FacesScopes();
		      String token =face.getBPMAuthenticationCtxToken();
		      IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		      BpmRequestDTO reqDTO = new BpmRequestDTO();
		      reqDTO.setCtxToken(token);
		      reqDTO.setTaskId(taskId);
		      
		      BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		      Object obj = res.getPojo();
		      this.getManagedBean().setTaskName(res.getTaskName());
		      this.getManagedBean().setAction(res.getAction());
		      this.getManagedBean().setTaskId(taskId);
		      
		      ContractReqDTO ctReqDTO = new ContractReqDTO();
		      CTContractDataType dataType = (CTContractDataType)obj;
			  ctReqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
			  ctReqDTO.setContractId(new Long(dataType.getContractId()));
	
		      IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
		      ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		      this.getManagedBean().setContractVer(resDTO.getCtContractVer());
		      this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	          //CombineSupAddress(resDTO.getCtContractVer());
	
			  FacesContext fc = FacesContext.getCurrentInstance();
			  fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		
         } catch(Exception e){
			e.printStackTrace();
	 	 }
	}
    
    public void selectedZoneValue(){
    	this.getManagedBean().setShowZoneDetails(true);
    	this.getManagedBean().setCtContractZoneDtls(this.getManagedBean().getSelectedCtZone().getCtContractZoneDtls());
    }
    
    public void CombineSupAddress(CtContractVer ctContractVer){
    	
    	StringBuilder address = new StringBuilder();
    	String strSpace = " ";
    	if(ctContractVer.getCtContractValue().getSupplierAddress().getAddress1() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getAddress1());
    		address.append(strSpace);
    	
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getAddress2() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getAddress2());
    		address.append(strSpace);
    	
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getAddress3() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getAddress3());
    		address.append(strSpace);
    	
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getAddress4() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getAddress4());
    		address.append(strSpace);
    		
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getPmDistrict().getDistrictName() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getPmDistrict().getDistrictName());
    		address.append(strSpace);
    	
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getPmState().getStateName() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getPmState().getStateName());
    		address.append(strSpace);
    	
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getPmCountry().getCountryName() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getPmCountry().getCountryName());
    		address.append("\n");
    	}if(ctContractVer.getCtContractValue().getSupplierAddress().getPostcode() != null){
    		address.append(ctContractVer.getCtContractValue().getSupplierAddress().getPostcode());
    	}
    	
    	this.getManagedBean().setSupplierAddress(address.toString());
    }
    
    /*Deduction component start*/
    public void addBondDetails() {
		CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
		this.getManagedBean().getContractVer().getCtPerformanceBonds().add(ctPerformanceBond);
	}
    
    public void delBondDetails() {
		
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
  
   public void selectedItemList(){
    	  this.getManagedBean().setServiceItem(false);
    	  this.getManagedBean().setProductItem(true);
    }
    
   public void  selectedServiceList(){
	   this.getManagedBean().setServiceItem(true);
 	  this.getManagedBean().setProductItem(false);
    }
    
    private void flush() {
    	
		this.getManagedBean().setItemPricing(false);
		this.getManagedBean().setItemCeilingQuantity(false);
		this.getManagedBean().setChkSchedule(false);
		this.getManagedBean().setChkProgressive(false);
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

    public void onEdit(RowEditEvent event){
    	
    }
    
    public void onCancel(RowEditEvent event){
    	
    }
    /*Schedule component start*/
    public void progressivePayment() {
		flush();
		if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("1"))
			this.getManagedBean().setChkSchedule(true);
		else if (this.getManagedBean().getScheduleOption().equalsIgnoreCase("2"))
			this.getManagedBean().setChkProgressive(true);
	}
    
    public void addScheduleDetails(){
		CtSchedule ctSchedules = new CtSchedule();
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
		this.getManagedBean().getContractVer().getCtProgressivePayments().add(ctProgressivePayment);
	}

	public void deleteFrequencyDetails() {
		for (int i = 0; i < selectedProgressive.length; i++) {
			CtProgressivePayment ctProgressivePayment = selectedProgressive[i];
			this.getManagedBean().getContractVer().getCtProgressivePayments().remove(ctProgressivePayment);
		}
		selectedProgressive = null;
	}/*End schedule*/
	
	
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
		if(this.getManagedBean().getCtNumber()!=null && this.getManagedBean().getCtNumber().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.docNo",SearchOperator.OPERATOR_EQUALS.value(),
			getManagedBean().getCtNumber()));	
		}
		if(this.getManagedBean().getCtName()!=null && this.getManagedBean().getCtName().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("contractName",SearchOperator.OPERATOR_LIKE.value(),
			this.getManagedBean().getCtName()));	
		}
		if(this.getManagedBean().getCtCircularNo()!=null && this.getManagedBean().getCtCircularNo().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("circular.latestCircularVer.circularNo",SearchOperator.OPERATOR_LIKE.value(),
			getManagedBean().getCtCircularNo()));	
		}			
		if(getManagedBean().getCtCircularName()!=null && getManagedBean().getCtCircularName().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("circular.latestCircularVer.name",SearchOperator.OPERATOR_EQUALS.value(),
			getManagedBean().getCtCircularName()));	
		}	
		if(getManagedBean().getCtSupplierId()!=null && getManagedBean().getCtSupplierId().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.supplierId",SearchOperator.OPERATOR_LIKE.value(),
			getManagedBean().getCtSupplierId()));	
		}		
		if(getManagedBean().getCtSupplierName()!=null && getManagedBean().getCtSupplierName().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("smSupplier.supplierName",SearchOperator.OPERATOR_EQUALS.value(),
			getManagedBean().getCtSupplierName()));	
		}	
		if(this.getManagedBean().getCtDateStart()!=null && this.getManagedBean().getCtDateStart().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.effDate",SearchOperator.OPERATOR_LIKE.value(),
			getManagedBean().getCtDateStart()));	
		}			
		if(getManagedBean().getCtDateEnd()!=null && getManagedBean().getCtDateEnd().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.expDate",SearchOperator.OPERATOR_EQUALS.value(),
			getManagedBean().getCtDateEnd()));	
		}	
		if(getManagedBean().getCtProductCode()!=null && getManagedBean().getCtProductCode().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("latestContractVer.ctContractValue.contractPhysicalNo",SearchOperator.OPERATOR_LIKE.value(),
			getManagedBean().getCtProductCode()));	
		}	
		if(getManagedBean().getCtLOANo()!=null && getManagedBean().getCtLOANo().length()>0) {
			searchBean.getDataFilterBeans().add(new SearchFilterBean("loaNo",SearchOperator.OPERATOR_LIKE.value(),
			getManagedBean().getCtLOANo()));	
		}	
				
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
		

		IContractAssociationSO contractAssociationSO = null;
		ISearchBean<CtAssocContract> searchBean = new SearchBean<CtAssocContract>();

		try {
			
			if(this.getManagedBean().getProjectName()!=null && this.getManagedBean().getProjectName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssociation.projectName",	SearchOperator.OPERATOR_EQUALS.value(),
				getManagedBean().getProjectName()));	
			}
			if(this.getManagedBean().getCtName()!=null && this.getManagedBean().getCtName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.contractName",SearchOperator.OPERATOR_LIKE.value(),
				this.getManagedBean().getCtName()));	
			}
			if(this.getManagedBean().getProjectDescription()!=null && this.getManagedBean().getProjectDescription().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctAssociation.description",SearchOperator.OPERATOR_LIKE.value(),
				getManagedBean().getProjectDescription()));	
			}			
			if(getManagedBean().getCtSupplierName()!=null && getManagedBean().getCtSupplierName().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.SmSupplier.supplierName",SearchOperator.OPERATOR_EQUALS.value(),
				getManagedBean().getCtSupplierName()));	
			}
			if(this.getManagedBean().getCtDateStart()!=null && this.getManagedBean().getCtDateStart().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.effDate",
				SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getCtDateStart()));	
			}			
			if(getManagedBean().getCtDateEnd()!=null && getManagedBean().getCtDateEnd().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.expDate",
				SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getCtDateEnd()));	
			}	
			if(getManagedBean().getCtProductCode()!=null && getManagedBean().getCtProductCode().length()>0) {
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContract.latestContractVer.ctContractValue.contractPhysicalNo",
				SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getCtProductCode()));	
			}	

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setSearchBean(super.createSearchBean(CtAssocContract.class,"associateSearch"));
			reqDTO.setSearchBean(searchBean);
			ContractAssociationResDTO ResDTO = contractAssociationSO.searchAssociation(reqDTO);
			this.getManagedBean().setAssociationList(ResDTO.getResultBean().getResultList());
			this.getManagedBean().setSelectedIndex(0);

		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
			searchBean = null;
		} 
	}
	
	public void addAdvanceContract() {

		for (int i = 0; i < selectedContractList.length; i++) {
			CtContract ad = selectedContractList[i];
			this.getManagedBean().getAssociateSearchList().add(ad);
		}
		selectedContractList = null;
	}

	public void deleteAdvanceContract() {
		this.getManagedBean().getAssociateSearchList().remove(this.getManagedBean().getAssocoiateCtContract());
	}
	
	public void deleteAssociation() {

		IContractAssociationSO contractAssociationSO = null;

		try {

			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setCtDelAssocContract(this.getManagedBean().getCtDelAssocContract());
			contractAssociationSO.deleteAssociation(reqDTO);
		} catch (Exception ex) {
			log.error("Exception>>", ex);

		} finally {
			contractAssociationSO = null;
		} 
	}
	
	public String createAssociation() {

		IContractAssociationSO contractAssociationSO = null;
        List<CtAssocContract> contractAssociationList = new ArrayList<CtAssocContract>();
        CtAssocContract ctAssocContract = new CtAssocContract();
		try {
              for(CtContract contract : this.getManagedBean().getAssociateSearchList()){
            	  ctAssocContract.setCtContract(contract);
            	  ctAssocContract.setCtAssociation(this.getManagedBean().getCtAssociation());
            	  contractAssociationList.add(ctAssocContract);  
              }
              
            this.getManagedBean().getCtAssociation().setCtAssocContracts(contractAssociationList);
			ContractAssociationReqDTO reqDTO = new ContractAssociationReqDTO();
			contractAssociationSO = (IContractAssociationSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractAssociationSO.class);
			reqDTO.setAssociation(this.getManagedBean().getCtAssociation());
			ContractAssociationResDTO resDTO = contractAssociationSO.createAssociation(reqDTO);
			if (resDTO.isSuccessful()) {
				
				  log.info("Record saved successfully");
				  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Association : "+ 
				   this.getManagedBean().getCtAssociation().getProjectName()));
					
			  } else {
					throw new EpException("Failed.");
				}

		} catch (Exception ex) {
			log.error("Exception>>>" + ex);
			ex.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,ex.toString(), ex.getMessage()));
		} finally {
			contractAssociationSO = null;
			contractAssociationList = null;
			ctAssocContract = null;
		}
       return null;
	}
	
    public String saveContract(String action){
		
	    IContractSO contractSO = null;
		try{
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
      
            if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE)){      
			    this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_SUSPEND_ACTIVE);
			    this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
          
            }else if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_TERMINATION)){
            	 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
            	 this.getManagedBean().getContractVer().getCtContract().setContractId(this.getManagedBean().getCtContract().getContractId());
          
            }else if(this.getManagedBean().getChkRenderPage().equalsIgnoreCase(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT)){
		         this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
                
            }else {
            	 this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_CREATION);	
            	 this.getManagedBean().getContractVer().getCtContract().setAgreement(true);
            }
            
            reqDTO.setToken(token);
            reqDTO.setOutcome(action);
            reqDTO.setTaskId(this.getManagedBean().getTaskId());
            reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
			
            reqDTO.setCtContract(this.getManagedBean().getCtContract());
            reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
			
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
	
    /*Approver details start*/
    private List<String> getAvailableApproverList() {
		
    	List<String> availableApprovers = new ArrayList<String>(); 
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
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
			for(PmUserGroup userGroup : getManagedBean().getUserGroupMap().values()) 
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}/*End approval details*/
	
	public void downloadFile(String filePath) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		res.setHeader( "Content-Disposition", "attachment; filepathe=\"" + filePath + "\"" );//
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
		} else {
			//out.write(attachment.getFileByte());
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}	
	
	public SelectItem[] getMinistryDropdown() {
		
		SelectItem[] items = null;     
		try {
		        int i = 1;
		        IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		        OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		        reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		
		        OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		        List<PmOrgProfile> list = resDTO.getOrgProfileList();
		        items = new SelectItem[list.size()+1];
				items[0] = new SelectItem(null, "- Select One -");
		      
				for(PmOrgProfile orgProfile : list) 
			      items[i++] = new SelectItem( orgProfile.getOrgCode(), orgProfile.getOrgName());       
				
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}
	
	public SelectItem[] getDepartmentDropdown() {
	
		SelectItem[] items = null;     
		try {
		        int i = 1;
		        IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		        OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		        reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		        OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		        List<PmOrgProfile> list = resDTO.getOrgProfileList();
		        items = new SelectItem[list.size()+1];
				items[0] = new SelectItem(null, "- Select One -");
	            for (PmOrgProfile orgProfile : list) {
		              System.out.println(orgProfile.getOrgProfileId() + "|"
				     + orgProfile.getOrgCode() + "|" + orgProfile.getOrgName());
		              items[i++] = new SelectItem(orgProfile.getOrgCode(), orgProfile.getOrgName());
	           }
		}
		catch (Exception e) {
	    	   e.printStackTrace();
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	      }
	   return items;
	}
	
	public String gotoTerminatePage() {

		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		TerminationBackingBean trBB= null;
		
		try {

			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setContractVerId(this.getManagedBean().getCtContract().getLatestContractVer().getContractVerId());
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVer(reqDTO);
			trBB = new TerminationBackingBean();
			
			if(resDTO.getCtContractVer().getCtTermination().getDecisionType() == null)
				resDTO.getCtContractVer().getCtTermination().setDecisionType(new PmParameter());
			
	    	trBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
			trBB.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());	
				
    	 } catch (Exception e) {
				e.printStackTrace();
		 } finally {
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
		
		try {

			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVer(reqDTO);
			suspBB = new SuspensionBackingBean();
			suspBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractSO = null;
			resDTO = null;
			reqDTO = null;
			suspBB = null;
			ctBB = null;
		}
		return PROCESS_SUSPEND_PAGE;
	}
	
	public String gotoAmendPage() {
		
		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		AmendmentBackingBean amendBB = null;
		
		try {

				reqDTO = new ContractReqDTO();
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
				reqDTO.setUpdateAction(true);
				resDTO = contractSO.retrieveContractVer(reqDTO);
				amendBB = new AmendmentBackingBean();
				amendBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
				if(amendBB.getManagedBean().getContractVer().getCtAmendment() ==null) {
					amendBB.getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				contractSO = null;
				resDTO = null;
				reqDTO = null;
				amendBB =null;
				ctBB = null;
				
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
	
	public String gotoAssociatePage() {
		return PROCESS_ASSOCIATE_PAGE;
	}
	
	public String gotoCancelPage() {
		return PROCESS_ASSOCIATE_CANCEL_PAGE;
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

	public CtContract[] getSelectedContractList() {
		return selectedContractList;
	}

	public void setSelectedContractList(CtContract[] selectedContractList) {
		this.selectedContractList = selectedContractList;
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
}
