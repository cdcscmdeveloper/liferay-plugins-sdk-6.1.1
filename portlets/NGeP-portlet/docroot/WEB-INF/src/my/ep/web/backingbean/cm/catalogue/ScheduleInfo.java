package my.ep.web.backingbean.cm.catalogue;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name="dtScheduleView")
@ViewScoped
public class ScheduleInfo {

	private String schNo;
	private String crNo;
	private String crNoStatus;
	private String coNo;
	private String coNoStatus;
	
	public ScheduleInfo() {
        // Default constructor logic
    }

	public ScheduleInfo(String schNo, String crNo, String crNoStatus, String coNo, String coNoStatus) {
		super();
		this.schNo = schNo;
		this.crNo = crNo;
		this.crNoStatus = crNoStatus;
		this.coNo = coNo;
		this.coNoStatus=coNoStatus;
	}

	public String getSchNo() {
		return schNo;
	}

	public void setSchNo(String schNo) {
		this.schNo = schNo;
	}

	public String getCrNo() {
		return crNo;
	}

	public void setCrNo(String crNo) {
		this.crNo = crNo;
	}

	public String getCoNo() {
		return coNo;
	}

	public void setCoNo(String coNo) {
		this.coNo = coNo;
	}

	public String getCoNoStatus() {
		return coNoStatus;
	}

	public void setCoNoStatus(String coNoStatus) {
		this.coNoStatus = coNoStatus;
	}
	
	public String getCrNoStatus() {
		return crNoStatus;
	}

	public void setCrNoStatus(String crNoStatus) {
		this.crNoStatus = crNoStatus;
	}

	@Override
	public String toString() {
		return "ScheduleInfo [schNo=" + schNo + ", crNo=" + crNo + ", crNoStatus=" + crNoStatus + ", coNo=" + coNo
				+ ", coNoStatus=" + coNoStatus + "]";
	}
	

	
	
	
	
    
}
