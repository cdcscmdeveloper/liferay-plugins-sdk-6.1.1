package my.ep.web.backingbean.sc.approval;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApprovalAttachment;
import my.ep.type.sc.ApprovalCategory;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.sc.quatationtender.TabBean;
import my.ep.web.managedbean.sc.quationtender.QTDetailManagedBean;
import my.ep.web.util.FacesScopes;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * @author ACER
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
		SelectItem[] selectItems = new SelectItem[7];

		selectItems[0] = new SelectItem(
				ApprovalCategory.BUDGET_APPROVAL.value(), "Budget Approval");
		selectItems[1] = new SelectItem(ApprovalCategory.BPK_APPROVAL.value(),
				"BPK Approval");
		selectItems[2] = new SelectItem(
				ApprovalCategory.COMMITTEE_APPROVAL.value(),
				"Committee Approval");
		selectItems[3] = new SelectItem(
				ApprovalCategory.PUBLICATION_APPROVAL.value(),
				"Publication Approval");
		selectItems[4] = new SelectItem(ApprovalCategory.PTJ_APPROVAL.value(),
				"PTJ Approval");
		selectItems[5] = new SelectItem(
				ApprovalCategory.PROCUREMENT_BOARD_APPROVAL.value(),
				"Procurement Board Approval");
		selectItems[6] = new SelectItem(
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
		ScQtApproval scQtApproval = this.getManagedBean().getScQtApproval();
		ScQtApprovalAttachment attachment = getManagedBean().getScQtApproval()
				.getScQtApprovalAttachment();
		if (null == attachment) {
			attachment = new ScQtApprovalAttachment();
			attachment.setScQtApproval(scQtApproval);
		}
		attachment.setUser(facesScopes.getUserSessionBean());
		attachment.setChangedDate(new Timestamp(System.currentTimeMillis()));
		uploadAttachment(event.getFile(), attachment);
		scQtApproval.setScQtApprovalAttachment(attachment);
		System.out.println("===scQtApproval size====="
				+ scQtApproval.getScQtApprovalAttachment().getFileName());
		//getManagedBean().setScQtApproval(scQtApproval);

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

	public String determineIcon() {
		String imageFileName = "icon_file_default.png";
		String ext = "";

		if (getManagedBean().getScQtApproval().getScQtApprovalAttachment() != null) {
			String fileName = getManagedBean().getScQtApproval()
					.getScQtApprovalAttachment().getFileName();

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

}