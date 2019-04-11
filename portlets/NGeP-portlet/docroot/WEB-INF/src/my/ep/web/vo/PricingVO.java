/**
 * 
 */
package my.ep.web.vo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author siew mei
 *
 */
public class PricingVO {
	

	private Long pricingSeq;
	private String pricingType;
	private BigDecimal price;
	private Date startDate;
	private Date endDate;
	private String currency;
	private Integer tierFrom;
	private Integer tierTo;
	private boolean selected;
	//private Integer tempNo;
	
	
	public String getPricingType() {
		return pricingType;
	}
	public void setPricingType(String pricingType) {
		this.pricingType = pricingType;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public Integer getTierFrom() {
		return tierFrom;
	}
	public void setTierFrom(Integer tierFrom) {
		this.tierFrom = tierFrom;
	}
	public Integer getTierTo() {
		return tierTo;
	}
	public void setTierTo(Integer tierTo) {
		this.tierTo = tierTo;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Long getPricingSeq() {
		return pricingSeq;
	}
	public void setPricingSeq(Long pricingSeq) {
		this.pricingSeq = pricingSeq;
	}
	
	
}
