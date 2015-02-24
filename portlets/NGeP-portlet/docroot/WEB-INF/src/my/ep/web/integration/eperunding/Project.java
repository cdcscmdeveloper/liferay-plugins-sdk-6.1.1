package my.ep.web.integration.eperunding;

import java.util.Date;

public class Project {

	private String projectNo;
	private String projectName;
	private String projectLocation;
	private String client;
	private Date apppointmentDate;
	private String cost;
	private String serviceDesc;
	private String workProgress;
	private String workProgressOthers;
	private String consultantFee;
	private String paymentMethod;

	public String getProjectNo() {
		return projectNo;
	}

	public void setProjectNo(String projectNo) {
		this.projectNo = projectNo;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectLocation() {
		return projectLocation;
	}

	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public Date getApppointmentDate() {
		return apppointmentDate;
	}

	public void setApppointmentDate(Date apppointmentDate) {
		this.apppointmentDate = apppointmentDate;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}

	public void setServiceDesc(String serviceDesc) {
		this.serviceDesc = serviceDesc;
	}

	public String getWorkProgress() {
		return workProgress;
	}

	public void setWorkProgress(String workProgress) {
		this.workProgress = workProgress;
	}

	public String getWorkProgressOthers() {
		return workProgressOthers;
	}

	public void setWorkProgressOthers(String workProgressOthers) {
		this.workProgressOthers = workProgressOthers;
	}

	public String getConsultantFee() {
		return consultantFee;
	}

	public void setConsultantFee(String consultantFee) {
		this.consultantFee = consultantFee;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

}
