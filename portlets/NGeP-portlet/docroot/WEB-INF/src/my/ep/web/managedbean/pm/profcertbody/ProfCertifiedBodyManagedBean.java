package my.ep.web.managedbean.pm.profcertbody;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "profcertifiedbodyManagedBean")
@SessionScoped
public class ProfCertifiedBodyManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ProfCertifiedBodySearchBean profCertifiedBodySearchBean;

	/**
	 * Search Bean List
	 */
	private List<PmCertBody> certBodyList;

	private PmCertBody certBody;

	private boolean islazy = false;

	/**
	 * @return the certBodyList
	 */
	public List<PmCertBody> getCertBodyList() {
		return certBodyList;
	}

	/**
	 * @param certBodyList
	 *            the certBodyList to set
	 */
	public void setCertBodyList(List<PmCertBody> certBodyList) {
		this.certBodyList = certBodyList;
	}

	/**
	 * @return the certBody
	 */
	public PmCertBody getCertBody() {
		return certBody;
	}

	/**
	 * @param certBody
	 *            the certBody to set
	 */
	public void setCertBody(PmCertBody certBody) {
		this.certBody = certBody;
	}

	public ProfCertifiedBodySearchBean getProfCertifiedBodySearchBean() {
		if (profCertifiedBodySearchBean == null) {
			profCertifiedBodySearchBean = new ProfCertifiedBodySearchBean();
		}
		return profCertifiedBodySearchBean;
	}

	public void setProfCertifiedBodySearchBean(
			ProfCertifiedBodySearchBean profCertifiedBodySearchBean) {
		this.profCertifiedBodySearchBean = profCertifiedBodySearchBean;
	}

	public boolean isIslazy() {
		return islazy;
	}

	public void setIslazy(boolean islazy) {
		this.islazy = islazy;
	}

}
