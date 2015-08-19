package my.ep.web.backingbean.report;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.dto.rpt.PMReportReqDTO;
import my.ep.biz.dto.rpt.PMReportResDTO;
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
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
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
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.rpt.PMReport;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.type.pm.OrganizationType;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.sc.quatationtender.SourcingBaseBackingBean;
import my.ep.web.bean.report.ReportsGenerateBean;
import my.ep.web.constant.report.CommonMethod;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.ReportMangedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

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
	private String reportCategory;
	private String stringZone;
	private ScQt scqt;
	private boolean enableValue = false;
	String parameterCodeType = new String();
	private List<SelectItem> reportList = new ArrayList<SelectItem>();
	private List<String> reportCategoryList = new ArrayList<String>();
	private List<SelectItem> moduleList;
	private List<SelectItem> regTypeList;
	ReportsGenerateBean rptGenBean = new ReportsGenerateBean();
	private List<String> authoriztionList = new ArrayList<String>();
	private List<String> orgProfileList = new ArrayList<String>();
	private List<String> scYearList = new ArrayList<String>();
	private List<SelectItem> orgProfileIdNameList = new ArrayList<SelectItem>();
	private List<SelectItem> userListbyprofileRole = new ArrayList<SelectItem>();
	private List<SelectItem> stateLst = new ArrayList<SelectItem>();
	private List<SelectItem> smStateList  = new ArrayList<SelectItem>();
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
	private List<SelectItem> pmMinistryList = new ArrayList<SelectItem>();
	private List<String> pmPtjList = new ArrayList<String>();
	private List<SelectItem> processingOfficerList = new ArrayList<SelectItem>();
	// private List<PmOrgProfile> ptjList;
	private List<String> loanList = new ArrayList<String>();
	private List<SelectItem> unspcList = new ArrayList<SelectItem>();
	private List<SelectItem> brandList = new ArrayList<SelectItem>();
	private List<SelectItem> extTypeList = new ArrayList<SelectItem>();
	private List<SelectItem> measurmentList = new ArrayList<SelectItem>();
	private List<SelectItem> colorList = new ArrayList<SelectItem>();
	private List<SelectItem> approverNayakTarafList = new ArrayList<SelectItem>();
	private List<SelectItem> scPtjList = new ArrayList<SelectItem>();
	private List<SelectItem> scRequisitionerList = new ArrayList<SelectItem>();
	private List<SelectItem> scDescOfficerList = new ArrayList<SelectItem>();
	private List<SelectItem> smUPSSVOUserList = new ArrayList<SelectItem>();
	private List<SelectItem> ctSubTypeList = new ArrayList<SelectItem>();
	private List<SelectItem> zoneList = new ArrayList<SelectItem>();
	private List<String> paramList = new ArrayList<String>();
	private List<String> highRoleCodeList = new ArrayList<String>();
	private List<String> allRoleCodesList = new ArrayList<String>();
	private List<String> paramTableRoleCodeList = new ArrayList<String>();
	private List<SelectItem> rolecodeRquisitionerList = new ArrayList<SelectItem>();
	private List<SelectItem> rolecodeApproverList = new ArrayList<SelectItem>();
	private List<SelectItem> rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
	private List<SelectItem> identificationTypeList = new ArrayList<SelectItem>();
	private List<SelectItem> accumulatedPaidUpCptlList = new ArrayList<SelectItem>();
	private List<SelectItem> registrationStatusList = new ArrayList<SelectItem>();
	private List<String> simpleQuoteNoList = new ArrayList<String>();
	
	SCReportBackingBean scReportBackingBean;
	CMReportBackingBean cmReportBackingBean;
	FLReportBackingBean flReportBackingBean;
	SMReportBackingBean smReportBackingBean;
	CTReportBackingBean ctReportBackingBean;
	PPReportBackingBean ppReportBackingBean;
	PMReportBackingBean pmReportBackingBean;
	Long unspid = null;
	Long ministryId= null;
	
	private List<String> roleCodeList = new ArrayList<String>();
	private List<String> supplCompanyList = new ArrayList<String>();
	private boolean valueReqd;
	private boolean showParentMsg;
	private List<SmSupplierCategory> selectedCatList;
	private List<SmSupplierCategory> selectCatDeletionList;	
	private Map<String, List<SelectItem>> moduleItemMap=new HashMap<String, List<SelectItem>>();
	public List<SmSupplierCategory> getSelectCatDeletionList() {
		return selectCatDeletionList;
	}
	SelectItem[] items = null;
	SelectItem[] docTypeStatusItems = null;
	List<SelectItem> docTypeStatusItemsList=new ArrayList<SelectItem>();	
	public void setSelectCatDeletionList(
			List<SmSupplierCategory> selectCatDeletionList) {
		this.selectCatDeletionList = selectCatDeletionList;
	}
	private boolean selectedAll;
	private boolean selected;
	private List<PmCategoryL1> supplierParentCatList;
	private List<PmCategoryL2> supplierChildCatList;
	private List<PmCategoryL3> supplierSubChildCatList;
		

	public String getReportCategory() {
		return reportCategory;
	}

	public void setReportCategory(String reportCategory) {
		this.reportCategory = reportCategory;
	}	

	public List<SelectItem> getProcessingOfficerList() {
		return processingOfficerList;
	}

	public void setProcessingOfficerList(List<SelectItem> processingOfficerList) {
		this.processingOfficerList = processingOfficerList;
	}

	public List<SelectItem> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<SelectItem> zoneList) {
		this.zoneList = zoneList;
	}

	public List<String> getReportCategoryList() {
		return reportCategoryList;
	}

	public void setReportCategoryList(List<String> reportCategoryList) {
		this.reportCategoryList = reportCategoryList;
	}

	public List<String> getParamTableRoleCodeList() {
		return paramTableRoleCodeList;
	}

	public void setParamTableRoleCodeList(List<String> paramTableRoleCodeList) {
		this.paramTableRoleCodeList = paramTableRoleCodeList;
	}

	public List<SelectItem> getIdentificationTypeList() {
		return identificationTypeList;
	}

	public void setIdentificationTypeList(List<SelectItem> identificationTypeList) {
		this.identificationTypeList = identificationTypeList;
	}

	public List<SelectItem> getAccumulatedPaidUpCptlList() {
		return accumulatedPaidUpCptlList;
	}

	public void setAccumulatedPaidUpCptlList(
			List<SelectItem> accumulatedPaidUpCptlList) {
		this.accumulatedPaidUpCptlList = accumulatedPaidUpCptlList;
	}

	public List<SelectItem> getSmStateList() {
		return smStateList;
	}

	public void setSmStateList(List<SelectItem> smStateList) {
		this.smStateList = smStateList;
	}

	public Long getMinistryId() {
		return ministryId;
	}

	public void setMinistryId(Long ministryId) {
		this.ministryId = ministryId;
	}

	public List<SelectItem> getPmMinistryList() {
		return pmMinistryList;
	}

	public void setPmMinistryList(List<SelectItem> pmMinistryList) {
		this.pmMinistryList = pmMinistryList;
	}

	public List<String> getPmPtjList() {
		return pmPtjList;
	}

	public void setPmPtjList(List<String> pmPtjList) {
		this.pmPtjList = pmPtjList;
	}

	public List<SelectItem> getDocTypeStatusItemsList() {
		return docTypeStatusItemsList;
	}

	public void setDocTypeStatusItemsList(List<SelectItem> docTypeStatusItemsList) {
		this.docTypeStatusItemsList = docTypeStatusItemsList;
	}

	public SelectItem[] getDocTypeStatusItems() {
		return docTypeStatusItems;
	}

	public void setDocTypeStatusItems(SelectItem[] docTypeStatusItems) {
		this.docTypeStatusItems = docTypeStatusItems;
	}

	public List<SelectItem> getRolecodeRquisitionerList() {
		return rolecodeRquisitionerList;
	}

	public void setRolecodeRquisitionerList(
			List<SelectItem> rolecodeRquisitionerList) {
		this.rolecodeRquisitionerList = rolecodeRquisitionerList;
	}

	public List<SelectItem> getRolecodeApproverList() {
		return rolecodeApproverList;
	}

	public void setRolecodeApproverList(List<SelectItem> rolecodeApproverList) {
		this.rolecodeApproverList = rolecodeApproverList;
	}

	public List<SelectItem> getRolecodeRecievingOfficerList() {
		return rolecodeRecievingOfficerList;
	}

	public void setRolecodeRecievingOfficerList(
			List<SelectItem> rolecodeRecievingOfficerList) {
		this.rolecodeRecievingOfficerList = rolecodeRecievingOfficerList;
	}

	public List<String> getHighRoleCodeList() {
		return highRoleCodeList;
	}

	public void setHighRoleCodeList(List<String> highRoleCodeList) {
		this.highRoleCodeList = highRoleCodeList;
	}

	public List<String> getAllRoleCodesList() {
		return allRoleCodesList;
	}

	public void setAllRoleCodesList(List<String> allRoleCodesList) {
		this.allRoleCodesList = allRoleCodesList;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	public List<SelectItem> getCtSubTypeList() {
		return ctSubTypeList;
	}

	public void setCtSubTypeList(List<SelectItem> ctSubTypeList) {
		this.ctSubTypeList = ctSubTypeList;
	}

	public List<SelectItem> getSmUPSSVOUserList() {
		return smUPSSVOUserList;
	}

	public void setSmUPSSVOUserList(List<SelectItem> smUPSSVOUserList) {
		this.smUPSSVOUserList = smUPSSVOUserList;
	}

	public List<SelectItem> getScDescOfficerList() {
		return scDescOfficerList;
	}

	public void setScDescOfficerList(List<SelectItem> scDescOfficerList) {
		this.scDescOfficerList = scDescOfficerList;
	}

	public List<SelectItem> getScPtjList() {
		return scPtjList;
	}

	public void setScPtjList(List<SelectItem> scPtjList) {
		this.scPtjList = scPtjList;
	}

	public List<SelectItem> getScRequisitionerList() {
		return scRequisitionerList;
	}

	public void setScRequisitionerList(List<SelectItem> scRequisitionerList) {
		this.scRequisitionerList = scRequisitionerList;
	}

	public List<SelectItem> getApproverNayakTarafList() {
		return approverNayakTarafList;
	}

	public void setApproverNayakTarafList(List<SelectItem> approverNayakTarafList) {
		this.approverNayakTarafList = approverNayakTarafList;
	}

	public List<String> getRoleCodeList() {
		return roleCodeList;
	}

	public void setRoleCodeList(List<String> roleCodeList) {
		this.roleCodeList = roleCodeList;
	}

	public List<String> getSupplCompanyList() {
		return supplCompanyList;
	}

	public void setSupplCompanyList(List<String> supplCompanyList) {
		this.supplCompanyList = supplCompanyList;
	}

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

	public List<SelectItem> getRegistrationStatusList() {
		return registrationStatusList;
	}

	public void setRegistrationStatusList(List<SelectItem> registrationStatusList) {
		this.registrationStatusList = registrationStatusList;
	}
 
	
	public String getStringZone() {
		return stringZone;
	}

	public void setStringZone(String stringZone) {
		this.stringZone = stringZone;
	}

	public boolean isValueReqd() {
		return valueReqd;
	}

	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
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
	

	public PMReportBackingBean getPmReportBackingBean() {
		return pmReportBackingBean;
	}

	public void setPmReportBackingBean(PMReportBackingBean pmReportBackingBean) {
		this.pmReportBackingBean = pmReportBackingBean;
	}

	public PPReportBackingBean getPpReportBackingBean() {
		return ppReportBackingBean;
	}

	public void setPpReportBackingBean(PPReportBackingBean ppReportBackingBean) {
		this.ppReportBackingBean = ppReportBackingBean;
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
	
	public String getParameterCodeType() {
		return parameterCodeType;
	}

	public void setParameterCodeType(String parameterCodeType) {
		this.parameterCodeType = parameterCodeType;
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

	private List<String> addingStateList = new ArrayList<String>();
	private List<String> addingDistrictList = new ArrayList<String>();
	
	
	public List<String> getAddingStateList() {
		return addingStateList;
	}

	public void setAddingStateList(List<String> addingStateList) {
		this.addingStateList = addingStateList;
	}

	public List<String> getAddingDistrictList() {
		return addingDistrictList;
	}

	public void setAddingDistrictList(List<String> addingDistrictList) {
		this.addingDistrictList = addingDistrictList;
	}

	private String localToDateVar;

	private String userId;	
	private Long lngUserId = new FacesScopes().getUserSessionBean().getUserId();
	//private Long lngUserId = (long)219490;//183931;//20854;//243371;//20854;//168887;//221747;//219490;//243394;//243371;//243371;//219490;//148500;//103852;//46;//61745;//41747;//220152;//229703;//25;//45;//62114;//235459;
	private String roleCode;
	private Date fromDate;
	private Date toDate;
	private String subModule;
	private String commonPtj;
	private String commonMinistry;
	private String ministryName;
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
	private String scRequestNoteStatus;
	private String scSimpleQuoteStatus;
	private String scSimpleQuoteDtlGroup;
	private String scPurchaseInDefaultStatus;
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
	private String flContractNo;
	private String flSst;
	private String flJabatan;	
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
	private String flSupplierInvoiceNo;
	private String flDeductionType;
	private String flCrNo;
	private String flApproverName;
	private String flDocumentType;
	private String flCriteria;
	private String flScoring;
	private String flPkpNo;
	private String flDeliveryOrderNo;
	private String flSupplierDeliveryOrderNo;
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
	private String flGroupSeventeen;
	private boolean reportCategoryView = false;
	private String flStopInstructionNo;
	private String flPaymentAdviceNo;
	// sm
	private String smThresholdFromYear;
	private String smThresholdToYear;
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
	
	private String smCompanyName;
	private String smRegistrationType;
	private String smYearText;
	
	private String smMofRegNoFrom;
	private String smMofRegNoTo;
	private String smPaidUpCapitalFrm;
	private String smPaidUpCapitalTo;
	private String smPaidUpCapitalNaccum;
	private String smPaymentMethod;
	private String smProcessingType;
	private String smReceiptNo;
	private String smReportType;
	private String smProcessingOfficerName;
	
	private String smRole;
	private String smEpNo;
	private String smRegistrationNo;
	private String smSupportingDocumentMode;
	private String smSupportingDocumentStatus;
	private String smPersonnelRole;
	private String smIdentificationType;
	private String smIdentificationNumber;
	private String smCancellationBy;
	private String smFromMonth;
	private String smToMonth;
	private String smUserName;
	private String smRequestExpiryDate;
	private String smProfileStatus;
	private String smGSTRegistrationNo;
	private String smGSTRegisteredStatus;
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
	private String ctProcurementMode;	
	private String ctPhysicalContractNo;
	private String ctGroupThree;
	private String ctStatusTwo;
	private String ctLOAPhysicalNo;	
	private String ctUserType;
	private String ctSubType;
	// PP
	private String ppYear;
	private String ppMinistry;
	private String ppPTJ;	
	private long[] lngStateId;
	private long[] lngDistrictId;
	//PM
	private String pmServiceType;
	private String pmStatus;
	private String pmFinancialOrgName;
	private String pmBusinessRegNo;
	
	
	public String getPmStatus() {
		return pmStatus;
	}

	public void setPmStatus(String pmStatus) {
		this.pmStatus = pmStatus;
	}

	public String getPmFinancialOrgName() {
		return pmFinancialOrgName;
	}

	public void setPmFinancialOrgName(String pmFinancialOrgName) {
		this.pmFinancialOrgName = pmFinancialOrgName;
	}

	public String getPmBusinessRegNo() {
		return pmBusinessRegNo;
	}

	public void setPmBusinessRegNo(String pmBusinessRegNo) {
		this.pmBusinessRegNo = pmBusinessRegNo;
	}

	public String getPmServiceType() {
		return pmServiceType;
	}
	public void setPmServiceType(String pmServiceType) {
		this.pmServiceType = pmServiceType;
	}

	public String getFlPaymentAdviceNo() {
		return flPaymentAdviceNo;
	}
	public void setFlPaymentAdviceNo(String flPaymentAdviceNo) {
		this.flPaymentAdviceNo = flPaymentAdviceNo;
	}
	public String getFlSupplierDeliveryOrderNo() {
		return flSupplierDeliveryOrderNo;
	}
	public void setFlSupplierDeliveryOrderNo(String flSupplierDeliveryOrderNo) {
		this.flSupplierDeliveryOrderNo = flSupplierDeliveryOrderNo;
	}
	public String getSmGSTRegistrationNo() {
		return smGSTRegistrationNo;
	}
	public void setSmGSTRegistrationNo(String smGSTRegistrationNo) {
		this.smGSTRegistrationNo = smGSTRegistrationNo;
	}
	public String getSmGSTRegisteredStatus() {
		return smGSTRegisteredStatus;
	}
	public void setSmGSTRegisteredStatus(String smGSTRegisteredStatus) {
		this.smGSTRegisteredStatus = smGSTRegisteredStatus;
	}
	public String getSmProfileStatus() {
		return smProfileStatus;
	}
	public void setSmProfileStatus(String smProfileStatus) {
		this.smProfileStatus = smProfileStatus;
	}
	public String getFlSupplierInvoiceNo() {
		return flSupplierInvoiceNo;
	}
	public void setFlSupplierInvoiceNo(String flSupplierInvoiceNo) {
		this.flSupplierInvoiceNo = flSupplierInvoiceNo;
	}
	public String getFlStopInstructionNo() {
		return flStopInstructionNo;
	}
	public void setFlStopInstructionNo(String flStopInstructionNo) {
		this.flStopInstructionNo = flStopInstructionNo;
	}
	public String getSmRequestExpiryDate() {
		return smRequestExpiryDate;
	}
	public void setSmRequestExpiryDate(String smRequestExpiryDate) {
		this.smRequestExpiryDate = smRequestExpiryDate;
	}
	public String getSmUserName() {
		return smUserName;
	}
	public void setSmUserName(String smUserName) {
		this.smUserName = smUserName;
	}
	public String getSmThresholdFromYear() {
		return smThresholdFromYear;
	}
	public void setSmThresholdFromYear(String smThresholdFromYear) {
		this.smThresholdFromYear = smThresholdFromYear;
	}
	public String getSmThresholdToYear() {
		return smThresholdToYear;
	}
	public void setSmThresholdToYear(String smThresholdToYear) {
		this.smThresholdToYear = smThresholdToYear;
	}
	public String getSmFromMonth() {
		return smFromMonth;
	}
	public void setSmFromMonth(String smFromMonth) {
		this.smFromMonth = smFromMonth;
	}
	public String getSmToMonth() {
		return smToMonth;
	}
	public void setSmToMonth(String smToMonth) {
		this.smToMonth = smToMonth;
	}
	public boolean isReportCategoryView() {
		return reportCategoryView;
	}
	public void setReportCategoryView(boolean reportCategoryView) {
		this.reportCategoryView = reportCategoryView;
	}
	public String getMinistryName() {
		return ministryName;
	}
	public void setMinistryName(String ministryName) {
		this.ministryName = ministryName;
	}
	public String getSmPaymentMethod() {
		return smPaymentMethod;
	}
	public void setSmPaymentMethod(String smPaymentMethod) {
		this.smPaymentMethod = smPaymentMethod;
	}
	public String getSmProcessingType() {
		return smProcessingType;
	}
	public void setSmProcessingType(String smProcessingType) {
		this.smProcessingType = smProcessingType;
	}
	public String getSmReceiptNo() {
		return smReceiptNo;
	}
	public void setSmReceiptNo(String smReceiptNo) {
		this.smReceiptNo = smReceiptNo;
	}
	public String getSmProcessingOfficerName() {
		return smProcessingOfficerName;
	}
	public void setSmProcessingOfficerName(String smProcessingOfficerName) {
		this.smProcessingOfficerName = smProcessingOfficerName;
	}
	public String getSmIdentificationType() {
		return smIdentificationType;
	}
	public void setSmIdentificationType(String smIdentificationType) {
		this.smIdentificationType = smIdentificationType;
	}
	public String getSmIdentificationNumber() {
		return smIdentificationNumber;
	}
	public void setSmIdentificationNumber(String smIdentificationNumber) {
		this.smIdentificationNumber = smIdentificationNumber;
	}
	public String getSmCancellationBy() {
		return smCancellationBy;
	}
	public void setSmCancellationBy(String smCancellationBy) {
		this.smCancellationBy = smCancellationBy;
	}
	public String getSmPersonnelRole() {
		return smPersonnelRole;
	}
	public void setSmPersonnelRole(String smPersonnelRole) {
		this.smPersonnelRole = smPersonnelRole;
	}
	public String getSmMofRegNoFrom() {
		return smMofRegNoFrom;
	}
	public void setSmMofRegNoFrom(String smMofRegNoFrom) {
		this.smMofRegNoFrom = smMofRegNoFrom;
	}
	public String getSmMofRegNoTo() {
		return smMofRegNoTo;
	}
	public void setSmMofRegNoTo(String smMofRegNoTo) {
		this.smMofRegNoTo = smMofRegNoTo;
	}
	public String getSmPaidUpCapitalFrm() {
		return smPaidUpCapitalFrm;
	}
	public void setSmPaidUpCapitalFrm(String smPaidUpCapitalFrm) {
		this.smPaidUpCapitalFrm = smPaidUpCapitalFrm;
	}

	public String getSmPaidUpCapitalTo() {
		return smPaidUpCapitalTo;
	}

	public void setSmPaidUpCapitalTo(String smPaidUpCapitalTo) {
		this.smPaidUpCapitalTo = smPaidUpCapitalTo;
	}

	public String getSmPaidUpCapitalNaccum() {
		return smPaidUpCapitalNaccum;
	}

	public void setSmPaidUpCapitalNaccum(String smPaidUpCapitalNaccum) {
		this.smPaidUpCapitalNaccum = smPaidUpCapitalNaccum;
	}

	public String getSmRole() {
		return smRole;
	}

	public void setSmRole(String smRole) {
		this.smRole = smRole;
	}

	public String getSmEpNo() {
		return smEpNo;
	}

	public void setSmEpNo(String smEpNo) {
		this.smEpNo = smEpNo;
	}

	public String getSmRegistrationNo() {
		return smRegistrationNo;
	}

	public void setSmRegistrationNo(String smRegistrationNo) {
		this.smRegistrationNo = smRegistrationNo;
	}

	public String getSmSupportingDocumentMode() {
		return smSupportingDocumentMode;
	}

	public void setSmSupportingDocumentMode(String smSupportingDocumentMode) {
		this.smSupportingDocumentMode = smSupportingDocumentMode;
	}

	public String getSmSupportingDocumentStatus() {
		return smSupportingDocumentStatus;
	}

	public void setSmSupportingDocumentStatus(String smSupportingDocumentStatus) {
		this.smSupportingDocumentStatus = smSupportingDocumentStatus;
	}

	public long[] getLngDistrictId() {
		return lngDistrictId;
	}

	public void setLngDistrictId(long[] lngDistrictId) {
		this.lngDistrictId = lngDistrictId;
	}

	public long[] getLngStateId() {
		return lngStateId;
	}

	public void setLngStateId(long[] lngStateId) {
		this.lngStateId = lngStateId;
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

	public String getScPurchaseInDefaultStatus() {
		return scPurchaseInDefaultStatus;
	}

	public void setScPurchaseInDefaultStatus(String scPurchaseInDefaultStatus) {
		this.scPurchaseInDefaultStatus = scPurchaseInDefaultStatus;
	}

	public String getScSimpleQuoteDtlGroup() {
		return scSimpleQuoteDtlGroup;
	}

	public void setScSimpleQuoteDtlGroup(String scSimpleQuoteDtlGroup) {
		this.scSimpleQuoteDtlGroup = scSimpleQuoteDtlGroup;
	}

	public String getScSimpleQuoteStatus() {
		return scSimpleQuoteStatus;
	}

	public void setScSimpleQuoteStatus(String scSimpleQuoteStatus) {
		this.scSimpleQuoteStatus = scSimpleQuoteStatus;
	}

	public SelectItem[] getItems() {
		return items;
	}

	public void setItems(SelectItem[] items) {
		this.items = items;
	}

	public String getScRequestNoteStatus() {
		return scRequestNoteStatus;
	}

	public void setScRequestNoteStatus(String scRequestNoteStatus) {
		this.scRequestNoteStatus = scRequestNoteStatus;
	}

	public String getCtSubType() {
		return ctSubType;
	}

	public void setCtSubType(String ctSubType) {
		this.ctSubType = ctSubType;
	}

	public String getCtUserType() {
		return ctUserType;
	}

	public void setCtUserType(String ctUserType) {
		this.ctUserType = ctUserType;
	}

	public String getCtLOAPhysicalNo() {
		return ctLOAPhysicalNo;
	}

	public void setCtLOAPhysicalNo(String ctLOAPhysicalNo) {
		this.ctLOAPhysicalNo = ctLOAPhysicalNo;
	}

	public String getCtStatusTwo() {
		return ctStatusTwo;
	}

	public void setCtStatusTwo(String ctStatusTwo) {
		this.ctStatusTwo = ctStatusTwo;
	}

	public String getCtGroupThree() {
		return ctGroupThree;
	}

	public void setCtGroupThree(String ctGroupThree) {
		this.ctGroupThree = ctGroupThree;
	}

	public String getCtPhysicalContractNo() {
		return ctPhysicalContractNo;
	}

	public void setCtPhysicalContractNo(String ctPhysicalContractNo) {
		this.ctPhysicalContractNo = ctPhysicalContractNo;
	}

	public String getFlGroupSeventeen() {
		return flGroupSeventeen;
	}

	public void setFlGroupSeventeen(String flGroupSeventeen) {
		this.flGroupSeventeen = flGroupSeventeen;
	}

	public String getCtProcurementMode() {
		return ctProcurementMode;
	}

	public void setCtProcurementMode(String ctProcurementMode) {
		this.ctProcurementMode = ctProcurementMode;
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

	

	public Long getLngUserId() {
		return lngUserId;
	}

	public void setLngUserId(Long lngUserId) {
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
	

	public String getCommonPtj() {
		return commonPtj;
	}

	public void setCommonPtj(String commonPtj) {
		this.commonPtj = commonPtj;
	}

	public String getCommonMinistry() {
		return commonMinistry;
	}

	public void setCommonMinistry(String commonMinistry) {
		this.commonMinistry = commonMinistry;
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
	public String getSmRegistrationStatus() {
		return smRegistrationStatus;
	}

	public void setSmRegistrationStatus(String smRegistrationStatus) {
		this.smRegistrationStatus = smRegistrationStatus;
	}

	public String getSmcategory() {
		return smcategory;
	}

	public void setSmcategory(String smcategory) {
		this.smcategory = smcategory;
	}

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
	public String getSmApplicationStatus() {
		return smApplicationStatus;
	}

	public void setSmApplicationStatus(String smApplicationStatus) {
		this.smApplicationStatus = smApplicationStatus;
	}
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
	public String getSmReportType() {
		return smReportType;
	}

	public void setSmReportType(String smReportType) {
		this.smReportType = smReportType;
	}

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
	public boolean isEnableValue() {
		return enableValue;
	}

	public void setEnableValue(boolean enableValue) {
		this.enableValue = enableValue;
	}
	
	public void showCapitalNaccumShare(){
		getManagedBean().getRptGenBean().setSmPaidUpCapitalFrmView(false);
		if ((null != this.getReportValue())&&(this.getReportValue().equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {			
			if ((null != this.getSmPaidUpCapitalNaccum())&&(ReportConst.EMPTY_VAL !=this.getSmApplicationStatus())) {
				getManagedBean().getRptGenBean().setSmPaidUpCapitalFrmView(true);				
			}
		}
	}
	public List<SelectItem> retrieveProcessingOfficerList(){
		processingOfficerList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			if ((null != this.getSmZone())&& (ReportConst.EMPTY_VAL != this.getSmZone())) {
					List<Object[]> regStatusList = reportSO.retrieveProcessingOfficerList(this.getSmZone());			
					for (Object[] object : regStatusList) {
						SelectItem selectItem = new SelectItem();						
						selectItem.setValue(object[1].toString());
						selectItem.setLabel(object[0].toString());
						processingOfficerList.add(selectItem);
					}
			}
		}
		return processingOfficerList;
	}
	public void applicationStatusDateChange(){
		
		getManagedBean().getRptGenBean().setSmMandatoryExpectedSiteVisitDateView(false);
		getManagedBean().getRptGenBean().setSmMandatoryActualSiteVisitDateView(false);
		//getManagedBean().getRptGenBean().setSmMandatoryFromDateView(false);
		if ((null != this.getReportValue())&&(this.getReportValue().equals(ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL))) {			
			if ((null != this.getSmApplicationStatus())&&(this.getSmApplicationStatus().equals(ReportConst.PEND_PRPR_SITE_VISIT))) {
				getManagedBean().getRptGenBean().setSmMandatoryFromDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryExpectedSiteVisitDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryDateView(false);
			}else if ((null != this.getSmApplicationStatus())&&(this.getSmApplicationStatus().equals(ReportConst.SITE_VISIT_COMPLTD))) {
				getManagedBean().getRptGenBean().setSmMandatoryFromDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryActualSiteVisitDateView(true);
				getManagedBean().getRptGenBean().setSmMandatoryDateView(false);
			}/*else{
				getManagedBean().getRptGenBean().setSmMandatoryDateView(true);
			}*/
		}
	}
	
	public void changeReportType(){
		
		if ((null != this.getReportValue())&&(this.getReportValue().equals(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
			getManagedBean().getRptGenBean().setSmMandatoryCreatedFromYearView(false);
			getManagedBean().getRptGenBean().setSmMandatoryRegisteredReportTypeDateView(false);
			getManagedBean().getRptGenBean().setSmMandatoryRecievedReportTypeView(false);
			getManagedBean().getRptGenBean().setSmMandatoryApprovedReportTypeView(false);
			getManagedBean().getRptGenBean().setSmMandatoryRejectedReportTypeView(false);
			if (null != this.getSmReportType()&&this.getSmReportType().equals("RECEIVED")) {
				getManagedBean().getRptGenBean().setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean().setSmMandatoryRecievedReportTypeView(true);
			} else if (null != this.getSmReportType()&&this.getSmReportType().equals("APPROVED")) {
				getManagedBean().getRptGenBean().setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean().setSmMandatoryApprovedReportTypeView(true);
			} else if (null != this.getSmReportType()&&this.getSmReportType().equals("REJECTED")) {
				getManagedBean().getRptGenBean().setSmMandatoryCreatedFromYearView(true);
				getManagedBean().getRptGenBean().setSmMandatoryRejectedReportTypeView(true);
			} /*else {
			    rptGenBean.setSmMandatoryCreatedFromYearView(true);
				rptGenBean.setSmMandatoryRegisteredReportTypeDateView(true);
			}*/
		}
	}
	public String retrieveMinistryName(Long orgprofileid){
		ministryName =null;
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> ministryNameLst = reportSO.retrieveMinistryNameByOrgprofileid(orgprofileid);			
					if (ministryNameLst.size()>0) {
					
						ministryName=ministryNameLst.get(0);
					}        	    
        }
		return ministryName;
	}
	public List<SelectItem> retrieveRegStatusList(){
		registrationStatusList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			
					List<Object[]> regStatusList = reportSO.retrieveRegStatusList("en");			
					for (Object[] object : regStatusList) {
						SelectItem selectItem = new SelectItem();						
						selectItem.setValue(object[1].toString());
						selectItem.setLabel(object[0].toString());
						registrationStatusList.add(selectItem);
					}
			}
		return registrationStatusList;
	}
	public void supplierTypeConsultant(){
		getManagedBean().getRptGenBean().setSmConsultantRegistrationStatusView(false);
		if((null!=this.getReportValue())&&(this.getReportValue()
				.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))){
			if((null!=this.getSmSupplierType())&&(this.getSmSupplierType()
					.equalsIgnoreCase(ReportConst.SUPPL_CONSLT_TYPE))){
				getManagedBean().getRptGenBean().setSmConsultantRegistrationStatusView(true);
				getManagedBean().getRptGenBean().setSmRegistrationStatusView(false);
			}else{
				getManagedBean().getRptGenBean().setSmConsultantRegistrationStatusView(false);
				getManagedBean().getRptGenBean().setSmRegistrationStatusView(true);
			}
		}
	}
	public List<SelectItem> getAllModuleList(){
		 moduleList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			
					List<Object[]> moduleLst = reportSO.retrieveModuleList("en",lngUserId);			
					for (Object[] object : moduleLst) {
						SelectItem selectItem = new SelectItem();						
						selectItem.setValue(object[0].toString());
						selectItem.setLabel(object[1].toString());
						moduleList.add(selectItem);
					}
			}
		return moduleList;
	 }
	public void retrieveIdentificationType(){
		identificationTypeList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			
					List<Object[]> identificationTypeLst = reportSO.retrieveIdentificationType();			
					for (Object[] object : identificationTypeLst) {
						SelectItem selectItem = new SelectItem();						
						selectItem.setValue(object[0].toString());
						selectItem.setLabel(object[1].toString());
						identificationTypeList.add(selectItem);
					}
			}
	 }
	public void retrieveAccumulatedPaidUpCptl(){
		accumulatedPaidUpCptlList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			
					List<Object[]> accumulatedPaidUpCptlLst = reportSO.retrieveAccumulatedPaidUpCptl();			
					for (Object[] object : accumulatedPaidUpCptlLst) {
						SelectItem selectItem = new SelectItem();						
						selectItem.setValue(object[0].toString());
						selectItem.setLabel(object[1].toString());
						accumulatedPaidUpCptlList.add(selectItem);
					}
			}
	 }
	public SelectItem[] getLtstMinistryList() {
		SelectItem[] items = null;

		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			items = new SelectItem[resDTO.getOrgProfileList().size() + 1];
			//items[0] = new SelectItem(null, "- Select One -");
			//int i = 0;
			SelectItem selectItem = new SelectItem();
			selectItem.setLabel(ResourceBundleUtils.getMessage("lbl.txt.select", 
		 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
			selectItem.setValue(null);
			ministryList.add(selectItem);
			for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(orgProfile.getOrgName());
				selectItem1.setValue(orgProfile.getOrgProfileId());
				ministryList.add(selectItem1);
				/*items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());*/
			}			
		} finally {
			orgProfileSO = null;
		}
		return items;
	}
	public SelectItem[] getLtstMinistryListByUserid() {
		SelectItem[] items = null;

		IOrgProfileSO orgProfileSO = null;
		try {
			orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);

			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setUserId(lngUserId);
			reqDTO.setOrgType(ParameterConst.ORG_TYPE_MINISTRY);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);

			items = new SelectItem[resDTO.getOrgProfileList().size() + 1];
			//items[0] = new SelectItem(null, "- Select One -");
			//int i = 0;
			/*SelectItem selectItem = new SelectItem();
			selectItem.setLabel("- Select One -");
			selectItem.setValue(null);
			ministryList.add(selectItem);*/
			for (PmOrgProfile orgProfile : resDTO.getOrgProfileList()) {
				SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(orgProfile.getOrgName());
				selectItem1.setValue(orgProfile.getOrgProfileId());
				ministryList.add(selectItem1);
				/*items[i++] = new SelectItem(orgProfile.getOrgProfileId(),
						orgProfile.getOrgName());*/
			}			
		} finally {
			orgProfileSO = null;
		}
		return items;
	}
	
	public void retrievePtjListByMinistryId() {		
		pmPtjList =new ArrayList<String>();		
		if(ministryId!=null){
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			reqDTO.setParentOrgProfileId(ministryId);
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrieveOrgProfileListByOrgType(reqDTO);
			List<PmOrgProfile> orgProfileList =resDTO.getOrgProfileList();
			for (PmOrgProfile ptj : orgProfileList) {
				ptj.getOrgName();
				pmPtjList.add(ptj.getOrgName());
			}
			/*reqDTO.setParentOrgProfileId(ministryId);
			//reqDTO.setParentOrgProfileId((long) 231618);
			OrgProfileResDTO resDTO = orgProfileSO
					.retrievePtjListByMinistryId(reqDTO);
			List<PmOrgProfile> orgProfileList =resDTO.getOrgProfileList();
			for (PmOrgProfile ptj : orgProfileList) {
				ptj.getOrgName();
			}*/
		}
	}
	
	public SelectItem[] getDocTypeStatusDropDown(){
		docTypeStatusItems = null;
		String doctype = null;
		SelectItem[] tempOneStatusItems = null;
		SelectItem[] tempTwoStatusItems = null;
		docTypeStatusItemsList=new ArrayList<SelectItem>();
		if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_REQ_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_REQ_RPT_CEN_PEN_VAL))){
			doctype =ReportConst.DOC_TYPE_CR;
		} else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_ORD_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_ORD_RPT_CEN_PEN_VAL))){
			doctype =ReportConst.DOC_TYPE_CO;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.DEL_ORD_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.DEL_ORD_RPT_CEN_PEN_VAL))){
			doctype =ReportConst.DOC_TYPE_DO;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL))){
			doctype =ReportConst.DOC_TYPE_FN;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.INV_RPT_VIEW_SUPL_MIN_VAL)||this.getReportValue().equals(ReportConst.INV_RPT_VIEW_PTJ_MIN_VAL)||this.getReportValue().equals(ReportConst.INV_RPT_VIEW_SUPL_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.INV_RPT_VIEW_PTJ_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.INV_RPT_VIEW_SUPL_PO_VAL)||this.getReportValue().equals(ReportConst.INV_RPT_VIEW_PTJ_PO_VAL))){
			doctype =ReportConst.DOC_TYPE_IN;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.PYMT_MATCH_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.PYMT_MATCH_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PYMT_MATCH_RPT_PO_VAL))){
			doctype =ReportConst.DOC_TYPE_PA;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.DED_RPT_MIN_VAL))){
			doctype =ReportConst.DOC_TYPE_DN;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SUMM_RPT_PAN_ITM_VAL)||this.getReportValue().equals(ReportConst.PUR_ORD_RPT_PO_VAL))){
			doctype =ReportConst.DOC_TYPE_PO;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.PUR_REQ_RPT_PO_VAL))){
			doctype =ReportConst.DOC_TYPE_PR;
		}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))){	
			docTypeStatusItemsList=new ArrayList<SelectItem>();
			tempOneStatusItems = getStatusDropdownValues(this.getModuleValue(),ReportConst.DOC_TYPE_PO);
			tempTwoStatusItems = getStatusDropdownValues(this.getModuleValue(),ReportConst.DOC_TYPE_PR);
			for(SelectItem item1:tempOneStatusItems){				
					docTypeStatusItemsList.add(item1);				
			}
			for(SelectItem item2:tempTwoStatusItems){
				if((!item2.getLabel().equals(ResourceBundleUtils.getMessage("lbl.txt.select", 
			 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())))){						
							docTypeStatusItemsList.add(item2);					
				}
			}
		}		
		if(null!=doctype){
			docTypeStatusItems = null;
			docTypeStatusItemsList=new ArrayList<SelectItem>();
			docTypeStatusItems = getStatusDropdownValues(this.getModuleValue(),doctype);
			for(SelectItem itemCommon:docTypeStatusItems){
				docTypeStatusItemsList.add(itemCommon);
			}
		}
		return docTypeStatusItems;
    }
	public List<SelectItem> retrieveSubTypeList(){
		ctSubTypeList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
        	if (null != this.getCtUserType()) {
        		String userTypeRoleCode = null;
        		if (this.getCtUserType().equals(ReportConst.MINISTRY_CONTRACT_ADMIN)) {
        			userTypeRoleCode=ReportConst.MIN_CONTRACT_ADMIN;
        		}else if(this.getCtUserType().equals(ReportConst.APPROVER_LBL)) {
        			userTypeRoleCode=ReportConst.ROLE_CODE_CT_APPROVER;
        		}
        		if (null != userTypeRoleCode) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<Object[]> ptLst = reportSO.retrieveSubTypeList(lngUserId,userTypeRoleCode);			
					for (Object[] object : ptLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						ctSubTypeList.add(selectItem);
					}
        	    }
        	}
        }
		return ctSubTypeList;
	}
	public List<SelectItem> retrieveZoneByUser(){
		zoneList = new ArrayList<SelectItem>();
		this.setStringZone(null);
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
        		
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<Object[]> zoneLst = reportSO.retrieveZoneByUser(lngUserId);			
					for (Object[] object : zoneLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setValue(object[0]);
						selectItem.setLabel(object[1].toString());
						stringZone =object[1].toString();
						zoneList.add(selectItem);
					}
        }
		return zoneList;
	}
	public List<String> retrieveHighRoleCodeByUser(){
		highRoleCodeList = new ArrayList<String>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> ptLst = reportSO.retrieveHighRoleCodeByUser(lngUserId);			
					for (int i = 0; i < ptLst.size(); i++) {
						highRoleCodeList.add(ptLst.get(i));
					}        	    
        }
		return highRoleCodeList;
	}
	public void  roleCodeUsersSetupOnload(Long lngPtjId){
		rolecodeRquisitionerList = new ArrayList<SelectItem>();
		rolecodeApproverList = new ArrayList<SelectItem>();
		rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			 if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER))){
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER);			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}				
			}else{
				if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_REQ_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_REQ_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_REQ_RPT_PO_VAL)||this.getReportValue().equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))){					
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRquisitionerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRecievingOfficerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_ORD_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_ORD_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_ORD_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_FL_APPROVER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeApproverList.add(selectItem);
					}
				}
			}
			if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_APPROVER)||highRoleCodeList.contains(ReportConst.ROLE_CODE_CT_APPROVER)||highRoleCodeList.contains(ReportConst.ROLE_CODE_RN_APPROVER))){
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,highRoleCodeList.get(0));			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeApproverList.add(selectItem);
				}				
			}
			if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER))){
				List<Object[]> recoffLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER);			
				for (Object[] object : recoffLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRecievingOfficerList.add(selectItem);
				}
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER);			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			}
		}
	} 
	public void  roleCodeUsersSetup(){
		rolecodeRquisitionerList = new ArrayList<SelectItem>();
		rolecodeApproverList = new ArrayList<SelectItem>();
		rolecodeRecievingOfficerList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
			/*if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_MIN_CONTRACT_ADMIN)||highRoleCodeList.contains(ReportConst.ROLE_CODE_FL_APPROVER)||highRoleCodeList.contains(ReportConst.ROLE_CODE_BPK_CCA)||highRoleCodeList.contains(ReportConst.ROLE_CODE_BPK_PCA))){
				//List<String> ptLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId);
				if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_REQ_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_REQ_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_REQ_RPT_PO_VAL)||this.getReportValue().equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRquisitionerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRecievingOfficerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_ORD_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_ORD_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_ORD_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_FL_APPROVER);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeApproverList.add(selectItem);
					}
				}
			}else*/ if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_REQUISITIONER))){
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER);			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}				
			}else{
				Long lngPtjId = null;
				if(this.getCommonPtj()!=null){
					lngPtjId = Long.parseLong(this.getCommonPtj());
				}			
				//List<String> ptLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId);
				if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_REQ_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_REQ_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_REQ_RPT_PO_VAL)||this.getReportValue().equals(ReportConst.EMR_PROCUR_RPT_PO_VAL))){					
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRquisitionerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.LATE_DEL_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.FL_REC_NOTE_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeRecievingOfficerList.add(selectItem);
					}
				}else if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.CON_ORD_RPT_MIN_VAL)||this.getReportValue().equals(ReportConst.CON_ORD_RPT_CEN_PEN_VAL)||this.getReportValue().equals(ReportConst.PUR_ORD_RPT_PO_VAL))){
					List<Object[]> rlcdLst = reportSO.retrieveUserListByAdminOrBKPRoleCode(lngUserId,ReportConst.ROLE_CODE_FL_APPROVER,lngPtjId);			
					for (Object[] object : rlcdLst) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1]);
						rolecodeApproverList.add(selectItem);
					}
				}
			}
			if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_APPROVER)||highRoleCodeList.contains(ReportConst.ROLE_CODE_CT_APPROVER)||highRoleCodeList.contains(ReportConst.ROLE_CODE_RN_APPROVER))){
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,highRoleCodeList.get(0));			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeApproverList.add(selectItem);
				}				
			}
			if((highRoleCodeList!=null)&&(highRoleCodeList.contains(ReportConst.ROLE_CODE_RECEIVING_OFFICER))){
				List<Object[]> recoffLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_RECEIVING_OFFICER);			
				for (Object[] object : recoffLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRecievingOfficerList.add(selectItem);
				}
				List<Object[]> rlcdLst = reportSO.retrieveUserListByREQUISITIONER(lngUserId,ReportConst.ROLE_CODE_REQUISITIONER);			
				for (Object[] object : rlcdLst) {
					SelectItem selectItem = new SelectItem();
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1]);
					rolecodeRquisitionerList.add(selectItem);
				}
			}
		}
	}
	public List<String> retrieveAllRoleCodesByUser(){
		allRoleCodesList = new ArrayList<String>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> roleCodesLst = reportSO.retrieveAllRoleCodesByUser(lngUserId);			
					for (int i = 0; i < roleCodesLst.size(); i++) {
						allRoleCodesList.add(roleCodesLst.get(i));
					}        	    
        }
		return allRoleCodesList;
	}
	
	public void  zoneByRolecode(){
		if ((this.getModuleValue().equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
			getManagedBean().getRptGenBean().setSmZoneLabelView(false);
			getManagedBean().getRptGenBean().setSmZoneView(false);
			getManagedBean().getRptGenBean().setSmMandatoryZoneView(false);
			if((null!=this.getReportValue())&&(this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SUPPL_STAT_BY_CATEGORY_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_SUPPL_STAT_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_QUESTIONAIRE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_DET_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_CONT_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_DET_CONT_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_REG_COMPANY_IN_MOF_PROJ_CONT_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_QUESTIONNAIRE_RESPONSE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SITE_VISIT_APPL_REQUIRE_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_SITE_VISIT_APPL_OFFICER_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SITE_VISIT_APPL_PERFORMED_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_APPLICATION_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_LIST_OFFLINE_SUPP_DOC_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_STATISTICS_COMPANYTYPE_CAP_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_MOF_CANCELLATION_RPT_VAL)
					||this.getReportValue().equals(ReportConst.SM_BPK_USER_PROFILE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_BPK_ADMIN_PROFILE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_DELETE_PENDING_APPL)
                    ||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK)||this.getReportValue().equals(ReportConst.SM_THRESHOLD)||this.getReportValue().equals(ReportConst.SM_LST_MONTH_WISE_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_GST_RGSTRD_SUPPLIER))){				
			
				if((allRoleCodesList!=null)&&(allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_PO)||allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_APPROVER)||allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_ADMIN)||allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_SVO)||allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_SVC)||allRoleCodesList.contains(ReportConst.ROLE_CODE_UPS_SDO))){
					//if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK))){				
						this.setSmZone(this.getStringZone());
						getManagedBean().getRptGenBean().setSmZoneLabelView(true);
					//}
					
				}else if((allRoleCodesList!=null)&&(allRoleCodesList.contains(ReportConst.ROLE_CODE_EP_ADMIN)||allRoleCodesList.contains(ReportConst.ROLE_CODE_PTJ_ADMIN))){
					//if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK))){				
						getManagedBean().getRptGenBean().setSmZoneView(true);
						getManagedBean().getRptGenBean().setSmMandatoryZoneView(true);
					//}
					
				}else if((allRoleCodesList!=null)&&(allRoleCodesList.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_ADMIN)||allRoleCodesList.contains(ReportConst.ROLE_CODE_MOF_SUPPLIER_USER)||allRoleCodesList.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_USER)||allRoleCodesList.contains(ReportConst.ROLE_CODE_BASIC_SUPPLIER_ADMIN))){
					if((this.getReportValue()!=null)&&(this.getReportValue().equals(ReportConst.SM_APPL_CLIENT_CHARTER_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_PERSON_BRANCH_BANK))){
						rptGenBean.setSmMandatorySupplierTypeView(false);
						rptGenBean.setSmSupplierTypeView(false);
					}
				}
			}	
		}
	}
	public List<String> retrieveParamCodeByUser(){
		paramList = new ArrayList<String>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> ptLst = reportSO.retrieveParamCodeByUser(lngUserId);			
					for (int i = 0; i < ptLst.size(); i++) {
						paramList.add(ptLst.get(i));
					}        	    
        }
		return paramList;
	}
	public void assignNewCompanyExperience(List<SmSupplierCategory> catList) {		
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
		//CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();		
		ICategoryCodeSO reqSO = (ICategoryCodeSO) BusinessDelegate
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ, ICategoryCodeSO.class);
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
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ, ICategoryCodeSO.class);
		CategoryCodeReqDTO reqDTO = new CategoryCodeReqDTO();
		reqDTO.setCategoryL1Id(categoryL1Id);
		CategoryCodeResDTO resDTO = reqSO.retrieveCategoryL2ListByL1Id(reqDTO);
		return resDTO.getCategoryL2List();
	}
	
	public List<SelectItem>  retrieveReportListByAuthorization() {
		reportList = new ArrayList<SelectItem>();
		rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);		
		this.setReportName(null);
		setRoleCode(null);
		
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		
		
		rptGenBean = new ReportsGenerateBean();
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		rptGenBean.setReportCategoryView(false);
		this.setReportCategory(null);
		this.setReportValue(null);
		reportList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {			
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					if((this.getModuleValue()!=null)&&(this.getModuleValue().equalsIgnoreCase("FL"))){
						rptGenBean.setReportCategoryView(true);
						retrieveSubModuleReportCategoryList();
					}else{						
						List<Object[]> reportModList = reportSO.retrieveReportListByModuleNUserAuthorization(lngUserId,this.getModuleValue());
						for (Object[] object : reportModList) {
							SelectItem selectItem = new SelectItem();
							selectItem.setLabel(object[0].toString());
							selectItem.setValue(object[1].toString());
							reportList.add(selectItem);
						}
					}					
				}
				return reportList;	
			}
	
	public List<String>  retrieveSubModuleReportCategoryList() {
		reportList = new ArrayList<SelectItem>();	
		reportCategoryList = new ArrayList<String>();	
		this.setReportName(null);
		setRoleCode(null);		
		getManagedBean().setReportList(new ArrayList<SelectItem>());		
		resetReportForm();
		this.setReportValue(null);		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {			
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> reportCatList = reportSO.retrieveSubModuleReportCategoryList(this.getModuleValue(), lngUserId);
					for (int i = 0; i < reportCatList.size(); i++) {
						reportCategoryList.add(reportCatList.get(i));
					}
				}
				return reportCategoryList;	
		}
		public List<String>  retrieveSimpleQuoteNo() {
			
			simpleQuoteNoList = new ArrayList<String>();	
					
			if (null != new FacesScopes().getUserSessionBean().getUserId()) {			
						IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
								.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
										IPMReportSO.class);
						List<String> smplQtNoList = reportSO.retrieveSimpleQuoteNo(this.getScProcessRptQuotationNo());
						if(smplQtNoList.size()>0){
							for (int i = 0; i < smplQtNoList.size(); i++) {
								simpleQuoteNoList.add(smplQtNoList.get(i));
							}
						}else{
							this.setScProcessRptQuotationNo(null);
							throw new EpException(MessageConst.ERROR_REPORT_780021);
						}
					}
					return simpleQuoteNoList;	
			}
	public List<SelectItem>  retrieveReportListBySubModule() {
		reportList = new ArrayList<SelectItem>();			
		this.setReportName(null);
		setRoleCode(null);		
		getManagedBean().setReportList(new ArrayList<SelectItem>());		
		resetReportForm();
		this.setReportValue(null);
		if(rptGenBean.isReportCategoryView()==true||(this.getModuleValue()
				.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)&&this.getReportCategory()!=null)){
			rptGenBean = new ReportsGenerateBean();
			rptGenBean.setReportCategoryView(true);
			//this.setReportCategoryView(true);
		}else{
		rptGenBean = new ReportsGenerateBean();
		//rptGenBean.setReportCategoryView(this.isReportCategoryView());
		}
		getManagedBean().setRptGenBean(null);
		this.setSubModule(null);
		this.setReportName(ReportConst.EMPTY_VAL);
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {			
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<Object[]> reportModList = reportSO.retrieveReportListBySubModule(this.getModuleValue(),this.getReportCategory(),lngUserId);
					for (Object[] object : reportModList) {
						SelectItem selectItem = new SelectItem();
						selectItem.setLabel(object[0].toString());
						selectItem.setValue(object[1].toString());
						reportList.add(selectItem);
					}
				}
				return reportList;	
		}
	
    public void getReportIdlist() {
    	rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
    	String paramModule = null;
    	items = null; 
    	if (null != new FacesScopes().getUserSessionBean().getUserId()) {
	    	if ((this.getModuleValue()) != null){
	    		paramModule="REP";
	    	}
	    	items=super.getRadioButtonCodeValuesBySearchBean(paramModule,this.getReportSearchBean());
	    	processItemsByModule(items);	    	
    	}
	}
    /*public void getAllModulelist() {
    	rptGenBean = new ReportsGenerateBean();
		this.setReportValue(null);
    	String paramModule = null;
    	items = null; 
    	if (null != new FacesScopes().getUserSessionBean().getUserId()) {
	    	if ((this.getModuleValue()) != null){
	    		paramModule="MOD";
	    	}
	    	items=super.getRadioButtonCodeValuesBySearchBean(paramModule,this.getReportSearchBean());
	    	//processItemsByModule(items);	    	
    	}
	}*/
	private void processItemsByModule(SelectItem[] items) {
		Map<String,List<SelectItem>> localModuleItemMap=new HashMap<String,List<SelectItem>>();
		if(null!=items){
			for(SelectItem item:items){
				String[] moduleItemArr= (String[])((String)item.getValue()).split(ReportConst.UNDERSCORE);
				if(localModuleItemMap.keySet().contains(moduleItemArr[0])){
					//code for contains
					localModuleItemMap.get(moduleItemArr[0]).add(item);
				} else {
					//not contains
					List<SelectItem> moduleItems=new ArrayList<SelectItem>();
					moduleItems.add(item);
					localModuleItemMap.put(moduleItemArr[0], moduleItems);
				}
			}
		}
		this.setReportList(localModuleItemMap.get(this.getModuleValue()));		
		setModuleItemMap(localModuleItemMap);		
	}

	public ISearchBean<PmParameterDesc> getReportSearchBean() {		
		return super.createSearchBean(
				PmParameterDesc.class, "moduleName");
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
				.getInstance().getServiceObject(ReportConst.PM_MODULE_SER_OBJ, ICategoryCodeSO.class);
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
		for (SmSupplierCategory cat : this.getSelectCatDeletionList()) {
			this.getSelectedCatList().remove(cat);
		}
		if ((this.getSelectedCatList().size()>9)) {
			this.setEnableValue(true);
			//throw new EpException(MessageConst.ERROR_REPORT_780010);
		}else{
			this.setEnableValue(false);
		}
	}

	public void assignCategoryForCompExperience() {		
		SmSupplierCategory cat = new SmSupplierCategory();		
		if ((this.getSelectedCatList().size()>8)) {
			this.setEnableValue(true);
			//throw new EpException(MessageConst.ERROR_REPORT_780010);
		}else{
			this.setEnableValue(false);
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
		SelectItem selectItem6 = new SelectItem();
		selectItem6.setLabel(CommonMethod
				.getMessageCommon(ReportConst.MOD_PP_TTL));
		selectItem6.setValue(ReportConst.PP_MOD_VAL);
		moduleList.add(selectItem6);
		
		//getAllModulelist();
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
		this.setReportName(null);
		setRoleCode(null);
		getManagedBean().setReportList(new ArrayList<SelectItem>());
		getManagedBean().setRptGenBean(rptGenBean);
		resetReportForm();
		
		
		if (this.getModuleValue() != null) {
			retriveReportListByModule(this.getModuleValue());
		}
		getManagedBean().setReportList(reportList);
		getManagedBean().setRptGenBean(rptGenBean);		
		retrieveRolecode();		
	}
	public void madatoryIndicator() {
		if((null!=this.getReportValue())&&(this.getModuleValue()
				.equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))){
			getManagedBean().getRptGenBean().setCmMadatoryIndicatorView(true);
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(false);
		}else{
			getManagedBean().getRptGenBean().setCmMadatoryIndicatorView(false);
			getManagedBean().getRptGenBean().setMadatoryIndicatorView(true);
		}
		if((null!=this.getReportValue())&&(this.getReportValue()
				.equalsIgnoreCase(ReportConst.SM_LAPORAN_TERIMAAN_HARIAN_RPT_VAL))){			
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
				if (!(pmReport.getReportCode()
						.equals(ReportConst.SC_S1_RCNT_PRICE_RPT_VAL))
						&& !(pmReport.getReportCode()
								.equals(ReportConst.SC_ITEM_PRICING_SUM_RPT_VAL))
						&& !(pmReport.getReportCode()
								.equals(ReportConst.SC_S1_REQUEST_NOTE_FINAL_RPT_VAL))) {
					selectItem.setValue(pmReport.getReportCode());
					selectItem.setLabel(label);
					reportList.add(selectItem);
				}
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780012);
		}
		return reportList;
	}
	public List<SelectItem> retrieveUPSSVOUser(){
		smUPSSVOUserList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrieveUPSSVOUser(lngUserId);			
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				smUPSSVOUserList.add(selectItem);
			}
        }
		return smUPSSVOUserList;
	}
	public List<SelectItem> retrievePTJS1(){
		scPtjList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrievePTJS1(lngUserId);			
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scPtjList.add(selectItem);
			}
        }
		return scPtjList;
	}
	public List<SelectItem> retrievePTJS2(){
		scPtjList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptLst = reportSO.retrievePTJS2(lngUserId);			
			for (Object[] object : ptLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scPtjList.add(selectItem);
			}
        }
		return scPtjList;
	}
	public List<SelectItem> retrieveRequisitioner(){
		scRequisitionerList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			if((this.getScPtj() != null) && (this.getScPtj().toString() != ReportConst.EMPTY_VAL)){
				orgProfileId = Long.parseLong(this.getScPtj());
			}
			List<Object[]> reqLst = reportSO.retrieveRequisitioner(lngUserId,orgProfileId);			
			for (Object[] object : reqLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scRequisitionerList.add(selectItem);
			}
        }
		return scRequisitionerList;
	}
	public List<SelectItem> retrieveDescOfficer(){
		scDescOfficerList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {        	
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			Long orgProfileId = null;
			if((this.getScPtj() != null) && (this.getScPtj().toString() != ReportConst.EMPTY_VAL)){
				orgProfileId = Long.parseLong(this.getScPtj());
			}
			List<Object[]> reqLst = reportSO.retrieveDeskOfficer(lngUserId,orgProfileId);			
			for (Object[] object : reqLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				scDescOfficerList.add(selectItem);
			}
        }
		return scDescOfficerList;
	}
	public List<SelectItem> retrieveApprovarNaikTarafDPS1(){
		approverNayakTarafList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO.retrieveApprovarNaikTarafDPS1(lngUserId);
			for (Object[] object : aprnktrfLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				approverNayakTarafList.add(selectItem);
			}
		}
		return approverNayakTarafList;
	}
	public List<SelectItem> retrieveApprovarNaikTarafSCS2(){
		approverNayakTarafList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> aprnktrfLst = reportSO.retrieveApprovarNaikTarafSCS2(lngUserId);
			for (Object[] object : aprnktrfLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1]);
				approverNayakTarafList.add(selectItem);
			}
		}
		return approverNayakTarafList;
	}
	/*
	 * Retrieve Role code by User id
	 */
	public List<String> retriveUserrloeByUserId(String moduleName) {
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {			
			reqDTO.setUserId(lngUserId);
			//reqDTO.setUserId((long) 240500);
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
		}
		return authoriztionList;
	}
	public void  retrieveAllMinistry() {
		ptjList = new ArrayList<SelectItem>();
		ministryList = new ArrayList<SelectItem>();
		retrieveRoleCodeFromParamTable();
		if(highRoleCodeList!=null){
			if((paramTableRoleCodeList!=null)&&(paramTableRoleCodeList.contains(highRoleCodeList.get(0)))){
			//if((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value()))))  {	
					 getLtstMinistryList();
					 getParameterCodeTypeString();
					 if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))){
							retrieveSupplierCompName();	
							if((this.getSupplCompanyList().size()>0)&&(getManagedBean().getRptGenBean().isFlSupplierView())){
								getManagedBean().getRptGenBean().setFlSupplierDropdownView(true);
							}else if((this.getSupplCompanyList().size()==0)&&(getManagedBean().getRptGenBean().isFlSupplierView())){
								getManagedBean().getRptGenBean().setFlSupplierTextView(true);
							}							
						}else{
							if(getManagedBean().getRptGenBean().isFlSupplierView()){
								getManagedBean().getRptGenBean().setFlSupplierTextView(true);
							}							
						}
					 paramCodeSupplierGroupDropdown(parameterCodeType);		
			}else{
				retrieveParameterCodeType();
			}
		}
	}
	
	public List<String>  retrieveRoleCodeFromParamTable() {
		paramTableRoleCodeList = new ArrayList<String>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
					IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
							.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
									IPMReportSO.class);
					List<String> roleCodeFromParamTableLst = reportSO.retrieveRoleCodeFromParamTable();			
					for (int i = 0; i < roleCodeFromParamTableLst.size(); i++) {
						paramTableRoleCodeList.add(roleCodeFromParamTableLst.get(i));
					}        	    
        }
		return paramTableRoleCodeList;
	}
	
	public String  getParameterCodeTypeString() {
		parameterCodeType = new String();
		/*ptjList = new ArrayList<String>();
		ministryList = new ArrayList<SelectItem>();*/
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> paramcodeLst = reportSO.retrieveParameterCodeByUserid(lngUserId);			
			for (Object[] object : paramcodeLst) {
				parameterCodeType = object[0].toString();
				//parameterCodeType = "SUP";
			}
		}
		return parameterCodeType;
	}
	public void  retrieveParameterCodeType() {
		ptjList = new ArrayList<SelectItem>();
		ministryList = new ArrayList<SelectItem>();
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			 getParameterCodeTypeString();
			if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FPJ))){
				//retrieveFPJMinistryList();
				//getLtstMinistryListByUserid();
				retrieveOnlyFPJMinistryList();
				retrieveFPJPTJList();
				if(getManagedBean().getRptGenBean().isFlSupplierView()){
					getManagedBean().getRptGenBean().setFlSupplierTextView(true);
				}
			}else if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI))){
				retrieveFPJMinistryList();
				//retrieveFPJPTJList();
				if(getManagedBean().getRptGenBean().isFlSupplierView()){
					getManagedBean().getRptGenBean().setFlSupplierTextView(true);
				}				
			}else if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))){
				retrieveSupplierCompName();	
				if((this.getSupplCompanyList().size()>0)&&(getManagedBean().getRptGenBean().isFlSupplierView())){
					getManagedBean().getRptGenBean().setFlSupplierDropdownView(true);
				}else if((this.getSupplCompanyList().size()==0)&&(getManagedBean().getRptGenBean().isFlSupplierView())){
					getManagedBean().getRptGenBean().setFlSupplierTextView(true);
				}
				
			}else{
				if(getManagedBean().getRptGenBean().isFlSupplierView()){
					getManagedBean().getRptGenBean().setFlSupplierTextView(true);
				}				
			}
			paramCodeSupplierGroupDropdown(parameterCodeType);				
		}		
	}
	public void  paramCodeSupplierGroupDropdown(String parameterCodeType) {
		if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))){
			if((getManagedBean().getRptGenBean().isFlGroupOneView())){
				getManagedBean().getRptGenBean().setFlGroupOneWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwoView())){
				getManagedBean().getRptGenBean().setFlGroupTwoWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupThreeView())){
				getManagedBean().getRptGenBean().setFlGroupThreeWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFourView())){
				getManagedBean().getRptGenBean().setFlGroupFourWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFiveView())){
				getManagedBean().getRptGenBean().setFlGroupFiveWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSixView())){
				getManagedBean().getRptGenBean().setFlGroupSixWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSevenView())){
				getManagedBean().getRptGenBean().setFlGroupSevenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupEightView())){
				getManagedBean().getRptGenBean().setFlGroupEightWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupNineView())){
				getManagedBean().getRptGenBean().setFlGroupNineWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTenView())){
				getManagedBean().getRptGenBean().setFlGroupTenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupElevenView())){
				getManagedBean().getRptGenBean().setFlGroupElevenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwelveView())){
				getManagedBean().getRptGenBean().setFlGroupTwelveWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupThirteenView())){
				getManagedBean().getRptGenBean().setFlGroupThirteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFourteenView())){
				getManagedBean().getRptGenBean().setFlGroupFourteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFifteenView())){
				getManagedBean().getRptGenBean().setFlGroupFifteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSixteenView())){
				getManagedBean().getRptGenBean().setFlGroupSixteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSeventeenView())){
				getManagedBean().getRptGenBean().setFlGroupSeventeenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupEighteenView())){
				getManagedBean().getRptGenBean().setFlGroupEighteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupNineteenView())){
				getManagedBean().getRptGenBean().setFlGroupNineteenWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyOneView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyOneWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyTwoView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyTwoWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyThreeView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyThreeWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyFourView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyFourWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyFiveView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyFiveWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentySixView())){
				getManagedBean().getRptGenBean().setFlGroupTwentySixWithoutSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentySevenView())){
				getManagedBean().getRptGenBean().setFlGroupTwentySevenWithoutSupplView(true);
			}
		}else{
			if((getManagedBean().getRptGenBean().isFlGroupOneView())){
				getManagedBean().getRptGenBean().setFlGroupOneWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwoView())){
				getManagedBean().getRptGenBean().setFlGroupTwoWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupThreeView())){
				getManagedBean().getRptGenBean().setFlGroupThreeWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFourView())){
				getManagedBean().getRptGenBean().setFlGroupFourWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFiveView())){
				getManagedBean().getRptGenBean().setFlGroupFiveWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSixView())){
				getManagedBean().getRptGenBean().setFlGroupSixWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSevenView())){
				getManagedBean().getRptGenBean().setFlGroupSevenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupEightView())){
				getManagedBean().getRptGenBean().setFlGroupEightWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupNineView())){
				getManagedBean().getRptGenBean().setFlGroupNineWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTenView())){
				getManagedBean().getRptGenBean().setFlGroupTenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupElevenView())){
				getManagedBean().getRptGenBean().setFlGroupElevenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwelveView())){
				getManagedBean().getRptGenBean().setFlGroupTwelveWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupThirteenView())){
				getManagedBean().getRptGenBean().setFlGroupThirteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFourteenView())){
				getManagedBean().getRptGenBean().setFlGroupFourteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupFifteenView())){
				getManagedBean().getRptGenBean().setFlGroupFifteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSixteenView())){
				getManagedBean().getRptGenBean().setFlGroupSixteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupSeventeenView())){
				getManagedBean().getRptGenBean().setFlGroupSeventeenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupEighteenView())){
				getManagedBean().getRptGenBean().setFlGroupEighteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupNineteenView())){
				getManagedBean().getRptGenBean().setFlGroupNineteenWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyOneView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyOneWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyTwoView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyTwoWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyThreeView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyThreeWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyFourView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyFourWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentyFiveView())){
				getManagedBean().getRptGenBean().setFlGroupTwentyFiveWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentySixView())){
				getManagedBean().getRptGenBean().setFlGroupTwentySixWithSupplView(true);
			}else if((getManagedBean().getRptGenBean().isFlGroupTwentySevenView())){
				getManagedBean().getRptGenBean().setFlGroupTwentySevenWithSupplView(true);
			}
		}
	}
	public void  enableSupplierCompNameTextOrDropdown() {
		if((this.getModuleValue()!=null)&&(this.getModuleValue().equalsIgnoreCase(
				ModuleCodeConst.SUPPLIER_MANAGEMENT))){
			getParameterCodeTypeString();
			retrieveSupplierCompName();
			if(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP)))&&(this.getReportValue().equalsIgnoreCase(ReportConst.SM_PERSON_BRANCH_BANK))){
				if((this.getSupplCompanyList().size()>0)&&(getManagedBean().getRptGenBean().isSmSupplierView())){
					getManagedBean().getRptGenBean().setSmCompanyNameDropdownView(true);
				}else if((this.getSupplCompanyList().size()==0)&&(getManagedBean().getRptGenBean().isSmSupplierView())){
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}
				
				getManagedBean().getRptGenBean().setSmExtraTwoColumnView(true);
				getManagedBean().getRptGenBean().setSmRegistrationTypeView(false);
				getManagedBean().getRptGenBean().setSmNonMandatoryDateView(false);
				getManagedBean().getRptGenBean().setSmMOFRegisteredDateView(false);
		    }else if((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.ROLE_CODE_SUP))){
					
				if((this.getSupplCompanyList().size()>0)&&(getManagedBean().getRptGenBean().isSmSupplierView())){
					getManagedBean().getRptGenBean().setSmCompanyNameDropdownView(true);
				}else if((this.getSupplCompanyList().size()==0)&&(getManagedBean().getRptGenBean().isSmSupplierView())){
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}
			}else{
				if(getManagedBean().getRptGenBean().isSmSupplierView()){
					getManagedBean().getRptGenBean().setSmCompanyNameView(true);
				}
			}
		}
	}
	public List<SelectItem>  retrieveFPJPTJList() {
		ptjList = new ArrayList<SelectItem>();		
		if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> ptjLst = reportSO.retrieveFPJPTJList(lngUserId);
			for (Object[] object : ptjLst) {				
				SelectItem selectItem = new SelectItem();				
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ptjList.add(selectItem);
			}			
		}
		return ptjList;
	}
	public List<SelectItem>  retrieveFMIPTJList() {
		ptjList = new ArrayList<SelectItem>();
		//if ((null !=this.getCommonMinistry())&&(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)))||((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value())))))) {
		if (null !=this.getCommonMinistry()){
			 getParameterCodeTypeString();	
			if(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)))||((paramTableRoleCodeList!=null)&&(paramTableRoleCodeList.contains(highRoleCodeList.get(0))))){				
				Long lngorgprflid = Long.parseLong(this.getCommonMinistry());
				IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				List<Object[]> ptjLst = reportSO.retrieveFMIPTJList(lngorgprflid);
				for (Object[] object : ptjLst) {				
					SelectItem selectItem = new SelectItem();				
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1].toString());
					ptjList.add(selectItem);
				}
			}
		}				
		return ptjList;
	}
	public List<SelectItem>  retrieveFMIPTJListWithArg(Long lngMinistry) {
		ptjList = new ArrayList<SelectItem>();		
		Long firstPTJValue = null;
		//if ((null !=this.getCommonMinistry())&&(((parameterCodeType!=null)&&(parameterCodeType.equals(ReportConst.PARAM_CODE_TYPE_FMI)))||((highRoleCodeList!=null)&&((highRoleCodeList.contains(RoleCode.BPK_PCA.value())||highRoleCodeList.contains(RoleCode.BPK_CCA.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_ADMIN.value())||highRoleCodeList.contains(RoleCode.BASIC_SUPPLIER_USER.value())||highRoleCodeList.contains(RoleCode.MOF_SUPPLIER_USER.value())))))) {
		
				IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
						.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
								IPMReportSO.class);
				List<Object[]> ptjLst = reportSO.retrieveFMIPTJList(lngMinistry);
				/*for (int i = 0; i < ptjLst.size(); i++) {				
					ptjList.add(ptjLst.get(i));
				}*/
				for (Object[] object : ptjLst) {				
					SelectItem selectItem = new SelectItem();				
					selectItem.setLabel(object[0].toString());
					selectItem.setValue(object[1].toString());
					ptjList.add(selectItem);
				}
				if (ptjList.size()>0) {					
					firstPTJValue=Long.parseLong((String) ptjList.get(0).getValue()) ;
					roleCodeUsersSetupOnload(firstPTJValue);
				}
		return ptjList;
	}
	public List<SelectItem>  retrieveOnlyFPJMinistryList() {
    	ministryList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> minstryLst = reportSO.retrieveFPJMinistryList(lngUserId);
			for (Object[] object : minstryLst) {				
				SelectItem selectItem = new SelectItem();				
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ministryList.add(selectItem);
			}				
		}
		return ministryList;	
	}
	public List<SelectItem>  retrieveFPJMinistryList() {
    	ministryList = new ArrayList<SelectItem>();
        if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> minstryLst = reportSO.retrieveFPJMinistryList(lngUserId);
			for (Object[] object : minstryLst) {
				/*SelectItem selectItem1 = new SelectItem();
				selectItem1.setLabel(ResourceBundleUtils.getMessage("lbl.txt.select", 
			 			ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));
				selectItem1.setValue(null);
				ministryList.add(selectItem1);*/
				SelectItem selectItem = new SelectItem();
				
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ministryList.add(selectItem);
				if ((null != object[1].toString())&&(ReportConst.EMPTY_VAL!= object[1].toString()  )) {
					Long lngMinistry = Long.parseLong(object[1].toString());
					retrieveFMIPTJListWithArg(lngMinistry);
				}
			}				
		}
		return ministryList;	
	}
/*public List<SelectItem>  retrieveFMIMinistryList() {
if (null != new FacesScopes().getUserSessionBean().getUserId()) {
			IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IPMReportSO.class);
			List<Object[]> minstryLst = reportSO.retrieveFMIMinistryList(lngUserId);
			for (Object[] object : minstryLst) {
				SelectItem selectItem = new SelectItem();
				selectItem.setLabel(object[0].toString());
				selectItem.setValue(object[1].toString());
				ministryList.add(selectItem);
			}
		}
		return ministryList;	
	}*/    
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
					throw new EpException(MessageConst.ERROR_REPORT_780013);
				}
			} else {
				this.setCmUnspscCode(null);
				this.setCmExtentionCode(null);
				rptGenBean.setCmExtentionCodeView(false);
				throw new EpException(MessageConst.ERROR_REPORT_780013);
			}
		} else {
			this.setCmExtentionCode(null);
			rptGenBean.setCmExtentionCodeView(false);
		}
	}
	public void enableExtensionTypes() {
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
					throw new EpException(MessageConst.ERROR_REPORT_780013);
				}
			} else {
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
				throw new EpException(MessageConst.ERROR_REPORT_780013);
			}
		} else {
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
	public void retrieveRolecode() {
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<String> rolecodeLst = reportSO.retrieveRolecode(lngUserId);
		for (int i = 0; i < rolecodeLst.size(); i++) {
			this.getRoleCodeList().add(rolecodeLst.get(i));
		}
	}
	public void retrieveSupplierCompName() {
		supplCompanyList = new ArrayList<String>();
		IPMReportSO reportSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IPMReportSO.class);
		List<String> supplCompNameLst = reportSO.retrieveSupplierCompName(lngUserId);
		for (int i = 0; i < supplCompNameLst.size(); i++) {
		supplCompanyList.add(supplCompNameLst.get(i));
		}
		if(supplCompNameLst.size()>0){
			if((this.getModuleValue()!=null)&&(this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_MANAGEMENT))){
				this.setSmCompanyName(supplCompNameLst.get(0));
			}
		    if((this.getModuleValue()!=null)&&(this.getModuleValue().equalsIgnoreCase(
						ModuleCodeConst.ORDER_FULFILLMENT))){	
					this.setFlSupplier(supplCompNameLst.get(0));
			}
		}
		
		
	}
	
	public void changeDateBasedOnRegType() {
		getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(false);
		getManagedBean().getRptGenBean().setSmActivationDateView(false);
		getManagedBean().getRptGenBean().setSmSubmissionDateView(false);
		getManagedBean().getRptGenBean().setSmActSubmisAllDateView(false);
		if(((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType().equals(ReportConst.RPT_REG_BASIC))&&(this.getReportValue().equals(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))){
			getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(true);
			getManagedBean().getRptGenBean().setSmActivationDateView(true);
		}else if(((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType().equals(ReportConst.RPT_REG_MOF))&&(this.getReportValue().equals(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))){
			getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(true);
			getManagedBean().getRptGenBean().setSmSubmissionDateView(true);
		}else if(((this.getSmRegistrationType()!=null)&&(this.getSmRegistrationType().equals(ReportConst.RPT_REG_ALL))&&(this.getReportValue().equals(ReportConst.SM_LST_DAILY_NEW_APPL_RPT_VAL)||this.getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)))){
			getManagedBean().getRptGenBean().setSmActiveSubmissiveFromDateView(true);
			getManagedBean().getRptGenBean().setSmActSubmisAllDateView(true);
		}
	}
	public void enableCriteriaMenu() {
		getManagedBean().getRptGenBean().setEnableCriteriaMenuView(false);
		if((this.getReportValue()!=null)&&(!this.getReportValue().equals(ReportConst.EMPTY_VAL))){
			getManagedBean().getRptGenBean().setEnableCriteriaMenuView(true);
		}		
	}
	/*
	 * Enable criteria variables by report name
	 */
	public void criteriaVariablesByReport() {
		parameterCodeType = new String();
		scReportBackingBean = new SCReportBackingBean();
		cmReportBackingBean = new CMReportBackingBean();
		flReportBackingBean = new FLReportBackingBean();
		smReportBackingBean = new SMReportBackingBean();
		ctReportBackingBean = new CTReportBackingBean();
		ppReportBackingBean = new PPReportBackingBean();
		pmReportBackingBean = new PMReportBackingBean();
		if(rptGenBean.isReportCategoryView()==true||(this.getModuleValue()
				.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT)&&this.getReportCategory()!=null)){
			rptGenBean = new ReportsGenerateBean();
			rptGenBean.setReportCategoryView(true);
			//this.setReportCategoryView(true);
		}else{
		rptGenBean = new ReportsGenerateBean();
		//rptGenBean.setReportCategoryView(this.isReportCategoryView());
		}
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
			reqDTO.setReportCode(getManagedBean().getReportValue());
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
			retrieveHighRoleCodeByUser();
			retrieveAllRoleCodesByUser();
			retrieveZoneByUser();
			//retrieveMinistryList();
			retriveUserrloeByUserId(this.getModuleValue());			
			retrieveParamCodeByUser();
			
			roleCodeUsersSetup();
			
			if ((this.getModuleValue()
					.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				retrieveApprovarNaikTarafDPS1();
				retrievePTJS1();
			}
			if ((this.getModuleValue()
							.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))) {
				retrieveApprovarNaikTarafSCS2();
				retrievePTJS2();
			}	
			retrieveRequisitioner();
			retrieveDescOfficer();
			generateYearList();
			if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CATALOGUE_MANAGEMENT))) {
				cmReportBackingBean.criteriaVariablesByReportByCM(rptGenBean,
						authoriztionList,paramList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_SELECTION))
					|| (this.getModuleValue()
							.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
				generateMonthList();
				
				scReportBackingBean.enableCriteriaVariablesByReportBySC(
						rptGenBean, authoriztionList,approverNayakTarafList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.ORDER_FULFILLMENT))) {
				getDocTypeStatusDropDown();
				flReportBackingBean.enableCriteriaVariablesByReportByFL(
						rptGenBean, authoriztionList);
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.SUPPLIER_MANAGEMENT))) {
				assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
				retrieveUPSSVOUser();
				retrieveIdentificationType();
				retrieveAccumulatedPaidUpCptl();
				retrieveRegStatusList();
				smReportBackingBean.enableCriteriaVariablesByReportBySM(
						rptGenBean, authoriztionList);
				
			} else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.CONTRACT_MANAGEMENT))) {
				ctReportBackingBean.enableCriteriaVariablesByReportByCT(
						rptGenBean, authoriztionList);
			}else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.PROCUREMENT_PLAN))) {
				ppReportBackingBean.enableCriteriaVariablesByReportByPP(
						rptGenBean, authoriztionList);
			}else if ((this.getModuleValue()
					.equalsIgnoreCase(ModuleCodeConst.PROFILE_MANAGEMENT))) {
				pmReportBackingBean.enableCriteriaVariablesByReportByPM(
						rptGenBean, authoriztionList);
			}
			rptGenBean.isReportCategoryView();
			getManagedBean().setRptGenBean(rptGenBean);
			resetReportForm();			
			retrieveDates();
			madatoryIndicator();
			retrieveAllMinistry();
			enableSupplierCompNameTextOrDropdown();
			//retrieveParameterCodeType();		
			enableCriteriaMenu();
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			zoneByRolecode();
			retrieveProcessingOfficerList();
		}
	}
	
	public void retrieveDates() {
		if ((this.getReportValue() != null) && (this.getReportValue() != ReportConst.EMPTY_VAL)) {
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
				if((null!=this.getReportValue())&&(this.getReportValue().equals(ReportConst.SM_SUPPLIER_PERFORMANCE)||this.getReportValue().equals(ReportConst.SM_THRESHOLD)||this.getReportValue().equals(ReportConst.SM_THRESHOLD_VALUE)||this.getReportValue().equals(ReportConst.SM_LST_MONTH_WISE_RPT_VAL))){
					Date currentDate = new Date();
					this.setSmCreatedFromYear(outputDateFormat.format(currentDate)
							.substring(6, 10));
					this.setSmCreatedToYear(outputDateFormat.format(currentDate)
							.substring(6, 10));
					this.setSmThresholdFromYear(outputDateFormat.format(currentDate)
							.substring(6, 10));
					this.setSmThresholdToYear(outputDateFormat.format(currentDate)
							.substring(6, 10));
					this.setSmMonthlyRptByYear(outputDateFormat.format(currentDate)
							.substring(6, 10));
				}		
			}
			if ((this.getModuleValue()
						.equalsIgnoreCase(ReportConst.DP_MOD_VAL))) {
					Date currentDate = new Date();
					this.setScSoneCrtdToDate(currentDate);
					Date newDate = new Date();
					newDate.setMonth(0);
					newDate.setDate(1);
					this.setScSoneCrtdFromDate(newDate);
			}
		}
	}
	public void checkOneYearDefference(Date fromDate,Date toDate) {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		String yearFrom=outputDateFormat.format(fromDate).substring(6, 10);
		String yearTo=outputDateFormat.format(toDate).substring(6, 10);
		String monthFrom=outputDateFormat.format(fromDate).substring(3, 5);
		String monthTo=outputDateFormat.format(toDate).substring(3, 5);
		String dayFrom=outputDateFormat.format(fromDate).substring(0, 2);
		String dayTo=outputDateFormat.format(toDate).substring(0, 2);
		int yearFromInt=Integer.parseInt(yearFrom);
		int yearToInt=Integer.parseInt(yearTo);
		int monthFromInt=Integer.parseInt(monthFrom);
		int monthToInt=Integer.parseInt(monthTo);
		int dayFromInt=Integer.parseInt(dayFrom);
		int dayToInt=Integer.parseInt(dayTo);		
		if(yearToInt==yearFromInt+1){
			if(monthToInt==monthFromInt){
				if(dayToInt>dayFromInt){
					throw new EpException(MessageConst.ERROR_REPORT_780011);
				}				
			}else if(monthToInt>monthFromInt){
				throw new EpException(MessageConst.ERROR_REPORT_780011);
			}			
		}else if(yearToInt>yearFromInt+1){			
			throw new EpException(MessageConst.ERROR_REPORT_780011);
		}		
	}
	public void checkOnlyYearFieldDefference(String yearFrom,String yearTo) {
		int yearFromInt=Integer.parseInt(yearFrom);
		int yearToInt=Integer.parseInt(yearTo);
		if(yearFromInt>yearToInt){			
			throw new EpException(MessageConst.ERROR_REPORT_780015);
		}
	}
	public void checkTwoYearsFieldDefference(String yearFrom,String yearTo) {
		int yearFromInt=Integer.parseInt(yearFrom);
		int yearToInt=Integer.parseInt(yearTo);
		if(yearToInt>yearFromInt+1){	
			throw new EpException(MessageConst.ERROR_REPORT_780017);
		}
	}
	public void retrieveState() {
		stateLst = new ArrayList<SelectItem>();
		districtLst = new ArrayList<SelectItem>();
		addingStateList = new ArrayList<String>();
		addingDistrictList = new ArrayList<String>();
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
		addingStateList = new ArrayList<String>();
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
				selectItem.setValue(pmDistrict.getDistrictId());
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
			selectItem.setLabel(catOne.getCategoryL1Code() + ReportConst.RPT_EIFON
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
				selectItem.setLabel(catTwo.getCategoryL2Code() + ReportConst.RPT_EIFON
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
				selectItem.setLabel(catThree.getCategoryL3Code() + ReportConst.RPT_EIFON
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
				
			} else if (this.getSmRegistrationType().equalsIgnoreCase(
					ReportConst.RPT_REG_MOF)) {
				getManagedBean().getRptGenBean().setSmMofRegView(true);
				getManagedBean().getRptGenBean().setSmBasicRegView(false);				
			}
		}
	}

	public List<SelectItem> generateMonthList() {
		scMonthList = scReportBackingBean.generateMonthList();
		return scMonthList;
	}

	public List<String> generateYearList() {
		scYearList = rptGenBean.generateCommonYearList();
		return scYearList;
	}

	public void createdToDtUpdate() {
		this.setScCreatedToYear(this.getScCreatedFromYear());
	}
	public void createdToMonthUpdate(){
		this.setScCreatedToMonth(this.getScCreatedFromMonth());
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
	
	public void checkSMYearNMonthDefference() {
		int fromyear = Integer.parseInt(this.getSmCreatedFromYear());
		int toyear = Integer.parseInt(this.getSmCreatedToYear());
		int fromMonth = Integer.parseInt(this.getSmFromMonth());
		int toMonth = Integer.parseInt(this.getSmToMonth());
		if(fromyear==toyear){
			if(fromMonth>toMonth){
				throw new EpException(MessageConst.ERROR_REPORT_780014);
			}			
		}else if(fromyear>toyear){			
			throw new EpException(MessageConst.ERROR_REPORT_780015);
		}		
	}

	/*
	 * Generate all modules reports
	 */
	public void generateReport() {
		if (((this.getReportValue() != null)&&this.getReportValue().toString().equals(ReportConst.SM_LIST_OF_COMP_SORT_ALPHA_RPT_VAL))||((this.getReportValue() != null)&&this.getReportValue().toString().equals(ReportConst.SM_LIST_OF_DIR_OWN_CEO_COMPANY_RPT_VAL))||((this.getReportValue() != null)&&this.getReportValue().toString().equals(ReportConst.SM_LIST_OF_COMP_DIR_OWN_CEO_RPT_VAL))||((this.getReportValue() != null)&&this.getReportValue().toString().equals(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))){
			mofRegNoNumericValidation();
		}
		
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
					throw new EpException(MessageConst.ERROR_REPORT_780004);
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getFromDate().after(this.getToDate())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				}
			}
			if ((this.getFromDate() != null) && (this.getToDate() != null)) {
				if (this.getToDate().after(this.getFromDate())) {
					checkOneYearDefference(this.getFromDate(),this.getToDate());
				}
			}
			
			if ((this.getScSoneCrtdFromDate() != null)
					&& (this.getScSoneCrtdToDate() != null)) {
				if (this.getScSoneCrtdFromDate().after(
						this.getScSoneCrtdToDate())) {
					throw new EpException(MessageConst.ERROR_REPORT_780008);
				}
			}
			if ((this.getScSoneCrtdFromDate() != null) && (this.getScSoneCrtdToDate() != null)) {
				if (this.getScSoneCrtdToDate().after(this.getScSoneCrtdFromDate())) {
					checkOneYearDefference(this.getScSoneCrtdFromDate(),this.getScSoneCrtdToDate());
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
			if (((this.getSmThresholdFromYear() != null)
					&& (this.getSmThresholdToYear() == null))||((this.getSmThresholdFromYear() == null)
							&& (this.getSmThresholdToYear() != null))) {
				throw new EpException(MessageConst.ERROR_REPORT_780016);
			}
			if ((this.getSmThresholdFromYear() != null)
					&& (this.getSmThresholdToYear() != null)) {
			 checkOnlyYearFieldDefference(this.getSmThresholdFromYear(),this.getSmThresholdToYear());
			}
			if ((this.getSmCreatedFromYear() != null)
					&& (this.getSmCreatedToYear() != null)) {
			 checkOnlyYearFieldDefference(this.getSmCreatedFromYear(),this.getSmCreatedToYear());
			}
			if((this.getReportValue()!= null && (getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SM_SUPPLIER_PERFORMANCE)))||(this.getReportValue()!= null && (getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SM_THRESHOLD)))||(this.getReportValue()!= null && (getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SM_THRESHOLD_VALUE)))){
				if ((this.getSmThresholdFromYear() != null) && (this.getSmThresholdToYear() != null)) {
					checkTwoYearsFieldDefference(this.getSmThresholdFromYear(),this.getSmThresholdToYear());
				}
			}
			if(this.getReportValue()!= null && (getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_YEAR_RPT_VAL))){
				if ((this.getSmCreatedFromYear() != null) && (this.getSmCreatedToYear() != null)) {
					checkTwoYearsFieldDefference(this.getSmCreatedFromYear(),this.getSmCreatedToYear());
				}
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
			}else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.PROCUREMENT_PLAN)) {
				generatePPReport();
			}else if (this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.PROFILE_MANAGEMENT)) {
				generatePMReport();
			}
			if (null != constructedParamString
					&& !ReportConst.EMPTY_VAL.equals(constructedParamString)) {
				//try {
					getManagedBean().getRptGenBean().setGenerateButtonView(true);
					//generateReportTimer();
					//generateReportIntime();
					fileGenerateReport(getManagedBean().getReportJsp(),	constructedParamString);
					/*FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("reportGenDialog.hide()");//execute("reportGenDialog.hide()");
					RequestContext.getCurrentInstance().execute("reportGenDialog.hide()");*/
					getManagedBean().getRptGenBean().setGenerateButtonView(false);
				/*} catch (IOException e) {
					getManagedBean().getRptGenBean().setGenerateButtonView(false);
					throw new EpException(MessageConst.ERROR_REPORT_780009);
				}*/
			}
			//InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/css/images/optimus.jpg");
			////file = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
	        //RequestContext.getCurrentInstance().execute("statusDialog.hide()");
			/*RequestContext context = RequestContext.getCurrentInstance();
			context.addCallbackParam(ReportConst.RPT_IN_VALID,
					ReportConst.RPT_TRUE);*/
			//externalContext.addResponseCookie("primefaces.download", "true", Collections.<String, Object>emptyMap());
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.addResponseCookie(org.primefaces.util.Constants.DOWNLOAD_COOKIE, ReportConst.RPT_TRUE, Collections.<String, Object>emptyMap());
		} else {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.addResponseCookie(org.primefaces.util.Constants.DOWNLOAD_COOKIE, ReportConst.RPT_TRUE, Collections.<String, Object>emptyMap());
			throw new EpException(MessageConst.ERROR_REPORT_780001);
		}
	}
 
	public void generateReportIntime() {
		ExecutorService service = Executors.newSingleThreadExecutor();

		try {
		    Runnable rnbl = new Runnable() {
		        @Override
		        public void run() {
						fileGenerateReport(getManagedBean().getReportJsp(),
								constructedParamString);					
		        }
		    };
		    Future<?> f = service.submit(rnbl);
		    f.get(1, TimeUnit.MINUTES);     // attempt the task for one minutes
		}
		catch (final InterruptedException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		}
		catch (final TimeoutException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780020);
		}
		catch (final ExecutionException e) {
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780023);
		} 
		finally {
		    service.shutdown();
		}
	}
	public void generateReportTimer() {
		Thread thread = new Thread(new Runnable() {
	        @Override
	        public void run(){
					fileGenerateReport(getManagedBean().getReportJsp(),
							constructedParamString);
	        }
		});
		thread.start();
		long endTimeMillis = System.currentTimeMillis() + 60000;
		while (thread.isAlive()) {
		    if (System.currentTimeMillis() > endTimeMillis) {
		    	getManagedBean().getRptGenBean().setGenerateButtonView(false);		    	
		    	break;		        
		    }
		    try {
		        Thread.sleep(50000);
		    }
		    catch (InterruptedException t) {
		    	getManagedBean().getRptGenBean().setGenerateButtonView(false);
		    	log.info(MessageConst.ERROR_REPORT_780020);
		    }
		}
	}	
	public void loadReportWithParams(List<String> paramLst) {
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
		if (((this.getCtContrMontrStatus() != null) && (this.getCtContrMontrStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtUserType() != null) && (this.getCtUserType() != ReportConst.EMPTY_VAL))
				|| ((this.getCtSubType() != null) && (this.getCtSubType() != ReportConst.EMPTY_VAL))
				|| ((this.getCtProductService() != null) && (this.getCtProductService() != ReportConst.EMPTY_VAL))
				|| ((this.getCtLoanNo() != null) && (this.getCtLoanNo() != ReportConst.EMPTY_VAL)) 
				|| ((this.getCtLOAPhysicalNo() != null) && (this.getCtLOAPhysicalNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getCtContractNo() != null) && (this.getCtContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtPhysicalContractNo() != null) && (this.getCtPhysicalContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getCtAgreement() != null) && (this.getCtAgreement() != ReportConst.EMPTY_VAL))
				|| ((this.getCtStatus() != null) && (this.getCtStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getCtStatusTwo() != null) && (this.getCtStatusTwo() != ReportConst.EMPTY_VAL))			
				/*|| ((this.getCtAgreement() != null) && (!this.getCtAgreement().equals(ReportConst.CT_GROUP_VAL_ALL)))
				|| ((this.getCtStatus() != null) && (!this.getCtStatus().equals(ReportConst.CT_GROUP_VAL_ALL)))
				|| ((this.getCtStatusTwo() != null) && (!this.getCtStatusTwo().equals(ReportConst.CT_GROUP_VAL_ALL)))*/
				|| ((this.getCtComplete() != null) && (this.getCtComplete() != ReportConst.EMPTY_VAL))
				/*|| ((this.getCtComplete() != null) && (!this.getCtComplete().equals(ReportConst.CT_GROUP_VAL_ALL)))*/
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL)) 
			    || ((this.getCtProcurementMode() != null) && (this.getCtProcurementMode().toString() != ReportConst.EMPTY_VAL)))
			    /*|| ((this.getCtProcurementMode() != null) && (!this.getCtProcurementMode().toString().equals(ReportConst.CT_GROUP_VAL_ALL))))*/
		       {
			
			if ((this.getCtStatus() != null)
					&& (this.getCtStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtStatusVar(ReportConst.CT_AMP_STATUS_EQ
						+ this.getCtStatus());
			}
			if ((this.getCtStatusTwo() != null)
					&& (this.getCtStatusTwo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtStatusTwoVar(ReportConst.CT_AMP_STATUS_EQ
						+ this.getCtStatusTwo());
			}
			if ((this.getCtContractNo() != null)
					&& (this.getCtContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtContractNoVar(ReportConst.CT_AMP_CONTRACT_NO_EQ
						+ this.getCtContractNo());
			}
			if ((this.getCtPhysicalContractNo() != null)
					&& (this.getCtPhysicalContractNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtPhysicalContractNoVar(ReportConst.CT_AMP_PHYSICALCONTRACT_NO_EQ
						+ this.getCtPhysicalContractNo());
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
			if ((this.getCtGroupThree() != null)
					&& (this.getCtGroupThree() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtGroupThreeVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getCtGroupThree());
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
			if ((this.getCtLOAPhysicalNo() != null)
					&& (this.getCtLOAPhysicalNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtLOAPhysicalNoVar(ReportConst.CT_AMP_PLOAPHYSICALNO_EQ
						+ this.getCtLOAPhysicalNo());
			}
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				/*Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				rptGenBean.setCtMinistryVar(ReportConst.CT_AMP_PMINISTRY_EQ
						+ retrieveMinistryName(orgprofileid));*/
				rptGenBean.setCtMinistryVar(ReportConst.CT_AMP_PMINISTRY_EQ
						+ this.getCommonMinistry());
			}
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtPtjVar(ReportConst.CT_AMP_PPTJNAME_EQ
						+ this.getCommonPtj());
			}
			if ((this.getCtUserType() != null)
					&& (this.getCtUserType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtUserTypeVar(ReportConst.CT_AMP_USERTYPE_EQ
						+ this.getCtUserType());
			}
			if ((this.getCtSubType() != null)
					&& (this.getCtSubType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCtSubTypeVar(ReportConst.CT_AMP_SUBTYPE_EQ
						+ this.getCtSubType());
			}
			
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getCtContractNoVar()
						+ rptGenBean.getCtPhysicalContractNoVar()
						+ rptGenBean.getCtStatusTwoVar()
						+ rptGenBean.getCtAgreementVar()
						+ rptGenBean.getCtStatusVar()
						+ rptGenBean.getCtCompleteVar()
						+ rptGenBean.getCtContrMontrStatusVar()
						+ rptGenBean.getCtGroupOneVar()
						+ rptGenBean.getCtGroupTwoVar()
						+ rptGenBean.getCtGroupThreeVar()
						+ rptGenBean.getCtProductServiceVar()
						+ rptGenBean.getCtMinistryVar()
						+ rptGenBean.getCtPtjVar()
						+ rptGenBean.getCtLoanNoVar()
						+ rptGenBean.getCtLOAPhysicalNoVar()
						+ rptGenBean.getCtUserTypeVar()
						+ rptGenBean.getCtSubTypeVar()
						+ rptGenBean.getCtProcurementModeVar()
						+ rptGenBean.getFromDateVar()
						+ rptGenBean.getToDateVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
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
			if ( (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_TOT_NUM_YEARLY_APPL_RPT_VAL))) {
				checkSMYearNMonthDefference();
			}
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
			if ((this.getSmFromMonth() != null)
					&& (this.getSmFromMonth() != ReportConst.EMPTY_VAL)) {
				String month = getManagedBean().getMonthMap().get(this.getSmFromMonth());
				rptGenBean
						.setSmFromMonthVar(ReportConst.AMP_SM_FROM_DATE_EQ
								+ month);
			}
			if ((this.getSmToMonth() != null)
					&& (this.getSmToMonth() != ReportConst.EMPTY_VAL)) {
				String month = getManagedBean().getMonthMap().get(this.getSmToMonth());
				rptGenBean
						.setSmToMonthVar(ReportConst.AMP_SM_TO_DATE_EQ
								+ month);
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
						+ rptGenBean.getSmFromMonthVar()
						+ rptGenBean.getSmToMonthVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} /*else if ((getManagedBean().getReportValue() != null)
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
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		}*/ else if (((this.getSmYearText() != null) && (this.getSmYearText() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationType() != null) && (this.getSmApplicationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMonthlyRptByYear() != null) && (this.getSmMonthlyRptByYear() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaymentType() != null) && (this.getSmPaymentType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCompanyName() != null) && (this.getSmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationType() != null) && (this.getSmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationNo() != null) && (this.getSmApplicationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmZone() != null) && (this.getSmZone() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupplierType() != null) && (this.getSmSupplierType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNo() != null) && (this.getSmMofRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationStatus() != null) && (this.getSmRegistrationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMainCategory() != null) && (this.getSmMainCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSubCategory() != null) && (this.getSmSubCategory() != ReportConst.EMPTY_VAL))//this.getSelectedCatList()
				//|| ((this.getSelectedCatList() != null) && (this.getSelectedCatList().size()> 0))
				||(((getManagedBean().getReportValue() != null)
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
											.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL))
											|| (getManagedBean().getReportValue() != null)
											&& (getManagedBean().getReportValue().trim()
													.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL)))&& (this.getSelectedCatList().size()> 0))
				|| ((this.getSmSubCategoryDivision() != null) && (this.getSmSubCategoryDivision() != ReportConst.EMPTY_VAL))
				|| ((this.getSmApplicationStatus() != null) && (this.getSmApplicationStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmState() != null) && (this.getSmState() != ReportConst.EMPTY_VAL))
				|| ((this.getSmDistrict() != null) && (this.getSmDistrict() != ReportConst.EMPTY_VAL))
				|| ((this.getSmReportType() != null) && (this.getSmReportType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNoFrom() != null) && (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL))
				|| ((this.getSmMofRegNoTo() != null) && (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalFrm() != null) && (this.getSmPaidUpCapitalFrm() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalTo() != null) && (this.getSmPaidUpCapitalTo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPaidUpCapitalNaccum() != null) && (this.getSmPaidUpCapitalNaccum() != ReportConst.EMPTY_VAL))
				//|| ((this.getSmRole() != null) && (this.getSmRole() != ReportConst.EMPTY_VAL))
				|| ((this.getSmEpNo() != null) && (this.getSmEpNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRegistrationNo() != null) && (this.getSmRegistrationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmGSTRegistrationNo() != null) && (this.getSmGSTRegistrationNo() != ReportConst.EMPTY_VAL))
				|| ((this.getSmGSTRegisteredStatus() != null) && (this.getSmGSTRegisteredStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupportingDocumentMode() != null) && (this.getSmSupportingDocumentMode() != ReportConst.EMPTY_VAL))
				|| ((this.getSmSupportingDocumentStatus() != null) && (this.getSmSupportingDocumentStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getSmPersonnelRole()!= null) && (this.getSmPersonnelRole() != ReportConst.EMPTY_VAL))
				|| ((this.getSmIdentificationType()!= null) && (this.getSmIdentificationType() != ReportConst.EMPTY_VAL))
				|| ((this.getSmIdentificationNumber()!= null) && (this.getSmIdentificationNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getSmCancellationBy()!= null) && (this.getSmCancellationBy() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmUserName() != null) && (this.getSmUserName().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmRequestExpiryDate() != null) && (this.getSmRequestExpiryDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmProfileStatus() != null) && (this.getSmProfileStatus().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmThresholdFromYear() != null) && (this.getSmThresholdFromYear().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getSmThresholdToYear() != null) && (this.getSmThresholdToYear().toString() != ReportConst.EMPTY_VAL))) {

			if ((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_APPLICATION_APPROVED_RPT_VAL))){
				if (((this.getSmCompanyName() == null) || (this.getSmCompanyName().equals(ReportConst.EMPTY_VAL)))
				&& ((this.getSmMofRegNo() == null) || (this.getSmMofRegNo().equals(ReportConst.EMPTY_VAL)))
				&& ((this.getFromDate() == null) || (this.getFromDate().toString().equals(ReportConst.EMPTY_VAL)))
				&& ((this.getToDate() == null) || (this.getToDate().toString().equals(ReportConst.EMPTY_VAL)))){
					throw new EpException(MessageConst.ERROR_REPORT_780003);
				}		
			}
			if((getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_SUPPLIER_PERFORMANCE))
					|| (getManagedBean().getReportValue() != null)
					&& (getManagedBean().getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SM_THRESHOLD))
							|| (getManagedBean().getReportValue() != null)
							&& (getManagedBean().getReportValue().trim()
									.equalsIgnoreCase(ReportConst.SM_THRESHOLD_VALUE))){
				if ((this.getSmThresholdFromYear() != null)
						&& (this.getSmThresholdFromYear() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setSmThresholdFromYearVar(ReportConst.AMP_SM_FROM_YEAR_EQ
									+ this.getSmThresholdFromYear());
				}
				if ((this.getSmThresholdToYear() != null)
						&& (this.getSmThresholdToYear() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setSmThresholdToYearVar(ReportConst.AMP_SM_TO_YEAR_EQ
									+ this.getSmThresholdToYear());
				}				
			}	
			if ((this.getSmProfileStatus() != null)
					&& (this.getSmProfileStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmProfileStatusVar(ReportConst.AMP_SM_PRFL_STS_EQ
								+ this.getSmProfileStatus());
			}
			if ((this.getSmGSTRegistrationNo() != null)
					&& (this.getSmGSTRegistrationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmGSTRegistrationNoVar(ReportConst.AMP_SM_GST_REGNO_EQ
								+ this.getSmGSTRegistrationNo());
			}
			if ((this.getSmGSTRegisteredStatus() != null)
					&& (this.getSmGSTRegisteredStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmGSTRegisteredStatusVar(ReportConst.AMP_SM_GST_STS_EQ
								+ this.getSmGSTRegisteredStatus());
			}
			if ((this.getSmUserName() != null)
					&& (this.getSmUserName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmUserNameVar(ReportConst.AMP_SM_USER_NAME_EQ
								+ this.getSmUserName());
			}
			if ((this.getSmRequestExpiryDate() != null)
					&& (this.getSmRequestExpiryDate() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmRequestExpiryDateVar(ReportConst.AMP_SM_REQ_ENQ_DATE_EQ
								+ this.getSmRequestExpiryDate());
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
			
			if ((this.getSmPaymentMethod() != null)
					&& (this.getSmPaymentMethod() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmPaymentMethodVar(ReportConst.AMP_SM_PAYMENT_MENTHOD_EQ
								+ this.getSmPaymentMethod());
			}
			if ((this.getSmProcessingType() != null)
					&& (this.getSmProcessingType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmProcessingTypeVar(ReportConst.AMP_SM_PRO_TYPE_EQ
								+ this.getSmProcessingType());
			}
			if ((this.getSmReceiptNo() != null)
					&& (this.getSmReceiptNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmReceiptNoVar(ReportConst.AMP_SM_RECEIPT_NO_EQ
								+ this.getSmReceiptNo());
			}
			if ((this.getSmProcessingOfficerName() != null)
					&& (this.getSmProcessingOfficerName() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setSmProcessingOfficerNameVar(ReportConst.AMP_SM_PRO_OFFICERNAME_EQ
								+ this.getSmProcessingOfficerName());
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
			if ((this.getSmMonthlyRptByYear() != null)
					&& (this.getSmMonthlyRptByYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmMonthlyRptByYearVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getSmMonthlyRptByYear());
			}
			if ((this.getSmMofRegNoFrom() != null && (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL))
					&& (this.getSmMofRegNoTo() != null && (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL))) {
				String mofRegNoFrom = null;
				String mofRegNoTo = null;
				if(this.getSmMofRegNoFrom().contains(ReportConst.PARAM_IFON)){
					mofRegNoFrom = this.getSmMofRegNoFrom().replace(ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);
					if(mofRegNoFrom.contains(ReportConst.PARAM_SPACE)){
						mofRegNoFrom=mofRegNoFrom.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);	
					}
				}else{
					mofRegNoFrom = this.getSmMofRegNoFrom();
				}
								
				if(this.getSmMofRegNoTo().contains(ReportConst.PARAM_IFON)){
					mofRegNoTo = this.getSmMofRegNoTo().replace(ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);	
					if(mofRegNoTo.contains(ReportConst.PARAM_SPACE)){
						mofRegNoTo=mofRegNoTo.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);	
					}
				}else{
					mofRegNoTo = this.getSmMofRegNoTo();
				}
				
			  ValidateMofRegNo(mofRegNoFrom, mofRegNoTo);
			}
			if ((this.getSmMofRegNoFrom() != null)
					&& (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL)) {
					rptGenBean.setSmMofRegNoFromVar(ReportConst.AMP_SM_MOF_REG_NO_FRM_EQ
						+ this.getSmMofRegNoFrom());
			}
			if ((this.getSmMofRegNoTo() != null)
					&& (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmMofRegNoToVar(ReportConst.AMP_SM_MOF_REG_NO_TO_EQ
						+ this.getSmMofRegNoTo());
			}
			if ((this.getSmPaidUpCapitalFrm() != null)
					&& (this.getSmPaidUpCapitalFrm() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmPaidUpCapitalFrmVar(ReportConst.AMP_SM_PAIDUP_CPTL_FROM_EQ
						+ this.getSmPaidUpCapitalFrm());
			}
			if ((this.getSmPaidUpCapitalTo() != null)
					&& (this.getSmPaidUpCapitalTo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmPaidUpCapitalToVar(ReportConst.AMP_SM_PAIDUP_CPTL_TO_EQ
						+ this.getSmPaidUpCapitalTo());
			}
			if ((this.getSmPaidUpCapitalNaccum() != null)
					&& (this.getSmPaidUpCapitalNaccum() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmPaidUpCapitalNaccumVar(ReportConst.AMP_SM_PAIDUP_CPTL_R_ACLTD_EQ
						+ this.getSmPaidUpCapitalNaccum());
			}			
			if ((this.getSmEpNo() != null)
					&& (this.getSmEpNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmEpNoVar(ReportConst.AMP_SM_EP_NO_EQ
						+ this.getSmEpNo());
			}
			if ((this.getSmRegistrationNo() != null)
					&& (this.getSmRegistrationNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmRegistrationNoVar(ReportConst.AMP_SM_REG_NO_EQ
						+ this.getSmRegistrationNo());
			}
			if ((this.getSmSupportingDocumentMode() != null)
					&& (this.getSmSupportingDocumentMode() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmSupportingDocumentModeVar(ReportConst.AMP_SM_SUPP_DOC_MODE_EQ
						+ this.getSmSupportingDocumentMode());
			}
			if ((this.getSmSupportingDocumentStatus() != null)
					&& (this.getSmSupportingDocumentStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmSupportingDocumentStatusVar(ReportConst.AMP_SM_SUPP_DOC_STATUS_EQ
						+ this.getSmSupportingDocumentStatus());
			}
			if ((this.getSmPersonnelRole() != null)
					&& (this.getSmPersonnelRole() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmPersonnelRoleVar(ReportConst.AMP_SM_PERSONNEL_ROLE_EQ
						+ this.getSmPersonnelRole());
			}
			if ((this.getSmIdentificationType() != null)
					&& (this.getSmIdentificationType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmIdentificationTypeVar(ReportConst.AMP_SM_IDENT_TYPE_EQ
						+ this.getSmIdentificationType());
			}
			if ((this.getSmIdentificationNumber() != null)
					&& (this.getSmIdentificationNumber() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmIdentificationNumberVar(ReportConst.AMP_SM_IDENT_NO_EQ
						+ this.getSmIdentificationNumber());
			}
			if ((this.getSmCancellationBy() != null)
					&& (this.getSmCancellationBy() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setSmCancellationByVar(ReportConst.AMP_SM_CANCEL_BY_EQ
						+ this.getSmCancellationBy());
			}
			StringBuffer maincategory = new StringBuffer();
			StringBuffer subcategory = new StringBuffer();
			StringBuffer subcategorydivision = new StringBuffer();
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
											.equalsIgnoreCase(ReportConst.SM_CAT_STATIC_BY_STATE_RPT_VAL))
											|| (getManagedBean().getReportValue() != null)
											&& (getManagedBean().getReportValue().trim()
													.equalsIgnoreCase(ReportConst.SM_LIST_OF_COMPANY_SUPTYPE_RPT_VAL))) {
				maincategory.append(ReportConst.AMP_SM_MAIN_CATEGORY_EQ);
				subcategory.append(ReportConst.AMP_SM_SUB_CATEGORY_EQ);
				subcategorydivision.append(ReportConst.AMP_SM_SUB_DIV_CATEGORY_EQ);
				for (SmSupplierCategory smSupplier : this.getSelectedCatList()){
					maincategory.append(ReportConst.KAMA+smSupplier.getPmCategoryL1().getCategoryL1Id());
					subcategory.append(ReportConst.KAMA+smSupplier.getPmCategoryL2().getCategoryL2Id());
					subcategorydivision.append(ReportConst.KAMA+smSupplier.getPmCategoryL3().getCategoryL3Id());
				}
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
						+ rptGenBean.getSmMofRegNoFromVar()
						+ rptGenBean.getSmMofRegNoToVar()
						+ rptGenBean.getSmPaidUpCapitalFrmVar()
						+ rptGenBean.getSmPaidUpCapitalToVar()
						+ rptGenBean.getSmPaidUpCapitalNaccumVar()
						+ rptGenBean.getSmProfileStatusVar()
						//+ rptGenBean.getSmRoleVar()
						+ rptGenBean.getSmEpNoVar()
						+ rptGenBean.getSmRegistrationNoVar()
						+ rptGenBean.getSmSupportingDocumentModeVar()
						+ rptGenBean.getSmSupportingDocumentStatusVar()
						+ rptGenBean.getSmPersonnelRoleVar()
						+ rptGenBean.getSmIdentificationTypeVar()
						+ rptGenBean.getSmIdentificationNumberVar()
						+ rptGenBean.getSmCancellationByVar()
						+maincategory
						+subcategory
						+subcategorydivision
						+ rptGenBean.getApplicationStatusTotVar()
						+ rptGenBean.getSmYearTextVar()
						+ rptGenBean.getSmMonthlyRptByYearVar()
						+ rptGenBean.getPaymentTypeTotVar()
						+ rptGenBean.getSmPaymentMethodVar()
						+ rptGenBean.getSmProcessingTypeVar()
						+ rptGenBean.getSmReceiptNoVar()
						+ rptGenBean.getSmThresholdFromYearVar()
						+ rptGenBean.getSmThresholdToYearVar()						
						+ rptGenBean.getSmUserNameVar()
						+ rptGenBean.getSmRequestExpiryDateVar()
						+ rptGenBean.getSmProcessingOfficerNameVar()						
						+ rptGenBean.getApplicationTypeTotVar()
						+ rptGenBean.getStateTotVar()
						+ rptGenBean.getDistrictTotVar()
						+ rptGenBean.getReportTypeTotVar()
						+ rptGenBean.getSmSVOUserVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
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
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else if (((getScQuotationNo() != null) && (getScQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getFromDate() != null) && (getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getToDate() != null) && (getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdFromDate() != null) && (getScSoneCrtdFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScSoneCrtdToDate() != null) && (getScSoneCrtdToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPIno() != null) && (getScPIno() != ReportConst.EMPTY_VAL))
				|| ((getScRequisenerId() != null) && (getScRequisenerId() != ReportConst.EMPTY_VAL))
				|| ((getScSupplier() != null) && (getScSupplier() != ReportConst.EMPTY_VAL))
				|| ((getScSoneStatus() != null) && (getScSoneStatus() != ReportConst.EMPTY_VAL))
				|| (this.getScProcureAllList().size() > 0)
				|| (this.getScQuotationList().size() > 0)
				|| (this.getScTenderList().size() > 0)
				|| (this.getScRstrctdTndrList().size() > 0)
				|| ((getScCreatedToMonth() != null) && (getScCreatedToMonth().toString() != ReportConst.EMPTY_VAL))
				|| ((getScCreatedFromMonth() != null) && (getScCreatedFromMonth().toString() != ReportConst.EMPTY_VAL))
				|| ((getScDeskOfficer() != null) && (getScDeskOfficer().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPtj() != null) && (getScPtj().toString() != ReportConst.EMPTY_VAL))
				|| ((getScStatus() != null) && (getScStatus().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishFromDate() != null) && (getScPublishFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((getScPublishToDate() != null) && (getScPublishToDate().toString() != ReportConst.EMPTY_VAL))
				/*|| ((getScPrcRptGroup() != null) && (getScPrcRptGroup() != ReportConst.EMPTY_VAL))
				|| ((getScPIDtlsPrcRptGroup() != null) && (getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL))*/
				|| ((getScProcurementType() != null) && (getScProcurementType() != ReportConst.EMPTY_VAL))
				|| ((getScProcessRptQuotationNo() != null) && (getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL))
				|| ((getScCommonStatus() != null) && (getScCommonStatus() != ReportConst.EMPTY_VAL))
				|| ((getScSimpleQuoteStatus() != null) && (getScSimpleQuoteStatus() != ReportConst.EMPTY_VAL))
				|| ((getScPurchaseInDefaultStatus() != null) && (getScPurchaseInDefaultStatus() != ReportConst.EMPTY_VAL))
				|| ((getScRequestNoteStatus() != null) && (getScRequestNoteStatus() != ReportConst.EMPTY_VAL))
				|| ((getScLAPRNPURCHDEFAULTGroup() != null) && (getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL))				
				|| ((getScPIDNo() != null) && (getScPIDNo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPType() != null) && (getScContractOrPKPType() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPNo() != null) && (getScContractOrPKPNo() != ReportConst.EMPTY_VAL))
				|| ((getScContractOrPKPName() != null) && (getScContractOrPKPName() != ReportConst.EMPTY_VAL))
				|| ((getScRequestQuoteNo() != null) && (getScRequestQuoteNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getScCreatedToMonth() == null)
					&& (this.getScCreatedFromMonth() != null)
					|| (this.getScCreatedToMonth() != null)
					&& (this.getScCreatedFromMonth() == null)) {
				throw new EpException(MessageConst.ERROR_REPORT_780004);
			}
			if ((this.getScCreatedToMonth() != null)
					&& (this.getScCreatedFromMonth() != null)) {
				if ((this.getScCreatedToYear() != null)
						&& (this.getScCreatedFromYear() != null)) {
					if (Integer.parseInt(this.getScCreatedToYear()) < Integer
							.parseInt(this.getScCreatedFromYear())) {
						throw new EpException(MessageConst.ERROR_REPORT_780008);
					} else if (Integer.parseInt(this.getScCreatedToYear()) == Integer
							.parseInt(this.getScCreatedFromYear())) {
						if (Integer.parseInt(this.getScCreatedToMonth()) < Integer
								.parseInt(this.getScCreatedFromMonth())) {
							throw new EpException(MessageConst.ERROR_REPORT_780008);
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
			if((this.getReportValue() != null)
					&& (this.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SC_SUMMARY_REQUEST_NOTE_S1_RPT_VAL))){
				if ((this.getScREQUESTNOTESUMMARYGroup() != null)
						&& (this.getScREQUESTNOTESUMMARYGroup() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScREQUESTNOTESUMMARYGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
									+ this.getScREQUESTNOTESUMMARYGroup());
				}
			}
			
			if ((this.getScLAPRNPURCHDEFAULTGroup() != null)
					&& (this.getScLAPRNPURCHDEFAULTGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScLAPRNPURCHDEFAULTGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScLAPRNPURCHDEFAULTGroup());
			}
			if ((this.getScSimpleQuoteDtlGroup() != null)
					&& (this.getScSimpleQuoteDtlGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScSimpleQuoteDtlGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScSimpleQuoteDtlGroup());
			}
			if ((this.getScCommonStatus() != null)
					&& (this.getScCommonStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScCommonStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScCommonStatus());
			}
			if ((this.getScPurchaseInDefaultStatus() != null)
					&& (this.getScPurchaseInDefaultStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScPurchaseInDefaultStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScPurchaseInDefaultStatus());
			}
			if ((this.getScSimpleQuoteStatus() != null)
					&& (this.getScSimpleQuoteStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScSimpleQuoteStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScSimpleQuoteStatus());
			}
			if ((this.getScRequestNoteStatus() != null)
					&& (this.getScRequestNoteStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setScRequestNoteStatusVar(ReportConst.SC_AMP_STATUS_EQ
						+ this.getScRequestNoteStatus());
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
				if((getManagedBean().getReportValue() != null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL))){
				  simpleQuoteNoValidation();
				}
				rptGenBean
						.setScProcessRptQuotationNoVar(ReportConst.SC_AMP_QT_NO_EQ
								+ this.getScProcessRptQuotationNo());
			}
			if ((this.getScProcurementType() != null)
					&& (this.getScProcurementType() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScProcurementTypeVar(ReportConst.SC_AMP_PROC_TYPE_EQ
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
			if((this.getReportValue() != null)
					&& (this.getReportValue().trim()
							.equalsIgnoreCase(ReportConst.SC_PURCHASE_ITEM_INQUIRY_FINAL_S1_RPT_VAL))){
				if ((this.getScPIDtlsPrcRptGroup() != null)
						&& (this.getScPIDtlsPrcRptGroup() != ReportConst.EMPTY_VAL)) {
					rptGenBean
							.setScPIDtlsPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
									+ this.getScPIDtlsPrcRptGroup());
				}
			}
			if ((this.getScPrcRptGroup() != null)
					&& (this.getScPrcRptGroup() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setScPrcRptGroupVar(ReportConst.SC_AMP_PRCESS_RPT_GROUP_EQ
								+ this.getScPrcRptGroup());
			}
			String userRoleLogin = ReportConst.EMPTY_VAL;
			if (approverNayakTarafList.size()>0) {
				userRoleLogin = ReportConst.APPROVERNT;
			} else if ((authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.RN_APPROVER))&&(this.getModuleValue().equalsIgnoreCase(
					ReportConst.DP_MOD_VAL))) {
				userRoleLogin = ReportConst.APPROVER;
			} else if ((authoriztionList.contains(ReportConst.BUDGET_APPROVER)||authoriztionList.contains(ReportConst.BPK_APPROVER)||authoriztionList.contains(ReportConst.COMMITTEE_APPROVER)||authoriztionList.contains(ReportConst.PUBLICATION_APPROVER))&&((this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_SELECTION)))) {
				userRoleLogin = ReportConst.APPROVER;
			}else if (authoriztionList.contains(ReportConst.DESK_OFFICER)&&((this.getModuleValue().equalsIgnoreCase(
					ModuleCodeConst.SUPPLIER_SELECTION)))) {
				userRoleLogin = ReportConst.DESKOFFR;
			} else if (authoriztionList.contains(ReportConst.REQUISITIONER)&&(this.getModuleValue().equalsIgnoreCase(
							ReportConst.DP_MOD_VAL))) {
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
						+ rptGenBean.getScPurchaseInDefaultStatusVar()
						+ rptGenBean.getScSimpleQuoteStatusVar()
						+ rptGenBean.getScRequestNoteStatusVar()
						+ rptGenBean.getScLAPRNPURCHDEFAULTGroupVar()
						+ rptGenBean.getScSimpleQuoteDtlGroupVar()
						+ rptGenBean.getScREQUESTNOTESUMMARYGroupVar()
						+ rptGenBean.getScPIDNoVar()
						+ rptGenBean.getScContractOrPKPTypeVar()
						+ rptGenBean.getScContractOrPKPNoVar()
						+ rptGenBean.getScContractOrPKPNameVar()
						+ rptGenBean.getScRequestQuoteNoVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}
	/*
	 * Generate CM module report
	 */
	public void generateCMReport() {
		if (((this.getCmExtention() != null) && (this.getCmExtention() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionCode() != null) && (this.getCmExtentionCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemName() != null) && (this.getCmItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemType() != null) && (this.getCmItemType() != ReportConst.EMPTY_VAL))
				/*|| ((this.getCmItemStatus() != null) && (this.getCmItemStatus() != ReportConst.EMPTY_VAL))*/
				|| ((this.getCmUnspscCode() != null) && (this.getCmUnspscCode() != ReportConst.EMPTY_VAL))
				|| ((this.getCmUnspscTitle() != null) && (this.getCmUnspscTitle() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionType() != null) && (this.getCmExtentionType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionBrand() != null) && (this.getCmExtentionBrand() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionMeasurment() != null) && (this.getCmExtentionMeasurment() != ReportConst.EMPTY_VAL))
				|| ((this.getCmExtentionColour() != null) && (this.getCmExtentionColour() != ReportConst.EMPTY_VAL))
				|| ((this.getCmCompanyName() != null) && (this.getCmCompanyName() != ReportConst.EMPTY_VAL))
				|| ((this.getCmRegistrationType() != null) && (this.getCmRegistrationType() != ReportConst.EMPTY_VAL))
				|| ((this.getCmItemCategory() != null) && (this.getCmItemCategory() != ReportConst.EMPTY_VAL))
				|| ((this.getCmProofIdentity() != null) && (this.getCmProofIdentity() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getCmEpNumber() != null) && (this.getCmEpNumber() != ReportConst.EMPTY_VAL))
				|| ((this.getCmBusinessRegNo() != null) && (this.getCmBusinessRegNo() != ReportConst.EMPTY_VAL))
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
			
			if ((this.getCmExtention() != null)
					&& (this.getCmExtention() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setCmExtentionVar(ReportConst.CM_AMP_EXTENSION_EQ
						+ this.getCmExtention());
			}
			if ((this.getCmGroupOne() != null)&& (this.getCmGroupOne() != ReportConst.EMPTY_VAL)) {
					rptGenBean.setCmGroupOneVar(ReportConst.CM_AMP_PGROUP_EQ
							+ this.getCmGroupOne());
			}			
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
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
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}

	/*
	 * Generate FL module report
	 */
	public void generateFLReport() {
		if (((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractNo() != null) && (this.getFlContractNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSst() != null) && (this.getFlSst() != ReportConst.EMPTY_VAL))
				|| ((this.getFlJabatan() != null) && (this.getFlJabatan() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplier() != null) && (this.getFlSupplier() != ReportConst.EMPTY_VAL))
				|| ((this.getFlStatus() != null) && (this.getFlStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDuration() != null) && (this.getFlDuration() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecievingOfficer() != null) && (this.getFlRecievingOfficer() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryAddress() != null) && (this.getFlDeliveryAddress() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRecNoteNo() != null) && (this.getFlRecNoteNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractType() != null) && (this.getFlContractType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlRequisitioner() != null) && (this.getFlRequisitioner() != ReportConst.EMPTY_VAL))
				|| ((this.getFlMtoContract() != null) && (this.getFlMtoContract() != ReportConst.EMPTY_VAL))
				|| ((this.getFlInvoiceNo() != null) && (this.getFlInvoiceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplierInvoiceNo() != null) && (this.getFlSupplierInvoiceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPaymentAdviceNo() != null) && (this.getFlPaymentAdviceNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeductionType() != null) && (this.getFlDeductionType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCrNo() != null) && (this.getFlCrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlApproverName() != null) && (this.getFlApproverName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDocumentType() != null) && (this.getFlDocumentType() != ReportConst.EMPTY_VAL))
				|| ((this.getFlCriteria() != null) && (this.getFlCriteria() != ReportConst.EMPTY_VAL))
				|| ((this.getFlScoring() != null) && (this.getFlScoring() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPkpNo() != null) && (this.getFlPkpNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlStopInstructionNo() != null) && (this.getFlStopInstructionNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlDeliveryOrderNo() != null) && (this.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlSupplierDeliveryOrderNo() != null) && (this.getFlSupplierDeliveryOrderNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemCode() != null) && (this.getFlItemCode() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPoAmount() != null) && (this.getFlPoAmount() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPrNo() != null) && (this.getFlPrNo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPmo() != null) && (this.getFlPmo() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItemName() != null) && (this.getFlItemName() != ReportConst.EMPTY_VAL))
				|| ((this.getFlItmType() != null) && (this.getFlItmType() != ReportConst.EMPTY_VAL))
				|| ((this.getFromDate() != null) && (this.getFromDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getToDate() != null) && (this.getToDate().toString() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractOrder() != null) && (this.getFlContractOrder() != ReportConst.EMPTY_VAL))
				|| ((this.getFlContractRequest() != null) && (this.getFlContractRequest() != ReportConst.EMPTY_VAL))
				|| ((this.getFlPurchaseOrderNo() != null) && (this.getFlPurchaseOrderNo() != ReportConst.EMPTY_VAL))) {
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPtjTotVar(ReportConst.FL_AMP_PTJ_NAME_EQ
						+ this.getCommonPtj());
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
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				/*Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				
				rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ
						+ retrieveMinistryName(orgprofileid));*/
				rptGenBean.setMinistryTotVar(ReportConst.FL_AMP_MINISTRY_EQ
						+ this.getCommonMinistry());
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
				rptGenBean.setInvoiceNoTotVar(ReportConst.FL_AMP_INV_NO_EQ
						+ this.getFlInvoiceNo());
			}
			if ((this.getFlSupplierInvoiceNo() != null)
					&& (this.getFlSupplierInvoiceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlSupplierInvoiceNoVar(ReportConst.FL_AMP_SUPPL_INV_NO_EQ
						+ this.getFlSupplierInvoiceNo());
			}
			
			if ((this.getFlPaymentAdviceNo() != null)
					&& (this.getFlPaymentAdviceNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlPaymentAdviceNoVar(ReportConst.FL_AMP_PAYMENT_ADV_NO_EQ
						+ this.getFlPaymentAdviceNo());
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
			if ((this.getFlStopInstructionNo() != null)
					&& (this.getFlStopInstructionNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlStopInstructionNoVar(ReportConst.FL_AMP_STOP_INSTR_NO_EQ
						+ this.getFlStopInstructionNo());
			}
			if ((this.getFlDeliveryOrderNo() != null)
					&& (this.getFlDeliveryOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setDeliveryOrderNoTotVar(ReportConst.FL_AMP_DELI_ORD_NO_EQ
								+ this.getFlDeliveryOrderNo());
			}
			if ((this.getFlSupplierDeliveryOrderNo() != null)
					&& (this.getFlSupplierDeliveryOrderNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean
						.setFlSupplierDeliveryOrderNoVar(ReportConst.FL_AMP_SUP_DELI_ORD_NO_EQ
								+ this.getFlSupplierDeliveryOrderNo());
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
				rptGenBean.setItemNameTotVar(ReportConst.FL_AMP_ITM_NAME_EQ
						+ this.getFlItemName());
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
			if ((this.getFlGroupSeventeen() != null)
					&& (this.getFlGroupSeventeen() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setFlGroupSeventeenVar(ReportConst.CT_AMP_GROUP_EQ
						+ this.getFlGroupSeventeen());
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
						+ rptGenBean.getFlSupplierInvoiceNoVar()
						+ rptGenBean.getFlPaymentAdviceNoVar()
						+ rptGenBean.getDeductionTypeTotVar()
						+ rptGenBean.getCrNoTotVar()
						+ rptGenBean.getApproverNameTotVar()
						+ rptGenBean.getDocumentTypeTotVar()
						+ rptGenBean.getCriteriaTotVar()
						+ rptGenBean.getScoringTotVar()
						+ rptGenBean.getPkpNoTotVar()
						+ rptGenBean.getFlStopInstructionNoVar()
						+ rptGenBean.getDeliveryOrderNoTotVar()
						+ rptGenBean.getFlSupplierDeliveryOrderNoVar()
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
						+ rptGenBean.getFlGroupSeventeenVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007); 
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}  
	/*
	 * Generate PP module report
	 */
	public void generatePPReport() {
		if (((this.getCommonPtj() != null) && (this.getCommonPtj() != ReportConst.EMPTY_VAL))
				|| ((this.getCommonMinistry() != null) && (this.getCommonMinistry() != ReportConst.EMPTY_VAL))
				|| ((this.getPpYear() != null) && (this.getPpYear() != ReportConst.EMPTY_VAL))) {
			if ((this.getCommonPtj() != null)
					&& (this.getCommonPtj() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPpPTJVar(ReportConst.FL_AMP_PTJ_NAME_EQ
						+ this.getCommonPtj());
			}
			if ((this.getCommonMinistry() != null)
					&& (this.getCommonMinistry() != ReportConst.EMPTY_VAL)) {
				Long orgprofileid = Long.parseLong(this.getCommonMinistry());
				
				rptGenBean
						.setPpMinistryVar(ReportConst.FL_AMP_MINISTRY_EQ
								+ retrieveMinistryName(orgprofileid));
			}
			if ((this.getPpYear() != null)
					&& (this.getPpYear() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPpYearVar(ReportConst.AMP_SM_YEAR_EQ
						+ this.getPpYear());
			}
			
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getPpPTJVar()
						+ rptGenBean.getPpMinistryVar()
						+ rptGenBean.getPpYearVar()			
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
	}
	/*
	 * Generate PM module report
	 */
	public void generatePMReport() {
		if (((this.getPmServiceType() != null) && (this.getPmServiceType() != ReportConst.EMPTY_VAL))
				|| ((this.getPmBusinessRegNo() != null) && (this.getPmBusinessRegNo() != ReportConst.EMPTY_VAL))
				|| ((this.getPmStatus() != null) && (this.getPmStatus() != ReportConst.EMPTY_VAL))
				|| ((this.getPmFinancialOrgName() != null) && (this.getPmFinancialOrgName() != ReportConst.EMPTY_VAL))) {
			if ((this.getPmServiceType() != null)
					&& (this.getPmServiceType() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmServiceTypeVar(ReportConst.PM_AMP_SERVICE_TYPE_EQ
						+ this.getPmServiceType());
			}
			if ((this.getPmBusinessRegNo() != null)
					&& (this.getPmBusinessRegNo() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmBusinessRegNoVar(ReportConst.PM_AMP_BIZREG_NO_EQ
						+ this.getPmBusinessRegNo());
			}
			if ((this.getPmStatus() != null)
					&& (this.getPmStatus() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmStatusVar(ReportConst.PM_AMP_STATUS_EQ
						+ this.getPmStatus());
			}
			if ((this.getPmFinancialOrgName() != null)
					&& (this.getPmFinancialOrgName() != ReportConst.EMPTY_VAL)) {
				rptGenBean.setPmFinancialOrgNameVar(ReportConst.PM_AMP_FINORG_NAME_EQ
						+ this.getPmFinancialOrgName());
			}
			rptGenBean.setUrlReportJSPName(getManagedBean().getReportJsp());
			if ((rptGenBean.getUrlReportJSPName() != null)
					&& (rptGenBean.getUrlReportJSPName() != ReportConst.EMPTY_VAL)) {
				setConstructedParamString(ReportConst.AMP_REPORT_NAME
						+ rptGenBean.getUrlReportJSPName()
						+ rptGenBean.getPmServiceTypeVar()
						+ rptGenBean.getPmBusinessRegNoVar()
						+ rptGenBean.getPmStatusVar()
						+ rptGenBean.getPmFinancialOrgNameVar()
						+ rptGenBean.getReportValueIdVar()
						+ rptGenBean.getUserIdVar());
			} else {
				throw new EpException(MessageConst.ERROR_REPORT_780007);
			}
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
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
	public void simpleQuoteNoValidation() {
		if ((this.getScProcessRptQuotationNo() != null)
				&& (this.getScProcessRptQuotationNo() != ReportConst.EMPTY_VAL)) {
			specialCharValidation();
		}
	}

	public void specialCharValidation() {
		String ptString = ReportConst.NUMERIC_CHAR_PATTERN_ONLY;
		Pattern pattern = Pattern.compile(ptString);
		Matcher match;
		if((getManagedBean().getReportValue() != null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL)||getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL)||getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL))){
			//if(this.getScProcessRptQuotationNo() != null){
			match = pattern.matcher(this.getScProcessRptQuotationNo());
		  //}
		}else {
			match = pattern.matcher(this.getScQuotationNo());
		}
		if (match.matches()) {
			if((getManagedBean().getReportValue() != null)&&(getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_S1_SIMPLE_QUOTE_PROCESS_RPT_VAL)||getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_SIMPLE_QUOTE_DTL_STS_COMPLD_S1_RPT_VAL)||getManagedBean().getReportValue().trim().equalsIgnoreCase(ReportConst.SC_SIMPLE_QUOTE_SMRY_BY_SUPPL_S1_RPT_VAL))){
				retrieveSimpleQuoteNo();
			}else {
				getQuotationNumber();
			}
			
			
		} else {
			this.setScQuotationNo(null);
			this.setScProcessRptQuotationNo(null);
			throw new EpException(MessageConst.ERROR_REPORT_780006);
		}
	}
	public void mofRegNoNumericValidation() {
		String ptString1 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern1 = Pattern.compile(ptString1);
		if((this.getSmMofRegNoFrom() != null)
				&& (this.getSmMofRegNoFrom() != ReportConst.EMPTY_VAL)){
			String mofRegNoFrom = null;
			if(this.getSmMofRegNoFrom().contains(ReportConst.PARAM_IFON)){
				mofRegNoFrom = this.getSmMofRegNoFrom().replace(ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);	
				if(mofRegNoFrom.contains(ReportConst.PARAM_SPACE)){
					mofRegNoFrom=mofRegNoFrom.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);	
				}
			}else{
				mofRegNoFrom = this.getSmMofRegNoFrom();
			}
						
			Matcher match1 = pattern1.matcher(mofRegNoFrom);
			if (match1.matches()) {
				//this.setSmMofRegNoFrom(mofRegNoFrom);
			} else {
				this.setSmMofRegNoFrom(null);
				this.setSmMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
		String ptString2 = ReportConst.NUMERIC_PATTERN_ONLY;
		Pattern pattern2 = Pattern.compile(ptString2);
		if((this.getSmMofRegNoTo() != null)
				&& (this.getSmMofRegNoTo() != ReportConst.EMPTY_VAL)){
			String mofRegNoTo = null;
			if(this.getSmMofRegNoTo().contains(ReportConst.PARAM_IFON)){
				mofRegNoTo = this.getSmMofRegNoTo().replace(ReportConst.PARAM_IFON, ReportConst.EMPTY_VAL);	
				if(mofRegNoTo.contains(ReportConst.PARAM_SPACE)){
					mofRegNoTo=mofRegNoTo.replace(ReportConst.PARAM_SPACE, ReportConst.EMPTY_VAL);	
				}
			}else{
				mofRegNoTo = this.getSmMofRegNoTo();
			}
						
			Matcher match2 = pattern2.matcher(mofRegNoTo);
			if (match2.matches()) {
				//this.setSmMofRegNoTo(mofRegNoTo);
			} else {
				this.setSmMofRegNoFrom(null);
				this.setSmMofRegNoTo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780018);
			}
		}
	}
	public void ValidateMofRegNo(String fromMofNo, String toMofNo) {
		Long fromMofNoLng=Long.parseLong(fromMofNo);
		Long toMofNoLng=Long.parseLong(toMofNo);		
		if(fromMofNoLng>toMofNoLng){
					throw new EpException(MessageConst.ERROR_REPORT_780019);
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
					throw new EpException(MessageConst.ERROR_REPORT_780005);
				}
			} else {
				this.setScQuotationNo(null);
				throw new EpException(MessageConst.ERROR_REPORT_780005);
			}
		}
	}

	/*
	 * Clear report criteria
	 */
	public void resetReportForm() {		
		assignNewCompanyExperience(new ArrayList<SmSupplierCategory>());
		getManagedBean().getRptGenBean().setCmMofRegView(false);
		getManagedBean().getRptGenBean().setCmBasicRegView(false);
		getManagedBean().getRptGenBean().setSmMofRegView(false);
		getManagedBean().getRptGenBean().setSmBasicRegView(false);
		getManagedBean().getRptGenBean().setCmExtentionBrandView(false);
		getManagedBean().getRptGenBean().setCmExtentionTypeView(false);
		getManagedBean().getRptGenBean().setCmExtentionMeasurmentView(false);
		getManagedBean().getRptGenBean().setCmExtentionColourView(false);
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
		this.setCommonPtj(null);
		this.setFlContractNo(null);
		this.setFlSst(null);
		this.setFlJabatan(null);
		this.setCommonMinistry(null);
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
		this.setFlStopInstructionNo(null);
		this.setFlInvoiceNo(null);
		this.setFlSupplierInvoiceNo(null);
		this.setFlPaymentAdviceNo(null);
		this.setFlDeliveryOrderNo(null);
		this.setFlSupplierDeliveryOrderNo(null);
		this.setFlItemCode(null);
		this.setFlPoAmount(null);
		this.setFlPrNo(null);
		this.setFlPmo(null);
		this.setFlItemName(null);
		this.setSmApplicationNo(null);
		if((allRoleCodesList!=null)&&(allRoleCodesList.contains(ReportConst.ROLE_CODE_EP_ADMIN)||allRoleCodesList.contains(ReportConst.ROLE_CODE_PTJ_ADMIN))){
				this.setSmZone(null);
		}
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
		this.setSmProcessingType(null);
		this.setSmReceiptNo(null);
		this.setSmProcessingOfficerName(null);
		this.setSmPaymentMethod(null);
		this.setSmFromMonth(null);
		this.setSmToMonth(null);
		this.setSmCompanyName(null);
		this.setSmRegistrationType(null);
		this.setSmCreatedFromYear(null);
		this.setSmCreatedToYear(null);
		this.setSmYearText(null);
		this.setSmMofRegNoFrom(null);
		this.setSmMofRegNoTo(null);
		this.setSmPaidUpCapitalFrm(null);
		this.setSmPaidUpCapitalTo(null);
		this.setSmPaidUpCapitalNaccum(null);		
		this.setSmRole(null);
		this.setSmEpNo(null);
		this.setSmRegistrationNo(null);
		this.setSmSupportingDocumentMode(null);
		this.setSmPersonnelRole(null);
		this.setSmSupportingDocumentStatus(null);
		this.setSmIdentificationType(null);
		this.setSmIdentificationNumber(null);
		this.setSmCancellationBy(null);
		this.setSmThresholdFromYear(null);
		this.setSmThresholdToYear(null);
		this.setSmRequestExpiryDate(null);
		this.setSmUserName(null);
		this.setSmProfileStatus(null);
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
		this.setScPurchaseInDefaultStatus(null);
		this.setScSimpleQuoteStatus(null);
		this.setScRequestNoteStatus(null);
		this.setScLAPRNPURCHDEFAULTGroup(null);
		this.setScSimpleQuoteDtlGroup(null);
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
		this.setCtPhysicalContractNo(null);
		this.setCtAgreement(null);
		this.setCtStatus(null);
		this.setCtStatusTwo(null);
		this.setCtComplete(null);
		this.setCtGroupOne(null);
		this.setCtGroupTwo(null);
		this.setCtGroupThree(null);
		this.setCtProductService(null);
		this.setCtLoanNo(null);
		this.setCtLOAPhysicalNo(null);
		this.setCtSubType(null);
		this.setCtUserType(null);		
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
		this.setFlGroupSeventeen(null);
		this.setPpMinistry(null);
		this.setPpPTJ(null);
		this.setPpYear(null);
		this.setMinistryName(null);
		this.setPmServiceType(null);
		this.setPmBusinessRegNo(null);
		this.setPmStatus(null);
		this.setPmFinancialOrgName(null);
		changeReportType();
		retrieveDates();
		applicationStatusDateChange();
	}

	protected void fileGenerateReport(String reportFile,
			String paramConstrString){
		StringBuilder gnrtdURL = new StringBuilder();
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_PREV_URL));
		gnrtdURL.append(paramConstrString);
		gnrtdURL.append(EpPropertiesUtils.getInstance().getProperty(
				EpPropertiesConst.EXC_NEXT_URL));
		HttpURLConnection urlConnection = null;
		URL obj;
		BufferedInputStream buf;
		try {
			obj = new URL(gnrtdURL.toString());
			log.info(gnrtdURL.toString());
			urlConnection = (HttpURLConnection) obj.openConnection();
			
			urlConnection.setConnectTimeout(290000);
			urlConnection.setReadTimeout(290000);
			new Thread(new InterruptThread(urlConnection)).start();
			
			buf = new BufferedInputStream(
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
		} catch (MalformedURLException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.addResponseCookie(org.primefaces.util.Constants.DOWNLOAD_COOKIE, ReportConst.RPT_TRUE, Collections.<String, Object>emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		}catch ( IllegalArgumentException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.addResponseCookie(org.primefaces.util.Constants.DOWNLOAD_COOKIE, ReportConst.RPT_TRUE, Collections.<String, Object>emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780009);
		}catch (IOException e) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			externalContext.addResponseCookie(org.primefaces.util.Constants.DOWNLOAD_COOKIE, ReportConst.RPT_TRUE, Collections.<String, Object>emptyMap());
			getManagedBean().getRptGenBean().setGenerateButtonView(false);
			throw new EpException(MessageConst.ERROR_REPORT_780020);
		}
		finally
	      {
			urlConnection.disconnect();
	      }
		
	}
	

	public Map<String, List<SelectItem>> getModuleItemMap() {
		return moduleItemMap;
	}

	public void setModuleItemMap(Map<String, List<SelectItem>> moduleItemMap) {
		this.moduleItemMap = moduleItemMap;
	}
	
}