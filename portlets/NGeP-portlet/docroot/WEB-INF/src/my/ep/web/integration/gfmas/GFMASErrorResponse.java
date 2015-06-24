package my.ep.web.integration.gfmas;

import java.util.List;

public class GFMASErrorResponse {

	private int recordType;
	private String agencyId;
	private int totalRecordCount;

	List<ErrorDesc> errorDescList;

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

	public List<ErrorDesc> getErrorDescList() {
		return errorDescList;
	}

	public void setErrorDescList(List<ErrorDesc> errorDescList) {
		this.errorDescList = errorDescList;
	}

}
