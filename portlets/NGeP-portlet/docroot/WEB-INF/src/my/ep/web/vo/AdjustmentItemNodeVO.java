/**
 * Copyright © 2015 Archisoft. All rights reserved.
 */
package my.ep.web.vo;

import java.io.Serializable;

import my.ep.persistence.entity.fl.adjust.FlAdjustmentItem;
import my.ep.persistence.entity.fl.payment.FlInvoiceItem;


/**
 * Adjustment Item VO for tree
 * 
 * @author kieyun
 * @since 17/8/2015
 *
 */
public class AdjustmentItemNodeVO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int level;
	
	private FlAdjustmentItem flAdjustmentItem;
	private FlInvoiceItem flInvoiceItem;
	
	public AdjustmentItemNodeVO(){
		
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
	public FlAdjustmentItem getFlAdjustmentItem() {
		return flAdjustmentItem;
	}
	public void setFlAdjustmentItem(FlAdjustmentItem flAdjustmentItem) {
		this.flAdjustmentItem = flAdjustmentItem;
	}

}
