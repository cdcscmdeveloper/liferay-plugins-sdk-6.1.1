package my.ep.web.managedbean.pm.vot;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pm.VotSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "votManagedBean")
@SessionScoped
public class VotManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private VotSearchBean votSearchBean;
	/**
	 * @return the votSearchBean
	 */
	public VotSearchBean getVotSearchBean() {
		return votSearchBean;
	}
	/**
	 * @param votSearchBean the votSearchBean to set
	 */
	public void setVotSearchBean(VotSearchBean votSearchBean) {
		this.votSearchBean = votSearchBean;
	}
	
	
	

}
