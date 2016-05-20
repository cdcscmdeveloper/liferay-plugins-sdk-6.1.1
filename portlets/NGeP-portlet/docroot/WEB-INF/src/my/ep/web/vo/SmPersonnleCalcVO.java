package my.ep.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class SmPersonnleCalcVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int equiOwnrNonBumiCnt;
	private BigDecimal equiOwnrNonBumiPerc;
	private int equiOwnrBumiCnt;
	private BigDecimal equiOwnrBumiPerc;
	private int equiOwnrFrgCnt;
	private BigDecimal equiOwnrFrgPerc;
	private int bodNonBumiCnt;
	private BigDecimal bodNonBumiPerc;
	private int bodBumiCnt;
	private BigDecimal bodBumiPerc;
	private int bodFrgCnt;
	private BigDecimal bodFrgPerc;
	private int hghMngNonBumiCnt;
	private BigDecimal hghMngNonBumiPerc;
	private int hghMngBumiCnt;
	private BigDecimal hghMngBumiPerc;
	private int hghMngFrgCnt;
	private BigDecimal hghMngFrgPerc;
	private int mngtNonBumiCnt;
	private BigDecimal mngtNonBumiPerc;
	private int mngtBumiCnt;
	private BigDecimal mngtBumiPerc;
	private int mngtFrgCnt;
	private BigDecimal mngtFrgPerc;
	private int stfNonBumiEnterCnt;
	private BigDecimal stfNonBumiEnterPerc;
	private int stfBumiEnterCnt;
	private BigDecimal stfBumiEnterPerc;
	private int stfFrgEnterCnt;
	private BigDecimal stfFrgEnterPerc;
	private int profNonBumiEnterCnt;
	private BigDecimal profNonBumiEnterPerc;
	private int profBumiEnterCnt;
	private BigDecimal profBumiEnterPerc;
	private int profFrgEnterCnt;
	private BigDecimal profFrgEnterPerc;
	private int semiNonBumiEnterCnt;
	private BigDecimal semiNonBumiEnterPerc;
	private int semiBumiEnterCnt;
	private BigDecimal semiBumiEnterPerc;
	private int semiFrgEnterCnt;
	private BigDecimal semiFrgEnterPerc;
	private int stfNonBumiActCnt;
	private BigDecimal stfNonBumiActPerc;
	private int stfBumiActCnt;
	private BigDecimal stfBumiActPerc;
	private int stfFrgActCnt;
	private BigDecimal stfFrgActPerc;
	private int profNonBumiActCnt;
	private BigDecimal profNonBumiActPerc;
	private int profBumiActCnt;
	private BigDecimal profBumiActPerc;
	private int profFrgActCnt;
	private BigDecimal profFrgActPerc;
	private int semiNonBumiActCnt;
	private BigDecimal semiNonBumiActPerc;
	private int semiBumiActCnt;
	private BigDecimal semiBumiActPerc;
	private int semiFrgActCnt;
	private BigDecimal semiFrgActPerc;
	private int bodTotalCnt;
	private BigDecimal bodTotalPerc;
	private int hghrMngmtTotalCnt;
	private BigDecimal hghrMngmtTotalPerc;
	private int mngmtTotalCnt;
	private BigDecimal mngmtTotalPerc;
	private int stfTotalCnt;
	private BigDecimal stfTotalPerc;
	private int semiTotalCnt;
	private BigDecimal semiTotalPerc;
	private int profTotalCnt;
	private BigDecimal profTotalPerc;
	private int stfActualTotalCnt;
	private BigDecimal stfActualTotalPerc;
	private int semiActualTotalCnt;
	private BigDecimal semiActualTotalPerc;
	private int profActualTotalCnt;
	private BigDecimal profActualTotalPerc;
	private int equityOwnerTotalCnt;
	private BigDecimal equityOwnerTotalPerc;
	private int totalStaffEntered;
	private int totalKeyRolesEntered;

	private SmPersonnelSummaryCountVO summaryCountVO;

	private BigDecimal totalStaffBumiPerc = new BigDecimal(0);

	private BigDecimal totalKeyRoleBumiPerc = new BigDecimal(0);

	private BigDecimal totalShrBumiShrHldrPerc = new BigDecimal(0);

	private boolean halfBumiStatus;

	private boolean fullBumiStatus;
	
	private boolean bumiStatus;
	
	private String bumiRecommendNote;
	
	private String nonBumiSignatoryBank;

	public int getBodNonBumiCnt() {
		return bodNonBumiCnt;
	}

	public void setBodNonBumiCnt(int bodNonBumiCnt) {
		this.bodNonBumiCnt = bodNonBumiCnt;
	}

	public int getBodBumiCnt() {
		return bodBumiCnt;
	}

	public void setBodBumiCnt(int bodBumiCnt) {
		this.bodBumiCnt = bodBumiCnt;
	}

	public int getBodFrgCnt() {
		return bodFrgCnt;
	}

	public void setBodFrgCnt(int bodFrgCnt) {
		this.bodFrgCnt = bodFrgCnt;
	}

	public int getHghMngNonBumiCnt() {
		return hghMngNonBumiCnt;
	}

	public void setHghMngNonBumiCnt(int hghMngNonBumiCnt) {
		this.hghMngNonBumiCnt = hghMngNonBumiCnt;
	}

	public int getHghMngBumiCnt() {
		return hghMngBumiCnt;
	}

	public void setHghMngBumiCnt(int hghMngBumiCnt) {
		this.hghMngBumiCnt = hghMngBumiCnt;
	}

	public int getHghMngFrgCnt() {
		return hghMngFrgCnt;
	}

	public void setHghMngFrgCnt(int hghMngFrgCnt) {
		this.hghMngFrgCnt = hghMngFrgCnt;
	}

	public int getMngtNonBumiCnt() {
		return mngtNonBumiCnt;
	}

	public void setMngtNonBumiCnt(int mngtNonBumiCnt) {
		this.mngtNonBumiCnt = mngtNonBumiCnt;
	}

	public int getMngtBumiCnt() {
		return mngtBumiCnt;
	}

	public void setMngtBumiCnt(int mngtBumiCnt) {
		this.mngtBumiCnt = mngtBumiCnt;
	}

	public int getMngtFrgCnt() {
		return mngtFrgCnt;
	}

	public void setMngtFrgCnt(int mngtFrgCnt) {
		this.mngtFrgCnt = mngtFrgCnt;
	}

	public int getStfNonBumiEnterCnt() {
		return stfNonBumiEnterCnt;
	}

	public void setStfNonBumiEnterCnt(int stfNonBumiEnterCnt) {
		this.stfNonBumiEnterCnt = stfNonBumiEnterCnt;
	}

	public int getStfBumiEnterCnt() {
		return stfBumiEnterCnt;
	}

	public void setStfBumiEnterCnt(int stfBumiEnterCnt) {
		this.stfBumiEnterCnt = stfBumiEnterCnt;
	}

	public int getStfFrgEnterCnt() {
		return stfFrgEnterCnt;
	}

	public void setStfFrgEnterCnt(int stfFrgEnterCnt) {
		this.stfFrgEnterCnt = stfFrgEnterCnt;
	}

	public int getProfNonBumiEnterCnt() {
		return profNonBumiEnterCnt;
	}

	public void setProfNonBumiEnterCnt(int profNonBumiEnterCnt) {
		this.profNonBumiEnterCnt = profNonBumiEnterCnt;
	}

	public int getProfBumiEnterCnt() {
		return profBumiEnterCnt;
	}

	public void setProfBumiEnterCnt(int profBumiEnterCnt) {
		this.profBumiEnterCnt = profBumiEnterCnt;
	}

	public int getProfFrgEnterCnt() {
		return profFrgEnterCnt;
	}

	public void setProfFrgEnterCnt(int profFrgEnterCnt) {
		this.profFrgEnterCnt = profFrgEnterCnt;
	}

	public int getSemiNonBumiEnterCnt() {
		return semiNonBumiEnterCnt;
	}

	public void setSemiNonBumiEnterCnt(int semiNonBumiEnterCnt) {
		this.semiNonBumiEnterCnt = semiNonBumiEnterCnt;
	}

	public int getSemiBumiEnterCnt() {
		return semiBumiEnterCnt;
	}

	public void setSemiBumiEnterCnt(int semiBumiEnterCnt) {
		this.semiBumiEnterCnt = semiBumiEnterCnt;
	}

	public int getSemiFrgEnterCnt() {
		return semiFrgEnterCnt;
	}

	public void setSemiFrgEnterCnt(int semiFrgEnterCnt) {
		this.semiFrgEnterCnt = semiFrgEnterCnt;
	}

	public int getStfNonBumiActCnt() {
		return stfNonBumiActCnt;
	}

	public void setStfNonBumiActCnt(int stfNonBumiActCnt) {
		this.stfNonBumiActCnt = stfNonBumiActCnt;
	}

	public int getStfBumiActCnt() {
		return stfBumiActCnt;
	}

	public void setStfBumiActCnt(int stfBumiActCnt) {
		this.stfBumiActCnt = stfBumiActCnt;
	}

	public int getStfFrgActCnt() {
		return stfFrgActCnt;
	}

	public void setStfFrgActCnt(int stfFrgActCnt) {
		this.stfFrgActCnt = stfFrgActCnt;
	}

	public int getProfNonBumiActCnt() {
		return profNonBumiActCnt;
	}

	public void setProfNonBumiActCnt(int profNonBumiActCnt) {
		this.profNonBumiActCnt = profNonBumiActCnt;
	}

	public int getProfBumiActCnt() {
		return profBumiActCnt;
	}

	public void setProfBumiActCnt(int profBumiActCnt) {
		this.profBumiActCnt = profBumiActCnt;
	}

	public int getProfFrgActCnt() {
		return profFrgActCnt;
	}

	public void setProfFrgActCnt(int profFrgActCnt) {
		this.profFrgActCnt = profFrgActCnt;
	}

	public int getSemiNonBumiActCnt() {
		return semiNonBumiActCnt;
	}

	public void setSemiNonBumiActCnt(int semiNonBumiActCnt) {
		this.semiNonBumiActCnt = semiNonBumiActCnt;
	}

	public int getSemiBumiActCnt() {
		return semiBumiActCnt;
	}

	public void setSemiBumiActCnt(int semiBumiActCnt) {
		this.semiBumiActCnt = semiBumiActCnt;
	}

	public int getSemiFrgActCnt() {
		return semiFrgActCnt;
	}

	public void setSemiFrgActCnt(int semiFrgActCnt) {
		this.semiFrgActCnt = semiFrgActCnt;
	}

	public int getBodTotalCnt() {
		return bodTotalCnt;
	}

	public void setBodTotalCnt(int bodTotalCnt) {
		this.bodTotalCnt = bodTotalCnt;
	}

	public int getHghrMngmtTotalCnt() {
		return hghrMngmtTotalCnt;
	}

	public void setHghrMngmtTotalCnt(int hghrMngmtTotalCnt) {
		this.hghrMngmtTotalCnt = hghrMngmtTotalCnt;
	}

	public int getMngmtTotalCnt() {
		return mngmtTotalCnt;
	}

	public void setMngmtTotalCnt(int mngmtTotalCnt) {
		this.mngmtTotalCnt = mngmtTotalCnt;
	}

	public int getStfTotalCnt() {
		return stfTotalCnt;
	}

	public void setStfTotalCnt(int stfTotalCnt) {
		this.stfTotalCnt = stfTotalCnt;
	}

	public int getSemiTotalCnt() {
		return semiTotalCnt;
	}

	public void setSemiTotalCnt(int semiTotalCnt) {
		this.semiTotalCnt = semiTotalCnt;
	}

	public int getProfTotalCnt() {
		return profTotalCnt;
	}

	public void setProfTotalCnt(int profTotalCnt) {
		this.profTotalCnt = profTotalCnt;
	}

	public int getTotalStaffEntered() {
		return totalStaffEntered;
	}

	public void setTotalStaffEntered(int totalStaffEntered) {
		this.totalStaffEntered = totalStaffEntered;
	}

	public BigDecimal getTotalStaffBumiPerc() {
		return totalStaffBumiPerc;
	}

	public void setTotalStaffBumiPerc(BigDecimal totalStaffBumiPerc) {
		this.totalStaffBumiPerc = totalStaffBumiPerc;
	}

	public int getTotalKeyRolesEntered() {
		return totalKeyRolesEntered;
	}

	public void setTotalKeyRolesEntered(int totalKeyRolesEntered) {
		this.totalKeyRolesEntered = totalKeyRolesEntered;
	}

	public BigDecimal getTotalKeyRoleBumiPerc() {
		return totalKeyRoleBumiPerc;
	}

	public void setTotalKeyRoleBumiPerc(BigDecimal totalKeyRoleBumiPerc) {
		this.totalKeyRoleBumiPerc = totalKeyRoleBumiPerc;
	}

	public BigDecimal getTotalShrBumiShrHldrPerc() {
		return totalShrBumiShrHldrPerc;
	}

	public void setTotalShrBumiShrHldrPerc(BigDecimal totalShrBumiShrHldrPerc) {
		this.totalShrBumiShrHldrPerc = totalShrBumiShrHldrPerc;
	}

	public boolean isHalfBumiStatus() {
		return halfBumiStatus;
	}

	public void setHalfBumiStatus(boolean halfBumiStatus) {
		this.halfBumiStatus = halfBumiStatus;
	}

	public boolean isFullBumiStatus() {
		return fullBumiStatus;
	}

	public void setFullBumiStatus(boolean fullBumiStatus) {
		this.fullBumiStatus = fullBumiStatus;
	}

	public int getEquiOwnrNonBumiCnt() {
		return equiOwnrNonBumiCnt;
	}

	public void setEquiOwnrNonBumiCnt(int equiOwnrNonBumiCnt) {
		this.equiOwnrNonBumiCnt = equiOwnrNonBumiCnt;
	}

	public int getEquiOwnrBumiCnt() {
		return equiOwnrBumiCnt;
	}

	public void setEquiOwnrBumiCnt(int equiOwnrBumiCnt) {
		this.equiOwnrBumiCnt = equiOwnrBumiCnt;
	}

	public int getEquiOwnrFrgCnt() {
		return equiOwnrFrgCnt;
	}

	public void setEquiOwnrFrgCnt(int equiOwnrFrgCnt) {
		this.equiOwnrFrgCnt = equiOwnrFrgCnt;
	}

	public SmPersonnelSummaryCountVO getSummaryCountVO() {
		return summaryCountVO;
	}

	public void setSummaryCountVO(SmPersonnelSummaryCountVO summaryCountVO) {
		this.summaryCountVO = summaryCountVO;
	}

	public int getEquityOwnerTotalCnt() {
		return equityOwnerTotalCnt;
	}

	public void setEquityOwnerTotalCnt(int equityOwnerTotalCnt) {
		this.equityOwnerTotalCnt = equityOwnerTotalCnt;
	}

	public int getStfActualTotalCnt() {
		return stfActualTotalCnt;
	}

	public void setStfActualTotalCnt(int stfActualTotalCnt) {
		this.stfActualTotalCnt = stfActualTotalCnt;
	}

	public int getSemiActualTotalCnt() {
		return semiActualTotalCnt;
	}

	public void setSemiActualTotalCnt(int semiActualTotalCnt) {
		this.semiActualTotalCnt = semiActualTotalCnt;
	}

	public int getProfActualTotalCnt() {
		return profActualTotalCnt;
	}

	public void setProfActualTotalCnt(int profActualTotalCnt) {
		this.profActualTotalCnt = profActualTotalCnt;
	}

	public String getBumiRecommendNote() {
		return bumiRecommendNote;
	}

	public void setBumiRecommendNote(String bumiRecommendNote) {
		this.bumiRecommendNote = bumiRecommendNote;
	}

	public boolean isBumiStatus() {
		return bumiStatus;
	}

	public String getNonBumiSignatoryBank() {
		return nonBumiSignatoryBank;
	}

	public void setNonBumiSignatoryBank(String nonBumiSignatoryBank) {
		this.nonBumiSignatoryBank = nonBumiSignatoryBank;
	}

	public void setBumiStatus(boolean bumiStatus) {
		this.bumiStatus = bumiStatus;
	}

	public BigDecimal getEquiOwnrNonBumiPerc() {
		return equiOwnrNonBumiPerc;
	}

	public void setEquiOwnrNonBumiPerc(BigDecimal equiOwnrNonBumiPerc) {
		this.equiOwnrNonBumiPerc = equiOwnrNonBumiPerc;
	}

	public BigDecimal getEquiOwnrBumiPerc() {
		return equiOwnrBumiPerc;
	}

	public void setEquiOwnrBumiPerc(BigDecimal equiOwnrBumiPerc) {
		this.equiOwnrBumiPerc = equiOwnrBumiPerc;
	}

	public BigDecimal getEquiOwnrFrgPerc() {
		return equiOwnrFrgPerc;
	}

	public void setEquiOwnrFrgPerc(BigDecimal equiOwnrFrgPerc) {
		this.equiOwnrFrgPerc = equiOwnrFrgPerc;
	}

	public BigDecimal getBodNonBumiPerc() {
		return bodNonBumiPerc;
	}

	public void setBodNonBumiPerc(BigDecimal bodNonBumiPerc) {
		this.bodNonBumiPerc = bodNonBumiPerc;
	}

	public BigDecimal getBodBumiPerc() {
		return bodBumiPerc;
	}

	public void setBodBumiPerc(BigDecimal bodBumiPerc) {
		this.bodBumiPerc = bodBumiPerc;
	}

	public BigDecimal getBodFrgPerc() {
		return bodFrgPerc;
	}

	public void setBodFrgPerc(BigDecimal bodFrgPerc) {
		this.bodFrgPerc = bodFrgPerc;
	}

	public BigDecimal getHghMngNonBumiPerc() {
		return hghMngNonBumiPerc;
	}

	public void setHghMngNonBumiPerc(BigDecimal hghMngNonBumiPerc) {
		this.hghMngNonBumiPerc = hghMngNonBumiPerc;
	}

	public BigDecimal getHghMngBumiPerc() {
		return hghMngBumiPerc;
	}

	public void setHghMngBumiPerc(BigDecimal hghMngBumiPerc) {
		this.hghMngBumiPerc = hghMngBumiPerc;
	}

	public BigDecimal getHghMngFrgPerc() {
		return hghMngFrgPerc;
	}

	public void setHghMngFrgPerc(BigDecimal hghMngFrgPerc) {
		this.hghMngFrgPerc = hghMngFrgPerc;
	}

	public BigDecimal getMngtNonBumiPerc() {
		return mngtNonBumiPerc;
	}

	public void setMngtNonBumiPerc(BigDecimal mngtNonBumiPerc) {
		this.mngtNonBumiPerc = mngtNonBumiPerc;
	}

	public BigDecimal getMngtBumiPerc() {
		return mngtBumiPerc;
	}

	public void setMngtBumiPerc(BigDecimal mngtBumiPerc) {
		this.mngtBumiPerc = mngtBumiPerc;
	}

	public BigDecimal getMngtFrgPerc() {
		return mngtFrgPerc;
	}

	public void setMngtFrgPerc(BigDecimal mngtFrgPerc) {
		this.mngtFrgPerc = mngtFrgPerc;
	}

	public BigDecimal getStfNonBumiEnterPerc() {
		return stfNonBumiEnterPerc;
	}

	public void setStfNonBumiEnterPerc(BigDecimal stfNonBumiEnterPerc) {
		this.stfNonBumiEnterPerc = stfNonBumiEnterPerc;
	}

	public BigDecimal getStfBumiEnterPerc() {
		return stfBumiEnterPerc;
	}

	public void setStfBumiEnterPerc(BigDecimal stfBumiEnterPerc) {
		this.stfBumiEnterPerc = stfBumiEnterPerc;
	}

	public BigDecimal getStfFrgEnterPerc() {
		return stfFrgEnterPerc;
	}

	public void setStfFrgEnterPerc(BigDecimal stfFrgEnterPerc) {
		this.stfFrgEnterPerc = stfFrgEnterPerc;
	}

	public BigDecimal getProfNonBumiEnterPerc() {
		return profNonBumiEnterPerc;
	}

	public void setProfNonBumiEnterPerc(BigDecimal profNonBumiEnterPerc) {
		this.profNonBumiEnterPerc = profNonBumiEnterPerc;
	}

	public BigDecimal getProfBumiEnterPerc() {
		return profBumiEnterPerc;
	}

	public void setProfBumiEnterPerc(BigDecimal profBumiEnterPerc) {
		this.profBumiEnterPerc = profBumiEnterPerc;
	}

	public BigDecimal getProfFrgEnterPerc() {
		return profFrgEnterPerc;
	}

	public void setProfFrgEnterPerc(BigDecimal profFrgEnterPerc) {
		this.profFrgEnterPerc = profFrgEnterPerc;
	}

	public BigDecimal getSemiNonBumiEnterPerc() {
		return semiNonBumiEnterPerc;
	}

	public void setSemiNonBumiEnterPerc(BigDecimal semiNonBumiEnterPerc) {
		this.semiNonBumiEnterPerc = semiNonBumiEnterPerc;
	}

	public BigDecimal getSemiBumiEnterPerc() {
		return semiBumiEnterPerc;
	}

	public void setSemiBumiEnterPerc(BigDecimal semiBumiEnterPerc) {
		this.semiBumiEnterPerc = semiBumiEnterPerc;
	}

	public BigDecimal getSemiFrgEnterPerc() {
		return semiFrgEnterPerc;
	}

	public void setSemiFrgEnterPerc(BigDecimal semiFrgEnterPerc) {
		this.semiFrgEnterPerc = semiFrgEnterPerc;
	}

	public BigDecimal getStfNonBumiActPerc() {
		return stfNonBumiActPerc;
	}

	public void setStfNonBumiActPerc(BigDecimal stfNonBumiActPerc) {
		this.stfNonBumiActPerc = stfNonBumiActPerc;
	}

	public BigDecimal getStfBumiActPerc() {
		return stfBumiActPerc;
	}

	public void setStfBumiActPerc(BigDecimal stfBumiActPerc) {
		this.stfBumiActPerc = stfBumiActPerc;
	}

	public BigDecimal getStfFrgActPerc() {
		return stfFrgActPerc;
	}

	public void setStfFrgActPerc(BigDecimal stfFrgActPerc) {
		this.stfFrgActPerc = stfFrgActPerc;
	}

	public BigDecimal getProfNonBumiActPerc() {
		return profNonBumiActPerc;
	}

	public void setProfNonBumiActPerc(BigDecimal profNonBumiActPerc) {
		this.profNonBumiActPerc = profNonBumiActPerc;
	}

	public BigDecimal getProfBumiActPerc() {
		return profBumiActPerc;
	}

	public void setProfBumiActPerc(BigDecimal profBumiActPerc) {
		this.profBumiActPerc = profBumiActPerc;
	}

	public BigDecimal getProfFrgActPerc() {
		return profFrgActPerc;
	}

	public void setProfFrgActPerc(BigDecimal profFrgActPerc) {
		this.profFrgActPerc = profFrgActPerc;
	}

	public BigDecimal getSemiNonBumiActPerc() {
		return semiNonBumiActPerc;
	}

	public void setSemiNonBumiActPerc(BigDecimal semiNonBumiActPerc) {
		this.semiNonBumiActPerc = semiNonBumiActPerc;
	}

	public BigDecimal getSemiBumiActPerc() {
		return semiBumiActPerc;
	}

	public void setSemiBumiActPerc(BigDecimal semiBumiActPerc) {
		this.semiBumiActPerc = semiBumiActPerc;
	}

	public BigDecimal getSemiFrgActPerc() {
		return semiFrgActPerc;
	}

	public void setSemiFrgActPerc(BigDecimal semiFrgActPerc) {
		this.semiFrgActPerc = semiFrgActPerc;
	}

	public BigDecimal getBodTotalPerc() {
		return bodTotalPerc;
	}

	public void setBodTotalPerc(BigDecimal bodTotalPerc) {
		this.bodTotalPerc = bodTotalPerc;
	}

	public BigDecimal getHghrMngmtTotalPerc() {
		return hghrMngmtTotalPerc;
	}

	public void setHghrMngmtTotalPerc(BigDecimal hghrMngmtTotalPerc) {
		this.hghrMngmtTotalPerc = hghrMngmtTotalPerc;
	}

	public BigDecimal getMngmtTotalPerc() {
		return mngmtTotalPerc;
	}

	public void setMngmtTotalPerc(BigDecimal mngmtTotalPerc) {
		this.mngmtTotalPerc = mngmtTotalPerc;
	}

	public BigDecimal getStfTotalPerc() {
		return stfTotalPerc;
	}

	public void setStfTotalPerc(BigDecimal stfTotalPerc) {
		this.stfTotalPerc = stfTotalPerc;
	}

	public BigDecimal getSemiTotalPerc() {
		return semiTotalPerc;
	}

	public void setSemiTotalPerc(BigDecimal semiTotalPerc) {
		this.semiTotalPerc = semiTotalPerc;
	}

	public BigDecimal getProfTotalPerc() {
		return profTotalPerc;
	}

	public void setProfTotalPerc(BigDecimal profTotalPerc) {
		this.profTotalPerc = profTotalPerc;
	}

	public BigDecimal getStfActualTotalPerc() {
		return stfActualTotalPerc;
	}

	public void setStfActualTotalPerc(BigDecimal stfActualTotalPerc) {
		this.stfActualTotalPerc = stfActualTotalPerc;
	}

	public BigDecimal getSemiActualTotalPerc() {
		return semiActualTotalPerc;
	}

	public void setSemiActualTotalPerc(BigDecimal semiActualTotalPerc) {
		this.semiActualTotalPerc = semiActualTotalPerc;
	}

	public BigDecimal getProfActualTotalPerc() {
		return profActualTotalPerc;
	}

	public void setProfActualTotalPerc(BigDecimal profActualTotalPerc) {
		this.profActualTotalPerc = profActualTotalPerc;
	}

	public BigDecimal getEquityOwnerTotalPerc() {
		return equityOwnerTotalPerc;
	}

	public void setEquityOwnerTotalPerc(BigDecimal equityOwnerTotalPerc) {
		this.equityOwnerTotalPerc = equityOwnerTotalPerc;
	}

}
