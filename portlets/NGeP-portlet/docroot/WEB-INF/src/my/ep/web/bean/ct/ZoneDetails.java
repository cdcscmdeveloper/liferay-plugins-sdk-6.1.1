package my.ep.web.bean.ct;

import java.io.Serializable;

public class ZoneDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String zoneName;
	private String zonePeriod;
	
	public ZoneDetails(){
		
	}
	
	public ZoneDetails(String zoneName, String zonePeriod){
		this.zoneName=zoneName;
		this.zonePeriod=zonePeriod;
	}
	
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}


	public String getZonePeriod() {
		return zonePeriod;
	}
	public void setZonePeriod(String zonePeriod) {
		this.zonePeriod = zonePeriod;
	}
}
