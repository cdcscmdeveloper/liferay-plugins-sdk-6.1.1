package my.ep.web.bean.sc.quatationtender;

import java.util.List;

import my.ep.persistence.entity.sc.quationtender.ScCommittee;
import my.ep.persistence.entity.sc.quationtender.ScCommitteeMember;
import my.ep.persistence.entity.sc.quationtender.ScQtApproval;
import my.ep.persistence.entity.sc.quationtender.ScQtApproverSettings;
import my.ep.persistence.entity.sc.quationtender.ScQtCommittee;

import org.primefaces.model.DualListModel;

public class TabBean {
	private String titleName;
	private ScQtApproval scQtApproval;
	private ScCommittee scCommittee;
	private ScCommittee selectedScCommittee;
	private DualListModel<ScQtApproverSettings> budgetApprovalUsers;
	private List<ScQtApproverSettings> selectedApproverList;
	private List<ScQtApproval> scQtApprovalList;
	private ScQtCommittee scQtCommittee;

	private List<ScCommitteeMember> members;
	
	public TabBean(String titleName) {
		this.titleName = titleName;
	}

	public TabBean(String titleName, ScQtApproval scQtApproval,
			DualListModel<ScQtApproverSettings> budgetApprovalUsers) {
		this.titleName = titleName;
		this.scQtApproval = scQtApproval;
		this.budgetApprovalUsers = budgetApprovalUsers;

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

	public TabBean(String titleName, ScQtCommittee scQtCommittee) {
		this.titleName = titleName;
		this.scQtCommittee = scQtCommittee;
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

	public DualListModel<ScQtApproverSettings> getBudgetApprovalUsers() {
		return budgetApprovalUsers;
	}

	public void setBudgetApprovalUsers(
			DualListModel<ScQtApproverSettings> budgetApprovalUsers) {
		this.budgetApprovalUsers = budgetApprovalUsers;
	}

	public List<ScQtApproverSettings> getSelectedApproverList() {
		return selectedApproverList;
	}

	public void setSelectedApproverList(
			List<ScQtApproverSettings> selectedApproverList) {
		this.selectedApproverList = selectedApproverList;
	}

	public List<ScQtApproval> getScQtApprovalList() {
		return scQtApprovalList;
	}

	public void setScQtApprovalList(List<ScQtApproval> scQtApprovalList) {
		this.scQtApprovalList = scQtApprovalList;
	}

	public ScQtCommittee getScQtCommittee() {
		return scQtCommittee;
	}

	public void setScQtCommittee(ScQtCommittee scQtCommittee) {
		this.scQtCommittee = scQtCommittee;
	}

	public List<ScCommitteeMember> getMembers() {
		return members;
	}

	public void setMembers(List<ScCommitteeMember> members) {
		this.members = members;
	}

}
