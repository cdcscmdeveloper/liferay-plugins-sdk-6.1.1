/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
*/
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;

/**
 * The Class CtCircularSearchBean.
 * 
 * @author:Garikapati Mangaiah.
 */
public class CtCircularSearchBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cir cular no. */
	private String cirCularNo;
	
	/** The circular name. */
	private String circularName;
	
	/** The item name. */
	private String itemName;
	
	/**
	 * Gets the cir cular no.
	 *
	 * @return the cir cular no
	 */
	public String getCirCularNo() {
		return cirCularNo;
	}
	
	/**
	 * Sets the cir cular no.
	 *
	 * @param cirCularNo the new cir cular no
	 */
	public void setCirCularNo(String cirCularNo) {
		this.cirCularNo = cirCularNo;
	}
	
	/**
	 * Gets the circular name.
	 *
	 * @return the circular name
	 */
	public String getCircularName() {
		return circularName;
	}
	
	/**
	 * Sets the circular name.
	 *
	 * @param circularName the new circular name
	 */
	public void setCircularName(String circularName) {
		this.circularName = circularName;
	}
	
	/**
	 * Gets the item name.
	 *
	 * @return the item name
	 */
	public String getItemName() {
		return itemName;
	}
	
	/**
	 * Sets the item name.
	 *
	 * @param itemName the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}
