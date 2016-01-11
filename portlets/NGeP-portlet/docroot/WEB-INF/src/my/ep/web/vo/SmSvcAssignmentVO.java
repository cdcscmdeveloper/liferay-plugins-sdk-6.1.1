package my.ep.web.vo;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSvTask;

public class SmSvcAssignmentVO {
	private SmSvTask smSvTask;
	private DualListModel<PmUser> svoUserList;
	
	public SmSvTask getSmSvTask() {
		return smSvTask;
	}
	public void setSmSvTask(SmSvTask smSvTask) {
		this.smSvTask = smSvTask;
	}
	public DualListModel<PmUser> getSvoUserList() {
		return svoUserList;
	}
	public void setSvoUserList(DualListModel<PmUser> svoUserList) {
		this.svoUserList = svoUserList;
	}
	
	
}
