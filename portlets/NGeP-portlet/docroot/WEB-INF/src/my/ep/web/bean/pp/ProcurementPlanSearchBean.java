package my.ep.web.bean.pp;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;

/**
 * Search Bean for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 20/10/2013
 *
 */
public class ProcurementPlanSearchBean {
	
	private String planNumber;
	
	private Long status;
	
	private String year;
	
	private PmOrgProfile ptj;
	
	private PmOrgProfile ministry;
	
	private PmOrgProfile kptj;
	
	private PmParameter planType;
	
	private PmParameter votType;
	
	private String planTitle;

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
	 * @return the year
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	

	/**
	 * @return the ptj
	 */
	public PmOrgProfile getPtj() {
		if(ptj==null)
			ptj = new PmOrgProfile();
		return ptj;
	}

	/**
	 * @param ptj the ptj to set
	 */
	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}

	/**
	 * @return the ministry
	 */
	public PmOrgProfile getMinistry() {
		if(ministry==null)
			ministry = new PmOrgProfile();
		return ministry;
	}

	/**
	 * @param ministry the ministry to set
	 */
	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;
	}

	/**
	 * @return the planType
	 */
	public PmParameter getPlanType() {
		if(planType==null)
			planType = new PmParameter();
		return planType;
	}

	/**
	 * @param planType the planType to set
	 */
	public void setPlanType(PmParameter planType) {
		this.planType = planType;
	}

	/**
	 * @return the votType
	 */
	public PmParameter getVotType() {
		if(votType==null)
			votType = new PmParameter();
		return votType;
	}

	/**
	 * @param votType the votType to set
	 */
	public void setVotType(PmParameter votType) {
		this.votType = votType;
	}

	/**
	 * @return the planNumber
	 */
	public String getPlanNumber() {
		return planNumber;
	}

	/**
	 * @param planNumber the planNumber to set
	 */
	public void setPlanNumber(String planNumber) {
		this.planNumber = planNumber;
	}

	/**
	 * @return the planTitle
	 */
	public String getPlanTitle() {
		return planTitle;
	}

	/**
	 * @param planTitle the planTitle to set
	 */
	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	/**
	 * @return the kptj
	 */
	public PmOrgProfile getKptj() {
		return kptj;
	}

	/**
	 * @param kptj the kptj to set
	 */
	public void setKptj(PmOrgProfile kptj) {
		this.kptj = kptj;
	}
	
}
