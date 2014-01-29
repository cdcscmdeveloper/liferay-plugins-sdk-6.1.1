package my.ep.web.bean.sc.quatationtender;

import java.util.Date;

public class QtnTenderSearchBean {
	private String qtNo;
	private String procurementMode;
	private String qtTitle;
	private Date fromCreatedDate;
	private Date toCreatedDate;
	private Date fromPublishDate;
	private Date toPublishDate;
	private Date fromClosingDate;
	private Date toClosingDate;
	private String qtStatus;
	
	public QtnTenderSearchBean() {
	}

	/**
	 * @return the qtNo
	 */
	public String getQtNo() {
		return qtNo;
	}

	/**
	 * @param qtNo the qtNo to set
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	/**
	 * @return the procurementMode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * @param procurementMode the procurementMode to set
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * @return the qtTitle
	 */
	public String getQtTitle() {
		return qtTitle;
	}

	/**
	 * @param qtTitle the qtTitle to set
	 */
	public void setQtTitle(String qtTitle) {
		this.qtTitle = qtTitle;
	}

	/**
	 * @return the fromCreatedDate
	 */
	public Date getFromCreatedDate() {
		return fromCreatedDate;
	}

	/**
	 * @param fromCreatedDate the fromCreatedDate to set
	 */
	public void setFromCreatedDate(Date fromCreatedDate) {
		this.fromCreatedDate = fromCreatedDate;
	}

	/**
	 * @return the toCreatedDate
	 */
	public Date getToCreatedDate() {
		return toCreatedDate;
	}

	/**
	 * @param toCreatedDate the toCreatedDate to set
	 */
	public void setToCreatedDate(Date toCreatedDate) {
		this.toCreatedDate = toCreatedDate;
	}

	/**
	 * @return the fromPublishDate
	 */
	public Date getFromPublishDate() {
		return fromPublishDate;
	}

	/**
	 * @param fromPublishDate the fromPublishDate to set
	 */
	public void setFromPublishDate(Date fromPublishDate) {
		this.fromPublishDate = fromPublishDate;
	}

	/**
	 * @return the toPublishDate
	 */
	public Date getToPublishDate() {
		return toPublishDate;
	}

	/**
	 * @param toPublishDate the toPublishDate to set
	 */
	public void setToPublishDate(Date toPublishDate) {
		this.toPublishDate = toPublishDate;
	}

	/**
	 * @return the fromClosingDate
	 */
	public Date getFromClosingDate() {
		return fromClosingDate;
	}

	/**
	 * @param fromClosingDate the fromClosingDate to set
	 */
	public void setFromClosingDate(Date fromClosingDate) {
		this.fromClosingDate = fromClosingDate;
	}

	/**
	 * @return the toClosingDate
	 */
	public Date getToClosingDate() {
		return toClosingDate;
	}

	/**
	 * @param toClosingDate the toClosingDate to set
	 */
	public void setToClosingDate(Date toClosingDate) {
		this.toClosingDate = toClosingDate;
	}

	/**
	 * @return the qtStatus
	 */
	public String getQtStatus() {
		return qtStatus;
	}

	/**
	 * @param qtStatus the qtStatus to set
	 */
	public void setQtStatus(String qtStatus) {
		this.qtStatus = qtStatus;
	}
}
