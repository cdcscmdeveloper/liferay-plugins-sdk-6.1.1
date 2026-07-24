/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.unitofmeasurement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.dto.pm.common.uom.UomResDTO;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Unit Of Measurement
 * 
 * @author DK
 * @since 9/8/2014
 */
@ManagedBean(name = "unitOfMeasurementManagedBean")
@SessionScoped
public class UnitOfMeasurementManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	/**
	 * unit of measurement search variable
	 */
	private PmUom pmUom;

	private String measurementCodes;

	private String measurementNames;

	private Long status;

	private boolean isLazy = false;
	
	private PmUom oldPmUom;

	/************ START: AUDIT #28895_35509 *************/
	private UomResDTO uomResDTO;

	/** The AUDIT PM UOM OLD Value. */
	private PmUom auditUomOldVal;

	/************ END: AUDIT #28895_35509 *************/
	
	public UnitOfMeasurementManagedBean(){}
	
	/**
	 * @return the pmUom
	 */
	public PmUom getPmUom() {
		return pmUom;
	}

	/**
	 * @param pmUom
	 *            the pmUom to set
	 */
	public void setPmUom(PmUom pmUom) {
		this.pmUom = pmUom;
	}

	/**
	 * @return the measurementCodes
	 */
	public String getMeasurementCodes() {
		return measurementCodes;
	}

	/**
	 * @param measurementCodes
	 *            the measurementCodes to set
	 */
	public void setMeasurementCodes(String measurementCodes) {
		this.measurementCodes = measurementCodes;
	}

	/**
	 * @return the getMeasurementNames
	 */
	public String getMeasurementNames() {
		return measurementNames;
	}

	/**
	 * @param getMeasurementNames
	 *            the getMeasurementNames to set
	 */
	public void setMeasurementNames(String measurementNames) {
		this.measurementNames = measurementNames;
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

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @return the oldPmUom
	 */
	public PmUom getOldPmUom() {
		return oldPmUom;
	}

	/**
	 * @param oldPmUom the oldPmUom to set
	 */
	public void setOldPmUom(PmUom oldPmUom) {
		this.oldPmUom = oldPmUom;
	}

	/************ START: AUDIT #28895_35509 *************/
	public PmUom getAuditUomOldVal() {
		return auditUomOldVal;
	}

	public void setAuditUomOldVal(PmUom auditUomOldVal) {
		this.auditUomOldVal = auditUomOldVal;
	}

	public UomResDTO getUomResDTO() {
		return uomResDTO;
	}

	public void setUomResDTO(UomResDTO uomResDTO) {
		this.uomResDTO = uomResDTO;
	}
	/************ END: AUDIT #28895_35509 *************/
	
}
