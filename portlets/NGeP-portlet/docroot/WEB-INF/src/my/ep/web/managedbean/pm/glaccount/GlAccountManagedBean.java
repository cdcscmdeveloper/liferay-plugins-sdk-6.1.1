package my.ep.web.managedbean.pm.glaccount;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.PmGlAccount;
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
	
	private PmGlAccount pmGlAccount;

	private String glAccountCode;

	private String glAccountName;

	private Long status;

	private boolean isLazy;
	
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
	/**
	 * @return the pmGlAccount
	 */
	public PmGlAccount getPmGlAccount() {
		return pmGlAccount;
	}
	/**
	 * @param pmGlAccount the pmGlAccount to set
	 */
	public void setPmGlAccount(PmGlAccount pmGlAccount) {
		this.pmGlAccount = pmGlAccount;
	}
	/**
	 * @return the glAccountCode
	 */
	public String getGlAccountCode() {
		return glAccountCode;
	}
	/**
	 * @param glAccountCode the glAccountCode to set
	 */
	public void setGlAccountCode(String glAccountCode) {
		this.glAccountCode = glAccountCode;
	}
	/**
	 * @return the glAccountName
	 */
	public String getGlAccountName() {
		return glAccountName;
	}
	/**
	 * @param glAccountName the glAccountName to set
	 */
	public void setGlAccountName(String glAccountName) {
		this.glAccountName = glAccountName;
	}
	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}
	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
}
