package my.ep.web.managedbean.sm.outputmsg;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smOutputMsgManagedBean")
@SessionScoped
public class SmOutputMsgManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8696387706472812466L;
	private SmAppl smAppl;
	public SmAppl getSmAppl() {
		return smAppl;
	}
	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

}
