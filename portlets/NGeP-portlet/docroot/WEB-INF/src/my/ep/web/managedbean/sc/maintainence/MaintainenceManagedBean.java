package my.ep.web.managedbean.sc.maintainence;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.sc.maintainence.MaintainList;

/**
*
* @author David
*/
@ManagedBean(name="maintainenceMB")
@SessionScoped


public class MaintainenceManagedBean {

private List<MaintainList> maintainencelist;
	
	
	private String selectedMaintainence;
	
	
	public List<MaintainList> getMaintainlist() {
		
		if(maintainencelist==null){
			maintainencelist = new ArrayList<MaintainList>();
		
			MaintainList a = new MaintainList();
			MaintainList b = new MaintainList();
			MaintainList c = new MaintainList();
		
		a.setMainno(1);
		a.setMaintitle("Ng");
		a.setSupno(1);
		a.setSuptitle("title1");
		a.setSuptype("type a");
		

	
		b.setMainno(2);
		b.setMaintitle("Jenny");
		b.setSupno(2);
		b.setSuptitle("title2");
		b.setSuptype("type b");
		

		c.setMainno(3);
		c.setMaintitle("John");
		c.setSupno(3);
		c.setSuptitle("title3");
		c.setSuptype("type c");
		
		
		maintainencelist.add(a);
		maintainencelist.add(b);
		maintainencelist.add(c);

		
		}
		return maintainencelist;
	}

	public void setMaintenencelist(List<MaintainList> maintainencelist) {
		this.maintainencelist = maintainencelist;
	}

	/**
	 * @return the selectedCommitte
	 */
	public String getSelectedMaintenence() {
		return selectedMaintainence;
	}

	/**
	 * @param selectedCommitte the selectedCommitte to set
	 */
	public void setSelectedMaintainence(String selectedMaintainence) {
		this.selectedMaintainence = selectedMaintainence;
	}
	
	public void changePanelState(){

	    //set panelIsVisible to true or false based on whatever conditions you choose
	
	}


}
