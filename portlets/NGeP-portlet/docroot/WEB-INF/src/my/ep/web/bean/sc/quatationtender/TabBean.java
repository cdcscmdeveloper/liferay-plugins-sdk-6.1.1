package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;

public class TabBean {
	private String titleName;
	private ScQtApproval scQtApproval;
	private ScCommittee scCommittee;
	private ScCommittee selectedScCommittee;
	private DualListModel<PmUser> budgetApprovalUsers;
	private List<PmUser> selectedApproverList;
	private List<ScQtApproval> scQtApprovalList;

	public TabBean(String titleName, ScQtApproval scQtApproval,
			DualListModel<PmUser> budgetApprovalUsers) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
		this.budgetApprovalUsers = budgetApprovalUsers;
		this.selectedApproverList = budgetApprovalUsers.getTarget();
	}

	public TabBean(String titleName, ScQtApproval scQtApproval) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
	}

	public TabBean(String titleName, ScCommittee scCommittee) {
		this.titleName = titleName;
		this.scCommittee = scCommittee;
	}
	
	public TabBean(String titleName, List<ScQtApproval> scQtApprovalList) {
		this.titleName = titleName;
		this.scQtApprovalList = scQtApprovalList;
	}

	public String getTitleName() {
		return titleName;
	}

	public void setTitleName(String titleName) {
		this.titleName = titleName;
	}

	public ScQtApproval getScQtApproval() {
		return scQtApproval;
	}

	public void setScQtApproval(ScQtApproval scQtApproval) {
		this.scQtApproval = scQtApproval;
	}

	public ScCommittee getScCommittee() {
		return scCommittee;
	}

	public void setScCommittee(ScCommittee scCommittee) {
		this.scCommittee = scCommittee;
	}

	public ScCommittee getSelectedScCommittee() {
		return selectedScCommittee;
	}

	public void setSelectedScCommittee(ScCommittee selectedScCommittee) {
		this.selectedScCommittee = selectedScCommittee;
	}

	public DualListModel<PmUser> getBudgetApprovalUsers() {
		return budgetApprovalUsers;
	}

	public void setBudgetApprovalUsers(DualListModel<PmUser> budgetApprovalUsers) {
		this.budgetApprovalUsers = budgetApprovalUsers;
	}

	public List<PmUser> getSelectedApproverList() {
		return selectedApproverList;
	}

	public void setSelectedApproverList(List<PmUser> selectedApproverList) {
		this.selectedApproverList = selectedApproverList;
	}

	public List<ScQtApproval> getScQtApprovalList() {
		return scQtApprovalList;
	}

	public void setScQtApprovalList(List<ScQtApproval> scQtApprovalList) {
		this.scQtApprovalList = scQtApprovalList;
	}

}
