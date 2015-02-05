package my.ep.web.integration.phis;

public class MaterialColor {
	
	private String materialColorCode;
	private String materialColorDescription;
	private String materialColorAction;
	private String materialOldColorCode;


	public MaterialColor() {
	}

	public String getMaterialColorCode() {
		return materialColorCode;
	}

	public void setMaterialColorCode(String materialColorCode) {
		this.materialColorCode = materialColorCode;
	}

	public String getMaterialColorDescription() {
		return materialColorDescription;
	}

	public void setMaterialColorDescription(String materialColorDescription) {
		this.materialColorDescription = materialColorDescription;
	}

	public String getMaterialColorAction() {
		return materialColorAction;
	}

	public void setMaterialColorAction(String materialColorAction) {
		this.materialColorAction = materialColorAction;
	}

	public String getMaterialOldColorCode() {
		return materialOldColorCode;
	}

	public void setMaterialOldColorCode(String materialOldColorCode) {
		this.materialOldColorCode = materialOldColorCode;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if(obj instanceof MaterialColor)
	    {
	    	MaterialColor temp = (MaterialColor) obj;
	        if(this.materialColorCode.equalsIgnoreCase(temp.materialColorCode) && this.materialColorDescription.equalsIgnoreCase(temp.materialColorDescription))
	            return true;
	    }
	    return false;

	}
	@Override
	public int hashCode() {

	    return (this.materialColorCode.hashCode()+this.materialColorDescription.hashCode());        
	}
	
	
	

}
