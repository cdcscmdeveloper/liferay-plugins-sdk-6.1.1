/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.ct.termination;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.UploadedFile;

import com.liferay.portal.util.PortalUtil;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchMultiFilterBean;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.maintain.IContractAmendmentSO;
import my.ep.biz.service.ct.termination.IContractTerminationSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.ct.ContractStatusConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constants.CTProcessNameConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.maintain.ContractAmendmentReqDTO;
import my.ep.dto.ct.maintain.ContractAmendmentResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.ct.termination.CTContractTerminationDataType;
import my.ep.persistence.entity.ct.CtActor;
import my.ep.persistence.entity.ct.CtAddress;
import my.ep.persistence.entity.ct.CtContractVer;
import my.ep.persistence.entity.ct.dtl.CtAgency;
import my.ep.persistence.entity.ct.terminate.CtTermination;
import my.ep.persistence.entity.ct.terminate.CtTerminationReason;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.type.aut.SearchCondition;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.web.backingbean.ct.ContractBackingBean;
import my.ep.web.backingbean.ct.ContractBaseBackingBean;
import my.ep.web.backingbean.ct.amendment.AmendmentBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.ct.ContractManagementConst;
import my.ep.web.format.AttachmentDateFormat;
import my.ep.web.format.AttachmentFormat;
import my.ep.web.managedbean.ct.termination.TerminationManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.DocumentRepositoryUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.template.DocumentGenerator;

/**
 * Backing Bean For Contract Termination 
 * 
 * @author Alam
 * @since 1/13/2014
 */
@ManagedBean (name = "terminationBackingBean")
@ViewScoped
public class TerminationBackingBean extends ContractBaseBackingBean<TerminationManagedBean> {

	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(TerminationBackingBean.class);
	private static final String CONTRACT_MAINT_PAGE = "/xhtml/ct/fulfilmentdetails/search/search.xhtml?faces-redirect=true";
	private static final String PROCESS_TERMINATE_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationTab.xhtml?faces-redirect=true";
	private static final String TERMINATE_HISTORY_PAGE = "/xhtml/ct/fulfilmentdetails/termination/terminationHistory.xhtml?faces-redirect=true";

	private boolean viewNoticeFlag;
	private List<SelectItem> decisionTypeList;
	private ContractBackingBean ctBB = null;
	private CtTerminationReason[] selectedTerminationReasonsList;
	private CtAgency selectedRecommendedAgency;
	private IPagingDataModel<CtAgency> agencyDataModel = new AgencyPagingDataModel();	
	SimpleDateFormat dateformat = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
	/**
	 * This constructor method used to initialize the variable on start up
	 * Setting button if user not call from BPM
	 * Method used to initialize approver list
	 */
	public TerminationBackingBean() {

		if(ctBB == null){
			ctBB = new ContractBackingBean();
		}
		if(getManagedBean().getOutComes() == null)
			this.getManagedBean().setOutComes(new String[] {BpmOutcomeConst.BPM_SAVE, BpmOutcomeConst.BPM_SUBMIT});		

		if(getManagedBean().getUserMap().size()==0) {
			initializeApproverList();
		}
	}

	/**
	 * This  method used to initialize approver list
	 */
	private void initializeApproverList() {
		if(getManagedBean().getApproverModel() == null) {
			getManagedBean().setApproverModel(new DualListModel<PmUser>( new ArrayList<PmUser>(), new ArrayList<PmUser>()));
		}
	}

	/**
	 * @return the ctBB
	 */
	public ContractBackingBean getCtBB() {
		return ctBB;
	}

	/**
	 * @param ctBB the ctBB to set
	 */
	public void setCtBB(ContractBackingBean ctBB) {
		this.ctBB = ctBB;
	}

	/**
	 * This  method call once user navigate termination  page from task list
	 * Method used to retrieve all information from BPM
	 * Method used to retrieve contract information from BPM
	 * Method return null if task id null
	 */
	public void preRenderView(){

		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter(ContractConst.TASK_ID);
		if(taskId==null) {
			return;
		}

		CTContractTerminationDataType dataType =  super.getBpmTaskDetails(CTContractTerminationDataType.class, new String[]{BpmOutcomeConst.BPM_SAVE});		

		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setUpdateAction(false);
		reqDTO.setContractId(new Long(dataType.getContractId()));
		reqDTO.setContractVerId(new Long(dataType.getLatestVersionId()));
		reqDTO.setDocType(dataType.getDocType());
		ctBB.getManagedBean().setContractVerIdFromTaskList(new Long(dataType.getLatestVersionId())); //#15851
		getManagedBean().setUserLoginId(dataType.getContractAdmin().getUserName());
		if(getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_UPDATE_HT_NAME)){
			getManagedBean().setDisableField(true);
		}
		setTerminationContractVer(reqDTO);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ContractManagementConst.NAV_TO_CONTRACT_TERMINATION);
	}

	/**
	 * This  method used to set retrieve value from BPM or contract maintenance
	 */
	public void setTerminationContractVer(ContractReqDTO reqDTO) {
		IContractTerminationSO contractTerminationSO = null;
		ContractResDTO resDTO = null;
		if(ctBB.getManagedBean().getDeleteAttachmentList() != null ){
			ctBB.getManagedBean().getDeleteAttachmentList().clear();
		}
		try {
			contractTerminationSO = (IContractTerminationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractTerminationSO.class);
			resDTO = contractTerminationSO.retrieveTerminationContractVerDtls(reqDTO);
			getManagedBean().setContractVer(resDTO.getCtContractVer());
			getManagedBean().setCtContract(resDTO.getCtContractVer().getCtContract());
//			ctBB.renewSesionObject(); //#36196 - Fixing Bugs on Remark.
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					ParameterConst.DOCTYPE_CONTRACT_TERMINATION, getManagedBean().getCtContract().getContractNo()));
			ctBB = new ContractBackingBean(getManagedBean().getFileDirectory());	
			ctBB.getManagedBean().setContractVer(resDTO.getCtContractVer());
			//Retrieve agreement contract version for physical no & signed date
			retrieveContractVerOfAgreement();

			if(getManagedBean().getContractVer().getCtTermination() == null){
				getManagedBean().getContractVer().setCtTermination(new CtTermination());
				getManagedBean().getContractVer().getCtTermination().setDecisionType(new PmParameter());
			}

			if(getManagedBean().getUserMap().size()==0) {
				initializeApproverList();
			}
			if (isApproverSetting()) {
				getManagedBean().getApproverModel().setSource(
						getAvailableApproverList(getManagedBean().getCtContract().getContractType().getParameterCode(),
								getManagedBean().getUserMap()));

				if(resDTO.getApproverList() != null && resDTO.getApproverList().size() != 0){
					getManagedBean().getApproverModel().setTarget(resDTO.getApproverList());
					List<PmUser> invalidApproverList = new ArrayList<PmUser>();
					for (PmUser approverUser : resDTO.getApproverList())  {
						Iterator<PmUser> sourceIt = getManagedBean().getApproverModel().getSource().iterator();	
						boolean isValidApprover = false;
						while (sourceIt.hasNext()) {
							PmUser user = sourceIt.next();
							if (user.getUserId().longValue() == approverUser.getUserId().longValue()) {
								sourceIt.remove();
								isValidApprover = true;
								break;
							}
						}
						if (!isValidApprover) {
							invalidApproverList.add(approverUser);
						}
					}
					if (invalidApproverList.size() > 0) {
						getManagedBean().getApproverModel().getTarget().removeAll(invalidApproverList);
					}
				} else {
					getManagedBean().getApproverModel().setTarget(new ArrayList<PmUser>());
				}
			}

			getManagedBean().setRemarkList(resDTO.getCtRemarks());
			getManagedBean().setAttachmentList(resDTO.getCtAttachments());
			getManagedBean().setFileDirectory(DocumentRepositoryUtils.getUploadDirectoryPath(ModuleCodeConst.CONTRACT_MANAGEMENT, 
					getManagedBean().getContractVer().getDocType(), getManagedBean().getCtContract().getContractNo()));

			ctBB.getManagedBean().setViewContract(getManagedBean().isViewContract());
			ctBB.initializeRemarkAndAttachement(getManagedBean().getContractVer().getCtWorkflowStatus(), getManagedBean().getRemarkList(), getManagedBean().getAttachmentList()); 
			// Enable or Disable GST field
			if (!ctBB.isLoaContractWithGst(getManagedBean().getCtContract().getLoa().getGstEffDate())
					&& !ctBB.isLoaContractWithGst(getManagedBean().getContractVer().getCtContractValue().getGstEffDate())) {
				ctBB.getManagedBean().setGstCheckingGUI(false);	// Display N/A
			} else {
				if (getManagedBean().getContractVer().getCtContractAmount().getGstAmount() == null) {
					getManagedBean().getContractVer().getCtContractAmount().setGstAmount(BigDecimal.ZERO);
				}
				ctBB.getManagedBean().setGstCheckingGUI(true);
			}
			
			// Enable or Disable STAX field
				if (getManagedBean().getContractVer().getCtContractAmount().getStaxAmount() == null) {
					getManagedBean().getContractVer().getCtContractAmount().setStaxAmount(BigDecimal.ZERO);
				}
				ctBB.getManagedBean().setStaxCheckingGUI(true);
			
			
			flush();
			if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode() != null) {
				if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_REMEDY)){
					getManagedBean().setRemedy(true);
					getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.remedy", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				} else if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_TERMINATE)){
					getManagedBean().setTermination(true);
					getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.termination", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				} else if(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterCode().equalsIgnoreCase(ContractConst.TR_LETTER_MUTUAL)) {
					getManagedBean().setMutualTermination(true);
					getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.mutual", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				}
			}

			if(getManagedBean().getContractVer().getStatus().getStatusId().longValue()==ContractStatusConst.FT_PENDING_LETTER_PRINT_AND_UPDATE.longValue() 
					&& getManagedBean().getContractVer().getCtTermination().getNoticeDate()==null) {
				getManagedBean().getContractVer().getCtTermination().setNoticeDate(new Date());
			}
		}catch(Exception e){
			e.printStackTrace();

		} finally {
			contractTerminationSO = null;
			resDTO = null;
		}
	}
	/**
	 * This  method used for back button
	 */
	public String backAction() {
		String navigateUrl = (getManagedBean().getExternalPageUrl() != null) ? getManagedBean().getExternalPageUrl() : CONTRACT_MAINT_PAGE;
		getManagedBean().setExternalPageUrl(null);
		return navigateUrl;
	}

	/**
	 * The method used for retrieving the contract version 
	 * for agreement or non-agreement contract.
	 * If it's original agreement, return the first contract version.
	 * If it's supplementary agreement, return the amended contract version.
	 */
	public void retrieveContractVerOfAgreement() {
		IContractSO contractSO = null;
		IContractAmendmentSO amendmentSO = null;
		ContractResDTO resDTO = null;
		ContractAmendmentResDTO amendmentResDTO = null;

		try {
			contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);
			amendmentSO = (IContractAmendmentSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractAmendmentSO.class);

			if (ctBB.getManagedBean().getContractVer().getCtContractAmount().isAgreement()) {
				//Retrieve amendment
				ContractAmendmentReqDTO amendmentReqDTO = new ContractAmendmentReqDTO();
				amendmentReqDTO.setContractId(this.getManagedBean().getCtContract().getContractId());
				amendmentReqDTO.setAmendmentEffectiveDate(this.getManagedBean().getContractVer().getCtContractAmount().getEffDate());
				amendmentResDTO = amendmentSO.getCtAmendmentByAmendmentEffDate(amendmentReqDTO);

				if (amendmentResDTO.getCtAmendment() != null) {
					ctBB.getManagedBean().setAgreementContractVer(amendmentResDTO.getCtAmendment().getCtContractVer());
				} else {
					ContractReqDTO reqDTO = new ContractReqDTO();
					reqDTO.setContractId(this.getManagedBean().getCtContract().getContractId());
					reqDTO.setParamCode(ParameterConst.DOCTYPE_CONTRACT_CREATION);

					resDTO = contractSO.retrieveContractVerByIdAndDocType(reqDTO);
					ctBB.getManagedBean().setAgreementContractVer(resDTO.getCtContractVer());
				}				
			} else {
				ctBB.getManagedBean().setAgreementContractVer(this.getManagedBean().getContractVer());
			}
		} catch(Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		} finally {
			contractSO = null;
			amendmentSO = null;
			resDTO  = null;
			amendmentResDTO = null;
		}
	}

	/**
	 * This method used to terminate contract
	 * This method call validate function to validate termination details
	 *  This method will navigate to acknowledgment page with
	 *   a confirmation message once submit successfully
	 */
	public String saveContractTermination(String outcome) throws Exception{

		IContractTerminationSO contractTerminationSO = null;
		ContractReqDTO reqDTO = null;
		ContractResDTO resDTO = null;
		ContractReqDTO ctReqDTO = null;
		try{

			/*Validation for login user's org profile before initiate contract task*/
			if (!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)
					&& getManagedBean().getContractVer().getStatus() != null
					&& getManagedBean().getContractVer().getStatus().getStatusId() == ContractStatusConst.FT_PENDING_SUBMISSION.longValue()) {
				validateOwnerOrgProfile(
						getManagedBean().getContractVer().getCtContract().getOwnerOrgProfile().getOrgProfileId());
			}
			
			validateContractTermination(outcome);
			reqDTO = new ContractReqDTO();		
			contractTerminationSO = (IContractTerminationSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractTerminationSO.class);

			reqDTO.setToken( getFacesScopes().getBPMAuthenticationCtxToken());
			reqDTO.setOutcome(outcome);
			reqDTO.setTaskId(getManagedBean().getTaskId());
			reqDTO.setPmUser(getFacesScopes().getUserSessionBean());

			getManagedBean().getContractVer().setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
			reqDTO.setCtContract(getManagedBean().getCtContract());
			reqDTO.setCtContractVer(getManagedBean().getContractVer());

			if(getManagedBean().isRemedy()){
				reqDTO.setTerminationAction(ContractConst.TR_LETTER_REMEDY);
			} else if(getManagedBean().isTermination()){
				reqDTO.setTerminationAction(ContractConst.TR_LETTER_TERMINATE);
			} else if(getManagedBean().isMutualTermination()) {
				reqDTO.setTerminationAction(ContractConst.TR_LETTER_MUTUAL);
			}

			reqDTO.setUserRole(getUserRole());
			reqDTO.setCtRemark(getManagedBean().getRemarkList().get(0));
			reqDTO.setAttachments(getManagedBean().getAttachmentList());
			reqDTO.setDeleteAttachments(ctBB.getManagedBean().getDeleteAttachmentList());
			reqDTO.setUserMap(getManagedBean().getUserMap());
			reqDTO.setSelectedApprovers(getManagedBean().getApproverModel().getTarget());
			reqDTO.setAvailableApprovers(getManagedBean().getApproverModel().getSource());

			if(getManagedBean().getPmDigiSign() !=null){
				reqDTO.setPmDigiSign(getManagedBean().getPmDigiSign());
			}    

			resDTO = contractTerminationSO.saveContractTerminationDetails(reqDTO);
			if(resDTO.isSuccessful()) {
				log.debug("Record saved successfully");
				String messageId = MessageConst.INFO_700046;
				if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE)) {
					messageId = MessageConst.INFO_700025;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE)) {
					messageId = MessageConst.INFO_700009;
					//#16025 - START
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
					messageId = MessageConst.INFO_700093;
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT)) {
//					messageId = MessageConst.INFO_700021;
					if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
							ParameterConst.CONTRACT_TYPE_MINISTRY)) {
						messageId = MessageConst.INFO_700094;
					} else { 
						messageId = MessageConst.INFO_700095; // Central Contract
					}
					//#16025 - END
				} else if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
					messageId = MessageConst.INFO_700010;
				}	
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,
						MessageUtils.getMessage(messageId, new String[]{getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, getManagedBean().getContractVer().getDocType()), 
								getManagedBean().getContractVer().getCtContract().getContractNo()}, 
								MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
				if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) || (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && getManagedBean().getTaskId()==null)) {
					renewSesionObject();
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				} 
				else {
					ctReqDTO = new ContractReqDTO();
					ctReqDTO.setContractVerId(resDTO.getCtContractVer().getContractVerId());
					ctReqDTO.setDocType(resDTO.getCtContractVer().getDocType());
					setTerminationContractVer(ctReqDTO);
				}
			}  else {
				throw new EpException(MessageConst.ERROR_700020);
			}
		}finally {
			contractTerminationSO = null;
			ctReqDTO = null;
			reqDTO = null;
			resDTO = null;
		}
		return null;
	}

	/**
	 * This method used to validate contract termination details
	 */
	public void validateContractTermination(String outcome){
		this.getManagedBean().setAutoUpdate(true);

		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) 
				|| outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {  //#36196 - Fixing 
			String remark = getManagedBean().getRemarkList().get(0).getRemark();
			if((remark == null || remark.length() == 0)) {
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.remark", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}	

		if(getManagedBean().getContractVer().getCtTermination().getDecisionDate() == null) {
			throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.decision.date", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
		}

		if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			if(getManagedBean().getContractVer().getCtTermination().getFileRefNo() == null
					|| getManagedBean().getContractVer().getCtTermination().getFileRefNo().isEmpty()) {
				throw new EpException(MessageConst.ERROR_100061, new String[]{ResourceBundleUtils.getMessage("lbl.ct.file.ref.no", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}

			// Decision = termination
			if(this.getManagedBean().isTermination()) {
				if(getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo() == null
						|| getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo().isEmpty()) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.last.remedy.file.no", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}

				if (getManagedBean().getContractVer().getCtTermination().getLastRemedyDate() == null) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.last.remedy.notice.date", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}

				if (getManagedBean().getContractVer().getCtTermination().getRecommendedAgency() == null
						|| getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().isEmpty()) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.recommended.agency", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}

				if (getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate() == null) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.recommended.agency.date", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}

			// Decision = mutual termination
			if(this.getManagedBean().isMutualTermination()) {
				if (getManagedBean().getContractVer().getCtTermination().getFileName() == null 
						|| getManagedBean().getContractVer().getCtTermination().getFileName().isEmpty()) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.copy.mutual.termination.agreement", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}		
			}

			if (getManagedBean().isRemedy()) {
				if (getManagedBean().getContractVer().getCtTermination().getRemedyStartDate() == null) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.remedy.start.date", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}

				if (getManagedBean().getContractVer().getCtTermination().getRemedyEndDate() == null) {
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.remedy.end.date", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}

				if(getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().isEmpty()) {
					throw new EpException(MessageConst.ERROR_100023, new String[]{ResourceBundleUtils.getMessage("lbl.ct.reason.termination", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}	
			}
		}

		if(getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons() != null && 
				!getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().isEmpty()) {
			for(CtTerminationReason reason : getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons()){
				if(reason.getClauseNo().length() == 0){
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.clause.no", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
				if(reason.getDescription().length() == 0){
					throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.termination.desc", 
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
				}
			}
		}

		if(!outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SAVE) && !outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) {
			if(getManagedBean().getContractVer().getTerminationLetterAttachments().isEmpty()){
				throw new EpException(MessageConst.ERROR_100022, new String[]{ResourceBundleUtils.getMessage("lbl.ct.termination.upload.notice", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
			}
		}
		/* //#36196 - Fixing
		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_REVISE) || outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_CANCEL)) { 
			// Mandatory to upload meeting minutes if the decision is Mutual Termination		
			if(this.getManagedBean().getAttachmentList().isEmpty()) {
				throw new EpException(MessageConst.ERROR_750014);
			}
		}*/

		if(outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_SUBMIT) && isApproverSetting() 
				&& getManagedBean().getApproverModel().getTarget().size()==0) {
			throw new EpException(MessageConst.ERROR_750010); //Approver is mandatory for document submission. Please select at least one approver.
		}
	}

	/**
	 * This method is used to change the type of termination 
	 * such as termination, mutual termination and termination remedy
	 */
	public void changeField() {

		try {
			flush();
			String parameterCode = ParameterHelper.getInstance().getPmParameterById(getManagedBean().getContractVer().getCtTermination().getDecisionType().getParameterId()).getParameterCode();
			if (parameterCode.equalsIgnoreCase(ParameterConst.TERMINATE_DECISION_REMEDY_NOTICE)) {
				getManagedBean().setRemedy(true);
				getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.remedy", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			} else if (parameterCode.equalsIgnoreCase(ParameterConst.TERMINATE_DECISION_TERMINATE)) {
				getManagedBean().setTermination(true);
				getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.termination", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				if (getManagedBean().getContractVer().getCtTermination().getRecommendedAgency() == null 
						|| getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().length() == 0) {
					if (getManagedBean().getContractVer().getCtAgencies() != null 
							&& getManagedBean().getContractVer().getCtAgencies().size() == 1) {
						getManagedBean().getContractVer().getCtTermination().setRecommendedAgency(
								getManagedBean().getContractVer().getCtAgencies().get(0).getOrgProfile().getOrgName());
					}
				}
			} else if (parameterCode.equalsIgnoreCase(ParameterConst.TERMINATE_DECISION_MUTUAL_TERMINATE)) {
				getManagedBean().setMutualTermination(true);
				getManagedBean().setNoticeTypelbl(ResourceBundleUtils.getMessage("lbl.ct.termination.upload.mutual", 
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to disable type of termination
	 */
	public void flush() {
		getManagedBean().setTermination(false);
		getManagedBean().setRemedy(false);
		getManagedBean().setMutualTermination(false);
		setViewNoticeFlag(false);
	}

	/**
	 * The method used for generating a list of available decision type
	 * as selection. If previously no remedy done before, 
	 * the list won't include Terminate, only have Remedy
	 * and Mutual Terminate'. 
	 * 
	 * @return the selection list of decision type
	 */
	public List<SelectItem> getDecisionTypeList() {
		if (decisionTypeList == null) {
			boolean remedy = false;
			decisionTypeList = new ArrayList<SelectItem>();
			ISearchBean<PmParameterDesc> searchBean = null;
			IContractSO contractSO = null;
			ContractResDTO resDTO = null;
			try {
				contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT, IContractSO.class);			
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setContractId(getManagedBean().getCtContract().getContractId());
				resDTO = contractSO.retrieveTerminationHistoryList(reqDTO);

				for (CtTermination ctTermination : resDTO.getTerminationHistoryList()) {
					if (ctTermination.getDecisionType().getParameterCode().equalsIgnoreCase(ParameterConst.TERMINATE_DECISION_REMEDY_NOTICE)) {
						reqDTO = new ContractReqDTO();
						reqDTO.setDocId(ctTermination.getCtContractVer().getContractVerId());
						reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
						PmStatus status = contractSO.retrieveWorkflowStatus(reqDTO).getCtWorkflowStatus().getPmStatus();
						if (status.getStatusId().longValue() == ContractStatusConst.FT_APPROVED.longValue()) {
							remedy = true;
							break;
						}
					}
				}			

				List<PmParameterDesc> parameterDescs = ParameterHelper.getInstance().
						getParameterDescListBySearchBean(ParameterTypeConst.TERMINATION_DECISION_TYPE, searchBean);

				decisionTypeList.add(new SelectItem(null, ResourceBundleUtils.getMessage(
						"lbl.txt.select",  ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())));

				for (PmParameterDesc parameterDesc : parameterDescs) {
					if (!getManagedBean().isViewContract() && !remedy 
							&& parameterDesc.getPmParameter().getParameterCode().equalsIgnoreCase(ParameterConst.TERMINATE_DECISION_TERMINATE)) {
						continue;
					}
					decisionTypeList.add(new SelectItem(parameterDesc.getPmParameter().getParameterId(), parameterDesc.getCodeName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
			}finally{
				contractSO = null;
				resDTO = null;
			}
		}
		return decisionTypeList;
	}

	/**
	 * @return the viewNoticeFlag
	 */
	public boolean isViewNoticeFlag() {
		return viewNoticeFlag;
	}

	/**
	 * @param viewNoticeFlag the viewNoticeFlag to set
	 */
	public void setViewNoticeFlag(boolean viewNoticeFlag) {
		this.viewNoticeFlag = viewNoticeFlag;
	}

	// #25000 - Start
	/**
	 * Previews the notice action by generating a document based on the contract
	 * state and configuration. This method validates the view notice flag,
	 * populates the Velocity context with necessary data, generates the
	 * document, and initiates a download.
	 * 
	 * @throws Exception
	 *             if any error occurs during the process
	 */
	public void previewNoticeAction() throws Exception {
		VelocityContext context = new VelocityContext();
		String vmFile = determineVmFile();
		StringBuilder ministryJabatan = new StringBuilder();

		validateViewNotice();
		try {

			if (isViewNoticeFlag() && vmFile != null) {
				populateContext(context, ministryJabatan);
				String filePath = prepareFilePath();
				DocumentGenerator.getInstance().generateDocFromVelocity(vmFile, context, filePath);
				commonDownload(DocumentRepositoryUtils.getDBFilePath(filePath), getTemplateFileName());
			}
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.CONTRACT_MANAGEMENT, "previewNoticeAction",
					getManagedBean().getCtContract().getContractNo(), "previewNoticeAction", getUserIdSessionFromLoginId(), e);
		}
	}

	/**
	 * Determines the appropriate Velocity template file based on the contract
	 * state.
	 * 
	 * @return the path to the Velocity template file, or null if no matching
	 *         state is found
	 */
	private String determineVmFile() {
		if (getManagedBean().isTermination()) {
			return ContractConst.DOCPATH_CT_TERMINATION_NOTICE;
		} else if (getManagedBean().isMutualTermination()) {
			return ContractConst.DOCPATH_CT_MUTUAL_TERMINATION_NOTICE;
		} else if (getManagedBean().isRemedy()) {
			//#25000
			return ContractConst.DOCPATH_CT_NOTICE_OF_DEFAULT;
		}
		return null;
	}

	/**
	 * Populates the Velocity context with necessary data for document
	 * generation.
	 * 
	 * @param context
	 *            the Velocity context to populate
	 * @param ministryJabatan
	 *            a StringBuilder to store ministry and jabatan information
	 */
	private void populateContext(VelocityContext context, StringBuilder ministryJabatan) {
		CtContractVer ctContractVer = getManagedBean().getContractVer();
		if (getServerUrl() != null) {
			context.put("imgUrl", getServerUrl() + "NGeP-portlet/css/images/gov.png");
		}
		context.put("ptjName", getManagedBean().getCtContract().getOwnerOrgProfile().getOrgName());

		addMinistryJabatanInfo(context, ministryJabatan);
		addTerminationContextInfo(context, ctContractVer);
		addApproverContextInfo(context, ctContractVer);
	}

	/**
	 * Adds ministry and jabatan information to the context.
	 * 
	 * @param context
	 *            the Velocity context to populate
	 * @param ministryJabatan
	 *            a StringBuilder to store ministry and jabatan information
	 */
	private void addMinistryJabatanInfo(VelocityContext context, StringBuilder ministryJabatan) {
		String jabatanName = retrieveJabatanByOrgProfileId(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		if (!jabatanName.isEmpty()) {
			ministryJabatan.append(jabatanName).append(" / ");
		}

		PmOrgProfile ministryOrgProfile = retrieveMinistryOrgProfile(getManagedBean().getCtContract().getOwnerOrgProfile().getOrgProfileId());
		if (ministryOrgProfile != null) {
			ministryJabatan.append(ministryOrgProfile.getOrgName());
			context.put("ministryName", ministryOrgProfile.getOrgName());
		}

		context.put("ministryJabatanName", ministryJabatan);

	}

	/**
	 * Adds termination-specific context information to the Velocity context.
	 * 
	 * @param context
	 *            the Velocity context to populate
	 */
	private void addTerminationContextInfo(VelocityContext context, CtContractVer ctContractVer) {
		CtTermination ctTermination = ctContractVer.getCtTermination();
		if (ctTermination != null && encodeHtmlStringOrNull(ctTermination.toString()) != null) {
			context.put("decisionDate", formatDate(ctTermination.getDecisionDate()));
			context.put("fileRefNo", ctTermination.getFileRefNo() != null ? ctTermination.getFileRefNo() : "");
			context.put("reasonList", ctTermination.getCtTerminationReasons() != null ? ctTermination.getCtTerminationReasons() : "");
			context.put("remedyStartDate", formatDate(ctTermination.getRemedyStartDate()));
			context.put("remedyEndDate", formatDate(ctTermination.getRemedyEndDate()));
			context.put("lastRemedyDate", formatDate(ctTermination.getLastRemedyDate()));
			context.put("lastRemedyFileNo", ctTermination.getLastRemedyFileNo() != null ? ctTermination.getLastRemedyFileNo() : "");
			context.put("recommendAgency", ctTermination.getRecommendedAgency() != null ? ctTermination.getRecommendedAgency() : "");
			context.put("recommendAgencyDate", formatDate(ctTermination.getRecommendedAgencyDate()));
			context.put("scopeOfContract", ctTermination.getScopeOfContract() != null ? ctTermination.getScopeOfContract() : "");
		}

		context.put("ptjAddress", formatAddress(ctContractVer.getOwnerOrgAddress()));
		context.put("telNo", ctContractVer.getOwnerOrgAddress().getPhoneNoFull() != null ? ctContractVer.getOwnerOrgAddress().getPhoneNoFull() : "");
		context.put("faxNo", ctContractVer.getOwnerOrgAddress().getFaxNoFull() != null ? ctContractVer.getOwnerOrgAddress().getFaxNoFull() : "");
		context.put("noticeDate", formatNoticeDate());
		context.put("supplierName", ctContractVer.getCtContractValue().getSupplierName());
		context.put("supplierAddress", formatAddress(ctContractVer.getCtContractValue().getSupplierAddress()));

		context.put("contractTitle", getManagedBean().getCtContract().getContractName());
		context.put(
				"contractPhysicalNo",
				formatContractPhysicalNo() != null ? formatContractPhysicalNo() : ResourceBundleUtils.getMessage("lbl.txt.na",
						ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
		context.put("contractNo", getManagedBean().getCtContract().getContractNo());
		context.put("contractAmount", ctContractVer.getCtContractAmount().getContractAmount().toString());
		context.put("effDate", formatDate(ctContractVer.getCtContractValue().getEffDate()));
		context.put("expDate", formatDate(ctContractVer.getCtContractValue().getExpDate()));
	}

	/**
	 * Adds approver information to the Velocity context.
	 * 
	 * @param context
	 *            the Velocity context to populate
	 */
	private void addApproverContextInfo(VelocityContext context, CtContractVer ctContractVer) {
		if (getManagedBean().getContractVer().getContractVerId() != null) {
			String roleCode = determineRoleCode();
			CtActor approverActor = retrieveContractActor(ctContractVer.getContractVerId(), ctContractVer.getDocType(), roleCode);

			context.put("approverName", approverActor != null ? approverActor.getUserName() : "");

			context.put("approverDesignation", approverActor != null ? approverActor.getDesignation() : "");

		} else {
			context.put("approverName", "");

			context.put("approverDesignation", "");

		}

	}

	/**
	 * Formats the address by replacing line breaks and special characters.
	 * 
	 * @param address
	 *            the Address object to format
	 * @return the formatted address string
	 */
	private String formatAddress(CtAddress address) {
		return (address != null) ? getFullAddress(address).replaceAll("\r\n", "<br />").replaceAll("&", "&#38;") : "";
	}
	
	/**
	 * Determines the role code based on the contract type.
	 * 
	 * @return the role code string, or null if no matching contract type is
	 *         found
	 */
	private String determineRoleCode() {
		if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_MINISTRY)) {
			return RoleCode.CT_APPROVER.value();
		} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(ParameterConst.CONTRACT_TYPE_CENTRAL)) {
			return RoleCode.BPK_CC_APPROVER.value();
		}
		return null;
	}

	/**
	 * Prepares the file path for storing the generated document.
	 * 
	 * @return the prepared file path string
	 */
	private String prepareFilePath() {
		String filePath = getManagedBean().getFileDirectory();
		new File(filePath).mkdirs();
		AttachmentFormat attachmentFormat = new AttachmentDateFormat();
		return filePath + "/" + attachmentFormat.format(getTemplateFileName(), null);
	}

	/**
	 * Formats the notice date. If the notice date is null, it sets the current
	 * date.
	 * 
	 * @return the formatted notice date string
	 */
	private String formatNoticeDate() {
		CtContractVer contractVer = getManagedBean().getContractVer();
		if (contractVer.getCtTermination().getNoticeDate() != null) {
			return dateformat.format(contractVer.getCtTermination().getNoticeDate());
		}
		contractVer.getCtTermination().setNoticeDate(new Date());
		return dateformat.format(new Date());
	}

	/**
	 * Formats the contract physical number by appending the LOA number if
	 * necessary.
	 * 
	 * @return the formatted contract physical number string
	 */
	private String formatContractPhysicalNo() {
		String contractPhysicalNo = getManagedBean().getCtContract().getContractNo();
		if (getManagedBean().getContractVer().getContractPhysicalNo() == null || getManagedBean().getContractVer().getContractPhysicalNo().isEmpty()) {
			contractPhysicalNo += " / " + getManagedBean().getContractVer().getCtContractValue().getLoaNo();
		} else {
			contractPhysicalNo += " / " + getManagedBean().getContractVer().getContractPhysicalNo();
		}
		return contractPhysicalNo;
	}

	// #25000 - End

	private void validateViewNotice() {
		setViewNoticeFlag(true);
		if (getManagedBean().getContractVer().getCtTermination().getDecisionDate() == null
				|| getManagedBean().getContractVer().getCtTermination().getFileRefNo() == null
				|| getManagedBean().getContractVer().getCtTermination().getFileRefNo().isEmpty()) {
			setViewNoticeFlag(false);
		}

		if (isViewNoticeFlag() && this.getManagedBean().isTermination()) {
			if (getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo() == null
					|| getManagedBean().getContractVer().getCtTermination().getLastRemedyFileNo().isEmpty()
					|| getManagedBean().getContractVer().getCtTermination().getLastRemedyDate() == null
					|| getManagedBean().getContractVer().getCtTermination().getRecommendedAgency() == null
					|| getManagedBean().getContractVer().getCtTermination().getRecommendedAgency().isEmpty()
					|| getManagedBean().getContractVer().getCtTermination().getRecommendedAgencyDate() == null) {
				setViewNoticeFlag(false);
			}
		}

		if (isViewNoticeFlag() && !this.getManagedBean().isMutualTermination()) {
			if (getManagedBean().getContractVer().getCtTermination().getRemedyStartDate() == null
					|| getManagedBean().getContractVer().getCtTermination().getRemedyEndDate() == null) {
				setViewNoticeFlag(false);
			} else {
				if (getManagedBean().getContractVer().getCtTermination().getRemedyEndDate().before(
						getManagedBean().getContractVer().getCtTermination().getRemedyStartDate())) {
					setViewNoticeFlag(false);
					throw new EpException(MessageConst.ERROR_760164);
				}
			}

			/*if (getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons() == null
					|| getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().isEmpty()) {
				setViewNoticeFlag(false);
			} else {
				for (CtTerminationReason reason : getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons()){
					if (reason.getClauseNo().length() == 0) {
						setViewNoticeFlag(false);
						break;
					}
					if (reason.getDescription().length() == 0) {
						setViewNoticeFlag(false);
						break;
					}
				}
			}*/
		}

		if (!isViewNoticeFlag()) {
			throw new EpException(MessageConst.ERROR_750031);
		}
	}

	/**
	 * Method used to check login user as central or ministry admin
	 */
	public boolean isContractAdmin() {
		return (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value()));
	}

	/**
	 * Method check  to upload document during termination update by admin
	 */
	public boolean isPrintAndUpdateTask() {
		if (getManagedBean().getTaskName() != null && getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_UPDATE_HT_NAME)) {
			return true;
		}
		else
			return false;
	}

	/**
	 * Method describes reason for termination
	 * Admin used to enter reason of termination
	 */
	public void addTerminateReason() { 
		CtTerminationReason terminateReason = new CtTerminationReason();
		terminateReason.setCtTermination(getManagedBean().getContractVer().getCtTermination());
		getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().add(terminateReason);
	}

	/**
	 * The method used for searching the recommended agency.
	 */
	public void searchRecommendedAgency() {
		try {
			setSelectedRecommendedAgency(null);
			agencyDataModel.reloadData();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), "contract search failed."));
		}
	}

	/**
	 * The method used to reset the search criteria for recommended agency.
	 */
	public void resetSearchAgency() {
		setSelectedRecommendedAgency(null);
		getManagedBean().setPtjCodeName(null);		
		getManagedBean().setPtjCodeNameSearchValue(null);
		agencyDataModel.resetData();
	}

	/**
	 * @return the selectedRecommendedAgency
	 */
	public CtAgency getSelectedRecommendedAgency() {
		return selectedRecommendedAgency;
	}

	/**
	 * @param selectedRecommendedAgency the selectedRecommendedAgency to set
	 */
	public void setSelectedRecommendedAgency(CtAgency selectedRecommendedAgency) {
		this.selectedRecommendedAgency = selectedRecommendedAgency;
	}

	/**
	 * @return the agencyDataModel
	 */
	public IPagingDataModel<CtAgency> getAgencyDataModel() {
		return agencyDataModel;
	}

	/**
	 * @param agencyDataModel the agencyDataModel to set
	 */
	public void setAgencytDataModel(IPagingDataModel<CtAgency> agencyDataModel) {
		this.agencyDataModel = agencyDataModel;
	}

	/**
	 * Pagination for available agency search list
	 */
	private class AgencyPagingDataModel extends PagingDataModel<CtAgency> {
		private static final long serialVersionUID = 1L;

		public AgencyPagingDataModel() {
			super("tableAgency");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep
		 * .biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<CtAgency> pageChangeListener(ISearchBean<CtAgency> searchBean) {
			try {				
				searchBean.getDataFilterBeans().add(new SearchFilterBean("ctContractVer.contractVerId",
						SearchOperator.OPERATOR_EQUALS.value(), getManagedBean().getCtContract().getLatestContractVer().getContractVerId()));

				Date currentDate = DateUtils.getCurrentDateWithoutTime();
				// Filter By - Org effDate
				searchBean.getDataFilterBeans()
				.add(new SearchFilterBean("effDate",SearchOperator.OPERATOR_LESS_EQUAL.value(), currentDate));

				// Filter By - Org expDate
				searchBean.getDataFilterBeans().add(
						new SearchMultiFilterBean(new SearchFilterBean(
								"expDate", SearchOperator.OPERATOR_GREATER_EQUAL.value(),
								currentDate), new SearchFilterBean(
										"expDate", SearchOperator.OPERATOR_IS_NULL.value(), null,SearchCondition.CONDITION_OR.value())));

				if (getManagedBean().getPtjCodeName() != null
						&& (getManagedBean().getPtjCodeNameSearchValue() != null || getManagedBean().getPtjCodeNameSearchValue().equalsIgnoreCase(""))) {
					if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_CODE)) {
						SearchFilterBean filterBean = new SearchFilterBean("orgProfile.pmOrgValidity.orgCode",
								SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValue());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);

					} else if (getManagedBean().getPtjCodeName().equalsIgnoreCase(ContractConst.PTJ_NAME)) {
						SearchFilterBean filterBean = new SearchFilterBean("orgProfile.pmOrgValidity.orgName",
								SearchOperator.OPERATOR_LIKE.value(), getManagedBean().getPtjCodeNameSearchValue());
						filterBean.setCaseInsensitive(true);
						searchBean.getDataFilterBeans().add(filterBean);
					}
				}

				IContractSO contractSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,IContractSO.class);

				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setCtAgencySearchBean(searchBean);
				ContractResDTO resDTO = contractSO.searchRecommendedAgency(reqDTO);
				if (resDTO.isSuccessful()) {
					return resDTO.getCtAgencyResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Method to set the selected recommended agency
	 */
	public void setSelectedRecommendedAgency() {
		if (selectedRecommendedAgency != null) {
			getManagedBean().getContractVer().getCtTermination().setRecommendedAgency(
					selectedRecommendedAgency.getOrgProfile().getOrgName());
		}
	}

	/**
	 * Method to set user role as per user login
	 * User role used in BPM to identify the task
	 */
	private String getUserRole() {
		String userRole = null;		
		if (getManagedBean().getTaskName() == null
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FT_HT_NAME)
				|| getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.PRINT_UPDATE_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY) && getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())) {
				userRole = RoleCode.MIN_CONTRACT_ADMIN.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)&& getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())) {
				userRole = RoleCode.BPK_CCA.value();
			}
		} else if (getManagedBean().getTaskName() != null
				&& getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVIEW_FT_HT_NAME)) {
			if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_MINISTRY)&& getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())) {
				userRole = RoleCode.CT_APPROVER.value();
			} else if (getManagedBean().getCtContract().getContractType().getParameterCode().equalsIgnoreCase(
					ParameterConst.CONTRACT_TYPE_CENTRAL)&& getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
				userRole = RoleCode.BPK_CC_APPROVER.value();
			}
		}

		if (userRole == null) {
			throw new EpException(MessageConst.ERROR_750040);
		} else {
			return userRole;
		}
	}

	/**
	 * Method Call SM API to trigger a task for complaint.
	 */
	public void lodgeComplaint() {
		getManagedBean().getContractVer().getCtTermination().setComplaintLodged(true);
		//TODO Call SM API to trigger a task for complaint.
	}

	/**
	 * Method used to navigate main page for contract termination
	 */
	public String gotoTerminatePage() throws NamingException {
		Long contractVerId = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contractVerId"));
		ContractReqDTO reqDTO = new ContractReqDTO();
		reqDTO.setContractVerId(contractVerId);
		reqDTO.setDocType(ParameterConst.DOCTYPE_CONTRACT_TERMINATION);
		getManagedBean().setViewContract(true);
		getManagedBean().setExternalPageUrl(TERMINATE_HISTORY_PAGE);
		setTerminationContractVer(reqDTO);
		return PROCESS_TERMINATE_PAGE;
	}

	/**
	 * Method used to call during generation of letter
	 * This method used to retrieve portal information 
	 */
	private String getServerUrl() {
		PortletRequest portletRequest = (PortletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		HttpServletRequest httpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(portletRequest));
		String requestURL = httpServletRequest.getRequestURL().toString();
		return requestURL.substring(0, requestURL.length()	- httpServletRequest.getRequestURI().length())+ httpServletRequest.getContextPath() + "/";
	}

	/**
	 * The method used to upload performance bond file.
	 * 
	 * @param  event      the upload event for file upload from screen
	 * @throws Exception  if an exception occurred
	 * @see    Exception
	 */
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

		getManagedBean().getContractVer().getCtTermination().setFileName(item.getFileName());
		getManagedBean().getContractVer().getCtTermination().setFilePath(DocumentRepositoryUtils.getDBFilePath(filePath));
		RequestContext.getCurrentInstance().execute("uploadTerminatinDlg.hide()");
	}

	/**
	 * Method to retrieve portal name from property file
	 */
	public String getPortletTitle() {
		String portletTitle = ResourceBundleUtils.getMessage("lbl.ct.contract.termination", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		return portletTitle;
	}

	/**
	 * Method used to set value for security check
	 */
	public String preSubmitContractTermination(PmDigiSign pmDigiSign) throws Exception {
		if (pmDigiSign != null) {
			this.getManagedBean().setPmDigiSign(pmDigiSign);			
		}
		return saveContractTermination(getManagedBean().getButtonAction());
	}

	/**
	 * Method used to check navigation of page for approver list
	 * Return list of user for contract admin 
	 */
	public boolean isApproverSetting() {
		if(isContractAdmin() && (getManagedBean().getTaskName() == null ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.CREATE_FT_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.SUBMIT_FT_HT_NAME) ||
				getManagedBean().getTaskName().equalsIgnoreCase(CTProcessNameConst.REVISE_FT_HT_NAME)))
			return true;
		else 
			return false;
	}


	/**
	 * Method to authenticate user during submit of termination
	 * Navigate confirmation dialog for authenticate of login user
	 * Call save suspension method once authentication done
	 */
	public String gpkiSignAction(String outcome) throws Exception{
		//#16025 - START
		if (BpmOutcomeConst.BPM_APPROVE.equals(outcome) || BpmOutcomeConst.BPM_REJECT.equals(outcome)) {
		//#16025 - END	
			validateContractTermination(outcome);
			//Temp setTextBeforeSign : ContractNo+DocType+StartDate+EndDate+ContractAmount+Type
			StringBuilder sb = new StringBuilder();
			sb.append(getManagedBean().getContractVer().getCtContract().getContractNo());
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(ContractConst.DOC_TYPE_CONTRACT_TERMINATION);
			sb.append(ContractConst.SEPRATE_LINE);
			if(getManagedBean().isRemedy()){
				sb.append(ContractConst.TR_LETTER_REMEDY);
			} else if(getManagedBean().isTermination()){
				sb.append(ContractConst.TR_LETTER_TERMINATE);
			} else if(getManagedBean().isMutualTermination()) {
				sb.append(ContractConst.TR_LETTER_MUTUAL);
			}
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(outcome);		
			sb.append(ContractConst.SEPRATE_LINE);
			sb.append(new FacesScopes().getUserSessionBean().getIdentificationNo());
			getManagedBean().setTextBeforeSign(sb.toString());	
			getManagedBean().setButtonAction(outcome);
			getManagedBean().setAutoUpdate(Boolean.FALSE);
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
			return null;
		} else if (BpmOutcomeConst.BPM_CANCEL.equals(outcome)) {
			RequestContext.getCurrentInstance().execute("cancelConfirmDlg.show()");
			return null;
		} else {
			return saveContractTermination(outcome);
		}
	}

	/**
	 * Method used to set update value false for disable purpose
	 */
	public void enableAutoUpdate(){
		getManagedBean().setAutoUpdate(Boolean.TRUE);
		RequestContext.getCurrentInstance().update("@form");
	}

	/**
	 * @return the selectedTerminationReasonsList
	 */
	public CtTerminationReason[] getSelectedTerminationReasonsList() {
		return selectedTerminationReasonsList;
	}

	/**
	 * @param selectedTerminationReasonsList the selectedTerminationReasonsList to set
	 */
	public void setSelectedTerminationReasonsList(
			CtTerminationReason[] selectedTerminationReasonsList) {
		this.selectedTerminationReasonsList = selectedTerminationReasonsList;
	}

	/**
	 * Method to set boolean value for checkbox 
	 * @param pass ajax event type
	 * Set boolean value for user action 
	 * */

	public void selectAllTermination(AjaxBehaviorEvent e) {

		for (CtTerminationReason ctTerminationReasons : getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons()) {
			if (getManagedBean().isSelectAll()) {
				ctTerminationReasons.setSelectedCheckBox(true);
			}else {
				ctTerminationReasons.setSelectedCheckBox(false);
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * Method to set boolean value for checkbox 
	 * @param pass ajax event type
	 * Set boolean value for user action 
	 * */
	public void selectOneTerminationCheckbox(AjaxBehaviorEvent e){

		getManagedBean().setSelectAll(false);
		for (CtTerminationReason ctTerminationReasons : getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons()){
			if(ctTerminationReasons.isSelectedCheckBox()){
				getManagedBean().setButtonHideActive(false);
				return;
			}
		}

		if((boolean) e.getComponent().getAttributes().get("value")){
			getManagedBean().setButtonHideActive(false);
		}else{
			getManagedBean().setButtonHideActive(true);
		}	
	}

	/**
	 * The method used to delete the selected deduction clause.
	 */
	public void deleteTableRow() {
		try {
			Iterator<CtTerminationReason> ctTerReasonIterator = getManagedBean().getContractVer().getCtTermination().getCtTerminationReasons().iterator();
			while(ctTerReasonIterator.hasNext()){
				CtTerminationReason  ctTerminationReasons = ctTerReasonIterator.next();
				if(ctTerminationReasons.isSelectedCheckBox()){
					ctTerReasonIterator.remove();
				}
			}
			getManagedBean().setButtonHideActive(true);
			getManagedBean().setSelectAll(false);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,e.toString(), e.getMessage()));
		}
	}

	public void deleteAttachAgrementDoc(){
		getManagedBean().getContractVer().getCtTermination().setFileName(null);
		getManagedBean().getContractVer().getCtTermination().setFilePath(null);
	}

	public void onToggleSelect(ToggleSelectEvent event) {
		getManagedBean().setButtonHideActive(false);
	}

	public void onRowSelect(SelectEvent event) {
		getManagedBean().setButtonHideActive(false);
	}

	public void onRowUnselect(UnselectEvent event) {
		getManagedBean().setButtonHideActive(true);
	}


	/**
	 * The method used to retrieve filename of the file to download
	 * according to template type.
	 * 
	 * @return the filename
	 */
	private String getTemplateFileName() {
		if (getManagedBean().isTermination()) {
			return "termination-notice.docx";
		} else if (getManagedBean().isMutualTermination()) {
			return "mutual-termination-notice.docx";
		} else if (getManagedBean().isRemedy()) {
			//#25000
			return "notice-of-default.docx";
		}
		return null;
	}

	//Enhancement 15851, 34142 - shaqimi start 02/12/2022
	/**
	 * The method used to navigate to contract/loa details screen
	 * for view purpose.
	 * 
	 * @return the navigate URL 
	 */
	@Override
	public void goToLoaView(ScLoa scLoa) {
		String slLoaNo = this.getManagedBean().getContractVer()==null?
				ctBB.getManagedBean().getContractVer().getCtContractValue().getLoaNo() : this.getManagedBean().getContractVer().getCtContractValue().getLoaNo();
		Long contractId = this.getManagedBean().getContractVer()==null?
				ctBB.getManagedBean().getContractVer().getCtContract().getContractId() : this.getManagedBean().getContractVer().getCtContract().getContractId();
		Long statusId = null;
		String docType = null;
		
		if(!ctBB.getManagedBean().isViewContract()){
			statusId = this.getManagedBean().getContractVer()==null?
					ctBB.getManagedBean().getContractVer().getStatus().getStatusId() : this.getManagedBean().getContractVer().getStatus().getStatusId();
			docType = ParameterConst.DOCTYPE_CONTRACT_TERMINATION;
		}
				
		if(slLoaNo.substring(0,2).equals(ParameterConst.DOCTYPE_SUPPLEMENTAL_CONTRACT_LETTER)){
			AmendmentBackingBean abb = new AmendmentBackingBean();
			
			FacesContext fc = FacesContext.getCurrentInstance();
			abb.getManagedBean().setBackToPreviousPage(fc.getViewRoot().getViewId()+"?faces-redirect=true");
			abb.getManagedBean().setTrackingDairyView(true);
			abb.getManagedBean().setSupplementalLoaNo(slLoaNo);
			
			ctBB.getManagedBean().setViewSCL(true);
			ctBB.getManagedBean().setDocTypeFromTaskList(docType);
			ctBB.getManagedBean().setStatusIdFromTaskList(statusId);
			ctBB.getManagedBean().setContractIdFromTaskList(null);
			ctBB.getManagedBean().setTaskNameFromTaskList(null);
	
			if(ctBB.getManagedBean().isViewContractFromAgreement()){
				ctBB.getManagedBean().setViewSCLFromContract(true);
			}
			abb.gotoAmendViewPage(contractId, scLoa.getLoaId());
		} else {
			super.goToLoaView(scLoa);
		}
	}
	//Enhancement 15851, 34142 - shaqimi end 02/12/2022	
}
