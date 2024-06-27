package my.ep.web.managedbean.sc.loa;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.web.managedbean.BaseManagedBean;

public abstract class LOABaseManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private PmParameter selectedOrgType;

	private PmOrgProfile selectedPtj;

	private ScLoa scLoa;
	
	private String bondReqSelectedVal;
	
	private String agreemetSelectedVal;
	
	private String yesParameterId;
	
	private String noParameterId;
	
	private Long periodicAsAndWhenParamId;
	
	private Long periodicScheduleParamId;
	//20230111@Haziq - #12487&29693
	private PmUserOrg selectedUserPtj;

	public PmParameter getSelectedOrgType() {
		if (this.selectedOrgType == null) {
			this.selectedOrgType = new PmParameter();
		}
		return selectedOrgType;
	}

	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	public PmOrgProfile getSelectedPtj() {
		return selectedPtj;
	}

	public void setSelectedPtj(PmOrgProfile selectedPtj) {
		this.selectedPtj = selectedPtj;
	}

	public ScLoa getScLoa() {
		return scLoa;
	}

	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	

	/**
	 * @return the bondReqSelectedVal
	 */
	public String getBondReqSelectedVal() {
		return bondReqSelectedVal;
	}

	/**
	 * @param bondReqSelectedVal
	 *            the bondReqSelectedVal to set
	 */
	public void setBondReqSelectedVal(String bondReqSelectedVal) {
		this.bondReqSelectedVal = bondReqSelectedVal;
	}
	
	/**
	 * @return the agreemetSelectedVal
	 */
	public String getAgreemetSelectedVal() {
		return agreemetSelectedVal;
	}

	/**
	 * @param agreemetSelectedVal
	 *            the agreemetSelectedVal to set
	 */
	public void setAgreemetSelectedVal(String agreemetSelectedVal) {
		this.agreemetSelectedVal = agreemetSelectedVal;
	}

	public String getYesParameterId() {
		return yesParameterId;
	}

	public void setYesParameterId(String yesParameterId) {
		this.yesParameterId = yesParameterId;
	}

	public String getNoParameterId() {
		return noParameterId;
	}

	public void setNoParameterId(String noParameterId) {
		this.noParameterId = noParameterId;
	}

	public Long getPeriodicAsAndWhenParamId() {
		return periodicAsAndWhenParamId;
	}

	public void setPeriodicAsAndWhenParamId(Long periodicAsAndWhenParamId) {
		this.periodicAsAndWhenParamId = periodicAsAndWhenParamId;
	}

	public Long getPeriodicScheduleParamId() {
		return periodicScheduleParamId;
	}

	public void setPeriodicScheduleParamId(Long periodicScheduleParamId) {
		this.periodicScheduleParamId = periodicScheduleParamId;
	}

	public PmUserOrg getSelectedUserPtj() {
		return selectedUserPtj;
	}

	public void setSelectedUserPtj(PmUserOrg selectedUserPtj) {
		this.selectedUserPtj = selectedUserPtj;
	}
	
}
