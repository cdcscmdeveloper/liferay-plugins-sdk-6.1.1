package my.ep.web.backingbean.ss.quatationtender;

import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.ss.quatationtender.QtnTenderBean;
import my.ep.web.bean.ss.quatationtender.TabBean;
import my.ep.web.managedbean.ss.quationtender.SearchQtnTenderManagedBean;

import org.primefaces.event.RowEditEvent;

@ManagedBean(name = "searchQtnTenderBB")
@RequestScoped
public class SearchQtnTenderBackingBean extends
		BaseBackingBean<SearchQtnTenderManagedBean> {

	
	private boolean checked;

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
		List<String> summary = getManagedBean().getSelectedOptions();
		System.out.println("===add summary value=====" + summary.size());

		String[] strarray = (String[]) summary.toArray(new String[0]);
		String value = "";
		List<TabBean> tabs = new ArrayList<TabBean>();
		for (int i = 0; i < strarray.length; i++) {
			value = strarray[i];
			
			TabBean mcities1 = getManagedBean().getTabview().get(value);
			
			tabs.add(mcities1);
		
			getManagedBean().setTabBean(tabs);
					}
		if(summary.isEmpty()){
			getManagedBean().setTabBean(new ArrayList());
		}

	}
	
	
	
	
	public void addBPKMessage(AjaxBehaviorEvent e) {
		String id = (String) e.getComponent().getAttributes().get("value");
		System.out.println("=======get id value=====" + id);
		getManagedBean().setCheckBoxBPK(id);
		/*System.out.println("===add summary value=====" + summary.size());

		
		 * List<SelectItem> mcities1 = new ArrayList<SelectItem>(); Map cities =
		 * new HashMap<String, List<SelectItem>>(); mcities1.add(new
		 * SelectItem("Budget Approval")); String[] strarray = (String[])
		 * summary.toArray(); String value=""; for(int
		 * i=0;i<strarray.length;i++){ value=strarray[i];
		 * System.out.println("=====selected tab values===="+value); }
		 
		// getManagedBean().setTabview(cities.put(value,mcities1));
		// mcities1.add(new SelectItem("Budget Approval"));
		String[] strarray = (String[]) summary.toArray(new String[0]);
		String value = "";
		List<TabBean> tabs = new ArrayList();
		for (int i = 0; i < strarray.length; i++) {
			value = strarray[i];
			System.out.println("=====selected tab values====" + value);
			TabBean mcities1 = getManagedBean().getTabview().get(value);
			System.out.println("=======mcities1===" + mcities1.getTitleName());
			tabs.add(mcities1);
			System.out.println("=======tabs size value=====" + tabs.size());
			getManagedBean().setTabBean(tabs);
			System.out.println("========getMangaed bean===="
					+ getManagedBean().getTabBean().size());*/
		//}
	/*	String[] strarray = (String[]) summary.toArray(new String[0]);
		String value = "";
		List tabs = new ArrayList();
		for (int i = 0; i < strarray.length; i++) {
			value = strarray[i];
			System.out.println("=====selected tab values====" + value);
			//TabBean mcities1 = getManagedBean().getTabview().get(value);
			//System.out.println("=======mcities1===" + mcities1.getTitleName());
			tabs.add(value);
			System.out.println("=======tabs size value=====" + tabs.size());
			getManagedBean().setTabBean(tabs);
			System.out.println("========getMangaed bean===="
					+ getManagedBean().getTabBean().size());
		}
		//}
		getManagedBean().setTabBean(tabs);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(value));*/
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
}
