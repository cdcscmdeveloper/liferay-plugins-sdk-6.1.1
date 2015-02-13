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
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.BpmOutcomeConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sc.QTStatusConst;
import my.ep.constant.sc.SourcingConstants;
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.awardsupplierdata.SCAwardSupplierDataType;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.evaluationdata.SCEvaluationDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScAttachment;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalReqAttachement;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRequest;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;
import my.ep.type.aut.SearchOperator;
import my.ep.type.sc.ApprovalCategory;
import my.ep.type.sc.CommitteType;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.type.sc.quationtender.ApprovalStatus;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private FacesContext facesContext = FacesContext.getCurrentInstance();

	private String textBeforeSign;
	private PmDigiSign pmDigiSign;

	public ApprovalBackingBean() {

	}

	// TODO Need to check why this code... vidya

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

	public SelectItem[] getApprovalCategories() {
		SelectItem[] selectItems = new SelectItem[5];
		// selectItems[0] = new
		// SelectItem(ApprovalCategory.BUDGET_APPROVAL.value(),
		// "Budget Approval");
		selectItems[0] = new SelectItem(ApprovalCategory.BPK_APPROVAL.value(),
				"BPK Approval");
		selectItems[1] = new SelectItem(
				ApprovalCategory.COMMITTEE_APPROVAL.value(),
				"Committee Appointment Approval");
		selectItems[2] = new SelectItem(ApprovalCategory.PTJ_APPROVAL.value(),
				"PTJ Approval");
		selectItems[3] = new SelectItem(
				ApprovalCategory.FINALIZATION_COMMITTEE_APPROVAL.value(),
				"Finalization Committee Approval");
		selectItems[4] = new SelectItem(
				ApprovalCategory.OTHER_APPROVAL.value(), "Others Approval");
		return selectItems;
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			ScQtApprovalAttachment attachment = scQtApproval
					.getScQtApprovalAttachment();
			if (null == attachment) {
				attachment = new ScQtApprovalAttachment();
				attachment.setScQtApproval(scQtApproval);
				scQtApproval.setScQtApprovalAttachment(attachment);
			} else {
				attachment.setScQtApproval(scQtApproval);
				scQtApproval.setScQtApprovalAttachment(attachment);
			}
			attachment.setDocNo(scQtApproval.getScQt().getQtNo());
			scAttachment = attachment;
			uploadAttachment(event.getFile(), scAttachment);
			getManagedBean().setScQtApproval(scQtApproval);
			attachments.add(attachment);
			getManagedBean().setAttachments(attachments);
		}
	}

	public void handleOtherFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		if (event.getFile() != null) {
			ScAttachment scAttachment = null;
			ScQtApprovalRequest scQtApprovalRequest = null;
			List<ScQtApprovalRequest> list = this.getManagedBean()
					.getSelectedApprovalTabBean().getScQtApproval()
					.getScQtApprovalDtls();
			System.out.println("Size : " + list.size());
			for (ScQtApprovalRequest approvalRequest : list) {
				scQtApprovalRequest = approvalRequest;
			}
			ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalRequest
					.getApprovalRequestAttachement();
			if (null == approvalReqAttachement) {
				approvalReqAttachement = new ScQtApprovalReqAttachement();
				approvalReqAttachement.setApprovalRequest(scQtApprovalRequest);
				scQtApprovalRequest
						.setApprovalRequestAttachement(approvalReqAttachement);
			} else {
				approvalReqAttachement.setApprovalRequest(scQtApprovalRequest);
				scQtApprovalRequest
						.setApprovalRequestAttachement(approvalReqAttachement);
			}
			approvalReqAttachement.setDocNo(String.valueOf(scQtApprovalRequest
					.getQtApprovalRequestId()));
			scAttachment = approvalReqAttachement;
			uploadAttachment(event.getFile(), scAttachment);
		}
	}

	public void deleteUploadFile() {
		getManagedBean().getScQtApproval().setScQtApprovalAttachment(null);
	}

	public void deleteFile() {
		TabBean bean = this.getManagedBean().getSelectedApprovalTabBean();
		this.getManagedBean().setScQtApproval(bean.getScQtApproval());
		getManagedBean().getScQtApproval().setScQtApprovalAttachment(null);
	}

	public void deleteOthersApprovalFile() {
		List<ScQtApprovalRequest> list = this.getManagedBean()
				.getSelectedApprovalTabBean().getScQtApproval()
				.getScQtApprovalDtls();
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setApprovalRequestAttachement(null);
		}
	}

	/**
	 * determineIcon for displaying image icon in pid
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

	public void preRenderView() {
		Object obj = getBpmTaskDetails(SCAwardSupplierDataType.class);
		String taskName = getManagedBean().getTaskName();
		
		Long qtId = null;
		if (obj instanceof SCCommitteeDataType) {
			qtId = ((SCCommitteeDataType) obj).getQtId().longValue();
		} else if (obj instanceof SCEvaluationDataType) {
			qtId = ((SCEvaluationDataType) obj).getQtId().longValue();
		} else {
			qtId = ((SCQuotationTenderDataType) obj).getQtId().longValue();
		}
		this.getManagedBean().setQuotationTenderDataType(obj);

		ScQtApproval scQtApproval = null;

		if (SCProcessNameConst.APPROVE_BUDGET.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BUDGET_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BUDGET,
					ParameterConst.DOCTYPE_BGT_APPROVAL);

		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BPK,
					ParameterConst.DOCTYPE_BPK_APPROVAL);
			scQtApproval.setApproverSettings(null);

			for (ScQtApprovalRequest approvalRequest : scQtApproval
					.getScQtApprovalDtls()) {
				if (approvalRequest.getApproverAction() == null) {
					approvalRequest.setApproverAction(new PmParameter());
				} else {
					approvalRequest.setApproverAction(new PmParameter());
				}
			}
		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BPK,
					ParameterConst.DOCTYPE_BPK_APPROVAL);
			scQtApproval.setApproverSettings(null);

		} else if (SCProcessNameConst.APPROVE_COMMITTEE
				.equalsIgnoreCase(taskName)
				|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
						.equalsIgnoreCase(taskName)) {

			// QTCommitteBackingBean backingBean= new QTCommitteBackingBean();
			this.getManagedBean().setApprovalType(
					ApprovalCategory.COMMITTEE_APPROVAL.dispalyName());

			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_COMMITTEEE_APOINTMENT,
					ParameterConst.DOCTYPE_COMMITTE_APROVAL);

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
					new SearchFilterBean("scCommittee.committeStatus.statusId",
							SearchOperator.OPERATOR_NOT_EQUALS.value(),
							QTStatusConst.CMT_COMPLETED));

			committeReqDTO.setCommSearchBean(commSearchBean);
			CommitteResDTO resDTO = committeSO
					.retriveQtCommitteDtls(committeReqDTO);
			ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO
					.getQtCommitteResultBean();
			this.getManagedBean().setQtCommittes(qtcomSearchBean);
			List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean
					.getResultList());
			// initializeRemark(qtcomSearchBean.getResultList());
			this.getManagedBean().getQt()
					.setScQtCommittees(qtcomSearchBean.getResultList());
			this.getManagedBean().setScCommitteeMemberTabList(tabBeans);
			MenuModel menuModel = new DefaultMenuModel();
			int zoneTabIndex = 0;
			for (TabBean tabBean : tabBeans) {
				MenuItem committeItem = new MenuItem();
				committeItem.setValue(tabBean.getTitleName());
				committeItem.setUpdate("@form");
				committeItem.setActionExpression(FacesScopes
						.createMethodExpression(facesContext,
								"#{qTDetailBB.committeTabChangeAction("
										+ zoneTabIndex + ")}", null,
								new Class[] { Integer.class }));
				menuModel.addMenuItem(committeItem);
				zoneTabIndex++;
			}
			getManagedBean().setMenuModel(menuModel);
			if (tabBeans.size() > 0) {
				getManagedBean().setSelectedTabBean(tabBeans.get(0));
			}
		} else if (SCProcessNameConst.APPROVE_PUBLICATION
				.equalsIgnoreCase(taskName)) {

			this.getManagedBean().setApprovalType("Publication Approval");
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_PTJ,
					ParameterConst.DOCTYPE_QUOTATION);
			Date clsDate = null;
			if (null != this.getManagedBean().getQt().getLatestBsvDate()) {

				clsDate = DateUtils.addDays(this.getManagedBean().getQt()
						.getLatestBsvDate(), this.getManagedBean().getQt()
						.getPublishPeriod());

			} else {
				clsDate = DateUtils.addDays(this.getManagedBean().getQt()
						.getPublishDate(), this.getManagedBean().getQt()
						.getPublishPeriod());
			}
			this.setDefaultDateTime(this.getManagedBean().getQt());
			getManagedBean().setProposalValidityUntil(
					DateUtils.addDays(clsDate, this.getManagedBean().getQt()
							.getQtValidityPeriod()));

		} else if (SCProcessNameConst.APPROVE_PCB.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.FINALIZATION_COMMITTEE_APPROVAL
							.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE,
					ParameterConst.DOCTYPE_PCB_APPROVAL);

		} else if (SCProcessNameConst.APPROVE_CANCELATION
				.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.FINALIZATION_COMMITTEE_APPROVAL
							.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_FINALIZATION_COMMITTEE,
					ParameterConst.DOCTYPE_PCB_APPROVAL);

		}

		scQtApproval
				.setScQtApprovalRemarks(initializeRemarks(scQtApproval, this
						.getManagedBean().getQt().getCurrentWorkFlowStatus(),
						scQtApproval.getScQtApprovalRemarks(),
						ScQtApprovalRemark.class));
		this.getManagedBean().setScQtApproval(scQtApproval);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication()
				.getNavigationHandler()
				.handleNavigation(context, null,
						"/xhtml/sc/quatationtender/Approver/approverMain.xhtml");
	}

	public void approvalBpmTask(String action) {
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderReqDTO reqDTO = new QuationTenderReqDTO();
		reqDTO.setQtdetail(this.getManagedBean().getQt());
		this.getManagedBean().setAction(action);
		if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
			List<ScQtApprovalRemark> remarks = this.getManagedBean()
					.getScQtApproval().getScQtApprovalRemarks();
			if (null != remarks) {
				for (int i = 0; i < remarks.size(); i++) {
					String userRemark = remarks.get(i).getRemark();
					if (null == userRemark || "".equals(userRemark)) {
						throw new EpException(MessageConst.ERROR_100022,
								new String[] { "Remark" });
					}
				}
			}
			RequestContext.getCurrentInstance().execute(
					"reviceApprovalDlg.show()");
		} else {
			if ("Publication Approval".equalsIgnoreCase(this.getManagedBean()
					.getApprovalType())) {

				this.setDefaultDateTime(reqDTO.getQtdetail());
				detailSO.validateScheduleDetails(reqDTO);
			}
			if (SCProcessNameConst.BPK_APPROVAL_PROCESS_NAME.equalsIgnoreCase(this.getManagedBean()
					.getApprovalType())) {
				List<ScQtApprovalRemark> remarks = this.getManagedBean()
						.getScQtApproval().getScQtApprovalRemarks();
				if (null != remarks) {
					for (int i = 0; i < remarks.size(); i++) {
						String userRemark = remarks.get(i).getRemark();
						if (null == userRemark || "".equals(userRemark)) {
							throw new EpException(MessageConst.ERROR_100022,
									new String[] { "Remark" });
						}
					}
				}
			}
			RequestContext.getCurrentInstance().execute("gpki_dlg.show()");
		}
	}

	private void setDefaultDateTime(ScQt scQt) {
		// Publish/Closing/sample Date
		Date publishDate = scQt.getPublishDate();
		if (publishDate != null) {
			scQt.setPublishDate(DateUtils.setDefaultDateTime(publishDate));
		}

		Date closingDate = scQt.getClosingDate();
		if (closingDate != null) {
			scQt.setClosingDate(DateUtils.setDefaultDateTime(closingDate));
		}

		Date sampleClosingDate = scQt.getSampleClosingDate();
		if (sampleClosingDate != null) {
			scQt.setSampleClosingDate(DateUtils
					.setDefaultDateTime(sampleClosingDate));
		}
	}

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
	 * @param action
	 * @param object
	 * @return
	 */
	private QuationTenderResDTO updateBpmTask(String outcome,
			PmDigiSign digiSign) {
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctxToken);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		String taskName = res.getTaskName();
		QuationTenderResDTO tenderResDTO = new QuationTenderResDTO();
		try {
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtStatus(null);
			qt.setScQtBriefings(null);
			this.setDefaultDateTime(qt);

			CommitteResDTO committeResDto = null;
			// QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				// setting the remarks to ScQtApprovalRemarks
				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				if (null != approval && null != approval.getQtApprovalId()) {
					updateApprovalRequest(approval.getScQtApprovalDtls(),
							outcome);
					finalizeRemarks(approval.getScQtApprovalRemarks());
					List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
					qtApproval.add(approval);
					qt.setScQtApprovals(qtApproval);
				}
				List<TabBean> qtCommittes = this.getManagedBean()
						.getScCommitteeMemberTabList();
				List<ScQtCommittee> committees = null;
				if (null != qtCommittes && !qtCommittes.isEmpty()) {
					committees = new ArrayList<ScQtCommittee>();
					for (int i = 0; i < qtCommittes.size(); i++) {
						ScQtCommittee qtCommittee = qtCommittes.get(i)
								.getScQtCommittee();
						PmStatus committeStatus = new PmStatus();
						if (BpmOutcomeConst.BPM_REVISE
								.equalsIgnoreCase(outcome)) {
							committeStatus
									.setStatusId(QTStatusConst.CMT_PENDING_REVISION);
						} else {
							if (SCProcessNameConst.ASSIGN_REEVALUATE_COMMITTEE
									.equalsIgnoreCase(taskName)) {
								committeStatus
										.setStatusId(QTStatusConst.CMT_PENDING_APPROVAL);
							} else {
								committeStatus
										.setStatusId(QTStatusConst.CMT_COMPLETED);
							}
						}
						qtCommittee.getScCommittee().setCommitteStatus(
								committeStatus);
						qtCommittee.setQtCommitteRemarks(this.getManagedBean()
								.getQtCommitteeRemarks());
						committees.add(qtCommittee);
					}
					qt.setScQtCommittees(committees);
				}
				ICommitteSO committeSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								ICommitteSO.class);
				CommitteReqDTO committeReqDTO = new CommitteReqDTO();
				committeReqDTO.setToken(ctxToken);
				committeReqDTO.setTaskId(this.getManagedBean().getTaskId());
				committeReqDTO.setPayLoad(this.getManagedBean()
						.getQuotationTenderDataType());
				committeReqDTO.setOutcome(outcome);
				committeReqDTO.setPmUser(new FacesScopes().getUserSessionBean());
				committeReqDTO.setRoleCode(getUserRoleCode());
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
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
				tenderResDTO.setSuccessful(committeResDto.isSuccessful());
				tenderResDTO.setQtDetail(qt);
			} else {
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setApprovalFlow(true);
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				String approvalDocType = ParameterConst.DOCTYPE_QUOTATION;
				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				if (null != approval && null != approval.getQtApprovalId()) {

					if (ParameterConst.APPROVAL_CATEGORY_BPK
							.equalsIgnoreCase(approval.getApprovalCategory()
									.getParameterCode())) {
						approvalDocType = ParameterConst.DOCTYPE_BPK_APPROVAL;

						List<ScQtApprovalRequest> approvalRequests = approval
								.getScQtApprovalDtls();
						String action = constuctOutCome(approvalRequests, qt);
						if (BpmOutcomeConst.BPM_REVISE.equalsIgnoreCase(action)) {
							approval.setApproverAction(SourcingConstants.REVISE_ACTION);
							((SCQuotationTenderDataType) this.getManagedBean()
									.getQuotationTenderDataType())
									.setBpkRevise(true);
						}
					}
					updateApprovalRequest(approval.getScQtApprovalDtls(),
							outcome);
					finalizeRemarks(approval.getScQtApprovalRemarks());
					List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
					qtApproval.add(approval);
					qt.setScQtApprovals(qtApproval);
				}
				qt.setScQtCommittees(null);
				qtReqDTO.setPayLoad(this.getManagedBean()
						.getQuotationTenderDataType());
				FacesScopes facesScope = new FacesScopes();
				qtReqDTO.setDeskOfficer(facesScope.getUserSessionBean());
				qtReqDTO.setQtdetail(qt);
				qtReqDTO.setPmUser(facesScope.getUserSessionBean());
				qtReqDTO.setRoleCode(getUserRoleCode());
				qtReqDTO.setOutcome(outcome);

				if (null != digiSign) {
					updatePmDigiSignData(digiSign, approvalDocType,
							qt.getQtId(), qt.getQtNo());
				}
				qtReqDTO.setDigiSignData(digiSign);
				tenderResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
				// tenderResDTO.setSuccessful(qtResDTO.isSuccessful());
			}
		} finally {
		}
		return tenderResDTO;
	}

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

	public List<TabBean> constructCommitteTabBean(
			List<ScQtCommittee> qtCommittees) {
		List<TabBean> committeTabs = new ArrayList<TabBean>();
		for (ScQtCommittee scQtCommittee : qtCommittees) {
			if (!"BO".equalsIgnoreCase(scQtCommittee.getScCommittee()
					.getCommitteeType().getParameterCode())) {
				TabBean bean = new TabBean(
						CommitteType.getValue(scQtCommittee.getScCommittee()
								.getCommitteeType().getParameterCode()),
						getQtCommitteeTypeDtl(scQtCommittee.getScCommittee()
								.getCommitteeType().getParameterCode(),
								scQtCommittee.getScQt().getQtId()));
				committeTabs.add(bean);
			}
		}
		return committeTabs;
	}

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

	/*
	 * private ScCommittee getCommitteeTypeDtl(String type, Long id) {
	 * CommitteReqDTO reqDTO = new CommitteReqDTO(); ScCommittee committee =
	 * null; reqDTO.setCommitteeType(type); reqDTO.setQtId(id); CommitteResDTO
	 * resDTO = null;
	 * 
	 * try { ICommitteSO committeSO = BusinessDelegate.getInstance()
	 * .getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
	 * resDTO = committeSO .retrieveCommitteMembersByQtIdAndCommType(reqDTO);
	 * List<ScCommitteeMember> commMembers = resDTO
	 * .getCommMemberResultBean().getResultList();
	 * 
	 * if (!commMembers.isEmpty()) { committee =
	 * commMembers.get(0).getScCommittee();
	 * committee.setScCommitteeMembers(commMembers);
	 * getManagedBean().setScCommittee(committee); } } catch (Exception ex) {
	 * ex.printStackTrace(); } return committee; }
	 */

	private ScQtApproval getApproval(Long qtId, String approvalType,
			String docType) {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setApprovalType(approvalType);
		tenderReqDTO.setQtId(qtId);
		tenderReqDTO.setDocType(docType);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQTApprovalDetails(tenderReqDTO);
		ScQtApproval approval = resDTO.getQtApproval();
		this.getManagedBean().setQt(resDTO.getQtDetail());
		return approval;
	}

	public String viewApprovalAction() {
		return "/xhtml/sc/quatationtender/Approver/approverMain.xhtml?faces-redirect=true";
	}

	public void roleChangeListner(ScCommitteeMember member) {
		List<ScCommitteeMember> members = this.getManagedBean()
				.getSelectedTabBean().getScQtCommittee().getScCommittee()
				.getScCommitteeMembers();
		for (ScCommitteeMember committeeMember : members) {
			if (committeeMember.getIcPassport().equalsIgnoreCase(
					member.getIcPassport())) {
				List<PmParameterDesc> roles = getManagedBean().getRoleTypes();
				for (PmParameterDesc parameter : roles) {
					if (parameter.getPmParameter().getParameterId()
							.equals(member.getMemberRole().getParameterId())) {
						member.setMemberRole(parameter.getPmParameter());
						break;
					}
				}
			}
		}
	}

	private String constuctOutCome(List<ScQtApprovalRequest> approvalRequests,
			ScQt scQt) {
		if (null != approvalRequests) {
			boolean isRevise = false;
			for (int i = 0; i < approvalRequests.size(); i++) {
				ScQtApprovalRequest approvalRequest = approvalRequests.get(i);
				String approvalType = approvalRequest.getApprovalType()
						.getParameterCode();
				if (ParameterConst.APPROVAL_REQUEST_RESTRICTED_TENDER
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.RESTR_APPROVAL_DECISION_TYPE_PROCEED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						scQt.setQtType(SourcingConstants.QT_TYPE_OPEN);
						scQt.setScQtSuppliers(null);
					} else if (ParameterConst.RESTR_APPROVAL_DECISION_TYPE_REVISE
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PROPOSAL_VALIDITY_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						scQt.setQtValidityPeriod(Integer
								.parseInt(approvalRequest.getOriginalValue()));
						approvalRequest.setChangedValue(approvalRequest
								.getOriginalValue());
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						scQt.setPublishPeriod(Integer.parseInt(approvalRequest
								.getOriginalValue()));
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_OPEN_FOR_BUMIPUTRA_ALL
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						//scQt.setOpenFor(approvalRequest.getOriginalValue());
						if ("Open".equalsIgnoreCase(approvalRequest.getOriginalValue())) {
							scQt.setOpenFor("A");
						} else {
							scQt.setOpenFor("B");
						}
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getApproverAction().getParameterCode())) {
						isRevise = true;
						break;
					}
				}
			}
			if (isRevise) {
				return BpmOutcomeConst.BPM_REVISE;
			}
		}
		return BpmOutcomeConst.BPM_APPROVE;
	}

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

	public String getTextBeforeSign() {
		return textBeforeSign;
	}

	public void setTextBeforeSign(String textBeforeSign) {
		StringBuilder sb = new StringBuilder();
		sb.append(getManagedBean().getQt().getQtNo());
		sb.append(new FacesScopes().getUserSessionBean().getUserId());
		this.textBeforeSign = sb.toString();
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	private void updateApprovalRequest(
			List<ScQtApprovalRequest> approvalRequests, String outCome) {

		PmParameter parameter = null;
		if (!"SUBMIT".equalsIgnoreCase(outCome)) {
			parameter = getParameterByTypeAndCode(
					ParameterTypeConst.APPROVAL_DECISION_TYPE, ApprovalStatus
							.valueOf(outCome).getApprovalStatus());
		}
		if (null != approvalRequests && !approvalRequests.isEmpty()) {
			for (ScQtApprovalRequest approvalRequest : approvalRequests) {
				if (null != parameter) {
					approvalRequest.setApproverAction(parameter);
				}
				approvalRequest.setApproverActionDate(new Date());
				approvalRequest.setApproverId(new FacesScopes()
						.getUserSessionBean());
			}
		}

	}

}