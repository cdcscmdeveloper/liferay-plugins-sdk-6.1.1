package my.ep.web.integration.gfmas;

import java.util.List;

/*
 * @author P Ganga Prasad
 */

public class GFMASError {

	private int recordType;
	private String agencyId;
	private int totalRecordCount;

	private List<ErrorDesc> errorDescs;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public List<ErrorDesc> getErrorDescs() {
		return errorDescs;
	}

	public void setErrorDescs(List<ErrorDesc> errorDescs) {
		this.errorDescs = errorDescs;
	}

}
