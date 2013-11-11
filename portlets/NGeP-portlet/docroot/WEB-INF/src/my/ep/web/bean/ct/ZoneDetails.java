package my.ep.web.bean.ct;

import java.io.Serializable;
import java.util.Date;

public class ZoneDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String zoneName;
	private String zonePeriod;
	private String status;
    private Date startDate;
    private Date endDate;
	public ZoneDetails(){
		
	}
	
	public ZoneDetails(String zoneName, String zonePeriod){
		this.zoneName=zoneName;
		this.zonePeriod=zonePeriod;
	}
	
	public ZoneDetails(String zoneName){
		this.zoneName=zoneName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
