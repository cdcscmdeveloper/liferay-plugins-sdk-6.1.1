/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.ct.IContractSO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractValue;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.CtSetting;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractedItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.factoring.CtFactoring;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.SettingDetails;
import my.ep.web.bean.ct.ZoneDetails;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * Backing Bean For Contract Details
 * 
 * @author Alam
 * @since 26/9/2013	 
 */
@ManagedBean
@RequestScoped
public class ContractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	private static Logger log = Logger.getLogger(ContractDetailsBackingBean.class);
	private static final String PROCESS_SUSPEND_PAGE="/xhtml/ct/fulfilmentdetails/suspension/suspend.xhtml";
	private static final String PROCESS_TERMINATE_PAGE="/xhtml/ct/fulfilmentdetails/termination/termination.xhtml";
	private static final String PROCESS_AMEND_PAGE="/xhtml/ct/fulfilmentdetails/amendment/amendment.xhtml";
	private static final String PROCESS_VIEW_PAGE="/xhtml/ct/agreement/viewAgreement.xhtml";

	private TreeNode rootNode;  
	private TreeNode selectedNode; 
	
	private boolean paymentHistory;
	private TreeNode rootNodeAgency;  
	private boolean activeSuspension;
	private String fulfillmentNo ="01234567889";
	 
	/*Testing Purpose Start*/
	private List<CtSetting> ctSetting;
	private List<CtAgency> ctAgencyList;
	private List<CtSchedule> ctSchedules;
	private List<CtFactoring> ctFactoring;
	private CtAgency[] selectedAgencyList;
	private List<CtItemPrice>  ctItemPrice;
	private List<SmSupplierBank> smSupplier;
	private CtContractedItem ctContractedItems;
	private ContractListItem contractListItem;
	private List<CtContractValue> ctContractValue;
	private List<CtContractedItem> ctContractedItem; 
	private List<CtItemCeilingQty> ctItemCeilingQty;
	private List<CtPerformanceBond> CtPerformanceBonds;
    private List<CreateContractDetails> agencyDetails = null;
	/*Testing purpose End*/

    private CtDeductionClause[] chkDeductionClause;
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

	public ContractDetailsBackingBean(){
		
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		 
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
		 
		
		 if(this.getManagedBean().getOutComes()==null) {
			this.getManagedBean().setOutComes(new String[]{BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
		}/*else{
			String[] outcomeArr = new String[this.getManagedBean().getOutComes().length + 1];
			outcomeArr[0] = BpmOutcomeConst.BPM_SAVE;
			for (int i =1; i <= this.getManagedBean().getOutComes().length; i++){
				outcomeArr[i] = this.getManagedBean().getOutComes()[i-1];
			}
			this.getManagedBean().setOutComes(outcomeArr);
		}*/
    }
	
	@PostConstruct
	public void initializeContractValue(){
		 /*Insert Data Contract Table*/
		 this.getManagedBean().getCtContract().setQtPublishDate(new Date());
		 this.getManagedBean().getCtContract().setQtLoaNo("LOA 325/2012");
		 this.getManagedBean().getCtContract().setQtLoaDate(new Date());
		 this.getManagedBean().getCtContract().setChkLocal(false);
		 this.getManagedBean().getCtContract().setChkImported(false);
		 this.getManagedBean().getCtContract().setChkMto(false);
		 this.getManagedBean().getCtContract().setContractName("Bekalan Pen Bebola");
		 this.getManagedBean().getCtContract().setServiceFee(0.4); 
		 this.getManagedBean().getCtContract().setQtLoaId(new Long(3252012));
		 this.getManagedBean().getCtContract().setQtLoaId(new Long(3252012)); 
		/* Date d1=new Date();
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		 Date date = null;
		try {
			date = dateFormat.parse(dateFormat.format(d1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 long time = date.getTime();
		 this.getManagedBean().getCtContract().setCreatedDate(new Timestamp(time));
		 this.getManagedBean().getCtContract().setChangedBy(new Long(12345));
		 this.getManagedBean().getCtContract().setChangedDate(new Timestamp(time)); */
		
		 /*Insert Data Bond Table*/
		/*CtPerformanceBond performanceBond = new CtPerformanceBond();
		 performanceBond.setBondType("N");
		 performanceBond.setRefNo("CIMB_99235");
		 performanceBond.setEffDate(new Date());
		 performanceBond.setExpDate(new Date());
		 performanceBond.setBondAmount(new Double(1000.00));
		 performanceBond.setBrandNew(true);
		 performanceBond.setCtContract(this.getManagedBean().getCtContract());
		 this.getManagedBean().getCtContract().getCtPerformanceBonds().add(performanceBond);*/
	 
	     /*Insert Data Bank Table*/
		 /* CtSupplierBank ctSupplierBank = new CtSupplierBank();
		 ctSupplierBank.setContractAdminId(new Long(101));
		 Date d1=new Date();
		 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		 Date date = null;
		 try {
			date = dateFormat.parse(dateFormat.format(d1));
		 } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 long time = date.getTime();
		 ctSupplierBank.setApprovedDate(new Timestamp(time));
		 ctSupplierBank.setCtContract(this.getManagedBean().getCtContract());
		 this.getManagedBean().getCtContract().getCtSupplierBanks().add(ctSupplierBank); */
		 
		 /*Insert Data Factoring Table*/
		/* CtFactoring ctFactoring = new CtFactoring();
		 ctFactoring.setQtLoaId(new Long(3252012));
		 ctFactoring.setCtContract(this.getManagedBean().getCtContract());
		 ctFactoring.setAuthRefNo("FC-320-345678");
		 ctFactoring.setEffDate(new Date());
		 ctFactoring.setExpDate(new Date());
		 ctFactoring.setApprovedDate(new Date());
		 this.getManagedBean().getCtContract().getCtFactorings().add(ctFactoring); */ 
		 
		 /*Insert Data Schedule Table*/
		/* CtSchedule ctSchedule = new CtSchedule();
		 ctSchedule.setCtContract(this.getManagedBean().getCtContract());
		 ctSchedule.setScheduleNo("FC-32-3400");
		 ctSchedule.setStartDate(new Date());
		 ctSchedule.setEndDate(new Date());
		 this.getManagedBean().getCtContract().getCtSchedules().add(ctSchedule); */
		 
		 /*Insert Data Setting Table*/
	/*	 CtSetting  ctSetting = new CtSetting();
		 ctSetting.setCtContract(this.getManagedBean().getCtContract());
		 ctSetting.setCtNotifyDays(30);
		 ctSetting.setAgNotifyDaysB4Ag(0);
		 ctSetting.setAgNotifyDayB4Ct(0);
		 ctSetting.setBondNotifyDays(30);
		 ctSetting.setTerminationNotifyDays(0);
		 ctSetting.setSupplierActionDays(0);
		 ctSetting.setApproverMtoDays(30);
		 this.getManagedBean().setCtSetting(ctSetting);
		 this.getManagedBean().getCtContract().getCtSettings().add(ctSetting); */
		 
		/*CtApprover ctApprover = new CtApprover();
		 ctApprover.setApproverSeq(new Long(1));
		 ctApprover.setDocId(new Long(101));
		 ctApprover.setDocType("doc");*/
		
		 /*Insert Value into contract value table*/
		/* CtContractValue ctContractValue = new CtContractValue();
		 ctContractValue.setContractAmount(999999.000);
		 ctContractValue.setAdditionalAmount(999999);
		 ctContractValue.setContractPeriod(24);
		 ctContractValue.setEffDate(new Date());
		 ctContractValue.setExpDate(new Date());
		 ctContractValue.setExtendPeriod(12);
		 ctContractValue.setLiquidatedDamage(999999.000);
		 ctContractValue.setDeliveryPeriod(4);
		 ctContractValue.setContractNo("234ER5670");
		 ctContractValue.setAgreement(false);
		 ctContractValue.setConcession(false);
		 ctContractValue.setExempted(false);
		 ctContractValue.setProgressivePayment(false);
		 ctContractValue.setFrequencySchedule(4);
		 ctContractValue.setCtContract(this.getManagedBean().getCtContract());
		 this.getManagedBean().getCtContract().getCtContractValues().add(ctContractValue);
		 
		 CtDeduction  ctDeduction = new  CtDeduction();
		 ctDeduction.setDescription("Supplier request for advanced payment 20% of the contract value");
		 ctDeduction.setAmount(5000.00);
		 ctDeduction.setCtContract(this.getManagedBean().getCtContract());
		 List<CtDeduction> deduct= new ArrayList<CtDeduction>(1);
		 deduct.add(ctDeduction);
	     this.getManagedBean().setCtDeductionList(deduct);    
		 this.getManagedBean().getCtContract().setCtDeductions(this.getManagedBean().getCtDeductionList());
		 
		/* CtAgency ctAgency = new CtAgency();
		 ctAgency.setCeilingAmount(5000.00);
		 ctAgency.setEffDate(new Date());
		 ctAgency.setExpDate(new Date());
		 ctAgency.setCtContract(this.getManagedBean().getCtContract());
		 this.getManagedBean().getCtContract().getCtAgencies().add(ctAgency); */
		 
		/* if(this.getManagedBean().getCtDeductionClause()== null){
		
		 CtDeductionClause ctDeductionClause = new CtDeductionClause();
		 ctDeductionClause.setDeductionClauseId(new Long(1));
		 ctDeductionClause.setClauseName("CLAUSE 8.1");
		 ctDeductionClause.setClauseDesc("The SERVICE to be provided by the COMPANY shall include, among other things, the following:");
		 ctDeductionClause.setCtDeduction(new CtDeduction());
		 					 
		 CtDeductionClause ctDeductionClause1 = new CtDeductionClause();
		 ctDeductionClause1.setDeductionClauseId(new Long(2));
		 ctDeductionClause1.setClauseName("CLAUSE 8.2");
		 ctDeductionClause1.setClauseDesc("Notwithstanding any provisions of this Contract, the COMPANY be allowed to carry out the SERVICE");
		 ctDeductionClause1.setCtDeduction(new CtDeduction());
		 
		 List<CtDeductionClause> clauses= new ArrayList<CtDeductionClause>(2);
		 clauses.add(ctDeductionClause);
		 clauses.add(ctDeductionClause1);
		 this.getManagedBean().setCtDeductionClause(clauses); 
		 
		 }*/
		 
		/*if(smSupplier == null){
				
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
		 }*/
		 	 
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
		 this.getManagedBean().setChkSchedule(false);
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
	
	
	
	/*Testing purpose started*/
	
	public void onRowSelect(SelectEvent event) {  
	    
		for( CtContractedItem contractedItems : ctContractedItem) {
		   if(contractedItems.getContractedItemId().equals(ctContractedItems.getContractedItemId())){
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
			          //flush();    	
		              this.getManagedBean().setSmSupplierBank(smSuppliers);
			         }
		         }
			 }catch(Exception e){
			   System.out.println(e);
		 }
			
	}

	/*Testing purpose End*/
	
	public void saveContract(String outcome) throws ParseException {
		
		IContractSO contractSO = null;
		try{
		  ContractReqDTO reqDTO = new ContractReqDTO();
		  contractSO = (IContractSO)BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
		  reqDTO.setCtContract(this.getManagedBean().getCtContract());
		  reqDTO.setOutcome(outcome);
		  reqDTO.setTaskId(this.getManagedBean().getTaskId());
		  if(outcome == BpmOutcomeConst.BPM_SAVE){
		     reqDTO.setOutcome("");
		  }else if(outcome == BpmOutcomeConst.BPM_REVISE)
		     reqDTO.setTaskId(this.getManagedBean().getTaskId());
		 
		  ContractResDTO resDTO = contractSO.saveContract(reqDTO);
		  System.out.println("Save Successful>>>:: "+ resDTO.isSuccessful());
	
		}catch(Exception ex){
		   log.error("Exception>>>" +ex);
		}finally{
		  contractSO = null;
		}
	}
	
	 public void onTransfer(TransferEvent event) {  
	        StringBuilder builder = new StringBuilder();  
	        for(Object item : event.getItems()) {  
	            builder.append(((SettingDetails) item).getAproverName()).append("<br />");  
	        }  
	          
	        FacesMessage msg = new FacesMessage();  
	        msg.setSeverity(FacesMessage.SEVERITY_INFO);  
	        msg.setSummary("Items Transferred");  
	        msg.setDetail(builder.toString());  
	        FacesContext.getCurrentInstance().addMessage(null, msg);  
	    }

	/*Code for agency*/
	public List<CtAgency> getCtAgencyList() {
		if(ctAgencyList == null ){
			ctAgencyList = new ArrayList<CtAgency>();
		
			CtAgency agencyDetails1 = new CtAgency(); 
			CtAgency agencyDetails2 = new CtAgency(); 
			agencyDetails1.setAgencyId(new Long(1));
			agencyDetails1.setCeilingAmount(5000.00);       
			agencyDetails1.setEffDate(new Date());
			agencyDetails1.setExpDate(new Date());
			PmOrgProfile pmOrgProfile = new PmOrgProfile();
			pmOrgProfile.setOrgCode("123451101");
			pmOrgProfile.setOrgName("KOLEJ MATRIKULASI NEGERIbx");
			agencyDetails1.setOrgProfile(pmOrgProfile);
			ctAgencyList.add(agencyDetails1);
			
			agencyDetails2.setAgencyId(new Long(2));
			agencyDetails2.setCeilingAmount(6000.00);       
			agencyDetails2.setEffDate(new Date());
			agencyDetails2.setExpDate(new Date());
			PmOrgProfile pmOrgProfile1 = new PmOrgProfile();
			pmOrgProfile.setOrgCode("123451102");
			pmOrgProfile.setOrgName("KOLEJ MATRIKULASI PULAU PINANG");
			agencyDetails1.setOrgProfile(pmOrgProfile1);
			ctAgencyList.add(agencyDetails1);
		}	
		return ctAgencyList;
	}

	public void setCtAgencyList(List<CtAgency> ctAgencyList) {
		this.ctAgencyList = ctAgencyList;
	}

	public CtDeductionClause[] getChkDeductionClause() {
		return chkDeductionClause;
	}

	public void setChkDeductionClause(CtDeductionClause[] chkDeductionClause) {
		if(chkDeductionClause != null)
			   this.chkDeductionClause = Arrays.copyOf(chkDeductionClause, chkDeductionClause.length);
    } 
	 
   public void addDeductionClause(ActionEvent event){
	   for (int i = 0; i < chkDeductionClause.length; i++){
	    	CtDeductionClause ad = chkDeductionClause[i];
	    	this.getManagedBean().getAddDeductionClause().add(ad); 
	    }
	    	
	    chkDeductionClause = null;		
	}
    
    public void deleteClauseDetails(){	
  		this.getManagedBean().getAddDeductionClause().remove(this.getManagedBean().getDeductionClause());
    }
	
    public void addDeductionClause(){
	 
    }
   
    public List<SmSupplierBank> getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(List<SmSupplierBank> smSupplier) {
		this.smSupplier = smSupplier;
	}

	public CtAgency[] getSelectedAgencyList() {
		return selectedAgencyList;
	}

	public void setSelectedAgencyList(CtAgency[] selectedAgencyList) {
		if(selectedAgencyList != null)
		   this.selectedAgencyList = Arrays.copyOf(selectedAgencyList, selectedAgencyList.length);;
	}
	
    public void onRowUnselect(UnselectEvent event) { }  
	
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
		if(this.getManagedBean().getContractValues().isProgressivePayment())
		  this.getManagedBean().setChkSchedule(true);
	}

	public List<CtSchedule> getCtSchedules() {
		return ctSchedules;
	}

	public void setCtSchedules(List<CtSchedule> ctSchedules) {
		this.ctSchedules = ctSchedules;
	}

	public List<CtFactoring> getCtFactoring() {
		return ctFactoring;
	}

	public void setCtFactoring(List<CtFactoring> ctFactoring) {
		this.ctFactoring = ctFactoring;
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
	
public void onChange(TabChangeEvent event) { }
	
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
	
	public void addDeductionDetails(){
   	 CtDeduction ctDeductions = new CtDeduction();
		 this.getManagedBean().getCtDeductionList().add(ctDeductions);
	}
	
	public void deleteDeductionDetails(){	
	     log.info("========================================================");
		 this.getManagedBean().getCtDeductionList().remove(this.getManagedBean().getCtDeduction());
	 }
	
	public void addScheduleDetails(){
     	 CtProgressivePayment ctProgressivePayment = new CtProgressivePayment();
        this.getManagedBean().getCtProgressivePaymentList().add(ctProgressivePayment);
	}
	
	public void deleteScheduleDetails(){			
		 this.getManagedBean().getCtProgressivePaymentList().remove(this.getManagedBean().getCtProgressivePayment());
	}
	
	public List<CtDeduction> getDeducationList(){
		
		 CtDeduction ctDeduction1 = new CtDeduction();
		 this.getManagedBean().getCtContract().getCtDeductions().add(ctDeduction1);
		 return null;
		
	}
}
