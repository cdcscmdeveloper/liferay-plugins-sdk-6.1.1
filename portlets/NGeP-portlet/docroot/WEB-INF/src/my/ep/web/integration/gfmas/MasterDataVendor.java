package my.ep.web.integration.gfmas;

import java.util.List;

/*
 *@author P Ganga Prasad
 */
public class MasterDataVendor {

	private int recordType;
	private String agencyId;
	private String vendorIdCode;
	private String entityCode;

	private List<CompanyBranch> companyBranchs;

	// for apove file
	private String sapVendorCode;
	private CompanyBranch companyBranch = new CompanyBranch();
	List<BankAccountInformation> bankAccountInfoList;

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

	public String getVendorIdCode() {
		return vendorIdCode;
	}

	public void setVendorIdCode(String vendorIdCode) {
		this.vendorIdCode = vendorIdCode;
	}

	public String getEntityCode() {
		return entityCode;
	}

	public void setEntityCode(String entityCode) {
		this.entityCode = entityCode;
	}

	public String getSapVendorCode() {
		return sapVendorCode;
	}

	public void setSapVendorCode(String sapVendorCode) {
		this.sapVendorCode = sapVendorCode;
	}

	public CompanyBranch getCompanyBranch() {
		return companyBranch;
	}

	public void setCompanyBranch(CompanyBranch companyBranch) {
		this.companyBranch = companyBranch;
	}

	public List<CompanyBranch> getCompanyBranchs() {
		return companyBranchs;
	}

	public void setCompanyBranchs(List<CompanyBranch> companyBranchs) {
		this.companyBranchs = companyBranchs;
	}

	public List<BankAccountInformation> getBankAccountInfoList() {
		return bankAccountInfoList;
	}

	public void setBankAccountInfoList(
			List<BankAccountInformation> bankAccountInfoList) {
		this.bankAccountInfoList = bankAccountInfoList;
	}

}
