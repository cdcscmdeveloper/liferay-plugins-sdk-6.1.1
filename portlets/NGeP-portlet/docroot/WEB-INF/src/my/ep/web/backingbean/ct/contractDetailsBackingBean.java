package my.ep.web.backingbean.ct;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;




import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ct.ApproverSetting;
import my.ep.web.bean.ct.CheckBoxSelections;
import my.ep.web.bean.ct.ContractListItem;
import my.ep.web.bean.ct.CreateContractDetails;
import my.ep.web.bean.ct.ZoneDetails;
import my.ep.web.managedbean.ct.ContractDetailsManagedBean;

@ManagedBean
@RequestScoped
public class contractDetailsBackingBean extends BaseBackingBean<ContractDetailsManagedBean> {

	
	private static final String PROCESS_SUSPEND_PAGE="/xhtml/ct/fulfilmentdetails/suspension/suspend.xhtml";
	private static final String PROCESS_TERMINATE_PAGE="/xhtml/ct/fulfilmentdetails/termination/termination.xhtml";
	private static final String PROCESS_AMEND_PAGE="/xhtml/ct/fulfilmentdetails/amendment/amendment.xhtml";
	private static final String PROCESS_VIEW_PAGE="/xhtml/ct/agreement/viewAgreement.xhtml";
	 private CreateContractDetails createContractDetails;
	 private ContractListItem contractListItem;
	 private CheckBoxSelections checkBoxSelections;
	 private ApproverSetting approverSetting;
	 
	 
	

	private boolean activeSuspension;
	 
	public contractDetailsBackingBean(){
		 createContractDetails = new CreateContractDetails();
		 contractListItem = new ContractListItem();
		 checkBoxSelections = new CheckBoxSelections();
		 approverSetting = new ApproverSetting();
		 
		 rootNode = new DefaultTreeNode("root", null);  
		  
		    TreeNode zone1 = new DefaultTreeNode(new ZoneDetails("Zone Name  ZON TENGAH", "Delivery Period 15"), rootNode);  
		    TreeNode Zone2 = new DefaultTreeNode(new ZoneDetails("Zone Name ZON UTARA", "Delivery Period 21 Days"), rootNode);  
		    TreeNode work = new DefaultTreeNode(new ZoneDetails("PUTRAJAYA", " "), zone1);  
		    TreeNode primefaces = new DefaultTreeNode(new ZoneDetails("SELANGOR", " "), Zone2);  
		    
		    
		    rootNodeAgency = new DefaultTreeNode("root", null);  
		    createContractDetails.setMinistryName("KEMENTERIAN PELAJARAN");
		    createContractDetails.setDepartmentName("BAHAGIAN MATRIKULASI");
		    
		    TreeNode agencyOne = new DefaultTreeNode(new CreateContractDetails("KEMENTERIAN PELAJARAN"), rootNodeAgency);  
		    TreeNode agencytwo = new DefaultTreeNode(new CreateContractDetails("BAHAGIAN MATRIKULASI"), agencyOne); 
		    
		    TreeNode expenses = new DefaultTreeNode(new CreateContractDetails("0211780401","KOLEJ MATRIKULASI NEGERI SEMBILAN","5000.00"), agencytwo);  
	 }
	
	public void onChange(TabChangeEvent event) { 
	    System.out.println("tab id alam101 = >>>>>>>>>>>>>>>>>>>>>>>" + event.getTab().getId());
	}
	
	 public ApproverSetting getApproverSetting() {
			return approverSetting;
		}

		public void setApproverSetting(ApproverSetting approverSetting) {
			this.approverSetting = approverSetting;
		}
	
	public CheckBoxSelections getCheckBoxSelections() {
		return checkBoxSelections;
	}

	public void setCheckBoxSelections(CheckBoxSelections checkBoxSelections) {
		this.checkBoxSelections = checkBoxSelections;
	}
	 
	 public ContractListItem getContractListItem() {
		return contractListItem;
	}

	public void setContractListItem(ContractListItem contractListItem) {
		this.contractListItem = contractListItem;
	}
	
	 public CreateContractDetails getCreateContractDetails() {
			return createContractDetails;
		}


		public void setCreateContractDetails(CreateContractDetails createContractDetails) {
			this.createContractDetails = createContractDetails;
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
		
		/*Agency details*/
		
		private TreeNode rootNode;  
		  private TreeNode rootNodeAgency;  

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

		private TreeNode selectedNode; 
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
			
			 private List<CreateContractDetails> agencyDetails = null;
			
			public List<CreateContractDetails> getAgencyDetails() {
				return agencyDetails;
			}

			public void setAgencyDetails(List<CreateContractDetails> agencyDetails) {
				this.agencyDetails = agencyDetails;
			}
			
			private CreateContractDetails[] selectedAgencyList;





			public CreateContractDetails[] getSelectedAgencyList() {
				return selectedAgencyList;
			}


			public void setSelectedAgencyList(CreateContractDetails[] selectedAgencyList) {
				this.selectedAgencyList = selectedAgencyList;
			}
			

			/*Item details methode*/
			private String fulfillmentNo ="01234567889";
			  private boolean paymentHistory;
			
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

			private boolean itemPricing;
		   
			public boolean isItemPricing() {
				return itemPricing;
			}

			public void setItemPricing(boolean itemPricing) {
				this.itemPricing = itemPricing;
			}

			private boolean itemCeilingQuantity;
		  
		
	


			public boolean isItemCeilingQuantity() {
				return itemCeilingQuantity;
			}


			public void setItemCeilingQuantity(boolean itemCeilingQuantity) {
				this.itemCeilingQuantity = itemCeilingQuantity;
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
			    	
			    
						if(createContractDetails.getZoneType().equalsIgnoreCase("Item Pricing")){			
							itemPricing = true;
						}
						
						else if(createContractDetails.getZoneType().equalsIgnoreCase("Item Ceiling Quantity")){
							itemCeilingQuantity = true;
						}
			    			
			    	
					
			    }catch(Exception e){
			    	System.out.println(e);
			    }
			}

			/*Payment history*/
			
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
}
