package my.ep.web.integration.phis;

/*
 * @author P Ganga prasad
 */
public class ItemType {

	private String unspscCode;
	private String itemCode;
	private String itemDescription;
	private String itemAction;
	private String itemOldcode;

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getItemAction() {
		return itemAction;
	}

	public void setItemAction(String itemAction) {
		this.itemAction = itemAction;
	}

	public String getItemOldcode() {
		return itemOldcode;
	}

	public void setItemOldcode(String itemOldcode) {
		this.itemOldcode = itemOldcode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ItemType) {
			ItemType temp = (ItemType) obj;
			if (this.itemCode.equalsIgnoreCase(temp.itemCode)
					&& this.itemDescription
							.equalsIgnoreCase(temp.itemDescription))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.itemCode.hashCode() + this.itemDescription.hashCode());
	}

}
