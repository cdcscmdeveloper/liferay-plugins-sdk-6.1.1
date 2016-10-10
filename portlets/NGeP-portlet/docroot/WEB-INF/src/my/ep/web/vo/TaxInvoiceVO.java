/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

/**
 * ChargeLineVO
 * 
 * @author KK LIM
 * @since 21/8/2014
 *
 */
public class TaxInvoiceVO  {
	
	private String gstFlag;
	private String gstRate;
	private String amountWithoutGst;
	private String gstAmount;
	private String gstFlagCode; 
	
	public TaxInvoiceVO(){
	}

	/**
	 * @return the gstAmount
	 */
	public String getGstAmount() {
		return gstAmount;
	}

	/**
	 * @param gstAmount the gstAmount to set
	 */
	public void setGstAmount(String gstAmount) {
		this.gstAmount = gstAmount;
	}

	/**
	 * @return the gstFlag
	 */
	public String getGstFlag() {
		return gstFlag;
	}

	/**
	 * @param gstFlag the gstFlag to set
	 */
	public void setGstFlag(String gstFlag) {
		this.gstFlag = gstFlag;
	}


	/**
	 * @return the gstRate
	 */
	public String getGstRate() {
		return gstRate;
	}


	/**
	 * @param gstRate the gstRate to set
	 */
	public void setGstRate(String gstRate) {
		this.gstRate = gstRate;
	}


	/**
	 * @return the gstFlagCode
	 */
	public String getGstFlagCode() {
		return gstFlagCode;
	}


	/**
	 * @param gstFlagCode the gstFlagCode to set
	 */
	public void setGstFlagCode(String gstFlagCode) {
		this.gstFlagCode = gstFlagCode;
	}


	public String getAmountWithoutGst() {
		return amountWithoutGst;
	}


	public void setAmountWithoutGst(String amountWithoutGst) {
		this.amountWithoutGst = amountWithoutGst;
	}

}
