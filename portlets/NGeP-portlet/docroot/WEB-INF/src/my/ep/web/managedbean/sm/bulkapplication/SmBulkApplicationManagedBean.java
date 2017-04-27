package my.ep.web.managedbean.sm.bulkapplication;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.dto.sm.bulkApplication.SmBulkApplicationVO;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "bulkApplicationMB")
@SessionScoped
public class SmBulkApplicationManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = -7139722023908927672L;
	private List<SmBulkApplicationVO> selectedTasks;
	private List<SmBulkApplicationVO> selectedUnclaimTasks;
	private List<SmBulkApplicationVO> claimTasks;
	private List<SmBulkApplicationVO> unClaimTasks;
	private List<PmStatusDesc> regStatusDescList;
	private List<SmAppl> failedSubmittedList;
	private List<SmBulkApplOverlapCatVO> selectedOverlapCategoryVOList;
	private List<PmReasonDesc> reasonDescList;
	private String maxBulkApprovalMsg;
	private int maxBulkApprovalCount;
	private String page;
	private String applStatus;
	private static final String RECOMMEND_APPROVE = "APPROVE";
	private static final String RECOMMEND_REJECT = "REJECT";

	public SmBulkApplicationManagedBean() {
		claimTasks = new ArrayList<SmBulkApplicationVO>();
		unClaimTasks = new ArrayList<SmBulkApplicationVO>();
	}

	public List<SmBulkApplicationVO> getSelectedTasks() {
		return selectedTasks;
	}

	public void setSelectedTasks(List<SmBulkApplicationVO> selectedTasks) {
		this.selectedTasks = selectedTasks;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}

	public List<SmAppl> getFailedSubmittedList() {
		return failedSubmittedList;
	}

	public void setFailedSubmittedList(List<SmAppl> failedSubmittedList) {
		this.failedSubmittedList = failedSubmittedList;
	}

	public int getMaxBulkApprovalCount() {
		return maxBulkApprovalCount;
	}

	public void setMaxBulkApprovalCount(int maxBulkApprovalCount) {
		this.maxBulkApprovalCount = maxBulkApprovalCount;
	}

	public List<SmBulkApplicationVO> getClaimTasks() {
		return claimTasks;
	}

	public void setClaimTasks(List<SmBulkApplicationVO> claimTasks) {
		this.claimTasks = claimTasks;
	}

	public List<SmBulkApplicationVO> getSelectedUnclaimTasks() {
		return selectedUnclaimTasks;
	}

	public void setSelectedUnclaimTasks(
			List<SmBulkApplicationVO> selectedUnclaimTasks) {
		this.selectedUnclaimTasks = selectedUnclaimTasks;
	}

	public String getMaxBulkApprovalMsg() {
		return maxBulkApprovalMsg;
	}

	public void setMaxBulkApprovalMsg(String maxBulkApprovalMsg) {
		this.maxBulkApprovalMsg = maxBulkApprovalMsg;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<SmBulkApplOverlapCatVO> getSelectedOverlapCategoryVOList() {
		return selectedOverlapCategoryVOList;
	}

	public void setSelectedOverlapCategoryVOList(
			List<SmBulkApplOverlapCatVO> selectedOverlapCategoryVOList) {
		this.selectedOverlapCategoryVOList = selectedOverlapCategoryVOList;
	}

	public List<PmReasonDesc> getReasonDescList() {
		return reasonDescList;
	}

	public void setReasonDescList(List<PmReasonDesc> reasonDescList) {
		this.reasonDescList = reasonDescList;
	}

	public List<SmBulkApplicationVO> getUnClaimTasks() {
		return unClaimTasks;
	}

	public void setUnClaimTasks(List<SmBulkApplicationVO> unClaimTasks) {
		this.unClaimTasks = unClaimTasks;
	}

	public String getApplStatus() {
		return applStatus;
	}

	public void setApplStatus(String applStatus) {
		this.applStatus = applStatus;
	}
	
	public String getRECOMMEND_APPROVE(){
		return RECOMMEND_APPROVE;
	}
	
	public String getRECOMMEND_REJECT(){
		return RECOMMEND_REJECT;
	}
}
