package my.ep.web.managedbean.report.ct.panel;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.report.ct.CtReportBaseManagedBean;


@ManagedBean(name = "ccContractItemMonitoringRptMB")
@SessionScoped
public class ContractItemMonitoringCCRptManagedBean extends CtReportBaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	
	private String circularNo;
	private String circularName;
	private Date circularEffFromDate;
	private Date circularEffToDate;
	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}
	/**
	 * @return the circularName
	 */
	public String getCircularName() {
		return circularName;
	}
	/**
	 * @return the circularEffFromDate
	 */
	public Date getCircularEffFromDate() {
		return circularEffFromDate;
	}
	/**
	 * @return the circularEffToDate
	 */
	public Date getCircularEffToDate() {
		return circularEffToDate;
	}
	/**
	 * @param circularNo the circularNo to set
	 */
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}
	/**
	 * @param circularName the circularName to set
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}
	/**
	 * @param circularEffFromDate the circularEffFromDate to set
	 */
	public void setCircularEffFromDate(Date circularEffFromDate) {
		this.circularEffFromDate = circularEffFromDate;
	}
	/**
	 * @param circularEffToDate the circularEffToDate to set
	 */
	public void setCircularEffToDate(Date circularEffToDate) {
		this.circularEffToDate = circularEffToDate;
	}


}
