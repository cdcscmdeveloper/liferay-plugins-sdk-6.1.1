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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.tpl.IContractTemplateSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
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
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.dto.ct.tpl.ContractTemplateReqDTO;
import my.ep.dto.ct.tpl.ContractTemplateResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.address.AddressReqDTO;
import my.ep.dto.pm.common.address.AddressResDTO;
import my.ep.dto.pm.common.document.DocumentReqDTO;
import my.ep.dto.pm.common.document.DocumentResDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgReqDTO;
import my.ep.dto.pm.common.financialorg.FinancialOrgResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.CtMeeting;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
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
import my.ep.persistence.entity.sc.loaloi.ScLoaAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.LocationDTO;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTAltChunk;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.MenuModel;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

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
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	
	private String editorValue;
	
	private ContractBackingBean ctBackingBean;
	private List<SelectItem> populateBankAccountList;
	private PmUser[] selectedPmUser;
	private CtMeeting[] selectedMeetingUser;
    private CtPerformanceBond[] selectedBondList;
	private CtCommitteeMember[] selectedCommitteMembers;
	
	private IPagingDataModel<PmUser> committeDataModel = new CommittePagingDataModel();	
	
	public AmendmentBackingBean(){
		if(this.getManagedBean().getAction() == null) {
			 this.getManagedBean().setAction(new String[] { BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT });
		}
		
		if(ctBackingBean == null){
		     ctBackingBean = new ContractBackingBean(this.getManagedBean().getFileDirectory());
		     ctBackingBean.initializeRemarkAndAttachement(this.getManagedBean().getRemarkList(), this.getManagedBean().getAttachmentList());
		}
	}
	
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void preRenderView() throws UnsupportedEncodingException {
		IContractSO contractSO = null;
		IContractTemplateSO contractTemplateSO = null;
		try {
			System.out.println("**********preRenderView**************");
			log.debug("Inside preRenderView()");
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
			this.getManagedBean().setSelectedTabIndex(0);
			
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
			this.getManagedBean().setTaskName(res.getTaskName());
			String[] outcome = new String[res.getAction().length];
			int i = 0;
			if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME) || res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME) ||
				res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME) || res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) || 
				res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)) {
				outcome = new String[res.getAction().length+1];
				outcome[i++] = BpmOutcomeConst.BPM_SAVE;
			}
		
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			
			this.getManagedBean().setAction(outcome);
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
			ctReqDTO.setTaskName(res.getTaskName());
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			this.getManagedBean().setSelectedTabIndex(0);
			
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);
			ContractResDTO resDTO = contractSO.retrieveContractVer(ctReqDTO);
			setAmendmentContractVer(resDTO);
			resetbooleanValue();
			 if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME) && 
					 this.getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)){
				     this.getManagedBean().setBpkSaSkApprover(true);
			 }
			
	        if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) || 
	                res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
			        //this.getManagedBean().setMemberSignOff(true);  
			        this.getManagedBean().setPbConfirmMeeting(true);
			        if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)){
					    this.getManagedBean().setSignUpdateDecision(true);
			       }
			
	        } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) || 
				    res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
				    this.getManagedBean().setLetterOfAcceptance(true);
				    this.getManagedBean().setPrepareLOA(true);
				    this.getManagedBean().setWitnessHeader(ContractConst.AGENCY_WITNES_HEADER);
				    if(this.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() > 0 && 
						  this.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null){
					      this.getManagedBean().setDecisionPbQcLOA(true);
			     	}
			  
				    if (this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() == null) {
					     ctReqDTO.setCtContractVer(this.getManagedBean().getContractVer());
					     ContractResDTO resDTO1 = contractSO.setScLoa(ctReqDTO);
					     getManagedBean().getContractVer().getCtAmendment().setAmendmentLoa(resDTO1.getAmendmentScLoa());
					    if (getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin() == null) {
						      getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractAdmin(getFacesScopes().getUserSessionBean());
					    }
				   }
			
	        } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
				    this.getManagedBean().setMemberSignOff(true);
				    this.getManagedBean().setPbConfirmMeeting(true);
				    this.getManagedBean().setSignUpdateDecision(false);
		
	        } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				     this.getManagedBean().setAckFA(ContractConst.SUPPLIER_ACK_LOA);
				     this.getManagedBean().setWitnessHeader(ContractConst.SUPPLIER_WITNES_HEADER);
				
				     contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
				     ContractTemplateReqDTO templateReqDTO = new ContractTemplateReqDTO();
				     templateReqDTO.setTemplateName(ContractConst.TEMPLATE_INTEGRITY_PACT);
				     ContractTemplateResDTO templateResDTO = contractTemplateSO.retrieveTemplateByName(templateReqDTO);
				     getManagedBean().setEditorValue("");
				    if(templateResDTO.getCtTemplate()!=null) {
				         	String content = new String(templateResDTO.getCtTemplate().getContent(), "UTF-8");
					        Map<String, String> valuesMap = new HashMap<String, String>();
					        valuesMap.put("PTJ_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
					        valuesMap.put("LOA_TITLE", getManagedBean().getContractVer().getCtAmendment().getTitle());
					        valuesMap.put("SUPPLIER_USER_NAME", getFacesScopes().getUserScreenName());
					        valuesMap.put("SUPPER_USER_IC_NO", getFacesScopes().getUserSessionBean().getIdentificationNo());
					        valuesMap.put("SUPPLIER_NAME", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
					        valuesMap.put("SUPPLIER_MOF_REG_NO", getManagedBean().getCtContract().getSmSupplier().getRegNo());
					
					        StrSubstitutor sub = new StrSubstitutor(valuesMap);
					        getManagedBean().setEditorValue(sub.replace(content));
				     }
		
	        } else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
				    this.getManagedBean().setAction(res.getAction());
				    this.getManagedBean().setSignOffDecision(true);
			
	        } else if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)){
				     this.getManagedBean().setBpkSaSkApprover(true);
			
	        } 
	        
		    if(getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa()!=null) {
		    	for(ScLoaWitness scLoaWitness : getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses()) {
		    		if((res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) || 
		    				res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) && scLoaWitness.getPartyType().equalsIgnoreCase(ContractConst.WITNES_TYPE_AGENCY)) {
		    			getManagedBean().setScLoaWitness(scLoaWitness);
		    			break;
		    		} 
		    		if(res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME) && scLoaWitness.getPartyType().equalsIgnoreCase(ContractConst.WITNES_TYPE_SUPPLIER)) {
		    			getManagedBean().setScLoaWitness(scLoaWitness);
		    			break;
		    		}		    		
		    	}
		    }
		    
		    if(this.getManagedBean().isLetterOfAcceptance()){
		    	   getLoaDocumentList();
		     	  if(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment() == null){
		    		     this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaAttachment(new ScLoaAttachment());
		    		     this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setScLoa(
		    			 this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
		    	   }
		     }
		    ConstructTabSideMenu();
		    FacesContext fc = FacesContext.getCurrentInstance();
			fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractConst.NAV_TO_CONTRACT_AMENDMENT);
		} finally {
			contractSO = null;
			contractTemplateSO = null;
		}
	}
	
	public String backAction() {
		return CONTRACT_MAINT_PAGE;
	}
	
	public void setAmendmentContractVer(ContractResDTO resDTO) {
		
		IContractSO contractSO = null;
		try {
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
			        this.getManagedBean().getCtItemList().clear();
			        this.getManagedBean().getCtServiceItemList().clear();
			        for(CtContractItem item : getManagedBean().getContractVer().getCtContractItems()) {
				           if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
					               getManagedBean().setItemProductExist(true);
					               getManagedBean().getCtItemList().add(item);
				          } else if(item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
					               getManagedBean().setItemServiceExist(true);
					               this.getManagedBean().getCtServiceItemList().add(item);
				          }
			         }
			   }
		
			    retriveZonesByContractVer();
			    initializeRemarks();
			    getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
						getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));
			    ctBackingBean = new ContractBackingBean(getManagedBean().getFileDirectory());			     
			    ctBackingBean.initializeRemarkAndAttachement(getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
			    getManagedBean().setRemarkList(resDTO.getCtRemarks());
			    getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			
		        if(getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())){
		    	        contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
		    	        ContractReqDTO reqDTO = new ContractReqDTO();
		    	        reqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
		    	        reqDTO.setContractId(resDTO.getCtContractVer().getCtContract().getContractId());
				        reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
				        ContractResDTO amendResDTO = contractSO.retrieveExtensionHistory(reqDTO);
				        getManagedBean().setAmendHistoryList(amendResDTO.getExtensionHistoryList());
		         }
	   	    } finally {
		     	contractSO = null;
		    }
	   }
	
	public void ConstructTabSideMenu(){
		
		int zoneTabIndex = 0;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		MenuModel amendMenuModel = new DefaultMenuModel();
		AmendTabSideMenus sideMenu = new AmendTabSideMenus();
		
		if(getManagedBean().getFinalListOfSideMenu() == null)
			getManagedBean().setFinalListOfSideMenu(new ArrayList<String>());
		else
			getManagedBean().getFinalListOfSideMenu().clear();
		
		if(this.getManagedBean().getAckFA() == null){
			    getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendFulfilment);
			    if(getManagedBean().getContractVer().getCtAmendment().isQuantityChange()|| getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()){
			    	 if(getManagedBean().getCtContract().isZonalPricing()){
				    	  getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithZone);
				    } getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithItem);
			
			    }if(getManagedBean().isBpkSaSkApprover()) { 
			    	getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithSpAprlAP201);
			  
			    }if(getManagedBean().isPrepareLOA() && getManagedBean().isDecisionPbQcLOA()){
			    	getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			    
			    }if(getManagedBean().isPrepareLOA()){
			    	getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareLoa);
			   
			    }if(getManagedBean().isPbConfirmMeeting()){
			    	getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithMeetings);
			    
			    }if(getManagedBean().isSignUpdateDecision()){
			    	getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			    
			    }if(getManagedBean().isSignOffDecision()){
		    	     getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
		        }
		   }else if(this.getManagedBean().getAckFA().equalsIgnoreCase("Ack")){
			   getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendSupplier);
		   }
		
		 for( String str : this.getManagedBean().getFinalListOfSideMenu()){
	 	    	MenuItem menuItem = new MenuItem();
	 	    	menuItem.setValue(str);
	 	    	menuItem.setUpdate("@form");
	 	    	menuItem.setActionExpression(FacesScopes
						.createMethodExpression(facesContext,
								"#{amendmentBackingBean.amendTabChangeAction("
										+ zoneTabIndex + ")}", null,
								new Class[] { Integer.class }));
	 	    	amendMenuModel.addMenuItem(menuItem);

				zoneTabIndex++;
	 	    } getManagedBean().setAmendMenuModel(amendMenuModel);
 }
	
	public void amendTabChangeAction(Integer selectedTabIndex) {
		if (log.isDebugEnabled()) {
			log.debug("fulfilmentTabChangeAction");
		}
		getManagedBean().setSelectedTabIndex(selectedTabIndex);
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
	
	    public void resetbooleanValue(){
	            	this.getManagedBean().setBpkSaSkApprover(false);
		            this.getManagedBean().setMemberSignOff(false);  
		            this.getManagedBean().setPbConfirmMeeting(false);
		            this.getManagedBean().setSignUpdateDecision(false);
		            this.getManagedBean().setLetterOfAcceptance(false);
		            this.getManagedBean().setPrepareLOA(false);
		            this.getManagedBean().setDecisionPbQcLOA(false);
		            this.getManagedBean().setSignOffDecision(false);
	     }
	
	   public String saveContractAmendment(String action) throws Exception {
		      log.debug("Inside saveContractAmendment(.)");
		      IContractSO contractSO = null;
		      ContractReqDTO reqDTO = null;
	
		      try{
			   validateContractAmendment(action);
			   reqDTO = new ContractReqDTO();
			   String token = getFacesScopes().getBPMAuthenticationCtxToken();	
			   reqDTO.setToken(token);
	           reqDTO.setOutcome(action);
	           reqDTO.setTaskId(this.getManagedBean().getTaskId());
	        
			   contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			   reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			   
			   if(this.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
					this.getManagedBean().getContractVer().getCtContractItems().clear();
	            }
	            this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtItemList());
	            this.getManagedBean().getContractVer().getCtContractItems().addAll(this.getManagedBean().getCtServiceItemList());
	      
	           getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	           chkComplyAP201Rule();	
	        
	           if(this.getManagedBean().getContractVer().getCtAmendment().isContractExtend() && this.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() == null)
	        	   this.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(this.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
	        
	           if(this.getManagedBean().isPrepareLOA()) {
	        
	        	   if(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses() == null) 
	                   this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaWitnesses(new ArrayList<ScLoaWitness>());
	            
	          	   this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses().add(this.getManagedBean().getScLoaWitness());
	        	   int contractDuration = getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() + 	getManagedBean().getContractVer().getCtContractValue().getContractPeriod();
	        	  this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractDuration(new BigDecimal(contractDuration));
	           }
	    
	           reqDTO.setTaskName(this.getManagedBean().getTaskName());
	           reqDTO.setCtContract(this.getManagedBean().getCtContract());
	           reqDTO.setCtContractVer(this.getManagedBean().getContractVer());
	        
	           reqDTO.setUserRole(getUserRole());
               reqDTO.setCtRemark(this.getManagedBean().getRemarkList().get(0));
		       reqDTO.setAttachments(this.getManagedBean().getAttachmentList());
			
			   ContractResDTO resDTO = contractSO.saveContractVer(reqDTO);
			   if (resDTO.isSuccessful()) {
				      log.info("Record saved successfully");
				      String messageId = MessageConst.INFO_700005;
			      	  if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					          messageId = MessageConst.INFO_700025;
				      }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					         messageId = MessageConst.INFO_700009;
				      }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					         messageId = MessageConst.INFO_700007;
				     }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					        messageId = MessageConst.INFO_700008;
				     }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					       messageId = MessageConst.INFO_700011;
				     }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)) {
					       messageId = MessageConst.INFO_700013;
				     }else if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getTaskName() != null) {
					      messageId = MessageConst.INFO_700026;
				     }
				    
			      	  log.info("Record saved successfully");
				      FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						    MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.getManagedBean().getContractVer().getDocType()), 
								this.getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			             if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) { 				  
					          renewSesionObject();
					          return CommonUIConst.ACKNOWLEDGE_PAGE;
				        } else {
					       setAmendmentContractVer(resDTO);
					       ConstructTabSideMenu();
				       }
		       	 } else {
				      throw new EpException("Failed.");
		      	}
	    	} finally {
			      contractSO = null;
		    }
		  return null;		
	  }
	 
	 private void validateContractAmendment(String action) {
		 if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
			if((getManagedBean().getRemarkList().get(0).getRemark() == null || getManagedBean().getRemarkList().get(0).getRemark().length()==0) &&
				(getManagedBean().getContractVer().getAp201Remark().get(0).getRemark() == null || getManagedBean().getContractVer().getAp201Remark().get(0).getRemark().length()==0) &&
				(getManagedBean().getContractVer().getItemRemarks().get(0).getRemark() == null || getManagedBean().getContractVer().getItemRemarks().get(0).getRemark().length()==0)) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{"Remark"});
			}
		}
		 
		 if(!getManagedBean().getContractVer().getCtAmendment().isContractExtend() && !getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				 && !getManagedBean().getContractVer().getCtAmendment().isQuantityChange() && !getManagedBean().getContractVer().getCtAmendment().isValueChange()
				 && !getManagedBean().getContractVer().getCtAmendment().isClauseChange()) {
			 throw new EpException(MessageConst.INFO_100022, new String[]{"Amendment Type"});
		 }
		 if (getManagedBean().getTaskName()!=null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
			 if(action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) && !getManagedBean().isTermAndConditionFlg()) {
				 throw new EpException(MessageConst.ERROR_750002);
			 }
		 }
	 }
	 
	 private String getUserRole() {		
		if(getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) 
			return RoleCode.MIN_CONTRACT_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()))
			return RoleCode.BPK_CCA.value();		
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
		else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value()))
			return RoleCode.MOF_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()))
			return RoleCode.MOF_SUPPLIER_USER.value();		
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()))
			return RoleCode.BASIC_SUPPLIER_ADMIN.value();
		else if(getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()))
			return RoleCode.BASIC_SUPPLIER_USER.value();				
		else 
			return null;
	 }
	 
	// Manually check AP201 rule. Should call BPM or rules engine 
	public void chkComplyAP201Rule() {
		
		getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		if(breachAP201Rules(1) || breachAP201Rules(2) || breachAP201Rules(3)) {
			     getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		
		 } else if(getManagedBean().getContractVer().getCtAmendment().isClauseChange() || getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			     getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		
		 } else if(getManagedBean().getCtContract().getRecordStatusFlg().intValue()!=DbValueConst.RECORD_STATUS_ACTIVE) {
			getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		}
	}
	 
	public void addContractValue() {
		double firstValue = this.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue();
		double contractValue = this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue();
		contractValue = contractValue + firstValue - this.getManagedBean().getContractVer().getCtAmendment().getSecondValue();

		this.getManagedBean().getContractVer().getCtAmendment().setSecondValue(firstValue);
		this.getManagedBean().getContractVer().getCtContractValue().setContractAmount(new BigDecimal(contractValue));
	}
	 
    public SelectItem[] getMinistryDropdown() {
			
			SelectItem[] items = null;     
			try {
			        int i = 1;
			        IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
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
	
	/*Implementation of Bond Details*/
    public void addBondDetails() {
		CtPerformanceBond ctPerformanceBond = new CtPerformanceBond();
	    ctPerformanceBond.setFinancialOrg(new PmFinancialOrg());
		this.getManagedBean().getContractVer().getCtPerformanceBonds().add(ctPerformanceBond);
	}
    
    public void delBondDetails() {
    	for(int i = 0; i < selectedBondList.length; i++) {
			CtPerformanceBond ctPerformanceBond = selectedBondList[i];
			this.getManagedBean().getContractVer().getCtPerformanceBonds().remove(ctPerformanceBond);
		}selectedBondList = null;	
	}
	
    public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
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
				out.write( buffer, 0, bytesRead );
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
    
    public void downloadDocFile(ScLoaReport loaReport) throws IOException {
		
    	IContractTemplateSO contractTemplateSO = null;
		AttachmentFormat attachmentFormat = null;
		ContractTemplateResDTO resDTO = null;
		 ContractTemplateReqDTO reqDTO  = null;
		
	    try{
	    	 String fileName = getTemplateFileName(loaReport.getPmDocument().getDocName());
		     if (fileName != null) {
		        
		    	   String content = null;	
		           attachmentFormat = new AttachmentDateFormat();
			       String filePath = getManagedBean().getFileDirectory() + "/temp";
			   
			        File file = new File(filePath);
		            file.mkdirs();
		            filePath = filePath + "/" + attachmentFormat.format(fileName, null);
			       
		            if(ContractConst.TEMPLATE_JADUAL_HARGA.equalsIgnoreCase(loaReport.getPmDocument().getDocName())) {
				         formJadualHargaExcel(filePath);
			       
		            } else {
				         contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
				         reqDTO = new ContractTemplateReqDTO();
				         reqDTO.setTemplateName(loaReport.getPmDocument().getDocName().toUpperCase());
				         resDTO = contractTemplateSO.retrieveTemplateByName(reqDTO);
				         content = new String(resDTO.getCtTemplate().getContent(), "UTF-8");
				         content = getContent(content, loaReport.getPmDocument().getDocName());
				         convertHtmlToDocFile(content, filePath);
			      }downloadFile(filePath, fileName);
		    
		    } else {
		    	  if(loaReport.getScLoaReportAttachment().getFilePath() == null){
		    		   throw new EpException(MessageConst.INFO_100052, new String[]{loaReport.getScLoaReportAttachment().getFileName()});
		    	  }downloadFile(loaReport.getScLoaReportAttachment().getFilePath(), loaReport.getScLoaReportAttachment().getFileName());
		    }
		 }catch(UnsupportedEncodingException e) {
			  e.printStackTrace();
		}finally{
			
			contractTemplateSO = null;
			attachmentFormat = null;
			resDTO = null;
			reqDTO  = null;
		}
	}
	
	public void downloadAll() {
		IContractTemplateSO contractTemplateSO = null;
		List<File> fileList = new ArrayList<File>();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String fileName = null;
		String filePath = null;
		String content = null;
		String tempDirectory = getManagedBean().getFileDirectory() + "/temp";
		File file = new File(tempDirectory);
		file.mkdirs();
		
		try{
			contractTemplateSO = (IContractTemplateSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractTemplateSO.class);
			ContractTemplateReqDTO templateReqDTO = new ContractTemplateReqDTO();
			
			for(ScLoaReport scLoaReport : getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports()) {
				// System Define Doc
				if (scLoaReport.getDefinationType().getParameterId().equals(
						(retrieveParameterByCodeType(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED).getParameterId()))) {
					fileName = getTemplateFileName(scLoaReport.getTitle());
					filePath = tempDirectory + "/" + attachmentFormat.format(fileName, null);
					if(scLoaReport.getTitle().equalsIgnoreCase(ContractConst.TEMPLATE_JADUAL_HARGA)) {
						formJadualHargaExcel(filePath);
						fileList.add(new File(filePath));
					}
					else if(scLoaReport.getTitle().equalsIgnoreCase(ContractConst.TEMPLATE_FORMAT_BON_PELAKSANAAN)) {
						// TODO: get from SC
					}
					else {
						templateReqDTO.setTemplateName(scLoaReport.getTitle().toUpperCase());
						ContractTemplateResDTO templateResDTO = contractTemplateSO.retrieveTemplateByName(templateReqDTO);					
						content = new String(templateResDTO.getCtTemplate().getContent(), "UTF-8");
						content = getContent(content, scLoaReport.getTitle());					
						convertHtmlToDocFile(content, filePath);
						fileList.add(new File(filePath));
					}
				}
				// User Upload Doc
				else if (scLoaReport.getDefinationType().getParameterId().equals(
						(retrieveParameterByCodeType(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD).getParameterId()))) {
					if (scLoaReport.getScLoaReportAttachment().getFilePath() != null)
						fileList.add(new File(scLoaReport.getScLoaReportAttachment().getFilePath()));
				}
			}
			
			String zipFileName = "amendment.zip";
			File zipFile = new File(tempDirectory + "/" + attachmentFormat.format(zipFileName, null));
			try {
				zipFile.createNewFile();
				byte[] buffer = new byte[1024];   
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));   
				out.setLevel(Deflater.DEFAULT_COMPRESSION);   
				for (File amendmentFile : fileList) {
					FileInputStream in = new FileInputStream(amendmentFile);   
					out.putNextEntry(new ZipEntry(amendmentFile.getName()));   
					int len;   
					while ((len = in.read(buffer)) > 0) {
						out.write(buffer, 0, len);   
					}   
					out.closeEntry();   
					in.close();   
				}  
				out.close();   
			} catch (IOException e) {
				e.printStackTrace();
			}
			downloadFile(zipFile.getPath(), zipFileName);
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
	
	private String getTemplateFileName(String templateType) {		
		if(templateType.equalsIgnoreCase(ContractConst.TEMPLATE_SURAT_SETUJU_TERIMA)) {
			return "AmendmentSuratSetujuPage.docx";
		} else if(templateType.equalsIgnoreCase(ContractConst.TEMPLATE_RINGKASAN_KONTRAK)) {
			return "AmendmentRingkasanKontrakPage.docx";
		} else if(templateType.equalsIgnoreCase(ContractConst.TEMPLATE_INTEGRITY_PACT)) {
			return "AmendmentIntegrityPactPage.docx";
		} else if(templateType.equalsIgnoreCase(ContractConst.TEMPLATE_JADUAL_HARGA)) {
			return "AmendmentJadualHargaPage.xlsx";
		} else if(templateType.equalsIgnoreCase(ContractConst.TEMPLATE_FORMAT_BON_PELAKSANAAN)) {
			
		}
		
		return null;
	}
	
	private void convertHtmlToDocFile(String content, String filePath){
		WordprocessingMLPackage wordMLPackage;
	 	try {
	 		wordMLPackage = WordprocessingMLPackage.createPackage();
	 		AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/hw.html"));
	 		afiPart.setBinaryData(content.getBytes());
	 		afiPart.setContentType(new ContentType("text/html"));
	 		Relationship altChunkRel = wordMLPackage.getMainDocumentPart().addTargetPart(afiPart);
	 		 
	 		CTAltChunk ac = Context.getWmlObjectFactory().createCTAltChunk();
	 		ac.setId(altChunkRel.getId() );
	 		wordMLPackage.getMainDocumentPart().addObject(ac);
	 		wordMLPackage.getContentTypeManager().addDefaultContentType("html", "text/html");
	 		wordMLPackage.save(new java.io.File(filePath));
	 	} catch (InvalidFormatException e) {
	 		e.printStackTrace();
	 	}catch (Docx4JException e) {
 			e.printStackTrace();
 		}
	}
	
	/**
	 * This is to generate excel sheet for Item products and services
	 */
	private void formJadualHargaExcel(String filePath){
		log.debug("Enter formJadualHargaExcel()");
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		File file = new File(filePath);
		
		ContractAmendmentReqDTO reqDto = new ContractAmendmentReqDTO();
		reqDto.setContractVerId(this.getManagedBean().getContractVer().getContractVerId());
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
		IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
		ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
		parameterReqDTO.setParameterCode(ParameterConst.ITEM_TYPE_PRODUCT);
		parameterReqDTO.setParameterType(ParameterTypeConst.SC_ITEM_TYPE);
		ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
		ContractAmendmentResDTO resDto = amendSO.getProductList(reqDto, resDTO.getPmParameter().getParameterId());
		List<CtItemPrice> productList = resDto.getItemPrices();
		
		parameterReqDTO.setParameterCode(ParameterConst.ITEM_TYPE_SERVICE);
		resDTO = parameterSO.retrieveParameterByTypeAndCode(parameterReqDTO);
		resDto = amendSO.getServiceList(reqDto, resDTO.getPmParameter().getParameterId());
		List<CtItemPrice> serviceList = resDto.getItemPrices();
		
		try {
			WritableWorkbook itemExcel = Workbook.createWorkbook(file);
			WritableSheet productSheet = null;
			WritableSheet ServiceSheet = null;
			WritableFont timesfontbold = new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
			WritableFont timesfont = new WritableFont(WritableFont.TIMES, 12);
			WritableCellFormat formatLeftBold = new WritableCellFormat(timesfontbold);
			WritableCellFormat formatRightBold = new WritableCellFormat(timesfontbold);
			WritableCellFormat formatLeft = new WritableCellFormat(timesfont);
			WritableCellFormat formatRight = new WritableCellFormat(timesfont);
			
			formatLeft.setWrap(true);
			formatLeft.setAlignment(Alignment.LEFT);
			formatRight.setWrap(true);
			formatLeft.setAlignment(Alignment.RIGHT);
			SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
			
			if(productList.size() > 0){
				productSheet = itemExcel.createSheet("ProductSheet", 1);
				int indexRow=0, indexCol=0;
				productSheet.setColumnView(indexCol, 10);
				productSheet.addCell(new Label(indexCol++, indexRow, "Zone", formatLeftBold));
				productSheet.setColumnView(indexCol, 35);
				productSheet.addCell(new Label(indexCol++, indexRow, "Item", formatLeftBold));
				productSheet.setColumnView(indexCol, 35);
				productSheet.addCell(new Label(indexCol++, indexRow, "Specification", formatLeftBold));
				productSheet.setColumnView(indexCol, 10);
				productSheet.addCell(new Label(indexCol++, indexRow, "UOM", formatLeftBold));
				productSheet.setColumnView(indexCol, 10);
				productSheet.addCell(new Label(indexCol++, indexRow, "Quantity", formatRightBold));
				productSheet.setColumnView(indexCol, 10);
				productSheet.addCell(new Label(indexCol++, indexRow, "From Qty.", formatRightBold));
				productSheet.setColumnView(indexCol, 10);
				productSheet.addCell(new Label(indexCol++, indexRow, "To Qty.", formatRightBold));
				productSheet.setColumnView(indexCol, 15);
				productSheet.addCell(new Label(indexCol++, indexRow, "Sub Sequence", formatRightBold));
				//Label header8 = new Label(indexCol++, indexRow, "Delivery Term");
				productSheet.setColumnView(indexCol, 16);
				productSheet.addCell(new Label(indexCol++, indexRow, "Unit Price(RM)", formatRightBold));
				productSheet.setColumnView(indexCol, 20);
				productSheet.addCell( new Label(indexCol++, indexRow, "Total Amount(RM)", formatRightBold));
				productSheet.setColumnView(indexCol, 15);
				productSheet.addCell( new Label(indexCol++, indexRow, "Effective Date", formatLeftBold));
				productSheet.setColumnView(indexCol, 15);
				productSheet.addCell( new Label(indexCol++, indexRow, "Expire Date", formatLeftBold));
				
				for(CtItemPrice price : productList){
					IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractItemSO.class);
					ContractItemReqDTO reqDTO = new ContractItemReqDTO();
					reqDTO.setContractItemId(price.getCtContractItem().getContractItemId());
					reqDTO.setEffDate(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					ContractItemResDTO contractResDTO = contractItemSO.getMaxQuantity(reqDTO);
					CtItemMaxQty ctItemMaxQty = contractResDTO.getCtItemMaxQuantity();
					
					indexRow++; indexCol=0;
					productSheet.addCell(new Label(indexCol++, indexRow, null == price.getCtContractZone() ? "N/A" : price.getCtContractZone().getZoneName(), formatRight));
					productSheet.addCell(new Label(indexCol++, indexRow, price.getCtContractItem().getItemName(), formatRight));
					productSheet.addCell(new Label(indexCol++, indexRow, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion() ? 
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClSpecDetail(), formatRight));//specification
					productSheet.addCell(new Label(indexCol++, indexRow, price.getCtContractItem().getUom().getUomName(), formatRight));
					productSheet.addCell(new Label(indexCol++, indexRow, ctItemMaxQty.getMaxQuantity()+"", formatLeft));
					
					productSheet.addCell(new Label(indexCol++, indexRow, price.getFromQty() == null ? "N/A": price.getFromQty() + "", formatLeft));
					productSheet.addCell(new Label(indexCol++, indexRow, price.getToQty() == null ? "N/A" : price.getToQty() + "", formatLeft));
					productSheet.addCell(new Label(indexCol++, indexRow, price.getSubSequence() == null ? "N/A" : price.getSubSequence() + "", formatLeft));
					/*String delveryTerm = "";
					for(CtContractZone zone : item.getCtContractVer().getCtContractZones()){
						delveryTerm = delveryTerm + zone.getDeliveryTerm() + " ";
					}
					sheet.addCell(new Label(indexCol++, indexRow, delveryTerm));*/
					productSheet.addCell(new Label(indexCol++, indexRow, nf.format(price.getUnitPrice().longValue()) + "", formatLeft));
					if(null != price.getUnitPrice() && null != ctItemMaxQty.getMaxQuantity()){
						productSheet.addCell(new Label(indexCol++, indexRow, price.getUnitPrice().multiply(ctItemMaxQty.getMaxQuantity()).toString(), formatLeft));
					} else {
						productSheet.addCell(new Label(indexCol++, indexRow, "N/A"));
					}
					productSheet.addCell(new Label(indexCol++, indexRow, df.format(price.getEffDate()), formatRight));
					productSheet.addCell(new Label(indexCol++, indexRow, df.format(price.getExpDate()), formatRight));
				}
			}
			
			if(serviceList.size() > 0){
				ServiceSheet = itemExcel.createSheet("ServiceSheet", 2);
				int indexRow2=0, indexCol2=0;
				ServiceSheet.setColumnView(indexCol2, 25);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Item", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 25);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Specification", formatLeftBold));
				//ServiceSheet.setColumnView(indexCol2, 10);
				//ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Fulfilment Type", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 10);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "UOM", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Frequency per UOM", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "No. UOM per day", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "No. UOM per month", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Duration (Month)", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 10);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Quantity", formatLeftBold));
				//ServiceSheet.setColumnView(indexCol2, 10);
				//ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Estimated Value (RM)", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Unit Price (RM)", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell(new Label(indexCol2++, indexRow2, "Total Amount (RM)", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell( new Label(indexCol2++, indexRow2, "Effective Date", formatLeftBold));
				ServiceSheet.setColumnView(indexCol2, 15);
				ServiceSheet.addCell( new Label(indexCol2++, indexRow2, "Expire Date", formatLeftBold));
				
				for(CtItemPrice price : serviceList){
					indexRow2++; indexCol2=0;
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, price.getCtContractItem().getItemName(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClSpecDetail(), formatRight));//specification
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUom().getUomName(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomFrequency() ? 
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomFrequency().toString(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerDay() ? 
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerDay().toString(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerMonth() ?
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerMonth().toString(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClDurationMonth()?
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClDurationMonth().toString(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, null == price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClQuantity() ?
							"N/A" : price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClQuantity().toString(), formatLeft));
								ServiceSheet.addCell(new Label(indexCol2++, indexRow2,  price.getUnitPrice().toString(), formatLeft));
						//ServiceSheet.addCell(new Label(indexCol2++, indexRow2, price.getUnitPrice().toString(), formatRight));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, price.getUnitPrice().multiply(price.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClQuantity()).toString(), formatLeft));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, df.format(price.getEffDate()), formatLeft));
					ServiceSheet.addCell(new Label(indexCol2++, indexRow2, df.format(price.getExpDate()), formatLeft));
				}
			}
			
			itemExcel.write();
			itemExcel.close();
		} catch (IOException | WriteException e) {
			e.printStackTrace();
		}
		log.debug("Exit formJadualHargaExcel()");
	}
	
	private String getContent(String content, String parameterCode) {
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		Map<String, String> valuesMap = new HashMap<String, String>();
		StringBuffer strBuff = new StringBuffer();
		
		if(ContractConst.TEMPLATE_SURAT_SETUJU_TERIMA.equalsIgnoreCase(parameterCode)) {
			IAddressSO addressSO = (IAddressSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IAddressSO.class);
			AddressReqDTO addressReqDTO = new AddressReqDTO();
			addressReqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
			addressReqDTO.setOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			AddressResDTO addressResDTO = addressSO.retrieveAddressTypeList(addressReqDTO);
			PmAddressType pmAddressType = null;
			if(addressResDTO.getAddressTypeList().size()>0) {
				pmAddressType = addressResDTO.getAddressTypeList().get(0);
			}
			
			if(getManagedBean().getCtContract().isBondReq()){
				strBuff.append("*3.1 mendepositkandenganKerajaan, suatu Bon PelaksanaandalambentukJaminan Bank/Jaminan Syarikat Kewangan/ JaminanInsurans " +
						"yang takbolehbataldaripadasebuah Bank/Syarikat Kewangan/Syarikat Insurans yang diluluskandandidaftarkan di Malaysia " +
						"denganjumlahsebanyak RM ${PERF_BOND_AMOUNT} yang merupakan${BOND_AMOUNT_IN_TEXT}peratus (${PERCENTAGE}%) darijumlahkontrakdantempohsahlakusehingga" +
						"${NEW_BOND_DATE}. Dokumen bon PelaksanaantersebuthendaklahdikemukakankepadaKerajaandalamtempoh${BOND_SUBMISSION_PERIOD}" +
						"haridaritarikhsuratinidisetujuterima;<br/><br/>");
				strBuff.append("*3.2 SekiranyapembekalmendepositkankepadaKerajaanPolisiInsurans. Pembekalhendaklahmengemukakanresit-resit " +
						"premium yang telahdibayarbagitujuanpemeriksaan.<br/><br/>");
				strBuff.append("*3.4 Apa-apakegagalandalammematuhikehendak-kehendakpara 3 inidalamtempohmasa yang dinyatakan, " +
						"kecualijikadiabaikandengannyataolehKerajaan, akanmenyebabkanpenerimaan LOA** " +
						"initerbataldanselepasituKerajaantidaklahdenganapa-apacarajugabertanggungjawabkepadatuan.<br/><br/>");
			}
			
			valuesMap.put("PERFORMANCE_BOND", strBuff.toString());
			valuesMap.put("FILE_REF_NO", getManagedBean().getContractVer().getCtAmendment().getFileRefNo());
			valuesMap.put("LOA_NO", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getLoaNo());
			valuesMap.put("LOA_SUBMITED_DATE", df.format(getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckDueDate()));
			valuesMap.put("SUPPLIER_NAME", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			
			if(pmAddressType!=null) {
				valuesMap.put("ADDRESS", AddressUtils.getFullAddress(pmAddressType.getPmAddress()));
			}
			else {
				valuesMap.put("ADDRESS", "");	
			}			
			valuesMap.put("CONTRACT_TITLE", getManagedBean().getCtContract().getContractName());
			valuesMap.put("PHYSICAL_CONTRACT_NO", getManagedBean().getCtContract().getContractPhysicalNo());
			valuesMap.put("AMENDMENT_EFF_DATE", df.format(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()));
			valuesMap.put("PERF_BOND_AMOUNT", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt() + "");
			valuesMap.put("BOND_AMOUNT_IN_TEXT", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt() + "");
			valuesMap.put("PERCENTAGE", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondPercentage() + "");
			valuesMap.put("NEW_BOND_DATE", df.format(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()));
			valuesMap.put("BOND_SUBMISSION_PERIOD", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getMtoDuration() + "");
			//valuesMap.put("NEW_CONTRACT_EFFECTIVE_DATE", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			valuesMap.put("CONTRACT_SIGNER_NAME", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin().getUserName());
			valuesMap.put("CONTRACT_SIGNER_POSITION", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin().getDesignation());

			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			content = sub.replace(content);
		} 
		else if(ContractConst.TEMPLATE_RINGKASAN_KONTRAK.equalsIgnoreCase(parameterCode)) {
			valuesMap.put("FILE_NO", getManagedBean().getCtContract().getQuotationTender().getFileNo());
			valuesMap.put("SOURCING_NO", getManagedBean().getCtContract().getLoa().getFileExtNo());
			valuesMap.put("SOURCING_TITLE",  getManagedBean().getCtContract().getLoa().getScLoiLoa().getTitle());
			valuesMap.put("SUCCESSFUL_SUPPLIER_NAME", getManagedBean().getCtContract().getCurrentContractVer().getCtContractValue().getSupplierName());
			valuesMap.put("CONTRACT_VALUE",  getManagedBean().getContractVer().getCtContractValue().getContractAmount()+"");
			double contractValue = this.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue() + this.getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue();
			valuesMap.put("NEW_CONTRACT_VALUE", contractValue + "");
			IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setParameterId(getManagedBean().getCtContract().getFulfillmentType().getParameterId());
			valuesMap.put("FULFILLMENT_TYPE", parameterSO.retrieveParameterById(parameterReqDTO).getPmParameter().getPmParameterType().getParameterName());
			valuesMap.put("REFER_ATTACHMENT", "Refer Attachment");
			for(CtDeduction deduction : getManagedBean().getContractVer().getCtDeductions()){
				if(deduction.getDeductionType().getParameterCode().equals(ParameterConst.DEDUCTION_TYPE_FIXED_DEPOSIT)){
					valuesMap.put("DEDUCTION_AMOUNT", deduction.getAmount()+"");
				}
				else{
					valuesMap.put("DEDUCTION_AMOUNT", "");
				}
			}
			valuesMap.put("MONTHS", getManagedBean().getContractVer().getCtContractValue().getContractPeriod()+"");
			valuesMap.put("EFF_START_DATE", df.format(getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			valuesMap.put("EFF_END_DATE", df.format(getManagedBean().getContractVer().getCtContractValue().getExpDate()));
			if(getManagedBean().getContractVer().getCtAmendment().isContractExtend()){
				valuesMap.put("AMEND_EFF_EXP_DATE", df.format(getManagedBean().getContractVer().getCtAmendment().getExtendStartDate()) 
						+ " " + df.format(getManagedBean().getContractVer().getCtAmendment().getExtendEndDate()));
			}
			else {
				valuesMap.put("AMEND_EFF_EXP_DATE", "");
			}
			valuesMap.put("EFF_DATE", df.format(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()));
			valuesMap.put("BOND_VALUE", getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt() + "");
			
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			content = sub.replace(content);
		}
		else if(ContractConst.TEMPLATE_INTEGRITY_PACT.equalsIgnoreCase(parameterCode)) {
			valuesMap.put("PTJ_NAME", getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());
			valuesMap.put("LOA_TITLE", getManagedBean().getContractVer().getCtAmendment().getTitle());
			valuesMap.put("SUPPLIER_USER_NAME", getFacesScopes().getUserScreenName());
			valuesMap.put("SUPPER_USER_IC_NO", getFacesScopes().getUserSessionBean().getIdentificationNo());
			valuesMap.put("SUPPLIER_NAME", getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			valuesMap.put("SUPPLIER_MOF_REG_NO", getManagedBean().getCtContract().getSmSupplier().getRegNo());
			StrSubstitutor sub = new StrSubstitutor(valuesMap);
			content = sub.replace(content);
		}
		return content;
	}
	
	public void fileUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    for(CtPerformanceBond ctPerformanceBond : this.getManagedBean().getContractVer().getCtPerformanceBonds()) {
	    	if(ctPerformanceBond.equals(this.getManagedBean().getCtPerformanceBond())) {
	    		ctPerformanceBond.setFileName(item.getFileName());
	    		ctPerformanceBond.setFilePath(filePath);
	        }
	    }
	}
	
	public void contentLetterUpload(FileUploadEvent event) throws Exception {
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
	    for(ScLoaReport reports : this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports()) {
	    	if(reports.equals(this.getManagedBean().getSelectedScLoaReports())) {
	    		reports.getScLoaReportAttachment().setFileName(item.getFileName());
	    		reports.getScLoaReportAttachment().setFilePath(filePath);
	    		reports.getScLoaReportAttachment().setFileDesc(item.getFileName());
	        }
	    }
	}
	
	public void letterOfAcceptenceUpload(FileUploadEvent event) throws Exception{
		UploadedFile item = event.getFile();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setFileName(item.getFileName());
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setFileDesc(item.getFileName());
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setFilePath(filePath);	
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setDocType(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaAttachment().setScLoa(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
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
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if(getManagedBean().getContractVer().getCtAmendment().isContractExtend())
				     	addDupObj.setExpDate(getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					else
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
					ctItemPrice.setExpDate(previousDate.getTime());
					ctItemPriceList.add(addDupObj);
				}
			}
		}
		this.getManagedBean().getSelectedItem().getCtItemPrices().clear();
		this.getManagedBean().getSelectedItem().getCtItemPrices().addAll(ctItemPriceList);
		ctItemPriceList.clear();
	}
	
	public void changeAmendmentEffDate(){
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		if(this.getManagedBean().getSelectedServiceItem() != null){
			    for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
			    	ctItemPrice.setExpDate(previousDate.getTime());
				     if(ctItemPrice.isChangePrice()){
				         ctItemPrice.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
				         ctItemPrice.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
				     }
			  }
		  }
		  if(this.getManagedBean().getSelectedItem() != null ){
			  
			  if(this.getManagedBean().getSelectedItem().getCtItemPrices() != null && this.getManagedBean().getSelectedItem().getCtItemPrices().size()>0){
			      for(CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()) {
			    	  ctItemPrice.setExpDate(previousDate.getTime());
			    	  if(ctItemPrice.isChangePrice()){
				             ctItemPrice.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
				             ctItemPrice.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			    	  }
			       }
			   }if(this.getManagedBean().getSelectedItem().getCtItemCeilingQties() != null && this.getManagedBean().getSelectedItem().getCtItemCeilingQties().size()>0){
			      for(CtItemCeilingQty ctItemCeilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			    	     ctItemCeilingQty.setExpDate(previousDate.getTime());
				         if(ctItemCeilingQty.isChangeCeilQuantity()){
				        	 ctItemCeilingQty.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
				        	 ctItemCeilingQty.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
				         }   
			       }
			  }if(this.getManagedBean().getSelectedItem().getCtItemMaxQties() != null && this.getManagedBean().getSelectedItem().getCtItemMaxQties().size()>0){
			      for(CtItemMaxQty ctItemMaxQty : this.getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			    	  ctItemMaxQty.setExpDate(previousDate.getTime());
				         if(ctItemMaxQty.isChangeQuantity()){
				        	 ctItemMaxQty.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
				        	 ctItemMaxQty.setExpDate(this.getManagedBean().getContractVer().getCtContractValue().getExpDate());
				         }
			       }
			  }
		  }
	}
	
	public void editServiceItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtServiceItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if(getManagedBean().getContractVer().getCtAmendment().isContractExtend())
				     	addDupObj.setExpDate(getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					else
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
					ctItemPrice.setExpDate(previousDate.getTime());
					ctItemPriceList.add(addDupObj);
				}
			}
		}
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().clear();
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().addAll(ctItemPriceList);
		ctItemPriceList.clear();
	}
	    
	public void deleteItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedItem().getCtItemPrices().remove(getManagedBean().getCtItemPrices());
	}
	public void deleteServiceItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtServiceItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(getManagedBean().getCtServiceItemPrices());
	}
	    
	public void editCeilingQuantityOnIndexs() {
		List<CtItemCeilingQty> ctItemCeilingQtys = new ArrayList<CtItemCeilingQty>();
		CtItemCeilingQty addDupObj = new CtItemCeilingQty();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemCeilingQty ceilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			ctItemCeilingQtys.add(ceilingQty);
			if (!ceilingQty.isChangeCeilQuantity() && !ceilingQty.isChkIndexOfRow()) {
				if (ceilingQty.equals(this.getManagedBean().getCtItemCeilingQtys())) {
					addDupObj.setCeilingQuantity(ceilingQty.getCeilingQuantity());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if(getManagedBean().getContractVer().getCtAmendment().isContractExtend())
				     	addDupObj.setExpDate(getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					else
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
					ceilingQty.setExpDate(previousDate.getTime());
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
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemMaxQty ctItemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			ctItemMaxQtyList.add(ctItemMaxQty);
			if (!ctItemMaxQty.isChangeQuantity() && !ctItemMaxQty.isChkIndexOfRow()) {
				if (ctItemMaxQty.equals(getManagedBean().getCtItemMaxQty())) {
					addDupObj.setMaxQuantity(ctItemMaxQty.getMaxQuantity());
					addDupObj.setEffDate(getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if(getManagedBean().getContractVer().getCtAmendment().isContractExtend())
				     	addDupObj.setExpDate(getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					else
						addDupObj.setExpDate(ctItemMaxQty.getExpDate());
					if (ctItemMaxQty.getCtContractItem() != null) {
						addDupObj.setCtContractItem(ctItemMaxQty.getCtContractItem());
					}
					addDupObj.setChangeQuantity(true);
					addDupObj.setAssociateCtItemMaxQty(ctItemMaxQty);
					ctItemMaxQty.setExpDate(previousDate.getTime());
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
			 try { 
				 committeDataModel.reloadData();
				} catch (Exception e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), "contract search failed."));
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
				     committeeMember.setPmUser(user);
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
		
		 private void getLoaDocumentList(){
			
			 IDocumentSO documentSO = null;
			 DocumentReqDTO reqDTO = null;
			 DocumentResDTO resDTO = null;
			try{
				
				if(getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports() == null)
			    	 getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaReports(new ArrayList<ScLoaReport>());
				
				reqDTO = new DocumentReqDTO();
			     documentSO = (IDocumentSO)BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
			     reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
			     resDTO = documentSO.retrieveDocumentCheckList(reqDTO);
			     for(PmDocument document : resDTO.getDocumentList()){
			      	 ScLoaReport reports = new ScLoaReport();
			    	 reports.setPmDocument(document);
			    	 reports.setTitle(document.getDocName());
			    	 reports.setDefinationType(document.getDocDefinitionTypeCode());
			    	 reports.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			    	 reports.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
			    	 reports.setScLoa( this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
			    	 this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().add(reports);	
			     }
			    
			}catch(Exception e){
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}finally{
				documentSO = null;
				reqDTO = null;
				resDTO = null;
			}		
		}
		
		 public List<SelectItem> getPopulateBankAccountList() {
	  
	    	 if( populateBankAccountList == null){
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
			 if(this.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() != null) {
				 for(SmSupplierBank supplierBank : this.getManagedBean().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()){
					 if(supplierBank.getAccountNo().equalsIgnoreCase(this.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo())){
						 this.getManagedBean().getContractVer().getCtContractValue().setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());  
						 this.getManagedBean().getContractVer().getCtContractValue().setBankBranch(supplierBank.getSmSupplierBranch().getBranchName());
						 this.getManagedBean().getContractVer().getCtContractValue().setBankAddress(supplierBank.getSmAddress());
				    }
			    }
		    }else{
		    	this.getManagedBean().getContractVer().getCtContractValue().setBankName(null);  
				 this.getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
				 this.getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
		    }
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
		if (getUserRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()))
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
			getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
		}
	}
	
	public void onExtensionPeriodChanged() {
		Calendar c = Calendar.getInstance();
		c.setTime(getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.DATE, 1);
		getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());
		c.add(Calendar.MONTH, getManagedBean().getContractVer().getCtAmendment().getExtendPeriod());
		getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
		getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
	}
		
	public void onItemPriceChecked() {
		if(getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
		}else if(!getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() && !getManagedBean().getContractVer().getCtAmendment().isQuantityChange()){
			getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
		}ConstructTabSideMenu();
	}

	public void onQuantityChecked() {
		if(getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			getManagedBean().getContractVer().getCtAmendment().setValueChange(true);
		}else if(!getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() && !getManagedBean().getContractVer().getCtAmendment().isQuantityChange()){
			getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
		}ConstructTabSideMenu();
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
	
	/*public List<CtContractItem> getContractItemByType(String itemTypeCode) {
		List<CtContractItem> ctItemList = new ArrayList<CtContractItem>();
		for (CtContractItem item : getManagedBean().getCtItemList()) {
			if (item.getItemType().getParameterCode().equalsIgnoreCase(itemTypeCode)) {
				ctItemList.add(item);
			}
		}
		return ctItemList;
	}
	*/
	public boolean breachAP201Rules(int rulesType) {
		if(rulesType==1) { // extend
			if(getManagedBean().getAmendHistoryList().size()>0 || getManagedBean().getContractVer().getCtAmendment().getExtendPeriod()>=24) {
				return true;
			}
		} else if(rulesType==2) { // quantity
			if(getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue() > 7500000) {
				return true;
			}
			if(getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue() > 25000000) {
				return true;
			}
			for(CtContractItem ctContractItem : getManagedBean().getCtItemList()) {
				for(CtItemCeilingQty ctItemCeilingQty : ctContractItem.getCtItemCeilingQties()) {
					if(ctItemCeilingQty.isChangeCeilQuantity()) {
						if(ctItemCeilingQty.getCeilingQuantity().doubleValue() > ctItemCeilingQty.getAssociateCtItemCeilingQty().getCeilingQuantity().doubleValue() * 1.5) {
							return true;
						}
					}
				}
			}
		} else if(rulesType==3) { // value
			if(getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue() > 7500000) {
				return true;
			}
			if(getManagedBean().getContractVer().getCtContractValue().getContractAmount().doubleValue() > 25000000) {
				return true;
			}
		}
		return false;
	}
	
	
	public void addLoaContents() {
		ScLoaReport loaReport = new ScLoaReport();
		try{
	 
			    loaReport.setDefinationType(new PmParameter());
			    loaReport.setDefinationType(retrieveParameterByCodeType(ParameterConst.DOCUMENT_DEFINITION_TYPE, ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD));
			    loaReport.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			    loaReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
			    loaReport.setScLoa(this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
			 
			    ScLoaReportAttachment attachment = new ScLoaReportAttachment();
			    attachment.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			    attachment.setScLoaReport(loaReport);
			    
			
			    loaReport.setScLoaReportAttachment(attachment);
			    this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().add(loaReport);
		}catch(Exception ex){
		   ex.printStackTrace();
		}
	}
	
	public void deleteLoaContents() {
		this.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().remove(this.getManagedBean().getSelectedScLoaReports());
	}
	
	public PmParameter retrieveParameterByCodeType(String parameterType, String parameterCode) throws NamingException {
		
		 IParameterSO parameterSO = null;
		 ParameterReqDTO reqDTO = null;
		 try{
			
			reqDTO = new ParameterReqDTO();
			reqDTO.setParameterCode(parameterCode);
			reqDTO.setParameterType(parameterType);
			parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterResDTO resDTO = parameterSO.retrieveParameterByTypeAndCode(reqDTO);
			return resDTO.getPmParameter();
		}catch(Exception e) {
			 log.error("Exception>>", e);
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		 } finally {
			 parameterSO = null;
		 }
		 return null;
	 }
	
	public boolean invalidContractExtendPeriod() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, ContractConst.CONTRACT_EXTEND_BEFORE_DAYS);

		return (getManagedBean().getContractVer().getCtContractValue().getExpDate().before(c.getTime()));
	}
	
	 public String getEditorValue() {
		   return editorValue;
	    }

	    public void setEditorValue(String editorValue) {
		  this.editorValue = editorValue;
	   }
		
	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
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

	public Date getCurrentDate() {
		return  new Date();
	}
	
	
	public IPagingDataModel<PmUser> getCommitteDataModel() {
		return committeDataModel;
	}

	public void setCommitteDataModel(IPagingDataModel<PmUser> committeDataModel) {
		this.committeDataModel = committeDataModel;
	}


	private class CommittePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public CommittePagingDataModel() {
			super("newCommitteTable");
		}
		
		@Override
		public ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
	
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			
			UserReqDTO userReqDTO = null;
			UserResDTO resDto = null;
			IUserSO userSO  = null;
			try {
				    userReqDTO = new UserReqDTO();
					if(getManagedBean().getName() != null && !"".equalsIgnoreCase(getManagedBean().getName())){
					      searchBean.getDataFilterBeans().add(new SearchFilterBean(
							  "userName", SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getName()));
				
					}if(getManagedBean().getIdentificationType() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationType())){
						searchBean.getDataFilterBeans().add(new SearchFilterBean("identificationCode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getIdentificationType()));
					}
					
					if(getManagedBean().getIdentificationNo() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())){
						searchBean.getDataFilterBeans().add(new SearchFilterBean("identificationNo", SearchOperator.OPERATOR_LIKE.value(),getManagedBean().getIdentificationNo()));
					}
					
				    userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
					userReqDTO.setUserSearchBean(searchBean);
					resDto = userSO.searchUserProfile(userReqDTO);
					if (resDto.isSuccessful()) {
						return resDto.getUserResultBean();
					}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
			    userReqDTO = null;
			    resDto = null;
				userSO = null;
			}
			return null;
		}
	}	
}
