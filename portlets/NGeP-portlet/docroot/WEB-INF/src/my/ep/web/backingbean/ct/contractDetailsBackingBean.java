/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.math.BigDecimal;
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
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.CtSetting;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractedItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ContractListItem;
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
	
	private TreeNode rootNodeAgency;  
	private boolean activeSuspension;
	private boolean paymentHistory;
	private static boolean chkSchedule;
	private String fulfillmentNo ="01234567889";
	 
	/*Testing Purpose Start*/
	private List<CtContractedItem> ctContractedItem; 
	private List<CtItemPrice>  ctItemPrice;
	private CtContractedItem ctContractedItems;
	private List<CtItemCeilingQty> ctItemCeilingQty;
	private List<CtContractValue> ctContractValue;
	private List<CtSchedule> ctSchedules;
	private List<CtDeduction> ctDeduction;
	private List<CtSetting> ctSetting;
	private List<CtPerformanceBond> CtPerformanceBonds;
	private List<SmSupplierBank> smSupplier;
	/*Testing purpose End*/

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
		
		 /*Testing Purpose Start*/
		  ctContractedItem = new ArrayList<CtContractedItem>();
		  ctItemPrice = new ArrayList<CtItemPrice>();
		  ctItemCeilingQty = new ArrayList<CtItemCeilingQty>();
		  ctContractValue =  new ArrayList<CtContractValue>();
		  
		  CtContractedItem ctContractedItem1 = new CtContractedItem();
		 
		  ctContractedItem1.setContractedItemId(new Long(1));
		  ctContractedItem1.setItemName("DESKTOP");
		  // ctContractedItem1.setUomId(new Long(20));
		  ctContractedItem1.setMinimumOrderQty(new Double(300));
		  ctContractedItem1.setChkExempted(true);
		
		  ctContractedItem.add(ctContractedItem1);
		
		  CtContractedItem ctContractedItem2 = new CtContractedItem();
		  ctContractedItem2.setContractedItemId(new Long(2));
		  ctContractedItem2.setItemName("DESKTOP INSTALLATION SERVICE");
		  //ctContractedItem2.setUomId(new Long(20));
		  ctContractedItem2.setMinimumOrderQty(new Double(300));
		  ctContractedItem2.setChkExempted(true);  
		  ctContractedItem.add(ctContractedItem2);
		  
          this.getManagedBean().getCtContract().setCtContractedItems(ctContractedItem);
         
          CtItemPrice ctItemPrices1 = new CtItemPrice();
	       
		  ctItemPrices1.setEffDate(new Date());
		  ctItemPrices1.setExpDate(new Date());
		  ctItemPrices1.setUnitPrice(new Double(4000));
		  ctItemPrices1.setTierFrom(new Long(1));
		  ctItemPrices1.setTierTo(new Long(50));
		  CtContractZone ctContractZone = new CtContractZone();
		  ctContractZone.setZoneName("North Zone");
		  ctItemPrices1.setCtContractZone(ctContractZone);
		  ctItemPrice.add(ctItemPrices1);
		  
		  CtItemPrice ctItemPrices2 = new CtItemPrice();
		  ctItemPrices2.setEffDate(new Date());
		  ctItemPrices2.setExpDate(new Date());
		  ctItemPrices2.setUnitPrice(new Double(5000));
	      ctItemPrices2.setTierFrom(new Long(1));
		  ctItemPrices2.setTierTo(new Long(50));
		  CtContractZone ctContractZone1 = new CtContractZone();
		  ctContractZone1.setZoneName("South Zone");
		  ctItemPrices2.setCtContractZone(ctContractZone1);
		  ctItemPrice.add(ctItemPrices2);
		  this.getManagedBean().getCtContract().getCtContractedItems().get(0).setCtItemPrices(ctItemPrice); 
          
		  CtItemCeilingQty ctItemCeilingQty1 = new CtItemCeilingQty();
		  ctItemCeilingQty1.setCeilingQuantity(new Double(400));
		  ctItemCeilingQty1.setEffDate(new Date());
		  ctItemCeilingQty1.setExpDate(new Date());
		  
		  ctItemCeilingQty.add(ctItemCeilingQty1);
		  this.getManagedBean().getCtContract().getCtContractedItems().get(0).setCtItemCeilingQties(ctItemCeilingQty);
		  
		  /*Fulfilment Details*/
		  // this.getManagedBean().getCtContract().setsetContractNo("QTN3944833483784");
		  this.getManagedBean().getCtContract().setContractType("Ministry");
		  this.getManagedBean().getCtContract().setChkLocal(true);
		  //this.getManagedBean().getCtContract().setCreatedDate(createdDate);
		  this.getManagedBean().getCtContract().setQtPublishDate(new Date());
		  this.getManagedBean().getCtContract().setQtLoaNo("LOA 325/2012");
		  this.getManagedBean().getCtContract().setQtLoaDate(new Date());
		  
		  CtContractValue ctContractValue1 = new CtContractValue();
		  ctContractValue1.setContractAmount(new Double(50000));
		  ctContractValue1.setEffDate(new Date());
		  ctContractValue1.setExpDate(new Date());
		 
		  ctContractValue.add(ctContractValue1);
		  //this.getManagedBean().getCtContract().setCtContractValues(ctContractValue);
		  this.getManagedBean().getCtContract().setFulfillmentType("Periodic - Scheduled");
		  //this.getManagedBean().getCtContract().setLiquidatedDamage(new Double(20000));
		  this.getManagedBean().getCtContract().setContractName("Bekalan Pen Bebola");
		  //this.getManagedBean().getCtContract().setDeliveryPeriod(14);
		  this.getManagedBean().getCtContract().setServiceFee(0.4);
		  
		  /*Deduction Details*/
		  if(ctDeduction == null){
		  ctDeduction = new ArrayList<CtDeduction>();
		  CtDeduction ctDeduction1 = new CtDeduction();
		  ctDeduction1.setDeductionType("Royalty");
		  ctDeduction1.setDescription("Royalty Fee 0.5% for Printing Buku Teks Sekolah Menengah");
		  ctDeduction1.setAmount(new Double(5000));
		  this.getManagedBean().getCtContract().getCtDeductions().add(ctDeduction1); 
		  }
		  //this.getManagedBean().getCtContract().setFrequencySchedule(2);
		  
		  if(ctSchedules == null){
		     ctSchedules = new ArrayList<CtSchedule>();
		     CtSchedule ctSchedule = new CtSchedule();
		     ctSchedule.setScheduleId(new Long(1));
		     ctSchedule.setScheduleNo("SCH2933823923");
		     ctSchedule.setStartDate(new Date());
		     ctSchedule.setEndDate(new Date());
		     ctSchedules.add(ctSchedule);
		     this.getManagedBean().getCtContract().setCtSchedules(ctSchedules);
		  }
		  
		 
		  CtSetting ctSetting1 = new CtSetting();
		  ctSetting1.setCtNotifyDays(new Integer(1));
		  ctSetting1.setAgNotifyDayB4Ct(new Integer(1));
		  ctSetting1 .setAgNotifyDaysB4Ag(3);
		  ctSetting1.setApproverMtoDays(4);
		  ctSetting1.setBondNotifyDays(5);
		  ctSetting1.setSupplierActionDays(6);
		  ctSetting1.setTerminationNotifyDays(7);

		  this.getManagedBean().setCtSetting(ctSetting1);
		  
		  /* Performance Bond*/
		  if(CtPerformanceBonds == null){
		 
	      CtPerformanceBonds = new ArrayList<CtPerformanceBond>();
		  CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
		
		  PmOrgProfile pmOrgProfile  = new PmOrgProfile();
		  pmOrgProfile.setOrgName("CIMB");
		  ctPerformanceBond.setFinancialOrgProfileId(pmOrgProfile);
		  ctPerformanceBond.setBondType("New");
		  ctPerformanceBond.setRefNo("CIMB_99235");
		  ctPerformanceBond.setBondAmount(new Double(125000.00));
		  ctPerformanceBond.setEffDate(new Date());
		  ctPerformanceBond.setExpDate(new Date());
		  CtPerformanceBonds.add(ctPerformanceBond);
		  this.getManagedBean().getCtContract().setCtPerformanceBonds(CtPerformanceBonds);
		}
		  
		  if(smSupplier == null){
			
			smSupplier = new ArrayList<SmSupplierBank>();
		    SmSupplierBank smSupplier1 = new SmSupplierBank();
			smSupplier1.setAccountNo(new BigDecimal(782374823));
			smSupplier1.setBankId(new BigDecimal(101));
			smSupplier1.setAddressId(new BigDecimal(201));
			smSupplier.add(smSupplier1);
			SmSupplierBank smSupplier2 = new SmSupplierBank();
			smSupplier2.setAccountNo(new BigDecimal(782371111));
			smSupplier1.setBankId(new BigDecimal(102));
			smSupplier1.setAddressId(new BigDecimal(202));
			smSupplier.add(smSupplier2);
			
			this.getManagedBean().setSmSupplierBank(smSupplier1);
	     }
    }
	
	public void addDeductionDetails(){
    	CtDeduction ctDeduction = new CtDeduction();
		this.getManagedBean().getCtContract().getCtDeductions().add(ctDeduction);
	}
	
	public void deleteDeductionDetails(){	
	    this.getManagedBean().getCtContract().getCtDeductions().remove(this.getManagedBean().getCtDeduction());
 	 }
	
	public void addScheduleDetails(){

		CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
        this.getManagedBean().getCtContract().getCtProgressivePayments().add(ctProgressivePayment);
	}
	
	public void deleteScheduleDetails(){			
		this.getManagedBean().getCtContract().getCtProgressivePayments().remove(this.getManagedBean().getCtProgressivePayment());
	}
	
	public List<SelectItem> getItemDetailsMethod() {	
		   
 	      List<SelectItem> list = new ArrayList<SelectItem>();
		  list.add(new SelectItem("Item Pricing","Item  Pricing"));
		  list.add(new SelectItem("Item Ceiling Quantity","Item Ceiling Quantity"));
		  return list;
	}
			
	private void flush(){
		 this.getManagedBean().setItemPricing(false);
		 this.getManagedBean().setItemCeilingQuantity(false);
	     //this.getManagedBean().setSmSupplierBank(null);
		 paymentHistory = false;
		 chkSchedule = false;
	}

	public void changeField(){  
			   
	    try{	
			flush();    	
			if(this.getManagedBean().getShowItem().equalsIgnoreCase("Item Pricing")){			
		    this.getManagedBean().setItemPricing(true);
			
		    }else if(this.getManagedBean().getShowItem().equalsIgnoreCase("Item Ceiling Quantity")){
				  this.getManagedBean().setItemCeilingQuantity(true);
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
		         
		        }  
    }  
	
	public void onChange(TabChangeEvent event) { 
	  
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
	
    public ContractListItem getContractListItem() {
			return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
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
	
	/*Testing purpose started*/
	
	public void onRowSelect(SelectEvent event) {  
		 
		 for( CtContractedItem contractedItems : ctContractedItem) {
			   if(contractedItems.getContractedItemId().equals(ctContractedItems.getContractedItemId())){
				   log.info("====================primary key two=========================" +ctContractedItems.getContractedItemId());
					   ctItemPrice = null;
				       ctItemPrice = new ArrayList<CtItemPrice>();
				       CtItemPrice ctItemPrices1 = new CtItemPrice();	
				       ctItemPrices1.setEffDate(new Date());
				       ctItemPrices1.setExpDate(new Date());
				       ctItemPrices1.setUnitPrice(new Double(8000));
				       ctItemPrices1.setTierFrom(new Long(1));
				       ctItemPrices1.setTierTo(new Long(100));
				       CtContractZone ctContractZone = new CtContractZone();
				       ctContractZone.setZoneName("East Zone");
				       ctItemPrices1.setCtContractZone(ctContractZone);
				       ctItemPrice.add(ctItemPrices1);
				       this.getManagedBean().getCtContract().getCtContractedItems().get(0).setCtItemPrices(ctItemPrice);
				  
				       CtItemPrice ctItemPrices2 = new CtItemPrice();
				       ctItemPrices2.setEffDate(new Date());
				       ctItemPrices2.setExpDate(new Date());
				       ctItemPrices2.setUnitPrice(new Double(15000));
			           ctItemPrices2.setTierFrom(new Long(1));
				       ctItemPrices2.setTierTo(new Long(150));
				       CtContractZone ctContractZone1 = new CtContractZone();
				       ctContractZone1.setZoneName("West Zone");
				       ctItemPrices2.setCtContractZone(ctContractZone1);
				       ctItemPrice.add(ctItemPrices2);
				       this.getManagedBean().getCtContract().getCtContractedItems().get(1).setCtItemPrices(ctItemPrice); 
				  } 
			  if(contractedItems.getContractedItemId().equals(new Long(1))){
		         log.info("====================primary key one=========================" +ctContractedItems.getContractedItemId());
		          ctItemPrice = null;
		           ctItemPrice = new ArrayList<CtItemPrice>();
		         CtItemPrice ctItemPrices1 = new CtItemPrice();
			       
				  ctItemPrices1.setEffDate(new Date());
				  ctItemPrices1.setExpDate(new Date());
				  ctItemPrices1.setUnitPrice(new Double(4000));
				  ctItemPrices1.setTierFrom(new Long(1));
				  ctItemPrices1.setTierTo(new Long(50));
				  CtContractZone ctContractZone = new CtContractZone();
				  ctContractZone.setZoneName("North Zone");
				  ctItemPrices1.setCtContractZone(ctContractZone);
				  ctItemPrice.add(ctItemPrices1);
				  
				  CtItemPrice ctItemPrices2 = new CtItemPrice();
				  ctItemPrices2.setEffDate(new Date());
				  ctItemPrices2.setExpDate(new Date());
				  ctItemPrices2.setUnitPrice(new Double(5000));
			      ctItemPrices2.setTierFrom(new Long(1));
				  ctItemPrices2.setTierTo(new Long(50));
				  CtContractZone ctContractZone1 = new CtContractZone();
				  ctContractZone1.setZoneName("South Zone");
				  ctItemPrices2.setCtContractZone(ctContractZone1);
				  ctItemPrice.add(ctItemPrices2);
				  this.getManagedBean().getCtContract().getCtContractedItems().get(0).setCtItemPrices(ctItemPrice); 
		       }
		 }
    }  
	
	public List<CtDeduction> getDeducationList(){
		
		  CtDeduction ctDeduction1 = new CtDeduction();
		  ctDeduction1.setDeductionType("Royalty");
		  ctDeduction1.setDescription("Royalty Fee 0.5% for Printing Buku Teks Sekolah Menengah");
		  ctDeduction1.setAmount(new Double(5000));
		  this.getManagedBean().getCtContract().getCtDeductions().add(ctDeduction1);
		
		return null;
		
	}
	
	public void onRowUnselect(UnselectEvent event) {  
        
    }  
	
	public List<CtContractedItem> getCtContractedItem() {
		return ctContractedItem;
	}

	public void setCtContractedItem(List<CtContractedItem> ctContractedItem) {
		this.ctContractedItem = ctContractedItem;
	}

	public List<CtItemPrice> getCtItemPrice() {
		return ctItemPrice;
	}

	public void setCtItemPrice(List<CtItemPrice> ctItemPrice) {
		this.ctItemPrice = ctItemPrice;
	}

	public CtContractedItem getCtContractedItems() {
		return ctContractedItems;
	}

	public void setCtContractedItems(CtContractedItem ctContractedItems) {
		this.ctContractedItems = ctContractedItems;
	}

	public List<CtItemCeilingQty> getCtItemCeilingQty() {
		return ctItemCeilingQty;
	}

	public void setCtItemCeilingQty(List<CtItemCeilingQty> ctItemCeilingQty) {
		this.ctItemCeilingQty = ctItemCeilingQty;
	}

	public List<CtContractValue> getCtContractValue() {
		return ctContractValue;
	}

	public void setCtContractValue(List<CtContractValue> ctContractValue) {
		this.ctContractValue = ctContractValue;
	}
	
	public void progressivePayment(){
		flush();
	//	if(this.getManagedBean().getCtContract().isChkProgressivePayment())
		  this.setChkSchedule(true);
	}

	public boolean isChkSchedule() {
		return chkSchedule;
	}

	public void setChkSchedule(boolean chkSchedule) {
		this.chkSchedule = chkSchedule;
	}

	public List<CtSchedule> getCtSchedules() {
		return ctSchedules;
	}

	public void setCtSchedules(List<CtSchedule> ctSchedules) {
		this.ctSchedules = ctSchedules;
	}

	public List<CtDeduction> getCtDeduction() {
		return ctDeduction;
	}

	public void setCtDeduction(List<CtDeduction> ctDeduction) {
		this.ctDeduction = ctDeduction;
	}

	public List<CtSetting> getCtSetting() {
		return ctSetting;
	}

	public void setCtSetting(List<CtSetting> ctSetting) {
		this.ctSetting = ctSetting;
	}

	public List<CtPerformanceBond> getCtPerformanceBonds() {
		return CtPerformanceBonds;
	}

	public void setCtPerformanceBonds(List<CtPerformanceBond> ctPerformanceBonds) {
		CtPerformanceBonds = ctPerformanceBonds;
	}
	
	
	public List<SelectItem> getSelectedBankAccount() {	
		   List<SelectItem> list = new ArrayList<SelectItem>();
		   for(SmSupplierBank smSupplier : this.getSmSupplier()){
			    list.add(new SelectItem(smSupplier.getAccountNo()));
	       }
	      return list;
		}
	
	public void changeAccDetails(){
			 try{	
		      	for(SmSupplierBank smSuppliers : this.getSmSupplier()){
			       if(this.getManagedBean().getSmSupplierBank().getAccountNo().equals(smSuppliers.getAccountNo())){
			        // flush();    	
		              this.getManagedBean().setSmSupplierBank(smSuppliers);
			         }
		         }
			 }catch(Exception e){
			   System.out.println(e);
		 }
			
	}

	public List<SmSupplierBank> getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(List<SmSupplierBank> smSupplier) {
		this.smSupplier = smSupplier;
	} 
	/*Testing purpose End*/
	
	public void saveContractDetails(){
		
	}
}
