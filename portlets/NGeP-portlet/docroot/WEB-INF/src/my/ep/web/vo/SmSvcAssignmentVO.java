package my.ep.web.vo;

import java.io.Serializable;

import org.primefaces.model.DualListModel;

import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSvTask;

public class SmSvcAssignmentVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
