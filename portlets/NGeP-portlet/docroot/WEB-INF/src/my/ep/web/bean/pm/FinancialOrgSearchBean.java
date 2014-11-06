/**
 * 
 */
package my.ep.web.bean.pm;

/**
 * @author venugopal
 * 
 */
public class FinancialOrgSearchBean {
	/**
	 * @return the orgName
	 */
	public String getOrgName() {
		return orgName;
	}

	/**
	 * @param orgName
	 *            the orgName to set
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 * @return the regNumber
	 */
	public String getRegNumber() {
		return regNumber;
	}

	/**
	 * @param regNumber
	 *            the regNumber to set
	 */
	public void setRegNumber(String regNumber) {
		this.regNumber = regNumber;
	}

	/**
	 * @return the orgStatus
	 */
	public String getOrgStatus() {
		return orgStatus;
	}

	/**
	 * @param orgStatus
	 *            the orgStatus to set
	 */
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}

	/**
	 * @return the serviceType
	 */
	public String getServiceType() {
		return serviceType;
	}

	/**
	 * @param serviceType
	 *            the serviceType to set
	 */
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	private String orgName;
	private String regNumber;
	private String orgStatus;
	private String serviceType;

}
