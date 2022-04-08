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
	//200911@akmal - Enhancement27624 - Start
	private Date updatedDateFrom; //[#27624]
	private Date updatedDateTo; //[#27624]
	private Long planDetstatus;
	//200911@akmal - Enhancement27624 - End
	
	
	private String paramforexpenditure; //[#25549]
	private PmParameter procurementType;  //[#25549] 
	private String yearB; //[#25549]
	private String yearT; //[#25549]
	private String yearP; //[#25549]
	private String yearS; //[#25549]
	private String yearH; //[#25549]
	private String planDesc;  //[#25549] 
	private String planExCode; //[#25549]
	private String planExDesc; //[#25549]
	private String planExCodeT; //[#25549]
	private String planExDescT; //[#25549]
	private String planExCodeP; //[#25549]
	private String planExDescP; //[#25549]
	private String planExCodeS; //[#25549]
	private String planExDescS; //[#25549]
	private String planExCodeH; //[#25549]
	private String planExDescH; //[#25549]
	private Long statusB; //[#25549]
	private Long statusT; //[#25549]
	private Long statusP; //[#25549]
	private Long statusS; //[#25549]
	private Long statusH; //[#25549]
	
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

	//200911@akmal - Enhancement27624 - Start
	public Date getUpdatedDateFrom() {
		return updatedDateFrom;
	}

	public void setUpdatedDateFrom(Date updatedDateFrom) {
		this.updatedDateFrom = updatedDateFrom;
	}

	public Date getUpdatedDateTo() {
		return updatedDateTo;
	}

	public void setUpdatedDateTo(Date updatedDateTo) {
		this.updatedDateTo = updatedDateTo;
	}

	public Long getPlanDetstatus() {
		return planDetstatus;
	}

	public void setPlanDetstatus(Long planDetstatus) {
		this.planDetstatus = planDetstatus;
	}
	//200911@akmal - Enhancement27624 - End

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

	//[#25549] - Start
	public String getPlanExCode() {
		return planExCode;
	}


	public void setPlanExCode(String planExCode) {
		this.planExCode = planExCode;
	}
	
	
	public String getPlanExDesc() {
		return planExDesc;
	}


	public void setPlanExDesc(String planExDesc) {
		this.planExDesc = planExDesc;
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
		
	public String getPlanDesc() {
		return planDesc;
	}
	
	
	public void setPlanDesc(String planDesc) {
	this.planDesc = planDesc;

	}
	
	public String getPlanExCodeT() {
		return planExCodeT;
	}


	public void setPlanExCodeT(String planExCodeT) {
		this.planExCodeT = planExCodeT;
	}
	
	
	public String getPlanExDescT() {
		return planExDescT;
	}


	public void setPlanExDescT(String planExDescT) {
		this.planExDescT = planExDescT;
	}
	
	public String getPlanExCodeP() {
		return planExCodeP;
	}


	public void setPlanExCodeP(String planExCodeP) {
		this.planExCodeP = planExCodeP;
	}
	
	
	public String getPlanExDescP() {
		return planExDescP;
	}


	public void setPlanExDescP(String planExDescP) {
		this.planExDescP = planExDescP;
	}
	
	public String getPlanExCodeH() {
		return planExCodeH;
	}


	public void setPlanExCodeH(String planExCodeH) {
		this.planExCodeH = planExCodeH;
	}
	
	
	public String getPlanExDescH() {
		return planExDescH;
	}


	public void setPlanExDescH(String planExDescH) {
		this.planExDescH = planExDescH;
	}
	
	
	public String getPlanExCodeS() {
		return planExCodeS;
	}


	public void setPlanExCodeS(String planExCodeS) {
		this.planExCodeS = planExCodeS;
	}
	
	
	public String getPlanExDescS() {
		return planExDescS;
	}


	public void setPlanExDescS(String planExDescS) {
		this.planExDescS = planExDescS;
	}
	
	
	public Long getStatusB() {
		return statusB;
	}


	public void setStatusB(Long statusB) {
		this.statusB = statusB;
	}
	
	public Long getStatusT() {
		return statusT;
	}


	public void setStatusT(Long statusT) {
		this.statusT = statusT;
	}
	
	
	public Long getStatusP() {
		return statusP;
	}


	public void setStatusP(Long statusP) {
		this.statusP = statusP;
	}
	
	
	public Long getStatusS() {
		return statusS;
	}


	public void setStatusS(Long statusS) {
		this.statusS = statusS;
	}
	
	
	public Long getStatusH() {
		return statusH;
	}


	public void setStatusH(Long statusH) {
		this.statusH = statusH;
	}
	
	
	public String getYearB() {
		return yearB;
	}

	public void setYearB(String yearB) {
		this.yearB = yearB;
	}

	
	public String getYearT() {
		return yearT;
	}

	public void setYearT(String yearT) {
		this.yearT = yearT;
	}
	
	
	public String getYearP() {
		return yearP;
	}
	
	public void setYearP(String yearP) {
		this.yearP = yearP;
	}
	
	public String getYearS() {
		return yearS;
	}


	public void setYearS(String yearS) {
		this.yearS = yearS;
	}
	
	public String getYearH() {
		return yearH;
	}


	public void setYearH(String yearH) {
		this.yearH = yearH;
	}
	
	//[#25549] - End

}
