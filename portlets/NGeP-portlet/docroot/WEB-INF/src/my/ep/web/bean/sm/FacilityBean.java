package my.ep.web.bean.sm;

public class FacilityBean {

	private int facilitySeq;
	private String facilityAddress;

	public FacilityBean() {
	}

	public FacilityBean(int facilitySeq, String facilityAddress) {
		this.facilitySeq = facilitySeq;
		this.facilityAddress = facilityAddress;
	}

	public int getFacilitySeq() {
		return facilitySeq;
	}

	public void setFacilitySeq(int facilitySeq) {
		this.facilitySeq = facilitySeq;
	}

	public String getFacilityAddress() {
		return facilityAddress;
	}

	public void setFacilityAddress(String facilityAddress) {
		this.facilityAddress = facilityAddress;
	}

}
