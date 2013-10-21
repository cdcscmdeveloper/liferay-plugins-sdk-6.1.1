package my.ep.web.managedbean.fl.pr;

import java.io.Serializable;
import java.util.Date;

public class PrItemBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String deliveryAddress;
	private String endDate;
	private String startDate;
	
	/*public PrItemBean(String deliveryAddress,String endDate,String startDate) {
		// TODO Auto-generated constructor stub
		this.deliveryAddress=deliveryAddress;
		this.endDate=endDate;
		this.startDate=startDate;
	}*/

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	
	
}
