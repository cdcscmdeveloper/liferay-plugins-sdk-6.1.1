package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.Date;

public class SearchDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contractNumber;
	private String contractName;
	private String physicalNumber;
	private String circularNo;
	private String supplierName;
	private String LOANumber;
	private String contractStatus;
	private Date contractStart;
	private Date contractEnd;
	
	
	public Date getContractStart() {
		return contractStart;
	}

	public void setContractStart(Date contractStart) {
		this.contractStart = contractStart;
	}

	public Date getContractEnd() {
		return contractEnd;
	}

	public void setContractEnd(Date contractEnd) {
		this.contractEnd = contractEnd;
	}

	public String getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(String contractStatus) {
		this.contractStatus = contractStatus;
	}

	
	
	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getPhysicalNumber() {
		return physicalNumber;
	}

	public void setPhysicalNumber(String physicalNumber) {
		this.physicalNumber = physicalNumber;
	}

	public String getLOANumber() {
		return LOANumber;
	}

	public void setLOANumber(String lOANumber) {
		LOANumber = lOANumber;
	}


	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCircularNo() {
		return circularNo;
	}

	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

}
