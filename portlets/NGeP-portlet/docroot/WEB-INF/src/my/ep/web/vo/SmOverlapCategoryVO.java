package my.ep.web.vo;

import java.util.List;

import my.ep.bpm.vo.BaseVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;

public class SmOverlapCategoryVO extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2057751152396626408L;
	private String overLappedCategoryMsg;
	private String overLappedCategoryRemarks;
	private boolean overLappedCategoryExist;
	private List<SmBulkApplOverlapCatVO> overlapCategoryVOList;
	//13362
	private String applnOverLapRemarks;
	private List<SmBulkApplOverlapCatVO> overlapCategoryMalayList;

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

	public List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList() {
		return overlapCategoryVOList;
	}

	public void setOverlapCategoryVOList(
			List<SmBulkApplOverlapCatVO> overlapCategoryVOList) {
		this.overlapCategoryVOList = overlapCategoryVOList;
	}

	//13362 - START
	public String getApplnOverLapRemarks() {
		return applnOverLapRemarks;
	}

	public void setApplnOverLapRemarks(String applnOverLapRemarks) {
		this.applnOverLapRemarks = applnOverLapRemarks;
	}

	public List<SmBulkApplOverlapCatVO> getOverlapCategoryMalayList() {
		return overlapCategoryMalayList;
	}

	public void setOverlapCategoryMalayList(List<SmBulkApplOverlapCatVO> overlapCategoryMalayList) {
		this.overlapCategoryMalayList = overlapCategoryMalayList;
	}
	//13362 - END
}
