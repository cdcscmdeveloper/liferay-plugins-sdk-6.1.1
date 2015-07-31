package my.ep.web.integration.eterimaan;

import java.util.List;

/*
 * author @Ganga prasad
 */
public class ReceiptInformation {

	private int recordType; // Default Value Zero(0)
	private String agencyId;
	private int totalBillCount; // Bill Header Count

	private List<ReceiptHeader> receiptHeaderList; // Contains minimum 1 record 1:N
	private List<ReceiptDetails> receiptDetailsList; // Contains minimum 1 record 1:N
	private List<BillingInformationLine> billingInformationLineList; // Contains minimum 0 record 0:N

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

	public int getTotalBillCount() {
		return totalBillCount;
	}

	public void setTotalBillCount(int totalBillCount) {
		this.totalBillCount = totalBillCount;
	}

	public List<ReceiptHeader> getReceiptHeaderList() {
		return receiptHeaderList;
	}

	public void setReceiptHeaderList(List<ReceiptHeader> receiptHeaderList) {
		this.receiptHeaderList = receiptHeaderList;
	}

	public List<ReceiptDetails> getReceiptDetailsList() {
		return receiptDetailsList;
	}

	public void setReceiptDetailsList(List<ReceiptDetails> receiptDetailsList) {
		this.receiptDetailsList = receiptDetailsList;
	}

	public List<BillingInformationLine> getBillingInformationLineList() {
		return billingInformationLineList;
	}

	public void setBillingInformationLineList(
			List<BillingInformationLine> billingInformationLineList) {
		this.billingInformationLineList = billingInformationLineList;
	}

}
