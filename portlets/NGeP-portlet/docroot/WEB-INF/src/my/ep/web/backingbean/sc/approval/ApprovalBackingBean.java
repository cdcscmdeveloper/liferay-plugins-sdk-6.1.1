package my.ep.web.backingbean.sc.approval;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
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
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
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
import my.ep.persistence.entity.sc.quationtender.ScQtCommitteRemarks;
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
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;

import com.liferay.portal.util.PortalUtil;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private FacesContext facesContext = FacesContext.getCurrentInstance();

	public ApprovalBackingBean() {

	}

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
	 * @param arrayList
	 */
	private void initializeRemark(List<ScQtCommittee> scQtCommittees) {
		FacesScopes facesScopes = new FacesScopes();
		IStatusSO iStatusSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IStatusSO.class);
		if (null != scQtCommittees && !scQtCommittees.isEmpty()) {
			for (ScQtCommittee committees : scQtCommittees) {
				this.getManagedBean().setQtCommitteeRemarks(
						committees.getQtCommitteRemarks());
				List<ScQtCommitteRemarks> scQtApprovalRemarks = this
						.getManagedBean().getQtCommitteeRemarks();
				if (null == scQtApprovalRemarks) {
					scQtApprovalRemarks = new ArrayList<ScQtCommitteRemarks>();
				}
				ScQtCommitteRemarks remark = new ScQtCommitteRemarks();
				setUserDetails(remark);
				remark.setCreatedUser(facesScopes.getUserSessionBean());
				remark.setScQtCommittee(committees);
				PmStatus status = new PmStatus();
				status.setStatusId(1L);
				StatusReqDTO statusReqDTO = new StatusReqDTO();
				if (status != null) {
					statusReqDTO.setStatusId(status.getStatusId());
					StatusResDTO statusResDTO = iStatusSO
							.retrieveStatusByStatusId(statusReqDTO);
					remark.setPmStatus(statusResDTO.getPmStatus());
				}
				this.getManagedBean().getQtCommitteeRemarks().add(remark);
			}
		}
	}

	/**
	 * @return the appCatList
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
				ApprovalCategory.FINALAIZATION_COMMITTEE_APPROVAL.value(),
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
			List<ScQtApprovalRequest> list = this.getManagedBean().getSelectedApprovalTabBean().getScQtApproval().getScQtApprovalDtls();
			System.out.println("Size : " + list.size());
			for (ScQtApprovalRequest approvalRequest : list) {
				scQtApprovalRequest = approvalRequest;
			}
			ScQtApprovalReqAttachement approvalReqAttachement = scQtApprovalRequest.getApprovalRequestAttachement();
			if (null == approvalReqAttachement) {
				approvalReqAttachement = new ScQtApprovalReqAttachement();
				approvalReqAttachement.setApprovalRequest(scQtApprovalRequest);
				scQtApprovalRequest.setApprovalRequestAttachement(approvalReqAttachement);
			} else {
				approvalReqAttachement.setApprovalRequest(scQtApprovalRequest);
				scQtApprovalRequest.setApprovalRequestAttachement(approvalReqAttachement);
			}
			approvalReqAttachement.setDocNo(String.valueOf(scQtApprovalRequest.getQtApprovalRequestId()));
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
		HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		this.getManagedBean().setOutComes(res.getAction());
		String taskName = res.getTaskName();
		Object obj = null;
		Long qtId = null;
		if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
			obj = (SCCommitteeDataType) res.getPojo();
			qtId = ((SCCommitteeDataType) obj).getQtId().longValue();
		} else {
			obj = (SCQuotationTenderDataType) res.getPojo();
			qtId = ((SCQuotationTenderDataType) obj).getQtId().longValue();
		}
		this.getManagedBean().setQuotationTenderDataType(obj);
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO qtReqDto = new QuationTenderReqDTO();
		qtReqDto.setQtId(qtId);
		qtReqDto.setDocType(QTConstants.DOCTYPE_QT);
		QuationTenderResDTO tenderResDTO = detailSO.retriveQTDetails(qtReqDto);
		ScQtApproval scQtApproval = null;
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		if (SCProcessNameConst.APPROVE_BUDGET.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(ApprovalCategory.BUDGET_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId, ParameterConst.APPROVAL_CATEGORY_BUDGET);
		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId, ParameterConst.APPROVAL_CATEGORY_BPK);
			scQtApproval.setApproverSettings(null);
			scQtApproval.setScQtApprovalRemarks(initializeRemarks(
					scQtApproval, scQtApproval.getScQtApprovalRemarks(),
					ScQtApprovalRemark.class));
			for (ScQtApprovalRequest approvalRequest : scQtApproval.getScQtApprovalDtls()) {
				if (approvalRequest.getDecessionType() == null) {
					approvalRequest.setDecessionType(new PmParameter());
				}
			}
		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId, ParameterConst.APPROVAL_CATEGORY_BPK);
			scQtApproval.setApproverSettings(null);
		} else if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)) {
			// QTCommitteBackingBean backingBean= new QTCommitteBackingBean();
			this.getManagedBean().setApprovalType(ApprovalCategory.COMMITTEE_APPROVAL.dispalyName());
			ICommitteSO committeSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION, ICommitteSO.class);
			CommitteReqDTO committeReqDTO = new CommitteReqDTO();
			ISearchBean<ScQtCommittee> commSearchBean = new SearchBean<ScQtCommittee>();
			commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scQt.qtId", SearchOperator.OPERATOR_EQUALS.value(), qtId));
			commSearchBean.getDataFilterBeans().add(new SearchFilterBean("scCommittee.approvalReq", SearchOperator.OPERATOR_EQUALS.value(), true));
			committeReqDTO.setCommSearchBean(commSearchBean);
			CommitteResDTO resDTO = committeSO.retriveQtCommitteDtls(committeReqDTO);
			ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO.getQtCommitteResultBean();
			this.getManagedBean().setQtCommittes(qtcomSearchBean);
			List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean
					.getResultList());
			initializeRemark(qtcomSearchBean.getResultList());
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
					ParameterConst.APPROVAL_CATEGORY_PTJ);
			scQtApproval.setScQtApprovalRemarks(initializeRemarks(
					scQtApproval, scQtApproval.getScQtApprovalRemarks(),
					ScQtApprovalRemark.class));
			if (null != getManagedBean().getQt().getClosingDate()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(getManagedBean().getQt().getClosingDate());
				cal.add(Calendar.DATE, getManagedBean().getQt()
						.getQtValidityPeriod());
				getManagedBean().setProposalValidityUntil(cal.getTime());
			}
		}
		this.getManagedBean().setScQtApproval(scQtApproval);
		FacesContext context = FacesContext.getCurrentInstance();
		context.getApplication()
				.getNavigationHandler()
				.handleNavigation(context, null,
						"/xhtml/sc/quatationtender/Approver/approverMain.xhtml");
	}

	public String updateBpmTask(String outcome) {
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctxToken);
		reqDTO.setTaskId(this.getManagedBean().getTaskId());
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		String taskName = res.getTaskName();
		try {
			ScQt qt = this.getManagedBean().getQt();
			qt.setScQtStatus(null);
			qt.setScQtBriefings(null);
			CommitteResDTO committeResDto = null;
			QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)) {

				List<TabBean> qtCommittes = this.getManagedBean()
						.getScCommitteeMemberTabList();
				List<ScQtCommittee> committees = null;
				if (null != qtCommittes && !qtCommittes.isEmpty()) {
					committees = new ArrayList<ScQtCommittee>();
					for (int i = 0; i < qtCommittes.size(); i++) {
						ScQtCommittee qtCommittee = qtCommittes.get(0)
								.getScQtCommittee();
						PmStatus committeStatus = new PmStatus();
						if (BpmOutcomeConst.BPM_REVISE
								.equalsIgnoreCase(outcome)) {
							committeStatus
									.setStatusId(QTStatusConst.CM_REVISION);
						} else {
							committeStatus
									.setStatusId(QTStatusConst.CM_COMPLETE);
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
				removeEmptyRemarks(qt);
				committeReqDTO.setScQt(qt);
				committeReqDTO.setUser(new FacesScopes().getUserSessionBean());
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else {
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				

				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);
				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				if (null != approval && null != approval.getQtApprovalId()) {

					approval.setApproverId(new FacesScopes()
							.getUserSessionBean());
					approval.setApproverActionDate(new Date());
					
					if (ParameterConst.APPROVAL_CATEGORY_BPK
							.equalsIgnoreCase(approval.getApprovalCategory()
									.getParameterCode())) {
						List<ScQtApprovalRequest> approvalRequests = approval
								.getScQtApprovalDtls();
						String action = constuctOutCome(approvalRequests, qt);
						if (BpmOutcomeConst.BPM_REVISE
								.equalsIgnoreCase(action)) {
							approval.setApproverAction(SourcingConstants.REVISE_ACTION);
							((SCQuotationTenderDataType)this.getManagedBean()
							.getQuotationTenderDataType()).setBpkRevise(true);
						}
					}
					finalizeRemarks(approval.getScQtApprovalRemarks());
					List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
					qtApproval.add(approval);
					qt.setScQtApprovals(qtApproval);
				}
				qt.setScQtCommittees(null);
				qtReqDTO.setPayLoad(this.getManagedBean()
						.getQuotationTenderDataType());
				qtReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
				qtReqDTO.setQtdetail(qt);
				qtReqDTO.setOutcome(outcome);
				qtResDTO = (QuationTenderResDTO) detailSO
						.updateBPMAction(qtReqDTO);
			}
			if ((null != committeResDto && committeResDto.isSuccessful())
					|| (null != qtResDTO && qtResDTO.isSuccessful())) {
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
				return CommonUIConst.ACKNOWLEDGE_PAGE;
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null,
							MessageUtils.getMessage(MessageConst.EP_MAJ_00001,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
			ex.printStackTrace();
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

	private ScQtApproval getApproval(Long qtId, String approvalType) {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		tenderReqDTO.setApprovalType(approvalType);
		tenderReqDTO.setQtId(qtId);
		IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
						IQTDetailSO.class);
		QuationTenderResDTO resDTO = detailSO
				.retriveQTApprovalDetails(tenderReqDTO);
		ScQtApproval approval = resDTO.getQtApproval();
		return approval;
	}

	public String viewApprovalAction() {
		return "/xhtml/sc/quatationtender/Approver/approverMain.xhtml?faces-redirect=true";
	}

	/*
	 * public void handleFileAttachment(FileUploadEvent event) throws
	 * FileNotFoundException, IOException { FacesScopes facesScopes = new
	 * FacesScopes(); if (event.getFile() != null) { ScQtApproval scQtApproval =
	 * this.getManagedBean().getScQtApproval(); ScQtApprovalAttachment
	 * attachment = getManagedBean()
	 * .getScQtApproval().getScQtApprovalAttachment(); if (null == attachment) {
	 * attachment = new ScQtApprovalAttachment();
	 * attachment.setScQtApproval(scQtApproval);
	 * attachment.setDocNo(scQtApproval.getScQt().getQtNo());
	 * attachment.setPrefixFileName(scQtApproval.getApprovalCategory()
	 * .getParameterCode()); }
	 * attachment.setUser(facesScopes.getUserSessionBean()); attachment
	 * .setChangedDate(new Timestamp(System.currentTimeMillis()));
	 * uploadAttachment(event.getFile(), attachment);
	 * scQtApproval.setScQtApprovalAttachment(attachment); } }
	 */

	/**
	 * @param qt
	 */
	private void removeEmptyRemarks(ScQt qt) {
		List<ScQtCommittee> qtcommittees = qt.getScQtCommittees();
		for (ScQtCommittee qtCommittee : qtcommittees) {
			List<ScQtCommitteRemarks> remarks = qtCommittee
					.getQtCommitteRemarks();
			for (int i = 0; i < remarks.size(); i++) {
				if (remarks.get(i).getRemark() == null
						|| "".equals(remarks.get(i).getRemark())) {
					remarks.remove(i);
				}
			}
		}
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
									.getDecessionType().getParameterCode())) {
						scQt.setQtType(SourcingConstants.QT_TYPE_OPEN);
						scQt.setScQtSuppliers(null);
					} else if (ParameterConst.RESTR_APPROVAL_DECISION_TYPE_REVISE
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PROPOSAL_VALIDITY_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						scQt.setQtValidityPeriod(Integer
								.parseInt(approvalRequest.getOriginalValue()));
						approvalRequest.setChangedValue(approvalRequest.getOriginalValue());
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_REDUCE_PUBLISH_PERIOD
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						scQt.setPublishPeriod(Integer.parseInt(approvalRequest
								.getOriginalValue()));
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						isRevise = true;
						break;
					}
				} else if (ParameterConst.APPROVAL_REQUEST_OPEN_FOR_BUMIPUTRA_ALL
						.equalsIgnoreCase(approvalType)) {
					if (ParameterConst.APPROVAL_DECISION_TYPE_REJECTED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
						scQt.setOpenFor(approvalRequest.getOriginalValue());
					} else if (ParameterConst.APPROVAL_DECISION_TYPE_REVISED
							.equalsIgnoreCase(approvalRequest
									.getDecessionType().getParameterCode())) {
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
	
	public void updatePmParameter(ScQtApprovalRequest requestdetails){
		if(null != requestdetails){
			IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
			ParameterReqDTO parameterReqDTO= new ParameterReqDTO();
			parameterReqDTO.setLanguageCode("en");
			parameterReqDTO.setParameterId(requestdetails.getDecessionType().getParameterId());
			requestdetails.setDecessionType(((ParameterResDTO)parameterSO.retrieveParameterById(parameterReqDTO)).getPmParameter());
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
}