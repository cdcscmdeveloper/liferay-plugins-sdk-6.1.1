package my.ep.web.bean.ss.quatationtender;

import java.util.List;

public class Location {

	private String state;
	
	private List<String> location;

	private String zone;

	private String zoneSt;
	
	private List<String> zoneLoc;
	
	public Location(){
		
	}
	public Location(String state,List<String> location){
		this.state=state;
		this.location=location;
	}
	
	public  Location(String zone,String zoneSt, List<String>zoneCt){
		this.zoneSt= zoneSt;
		this.zone=zone;
		this.zoneLoc=zoneCt;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the location
	 */
	public List<String> getLocation() {
		return location;
	}
	/**
	 * @param location the location to set
	 */
	public void setLocation(List<String> location) {
		this.location = location;
	}
	/**
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	/**
	 * @param zone the zone to set
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}
	/**
	 * @return the zoneSt
	 */
	public String getZoneSt() {
		return zoneSt;
	}
	/**
	 * @param zoneSt the zoneSt to set
	 */
	public void setZoneSt(String zoneSt) {
		this.zoneSt = zoneSt;
	}
	/**
	 * @return the zoneLoc
	 */
	public List<String> getZoneLoc() {
		return zoneLoc;
	}
	/**
	 * @param zoneLoc the zoneLoc to set
	 */
	public void setZoneLoc(List<String> zoneLoc) {
		this.zoneLoc = zoneLoc;
	}

	}
