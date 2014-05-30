/**
ctItemList * Copyright © 2013 Archisoft. All rights reserved.
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
import java.util.Calendar;
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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
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
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.CtMeeting;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZoneDtl;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.LocationDTO;
import my.ep.web.constant.CommonUIConst;
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
	private CtCommitteeMember[] selectedCommitteMembers;
	private PmUser[] selectedPmUser;
	private CtMeeting[] selectedMeetingUser;
	private List<SelectItem> populateBankAccountList;
	
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
		  if(this.getManagedBean().getCtCommitteeMember() == null){
			  this.getManagedBean().setCtCommitteeMember(new CtCommitteeMember());
			  if(this.getManagedBean().getCtCommitteeMember().getUserId() == null){
				  this.getManagedBean().getCtCommitteeMember().setUserId(new PmUser());				  
			  }
			  if(this.getManagedBean().getCtCommitteeMember().getMemberRole() == null){
				  this.getManagedBean().getCtCommitteeMember().setMemberRole(new PmParameter());
			  }
		  }
	}

	public void preRenderView() {
		IContractSO contractSO = null;
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
			ContractReqDTO ctReqDTO = new ContractReqDTO();

			reqDTO.setCtxToken(token);
			reqDTO.setTaskId(taskId);
			ctReqDTO.setUpdateAction(false);
			this.getManagedBean().setTaskId(taskId);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			this.getManagedBean().setTaskName(res.getTaskName());
			String[] outcome = new String[res.getAction().length + 1];
			outcome[0] = BpmOutcomeConst.BPM_SAVE;
			int i = 1;
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}this.getManagedBean().setAction(outcome);
			
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
			ctReqDTO.setTaskName(res.getTaskName());
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			setAmendmentContractVer(resDTO);
			
	        if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) || 
	           res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)){
			   this.getManagedBean().setMemberSignOff(true);  
			   this.getManagedBean().setPbConfirmMeeting(true);
				
		    }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)){
				this.getManagedBean().setLetterOfAcceptance(true);
		        this.getManagedBean().setPrepareLOA(true);		
		        this.getManagedBean().setMemberSignOff(true);
		        this.getManagedBean().setWitnessHeader("AGENCY WITNESS DETAILS");
		        ctReqDTO.setCtContractVer(this.getManagedBean().getContractVer());
		        if(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() == null){
		        	ContractResDTO resDTO1 = contractSO.setScLoa(ctReqDTO);
		        	 this.getManagedBean().getContractVer().getCtAmendment().setAmendmentLoa(resDTO1.getAmendmentScLoa());
		        	 if(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin() == null){
			        	//	this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractAdmin(new PmUser());
			        		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractAdmin(getFacesScopes().getUserSessionBean());
			        }
		        }
		    }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)){
		    	  this.getManagedBean().setMemberSignOff(false);
		    	  this.getManagedBean().setPbConfirmMeeting(true);
		    }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)){
		    	  this.getManagedBean().setAckFA("Ack");
		    }if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)){
		        this.getManagedBean().setAction(res.getAction());
		        this.getManagedBean().setSignOffDecision(true);
			 }
			 if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)){
				 this.getManagedBean().setSignUpdateDecision(true);
				 this.getManagedBean().setPbConfirmMeeting(true);	
			 }
			 if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)){
				 this.getManagedBean().setBpkSaSkApprover(true); 
			 } 
			
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT);
		} finally {
			contractSO = null;
		}
	}
	
	public void setAmendmentContractVer(ContractResDTO resDTO) {
		getManagedBean().setContractVer(resDTO.getCtContractVer());
		getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
		
		if(getManagedBean().getContractVer().getCtAmendment()==null) {
			getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
			getManagedBean().getContractVer().getCtAmendment().setFileRefNo(getManagedBean().getCtContract().getContractNo()+"-");
			getManagedBean().getContractVer().getCtAmendment().setTitle(getManagedBean().getCtContract().getContractName());
			getManagedBean().getContractVer().getCtAmendment().setDescription(getManagedBean().getCtContract().getContractName());
			getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(new Date());
			getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(new BigDecimal(0));
		}
		
		if(resDTO.getCtContractVer().getCtContractItems() != null && resDTO.getCtContractVer().getCtContractItems().size() > 0){
		   if(this.getManagedBean().getCtItemList() == null) {
		      this.getManagedBean().setCtItemList(new ArrayList<CtContractItem>());
		   } else {
		      this.getManagedBean().getCtItemList().clear();
		   }
		   for(CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
			   getManagedBean().getCtItemList().add(item);
			   if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
				   getManagedBean().setItemProductExist(true);
			   } else if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
				   getManagedBean().setItemServiceExist(true);
			   }
		   }
		}
		CombineSupAddress(this.getManagedBean().getContractVer());
		retriveZonesByContractVer();
		
		ContractBackingBean ctBB = new ContractBackingBean();
		ctBB.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		getManagedBean().setRemarkList(resDTO.getCtRemarks());
		getManagedBean().setAttachmentList(resDTO.getCtAttachments());
		initializeRemarks();		
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
	
	public String saveContractAmendment(String action) throws Exception {
		IContractSO contractSO = null;
		try {
			validateContractAmendment();
			
			ContractReqDTO reqDTO = new ContractReqDTO();
			String token = getFacesScopes().getBPMAuthenticationCtxToken();	
			reqDTO.setToken(token);
	        reqDTO.setOutcome(action);
	        reqDTO.setTaskId(this.getManagedBean().getTaskId());
	        
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
	      
			if(this.getManagedBean().getCtItemList().size() > 0 && this.getManagedBean().getCtItemList() != null){
	           if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
			  	 	this.getManagedBean().getContractVer().getCtContractItems().clear();
	           }
               this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtItemList());
	        }
	        
	        this.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	        if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() || this.getManagedBean().getContractVer().getCtAmendment().isValueChange() ){
	            ChkComplyAP201Rule();	
	        }   
	        
	        if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() && this.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() == null){
	        	this.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(this.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
	        }
	        
	        if(this.getManagedBean().isPrepareLOA()){
	        	if(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses() == null) {
	        		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaWitnesses(new ArrayList<ScLoaWitness>());
	        	}
	        	this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses().add(this.getManagedBean().getScLoaWitness());
	        }
	        
	        if(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME.equalsIgnoreCase(this.getManagedBean().getTaskName()) ||
	        		CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME.equalsIgnoreCase(this.getManagedBean().getTaskName())){
	        	reqDTO.setTaskName(this.getManagedBean().getTaskName());
	        } else {
	        	reqDTO.setTaskName(this.getManagedBean().getTaskName());
	        }
	    
	        reqDTO.setCtContract(this.getManagedBean().getContractVer().getCtContract());
	        reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
	        
	        reqDTO.setUserRole(getUserRole());
            reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		    reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			
			ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700007;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)) {
					messageId = MessageConst.INFO_700013;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getTaskName() != null) {
					messageId = MessageConst.INFO_700026;
				}
				log.info("Record saved successfully");
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			    if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) { 				  
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			    initializeRemarks();
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			contractSO = null;
		}
		return null;		
	}
	 
	 private void validateContractAmendment() {
		 if(!getManagedBean().getContractVer().getCtAmendment().isContractExtend() && !getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				 && !getManagedBean().getContractVer().getCtAmendment().isQuantityChange() && !getManagedBean().getContractVer().getCtAmendment().isValueChange()
				 && !getManagedBean().getContractVer().getCtAmendment().isClauseChange()) {
			 throw new EpException(MessageConst.INFO_100022, new String[]{"Amendment Type"});
		 }
	 }
	 
	 private String getUserRole() {		
			if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
				return RoleCode.MIN_CONTRACT_ADMIN.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value()))
				return RoleCode.CT_APPROVER.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value()))
				return RoleCode.BPK_APPROVER.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBA_CHAIRPERSON.value()))
				return RoleCode.PBA_CHAIRPERSON.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBA_MEMBER.value()))
				return RoleCode.PBA_MEMBER.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBA_SECRETARY.value()))
				return RoleCode.PBA_SECRETARY.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBB_CHAIRPERSON.value()))
				return RoleCode.PBB_CHAIRPERSON.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBB_MEMBER.value()))
				return RoleCode.PBB_MEMBER.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.PBB_SECRETARY.value()))
				return RoleCode.PBB_SECRETARY.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QCA_CHAIRPERSON.value()))
				return RoleCode.QCA_CHAIRPERSON.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QCA_MEMBER.value()))
				return RoleCode.QCA_MEMBER.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QCA_SECRETARY.value()))
				return RoleCode.QCA_SECRETARY.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QCB_CHAIRPERSON.value()))
				return RoleCode.QCB_CHAIRPERSON.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QCB_SECRETARY.value()))
				return RoleCode.QCB_SECRETARY.value();
			else if(getFacesScopes().hasRoleUser(RoleCode.QC_MEMBER.value()))
				return RoleCode.QC_MEMBER.value();
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
		flushItem();
		if(this.getManagedBean().getShowItem()!=null) {
			if (this.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				this.getManagedBean().setItemPricing(true);
			} else if (this.getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				this.getManagedBean().setItemCeilingQuantity(true);
			}
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
		initiateRemark(getManagedBean().getContractVer().getAp201Remark());
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
				   zoneNode = new DefaultTreeNode("zone", new LocationDTO(zone.toString(), zone.getZoneName(), zone.getDeliveryTerm(), zone.getDeliveryTermUom().getParameterId()), rootLocationTree);
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
	    				if(ctContractZone.toString().equalsIgnoreCase(((LocationDTO)zonename.getData()).getZoneId())) {
	    					if(ctContractZone.getDeliveryTermUom() == null)
	     					   ctContractZone.setDeliveryTermUom(new PmParameter());
	     					ctContractZone.getDeliveryTermUom().setParameterId(((LocationDTO)zonename.getData()).getUomId());
	     					ctContractZone.setDeliveryTerm(((LocationDTO)zonename.getData()).getDeliveryTerm());
	     				}
	    			}
	    		}
	    	}
		}
	    
	public void editItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();
		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					addDupObj.setExpDate(ctItemPrice.getExpDate());
					if (ctItemPrice.getCtContractZone() != null) {
						addDupObj.setCtContractZone(ctItemPrice.getCtContractZone());
					}
					if (ctItemPrice.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemPrice.getCtContractItem());
					}
					addDupObj.setChangePrice(true);
					addDupObj.setAssociateCtItemPrice(ctItemPrice);
					ctItemPrice.setChkIndexOfRow(true);
					ctItemPrice.setExpDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					ctItemPriceList.add(addDupObj);
				}
			}
		}
		this.getManagedBean().getSelectedItem().getCtItemPrices().clear();
		this.getManagedBean().getSelectedItem().getCtItemPrices().addAll(ctItemPriceList);
		ctItemPriceList.clear();
	}
	    
	public void deleteItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedItem().getCtItemPrices().remove(getManagedBean().getCtItemPrices());
	}
	    
	public void editCeilingQuantityOnIndexs() {
		List<CtItemCeilingQty> ctItemCeilingQtys = new ArrayList<CtItemCeilingQty>();
		CtItemCeilingQty addDupObj = new CtItemCeilingQty();
		for (CtItemCeilingQty ceilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			ctItemCeilingQtys.add(ceilingQty);
			if (!ceilingQty.isChangeCeilQuantity() && !ceilingQty.isChkIndexOfRow()) {
				if (ceilingQty.equals(this.getManagedBean().getCtItemCeilingQtys())) {
					addDupObj.setCeilingQuantity(ceilingQty.getCeilingQuantity());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					addDupObj.setExpDate(ceilingQty.getExpDate());
					if (ceilingQty.getCtAgency() != null) {
						addDupObj.setCtAgency(ceilingQty.getCtAgency());
					}
					if (ceilingQty.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ceilingQty.getCtContractItem());
					}
					addDupObj.setChangeCeilQuantity(true);
					addDupObj.setAssociateCtItemCeilingQty(ceilingQty);
					ceilingQty.setChkIndexOfRow(true);
					ceilingQty.setExpDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					ctItemCeilingQtys.add(addDupObj);
				}
			}
		}
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().clear();
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().addAll(ctItemCeilingQtys);
		ctItemCeilingQtys.clear();
	}
	
	public void deleteCeilingQuantityOnIndexs() {
		CtItemCeilingQty ctItemCeilingQty = getManagedBean().getCtItemCeilingQtys().getAssociateCtItemCeilingQty();
		ctItemCeilingQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemCeilingQty.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().remove(getManagedBean().getCtItemCeilingQtys());		
	}
	    
	public void editItemMaxQtyOnIndexs() {
		List<CtItemMaxQty> ctItemMaxQtyList = new ArrayList<CtItemMaxQty>();
		CtItemMaxQty addDupObj = new CtItemMaxQty();
		for (CtItemMaxQty ctItemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			ctItemMaxQtyList.add(ctItemMaxQty);
			if (!ctItemMaxQty.isChangeQuantity() && !ctItemMaxQty.isChkIndexOfRow()) {
				if (ctItemMaxQty.equals(getManagedBean().getCtItemMaxQty())) {
					addDupObj.setMaxQuantity(ctItemMaxQty.getMaxQuantity());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					addDupObj.setExpDate(ctItemMaxQty.getExpDate());
					if (ctItemMaxQty.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemMaxQty.getCtContractItem());
					}
					addDupObj.setChangeQuantity(true);
					addDupObj.setAssociateCtItemMaxQty(ctItemMaxQty);
					ctItemMaxQty.setExpDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					ctItemMaxQty.setChkIndexOfRow(true);
					ctItemMaxQtyList.add(addDupObj);
				}
			}
		}
		getManagedBean().getSelectedItem().getCtItemMaxQties().clear();
		getManagedBean().getSelectedItem().getCtItemMaxQties().addAll(ctItemMaxQtyList);
		ctItemMaxQtyList.clear();
	}
	    
	public void deleteItemMaxQtyOnIndexs() {
		CtItemMaxQty ctItemMaxQty = getManagedBean().getCtItemMaxQty().getAssociateCtItemMaxQty();
		ctItemMaxQty.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemMaxQty.setChkIndexOfRow(false);
		getManagedBean().getSelectedItem().getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty());
	}	
	
	public void setNewMaxQty() {
		BigDecimal nexMaxQty = getManagedBean().getSelectedItem().getCtItemMaxQties().get(getManagedBean().getSelectedItem().getCtItemMaxQties().size()-1).getMaxQuantity();
		getManagedBean().getSelectedItem().setNewMaxQuantity(nexMaxQty);
	}
	
	    public void addPbQccMeetingDetails(){
	    	CtMeeting ctMeeting = new CtMeeting();
	    	ctMeeting.setCtAmendment(this.getManagedBean().getContractVer().getCtAmendment());
	    	this.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().add(ctMeeting);
	    }
	    
	    public void delPbQccMeetingDetails(){
	    	for(int i=0; i <selectedMeetingUser.length; i++){
	    		CtMeeting meeting = selectedMeetingUser[i];
	    		this.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().remove(meeting);
	    	}selectedMeetingUser = null;
	    }
	    
        public void delCommitteMembers(){
	    	for(int i=0; i<selectedCommitteMembers.length; i++){
	    		 CtCommitteeMember committeMember = selectedCommitteMembers[i];
	    		 this.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().remove(committeMember);
	    	} selectedCommitteMembers = null;
	    }
	    /*Schedule Start*/
	    public void radioButtonForMeeting() {
			
	       this.getManagedBean().setExistingUsers(false);
	  	   this.getManagedBean().setNewUsers(false);
	  	   
		   if(this.getManagedBean().getSelectedRadioButton().equalsIgnoreCase("1"))
			   this.getManagedBean().setExistingUsers(true);
		   else if(this.getManagedBean().getSelectedRadioButton().equalsIgnoreCase("2"))
			  this.getManagedBean().setNewUsers(true);
		   
		}
	    
	    public void addNewUserMemberCommitte(){
	    	this.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().add(
	    			this.getManagedBean().getCtCommitteeMember());
	    }
	    
	    /**
		 * @return
		 */
		public void existingCommitteMemberList() {
			UserReqDTO userReqDTO = new UserReqDTO();
			SearchBean<PmUser> searchBean = new SearchBean<PmUser>();
			UserResDTO resDto = null;

			if (getManagedBean().getName() != null
					&& !"".equalsIgnoreCase(getManagedBean().getName())) {
				SearchFilterBean usernameSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				usernameSearchBean.setSearchField("userName");
				usernameSearchBean.setSearchValue(getManagedBean().getName());
				searchBean.getDataFilterBeans().add(usernameSearchBean);

			}

			if (getManagedBean().getIdentificationNo() != null
					&& !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())) {
				SearchFilterBean idNoSearchBean = new SearchFilterBean(
						SearchOperator.OPERATOR_EQUALS.value());
				idNoSearchBean.setSearchField("identificationNo");
				idNoSearchBean.setSearchValue(getManagedBean()
						.getIdentificationNo());
				searchBean.getDataFilterBeans().add(idNoSearchBean);
			}
			userReqDTO.setUserSearchBean(searchBean);

			try {
				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				resDto = userSO.searchUserProfile(userReqDTO);
				if (resDto != null) {
					this.getManagedBean().setExternalUsers(
							resDto.getUserResultBean().getResultList());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		public void okCommitteMembers(){
		
			List<CtCommitteeMember> memberList = getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers();
			if (null == memberList) {
				memberList = new ArrayList<CtCommitteeMember>();
			}
			
			for (int i=0; i<selectedPmUser.length; i++) {
				    PmUser user = selectedPmUser[i];
				         CtCommitteeMember committeeMember = new CtCommitteeMember();
				        committeeMember.setMemberName(user.getUserName());
				        committeeMember.setIcPassport(user.getIdentificationNo());
				        committeeMember.setDesignation(user.getDesignation());
				        if(null!=user.getPmAddressTypes()){
					      for(PmAddressType address:user.getPmAddressTypes()){
						    if(null!=address.getPmAddress()){
						    	committeeMember.setEmail(address.getPmAddress().getEmail());
							break;
						  }
					    }
			  	      }
				     committeeMember.setGrade(user.getGrade());
				     committeeMember.setCtAmendment(this.getManagedBean().getContractVer().getCtAmendment());
				     committeeMember.setMemberRole(new PmParameter());
				     committeeMember.setPnp(user.isPMP());
				     committeeMember.setUserId(user);
				     committeeMember.setExternalUser(true);
			         memberList.add(committeeMember);					     
			    }
			getManagedBean().getContractVer().getCtAmendment().setCtCommitteeMembers(memberList);
		}
		
		public SelectItem[] getcontractSignerDropdown(){
			SelectItem[] items = null;     
			try{
			   int i = 1;
			   IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			   UserReqDTO reqDTO = new UserReqDTO();
			   reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());	
			 //  reqDTO.setOrgProfileId(this.getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			   UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				List<PmUser> list = resDTO.getUserList();
			   items = new SelectItem[list.size()+1];
		       items[0] = new SelectItem(null, "- Select One -");
		       for(PmUser pmUser : list) {
			       items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
		       }
			 }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     }
		   return items;
		}
		
		public SelectItem[] getcontractAdminDropdown(){
			SelectItem[] items = null;     
			try{
			   int i = 1;
			   IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			   UserReqDTO reqDTO = new UserReqDTO();
			   reqDTO.setRoleCode(RoleCode.MIN_CONTRACT_ADMIN.value());	
			   reqDTO.setOrgProfileId(this.getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			   UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
				List<PmUser> list = resDTO.getUserList();
			   items = new SelectItem[list.size()+1];
		       items[0] = new SelectItem(null, "- Select One -");
		       for(PmUser pmUser : list) {
			       items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
		       }
			 }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     }
		   return items;
		}
		
		public List<PmDocument> getLoaDocumentList(){
			
			 IDocumentSO documentSO = null;
			try{
			     documentSO = (IDocumentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			     DocumentReqDTO reqDTO = new DocumentReqDTO();
			     reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
			     DocumentResDTO resDTO = documentSO.retrieveDocumentCheckList(reqDTO);
			     return resDTO.getDocumentList();
			}catch(Exception e){
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		
			}
			return null;
		}
		
		 public List<SelectItem> getPopulateBankAccountList() {
	         populateBankAccountList = null;
	    	 if(this.getManagedBean().getCtContract() != null){
	    	   if(this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
	    	     && this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
	    		 populateBankAccountList = new ArrayList<SelectItem>();
			     List<SmSupplierBank> supplierBank = this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks();
				 for(SmSupplierBank obj : supplierBank) {
					populateBankAccountList.add(new SelectItem(obj.getAccountNo(),obj.getAccountNo()));
				 }      
	    	   }
	     }return populateBankAccountList;
	 }
		 
		 public void changeAccountActionListener(){
			 if(this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
			    	    && this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				 for(SmSupplierBank supplierBank : this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()){
					 if(supplierBank.getAccountNo().equalsIgnoreCase(this.getManagedBean().getContractVer().getCtContractValue().getSupplierBankAccountNo())){
						 this.getManagedBean().setSupplierBank(supplierBank);
						 if(supplierBank.getSmAddress() != null)
						    CombineBankAddress(supplierBank.getSmAddress());
					 }
				 }
			 }
		  }
		 
		 public void CombineBankAddress(SmAddress smAddress){
		    	
		    	StringBuilder address = new StringBuilder();
		    	String strSpace =", ";
		    	if(smAddress.getAddress1() != null){
		    		address.append(smAddress.getAddress1());
		    		address.append(strSpace);
		    	
		    	}if(smAddress.getAddress2() != null){
		    		address.append(smAddress.getAddress2());
		    		address.append(strSpace);
		    	
		    	}if(smAddress.getAddress3() != null){
		    		address.append(smAddress.getAddress3());
		    		address.append(strSpace);
		    	
		    	}if(smAddress.getAddress4() != null){
		    		address.append(smAddress.getAddress4());
		    		address.append(strSpace);
		    		
		    	}if(smAddress.getPmDistrict().getDistrictName() != null){
		    		address.append(smAddress.getPmDistrict().getDistrictName());
		    		address.append(strSpace);
		    	
		    	}if(smAddress.getPmState().getStateName() != null){
		    		address.append(smAddress.getPmState().getStateName());
		    		address.append(strSpace);
		    	
		    	}if(smAddress.getPmCountry().getCountryName() != null){
		    		address.append(smAddress.getPmCountry().getCountryName());
		    		address.append("\n");
		    	}if(smAddress.getPostcode() != null){
		    		address.append(smAddress.getPostcode());
		    	}
		    	
		    	this.getManagedBean().setSupplierAddress(address.toString());
		    }
		
		public SelectItem[] getMeetingStatusDropdown(){
			SelectItem[] items = null;     
			try{
			   int i = 1;
			   IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			   StatusReqDTO reqDTO = new StatusReqDTO();
				reqDTO.setModuleCode("SC");
				reqDTO.setDocType("MS");
				reqDTO.setLanguageCode("en");
				
				StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
				
				List<PmStatusDesc> list = resDTO.getStatusDescList();
			   items = new SelectItem[list.size()+1];
		       items[0] = new SelectItem(null, "- Select One -");
		       for(PmStatusDesc statusDesc : list) {
			       items[i++] = new SelectItem(statusDesc.getStatusDescId(), statusDesc.getStatusName());
		       }
			 }catch (Exception e) {
		    	   e.printStackTrace();
		    	   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		     }
		   return items;
		}
		
	public Date meetingDate(List<CtMeeting> ctmeeting) {
		CtMeeting newObject = null;
		for (CtMeeting o : ctmeeting) {
			if (newObject == null || o.getMeetingDate().compareTo(newObject.getMeetingDate()) > 0) {
				newObject = o;
			}
		}
		return newObject.getMeetingDate();
	}

	public String meetingLocation(List<CtMeeting> ctmeeting) {
		CtMeeting newObject = null;
		for (CtMeeting o : ctmeeting) {
			if (newObject == null || o.getMeetingDate().compareTo(newObject.getMeetingDate()) > 0) {
				newObject = o;
			}
		}
		this.getManagedBean().setMeetingStatusId(newObject.getMeetingStatusId());
		return newObject.getMeetingLocation();
	}

	public boolean getCompareUser() {
		if (getUserRole().equalsIgnoreCase(RoleCode.MIN_CONTRACT_ADMIN.value()))
			return true;
		else
			return false;
	}
	
	public void onContractExtensionChecked() {
		if(getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
			getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(3); // default to 3 month
			Calendar c = Calendar.getInstance();
			c.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
			c.add(Calendar.DATE, 1);
			getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());
			c.add(Calendar.MONTH, 3);
			getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
			getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(c.getTime());
		}
	}
	
	public void onExtensionPeriodChanged() {
		Calendar c = Calendar.getInstance();
		c.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.DATE, 1);
		getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());
		c.add(Calendar.MONTH, getManagedBean().getContractVer().getCtAmendment().getExtendPeriod());
		getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
	}
		
	public void onItemPriceChecked() {
		if(getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
		}
	}

	public void onQuantityChecked() {
		if(getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
		}
	}
	
	public SelectItem[] getItemDisplayTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		if(getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
			&& !getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(), 
							ParameterConst.ITEM_DISPLAY_TYPE_PRICE));
		} else if(!getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				&& getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(),
							ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY));
		}
		
		return super.getDropdownCodeValuesBySearchBean(ParameterTypeConst.ITEM_DISPLAY_TYPE, searchBean);
	}
	
	public List<CtContractItem> getContractItemByType(String itemTypeCode) {
		List<CtContractItem> ctItemList = new ArrayList<CtContractItem>();
		for (CtContractItem item : getManagedBean().getCtItemList()) {
			if (item.getItemType().getParameterCode().equalsIgnoreCase(itemTypeCode)) {
				ctItemList.add(item);
			}
		}
		return ctItemList;
	}
	
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

	public CtCommitteeMember[] getSelectedCommitteMembers() {
		return selectedCommitteMembers;
	}

	public void setSelectedCommitteMembers(
			CtCommitteeMember[] selectedCommitteMembers) {
		this.selectedCommitteMembers = selectedCommitteMembers;
	}

	public PmUser[] getSelectedPmUser() {
		return selectedPmUser;
	}

	public void setSelectedPmUser(PmUser[] selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	public CtMeeting[] getSelectedMeetingUser() {
		return selectedMeetingUser;
	}

	public void setSelectedMeetingUser(CtMeeting[] selectedMeetingUser) {
		this.selectedMeetingUser = selectedMeetingUser;
	}

	public int calculateInMonths(int years) {
		return years * 12;
	}
}
