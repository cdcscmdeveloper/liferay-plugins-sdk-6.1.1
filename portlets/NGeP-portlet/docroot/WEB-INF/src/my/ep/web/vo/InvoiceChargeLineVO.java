/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import my.ep.persistence.entity.fl.payment.FlInvoiceItem;
import my.ep.persistence.entity.fl.payment.FlInvoiceItemSummary;

/**
 * ChargeLineVO
 * 
 * @author KK LIM
 * @since 20/5/2014
 *
 */
public class InvoiceChargeLineVO  {
	
	private int level;
	private FlInvoiceItem flInvoiceItem;
	private FlInvoiceItemSummary flInvoiceSummary;
	
	public InvoiceChargeLineVO(){
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the flInvoiceItem
	 */
	public FlInvoiceItem getFlInvoiceItem() {
		return flInvoiceItem;
	}
	/**
	 * @param flInvoiceItem the flInvoiceItem to set
	 */
	public void setFlInvoiceItem(FlInvoiceItem flInvoiceItem) {
		this.flInvoiceItem = flInvoiceItem;
	}
	/**
	 * @return the flInvoiceSummary
	 */
	public FlInvoiceItemSummary getFlInvoiceSummary() {
		return flInvoiceSummary;
	}
	/**
	 * @param flInvoiceSummary the flInvoiceSummary to set
	 */
	public void setFlInvoiceSummary(FlInvoiceItemSummary flInvoiceSummary) {
		this.flInvoiceSummary = flInvoiceSummary;
	}

}
