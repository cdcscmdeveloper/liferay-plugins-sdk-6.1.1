package my.ep.web.bean.ss.quatationtender;

public class CategoryCode {
	private int num;
	
	private String mainCCode;
	
	private String subCCode;
	
	private String subCDevison;
	
	private boolean manditory;

		
	/**
	 * @return the mainCCode
	 */
	public String getMainCCode() {
		return mainCCode;
	}

	/**
	 * @param mainCCode the mainCCode to set
	 */
	public void setMainCCode(String mainCCode) {
		this.mainCCode = mainCCode;
	}

	/**
	 * @return the subCCode
	 */
	public String getSubCCode() {
		return subCCode;
	}

	/**
	 * @param subCCode the subCCode to set
	 */
	public void setSubCCode(String subCCode) {
		this.subCCode = subCCode;
	}

	/**
	 * @return the subCDevison
	 */
	public String getSubCDevison() {
		return subCDevison;
	}

	/**
	 * @param subCDevison the subCDevison to set
	 */
	public void setSubCDevison(String subCDevison) {
		this.subCDevison = subCDevison;
	}

	/**
	 * @return the manditory
	 */
	public boolean isManditory() {
		return manditory;
	}

	/**
	 * @param manditory the manditory to set
	 */
	public void setManditory(boolean manditory) {
		this.manditory = manditory;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	
}
