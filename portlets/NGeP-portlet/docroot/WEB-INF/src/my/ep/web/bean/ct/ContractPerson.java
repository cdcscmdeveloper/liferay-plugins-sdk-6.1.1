package my.ep.web.bean.ct;

import java.io.Serializable;

public class ContractPerson implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String contractPersonName;
	private String contractAddress;
	private String contractEmail;
	private String contractPhone;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContractPersonName() {
		return contractPersonName;
	}
	public void setContractPersonName(String contractPersonName) {
		this.contractPersonName = "Khan Alam";
	}
	public String getContractAddress() {
		return contractAddress;
	}
	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}
	public String getContractEmail() {
		return contractEmail;
	}
	public void setContractEmail(String contractEmail) {
		this.contractEmail = contractEmail;
	}
	public String getContractPhone() {
		return contractPhone;
	}
	public void setContractPhone(String contractPhone) {
		this.contractPhone = contractPhone;
	}
	
}
