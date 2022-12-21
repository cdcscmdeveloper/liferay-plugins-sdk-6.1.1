/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Zone
 * 
 * @author venugopal
 * @since 6/11/2014
 *
 */
public class ZoneSearchBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * @param args
	 */
	private String zoneCode;
	private String zoneName;
	private String stateName;
	private String status;
	
	public ZoneSearchBean(){}
	
	/**
	 * @return the zoneCode
	 */
	public String getZoneCode() {
		return zoneCode;
	}
	/**
	 * @param zoneCode the zoneCode to set
	 */
	public void setZoneCode(String zoneCode) {
		this.zoneCode = zoneCode;
	}
	/**
	 * @return the zoneName
	 */
	public String getZoneName() {
		return zoneName;
	}
	/**
	 * @param zoneName the zoneName to set
	 */
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}
	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	

}
