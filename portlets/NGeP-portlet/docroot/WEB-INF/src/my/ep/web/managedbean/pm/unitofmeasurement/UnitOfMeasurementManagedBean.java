package my.ep.web.managedbean.pm.unitofmeasurement;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author DK
 * 
 */
@ManagedBean(name = "unitOfMeasurementManagedBean")
@SessionScoped
public class UnitOfMeasurementManagedBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * unit of measurement 
	 * search variable
	 */
	private PmUom pmUom;
	
	private String measurementCodes;
	
	private String measurementNames;
	
	private Long status;
	
	
	/**
	 * @return the pmUom
	 */
	public PmUom getPmUom() {
		return pmUom;
	}

	/**
	 * @param pmUom the pmUom to set
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
	 * @param measurementCodes the measurementCodes to set
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
	 * @param getMeasurementNames the getMeasurementNames to set
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
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}
	
	
	
}
