package my.ep.web.vo;

import java.util.List;

import my.ep.bpm.vo.BaseVO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmFacility;

public class SmBulkApplicationVO extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8322228612623127633L;
	private WorklistItemVO item;
	private boolean overlappedCategory;
	private String approvalRemarks;
	private String overlapRemarks;
	private SmAppl appl;
	private List<PmStatusDesc> regStatusDescList;
	private String currRegStatus;
	private Long approvedStatusId;
	private boolean changeCurrRegStatus;
	private SmFacility hqFacility;

	public boolean isOverlappedCategory() {
		return overlappedCategory;
	}

	public void setOverlappedCategory(boolean overlappedCategory) {
		this.overlappedCategory = overlappedCategory;
	}

	public WorklistItemVO getItem() {
		return item;
	}

	public void setItem(WorklistItemVO item) {
		this.item = item;
	}

	public String getApprovalRemarks() {
		return approvalRemarks;
	}

	public void setApprovalRemarks(String approvalRemarks) {
		this.approvalRemarks = approvalRemarks;
	}

	public String getOverlapRemarks() {
		return overlapRemarks;
	}

	public void setOverlapRemarks(String overlapRemarks) {
		this.overlapRemarks = overlapRemarks;
	}

	public SmAppl getAppl() {
		return appl;
	}

	public void setAppl(SmAppl appl) {
		this.appl = appl;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}

	public String getCurrRegStatus() {
		return currRegStatus;
	}

	public void setCurrRegStatus(String currRegStatus) {
		this.currRegStatus = currRegStatus;
	}

	public Long getApprovedStatusId() {
		return approvedStatusId;
	}

	public void setApprovedStatusId(Long approvedStatusId) {
		this.approvedStatusId = approvedStatusId;
	}

	public boolean isChangeCurrRegStatus() {
		return changeCurrRegStatus;
	}

	public void setChangeCurrRegStatus(boolean changeCurrRegStatus) {
		this.changeCurrRegStatus = changeCurrRegStatus;
	}

	public SmFacility getHqFacility() {
		return hqFacility;
	}

	public void setHqFacility(SmFacility hqFacility) {
		this.hqFacility = hqFacility;
	}
}
