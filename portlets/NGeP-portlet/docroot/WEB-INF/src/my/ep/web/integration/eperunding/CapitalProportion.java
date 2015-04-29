package my.ep.web.integration.eperunding;

import java.math.BigDecimal;

public class CapitalProportion {

	private BigDecimal bumi;
	private BigDecimal nonBumi;
	private BigDecimal nonMalaysian;
	private BigDecimal govAgency;
	private BigDecimal govEquity;
	private BigDecimal localCo;
	private BigDecimal foreignCo;
	private BigDecimal totalMember;
	private BigDecimal totalShare;

	public BigDecimal getBumi() {
		return bumi;
	}

	public void setBumi(BigDecimal bumi) {
		this.bumi = bumi;
	}

	public BigDecimal getNonBumi() {
		return nonBumi;
	}

	public void setNonBumi(BigDecimal nonBumi) {
		this.nonBumi = nonBumi;
	}

	public BigDecimal getNonMalaysian() {
		return nonMalaysian;
	}

	public void setNonMalaysian(BigDecimal nonMalaysian) {
		this.nonMalaysian = nonMalaysian;
	}

	public BigDecimal getGovAgency() {
		return govAgency;
	}

	public void setGovAgency(BigDecimal govAgency) {
		this.govAgency = govAgency;
	}

	public BigDecimal getGovEquity() {
		return govEquity;
	}

	public void setGovEquity(BigDecimal govEquity) {
		this.govEquity = govEquity;
	}

	public BigDecimal getLocalCo() {
		return localCo;
	}

	public void setLocalCo(BigDecimal localCo) {
		this.localCo = localCo;
	}

	public BigDecimal getForeignCo() {
		return foreignCo;
	}

	public void setForeignCo(BigDecimal foreignCo) {
		this.foreignCo = foreignCo;
	}

	public BigDecimal getTotalMember() {
		return totalMember;
	}

	public void setTotalMember(BigDecimal totalMember) {
		this.totalMember = totalMember;
	}

	public BigDecimal getTotalShare() {
		return totalShare;
	}

	public void setTotalShare(BigDecimal totalShare) {
		this.totalShare = totalShare;
	}

}
