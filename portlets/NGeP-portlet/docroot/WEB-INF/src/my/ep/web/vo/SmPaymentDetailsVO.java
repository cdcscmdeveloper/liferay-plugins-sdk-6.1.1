package my.ep.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class SmPaymentDetailsVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2278885503716631079L;

	private String details;
	private BigDecimal price;
	private BigDecimal totalAmount;
	// #RUU Start
	private int quantity;
	private boolean catCode;
	// #RUU End

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	// #RUU Start
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isCatCode() {
		return catCode;
	}

	public void setCatCode(boolean catCode) {
		this.catCode = catCode;
	}
	// #RUU end

}
