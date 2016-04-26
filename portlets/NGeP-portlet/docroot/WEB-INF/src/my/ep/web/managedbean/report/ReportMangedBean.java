/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sc.loaloi.ScLoa;
import my.ep.persistence.entity.sc.loaloi.ScLoaReport;
import my.ep.persistence.entity.sc.loaloi.ScLoiLoa;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.managedbean.BaseManagedBean;

// TODO: Auto-generated Javadoc

/**
 * The Class ReportMangedBean.
 * 
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportManagedBean")
@SessionScoped
public class ReportMangedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The rpt gen bean. */
	private ReportsGenerateBean rptGenBean = new ReportsGenerateBean();

	/** The report list. */
	private List<SelectItem> reportList = new ArrayList<SelectItem>();
	// private String reportName;
	/** The report value. */
	private String reportValue;
	// private String moduleValue;
	/** The report jsp. */
	private String reportJsp;

	/** The report title. */
	private String reportTitle;
	
	/** The report code. */
	private String reportCode;
	
	/** The report url value. */
	private String reportUrlValue;
	
	/** The values empty. */
	boolean valuesEmpty=false;
	
	/** The ministry id. */
	private String ministryId;
	
	/** The error value. */
	boolean errorValue=false;
	
	/** The sm supplier. */
	private SmSupplier smSupplier;

	/** The month map. */
	private HashMap<String, String> monthMap = new HashMap<String, String>();

	/** The loa report document list. */
	private List<ScLoaReport> loaReportDocumentList;
	
	/** The lovs values map. */
	private HashMap<String,String> lovValuesMap=new HashMap<String,String>();
	
	/** The lov new values map. */
	private HashMap<String,String> lovNewValuesMap=new HashMap<String,String>();

	/** The sc loa. */
	private ScLoa scLoa;

	/** The pm status. */
	private PmStatus pmStatus;

	/** The sc loa report. */
	private List<ScLoaReport> scLoaReport;

	/** The supp loi loa map. */
	private Map<Long, List<ScLoiLoa>> suppLoiLoaMap;

	/** The sc qt. */
	private ScQt scQt;

	/** The sc qt id. */
	private Long scQtId;

	/** The module name value. */
	private String moduleNameValue;

	/** The selected ministry. */
	private PmOrgProfile selectedMinistry;

	/** The selected pegwai pengawal. */
	private PmOrgProfile selectedPegwaiPengawal;

	/** The selected kumpulan ptj. */
	private PmOrgProfile selectedKumpulanPTJ;

	/** The selected ptj. */
	private PmOrgProfile selectedPTJ;

	/** The selected org profile. */
	private PmOrgProfile selectedOrgProfile;
	
	/** The selected item. */
	private CmItem selectedItem;
	
	/** The selected org type. */
	private PmParameter selectedOrgType;

	/** The ptj id. */
	private Long ptjId = null;

	/** The search value. */
	private String[] searchValue = new String[3];

	/** The search field value. */
	private String[] searchFieldValue = new String[3];

	/** The search operator value. */
	private String[] searchOperatorValue = new String[3];

	/** The selected lov. */
	private CmUnspsc selectedLOV;
	/** The search field. */
	private List<SelectItem> searchField;
	/** The search operator. */
	private List<SelectItem> searchOperator;

	/**
	 * Gets the report jsp.
	 * 
	 * @return the report jsp
	 */
	public String getReportJsp() {
		return reportJsp;
	}

	/**
	 * Sets the report jsp.
	 * 
	 * @param reportJsp
	 *            the new report jsp
	 */
	public void setReportJsp(String reportJsp) {
		this.reportJsp = reportJsp;
	}

	/**
	 * Gets the report value.
	 * 
	 * @return the report value
	 */
	public String getReportValue() {
		return reportValue;
	}

	/**
	 * Gets the lovs values map.
	 * 
	 * @return the lovsValuesMap
	 */
	public HashMap<String, String> getLovValuesMap() {
		return lovValuesMap;
	}

	/**
	 * Sets the lovs values map.
	 * 
	 * @param lovValuesMap
	 *            the lov values map
	 */
	public void setLovValuesMap(HashMap<String, String> lovValuesMap) {
		this.lovValuesMap = lovValuesMap;
	}

	/**
	 * Sets the report value.
	 * 
	 * @param reportValue
	 *            the new report value
	 */
	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	/*
	 * public String getModuleValue() { return moduleValue; } public void
	 * setModuleValue(String moduleValue) { this.moduleValue = moduleValue; }
	 */
	/**
	 * Gets the rpt gen bean.
	 * 
	 * @return the rpt gen bean
	 */
	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}

	/**
	 * Sets the rpt gen bean.
	 * 
	 * @param rptGenBean
	 *            the new rpt gen bean
	 */
	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}

	/*
	 * public String getReportName() { return reportName; } public void
	 * setReportName(String reportName) { this.reportName = reportName; }
	 */
	/**
	 * Gets the report list.
	 * 
	 * @return the report list
	 */
	public List<SelectItem> getReportList() {
		return reportList;
	}

	/**
	 * Sets the report list.
	 * 
	 * @param reportList
	 *            the new report list
	 */
	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	/**
	 * Gets the month map.
	 * 
	 * @return the month map
	 */
	public HashMap<String, String> getMonthMap() {
		return monthMap;
	}	
	/**
	 * Sets the month map.
	 * 
	 * @param monthMap
	 *            the month map
	 */
	public void setMonthMap(HashMap<String, String> monthMap) {
		this.monthMap = monthMap;
	}

	/**
	 * Gets the search value.
	 * 
	 * @return the searchValue
	 */
	public String[] getSearchValue() {
		return searchValue;
	}

	/**
	 * Gets the report url value.
	 * 
	 * @return the reportUrlValue
	 */
	public String getReportUrlValue() {
		return reportUrlValue;
	}

	/**
	 * Sets the report url value.
	 * 
	 * @param reportUrlValue
	 *            the reportUrlValue to set
	 */
	public void setReportUrlValue(String reportUrlValue) {
		this.reportUrlValue = reportUrlValue;
	}

	/**
	 * Sets the search value.
	 * 
	 * @param searchValue
	 *            the searchValue to set
	 */
	public void setSearchValue(String[] searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * Gets the search field value.
	 * 
	 * @return the searchFieldValue
	 */
	public String[] getSearchFieldValue() {
		return searchFieldValue;
	}

	/**
	 * Sets the search field value.
	 * 
	 * @param searchFieldValue
	 *            the searchFieldValue to set
	 */
	public void setSearchFieldValue(String[] searchFieldValue) {
		this.searchFieldValue = searchFieldValue;
	}

	/**
	 * Gets the search operator value.
	 * 
	 * @return the searchOperatorValue
	 */
	public String[] getSearchOperatorValue() {
		return searchOperatorValue;
	}

	/**
	 * Sets the search operator value.
	 * 
	 * @param searchOperatorValue
	 *            the searchOperatorValue to set
	 */
	public void setSearchOperatorValue(String[] searchOperatorValue) {
		this.searchOperatorValue = searchOperatorValue;
	}

	/**
	 * Sets the search field.
	 * 
	 * @param searchField
	 *            the searchField to set
	 */
	public void setSearchField(List<SelectItem> searchField) {
		this.searchField = searchField;
	}

	/**
	 * Gets the search operator.
	 * 
	 * @return the searchOperator
	 */
	public List<SelectItem> getSearchOperator() {
		return searchOperator;
	}

	/**
	 * Sets the search operator.
	 * 
	 * @param searchOperator
	 *            the searchOperator to set
	 */
	public void setSearchOperator(List<SelectItem> searchOperator) {
		this.searchOperator = searchOperator;
	}

	/**
	 * Gets the search field.
	 * 
	 * @return the searchField
	 */
	public List<SelectItem> getSearchField() {
		return searchField;
	}

	/**
	 * Gets the selected lov.
	 * 
	 * @return the selectedLOV
	 */
	public CmUnspsc getSelectedLOV() {
		return selectedLOV;
	}

	/**
	 * Sets the selected lov.
	 * 
	 * @param selectedLOV
	 *            the selectedLOV to set
	 */
	public void setSelectedLOV(CmUnspsc selectedLOV) {
		this.selectedLOV = selectedLOV;
	}

	/**
	 * Gets the sc loa.
	 * 
	 * @return the scLoa
	 */
	public ScLoa getScLoa() {
		return scLoa;
	}

	/**
	 * Gets the sm supplier.
	 * 
	 * @return the smSupplier
	 */
	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	/**
	 * Sets the sm supplier.
	 * 
	 * @param smSupplier
	 *            the smSupplier to set
	 */
	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	/**
	 * Sets the sc loa.
	 * 
	 * @param scLoa
	 *            the scLoa to set
	 */
	public void setScLoa(ScLoa scLoa) {
		this.scLoa = scLoa;
	}

	/**
	 * Gets the pm status.
	 * 
	 * @return the pmStatus
	 */
	public PmStatus getPmStatus() {
		return pmStatus;
	}

	/**
	 * Sets the pm status.
	 * 
	 * @param pmStatus
	 *            the pmStatus to set
	 */
	public void setPmStatus(PmStatus pmStatus) {
		this.pmStatus = pmStatus;
	}

	/**
	 * Gets the sc loa report.
	 * 
	 * @return the scLoaReport
	 */
	public List<ScLoaReport> getScLoaReport() {
		return scLoaReport;
	}

	/**
	 * Sets the sc loa report.
	 * 
	 * @param scLoaReport
	 *            the scLoaReport to set
	 */
	public void setScLoaReport(List<ScLoaReport> scLoaReport) {
		this.scLoaReport = scLoaReport;
	}

	/**
	 * Gets the supp loi loa map.
	 * 
	 * @return the suppLoiLoaMap
	 */
	public Map<Long, List<ScLoiLoa>> getSuppLoiLoaMap() {
		return suppLoiLoaMap;
	}

	/**
	 * Sets the supp loi loa map.
	 * 
	 * @param suppLoiLoaMap
	 *            the suppLoiLoaMap to set
	 */
	public void setSuppLoiLoaMap(Map<Long, List<ScLoiLoa>> suppLoiLoaMap) {
		this.suppLoiLoaMap = suppLoiLoaMap;
	}

	/**
	 * Gets the sc qt.
	 * 
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * Sets the sc qt.
	 * 
	 * @param scQt
	 *            the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * Gets the sc qt id.
	 * 
	 * @return the scQtId
	 */
	public Long getScQtId() {
		return scQtId;
	}

	/**
	 * Sets the sc qt id.
	 * 
	 * @param scQtId
	 *            the scQtId to set
	 */
	public void setScQtId(Long scQtId) {
		this.scQtId = scQtId;
	}

	/**
	 * Gets the loa report document list.
	 * 
	 * @return the loaReportDocumentList
	 */
	public List<ScLoaReport> getLoaReportDocumentList() {
		return loaReportDocumentList;
	}

	/**
	 * Sets the loa report document list.
	 * 
	 * @param loaReportDocumentList
	 *            the loaReportDocumentList to set
	 */
	public void setLoaReportDocumentList(List<ScLoaReport> loaReportDocumentList) {
		this.loaReportDocumentList = loaReportDocumentList;
	}

	/**
	 * Gets the module name value.
	 * 
	 * @return the moduleNameValue
	 */
	public String getModuleNameValue() {
		return moduleNameValue;
	}

	/**
	 * Gets the selected item.
	 * 
	 * @return the selectedItem
	 */
	public CmItem getSelectedItem() {
		return selectedItem;
	}

	/**
	 * Sets the selected item.
	 * 
	 * @param selectedItem
	 *            the selectedItem to set
	 */
	public void setSelectedItem(CmItem selectedItem) {
		this.selectedItem = selectedItem;
	}

	/**
	 * Sets the module name value.
	 * 
	 * @param moduleNameValue
	 *            the moduleNameValue to set
	 */
	public void setModuleNameValue(String moduleNameValue) {
		this.moduleNameValue = moduleNameValue;
	}

	/**
	 * Gets the selected ministry.
	 * 
	 * @return the selectedMinistry
	 */
	public PmOrgProfile getSelectedMinistry() {			
		return selectedMinistry;
	}

	/**
	 * Gets the lov new values map.
	 * 
	 * @return the lovNewValuesMap
	 */
	public HashMap<String, String> getLovNewValuesMap() {
		return lovNewValuesMap;
	}

	/**
	 * Sets the lov new values map.
	 * 
	 * @param lovNewValuesMap
	 *            the lovNewValuesMap to set
	 */
	public void setLovNewValuesMap(HashMap<String, String> lovNewValuesMap) {
		this.lovNewValuesMap = lovNewValuesMap;
	}

	/**
	 * Sets the selected ministry.
	 * 
	 * @param selectedMinistry
	 *            the selectedMinistry to set
	 */
	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}

	/**
	 * Gets the selected pegwai pengawal.
	 * 
	 * @return the selectedPegwaiPengawal
	 */
	public PmOrgProfile getSelectedPegwaiPengawal() {	
		
		return selectedPegwaiPengawal;
	}

	/**
	 * Sets the selected pegwai pengawal.
	 * 
	 * @param selectedPegwaiPengawal
	 *            the selectedPegwaiPengawal to set
	 */
	public void setSelectedPegwaiPengawal(PmOrgProfile selectedPegwaiPengawal) {
		this.selectedPegwaiPengawal = selectedPegwaiPengawal;
	}

	/**
	 * Gets the selected kumpulan ptj.
	 * 
	 * @return the selectedKumpulanPTJ
	 */
	public PmOrgProfile getSelectedKumpulanPTJ() {		
		return selectedKumpulanPTJ;
	}

	/**
	 * Sets the selected kumpulan ptj.
	 * 
	 * @param selectedKumpulanPTJ
	 *            the selectedKumpulanPTJ to set
	 */
	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	/**
	 * Gets the selected ptj.
	 * 
	 * @return the selectedPTJ
	 */
	public PmOrgProfile getSelectedPTJ() {		
		return selectedPTJ;
	}

	/**
	 * Sets the selected ptj.
	 * 
	 * @param selectedPTJ
	 *            the selectedPTJ to set
	 */
	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	/**
	 * Gets the report title.
	 * 
	 * @return the reportTitle
	 */
	public String getReportTitle() {
		return reportTitle;
	}

		/**
	 * Sets the report title.
	 * 
	 * @param reportTitle
	 *            the reportTitle to set
	 */
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	/**
	 * Gets the ptj id.
	 * 
	 * @return the ptjId
	 */
	public Long getPtjId() {
		return ptjId;
	}

	/**
	 * Sets the ptj id.
	 * 
	 * @param ptjId
	 *            the ptjId to set
	 */
	public void setPtjId(Long ptjId) {
		this.ptjId = ptjId;
	}

	/**
	 * Checks if is values empty.
	 * 
	 * @return the valuesEmpty
	 */
	public boolean isValuesEmpty() {
		return valuesEmpty;
	}

	/**
	 * Sets the values empty.
	 * 
	 * @param valuesEmpty
	 *            the valuesEmpty to set
	 */
	public void setValuesEmpty(boolean valuesEmpty) {
		this.valuesEmpty = valuesEmpty;
	}

	/**
	 * Checks if is error value.
	 * 
	 * @return the errorValue
	 */
	public boolean isErrorValue() {
		return errorValue;
	}

	/**
	 * Sets the error value.
	 * 
	 * @param errorValue
	 *            the errorValue to set
	 */
	public void setErrorValue(boolean errorValue) {
		this.errorValue = errorValue;
	}

	/**
	 * Gets the report code.
	 * 
	 * @return the reportCode
	 */
	public String getReportCode() {
		return reportCode;
	}

	/**
	 * Sets the report code.
	 * 
	 * @param reportCode
	 *            the reportCode to set
	 */
	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}
	
	/**
	 * Gets the selected org profile.
	 * 
	 * @return the selectedOrgProfile
	 */
	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	/**
	 * Sets the selected org profile.
	 * 
	 * @param selectedOrgProfile
	 *            the selectedOrgProfile to set
	 */
	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	/**
	 * Sets the selected org type.
	 * 
	 * @param selectedOrgType
	 *            the selectedOrgType to set
	 */
	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}

	/**
	 * Gets the selected org type.
	 * 
	 * @return the selectedOrgType
	 */
	public PmParameter getSelectedOrgType() {
		return selectedOrgType;
	}

	/**
	 * Gets the ministry id.
	 * 
	 * @return the ministryId
	 */
	public String getMinistryId() {
		return ministryId;
	}

	/**
	 * Sets the ministry id.
	 * 
	 * @param ministryId
	 *            the ministryId to set
	 */
	public void setMinistryId(String ministryId) {
		this.ministryId = ministryId;
	}

}
