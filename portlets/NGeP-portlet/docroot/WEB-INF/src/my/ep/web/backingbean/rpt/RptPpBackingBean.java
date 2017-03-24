package my.ep.web.backingbean.rpt;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmOrgValidity;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.type.pm.OrganizationType;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.FacesScopes;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "rptPpBackingBean")
@SessionScoped
public class RptPpBackingBean{

	/**
	 * Global report variables.
	 */
	
	// Enviroment variables.
	private Long userId;
	private String strLanguage;
	
	// Report db Information.
	private String moduleCode;
	private String reportCategory;
	private BigDecimal reportId;
	private String reportCode;
	private String reportTitle;
	private String reportTitleMalay;
	private String reportFileName;
	private boolean isPdf;
	private boolean isTempAccess;

	// Report parameters
	private String reportParametersUrl;
	
	// Report requested information after generated.
	private String requestId;
	private String jobId;
	
	// Report Utility.
	ReportUtil reportUtil;
	
	/**
	 * Individual report variables.
	 */
	
	// Components info.
	private String reportName;
	private SelectItem[] itemsList;
	private UserProfilesSearchBean userProfileSearchBean;
	private List<SelectItem> reportList;

	// This component info.
	protected String ppYear;
	protected String ppMinistry;
	protected String ppPTJ;
	
	protected PmOrgProfile selectedMinistry;
	protected PmOrgProfile selectedPegwaiPengawal;
	protected PmOrgProfile selectedKumpulanPTJ;
	protected PmOrgProfile selectedPTJ;
	protected PmOrgProfile selectedOrgProfile;
	protected PmRoleDesc selectedPmRoleDesc;
	protected HashMap<String,String> lovValuesMap;
	
	public RptPpBackingBean(){
		
		userId = new FacesScopes().getUserSessionBean().getUserId();
		
		strLanguage = ResourceBundleUtils.getMessage(
				ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		
		moduleCode = "PP";
		
		reportUtil = new ReportUtil(strLanguage);
		
		showAllReports();
	}	
	
	private void showAllReports(){
		
		List<Object[]> reportModList = new ArrayList<Object[]>();
		
		IPMReportSO reportSO = reportUtil.getPMReportSO();
		
		if(reportUtil.isEnglish()){
			
			reportModList = reportSO
					.retrieveReportListByModuleNUserAuthorization(
							userId, moduleCode);
		}	

		if(reportUtil.isMalay()){
			
			reportModList = reportSO
					.retrieveMalayReportListByModuleNUserAuthorization(
							userId, moduleCode);
		}
		
		reportList = reportUtil.assignListToSelectItemLabelValue(reportModList);
		
	}
	
	public void init(String reportCode){
		resetVariable();
		retrieveReportValueFromDb(reportCode);
	}
	
	public void resetVariable(){

		ppYear = null;
		ppMinistry = null;
		ppPTJ = null;
		
		selectedMinistry = null;
		selectedPegwaiPengawal = null;
		selectedKumpulanPTJ = null;
		selectedPTJ = null;
		selectedOrgProfile = null;
		selectedPmRoleDesc = null;
		
		lovValuesMap = null;
		
		userProfileSearchBean = new UserProfilesSearchBean();
	}
	
	public void retrieveReportValueFromDb(String reportCode){
		
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setReportCode(reportCode);
		
		IPMReportSO reportSO = reportUtil.getPMReportSO();
		PMReportResDTO resDTO = reportSO.retriveReportByrptId(reqDTO);
		
		PMReport pmReport = resDTO.getPmReportDtls();
		
		this.moduleCode = pmReport.getModuleCode();
		this.reportCategory = pmReport.getSubModule();
		
		this.reportId = pmReport.getReportId();
		this.reportCode = reportCode; 
		this.reportTitle = pmReport.getReportTitle();
		this.reportTitleMalay = pmReport.getReportTitleMalay();
		this.reportFileName = pmReport.getReportFilename();
		
		this.isTempAccess = pmReport.getIsTempAccess() == 1 ? true : false;
		this.isPdf = false;
	}
	
	public List<SelectItem> getAllModuleList() {

		IPMReportSO reportSO = reportUtil.getPMReportSO();
			
		List<Object[]> reportModList = reportSO.retrieveModuleList(strLanguage,
				userId);
		
		return reportUtil.assignListToSelectItemValueLabel(reportModList);
	}
	
	
	
	public void generateReport(){
		
//		HashMap<String, String> parameter = new HashMap<String, String>();
//		
//		parameter.put("report_id", String.valueOf(reportId));
//		parameter.put("report_name", reportName);
//		parameter.put("user_id", String.valueOf(userId));
//		
//		if(selectedMinistry != null){
//			parameter.put("ministry_id", String.valueOf(selectedMinistry.getOrgProfileId()));
//		}
//		
//		if(selectedPegwaiPengawal != null){
//			parameter.put("pp_id", String.valueOf(selectedPegwaiPengawal.getOrgProfileId()));
//		}
//		
//		if(selectedKumpulanPTJ != null){
//			parameter.put("kptj_id", String.valueOf(selectedKumpulanPTJ.getOrgProfileId()));
//		}
//		
//		if(selectedPTJ != null){
//			parameter.put("ptj_id", String.valueOf(selectedPTJ.getOrgProfileId()));
//		}
//		
//		parameter.put("year", ppYear);
//		
//		IRPTRequestSO rptRequestSO = util.getRptRequestSO();
//		
//		RPTRequestResDTO resDTO = rptRequestSO.generateReport(new StringBuffer(reportParametersUrl), 
//				reportCode, reportTitle, userId, isPdf, isTempAccess);
//		
//		this.requestId = String.valueOf(resDTO.getRequestId());
//		this.jobId = String.valueOf(resDTO.getJobId());
//		
//		RequestContext.getCurrentInstance().execute("ex.show();");
	}
	
	public void refreshLovData() {

		PmOrgValidity orgvValidty = null;
		
		if(selectedMinistry != null){
			orgvValidty = selectedMinistry.getPmOrgValidity();
		} else {
			return;
		}
		
		String orgName = null;
		
		if(orgvValidty != null){
			orgName = orgvValidty.getOrgName();
		} else {
			return;
		}
		
		if (reportUtil.isEmpty(lovValuesMap)){
			return;
		}
		
		if(orgName == null){
			return;
		}
			
		if(lovValuesMap.get("0").equals(orgName)){
		
			selectedPegwaiPengawal = null;
			selectedKumpulanPTJ = null;
			selectedPTJ = null;
		}

		lovValuesMap.put("0", orgName);

		if (selectedPegwaiPengawal != null && selectedPegwaiPengawal.getPmOrgValidity() != null) {
			lovValuesMap.put("1", selectedPegwaiPengawal.getPmOrgValidity().getOrgName());
		}
			
		if (selectedKumpulanPTJ != null && selectedKumpulanPTJ.getPmOrgValidity() != null) {
			lovValuesMap.put("2", selectedKumpulanPTJ.getPmOrgValidity().getOrgName());
		}
			
		if(!reportUtil.isEmpty(selectedPTJ) && !reportUtil.isEmpty(selectedPTJ.getPmOrgValidity())){
			lovValuesMap.put("3", selectedPTJ.getPmOrgValidity().getOrgName());
		}
	}
	
	public String constructMinistryConditionFields() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		
		return sb.toString();
	}
	
	public String constructPegwaiPengawalConditionFields() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.PENGAWAI_PENGAWAL.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.PENGAWAI_PENGAWAL.value(),
				OrganizationType.MINISTRY.value()));
		sb.append("^");
	
		if (selectedMinistry != null) {
			sb.append(selectedMinistry.getOrgProfileId());
		}

		return sb.toString();
	}
	
	public List<String> generateYearList() {
		
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
	
		}
		
		ArrayList yearList = new ArrayList<String>();
		
		if (year != null) {
		
			for (int i = year - 7; i <= year; i++) {
				yearList.add(Integer.toString(i));
			}
			
		}
		
		return yearList;
	}
	
	public String constructKumpulanPTJConditionFields() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(
				OrganizationType.DEPARTMENT.value(),
				OrganizationType.PENGAWAI_PENGAWAL.value()));
		sb.append("^");
		
		if (selectedPegwaiPengawal != null) {
			selectedPegwaiPengawal.getOrgProfileId();
		}

		return sb.toString();
	}

	public String constructPTJConditionFields() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil
				.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");

		if (selectedKumpulanPTJ != null) {
			sb.append(selectedKumpulanPTJ.getOrgProfileId());
		}
		
		return sb.toString();
	}


	/**
	 * Setter Getter methods.
	 */
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getStrLanguage() {
		return strLanguage;
	}

	public void setStrLanguage(String strLanguage) {
		this.strLanguage = strLanguage;
	}

	public String getModuleCode() {
		return moduleCode;
	}

	public void setModuleCode(String moduleCode) {
		this.moduleCode = moduleCode;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getReportTitleMalay() {
		return reportTitleMalay;
	}

	public void setReportTitleMalay(String reportTitleMalay) {
		this.reportTitleMalay = reportTitleMalay;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public boolean isPdf() {
		return isPdf;
	}

	public void setPdf(boolean isPdf) {
		this.isPdf = isPdf;
	}

	public boolean isTempAccess() {
		return isTempAccess;
	}

	public void setTempAccess(boolean isTempAccess) {
		this.isTempAccess = isTempAccess;
	}

	public String getReportParametersUrl() {
		return reportParametersUrl;
	}

	public void setReportParametersUrl(String reportParametersUrl) {
		this.reportParametersUrl = reportParametersUrl;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public SelectItem[] getItemsList() {
		return itemsList;
	}

	public void setItemsList(SelectItem[] itemsList) {
		this.itemsList = itemsList;
	}

	public UserProfilesSearchBean getUserProfileSearchBean() {
		return userProfileSearchBean;
	}

	public void setUserProfileSearchBean(
			UserProfilesSearchBean userProfileSearchBean) {
		this.userProfileSearchBean = userProfileSearchBean;
	}

	public List<SelectItem> getReportList() {
		return reportList;
	}

	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	public String getPpYear() {
		return ppYear;
	}

	public void setPpYear(String ppYear) {
		this.ppYear = ppYear;
	}

	public String getPpMinistry() {
		return ppMinistry;
	}

	public void setPpMinistry(String ppMinistry) {
		this.ppMinistry = ppMinistry;
	}

	public String getPpPTJ() {
		return ppPTJ;
	}

	public void setPpPTJ(String ppPTJ) {
		this.ppPTJ = ppPTJ;
	}

	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}

	public PmOrgProfile getSelectedPegwaiPengawal() {
		return selectedPegwaiPengawal;
	}

	public void setSelectedPegwaiPengawal(PmOrgProfile selectedPegwaiPengawal) {
		this.selectedPegwaiPengawal = selectedPegwaiPengawal;
	}

	public PmOrgProfile getSelectedKumpulanPTJ() {
		return selectedKumpulanPTJ;
	}

	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	public PmOrgProfile getSelectedPTJ() {
		return selectedPTJ;
	}

	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public PmRoleDesc getSelectedPmRoleDesc() {
		return selectedPmRoleDesc;
	}

	public void setSelectedPmRoleDesc(PmRoleDesc selectedPmRoleDesc) {
		this.selectedPmRoleDesc = selectedPmRoleDesc;
	}

	public HashMap<String, String> getLovValuesMap() {
		return lovValuesMap;
	}

	public void setLovValuesMap(HashMap<String, String> lovValuesMap) {
		this.lovValuesMap = lovValuesMap;
	}
}