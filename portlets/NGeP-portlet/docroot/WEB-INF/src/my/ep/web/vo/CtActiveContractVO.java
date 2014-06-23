/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.util.Date;

import my.ep.persistence.entity.pm.common.PmParameter;


/**
 * CtActiveContractVO
 * 
 * @author KK LIM
 * @since 22/6/2014
 *
 */
public class CtActiveContractVO  {
	
	
	private String contractNo;
    
    private String scheduleNo;
     
    private String circularNo;
    
    private Date startEffective;
    
    private Date endEffective;
    
    private PmParameter fulfilmentType;
    
    private PmParameter contractType;
    
    private String BusinessRegNo;
    
    private String mofNo;
    
    private String supplierName;
    
    private Long contractId;
    
    public CtActiveContractVO(String contractNo, String scheduleNo, String circularNo,Date startEffective,Date endEffective,PmParameter fulfilmentType,PmParameter contractType,String BusinessRegNo,String mofNo, String supplierName,Long contractId) {
        this.contractNo = contractNo;
        this.scheduleNo = scheduleNo;
        this.circularNo = circularNo;
        this.startEffective = startEffective;
        this.endEffective = endEffective;
        this.fulfilmentType = fulfilmentType;
        this.contractType = contractType;
        this.BusinessRegNo = BusinessRegNo;
        this.mofNo = mofNo;
        this.supplierName = supplierName;
        this.contractId = contractId;
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
	 * @return the scheduleNo
	 */
	public String getScheduleNo() {
		return scheduleNo;
	}

	/**
	 * @param scheduleNo the scheduleNo to set
	 */
	public void setScheduleNo(String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}

	/**
	 * @return the circularNo
	 */
	public String getCircularNo() {
		return circularNo;
	}

	/**
	 * @param circularNo the circularNo to set
	 */
	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	/**
	 * @return the mofNo
	 */
	public String getMofNo() {
		return mofNo;
	}

	/**
	 * @param mofNo the mofNo to set
	 */
	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the fulfilmentType
	 */
	public PmParameter getFulfilmentType() {
		return fulfilmentType;
	}

	/**
	 * @param fulfilmentType the fulfilmentType to set
	 */
	public void setFulfilmentType(PmParameter fulfilmentType) {
		this.fulfilmentType = fulfilmentType;
	}

	/**
	 * @return the contractType
	 */
	public PmParameter getContractType() {
		return contractType;
	}

	/**
	 * @param contractType the contractType to set
	 */
	public void setContractType(PmParameter contractType) {
		this.contractType = contractType;
	}

	/**
	 * @return the businessRegNo
	 */
	public String getBusinessRegNo() {
		return BusinessRegNo;
	}

	/**
	 * @param businessRegNo the businessRegNo to set
	 */
	public void setBusinessRegNo(String businessRegNo) {
		BusinessRegNo = businessRegNo;
	}

	/**
	 * @return the startEffective
	 */
	public Date getStartEffective() {
		return startEffective;
	}

	/**
	 * @param startEffective the startEffective to set
	 */
	public void setStartEffective(Date startEffective) {
		this.startEffective = startEffective;
	}

	/**
	 * @return the endEffective
	 */
	public Date getEndEffective() {
		return endEffective;
	}

	/**
	 * @param endEffective the endEffective to set
	 */
	public void setEndEffective(Date endEffective) {
		this.endEffective = endEffective;
	}

	/**
	 * @return the contractId
	 */
	public Long getContractId() {
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}
    



}
