/**
 * 
 */
package my.ep.web.backingbean.ss.approval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
import my.ep.type.ss.ApprovalCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.GoSoDo;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.SearchQtnTenderManagedBean;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TabChangeEvent;

/**
 * @author ACER
 * 
 */
@ManagedBean(name = "approvalBackingBean")
@RequestScoped
public class ApprovalBackingBean extends
		BaseBackingBean<SearchQtnTenderManagedBean> {

	private boolean checked;

	private ScQtApproval scQtApprov = new ScQtApproval();

	List<GoSoDo> gosodo = this.getManagedBean().getGoSodo();
	
	
	private int activeApprovalTab;
	public String[] favNumber2;
	public String[] favNumber3;
	public String[] favNumber4;

	private String activeTabName;

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

	@PostConstruct
	public void init() {
		System.out
				.println("  ********** ENTER BACKING BEAN MAIN **************");	

		System.out.println("===== Initial Tab On Page Load ===== "
				+ activeTabName + " --- gssh ----"
				+ getManagedBean().getTabList().size());
		
		
		Map<Integer, ScQtApproval> tabSelection1 = new HashMap<Integer, ScQtApproval>();

	}

	public void save() {
		System.out
				.println("-----enter save method for the budger approval-----");
			}

	public void add() {
		System.out
				.println("inside add method...------------------------------");
		gosodo.add(new GoSoDo());
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked
	 *            the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void onEdit(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Edited",
				((QtnTenderBean) event.getObject()).getDeptCode());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void onCancel(RowEditEvent event) {
		FacesMessage msg = new FacesMessage("Car Cancelled",
				((QtnTenderBean) event.getObject()).getDeptCode());

		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void addMessage() {
		// List<String> summary = getManagedBean().getSelectedOptions();
		List<String> summary = this.getAppCatList();
		

		String[] strarray = (String[]) summary.toArray(new String[0]);
		String value = "";
		List<TabBean> tabs = new ArrayList<TabBean>();
		for (int i = 0; i < strarray.length; i++) {
			value = strarray[i];

			TabBean mcities1 = getManagedBean().getTabview().get(value);

			tabs.add(mcities1);

			getManagedBean().setTabBean(tabs);
		}
		if (summary.isEmpty()) {
			getManagedBean().setTabBean(new ArrayList());
		}

	}

	public void addBPKMessage(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setCheckBoxBPK(id);
		
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
	
	public SelectItem[] getApprovalCategories() {
		SelectItem[] selectItems = new SelectItem[6];
		
		selectItems[0] = new SelectItem(
				ApprovalCategory.BUDGET_APPROVAL.value(), "Budget Approval");
		selectItems[1] = new SelectItem(
				ApprovalCategory.BPK_APPROVAL.value(), "BPK Approval");
		selectItems[2] = new SelectItem(
				ApprovalCategory.MITI_APPROVAL.value(), "MITI Approval");
		selectItems[3] = new SelectItem(
				ApprovalCategory.ICT_APPROVAL.value(), "ICT Approval");
		selectItems[4] = new SelectItem(
				ApprovalCategory.PROCUREMENT_BOARD_APPROVAL.value(), "Procurement Board Approval");
		selectItems[5] = new SelectItem(
				ApprovalCategory.DIVISION_APPROVAL.value(), "Division Approval");
		
		return selectItems;
	}	

	/*public void onTabChange(TabChangeEvent abe) {

		System.out.println("=====previous tab  ==="
				+ this.getManagedBean().getPreviousTab()
				+ "===========Active Tab====" + getActivTab());
		System.out
				.println(" ***** BEFORE CHANGE MB ****** "
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());
		System.out.println("***********"
				+ this.getManagedBean().getTabSelection()
						.get(this.getManagedBean().getPreviousTab())
						.getApproverLetterNo());
		this.getManagedBean()
				.getTabSelection()
				.put(this.getManagedBean().getPreviousTab(),
						this.getManagedBean().getScQtApproval());
		System.out.println("***********"
				+ this.getManagedBean().getTabSelection()
						.get(this.getManagedBean().getPreviousTab())
						.getApproverLetterNo());
		ScQtApproval approval1 = this.getManagedBean().getTabSelection()
				.get(getActivTab());
		System.out.println(" ---------- ACTIV VALUE -------- "
				+ approval1.getApproverLetterNo() + "--- Activ ---"
				+ getActivTab());
		this.getManagedBean().setScQtApproval(approval1);
		System.out
				.println("=====Value After Setting New MB====="
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());
		FacesContext context = FacesContext.getCurrentInstance();

		System.out.println(" ===== Selected Tab Name ===== "
				+ getManagedBean().getTabList().get(getActivTab())
						.getTitleName());
		FacesMessage msg = new FacesMessage("Tab Changed", "Active Tab: "
				+ abe.getTab().getId());
		System.out.println("=====tab change value======"
				+ abe.getTab().getTitle());
	}
*/
	public void approvalTabChanged(TabChangeEvent ev) {
		for (TabBean tabBean : getManagedBean().getTabList()) {
			ScQtApproval scApproval = tabBean.getScQtApproval();
			System.out.println(tabBean.getTitleName() + " ::: "
					+ scApproval.getApproverLetterNo());
		}
		
		/*int activeApprovalTab = getActiveApprovalTab();
		TabBean tabBean = this.getManagedBean().getTabList().get(activeApprovalTab);
		System.out.println("title: " + tabBean.getTitleName());
		System.out.println(" **** VALUE FROM Prev FORM "
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());
		System.out.println("Object Code "
				+ this.getManagedBean().getScQtApproval());
		
		ScQtApproval scQtApproval = this.getManagedBean().getTabSelection().get(tabBean.getTitleName());

		this.getManagedBean().setScQtApproval(scQtApproval);
		for (Entry<String, ScQtApproval> appEntry : getManagedBean()
				.getTabSelection().entrySet()) {
			ScQtApproval app = appEntry.getValue();
			System.out.println(appEntry.getKey() + " ::: "
					+ app.getApproverLetterNo());
		}
		
		System.out
				.println(" **** VALUE TO Current FORM "
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());*/
	}

	private ScQtApproval cloneApproval(ScQtApproval app) {
		ScQtApproval cloneApp = new ScQtApproval();
		cloneApp.setApproverLetterNo(app.getApproverLetterNo());
		return cloneApp;
	}

	/**
	 * @return the gosodo
	 */
	public List<GoSoDo> getGosodo() {
		return gosodo;
	}

	/**
	 * @param gosodo
	 *            the gosodo to set
	 */
	public void setGosodo(List<GoSoDo> gosodo) {
		this.gosodo = gosodo;
	}

	

	private String tabName;

	/**
	 * @return the tabName
	 */
	public String getTabName() {
		return tabName;
	}

	/**
	 * @param tabName
	 *            the tabName to set
	 */
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	/**
	 * @return the favNumber2
	 */
	public String[] getFavNumber2() {
		return favNumber2;
	}

	/**
	 * @param favNumber2
	 *            the favNumber2 to set
	 */
	public void setFavNumber2(String[] favNumber2) {
		this.favNumber2 = favNumber2;
	}

	/**
	 * @return the favNumber3
	 */
	public String[] getFavNumber3() {
		return favNumber3;
	}

	/**
	 * @param favNumber3
	 *            the favNumber3 to set
	 */
	public void setFavNumber3(String[] favNumber3) {
		this.favNumber3 = favNumber3;
	}

	
	/**
	 * @return the activeTabName
	 */
	public String getActiveTabName() {
		return activeTabName;
	}

	/**
	 * @param activeTabName
	 *            the activeTabName to set
	 */
	public void setActiveTabName(String activeTabName) {
		this.activeTabName = activeTabName;
	}

	/**
	 * @return the scQtApprov
	 */
	public ScQtApproval getScQtApprov() {
		return scQtApprov;
	}

	/**
	 * @param scQtApprov
	 *            the scQtApprov to set
	 */
	public void setScQtApprov(ScQtApproval scQtApprov) {
		this.scQtApprov = scQtApprov;
	}

	/**
	 * @return the activeApprovalTab
	 */
	public int getActiveApprovalTab() {
		return activeApprovalTab;
	}

	/**
	 * @param activeApprovalTab the activeApprovalTab to set
	 */
	public void setActiveApprovalTab(int activeApprovalTab) {
		this.activeApprovalTab = activeApprovalTab;
	}

}