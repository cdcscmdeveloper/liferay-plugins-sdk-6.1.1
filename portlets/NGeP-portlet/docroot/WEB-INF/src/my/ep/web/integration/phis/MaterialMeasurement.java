package my.ep.web.integration.phis;

public class MaterialMeasurement {

	private String measurementCode;
	private String measurementDescription;
	private String measurementAction;
	private String measurementOldCode;

	public String getMeasurementCode() {
		return measurementCode;
	}

	public void setMeasurementCode(String measurementCode) {
		this.measurementCode = measurementCode;
	}

	public String getMeasurementDescription() {
		return measurementDescription;
	}

	public void setMeasurementDescription(String measurementDescription) {
		this.measurementDescription = measurementDescription;
	}

	public String getMeasurementAction() {
		return measurementAction;
	}

	public void setMeasurementAction(String measurementAction) {
		this.measurementAction = measurementAction;
	}

	public String getMeasurementOldCode() {
		return measurementOldCode;
	}

	public void setMeasurementOldCode(String measurementOldCode) {
		this.measurementOldCode = measurementOldCode;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof MaterialClass)
	    {
	    	MaterialMeasurement temp = (MaterialMeasurement) obj;
	        if(this.measurementCode.equalsIgnoreCase(temp.measurementCode) && this.measurementDescription.equalsIgnoreCase(temp.measurementDescription))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.measurementCode.hashCode()+this.measurementDescription.hashCode());        
	}

}
