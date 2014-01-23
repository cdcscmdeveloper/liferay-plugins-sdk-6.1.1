/**
 * 
 */
package my.ep.web.backingbean.ss.approval;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.persistence.entity.ss.quationtender.ScQtApprovalAttachment;
import my.ep.type.ss.ApprovalCategory;
import my.ep.web.backingbean.ss.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;

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
		SelectItem[] selectItems = new SelectItem[6];

		selectItems[0] = new SelectItem(
				ApprovalCategory.BUDGET_APPROVAL.value(), "Budget Approval");
		selectItems[1] = new SelectItem(ApprovalCategory.ICT_APPROVAL.value(),
				"ICT Project Approval");
		selectItems[2] = new SelectItem(ApprovalCategory.BPK_APPROVAL.value(),
				"BPK Approval");
		selectItems[3] = new SelectItem(
				ApprovalCategory.PROCUREMENT_BOARD_APPROVAL.value(),
				"Finalization Committee Approval");
		selectItems[4] = new SelectItem(ApprovalCategory.MITI_APPROVAL.value(),
				"MITI Approval");
		selectItems[5] = new SelectItem(
				ApprovalCategory.DIVISION_APPROVAL.value(), "PTJ Approval");

		return selectItems;
	}

	public void approvalTabChanged(TabChangeEvent ev) {
		
		System.out.println(activeApprovalTab);
	/*	for (TabBean tabBean : getManagedBean().getTabList()) {
			ScQtApproval scApproval = tabBean.getScQtApproval();
			System.out.println(tabBean.getTitleName() + " ::: "
					+ scApproval.getApprovalLetterNo());
		}*/

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

		ScQtApproval scQtApproval =this.getManagedBean().getScQtApproval();

		String uploadActionName = (String)event.getComponent().getAttributes().get("uploadAction");
		System.out.println("=============display uplaod event name====="+uploadActionName);
		System.out.println("getUploadUpdateId: " +getManagedBean().getUploadUpdateId());
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				"The file was uploaded successfully.", null);
		FacesContext.getCurrentInstance().addMessage(null, message);

		Timestamp date = new Timestamp(System.currentTimeMillis());
		ScQtApprovalAttachment attachment =scQtApproval.getScQtApprovalAttachment();
				
		if (null == attachment) {
			attachment = new ScQtApprovalAttachment();
			attachment.setScQtApproval(scQtApproval);
			
		}
		uploadAttachment(event.getFile(), attachment);
		setUploadAction(uploadActionName);

		scQtApproval.setScQtApprovalAttachment(attachment);
		System.out.println("===scQtApproval size====="
				+ scQtApproval.getScQtApprovalAttachment().getFileName());

	}

	/**
	 * @return the uploadAction
	 */
	public String getUploadAction() {
		return uploadAction;
	}

	/**
	 * @param uploadAction the uploadAction to set
	 */
	public void setUploadAction(String uploadAction) {
		this.uploadAction = uploadAction;
	}
	
	
}