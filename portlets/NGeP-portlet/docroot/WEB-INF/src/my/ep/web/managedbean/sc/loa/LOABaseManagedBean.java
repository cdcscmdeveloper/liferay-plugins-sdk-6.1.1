package my.ep.web.managedbean.sc.loa;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.web.managedbean.BaseManagedBean;

public abstract class LOABaseManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private Long orgTypeId;

	private PmParameter selectedOrgType;

	private PmOrgProfile selectedPtj;

	private ScLoa scLoa;

	public Long getOrgTypeId() {
		return orgTypeId;
	}

	public void setOrgTypeId(Long orgTypeId) {
		this.orgTypeId = orgTypeId;
	}

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

}
