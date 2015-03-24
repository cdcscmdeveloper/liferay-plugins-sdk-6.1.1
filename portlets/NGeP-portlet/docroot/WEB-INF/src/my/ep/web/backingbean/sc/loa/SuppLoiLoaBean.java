/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.loa;

import java.io.Serializable;
import java.util.List;

import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoaItem;

// TODO: Auto-generated Javadoc
/**
 * The Class SuppLoiLoaBean.
 * @author Ken
 * @since 9.10.2014
 */
public class SuppLoiLoaBean implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The loi loa no. */
	private String loiLoaNo;
	
	/** The type. */
	private String type;
	
	/** The status. */
	private PmStatus status;
	
	/** The sc loa. */
	private ScLoa scLoa;
	
	/** The sc loi. */
	private ScLoi scLoi;
	
	/** The sc loi loa items. */
	private List<ScLoiLoaItem> scLoiLoaItems;
	
	/**
	 * Gets the loi loa no.
	 *
	 * @return the loi loa no
	 */
	public String getLoiLoaNo() {
		return loiLoaNo;
	}
	
	/**
	 * Sets the loi loa no.
	 *
	 * @param loiLoaNo the new loi loa no
	 */
	public void setLoiLoaNo(String loiLoaNo) {
		this.loiLoaNo = loiLoaNo;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public PmStatus getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the status to set
	 */
	public void setStatus(PmStatus status) {
		this.status = status;
	}
	
	/**
	 * Gets the sc loa.
	 *
	 * @return the sc loa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}
	
	/**
	 * Sets the sc loa.
	 *
	 * @param scLoa the new sc loa
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	
	/**
	 * Gets the sc loi.
	 *
	 * @return the sc loi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}
	
	/**
	 * Sets the sc loi.
	 *
	 * @param scLoi the new sc loi
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}
	
	/**
	 * Gets the sc loi loa items.
	 *
	 * @return the sc loi loa items
	 */
	public List<ScLoiLoaItem> getScLoiLoaItems() {
		return scLoiLoaItems;
	}
	
	/**
	 * Sets the sc loi loa items.
	 *
	 * @param scLoiLoaItems the new sc loi loa items
	 */
	public void setScLoiLoaItems(List<ScLoiLoaItem> scLoiLoaItems) {
		this.scLoiLoaItems = scLoiLoaItems;
	}
}
