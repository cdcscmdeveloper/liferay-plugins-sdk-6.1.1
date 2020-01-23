package my.ep.web.bean.pp;

import java.io.Serializable;
import java.util.Date;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;

/**
 * Search Bean for Procurement Plan
 * 
 * @author RAIN POH
 * @since 20/10/2013
 * 
 */
public class ProcurementPlanSearchBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String planNumber;
	private Long status;
	private String year;
	private PmOrgProfile ptj;
	private PmOrgProfile ministry;
	private PmOrgProfile kptj;
	private PmParameter planType;
	private PmParameter votType;
	private String planTitle;
	private Date invitationDateFrom;	//[#25550]
	private Date invitationDateTo;		//[#25550]
	private PmParameter categoryType;	//[#25550]
	private String titleOrDesc;			//[#25550]
	
	
	private String code; //[#25549]
	private String desc; //[#25549]
	private String paramforexpenditure; //[#25549]
	private PmParameter procurementType;  //[#25549] 
	private String planDesc;  //[#25549] 
	
	public ProcurementPlanSearchBean() {
	}

	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
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
	 * @param year
	 *            the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * @return the ptj
	 */
	public PmOrgProfile getPtj() {
		if (ptj == null)
			ptj = new PmOrgProfile();
		return ptj;
	}

	/**
	 * @param ptj
	 *            the ptj to set
	 */
	public void setPtj(PmOrgProfile ptj) {
		this.ptj = ptj;
	}

	/**
	 * @return the ministry
	 */
	public PmOrgProfile getMinistry() {
		if (ministry == null)
			ministry = new PmOrgProfile();
		return ministry;
	}

	/**
	 * @param ministry
	 *            the ministry to set
	 */
	public void setMinistry(PmOrgProfile ministry) {
		this.ministry = ministry;
	}

	/**
	 * @return the planType
	 */
	public PmParameter getPlanType() {
		if (planType == null)
			planType = new PmParameter();
		return planType;
	}

	/**
	 * @param planType
	 *            the planType to set
	 */
	public void setPlanType(PmParameter planType) {
		this.planType = planType;
	}

	/**
	 * @return the votType
	 */
	public PmParameter getVotType() {
		if (votType == null)
			votType = new PmParameter();
		return votType;
	}

	/**
	 * @param votType
	 *            the votType to set
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
	 * @param planNumber
	 *            the planNumber to set
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
	 * @param planTitle
	 *            the planTitle to set
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
	 * @param kptj
	 *            the kptj to set
	 */
	public void setKptj(PmOrgProfile kptj) {
		this.kptj = kptj;
	}

	public Date getInvitationDateFrom() {
		return invitationDateFrom;
	}

	public void setInvitationDateFrom(Date invitationDateFrom) {
		this.invitationDateFrom = invitationDateFrom;
	}

	public Date getInvitationDateTo() {
		return invitationDateTo;
	}

	public void setInvitationDateTo(Date invitationDateTo) {
		this.invitationDateTo = invitationDateTo;
	}

	public PmParameter getCategoryType() {
		if (categoryType == null)
			categoryType = new PmParameter();
		return categoryType;
	}

	public void setCategoryType(PmParameter categoryType) {
		this.categoryType = categoryType;
	}

	public String getTitleOrDesc() {
		return titleOrDesc;
	}

	public void setTitleOrDesc(String titleOrDesc) {
		this.titleOrDesc = titleOrDesc;
	}
	
	
	//25549 - Expenditure Plan - Start
	
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
		
		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		public String getParamforexpenditure() {
			return paramforexpenditure;
		}

		public void setParamforexpenditure(String paramforexpenditure) {
			this.paramforexpenditure = paramforexpenditure;
		}
		
		
		public PmParameter getProcurementType() {
			if (procurementType == null)
				procurementType = new PmParameter();
			return procurementType;
		}

		public void setProcurementType(PmParameter procurementType) {
			this.procurementType = procurementType;
		}
		
		/**
		 * @return the planDesc
		 */
		public String getPlanDesc() {
			return planDesc;
		}

		/**
		 * @param planDesc
		 *            the planDesc to set
		 */
		public void setPlanDesc(String planDesc) {
			this.planDesc = planDesc;
		}

		
		//[#25549] - End

}
