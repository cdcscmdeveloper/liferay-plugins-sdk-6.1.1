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

@ManagedBean(name = "tasklistMB")
@SessionScoped
public class TaskListManagedBean extends BaseManagedBean {

	private List<WorklistItemVO> taskList;
	private Map bpmMap = new HashMap();
	private String processName;
	private String[] action;
	
	private String taskId;
	
	private String status;
	
	private String taskName;
	private String pageUrl;
	
	private String tempLoginId; //TODO: temp and to be removed
	

	/**
	 * @return the tempLoginId
	 */
	public String getTempLoginId() {
		return tempLoginId;
	}

	/**
	 * @param tempLoginId the tempLoginId to set
	 */
	public void setTempLoginId(String tempLoginId) {
		this.tempLoginId = tempLoginId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Map getBpmMap() {
		return bpmMap;
	}

	public void setBpmMap(Map bpmMap) {
		this.bpmMap = bpmMap;
	}



	public List<WorklistItemVO> getTaskList() {
		if(taskList==null)
			taskList = new ArrayList<WorklistItemVO>();
		return taskList;
	}

	public void setTaskList(List<WorklistItemVO> taskList) {
		this.taskList = taskList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	
	

}
