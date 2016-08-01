/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Unit of Measurement
 * 
 * @author Zeinab
 * @since 8/11/2015
 *
 */
public class UnitOfMeasurmentSearchBean implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String UOMCode;
	private String UOMName;
	public String getUOMCode() {
		return UOMCode;
	}
	public String getUOMName() {
		return UOMName;
	}
	public void setUOMCode(String uOMCode) {
		UOMCode = uOMCode;
	}
	public void setUOMName(String uOMName) {
		UOMName = uOMName;
	}


}
