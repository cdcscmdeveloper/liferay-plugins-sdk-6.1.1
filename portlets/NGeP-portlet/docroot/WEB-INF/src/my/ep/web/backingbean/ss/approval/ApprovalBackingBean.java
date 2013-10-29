/**
 * 
 */
package my.ep.web.backingbean.ss.approval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.persistence.entity.ss.quationtender.ScQtApproval;
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
	List<String> appCatList = new ArrayList<String>();
	
	
	private int activTab;
	public String[] favNumber2;
	public String[] favNumber3;
	public String[] favNumber4;

	private String activeTabName;

	public int getActivTab() {
		return activTab;
	}

	public void setActivTab(int activTab) {
		this.activTab = activTab;
	}

	/**
	 * @return the appCatList
	 */
	public List<String> getAppCatList() {
		return appCatList;
	}

	/**
	 * @param appCatList
	 *            the appCatList to set
	 */
	public void setAppCatList(List<String> appCatList) {
		this.appCatList = appCatList;
	}

	@PostConstruct
	public void init() {
		System.out
				.println("  ********** ENTER BACKING BEAN MAIN **************");
		List<String> appCatList = new ArrayList<String>();

	
	

		appCatList.add("Budget Approval");
		appCatList.add("BPK Approval");

		appCatList.add("MITI Approval");
		appCatList.add("ICT Approval");
		// appCatList.add("ProcurementBoard Approval");
		// appCatList.add("PTJ Approval");
		this.setAppCatList(appCatList);

	

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

	public String[] getFavNumber2Value() {

		favNumber2 = new String[2];
		favNumber2[0] = "Budget Approval";

		favNumber2[1] = "ICT Approval";

		return favNumber2;
	}

	

	public String getFavNumber2InString() {
		return Arrays.toString(favNumber2);
	}



	public String[] getFavNumber3Value() {

		favNumber3 = new String[2];
		favNumber3[0] = "BPK Approval";

		favNumber3[1] = "Finalization Committee Approval";

		return favNumber3;
	}

	public String getFavNumber3InString() {
		return Arrays.toString(favNumber3);
	}

	

	public String[] getFavNumber4Value() {

		favNumber4 = new String[2];
		favNumber3[0] = "MITI Approval";

		favNumber3[1] = "Division Approval ";

		;

		return favNumber3;
	}

	public String getFavNumber4InString() {
		return Arrays.toString(favNumber3);
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
		this.getManagedBean().setPreviousTab(getActivTab());
	}
*/
	public void newTabSelected(TabChangeEvent ev) {
		System.out.println("=====previous tab  ==="
				+ this.getManagedBean().getPreviousTab()
				+ "===========Active Tab====" + getActivTab());
		System.out
				.println(" **** VALUE FROM Prev FORM "
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());
		System.out.println("Object Code "
				+ this.getManagedBean().getScQtApproval());
		// this.getManagedBean()
		// .getTabSelection()
		// .put(this.getManagedBean().getPreviousTab(),
		// this.getManagedBean().getScQtApproval());

		// this.getManagedBean().setScQtApproval(new ScQtApproval());
		ScQtApproval approval1 = this.getManagedBean().getTabSelection()
				.get(getActivTab());

		this.getManagedBean().setScQtApproval(approval1);
		for (Entry<Integer, ScQtApproval> appEntry : getManagedBean()
				.getTabSelection().entrySet()) {
			ScQtApproval app = appEntry.getValue();
			System.out.println(appEntry.getKey() + " ::: "
					+ app.getApproverLetterNo());
		}
		
		System.out
				.println(" **** VALUE TO Current FORM "
						+ this.getManagedBean().getScQtApproval()
								.getApproverLetterNo());

		FacesContext context = FacesContext.getCurrentInstance();

		System.out.println(" ===== Selected Tab Name ===== "
				+ getManagedBean().getTabList().get(getActivTab())
						.getTitleName());
	
		// this.getManagedBean().setPreviousTab(getActivTab());

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

}