/*
 * 
 */
package my.ep.web.backingbean.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.role.IRoleSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.EpPropertiesConst; //#28833 -shafie
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.ProfileManagementStatusConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.authorization.role.RoleReqDTO;
import my.ep.dto.pm.authorization.role.RoleResDTO;
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmRoleDesc;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils; //#28833 -shafie
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.PmReportsManagedBean;
import my.ep.web.util.DateUtils;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "pmReportsBackingBean")
@ViewScoped
public class PmReportsBackingBean extends
		ReportBaseBackingBean<PmReportsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_PMREPORT = "/xhtml/reports/new/pm/pmreportsmain.xhtml";	
	private static final String CAP_CHAR = "^";  // shafie - #28833

	private static Map<Long, Object> orgTypeMapper;
	
	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PmReportsBackingBean.class);

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportListByAuthorization(ModuleCodeConst.PROFILE_MANAGEMENT
				.toUpperCase());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_PMREPORT);
	}

	public String getLanguage() {
		return getFacesScopes().getUserLocale().getLanguage();
	}
	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}

	// public List<SelectItem> retrieveReportListByAuthorization() {
	// log.info("ReportBackingBean-retrieveReportListByAuthorization");
	// return
	// this.retrieveReportListByAuthorization(ModuleCodeConst.PROFILE_MANAGEMENT
	// .toUpperCase());
	// }

	public void onChangeReportName() {
		log.info("onChangeReportName");
		resetAction();
		
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		
		if (PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL.equals(getManagedBean()
				.getReportCode()) ||
				PmReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL.equals(getManagedBean()
						.getReportCode())) {	
			setCurrentActiveOrg();
			retrieveOrgTypeMap();
			this.getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_FEDERAL_GOVT);
			retrieveOrgTypeAndOrgName();
		}
		else if(PmReportConst.PM_NO_OF_CP_AND_MR_ACCESS_APPLICATION_RPT_VAL.equals(
				getManagedBean().getReportCode())) {
			retrieveOrgTypeMap();
			this.getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_CP_MR);
		//#28833 - shafie -start
		}else if(PmReportConst.PM_LIST_OF_PTJ_ALLOWED_TO_APPROVE_OTHER_PTJ_PROCUREMENT.equals(
				getManagedBean().getReportCode())) {
			setCurrentActiveOrg(); 
		}
		//#28833 - shafie -end
		
		
	}

	public void generatePmReport() {
		//#29486 - shafie -start
		if(PmReportConst.PM_MONTHLY_USER_AND_ROLE_RPT_VAL.equals(
				getManagedBean().getReportCode())){
			pickMonthYear();
		}
		//#29486 - shafie -end
		
		IRPTRequestSO rptRequestSO = null;
		try {
			LinkedHashMap<String, String> map = constructParamMap();
			rptRequestSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IRPTRequestSO.class);
			RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
			reqDTO.setParametersMap(map);
			reqDTO.setDefaultOutputType(getManagedBean().getDefaultOutputType());
			reqDTO.setTempAccess(getManagedBean().isTempAccess());
			RPTRequestResDTO resDTO = rptRequestSO.generateReport(reqDTO);
			getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId()
					.toString() : "");
			RequestContext.getCurrentInstance()
			.execute("confirmationId.show()");
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			rptRequestSO = null;
		}

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

	private LinkedHashMap<String, String> constructParamMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		// To cater 'All'
		boolean ignoreAtLeastOne = false;
		switch (getManagedBean().getReportCode()) {
		case PmReportConst.PM_LIST_OF_FINACIAL_INSTITUTIONS_RPT_VAL:
			constructPm001Map(map);
			break;
		case PmReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL:
			constructPm002Map(map);
			break;
		case PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL:
			constructPm003Map(map);
			break;
		case PmReportConst.PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL:
			constructPm004Map(map);
			break;
		case PmReportConst.PM_LIST_OF_EXEMPTED_ITEMS_BY_SUPPLIER_RPT_VAL:
			constructPm005Map(map);
			break;
		case PmReportConst.PM_LIST_OF_EXEMPTED_ITEMS_RPT_VAL:
			constructPm006Map(map);
			break;
		case PmReportConst.PM_LIST_OF_EXEMPTED_CONTRACTED_ITEMS_RPT_VAL:
			constructPm007Map(map);
			break;
		case PmReportConst.PM_BPK_USER_PROFILE_RPT_VAL:
			ignoreAtLeastOne = true;
			constructPm008Map(map);
			break;
		case PmReportConst.PM_BPK_ADMIN_PROFILE_RPT_VAL:
			ignoreAtLeastOne = true;
			constructPm009Map(map);
			break;
		case PmReportConst.PM_NO_OF_CP_AND_MR_ACCESS_APPLICATION_RPT_VAL:
			constructPm010Map(map);
			break;
			//#28833 shafie  start
		case PmReportConst.PM_LIST_OF_PTJ_ALLOWED_TO_APPROVE_OTHER_PTJ_PROCUREMENT:
			constructPm012Map(map);
			break;
			//#28833 shafie  end
		//shafie - #29486 -start
		case PmReportConst.PM_MONTHLY_USER_AND_ROLE_RPT_VAL:
			constructPm013Map(map);
			break;
		//shafie - #29486 - end
		default:
			throw new EpException("Report not found.");

		}

		if (MapUtils.isNotEmpty(map) || ignoreAtLeastOne) {
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

	// Financial Institution List
	private void constructPm001Map(LinkedHashMap<String, String> map) {
		if (StringUtils.isNotBlank(getManagedBean().getServiceType())) {
			map.put(PmReportConst.PARAM_PM_SERVICE_TYPE, getManagedBean()
					.getServiceType());
		}

		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getPmFinancialOrg() != null) {
			map.put(PmReportConst.PARAM_PM_FINORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmFinancialOrg()
					.getFinOrgName());
			map.put(PmReportConst.PARAM_PM_BIZREG_NO, getManagedBean()
					.getSelectedOrgProfile().getPmFinancialOrg()
					.getBizRegNumber());
		}

		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_SUPP_PROFILE_STATUS,
					getManagedBean().getStatus().toString());
		}
	}

	// Organization list
	private void constructPm002Map(LinkedHashMap<String, String> map) {

		map.put(PmReportConst.PARAM_PM_ORG_CATEGORY,
				String.valueOf(getManagedBean().getSearchOrgCategory()));

		if (getManagedBean().getSelectedOrgType() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_PARAM_ID, getManagedBean()
					.getSelectedOrgType().getParameterId().toString());
		}

		if (getManagedBean().getSelectedOrgProfile() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgName());
			map.put(PmReportConst.PARAM_PM_ORG_CODE, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgCode());
		}
		
		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_STATUS, getManagedBean().getStatus()
					.toString());
		}
		
		// shafie #28833 start
		if (getManagedBean().getSelectedOrgType().getParameterId() == PmReportConst.ORG_TYPE_MINISTRY ||
		getManagedBean().getSelectedOrgType().getParameterId() == PmReportConst.ORG_TYPE_KUMPULAN_PTJ ||
		getManagedBean().getSelectedOrgType().getParameterId() == PmReportConst.ORG_TYPE_PTJ) {
			if (getManagedBean().isGovtSeller()){
				String PG2G = "1";
				map.put(PmReportConst.PARAM_PM_G2G, PG2G);
			}
		} 
		// shafie #28833 - end
	}

	// User Profile List
	private void constructPm003Map(LinkedHashMap<String, String> map) {
		
		if (getManagedBean().getSelectedPmUser() != null) {			
			map.put(PmReportConst.PARAM_PM_USERNAME, getManagedBean()
					.getSelectedPmUser().getUserName());
			map.put(PmReportConst.PARAM_PM_IDENTIFICATION_NO, getManagedBean()
					.getSelectedPmUser().getIdentificationNo());
		}
		
		if (getManagedBean().getSelectedOrgType() != null) {			
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_PARAM_ID, getManagedBean()
					.getSelectedOrgType().getParameterId().toString());
		}
		
		// 28239 - start
		
		log.info("ParameterCode" + this.getManagedBean().getSelectedOrgType().getParameterCode());
	
	if (this.getManagedBean().getSelectedOrgType().getParameterCode().equals("FFC")){
		log.info("ORG_TYPE_FACTORING");
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getPmFinancialOrg() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmFinancialOrg()
					.getFinOrgName());
		}

	}else
		
	if (this.getManagedBean().getSelectedOrgType().getParameterCode().equals("SUP")){
		log.info("ORG_TYPE_SUPPLIER");
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null){
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}
		
	}else{	
		    log.info("PARAM_PM_ORG_NAME");
		    
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getPmOrgValidity().getOrgName() != null) { 
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgName());
			map.put(PmReportConst.PARAM_PM_ORG_CODE, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgCode());
		}	
	}
	// 28239 - end

		if (StringUtils.isNotBlank(getManagedBean().getSelectedRoleCode())) {
			map.put(PmReportConst.PARAM_PM_ROLE_CODE, getManagedBean()
					.getSelectedRoleCode());
		}

		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_STATUS, getManagedBean().getStatus()
					.toString());
		}
	}

	// Govt Seller Org List
	private void constructPm004Map(LinkedHashMap<String, String> map) {
		if (getManagedBean().getSelectedOrgProfile() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgName());
			map.put(PmReportConst.PARAM_PM_ORG_CODE, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgCode());
		}

		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_STATUS, getManagedBean().getStatus()
					.toString());
		}
	}

	// Exempted Items List by Supplier
	private void constructPm005Map(LinkedHashMap<String, String> map) {
		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			map.put(PmReportConst.PARAM_PM_COMPANY_NAME, getManagedBean()
					.getSelectedOrgProfile().getSmSupplier().getCompanyName());
		}

		if (StringUtils.isNotBlank(getManagedBean().getBizRegNo())) {
			map.put(PmReportConst.PARAM_PM_BIZREG_NO, getManagedBean()
					.getBizRegNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getEpNo())) {
			map.put(PmReportConst.PARAM_PM_EP_NO, getManagedBean().getEpNo());
		}

		constructExemptionCommonFieldsToMap(map);
	}

	// Exempted Items List
	private void constructPm006Map(LinkedHashMap<String, String> map) {
		constructExemptionCommonFieldsToMap(map);
	}

	// Exempted Contract Items
	private void constructPm007Map(LinkedHashMap<String, String> map) {
		if (StringUtils.isNotBlank(getManagedBean().getContractNo())) {
			map.put(PmReportConst.PARAM_PM_CONTRACT_NO, getManagedBean()
					.getContractNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getContractPhyscialNo())) {
			map.put(PmReportConst.PARAM_PM_PHYSICAL_CONTRACT_NO,
					getManagedBean().getContractPhyscialNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getLoaNo())) {
			map.put(PmReportConst.PARAM_PM_LOA_NO, getManagedBean().getLoaNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getLoaPhyscialNo())) {
			map.put(PmReportConst.PARAM_PM_PHYSICAL_LOA_NO, getManagedBean()
					.getLoaPhyscialNo());
		}

		constructExemptionCommonFieldsToMap(map);
	}

	// BPK User Profile List
	private void constructPm008Map(LinkedHashMap<String, String> map) {
		if (StringUtils.isNotBlank(getManagedBean().getZoneName())) {
			map.put(PmReportConst.PARAM_PM_SUPP_ZONE, getManagedBean()
					.getZoneName());
		}
	}

	// BPK Admin Profile List
	private void constructPm009Map(LinkedHashMap<String, String> map) {
		if (StringUtils.isNotBlank(getManagedBean().getZoneName())) {
			map.put(PmReportConst.PARAM_PM_SUPP_ZONE, getManagedBean()
					.getZoneName());
		}

		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_SUPP_PROFILE_STATUS,
					getManagedBean().getStatus() == 1 ? "Active" : "Inactive");
		}
	}
	
	//#28833 -start
	//List of PTJ allowed to approve other PTJ's procurement
	private void constructPm012Map(LinkedHashMap<String, String> map) {
		if (getManagedBean().getSelectedMinistry() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_MINISTRY, 
					getManagedBean().getSelectedMinistry().getPmOrgValidity().getOrgName());
		}
		
		if (getManagedBean().getSelectedKumpulanPTJ() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_KUMPULAN_PTJ, 
					getManagedBean().getSelectedKumpulanPTJ().getPmOrgValidity().getOrgName());
		}
		
		if (getManagedBean().getSelectedPTJ() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_PTJ, 
					getManagedBean().getSelectedPTJ().getPmOrgValidity().getOrgName()); 
		}
	}	
	//shafie - #28833 -end
	
	//shafie - #29486 - start
	//Monthly User and Role Report
	private void constructPm013Map(LinkedHashMap<String, String> map) {
		if (getManagedBean().getSelectedMinistry() != null) {
			map.put(PmReportConst.PARAM_PM_MINISTRY, 
					getManagedBean().getSelectedMinistry().getPmOrgValidity().getOrgName());
		}else if(getManagedBean().getSelectedMinistry() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780059);}
		
		if (getManagedBean().getSelectedKumpulanPTJ() != null) {
			map.put(PmReportConst.PARAM_PM_KUMPULAN_PTJ, 
					getManagedBean().getSelectedKumpulanPTJ().getPmOrgValidity().getOrgName());
		}else if(getManagedBean().getSelectedKumpulanPTJ() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780063);}
		
		if (getManagedBean().getSelectedPTJ() != null) {
			map.put(PmReportConst.PARAM_PM_PTJ, 
					getManagedBean().getSelectedPTJ().getPmOrgValidity().getOrgName());
		}else if(getManagedBean().getSelectedPTJ() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780064);}
		
		if (getManagedBean().getStatus() != null) {
			map.put(PmReportConst.PARAM_PM_USER_STATUS, getManagedBean().getStatus()
					.toString());
		}
		
		if (getManagedBean().getmonthPicked() != null) {
			map.put(PmReportConst.PARAM_PM_MONTH, getManagedBean().getmonthPicked());
			
		}
		
	}
	
	public void pickMonthYear(){
		log.info("________________Masuk pickMonthYear________________");
        String month= FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()  
           .get("month");
        String year= FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()  
           .get("year");
        
        if(month != null || year != null){
        	changeStringToDate(month, year);
        }
   }
	
	public void changeStringToDate(String month, String year){
		
		String monthInString = null;
		if(month.equals("0")){monthInString = "Jan";}
		else if(month.equals("1")){monthInString = "Feb";}
		else if(month.equals("2")){monthInString = "Mar";}
		else if(month.equals("3")){monthInString = "Apr";}
		else if(month.equals("4")){monthInString = "May";}
		else if(month.equals("5")){monthInString = "Jun";}
		else if(month.equals("6")){monthInString = "Jul";}
		else if(month.equals("7")){monthInString = "Aug";}
		else if(month.equals("8")){monthInString = "Sep";}
		else if(month.equals("9")){monthInString = "Oct";}
		else if(month.equals("10")){monthInString = "Nov";}
		else if(month.equals("11")){monthInString = "Dec";}
		
		String dateInString = monthInString + year;
		getManagedBean().setmonthPicked(dateInString);
	
	}
	
	//shafie - #29486 - end
	
	//No of Company Profile and Market Research Access Application
	private void constructPm010Map(LinkedHashMap<String, String> map) {
		if (getManagedBean().getSelectedOrgType() != null && 
				getManagedBean().getSelectedOrgType().getParameterId() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_TYPE_PARAM_ID, getManagedBean()
					.getSelectedOrgType().getParameterId().toString());
		}

		if (getManagedBean().getSelectedOrgProfile() != null) {
			map.put(PmReportConst.PARAM_PM_ORG_NAME, getManagedBean()
					.getSelectedOrgProfile().getPmOrgValidity().getOrgName());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getApplicantName())) {
			map.put(PmReportConst.PARAM_PM_APPLICANT_NAME, getManagedBean().getApplicantName());
		}
		
		if (StringUtils.isNotBlank(getManagedBean().getSelectedAccessType())) {
			String accessType = getManagedBean().getSelectedAccessType();
			
			if((ParameterConst.DOCTYPE_COMPANY_PROFILE + " _ " + 
			ParameterConst.DOCTYPE_MARKET_RESEARCH).equalsIgnoreCase(accessType)){
				map.put(PmReportConst.PARAM_PM_ACCESS_TYPE, null);
			} else {
				map.put(PmReportConst.PARAM_PM_ACCESS_TYPE, accessType);
			}
		}
		
		
		
		if(getManagedBean().getSelectedApplicationStatus() != null){			
			if(getManagedBean().getSelectedApplicationStatus().longValue() == 
					ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.longValue()) {			
				if(map.get(PmReportConst.PARAM_PM_ACCESS_TYPE) != null && 
						StringUtils.isNotBlank(map.get(PmReportConst.PARAM_PM_ACCESS_TYPE)) && 
						map.get(PmReportConst.PARAM_PM_ACCESS_TYPE).equalsIgnoreCase(ParameterConst.DOCTYPE_COMPANY_PROFILE)) {
					map.put(PmReportConst.PARAM_PM_APPLICATION_STATUS_1, 
							String.valueOf(ProfileManagementStatusConst.PENDING_COMPANY_ACCESS_APPROVAL));
				} else {
					map.put(PmReportConst.PARAM_PM_APPLICATION_STATUS_1, 
							String.valueOf(getManagedBean().getSelectedApplicationStatus()));
				}
			} else {				
				map.put(PmReportConst.PARAM_PM_APPLICATION_STATUS, 
						String.valueOf(getManagedBean().getSelectedApplicationStatus()));
			}
				
		
		}
		
		if(getManagedBean().getFromDate() != null && getManagedBean().getToDate() != null) {
			dateValidation(getManagedBean().getFromDate(), getManagedBean().getToDate());
			map.put(PmReportConst.PARAM_PM_FROM_DATE, DateUtils.dateToString(
					CommonConstants.DEFAULT_DATE_FORMAT, getManagedBean()
					.getFromDate()));
			map.put(PmReportConst.PARAM_PM_TO_DATE, DateUtils.dateToString(
					CommonConstants.DEFAULT_DATE_FORMAT, getManagedBean()
					.getToDate()));
		} else if(getManagedBean().getFromDate() != null && getManagedBean().getToDate() == null) {
			throw new EpException(MessageConst.ERROR_REPORT_780055);
		} else if(getManagedBean().getFromDate() == null && getManagedBean().getToDate() != null) {
			throw new EpException(MessageConst.ERROR_REPORT_780054);
		}
		
		if(!map.isEmpty()) {
			//need to remove to indicate both CP and MR access type required by the report
			if(map.get(PmReportConst.PARAM_PM_ACCESS_TYPE) == null)
				map.remove(PmReportConst.PARAM_PM_ACCESS_TYPE);
			
			if (getManagedBean().getFromDate() == null && getManagedBean().getToDate() == null) {	
				Calendar now = Calendar.getInstance();
				Calendar calendar = new GregorianCalendar(now.get(Calendar.YEAR),0,1);	
				map.put(PmReportConst.PARAM_PM_FROM_DATE, DateUtils.dateToString(
						CommonConstants.DEFAULT_DATE_FORMAT, calendar.getTime()));

				map.put(PmReportConst.PARAM_PM_TO_DATE, DateUtils.dateToString(
						CommonConstants.DEFAULT_DATE_FORMAT, new Date()));
			} 
		}
		
		
		
		log.info("Mapping: " + map.toString());
	}
	
	
	// Item Code, Date From, Date To
	private void constructExemptionCommonFieldsToMap(
			LinkedHashMap<String, String> map) {
		dateValidation(getManagedBean().getFromDate(), getManagedBean().getToDate());
		
		if (getManagedBean().getSelectedUnspscItem() != null) {
			map.put(PmReportConst.PARAM_PM_ITEM_CODE, getManagedBean()
					.getSelectedUnspscItem().getUnspscCode());
			// map.put(PmReportConst.PARAM_PM_ITEM_NAME,
			// getManagedBean().getSelectedUnspscItem().getUnspscTitle());
		}

		if (getManagedBean().getFromDate() != null) {
			map.put(PmReportConst.PARAM_PM_FROM_DATE, DateUtils.dateToString(
					CommonConstants.DEFAULT_DATE_FORMAT, getManagedBean()
							.getFromDate()));
		}

		if (getManagedBean().getToDate() != null) {
			map.put(PmReportConst.PARAM_PM_TO_DATE, DateUtils.dateToString(
					CommonConstants.DEFAULT_DATE_FORMAT, getManagedBean()
							.getToDate()));
		}
	}

	public void resetAction() {
		getManagedBean().setZoneName(null);
		getManagedBean().setStatus(null);
		getManagedBean().setContractNo(null);
		getManagedBean().setContractPhyscialNo(null);
		getManagedBean().setLoaNo(null);
		getManagedBean().setLoaPhyscialNo(null);
		getManagedBean().setSelectedUnspscItem(null);
		getManagedBean().setFromDate(null);
		getManagedBean().setToDate(null);
		getManagedBean().setBizRegNo(null);
		getManagedBean().setEpNo(null);
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setServiceType(null);
		getManagedBean().setNotFirstRun(false);
		if(getManagedBean().getSearchOrgCategory() == PmReportConst.ORG_CAT_CP_MR)
			getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_CP_MR);
		else
			getManagedBean().setSearchOrgCategory(0);
		getManagedBean().setSelectedOrgType(null);
		getManagedBean().setSelectedPmUser(null);
		getManagedBean().setRoleDescResults(null);
		getManagedBean().setSelectedRoleDesc(null);
		getManagedBean().setCurrentActiveOrg(null);
		getManagedBean().setSelectedAccessType(null);
		getManagedBean().setApplicantName(null);
		getManagedBean().setSelectedApplicationStatus(null);
		//shafie - #28833 - start
		if(!getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()) && !getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()) && !isInternalAuditorUnderMinistry()){
			getManagedBean().setSelectedMinistry(null);
		}else if (getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()) || getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()) || isInternalAuditorUnderMinistry()){
			//#28833, #29486 - shafie start
			if(PmReportConst.PM_LIST_OF_PTJ_ALLOWED_TO_APPROVE_OTHER_PTJ_PROCUREMENT.equals(getManagedBean().getReportCode())) {
			setCurrentActiveOrg();
			}else {   
				getManagedBean().setSelectedMinistry(null);
			}	
			//#28833,#29486 - shafie end
		}
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
		//shafie - #28833 - end
	}

	public void autoSelectOrgType() {
		if (!getManagedBean().isNotFirstRun()) {
			log.info("autoSelectOrgType");
			RequestContext.getCurrentInstance().execute("autoSelectOrgType();");
			getManagedBean().setNotFirstRun(true);
		}

	}

	public void resetOrganisationSearchBean() {
		log.info("resetOrgSearchBean");
		this.getManagedBean().setSelectedOrgProfile(null);
		this.getManagedBean().setSelectedOrgType(null);
		getManagedBean().setNotFirstRun(false);
	}

	/**
	 * The method used for generating a list of available display type of org
	 * type as selection.
	 * 
	 */
	public SelectItem[] getOrgTypeDropdownValues() {
		ISearchBean<PmParameterDesc> searchBean = null;
		if (getManagedBean().getSearchOrgCategory() == PmReportConst.ORG_CAT_FEDERAL_GOVT) {
			searchBean = getOrgTypeSearchBean("orgTypeFederalList");
		} else if (getManagedBean().getSearchOrgCategory() == PmReportConst.ORG_CAT_OTHERS) {
			searchBean = getOrgTypeSearchBean("orgTypeOthersList");
		} else if(getManagedBean().getSearchOrgCategory() == PmReportConst.ORG_CAT_CP_MR) {
			searchBean = getOrgTypeSearchBean("orgTypeForCPandMR");
		}
		SelectItem[] items = null, filteredItems = null;
		if (searchBean != null) {
			searchBean.getSortColumns();
			items = getDropdownValuesBySearchBean(
					ParameterTypeConst.ORGANISATION_TYPES, searchBean);
			
			if(getManagedBean().getSearchOrgCategory() == PmReportConst.ORG_CAT_CP_MR) {
				int index = 1;
				filteredItems = new SelectItem[6];
				filteredItems[0] = items[0];
				for(int i = 1; i < items.length; i++) {
					IParameterSO parameterSO = (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
							ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
					ParameterReqDTO parameterReqDTO = new ParameterReqDTO();
					parameterReqDTO.setParameterId((Long)items[i].getValue());
					ParameterResDTO parameterResDTO = parameterSO.retrieveParameterById(parameterReqDTO);
					PmParameter pmParameter = parameterResDTO.getPmParameter();
					if(pmParameter.getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_GLC)||
							pmParameter.getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_STATUTORY_BODY)||
							pmParameter.getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_STATE_STATUTORY_BODY)||
							pmParameter.getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_LOCAL_AUTHORITY)||
							pmParameter.getParameterCode().equalsIgnoreCase(ParameterConst.ORG_TYPE_STATE_GOVT)) {
						filteredItems[index++] = items[i];
					}
				}
				return filteredItems;
			}
		}

		return items;
	}
	
	private ISearchBean<PmParameterDesc> getOrgTypeSearchBean(String componentId) {
		ISearchBean<PmParameterDesc> orgTypeSearchBean = super
				.createSearchBean(PmParameterDesc.class, componentId);
		return orgTypeSearchBean;
	}

	public void onChangeOrgType() {
		getManagedBean().setSelectedOrgProfile(null);

		if (this.getManagedBean().getSelectedOrgType().getParameterId() != null) {
			PmParameter pmParameter = (PmParameter) orgTypeMapper.get(this
					.getManagedBean().getSelectedOrgType().getParameterId());
			this.getManagedBean().getSelectedOrgType()
					.setParameterCode(pmParameter.getParameterCode());
		} else {
			this.getManagedBean().getSelectedOrgType().setParameterCode(null);
		}

		if (PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL.equals(getManagedBean()
				.getReportCode())) {
			getManagedBean().setSelectedPmUser(null);
			getManagedBean().setSelectedRoleDesc(null);

		}
	}

	public String constructConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("recordStatus^1");
		if (PmReportConst.PM_LIST_OF_ORGANIZATION_RPT_VAL
				.equals(getManagedBean().getReportCode())) {
			if (getManagedBean().getSelectedOrgType() != null) {
				sb.append("|orgTypeCode.parameterId^");
				sb.append(getManagedBean().getSelectedOrgType()
						.getParameterId());
				sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
						getManagedBean().getSelectedOrgType().getParameterCode(),
						getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
						//Internal Auditor (report) same authorization as Ministry Admin
						getFacesScopes().hasRoleUser(
								RoleCode.MINISTRY_ADMIN.value()) ||
								getFacesScopes().hasRoleUser(
										RoleCode.AUDITOR_INTERNAL.value()), getFacesScopes()
								.hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
						getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));
			}
		} else if (PmReportConst.PM_LIST_OF_GOVERNMENT_SELLERS_RPT_VAL
				.equals(getManagedBean().getReportCode())) {
			sb.append("|govtSeller^1|orgTypeCode.parameterCode^");
			sb.append(OrganizationType.DIVISION.value());
		} else if (PmReportConst.PM_LIST_OF_PROFILES_RPT_VAL
				.equals(getManagedBean().getReportCode())) {
			sb.append("|orgTypeCode.parameterId^");
			sb.append(getManagedBean().getSelectedOrgType().getParameterId());
			sb.append(OrgProfileUtil.retrieveAuthorizationLovSb(
					getManagedBean().getSelectedOrgType().getParameterCode(),
					getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
					//Internal Auditor (report) same authorization as Ministry Admin
					getFacesScopes().hasRoleUser(
							RoleCode.MINISTRY_ADMIN.value()) ||
							getFacesScopes().hasRoleUser(
									RoleCode.AUDITOR_INTERNAL.value()),
					getFacesScopes().hasRoleUser(
							RoleCode.JABATAN_ADMIN.value()),
					getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())));
		} else if (PmReportConst.PM_NO_OF_CP_AND_MR_ACCESS_APPLICATION_RPT_VAL
				.equals(getManagedBean().getReportCode())) {
			
				if(getManagedBean().getSelectedOrgType()
						.getParameterId() != null) {
					
					sb.append("|orgTypeCode.parameterId^");
					sb.append(getManagedBean().getSelectedOrgType()
							.getParameterId());
				}
				else {
					sb.append("|orgTypeCode.parameterCode^");
					sb.append(OrganizationType.GOVERNMENT_LINK_COMPANIES.value());
					sb.append(",");
					sb.append(OrganizationType.STATUTORY_BODIES.value());
					sb.append(",");
					sb.append(OrganizationType.STATUTORY_BODIES_STATE.value());
					sb.append(",");
					sb.append(OrganizationType.LOCAL_AUTHORITY.value());
					sb.append(",");
					sb.append(OrganizationType.STATE_GOVERNMENT.value());
					sb.append("^");
					sb.append(SearchOperator.OPERATOR_IN.value());
				}
	
		}

		return sb.toString();
	}
	
	//shafie - #28833 - start
	public String constuctConditionFieldsForPTJGroup() {

		StringBuffer parentOrgProfile = new StringBuffer();
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistry();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile
				.getOrgProfileId() : null;
		if (null != MinistryOrgProfileId) {
			String minLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_MINISTRY);
			String ptjGrpLevel = EpPropertiesUtils.getInstance().getProperty(
					EpPropertiesConst.LVL_ORG_PROFILE_PTJGROUP);
			if (minLevel != null && ptjGrpLevel != null) {
				int ministryLevel = Integer.valueOf(minLevel);
				int ptjGroupLevel = Integer.valueOf(ptjGrpLevel);
				int orgLevel = ministryLevel - ptjGroupLevel;

				for (int i = 0; i < orgLevel; i++) {
					parentOrgProfile.append("parentOrgProfile.");
				}
				parentOrgProfile.append("orgProfileId");
				parentOrgProfile.append(CAP_CHAR);
				parentOrgProfile.append(MinistryOrgProfileId);
			}
		} else {
			parentOrgProfile.append("  ");
		}
		return parentOrgProfile.toString();
	}
	
	public String constuctConditionFieldsForPTJ() {

		String parentOrgProfile = null;
		
		PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistry();
		Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile.getOrgProfileId() : null;
		PmOrgProfile groupOrgProfile = this.getManagedBean().getSelectedKumpulanPTJ();
		Long PTJGroupOrgProfileId = groupOrgProfile != null ? groupOrgProfile.getOrgProfileId() : null;
		if (null != PTJGroupOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
					+ CAP_CHAR + PTJGroupOrgProfileId + ("|groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		} else if (null != MinistryOrgProfileId) {
			parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
					+ CAP_CHAR + MinistryOrgProfileId + ("|groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		} else {
			parentOrgProfile = ("groupOrgType^") + ParameterConst.ORG_AUTH_TYPE_ORG_GROUP;
		}

		return parentOrgProfile;
	}
	
	
	
	public boolean isMinistryJabatanAdmin(){
		boolean Admin = false;
	
		if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value()) ||
			getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value()) ||
			isInternalAuditorUnderMinistry()){
			
			Admin = true;			
			setSelectedMinistryforMinistryJabatanAdmin();			
		}		
		
		return Admin;
	}
	
	public void setSelectedMinistryforMinistryJabatanAdmin(){
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);
		
		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);			
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);	
		
		if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
			getManagedBean().setSelectedMinistry(pmOrgProfile);
			this.getManagedBean().getPmSearchBean().setMinistry(pmOrgProfile);
		}
		else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
			getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
			this.getManagedBean().getPmSearchBean().setMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
		}
		else if (isInternalAuditorUnderMinistry()){
			getManagedBean().setSelectedMinistry(pmOrgProfile);
			this.getManagedBean().getPmSearchBean().setMinistry(pmOrgProfile);
		}
		
	}
	
	public boolean isInternalAuditorUnderMinistry(){
		boolean role = false;
		
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);
		
		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);			
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		
		if(getManagedBean().getCurrentActiveOrg().getGroupOrgType() !=null 
				&& ParameterConst.ORG_AUTH_TYPE_MINISTRY.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())){
			if (getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
				role = true;
			}
		}
		return role;
	}
	
	/**
	 * To construct Ministry condition fields for LOV
	 * 
	 * @return String
	 */
	public String constructMinistryConditionFields() {		
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.MINISTRY.value());
		sb.append("|recordStatus^1");
		return sb.toString();
	}
	
	/**
	 * To construct Kumpulan PTJ condition fields for LOV
	 * 
	 * @return String
	 */
	public String constructKumpulanPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DEPARTMENT.value());		
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getFromToSearchBeanParentString(OrganizationType.DEPARTMENT.value(),OrganizationType.MINISTRY.value()));
		sb.append("^");
		sb.append(getManagedBean().getSelectedMinistry().getOrgProfileId());
		return sb.toString();
	}
	
	/**
	 * To construct PTJ condition fields for LOV
	 * 
	 * @return String
	 */
	public String constructPTJConditionFields() {
		StringBuilder sb = new StringBuilder();
		sb.append("orgTypeCode.parameterCode^");
		sb.append(OrganizationType.DIVISION.value());
		sb.append("|recordStatus^1");
		sb.append("|");
		sb.append(OrgProfileUtil.getSearchBeanParentString(OrganizationType.DEPARTMENT.value()));
		sb.append("^");
		sb.append(getManagedBean().getSelectedKumpulanPTJ().getOrgProfileId());		
		return sb.toString();
	}
	
	/**
	 * Post Action Listener : to set PTJ in PmSearchBean after PTJ has been selected in LOV
	 */	
	public void updateSearchCriteria(BaseEntity entity) {
		log.info("updateSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setPtj(orgProfile);
	}
	
	/**
	 * Post Action Listener : to set Kumpulan PTJ in PmSearchBean after Kumpulan PTJ has been selected in LOV
	 */	
	public void updateKPTJSearchCriteria(BaseEntity entity) {
		log.info("updateKPTJSearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setKptj(orgProfile);

		resetPtjOrgProfile();
	}
		
	/**
	 * Post Action Listener : to set Ministry in PmSearchBean after Ministry has been selected in LOV
	 */	
	public void updateMinistrySearchCriteria(BaseEntity entity) {
		log.info("updateMinistrySearchCriteria");
		PmOrgProfile orgProfile = (PmOrgProfile) entity;
		this.getManagedBean().getPmSearchBean()
				.setMinistry(orgProfile);
		
		resetPtjGroupOrgProfile(); 
	}
	
	public void resetPtjGroupOrgProfile() {
		this.getManagedBean().setSelectedKumpulanPTJ(null);		
		this.getManagedBean().getPmSearchBean().setKptj(null);
		resetPtjOrgProfile();
	}
	
	
	public void resetPtjOrgProfile() {
		this.getManagedBean().setSelectedPTJ(null);		
		this.getManagedBean().getPmSearchBean().setPtj(null);
	}
	//shafie - #28833 - end
	
	//shafie #29486 Start
		public String constuctConditionFieldsForPTJEpEnabled() {

			String parentOrgProfile = null;
			
			PmOrgProfile ministryOrgProfile = this.getManagedBean().getSelectedMinistry();
			Long MinistryOrgProfileId = ministryOrgProfile != null ? ministryOrgProfile.getOrgProfileId() : null;
			PmOrgProfile groupOrgProfile = this.getManagedBean().getSelectedKumpulanPTJ();
			Long PTJGroupOrgProfileId = groupOrgProfile != null ? groupOrgProfile.getOrgProfileId() : null;
			if (null != PTJGroupOrgProfileId) {
				parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_PTJ_GROUP)
						+ CAP_CHAR + PTJGroupOrgProfileId + ("|epPtj^") + ParameterConst.FLAG_YES;
			} else if (null != MinistryOrgProfileId) {
				parentOrgProfile = OrgProfileUtil.getSearchBeanParentString(ParameterConst.ORG_TYPE_MINISTRY)
						+ CAP_CHAR + MinistryOrgProfileId + ("|epPtj^") + ParameterConst.FLAG_YES;
			} else {
				parentOrgProfile = ("epPtj^") + ParameterConst.FLAG_YES;
			}
	
			return parentOrgProfile;
		}
		//shafie #29486 end

	/**
	 * retrieve all org type into mapper
	 */
	private void retrieveOrgTypeMap() {
		log.info("....retrieveOrgTypeMap....");
		List<PmParameterDesc> parameterDescs = PmCommonUtil
				.retrieveFilteredParamDesc(
						ParameterTypeConst.ORGANISATION_TYPES, null, null);

		orgTypeMapper = new HashMap<Long, Object>();
		for (PmParameterDesc pmDesc : parameterDescs) {
			orgTypeMapper.put(pmDesc.getPmParameter().getParameterId(),
					pmDesc.getPmParameter());
		}

	}

	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.info("setCurrentActiveOrg");
		boolean isFactoring = false;
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		if (getFacesScopes().hasRoleUser(RoleCode.FACTORING_ADMIN.value())) {
			isFactoring = true;
		}
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), isFactoring);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);
		//#28833 -shafie -start
		
		if(getFacesScopes().hasRoleUser(RoleCode.MINISTRY_ADMIN.value())){
			getManagedBean().setSelectedMinistry(pmOrgProfile);
			this.getManagedBean().getPmSearchBean().setMinistry(pmOrgProfile);
		}
		else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())){
			getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
			this.getManagedBean().getPmSearchBean().setMinistry(retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
		}
		else if (isInternalAuditorUnderMinistry()){
			getManagedBean().setSelectedMinistry(pmOrgProfile);
			this.getManagedBean().getPmSearchBean().setMinistry(pmOrgProfile);
		}
		//#28833 -shafie -end
		
	}

	public void searchRoleMatrix() {
		IRoleSO roleSO = null;
		if (getManagedBean().getSelectedRoleOptionSearch() == null
				&& StringUtils.isNotBlank(getManagedBean()
						.getUserRoleNameSearch())) {
			throw new EpException(MessageConst.ERROR_100060);
		}

		try {
			ISearchBean<PmRoleDesc> searchBeanData = new SearchBean<PmRoleDesc>();

			if (StringUtils
					.isNotBlank(getManagedBean().getUserRoleNameSearch())) {
				searchBeanData
						.getDataFilterBeans()
						.add(new SearchFilterBean("roleName",
								SearchOperator.OPERATOR_LIKE.value(),
								getManagedBean().getUserRoleNameSearch(), true));
			}

			RoleReqDTO roleReqDTO = new RoleReqDTO();
			roleReqDTO.setLanguage(getFacesScopes().getUserLocale()
					.getLanguage());

			roleReqDTO.setSearchRoleBean(searchBeanData);

			roleReqDTO.setLanguage(getFacesScopes().getUserLocale()
					.getLanguage());
			roleReqDTO.setRoleCode(getRoleCode());
			roleReqDTO.setOrgTypeId(getManagedBean().getSelectedOrgType()
					.getParameterId());
			roleReqDTO.setBpk(true);
			roleSO = BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRoleSO.class);
			RoleResDTO resDto = roleSO
					.retrieveRoleDecsFromRoleMatrix(roleReqDTO);

			if (resDto != null && resDto.isSuccessful()) {
				getManagedBean().setRoleDescResults(
						resDto.getSearchRoleResultBean().getResultList());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			roleSO = null;
		}
	}

	public void resetRoleSearchAction() {
		getManagedBean().setSelectedRoleOptionSearch(null);
		getManagedBean().setUserRoleNameSearch(null);
		getManagedBean().getRoleDescResults().clear();
		searchRoleMatrix();
	}

	private String getRoleCode() {
		String roleCode = null;
		// Exclude Auditor role in order to bypass assignor role.
		// Means will retrieve all roles by org type
		if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN.value())) {
			roleCode = RoleCode.EP_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_1.value())) {
			roleCode = RoleCode.EP_ADMIN_1.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.EP_ADMIN_2.value())) {
			roleCode = RoleCode.EP_ADMIN_2.value();
		} else if (getFacesScopes()
				.hasRoleUser(RoleCode.MINISTRY_ADMIN.value())) {
			roleCode = RoleCode.MINISTRY_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.JABATAN_ADMIN.value())) {
			roleCode = RoleCode.JABATAN_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())) {
			roleCode = RoleCode.PTJ_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())) {
			roleCode = RoleCode.BPK_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			roleCode = RoleCode.UPS_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(
				RoleCode.PROCUREMENT_ADMIN.value())) {
			roleCode = RoleCode.PROCUREMENT_ADMIN.value();
		} else if (getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
			roleCode = RoleCode.SUPPORT_ADMIN.value();
		}

		return roleCode;
	}

	public void populateToRoleSaveObject() {
		if (getManagedBean().getSelectedRoleDesc() != null) {
			getManagedBean().setSelectedRoleCode(
					getManagedBean().getSelectedRoleDesc().getPmRole()
							.getRoleCode());
		}
	}

	public void selectPtjAction(PmOrgProfile orgProfile) {
		getManagedBean().setSelectedOrgProfile(orgProfile);
		getManagedBean().setSelectedPmUser(null);
		getManagedBean().setSelectedRoleDesc(null);
	}

	public String constructConditionForIdentification() {

		StringBuilder sb = new StringBuilder();
		sb.append("pmUserOrgs.pmOrgProfile.recordStatus^1");
		sb.append("|pmUserOrgs.pmOrgProfile.orgTypeCode.parameterCode^");
		sb.append(getManagedBean().getSelectedOrgType().getParameterCode());
		sb.append("|pmUserOrgs.pmOrgProfile.orgTypeCode.parameterType^");
		sb.append(ParameterTypeConst.ORGANISATION_TYPES);
		if (this.getManagedBean().getSelectedOrgType().getParameterCode()
				.equals(ParameterConst.ORG_TYPE_FACTORING)) {
			if (this.getManagedBean().getSelectedOrgProfile() != null
					&& this.getManagedBean().getSelectedOrgProfile()
							.getPmFinancialOrg() != null
					&& this.getManagedBean().getSelectedOrgProfile()
							.getPmFinancialOrg().getBizRegNumber() != null) {
				sb.append("|pmUserOrgs.pmOrgProfile.pmFinancialOrg.recordStatus^1");
				sb.append("|pmUserOrgs.pmOrgProfile.pmFinancialOrg.financialOrgId^");
				sb.append(getManagedBean().getSelectedOrgProfile()
						.getPmFinancialOrg().getFinancialOrgId());
			}
		} else if (this.getManagedBean().getSelectedOrgType()
				.getParameterCode().equals(ParameterConst.ORG_TYPE_SUPPLIER)) {
			if (this.getManagedBean().getSelectedOrgProfile() != null
					&& this.getManagedBean().getSelectedOrgProfile()
							.getSmSupplier() != null
					&& this.getManagedBean().getSelectedOrgProfile()
							.getSmSupplier().getSupplierId() != null) {
				sb.append("|pmUserOrgs.pmOrgProfile.smSupplier.recordStatus^1");
				sb.append("|pmUserOrgs.pmOrgProfile.smSupplier.supplierId^");
				sb.append(getManagedBean().getSelectedOrgProfile()
						.getSmSupplier().getSupplierId());
			}
		} else {
			if (this.getManagedBean().getSelectedOrgProfile() != null
					&& this.getManagedBean().getSelectedOrgProfile()
							.getPmOrgValidity() != null) {
				// sb.append("|pmUserOrgs.pmOrgProfile.pmOrgValidity.recordStatus^1");
				sb.append("|pmUserOrgs.pmOrgProfile.orgProfileId^");
				sb.append(getManagedBean().getSelectedOrgProfile()
						.getOrgProfileId());
			}else{
				sb.append("|pmUserOrgs.pmOrgProfile.");
				String authorization = OrgProfileUtil.retrieveAuthorizationLovSb(
						getManagedBean().getSelectedOrgType().getParameterCode(),
						getManagedBean().getCurrentActiveOrg().getOrgProfileId(),
						getFacesScopes().hasRoleUser(
								RoleCode.MINISTRY_ADMIN.value()) ||
								getFacesScopes().hasRoleUser(
										RoleCode.AUDITOR_INTERNAL.value()), getFacesScopes()
								.hasRoleUser(RoleCode.JABATAN_ADMIN.value()),
						getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value()));
				if(StringUtils.isNotBlank(authorization)){
					//remove the "|"
					sb.append(authorization.substring(1));
				}
				
			}
		}
		return sb.toString();
	}

	public void onChangeOrgLov(BaseEntity entity) {
		getManagedBean().setSelectedPmUser(null);
		getManagedBean().setSelectedRoleDesc(null);
	}

	public void onChangeUserLov(BaseEntity entity) {
		getManagedBean().setSelectedRoleDesc(null);
	}

	// Org Type and Org Name will be defaulted to the admin's organisation
	// Only applicable for PTJ Admin,BPK Admin and Support Admin
	private void retrieveOrgTypeAndOrgName() {
		if (getFacesScopes().hasRoleUser(RoleCode.PTJ_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.SUPPORT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.UPS_ADMIN.value())) {
			if (getManagedBean().getCurrentActiveOrg() != null) {
				getManagedBean()
						.setSelectedOrgType(
								getManagedBean().getCurrentActiveOrg()
										.getOrgTypeCode());
				getManagedBean().setSelectedOrgProfile(
						getManagedBean().getCurrentActiveOrg());
				getManagedBean().setDisabledOrgFields(true);
				if (getFacesScopes()
						.hasRoleUser(RoleCode.SUPPORT_ADMIN.value())) {
					this.getManagedBean().setSearchOrgCategory(PmReportConst.ORG_CAT_OTHERS);
				}
			}
		} else {
			getManagedBean().setDisabledOrgFields(false);
		}
	}
	
	private void dateValidation(Date dateFrom, Date dateTo){
		
		if (dateFrom == null || dateTo == null) {
			throw new EpException(MessageConst.ERROR_715050);
		}

		if ((dateFrom != null) && (dateTo != null)) {
			if (dateFrom.after(dateTo)) {
				throw new EpException(MessageConst.ERROR_REPORT_780008);
			}
		}
		if ((dateFrom != null) && (dateTo != null)) {
			if (dateTo.after(dateFrom)) {
				checkOneYearDifferent(dateFrom,dateTo);
			}
		}

	}
	
	/**
	 * The method used for generating a list of available meeting status
	 * as selection.
	 * 
	 * @return the selection list of meeting status
	 */
	public SelectItem[] getApplicationStatusDropdown() {
		SelectItem[] items = null;     
		IStatusSO statusSO = null;
		StatusResDTO resDTO = null;
		StatusReqDTO reqDTO = null;
		List<PmStatusDesc> newStatusDescList = new ArrayList<PmStatusDesc>();
		try {
			int i = 1;
			statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
			reqDTO = new StatusReqDTO();
			reqDTO.setModuleCode(ModuleCodeConst.PROFILE_MANAGEMENT);
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			resDTO = statusSO.retrieveStatusDescListByModule(reqDTO);
			
			for(PmStatusDesc pmStatusDesc: resDTO.getStatusDescList())
			{
				//PENDING_MARKET_RESEARCH_ACCESS_APPROVAL(75000) has the same description with PENDING_COMPANY_ACCESS_APPROVAL
				//For display purpose, by default it uses PENDING_MARKET_RESEARCH_ACCESS_APPROVAL
				if(pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.PENDING_MARKET_RESEARCH_ACCESS_APPROVAL.longValue() 
						|| pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.ACCESS_COMPLETED.longValue() 
						|| pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.ACCESS_REJECTED.longValue() 
						|| pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.PENDING_ACTIVATION.longValue() 
						|| pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.ACCESS_APPROVED.longValue() 
						|| pmStatusDesc.getPmStatus().getStatusId().longValue() == ProfileManagementStatusConst.ACCESS_CANCELLED.longValue())
				{
					newStatusDescList.add(pmStatusDesc);
					if(newStatusDescList.size() == 6)
						break;
				}
			}
			
			items = new SelectItem[newStatusDescList.size() + 1];
			items[0] = new SelectItem(null, ResourceBundleUtils.getMessage("lbl.txt.select", 
					ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale()));

			for (PmStatusDesc statusDesc : newStatusDescList) {
				items[i++] = new SelectItem(statusDesc.getPmStatus().getStatusId(), statusDesc.getStatusName());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(), e.getMessage()));
		}finally{
			resDTO = null;
			reqDTO = null;
			statusSO = null;
		}
		return items;
	}
}
