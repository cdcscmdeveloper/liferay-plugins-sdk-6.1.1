/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.Format;
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

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.util.PortalUtil;

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
			this.getManagedBean().setAction(new String[] {"Save","Submit"});
		}
		if(getManagedBean().getItemType()==null) {
			getManagedBean().setItemType(ParameterConst.ITEM_TYPE_PRODUCT);
		}
		if(getManagedBean().getContractVer()!=null && getManagedBean().getContractVer().getItemRemarks().size()==0) {
			initiateRemark(getManagedBean().getContractVer().getItemRemarks());
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
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getAmendmentId()));
			this.getManagedBean().setUserLoginId(dataType.getContractAdmin());
			
			IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			this.getManagedBean().setContractVer(resDTO.getCtContractVer());
			this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
			ContractBackingBean ctBB = new ContractBackingBean();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			
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
	        
	        if(this.getManagedBean().getUserLoginId() == null)
	             reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
	        else
	        	reqDTO.setUserLoginId(this.getManagedBean().getUserLoginId());
	       
	        this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	        //TODO change it
	        if( this.getManagedBean().getContractVer().getDocNo()==null) {
	        	this.getManagedBean().getContractVer().setDocNo("FA"+new Long(System.currentTimeMillis()).toString().substring(10));
	        }
	         
	        if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() || this.getManagedBean().getContractVer().getCtAmendment().isValueChange() ){
	            ChkComplyAP201Rule();	
	        }   
	            
	        reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
	        reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
	        
	        reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
	        reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
	        
	        if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				reqDTO.getCtContractVer().setApprover(getFacesScopes().getUserSessionBean());
				reqDTO.getCtContractVer().setApprovedDate(new Date());
	        }
			
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
		
	    Long temp = this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().longValue();
		if(temp > ContractConst.CONTRACT_ADDITIONAL_VALUE){
			this.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		
		 }if(getYear(this.getManagedBean().getContractVer().getCtContractValue().getEffDate(),
				 this.getManagedBean().getContractVer().getCtContractValue().getExpDate()) > 2){
			 this.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		
		 } if(this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			 this.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		}
	 }
	 
	 private int getYear(Date curDate, Date ctStartDate){ 
	 
		  SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
	      Integer.parseInt(simpleDateformat.format(curDate));
	      return Integer.parseInt(simpleDateformat.format(ctStartDate))- Integer.parseInt(simpleDateformat.format(curDate));
	 }
	 
	 public void addContractValue(){
		  
		 double firstVaue = this.getManagedBean().getContractVer().getCtContractValue().getAdditionalAmount().doubleValue();
		 double contractValue = this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue();
		 contractValue = contractValue + firstVaue - this.getManagedBean().getSecondValue();
		
		 this.getManagedBean().setSecondValue(firstVaue);
		
		 this.getManagedBean().getContractVer().getCtContractValue().setContractAmount(new BigDecimal(contractValue));
	 }
	 
    public void changeItemTypes(){
    	try{
    		flushItem();
    		if (this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				this.getManagedBean().setServiceItem(true);
			} else if (this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
				this.getManagedBean().setProductItem(true);
			}
    	} catch (Exception e) {
			System.out.println(e);
		}
    }
    

    public void changeField() {

		try {
			flushItem();
			if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Pricing")) {
				this.getManagedBean().setItemPricing(true);

			} else if (this.getManagedBean().getShowItem().equalsIgnoreCase("Item Ceiling Quantity")) {
				this.getManagedBean().setItemCeilingQuantity(true);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	 
	public void flushItem() {
		this.getManagedBean().setItemPricing(false);
		this.getManagedBean().setItemCeilingQuantity(false);
		this.getManagedBean().setProductItem(false);
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
		
		public String convertTime(Date time){ 
	        String str ="";
	    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
	        if(time == null)
	        	return str;
	        return format.format(time); 
	    } 
		
	public List<SelectItem> getItemTypeDropDown(){
    	List<SelectItem> list = new ArrayList<SelectItem>();
		SelectItem[] items = getDropdownCodeValues(ParameterTypeConst.SC_ITEM_TYPE);
		for(SelectItem item : items) {
			if(item.getValue()!=null) {
				if(item.getValue().toString().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT) || item.getValue().toString().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
					list.add(item);
				}
			}
		}
		return list;
    }		
	
	public List<CtContractItem> getContractItemList() {
		List<CtContractItem> contractItemList = new ArrayList<CtContractItem>();
		for(CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
			if(item.getItemType().getParameterCode().equalsIgnoreCase(getManagedBean().getItemType())) {
				contractItemList.add(item);
			}
		}
		return contractItemList;
	}	
	
	private void initiateRemark(List<CtRemark> remarks) {
		if(remarks.size()==0 || !compareUserName(remarks.get(0).getCreatedUser().getLoginId())) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}
	 	 
}
