package my.ep.web.backingbean.sc.approval;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.jaxb.sc.quotationtenderdata.SCQuotationTenderDataType;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalRemark;
import my.ep.type.sc.ApprovalCategory;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
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

		initializeRemark(new ArrayList<ScQtApprovalRemark>());

		// getBudgetApproverName();
	}

	/**
	 * @param arrayList
	 */
	private void initializeRemark(
			ArrayList<ScQtApprovalRemark> scQtApprovalRemarks) {
		FacesScopes facesScopes = new FacesScopes();

		if (scQtApprovalRemarks.size() == 0
				|| !compareUserName(scQtApprovalRemarks.get(0)
						.getCreatedUsername())) {
			ScQtApprovalRemark qtApprovalRemark = new ScQtApprovalRemark();

			qtApprovalRemark.setCreatedBy(facesScopes.getUserSessionBean()
					.getUserId());
			qtApprovalRemark.setCreatedDate(new Timestamp(System
					.currentTimeMillis()));
			qtApprovalRemark.setChangedBy(qtApprovalRemark.getCreatedBy());
			qtApprovalRemark.setChangedDate(qtApprovalRemark.getCreatedDate());
			qtApprovalRemark.setCreatedUsername(facesScopes
					.getUserSessionBean().getLoginId());

			getManagedBean().getScQtApproval().setScQtApprovalRemarks(
					scQtApprovalRemarks);
			getManagedBean().getScQtApproval().getScQtApprovalRemarks()
					.add(0, qtApprovalRemark);
		} else {
			getManagedBean().getScQtApproval().setScQtApprovalRemarks(
					scQtApprovalRemarks);
		}
	}

	/**
	 * @return the appCatList
	 */
	public List<String> getAppCatList() {
		List<String> appCatList = new ArrayList<String>();

		for (TabBean tabBean : this.getManagedBean().getTabList()) {
			appCatList.add(tabBean.getScQtApproval().getApprovalCategory());
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

	public void handleFileUpload(FileUploadEvent event) {

		FacesScopes facesScopes = new FacesScopes();
		List<ScQtApprovalAttachment> attachments = new ArrayList<ScQtApprovalAttachment>();
		if (event.getFile() != null) {
			ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
			ScQtApprovalAttachment attachment = getManagedBean()
					.getScQtApproval().getScQtApprovalAttachment();
			if (null == attachment) {
				attachment = new ScQtApprovalAttachment();
				attachment.setScQtApproval(scQtApproval);
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
	
	public void preRenderView(){
		HttpServletRequest servletRequest = PortalUtil
				.getHttpServletRequest(new FacesScopes().getRequest());
		HttpServletRequest originalServletRequest = PortalUtil
				.getOriginalServletRequest(servletRequest);
		String taskId = originalServletRequest.getParameter("taskId");
		this.getManagedBean().setTaskId(taskId);
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
				.getServiceObject(IBpmSO.class);
		// TODO
		FacesScopes facesScopes = new FacesScopes();
		String ctx = facesScopes.getBPMAuthenticationCtxToken();
		// String ctx = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(ctx);
		reqDTO.setTaskId(taskId);
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		SCQuotationTenderDataType obj = (SCQuotationTenderDataType)res.getPojo();
		this.getManagedBean().setQuotationTenderDataType(obj);
		this.getManagedBean().setOutComes(res.getAction());		
		FacesContext context= FacesContext.getCurrentInstance();
		context.getApplication().getNavigationHandler().handleNavigation(context, null, "/xhtml/sc/quatationtender/approval/approvalresults.xhtml");
	}
	
	public String updateBpmTask(String outcome) {

		QuationTenderReqDTO qtReqDTO = new QuationTenderReqDTO();
		FacesScopes facesScopes = new FacesScopes();
		String ctxToken = facesScopes.getBPMAuthenticationCtxToken();
		qtReqDTO.setToken(ctxToken);
		qtReqDTO.setTaskId(this.getManagedBean().getTaskId());

		qtReqDTO.setPayLoad(this.getManagedBean().getQuotationTenderDataType());
		// TODO Need to get it from front end and set it

		qtReqDTO.setOutcome(outcome);
		try {
			IQTDetailSO detailSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);

		/*	QuationTenderResDTO resDTO = (QuationTenderResDTO) detailSO
					.updateBPMAction(qtReqDTO);*/
			QuationTenderResDTO resDTO= new QuationTenderResDTO();
			resDTO.setSuccessful(true);
			if (resDTO.isSuccessful()) {

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Record saved successfully.", ""

						));

				FacesContext fc = FacesContext.getCurrentInstance();
				fc.getApplication()
						.getNavigationHandler()
						.handleNavigation(fc, null,
								"/xhtml/shared/acknowledge.xhtml");

			} else {
				throw new EpException("Failed.");
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Unable to process now", ""

					));
		}
		return null;

	}

}