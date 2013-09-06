package my.ep.web.managedbean.sm.registration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="registrationMB")
@SessionScoped
public class RegistrationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private String regType;
	private String businessType;
	private int userIdentificationType;
	private int userNationality;
	private String mofSuppType;
	
	private String idTypeMasking;

	public String getRegType() {
		return regType;
	}

	public String getIdTypeMasking() {
		return idTypeMasking;
	}

	public void setIdTypeMasking(String idTypeMasking) {
		this.idTypeMasking = idTypeMasking;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public int getUserIdentificationType() {
		return userIdentificationType;
	}

	public void setUserIdentificationType(int userIdentificationType) {
		this.userIdentificationType = userIdentificationType;
	}

	public int getUserNationality() {
		return userNationality;
	}

	public void setUserNationality(int userNationality) {
		this.userNationality = userNationality;
	}

	public String getMofSuppType() {
		return mofSuppType;
	}

	public void setMofSuppType(String mofSuppType) {
		this.mofSuppType = mofSuppType;
	}
	
	
}
