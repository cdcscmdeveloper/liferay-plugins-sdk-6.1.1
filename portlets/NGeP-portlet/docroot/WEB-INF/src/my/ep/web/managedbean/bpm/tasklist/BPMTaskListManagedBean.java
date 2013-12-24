package my.ep.web.managedbean.bpm.tasklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.bpm.vo.WorklistItemVO;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.vo.AdvanceSearchVO;
import my.ep.web.vo.PricingVO;

@ManagedBean(name = "bpmTasklistMB")
@SessionScoped
public class BPMTaskListManagedBean extends BaseManagedBean {
	
	private List<WorklistItemVO> taskList;
	private Map bpmMap = new HashMap();
	private String processName;
	private String[] action;
	
	private String taskId;
	
	//private String status="ASSIGNED";
	
	private String taskName;
	
	private String tempLoginId; //TODO: temp and to be removed
	
	private String viewBy="DAY";

	public List<WorklistItemVO> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	public Map getBpmMap() {
		return bpmMap;
	}

	public void setBpmMap(Map bpmMap) {
		this.bpmMap = bpmMap;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String[] getAction() {
		return action;
	}

	public void setAction(String[] action) {
		this.action = action;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/*public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}*/

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTempLoginId() {
		return tempLoginId;
	}

	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	public String getViewBy() {
		return viewBy;
	}

	public void setViewBy(String viewBy) {
		this.viewBy = viewBy;
	}
	
	
	
	
	
	
}
