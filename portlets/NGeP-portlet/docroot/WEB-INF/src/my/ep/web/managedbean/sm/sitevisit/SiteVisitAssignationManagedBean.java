package my.ep.web.managedbean.sm.sitevisit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name="siteVisitAssignatioMB")
@SessionScoped
public class SiteVisitAssignationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private List<DummyTask> dtList;
	private List<SiteView> siteViewList;
	
	
	public List<SiteView> getSiteViewList() {
		return siteViewList;
	}


	public void setSiteViewList(List<SiteView> siteViewList) {
		this.siteViewList = siteViewList;
	}


	public List<DummyTask> getDtList() {
		return dtList;
	}


	public void setDtList(List<DummyTask> dtList) {
		this.dtList = dtList;
	}


	public SiteVisitAssignationManagedBean(){
		dtList = new ArrayList<DummyTask>();
		dtList.add(new DummyTask(1,"Jalan 123"));
		dtList.add(new DummyTask(2,"Jalan 5354"));
		
		siteViewList = new ArrayList<SiteView>();
		siteViewList.add(new SiteView(1, new Date(),"Demo Sdn Bhd", "Pending for assign", "MOF123456789","APP789456"));
		siteViewList.add(new SiteView(2, new Date(),"Demo Sdn Bhd", "Pending for assign", "MOF123456789","APP123456"));
	}
	

	public class DummyTask {
		private int taskNum;
		private String siteAdd;
		private Date siteVisitDate;

		public DummyTask(int taskNum,String siteAdd){
			this.taskNum = taskNum;
			this.siteAdd = siteAdd;
			this.siteVisitDate = new Date();
		}
		
		public int getTaskNum() {
			return taskNum;
		}

		public void setTaskNum(int taskNum) {
			this.taskNum = taskNum;
		}

		public String getSiteAdd() {
			return siteAdd;
		}

		public void setSiteAdd(String siteAdd) {
			this.siteAdd = siteAdd;
		}

		public Date getSiteVisitDate() {
			return siteVisitDate;
		}

		public void setSiteVisitDate(Date siteVisitDate) {
			this.siteVisitDate = siteVisitDate;
		}

	}

	public class SiteView {
		private int taskNo;
		private Date dateOfCreation;
		private String companyName;
		private String status;
		private String mofNo;
		private String appNo;
		
		public SiteView(int taskNo, Date dateOfCreation, String companyName,String status,String mofNo, String appNo){
			this.taskNo = taskNo;
			this.dateOfCreation = dateOfCreation;
			this.companyName=companyName;
			this.status=status;
			this.mofNo=mofNo;
			this.appNo = appNo;
		}
		
		public int getTaskNo() {
			return taskNo;
		}
		public void setTaskNo(int taskNo) {
			this.taskNo = taskNo;
		}
		public Date getDateOfCreation() {
			return dateOfCreation;
		}
		public void setDateOfCreation(Date dateOfCreation) {
			this.dateOfCreation = dateOfCreation;
		}
		public String getCompanyName() {
			return companyName;
		}
		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getMofNo() {
			return mofNo;
		}
		public void setMofNo(String mofNo) {
			this.mofNo = mofNo;
		}

		public String getAppNo() {
			return appNo;
		}

		public void setAppNo(String appNo) {
			this.appNo = appNo;
		}
		
	}
}
