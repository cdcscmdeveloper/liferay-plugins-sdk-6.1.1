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

	public List<CommitteeList> getCommitteelist() {
		
		if(committeelists==null){
			committeelists = new ArrayList<CommitteeList>();
		
	    CommitteeList a = new CommitteeList();
	    CommitteeList b = new CommitteeList();
	    CommitteeList c = new CommitteeList();
		
		a.setComno(1);
		a.setComname("Abcdef");
		a.setIc("urgent");
		a.setPass("dsf");
		a.setRoles("Activate");
		a.setAppno("No1");
		a.setSecname("Mani");

		
		b.setComno(1);
		b.setComname("Abcdef");
		b.setIc("urgent");
		b.setPass("dsf");
		b.setRoles("Activate");
		b.setAppno("No2");
		b.setSecname("Noni");


		c.setComno(1);
		c.setComname("Abcdef");
		c.setIc("urgent");
		c.setPass("dsf");
		c.setRoles("Activate");
		c.setAppno("No3");
		c.setSecname("Manny");
		
		committeelists.add(a);
		committeelists.add(b);
		committeelists.add(c);

		
		}
		return committeelists;
	}

	public void setCommitteelist(List<CommitteeList> committeelists) {
		this.committeelists = committeelists;
	}

} 