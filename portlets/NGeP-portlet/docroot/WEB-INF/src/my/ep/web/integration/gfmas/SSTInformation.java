package my.ep.web.integration.gfmas;


public class SSTInformation {

	private int recordType;
	private String SSTNumber;
	private String physicalSSTNumber;
	private String sapVendorCode;
	private String kumpPtj;
	private String sstAcknowledgeDate;

	public int getRecordType() {
		return recordType;
	}

	public void setRecordType(int recordType) {
		this.recordType = recordType;
	}

	public String getSSTNumber() {
		return SSTNumber;
	}

	public void setSSTNumber(String sSTNumber) {
		SSTNumber = sSTNumber;
	}

	public String getPhysicalSSTNumber() {
		return physicalSSTNumber;
	}

	public void setPhysicalSSTNumber(String physicalSSTNumber) {
		this.physicalSSTNumber = physicalSSTNumber;
	}

	public String getSapVendorCode() {
		return sapVendorCode;
	}

	public void setSapVendorCode(String sapVendorCode) {
		this.sapVendorCode = sapVendorCode;
	}

	public String getKumpPtj() {
		return kumpPtj;
	}

	public void setKumpPtj(String kumpPtj) {
		this.kumpPtj = kumpPtj;
	}

	public String getSstAcknowledgeDate() {
		return sstAcknowledgeDate;
	}

	public void setSstAcknowledgeDate(String sstAcknowledgeDate) {
		this.sstAcknowledgeDate = sstAcknowledgeDate;
	}

}
