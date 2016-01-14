package my.ep.web.vo;

import java.io.Serializable;
import java.util.Date;

public class ReactivateSupplierVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2425257792270440147L;
	private String identificationNo;
	private Long identificationType;
	private Date establishDate;
	private String regNo;
	private String newName;
	private String newIdentificationNo;
	private Long newIdentificationType;
	private boolean showEmailInput;
	private String email;

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
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

	public Long getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(Long identificationType) {
		this.identificationType = identificationType;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewIdentificationNo() {
		return newIdentificationNo;
	}

	public void setNewIdentificationNo(String newIdentificationNo) {
		this.newIdentificationNo = newIdentificationNo;
	}

	public Long getNewIdentificationType() {
		return newIdentificationType;
	}

	public void setNewIdentificationType(Long newIdentificationType) {
		this.newIdentificationType = newIdentificationType;
	}

	public boolean isShowEmailInput() {
		return showEmailInput;
	}

	public void setShowEmailInput(boolean showEmailInput) {
		this.showEmailInput = showEmailInput;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
