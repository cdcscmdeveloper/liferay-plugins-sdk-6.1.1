package my.ep.web.integration.eperunding;

/*
 * @author P Ganga prasad
 */

public class RegisteredCategory {

	private String mOFNo;

	private Category category;

	public String getmOFNo() {
		return mOFNo;
	}

	public void setmOFNo(String mOFNo) {
		this.mOFNo = mOFNo;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RegisteredCategory) {
			RegisteredCategory temp = (RegisteredCategory) obj;
			if (this.mOFNo.equalsIgnoreCase(temp.mOFNo)
					&& this.category.getMainCat().equalsIgnoreCase(
							temp.category.getMainCat())
					&& this.category.getSubCat().equalsIgnoreCase(
							temp.category.getSubCat())
					&& this.category.getSubCatDivision().equalsIgnoreCase(
							temp.category.getSubCatDivision()))
				return true;
		}
		return false;

	}

	@Override
	public int hashCode() {

		return (this.mOFNo.hashCode() + this.category.getMainCat().hashCode()
				+ this.category.getSubCat().hashCode() + this.category
				.getSubCatDivision().hashCode());
	}

}
