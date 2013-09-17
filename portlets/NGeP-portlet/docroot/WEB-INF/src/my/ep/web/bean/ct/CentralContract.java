package my.ep.web.bean.ct;

import java.io.Serializable;

public class CentralContract implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String contractNo;
	private String circularNo;

	public String getCircularNo() {
		return circularNo;
	}

	public void setCircularNo(String circularNo) {
		this.circularNo = circularNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
