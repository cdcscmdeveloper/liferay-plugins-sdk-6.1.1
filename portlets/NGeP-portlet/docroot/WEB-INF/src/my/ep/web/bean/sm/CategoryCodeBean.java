package my.ep.web.bean.sm;

public class CategoryCodeBean {

	private int catCodeSeq;
	private int catCodeId;
	private String catName;

	public CategoryCodeBean() {
	}

	public CategoryCodeBean(int catCodeSeq, int catCodeId, String catName) {
		this.catCodeId = catCodeId;
		this.catName = catName;
		this.catCodeSeq = catCodeSeq;
	}

	public int getCatCodeSeq() {
		return catCodeSeq;
	}

	public void setCatCodeSeq(int catCodeSeq) {
		this.catCodeSeq = catCodeSeq;
	}

	public int getCatCodeId() {
		return catCodeId;
	}

	public void setCatCodeId(int catCodeId) {
		this.catCodeId = catCodeId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

}
