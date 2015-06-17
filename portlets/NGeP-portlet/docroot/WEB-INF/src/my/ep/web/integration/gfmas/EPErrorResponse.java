package my.ep.web.integration.gfmas;

import java.util.List;

public class EPErrorResponse {

	private String recordType;
	private int tahunKewangan;
	private String namaFailAsal;

	private List<ErrorDetail> errorDetailList;

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public int getTahunKewangan() {
		return tahunKewangan;
	}

	public void setTahunKewangan(int tahunKewangan) {
		this.tahunKewangan = tahunKewangan;
	}

	public String getNamaFailAsal() {
		return namaFailAsal;
	}

	public void setNamaFailAsal(String namaFailAsal) {
		this.namaFailAsal = namaFailAsal;
	}

	public List<ErrorDetail> getErrorDetailList() {
		return errorDetailList;
	}

	public void setErrorDetailList(List<ErrorDetail> errorDetailList) {
		this.errorDetailList = errorDetailList;
	}

}
