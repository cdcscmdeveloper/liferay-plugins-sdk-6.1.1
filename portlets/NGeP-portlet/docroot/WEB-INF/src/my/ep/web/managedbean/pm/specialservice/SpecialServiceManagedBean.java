package my.ep.web.managedbean.pm.specialservice;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The Class SpecialServiceManagedBean.
 * 
 * @author wei lurn
 *
 */
@ManagedBean(name = "SpecialServiceManagedBean")
@SessionScoped
public class SpecialServiceManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	private PmParameterDesc pmSpecialService;

	private PmParameterDesc oldPmSpecialService;

	private PmParameter pmParameter;

	private List<PmParameterDesc> specialService;

	private String codeName;

	private Long status;

	private boolean isLazy = false;

	private String parameterType;

	public SpecialServiceManagedBean() {}

	public PmParameterDesc getPmSpecialService() {
		return pmSpecialService;
	}

	public void setPmSpecialService(PmParameterDesc pmSpecialService) {
		this.pmSpecialService = pmSpecialService;
	}

	public PmParameterDesc getOldPmSpecialService() {
		return oldPmSpecialService;
	}

	public void setOldPmSpecialService(PmParameterDesc oldPmSpecialService) {
		this.oldPmSpecialService = oldPmSpecialService;
	}

	/**
	 * Gets the pm parameter.
	 * 
	 * @return the pmParameter
	 */
	public PmParameter getPmParameter() {
		return pmParameter;
	}

	public void setPmParameter(PmParameter pmParameter) {
		this.pmParameter = pmParameter;
	}

	public List<PmParameterDesc> getSpecialService() {
		return specialService;
	}

	public void setSpecialService(List<PmParameterDesc> specialService) {
		this.specialService = specialService;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public boolean isLazy() {
		return isLazy;
	}

	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}
} //#29644