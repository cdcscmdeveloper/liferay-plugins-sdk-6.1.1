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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
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
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.BpmConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
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
public class ApprovalBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

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
		if (event.getFile() != null) {
			ScQtApprovalRequest scQtApprovalDtls = this.getManagedBean().getScQtApprovalDtls();
			ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalDtls.getApprovalRequestAttachement();
			PmParameter approvalType = scQtApprovalDtls.getApprovalType(); //#25077
			
			if (null == approvalReqAttachement) {
				approvalReqAttachement = new ScQtApprovalReqAttachement();
				approvalReqAttachement.setApprovalRequest(scQtApprovalDtls);
			}
			
			approvalReqAttachement.setDocNo(this.getManagedBean().getQt().getQtNo()); // #34694,#25077

			uploadAttachment(event.getFile(), approvalReqAttachement);
			scQtApprovalDtls.setApprovalRequestAttachement(approvalReqAttachement);
			
			// #25077 - Start
			if (approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
				this.getManagedBean().setScQtApprovalReqAttachementICT(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(false);
			}
			
			if (approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
				this.getManagedBean().setScQtApprovalReqAttachementMITI(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(false);
			}
			// #25077 - End
		}
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

	// #25077 -start-
	/**
	 * Delete Attachment for other approvals ICT.
	 */
	public void deleteOthersApprovalFileICT(BaseEntity delEntity) {
		List<ScQtApprovalRequest> list = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
				list.get(i).setApprovalRequestAttachement(null);

				this.getManagedBean().setScQtApprovalReqAttachementICT(null);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(true);
			}
		}
	}
	// #25077 -end-
	
	// #25077 -start-
	/**
	 * Delete Attachment for other approvals MITI.
	 */
	public void deleteOthersApprovalFileMITI(BaseEntity delEntity) {
		List<ScQtApprovalRequest> list = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
				list.get(i).setApprovalRequestAttachement(null);
				
				this.getManagedBean().setScQtApprovalReqAttachementMITI(null);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(true);
			}
		}
	}
	// #25077 -end-

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

	
	/**
	 * preRenderView(). user click on task list it will Render Approvals screens
	 * for(Committee/BPK/Finalization/Publication).
	 */
	public void preRenderView() {
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

		} else if (SCProcessNameConst.FC_APPROVE_QT_CHANGE
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			currentStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_PCB_APPROVAL,
					getManagedBean().getQt().getQtId());

			docType = ParameterConst.DOCTYPE_PCB_APPROVAL; //#25692#128

			this.getManagedBean().setApprovalType(approvalCategoryCode);

		} else if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE
						.equalsIgnoreCase(taskName)) {
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
						approvalRequests.add(approvalRequest);;
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
			paramTypes = Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST);// 25037 #372 #32615
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
				
				if(approvalRequests==null || approvalRequests.isEmpty()){
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
		}
		
		if (currentStatus == null) {
			currentStatus = getManagedBean().getQt().getCurrentWorkFlowStatus();
		}

		initializeRemarks(this.getManagedBean().getScQtApproval(), 
				currentStatus, 
				getManagedBean().getScQtApproval().getScQtApprovalRemarks(),
				ScQtApprovalRemark.class);
		// this.getManagedBean().setScQtApproval(this.getManagedBean().getScQtApproval());
		this.getManagedBean().setScQtApprovalRequestList(approvalRequests);
		FacesContext context = FacesContext.getCurrentInstance();

		// ***25692 -start-
		boolean qtPageFirst = false;
		String page = "/xhtml/sc/quatationtender/Approver/approverMain.xhtml";
		
		if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE.equalsIgnoreCase(taskName)) {
			page = "/xhtml/sc/quatationtender/Approver/supplierAccepetance.xhtml";
			qtPageFirst = false;
		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName) 
				|| SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)) {
			qtPageFirst = true;
		}

		if (qtPageFirst) {
			String pageAppr = "/xhtml/sc/quatationtender/Approver/approverPanel.xhtml";
			QTDetailBackingBean qtDetailBB = new QTDetailBackingBean();
			
			page = qtDetailBB.editQuationTenderDetails(getManagedBean().getQt(), false);
			
			qtDetailBB.getManagedBean().setAdditionalPages(null);
			qtDetailBB.getManagedBean().setUserTasks(null);
		    
			Long statusId = qtDetailBB.getManagedBean().getQt().getQtStatus().getStatusId();
			boolean isQTStatusScheduleDocAndAfter = qtDetailBB.isQTStatusGreaterThanOrEquals(statusId,
							QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
			boolean isQTStatusBtwScheduleDocAndPendingPublication = qtDetailBB.isQTStatusInBetween(statusId,
							QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
			boolean isQTStatusSCApprovalAndBefore = qtDetailBB.isQTStatusLessThanOrEquals(statusId,
							QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL);
			boolean isCmtStatusCompletedAndBefore = qtDetailBB.isStatusLessThanOrEquals(statusId,
							QTStatusConst.CMT_COMPLETED, QTStatusConst.CMT_ORDERED_STATUSES);

			if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
				qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.BPKApproval.toString(), pageAppr);
				qtDetailBB.getManagedBean().addUserTasks(UserTasks.BPKApproval);

				ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(
						ParameterConst.DOCTYPE_QUOTATION, qtDetailBB.getManagedBean().getQt().getQtId());

				if (null != workFlowStatus) {
					qtDetailBB.getManagedBean().getQt().setQtStatus(workFlowStatus.getPmStatus());
					
					statusId = qtDetailBB.getManagedBean().getQt().getQtStatus().getStatusId();
					
					isQTStatusScheduleDocAndAfter = qtDetailBB.isQTStatusGreaterThanOrEquals(statusId,
									QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);
					isQTStatusBtwScheduleDocAndPendingPublication = qtDetailBB.isQTStatusInBetween(statusId,
									QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT, QTStatusConst.QT_PENDING_RESCHEDULE_PUBLICATION);
					isQTStatusSCApprovalAndBefore = qtDetailBB.isQTStatusLessThanOrEquals(statusId,
									QTStatusConst.QT_PENDING_SC_COMMITTEE_APPROVAL);
					isCmtStatusCompletedAndBefore = qtDetailBB.isStatusLessThanOrEquals(statusId,
									QTStatusConst.CMT_COMPLETED, QTStatusConst.CMT_ORDERED_STATUSES);
				}

				if (isQTStatusScheduleDocAndAfter) {
					qtDetailBB.getManagedBean().setActiveIndex(9);
				}

				if (isQTStatusBtwScheduleDocAndPendingPublication) {
					qtDetailBB.getManagedBean().setActiveIndex(9);
				}
				
				if (isQTStatusSCApprovalAndBefore) {
					qtDetailBB.getManagedBean().setActiveIndex(5);
				}

				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(
						qtDetailBB.getManagedBean().getQt().getQtId(),
						docType == null ? ParameterConst.DOCTYPE_QUOTATION : ParameterConst.DOCTYPE_BPK_APPROVAL);
				
				this.getManagedBean().setSubStatusId(sts.getStatusId());

			} else if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
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
				
				if (this.getManagedBean().getQt() != null) {
					cmts = this.getManagedBean().getQt().getScQtCommittees();
				}
				
				boolean isEcOcTecFec = false;
				
				for (int i = 0; cmts != null && i < cmts.size(); i++) {
					if (CommitteeTypeIsEcOcTecFec(cmts.get(i).getScCommittee())) {
						isEcOcTecFec = true;
						
						break;
					}
				}

				// 25037 - start
				boolean isFullTablist = false;
				// #25692 - start
				ScWorkFlowStatus workFlowStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION,
						qtDetailBB.getManagedBean().getQt().getQtId());
				if (null != workFlowStatus) {
					qtDetailBB.getManagedBean().getQt().setQtStatus(workFlowStatus.getPmStatus());
					Long qtStatusId = workFlowStatus.getPmStatus().getStatusId();
					isFullTablist = qtDetailBB.isQTStatusGreaterThanOrEquals(qtStatusId,
							QTStatusConst.QT_PENDING_SCHEDULE_DOCUMENT);

				}
				// #25692 - End
				if (isEcOcTecFec || isFullTablist) {
				// 25037 - end
					qtDetailBB.getManagedBean().addAdditionalPages(
									UserTasks.CommitteeAppointmentApprovalEcOcTecFec.toString(), pageAppr);
					qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApprovalEcOcTecFec);
					qtDetailBB.getManagedBean().setActiveIndex(9);
					
					if (isQTStatusScheduleDocAndAfter) {
						qtDetailBB.getManagedBean().setActiveIndex(9);
					}
					
					if (isQTStatusBtwScheduleDocAndPendingPublication) {
						qtDetailBB.getManagedBean().setActiveIndex(9);
					}
					
					if (isQTStatusSCApprovalAndBefore) {
						qtDetailBB.getManagedBean().setActiveIndex(5);
					}
					
					if (isCmtStatusCompletedAndBefore) {
						qtDetailBB.getManagedBean().setActiveIndex(9);
					}
				} else {
					qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(), pageAppr);
					qtDetailBB.getManagedBean().addUserTasks(UserTasks.CommitteeAppointmentApproval);
					qtDetailBB.getManagedBean().setActiveIndex(5);
				}

				PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(
						qtDetailBB.getManagedBean().getQt().getQtId(),
						docType == null ? ParameterConst.DOCTYPE_QUOTATION : ParameterConst.DOCTYPE_COMMITTE_APROVAL);
				
				this.getManagedBean().setSubStatusId(sts.getStatusId());

			} else if (SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)) {
				qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.PublicationApproval.toString(), pageAppr);
				qtDetailBB.getManagedBean().addUserTasks(UserTasks.PublicationApproval);
				qtDetailBB.getManagedBean().setActiveIndex(9);
				qtDetailBB.getManagedBean().setFirstPagePA(qtPageFirst); //#25077
			} else {
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
			
			/* ***#25692#111 - start */
			PmStatus sts = getQTDetailSO().retrieveActiveStatusByDoctype(
					qtDetailBB.getManagedBean().getQt().getQtId(),
					docType == null ? ParameterConst.DOCTYPE_QUOTATION : docType);
			
			if (sts != null) {
				qtDetailBB.getManagedBean().setCurrentStatusId(sts.getStatusId());
			}
			/* ***#25692#111 - end */			
			
			boolean readOnlyOri = qtDetailBB.getManagedBean().isReadOnly();
			
			this.getManagedBean().copyBPMTaskDetails(qtDetailBB.getManagedBean());
			
			qtDetailBB.getManagedBean().setReadOnly(readOnlyOri);
			
			ScQt mb1 = this.getManagedBean().getQt();
			ScQt mb2 = qtDetailBB.getManagedBean().getQt();
			ScQtApproval mbAppr1 = this.getManagedBean().getScQtApproval();
			ScQtApproval mbAppr2 = qtDetailBB.getManagedBean().getScQtApproval();
			
			if (page.equalsIgnoreCase(QTConstants.MAINTANANCE_PAGE)) {
				page = QTConstants.MAINTANANCE_PAGE_FULL;				
			}
		}

		context.getApplication().getNavigationHandler().handleNavigation(context, null, page);
		// ***25692 -end-
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
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtdetail(this.getManagedBean().getQt());
		reqDTO.setApproval(this.getManagedBean().getScQtApproval());
		reqDTO.setApprovalRequests(this.getManagedBean()
						.getScQtApprovalRequestList());
		reqDTO.setUserLocale(new FacesScopes().getUserLocale());
		this.getManagedBean().setAction(action);
		String taskName = this.getManagedBean().getTaskName();

		if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
			boolean isRevised = false;
			boolean isApproved = false;
			boolean isRejected = false;
			for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
				String approverAction = approvalRequest.getApproverAction().getParameterCode();
				
				// #25692-start
				if (null == approverAction) {
					String approverActionmsg = ResourceBundleUtils.getMessage("lbl.sc.decision",
							ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
					
					this.getManagedBean().setActiveIndex(9);
					
					throw new EpException(approverActionmsg);
				} 
				// #25692-End
				
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

				// #25638 -start-
				gpkiSignAction(null);
				// #25638 -end-
			} else if (isRejected) {
				action = BpmOutcomeConst.BPM_REJECT;
				showDialog = "rejectApprovalDlg.show()";
			}
			
		} else {
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				showDialog = "reviceApprovalDlg.show()";

				// #34694, #32376, #25077 - Start
				if (SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
					showDialog = "reviceApprovalDlgTemp.show()";
				}
				// #34694, #32376, #25077 - End
			} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(action)) {
				showDialog = "rejectApprovalDlg.show()";
			} else {
				if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL.equalsIgnoreCase(this
						.getManagedBean().getApprovalType())) {
					// #29762, #25692 -start-
					if (this.getManagedBean().getQt().isSampleReq()) {
						checkSampleRequiredChecking(this.getManagedBean().getQt());
					}
					// #29762, #25692 -end-
					
					detailSO.validatePublicationScheduleDetails(reqDTO);
				} else if(ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT.equalsIgnoreCase(this
						.getManagedBean().getApprovalType())){
					
					List<TabBean> qtCommittes = this.getManagedBean().getScCommitteeMemberTabList();
					if (CollectionUtils.isNotEmpty(qtCommittes)) {
						List<ScQtCommittee> qtCommitees = new ArrayList<ScQtCommittee>(
								qtCommittes.size());
						for (int i = 0; i < qtCommittes.size(); i++) {
							ScQtCommittee qtCommittee = qtCommittes.get(i)
									.getScQtCommittee();

							if (null != qtCommittee) { // #29724, #34056
								qtCommittee.setQtCommitteRemarks(this.getManagedBean().getQtCommitteeRemarks());
								
								qtCommitees.add(qtCommittee);
							} // #29724, #34056
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

				// #25426 -start-
				if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)
						&& BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(action)) {
					approveCancelQTwithDOASenariosExists(this.getManagedBean().getQt()); // #25426
				}
				// #25426 -end-
				
				if (SCProcessNameConst.APPROVE_CANCELATION.equalsIgnoreCase(taskName)
						|| SCProcessNameConst.FC_APPROVE_QT_CHANGE.equalsIgnoreCase(taskName)) {
					// #25638 -start-
					gpkiSignAction(null);
					// #25638 -end-

					// 15391 -start- // 25037 - start
				} else if (SCProcessNameConst.APPROVE_PUBLICATION.equalsIgnoreCase(taskName)) {
					isGpkiPopup = assertSpecificationUpdateInProgress();
					showDialog = isGpkiPopup ? "" : QTConstants.QT_GPKI_DLG_SHOW;// 25037
					// 15391 -end- // 25037 - end
				} else {
					showDialog = QTConstants.QT_GPKI_DLG_SHOW;// 25037

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
	// #25638 -start-
	public void gpkiSignAction(PmDigiSign digiSign) {
		// #25638 -end-
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean().getAction(), digiSign);

		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();

			if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL
					.equalsIgnoreCase(this.getManagedBean().getApprovalType())) {
				// #14398 - Start.
				if (QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL_RESCHEDULE.equals(this.getManagedBean()
						.getCurrentStatusId())) {
					successMessage(MessageConst.INFO_700069,
							getMessageCommon("lbl.sc.qt.public.approval.reschedule.approve.message"), qt.getQtNo(),
							Boolean.TRUE);
				} else {
					// #14398 - End.
					// #25638 -start-
					successMessage(MessageConst.INFO_700069, getMessageCommon("lbl.sc.qt.public.approval.approve.message"),
							qt.getQtNo(), Boolean.TRUE);
					// #25638 -end-
					// #14398 - Start.
				}
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
			else {
				log.info("Other Approval executed");

				// #25638 -start-
				successMessage(getMessgeConstant(this.getManagedBean().getAction()),
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
						qt.getQtNo(), Boolean.TRUE);
				// #25638 -end-
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
	private QuationTenderResDTO updateBpmTask(String outcome,
			PmDigiSign digiSign) {
		FacesScopes facesScopes = new FacesScopes();
		String taskName = this.getManagedBean().getTaskName();
		QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
		try {
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtStatus(null);
			qt.setScQtBriefings(null);
			
			// Start 9019 #PREPROD Remark null error
			boolean emptyRemarksExist = false;

			if (null != this.getManagedBean().getQt().getScQtRemarks()
					&& CollectionUtils.isNotEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
				for (int i = 0; i < this.getManagedBean().getQt().getScQtRemarks().size(); i++) {
					String userRemark = this.getManagedBean().getQt().getScQtRemarks().get(i).getRemark();
				
					if (null == userRemark || "".equals(userRemark)) {
						emptyRemarksExist = true;
					} else {
						emptyRemarksExist = false;
					}
				}
				if (emptyRemarksExist) {
					this.getManagedBean().getQt().setScQtRemarks(null);
				} else {
					this.getManagedBean().getQt().setScQtRemarks(qt.getScQtRemarks());
				}
			} else {
				if (CollectionUtils.isEmpty(this.getManagedBean().getQt().getScQtRemarks())) {
					this.getManagedBean().getQt().setScQtRemarks(null);
				}else{
					this.getManagedBean().getQt().setScQtRemarks(qt.getScQtRemarks());
				}
			}
			// End Reopen 9019

			CommitteResDTO committeResDto = null;

			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
				// setting the remarks to ScQtApprovalRemarks
				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				List<String> committeeTypes = new ArrayList<String>();

				updateApprovalRequest(this.getManagedBean()
						.getScQtApprovalRequestList(), outcome);
				finalizeRemarks(approval.getScQtApprovalRemarks());
				List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
				qtApproval.add(approval);

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

						if (null != qtCommittee) { // #29724, #34056
							if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome)) {
								qtCommittee.getScCommittee().setCommitteStatus(
										getPmStatusByStatusId(QTStatusConst.CMT_PENDING_REVISION));
							} else {
								qtCommittee.getScCommittee().setCommitteStatus(
										getPmStatusByStatusId(QTStatusConst.CMT_COMPLETED));
							}

							qtCommittee.setQtCommitteRemarks(this.getManagedBean().getQtCommitteeRemarks());
							
							qtCommitees.add(qtCommittee);
							
							committeeTypes.add(qtCommittee.getScCommittee().getCommitteeType().getParameterCode());
						} // #29724, #34056
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
				committeReqDTO.setCommitteeTypes(committeeTypes);
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				tenderResDTO.setSuccessful(committeResDto.isSuccessful());
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
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				String approvalDocType = ParameterConst.DOCTYPE_QUOTATION;
				// Bug #31759 - Start
				checkApprovalAndRefreshDetails (qt.getQtId(),taskName, this.getManagedBean().getScQtApproval());
				// Bug #31759 - End

				invokeRemarksWhileExecuteReviseFlow(outcome); // 25077, #34698

				ScQtApproval approval = this.getManagedBean().getScQtApproval();

				String approvalCategoryCode = approval.getApprovalCategory()
						.getParameterCode();
				qtReqDTO.setApprovalCategory(approvalCategoryCode);
				List<ScQtApprovalRequest> approvalRequests = this
						.getManagedBean().getScQtApprovalRequestList();
				if (ParameterConst.APPROVAL_CATEGORY_BPK
						.equalsIgnoreCase(approvalCategoryCode)) {
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
				} else if (ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE
						.equalsIgnoreCase(approvalCategoryCode)) {

					if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
						qtReqDTO.setRejected(true);
						revertToOldValues(qt, approvalRequests);
					} else if (BpmOutcomeConst.BPM_APPROVE
							.equalsIgnoreCase(outcome)) {
						isFinalApproval = dataType != null && CollectionUtils.isEmpty(dataType.getSupplierList());
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
				qtReqDTO.setToBeRemoveRestrictedSupplierList(this
						.getManagedBean().getToBeRemoveRestrictedSupplierList());

				if (null != digiSign) {
					updatePmDigiSignData(digiSign, approvalDocType,
							qt.getQtId(), qt.getQtNo());
				}
				qtReqDTO.setDigiSignData(digiSign);
				tenderResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
				
				if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
					if (CollectionUtils.isNotEmpty(approvalTypes)) {
						if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE)) {
							new QTDocumentsBackingBean().reGenerateDocuments(
									qt.getQtId(), 
									QTFieldConst.FIELD_QT_PUBLISH_PERIOD);
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
				}
			}
		} finally {
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
	// #25638 -start-
	public void updateReviceBpmTask() {
		// #25638 -end-
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean().getAction(), null);

		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			
			// #14398 - Start.
			IScWorkFlowStatusSO workFlowStatusSO;
			boolean isPendingReScheduleInvolved = false;
			
			try {
				workFlowStatusSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IScWorkFlowStatusSO.class);

				ScWorkFlowStatusReqDTO qtReqDTO = new ScWorkFlowStatusReqDTO();
				qtReqDTO.setDocId(qt.getQtId());
				
				List<Long> statusIds = new ArrayList<Long>();
				statusIds.add(QTStatusConst.QT_PENDING_SCHEDULE_REVISION);
				
				qtReqDTO.setStatusIds(statusIds);

				List<ScWorkFlowStatus> statusList = workFlowStatusSO.retrieveWorkFlowStatus(qtReqDTO)
						.getWorkFlowStatusList();
				
				if (CollectionUtils.isNotEmpty(statusList) && statusList.size() > 0) {
					isPendingReScheduleInvolved = true;
				}
			} finally {
				workFlowStatusSO = null;
			}

			if (isPendingReScheduleInvolved) {
				successMessage(MessageConst.INFO_60119, new String[] { qt.getQtNo() }, true);
			}
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
			// #14398 - end				 
				// #25638 -start-
				successMessage(getMessgeConstant(this.getManagedBean().getAction()),
						getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
						qt.getQtNo(), Boolean.TRUE);
				// #25638 -end-
			} // #14398
		}
	}
	
	/**
	 * Update Approval Reject from bpm.
	 * 
	 * @return the string
	 */
	// #25638 -start-
	public void updateRejectBpmTask() {
		// #25638 -end-
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean().getAction(), null);

		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();

			// #25638 -start-
			successMessage(getMessgeConstant(this.getManagedBean().getAction()),
					getParameterDesc(ParameterTypeConst.DOCUMENT_TYPES, ParameterConst.DOCTYPE_QUOTATION),
					qt.getQtNo(), Boolean.TRUE);
			// #25638 -end-
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
		// #25037-start #372 #32615 start
		Collections.sort(committeTabs, new Comparator<TabBean>() {
			List<String> sortOrder = Arrays.asList(ParameterConst.QT_COMMITTEES_ORDERED_LIST);

			@Override
			public int compare(TabBean o1, TabBean o2) {
				return Integer.valueOf(sortOrder.indexOf(o1.getTabType())).compareTo(
						Integer.valueOf(sortOrder.indexOf(o2.getTabType())));
			}
		});
		// #25037-end #372 #32615 end
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
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retrieveApprovalsByQtIdNType(tenderReqDTO);

		this.getManagedBean().setQt(resDTO.getQtDetail());
		this.getManagedBean().setScQtApproval(resDTO.getQtApproval());
		this.getManagedBean().setAmendMentBean(resDTO.getAmendMentBean());
		return resDTO.getApprovalRequests();
	}

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
private ScQt checkProcuremntCategoryMedicine(ScQt scQt){
	IQTSpecSO qtSpecSO =null;
try{
	 qtSpecSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTSpecSO.class);
	SpecificationReqDTO specReqDTO=new SpecificationReqDTO();
	specReqDTO.setScQt(scQt);
	SpecificationResDTO specResDTO=qtSpecSO.retriveSpecItemCount(specReqDTO);
	log.info("QTID:"+scQt.getQtId()+"itemCount"+specResDTO.getItemCount());
	boolean isMedicine=isMultipleProposalCategory(scQt, null);
	log.info("QTID:"+scQt.getQtId()+"isMedicine"+isMedicine);
	if(isMedicine || specResDTO.getItemCount()<=1){
		scQt.setEvaluateBy(SourcingConstants.EVALUATE_BY__ITEM);	
	}else{
		scQt.setEvaluateBy(SourcingConstants.EVALUATE_BY_BOTH_PACKAGE_ITEM);
	}
}finally{
	qtSpecSO =null;
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
				List<ScQtApprovalRemark> remarks = approval.getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						
						if (null == userRemark || "".equals(userRemark)) {
							
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR,null,
											MessageUtils.getMessage(MessageConst.ERROR_100022, new String[] {
													getMessageCommon("lbl.sc.remark")}, MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes().getUserLocale())));
							return;
						}
					}
				}
			}

			finalizeRemarks(approval.getScQtApprovalRemarks());
			approval.getScQtApprovalDtls().addAll(approvalRequests);
			List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
			qtApproval.add(approval);
			qt.setScQtApprovals(qtApproval);

			qt.setScQtCommittees(null);
			
			
			qtReqDTO.setDigiSignData(digiSign);
			qtReqDTO.setPayLoad(dataType);
			FacesScopes facesScope = new FacesScopes();
			qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
			qtReqDTO.setQtdetail(qt);
			qtReqDTO.setPmUser(facesScope.getUserSessionBean());
			qtReqDTO.setRoleCode(getUserRoleCode());
			qtReqDTO.setOutcome(outcome);

			QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO
					.updateSupplierAction(qtReqDTO);

			qt = tenderResDTO.getQtDetail();
			if (tenderResDTO.isSuccessful()) {
				if (isAllAccepted) {
					new QTDocumentsBackingBean().reGenerateDocuments(
							qt.getQtId(), 
							QTFieldConst.FIELD_QT_VALIDITY_PERIOD);
				}
				
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_INFO,
										null,
										MessageUtils
												.getMessage(
														getMessgeConstant(outcome),
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				// clear data
				super.renewSesionObject();
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication().getNavigationHandler()
						.handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
				
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
				&& smSupplier.getSmAppl().getSmCompanyBasic()
						.getSmCompanyAddresses().size() > 0) {
			List<SmCompanyAddress> companyAddress = smSupplier.getSmAppl()
					.getSmCompanyBasic().getSmCompanyAddresses();

			for (SmCompanyAddress compaAddress : companyAddress) {
				if (compaAddress.getAddressType().equalsIgnoreCase(
						AddressType.CONTACT_ADDRESS.value())) {
					countryCode = compaAddress.getSmAddress().getPmCountry()
							.getCountryCode();
					break;
				}
			}
		}
		sb.append(getManagedBean().getQt().getQtNo()).append("|")
				.append(facesScopes.getUserSessionBean().getIdentificationNo())
				.append("|").append(smSupplier.getRegNo()).append("|")
				.append(countryCode);
		orginalText = sb.toString();
		return orginalText;
	}

	// ***25692 -start-
	private boolean CommitteeTypeIsEcOcTecFec(ScCommittee cte) {
		if (cte.getCommitteeType() != null && cte.getCommitteeType().getParameterCode() != null) {
			return "EC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode())
					|| "OC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode())
					|| "TEC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode())
					|| "FEC".equalsIgnoreCase(cte.getCommitteeType().getParameterCode());
		}
		
		return false;
	}
	// ***25692 -end-
	
	// ***25692 -start-
	/**
	 * only if managedBean.readOnly and managedBean.approvalEditableSection()
	 * 
	 * @return
	 */
	public boolean isInApprovalEditableSection() {
		if (this.getManagedBean().isReadOnly()) {
			if (this.getManagedBean().isApprovalEditableSection()) {
				return true;
			}
		}
		
		return false;
	}
	// ***25692 -end-
	
	// #25692-start
	public void retriveCommiteeApprovalTabDetails() {
		List<Long> cmtStatus = new ArrayList<Long>(3);
		cmtStatus.add(QTStatusConst.CMT_PENDING_SUBMISSION);
		cmtStatus.add(QTStatusConst.CMT_COMPLETED);
		cmtStatus.add(QTStatusConst.CMT_PENDING_REVISION);
		
		ICommitteSO committeSO = BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
		CommitteReqDTO committeReqDTO = new CommitteReqDTO();
		
		ISearchBean<ScQtCommittee> commSearchBean = new SearchBean<ScQtCommittee>();
		commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQt.qtId",
						SearchOperator.OPERATOR_EQUALS.value(), this.getManagedBean().getQt().getQtId()));
		commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.approvalReq",
						SearchOperator.OPERATOR_EQUALS.value(), true));
		commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.committeeType.parameterCode",
						SearchOperator.OPERATOR_IN.value(), this.getManagedBean().getParamTypes()));
		commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.committeStatus.statusId",
						SearchOperator.OPERATOR_NOT_IN.value(), cmtStatus));

		committeReqDTO.setCommSearchBean(commSearchBean);
		
		CommitteResDTO resDTO = committeSO.retriveQtCommitteDtls(committeReqDTO);
		
		ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO.getQtCommitteResultBean();
		
		this.getManagedBean().setQtCommittes(qtcomSearchBean);
		
		List<TabBean> tabBeans = constructCommitteTabBean(
				qtcomSearchBean.getResultList(), this.getManagedBean().getQt().getQtId());
		
		// initializeRemark(qtcomSearchBean.getResultList());
		this.getManagedBean().getQt().setScQtCommittees(qtcomSearchBean.getResultList());
		this.getManagedBean().setScCommitteeMemberTabList(tabBeans);

		if (tabBeans.size() > 0) {
			getManagedBean().setSelectedTabBean(tabBeans.get(0));
		}
		
		this.getManagedBean().setPublicationApproval(null);
	}
	// #25692-End
	
	// #29762, #25692 -start-
	private void checkSampleRequiredChecking(ScQt scQt) {
		// Setting Sample Submission to qtDetails.
		List<ScQtSampleSubmission> sampleSubmissionList = null;
		
		if (scQt.isZonal()) {
			sampleSubmissionList = getManagedBean().getScQtSampleSubmissionList();
			
			if (sampleSubmissionList != null && !sampleSubmissionList.isEmpty()) {				
				for (ScQtSampleSubmission scQtSampleSubmission : sampleSubmissionList) {
					ScAddress scAddress = scQtSampleSubmission.getScAddress();

					if (null != scAddress) {
						if (scAddress.getPmDivision() != null && scAddress.getPmDivision().getDivisionId() == null) {
							scAddress.setPmDivision(null);
						}
					}
				}
			}
		} else {
			sampleSubmissionList = new ArrayList<ScQtSampleSubmission>();
			
			if (null != this.getManagedBean().getScQtSampleSubmission()) {
				ScAddress scAddress = this.getManagedBean().getScQtSampleSubmission().getScAddress();

				if (scAddress.getPmDivision() != null && scAddress.getPmDivision().getDivisionId() == null) {
					scAddress.setPmDivision(null);
				}

				sampleSubmissionList.add(this.getManagedBean().getScQtSampleSubmission());
			}
		}
	}
	// #29762, #25692 -end-
	
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
	public void handleHistoryOtherFileUpload(FileUploadEvent event) throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScQtApprovalRequest scQtApprovalHistoryDtls = this.getManagedBean().getHistoryApprovalRequests();
			PmParameter approvalType = scQtApprovalHistoryDtls.getApprovalType();
			ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalHistoryDtls.getApprovalRequestAttachement();
			
			if (null == approvalReqAttachement) {
				approvalReqAttachement = new ScQtApprovalReqAttachement();
				approvalReqAttachement.setApprovalRequest(scQtApprovalHistoryDtls);
			}
			
			approvalReqAttachement.setDocNo(this.getManagedBean().getQt().getQtNo()); // #34694,#25077
			
			uploadAttachment(event.getFile(), approvalReqAttachement);
			
			scQtApprovalHistoryDtls.setApprovalRequestAttachement(approvalReqAttachement);
			
			if (approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
				this.getManagedBean().setScQtApprovalReqAttachementHistoryICT(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(false);
			}

			if (approvalType.getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
				this.getManagedBean().setScQtApprovalReqAttachementHistoryMITI(approvalReqAttachement);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(false);
			}
		}
	}
	// #25077 -end-
	
	// #25077 -start-	
	/**
	 * Delete attachment for history others approval ICT.
	 */
	public void deleteHistoryOthersApprovalFileICT(BaseEntity delEntity) {
		List<ScQtApprovalRequest> list = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
				.getHistoryApprovalRequests();
		
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_ICT_PROJECTS)) {
				list.get(i).setApprovalRequestAttachement(null);
				this.getManagedBean().setScQtApprovalReqAttachementHistoryICT(null);
				this.getManagedBean().setFlagFileAttahmentDeletedICT(true);
			}
		}
	}
	// #25077 -end-
	
	// #25077 -start-
	/**
	 * Delete attachment for history others approval MITI.
	 */
	public void deleteHistoryOthersApprovalFileMITI(BaseEntity delEntity) {
		List<ScQtApprovalRequest> list = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval()
				.getHistoryApprovalRequests();
	
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getApprovalType().getParameterCode().equals(ParameterConst.APPROVAL_REQUEST_IMPORTED_GOODS)) {
				list.get(i).setApprovalRequestAttachement(null);
			
				this.getManagedBean().setScQtApprovalReqAttachementHistoryMITI(null);
				this.getManagedBean().setFlagFileAttahmentDeletedMITI(true);
			}
		}
	}
	// #25077 - End
	
	// #25077 -start-
	/**
	 * insert remarks while doing revise process flow
	 */
	// #34698 -start-
	private void invokeRemarksWhileExecuteReviseFlow(String outcome) {
		// #34698 -end-
		ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, this.getManagedBean()
				.getQt().getQtId());
		wfStatus = wfStatus == null ? new ScWorkFlowStatus() : wfStatus;

		// #34698 -start-
		if (wfStatus.getPmStatus() != null
				&& wfStatus.getPmStatus().getStatusId().equals(QTStatusConst.QT_PENDING_PUBLICATION_APPROVAL)
				&& BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(outcome) && !this.getManagedBean().isFirstPagePA()) {
			// #34698 -end-
			ScQtApproval publication = this.getManagedBean().getPublicationApproval();
			List<ScQtApprovalRemark> qtRemarks = initializeRemarks(publication, wfStatus,
					publication.getScQtApprovalRemarks(), ScQtApprovalRemark.class);

			for (ScQtApprovalRemark qtRemark : qtRemarks) {
				qtRemark.setEditable(false);
			}

			publication.setScQtApprovalRemarks(qtRemarks);

			this.getManagedBean().setScQtApproval(publication);
		}
	}
	// #25077 - End
	
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
	// 25037 - end

	// 25037 - start
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

		if (!Arrays.asList(taskNames).contains(taskName)) {
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
	// 25037 - end

	// 25037 - start
	private void assertBriefingSiteVisitIsComplete(Long qtId) {
		ScWorkFlowStatus wfStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_QUOTATION, qtId);
		wfStatus = wfStatus == null ? new ScWorkFlowStatus() : wfStatus;
		if (wfStatus.getPmStatus() != null) {
			Long currentStatusId = wfStatus.getPmStatus().getStatusId();
			if (this.isStatusGreaterThanOrEquals(currentStatusId, QTStatusConst.QT_PROPOSAL_PREPARATION,
					QTStatusConst.QT_ORDERED_STATUSES)) {
				this.getManagedBean().setOutComes(new String[] { BpmOutcomeConst.BPM_CANCEL }); //25037 - note 510
				FacesContext.getCurrentInstance().addMessage(
						null, new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
								getMessage("lbl.sc.doaRequestExpired", null)));
			}
		}
	}
	// 25037 - end

	// 25037 - start
	/**
	 * The pre-render method for the portlet maintenanceRequestApproval
	 * prerenderMaintenanceRequestApproval.xhtml
	 */
	public void preRenderGenericApproval() {
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
	// 25037 - end

	// 25037 - start
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
	// 25037 - end

	// 25037 - start
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
	// 25037 - end

	// 25037 - start
	/**
	 * Method called before submission, determine if gpki approval was required.
	 * 
	 * @param outcome
	 */
	public void preSubmitGenericApprovalTask(String outcome) {
		String taskName = this.getManagedBean().getTaskName();
		this.getManagedBean().setAction(outcome);
		if (taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_APPROVE)
				|| taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_REVISE)) {
			if (outcome.equalsIgnoreCase(BpmOutcomeConst.BPM_APPROVE)) {
				getManagedBean().setAutoUpdate(false);
				RequestContext.getCurrentInstance().execute(QTConstants.QT_GPKI_DLG_SHOW);// 25037
			}// #30477 - Start
			else if (outcome.equals(BpmOutcomeConst.BPM_SUBMIT)
					&& taskName.equalsIgnoreCase(SCProcessNameConst.GENERIC_APPROVAL_REVISE)) {
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
	// 25037 - end

	// 25037 - start
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
						qtDocTypeDesc, qtNo }, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, CommonUIConst.ACKNOWLEDGE_PAGE);
	}
	// 25037 - end

	// 25037 - start
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

	// 36671, 25037 - start
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
	// 36671, 25037 - end

	// 36671, 25037 - start
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
			qtDetailBB.getManagedBean().addAdditionalPages(UserTasks.CommitteeAppointmentApproval.toString(),
					QTConstants.QT_CMT_APPROVER_PANEL);
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
	// 36671, 25037 - end

	// 36671, 25037 - start
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

		scQt.setScQtCommittees(new ArrayList<ScQtCommittee>());
		
		for (TabBean tabBean : scQtCommittees) {
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
	// 36671, 25037 - end
	
	//#30477, #25426 - Start
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
	//#30477, #25426 -end-
	
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
}
