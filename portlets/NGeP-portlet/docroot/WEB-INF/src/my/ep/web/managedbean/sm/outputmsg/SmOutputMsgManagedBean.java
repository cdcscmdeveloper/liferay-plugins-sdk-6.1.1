package my.ep.web.managedbean.sm.outputmsg;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smOutputMsgManagedBean")
@SessionScoped
public class SmOutputMsgManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8696387706472812466L;
	private Long applId;
	private String applType;

	public Long getApplId() {
		return applId;
	}

	public void setApplId(Long applId) {
		this.applId = applId;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	}

}
