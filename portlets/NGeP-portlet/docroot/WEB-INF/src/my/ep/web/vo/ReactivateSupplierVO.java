package my.ep.web.vo;

import java.io.Serializable;
import java.util.Date;

public class ReactivateSupplierVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2425257792270440147L;
	private String identificationNo;
	private String email;
	private Date establishDate;
	private String regNo;

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getEstablishDate() {
		return establishDate;
	}

	public void setEstablishDate(Date establishDate) {
		this.establishDate = establishDate;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
}
