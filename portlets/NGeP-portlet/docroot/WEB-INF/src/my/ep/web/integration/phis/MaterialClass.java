package my.ep.web.integration.phis;


public class MaterialClass {

	private String materialClassCode;
	private String materialClassDescription;
	private String materialClassAction;
	private String materialOldClassCode;

	public String getMaterialClassCode() {
		return materialClassCode;
	}

	public void setMaterialClassCode(String materialClassCode) {
		this.materialClassCode = materialClassCode;
	}

	public String getMaterialClassDescription() {
		return materialClassDescription;
	}

	public void setMaterialClassDescription(String materialClassDescription) {
		this.materialClassDescription = materialClassDescription;
	}

	public String getMaterialClassAction() {
		return materialClassAction;
	}

	public void setMaterialClassAction(String materialClassAction) {
		this.materialClassAction = materialClassAction;
	}

	public String getMaterialOldClassCode() {
		return materialOldClassCode;
	}

	public void setMaterialOldClassCode(String materialOldClassCode) {
		this.materialOldClassCode = materialOldClassCode;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof MaterialClass)
	    {
	    	MaterialClass temp = (MaterialClass) obj;
	        if(this.materialClassCode.equalsIgnoreCase(temp.materialClassCode) && this.materialClassDescription.equalsIgnoreCase(temp.materialClassDescription))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.materialClassCode.hashCode()+this.materialClassDescription.hashCode());        
	}

}
