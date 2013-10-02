package my.ep.web.managedbean.ss.committee;

import java.util.ArrayList;
import java.util.List;


import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
*
* @author David
*/
@ManagedBean(name="committeeMB")
@SessionScoped

public class CommitteeManagedBean {

	private List<CommitteeList> committeelists;
	
	
	private String selectedCommitte;
	
	
	public List<CommitteeList> getCommitteelist() {
		
		if(committeelists==null){
			committeelists = new ArrayList<CommitteeList>();
		
	    CommitteeList a = new CommitteeList();
	    CommitteeList b = new CommitteeList();
	    CommitteeList c = new CommitteeList();
		
		a.setComno(1);
		a.setComname("Ng");
		a.setIc("a8753");
		a.setPass("dsf");
		a.setRoles("Activate");
		a.setAppno("No1");
		a.setSecname("Mani");
		a.setListaccess("Approve");
		a.setTypemem("Other");
		a.setSecgroup("Other");
	
		
		b.setComno(2);
		b.setComname("Yp");
		b.setIc("a4431");
		b.setPass("dsf");
		b.setRoles("Activate");
		b.setAppno("No2");
		b.setSecname("Noni");
		b.setListaccess("Approve");
		b.setTypemem("Other");
		b.setSecgroup("Other");
		

		c.setComno(3);
		c.setComname("Danny");
		c.setIc("a7653");
		c.setPass("dsf");
		c.setRoles("Activate");
		c.setAppno("No3");
		c.setSecname("Manny");
		c.setListaccess("Approve");
		c.setTypemem("Other");
		c.setSecgroup("Other");
		
		
		committeelists.add(a);
		committeelists.add(b);
		committeelists.add(c);

		
		}
		return committeelists;
	}

	public void setCommitteelist(List<CommitteeList> committeelists) {
		this.committeelists = committeelists;
	}

	/**
	 * @return the selectedCommitte
	 */
	public String getSelectedCommitte() {
		return selectedCommitte;
	}

	/**
	 * @param selectedCommitte the selectedCommitte to set
	 */
	public void setSelectedCommitte(String selectedCommitte) {
		this.selectedCommitte = selectedCommitte;
	}
	
	

} 