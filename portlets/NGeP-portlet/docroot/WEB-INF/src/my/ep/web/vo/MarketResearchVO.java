/**
 * Copyright © 2016 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import my.ep.persistence.entity.pm.common.PmParameter;


/**
 * Adjustment Item VO for tree
 * 
 * @author Ken
 * @since 14.04.2016
 *
 */
public class MarketResearchVO  {
	
	private boolean isPanel;
	private String circularNo;
	private String itemGroupName;
	private String procurementTitle;
	private BigDecimal estimatedAmount;
	private PmParameter procurementMode;
	private String openFor;
	private boolean isZonal;
	private PmParameter procurementTypeCatId;
	private PmParameter pmProcurementCategory;
	private String evaluationType;
	private PmParameter contractType; 
	private PmParameter fulfilmentType;
	private Integer contractDuration;
	private BigDecimal loaAmt;
	private BigDecimal gstAmt;
	private BigDecimal overallContractAmt;	
	private Date ackDate;
	
	public MarketResearchVO(){
		
	}

	/**
	 * @return the isPanel
	 */
	public boolean isPanel() {
		return isPanel;
	}

	/**
	 * @param isPanel the isPanel to set
	 */
	public void setIsPanel(boolean isPanel) {
		this.isPanel = isPanel;
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
	 * @return the itemGroupName
	 */
	public String getItemGroupName() {
		return itemGroupName;
	}

	/**
	 * @param itemGroupName the itemGroupName to set
	 */
	public void setItemGroupName(String itemGroupName) {
		this.itemGroupName = itemGroupName;
	}

	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle() {
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle) {
		this.procurementTitle = procurementTitle;
	}

	/**
	 * @return the estimateAmt
	 */
	public BigDecimal getEstimatedAmount() {
		return estimatedAmount;
	}

	/**
	 * @param estimateAmt the estimateAmt to set
	 */
	public void setEstimatedAmount(BigDecimal estimatedAmount) {
		this.estimatedAmount = estimatedAmount;
	}

	/**
	 * @return the procurementMode
	 */
	public PmParameter getProcurementMode() {
		return procurementMode;
	}

	/**
	 * @param procurementMode the procurementMode to set
	 */
	public void setProcurementMode(PmParameter procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * @return the openFor
	 */
	public String getOpenFor() {
		return openFor;
	}

	/**
	 * @param openFor the openFor to set
	 */
	public void setOpenFor(String openFor) {
		this.openFor = openFor;
	}

	/**
	 * @return the procurementTypeCatId
	 */
	public PmParameter getProcurementTypeCatId() {
		return procurementTypeCatId;
	}

	/**
	 * @param procurementTypeCatId the procurementTypeCatId to set
	 */
	public void setProcurementTypeCatId(PmParameter procurementTypeCatId) {
		this.procurementTypeCatId = procurementTypeCatId;
	}

	/**
	 * @return the pmProcurementCategory
	 */
	public PmParameter getPmProcurementCategory() {
		return pmProcurementCategory;
	}

	/**
	 * @param pmProcurementCategory the pmProcurementCategory to set
	 */
	public void setPmProcurementCategory(PmParameter pmProcurementCategory) {
		this.pmProcurementCategory = pmProcurementCategory;
	}

	/**
	 * @return the evaluationType
	 */
	public String getEvaluationType() {
		return evaluationType;
	}

	/**
	 * @param evaluationType the evaluationType to set
	 */
	public void setEvaluationType(String evaluationType) {
		this.evaluationType = evaluationType;
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
	 * @return the contractDuration
	 */
	public Integer getContractDuration() {
		return contractDuration;
	}

	/**
	 * @param contractDuration the contractDuration to set
	 */
	public void setContractDuration(Integer contractDuration) {
		this.contractDuration = contractDuration;
	}

	/**
	 * @return the loaAmt
	 */
	public BigDecimal getLoaAmt() {
		return loaAmt;
	}

	/**
	 * @param loaAmt the loaAmt to set
	 */
	public void setLoaAmt(BigDecimal loaAmt) {
		this.loaAmt = loaAmt;
	}

	/**
	 * @return the gstAmt
	 */
	public BigDecimal getGstAmt() {
		return gstAmt;
	}

	/**
	 * @param gstAmt the gstAmt to set
	 */
	public void setGstAmt(BigDecimal gstAmt) {
		this.gstAmt = gstAmt;
	}

	/**
	 * @return the overallContractAmt
	 */
	public BigDecimal getOverallContractAmt() {
		return overallContractAmt;
	}

	/**
	 * @param overallContractAmt the overallContractAmt to set
	 */
	public void setOverallContractAmt(BigDecimal overallContractAmt) {
		this.overallContractAmt = overallContractAmt;
	}

	/**
	 * @return the ackDate
	 */
	public Date getAckDate() {
		return ackDate;
	}

	/**
	 * @param ackDate the ackDate to set
	 */
	public void setAckDate(Date ackDate) {
		this.ackDate = ackDate;
	}

	/**
	 * @return the isZonal
	 */
	public boolean isZonal() {
		return isZonal;
	}

	/**
	 * @param isZonal the isZonal to set
	 */
	public void setZonal(boolean isZonal) {
		this.isZonal = isZonal;
	}
	
	
}