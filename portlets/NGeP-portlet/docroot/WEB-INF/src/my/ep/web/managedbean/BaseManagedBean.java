/**
 * 
 */
package my.ep.web.managedbean;

import java.io.Serializable;

/**
 * @author siew mei
 *
 */
public abstract class BaseManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String token;
	private String taskId;
	private String[] outComes;

	public String[] getOutComes() {
		return outComes;
	}

	public void setOutComes(String[] outComes) {
		this.outComes = outComes;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}
