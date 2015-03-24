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
package my.ep.web.bean.sc.quatationtender;

import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoi;

// TODO: Auto-generated Javadoc
/**
 * The Class LoiLoaBean.
 * 
 * @author Sri
 * @since 09.12.2014
 */
public class LoiLoaBean {
	
	/** The sc loi. */
	private ScLoi scLoi ;
	
	/** The sc loa. */
	private ScLoa scLoa ;
	
	/** The loi loa type. */
	private String loiLoaType;
	
	/** The loi loa status. */
	private String loiLoaStatus;
	
	/**
	 * Gets the sc loi.
	 *
	 * @return the scLoi
	 */
	public ScLoi getScLoi() {
		return scLoi;
	}
	
	/**
	 * Sets the sc loi.
	 *
	 * @param scLoi the scLoi to set
	 */
	public void setScLoi(ScLoi scLoi) {
		this.scLoi = scLoi;
	}
	
	/**
	 * Gets the sc loa.
	 *
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}
	
	/**
	 * Sets the sc loa.
	 *
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	
	/**
	 * Gets the loi loa type.
	 *
	 * @return the loiLoaType
	 */
	public String getLoiLoaType() {
		return loiLoaType;
	}
	
	/**
	 * Sets the loi loa type.
	 *
	 * @param loiLoaType the loiLoaType to set
	 */
	public void setLoiLoaType(String loiLoaType) {
		this.loiLoaType = loiLoaType;
	}
	
	/**
	 * Gets the loi loa status.
	 *
	 * @return the loiLoaStatus
	 */
	public String getLoiLoaStatus() {
		return loiLoaStatus;
	}
	
	/**
	 * Sets the loi loa status.
	 *
	 * @param loiLoaStatus the loiLoaStatus to set
	 */
	public void setLoiLoaStatus(String loiLoaStatus) {
		this.loiLoaStatus = loiLoaStatus;
	}
}
