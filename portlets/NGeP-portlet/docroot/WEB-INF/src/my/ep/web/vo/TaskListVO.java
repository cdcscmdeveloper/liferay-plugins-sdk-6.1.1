package my.ep.web.vo;

import java.io.Serializable;

import my.ep.bpm.vo.WorklistItemVO;

public class TaskListVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2078243221960560835L;
	private WorklistItemVO item;

	public WorklistItemVO getItem() {
		return item;
	}

	public void setItem(WorklistItemVO item) {
		this.item = item;
	}
}
