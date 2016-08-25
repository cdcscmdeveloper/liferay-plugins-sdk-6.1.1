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
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.sc.committe.ICommitteSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.biz.service.sc.quationtender.IQTProposalSO;
import my.ep.biz.service.sc.workflowstatus.IScWorkFlowStatusSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTFieldConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.ProposalReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.dto.sc.workflowstatus.ScWorkFlowStatusReqDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sc.common.ScAttachment;
import my.ep.persistence.entity.sc.common.ScWorkFlowStatus;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalReqAttachement;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.QTDetailBackingBean;
import my.ep.web.backingbean.sc.quatationtender.QTDocumentsBackingBean;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sc.QTConstants;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.scheduler.helper.EpSchedulerHelper;
import my.ep.web.scheduler.helper.sc.ScSchedulerHelper;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.quartz.JobKey;
import org.quartz.SchedulerException;

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
		logger.info("handleFileUpload - START");
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
		logger.info("handleFileUpload - END");
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
		logger.info("handleOtherFileUpload - START");
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
			approvalReqAttachement.setDocNo(scQtApprovalDtls.getScQtApproval().getScQt().getQtNo());
			//scAttachment = approvalReqAttachement;
			uploadAttachment(event.getFile(), approvalReqAttachement);
			scQtApprovalDtls.setApprovalRequestAttachement(approvalReqAttachement);
		}
		logger.info("handleOtherFileUpload - END");
	}

	/**
	 * Delete Attachment.
	 */
	public void deleteUploadFile() {
		logger.info("deleteUploadFile - START");
		// getManagedBean().getScQtApproval().setScQtApprovalAttachments(null);
		getManagedBean().getAttachments().clear();
		logger.info("deleteUploadFile - END");
	}

	/**
	 * Delete Attachment for other approvals.
	 */
	public void deleteOthersApprovalFile(BaseEntity delEntity) {
		logger.info("deleteOthersApprovalFile - START");
		List<ScQtApprovalRequest> list = this.getManagedBean()
				.getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setApprovalRequestAttachement(null);
		}
		logger.info("deleteOthersApprovalFile - END");
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

	/*
	 * 
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

		if (SCProcessNameConst.APPROVE_BUDGET.equalsIgnoreCase(taskName)) {
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BUDGET;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BGT_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);

		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.BPK_APPROVE_QT_CHANGE
						.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagBpk());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);

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

			this.getManagedBean().setApprovalType(approvalCategoryCode);

		} else if (SCProcessNameConst.FC_APPROVE_QT_CHANGE
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagFinalization());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			currentStatus = retrieveStatusByTypeAndId(ParameterConst.DOCTYPE_PCB_APPROVAL,
					getManagedBean().getQt().getQtId());

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

		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagBpk());
			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_BPK;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_BPK_APPROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);

		} else if (SCProcessNameConst.APPROVE_COMMITTEE
				.equalsIgnoreCase(taskName)) {

			approvalCategoryCode = ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT;
			approvalRequests = getApprovalRequests(qtId, approvalCategoryCode,
					ParameterConst.DOCTYPE_COMMITTE_APROVAL, paramTypes, null);

			this.getManagedBean().setApprovalType(approvalCategoryCode);

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
			
			if (tabBeans.size() > 0) {
				getManagedBean().setSelectedTabBean(tabBeans.get(0));
			}
		} else if (SCProcessNameConst.APPROVE_PUBLICATION
				.equalsIgnoreCase(taskName)) {
			paramTypes = getRequestTypes(((SCQuotationTenderDataType) obj)
					.getXflagPublication());
			if (CollectionUtils.isNotEmpty(paramTypes)) {
				this.getManagedBean().setApprovalType(paramTypes.get(0));
			}
			
			approvalRequests = getApprovalRequests(qtId,
					ParameterConst.APPROVAL_CATEGORY_PTJ,
					ParameterConst.DOCTYPE_QUOTATION, paramTypes, null);
			
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
		if (SCProcessNameConst.SUPPLIER_ACCEPT_QT_CHANGE
				.equalsIgnoreCase(taskName)) {
			context.getApplication()
					.getNavigationHandler()
					.handleNavigation(context, null,
							"/xhtml/sc/quatationtender/Approver/supplierAccepetance.xhtml");
		} else {
			context.getApplication()
					.getNavigationHandler()
					.handleNavigation(context, null,
							"/xhtml/sc/quatationtender/Approver/approverMain.xhtml");
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

	/**
	 * Submit Approval Details. Event handle to submit data for bpm.
	 * 
	 * @param outCome
	 *            (Mandatory)
	 * @return the string(ACKNOWLEDGE_PAGE)
	 */
	public void approvalBpmTask(String action) {
		String showDialog = null;
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
		logger.info("Task Name : " + this.getManagedBean().getTaskName());
		if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(this.getManagedBean().getTaskName())) {
			boolean isRevised = false;
			boolean isApproved = false;
			boolean isRejected = false;
			for (ScQtApprovalRequest approvalRequest : this.getManagedBean().getScQtApprovalRequestList()) {
				String approverAction = approvalRequest.getApproverAction().getParameterCode();
				
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
				showDialog = "gpki_dlg.show()";
			} else if (isRejected) {
				action = BpmOutcomeConst.BPM_REJECT;
				showDialog = "rejectApprovalDlg.show()";
			}
			
		} else {
			if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
				showDialog = "reviceApprovalDlg.show()";
			} else if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(action)) {
				showDialog = "rejectApprovalDlg.show()";
			} else {
				if (ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL.equalsIgnoreCase(this
						.getManagedBean().getApprovalType())) {
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
							qtCommittee.setQtCommitteRemarks(this.getManagedBean()
									.getQtCommitteeRemarks());
							qtCommitees.add(qtCommittee);
						}
						reqDTO.getQtdetail().setScQtCommittees(qtCommitees);
					}
					ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,ICommitteSO.class);
					CommitteReqDTO committeReqDTO = new CommitteReqDTO();
					committeReqDTO.setScQt(reqDTO.getQtdetail());
					FacesScopes scope = new FacesScopes();
					committeReqDTO.setUser(scope.getUserSessionBean());
					committeReqDTO.setUserLocale(scope.getUserLocale());
					committeReqDTO.setSpecApprovalReq(true);
					committeSO.validateAppointCommitteDtls(committeReqDTO);
				}
				showDialog = "gpki_dlg.show()";
			}
			
			
		}
		
		if (StringUtils.isNotBlank(showDialog)) {
			reqDTO.setOutcome(action);
			detailSO.validateQtApprovalDtl(reqDTO);
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
	public String gpkiSignAction(PmDigiSign digiSign) {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean()
				.getAction(), digiSign);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getAction()),
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.DOCUMENT_TYPES,
																	ParameterConst.DOCTYPE_QUOTATION),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
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

			CommitteResDTO committeResDto = null;
			// QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)

					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				// setting the remarks to ScQtApprovalRemarks
				ScQtApproval approval = this.getManagedBean().getScQtApproval();

				updateApprovalRequest(this.getManagedBean()
						.getScQtApprovalRequestList(), outcome);
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
				committeReqDTO.setSaveFlow(true);
				committeReqDTO.setUser(new FacesScopes().getUserSessionBean());
				committeReqDTO.setToBeDeletedMembers(this.getManagedBean()
						.getToBeDelCommitteMemebers());
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				tenderResDTO.setSuccessful(committeResDto.isSuccessful());
				tenderResDTO.setQtDetail(qt);
			} else {
				boolean isFinalApproval = false;
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

						((SCQuotationTenderDataType) this.getManagedBean()
								.getPayLoad())
								.setBpkRevise(true);
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
					
					if (BpmOutcomeConst.BPM_REJECT.equalsIgnoreCase(outcome)) {
						qtReqDTO.setRejected(true);
					} else if (BpmOutcomeConst.BPM_APPROVE
							.equalsIgnoreCase(outcome)) {
						approvalTypes = approveApprovalRequests(qt, approvalRequests, true);
					}
				}
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
				qtReqDTO.setToBeRemoveRestrictedSupplierList(this
						.getManagedBean().getToBeRemoveRestrictedSupplierList());

				if (null != digiSign) {
					updatePmDigiSignData(digiSign, approvalDocType,
							qt.getQtId(), qt.getQtNo());
				}
				qtReqDTO.setDigiSignData(digiSign);
				tenderResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
				// tenderResDTO.setSuccessful(qtResDTO.isSuccessful());
				
				if (BpmOutcomeConst.BPM_APPROVE.equalsIgnoreCase(outcome)) {
					if (CollectionUtils.isNotEmpty(approvalTypes)) {
						if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_PUBLICATION_APPROVAL)) {
							Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
							if (!DateUtils.isDateGreater(qt.getPublishDate(), todayDate)) {
								try {
									log.info("call simple scheduler if QT is publishing today.");
									ScSchedulerHelper.getInstance().scheduleSimpleQTPublishingJob(qt);
								} catch (SchedulerException e) {
									e.printStackTrace();
									log.error(e.getMessage());
								}
							}
						} else if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_EXTEND_CLOSING_DATE)) {
							Date todayDate = DateUtils.setDefaultDateTime(DateUtils.getCurrentDateWithoutTime());
							if (DateUtils.isDateGreater(qt.getClosingDate(), todayDate)) {
								// To remove the existing QT closing job from scheduler if any
								JobKey jobKey = ScSchedulerHelper.getInstance().getQTClosingJobKey(qt);
								jobKey = EpSchedulerHelper.getJobKey(jobKey.getName(), jobKey.getGroup());
								if (jobKey != null) {
									EpSchedulerHelper.deleteJobAndAssociatedTriggers(jobKey);	
								}
							}
						} else if (approvalTypes.contains(ParameterConst.APPROVAL_REQUEST_EXTEND_PROPOSAL_VALIDITY_PERIOD)) {
							if (isFinalApproval) {
								new QTDocumentsBackingBean().reGenerateDocuments(
										qt.getQtId(), 
										QTFieldConst.FILED_QT_VALIDITY_PERIOD);
							}
						}				
					}	
				}
			}
		} finally {
		}
		return tenderResDTO;
	}

	/**
	 * Update Approval Revice from bpm.
	 * 
	 * @return the string
	 */
	public String updateReviceBpmTask() {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean()
				.getAction(), null);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getAction()),
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.DOCUMENT_TYPES,
																	ParameterConst.DOCTYPE_QUOTATION),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
	}
	
	/**
	 * Update Approval Reject from bpm.
	 * 
	 * @return the string
	 */
	public String updateRejectBpmTask() {
		QuationTenderResDTO tenderResDTO = updateBpmTask(this.getManagedBean()
				.getAction(), null);
		if (tenderResDTO.isSuccessful()) {
			ScQt qt = tenderResDTO.getQtDetail();
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_INFO,
									null,
									MessageUtils
											.getMessage(
													getMessgeConstant(this
															.getManagedBean()
															.getAction()),
													new String[] {
															getParameterDesc(
																	ParameterTypeConst.DOCUMENT_TYPES,
																	ParameterConst.DOCTYPE_QUOTATION),
															qt.getQtNo() },
													MessageUtils.BUNDLE_COMMON_MESSAGES,
													new FacesScopes()
															.getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		return null;
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

		tenderReqDTO.setQtId(qtId);
		tenderReqDTO.setDocType(docType);
		tenderReqDTO.setApprovalReqTypes(reqTypes);
		tenderReqDTO.setApprovalCategory(approvalCategory);
		tenderReqDTO.setApproverAction(approverAction);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retrieveApprovalsByQtIdNType(tenderReqDTO);

		this.getManagedBean().setQt(resDTO.getQtDetail());
		this.getManagedBean().setScQtApproval(resDTO.getQtApproval());
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

						throw new EpException(e.getMessage());
					}
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
				}

				approvalTypes.add(approvalType);
			}
		}
		
		return approvalTypes;
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

				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (approvalRequest.getApproverAction() != null
							&& ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
									.equalsIgnoreCase(approvalRequest
											.getApproverAction()
											.getParameterCode())) {
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
						if (QTConstants.QT_OPEN
								.equalsIgnoreCase(approvalRequest
										.getOriginalValue())) {
							scQt.setOpenFor(ParameterConst.OPEN_FOR_ALL);
						} else {
							scQt.setOpenFor(ParameterConst.OPEN_FOR_BUMI);
						}
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
	public String supplierAcceptance(String outCome) {

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
			if(BpmOutcomeConst.BPM_ACCEPT.equalsIgnoreCase(outCome)){
				// P1 - update proposal validity period if all suppliers approved.
				// P2 - update proposal validity period if one of suppliers approved.
				isAllAccepted = totalSuppliers > 0 && totalSuppliers == totalAccepted+1;
				if (isAllAccepted) {
					approveApprovalRequests(qt, approvalRequests, true);
				}
				
			}
			
			finalizeRemarks(approval.getScQtApprovalRemarks());
			approval.getScQtApprovalDtls().addAll(approvalRequests);
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
			qtReqDTO.setOutcome(outCome);

			QuationTenderResDTO tenderResDTO = (QuationTenderResDTO) detailSO
					.updateSupplierAction(qtReqDTO);

			qt = tenderResDTO.getQtDetail();
			if (tenderResDTO.isSuccessful()) {
				if (isAllAccepted) {
					new QTDocumentsBackingBean().reGenerateDocuments(
							qt.getQtId(), 
							QTFieldConst.FILED_QT_VALIDITY_PERIOD);
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
														getMessgeConstant(outCome),
														new String[] {
																getParameterDesc(
																		ParameterTypeConst.DOCUMENT_TYPES,
																		ParameterConst.DOCTYPE_QUOTATION),
																qt.getQtNo() },
														MessageUtils.BUNDLE_COMMON_MESSAGES,
														new FacesScopes()
																.getUserLocale())));
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}

		} finally {
		}
		return null;
	}

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
	
}