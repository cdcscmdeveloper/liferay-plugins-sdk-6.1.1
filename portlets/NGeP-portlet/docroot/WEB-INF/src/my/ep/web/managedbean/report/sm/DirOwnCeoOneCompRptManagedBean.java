package my.ep.web.managedbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "dirOwnCeoOneCompRptMB")
@SessionScoped
public class DirOwnCeoOneCompRptManagedBean extends SmReportManagedBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2486000745942119419L;
	//15174 - start
	private String personnelName;
	//15174 - end
	
	//15174 - start
	public String getPersonnelName() {
		return personnelName;
	}
	public void setPersonnelName(String personnelName) {
		this.personnelName = personnelName;
	}
	//15174 - end

}
