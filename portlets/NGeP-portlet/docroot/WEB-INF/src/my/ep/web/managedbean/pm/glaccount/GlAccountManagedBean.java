package my.ep.web.managedbean.pm.glaccount;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pm.GlAccountSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "glAccountManagedBean")
@SessionScoped
public class GlAccountManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private GlAccountSearchBean glAccountSearchBean;
	/**
	 * @return the glAccountSearchBean
	 */
	public GlAccountSearchBean getGlAccountSearchBean() {
		return glAccountSearchBean;
	}
	/**
	 * @param glAccountSearchBean the glAccountSearchBean to set
	 */
	public void setGlAccountSearchBean(GlAccountSearchBean glAccountSearchBean) {
		this.glAccountSearchBean = glAccountSearchBean;
	}
	
}
