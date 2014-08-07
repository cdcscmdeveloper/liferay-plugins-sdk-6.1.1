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

	/**
	 * Search Bean 	
	 */ 	
	private String bodyCode;
	
	private String bodyName;
	
	private Long status;

	/**
	 * Search Bean List
	 */
    private List<PmCertBody> certBodyList; 
    
    private PmCertBody certBody;

	/**
	 * @return the bodyCode
	 */
	public String getBodyCode() {
		return bodyCode;
	}

	/**
	 * @param bodyCode the bodyCode to set
	 */
	public void setBodyCode(String bodyCode) {
		this.bodyCode = bodyCode;
	}

	/**
	 * @return the bodyName
	 */
	public String getBodyName() {
		return bodyName;
	}

	/**
	 * @param bodyName the bodyName to set
	 */
	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
	}

	/**
	 * @return the status
	 */
	public Long getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Long status) {
		this.status = status;
	}

	/**
	 * @return the certBodyList
	 */
	public List<PmCertBody> getCertBodyList() {
		return certBodyList;
	}

	/**
	 * @param certBodyList the certBodyList to set
	 */
	public void setCertBodyList(List<PmCertBody> certBodyList) {
		this.certBodyList = certBodyList;
	}

	/**
	 * @return the certBody
	 */
	public PmCertBody getCertBody() {
		return certBody;
	}

	/**
	 * @param certBody the certBody to set
	 */
	public void setCertBody(PmCertBody certBody) {
		this.certBody = certBody;
	}	
	
}
