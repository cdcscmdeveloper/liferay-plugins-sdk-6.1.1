package my.ep.web.managedbean.pm.vot;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;


import my.ep.persistence.entity.pm.common.PmVotFund;
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
	
	private PmVotFund pmVotFund;

	private String votCode;

	private String votName;
	
	private String kumpulanPtjCode;
	
	private String kumpulanPtjDesc;

	private Long status;

	private boolean isLazy;
	
	
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
	/**
	 * @return the pmVotFund
	 */
	public PmVotFund getPmVotFund() {
		return pmVotFund;
	}
	/**
	 * @param pmVotFund the pmVotFund to set
	 */
	public void setPmVotFund(PmVotFund pmVotFund) {
		this.pmVotFund = pmVotFund;
	}
	/**
	 * @return the votCode
	 */
	public String getVotCode() {
		return votCode;
	}
	/**
	 * @param votCode the votCode to set
	 */
	public void setVotCode(String votCode) {
		this.votCode = votCode;
	}
	/**
	 * @return the votName
	 */
	public String getVotName() {
		return votName;
	}
	/**
	 * @param votName the votName to set
	 */
	public void setVotName(String votName) {
		this.votName = votName;
	}
	/**
	 * @return the kumpulanPtjCode
	 */
	public String getKumpulanPtjCode() {
		return kumpulanPtjCode;
	}
	/**
	 * @param kumpulanPtjCode the kumpulanPtjCode to set
	 */
	public void setKumpulanPtjCode(String kumpulanPtjCode) {
		this.kumpulanPtjCode = kumpulanPtjCode;
	}
	/**
	 * @return the kumpulanPtjDesc
	 */
	public String getKumpulanPtjDesc() {
		return kumpulanPtjDesc;
	}
	/**
	 * @param kumpulanPtjDesc the kumpulanPtjDesc to set
	 */
	public void setKumpulanPtjDesc(String kumpulanPtjDesc) {
		this.kumpulanPtjDesc = kumpulanPtjDesc;
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
