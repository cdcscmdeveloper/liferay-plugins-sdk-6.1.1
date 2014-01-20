/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backing Bean For Contract Amendment Details
 * 
 * @author Alam
 * @since 1/13/2014
 */

@ManagedBean (name = "amendmentBackingBean")
@RequestScoped
public class AmendmentBackingBean extends BaseBackingBean<AmendmentManagedBean> {
    
	private static Logger log = Logger.getLogger(AmendmentBackingBean.class);
	private String editorValue;
	private TreeNode rootNodeAgency;
	
	public AmendmentBackingBean(){
		
		rootNodeAgency = new DefaultTreeNode("root", null);
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE,"Submit"});
		}
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void initializeContractValue() throws ParseException {
	     
		  ContractBackingBean ctBackingBean = new ContractBackingBean();
		  ctBackingBean.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
	}

	public void preRenderView(){
		
		try{	
			
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
		    
			if(taskId==null) {
				return;
			}
			
			FacesScopes face = new FacesScopes();
			String token =face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			ContractReqDTO ctReqDTO = new ContractReqDTO();
			
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			ctReqDTO.setUpdateAction(false);
			this.getManagedBean().setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			this.getManagedBean().setAction(res.getAction());
			this.getManagedBean().setTaskName(res.getTaskName());
			
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
			ctReqDTO.setUserLoginId(dataType.getContractAdmin());
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getAmendmentId()));
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
	        if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) || 
	           res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)){
			   FacesContext fc = FacesContext.getCurrentInstance();
			   this.getManagedBean().setAmendAckw(true);
			   this.getManagedBean().setAmendDisable(true);  
			   fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
			
		    }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)){
				FacesContext fc = FacesContext.getCurrentInstance();
			    this.getManagedBean().setAmendDisable(false);  
				this.getManagedBean().setApprovrLetter(true);
			    this.getManagedBean().setAmendAckw(true);
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
					 
			 }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)){
				 FacesContext fc = FacesContext.getCurrentInstance();
				 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
				
			 }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)){
				 FacesContext fc = FacesContext.getCurrentInstance();
			     this.getManagedBean().setAmendAckw(true);
				 this.getManagedBean().setAmendDisable(false);  
				 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
					  
			 }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_HT_NAME)){
				 FacesContext fc = FacesContext.getCurrentInstance();
				 this.getManagedBean().setApprovrLetter(true);
				 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT);
					  
			 }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)){
				 FacesContext fc = FacesContext.getCurrentInstance();
				 this.getManagedBean().setAmendAckw(true);
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT_PBQCC);
			 
			 } else{
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT);
			 }
	      } catch(Exception e){
				e.printStackTrace();
			}

		}
	
	 public String saveContractAmendment(String action){
			
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		try{
				
			reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
				
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
	   
	        reqDTO.setToken(token);
	        reqDTO.setOutcome(action);
	        reqDTO.setTaskId(this.getManagedBean().getTaskId());
	        reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
	        this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	        this.getManagedBean().getContractVer().setDocNo("FA"+new Long(System.currentTimeMillis()).toString().substring(10));
	        this.getManagedBean().getContractVer().getCtContractValue().setContractPhysicalNo("PN"+new Long(System.currentTimeMillis()).toString().substring(10));  
	         
	        if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() ||
	           this.getManagedBean().getContractVer().getCtAmendment().isValueChange() ){
	            ChkComplyAP201Rule();	
	        }
	            
	        reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
	        reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
	        
	        reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
	        reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
	        
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			if(resDTO.isSuccessful()) {
					
			   log.info("Record saved successfully");
			   FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ 
			   this.getManagedBean().getContractVer().getContractVerId()));
			   if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || 
				  (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
				    return "/xhtml/shared/acknowledge.xhtml";
				}
			 } else{
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
	 
	 public void ChkComplyAP201Rule(){
		
		if(this.getManagedBean().getContractVer().getCtContractValue().getContractAmount() > ContractConst.CONTRACT_ADDITIONAL_VALUE){
			this.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		
		 }if(getYear(this.getManagedBean().getContractVer().getCtContractValue().getEffDate(),
				 this.getManagedBean().getContractVer().getCtContractValue().getExpDate()) > 2){
			 this.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		}
	 }
	 
	 private int getYear(Date curDate, Date ctStartDate){ 
	 
		  SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
	      Integer.parseInt(simpleDateformat.format(curDate));
	      return Integer.parseInt(simpleDateformat.format(ctStartDate))- Integer.parseInt(simpleDateformat.format(curDate));
	 }
	 
	 public void addContractValue(){
		  
		 double firstVaue = this.getManagedBean().getContractVer().getCtContractValue().getAdditionalAmount();
		 double contractValue = this.getManagedBean().getContractVer().getCtContractValue().getContractAmount();
		 contractValue = contractValue + firstVaue - this.getManagedBean().getSecondValue();
		
		 this.getManagedBean().setSecondValue(firstVaue);
		
		 this.getManagedBean().getContractVer().getCtContractValue().setContractAmount(contractValue);
	 }
	 
	 public void ChkAmendmentAction(){
	    	
	   try {
		
		  if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend())
			  this.getManagedBean().getContractVer().getCtAmendment().setContractExtend(true);
				
		  else if(this.getManagedBean().getContractVer().getCtAmendment().isPriceChange())
			   this.getManagedBean().getContractVer().getCtAmendment().setPriceChange(true);
			  
		  else if(this.getManagedBean().getContractVer().getCtAmendment().isQuantityChange())
			   this.getManagedBean().getContractVer().getCtAmendment().setQuantityChange(true);
			  
		  else if(this.getManagedBean().getContractVer().getCtAmendment().isClauseChange())
			   this.getManagedBean().getContractVer().getCtAmendment().setClauseChange(true);
				
		  else if(this.getManagedBean().getContractVer().getCtAmendment().isValueChange())
			   this.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
				
	    } catch (Exception e) {
				System.out.println(e);
		  }
	 }
	 
	 public void changeItemTypes(){
	    	
	    try{
	    	
	    	flushItem();
	    	if(this.getManagedBean().getItemType().equalsIgnoreCase("Service")) {
				this.getManagedBean().setServiceItem(true);
				
	    	}else if(this.getManagedBean().getItemType().equalsIgnoreCase("Product")) {
					 this.getManagedBean().setProductItem(true);
				
			}else if(this.getManagedBean().getItemType().equalsIgnoreCase("ProductAndService")) {
					  this.getManagedBean().setProductAndService(true);
            }
	      } catch (Exception e) {
				System.out.println(e);
		  }
	    }
	 
	 public void flushItem(){
	    	this.getManagedBean().setServiceItem(false);
	    	this.getManagedBean().setProductItem(false);
	    	this.getManagedBean().setProductAndService(false);
	    }
	  
	 public void selectedItemList(){
   	        this.getManagedBean().setServiceItem(false);
   	        this.getManagedBean().setProductItem(true);
       }
	 
	 public void  selectedServiceList(){
		   this.getManagedBean().setServiceItem(true);
	 	  this.getManagedBean().setProductItem(false);
	    }
	 
	 /*Implementation for item details*/
	    public List<SelectItem> getItemDetailsMethod() {

			List<SelectItem> list = new ArrayList<SelectItem>();
			list.add(new SelectItem("Item Pricing", "Item  Pricing"));
			list.add(new SelectItem("Item Ceiling Quantity", "Item Ceiling Quantity"));
			return list;
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
	    
	    public List<SelectItem> getItemTypeMethode(){
	    	
	    	List<SelectItem> list = new ArrayList<SelectItem>();
			list.add(new SelectItem("Product", "Product"));
			list.add(new SelectItem("Service", "Service"));
			list.add(new SelectItem("ProductAndService", "Product And Service"));
			return list;
	    }

	    private void flush() {
	    	
			this.getManagedBean().setItemPricing(false);
			this.getManagedBean().setItemCeilingQuantity(false);
		}
	    
	    public void onEdit(RowEditEvent event) {  
	      
	    }  
	      
	    public void onCancel(RowEditEvent event) {  
	    	
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
	    
	    public String getEditorValue() {
		   return editorValue;
	    }

	    public void setEditorValue(String editorValue) {
		  this.editorValue = editorValue;
	   }

		public TreeNode getRootNodeAgency() {
			return rootNodeAgency;
		}

		public void setRootNodeAgency(TreeNode rootNodeAgency) {
			this.rootNodeAgency = rootNodeAgency;
		} 
	 	 
}
