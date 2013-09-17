package my.ep.web.bean.ct;

import java.io.Serializable;

public class AdvanceSearchDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String projectName;
	private String projectDescription;
	private String numberOfContract;
	
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectDescription() {
		return projectDescription;
	}
	public void setProjectDescription(String projectDescription) {
		this.projectDescription = projectDescription;
	}
	
	
	public String getNumberOfContract() {
		return numberOfContract;
	}
	public void setNumberOfContract(String numberOfContract) {
		this.numberOfContract = numberOfContract;
	}

}
