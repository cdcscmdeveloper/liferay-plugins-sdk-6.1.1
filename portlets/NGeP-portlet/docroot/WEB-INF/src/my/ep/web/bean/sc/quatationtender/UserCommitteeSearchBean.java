package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;

/**
 * @author Viswanath
 * 
 */
public class UserCommitteeSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String identificationNo;
	private String identificationType;

	public UserCommitteeSearchBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public String getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(String identificationType) {
		this.identificationType = identificationType;
	}

}
