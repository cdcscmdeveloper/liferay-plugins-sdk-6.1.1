package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.BaseManagedBean;
/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportManagedBean")
@SessionScoped
public class ReportMangedBean extends BaseManagedBean{
	
	private static final long serialVersionUID = 1L;
	
	private List<SelectItem> reportList=new ArrayList<SelectItem>();
	private String moduleName;
	private String reportName;
	private String reportNameValue;
	
	public String getReportNameValue() {
		return reportNameValue;
	}
	public void setReportNameValue(String reportNameValue) {
		this.reportNameValue = reportNameValue;
	}
	private ReportsGenerateBean rptGenBean ;
	
	public String getModuleName() {
		return moduleName;
	}
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public List<SelectItem> getReportList() {
		return reportList;
	}
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}
	
}
