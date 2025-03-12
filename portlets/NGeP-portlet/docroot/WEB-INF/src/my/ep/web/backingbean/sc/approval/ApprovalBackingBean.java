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
package my.ep.web.backingbean.sc.approval;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import my.ep.biz.bean.sc.QtAmendMentValueBean;
import my.ep.biz.bean.sc.QtCommitteeMemberBean;
import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.IAuditTrailSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTAuditTrailSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.quationtender.IQTSpecSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.constant.sc.QTAuditConstant;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.CommonConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.AuditTrailReqDTO;
import my.ep.dto.sc.quationtender.AuditTrailResDTO;
import my.ep.dto.sc.quationtender.DemoResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.ProposalResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.quationtender.SpecificationReqDTO;
import my.ep.dto.sc.quationtender.SpecificationResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.common.ScAddress;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtAppRequestRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalReqAttachement;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtBsv;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtContactPerson;
import my.ep.persistence.entity.sc.quationtender.ScQtEpvApprovalReqAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtEpvSupplierResponse;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.persistence.entity.sc.quationtender.ScQtRequestContent;
import my.ep.persistence.entity.sc.quationtender.ScQtSampleSubmission;
import my.ep.persistence.entity.sc.quationtender.ScQtStatus;
import my.ep.persistence.entity.sm.SmCompanyAddress;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.AddressType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.SLAUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.demoInvitation.DemoInvitationBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDocumentsBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTScheduleDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.constant.sc.SLALogConstant;
import my.ep.web.managedbean.UserTasks;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.EpPortletUtil;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;

/**
 * Backing Bean for Approvals.
 * 
 * @author Viswanath
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends SourcingBaseBackingBean<QTDetailManagedBean> {
	private static final long serialVersionUID = 1L;

	/** The Constant logger. */
	private static final Logger logger = EpLogger.getLogger(ApprovalBackingBean.class);

	/** The text before sign. */
	private String textBeforeSign;

	/** The pm digi sign. */
	private PmDigiSign pmDigiSign;
	
	
    
	/**
	 * Instantiates a new approval backing bean.
	 */
	public ApprovalBackingBean() {

	}

	// TODO Need to check why this code... vidya

	/**
	 * Inits the.
	 */
	@PostConstruct
	public void init() {
		if (this.getManagedBean().getScQtApproval() == null) {
			this.getManagedBean().setScQtApproval(new ScQtApproval());
		}

		if (this.getManagedBean().getScQtApproval().getApprovalCategory() == null) {
			this.getManagedBean().getScQtApproval()
					.setApprovalCategory(new PmParameter());
		}

	}

	/**
	 * Gets the approval categories list.
	 * 
	 * @return List<String>
	 */
	public List<String> getAppCatList() {
		List<String> appCatList = new ArrayList<String>();
		if (null != this.getManagedBean().getTabList()) {
			for (TabBean tabBean : this.getManagedBean().getTabList()) {
				appCatList.add(tabBean.getScQtApproval().getApprovalCategory()
						.getParameterCode());
			}
		}
		return appCatList;
	}

	/**
	 * Gets the approval categories.
	 * 
	 * @return the approval categories
	 */
	public SelectItem[] getApprovalCategories() {
		List<String> approvalCategories = new ArrayList<String>();
		// approvalCategories.add(ParameterConst.APPROVAL_CATEGORY_BUDGET);
		approvalCategories.add(ParameterConst.APPROVAL_CATEGORY_BPK);
		approvalCategories
				.add(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT);
		approvalCategories.add(ParameterConst.APPROVAL_CATEGORY_PTJ);
		approvalCategories
				.add(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
		approvalCategories
		.add(ParameterConst.APPROVAL_CATEGORY_PROCUREMENT_BOARD);
		approvalCategories.add(ParameterConst.APPROVAL_CATEGORY_OTHERS);

		SearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		searchBean
				.getDataFilterBeans()
				.add(new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_IN.value(), approvalCategories));

		return getRadioButtonCodeValuesBySearchBean(
				ParameterTypeConst.APPROVAL_CATEGORY, searchBean);
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
		logger.debug("handleFileUpload - START");
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApprovalAttachment attachment = new ScQtApprovalAttachment();
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
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
			getManagedBean().setScQtApproval(scQtApproval);
			attachments.add(attachment);
			getManagedBean().setAttachments(attachments);
		}
		logger.debug("handleFileUpload - END");
	}

	/**
	 * Add attachment to list.
	 * 
	 * @param event
	 *            the event
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void handleOtherFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		logger.debug("handleOtherFileUpload - START");
		if (event.getFile() != null) {
			//ScAttachment scAttachment = null;
			//ScQtApprovalRequest scQtApprovalRequest = null;
			/*List<ScQtApprovalRequest> list = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval()
					.getScQtApprovalDtls();*/
			ScQtApprovalRequest scQtApprovalDtls = this.getManagedBean().getScQtApprovalDtls();
			/*System.out.println("Size : " + list.size());
			for (ScQtApprovalRequest approvalRequest : list) {
				scQtApprovalRequest = approvalRequest;
			}*/
			ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalDtls
					.getApprovalRequestAttachement();
			PmParameter approvalType = scQtApprovalDtls.getApprovalType(); //#25077
			if (null == approvalReqAttachement) {
				approvalReqAttachement = new ScQtApprovalReqAttachement();
				approvalReqAttachement.setApprovalRequest(scQtApprovalDtls);
				/*scQtApprovalDtls
						.setApprovalRequestAttachement(approvalReqAttachement);*/
			/*} else {
				approvalReqAttachement.setApprovalRequest(scQtApprovalDtls);*/
				/*scQtApprovalDtls
						.setApprovalRequestAttachement(approvalReqAttachement);*/
			}
			approvalReqAttachement.setDocNo(this.getManagedBean().getQt().getQtNo()); //#25077
			//scAttachment = approvalReqAttachement;
			uploadAttachment(event.getFile(), approvalReqAttachement);
			scQtApprovalDtls.setApprovalRequestAttachement(approvalReqAttachement);
			// #25077 - Start
			if(approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)){
				this.getManagedBean().setScQtApprovalReqAttachementICT(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(false); 
			}
			if(approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)){
				this.getManagedBean().setScQtApprovalReqAttachementMITI(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(false); 
			}
			// #25077 - End
		}
		logger.debug("handleOtherFileUpload - END");
	}

	/**
	 * Delete Attachment.
	 */
	public void deleteUploadFile() {
		logger.debug("deleteUploadFile - START");
		// getManagedBean().getScQtApproval().setScQtApprovalAttachments(null);
		getManagedBean().getAttachments().clear();
		logger.debug("deleteUploadFile - END");
	}

	/**
	 * Delete Attachment for other approvals ICT.
	 */
	public void deleteOthersApprovalFileICT(BaseEntity delEntity) {//#25077
		logger.debug("deleteOthersApprovalFile - START");
		List<ScQtApprovalRequest> list = this.getManagedBean()
				.getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();
		for (int i = 0; i < list.size(); i++) {
			// #25077 - Start
			if(list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)){
				list.get(i).setApprovalRequestAttachement(null);
				this.getManagedBean().setScQtApprovalReqAttachementICT(null);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(true); 
			}
			// #25077 - End
		}
		logger.debug("deleteOthersApprovalFile - END");
	}
	
	/**
	 * Delete Attachment for other approvals MITI.
	 */
	public void deleteOthersApprovalFileMITI(BaseEntity delEntity) {//#25077
		logger.debug("deleteOthersApprovalFile - START");
		List<ScQtApprovalRequest> list = this.getManagedBean()
				.getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();
		for (int i = 0; i < list.size(); i++) {
			// #25077 - Start
			if(list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)){
				list.get(i).setApprovalRequestAttachement(null);
				this.getManagedBean().setScQtApprovalReqAttachementMITI(null);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(true); 
			}
			// #25077 - End
		}
		logger.debug("deleteOthersApprovalFile - END");
	}

	/**
	 * Determine icon.set icon for all uploaded files in Approvals.
	 * 
	 * @param attachment
	 *            the attachment
	 * @return the string
	 */
	public String determineIcon(ScQtApprovalAttachment attachment) {
		String imageFileName = "icon_file_default.png";
		String ext = "";
		if (attachment != null) {
			String fileName = attachment.getFileName();
			if (fileName != null) {
				int mid = fileName.lastIndexOf(".");
				ext = fileName.substring(mid + 1, fileName.length());
			}
			if ("doc".equals(ext) || "docx".equals(ext)) {
				imageFileName = "icon_doc.png";
			} else if ("pdf".equals(ext)) {
				imageFileName = "icon_pdf.png";
			} else if ("xls".equals(ext) || "xlsx".equals(ext)
					|| "csv".equals(ext)) {
				imageFileName = "icon_exel.png";
			} else if ("jpg".equals(ext) || "bmp".equals(ext)
					|| "png".equals(ext) || "gif".equals(ext)) {
				imageFileName = "icon_image.png";
			} else if ("txt".equals(ext)) {
				imageFileName = "icon_txt.png";
			}
		}
		return imageFileName;
	}

	/*:::Enhancement #21692 #135 - start:::*/
	/**
	 * preRenderView(). user click on task list it will Render Approvals screens
	 * for(Committee/BPK/Finalization/Publication).
	 */
	public void prepareTrackingDiary(ScQt scQt, Long decisionType) {
		Long currentStatusId = this.getManagedBean().getCurrentStatusId();
		FacesScopes facesScope = new FacesScopes();
		QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
		
		qtReqDTO.setToken(facesScope.getBPMAuthenticationCtxToken());
		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
		qtReqDTO.setStatusId(currentStatusId);
		qtReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
		qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
		qtReqDTO.setQtdetail(scQt);
		qtReqDTO.setPmUser(facesScope.getUserSessionBean());
		
		String approvalType = this.getManagedBean().getApprovalType();
		
		if (approvalType.equals(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE) && 
			currentStatusId.equals(QTStatusConst.EPV_PENDING_APPROVAL)) {

			String fcSecretaryRole = null;
			List<ScQtApproverSettings> approverSettings = this.getManagedBean().getScQtApproval().getApproverSettings();
		
			if (null != approverSettings && !approverSettings.isEmpty()) {
								
				if (approverSettings.get(0).getCommitteeType().getParameterCode().equals(ParameterConst.COMMITTEE_TYPE_PROCUREMENT_BOARD)) {
					fcSecretaryRole = RoleCode.PBB_SECRETARY.value();
				} else if (approverSettings.get(0).getCommitteeType().getParameterCode().equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SBH)
						|| approverSettings.get(0).getCommitteeType().getParameterCode().equals(ParameterConst.COMMITTEE_TYPE_FEDERAL_PROCUREMENT_BOARD_SWK)) {
					fcSecretaryRole = RoleCode.FPB_SECRETARY.value();
				}					
				
				log.info(":::prepareTrackingDiary() roleCode: "+ fcSecretaryRole +", decisionType: "+ decisionType);

				qtReqDTO.setRoleCode(fcSecretaryRole);
			}						
		}

		getQTDetailSO().prepareTrackingDiaryByDecisionType(qtReqDTO, decisionType);
	}
	/*:::Enhancement #21692 #135 - end:::*/

	public void preRenderView(){
		Object obj = getBpmTaskDetails(Object.class, null);
		String taskName = getManagedBean().getTaskName();

		Long qtId = null;
		List<String> paramTypes = null;
		if (obj instanceof SCCommitteeDataType) {
			qtId = ((SCCommitteeDataType) obj).getQtId().longValue();
			paramTypes = getRequestTypes(((SCCommitteeDataType) obj)
					.getXflagCommittee());
		} else if (obj instanceof SCEvaluationDataType) {
			qtId = ((SCEvaluationDataType) obj).getQtId().longValue();
			paramTypes = getRequestTypes(((SCEvaluationDataType) obj)
					.getXflagCommittee());
		} else {
			qtId = ((SCQuotationTenderDataType) obj).getQtId().longValue();

		}

		String approvalCategoryCode = null;
		ScWorkFlowStatus currentStatus = null;
		List<ScQtApprovalRequest> approvalRequests = null;
		this.getManagedBean().setSubStatusId(null); //#25692
		String docType = null; //#25692#128

		if (SCProcessNameConst.APPROVE_BUDGET.equalsIgnoreCase(taskName)) {
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BUDGET;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BGT_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_BGT_APPROVAL; //#25692#128

		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.BPK_APPROVE_QT_CHANGE
						.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagBpk());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);
			// ***25692 - start
			//this.getManagedBean().setOutComes(new String[]{my.ep.web.constant.BpmOutcomeConst.BPM_APPROVE,
			//                                             my.ep.web.constant.BpmOutcomeConst.BPM_REVISE,
			//                                             my.ep.web.constant.BpmOutcomeConst.BPM_REJECT});
			// ***25692 - end
			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_BPK_APPROVAL; //#25692#128	
			
			if (CollectionUtils.isNotEmpty(approvalRequests)) {

				for (ScQtApprovalRequest approvalRequest : approvalRequests) {
					if (approvalRequest.getApproverAction() == null) {
						approvalRequest.setApproverAction(new PmParameter());
					}
				}
			}
			if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
					.equalsIgnoreCase(this.getManagedBean().getQt().getQtType())) {
				if (null != this.getManagedBean().getQt().getScQtSuppliers()) {
					this.getManagedBean().setQtSupplierList(
							this.getManagedBean().getQt().getScQtSuppliers());
				}
			}
		} else if (SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagPublication());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_PTJ;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);

			if (CollectionUtils.isNotEmpty(paramTypes)) {
		//200520@asyrafhaziq_27839 - Start
				for (String paramType : paramTypes){
					if (ParameterConst.APPROVAL_REQUEST_UPDATE_BSV_DETAILS.equals(paramType)) {
						approvalCategoryCode = ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL;
	
						QTDetailBackingBean qTDetailBackingBean = new QTDetailBackingBean();
						qTDetailBackingBean.retrieveBriefingSitevisitTabDtl();
						break;
					}
				}
		//200520@asyrafhaziq_27839 - End
			}
			currentStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_PTJ_APPROVAL,
					getManagedBean().getQt().getQtId());
			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_PTJ_APPROVAL; //#25692#128

		} else if (SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) { //valid
			
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj).getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			currentStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_PCB_APPROVAL,
					getManagedBean().getQt().getQtId());

			setFCApproveQTChangeValues(approvalRequests);
			setEpvAttachment(approvalRequests);//#21692 add attachments
			docType = ParameterConst.DOCTYPE_PCB_APPROVAL; //#25692#128

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			
		} else if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(taskName)) {
			
			SCQuotationTenderDataType dataType = (SCQuotationTenderDataType)obj;
			paramTypes = getRequestTypes(dataType.getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, 
					ParameterConst.APPROVAL_DECISION_TYPE_APPROVED);
			if (CollectionUtils.isNotEmpty(approvalRequests)) {
				// Get the approval request for Extend Proposal Validity Period.
				for (ScQtApprovalRequest approvalRequest : approvalRequests) {
					if (approvalRequest.getQtApprovalRequestId().equals(dataType.getRequestId())) {
						approvalRequests = new ArrayList<ScQtApprovalRequest>();
						approvalRequests.add(approvalRequest);
						break;
					}
				}
			}
			currentStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_PCB_APPROVAL,
					getManagedBean().getQt().getQtId());

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_PCB_APPROVAL; //#25692#128

		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagBpk());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_BPK_APPROVAL; //#25692#128

		} else if (SCProcessNameConst.APPROVE_COMMITTEE
				.equalsIgnoreCase(taskName)) {
			// 25037 - start
			Long currentTaskStatusId = this.getManagedBean().getCurrentStatusId();
			currentTaskStatusId = currentTaskStatusId != null ? currentTaskStatusId : 0L;
			boolean isCommitteeUpdateFlow = Arrays.asList(
					new Long[] { QTStatusConst.CMT_PENDING_REAPPROVAL,
							QTStatusConst.CMT_PENDING_REAPPROVAL_REVISION_APPROVAL }).contains(currentTaskStatusId);
			this.getManagedBean().setCommitteeUpdateFlow(isCommitteeUpdateFlow);
			// 25037 - end

			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT;
			paramTypes = Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST);//25037 #372
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_COMMITTE_APROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_COMMITTE_APROVAL; //#25692#128

			List<Long> cmtStatus = new ArrayList<Long>(3);
			cmtStatus.add(QTStatusConst.CMT_PENDING_SUBMISSION);
			cmtStatus.add(QTStatusConst.CMT_COMPLETED);
			cmtStatus.add(QTStatusConst.CMT_PENDING_REVISION);
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			ISearchBean<ScQtCommittee> commSearchBean = new SearchBean<ScQtCommittee>();
			commSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("scQt.qtId",
							SearchOperator.OPERATOR_EQUALS.value(), qtId));
			commSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("scCommittee.approvalReq",
							SearchOperator.OPERATOR_EQUALS.value(), true));
			commSearchBean.getDataFilterBeans().add(
					new SearchFilterBean(
							"scCommittee.committeeType.parameterCode",
							SearchOperator.OPERATOR_IN.value(), paramTypes));
			commSearchBean.getDataFilterBeans().add(
					new SearchFilterBean("scCommittee.committeStatus.statusId",
							SearchOperator.OPERATOR_NOT_IN.value(), cmtStatus));

			committeReqDTO.setCommSearchBean(commSearchBean);
			CommitteResDTO resDTO = committeSO
					.retriveQtCommitteDtls(committeReqDTO);
			ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO
					.getQtCommitteResultBean();
			this.getManagedBean().setQtCommittes(qtcomSearchBean);
			List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean
					.getResultList(),qtId);
			// initializeRemark(qtcomSearchBean.getResultList());
			this.getManagedBean().getQt()
					.setScQtCommittees(qtcomSearchBean.getResultList());
			this.getManagedBean().setScCommitteeMemberTabList(tabBeans);
			this.getManagedBean().setParamTypes(paramTypes); //#25692
			
			if (tabBeans.size() > 0) {
				getManagedBean().setSelectedTabBean(tabBeans.get(0));
				// 25037 - start
				List<QtCommitteeMemberBean> historyList = getMemberHistoryList(getManagedBean().getSelectedTabBean()
						.getScQtCommittee().getScCommittee());
				this.getManagedBean().setMemberHistoryList(historyList);
				// 25037 - end
			}
			prepareEvalCommiteeApproval(approvalRequests);// 25037
		} else if (SCProcessNameConst.APPROVE_PUBLICATION
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagPublication());
			if (CollectionUtils.isNotEmpty(paramTypes)) {
				this.getManagedBean().setApprovalType(paramTypes.get(0));
			}
			docType = ParameterConst.DOCTYPE_QUOTATION; //#25692#128
			
			approvalRequests = getApprovalRequests(qtId,
					ParameterConst.APPROVAL_CATEGORY_PTJ,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			// #14398 - Start.
			if(null!=this.getManagedBean().getCurrentStatusId() && (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL.equals(this.getManagedBean().getCurrentStatusId())
					|| QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL_RESCHEDULE.equals(this.getManagedBean().getCurrentStatusId()))){
			// #14398 - End.
				log.info("======displayCurrentStatusIdCodesInnerLoopExecuted======");
			if(approvalRequests==null || approvalRequests.isEmpty()){
				log.info("PublicationApprovalApprovalRequestEmptyCodesExecuted");
				approvalRequests=new ArrayList<ScQtApprovalRequest>();
				ScQtApprovalRequest scQtApprovalDtl = new ScQtApprovalRequest();
				scQtApprovalDtl.setApprovalType(getParameterByTypeAndCode(ParameterTypeConst.APPROVAL_REQUEST_TYPES, 
						ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL));
				scQtApprovalDtl.setScQtApproval(this.getManagedBean().getScQtApproval());
				approvalRequests.add(scQtApprovalDtl);
				//approvalDtls.add(scQtApprovalDtl);
			}
			}
			
			if (getManagedBean().getQt().isBsvRequired()) {
				QTDetailBackingBean qTDetailBackingBean = new QTDetailBackingBean();
				qTDetailBackingBean.retrieveBriefingSitevisitTabDtl();	
			}
			
			ScQt scQt = getManagedBean().getQt();
			// Set New publish date for update publish date. To solve issue on publish date reset back to original time during primefaces calender update phase.
			scQt.setNewPublishDate(scQt.getPublishDate());

		} else if (SCProcessNameConst.APPROVE_PCB.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_PROCUREMENT_BOARD;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_PCB_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_PCB_APPROVAL; //#25692#128

			if (ParameterConst.QUOTATION_TENDER_TYPE_RESTRICTED_TENDER
					.equalsIgnoreCase(this.getManagedBean().getQt().getQtType())) {
				if (null != this.getManagedBean().getQt().getScQtSuppliers()) {
					this.getManagedBean().setQtSupplierList(
							this.getManagedBean().getQt().getScQtSuppliers());
				}
			}

		} else if (SCProcessNameConst.APPROVE_CANCELATION
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_PCB_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.DOCTYPE_PCB_APPROVAL; //#25692#128
			IScWorkFlowStatusSO workFlowStatusSO;
			try {
				workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
				ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
				reqDTO.setDocType(ParameterConst.DOCTYPE_QT_CANCELLATION);
				reqDTO.setDocId(qtId);

				currentStatus = workFlowStatusSO.
						retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			}
			finally {
				workFlowStatusSO = null;
			}
		} else if (SCProcessNameConst.DEMO_APPROVE_CANCELLATION.equalsIgnoreCase(taskName)) {// #28784
			SCEvaluationDataType dataType = getBpmTaskDetails(SCEvaluationDataType.class, null);
			getManagedBean().setPayLoad(dataType);
			qtId = Long.valueOf(dataType.getQtId());

			List<String> fc = new ArrayList<String>();
			fc.add(ParameterConst.APPROVAL_REQUEST_CANCELATITION_QT);
			paramTypes = fc;
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.APPROVAL_REQUEST_CANCELATITION_QT, paramTypes, null);
			this.getManagedBean().setApprovalType(approvalCategoryCode);
			docType = ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE;
			IScWorkFlowStatusSO workFlowStatusSO;
			try {
				workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IScWorkFlowStatusSO.class);
				ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
				reqDTO.setDocType(ParameterConst.COMMITTEE_TYPE_TECHNICAL_EVALUATION_COMMITTEE);
				reqDTO.setDocId(qtId);

				currentStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();

				QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
				qtBackingBean.retrieveQuationTenderDetails(qtId);
				ScQt scQt = qtBackingBean.getManagedBean().getQt();
				this.getManagedBean().setQt(scQt);

				IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);

				QuationTenderReqDTO quationTenderReqDTO = new QuationTenderReqDTO();
				quationTenderReqDTO.setQtId(scQt.getQtId());
				quationTenderReqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE);
				QuationTenderResDTO resDTO = detailSO.retrieveApprovalsByQtIdNType(quationTenderReqDTO);
				this.getManagedBean().setQt(resDTO.getQtDetail());
				ScQtApproval qtApproval = resDTO.getQtApproval();
				this.getManagedBean().setScQtApproval(qtApproval);
			} finally {
				workFlowStatusSO = null;
			}
		}
		
		if (currentStatus == null) {
			currentStatus = getManagedBean().getQt().getCurrentWorkFlowStatus();
		}

		//#21692 Re-open - Start
		if (!SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) {
			initializeRemarks(this.getManagedBean().getScQtApproval(), 
					currentStatus, 
					getManagedBean().getScQtApproval().getScQtApprovalRemarks(),
					ScQtApprovalRemark.class);
		}
		this.getManagedBean().setScQtApprovalRequestList(approvalRequests);
		FacesContext context = FacesContext.getCurrentInstance();

		boolean qtPageFirst = false;
		String page = "/xhtml/sc/quatationtender/Approver/approverMain.xhtml";
		if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(taskName)) {
			// #21692 new remark for supplier - Start
			if (this.getManagedBean().getQt().getResponsePeriod() != null) {
				IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
				QuationTenderReqDTO epvReqDTO = new QuationTenderReqDTO();
				FacesScopes facesScope = new FacesScopes();
				epvReqDTO.setQtId(this.getManagedBean().getQt().getQtId());
				epvReqDTO.setPmUser(facesScope.getUserSessionBean());
				this.getManagedBean().setPmUser(facesScope.getUserSessionBean());
				QuationTenderResDTO epvResDTO = detailSO.retrieveEpvApprovalRequest(epvReqDTO);
				ScQtApprovalRequest qtEPVApprolvaReq = epvResDTO.getDoaActiveRequest();
				ScQtProposal prop = epvResDTO.getScQtProposal();

				ScQtEpvSupplierResponse suppResp = new ScQtEpvSupplierResponse();
				suppResp.setScQtApprovalRequest(qtEPVApprolvaReq);
				suppResp.setScQtSupplier(prop.getScQtSupplier());
				suppResp.setQtSupplierName(prop.getSupplierName());

				this.getManagedBean().setSuppResp(suppResp);
			}
			//#21692 new remark for supplier - End
			page = QTConstants.EXTEND_PROPOSAL_VALIDITY_SUPPLIER_ACCEPTANCE;
			qtPageFirst = false;
		} else if (SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) {
			initializeRemarks(this.getManagedBean().getScQtApproval(), 
					currentStatus, 
					getManagedBean().getScQtApproval().getScQtApprovalRemarks(),
					ScQtApprovalRemark.class, true);
			//#21692 WIP - Start
			if (this.getManagedBean().getQt().getChmnFinalizationDate() == null) {
				QTScheduleDetailBackingBean qtScheBB = new QTScheduleDetailBackingBean();
				qtScheBB.deleteEpvAttachments();
			}
			if (this.getManagedBean().getQt().getResponsePeriod() != null) {
				int responsePeriod = this.getManagedBean().getScQtApprovalRequestList().get(0).getResponsePeriod() == null ? 
						this.getManagedBean().getQt().getResponsePeriod() : this.getManagedBean().getScQtApprovalRequestList().get(0).getResponsePeriod();
				this.getManagedBean().getScQtApprovalRequestList().get(0).setResponsePeriod(responsePeriod);			
				initializeDecisionCertPBM();
				page = QTConstants.EXTEND_PROPOSAL_VALIDITY_PENDING_APPROVAL;
			}
			//#21692 WIP - End
			qtPageFirst = false;
		}
		//#21692 Re-open - End
		else if(SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName) 
				|| SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)){
			log.info("*********** qtPageFirst");
			qtPageFirst = true;
		}
		
		// ***25692
		if(qtPageFirst){
			String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
			QTDetailBackingBean qtDetailBB = new QTDetailBackingBean();
			page = qtDetailBB.editQuationTenderDetails(getManagedBean().getQt(), false);
			qtDetailBB.getManagedBean().setAdditionalPages(null);
			qtDetailBB.getManagedBean().setUserTasks(null);
		    Long statusId = qtDetailBB.getManagedBean().getQt().getQtStatus().getStatusId();
		    boolean isQTStatusScheduleDocAndAfter = qtDetailBB.isQTStatusGreaterThanOrEquals(statusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
		    boolean isQTStatusBtwScheduleDocAndPendingPublication = qtDetailBB.isQTStatusInBetween(statusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
		    boolean isQTStatusSCApprovalAndBefore = qtDetailBB.isQTStatusLessThanOrEquals(statusId, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL);
		    boolean isCmtStatusCompletedAndBefore = qtDetailBB.isStatusLessThanOrEquals(statusId, QTStatusConst.CMT_COMPLETED, QTStatusConst.CMT_ORDERED_STATUSES);
		    
		    if(SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)){
				qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.BPKApproval.toString(), pageAppr);
				qtDetailBB.getManagedBean().addUserTasks(UserTasks.BPKApproval);
				//#25692-start
				ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(
						ParameterConst.DOCTYPE_QUOTATION,qtDetailBB.getManagedBean().getQt().getQtId());
				if(null!=workFlowStatus){
				     qtDetailBB.getManagedBean().getQt().setQtStatus(workFlowStatus.getPmStatus());
				     statusId = qtDetailBB.getManagedBean().getQt().getQtStatus().getStatusId();
				     isQTStatusScheduleDocAndAfter = qtDetailBB.isQTStatusGreaterThanOrEquals(statusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
				     isQTStatusBtwScheduleDocAndPendingPublication = qtDetailBB.isQTStatusInBetween(statusId, QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
				     isQTStatusSCApprovalAndBefore = qtDetailBB.isQTStatusLessThanOrEquals(statusId, QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL);
				     isCmtStatusCompletedAndBefore = qtDetailBB.isStatusLessThanOrEquals(statusId, QTStatusConst.CMT_COMPLETED, QTStatusConst.CMT_ORDERED_STATUSES);
				}
				//#25692-End   
				if(isQTStatusScheduleDocAndAfter){
					qtDetailBB.getManagedBean().setActiveIndex(9);
				}
				if(isQTStatusBtwScheduleDocAndPendingPublication){
					qtDetailBB.getManagedBean().setActiveIndex(10);//#29474
				}
				if(isQTStatusSCApprovalAndBefore){
					qtDetailBB.getManagedBean().setActiveIndex(5);
				}
				log.info("*********** preRenderView:userHasRole(BPK_APPROVER)");
				//#25692-start
				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(qtDetailBB.getManagedBean().getQt().getQtId(), docType==null?ParameterConst.DOCTYPE_QUOTATION:ParameterConst.DOCTYPE_BPK_APPROVAL);
				this.getManagedBean().setSubStatusId(sts.getStatusId());
				//#25692-End
			}
			else if(SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName) ){
				// 25037 - start
				if (CollectionUtils.isNotEmpty(this.getManagedBean().getScCommitteeMemberTabList())) {
					List<TabBean> cTabs = this.getManagedBean().getScCommitteeMemberTabList();
					String currentCommitteeType = cTabs.get(this.getManagedBean().getSelectedCommitteeTabIndex())
							.getTabType();
					getManagedBean().setRoleFilterList(
							getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(),
									currentCommitteeType));
				}
				if (this.getManagedBean().isCommitteeReviseFlow()) {
					getManagedBean().setRoleFilterList(new String[] { ParameterConst.COMMITTEE_ROLE_SECRETARY });
				}
				// 25037 - end
				List<ScQtCommittee> cmts = null;
				if(this.getManagedBean().getQt()!=null) { cmts = this.getManagedBean().getQt().getScQtCommittees();}
				boolean isEcOcTecFec = false;
				for(int i = 0; cmts!=null && i<cmts.size(); i++){
					if(CommitteeTypeIsEcOcTecFec(cmts.get(i).getScCommittee())){
						isEcOcTecFec = true;
						break;
					}
				}
				// 25037 - start
				boolean isFullTablist = false;
				// #25692-start
				ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION,
						qtDetailBB.getManagedBean().getQt().getQtId());
				if (null != workFlowStatus) {
					qtDetailBB.getManagedBean().getQt().setQtStatus(workFlowStatus.getPmStatus());
					Long qtStatusId = workFlowStatus.getPmStatus().getStatusId();
					isFullTablist = qtDetailBB.isQTStatusGreaterThanOrEquals(qtStatusId,
							QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);

				}
				//#25692-End
				if(isEcOcTecFec || isFullTablist){
				// 25037 - end
					qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString(), pageAppr);
					qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
					qtDetailBB.getManagedBean().setActiveIndex(9);
					if(isQTStatusScheduleDocAndAfter){
						qtDetailBB.getManagedBean().setActiveIndex(9);
					}
					if(isQTStatusBtwScheduleDocAndPendingPublication){
						qtDetailBB.getManagedBean().setActiveIndex(9);
					}
					if(isQTStatusSCApprovalAndBefore){
						qtDetailBB.getManagedBean().setActiveIndex(5);
					}
					if(isCmtStatusCompletedAndBefore){
						qtDetailBB.getManagedBean().setActiveIndex(10);//#29474
					}
					log.info("*********** preRenderView:userHasRole(COMMITTEE_APPROVER) for ecOcTecFec");
				}
				else{
					qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(), pageAppr);
					qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApproval);
					qtDetailBB.getManagedBean().setActiveIndex(5);
					log.info("*********** preRenderView:userHasRole(COMMITTEE_APPROVER)");
				}
				//#25692-start
				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(qtDetailBB.getManagedBean().getQt().getQtId(), docType==null?ParameterConst.DOCTYPE_QUOTATION:ParameterConst.DOCTYPE_COMMITTE_APROVAL);
				this.getManagedBean().setSubStatusId(sts.getStatusId());
				//#25692-End
				
			}
			else if(SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)){
				qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.PublicationApproval.toString(), pageAppr);
				qtDetailBB.getManagedBean().addUserTasks(UserTasks.PublicationApproval);
				qtDetailBB.getManagedBean().setActiveIndex(10);//#29474
				qtDetailBB.getManagedBean().setFirstPagePA(qtPageFirst); //#25077 
				log.info("*********** preRenderView:userHasRole(PUBLICATION_APPROVER)");
			}
			else{
				log.info("*********** preRenderView:userHasRole()");
			}
				
			
			
			/*
			 * copying required(called in xhtml) fields in this.getManagedBean() to qtDetailBB.getManagedBean()
			 */
		    qtDetailBB.getManagedBean().setApprovalType(this.getManagedBean().getApprovalType());
			qtDetailBB.getManagedBean().setAutoUpdate(this.getManagedBean().isAutoUpdate());
			qtDetailBB.getManagedBean().setCurrentStatusId(this.getManagedBean().getCurrentStatusId());
			qtDetailBB.getManagedBean().setOutComes(this.getManagedBean().getOutComes());
			qtDetailBB.getManagedBean().getQt().setScQtCommittees(this.getManagedBean().getQt().getScQtCommittees());
			qtDetailBB.getManagedBean().getQt().setNewPublishDate(this.getManagedBean().getQt().getNewPublishDate());
			qtDetailBB.getManagedBean().setQtCommitteeRemarks(this.getManagedBean().getQtCommitteeRemarks());
			qtDetailBB.getManagedBean().setQtSupplierList(this.getManagedBean().getQtSupplierList());
			qtDetailBB.getManagedBean().setScCommitteeMemberTabList(this.getManagedBean().getScCommitteeMemberTabList());
			qtDetailBB.getManagedBean().setScQtApproval(this.getManagedBean().getScQtApproval());
			qtDetailBB.getManagedBean().setScQtApprovalRequestList(this.getManagedBean().getScQtApprovalRequestList());
			qtDetailBB.getManagedBean().setSelectedTabBean(this.getManagedBean().getSelectedTabBean());
			qtDetailBB.getManagedBean().setSelectedCommitteeTabIndex(this.getManagedBean().getSelectedApprovalTabIndex());
			qtDetailBB.getManagedBean().setTaskName(this.getManagedBean().getTaskName());
			qtDetailBB.getManagedBean().setApprovalEditableSection(true);
			qtDetailBB.getManagedBean().setApprovalScreen(true);
			
			//Bug #31991 - start
			//qtDetailBB.getManagedBean().getScQtApproval().setScQtApprovalAttachments(this.getManagedBean().getScQtApproval().getScQtApprovalAttachments());
			qtDetailBB.getManagedBean().setAttachments(qtDetailBB.getManagedBean().getScQtApproval().getScQtApprovalAttachments());
			if(this.getManagedBean().getScQtApproval().getScQtApprovalAttachments() != null){
				logger.info("this getScQtApprovalAttachments() size : " + this.getManagedBean().getScQtApproval().getScQtApprovalAttachments().size());
			}
			if(qtDetailBB.getManagedBean().getScQtApproval().getScQtApprovalAttachments() != null){
				logger.info("qtDetailBB getScQtApprovalAttachments() size : " + qtDetailBB.getManagedBean().getScQtApproval().getScQtApprovalAttachments().size());
			}
			//Bug #31991 - end
			
			/* ***#25692#111 - start */
			PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(qtDetailBB.getManagedBean().getQt().getQtId(), docType==null?ParameterConst.DOCTYPE_QUOTATION:docType);
			if(sts != null){
				qtDetailBB.getManagedBean().setCurrentStatusId(sts.getStatusId());
			}
			/* ***#25692#111 - end */

			boolean readOnlyOri = qtDetailBB.getManagedBean().isReadOnly();
			this.getManagedBean().copyBPMTaskDetails(qtDetailBB.getManagedBean());
			qtDetailBB.getManagedBean().setReadOnly(readOnlyOri);
			// #10141 Start Vinod
			qtDetailBB.checkInactiveChecklistAndDisplayWarning(this.getManagedBean().getTaskName());
			// #10141 End Vinod
			
			if(page.equalsIgnoreCase(QTConstants.MAINTANANCE_PAGE)){
				page = QTConstants.MAINTANANCE_PAGE_FULL;				
			}
		}
		
		context.getApplication().getNavigationHandler().handleNavigation(context, null, page);
	}

	private void setFCApproveQTChangeValues(List<ScQtApprovalRequest> approvalRequests) {

		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				PmParameter pmParameter = new PmParameter();
				if (approvalRequest.getDecisionType() == null) {
					approvalRequest.setDecisionType(pmParameter);
				}

				ScQt scQt = getManagedBean().getQt();
				if (scQt.getMofDate() != null) {
					if (approvalRequest.getDecisionCertPBM() == null) {
						approvalRequest.setDecisionCertPBM(pmParameter);
					}
				}
			}
		}
	}

	private ScWorkFlowStatus retrieveStatusByTypeAndId(String docType, Long docId) {
		IScWorkFlowStatusSO workFlowStatusSO;
		try {
			workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocType(docType);
			reqDTO.setDocId(docId);
			
			return workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		} finally {
			workFlowStatusSO = null;
		}
	}

	//#21692 - Start
	/**
	 * Save used by Proposal Validity Extension, will save all data in
	 * ScQtApprovalRequest, including attachments, dates, response period and etc.
	 * 
	 */
	public void saveAction() {
		log.info("Save proposal validity Period (Days) extension");
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		ScQt scQt = this.getManagedBean().getQt();

		reqDTO.setQtdetail(scQt);
		//#21692 add data for report/history usage - Start
		List<ScQtApprovalRequest> scQtApprovalRequests = new ArrayList<>();
		for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
			
			approvalRequest.setProposalValidityEndDateOri(this.getManagedBean().getQt().getProposalValidityEndDate());
			approvalRequest.setProposalValidityEndDateNew(getNewValidityDueDate(this.getManagedBean().getQt(), 
					Integer.parseInt(approvalRequest.getChangedValue())));
			approvalRequest.setMofDate(this.getManagedBean().getQt().getMofDate());
			//#21692 - ensure that is date of FC chairperson less than QT validity, date of MOF = null
			if (!scQt.isApprovalPBM()) {
				approvalRequest.setMofDate(null);
			}
			approvalRequest.setChmnFinalizationDate(this.getManagedBean().getQt().getChmnFinalizationDate());
			if (approvalRequest.getDecisionType() != null
					&& approvalRequest.getDecisionType().getParameterType() == null) {
				approvalRequest.setDecisionType(null);
			}
			
			if (approvalRequest.getDecisionCertPBM() != null
					&& approvalRequest.getDecisionCertPBM().getParameterType() == null) {
				approvalRequest.setDecisionCertPBM(null);
			}

			//#21692 #note-504 - Start
			//To remove attachments R1, R2, R3
			approvalRequest = removeAttachments(approvalRequest);
			//To add attachments from method epvHandleFileUpload, IF not empty
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getAllEPVAttachments())) {
				approvalRequest.setEpvApprovalReqAttachment(this.getManagedBean().getAllEPVAttachments());
			}
			//#21692 #note-504 - End

			scQtApprovalRequests.add(approvalRequest);
		}
		//#21692 add data for report/history usage - End
		
		reqDTO.setApprovalRequests(this.getManagedBean().getScQtApprovalRequestList());
		
		QuationTenderResDTO resDTO = getQTDetailSO().saveActionApprovalReq(reqDTO);

		if (resDTO.isSuccessful()) {
			ScQt scQtDetail = resDTO.getQtDetail();
			successMessage(MessageConst.INFO_700025, ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION, scQtDetail.getQtNo(), Boolean.TRUE);
		}
	}
	//#21692 - End

	//#21692 - Start
	private void checkValidProposalPeriodExtension(QuationTenderReqDTO reqDTO) {
		ScQt scQt = reqDTO.getQtdetail();
		boolean enableDateMOF = scQt.isApprovalPBM();

		if (scQt.getChmnFinalizationDate() == null) {
			dateMandatoryMessage("lbl.sc.qt.proposal.approval.date.chairperson.finalization");
		}

		if (enableDateMOF && !getManagedBean().isChmnFinalizationRejectEPV() && scQt.getMofDate() == null) {
			dateMandatoryMessage("lbl.sc.qt.proposal.approval.date.mof");
		}

		List<ScQtApprovalRequest> approvalRequests = reqDTO.getApprovalRequests();
		for (ScQtApprovalRequest approvalRequest : approvalRequests) {
			decisionChecking(approvalRequest, enableDateMOF);
		}
	}
	//#21692 - End
	
	//#21692 - Start
	private void decisionChecking(ScQtApprovalRequest approvalRequest, boolean enableDateMOF) {
		if (approvalRequest.getDecisionType().getParameterId() == null) {
			if (enableDateMOF) {
				//#21692 - start
				if (null != approvalRequest.getDecisionCertPBM().getParameterId()
						&& approvalRequest.getDecisionCertPBM().getParameterCode().equals(ParameterConst.TR2_ACKNOWLEDGE)) {
					mandatoryByMessage("lbl.sc.qt.proposal.extension.mof");
				}
				//#21692 - end
			} else {
				mandatoryByMessage("lbl.sc.qt.proposal.decision.chairperson.finalization");
			}
		}

		if (enableDateMOF && approvalRequest.getDecisionCertPBM().getParameterId() == null) {
			mandatoryByMessage("lbl.sc.qt.proposal.decision.mof");
		}		
	}
	//#21692 - End

	private void mandatoryByMessage(String message) {
		throw new EpException(MessageConst.ERROR_770400, new String[] {ResourceBundleUtils.getMessage(message, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale())});//#21692
	}
	
	private void dateMandatoryMessage(String message) {
		throw new EpException(MessageConst.ERROR_770403, new String[] {ResourceBundleUtils.getMessage(message, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale())});//#21692
	}

	//#21692 - Start
	/**
	 * Procurement Board Secretary's Submission action for status Pending Approval.
	 * Approval will be based on Decision column in supplierFinalizationCommitteeApproval.xhtml
	 * it can be Approved, Rejected, Revised.
	 * Rejected and Revised will use same flow, but different Parameters and Notifications.
	 */
	public void submitAction() {
		log.info("Submit proposal validity Period (Days) extension");

		this.getManagedBean().setProposalValidityRevised(false);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		ScQt scQt = this.getManagedBean().getQt();
		checkResponsePeriod();
		scQt.setResponsePeriod(this.getManagedBean().getScQtApprovalRequestList().get(0).getResponsePeriod());

		reqDTO.setQtdetail(scQt);
		reqDTO.setApprovalRequests(this.getManagedBean().getScQtApprovalRequestList());

		checkValidProposalPeriodExtension(reqDTO);
		//#21692 - start
		responsePeriodValidations(reqDTO.getQtdetail());
		checkAttachedDocument(reqDTO);
		//#21692 - end

		boolean enableDateMOF = scQt.isApprovalPBM();
		if (!enableDateMOF) {
			scQt.setMofDate(null);
		}
		this.getManagedBean().setQt(scQt);

		List<ScQtApprovalRequest> scQtApprovalRequests = new ArrayList<>();
		for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
			PmParameter pmParameter = getQTDetailSO().getPmParameterByTypeAndCode(
					ParameterTypeConst.APPROVAL_DECISION_TYPE, approvalRequest.getDecisionType().getParameterCode());
			approvalRequest.setApproverAction(pmParameter);

			if (!enableDateMOF) {
				approvalRequest.setDecisionCertPBM(null);
			}
			
			//#21692 add data for report/history usage - Start
			approvalRequest.setProposalValidityEndDateOri(this.getManagedBean().getQt().getProposalValidityEndDate());
			approvalRequest.setProposalValidityEndDateNew(getNewValidityDueDate(this.getManagedBean().getQt(), 
					Integer.parseInt(approvalRequest.getChangedValue())));
			approvalRequest.setMofDate(this.getManagedBean().getQt().getMofDate());
			//#21692 - ensure that is date of FC chairperson less than QT validity, date of MOF = null
			if (!scQt.isApprovalPBM()) {
				approvalRequest.setMofDate(null);
			}
			approvalRequest.setChmnFinalizationDate(this.getManagedBean().getQt().getChmnFinalizationDate());
			checkValidityDateAndSupplierResponseDate();
			//#21692 add data for report/history usage - End

			//#21692 #note-504 - Start
			//To remove attachments R1, R2, R3
			approvalRequest = removeAttachments(approvalRequest);
			//To add attachments from method epvHandleFileUpload, IF not empty
			if (CollectionUtils.isNotEmpty(this.getManagedBean().getAllEPVAttachments())) {
				approvalRequest.setEpvApprovalReqAttachment(this.getManagedBean().getAllEPVAttachments());
			}
			//#21692 #note-504 - End

			scQtApprovalRequests.add(approvalRequest);
		}

		this.getManagedBean().setScQtApprovalRequestList(scQtApprovalRequests);
		checkNullRemark();
		for (ScQtApprovalRequest request : this.getManagedBean().getScQtApprovalRequestList()) {
			procurementBoardSecretaryDecision(request);
		}
	}
	//#21692 - End
	
	//#21692 sonarlint - Start
	private void procurementBoardSecretaryDecision(ScQtApprovalRequest approvalRequest) {

		Long decisionType = approvalRequest.getDecisionType().getParameterId();

		if (QTStatusConst.PCB_RQ1_APPROVED.longValue() == decisionType.longValue()
				|| QTStatusConst.PCB_RQ3_APPROVED.longValue() == decisionType.longValue()) { //1834L || 1854L
			
			approvalBpmTask(BpmOutcomeConst.BPM_APPROVE);
			notifyUsers(approvalRequest);
		} else if (QTStatusConst.PCB_RQ1_REJECTED.longValue() == decisionType.longValue()
				|| QTStatusConst.PCB_RQ3_REJECTED.longValue() == decisionType.longValue()) { //1835L || 1855L

			reviseRemarkMandatory();//#21692 remark is mandatory for reject.
			approvalBpmTask(BpmOutcomeConst.BPM_REJECT);
			notifyUsers(approvalRequest);
			//previously line of code for Tracking Diary, removed to avoid duplication
		} else if (QTStatusConst.PCB_RQ1_REVISED.longValue() == decisionType.longValue()
				|| QTStatusConst.PCB_RQ3_REVISED.longValue() == decisionType.longValue()) {
			reviseRemarkMandatory();
			this.getManagedBean().setProposalValidityRevised(true);
			approvalBpmTask(BpmOutcomeConst.BPM_REJECT);//Revised, but use reject due to Revise flow == Reject flow
			notifyUsers(approvalRequest);
		}
	}
	//#21692 sonarlint - End
	
	/**
	 * Submit Approval Details. Event handle to submit data for bpm.
	 * 
	 * @param outCome
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public void approvalBpmTask(String action) {
		String showDialog = null;
		boolean isGpkiPopup = false;
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtdetail(this.getManagedBean().getQt());
		reqDTO.setApproval(this.getManagedBean().getScQtApproval());
		reqDTO.setApprovalRequests(this.getManagedBean().getScQtApprovalRequestList());
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		
		this.getManagedBean().setAction(action);
		
		String taskName = this.getManagedBean().getTaskName();
		logger.debug("Task Name : " + taskName);
		
		if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
			boolean isRevised = false;
			boolean isApproved = false;
			boolean isRejected = false;
			
			for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
				String approverAction = approvalRequest.getApproverAction().getParameterCode();
				//#25692-start
				if(null==approverAction){
				String	approverActionmsg = ResourceBundleUtils.getMessage(
							"lbl.sc.decision",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				    this.getManagedBean().setActiveIndex(9);
					throw new EpException(approverActionmsg);
				} //#25692-End
				
				if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED.equalsIgnoreCase(approverAction) ||
						ParameterConst.RESTR_APPROVAL_DECISION_TYPE_REVISE.equalsIgnoreCase(approverAction)) {
					isRevised = true;
					
				} else if (ParameterConst.APPROVAL_DECISION_TYPE_APPROVED.equalsIgnoreCase(approverAction) ||
						ParameterConst.RESTR_APPROVAL_DECISION_TYPE_AGREED.equalsIgnoreCase(approverAction) ||
						ParameterConst.RESTR_APPROVAL_DECISION_TYPE_PROCEED.equalsIgnoreCase(approverAction)) {
					isApproved = true;
					
				} else if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED.equalsIgnoreCase(approverAction)) {
					isRejected = true;
				}
			}
			
			if (isRevised) {
				action = BpmOutcomeConst.BPM_REVISE;
				showDialog = "reviceApprovalDlg.show()";
			} else if (isApproved) {
				action = BpmOutcomeConst.BPM_APPROVE;
//				showDialog = "gpki_dlg.show()";
//				isGpkiPopup = true;
				gpkiSignAction(null);
			} else if (isRejected) {
				action = BpmOutcomeConst.BPM_REJECT;
				showDialog = "rejectApprovalDlg.show()";
			}
			
		} else {
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				showDialog = "reviceApprovalDlg.show()";
				// #32376 - Start
				if(SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName) || 
						SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)){//#34694
					showDialog = "reviceApprovalDlgTemp.show()";
				}
				// #32376 - End
			} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(action)
					&& !SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) {//#21692 Re-open
				showDialog = "rejectApprovalDlg.show()";
			} else {
				if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL.equalsIgnoreCase(this
						.getManagedBean().getApprovalType())) {
					if(this.getManagedBean().getQt().isSampleReq()){//#29762-start
					checkSampleRequiredChecking(this.getManagedBean().getQt());
					}//#29762-End
					detailSO.validatePublicationScheduleDetails(reqDTO);
					// detailSO.validateScheduleDetails(reqDTO);
				} else if(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT.equalsIgnoreCase(this
						.getManagedBean().getApprovalType())){
					
					List<TabBean> qtCommittes = this.getManagedBean().getScCommitteeMemberTabList();
					if (CollectionUtils.isNotEmpty(qtCommittes)) {
						List<ScQtCommittee> qtCommitees = new ArrayList<ScQtCommittee>(
								qtCommittes.size());
						for (int i = 0; i < qtCommittes.size(); i++) {
							ScQtCommittee qtCommittee = qtCommittes.get(i)
									.getScQtCommittee();
							if(null!=qtCommittee){ //#29724-start
							qtCommittee.setQtCommitteRemarks(this.getManagedBean()
									.getQtCommitteeRemarks());
							qtCommitees.add(qtCommittee);
							} //#29724-End
						}
						reqDTO.getQtdetail().setScQtCommittees(qtCommitees);
					}
					ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,ICommitteSO.class);
					CommitteReqDTO committeReqDTO = new CommitteReqDTO();
					committeReqDTO.setScQt(reqDTO.getQtdetail());
					FacesScopes scope = new FacesScopes();
					committeReqDTO.setPmUser(scope.getUserSessionBean());
					committeReqDTO.setUserLocale(scope.getUserLocale());
					committeReqDTO.setSpecApprovalReq(true);
					committeSO.validateAppointCommitteDtls(committeReqDTO);
				}
				
				if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)
						&& BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
					approveCancelQTwithDOASenariosExists(this.getManagedBean().getQt()); // #25426
				}

				
				if(SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName) || 
						SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) {
					gpkiSignAction(null);
				}
				// 25037 - start
				else if (SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)) {
					isGpkiPopup = assertSpecificationUpdateInProgress();
					showDialog = isGpkiPopup ? "" : QTConstants.QT_GPKI_DLG_SHOW;// 25037
				}
				// 25037 - end
				else {
					showDialog = "gpki_dlg.show()";
					isGpkiPopup = true;
				}
			}
		}
		
		if (StringUtils.isNotBlank(showDialog)) {
			reqDTO.setOutcome(action);
			detailSO.validateQtApprovalDtl(reqDTO);
			if (isGpkiPopup) {
				getManagedBean().setAutoUpdate(false);	
			}
			RequestContext.getCurrentInstance().execute(showDialog);	
		}
	}

	/**
	 * Gpki sign action.Event handle when Approver submit.
	 * 
	 * @param digiSign
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public void gpkiSignAction(PmDigiSign digiSign) {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean().getAction(), digiSign);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL.equalsIgnoreCase(this.getManagedBean().getApprovalType())) {
				log.info("Publication Approval executed");
				// #14398 - Start.
				if (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL_RESCHEDULE.equals(this.getManagedBean().getCurrentStatusId())) {
					successMessage(MessageConst.INFO_700069, getMessageCommon("lbl.sc.qt.public.approval.reschedule.approve.message"),
							qt.getQtNo(), Boolean.TRUE);
				} else {
					successMessage(MessageConst.INFO_700069, getMessageCommon("lbl.sc.qt.public.approval.approve.message"),
							qt.getQtNo(), Boolean.TRUE);
				}
				// #14398 - End.
			}
			// 25037 - start
			else if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT.equalsIgnoreCase(this.getManagedBean()
					.getApprovalType())) {
				Long statusId = this.getManagedBean().getCurrentStatusId() != null ? this.getManagedBean()
						.getCurrentStatusId() : 0L;
				if (!Arrays.asList(
						new Long[] { QTStatusConst.CMT_PENDING_REAPPROVAL,
								QTStatusConst.CMT_PENDING_REAPPROVAL_REVISION_APPROVAL }).contains(statusId)) {
					successMessage(getMessgeConstant(this.getManagedBean().getAction()),
							getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
							qt.getQtNo(), true);
				} else {
					String procurementMode = this.getParameterDesc(qt.getProcurementMode());
					String qtNo = qt.getQtNo();
					successMessage(MessageConst.INFO_760505, new String[] { getCommitteeNames(qt.getScQtCommittees()),
							procurementMode, qtNo }, true);
				}
			}
			// 25037 - end
			else{
				log.info("Other Approval executed");
				//#21692 Re-open - Start
				if (null != getManagedBean().getTaskName()
						&& SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(getManagedBean().getTaskName())) {
					successMessage(getFCMessgeConstant(this.getManagedBean().getAction()),
							getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
							qt.getQtNo(), Boolean.TRUE);
				} else {
					successMessage(getMessgeConstant(this.getManagedBean().getAction()),
							getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
							qt.getQtNo(), Boolean.TRUE);
				}
				//#21692 Re-open - End
			}
		}
	}

	/**
	 * Update ApprovalDetails.
	 * 
	 * @param action
	 *            (Mandatory)
	 * @param digiSign
	 *            (optional)
	 * @return QuationTenderResDTO (boolean successful,ScQtCommittee committee)
	 */
	private QuationTenderResDTO updateBpmTask(String outcome, PmDigiSign digiSign) {
		long startTime = System.currentTimeMillis();
		log.info("ApprovalBackingBean.updateBpmTask(): startTime in milli seconds is: " + startTime + " : QT No. is: " + this.getManagedBean().getQt().getQtNo());
		FacesScopes facesScopes = new FacesScopes();
		String taskName = this.getManagedBean().getTaskName();
		QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
		
		try {
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtStatus(null);
			qt.setScQtBriefings(null);
			log.info("^^^^^ updateBpmTask call for QT: "+qt.getQtNo());

			CommitteResDTO committeResDto = null;
			// QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
				// setting the remarks to ScQtApprovalRemarks
				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				List<String> committeeTypes = new ArrayList<String>();

				updateApprovalRequest(this.getManagedBean().getScQtApprovalRequestList(), outcome);
				finalizeRemarks(approval.getScQtApprovalRemarks());
				List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
				qtApproval.add(approval);
				// approval.getScQtApprovalDtls().clear();
				approval.setScQtApprovalDtls(this.getManagedBean()
						.getScQtApprovalRequestList());
				qt.setScQtApprovals(qtApproval);

				List<TabBean> qtCommittes = this.getManagedBean()
						.getScCommitteeMemberTabList();

				if (CollectionUtils.isNotEmpty(qtCommittes)) {
					List<ScQtCommittee> qtCommitees = new ArrayList<ScQtCommittee>(
							qtCommittes.size());
					for (int i = 0; i < qtCommittes.size(); i++) {
						ScQtCommittee qtCommittee = qtCommittes.get(i)
								.getScQtCommittee();
                       if(null!=qtCommittee){//#29724_start
						if (BpmOutcomeConst.BPM_REVISE
								.equalsIgnoreCase(outcome)) {
							qtCommittee
									.getScCommittee()
									.setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_PENDING_REVISION));
						} else {
							qtCommittee.getScCommittee().setCommitteStatus(getPmStatusByStatusId(QTStatusConst.CMT_COMPLETED));

						}

						qtCommittee.setQtCommitteRemarks(this.getManagedBean()
								.getQtCommitteeRemarks());
						qtCommitees.add(qtCommittee);
						committeeTypes.add(qtCommittee.getScCommittee().getCommitteeType().getParameterCode());
                       }//#29724-End
					}
					qt.setScQtCommittees(qtCommitees);
				}
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(facesScopes
						.getBPMAuthenticationCtxToken());
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(this.getManagedBean()
						.getPayLoad());
				committeReqDTO.setOutcome(outcome);
				committeReqDTO
						.setPmUser(new FacesScopes().getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
				committeReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				finalizeRemarks(this.getManagedBean().getScQtApproval()
						.getScQtApprovalRemarks());
				committeReqDTO.setScQt(qt);
				committeReqDTO.setQtCommittee(qt.getScQtCommittees());

				// committeReqDTO.setUser(new
				// FacesScopes().getUserSessionBean());

				if (null != digiSign) {
					updatePmDigiSignData(digiSign,
							ParameterConst.DOCTYPE_COMMITTE_APROVAL,
							qt.getQtId(), qt.getQtNo());
				}
				committeReqDTO.setDigiSignData(digiSign);
				committeReqDTO.setSpecApprovalReq(true);
				committeReqDTO.setApprovalFlow(true);
				committeReqDTO.setSaveFlow(false);
				committeReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				committeReqDTO.setToBeDeletedMembers(this.getManagedBean()
						.getToBeDelCommitteMemebers());
				//#28895 - 35500 start
				committeReqDTO.setNewScCommitteeMemberAuditTrailList(this.getManagedBean()
						.getNewScCommitteeMemberAuditTrailList());
				committeReqDTO.setToBeRemovedCommitteeMemberAuditTrailList(this.getManagedBean()
						.getDeletedScCommitteeMemberAuditTrailList());
				//#28895 - 35500 end
				committeReqDTO.setCommitteeTypes(committeeTypes);
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				tenderResDTO.setSuccessful(committeResDto.isSuccessful());
				tenderResDTO.setQtDetail(qt);
			
			} else if(SCProcessNameConst.DEMO_APPROVE_CANCELLATION.equalsIgnoreCase(taskName)){ //#28784
				DemoResDTO resDTO = new DemoResDTO();
				DemoInvitationBackingBean demoBB = new DemoInvitationBackingBean();
				resDTO.setScQt(qt);
				resDTO.setPmUser(new FacesScopes().getUserSessionBean());				
				resDTO.setOutcome(BpmOutcomeConst.BPM_APPROVE);
				resDTO.setPayLoad(this.getManagedBean().getPayLoad());
				resDTO.setTaskName(taskName);
				resDTO.setTaskId(this.getManagedBean().getTaskId());
				resDTO = demoBB.demoUpdateBpmTask(resDTO, outcome);
				tenderResDTO.setSuccessful(resDTO.isSuccessful());
				tenderResDTO.setQtDetail(qt);			
				
			} else {
				
				boolean isFinalApproval = false;
				boolean bpkRevise = false;
				List<String> approvalTypes = null;
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				SCQuotationTenderDataType dataType = (SCQuotationTenderDataType)this.getManagedBean().getPayLoad();
				
				qtReqDTO.setApprovalFlow(true);
				qtReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
				qtReqDTO.setUserLocale(new FacesScopes().getUserLocale());
				
				IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
				String approvalDocType = ParameterConst.DOCTYPE_QUOTATION;
				// Bug #31759 - Start
				checkApprovalAndRefreshDetails (qt.getQtId(),taskName, this.getManagedBean().getScQtApproval());
				// Bug #31759 - End
				invokeRemarksWhileExecuteReviseFlow(outcome); // 25077
				ScQtApproval approval = this.getManagedBean().getScQtApproval();

				String approvalCategoryCode = approval.getApprovalCategory().getParameterCode();
				qtReqDTO.setApprovalCategory(approvalCategoryCode);
				
				List<ScQtApprovalRequest> approvalRequests = this.getManagedBean().getScQtApprovalRequestList();
				
				if (ParameterConst.APPROVAL_CATEGORY_BPK.equalsIgnoreCase(approvalCategoryCode)) { //BPK
					approvalDocType = ParameterConst.DOCTYPE_BPK_APPROVAL;
					approval.setApproverSettings(null);

					String action = constuctOutCome(approvalRequests, qt);
					if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
						bpkRevise = true;
						
						// revert to old value if got any approval request rejected.
						revertToOldValues(qt, approvalRequests);
					} else if (BpmOutcomeConst.BPM_REJECT
							.equalsIgnoreCase(action)) {
						qtReqDTO.setRejected(true);
						revertToOldValues(qt, approvalRequests);
					}
					
				} else if (ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE.equalsIgnoreCase(approvalCategoryCode)) { //FC

					approvalRequests = proposalValidityWip(outcome);
					if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
						qtReqDTO.setRejected(true);
						revertToOldValues(qt, approvalRequests);
					} else if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
						//#21692 - start
						ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtId());
						wfStatus = wfStatus==null ? new ScWorkFlowStatus(): wfStatus;
						if(wfStatus.getPmStatus()!=null
								&& (QTStatusConst.QT_RE_FINALIZATION.equals(wfStatus.getPmStatus().getStatusId())
								|| QTStatusConst.QT_PENDING_RECOMMENDATION_REVISE_BRIEFING_PAPER.equals(wfStatus.getPmStatus().getStatusId()))) {
							QuationTenderReqDTO qtReqDTO1 = new QuationTenderReqDTO();
							qtReqDTO1.setQtdetail(qt);
							QuationTenderResDTO qtResDTO1 = detailSO.updateDataTypeSupplierList(qtReqDTO1);
							String[] supplierIdsArray = qtResDTO1.getDataTypeSupplierList();
							dataType.getSupplierList().clear();

							dataType.getSupplierList().addAll(
									Arrays.asList(supplierIdsArray));
						}
						//#21692 - end
						isFinalApproval = dataType != null && CollectionUtils.isEmpty(dataType.getSupplierList());
						this.getManagedBean().setNoSupplierExistForEPV(CollectionUtils.isEmpty(dataType.getSupplierList()));//#21692 note-569
						
						approvalTypes = approveApprovalRequests(qt, 
								approvalRequests, 
								isFinalApproval);
					}
				} else if (ParameterConst.APPROVAL_CATEGORY_PTJ
						.equalsIgnoreCase(approvalCategoryCode)) {
					List<ScQtBsv> scQtBsvs = new QTDetailBackingBean()
						.constructBriefingSiteVisitList(qt);
					qtReqDTO.setScQtBsvs(scQtBsvs);
					
					if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
						qtReqDTO.setRejected(true);
						revertToOldValues(qt, approvalRequests);
					} else if (BpmOutcomeConst.BPM_APPROVE
							.equalsIgnoreCase(outcome)) {
						approvalTypes = approveApprovalRequests(qt, approvalRequests, true);
					}
				}
				dataType.setBpkRevise(bpkRevise);
				
				updateApprovalRequest(approvalRequests, outcome);

				finalizeRemarks(approval.getScQtApprovalRemarks());
				// approval.getScQtApprovalDtls().clear();

				approval.setScQtApprovalDtls(approvalRequests);

				List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
				qtApproval.add(approval);
				qt.setScQtApprovals(qtApproval);

				qt.setScQtCommittees(null);
				qtReqDTO.setPayLoad(dataType);
				
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setQtdetail(qt);
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
				qtReqDTO.setOutcome(outcome);
				qtReqDTO.setUserLocale(facesScope.getUserLocale());
				qtReqDTO.setToBeRemoveRestrictedSupplierList(this.getManagedBean().getToBeRemoveRestrictedSupplierList());

				if (null != digiSign) {
					updatePmDigiSignData(digiSign, approvalDocType, qt.getQtId(), qt.getQtNo());
				}
				
				qtReqDTO.setDigiSignData(digiSign);
				
				tenderResDTO = (QuationTenderResDTO) detailSO.updateBPMAction(qtReqDTO);
				// tenderResDTO.setSuccessful(qtResDTO.isSuccessful());
				
				if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
					if (CollectionUtils.isNotEmpty(approvalTypes)) {
						/*if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL)) {
							Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
							if (!DateUtils.isDateGreater(qt.getPublishDate(), todayDate)) {
								try {
									log.info("call simple scheduler if QT is publishing today.");
									IQTNoticeBoardSO nbSO = BusinessDelegate.getInstance()
											.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
													IQTNoticeBoardSO.class);
									NoticeBoardReqDTO reqDTO = new NoticeBoardReqDTO();
									reqDTO.setScQt(qt);
									NoticeBoardResDTO resDTO =nbSO.publishQT(reqDTO);
									if(resDTO.isSuccessful()){
										log.info("Qt will be published as job was built successfully for QT : "+qt.getQtNo());
									}
								} catch (Exception e) {
									log.error(e.getMessage(), e);
								}
							}
						} else*/ if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE)) {
							new QTDocumentsBackingBean().reGenerateDocuments(
									qt.getQtId(), 
									QTFieldConst.FIELD_QT_PUBLISH_PERIOD);
							/*Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
							if (DateUtils.isDateGreater(qt.getClosingDate(), todayDate)) {
								// To remove the existing QT closing job from scheduler if any
								log.info("remove simple scheduler if QT is closing today.");
								try{
									IQTProposalSO proposalSO = BusinessDelegate.getInstance()
											.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
													IQTProposalSO.class);
									ProposalReqDTO reqDTO = new ProposalReqDTO();
									reqDTO.setScQt(qt);
									ProposalResDTO resDTO = proposalSO.deleteQtProposalCloseJob(reqDTO);
									if(resDTO.isSuccessful()){
										log.info("Simple job for propsoal close deleted successfully for QT : "+qt.getQtNo());
									}
								}catch(Exception e){
									log.error(e.getMessage(), e);
								}
							}*/
						} else if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD)) {
							if (isFinalApproval) {
								new QTDocumentsBackingBean().reGenerateDocuments(
										qt.getQtId(), 
										QTFieldConst.FIELD_QT_VALIDITY_PERIOD);
							}
						} else if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_UPDATE_BSV_DETAILS)) {
							new QTDocumentsBackingBean().reGenerateDocuments(
									qt.getQtId(), 
									QTFieldConst.FIELD_QT_BSVDETAILS);
						}			
					}	
					//28895-35500 Start
					IAuditTrailSO auditTrailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
							IAuditTrailSO.class);
					if (SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName) && auditTrailSO.isAuditConfig(QTAuditConstant.QT_CONFIG_ID)) {
						prepareQtMaintenanceAuditTrail(qt, tenderResDTO.getGroupTabSection(), null);
					}
					//28895-35500 End
				}				
				
			}
			log.info("ApprovalBackingBean.updateBpmTask(): duration in milli seconds is: " + (System.currentTimeMillis() - startTime) + " : QT No. is: " + qt.getQtNo());
		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError("QT", "updateBpmTask", this.getManagedBean().getQt().getQtNo(), "updateBpmTask",getUserIdSessionFromLoginId(), e);
			throw new EpException(MessageConst.ERROR_722632);
	    }
		return tenderResDTO;
	}
	// Bug #31759 - Start
	public void checkApprovalAndRefreshDetails (Long qtId, String taskName, ScQtApproval approval){

    	List<String> paramTypes = new ArrayList<String>();
    	List<ScQtApprovalRequest> approvalRequests = this
				.getManagedBean().getScQtApprovalRequestList();
		
		if (approvalRequests != null ){
		       for (ScQtApprovalRequest req : approvalRequests){
			      paramTypes.add(req.getApprovalType().getParameterCode()) ;
		       }
		}
	
		String approvalCategoryCode = approval.getApprovalCategory().getParameterCode();		
	
	 if ((SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.BPK_APPROVE_QT_CHANGE
						.equalsIgnoreCase(taskName) )
			&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_BPK)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);

		} else if (SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE
				.equalsIgnoreCase(taskName)
				&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_PTJ)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_PTJ;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);

		} else if (SCProcessNameConst.FC_APPROVE_QT_CHANGE
				.equalsIgnoreCase(taskName)
				&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
		
		} else if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE
						.equalsIgnoreCase(taskName)
			&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, 
					ParameterConst.APPROVAL_DECISION_TYPE_APPROVED);
			
		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)
				&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_BPK)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);

		} else if (SCProcessNameConst.APPROVE_PUBLICATION
				.equalsIgnoreCase(taskName)
				&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_PTJ)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_PTJ;
			getApprovalRequest(qtId,
					approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			
		}  else if (SCProcessNameConst.APPROVE_CANCELATION
				.equalsIgnoreCase(taskName)
				&& !approvalCategoryCode.equalsIgnoreCase(ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE)) {
			
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			getApprovalRequest(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_PCB_APPROVAL, paramTypes, null);

		}
	}
	
	private void getApprovalRequest(Long qtId,
			String approvalCategory, String docType, List<String> reqTypes, String approverAction) {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		String taskName = this.getManagedBean().getTaskName();

		tenderReqDTO.setQtId(qtId);
		tenderReqDTO.setDocType(docType);
		tenderReqDTO.setApprovalReqTypes(reqTypes);
		tenderReqDTO.setApprovalCategory(approvalCategory);
		tenderReqDTO.setApproverAction(approverAction);
		tenderReqDTO.setSuppAcceptedQtChange(
				SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(taskName));
		tenderReqDTO.setShowAmendmentData(
				SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName));
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retrieveApprovalsByQtIdNType(tenderReqDTO);

		this.getManagedBean().setScQtApproval(resDTO.getQtApproval());
	}
	//Bug #31759 - End
	
	

	/**
	 * Update Approval Revice from bpm.
	 * 
	 * @return the string
	 */
	public void updateReviceBpmTask() {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean()
				.getAction(), null);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			
			// #14398 - Start.
			IScWorkFlowStatusSO workFlowStatusSO;
			boolean isPendingReScheduleInvolved = false;
			try {
				workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
						ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
				
				ScWorkFlowStatusReqDTO qtReqDTO = new ScWorkFlowStatusReqDTO();
				qtReqDTO.setDocId(qt.getQtId());	
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(QTStatusConst.QT_PENDING_SCHEDULE_REVISION);
				qtReqDTO.setStatusIds(statusIds);
				
				List<ScWorkFlowStatus> statusList = workFlowStatusSO.retrieveWorkFlowStatus(qtReqDTO)
						.getWorkFlowStatusList();
				if (CollectionUtils.isNotEmpty(statusList) && statusList.size() > 0) {
					isPendingReScheduleInvolved = true;
					log.info("ApprovalBackingBean.updateReviceBpmTask(): isPendingReScheduleInvolved is: " + isPendingReScheduleInvolved);
				}
			}
			finally {
				workFlowStatusSO = null;
			}
			
			if (isPendingReScheduleInvolved) {
				log.info("---> isPendingReScheduleInvolvedisPendingReScheduleInvolved");
				successMessage(MessageConst.INFO_60119, new String[]{qt.getQtNo()}, true);
			}
			// #14398 - end
			// 25037 - start
			// 25037 - start - note 372
			else if (ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT.equalsIgnoreCase(this.getManagedBean()
					.getApprovalType())
					&& Arrays.asList(
							new Long[] { QTStatusConst.CMT_PENDING_REAPPROVAL,
									QTStatusConst.CMT_PENDING_REAPPROVAL_REVISION_APPROVAL }).contains(
							this.getManagedBean().getCurrentStatusId())) {
				// 25037 - end - note 372
				String procurementMode = this.getParameterDesc(qt.getProcurementMode());
				String qtNo = qt.getQtNo();
				successMessage(MessageConst.INFO_760510, new String[] { getCommitteeNames(qt.getScQtCommittees()),
						procurementMode, qtNo }, true);
			}
			// 25037 - end
			else {
			successMessage(getMessgeConstant(this.getManagedBean().getAction()),
					getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
					qt.getQtNo(), Boolean.TRUE);
			}
		}
	}
	
	/**
	 * Update Approval Reject from bpm.
	 * 
	 * @return the string
	 */
	public void updateRejectBpmTask() {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean()
				.getAction(), null);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();

				successMessage(getMessgeConstant(this.getManagedBean().getAction()),
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
						qt.getQtNo(), Boolean.TRUE);
		}
	}

	/**
	 * Construct committee tab bean.
	 * 
	 * @param qtCommittees
	 *            the qt committees
	 * @return the List<TabBean>
	 */
	public List<TabBean> constructCommitteTabBean(
			List<ScQtCommittee> qtCommittees,Long qtId) {

		String parameterType = ParameterTypeConst.SC_COMMITTEE_TYPE;
		ParameterHelper parameterHelper = ParameterHelper.getInstance();
		List<TabBean> committeTabs = new ArrayList<TabBean>();

		for (ScQtCommittee scQtCommittee : qtCommittees) {
			String committeeType = scQtCommittee.getScCommittee()
					.getCommitteeType().getParameterCode();

			if (!ParameterConst.COMMITTEE_TYPE_BUSINESS_OWNER
					.equals(committeeType)) {
				TabBean bean = new TabBean(parameterHelper.getParameterDesc(
						parameterType, committeeType), committeeType,
						getQtCommitteeTypeDtl(scQtCommittee.getScCommittee()
								.getCommitteeType().getParameterCode(),
								qtId));
				committeTabs.add(bean);
			}
		}
		// #25037-start #372
		Collections.sort(committeTabs, new Comparator<TabBean>() {
			List<String> sortOrder = Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST);
			@Override
			public int compare(TabBean o1, TabBean o2) {
				return Integer.valueOf(sortOrder.indexOf(o1.getTabType())).compareTo(
						Integer.valueOf(sortOrder.indexOf(o2.getTabType())));
			}
		});
		// #25037-end #372
		return committeTabs;
	}

	/**
	 * Gets the ScQtCommittee Details.
	 * 
	 * @param type
	 *            the type
	 * @param id
	 *            the id
	 * @return ScQtCommittee
	 */
	private ScQtCommittee getQtCommitteeTypeDtl(String type, Long id) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ScQtCommittee qtCommitte = null;
		reqDTO.setCommitteeType(type);
		reqDTO.setQtId(id);
		CommitteResDTO resDTO = null;
		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO.retrieveCommitteByQtIdNCommType(reqDTO);
			qtCommitte = resDTO.getScQtCommittee();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return qtCommitte;
	}

	/**
	 * Gets the ScQtApproval Details.
	 * 
	 * @param qtId
	 *            the qt id
	 * @param approvalType
	 *            the approval type
	 * @param docType
	 *            the doc type
	 * @return the ScQtApproval
	 */
	private List<ScQtApprovalRequest> getApprovalRequests(Long qtId,
			String approvalCategory, String docType, List<String> reqTypes, String approverAction) {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		String taskName = this.getManagedBean().getTaskName();

		tenderReqDTO.setQtId(qtId);
		tenderReqDTO.setDocType(docType);
		tenderReqDTO.setApprovalReqTypes(reqTypes);
		tenderReqDTO.setApprovalCategory(approvalCategory);
		tenderReqDTO.setApproverAction(approverAction);
		tenderReqDTO.setSuppAcceptedQtChange(
				SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(taskName));
		tenderReqDTO.setShowAmendmentData(
				SCProcessNameConst.PUBLICATION_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName));
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO.retrieveApprovalsByQtIdNType(tenderReqDTO);

		this.getManagedBean().setQt(resDTO.getQtDetail());
		this.getManagedBean().setScQtApproval(resDTO.getQtApproval());
		this.getManagedBean().setAmendMentBean(resDTO.getAmendMentBean());
		
		return resDTO.getApprovalRequests();
	}
	
	//#21692 - start
	public boolean isEPVApprovalRequestAccepted(Long qtId) {

		boolean epv = false;	
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		
		tenderReqDTO.setQtId(qtId);
		tenderReqDTO.setApprovalCategory(ParameterConst.FINALIZATION_COMMITTEE);
		tenderReqDTO.setApprovalTypeValue(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD);
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO.retrieveApprovalReqByQtIdAndReqCategory(tenderReqDTO);
		
		List<ScQtApprovalRequest> requestList = resDTO.getApprovalRequests();
		
		if (null != requestList && CollectionUtils.isNotEmpty(requestList)) {
			for (ScQtApprovalRequest req : requestList) {
				if (QTConstants.PORTLET_NAME_QT_MAINTENANCE
						.equalsIgnoreCase(EpPortletUtil.getDisplayPortletId())) {
					if ((null != req.getApproverAction() && req
							.getApproverAction()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_DECISION_TYPE_REJECTED))) {
						epv = true;
					}
				} else {
					if ((null != req.getApproverAction() && req
							.getApproverAction()
							.getParameterCode()
							.equals(ParameterConst.APPROVAL_DECISION_TYPE_APPROVED))) {
						epv = true;
					}
				}
			}
		}
		return epv;
		
	}
	/**
	 * Get latest status of Extension of Proposal Validity (EPV) return true if Approved
	 * 
	 * @param qtId
	 * 
	 * @return the boolean
	 * 
	 */
	public boolean isEPVLatestStatusApprove(Long qtId) {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		
		tenderReqDTO.setQtId(qtId);
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO.getLatestEPVWorkflowStatus(tenderReqDTO);
		
		ScWorkFlowStatus workflowStatus = resDTO.getWorkflowStatus();
		if (null != workflowStatus) {
			return workflowStatus.getPmStatus().getStatusId().equals(QTStatusConst.EPV_APPROVED) ||
					workflowStatus.getPmStatus().getStatusId().equals(QTStatusConst.EPV_PENDING_SUPPLIER_ACCEPTANCE) ||
					workflowStatus.getPmStatus().getStatusId().equals(QTStatusConst.EPV_ACCEPTED);
		} else {
			return false;
		}
	}
	
	/**
	 * Check if EPV workflow null or not, if null return true
	 * 
	 * @param qtId
	 * @return
	 */
	public boolean isEPVWorkflowStatusNull(Long qtId) {

		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		
		tenderReqDTO.setQtId(qtId);
		
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO.getLatestEPVWorkflowStatus(tenderReqDTO);
		
		ScWorkFlowStatus workflowStatus = resDTO.getWorkflowStatus();
		return null == workflowStatus;
	
	}
	//#21692 - end

	/**
	 * View approval action.
	 * 
	 * @return the string
	 */
	public String viewApprovalAction() {
		return "/xhtml/sc/quatationtender/Approver/approverMain.xhtml?faces-redirect=true";
	}

	/**
	 * Constuct out come.
	 * 
	 * @param approvalRequests
	 *            the approval requests
	 * @param scQt
	 *            the sc qt
	 * @return the string
	 */
	private String constuctOutCome(List<ScQtApprovalRequest> approvalRequests,
			ScQt scQt) {
		if (null != approvalRequests) {
			boolean isRevise = false;
			boolean isRejected = false;
			for (int i = 0; i < approvalRequests.size(); i++) {
				ScQtApprovalRequest approvalRequest = approvalRequests.get(i);
				String approvalType = approvalRequest.getApprovalType()
						.getParameterCode();
				if (ParameterConst.APPROVAL_REQUEST_RESTRICTED_TENDER
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.RESTR_APPROVAL_DECISION_TYPE_PROCEED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						revertQTTypeAsOpen(scQt);
						
					} else if (ParameterConst.RESTR_APPROVAL_DECISION_TYPE_REVISE
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						//break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PROPOSAL_VALIDITY_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRejected = true;
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						//break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRejected = true;
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						//break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_OPEN_TO_BUMI
						.equals(approvalType)
						|| ParameterConst.APPROVAL_REQUEST_BUMI_TO_OPEN
								.equals(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRejected = true;
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						//break;
					}
				}
			}
			
			if (isRevise) {
				return BpmOutcomeConst.BPM_REVISE;
			} else if (isRejected) {
				return BpmOutcomeConst.BPM_REJECT;
			}
		}
		return BpmOutcomeConst.BPM_APPROVE;
	}

	/**
	 * Update pm parameter.
	 * 
	 * @param requestdetails
	 *            the requestdetails
	 */
	public void updatePmParameter(ScQtApprovalRequest requestdetails) {
		if (null != requestdetails) {
			IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IParameterSO.class);
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setLanguageCode("en");
			parameterReqDTO.setParameterId(requestdetails.getApproverAction()
					.getParameterId());
			requestdetails.setApproverAction(((ParameterResDTO) parameterSO
					.retrieveParameterById(parameterReqDTO)).getPmParameter());
		}
	}

	public void updateDecisionType(ScQtApprovalRequest requestdetails) {
		if (null != requestdetails) {
			IParameterSO parameterSO = getParameterSOGlobal();
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setLanguageCode("en");
			if (requestdetails.getDecisionType().getParameterId() != null) {
				parameterReqDTO.setParameterId(requestdetails.getDecisionType().getParameterId());
				requestdetails.setDecisionType((parameterSO.retrieveParameterById(parameterReqDTO)).getPmParameter());
				//#21692 note 524 - Start
				this.getManagedBean().setEpvAttachmentValue(!getManagedBean().getQt().isApprovalPBM() ?
						ParameterConst.EPV_TYPE_REQUEST_1 : ParameterConst.EPV_TYPE_REQUEST_3);
				deleteEpvUploadFile();
				//#21692 note 524 - End
			}
			//#21692 note-418 issue 3 - Start
			else {
				requestdetails.setDecisionType(new PmParameter());
				//#21692 note 524 - Start
				this.getManagedBean().setEpvAttachmentValue(!getManagedBean().getQt().isApprovalPBM() ?
						ParameterConst.EPV_TYPE_REQUEST_1 : ParameterConst.EPV_TYPE_REQUEST_3);
				deleteEpvUploadFile();
				//#21692 note 524 - End
			}
			//#21692 note-418 issue 3 - End
		}
	}

	/**
	 * Update ScQtApprovalRequest DecisionCert's data
	 * 
	 * @param approvalRequestdetails
	 */
	public void updateDecisionCertPBM(ScQtApprovalRequest approvalRequestdetails) {
		ScQtApprovalRequest requestdetails = approvalRequestdetails;
		if (null != requestdetails) {
			IParameterSO parameterSO = getParameterSOGlobal();
			ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
			parameterReqDTO.setLanguageCode("en");
			if (requestdetails.getDecisionCertPBM().getParameterId() != null) {
				parameterReqDTO.setParameterId(requestdetails.getDecisionCertPBM().getParameterId());
				requestdetails
						.setDecisionCertPBM((parameterSO.retrieveParameterById(parameterReqDTO)).getPmParameter());
				requestdetails
						.setDecisionType(new PmParameter());
				//#21692 note 524 - Start
				this.getManagedBean().setEpvAttachmentValue(ParameterConst.EPV_TYPE_REQUEST_2);
				deleteEpvUploadFile();
				//#21692 note 524 - End
				if (requestdetails.getEpvApprovalReqAttachmentR3() != null) {
					this.getManagedBean().setEpvAttachmentValue(ParameterConst.EPV_TYPE_REQUEST_3);
					deleteEpvUploadFile();
					requestdetails = this.getManagedBean().getScQtApprovalRequestList().get(0);
				}
			} else {
				requestdetails
						.setDecisionCertPBM(new PmParameter());
				this.getManagedBean().setChmnFinalizationRejectEPV(Boolean.FALSE);//#21692 show MOF Date if Chairman hasnt decide EPV (after Proposal Validity Ended)
				//#21692 note 524 - Start
				this.getManagedBean().setEpvAttachmentValue(ParameterConst.EPV_TYPE_REQUEST_2);
				deleteEpvUploadFile();
				//#21692 note 524 - End
			}
			//#21692 - start
			if (requestdetails.getDecisionCertPBM().getParameterCode() != null) {
				if (requestdetails.getDecisionCertPBM().getParameterCode().equals(ParameterConst.TR2_NOTACKNOWLEDGE)) {
					parameterReqDTO.setParameterId(ParameterConst.TR3_REJECT);
					requestdetails
							.setDecisionType((parameterSO.retrieveParameterById(parameterReqDTO)).getPmParameter());
					this.getManagedBean().setChmnFinalizationRejectEPV(Boolean.TRUE);//#21692 hide MOF Date if Chairman Reject EPV (after Proposal Validity Ended)
					this.getManagedBean().getQt().setMofDate(null);//Null date after hide
				} else if (requestdetails.getDecisionCertPBM().getParameterCode().equals(ParameterConst.TR2_REVISE)) {
					parameterReqDTO.setParameterId(QTStatusConst.PCB_RQ3_REVISED);
					requestdetails
							.setDecisionType((parameterSO.retrieveParameterById(parameterReqDTO)).getPmParameter());
					this.getManagedBean().setChmnFinalizationRejectEPV(Boolean.TRUE);//#21692 hide MOF Date if Chairman Reject EPV (after Proposal Validity Ended)
					this.getManagedBean().getQt().setMofDate(null);//Null date after hide
				} else {
					this.getManagedBean().setChmnFinalizationRejectEPV(Boolean.FALSE);//#21692 show MOF Date if Chairman Accept EPV (after Proposal Validity Ended)
				}
			}
			//#21692 - end
		}
	}
	/**
	 * Gets the Status Details.
	 * 
	 * @return List<PmParameterDesc>
	 */
	public List<PmParameterDesc> getStatusDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = new SearchBean<PmParameterDesc>();
		List<String> recommendList = new ArrayList<String>();
		recommendList.add(ParameterConst.APPROVAL_DECISION_TYPE_REVISED);
		searchBean.getDataFilterBeans().add(
				new SearchFilterBean("pmParameter.parameterCode",
						SearchOperator.OPERATOR_NOT_IN.value(), recommendList));
		return retrieveParametersBySearchBean(
				ParameterTypeConst.APPROVAL_DECISION_TYPE, searchBean);
	}

	/**
	 * Gets the text before sign.
	 * 
	 * @return the text before sign
	 */
	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	/**
	 * Sets the text before sign.
	 * 
	 * @param textBeforeSign
	 *            the new text before sign
	 */
	public void setTextBeforeSign(String textBeforeSign) {
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}

	/**
	 * Gets the pm digi sign.
	 * 
	 * @return the pm digi sign
	 */
	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	/**
	 * Sets the pm digi sign.
	 * 
	 * @param pmDigiSign
	 *            the new pm digi sign
	 */
	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}
	
	private List<String> approveApprovalRequests(ScQt scQt, List<ScQtApprovalRequest> approvalRequests, boolean isFinalApproval) {
		List<String> approvalTypes = new ArrayList<String>();
		
		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			for (int i = 0; i < approvalRequests.size(); i++) {
				ScQtApprovalRequest approvalRequest = approvalRequests.get(i);
				String approvalType = approvalRequest.getApprovalType()
						.getParameterCode();
				
				if (ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE
						.equalsIgnoreCase(approvalType)) {
					
					scQt.setChanged(true);
					scQt.setPublishPeriod(Integer.parseInt(approvalRequest
							.getChangedValue()));
					updatePublishClosingDate(scQt);
					
				} else if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL
						.equalsIgnoreCase(approvalType)) {
					QTDetailBackingBean backingBean = new QTDetailBackingBean();
					backingBean.retrieveDocumentTabDtl();

					try {
						new QTDocumentsBackingBean()
								.createDocumentAttachments();
					} catch (Exception e) {
						e.printStackTrace();
						if(e.getMessage() != null)	{
							throw new EpException(e.getMessage());
						} else {
							throw new EpException(MessageConst.ERROR_000001);
						}
						
					}
					scQt=checkProcuremntCategoryMedicine(scQt);
					scQt.setScQtDocReports(this.getManagedBean()
							.getScQtDocReportList());
					updatePublishClosingDate(scQt); //Bug_37764
					
				} else if (ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD.equalsIgnoreCase(approvalType)) {
					if (isFinalApproval) {
						scQt.setChanged(true);
						scQt.setQtValidityPeriod(Integer
								.parseInt(approvalRequest
										.getChangedValue()));
						updateProposalValidityDate(scQt);
					}
				} else if (ParameterConst.APPROVAL_REQUEST_UPDATE_BSV_DETAILS.equalsIgnoreCase(approvalType)) {
					scQt.setChanged(true);
				}

				approvalTypes.add(approvalType);
			}
		}
		
		return approvalTypes;
	}

	private ScQt checkProcuremntCategoryMedicine(ScQt scQt) {

		IQTSpecSO qtSpecSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				IQTSpecSO.class);
		SpecificationReqDTO specReqDTO = new SpecificationReqDTO();
		specReqDTO.setScQt(scQt);
		SpecificationResDTO specResDTO = qtSpecSO.retriveSpecItemCount(specReqDTO);
		log.info("QTID:" + scQt.getQtId() + "itemCount" + specResDTO.getItemCount());
		boolean isMedicine = isMultipleProposalCategory(scQt, null);
		log.info("QTID:" + scQt.getQtId() + "isMedicine" + isMedicine);
		if (isMedicine || specResDTO.getItemCount() <= 1) {
			scQt.setEvaluateBy(SourcingConstants.EVALUATE_BY__ITEM);
		} else {
			scQt.setEvaluateBy(SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM);
		}
		return scQt;
	}

	private void revertToOldValues(ScQt scQt, List<ScQtApprovalRequest> approvalRequests) {
		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			for (int i = 0; i < approvalRequests.size(); i++) {
				ScQtApprovalRequest approvalRequest = approvalRequests.get(i);
				String approvalType = approvalRequest.getApprovalType()
						.getParameterCode();
				if (ParameterConst.APPROVAL_REQUEST_RESTRICTED_TENDER
						.equalsIgnoreCase(approvalType)) {
					if (approvalRequest.getApproverAction() == null) {
						revertQTTypeAsOpen(scQt);
					}

				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PROPOSAL_VALIDITY_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (approvalRequest.getApproverAction() != null
							&& ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
									.equalsIgnoreCase(approvalRequest
											.getApproverAction()
											.getParameterCode())) {
						scQt.setQtValidityPeriod(Integer
								.parseInt(approvalRequest.getOriginalValue()));
						updateProposalValidityDate(scQt);
					}

				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD.equalsIgnoreCase(approvalType) ||
						ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE.equalsIgnoreCase(approvalType)) {
					if ((approvalRequest.getApproverAction() != null
							&& ParameterConst.APPROVAL_DECISION_TYPE_REJECTED.equalsIgnoreCase(approvalRequest.getApproverAction().getParameterCode())) ||
							ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE.equalsIgnoreCase(approvalType)) {
						scQt.setPublishPeriod(Integer.parseInt(approvalRequest
								.getOriginalValue()));
						updatePublishClosingDate(scQt);
					}

				} else if (ParameterConst.APPROVAL_REQUEST_OPEN_TO_BUMI
						.equals(approvalType)
						|| ParameterConst.APPROVAL_REQUEST_BUMI_TO_OPEN
								.equals(approvalType)) {
					if (approvalRequest.getApproverAction() != null
							&& ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
									.equalsIgnoreCase(approvalRequest
											.getApproverAction()
											.getParameterCode())) {
						scQt.setOpenFor(approvalRequest
								.getOriginalValue());
					}

				} else if (ParameterConst.APPROVAL_REQUEST_UPDATE_BSV_DETAILS.equals(approvalType)) {
					// Proposal Start Date
					QtAmendMentValueBean valueBean = getManagedBean().getAmendMentBean().getValueMap().get(QTFieldConst.FIELD_QT_PROPOSAL_START_DATE);
					if (valueBean != null && valueBean.getOldValue() != null && valueBean.getOldValue() instanceof Date) {
						scQt.setProposalStartDate((Date)valueBean.getOldValue());
						updatePublishClosingDate(scQt);
					}
				}
			}
		}

	}
	
	private void revertQTTypeAsOpen(ScQt scQt) {
		scQt.setQtType(ParameterConst.QUOTATION_TENDER_TYPE_OPEN);
		this.getManagedBean().setToBeRemoveRestrictedSupplierList(
				scQt.getScQtSuppliers());
		scQt.setScQtSuppliers(null);
	}

	/**
	 * Method to supplier acceptance..
	 * 
	 * @param outCome
	 */
	public void  supplierAcceptance(String outCome) {
		this.getManagedBean().setAction(outCome);
		if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outCome)){
		getManagedBean().getSpkiBackingBean().getChallengeQuestions();
		this.getManagedBean().setAutoUpdate(Boolean.FALSE);
		RequestContext.getCurrentInstance().execute("spki_dlg.show();");
		RequestContext.getCurrentInstance().execute("blockWidget.hide();");
		}else{
			updateBpmSupplierAcceptanceTaskAction(outCome, null);
		}


	}

	/**
	 * Method to Revise or Reject..
	 * 
	 * @return boolean
	 */
	public boolean isReviceOrReject() {
		String[] outComes = this.getManagedBean().getOutComes();
		if (null != outComes && outComes.length > 0) {
			for (String outCome : outComes) {
				if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outCome)) {
					return true;
				} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outCome)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * The method to used auto update of request.
	 */
	public void enableAutoUpdate() {
		getManagedBean().setAutoUpdate(Boolean.TRUE);		
	}
	
	
	/**
	 * Spki sign action.
	 * 
	 * @param digiSign
	 *            the digi sign
	 */
	public void spkiSignAction(PmDigiSign digiSign) {
		updateBpmSupplierAcceptanceTaskAction(this.getManagedBean().getAction(), digiSign);
	}
	
	/**
	 * Update bpm task. update bpm - save data into database and BPM process
	 * will route to the next respective user.
	 * 
	 * @param outcome
	 *            the outcome
	 * @param digiSign
	 *            the digi sign
	 * 
	 */
	private void updateBpmSupplierAcceptanceTaskAction(String outcome,PmDigiSign digiSign) {
		FacesScopes facesScopes = new FacesScopes();

		try {
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtStatus(null);
			qt.setScQtBriefings(null);

			QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
			SCQuotationTenderDataType dataType = (SCQuotationTenderDataType)this.getManagedBean().getPayLoad();
			
			qtReqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
			qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
			qtReqDTO.setDocType(ParameterConst.DOCTYPE_QUOTATION);
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

			ScQtApproval approval = this.getManagedBean().getScQtApproval();

			List<ScQtApprovalRequest> approvalRequests = this.getManagedBean()
					.getScQtApprovalRequestList();

			
			IQTProposalSO proposalSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTProposalSO.class);
			ProposalReqDTO proposalReqDTO= new ProposalReqDTO();
			proposalReqDTO.setQtId(qt.getQtId());
			boolean isAllAccepted = false;
			int totalAccepted = proposalSO.getTotalQtValidityAcceptedSuppliers(proposalReqDTO);
			dataType.getSupplierList();
			int totalSuppliers = dataType.getSupplierList().size();
			// Generate document if last supplier accept.
			if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outcome)){
				// P1 - update proposal validity period if all suppliers approved.
				// P2 - update proposal validity period if one of suppliers approved.
				
				isAllAccepted = totalSuppliers > 0 && totalSuppliers == totalAccepted+1;
				if (isAllAccepted) {
					approveApprovalRequests(qt, approvalRequests, true);
				}
				if (null != digiSign) {
					ProposalReqDTO proposalReqDto=new ProposalReqDTO();
					proposalReqDto.setQtId(qt.getQtId());
					proposalReqDto.setUserId(new FacesScopes()
					.getUserSessionBean().getUserId());
					proposalReqDto.setProposalValidityExtension(Boolean.TRUE);
					ProposalResDTO resDTO=proposalSO.retriveProposalBySupplierUserId(proposalReqDto);
					if(null!=resDTO.getSmSupplier()){
					String originalText = prepareOriginalText(resDTO.getSmSupplier());
					byte[] signedData = getManagedBean().getSpkiBackingBean().spkiSignTransaction(originalText);
					updateSpkiDigiSignData(digiSign,
							ParameterConst.DOCTYPE_QUOTATION, qt.getQtId(),
							qt.getQtNo(),originalText);
					digiSign.setSpkiSignedData(signedData);
				}
				}
			}
			
			if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)){
				//#21692 remark checking - Start
				if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(getManagedBean().getTaskName())) {
					if(null != this.getManagedBean().getSuppResp() &&
							(this.getManagedBean().getSuppResp().getRemark() == null || this.getManagedBean().getSuppResp().getRemark().equals(""))) {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
										getMessage(SourcingConstants.EXTENSION_PROPOSAL_VALIDITY_REMARK,null)));//#21692 change to constant
						return;
					}
				} else {
					List<ScQtApprovalRemark> remarks = approval.getScQtApprovalRemarks();
					if (null != remarks) {
						for (int i = 0; i < remarks.size(); i++) {
							String userRemark = remarks.get(i).getRemark();
							
							if (null == userRemark || "".equals(userRemark)) {
								
								FacesContext.getCurrentInstance().addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
												getMessage(SourcingConstants.EXTENSION_PROPOSAL_VALIDITY_REMARK,null)));//#21692 change to constant
								return;
							}
						}
					}
				}
				//#21692 remark checking - End
			}

			finalizeRemarks(approval.getScQtApprovalRemarks());
			approval.getScQtApprovalDtls().addAll(approvalRequests);
			List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
			qtApproval.add(approval);
			qt.setScQtApprovals(qtApproval);

			qt.setScQtCommittees(null);
			
			qtReqDTO.setSuppResp(this.getManagedBean().getSuppResp());//#21962 new remark for supplier
			qtReqDTO.setDigiSignData(digiSign);
			qtReqDTO.setPayLoad(dataType);
			FacesScopes facesScope = new FacesScopes();
			qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
			qtReqDTO.setQtdetail(qt);
			qtReqDTO.setPmUser(facesScope.getUserSessionBean());
			qtReqDTO.setRoleCode(getUserRoleCode());
			qtReqDTO.setOutcome(outcome);

			QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO.updateSupplierAction(qtReqDTO);

			qt = tenderResDTO.getQtDetail();
			if (tenderResDTO.isSuccessful()) {
				if (isAllAccepted) {
					new QTDocumentsBackingBean().reGenerateDocuments(
							qt.getQtId(), 
							QTFieldConst.FIELD_QT_VALIDITY_PERIOD);
				}
				successMessage(getMessgeConstant(outcome), ParameterTypeConst.DOCUMENT_TYPES,
						ParameterConst.DOCTYPE_QUOTATION, qt.getQtNo(), Boolean.TRUE);
				// clear data
				super.renewSesionObject();
			}
		} finally {
		}
		
	}

	/**
	 * Prepare original text.
	 * 
	 * @param smSupplier
	 *            the sm supplier
	 * @return the string
	 */
	private String prepareOriginalText(SmSupplier smSupplier) {
		FacesScopes facesScopes = new FacesScopes();
		StringBuilder sb = new StringBuilder();
		String countryCode = "";
		String orginalText = "";

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
		sb.append(getManagedBean().getQt().getQtNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo()).append("|")
				.append(smSupplier.getRegNo()).append("|").append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}
	
	
	private boolean CommitteeTypeIsEcOcTecFec(ScCommittee cte){
		if(cte.getCommitteeType()!=null && cte.getCommitteeType().getParameterCode()!=null){
			return 
				"EC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode()) ||
				"OC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode()) ||
				"TEC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode()) ||
				"FEC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode());
		}
		return false;
	}
	
	/**
	 * only if managedBean.readOnly and managedBean.approvalEditableSection()
	 * @return
	 */
	public boolean isInApprovalEditableSection(){
		if(this.getManagedBean().isReadOnly()){
			if(this.getManagedBean().isApprovalEditableSection()){
				return true;
			}
		}
		return false;
	}
	
	//#25692-start
	public void retriveCommiteeApprovalTabDetails(){
		log.debug("===startApprovalbackingBeanretriveCommiteeApprovalTabDetails=====");
		log.debug("===startApprovalbackingBeanretriveCommiteeApprovalTabDetailsparamtypes====="+this.getManagedBean().getParamTypes());
		List<Long> cmtStatus = new ArrayList<Long>(3);
		cmtStatus.add(QTStatusConst.CMT_PENDING_SUBMISSION);
		cmtStatus.add(QTStatusConst.CMT_COMPLETED);
		cmtStatus.add(QTStatusConst.CMT_PENDING_REVISION);
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> commSearchBean = new SearchBean<ScQtCommittee>();
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scQt.qtId",
						SearchOperator.OPERATOR_EQUALS.value(), this.getManagedBean().getQt().getQtId()));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scCommittee.approvalReq",
						SearchOperator.OPERATOR_EQUALS.value(), true));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean(
						"scCommittee.committeeType.parameterCode",
						SearchOperator.OPERATOR_IN.value(), this.getManagedBean().getParamTypes()));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scCommittee.committeStatus.statusId",
						SearchOperator.OPERATOR_NOT_IN.value(), cmtStatus));

		committeReqDTO.setCommSearchBean(commSearchBean);
		CommitteResDTO resDTO = committeSO
				.retriveQtCommitteDtls(committeReqDTO);
		ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO
				.getQtCommitteResultBean();
		this.getManagedBean().setQtCommittes(qtcomSearchBean);
		List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean
				.getResultList(),this.getManagedBean().getQt().getQtId());
		// initializeRemark(qtcomSearchBean.getResultList());
		this.getManagedBean().getQt()
				.setScQtCommittees(qtcomSearchBean.getResultList());
		this.getManagedBean().setScCommitteeMemberTabList(tabBeans);
		
		if (tabBeans.size() > 0) {
			getManagedBean().setSelectedTabBean(tabBeans.get(0));
		}
		this.getManagedBean().setPublicationApproval(null);
	}
	//#25692-End
	
	// 25037 - start
	private void prepareEvalCommiteeApproval(List<ScQtApprovalRequest> approvalRequests) {
		IScWorkFlowStatusSO workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		String committeeType = "";
		if (CollectionUtils.isNotEmpty(approvalRequests)) {
			committeeType = approvalRequests.get(0).getApprovalType().getParameterCode();
		}
		reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
		reqDTO.setDocType(committeeType);

		ScWorkFlowStatus wfStatus = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
		if (null == wfStatus || null == wfStatus.getPmStatus()) {
			return; 
		}
		if (Arrays.asList(QTStatusConst.QT_EVAL_PREPARATION_GROUP).contains(wfStatus.getPmStatus().getStatusId())
				|| Arrays.asList(QTStatusConst.QT_EVAL_FINALIZATION_GROUP).contains(
						wfStatus.getPmStatus().getStatusId())) {
			if (CollectionUtils.isNotEmpty(approvalRequests)) {
				this.getManagedBean().setScQtApprovalRequest(approvalRequests.get(0));
				// 25037 - start - note 568
				if (this.getManagedBean().getScQtApproval() != null) {
					this.getManagedBean().getScQtApproval().setRequestDate(approvalRequests.get(0).getCreatedDate());
				}
				// 25037 - end - note 568
			}
			this.getManagedBean().setCommitteeReviseFlow(true);
		}
	}
	
	/**
	 * To check if specification is pending specification update
	 * 
	 * @return boolean flag to indicate status is in progress
	 */
	public boolean assertSpecificationUpdateInProgress() {
		String taskName = this.getManagedBean().getTaskName();
		taskName = StringUtils.isNotEmpty(taskName) ? taskName : "";
		String[] taskNames = new String[] { SCProcessNameConst.PREPARE_QT_SCHEDULE,
				SCProcessNameConst.APPROVE_PUBLICATION };
		String[] specTaskName = new String[] { SCProcessNameConst.SPEC_MAINTENANCE_ENDORSE_SCORING,
				SCProcessNameConst.SPEC_MAINTENANCE_SIGNOFF_SCORING, SCProcessNameConst.SPEC_MAINTENANCE_REVISE_SCORING };// 15391

		if (!Arrays.asList(taskNames).contains(taskName)){
			return false;
		}
		
		boolean inProgress = false;
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		ScQt qt = this.getManagedBean().getQt();
		// 25037 - start - note 568
		ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_SPECIFICATION_ENDORSMENT,
				qt.getQtId());
		Long statusId = (wfStatus != null && wfStatus.getPmStatus() != null) ? wfStatus.getPmStatus().getStatusId()
				: 0L;
		if (statusId.equals(QTStatusConst.SE_PENDING_SUBMISSION)) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, getMessage("lbl.sc.popup.checkBeforePublish2",
							null)));
			this.getManagedBean().setSpecUpdateInProgress(true);
			inProgress = true;
		} else {
			bpmReqDTO.setDocNumber(qt.getQtNo());
			bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
			BpmResponseDTO resDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
			for (WorklistItemVO currentVO : resDTO.getWorklistVO().getWorklistItem()) {
				if (StringUtils.isNotBlank(currentVO.getTaskName())
						&& Arrays.asList(specTaskName).contains(currentVO.getTaskName())) {
					this.getManagedBean().setSpecUpdateInProgress(true);
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, null, getMessage(
									"lbl.sc.popup.checkBeforePublish2", null)));

					inProgress = true;
					break;
				}
			}
		}
		// 25037 - end - note 568
		return inProgress;
	}
	
	private void assertBriefingSiteVisitIsComplete(Long qtId) {
		ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, qtId);
		wfStatus = wfStatus == null ? new ScWorkFlowStatus() : wfStatus;
		if (wfStatus.getPmStatus() != null) {
			Long currentStatusId = wfStatus.getPmStatus().getStatusId();
			if (this.isStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_PROPOSAL_PREPARATION,
					QTStatusConst.QT_ORDERED_STATUSES)) {
				this.getManagedBean().setOutComes(new String[] { BpmOutcomeConst.BPM_CANCEL }); // 25037 - note 510
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								getMessage("lbl.sc.doaRequestExpired", null)));
			}
		}
	}
	
	/**
	 * The pre-render method for the portlet maintenanceRequestApproval
	 * prerenderMaintenanceRequestApproval.xhtml
	 */
	public void preRenderGenericApproval(){
		SCQuotationTenderDataType obj = getBpmTaskDetails(SCQuotationTenderDataType.class, null);
		Long requestId = obj.getRequestId();
		Long qtId = obj.getQtId();
		String requestType = this.getManagedBean().getCurrentTaskDocType();
		String page = "/xhtml/sc/quatationtender/maintenanceRequest/approval/maintenanceRequestApproval.xhtml";

		try {
			if (ParameterConst.DOCTYPE_DOA_ASSIGNMENT.equals(requestType)) {
				prepareDeskOfficerAssistantApproval(qtId, requestId);
			}

			FacesContext context = FacesContext.getCurrentInstance();
			PortletRequest request = (PortletRequest) context.getExternalContext().getRequest();
			RenderResponse response = (RenderResponse) request.getAttribute("javax.portlet.response");
			response.setTitle(ResourceBundleUtils.getMessage(
					"lbl.sc.portlet.title.".concat(requestType).concat(
							this.getManagedBean().getTaskName().split(" ")[0]),
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, (new FacesScopes()).getUserLocale()));
			context.getApplication().getNavigationHandler().handleNavigation(context, null, page);
		} catch (Exception e) {
			ScQt scQt = this.getManagedBean().getQt();
			String qtNo = scQt == null ? "" : scQt.getQtNo();
			SLAUtils.getInstance().logSLAError(
					ModuleCodeConst.SUPPLIER_SELECTION,
					ParameterConst.DOCTYPE_QUOTATION,
					qtNo,
					SLALogConstant.QT_APPROVAL_BACKINGBEAN.concat(SLALogConstant.QT_SLA_DOT_SEPARATOR).concat(
							SLALogConstant.QT_PRERENDER_GENERIC_APPROVAL), getUserIdSessionFromLoginId(), e);
		}

	}
	
	private void prepareDeskOfficerAssistantApproval(Long qtId, Long requestId) {
		assertBriefingSiteVisitIsComplete(qtId);
		String requestType = this.getManagedBean().getCurrentTaskDocType();
		getApprovalRequests(qtId, ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APPROVAL, "",
				Arrays.asList(new String[] { requestType }), "");
		QTScheduleDetailBackingBean scheduleBB = new QTScheduleDetailBackingBean();
		scheduleBB.getDeskOfficerAdminAssistant();
		ScQt scQt = this.getManagedBean().getQt();

		ScQtRequestContent requestContent = getRequestContentByRequestId(scQt, requestId,
				ParameterConst.DOCTYPE_DOA_ASSIGNMENT);

		this.getManagedBean().setRequestContent(requestContent);
		ScQtApprovalRequest request = requestContent.getApprovalRequest();

		this.getManagedBean().setDoaaRequest(request);
		@SuppressWarnings("unchecked")
		Map<Long, String> doaMap = (Map<Long, String>) requestContent.getRequestContent();
		List<ScQtContactPerson> currentDoaList = scQt.getScQtContactPersons();
		this.getManagedBean().setPrevDoaList(new ArrayList<ScQtContactPerson>());
		this.getManagedBean().setScQtContactPersons(new ArrayList<ScQtContactPerson>());
		for (ScQtContactPerson p : currentDoaList) {
			if (p.getOfficerType().equals(SourcingConstants.OFFICER_TYPE_DOAA)) {
				this.getManagedBean().getPrevDoaList().add(p);
			}
		}
		this.getManagedBean().setPmuserContactpersonList(new ArrayList<ScQtContactPerson>());
		for (Map.Entry<Long, String> d : doaMap.entrySet()) {
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setUserId(d.getKey());
			UserResDTO userResDTO = this.getUserSOGlobal().retrieveUserByUserId(userReqDTO);
			PmUser user = userResDTO.getPmUser();

			if (user != null && user.getLoginId().equalsIgnoreCase(d.getValue())) {
				ScQtContactPerson doa = new ScQtContactPerson();
				doa.setBrandNew(false);
				doa.setScQt(scQt);
				doa.setOfficerType(SourcingConstants.OFFICER_TYPE_DOAA);
				doa.setName(user.getUserName());
				doa.setUser(user);
				doa.setPhoneNo(user.getPhoneNo());
				doa.setFaxNo(user.getFaxNo());
				doa.setEmail(user.getEmail());
				doa.setUserId(user.getUserId());
				this.getManagedBean().getPmuserContactpersonList().add(doa);
			}
		}
		ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
		reqDTO.setDocType(ParameterConst.DOCTYPE_DOA_ASSIGNMENT);
		reqDTO.setDocId(this.getManagedBean().getQt().getQtId());
		IScWorkFlowStatusSO wfStatusSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
		ScWorkFlowStatus wfStatus = wfStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();

		if (request.getAppRequestRemarks() == null) {
			request.setAppRequestRemarks(new ArrayList<ScQtAppRequestRemark>());
		}
		this.initializeRemarks(request, wfStatus, request.getAppRequestRemarks(), ScQtAppRequestRemark.class);
	}
	
	private ScQtRequestContent getRequestContentByRequestId(ScQt scQt, Long requestId, String docType) {
		if (CollectionUtils.isNotEmpty(scQt.getRequestContents())) {
			for (ScQtRequestContent content : scQt.getRequestContents()) {
				if (null != content.getApprovalRequest()
						&& content.getRequestType().getParameterCode().equalsIgnoreCase(docType)
						&& content.getApprovalRequest().getQtApprovalRequestId().equals(requestId)) {
					return content;
				}
			}
		}
		throw new EpException("Request does not exist.");
	}
	
	/**
	 * Method called before submission, determine if gpki approval was required.
	 * 
	 * @param outcome
	 */
	public void preSubmitGenericApprovalTask(String outcome) {
		String taskName = this.getManagedBean().getTaskName();
		taskName = taskName == null ? "" : taskName;
		this.getManagedBean().setAction(outcome);
		if (taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_APPROVE)
				|| taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_REVISE)) {
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_GPKI_DLG_SHOW);
			} else if (outcome.equals(BpmOutcomeConst.BPM_SUBMIT)
					&& taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_REVISE)) { // #30477 - Start
				if (isInactivePublicationApproverRole()) {
					throw new EpException(MessageConst.ERROR_775173);
				} else {
					updateGenericApprovalTask(null);
				}
				// #30477 - End
			} else {
				updateGenericApprovalTask(null);
			}
		}
	}	
	
	/**
	 * Persist updated approval data
	 * 
	 * @param digiSign
	 */
	public void updateGenericApprovalTask(PmDigiSign digiSign) {

		FacesScopes facesScopes = new FacesScopes();
		SCQuotationTenderDataType payload = (SCQuotationTenderDataType) this.getManagedBean().getPayLoad();
		ScQt scQt = this.getManagedBean().getQt();
		String qtNo = scQt.getQtNo();
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		if (CollectionUtils.isEmpty(this.getManagedBean().getPmuserContactpersonList())) {
			this.getManagedBean().setPmuserContactpersonList(new ArrayList<ScQtContactPerson>());
		}

		Map<Long, String> map = new HashMap<>();
		for (ScQtContactPerson p : this.getManagedBean().getPmuserContactpersonList()) {
			map.put(p.getUser().getUserId(), p.getUser().getLoginId());
		}
		this.getManagedBean().getRequestContent().setRequestContent(map);

		reqDTO.setApproverAction(this.getManagedBean().getAction());
		reqDTO.setPayLoad(payload);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		reqDTO.setToken(facesScopes.getBPMAuthenticationCtxToken());
		reqDTO.setPmUser(facesScopes.getUserSessionBean());
		reqDTO.setDocId(payload.getRequestId());
		reqDTO.setDocType(this.getManagedBean().getCurrentTaskDocType());
		reqDTO.setQtdetail(scQt);
		reqDTO.setTaskName(this.getManagedBean().getTaskName());
		reqDTO.setRequestRemark(this.getManagedBean().getDoaaRequest().getAppRequestRemarks().get(0));
		//#28895-35500 start
		reqDTO.setDeletedDoaaList(this.getManagedBean().getPmUserDeletedContactPersonList());
		reqDTO.setDoaaList(this.getManagedBean().getNewDoaList());
		//#28895-35500 end
		ScQtAppRequestRemark remark = reqDTO.getRequestRemark();
		String remarkStr = remark == null ? "" : remark.getRemark();

		if (this.getManagedBean().getAction().equals(BpmOutcomeConst.BPM_REVISE) && StringUtils.isEmpty(remarkStr)) {
			throw new EpException(MessageConst.ERROR_100022, new String[] { ResourceBundleUtils.getMessage(
					"lbl.sc.remark", ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()) });
		}
		if (digiSign != null) {
			updatePmDigiSignData(digiSign, ParameterConst.DOCTYPE_DOA_ASSIGNMENT, reqDTO.getQtdetail().getQtId(),
					reqDTO.getQtdetail().getQtNo());
			reqDTO.setDigiSignData(digiSign);
		}

		getQTDetailSO().updateGenericRequestApprovalTask(reqDTO);
		String qtDocTypeDesc = getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION);
		FacesContext fc = FacesContext.getCurrentInstance();
		String msgCode = MessageConst.INFO_700026;
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils.getMessage(msgCode, new String[] {
						qtDocTypeDesc, qtNo }, MessageUtils.BUNDLE_COMMON_MESSAGES,
						new FacesScopes().getUserLocale())));
		
		prepareQtMaintenanceAuditTrail(scQt,null,reqDTO); //#28895-35500
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	
	}
	
	/**
	 * Determine if role is disabled
	 * 
	 * @param memberRole
	 * @return flag to indicate if member is disabled
	 */
	public boolean assertCommitteeMemberRoleIsDisabled(String memberRole) {
		ScQt scQt = getManagedBean().getQt();
		if (this.getManagedBean().getCurrentQTStatus() == null && scQt != null) {
			ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, scQt.getQtId());
			if (wfStatus != null) {
				this.getManagedBean().setCurrentQTStatus(wfStatus.getPmStatus().getStatusId());
			}
		}
		Long qtStatus = this.getManagedBean().getCurrentQTStatus();
		if (null == qtStatus)
			return false;

		Long[] specDisabledRoleStatusArr = new Long[] { QTStatusConst.QT_PENDING_APPROVALS_CHECKLIST_SCORING,
				QTStatusConst.QT_PENDING_CHECKLIST_SCORING_ENDORSEMENT,
				QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF };

		List<String> list = new ArrayList<>();

		if (Arrays.asList(specDisabledRoleStatusArr).contains(qtStatus)
				|| this.getManagedBean().isCommitteeReviseFlow()) {
			list.add(ParameterConst.COMMITTEE_ROLE_SECRETARY);
			if (qtStatus.equals(QTStatusConst.QT_PENDING_CHECKLIST_SCORING_SIGN_OFF)) {
				list.add(ParameterConst.COMMITTEE_ROLE_MEMBER);
			}
		}
		return list.contains(memberRole);
	}

	// 25037 - end
	//#25077 - Start
		/**
		 * Upload attachment for history others approval.
		 * 
		 * @param event
		 *            the event
		 * @throws FileNotFoundException
		 *             the file not found exception
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 */
		public void handleHistoryOtherFileUpload(FileUploadEvent event)
				throws FileNotFoundException, IOException {
			if (event.getFile() != null) {
				ScQtApprovalRequest scQtApprovalHistoryDtls = this.getManagedBean().getHistoryApprovalRequests();
				PmParameter approvalType = scQtApprovalHistoryDtls.getApprovalType();
				ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalHistoryDtls.getApprovalRequestAttachement();
				if (null == approvalReqAttachement) {
					approvalReqAttachement = new ScQtApprovalReqAttachement();
					approvalReqAttachement.setApprovalRequest(scQtApprovalHistoryDtls);
				}
				approvalReqAttachement.setDocNo(this.getManagedBean().getQt().getQtNo());
				uploadAttachment(event.getFile(), approvalReqAttachement);
				scQtApprovalHistoryDtls.setApprovalRequestAttachement(approvalReqAttachement);
				if(approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)){
					this.getManagedBean().setScQtApprovalReqAttachementHistoryICT(approvalReqAttachement);
					this.getManagedBean().setFlagFileAttahmentDeletedICT(false); 
				}
				if(approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)){
					this.getManagedBean().setScQtApprovalReqAttachementHistoryMITI(approvalReqAttachement);
					this.getManagedBean().setFlagFileAttahmentDeletedMITI(false); 
				}
			}
		}
		
		/**
		 * Delete attachment for history others approval ICT.
		 */
		public void deleteHistoryOthersApprovalFileICT(BaseEntity delEntity) {
			List<ScQtApprovalRequest> list = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval()
					.getHistoryApprovalRequests();
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)){
					list.get(i).setApprovalRequestAttachement(null);
					this.getManagedBean().setScQtApprovalReqAttachementHistoryICT(null);
					this.getManagedBean().setFlagFileAttahmentDeletedICT(true);
				}
			}
		}
		
		/**
		 * Delete attachment for history others approval MITI.
		 */
		public void deleteHistoryOthersApprovalFileMITI(BaseEntity delEntity) {
			List<ScQtApprovalRequest> list = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval()
					.getHistoryApprovalRequests();
			for (int i = 0; i < list.size(); i++) {
				if(list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)){
					list.get(i).setApprovalRequestAttachement(null);
					this.getManagedBean().setScQtApprovalReqAttachementHistoryMITI(null);
					this.getManagedBean().setFlagFileAttahmentDeletedMITI(true); 
				}
			}
		}
		
		/**
		 * insert remarks while doing revise process flow
		 */
		private void invokeRemarksWhileExecuteReviseFlow(String outcome){
			ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean().getQt().getQtId());
			wfStatus = wfStatus==null ? new ScWorkFlowStatus(): wfStatus;
			if(wfStatus.getPmStatus()!=null && wfStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL) && 
					BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && !this.getManagedBean().isFirstPagePA()){	
				
					ScQtApproval publication = this.getManagedBean().getPublicationApproval();
					List<ScQtApprovalRemark> qtRemarks = initializeRemarks(publication, 
							wfStatus,
							publication.getScQtApprovalRemarks(), 
							ScQtApprovalRemark.class);
					for(ScQtApprovalRemark qtRemark : qtRemarks) {
						qtRemark.setEditable(false);
					}
					publication.setScQtApprovalRemarks(qtRemarks);
					this.getManagedBean().setScQtApproval(publication);
			}
		}
		//#25077 - End
		
	//#	29762-start
		private void checkSampleRequiredChecking(ScQt scQt){
			log.info("===========ApprovalBackingbeancheckSampleRequiredChecking========");
			// Setting Sample Submission to qtDetails.
			List<ScQtSampleSubmission> sampleSubmissionList = null;
			if (scQt.isZonal()) {
				sampleSubmissionList = getManagedBean()
						.getScQtSampleSubmissionList();
				if (sampleSubmissionList != null
						&& !sampleSubmissionList.isEmpty()) {
					for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
						ScAddress scAddress = scQtSampleSubmission.getScAddress();

						if (null != scAddress) {
							if (scAddress.getPmDivision() != null
									&& scAddress.getPmDivision()
											.getDivisionId() == null) {
								scAddress.setPmDivision(null);
							}
							//scAddress.setPmCountry(retrieveCountry());
						}
						//scQtSampleSubmission.setScQt(getManagedBean().getQt());
					}
				}
			} else {
				sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
				if (null != this.getManagedBean().getScQtSampleSubmission()) {
					ScAddress scAddress = this.getManagedBean()
							.getScQtSampleSubmission().getScAddress();

					if (scAddress.getPmDivision() != null
							&& scAddress.getPmDivision().getDivisionId() == null) {
						scAddress.setPmDivision(null);
					}
					//scAddress.setPmCountry(retrieveCountry());
					sampleSubmissionList.add(this.getManagedBean()
							.getScQtSampleSubmission());
				}
			}
		}
		//#	29762-start
		
		//#30477 - Start
		/**
		 * to get check is inactive publication approver
		 * @return
		 */
		public boolean isInactivePublicationApproverRole() {
			IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTDetailSO.class);
			ScQt scQt = this.getManagedBean().getQt();
			ScQtApproval publicationApproval = this.getManagedBean().getPublicationApproval();
			
			if (null == publicationApproval ) {
				QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
				reqDTO.setQtId(scQt.getQtId());
				reqDTO.setApprovalCategory(ParameterConst.APPROVAL_CATEGORY_PTJ);
				QuationTenderResDTO resDTO = detailSO.retrieveApprovalDtlsByQtIdNType(reqDTO);
				publicationApproval = resDTO.getQtApproval();
			}
		
			List<ScQtApproverSettings> approverSettings = publicationApproval.getApproverSettings();
			QTDetailBackingBean qtBackingBean = new QTDetailBackingBean();
			return qtBackingBean.isInactiveCommiteeApproverRole(null,approverSettings, RoleCode.PUBLICATION_APPROVER.value());
		}
		//#30477 - Start
		
	// #25426 - Start
	/**
	 * 202407252@haziq #25426 To remove EXIST DOA when approve cancel QT
	 * 
	 * @param scQt
	 */
	private void approveCancelQTwithDOASenariosExists(ScQt scQt) {
		String docNo = scQt.getQtNo();

		BpmRequestDTO bpmReqDTO = new BpmRequestDTO();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);

		bpmReqDTO.setDocNumber(docNo);
		bpmReqDTO.setState(BpmConstants.STATE_ASSIGNED);
		BpmResponseDTO bpmResDTO = bpmSO.getTasksByDocNumber(bpmReqDTO);
		boolean isWithdrawTask = false;
		
		if (bpmResDTO.getWorklistVO() != null
				&& CollectionUtils.isNotEmpty(bpmResDTO.getWorklistVO().getWorklistItem())) {
			for (WorklistItemVO currentVO : bpmResDTO.getWorklistVO().getWorklistItem()) {
				String docType = currentVO.getDocType();
				if (StringUtils.isNotBlank(docType) && docType.equals(ParameterConst.DOCTYPE_DOA_ASSIGNMENT)
						&& StringUtils.isNotBlank(currentVO.getTaskId())) {

					Object token = bpmSO.authenticate(EpPropertiesUtils.getInstance().getProperty(
							EpPropertiesConst.BPM_PRINCIPAL));

					bpmReqDTO = new BpmRequestDTO();
					bpmReqDTO.setTaskId(currentVO.getTaskId());
					bpmReqDTO.setCtxToken(token);
					bpmSO.withdrawTask(bpmReqDTO);

					isWithdrawTask = true;
					break;
				}
			}
			if (isWithdrawTask) {
				updateWorkFlowStatus(scQt);
			}

		}
		// #25426 - End
	}
	
	/**
	 * update workflow Status
	 * @param scQt
	 */
	private void updateWorkFlowStatus(ScQt scQt) {
		IScWorkFlowStatusSO workFlowStatusSO = null;

		try {
			workFlowStatusSO = (IScWorkFlowStatusSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			ScWorkFlowStatusReqDTO workFlowReqDTO = new ScWorkFlowStatusReqDTO();
			ScQtStatus workFlowStatus = new ScQtStatus();
			workFlowStatus.setScQt(scQt);
			workFlowStatus.setCurrent(true);
			PmStatus newStatus = getPmStatusByStatusId(QTStatusConst.DAR_CANCELLED);
			workFlowStatus.setPmStatus(newStatus);
			workFlowReqDTO.setScWorkFlowStatus(workFlowStatus);
			workFlowReqDTO.setDocType(ParameterConst.DOCTYPE_DOA_ASSIGNMENT);
			workFlowReqDTO.setDocId(scQt.getQtId());
			workFlowStatusSO.updateCurrentWorkflowStatus(workFlowReqDTO);

		} catch (Exception e) {
			SLAUtils.getInstance().logSLAError(ModuleCodeConst.SUPPLIER_SELECTION, ParameterConst.DOCTYPE_QUOTATION,
					scQt.getQtNo(), "approvalBackingBean.updateWorkFlowStatus", getUserIdSessionFromLoginId(), e);
		}
	}

	// #25426 - End
			

		
		//#28784
		private FacesScopes getFacesScopes() {
			FacesScopes facesScopes = new FacesScopes();
			return facesScopes;
		}
				
		//#21692 - start
		public String getNewValidityPeriod () {
			String newValidity= null;
			
			for (ScQtApprovalRequest req : this.getManagedBean().getScQtApprovalRequestList()) {
				if (null != req.getChangedValue()) {
					newValidity = req.getChangedValue();
				}
			}
			
			return newValidity;
		}
		
		private void responsePeriodValidations(ScQt scQt) {
			if (null == scQt.getResponsePeriod()) {
				throw new EpException(MessageConst.ERROR_770243,
						new String[] {ResourceBundleUtils.getMessage("lbl.sc.qt.supplier.response.period", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
								new FacesScopes().getUserLocale())});
			}
			int newValue = scQt.getResponsePeriod();
			String fromValue = retrieveResponsePeriod(ParameterConst.FROM_VALUE);
			String toValue = retrieveResponsePeriod(ParameterConst.TO_VALUE);
			if (newValue < Integer.parseInt(fromValue)) {
				throw new EpException(MessageConst.ERROR_770244,
						new String[] { fromValue });
			}
			if (newValue > Integer.parseInt(toValue)) {
				throw new EpException(MessageConst.ERROR_770245,
						new String[] { toValue });
			}
		}
		
		private String retrieveResponsePeriod(String value) {
			
			String result = null;
			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setRuleType(RuleTypeConst.VALIDITY_PERIOD_EXTENSION_RESPONSE_PERIOD);
			IRuleSO ruleSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,IRuleSO.class);
			RuleResDTO roleResDTO = ruleSO.getRuleValuesByRuleType(reqDTO);
			if (roleResDTO.isSuccessful()
					&& CollectionUtils.isNotEmpty(roleResDTO.getRuleValueList())) {
				if (value.equals(ParameterConst.FROM_VALUE)) {
					result = roleResDTO.getRuleValueList()
							.get(0).getFromValue().toString();
				}
				if (value.equals(ParameterConst.TO_VALUE)) {
					result = roleResDTO.getRuleValueList()
							.get(0).getToValue().toString();
				}
			} else {
				throw new EpException(MessageConst.ERROR_770222,
						new String[] { getRuleTypeDesc(reqDTO.getRuleType()) });
			}
			
			return result;
		}
		
		private void checkAttachedDocument(QuationTenderReqDTO reqDTO) {
			
			ScQtApprovalRequest approvalRequest = reqDTO.getApprovalRequests().get(0);
			if (getManagedBean().getQt().isApprovalPBM()) {
				if (approvalRequest.getEpvApprovalReqAttachmentR2() == null) {
					throw new EpException(MessageConst.ERROR_770401);
				} else if (approvalRequest.getDecisionCertPBM().getParameterId().equals(QTStatusConst.PCB_RQ2_APPROVED)
						&& approvalRequest.getEpvApprovalReqAttachmentR3() == null) {
					throw new EpException(MessageConst.ERROR_770402);
				}
			} else {
				if (approvalRequest.getEpvApprovalReqAttachmentR1() == null) {
					throw new EpException(MessageConst.ERROR_770401);
				}
			}
		}
		
		//doing this to enable date validation
		/**
		 * validate html first to make sure no error happen
		 */
		public void validDateFirst() {
			String showDialog = "confirmUpdateValidityExtension.show()";
			RequestContext.getCurrentInstance().execute(showDialog);
		}
		/**
		 * pop up reject screen with yes or no after use click on button submit
		 */
		public void rejectShowUp() {
			String showDialog = "supplierRejectConfirmation.show()";
			RequestContext.getCurrentInstance().execute(showDialog);
		}
		//#21692 - Start
		/**
		 * Finalization committee message after use click on submit
		 * whether reject or accept EPV
		 * 
		 * @param outCome
		 * @return
		 */
		private String getFCMessgeConstant(String outCome) {
			String msgCode;
			if (outCome.equalsIgnoreCase(BpmOutcomeConst.BPM_REJECT)) {
				msgCode = this.getManagedBean().isProposalValidityRevised() ? MessageConst.INFO_790104 : MessageConst.INFO_790103;
			} else {
				msgCode = this.getManagedBean().isNoSupplierExistForEPV() ? MessageConst.INFO_790105 : MessageConst.INFO_790102;
			}
			
			return msgCode;
		}
		//#21692 - End
		
		private Timestamp getNewValidityDueDate(ScQt scQt, int newValidityPeriod) {
			IScWorkFlowStatusSO workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_SELECTION, IScWorkFlowStatusSO.class);
			
			ScWorkFlowStatusReqDTO reqDTO = new ScWorkFlowStatusReqDTO();
			reqDTO.setDocId(scQt.getQtId());
			reqDTO.setDocType(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD);
			
			ScWorkFlowStatus status = workFlowStatusSO.retrieveStatusByTypeAndId(reqDTO).getWorkFlowStatus();
			if (status != null) {

				Date date = DateUtils.addDays(scQt.getClosingDate(), newValidityPeriod);

				Timestamp ts = null;
				if (date != null) {
					ts = new Timestamp(date.getTime());
				}
				return ts;
			}
			return null;
		}
		
		/**
		 * check if FC approve EPV 
		 * 
		 * @param apprvReq
		 * @return
		 */
		public boolean isEpvRequestApproved(ScQtApprovalRequest apprvReq) {
			return QTStatusConst.PCB_RQ1_APPROVED.longValue() == apprvReq.getDecisionType().getParameterId()
					|| QTStatusConst.PCB_RQ3_APPROVED.longValue() == apprvReq.getDecisionType().getParameterId();
		}
		
		/**
		 * enable each Type of Request to has attachment
		 * 
		 * @param event
		 * @throws FileNotFoundException
		 * @throws IOException
		 */
		public void epvHandleFileUpload(FileUploadEvent event)
				throws IOException {
			String typeOfRequest = this.getManagedBean().getEpvAttachmentValue();
			ScQtApprovalRequest apprvRequest = this.getManagedBean().getScQtApprovalRequestList().get(0);
			List<ScQtEpvApprovalReqAttachment> attachmentList = new ArrayList<>();
			setDecisionsNull(apprvRequest);
			ScQtEpvApprovalReqAttachment epvAttachment = null;
			if (CollectionUtils.isNotEmpty(apprvRequest.getEpvApprovalReqAttachment())) {
				for (ScQtEpvApprovalReqAttachment att : apprvRequest.getEpvApprovalReqAttachment()) {
					//if sub doc type same as needed will assign as exist
					if (att.getSubDocType().equalsIgnoreCase(typeOfRequest)) {
						epvAttachment = att;
					}
					attachmentList.add(att);
				}
			}
			
			if (epvAttachment == null) {
				epvAttachment = new ScQtEpvApprovalReqAttachment();
				epvAttachment.setApprovalRequest(apprvRequest);
				epvAttachment.setSubDocType(typeOfRequest);
			}
			epvAttachment.setDocNo(getManagedBean().getQt().getQtNo());
			uploadAttachment(event.getFile(), epvAttachment);
			attachmentList.add(epvAttachment);
			apprvRequest.setEpvApprovalReqAttachment(attachmentList);
			this.getManagedBean().setAllEPVAttachments(attachmentList);
			
			if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_1)) {
				apprvRequest.setEpvApprovalReqAttachmentR1(epvAttachment);
			} else if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_2)) {
				apprvRequest.setEpvApprovalReqAttachmentR2(epvAttachment);
			} else if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_3)) {
				apprvRequest.setEpvApprovalReqAttachmentR3(epvAttachment);
			}

			handleDecisionNull(apprvRequest);
			boolean toBeRemoveAttachmentIsNotEmpty = CollectionUtils.isNotEmpty(this.getManagedBean().getToBeRemoveEPVAttachments())
					&& this.getManagedBean().getToBeRemoveEPVAttachments().contains(typeOfRequest);
			//#21692 HashSet to remove duplicate, clear current list, add list to managedBean - Start
			if (toBeRemoveAttachmentIsNotEmpty) {
				Set<String> set = new HashSet<>(this.getManagedBean().getToBeRemoveEPVAttachments());
				this.getManagedBean().getToBeRemoveEPVAttachments().clear();
				this.getManagedBean().getToBeRemoveEPVAttachments().addAll(set);
				this.getManagedBean().getToBeRemoveEPVAttachments().remove(typeOfRequest);
			}
			//#21692 HashSet to remove duplicate, clear current list, add list to managedBean - End
		}
		
		/**
		 * Needed to handle decisions if empty/null AFTER file upload/delete
		 * 
		 * @param apprvRequest
		 */
		private void handleDecisionNull(ScQtApprovalRequest apprvRequest) {
			if (apprvRequest.getDecisionType() == null) {
				apprvRequest.setDecisionType(new PmParameter());
			}
			if (this.getManagedBean().getQt().isApprovalPBM()
					&& apprvRequest.getDecisionCertPBM() == null) {
				apprvRequest.setDecisionCertPBM(new PmParameter());
			}
		}
		
		/**
		 * overload method without Base Entity
		 * 
		 * @param delEntity
		 */
		public void deleteEpvUploadFile(BaseEntity delEntity) {
			deleteEpvUploadFile();
		}
		
		/**
		 * delete EPV file update either R1, R2 or R3
		 * 
		 * @param delEntity
		 */
		public void deleteEpvUploadFile() {
			String typeOfRequest = this.getManagedBean().getEpvAttachmentValue();
			ScQtApprovalRequest apprvRequest = this.getManagedBean().getScQtApprovalRequestList().get(0);
			setDecisionsNull(apprvRequest);
			if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_1)) {
				apprvRequest.setEpvApprovalReqAttachmentR1(null);
			} else if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_2)) {
				apprvRequest.setEpvApprovalReqAttachmentR2(null);
			} else if (typeOfRequest.equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_3)) {
				apprvRequest.setEpvApprovalReqAttachmentR3(null);
			}
			if (CollectionUtils.isEmpty(this.getManagedBean().getToBeRemoveEPVAttachments())) {
				this.getManagedBean().setToBeRemoveEPVAttachments(new ArrayList<String>());
			}
			this.getManagedBean().getToBeRemoveEPVAttachments().add(typeOfRequest);
			handleDecisionNull(apprvRequest);
		}
		
		/**
		 * cannot assign value a constant directly from html, take the value to backing bean and return string
		 * 
		 * @param epvType
		 * @return
		 */
		public String assignValueForEpvAttachment(String epvType) {			
			return epvType;
		}
		
		//make sure if epv attachment is not empty, assign each of them to their transient
		private void setEpvAttachment(List<ScQtApprovalRequest> apprvReqList) {
			ScQtApprovalRequest apprvReq = apprvReqList.get(0);
			
			if (CollectionUtils.isNotEmpty(apprvReq.getEpvApprovalReqAttachment())) {
				for (ScQtEpvApprovalReqAttachment att : apprvReq.getEpvApprovalReqAttachment()) {
					if (att.getSubDocType().equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_1)) {
						apprvReq.setEpvApprovalReqAttachmentR1(att);
					} else if (att.getSubDocType().equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_2)) {
						apprvReq.setEpvApprovalReqAttachmentR2(att);
					} else if (att.getSubDocType().equalsIgnoreCase(ParameterConst.EPV_TYPE_REQUEST_3)) {
						apprvReq.setEpvApprovalReqAttachmentR3(att);
					}
				}
			}
		}
		
		//set decisions null for attachments
		private void setDecisionsNull(ScQtApprovalRequest apprvRequest) {
			if (this.getManagedBean().getQt().isApprovalPBM()) {
				if (apprvRequest.getDecisionCertPBM().getParameterId() == null) {
					apprvRequest.setDecisionCertPBM(null);
				}
				if (apprvRequest.getDecisionType().getParameterId() == null) {
					apprvRequest.setDecisionType(null);
				}
			} else {
				if (apprvRequest.getDecisionType().getParameterId() == null) {
					apprvRequest.setDecisionType(null);
				}
			}
		}
		
		//set new param for null decisions
		private void setDecisionsNewParam(ScQtApprovalRequest apprvReq) {
			if (this.getManagedBean().getQt().isApprovalPBM()) {
				if (apprvReq.getDecisionCertPBM() == null) {
					apprvReq.setDecisionCertPBM(new PmParameter());
				}
				if (apprvReq.getDecisionType() == null) {
					apprvReq.setDecisionType(new PmParameter());
				}
			} else {
				if (apprvReq.getDecisionType() == null) {
					apprvReq.setDecisionType(new PmParameter());
				}
			}
		}
		//#21692 - End
		//#21692 - Start
		private List<ScQtApprovalRequest> proposalValidityWip(String outcome) {

			List<ScQtApprovalRequest> scQtApprovalRequests = new ArrayList<>();
			for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
				if (approvalRequest.getApproverAction() == null) {
					PmParameter pmParameter = getQTDetailSO().getPmParameterByTypeAndCode(
							ParameterTypeConst.APPROVAL_DECISION_TYPE, outcome == BpmOutcomeConst.BPM_REJECT ? 
									this.getManagedBean().isProposalValidityRevised() ? ParameterConst.TR2_REVISE : ParameterConst.FLAG_REJECT : ParameterConst.FLAG_ACCEPT);
					approvalRequest.setApproverAction(pmParameter);
					approvalRequest.setDecisionType(null);
					approvalRequest.setDecisionCertPBM(null);
					
				}
				scQtApprovalRequests.add(approvalRequest);
			}
			return scQtApprovalRequests;
		}
		//#21692 - End
		//#21692 - Start
		private void checkResponsePeriod() {
			if (this.getManagedBean().getQt().getResponsePeriod() != this.getManagedBean().getScQtApprovalRequestList().get(0).getResponsePeriod()) {
				List<ScQtApprovalRemark> remarks = getManagedBean().getScQtApproval().getScQtApprovalRemarks();
				if (null != remarks) {
					checkRemarks(remarks);
				}
			}
		}
		
		private void checkRemarks(List<ScQtApprovalRemark> remarks) {
			for (int i = 0; i < remarks.size(); i++) {
				String userRemark = remarks.get(i).getRemark();
				if (null == userRemark || "".equals(userRemark)) {
					throw new EpException(MessageConst.ERROR_770404);
				}
			}
		
		}
		
		private void checkValidityDateAndSupplierResponseDate() {
			ScQtApprovalRequest request = this.getManagedBean().getScQtApprovalRequestList().get(0);
			Date date = new Date();
			date = DateUtils.addDays(date, request.getResponsePeriod());
			if (date.after(this.getManagedBean().getScQtApprovalRequestList().get(0).getProposalValidityEndDateNew())
					&& (QTStatusConst.PCB_RQ1_APPROVED.longValue() == request.getDecisionType().getParameterId()
							|| QTStatusConst.PCB_RQ3_APPROVED.longValue() == request.getDecisionType().getParameterId())) {
				throw new EpException(MessageConst.ERROR_770405);
			}
		}
		//#21692 - End
		
		//#21692 - Start
		private void initializeDecisionCertPBM() {
			boolean mofDateAppeared = this.getManagedBean().getScQtApprovalRequestList().get(0).getChmnFinalizationDate() != null
					&& this.getManagedBean().getQt().getProposalValidityEndDate().before(this.getManagedBean().getScQtApprovalRequestList().get(0).getChmnFinalizationDate());
			if (this.getManagedBean().getScQtApprovalRequestList().get(0).getDecisionCertPBM() == null
					&& mofDateAppeared) {
				this.getManagedBean().getScQtApprovalRequestList().get(0).setDecisionCertPBM(new PmParameter());
			}
			
			if (this.getManagedBean().getScQtApprovalRequestList().get(0).getDecisionCertPBM() != null
					&& this.getManagedBean().getScQtApprovalRequestList().get(0).getDecisionCertPBM().getParameterCode() != null
					&& (this.getManagedBean().getScQtApprovalRequestList().get(0).getDecisionCertPBM().getParameterCode().equals(ParameterConst.TR2_NOTACKNOWLEDGE)
							|| this.getManagedBean().getScQtApprovalRequestList().get(0).getDecisionCertPBM().getParameterCode().equals(ParameterConst.TR2_REVISE))) {
				this.getManagedBean().setChmnFinalizationRejectEPV(Boolean.TRUE);
				this.getManagedBean().getQt().setMofDate(null);//Null date after hide
			}
		}
		//#21692 - End
		

		/**
	     * Method to prepare needed audit trails parameters for Briefing Site/Visit & Desk Officer Assistant Maintenance
	     * #28895-35500
	     * @param scQt
	     * @param groupTabSection
	     * @param reqDTO
	     */
	public void prepareQtMaintenanceAuditTrail(ScQt scQt, Long groupTabSection, QuationTenderReqDTO reqDTO) {
		IQTAuditTrailSO qtAuditTrailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTAuditTrailSO.class);

		AuditTrailResDTO auditResDTO = new AuditTrailResDTO();
		List<AuditTrailReqDTO> auditReqList = new ArrayList<>();


		auditResDTO.setDocTypeId(scQt.getProcurementMode().getParameterId());
		auditResDTO.setDocId(scQt.getQtId());
		auditResDTO.setDocNo(scQt.getQtNo());
		auditResDTO.setCreatedBy(getUserIdSessionFromLoginId());
		if (groupTabSection != null) {// BSV Audit Trails Section
			auditResDTO.setAuGroupSectionId(groupTabSection);
			auditResDTO.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			auditResDTO.setStatusId(QTStatusConst.EPV_APPROVED);
			AuditTrailReqDTO auditReqDTO = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
			auditReqList.add(auditReqDTO);

			qtAuditTrailSO.insertAuditTrail(auditReqList);
		} else if (reqDTO != null) {// DOA Audit Trails Section
			if (reqDTO.getApproverAction().equals(BpmOutcomeConst.BPM_SUBMIT)) {
				auditResDTO.setRoleCode(RoleCode.DESK_OFFICER.value());
			} else {
				auditResDTO.setRoleCode(RoleCode.PUBLICATION_APPROVER.value());
			}
			auditResDTO.setAuGroupSectionId(QTAuditConstant.AU_SECTION_DOA);			

			if (reqDTO.getApproverAction().equals(BpmOutcomeConst.BPM_SUBMIT)) {
				auditResDTO.setStatusId(QTStatusConst.DAR_PENDING_REVISE_APPROVAL);
				auditResDTO.setQtFieldLabelId(QTAuditConstant.AU_FIELD_DOA_DESK_OFFICER_ASSISTANT_NAMES);
				List<ScQtContactPerson> removedList = reqDTO.getDeletedDoaaList() == null ? new ArrayList<ScQtContactPerson>()
						: reqDTO.getDeletedDoaaList();
				List<ScQtContactPerson> newDoaList = reqDTO.getDoaaList() == null ? new ArrayList<ScQtContactPerson>()
						: reqDTO.getDoaaList();

				for (ScQtContactPerson add : newDoaList) {
					auditResDTO.setActionType(QTAuditConstant.AU_TYPE_CREATE);
					auditResDTO.setNewValue(add.getUser().getUserName());
					auditResDTO.setNewValueId(add.getUser().getUserId());

					AuditTrailReqDTO auditReqDTO = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
					auditReqList.add(auditReqDTO);
				}
				for (ScQtContactPerson remove : removedList) {
					auditResDTO.setNewValue(null);
					auditResDTO.setActionType(QTAuditConstant.AU_TYPE_DELETE);
					auditResDTO.setOldValue(remove.getUser().getUserName());
					auditResDTO.setOldValueId(remove.getUser().getUserId());

					AuditTrailReqDTO auditReqDTO = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
					auditReqList.add(auditReqDTO);
				}
				qtAuditTrailSO.insertAuditTrail(auditReqList);
			} else {
				if (reqDTO.getApproverAction().equals(BpmOutcomeConst.BPM_APPROVE)) {
					auditResDTO.setStatusId(QTStatusConst.DAR_COMPLETED);
				} else {
					auditResDTO.setStatusId(QTStatusConst.DAR_PENDING_REVISION);
				}
				AuditTrailReqDTO auditReqDTO = qtAuditTrailSO.populateAuditReqDTO(auditResDTO);
				auditReqList.add(auditReqDTO);
				qtAuditTrailSO.insertAuditTrail(auditReqList);
			}
		}
	}
		
		//#21692 - Start
		/**
		 * Send notification after Submission
		 * Notification of Decision on the Extension of Validity Period for Tender
		 * 
		 * @param approvalRequest
		 */
		public void notifyUsers(ScQtApprovalRequest approvalRequest) {
			Map<String, Object> epvObj = new HashMap<>();
			ScQt scQt = this.getManagedBean().getQt();
			try {
				PmOrgProfile ptjProfile = scQt.getPmOrgProfile();
				epvObj.put(CommonConstants.OBJ_PTJ_NAME, ptjProfile.getPmOrgValidity().getOrgName());
				epvObj.put(CommonConstants.OBJ_PTJ_CODE, ptjProfile.getPmOrgValidity().getOrgCode());
				PmOrgProfile pmOrgProfile = retrieveMinistryOrgProfile(scQt.getPmOrgProfile().getOrgProfileId());
				if (null != pmOrgProfile && null != pmOrgProfile.getPmOrgValidity()) {
					epvObj.put(CommonConstants.OBJ_MINISTRY_NAME, pmOrgProfile.getPmOrgValidity().getOrgName());
				}
				
				String userRemark = this.getManagedBean().getScQtApproval().getScQtApprovalRemarks().get(0).getRemark();
				if (this.getManagedBean().isNullEPVRemark()) {
					userRemark = ResourceBundleUtils.getMessage(SourcingConstants.NOT_AVAILABLE, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
							new FacesScopes().getUserLocale());
				}
				epvObj.put(CommonConstants.REMARK, userRemark);
				
				getQTDetailSO().sendEPVNotification(epvObj, scQt, approvalRequest);
				
			} catch(Exception e) {
				SLAUtils.getInstance().logSLAError(SourcingConstants.SUPPLIER_SELECTION_DOC_TYPE, "notifyUsers", scQt.getQtNo(), "notifyUsers",
						getUserIdSessionFromLoginId(), e);
			}
		}
		//#21692 - End
		//#21692 - Start
		private void reviseRemarkMandatory() {
			List<ScQtApprovalRemark> remarks = this.getManagedBean().getScQtApproval().getScQtApprovalRemarks();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					
					if (null == userRemark || "".equals(userRemark)) {
						throw new EpException(MessageConst.ERR_775152);
					}
				}
			}
		}
		//#21692 - End
		//#21692 - Start
		private void checkNullRemark() {
			this.getManagedBean().setNullEPVRemark(false);
			List<ScQtApprovalRemark> remarks = this.getManagedBean().getScQtApproval().getScQtApprovalRemarks();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					
					if (null == userRemark || "".equals(userRemark)) {
						this.getManagedBean().setNullEPVRemark(true);
					}
				}
			}
		}
		//#21692 - End
		
		//#21692 - Start
		/**
		 * retrieve the result of Proposal Validity Extension
		 * from status Pending Approval
		 * 
		 * @param approvalRequest
		 * @return 'Revise' if parameterCode 'V'
		 */
		public String retrieveApproverAction(ScQtApprovalRequest approvalRequest) {
			if (approvalRequest.getDecisionType().getParameterCode().equals(ParameterConst.APPROVAL_DECISION_TYPE_REVISED)) {
				return ResourceBundleUtils.getMessage(SourcingConstants.STATUS_REVISE_ACTION_LABEL, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
						new FacesScopes().getUserLocale());
			} else {
				return getParameterDesc(approvalRequest.getApproverAction());
			}
		}
		//#21692 - End

	// 36671 - start
	private void prepareCommitteApprovalRequest(Long qtId) {

		ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_COMMITTE_APROVAL, qtId);
		Long currentCmtStatus = wfStatus == null ? 0L : wfStatus.getPmStatus().getStatusId();
		boolean isCommitteeUpdateFlow = Arrays.asList(
				new Long[] { QTStatusConst.CMT_PENDING_REAPPROVAL,
						QTStatusConst.CMT_PENDING_REAPPROVAL_REVISION_APPROVAL }).contains(currentCmtStatus);

		this.getManagedBean().setCommitteeUpdateFlow(isCommitteeUpdateFlow);

		String approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT;
		List<String> paramTypes = Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST);
		List<ScQtApprovalRequest> approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
				ParameterConst.DOCTYPE_COMMITTE_APROVAL, paramTypes, null);

		this.getManagedBean().setApprovalType(approvalCategoryCode);

		ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
				ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		ISearchBean<ScQtCommittee> commSearchBean = new SearchBean<>();
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scQt.qtId", SearchOperator.OPERATOR_EQUALS.value(), qtId));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scCommittee.approvalReq", SearchOperator.OPERATOR_EQUALS.value(), true));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scCommittee.committeeType.parameterCode", SearchOperator.OPERATOR_IN.value(),
						paramTypes));
		commSearchBean.getDataFilterBeans().add(
				new SearchFilterBean("scCommittee.committeStatus.statusId", SearchOperator.OPERATOR_NOT_IN.value(),
						Arrays.asList(QTStatusConst.CMT_PENDING_SUBMISSION, QTStatusConst.CMT_COMPLETED,
								QTStatusConst.CMT_PENDING_REVISION)));

		committeReqDTO.setCommSearchBean(commSearchBean);
		CommitteResDTO resDTO = committeSO.retriveQtCommitteDtls(committeReqDTO);
		ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO.getQtCommitteResultBean();
		this.getManagedBean().setQtCommittes(qtcomSearchBean);
		List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean.getResultList(), qtId);
		this.getManagedBean().getQt().setScQtCommittees(qtcomSearchBean.getResultList());
		this.getManagedBean().setScCommitteeMemberTabList(tabBeans);
		this.getManagedBean().setParamTypes(paramTypes);
		this.getManagedBean().setToBeDelCommitteMemebers(new ArrayList<ScCommitteeMember>());
		if (CollectionUtils.isNotEmpty(tabBeans)) {
			getManagedBean().setSelectedTabBean(tabBeans.get(0));
			List<QtCommitteeMemberBean> historyList = getMemberHistoryList(getManagedBean().getSelectedTabBean()
					.getScQtCommittee().getScCommittee());
			this.getManagedBean().setMemberHistoryList(historyList);
		}
		prepareEvalCommiteeApproval(approvalRequests);
		ScWorkFlowStatus currentStatus = getManagedBean().getQt().getCurrentWorkFlowStatus();
		initializeRemarks(this.getManagedBean().getScQtApproval(), currentStatus, getManagedBean().getScQtApproval()
				.getScQtApprovalRemarks(), ScQtApprovalRemark.class);
		this.getManagedBean().setScQtApprovalRequestList(approvalRequests);

	}

	// 36671 - end

	// 36671 - start
	private void loadQtDetails() {
		QTDetailBackingBean qtDetailBB = new QTDetailBackingBean();
		qtDetailBB.editQuationTenderDetails(getManagedBean().getQt(), false);
		qtDetailBB.getManagedBean().setAdditionalPages(null);
		qtDetailBB.getManagedBean().setUserTasks(null);
		ScQt scQt = qtDetailBB.getManagedBean().getQt();

		if (CollectionUtils.isNotEmpty(this.getManagedBean().getScCommitteeMemberTabList())) {
			List<TabBean> tabs = this.getManagedBean().getScCommitteeMemberTabList();
			String committeeType = tabs.get(this.getManagedBean().getSelectedCommitteeTabIndex()).getTabType();
			getManagedBean().setRoleFilterList(
					getRoleFilterListForCommitteeMaintenance(this.getManagedBean().getQt(), committeeType));
		}

		if (this.getManagedBean().isCommitteeReviseFlow()) {
			getManagedBean().setRoleFilterList(new String[] { ParameterConst.COMMITTEE_ROLE_SECRETARY });
		}

		List<ScQtCommittee> qtCommittees = this.getManagedBean().getQt().getScQtCommittees();

		boolean isEvalCommittee = false;
		for (ScQtCommittee qtCommittee : qtCommittees) {
			if (CommitteeTypeIsEcOcTecFec(qtCommittee.getScCommittee())) {
				isEvalCommittee = true;
				break;
			}
		}

		boolean isFullTablist = false;
		ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, scQt.getQtId());

		if (null != workFlowStatus) {
			PmStatus status = workFlowStatus.getPmStatus();
			scQt.setQtStatus(status);
			Long qtStatusId = status.getStatusId();
			isFullTablist = qtDetailBB.isQTStatusGreaterThanOrEquals(qtStatusId,
					QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
			qtDetailBB.getManagedBean().setCurrentStatusId(qtStatusId);
			this.getManagedBean().setSubStatusId(qtStatusId);
		}

		if (isEvalCommittee || isFullTablist) {
			qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString(),
					QTConstants.QT_CMT_APPROVER_PANEL);
			qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
		} else {
			qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(), QTConstants.QT_CMT_APPROVER_PANEL);
			qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApproval);
		}

		qtDetailBB.getManagedBean().setActiveIndex(this.getManagedBean().getActiveIndex());
		qtDetailBB.getManagedBean().setApprovalType(this.getManagedBean().getApprovalType());
		qtDetailBB.getManagedBean().setAutoUpdate(this.getManagedBean().isAutoUpdate());
		qtDetailBB.getManagedBean().setCurrentStatusId(this.getManagedBean().getCurrentStatusId());
		qtDetailBB.getManagedBean().setOutComes(this.getManagedBean().getOutComes());
		scQt.setScQtCommittees(this.getManagedBean().getQt().getScQtCommittees());
		scQt.setNewPublishDate(this.getManagedBean().getQt().getNewPublishDate());
		qtDetailBB.getManagedBean().setQtCommitteeRemarks(this.getManagedBean().getQtCommitteeRemarks());
		qtDetailBB.getManagedBean().setQtSupplierList(this.getManagedBean().getQtSupplierList());
		qtDetailBB.getManagedBean().setScCommitteeMemberTabList(this.getManagedBean().getScCommitteeMemberTabList());
		qtDetailBB.getManagedBean().setScQtApproval(this.getManagedBean().getScQtApproval());
		qtDetailBB.getManagedBean().setScQtApprovalRequestList(this.getManagedBean().getScQtApprovalRequestList());
		qtDetailBB.getManagedBean().setSelectedTabBean(this.getManagedBean().getSelectedTabBean());
		qtDetailBB.getManagedBean().setSelectedCommitteeTabIndex(this.getManagedBean().getSelectedApprovalTabIndex());
		qtDetailBB.getManagedBean().setTaskName(this.getManagedBean().getTaskName());
		qtDetailBB.getManagedBean().setApprovalEditableSection(true);
		qtDetailBB.getManagedBean().setApprovalScreen(true);

		boolean readOnly = qtDetailBB.getManagedBean().isReadOnly();
		this.getManagedBean().copyBPMTaskDetails(qtDetailBB.getManagedBean());
		qtDetailBB.getManagedBean().setReadOnly(readOnly);
	}

	// 36671 - end

	// 36671 - start
	/**
	 * Save method for when committee approver approve committee
	 */
	public void saveQtCAApproval() {
		ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ScQt scQt = this.getManagedBean().getQt();
		ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();

		List<TabBean> scQtCommittees = this.getManagedBean().getScCommitteeMemberTabList();
		for (TabBean tabBean : scQtCommittees) {
			scQt.setScQtCommittees(new ArrayList<ScQtCommittee>());
			scQt.getScQtCommittees().add(tabBean.getScQtCommittee());
		}
		scQtApproval.setScQtApprovalDtls(this.getManagedBean().getScQtApprovalRequestList());
		scQt.setScQtApprovals(new ArrayList<ScQtApproval>());
		scQt.getScQtApprovals().add(scQtApproval);
		finalizeRemarks(scQtApproval.getScQtApprovalRemarks());

		reqDTO.setScQt(scQt);
		reqDTO.setApprovalRequests(this.getManagedBean().getScQtApprovalRequestList());
		reqDTO.setQtCommittee(scQt.getScQtCommittees());
		reqDTO.setToBeDeletedMembers(this.getManagedBean().getToBeDelCommitteMemebers());
		CommitteResDTO resDTO = committeSO.saveCommitteeDetails(reqDTO);

		if (resDTO.isSuccessful()) {
			prepareCommitteApprovalRequest(scQt.getQtId());
			loadQtDetails();
			successMessage(MessageConst.INFO_700090, ParameterTypeConst.DOCUMENT_TYPES,
					ParameterConst.DOCTYPE_QUOTATION, scQt.getQtNo(), false);
		}
	}
	// 36671 - end

	//#21692 #note-504 - Start
	/**
	 * Only used for Proposal Validity Extension Attachment Removal
	 * Will remove every attachment in list of toBeRemoveEpvAttachments
	 * 
	 * @param approvalRequest
	 * @return	ScQtApprovalRequest with new EPV Attachment (deleted)
	 */
	private ScQtApprovalRequest removeAttachments(ScQtApprovalRequest approvalRequest) {
		List<String> toBeRemoveEPVAttachments = this.getManagedBean().getToBeRemoveEPVAttachments();
		List<ScQtEpvApprovalReqAttachment> existingAttachments = approvalRequest.getEpvApprovalReqAttachment();
		List<ScQtEpvApprovalReqAttachment> attachmentList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(toBeRemoveEPVAttachments)) {
			for (ScQtEpvApprovalReqAttachment att : approvalRequest.getEpvApprovalReqAttachment()) {
				if (toBeRemoveEPVAttachments.contains(att.getSubDocType())) {
					attachmentList.add(att);
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(attachmentList)) {
			existingAttachments.removeAll(attachmentList);
		}
		approvalRequest.setEpvApprovalReqAttachment(existingAttachments);
		return approvalRequest;
	}
	//#21692 #note-504 - End

	//#21692 #note-504 - Start
	/**
	 * To make sure Supplier Response Period's value stay as latest inputed by user.
	 * To avoid bug where the value will reset when panel refreshed due to change date condition
	 * or change decision.
	 */
	public void replaceResponsePeriodValue() {
		this.getManagedBean().getScQtApprovalRequestList().get(0).getResponsePeriod();
	}
	//#21692 #note-504 - End
}