package my.ep.web.vo;

import java.util.List;

import my.ep.bpm.vo.BaseVO;
import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;

public class SmBulkApplicationVO extends BaseVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8322228612623127633L;
	private WorklistItemVO item;
	private boolean overlappedCategory;
	private String remarks;
	private String remarksForOverlapped;
	private SmAppl appl;
	private List<PmStatusDesc> regStatusDescList;

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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRemarksForOverlapped() {
		return remarksForOverlapped;
	}

	public void setRemarksForOverlapped(String remarksForOverlapped) {
		this.remarksForOverlapped = remarksForOverlapped;
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
}
