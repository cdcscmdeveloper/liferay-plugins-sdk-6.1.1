package my.ep.web.managedbean.report.sm;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.VBasicAccountWithMof;
import my.ep.persistence.entity.sm.VMofAccount;
import my.ep.persistence.entity.sm.VSupplierPerformance;
import my.ep.web.managedbean.report.ReportBaseManagedBean;
import my.ep.web.vo.CategoryRptVO;

public abstract class SmReportManagedBean extends ReportBaseManagedBean {
	private static final long serialVersionUID = 1L;

	private Date dateFrom;
	private Date dateTo;
	private Date dateExpTo;
	private Date dateExpFrom;
	private String zone;
	private PmOrgProfile selectedOrgProfile;
	private String mofNo;
	private String epNo;
	private String supplierType;
	private String regType;
	private String regNo;
	private String companyName;
	private String applNo;
	private String applType;
	private String documentType;
	private String state;
	private String district;
	private String year;
	private String yearTo;
	private String applStatus;
	private String suppDocMode;
	private String suppDocStatus;
	private String role;
	public String mofRegNoFrom;
	public String mofRegNoTo;
	private String reportType;
	private String paymentType;
	private String identificationNo;
	private String bussType;
	private String capitalLabel;
	private String capitalShareFrom;
	private String capitalShareTo;
	private String currYear;
	private String softCert;
	
	private Long regStatus;
	private boolean isSupplier;
	private boolean isCdcCollector;
	
	private List<SelectItem> regStatusLs;

	private List<SelectItem> applStatusList;
	private List<SelectItem> stateLst;
	private List<SelectItem> districtLst;
	private List<SelectItem> bussTypeList;
	private List<String> yearList;

	private List<String> selectedPaymentTypes;
	private List<CategoryRptVO> categoryLs;
	private List<CategoryRptVO> selectedCategoryLs;
	public String searchDate;
	public String orgTypeCode;
	public String orgTypeName;
	public List<SelectItem> agencyTypeItems;
	
	// #29712, #32564 - start
	private SmSupplier selectedSupplier;
	
	private VBasicAccountWithMof selectedBasicAccount;
	// #29712, #32564 - end
	
	// #36125
	private VSupplierPerformance selectedSupplierPerformance;

	public VBasicAccountWithMof getSelectedBasicAccount() {
		return selectedBasicAccount;
	}

	public void setSelectedBasicAccount(VBasicAccountWithMof selectedBasicAccount) {
		this.selectedBasicAccount = selectedBasicAccount;
	}

	// #30824 - START
	private VMofAccount selectedMofAccount;

	public VMofAccount getSelectedMofAccount() {
		return selectedMofAccount;
	}

	public void setSelectedMofAccount(VMofAccount selectedMofAccount) {
		this.selectedMofAccount = selectedMofAccount;
	}

	// #30824 - END

	//#32521 - START
	private PmUser selectedUser;
	private String disciplinaryActionStatus;
	//#32521 - END
	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public PmOrgProfile getSelectedOrgProfile() {
		return selectedOrgProfile;
	}

	public void setSelectedOrgProfile(PmOrgProfile selectedOrgProfile) {
		this.selectedOrgProfile = selectedOrgProfile;
	}

	public String getMofNo() {
		return mofNo;
	}

	public void setMofNo(String mofNo) {
		this.mofNo = mofNo;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getApplNo() {
		return applNo;
	}

	public void setApplNo(String applNo) {
		this.applNo = applNo;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	}

	public List<SelectItem> getStateLst() {
		return stateLst;
	}

	public void setStateLst(List<SelectItem> stateLst) {
		this.stateLst = stateLst;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<String> getYearList() {
		return yearList;
	}

	public void setYearList(List<String> yearList) {
		this.yearList = yearList;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYearTo() {
		return yearTo;
	}

	public void setYearTo(String yearTo) {
		this.yearTo = yearTo;
	}

	public String getApplStatus() {
		return applStatus;
	}

	public void setApplStatus(String applStatus) {
		this.applStatus = applStatus;
	}

	public String getSuppDocMode() {
		return suppDocMode;
	}

	public void setSuppDocMode(String suppDocMode) {
		this.suppDocMode = suppDocMode;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public List<SelectItem> getDistrictLst() {
		return districtLst;
	}

	public void setDistrictLst(List<SelectItem> districtLst) {
		this.districtLst = districtLst;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getMofRegNoFrom() {
		return mofRegNoFrom;
	}

	public void setMofRegNoFrom(String mofRegNoFrom) {
		this.mofRegNoFrom = mofRegNoFrom;
	}

	public String getMofRegNoTo() {
		return mofRegNoTo;
	}

	public void setMofRegNoTo(String mofRegNoTo) {
		this.mofRegNoTo = mofRegNoTo;
	}

	public List<String> getSelectedPaymentTypes() {
		return selectedPaymentTypes;
	}

	public void setSelectedPaymentTypes(List<String> selectedPaymentTypes) {
		this.selectedPaymentTypes = selectedPaymentTypes;
	}

	public String getEpNo() {
		return epNo;
	}

	public void setEpNo(String epNo) {
		this.epNo = epNo;
	}

	public String getSuppDocStatus() {
		return suppDocStatus;
	}

	public void setSuppDocStatus(String suppDocStatus) {
		this.suppDocStatus = suppDocStatus;
	}

	public List<SelectItem> getApplStatusList() {
		return applStatusList;
	}

	public void setApplStatusList(List<SelectItem> applStatusList) {
		this.applStatusList = applStatusList;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public String getIdentificationNo() {
		return identificationNo;
	}

	public void setIdentificationNo(String identificationNo) {
		this.identificationNo = identificationNo;
	}

	public Date getDateExpTo() {
		return dateExpTo;
	}

	public void setDateExpTo(Date dateExpTo) {
		this.dateExpTo = dateExpTo;
	}

	public Date getDateExpFrom() {
		return dateExpFrom;
	}

	public void setDateExpFrom(Date dateExpFrom) {
		this.dateExpFrom = dateExpFrom;
	}

	public List<CategoryRptVO> getCategoryLs() {
		return categoryLs;
	}

	public void setCategoryLs(List<CategoryRptVO> categoryLs) {
		this.categoryLs = categoryLs;
	}

	public List<CategoryRptVO> getSelectedCategoryLs() {
		return selectedCategoryLs;
	}

	public void setSelectedCategoryLs(List<CategoryRptVO> selectedCategoryLs) {
		this.selectedCategoryLs = selectedCategoryLs;
	}
	
	public String getBussType() {
		return bussType;
	}

	public void setBussType(String bussType) {
		this.bussType = bussType;
	}

	public String getCapitalLabel() {
		return capitalLabel;
	}

	public void setCapitalLabel(String capitalLabel) {
		this.capitalLabel = capitalLabel;
	}

	public List<SelectItem> getBussTypeList() {
		return bussTypeList;
	}

	public void setBussTypeList(List<SelectItem> bussTypeList) {
		this.bussTypeList = bussTypeList;
	}

	public String getCapitalShareFrom() {
		return capitalShareFrom;
	}

	public void setCapitalShareFrom(String capitalShareFrom) {
		this.capitalShareFrom = capitalShareFrom;
	}

	public String getCapitalShareTo() {
		return capitalShareTo;
	}

	public void setCapitalShareTo(String capitalShareTo) {
		this.capitalShareTo = capitalShareTo;
	}

	public String getCurrYear() {
		if (StringUtils.isBlank(currYear)) {
			return Calendar.getInstance().get(Calendar.YEAR) + "";
		} else {
			return currYear;
		}
	}

	public Long getRegStatus() {
		return regStatus;
	}

	public void setRegStatus(Long regStatus) {
		this.regStatus = regStatus;
	}

	public List<SelectItem> getRegStatusLs() {
		return regStatusLs;
	}

	public void setRegStatusLs(List<SelectItem> regStatusLs) {
		this.regStatusLs = regStatusLs;
	}

	public String getSoftCert() {
		return softCert;
	}

	public void setSoftCert(String softCert) {
		this.softCert = softCert;
	}

	public boolean isSupplier() {
		return isSupplier;
	}

	public void setSupplier(boolean isSupplier) {
		this.isSupplier = isSupplier;
	}

	public boolean isCdcCollector() {
		return isCdcCollector;
	}

	public void setCdcCollector(boolean isCdcCollector) {
		this.isCdcCollector = isCdcCollector;
	}
	
	public String getSearchDate() {
		return searchDate;
	}

	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}

	public String getOrgTypeCode() {
		return orgTypeCode;
	}

	public void setOrgTypeCode(String orgTypeCode) {
		this.orgTypeCode = orgTypeCode;
	}

	public List<SelectItem> getAgencyTypeItems() {
		return agencyTypeItems;
	}

	public void setAgencyTypeItems(List<SelectItem> agencyTypeItems) {
		this.agencyTypeItems = agencyTypeItems;
	}
	
	public String getOrgTypeName() {
		return orgTypeName;
	}

	public void setOrgTypeName(String orgTypeName) {
		this.orgTypeName = orgTypeName;
	}
	
	// #29712 - start
	public SmSupplier getSelectedSupplier() {
		return selectedSupplier;
	}

	public void setSelectedSupplier(SmSupplier selectedSupplier) {
		this.selectedSupplier = selectedSupplier;
	}
	// #29712 - end
	//#32521 - START
	public PmUser getSelectedUser() {
		return selectedUser;
	}

	public void setSelectedUser(PmUser selectedUser) {
		this.selectedUser = selectedUser;
	}

	public String getDisciplinaryActionStatus() {
		return disciplinaryActionStatus;
	}

	public void setDisciplinaryActionStatus(String disciplinaryActionStatus) {
		this.disciplinaryActionStatus = disciplinaryActionStatus;
	}
	//#32521 - END
	public VSupplierPerformance getSelectedSupplierPerformance() {
		return selectedSupplierPerformance;
	}

	public void setSelectedSupplierPerformance(
			VSupplierPerformance selectedSupplierPerformance) {
		this.selectedSupplierPerformance = selectedSupplierPerformance;
	}
}
