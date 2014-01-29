package my.ep.web.bean.sc.quatationtender;

import java.util.Date;

public class QtnTenderBean {

	private String qtnTenderNum;
	private String procumentMode;
	private Date crtDateFrom;
	private Date crtdDateTo;
	private Date pubDateFrom;
	private Date pubDateTo;
	private Date clsDateFrom;
	private Date clsDateTo;
	private String title;
	private String status;	
	private String minstryCode;
	private String deptCode;
	private String ptjCode;
	private String fileNumber;
	private boolean type;
	private String planNumber;
	private String estmtPrice;
	private boolean currentYear;
	
	private String goSoDo;
	private boolean fullfillmentType;
	private Date completionPeriod;
	private boolean mto;
	private String associateQtnNumber;
	private boolean rfb;
	private String tenderType;
	private String serviceType;
	private String proposalValidityDays;
	
	
	public QtnTenderBean(){
		super();
	}
	
	/**
	 * @return the qtnTenderNum
	 */
	public String getQtnTenderNum() {
		return qtnTenderNum;
	}
	/**
	 * @param qtnTenderNum the qtnTenderNum to set
	 */
	public void setQtnTenderNum(String qtnTenderNum) {
		this.qtnTenderNum = qtnTenderNum;
	}
	/**
	 * @return the crtDateFrom
	 */
	public Date getCrtDateFrom() {
		return crtDateFrom;
	}
	/**
	 * @param crtDateFrom the crtDateFrom to set
	 */
	public void setCrtDateFrom(Date crtDateFrom) {
		this.crtDateFrom = crtDateFrom;
	}
	/**
	 * @return the crtdDateTo
	 */
	public Date getCrtdDateTo() {
		return crtdDateTo;
	}
	/**
	 * @param crtdDateTo the crtdDateTo to set
	 */
	public void setCrtdDateTo(Date crtdDateTo) {
		this.crtdDateTo = crtdDateTo;
	}
	/**
	 * @return the pubDateFrom
	 */
	public Date getPubDateFrom() {
		return pubDateFrom;
	}
	/**
	 * @param pubDateFrom the pubDateFrom to set
	 */
	public void setPubDateFrom(Date pubDateFrom) {
		this.pubDateFrom = pubDateFrom;
	}
	/**
	 * @return the pubDateTo
	 */
	public Date getPubDateTo() {
		return pubDateTo;
	}
	/**
	 * @param pubDateTo the pubDateTo to set
	 */
	public void setPubDateTo(Date pubDateTo) {
		this.pubDateTo = pubDateTo;
	}
	/**
	 * @return the clsDateFrom
	 */
	public Date getClsDateFrom() {
		return clsDateFrom;
	}
	/**
	 * @param clsDateFrom the clsDateFrom to set
	 */
	public void setClsDateFrom(Date clsDateFrom) {
		this.clsDateFrom = clsDateFrom;
	}
	/**
	 * @return the clsDateTo
	 */
	public Date getClsDateTo() {
		return clsDateTo;
	}
	/**
	 * @param clsDateTo the clsDateTo to set
	 */
	public void setClsDateTo(Date clsDateTo) {
		this.clsDateTo = clsDateTo;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	
	
	/**
	 * @return the minstryCode
	 */
	public String getMinstryCode() {
		return minstryCode;
	}

	/**
	 * @param minstryCode the minstryCode to set
	 */
	public void setMinstryCode(String minstryCode) {
		this.minstryCode = minstryCode;
	}

	/**
	 * @return the deptCode
	 */
	public String getDeptCode() {
		return deptCode;
	}

	/**
	 * @param deptCode the deptCode to set
	 */
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	/**
	 * @return the ptjCode
	 */
	public String getPtjCode() {
		return ptjCode;
	}

	/**
	 * @param ptjCode the ptjCode to set
	 */
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}

	/**
	 * @return the fileNumber
	 */
	public String getFileNumber() {
		return fileNumber;
	}

	/**
	 * @param fileNumber the fileNumber to set
	 */
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	/**
	 * @return the type
	 */
	public boolean isType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(boolean type) {
		this.type = type;
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
	 * @return the estmtPrice
	 */
	public String getEstmtPrice() {
		return estmtPrice;
	}

	/**
	 * @param estmtPrice the estmtPrice to set
	 */
	public void setEstmtPrice(String estmtPrice) {
		this.estmtPrice = estmtPrice;
	}

	/**
	 * @return the currentYear
	 */
	public boolean isCurrentYear() {
		return currentYear;
	}

	/**
	 * @param currentYear the currentYear to set
	 */
	public void setCurrentYear(boolean currentYear) {
		this.currentYear = currentYear;
	}

	
	/**
	 * @return the fullfillmentType
	 */
	public boolean isFullfillmentType() {
		return fullfillmentType;
	}

	/**
	 * @param fullfillmentType the fullfillmentType to set
	 */
	public void setFullfillmentType(boolean fullfillmentType) {
		this.fullfillmentType = fullfillmentType;
	}

	/**
	 * @return the completionPeriod
	 */
	public Date getCompletionPeriod() {
		return completionPeriod;
	}

	/**
	 * @param completionPeriod the completionPeriod to set
	 */
	public void setCompletionPeriod(Date completionPeriod) {
		this.completionPeriod = completionPeriod;
	}

	/**
	 * @return the mto
	 */
	public boolean isMto() {
		return mto;
	}

	/**
	 * @param mto the mto to set
	 */
	public void setMto(boolean mto) {
		this.mto = mto;
	}

	/**
	 * @return the associateQtnNumber
	 */
	public String getAssociateQtnNumber() {
		return associateQtnNumber;
	}

	/**
	 * @param associateQtnNumber the associateQtnNumber to set
	 */
	public void setAssociateQtnNumber(String associateQtnNumber) {
		this.associateQtnNumber = associateQtnNumber;
	}

	/**
	 * @return the rfb
	 */
	public boolean isRfb() {
		return rfb;
	}

	/**
	 * @param rfb the rfb to set
	 */
	public void setRfb(boolean rfb) {
		this.rfb = rfb;
	}

	
	/**
	 * @return the proposalValidityDays
	 */
	public String getProposalValidityDays() {
		return proposalValidityDays;
	}

	/**
	 * @param proposalValidityDays the proposalValidityDays to set
	 */
	public void setProposalValidityDays(String proposalValidityDays) {
		this.proposalValidityDays = proposalValidityDays;
	}

	/**
	 * @return the procumentMode
	 */
	public String getProcumentMode() {
		return procumentMode;
	}

	/**
	 * @param procumentMode the procumentMode to set
	 */
	public void setProcumentMode(String procumentMode) {
		this.procumentMode = procumentMode;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the tenderType
	 */
	public String getTenderType() {
		return tenderType;
	}

	/**
	 * @param tenderType the tenderType to set
	 */
	public void setTenderType(String tenderType) {
		this.tenderType = tenderType;
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

		
}
