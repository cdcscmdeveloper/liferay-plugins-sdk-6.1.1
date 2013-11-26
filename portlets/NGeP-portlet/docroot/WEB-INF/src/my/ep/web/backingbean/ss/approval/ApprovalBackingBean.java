/**
 * 
 */
package my.ep.web.backingbean.ss.approval;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.biz.service.ss.quationtender.IQTDetailSO;
import my.ep.dto.ss.quationtender.QuationTenderReqDTO;
import my.ep.dto.ss.quationtender.QuationTenderResDTO;
import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.type.ss.ApprovalCategory;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.QTDetailManagedBean;
import my.ep.web.ss.quationtender.common.QTConstants;

import org.primefaces.event.TabChangeEvent;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends
		BaseBackingBean<QTDetailManagedBean> {

	private int activeApprovalTab;

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
		selectItems[1] = new SelectItem(ApprovalCategory.BPK_APPROVAL.value(),
				"BPK Approval");
		selectItems[2] = new SelectItem(ApprovalCategory.MITI_APPROVAL.value(),
				"MITI Approval");
		selectItems[3] = new SelectItem(ApprovalCategory.ICT_APPROVAL.value(),
				"ICT Approval");
		selectItems[4] = new SelectItem(
				ApprovalCategory.PROCUREMENT_BOARD_APPROVAL.value(),
				"Procurement Board Approval");
		selectItems[5] = new SelectItem(
				ApprovalCategory.DIVISION_APPROVAL.value(), "Division Approval");

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

	
	
	
}