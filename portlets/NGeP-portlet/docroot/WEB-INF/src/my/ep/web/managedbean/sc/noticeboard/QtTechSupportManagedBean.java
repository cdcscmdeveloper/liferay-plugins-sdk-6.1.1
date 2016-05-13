package my.ep.web.managedbean.sc.noticeboard;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "schedulerManagerManagedBean")
@SessionScoped
public class QtTechSupportManagedBean extends BaseManagedBean {

	private List<ScQt> erroredQts;

	/**
	 * @return the erroredQts
	 */
	public List<ScQt> getErroredQts() {
		return erroredQts;
	}

	/**
	 * @param erroredQts the erroredQts to set
	 */
	public void setErroredQts(List<ScQt> erroredQts) {
		this.erroredQts = erroredQts;
	}
	
}
