/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.CTContractDataType;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeductionClause;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserGroupUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
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

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.util.PortalUtil;

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
		
	private TreeNode rootNode;
	private TreeNode selectedNode;
	private TreeNode rootNodeAgency;
	
	private String editorValue;
	private boolean showZoneDetails;
	private String fulfillmentNo = "01234567889";
	
	private CtSchedule[] selectedSchedule;
	private PmOrgProfile[] selectedPmOrgProfile;
	private CtPerformanceBond[] selectedBondList;
	private CtDeductionClause[] chkDeductionClause;
	private CtProgressivePayment[] selectedProgressive;
	private CtAgency[] selectedListOfAgency;
	
	
	ContractBackingBean ctBB = null;
	
	private final static String[] deductions;
	static {

		deductions = new String[1];
		deductions[0] = "Advanced Payment";
	}
	
	
	public String[] getDeductions() {
		return deductions;
	}
	
	public ContractDetailsBackingBean() {
		if(this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
			this.getManagedBean().setContractVer(new CtContractVer());
		}
		if(getManagedBean().getUserGroupMap() == null) {
			initializeApproverGroup();
		}
		if(getManagedBean().getItemType()==null) {
			getManagedBean().setItemType(ParameterConst.ITEM_TYPE_PRODUCT);
		}
		initializeRemarks();
	}
	
	private FacesScopes getFacesScopes(){
	  
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	@PostConstruct
	public void init() throws ParseException {
		 
		 ctBB = new ContractBackingBean();
		 ctBB.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
	 }
	
	private void initializeApproverGroup() {
		IUserSO userSO = null;
		try {
			userSO = (IUserSO)BusinessDelegate.getInstance().getServiceObject("pm",IUserSO.class);
			
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setUserId(getFacesScopes().getUserSessionBean().getUserId());
			UserResDTO resDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			
			//reqDTO.setOrgProfileId(resDTO.getUserOrgList().get(0).getPmOrgProfile().getOrgProfileId());
			reqDTO.setOrgProfileId(1250L);
			resDTO = userSO.retrieveUserGroupList(reqDTO);
			
			if(getManagedBean().getContractVer().getPmUserGroup()==null) {
				PmUserGroup pmUserGroup = new PmUserGroup();
				getManagedBean().getContractVer().setPmUserGroup(pmUserGroup);
			}
			if(getManagedBean().getApproverModel()==null) {
				List<String> source = new ArrayList<String>(); 
		        List<String> target = new ArrayList<String>();  
		        getManagedBean().setApproverModel(new DualListModel<String>(source, target));
			}			
			
			getManagedBean().setUserGroupMap(new HashMap<Long, PmUserGroup>());
			int i = 0;
			for(PmUserGroup userGroup : resDTO.getUserGroupList()) {
				getManagedBean().getUserGroupMap().put(userGroup.getUserGroupId(), userGroup);
				if(i==0) {
					getManagedBean().getContractVer().getPmUserGroup().setUserGroupId(userGroup.getUserGroupId());
					List<String> source = new ArrayList<String>();
					for(PmUserGroupUser user : userGroup.getPmUserGroupUsers()) {
						source.add(user.getPmUser().getUserName());
					}
					getManagedBean().getApproverModel().setSource(source);
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		} finally {
			userSO = null;
		}
	}
	
	public void preRenderView() {
		try {
			System.out.println("**********preRenderView**************");
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			if (taskId == null)
				return;

			FacesScopes face = new FacesScopes();
			String token = face.getBPMAuthenticationCtxToken();
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);

			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
		      
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = "Save";
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
				
			this.getManagedBean().setTaskName(res.getTaskName());
			this.getManagedBean().setAction(outcome);
			this.getManagedBean().setTaskId(taskId);

			ContractReqDTO ctReqDTO = new ContractReqDTO();
			CTContractDataType dataType = (CTContractDataType) obj;
			ctReqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
			ctReqDTO.setContractId(new Long(dataType.getContractId()));

		      IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct",IContractSO.class);
		      ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
		      this.getManagedBean().getApproverModel().getSource().clear();
			  if(resDTO.getCtContractVer().getPmUserGroup()!=null) {
					for(PmUserGroupUser user : resDTO.getCtContractVer().getPmUserGroup().getPmUserGroupUsers()) {
						getManagedBean().getApproverModel().getSource().add(user.getPmUser().getUserName());
					}
				} else {
					resDTO.getCtContractVer().setPmUserGroup(new PmUserGroup());
				}
			  if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
			    this.getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			    this.getManagedBean().getApproverModel().getSource().removeAll(resDTO.getApproverList());
			  } 
			 
		      this.getManagedBean().setContractVer(resDTO.getCtContractVer());
		      this.getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
	          CombineSupAddress(resDTO.getCtContractVer());
	          
	          this.getManagedBean().setRemarkList(resDTO.getCtRemarks());
			  this.getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			  ContractBackingBean ctBB = new ContractBackingBean();
			  ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
	
			  initializeRemarks();
			  
			  FacesContext fc = FacesContext.getCurrentInstance();
			  fc.getApplication().getNavigationHandler().handleNavigation(fc, null,  ContractManagementConst.NAV_TO_CONTRACT_DETAILS);
		
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
    
    /*Implementation of Bond Details*/
    public void addBondDetails() {
		CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
		this.getManagedBean().getContractVer().getCtPerformanceBonds().add(ctPerformanceBond);
	}
    
    public void delBondDetails() {
		
    	for (int i = 0; i < selectedBondList.length; i++) {
			CtPerformanceBond ctPerformanceBond = selectedBondList[i];
			this.getManagedBean().getContractVer().getCtPerformanceBonds().remove(ctPerformanceBond);
		}
    	selectedBondList = null;	
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
	
	public String fileNames(String filePath){
		 
		String fileName = null;
		File file = new File(filePath);
		fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
	    return fileName;
	}/*End Bond Details*/
	
	 public void selectedZoneValue(){
	    	this.getManagedBean().setShowZoneDetails(true);
	    	this.getManagedBean().setCtContractZoneDtls(this.getManagedBean().getSelectedCtZone().getCtContractZoneDtls());
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
    		if (this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				this.getManagedBean().setServiceItem(true);
			} else if (this.getManagedBean().getItemType().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
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
    	this.getManagedBean().setSelectedItem(null);
    	this.getManagedBean().setShowItem(null);
    }
  
    public void selectedItemList(){
    	this.getManagedBean().setServiceItem(false);
    	this.getManagedBean().setProductItem(true);
    }
   
    public void onRowSelect(SelectEvent event) {
   	
    }/*End*//*End Item Details*/
    
    private void flush() {
    	
		this.getManagedBean().setItemPricing(false);
		this.getManagedBean().setItemCeilingQuantity(false);
		this.getManagedBean().setChkSchedule(false);
		this.getManagedBean().setChkProgressive(false);
	}
    
    /*Deduction Start*/
    public void addDeductionDetails() {
		IParameterSO parameterSO;
		try {
			parameterSO = BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterReqDTO reqDTO = new ParameterReqDTO();
			reqDTO.setParameterType(ParameterTypeConst.DEDUCTION_TYPE);
			reqDTO.setParameterCode(ParameterConst.DEDUCTION_TYPE_ADVANCE_PAYMENT);
			
			CtDeduction ctDeductions = new CtDeduction();
			ctDeductions.setDeductionType(parameterSO.retrieveParameterByTypeAndCode(reqDTO).getPmParameter());
			this.getManagedBean().getContractVer().getCtDeductions().add(ctDeductions);
		} finally {
			parameterSO = null;	
		}
	}

	public void deleteDeductionDetails() {
		this.getManagedBean().getContractVer().getCtDeductions().remove(getManagedBean().getSelectedDeduction());
		getManagedBean().setSelectedDeduction(null);
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
	
	public void addDeductionClause() {}

    public void onEdit(RowEditEvent event){}
    
    public void onCancel(RowEditEvent event){}/*End Deduction*/

    /*Schedule Start*/
    public void progressivePayment() {
		
       flush();
	   if(this.getManagedBean().getScheduleOption().equalsIgnoreCase("1"))
		   this.getManagedBean().setChkSchedule(true);
	   else if(this.getManagedBean().getScheduleOption().equalsIgnoreCase("2"))
		  this.getManagedBean().setChkProgressive(true);
	}
    
    public void addScheduleDetails(){
		CtSchedule ctSchedules = new CtSchedule();
		int count = 1;
		for(CtSchedule ctSched : this.getManagedBean().getContractVer().getCtSchedules()){
			count++;
		}
		ctSchedules.setScheduleNo("S"+count);
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
	
    public String saveContract(String action){
		
	    IContractSO contractSO = null;
		try{
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
          
			this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_CREATION);	
            this.getManagedBean().getContractVer().getCtContract().setAgreement(true);
           
            reqDTO.setToken(token);
            reqDTO.setOutcome(action);
            reqDTO.setTaskId(this.getManagedBean().getTaskId());
            reqDTO.setUserLoginId(getFacesScopes().getUserSessionBean().getLoginId());
            if(isApprover()) {
            	this.getManagedBean().getContractVer().setApprover(getFacesScopes().getUserSessionBean());
            	this.getManagedBean().getContractVer().setApprovedDate(new Date());
			}
            reqDTO.setCtContract(this.getManagedBean().getCtContract());
            reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
			
		    ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
		    if(resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,"Record saved successfully.","Contract Name. : "+ 
				this.getManagedBean().getContractVer().getContractVerId()));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return "/xhtml/shared/acknowledge.xhtml";
				}
			    initializeRemarks();
			  }else {
					throw new EpException("Failed.");
			  }
		  }catch (Exception e) {
			log.error("Exception>>", e);
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		  }finally {
			contractSO = null;
		  }
		return null;	
	}
    
    public boolean isApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.APPROVER.value());
	}
	
   /* Approver details start
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
*/
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
			for(PmUserGroup userGroup : this.getManagedBean().getUserGroupMap().values()) 
				items[i++] = new SelectItem(userGroup.getUserGroupId(), userGroup.getGroupName());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	    return items;
	}/*End approval details*/
	
	public SelectItem[] getMinistryDropdown() {
		
		SelectItem[] items = null;     
		try{
		    int i = 1;
		    IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		    OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		    reqDTO.setOrgType(OrganizationType.MINISTRY.value());
		    OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		    List<PmOrgProfile> list = resDTO.getOrgProfileList();
		    items = new SelectItem[list.size()+1];
		    items[0] = new SelectItem(null, "- Select One -");
		      
			for(PmOrgProfile orgProfile : list) 
			  items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());   
		 }catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 }
	    return items;
	}
	
    public void reset(){
    	this.getManagedBean().setPmOrgProfileList(null);
    	 
     }
    
    public void resetDepartment(){
    	this.getManagedBean().setPmOrgProfileList(null);
    	 getPtjCodeName();
    	 
     }
	
	 public SelectItem[] getDepartmentDropdown() {
	
		SelectItem[] items = null;     
		try{
		   int i = 1;
		   IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
		   OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		   reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
		   reqDTO.setParentOrgProfileId(this.getManagedBean().getMinistryProfileId());
		   OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
		   List<PmOrgProfile> list = resDTO.getOrgProfileList();
		   items = new SelectItem[list.size()+1];
	       items[0] = new SelectItem(null, "- Select One -");
	       for(PmOrgProfile orgProfile : list) {
		       items[i++] = new SelectItem(orgProfile.getOrgProfileId(), orgProfile.getOrgName());
	       }
		 }catch (Exception e) {
	    	   e.printStackTrace();
	    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
	     }
	   return items;
	}
	 
	 public List<PmOrgProfile> getPtjCodeName(){
			
		   try{
		      IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
			  OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			  reqDTO.setOrgType(OrganizationType.DIVISION.value());
			 
			  if(this.getManagedBean().getDepartmentProfileId() == null)
				  this.getManagedBean().setDepartmentProfileId(1L);
			  
			  reqDTO.setParentOrgProfileId(this.getManagedBean().getDepartmentProfileId());
			  OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
			  this.getManagedBean().setPmOrgProfileList(resDTO.getOrgProfileList());
			}catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			 }
			return this.getManagedBean().getPmOrgProfileList();
		}
	 
	
	 public void addAgencyDetails(){
		
		 CtAgency ctAgency = new CtAgency();
		 for(int i=0; i<selectedPmOrgProfile.length; i++){
			 PmOrgProfile pmOrgProfile = selectedPmOrgProfile[i];
			 ctAgency.setOrgProfile(pmOrgProfile);
			 this.getManagedBean().getContractVer().getCtAgencies().add(ctAgency);
		  }
		  selectedPmOrgProfile = null;
	 }
	 
	 public void deleteAgencyDetails(){
			for (int i = 0; i < selectedListOfAgency.length; i++) {
				 CtAgency ctAgency = selectedListOfAgency[i];
				 this.getManagedBean().getContractVer().getCtAgencies().remove(ctAgency);
			}
			selectedListOfAgency = null;	
		}
	
	 public double validateServiceFee(double serviceFee){
		 try{
			  String parameterCode = retrieveParameterById(this.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterId());
			  if(parameterCode.equalsIgnoreCase(ContractConst.PARAMER_TYPE_ONOFF)){
				this.getManagedBean().getContractVer().getCtContract().setServiceFee(ContractConst.ONOFF_SERVICE_FEE);	
				return ContractConst.ONOFF_SERVICE_FEE;
			  }else{
				this.getManagedBean().getContractVer().getCtContract().setServiceFee(ContractConst.SCHEDULE_PERODIC_SERVICE_FEE);
				return ContractConst.SCHEDULE_PERODIC_SERVICE_FEE;
			 }
		 } catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		 return 0.0;
	 }
	 
	 public String retrieveParameterById(Long parameterId) throws NamingException {
			
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
			return resDTO.getPmParameter().getParameterCode();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 } 
	 
	 public String getDeductionType(Long parameterId){
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterId(parameterId);
			reqDTO.setParameterType("DDT");
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterDescListByType(reqDTO);
			return resDTO.getParameterDescList().get(0).getCodeName();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 }
	 
	public String gotoTerminatePage() {

		IContractSO contractSO = null;
		ContractResDTO resDTO  = null;
		ContractReqDTO reqDTO  = null;
		TerminationBackingBean trBB= null;
		try{
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
		}catch(Exception e) {
				e.printStackTrace();
		} finally{
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
		try{
            reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setContractVerId(this.getManagedBean().getCtContract().getContractId());
			reqDTO.setUpdateAction(true);
			resDTO = contractSO.retrieveContractVer(reqDTO);
			suspBB = new SuspensionBackingBean();
			suspBB.getManagedBean().setContractVer(resDTO.getCtContractVer());	
		} catch(Exception e) {
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
				
			reqDTO.setContractId(resDTO.getCtContractVer().getCtContract().getContractId());
			reqDTO.setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
			resDTO = contractSO.retrieveContractVerByIdDoctype(reqDTO);
			amendBB.getManagedBean().setAmendHistoryList(resDTO.getAmendHistoryList());
		}catch (Exception e) {
				e.printStackTrace();
		}finally {
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
		try{

			reqDTO = new ContractReqDTO();
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
			reqDTO.setContractId(this.getManagedBean().getCtContract().getContractId());
			resDTO = contractSO.retrieveContract(reqDTO);
			aBB = new AgreementBackingBean();
			aBB.getManagedBean().setCtContract(resDTO.getContract());		
		}catch(Exception e) {
				e.printStackTrace();
		}finally {
			contractSO = null;
			resDTO = null;
			reqDTO = null;
			aBB= null;
		}
		return PROCESS_VIEW_PAGE;
	}
	
	public String convertTime(Date time){ 
        String str ="";
    	Format format = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT); 
        if(time == null)
        	return str;
        return format.format(time); 
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

	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
	}

	public PmOrgProfile[] getSelectedPmOrgProfile() {
		return selectedPmOrgProfile;
	}

	public void setSelectedPmOrgProfile(PmOrgProfile[] selectedPmOrgProfile) {
		this.selectedPmOrgProfile = selectedPmOrgProfile;
	}

	public CtAgency[] getSelectedListOfAgency() {
		return selectedListOfAgency;
	}

	public void setSelectedListOfAgency(CtAgency[] selectedListOfAgency) {
		this.selectedListOfAgency = selectedListOfAgency;
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
	
	private void initializeRemarks() {
		initiateRemark(getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(getManagedBean().getContractVer().getSettingRemarks());
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
