package my.ep.web.backingbean.sc.approval;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.user.IUserSO;
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
import my.ep.constants.SCProcessNameConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sc.committe.CommitteReqDTO;
import my.ep.dto.sc.committe.CommitteResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.jaxb.sc.committeedata.SCCommitteeDataType;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
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

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

import com.liferay.portal.util.PortalUtil;

/**
 * @author Viswanath
 * 
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends
		SourcingBaseBackingBean<QTDetailManagedBean> {

	private int activeApprovalTab;
	public String uploadAction;

	private String activeTabName;

	/**
	 * 
	 */
	public ApprovalBackingBean() {
		if (this.getManagedBean().getScQtApproval() == null) {
			this.getManagedBean().setScQtApproval(new ScQtApproval());
		}
		
		if (this.getManagedBean().getScQtApproval().getScQtApprovalRemarks() == null) {
			this.getManagedBean().getScQtApproval().setScQtApprovalRemarks(new  ArrayList<ScQtApprovalRemark>());
		}

		if (this.getManagedBean().getScQtApproval().getApprovalCategory() == null) {
			this.getManagedBean().getScQtApproval()
					.setApprovalCategory(new PmParameter());
		}

		initializeRemark(new ArrayList<ScQtApprovalRemark>());

		// getBudgetApproverName();
	}

	/**
	 * @param arrayList
	 */
	private void initializeRemark(ArrayList<ScQtApprovalRemark> scQtApprovalRemarks) {
		FacesScopes facesScopes = new FacesScopes();
		String screnName = null;
		if (scQtApprovalRemarks != null && !scQtApprovalRemarks.isEmpty()) {
			Long userId = scQtApprovalRemarks.get(0).getCreatedBy();
			IUserSO userSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO dto = new UserReqDTO();
			dto.setUserId(userId);
			UserResDTO userResDTO = userSO.retrieveUserDetails(dto);
			screnName = userResDTO.getPmUser().getLoginId();
		}
		if (scQtApprovalRemarks.size() == 0 || !compareUserName(screnName)) {
			ScQtApprovalRemark qtApprovalRemark = null;
			if (null == qtApprovalRemark) {
				qtApprovalRemark = new ScQtApprovalRemark();
			}
			qtApprovalRemark.setCreatedBy(facesScopes.getUserSessionBean().getUserId());
			qtApprovalRemark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			qtApprovalRemark.setChangedBy(qtApprovalRemark.getCreatedBy());
			qtApprovalRemark.setCreatedUsername(facesScopes.getUserSessionBean().getLoginId());
			getManagedBean().getScQtApproval().getScQtApprovalRemarks().add(0, qtApprovalRemark);
			getManagedBean().getScQtApproval().setScQtApprovalRemarks(scQtApprovalRemarks);
		} else {
			getManagedBean().getScQtApproval().setScQtApprovalRemarks(scQtApprovalRemarks);
		}
	}

	/**
	 * @return the appCatList
	 */
	public List<Long> getAppCatList() {
		List<Long> appCatList = new ArrayList<Long>();
		if (null != this.getManagedBean().getTabList()) {
			for (TabBean tabBean : this.getManagedBean().getTabList()) {
				appCatList.add(tabBean.getScQtApproval().getApprovalCategory()
						.getParameterId());
			}
		}
		return appCatList;
	}

	public SelectItem[] getApprovalCategories() {
		SelectItem[] selectItems = new SelectItem[6];

		selectItems[0] = new SelectItem(
				ApprovalCategory.BUDGET_APPROVAL.value(), "Budget Approval");
		selectItems[1] = new SelectItem(ApprovalCategory.BPK_APPROVAL.value(),
				"BPK Approval");
		selectItems[2] = new SelectItem(
				ApprovalCategory.COMMITTEE_APPROVAL.value(),
				"Committee Appointment Approval");
		selectItems[3] = new SelectItem(ApprovalCategory.PTJ_APPROVAL.value(),
				"PTJ Approval");
		selectItems[4] = new SelectItem(
				ApprovalCategory.FINALAIZATION_COMMITTEE_APPROVAL.value(),
				"Finalization Committee Approval");
		selectItems[5] = new SelectItem(
				ApprovalCategory.OTHER_APPROVAL.value(), "Others Approval");

		return selectItems;
	}

	public void approvalTabChanged(TabChangeEvent ev) {

		System.out.println(activeApprovalTab);
		/*
		 * for (TabBean tabBean : getManagedBean().getTabList()) { ScQtApproval
		 * scApproval = tabBean.getScQtApproval();
		 * System.out.println(tabBean.getTitleName() + " ::: " +
		 * scApproval.getApprovalLetterNo()); }
		 */

	}

	public void processChecked(AjaxBehaviorEvent e) {
		// doing some stuff here
		System.out.println("====enter process checked===="
				+ getManagedBean().getSelectedBoolean1());
		// String dueDate = (String) ((UIOutput)e.getSource()).getValue();
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setSelectedBoolean1(id);

	}

	public int getActiveApprovalTab() {
		return activeApprovalTab;
	}

	public void setActiveApprovalTab(int activeApprovalTab) {
		this.activeApprovalTab = activeApprovalTab;
	}

	public String getActiveTabName() {
		return activeTabName;
	}

	public void setActiveTabName(String activeTabName) {
		this.activeTabName = activeTabName;
	}

	public void handleFileUpload(FileUploadEvent event)
			throws FileNotFoundException, IOException {

		FacesScopes facesScopes = new FacesScopes();
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			ScQtApprovalAttachment attachment = getManagedBean()
					.getScQtApproval().getScQtApprovalAttachment();
			if (null == attachment) {
				attachment = new ScQtApprovalAttachment();
				attachment.setScQtApproval(scQtApproval);
				attachment.setDocNo(scQtApproval.getScQt().getQtNo());
				attachment.setPrefixFileName(scQtApproval.getApprovalCategory()
						.getParameterCode());
			}
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			attachments.add(attachment);
			getManagedBean().setAttachments(attachments);
		}
	}

	public void deleteUploadFile() {
		getManagedBean().getAttachments().clear();
	}

	/**
	 * @return the uploadAction
	 */
	public String getUploadAction() {
		return uploadAction;
	}

	/**
	 * @param uploadAction
	 *            the uploadAction to set
	 */
	public void setUploadAction(String uploadAction) {
		this.uploadAction = uploadAction;
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
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
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
		IQTDetailSO detailSO = BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_SELECTION, IQTDetailSO.class);
		QuationTenderReqDTO qtReqDto = new QuationTenderReqDTO();
		qtReqDto.setQtId(qtId);
		qtReqDto.setDocType(QTConstants.DOCTYPE_QT);
		QuationTenderResDTO tenderResDTO = detailSO.retriveQTDetails(qtReqDto);

		ScQtApproval scQtApproval = null;
		this.getManagedBean().setQt(tenderResDTO.getQtDetail());
		if (SCProcessNameConst.APPROVE_BUDGET.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BUDGET_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BUDGET);

		} else if (SCProcessNameConst.APPROVE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BPK);
			scQtApproval.setApproverSettings(null);
		} else if (SCProcessNameConst.REVISE_BPK.equalsIgnoreCase(taskName)) {
			this.getManagedBean().setApprovalType(
					ApprovalCategory.BPK_APPROVAL.dispalyName());
			scQtApproval = getApproval(qtId,
					ParameterConst.APPROVAL_CATEGORY_BPK);
			scQtApproval.setApproverSettings(null);
		} else if (SCProcessNameConst.APPROVE_COMMITTEE
				.equalsIgnoreCase(taskName)) {
			// QTCommitteBackingBean backingBean= new QTCommitteBackingBean();
			this.getManagedBean().setApprovalType(
					ApprovalCategory.COMMITTEE_APPROVAL.dispalyName());
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
			committeReqDTO.setCommSearchBean(commSearchBean);
			CommitteResDTO resDTO = committeSO
					.retriveQtCommitteDtls(committeReqDTO);
			ISearchResultBean<ScQtCommittee> qtcomSearchBean = resDTO
					.getQtCommitteResultBean();
			this.getManagedBean().setQtCommittes(qtcomSearchBean);
			List<TabBean> tabBeans = constructCommitteTabBean(qtcomSearchBean
					.getResultList());
			this.getManagedBean().setScCommitteeMemberTabList(tabBeans);

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
			CommitteResDTO committeResDto = null;
			QuationTenderResDTO qtResDTO = null;
			if (SCProcessNameConst.APPROVE_COMMITTEE.equalsIgnoreCase(taskName)
					|| SCProcessNameConst.REVISE_COMMITTEE
							.equalsIgnoreCase(taskName)) {
				ISearchResultBean<ScQtCommittee> qtCommittes = this
						.getManagedBean().getQtCommittes();
				if (null != qtCommittes && null != qtCommittes.getResultList()) {
					for (ScQtCommittee scQtCommittee : qtCommittes
							.getResultList()) {
						PmStatus committeStatus = new PmStatus();
						if (BpmOutcomeConst.BPM_REVISE
								.equalsIgnoreCase(outcome)) {
							committeStatus
									.setStatusId(QTStatusConst.CM_REVISION);
						} else {
							committeStatus
									.setStatusId(QTStatusConst.CM_COMPLETE);
						}
						scQtCommittee.getScCommittee().setCommitteStatus(
								committeStatus);
					}
					qt.setScQtCommittees(qtCommittes.getResultList());
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
				committeReqDTO.setScQt(qt);
				committeResDto = committeSO.updateBPMAction(committeReqDTO);
			} else {
				QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
				qtReqDTO.setToken(ctxToken);
				qtReqDTO.setTaskId(this.getManagedBean().getTaskId());
				qtReqDTO.setDocType(QTConstants.DOCTYPE_QT);
				qtReqDTO.setPayLoad(this.getManagedBean()
						.getQuotationTenderDataType());
				qtReqDTO.setOutcome(outcome);

				IQTDetailSO detailSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
								IQTDetailSO.class);

				ScQtApproval approval = this.getManagedBean().getScQtApproval();
				if (null != approval && null != approval.getQtApprovalId()) {
					approval.setApproverAction("A");
					List<ScQtApproval> qtApproval = new ArrayList<ScQtApproval>();
					qtApproval.add(approval);
					qt.setScQtApprovals(qtApproval);
				}

				qt.setScQtCommittees(null);
				qtReqDTO.setDeskOfficer(new FacesScopes().getUserSessionBean());
				qtReqDTO.setQtdetail(qt);
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
						getCommitteeTypeDtl(scQtCommittee.getScCommittee()
								.getCommitteeType().getParameterCode(),
								scQtCommittee.getScQt().getQtId()));
				committeTabs.add(bean);
			}
		}
		return committeTabs;
	}

	private ScCommittee getCommitteeTypeDtl(String type, Long id) {
		CommitteReqDTO reqDTO = new CommitteReqDTO();
		ScCommittee committee = null;
		reqDTO.setCommitteeType(type);
		reqDTO.setQtId(id);
		CommitteResDTO resDTO = null;

		try {
			ICommitteSO committeSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							ICommitteSO.class);
			resDTO = committeSO
					.retrieveCommitteMembersByQtIdAndCommType(reqDTO);
			List<ScCommitteeMember> commMembers = resDTO
					.getCommMemberResultBean().getResultList();

			if (!commMembers.isEmpty()) {
				committee = commMembers.get(0).getScCommittee();
				committee.setScCommitteeMembers(commMembers);
				getManagedBean().setScCommittee(committee);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return committee;
	}

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

	public void handleFileAttachment(FileUploadEvent event)
			throws FileNotFoundException, IOException {
		FacesScopes facesScopes = new FacesScopes();
		if (event.getFile() != null) {
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			ScQtApprovalAttachment attachment = getManagedBean()
					.getScQtApproval().getScQtApprovalAttachment();
			if (null == attachment) {
				attachment = new ScQtApprovalAttachment();
				attachment.setScQtApproval(scQtApproval);
				attachment.setDocNo(scQtApproval.getScQt().getQtNo());
				attachment.setPrefixFileName(scQtApproval.getApprovalCategory()
						.getParameterCode());
			}
			attachment.setUser(facesScopes.getUserSessionBean());
			attachment
					.setChangedDate(new Timestamp(System.currentTimeMillis()));
			uploadAttachment(event.getFile(), attachment);
			scQtApproval.setScQtApprovalAttachment(attachment);
		}
	}
}