/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.SelectItem;

/**
 * @author siew mei
 * 
 */
public abstract class BaseManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object token;

	// For BPM task details
	private String taskId;
	private String[] outComes;
	private Long currentStatusId;
	private String taskName;
	private boolean isAssignedState;
	private Object payLoad;
	private String processName;
	private Boolean groupTask;
	private boolean isReadOnly;
	private List<SelectItem> localAuthorityItems;

	// For sub title displayed in screen.
	private String title;

	/**
	 * @return the token
	 */
	public Object getToken() {
		return token;
	}

	/**
	 * @param token
	 *            the token to set
	 */
	public void setToken(Object token) {
		this.token = token;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the outComes
	 */
	public String[] getOutComes() {
		return outComes;
	}

	/**
	 * @param outComes
	 *            the outComes to set
	 */
	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	/**
	 * @return the currentStatusId
	 */
	public Long getCurrentStatusId() {
		return currentStatusId;
	}

	/**
	 * @param currentStatusId
	 *            the currentStatusId to set
	 */
	public void setCurrentStatusId(Long currentStatusId) {
		this.currentStatusId = currentStatusId;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName
	 *            the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the isAssignedState
	 */
	public boolean isAssignedState() {
		return isAssignedState;
	}

	/**
	 * @param isAssignedState
	 *            the isAssignedState to set
	 */
	public void setAssignedState(boolean isAssignedState) {
		this.isAssignedState = isAssignedState;
	}

	/**
	 * @return the payLoad
	 */
	public Object getPayLoad() {
		return payLoad;
	}

	/**
	 * @param payLoad
	 *            the payLoad to set
	 */
	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the processName
	 */
	public String getProcessName() {
		return processName;
	}

	/**
	 * @param processName
	 *            the processName to set
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}

	/**
	 * @return the groupTask
	 */
	public Boolean getGroupTask() {
		return groupTask;
	}

	/**
	 * @param groupTask
	 *            the groupTask to set
	 */
	public void setGroupTask(Boolean groupTask) {
		this.groupTask = groupTask;
	}

	/**
	 * @return the isReadOnly
	 */
	public boolean isReadOnly() {
		return isReadOnly;
	}

	/**
	 * @param isReadOnly
	 *            the isReadOnly to set
	 */
	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	/**
	 * @return local authority items
	 */
	public List<SelectItem> getLocalAuthorityItems() {
		return localAuthorityItems;
	}

	/**
	 * @param List
	 *            <SelectItem> localAuthorityItems
	 */
	public void setLocalAuthorityItems(List<SelectItem> localAuthorityItems) {
		this.localAuthorityItems = localAuthorityItems;
	}

}
