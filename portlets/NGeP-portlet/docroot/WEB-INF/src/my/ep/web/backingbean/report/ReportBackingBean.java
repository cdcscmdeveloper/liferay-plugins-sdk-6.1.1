package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.categorycode.ICategoryCodeSO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.rpt.IPMReportSO;
import my.ep.biz.service.sc.quationtender.IQTDetailSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeReqDTO;
import my.ep.dto.pm.common.categorycode.CategoryCodeResDTO;
import my.ep.dto.pm.common.district.DistrictReqDTO;
import my.ep.dto.pm.common.district.DistrictResDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sc.quationtender.QuationTenderReqDTO;
import my.ep.dto.sc.quationtender.QuationTenderResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserGroup;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtBsvDtl;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.sc.quationtender.common.QTConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;
import com.liferay.portal.util.PortalUtil;

/**
 * @Author Jangari Kondal Reddy
 */
@ManagedBean(name = "reportBackingBean")
@ViewScoped
public class ReportBackingBean extends
		SourcingBaseBackingBean<ReportMangedBean> {
	private static final Logger log = EpLogger
			.getLogger(ReportBackingBean.class);

	public ReportBackingBean() {
	}

	private String moduleValue;
	private String reportName;
	private String reportValue;
	private ScQt scqt;
	private List<SelectItem> reportList = new ArrayList<SelectItem>();
	private List<SelectItem> moduleList;
	private List<SelectItem> regTypeList;
	ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<String> authoriztionList = new ArrayList<String>();
	private List<String> orgProfileList = new ArrayList<String>();
	private List<String> scYearList = new ArrayList<String>();
	private List<SelectItem> orgProfileIdNameList = new ArrayList<SelectItem>();
	private List<SelectItem> userListbyprofileRole = new ArrayList<SelectItem>();
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();
	private List<SelectItem> districtLst = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst = new ArrayList<SelectItem>();
	private List<SelectItem> procurementList = new ArrayList<SelectItem>();
	private List<SelectItem> scProcureAllList = new ArrayList<SelectItem>();
	private List<SelectItem> scQuotationList = new ArrayList<SelectItem>();
	private List<SelectItem> scTenderList = new ArrayList<SelectItem>();
	private List<SelectItem> scRstrctdTndrList = new ArrayList<SelectItem>();
	private List<SelectItem> scMonthList = new ArrayList<SelectItem>();

	private List<SelectItem> requisitionerList = new ArrayList<SelectItem>();
	private List<SelectItem> statusList = new ArrayList<SelectItem>();
	private List<SelectItem> ministryList = new ArrayList<SelectItem>();
	private List<SelectItem> ptjList = new ArrayList<SelectItem>();
	// private List<PmOrgProfile> ptjList;
	private List<String> loanList = new ArrayList<String>();
	private List<SelectItem> unspcList = new ArrayList<SelectItem>();
	private List<SelectItem> brandList = new ArrayList<SelectItem>();
	private List<SelectItem> extTypeList = new ArrayList<SelectItem>();
	private List<SelectItem> measurmentList = new ArrayList<SelectItem>();
	private List<SelectItem> colorList = new ArrayList<SelectItem>();

	SCReportBackingBean scReportBackingBean;
	CMReportBackingBean cmReportBackingBean;
	FLReportBackingBean flReportBackingBean;
	SMReportBackingBean smReportBackingBean;
	CTReportBackingBean ctReportBackingBean;
	Long unspid = null;
	private List<SelectItem> levOneCatLst2 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst3 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst4 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst5 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst6 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst7 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst8 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst9 = new ArrayList<SelectItem>();
	private List<SelectItem> levOneCatLst10 = new ArrayList<SelectItem>();

	private List<SelectItem> levTwoCatLst1 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst1 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst2 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst2 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst3 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst3 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst4 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst4 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst5 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst5 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst6 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst6 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst7 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst7 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst8 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst8 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst9 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst9 = new ArrayList<SelectItem>();
	private List<SelectItem> levTwoCatLst10 = new ArrayList<SelectItem>();
	private List<SelectItem> levThreeCatLst10 = new ArrayList<SelectItem>();
	private boolean valueReqd;
	private boolean showParentMsg;
	private List<SmSupplierCategory> selectedCatList;
	private List<SmSupplierCategory> selectCatDeletionList;

	public List<SmSupplierCategory> getSelectCatDeletionList() {
		return selectCatDeletionList;
	}

	public void setSelectCatDeletionList(
			List<SmSupplierCategory> selectCatDeletionList) {
		this.selectCatDeletionList = selectCatDeletionList;
	}

	// private SmCompanyExperience selectedCompanyExperience;
	// private List<SmCompanyExperienceCat> smCompanyExperienceCats;
	private boolean selectedAll;
	private boolean selected;
	private List<PmCategoryL1> supplierParentCatList;
	private List<PmCategoryL2> supplierChildCatList;
	private List<PmCategoryL3> supplierSubChildCatList;

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public List<PmCategoryL2> getSupplierChildCatList() {
		return supplierChildCatList;
	}

	public void setSupplierChildCatList(List<PmCategoryL2> supplierChildCatList) {
		this.supplierChildCatList = supplierChildCatList;
	}

	public List<PmCategoryL3> getSupplierSubChildCatList() {
		return supplierSubChildCatList;
	}

	public void setSupplierSubChildCatList(
			List<PmCategoryL3> supplierSubChildCatList) {
		this.supplierSubChildCatList = supplierSubChildCatList;
	}

	public List<PmCategoryL1> getSupplierParentCatList() {
		return supplierParentCatList;
	}

	public void setSupplierParentCatList(
			List<PmCategoryL1> supplierParentCatList) {
		this.supplierParentCatList = supplierParentCatList;
	}

	public boolean isSelectedAll() {
		return selectedAll;
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public List<SmSupplierCategory> getSelectedCatList() {
		return selectedCatList;
	}

	public void setSelectedCatList(List<SmSupplierCategory> selectedCatList) {
		this.selectedCatList = selectedCatList;
	}

	/*
	 * public SmCompanyExperience getSelectedCompanyExperience() { return
	 * selectedCompanyExperience; } public void setSelectedCompanyExperience(
	 * SmCompanyExperience selectedCompanyExperience) {
	 * this.selectedCompanyExperience = selectedCompanyExperience; }
	 */
	public boolean isValueReqd() {
		return valueReqd;
	}

	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	public List<SelectItem> getLevOneCatLst2() {
		return levOneCatLst2;
	}

	public void setLevOneCatLst2(List<SelectItem> levOneCatLst2) {
		this.levOneCatLst2 = levOneCatLst2;
	}

	public List<SelectItem> getLevOneCatLst3() {
		return levOneCatLst3;
	}

	public void setLevOneCatLst3(List<SelectItem> levOneCatLst3) {
		this.levOneCatLst3 = levOneCatLst3;
	}

	public List<SelectItem> getLevOneCatLst4() {
		return levOneCatLst4;
	}

	public void setLevOneCatLst4(List<SelectItem> levOneCatLst4) {
		this.levOneCatLst4 = levOneCatLst4;
	}

	public List<SelectItem> getLevOneCatLst5() {
		return levOneCatLst5;
	}

	public void setLevOneCatLst5(List<SelectItem> levOneCatLst5) {
		this.levOneCatLst5 = levOneCatLst5;
	}

	public List<SelectItem> getLevOneCatLst6() {
		return levOneCatLst6;
	}

	public void setLevOneCatLst6(List<SelectItem> levOneCatLst6) {
		this.levOneCatLst6 = levOneCatLst6;
	}

	public List<SelectItem> getLevOneCatLst7() {
		return levOneCatLst7;
	}

	public void setLevOneCatLst7(List<SelectItem> levOneCatLst7) {
		this.levOneCatLst7 = levOneCatLst7;
	}

	public List<SelectItem> getLevOneCatLst8() {
		return levOneCatLst8;
	}

	public void setLevOneCatLst8(List<SelectItem> levOneCatLst8) {
		this.levOneCatLst8 = levOneCatLst8;
	}

	public List<SelectItem> getLevOneCatLst9() {
		return levOneCatLst9;
	}

	public void setLevOneCatLst9(List<SelectItem> levOneCatLst9) {
		this.levOneCatLst9 = levOneCatLst9;
	}

	public List<SelectItem> getLevOneCatLst10() {
		return levOneCatLst10;
	}

	public void setLevOneCatLst10(List<SelectItem> levOneCatLst10) {
		this.levOneCatLst10 = levOneCatLst10;
	}

	public List<SelectItem> getLevTwoCatLst1() {
		return levTwoCatLst1;
	}

	public void setLevTwoCatLst1(List<SelectItem> levTwoCatLst1) {
		this.levTwoCatLst1 = levTwoCatLst1;
	}

	public List<SelectItem> getLevThreeCatLst1() {
		return levThreeCatLst1;
	}

	public void setLevThreeCatLst1(List<SelectItem> levThreeCatLst1) {
		this.levThreeCatLst1 = levThreeCatLst1;
	}

	public List<SelectItem> getLevTwoCatLst2() {
		return levTwoCatLst2;
	}

	public void setLevTwoCatLst2(List<SelectItem> levTwoCatLst2) {
		this.levTwoCatLst2 = levTwoCatLst2;
	}

	public List<SelectItem> getLevThreeCatLst2() {
		return levThreeCatLst2;
	}

	public void setLevThreeCatLst2(List<SelectItem> levThreeCatLst2) {
		this.levThreeCatLst2 = levThreeCatLst2;
	}

	public List<SelectItem> getLevTwoCatLst3() {
		return levTwoCatLst3;
	}

	public void setLevTwoCatLst3(List<SelectItem> levTwoCatLst3) {
		this.levTwoCatLst3 = levTwoCatLst3;
	}

	public List<SelectItem> getLevThreeCatLst3() {
		return levThreeCatLst3;
	}

	public void setLevThreeCatLst3(List<SelectItem> levThreeCatLst3) {
		this.levThreeCatLst3 = levThreeCatLst3;
	}

	public List<SelectItem> getLevTwoCatLst4() {
		return levTwoCatLst4;
	}

	public void setLevTwoCatLst4(List<SelectItem> levTwoCatLst4) {
		this.levTwoCatLst4 = levTwoCatLst4;
	}

	public List<SelectItem> getLevThreeCatLst4() {
		return levThreeCatLst4;
	}

	public void setLevThreeCatLst4(List<SelectItem> levThreeCatLst4) {
		this.levThreeCatLst4 = levThreeCatLst4;
	}

	public List<SelectItem> getLevTwoCatLst5() {
		return levTwoCatLst5;
	}

	public void setLevTwoCatLst5(List<SelectItem> levTwoCatLst5) {
		this.levTwoCatLst5 = levTwoCatLst5;
	}

	public List<SelectItem> getLevThreeCatLst5() {
		return levThreeCatLst5;
	}

	public void setLevThreeCatLst5(List<SelectItem> levThreeCatLst5) {
		this.levThreeCatLst5 = levThreeCatLst5;
	}

	public List<SelectItem> getLevTwoCatLst6() {
		return levTwoCatLst6;
	}

	public void setLevTwoCatLst6(List<SelectItem> levTwoCatLst6) {
		this.levTwoCatLst6 = levTwoCatLst6;
	}

	public List<SelectItem> getLevThreeCatLst6() {
		return levThreeCatLst6;
	}

	public void setLevThreeCatLst6(List<SelectItem> levThreeCatLst6) {
		this.levThreeCatLst6 = levThreeCatLst6;
	}

	public List<SelectItem> getLevTwoCatLst7() {
		return levTwoCatLst7;
	}

	public void setLevTwoCatLst7(List<SelectItem> levTwoCatLst7) {
		this.levTwoCatLst7 = levTwoCatLst7;
	}

	public List<SelectItem> getLevThreeCatLst7() {
		return levThreeCatLst7;
	}

	public void setLevThreeCatLst7(List<SelectItem> levThreeCatLst7) {
		this.levThreeCatLst7 = levThreeCatLst7;
	}

	public List<SelectItem> getLevTwoCatLst8() {
		return levTwoCatLst8;
	}

	public void setLevTwoCatLst8(List<SelectItem> levTwoCatLst8) {
		this.levTwoCatLst8 = levTwoCatLst8;
	}

	public List<SelectItem> getLevThreeCatLst8() {
		return levThreeCatLst8;
	}

	public void setLevThreeCatLst8(List<SelectItem> levThreeCatLst8) {
		this.levThreeCatLst8 = levThreeCatLst8;
	}

	public List<SelectItem> getLevTwoCatLst9() {
		return levTwoCatLst9;
	}

	public void setLevTwoCatLst9(List<SelectItem> levTwoCatLst9) {
		this.levTwoCatLst9 = levTwoCatLst9;
	}

	public List<SelectItem> getLevThreeCatLst9() {
		return levThreeCatLst9;
	}

	public void setLevThreeCatLst9(List<SelectItem> levThreeCatLst9) {
		this.levThreeCatLst9 = levThreeCatLst9;
	}

	public List<SelectItem> getLevTwoCatLst10() {
		return levTwoCatLst10;
	}

	public void setLevTwoCatLst10(List<SelectItem> levTwoCatLst10) {
		this.levTwoCatLst10 = levTwoCatLst10;
	}

	public List<SelectItem> getLevThreeCatLst10() {
		return levThreeCatLst10;
	}

	public void setLevThreeCatLst10(List<SelectItem> levThreeCatLst10) {
		this.levThreeCatLst10 = levThreeCatLst10;
	}

	public List<SelectItem> getBrandList() {
		return brandList;
	}

	public void setBrandList(List<SelectItem> brandList) {
		this.brandList = brandList;
	}

	public List<SelectItem> getExtTypeList() {
		return extTypeList;
	}

	public void setExtTypeList(List<SelectItem> extTypeList) {
		this.extTypeList = extTypeList;
	}

	public List<SelectItem> getMeasurmentList() {
		return measurmentList;
	}

	public void setMeasurmentList(List<SelectItem> measurmentList) {
		this.measurmentList = measurmentList;
	}

	public List<SelectItem> getColorList() {
		return colorList;
	}

	public void setColorList(List<SelectItem> colorList) {
		this.colorList = colorList;
	}

	public Long getUnspid() {
		return unspid;
	}

	public void setUnspid(Long unspid) {
		this.unspid = unspid;
	}

	public List<SelectItem> getUnspcList() {
		return unspcList;
	}

	public void setUnspcList(List<SelectItem> unspcList) {
		this.unspcList = unspcList;
	}

	public String getReportValue() {
		return reportValue;
	}

	public void setReportValue(String reportValue) {
		this.reportValue = reportValue;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getModuleValue() {
		return moduleValue;
	}

	public void setModuleValue(String moduleValue) {
		this.moduleValue = moduleValue;
	}

	public CTReportBackingBean getCtReportBackingBean() {
		return ctReportBackingBean;
	}

	public void setCtReportBackingBean(CTReportBackingBean ctReportBackingBean) {
		this.ctReportBackingBean = ctReportBackingBean;
	}

	public SCReportBackingBean getScReportBackingBean() {
		return scReportBackingBean;
	}

	public void setScReportBackingBean(SCReportBackingBean scReportBackingBean) {
		this.scReportBackingBean = scReportBackingBean;
	}

	public CMReportBackingBean getCmReportBackingBean() {
		return cmReportBackingBean;
	}

	public void setCmReportBackingBean(CMReportBackingBean cmReportBackingBean) {
		this.cmReportBackingBean = cmReportBackingBean;
	}

	public FLReportBackingBean getFlReportBackingBean() {
		return flReportBackingBean;
	}

	public void setFlReportBackingBean(FLReportBackingBean flReportBackingBean) {
		this.flReportBackingBean = flReportBackingBean;
	}

	public SMReportBackingBean getSmReportBackingBean() {
		return smReportBackingBean;
	}

	public void setSmReportBackingBean(SMReportBackingBean smReportBackingBean) {
		this.smReportBackingBean = smReportBackingBean;
	}

	public List<SelectItem> getOrgProfileIdNameList() {
		return orgProfileIdNameList;
	}

	public void setOrgProfileIdNameList(List<SelectItem> orgProfileIdNameList) {
		this.orgProfileIdNameList = orgProfileIdNameList;
	}

	public List<SelectItem> getScMonthList() {
		return scMonthList;
	}

	public void setScMonthList(List<SelectItem> scMonthList) {
		this.scMonthList = scMonthList;
	}

	public List<String> getScYearList() {
		return scYearList;
	}

	public void setScYearList(List<String> scYearList) {
		this.scYearList = scYearList;
	}

	public List<SelectItem> getUserListbyprofileRole() {
		return userListbyprofileRole;
	}

	public void setUserListbyprofileRole(List<SelectItem> userListbyprofileRole) {
		this.userListbyprofileRole = userListbyprofileRole;
	}

	public List<String> getOrgProfileList() {
		return orgProfileList;
	}

	public void setOrgProfileList(List<String> orgProfileList) {
		this.orgProfileList = orgProfileList;
	}

	public List<String> getLoanList() {
		return loanList;
	}

	public void setLoanList(List<String> loanList) {
		this.loanList = loanList;
	}

	public List<SelectItem> getRequisitionerList() {
		return requisitionerList;
	}

	public void setRequisitionerList(List<SelectItem> requisitionerList) {
		this.requisitionerList = requisitionerList;
	}

	public List<SelectItem> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<SelectItem> statusList) {
		this.statusList = statusList;
	}

	public List<SelectItem> getMinistryList() {
		return ministryList;
	}

	public void setMinistryList(List<SelectItem> ministryList) {
		this.ministryList = ministryList;
	}

	public List<SelectItem> getPtjList() {
		return ptjList;
	}

	public void setPtjList(List<SelectItem> ptjList) {
		this.ptjList = ptjList;
	}

	private String localToDateVar;

	/*
	 * public List<PmOrgProfile> getPtjList() { return ptjList; } public void
	 * setPtjList(List<PmOrgProfile> ptjList) { this.ptjList = ptjList; }
	 */
	private String userId;
	private long lngUserId;
	private String roleCode;
	private Date fromDate;
	private Date toDate;
	private String subModule;
	// sc
	private String scQuotationId;
	private String scQuotationNo;
	private String scPaymentId;
	private String scCreatedToYear;
	private String scCreatedToMonth;
	private String scCreatedFromYear;
	private String scCreatedFromMonth;
	private String scDeskOfficer;
	private String scPtj;
	private String scStatus;
	private Date scPublishFromDate;
	private Date scPublishToDate;
	private Date scSoneCrtdFromDate;
	private Date scSoneCrtdToDate;
	private String scRequisenerId;
	private String scSupplier;
	private String scLogin;
	private String scPIno;
	private String scSoneStatus;
	private String scPrcRptGroup;
	private String scPIDtlsPrcRptGroup;
	private String scProcurementType;
	private String scProcessRptQuotationNo;

	private String scCommonStatus;
	private String scLAPRNPURCHDEFAULTGroup;
	private String scREQUESTNOTESUMMARYGroup;
	private String scPIDNo;
	private String scContractOrPKPType;
	private String scContractOrPKPNo;
	private String scContractOrPKPName;
	private String scRequestQuoteNo;
	// cm
	private String cmItemType;
	private String cmItemStatus;
	private String cmUnspscCode;
	private String cmUnspscTitle;
	private String cmCompanyName;
	private String cmRegistrationType;
	private String cmItemCategory;
	private String cmItemName;
	private String cmEpNumber;
	private String cmBusinessRegNo;
	private String cmIcNumber;
	private String cmMofNumber;
	private String cmExtentionCode;
	private String cmExtentionType;
	private String cmExtentionBrand;
	private String cmExtentionMeasurment;
	private String cmExtentionColour;
	private String cmProofIdentity;
	private String cmSupplOrAdmin;
	private String cmExtention;
	private String cmGroupOne;
	// fl
	private String flItmType;
	private String flPtj;
	private String flContractNo;
	private String flSst;
	private String flJabatan;
	private String flMinistry;
	private String flSupplier;
	private String flStatus;
	private String flDuration;
	private String flRecievingOfficer;
	private String flDeliveryAddress;
	private String flRecNoteNo;
	private String flContractType;
	private String flRequisitioner;
	private String flMtoContract;
	private String flInvoiceNo;
	private String flDeductionType;
	private String flCrNo;
	private String flApproverName;
	private String flDocumentType;
	private String flCriteria;
	private String flScoring;
	private String flPkpNo;
	private String flDeliveryOrderNo;
	private String flItemCode;
	private String flPoAmount;
	private String flPrNo;
	private String flPmo;
	private String flItemName;
	private String flContractOrder;
	private String flContractRequest;
	private String flPurchaseOrderNo;
	private String flGroupOne;
	private String flGroupTwo;
	private String flGroupThree;
	private String flGroupFour;
	private String flGroupFive;
	private String flGroupSix;
	private String flGroupSeven;
	private String flGroupEight;
	private String flGroupNine;
	private String flGroupTen;
	private String flGroupEleven;
	private String flGroupTwelve;
	private String flGroupThirteen;
	private String flGroupFourteen;
	private String flGroupFifteen;
	private String flGroupSixteen;
	// sm
	private String smCreatedFromYear;
	private String smCreatedToYear;
	private String smMonthlyRptByYear;
	private String smSVOUser;
	private String smApplicationNo;
	private String smZone;
	private String smSupplierType;
	private String smMofRegNo;
	private String smRegistrationStatus;
	private String smcategory;
	private String smMainCategory;
	private String smSubCategory;
	private String smSubCategoryDivision;
	private String smApplicationStatus;
	private String smPaymentType;
	private String smApplicationType;
	private String smState;
	private String smDistrict;
	private String smReportType;
	private String smCompanyName;
	private String smRegistrationType;
	private String smYearText;
	private String smMainCategory1;
	private String smSubCategory1;
	private String smSubCategoryDivision1;
	private String smMainCategory2;
	private String smSubCategory2;
	private String smSubCategoryDivision2;
	private String smMainCategory3;
	private String smSubCategory3;
	private String smSubCategoryDivision3;
	private String smMainCategory4;
	private String smSubCategory4;
	private String smSubCategoryDivision4;
	private String smMainCategory5;
	private String smSubCategory5;
	private String smSubCategoryDivision5;
	private String smMainCategory6;
	private String smSubCategory6;
	private String smSubCategoryDivision6;
	private String smMainCategory7;
	private String smSubCategory7;
	private String smSubCategoryDivision7;
	private String smMainCategory8;
	private String smSubCategory8;
	private String smSubCategoryDivision8;
	private String smMainCategory9;
	private String smSubCategory9;
	private String smSubCategoryDivision9;
	private String smMainCategory10;
	private String smSubCategory10;
	private String smSubCategoryDivision10;

	// CT
	private String ctContractNo;
	private String ctAgreement;
	private String ctStatus;
	private String ctContrMontrStatus;
	private String ctComplete;
	private String ctGroupOne;
	private String ctGroupTwo;
	private String ctProductService;
	private String ctLoanNo;
	private String ctMinistry;
	private String ctPtj;
	private String ctProcurementMode;
	

	public String getCtProcurementMode() {
		return ctProcurementMode;
	}

	public void setCtProcurementMode(String ctProcurementMode) {
		this.ctProcurementMode = ctProcurementMode;
	}

	public String getSmMainCategory1() {
		return smMainCategory1;
	}

	public void setSmMainCategory1(String smMainCategory1) {
		this.smMainCategory1 = smMainCategory1;
	}

	public String getSmSubCategory1() {
		return smSubCategory1;
	}

	public void setSmSubCategory1(String smSubCategory1) {
		this.smSubCategory1 = smSubCategory1;
	}

	public String getSmSubCategoryDivision1() {
		return smSubCategoryDivision1;
	}

	public void setSmSubCategoryDivision1(String smSubCategoryDivision1) {
		this.smSubCategoryDivision1 = smSubCategoryDivision1;
	}

	public String getSmMainCategory2() {
		return smMainCategory2;
	}

	public void setSmMainCategory2(String smMainCategory2) {
		this.smMainCategory2 = smMainCategory2;
	}

	public String getSmSubCategory2() {
		return smSubCategory2;
	}

	public void setSmSubCategory2(String smSubCategory2) {
		this.smSubCategory2 = smSubCategory2;
	}

	public String getSmSubCategoryDivision2() {
		return smSubCategoryDivision2;
	}

	public void setSmSubCategoryDivision2(String smSubCategoryDivision2) {
		this.smSubCategoryDivision2 = smSubCategoryDivision2;
	}

	public String getSmMainCategory3() {
		return smMainCategory3;
	}

	public void setSmMainCategory3(String smMainCategory3) {
		this.smMainCategory3 = smMainCategory3;
	}

	public String getSmSubCategory3() {
		return smSubCategory3;
	}

	public void setSmSubCategory3(String smSubCategory3) {
		this.smSubCategory3 = smSubCategory3;
	}

	public String getSmSubCategoryDivision3() {
		return smSubCategoryDivision3;
	}

	public void setSmSubCategoryDivision3(String smSubCategoryDivision3) {
		this.smSubCategoryDivision3 = smSubCategoryDivision3;
	}

	public String getSmMainCategory4() {
		return smMainCategory4;
	}

	public void setSmMainCategory4(String smMainCategory4) {
		this.smMainCategory4 = smMainCategory4;
	}

	public String getSmSubCategory4() {
		return smSubCategory4;
	}

	public void setSmSubCategory4(String smSubCategory4) {
		this.smSubCategory4 = smSubCategory4;
	}

	public String getSmSubCategoryDivision4() {
		return smSubCategoryDivision4;
	}

	public void setSmSubCategoryDivision4(String smSubCategoryDivision4) {
		this.smSubCategoryDivision4 = smSubCategoryDivision4;
	}

	public String getSmMainCategory5() {
		return smMainCategory5;
	}

	public void setSmMainCategory5(String smMainCategory5) {
		this.smMainCategory5 = smMainCategory5;
	}

	public String getSmSubCategory5() {
		return smSubCategory5;
	}

	public void setSmSubCategory5(String smSubCategory5) {
		this.smSubCategory5 = smSubCategory5;
	}

	public String getSmSubCategoryDivision5() {
		return smSubCategoryDivision5;
	}

	public void setSmSubCategoryDivision5(String smSubCategoryDivision5) {
		this.smSubCategoryDivision5 = smSubCategoryDivision5;
	}

	public String getSmMainCategory6() {
		return smMainCategory6;
	}

	public void setSmMainCategory6(String smMainCategory6) {
		this.smMainCategory6 = smMainCategory6;
	}

	public String getSmSubCategory6() {
		return smSubCategory6;
	}

	public void setSmSubCategory6(String smSubCategory6) {
		this.smSubCategory6 = smSubCategory6;
	}

	public String getSmSubCategoryDivision6() {
		return smSubCategoryDivision6;
	}

	public void setSmSubCategoryDivision6(String smSubCategoryDivision6) {
		this.smSubCategoryDivision6 = smSubCategoryDivision6;
	}

	public String getSmMainCategory7() {
		return smMainCategory7;
	}

	public void setSmMainCategory7(String smMainCategory7) {
		this.smMainCategory7 = smMainCategory7;
	}

	public String getSmSubCategory7() {
		return smSubCategory7;
	}

	public void setSmSubCategory7(String smSubCategory7) {
		this.smSubCategory7 = smSubCategory7;
	}

	public String getSmSubCategoryDivision7() {
		return smSubCategoryDivision7;
	}

	public void setSmSubCategoryDivision7(String smSubCategoryDivision7) {
		this.smSubCategoryDivision7 = smSubCategoryDivision7;
	}

	public String getSmMainCategory8() {
		return smMainCategory8;
	}

	public void setSmMainCategory8(String smMainCategory8) {
		this.smMainCategory8 = smMainCategory8;
	}

	public String getSmSubCategory8() {
		return smSubCategory8;
	}

	public void setSmSubCategory8(String smSubCategory8) {
		this.smSubCategory8 = smSubCategory8;
	}

	public String getSmSubCategoryDivision8() {
		return smSubCategoryDivision8;
	}

	public void setSmSubCategoryDivision8(String smSubCategoryDivision8) {
		this.smSubCategoryDivision8 = smSubCategoryDivision8;
	}

	public String getSmMainCategory9() {
		return smMainCategory9;
	}

	public void setSmMainCategory9(String smMainCategory9) {
		this.smMainCategory9 = smMainCategory9;
	}

	public String getSmSubCategory9() {
		return smSubCategory9;
	}

	public void setSmSubCategory9(String smSubCategory9) {
		this.smSubCategory9 = smSubCategory9;
	}

	public String getSmSubCategoryDivision9() {
		return smSubCategoryDivision9;
	}

	public void setSmSubCategoryDivision9(String smSubCategoryDivision9) {
		this.smSubCategoryDivision9 = smSubCategoryDivision9;
	}

	public String getSmMainCategory10() {
		return smMainCategory10;
	}

	public void setSmMainCategory10(String smMainCategory10) {
		this.smMainCategory10 = smMainCategory10;
	}

	public String getSmSubCategory10() {
		return smSubCategory10;
	}

	public void setSmSubCategory10(String smSubCategory10) {
		this.smSubCategory10 = smSubCategory10;
	}

	public String getSmSubCategoryDivision10() {
		return smSubCategoryDivision10;
	}

	public void setSmSubCategoryDivision10(String smSubCategoryDivision10) {
		this.smSubCategoryDivision10 = smSubCategoryDivision10;
	}

	public String getSmYearText() {
		return smYearText;
	}

	public void setSmYearText(String smYearText) {
		this.smYearText = smYearText;
	}

	public String getCmGroupOne() {
		return cmGroupOne;
	}

	public void setCmGroupOne(String cmGroupOne) {
		this.cmGroupOne = cmGroupOne;
	}

	public String getCtMinistry() {
		return ctMinistry;
	}

	public void setCtMinistry(String ctMinistry) {
		this.ctMinistry = ctMinistry;
	}

	public String getCtPtj() {
		return ctPtj;
	}

	public void setCtPtj(String ctPtj) {
		this.ctPtj = ctPtj;
	}

	public String getCtLoanNo() {
		return ctLoanNo;
	}

	public void setCtLoanNo(String ctLoanNo) {
		this.ctLoanNo = ctLoanNo;
	}

	public String getCtProductService() {
		return ctProductService;
	}

	public void setCtProductService(String ctProductService) {
		this.ctProductService = ctProductService;
	}

	public String getCtGroupOne() {
		return ctGroupOne;
	}

	public void setCtGroupOne(String ctGroupOne) {
		this.ctGroupOne = ctGroupOne;
	}

	public String getCtGroupTwo() {
		return ctGroupTwo;
	}

	public void setCtGroupTwo(String ctGroupTwo) {
		this.ctGroupTwo = ctGroupTwo;
	}

	public String getCmExtention() {
		return cmExtention;
	}

	public void setCmExtention(String cmExtention) {
		this.cmExtention = cmExtention;
	}

	public String getLocalToDateVar() {
		return localToDateVar;
	}

	public void setLocalToDateVar(String localToDateVar) {
		this.localToDateVar = localToDateVar;
	}

	public long getLngUserId() {
		return lngUserId;
	}

	public void setLngUserId(long lngUserId) {
		this.lngUserId = lngUserId;
	}

	public String getCmSupplOrAdmin() {
		return cmSupplOrAdmin;
	}

	public void setCmSupplOrAdmin(String cmSupplOrAdmin) {
		this.cmSupplOrAdmin = cmSupplOrAdmin;
	}

	public String getSmSVOUser() {
		return smSVOUser;
	}

	public void setSmSVOUser(String smSVOUser) {
		this.smSVOUser = smSVOUser;
	}

	public String getSmMonthlyRptByYear() {
		return smMonthlyRptByYear;
	}

	public void setSmMonthlyRptByYear(String smMonthlyRptByYear) {
		this.smMonthlyRptByYear = smMonthlyRptByYear;
	}

	public String getSmCreatedFromYear() {
		return smCreatedFromYear;
	}

	public void setSmCreatedFromYear(String smCreatedFromYear) {
		this.smCreatedFromYear = smCreatedFromYear;
	}

	public String getSmCreatedToYear() {
		return smCreatedToYear;
	}

	public void setSmCreatedToYear(String smCreatedToYear) {
		this.smCreatedToYear = smCreatedToYear;
	}

	public String getScRequestQuoteNo() {
		return scRequestQuoteNo;
	}

	public void setScRequestQuoteNo(String scRequestQuoteNo) {
		this.scRequestQuoteNo = scRequestQuoteNo;
	}

	public String getScPIDNo() {
		return scPIDNo;
	}

	public void setScPIDNo(String scPIDNo) {
		this.scPIDNo = scPIDNo;
	}

	public String getScContractOrPKPType() {
		return scContractOrPKPType;
	}

	public void setScContractOrPKPType(String scContractOrPKPType) {
		this.scContractOrPKPType = scContractOrPKPType;
	}

	public String getScContractOrPKPNo() {
		return scContractOrPKPNo;
	}

	public void setScContractOrPKPNo(String scContractOrPKPNo) {
		this.scContractOrPKPNo = scContractOrPKPNo;
	}

	public String getScContractOrPKPName() {
		return scContractOrPKPName;
	}

	public void setScContractOrPKPName(String scContractOrPKPName) {
		this.scContractOrPKPName = scContractOrPKPName;
	}

	public String getScLAPRNPURCHDEFAULTGroup() {
		return scLAPRNPURCHDEFAULTGroup;
	}

	public void setScLAPRNPURCHDEFAULTGroup(String scLAPRNPURCHDEFAULTGroup) {
		this.scLAPRNPURCHDEFAULTGroup = scLAPRNPURCHDEFAULTGroup;
	}

	public String getScREQUESTNOTESUMMARYGroup() {
		return scREQUESTNOTESUMMARYGroup;
	}

	public void setScREQUESTNOTESUMMARYGroup(String scREQUESTNOTESUMMARYGroup) {
		this.scREQUESTNOTESUMMARYGroup = scREQUESTNOTESUMMARYGroup;
	}

	public String getScCommonStatus() {
		return scCommonStatus;
	}

	public void setScCommonStatus(String scCommonStatus) {
		this.scCommonStatus = scCommonStatus;
	}

	public String getFlGroupOne() {
		return flGroupOne;
	}

	public void setFlGroupOne(String flGroupOne) {
		this.flGroupOne = flGroupOne;
	}

	public String getFlGroupTwo() {
		return flGroupTwo;
	}

	public void setFlGroupTwo(String flGroupTwo) {
		this.flGroupTwo = flGroupTwo;
	}

	public String getFlGroupThree() {
		return flGroupThree;
	}

	public void setFlGroupThree(String flGroupThree) {
		this.flGroupThree = flGroupThree;
	}

	public String getFlGroupFour() {
		return flGroupFour;
	}

	public void setFlGroupFour(String flGroupFour) {
		this.flGroupFour = flGroupFour;
	}

	public String getFlGroupFive() {
		return flGroupFive;
	}

	public void setFlGroupFive(String flGroupFive) {
		this.flGroupFive = flGroupFive;
	}

	public String getFlGroupSix() {
		return flGroupSix;
	}

	public void setFlGroupSix(String flGroupSix) {
		this.flGroupSix = flGroupSix;
	}

	public String getFlGroupSeven() {
		return flGroupSeven;
	}

	public void setFlGroupSeven(String flGroupSeven) {
		this.flGroupSeven = flGroupSeven;
	}

	public String getFlGroupEight() {
		return flGroupEight;
	}

	public void setFlGroupEight(String flGroupEight) {
		this.flGroupEight = flGroupEight;
	}

	public String getFlGroupNine() {
		return flGroupNine;
	}

	public void setFlGroupNine(String flGroupNine) {
		this.flGroupNine = flGroupNine;
	}

	public String getFlGroupTen() {
		return flGroupTen;
	}

	public void setFlGroupTen(String flGroupTen) {
		this.flGroupTen = flGroupTen;
	}

	public String getFlGroupEleven() {
		return flGroupEleven;
	}

	public void setFlGroupEleven(String flGroupEleven) {
		this.flGroupEleven = flGroupEleven;
	}

	public String getFlGroupTwelve() {
		return flGroupTwelve;
	}

	public void setFlGroupTwelve(String flGroupTwelve) {
		this.flGroupTwelve = flGroupTwelve;
	}

	public String getFlGroupThirteen() {
		return flGroupThirteen;
	}

	public void setFlGroupThirteen(String flGroupThirteen) {
		this.flGroupThirteen = flGroupThirteen;
	}

	public String getFlGroupFourteen() {
		return flGroupFourteen;
	}

	public void setFlGroupFourteen(String flGroupFourteen) {
		this.flGroupFourteen = flGroupFourteen;
	}

	public String getFlGroupFifteen() {
		return flGroupFifteen;
	}

	public void setFlGroupFifteen(String flGroupFifteen) {
		this.flGroupFifteen = flGroupFifteen;
	}

	public String getFlGroupSixteen() {
		return flGroupSixteen;
	}

	public void setFlGroupSixteen(String flGroupSixteen) {
		this.flGroupSixteen = flGroupSixteen;
	}

	public String getCtComplete() {
		return ctComplete;
	}

	public void setCtComplete(String ctComplete) {
		this.ctComplete = ctComplete;
	}

	public String getCtContrMontrStatus() {
		return ctContrMontrStatus;
	}

	public void setCtContrMontrStatus(String ctContrMontrStatus) {
		this.ctContrMontrStatus = ctContrMontrStatus;
	}

	private String generatedURL;
	private String reportURL;
	private StreamedContent pdfFile;
	private String constructedParamString;
	private String reportFileNameJSP;

	public String getCtContractNo() {
		return ctContractNo;
	}

	public void setCtContractNo(String ctContractNo) {
		this.ctContractNo = ctContractNo;
	}

	public String getCtAgreement() {
		return ctAgreement;
	}

	public void setCtAgreement(String ctAgreement) {
		this.ctAgreement = ctAgreement;
	}

	public String getCtStatus() {
		return ctStatus;
	}

	public void setCtStatus(String ctStatus) {
		this.ctStatus = ctStatus;
	}

	public String getSubModule() {
		return subModule;
	}

	public void setSubModule(String subModule) {
		this.subModule = subModule;
	}

	public String getScPrcRptGroup() {
		return scPrcRptGroup;
	}

	public void setScPrcRptGroup(String scPrcRptGroup) {
		this.scPrcRptGroup = scPrcRptGroup;
	}

	public String getScPIDtlsPrcRptGroup() {
		return scPIDtlsPrcRptGroup;
	}

	public void setScPIDtlsPrcRptGroup(String scPIDtlsPrcRptGroup) {
		this.scPIDtlsPrcRptGroup = scPIDtlsPrcRptGroup;
	}

	public String getScProcurementType() {
		return scProcurementType;
	}

	public void setScProcurementType(String scProcurementType) {
		this.scProcurementType = scProcurementType;
	}

	public String getScProcessRptQuotationNo() {
		return scProcessRptQuotationNo;
	}

	public void setScProcessRptQuotationNo(String scProcessRptQuotationNo) {
		this.scProcessRptQuotationNo = scProcessRptQuotationNo;
	}

	public String getScSoneStatus() {
		return scSoneStatus;
	}

	public void setScSoneStatus(String scSoneStatus) {
		this.scSoneStatus = scSoneStatus;
	}

	public Date getScSoneCrtdFromDate() {
		return scSoneCrtdFromDate;
	}

	public void setScSoneCrtdFromDate(Date scSoneCrtdFromDate) {
		this.scSoneCrtdFromDate = scSoneCrtdFromDate;
	}

	public Date getScSoneCrtdToDate() {
		return scSoneCrtdToDate;
	}

	public void setScSoneCrtdToDate(Date scSoneCrtdToDate) {
		this.scSoneCrtdToDate = scSoneCrtdToDate;
	}

	public String getScRequisenerId() {
		return scRequisenerId;
	}

	public void setScRequisenerId(String scRequisenerId) {
		this.scRequisenerId = scRequisenerId;
	}

	public String getScSupplier() {
		return scSupplier;
	}

	public void setScSupplier(String scSupplier) {
		this.scSupplier = scSupplier;
	}

	public String getScLogin() {
		return scLogin;
	}

	public void setScLogin(String scLogin) {
		this.scLogin = scLogin;
	}

	public String getScPIno() {
		return scPIno;
	}

	public void setScPIno(String scPIno) {
		this.scPIno = scPIno;
	}

	public Date getScPublishFromDate() {
		return scPublishFromDate;
	}

	public void setScPublishFromDate(Date scPublishFromDate) {
		this.scPublishFromDate = scPublishFromDate;
	}

	public Date getScPublishToDate() {
		return scPublishToDate;
	}

	public void setScPublishToDate(Date scPublishToDate) {
		this.scPublishToDate = scPublishToDate;
	}

	public List<SelectItem> getScProcureAllList() {
		return scProcureAllList;
	}

	public void setScProcureAllList(List<SelectItem> scProcureAllList) {
		this.scProcureAllList = scProcureAllList;
	}

	public List<SelectItem> getScQuotationList() {
		return scQuotationList;
	}

	public void setScQuotationList(List<SelectItem> scQuotationList) {
		this.scQuotationList = scQuotationList;
	}

	public List<SelectItem> getScTenderList() {
		return scTenderList;
	}

	public void setScTenderList(List<SelectItem> scTenderList) {
		this.scTenderList = scTenderList;
	}

	public List<SelectItem> getScRstrctdTndrList() {
		return scRstrctdTndrList;
	}

	public void setScRstrctdTndrList(List<SelectItem> scRstrctdTndrList) {
		this.scRstrctdTndrList = scRstrctdTndrList;
	}

	public String getScCreatedToYear() {
		return scCreatedToYear;
	}

	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}

	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}

	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}

	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}

	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}

	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}

	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}

	public String getScDeskOfficer() {
		return scDeskOfficer;
	}

	public void setScDeskOfficer(String scDeskOfficer) {
		this.scDeskOfficer = scDeskOfficer;
	}

	public String getScPtj() {
		return scPtj;
	}

	public void setScPtj(String scPtj) {
		this.scPtj = scPtj;
	}

	public String getScStatus() {
		return scStatus;
	}

	public void setScStatus(String scStatus) {
		this.scStatus = scStatus;
	}

	public List<SelectItem> getProcurementList() {
		return procurementList;
	}

	public void setProcurementList(List<SelectItem> procurementList) {
		this.procurementList = procurementList;
	}

	public String getFlContractOrder() {
		return flContractOrder;
	}

	public void setFlContractOrder(String flContractOrder) {
		this.flContractOrder = flContractOrder;
	}

	public String getFlContractRequest() {
		return flContractRequest;
	}

	public void setFlContractRequest(String flContractRequest) {
		this.flContractRequest = flContractRequest;
	}

	public String getFlPurchaseOrderNo() {
		return flPurchaseOrderNo;
	}

	public void setFlPurchaseOrderNo(String flPurchaseOrderNo) {
		this.flPurchaseOrderNo = flPurchaseOrderNo;
	}

	public String getReportFileNameJSP() {
		return reportFileNameJSP;
	}

	public void setReportFileNameJSP(String reportFileNameJSP) {
		this.reportFileNameJSP = reportFileNameJSP;
	}

	public StreamedContent getPdfFile() {
		return pdfFile;
	}

	public String getFlItmType() {
		return flItmType;
	}

	public void setFlItmType(String flItmType) {
		this.flItmType = flItmType;
	}

	public String getFlPtj() {
		return flPtj;
	}

	public void setFlPtj(String flPtj) {
		this.flPtj = flPtj;
	}

	public String getFlContractNo() {
		return flContractNo;
	}

	public void setFlContractNo(String flContractNo) {
		this.flContractNo = flContractNo;
	}

	public String getFlSst() {
		return flSst;
	}

	public void setFlSst(String flSst) {
		this.flSst = flSst;
	}

	public String getFlJabatan() {
		return flJabatan;
	}

	public void setFlJabatan(String flJabatan) {
		this.flJabatan = flJabatan;
	}

	public String getFlMinistry() {
		return flMinistry;
	}

	public void setFlMinistry(String flMinistry) {
		this.flMinistry = flMinistry;
	}

	public String getFlSupplier() {
		return flSupplier;
	}

	public void setFlSupplier(String flSupplier) {
		this.flSupplier = flSupplier;
	}

	public String getFlStatus() {
		return flStatus;
	}

	public void setFlStatus(String flStatus) {
		this.flStatus = flStatus;
	}

	public String getFlDuration() {
		return flDuration;
	}

	public void setFlDuration(String flDuration) {
		this.flDuration = flDuration;
	}

	public String getFlRecievingOfficer() {
		return flRecievingOfficer;
	}

	public void setFlRecievingOfficer(String flRecievingOfficer) {
		this.flRecievingOfficer = flRecievingOfficer;
	}

	public String getFlDeliveryAddress() {
		return flDeliveryAddress;
	}

	public void setFlDeliveryAddress(String flDeliveryAddress) {
		this.flDeliveryAddress = flDeliveryAddress;
	}

	public String getFlContractType() {
		return flContractType;
	}

	public void setFlContractType(String flContractType) {
		this.flContractType = flContractType;
	}

	public String getFlRequisitioner() {
		return flRequisitioner;
	}

	public void setFlRequisitioner(String flRequisitioner) {
		this.flRequisitioner = flRequisitioner;
	}

	public String getFlMtoContract() {
		return flMtoContract;
	}

	public void setFlMtoContract(String flMtoContract) {
		this.flMtoContract = flMtoContract;
	}

	public String getFlInvoiceNo() {
		return flInvoiceNo;
	}

	public void setFlInvoiceNo(String flInvoiceNo) {
		this.flInvoiceNo = flInvoiceNo;
	}

	public String getFlDeductionType() {
		return flDeductionType;
	}

	public void setFlDeductionType(String flDeductionType) {
		this.flDeductionType = flDeductionType;
	}

	public String getFlCrNo() {
		return flCrNo;
	}

	public void setFlCrNo(String flCrNo) {
		this.flCrNo = flCrNo;
	}

	public String getFlApproverName() {
		return flApproverName;
	}

	public void setFlApproverName(String flApproverName) {
		this.flApproverName = flApproverName;
	}

	public String getFlDocumentType() {
		return flDocumentType;
	}

	public void setFlDocumentType(String flDocumentType) {
		this.flDocumentType = flDocumentType;
	}

	public String getFlCriteria() {
		return flCriteria;
	}

	public void setFlCriteria(String flCriteria) {
		this.flCriteria = flCriteria;
	}

	public String getFlScoring() {
		return flScoring;
	}

	public void setFlScoring(String flScoring) {
		this.flScoring = flScoring;
	}

	public String getFlPkpNo() {
		return flPkpNo;
	}

	public void setFlPkpNo(String flPkpNo) {
		this.flPkpNo = flPkpNo;
	}

	public String getFlDeliveryOrderNo() {
		return flDeliveryOrderNo;
	}

	public void setFlDeliveryOrderNo(String flDeliveryOrderNo) {
		this.flDeliveryOrderNo = flDeliveryOrderNo;
	}

	public String getFlItemCode() {
		return flItemCode;
	}

	public void setFlItemCode(String flItemCode) {
		this.flItemCode = flItemCode;
	}

	public String getFlPoAmount() {
		return flPoAmount;
	}

	public void setFlPoAmount(String flPoAmount) {
		this.flPoAmount = flPoAmount;
	}

	public String getFlPrNo() {
		return flPrNo;
	}

	public void setFlPrNo(String flPrNo) {
		this.flPrNo = flPrNo;
	}

	public String getFlPmo() {
		return flPmo;
	}

	public void setFlPmo(String flPmo) {
		this.flPmo = flPmo;
	}

	public String getFlItemName() {
		return flItemName;
	}

	public void setFlItemName(String flItemName) {
		this.flItemName = flItemName;
	}

	/*
	 * public String getSmSupportingDocument() { return smSupportingDocument; }
	 * public void setSmSupportingDocument(String smSupportingDocument) {
	 * this.smSupportingDocument = smSupportingDocument; } public String
	 * getSmDocStatus() { return smDocStatus; } public void
	 * setSmDocStatus(String smDocStatus) { this.smDocStatus = smDocStatus; }
	 * public String getSmSuppDocStatus() { return smSuppDocStatus; } public
	 * void setSmSuppDocStatus(String smSuppDocStatus) { this.smSuppDocStatus =
	 * smSuppDocStatus; } public String getSmRegistrationNo() { return
	 * smRegistrationNo; } public void setSmRegistrationNo(String
	 * smRegistrationNo) { this.smRegistrationNo = smRegistrationNo; } public
	 * String getSmEpNo() { return smEpNo; } public void setSmEpNo(String
	 * smEpNo) { this.smEpNo = smEpNo; }
	 */
	public String getSmApplicationNo() {
		return smApplicationNo;
	}

	public void setSmApplicationNo(String smApplicationNo) {
		this.smApplicationNo = smApplicationNo;
	}

	public String getSmZone() {
		return smZone;
	}

	public void setSmZone(String smZone) {
		this.smZone = smZone;
	}

	public String getSmSupplierType() {
		return smSupplierType;
	}

	public void setSmSupplierType(String smSupplierType) {
		this.smSupplierType = smSupplierType;
	}

	public String getSmMofRegNo() {
		return smMofRegNo;
	}

	public void setSmMofRegNo(String smMofRegNo) {
		this.smMofRegNo = smMofRegNo;
	}

	/*
	 * public String getSmMofRegNoFrom() { return smMofRegNoFrom; } public void
	 * setSmMofRegNoFrom(String smMofRegNoFrom) { this.smMofRegNoFrom =
	 * smMofRegNoFrom; } public String getSmMofRegNoTo() { return smMofRegNoTo;
	 * } public void setSmMofRegNoTo(String smMofRegNoTo) { this.smMofRegNoTo =
	 * smMofRegNoTo; }
	 */
	public String getSmRegistrationStatus() {
		return smRegistrationStatus;
	}

	public void setSmRegistrationStatus(String smRegistrationStatus) {
		this.smRegistrationStatus = smRegistrationStatus;
	}

	/*
	 * public String getSmCapitalNaccumFrm() { return smCapitalNaccumFrm; }
	 * public void setSmCapitalNaccumFrm(String smCapitalNaccumFrm) {
	 * this.smCapitalNaccumFrm = smCapitalNaccumFrm; } public String
	 * getSmCapitalNaccumTo() { return smCapitalNaccumTo; } public void
	 * setSmCapitalNaccumTo(String smCapitalNaccumTo) { this.smCapitalNaccumTo =
	 * smCapitalNaccumTo; } public String getSmPaidUpCapitalNaccumShare() {
	 * return smPaidUpCapitalNaccumShare; } public void
	 * setSmPaidUpCapitalNaccumShare(String smPaidUpCapitalNaccumShare) {
	 * this.smPaidUpCapitalNaccumShare = smPaidUpCapitalNaccumShare; }
	 */
	public String getSmcategory() {
		return smcategory;
	}

	public void setSmcategory(String smcategory) {
		this.smcategory = smcategory;
	}

	/*
	 * public String getSmNoOfCategory() { return smNoOfCategory; } public void
	 * setSmNoOfCategory(String smNoOfCategory) { this.smNoOfCategory =
	 * smNoOfCategory; }
	 */
	public String getSmMainCategory() {
		return smMainCategory;
	}

	public void setSmMainCategory(String smMainCategory) {
		this.smMainCategory = smMainCategory;
	}

	public String getSmSubCategory() {
		return smSubCategory;
	}

	public void setSmSubCategory(String smSubCategory) {
		this.smSubCategory = smSubCategory;
	}

	public String getSmSubCategoryDivision() {
		return smSubCategoryDivision;
	}

	public void setSmSubCategoryDivision(String smSubCategoryDivision) {
		this.smSubCategoryDivision = smSubCategoryDivision;
	}

	/*
	 * public String getSmRole() { return smRole; } public void setSmRole(String
	 * smRole) { this.smRole = smRole; }
	 */
	public String getSmApplicationStatus() {
		return smApplicationStatus;
	}

	public void setSmApplicationStatus(String smApplicationStatus) {
		this.smApplicationStatus = smApplicationStatus;
	}

	/*
	 * public String getSmCancelledBy() { return smCancelledBy; } public void
	 * setSmCancelledBy(String smCancelledBy) { this.smCancelledBy =
	 * smCancelledBy; }
	 */
	public String getSmPaymentType() {
		return smPaymentType;
	}

	public void setSmPaymentType(String smPaymentType) {
		this.smPaymentType = smPaymentType;
	}

	public String getSmApplicationType() {
		return smApplicationType;
	}

	public void setSmApplicationType(String smApplicationType) {
		this.smApplicationType = smApplicationType;
	}

	public String getSmState() {
		return smState;
	}

	public void setSmState(String smState) {
		this.smState = smState;
	}

	public String getSmDistrict() {
		return smDistrict;
	}

	public void setSmDistrict(String smDistrict) {
		this.smDistrict = smDistrict;
	}

	/*
	 * public String getSmSupplierName() { return smSupplierName; } public void
	 * setSmSupplierName(String smSupplierName) { this.smSupplierName =
	 * smSupplierName; }
	 */
	public String getSmReportType() {
		return smReportType;
	}

	public void setSmReportType(String smReportType) {
		this.smReportType = smReportType;
	}

	/*
	 * public String getSmOrganizationType() { return smOrganizationType; }
	 * public void setSmOrganizationType(String smOrganizationType) {
	 * this.smOrganizationType = smOrganizationType; } public String
	 * getSmKementerian() { return smKementerian; } public void
	 * setSmKementerian(String smKementerian) { this.smKementerian =
	 * smKementerian; } public String getSmNamaAgensi() { return smNamaAgensi; }
	 * public void setSmNamaAgensi(String smNamaAgensi) { this.smNamaAgensi =
	 * smNamaAgensi; } public String getSmComplaintNo() { return smComplaintNo;
	 * } public void setSmComplaintNo(String smComplaintNo) { this.smComplaintNo
	 * = smComplaintNo; } public String getSmProofIdentity() { return
	 * smProofIdentity; } public void setSmProofIdentity(String smProofIdentity)
	 * { this.smProofIdentity = smProofIdentity; } public String
	 * getSmCompanyStatus() { return smCompanyStatus; } public void
	 * setSmCompanyStatus(String smCompanyStatus) { this.smCompanyStatus =
	 * smCompanyStatus; } public String getSmCaseNo() { return smCaseNo; }
	 * public void setSmCaseNo(String smCaseNo) { this.smCaseNo = smCaseNo; }
	 * public String getSmCompApplStatus() { return smCompApplStatus; } public
	 * void setSmCompApplStatus(String smCompApplStatus) { this.smCompApplStatus
	 * = smCompApplStatus; } public String getSmCategoryCode() { return
	 * smCategoryCode; } public void setSmCategoryCode(String smCategoryCode) {
	 * this.smCategoryCode = smCategoryCode; } public String
	 * getSmSiteVisitOfficer() { return smSiteVisitOfficer; } public void
	 * setSmSiteVisitOfficer(String smSiteVisitOfficer) {
	 * this.smSiteVisitOfficer = smSiteVisitOfficer; } public Date
	 * getSmReleaseFromDate() { return smReleaseFromDate; } public void
	 * setSmReleaseFromDate(Date smReleaseFromDate) { this.smReleaseFromDate =
	 * smReleaseFromDate; } public Date getSmReleaseToDate() { return
	 * smReleaseToDate; } public void setSmReleaseToDate(Date smReleaseToDate) {
	 * this.smReleaseToDate = smReleaseToDate; } public Date
	 * getSmApplicationFromDate() { return smApplicationFromDate; } public void
	 * setSmApplicationFromDate(Date smApplicationFromDate) {
	 * this.smApplicationFromDate = smApplicationFromDate; } public Date
	 * getSmApplicationToDate() { return smApplicationToDate; } public void
	 * setSmApplicationToDate(Date smApplicationToDate) {
	 * this.smApplicationToDate = smApplicationToDate; } public Date
	 * getSmReceiveFromDate() { return smReceiveFromDate; } public void
	 * setSmReceiveFromDate(Date smReceiveFromDate) { this.smReceiveFromDate =
	 * smReceiveFromDate; } public Date getSmReceiveToDate() { return
	 * smReceiveToDate; } public void setSmReceiveToDate(Date smReceiveToDate) {
	 * this.smReceiveToDate = smReceiveToDate; } public Date
	 * getSmExpiryFromDate() { return smExpiryFromDate; } public void
	 * setSmExpiryFromDate(Date smExpiryFromDate) { this.smExpiryFromDate =
	 * smExpiryFromDate; } public Date getSmExpiryToDate() { return
	 * smExpiryToDate; } public void setSmExpiryToDate(Date smExpiryToDate) {
	 * this.smExpiryToDate = smExpiryToDate; } public Date
	 * getSmPrintedFromDate() { return smPrintedFromDate; } public void
	 * setSmPrintedFromDate(Date smPrintedFromDate) { this.smPrintedFromDate =
	 * smPrintedFromDate; } public Date getSmPrintedToDate() { return
	 * smPrintedToDate; } public void setSmPrintedToDate(Date smPrintedToDate) {
	 * this.smPrintedToDate = smPrintedToDate; }
	 */
	public String getSmCompanyName() {
		return smCompanyName;
	}

	public void setSmCompanyName(String smCompanyName) {
		this.smCompanyName = smCompanyName;
	}
	
	public String getSmRegistrationType() {
		return smRegistrationType;
	}

	public void setSmRegistrationType(String smRegistrationType) {
		this.smRegistrationType = smRegistrationType;
	}
	public String getCmItemType() {
		return cmItemType;
	}

	public void setCmItemType(String cmItemType) {
		this.cmItemType = cmItemType;
	}

	public String getCmItemStatus() {
		return cmItemStatus;
	}

	public void setCmItemStatus(String cmItemStatus) {
		this.cmItemStatus = cmItemStatus;
	}

	public String getCmUnspscCode() {
		return cmUnspscCode;
	}

	public void setCmUnspscCode(String cmUnspscCode) {
		this.cmUnspscCode = cmUnspscCode;
	}

	public String getCmUnspscTitle() {
		return cmUnspscTitle;
	}

	public void setCmUnspscTitle(String cmUnspscTitle) {
		this.cmUnspscTitle = cmUnspscTitle;
	}

	public String getCmCompanyName() {
		return cmCompanyName;
	}

	public void setCmCompanyName(String cmCompanyName) {
		this.cmCompanyName = cmCompanyName;
	}

	public String getCmRegistrationType() {
		return cmRegistrationType;
	}

	public void setCmRegistrationType(String cmRegistrationType) {
		this.cmRegistrationType = cmRegistrationType;
	}

	public String getCmItemCategory() {
		return cmItemCategory;
	}

	public void setCmItemCategory(String cmItemCategory) {
		this.cmItemCategory = cmItemCategory;
	}

	public String getCmItemName() {
		return cmItemName;
	}

	public void setCmItemName(String cmItemName) {
		this.cmItemName = cmItemName;
	}

	public String getCmEpNumber() {
		return cmEpNumber;
	}

	public void setCmEpNumber(String cmEpNumber) {
		this.cmEpNumber = cmEpNumber;
	}

	public String getCmBusinessRegNo() {
		return cmBusinessRegNo;
	}

	public void setCmBusinessRegNo(String cmBusinessRegNo) {
		this.cmBusinessRegNo = cmBusinessRegNo;
	}

	public String getCmIcNumber() {
		return cmIcNumber;
	}

	public void setCmIcNumber(String cmIcNumber) {
		this.cmIcNumber = cmIcNumber;
	}

	public String getCmMofNumber() {
		return cmMofNumber;
	}

	public void setCmMofNumber(String cmMofNumber) {
		this.cmMofNumber = cmMofNumber;
	}

	public String getCmExtentionCode() {
		return cmExtentionCode;
	}

	public void setCmExtentionCode(String cmExtentionCode) {
		this.cmExtentionCode = cmExtentionCode;
	}

	public String getCmExtentionType() {
		return cmExtentionType;
	}

	public void setCmExtentionType(String cmExtentionType) {
		this.cmExtentionType = cmExtentionType;
	}

	public String getCmExtentionBrand() {
		return cmExtentionBrand;
	}

	public void setCmExtentionBrand(String cmExtentionBrand) {
		this.cmExtentionBrand = cmExtentionBrand;
	}

	public String getCmExtentionMeasurment() {
		return cmExtentionMeasurment;
	}

	public void setCmExtentionMeasurment(String cmExtentionMeasurment) {
		this.cmExtentionMeasurment = cmExtentionMeasurment;
	}

	public String getCmExtentionColour() {
		return cmExtentionColour;
	}

	public void setCmExtentionColour(String cmExtentionColour) {
		this.cmExtentionColour = cmExtentionColour;
	}

	public String getCmProofIdentity() {
		return cmProofIdentity;
	}

	public void setCmProofIdentity(String cmProofIdentity) {
		this.cmProofIdentity = cmProofIdentity;
	}

	public String getScQuotationId() {
		return scQuotationId;
	}

	public void setScQuotationId(String scQuotationId) {
		this.scQuotationId = scQuotationId;
	}

	public String getScQuotationNo() {
		return scQuotationNo;
	}

	public void setScQuotationNo(String scQuotationNo) {
		this.scQuotationNo = scQuotationNo;
	}

	public String getScPaymentId() {
		return scPaymentId;
	}

	public void setScPaymentId(String scPaymentId) {
		this.scPaymentId = scPaymentId;
	}

	public void setPdfFile(StreamedContent pdfFile) {
		this.pdfFile = pdfFile;
	}

	public String getConstructedParamString() {
		return constructedParamString;
	}

	public void setConstructedParamString(String constructedParamString) {
		this.constructedParamString = constructedParamString;
	}

	public List<SelectItem> getLevOneCatLst() {
		return levOneCatLst;
	}

	public void setLevOneCatLst(List<SelectItem> levOneCatLst) {
		this.levOneCatLst = levOneCatLst;
	}

	public List<SelectItem> getLevTwoCatLst() {
		return levTwoCatLst;
	}

	public void setLevTwoCatLst(List<SelectItem> levTwoCatLst) {
		this.levTwoCatLst = levTwoCatLst;
	}

	public List<SelectItem> getLevThreeCatLst() {
		return levThreeCatLst;
	}

	public void setLevThreeCatLst(List<SelectItem> levThreeCatLst) {
		this.levThreeCatLst = levThreeCatLst;
	}

	public List<SelectItem> getStateLst() {
		return stateLst;
	}

	public void setStateLst(List<SelectItem> stateLst) {
		this.stateLst = stateLst;
	}

	public List<SelectItem> getDistrictLst() {
		return districtLst;
	}

	public void setDistrictLst(List<SelectItem> districtLst) {
		this.districtLst = districtLst;
	}

	public String getGeneratedURL() {
		return generatedURL;
	}

	public void setGeneratedURL(String generatedURL) {
		this.generatedURL = generatedURL;
	}

	public String getReportURL() {
		return reportURL;
	}

	public void setReportURL(String reportURL) {
		this.reportURL = reportURL;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getFlRecNoteNo() {
		return flRecNoteNo;
	}

	public void setFlRecNoteNo(String flRecNoteNo) {
		this.flRecNoteNo = flRecNoteNo;
	}

	public List<String> getAuthoriztionList() {
		return authoriztionList;
	}

	public void setAuthoriztionList(List<String> authoriztionList) {
		this.authoriztionList = authoriztionList;
	}

	public ScQt getScqt() {
		return scqt;
	}

	public void setScqt(ScQt scqt) {
		this.scqt = scqt;
	}

	public static Logger getLog() {
		return log;
	}

	public List<SelectItem> getRegTypeList() {
		return regTypeList;
	}

	public void setRegTypeList(List<SelectItem> regTypeList) {
		this.regTypeList = regTypeList;
	}

	public List<SelectItem> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<SelectItem> moduleList) {
		this.moduleList = moduleList;
	}

	public ReportsGenerateBean getRptGenBean() {
		return rptGenBean;
	}

	public void setRptGenBean(ReportsGenerateBean rptGenBean) {
		this.rptGenBean = rptGenBean;
	}

	public List<SelectItem> getReportList() {
		return reportList;
	}

	public void setReportList(List<SelectItem> reportList) {
		this.reportList = reportList;
	}

	public void assignNewCompanyExperience(List<SmSupplierCategory> catList) {
		log.info("***** Assign New Company Exp ***********");
		if (catList == null) {
			catList = new ArrayList<SmSupplierCategory>();
		}

		SmSupplierCategory cat = new SmSupplierCategory();
		// cat.setPmCategoryL1(catOneList);
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		catList.add(cat);
		this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
		this.setSelectedCatList(catList);
		resetChildCatList();
		assignValReqdToTrue();
	}

	private void assignValReqdToTrue() {
		this.setShowParentMsg(false);
		this.setValueReqd(true);
	}

	private void resetChildCatList() {
		this.setSupplierChildCatList(new ArrayList<PmCategoryL2>());
		this.setSupplierSubChildCatList(new ArrayList<PmCategoryL3>());
	}

	public static List<PmCategoryL1> retrieveParentCategoryListForExperiencePage() {
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();		
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = reqSO.retrieveCategoryL1List();
		if (catCodeResDTO != null) {
			return catCodeResDTO.getCategoryL1List();
		} else {
			return null;
		}
	}

	public void selectAll() {

		if (this.isSelectedAll()) {
			this.setSelected(true);
			this.getSelectedCatList();
		} else {
			this.setSelected(false);
			this.setSelectedCatList(null);
		}
	}

	public void selected(int index) {

		List<SmSupplierCategory> selectedCatsList = this.getSelectedCatList();

		if (this.isSelected()) {
			if (selectedCatsList == null) {
				selectedCatsList = new ArrayList<SmSupplierCategory>();
			}
			selectedCatsList.add(this.getSelectedCatList().get(index));

		} else {

			if (selectedCatsList.contains(this.getSelectedCatList().get(index))) {
				selectedCatsList.remove(this.getSelectedCatList().get(index));
			}
		}
		this.setSelectedCatList(selectedCatsList);
	}

	public void selectAllCompanyExperinceCategories() {

		if (this.isSelectedAll()) {
			this.setSelected(true);
			this.setSelectedCatList(this.getSelectedCatList());
		} else {
			this.setSelected(false);
			this.setSelectedCatList(null);
		}
	}

	public void selectedCompanyExperinceCategory(int index) {
		List<SmSupplierCategory> selectedCatsList = this.getSelectedCatList();
		if (this.isSelected()) {
			if (selectedCatsList == null) {
				selectedCatsList = new ArrayList<SmSupplierCategory>();
			}
			selectedCatsList.add(this.getSelectedCatList().get(index));

		} else {
			if (selectedCatsList.contains(this.getSelectedCatList().get(index))) {
				selectedCatsList.remove(this.getSelectedCatList().get(index));
			}
		}
		this.setSelectedCatList(selectedCatsList);
	}

	public void populateExperienceChildCategoryByParent(long parentId,
			PmCategoryL2 catL2, PmCategoryL3 catL3) {
		try {
			List<PmCategoryL2> categoryL2s = retrieveExperienceChildCategoryList(parentId);
			catL2.setPmCategoryL2s(categoryL2s);
			catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<PmCategoryL2> retrieveExperienceChildCategoryList(
			long categoryL1Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);
		return resDTO.getCategoryL2List();
	}

	public void populateExperienceSubChildCategoryByChild(long childId,
			PmCategoryL3 catL3) {
		try {
			List<PmCategoryL3> categoryL3s = retrieveExperienceSubChildCategoryList(childId);
			catL3.setPmCategoryL3s(categoryL3s);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<PmCategoryL3> retrieveExperienceSubChildCategoryList(
			long categoryL2Id) {
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject("pm", ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL2Id(categoryL2Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL3ListByL2Id(reqDTO);
		return resDTO.getCategoryL3List();
	}

	public void checkDuplicateCategoryForProject(SmSupplierCategory newCat) {
		for (SmSupplierCategory cat : this.getSelectedCatList()) {
			if ((cat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (newCat.getPmCategoryL1().getCategoryL1Id() != null)
					&& (cat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (newCat.getPmCategoryL2().getCategoryL2Id() != null)
					&& (cat.getPmCategoryL3().getCategoryL3Id() != null)
					&& (newCat.getPmCategoryL3().getCategoryL3Id() != null)) {
				if (newCat.equals(cat)) {
					log.info(" *** Same Obj ****"
							+ cat.getPmCategoryL1().getCategoryL1Code()
							+ " -- --"
							+ newCat.getPmCategoryL1().getCategoryL1Code());

				} else if (cat.getPmCategoryL1().getCategoryL1Id()
						.equals(newCat.getPmCategoryL1().getCategoryL1Id())
						&& cat.getPmCategoryL2()
								.getCategoryL2Id()
								.equals(newCat.getPmCategoryL2()
										.getCategoryL2Id())
						&& cat.getPmCategoryL3()
								.getCategoryL3Id()
								.equals(newCat.getPmCategoryL3()
										.getCategoryL3Id())) {
					newCat.getPmCategoryL3().setCategoryL3Id(null);
					throw new EpException(MessageConst.ERROR_722533);
				}
			}
		}

	}

	public void deleteSelectedCodes() {
		System.out.println("*** Selected value **** "
				+ this.getSelectCatDeletionList().size());
		for (SmSupplierCategory cat : this.getSelectCatDeletionList()) {
			this.getSelectedCatList().remove(cat);

		}
	}

	public void assignCategoryForCompExperience() {
		log.info("*****  ***********");
		SmSupplierCategory cat = new SmSupplierCategory();		
		if ((this.getSelectedCatList().size()>9)) {
			throw new EpException(ReportConst.ERR_999000_MORTHAN_TEN);
		}
		
		
		cat.setPmCategoryL1(new PmCategoryL1());
		PmCategoryL2 catL2 = new PmCategoryL2();
		catL2.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		cat.setPmCategoryL2(catL2);
		PmCategoryL3 catL3 = new PmCategoryL3();
		catL3.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		cat.setPmCategoryL3(catL3);
		// cat.setSmCompanyExperience(exp);
		this.setSupplierParentCatList(retrieveParentCategoryListForExperiencePage());
		resetChildCatList();
		assignValReqdToTrue();
		
		this.getSelectedCatList().add(cat);
	}

	/*
	 * Retrieve module list
	 */
	public List<SelectItem> retriveModuleList() {

		moduleList = new ArrayList<SelectItem>();
		SelectItem selectItem = new SelectItem();
		selectItem.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_SC_TTL));
		selectItem.setValue(ReportConst.SC_MOD_VAL);
		moduleList.add(selectItem);
		SelectItem selectItem5 = new SelectItem();
		selectItem5.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_DP_TTL));
		selectItem5.setValue(ReportConst.DP_MOD_VAL);
		moduleList.add(selectItem5);
		SelectItem selectItem1 = new SelectItem();
		selectItem1.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_CM_TTL));
		selectItem1.setValue(ReportConst.CM_MOD_VAL);
		moduleList.add(selectItem1);
		SelectItem selectItem2 = new SelectItem();
		selectItem2.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_FL_TTL));
		selectItem2.setValue(ReportConst.FL_MOD_VAL);
		moduleList.add(selectItem2);
		SelectItem selectItem3 = new SelectItem();
		selectItem3.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_SM_TTL));
		selectItem3.setValue(ReportConst.SM_MOD_VAL);
		moduleList.add(selectItem3);
		SelectItem selectItem4 = new SelectItem();
		selectItem4.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_CT_TTL));
		selectItem4.setValue(ReportConst.CT_MOD_VAL);
		moduleList.add(selectItem4);
		
		return moduleList;
	}

	public void preRenderView() {
		rptGenBean = new ReportsGenerateBean();
		getManagedBean().setRptGenBean(rptGenBean);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setReportValue(null);
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, ReportConst.REPORT_PAGE);
	}

	/*
	 * Retrieve report list by module
	 */
	public void retriveReportListBymoduleName() {
		reportList = new ArrayList<SelectItem>();
		rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
		/*
		 * getManagedBean().setReportName(null);
		 * getManagedBean().setReportValue(null);
		 */
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setRptGenBean(null);
		retriveUserrloeByUserId(this.getModuleValue());
		if (this.getModuleValue() != null) {
			retriveReportListByModule(this.getModuleValue());
		}
		getManagedBean().setReportList(reportList);
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		
	}
	public void madatoryIndicator() {
		if(null!=this.getReportValue()){
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(true);
		}else{
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(false);
		}
	}
	public List<SelectItem> retriveReportListByModule(String module) {
		reportList = new ArrayList<SelectItem>();

		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		PMReportReqDTO reqDTO = new PMReportReqDTO();
		reqDTO.setModuleCode(module);
		PMReportResDTO rptResDTO = reportSO.retriveReportByModule(reqDTO);
		List<PMReport> rptLst = rptResDTO.getPmReportList();
		if (null != rptLst) {

			for (PMReport pmReport : rptLst) {
				SelectItem selectItem = new SelectItem();
				String label = ReportConst.EMPTY_VAL;
				if ((null != pmReport.getSubModule())
						&& (ReportConst.EMPTY_VAL != pmReport.getSubModule())) {
					label = pmReport.getReportTitle() + ReportConst.RPT_EIFON
							+ pmReport.getSubModule();
				} else {
					label = pmReport.getReportTitle();
				}
				if (!(pmReport.getReportId()
						.equals(ReportConst.SC_S1_RCNT_PRICE_RPT_VAL))
						&& !(pmReport.getReportId()
								.equals(ReportConst.SC_ITEM_PRICING_SUM_RPT_VAL))
						&& !(pmReport.getReportId()
								.equals(ReportConst.SC_S1_REQUEST_NOTE_FINAL_RPT_VAL))) {
					selectItem.setValue(pmReport.getReportId());
					selectItem.setLabel(label);
					reportList.add(selectItem);
				}

			}
			// retrieveCMUnspc();
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.REPORTS_NOT_AVAILABLE_BY_MODULE));
		}
		return reportList;
	}

	/*
	 * Retrieve Role code by User id
	 */
	public List<String> retriveUserrloeByUserId(String moduleName) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		retrieveMinistryByOrgtype();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			userId = new FacesScopes().getUserSessionBean().getUserId()
					.toString();
			lngUserId = new FacesScopes().getUserSessionBean().getUserId();
			reqDTO.setUserId(new FacesScopes().getUserSessionBean().getUserId());
			reqDTO.setLanguageCode(ReportConst.ENG_LANG);
			UserResDTO userResDTO = userSO
					.retrieveUserRoleDescListByUserId(reqDTO);
			List<PmRoleDesc> roleDesc = userResDTO.getRoleDescList();
			if (null != roleDesc) {
				for (PmRoleDesc pmRoleDesc : roleDesc) {
					pmRoleDesc.getPmRole().getRoleCode();
					if (!authoriztionList.contains(pmRoleDesc.getPmRole()
							.getRoleCode())) {
						authoriztionList.add(pmRoleDesc.getPmRole()
								.getRoleCode());
					}
				}
			}
			userResDTO = userSO.retrieveUserOrgListByUserId(reqDTO);
			if (userResDTO.getUserOrgList() != null
					&& userResDTO.getUserOrgList().size() > 0) {
				List<PmUserOrg> userOrg = userResDTO.getUserOrgList();
				for (PmUserOrg pmuserOrg : userOrg) {
					if (pmuserOrg.getPmOrgProfile().getOrgProfileId() != null) {
						if (!orgProfileList.contains((pmuserOrg
								.getPmOrgProfile().getOrgProfileId())
								.toString())) {
							orgProfileList.add((pmuserOrg.getPmOrgProfile()
									.getOrgProfileId()).toString());
							SelectItem selectItem = new SelectItem();
							selectItem.setLabel(pmuserOrg.getPmOrgProfile()
									.getOrgName());
							selectItem.setValue((pmuserOrg.getPmOrgProfile()
									.getOrgProfileId()).toString());
							orgProfileIdNameList.add(selectItem);
						}
					}
				}
			}
			String roleCode = ReportConst.EMPTY_VAL;
			if (authoriztionList.contains(ReportConst.APPROVER_NAIK_TARAF)) {
				roleCode = ReportConst.APPROVER_NAIK_TARAF;
			} else if (authoriztionList.contains(ReportConst.RN_APPROVER)) {
				roleCode = ReportConst.RN_APPROVER;
			}
			if (roleCode != null && roleCode != ReportConst.EMPTY_VAL) {
				for (int i = 0; i < orgProfileList.size(); i++) {
					reqDTO.setUserId(new FacesScopes().getUserSessionBean()
							.getUserId());
					// reqDTO.setRoleCode(RoleCode.CT_APPROVER.value());
					reqDTO.setRoleCode(roleCode);
					reqDTO.setOrgProfileId(Long.parseLong(orgProfileList.get(i)));
					userResDTO = userSO.retrieveUserListByRole(reqDTO);
					List<PmUser> list = userResDTO.getUserList();
					for (PmUser user : list) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(user.getUserName());
						selectItem.setValue(user.getUserId());
						userListbyprofileRole.add(selectItem);
					}
				}
			}
		}

		return authoriztionList;
	}

	public void retrieveMinistryByOrgtype() {
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			userId = new FacesScopes().getUserSessionBean().getUserId()
					.toString();
			lngUserId = new FacesScopes().getUserSessionBean().getUserId();
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			// if (null != this.getCtMinistry()) {
			reqDTO.setUserId(lngUserId);
			reqDTO.setOrgType(OrganizationType.MINISTRY.value());
			// reqDTO.setParentOrgProfileId(Long.parseLong(this.getCtMinistry()));
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> list = resDTO.getOrgProfileList();
			for (PmOrgProfile user : list) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(user.getOrgName());
				selectItem.setValue(user.getOrgProfileId());
				ministryList.add(selectItem);
			}
			// }
		}
	}

	public void retrievePTJByOrgProfileId() {
		// OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
		IOrgProfileSO orgProfileSO = null;
		try {

			if (null != this.getFlMinistry() || null != this.getCtMinistry()) {
				Long ministryId = null;
				if (null != this.getFlMinistry()) {
					ministryId = Long.parseLong(this.getFlMinistry());
				} else if (null != this.getCtMinistry()) {
					ministryId = Long.parseLong(this.getFlMinistry());
				}
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(OrganizationType.DIVISION.value());
				reqDTO.setParentOrgProfileId(ministryId);
				orgProfileSO = BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO
						.retrieveOrgProfileListByOrgType(reqDTO);
				List<PmOrgProfile> list = resDTO.getOrgProfileList();
				for (PmOrgProfile user : list) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(user.getOrgName());
					selectItem.setValue(user.getOrgProfileId());
					ptjList.add(selectItem);
				}
				// this.setPtjList(resDTO.getOrgProfileList());
			}

		} finally {
			orgProfileSO = null;
		}
	}

	public void retrieveCMUnspc() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> cmunspcLst = reportSO.retrieveCMUnspc();
		for (Object[] object : cmunspcLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(object[0].toString());
			selectItem.setValue(object[1]);
			unspcList.add(selectItem);
		}
	}

	public void enableExtension() {
		this.setCmExtentionCode(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		rptGenBean.setCmExtentionCodeView(false);
		rptGenBean.setCmExtentionBrandView(false);
		rptGenBean.setCmExtentionTypeView(false);
		rptGenBean.setCmExtentionMeasurmentView(false);
		rptGenBean.setCmExtentionColourView(false);
		if (null != this.getCmUnspscCode()
				&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
			if ((this.getCmUnspscCode().length() == 8)) {
				unspid = retrieveCMUnspcIdbyCode(this.getCmUnspscCode());
				if (null != unspid) {
					rptGenBean.setCmExtentionCodeView(true);
				} else {
					this.setCmUnspscCode(null);
					this.setCmExtentionCode(null);
					rptGenBean.setCmExtentionCodeView(false);
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.PLZ_PROV_VALID_UNSPCCODE));
				}
			} else {
				this.setCmUnspscCode(null);
				this.setCmExtentionCode(null);
				rptGenBean.setCmExtentionCodeView(false);
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.PLZ_PROV_VALID_UNSPCCODE));
			}
		} else {
			this.setCmExtentionCode(null);
			rptGenBean.setCmExtentionCodeView(false);
		}
	}
	public void enableExtensionTypes() {
		// this.setUnspid((long) 12392);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
		getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
		getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
		getManagedBean().getRptGenBean().setCmExtentionColourView(false);
		
		if (null != this.getCmUnspscCode()
				&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
			if ((this.getCmUnspscCode().length() == 8)) {
				
				
				unspid = retrieveCMUnspcIdbyCode(this.getCmUnspscCode());
				if (null != unspid) {
					//rptGenBean.setCmExtentionCodeView(true);
					retrieveBrand();
					retrieveExtType();
					retrieveMeasurement();
					retrieveColor();
					
					getManagedBean().getRptGenBean().setCmExtentionBrandView(true);		
					getManagedBean().getRptGenBean().setCmExtentionTypeView(true);		
					getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(true);	
					getManagedBean().getRptGenBean().setCmExtentionColourView(true);
				} else {
					this.setCmUnspscCode(null);
					//this.setCmExtentionCode(null);
					//rptGenBean.setCmExtentionCodeView(false);
					this.setCmUnspscCode(null);
					this.setCmExtentionBrand(null);
					this.setCmExtentionType(null);
					this.setCmExtentionMeasurment(null);
					this.setCmExtentionColour(null);
					brandList = new ArrayList<SelectItem>();
					extTypeList = new ArrayList<SelectItem>();
					measurmentList = new ArrayList<SelectItem>();
					colorList = new ArrayList<SelectItem>();
					getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
					getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
					getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
					getManagedBean().getRptGenBean().setCmExtentionColourView(false);
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.PLZ_PROV_VALID_UNSPCCODE));
				}
			} else {
				//this.setCmExtentionCode(null);
				this.setCmUnspscCode(null);
				this.setCmExtentionBrand(null);
				this.setCmExtentionType(null);
				this.setCmExtentionMeasurment(null);
				this.setCmExtentionColour(null);
				brandList = new ArrayList<SelectItem>();
				extTypeList = new ArrayList<SelectItem>();
				measurmentList = new ArrayList<SelectItem>();
				colorList = new ArrayList<SelectItem>();
				getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
				getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
				getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
				getManagedBean().getRptGenBean().setCmExtentionColourView(false);
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.PLZ_PROV_VALID_UNSPCCODE));
			}
		} else {
			//this.setCmExtentionCode(null);
			this.setCmUnspscCode(null);
			this.setCmExtentionBrand(null);
			this.setCmExtentionType(null);
			this.setCmExtentionMeasurment(null);
			this.setCmExtentionColour(null);
			brandList = new ArrayList<SelectItem>();
			extTypeList = new ArrayList<SelectItem>();
			measurmentList = new ArrayList<SelectItem>();
			colorList = new ArrayList<SelectItem>();
			getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
			getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
			getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
			getManagedBean().getRptGenBean().setCmExtentionColourView(false);
		}		
	}

	public Long retrieveCMUnspcIdbyCode(String unspcCode) {
		Long unspcid = null;
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<BigDecimal> cmunspcLst = reportSO
				.retrieveCMUnspcIdbyCode(unspcCode);
		if ((null != cmunspcLst) && (cmunspcLst.size() != 0)) {
			unspcid = ((BigDecimal) cmunspcLst.get(0)).longValue();
		}
		return unspcid;
	}

	public void retrieveExtention() {
		// this.setUnspid((long) 12392);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		rptGenBean.setCmExtentionBrandView(false);
		rptGenBean.setCmExtentionTypeView(false);
		rptGenBean.setCmExtentionMeasurmentView(false);
		rptGenBean.setCmExtentionColourView(false);
		if (null != this.getCmExtentionCode()) {
			if (this.getCmExtentionCode().equals(ReportConst.EXT_BRAND)) {
				rptGenBean.setCmExtentionBrandView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveBrand();
			} else if (this.getCmExtentionCode().equals(ReportConst.EXT_TYPE)) {
				rptGenBean.setCmExtentionTypeView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveExtType();
			} else if (this.getCmExtentionCode().equals(
					ReportConst.EXT_MEASUREMENT)) {
				rptGenBean.setCmExtentionMeasurmentView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveMeasurement();
			} else if (this.getCmExtentionCode().equals(ReportConst.EXT_COLOUR)) {
				rptGenBean.setCmExtentionColourView(true);
				rptGenBean.setExtraTwoColumnView(true);
				retrieveColor();
			}
		}
	}

	public void retrieveBrand() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> brandLst = reportSO.retrieveBrandByUnspsc(unspid);
		for (Object[] object : brandLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			brandList.add(selectItem);
		}
	}

	public void retrieveExtType() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> typeLst = reportSO.retrieveTypeByUnspsc(unspid);
		for (Object[] object : typeLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			extTypeList.add(selectItem);
		}
	}

	public void retrieveMeasurement() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> msrmntLst = reportSO.retrieveMeasurementByUnspsc(unspid);
		for (Object[] object : msrmntLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			measurmentList.add(selectItem);
		}
	}

	public void retrieveColor() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<Object[]> clrLst = reportSO.retrieveColorByUnspsc(unspid);
		for (Object[] object : clrLst) {
			SelectItem selectItem = new SelectItem();
			selectItem.setValue(object[0]);
			selectItem.setLabel(object[1].toString());
			colorList.add(selectItem);
		}
	}

	/*
	 * Enable criteria variables by report name
	 */
	public void criteriaVariablesByReport() {
		scReportBackingBean = new SCReportBackingBean();
		cmReportBackingBean = new CMReportBackingBean();
		flReportBackingBean = new FLReportBackingBean();
		smReportBackingBean = new SMReportBackingBean();
		ctReportBackingBean = new CTReportBackingBean();
		rptGenBean = new ReportsGenerateBean();
		getManagedBean().setRptGenBean(null);
		this.setSubModule(null);
		this.setReportName(ReportConst.EMPTY_VAL);
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		levOneCatLst = new ArrayList<SelectItem>();
		levTwoCatLst = new ArrayList<SelectItem>();
		levThreeCatLst = new ArrayList<SelectItem>();
		getManagedBean().setReportValue(this.getReportValue());
		if ((this.getModuleValue()) != null
				&& (getManagedBean().getReportValue()) != null) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			PMReportReqDTO reqDTO = new PMReportReqDTO();
			reqDTO.setReportId(getManagedBean().getReportValue());
			PMReportResDTO rptResDTO = reportSO.retriveReportByrptId(reqDTO);
			PMReport pmReport = rptResDTO.getPmReportDtls();
			if (null != pmReport) {
				String label = ReportConst.EMPTY_VAL;
				if ((null != pmReport.getSubModule())
						&& (ReportConst.EMPTY_VAL != pmReport.getSubModule())) {
					label = pmReport.getReportTitle() + ReportConst.RPT_EIFON
							+ pmReport.getSubModule();
					this.setSubModule(pmReport.getSubModule());
				} else {
					label = pmReport.getReportTitle();
				}
				this.setReportName(label);
				reportFileNameJSP = pmReport.getReportFilename();
				getManagedBean().setReportJsp(pmReport.getReportFilename());
			}
			if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))) {
				cmReportBackingBean.criteriaVariablesByReportByCM(rptGenBean,
						authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))
					|| (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				generateMonthList();
				generateYearList();
				scReportBackingBean.enableCriteriaVariablesByReportBySC(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))) {
				flReportBackingBean.enableCriteriaVariablesByReportByFL(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
				assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
				smReportBackingBean.enableCriteriaVariablesByReportBySM(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT))) {
				ctReportBackingBean.enableCriteriaVariablesByReportByCT(
						rptGenBean, authoriztionList);
			}
			resetReportForm();
			retrieveDates();
			getManagedBean().setRptGenBean(rptGenBean);
			madatoryIndicator();
		}
	}

	/*
	 * public void retriveSVOfficerUser(){ IPMReportSO reportSO =
	 * EpEjbBusinessDelegate
	 * .getInstance().getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
	 * IPMReportSO.class); List<String> svoUser=
	 * reportSO.retriveSVOfficerUser(lngUserId); stateLst = new
	 * ArrayList<SelectItem>(); if (svoUser.size()>0) { for (int i = 0; i <
	 * svoUser.size(); i++) {
	 * log.info("SVOUSER LIST #########################"+svoUser.get(i)); } } }
	 */
	public void retrieveDates() {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		if (this.getModuleValue().equalsIgnoreCase(
				ModuleCodeConst.SUPPLIER_SELECTION)) {
			Date currentDate = new Date();
			this.setScCreatedFromYear(outputDateFormat.format(currentDate)
					.substring(6, 10));
			this.setScCreatedToYear(outputDateFormat.format(currentDate)
					.substring(6, 10));
		}
		if (this.getModuleValue().equalsIgnoreCase(
				ModuleCodeConst.SUPPLIER_MANAGEMENT)) {
			Date currentDate = new Date();
			this.setSmCreatedFromYear(outputDateFormat.format(currentDate)
					.substring(6, 10));
			this.setSmCreatedToYear(outputDateFormat.format(currentDate)
					.substring(6, 10));
		}		
		/*
		 * if((getManagedBean().getModuleValue()!=null)&&((getManagedBean().
		 * getReportValue()!=null))){
		 * if((getManagedBean().getModuleValue().equalsIgnoreCase
		 * (ModuleCodeConst
		 * .SUPPLIER_MANAGEMENT))&&((getManagedBean().getReportValue
		 * ().equalsIgnoreCase
		 * (ReportConst.SM_LST_OF_SUPP_DOC_PEND_SUB_NO_DAYS_LAPS_RPT_VAL
		 * ))||(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.
		 * SM_LST_STATISTIC_COMP_BASE_COMP_TYPE_CAPITAL_RPT_VAL
		 * ))||(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.
		 * SM_LST_PRE_EXP_COMP_RPT_VAL)))){ this.setToDate(currentDate);
		 * this.setSmExpiryToDate(currentDate);
		 * this.setScSoneCrtdToDate(currentDate); Date newDate= new Date();
		 * newDate.setMonth(0); newDate.setDate(1); this.setFromDate(newDate);
		 * this.setSmExpiryFromDate(newDate);
		 * this.setScSoneCrtdFromDate(newDate); } }
		 */
		if ((this.getModuleValue() != null) && (this.getSubModule() != null)) {
			if ((this.getModuleValue()
					.equalsIgnoreCase(ReportConst.DP_MOD_VAL))
					&& (this.getSubModule()
							.equalsIgnoreCase(ReportConst.SUB_MOD_S1))) {
				Date currentDate = new Date();
				this.setScSoneCrtdToDate(currentDate);
				Date newDate = new Date();
				newDate.setMonth(0);
				newDate.setDate(1);
				this.setScSoneCrtdFromDate(newDate);
			}
		}
	}
	public void checkYearDefference() {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		String yearFrom=outputDateFormat.format(this.getFromDate()).substring(6, 10);
		String yearTo=outputDateFormat.format(this.getToDate()).substring(6, 10);
		String monthFrom=outputDateFormat.format(this.getFromDate()).substring(3, 5);
		String monthTo=outputDateFormat.format(this.getToDate()).substring(3, 5);
		String dayFrom=outputDateFormat.format(this.getFromDate()).substring(0, 2);
		String dayTo=outputDateFormat.format(this.getToDate()).substring(0, 2);
		int yearFromInt=Integer.parseInt(yearFrom);
		int yearToInt=Integer.parseInt(yearTo);
		int monthFromInt=Integer.parseInt(monthFrom);
		int monthToInt=Integer.parseInt(monthTo);
		int dayFromInt=Integer.parseInt(dayFrom);
		int dayToInt=Integer.parseInt(dayTo);		
		if(yearToInt==yearFromInt+1){
			if(monthToInt==monthFromInt){
				if(dayToInt>dayFromInt){
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.TO_DT_SHOULD_NOT_MORE_THAN_ONE_YEAR));
				}				
			}else if(monthToInt>monthFromInt){
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.TO_DT_SHOULD_NOT_MORE_THAN_ONE_YEAR));
			}			
		}else if(yearToInt>yearFromInt+1){			
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.TO_DT_SHOULD_NOT_MORE_THAN_ONE_YEAR));
		}		
	}

	public void retrieveState() {
		stateLst = new ArrayList<SelectItem>();
		IStateSO stateSO = (IStateSO) BusinessDelegate
				.getInstance()
				.getServiceObject(ReportConst.PM_MODULE_SER_OBJ, IStateSO.class);
		StateReqDTO stateReqDTO = new StateReqDTO();
		stateReqDTO.setCountryId(1L);// TODO malaysia
		StateResDTO resStateDTO = stateSO
				.retriveStatesListByCountryID(stateReqDTO);
		for (PmState state : resStateDTO.getStateList()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(state.getStateName());
			selectItem.setValue(state.getStateId());
			stateLst.add(selectItem);
		}
	}

	public void retrieveDistrict() {
		districtLst = new ArrayList<SelectItem>();
		IDistrictSO districtSO = (IDistrictSO) BusinessDelegate.getInstance()
				.getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						IDistrictSO.class);
		DistrictReqDTO districtReqDTO = new DistrictReqDTO();
		if (this.getSmState() != null
				&& this.getSmState() != ReportConst.EMPTY_VAL) {
			districtReqDTO.setStateId(Long.parseLong(this.getSmState()));
			DistrictResDTO districtResDTO = districtSO
					.retriveDistrictListByStateID(districtReqDTO);
			for (PmDistrict pmDistrict : districtResDTO.getDistrictList()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(pmDistrict.getDistrictName());
				selectItem.setValue(pmDistrict.getDistrictCode());
				districtLst.add(selectItem);
			}
		}
	}

	public List<SelectItem> retrieveCategoryLevOne() {
		levOneCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeResDTO catCodeResDTO = categoryCodeSO
				.retrieveCategoryL1List();
		for (PmCategoryL1 catOne : catCodeResDTO.getCategoryL1List()) {
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(catOne.getCategoryL1Code() + " - "
					+ catOne.getCategoryName());
			selectItem.setValue(catOne.getCategoryL1Id());
			levOneCatLst.add(selectItem);
		}
		return levOneCatLst;
	}

	

	public List<SelectItem> retrieveCategoryLevTwo(String mainCategory) {
		levTwoCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
		if ((mainCategory != null) && (mainCategory != ReportConst.EMPTY_VAL)) {
			catCodeReqDTO.setCategoryL1Id(Long.parseLong(mainCategory));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO
					.retrieveCategoryL2ListByL1Id(catCodeReqDTO);
			for (PmCategoryL2 catTwo : catCodeResDTO.getCategoryL2List()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(catTwo.getCategoryL2Code() + " - "
						+ catTwo.getCategoryName());
				selectItem.setValue(catTwo.getCategoryL2Id());
				levTwoCatLst.add(selectItem);
			}
		}
		return levTwoCatLst;
	}

	

	public List<SelectItem> retrieveCategoryLevThree(String subCategory) {
		levThreeCatLst = new ArrayList<SelectItem>();
		ICategoryCodeSO categoryCodeSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ,
						ICategoryCodeSO.class);
		CategoryCodeReqDTO catCodeReqDTO = new CategoryCodeReqDTO();
		if ((subCategory != null) && (subCategory != ReportConst.EMPTY_VAL)) {
			catCodeReqDTO.setCategoryL2Id(Long.parseLong(subCategory));
			CategoryCodeResDTO catCodeResDTO = categoryCodeSO
					.retrieveCategoryL3ListByL2Id(catCodeReqDTO);
			for (PmCategoryL3 catThree : catCodeResDTO.getCategoryL3List()) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(catThree.getCategoryL3Code() + " - "
						+ catThree.getCategoryName());
				selectItem.setValue(catThree.getCategoryL3Id());
				levThreeCatLst.add(selectItem);
			}
		}
		return levThreeCatLst;
	}

	

	public void enableRegistrationType() {
		this.setCmEpNumber(null);
		this.setCmIcNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmMofNumber(null);
		/*
		 * this.setSmEpNumber(null); this.setSmIcNumber(null);
		 * this.setSmBusinessRegNo(null); this.setSmMofNumber(null);
		 */
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
		if ((this.getCmRegistrationType() != null)
				&& (this.getCmRegistrationType() != ReportConst.EMPTY_VAL)) {
			if (this.getCmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_BASIC)) {
				getManagedBean().getRptGenBean().setCmBasicRegView(true);
				getManagedBean().getRptGenBean().setCmMofRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			} else if (this.getCmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_MOF)) {
				getManagedBean().getRptGenBean().setCmMofRegView(true);
				getManagedBean().getRptGenBean().setCmBasicRegView(false);
				this.setCmMofNumber(null);
				this.setCmEpNumber(null);
				this.setCmIcNumber(null);
				this.setCmBusinessRegNo(null);
			}
		} else if ((this.getSmRegistrationType() != null)
				&& (this.getSmRegistrationType() != ReportConst.EMPTY_VAL)) {
			if (this.getSmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_BASIC)) {
				getManagedBean().getRptGenBean().setSmBasicRegView(true);
				getManagedBean().getRptGenBean().setSmMofRegView(false);
				/*
				 * this.setSmMofNumber(null); this.setSmEpNumber(null);
				 * this.setSmIcNumber(null); this.setSmBusinessRegNo(null);
				 */
			} else if (this.getSmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_MOF)) {
				getManagedBean().getRptGenBean().setSmMofRegView(true);
				getManagedBean().getRptGenBean().setSmBasicRegView(false);
				/*
				 * this.setSmMofNumber(null); this.setSmEpNumber(null);
				 * this.setSmIcNumber(null); this.setSmBusinessRegNo(null);
				 */
			}
		}
	}

	public List<SelectItem> generateMonthList() {
		scMonthList = scReportBackingBean.generateMonthList();
		return scMonthList;
	}

	public List<String> generateYearList() {
		scYearList = scReportBackingBean.generateYearList();
		return scYearList;
	}

	public void createdToDtUpdate() {
		this.setScCreatedToYear(this.getScCreatedFromYear());
	}

	public void onSelectEffectedValues() {
		if (this.getScProcureAllList().size() > 0) {
			rptGenBean.setScProcureQuoteEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
			rptGenBean.setScProcureRestrctdEditView(true);
			scQuotationList = new ArrayList<SelectItem>();
			scTenderList = new ArrayList<SelectItem>();
			scRstrctdTndrList = new ArrayList<SelectItem>();
		} else if ((this.getScQuotationList().size() > 0)
				&& (this.getScTenderList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureRestrctdEditView(true);
		} else if ((this.getScRstrctdTndrList().size() > 0)
				&& (this.getScTenderList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureQuoteEditView(true);
		} else if ((this.getScRstrctdTndrList().size() > 0)
				&& (this.getScQuotationList().size() > 0)) {
			rptGenBean.setScProcureAllEditView(true);
			rptGenBean.setScProcureTenderEditView(true);
		} else {
			rptGenBean.setScProcureAllEditView(false);
			rptGenBean.setScProcureQuoteEditView(false);
			rptGenBean.setScProcureTenderEditView(false);
			rptGenBean.setScProcureRestrctdEditView(false);
		}
	}

	/*
	 * Generate all modules reports
	 */
	public void generateReport() {
		rptGenBean = new ReportsGenerateBean();
		if ((this.getModuleValue() != null)
				&& (getManagedBean().getReportValue() != null)) {
			if (!(getManagedBean().getReportValue().toString()
					.equals(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))
					&& !(getManagedBean().getReportValue().toString()
							.equals(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))
					&& !(getManagedBean().getReportValue().toString()
							.equals(ReportConst.PDW_EC_RPT_VAL))) {
				if ((((this.getFromDate() == null) && (this.getToDate() != null)) || ((this
						.getFromDate() != null) && (this.getToDate() == null)))
						|| (((this.getScCreatedToMonth() == null)
								&& (this.getScCreatedFromMonth() != null) || (this
								.getScCreatedToMonth() != null)
								&& (this.getScCreatedFromMonth() == null)))) {
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.PLZ_SEL_BOTH_DTS_RPT_SEARCH));
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getFromDate().after(this.getToDate())) {
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getToDate().after(this.getFromDate())) {
					checkYearDefference();
				}
			}
			/*
			 * if((this.getSmApplicationFromDate()!=null)&&(this.
			 * getSmApplicationToDate()!=null)){
			 * if(this.getSmApplicationFromDate
			 * ().after(this.getSmApplicationToDate())){ throw new
			 * EpException(CommonMethod
			 * .getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT)); } }
			 * if((this
			 * .getSmReceiveFromDate()!=null)&&(this.getSmReceiveToDate()
			 * !=null)){
			 * if(this.getSmReceiveFromDate().after(this.getSmReceiveToDate())){
			 * throw new EpException(CommonMethod.getMessageCommon(ReportConst.
			 * TO_DT_GTR_THN_FRM_DT)); } }
			 * if((this.getSmPrintedFromDate()!=null)
			 * &&(this.getSmPrintedToDate()!=null)){
			 * if(this.getSmPrintedFromDate().after(this.getSmPrintedToDate())){
			 * throw new EpException(CommonMethod.getMessageCommon(ReportConst.
			 * TO_DT_GTR_THN_FRM_DT)); } }
			 * if((this.getSmExpiryFromDate()!=null)&&
			 * (this.getSmExpiryToDate()!=null)){
			 * if(this.getSmExpiryFromDate().after(this.getSmExpiryToDate())){
			 * throw new EpException(CommonMethod.getMessageCommon(ReportConst.
			 * TO_DT_GTR_THN_FRM_DT)); } }
			 */
			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdToDate() != null)) {
				if (this.getScSoneCrtdFromDate().after(
						this.getScSoneCrtdToDate())) {
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
				}
			}
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setReportValueIdVar(ReportConst.FL_AMP_REPORT_ID_EQ
						+ getManagedBean().getReportValue());
			}
			if ((((PmUser) new FacesScopes().getUserSessionBean()).getUserId() != null)
					&& (new FacesScopes().getUserSessionBean().getUserId()
							.toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setUserIdVar(ReportConst.FL_AMP_USER_ID_EQ
						+ new FacesScopes().getUserSessionBean().getUserId()
								.toString());
			}
			if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_SELECTION)
					|| this.getModuleValue().equalsIgnoreCase(
							ReportConst.DP_MOD_VAL)) {
				generateSCReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.CATALOGUE_MANAGEMENT)) {
				generateCMReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.ORDER_FULFILLMENT)) {
				generateFLReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_MANAGEMENT)) {
				generateSMReport();
			} else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.CONTRACT_MANAGEMENT)) {
				generateCTReport();
			}
			if (null != constructedParamString
					&& !ReportConst.EMPTY_VAL.equals(constructedParamString)) {
				try {
					fileGenerateReport(getManagedBean().getReportJsp(),
							constructedParamString);
				} catch (IOException e) {
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.URL_NOT_CONNECTED));
				}
				// loadReportWithParamsString(constructedParamString);
			}
			RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam(ReportConst.RPT_IN_VALID,
					ReportConst.RPT_TRUE);
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_SEARCH));
		}
	}

	public void loadReportWithParams(List paramLst) {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		for (int i = 1; i <= paramLst.size(); i++) {
			gnrtdURL.append(paramLst.get(i));
		}
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			req.getSession().setAttribute(ReportConst.GENERATED_URL,
					gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadReport(String gnrtdURL) {
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			req.getSession().setAttribute(ReportConst.GENERATED_URL, gnrtdURL);
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadReportWithParamsString(String paramConstrString) {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString);
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		try {
			PortletRequest portalRequest = (PortletRequest) FacesContext
					.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest req = com.liferay.portal.util.PortalUtil
					.getOriginalServletRequest(com.liferay.portal.util.PortalUtil
							.getHttpServletRequest(portalRequest));
			// String
			// serverDetails="http://"+req.getServerName()+":"+req.getLocalPort();
			String baseUrl = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestContextPath();
			String path = baseUrl + ReportConst.SLASH_REPRT_VIEW_SERVLET;
			log.info(gnrtdURL.toString());
			req.getSession().setAttribute(ReportConst.GENERATED_URL,
					gnrtdURL.toString());
			RequestContext.getCurrentInstance().addCallbackParam(
					ReportConst.REPORT_VIEW_URL, path);
			setReportURL(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Generate CT module report
	 */
	public void generateCTReport() {
		if (((this.getCtContrMontrStatus() != null) && (this
				.getCtContrMontrStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtProductService() != null) && (this
						.getCtProductService() != ReportConst.EMPTY_VAL))
				|| ((this.getCtLoanNo() != null) && (this.getCtLoanNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtMinistry() != null) && (this.getCtMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getCtPtj() != null) && (this.getCtPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getCtContractNo() != null) && (this.getCtContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtAgreement() != null) && (this.getCtAgreement() != ReportConst.EMPTY_VAL))
				|| ((this.getCtStatus() != null) && (this.getCtStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtComplete() != null) && (this.getCtComplete() != ReportConst.EMPTY_VAL))
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL)) 
			    || ((this.getCtProcurementMode() != null) && (this.getCtProcurementMode().toString() != ReportConst.EMPTY_VAL))) {
			if ((this.getCtStatus() != null)
					&& (this.getCtStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtStatusVar(ReportConst.CT_AMP_STATUS_EQ
						+ this.getCtStatus());
			}
			if ((this.getCtContractNo() != null)
					&& (this.getCtContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtContractNoVar(ReportConst.CT_AMP_CONTRACT_NO_EQ
						+ this.getCtContractNo());
			}
			if ((this.getCtAgreement() != null)
					&& (this.getCtAgreement() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtAgreementVar(ReportConst.CT_AMP_AGREEMENT_EQ
						+ this.getCtAgreement());
			}
			if ((this.getCtComplete() != null)
					&& (this.getCtComplete() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtCompleteVar(ReportConst.CT_AMP_COMPLETE_EQ
						+ this.getCtComplete());
			}
			if ((this.getCtContrMontrStatus() != null)
					&& (this.getCtContrMontrStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtContrMontrStatusVar(ReportConst.CT_AMP_STATUS_EQ
								+ this.getCtContrMontrStatus());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.FL_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.FL_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getCtProcurementMode() != null)
					&& (this.getCtProcurementMode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtProcurementModeVar(ReportConst.CT_AMP_PROCUREMENTMODE_EQ
						+ this.getCtProcurementMode());
			}
			/*
			 * if((getManagedBean().getReportValue()!=null)&&(getManagedBean().
			 * getReportValue()!=ReportConst.EMPTY_VAL)){
			 * if(getManagedBean().getReportValue
			 * ().equalsIgnoreCase(ReportConst.
			 * CT_LST_OF_CONTRACT_USERS_ALL_RPT_VAL)){
			 * rptGenBean.setCtHiddenParamGroupVar
			 * (ReportConst.CT_AMP_GROUP_EQ+ReportConst.CT_GROUP_VAL_ALL); }else
			 * if
			 * (getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.
			 * CT_LST_OF_CONTRACT_USERS_CONTRCT_NO_RPT_VAL)){
			 * rptGenBean.setCtHiddenParamGroupVar
			 * (ReportConst.CT_AMP_GROUP_EQ+ReportConst.CT_GROUP_VAL_CNTRNO);
			 * }else
			 * if(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst
			 * .CT_LST_OF_CONTRACT_ALL_RPT_VAL)){
			 * rptGenBean.setCtHiddenParamGroupVar
			 * (ReportConst.CT_AMP_GROUP_EQ+ReportConst.CT_GROUP_VAL_ALL); }else
			 * if
			 * (getManagedBean().getReportValue().equalsIgnoreCase(ReportConst.
			 * CT_LST_OF_CONTRACT_ACTIVE_RPT_VAL)){
			 * rptGenBean.setCtHiddenParamGroupVar
			 * (ReportConst.CT_AMP_GROUP_EQ+ReportConst.CT_GROUP_VAL_ACTIVE);
			 * }else
			 * if(getManagedBean().getReportValue().equalsIgnoreCase(ReportConst
			 * .CT_LST_OF_CONTRACT_INACTIVE_RPT_VAL)){
			 * rptGenBean.setCtHiddenParamGroupVar
			 * (ReportConst.CT_AMP_GROUP_EQ+ReportConst.CT_GROUP_VAL_INACTIVE);
			 * } }
			 */
			if ((this.getCtGroupOne() != null)
					&& (this.getCtGroupOne() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupOneVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupOne());
			}
			if ((this.getCtGroupTwo() != null)
					&& (this.getCtGroupTwo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupTwoVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupTwo());
			}
			if ((this.getCtProductService() != null)
					&& (this.getCtProductService() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCtProductServiceVar(ReportConst.CT_AMP_PPRODUCTSERVICE_EQ
								+ this.getCtProductService());
			}
			if ((this.getCtLoanNo() != null)
					&& (this.getCtLoanNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtLoanNoVar(ReportConst.CT_AMP_PLOANO_EQ
						+ this.getCtLoanNo());
			}
			if ((this.getCtMinistry() != null)
					&& (this.getCtMinistry() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtMinistryVar(ReportConst.CT_AMP_PMINISTRY_EQ
						+ this.getCtMinistry());
			}
			if ((this.getCtPtj() != null)
					&& (this.getCtPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtPtjVar(ReportConst.CT_AMP_PPTJNAME_EQ
						+ this.getCtPtj());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getCtContractNoVar()
						+ rptGenBean.getCtAgreementVar()
						+ rptGenBean.getCtStatusVar()
						+ rptGenBean.getCtCompleteVar()
						+ rptGenBean.getCtContrMontrStatusVar()
						+ rptGenBean.getCtGroupOneVar()
						+ rptGenBean.getCtGroupTwoVar()
						+ rptGenBean.getCtProductServiceVar()
						+ rptGenBean.getCtMinistryVar()
						+ rptGenBean.getCtPtjVar()
						+ rptGenBean.getCtLoanNoVar()
						+ rptGenBean.getCtProcurementModeVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
	}

	/*
	 * Generate SM module report
	 */
	public void generateSMReport() {
		if ((getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL))
				|| (getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
			if ((this.getSmCreatedFromYear() != null)
					&& (this.getSmCreatedFromYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMandatoryCreatedFromYearVar(ReportConst.AMP_SM_YEAR_FROM_EQ
								+ this.getSmCreatedFromYear());
			}
			if ((this.getSmCreatedToYear() != null)
					&& (this.getSmCreatedToYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmMandatoryCreatedToYearVar(ReportConst.AMP_SM_YEAR_TO_EQ
								+ this.getSmCreatedToYear());
			}
			if ((this.getSmReportType() != null)
					&& (this.getSmReportType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setReportTypeTotVar(ReportConst.AMP_SM_REPORT_TYPE_EQ
								+ this.getSmReportType());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.AMP_SM_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.AMP_SM_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getSmMandatoryCreatedFromYearVar()
						+ rptGenBean.getSmMandatoryCreatedToYearVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getReportTypeTotVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else if ((getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL))) {

			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else if (((this.getSmYearText() != null) && (this.getSmYearText() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationType() != null) && (this
						.getSmApplicationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaymentType() != null) && (this
						.getSmPaymentType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCompanyName() != null) && (this
						.getSmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationType() != null) && (this
						.getSmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationNo() != null) && (this
						.getSmApplicationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmZone() != null) && (this.getSmZone() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupplierType() != null) && (this
						.getSmSupplierType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNo() != null) && (this.getSmMofRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationStatus() != null) && (this
						.getSmRegistrationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMainCategory() != null) && (this
						.getSmMainCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSubCategory() != null) && (this
						.getSmSubCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSubCategoryDivision() != null) && (this
						.getSmSubCategoryDivision() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationStatus() != null) && (this
						.getSmApplicationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmState() != null) && (this.getSmState() != ReportConst.EMPTY_VAL))
				|| ((this.getSmDistrict() != null) && (this.getSmDistrict() != ReportConst.EMPTY_VAL))
				|| ((this.getSmReportType() != null) && (this.getSmReportType() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))) {

			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.AMP_SM_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.AMP_SM_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}

			if ((this.getSmMainCategory() != null)
					&& (this.getSmMainCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setMainCategoryTotVar(ReportConst.AMP_SM_MAIN_CATEGORY_EQ
								+ this.getSmMainCategory());
			}
			if ((this.getSmSubCategory() != null)
					&& (this.getSmSubCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSubCategoryTotVar(ReportConst.AMP_SM_SUB_CATEGORY_EQ
								+ this.getSmSubCategory());
			}
			if ((this.getSmSubCategoryDivision() != null)
					&& (this.getSmSubCategoryDivision() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSubCategoryDivisionTotVar(ReportConst.AMP_SM_SUB_DIV_CATEGORY_EQ
								+ this.getSmSubCategoryDivision());
			}
			if ((this.getSmRegistrationType() != null)
					&& (this.getSmRegistrationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationTypeTotVar(ReportConst.AMP_SM_REG_TYPE_EQ
								+ this.getSmRegistrationType());
			}
			if ((this.getSmMofRegNo() != null)
					&& (this.getSmMofRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMofRegNoTotVar(ReportConst.AMP_SM_MOF_REG_NO_EQ
						+ this.getSmMofRegNo());
			}
			if ((this.getSmApplicationStatus() != null)
					&& (this.getSmApplicationStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationStatusTotVar(ReportConst.AMP_SM_APPL_STATUS_EQ
								+ this.getSmApplicationStatus());
			}
			if ((this.getSmCompanyName() != null)
					&& (this.getSmCompanyName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCompanyNameTotVar(ReportConst.AMP_SM_COMP_NAME_EQ
						+ this.getSmCompanyName());
			}
			if ((this.getSmRegistrationStatus() != null)
					&& (this.getSmRegistrationStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationStatusTotVar(ReportConst.AMP_SM_REG_STATUS_EQ
								+ this.getSmRegistrationStatus());
			}
			if ((this.getSmState() != null)
					&& (this.getSmState() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setStateTotVar(ReportConst.AMP_SM_STATE_NAME_EQ
						+ this.getSmState());
			}
			if ((this.getSmDistrict() != null)
					&& (this.getSmDistrict() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDistrictTotVar(ReportConst.AMP_SM_DISTRICT_NAME_EQ
								+ this.getSmDistrict());
			}
			if ((this.getSmSupplierType() != null)
					&& (this.getSmSupplierType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSupplierTypeTotVar(ReportConst.AMP_SM_SUPPLIER_TYPE_EQ
								+ this.getSmSupplierType());
			}
			if ((this.getSmZone() != null)
					&& (this.getSmZone() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setZoneTotVar(ReportConst.AMP_SM_SUPPLIER_ZONE_NAME_EQ
								+ this.getSmZone());
			}
			if ((this.getSmReportType() != null)
					&& (this.getSmReportType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setReportTypeTotVar(ReportConst.AMP_SM_REPORT_TYPE_EQ
								+ this.getSmReportType());
			}
			if ((this.getSmApplicationNo() != null)
					&& (this.getSmApplicationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationNoTotVar(ReportConst.AMP_SM_APPLICATION_NO_EQ
								+ this.getSmApplicationNo());
			}
			if ((this.getSmPaymentType() != null)
					&& (this.getSmPaymentType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setPaymentTypeTotVar(ReportConst.AMP_SM_PAYMENT_TYPE_EQ
								+ this.getSmPaymentType());
			}
			if ((this.getSmApplicationType() != null)
					&& (this.getSmApplicationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApplicationTypeTotVar(ReportConst.AMP_SM_APPL_TYPE_EQ
								+ this.getSmApplicationType());
			}
			if ((this.getSmSVOUser() != null)
					&& (this.getSmSVOUser() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmSVOUserVar(ReportConst.AMP_SM_APPL_TYPE_EQ
						+ this.getSmSVOUser());
			}
			if ((this.getSmYearText() != null)
					&& (this.getSmYearText() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmYearTextVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getSmYearText());
			}
			StringBuffer strBuff = new StringBuffer();
			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_APPL_PEND_SITE_VISIT_RPT_VAL))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL))
							|| (getManagedBean().getReportValue() != null)
							&& (getManagedBean().getReportValue().trim()
									.equalsIgnoreCase(ReportConst.SM_CATEGORY_STATISTIC_RPT_VAL))
									|| (getManagedBean().getReportValue() != null)
									&& (getManagedBean().getReportValue().trim()
											.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL))) {
				
				for (SmSupplierCategory smSupplier : this.getSelectedCatList()){
					strBuff.append(ReportConst.AMP_SM_MAIN_CATEGORY_EQ+smSupplier.getPmCategoryL1().getCategoryL1Id());
					strBuff.append(ReportConst.AMP_SM_SUB_CATEGORY_EQ+smSupplier.getPmCategoryL2().getCategoryL2Id());
					strBuff.append(ReportConst.AMP_SM_SUB_DIV_CATEGORY_EQ+smSupplier.getPmCategoryL3().getCategoryL3Id());
				}
			}

			// ----------------------------
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getExtentionTypeTotVar()
						+ rptGenBean.getExtentionBrandTotVar()
						+ rptGenBean.getExtentionMeasurmentTotVar()
						+ rptGenBean.getExtentionColourTotVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getEpNumberTotVar()
						+ rptGenBean.getBusinessRegNoTotVar()
						+ rptGenBean.getIcNumberTotVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getApplicationNoTotVar()
						+ rptGenBean.getZoneTotVar()
						+ rptGenBean.getSupplierTypeTotVar()
						+ rptGenBean.getMofRegNoTotVar()
						+ rptGenBean.getRegistrationStatusTotVar()
						/*+ rptGenBean.getMainCategoryTotVar()
						+ rptGenBean.getSubCategoryTotVar()
						+ rptGenBean.getSubCategoryDivisionTotVar()*/
						+strBuff
						+ rptGenBean.getApplicationStatusTotVar()
						+ rptGenBean.getSmYearTextVar()
						+ rptGenBean.getPaymentTypeTotVar()
						+ rptGenBean.getApplicationTypeTotVar()
						+ rptGenBean.getStateTotVar()
						+ rptGenBean.getDistrictTotVar()
						+ rptGenBean.getReportTypeTotVar()
						+ rptGenBean.getSmSVOUserVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
	}

	/*
	 * Generate SC module report
	 */
	public void generateSCReport() {
		if ((getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.KPPP1_PEMATUHAN_OC_RPT_VAL))
				|| (getManagedBean().getReportValue() != null)
				&& (getManagedBean().getReportValue().trim()
						.equalsIgnoreCase(ReportConst.KPP1_PEMATUHAN_EC_RPT_VAL))) {
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else if (((getScQuotationNo() != null) && (getScQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdFromDate() != null) && (getScSoneCrtdFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdToDate() != null) && (getScSoneCrtdToDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPIno() != null) && (getScPIno() != ReportConst.EMPTY_VAL))
				|| ((getScRequisenerId() != null) && (getScRequisenerId() != ReportConst.EMPTY_VAL))
				|| ((getScSupplier() != null) && (getScSupplier() != ReportConst.EMPTY_VAL))
				|| ((getScSoneStatus() != null) && (getScSoneStatus() != ReportConst.EMPTY_VAL))
				|| (this.getScProcureAllList().size() > 0)
				|| (this.getScQuotationList().size() > 0)
				|| (this.getScTenderList().size() > 0)
				|| (this.getScRstrctdTndrList().size() > 0)
				|| ((getScCreatedToMonth() != null) && (getScCreatedToMonth()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScCreatedFromMonth() != null) && (getScCreatedFromMonth()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScDeskOfficer() != null) && (getScDeskOfficer()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPtj() != null) && (getScPtj().toString() != ReportConst.EMPTY_VAL))
				|| ((getScStatus() != null) && (getScStatus().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishFromDate() != null) && (getScPublishFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishToDate() != null) && (getScPublishToDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((getScPrcRptGroup() != null) && (getScPrcRptGroup() != ReportConst.EMPTY_VAL))
				|| ((getScPIDtlsPrcRptGroup() != null) && (getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL))
				|| ((getScProcurementType() != null) && (getScProcurementType() != ReportConst.EMPTY_VAL))
				|| ((getScProcessRptQuotationNo() != null) && (getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getScCommonStatus() != null) && (getScCommonStatus() != ReportConst.EMPTY_VAL))
				|| ((getScLAPRNPURCHDEFAULTGroup() != null) && (getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL))
				|| ((getScREQUESTNOTESUMMARYGroup() != null) && (getScREQUESTNOTESUMMARYGroup() != ReportConst.EMPTY_VAL))
				|| ((getScPIDNo() != null) && (getScPIDNo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPType() != null) && (getScContractOrPKPType() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPNo() != null) && (getScContractOrPKPNo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPName() != null) && (getScContractOrPKPName() != ReportConst.EMPTY_VAL))
				|| ((getScRequestQuoteNo() != null) && (getScRequestQuoteNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getScCreatedToMonth() == null)
					&& (this.getScCreatedFromMonth() != null)
					|| (this.getScCreatedToMonth() != null)
					&& (this.getScCreatedFromMonth() == null)) {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.PLZ_SEL_BOTH_DTS_RPT_SEARCH));
			}
			if ((this.getScCreatedToMonth() != null)
					&& (this.getScCreatedFromMonth() != null)) {
				if ((this.getScCreatedToYear() != null)
						&& (this.getScCreatedFromYear() != null)) {
					if (Integer.parseInt(this.getScCreatedToYear()) < Integer
							.parseInt(this.getScCreatedFromYear())) {
						throw new EpException(
								CommonMethod
										.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
					} else if (Integer.parseInt(this.getScCreatedToYear()) == Integer
							.parseInt(this.getScCreatedFromYear())) {
						if (Integer.parseInt(this.getScCreatedToMonth()) < Integer
								.parseInt(this.getScCreatedFromMonth())) {
							throw new EpException(
									CommonMethod
											.getMessageCommon(ReportConst.TO_DT_GTR_THN_FRM_DT));
						}
					}
					rptGenBean
							.setScCreatedFromDtVar(ReportConst.AMP_CREAT_FRM_DT_EQ
									+ this.getScCreatedFromMonth()
									+ this.getScCreatedFromYear());
					rptGenBean
							.setScCreatedToDtVar(ReportConst.AMP_CREAT_TO_DT_EQ
									+ this.getScCreatedToMonth()
									+ this.getScCreatedToYear());
				}
			}
			if ((this.getScRequestQuoteNo() != null)
					&& (this.getScRequestQuoteNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScRequestQuoteNoVar(ReportConst.SC_AMP_RQ_NO_EQ
						+ this.getScRequestQuoteNo());
			}
			if ((this.getScContractOrPKPName() != null)
					&& (this.getScContractOrPKPName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScContractOrPKPNameVar(ReportConst.SC_AMP_CON_M_EQ
								+ this.getScContractOrPKPName());
			}
			if ((this.getScContractOrPKPNo() != null)
					&& (this.getScContractOrPKPNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScContractOrPKPNoVar(ReportConst.SC_AMP_CON_NO_EQ
						+ this.getScContractOrPKPNo());
			}
			if ((this.getScContractOrPKPType() != null)
					&& (this.getScContractOrPKPType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScContractOrPKPTypeVar(ReportConst.SC_AMP_CON_TYPE_EQ
								+ this.getScContractOrPKPType());
			}
			if ((this.getScPIDNo() != null)
					&& (this.getScPIDNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPIDNoVar(ReportConst.SC_AMP_PID_NO_EQ
						+ this.getScPIDNo());
			}
			if ((this.getScREQUESTNOTESUMMARYGroup() != null)
					&& (this.getScREQUESTNOTESUMMARYGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScREQUESTNOTESUMMARYGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScREQUESTNOTESUMMARYGroup());
			}
			if ((this.getScLAPRNPURCHDEFAULTGroup() != null)
					&& (this.getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScLAPRNPURCHDEFAULTGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScLAPRNPURCHDEFAULTGroup());
			}
			if ((this.getScCommonStatus() != null)
					&& (this.getScCommonStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScCommonStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScCommonStatus());
			}
			if ((this.getScQuotationNo() != null)
					&& (this.getScQuotationNo() != ReportConst.EMPTY_VAL)) {
				quotationNumberValidation();
				rptGenBean.setQuotationIdTotVar(ReportConst.AMP_QUOTE_ID_EQ
						+ this.getScQuotationId());
			}
			if ((this.getScDeskOfficer() != null)
					&& (this.getScDeskOfficer() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScDeskOfficerVar(ReportConst.AMP_DESK_OFFCR_EQ
						+ this.getScDeskOfficer());
			}
			if ((this.getScPtj() != null)
					&& (this.getScPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPtjVar(ReportConst.AMP_PROCURE_PTJ_EQ
						+ this.getScPtj());
			}
			if ((this.getScStatus() != null)
					&& (this.getScStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScStatusVar(ReportConst.AMP_PROCURE_STATUS_EQ
						+ this.getScStatus());
			}
			if ((this.getScProcessRptQuotationNo() != null)
					&& (this.getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScProcessRptQuotationNoVar(ReportConst.SC_AMP_QT_NO_EQ
								+ this.getScProcessRptQuotationNo());
			}
			if ((this.getScProcurementType() != null)
					&& (this.getScProcurementType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScProcessRptQuotationNoVar(ReportConst.SC_AMP_PROC_TYPE_EQ
								+ this.getScProcurementType());
			}
			StringBuilder procureMethod = new StringBuilder();
			if (this.getScProcureAllList().size() > 0) {
				rptGenBean
						.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ
								+ this.getScProcureAllList().get(0));
			} else {
				if (this.getScQuotationList().size() > 0) {
					procureMethod.append(this.getScQuotationList().get(0));
				}
				if (this.getScTenderList().size() > 0) {
					procureMethod.append(this.getScTenderList().get(0));
				}
				if (this.getScRstrctdTndrList().size() > 0) {
					procureMethod.append(this.getScRstrctdTndrList().get(0));
				}
				if ((procureMethod.length() > 0)) {
					rptGenBean
							.setScProcureMethodChkVar(ReportConst.AMP_PROCURE_METHOD_EQ
									+ procureMethod);
				}
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					ReportConst.URL_RPT_DATE_ANOTHER_FORMAT);
			if ((this.getScPublishFromDate() != null)
					&& (this.getScPublishFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPublishFromDateVar(ReportConst.AMP_PUB_FRM_DT_EQ
								+ outputDateFormat.format(this
										.getScPublishFromDate()));
			}
			if ((this.getScPublishToDate() != null)
					&& (this.getScPublishToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPublishToDateVar(ReportConst.AMP_PUB_TO_DT_EQ
						+ outputDateFormat.format(this.getScPublishToDate()));
			}
			SimpleDateFormat outputDateFormat2 = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSoneCrtdFromDateVar(ReportConst.AMP_CREAT_FRM_DT_EQ
								+ outputDateFormat2.format(this
										.getScSoneCrtdFromDate()));
			}
			if ((this.getScSoneCrtdToDate() != null)
					&& (this.getScSoneCrtdToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSoneCrtdToDateVar(ReportConst.AMP_CREAT_TO_DT_EQ
								+ outputDateFormat2.format(this
										.getScSoneCrtdToDate()));
			}
			if ((this.getScPIno() != null)
					&& (this.getScPIno() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPInoVar(ReportConst.SC_AMP_PI_NO_EQ
						+ this.getScPIno());
			}

			if ((this.getScSupplier() != null)
					&& (this.getScSupplier() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScSupplierVar(ReportConst.SC_AMP_SUPPLIER_EQ
						+ this.getScSupplier());
			}
			if ((this.getScSoneStatus() != null)
					&& (this.getScSoneStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScSoneStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScSoneStatus());
			}
			if ((this.getScPIDtlsPrcRptGroup() != null)
					&& (this.getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPIDtlsPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScPIDtlsPrcRptGroup());
			}
			if ((this.getScPrcRptGroup() != null)
					&& (this.getScPrcRptGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScPrcRptGroup());
			}
			String userRoleLogin = ReportConst.EMPTY_VAL;
			if (authoriztionList.contains(ReportConst.APPROVER_NAIK_TARAF)) {
				userRoleLogin = ReportConst.APPROVERNT;
			} else if (authoriztionList.contains(ReportConst.RN_APPROVER)) {
				userRoleLogin = ReportConst.APPROVER;
			} else if (authoriztionList.contains(ReportConst.DESK_OFFICER)) {
				userRoleLogin = ReportConst.DESKOFFR;
			} else if (authoriztionList.contains(ReportConst.REQUISITIONER)) {
				userRoleLogin = ReportConst.REQUISITIONER;
			}
			if (!userRoleLogin.equals(ReportConst.REQUISITIONER)) {
				if ((this.getScRequisenerId() != null)
						&& (this.getScRequisenerId() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScRequisenerIdVar(ReportConst.SC_AMP_REQUISENER_EQ
									+ this.getScRequisenerId());
				}
			}
			if ((userRoleLogin != null)
					&& (userRoleLogin != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScUserAccessRoleVar(ReportConst.AMP_USER_ACCESS_ROLE_EQ
								+ userRoleLogin);
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getQuotationIdTotVar()
						+ rptGenBean.getScProcureMethodChkVar()
						+ rptGenBean.getScDeskOfficerVar()
						+ rptGenBean.getScPtjVar()
						+ rptGenBean.getScSupplierVar()
						+ rptGenBean.getScRequisenerIdVar()
						+ rptGenBean.getScSoneStatusVar()
						+ rptGenBean.getScProcessRptQuotationNoVar()
						+ rptGenBean.getScProcurementTypeVar()
						+ rptGenBean.getScPInoVar()
						+ rptGenBean.getScPIDtlsPrcRptGroupVar()
						+ rptGenBean.getScPrcRptGroupVar()
						+ rptGenBean.getScSoneCrtdFromDateVar()
						+ rptGenBean.getScSoneCrtdToDateVar()
						+ rptGenBean.getScUserAccessRoleVar()
						+ rptGenBean.getScCreatedFromDtVar()
						+ rptGenBean.getScCreatedToDtVar()
						+ rptGenBean.getScPublishFromDateVar()
						+ rptGenBean.getScPublishToDateVar()
						+ rptGenBean.getScCommonStatusVar()
						+ rptGenBean.getScLAPRNPURCHDEFAULTGroupVar()
						+ rptGenBean.getScREQUESTNOTESUMMARYGroupVar()
						+ rptGenBean.getScPIDNoVar()
						+ rptGenBean.getScContractOrPKPTypeVar()
						+ rptGenBean.getScContractOrPKPNoVar()
						+ rptGenBean.getScContractOrPKPNameVar()
						+ rptGenBean.getScRequestQuoteNoVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
	}

	/*
	 * Generate CM module report
	 */
	public void generateCMReport() {
		if (((this.getCmExtention() != null) && (this.getCmExtention() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionCode() != null) && (this
						.getCmExtentionCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemName() != null) && (this.getCmItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemStatus() != null) && (this.getCmItemStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCmUnspscCode() != null) && (this.getCmUnspscCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmUnspscTitle() != null) && (this
						.getCmUnspscTitle() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionType() != null) && (this
						.getCmExtentionType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionBrand() != null) && (this
						.getCmExtentionBrand() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionMeasurment() != null) && (this
						.getCmExtentionMeasurment() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionColour() != null) && (this
						.getCmExtentionColour() != ReportConst.EMPTY_VAL))
				|| ((this.getCmCompanyName() != null) && (this
						.getCmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmRegistrationType() != null) && (this
						.getCmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemCategory() != null) && (this
						.getCmItemCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getCmProofIdentity() != null) && (this
						.getCmProofIdentity() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getCmEpNumber() != null) && (this.getCmEpNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmBusinessRegNo() != null) && (this
						.getCmBusinessRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCmIcNumber() != null) && (this.getCmIcNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmMofNumber() != null) && (this.getCmMofNumber() != ReportConst.EMPTY_VAL))) {
			if ((this.getCmItemName() != null)
					&& (this.getCmItemName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemNameTotVar(ReportConst.CM_AMP_ITM_NAME_EQ
						+ this.getCmItemName());
			}
			if ((this.getCmItemType() != null)
					&& (this.getCmItemType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemTypeTotVar(ReportConst.CM_AMP_ITEM_TYPE_EQ
						+ this.getCmItemType());
			}
			if ((this.getCmItemStatus() != null)
					&& (this.getCmItemStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setItemStatusTotVar(ReportConst.CM_AMP_ITEM_STATUS_EQ
								+ this.getCmItemStatus());
			}
			if ((this.getCmUnspscCode() != null)
					&& (this.getCmUnspscCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setUnspscCodeTotVar(ReportConst.CM_AMP_UNSPSC_CODE_EQ
								+ this.getCmUnspscCode());
			}
			if ((this.getCmUnspscTitle() != null)
					&& (this.getCmUnspscTitle() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setUnspscTitleTotVar(ReportConst.CM_AMP_UNSPSC_TITLE_EQ
								+ this.getCmUnspscTitle());
			}
			if ((this.getCmExtentionType() != null)
					&& (this.getCmExtentionType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionTypeVar(ReportConst.AMP_EXT_TYPE_EQ
						+ this.getCmExtentionType());
			}
			if ((this.getCmExtentionBrand() != null)
					&& (this.getCmExtentionBrand() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionBrandVar(ReportConst.AMP_EXT_BRAND_EQ
						+ this.getCmExtentionBrand());
			}
			if ((this.getCmExtentionMeasurment() != null)
					&& (this.getCmExtentionMeasurment() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCmExtentionMeasurmentVar(ReportConst.AMP_EXT_MEASURMENT_EQ
								+ this.getCmExtentionMeasurment());
			}
			if ((this.getCmExtentionColour() != null)
					&& (this.getCmExtentionColour() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setCmExtentionColourVar(ReportConst.AMP_EXT_COLOUR_EQ
								+ this.getCmExtentionColour());
			}
			if ((this.getCmExtentionCode() != null)
					&& (this.getCmExtentionCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionCodeVar(ReportConst.CM_AMP_EXT_CODE_EQ
						+ this.getCmExtentionCode());
			}
			if ((this.getCmCompanyName() != null)
					&& (this.getCmCompanyName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCompanyNameTotVar(ReportConst.CM_AMP_COMP_NAME_EQ
						+ this.getCmCompanyName());
			}
			if ((this.getCmRegistrationType() != null)
					&& (this.getCmRegistrationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRegistrationTypeTotVar(ReportConst.CM_AMP_REG_TYPE_EQ
								+ this.getCmRegistrationType());
			}
			if ((this.getCmItemCategory() != null)
					&& (this.getCmItemCategory() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemCategoryTotVar(ReportConst.AMP_ITM_CAT_EQ
						+ this.getCmItemCategory());
			}
			if ((this.getCmProofIdentity() != null)
					&& (this.getCmProofIdentity() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setProofIdentityTotVar(ReportConst.AMP_PROOF_IDNT_EQ
						+ this.getCmProofIdentity());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.CM_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.CM_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));
			}
			if ((this.getCmEpNumber() != null)
					&& (this.getCmEpNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setEpNumberTotVar(ReportConst.CM_AMP_EP_NO_EQ
						+ this.getCmEpNumber());
			}
			if ((this.getCmBusinessRegNo() != null)
					&& (this.getCmBusinessRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setBusinessRegNoTotVar(ReportConst.CM_AMP_BUSI_REG_NO_EQ
								+ this.getCmBusinessRegNo());
			}
			if ((this.getCmIcNumber() != null)
					&& (this.getCmIcNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setIcNumberTotVar(ReportConst.CM_AMP_IC_NO_EQ
						+ this.getCmIcNumber());
			}
			if ((this.getCmMofNumber() != null)
					&& (this.getCmMofNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMofNumberTotVar(ReportConst.CM_AMP_MOF_NO_EQ
						+ this.getCmMofNumber());
			}
			/*
			 * if((this.getCmSupplOrAdmin()!=null)&&(this.getCmSupplOrAdmin()!=
			 * ReportConst.EMPTY_VAL)){
			 * rptGenBean.setCmSupplOrAdminVar(ReportConst
			 * .CM_AMP_SUPPOR_ADMIN_EQ+this.getCmSupplOrAdmin()); }
			 */
			if ((this.getCmExtention() != null)
					&& (this.getCmExtention() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionVar(ReportConst.CM_AMP_EXTENSION_EQ
						+ this.getCmExtention());
			}			
			if ((this.getCmGroupOne() != null)
						&& (this.getCmGroupOne() != ReportConst.EMPTY_VAL)) {
					rptGenBean.setCmGroupOneVar(ReportConst.CM_AMP_PGROUP_EQ
							+ this.getCmGroupOne());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getCmExtentionTypeVar()
						+ rptGenBean.getCmExtentionBrandVar()
						+ rptGenBean.getCmExtentionMeasurmentVar()
						+ rptGenBean.getCmExtentionColourVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getCmExtentionVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getEpNumberTotVar()
						+ rptGenBean.getBusinessRegNoTotVar()
						+ rptGenBean.getIcNumberTotVar()
						+ rptGenBean.getCmGroupOneVar()
						+ rptGenBean.getMofNumberTotVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getCmExtentionCodeVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
	}

	/*
	 * Generate FL module report
	 */
	public void generateFLReport() {
		if (((this.getFlPtj() != null) && (this.getFlPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractNo() != null) && (this.getFlContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSst() != null) && (this.getFlSst() != ReportConst.EMPTY_VAL))
				|| ((this.getFlJabatan() != null) && (this.getFlJabatan() != ReportConst.EMPTY_VAL))
				|| ((this.getFlMinistry() != null) && (this.getFlMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplier() != null) && (this.getFlSupplier() != ReportConst.EMPTY_VAL))
				|| ((this.getFlStatus() != null) && (this.getFlStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDuration() != null) && (this.getFlDuration() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecievingOfficer() != null) && (this
						.getFlRecievingOfficer() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryAddress() != null) && (this
						.getFlDeliveryAddress() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecNoteNo() != null) && (this.getFlRecNoteNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractType() != null) && (this
						.getFlContractType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRequisitioner() != null) && (this
						.getFlRequisitioner() != ReportConst.EMPTY_VAL))
				|| ((this.getFlMtoContract() != null) && (this
						.getFlMtoContract() != ReportConst.EMPTY_VAL))
				|| ((this.getFlInvoiceNo() != null) && (this.getFlInvoiceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeductionType() != null) && (this
						.getFlDeductionType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCrNo() != null) && (this.getFlCrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlApproverName() != null) && (this
						.getFlApproverName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDocumentType() != null) && (this
						.getFlDocumentType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCriteria() != null) && (this.getFlCriteria() != ReportConst.EMPTY_VAL))
				|| ((this.getFlScoring() != null) && (this.getFlScoring() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPkpNo() != null) && (this.getFlPkpNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryOrderNo() != null) && (this
						.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemCode() != null) && (this.getFlItemCode() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPoAmount() != null) && (this.getFlPoAmount() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPrNo() != null) && (this.getFlPrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPmo() != null) && (this.getFlPmo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemName() != null) && (this.getFlItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItmType() != null) && (this.getFlItmType() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate()
						.toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractOrder() != null) && (this
						.getFlContractOrder() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractRequest() != null) && (this
						.getFlContractRequest() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPurchaseOrderNo() != null) && (this
						.getFlPurchaseOrderNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getFlPtj() != null)
					&& (this.getFlPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPtjTotVar(ReportConst.FL_AMP_PTJ_NAME_EQ
						+ this.getFlPtj());
			}
			if ((this.getFlContractNo() != null)
					&& (this.getFlContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setContractNoTotVar(ReportConst.FL_AMP_CONTRACT_NO_EQ
								+ this.getFlContractNo());
			}
			if ((this.getFlSst() != null)
					&& (this.getFlSst() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSstTotVar(ReportConst.FL_AMP_SST_NO_EQ
						+ this.getFlSst());
			}
			if ((this.getFlMinistry() != null)
					&& (this.getFlMinistry() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ
						+ this.getFlMinistry());
			}
			if ((this.getFlSupplier() != null)
					&& (this.getFlSupplier() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSupplierTotVar(ReportConst.FL_AMP_SUPPL_NAME_EQ
						+ this.getFlSupplier());
			}
			if ((this.getFlStatus() != null)
					&& (this.getFlStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setStatusTotVar(ReportConst.FL_AMP_STATUS_EQ
						+ this.getFlStatus());
			}
			if ((this.getFlRecievingOfficer() != null)
					&& (this.getFlRecievingOfficer() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRecievingOfficerTotVar(ReportConst.FL_AMP_FL_REC_OFF_EQ
								+ this.getFlRecievingOfficer());
			}
			if ((this.getFlDeliveryAddress() != null)
					&& (this.getFlDeliveryAddress() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeliveryAddressTotVar(ReportConst.FL_AMP_DELI_ADDR_EQ
								+ this.getFlDeliveryAddress());
			}
			if ((this.getFlRecNoteNo() != null)
					&& (this.getFlRecNoteNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlRecNoteNoTotVar(ReportConst.FL_AMP_FL_REC_NOTE_EQ
								+ this.getFlRecNoteNo());
			}
			if ((this.getFlContractType() != null)
					&& (this.getFlContractType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setContractTypeTotVar(ReportConst.FL_AMP_COTRACT_TYPE_EQ
								+ this.getFlContractType());
			}
			if ((this.getFlRequisitioner() != null)
					&& (this.getFlRequisitioner() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setRequisitionerTotVar(ReportConst.FL_AMP_REQUI_NAME_EQ
								+ this.getFlRequisitioner());
			}
			if ((this.getFlMtoContract() != null)
					&& (this.getFlMtoContract() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setMtoContractTotVar(ReportConst.AMP_MTO_CONTR_EQ
						+ this.getFlMtoContract());
			}
			if ((this.getFlInvoiceNo() != null)
					&& (this.getFlInvoiceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setInvoiceNoTotVar(ReportConst.AMP_INVOICE_NO_EQ
						+ this.getFlInvoiceNo());
			}
			if ((this.getFlDeductionType() != null)
					&& (this.getFlDeductionType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeductionTypeTotVar(ReportConst.AMP_DEDUCT_TYPE_EQ
								+ this.getFlDeductionType());
			}
			if ((this.getFlCrNo() != null)
					&& (this.getFlCrNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCrNoTotVar(ReportConst.AMP_CR_NO_EQ
						+ this.getFlCrNo());
			}
			if ((this.getFlApproverName() != null)
					&& (this.getFlApproverName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setApproverNameTotVar(ReportConst.FL_AMP_APPROVER_NAME_EQ
								+ this.getFlApproverName());
			}
			if ((this.getFlDocumentType() != null)
					&& (this.getFlDocumentType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setDocumentTypeTotVar(ReportConst.AMP_DOC_TYPE_EQ
						+ this.getFlDocumentType());
			}
			if ((this.getFlCriteria() != null)
					&& (this.getFlCriteria() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCriteriaTotVar(ReportConst.AMP_CRITERIA_EQ
						+ this.getFlCriteria());
			}
			if ((this.getFlScoring() != null)
					&& (this.getFlScoring() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScoringTotVar(ReportConst.AMP_SCORING_EQ
						+ this.getFlScoring());
			}
			if ((this.getFlPkpNo() != null)
					&& (this.getFlPkpNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPkpNoTotVar(ReportConst.FL_AMP_PKP_NO_EQ
						+ this.getFlPkpNo());
			}
			if ((this.getFlDeliveryOrderNo() != null)
					&& (this.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeliveryOrderNoTotVar(ReportConst.FL_AMP_DELI_ORD_NO_EQ
								+ this.getFlDeliveryOrderNo());
			}
			if ((this.getFlItemCode() != null)
					&& (this.getFlItemCode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemCodeTotVar(ReportConst.AMP_ITM_CODE_EQ
						+ this.getFlItemCode());
			}
			if ((this.getFlPoAmount() != null)
					&& (this.getFlPoAmount() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPoAmountTotVar(ReportConst.AMP_PO_AMT_EQ
						+ this.getFlPoAmount());
			}
			if ((this.getFlPrNo() != null)
					&& (this.getFlPrNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPrNoTotVar(ReportConst.AMP_PR_NO_EQ
						+ this.getFlPrNo());
			}
			if ((this.getFlPmo() != null)
					&& (this.getFlPmo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmoTotVar(ReportConst.AMP_PMO_EQ
						+ this.getFlPmo());
			}
			if ((this.getFlItemName() != null)
					&& (this.getFlItemName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setItemNameTotVar(ReportConst.AMP_ITM_NAME_EQ
						+ this.getFlItemName());
			}
			SimpleDateFormat outputDateFormat = new SimpleDateFormat(
					CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getFromDate() != null)
					&& (this.getFromDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFromDateVar(ReportConst.FL_AMP_FROM_DATE_EQ
						+ outputDateFormat.format(this.getFromDate()));
			}
			// SimpleDateFormat outputDateFormat2 = new
			// SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
			if ((this.getToDate() != null)
					&& (this.getToDate().toString() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setToDateVar(ReportConst.FL_AMP_TO_DATE_EQ
						+ outputDateFormat.format(this.getToDate()));

			}
			if ((this.getFlContractOrder() != null)
					&& (this.getFlContractOrder() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlContractOrderVar(ReportConst.FL_AMP_COTRACT_ORDER_EQ
								+ this.getFlContractOrder());
			}
			if ((this.getFlContractRequest() != null)
					&& (this.getFlContractRequest() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlContractRequestVar(ReportConst.FL_AMP_COTRACT_REQUEST_EQ
								+ this.getFlContractRequest());
			}
			if ((this.getFlPurchaseOrderNo() != null)
					&& (this.getFlPurchaseOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlPurchaseOrderNoVar(ReportConst.FL_AMP_PURCHASE_ORDER_EQ
								+ this.getFlPurchaseOrderNo());
			}
			if ((this.getFlGroupOne() != null)
					&& (this.getFlGroupOne() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupOneVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupOne());
			}
			if ((this.getFlGroupTwo() != null)
					&& (this.getFlGroupTwo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTwoVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTwo());
			}
			if ((this.getFlGroupThree() != null)
					&& (this.getFlGroupThree() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupThreeVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupThree());
			}
			if ((this.getFlGroupFour() != null)
					&& (this.getFlGroupFour() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFourVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFour());
			}
			if ((this.getFlGroupFive() != null)
					&& (this.getFlGroupFive() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFiveVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFive());
			}
			if ((this.getFlGroupSix() != null)
					&& (this.getFlGroupSix() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSixVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSix());
			}
			if ((this.getFlGroupSeven() != null)
					&& (this.getFlGroupSeven() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSevenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSeven());
			}
			if ((this.getFlGroupEight() != null)
					&& (this.getFlGroupEight() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupEightVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupEight());
			}
			if ((this.getFlGroupNine() != null)
					&& (this.getFlGroupNine() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupNineVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupNine());
			}
			if ((this.getFlGroupTen() != null)
					&& (this.getFlGroupTen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTen());
			}
			if ((this.getFlGroupEleven() != null)
					&& (this.getFlGroupEleven() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupElevenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupEleven());
			}
			if ((this.getFlGroupTwelve() != null)
					&& (this.getFlGroupTwelve() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupTwelveVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupTwelve());
			}
			if ((this.getFlGroupThirteen() != null)
					&& (this.getFlGroupThirteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupThirteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupThirteen());
			}
			if ((this.getFlGroupFourteen() != null)
					&& (this.getFlGroupFourteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFourteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFourteen());
			}
			if ((this.getFlGroupFifteen() != null)
					&& (this.getFlGroupFifteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupFifteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupFifteen());
			}
			if ((this.getFlGroupSixteen() != null)
					&& (this.getFlGroupSixteen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSixteenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSixteen());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getItemNameTotVar()
						+ rptGenBean.getItemTypeTotVar()
						+ rptGenBean.getItemStatusTotVar()
						+ rptGenBean.getUnspscCodeTotVar()
						+ rptGenBean.getUnspscTitleTotVar()
						+ rptGenBean.getExtentionCodeTotVar()
						+ rptGenBean.getCompanyNameTotVar()
						+ rptGenBean.getRegistrationTypeTotVar()
						+ rptGenBean.getItemCategoryTotVar()
						+ rptGenBean.getProofIdentityTotVar()
						+ rptGenBean.getPtjTotVar()
						+ rptGenBean.getContractNoTotVar()
						+ rptGenBean.getSstTotVar()
						+ rptGenBean.getJabatanTotVar()
						+ rptGenBean.getMinistryTotVar()
						+ rptGenBean.getSupplierTotVar()
						+ rptGenBean.getStatusTotVar()
						+ rptGenBean.getDurationTotVar()
						+ rptGenBean.getRecievingOfficerTotVar()
						+ rptGenBean.getDeliveryAddressTotVar()
						+ rptGenBean.getFlRecNoteNoTotVar()
						+ rptGenBean.getContractTypeTotVar()
						+ rptGenBean.getRequisitionerTotVar()
						+ rptGenBean.getMtoContractTotVar()
						+ rptGenBean.getInvoiceNoTotVar()
						+ rptGenBean.getDeductionTypeTotVar()
						+ rptGenBean.getCrNoTotVar()
						+ rptGenBean.getApproverNameTotVar()
						+ rptGenBean.getDocumentTypeTotVar()
						+ rptGenBean.getCriteriaTotVar()
						+ rptGenBean.getScoringTotVar()
						+ rptGenBean.getPkpNoTotVar()
						+ rptGenBean.getDeliveryOrderNoTotVar()
						+ rptGenBean.getItemCodeTotVar()
						+ rptGenBean.getPoAmountTotVar()
						+ rptGenBean.getPrNoTotVar()
						+ rptGenBean.getPmoTotVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getFlContractOrderVar()
						+ rptGenBean.getFlContractRequestVar()
						+ rptGenBean.getFlPurchaseOrderNoVar()
						+ rptGenBean.getFlGroupOneVar()
						+ rptGenBean.getFlGroupTwoVar()
						+ rptGenBean.getFlGroupThreeVar()
						+ rptGenBean.getFlGroupFourVar()
						+ rptGenBean.getFlGroupFiveVar()
						+ rptGenBean.getFlGroupSixVar()
						+ rptGenBean.getFlGroupSevenVar()
						+ rptGenBean.getFlGroupEightVar()
						+ rptGenBean.getFlGroupNineVar()
						+ rptGenBean.getFlGroupTenVar()
						+ rptGenBean.getFlGroupElevenVar()
						+ rptGenBean.getFlGroupTwelveVar()
						+ rptGenBean.getFlGroupThirteenVar()
						+ rptGenBean.getFlGroupFourteenVar()
						+ rptGenBean.getFlGroupFifteenVar()
						+ rptGenBean.getFlGroupSixteenVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NO_URL_PROVIDED_FOR_THIS_RPT));
			}
		} else {
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.PLZ_SEL_ONE_RPT_CRITERIA_SEARCH));
		}
	}

	/*
	 * validate quotNo
	 */
	public void quotationNumberValidation() {
		if ((this.getScQuotationNo() != null)
				&& (this.getScQuotationNo() != ReportConst.EMPTY_VAL)) {
			specialCharValidation();
		}
	}

	public void specialCharValidation() {
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		Matcher match = pattern.matcher(this.getScQuotationNo());
		if (match.matches()) {
			getQuotationNumber();
		} else {
			this.setScQuotationNo(null);
			throw new EpException(
					CommonMethod
							.getMessageCommon(ReportConst.NUMERIC_CHAR_VALID_ONLY));
		}
	}

	/*
	 * retrieve qtid based on qtno
	 */
	public void getQuotationNumber() {
		QuationTenderReqDTO tenderReqDTO = new QuationTenderReqDTO();
		// log.info("QtNo="+this.getScQuotationNo());
		if (this.getScQuotationNo() != null) {
			tenderReqDTO.setQtTndrNo(this.getScQuotationNo().toUpperCase());
			IQTDetailSO detailSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
							IQTDetailSO.class);
			QuationTenderResDTO resDTO = detailSO
					.retriveQtByQtNumber(tenderReqDTO);
			ScQt scqt = resDTO.getQtDetail();
			if (scqt != null) {
				if (scqt.getQtId() != null) {
					// log.info("QtId="+scqt.getQtId());
					this.setScQuotationId(scqt.getQtId().toString());
				} else {
					this.setScQuotationNo(null);
					throw new EpException(
							CommonMethod
									.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
				}
			} else {
				this.setScQuotationNo(null);
				throw new EpException(
						CommonMethod
								.getMessageCommon(ReportConst.NOT_VALID_QOTATION_NO));
			}
		}
	}

	/*
	 * Clear report criteria
	 */
	public void resetReportForm() {
		// retriveModuleList();
		// retriveReportListBymoduleName();
		// getManagedBean().setReportValue(null);
		// this.setModuleValue(null);
		assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
		this.setFromDate(null);
		this.setToDate(null);
		this.setScQuotationId(null);
		this.setScQuotationNo(null);
		this.setScPaymentId(null);
		this.setCmItemType(null);
		this.setCmItemStatus(null);
		this.setCmUnspscCode(null);
		this.setCmUnspscTitle(null);
		this.setCmCompanyName(null);
		this.setCmRegistrationType(null);
		this.setCmItemCategory(null);
		this.setCmItemName(null);
		this.setCmEpNumber(null);
		this.setCmBusinessRegNo(null);
		this.setCmIcNumber(null);
		this.setCmMofNumber(null);
		this.setCmExtentionCode(null);
		this.setCmExtentionType(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		this.setCmProofIdentity(null);
		this.setCmSupplOrAdmin(null);
		this.setCmGroupOne(null);
		this.setCmExtentionBrand(null);
		this.setCmExtentionType(null);
		this.setCmExtentionMeasurment(null);
		this.setCmExtentionColour(null);
		brandList = new ArrayList<SelectItem>();
		extTypeList = new ArrayList<SelectItem>();
		measurmentList = new ArrayList<SelectItem>();
		colorList = new ArrayList<SelectItem>();
		this.setFlItmType(null);
		this.setFlPtj(null);
		this.setFlContractNo(null);
		this.setFlSst(null);
		this.setFlJabatan(null);
		this.setFlMinistry(null);
		this.setFlSupplier(null);
		this.setFlStatus(null);
		this.setFlDuration(null);
		this.setFlRecievingOfficer(null);
		this.setFlDeliveryAddress(null);
		this.setFlRecNoteNo(null);
		this.setFlContractType(null);
		this.setFlRequisitioner(null);
		this.setFlMtoContract(null);
		this.setFlInvoiceNo(null);
		this.setFlDeductionType(null);
		this.setFlCrNo(null);
		this.setFlApproverName(null);
		this.setFlDocumentType(null);
		this.setFlCriteria(null);
		this.setFlScoring(null);
		this.setFlPkpNo(null);
		this.setFlDeliveryOrderNo(null);
		this.setFlItemCode(null);
		this.setFlPoAmount(null);
		this.setFlPrNo(null);
		this.setFlPmo(null);
		this.setFlItemName(null);
		this.setSmApplicationNo(null);
		this.setSmZone(null);
		this.setSmSupplierType(null);
		this.setSmMofRegNo(null);
		this.setSmRegistrationStatus(null);
		this.setSmcategory(null);
		this.setSmMainCategory(null);
		this.setSmSubCategory(null);
		this.setSmSubCategoryDivision(null);
		this.setSmApplicationStatus(null);
		this.setSmPaymentType(null);
		this.setSmApplicationType(null);
		this.setSmState(null);
		this.setSmSVOUser(null);
		this.setSmDistrict(null);
		this.setSmReportType(null);
		this.setSmCompanyName(null);
		this.setSmRegistrationType(null);
		this.setSmCreatedFromYear(null);
		this.setSmCreatedToYear(null);
		this.setSmYearText(null);
		this.setFlContractOrder(null);
		this.setFlContractRequest(null);
		this.setFlPurchaseOrderNo(null);
		this.setScCreatedToYear(null);
		this.setScCreatedToMonth(null);
		this.setScCreatedFromYear(null);
		this.setScCreatedFromMonth(null);
		this.setScDeskOfficer(null);
		this.setScPtj(null);
		this.setScStatus(null);
		this.setScPublishFromDate(null);
		this.setScPublishToDate(null);
		this.setScSoneCrtdFromDate(null);
		this.setScSoneCrtdToDate(null);
		this.setScRequisenerId(null);
		this.setScSupplier(null);
		this.setScLogin(null);
		this.setScPIno(null);
		this.setScSoneStatus(null);
		this.setScPrcRptGroup(null);
		this.setScPIDtlsPrcRptGroup(null);
		this.setScProcurementType(null);
		this.setScProcessRptQuotationNo(null);
		this.setScCommonStatus(null);
		this.setScLAPRNPURCHDEFAULTGroup(null);
		this.setScREQUESTNOTESUMMARYGroup(null);
		this.setScPIDNo(null);
		this.setScContractOrPKPType(null);
		this.setScContractOrPKPNo(null);
		this.setScContractOrPKPName(null);
		this.setScRequestQuoteNo(null);
		scProcureAllList = new ArrayList<SelectItem>();
		scQuotationList = new ArrayList<SelectItem>();
		scTenderList = new ArrayList<SelectItem>();
		scRstrctdTndrList = new ArrayList<SelectItem>();
		this.setCtContractNo(null);
		this.setCtAgreement(null);
		this.setCtStatus(null);
		this.setCtComplete(null);
		this.setCtGroupOne(null);
		this.setCtGroupTwo(null);
		this.setCtProductService(null);
		this.setCtLoanNo(null);
		this.setCtMinistry(null);
		this.setCtPtj(null);
		this.setCtProcurementMode(null);
		this.setFlGroupOne(null);
		this.setFlGroupTwo(null);
		this.setFlGroupThree(null);
		this.setFlGroupFour(null);
		this.setFlGroupFive(null);
		this.setFlGroupSix(null);
		this.setFlGroupSeven(null);
		this.setFlGroupEight(null);
		this.setFlGroupNine(null);
		this.setFlGroupTen(null);
		this.setFlGroupEleven(null);
		this.setFlGroupTwelve(null);
		this.setFlGroupThirteen(null);
		this.setFlGroupFourteen(null);
		this.setFlGroupFifteen(null);
		this.setFlGroupSixteen(null);
	}

	protected void fileGenerateReport(String reportFile,
			String paramConstrString) throws IOException {
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString);
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		HttpURLConnection urlConnection = null;
		URL obj = new URL(gnrtdURL.toString());
		log.info(gnrtdURL.toString());
		urlConnection = (HttpURLConnection) obj.openConnection();
		BufferedInputStream buf = new BufferedInputStream(
				urlConnection.getInputStream());
		PortletResponse portletResponse = (PortletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		HttpServletResponse res = PortalUtil
				.getHttpServletResponse(portletResponse);
		int position = reportFile.lastIndexOf(ReportConst.DOTJSP);
		if (position != -1) {
			reportFile = reportFile.substring(0, position);
		}
		res.setHeader(
				ReportConst.CONTENT_DISPOSITION,
				ReportConst.ATTACHMENTNFILENAMEEQUIFON + reportFile
						+ ReportConst.UNDERSCORE
						+ ReportConst.REPORT_DATE_TIME.format(new Date())
						+ ReportConst.DOTPDFEIFON);//
		res.setHeader(ReportConst.CONTENT_TRANSFER_ENCODING, ReportConst.BINARY);
		res.setContentType(ReportConst.APPLICATIONEIFONPDF);
		res.flushBuffer();
		OutputStream out = res.getOutputStream();
		byte[] b = new byte[128];
		int size;
		while ((size = buf.read(b, 0, b.length)) != -1) {
			out.write(b, 0, size);
		}
		buf.close();
		out.close();
	}
}
