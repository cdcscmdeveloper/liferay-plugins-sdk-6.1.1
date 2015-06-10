package my.ep.web.managedbean.pm.fund;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.PmVotFund;
import my.ep.web.bean.pm.FundSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "fundManagedBean")
@SessionScoped
public class FundManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FundSearchBean fundSearchBean;
	
	private PmVotFund pmVotFund;

	private String fundCode;

	private String fundName;
	
	private Long status;

	private boolean isLazy;
	
	
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
	 * @return the fundSearchBean
	 */
	public FundSearchBean getFundSearchBean() {
		return fundSearchBean;
	}
	/**
	 * @param fundSearchBean the fundSearchBean to set
	 */
	public void setFundSearchBean(FundSearchBean fundSearchBean) {
		this.fundSearchBean = fundSearchBean;
	}
	
	/**
	 * @return the fundCode
	 */
	public String getCode() {
		return fundCode;
	}
	/**
	 * @param fundCode the fundCode to set
	 */
	public void setFundCode(String fundCode) {
		this.fundCode = fundCode;
	}
	/**
	 * @return the fundName
	 */
	public String getFundName() {
		return fundName;
	}
	/**
	 * @param fundName the fundName to set
	 */
	public void setFundName(String fundName) {
		this.fundName = fundName;
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
