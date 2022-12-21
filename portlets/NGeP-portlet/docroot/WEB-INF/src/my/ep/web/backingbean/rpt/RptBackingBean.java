package my.ep.web.backingbean.rpt;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.bean.pm.UserProfilesSearchBean;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.util.FacesScopes;

import org.apache.commons.collections.CollectionUtils;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "rptBackingBean")
@SessionScoped
public class RptBackingBean{

	private SelectItem[] itemsList;
	private Long userId;
	private String strLanguage;
	private String moduleValue;
	private String reportValue;
	private String reportCategory;
	private UserProfilesSearchBean userProfileSearchBean;
	private List<SelectItem> reportList;
	
	private String requestId;
	private String jobId;
	private boolean reportServerHasError;
	
	private String reportId;
	private String reportName;
	private boolean isExcel;
	private boolean isTempAccess;
	
	public RptBackingBean(){
		
		userId = new FacesScopes().getUserSessionBean().getUserId();
		
		strLanguage = ResourceBundleUtils.getMessage(
				ReportConst.LABEL_LANG, ResourceBundleUtils.BUNDLE_COMMON_LABELS,
				new FacesScopes().getUserLocale());
		
		userProfileSearchBean = new UserProfilesSearchBean();
	}
	
	public RptBackingBean(String reportId, String reportName, boolean isExcel, boolean isTempAccess) {
		
		this();
		
		this.reportId = reportId;
		this.reportName = reportName;
		this.isExcel = isExcel;
		this.isTempAccess = isTempAccess;
	}
	
	/**
	 * Xhtml method
	 * Value selected is stored in moduleValue 
	 */
	
	public List<SelectItem> getAllModuleList() {

		IPMReportSO reportSO = getPMReportSO();
			
		List<Object[]> reportModList = reportSO.retrieveModuleList(strLanguage,
				userId);
		
		return assignListToSelectItemValueLabel(reportModList);
	}
	
	public void getReportNameList(){

		if(moduleValue.equals("")){
			reportValue = "";
			return;
		}
		
		List<Object[]> reportModList = new ArrayList<Object[]>();
		
		IPMReportSO reportSO = getPMReportSO();
		
		if(isEnglish()){
			
			reportModList = reportSO
					.retrieveReportListByModuleNUserAuthorization(
							userId, moduleValue);
		}	

		if(isMalay()){
			
			reportModList = reportSO
					.retrieveMalayReportListByModuleNUserAuthorization(
							userId, moduleValue);
		}
		
		reportList = assignListToSelectItemLabelValue(reportModList);
	}
	
	public void generateReport(StringBuffer reportUrl){
		
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		
		RPTRequestResDTO resDTO = rptRequestSO.generateReport(reportUrl.toString(), "PP_004", "pp_detail_expenditure.jsp", this.userId, false, false);
		
		this.requestId = String.valueOf(resDTO.getRequestId());
		this.jobId = String.valueOf(resDTO.getJobId());
		this.reportServerHasError = resDTO.isReportServerHasError();
		
		RequestContext.getCurrentInstance().execute("ex.show();");

	}

	public IPMReportSO getPMReportSO(){
		return EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
	}

	public boolean isEnglish(){
		return strLanguage.equalsIgnoreCase(ReportConst.ENG_LANG);
	}
	
	public boolean isMalay(){
		return strLanguage.equalsIgnoreCase(ReportConst.MALAY_LANG);
	}
	
	public List<SelectItem> assignListToSelectItemValueLabel(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setValue(object[0].toString());
				selectItem.setLabel(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}	
	
	public List<SelectItem> assignListToSelectItemLabelValue(List<Object[]> list){

		List<SelectItem> selectItemList = new ArrayList<SelectItem>();
		
		if (CollectionUtils.isNotEmpty(list)) {
			
			for (Object[] object : list) {
			
				SelectItem selectItem = new SelectItem();
			
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				
				selectItemList.add(selectItem);
				
			}
		}
		
		return selectItemList;
	}
	
	public void preRenderView() {

		FacesContext fc = FacesContext.getCurrentInstance();
		
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, "rptcontentmain");
		
	}

	public SelectItem[] getItemsList() {
		return itemsList;
	}

	public void setItemsList(SelectItem[] itemsList) {
		this.itemsList = itemsList;
	}

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

	public String getModuleValue() {
		return moduleValue;
	}

	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}

	public String getReportValue() {
		return reportValue;
	}

	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
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

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public boolean isExcel() {
		return isExcel;
	}

	public void setExcel(boolean isExcel) {
		this.isExcel = isExcel;
	}

	public boolean isTempAccess() {
		return isTempAccess;
	}

	public void setTempAccess(boolean isTempAccess) {
		this.isTempAccess = isTempAccess;
	}
}