/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ContractDeduction;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.bean.ct.ContractSchedule;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.ZoneDetails;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;

/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013	 
 */

@ManagedBean
@RequestScoped
public class contractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	private static Logger log = Logger.getLogger(contractDetailsBackingBean.class);
	private static final String PROCESS_SUSPEND_PAGE="/xhtml/ct/fulfilmentdetails/suspension/suspend.xhtml";
	private static final String PROCESS_TERMINATE_PAGE="/xhtml/ct/fulfilmentdetails/termination/termination.xhtml";
	private static final String PROCESS_AMEND_PAGE="/xhtml/ct/fulfilmentdetails/amendment/amendment.xhtml";
	private static final String PROCESS_VIEW_PAGE="/xhtml/ct/agreement/viewAgreement.xhtml";

	private ContractListItem contractListItem;
	private CreateContractDetails[] selectedAgencyList;
	private List<CreateContractDetails> agencyDetails = null;
	 
	private TreeNode rootNode;  
	private TreeNode selectedNode; 
	private boolean itemPricing;
	private TreeNode rootNodeAgency;  
	private boolean activeSuspension;
	private boolean paymentHistory;
	private boolean itemCeilingQuantity;
	private String fulfillmentNo ="01234567889";
	 
	private final static String[] deductions;   
	static {  
		  
		deductions = new String[4];  
		deductions[0] = "Royalty";  
		deductions[1] = "Penalty";  
		deductions[2] = "Advanced Payment";  
		deductions[3] = "Others";  
    }  
	
	public String[] getDeductions() {
		return deductions;
	}

	public contractDetailsBackingBean(){
		 
		 contractListItem = new ContractListItem();
		 rootNode = new DefaultTreeNode("root", null);  
		 TreeNode zone1 = new DefaultTreeNode(new ZoneDetails("Zone Name  ZON TENGAH", "Delivery Period 15"), rootNode);  
	     TreeNode Zone2 = new DefaultTreeNode(new ZoneDetails("Zone Name ZON UTARA", "Delivery Period 21 Days"), rootNode);  
	     new DefaultTreeNode(new ZoneDetails("PUTRAJAYA", " "), zone1);  
	     new DefaultTreeNode(new ZoneDetails("SELANGOR", " "), Zone2);  
	    
	     rootNodeAgency = new DefaultTreeNode("root", null);  
		 this.getManagedBean().getCreateContractDetails().setMinistryName("KEMENTERIAN PELAJARAN");
		 this.getManagedBean().getCreateContractDetails().setDepartmentName("BAHAGIAN MATRIKULASI");
		    
		 TreeNode agencyOne = new DefaultTreeNode(new CreateContractDetails("KEMENTERIAN PELAJARAN"), rootNodeAgency);  
		 TreeNode agencytwo = new DefaultTreeNode(new CreateContractDetails("BAHAGIAN MATRIKULASI"), agencyOne); 
		    
		 new DefaultTreeNode(new CreateContractDetails("0211780401","NEGERI SEMBILAN","5000.00", new Date(),  new Date()), agencytwo);  
	 }
	
	public void addDeductionDetails(){

		log.debug("*****Add Deduction Details >>>>>>");
		ContractDeduction contractDeduction = new ContractDeduction();
        this.getManagedBean().getDeductionList().add(contractDeduction);
	}
	
	public void deleteDeductionDetails(){			
		this.getManagedBean().getDeductionList().remove(this.getManagedBean().getContractDeduction());
	}
	
	public void addScheduleDetails(){

		log.debug("*****Add Schedule Details >>>>>>");
		 ContractSchedule contractSchedule = new ContractSchedule();
        this.getManagedBean().getScheduleList().add(contractSchedule);
	}
	
	public void deleteScheduleDetails(){			
		this.getManagedBean().getScheduleList().remove(this.getManagedBean().getContractSchedule());
	}
	
	public List<SelectItem> getItemDetailsMethod() {	
		   
 	   List<SelectItem> list = new ArrayList<SelectItem>();
		   list.add(new SelectItem("Item Pricing","Item  Pricing"));
		   list.add(new SelectItem("Item Ceiling Quantity","Item Ceiling Quantity"));
		   return list;
	}
			
	private void flush(){
		itemPricing = false;
		itemCeilingQuantity = false;
		paymentHistory = false;
	}

	public void changeField(){  
			   
	     try{	
			  flush();    	
			  if(this.getManagedBean().getCreateContractDetails().getZoneType().equalsIgnoreCase("Item Pricing")){			
					itemPricing = true;
			  }else if(this.getManagedBean().getCreateContractDetails().getZoneType().equalsIgnoreCase("Item Ceiling Quantity")){
							itemCeilingQuantity = true;
			  }
	     }catch(Exception e){
			    	System.out.println(e);
		  }
	}

	public void changePaymentValue(){  
			   
		 try{	
			  flush();    	
			  if(getFulfillmentNo() != null){			
					paymentHistory = true;
			   }
		 }catch(Exception e){
			    	System.out.println(e);
			    }
	}
	
	public void onCellEdit(CellEditEvent event) {  
		        Object oldValue = event.getOldValue();  
		        Object newValue = event.getNewValue();  
		        if(newValue != null && !newValue.equals(oldValue)) {  
		            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);  
		            FacesContext.getCurrentInstance().addMessage(null, msg);  
		        }  
    }  
	
	public void onChange(TabChangeEvent event) { 
	    System.out.println("tab id alam101 = >>>>>>>>>>>>>>>>>>>>>>>" + event.getTab().getId());
	}
	
	 public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}
	
	public String gotoSuspendPage() {	
			return PROCESS_SUSPEND_PAGE;
    }

	public String gotoTerminatePage() {	
			return PROCESS_TERMINATE_PAGE;
	}
   
	public String gotoAmendPage() {	
			return PROCESS_AMEND_PAGE;
	}

	public String gotoViewPage(){
	        this.getManagedBean().setViewAgreement("view");
			return PROCESS_VIEW_PAGE;
	}
	
	public boolean isActiveSuspension() {
			return activeSuspension;
	}

    public void setActiveSuspension(boolean activeSuspension) {
			this.activeSuspension = activeSuspension;
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

    public TreeNode getSelectedNode() {
				return selectedNode;
	}

    public void setSelectedNode(TreeNode selectedNode) {
				this.selectedNode = selectedNode;
	}
	
    public List<CreateContractDetails> getAgencyLists(){
				agencyDetails = new ArrayList<CreateContractDetails>();
				return agencyDetails;
	}
			
	public List<CreateContractDetails> getAgencyDetails() {
				return agencyDetails;
	}

	public void setAgencyDetails(List<CreateContractDetails> agencyDetails) {
				this.agencyDetails = agencyDetails;
	}
			
	public CreateContractDetails[] getSelectedAgencyList() {
				return selectedAgencyList;
	}

    public void setSelectedAgencyList(CreateContractDetails[] selectedAgencyList) {
				this.selectedAgencyList = selectedAgencyList;
	}
			
    public String getFulfillmentNo() {
				return fulfillmentNo;
	}

	public void setFulfillmentNo(String fulfillmentNo) {
				this.fulfillmentNo = fulfillmentNo;
	}

	public boolean isPaymentHistory() {
				return paymentHistory;
	}

	public void setPaymentHistory(boolean paymentHistory) {
			this.paymentHistory = paymentHistory;
	}

	public boolean isItemPricing() {
				return itemPricing;
	}

	public void setItemPricing(boolean itemPricing) {
		 this.itemPricing = itemPricing;
	}

	public boolean isItemCeilingQuantity() {
				return itemCeilingQuantity;
	}

    public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
				this.itemCeilingQuantity = itemCeilingQuantity;
	}	
    
}
