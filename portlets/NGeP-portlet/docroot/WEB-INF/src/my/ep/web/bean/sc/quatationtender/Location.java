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

import java.io.Serializable;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Location.
 * @author Manga
 * @since 29.01.2014
 */
public class Location implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The state. */
	private String state;
	
	/** The location. */
	private List<String> location;

	/** The zone. */
	private String zone;

	/** The zone st. */
	private String zoneSt;
	
	/** The zone loc. */
	private List<String> zoneLoc;
	
	/**
	 * Instantiates a new location.
	 */
	public Location(){
		
	}
	
	/**
	 * Instantiates a new location.
	 *
	 * @param state the state
	 * @param location the location
	 */
	public Location(String state,List<String> location){
		this.state=state;
		this.location=location;
	}
	
	/**
	 * Instantiates a new location.
	 *
	 * @param zone the zone
	 * @param zoneSt the zone st
	 * @param zoneCt the zone ct
	 */
	public  Location(String zone,String zoneSt, List<String>zoneCt){
		this.zoneSt= zoneSt;
		this.zone=zone;
		this.zoneLoc=zoneCt;
	}
	
	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the state.
	 *
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public List<String> getLocation() {
		return location;
	}
	
	/**
	 * Sets the location.
	 *
	 * @param location the location to set
	 */
	public void setLocation(List<String> location) {
		this.location = location;
	}
	
	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	
	/**
	 * Sets the zone.
	 *
	 * @param zone the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	/**
	 * Gets the zone st.
	 *
	 * @return the zoneSt
	 */
	public String getZoneSt() {
		return zoneSt;
	}
	
	/**
	 * Sets the zone st.
	 *
	 * @param zoneSt the zoneSt to set
	 */
	public void setZoneSt(String zoneSt) {
		this.zoneSt = zoneSt;
	}
	
	/**
	 * Gets the zone loc.
	 *
	 * @return the zoneLoc
	 */
	public List<String> getZoneLoc() {
		return zoneLoc;
	}
	
	/**
	 * Sets the zone loc.
	 *
	 * @param zoneLoc the zoneLoc to set
	 */
	public void setZoneLoc(List<String> zoneLoc) {
		this.zoneLoc = zoneLoc;
	}

	}
