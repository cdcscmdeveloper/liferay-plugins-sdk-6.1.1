/*
 * 
 */
package my.ep.web.backingbean.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.type.pm.OrganizationType;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.FlReportConst;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.PpReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.FlReportsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "flReportsBackingBean")
@SessionScoped
public class FlReportsBackingBean extends ReportBaseBackingBean<FlReportsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_FLREPORT = "/xhtml/reports/new/fl/flreportsmain.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(FlReportsBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		
		retrieveReportCategoryListByModuleCode(ModuleCodeConst.ORDER_FULFILLMENT
				.toUpperCase());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_FLREPORT);
	}
	
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase(),
				reportCategory);
	}
	
	public void onChangeReportName(){

		log.info("onChangeReportName");	
		
		resetAction();
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		
//		if (PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL.equals(getManagedBean()
//				.getReportCode()) ||
//				PmReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL.equals(getManagedBean()
//						.getReportCode())) {	
//			setCurrentActiveOrg();
//			retrieveOrgTypeMap();
//			this.getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_FEDERAL_GOVT);
//			retrieveOrgTypeAndOrgName();
//		}
	}
	
	public void generateFlReport(){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);		
		
		RPTRequestResDTO resDTO = rptRequestSO.generateReport(
				constructFlParamStrings(), 
				getManagedBean().getReportCode(), 
				null, 
				getFacesScopes().getUserSessionBean().getUserId(), 
				false, 
				getManagedBean().isTempAccess());	
		getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
		RequestContext.getCurrentInstance().execute("confirmationId.show()");
		
	
	}
	
	public void resetAction() {

	}
	
	/**
	 * The method used for generating a list of available display type of status
	 * as selection.
	 * 
	 * @return the selection list of display type
	 */
	public SelectItem[] getStatusOptions() {
		return PmCommonUtil.getRecordStatusOptions();
	}
	
	public SelectItem[] getPmZoneList() {
		return PmCommonUtil.getPmZoneListOptions();
	}
	
	private String constructFlParamStrings(){
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();	
		
		map.put(FlReportConst.PARAM_KEY_REPORT, getManagedBean().getReportName());
		map.put(FlReportConst.PARAM_FL_REPORT_ID_EQ, getManagedBean().getReportCode());
		
		
		return constructParameterString(map);
	}
	
	/**
	 * Construct ministry condition fields.
	 * 
	 * @return the string
	 */
	public String constructMinistryConditionFields() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}

	/**
	 * Construct pegwai pengawal condition fields.
	 * 
	 * @return the string
	 */
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
		if (this.getManagedBean().getSelectedMinistry() != null) {
			sb.append(this.getManagedBean().getSelectedMinistry()
					.getOrgProfileId());
		}

		return sb.toString();
	}

	/**
	 * Construct kumpulan ptj condition fields.
	 * 
	 * @return the string
	 */
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
		if (this.getManagedBean().getSelectedPegawaiPengawal() != null) {
			sb.append(this.getManagedBean().getSelectedPegawaiPengawal()
					.getOrgProfileId());
		}

		return sb.toString();
	}

	/**
	 * Construct ptj condition fields.
	 * 
	 * @return the string
	 */
	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil
				.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		if (this.getManagedBean().getSelectedKumpulanPTJ() != null) {
			sb.append(this.getManagedBean().getSelectedKumpulanPTJ()
					.getOrgProfileId());
		}
		return sb.toString();
	}
	
	public void onChangeMinistry(BaseEntity entity) {
		getManagedBean().setSelectedPegawaiPengawal(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
	}

	public List<String> generateCommonYearList() {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
		}
		List<String> yearList = new ArrayList<String>();
		if (year != null) {
			year = year + 1;
			for (int i = year - 7; i <= year; i++) {
				yearList.add(Integer.toString(i));
			}
		}
		return yearList;
	}

	public void generatePpReport() {
		IRPTRequestSO rptRequestSO = null;
		
		if(StringUtils.isBlank(getManagedBean().getSelectedYear())){
			throw new EpException(MessageConst.ERROR_REPORT_780030);
		}
		
		try {
			rptRequestSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IRPTRequestSO.class);
			RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
			reqDTO.setParametersMap(constructParamMap());
			reqDTO.setDefaultOutputType(getManagedBean().getDefaultOutputType());
			reqDTO.setTempAccess(getManagedBean().isTempAccess());
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId()
					.toString() : "");
			RequestContext.getCurrentInstance()
					.execute("confirmationId.show()");
			
			//this.getManagedBean().setReportUrl(resDTO.getReportUrl());
			
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		} finally {
			rptRequestSO = null;
		}
	}

	private LinkedHashMap<String, String> constructParamMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		if (getManagedBean().getSelectedMinistry() != null) {
			map.put(PpReportConst.PARAM_PP_MINISTRY, getManagedBean()
					.getSelectedMinistry().getOrgProfileId().toString());
		}

		if (getManagedBean().getSelectedPegawaiPengawal() != null) {
			map.put(PpReportConst.PARAM_PP_PEGAWAI_PEGAWAL, getManagedBean()
					.getSelectedPegawaiPengawal().getOrgProfileId().toString());
		}

		if (getManagedBean().getSelectedKumpulanPTJ() != null) {
			map.put(PpReportConst.PARAM_PP_KUMPULAN_PTJ, getManagedBean()
					.getSelectedKumpulanPTJ().getOrgProfileId().toString());
		}
		
		if (getManagedBean().getSelectedPTJ() != null) {
			map.put(PpReportConst.PARAM_PP_PTJ, getManagedBean()
					.getSelectedPTJ().getOrgProfileId().toString());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getSelectedYear())) {
			map.put(PpReportConst.PARAM_PP_YEAR, getManagedBean()
					.getSelectedYear());
		}
		
		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(PmReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			map.put(PmReportConst.PARAM_REPORT_ID, getManagedBean()
					.getReportCode());
			map.put(PmReportConst.PARAM_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());

		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}
		
		return map;
	}
	
	
}