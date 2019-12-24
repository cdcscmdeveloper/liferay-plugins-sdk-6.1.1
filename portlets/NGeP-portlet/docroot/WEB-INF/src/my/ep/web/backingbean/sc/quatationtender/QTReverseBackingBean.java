/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.quatationtender;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoaForQt;
import my.ep.persistence.entity.sc.loaloi.ScLoiForQt;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.type.pm.RoleCode;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;

/**
 * The Class QTDetailBackingBean.
 * 
 * @Author VidyasagarReddy
 */
@ManagedBean(name = "qTReverseBB")
@RequestScoped
public class QTReverseBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(QTDetailBackingBean.class);
	
	private RoleCode[] deskOfficerRoleCodes = {
			RoleCode.DESK_OFFICER, 
			RoleCode.PBB_SECRETARY, 
			RoleCode.PBA_SECRETARY,
			RoleCode.QCA_SECRETARY,
			RoleCode.QCB_SECRETARY,
			RoleCode.FPB_SECRETARY,
			// ***17376#55
			RoleCode.PPB_SECRETARY 
			};

	/**
	 * Instantiates a new QT detail backing bean.
	 */
	public QTReverseBackingBean() {
		log.debug("inside constructor");
		log.debug("@@@@@@@@@ Managed Bean @@@@@@@@@ = "+this.getManagedBean());

	}

	/**
	 * Debug logger.
	 * 
	 * @param message
	 *            the message
	 */
	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}
	
	/**
	 * Enhancement #21694 START (SHAFFUL)
	 * @param obj
	 * 		instance of ScQt/ScLoiForQt/ScLoaForQt
	 * @param previousPageUrl
	 * @return
	 */
	public String viewReverseStatusDetailsGen(Object obj, String previousPageUrl) {
		
		debugLogger("viewReverseStatusDetails");
		FacesContext fc = FacesContext.getCurrentInstance();
		String docType = getManagedBean().getQtnTenderSearchBean().getResultDocType();
		ScQt scQt = null;
		Boolean isReverseLOI = null;
		Boolean isReverseLOA = null;
		ScLoiForQt scLoiForQt = null;
		ScLoaForQt scLoaForQt = null;
		IScWorkFlowStatusSO workFlowStatusSO;
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		if(getManagedSearchBeanDocTypeIsLOAAndPrefixLAAndIsDO() || getManagedSearchBeanDocTypeIsLOIAndPrefixLIAndIsDO()){
			
			if(obj instanceof ScLoiForQt ){
				scLoiForQt = (ScLoiForQt)obj;
				if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
					scQt = scLoiForQt.getScLoiLoa()!=null?scLoiForQt.getScLoiLoa().getScQt():null;
					
					/*
					 * FINDING WORKFLOW STATS
					 */
					reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_INTENT);
					reqDTO.setDocId(scLoiForQt.getLoiId());					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					this.getManagedBean().setQt(scQt);
					this.getManagedBean().setScLoiForQt(scLoiForQt);
					this.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, reverseQtDetails(isReverseLOI=Boolean.TRUE,isReverseLOA = Boolean.FALSE));
				}
			}
			else if(obj instanceof ScLoaForQt){
				scLoaForQt = (ScLoaForQt)obj;
				if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equals(docType)){
					/*Map<MapKeyEnum, Object> otherArgs = new HashMap<MapKeyEnum, Object>();
					otherArgs.put(MapKeyEnum.loiLoaSelected, scLoaForQt);*/
					scQt = scLoaForQt.getScLoiLoa()!=null?scLoaForQt.getScLoiLoa().getScQt():null;
					
					/*
					 * FINDING WORKFLOW STATS
					 */
					reqDTO.setDocType(ParameterConst.DOCTYPE_LETTER_OF_AWARD);
					reqDTO.setDocId(scLoaForQt.getLoaId());					
					ScWorkFlowStatus currentWorkFlowStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
					this.getManagedBean().setQt(scQt);
					this.getManagedBean().setScLoaForQt(scLoaForQt);
					this.getManagedBean().setStatus(getStatusDescById(currentWorkFlowStatus.getPmStatus().getStatusId()));
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null, reverseQtDetails(isReverseLOI=Boolean.FALSE,isReverseLOA=Boolean.TRUE));
				}
			}
			else;
		}
		/*else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType) 
				|| ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(docType)){
			scQt = (ScQt)obj;
			if (ParameterConst.DOCTYPE_LETTER_OF_INTENT.equals(docType)) {
				viewLoiListDetails(scQt, previousPageUrl);
				return null;
			}
			else if(ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(docType)){
				// ***19287#42 , ******17376#55  
				if(scQt.getLoiLoa()!=null && ParameterConst.DOCTYPE_DIRECT_NEGO.equalsIgnoreCase(scQt.getLoiLoa().getDocType())){
					DirectLOABackingBean directLoaBackingBean = new DirectLOABackingBean();
					directLoaBackingBean.renewSesionObject();
					
					return viewDirectLOADetails(scQt, previousPageUrl);
				}else{	
					QtLOABackingBean bb = new QtLOABackingBean();
					bb.preRenderViewForQtMaintenance(scQt, previousPageUrl, null);
					return null;
				}
				
			}
			else{
				return null;
			}
		}
		else if(obj instanceof ScQt){
			scQt = (ScQt)obj;
			if(	ParameterConst.DOCTYPE_QUOTATION.equalsIgnoreCase(docType)
				||  ParameterConst.DOCTYPE_SUPPLIER_PROPOSAL.equals(docType)){
				return new SPDetailBackingBean().retriveSupplierProposalDtl(scQt.getQtId(), previousPageUrl, true);
			}
			else if(ParameterConst.DOCTYPE_BIDDING.equals(docType)) {
				return QTConstants.ONLINE_BIDDING_MAIN_PAGE;
			}
		}
		else;*/
		
		return null;
	}
	
	/**
	 * Enhancement #21694
	 * will be called in maintance flow only..
	 * 
	 * @return the string
	 */
	public String reverseQtDetails(Boolean isReverseLOI, Boolean isReverseLOA) {
		debugLogger("reverseQtDetails");
		IQTDetailSO detailSO;
		try {
			detailSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
			QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
			ScQt scQt = this.getManagedBean().getQt();

				quationTenderReqDTO.setQtId(scQt.getQtId());
				quationTenderReqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				quationTenderReqDTO.setReversedLOI(isReverseLOI!=null && isReverseLOI?isReverseLOI:Boolean.FALSE);
				quationTenderReqDTO.setReversedLOA(isReverseLOA!=null && isReverseLOA?isReverseLOA:Boolean.FALSE);
				QuationTenderResDTO resDTO = detailSO
						.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();
				
				List<ScQtApprovalRemark> qtRemarks = initializeRemarks(qtApproval, 
						this.getManagedBean().getQt()
						.getCurrentWorkFlowStatus(),
						qtApproval.getScQtApprovalRemarks(), ScQtApprovalRemark.class);
				for(ScQtApprovalRemark qtRemark : qtRemarks) {
					qtRemark.setEditable(false);
				}
				qtApproval.setScQtApprovalRemarks(qtRemarks);
				TabBean tabBean = new TabBean(null, null, qtApproval,
						retrieveFinalizationCommitteeType());
				this.getManagedBean().setSelectedApprovalTabBean(tabBean);			
			return "/xhtml/sc/loiLoaReverse/loiLoaReverseDetails.xhtml";

		} finally {
			detailSO=null;
		}

	}
	
	/**
	 * Retrieve finalization committee types.
	 * 
	 * @return the list
	 */
	public SelectItem[] retrieveFinalizationCommitteeType() {
		
		ScQt scQt = getManagedBean().getQt();
		SelectItem[] finalizationSecratary = getManagedBean().getFinalizationSecratary();
		Long currentStatusId = scQt.getQtStatus().getStatusId();
		
		if (finalizationSecratary == null
				&& isQTStatusLessThanOrEquals(currentStatusId, QTStatusConst.QT_PENDING_LOA)) {
			ICommitteSO committeSO;
			try {
				committeSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

				CommitteReqDTO reqDTO = new CommitteReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				CommitteResDTO resDTO = committeSO
						.retrieveFinalizationCommitteeType(reqDTO);

				List<PmParameter> finalizationCommTypes = resDTO.getFinalizationSecratry();
				if(CollectionUtils.isNotEmpty(finalizationCommTypes)){
					finalizationSecratary = new SelectItem[finalizationCommTypes.size()];
					for(int i=0;i<finalizationCommTypes.size();i++){
						String commTypeDesc = getParameterDesc(
								finalizationCommTypes.get(i),new FacesScopes().getUserLocale());
						finalizationSecratary[i] = new SelectItem(
								finalizationCommTypes.get(i).getParameterId(),commTypeDesc);
					}
					getManagedBean().setFinalizationSecratary(finalizationSecratary);
				}

				
			} finally {
				committeSO = null;
			}
		}

		return finalizationSecratary;
	}
	
	/**
	 * Enhancement #21694
	 * Show prepared by.
	 *
	 * @param createdBy the created by
	 * @return the string
	 */
	public String showPreparedBy(Long createdBy) {
		String userName = "";
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (createdBy != null) {
			reqDTO.setUserId(createdBy);
			UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);

			if (resDTO.getPmUser() != null) {
				userName = resDTO.getPmUser().getUserName();
			}
		}
		return userName;
	}
	
	/**
	 * Add attachment to list.
	 * 
	 * @param event
	 *            File upload event instance
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		log.debug("qtDetailhandleFileUpload - START");
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			log.debug("qtDetailhandleFileUpload - START");
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval();
			if (scQtApproval.getScQt() == null) {
				scQtApproval.setScQt(this.getManagedBean().getQt());
			}
			List<ScQtApprovalAttachment> approvalAttachments = scQtApproval
					.getScQtApprovalAttachments();
			if (null == approvalAttachments) {
				approvalAttachments = new ArrayList<ScQtApprovalAttachment>();
			}
			attachment.setScQtApproval(scQtApproval);
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			approvalAttachments.add(attachment);
			scQtApproval.setScQtApprovalAttachments(approvalAttachments);
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			this.getManagedBean()
			.getSelectedApprovalTabBean().setScQtApproval(scQtApproval);
			//getManagedBean().setScQtApproval(scQtApproval);
			//attachments.add(attachment);
			//getManagedBean().setAttachments(attachments);
		}
		attachments=null;
		log.debug("qtDetailhandleFileUpload - END");
	}
	
	/**
	 * Get the ApprovalDetails Enable or Disable based on Approval and status.
	 * 
	 * @return the enable or disable approvals.
	 */
	public boolean approvalDtlsEditable(ScQtApproval approval) {
		
		Long statusId = this
				.getManagedBean().getQt().getQtStatus().getStatusId();
		if (this.getManagedBean().isMaintanceFlow()
				&& QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL.equals(statusId)) {
			return true;
		}
		if ((QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT.equals(statusId) || 
				isStatusOfBpkOrScheduleRevise(statusId)) 
				&& (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode()) || (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approval.getApprovalCategory()
								.getParameterCode())))) {
			return (this.getManagedBean().isReadOnly());
		}
		List<ScQtApprovalRequest> approvalRequests = approval
				.getScQtApprovalDtls();
		if (CollectionUtils.isNotEmpty(approvalRequests)) {

			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null == approvalRequest.getSubmissionDate()
						|| (null != approvalRequest.getApproverAction() && ParameterConst.APPROVAL_DECISION_TYPE_REVISED
								.equalsIgnoreCase(approvalRequest
										.getApproverAction().getParameterCode()))) {
					return (this.getManagedBean().isReadOnly());
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Change pb secretary comm type.
	 * 
	 * @param event
	 *            the event
	 */
	public void changePbSecretaryCommType(ScQtApproverSettings approverSettings) {
		debugLogger("changePbSecretaryCommType");
		
		Long commTypeId = approverSettings.getCommitteeType().getParameterId();
		if (null != commTypeId) {
			PmParameter commTypeCode = ParameterHelper.getInstance().getPmParameterById(commTypeId);
			approverSettings.setCommitteeType(commTypeCode);
			
			getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
					.getApproverSettings().get(0).setPmUser(new PmUser());
		}
	}
	
	/**
	 * Retrieve pm user secratry dtl.
	 * 
	 * @param committeeType
	 *            the committee type
	 * @return the list
	 */
	public List<PmUser> retrievePmUserSecratryDtl(String committeeType) {
		log.info(":::retrievePmUserSecratryDtl() committeeType: "+ committeeType);
		this.getManagedBean().setPmUsersList(null);
		ICommitteSO committeSO;
		
		try {
			committeSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);

			CommitteReqDTO reqDTO = new CommitteReqDTO();
			// reqDTO.setPmOrgProfile(retrieveMinistryOrgProfile(getManagedBean()
			// .getQt().getPmOrgProfile().getOrgProfileId()));
			reqDTO.setPmOrgProfile(getManagedBean().getQt().getPmOrgProfile());
			reqDTO.setCommitteeType(committeeType);

			CommitteResDTO resDTO = committeSO.retrieveFcSecretaryUsersByCommType(reqDTO);
			this.getManagedBean().setPmUsersList(resDTO.getUserList());
			
			return resDTO.getUserList();

		} finally {
			committeSO = null;
		}
	}
	
	/**
	 * Update user id to settings.
	 * 
	 * @param event
	 *            the event
	 */
	public void updateUserIdToSettings(AjaxBehaviorEvent event) {
		Long id = (Long) event.getComponent().getAttributes().get("value");
		List<PmUser> userList = this.getManagedBean().getPmUsersList();
		for (PmUser user : userList) {
			if (user.getUserId().equals(id)) {
				this.getManagedBean().getSelectedApprovalTabBean()
						.getScQtApproval().getApproverSettings().get(0)
						.setPmUser(user);
				break;
			}
		}
	}
	
	private boolean isStatusOfBpkOrScheduleRevise(Long statusId) {
		
		boolean isStatusOfBpkOrScheduleRevise = false;
		if (!this.getManagedBean().isMaintanceFlow()) {
			if (QTStatusConst.QT_PENDING_SCHEDULE_REVISION.equals(statusId)
					|| QTStatusConst.QT_PENDING_PB_APPROVAL.equals(statusId)
					|| QTStatusConst.QT_PENDING_BPK_APPROVAL.equals(statusId)
					|| QTStatusConst.QT_PENDING_DOCUMENT_REVISION.equals(statusId)) {
				isStatusOfBpkOrScheduleRevise = true;
			}
		}
		return isStatusOfBpkOrScheduleRevise;
	}
	
	private boolean isQTMaintenanceScreen() {
		return QTConstants.PORTLET_NAME_QT_MAINTENANCE.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	private boolean isQTSearchScreen() {
		return QTConstants.PORTLET_NAME_QT_SEARCH.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId());
	}
	
	
		private ICommitteSO getCommitteSO() {
		return BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	}
		
	/**
	 * Sets the out come action.
	 * 
	 * @param outCome
	 *            the new out come action
	 */
	public void setOutComeAction(String outCome) {
		this.getManagedBean().setOutCome(outCome);

	}

	/**
	 * document type in search bean is LOA and prefix of doc no is LA
	 * @return
	 */
	public boolean getManagedSearchBeanDocTypeIsLOAAndPrefixLAAndIsDO(){
		return getManagedSearchBeanDocumentTypeIsLOA() && checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_AWARD) && userHasRole(deskOfficerRoleCodes);
	}
	
	private boolean checkSearchBeanDocTypePrefix(String prefix){
		if(prefix == null){
			prefix = "";
		}
		return  ( getManagedBean().getQtnTenderSearchBean()!=null && !StringUtils.isBlank(getManagedBean().getQtnTenderSearchBean().getDocNo()) && getManagedBean().getQtnTenderSearchBean().getDocNo().substring(0, prefix.length()).equalsIgnoreCase(prefix) );
	}
	
	/**
	 * document type in search bean is LOI and prefix of doc no is LI
	 * @return
	 */
	public boolean getManagedSearchBeanDocTypeIsLOIAndPrefixLIAndIsDO(){
		return getManagedSearchBeanDocumentTypeIsLOI() && checkSearchBeanDocTypePrefix(ParameterConst.DOCTYPE_LETTER_OF_INTENT) && userHasRole(deskOfficerRoleCodes);
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOI(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			) return true;
		return false;
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOA(){
		if(!StringUtils.isEmpty(getManagedBean().getQtnTenderSearchBean().getDocType())
			&& ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			) return true;
		return false;
	}
	
	public Boolean getManagedSearchBeanDocumentTypeIsLOILOA(){
		if(	getManagedBean().getQtnTenderSearchBean().getDocType() != null && 
			(	ParameterConst.DOCTYPE_LETTER_OF_INTENT.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
				||  ParameterConst.DOCTYPE_LETTER_OF_AWARD.equalsIgnoreCase(getManagedBean().getQtnTenderSearchBean().getDocType())
			)) return true;
		return false;
	}
}
