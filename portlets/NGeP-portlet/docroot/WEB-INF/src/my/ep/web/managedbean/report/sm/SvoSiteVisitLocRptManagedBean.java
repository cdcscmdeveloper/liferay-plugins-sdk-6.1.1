package my.ep.web.managedbean.report.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "svoSiteVisitLocRptMB")
@SessionScoped
public class SvoSiteVisitLocRptManagedBean extends SmReportManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8216956527603600956L;
	private List<SelectItem> svoUserList;
	private String svoUser;

	public List<SelectItem> getSvoUserList() {
		return svoUserList;
	}

	public void setSvoUserList(List<SelectItem> svoUserList) {
		this.svoUserList = svoUserList;
	}

	public String getSvoUser() {
		return svoUser;
	}

	public void setSvoUser(String svoUser) {
		this.svoUser = svoUser;
	}

}
