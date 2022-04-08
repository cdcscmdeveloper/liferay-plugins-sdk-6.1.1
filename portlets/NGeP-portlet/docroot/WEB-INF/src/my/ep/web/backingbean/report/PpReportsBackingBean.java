package my.ep.web.backingbean.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.service.rpt.IRPTRequestSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.report.RPTRequestReqDTO;
import my.ep.dto.pm.common.report.RPTRequestResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.BaseEntity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.persistence.entity.pm.common.PmProcurementCatType;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.util.pm.OrgProfileUtil;
import my.ep.web.constant.report.PmReportConst;
import my.ep.web.constant.report.PpReportConst;
import my.ep.web.constant.report.ReportConst;
import my.ep.web.managedbean.report.PpReportsManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.pp.common.PpCommonUtil;

// START #27472
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import my.ep.constant.EpPropertiesConst;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.AlphanumericSortUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.context.RequestContext;
import java.text.DecimalFormat;
import my.ep.web.util.CommonUtils;

//END #27472

@ManagedBean(name = "ppReportsBackingBean")
@ViewScoped
public class PpReportsBackingBean extends
		ReportBaseBackingBean<PpReportsManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_PPREPORT = "/xhtml/reports/new/pp/ppreportsmain.xhtml";
	// list of report where Ministry search field is mandatory
	public static final List<String> ppReportCodes = Arrays.asList(PpReportConst.PP_REPORTS_MINISTRY_IS_MANDATORY);	//[#27197]

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(PpReportsBackingBean.class);

	public PpReportsBackingBean() {
	}

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportListByAuthorization(ModuleCodeConst.PROCUREMENT_PLAN
				.toUpperCase());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_PPREPORT);
	}

	private FacesScopes getFacesScopes() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	public void onChangeReportName() {
		log.info("onChangeReportName");
		resetAction();

		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			
			//[#27197] - start
			log.info("[#27197] Report code: " + getManagedBean().getReportCode());
			log.info("[#27197] Is BPK_OFFICER? " + isBPKOfficer());
			log.info("[#27197] Is BPK_AUDITOR? " + isBPKAuditor());
			log.info("[#27197] Is AUDITOR_GENERAL? " + isGeneralAuditor());
			
			//set mandatory for Ministry search field for role BPK_OFFICER, BPK_AUDITOR, AUDITOR_GENERAL
			if(isBPKOfficer() || isBPKAuditor()	|| isGeneralAuditor()) {
				
				log.info("[#27197] Report code list: " + Arrays.toString(ppReportCodes.toArray()));
				if(ppReportCodes.contains(getManagedBean().getReportCode())) {
					log.info("[#27197] Report code exists...");
					this.getManagedBean().setMinistryMandatory(true);
				} else {
					log.info("[#27197] Report code not exists...");
					getManagedBean().setMinistryMandatory(false);
				}
			}
			//[#27197] - end
			
			retrieveReportValueFromDb();
		}

		setCurrentActiveOrg();

	}

	public void resetAction() {
		
		//start #27472
		if(!getManagedBean().isDisabledMinistryLov()){
			//getManagedBean().setCurrentActiveOrg(null);
			getManagedBean().setSelectedMinistry(null);
			getManagedBean().setDisabledMinistryLov(false);
		}
		
		if(!getManagedBean().isDisabledPegawaiLov()){
			getManagedBean().setSelectedPegawaiPengawal(null);
			getManagedBean().setDisabledPegawaiLov(false);			
		}
		
		if(!getManagedBean().isDisabledKumpulanPTJLov()){
			getManagedBean().setSelectedKumpulanPTJ(null);			
			getManagedBean().setDisabledKumpulanPTJLov(false);
		}
		
		if(!getManagedBean().isDisabledPTJLov()){
			getManagedBean().setSelectedPTJ(null);
			getManagedBean().setDisabledPTJLov(false);	
		}
		//end #27472
		
		getManagedBean().setSelectedYear(null);
		getManagedBean().setProcurementCatId(null);
		getManagedBean().setProcurementTypeId(null);
		getManagedBean().setStatus(null);
		getManagedBean().setDocumentType(ReportConst.PDF); //#27472 reset to pdf default
		
		//[#27197] - start
		//set mandatory for Ministry search field for role BPK_OFFICER, BPK_AUDITOR, AUDITOR_GENERAL
		if(isBPKOfficer() || isBPKAuditor()	|| isGeneralAuditor()) {
			
			log.info("[#27197] Report code list: " + Arrays.toString(ppReportCodes.toArray()));
			if(ppReportCodes.contains(getManagedBean().getReportCode())) {
				log.info("[#27197] Report code exists...");
				this.getManagedBean().setMinistryMandatory(true);
			} else {
				log.info("[#27197] Report code not exists...");
				getManagedBean().setMinistryMandatory(false);
			}
		}			
		//[#27197] - end
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
		if(getManagedBean().getCurrentActiveOrg() !=null){
			Long orgProfileId = getManagedBean().getCurrentActiveOrg().getOrgProfileId();
			if(getManagedBean().getCurrentActiveOrg().getGroupOrgType() !=null 
					&&(ParameterConst.ORG_AUTH_TYPE_KUMPULAN_PTJ.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())
							|| ParameterConst.ORG_AUTH_TYPE_MINISTRY.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType()))){
				if(isRolePlanner() || isRoleVerifier()){
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));					
					getManagedBean().setSelectedPegawaiPengawal(retrievePegawaiPegawalOrgProfile(orgProfileId));					
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));					
					getManagedBean().setSelectedPTJ(getManagedBean().getCurrentActiveOrg());
					getManagedBean().setDisabledMinistryLov(true);
					getManagedBean().setDisabledPegawaiLov(true);
					getManagedBean().setDisabledKumpulanPTJLov(true);
					getManagedBean().setDisabledPTJLov(true);
					
				}else if(isRoleApprover()){
					 // group org hierarchy
					if(ParameterConst.ORG_AUTH_TYPE_MINISTRY.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())){
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().setDisabledMinistryLov(true);
					}else if(ParameterConst.ORG_AUTH_TYPE_KUMPULAN_PTJ.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType())){					
						getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
						getManagedBean().setSelectedPegawaiPengawal(retrievePegawaiPegawalOrgProfile(orgProfileId));	
						getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
						getManagedBean().setDisabledPegawaiLov(true);
						getManagedBean().setDisabledMinistryLov(true);
						getManagedBean().setDisabledKumpulanPTJLov(true);
					}
				}else if(isInternalAuditor()){
					//getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId)); //#27472
					getManagedBean().setSelectedMinistry(getManagedBean().getCurrentActiveOrg()); //#27472
					getManagedBean().setDisabledMinistryLov(true);
				}
			}else if(getManagedBean().getCurrentActiveOrg().getGroupOrgType() ==null 
					|| (getManagedBean().getCurrentActiveOrg().getGroupOrgType() !=null 
					&& ParameterConst.ORG_AUTH_TYPE_ORG_GROUP.equals(getManagedBean().getCurrentActiveOrg().getGroupOrgType()))){
				// usual org hierarchy 
				if(isRolePlanner() || isRoleVerifier() || isRoleApprover()){
					getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId));
					getManagedBean().setSelectedPegawaiPengawal(retrievePegawaiPegawalOrgProfile(orgProfileId));
					getManagedBean().setSelectedKumpulanPTJ(retrieveJabatanOrgProfile(orgProfileId));
					getManagedBean().setSelectedPTJ(getManagedBean().getCurrentActiveOrg());
					getManagedBean().setDisabledMinistryLov(true);
					getManagedBean().setDisabledPegawaiLov(true);
					getManagedBean().setDisabledKumpulanPTJLov(true);
					getManagedBean().setDisabledPTJLov(true);
				}else if(isInternalAuditor()){
					//getManagedBean().setSelectedMinistry(retrieveMinistryOrgProfile(orgProfileId)); //#27472
					getManagedBean().setSelectedMinistry(getManagedBean().getCurrentActiveOrg()); //#27472
					getManagedBean().setDisabledMinistryLov(true);
				}
			}
		}
		
//		if (isRoleApprover()
//				|| isRoleVerifier()
//				|| isRolePlanner()) {
//			getManagedBean().setDisableOrgFields(true);
//			getManagedBean().setDisableMinistryField(true);
//			getManagedBean().setSelectedMinistry(
//					retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
//			getManagedBean().setSelectedPegawaiPengawal(
//					retrievePegawaiPegawalOrgProfile(pmOrgProfile
//							.getOrgProfileId()));
//			getManagedBean().setSelectedKumpulanPTJ(
//					retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
//			getManagedBean().setSelectedPTJ(pmOrgProfile);
//		}else if(isInternalAuditor()){
//			getManagedBean().setSelectedMinistry(
//					pmOrgProfile);
//			getManagedBean().setDisableMinistryField(true);
//		}
	}

	public void onChangeMinistry(BaseEntity entity) {
		getManagedBean().setSelectedPegawaiPengawal(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
	}

	public void onChangePegawaiPengawal(BaseEntity entity) {
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
	}

	public void onChangeJabatan(BaseEntity entity) {
		getManagedBean().setSelectedPTJ(null);
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

	public List<String> generateCommonYearList() {
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);

		Date currentDate = new Date();
		Integer year = null;
		String yearFrom = outputDateFormat.format(currentDate).substring(6, 10);
		int range = 7;	//[#24976]		
		if (yearFrom != null) {
			year = Integer.parseInt(yearFrom);
		}
		List<String> yearList = new ArrayList<String>();
		if (year != null) {
			year = year + 1;
			//[#24976],[#24974] - start
			if(PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL.equals(this.getManagedBean().getReportCode()) 
					|| PpReportConst.PP_BUDGET_ALLOCATION_AMENDMENT_RPT_VAL.equals(this.getManagedBean().getReportCode())
					|| PpReportConst.PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL.equals(this.getManagedBean().getReportCode())) { //[#29484]
				range = 8;
			}
			//[#24976],[#24974] - end
			for (int i = year - range; i <= year; i++) {
				yearList.add(Integer.toString(i));
			}
		}		
		//[#24976] - start
		if(PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL.equals(this.getManagedBean().getReportCode())
				|| PpReportConst.PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL.equals(this.getManagedBean().getReportCode())) {//[#29484]
			this.getManagedBean().setSelectedYear(yearFrom);
		}
		//[#24976] - end
		return yearList;
	}

	public void generatePpReport() {
		IRPTRequestSO rptRequestSO = null;
		RPTRequestResDTO resDTO = new RPTRequestResDTO(); //#27472
		
		//[#27197] - start
		//validate Ministry search field for role BPK_OFFICER, BPK_AUDITOR, AUDITOR_GENERAL
		if(isBPKOfficer() || isBPKAuditor()	|| isGeneralAuditor()) {
			
			log.info("[#27197] Report code list: " + Arrays.toString(ppReportCodes.toArray()));
			if(ppReportCodes.contains(getManagedBean().getReportCode())) {
				log.info("[#27197] Report code exists...");
				if(getManagedBean().getSelectedMinistry() == null) {
					throw new EpException(MessageConst.ERROR_REPORT_780059);
				}
			}
		}
		//[#27197] - end
		
		if(StringUtils.isBlank(getManagedBean().getSelectedYear())){
			throw new EpException(MessageConst.ERROR_REPORT_780030);
		}

		//start #27472
		if(StringUtils.isBlank(getManagedBean().getDocumentType())){
			throw new EpException(MessageConst.ERROR_760444);
		}
		//end #27472

		try {
			rptRequestSO = EpEjbBusinessDelegate.getInstance()
					.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
							IRPTRequestSO.class);
			RPTRequestReqDTO reqDTO = new RPTRequestReqDTO();
			reqDTO.setParametersMap(constructParamMap());
			reqDTO.setDefaultOutputType(getManagedBean().getDefaultOutputType());
			reqDTO.setTempAccess(getManagedBean().isTempAccess());
			// start #27472
			reqDTO.setDefaultOutputType(getManagedBean().getDocumentType());
			
			//add if else for document selection
			if(getManagedBean().getDocumentType().equals(ReportConst.PDF)){
				resDTO = rptRequestSO.generateReport(reqDTO);
			}else{
				resDTO = rptRequestSO.generateEmptyRequest(reqDTO);
				String ministryName = this.getManagedBean().getSelectedMinistry() != null? 
						this.getManagedBean().getSelectedMinistry().getOrgName():""; //need this because this.getManagedBean().getSelectedMinistry() return null/false name in other method
				try{
					generateExcelPP(resDTO,ministryName,constructParamMap());
				}catch(EpException e){
					getRptRequestSO().updateFailedExcelReport(resDTO.getRequestId());
					throw new EpException(e.getMessage());
				}
			}
			
			getPopUpMsg(resDTO.getRequestId() != null ? resDTO.getRequestId().toString() : "");
			RequestContext.getCurrentInstance().execute("confirmationId.show()");				
			
			//end #27472
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			//start #27472
			if(getManagedBean().getDocumentType().equals(ReportConst.XLS))
				getRptRequestSO().updateFailedExcelReport(resDTO.getRequestId()); 
			//end #27472
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
		
		if(getManagedBean().getProcurementCatId() != null){
			map.put(PpReportConst.PARAM_PP_CATEGORY, getManagedBean()
					.getProcurementCatId().toString());
		}
		
		if(getManagedBean().getProcurementTypeId() != null){
			map.put(PpReportConst.PARAM_PP_METHOD, getManagedBean()
					.getProcurementTypeId().toString());
		}
		
		if(getManagedBean().getStatus() != null){
			map.put(PpReportConst.PARAM_PP_STATUS, getManagedBean()
					.getStatus().toString());
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
	
	/**
	 *  get Procurement Cat options 
	 * @return List<PmProcurementCatType>
	 */
	public List<PmProcurementCatType> getProcurementCategoryOptions() {
		log.info("getProcurementCategoryOptions");
		String languageCode = getFacesScopes().getUserLocale().getLanguage();
		return PpCommonUtil.retrieveProcurementCatTypeList(languageCode);
	}
	
	/**
	 * get Procurement Type options by cat id
	 * @return List<PmProcurementCatType>
	 */
	public List<PmProcurementCatType> getProcurementTypeOptions(Long procurementCatId) {
		log.info("getProcurementTypeOptions");
		List<PmProcurementCatType> procurementCatTypeList = new ArrayList<PmProcurementCatType>();
		String languageCode = getFacesScopes().getUserLocale().getLanguage();
		if(procurementCatId!=null && procurementCatId>0L){			
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeListByCatId(languageCode, procurementCatId);
		}else{
			procurementCatTypeList = PpCommonUtil.retrieveProcurementTypeList(languageCode,null);
		}
		return procurementCatTypeList;
	}
	
	//#27472 generate by report type
	public void generateExcelPP(RPTRequestResDTO resDTO, String ministryName, LinkedHashMap<String, String> params){
		int tier = findHigherRole(); //ministry 1,kumpPtj 2, ptj 3
		String reportName = "";
			
		if(getManagedBean().getReportCode().equals(PpReportConst.PP_PROCUREMENT_DETAIL_RPT_VAL)){
			reportName = "Procurement Plan Detailed Report";
			generateExlPPDR(resDTO.getRequestId().toString(),tier,ministryName,params);
		}else if(getManagedBean().getReportCode().equals(PpReportConst.PP_PROCUREMENT_SUMM_RPT_VAL)){
			reportName = "Procurement Plan Summary Report";
			generateExlPPSR(resDTO.getRequestId().toString(),tier,ministryName,params);
		}else if(getManagedBean().getReportCode().equals(PpReportConst.PP_EXPENDITURE_DETAIL_RPT_VAL)){
			reportName = "Expenditure Plan Detailed Report";
			generateExlEPDR(resDTO.getRequestId().toString(),tier,ministryName,params);
		}else if(getManagedBean().getReportCode().equals(PpReportConst.PP_EXPENDITURE_SUMM_RPT_VAL)){
			reportName = "Expenditure Plan Summary Report";
			generateExlEPSR(resDTO.getRequestId().toString(),tier,ministryName,params);
		}else if(getManagedBean().getReportCode().equals(PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL)){
			reportName = "Expenditure Performance Using ePerolehan (eP) system Report";
			generateExlEPR(resDTO.getRequestId().toString(),params);
		}else if(getManagedBean().getReportCode().equals(PpReportConst.PP_BUDGET_ALLOCATION_AMENDMENT_RPT_VAL)){
			reportName = "Budget Allocation Amendment Through (eP) Report";
			generateExlBAAR(resDTO.getRequestId().toString(),params);			
		}
		//start [#29484]
		else if(getManagedBean().getReportCode().equals(PpReportConst.PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL)){
			reportName = "Total Approved Procurement and Expenditure Plan by Ministry Report";
			generateExlTAM(resDTO.getRequestId().toString(),params);
		}
		//end [#29484]
		getRptRequestSO().updateExcelPPReportName(resDTO.getRequestId(),reportName);
	}

	//#27472 Procurement Plan Detailed Report
	public void generateExlPPDR(String reportName, int tierLevel, String ministryName, LinkedHashMap<String, String> params){
        try{
			Workbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			String titleReport = "LAPORAN PERINCIAN PERANCANGAN PEROLEHAN BAGI TAHUN "+ getManagedBean().getSelectedYear();
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_PROCUREMENT_DETAIL_RPT_VAL);
			
			Map<Integer, Object[]> itemList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ministryList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> kumpPtjList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ptjList = new TreeMap<Integer, Object[]>();
			
			//start senarai item tab
			ArrayList<String> columnHeaderListItem = new ArrayList<String>(Arrays.asList(
					"Bil.","Pegawai Pengawal","Kod Kump PTJ","Kumpulan PTJ","Kod PTJ","Nama PTJ","Alamat","Nombor Pelan","Tajuk Perolehan",
					"Perihal Perolehan", "Kategori Jenis Perolehan","Kaedah Perolehan","Anggaran Amaun(RM)","Tarikh Jangkaan Pelawaan","Tarikh Akhir Kemaskini"
			));
			Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Item", titleReport, 
					columnHeaderListItem, columnHeaderListItem.size(),params.get("PMINIS")!=null?true:false,ministryName);
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListItem.size(); i++) {   
	    		if(i==0)sheetItemList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetItemList.setColumnWidth(i, 45*256); //Pegawai Pengawal
	        	else if(i==2)sheetItemList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
	        	else if(i==3)sheetItemList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==4)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==5)sheetItemList.setColumnWidth(i, 45*256); //Col : Nama PTJ
	        	else if(i==6)sheetItemList.setColumnWidth(i, 70*256); //Col : Alamat
	        	else if(i==7)sheetItemList.setColumnWidth(i, 20*256); //Col : Nombor Pelan
	        	else if(i==8)sheetItemList.setColumnWidth(i, 45*256); //Col : Tajuk Perolehan
	        	else if(i==9)sheetItemList.setColumnWidth(i, 35*256); //Col : Perihal Perolehan
	        	else if(i==10)sheetItemList.setColumnWidth(i, 25*256); //Col : Kategori Jenis Perolehan
	        	else if(i==11)sheetItemList.setColumnWidth(i, 25*256); //Col : Kaedah Perolehan
	        	else if(i==12)sheetItemList.setColumnWidth(i, 25*256); //Col : Anggaran Amount
	        	else if(i==13)sheetItemList.setColumnWidth(i, 25*256); //Col : Tarikh Jangkaan Pelawaan
	        	else if(i==14)sheetItemList.setColumnWidth(i, 25*256); //Col : Tarikh Akhir Kemaskini
	        }
	        
			if(resultList.isEmpty())
		        createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]alamat,[8]plan,[9]title,
				//[10]desc,[11]cat,[12]method,[13]esimateAmt[14]invDate[15]chngeDate,[16]proctypehierarchy
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					
					itemList.put(m, new Object[]{
						Integer.valueOf(m+1),
						(String)row[2],Integer.valueOf((String)row[3]),(String)row[4],Integer.valueOf((String)row[5]),(String)row[6],(String)row[7],(String)row[8],(String)row[9],
						(String)row[10],(String)row[11],(String)row[12],((BigDecimal)row[13]).doubleValue(),(Date)row[14],(Date)row[15]
					});
				}
				insertInCell(wb,createHelper,itemList,sheetItemList);
			}
	
			//end senarai item tab
	        
	        //start kementerian tab
	        if(tierLevel==1){
	
		  		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
		  				"Bil.","Kementerian","Anggaran Amaun (RM)"));
		  		Sheet sheetMinistryList = createSheetBase(wb, createHelper, "Kementerian", titleReport, 
		  				columnHeaderListMinistry, columnHeaderListMinistry.size(),params.get("PMINIS")!=null?true:false,ministryName);
		          
		  		// Resize all columns to fit the content size
				for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
					if(i==0)sheetMinistryList.setColumnWidth(i, 5*256);//Col : Bil.
					else if(i==1)sheetMinistryList.setColumnWidth(i, 45*256); //Col : Ministry
					else if(i==2)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
				}
		          
				if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetMinistryList,columnHeaderListMinistry);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]alamat,[8]plan,[9]title,
					//[10]desc,[11]cat,[12]method,[13]esimateAmt[14]invDate[15]chngeDate,[16]proctypehierarchy

					sortBySelectedObject(resultList,1); //1:ministry
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(ministryList.containsKey(((BigDecimal)row[1]).intValueExact())){ //find key in map
							Object[] obj = ministryList.get(((BigDecimal)row[1]).intValueExact());
							BigDecimal oldValue = (BigDecimal)obj[2];
							obj[2] = ((BigDecimal)row[13]).add(oldValue); //add existing anggaran amaun with new object
							ministryList.put(((BigDecimal)row[1]).intValueExact(), obj);
						}else{						
							ministryList.put(((BigDecimal)row[1]).intValueExact(), new Object[]{
								count,(String)row[0],((BigDecimal)row[13])
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,ministryList,sheetMinistryList);
				}
	        }
	  	
	  		//end kementerian tab
	          
	        //start Kumpulan PTJ tab
	        if(tierLevel==1 || tierLevel==2){
				ArrayList<String> columnHeaderListKumpPtj = new ArrayList<String>(Arrays.asList(
						"Bil.","Kod Kump PTJ","Kumpulan PTJ","Anggaran Amaun (RM)"));
				Sheet sheetKumpPtjList = createSheetBase(wb, createHelper, "Kumpulan PTJ", titleReport, 
						columnHeaderListKumpPtj, columnHeaderListKumpPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (KUMP PTJ)");
		        
				// Resize all columns to fit the content size
		        for(int i = 0; i < columnHeaderListKumpPtj.size(); i++) {   
		    		if(i==0)sheetKumpPtjList.setColumnWidth(i, 5*256);//Col : Bil.
		    		else if(i==1)sheetKumpPtjList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
		        	else if(i==2)sheetKumpPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
		        	else if(i==3)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
		        }
		
		        if(resultList.isEmpty())
		        	createNotFoundRecord(wb,sheetKumpPtjList,columnHeaderListKumpPtj);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]alamat,[8]plan,[9]title,
					//[10]desc,[11]cat,[12]method,[13]esimateAmt[14]invDate[15]chngeDate,[16]proctypehierarchy

					sortBySelectedObject(resultList,3); //1:kumptjcode
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(kumpPtjList.containsKey(Integer.parseInt((String)row[3]))){ //find key in map
							Object[] obj = kumpPtjList.get(Integer.parseInt((String)row[3]));
							BigDecimal oldValue = (BigDecimal)obj[3];
							obj[3] = ((BigDecimal)row[13]).add(oldValue); //add existing anggaran amaun with new object
							kumpPtjList.put(Integer.parseInt((String)row[3]), obj);
						}else{						
							kumpPtjList.put(Integer.parseInt((String)row[3]), new Object[]{
								count,Integer.valueOf((String)row[3]),(String)row[4],((BigDecimal)row[13])
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,kumpPtjList,sheetKumpPtjList);
				}
	        }
	
	    	//end Kumpulan PTJ tab
	        
		    //start PTJ tab
			ArrayList<String> columnHeaderListPtj = new ArrayList<String>(Arrays.asList(
					"Bil.","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan","Anggaran Amaun (RM)"));
			Sheet sheetPtjList = createSheetBase(wb, createHelper, "PTJ", titleReport, columnHeaderListPtj, 
					columnHeaderListPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (PTJ)");
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListPtj.size(); i++) {   
	    		if(i==0)sheetPtjList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==2)sheetPtjList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==3)sheetPtjList.setColumnWidth(i, 45*256); //Col : Name PTJ
	        	else if(i==4)sheetPtjList.setColumnWidth(i, 25*256); //Col : Nombor Pelan
	        	else if(i==5)sheetPtjList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
	        }
	        
	        if(resultList.isEmpty())
	        	createNotFoundRecord(wb,sheetPtjList,columnHeaderListPtj);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]alamat,[8]plan,[9]title,
				//[10]desc,[11]cat,[12]method,[13]esimateAmt[14]invDate[15]chngeDate,[16]proctypehierarchy

				sortBySelectedObject(resultList,5); //1:ptjCode
				
				Integer count = 1;
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m); //get object
					
					if(ptjList.containsKey(Integer.parseInt((String)row[5]))){ //find key in map
						Object[] obj = ptjList.get(Integer.parseInt((String)row[5]));
						BigDecimal oldValue = (BigDecimal)obj[5];
						obj[5] = ((BigDecimal)row[13]).add(oldValue); //add existing anggaran amaun with new object
						ptjList.put(Integer.parseInt((String)row[5]), obj);
					}else{						
						ptjList.put(Integer.parseInt((String)row[5]), new Object[]{
							count,(String)row[4],Integer.valueOf((String)row[5]),(String)row[6],(String)row[8],((BigDecimal)row[13])
							});
						count++;
					}
				}

				insertInCell(wb,createHelper,ptjList,sheetPtjList);
			}
	    	//end PTJ tab
	          
	        exportExcelReportPP(wb, reportName);
	        
        }catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}
	}
	
	//#27472 Procurement Plan Summary Report
	public void generateExlPPSR(String reportName, int tierLevel, String ministryName, LinkedHashMap<String, String> params){
		try{
			Workbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			String titleReport = "LAPORAN RINGKASAN PERANCANGAN PEROLEHAN BAGI TAHUN "+ getManagedBean().getSelectedYear();
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_PROCUREMENT_SUMM_RPT_VAL);
			Map<Integer, Object[]> itemList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ministryList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> kumpPtjList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ptjList = new TreeMap<Integer, Object[]>();
			
			//start senarai item tab
			ArrayList<String> columnHeaderListItem = new ArrayList<String>(Arrays.asList(
					"Bil.","Pegawai Pengawal","Kod Kump PTJ","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan",
					"Kaedah Perolehan","Bilangan","Anggaran Amaun(RM)"
			));
			Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Item", titleReport, 
					columnHeaderListItem, columnHeaderListItem.size(),params.get("PMINIS")!=null?true:false,ministryName);
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListItem.size(); i++) {   
	    		if(i==0)sheetItemList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetItemList.setColumnWidth(i, 45*256); //Pegawai Pengawal
	        	else if(i==2)sheetItemList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
	        	else if(i==3)sheetItemList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==4)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==5)sheetItemList.setColumnWidth(i, 45*256); //Col : Nama PTJ
	        	else if(i==6)sheetItemList.setColumnWidth(i, 20*256); //Col : Nombor Pelan
	        	else if(i==7)sheetItemList.setColumnWidth(i, 25*256); //Col : Kaedah Perolehan
	        	else if(i==8)sheetItemList.setColumnWidth(i, 15*256); //Col : Bilangan
	        	else if(i==9)sheetItemList.setColumnWidth(i, 25*256); //Col : Anggaran Amount
	        }
	        
	        if(resultList.isEmpty())
		        createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,
				//[7]plan,[8]proctype,[9]bil,[10]esimateAmt,[11]proctypehierarchy
				List<Object[]> newList = new ArrayList<Object[]>();
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					row = appendValue(row, Integer.valueOf(((BigDecimal)row[1]).toString()+((BigDecimal)row[11]).toString())); //become object[12]
					row = appendValue(row, Integer.valueOf((String)row[3]+((BigDecimal)row[11]).toString())); //become object[13]
					newList.add(row);
					
					itemList.put(m, new Object[]{
						Integer.valueOf(m+1),
						(String)row[2],Integer.valueOf((String)row[3]),(String)row[4],Integer.valueOf((String)row[5]),(String)row[6],(String)row[7],(String)row[8],
						((BigDecimal)row[9]==null? new BigDecimal(0):(BigDecimal)row[9]).intValueExact(),((BigDecimal)row[10])
					});
				}
				insertInCell(wb,createHelper,itemList,sheetItemList);
				resultList.clear();
				resultList.addAll(newList);
			}
	
	        //end senarai item tab
	        
	        //start kementerian tab
	        if(tierLevel==1){
		  		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
		  				"Bil.","Kementerian","Bilangan","Kaedah Perolehan","Anggaran Amaun (RM)"));
		  		Sheet sheetMinistryList = createSheetBase(wb, createHelper, "Kementerian", titleReport, 
		  				columnHeaderListMinistry, columnHeaderListMinistry.size(),params.get("PMINIS")!=null?true:false,ministryName);
		          
		  		// Resize all columns to fit the content size
				for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
					if(i==0)sheetMinistryList.setColumnWidth(i, 5*256);//Col : Bil.
					else if(i==1)sheetMinistryList.setColumnWidth(i, 45*256); //Col : Ministry
					else if(i==2)sheetMinistryList.setColumnWidth(i, 15*256); //Col : Bilangan
					else if(i==3)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Kaedah Perolehan
					else if(i==4)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
				}
		          
				if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetMinistryList,columnHeaderListMinistry);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,
					//[7]plan,[8]proctype,[9]bil,[10]esimateAmt,[11]proctypehierarchy,[12]codeministry+proctype,[13]codekmp+proctype
					
					sortBySelectedObject(resultList,12);
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						//if(ministryList.containsKey(((BigDecimal)row[1]).intValue())){ //find key in map
						if(ministryList.containsKey((Integer) row[12])){ //find key in map
							Object[] obj = ministryList.get((Integer) row[12]);
							
							Integer oldCountValue = (Integer)obj[2]; //old count
							obj[2] = ((BigDecimal)row[9]==null? new BigDecimal(0):(BigDecimal)row[9]).intValueExact() + oldCountValue;
							
							BigDecimal oldValue = (BigDecimal)obj[4]; //old anggaran
							obj[4] = ((BigDecimal)row[10]).add(oldValue);
							
							ministryList.put((Integer) row[12], obj);
						}else{
							ministryList.put((Integer) row[12], new Object[]{
								count,(String)row[0],((BigDecimal)row[9]==null? new BigDecimal(0):(BigDecimal)row[9]).intValueExact(),
								(String)row[8],((BigDecimal)row[10])
								});
							count++;
						}
					}
					
					insertInCell(wb,createHelper,ministryList,sheetMinistryList);
				}
	        }
	  	
	  		//end kementerian tab
	          
	        //start Kumpulan PTJ tab
	        if(tierLevel==1 || tierLevel==2){
				ArrayList<String> columnHeaderListKumpPtj = new ArrayList<String>(Arrays.asList(
						"Bil.","Kod Kump PTJ","Kumpulan PTJ","Bilangan","Kaedah Perolehan","Anggaran Amaun (RM)"));
				Sheet sheetKumpPtjList = createSheetBase(wb, createHelper, "Kumpulan PTJ", titleReport, 
						columnHeaderListKumpPtj, columnHeaderListKumpPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (KUMP PTJ)");
		        
				// Resize all columns to fit the content size
		        for(int i = 0; i < columnHeaderListKumpPtj.size(); i++) {   
		    		if(i==0)sheetKumpPtjList.setColumnWidth(i, 5*256);//Col : Bil.
		    		else if(i==1)sheetKumpPtjList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
		        	else if(i==2)sheetKumpPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
		        	else if(i==3)sheetKumpPtjList.setColumnWidth(i, 15*256); //Col : Bilangan
		        	else if(i==4)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Kaedah Perolehan
		        	else if(i==5)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
		        }
		        
		        if(resultList.isEmpty())
		        	createNotFoundRecord(wb,sheetKumpPtjList,columnHeaderListKumpPtj);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,
					//[7]plan,[8]proctype,[9]bil,[10]esimateAmt,[11]proctypehierarchy,[12]codeministry+proctype,[13]codekmp+proctype
					
					sortBySelectedObject(resultList,13);
					
					Integer count = 1;					
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(kumpPtjList.containsKey((Integer) row[13])){ //find key in map
							Object[] obj = kumpPtjList.get((Integer) row[13]);
							Integer oldCountValue = (Integer)obj[3]; //old count
							obj[3] = ((BigDecimal)row[9]==null? new BigDecimal(0):(BigDecimal)row[9]).intValueExact() + oldCountValue;
							
							BigDecimal oldValue = (BigDecimal)obj[5]; //old anggaran
							obj[5] = ((BigDecimal)row[10]).add(oldValue);
							kumpPtjList.put((Integer) row[13], obj);
						}else{
							kumpPtjList.put((Integer) row[13], new Object[]{
								count,Integer.valueOf((String)row[3]),(String)row[4],((BigDecimal)row[9]==null? new BigDecimal(0):(BigDecimal)row[9]).intValueExact(),
								(String)row[8],((BigDecimal)row[10])
								});
							count++;
						}
					}
					
					insertInCell(wb,createHelper,kumpPtjList,sheetKumpPtjList);
				}
	        }
	    	//end Kumpulan PTJ tab
	        
	        //start PTJ tab
			ArrayList<String> columnHeaderListPtj = new ArrayList<String>(Arrays.asList(
					"Bil.","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan","Anggaran Amaun (RM)"));
			Sheet sheetPtjList = createSheetBase(wb, createHelper, "PTJ", titleReport, columnHeaderListPtj, 
					columnHeaderListPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (PTJ)");
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListPtj.size(); i++) {   
	    		if(i==0)sheetPtjList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==2)sheetPtjList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==3)sheetPtjList.setColumnWidth(i, 45*256); //Col : Name PTJ
	        	else if(i==4)sheetPtjList.setColumnWidth(i, 25*256); //Col : Nombor Pelan
	        	else if(i==5)sheetPtjList.setColumnWidth(i, 25*256); //Col : Anggaran Amaun
	        }
	        
	        if(resultList.isEmpty())
	        	createNotFoundRecord(wb,sheetPtjList,columnHeaderListPtj);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,
				//[7]plan,[8]proctype,[9]bil,[10]esimateAmt,[11]proctypehierarchy,[12]codeministry+proctype,[13]codekmp+proctype
				
				sortBySelectedObject(resultList,5);
				
				Integer count = 1;
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m); //get whole object					
					
					if(ptjList.containsKey(Integer.valueOf((String)row[5]))){ //find key in map
						Object[] obj = ptjList.get(Integer.valueOf((String)row[5]));
						BigDecimal oldValue = (BigDecimal)obj[5];
						obj[5] = ((BigDecimal)row[10]).add(oldValue); //add existing anggaran amaun with new object
						ptjList.put(Integer.valueOf((String)row[5]), obj);
					}else{
						ptjList.put(Integer.valueOf((String)row[5]), new Object[]{
							count,(String)row[4],Integer.parseInt((String)row[5]),(String)row[6],(String)row[7],((BigDecimal)row[10])
							});
						count++;
					}
				}
				insertInCell(wb,createHelper,ptjList,sheetPtjList);
			}
	        
	    	//end PTJ tab
	          
	        exportExcelReportPP(wb, reportName);
		}catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}
	}
	
	//#27472 Expenditure Plan Detail Report
	public void generateExlEPDR(String reportName, int tierLevel, String ministryName, LinkedHashMap<String, String> params){
		try{
			Workbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			String titleReport = "LAPORAN PERINCIAN PERANCANGAN PERBELANJAAN BAGI TAHUN "+ getManagedBean().getSelectedYear();
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_EXPENDITURE_DETAIL_RPT_VAL);
			Map<Integer, Object[]> itemList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ministryList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> kumpPtjList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ptjList = new TreeMap<Integer, Object[]>();
			//start #27472 change format for deduction
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			//end
			
			//start senarai item tab
			ArrayList<String> columnHeaderListItem = new ArrayList<String>(Arrays.asList(
					"Bil.","Pegawai Pengawal","Kod Kump PTJ","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan","Jenis VOT","Kod Akaun","Jenis Perbelanjaan",
					"Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)","Jumlah Amaun Perbelanjaan (RM)",
					"Jumlah Peratusan Amaun Perbelanjaan eP(%)","Justifikasi Tidak Menggunakan eP"
			));
			Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Item", titleReport, 
					columnHeaderListItem, columnHeaderListItem.size(),params.get("PMINIS")!=null?true:false,ministryName );
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListItem.size(); i++) {   
	    		if(i==0)sheetItemList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetItemList.setColumnWidth(i, 45*256); //Pegawai Pengawal
	        	else if(i==2)sheetItemList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
	        	else if(i==3)sheetItemList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==4)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==5)sheetItemList.setColumnWidth(i, 45*256); //Col : Nama PTJ
	        	else if(i==6)sheetItemList.setColumnWidth(i, 20*256); //Col : Nombor Pelan
	        	else if(i==7)sheetItemList.setColumnWidth(i, 30*256); //Col : Jenis VOT
	        	else if(i==8)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod Akaun
	        	else if(i==9)sheetItemList.setColumnWidth(i, 25*256); //Col : Jenis Perbelanjaan
	        	else if(i==10)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang diluluskan
	        	else if(i==11)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk kegunaan eP
	        	else if(i==12)sheetItemList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
	        	else if(i==13)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
	        	else if(i==14)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan eP
	        	else if(i==15)sheetItemList.setColumnWidth(i, 25*256); //Col : Justifikasi Tidak Menggunakan eP
	        }
	        
	        if(resultList.isEmpty())
		        createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			else{
				//[0]pegawai,[1]kumptjCode,[2]kumpptj,[3]ptjcode,[4]ptj,[5]plan,[6]vot,[7]glCode,[8]expType,[9]bdgetAmt,[10]plnAmnt,[11]unusedAmt,
				//[12]orgprofileid,[13]votType,[14]justifikasi,[15]deduct,[16]ministry,[17]pminis,[18]vottypehierarchy
				List<Object[]> newList = new ArrayList<Object[]>();
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					Double amountExpenditure = getRptRequestSO().getSumOfExpDtlAmnt(((BigDecimal)row[12]).toString(),
							params.get("PYEAR"),((BigDecimal)row[13]).toString(),(String)row[7]);
					Double percentageExpenditure = amountExpenditure / ((BigDecimal)row[10]).doubleValue() * 100;
					
					//add calculated amountExpenditure in objectlist for usage in another tab
					row = appendValue(row, amountExpenditure); //its become obj[19]
					newList.add(row);
					
					itemList.put(m, new Object[]{
						Integer.valueOf(m+1),(String)row[0],Integer.valueOf((String)row[1]),(String)row[2],Integer.valueOf((String)row[3]),
						(String)row[4],(String)row[5],(String)row[6],(String)row[7],(String)row[8],(BigDecimal)row[9],
						(BigDecimal)row[10],(BigDecimal)row[11],amountExpenditure, percentageExpenditure,(String)row[14]
					});
				}
				insertInCell(wb,createHelper,itemList,sheetItemList);
				resultList.clear();
				resultList.addAll(newList);
			}
	
			//end senarai item tab
	        
	        //start kementerian tab
	        if(tierLevel==1){
		  		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
		  				"Bil.","Kementerian","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
		  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
		  				));
		  		Sheet sheetMinistryList = createSheetBase(wb, createHelper, "Kementerian", titleReport, 
		  				columnHeaderListMinistry, columnHeaderListMinistry.size(),params.get("PMINIS")!=null?true:false,ministryName);
		          
		  		// Resize all columns to fit the content size
				for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
					if(i==0)sheetMinistryList.setColumnWidth(i, 5*256);//Col : Bil.
					else if(i==1)sheetMinistryList.setColumnWidth(i, 45*256); //Col : Ministry
					else if(i==2)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang Diluluskan
					else if(i==3)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
					else if(i==4)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
					else if(i==5)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
					else if(i==6)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
					else if(i==7)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
				}
		          
				if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetMinistryList,columnHeaderListMinistry);
				else{
					//[0]pegawai,[1]kumptjCode,[2]kumpptj,[3]ptjcode,[4]ptj,[5]plan,[6]vot,[7]glCode,[8]expType,[9]bdgetAmt,[10]plnAmnt,[11]unusedAmt,
					//[12]orgprofileid,[13]votType,[14]justifikasi,[15]deduct,[16]ministry,[17]pminis,[18]vottypehierarchy,[19]amaun
					
					sortBySelectedObject(resultList,17);
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(ministryList.containsKey(((BigDecimal)row[17]).intValueExact())){ //find key in map
							Object[] obj = ministryList.get(((BigDecimal)row[17]).intValueExact());
							//9-10
							BigDecimal oldBudgetValue = (BigDecimal)obj[2]; //old budget
							obj[2] = ((BigDecimal)row[9]).add(oldBudgetValue);
							
							BigDecimal oldPlanValue = (BigDecimal)obj[3]; //old plan
							obj[3] = ((BigDecimal)row[10]).add(oldPlanValue);

							BigDecimal oldUnusedValue = (BigDecimal)obj[4]; //old unused
							obj[4] = ((BigDecimal)row[11]).add(oldUnusedValue);

							Double oldAmountValue = (Double)obj[5]; //old amount
							obj[5] = (Double)row[19] + oldAmountValue;
							
							//BigDecimal oldDeductValue = (BigDecimal)obj[6]; //old deduct
							//obj[6] = ((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).add(oldDeductValue);
							
							Double dVal = 0.00;
							if((BigDecimal)row[15]!=null){
								obj[6]="("+ nf.format((BigDecimal)row[15])+")"; //#27472 change format for deduction
								dVal = ((BigDecimal)row[15]).doubleValue();
							}else if(!obj[6].toString().equals("(0.00)"))
								dVal = Double.parseDouble(obj[6].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
							
							//percentage
							Double amntMinusDeduct = (Double)obj[5] - dVal;
							obj[7] = amntMinusDeduct / ((BigDecimal)obj[3]).doubleValue() * 100;

							ministryList.put(((BigDecimal)row[17]).intValueExact(), obj);
						}else{
							Double amntMinusDeduct = ((Double)row[19]) - ((BigDecimal)row[15]!=null?((BigDecimal)row[15]).doubleValue():0.0);
							Double percentageExpenditure = amntMinusDeduct/((BigDecimal)row[10]).doubleValue()*100;
							ministryList.put(((BigDecimal)row[17]).intValueExact(), new Object[]{
								count,(String)row[16],(BigDecimal)row[9],(BigDecimal)row[10],(BigDecimal)row[11],(Double)row[19],
								(BigDecimal)row[15]!=null?"("+((BigDecimal)row[15]).toString()+")":"(0.00)",percentageExpenditure
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,ministryList,sheetMinistryList);
				}
	        }
	  	
	  		//end kementerian tab
	          
	        //start Kumpulan PTJ tab
	        if(tierLevel==1 || tierLevel==2){
				ArrayList<String> columnHeaderListKumpPtj = new ArrayList<String>(Arrays.asList(
						"Bil.","Kod Kump PTJ","Kumpulan PTJ","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
		  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
		  				));
				Sheet sheetKumpPtjList = createSheetBase(wb, createHelper, "Kumpulan PTJ", titleReport, 
						columnHeaderListKumpPtj, columnHeaderListKumpPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (KUMP PTJ)");
		        
				// Resize all columns to fit the content size
		        for(int i = 0; i < columnHeaderListKumpPtj.size(); i++) {   
		    		if(i==0)sheetKumpPtjList.setColumnWidth(i, 5*256);//Col : Bil.
		    		else if(i==1)sheetKumpPtjList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
		        	else if(i==2)sheetKumpPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
		        	else if(i==3)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang Diluluskan
					else if(i==4)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
					else if(i==5)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
					else if(i==6)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
					else if(i==7)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
					else if(i==8)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
		        }
		
		        if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetKumpPtjList,columnHeaderListKumpPtj);
				else{
					//[0]pegawai,[1]kumptjCode,[2]kumpptj,[3]ptjcode,[4]ptj,[5]plan,[6]vot,[7]glCode,[8]expType,[9]bdgetAmt,[10]plnAmnt,[11]unusedAmt,
					//[12]orgprofileid,[13]votType,[14]justifikasi,[15]deduct,[16]ministry,[17]pminis,[18]vottypehierarchy,[19]amaun
					
					sortBySelectedObject(resultList,1);
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(kumpPtjList.containsKey(Integer.parseInt((String)row[1]))){ //find key in map
							Object[] obj = kumpPtjList.get(Integer.parseInt((String)row[1]));
							//9-10
							BigDecimal oldBudgetValue = (BigDecimal)obj[3]; //old budget
							obj[3] = ((BigDecimal)row[9]).add(oldBudgetValue);
							
							BigDecimal oldPlanValue = (BigDecimal)obj[4]; //old plan
							obj[4] = ((BigDecimal)row[10]).add(oldPlanValue);

							BigDecimal oldUnusedValue = (BigDecimal)obj[5]; //old unused
							obj[5] = ((BigDecimal)row[11]).add(oldUnusedValue);

							Double oldAmountValue = (Double)obj[6]; //old amount
							obj[6] = (Double)row[19] + oldAmountValue;
							
							//BigDecimal oldDeductValue = (BigDecimal)obj[7]; //old deduct
							//obj[7] = ((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).add(oldDeductValue);
							
							Double dVal = 0.00;
							if((BigDecimal)row[15]!=null){
								obj[7]="("+ nf.format((BigDecimal)row[15])+")"; //#27472 change format for deduction
								dVal = ((BigDecimal)row[15]).doubleValue();
							}else if(!obj[7].toString().equals("(0.00)"))
								dVal = Double.parseDouble(obj[7].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
							
							//percentage
							Double amntMinusDeduct = ((Double)obj[6]) - dVal;
							obj[8] = amntMinusDeduct / ((BigDecimal)obj[4]).doubleValue() * 100;

							kumpPtjList.put(Integer.parseInt((String)row[1]), obj);
						}else{
							Double amntMinusDeduct = (Double)row[19] - ((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).doubleValue();
							Double percentageExpenditure = amntMinusDeduct /((BigDecimal)row[10]).doubleValue()*100;
							kumpPtjList.put(Integer.parseInt((String)row[1]), new Object[]{
								count,Integer.valueOf((String)row[1]),(String)row[2],(BigDecimal)row[9],(BigDecimal)row[10],(BigDecimal)row[11],
								(Double)row[19],(BigDecimal)row[15]!=null?"("+((BigDecimal)row[15]).toString()+")":"(0.00)",percentageExpenditure
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,kumpPtjList,sheetKumpPtjList);
				}
	        }
	    	//end Kumpulan PTJ tab
	        
	        //start PTJ tab
			ArrayList<String> columnHeaderListPtj = new ArrayList<String>(Arrays.asList(
					"Bil.","Kumpulan PTJ","Kod PTJ","Nama PTJ","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
	  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
	  				));
			Sheet sheetPtjList = createSheetBase(wb, createHelper, "PTJ", titleReport, columnHeaderListPtj, 
					columnHeaderListPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (PTJ)");
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListPtj.size(); i++) {   
	    		if(i==0)sheetPtjList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==2)sheetPtjList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==3)sheetPtjList.setColumnWidth(i, 45*256); //Col : Name PTJ
	        	else if(i==4)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang Diluluskan
				else if(i==5)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
				else if(i==6)sheetPtjList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
				else if(i==7)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
				else if(i==8)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
				else if(i==9)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
	        }
	        
	        if(resultList.isEmpty())
	        	createNotFoundRecord(wb,sheetPtjList,columnHeaderListPtj);
			else{
				//[0]pegawai,[1]kumptjCode,[2]kumpptj,[3]ptjcode,[4]ptj,[5]plan,[6]vot,[7]glCode,[8]expType,[9]bdgetAmt,[10]plnAmnt,[11]unusedAmt,
				//[12]orgprofileid,[13]votType,[14]justifikasi,[15]deduct,[16]ministry,[17]pminis,[18]vottypehierarchy,[19]amaun
				
				sortBySelectedObject(resultList,3);
				
				Integer count = 1;
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m); //get whole object
					
					if(ptjList.containsKey(Integer.parseInt((String)row[3]))){ //find key in map
						Object[] obj = ptjList.get(Integer.parseInt((String)row[3]));
						//9-10
						BigDecimal oldBudgetValue = (BigDecimal)obj[4]; //old budget
						obj[4] = ((BigDecimal)row[9]).add(oldBudgetValue);
						
						BigDecimal oldPlanValue = (BigDecimal)obj[5]; //old plan
						obj[5] = ((BigDecimal)row[10]).add(oldPlanValue);

						BigDecimal oldUnusedValue = (BigDecimal)obj[6]; //old unused
						obj[6] = ((BigDecimal)row[11]).add(oldUnusedValue);

						Double oldAmountValue = (Double)obj[7]; //old amount
						obj[7] = (Double)row[19] + oldAmountValue;
						
						//BigDecimal oldDeductValue = (BigDecimal)obj[8]; //old deduct
						//obj[8] = ((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).add(oldDeductValue);
						
						Double dVal = 0.00;
						if((BigDecimal)row[15]!=null){
							obj[8]="("+ nf.format((BigDecimal)row[15])+")"; //#27472 change format for deduction
							dVal = ((BigDecimal)row[15]).doubleValue();
						}else if(!obj[8].toString().equals("(0.00)"))
							dVal = Double.parseDouble(obj[8].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
						
						//percentage
						Double amntMinusDeduct = (Double)obj[7] - dVal;//((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).doubleValue();
						obj[9] = amntMinusDeduct / ((BigDecimal)obj[5]).doubleValue() * 100;

						ptjList.put(Integer.parseInt((String)row[3]), obj);
					}else{
						Double amntMinusDeduct = (Double)row[19] - ((BigDecimal)row[15]!=null?(BigDecimal)row[15]:BigDecimal.ZERO).doubleValue();
						Double percentageExpenditure = amntMinusDeduct /((BigDecimal)row[10]).doubleValue()*100;
						ptjList.put(Integer.parseInt((String)row[3]), new Object[]{
							count,(String)row[2],Integer.valueOf((String)row[3]),(String)row[4],(BigDecimal)row[9],(BigDecimal)row[10],(BigDecimal)row[11],
							(Double)row[19],(BigDecimal)row[15]!=null?"("+((BigDecimal)row[15]).toString()+")":"(0.00)",percentageExpenditure
							});
						count++;
					}
				}

				insertInCell(wb,createHelper,ptjList,sheetPtjList);
			}
	        
	    	//end PTJ tab
	          
	        exportExcelReportPP(wb, reportName);
		}catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}
	}
	
	//#27472 Expenditure Plan Summary Report
	public void generateExlEPSR(String reportName, int tierLevel, String ministryName, LinkedHashMap<String, String> params){
		try{
			Workbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			String titleReport = "LAPORAN RINGKASAN PERANCANGAN PERBELANJAAN BAGI TAHUN "+ getManagedBean().getSelectedYear();
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_EXPENDITURE_SUMM_RPT_VAL);
			Map<Integer, Object[]> itemList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ministryList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> kumpPtjList = new TreeMap<Integer, Object[]>();
			Map<Integer, Object[]> ptjList = new TreeMap<Integer, Object[]>();
			//start #27472 change format for deduction
			CommonUtils commonUtils = new CommonUtils();
			DecimalFormat nf = new DecimalFormat(commonUtils.getDisplayNumberFormat(CommonConstants.NUMBER_FORMAT_TYPE_AMOUNT));
			//end
			
			//start senarai item tab
			ArrayList<String> columnHeaderListItem = new ArrayList<String>(Arrays.asList(
					"Bil.","Pegawai Pengawal","Kod Kump PTJ","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan","Jenis VOT",
					"Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Jumlah Amaun Perbelanjaan (RM)",
					"Jumlah Peratusan Amaun Perbelanjaan eP(%)"
			));
			Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Item", titleReport, columnHeaderListItem, 
					columnHeaderListItem.size(),params.get("PMINIS")!=null?true:false,ministryName);
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListItem.size(); i++) {   
	    		if(i==0)sheetItemList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetItemList.setColumnWidth(i, 45*256); //Pegawai Pengawal
	        	else if(i==2)sheetItemList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
	        	else if(i==3)sheetItemList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==4)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==5)sheetItemList.setColumnWidth(i, 45*256); //Col : Nama PTJ
	        	else if(i==6)sheetItemList.setColumnWidth(i, 20*256); //Col : Nombor Pelan
	        	else if(i==7)sheetItemList.setColumnWidth(i, 25*256); //Col : Jenis VOT
	        	else if(i==8)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang diluluskan
	        	else if(i==9)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk kegunaan eP
	        	else if(i==10)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
	        	else if(i==11)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan eP
	        }
	        
	        if(resultList.isEmpty())
		        createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]plan,[8]vot,[9]bdgetAmt,[10]plnAmnt,[11]deduct,
				//[12]orgprofileid,[13]votType,[14]vottypehierarchy
				List<Object[]> newList = new ArrayList<Object[]>();
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					Double cf5 = getRptRequestSO().getSumOfExpCFAmnt("CF5",((BigDecimal)row[12]).toString(),((BigDecimal)row[13]).toString(),params);
					Double cf6 = getRptRequestSO().getSumOfExpCFAmnt("CF6",((BigDecimal)row[12]).toString(),((BigDecimal)row[13]).toString(),params);
					Double amountExpenditure = cf5 + cf6;
					Double percentageExpenditure = amountExpenditure / ((BigDecimal)row[10]).doubleValue() * 100;
					
					//add calculated amountExpenditure in objectlist for usage in another tab
					row = appendValue(row, amountExpenditure); //its become obj[15]
					newList.add(row);
					
					itemList.put(m, new Object[]{
						Integer.valueOf(m+1),(String)row[2],Integer.valueOf((String)row[3]),(String)row[4],Integer.valueOf((String)row[5]),
						(String)row[6],(String)row[7],(String)row[8],(BigDecimal)row[9],(BigDecimal)row[10],amountExpenditure, percentageExpenditure
					});
				}
				insertInCell(wb,createHelper,itemList,sheetItemList);
				resultList.clear();
				resultList.addAll(newList);
			}
			//end senarai item tab
	        
	        //start kementerian tab
	        if(tierLevel==1){
		  		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
		  				"Bil.","Kementerian","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
		  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
		  				));
		  		Sheet sheetMinistryList = createSheetBase(wb, createHelper, "Kementerian", titleReport, 
		  				columnHeaderListMinistry, columnHeaderListMinistry.size(),params.get("PMINIS")!=null?true:false,ministryName);
		          
		  		// Resize all columns to fit the content size
				for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
					if(i==0)sheetMinistryList.setColumnWidth(i, 5*256);//Col : Bil.
					else if(i==1)sheetMinistryList.setColumnWidth(i, 45*256); //Col : Ministry
					else if(i==2)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang Diluluskan
					else if(i==3)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
					else if(i==4)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
					else if(i==5)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
					else if(i==6)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
					else if(i==7)sheetMinistryList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
				}
		          
				if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetMinistryList,columnHeaderListMinistry);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]plan,[8]vot,[9]bdgetAmt,[10]plnAmnt,[11]deduct,
					//[12]orgprofileid,[13]votType,[14]vottypehierarchy,[15]amount
					
					sortBySelectedObject(resultList,1);
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(ministryList.containsKey(((BigDecimal)row[1]).intValueExact())){ //find key in map
							Object[] obj = ministryList.get(((BigDecimal)row[1]).intValueExact());
							//9-10
							BigDecimal oldBudgetValue = (BigDecimal)obj[2]; //old budget
							obj[2] = ((BigDecimal)row[9]).add(oldBudgetValue);
							
							BigDecimal oldPlanValue = (BigDecimal)obj[3]; //old plan
							obj[3] = ((BigDecimal)row[10]).add(oldPlanValue);

							obj[4] = ((BigDecimal)obj[2]).subtract((BigDecimal)obj[3]); //unused

							Double oldAmountValue = (Double)obj[5]; //old amount
							obj[5] = (Double)row[15] + oldAmountValue;
							
							//BigDecimal oldDeductValue = (BigDecimal)obj[6]; //old deduct
							//obj[6] = ((BigDecimal)row[11]!=null?(BigDecimal)row[11]:BigDecimal.ZERO).add(oldDeductValue);
							
							Double dVal = 0.00;
							if((BigDecimal)row[11]!=null){
								obj[6]="("+ nf.format((BigDecimal)row[11])+")"; //#27472 change format for deduction
								dVal = ((BigDecimal)row[11]).doubleValue();
							}else if(!obj[6].toString().equals("(0.00)"))
								dVal = Double.parseDouble(obj[6].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
							
							//percentage
							Double amntMinusDeduct = (Double)obj[5] - dVal;//((BigDecimal)row[11]!=null ? ((BigDecimal)row[11]).doubleValue():0.00);
							obj[7] = amntMinusDeduct / ((BigDecimal)obj[3]).doubleValue() * 100;

							ministryList.put(((BigDecimal)row[1]).intValueExact(), obj);
						}else{
							BigDecimal unUsedAmnt = ((BigDecimal)row[9]).subtract((BigDecimal)row[10]);
							Double amntMinusDeduct = (Double)row[15] - ((BigDecimal)row[11]!=null ? ((BigDecimal)row[11]).doubleValue():0.00);
							Double percentageExpenditure = amntMinusDeduct/((BigDecimal)row[10]).doubleValue()*100;
							ministryList.put(((BigDecimal)row[1]).intValueExact(), new Object[]{
								count,(String)row[0],(BigDecimal)row[9],(BigDecimal)row[10],unUsedAmnt,(Double)row[15],
								(BigDecimal)row[11]!=null?"("+((BigDecimal)row[11]).toString()+")":"(0.00)",percentageExpenditure
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,ministryList,sheetMinistryList);
				}
	        }
	  	
	  		//end kementerian tab
	          
	        //start Kumpulan PTJ tab
	        if(tierLevel==1 || tierLevel==2){
				ArrayList<String> columnHeaderListKumpPtj = new ArrayList<String>(Arrays.asList(
						"Bil.","Kod Kump PTJ","Kumpulan PTJ","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
		  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
		  				));
				Sheet sheetKumpPtjList = createSheetBase(wb, createHelper, "Kumpulan PTJ", titleReport, 
						columnHeaderListKumpPtj, columnHeaderListKumpPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (KUMP PTJ)");
		        
				// Resize all columns to fit the content size
		        for(int i = 0; i < columnHeaderListKumpPtj.size(); i++) {   
		    		if(i==0)sheetKumpPtjList.setColumnWidth(i, 5*256);//Col : Bil.
		    		else if(i==1)sheetKumpPtjList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
		        	else if(i==2)sheetKumpPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
		        	else if(i==3)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet yang Diluluskan
					else if(i==4)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
					else if(i==5)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
					else if(i==6)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
					else if(i==7)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
					else if(i==8)sheetKumpPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
		        }
		
		        if(resultList.isEmpty())
					createNotFoundRecord(wb,sheetKumpPtjList,columnHeaderListKumpPtj);
				else{
					//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]plan,[8]vot,[9]bdgetAmt,[10]plnAmnt,[11]deduct,
					//[12]orgprofileid,[13]votType,[14]vottypehierarchy,[15]amount
					
					sortBySelectedObject(resultList,3);
					
					Integer count = 1;
					for(int m=0; m<resultList.size(); m++){
						Object[] row = resultList.get(m); //get whole object
						
						if(kumpPtjList.containsKey(Integer.parseInt((String)row[3]))){ //find key in map
							Object[] obj = kumpPtjList.get(Integer.parseInt((String)row[3]));
							//9-10
							BigDecimal oldBudgetValue = (BigDecimal)obj[3]; //old budget
							obj[3] = ((BigDecimal)row[9]).add(oldBudgetValue);
							
							BigDecimal oldPlanValue = (BigDecimal)obj[4]; //old plan
							obj[4] = ((BigDecimal)row[10]).add(oldPlanValue);

							obj[5] = ((BigDecimal)obj[3]).subtract((BigDecimal)obj[4]); //unused

							Double oldAmountValue = (Double)obj[6]; //old amount
							obj[6] = (Double)row[15] + oldAmountValue;
							
							//BigDecimal oldDeductValue = (BigDecimal)obj[7]; //old deduct
							//obj[7] = ((BigDecimal)row[11]!=null?(BigDecimal)row[11]:BigDecimal.ZERO).add(oldDeductValue);
							
							Double dVal = 0.00;
							if((BigDecimal)row[11]!=null){
								obj[7]="("+ nf.format((BigDecimal)row[11])+")"; //#27472 change format for deduction
								dVal = ((BigDecimal)row[11]).doubleValue();
							}else if(!obj[7].toString().equals("(0.00)"))
								dVal = Double.parseDouble(obj[7].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
							
							//percentage
							Double amntMinusDeduct = (Double)obj[6] - dVal;//((BigDecimal)row[11]!=null ? ((BigDecimal)row[11]).doubleValue():0.00);
							obj[8] = amntMinusDeduct / ((BigDecimal)obj[4]).doubleValue() * 100;

							kumpPtjList.put(Integer.parseInt((String)row[3]), obj);
						}else{
							BigDecimal unUsedAmnt = ((BigDecimal)row[9]).subtract((BigDecimal)row[10]);
							Double amntMinusDeduct = (Double)row[15] - ((BigDecimal)row[11]!=null?(BigDecimal)row[11]:BigDecimal.ZERO).doubleValue();
							Double percentageExpenditure = amntMinusDeduct/((BigDecimal)row[10]).doubleValue()*100;
							kumpPtjList.put(Integer.parseInt((String)row[3]), new Object[]{
								count,Integer.parseInt((String)row[3]),(String)row[4],(BigDecimal)row[9],(BigDecimal)row[10],unUsedAmnt,(Double)row[15],
								(BigDecimal)row[11]!=null?"("+((BigDecimal)row[11]).toString()+")":"(0.00)",percentageExpenditure
								});
							count++;
						}
					}

					insertInCell(wb,createHelper,kumpPtjList,sheetKumpPtjList);
				}
	        }	
	    	//end Kumpulan PTJ tab
	        
	        //start PTJ tab
			ArrayList<String> columnHeaderListPtj = new ArrayList<String>(Arrays.asList(
					"Bil.","Kumpulan PTJ","Kod PTJ","Nama PTJ","Jumlah Bajet yang Diluluskan (RM)","Jumlah Bajet untuk Kegunaan eP (RM)","Peruntukan Tidak Menggunakan eP (RM)",
	  				"Jumlah Amaun Perbelanjaan (RM)","Jumlah Potongan (RM)","Jumlah Peratusan Amaun Perbelanjaan(%)"
	  				));
			Sheet sheetPtjList = createSheetBase(wb, createHelper, "PTJ", titleReport, columnHeaderListPtj, 
					columnHeaderListPtj.size(),params.get("PMINIS")!=null?true:false,ministryName+" (PTJ)");
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListPtj.size(); i++) {   
	    		if(i==0)sheetPtjList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetPtjList.setColumnWidth(i, 45*256); //Col : Kumpulan PTJ
	        	else if(i==2)sheetPtjList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==3)sheetPtjList.setColumnWidth(i, 45*256); //Col : Name PTJ
	        	else if(i==4)sheetPtjList.setColumnWidth(i, 15*256); //Col : Jumlah Bajet yang Diluluskan
				else if(i==5)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Bajet untuk Kegunaan eP
				else if(i==6)sheetPtjList.setColumnWidth(i, 25*256); //Col : Peruntukan Tidak Menggunakan eP
				else if(i==7)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Amaun Perbelanjaan
				else if(i==8)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Potongan
				else if(i==9)sheetPtjList.setColumnWidth(i, 25*256); //Col : Jumlah Peratusan Amaun Perbelanjaan
	        }
	        
	        if(resultList.isEmpty())
	        	createNotFoundRecord(wb,sheetPtjList,columnHeaderListPtj);
			else{
				//[0]ministry,[1]pminis,[2]pegawai,[3]kumptjCode,[4]kumpptj,[5]ptjcode,[6]ptj,[7]plan,[8]vot,[9]bdgetAmt,[10]plnAmnt,[11]deduct,
				//[12]orgprofileid,[13]votType,[14]vottypehierarchy,[15]amount
				
				sortBySelectedObject(resultList,5);
				
				Integer count = 1;
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m); //get whole object
					
					if(ptjList.containsKey(Integer.parseInt((String)row[5]))){ //find key in map
						Object[] obj = ptjList.get(Integer.parseInt((String)row[5]));
						//9-10
						BigDecimal oldBudgetValue = (BigDecimal)obj[4]; //old budget
						obj[4] = ((BigDecimal)row[9]).add(oldBudgetValue);
						
						BigDecimal oldPlanValue = (BigDecimal)obj[5]; //old plan
						obj[5] = ((BigDecimal)row[10]).add(oldPlanValue);

						obj[6] = ((BigDecimal)obj[4]).subtract((BigDecimal)obj[5]); //unused

						Double oldAmountValue = (Double)obj[7]; //old amount
						obj[7] = (Double)row[15] + oldAmountValue;
						
						//BigDecimal oldDeductValue = (BigDecimal)obj[8]; //old deduct
						//obj[8] = ((BigDecimal)row[11]!=null?(BigDecimal)row[11]:BigDecimal.ZERO).add(oldDeductValue);
						
						Double dVal = 0.00;
						if((BigDecimal)row[11]!=null){
							obj[8]="("+ nf.format((BigDecimal)row[11])+")"; //#27472 change format for deduction
							dVal = ((BigDecimal)row[11]).doubleValue();
						}else if(!obj[8].toString().equals("(0.00)"))
							dVal = Double.parseDouble(obj[8].toString().replaceAll("[(,)]", "")); //#27472 change format for deduction
												
						//percentage
						Double amntMinusDeduct = (Double)obj[7] - dVal;//((BigDecimal)row[11]!=null ? ((BigDecimal)row[11]).doubleValue():0.00);
						obj[9] = amntMinusDeduct / ((BigDecimal)obj[5]).doubleValue() * 100;


						ptjList.put(Integer.parseInt((String)row[5]), obj);
					}else{
						BigDecimal unUsedAmnt = ((BigDecimal)row[9]).subtract((BigDecimal)row[10]);
						Double amntMinusDeduct = (Double)row[15] - ((BigDecimal)row[11]!=null?(BigDecimal)row[11]:BigDecimal.ZERO).doubleValue();
						Double percentageExpenditure = amntMinusDeduct/((BigDecimal)row[10]).doubleValue()*100;
						ptjList.put(Integer.parseInt((String)row[5]), new Object[]{
							count,(String)row[4],Integer.parseInt((String)row[5]),(String)row[6],(BigDecimal)row[9],(BigDecimal)row[10],unUsedAmnt,
							(Double)row[15],(BigDecimal)row[11]!=null?"("+((BigDecimal)row[11]).toString()+")":"(0.00)",percentageExpenditure
							});
						count++;
					}
				}

				insertInCell(wb,createHelper,ptjList,sheetPtjList);
			}
	        
	    	//end PTJ tab
	          
	        exportExcelReportPP(wb, reportName);
		}catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}
	}
	
	//#27472 Expenditure Performance Report
	public void generateExlEPR(String reportName, LinkedHashMap<String, String> params){
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		String titleReport = "LAPORAN PRESTASI PERBELANJAAN MENGGUNAKAN SISTEM EPEROLEHAN(eP) BAGI TAHUN "+ getManagedBean().getSelectedYear();
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		String today = formater.format(new Date());
		Map<Integer, Object[]> collectionList = new TreeMap<Integer, Object[]>();
		Double total2 = 0.0;
		Double total3 = 0.0;
		
		//start kementerian tab
		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
				"BIL.","KEMENTERIAN / JABATAN","ANGGARAN PERUNTUKAN MENGGUNAKAN eP (RM)",
				"PRESTASI PERBELANJAAN SEHINGGA "+ today+" (RM)","PERATUSAN PENGGUNAAN eP SEHINGGA "+ today+" (%)"
		));
		Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Kementerian", titleReport, 
				columnHeaderListMinistry, columnHeaderListMinistry.size(), false,null);
        
		// Resize all columns to fit the content size
        for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
    		if(i==0)sheetItemList.setColumnWidth(i, 10*256);//Col : Bil.
    		else if(i==1)sheetItemList.setColumnWidth(i, 65*256); //Kementerian
        	else if(i==2)sheetItemList.setColumnWidth(i, 30*256); //Col : Anggaran
        	else if(i==3)sheetItemList.setColumnWidth(i, 30*256); //Col : Prestasi Belanja
        	else if(i==4)sheetItemList.setColumnWidth(i, 30*256); //Col : Peratus Penggunaan
        }
        
        try{
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_EXPENDITURE_PERFORMANCE_RPT_VAL);
			if(resultList.isEmpty())
				createNotFoundRecord(wb,sheetItemList,columnHeaderListMinistry);
			else{
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					total2 += row[1] != null ? ((BigDecimal)row[1]).doubleValue() : 0.00;
					total3 += row[2] != null ? ((BigDecimal)row[2]).doubleValue() : 0.00;
					
					collectionList.put(m, new Object[]{
						Integer.valueOf(m+1),
						row[0] != null ? (String)row[0] : "",
						row[1] != null ? ((BigDecimal)row[1]).doubleValue() : 0.00,
						row[2] != null ? ((BigDecimal)row[2]).doubleValue() : 0.00,
						row[3] != null ? Double.parseDouble((String)row[3]) : 0.00
					});
				}
				
				insertInCell(wb,createHelper,collectionList,sheetItemList);
				
				//total footer
		        Row totalRecordRowItem = sheetItemList.createRow(sheetItemList.getLastRowNum()+1);
		        
		        for (int i = 0; i <= 1; ++i) {
		            Cell cell = totalRecordRowItem.createCell(i);
		            cell.setCellStyle(columnStyle(wb,createHelper,true,true,false,true,false,false,true,true));
		            if (i == 0) {
		                cell.setCellValue("Jumlah");
		            } 
		        }
				Cell cellTotalRecordItem2 = totalRecordRowItem.createCell(2);
				cellTotalRecordItem2.setCellValue(total2);
				cellTotalRecordItem2.setCellStyle(columnStyle(wb,createHelper,true,true,false,true,false,true,false,true));
				Cell cellTotalRecordItem3 = totalRecordRowItem.createCell(3);
				cellTotalRecordItem3.setCellValue(total3);
				cellTotalRecordItem3.setCellStyle(columnStyle(wb,createHelper,true,true,false,true,false,true,false,true));
				Cell cellTotalRecordItem4 = totalRecordRowItem.createCell(4);
				cellTotalRecordItem4.setCellValue(total2 == 0? 0.00 : total3/total2*100.00);
				cellTotalRecordItem4.setCellStyle(columnStyle(wb,createHelper,true,true,false,true,false,true,false,true));
				
				sheetItemList.addMergedRegion(new CellRangeAddress(sheetItemList.getLastRowNum(),sheetItemList.getLastRowNum(),0,1)); 

			}
        
        }catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}

		//end kementerian tab
        	          
        exportExcelReportPP(wb, reportName);
	}
	
	//#27472 Budget Allocation Amendment
	public void generateExlBAAR(String reportName, LinkedHashMap<String, String> params){
		try{
			Workbook wb = new XSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			String titleReport = "LAPORAN PINDAAN ANGGARAN PERUNTUKAN MENGGUNAKAN SISTEM EPEROLEHAN (eP) BAGI TAHUN "+ getManagedBean().getSelectedYear();
			Map<Integer, Object[]> collectionList = new TreeMap<Integer, Object[]>();
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_BUDGET_ALLOCATION_AMENDMENT_RPT_VAL);
			
			//start senarai item tab
			ArrayList<String> columnHeaderListItem = new ArrayList<String>(Arrays.asList(
					"Bil.","Kementerian","Pegawai Pengawal","Kod Kump PTJ","Kumpulan PTJ","Kod PTJ","Nama PTJ","Nombor Pelan","Jenis VOT",
					"Kod Akaun","Jenis Perbelanjaan","Anggaran Peruntukan Menggunakan eP (Asal) (RM)","Anggaran Peruntukan Menggunakan eP (Dipinda) (RM)",
					"Justifikasi Pindaan","Tarikh Diluluskan","Status","Jumlah Kali Diluluskan"
			));
			Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Item", titleReport, 
					columnHeaderListItem, columnHeaderListItem.size(), false,null);
	        
			// Resize all columns to fit the content size
	        for(int i = 0; i < columnHeaderListItem.size(); i++) {   
	    		if(i==0)sheetItemList.setColumnWidth(i, 5*256);//Col : Bil.
	    		else if(i==1)sheetItemList.setColumnWidth(i, 50*256); //Col : Kementerian
	    		else if(i==2)sheetItemList.setColumnWidth(i, 50*256); //Col : Pegawai Pengawal
	        	else if(i==3)sheetItemList.setColumnWidth(i, 20*256); //Col : Kod Kump PTJ
	        	else if(i==4)sheetItemList.setColumnWidth(i, 50*256); //Col : Kumpulan PTJ
	        	else if(i==5)sheetItemList.setColumnWidth(i, 15*256); //Col : Kod PTJ
	        	else if(i==6)sheetItemList.setColumnWidth(i, 50*256); //Col : Nama PTJ
	        	else if(i==7)sheetItemList.setColumnWidth(i, 25*256); //Col : Nombor Pelan
	        	else if(i==8)sheetItemList.setColumnWidth(i, 25*256); //Col : Jenis VOT
	        	else if(i==9)sheetItemList.setColumnWidth(i, 25*256); //Col : Kod Akaun
	        	else if(i==10)sheetItemList.setColumnWidth(i, 35*256); //Col : Jenis Perbelanjaan
	        	else if(i==11)sheetItemList.setColumnWidth(i, 25*256); //Col : Anggaran Peruntukan Menggunakan eP (Asal)
	        	else if(i==12)sheetItemList.setColumnWidth(i, 25*256); //Col : Anggaran Peruntukan Menggunakan eP (Dipinda)
	        	else if(i==13)sheetItemList.setColumnWidth(i, 25*256); //Col : Justifikasi Pindaan
	        	else if(i==14)sheetItemList.setColumnWidth(i, 25*256); //Col : Tarikh Diluluskan
	        	else if(i==15)sheetItemList.setColumnWidth(i, 25*256); //Col : Status
	        	else if(i==16)sheetItemList.setColumnWidth(i, 25*256); //Col : Jumlah Kali Diluluskan
	        }
	        
	        if(resultList.isEmpty())
	        	createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			else{
				//[0]MINISTRY_NAME,[1]PEGAWAI,[2]KMP_CODE,[3]KUMP_PTJ,[4]PTJCODE,[5]PTJ,[6]PLAN_NO,[7]CODE_NAME,[8]GL_ACC_CODE,[9]DESCRIPTION,
				//[10]ASAL,[11]DIPINDA,[12]JUSTIFIKASI,[13]APPROVE_DATE,[14]ITEM_STATUS,[15]EXPENDITURE_ITEM_ID,[16]FROM_PPITEM,[17]TYPESORT
				
				Integer count = 2;
				Integer bil = 1;
				BigDecimal oldVal = new BigDecimal(0);
				BigDecimal origin = new BigDecimal(0);
				
				Boolean proceed=true;
				String newRemark="";
				String oldRemark="";
				String newDateStr="";
				String oldDateStr="";
				BigDecimal oldPinda = new BigDecimal(0);
				BigDecimal pinda = new BigDecimal(0);
				
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					
					if(m>0){
						BigDecimal fromVal = (BigDecimal)row[16];
						if(oldVal.equals(fromVal)){
							oldVal = (BigDecimal)row[15];
							oldRemark = newRemark;
							newRemark = row[12]!=null? (String)row[12]:"";
							oldDateStr = newDateStr;
							newDateStr = row[13]!=null? (String)row[13]:"";
							oldPinda = pinda;
							pinda = (BigDecimal)row[11];
							
							if(oldPinda.equals(pinda) && ((String)row[14]).equals("TIDAK AKTIF")){
								proceed = false; //skip
							}else if(oldPinda.equals((BigDecimal)row[11]) && ((String)row[14]).equals("AKTIF")){
								newRemark = oldRemark;
								newDateStr = oldDateStr;
								count++;
							}else
								count++;
							
						}else{
							
							if(((BigDecimal)row[10]).compareTo((BigDecimal)row[11]) == 0 && ((String)row[14]).equals("AKTIF")){
								proceed=false; //skip
							}else{
								oldVal = (BigDecimal)row[15];
								origin = (BigDecimal)row[10];
								newRemark = row[12]!=null? (String)row[12]:"";
								newDateStr = row[13]!=null? (String)row[13]:"";
								pinda = (BigDecimal)row[11];
								count=2;
							}
						}
					}else{
						if(((BigDecimal)row[10]).compareTo((BigDecimal)row[11]) == 0 && ((String)row[14]).equals("AKTIF")){
							proceed=false; //skip
						}else{
							oldVal = (BigDecimal)row[15];
							origin = (BigDecimal)row[10];
							newRemark = row[12]!=null? (String)row[12]:"";
							newDateStr = row[13]!=null? (String)row[13]:"";
							pinda = (BigDecimal)row[11];
						}
					}
					
					if(proceed){
						collectionList.put(m, new Object[]{
							bil,(String)row[0],(String)row[1],Integer.parseInt((String)row[2]),(String)row[3],
							Integer.parseInt((String)row[4]),(String)row[5],row[6] != null ? (String)row[6] : "",
							row[7] != null ? (String)row[7] : "",row[8] != null ? (String)row[8] : "",row[9] != null ? (String)row[9] : "",
							origin,pinda,newRemark,newDateStr,row[14] != null ? (String)row[14] : "",Integer.valueOf(count)
						});
						bil++;
					}
					
					proceed = true;//reset
					
				}
				
				//need checking again because have skip mehtod
				if(!collectionList.isEmpty()){
					insertInCell(wb,createHelper,collectionList,sheetItemList);
				}else
					createNotFoundRecord(wb,sheetItemList,columnHeaderListItem);
			}
	
			//end senarai item tab
	        	          
	        exportExcelReportPP(wb, reportName);
		}catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}
	}

	public Sheet createSheetBase(Workbook wb, CreationHelper createHelper,String tabName, String titleReport, 
			ArrayList<String> columnHeaderList, int firstTitle, boolean displayMinistry, String ministryName){
		Sheet sheet = wb.createSheet(tabName);
		
		//Setting first title
		Row titleRowList= sheet.createRow(0);
		Cell cellTitleRow = titleRowList.createCell(0);
	    cellTitleRow.setCellValue(titleReport);
	    cellTitleRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false,false));
	    sheet.addMergedRegion(new CellRangeAddress(0,0,0,firstTitle-1)); 
	    
	    //Setting ministry name under title
	    if(displayMinistry){
		    Row ministryList= sheet.createRow(1);
			Cell cellministryRow = ministryList.createCell(0);
			cellministryRow.setCellValue(ministryName);
			cellministryRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false,false));
			sheet.addMergedRegion(new CellRangeAddress(1,1,0,firstTitle-1));
	    }
		
		//Setting ministry name under title
	    int rowNumber = !displayMinistry? 1 : 2;
	    Row blankList= sheet.createRow(rowNumber);
		Cell cellBlankRow = blankList.createCell(0);
		cellBlankRow.setCellValue("");
		cellBlankRow.setCellStyle(titleHeaderStyle(wb,true,false,false,false,false));
		sheet.addMergedRegion(new CellRangeAddress(rowNumber,rowNumber,0,firstTitle-1));
		
		//Setting Column header list
	    Row headerRowList = sheet.createRow(sheet.getLastRowNum()+1);
        for(int i = 0; i < columnHeaderList.size(); i++) {
            Cell cell = headerRowList.createCell(i);
            cell.setCellValue(columnHeaderList.get(i).toString());
            cell.setCellStyle(columnStyle(wb,createHelper,true,true,true,false,false,false,true,true));
        }
		
		return sheet;
	}
	
	public void createNotFoundRecord(Workbook wb, Sheet sheet, ArrayList<String> columnHeaderList){
        Row noRecordRow = sheet.createRow(sheet.getLastRowNum()+1);
  		Cell cellNoRecord = noRecordRow.createCell(0);
  		cellNoRecord.setCellValue("Rekod tidak dijumpai");
  		cellNoRecord.setCellStyle(titleHeaderStyle(wb,false,false,false,false,false));
  		sheet.addMergedRegion(new CellRangeAddress(sheet.getLastRowNum(),sheet.getLastRowNum(),0,columnHeaderList.size()-1));
	}
	
	public void exportExcelReportPP(Workbook workbook, String reportName){
		try{
	    	String path = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.DOCUMENT_REPOSITORY_SHARED_REPORT_DIR);
	    	String fullPath = path+"/"+reportName+".xlsx";
	    	log.info("FULL PATH (CREATE FILE) == "+fullPath);
	    	FileOutputStream out = new FileOutputStream(new File(fullPath));
	    	workbook.write(out);
	        out.close();
	        workbook.close();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    log.info("DONE GENERATE " + reportName);
	}
	
	public IRPTRequestSO getRptRequestSO(){
		IRPTRequestSO rptRequestSO = EpEjbBusinessDelegate.getInstance()
				.getServiceObject(ReportConst.RPT_MODULE_SER_OBJ,
						IRPTRequestSO.class);
		
		return rptRequestSO;
	}
	
	public CellStyle titleHeaderStyle(Workbook wb, Boolean isBold, Boolean isAlignCenter, Boolean isWrapTxt, Boolean isVAlignTop, Boolean isAlignRight){
		Font titleReportFont = wb.createFont();
		if(isBold)titleReportFont.setBold(true);
		else titleReportFont.setBold(false);
		titleReportFont.setFontHeightInPoints((short)10);  
		titleReportFont.setFontName("Arial"); 
		
		CellStyle titleReportCellStyle = wb.createCellStyle();
		titleReportCellStyle.setFont(titleReportFont);
		if(isAlignCenter && !isAlignRight)titleReportCellStyle.setAlignment(HorizontalAlignment.CENTER);
		else if(isAlignRight && !isAlignCenter)titleReportCellStyle.setAlignment(HorizontalAlignment.RIGHT);
		else if(!isAlignRight && !isAlignCenter) titleReportCellStyle.setAlignment(HorizontalAlignment.LEFT);
		else titleReportCellStyle.setAlignment(HorizontalAlignment.CENTER);
		if(isWrapTxt)titleReportCellStyle.setWrapText(true);
		if(isVAlignTop)titleReportCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
		else titleReportCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		return titleReportCellStyle;
	}

	private int findHigherRole(){
		
		if(getManagedBean().isDisabledMinistryLov() && getManagedBean().isDisabledPegawaiLov()
				&& getManagedBean().isDisabledKumpulanPTJLov() && getManagedBean().isDisabledPTJLov())
			return 3;
		if(getManagedBean().isDisabledMinistryLov() && getManagedBean().isDisabledPegawaiLov()
				&& getManagedBean().isDisabledKumpulanPTJLov())
			return 2;
		else
			return 1;
	}
	
	public void insertInCell(Workbook wb, CreationHelper createHelper, Map<Integer, Object[]> itemList,Sheet sheetItemList){
		Set<Integer> keyIdSummary = itemList.keySet();
		int rowid = sheetItemList.getLastRowNum()+1;
		for (Integer key : keyIdSummary) {
			Row row = sheetItemList.createRow(rowid++);
			Object[] objectArr = itemList.get(key);
			int cellid = 0;
			for (int x=0; x<objectArr.length; x++) {
				Cell cell = row.createCell(cellid);
				
				if(objectArr[x] instanceof Double){
					cell.setCellValue(((Double) objectArr[x]).doubleValue());
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,true,false,true,false,true));
				}else if(objectArr[x] instanceof BigDecimal){
					cell.setCellValue(((BigDecimal) objectArr[x]).doubleValue());
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,true,false,true,false,true));
				}else if(objectArr[x] instanceof Integer){
					cell.setCellValue(Integer.class.cast(objectArr[x]));
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,true,true));
				}else if(objectArr[x] instanceof Date){
					cell.setCellValue((Date)objectArr[x]);
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,true,false,true,true));
				}else if(objectArr[x]!=null && StringUtils.isNotBlank(objectArr[x].toString()) &&
						objectArr[x].toString().charAt(0) == '(' && objectArr[x].toString().charAt(objectArr[x].toString().length()-1) == ')'){
					cell.setCellValue((String) objectArr[x]);
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,true,false,false,true,true));
				}else{
					cell.setCellValue((String) objectArr[x]);
					cell.setCellStyle(columnStyle(wb,createHelper,false,false,false,false,false,false,true,true));
				}
				cellid++;
			}
		}
	}
	
	private Object[] appendValue(Object[] obj, Object newObj) {
		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
		temp.add(newObj);
		return temp.toArray();
	}
	
	public static SelectItem[] getDocumentTypeOptions() {
		return PmCommonUtil.getDocumentTypeOption();
	}
	
	public static void sortBySelectedObject(List<Object[]> list, final Integer ind) {
		Collections.sort(list, new Comparator<Object[]>() {
			public int compare(Object[] s1, Object[] s2)
		    {
		        return AlphanumericSortUtil.processString((s1[ind]).toString(),(s2[ind]).toString());
		    }});
	}
	
	//end #27472
	
	//start [#29484] Total Approved Procurement and Expenditure Plan by Ministry Report
	private void generateExlTAM(String reportName, LinkedHashMap<String, String> params){
		Workbook wb = new XSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		String titleReport = "LAPORAN JUMLAH PERANCANGAN PEROLEHAN DAN PERBELANJAAN YANG DILULUSKAN MENGIKUT KEMENTERIAN.";
		String tahun = "TAHUN "+ getManagedBean().getSelectedYear(); //second row
		Map<Integer, Object[]> collectionList = new TreeMap<Integer, Object[]>();
		Integer total1 = 0;
		Integer total2 = 0;
		Integer total3 = 0;
		
		//start kementerian tab
		ArrayList<String> columnHeaderListMinistry = new ArrayList<String>(Arrays.asList(
				"BIL.","KEMENTERIAN","BILANGAN PTJ eP ENABLE",
				"BILANGAN PERANCANGAN PEROLEHAN DILULUSKAN","BILANGAN PERANCANGAN PERBELANJAAN DILULUSKAN"
		));
		Sheet sheetItemList = createSheetBase(wb, createHelper, "Senarai Kementerian", titleReport, 
				columnHeaderListMinistry, columnHeaderListMinistry.size(),true,tahun);
        
		// Resize all columns to fit the content size
        for(int i = 0; i < columnHeaderListMinistry.size(); i++) {   
    		if(i==0)sheetItemList.setColumnWidth(i, 10*256);//Col : Bil.
    		else if(i==1)sheetItemList.setColumnWidth(i, 65*256); //Kementerian
        	else if(i==2)sheetItemList.setColumnWidth(i, 30*256); //Col : Bil Ptj
        	else if(i==3)sheetItemList.setColumnWidth(i, 30*256); //Col : Bil PP
        	else if(i==4)sheetItemList.setColumnWidth(i, 30*256); //Col : Bil PE
        }
        
        try{
			List<Object[]> resultList = getRptRequestSO().generatePPData(params,PpReportConst.PP_TOTAL_APPROVED_PP_PE_MINISTRY_RPT_VAL);
			//[0]MINISTRY_NAME,[1]TOT_ENBL_ACTIVE_PTJ,[2]APPROVE_PP_PTJ,[3]APPROV_PE_PTJ
			if(resultList.isEmpty())
				createNotFoundRecord(wb,sheetItemList,columnHeaderListMinistry);
			else{
				for(int m=0; m<resultList.size(); m++){
					Object[] row = resultList.get(m);
					total1 += row[1] != null ? ((BigDecimal)row[1]).intValueExact() : 0;
					total2 += row[2] != null ? ((BigDecimal)row[2]).intValueExact() : 0;
					total3 += row[3] != null ? ((BigDecimal)row[3]).intValueExact() : 0;
					
					collectionList.put(m, new Object[]{
						Integer.valueOf(m+1),
						row[0] != null ? (String)row[0] : "",
						row[1] != null ? ((BigDecimal)row[1]).intValueExact() : 0,
						row[2] != null ? ((BigDecimal)row[2]).intValueExact() : 0,
						row[3] != null ? ((BigDecimal)row[3]).intValueExact() : 0
					});
				}
				
				insertInCell(wb,createHelper,collectionList,sheetItemList);
				
				//total footer
		        Row totalRecordRowItem = sheetItemList.createRow(sheetItemList.getLastRowNum()+1);
		        
		        for (int i = 0; i <= 1; ++i) {
		            Cell cell = totalRecordRowItem.createCell(i);
		            cell.setCellStyle(columnStyle(wb,createHelper,true,true,false,true,false,false,true,true));
		            if (i == 0) {
		                cell.setCellValue("Jumlah Keseluruhan");
		            } 
		        }
				Cell cellTotalRecordItem2 = totalRecordRowItem.createCell(2);
				cellTotalRecordItem2.setCellValue(total1);
				cellTotalRecordItem2.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,true,true));
				Cell cellTotalRecordItem3 = totalRecordRowItem.createCell(3);
				cellTotalRecordItem3.setCellValue(total2);
				cellTotalRecordItem3.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,true,true));
				Cell cellTotalRecordItem4 = totalRecordRowItem.createCell(4);
				cellTotalRecordItem4.setCellValue(total3);
				cellTotalRecordItem4.setCellStyle(columnStyle(wb,createHelper,false,false,true,false,false,false,true,true));
				
				sheetItemList.addMergedRegion(new CellRangeAddress(sheetItemList.getLastRowNum(),sheetItemList.getLastRowNum(),0,1)); 

			}
        
        }catch(EpException e){
        	getRptRequestSO().updateFailedExcelReport(new BigDecimal(reportName));
			throw new EpException(e.getMessage());
		}

		//end kementerian tab
        	          
        exportExcelReportPP(wb, reportName);
	}
	//end [#29484]
	
	public static SelectItem[] getActiveAndInactiveStatusOptions() {
		return PpCommonUtil.getStatusOptionsDefault();
	}
	
	public void onChangeProcurementMethod(){
		getManagedBean().setProcurementTypeId(null);
	}
	
	/**
	 * Login user has approver role
	 * @return boolean
	 */
	public boolean isRoleApprover(){
		return getFacesScopes().hasRoleUser(RoleCode.PLAN_APPROVER.value());
	}
	
	/**
	 * Login user has verifier role
	 * @return boolean
	 */
	public boolean isRoleVerifier(){
		return getFacesScopes().hasRoleUser(RoleCode.PLAN_VERFICATION_OFFICER.value());
	}
	
	/**
	 * Login user has planner role
	 * @return boolean
	 */
	public boolean isRolePlanner(){
		return getFacesScopes().hasRoleUser(RoleCode.PLAN_OFFICER.value());
	}
	
	private boolean isInternalAuditor(){
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value());
	}
	
	//[#27197] - start
	private boolean isBPKOfficer(){
		return getFacesScopes().hasRoleUser(RoleCode.BPK_OFFICER.value());
	}
	
	private boolean isBPKAuditor(){
		return getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value());
	}
	
	private boolean isGeneralAuditor(){
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}
	//[#27197] - end

}
