package my.ep.web.integration.crm;

import java.util.List;

public class SupplierInfo {

	private String companyName;
	private String supplierID;

	private List<Branch> branchList;
	private List<Appl> smApplList;
	private List<MainContact> mainContactList;
	private List<Bidang> bidangList;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getSupplierID() {
		return supplierID;
	}

	public void setSupplierID(String supplierID) {
		this.supplierID = supplierID;
	}

	public List<Branch> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<Branch> branchList) {
		this.branchList = branchList;
	}

	public List<Appl> getSmApplList() {
		return smApplList;
	}

	public void setSmApplList(List<Appl> smApplList) {
		this.smApplList = smApplList;
	}

	public List<MainContact> getMainContactList() {
		return mainContactList;
	}

	public void setMainContactList(List<MainContact> mainContactList) {
		this.mainContactList = mainContactList;
	}

	public List<Bidang> getBidangList() {
		return bidangList;
	}

	public void setBidangList(List<Bidang> bidangList) {
		this.bidangList = bidangList;
	}

}
