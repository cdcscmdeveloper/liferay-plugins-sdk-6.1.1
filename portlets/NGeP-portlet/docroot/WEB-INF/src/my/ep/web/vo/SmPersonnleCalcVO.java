package my.ep.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class SmPersonnleCalcVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int equiOwnrNonBumiCnt;

	private int equiOwnrBumiCnt;

	private int equiOwnrFrgCnt;

	private int bodNonBumiCnt;

	private int bodBumiCnt;

	private int bodFrgCnt;

	private int hghMngNonBumiCnt;

	private int hghMngBumiCnt;

	private int hghMngFrgCnt;

	private int mngtNonBumiCnt;

	private int mngtBumiCnt;

	private int mngtFrgCnt;

	private int stfNonBumiEnterCnt;

	private int stfBumiEnterCnt;

	private int stfFrgEnterCnt;

	private int profNonBumiEnterCnt;

	private int profBumiEnterCnt;

	private int profFrgEnterCnt;

	private int semiNonBumiEnterCnt;

	private int semiBumiEnterCnt;

	private int semiFrgEnterCnt;

	private int stfNonBumiActCnt;

	private int stfBumiActCnt;

	private int stfFrgActCnt;

	private int profNonBumiActCnt;

	private int profBumiActCnt;

	private int profFrgActCnt;

	private int semiNonBumiActCnt;

	private int semiBumiActCnt;

	private int semiFrgActCnt;

	private int bodTotalCnt;

	private int hghrMngmtTotalCnt;

	private int mngmtTotalCnt;

	private int stfTotalCnt;

	private int semiTotalCnt;

	private int profTotalCnt;
	
	private int stfActualTotalCnt;

	private int semiActualTotalCnt;

	private int profActualTotalCnt;

	private int equityOwnerTotalCnt;

	private int totalStaffEntered;

	private int totalKeyRolesEntered;

	private SmPersonnelSummaryCountVO summaryCountVO;

	private BigDecimal totalStaffBumiPerc = new BigDecimal(0);

	private BigDecimal totalKeyRoleBumiPerc = new BigDecimal(0);

	private BigDecimal totalShrBumiShrHldrPerc = new BigDecimal(0);

	private boolean halfBumiStatus;

	private boolean fullBumiStatus;

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

}
