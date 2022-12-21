package my.ep.web.vo;

import java.io.Serializable;

public class SmPersonnelSummaryCountVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int bumiCount;

	private int nonBumiCount;

	private int frgCount;


	public int getNonBumiCount() {
		return nonBumiCount;
	}

	public void setNonBumiCount(int nonBumiCount) {
		this.nonBumiCount = nonBumiCount;
	}

	public int getFrgCount() {
		return frgCount;
	}

	public void setFrgCount(int frgCount) {
		this.frgCount = frgCount;
	}

	public int getBumiCount() {
		return bumiCount;
	}

	public void setBumiCount(int bumiCount) {
		this.bumiCount = bumiCount;
	}

}
