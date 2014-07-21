package my.ep.web.managedbean.pm.profcertbody;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.web.managedbean.BaseManagedBean;


/**
 * @author DK
 * 
 */
@ManagedBean(name = "profcertifiedbodyManagedBean")
@SessionScoped
public class ProfCertifiedBodyManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// Search Bean 	
	private String bodyCode;
	
	private String bodyName;
	
	private Long certifiedBody;
	
	private Long professionalBody;
	
	private Long status;
	
    // Search Bean List
    private List<PmCertBody> certBodyList; 

	public String getBodyCode() {
		return bodyCode;
	}

	public void setBodyCode(String bodyCode) {
		this.bodyCode = bodyCode;
	}

	public String getBodyName() {
		return bodyName;
	}

	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
	}

	public Long getCertifiedBody() {
		return certifiedBody;
	}

	public void setCertifiedBody(Long certifiedBody) {
		this.certifiedBody = certifiedBody;
	}

	public Long getProfessionalBody() {
		return professionalBody;
	}

	public void setProfessionalBody(Long professionalBody) {
		this.professionalBody = professionalBody;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public List<PmCertBody> getCertBodyList() {
		return certBodyList;
	}

	public void setCertBodyList(List<PmCertBody> certBodyList) {
		this.certBodyList = certBodyList;
	}	
	
}
