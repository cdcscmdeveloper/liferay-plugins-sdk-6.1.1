package my.ep.web.backingbean.sc.supplierproposal;

import java.util.List;

import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.sm.SmPersonnel;

public class TechnicalStaffBean {
	private SmPersonnel smPersonnel;
	private List<PmParameterDesc> smStaffAcademics;
	
	public TechnicalStaffBean(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	/**
	 * @return the smPersonnel
	 */
	public SmPersonnel getSmPersonnel() {
		return smPersonnel;
	}

	/**
	 * @param smPersonnel the smPersonnel to set
	 */
	public void setSmPersonnel(SmPersonnel smPersonnel) {
		this.smPersonnel = smPersonnel;
	}

	/**
	 * @return the smStaffAcademics
	 */
	public List<PmParameterDesc> getSmStaffAcademics() {
		return smStaffAcademics;
	}

	/**
	 * @param smStaffAcademics the smStaffAcademics to set
	 */
	public void setSmStaffAcademics(List<PmParameterDesc> smStaffAcademics) {
		this.smStaffAcademics = smStaffAcademics;
	}
	
}
