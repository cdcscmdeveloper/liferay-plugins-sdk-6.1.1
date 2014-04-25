/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
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
import javax.naming.NamingException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.LocationDTO;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

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
	private CtPerformanceBond[] selectedBondList;
	private String editorValue;
	private double secondValue =0.0;

	public AmendmentBackingBean(){
		if(this.getManagedBean().getAction() == null) {
			 this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
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
			if(taskId==null) 
			return;
			
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
			
			if(resDTO.getCtContractVer().getCtContractItems() != null && resDTO.getCtContractVer().getCtContractItems().size() > 0){
			   if(this.getManagedBean().getCtItemList() == null)
			      this.getManagedBean().setCtItemList(new ArrayList<CtContractItem>());
			   else
			      this.getManagedBean().getCtItemList().clear();
			   for(CtContractItem item : this.getManagedBean().getContractVer().getCtContractItems()) {
				   if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT))
					  this.getManagedBean().getCtItemList().add(item);
			   }
			}
			
			CombineSupAddress(this.getManagedBean().getContractVer());
			retriveZonesByContractVer();
			
			ContractBackingBean ctBB = new ContractBackingBean();
			initializeRemarks();
			ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
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
					  
			 }
//			 if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_HT_NAME)){
//				 FacesContext fc = FacesContext.getCurrentInstance();
//				 this.getManagedBean().setApprovrLetter(true);
//				 fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT);
//					  
//			 }
			 if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)){
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
	
    public void CombineSupAddress(CtContractVer ctContractVer){
    	
    	StringBuilder address = new StringBuilder();
    	String strSpace =", ";
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
	
	 public String saveContractAmendment(String action){
			
		IContractSO contractSO = null;
		ContractReqDTO reqDTO = null;
		try{
				
			reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();	
			reqDTO.setToken(token);
	        reqDTO.setOutcome(action);
	        reqDTO.setTaskId(this.getManagedBean().getTaskId());
	        
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
	   
	        if(this.getManagedBean().getUserLoginId() == null)
	             reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
	        else
	        	reqDTO.setUserLoginId(this.getManagedBean().getUserLoginId());
	       
	        if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0)
				this.getManagedBean().getContractVer().getCtContractItems().clear();
            this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtItemList());
	        
	        this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	       /* if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() || this.getManagedBean().getContractVer().getCtAmendment().isValueChange() ){
	            ChkComplyAP201Rule();	
	        } */  
	            
	        reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
	        reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
	        
	        reqDTO.setUserRole(getUserRole());
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
	 
	 private String getUserRole() {		
			if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
				return RoleCode.MIN_CONTRACT_ADMIN.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
				return RoleCode.CT_APPROVER.value();
			else 
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
		  
		 double firstVaue = this.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue();
		 double contractValue = this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue();
		 contractValue = contractValue + firstVaue - this.getManagedBean().getContractVer().getCtAmendment().getSecondValue();
		 
		 this.getManagedBean().getContractVer().getCtAmendment().setSecondValue(firstVaue);
		 this.getManagedBean().getContractVer().getCtContractValue().setContractAmount(new BigDecimal(contractValue));
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
	
	public void initializeRemarks() {
		initiateRemark(getManagedBean().getContractVer().getItemRemarks());
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
	
	public SelectItem[] retrieveFinancialOrgList() throws NamingException {
		
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;
		try{
		    int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		    for(PmFinancialOrg pmFinancialOrg : list) 
				  items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());   
	    }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}   	
	   return items;
	}

	/*Implementation of Bond Details*/
    public void addBondDetails() {
		CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
		if(ctPerformanceBond.getFinancialOrg() == null)
			ctPerformanceBond.setFinancialOrg(new PmFinancialOrg());
		this.getManagedBean().getContractVer().getCtPerformanceBonds().add(ctPerformanceBond);
	}
    
    public void delBondDetails() {
    	for(int i = 0; i < selectedBondList.length; i++) {
			CtPerformanceBond ctPerformanceBond = selectedBondList[i];
			this.getManagedBean().getContractVer().getCtPerformanceBonds().remove(ctPerformanceBond);
		}selectedBondList = null;	
	}
	
	public String fileNames(String filePath){
		String fileName = "";
		if(filePath == null || filePath=="")
		  return fileName;
		
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	    return fileName;
	}
	
	public void downloadFile(String filePath) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		String fileName = null;
		
		File file = new File(filePath);
		fileName = file.getName();
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
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
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
	
	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		String filePath = "C:/projects/uploads/"+item.getFileName();
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    int i=0;
	    for(CtPerformanceBond ctPerformanceBond : this.getManagedBean().getContractVer().getCtPerformanceBonds()){
	      if(ctPerformanceBond.equals(this.getManagedBean().getCtPerformanceBond()))
	    	{
	    	  this.getManagedBean().getContractVer().getCtPerformanceBonds().get(i).setFilePath(filePath);
	        }i++;
	    }
	}
	
	public void chkJustificationAction(){
		  try {
			    if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && !this.getManagedBean().isChkConceValue()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
			    }else if(this.getManagedBean().getContractVer().getCtContractValue().isConcession()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setConcession(true);  
			    	if(!this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && !this.getManagedBean().isChkConceValue()){
				    	  this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(true);
				    	  this.getManagedBean().setChkConceValue(true);
				     }
			    }else if(this.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt() && this.getManagedBean().isChkConceValue()){
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    	this.getManagedBean().setChkConceValue(false);
			    }else{
			    	this.getManagedBean().getContractVer().getCtContractValue().setAp201Exempt(false);
			    	this.getManagedBean().getContractVer().getCtContractValue().setConcession(false); 
			    	this.getManagedBean().setChkConceValue(false);
			    }
		   } catch (Exception e) {
				System.out.println(e);
		   }
	}
	 
	 public SelectItem[] getStatusDropdown() {
			SelectItem[] items = null;
			try {
				items = new SelectItem[2];
				int i = 0;
				items[i++] = new SelectItem(1, "Active");
				items[i++] = new SelectItem(2, "Inactive");
				
			}catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}
			return items;
		}
	
	 public void chkBooleanVlaue(){
		 
		 if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend())
			  this.getManagedBean().setChkAmendExtendValue(true);
		    
		 if(this.getManagedBean().getContractVer().getCtAmendment().isQuantityChange() || 
				 this.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()||
				 this.getManagedBean().getContractVer().getCtAmendment().isValueChange())
			    this.getManagedBean().setChkAmendOtherValue(true);
		 
		 if(this.getManagedBean().getContractVer().getCtAmendment().isClauseChange())
			 this.getManagedBean().setChkClause(true);
		 
		 if(this.getManagedBean().getContractVer().getCtAmendment().isQuantityChange() && 
				 this.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() &&
				 this.getManagedBean().getContractVer().getCtAmendment().isValueChange() &&
				 this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() &&
				 this.getManagedBean().getContractVer().getCtAmendment().isClauseChange())
			 this.getManagedBean().setChkAmendAllValue(true);
			 
		 } 
	 
	 public void retriveZonesByContractVer() {
			
			TreeNode rootLocationTree =  new DefaultTreeNode("Root", null);
			TreeNode zoneNode =  null;
			if(this.getManagedBean().getContractVer().getCtContractZones() != null) {
			   for(CtContractZone zone : this.getManagedBean().getContractVer().getCtContractZones()) {
				   if(zone.getDeliveryTermUom() == null){
					   zone.setDeliveryTermUom(new PmParameter()); 
					   zone.getDeliveryTermUom().setParameterId(0L);
				   }
				   zoneNode = new DefaultTreeNode("zone", new LocationDTO(zone.getContractZoneId(),zone.getZoneName(), zone.getDeliveryTerm(), zone.getDeliveryTermUom().getParameterId()), rootLocationTree);
				   TreeNode stateNode = null;
				   List<PmState> state = new ArrayList<PmState>();
				   if(zone.getCtContractZoneDtls() != null) {
					 for(CtContractZoneDtl zoneDtl : zone.getCtContractZoneDtls()) {
					   if(zoneDtl.getState() != null){
						  if(!state.contains(zoneDtl.getState())){
							  stateNode = new DefaultTreeNode("state",new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getState()) , zoneNode);
						   }state.add(zoneDtl.getState());
					    }
					   if(zoneDtl.getDistrict() != null){
						   new DefaultTreeNode("district", new LocationDTO(zoneDtl.getContractZoneDtlId(),zoneDtl.getDistrict()), stateNode);
					   }
					 }
				  }
				}
			 this.getManagedBean().setCtZoneTrees(rootLocationTree);
		  }
		} 
		
	    public void getZoneObjDetails() {
	    	if(this.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
	    		for(CtContractZone ctContractZone : getManagedBean().getContractVer().getCtContractZones()) {
	    			for(TreeNode zonename : this.getManagedBean().getCtZoneTrees().getChildren()){
	    				if(ctContractZone.getContractZoneId().longValue()==((LocationDTO)zonename.getData()).getZoneId().longValue()) {
	    					if(ctContractZone.getDeliveryTermUom() == null)
	     					   ctContractZone.setDeliveryTermUom(new PmParameter());
	     					ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
	     					ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
	     				}
	    			}
	    		}
	    	}
		}
	    
	    public void editItemPriceOnIndexs(){
	    	
	    	List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
	    	CtItemPrice addDupObj = new CtItemPrice();
	    	for(CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()){
	    	 ctItemPriceList.add(ctItemPrice); 
	    	  if(!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()){
	    		 if(ctItemPrice.equals(this.getManagedBean().getCtItemPrices())){
	    			addDupObj.setFromQty(ctItemPrice.getFromQty());
	    			addDupObj.setToQty(ctItemPrice.getToQty());
	    			addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
	    			addDupObj.setEffDate(ctItemPrice.getEffDate());
	    			addDupObj.setExpDate(ctItemPrice.getExpDate());
	    			if(ctItemPrice.getCtContractZone() != null){
	    				addDupObj.setCtContractZone(new CtContractZone());
	    			    addDupObj.setCtContractZone(ctItemPrice.getCtContractZone());
	    			}if(ctItemPrice.getCtContractItem() != null){
	    				addDupObj.setCtContractItem(new CtContractItem());
	    				addDupObj.setCtContractItem(ctItemPrice.getCtContractItem());
	    			}
	    			addDupObj.setSubSequence(ctItemPrice.getSubSequence());
	    			addDupObj.setChangePrice(true);
	    			ctItemPrice.setChkIndexOfRow(true);
	    			ctItemPriceList.add(addDupObj);
	    		  }
	    		}  
	    	  }this.getManagedBean().getSelectedItem().getCtItemPrices().clear();
		    this.getManagedBean().getSelectedItem().getCtItemPrices().addAll(ctItemPriceList);
		    ctItemPriceList.clear();	
	    }
	    
        public void deleteItemPriceOnIndexs(){
	    	this.getManagedBean().getSelectedItem().getCtItemPrices().remove(this.getManagedBean().getSelectedIndexOfPrice());
	    }
	    
	    public void editCeilingQuantityOnIndexs(){
	    	
	    	List<CtItemCeilingQty> ctItemCeilingQtys = new ArrayList<CtItemCeilingQty>();
	    	CtItemCeilingQty addDupObj = new CtItemCeilingQty();
	    	for(CtItemCeilingQty ceilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()){
	    		ctItemCeilingQtys.add(ceilingQty); 
	    		if(ceilingQty.equals(this.getManagedBean().getCtItemCeilingQtys())){
	    			addDupObj.setCeilingQuantity(ceilingQty.getCeilingQuantity());
	    			addDupObj.setEffDate(ceilingQty.getEffDate());
	    			addDupObj.setExpDate(ceilingQty.getExpDate());
	    			if(ceilingQty.getCtAgency() != null){
	    				addDupObj.setCtAgency(new CtAgency());
	    			    addDupObj.setCtAgency(ceilingQty.getCtAgency());
	    			}if(ceilingQty.getCtContractItem() != null){
	    				addDupObj.setCtContractItem(new CtContractItem());
	    				addDupObj.setCtContractItem(ceilingQty.getCtContractItem());
	    			}
	    			
	    			addDupObj.setChangeCeilQuantity(true);
	    			ctItemCeilingQtys.add(addDupObj);
	    		}
		    }this.getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		    this.getManagedBean().getSelectedItem().getCtItemCeilingQties().addAll(ctItemCeilingQtys);
		    ctItemCeilingQtys.clear();	
	    }
	    
	    public void deleteCeilingQuantityOnIndexs(){
	    	this.getManagedBean().getSelectedItem().getCtItemCeilingQties().remove(this.getManagedBean().getSelectedIndexOfCeilingQty());	    }
	    
	    public CtPerformanceBond[] getSelectedBondList() {
			return selectedBondList;
		}

		public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
			this.selectedBondList = selectedBondList;
		}
		
		public double getSecondValue() {
			return secondValue;
		}

		public void setSecondValue(double secondValue) {
			this.secondValue = secondValue;
		}
}
