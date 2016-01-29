package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
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
	private List<ScLoaReport> loaReportDocumentList;	
	private ScLoa scLoa;
	private PmStatus pmStatus;
	private List<ScLoaReport> scLoaReport;
	private Map<Long, List<ScLoiLoa>> suppLoiLoaMap;	
	private ScQt scQt;
	private Long scQtId;
	private String moduleNameValue;
	
	/** The search value. */
	private String[] searchValue = new String[3];
	
	/** The search field value. */
	private String[] searchFieldValue = new String[3];
	
	/** The search operator value. */
	private String[] searchOperatorValue = new String[3];
	private CmUnspsc selectedLOV;
	/** The search field. */
	private List<SelectItem> searchField;
	/** The search operator. */
	private List<SelectItem> searchOperator;
			
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
	/**
	 * @return the searchValue
	 */
	public String[] getSearchValue() {
		return searchValue;
	}
	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String[] searchValue) {
		this.searchValue = searchValue;
	}
	/**
	 * @return the searchFieldValue
	 */
	public String[] getSearchFieldValue() {
		return searchFieldValue;
	}
	/**
	 * @param searchFieldValue the searchFieldValue to set
	 */
	public void setSearchFieldValue(String[] searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}
	/**
	 * @return the searchOperatorValue
	 */
	public String[] getSearchOperatorValue() {
		return searchOperatorValue;
	}
	/**
	 * @param searchOperatorValue the searchOperatorValue to set
	 */
	public void setSearchOperatorValue(String[] searchOperatorValue) {
		this.searchOperatorValue = searchOperatorValue;
	}
		
	/**
	 * @param searchField the searchField to set
	 */
	public void setSearchField(List<SelectItem> searchField) {
		this.searchField = searchField;
	}
	/**
	 * @return the searchOperator
	 */
	public List<SelectItem> getSearchOperator() {
		return searchOperator;
	}
	/**
	 * @param searchOperator the searchOperator to set
	 */
	public void setSearchOperator(List<SelectItem> searchOperator) {
		this.searchOperator = searchOperator;
	}
	/**
	 * @return the searchField
	 */
	public List<SelectItem> getSearchField() {
		return searchField;
	}
	/**
	 * @return the selectedLOV
	 */
	public CmUnspsc getSelectedLOV() {
		return selectedLOV;
	}
	/**
	 * @param selectedLOV the selectedLOV to set
	 */
	public void setSelectedLOV(CmUnspsc selectedLOV) {
		this.selectedLOV = selectedLOV;
	}
	
	/**
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}
	/**
	 * @param scLoa the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}
	/**
	 * @return the pmStatus
	 */
	public PmStatus getPmStatus() {
		return pmStatus;
	}
	/**
	 * @param pmStatus the pmStatus to set
	 */
	public void setPmStatus(PmStatus pmStatus) {
		this.pmStatus = pmStatus;
	}
	/**
	 * @return the scLoaReport
	 */
	public List<ScLoaReport> getScLoaReport() {
		return scLoaReport;
	}
	/**
	 * @param scLoaReport the scLoaReport to set
	 */
	public void setScLoaReport(List<ScLoaReport> scLoaReport) {
		this.scLoaReport = scLoaReport;
	}
	/**
	 * @return the suppLoiLoaMap
	 */
	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}
	/**
	 * @param suppLoiLoaMap the suppLoiLoaMap to set
	 */
	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {		
		this.suppLoiLoaMap = suppLoiLoaMap;
	}
	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}
	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}
	/**
	 * @return the scQtId
	 */
	public Long getScQtId() {
		return scQtId;
	}
	/**
	 * @param scQtId the scQtId to set
	 */
	public void setScQtId(Long scQtId) {
		this.scQtId = scQtId;
	}
	/**
	 * @return the loaReportDocumentList
	 */
	public List<ScLoaReport> getLoaReportDocumentList() {
		return loaReportDocumentList;
	}
	/**
	 * @param loaReportDocumentList the loaReportDocumentList to set
	 */
	public void setLoaReportDocumentList(List<ScLoaReport> loaReportDocumentList) {
		this.loaReportDocumentList = loaReportDocumentList;
	}
	/**
	 * @return the moduleNameValue
	 */
	public String getModuleNameValue() {
		return moduleNameValue;
	}
	/**
	 * @param moduleNameValue the moduleNameValue to set
	 */
	public void setModuleNameValue(String moduleNameValue) {
		this.moduleNameValue = moduleNameValue;
	}
	
	
}
