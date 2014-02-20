/**
 * 
 */
package my.ep.web.backingbean.sc.specification;

/**
 * @author ACER
 * 
 */
public class BankingBean {
	private String month;
	private String amount;

	public BankingBean(String month, String amount) {
		this.month = month;
		this.amount = amount;
	}

	/**
	 * @return the month
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(String month) {
		this.month = month;
	}

	/**
	 * @return the amount
	 */
	public String getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(String amount) {
		this.amount = amount;
	}

}
