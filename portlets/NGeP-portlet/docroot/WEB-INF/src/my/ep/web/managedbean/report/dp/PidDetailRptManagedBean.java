/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.report.dp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The Class PidDetailRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="pidDetailRptManagedBean")
@SessionScoped
public class PidDetailRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	private String contractType;
	private String contractNo;
	private String contractName;
	/** The pid group. */
	private String pidGroup;
	
	/**
	 * @return the contractType
	 */
	public String getContractType() {
		return contractType;
	}
	/**
	 * @param contractType the contractType to set
	 */
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	/**
	 * @return the contractNo
	 */
	public String getContractNo() {
		return contractNo;
	}
	/**
	 * @param contractNo the contractNo to set
	 */
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	/**
	 * @return the contractName
	 */
	public String getContractName() {
		return contractName;
	}
	/**
	 * @param contractName the contractName to set
	 */
	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	/**
	 * @return the pidGroup
	 */
	public String getPidGroup() {
		return pidGroup;
	}
	/**
	 * @param pidGroup the pidGroup to set
	 */
	public void setPidGroup(String pidGroup) {
		this.pidGroup = pidGroup;
	}
	
}
