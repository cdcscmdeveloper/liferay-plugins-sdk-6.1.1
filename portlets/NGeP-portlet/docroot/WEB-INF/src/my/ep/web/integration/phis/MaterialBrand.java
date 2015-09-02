package my.ep.web.integration.phis;

/*
 * @author P Ganga prasad
 */
public class MaterialBrand {

	private String unspscCode;
	private String materialBrandCode;
	private String materialBrandDescription;
	private String materialBrandAction;
	private String materialOldBrandCode;

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getMaterialBrandCode() {
		return materialBrandCode;
	}

	public void setMaterialBrandCode(String materialBrandCode) {
		this.materialBrandCode = materialBrandCode;
	}

	public String getMaterialBrandDescription() {
		return materialBrandDescription;
	}

	public void setMaterialBrandDescription(String materialBrandDescription) {
		this.materialBrandDescription = materialBrandDescription;
	}

	public String getMaterialBrandAction() {
		return materialBrandAction;
	}

	public void setMaterialBrandAction(String materialBrandAction) {
		this.materialBrandAction = materialBrandAction;
	}

	public String getMaterialOldBrandCode() {
		return materialOldBrandCode;
	}

	public void setMaterialOldBrandCode(String materialOldBrandCode) {
		this.materialOldBrandCode = materialOldBrandCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MaterialBrand) {
			MaterialBrand temp = (MaterialBrand) obj;
			if (this.materialBrandCode.equalsIgnoreCase(temp.materialBrandCode)
					&& this.materialBrandDescription
							.equalsIgnoreCase(temp.materialBrandDescription))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.materialBrandCode.hashCode() + this.materialBrandDescription
				.hashCode());
	}
}
