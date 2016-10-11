package my.ep.web.vo;

import my.ep.bpm.vo.BaseVO;

public class SmOverlapCategoryVO extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2057751152396626408L;
	private String overLappedCategoryMsg;
	private String overLappedCategoryRemarks;
	private boolean overLappedCategoryExist;

	public String getOverLappedCategoryMsg() {
		return overLappedCategoryMsg;
	}

	public void setOverLappedCategoryMsg(String overLappedCategoryMsg) {
		this.overLappedCategoryMsg = overLappedCategoryMsg;
	}

	public String getOverLappedCategoryRemarks() {
		return overLappedCategoryRemarks;
	}

	public void setOverLappedCategoryRemarks(String overLappedCategoryRemarks) {
		this.overLappedCategoryRemarks = overLappedCategoryRemarks;
	}

	public boolean isOverLappedCategoryExist() {
		return overLappedCategoryExist;
	}

	public void setOverLappedCategoryExist(boolean overLappedCategoryExist) {
		this.overLappedCategoryExist = overLappedCategoryExist;
	}
}
