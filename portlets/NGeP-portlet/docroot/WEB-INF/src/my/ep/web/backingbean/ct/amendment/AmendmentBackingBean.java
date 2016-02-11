/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.amendment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.dtl.IContractItemSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.dtl.ContractItemReqDTO;
import my.ep.dto.ct.dtl.ContractItemResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.contract.amendment.CTContractAmendmentDataType;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.ct.CtAttachment;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.CtRemark;
import my.ep.persistence.entity.ct.amend.CtAmendment;
import my.ep.persistence.entity.ct.amend.CtCommitteeMember;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.dtl.CtContractAmount;
import my.ep.persistence.entity.ct.dtl.CtMeeting;
import my.ep.persistence.entity.ct.dtl.CtPerformanceBond;
import my.ep.persistence.entity.ct.dtl.CtPidReason;
import my.ep.persistence.entity.ct.dtl.CtProgressivePayment;
import my.ep.persistence.entity.ct.dtl.CtSchedule;
import my.ep.persistence.entity.ct.dtl.deduction.CtDeduction;
import my.ep.persistence.entity.ct.dtl.item.CtContractItem;
import my.ep.persistence.entity.ct.dtl.item.CtItemCeilingQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemMaxQty;
import my.ep.persistence.entity.ct.dtl.item.CtItemPrice;
import my.ep.persistence.entity.ct.dtl.zone.CtContractZone;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmProfileGroup;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmAddressType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoaReportAttachment;
import my.ep.persistence.entity.sc.loaloi.ScLoaWitness;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.type.sc.MemberRole;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.amendment.AmendmentManagedBean;
import my.ep.web.util.AddressUtils;
import my.ep.web.util.CommonUtils;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.NumberWordUtil;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;
import org.springframework.util.CollectionUtils;

import com.liferay.portal.util.PortalUtil;

/**
 * Backing Bean For Contract Amendment Details
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "amendmentBackingBean")
@ViewScoped
public class AmendmentBackingBean extends ContractBaseBackingBean<AmendmentManagedBean> {
    
	private static Logger log = Logger.getLogger(AmendmentBackingBean.class);
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private String editorValue;
	private ContractBackingBean ctBackingBean = null;
	private PmUser[] selectedPmUser;
	private CtMeeting[] selectedMeetingUser;
	private CtPerformanceBond[] selectedBondList;
	private List<SelectItem> populateBankAccountList;
	private IPagingDataModel<PmUser> committeDataModel = new CommittePagingDataModel();	
	
	/**
	 * The constructor used for initializing the variable.
	 * This method will initialize the button and approver list.
	 */
	public AmendmentBackingBean() {
		/* Initialization of button */
		if(ctBackingBean == null){
			ctBackingBean = new ContractBackingBean();
		}
		if (this.getManagedBean().getAction() == null) {
			this.getManagedBean().setAction(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});
			ctBackingBean.getManagedBean().setContractVer(new CtContractVer());
		}
		if (getManagedBean().getUserMap().size() == 0) {
			initializeApproverList();
		}
	}
	
	/**
	 * This  method used to initialize approver list.
	 */
	private void initializeApproverList() {
		if (getManagedBean().getApproverModel() == null) {
			List<PmUser> source = new ArrayList<PmUser>();
			List<PmUser> target = new ArrayList<PmUser>();
			getManagedBean().setApproverModel(new DualListModel<PmUser>(source, target));
		}
	}
	
	/**
	 * @return the editorValue
	 */
	public String getEditorValue() {
		return editorValue;
	}

	/**
	 * @param editorValue the editorValue to set
	 */
	public void setEditorValue(String editorValue) {
		this.editorValue = editorValue;
	}

	/**
	 * @return the selectedPmUser
	 */
	public PmUser[] getSelectedPmUser() {
		return selectedPmUser;
	}

	/**
	 * @param selectedPmUser the selectedPmUser to set
	 */
	public void setSelectedPmUser(PmUser[] selectedPmUser) {
		this.selectedPmUser = selectedPmUser;
	}

	/**
	 * @return the selectedMeetingUser
	 */
	public CtMeeting[] getSelectedMeetingUser() {
		return selectedMeetingUser;
	}

	/**
	 * @param selectedMeetingUser the selectedMeetingUser to set
	 */
	public void setSelectedMeetingUser(CtMeeting[] selectedMeetingUser) {
		this.selectedMeetingUser = selectedMeetingUser;
	}

	/**
	 * @return the selectedBondList
	 */
	public CtPerformanceBond[] getSelectedBondList() {
		return selectedBondList;
	}

	/**
	 * @param selectedBondList the selectedBondList to set
	 */
	public void setSelectedBondList(CtPerformanceBond[] selectedBondList) {
		this.selectedBondList = selectedBondList;
	}
	
	/**
	 * @return the committeDataModel
	 */
	public IPagingDataModel<PmUser> getCommitteDataModel() {
		return committeDataModel;
	}

	/**
	 * @param committeDataModel the committeDataModel to set
	 */
	public void setCommitteDataModel(IPagingDataModel<PmUser> committeDataModel) {
		this.committeDataModel = committeDataModel;
	}
	
	/**
	 * Pagination for available committee search list
	 */
	private class CommittePagingDataModel extends PagingDataModel<PmUser> {
		private static final long serialVersionUID = 1L;

		public CommittePagingDataModel() {
			super("newCommitteTable");
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PmUser> pageChangeListener(ISearchBean<PmUser> searchBean) {
			UserReqDTO userReqDTO = null;
			UserResDTO resDto = null;
			IUserSO userSO  = null;
			try {
				userReqDTO = new UserReqDTO();
				if (getManagedBean().getCommitteeName() != null && !"".equalsIgnoreCase(getManagedBean().getCommitteeName())) {
					SearchFilterBean filterBean = new SearchFilterBean("userName", SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getCommitteeName());
					filterBean.setCaseInsensitive(true);
					searchBean.getDataFilterBeans().add(filterBean);
				}
				if (getManagedBean().getIdentificationType() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationType())){
					searchBean.getDataFilterBeans().add(new SearchFilterBean("identificationCode.parameterCode", SearchOperator.OPERATOR_EQUALS.value(),getManagedBean().getIdentificationType()));
				}
				if (getManagedBean().getIdentificationNo() != null && !"".equalsIgnoreCase(getManagedBean().getIdentificationNo())){
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
			} finally {
			    userReqDTO = null;
			    resDto = null;
				userSO = null;
			}
			return null;
		}
	}
	
	/**
	 * The method used for searching the available committee member.
	 */
	public void searchExistingCommitteMember() {
		try { 
			committeDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}
	
	/**
	 * The method used to reset the search criteria for committee member.
	 */
	public void searchExistingCommitteMemberReset() {
		this.getManagedBean().setCommitteeName(null);
		this.getManagedBean().setIdentificationType(null);	
		this.getManagedBean().setIdentificationNo(null);	
		committeDataModel.resetData();
	}
	
	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() throws UnsupportedEncodingException {
		IContractAmendmentSO contractAmendmentSO;
		ContractResDTO resDTO = null;
	
		try {
			HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
			
			if (taskId == null) {
				ConstructTabSideMenu();
				return;
			}
			FacesScopes face = new FacesScopes();
			Object token = face.getBPMAuthenticationCtxToken();
			getManagedBean().setTaskId(taskId);
			getManagedBean().setSelectedTabIndex(0);
			     
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
	     	reqDTO.setTaskId(taskId);
			     
	     	ContractReqDTO ctReqDTO = new ContractReqDTO();
	     	ctReqDTO.setUpdateAction(false);
			     
	     	IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
	     	BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
	     	Object obj = res.getPojo();
			getManagedBean().setTaskName(res.getTaskName());
			ctReqDTO.setTaskName(res.getTaskName());
			String[] outcome = new String[res.getAction().length];
			int i = 0;
			 
			if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				outcome = new String[res.getAction().length+1];
				outcome[i++] = BpmOutcomeConst.BPM_SAVE;
			}
			
			for (String action : res.getAction()) {
				outcome[i++] = action;
			}
			getManagedBean().setAction(outcome);
			
			CTContractAmendmentDataType dataType = (CTContractAmendmentDataType)obj;
			ctReqDTO.setContractId(new Long(dataType.getContractId()));
			ctReqDTO.setContractVerId(new Long(dataType.getDocId()));
			ctReqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractAmendmentSO.class);
			resDTO = contractAmendmentSO.retrieveContractAmendVerDtls(ctReqDTO);
			setAmendmentContractVer(resDTO);
			getManagedBean().setSelectedItem(null);
			getManagedBean().setSelectedServiceItem(null);			     
			getManagedBean().setBpkSaSkApprover(false);
		   	getManagedBean().setMemberSignOff(false);  
			getManagedBean().setPbConfirmMeeting(false);
		 	getManagedBean().setSignUpdateDecision(false);
		    getManagedBean().setLetterOfAcceptance(false);
			getManagedBean().setPrepareLOA(false);
			getManagedBean().setDecisionPbQcLOA(false);
			getManagedBean().setSignOffDecision(false);
			
			if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
				getManagedBean().setPbConfirmMeeting(true);
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null
						&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().isEmpty()) {
					Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers());
				}
				if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
					getManagedBean().setSignUpdateDecision(true);
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isBpkApproved()) {
						getManagedBean().setPbQcApproved("A");
					}
				}
			} else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				getManagedBean().setLetterOfAcceptance(true);
				getManagedBean().setPrepareLOA(true);
	         	getManagedBean().setPbQcApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbQcApproved() ? "A" : "R");
	         	getManagedBean().setWitnessHeader(ResourceBundleUtils.getMessage("lbl.ct.panel.agency.witness", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
	         	getManagedBean().setWitnessHeaderTooltip(ResourceBundleUtils.getMessage("lbl.ct.tooltip.agency.witness.dtls", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
	        	if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() > 0 
	        			&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
	        		getManagedBean().setDecisionPbQcLOA(true);
	        	}
	        	if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() == null) {
	        		ctReqDTO.setCtContractVer(ctBackingBean.getManagedBean().getContractVer());
	        		ContractResDTO resDTO1 = contractAmendmentSO.setScLoa(ctReqDTO);
	        		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentLoa(resDTO1.getAmendmentScLoa());
	        		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractAdmin() == null) {
	        			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractAdmin(getFacesScopes().getUserSessionBean());
	        		}
	        	}
	        	if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
	        		getManagedBean().setShowPrintButton(true);
	        		getManagedBean().setBpmActionFlag(false);
	        	}
			} else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
				this.getManagedBean().setMemberSignOff(true);
				this.getManagedBean().setPbConfirmMeeting(true);
				this.getManagedBean().setSignUpdateDecision(false);
			} else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				this.getManagedBean().setAckFA(ContractConst.SUPPLIER_ACK_LOA);
				this.getManagedBean().setWitnessHeader(ResourceBundleUtils.getMessage("lbl.ct.panel.supplier.witness", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
				this.getManagedBean().setWitnessHeaderTooltip(ResourceBundleUtils.getMessage("lbl.ct.tooltip.supplier.witness.dtls", ResourceBundleUtils.BUNDLE_COMMON_LABELS, face.getUserLocale()));
				          
				VelocityContext context = new VelocityContext();
				String vmFile = ContractConst.DOCPATH_CT_INTEGRITY_PACT;
				String content = null;
				getManagedBean().setEditorValue("");
				FacesScopes fs = new FacesScopes();
				String hostUrl = fs.getRequest().getServerName() + ":" + fs.getRequest().getServerPort();				
				context = new VelocityContext();	
				context.put("hostUrl", hostUrl);
				context.put("ptjName", ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgName());
				context.put("amendmentTitle", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle());
				context.put("contractNo", ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
				context.put("amendmentRefNo", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo());
				context.put("supplierUserName", getFacesScopes().getUserSessionBean().getUserName());
				context.put("supplierUserIcNo", getFacesScopes().getUserSessionBean().getIdentificationNo());
				context.put("supplierName", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				context.put("supplierMofRegNo", ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getRegNo());
				
				StringBuilder sb = new StringBuilder();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress1());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress2());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity() != null 
							? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity().getCityName() : "");
					sb.append(" ");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPostcode());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState() != null 
							? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState().getStateName() : "");
				}
				context.put("supplierAddress", sb.toString());
				context.put("supplierPhoneNo", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo() != null 
						? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo() : "");
				context.put("supplierFaxNo", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo() != null 
						? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo() : "");
				context.put("supplier",true);
						
				if (vmFile != null && context != null) {
					content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
				}
				getManagedBean().setEditorValue(content);
				changeAccountActionListener();
			} else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME) 
					|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)) {
				this.getManagedBean().setAction(res.getAction());
				this.getManagedBean().setSignOffDecision(true);
				getManagedBean().setPbQcApproved(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isPbQcApproved() ? "A" : "R");
			} else if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)) {
				this.getManagedBean().setBpkSaSkApprover(true);
			} 
	        
		    if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa() != null
		    		&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses() != null) {		    	  
		    	for (ScLoaWitness scLoaWitness : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses()) {
		    		if ((res.getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
		    				|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
		    				|| res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME))
		    				&& scLoaWitness.getPartyType().equalsIgnoreCase(ContractConst.WITNES_TYPE_AGENCY)) {
		    			getManagedBean().setScLoaWitness(scLoaWitness);
		    			break;
		    		}
		    		if (res.getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
		    				&& scLoaWitness.getPartyType().equalsIgnoreCase(ContractConst.WITNES_TYPE_SUPPLIER)) {
		    			getManagedBean().setScLoaWitness(scLoaWitness);
		    			break;
		    		}		    		
		    	}
		    }
		    
		    if (this.getManagedBean().isLetterOfAcceptance()) {
		    	getLoaDocumentList();
		    }
		    ConstructTabSideMenu();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			contractAmendmentSO = null;
		}
	}
	
	/**
	 * The method used to navigate to contract maintenance screen.
	 * 
	 * @return the navigate URL 
	 */
	public String backAction() {
		return CONTRACT_MAINT_PAGE;
	}
	
	/**
	 * The method used to set the contract amendment information while
	 * rendering the screen.
	 * 
	 * @param resDTO  the response data transfer object for contract
	 */
	public void setAmendmentContractVer(ContractResDTO resDTO) {		
		IContractAmendmentSO contractAmendmentSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO extResDTO = null;
		Date currentDate = DateUtils.getCurrentDateWithoutTime();

		try {
			ctBackingBean.getManagedBean().setContractVer(resDTO.getCtContractVer());	 
			//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();
			
		 	reqDTO = new ContractReqDTO();
		 	contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
		 			ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		 	reqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
	    	reqDTO.setContractId(resDTO.getCtContractVer().getCtContract().getContractId());
			reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			extResDTO = contractAmendmentSO.retrieveExtensionHistory(reqDTO);
			getManagedBean().setAmendHistoryList(extResDTO.getExtensionHistoryList());
			getManagedBean().setNewAmendmentEffDate(retrieveNewEffDate());
			// Initialize new amendment details
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment() == null) {
				ctBackingBean.getManagedBean().getContractVer().setCtAmendment(new CtAmendment());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setFileRefNo("");
				String contractName = ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractName();
				if (contractName != null && contractName.length() > 100) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setTitle(contractName.substring(0, 100));
				} else {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setTitle(contractName);
				}
				if (contractName != null && contractName.length() > 200) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setDescription(contractName.substring(0, 200));
				} else {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setDescription(contractName);
				}
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getNewAmendmentEffDate());
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null) {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount());	
				} else {
					ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(BigDecimal.ZERO);	
				}
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(0);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(null);
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(null);
			}
			
			if (getManagedBean().getTaskName() != null 
	     			&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				   for(ScLoaWitness witness : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses()){
					        if(witness.getPartyType().equalsIgnoreCase(ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER)){
					        	getManagedBean().setScLoaWitness(witness);
					        }
				   }
			}
	     
		  	getManagedBean().setSecretaryId(null);
		  	if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary() != null) {
		  		getManagedBean().setSecretaryId(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary().getUserId());
		  	}
		  	
		 
		  	// Retrieve agencies
		  	if (ctBackingBean.getManagedBean().getContractVer().getCtAgencies() != null 
		  			&& this.ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size() > 0 ) {
		  		ctBackingBean.getManagedBean().setSelectedAgency(null);
		     	sortAgencyList(ctBackingBean.getManagedBean().getContractVer().getCtAgencies());
		  		for (CtAgency ctAgency : ctBackingBean.getManagedBean().getContractVer().getCtAgencies()) {
		  			// If effective date is after current date, status is still display old status
		  			ctBackingBean.checkAgencyStatus(ctAgency);
		  			if (!retrieveMinistryOrgProfile(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId()).getOrgProfileId().equals(
							retrieveMinistryOrgProfile(ctAgency.getOrgProfile().getOrgProfileId()).getOrgProfileId())) {
						ctAgency.setAp178(true);
					} else {
						ctAgency.setAp178(false);
					}
		  		}
			}
		  	// Retrieve Items
		  	this.getManagedBean().getCtItemList().clear();
			this.getManagedBean().getCtServiceItemList().clear();
			getManagedBean().setItemProductExist(false);
			getManagedBean().setItemServiceExist(false);
			if (resDTO.getCtContractVer().getCtContractItems() != null 
					&& resDTO.getCtContractVer().getCtContractItems().size() > 0) {				
				for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
					// If effective date is after current date, status is still display old status
					if (item.getRecordStatusFlg() == 0) {				       		
						if (item.getEffDate().after(currentDate)) {
							item.setChkStatus(true);
						} else {
							item.setChkStatus(false);
						}
			      	} else {
			      		if (item.getEffDate().after(currentDate)) {
			      			item.setChkStatus(false);
			      		} else {
			      			item.setChkStatus(true);		        		   
			      		}
			      		
			      		if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {
			      			getManagedBean().setItemProductExist(true);
			      			Collections.sort(item.getCtItemPrices(), new CtItemPrice());
					    	if (item.getCtItemCeilingQties() != null && item.getCtItemCeilingQties().size() > 0 ) {					    		
					    		//Collections.sort(item.getCtItemCeilingQties(), new CtItemCeilingQty());
					    		sortItemCeilingQtyList(item.getCtItemCeilingQties());
					    	}
					    	Collections.sort(item.getCtItemPrices(), new CtItemPrice());
					    	getManagedBean().getCtItemList().add(item);
			      		} else if (item.getItemType().getParameterCode().equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
			      			getManagedBean().setItemServiceExist(true);
					    	Collections.sort(item.getCtItemPrices(), new CtItemPrice());
					    	getManagedBean().getCtServiceItemList().add(item);
				     	}
			    	}
					if (getManagedBean().getCtServiceItemList() != null 
							&& getManagedBean().getCtServiceItemList().size() > 0) {
			        	Collections.sort(this.getManagedBean().getCtServiceItemList(), new CtContractItem());
					}
					if (getManagedBean().getCtItemList() != null 
							&& getManagedBean().getCtItemList().size() > 0) {
						Collections.sort(getManagedBean().getCtItemList(), new CtContractItem());
					}
				}
			}
			// Retrieve Zone
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractZones() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtContractZones().size() > 0) {
				Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtContractZones(), new CtContractZone());
				ctBackingBean.retriveZonesByContractVer();
			}
			
			// Retrieve Deduction Clauses
		    if (ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0) {
		    	ctBackingBean.getManagedBean().setSelectedDeduction(null);
		    	for (CtDeduction deduct : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()) {
		    		if (deduct.getDeductionType().getParameterCode().equalsIgnoreCase(ContractConst.AP_DEDUCTION_TYPE)) {
		    			ctBackingBean.getManagedBean().setFlgPaymentTracking(true);
		    			ctBackingBean.getManagedBean().setAdvancePaymentAmt(deduct.getAmount());	    			
		    		}
		    	}
		    }
		    
		    // Retrieve Schedules
		    if (ctBackingBean.getManagedBean().getContractVer().getCtSchedules() != null 
		    		&& !ctBackingBean.getManagedBean().getContractVer().getCtSchedules().isEmpty()) {
		    	ctBackingBean.getManagedBean().setChkSchedule(true);
		    	Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtSchedules(), new CtSchedule());
		    	for (CtSchedule ctSchedule : ctBackingBean.getManagedBean().getContractVer().getCtSchedules()) {
		    		if (ctSchedule.isUseInFulfilment()) {
		    			ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
		    			break;
		    		}
		    	}
		    	ctBackingBean.scheduleDate();
		    }
		    
		    if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() != null 
		    		&& !ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty()) {
		    	Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments(), new CtProgressivePayment());
		    	for (CtProgressivePayment ctProgressivePayment : ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments()) {
		    		if (ctProgressivePayment.isUseInFulfilment()) {
		    			ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
		    			break;
		    		}
				}
		    	ctBackingBean.progressiveDate();
		    }
		    // Retrieve approver list
		    // Skip during supplier acknowledgment
		    if (getManagedBean().getTaskName() == null || !getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)){
			    getManagedBean().getApproverModel().setSource(
						getAvailableApproverList(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode(),
								getManagedBean().getUserMap()));
			    if (resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0) {
			    	getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
			    	for (PmUser approverUser : resDTO.getApproverList())  {
						Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();				
						while (sourceIt.hasNext()) {
							PmUser user = sourceIt.next();
							if (user.getUserId() == approverUser.getUserId()) {
								sourceIt.remove();
							}
						}
					}
				} 
		    }
			
		    initializeRemarks();
		    getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(
		    		ModuleCodeConst.CONTRACT_MANAGEMENT, ctBackingBean.getManagedBean().getContractVer().getDocType(), ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()));
		  
		    getManagedBean().setRemarkList(resDTO.getCtRemarks());
		    getManagedBean().setAttachmentList(resDTO.getCtAttachments());	
		    ctBackingBean.getManagedBean().setCtContractAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount());
		    if(ctBackingBean.getManagedBean().getCtContractAmount()!= null && ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() !=  null){
		    	ctBackingBean.getManagedBean().setGstTotalAmount(ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount().add(ctBackingBean.getManagedBean().getCtContractAmount().getContractAmount()));    
	    	}else{
	    		 ctBackingBean.getManagedBean().setGstTotalAmount(ctBackingBean.getManagedBean().getCtContractAmount().getContractAmount());
	  		}
		     ctBackingBean = new ContractBackingBean(this.getManagedBean().getFileDirectory());
		    ctBackingBean.getManagedBean().setViewContract(false);
		    ctBackingBean.getManagedBean().setUpdateCompanyName(false);
		    ctBackingBean.getManagedBean().setUpdateSupplierAddress(false);
		    ctBackingBean.getManagedBean().setSmAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress());
		    ctBackingBean.getManagedBean().setSupplierAddress(AddressUtils.getFullAddress(ctBackingBean.getManagedBean().getSmAddress()));
		    ctBackingBean.renderUpdateCheckbox(ctBackingBean.getManagedBean().getContractVer());
		    ctBackingBean.getManagedBean().setSupplierCompanyName(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
		    ctBackingBean.getManagedBean().setSupplierBranchName(this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierBranch());
		    ctBackingBean.retrieveBranchInfo();
		    ctBackingBean.initializeRemarkAndAttachement(ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList());
		    if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
		    	ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else {
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE)) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules()!= null  && !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtSchedules().isEmpty()) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.FREQENCY_SCHEDULE);
			}  else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments() != null && !ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScQtProgPayments().isEmpty()) {
				ctBackingBean.getManagedBean().setDisableScheduleRadio(true);
				ctBackingBean.getManagedBean().setFrequencySchedule(ContractConst.PROGRESSIVE_SCHEDULE);
			}
		    
			if (isContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
		    	if (ctBackingBean.getManagedBean().getCtContractAmount().getGstAmount() == null) {
		    		ctBackingBean.getManagedBean().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				}
		    	ctBackingBean.getManagedBean().setGstCheckingGUI(true);
		    } else {
		    	ctBackingBean.getManagedBean().setGstCheckingGUI(false);
		    }
			
		    // Amendment process	  
		    getManagedBean().setCommitteeType(retrieveCommitteeType());
		    ctBackingBean.getManagedBean().setDisableAmendDetails(false);
		    if (getManagedBean().getTaskName() != null
		    		&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME) 
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)
		    				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME))) {
		    	ctBackingBean.getManagedBean().setDisableAmendDetails(true);
		    }
		    if (getManagedBean().getTaskName() != null 
		    		&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
		    	getManagedBean().setDisableEndorseAmendDetails(true);
		    }
		    if (getManagedBean().getTaskName() != null 
		    		&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
		    	getManagedBean().setDisableFinalAmendDetails(true);
		    }
		    if (getManagedBean().getTaskName() != null 
		    		&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VIEW_FA_DETAILS_HT_NAME)) {
		    	getManagedBean().setDisableMeetingDetails(true);
		    	ctBackingBean.getManagedBean().setViewContract(true);
		    }
		    //Initiate the secretary
		    if (getManagedBean().getSecretaryId() == null && getManagedBean().getCommitteeType() != null
		    		&& !ctBackingBean.getManagedBean().isDisableAmendDetails()) {
		    	
		    	SelectItem[] secretaryList = getSecretaryDropdownList(); 
		    	// Default the secretary if selection list only have one secretary
		    	if (secretaryList != null && secretaryList.length == 2) {
		    		if (secretaryList != null && secretaryList.length == 2) {
			    		getManagedBean().setSecretaryId((Long)secretaryList[1].getValue());
			    	}
		    	}
		    }
	  	} finally {
	  		contractAmendmentSO = null;
			reqDTO = null;
			extResDTO =  null;
			resDTO = null;
		}
	}
	
	public void amendTabChangeAction(Integer selectedTabIndex) {
		getManagedBean().setSelectedTabIndex(selectedTabIndex);
	}
	
	/**
	 * The method used to save the contract amendment.
	 * This method will navigate to acknowledgment page with a 
	 * confirmation message once submit successfully. 
	 *
	 * @param  action  the user action on this contract
	 * @return the navigate URL 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public String saveContractAmendment(String action) throws Exception {
		IContractAmendmentSO contractAmendmentSO = null;
		ContractReqDTO reqDTO = null;
		
		try {
			chkComplyAP201Rule();
			setSecretary();
			if(!ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isPid()){
				ctBackingBean.getManagedBean().getContractVer().setCtPidReasons(null);
         	}
			validateContractAmendment(action);
			reqDTO = new ContractReqDTO();
			Object token = getFacesScopes().getBPMAuthenticationCtxToken();	
			reqDTO.setToken(token);
			reqDTO.setOutcome(action);
			reqDTO.setTaskId(getManagedBean().getTaskId());
	        	
			contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());
			       
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems().size() > 0) {
				ctBackingBean.getManagedBean().getContractVer().getCtContractItems().clear();
			}
			ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtItemList());
	     	ctBackingBean.getManagedBean().getContractVer().getCtContractItems().addAll(getManagedBean().getCtServiceItemList());
	     	ctBackingBean.getManagedBean().getContractVer().setDocType(ContractConst.DOC_TYPE_CONTRACT_AMENDMENT);
	              
	     	if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
	     			&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() == null) {
	     		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(
	     				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
	     	}
	     	if (getManagedBean().isPrepareLOA()) {
	     		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses() == null) {
	     			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaWitnesses(new ArrayList<ScLoaWitness>());
	     			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses().add(getManagedBean().getScLoaWitness());
	     		}
	     		int contractDuration = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() 
     					+ ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getContractPeriod();
     			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractDuration(new BigDecimal(contractDuration));
     			
     			getSupplierListForLoaAck();
     			reqDTO.setSupplierList(getManagedBean().getSupplierList());
     			
     			// Acknowledge Due Date
     			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
     							|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
     				Integer loaRespPeriod = getDaysByRules(RuleTypeConst.LOA_RESPONSE_PERIOD);
     				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setAckDueDate(DateUtils.addDays(DateUtils.getCurrentDateWithoutTime(), loaRespPeriod));
     			}
	     	}
	     	
	     	if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isComplyAP201()) {
	     		getBpkApproverList();
	     		reqDTO.setBpkApproverList(getManagedBean().getBpkApproverList());
	     	}
	     		
	     	if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)
	     			&& getManagedBean().getTaskName() != null 
	     			&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)
	     			&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses().size() == 1) {
	     		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaWitnesses().add(getManagedBean().getScLoaWitness());
	     	}
	     	
	     	if (isSupplierRole() && action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)
	     			&& !getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
     			// Generate loa document and set into sc_attachment
     			AttachmentFormat attachmentFormat = new AttachmentDateFormat();		
     			String fileName = null;
     			String filePath = null;
     			String content = null;
     			File file = new File(getManagedBean().getFileDirectory());
     			file.mkdirs();
     			List <ScLoaReport> scLoaReportList = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports();
		    	 
     			for (ScLoaReport scLoaReport : scLoaReportList) {
     				if (scLoaReport.getDefinationType().getParameterId().equals(
     						(getParameterByTypeAndCode(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
     								ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED).getParameterId()))) {
     					fileName = getTemplateFileName(scLoaReport.getPmDocument().getDocDefinitionKey());
    					filePath = getManagedBean().getFileDirectory() + attachmentFormat.format(fileName, null);
    					if (scLoaReport.getTitle().equalsIgnoreCase(ContractConst.TEMPLATE_JADUAL_HARGA)) {
    						formJadualHargaExcel(filePath);
    					} else {
    						String docDefinitionKey = scLoaReport.getPmDocument().getDocDefinitionKey();
    						content = getContent(docDefinitionKey);					
    						DocumentGenerator.getInstance().generatePdf(new FileOutputStream(filePath), content);
    					}
    					
    					ScLoaReportAttachment attachment = new ScLoaReportAttachment();
    				    attachment.setScLoaReport(scLoaReport);			    
    				    attachment.setFileName(fileName);
    				    attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
    				    attachment.setFileDesc(fileName);
    					
    				    scLoaReport.setScLoaReportAttachment(attachment);
     				}
     			}
	     	}
	     	// Retrieve the proper committee role during sign off process and set the sign off flag to true
	     	if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)
	     			&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
	     		List<CtCommitteeMember> committeeMemberList = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers();
	    		String role = null;
	    		for(CtCommitteeMember committeeMember : committeeMemberList) {
	    			if (committeeMember.getPmUser().getUserId().equals(getFacesScopes().getUserSessionBean().getUserId())) {
	    				committeeMember.setSignedOff(true);
	    				if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
	    			   			|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) { 
	    					role = "FPB_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
	    				} else {
	    					role = getManagedBean().getCommitteeType() + "_" + MemberRole.getDisplayName(committeeMember.getMemberRole().getParameterCode());
	    				}
	    				reqDTO.setUserRole(role);
	    				break;
	    			}
	    		}	     		
	     	} else {
	     		reqDTO.setUserRole(getUserRole());
	     	}
	     	reqDTO.setTaskName(getManagedBean().getTaskName());
	     	if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
	     		ctBackingBean.scheduleProgressiveDate();
         	}
	     	reqDTO.setCtContract(ctBackingBean.getManagedBean().getContractVer().getCtContract());
	     	reqDTO.setCtContractVer(ctBackingBean.getManagedBean().getContractVer());
	     	reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
	     	reqDTO.setAttachments(getManagedBean().getAttachmentList());
	     	reqDTO.setUserMap(getManagedBean().getUserMap());
	     	reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());
		           
			if (getManagedBean().getPmDigiSign() != null) {
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}
			ContractResDTO resDTO = contractAmendmentSO.saveContractAmendmentVerDtls(reqDTO);
			if (resDTO.isSuccessful()) {
				log.info("Record saved successfully");
				String messageId = MessageConst.INFO_700005;
				if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) 
						&& ContractConst.DOC_TYPE_CONTRACT_AMENDMENT.equals(ctBackingBean.getManagedBean().getContractVer().getDocType())) {
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)) {
						messageId = MessageConst.INFO_700060; 
					} 
					else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
						messageId = MessageConst.INFO_700061; 
					} else {
						if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()) {
							messageId = MessageConst.INFO_700051; 
						} else {
							messageId = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isComplyAP201() 
									? MessageConst.INFO_700051 : MessageConst.INFO_700050;
					
						}
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
					messageId = MessageConst.INFO_700008;
				} else if( action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE)) {
					messageId = MessageConst.INFO_700011;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACCEPT)) {
					messageId = MessageConst.INFO_700013;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && getManagedBean().getTaskName() != null) {
					messageId = MessageConst.INFO_700026;
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)) {
						messageId = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isApprovalReq()
								? MessageConst.INFO_700046 : MessageConst.INFO_700043;
					}
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_COMPLETE)) {
					messageId = MessageConst.INFO_700001;
				} else if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(messageId,
								new String[] {getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, this.ctBackingBean.getManagedBean().getContractVer().getDocType()), 
									this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo()},
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) 
						|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId() == null)) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} else {
					ContractReqDTO ctReqDTO = new ContractReqDTO();
					ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
			    
					ContractResDTO ctResDTO = contractAmendmentSO.retrieveContractAmendVerDtls(ctReqDTO);
					setAmendmentContractVer(ctResDTO);
					ConstructTabSideMenu();
				}
			} else {
				throw new EpException("Failed.");
			}
		} finally {
			contractAmendmentSO = null;
		}
		return null;		
	}
	   
	/**
	 * The method used for validating the contract amendment information.
	 * A warning or error message will display on screen when the validation
	 * is failed. User required to modify the information accordingly
	 * to proceed.
	 *
	 * @param  outcome  the user action on this contract. The validation 
	 * 					will invoke base on the action
	 */
	public void validateContractAmendment(String action) throws EpException {
		
		   getManagedBean().setAutoUpdate(true);	
	       // Validation for remark
		   if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)||
			    	(isBpkApprover() && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME))) {
			           String remark = getManagedBean().getRemarkList().get(0).getRemark();
			           String itemRemark = ctBackingBean.getManagedBean().getContractVer().getItemRemarks().get(0).getRemark();
			           String ap201Remark = ctBackingBean.getManagedBean().getContractVer().getAp201Remark().get(0).getRemark();
			           String supplierRemark = ctBackingBean.getManagedBean().getContractVer().getSupplierRemarks().get(0).getRemark();
		
			           if ((remark == null || remark.length() == 0) && (itemRemark == null || itemRemark.length() == 0) 
					        && (ap201Remark == null || ap201Remark.length() == 0) && (supplierRemark == null || supplierRemark.length() == 0)) {
				              RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				              throw new EpException(MessageConst.ERROR_100022, 
						         new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
								    ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			           }
		     }/*Remark validation end*/
		   
		 //Validation for PID
			if(!action.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && 
					ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isPid() && 
					(ctBackingBean.getManagedBean().getContractVer().getCtPidReasons() == null || ctBackingBean.getManagedBean().getContractVer().getCtPidReasons().size() == 0)) {
				  throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			
		// Validation for PID Reason
    		if(ctBackingBean.getManagedBean().getContractVer().getCtPidReasons() != null && ctBackingBean.getManagedBean().getContractVer().getCtPidReasons().size() > 0 ){
    			for(CtPidReason reason : ctBackingBean.getManagedBean().getContractVer().getCtPidReasons()){
    			   if(reason.getPmReason() == null || reason.getPmReason().getReasonId() == null){
    				   throw new EpException(MessageConst.ERROR_100022, 
    							new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason", 
    									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    			   }
    			   if(ctBackingBean.PidPurchaseTypeOther(reason.getPmReason().getReasonId())){
    				   if(reason.getReasonDesc().isEmpty()){
    					   throw new EpException(MessageConst.ERROR_100022, 
    								new String[]{ResourceBundleUtils.getMessage("lbl.ct.pid.reason.desc", 
    										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    				   }
    			   }
    			}
    		}
	
		     /*validation for other  selection*/
	         if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
	        	 // Validation for mandatory attachments
	        	 if(getManagedBean().getAttachmentList() == null || getManagedBean().getAttachmentList().isEmpty()) {
	                     throw new EpException(MessageConst.ERROR_750033);
                 }
	        	 
	     		// Validation for Deduction
	     		if(ctBackingBean.getManagedBean().getContractVer().getCtDeductions() != null && ctBackingBean.getManagedBean().getContractVer().getCtDeductions().size() > 0){
	     			  for(CtDeduction deduction : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()){
	     				   if(deduction.getDeductionType() == null ||deduction.getDeductionType().getParameterId() == null ){
	     					   throw new EpException(MessageConst.ERROR_100022, 
	     								new String[]{ResourceBundleUtils.getMessage("lbl.ct.deduction.type", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     				   }
	     			  }
	     		}
	     		// Validation for zone
	     		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
	     			ctBackingBean.getZoneObjDetails();
	     			if (ctBackingBean.getManagedBean().getContractVer().getCtContractZones() != null) {
	     				for (CtContractZone contractZone : ctBackingBean.getManagedBean().getContractVer().getCtContractZones()) {
	     					if (contractZone.getDeliveryTerm() == 0) {
	     						throw new EpException(MessageConst.ERROR_750004, new String[]{contractZone.getZoneName()});
	     					}
	     				}
	     			}
	     		}		
	     		// Validation for agency
	     		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY) 
	     				&& ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size() == 0 && action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
	     			throw new EpException(MessageConst.ERROR_750006); // At least one row should be in Agency section.
	     		}
	     		// Validation for schedule
	     		if(ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
	     			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
	     				ctBackingBean.getManagedBean().getContractVer().setCtProgressivePayments(null);
	     			} else {
	     				ctBackingBean.getManagedBean().getContractVer().setCtSchedules(null);  	
	     			}
	     			List<String> schedulePhaseNoList = new ArrayList<String>();
	     			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
	     				if (ctBackingBean.getManagedBean().getContractVer().getCtSchedules() == null || ctBackingBean.getManagedBean().getContractVer().getCtSchedules().size() < 2 
	     						&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
	     					throw new EpException(MessageConst.ERROR_750022);
	     		     	}
	     				for (CtSchedule schedules : ctBackingBean.getManagedBean().getContractVer().getCtSchedules()) {
	     					if (schedules.getScheduleNo() == null) {
	     						throw new EpException(MessageConst.ERROR_100022,
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     					if (schedulePhaseNoList.contains(schedules.getScheduleNo().toString())) {
	     						throw new EpException(MessageConst.ERROR_100046, 
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.no", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					} else {
	     						schedulePhaseNoList.add(schedules.getScheduleNo().toString());
	     					}
	     					if (schedules.getStartMonth() == null) {
	     						throw new EpException(MessageConst.ERROR_100022,
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     					if (schedules.getEndMonth() == null) {
	     						throw new EpException(MessageConst.ERROR_100022,
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.frequency.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) 
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     		        	}
	     					
	     					if (schedules.getScheduleType() == null || schedules.getScheduleType().getParameterId() == null) {
	     						throw new EpException(MessageConst.ERROR_100022, 
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.schedule.type", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     				}     	
	     			} else if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() == null || ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().size() < 2 
	     					&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()
	     					&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) || action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
	     				throw new EpException(MessageConst.ERROR_750022);
	     			}
	     			if (ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments() != null && !ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments().isEmpty() 
	     					&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().isFrequencySchedule()) {
	     				float percentage = 0.0f;
	     				for (CtProgressivePayment progressivePayment : ctBackingBean.getManagedBean().getContractVer().getCtProgressivePayments()) {
	     					percentage = percentage + progressivePayment.getPercentage();
	     					if (progressivePayment.getPhaseNo()== null) {
	     						throw new EpException(MessageConst.ERROR_100022,
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.phase", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     					if (schedulePhaseNoList.contains(progressivePayment.getPhaseNo().toString())) {
	     	           			throw new EpException(MessageConst.ERROR_100046, 
	     	           					new String[] {ResourceBundleUtils.getMessage("lbl.ct.phase", 
	     	           							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					} else {
	     						schedulePhaseNoList.add(progressivePayment.getPhaseNo().toString());
	     					}
	     					if (progressivePayment.getPhaseDesc() == null ||progressivePayment.getPhaseDesc() == "" ) {
	     						throw new EpException(MessageConst.ERROR_100022,
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.description", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     					if (progressivePayment.getStartMonth() == null) {
	     						throw new EpException(MessageConst.ERROR_100022, 
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.start.period", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}
	     					if (progressivePayment.getEndMonth() == null) {
	     						throw new EpException(MessageConst.ERROR_100022, 
	     								new String[] {ResourceBundleUtils.getMessage("lbl.ct.progressive.schedule", 
	     										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())
	     										+ " " + ResourceBundleUtils.getMessage("lbl.ct.end.period", 
	     												ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     					}     	
	     				}
	     				int value = Float.compare(percentage,100);
	     				if (value != 0 && (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
	     						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
	     					throw new EpException(MessageConst.ERROR_100022, 
	                					new String[] {ResourceBundleUtils.getMessage("lbl.ct.percentage.progressive", 
	                							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     				}
	     			}
	     		}
	     		// Validation for AP178 agency
	     		if (ctBackingBean.getManagedBean().getContractVer().getCtAgencies().size()>0) {
	     			double ceilingAmt = 0.0;
	     			DecimalFormat df = new DecimalFormat("#.00");
	     			for (CtAgency ctAgency : ctBackingBean.getManagedBean().getContractVer().getCtAgencies()) {
	     				ceilingAmt = ceilingAmt + ctAgency.getCeilingAmount().doubleValue();
	     				if (ctAgency.isAp178() == true && (ctAgency.getFileName() == null  || ctAgency.getFileName().equalsIgnoreCase(""))) {
	     					throw new EpException(MessageConst.ERROR_750008); 
	     					// Approval document is mandatory for AP178 agency. Please upload the document.
	     				}
	     				
	     				if (ctAgency.isAp178() == true && (ctAgency.getRefNo() == null  || ctAgency.getRefNo().equalsIgnoreCase(""))) {
	     					throw new EpException(MessageConst.ERROR_100022, 
	                					new String[] {ResourceBundleUtils.getMessage("lbl.ct.file.ref.no", 
	                							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     				}
	     			}
	     			ceilingAmt = Double.valueOf(df.format(ceilingAmt));
	     			
	     			double latestContractAmount = 0.0;
	     			for (CtContractAmount contractAmt : ctBackingBean.getManagedBean().getContractVer().getCtContractAmounts()){
	     	    		if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() >= contractAmt.getEffDate().getTime() 
	     	    				&& ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime() <= contractAmt.getExpDate().getTime()){
	     	    			latestContractAmount = contractAmt.getContractAmount().doubleValue();
	     	    			latestContractAmount = Double.valueOf(df.format(latestContractAmount));
	     	    			break;
	     	    		}
	     	    	}
	     			if (ceilingAmt >  latestContractAmount) {
	     				CommonUtils commonUtils = new CommonUtils();
	     				DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(
	     						CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
	     				throw new EpException(MessageConst.ERROR_750007, 
	     						new String[] {nf.format(latestContractAmount)}); // Total ceiling value exceeded contract value. Please check your ceiling amount.
	     			}
	     		}			
	     		// Validation for ceiling quantity
	     		if (ctBackingBean.getManagedBean().getContractVer().getCtContractItems() != null) {
	     			for (CtContractItem item : ctBackingBean.getManagedBean().getContractVer().getCtContractItems()) {
	     				double totalCeilingQuantity = 0.0;
	     				if (item.getCtItemMaxQties().size() != 0) {						
	     					for (CtItemCeilingQty ctItemCeilingQty : item.getCtItemCeilingQties()) {
	     						totalCeilingQuantity = totalCeilingQuantity + ctItemCeilingQty.getCeilingQuantity().doubleValue();
	     					}
	     					if (item.getTotalMaxQuantity().doubleValue() < totalCeilingQuantity) {
	     						throw new EpException(MessageConst.ERROR_750009, new String[]{item.getScRequestItem().getItemName()});
	     					}
	     				}
	     			}
	     		}
	     		// Validation for AP201 exempt
	     		if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
	     				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
	     				&& (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getAp201FileName() == null 
	     				||ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getAp201FileName().equalsIgnoreCase("")) 
	     				&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
	     			throw new EpException(MessageConst.ERROR_100022, 
	        					new String[] {ResourceBundleUtils.getMessage("lbl.ct.attachment.ap201", 
	        							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	     		} 
	         }
	    
		     /* Validation mandatory for supporting document of AP201 special approver */
		      if(isBpkApprover() && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME) &&
				     action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE) && ctBackingBean.getManagedBean().getContractVer().getAp201Attachements().isEmpty()){
				        throw new EpException(MessageConst.ERROR_750018, new String[]{ResourceBundleUtils.getMessage("lbl.ct.menu.special.ap201", 
					    	ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		       }
		
		      if (!action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT) && getManagedBean().getAckFA() != null
				  && getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)
				  && ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() == null){
			        throw new EpException(MessageConst.ERROR_100022, 
					    new String[]{ResourceBundleUtils.getMessage("lbl.ct.bank.account.name", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		       }
	
		// Validation for amendment type
		if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()
				&&  !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.amendment.type", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		// Validation for reference no
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo() == null 
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo() == "") {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.file.ref.no", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		// Validation for amendment title
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle() == null
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle() == "") {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.amendment.title", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		// Validation for amendment description
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getDescription() == null
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getDescription() == "") {
			RequestContext.getCurrentInstance().addCallbackParam("epException",true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.amendment.desc", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		// Validation for amendment effective date
		if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() != null
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate().getTime() > ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate().getTime()) {
			 throw new EpException(MessageConst.ERROR_750035);
		}
		// Validation for contract signer
		if ((!action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)
				&& getManagedBean().getAckFA() != null 
				&& getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA))
			|| (getManagedBean().getTaskName()!=null 
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)))) {
			if	(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner() == null 
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner().getUserId() == null) {
				RequestContext.getCurrentInstance().addCallbackParam("epException",true);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.loa.signer", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
			if (getManagedBean().getScLoaWitness() != null) {
				if (getManagedBean().getScLoaWitness().getIdentificationNo() == null 
						|| getManagedBean().getScLoaWitness().getIdentificationNo().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.ic.no", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getScLoaWitness().getWitnessName() == null 
						|| getManagedBean().getScLoaWitness().getWitnessName().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.full.name", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getScLoaWitness().getDesignation() == null 
						|| getManagedBean().getScLoaWitness().getDesignation().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.designation", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getScLoaWitness().getAddress() == null 
						|| getManagedBean().getScLoaWitness().getAddress().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.address", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getScLoaWitness().getPhoneNo() == null 
						|| getManagedBean().getScLoaWitness().getPhoneNo().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.tel", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (getManagedBean().getScLoaWitness().getEmail() == null 
						|| getManagedBean().getScLoaWitness().getEmail().length() == 0) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.email", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}			
			// Validation for Upload LOA content
			List <ScLoaReport> scLoaReportList = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports();
			for (ScLoaReport scLoaReport : scLoaReportList) {
				if (scLoaReport.getDefinationType().getParameterId().equals(
						(getParameterByTypeAndCode(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD).getParameterId()))) {						
					if (scLoaReport.getTitle() == null || scLoaReport.getTitle() == "") {
						RequestContext.getCurrentInstance().addCallbackParam("epException",true);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.loa.content.file.name", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}					
					if (scLoaReport.getScLoaReportAttachment() != null
							&& (scLoaReport.getScLoaReportAttachment().getFilePath() == null || scLoaReport.getScLoaReportAttachment().getFilePath() == "")) {
						RequestContext.getCurrentInstance().addCallbackParam("epException",true);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.loa.content.upload.file", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
		}
		// Validation for term and condition
		if (getManagedBean().getTaskName() != null 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
			if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_ACKNOWLEDGE) && !getManagedBean().isTermAndConditionFlg()) {
				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
				throw new EpException(MessageConst.ERROR_750002);
			}
		}
		
		if ((getManagedBean().getTaskName() != null 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME))
				&& action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isComplyAP201()
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {
				//Validation for committee
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary() == null) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.secretary.committee", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			} 
			if (!ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isComplyAP201()) {
				// Validation for valid bpk approver for special approver task assignment
				getBpkApproverList();
				if (getManagedBean().getBpkApproverList() == null || getManagedBean().getBpkApproverList().isEmpty()) {
					RequestContext.getCurrentInstance().addCallbackParam("epException",true);
					throw new EpException(MessageConst.ERROR_750038);
				}					
			}
		}
		// Check all the member role must be selected
		if (getManagedBean().getTaskName() != null 
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME) 
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
			validateCommiteeMemberDetails();
		}
		// Validation for meeting and committee
		if (getManagedBean().getTaskName() != null
				&& (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
						|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME))) {
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().size() == 0) {
				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
				throw new EpException(MessageConst.ERROR_100023, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.menu.meeting",
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else {
				int counter = 0;
				for (CtMeeting ctMeeting : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings()) {
					if (ctMeeting.getMeetingDate() == null) {
						RequestContext.getCurrentInstance().addCallbackParam("epException", true);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.date.of.meeting", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					} 
					if (ctMeeting.getMeetingLocation() == null || ctMeeting.getMeetingLocation().isEmpty()) {
						RequestContext.getCurrentInstance().addCallbackParam("epException", true);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.location.of.meeting", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					} 
					if (ctMeeting.getMeetingStatusId() == null) {
						RequestContext.getCurrentInstance().addCallbackParam("epException", true);
						throw new EpException(MessageConst.ERROR_100022, 
								new String[]{ResourceBundleUtils.getMessage("lbl.ct.meeting.status", 
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
						if (ctMeeting.getMeetingStatusId().equals(ContractStatusConst.FA_MEETING_PENDING)) {
							RequestContext.getCurrentInstance().addCallbackParam("epException", true);
							throw new EpException(MessageConst.ERROR_750027);
		    			}
					}
					if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
						if (ctMeeting.getMeetingStatusId().equals(ContractStatusConst.FA_MEETING_APPROVE)) {
							counter++;
						}
					}
				}
				if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
					if (counter == 0) {
						RequestContext.getCurrentInstance().addCallbackParam("epException", true);
						throw new EpException(MessageConst.ERROR_750028);
					}
				}
		    	                	     
			}
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
				if (getManagedBean().getPbQcApproved() == null || getManagedBean().getPbQcApproved().equalsIgnoreCase("")) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.decision", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbqcJustification() == null
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getPbqcJustification().equalsIgnoreCase("")) {
					RequestContext.getCurrentInstance().addCallbackParam("epException", true);
					throw new EpException(MessageConst.ERROR_100022, 
							new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
				int chairPersonCount = 0;
				Long chairPersonId = getParameterByTypeAndCode(ParameterTypeConst.COMMITTEE_ROLE, ParameterConst.COMMITTEE_ROLE_CHAIRPERSON).getParameterId();
				for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
					if (committeMember.getMemberRole().getParameterId().equals(chairPersonId)) {
						chairPersonCount++;
						if (chairPersonCount > 1) {
							throw new EpException(MessageConst.ERROR_750026);
						}
					}
				}
			}
		}
		// Validation for committee member
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && 
				getManagedBean().isConfirmMeeting() && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
			validateMeetingDetails();
		}
		// Validation for approver
		if (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
				&& getManagedBean().getApproverModel().getTarget().size() == 0) {
			RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
		// Validation for valid supplier for acknowledge loa task assignment
		if (getManagedBean().isPrepareLOA() 
				&& (action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) 
						|| action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))) {
			getSupplierListForLoaAck();
	    	if (getManagedBean().getSupplierList() == null 
	    			|| getManagedBean().getSupplierList().isEmpty()) {
	    		throw new EpException(MessageConst.ERROR_750039);
	    	}
		}
				
		if ((action.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) 
				|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE))
				|| (action.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT))) {
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ctBackingBean.getManagedBean().getContractVer().getDocType());
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate());
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount());
			sb.append(action);			
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(action);
		} 
	}
	
	/**
	 * The method used to authenticate user during submit the contract amendment.
	 * This method will invoke a confirmation dialog for user authentication.
	 * This method will call save contract amendment method once authentication success.
	 * 
	 * @param  outcome  the user action on this contract
	 * @return result of the saveContractAmendment process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String gpkiSignAction(String outcome) throws Exception {
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome)) {
			chkComplyAP201Rule();
    	    setSecretary();
			validateContractAmendment(outcome);
			// Temp setTextBeforeSign : DocNo+Type
			StringBuilder sb = new StringBuilder();
			sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(outcome);
			getManagedBean().setTextBeforeSign(sb.toString());
			// set action
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_ACKNOWLEDGE.equals(outcome) && isSupplierRole()
				&& !getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
			validateContractAmendment(outcome);	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);			
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
			return null;
		}
		else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		} else {
			return saveContractAmendment(outcome);
		}
	}
	
	/**
	 * The method used to set value for security check
	 * before save the contract amendment.
	 * 
	 * @param  pmDigiSign  an object of digital sign
	 * @return result of the saveContractAmendment process
	 * @throws Exception   if an exception occurred
	 * @see    Exception
     */
	public String preSubmitContractAmendment(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null && isSupplierRole()) {
			FacesScopes facesScope = new FacesScopes();
			Long userId = facesScope.getUserSessionBean().getUserId();
			Timestamp date = new Timestamp(System.currentTimeMillis());
			String originalText = prepareSupplierOriginalText();
			pmDigiSign.setCreatedBy(userId);
		    pmDigiSign.setCreatedDate(date);
		    pmDigiSign.setOriginalData(originalText);
		    pmDigiSign.setSpkiSignedData(getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText));
			getManagedBean().setPmDigiSign(pmDigiSign);			
		} else if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);
		}
		return saveContractAmendment(getManagedBean().getButtonAction());
	}
	
	/**
	 * The method used to retrieve the supplier information for security check.
	 * 
	 * @return orginalText the supplier information
	 */
    private String prepareSupplierOriginalText() {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		SmSupplier smSupplier = null;
		String countryCode ="";
		String orginalText = "";

		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier() != null) {
			smSupplier = ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier();
			if (smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses() != null 
					&& smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().size() > 0) {
				List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl().getSmCompanyBasic().getSmCompanyAddresses();

				for (SmCompanyAddress compaAddress : companyAddress) {
					if (compaAddress.getAddressType().equalsIgnoreCase(AddressType.CONTACT_ADDRESS.value())) {
						countryCode = compaAddress.getSmAddress().getPmCountry().getCountryCode();
						break;
					}
				}
			}
		}		
		sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo())
			.append("|")
			.append(facesScopes.getUserSessionBean().getIdentificationNo())
			.append("|")
			.append(smSupplier.getRegNo())
			.append("|")
			.append(countryCode);
		orginalText  = sb.toString();
		return orginalText;
	}
	
	/**
	 * The method used to construct the tab menu for contract amendment.
	 */
	public void ConstructTabSideMenu() {
		AmendTabSideMenus sideMenu = new AmendTabSideMenus();
		getManagedBean().setFinalListOfSideMenu(new ArrayList<String>());
		getManagedBean().setConfirmMeeting(false);
	
		if (getManagedBean().getAckFA() == null) {
			getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendFulfilment);
		
			if (ctBackingBean.getManagedBean().getContractVer() != null 
					&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment() != null
					&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {
				   
				           if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
					            getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithZone);
				          
				           } if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
								getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAgency);
						   }
				   getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithItem);
			  }
			
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuDeduction);
			}
			if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange() && 
					ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuSchedule);
			}
		
			if (getManagedBean().isBpkSaSkApprover()) { 
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithSpAprlAP201);
			}
			if (getManagedBean().isPrepareLOA()) {
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareLoa);					 
				if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
								ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE))) {
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithPrepareBond);
				}
				if (getManagedBean().isDecisionPbQcLOA()) {
					getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);	    
				}
			}
			if (getManagedBean().isPbConfirmMeeting()) {
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithMeetings);
			}
			if (getManagedBean().isSignUpdateDecision()) {
			 	getManagedBean().setConfirmMeeting(true);
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			}
			if (getManagedBean().isSignOffDecision()) {
				getManagedBean().setConfirmMeeting(true);
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithDecision);
			}
			if (isApproverSetting() && !getManagedBean().isPrepareLOA()) {
				getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendSetting);
			}
		} else if (getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)) {
			getManagedBean().getFinalListOfSideMenu().addAll(sideMenu.menuWithAmendSupplier);
		}
	}
	
	/**
	 * The method used for controlling the tab menu
	 * of contract amendment dynamically.
	 * 
	 * @param  currentTab  current selected tab index
	 * @param  outputTab   current selected tab constant
     */
	public void setTabValue(int currentValue, String outputValue) {
		getManagedBean().setConfirmMeeting(false);
		if (getManagedBean().getAckFA() == null) {
			int index = 1;
			if (outputValue.equalsIgnoreCase("FD")) {
				getManagedBean().setSelectedTabIndex(0);
			}
			if (outputValue.equalsIgnoreCase("AD")) {
				getManagedBean().setSelectedTabIndex(1);
			}
			
			if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
			
			if(ctBackingBean.getManagedBean().getContractVer() != null && ctBackingBean.getManagedBean().getContractVer().getCtAmendment() != null
					&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()
							|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange())) {
				index = 2;
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
		        	index = 3;
		        	if (outputValue.equalsIgnoreCase("ZO")) {
		 	       		getManagedBean().setSelectedTabIndex(2);
		        	}
		        	if (outputValue.equalsIgnoreCase("IT")) {
		        		getManagedBean().setSelectedTabIndex(3);
		        	}
		 	  	} else if(outputValue.equalsIgnoreCase("IT")) {
		 	  		getManagedBean().setSelectedTabIndex(2);
		 	  	}
			}
			if (getManagedBean().isBpkSaSkApprover() && outputValue.equalsIgnoreCase("BPK")) { 
				getManagedBean().setSelectedTabIndex(index + 1);
		   	}
		    if (getManagedBean().isPrepareLOA()) {
		    	if (outputValue.equalsIgnoreCase("LOD")) {
		    		getManagedBean().setSelectedTabIndex(index + 1);
		    	}
		    	if (outputValue.equalsIgnoreCase("CNT")) {
		    		getManagedBean().setSelectedTabIndex(index + 2);
		    	}
		    	if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq() && outputValue.equalsIgnoreCase("PB")
		    			&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
		    			&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
		    					ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE))) {
		    		getManagedBean().setSelectedTabIndex(index + 3);
		    	}
		    	if (getManagedBean().isDecisionPbQcLOA() && outputValue.equalsIgnoreCase("DCN")) {
		    		if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq()
		    				&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
		    				&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
		    						ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE))) {
		    			getManagedBean().setSelectedTabIndex(index + 4);
		    		} else {
		    			getManagedBean().setSelectedTabIndex(index+3);
		    		}
		    	}
		    }
		    if (getManagedBean().isPbConfirmMeeting() && outputValue.equalsIgnoreCase("MET")) {
		    	getManagedBean().setSelectedTabIndex(index + 1);
			}
		    if ((getManagedBean().isSignUpdateDecision() || getManagedBean().isSignOffDecision()) 
		    		&& outputValue.equalsIgnoreCase("DCN")) {
		    	getManagedBean().setConfirmMeeting(true);
		    	if (getManagedBean().isPbConfirmMeeting()) {
		    		getManagedBean().setSelectedTabIndex(index + 2);
		    	} else {
		    		getManagedBean().setSelectedTabIndex(index + 1);
		    	}
		  	}
		    if (isApproverSetting() && !getManagedBean().isPrepareLOA() && outputValue.equalsIgnoreCase("SE")) {
		    	getManagedBean().setSelectedTabIndex(index + 1);
		      }
	    	} 
	  /*Menu for other started*/
		 else if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()){
			   if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
				   if (outputValue.equalsIgnoreCase("ZO")) {
	 	       	     	getManagedBean().setSelectedTabIndex(2);
	           	   } else if (outputValue.equalsIgnoreCase("AG")) {
	 	       		   getManagedBean().setSelectedTabIndex(3);
	        	   } else if (outputValue.equalsIgnoreCase("IT")) {
		        		getManagedBean().setSelectedTabIndex(4);
		           }
		 	  	} else {
		 	  		if (outputValue.equalsIgnoreCase("AG")) {
		 	       		   getManagedBean().setSelectedTabIndex(2);
		 	  		}else if(outputValue.equalsIgnoreCase("IT")) {
		 	  		     getManagedBean().setSelectedTabIndex(3);
		 	  	    }
		 	  	}
			   if (outputValue.equalsIgnoreCase(ContractConst.DEDUCTION_DETAIL)) {
		  		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {
		  			getManagedBean().setSelectedTabIndex(5);
		  		} else {
		  			getManagedBean().setSelectedTabIndex(4);
		      	}
		  	} 
		  	if (outputValue.equalsIgnoreCase(ContractConst.SCHEDULE_DETAIL) 
		  		 && ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
		  			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
		  		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()) {  
		  			     getManagedBean().setSelectedTabIndex(6);
		  			} else {
		  				getManagedBean().setSelectedTabIndex(5);
		  			}
		  	  	} 
		  	if (ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing()){
		  		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
			  			ParameterConst.FULFILMENT_TYPE_SCHEDULED)){
		  			index = 6;
		  	   } else {
		  		 index = 5;
		  	   }
		  	} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType().getParameterCode().equalsIgnoreCase(
		  			ParameterConst.FULFILMENT_TYPE_SCHEDULED)) {
		  		index = 5;
		  	} else {
		  		index = 4;
		  	}
		  	if (getManagedBean().isBpkSaSkApprover() && outputValue.equalsIgnoreCase("BPK")) { 
					getManagedBean().setSelectedTabIndex(index + 1);
			   	}
			    if (getManagedBean().isPrepareLOA()) {
			    	if (outputValue.equalsIgnoreCase("LOD")) {
			    		getManagedBean().setSelectedTabIndex(index + 1);
			    	}
			    	if (outputValue.equalsIgnoreCase("CNT")) {
			    		getManagedBean().setSelectedTabIndex(index + 2);
			    	}
			    	if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq() && outputValue.equalsIgnoreCase("PB")
			    			&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
			    			&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
			    					ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE))) {
			    		getManagedBean().setSelectedTabIndex(index + 3);
			    	}
			    	if (getManagedBean().isDecisionPbQcLOA() && outputValue.equalsIgnoreCase("DCN")) {
			    		if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq()
			    				&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()
			    				&& !ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
			    						ParameterConst.SC_PROCUREMENT_MODE_DIRECT_PURCHASE))) {
			    			getManagedBean().setSelectedTabIndex(index + 4);
			    		} else {
			    			getManagedBean().setSelectedTabIndex(index+3);
			    		}
			    	}
			    }
			    if (getManagedBean().isPbConfirmMeeting() && outputValue.equalsIgnoreCase("MET")) {
			    	getManagedBean().setSelectedTabIndex(index + 1);
				}
			    if ((getManagedBean().isSignUpdateDecision() || getManagedBean().isSignOffDecision()) 
			    		&& outputValue.equalsIgnoreCase("DCN")) {
			    	getManagedBean().setConfirmMeeting(true);
			    	if (getManagedBean().isPbConfirmMeeting()) {
			    		getManagedBean().setSelectedTabIndex(index + 2);
			    	} else {
			    		getManagedBean().setSelectedTabIndex(index + 1);
			    	}
			  	}
			  if (isApproverSetting() && !getManagedBean().isPrepareLOA() && outputValue.equalsIgnoreCase("SE")) {
			    	getManagedBean().setSelectedTabIndex(index + 1);
			    }
		  } 
		}else if(getManagedBean().getAckFA().equalsIgnoreCase(ContractConst.SUPPLIER_ACK_LOA)){
			if (outputValue.equalsIgnoreCase("LOD")) {
				getManagedBean().setSelectedTabIndex(0);
			}
			if (outputValue.equalsIgnoreCase("CNT")) {
				getManagedBean().setSelectedTabIndex(1);
			}
			if (outputValue.equalsIgnoreCase("SLOA")) {
				getManagedBean().setSelectedTabIndex(2);
			}
			if (outputValue.equalsIgnoreCase("SDEC")) {
				getManagedBean().setSelectedTabIndex(3);
			}		 
		}
	}
	
	/**
	 * The method used for retrieving the contract version 
	 * for agreement or non-agreement contract.
	 * If it's original agreement, return the first contract version.
	 * If it's supplementary agreement, return the amended contract version.
     */
	private void retrieveContractVerOfAgreement() {
		IContractSO contractSO = null;
		IContractAmendmentSO amendmentSO = null;
		ContractResDTO resDTO = null;
		ContractAmendmentResDTO amendmentResDTO = null;
		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
			ContractReqDTO reqDTO = new ContractReqDTO();
			ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				amendmentReqDTO.setContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);
				
				if (amendmentResDTO.getCtAmendment() != null) {
					getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					reqDTO.setContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);
					
					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}				
			} else {
				getManagedBean().setAgreementContractVer(ctBackingBean.getManagedBean().getContractVer());
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			 contractSO = null;
			 amendmentSO = null;
		}
	}
	
	/**
	 * The method used for retrieving the committee type
	 * base on loa amount and procurement type
     */
	private String retrieveCommitteeType() {	    
		
		IContractAmendmentSO contractAmendmentSO = null;
		contractAmendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		 if(contractAmendmentSO.getCommitteeType(ctBackingBean.getManagedBean().getContractVer())== null) {
			 throw new EpException(MessageConst.ERROR_755018);
		 }
	    return contractAmendmentSO.getCommitteeType(ctBackingBean.getManagedBean().getContractVer());
	}
	
	/**
	 * The method used to set the secretary for different committee type.
     */
	private void setSecretary() {
		//if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getSecretary() == null) {
			PmUser pmUser = null;
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isAp201Exempt()
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isComplyAP201()
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()) {				
				UserReqDTO userReqDTO = new UserReqDTO();
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				if (getManagedBean().getSecretaryId() != null) {
					userReqDTO.setUserId(getManagedBean().getSecretaryId());
					pmUser = userSO.retrieveUserProfileByUserId(userReqDTO).getPmUser();
				}				
			}
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setSecretary(pmUser);
		//}
	}
	
	/**
	 * The method used to retrieve the bpk approver list by the role code.
     */
	private void getBpkApproverList() {
		try{
			if (getManagedBean().getBpkApproverList() == null) {
				getManagedBean().setBpkApproverList(new ArrayList<PmUser>());
				IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO userReqDTO = new UserReqDTO();
				userReqDTO.setRoleCode(RoleCode.BPK_APPROVER.value());
				UserResDTO userResDTO = userSO.retrieveUserListByRole(userReqDTO);
				getManagedBean().setBpkApproverList(userResDTO.getUserList());
			}
		} finally {}
	}
	
	/**
	 * The method used to retrieve the supplier list for LOA acknowledgment.
     */
	private void getSupplierListForLoaAck() {		
		try{
			if (getManagedBean().getSupplierList() == null) {
				getManagedBean().setSupplierList(new ArrayList<SmPersonnel>());
				IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
				ContractAmendmentReqDTO amendReqDTO = new ContractAmendmentReqDTO();
				amendReqDTO.setSupplierId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
				amendReqDTO.setContractSigner(true);
				ContractAmendmentResDTO amendResDTO = amendSO.retrieveSupplierListById(amendReqDTO);
				
				getManagedBean().setSupplierList(amendResDTO.getSupplierList());
//				IPersonnelSO personnelSO =  SmServiceLookup.getPersonnelSO();
//				PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
//				personnelReqDTO.setSmSupplierId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSupplierId());
//				PersonnelResDTO personnelResDTO = personnelSO.retrievePersonnelBySupplierId(personnelReqDTO);
//				
//				for (SmPersonnel smPersonel : personnelResDTO.getSmPersonnelList()) {
//					if (smPersonel.getPmUser() != null && smPersonel.isRecordStatus()
//							&& smPersonel.isContractSigner()) { //Only supplier with contract signer for acknowledge
//						if (!smPersonel.getPmUser().isRecordStatus()) { //TODO remove !
//							getManagedBean().getSupplierList().add(smPersonel);
//						}
//					}
//				}
			}
		} finally {}
	}
	
	/**
	 * The method used for retrieving the list of loa documents
	 * during loa preparation for contract amendment.
     */
	private void getLoaDocumentList() {
		IDocumentSO documentSO = null;
		DocumentReqDTO reqDTO = null;
		DocumentResDTO resDTO = null;
		
		try {
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports() == null) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setScLoaReports(new ArrayList<ScLoaReport>());
				reqDTO = new DocumentReqDTO();
				documentSO = (IDocumentSO)BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
				reqDTO.setDocCategoryCode(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
				resDTO = documentSO.retrieveDocumentCheckList(reqDTO);
				
				for (PmDocument document : resDTO.getDocumentList()) {
					ScLoaReport reports = new ScLoaReport();
			    	reports.setPmDocument(document);
			    	reports.setTitle(document.getDocName());
			    	reports.setDefinationType(document.getDocDefinitionTypeCode());
			    	//reports.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
			    	reports.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
			    	reports.setScLoa( this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
			    	this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().add(reports);	
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		 } finally {
			documentSO = null;
			reqDTO = null;
			resDTO = null;
		}		
	}
	
	/**
	 * The method used for rendering the related information for
	 * different amendment type.
	 */
	public boolean getAmendValue(int value) {		
		if (value == 1 && (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange())) {
			return true;
		}		
		if (value == 2  && !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()
				&& (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange() 
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isValueChange()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange()
						|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange())) {
			return true;
		}
		return false;
	}
	
	/**
	 * The method used to retrieve proper quantity label for AP201 Approval screen
	 * to be viewed by BPK approver.
	 * 
	 * @return labelString  the additional quantity label
     */
	public String getAdditionalQtyLabel() {
		String labelString = "lbl.ct.additional.qty.exceed.quotation";
		
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			labelString = "lbl.ct.additional.qty.exceed.quotation";
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_TENDER)) {
			labelString = "lbl.ct.additional.qty.exceed";
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)) {
			BigDecimal originalContractAmount = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getLoaAmt();
			if (originalContractAmount != null) {
				labelString = (originalContractAmount.doubleValue() <= ContractConst.CONTRACT_THRESHOLD_500k) ? "lbl.ct.additional.qty.exceed.quotation" : "lbl.ct.additional.qty.exceed";
			}
		}		
		
		return ResourceBundleUtils.getMessage(labelString, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	/**
	 * The method used to retrieve proper additional value label for AP201 Approval screen
	 * to be viewed by BPK approver.
	 * 
	 * @return labelString  the additional value label
     */
	public String getAdditionalValueLabel() {
		String labelString = "lbl.ct.additional.contract.value.exceed.quotation";
		
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			labelString = "lbl.ct.additional.contract.value.exceed.quotation";
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_TENDER)) {
			labelString = "lbl.ct.additional.contract.value.exceed";
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)) {
			BigDecimal originalContractAmount = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getLoaAmt();
			if (originalContractAmount != null) {
				labelString = (originalContractAmount.doubleValue() <= ContractConst.CONTRACT_THRESHOLD_500k) ? "lbl.ct.additional.contract.value.exceed.quotation" : "lbl.ct.additional.contract.value.exceed";
			}
		}		
		
		return ResourceBundleUtils.getMessage(labelString, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	/**
	 * The method used to retrieve proper contract value label for AP201 Approval screen
	 * to be viewed by BPK approver.
	 * 
	 * @return labelString  the contract value label
     */
	public String getValueLabel() {
		String labelString = ResourceBundleUtils.getMessage("lbl.ct.contract.amendment", 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return labelString;
	}
	
	/**
	 * The method used to enable the Complete button for loa creation
	 * after user print out the loa documents.
	 */
	public void showCompleteButton() {
		if (getManagedBean().isShowPrintButton()) {
			getManagedBean().setBpmActionFlag(true);
		}
	}
	
	/**
	 * The method used to retrieve the new amendment effective date.
	 * The new amendment effective date will not lesser than the previous
	 * amendment effective date.
	 * 
	 * @return amendment effective date.
	 */
	private Date retrieveNewEffDate() {
		Date currentDate = DateUtils.getCurrentDateWithoutTime();
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		ContractAmendmentResDTO resAmendDto = amendSO.getCtAmendmentByContractId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractId());
		//
		if (resAmendDto.getAmendmentList() != null && !resAmendDto.getAmendmentList().isEmpty()) {
			int index = resAmendDto.getAmendmentList().size() - 1;
			CtAmendment ctAmendment = resAmendDto.getAmendmentList().get(index);
			if (ctAmendment.getAmendmentEffDate().getTime() < currentDate.getTime()) {
				return currentDate;
			} else {	
				return DateUtils.addDays(ctAmendment.getAmendmentEffDate(), 1);
			}
		} else {
			return currentDate;
		}
	}
	
	/**
	 * The method used to retrieve the new contract amount
	 * with addition of old contract amount and additional amount.
	 * 
	 * @return new contract amount
	 */
	public BigDecimal newContractAmt() {
		return ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().add(
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount());
	}
	
	/**
	 * The method used to retrieve the new overall contract amount
	 * with addition of new contract amount and new gst amount amount.
	 * 
	 * @return new overall contract amount
	 */
	public BigDecimal newOverallContractAmt() {
		BigDecimal contractAmt = newContractAmt();
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount() != null) {
			contractAmt =  newContractAmt().add(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount());
		}
		return contractAmt;
	}
	
	/**
	 * The method used to retrieve the new contract period
	 * with addition of old contract period and extended period.
	 * 
	 * @return new contract period
	 */
	public String calculateNewContractPeriod() {
		return String.valueOf(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getContractPeriod()
				+ ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod());
	}
	
	/**
	 * The method used to retrieve the new contract period
	 * with addition of old contract period and extended period
	 * in years unit
	 * 
	 * @return new contract period in years unit
	 */
	public String calculateMonthsAndYear() {
		int months = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getContractPeriod()
				+ ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod();
		return String.format("%.2f", months / 12.0);
	}
	
	/**
	 * The method used to retrieve the new bond amount
	 * in text format.
	 * 
	 * @return new bond amount in text format.
	 */
	public String getBondAmountInText() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt() == null
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt().doubleValue() == 0) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#.00");		
		return NumberWordUtil.convert(df.format(
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt()));
	}
	
	/**
	 * The method used to indicate the label flag for new price of product item.
	 */
	private void newItemPrice() {
		if (getManagedBean().getSelectedItem() != null && !getManagedBean().getSelectedItem().getCtItemPrices().isEmpty()
				&& (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
						ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
								ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
								ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
			for (CtItemPrice zonelOfItem : getManagedBean().getSelectedItem().getCtItemPrices()) {
				zonelOfItem.setChangeZoneLabel(false);
			}
			for (int temp = 0; temp<getManagedBean().getSelectedItem().getCtItemPrices().size(); temp++) {
				Date date = getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getEffDate();
				int counter = 0;
				int index = 0;
				int countRow = 0;
				for (CtItemPrice itemPrice1 : getManagedBean().getSelectedItem().getCtItemPrices()) {
					countRow++;
					if (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER)
							|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)
							|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF)
	    	    			|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
						if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getFromQty().equals(itemPrice1.getFromQty())
								&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getToQty().equals(itemPrice1.getToQty())
								&& getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
							counter++;
							if (counter >= 2) {
								if (date.before(itemPrice1.getEffDate())) {
									index = countRow;
									date = itemPrice1.getEffDate();
								}
							}
						}
					} else if (getManagedBean().getSelectedItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(itemPrice1.getCtContractZone().getZoneName())) {
						counter++;
						if (counter >= 2) {
							if (date.before(itemPrice1.getEffDate())) {
								index = countRow;
								date = itemPrice1.getEffDate();
							}
		    			}
		    		}
				}
				if (index > 0) {
					CtItemPrice priceOfItem = getManagedBean().getSelectedItem().getCtItemPrices().get(index - 1);
					priceOfItem.setChangeZoneLabel(true);
					getManagedBean().getSelectedItem().getCtItemPrices().remove(index - 1);
					getManagedBean().getSelectedItem().getCtItemPrices().add(index - 1, priceOfItem);
	    		}
			}
		}			
	}
	
	/**
	 * The method used to indicate the label flag for new price of service item.
	 */
	private void newServiceItemPrice() {
		if (getManagedBean().getSelectedServiceItem() != null 
				&& !getManagedBean().getSelectedServiceItem().getCtItemPrices().isEmpty()
				&& getManagedBean().getSelectedServiceItem().getPriceType().getParameterCode().equalsIgnoreCase(
						ParameterConst.PRICE_TYPE_ZONAL_STANDARD)) {
			for (CtItemPrice zonelOfItem : getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
				zonelOfItem.setChangeZoneLabel(false);
    		}
			for (int temp = 0; temp < getManagedBean().getSelectedServiceItem().getCtItemPrices().size(); temp++) {
				Date date = getManagedBean().getSelectedServiceItem().getCtItemPrices().get(temp).getEffDate();
				int counter =0;
			 	int index = 0;
			   	int countRow =0;
			   	for (CtItemPrice itemPrice1 : getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
			   		countRow++;
			   		if (getManagedBean().getSelectedServiceItem().getCtItemPrices().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(
			   				itemPrice1.getCtContractZone().getZoneName())) {
			   			counter++;
			   			if (counter >= 2) {
			   				if (date.before(itemPrice1.getEffDate())) {
			   					index = countRow;
			   					date = itemPrice1.getEffDate();
			   				}
			   			}
			   		}
				}
			   	if (index > 0) {
			   		CtItemPrice priceOfItem = getManagedBean().getSelectedServiceItem().getCtItemPrices().get(index-1);
			   		priceOfItem.setChangeZoneLabel(true);
			    	getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(index-1);
			    	getManagedBean().getSelectedServiceItem().getCtItemPrices().add(index-1, priceOfItem);
			   	}
			}
		}
	}
	
	/**
	 * The method used to edit the product item price.
	 * This method will creating a new records in the item price list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new item price for the product item.
	 */
	public void editItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				     	addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
					  addDupObj.setExpDate(ctItemPrice.getExpDate());
					}
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
		newItemPrice();
		ctItemPriceList.clear();
	}
	
	/**
	 * The method used to edit the service item price.
	 * This method will creating a new records in the item price list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new item price for the service item.
	 */	
	public void editServiceItemPriceOnIndexs() {
		List<CtItemPrice> ctItemPriceList = new ArrayList<CtItemPrice>();
		CtItemPrice addDupObj = new CtItemPrice();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemPrice ctItemPrice : this.getManagedBean().getSelectedServiceItem().getCtItemPrices()) {
			ctItemPriceList.add(ctItemPrice);
			if (!ctItemPrice.isChangePrice() && !ctItemPrice.isChkIndexOfRow()) {
				if (ctItemPrice.equals(this.getManagedBean().getCtServiceItemPrices())) {
					addDupObj.setFromQty(ctItemPrice.getFromQty());
					addDupObj.setToQty(ctItemPrice.getToQty());
					addDupObj.setSubSequence(ctItemPrice.getSubSequence());
					addDupObj.setUnitPrice(ctItemPrice.getUnitPrice());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				     	addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
					  addDupObj.setExpDate(ctItemPrice.getExpDate());
					}
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
		newServiceItemPrice();
		ctItemPriceList.clear();
	}
	
	/**
	 * The method used to delete the newly added product item price.
	 */
	public void deleteItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		getManagedBean().getSelectedItem().getCtItemPrices().remove(getManagedBean().getCtItemPrices());
		newItemPrice();
	}
	
	/**
	 * The method used to delete the newly added service item price.
	 */
	public void deleteServiceItemPriceOnIndexs() {
		CtItemPrice ctItemPrice = getManagedBean().getCtServiceItemPrices().getAssociateCtItemPrice();
		ctItemPrice.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemPrice.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedServiceItem().getCtItemPrices().remove(getManagedBean().getCtServiceItemPrices());
		newServiceItemPrice();
	}
	
	/**
	 * The method used to edit the ceiling quantity of product item.
	 * This method will creating a new records in the ceiling quantity list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new ceiling quantity for the product item.
	 */	
	public void editCeilingQuantityOnIndexs() {
		List<CtItemCeilingQty> ctItemCeilingQtys = new ArrayList<CtItemCeilingQty>();
		CtItemCeilingQty addDupObj = new CtItemCeilingQty();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemCeilingQty ceilingQty : this.getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
			ctItemCeilingQtys.add(ceilingQty);
			if (!ceilingQty.isChangeCeilQuantity() && !ceilingQty.isChkIndexOfRow()) {
				if (ceilingQty.equals(this.getManagedBean().getCtItemCeilingQtys())) {
					addDupObj.setCeilingQuantity(ceilingQty.getCeilingQuantity());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				     	addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						addDupObj.setExpDate(ceilingQty.getExpDate());
					}
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
	
	/**
	 * The method used to delete the newly added ceiling quantity.
	 */
	public void deleteCeilingQuantityOnIndexs() {
		CtItemCeilingQty ctItemCeilingQty = getManagedBean().getCtItemCeilingQtys().getAssociateCtItemCeilingQty();
		ctItemCeilingQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemCeilingQty.setChkIndexOfRow(false);
		this.getManagedBean().getSelectedItem().getCtItemCeilingQties().remove(getManagedBean().getCtItemCeilingQtys());		
	}
	
	/**
	 * The method used to edit the maximum quantity of product item.
	 * This method will creating a new records in the maximum quantity list
	 * with the new effective date and expiry date.
	 * Meanwhile, previous old record will have new corresponding expiry date.
	 * User allow to enter the new maximum quantity for the product item.
	 */
	public void editItemMaxQtyOnIndexs() {
		List<CtItemMaxQty> ctItemMaxQtyList = new ArrayList<CtItemMaxQty>();
		CtItemMaxQty addDupObj = new CtItemMaxQty();
		
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		
		for (CtItemMaxQty ctItemMaxQty : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			ctItemMaxQtyList.add(ctItemMaxQty);
			if (!ctItemMaxQty.isChangeQuantity() && !ctItemMaxQty.isChkIndexOfRow()) {
				if (ctItemMaxQty.equals(getManagedBean().getCtItemMaxQty())) {
					addDupObj.setMaxQuantity(ctItemMaxQty.getMaxQuantity());
					addDupObj.setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				     	addDupObj.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						addDupObj.setExpDate(ctItemMaxQty.getExpDate());
					}
					if (ctItemMaxQty.getCtContractZone() != null) {
						addDupObj.setCtContractZone(ctItemMaxQty.getCtContractZone());
					}
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
		newItemMaxQty();
		ctItemMaxQtyList.clear();
	}
	
	/**
	 * The method used to delete the newly added maximum quantity.
	 */
	public void deleteItemMaxQtyOnIndexs() {
		CtItemMaxQty ctItemMaxQty = getManagedBean().getCtItemMaxQty().getAssociateCtItemMaxQty();
		ctItemMaxQty.setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		ctItemMaxQty.setChkIndexOfRow(false);
		getManagedBean().getSelectedItem().getCtItemMaxQties().remove(getManagedBean().getCtItemMaxQty());
		newItemMaxQty();
	}
		
	/**
	 * The method used to indicate the label flag for new max order quantity of product item.
	 */
	private void newItemMaxQty() {
		if (getManagedBean().getSelectedItem() != null && !getManagedBean().getSelectedItem().getCtItemMaxQties().isEmpty()
				&& (getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
						ParameterConst.PRICE_TYPE_ZONAL_STANDARD)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
								ParameterConst.PRICE_TYPE_ZONAL_TIER)
						|| getManagedBean().getSelectedItem().getPriceType().getParameterCode().equalsIgnoreCase(
								ParameterConst.PRICE_TYPE_ZONAL_TARIFF))) {
			for (CtItemMaxQty zonelOfItem : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
				zonelOfItem.setChangeZoneLabel(false);
			}
			for (int temp = 0; temp < getManagedBean().getSelectedItem().getCtItemMaxQties().size(); temp++) {
				Date date = getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getEffDate();
				int counter =0;
			 	int index = 0;
			   	int countRow =0;
			   	for (CtItemMaxQty itemMaxQty1 : getManagedBean().getSelectedItem().getCtItemMaxQties()) {
			   		countRow++;
			   		if (getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getCtContractZone() != null
			   				&& getManagedBean().getSelectedItem().getCtItemMaxQties().get(temp).getCtContractZone().getZoneName().equalsIgnoreCase(
			   						itemMaxQty1.getCtContractZone().getZoneName())) {
			   			counter++;
			   			if (counter >= 2) {
			   				if (date.before(itemMaxQty1.getEffDate())) {
			   					index = countRow;
			   					date = itemMaxQty1.getEffDate();
			   				}
			   			}
			   		}
				}
			   	if (index > 0) {
			   		CtItemMaxQty maxQtyOfItem = getManagedBean().getSelectedItem().getCtItemMaxQties().get(index-1);
			   		maxQtyOfItem.setChangeZoneLabel(true);
			    	getManagedBean().getSelectedItem().getCtItemMaxQties().remove(index-1);
			    	getManagedBean().getSelectedItem().getCtItemMaxQties().add(index-1, maxQtyOfItem);
			   	}
			}
		}			
	}
	
	/**
	 * The method used to retrieve the item ceiling quantity which in active status
	 * to be displayed on screen.
	 * 
	 * @return activeList  the list of active ceiling quantity to be displayed 
	 */
	public List<CtItemCeilingQty> getActiveItemCeilingQty() {
		List<CtItemCeilingQty> activeList = new ArrayList<CtItemCeilingQty>();
		if (getManagedBean().getSelectedItem() != null) {
			for (CtItemCeilingQty ctItemCeilingQty : getManagedBean().getSelectedItem().getCtItemCeilingQties()) {
				if (ctItemCeilingQty.getCtAgency().isChkStatus()) {
					activeList.add(ctItemCeilingQty);
				}
			}
		}
		return activeList;
	}
	
	/**
	 * The method used for rendering the item price or ceiling quantity
	 * of selected product item that to be displayed on screen.
	 */
	public void onSelectProductItem() {		
		if (getManagedBean().getSelectedItem() != null) {
			if (getManagedBean().getShowItem() != null 
					&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_PRICE)) {
				getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
				
			} else if (getManagedBean().getShowItem() != null 
					&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY)) {
				getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			} else if (getManagedBean().getShowItem() != null 
					&& getManagedBean().getShowItem().equalsIgnoreCase(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY)) {
				getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);				
			} else {
				getManagedBean().setShowItem(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
			}
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
				newItemPrice();
				changeSelectedItemPriceAmendDate(getManagedBean().getSelectedItem());
			}
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
				newItemMaxQty();
				changeSelectedItemMaxQtyAmendDate(getManagedBean().getSelectedItem());
			}
		}
	}
	
	/**
	 * The method used to assign the amendment period for new
	 * item price and old item price.
	 * 
	 * @param selectedItem  the selected item instance
	 */
	private void changeSelectedItemPriceAmendDate(CtContractItem selectedItem) {
		if (selectedItem != null && selectedItem.getCtItemPrices() != null && selectedItem.getCtItemPrices().size() > 0) {
			List<CtItemPrice> itemPriceList = selectedItem.getCtItemPrices();
			for (int i = 0; i < itemPriceList.size(); i++) {
				if (itemPriceList.get(i).isChangePrice()) {
					itemPriceList.get(i - 1).setExpDate(getPreviousDate().getTime());
					itemPriceList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						itemPriceList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						itemPriceList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
				}
			}
	   }
	}
	
	/**
	 * The method used to assign the amendment period for new
	 * item max order quantity and old item max order quantity.
	 * 
	 * @param selectedItem  the selected item instance
	 */
	private void changeSelectedItemMaxQtyAmendDate(CtContractItem selectedItem) {
		if (selectedItem != null && selectedItem.getCtItemMaxQties() != null && selectedItem.getCtItemMaxQties().size() > 0) {
			List<CtItemMaxQty> itemMaxQtyList = selectedItem.getCtItemMaxQties();
			for (int i = 0; i < itemMaxQtyList.size(); i++) {
				if (itemMaxQtyList.get(i).isChangeQuantity()) {
					itemMaxQtyList.get(i - 1).setExpDate(getPreviousDate().getTime());
					itemMaxQtyList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
					if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
						itemMaxQtyList.get(i).setExpDate(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
					} else {
						itemMaxQtyList.get(i).setExpDate(this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
					}
				}
			}
	   }
	}
	
	/**
	 * The method used for rendering the item price
	 * of selected service item that to be displayed on screen.
	 */
	public void onSelectServiceItem() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			newServiceItemPrice();
			changeSelectedItemPriceAmendDate(getManagedBean().getSelectedServiceItem());
		}
	}
	
	/**
	 * The method used to add a new meeting record.
	 * This method will generate a new record in meeting
	 * list, user allow to enter the meeting details. 
	 */
	public void addPbQccMeetingDetails() {
		CtMeeting ctMeeting = new CtMeeting();
		ctMeeting.setCtAmendment(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment());
		this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().add(ctMeeting);
	}
	
	/**
	 * The method used to remove the selected meeting detail(s).
     */
	public void delPbQccMeetingDetails() {
		for (int i = 0; i <selectedMeetingUser.length; i++) {
			CtMeeting meeting = selectedMeetingUser[i];
			this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtMeetings().remove(meeting);
		}
		selectedMeetingUser = null;
	}
	
	/**
	 * The method used to invoke and initiate the search committee 
	 * member dialog.
	 */
	public void fetchCommitteMembers() {
		getManagedBean().setCommitteeName(null);
		getManagedBean().setIdentificationNo(null);
		getManagedBean().setIdentificationType(null);
		getCommitteDataModel().resetData();
		RequestContext.getCurrentInstance().execute("dlg2.show()");
	}
	
	/**
	 * The method used to add the selected existing committee
	 * member as the meeting committee.
	 * This method will generate new record(s) in meeting
	 * committee list.
	 */
	public void addSelectedCommitteMembers() {			
		Map<Long, CtCommitteeMember> duplicatePmUserMap = new HashMap<Long, CtCommitteeMember>();
		List<CtCommitteeMember> memberList = new ArrayList<CtCommitteeMember>();
	
		for (int i = 0; i < selectedPmUser.length; i++) {
			PmUser user = selectedPmUser[i];
			CtCommitteeMember committeeMember = new CtCommitteeMember();
			committeeMember.setCtAmendment(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment());
			committeeMember.setPmUser(user);
			committeeMember.setMemberRole(new PmParameter());
			committeeMember.setExternalUser(false);
			committeeMember.setMemberName(user.getUserName());
			committeeMember.setIcPassport(user.getIdentificationNo());
			committeeMember.setGrade(user.getGrade());
			committeeMember.setPnp(user.isPMP());
			committeeMember.setDesignation(user.getDesignation());
			committeeMember.setEmail(user.getEmail());
		   
		    if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() == 0) {
		    	memberList.add(committeeMember);
		    } else {
		    	 duplicatePmUserMap.put(user.getUserId(), committeeMember);
		    }
		}
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() == 0) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().addAll(memberList);
		} else {
			List<CtCommitteeMember> committeMember = new ArrayList<CtCommitteeMember>();
			for (Map.Entry<Long, CtCommitteeMember> entry : duplicatePmUserMap.entrySet()) {
				boolean chkDataDuplication = false;
     		    for (CtCommitteeMember ctMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
     		    	if (entry.getKey().equals(ctMember.getPmUser().getUserId())) {
     		    		chkDataDuplication = true;
     		    	}
     		    }
     		    if (!chkDataDuplication) {
     		    	committeMember.add(entry.getValue());
     		    }	    
			}
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().addAll(committeMember);
		} 
		Collections.sort(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers());  
	}
	
	/**
	 * The method used to select all added meeting committee(s).
     */
	public void selectAllCommittees() {
	    
		    if(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null 
		    	 && ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().size() > 0){
	    	    
		    	  for (CtCommitteeMember  ctCommiteMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
	    		         if (getManagedBean().isSelectAll()) {
	    		               if (ctCommiteMember.getMemberRole() != null 
	    				              && ctCommiteMember.getMemberRole().getParameterCode().equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
	    			                 continue;
	    		                } else {
	    			               ctCommiteMember.setSelectedCheckBox(true);
	    		                }
	    		         } else {
	    		        	 ctCommiteMember.setSelectedCheckBox(false);
	    		         }
	    	       }
	        }
	}
	
	/**
	 * The method used to remove the selected meeting committee(s).
     */
	public void removeCommitteeMembers(){
		try {
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers() != null) {
				Iterator<CtCommitteeMember> committeeMembers = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().iterator();
				while (committeeMembers.hasNext()) {
					CtCommitteeMember  members = committeeMembers.next();
					if (members.isSelectedCheckBox()) {
						committeeMembers.remove();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally{
			
		}
	}	
	
	/**
	 * The method used to get the parameter description by parameter.
	 * with additional setting for secretary role.
	 * Attendance is compulsory for secretary.
	 * 
	 * @param   index      the current record index
	 * @param   parameter  the PmParameter instance
	 * @return  parameter description
	 */
	public String getParameterDesc(int index,  PmParameter parameter) {
		CtCommitteeMember ctCommitteeMember = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().get(index);
		if (parameter != null && parameter.getParameterCode() != null 
				&& parameter.getParameterCode().equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
			ctCommitteeMember.setAttended(true);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().remove(index);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers().add(index, ctCommitteeMember);	
		}
		return getParameterDesc(parameter);
	}
	
	/**
	 * The method used for rendering the representative type
	 * selection.
	 * 
	 * @param   parameterId  the parameter id for member role
	 * @param   pnp  		 the pnp flag
	 * @return  true   if the committee has member role and is not pnp
	 * 			false  if the committee has not member role and is pnp
	 */
	public boolean chkBooleanRepresentType(Long parameterId, boolean pnp) {		
		if ((parameterId == null || parameterId == 0) && !pnp) {
			return true;
		}
		if (parameterId != null && parameterId != 0
				&& ParameterHelper.getInstance().getPmParameterById(parameterId).getParameterCode().equalsIgnoreCase(
						ParameterConst.COMMITTEE_ROLE_MEMBER)) {
			return true;
		} 
		return false;
	}
	
	/**
	 * The method used to set the meeting decision. 
	 */
	public void setPbQcDecisionValue() {
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApproved(false);
		if (getManagedBean().getPbQcApproved().equalsIgnoreCase("A")) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setPbQcApproved(true);
		}
	}
	
	/**
	 * The method used to add a new user defined loa content.
	 * This method will generate a new record in loa content
	 * list, user allow to enter the title and attachment. 
	 */
	public void addLoaContents() {
		ScLoaReport loaReport = new ScLoaReport();
		
		try {
			loaReport.setDefinationType(new PmParameter());
		    loaReport.setDefinationType(getParameterByTypeAndCode(ParameterConst.DOCUMENT_DEFINITION_TYPE, ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD));
		    //loaReport.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
		    loaReport.setDocCategory(ParameterConst.DOCUMENT_CATEGORY_LOA_FOR_CONTRACT_AMENDMENT);
		    loaReport.setScLoa(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa());
		 
		    ScLoaReportAttachment attachment = new ScLoaReportAttachment();
		    //attachment.setDocType(ParameterConst.DOCTYPE_CONTRACT_AMENDMENT);
		    attachment.setScLoaReport(loaReport);			    
			
		    loaReport.setScLoaReportAttachment(attachment);
		    this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().add(loaReport);
		} catch(Exception ex) {
		   ex.printStackTrace();
		}
	}
	
	/**
	 * The method used to delete the selected loa content that
	 * defined by user.
	 */
	public void deleteLoaContents() {
		this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports().remove(this.getManagedBean().getSelectedScLoaReports());
	}
	
	/**
	 * The method used to set the amendment information
	 * when user wish to amend the contract period.
	 */
	public void onContractExtensionChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(1); // default to 1 month
			Calendar c = Calendar.getInstance();
			c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			c.add(Calendar.DATE, 1);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());
			
			c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
			c.add(Calendar.MONTH, 1);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
		}
		else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendPeriod(0);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(null);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(null);
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(getManagedBean().getNewAmendmentEffDate());
		}
		changeAmendmentEffDate();
	}
	
	/**
	 * The method used to set the related amendment information
	 * when user wish to amend the item price.
	 */
	public void onItemPriceChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			// contract type: central and followLoaAmount: false, disallow to change contract value
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);				
			}
		} else if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			onChangeValueChecked();
		}
		ConstructTabSideMenu();
	}

	/**
	 * The method used to set the related amendment information
	 * when user wish to amend the item quantity.
	 */
	public void onQuantityChecked() {
		if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			// contract type: central and followLoaAmount: false, disallow to change contract value
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL) 
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractValue().isFollowLoaAmt()) {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			} else {
				ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(true);				
			}
		} else if(!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange() 
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setValueChange(false);
			onChangeValueChecked();
		}
		ConstructTabSideMenu();
	}
	
	/**
	 * The method used to set the amendment information
	 * when user wish to amend the contract period.
	 */
	public void onChangeValueChecked() {
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAdditionalAmount(BigDecimal.ZERO);
		if (ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount() != null) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getGstAmount());	
		} else {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setNewGstAmount(BigDecimal.ZERO);
		}
	}
	
	/**
	 * The method used to set the related amendment information
	 * when user extended the contract period.
	 */
	public void onExtensionPeriodChanged() {
		Calendar c = Calendar.getInstance();
		c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.DATE, 1);
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendStartDate(c.getTime());
		
		c.setTime(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
		c.add(Calendar.MONTH, ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod());
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setExtendEndDate(c.getTime());
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setAmendmentEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate());
		changeAmendmentEffDate();
	}
	
	/**
	 * The method used to set the related amendment information
	 * when user changed the amendment effective date.
	 */
	public void changeAmendmentEffDate() {
		if (this.getManagedBean().getSelectedServiceItem() != null) {
			changeSelectedItemPriceAmendDate(this.getManagedBean().getSelectedServiceItem());
		}
		if (this.getManagedBean().getSelectedItem() != null ){
			if (this.getManagedBean().getSelectedItem().getCtItemPrices() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemPrices().size() > 0) {
				changeSelectedItemPriceAmendDate(this.getManagedBean().getSelectedItem());				  
			}
			if (this.getManagedBean().getSelectedItem().getCtItemCeilingQties() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemCeilingQties().size() > 0) {
				List <CtItemCeilingQty> ceilingQtyList = this.getManagedBean().getSelectedItem().getCtItemCeilingQties();
				for (int i = 0; i < ceilingQtyList.size(); i++) {
					if (ceilingQtyList.get(i).isChangeCeilQuantity()) {						   
						ceilingQtyList.get(i - 1).setExpDate(getPreviousDate().getTime());
						ceilingQtyList.get(i).setEffDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
						if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
							ceilingQtyList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate());
						} else {
							ceilingQtyList.get(i).setExpDate(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate());
						}
					}
				}
			}
			if (this.getManagedBean().getSelectedItem().getCtItemMaxQties() != null 
					&& this.getManagedBean().getSelectedItem().getCtItemMaxQties().size() > 0) {
				changeSelectedItemMaxQtyAmendDate(this.getManagedBean().getSelectedItem());				  
			}
		}
	}
	
	/**
	 * The method used to set the contract signer information
	 * when user select a signer from the selection list.
	 */
	public void contractSignerChange() {
		try {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			Long contractSignerId = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getContractSigner().getUserId();
			if (contractSignerId != null) {
				UserReqDTO reqDTO = new UserReqDTO();
			   	reqDTO.setUserId(contractSignerId);			   	
			   	UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
			   	PmUser pmUser = resDTO.getPmUser();
			   	if (pmUser != null) {
			   		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().setContractSigner(pmUser);
			   	}
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
	}
	
	/**
	 * The method used to set the bank and account information
	 * when user select a bank from the selection list.
	 */
	public void changeAccountActionListener() {
		if (this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo() != null) {
			for (SmSupplierBank supplierBank : this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()) {
				if (supplierBank.getAccountNo().equalsIgnoreCase(
						this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo())) {
					this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankName(supplierBank.getPmFinancialOrg().getFinOrgName());  
					if (supplierBank.getSmSupplierBranch() != null) {
						this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankBranch(supplierBank.getSmSupplierBranch().getBranchName());
					} else {
						this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
					}
					if (supplierBank.getSmAddress() != null) {
						this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAddress(supplierBank.getSmAddress());
					} else {
						this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
					}
				}
			}
		} else {
			this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankName(null);  
			this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankBranch(null);
			this.ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAddress(null);
		}
	}
	
	/**
	 * The method used for validating the duplicate chairperson.
	 * A warning message will display on screen when more than one chairperson
	 * is added as committee.
	 * 
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void validateDuplicateChairperson(Long parameterId, int index) throws Exception {
		if (parameterId == null || parameterId.intValue() == 0) {
			return;
		}
		int counter = 0;
		String parametercode = ParameterHelper.getInstance().getPmParameterById(parameterId).getParameterCode();
		if (parametercode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {  
				if (committeMember.getMemberRole() != null && committeMember.getMemberRole().getParameterId() != null
						&& committeMember.getMemberRole().getParameterId().intValue() == parameterId.intValue()) {
					counter++;
					if (counter == 2) {
						throw new EpException(MessageConst.ERROR_755016);
					}
				}
			}
		}	
	}
	
	/**
	 * The method used for validating the meeting attendance.
	 * A warning message will display on screen when the required
	 * committee doesn't not attended the meeting.
	 */
	private void validateMeetingDetails() {
        boolean secretaryRole = true;
        boolean chairpersonRole = true;
        boolean memberRole = true;
        boolean representativeJKR = true;
        boolean representativeBPK = true;
        boolean representativeSecretary = true;
        boolean representativeOfficer = true;
        boolean representativeDirector = true;
                
		for (CtCommitteeMember committeObj : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
			if (committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
					ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
			 	chairpersonRole = false;
	    	} else if(committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
	    			ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
				secretaryRole = false;
	    	} else if (committeObj.isAttended() && committeObj.getMemberRole().getParameterCode().equalsIgnoreCase(
	    			ParameterConst.COMMITTEE_ROLE_MEMBER)){
	    		memberRole = false;
	    	}
			
			if (committeObj.getRepresentativeType() != null && committeObj.getRepresentativeType().getParameterCode() != null) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_SECRETARY_HEAD_DEPARTMENT)) {
						representativeSecretary = false;
					} 
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR)) {
						representativeJKR = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_BPK)) {
						representativeBPK = false;  
					}
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
					if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SBH)) {
						representativeOfficer = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_ASSISTANT_DIRECTOR_SBH)) {
						representativeDirector = false;  
					} else if(committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SBH)) {
						representativeSecretary = false;  
					}
				}
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(
						ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if(committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst. COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SWK)) {
						representativeSecretary = false;  
					} else if (committeObj.isAttended() && committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SWK )) {
						representativeOfficer = false;  
					} else if (committeObj.isAttended() &&committeObj.getRepresentativeType().getParameterCode().equalsIgnoreCase(
							ParameterConst.COMMITTEE_REPRESENT_JKR_DIRECTOR_SWK)) {
						representativeDirector = false;  
					}
				}
			}
		}    
		if (chairpersonRole) {
			RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.chairperson.attendance", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
		if (secretaryRole){
			RequestContext.getCurrentInstance().addCallbackParam("epException", true);
			throw new EpException(MessageConst.ERROR_100022, 
					new String[]{ResourceBundleUtils.getMessage("lbl.ct.secretary.attendance", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	  
		}
		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B)
				|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
			if (memberRole) {
				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.member.attendance", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}
		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
			if (representativeJKR) {
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeSecretary) {
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.secretary", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	    	}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
    	    if (representativeJKR) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.jkr", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    } else if (representativeBPK) {
				throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.bpk", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    }
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
    	    if (representativeOfficer) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.officer.sabah", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    } else if (representativeDirector) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.director.sabah", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    } else if (representativeSecretary) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.secretary.sabah", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    }
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
    	    if (representativeSecretary) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.secretary.sarawak", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    } else if (representativeOfficer) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.officer.sarawak", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    } else if(representativeDirector) {
    	    	throw new EpException(MessageConst.ERROR_100022, 
						new String[]{ResourceBundleUtils.getMessage("lbl.ct.attendance.director.sarawak", 
								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
    	    }
		}
	}
	
	/**
	 * The method used for validating the committee member details.
	 * A warning message will display on screen when the required
	 * committee doesn't not included in the meeting.
	 */
	private void validateCommiteeMemberDetails() {
        boolean secretaryRole = true;
		boolean chairpersonRole = true;
        boolean memberRole = true;
        boolean representativeJKR = true;
        boolean representativeBPK = true;
        boolean representativeSecretary = true;
        boolean representativeOfficer = true;
        boolean representativeDirector = true;
	     
        for (CtCommitteeMember committeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
        	if (committeMember.getMemberRole() != null && committeMember.getMemberRole().getParameterId() != null) {
        		String representativeCode = "";
        		String parameterCode = ParameterHelper.getInstance().getPmParameterById(committeMember.getMemberRole().getParameterId()).getParameterCode();
        		
        		if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON)) {
        			chairpersonRole = false;
        		} else if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_SECRETARY)) {
        			secretaryRole = false;
             	} else if (parameterCode.equalsIgnoreCase(ParameterConst.COMMITTEE_ROLE_MEMBER)) {
             		memberRole = false;
             		if (!getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_B) 
             				&& !getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_QUOTATION_COMMITTEE_A)) {
             			if (committeMember.getRepresentativeType() == null 
             					|| committeMember.getRepresentativeType().getParameterId() == null) {
             				RequestContext.getCurrentInstance().addCallbackParam("epException", true);
             				throw new EpException(MessageConst.ERROR_100022, 
             						new String[]{ResourceBundleUtils.getMessage("lbl.ct.represent.type", 
             								ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
             			} else {
             				representativeCode = ParameterHelper.getInstance().getPmParameterById(
             						committeMember.getRepresentativeType().getParameterId()).getParameterCode();
             			}
             		}
             	}
        		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
        			if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR)) {
        				representativeJKR = false;
                 	} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_SECRETARY_HEAD_DEPARTMENT)) {
                 		representativeSecretary = false;
                 	} 
        		}
        		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
        			if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR)) {
        				representativeJKR = false;  
        			} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_BPK)) {
        				representativeBPK = false;  
        			}
        		}
        		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
        			if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SBH)) {
        				representativeOfficer = false;  
	    			} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_ASSISTANT_DIRECTOR_SBH)) {
	    				representativeDirector = false;  
	    			} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SBH)) {
	    				representativeSecretary = false;  
	    			}
	    		}
        		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
        			if (representativeCode.equalsIgnoreCase(ParameterConst. COMMITTEE_REPRESENT_FEDERAL_SECRETARY_SWK)) {
        				representativeSecretary = false;  
        			} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_STATE_DEV_OFFICER_SWK )) {
             		   	representativeOfficer = false;  
        			} else if (representativeCode.equalsIgnoreCase(ParameterConst.COMMITTEE_REPRESENT_JKR_DIRECTOR_SWK)) {
        				representativeDirector = false;  
        			}
        		}
        	} else if (committeMember.getMemberRole() == null || committeMember.getMemberRole().getParameterId() == null) {
        		RequestContext.getCurrentInstance().addCallbackParam("epException", true);
    			new EpException(MessageConst.ERROR_750025);
	    	}
		}
        
        if (chairpersonRole) {
        	RequestContext.getCurrentInstance().addCallbackParam("epException", true);
        	throw new EpException(MessageConst.ERROR_100022, 
        			new String[]{ResourceBundleUtils.getMessage("lbl.ct.chairperson", 
        					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
        if (secretaryRole) {
        	RequestContext.getCurrentInstance().addCallbackParam("epException", true);
        	throw new EpException(MessageConst.ERROR_100022, 
        			new String[]{ResourceBundleUtils.getMessage("lbl.ct.secretary", 
        					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}
        if(memberRole) {
        	RequestContext.getCurrentInstance().addCallbackParam("epException", true);
        	throw new EpException(MessageConst.ERROR_100022,
        			new String[]{ResourceBundleUtils.getMessage("lbl.ct.member", 
        					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
        }
        
        if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
        	if (representativeJKR) {
        		throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.jkr", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
        	} else if (representativeSecretary) {
        		throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.secretary", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
        	}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
			if (representativeJKR) {
        		throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.jkr", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeBPK) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.bpk", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	    	}
		} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
			if (representativeOfficer) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.officer.sabah", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			} else if (representativeDirector) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.director.sabah", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}else if(representativeSecretary){
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.secretary.sabah", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
	 	} else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
	 		if (representativeSecretary) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.secretary.sarawak", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	 		} else if (representativeOfficer) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.officer.sarawak", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	    	} else if (representativeDirector) {
				throw new EpException(MessageConst.ERROR_100022,
            			new String[]{ResourceBundleUtils.getMessage("lbl.ct.representative.director.sarawak", 
            					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
	    	}
	 	}
	}	
	
	/**
	 * The method used to set the flag of comply AP201 rule.
	 */
	private void chkComplyAP201Rule() {
		ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(true);
		if (breachAP201Rules(1) || breachAP201Rules(2) || breachAP201Rules(3)) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isClauseChange() 
				|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()) {
			// 10/11/2014 Lilian said clause change & item price change is still comply with AP201.
			//ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getRecordStatusFlg().intValue()!=DbValueConst.RECORD_STATUS_ACTIVE) {
			ctBackingBean.getManagedBean().getContractVer().getCtAmendment().setComplyAP201(false);
		}
	}
	
	/**
	 * The method used to check the rules for AP201.
	 * 
	 * @return true   if breach the AP201 rule
	 * @return false  if not breach the AP201 rule
	 */
	public boolean breachAP201Rules(int rulesType) {
		double maxAdditionalLimit = ContractConst.CONTRACT_THRESHOLD_7_5m;
		double maxTotalLimit = ContractConst.CONTRACT_THRESHOLD_25m;
		if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
			maxAdditionalLimit = ContractConst.CONTRACT_THRESHOLD_250k;
			maxTotalLimit = ContractConst.CONTRACT_THRESHOLD_750k;
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_TENDER)) {
			maxAdditionalLimit = ContractConst.CONTRACT_THRESHOLD_7_5m;
			maxTotalLimit = ContractConst.CONTRACT_THRESHOLD_25m;
		} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(
				ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)) {
			BigDecimal originalContractAmount = ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getLoaAmt();
			if (originalContractAmount != null) {
				if (originalContractAmount.doubleValue() > ContractConst.CONTRACT_THRESHOLD_500k) {
					maxAdditionalLimit = ContractConst.CONTRACT_THRESHOLD_7_5m;
					maxTotalLimit = ContractConst.CONTRACT_THRESHOLD_25m;
				} else {
					maxAdditionalLimit = ContractConst.CONTRACT_THRESHOLD_250k;
					maxTotalLimit = ContractConst.CONTRACT_THRESHOLD_750k;
				}
			}
		} 
		
		if (rulesType == 1) { // contract extend
			if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend() 
					&& getManagedBean().getAmendHistoryList().size() > 0) 
					|| ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() >= 24) {
				return true;
			}
		} else if (rulesType == 2) { // change quantity		
			for (CtContractItem ctContractItem : getManagedBean().getCtItemList()) {
				for (CtItemMaxQty ctItemMaxQty : ctContractItem.getCtItemMaxQties()) {
					if (ctItemMaxQty.isChangeQuantity()) {
						if (ctItemMaxQty.getMaxQuantity().doubleValue() > ctItemMaxQty.getAssociateCtItemMaxQty().getMaxQuantity().doubleValue() * 1.5) {
							return true;
						}
					}
				}
			}
		} else if (rulesType == 3) { // change coontract value
			if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue() / ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().doubleValue()) > 0.5) {
				return true;
			}			
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue() > maxAdditionalLimit) {
				return true;
			}
			if ((ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount().doubleValue()+ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount().doubleValue()) > maxTotalLimit) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * The method used for generating a list of available bank and account
	 * for supplier as selection.
	 * 
	 * @return populateBankAccountList  the selection list of bank and account
	 */
	public List<SelectItem> getPopulateBankAccountList() {
		if (populateBankAccountList == null) {
			if (this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks()!= null
					&& this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks().size() > 0) {
				populateBankAccountList = new ArrayList<SelectItem>();
				List<SmSupplierBank> supplierBank = this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getSmAppl().getSmSupplierBanks();
				if (supplierBank.size() == 1) {
					ctBackingBean.getManagedBean().getContractVer().getCtContractValue().setBankAccountNo(supplierBank.get(0).getAccountNo());
					changeAccountActionListener();
				}
				for (SmSupplierBank obj : supplierBank) {
					String account = obj.getAccountNo();
					if (obj.getPmFinancialOrg() != null) {
						account = account + " / " + obj.getPmFinancialOrg().getFinOrgName();
					}
					populateBankAccountList.add(new SelectItem(obj.getAccountNo(),account));
				}     
			}
		}
		return populateBankAccountList;
	}
	
	/**
	 * The method used for generating a list of available display type
	 * of product item as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getItemDisplayTypeDropdownList() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		
		if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isOtherChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange())) {
			
					List<String> itemDisplayTypeList = new ArrayList<String> ();
					itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_PRICE);
					itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
					itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
					
					searchBean.getDataFilterBeans().add(
							new SearchFilterBean("pmParameter.parameterCode",
									SearchOperator.OPERATOR_IN.value(),
									itemDisplayTypeList));
	
		} else if ((ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				&& !ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange())) {
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_EQUALS.value(), 
							ParameterConst.ITEM_DISPLAY_TYPE_PRICE));
		
		} else if (!ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isItemPriceChange()
				&& ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isQuantityChange()) {
			
			List<String> itemDisplayTypeList = new ArrayList<String> ();
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_CEIL_QTY);
			itemDisplayTypeList.add(ParameterConst.ITEM_DISPLAY_TYPE_MAX_QTY);
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("pmParameter.parameterCode",
							SearchOperator.OPERATOR_IN.value(),
							itemDisplayTypeList));
		} 
		return super.getDropdownCodeValuesBySearchBean(ParameterTypeConst.ITEM_DISPLAY_TYPE, searchBean);
	}
	
	/**
	 * The method used for generating a list of available secretary 
	 * as selection.
	 * 
	 * @return the selection list of secretary
	 */
	public SelectItem[]  getSecretaryDropdownList() {
		SelectItem[] items = null;     
		try {
			int i = 1;
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
			List<Long> orgProfileIds = new ArrayList<Long>();
			Long ownerOrgProfileId = ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId();
			
		   	OrgProfileReqDTO orgProfileReqDTO = new OrgProfileReqDTO();
		   	orgProfileReqDTO.setOrgProfileId(ownerOrgProfileId);
		   	OrgProfileResDTO orgProfileResDTO = orgProfileSO.retrieveProfileGroupByAuthorizedProfileId(orgProfileReqDTO);
		   	
		   	for (PmProfileGroup profileGroup : orgProfileResDTO.getProfileGroups()) {
		   		orgProfileIds.add(profileGroup.getMainOrgProfile().getOrgProfileId());
		   	}
		   	
		   	UserReqDTO reqDTO = new UserReqDTO();
		   	if(getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
		   			|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
				reqDTO.setRoleCode(RoleCode.FPB_SECRETARY.value());
			} else {
				reqDTO.setRoleCode(getManagedBean().getCommitteeType()+"_SECRETARY");
			}
			orgProfileIds.add(ownerOrgProfileId);
			// TODO: Removed after all procurement level set in PTJ??? TBC
			PmOrgProfile ministryOrgProfile = retrieveMinistryOrgProfile(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
	   	  	if (ministryOrgProfile != null) {
	   	  		orgProfileIds.add(ministryOrgProfile.getOrgProfileId());
	   	  	}
		   	reqDTO.setOrgProfileIds(orgProfileIds);		   	
		   	UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
		   	
		   	List<PmUser> list = resDTO.getUserList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			
			for (PmUser user : list) {
		       items[i++] = new SelectItem(user.getUserId(), user.getUserName());
			}
		} catch (Exception e) {
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
	  	}
		return items;
	}	
	
	/**
	 * The method used for generating a list of available financial institution
	 * for performance bond as selection.
	 * 
	 * @return the selection list of financial institution
	 */
	public SelectItem[] retrieveFinancialOrgList() {
		SelectItem[] items = null; 
		IFinancialOrgSO financialOrgSO;
		
		try {
		    int i = 1;
			financialOrgSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IFinancialOrgSO.class);
			FinancialOrgReqDTO reqDTO = new FinancialOrgReqDTO();
			reqDTO.setServiceType(ParameterConst.BANK_SERVICES_PERFORMANCE_BOND);
			FinancialOrgResDTO resDTO = financialOrgSO.retrieveFinancialOrgListByServiceType(reqDTO);
			
			List<PmFinancialOrg> list = resDTO.getFinancialOrgList();
		    items = new SelectItem[list.size() + 1];
		    items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
		    		ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		    
		    for (PmFinancialOrg pmFinancialOrg : list) {
		    	items[i++] = new SelectItem(pmFinancialOrg.getFinancialOrgId(), pmFinancialOrg.getFinOrgName());   
		    }
	    } catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}   	
		return items;
	}
	
	/**
	 * The method used for generating a list of available contract signer
	 * as selection.
	 * 
	 * @return the selection list of contract signer
	 */
	public SelectItem[] getContractSignerDropdown() {
		SelectItem[] items = null;  
		try {
			int i = 1;
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.CONTRACT_SIGNER.value());	
			reqDTO.setOrgProfileId(this.ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			reqDTO.setApprovalLimit(newContractAmt());
			UserResDTO resDTO = userSO.retrieveUserListByRole(reqDTO);
			
			List<PmUser> list = resDTO.getUserList();
			items = new SelectItem[list.size()+1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			
			for (PmUser pmUser : list) {
				items[i++] = new SelectItem(pmUser.getUserId(), pmUser.getUserName());
			}
		 } catch (Exception e) {
			 e.printStackTrace();
			 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
		return items;
	}
	
	/**
	 * The method used for generating a list of available meeting status
	 * as selection.
	 * 
	 * @return the selection list of meeting status
	 */
	public SelectItem[] getMeetingStatusDropdown() {
		SelectItem[] items = null;     
		try {
			int i = 1;
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode("SC");
			reqDTO.setDocType("MS");
			String languageCode = new FacesScopes().getUserLocale().getLanguage();
			reqDTO.setLanguageCode(languageCode);
			StatusResDTO resDTO = statusSO.retrieveStatusDescList(reqDTO);
				
			List<PmStatusDesc> list = resDTO.getStatusDescList();
			items = new SelectItem[list.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		  	
			for (PmStatusDesc statusDesc : list) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}
		return items;
	}
	
	/**
	 * The method used for generating a list of available representative role
	 * as selection.
	 * 
	 * @return the selection list of representative role
	 */
	public SelectItem[] getRepresentativeDropdown() {		
		for (CtCommitteeMember committeeMember : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getCtCommitteeMembers()) {
	        if (committeeMember.getRepresentativeType() == null) {
	        	committeeMember.setRepresentativeType(new PmParameter());
			}
		} 
		String representativeRole ="";
		if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_B)) {
			representativeRole =ParameterTypeConst.REPRESENTATIVE_TYPE_PBB;
        } else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD_A)) {
        	representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_PBA;
        } else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)) {
        	representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBH;
        } else if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
        	representativeRole = ParameterTypeConst.REPRESENTATIVE_TYPE_FPBK;
        }
		return getDropdownValues(representativeRole);
	}
	
	/**
	 * The method used for generating a list of available role type
	 * as selection.
	 * 
	 * @return the selection list of role type
	 */
	public SelectItem[] getRoleTypeDropdown(boolean pnp) {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> committeRole = new ArrayList<String>();
		if (pnp) {
			committeRole.add(ParameterConst.COMMITTEE_ROLE_CHAIRPERSON);
		}
		committeRole.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
		searchBean.getDataFilterBeans().add(new SearchFilterBean("pmParameter.parameterCode", 
				SearchOperator.OPERATOR_IN.value(), committeRole));
		return getDropdownValuesBySearchBean(ParameterTypeConst.COMMITTEE_ROLE, searchBean);
	}
	
	/**
	 * The method used for initializing the remarks of contract amendment.
	 */
	public void initializeRemarks() {
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getAp201Remark());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getSupplierRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getAgencyRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getZoneRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getItemRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getDeductionRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getScheduleRemarks());
		initiateRemark(ctBackingBean.getManagedBean().getContractVer().getSettingRemarks());
	}
	
	/**
	 * The method used for initializing the remarks of contract amendment.
	 * This method will create new remark object for every single new 
	 * process, else will display all remarks.
	 * 
	 * @param remarkList  the list of remarks to display. If the remarkList is null, 
	 *                    initiate a new remark
	 */
	private void initiateRemark(List<CtRemark> remarks) {
		
		boolean ctWorkflowStatusMatch = false;
		if (ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus() != null) {
			if (remarks.size()>0 && remarks.get(0).getCtWorkflowStatus() != null) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtWorkflowStatus().getWorkflowStatusId() 
						== remarks.get(0).getCtWorkflowStatus().getWorkflowStatusId().longValue()) {
					ctWorkflowStatusMatch = true;
				}
			}
		} else { // both also null
			ctWorkflowStatusMatch = true;
		}		
		
		if (remarks.size() == 0 || !ctWorkflowStatusMatch) {
			CtRemark remark = new CtRemark();
			remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
			remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			remark.setChangedBy(remark.getCreatedBy());
			remark.setChangedDate(remark.getCreatedDate());
			remark.setCreatedUser(getFacesScopes().getUserSessionBean());
			remarks.add(0, remark);
		}
	}
	
	/**
	 * The method used to get the Surat Pengesahan Setuju Terima Pembekal
	 * that to be displayed on screen.
	 * 
	 * @throws FileNotFoundException  if an file cannot be found
	 * @throws Exception  if an exception occurred
	 * @see    FileNotFoundException, Exception
	 */	
	public String getLoaStreamFilePath() throws FileNotFoundException, Exception {
		String filePath = null;
		ScLoaReport agreementLetterReport = null;
		for (ScLoaReport scLoaReport : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports()) {
			if (scLoaReport.getPmDocument() != null) {
				String docDefinitionKey = scLoaReport.getPmDocument().getDocDefinitionKey();
				if (SourcingConstants.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equals(docDefinitionKey)) {
					agreementLetterReport = scLoaReport;
					break;
				}
			}
		}
		if (agreementLetterReport != null) {
			String docDefinitionKey = agreementLetterReport.getPmDocument().getDocDefinitionKey();
			String fileName = getTemplateFileName(docDefinitionKey);
			if (fileName != null) {
				String content = null;
				AttachmentFormat attachmentFormat = new AttachmentDateFormat();
				filePath = getManagedBean().getFileDirectory() + "/temp";
				File file = new File(filePath);
				file.mkdirs();
				filePath = filePath + "/" + attachmentFormat.format(fileName, null);
				content = getContent(docDefinitionKey);
				DocumentGenerator.getInstance().generatePdf(
						new FileOutputStream(filePath), content);
			}
		}
		return filePath;
	}
	
	/**
	 * The method used to set the file index of attachment list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void setAttachFileIndex(int index) {
		this.getManagedBean().setAttachFileIndex(index);
	}
	
	/**
	 * The method used to delete the AP201 attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteAttachmentAction(int index) {
		ctBackingBean.getManagedBean().getContractVer().getAp201Attachements().remove(index);
	}
	
	/**
	 * The method used to delete the meeting decision attachment from the list.
	 * 
	 * @param index  file index of attachment list
	 */
	public void deleteCommitteAttachmentAction(int index) {
		ctBackingBean.getManagedBean().getContractVer().getCommitteDecisionAttachements().remove(index);
	}
	
	/**
	 * The method used to upload ap201 file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */	
	public void ap201FileUpload(FileUploadEvent event) throws Exception {
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		this.ctBackingBean.getManagedBean().getContractVer().getAp201Attachements().add(attachment);
	}
	
	/**
	 * The method used to upload loa content file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */	
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
	    
	    for(ScLoaReport reports : this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports()) {
	    	if(reports.equals(this.getManagedBean().getSelectedScLoaReports())) {
	    		reports.getScLoaReportAttachment().setFileName(item.getFileName());
	    		reports.getScLoaReportAttachment().setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
	    		reports.getScLoaReportAttachment().setFileDesc(item.getFileName());
	        }
	    }
	}
	
	/**
	 * The method used to upload meeting decision file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
	public void committeeFileUpload(FileUploadEvent event) throws Exception {
		FacesScopes facesScopes = new FacesScopes();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		
		UploadedFile item = event.getFile();
		
		String filePath = getManagedBean().getFileDirectory();
		File file = new File(filePath);
		file.mkdirs();
		filePath = filePath + "/" + attachmentFormat.format(item.getFileName(), null);
		
		FileOutputStream fileOuputStream = new FileOutputStream(filePath); 
	    fileOuputStream.write(item.getContents());
	    fileOuputStream.close();
	    
		CtAttachment attachment = new CtAttachment();
		attachment.setFileName(item.getFileName());
		attachment.setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		attachment.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
		attachment.setCreatedUser(facesScopes.getUserSessionBean());
		attachment.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		ctBackingBean.getManagedBean().getContractVer().getCommitteDecisionAttachements().add(attachment);
	}

	/**
	 * The method used to download the attached file.
	 * 
	 * @param  filePath	    will be download file path
	 * @param  fileName     will be download filename
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
    public void downloadFile(String filePath, String fileName) throws IOException {
		PortletResponse  portletResponse = (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();  
		HttpServletResponse res = com.liferay.portal.util.PortalUtil.getHttpServletResponse(portletResponse);
		
		res.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\"" );//
		res.setHeader( "Content-Transfer-Encoding", "binary" );
		res.setContentType("application/octet-stream");
		res.flushBuffer();
		OutputStream out = res.getOutputStream();   
		if (filePath != null) {
			filePath = DocumentRepositoryUtils.getBaseDir()+filePath;
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
			while ((bytesRead = input.read( buffer )) != -1) { 
				out.write(buffer, 0, bytesRead);
				buffer = new byte[4096]; 
			}
			input.close();			
		}
		out.flush();
		out.close(); 
		FacesContext.getCurrentInstance().responseComplete();
	}
    
    /**
	 * The method used to download the sample bond letter
	 * as pdf file.
	 */
    public void downloadSampleBondLetter() {
		try {
			String filePath = getManagedBean().getFileDirectory();		      
	        File file = new File(filePath);
            file.mkdirs();
			
			String fileName = "Sample Bond Letter.pdf";
			filePath = getManagedBean().getFileDirectory() +  "/" + fileName;
	
			File pdfFile = new File(filePath);
			if (!pdfFile.exists()) {
				VelocityContext context = new VelocityContext();
				int type = 0;				
				context.put("type", type);
				context.put("contractNo", "...........................");
				context.put("supplierName", "...........................");
				context.put("sourcingTitle", "...........................");
				context.put("bondAmount", "..............................");
				context.put("bondAmountWord", "..................................................");
				context.put("expiryPeriod", "..........................");	
			
				DocumentGenerator.getInstance().generatePdfFromVelocity(
						SourcingConstants.LOA_BOND_LETTER, context,
						new FileOutputStream(filePath));
			}			
			downloadFile(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    /**
	 * The method used to download the loa report.
	 * 
	 * @param  loaReport    an object of loa report
	 * @throws IOException  if an input or output exception occurred
	 * @see    IOException
	 */
    public void downloadDocFile(ScLoaReport loaReport) throws IOException {
		AttachmentFormat attachmentFormat = null;
		
	    try {
	    	String docDefinitionKey = loaReport.getPmDocument().getDocDefinitionKey();
	    	String fileName = getTemplateFileName(docDefinitionKey);
			if (fileName != null) {		        
				String content = null;	
		      	attachmentFormat = new AttachmentDateFormat();
		      	String filePath = getManagedBean().getFileDirectory() + "/temp";
			      
				File file = new File(filePath);
				file.mkdirs();
		      	filePath = filePath + "/" + attachmentFormat.format(fileName, null);
			       
		      	if (ContractConst.TEMPLATE_JADUAL_HARGA.equalsIgnoreCase(loaReport.getPmDocument().getDocName())) {
					formJadualHargaExcel(filePath);			       
		     	} else {				        
		     		content = getContent(docDefinitionKey);
		     		DocumentGenerator.getInstance().generatePdf(new FileOutputStream(filePath), content);
		     	}
		      	downloadFile(DocumentRepositoryUtils.getDBFilePath(filePath), fileName);
		    } else {
		    	if (loaReport.getScLoaReportAttachment() == null || loaReport.getScLoaReportAttachment().getFilePath() == null) {
		    		throw new EpException(MessageConst.INFO_100052, new String[]{loaReport.getTitle()});
		    	}
		    	downloadFile(DocumentRepositoryUtils.getDBFilePath(loaReport.getScLoaReportAttachment().getFilePath()), loaReport.getScLoaReportAttachment().getFileName());
		    }
	    } catch(Exception e) {
	    	e.printStackTrace();
		} finally {
			attachmentFormat = null;
		}
	}
    
    /**
	 * The method used to download all loa reports
	 * as a zip file.
	 */
	public void downloadAll() {		
		List<File> fileList = new ArrayList<File>();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();		
		String fileName = null;
		String filePath = null;
		String content = null;
		String tempDirectory = getManagedBean().getFileDirectory() + "/temp";
		File file = new File(tempDirectory);
		file.mkdirs();		
		
		try {
			for (ScLoaReport scLoaReport : ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoaReports()) {
				// System Define Doc
				if (scLoaReport.getDefinationType().getParameterId().equals(
						(getParameterByTypeAndCode(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_SYSTEM_DEFINED).getParameterId()))) {
					fileName = getTemplateFileName(scLoaReport.getPmDocument().getDocDefinitionKey());
					filePath = tempDirectory + "/" + attachmentFormat.format(fileName, null);
					if (scLoaReport.getTitle().equalsIgnoreCase(ContractConst.TEMPLATE_JADUAL_HARGA)) {
						formJadualHargaExcel(filePath);
						fileList.add(new File(filePath));
					} else {
						String docDefinitionKey = scLoaReport.getPmDocument().getDocDefinitionKey();
						content = getContent(docDefinitionKey);					
						DocumentGenerator.getInstance().generatePdf(new FileOutputStream(filePath), content);
						fileList.add(new File(filePath));
					}
				}
				// User Upload Doc
				else if (scLoaReport.getDefinationType().getParameterId().equals(
						(getParameterByTypeAndCode(ParameterConst.DOCUMENT_DEFINITION_TYPE, 
								ParameterConst.DOCUMENT_DEFINITION_TYPE_UPLOAD).getParameterId()))) {
					if (scLoaReport.getScLoaReportAttachment().getFilePath() != null) {
						fileList.add(new File(scLoaReport.getScLoaReportAttachment().getFilePath()));
					}
				}
			}
			
			String zipFileName = "NGeP-LOA-Report.zip";
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
			downloadFile(DocumentRepositoryUtils.getDBFilePath(zipFile.getPath().replace("\\", "/")), zipFileName);
		} catch(Exception exp) {
			exp.printStackTrace();
		}
	}
	
	/**
	 * The method used to retrieve filename of the file to download
	 * according to template type.
	 * 
	 * @return the filename
	 */
	private String getTemplateFileName(String docDefinitionKey) {		
		if (ContractConst.DOC_DEF_KEY_CONTRACT_SUMMARY.equals(docDefinitionKey)) {
			return "AmendmentRingkasanKontrakPage.pdf";
		} else if (ContractConst.DOC_DEF_KEY_INTEGRITY_PACT.equals(docDefinitionKey)) {
			return "AmendmentIntegrityPactPage.pdf";
		} else if (ContractConst.DOC_DEF_KEY_JADUAL_HARGA.equals(docDefinitionKey)) {
			return "AmendmentJadualHargaPage.xlsx";
		} else if (ContractConst.DOC_DEF_KEY_FORMAT_BOND_EXECUTION.equals(docDefinitionKey)) {
			return "AmendmentBonPelaksanaanPage.pdf";
		}else if (ContractConst.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equals(docDefinitionKey)) {
			return "AmendmentSuratPengesahanSetujuTerimaPembekalPage.pdf";
		}		
		return null;
	}
	
	/**
	 * The method used for generating the content of loa reports
	 * that defined from template.
	 * 
	 * @param docDefinitionKey  the defination key for different report. Different key will have different template.
	 * @return the complete content of the template
	 * @throws Exception        if an exception occurred
	 * @see    Exception
	 */	
	private String getContent(String docDefinitionKey) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		VelocityContext context = null;
		String vmFile = null;
		String content = null;
		CommonUtils commonUtils = new CommonUtils();
		SimpleDateFormat dateSDF = new SimpleDateFormat(commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE));
		DecimalFormat percentageDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_PERCENTAGE));
		DecimalFormat amountDF = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		DecimalFormat amountNoCommaDF = new DecimalFormat("#.00");
		
		// Ringkasan Kontrak
		if (ContractConst.DOC_DEF_KEY_CONTRACT_SUMMARY.equals(docDefinitionKey)) {
			context = new VelocityContext();	
			vmFile = ContractConst.DOCPATH_CT_LOA_CONTRACT_SUMMARY;
			context.put("fileNo", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getScLoiLoa().getFileNo() + "-" + ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getFileExtNo());
			context.put("sourcingTitle",  ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getScLoiLoa().getTitle());
			context.put("successfulSupplierName", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
			
			BigDecimal contractValue = ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount() != null 
					? ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().getContractAmount() : BigDecimal.ZERO;			
			context.put("contractValue", amountDF.format(contractValue));
				
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount() != null) {
				contractValue = contractValue.add(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAdditionalAmount());
			}				
			context.put("newContractValue", amountDF.format(contractValue));
			
			if (isContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				context.put("newGSTAmout", amountDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount()));
				context.put("newOverallAmt", amountDF.format(contractValue.add(
						ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getNewGstAmount())));
			} else {
				context.put("newGSTAmout", "T/B");
				context.put("newOverallAmt", amountDF.format(contractValue));
			}			
						
			String fulfilmentType = getParameterDesc(ctBackingBean.getManagedBean().getContractVer().getCtContract().getFulfillmentType());
			fulfilmentType = fulfilmentType.replaceAll("&", "&#38;");
			context.put("fulfilmentType", fulfilmentType);
				
			double deductionAmount = 0;				
			for (CtDeduction deduction : ctBackingBean.getManagedBean().getContractVer().getCtDeductions()) {
				if (deduction.getDeductionType().getParameterCode().equals(ParameterConst.DEDUCTION_TYPE_FIXED)) {
					deductionAmount = deductionAmount + deduction.getAmount();
				}
			}				
			context.put("deductionAmount", amountDF.format(deductionAmount));				
			context.put("months", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getContractPeriod() + " bulan");
			context.put("contractEffDate", df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getEffDate()));
			context.put("contractExpDate", df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate()));
			
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().isContractExtend()) {
				context.put("extendMonths",ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendPeriod() + " bulan");
				context.put("extStartDate", df.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendStartDate()));
				context.put("extEndDate", df.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate()));
			} else {
				context.put("extendMonths","");
				context.put("extStartDate", "");
				context.put("extEndDate", "");
			}
			context.put("amendEffExpDate", df.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate()));
													
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().isBondReq()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
				context.put("bondValue",ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt() != null  
						? amountDF.format(ctBackingBean.getManagedBean().getContractVer().getCtContract().getLoa().getBondAmt()) : amountDF.format(BigDecimal.ZERO));					
				context.put("amendBondValue", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt() != null 
						? amountDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getBondAmt()) : amountDF.format(BigDecimal.ZERO));
			} else {
				context.put("bondValue","TB");
				context.put("amendBondValue","TB");
			}
		}
		// Integrity Pact (Akuan Pembida Berjaya)
		else if(ContractConst.DOC_DEF_KEY_INTEGRITY_PACT.equalsIgnoreCase(docDefinitionKey)) {
			FacesScopes fs = new FacesScopes();
			String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();				
			context = new VelocityContext();	
			context.put("hostUrl", hostUrl);
			context.put("ptjName", ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgName());
			context.put("amendmentTitle", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getTitle());
			context.put("contractNo", ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			context.put("amendmentRefNo", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getFileRefNo());				
			vmFile = ContractConst.DOCPATH_CT_INTEGRITY_PACT;
			
			if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(
					CTProcessNameConst.PREPARE_LOA_HT_NAME)) {
				context.put("supplierName", "________________");
				context.put("supplierMofRegNo", "________________");
				context.put("supplierAddress","");
				context.put("supplierPhoneNo","");
				context.put("supplierFaxNo","");
				context.put("supplierUserName", "________________");
				context.put("supplierUserIcNo", "________________");
				context.put("supplier",false);
			} else {
				if (getManagedBean().getTaskName() != null 
						&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
					context.put("supplierUserName", getFacesScopes().getUserSessionBean().getUserName());
					context.put("supplierUserIcNo", getFacesScopes().getUserSessionBean().getIdentificationNo());
				} else {
					context.put("supplierUserName", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy().getUserName());
					context.put("supplierUserIcNo", ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa().getAckBy().getIdentificationNo());
				}
				context.put("supplierName", ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierName());
				context.put("supplierMofRegNo", ctBackingBean.getManagedBean().getContractVer().getCtContract().getSmSupplier().getRegNo());
				
				StringBuilder sb = new StringBuilder();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress1());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress2());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity() != null
							? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity().getCityName() : "");
					sb.append(" ");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPostcode());
					sb.append(", <br/>");
					sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState() != null 
							? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState().getStateName() : "");
				}
				String supplierAddress = sb.toString();
				supplierAddress = supplierAddress.replaceAll("&", "&#38;");
				context.put("supplierAddress", supplierAddress);
				
				context.put("supplierPhoneNo",ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo() != null 
						? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierPhoneNo() : "");
				context.put("supplierFaxNo",ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo() != null 
						? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierFaxNo() : "");
				context.put("supplier",true);
			}
		}
		// Format Bon Pelaksanaan
		else if (ContractConst.DOC_DEF_KEY_FORMAT_BOND_EXECUTION.equals(docDefinitionKey)) {
			vmFile = ContractConst.DOCPATH_CT_LOA_BOND_LETTER;
			context = new VelocityContext();
			int type = 1;
			context.put("type", type);
			context.put("contractNo", "...........................");
			context.put("supplierName", "...........................");
			context.put("sourcingTitle", "...........................");
			context.put("bondAmount", "..............................");
			context.put("bondAmountWord", "..................................................");
			context.put("expiryPeriod", "..........................");	
		}
		// Surat Pengesahan Setuju Terima Pembekal
		else if (ContractConst.DOC_DEF_KEY_SUPPLIER_AGREEMENT_LETTER.equals(docDefinitionKey)) {
			FacesScopes fs = new FacesScopes();
			String hostUrl = fs.getRequest().getServerName() +":" + fs.getRequest().getServerPort();
			vmFile = ContractConst.DOCPATH_CT_LOA_AGREEMENT_LETTER;
			ScLoa scLoa = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentLoa();
			ScLoiLoa scLoiLoa = scLoa.getScLoiLoa();
			context = new VelocityContext();
			PmOrgProfile pmOrgProfile = ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile();
			IAddressSO addressSO = null;
			PmAddressType pmAddressType = null;
			
			try {
				addressSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.PROFILE_MANAGEMENT, IAddressSO.class);
				AddressReqDTO reqDTO = new AddressReqDTO();
				reqDTO.setOrgProfileId(pmOrgProfile.getOrgProfileId());
				reqDTO.setAddressType(AddressType.CONTACT_ADDRESS.value());
				AddressResDTO resDTO = addressSO.retrieveAddressTypeList(reqDTO);

				if (resDTO.getAddressTypeList().size() > 0) {
					pmAddressType = resDTO.getAddressTypeList().get(0);
				}
				
				context.put("ptjName", pmOrgProfile.getOrgName());
				String jabtanName = retrieveJabatanByOrgProfileId(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				StringBuffer  ministryJabatan = new StringBuffer();
				if(!jabtanName.isEmpty()) {
				      ministryJabatan.append(jabtanName);
				      ministryJabatan.append(" / ");
				}
			
				PmOrgProfile ministryOrgProfile = retrieveMinistryOrgProfile(ctBackingBean.getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
				if(ministryOrgProfile!=null) {
					ministryJabatan.append(ministryOrgProfile.getOrgName());
				}				
				context.put("ministryJabatanName", ministryJabatan );
			} finally {
				addressSO = null;
			}
			context.put("hostUrl", hostUrl);
			StringBuilder ptjAddress = new StringBuilder();
			String phoneNo = "";
			String faxNo = "";
				
			if (pmAddressType != null) {
				PmAddress pmAddress = pmAddressType.getPmAddress();
				ptjAddress.append(pmAddress.getAddress1());
				ptjAddress.append(",<br />");
				ptjAddress.append(pmAddress.getAddress2());
				ptjAddress.append(",<br />");
				ptjAddress.append(pmAddress.getPostcode());
				ptjAddress.append(" ");
				ptjAddress.append(pmAddress.getPmCity() != null ? pmAddress.getPmCity().getCityName() : "");
				ptjAddress.append(",<br />");
				ptjAddress.append(pmAddress.getPmState() != null ? pmAddress.getPmState().getStateName() : "");
				
				phoneNo = pmAddress.getPhoneNo();
				faxNo = pmAddress.getFaxNo();
			}
			context.put("ptjAddress", ptjAddress.toString());
			context.put("phoneNo", phoneNo);
			context.put("faxNo", faxNo);
			context.put("fileNo", scLoiLoa.getFileNo() + "-" + scLoa.getFileExtNo());
			context.put("loaNo", scLoa.getLoaNo());
				
			String issueDate = "";
			if (scLoa.getIssueDate() != null) {
				issueDate = dateSDF.format(scLoa.getIssueDate());
			}
			context.put("issueDate", issueDate);
			context.put("supplierName", scLoiLoa.getSupplierName());
			
			StringBuilder sb = new StringBuilder();
			if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress() != null) {
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress1());
				sb.append(", <br/>");
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress2());
				sb.append(", <br/>");
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getAddress3());
				sb.append(", <br/>");
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPostcode());
				sb.append(" ");
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity() != null
						&& !ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity().getCityCode().equals("000000")
							? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmCity().getCityName() : "");
				sb.append(", <br/>");
				sb.append(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState() != null 
						? ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getSupplierAddress().getPmState().getStateName() : "");

			}
			String supplierAddress = sb.toString();
			supplierAddress = supplierAddress.replaceAll("&", "&#38;");
			context.put("supplierAddress", supplierAddress);			
			context.put("contractNo",ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractNo());
			
			String procurementMode = "";
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_DIRECT_NEGO)) {
				procurementMode = "Tawaran Pelawaan Harga";
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_QUOTATION)) {
				procurementMode = "Sebut Harga";
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getProcurementMode().getParameterCode().equalsIgnoreCase(ParameterConst.PROCUREMENT_MODE_TENDER)) {
				procurementMode = "Tender";
			}
			context.put("procurementMode", procurementMode);
			
			if (scLoa.getBondAmt() != null && scLoa.isBondReq()
					&& !ctBackingBean.getManagedBean().getContractVer().getCtContractAmount().isBondExempted()) {
				context.put("bondAmt200k", true);
			
				String bondAmt = "";
				if (scLoa.getBondAmt() != null) {
					bondAmt = amountDF.format(scLoa.getBondAmt());
				}
				context.put("bondAmt", bondAmt);
				context.put("bondAmtText", scLoa.getBondAmt() != null && scLoa.getBondAmt().doubleValue()!=0 ? NumberWordUtil.convert(amountNoCommaDF.format(scLoa.getBondAmt()) ) : ""); 
				
				String bondPercentage = "";
				if (scLoa.getBondPercentage() != null) {
					bondPercentage = percentageDF.format(scLoa.getBondPercentage());
				}
				context.put("bondPercentage", bondPercentage);
				Date bondExpDate = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getExpDate();
				if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate() != null) {
					bondExpDate = ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getExtendEndDate();
				}
				Calendar bondDate = Calendar.getInstance();
				bondDate.setTime(bondExpDate);
				bondDate.add(Calendar.YEAR, 1);
				context.put("bondExpDate", dateSDF.format(bondDate.getTime())); 
	
				//TODO #3271: the government want this to be configured; hence this need to be set in PM but at the moment default this to 30
				String bondResDueDays = "30";
				context.put("bondResDueDays", bondResDueDays);
			} else {
				context.put("bondAmt200k", false);
			}
			
			context.put("contractWithGst", isContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate()));
			String gstRegNo = "TB";
			String gstEffDate = "TB";
			String gstDeclareDate = "TB";
			BigDecimal overallAmt = scLoa.getLoaAmt();
			
			if (isContractWithGst(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				context.put("contractAmt", amountDF.format(overallAmt));
				context.put("contractAmtTxt", (scLoa.getLoaAmt() != null && scLoa.getLoaAmt().doubleValue() != 0) ? NumberWordUtil.convert(amountNoCommaDF.format(overallAmt)) : ""); 
				
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstRegNo() != null)
					gstRegNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstRegNo();
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate() != null)
					gstEffDate = df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstEffDate());
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstDeclaredDate() != null)
					gstDeclareDate = df.format(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getGstDeclaredDate());
				
				if (scLoa.getGstAmt() != null) {
					context.put("gstAmt", amountDF.format(scLoa.getGstAmt()));
					context.put("gstAmtTxt", scLoa.getGstAmt().doubleValue() != 0 ? NumberWordUtil.convert(amountNoCommaDF.format(scLoa.getGstAmt())) : ""); 
					overallAmt = scLoa.getLoaAmt().add(scLoa.getGstAmt());
				} else {
					context.put("gstAmt", "TB");
					context.put("gstAmtTxt", "TB");
				}
			}
			
			context.put("gstRegNo", gstRegNo);
			context.put("gstEffDate", gstEffDate);
			context.put("gstDeclareDate", gstDeclareDate);
			context.put("overallContractValue", amountDF.format(overallAmt));
			context.put("overallContractValueTxt", (scLoa.getLoaAmt() != null && scLoa.getLoaAmt().doubleValue() != 0) ? NumberWordUtil.convert(amountNoCommaDF.format(overallAmt)) : ""); 
			
						
			String contractEffDate = "";
			if (ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate() != null) {
				contractEffDate = dateSDF.format(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
			}
			context.put("contractEffDate", contractEffDate);
			if (scLoa.getContractSigner() != null) {
				context.put("contractSigner", scLoa.getContractSigner().getUserName());
				context.put("contractSignerDesignation", scLoa.getContractSigner().getDesignation());
			}
			
			String ackDate = "";
			String bankAccNo = "";
			String bankName = "";
			String bankBranch = "";
			String bankAddress = "";				
			String supplierUserName = "";
			String supplierUserID = "";
			String supplierUserDesignation = "";
			String supplierTelNo = "";
			String supplierFaxNo = "";
			String supplierMobileNo = "";
			String supplierEmail = "";
			String supplierCompName = "";
			String supplierCompAddress = "";				
			String witnessName = "";
			String witnessID = "";
			String witnessDesignation = "";
			String witnessAddress = "";
			String witnessTelNo = "";
			String witnessFaxNo = "";
			String witnessMobileNo = "";
			String witnessEmail = "";
			
			if (ContractStatusConst.FA_PENDING_LOA_ACKNOWLEDGEMENT.equals(
					ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) {
				ackDate = dateSDF.format(new Date());	
				// Bank account details.
				bankAccNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
				if (bankAccNo == null) {
					bankAccNo = "";
				}
				bankName = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName();
				if (bankName == null) {
					bankName = "";
				}
				bankBranch = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankBranch();
				if (bankBranch == null) {
					bankBranch = "";
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress() != null) {
					bankAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress());
					bankAddress = bankAddress.replaceAll("\r\n", "<br />");
					bankAddress = bankAddress.replaceAll("&", "&#38;");
				}
				// Supplier User
				supplierUserName = getFacesScopes().getUserSessionBean().getUserName();
				supplierUserID = getFacesScopes().getUserSessionBean().getIdentificationNo();
				supplierUserDesignation = getFacesScopes().getUserSessionBean().getDesignation() != null
						? getFacesScopes().getUserSessionBean().getDesignation(): "";
				supplierTelNo = getFacesScopes().getUserSessionBean().getPhoneNo() != null
						? getFacesScopes().getUserSessionBean().getPhoneNo() : "";
				supplierFaxNo = getFacesScopes().getUserSessionBean().getFaxNo() != null
						? getFacesScopes().getUserSessionBean().getFaxNo() : "";
				supplierMobileNo = getFacesScopes().getUserSessionBean().getMobileNo() != null
						? getFacesScopes().getUserSessionBean().getMobileNo() : "";
				supplierEmail = getFacesScopes().getUserSessionBean().getEmail() != null
						? getFacesScopes().getUserSessionBean().getEmail() : "";
				supplierCompName = scLoiLoa.getSupplierName();
				supplierCompAddress = supplierAddress;
				// Witness
				witnessName = getManagedBean().getScLoaWitness().getWitnessName();
				witnessID = getManagedBean().getScLoaWitness().getIdentificationNo();
				witnessDesignation = getManagedBean().getScLoaWitness().getDesignation();
				witnessAddress = getManagedBean().getScLoaWitness().getAddress();
				witnessTelNo = getManagedBean().getScLoaWitness().getPhoneNo();
				witnessFaxNo = getManagedBean().getScLoaWitness().getFaxNo();
				witnessMobileNo = getManagedBean().getScLoaWitness().getMobileNo();
				witnessEmail = getManagedBean().getScLoaWitness().getEmail();
			} else if (ContractStatusConst.FA_PENDING_VERIFICATION.equals(
					ctBackingBean.getManagedBean().getContractVer().getStatus().getStatusId())) {
				ackDate = dateSDF.format(scLoa.getAckDate());
				// Bank account details.
				bankAccNo = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAccountNo();
				if (bankAccNo == null) {
					bankAccNo = "";
				}
				bankName = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankName();
				if (bankName == null) {
					bankName = "";
				}
				bankBranch = ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankBranch();
				if (bankBranch == null) {
					bankBranch = "";
				}
				if (ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress() != null) {
					bankAddress = getFullAddress(ctBackingBean.getManagedBean().getContractVer().getCtContractValue().getBankAddress());
					bankAddress = bankAddress.replaceAll("\r\n", "<br />");
					bankAddress = bankAddress.replaceAll("&", "&#38;");
				}
				// Supplier User
				supplierUserName = scLoa.getAckBy().getUserName();
				supplierUserID = scLoa.getAckBy().getIdentificationNo();
				supplierUserDesignation = scLoa.getAckBy().getDesignation() != null ? scLoa.getAckBy().getDesignation() : "";
				supplierTelNo = scLoa.getAckBy().getPhoneNo() != null ? scLoa.getAckBy().getPhoneNo() : "";
				supplierFaxNo = scLoa.getAckBy().getFaxNo() != null ? scLoa.getAckBy().getFaxNo() : "";
				supplierMobileNo = scLoa.getAckBy().getMobileNo() != null ? scLoa.getAckBy().getMobileNo() : "";
				supplierEmail = scLoa.getAckBy().getEmail() != null ? scLoa.getAckBy().getEmail() : "";
				supplierCompName = scLoiLoa.getSupplierName();
				supplierCompAddress = supplierAddress;					
				// Witness
				ScLoaWitness supplierWitness = null;
				for (ScLoaWitness scLoaWitness : scLoa.getScLoaWitnesses()) {
					if (ParameterConst.WITNESS_PARTY_TYPE_SUPPLIER.equals(scLoaWitness.getPartyType())) {
						supplierWitness = scLoaWitness;
						break;
					}
				}
				if (supplierWitness != null) {
					witnessName = supplierWitness.getWitnessName();
					witnessID = supplierWitness.getIdentificationNo();
					witnessDesignation = supplierWitness.getDesignation();
					witnessAddress = supplierWitness.getAddress();
					witnessTelNo = supplierWitness.getPhoneNo();
					witnessFaxNo = supplierWitness.getFaxNo();
					witnessMobileNo = supplierWitness.getMobileNo();
					witnessEmail = supplierWitness.getEmail();
				}
			}
			context.put("ackDate", ackDate);
			context.put("bankAccNo", bankAccNo);
			context.put("bankName", bankName.replaceAll("&", "&#38;"));
			context.put("bankBranch", bankBranch);
			context.put("bankAddress", bankAddress);
			// Supplier User
			context.put("supplierUserName", supplierUserName);
			context.put("supplierUserID", supplierUserID);
			context.put("supplierUserDesignation", supplierUserDesignation);
			context.put("supplierTelNo", supplierTelNo);
			context.put("supplierFaxNo", supplierFaxNo);
			context.put("supplierMobileNo", supplierMobileNo);
			context.put("supplierEmail", supplierEmail);
			context.put("supplierCompName", supplierCompName);
			context.put("supplierCompAddress", supplierCompAddress);
			// Witness
			context.put("witnessName", witnessName);
			context.put("witnessID", witnessID);
			context.put("witnessDesignation", witnessDesignation);
			context.put("witnessAddress", witnessAddress);
			context.put("witnessTelNo", witnessTelNo);
			context.put("witnessFaxNo", witnessFaxNo);
			context.put("witnessMobileNo", witnessMobileNo);
			context.put("witnessEmail", witnessEmail);				
		}
		if (vmFile != null && context != null) {
			content = DocumentGenerator.getInstance().getHtmlFromVelocity(vmFile, context);
		}
		return content;
	}
	
	/**
	 * The method used for generating the excel sheet for Item products and services.
	 * 
	 * @param filePath  will be download file path
	 */	
	private void formJadualHargaExcel(String filePath) {
		ContractAmendmentReqDTO reqDto = new ContractAmendmentReqDTO();
		reqDto.setContractVerId(this.ctBackingBean.getManagedBean().getContractVer().getContractVerId());
		reqDto.setZonal(ctBackingBean.getManagedBean().getContractVer().getCtContract().isZonalPricing());
		reqDto.setItemType(ParameterConst.ITEM_TYPE_PRODUCT);
		IContractAmendmentSO amendSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);
		ContractAmendmentResDTO resDto = amendSO.getItemList(reqDto);
		List<CtItemPrice> productList = resDto.getItemPrices();
		
		reqDto.setItemType(ParameterConst.ITEM_TYPE_SERVICE);
		resDto = amendSO.getItemList(reqDto);
		List<CtItemPrice> serviceList = resDto.getItemPrices();
		
		List<JadualHargaBean> productJadualHargaList = new ArrayList<>();
		List<JadualHargaBean> serviceJadualHargaList = new ArrayList<>();
		JadualHargaBean jadualHarga = null;
			        	
		for (CtItemPrice productItemPrice : productList) {
		    IContractItemSO contractItemSO = (IContractItemSO) BusinessDelegate.getInstance().getServiceObject(
		    		ModuleCodeConst.CONTRACT_MANAGEMENT, IContractItemSO.class);
			ContractItemReqDTO reqDTO = new ContractItemReqDTO();
			reqDTO.setContractItemId(productItemPrice.getCtContractItem().getContractItemId());
			reqDTO.setEffDate(this.ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
			if (productItemPrice.getCtContractZone() != null) {
				reqDTO.setContractZoneId(productItemPrice.getCtContractZone().getContractZoneId());
			}
			ContractItemResDTO contractResDTO = contractItemSO.getMaxQuantity(reqDTO);
			CtItemMaxQty ctItemMaxQty = contractResDTO.getCtItemMaxQuantity();
			productItemPrice.setMaxQuantity(ctItemMaxQty.getMaxQuantity());
			jadualHarga = mappingJadualHarga(productItemPrice, ParameterConst.ITEM_TYPE_PRODUCT);
			productJadualHargaList.add(jadualHarga);
		}		
		for (CtItemPrice serviceItemPrice : serviceList) {
			jadualHarga = mappingJadualHarga(serviceItemPrice, ParameterConst.ITEM_TYPE_SERVICE);
			serviceJadualHargaList.add(jadualHarga);
		}
		if (!productList.isEmpty() || !serviceList.isEmpty())
		generateJadualHarga(filePath, productJadualHargaList, serviceJadualHargaList);
	}
	
	/**
	 * The method used to retrieve the price information of the
	 * item products and services.
	 * 
	 * @param itemPrice     an object of item price
	 * @param type          item type, 1:product item, 2:serive item
	 * @return jadualHarga  a serialized object of jadual harga (excel sheet)
	 */
	private JadualHargaBean mappingJadualHarga(CtItemPrice itemPrice, String type) {
		JadualHargaBean jadualHarga = new JadualHargaBean();
		CommonUtils commonUtils = new CommonUtils();
		DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
		jadualHarga.setZone(itemPrice.getCtContractZone() != null ? itemPrice.getCtContractZone().getZoneName() : "N/A");
		jadualHarga.setItem(itemPrice.getCtContractItem().getScRequestItem().getItemName());
		jadualHarga.setUnitPrice(nf.format(itemPrice.getUnitPrice().longValue()));
		jadualHarga.setPriceType(itemPrice.getCtContractItem().getPriceType().getParameterCode());
		if (itemPrice.getCtContractItem().getScRequestItem().getUom() != null) {
			jadualHarga.setUom(itemPrice.getCtContractItem().getScRequestItem().getUom().getUomName());
		}
		// Product Item
		if (type.equalsIgnoreCase(ParameterConst.ITEM_TYPE_PRODUCT)) {			
			if (itemPrice.getFromQty() != null && itemPrice.getToQty() != null) {
				jadualHarga.setTier(itemPrice.getFromQty() + " to " + itemPrice.getToQty());
			}
			jadualHarga.setQuantity(itemPrice.getMaxQuantity() != null ? itemPrice.getMaxQuantity().toString() : "N/A");
			jadualHarga.setSubSequence(itemPrice.getSubSequence() != null ? itemPrice.getSubSequence() + "" : "N/A");
		}
		// Service Item
		else if(type.equalsIgnoreCase(ParameterConst.ITEM_TYPE_SERVICE)) {
			jadualHarga.setFreqUom(null == itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomFrequency()
					? "N/A" : itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomFrequency().toString());
	    	jadualHarga.setUomPerDay(null == itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerDay()
	    			? "N/A" : itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerDay().toString());
	    	jadualHarga.setUomPerMonth(null == itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerMonth()
					? "N/A" : itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClUomPerMonth().toString());
	    	jadualHarga.setUomPerYear(null == itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClDurationMonth()
					? "N/A" : itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClDurationMonth().toString());
	    	jadualHarga.setQuantity(null == itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClQuantity()
	    			? "N/A" : itemPrice.getCtContractItem().getScRequestItem().getTplSpecQuestion().getClQuantity().toString());
		}
		return jadualHarga;		
	}
	
	/**
	 * The method used for generating the excel sheet for item products and services.
	 * 
	 * @param filePath  will be download file path
	 * @param productList the list of product item to be generated in excel sheet
	 * @param serviceList the list of service item to be generated in excel sheet
	 */	
	private void generateJadualHarga(String filePath, List<JadualHargaBean> productList, List<JadualHargaBean> serviceList) {
		try {
			final String templatePath = "templates" + ContractConst.DOCPATH_CT_LOA_JADUAL_HARGA;
		    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
		    if (inputStream != null) {
				XSSFWorkbook wb = new XSSFWorkbook(inputStream); //or new HSSFWorkbook();		     
		        Sheet productSheet = null;
		        Sheet serviceSheet = null;
		        boolean emptyProductList = false;
		        Row row = null;
		        int rowIndex = 0;
		        int cellIndex = 0;
		        boolean zoneExist = false;
		        // Product Item
		        if (!CollectionUtils.isEmpty(productList)) {
		        	rowIndex = 0;
		        	productSheet = wb.getSheetAt(0);
		        	wb.setSheetName(0, "ProductSheet");
		        	for (JadualHargaBean product : productList) {
		        		if (!product.getZone().equals("N/A")) {
		        			zoneExist = true;
		        			break;
		        		}
		        	}
		        	for (JadualHargaBean product : productList) {
		        		rowIndex ++;
		        		cellIndex = 0;
		        		row = productSheet.createRow(rowIndex);
		        		row.createCell(cellIndex++).setCellValue(product.getZone());
		        		row.createCell(cellIndex++).setCellValue(product.getItem());
		        		row.createCell(cellIndex++).setCellValue(product.getUom());
		        		row.createCell(cellIndex++).setCellValue(product.getTier());
		        		row.createCell(cellIndex++).setCellValue(product.getSubSequence());
		        		row.createCell(cellIndex++).setCellValue(product.getQuantity());
		        		row.createCell(cellIndex++).setCellValue(product.getUnitPrice());
		        	}
		            //Auto size all the columns
			        for (int x = 0; x < productSheet.getRow(0).getPhysicalNumberOfCells(); x++) {
			        	productSheet.autoSizeColumn(x);
			        }
			        //Hide column by price type
			        productSheet.setColumnHidden(3, true);
			        productSheet.setColumnHidden(4, true);
		        	if (productList.get(0).getPriceType().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TARIFF) 
		        			|| productList.get(0).getPriceType().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TARIFF)) {
		        		productSheet.setColumnHidden(3, false);
		        		productSheet.setColumnHidden(4, false);
		        	}
		        	if (productList.get(0).getPriceType().equalsIgnoreCase(ParameterConst.PRICE_TYPE_TIER) 
		        			|| productList.get(0).getPriceType().equalsIgnoreCase(ParameterConst.PRICE_TYPE_ZONAL_TIER)) {
				        productSheet.setColumnHidden(3, false);
		        	}
		        	// Hide zone column if all N/A
		        	if (!zoneExist) {
		        		productSheet.setColumnHidden(0, true);
		        	}
		        } else {
		        	wb.removeSheetAt(0);
		        	emptyProductList = true;
		        }
		        // Serive Item
		        if (!CollectionUtils.isEmpty(serviceList)) {
		        	rowIndex = 0;
		        	if (emptyProductList) {
		        		serviceSheet = wb.getSheetAt(0);
		        		wb.setSheetName(0, "ServiceSheet");
		        	} else {
		        		serviceSheet = wb.getSheetAt(1);
		        		wb.setSheetName(1, "ServiceSheet");
		        	}		        	
		          	for (JadualHargaBean service : serviceList) {
		        		if (!service.getZone().equals("N/A")) {
		        			zoneExist = true;
		        			break;
		        		}
		        	}		          	
		        	for (JadualHargaBean service : serviceList) {
		        		rowIndex ++;
		        		cellIndex =0;
		        		row = serviceSheet.createRow(rowIndex);
		        		row.createCell(cellIndex++).setCellValue(service.getZone());
		        		row.createCell(cellIndex++).setCellValue(service.getItem());
		        		row.createCell(cellIndex++).setCellValue(service.getUom());
		        		row.createCell(cellIndex++).setCellValue(service.getFreqUom());
		        		row.createCell(cellIndex++).setCellValue(service.getUomPerDay());
		        		row.createCell(cellIndex++).setCellValue(service.getUomPerMonth());
		        	//	row.createCell(cellIndex++).setCellValue(service.getUomPerYear());
		        		row.createCell(cellIndex++).setCellValue(service.getQuantity());
		        		row.createCell(cellIndex++).setCellValue(service.getUnitPrice());
		        	}
		        	for (int x = 0; x < serviceSheet.getRow(0).getPhysicalNumberOfCells(); x++) {
		 	        	serviceSheet.autoSizeColumn(x);
		 	        }
		        	// Hide zone column if all N/A
		        	if (!zoneExist) {
		        		 serviceSheet.setColumnHidden(0, true);
			        }
		        } else {
		        	wb.removeSheetAt(1);
		        }
		        wb.setActiveSheet(0);
		        // Write the output to a file
		        FileOutputStream fileOut = new FileOutputStream(filePath);
		        wb.write(fileOut);
		        fileOut.close();
		    } else {
		    	 throw new EpException("Template file doesn't exist");
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The method used to retrieve proper portal name from property file.
	 * 
	 * @return portletTitle  the name of the portal
     */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.amendment", 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}
    
    /**
   	 * The method used to retrieve yesterday date from system.
   	 *  
   	 * @return  yesterday date
   	 */
    private Calendar getPreviousDate() {
		Calendar previousDate = Calendar.getInstance();
		previousDate.setTime(ctBackingBean.getManagedBean().getContractVer().getCtAmendment().getAmendmentEffDate());
		previousDate.add(Calendar.DATE, -1);
		return previousDate;
	}
	
	/**
   	 * The method to used auto update of request.
   	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}
			
	/**
	 * The Method used to set user role as per user login.
	 * This user role is required to identify the task.
	 * 
	 * @return the role of login user
	 */
	private String getUserRole() {
		String userRole = null;	
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PREPARE_LOA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.VERIFY_FA_HT_NAME)) {
			if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)
					&& getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)
					&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();			
			}
		} else {
			if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FA_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ENDORSE_LOA_HT_NAME)) {
				if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_MINISTRY)
						&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
					userRole = RoleCode.CT_APPROVER.value();
				} else if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
						ParameterConst.CONTRACT_TYPE_CENTRAL)
						&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
					userRole = RoleCode.BPK_CC_APPROVER.value();
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_BREACHED_FA_HT_NAME)) {
				if (getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value())) {
					userRole = RoleCode.BPK_APPROVER.value();
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.ACK_FA_HT_NAME)) {
				if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())) {
					userRole = RoleCode.MOF_SUPPLIER_USER.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
					userRole = RoleCode.MOF_SUPPLIER_ADMIN.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())) {
					userRole = RoleCode.BASIC_SUPPLIER_USER.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())) {
					userRole = RoleCode.BASIC_SUPPLIER_ADMIN.value();
				} else if (getFacesScopes().hasRoleUser(RoleCode.GOVT_SELLER.value())) {
					userRole = RoleCode.GOVT_SELLER.value();
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CONFIRM_FA_MEETING_HT_NAME)
					|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.UPDATE_FA_RESULT_HT_NAME)) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
			   			|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_SECRETARY.value())) {
						userRole = RoleCode.FPB_SECRETARY.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_SECRETARY";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CHAIR_PERSON_SIGN_OFF_HT_NAME)) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
			   			|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_CHAIRPERSON.value())) {
						userRole = RoleCode.FPB_CHAIRPERSON.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_CHAIRPERSON";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			} else if (getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SIGN_OFF_HT_NAME)) {
				if (getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH) 
			   			|| getManagedBean().getCommitteeType().equalsIgnoreCase(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					if (getFacesScopes().hasRoleUser(RoleCode.FPB_MEMBER.value())) {
						userRole = RoleCode.FPB_MEMBER.value();
					}
				} else {
					String roleCode = getManagedBean().getCommitteeType() + "_MEMBER";
					if (getFacesScopes().hasRoleUser(roleCode)) {
						userRole = roleCode;
					}
				}
			}
		}
		
		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}
		
	/**
	 * The method used to check whether approver setting tab menu is required to display.
	 * 
	 * @return true, if approver setting tab menu is required to display
	 *         false, if approver setting tab menu is not required
     */
	public boolean isApproverSetting() {
		if (isContractAdmin() && (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FA_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FA_HT_NAME))) {
			return true;
		} else {
			return false;
		}
	}
    
	/**
     * The method used to verify the login user is BPK approver.
	 * 
	 * @return true, if login user is BPK approver
	 *         false, if login user is not BPK approver
     */
    public boolean isBpkApprover() {
		return getFacesScopes().hasRoleUser(RoleCode.BPK_APPROVER.value());
	}
    
	/**
	 * The method used to verify the login user is central contract 
	 * administrator or ministry administrator.
	 * 
	 * @return true, if login user is central contract administrator or ministry administrator
	 *         false, if login user is not central contract administrator or ministry administrator
     */
    public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) 
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}
	
	/**
     * The method used to check the accessibility of factoring tab menu.
	 * 
	 * @return true, if factoring tab menu is accessible
	 *         false, if factoring tab menu is not accessible
     */
	public boolean checkFactoringExpiryDate() {
    	if (ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring() != null 
    			&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus() != null
    			&& ctBackingBean.getManagedBean().getContractVer().getCtContract().getCtFactoring().getStatus().getStatusId().longValue() ==  ContractStatusConst.RF_COMPLETE.longValue()) {
    		return true;
    	}
    	return false;
    }
	
	/**
	 * Gets the days by rules.
	 *
	 * @param RuleType the rule type
	 * @return the days by rules
	 */
	private Integer getDaysByRules(String RuleType){
		Integer days = 0;
		IRuleSO ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
		RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleType);
		List<PmConfigRuleValue> ruleValueList = ruleSO.getRuleValuesByTypeInCurrentDate(ruleReqDTO).getRuleValueList();
		if (ruleValueList != null && ruleValueList.size() > 0) {
			PmConfigRuleValue configRuleValue = ruleValueList.get(0);
			days  = Integer.valueOf(configRuleValue.getRuleValue());
		}
		return days;
	}
}