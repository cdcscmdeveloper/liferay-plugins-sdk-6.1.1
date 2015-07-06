package my.ep.web.integration.eterimaan;

import java.util.List;

/*
 * author @Ganga prasad
*/

public class BillingInformation {

	private int recordType;  
	private String agencyId;
	private int totalBillCount; // Bill Header Count

	private List<BillHeader> billHeaderList;
	private List<CustomerInformation> customerInformationList;
	private List<ChargeLine> chargeLineList;

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

	public List<BillHeader> getBillHeaderList() {
		return billHeaderList;
	}

	public void setBillHeaderList(List<BillHeader> billHeaderList) {
		this.billHeaderList = billHeaderList;
	}

	public List<CustomerInformation> getCustomerInformationList() {
		return customerInformationList;
	}

	public void setCustomerInformationList(
			List<CustomerInformation> customerInformationList) {
		this.customerInformationList = customerInformationList;
	}

	public List<ChargeLine> getChargeLineList() {
		return chargeLineList;
	}

	public void setChargeLineList(List<ChargeLine> chargeLineList) {
		this.chargeLineList = chargeLineList;
	}

}
