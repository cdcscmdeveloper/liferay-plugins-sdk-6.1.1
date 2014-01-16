/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.service.ct.IContractSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.ss.quationtender.ScCommittee;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.ct.ContractManagementConst;
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
	
	public AmendmentBackingBean(){
		
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE,BpmOutcomeConst.BPM_SUBMIT});
		}
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
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
			
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
		    System.out.println("dataType.getContractId()>>"+dataType.getContractId());
			ctReqDTO.setContractVerId(new Long(dataType.getContractId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
		

			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			
			this.getManagedBean().setTaskId(taskId);
			this.getManagedBean().setAction(res.getAction());
			ctReqDTO.setUpdateAction(false);
			
	        if(res.getTaskName().equalsIgnoreCase("Confirm Amendment Meeting") || 
	        		res.getTaskName().equalsIgnoreCase("Update Amendment Result")){
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
	      } catch(Exception e){
				e.printStackTrace();
			}

		}
	
	 public String saveContractAmendment(String action){
			
		    IContractSO contractSO = null;
			try{
				
				ContractReqDTO reqDTO = new ContractReqDTO();
				String token = getFacesScopes().getBPMAuthenticationCtxToken();
				
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
	   
	            reqDTO.setToken(token);
	            reqDTO.setOutcome(action);
	            reqDTO.setTaskId(this.getManagedBean().getTaskId());
	            reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
	            this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	           
	          /*  this.getManagedBean().getContractVer().getCtAmendment().setCommittee(new ScCommittee());
	            this.getManagedBean().getContractVer().getCtAmendment().getCommittee().setCommitteeId(1L);
	            this.getManagedBean().getContractVer().getCtAmendment().setBpkApprover(new PmUser());
	            this.getManagedBean().getContractVer().getCtAmendment().getBpkApprover().setUserId(1L);
	            this.getManagedBean().getContractVer().getCtAmendment().setSecretary(new PmUser());
	            this.getManagedBean().getContractVer().getCtAmendment().getSecretary().setUserId(1L);*/
	          
	            reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
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
	    		if (this.getManagedBean().getItemType().equalsIgnoreCase("Service")) {
					this.getManagedBean().setServiceItem(true);
				
	    		} else if (this.getManagedBean().getItemType().equalsIgnoreCase("Product")) {
					this.getManagedBean().setProductItem(true);
				
				} else if (this.getManagedBean().getItemType().equalsIgnoreCase("ProductAndService")) {
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
	    
	    public String getEditorValue() {
		   return editorValue;
	    }

	    public void setEditorValue(String editorValue) {
		  this.editorValue = editorValue;
	   }
	 	 
}
