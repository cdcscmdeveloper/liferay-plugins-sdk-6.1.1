package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.managedbean.BaseManagedBean;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportManagedBean")
@SessionScoped
public class ReportMangedBean extends BaseManagedBean{
	
	private static final long serialVersionUID = 1L;
	private ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<SelectItem> reportList=new ArrayList<SelectItem>();
	//private String reportName;
	private String reportValue;
	//private String moduleValue;
	private String reportJsp;
	private HashMap<String, String> monthMap= new HashMap<String, String>();
			
	public String getReportJsp() {
		return reportJsp;
	}
	public void setReportJsp(String reportJsp) {
		this.reportJsp = reportJsp;
	}
	public String getReportValue() {
		return reportValue;
	}
	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}
	/*public String getModuleValue() {
		return moduleValue;
	}
	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}*/	
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}	
	/*public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}*/
	public List<SelectItem> getReportList() {
		return reportList;
	}
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}
	public HashMap<String, String> getMonthMap() {
		return monthMap;
	}
	public void setMonthMap(HashMap<String, String> monthMap) {
		this.monthMap = monthMap;
	}
	
}
